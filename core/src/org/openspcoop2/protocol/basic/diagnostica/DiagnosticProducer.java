/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.basic.diagnostica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.basic.BasicConnectionResult;
import org.openspcoop2.protocol.basic.BasicProducer;
import org.openspcoop2.protocol.basic.BasicProducerType;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoException;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKey;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyJDBCType;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyObject;
import org.openspcoop2.utils.jdbc.JDBCUtilities;


/**
 * Contiene l'implementazione di un appender personalizzato,
 * per la registrazione dei msg diagnostici su database.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DiagnosticProducer extends BasicProducer implements IDiagnosticProducer{

	public DiagnosticProducer(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory, BasicProducerType.DIAGNOSTIC);
	}
	
	
    
    /**
	 * Inizializza l'engine di un appender per la registrazione
	 * di un msg Diagnostico emesso da una porta di dominio.
	 * 
	 * @param appenderProperties Proprieta' dell'appender
	 * @throws MsgDiagnosticoException
	 */
	@Override
	public void initializeAppender(OpenspcoopAppender appenderProperties) throws MsgDiagnosticoException{
		
		try{
			this.initializeAppender(appenderProperties, false);
		}catch(Exception e){
			throw new MsgDiagnosticoException("Errore durante l'inizializzazione dell'appender: "+e.getMessage(),e);
		}
	}

	
	/**
	 * Registra un msg Diagnostico emesso da una porta di dominio,
	 * utilizzando le informazioni definite dalla specifica SPC.
	 * 
	 * @param msgDiagnostico Messaggio diagnostico
	 * @throws MsgDiagnosticoException
	 */
	@Override
	public void log(Connection conOpenSPCoopPdD,MsgDiagnostico msgDiagnostico) throws MsgDiagnosticoException{
		PreparedStatement stmt = null;
		Connection con = null;
		String messaggio = msgDiagnostico.getMessaggio();
		BasicConnectionResult cr = null;
		try{
			
			Date gdo = msgDiagnostico.getGdo();
			IDSoggetto idPorta = msgDiagnostico.getIdSoggetto();
			String idFunzione = msgDiagnostico.getIdFunzione();
			int severita = msgDiagnostico.getSeverita();
			String idBusta = msgDiagnostico.getIdBusta();
			String idBustaRisposta = msgDiagnostico.getIdBustaRisposta();
			String codiceDiagnostico = msgDiagnostico.getCodice();
			
			String idTransazione = msgDiagnostico.getProperty(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			
			if(this.debug){
				this.log.debug("@@ log idTransazione["+idTransazione+"] idBusta["+idBusta+"] ....");
			}
			
			TipiDatabase tipo = null;
			if(this.tipoDatabase!=null){
				if(!TipiDatabase.isAMember(this.tipoDatabase)){
					throw new MsgDiagnosticoException("Tipo database ["+this.tipoDatabase+"] non supportato");
				}
				tipo = TipiDatabase.toEnumConstant(this.tipoDatabase);
			}
			
			//	Connessione al DB
			cr = this.getConnection(conOpenSPCoopPdD,"log");
			con = cr.getConnection();

			if(this.debug){
				this.log.debug("@@ log idTransazione["+idTransazione+"] idBusta["+idBusta+"] (getConnection finished) ....");
			}
			
			if(tipo==null){
			
				// Inserimento della traccia nel DB in modalità retro compatibile
				// in questa versione non viene recuperato l'id long
				
				if(this.debug){
					this.log.debug("@@ log idTransazione["+idTransazione+"] idBusta["+idBusta+"] (inserimentoDiagnostico BackwardCompatible) ....");
				}
				
				String updateString = "INSERT INTO "+CostantiDB.MSG_DIAGNOSTICI+" ("+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_GDO+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_CODICE+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_TIPO_SOGGETTO+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_NOME_SOGGETTO+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDFUNZIONE+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_SEVERITA+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO_RISPOSTA+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_PROTOCOLLO+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE+""+
				") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?)";
				int index = 1;
				stmt = con.prepareStatement(updateString);
				if(gdo!=null)
					stmt.setTimestamp(index++, new java.sql.Timestamp(gdo.getTime()));
				else
					stmt.setTimestamp(index++,null);
				JDBCUtilities.setSQLStringValue(stmt,index++, idPorta.getCodicePorta());
				JDBCUtilities.setSQLStringValue(stmt,index++, idPorta.getTipo());
				JDBCUtilities.setSQLStringValue(stmt,index++, idPorta.getNome());
				JDBCUtilities.setSQLStringValue(stmt,index++, idFunzione);
				stmt.setInt(index++, severita);
				JDBCUtilities.setSQLStringValue(stmt,index++, messaggio);
				JDBCUtilities.setSQLStringValue(stmt,index++, idBusta);
				JDBCUtilities.setSQLStringValue(stmt,index++, idBustaRisposta);
				JDBCUtilities.setSQLStringValue(stmt,index++,msgDiagnostico.getProtocollo());
				JDBCUtilities.setSQLStringValue(stmt,index++, codiceDiagnostico);
				JDBCUtilities.setSQLStringValue(stmt,index++, idTransazione);
				stmt.executeUpdate();
				stmt.close();
				
				if(this.debug){
					this.log.debug("@@ log idTransazione["+idTransazione+"] idBusta["+idBusta+"] (inserimentoDiagnostico BackwardCompatible terminato) ....");
				}
				
			}
			else{
				
				// Modalità di inserimento dove viene recuperato l'id long
				
				if(this.debug){
					this.log.debug("@@ log idTransazione["+idTransazione+"] idBusta["+idBusta+"] (inserimentoDiagnostico) ....");
				}
				
				List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
				java.sql.Timestamp gdoT = null;
				if(gdo!=null)
					gdoT =  new java.sql.Timestamp(gdo.getTime());
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_GDO, gdoT , InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_CODICE, getSQLStringValue(idPorta.getCodicePorta()), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_TIPO_SOGGETTO, getSQLStringValue(idPorta.getTipo()), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_NOME_SOGGETTO, getSQLStringValue(idPorta.getNome()), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDFUNZIONE, getSQLStringValue(idFunzione), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_SEVERITA, severita, InsertAndGeneratedKeyJDBCType.INT) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO, getSQLStringValue(messaggio), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO, getSQLStringValue(idBusta), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO_RISPOSTA, getSQLStringValue(idBustaRisposta), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_PROTOCOLLO, getSQLStringValue(msgDiagnostico.getProtocollo()), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE, getSQLStringValue(codiceDiagnostico), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE, getSQLStringValue(idTransazione), InsertAndGeneratedKeyJDBCType.STRING) );
								
				// ** Insert and return generated key
				long iddiagnostico = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, tipo, 
						new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGNOSTICI, CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID, CostantiDB.MSG_DIAGNOSTICI_SEQUENCE, CostantiDB.MSG_DIAGNOSTICI_TABLE_FOR_ID),
						listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
				if(iddiagnostico<=0){
					throw new Exception("ID autoincrementale non ottenuto");
				}
				msgDiagnostico.setId(iddiagnostico);
				
				if(this.debug){
					this.log.debug("@@ log idTransazione["+idTransazione+"] idBusta["+idBusta+"] (inserimentoDiagnostico terminato) ....");
				}
			}
			
			if(this.debug){
				this.log.debug("@@ log idTransazione["+idTransazione+"] idBusta["+idBusta+"] completato");
			}
			
		}catch(Exception e){
			throw new MsgDiagnosticoException("Errore durante la registrazione del msg diagnostico: "+e.getMessage()+"\nIl messaggio era: "+messaggio,e);
		}finally{
			try{
				if(stmt!=null)
					stmt.close();
			}catch(Exception e){}
			try{
				this.releaseConnection(cr, "log");
			}catch(Exception e){}
		}
	}
		

}
