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

package org.openspcoop2.pdd.core.connettori;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.input.CountingInputStream;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_soap_impl;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.core.CostantiPdD;
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
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.LimitedInputStream;
import org.openspcoop2.utils.TimeoutInputStream;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;

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
	
	/** MessageType Risposta */
	protected MessageType messageTypeResponse = null;
	
	/** ContentType Risposta */
	protected String tipoRisposta = null;
	
	/** Check ContentType */
	protected boolean checkContentType = true;
	
	/** NotifierInputStreamParams */
	protected NotifierInputStreamParams notifierInputStreamParams;
	
	/** Imbustamento SOAP */
	protected boolean imbustamentoConAttachment;
	protected String mimeTypeAttachment;
	
	/** acceptOnlyReturnCode_202_200 SOAP */
	protected boolean acceptOnlyReturnCode_202_200 = true;
			
	protected void normalizeInputStreamResponse(int timeout, boolean configurazioneGlobale) throws Exception{
		//Se non e' null, controllo che non sia vuoto.
		if(this.isResponse!=null){
			this.isResponse = Utilities.normalizeStream(this.isResponse, false);
		}
		else{
			this.logger.info("Stream di risposta (return-code:"+this.codice+") is null",true);
		}
		
		if(this.isResponse!=null && this.useLimitedInputStream) {
			if(this.limitBytes!=null && this.limitBytes.getSogliaKb()>0) {
				LimitExceededNotifier notifier = new LimitExceededNotifier(this.getPddContext(), this.limitBytes, this.logger.getLogger());
				long limitBytes = this.limitBytes.getSogliaKb()*1024; // trasformo kb in bytes
				this.isResponse = new LimitedInputStream(this.isResponse, limitBytes, 
						CostantiPdD.PREFIX_LIMITED_RESPONSE,
						this.getPddContext(),
						notifier);
			}
		}
		if(this.isResponse!=null && this.useTimeoutInputStream) {
			if(timeout>0) {
				TimeoutNotifier notifier = getTimeoutNotifier(timeout, configurazioneGlobale, TimeoutNotifierType.RECEIVE_RESPONSE);
				this.isResponse = new TimeoutInputStream(this.isResponse, timeout, 
						CostantiPdD.PREFIX_TIMEOUT_RESPONSE,
						this.getPddContext(),
						notifier);
			}
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
		boolean saveInContext = (this.policyTimeoutConfig!=null && this.policyTimeoutConfig.getAttributeAuthority()!=null) ? false : true;
		return new TimeoutNotifier(this.getPddContext(), this.getProtocolFactory(), 
				soglia, type, this.logger.getLogger(), saveInContext);
	}
	
    protected void processReadTimeoutException(int timeout, boolean configurazioneGlobale, Exception e, String message) {
    	try {
	    	if(timeout>0 && "Read timed out".equals(message) && (e instanceof java.net.SocketTimeoutException)) {
	      		TimeoutNotifier notifier = getTimeoutNotifier(timeout, configurazioneGlobale, TimeoutNotifierType.WAIT_RESPONSE);
	    		notifier.notify(timeout);
	    	}
    	}catch(Exception error) {
    		if(this.logger!=null) {
    			this.logger.error("Errore avvenuto durante la registrazione dell'evento di read timeout: "+error.getMessage(),error);
    		}
    	}
    }
    
    protected void processConnectionTimeoutException(int timeout, boolean configurazioneGlobale, Exception e, String message) {
    	try {
	    	if(timeout>0 && "connect timed out".equals(message) && (e instanceof java.net.SocketTimeoutException)) {
	      		TimeoutNotifier notifier = getTimeoutNotifier(timeout, configurazioneGlobale, TimeoutNotifierType.CONNECTION);
	    		notifier.notify(timeout);
	    	}
    	}catch(Exception error) {
    		if(this.logger!=null) {
    			this.logger.error("Errore avvenuto durante la registrazione dell'evento di connection timeout: "+error.getMessage(),error);
    		}
    	}
    }
	
	protected void initCheckContentTypeConfiguration(){		
		this.checkContentType = true;
		if(this.idModulo!=null){
			if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
				this.checkContentType = this.openspcoopProperties.isControlloContentTypeAbilitatoRicezioneBuste();
			}else{
				this.checkContentType = this.openspcoopProperties.isControlloContentTypeAbilitatoRicezioneContenutiApplicativi();
			}
		}
	}
	
	protected void initConfigurationAcceptOnlyReturnCode_202_200(){
		this.acceptOnlyReturnCode_202_200 = true;
		if(this.isRest){
			this.acceptOnlyReturnCode_202_200 = false;
		}
		else{
			if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
				this.acceptOnlyReturnCode_202_200 = this.openspcoopProperties.isAcceptOnlyReturnCode_200_202_consegnaContenutiApplicativi();
			}
			else{
				// InoltroBuste e InoltroRisposte
				this.acceptOnlyReturnCode_202_200 = this.openspcoopProperties.isAcceptOnlyReturnCode_200_202_inoltroBuste();
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
			returnValue = _dumpResponse(trasporto);
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
	
	
	private DumpByteArrayOutputStream readResponseForDump() throws Exception{
		DumpByteArrayOutputStream bout = null;
		try {
			bout = new DumpByteArrayOutputStream(this.dumpBinario_soglia, this.dumpBinario_repositoryFile, this.idTransazione, 
					TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO.getValue());
			
			this.emitDiagnosticStartDumpBinarioRispostaIngresso();
			
//				byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
//				int readByte = 0;
//				while((readByte = this.isResponse.read(readB))!= -1){
//					bout.write(readB,0,readByte);
//				}
			//System.out.println("READ FROM ["+this.isResponse.getClass().getName()+"] ...");
			CopyStream.copy(this.isResponse, bout);
			//System.out.println("READ FROM ["+this.isResponse.getClass().getName()+"] complete");
			this.isResponse.close();
		}finally {
			try {
				if(bout!=null) {
					bout.flush();
				}
			}catch(Throwable t) {
				// ignore
			}
			try {
				if(bout!=null) {
					bout.close();
				}
			}catch(Throwable t) {
				// ignore
			}
		}
		return bout;
	}
	
	private boolean _dumpResponse(Map<String, List<String>> trasporto) throws Exception{
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
	
	private void checkRestResponseMessageType() throws Exception{
		
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
						throw new Exception("Header Content-Type non risulta definito nell'http reply e non esiste una configurazione che supporti tale casistica. Content-Type conosciuti: "+ctConosciuti);
					}
					else {
						throw new Exception("Header Content-Type definito nell'http reply non è tra quelli conosciuti: "+ctConosciuti);
					}
					
				}
			}catch(Exception e){
				exErrore = e;
				msgErrore = "Non è stato possibile comprendere come trattare il messaggio ricevuto (Content-Type: "+contentTypeString+"): "+e.getMessage();
			}
		}
		
		if(msgErrore!=null){
			if(this.checkContentType){
				//if(exErrore!=null){
				this.logger.error(msgErrore,exErrore);
				//}
				//else{
				//	this.logger.error(msgErrore);
				//}
				Exception e = new Exception(msgErrore);
				this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
				this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION,
						ParseExceptionUtils.buildParseException(e, MessageRole.RESPONSE));
				throw e;
			}else{
				msgErrore = msgErrore+"; viene utilizzata forzatamente la tipologia "+MessageType.BINARY.name() +" come modalità di gestione del messaggio";
				//if(exErrore!=null){
				this.logger.warn(msgErrore,exErrore);
				//}
				//else{
				//	this.logger.warn(msgErrore);
				//}
				this.messageTypeResponse = MessageType.BINARY;
			}
		}
	}
	
	protected boolean doRestResponse() throws Exception{
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
		
		TransportResponseContext responseContext = new TransportResponseContext(this.logger.getLogger());
		responseContext.setCodiceTrasporto(this.codice+"");
		responseContext.setContentLength(this.contentLength);
		responseContext.setHeaders(this.propertiesTrasportoRisposta);
		
		if(isParam!=null) {
			this.emitDiagnosticResponseRead(isParam);
		}
		
		OpenSPCoop2MessageParseResult pr = org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(this.logger.getLogger(),this.requestMsg, this.requestInfo,MessageRole.RESPONSE).
				createMessage(this.messageTypeResponse,responseContext,
						isParam,this.notifierInputStreamParams,
						this.openspcoopProperties.getAttachmentsProcessingMode());	
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
			
			if( premature == false ){
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
	
	private void checkSoapResponseMessageType() throws Exception{
		
		if(this.messageTypeResponse!=null) {
			return; // gia' calcolato
		}
		
		this.contentTypeMessaggioOriginale_tunnelSoap = this.tipoRisposta; // serve per funzionalità TunnelSOAP
		
		if(this.isResponse!=null){
			
			if(this.sbustamentoSoap==false){
				
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
								throw new Exception("BindingConfig is null");
							}
							if(this.requestInfo.getBindingConfig()==null) {
								throw new Exception("BindingConfig is null");
							}
							if(this.requestMsg==null) {
								throw new Exception("RequestMsg is null");
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
								throw new Exception("Header Content-Type non risulta definito nell'http reply e non esiste una configurazione che supporti tale casistica. Content-Type conosciuti: "+ctConosciuti);
							}
							else {
								throw new Exception("Header Content-Type definito nell'http reply non è tra quelli conosciuti: "+ctConosciuti);
							}
						}
						else{
							if(this.requestMsg.getMessageType().equals(this.messageTypeResponse)==false){
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
						Exception e = null;
						if(exErrore!=null){
							this.logger.error(msgErrore,exErrore);
							e = new Exception(msgErrore, exErrore);
						}
						else{
							this.logger.error(msgErrore);
							e = new Exception(msgErrore);
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
	
	private String contentTypeMessaggioOriginale_tunnelSoap = null; // serve per funzionalità TunnelSOAP;
	
	protected boolean doSoapResponse() throws Exception{

		String tipoLetturaRisposta = null;
		
		// gestione ordinaria via WS/SOAP
		
		if(this.debug)
			this.logger.debug("gestione WS/SOAP in corso ...");
		
		checkSoapResponseMessageType();
		
		if(this.isResponse!=null){
			
			TransportResponseContext responseContext = new TransportResponseContext(this.logger.getLogger());
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
							
							if( premature == false ){
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
					
						CountingInputStream cis = null;
						try{
							cis = new CountingInputStream(isParam);
							
							if(this.imbustamentoConAttachment){
								if(this.debug)
									this.logger.debug("Imbustamento con attachments...");
								
								// Imbustamento per Tunnel OpenSPCoop
								tipoLetturaRisposta = "Costruzione messaggio SOAP per Tunnel con mimeType "+this.mimeTypeAttachment;
								this.responseMsg = TunnelSoapUtils.imbustamentoMessaggioConAttachment(org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(this.logger.getLogger(),this.requestMsg, this.requestInfo,MessageRole.RESPONSE),
										this.messageTypeResponse,MessageRole.RESPONSE, 
										cis,this.mimeTypeAttachment,
										MailcapActivationReader.existsDataContentHandler(this.mimeTypeAttachment),this.contentTypeMessaggioOriginale_tunnelSoap, 
										this.openspcoopProperties.getHeaderSoapActorIntegrazione());
							}else{
								if(this.debug)
									this.logger.debug("Imbustamento messaggio...");
								tipoLetturaRisposta = "Imbustamento messaggio in un messaggio SOAP";
								
								// Per ottenere il corretto content length in questo caso devo leggere tutto il messaggio
								// ALtrimenti dopo effettuando la close, nel caso di saaj instreaming otterrei un errore (o il msg non viene cmq costruito)
								byte[] msg = Utilities.getAsByteArray(cis);
								if(msg==null || msg.length<=0){
									throw new Exception("Contenuto messaggio da imbustare non presente");
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
								this.responseMsg.updateIncomingMessageContentLength(cis.getByteCount());
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
					
					if( premature == false ){
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
				if(this.responseMsg!=null){
					// save changes.
					// N.B. il countAttachments serve per il msg con attachments come saveMessage!
					if(this.responseMsg.castAsSoap().hasAttachments()) {
						if(this.responseMsg.castAsSoap().countAttachments()==0){
							this.responseMsg.castAsSoap().getSOAPPart();
						}
					}
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
