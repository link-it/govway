/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.RequestInfoConfigUtilities;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.UtilsException;


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

	public static void save(SavedMessage savedMessage,
			OpenSPCoop2Message msg, boolean isRichiesta, boolean portaDiTipoStateless, boolean consumeMessage, Timestamp oraRegistrazione) throws UtilsException{

		if( !portaDiTipoStateless ) {
			StateMessage stateMsg = (isRichiesta) ?  
					(StateMessage)savedMessage.openspcoopstate.getStatoRichiesta() :
						(StateMessage)savedMessage.openspcoopstate.getStatoRisposta();
			Connection connectionDB = stateMsg.getConnectionDB();

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

				// Elimino dalla RequestInfo i dati "cached"
				RequestInfo requestInfoBackup = RequestInfoConfigUtilities.normalizeRequestInfoBeforeSerialization(msg);
				try {
					if(savedMessage.saveOnFS){
						// SAVE IN FILE SYSTEM
						
						String saveDir = savedMessage.getBaseDir();
						if(saveDir==null){
							String errorMsg = "WorkDir non correttamente inizializzata";		
							throw new UtilsException(errorMsg);
						}
						
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
						//System.out.println("---------SALVO RISPOSTA: "+msgByte.toString());
						savedMessage.adapter.setBinaryData(pstmt,5,bout.toByteArray());
						
						// Save message context
						bout = new java.io.ByteArrayOutputStream();
						msg.serializeResourcesTo(bout);
						bout.flush();
						bout.close();
						//System.out.println("---------SALVO CONTEXT: "+msgByte.toString());
						savedMessage.adapter.setBinaryData(pstmt,6,bout.toByteArray());
					}
				}finally {
					if(requestInfoBackup!=null) {
						RequestInfoConfigUtilities.restoreRequestInfoAfterSerialization(msg, requestInfoBackup);
					}
				}

				// Set del contentType nella query
				String contentType = msg.getContentType();
				if(contentType==null || "".equals(contentType)){
					if(ServiceBinding.REST.equals(msg.getServiceBinding())){
						if(MessageType.BINARY.equals(msg.getMessageType())) {
							if(msg.castAsRest().hasContent()) {
								throw new Exception("Rilevata una richiesta "+msg.getServiceBinding()+" "+msg.getMessageType()+" con payload per la quale non è stato fornito un ContentType"); // sul DB e' required la colonna
							}
							else {
								contentType = SavedMessage.REST_CONTENT_TYPE_EMPTY;
							}
						}
						else {
							throw new Exception("Rilevata una richiesta "+msg.getServiceBinding()+" "+msg.getMessageType()+" per la quale non è stato fornito un ContentType"); // sul DB e' required la colonna
						}
					}
					else {
						throw new Exception("Rilevata una richiesta "+msg.getServiceBinding()+" per la quale non è stato fornito un ContentType"); // sul DB e' required la colonna
					}
				}
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
				savedMessage.log.error(errorMsg,e);
				throw new UtilsException(errorMsg,e);
			}
		}else { // if (portaDiTipoStateless){

			if (isRichiesta) ((OpenSPCoopStateless)savedMessage.openspcoopstate).setRichiestaMsg(msg);
			else ((OpenSPCoopStateless)savedMessage.openspcoopstate).setRispostaMsg(msg);


		}

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
			String contentType = msg.getContentType();
			if(contentType==null || "".equals(contentType)){
				if(ServiceBinding.REST.equals(msg.getServiceBinding())){
					if(MessageType.BINARY.equals(msg.getMessageType())) {
						if(msg.castAsRest().hasContent()) {
							throw new Exception("Rilevata una richiesta "+msg.getServiceBinding()+" "+msg.getMessageType()+" con payload per la quale non è stato fornito un ContentType"); // sul DB e' required la colonna
						}
						else {
							contentType = SavedMessage.REST_CONTENT_TYPE_EMPTY;
						}
					}
					else {
						throw new Exception("Rilevata una richiesta "+msg.getServiceBinding()+" "+msg.getMessageType()+" per la quale non è stato fornito un ContentType"); // sul DB e' required la colonna
					}
				}
				else {
					throw new Exception("Rilevata una richiesta "+msg.getServiceBinding()+" per la quale non è stato fornito un ContentType"); // sul DB e' required la colonna
				}
			}
			pstmt.setString(index++,contentType);
			
			// Elimino dalla RequestInfo i dati "cached"
			RequestInfo requestInfoBackup = RequestInfoConfigUtilities.normalizeRequestInfoBeforeSerialization(msg);
			try {
				if(savedMessage.saveOnFS){
					// SAVE IN FILE SYSTEM
					
					String saveDir = savedMessage.getBaseDir();
					if(saveDir==null){
						String errorMsg = "WorkDir non correttamente inizializzata";		
						throw new UtilsException(errorMsg);
					}
					
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
					//System.out.println("---------SALVO RISPOSTA: "+msgByte.toString());
					savedMessage.adapter.setBinaryData(pstmt,index++,bout.toByteArray());
					
					// Save message context
					bout = new java.io.ByteArrayOutputStream();
					msg.serializeResourcesTo(bout);
					bout.flush();
					bout.close();
					//System.out.println("---------SALVO CONTEXT: "+msgByte.toString());
					savedMessage.adapter.setBinaryData(pstmt,index++,bout.toByteArray());
				}
			}finally {
				if(requestInfoBackup!=null) {
					RequestInfoConfigUtilities.restoreRequestInfoAfterSerialization(msg, requestInfoBackup);
				}
			}
			
			pstmt.setString(index++,savedMessage.idMessaggio);
			if(Costanti.INBOX.equals(savedMessage.box))
				pstmt.setString(index++,Costanti.INBOX);
			else
				pstmt.setString(index++,Costanti.OUTBOX);		


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
			savedMessage.log.error(errorMsg,e);
			throw new UtilsException(errorMsg,e);
		}

	}     
	
}





