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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.openspcoop2.generic_project.exception.DeserializerException;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.context.MessageContext;
import org.openspcoop2.message.context.SerializedContext;
import org.openspcoop2.message.context.SerializedParameter;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.serialization.JavaDeserializer;
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
	protected transient Logger internalLog = null;
	private synchronized void initLog() {
		if(this.internalLog==null) {
			this.internalLog = LoggerWrapperFactory.getLogger(SavedMessage.class);
		}
	}
	protected Logger getLog() {
		if(this.internalLog==null) {
			this.initLog();
		}
		return this.internalLog;
	}

	protected static final String REST_CONTENT_TYPE_EMPTY = "____EMPTY____";
	
	private static final String MSG_BYTES = "_bytes.bin";
	private static final String MSG_CONTEXT = "_context.bin";
	private static final String MSG_RESPONSE_BYTES = "_response_bytes.bin";
	private static final String MSG_RESPONSE_CONTEXT = "_response_context.bin";
	private static final String MSG_TRANSACTION_CONTEXT = "_transaction_context.bin";


	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Identificativo del Messaggio */
	protected String idMessaggio;
	/** Indicazione se il messaggio sara' salvato nella INBOX dei messaggi, o nella OUTBOX */
	protected String box;
	/** Indicazione se il messaggio deve essere registrato su file System o su DB */
	protected boolean saveOnFS;
	/** Identificativo del Messaggio passato sotto una funziona HASH */
	protected String keyMsgBytes;
	/** Identificativo del MessaggeContext passato sotto una funziona HASH */
	protected String keyMsgContext;
	/** Indica la directory dove effettuare salvataggi */
	/** Identificativo del Messaggio passato sotto una funziona HASH */
	protected String keyMsgResponseBytes;
	/** Identificativo del MessageContext passato sotto una funziona HASH */
	protected String keyMsgResponseContext;
	/** Identificativo del TransactionContext passato sotto una funziona HASH */
	protected String keyMsgTransactionContext;
	/** Indica la directory dove effettuare salvataggi */
	private String workDir;
	/** AdapterJDBC */
	protected transient IJDBCAdapter adapter;


	protected transient IOpenSPCoopState openspcoopstate;

	/** OpenSPCoopProperties */
	private transient OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();




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
			this.internalLog = alog;
		}else{
			this.internalLog = LoggerWrapperFactory.getLogger(SavedMessage.class);
		}
		try{

			String hashKey = this.hash(idMsg);
			if(hashKey == null){
				throw new UtilsException("Codifica hash non riuscita: keyMsgBytes is null");
			}
			
			this.keyMsgBytes = hashKey + MSG_BYTES;
			this.keyMsgResponseBytes = hashKey + MSG_RESPONSE_BYTES;

			this.keyMsgContext = hashKey + MSG_CONTEXT;
			this.keyMsgResponseContext = hashKey + MSG_RESPONSE_CONTEXT;
			
			this.keyMsgTransactionContext = hashKey + MSG_TRANSACTION_CONTEXT;

		}catch(Exception e){
			String errorMsg = "SOAP_MESSAGE, costructor error (CodificaHash): "+box+"/"+idMsg+": "+e.getMessage();		
			this.getLog().error(errorMsg);
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

		String prefix = "SOAP_MESSAGE, getBaseDir: "+this.box+"/"+this.idMessaggio;
		
		// Controllo esistenza directory fornita per salvare i messaggi
		File dir = new File(this.workDir);
		if(!dir.exists()){
			String errorMsg = prefix+": directory di lavoro inesistente ("+this.workDir+").";		
			this.getLog().error(errorMsg);
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
			String errorMsg = prefix+": box non valido? .";		
			this.getLog().error(errorMsg);
			throw new UtilsException(errorMsg);
		}

		// Controllo esistenza di INBUX/OUTBOX
		File dirINOUT = new File(baseDir);
		if(!dirINOUT.exists() &&
			!dirINOUT.mkdir()){
			String errorMsg = prefix+": directory di lavoro ("+this.workDir+") non permette la creazione di sottodirectory INBOX/OUTBOX.";		
			this.getLog().error(errorMsg);
			throw new UtilsException(errorMsg);
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
	
	protected void checkInizializzazioneWorkingDir(String saveDir) throws UtilsException {
		if(saveDir==null){
			String errorMsg = "WorkDir non correttamente inizializzata";		
			throw new UtilsException(errorMsg);
		}
	}
	
	protected void checkInizializzazioneAdapter() throws UtilsException {
		if(this.adapter==null) {
			throw new UtilsException("Adapter unavailable");
		}
	}
	
	protected void logError(String errorMsg, Exception e) {
		this.getLog().error(errorMsg,e);
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
		SavedMessagePSUtilities.save(this, msg, isRichiesta, portaDiTipoStateless, consumeMessage, oraRegistrazione);
	}     
	protected void saveMessageBytes(String path,OpenSPCoop2Message msg, boolean consumeMessage, boolean overwrite) throws UtilsException{

		FileOutputStream fos = null;
		try{

			File fileMsg = new File(path);
			if(fileMsg.exists()){
				if(overwrite) {
					deleteMessageFile(fileMsg);
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
			} catch(Exception er) {
				// close
			}
			throw new UtilsException("Utilities.saveMessage error "+e.getMessage(),e);
		}
	}
	protected void saveMessageContext(String path,OpenSPCoop2Message msg, boolean overwrite) throws UtilsException{

		FileOutputStream fos = null;
		try{

			File fileMsg = new File(path);
			if(fileMsg.exists()){
				if(overwrite) {
					deleteMessageFile(fileMsg);
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
			} catch(Exception er) {
				// close
			}
			throw new UtilsException("Utilities.saveMessageContext error "+e.getMessage(),e);
		}
	}
	protected void saveTransactionContext(String path,SerializedContext sc, boolean overwrite) throws UtilsException{

		FileOutputStream fos = null;
		try{

			File fileMsg = new File(path);
			if(fileMsg.exists()){
				if(overwrite) {
					deleteMessageFile(fileMsg);
				}
				else {
					throw new UtilsException("L'identificativo del Messaggio risulta gia' registrato: "+path);
				}
			}	

			fos = new FileOutputStream(path);
			// Scrittura Messaggio su FileSystem
			sc.writeTo(fos, WriteToSerializerType.XML_JAXB);
			fos.close();

		}catch(Exception e){
			try{
				if( fos != null )
					fos.close();
			} catch(Exception er) {
				// close
			}
			throw new UtilsException("Utilities.saveTransactionContext error "+e.getMessage(),e);
		}
	}

	
	public void updateResponse(OpenSPCoop2Message msg, boolean consumeMessage) throws UtilsException{
		SavedMessagePSUtilities.updateResponse(this, msg, consumeMessage);
	} 
	
	public void updateTransactionContext(Context transactionContext) throws UtilsException{
		SavedMessagePSUtilities.updateTransactionContext(this, transactionContext);
	} 
	
	private void deleteMessageFile(File fileMsg) throws UtilsException {
		try {
			Files.delete(fileMsg.toPath());
		}catch(Exception e) {
			throw new UtilsException("L'identificativo del Messaggio risulta già registrato e non eliminabile ("+fileMsg.getAbsolutePath()+"): "+e.getMessage(),e);
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
		 return readEngine(isRichiesta, portaDiTipoStateless, oraRegistrazione, false, null);
	}
	public OpenSPCoop2Message readResponse(Date oraRegistrazione) throws UtilsException {
		 return readEngine(false, false, oraRegistrazione, true, null);
	}
	public Context readTransactionContext(Date oraRegistrazione) throws UtilsException {
		Context c = new Context();
		readEngine(false, false, oraRegistrazione, false, c);
		return c;
	}
	private OpenSPCoop2Message readEngine(boolean isRichiesta, boolean portaDiTipoStateless, Date oraRegistrazione, boolean readResponseField, Context readTransactionContext) throws UtilsException {

		if( !portaDiTipoStateless ) {

			Connection connectionDB = (isRichiesta) ? 
					((StateMessage)this.openspcoopstate.getStatoRichiesta()).getConnectionDB() :
						((StateMessage)this.openspcoopstate.getStatoRisposta()).getConnectionDB();

			return readMessage(oraRegistrazione, readResponseField, readTransactionContext, connectionDB);

		}else { /** if ( portaDiTipoStateless ){ */
			if (isRichiesta) return	((OpenSPCoopStateless)this.openspcoopstate).getRichiestaMsg();
			else return	((OpenSPCoopStateless)this.openspcoopstate).getRispostaMsg();
		}
	}

	private OpenSPCoop2Message readMessage(Date oraRegistrazione, boolean readResponseField, Context readTransactionContext, Connection connectionDB) throws UtilsException {
		
		if(readTransactionContext!=null && this.saveOnFS) {
			fillTransactionContextFromFileSystem(readTransactionContext);
			return null;	
		}
		
		OpenSPCoop2Message msg = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{

			String sql = buildReadStatement(oraRegistrazione, readResponseField, readTransactionContext);
			
			pstmt =  connectionDB.prepareStatement(sql);
			initPreparedStatement(oraRegistrazione, pstmt);
			rs = pstmt.executeQuery();
			
			msg = readMessage(rs, readResponseField, readTransactionContext);

			rs.close();
			pstmt.close();

		}catch(Exception e){
			try{
				if( rs != null )
					rs.close();
			} catch(Exception er) {
				// close
			}
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception er) {
				// close
			}
			String errorMsg = "SOAP_MESSAGE, read: "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
			logError(errorMsg, e);
			throw new UtilsException(errorMsg,e);
		}
		
		return msg;
	}
	
	private void fillTransactionContextFromFileSystem(Context readTransactionContext) throws UtilsException {
		String saveDir = getBaseDir();
		checkInizializzazioneWorkingDir(saveDir);
		
		InputStream isContext = null;
		try{
		
			// Lettura Message Context
			isContext = readTransactionContextBytes(saveDir, null);
								
			// CostruzioneMessaggio
			fillTransactionContext(readTransactionContext, isContext);
			
		}catch(Exception e){
			try{
				if( isContext != null )
					isContext.close();
			} catch(Exception er) {
				// close
			}
			throw new UtilsException(e.getMessage(),e);
		}
		
	}
	
	private String buildReadStatement(Date oraRegistrazione, boolean readResponseField, Context readTransactionContext) {
		StringBuilder query = new StringBuilder();
		if(readTransactionContext!=null) {
			query.append("select TRANSACTION_CONTEXT ");
		}
		else {
			String columnContentType = readResponseField ? "RESPONSE_CONTENT_TYPE" : "CONTENT_TYPE";
			String columnMsgBytes = readResponseField ? "RESPONSE_MSG_BYTES" : "MSG_BYTES";
			String columnMsgContext = readResponseField ? "RESPONSE_MSG_CONTEXT" : "MSG_CONTEXT";
			
			// Leggo proprieta' messaggio
			if(this.saveOnFS)
				query.append("select "+columnContentType+" ");
			else
				query.append("select "+columnContentType+","+columnMsgBytes+","+columnMsgContext+" ");
		}
		query.append("from ");
		query.append(GestoreMessaggi.DEFINIZIONE_MESSAGGI);
		query.append(" WHERE ");
		if(oraRegistrazione!=null) {
			query.append("(ORA_REGISTRAZIONE BETWEEN ? AND ?) AND ");
		}
		query.append("ID_MESSAGGIO = ? AND TIPO = ?");
		
		return query.toString();
	}
	
	private void initPreparedStatement(Date oraRegistrazione, PreparedStatement pstmt) throws SQLException {
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
			pstmt.setString(index,Costanti.INBOX);
		else
			pstmt.setString(index,Costanti.OUTBOX);
	}
	
	private InputStream readMessageBytes(String contentType, String saveDir, boolean readResponseField, ResultSet rs) throws UtilsException, FileNotFoundException, SQLException {
		if(contentType!=null && !"".equals(contentType)) {
			if(this.saveOnFS){
				// READ FROM FILE SYSTEM
				String pathBytes = saveDir + (readResponseField ? this.keyMsgResponseBytes : this.keyMsgBytes);
				File fileCheckBytes = new File(pathBytes);
				if(!fileCheckBytes.exists()){
					String errorMsg = "Il messaggio non risulta gia' registrato ("+pathBytes+").";		
					throw new UtilsException(errorMsg);
				}	   
				return new FileInputStream(pathBytes);
			}else{
				// READ FROM DB
				this.checkInizializzazioneAdapter();
				return new java.io.ByteArrayInputStream(this.adapter.getBinaryData(rs,2));
			}
		}
		return null;
	}

	private InputStream readContextBytes(String saveDir, boolean readResponseField, ResultSet rs) throws UtilsException, FileNotFoundException, SQLException {
		if(this.saveOnFS){
			// READ FROM FILE SYSTEM
			String pathContext = saveDir + (readResponseField ? this.keyMsgResponseContext : this.keyMsgContext);
			File fileCheckContext = new File(pathContext);
			if(!fileCheckContext.exists()){
				String errorMsg = "Il messaggio (contesto) non risulta gia' registrato ("+pathContext+").";		
				throw new UtilsException(errorMsg);
			}	   
			return new FileInputStream(pathContext);
		}else{
			// READ FROM DB
			this.checkInizializzazioneAdapter();
			return new java.io.ByteArrayInputStream(this.adapter.getBinaryData(rs,3));
		}
	}
	
	private InputStream readTransactionContextBytes(String saveDir, ResultSet rs) throws UtilsException, FileNotFoundException, SQLException {
		if(this.saveOnFS){
			// READ FROM FILE SYSTEM
			String pathContext = saveDir + this.keyMsgTransactionContext;
			File fileCheckContext = new File(pathContext);
			if(!fileCheckContext.exists()){
				String errorMsg = "Il messaggio (transaction context) non risulta gia' registrato ("+pathContext+").";		
				throw new UtilsException(errorMsg);
			}	   
			return new FileInputStream(pathContext);
		}else{
			// READ FROM DB
			this.checkInizializzazioneAdapter();
			return new java.io.ByteArrayInputStream(this.adapter.getBinaryData(rs,1));
		}
	}
	
	private OpenSPCoop2Message readMessage(ResultSet rs, boolean readResponseField, Context readTransactionContext) throws UtilsException, SQLException {
		if(rs==null){
			String errorMsg = "ResultSet is null?";		
			throw new UtilsException(errorMsg);
		}
		if(!rs.next()){
			String errorMsg = "Messaggio non esistente";		
			throw new UtilsException(errorMsg);
		}

		OpenSPCoop2Message msg = null;
		if(readTransactionContext!=null) {
			fillTransactionContext(rs, readTransactionContext);
		}
		else {
			msg = readMessage(rs, readResponseField);
		}
		return msg;
	}
	
	private void fillTransactionContext(ResultSet rs, Context readTransactionContext) throws UtilsException {
		InputStream isContext = null;
		try{
		
			// Lettura Message Context
			isContext = readTransactionContextBytes(null, rs);
								
			// CostruzioneMessaggio
			fillTransactionContext(readTransactionContext, isContext);
			
		}catch(Exception e){
			try{
				if( isContext != null )
					isContext.close();
			} catch(Exception er) {
				// close
			}
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	private OpenSPCoop2Message readMessage(ResultSet rs, boolean readResponseField) throws UtilsException, SQLException {
		String saveDir = null;
		if(this.saveOnFS){
			// READ FROM FILE SYSTEM
			
			saveDir = getBaseDir();
			checkInizializzazioneWorkingDir(saveDir);
		}
		
		InputStream isBytes = null;
		InputStream isContext = null;
		try{
		
			// Lettura Message Context
			isContext = readContextBytes(saveDir, readResponseField, rs);
			
			// ContentType
			String columnContentType = readResponseField ? "RESPONSE_CONTENT_TYPE" : "CONTENT_TYPE";
			String contentType = rs.getString(columnContentType);
			
			// Costruzione MessageContext
			SavedMessageContext smc = buildMessageContext(contentType, isContext);
			contentType = smc.contentTypeNormalized;
			
			// Lettura Message Bytes
			isBytes = readMessageBytes(contentType, saveDir, readResponseField, rs);
				
			// CostruzioneMessaggio
			return buildMessage(smc, contentType, isBytes);
			
		}catch(Exception e){
			try{
				if( isBytes != null )
					isBytes.close();
			} catch(Exception er) {
				// close
			}
			try{
				if( isContext != null )
					isContext.close();
			} catch(Exception er) {
				// close
			}
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	private SavedMessageContext buildMessageContext(String contentType, InputStream isContext) throws UtilsException, DeserializerException {
		org.openspcoop2.message.context.utils.serializer.JaxbDeserializer jaxbDeserializer  = 
				new org.openspcoop2.message.context.utils.serializer.JaxbDeserializer();
		MessageContext msgContext = jaxbDeserializer.readMessageContext(isContext);
		
		if(msgContext.getMessageType()==null) {
			throw new UtilsException("Message Type undefined in context serialized");
		}
		MessageType mt = MessageType.valueOf(msgContext.getMessageType());
		if(mt==null) {
			throw new UtilsException("MessageType ["+msgContext.getMessageType()+"] unknown");
		}
		
		if(msgContext.getMessageRole()==null) {
			throw new UtilsException("Message Role undefined in context serialized");
		}
		MessageRole mr = MessageRole.valueOf(msgContext.getMessageRole());
		if(mr==null) {
			throw new UtilsException("MessageRole ["+msgContext.getMessageRole()+"] unknown");
		}
		
		if(MessageType.BINARY.equals(mt) &&
				REST_CONTENT_TYPE_EMPTY.equals(contentType)) {
			contentType = null;
		}
		
		SavedMessageContext smc = new SavedMessageContext();
		smc.msgContext = msgContext;
		smc.mt = mt;
		smc.mr = mr;
		smc.contentTypeNormalized = contentType;
		return smc;
	}
	
	private OpenSPCoop2Message buildMessage(SavedMessageContext smc, String contentType, InputStream isBytes) throws MessageException {
		
		OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		NotifierInputStreamParams notifierInputStreamParams = null; // Non dovrebbe servire, un eventuale handler attaccato, dovrebbe gia aver ricevuto tutto il contenuto una volta serializzato il messaggio su database.
		OpenSPCoop2MessageParseResult pr = null;
		pr = mf.createMessage(smc.mt,smc.mr,contentType,
				isBytes,notifierInputStreamParams,
				this.openspcoopProperties.getAttachmentsProcessingMode());
		try {
			OpenSPCoop2Message msg = pr.getMessage_throwParseException();
			msg.readResourcesFrom(smc.msgContext);
			return msg;
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}
	}

	private void fillTransactionContext(Context context, InputStream is) throws UtilsException {
		SerializedContext serializedContext = null;
		try {
			org.openspcoop2.message.context.utils.serializer.JaxbDeserializer deserializer = 
					new org.openspcoop2.message.context.utils.serializer.JaxbDeserializer();
			serializedContext = deserializer.readSerializedContext(is);
			
			if(serializedContext!=null && serializedContext.sizePropertyList()>0) {
				JavaDeserializer jDeserializer = new JavaDeserializer();
				for (SerializedParameter p : serializedContext.getPropertyList()) {
					Object o = jDeserializer.readObject(new ByteArrayInputStream(p.getBase()), Class.forName(p.getClasse()));
					context.addObject(org.openspcoop2.utils.Map.newMapKey(p.getNome()), o);
				}
			}
			
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
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

			deleteStateful(connectionDB, data);
			
		}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			// NOP
		}else{
			throw new UtilsException("Metodo invocato con IState non valido");
		}
	}
	
	private void deleteStateful(Connection connectionDB, java.sql.Timestamp data) throws UtilsException {
		PreparedStatement pstmt = null;
		try{
			// Eliminazione da FileSystem
			if(this.saveOnFS){
				deleteFileSystem();
			}

			//	Eliminazione from DB.
			String sql = buildDeleteStatement(data);
			pstmt= connectionDB.prepareStatement(sql);
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
			} catch(Exception er) {
				// close
			}
			String errorMsg = "SOAP_MESSAGE, delete: "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
			logError(errorMsg,e);
			throw new UtilsException(errorMsg,e);
		}
	}
	
	private String buildDeleteStatement(java.sql.Timestamp data) {
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
		return query.toString();
	}
	
	/**
	 * Elimina un messaggio completamente filesystem
	 * 
	 * 
	 */
	public void deleteMessageFromFileSystem() {


		try{
			// Eliminazione da FileSystem
			if(this.saveOnFS){
				
				deleteFileSystem();
				
			}

		}catch(Exception e){
			String errorMsg = "SOAP_MESSAGE, deleteMessageFromFileSystem: "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
			this.getLog().error(errorMsg,e);
		}
	}
	
	private void deleteFileSystem() throws UtilsException {
		
		String saveDir = getBaseDir();
		checkInizializzazioneWorkingDir(saveDir);
		
		String pathBytes = saveDir + this.keyMsgBytes;
		File fileDeleteBytes = new File(pathBytes);
		if(fileDeleteBytes.exists()){
			deleteFileIgnoreException(fileDeleteBytes);
		}	
						
		String pathContext = saveDir + this.keyMsgContext;
		File fileDeleteContext = new File(pathContext);
		if(fileDeleteContext.exists()){
			deleteFileIgnoreException(fileDeleteContext);
		}
		
		String pathResponseBytes = saveDir + this.keyMsgResponseBytes;
		File fileDeleteResponseBytes = new File(pathResponseBytes);
		if(fileDeleteResponseBytes.exists()){
			deleteFileIgnoreException(fileDeleteResponseBytes);
		}
		
		String pathResponseContext = saveDir + this.keyMsgResponseContext;
		File fileDeleteResponseContext = new File(pathResponseContext);
		if(fileDeleteResponseContext.exists()){
			deleteFileIgnoreException(fileDeleteResponseContext);
		}
		
		String pathTransactionContext = saveDir + this.keyMsgTransactionContext;
		File fileDeleteTransactionContext = new File(pathTransactionContext);
		if(fileDeleteTransactionContext.exists()){
			deleteFileIgnoreException(fileDeleteTransactionContext);
		}
		
	}

	
	private void deleteFileIgnoreException(File file) {
		try {
			Files.delete(file.toPath());
		}catch(Exception e) {
			// ignore
		}
	}
}

class SavedMessageContext {
	
	MessageContext msgContext;
	MessageType mt;
	MessageRole mr;
	String contentTypeNormalized;
	
}



