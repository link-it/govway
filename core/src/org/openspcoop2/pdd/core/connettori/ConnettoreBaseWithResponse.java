/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.connettori;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.input.BoundedInputStream;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_soap_impl;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.controllo_traffico.DimensioneMessaggiUtils;
import org.openspcoop2.pdd.core.controllo_traffico.LimitExceededNotifier;
import org.openspcoop2.pdd.core.controllo_traffico.ReadTimeoutConfigurationUtils;
import org.openspcoop2.pdd.core.controllo_traffico.ReadTimeoutContextParam;
import org.openspcoop2.pdd.core.controllo_traffico.SogliaReadTimeout;
import org.openspcoop2.pdd.core.controllo_traffico.TimeoutNotifier;
import org.openspcoop2.pdd.core.controllo_traffico.TimeoutNotifierType;
import org.openspcoop2.pdd.logger.DiagnosticInputStream;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.dump.DumpException;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.LimitedInputStream;
import org.openspcoop2.utils.TimeoutInputStream;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * ConnettoreBaseWithResponse
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class ConnettoreBaseWithResponse extends ConnettoreBase {

	/** InputStream Risposta */
	protected InputStream isResponse = null;
	public InputStream getIsResponse() {
		return this.isResponse;
	}
	public void setInputStreamResponse(InputStream isResponse) {
		this.isResponse = isResponse;
	}

	/** MessageType Risposta */
	protected MessageType messageTypeResponse = null;
	
	/** ContentType Risposta */
	protected String tipoRisposta = null;
	public void setTipoRisposta(String tipoRisposta) {
		this.tipoRisposta = tipoRisposta;
	}

	/** Check ContentType */
	protected boolean checkContentType = true;
	
	/** NotifierInputStreamParams */
	protected NotifierInputStreamParams notifierInputStreamParams;
	public void setNotifierInputStreamParams(NotifierInputStreamParams notifierInputStreamParams) {
		this.notifierInputStreamParams = notifierInputStreamParams;
	}

	/** Imbustamento SOAP */
	protected boolean imbustamentoConAttachment;
	protected String mimeTypeAttachment;
	public void setImbustamentoConAttachment(boolean imbustamentoConAttachment) {
		this.imbustamentoConAttachment = imbustamentoConAttachment;
	}
	public void setMimeTypeAttachment(String mimeTypeAttachment) {
		this.mimeTypeAttachment = mimeTypeAttachment;
	}
	
	/** acceptOnlyReturnCode_202_200 SOAP */
	protected boolean acceptOnlyReturnCode202or200 = true;
	public boolean isAcceptOnlyReturnCode202or200() {
		return this.acceptOnlyReturnCode202or200;
	}
			
	public void normalizeInputStreamResponse(int timeout, boolean configurazioneGlobale) throws UtilsException, IOException, DriverConfigurazioneException, ProtocolException {
		//Se non e' null, controllo che non sia vuoto.
		if(this.isResponse!=null){
			this.isResponse = Utilities.normalizeStream(this.isResponse, false);
		}
		else{
			this.logger.info("Stream di risposta (return-code:"+this.codice+") is null",true);
		}
		
		if(this.isResponse!=null && this.useLimitedInputStream &&
			this.limitBytes!=null && this.limitBytes.getSogliaKb()>0) {
			LimitExceededNotifier notifier = new LimitExceededNotifier(this.getPddContext(), this.limitBytes, this.logger.getLogger());
			
			if(this.limitBytes.isUseContentLengthHeader()) {
				List<String> l = TransportUtils.getValues(this.propertiesTrasportoRisposta, HttpConstants.CONTENT_LENGTH);
				if(l!=null && !l.isEmpty()) {
					DimensioneMessaggiUtils.verifyByContentLength(this.logger.getLogger(), l, this.limitBytes, notifier, this.getPddContext(), DimensioneMessaggiUtils.RESPONSE);
				}
			}
			
			long limitBytes = this.limitBytes.getSogliaKb()*1024; // trasformo kb in bytes
			this.isResponse = new LimitedInputStream(this.isResponse, limitBytes, 
					CostantiPdD.PREFIX_LIMITED_RESPONSE,
					this.getPddContext(),
					notifier);
		}
		if(this.isResponse!=null && this.useTimeoutInputStream &&
			timeout>0) {
			TimeoutNotifier notifier = getTimeoutNotifier(timeout, configurazioneGlobale, TimeoutNotifierType.RECEIVE_RESPONSE);
			this.isResponse = new TimeoutInputStream(this.isResponse, timeout, 
					CostantiPdD.PREFIX_TIMEOUT_RESPONSE,
					this.getPddContext(),
					notifier);
		}
		if(this.isResponse!=null && this.useDiagnosticInputStream && this.msgDiagnostico!=null) {
			String idModuloFunzionale = 
					ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo) ? 
							MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI : MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE;
			this.isResponse = new DiagnosticInputStream(this.isResponse, idModuloFunzionale, "letturaPayloadRisposta", false, this.msgDiagnostico, 
					(this.logger!=null && this.logger.getLogger()!=null) ? this.logger.getLogger() : OpenSPCoop2Logger.getLoggerOpenSPCoopCore(),
					this.getPddContext());
		}
	}
	
	protected TimeoutNotifier getTimeoutNotifier(int timeout, boolean configurazioneGlobale, TimeoutNotifierType type) throws DriverConfigurazioneException, ProtocolException {
		SogliaReadTimeout soglia = null;
		if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo) || this.pa!=null) {
			soglia = (this.pa!=null) ? 
					ReadTimeoutConfigurationUtils.buildSogliaResponseTimeout(timeout, configurazioneGlobale, this.pa, this.nomeConnettoreAsincrono, this.policyTimeoutConfig,
							new ReadTimeoutContextParam(this.requestInfo, this.getProtocolFactory(), this.getPddContext(), this.state)) : 
						ReadTimeoutConfigurationUtils.buildSogliaResponseTimeout(timeout, false, this.getProtocolFactory());
		}
		else {
			soglia = (this.pd!=null) ? 
					ReadTimeoutConfigurationUtils.buildSogliaResponseTimeout(timeout, configurazioneGlobale, this.pd, this.policyTimeoutConfig,
							new ReadTimeoutContextParam(this.requestInfo, this.getProtocolFactory(), this.getPddContext(), this.state)) : 
						ReadTimeoutConfigurationUtils.buildSogliaResponseTimeout(timeout, true, this.getProtocolFactory());
		}
		boolean saveInContext = !(this.policyTimeoutConfig!=null && 
									(this.policyTimeoutConfig.getAttributeAuthority()!=null || this.policyTimeoutConfig.getAttributeAuthorityResponseJwt()!=null)
								);
		return new TimeoutNotifier(this.getPddContext(), this.getProtocolFactory(), 
				soglia, type, this.logger.getLogger(), saveInContext);
	}
	
	public static boolean isReadTimeoutException(Exception e, String message){
		return (e instanceof java.net.SocketTimeoutException)
				&&
				(
						("Read timed out".equals(message))
						||
						(message.contains(" MILLISECONDS") && !(e instanceof org.apache.hc.client5.http.ConnectTimeoutException)) // httpclient5 nio usa una eccezione specifica solo per il connect timeout
				);
	}
	public static boolean containsReadTimeoutException(Exception e, String message){
		return (message!=null)
				&&
				(
						(message.contains("Read timed out"))
						||
						(message.contains(" MILLISECONDS") && !(e instanceof org.apache.hc.client5.http.ConnectTimeoutException) && !(Utilities.existsInnerException(e, org.apache.hc.client5.http.ConnectTimeoutException.class))) // httpclient5 nio usa una eccezione specifica solo per il connect timeout
				)
				&&
				(e instanceof java.net.SocketTimeoutException || Utilities.existsInnerException(e, java.net.SocketTimeoutException.class));		
	}
	public boolean processReadTimeoutException(int timeout, boolean configurazioneGlobale, Exception e, String message) {
		boolean isException = false;
    	try {
	    	if(timeout>0) {
	    		isException = isReadTimeoutException(e, message);
	    		if(isException) {
		      		TimeoutNotifier notifier = getTimeoutNotifier(timeout, configurazioneGlobale, TimeoutNotifierType.WAIT_RESPONSE);
		    		notifier.notify(timeout);
	    		}
	    	}
    	}catch(Exception error) {
    		if(this.logger!=null) {
    			this.logger.error("Errore avvenuto durante la registrazione dell'evento di read timeout: "+error.getMessage(),error);
    		}
    	}
    	return isException;
    }
    
    public static boolean isConnectionTimeoutException(Exception e, String message){
    	/**return (
    				"connect timed out".equals(message) // http url connection
    				||
    				(message!=null && message.contains("Connect timed out")) // httpcore5
    				||
    				(e instanceof org.apache.hc.client5.http.ConnectTimeoutException || Utilities.existsInnerException(e, org.apache.hc.client5.http.ConnectTimeoutException.class)) // httpclient5 nio, l'eccezione estende comunque java.net.SocketTimeoutException
    			)
    			&&
    			(e instanceof java.net.SocketTimeoutException);*/
    	// Allineo ad unico metodo
    	return containsConnectionTimeoutException(e, message);
	}
    public static boolean containsConnectionTimeoutException(Exception e, String message){
    	/**System.out.println("CONDIZIONE 1 ["+message.contains("connect timed out")+"]");
    	System.out.println("CONDIZIONE 2 ["+message.contains("Connect timed out")+"]");
    	System.out.println("CONDIZIONE 3 ["+(e instanceof org.apache.hc.client5.http.ConnectTimeoutException || Utilities.existsInnerException(e, org.apache.hc.client5.http.ConnectTimeoutException.class))+"]");
    	System.out.println("CONDIZIONE 4 ["+(e instanceof java.net.SocketTimeoutException )+"]");
    	System.out.println("CONDIZIONE 5 ["+(Utilities.existsInnerException(e, java.net.SocketTimeoutException.class))+"]");
    	System.out.println("CONDIZIONE 6 ["+(Utilities.existsInnerInstanceException(e, java.net.SocketTimeoutException.class))+"]");*/
		return message!=null && 
				( 
						message.contains("connect timed out") // http url connection
						||
						message.contains("Connect timed out") // httpcore5
	    				||
	    				(e instanceof org.apache.hc.client5.http.ConnectTimeoutException || Utilities.existsInnerException(e, org.apache.hc.client5.http.ConnectTimeoutException.class)) // httpclient5 nio, l'eccezione estende comunque java.net.SocketTimeoutException
				)
				&& 
				(
						e instanceof java.net.SocketTimeoutException 
						|| 
						Utilities.existsInnerException(e, java.net.SocketTimeoutException.class)
						|| 
						Utilities.existsInnerInstanceException(e, java.net.SocketTimeoutException.class) // per org.apache.hc.client5.http.ConnectTimeoutException
				);
	}
    public boolean processConnectionTimeoutException(int timeout, boolean configurazioneGlobale, Exception e, String message) {
    	boolean isException = false;
    	try {
    		if(timeout>0) {
    			isException = isConnectionTimeoutException(e, message); 
    			if(isException) {
    				TimeoutNotifier notifier = getTimeoutNotifier(timeout, configurazioneGlobale, TimeoutNotifierType.CONNECTION);
    				notifier.notify(timeout);
    			}
	    	}
    	}catch(Exception error) {
    		if(this.logger!=null) {
    			this.logger.error("Errore avvenuto durante la registrazione dell'evento di connection timeout: "+error.getMessage(),error);
    		}
    	}
    	return isException;
    }
	
	public void initCheckContentTypeConfiguration(){		
		this.checkContentType = true;
		if(this.idModulo!=null){
			if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
				this.checkContentType = this.openspcoopProperties.isControlloContentTypeAbilitatoRicezioneBuste();
			}else{
				this.checkContentType = this.openspcoopProperties.isControlloContentTypeAbilitatoRicezioneContenutiApplicativi();
			}
		}
	}
	
	public void initConfigurationAcceptOnlyReturnCode202or200(){
		this.acceptOnlyReturnCode202or200 = true;
		if(this.isRest){
			this.acceptOnlyReturnCode202or200 = false;
		}
		else{
			if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
				this.acceptOnlyReturnCode202or200 = this.openspcoopProperties.isAcceptOnlyReturnCode_200_202_consegnaContenutiApplicativi();
			}
			else{
				// InoltroBuste e InoltroRisposte
				this.acceptOnlyReturnCode202or200 = this.openspcoopProperties.isAcceptOnlyReturnCode_200_202_inoltroBuste();
			}
		}
	}
	
	protected boolean dumpResponse(Map<String, List<String>> trasporto) throws Exception{
		
		Exception exceptionCheck = null;
		try {
			if(this.isRest){
				checkRestResponseMessageType();
			}
			else {
				checkSoapResponseMessageType();
			}
		}catch(Exception e) {
			exceptionCheck = e;
		}
		
		boolean returnValue = false;
		try {
			returnValue = internalDumpResponse(trasporto);
		}
		catch(Exception e) {
			if(exceptionCheck!=null) {
				throw exceptionCheck;
			}
			else {
				throw e;
			}
		}
		
		if(exceptionCheck!=null) {
			throw exceptionCheck;
		}
		else {
			return returnValue;
		}

	}
	
	
	private DumpByteArrayOutputStream readResponseForDump() throws UtilsException, IOException {
		DumpByteArrayOutputStream bout = null;
		try {
			bout = new DumpByteArrayOutputStream(this.dumpBinarioSoglia, this.dumpBinarioRepositoryFile, this.idTransazione, 
					TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO.getValue());
			
			this.emitDiagnosticStartDumpBinarioRispostaIngresso();
			
			/**	byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
				int readByte = 0;
				while((readByte = this.isResponse.read(readB))!= -1){
					bout.write(readB,0,readByte);
				}*/
			/**System.out.println("READ FROM ["+this.isResponse.getClass().getName()+"] ...");*/
			CopyStream.copy(this.isResponse, bout);
			/**System.out.println("READ FROM ["+this.isResponse.getClass().getName()+"] complete");*/
			this.isResponse.close();
		}finally {
			try {
				if(bout!=null) {
					bout.flush();
				}
			}catch(Exception t) {
				// ignore
			}
			try {
				if(bout!=null) {
					bout.close();
				}
			}catch(Exception t) {
				// ignore
			}
		}
		return bout;
	}
	
	private boolean internalDumpResponse(Map<String, List<String>> trasporto) throws UtilsException, IOException, DumpException {
		if(this.isResponse!=null){
			
			this.emitDiagnosticResponseRead(this.isResponse);
			
			// Registro Debug.
			DumpByteArrayOutputStream bout = null;
			try {
				bout = readResponseForDump();
				if(this.debug) {
					this.logger.info("Messaggio ricevuto (ContentType:"+this.tipoRisposta+") :\n"+bout.toString(),false);
				}
				// Creo nuovo inputStream
				if(bout.isSerializedOnFileSystem()) {
					this.isResponse = new FileInputStream(bout.getSerializedFile());
				}
				else {
					this.isResponse = new ByteArrayInputStream(bout.toByteArray());
				}
				
				this.dumpBinarioRispostaIngresso(bout, this.messageTypeResponse, trasporto);
			}finally {
				try {
					if(bout!=null) {
						bout.clearResources();
					}
				}catch(Throwable t) {
					this.logger.error("Release resources failed: "+t.getMessage(),t);
				}
			}
		}
		else {
			if(this.debug) {
				if(this.tipoRisposta!=null) {
					this.logger.info("Messaggio ricevuto (ContentType:"+this.tipoRisposta+") senza contenuto nell'http-reply",false);
				}
				else {
					this.logger.info("Messaggio ricevuto senza contenuto nell'http-reply",false);
				}
			}
			
			// devo registrare almeno gli header HTTP
			this.emitDiagnosticStartDumpBinarioRispostaIngresso();
			this.dumpBinarioRispostaIngresso(null, null, trasporto);
		}
		
		return true;
	}
	
	private void checkRestResponseMessageType() throws ConnettoreException{
		
		if(this.messageTypeResponse!=null) {
			return; // gia' calcolato
		}
		
		String msgErrore = null;
		Exception exErrore = null;
				
		String contentTypeString = "N.D.";
		if(this.tipoRisposta!=null && !"".equals(this.tipoRisposta)){
			contentTypeString = this.tipoRisposta;
			
			// Verifico correttezza Content-Type
			try {
				ContentTypeUtilities.validateContentType(contentTypeString);
			}catch(Exception error){
				exErrore = error;
				msgErrore = "Content-Type '"+contentTypeString+"' presente nella risposta non valido: "+error.getMessage();
			}
		}
		
		if(msgErrore==null) {
			try{
				this.messageTypeResponse = this.requestInfo.getBindingConfig().getResponseMessageType(this.requestMsg.getServiceBinding(), 
						this.requestMsg.getTransportRequestContext(),
						this.tipoRisposta, 
						this.codice>0?this.codice:null);				
				if(this.messageTypeResponse==null){
					String ctConosciuti = this.requestInfo.getBindingConfig().getContentTypesSupportedAsString(this.requestMsg.getServiceBinding(), MessageRole.RESPONSE, 
							this.requestMsg.getTransportRequestContext());
					if(this.tipoRisposta==null){
						throw new ConnettoreException("Header Content-Type non risulta definito nell'http reply e non esiste una configurazione che supporti tale casistica. Content-Type conosciuti: "+ctConosciuti);
					}
					else {
						throw new ConnettoreException("Header Content-Type definito nell'http reply non è tra quelli conosciuti: "+ctConosciuti);
					}
					
				}
			}catch(Exception e){
				exErrore = e;
				msgErrore = "Non è stato possibile comprendere come trattare il messaggio ricevuto (Content-Type: "+contentTypeString+"): "+e.getMessage();
			}
		}
		
		if(msgErrore!=null){
			if(this.checkContentType){
				/**if(exErrore!=null){*/
				this.logger.error(msgErrore,exErrore);
				/**}
				//else{
				//	this.logger.error(msgErrore);
				//}*/
				ConnettoreException e = new ConnettoreException(msgErrore);
				this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
				this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION,
						ParseExceptionUtils.buildParseException(e, MessageRole.RESPONSE));
				throw e;
			}else{
				msgErrore = msgErrore+"; viene utilizzata forzatamente la tipologia "+MessageType.BINARY.name() +" come modalità di gestione del messaggio";
				/**if(exErrore!=null){*/
				this.logger.warn(msgErrore,exErrore);
				/**}
				//else{
				//	this.logger.warn(msgErrore);
				//}*/
				this.messageTypeResponse = MessageType.BINARY;
			}
		}
	}
	
	protected boolean doRestResponse() throws ConnettoreException{
		if(this.debug)
			this.logger.debug("gestione REST in corso ...");
		
		checkRestResponseMessageType();
		
		InputStream isParam = null;
		if(this.contentLength>=0){
			isParam = this.isResponse;
			if(this.contentLength==0){
				isParam = null;
			}
		}
		else{
			//non ho trovato ContentLength. Devo scoprire se c'e' un payload.
			isParam = this.isResponse; // è stato normalizzato, quindi se non c'è un contenuto è null
		}
		
		TransportResponseContext responseContext = null;
		try {
			responseContext = new TransportResponseContext(this.logger.getLogger());
		}catch(Exception e) {
			throw new ConnettoreException(e.getMessage(),e);
		}
		responseContext.setCodiceTrasporto(this.codice+"");
		responseContext.setContentLength(this.contentLength);
		responseContext.setHeaders(this.propertiesTrasportoRisposta);
		
		if(isParam!=null) {
			this.emitDiagnosticResponseRead(isParam);
		}
		
		OpenSPCoop2MessageParseResult pr = null;
		try {
			pr = org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(this.logger.getLogger(),this.requestMsg, this.requestInfo,MessageRole.RESPONSE).
					createMessage(this.messageTypeResponse,responseContext,
							isParam,this.notifierInputStreamParams,
							this.openspcoopProperties.getAttachmentsProcessingMode());	
		}catch(Exception e) {
			throw new ConnettoreException(e.getMessage(),e);
		}
		if(pr.getParseException()!=null){
			this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
		}
		try{
			this.responseMsg = pr.getMessage_throwParseException();
		}catch(Exception e){
			this.responseMsg=null;
			// L'errore 'premature end of file' consiste solo nel fatto che una risposta non e' stata ricevuta.
			boolean result2XX = (this.codice>=200 && this.codice<=299);
			boolean premature =  Utilities.existsInnerMessageException(e, "Premature end of file", true) && result2XX;
			// Se non ho un premature, ed un errore di lettura in 200, allora devo segnalare l'errore, altrimenti comunque 
			// il msg ritornato e' null e nel codiceStato vi e' l'errore.
			
			if( !premature ){
				this.eccezioneProcessamento = e;
				this.errore = "Errore avvenuto durante il parsing della risposta: " + this.readExceptionMessageFromException(e);
				this.logger.error("Errore avvenuto durante il parsing della risposta: " + this.readExceptionMessageFromException(e),e);
				if(result2XX){
					return false;
				}
			}
		}
		
		return true;
	}
	
	private void checkSoapResponseMessageType() throws ConnettoreException, MessageNotSupportedException{
		
		if(this.messageTypeResponse!=null) {
			return; // gia' calcolato
		}
		
		this.contentTypeMessaggioOriginaleTunnelSoap = this.tipoRisposta; // serve per funzionalità TunnelSOAP
		
		if(this.isResponse!=null){
			
			if(!this.sbustamentoSoap){
				
				String msgErrore = null;
				Exception exErrore = null;
								
				String contentTypeString = "N.D.";
				if(this.tipoRisposta!=null && !"".equals(this.tipoRisposta)){
					contentTypeString = this.tipoRisposta;
					
					// Verifico correttezza Content-Type
					try {
						ContentTypeUtilities.validateContentType(contentTypeString);
					}catch(Exception error){
						exErrore = error;
						msgErrore = "Content-Type '"+contentTypeString+"' presente nella risposta non valido: "+error.getMessage();
					}
				}
				
				if(msgErrore==null) {
					try{
					
						if(this.tipoRisposta==null){
							// obbligatorio in SOAP
							msgErrore = "Header Content-Type non definito nell'http reply";
						}
						else{
							if(this.requestInfo==null) {
								throw new ConnettoreException("BindingConfig is null");
							}
							if(this.requestInfo.getBindingConfig()==null) {
								throw new ConnettoreException("BindingConfig is null");
							}
							if(this.requestMsg==null) {
								throw new ConnettoreException("RequestMsg is null");
							}
							this.messageTypeResponse = this.requestInfo.getBindingConfig().getResponseMessageType(this.requestMsg.getServiceBinding(), 
									this.requestMsg.getTransportRequestContext(),
									this.tipoRisposta, 
									this.codice>0?this.codice:null);
						}	
					
						if(this.messageTypeResponse==null){
							
							String ctConosciuti = this.requestInfo.getBindingConfig().getContentTypesSupportedAsString(this.requestMsg.getServiceBinding(), MessageRole.RESPONSE, 
									this.requestMsg.getTransportRequestContext());
							
							if(this.tipoRisposta==null){
								throw new ConnettoreException("Header Content-Type non risulta definito nell'http reply e non esiste una configurazione che supporti tale casistica. Content-Type conosciuti: "+ctConosciuti);
							}
							else {
								throw new ConnettoreException("Header Content-Type definito nell'http reply non è tra quelli conosciuti: "+ctConosciuti);
							}
						}
						else{
							if(!this.requestMsg.getMessageType().equals(this.messageTypeResponse)){
								msgErrore = "Header Content-Type definito nell'http reply associato ad un tipo ("+this.messageTypeResponse.name()
											+") differente da quello associato al messaggio di richiesta ("+this.requestMsg.getMessageType().name()+")";
							}
						}
					}catch(Exception e){
						exErrore = e;
						msgErrore = "Non è stato possibile comprendere come trattare il messaggio ricevuto (Content-Type: "+contentTypeString+"): "+e.getMessage();
					}
				}
				
				if(msgErrore!=null){
					if(this.checkContentType){
						ConnettoreException e = null;
						if(exErrore!=null){
							this.logger.error(msgErrore,exErrore);
							e = new ConnettoreException(msgErrore, exErrore);
						}
						else{
							this.logger.error(msgErrore);
							e = new ConnettoreException(msgErrore);
						}
						this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
						this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION,
								ParseExceptionUtils.buildParseException(e, MessageRole.RESPONSE));
						throw e;
					}else{
						this.messageTypeResponse = MessageType.SOAP_11;
						this.tipoRisposta = SoapUtils.getSoapContentTypeForMessageWithoutAttachments(this.messageTypeResponse);
						msgErrore = msgErrore+"; per trattare il messaggio viene utilizzato forzatamente il content-type "+this.tipoRisposta+" e la tipologia "+MessageType.SOAP_11.name();
						if(exErrore!=null){
							this.logger.warn(msgErrore,exErrore);
						}
						else{
							this.logger.warn(msgErrore);
						}						
					}
				}
			}
			else{
				this.messageTypeResponse = this.requestMsg.getMessageType();
				this.tipoRisposta = SoapUtils.getSoapContentTypeForMessageWithoutAttachments(this.messageTypeResponse);
			}
		}
	}
	
	private String contentTypeMessaggioOriginaleTunnelSoap = null; /** serve per funzionalità TunnelSOAP; */
	
	protected boolean doSoapResponse() throws ConnettoreException{

		String tipoLetturaRisposta = null;
		
		// gestione ordinaria via WS/SOAP
		
		if(this.debug)
			this.logger.debug("gestione WS/SOAP in corso ...");
		
		try {
			checkSoapResponseMessageType();
		}catch(Exception e) {
			throw new ConnettoreException(e.getMessage(),e);
		}
		
		if(this.isResponse!=null){
			
			TransportResponseContext responseContext = null;
			try {
				responseContext = new TransportResponseContext(this.logger.getLogger());
			}catch(Exception e) {
				throw new ConnettoreException(e.getMessage(),e);
			}
			responseContext.setCodiceTrasporto(this.codice+"");
			responseContext.setContentLength(this.contentLength);
			responseContext.setHeaders(this.propertiesTrasportoRisposta);
			
			this.emitDiagnosticResponseRead(this.isResponse);
			
			try{
				
				if(this.sbustamentoSoap==false){
					if(this.debug)
						this.logger.debug("Ricostruzione normale...");
					
					// Ricostruzione messaggio soap: secondo parametro a false, indica che il messaggio e' gia un SOAPMessage
					tipoLetturaRisposta = "Parsing Risposta SOAP";
						
					if(this.contentLength>0){
						OpenSPCoop2MessageParseResult pr = org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(this.logger.getLogger(),this.requestMsg, this.requestInfo,MessageRole.RESPONSE).
								createMessage(this.messageTypeResponse,responseContext,
										this.isResponse,this.notifierInputStreamParams,
										this.openspcoopProperties.getAttachmentsProcessingMode());	
						if(pr.getParseException()!=null){
							this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
						}
						this.responseMsg = pr.getMessage_throwParseException();
					}
					else if(this.contentLength==0){
						this.responseMsg = null;
					}
					else{
						// non ho trovato ContentLength. Devo scoprire se c'e' un payload.
						// L'inputstream è stato normalizzato, quindi se non c'è un contenuto è null
						// Devo scoprire se c'e' un payload. Costruisco il messaggio e poi provo ad accedere all'envelope
						try{
							OpenSPCoop2MessageParseResult pr = org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(this.logger.getLogger(),this.requestMsg, this.requestInfo,MessageRole.RESPONSE)
									.createMessage(this.messageTypeResponse,responseContext,
											this.isResponse,this.notifierInputStreamParams,
											this.openspcoopProperties.getAttachmentsProcessingMode());
							if(pr.getParseException()!=null){
								this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
							}
							this.responseMsg = pr.getMessage_throwParseException();
						}catch(Exception e){
							this.responseMsg=null;
							// L'errore 'premature end of file' consiste solo nel fatto che una risposta non e' stata ricevuta.
							boolean result2XX = (this.codice>=200 && this.codice<=299);
							boolean premature =  Utilities.existsInnerMessageException(e, "Premature end of file", true) && result2XX;
							// Se non ho un premature, ed un errore di lettura in 200, allora devo segnalare l'errore, altrimenti comunque 
							// il msg ritornato e' null e nel codiceStato vi e' l'errore.
							
							if( !premature ){
								this.eccezioneProcessamento = e;
								this.errore = "Errore avvenuto durante il parsing della risposta: " + this.readExceptionMessageFromException(e);
								this.logger.error("Errore avvenuto durante il parsing della risposta: " + this.readExceptionMessageFromException(e),e);
								if(result2XX){
									return false;
								}
							}
						}
					}
					
				}else{
					InputStream isParam = null;
					if(this.contentLength>=0){
						isParam = this.isResponse;
						if(this.contentLength==0){
							isParam = null;
						}
					}
					else{
						//non ho trovato ContentLength. Devo scoprire se c'e' un payload.
						isParam = this.isResponse; // è stato normalizzato, quindi se non c'è un contenuto è null
					}
					if(isParam!=null){
					
						BoundedInputStream cis = null;
						try{
							cis = BoundedInputStream.builder().setInputStream(isParam).get();
							
							if(this.imbustamentoConAttachment){
								if(this.debug)
									this.logger.debug("Imbustamento con attachments...");
								
								// Imbustamento per Tunnel OpenSPCoop
								tipoLetturaRisposta = "Costruzione messaggio SOAP per Tunnel con mimeType "+this.mimeTypeAttachment;
								this.responseMsg = TunnelSoapUtils.imbustamentoMessaggioConAttachment(org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(this.logger.getLogger(),this.requestMsg, this.requestInfo,MessageRole.RESPONSE),
										this.messageTypeResponse,MessageRole.RESPONSE, 
										cis,this.mimeTypeAttachment,
										MailcapActivationReader.existsDataContentHandler(this.mimeTypeAttachment),this.contentTypeMessaggioOriginaleTunnelSoap, 
										this.openspcoopProperties.getHeaderSoapActorIntegrazione());
							}else{
								if(this.debug)
									this.logger.debug("Imbustamento messaggio...");
								tipoLetturaRisposta = "Imbustamento messaggio in un messaggio SOAP";
								
								// Per ottenere il corretto content length in questo caso devo leggere tutto il messaggio
								// ALtrimenti dopo effettuando la close, nel caso di saaj instreaming otterrei un errore (o il msg non viene cmq costruito)
								byte[] msg = Utilities.getAsByteArray(cis);
								if(msg==null || msg.length<=0){
									throw new ConnettoreException("Contenuto messaggio da imbustare non presente");
								}
								this.isResponse.close();
								// Creo nuovo inputStream
								this.isResponse = new ByteArrayInputStream(msg);
								
								OpenSPCoop2MessageParseResult pr = org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(this.logger.getLogger(),this.requestMsg, this.requestInfo,MessageRole.RESPONSE).
										envelopingMessage(this.messageTypeResponse, this.tipoRisposta, this.soapAction, responseContext, 
												this.isResponse, this.notifierInputStreamParams, 
												this.openspcoopProperties.getAttachmentsProcessingMode(),
												true,
												this.openspcoopProperties.useSoapMessageReader(), this.openspcoopProperties.getSoapMessageReaderBufferThresholdKb());
								if(pr.getParseException()!=null){
									this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
								}
								this.responseMsg = pr.getMessage_throwParseException();
	
							}
							
							if(this.responseMsg!=null){
								this.responseMsg.updateIncomingMessageContentLength(cis.getCount());
							}
							
						}finally{
							try{
								if(cis!=null){
									cis.close();
								}
							}catch(Exception eClose){
								// close
							}
						}
					}
					
				}
				try{
					if(this.responseMsg!=null){
						if(this.responseMsg instanceof AbstractOpenSPCoop2Message_soap_impl) {
							AbstractOpenSPCoop2Message_soap_impl<?> soap = (AbstractOpenSPCoop2Message_soap_impl<?>) this.responseMsg;
							if(!soap.hasContent()) {
								this.responseMsg = null;
							}
						}
						else {
							this.responseMsg.castAsSoap().getSOAPPart().getEnvelope();
						}
					}
				}
				catch(Exception e){
					this.responseMsg=null;
					// L'errore 'premature end of file' consiste solo nel fatto che una risposta non e' stata ricevuta.
					boolean result2XX = (this.codice>=200 && this.codice<=299);
					boolean premature =  Utilities.existsInnerMessageException(e, "Premature end of file", true) && result2XX;
					// Se non ho un premature, ed un errore di lettura in 200, allora devo segnalare l'errore, altrimenti comunque 
					// il msg ritornato e' null e nel codiceStato vi e' l'errore.
					
					if( !premature ){
						this.eccezioneProcessamento = e;
						this.errore = "Errore avvenuto durante il parsing della risposta: " + this.readExceptionMessageFromException(e);
						this.logger.error("Errore avvenuto durante il parsing della risposta: " + this.readExceptionMessageFromException(e),e);
						if(result2XX){
							return false;
						}
					}
				}
			}catch(Exception e){
				this.eccezioneProcessamento = e;
				String msgErrore = this.readExceptionMessageFromException(e);
				this.errore = "Errore avvenuto durante il processamento della risposta ("+tipoLetturaRisposta+"): " + msgErrore;
				this.logger.error("Errore avvenuto durante il processamento della risposta ("+tipoLetturaRisposta+"): " + msgErrore,e);
				return false;
			}

			

			// save Msg
			if(this.debug)
				this.logger.debug("Save messaggio...");
			try{
				if(this.responseMsg!=null &&
					// save changes.
					// N.B. il countAttachments serve per il msg con attachments come saveMessage!
					this.responseMsg.castAsSoap().hasAttachments() &&
					this.responseMsg.castAsSoap().countAttachments()==0){
					this.responseMsg.castAsSoap().getSOAPPart();
				}
			}catch(Exception e){
				this.eccezioneProcessamento = e;
				this.errore = "Errore avvenuto durante il salvataggio della risposta: " + this.readExceptionMessageFromException(e);
				this.logger.error("Errore avvenuto durante il salvataggio della risposta: " + this.readExceptionMessageFromException(e),e);
				return false;
			}

		}
		
		return true;
	}
}
