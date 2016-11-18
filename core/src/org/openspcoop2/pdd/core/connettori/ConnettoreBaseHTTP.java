/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.io.input.CountingInputStream;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * ConnettoreBaseHTTP
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */

public abstract class ConnettoreBaseHTTP extends ConnettoreBase {

	/** InputStream Risposta */
	protected InputStream isResponse = null;
	
	/** httpMethod */
	protected String httpMethod = null;
	public String getHttpMethod() {
		return this.httpMethod;
	}
	
	/** ContentType Risposta */
	protected String tipoRisposta = null;
	
	/** Check ContentType */
	protected boolean checkContentType = true;
	
	/** NotifierInputStreamParams */
	protected NotifierInputStreamParams notifierInputStreamParams;
	
	/** InputStream Risposta */
	protected String resultHTTPMessage;
	
	/** Imbustamento SOAP */
	protected boolean imbustamentoConAttachment;
	protected String mimeTypeAttachment;
	
	/** acceptOnlyReturnCode_202_200 SOAP */
	protected boolean acceptOnlyReturnCode_202_200 = true;
	
	/** SOAPAction */
	protected String soapAction = null;
	
	protected void forwardHttpRequestHeader() throws Exception{
		OpenSPCoop2MessageProperties forwardHeader = 
				this.requestMsg.getForwardTransportHeader(this.openspcoopProperties.getRESTServicesWhiteListRequestHeaderList());
		if(forwardHeader!=null && forwardHeader.size()>0){
			if(this.debug)
				this.logger.debug("Forward header di trasporto (size:"+forwardHeader.size()+") ...");
			if(this.propertiesTrasporto==null){
				this.propertiesTrasporto = new Properties();
			}
			Enumeration<?> keys = forwardHeader.getKeys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = forwardHeader.getProperty(key);
				if(this.debug)
					this.logger.debug("Forward Transport Header ["+key+"]=["+value+"]");
				this.propertiesTrasporto.put(key, value);
			}
		}
	}
	
	protected void normalizeInputStreamResponse() throws Exception{
		//Se non e' null, controllo che non sia vuoto.
		byte[] b = new byte[1];
		if(this.isResponse!=null){
			if(this.isResponse.read(b) == -1) {
				this.isResponse = null;
			} else {
				// Metodo alternativo: java.io.PushbackInputStream
				this.isResponse = new SequenceInputStream(new ByteArrayInputStream(b),this.isResponse);
			}
		}
		else{
			this.logger.info("Stream di risposta (return-code:"+this.codice+") is null",true);
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
	
	protected void dumpResponse() throws Exception{
		if(this.isResponse!=null){
			// Registro Debug.
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
			int readByte = 0;
			while((readByte = this.isResponse.read(readB))!= -1){
				bout.write(readB,0,readByte);
			}
			this.isResponse.close();
			bout.flush();
			bout.close();
			this.logger.info("Messaggio ricevuto (ContentType:"+this.tipoRisposta+") :\n"+bout.toString(),false);
			// Creo nuovo inputStream
			this.isResponse = new ByteArrayInputStream(bout.toByteArray());
		}
		else{
			this.logger.info("Messaggio ricevuto (ContentType:"+this.tipoRisposta+") senza contenuto nell'http-reply",false);
		}
	}
	
	protected boolean doRestResponse() throws Exception{
		if(this.debug)
			this.logger.debug("gestione REST in corso ...");
		
		String contentTypeString = "N.D.";
		if(this.tipoRisposta!=null && !"".equals(this.tipoRisposta)){
			contentTypeString = this.tipoRisposta;
		}
		
		MessageType messageTypeResponse = null;
		String msgErrore = null;
		Exception exErrore = null;
		try{
			messageTypeResponse = this.requestInfo.getBindingConfig().getMessageType(this.requestMsg.getServiceBinding(), MessageRole.RESPONSE, 
					this.requestMsg.getTransportRequestContext(),
					this.tipoRisposta);				
			if(messageTypeResponse==null){
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
		if(msgErrore!=null){
			if(this.checkContentType){
				if(exErrore!=null){
					this.logger.error(msgErrore,exErrore);
				}
				else{
					this.logger.error(msgErrore);
				}
				Exception e = new Exception(msgErrore);
				this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
				this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION,
						ParseExceptionUtils.buildParseException(e));
				throw e;
			}else{
				msgErrore = msgErrore+"; viene utilizzata forzatamente la tipologia "+MessageType.BINARY.name() +" come modalità di gestione del messaggio";
				if(exErrore!=null){
					this.logger.warn(msgErrore,exErrore);
				}
				else{
					this.logger.warn(msgErrore);
				}
				messageTypeResponse = MessageType.BINARY;
			}
		}
		
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
		
		TransportResponseContext responseContext = new TransportResponseContext();
		responseContext.setCodiceTrasporto(this.codice+"");
		responseContext.setContentLength(this.contentLength);
		responseContext.setParametersTrasporto(this.propertiesTrasportoRisposta);
		
		OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageTypeResponse,responseContext,
				isParam,this.notifierInputStreamParams,
				this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());	
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
				this.errore = "Errore avvenuto durante la consegna HTTP (lettura risposta): " + this.readExceptionMessageFromException(e);
				this.logger.error("Errore avvenuto durante la consegna HTTP (lettura risposta): " + this.readExceptionMessageFromException(e),e);
				if(result2XX){
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	
	protected boolean doSoapResponse() throws Exception{

		String tipoLetturaRisposta = null;
		
		// gestione ordinaria via WS/SOAP
		
		if(this.debug)
			this.logger.debug("gestione WS/SOAP in corso ...");
		
		/*
		 * Se il messaggio e' un html di errore me ne esco 
		 */			
		if(this.codice>=400 && this.tipoRisposta!=null && this.tipoRisposta.contains(HttpConstants.CONTENT_TYPE_HTML)){
			tipoLetturaRisposta = "("+this.codice+") " + this.resultHTTPMessage ;
			
			// Registro HTML ricevuto.
			String htmlRicevuto = null;
			if(this.isResponse!=null){
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
				int readByte = 0;
				while((readByte = this.isResponse.read(readB))!= -1){
					bout.write(readB,0,readByte);
				}
				this.isResponse.close();
				bout.flush();
				bout.close();
				htmlRicevuto = bout.toString();
			}
			
			if(htmlRicevuto!=null && !"".equals(htmlRicevuto))
				this.errore = tipoLetturaRisposta +"\nhttp response: "+htmlRicevuto;
			else
				this.errore = tipoLetturaRisposta;
			return false;
		}
		
		if(this.isResponse!=null){
			
			MessageType messageTypeResponse = null;
			
			if(this.sbustamentoSoap==false){
				
				String contentTypeString = "N.D.";
				if(this.tipoRisposta!=null && !"".equals(this.tipoRisposta)){
					contentTypeString = this.tipoRisposta;
				}
				
				String msgErrore = null;
				Exception exErrore = null;
				try{
				
					if(this.tipoRisposta==null){
						// obbligatorio in SOAP
						msgErrore = "Header Content-Type non definito nell'http reply";
					}
					else{
						messageTypeResponse = this.requestInfo.getBindingConfig().getMessageType(this.requestMsg.getServiceBinding(), MessageRole.RESPONSE, 
								this.requestMsg.getTransportRequestContext(),
								this.tipoRisposta);				
					}	
				
					if(messageTypeResponse==null){
						
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
						if(this.requestMsg.getMessageType().equals(messageTypeResponse)==false){
							msgErrore = "Header Content-Type definito nell'http reply associato ad un tipo ("+messageTypeResponse.name()
										+") differente da quello associato al messaggio di richiesta ("+this.requestMsg.getMessageType().name();
						}
					}
				}catch(Exception e){
					exErrore = e;
					msgErrore = "Non è stato possibile comprendere come trattare il messaggio ricevuto (Content-Type: "+contentTypeString+"): "+e.getMessage();
				}
				if(msgErrore!=null){
					if(this.checkContentType){
						if(exErrore!=null){
							this.logger.error(msgErrore,exErrore);
						}
						else{
							this.logger.error(msgErrore);
						}
						Exception e = new Exception(msgErrore);
						this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
						this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION,
								ParseExceptionUtils.buildParseException(e));
						throw e;
					}else{
						messageTypeResponse = MessageType.SOAP_11;
						this.tipoRisposta = SoapUtils.getSoapContentTypeForMessageWithoutAttachments(messageTypeResponse);
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
				messageTypeResponse = this.requestMsg.getMessageType();
				this.tipoRisposta = SoapUtils.getSoapContentTypeForMessageWithoutAttachments(messageTypeResponse);
			}

			TransportResponseContext responseContext = new TransportResponseContext();
			responseContext.setCodiceTrasporto(this.codice+"");
			responseContext.setContentLength(this.contentLength);
			responseContext.setParametersTrasporto(this.propertiesTrasportoRisposta);
			
			try{
				
				if(this.sbustamentoSoap==false){
					if(this.debug)
						this.logger.debug("Ricostruzione normale...");
					
					// Ricostruzione messaggio soap: secondo parametro a false, indica che il messaggio e' gia un SOAPMessage
					tipoLetturaRisposta = "Parsing Risposta SOAP";
						
					if(this.contentLength>0){
						OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageTypeResponse,responseContext,
								this.isResponse,this.notifierInputStreamParams,
								this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());	
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
							OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageTypeResponse,responseContext,
									this.isResponse,this.notifierInputStreamParams,
									this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());
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
								this.errore = "Errore avvenuto durante la consegna HTTP (lettura risposta): " + this.readExceptionMessageFromException(e);
								this.logger.error("Errore avvenuto durante la consegna HTTP (lettura risposta): " + this.readExceptionMessageFromException(e),e);
								if(result2XX){
									return false;
								}
							}
						}
					}
					
					// Check eventuale 'xml instruction presente nel body' 
					/*try{
						this.checkXMLInstructionTargetMachine();
					}catch(Exception e){
						this.errore = "Errore avvenuto durante la consegna HTTP (salvataggio risposta, check xml instruction): " + e.getMessage();
						this.log.error("Errore avvenuto durante la consegna HTTP (salvataggio risposta, check xml instruction)",e);
						return false;
					}*/
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
								this.responseMsg = TunnelSoapUtils.imbustamentoMessaggioConAttachment(messageTypeResponse,MessageRole.RESPONSE, 
										cis,this.mimeTypeAttachment,
										MailcapActivationReader.existsDataContentHandler(this.mimeTypeAttachment),this.tipoRisposta, 
										this.openspcoopProperties.getHeaderSoapActorIntegrazione());
							}else{
								if(this.debug)
									this.logger.debug("Imbustamento messaggio...");
								tipoLetturaRisposta = "Imbustamento messaggio in un messaggio SOAP";
								OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().
										envelopingMessage(messageTypeResponse, this.tipoRisposta, this.soapAction, responseContext, 
												cis, this.notifierInputStreamParams, 
												this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold(),
												true);
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
							}catch(Exception eClose){}
						}
					}
					
				}
				try{
					if(this.responseMsg!=null){
						this.responseMsg.castAsSoap().getSOAPPart().getEnvelope();
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
						this.errore = "Errore avvenuto durante la consegna HTTP (lettura risposta): " + this.readExceptionMessageFromException(e);
						this.logger.error("Errore avvenuto durante la consegna HTTP (lettura risposta): " + this.readExceptionMessageFromException(e),e);
						if(result2XX){
							return false;
						}
					}
				}
			}catch(Exception e){
				this.eccezioneProcessamento = e;
				this.errore = "Errore avvenuto durante la consegna HTTP ("+tipoLetturaRisposta+"): " + this.readExceptionMessageFromException(e);
				this.logger.error("Errore avvenuto durante la consegna HTTP ("+tipoLetturaRisposta+")",e);
				return false;
			}

			

			// save Msg
			if(this.debug)
				this.logger.debug("Save messaggio...");
			try{
				if(this.responseMsg!=null){
					// save changes.
					// N.B. il countAttachments serve per il msg con attachments come saveMessage!
					if(this.responseMsg.castAsSoap().countAttachments()==0){
						this.responseMsg.castAsSoap().getSOAPPart();
					}
				}
			}catch(Exception e){
				this.eccezioneProcessamento = e;
				this.errore = "Errore avvenuto durante la consegna HTTP (salvataggio risposta): " + this.readExceptionMessageFromException(e);
				this.logger.error("Errore avvenuto durante la consegna HTTP (salvataggio risposta): " + this.readExceptionMessageFromException(e),e);
				return false;
			}

		}
		
		return true;
	}
}
