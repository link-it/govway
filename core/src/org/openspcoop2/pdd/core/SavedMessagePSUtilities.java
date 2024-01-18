/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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




package org.openspcoop2.pdd.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.context.SerializedContext;
import org.openspcoop2.message.context.SerializedParameter;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.RequestInfoConfigUtilities;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.serialization.JavaSerializer;


/**
 * SavedMessagePSUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */



public class SavedMessagePSUtilities {
	
	private SavedMessagePSUtilities() {}

	public static void save(SavedMessage savedMessage,
			OpenSPCoop2Message msg, boolean isRichiesta, boolean portaDiTipoStateless, boolean consumeMessage, Timestamp oraRegistrazione) throws UtilsException{

		if( !portaDiTipoStateless ) {
			StateMessage stateMsg = (isRichiesta) ?  
					(StateMessage)savedMessage.openspcoopstate.getStatoRichiesta() :
						(StateMessage)savedMessage.openspcoopstate.getStatoRisposta();
			Connection connectionDB = stateMsg.getConnectionDB();

			saveStateful(savedMessage, 
					msg, consumeMessage, oraRegistrazione,
					connectionDB, stateMsg);
			
		}else { /** if (portaDiTipoStateless){ */

			if (isRichiesta) ((OpenSPCoopStateless)savedMessage.openspcoopstate).setRichiestaMsg(msg);
			else ((OpenSPCoopStateless)savedMessage.openspcoopstate).setRispostaMsg(msg);


		}

	}  
	
