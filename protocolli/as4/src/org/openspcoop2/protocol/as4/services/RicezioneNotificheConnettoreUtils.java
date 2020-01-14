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

package org.openspcoop2.protocol.as4.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.TextMessage;
import javax.sql.DataSource;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.dao.ITransazioneService;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.DriverTracciamento;
import org.openspcoop2.pdd.logger.TracciamentoOpenSPCoopAppenderDB;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.tracciamento.EsitoElaborazioneMessaggioTracciato;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.threads.RunnableLogger;

/**
 * RicezioneBusteConnettoreUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneNotificheConnettoreUtils extends BaseConnettoreUtils {

	private AS4Properties properties;
	private DAOFactory daoFactory;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni;
	
	public RicezioneNotificheConnettoreUtils(RunnableLogger log, AS4Properties properties) throws Exception {
		super(log);
		this.properties = properties;
		
		this.daoFactory = DAOFactory.getInstance(log.getLog());
		DAOFactoryProperties daoFactoryProperties = DAOFactoryProperties.getInstance(log.getLog());
		this.daoFactoryServiceManagerPropertiesTransazioni = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
		boolean debug = true;
		this.daoFactoryServiceManagerPropertiesTransazioni.setShowSql(debug);	
		this.daoFactoryServiceManagerPropertiesTransazioni.setDatabaseType(DBTransazioniManager.getInstance().getTipoDatabase());
	}
	
	
	public void updateTraccia(TextMessage textMsg) throws Exception {
		
		String idNotifica = this.getPropertyJms(textMsg, AS4Costanti.JMS_NOTIFICA_MESSAGE_ID, true);
		String messageStatus = this.getPropertyJms(textMsg, AS4Costanti.JMS_NOTIFICA_NOTIFICATION_TYPE, true);
		
		String datasource = this.properties.getAckTraceDatasource();
		Properties datasourceJndiContext = this.properties.getAckTraceDatasource_jndiContext();
		String tipoDatabase = this.properties.getAckTraceTipoDatabase();
		if(tipoDatabase==null) {
			tipoDatabase = OpenSPCoop2Properties.getInstance().getDatabaseType();
		}
				
		GestoreJNDI gestoreJNDI = new GestoreJNDI(datasourceJndiContext);
		DataSource ds = (DataSource) gestoreJNDI.lookup(datasource);
		
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Long> ids = new ArrayList<>();
		try {
			connection = ds.getConnection();
			
			// Recupero idTraccia e gdo che contiene l'id notificato
			
			ISQLQueryObject sqlQueryObjectCore = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObjectCore.addFromTable(CostantiDB.TRACCE);
			sqlQueryObjectCore.addFromTable(CostantiDB.TRACCE_EXT_INFO);
			sqlQueryObjectCore.addSelectField(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_ID_TRACCIA);
			sqlQueryObjectCore.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ID+"="+CostantiDB.TRACCE_EXT_INFO+"."+CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_ID_TRACCIA);
			sqlQueryObjectCore.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_PDD_RUOLO+"=?");
			sqlQueryObjectCore.addWhereCondition(CostantiDB.TRACCE_EXT_INFO+"."+CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_NAME+"=?");
			sqlQueryObjectCore.addWhereCondition(CostantiDB.TRACCE_EXT_INFO+"."+CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_VALUE+"=?");
			sqlQueryObjectCore.setANDLogicOperator(true);
			String query = sqlQueryObjectCore.createSQLQuery();
			pstmt = connection.prepareStatement(query);
			int index = 1;
			pstmt.setString(index++, TipoPdD.DELEGATA.getTipo());
			pstmt.setString(index++, AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_ID);
			pstmt.setString(index++, idNotifica);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ids.add(rs.getLong(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_ID_TRACCIA));
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			if(ids.size()<=0) {
				this.log.error("Traccia con proprietà 'as4' con nome ["+AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_ID+"] e valore["+idNotifica+"] non trovata");
				return;
			}
			
			// Aggiorno la traccia di richiesta con lo stato notificato
			
			for (int i = 0; i < ids.size(); i++) { // dovrebbe essere solo una
				Long id = ids.get(i);
				
				sqlQueryObjectCore = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
				sqlQueryObjectCore.addUpdateTable(CostantiDB.TRACCE_EXT_INFO);
				sqlQueryObjectCore.addUpdateField(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_VALUE, "?");
				sqlQueryObjectCore.addWhereCondition(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_ID_TRACCIA+"=?");
				sqlQueryObjectCore.addWhereCondition(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_NAME+"=?");
				sqlQueryObjectCore.setANDLogicOperator(true);
				String update = sqlQueryObjectCore.createSQLUpdate();
				pstmt = connection.prepareStatement(update);
				pstmt.setString(1, messageStatus);
				pstmt.setLong(2, id);
				pstmt.setString(3, AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_SEND_STATUS);
				int n = pstmt.executeUpdate();
				if(n<=0) {
					this.log.error("Traccia con proprietà 'as4' con nome ["+AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_SEND_STATUS+"] e idtraccia["+id+"] non trovata");
				}
				else {
					this.log.debug("Traccia con proprietà 'as4' con nome ["+AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_SEND_STATUS+"] e idtraccia["+id+"] aggiornata allo stato ["+messageStatus+"]");
				}
				pstmt.close();
				pstmt = null;
				
			}
			
			// Se esiste una traccia della richiesta, creo una traccia della risposta se e' arrivato un riscontro
			
			if(ids.size()>0) {
				
				DomibusRicevuta ricevuta = readRicevuta(idNotifica, messageStatus);
				if(ricevuta!=null) {
					
					DriverTracciamento driverTracciamento = new DriverTracciamento(connection, tipoDatabase, this.log.getLog());
					
					
					for (int i = 0; i < ids.size(); i++) { // dovrebbe essere solo una
						Long id = ids.get(i);
					
						sqlQueryObjectCore = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
						sqlQueryObjectCore.addFromTable(CostantiDB.TRACCE);
						sqlQueryObjectCore.addSelectField(CostantiDB.TRACCE_COLUMN_ID_TRANSAZIONE);
						sqlQueryObjectCore.addWhereCondition(CostantiDB.TRACCE_COLUMN_ID+"=?");
						sqlQueryObjectCore.setANDLogicOperator(true);
						query = sqlQueryObjectCore.createSQLQuery();
						pstmt = connection.prepareStatement(query);
						pstmt.setLong(1, id);
						rs = pstmt.executeQuery();
						String idTransazione = null;
						if(rs.next()) {
							idTransazione = rs.getString(CostantiDB.TRACCE_COLUMN_ID_TRANSAZIONE);
						}
						else {
							this.log.error("Traccia con id["+id+"] non trovata ? (Id recuperato grazie alla notifica '"+idNotifica+"')");
						}
						rs.close();
						pstmt.close();
						rs = null;
						pstmt = null;
						
						if(idTransazione==null) {
							this.log.error("Traccia con id["+id+"] senza id di transazione ? (Id recuperato grazie alla notifica '"+idNotifica+"')");
						}
						else {
							
							Traccia traccia = driverTracciamento.getTraccia(idTransazione, RuoloMessaggio.RICHIESTA);
							
							TracciamentoOpenSPCoopAppenderDB appender = new TracciamentoOpenSPCoopAppenderDB();
							OpenspcoopAppender appenderProperties = new OpenspcoopAppender();
							Property propertyDatasource = new Property();
							propertyDatasource.setNome("datasource");
							propertyDatasource.setValore(datasource);
							appenderProperties.addProperty(propertyDatasource);
							Property propertyTipoDatabase = new Property();
							propertyTipoDatabase.setNome("tipoDatabase");
							propertyTipoDatabase.setValore(tipoDatabase);
							appenderProperties.addProperty(propertyTipoDatabase);
							Property propertyUsePdDConnnection = new Property();
							propertyUsePdDConnnection.setNome("usePdDConnection");
							propertyUsePdDConnnection.setValore("true");
							appenderProperties.addProperty(propertyUsePdDConnnection);
							appender.initializeAppender(appenderProperties);
							
							Busta bustaRicevuta = traccia.getBusta().invertiBusta(traccia.getBusta().getTipoOraRegistrazione(), 
									traccia.getBusta().getTipoOraRegistrazioneValue());
							bustaRicevuta.setID(ricevuta.id);
							bustaRicevuta.setRiferimentoMessaggio(traccia.getBusta().getID());
							bustaRicevuta.setTipoServizio(traccia.getBusta().getTipoServizio());
							bustaRicevuta.setServizio(traccia.getBusta().getServizio());
							bustaRicevuta.setVersioneServizio(traccia.getBusta().getVersioneServizio());
							bustaRicevuta.setAzione(traccia.getBusta().getAzione());
							
							Riscontro r = new Riscontro();
							r.setTipoOraRegistrazione(traccia.getBusta().getTipoOraRegistrazione());
							r.setOraRegistrazione(ricevuta.date);
							r.setRicevuta(ricevuta.xml);
							r.setID(traccia.getBusta().getID());
							bustaRicevuta.addRiscontro(r);
							
							Traccia tracciaRisposta = new Traccia();
							tracciaRisposta.setBusta(bustaRicevuta);
							tracciaRisposta.setEsitoElaborazioneMessaggioTracciato(EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioRicevuto());
							tracciaRisposta.setCorrelazioneApplicativa(traccia.getCorrelazioneApplicativa());
							tracciaRisposta.setCorrelazioneApplicativaRisposta(traccia.getCorrelazioneApplicativaRisposta());
							tracciaRisposta.setGdo(DateManager.getDate());
							tracciaRisposta.setIdSoggetto(traccia.getIdSoggetto());
							tracciaRisposta.setIdTransazione(traccia.getIdTransazione());
							tracciaRisposta.setLocation("XmlReceiptByDomibus");
							tracciaRisposta.setProtocollo(traccia.getProtocollo());
							tracciaRisposta.setTipoMessaggio(RuoloMessaggio.RISPOSTA);
							tracciaRisposta.setTipoPdD(traccia.getTipoPdD());
							
							appender.log(connection, tracciaRisposta);
							
							this.log.info("Creata traccia con riscontro con id '"+bustaRicevuta.getID()+"' come risposta alla traccia con id long '"+id+"' e id '"+traccia.getBusta().getID()+"'");
							


							org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager jdbcServiceManager =
									(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), 
											connection, true,
											this.daoFactoryServiceManagerPropertiesTransazioni, this.log.getLog());
							ITransazioneService transazioneService = jdbcServiceManager.getTransazioneService();
							UpdateField updateField = new UpdateField(Transazione.model().ID_MESSAGGIO_RISPOSTA, bustaRicevuta.getID());
							transazioneService.updateFields(idTransazione, updateField);
							
							this.log.info("Aggiornata transazione con id '"+idTransazione+"' per indicare l'id di risposta '"+bustaRicevuta.getID()+"' come risposta alla traccia con id long '"+id+"' e id '"+traccia.getBusta().getID()+"'");
							
						}
					}
				}
				
			}
						
		}
		catch(Throwable t) {
			this.log.error(t.getMessage(),t);
			throw new Exception(t);
		}
		finally {
			try {
				if(rs!=null)
					rs.close();
			}catch(Exception e) {}
			try {
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception e) {}
			try {
				if(connection!=null)
					connection.close();
			}catch(Exception e) {}
		}
		
		
	}
	
	private DomibusRicevuta readRicevuta(String idNotifica, String messageStatus) throws Exception{
		
		boolean sendSuccess = AS4Costanti.AS4_MESSAGE_SEND_SUCCESS.equalsIgnoreCase(messageStatus);
		String prefixError = "Ricevuta con id di notifica '"+idNotifica+"' (messageStatus: "+messageStatus+") non trovata.";
		
		String datasource = this.properties.getAckDomibusDatasource();
		Properties datasourceJndiContext = this.properties.getAckDomibusDatasource_jndiContext();
		String tipoDatabase = this.properties.getAckDomibusTipoDatabase();
		if(tipoDatabase==null) {
			tipoDatabase = OpenSPCoop2Properties.getInstance().getDatabaseType();
		}
		
		GestoreJNDI gestoreJNDI = new GestoreJNDI(datasourceJndiContext);
		DataSource ds = (DataSource) gestoreJNDI.lookup(datasource);
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = ds.getConnection();
			
			ISQLQueryObject sqlQueryObjectCore = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObjectCore.addFromTable(AS4Costanti.AS4_TABELLA_MESSAGE_INFO);
			sqlQueryObjectCore.addSelectField(AS4Costanti.AS4_TABELLA_MESSAGE_INFO_ID_PK);
			sqlQueryObjectCore.addSelectField(AS4Costanti.AS4_TABELLA_MESSAGE_INFO_ID_MESSAGE);
			sqlQueryObjectCore.addSelectField(AS4Costanti.AS4_TABELLA_MESSAGE_INFO_TIMESTAMP);
			sqlQueryObjectCore.addWhereCondition(AS4Costanti.AS4_TABELLA_MESSAGE_INFO_REF_ID_MESSAGE+"=?");
			sqlQueryObjectCore.setANDLogicOperator(true);
			String query = sqlQueryObjectCore.createSQLQuery();
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, idNotifica);
			rs = pstmt.executeQuery();
			long idPK = -1;
			String idMessage = null;
			Timestamp gdoMessage = null;
			if(rs.next()) {
				idPK = rs.getLong(AS4Costanti.AS4_TABELLA_MESSAGE_INFO_ID_PK);
				idMessage = rs.getString(AS4Costanti.AS4_TABELLA_MESSAGE_INFO_ID_MESSAGE);
				gdoMessage = rs.getTimestamp(AS4Costanti.AS4_TABELLA_MESSAGE_INFO_TIMESTAMP);
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			if(idPK<=0) {
				String msg = prefixError+" Query utilizzata: "+query;
				if(sendSuccess) {
					this.log.error(msg);
				}
				else {
					this.log.debug(msg);
				}
				return null;
			}
			
			sqlQueryObjectCore = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObjectCore.addFromTable(AS4Costanti.AS4_TABELLA_SIGNAL_MESSAGE);
			sqlQueryObjectCore.addSelectField(AS4Costanti.AS4_TABELLA_SIGNAL_MESSAGE_COLONNA_ID_RICEVUTA);
			sqlQueryObjectCore.addWhereCondition(AS4Costanti.AS4_TABELLA_SIGNAL_MESSAGE_COLONNA_ID_MESSAGE+"=?");
			sqlQueryObjectCore.setANDLogicOperator(true);
			query = sqlQueryObjectCore.createSQLQuery();
			pstmt = connection.prepareStatement(query);
			pstmt.setLong(1, idPK);
			rs = pstmt.executeQuery();
			long idRicevuta = -1;
			if(rs.next()) {
				idRicevuta = rs.getLong(AS4Costanti.AS4_TABELLA_SIGNAL_MESSAGE_COLONNA_ID_RICEVUTA);
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			if(idRicevuta<=0) {
				String msg = prefixError+" Query utilizzata ("+AS4Costanti.AS4_TABELLA_SIGNAL_MESSAGE_COLONNA_ID_MESSAGE+"="+idPK+"): "+query;
				if(sendSuccess) {
					this.log.error(msg);
				}
				else {
					this.log.debug(msg);
				}
				return null;
			}
			
			sqlQueryObjectCore = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObjectCore.addFromTable(AS4Costanti.AS4_TABELLA_RICEVUTA);
			sqlQueryObjectCore.addSelectField(AS4Costanti.AS4_TABELLA_RICEVUTA_COLONNA_XML);
			sqlQueryObjectCore.addWhereCondition(AS4Costanti.AS4_TABELLA_RICEVUTA_COLONNA_ID_RICEVUTA+"=?");
			sqlQueryObjectCore.setANDLogicOperator(true);
			query = sqlQueryObjectCore.createSQLQuery();
			pstmt = connection.prepareStatement(query);
			pstmt.setLong(1, idRicevuta);
			rs = pstmt.executeQuery();
			String xml = null;
			if(rs.next()) {
				xml = rs.getString(AS4Costanti.AS4_TABELLA_RICEVUTA_COLONNA_XML);
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			if(idRicevuta<=0) {
				String msg = prefixError+" Query utilizzata ("+AS4Costanti.AS4_TABELLA_RICEVUTA_COLONNA_ID_RICEVUTA+"="+idRicevuta+"): "+query;
				if(sendSuccess) {
					this.log.error(msg);
				}
				else {
					this.log.debug(msg);
				}
				return null;
			}
			
			DomibusRicevuta ricevuta = new DomibusRicevuta();
			ricevuta.xml = xml;
			ricevuta.id = idMessage;
			ricevuta.date = gdoMessage;

			this.log.debug("Recuperata ricevuta con id '"+ricevuta.id+"' (data:"+ricevuta.date+"): "+ricevuta.xml);
			
			return ricevuta;
			
		}finally {
			try {
				if(rs!=null)
					rs.close();
			}catch(Exception e) {}
			try {
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception e) {}
			try {
				if(connection!=null)
					connection.close();
			}catch(Exception e) {}
		}
		
	}
}

class DomibusRicevuta {
	
	protected String xml;
	protected String id;
	protected Timestamp date;
	
}
