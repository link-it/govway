/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.services;

import java.sql.Timestamp;
import java.util.Hashtable;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.core.connettori.IConnettore;
import org.openspcoop2.pdd.core.connettori.RepositoryConnettori;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.handlers.PreInRequestContext;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.ConnectorOutMessage;
import org.openspcoop2.pdd.services.connector.DirectVMConnectorInMessage;
import org.openspcoop2.pdd.services.connector.DirectVMConnectorOutMessage;
import org.openspcoop2.pdd.services.connector.DirectVMProtocolInfo;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.builder.ErroreApplicativoBuilder;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Esito;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;

import com.ctc.wstx.exc.WstxException;

/**
 * Servlet di ingresso al servizio DirectPD che costruisce il SOAPMessage gli applica gli handlers
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */



public class RicezioneContenutiApplicativiSOAP {

	
	public static OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getMessageFactory();


	public void process(ConnectorInMessage req, ConnectorOutMessage res) throws ConnectorException {

		// IDModulo
		String idModulo = req.getIdModulo();
		IDService idModuloAsService = req.getIdModuloAsIDService();
		
		// Timestamp
		Timestamp dataIngressoMessaggio = DateManager.getTimestamp();
		
		// Log
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = Logger.getLogger(idModulo);
		
		//	Proprieta' OpenSPCoop
		OpenSPCoop2Properties openSPCoopProperties = OpenSPCoop2Properties.getInstance();
		
		//Cerco di ricavare la versione SOAP
		
		
		if (openSPCoopProperties == null) {
			logCore.error("Inizializzazione di OpenSPCoop non correttamente effettuata: OpenSPCoopProperties");
			try{
				OpenSPCoop2Message msg = OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(SOAPVersion.SOAP11, "ErroreInizializzazioneOpenSPCoopProperties"); 
				res.setStatus(500);
				res.sendResponse(msg, true);
				res.flush(false);
				res.close(false);
				return;
			}catch(Exception e){
				logCore.error("Errore generazione SOAPFault",e);
				throw new ConnectorException("Inizializzazione di OpenSPCoop non correttamente effettuata: OpenSPCoopProperties");
			}
		}
		
		// Logger dei messaggi diagnostici
		MsgDiagnostico msgDiag = new MsgDiagnostico(idModulo);
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
			
		// PddContext from servlet
		Object oPddContextFromServlet = null;
		try{
			oPddContextFromServlet = req.getAttribute(CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP);
		}catch(Exception e){
			logCore.error("req.getAttribute("+CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP+") error: "+e.getMessage(),e);
		}
		PdDContext pddContextFromServlet = null;
		if(oPddContextFromServlet!=null){
			pddContextFromServlet = (PdDContext) oPddContextFromServlet;
		}
		

		
		
		
				
		/* --------------- ProtocolFactory ----------------------- */
		IProtocolFactory protocolFactory = null;
		ErroreApplicativoBuilder erroreApplicativoBuilder = null;
		
		/* ------------  Lettura parametri della richiesta ------------- */

		//	Risposta Soap
		OpenSPCoop2Message responseMessage = null;
				
		// Proprieta errore applicativo
		ProprietaErroreApplicativo proprietaErroreAppl = null;

		// PostOutResponseContext
		PostOutResponseContext postOutResponseContext = null;
		
		RicezioneContenutiApplicativiContext context = null;
		PdDContext pddContext = null;
		OpenSPCoop2Message requestMessage = null;
		SOAPVersion versioneSoap = SOAPVersion.SOAP11;
		String protocol = null;
		try{
			
			/* --------------- Creo il context che genera l'id univoco ----------------------- */
			
			protocolFactory = req.getProtocolFactory();
			protocol = protocolFactory.getProtocol();
			
			proprietaErroreAppl = openSPCoopProperties.getProprietaGestioneErrorePD(protocolFactory.createProtocolManager());
			proprietaErroreAppl.setDominio(openSPCoopProperties.getIdentificativoPortaDefault(protocol));
			proprietaErroreAppl.setIdModulo(idModulo);
			
			context = new RicezioneContenutiApplicativiContext(idModuloAsService,dataIngressoMessaggio,openSPCoopProperties.getIdentitaPortaDefault(protocol));
			context.setTipoPorta(TipoPdD.DELEGATA);
			context.setIdModulo(idModulo);
			context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
			msgDiag.setPddContext(context.getPddContext(), protocolFactory);
			pddContext = context.getPddContext();
			
			if(req instanceof DirectVMConnectorInMessage){
				DirectVMConnectorInMessage vm = (DirectVMConnectorInMessage) req;
				if(vm.getDirectVMProtocolInfo()!=null){
					vm.getDirectVMProtocolInfo().setInfo(pddContext);
				}
			}
			
			
			
			
			/* ------------  PostOutResponseContext ------------- */
			postOutResponseContext = new PostOutResponseContext(logCore,protocolFactory);
			postOutResponseContext.setTipoPorta(TipoPdD.DELEGATA);
			postOutResponseContext.setPddContext(pddContext);
			
			
			
			
			/* ------------  PreInHandler ------------- */
			
			// build context
			PreInRequestContext preInRequestContext = new PreInRequestContext(pddContext);
			if(pddContextFromServlet!=null){
				preInRequestContext.getPddContext().addAll(pddContextFromServlet, true);
			}
			preInRequestContext.setTipoPorta(TipoPdD.DELEGATA);
			Hashtable<String, Object> transportContext = new Hashtable<String, Object>();
			transportContext.put(PreInRequestContext.SERVLET_REQUEST, req);
			transportContext.put(PreInRequestContext.SERVLET_RESPONSE, res);
			preInRequestContext.setTransportContext(transportContext);	
			preInRequestContext.setLogCore(logCore);
			
			// invocazione handler
			GestoreHandlers.preInRequest(preInRequestContext, msgDiag, logCore);
			
			// aggiungo eventuali info inserite nel preInHandler
			pddContext.addAll(preInRequestContext.getPddContext(), false);
			
			// Lettura risposta parametri NotifierInputStream
			NotifierInputStreamParams notifierInputStreamParams = preInRequestContext.getNotifierInputStreamParams();
			context.setNotifierInputStreamParams(notifierInputStreamParams);
			
			
				
			
			
			/* ------------ Controllo ContentType -------------------- */
			String contentTypeReq = req.getContentType();
			versioneSoap = ServletUtils.getVersioneSoap(logCore,contentTypeReq);
			
			boolean contentTypeSupportato = ServletUtils.isContentTypeSupported(versioneSoap, protocolFactory);
			
			// Imposto la versione soap di gestione visto che quella della richiesta non e' stata rilevata.
			if(versioneSoap==null)
				if(protocolFactory.createProtocolConfiguration().isSupportoSOAP11())
					versioneSoap = SOAPVersion.SOAP11;
				else 
					versioneSoap = SOAPVersion.SOAP12;
			
			erroreApplicativoBuilder = new ErroreApplicativoBuilder(logCore, protocolFactory, 
					openSPCoopProperties.getIdentitaPortaDefault(protocol), null, null, proprietaErroreAppl.getIdModulo(), 
					proprietaErroreAppl, versioneSoap, TipoPdD.DELEGATA, null); 
			
			pddContext.addObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION, versioneSoap);
			msgDiag.addKeyword(CostantiPdD.KEY_SOAP_VERSION, versioneSoap.getSoapVersionAsString());
			msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE_ATTESO, versioneSoap.getSoapEnvelopeNS());
			msgDiag.addKeyword(CostantiPdD.KEY_CONTENT_TYPES_ATTESI, 
					SOAPVersion.getKnownContentTypesAsString(protocolFactory.createProtocolConfiguration().isSupportoSOAP11(), 
													 		 protocolFactory.createProtocolConfiguration().isSupportoSOAP12()));
			String [] supportedContentTypes = 
					SOAPVersion.getKnownContentTypes(protocolFactory.createProtocolConfiguration().isSupportoSOAP11(), 
														     protocolFactory.createProtocolConfiguration().isSupportoSOAP12());
			