	private static void saveStateful(SavedMessage savedMessage, 
			OpenSPCoop2Message msg, boolean consumeMessage, Timestamp oraRegistrazione,
			Connection connectionDB, StateMessage stateMsg) throws UtilsException {
		PreparedStatement pstmt = null;
		try{
			// Save proprieta' msg
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO  ");
			query.append(GestoreMessaggi.DEFINIZIONE_MESSAGGI);
			if(savedMessage.saveOnFS)
				query.append(" (ID_MESSAGGIO,TIPO,CONTENT_TYPE,ORA_REGISTRAZIONE) VALUES ( ? , ? , ? , ? )");
			else
				query.append(" (ID_MESSAGGIO,TIPO,CONTENT_TYPE,ORA_REGISTRAZIONE,MSG_BYTES,MSG_CONTEXT) VALUES ( ? , ? , ? , ? , ? , ?)");

			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,savedMessage.idMessaggio);
			if(Costanti.INBOX.equals(savedMessage.box))
				pstmt.setString(2,Costanti.INBOX);
			else
				pstmt.setString(2,Costanti.OUTBOX);		

			//Sposto il set del contentType dopo la writeTo del messaggio 
			//cosi nel caso di attachment lo trovo corretto.

			saveNormalizeRequestInfoBeforeSerialization(savedMessage,
					msg, consumeMessage,
					pstmt);

			// Set del contentType nella query
			String contentType = readContentType(msg);
			pstmt.setString(3,contentType);
			
			// Set Ora Registrazione
			pstmt.setTimestamp(4,oraRegistrazione);

			// Add PreparedStatement
			stateMsg.getPreparedStatement().put("INSERT (MSG_OP_STEP1a) saveMessage["+savedMessage.idMessaggio+","+savedMessage.box+"]",pstmt);

		}catch(Exception e){
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception err) {
				// close
			}
			String errorMsg = "SOAP_MESSAGE, save : "+savedMessage.box+"/"+savedMessage.idMessaggio+": "+e.getMessage();		
			savedMessage.logError(errorMsg,e);
			throw new UtilsException(errorMsg,e);
		}
	}
	
	private static void saveNormalizeRequestInfoBeforeSerialization(SavedMessage savedMessage,
			OpenSPCoop2Message msg, boolean consumeMessage,
			PreparedStatement pstmt) throws UtilsException, MessageException, IOException, SQLException {
		// Elimino dalla RequestInfo i dati "cached"
		RequestInfo requestInfoBackup = RequestInfoConfigUtilities.normalizeRequestInfoBeforeSerialization(msg);
		try {
			save(savedMessage,
					msg, consumeMessage,
					pstmt);
		}finally {
			if(requestInfoBackup!=null) {
				RequestInfoConfigUtilities.restoreRequestInfoAfterSerialization(msg, requestInfoBackup);
			}
		}
	}
	private static void save(SavedMessage savedMessage,
			OpenSPCoop2Message msg, boolean consumeMessage,
			PreparedStatement pstmt) throws UtilsException, MessageException, IOException, SQLException {
		if(savedMessage.saveOnFS){
			// SAVE IN FILE SYSTEM
			
			String saveDir = savedMessage.getBaseDir();
			savedMessage.checkInizializzazioneWorkingDir(saveDir);
			
			// Save bytes message
			String pathBytes = saveDir + savedMessage.keyMsgBytes;
			savedMessage.saveMessageBytes(pathBytes,msg, consumeMessage,false);
			
			// Save message context
			String pathContext = saveDir + savedMessage.keyMsgContext;
			savedMessage.saveMessageContext(pathContext,msg,false);
			
		}else{
			// SAVE IN DB
			
			// Save bytes message
			java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
			msg.writeTo(bout,consumeMessage);
			bout.flush();
			bout.close();
			/** System.out.println("---------SALVO RISPOSTA: "+msgByte.toString()); */
			savedMessage.adapter.setBinaryData(pstmt,5,bout.toByteArray());
			
			// Save message context
			bout = new java.io.ByteArrayOutputStream();
			msg.serializeResourcesTo(bout);
			bout.flush();
			bout.close();
			/** System.out.println("---------SALVO CONTEXT: "+msgByte.toString()); */
			savedMessage.adapter.setBinaryData(pstmt,6,bout.toByteArray());
		}
	}
	private static String readContentType(OpenSPCoop2Message msg) throws UtilsException, MessageException, MessageNotSupportedException {
		
		String prefix = "Rilevata una richiesta "+msg.getServiceBinding();
		
		String contentType = msg.getContentType();
		if(contentType==null || "".equals(contentType)){
			if(ServiceBinding.REST.equals(msg.getServiceBinding())){
				if(MessageType.BINARY.equals(msg.getMessageType())) {
					if(msg.castAsRest().hasContent()) {
						throw new UtilsException(prefix+" "+msg.getMessageType()+" con payload per la quale non è stato fornito un ContentType"); // sul DB e' required la colonna
					}
					else {
						contentType = SavedMessage.REST_CONTENT_TYPE_EMPTY;
					}
				}
				else {
					throw new UtilsException(prefix+" "+msg.getMessageType()+" per la quale non è stato fornito un ContentType"); // sul DB e' required la colonna
				}
			}
			else {
				throw new UtilsException(prefix+" per la quale non è stato fornito un ContentType"); // sul DB e' required la colonna
			}
		}
		return contentType;
	}

	
	public static void updateResponse(SavedMessage savedMessage,
			OpenSPCoop2Message msg, boolean consumeMessage) throws UtilsException{

		StateMessage stateMsg = (StateMessage)savedMessage.openspcoopstate.getStatoRisposta();
		Connection connectionDB = stateMsg.getConnectionDB();

		PreparedStatement pstmt = null;
		try{
			// Save proprieta' msg
			StringBuilder query = new StringBuilder();
			query.append("UPDATE ");
			query.append(GestoreMessaggi.DEFINIZIONE_MESSAGGI);
			query.append(" SET ");
			query.append(" RESPONSE_CONTENT_TYPE=? ");
			if(!savedMessage.saveOnFS) {
				query.append(" , RESPONSE_MSG_BYTES=? ");
				query.append(" , RESPONSE_MSG_CONTEXT=? ");
			}
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");


			pstmt = connectionDB.prepareStatement(query.toString());
			int index = 1;
			
			// Set del contentType nella query
			String contentType = readContentType(msg);
			pstmt.setString(index++,contentType);
			
			index = updateResponseNormalizeRequestInfoBeforeSerialization(savedMessage,
					msg, consumeMessage,
					pstmt, index);
			
			pstmt.setString(index++,savedMessage.idMessaggio);
			if(Costanti.INBOX.equals(savedMessage.box))
				pstmt.setString(index,Costanti.INBOX);
			else
				pstmt.setString(index,Costanti.OUTBOX);		


			// Add PreparedStatement
			stateMsg.getPreparedStatement().put("UPDATE (RESPONSE) saveMessage["+savedMessage.idMessaggio+","+savedMessage.box+"]",pstmt);
	
		}catch(Exception e){
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception err) {
				// close
			}
			String errorMsg = "SOAP_MESSAGE, update response : "+savedMessage.box+"/"+savedMessage.idMessaggio+": "+e.getMessage();		
			savedMessage.logError(errorMsg,e);
			throw new UtilsException(errorMsg,e);
		}

	}     
	
	private static int updateResponseNormalizeRequestInfoBeforeSerialization(SavedMessage savedMessage,
			OpenSPCoop2Message msg, boolean consumeMessage,
			PreparedStatement pstmt, int index) throws UtilsException, MessageException, IOException, SQLException {
		// Elimino dalla RequestInfo i dati "cached"
		RequestInfo requestInfoBackup = RequestInfoConfigUtilities.normalizeRequestInfoBeforeSerialization(msg);
		try {
			return updateResponse(savedMessage,
					msg, consumeMessage,
					pstmt, index);
		}finally {
			if(requestInfoBackup!=null) {
				RequestInfoConfigUtilities.restoreRequestInfoAfterSerialization(msg, requestInfoBackup);
			}
		}
	}
	private static int updateResponse(SavedMessage savedMessage,
			OpenSPCoop2Message msg, boolean consumeMessage,
			PreparedStatement pstmt, int index) throws UtilsException, MessageException, IOException, SQLException {
		if(savedMessage.saveOnFS){
			// SAVE IN FILE SYSTEM
			
			String saveDir = savedMessage.getBaseDir();
			savedMessage.checkInizializzazioneWorkingDir(saveDir);
			
			// Save bytes message
			String pathBytes = saveDir + savedMessage.keyMsgResponseBytes;
			savedMessage.saveMessageBytes(pathBytes,msg, consumeMessage, false);
			
			// Save message context
			String pathContext = saveDir + savedMessage.keyMsgResponseContext;
			savedMessage.saveMessageContext(pathContext,msg, false);
			
		}else{
			// SAVE IN DB
			
			// Save bytes message
			java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
			msg.writeTo(bout,consumeMessage);
			bout.flush();
			bout.close();
			/** System.out.println("---------SALVO RISPOSTA: "+msgByte.toString()); */
			savedMessage.adapter.setBinaryData(pstmt,index++,bout.toByteArray());
			
			// Save message context
			bout = new java.io.ByteArrayOutputStream();
			msg.serializeResourcesTo(bout);
			bout.flush();
			bout.close();
			/** System.out.println("---------SALVO CONTEXT: "+msgByte.toString()); */
			savedMessage.adapter.setBinaryData(pstmt,index++,bout.toByteArray());
		}
		return index;
	}
	
	
	
	
	
	
	
	public static void updateTransactionContext(SavedMessage savedMessage,Context transactionContext) throws UtilsException{

		if(transactionContext==null || transactionContext.isEmpty()) {
			return;
		}
		
		SerializedContext sc = buildSerializedContext(transactionContext);
		
		if(savedMessage.saveOnFS) {
			
			String saveDir = savedMessage.getBaseDir();
			savedMessage.checkInizializzazioneWorkingDir(saveDir);
			
			// Save bytes message
			String pathBytes = saveDir + savedMessage.keyMsgTransactionContext;
			savedMessage.saveTransactionContext(pathBytes,sc, false);
			
		}
		else {
		
			StateMessage stateMsg = (StateMessage)savedMessage.openspcoopstate.getStatoRisposta();
			Connection connectionDB = stateMsg.getConnectionDB();
	
			// Save proprieta' msg
			StringBuilder query = new StringBuilder();
			query.append("UPDATE ");
			query.append(GestoreMessaggi.DEFINIZIONE_MESSAGGI);
			query.append(" SET ");
			query.append(" TRANSACTION_CONTEXT=? ");
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");
			
			try (PreparedStatement pstmt = connectionDB.prepareStatement(query.toString());){
				int index = 1;
				
				java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
				sc.writeTo(bout, WriteToSerializerType.XML_JAXB);
				bout.flush();
				bout.close();
				
				/** System.out.println("---------SALVO TRANSACTION CONTEXT: "+msgByte.toString()); */
				savedMessage.adapter.setBinaryData(pstmt,index++,bout.toByteArray());
				
				pstmt.setString(index++,savedMessage.idMessaggio);
				if(Costanti.INBOX.equals(savedMessage.box))
					pstmt.setString(index,Costanti.INBOX);
				else
					pstmt.setString(index,Costanti.OUTBOX);		
				
				pstmt.executeUpdate();
		
			}catch(Exception e){
				String errorMsg = "SOAP_MESSAGE, update transaction context : "+savedMessage.box+"/"+savedMessage.idMessaggio+": "+e.getMessage();		
				savedMessage.logError(errorMsg,e);
				throw new UtilsException(errorMsg,e);
			}
			
		}

	}  
	private static SerializedContext buildSerializedContext(Context transactionContext) throws UtilsException{
		
		try {
		
			SerializedContext sc = new SerializedContext();
			JavaSerializer jSerializer = new JavaSerializer();
			for (MapKey<String> key : transactionContext.keySet()) {
				Object o = transactionContext.get(key);
				if(!CostantiPdD.SALVA_CONTESTO_IDENTIFICATIVO_MESSAGGIO_NOTIFICA.equals(key) && 
						(o instanceof Serializable)
						) {
					SerializedParameter p = new SerializedParameter();
					p.setNome(key.getValue());
					p.setClasse(o.getClass().getName());
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					jSerialize(jSerializer, o, bout, p);
					bout.flush();
					bout.close();
					p.setBase(bout.toByteArray());
					sc.addProperty(p);
				}
			}
			return sc;
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private static void jSerialize(JavaSerializer jSerializer, Object o, ByteArrayOutputStream bout, SerializedParameter p) throws UtilsException {
		try {
			jSerializer.writeObject(o, bout);
		}catch(Exception t) {
			throw new UtilsException("Serialization error (nome:"+p.getNome()+" classe:"+p.getClasse()+"): "+t.getMessage(), t);
		}
	}
}





