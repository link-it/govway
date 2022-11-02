/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.context.MessageContext;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.RequestInfoConfigUtilities;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.slf4j.Logger;


/**
 * Classe utilizzata per rappresentare un messaggio Soap nel contesto della libreria.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */



public class SavedMessage implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** Logger utilizzato per debug. */
	private Logger log = null;

	private static final String REST_CONTENT_TYPE_EMPTY = "____EMPTY____";
	
	private static final String MSG_BYTES = "_bytes.bin";
	private static final String MSG_CONTEXT = "_context.bin";
	private static final String MSG_RESPONSE_BYTES = "_response_bytes.bin";
	private static final String MSG_RESPONSE_CONTEXT = "_response_context.bin";


	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Identificativo del Messaggio */
	private String idMessaggio;
	/** Indicazione se il messaggio sara' salvato nella INBOX dei messaggi, o nella OUTBOX */
	private String box;
	/** Indicazione se il messaggio deve essere registrato su file System o su DB */
	private boolean saveOnFS;
	/** Identificativo del Messaggio passato sotto una funziona HASH */
	private String keyMsgBytes;
	/** Identificativo del MessaggeContext passato sotto una funziona HASH */
	private String keyMsgContext;
	/** Indica la directory dove effettuare salvataggi */
	/** Identificativo del Messaggio passato sotto una funziona HASH */
	private String keyMsgResponseBytes;
	/** Identificativo del MessaggeContext passato sotto una funziona HASH */
	private String keyMsgResponseContext;
	/** Indica la directory dove effettuare salvataggi */
	private String workDir;
	/** AdapterJDBC */
	private IJDBCAdapter adapter;


	private IOpenSPCoopState openspcoopstate;

	/** OpenSPCoopProperties */
	private OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();




	/* ********  C O S T R U T T O R E  ******** */
	/**
	 * Costruttore. 
	 *
	 * @param idMsg ID del Messaggio
	 * @param openspcoopstate state Oggetto che rappresenta lo stato di una busta
	 * @param box Indicazione se il messaggio sara' salvato nella INBOX dei messaggi, o nella OUTBOX
	 * @param workDir Directory dove effettuare salvataggi se il messaggio deve essere registrato su FileSystem, null se il messaggio deve essere registrato su DB
	 * 
	 */
	public SavedMessage(String idMsg, IOpenSPCoopState openspcoopstate, String box, String workDir,Logger alog) throws UtilsException{
		this(idMsg, openspcoopstate ,box,workDir,null,alog);
	}

	/**
	 * Costruttore. 
	 *
	 * @param idMsg ID del Messaggio
	 * @param openspcoopstate state Oggetto che rappresenta lo stato di una busta
	 * @param box Indicazione se il messaggio sara' salvato nella INBOX dei messaggi, o nella OUTBOX
	 * @param adapterJDBC  JDBCAdapter se il messaggio deve essere registrato su DB, null se il messaggio deve essere registrato su file System 
	 * 
	 */
	public SavedMessage(String idMsg, IOpenSPCoopState openspcoopstate, String box, IJDBCAdapter adapterJDBC,Logger alog) throws UtilsException{
		this(idMsg, openspcoopstate ,box,null,adapterJDBC,alog);
	}


	/**
	 * Costruttore. 
	 *
	 * @param idMsg ID del Messaggio
	 * @param openspcoopstate state Oggetto che rappresenta lo stato di una busta
	 * @param box Indicazione se il messaggio sara' salvato nella INBOX dei messaggi, o nella OUTBOX
	 * @param workDir Directory dove effettuare salvataggi se il messaggio deve essere registrato su FileSystem, null se il messaggio deve essere registrato su DB
	 * @param adapterJDBC  JDBCAdapter se il messaggio deve essere registrato su DB, null se il messaggio deve essere registrato su file System 
	 * 
	 */
	public SavedMessage(String idMsg, IOpenSPCoopState openspcoopstate, String box, String workDir, 
			IJDBCAdapter adapterJDBC,Logger alog) throws UtilsException{
		this.idMessaggio = idMsg;
		this.box = box;
		this.openspcoopstate = openspcoopstate;
		if(alog!=null){
			this.log = alog;
		}else{
			this.log = LoggerWrapperFactory.getLogger(SavedMessage.class);
		}
		try{

			String hashKeyMsgBytes = this.hash(idMsg);
			if(hashKeyMsgBytes == null){
				throw new Exception("Codifica hash non riuscita: keyMsgBytes is null");
			}
			this.keyMsgBytes = hashKeyMsgBytes + MSG_BYTES;
			this.keyMsgResponseBytes = hashKeyMsgBytes + MSG_RESPONSE_BYTES;

			String hashKeyMsgContext = this.hash(idMsg);
			if(hashKeyMsgContext == null){
				throw new Exception("Codifica hash non riuscita: keyMsgContext is null");
			}
			this.keyMsgContext = hashKeyMsgContext + MSG_CONTEXT;
			this.keyMsgResponseContext = hashKeyMsgContext + MSG_RESPONSE_CONTEXT;

		}catch(Exception e){
			String errorMsg = "SOAP_MESSAGE, costructor error (CodificaHash): "+box+"/"+idMsg+": "+e.getMessage();		
			this.log.error(errorMsg);
			throw new UtilsException(errorMsg,e);
		}

		if(adapterJDBC==null){
			this.saveOnFS = true;
			this.workDir = workDir;
		}else{
			this.saveOnFS = false;
			this.adapter = adapterJDBC;
		}
	}

	/**
	 * Ritorna un intero che rappresenta la chiave di una stringa.
	 *
	 * @param key Stringa su cui effettuare la traduzione.
	 * @return hash della stringa.
	 * 
	 */
	private String hash(String key) throws UtilsException{
		try{
			StringBuilder returnKey = new StringBuilder();
			for(int i=0; i<key.length();i++){
				if( (key.charAt(i) != '_') && (key.charAt(i) != '-') &&
						(key.charAt(i) != '.') && (key.charAt(i) != ':') )
					returnKey.append(key.charAt(i));
			}

			//log.info("Costruito ["+returnKey.toString()+"]");

			return returnKey.toString();

		} catch (java.lang.Exception e) {
			throw new UtilsException("Utilities.hash error "+e.getMessage(),e);
		}
	}








	/* ********  U T I L I T Y  ******** */

	/**
	 * Ritorna la directory base su cui effettuare salvataggi 
	 *
	 * @return Ritorna la directory base su cui effettuare salvataggi 
	 * 
	 */
	public String getBaseDir() throws UtilsException{

		// Controllo esistenza directory fornita per salvare i messaggi
		File dir = new File(this.workDir);
		if(dir.exists() == false){
			String errorMsg = "SOAP_MESSAGE, getBaseDir: "+this.box+"/"+this.idMessaggio+": directory di lavoro inesistente ("+this.workDir+").";		
			this.log.error(errorMsg);
			throw new UtilsException(errorMsg);
		}
		String baseDir = this.workDir;
		if (!baseDir.endsWith(File.separator))
			baseDir = baseDir + File.separator;

		// Seleziono INBOX/OUTBOX
		if(Costanti.INBOX.equals(this.box)){
			baseDir = baseDir + Costanti.INBOX;
		}else if (Costanti.OUTBOX.equals(this.box)){
			baseDir = baseDir + Costanti.OUTBOX;
		}else{
			String errorMsg = "SOAP_MESSAGE, getBaseDir: "+this.box+"/"+this.idMessaggio+": box non valido? .";		
			this.log.error(errorMsg);
			throw new UtilsException(errorMsg);
		}

		// Controllo esistenza di INBUX/OUTBOX
		File dirINOUT = new File(baseDir);
		if(dirINOUT.exists() == false){
			if(dirINOUT.mkdir()==false){
				String errorMsg = "SOAP_MESSAGE, getBaseDir: "+this.box+"/"+this.idMessaggio+": directory di lavoro ("+this.workDir+") non permette la creazione di sottodirectory INBOX/OUTBOX.";		
				this.log.error(errorMsg);
				throw new UtilsException(errorMsg);
			}
		}

		return (baseDir+ File.separator);

	}

	/**
	 * Ritorna il codice del Messaggio
	 *
	 * @return Codice.
	 * 
	 */
	public String getIdMessaggio(){
		return this.idMessaggio;
	}

	/**
	 *Aggiorna lo stato
	 */
	public void updateState(IOpenSPCoopState openspcoopstate){
		this.openspcoopstate = openspcoopstate;
	}


	/* ********  S A V E  ******** */

	/**
	 * Dato un messaggio come parametro, si occupa di salvarlo nel filesystem/DB. 
	 * Il SoapEnvelope viene salvato nel FileSystem associandoci l'informazione strutturale <var>idMessaggio</var>.
	 *
	 *
	 * @param msg Messaggio.
	 * 
	 */
	public void save(OpenSPCoop2Message msg, boolean isRichiesta, boolean portaDiTipoStateless, boolean consumeMessage, Timestamp oraRegistrazione) throws UtilsException{

		if( !portaDiTipoStateless ) {
			StateMessage stateMsg = (isRichiesta) ?  
					(StateMessage)this.openspcoopstate.getStatoRichiesta() :
						(StateMessage)this.openspcoopstate.getStatoRisposta();
			Connection connectionDB = stateMsg.getConnectionDB();

			PreparedStatement pstmt = null;
			try{
				// Save proprieta' msg
				StringBuilder query = new StringBuilder();
				query.append("INSERT INTO  ");
				query.append(GestoreMessaggi.DEFINIZIONE_MESSAGGI);
				if(this.saveOnFS)
					query.append(" (ID_MESSAGGIO,TIPO,CONTENT_TYPE,ORA_REGISTRAZIONE) VALUES ( ? , ? , ? , ? )");
				else
					query.append(" (ID_MESSAGGIO,TIPO,CONTENT_TYPE,ORA_REGISTRAZIONE,MSG_BYTES,MSG_CONTEXT) VALUES ( ? , ? , ? , ? , ? , ?)");

				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,this.idMessaggio);
				if(Costanti.INBOX.equals(this.box))
					pstmt.setString(2,Costanti.INBOX);
				else
					pstmt.setString(2,Costanti.OUTBOX);		

				//Sposto il set del contentType dopo la writeTo del messaggio 
				//cosi nel caso di attachment lo trovo corretto.

				// Elimino dalla RequestInfo i dati "cached"
				RequestInfo requestInfoBackup = RequestInfoConfigUtilities.normalizeRequestInfoBeforeSerialization(msg);
				try {
					if(this.saveOnFS){
						// SAVE IN FILE SYSTEM
						
						String saveDir = getBaseDir();
						if(saveDir==null){
							String errorMsg = "WorkDir non correttamente inizializzata";		
							throw new UtilsException(errorMsg);
						}
						
						// Save bytes message
						String pathBytes = saveDir + this.keyMsgBytes;
						this.saveMessageBytes(pathBytes,msg, consumeMessage,false);
						
						// Save message context
						String pathContext = saveDir + this.keyMsgContext;
						this.saveMessageContext(pathContext,msg,false);
						
					}else{
						// SAVE IN DB
						
						// Save bytes message
						java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
						msg.writeTo(bout,consumeMessage);
						bout.flush();
						bout.close();
						//System.out.println("---------SALVO RISPOSTA: "+msgByte.toString());
						this.adapter.setBinaryData(pstmt,5,bout.toByteArray());
						
						// Save message context
						bout = new java.io.ByteArrayOutputStream();
						msg.serializeResourcesTo(bout);
						bout.flush();
						bout.close();
						//System.out.println("---------SALVO CONTEXT: "+msgByte.toString());
						this.adapter.setBinaryData(pstmt,6,bout.toByteArray());
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
								contentType = REST_CONTENT_TYPE_EMPTY;
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
				stateMsg.getPreparedStatement().put("INSERT (MSG_OP_STEP1a) saveMessage["+this.idMessaggio+","+this.box+"]",pstmt);

			}catch(Exception e){
				try{
					if( pstmt != null )
						pstmt.close();
				} catch(Exception err) {}
				String errorMsg = "SOAP_MESSAGE, save : "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
				this.log.error(errorMsg,e);
				throw new UtilsException(errorMsg,e);
			}
		}else if (portaDiTipoStateless){

			if (isRichiesta) ((OpenSPCoopStateless)this.openspcoopstate).setRichiestaMsg(msg);
			else ((OpenSPCoopStateless)this.openspcoopstate).setRispostaMsg(msg);


		}else{
			throw new UtilsException("Metodo invocato con OpenSPCoopState non valido");
		}

	}     
	private void saveMessageBytes(String path,OpenSPCoop2Message msg, boolean consumeMessage, boolean overwrite) throws UtilsException{

		FileOutputStream fos = null;
		try{

			File fileMsg = new File(path);
			if(fileMsg.exists() == true){
				if(overwrite) {
					if(!fileMsg.delete()) {
						throw new UtilsException("L'identificativo del Messaggio risulta già registrato e non eliminabile: "+path);
					}
				}
				else {
					throw new UtilsException("L'identificativo del Messaggio risulta già registrato: "+path);
				}
			}	

			fos = new FileOutputStream(path);
			// Scrittura Messaggio su FileSystem
			msg.writeTo(fos,consumeMessage);
			fos.close();

		}catch(Exception e){
			try{
				if( fos != null )
					fos.close();
			} catch(Exception er) {}
			throw new UtilsException("Utilities.saveMessage error "+e.getMessage(),e);
		}
	}
	private void saveMessageContext(String path,OpenSPCoop2Message msg, boolean overwrite) throws UtilsException{

		FileOutputStream fos = null;
		try{

			File fileMsg = new File(path);
			if(fileMsg.exists() == true){
				if(overwrite) {
					if(!fileMsg.delete()) {
						throw new UtilsException("L'identificativo del Messaggio risulta già registrato e non eliminabile: "+path);
					}
				}
				else {
					throw new UtilsException("L'identificativo del Messaggio risulta gia' registrato: "+path);
				}
			}	

			fos = new FileOutputStream(path);
			// Scrittura Messaggio su FileSystem
			msg.serializeResourcesTo(fos);
			fos.close();

		}catch(Exception e){
			try{
				if( fos != null )
					fos.close();
			} catch(Exception er) {}
			throw new UtilsException("Utilities.saveMessage error "+e.getMessage(),e);
		}
	}

	
	public void updateResponse(OpenSPCoop2Message msg, boolean consumeMessage) throws UtilsException{

		StateMessage stateMsg = (StateMessage)this.openspcoopstate.getStatoRisposta();
		Connection connectionDB = stateMsg.getConnectionDB();

		PreparedStatement pstmt = null;
		try{
			// Save proprieta' msg
			StringBuilder query = new StringBuilder();
			query.append("UPDATE ");
			query.append(GestoreMessaggi.DEFINIZIONE_MESSAGGI);
			query.append(" SET ");
			query.append(" RESPONSE_CONTENT_TYPE=? ");
			if(!this.saveOnFS) {
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
							contentType = REST_CONTENT_TYPE_EMPTY;
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
				if(this.saveOnFS){
					// SAVE IN FILE SYSTEM
					
					String saveDir = getBaseDir();
					if(saveDir==null){
						String errorMsg = "WorkDir non correttamente inizializzata";		
						throw new UtilsException(errorMsg);
					}
					
					// Save bytes message
					String pathBytes = saveDir + this.keyMsgResponseBytes;
					this.saveMessageBytes(pathBytes,msg, consumeMessage, false);
					
					// Save message context
					String pathContext = saveDir + this.keyMsgResponseContext;
					this.saveMessageContext(pathContext,msg, false);
					
				}else{
					// SAVE IN DB
					
					// Save bytes message
					java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
					msg.writeTo(bout,consumeMessage);
					bout.flush();
					bout.close();
					//System.out.println("---------SALVO RISPOSTA: "+msgByte.toString());
					this.adapter.setBinaryData(pstmt,index++,bout.toByteArray());
					
					// Save message context
					bout = new java.io.ByteArrayOutputStream();
					msg.serializeResourcesTo(bout);
					bout.flush();
					bout.close();
					//System.out.println("---------SALVO CONTEXT: "+msgByte.toString());
					this.adapter.setBinaryData(pstmt,index++,bout.toByteArray());
				}
			}finally {
				if(requestInfoBackup!=null) {
					RequestInfoConfigUtilities.restoreRequestInfoAfterSerialization(msg, requestInfoBackup);
				}
			}
			
			pstmt.setString(index++,this.idMessaggio);
			if(Costanti.INBOX.equals(this.box))
				pstmt.setString(index++,Costanti.INBOX);
			else
				pstmt.setString(index++,Costanti.OUTBOX);		


			// Add PreparedStatement
			stateMsg.getPreparedStatement().put("UPDATE (RESPONSE) saveMessage["+this.idMessaggio+","+this.box+"]",pstmt);
	
		}catch(Exception e){
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception err) {}
			String errorMsg = "SOAP_MESSAGE, update response : "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new UtilsException(errorMsg,e);
		}

	}     
	
	



	/* ********  R E A D  ******** */

	/**
	 * Ritorna un messaggio che era stata precedentemente salvata nel filesystem/DB. 
	 *
	 * @return il messaggio precedentemente salvato
	 * 
	 */
	public OpenSPCoop2Message read(boolean isRichiesta, boolean portaDiTipoStateless, Date oraRegistrazione) throws UtilsException {
		 return _read(isRichiesta, portaDiTipoStateless, oraRegistrazione, false);
	}
	public OpenSPCoop2Message readResponse(Date oraRegistrazione) throws UtilsException {
		 return _read(false, false, oraRegistrazione, true);
	}
	public OpenSPCoop2Message _read(boolean isRichiesta, boolean portaDiTipoStateless, Date oraRegistrazione, boolean readResponseField) throws UtilsException {

		if( !portaDiTipoStateless ) {

			@SuppressWarnings("resource")
			Connection connectionDB = (isRichiesta) ? 
					((StateMessage)this.openspcoopstate.getStatoRichiesta()).getConnectionDB() :
						((StateMessage)this.openspcoopstate.getStatoRisposta()).getConnectionDB();

			OpenSPCoop2Message msg = null;
			PreparedStatement pstmt = null;
			InputStream isBytes = null;
			InputStream isContext = null;
			ResultSet rs = null;
			try{

				String columnContentType = readResponseField ? "RESPONSE_CONTENT_TYPE" : "CONTENT_TYPE";
				String columnMsgBytes = readResponseField ? "RESPONSE_MSG_BYTES" : "MSG_BYTES";
				String columnMsgContext = readResponseField ? "RESPONSE_MSG_CONTEXT" : "MSG_CONTEXT";
				
				// Leggo proprieta' messaggio
				StringBuilder query = new StringBuilder();
				if(this.saveOnFS)
					query.append("select "+columnContentType+" ");
				else
					query.append("select "+columnContentType+","+columnMsgBytes+","+columnMsgContext+" ");
				query.append("from ");
				query.append(GestoreMessaggi.DEFINIZIONE_MESSAGGI);
				query.append(" WHERE ");
				if(oraRegistrazione!=null) {
					query.append("(ORA_REGISTRAZIONE BETWEEN ? AND ?) AND ");
				}
				query.append("ID_MESSAGGIO = ? AND TIPO = ?");
				
				pstmt =  connectionDB.prepareStatement(query.toString());
				int index = 1;
				
				Timestamp leftValue = null;
				Timestamp rightValue = null;
				if(oraRegistrazione!=null) {
					leftValue = new Timestamp(oraRegistrazione.getTime() - (1000*60*5));
					rightValue = new Timestamp(oraRegistrazione.getTime() + (1000*60*5));
					pstmt.setTimestamp(index++,leftValue);
					pstmt.setTimestamp(index++,rightValue);
				}
				
				pstmt.setString(index++,this.idMessaggio);
				if(Costanti.INBOX.equals(this.box))
					pstmt.setString(index++,Costanti.INBOX);
				else
					pstmt.setString(index++,Costanti.OUTBOX);
				rs = pstmt.executeQuery();
				if(rs==null){
					String errorMsg = "ResultSet is null?";		
					throw new UtilsException(errorMsg);
				}
				if(rs.next()==false){
					String errorMsg = "Messaggio non esistente";		
					throw new UtilsException(errorMsg);
				}

				String saveDir = null;
				if(this.saveOnFS){
					// READ FROM FILE SYSTEM
					
					saveDir = getBaseDir();
					if(saveDir==null){
						String errorMsg = "WorkDir non correttamente inizializzata";		
						throw new UtilsException(errorMsg);
					}
				}
				
				// Lettura Message Context
				if(this.saveOnFS){
					// READ FROM FILE SYSTEM
					String pathContext = saveDir + (readResponseField ? this.keyMsgResponseContext : this.keyMsgContext);
					File fileCheckContext = new File(pathContext);
					if(fileCheckContext.exists() == false){
						String errorMsg = "Il messaggio (contesto) non risulta gia' registrato ("+pathContext+").";		
						throw new UtilsException(errorMsg);
					}	   
					isContext = new FileInputStream(pathContext);
				}else{
					// READ FROM DB
					isContext = new java.io.ByteArrayInputStream(this.adapter.getBinaryData(rs,3));
				}
				
				org.openspcoop2.message.context.utils.serializer.JaxbDeserializer jaxbDeserializer  = 
						new org.openspcoop2.message.context.utils.serializer.JaxbDeserializer();
				MessageContext msgContext = jaxbDeserializer.readMessageContext(isContext);
				
				if(msgContext.getMessageType()==null) {
					throw new Exception("Message Type undefined in context serialized");
				}
				MessageType mt = MessageType.valueOf(msgContext.getMessageType());
				if(mt==null) {
					throw new Exception("MessageType ["+msgContext.getMessageType()+"] unknown");
				}
				
				if(msgContext.getMessageRole()==null) {
					throw new Exception("Message Role undefined in context serialized");
				}
				MessageRole mr = MessageRole.valueOf(msgContext.getMessageRole());
				if(mr==null) {
					throw new Exception("MessageRole ["+msgContext.getMessageRole()+"] unknown");
				}
				
				// ContentType
				String contentType = rs.getString(columnContentType);
				if(MessageType.BINARY.equals(mt)) {
					if(REST_CONTENT_TYPE_EMPTY.equals(contentType)) {
						contentType = null;
					}
				}
				
				// Lettura Message Bytes
				if(contentType!=null && !"".equals(contentType)) {
					if(this.saveOnFS){
						// READ FROM FILE SYSTEM
						String pathBytes = saveDir + (readResponseField ? this.keyMsgResponseBytes : this.keyMsgBytes);
						File fileCheckBytes = new File(pathBytes);
						if(fileCheckBytes.exists() == false){
							String errorMsg = "Il messaggio non risulta gia' registrato ("+pathBytes+").";		
							throw new UtilsException(errorMsg);
						}	   
						isBytes = new FileInputStream(pathBytes);
					}else{
						// READ FROM DB
						isBytes = new java.io.ByteArrayInputStream(this.adapter.getBinaryData(rs,2));
					}
				}
					
				// CostruzioneMessaggio
				OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
				NotifierInputStreamParams notifierInputStreamParams = null; // Non dovrebbe servire, un eventuale handler attaccato, dovrebbe gia aver ricevuto tutto il contenuto una volta serializzato il messaggio su database.
				OpenSPCoop2MessageParseResult pr = null;
				pr = mf.createMessage(mt,mr,contentType,
						isBytes,notifierInputStreamParams,
						this.openspcoopProperties.getAttachmentsProcessingMode());
				msg = pr.getMessage_throwParseException();
				msg.readResourcesFrom(msgContext);

				rs.close();
				pstmt.close();

			}catch(Exception e){
				try{
					if( rs != null )
						rs.close();
				} catch(Exception er) {}
				try{
					if( pstmt != null )
						pstmt.close();
				} catch(Exception er) {}
				try{
					if( isBytes != null )
						isBytes.close();
				} catch(Exception er) {}
				try{
					if( isContext != null )
						isContext.close();
				} catch(Exception er) {}
				String errorMsg = "SOAP_MESSAGE, read: "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
				this.log.error(errorMsg,e);
				throw new UtilsException(errorMsg,e);
			}

			return msg;
		}else if ( portaDiTipoStateless ){
			if (isRichiesta) return	((OpenSPCoopStateless)this.openspcoopstate).getRichiestaMsg();
			else return	((OpenSPCoopStateless)this.openspcoopstate).getRispostaMsg();
		}else{
			throw new UtilsException("Metodo invocato con IState non valido");

		}
	}










	/* ********  D E L E T E  ******** */

	/**
	 * Elimina un messaggio completamente, sia dal filesystem che dal db 
	 * 
	 * 
	 */
	public void delete(boolean isRichiesta,boolean onewayVersione11, java.sql.Timestamp data) throws UtilsException{
		if((this.openspcoopstate instanceof OpenSPCoopStateful) || onewayVersione11) {
			StateMessage stateMSG = (isRichiesta) ?  (StateMessage)this.openspcoopstate.getStatoRichiesta() 
					: (StateMessage)this.openspcoopstate.getStatoRisposta();
			Connection connectionDB = stateMSG.getConnectionDB();

			PreparedStatement pstmt = null;
			try{
				// Eliminazione da FileSystem
				if(this.saveOnFS){
					
					String saveDir = getBaseDir();
					if(saveDir==null){
						String errorMsg = "WorkDir non correttamente inizializzata";		
						throw new UtilsException(errorMsg);
					}
					
					String pathBytes = saveDir + this.keyMsgBytes;
					File fileDeleteBytes = new File(pathBytes);
					if(fileDeleteBytes.exists()){
						fileDeleteBytes.delete();
					}	
					
					String pathContext = saveDir + this.keyMsgContext;
					File fileDeleteContext = new File(pathContext);
					if(fileDeleteContext.exists()){
						fileDeleteContext.delete();
					}
				}

				//	Eliminazione from DB.
				StringBuilder query = new StringBuilder();
				query.append("DELETE from ");
				query.append(GestoreMessaggi.DEFINIZIONE_MESSAGGI);
				query.append(" WHERE ");
				query.append(GestoreMessaggi.DEFINIZIONE_MESSAGGI_COLUMN_ID_MESSAGGIO);
				query.append(" = ? AND ");
				query.append(GestoreMessaggi.DEFINIZIONE_MESSAGGI_COLUMN_TIPO_MESSAGGIO);
				query.append(" = ?");	    
				if(data!=null) {
					query.append(" AND ");
					query.append(GestoreMessaggi.DEFINIZIONE_MESSAGGI_COLUMN_ORA_REGISTRAZIONE);
					query.append("<=?");
				}
				pstmt= connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,this.idMessaggio);
				if(Costanti.INBOX.equals(this.box))
					pstmt.setString(2,Costanti.INBOX);
				else
					pstmt.setString(2,Costanti.OUTBOX);
				if(data!=null) {
					pstmt.setTimestamp(3, data);
				}
				pstmt.execute();
				pstmt.close();

			}catch(Exception e){
				try{
					if( pstmt != null )
						pstmt.close();
				} catch(Exception er) {}
				String errorMsg = "SOAP_MESSAGE, delete: "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
				this.log.error(errorMsg,e);
				throw new UtilsException(errorMsg,e);
			}
		}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			// NOP
		}else{
			throw new UtilsException("Metodo invocato con IState non valido");
		}
	}

	/**
	 * Elimina un messaggio completamente, sia dal filesystem che dal db 
	 * 
	 * 
	 */
	public void deleteMessageFromFileSystem() {


		try{
			// Eliminazione da FileSystem
			if(this.saveOnFS){
				
				String saveDir = getBaseDir();
				if(saveDir==null){
					String errorMsg = "WorkDir non correttamente inizializzata";		
					throw new UtilsException(errorMsg);
				}
				
				String pathBytes = saveDir + this.keyMsgBytes;
				File fileDeleteBytes = new File(pathBytes);
				if(fileDeleteBytes.exists()){
					fileDeleteBytes.delete();
				}	
								
				String pathContext = saveDir + this.keyMsgContext;
				File fileDeleteContext = new File(pathContext);
				if(fileDeleteContext.exists()){
					fileDeleteContext.delete();
				}
				
				String pathResponseBytes = saveDir + this.keyMsgResponseBytes;
				File fileDeleteResponseBytes = new File(pathResponseBytes);
				if(fileDeleteResponseBytes.exists()){
					fileDeleteResponseBytes.delete();
				}
				
				String pathResponseContext = saveDir + this.keyMsgResponseContext;
				File fileDeleteResponseContext = new File(pathResponseContext);
				if(fileDeleteResponseContext.exists()){
					fileDeleteResponseContext.delete();
				}
			}

		}catch(Exception e){
			String errorMsg = "SOAP_MESSAGE, deleteMessageFromFileSystem: "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
			this.log.error(errorMsg,e);
		}
	}

}