			msgDiag.addKeyword(CostantiPdD.KEY_HTTP_HEADER, contentTypeReq);
			if(openSPCoopProperties.isControlloContentTypeAbilitatoRicezioneContenutiApplicativi() == false){
				if(!contentTypeSupportato){
					if(CostantiPdD.CONTENT_TYPE_NON_PRESENTE.equals(contentTypeReq)){
						msgDiag.logPersonalizzato("contentType.notDefined");
					}else{
						msgDiag.logPersonalizzato("contentType.unsupported");
					}
					contentTypeReq = SOAPVersion.SOAP11.getContentTypeForMessageWithoutAttachments(); // Forzo text/xml;
					logCore.warn("Content-Type non supportato, viene utilizzato forzatamente il tipo: "+contentTypeReq);
					contentTypeSupportato = true;
				}
			}
			if(!contentTypeSupportato){
				
				// Leggo content Type dall'header HTTP per capire se l'header proprio non esiste o se e' il valore errato.
				if(CostantiPdD.CONTENT_TYPE_NON_PRESENTE.equals(contentTypeReq)){
					//ContentType del messaggio non presente
					msgDiag.logPersonalizzato("contentType.notDefined");
					responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_433_CONTENT_TYPE_NON_PRESENTE.
							getErrore433_ContentTypeNonPresente(versioneSoap,supportedContentTypes),null);
				}
				else{
					//ContentType del messaggio non supportato
					msgDiag.logPersonalizzato("contentType.unsupported");
					responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_429_CONTENT_TYPE_NON_SUPPORTATO.
							getErrore429_ContentTypeNonSupportato(versioneSoap, 
									req.getContentType(),supportedContentTypes),null);
				}
			}
			else{
				/* ------------  SoapAction check 1 (controlla che non sia null e ne ritorna il valore) ------------- */			
				String soapAction = req.getSOAPAction(versioneSoap, contentTypeReq);
				
				/* ------------ Costruzione Messaggio -------------------- */
				Utilities.printFreeMemory("RicezioneContenutiApplicativiDirect - Pre costruzione richiesta");
				requestMessage = req.getRequest(notifierInputStreamParams, contentTypeReq);
				Utilities.printFreeMemory("RicezioneContenutiApplicativiDirect - Post costruzione richiesta");
				
				/* ------------ Controllo MustUnderstand -------------------- */
				String mustUnderstandError = ServletUtils.checkMustUnderstand((SOAPMessage) requestMessage,protocolFactory);
				
				/* ------------ Controllo Soap namespace -------------------- */
				String soapEnvelopeNamespaceVersionMismatch = ServletUtils.checkSOAPEnvelopeNamespace((SOAPMessage) requestMessage, versioneSoap);

				/* ------------  Raccolta Parametri Porta Delegata ------------- */		
				URLProtocolContext urlProtocolContext = req.getURLProtocolContext();
				requestMessage.setTransportRequestContext(urlProtocolContext);					
				
				/* ------------  Raccolta Credenziali ------------- */			
				Credenziali credenziali = req.getCredenziali();
						
				/* ------------  Location ------------- */
				context.setFromLocation(req.getLocation(credenziali));
				
				/* ------------  SoapAction check 2 ------------- */
				if(soapAction!=null){
					if(openSPCoopProperties.checkSoapActionQuotedString_ricezioneContenutiApplicativi()){
						ServletUtils.checkSoapActionQuotedString(soapAction,versioneSoap);
					}
					context.setSoapAction(soapAction);
					requestMessage.setProperty("SOAPAction", soapAction);
					requestMessage.getMimeHeaders().addHeader(org.openspcoop2.message.Costanti.SOAP_ACTION, soapAction);
				}
				
							
				/* ------------  Raccolta dati di Richiesta ------------- */
			
				// Contesto di Richiesta
				context.setCredenziali(credenziali);
				context.setGestioneRisposta(true); // siamo in una servlet, la risposta deve essere aspettata
				context.setInvocazionePDPerRiferimento(false); // la PD con questa servlet non effettuera' mai invocazioni per riferimento.
				context.setMessageRequest(requestMessage);
				
				context.setUrlProtocolContext(urlProtocolContext);
				context.setMsgDiagnostico(msgDiag);
			
				// Invocazione...
				if(mustUnderstandError==null && soapEnvelopeNamespaceVersionMismatch==null){
					RicezioneContenutiApplicativi gestoreRichiesta = new RicezioneContenutiApplicativi(context);
					gestoreRichiesta.process(req);
					responseMessage = context.getMessageResponse();
				}	
				else{
					if(mustUnderstandError!=null){
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, mustUnderstandError);
						msgDiag.logPersonalizzato("mustUnderstand.unknown");
						responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_427_MUSTUNDERSTAND_ERROR.
								getErrore427_MustUnderstandHeaders(mustUnderstandError),null);
					}
					else{
						msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE, soapEnvelopeNamespaceVersionMismatch);
						msgDiag.logPersonalizzato("soapEnvelopeNamespace.versionMismatch");
						responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR.
								getErrore430_SoapNamespaceNonSupportato(versioneSoap,  soapEnvelopeNamespaceVersionMismatch),null);
					}
				}
			}
			
		}
		catch (ProtocolException e){
			try{
				// Capita nell'instanziazione della ProtocolFactory. Uso la factory basic per costruire l'errore.
				protocolFactory = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory();
				
				proprietaErroreAppl = openSPCoopProperties.getProprietaGestioneErrorePD(protocolFactory.createProtocolManager());
				proprietaErroreAppl.setDominio(openSPCoopProperties.getIdentificativoPortaDefault(protocolFactory.getProtocol()));
				proprietaErroreAppl.setIdModulo(idModulo);
				
				erroreApplicativoBuilder = this.newErroreApplicativoBuilder(req, logCore, protocolFactory, openSPCoopProperties, idModulo, proprietaErroreAppl);
				
				context = RicezioneContenutiApplicativiContext.newRicezioneContenutiApplicativiContext(idModuloAsService,dataIngressoMessaggio,openSPCoopProperties.getIdentitaPortaDefault(protocol));
				context.setTipoPorta(TipoPdD.DELEGATA);
				context.setIdModulo(idModulo);
				context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
				
				pddContext = context.getPddContext();
				if(postOutResponseContext!=null)
					postOutResponseContext.setPddContext(pddContext);
				
				msgDiag.setPddContext(pddContext, protocolFactory);
				msgDiag.logErroreGenerico(e, "MessaggioRichiestaMalformato");
				
				responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.getErrore426_ServletError(true, e),e);
			}catch(ProtocolException ep){
				throw new ConnectorException(ep.getMessage(),ep);
			}
		}
		catch (Exception e) {
			
			if(context==null){
				// Errore durante la generazione dell'id
				context = RicezioneContenutiApplicativiContext.newRicezioneContenutiApplicativiContext(idModuloAsService,dataIngressoMessaggio,openSPCoopProperties.getIdentitaPortaDefault(protocol));
				context.setTipoPorta(TipoPdD.DELEGATA);
				context.setIdModulo(idModulo);
				context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
				pddContext = context.getPddContext();
				postOutResponseContext.setPddContext(pddContext);
				msgDiag.setPddContext(pddContext, protocolFactory);
			}
			
			// La risposta dovra' essere un msg di errore applicativo 
			//System.out.println(e.getMessage());
			logCore.error("ErroreGenerale",e);
			msgDiag.logErroreGenerico(e, "Generale(richiesta)");
			
			// Verifico se e' stato instanziato l'erroreApplicativoBuilder (es. eccezione lanciata da PreInRequestHandler non fa inizializzare l'oggetto)
			if(erroreApplicativoBuilder==null){
				erroreApplicativoBuilder = newErroreApplicativoBuilder(req, logCore, protocolFactory, openSPCoopProperties, idModulo, proprietaErroreAppl);
			}
			
			// Genero risposta con errore
			String msgErrore = e.getMessage();
			if(msgErrore==null){
				msgErrore = e.toString();
			}
			if(msgErrore.equals("Transport level information does not match with SOAP Message namespace URI") || msgErrore.equals("I dati ricevuti non rappresentano un messaggio SOAP 1.1 valido: ")){
				msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE, "Impossibile recuperare il valore del namespace");
				msgDiag.logPersonalizzato("soapEnvelopeNamespace.versionMismatch");
				responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR.
						getErrore430_SoapNamespaceNonSupportato(versioneSoap,  "Impossibile recuperare il valore del namespace"),e);
			} else if(e instanceof WstxException || e.getCause() instanceof WstxException){
				responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_432_MESSAGGIO_XML_MALFORMATO.
						getErrore432_MessaggioRichiestaMalformato(true,e),e);
			} else {
				responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.getErrore426_ServletError(true, e),e);
			}
		}
		finally{
			
			if(requestMessage!=null){ 
				if(requestMessage.getParsingError() != null){
					String msgErrore = requestMessage.getParsingError().getMessage();
					if(msgErrore==null){
						msgErrore = requestMessage.getParsingError().toString();
					}
					msgDiag.logErroreGenerico(requestMessage.getParsingError(), "MessaggioRichiestaMalformato");
					responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_432_MESSAGGIO_XML_MALFORMATO.
							getErrore432_MessaggioRichiestaMalformato(true,requestMessage.getParsingError()),
							requestMessage.getParsingError());
				} else if(responseMessage!=null){ 
					if(responseMessage.getParsingError() != null){
						String msgErrore = responseMessage.getParsingError().getMessage();
						if(msgErrore==null){
							msgErrore = responseMessage.getParsingError().toString();
						}
						msgDiag.logErroreGenerico(responseMessage.getParsingError(), "MessaggioRispostaMalformato");
						responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_432_MESSAGGIO_XML_MALFORMATO.
								getErrore432_MessaggioRichiestaMalformato(false,responseMessage.getParsingError()),
								responseMessage.getParsingError());
					}
				}
			}
			
			
			
			// *** GB ***
			try{
				req.close();
			}catch(Exception e){
				logCore.error("Request.close() error: "+e.getMessage(),e);
			}
			// *** GB ***
		}
		
		// Imposto risposta
		if(context.getMsgDiagnostico()!=null){
			msgDiag = context.getMsgDiagnostico();
		}
		if(context.getHeaderIntegrazioneRisposta()!=null){
			java.util.Enumeration<?> en = context.getHeaderIntegrazioneRisposta().keys();
	    	while(en.hasMoreElements()){
	    		String key = (String) en.nextElement();
	    		String value = null;
	    		try{
	    			value = context.getHeaderIntegrazioneRisposta().getProperty(key);
	    			res.setHeader(key,value);
	    		}catch(Exception e){
	    			logCore.error("Request.setHeader("+key+","+value+") error: "+e.getMessage(),e);
	    		}
	    	}	
		}
		if(context!=null && context.getProtocol()!=null){
			erroreApplicativoBuilder.setMittente(context.getProtocol().getFruitore());
			IDServizio idServizio = new IDServizio();
			idServizio.setSoggettoErogatore(context.getProtocol().getErogatore());
			idServizio.setTipoServizio(context.getProtocol().getTipoServizio());
			idServizio.setServizio(context.getProtocol().getServizio());
			idServizio.setAzione(context.getProtocol().getAzione());
			erroreApplicativoBuilder.setServizio(idServizio);
			erroreApplicativoBuilder.setDominio(context.getIdentitaPdD());
			erroreApplicativoBuilder.setProprietaErroreApplicato(context.getProprietaErroreAppl());
		}
		if(context!=null && context.getIntegrazione()!=null){
			erroreApplicativoBuilder.setServizioApplicativo(context.getIntegrazione().getServizioApplicativoFruitore());
		}
		if(res instanceof DirectVMConnectorOutMessage){
			if(context!=null && context.getPddContext()!=null){
				DirectVMConnectorOutMessage vm = (DirectVMConnectorOutMessage) res;
				DirectVMProtocolInfo pInfo = new DirectVMProtocolInfo();
				Object oIdTransazione = context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
				if(oIdTransazione!=null){
					pInfo.setIdTransazione((String)oIdTransazione);
				}
				if(context.getProtocol()!=null){
					if(context.getProtocol().getIdRichiesta()!=null){
						pInfo.setIdMessaggioRichiesta(context.getProtocol().getIdRichiesta());
					}
					if(context.getProtocol().getIdRisposta()!=null){
						pInfo.setIdMessaggioRisposta(context.getProtocol().getIdRisposta());
					}
				}
				vm.setDirectVMProtocolInfo(pInfo);
			}
		}
		
		OpenSPCoop2Message responseMessageError = null;
		SOAPBody body = null;
		Esito esito = null;
		String descrizioneSoapFault = "";
		int statoServletResponse = 200;
		Exception erroreConsegnaRisposta = null;
		boolean httpEmptyResponse = false;
		try{
			if(responseMessage!=null){
				
				// force response code
				if(responseMessage.getForcedResponseCode()!=null){
					try{
						statoServletResponse = Integer.parseInt(responseMessage.getForcedResponseCode());
					}catch(Exception e){}
				}
								
				// transfer length
				ServletUtils.setTransferLength(openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi(), 
						req, res, responseMessage);
				
				// content type
				// Alcune implementazioni richiedono di aggiornare il Content-Type
				responseMessage.updateContentType();
				String contentTypeRisposta = responseMessage.getContentType();
				if (contentTypeRisposta != null) 
					res.setContentType(contentTypeRisposta);
				else 
					throw new Exception("Risposta senza Content-type");
				
				// http status
				body = responseMessage.getSOAPBody();
				esito = protocolFactory.createEsitoBuilder().getEsito(responseMessage, context.getProprietaErroreAppl());
				if(body!=null && body.hasFault()){
					statoServletResponse = 500;
					descrizioneSoapFault = " ("+SoapUtils.toString(body.getFault(), false)+")";
				}
				res.setStatus(statoServletResponse);
				
				// contenuto
				Utilities.printFreeMemory("RicezioneContenutiApplicativiDirect - Pre scrittura risposta");
				
				// Il contentLenght, nel caso di TransferLengthModes.CONTENT_LENGTH e' gia' stato calcolato
				// con una writeTo senza consume. Riuso il solito metodo per evitare differenze di serializzazione
				// e cambiare quindi il content length effettivo.
				if(TransferLengthModes.CONTENT_LENGTH.equals(openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi())){
					res.sendResponse(responseMessage, false);
				} else {
					res.sendResponse(responseMessage, true);
				}
				Utilities.printFreeMemory("RicezioneContenutiApplicativiDirect - Post scrittura risposta");


			}else{
				statoServletResponse = protocolFactory.createProtocolManager().getHttpReturnCodeEmptyResponseOneWay();
				res.setStatus(statoServletResponse);
				httpEmptyResponse = true;
				esito = Esito.OK; // carico-vuoto
			}
		}catch(Exception e){			
			logCore.error("ErroreGenerale",e);
			erroreConsegnaRisposta = e;
						
			// Genero risposta con errore
			try{
				if(responseMessage.getParsingError() != null){
					String msgErrore = responseMessage.getParsingError().getMessage();
					if(msgErrore==null){
						msgErrore = responseMessage.getParsingError().toString();
					}
					msgDiag.logErroreGenerico(responseMessage.getParsingError(), "MessaggioRispostaMalformato");
					responseMessageError = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_432_MESSAGGIO_XML_MALFORMATO.
							getErrore432_MessaggioRichiestaMalformato(false,responseMessage.getParsingError()),e);
				} else
					responseMessageError = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.getErrore426_ServletError(false, e),
							responseMessage.getParsingError());
				
				// transfer length
				ServletUtils.setTransferLength(openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi(), 
						req, res, responseMessageError);
				
				// content type
				String contentTypeRisposta = responseMessage.getContentType();
				if (contentTypeRisposta != null) 
					res.setContentType(contentTypeRisposta);
				else 
					throw new Exception("Risposta errore senza Content-type");
				
				// http status (puo' essere 200 se il msg di errore e' un msg errore applicativo cnipa non in un soap fault)
				body = responseMessageError.getSOAPBody();
				esito = protocolFactory.createEsitoBuilder().getEsito(responseMessageError, context.getProprietaErroreAppl());
				if(body!=null && body.hasFault()){
					statoServletResponse = 500;
					res.setStatus(500);
					descrizioneSoapFault = " ("+SoapUtils.toString(body.getFault(), false)+")";
				}
				
				// contenuto
				// Il contentLenght, nel caso di TransferLengthModes.CONTENT_LENGTH e' gia' stato calcolato
				// con una writeTo senza consume. Riuso il solito metodo per evitare differenze di serializzazione
				// e cambiare quindi il content length effettivo.
				if(TransferLengthModes.CONTENT_LENGTH.equals(openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi())){
					res.sendResponse(responseMessage, false);
				} else {
					res.sendResponse(responseMessage, true);
				}
																
			}catch(Exception error){
				logCore.error("Generazione di un risposta errore non riuscita",error);
				statoServletResponse = 500;
				try{
					res.setStatus(500);
				}catch(Exception eStatus){
					logCore.error("Response.setStatus(500) error: "+eStatus.getMessage(),eStatus);
				}
				try{
					OpenSPCoop2Message m = OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(versioneSoap, error);
					res.sendResponse(m, true);
				}catch(Exception  eError){
					try{
						res.sendResponse(error.toString().getBytes());
					}catch(Exception erroreStreamChiuso){ 
						//se lo stream non e' piu' disponibile non si potra' consegnare alcuna risposta
					}
				}
				esito = Esito.ERRORE_PROCESSAMENTO_PDD_5XX;
			}
			
		}finally{
						
			statoServletResponse = res.getResponseStatus(); // puo' essere "trasformato" da api engine
			msgDiag.addKeyword(CostantiPdD.KEY_CODICE_CONSEGNA, ""+statoServletResponse);
			msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, descrizioneSoapFault);
			
			try{

				// Flush and close response
				// NOTA: per poter ottenere l'errore di BrokenPipe sempre, deve essere disabilitato il socketBufferOutput sul servlet container.
				// Se non lo si disabilta, l'errore viene ritornato solo se il messaggio supera la dimensione del buffer (default: 8192K)
				// Ad esempio in tomcat utilizzare (socketBuffer="-1"): 
				//    <Connector protocol="HTTP/1.1" port="8080" address="${jboss.bind.address}" 
	            //       connectionTimeout="20000" redirectPort="8443" socketBuffer="-1" />
				res.flush(true);
				res.close(true);
				
				// Emetto diagnostico
				if(erroreConsegnaRisposta!=null){
					
					// Risposta non ritornata al servizio applicativo, il socket verso il servizio applicativo era chiuso o cmq inutilizzabile
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, erroreConsegnaRisposta.toString()); // NOTA: lasciare e.toString()
					msgDiag.logPersonalizzato("consegnaRispostaApplicativaFallita");
					
				}else{
					if(httpEmptyResponse){
						msgDiag.logPersonalizzato("consegnaRispostaApplicativaVuota");
					}else{
						if(statoServletResponse==500)
							msgDiag.logPersonalizzato("consegnaRispostaApplicativaKoEffettuata");
						else
							msgDiag.logPersonalizzato("consegnaRispostaApplicativaOkEffettuata");
					}
				}
				
			}catch(Exception e){
				
				logCore.error("Chiusura stream non riuscita",e);
				
				// Risposta non ritornata al servizio applicativo, il socket verso il servizio applicativo era chiuso o cmq inutilizzabile
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, e.toString()); // NOTA: lasciare e.toString()
				
				msgDiag.logPersonalizzato("consegnaRispostaApplicativaFallita");
				
				erroreConsegnaRisposta = e;
				
			}
			

		}
		
		
		
		
		
		
		
		
		// *** Chiudo connessione verso PdD Destinazione per casi stateless ***
		String location = "...";
		try{
			IConnettore c = null;
			if(context.getIdMessage()!=null){
				c = RepositoryConnettori.removeConnettorePD(context.getIdMessage());
			}
			if(c!=null){
				location = c.getLocation();
				c.disconnect();
			}
		}catch(Exception e){
			msgDiag.logDisconnectError(e, location);
		}
		
		

		
		
		
		
		
		
		
		/* ------------  PostOutResponseHandler ------------- */
		
		if(postOutResponseContext!=null){
			try{
				postOutResponseContext.getPddContext().addObject(CostantiPdD.DATA_INGRESSO_MESSAGGIO_RICHIESTA, dataIngressoMessaggio);
				postOutResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
				postOutResponseContext.setEsito(esito);
				postOutResponseContext.setReturnCode(statoServletResponse);
				postOutResponseContext.setPropertiesRispostaTrasporto(context.getHeaderIntegrazioneRisposta());
				postOutResponseContext.setProtocollo(context.getProtocol());
				postOutResponseContext.setIntegrazione(context.getIntegrazione());
				if(context.getTipoPorta()!=null)
					postOutResponseContext.setTipoPorta(context.getTipoPorta());	
				
				if(requestMessage!=null){
					postOutResponseContext.setInputRequestMessageSize(requestMessage.getIncomingMessageContentLength());
					postOutResponseContext.setOutputRequestMessageSize(requestMessage.getOutgoingMessageContentLength());
				}else{
					postOutResponseContext.setInputRequestMessageSize(req.getContentLength()+0l);
				}
				
				if(erroreConsegnaRisposta!=null){
					if(responseMessageError!=null){
						postOutResponseContext.setInputResponseMessageSize(responseMessageError.getIncomingMessageContentLength());
						postOutResponseContext.setOutputResponseMessageSize(responseMessageError.getOutgoingMessageContentLength());
						postOutResponseContext.setMessaggio(responseMessageError);
					}else{
						if(responseMessage!=null){
							postOutResponseContext.setInputResponseMessageSize(responseMessage.getIncomingMessageContentLength());
							postOutResponseContext.setOutputResponseMessageSize(responseMessage.getOutgoingMessageContentLength());
							postOutResponseContext.setMessaggio(responseMessage);
						}
					}
					postOutResponseContext.setErroreConsegna(erroreConsegnaRisposta.toString()); // NOTA: lasciare e.toString()
				}
				else if(responseMessage!=null){
					postOutResponseContext.setInputResponseMessageSize(responseMessage.getIncomingMessageContentLength());
					postOutResponseContext.setOutputResponseMessageSize(responseMessage.getOutgoingMessageContentLength());
					postOutResponseContext.setMessaggio(responseMessage);
				}
								
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"postOutResponse, preparazione contesto");
			}
			
			GestoreHandlers.postOutResponse(postOutResponseContext, msgDiag, logCore);
		}
		
		
		
		
		
		
		
		
		
		
		// *** Rilascio risorse NotifierInputStream ***
		
		// request
		try{
			if(requestMessage!=null && requestMessage.getNotifierInputStream()!=null){
				requestMessage.getNotifierInputStream().close();
			}
		}catch(Exception e){
			msgDiag.logErroreGenerico(e,"Rilascio risorse NotifierInputStream richiesta");
		}
		
		// response
		try{
			if(responseMessage!=null && responseMessage.getNotifierInputStream()!=null){
				responseMessage.getNotifierInputStream().close();
			}
		}catch(Exception e){
			msgDiag.logErroreGenerico(e,"Rilascio risorse NotifierInputStream risposta");
		}
		
		
		
		
		
		
		
		
		
		// *** GB ***
		requestMessage = null;
		body = null;
		responseMessage = null;
		responseMessageError = null;
		// *** GB ***
		
		return;

	}
	

	
	private ErroreApplicativoBuilder newErroreApplicativoBuilder(ConnectorInMessage req,Logger logCore,
			IProtocolFactory protocolFactory, OpenSPCoop2Properties openSPCoopProperties,
			String idModulo, ProprietaErroreApplicativo proprietaErroreAppl) throws ConnectorException{
		try{
			SOAPVersion versioneSoap = SOAPVersion.SOAP11;
			try{
				String contentTypeReq = req.getContentType();
				versioneSoap = ServletUtils.getVersioneSoap(logCore,contentTypeReq);
			}catch(Exception e){
				//ignore}
			}
			return new ErroreApplicativoBuilder(logCore, protocolFactory, 
					openSPCoopProperties.getIdentitaPortaDefault(protocolFactory.getProtocol()), null, null, idModulo, 
					proprietaErroreAppl, versioneSoap, TipoPdD.DELEGATA, null);
		}catch(Throwable ep){
			throw new ConnectorException(ep.getMessage(),ep);
		}
	}
	
}
