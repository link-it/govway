/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */



package org.openspcoop2.protocol.basic.tracciamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.basic.BasicConnectionResult;
import org.openspcoop2.protocol.basic.BasicProducer;
import org.openspcoop2.protocol.basic.BasicProducerType;
import org.openspcoop2.protocol.sdk.Allegato;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKey;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyJDBCType;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyObject;
import org.openspcoop2.utils.jdbc.JDBCUtilities;

/**
 * Contiene l'implementazione di un appender personalizzato,
 * per la registrazione dei tracciamenti su database.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TracciaProducer extends BasicProducer implements ITracciaProducer{

	public TracciaProducer(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory, BasicProducerType.TRACCE);
	}

	/**
	 * Inizializza l'engine di un appender per la registrazione
	 * di un tracciamento emesso da una porta di dominio.
	 * 
	 * @param appenderProperties Proprieta' dell'appender
	 * @throws TracciamentoException
	 */
	@Override
	public void initializeAppender(OpenspcoopAppender appenderProperties) throws TracciamentoException{
		try{
			this.initializeAppender(appenderProperties, true);
		}catch(Exception e){
			throw new TracciamentoException("Errore durante l'inizializzazione dell'appender: "+e.getMessage(),e);
		}
	}



	/**
	 * Registra una traccia prodotta da una porta di dominio, utilizzando le informazioni definite dalla specifica SPC.
	 * 
	 * @param conOpenSPCoopPdD Connessione verso il database
	 * @param traccia Traccia
	 * @throws TracciamentoException
	 */
	@Override
	public void log(Connection conOpenSPCoopPdD, Traccia traccia) throws TracciamentoException{

		if(traccia==null)
			throw new TracciamentoException("Errore durante il tracciamento: traccia is null");

		Busta busta = traccia.getBusta();
		if(busta==null)
			throw new TracciamentoException("Errore durante il tracciamento di un busta: busta is null");

		Date gdo = traccia.getGdo();
		IDSoggetto idSoggetto = traccia.getIdSoggetto();
		String tipoMessaggio = traccia.getTipoMessaggio().getTipo();
		String location = traccia.getLocation();
		String idCorrelazioneApplicativa = traccia.getCorrelazioneApplicativa();
		String idCorrelazioneApplicativaRisposta = traccia.getCorrelazioneApplicativaRisposta();
		
		if(this.debug){
			this.log.debug("@@ log["+busta.getID()+"] ....");
		}
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		BasicConnectionResult cr = null;
		try{
			//	Connessione al DB
			cr = this.getConnection(conOpenSPCoopPdD,"traccia.log");
			con = cr.getConnection();

			if(this.debug){
				this.log.debug("@@ log["+busta.getID()+"] (getConnection finished) ....");
			}
			
			//Inserimento della traccia nel DB
			if(!TipiDatabase.isAMember(this.tipoDatabase)){
				throw new TracciamentoException("Tipo database ["+this.tipoDatabase+"] non supportato");
			}
			TipiDatabase tipo = TipiDatabase.toEnumConstant(this.tipoDatabase);
			// ** Preparazione parametri
			java.sql.Timestamp gdoT = new java.sql.Timestamp(gdo.getTime());
			java.sql.Timestamp oraRegistrazioneT = null;
			java.sql.Timestamp scadenzaT = null;
			if(busta.getOraRegistrazione()!=null)
				oraRegistrazioneT = new java.sql.Timestamp(busta.getOraRegistrazione().getTime());
			if (busta.getScadenza() != null)
				scadenzaT =  new java.sql.Timestamp(busta.getScadenza().getTime());
			int confermaRicezione = -1;
			if(busta.isConfermaRicezione())
				confermaRicezione = 1;
			else
				confermaRicezione = 0 ;
			
			String headerProtocollo = null;
			if(traccia.getBustaAsRawContent()!=null){
				try{
					headerProtocollo = traccia.getBustaAsRawContent().toString(TipoSerializzazione.DEFAULT);
				}catch(Exception e){
					throw new Exception("Serializzazione RawContent non riuscita: "+e.getMessage(),e);
				}
			}
			else if(traccia.getBustaAsString()!=null){
				headerProtocollo = traccia.getBustaAsString();
			}else if(traccia.getBustaAsByteArray()!=null){
				headerProtocollo = new String(traccia.getBustaAsByteArray());
			}else{
				// ProtocolFactoryManager non utilizzabile in questo package
				/*
				headerProtocollo = 
						ProtocolFactoryManager.getInstance().
							getProtocolFactoryByName(traccia.getProtocollo()).
								createBustaBuilder().toString(busta, org.openspcoop2.protocol.sdk.constants.TipoTraccia.RICHIESTA.equals(traccia.getTipoMessaggio()));
								*/
			}
			
			if(this.debug){
				this.log.debug("@@ log["+busta.getID()+"] (prima inserimento traccia) ....");
			}
			List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_GDO, gdoT , InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_GDO_INT, gdoT.getTime(), InsertAndGeneratedKeyJDBCType.LONG) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_PDD_CODICE, idSoggetto.getCodicePorta(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_PDD_TIPO_SOGGETTO, idSoggetto.getTipo(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_PDD_NOME_SOGGETTO, idSoggetto.getNome(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_PDD_RUOLO, traccia.getTipoPdD().getTipo(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_TIPO_MESSAGGIO, tipoMessaggio, InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_ESITO_ELABORAZIONE, traccia.getEsitoElaborazioneMessaggioTracciato().getEsito().toString(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_DETTAGLIO_ESITO_ELABORAZIONE, traccia.getEsitoElaborazioneMessaggioTracciato().getDettaglio(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_MITTENTE_IDPORTA, busta.getIdentificativoPortaMittente(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_MITTENTE_NOME, busta.getMittente(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_MITTENTE_TIPO, busta.getTipoMittente(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_MITTENTE_INDIRIZZO, busta.getIndirizzoMittente(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_DESTINATARIO_IDPORTA, busta.getIdentificativoPortaDestinatario(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_DESTINATARIO_NOME, busta.getDestinatario(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_DESTINATARIO_TIPO, busta.getTipoDestinatario(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_DESTINATARIO_INDIRIZZO, busta.getIndirizzoDestinatario(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_PROFILO_COLLABORAZIONE, busta.getProfiloDiCollaborazioneValue(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_PROFILO_COLLABORAZIONE_SDK_CONSTANT, busta.getProfiloDiCollaborazione() == null ? null : busta.getProfiloDiCollaborazione().getEngineValue(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_SERVIZIO_CORRELATO_NOME, busta.getServizioCorrelato(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_SERVIZIO_CORRELATO_TIPO, busta.getTipoServizioCorrelato(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_COLLABORAZIONE, busta.getCollaborazione(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_SERVIZIO_VERSIONE, busta.getVersioneServizio(), InsertAndGeneratedKeyJDBCType.INT) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_SERVIZIO_NOME, busta.getServizio(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_SERVIZIO_TIPO, busta.getTipoServizio(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_AZIONE, busta.getAzione(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO, busta.getID(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE,oraRegistrazioneT,InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO, busta.getTipoOraRegistrazioneValue(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT,  busta.getTipoOraRegistrazione() == null ? null : busta.getTipoOraRegistrazione().getEngineValue(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_RIFERIMENTO_MESSAGGIO, busta.getRiferimentoMessaggio(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_SCADENZA,scadenzaT,InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_INOLTRO, busta.getInoltroValue(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_INOLTRO_SDK_CONSTANT, busta.getInoltro() == null ? null : busta.getInoltro().getEngineValue(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_CONFERMA_RICEZIONE, confermaRicezione, InsertAndGeneratedKeyJDBCType.INT) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_SEQUENZA, busta.getSequenza(), InsertAndGeneratedKeyJDBCType.LONG) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_LOCATION, location, InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_CORRELAZIONE_APPLICATIVA_RICHIESTA, idCorrelazioneApplicativa, InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_CORRELAZIONE_APPLICATIVA_RISPOSTA, idCorrelazioneApplicativaRisposta, InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_SA_FRUITORE, busta.getServizioApplicativoFruitore(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_SA_EROGATORE, busta.getServizioApplicativoErogatore(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_PROTOCOLLO, traccia.getProtocollo(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_DIGEST, busta.getDigest(), InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_SOAP, headerProtocollo, InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRACCE_COLUMN_ID_TRANSAZIONE, traccia.getIdTransazione(), InsertAndGeneratedKeyJDBCType.STRING) );
			
			
			// ** Insert and return generated key
			long idtraccia = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, tipo, 
					new CustomKeyGeneratorObject(CostantiDB.TRACCE, CostantiDB.TRACCE_COLUMN_ID, CostantiDB.TRACCE_SEQUENCE, CostantiDB.TRACCE_TABLE_FOR_ID),
					listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
			if(idtraccia<=0){
				throw new Exception("ID autoincrementale non ottenuto");
			}
			traccia.setId(idtraccia);

			if(this.debug){
				this.log.debug("@@ log["+busta.getID()+"] (traccia inserita) ....");
			}
			
			
			
			String sqlString = null;
			
			if(this.debug){
				this.log.debug("@@ log["+busta.getID()+"] (inserimento "+busta.sizeListaRiscontri()+" riscontri) ....");
			}
			for (int i = 0; i < busta.sizeListaRiscontri(); i++) {
				Riscontro riscontro = busta.getRiscontro(i);

				//Inserimento nel DB
				sqlString = "INSERT INTO "+CostantiDB.TRACCE_RISCONTRI+" ("+
						CostantiDB.TRACCE_RISCONTRI_COLUMN_ID_TRACCIA+", "+
						CostantiDB.TRACCE_RISCONTRI_COLUMN_ID_RISCONTRO+", "+
						CostantiDB.TRACCE_RISCONTRI_COLUMN_RICEVUTA+", "+
						CostantiDB.TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE+", "+
						CostantiDB.TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE_TIPO+", "+
						CostantiDB.TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT+", "+
						CostantiDB.TRACCE_RISCONTRI_COLUMN_GDO+
						") VALUES (?, ?, ?, ?, ?, ?, ?)";
				stmt = con.prepareStatement(sqlString);
				int index = 1;
				stmt.setLong(index++, idtraccia);
				JDBCUtilities.setSQLStringValue(stmt,index++, riscontro.getID());
				JDBCUtilities.setSQLStringValue(stmt,index++, riscontro.getRicevuta());
				if(riscontro.getOraRegistrazione()!=null)
					stmt.setTimestamp(index++, new java.sql.Timestamp(riscontro.getOraRegistrazione().getTime()));
				else
					stmt.setTimestamp(index++, null);
				JDBCUtilities.setSQLStringValue(stmt,index++, riscontro.getTipoOraRegistrazioneValue(this.protocolFactory));
				JDBCUtilities.setSQLStringValue(stmt,index++, riscontro.getTipoOraRegistrazione().getEngineValue());
				stmt.setTimestamp(index++, gdoT);
				stmt.executeUpdate();
				stmt.close();
			}
			if(this.debug){
				this.log.debug("@@ log["+busta.getID()+"] (inserimento "+busta.sizeListaRiscontri()+" riscontri effettuato) ....");
			}
			
			if(this.debug){
				this.log.debug("@@ log["+busta.getID()+"] (inserimento "+busta.sizeListaTrasmissioni()+" trasmissioni) ....");
			}
			for (int i = 0; i < busta.sizeListaTrasmissioni(); i++) {
				Trasmissione trasmissione = busta.getTrasmissione(i);

				//Inserimento nel DB
				sqlString = "INSERT INTO "+CostantiDB.TRACCE_TRASMISSIONI+" ("+
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ID_TRACCIA+", "+
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE+", "+
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_TIPO+", "+
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_INDIRIZZO+", "+
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_IDPORTA+", "+
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE+", "+
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_TIPO+", "+
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_INDIRIZZO+", "+
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_IDPORTA+", "+
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE+", "+
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE_TIPO+", "+
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT+", "+
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_GDO+
						") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				stmt = con.prepareStatement(sqlString);
				int index = 1;
				stmt.setLong(index++, idtraccia);
				JDBCUtilities.setSQLStringValue(stmt,index++, trasmissione.getOrigine());
				JDBCUtilities.setSQLStringValue(stmt,index++, trasmissione.getTipoOrigine());
				JDBCUtilities.setSQLStringValue(stmt,index++, trasmissione.getIndirizzoOrigine());
				JDBCUtilities.setSQLStringValue(stmt,index++, trasmissione.getIdentificativoPortaOrigine());
				JDBCUtilities.setSQLStringValue(stmt,index++, trasmissione.getDestinazione());
				JDBCUtilities.setSQLStringValue(stmt,index++, trasmissione.getTipoDestinazione());
				JDBCUtilities.setSQLStringValue(stmt,index++, trasmissione.getIndirizzoDestinazione());
				JDBCUtilities.setSQLStringValue(stmt,index++, trasmissione.getIdentificativoPortaDestinazione());
				if(trasmissione.getOraRegistrazione()!=null)
					stmt.setTimestamp(index++, new java.sql.Timestamp(trasmissione.getOraRegistrazione().getTime()));
				else
					stmt.setTimestamp(index++, null);
				JDBCUtilities.setSQLStringValue(stmt,index++, trasmissione.getTempoValue(this.protocolFactory));
				JDBCUtilities.setSQLStringValue(stmt,index++, trasmissione.getTempo().getEngineValue());
				stmt.setTimestamp(index++, gdoT);
				stmt.executeUpdate();
				stmt.close();
			}
			if(this.debug){
				this.log.debug("@@ log["+busta.getID()+"] (inserimento "+busta.sizeListaTrasmissioni()+" trasmissioni effettuato) ....");
			}
			
			if(this.debug){
				this.log.debug("@@ log["+busta.getID()+"] (inserimento "+busta.sizeListaEccezioni()+" eccezioni) ....");
			}
			for (int i = 0; i < busta.sizeListaEccezioni(); i++) {
				Eccezione eccezione = busta.getEccezione(i);

				//Inserimento nel DB
				String subCodiceMeta = "";
				String subCodiceMetaInsert = "";
				if(eccezione.getSubCodiceEccezione()!=null && eccezione.getSubCodiceEccezione().getSubCodice()!=null){
					subCodiceMeta = CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE_SUBCOD_SDK_CONSTANT+", ";
					subCodiceMetaInsert = "?,";
				}
				sqlString = "INSERT INTO "+CostantiDB.TRACCE_ECCEZIONI+" ("+
						CostantiDB.TRACCE_ECCEZIONI_COLUMN_ID_TRACCIA+", "+
						CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA+", "+
						CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA_SDK_CONSTANT+", "+
						CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE+", "+
						CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE_SDK_CONSTANT+", "+
						subCodiceMeta+
						CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+", "+
						CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA_SDK_CONSTANT+", "+
						CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+", "+
						CostantiDB.TRACCE_ECCEZIONI_COLUMN_GDO+
						") VALUES (?, ?, ?, ?, ?, "+subCodiceMetaInsert+" ?, ? , ? , ?)";
				stmt = con.prepareStatement(sqlString);
				int index = 1;
				stmt.setLong(index++, idtraccia);
				JDBCUtilities.setSQLStringValue(stmt,index++, eccezione.getContestoCodificaValue(this.protocolFactory));
				JDBCUtilities.setSQLStringValue(stmt,index++, eccezione.getContestoCodifica().getEngineValue());
				JDBCUtilities.setSQLStringValue(stmt,index++, eccezione.getCodiceEccezioneValue(this.protocolFactory));
				stmt.setInt(index++, eccezione.getCodiceEccezione().getCodice());
				if(eccezione.getSubCodiceEccezione()!=null && eccezione.getSubCodiceEccezione().getSubCodice()!=null)
					stmt.setInt(index++, eccezione.getSubCodiceEccezione().getSubCodice());
				JDBCUtilities.setSQLStringValue(stmt,index++, eccezione.getRilevanzaValue(this.protocolFactory));
				JDBCUtilities.setSQLStringValue(stmt,index++, eccezione.getRilevanza().getEngineValue());
				JDBCUtilities.setSQLStringValue(stmt,index++, eccezione.getDescrizione(this.protocolFactory));
				stmt.setTimestamp(index++, gdoT);
				stmt.executeUpdate();
				stmt.close();
			}
			if(this.debug){
				this.log.debug("@@ log["+busta.getID()+"] (inserimento "+busta.sizeListaEccezioni()+" eccezioni effettuato) ....");
			}
			
			if(this.debug){
				this.log.debug("@@ log["+busta.getID()+"] (inserimento "+traccia.sizeListaAllegati()+" allegati) ....");
			}
			for (int i = 0; i < traccia.sizeListaAllegati(); i++) {
				Allegato allegato = traccia.getAllegato(i);

				//Inserimento nel DB
				sqlString = "INSERT INTO "+CostantiDB.TRACCE_ALLEGATI+" ("+
						CostantiDB.TRACCE_ALLEGATI_COLUMN_ID_TRACCIA+", "+
						CostantiDB.TRACCE_ALLEGATI_COLUMN_CONTENT_ID+", "+
						CostantiDB.TRACCE_ALLEGATI_COLUMN_CONTENT_LOCATION+", "+
						CostantiDB.TRACCE_ALLEGATI_COLUMN_CONTENT_TYPE+", "+
						CostantiDB.TRACCE_ALLEGATI_COLUMN_DIGEST+", "+
						CostantiDB.TRACCE_ALLEGATI_COLUMN_GDO+
						") VALUES (?, ?, ?, ?, ?, ?)";
				stmt = con.prepareStatement(sqlString);
				int index = 1;
				stmt.setLong(index++, idtraccia);
				JDBCUtilities.setSQLStringValue(stmt,index++, allegato.getContentId());
				JDBCUtilities.setSQLStringValue(stmt,index++, allegato.getContentLocation());
				JDBCUtilities.setSQLStringValue(stmt,index++, allegato.getContentType());
				JDBCUtilities.setSQLStringValue(stmt,index++, allegato.getDigest());
				stmt.setTimestamp(index++, gdoT);
				stmt.executeUpdate();
				stmt.close();
			}
			if(this.debug){
				this.log.debug("@@ log["+busta.getID()+"] (inserimento "+traccia.sizeListaAllegati()+" allegati effettuato) ....");
			}
						
			String [] propertiesNames = busta.getPropertiesNames();
			if(propertiesNames!=null){
				if(this.debug){
					this.log.debug("@@ log["+busta.getID()+"] (inserimento "+propertiesNames.length+" ext-protocol-info) ....");
				}
				for (int i = 0; i < propertiesNames.length; i++) {
	
					int limit = 4000;
					
					String v = busta.getProperty(propertiesNames[i]);
					String columnExtV = "";
					String valueExtV = "";
					if(v.length()>limit) {
						columnExtV = ", "+CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_EXT_VALUE;
						valueExtV = ", ?";
					}
					
					//Inserimento nel DB
					sqlString = "INSERT INTO "+CostantiDB.TRACCE_EXT_INFO+" ("+
						CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_ID_TRACCIA+", "+
						CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_NAME+", "+
						CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_VALUE+", "+
						columnExtV+
						CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_GDO+
					") VALUES (?, ?, ?"+valueExtV+", ?)";
					stmt = con.prepareStatement(sqlString);
					int index = 1;
					stmt.setLong(index++, idtraccia);
					JDBCUtilities.setSQLStringValue(stmt,index++, propertiesNames[i]);
					if(v.length()>limit) {
						JDBCUtilities.setSQLStringValue(stmt,index++, v.substring(0, limit));
						JDBCUtilities.setSQLStringValue(stmt,index++, v);
					}
					else {
						JDBCUtilities.setSQLStringValue(stmt,index++, v);
					}
					stmt.setTimestamp(index++, gdoT);
					stmt.executeUpdate();
					stmt.close();
				
				}
				if(this.debug){
					this.log.debug("@@ log["+busta.getID()+"] (inserimento "+propertiesNames.length+" effettuato) ....");
				}
			}
			
			if(this.debug){
				this.log.debug("@@ log["+busta.getID()+"] completato");
			}

		}catch(Exception e){
			throw new TracciamentoException("Errore durante il tracciamento di una busta: "+e.getMessage()+"\nL'id del messaggio della busta era: "+busta.getID(),e);
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){}
			try{
				if(stmt!=null)
					stmt.close();
			}catch(Exception e){}
			try{
				this.releaseConnection(cr, "traccia.log");
			}catch(Exception e){}
		}
	}


}
