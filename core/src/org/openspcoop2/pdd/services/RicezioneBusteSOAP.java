/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.builder.Imbustamento;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Esito;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;


/**
 * Contiene la definizione di una servlet 'RicezioneBusteDirect'
 * Si occupa di creare il Messaggio, applicare la logica degli handlers 
 * e passare il messaggio ad OpenSPCoop per lo sbustamento e consegna.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RicezioneBusteSOAP  {

	public static OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getMessageFactory();


	public void process(ConnectorInMessage req, ConnectorOutMessage res) throws ConnectorException {
		
		// IDModulo
		String idModulo = req.getIdModulo();
		
		// Timestamp
		Timestamp dataIngressoMessaggio = DateManager.getTimestamp();
		
		// Log
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = Logger.getLogger(idModulo);
		
		//	Proprieta' OpenSPCoop
		OpenSPCoop2Properties openSPCoopProperties = OpenSPCoop2Properties.getInstance();
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
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
		
		// PddContext from servlet
		Object oPddContextFromServlet = req.getAttribute(CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP);
		PdDContext pddContextFromServlet = null;
		if(oPddContextFromServlet!=null){
			pddContextFromServlet = (PdDContext) oPddContextFromServlet;
		}
		
		
		
		
		

		
		
		
		
		
		
		
		/* ------------  Lettura parametri della richiesta ------------- */
		
		
		//	Risposta Soap
		OpenSPCoop2Message responseMessage = null;

		// Proprieta errore applicativo
		ProprietaErroreApplicativo proprietaErroreAppl = null;

		// PostOutResponseContext
		PostOutResponseContext postOutResponseContext = null;

		RicezioneBusteContext context = null;
		PdDContext pddContext = null;
		OpenSPCoop2Message requestMessage = null;
		
		
		Imbustamento protocolErroreBuilder = null;
		IProtocolFactory protocolFactory = null;
		SOAPVersion versioneSoap = SOAPVersion.SOAP11;
		String protocol = null;
		try{
			
			/* --------------- Creo il context che genera l'id univoco ----------------------- */
			
			protocolFactory = req.getProtocolFactory();
			protocol = protocolFactory.getProtocol();
			
			proprietaErroreAppl = openSPCoopProperties.getProprietaGestioneErrorePD(protocolFactory.createProtocolManager());
			proprietaErroreAppl.setDominio(openSPCoopProperties.getIdentificativoPortaDefault(protocolFactory.getProtocol()));
			proprietaErroreAppl.setIdModulo(idModulo);
			
			context = new RicezioneBusteContext(dataIngressoMessaggio,openSPCoopProperties.getIdentitaPortaDefault(protocolFactory.getProtocol()));
			context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
			context.setTipoPorta(TipoPdD.APPLICATIVA);
			context.setIdModulo(idModulo);
			msgDiag.setPddContext(context.getPddContext(), protocolFactory);
			pddContext = context.getPddContext();
			
			protocolErroreBuilder = new Imbustamento(logCore, protocolFactory);
			
			
			
			
					
			/* ------------  PreInHandler ------------- */
			
			// build context
			PreInRequestContext preInRequestContext = new PreInRequestContext(pddContext);
			if(pddContextFromServlet!=null){
				preInRequestContext.getPddContext().addAll(pddContextFromServlet, true);
			}
			preInRequestContext.setTipoPorta(TipoPdD.APPLICATIVA);
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
			
			
			
			
			/* ------------  PostOutResponseContext ------------- */
			postOutResponseContext = new PostOutResponseContext(logCore, protocolFactory);
			postOutResponseContext.setTipoPorta(TipoPdD.APPLICATIVA);
			postOutResponseContext.setPddContext(pddContext);
			
			
			
			
			
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
			if(openSPCoopProperties.isControlloContentTypeAbilitatoRicezioneBuste() == false){
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
					responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(), context.getTipoPorta(), context.getIdModulo(), 
							ErroriIntegrazione.ERRORE_433_CONTENT_TYPE_NON_PRESENTE.getErrore433_ContentTypeNonPresente(versioneSoap,supportedContentTypes),
							versioneSoap);	
				}	
				else{
					//ContentType del messaggio non supportato
					msgDiag.logPersonalizzato("contentType.unsupported");
					responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(),  context.getTipoPorta(),context.getIdModulo(), 
							ErroriIntegrazione.ERRORE_429_CONTENT_TYPE_NON_SUPPORTATO.getErrore429_ContentTypeNonSupportato(versioneSoap, 
									contentTypeReq,supportedContentTypes), versioneSoap);	
				}
			}
			else{
				/* ------------  SoapAction check 1 (controlla che non sia null e ne ritorna il valore) ------------- */			
				String soapAction = req.getSOAPAction(versioneSoap, contentTypeReq);
				
				/* ------------ Costruzione Messaggio -------------------- */
				requestMessage = req.getRequest(notifierInputStreamParams, contentTypeReq);
				requestMessage.setProtocolName(protocolFactory.getProtocol());
				
				/* ------------ Controllo MustUnderstand -------------------- */
				String mustUnderstandError = ServletUtils.checkMustUnderstand((SOAPMessage) requestMessage,protocolFactory);
				
				/* ------------ Controllo Soap namespace -------------------- */
				String soapEnvelopeNamespaceVersionMismatch = ServletUtils.checkSOAPEnvelopeNamespace((SOAPMessage) requestMessage, versioneSoap);
				
				/* ------------  Raccolta Parametri Porta Applicativa ------------- */	
				URLProtocolContext urlProtocolContext = req.getURLProtocolContext();
				requestMessage.setTransportRequestContext(urlProtocolContext);				
				
				/* ------------  Raccolta Credenziali ------------- */
				Credenziali credenziali = req.getCredenziali();
	
				/* ------------  Location ------------- */
				context.setFromLocation(req.getLocation(credenziali));
	
				/* ------------  SoapAction check 2 ------------- */
				if(soapAction!=null){
					if(openSPCoopProperties.checkSoapActionQuotedString_ricezioneBuste()){
						ServletUtils.checkSoapActionQuotedString(soapAction, versioneSoap);
					}
					context.setSoapAction(soapAction);
					requestMessage.setProperty("SOAPAction", soapAction);
					requestMessage.getMimeHeaders().addHeader(org.openspcoop2.message.Costanti.SOAP_ACTION, soapAction);
				}
				
//				/* ------------  Header di Trasporto ------------- */		
//				java.util.Properties headerTrasporto = 
//					new java.util.Properties();	    
//				java.util.Enumeration<?> enTrasporto = req.getHeaderNames();
//				while(enTrasporto.hasMoreElements()){
//					String nomeProperty = (String)enTrasporto.nextElement();
//					headerTrasporto.setProperty(nomeProperty,req.getHeader(nomeProperty));
//					//log.info("Proprieta' Trasporto: nome["+nomeProperty+"] valore["+req.getHeader(nomeProperty)+"]");
//				}
				
				
				/* ------------  Raccolta dati di Richiesta ------------- */
	
	
				
				// Contesto di Richiesta
				context.setGestioneRisposta(true); // siamo in un webServices, la risposta deve essere aspettata
				context.setMessageRequest(requestMessage);
				context.setTracciamentoAbilitato(true);
				context.setCredenziali(credenziali);
				context.setUrlProtocolContext(urlProtocolContext);
				context.setMsgDiagnostico(msgDiag);
	
				// Controllo se c'è stato un errore nel bypass dei MustUnderstand
				if(mustUnderstandError==null && soapEnvelopeNamespaceVersionMismatch==null){
					// Processo la richiesta e ottengo la risposta
					RicezioneBuste gestoreRichiesta = new RicezioneBuste(context);
					gestoreRichiesta.process(req);
					responseMessage = context.getMessageResponse();
				}
				else{
					if(mustUnderstandError!=null){
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, mustUnderstandError);
						msgDiag.logPersonalizzato("mustUnderstand.unknown");
						responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(), context.getTipoPorta(),context.getIdModulo(), 
								ErroriIntegrazione.ERRORE_427_MUSTUNDERSTAND_ERROR.getErrore427_MustUnderstandHeaders(mustUnderstandError), versioneSoap);
					}
					else{
						msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE, soapEnvelopeNamespaceVersionMismatch);
						msgDiag.logPersonalizzato("soapEnvelopeNamespace.versionMismatch");
						responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(), context.getTipoPorta(),context.getIdModulo(),
								ErroriIntegrazione.ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR.
									getErrore430_SoapNamespaceNonSupportato(versioneSoap, soapEnvelopeNamespaceVersionMismatch), versioneSoap);
					}
				}
			}
				
		} catch (ProtocolException e) {
			try{
				// Capita nell'instanziazione della ProtocolFactory. Uso la factory basic per costruire l'errore.
				context = RicezioneBusteContext.newRicezioneBusteContext(dataIngressoMessaggio, openSPCoopProperties.getIdentitaPortaDefault(protocol));
				pddContext = context.getPddContext();
				if(postOutResponseContext!=null){
					postOutResponseContext.setPddContext(pddContext);
				}
				msgDiag.setPddContext(pddContext,ProtocolFactoryManager.getInstance().getDefaultProtocolFactory());
				msgDiag.logErroreGenerico(e, "MessaggioRichiestaMalformato");
				responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_intestazione(context.getIdentitaPdD(), context.getTipoPorta(),context.getIdModulo(),
						ErroriIntegrazione.ERRORE_432_MESSAGGIO_XML_MALFORMATO.getErrore432_MessaggioRichiestaMalformato(true, e), versioneSoap);
			}catch(ProtocolException ep){
				throw new ConnectorException(ep.getMessage(),ep);
			}
		} catch (Exception e) {
			
			if(context==null){
				// Errore durante la generazione dell'id
				context = RicezioneBusteContext.newRicezioneBusteContext(dataIngressoMessaggio,openSPCoopProperties.getIdentitaPortaDefault(protocol));
				context.setTipoPorta(TipoPdD.APPLICATIVA);
				context.setIdModulo(idModulo);
				context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
				pddContext = context.getPddContext();
				msgDiag.setPddContext(pddContext,protocolFactory);
				postOutResponseContext.setPddContext(pddContext);
			}

			boolean saxParseException = Utilities.existsInnerException(e, "org.xml.sax.SAXParseException");
			boolean wstxParsingException = false;
			boolean wstxUnexpectedCharException = false;
			if(saxParseException==false){
				wstxParsingException = Utilities.existsInnerException(e, "com.ctc.wstx.exc.WstxParsingException");
				wstxUnexpectedCharException = Utilities.existsInnerException(e, "com.ctc.wstx.exc.WstxUnexpectedCharException");
			}
			
			logCore.error("ErroreGenerale",e);
			if(wstxParsingException){
				msgDiag.logErroreGenerico(Utilities.getInnerException(e, "com.ctc.wstx.exc.WstxParsingException"), "Generale(richiesta)");
			} else if(wstxUnexpectedCharException){
				msgDiag.logErroreGenerico(Utilities.getInnerException(e, "com.ctc.wstx.exc.WstxUnexpectedCharException"), "Generale(richiesta)");
			} else {
				msgDiag.logErroreGenerico(e, "Generale(richiesta)");
			}
			
			// Genero risposta con errore
			String msgErrore = e.getMessage();
			if(msgErrore==null){
				msgErrore = e.toString();
			}
			try {
				
				// Ricerca org.xml.sax.SAXParseException || com.ctc.wstx.exc.WstxParsingException ||  com.ctc.wstx.exc.WstxUnexpectedCharException
				if(saxParseException || wstxParsingException || wstxUnexpectedCharException){
					// Richiesto da certificazione DigitPA
					responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_intestazione(context.getIdentitaPdD(),context.getTipoPorta(), context.getIdModulo(),
							ErroriIntegrazione.ERRORE_432_MESSAGGIO_XML_MALFORMATO.getErrore432_MessaggioRichiestaMalformato(true,e), versioneSoap);
				}else if(msgErrore.equals("Transport level information does not match with SOAP Message namespace URI")){
					msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE, "Impossibile recuperare il valore del namespace");
					msgDiag.logPersonalizzato("soapEnvelopeNamespace.versionMismatch");
					responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(),context.getTipoPorta(), context.getIdModulo(),
							ErroriIntegrazione.ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR.
								getErrore430_SoapNamespaceNonSupportato(versioneSoap,  "Impossibile recuperare il valore del namespace"), versioneSoap);
				}else{
					responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(),context.getTipoPorta(), context.getIdModulo(),
							ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.getErrore426_ServletError(true, e), e, versioneSoap);
				}
			} catch (Exception ee) {
				logCore.error("Creazione del messaggio di fault fallita");
				responseMessage = OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(versioneSoap, "ErroreCreazioneMessaggioFault: " + ee.getMessage());
			}
		}
		finally{
			
			if(requestMessage != null){
				if(requestMessage.getParsingError() != null){
					try {
						Throwable eParse = Utilities.getInnerException(requestMessage.getParsingError(), "com.ctc.wstx.exc.WstxParsingException");
						if(eParse == null) eParse = Utilities.getInnerException(requestMessage.getParsingError(), "com.ctc.wstx.exc.WstxEOFException");
						if(eParse == null) eParse=requestMessage.getParsingError();
						msgDiag.logErroreGenerico(eParse, "Generale(richiesta)");
						String msgErrore = requestMessage.getParsingError().getMessage();
						if(msgErrore==null){
							msgErrore = requestMessage.getParsingError().toString();
						}
						responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_intestazione(context.getIdentitaPdD(),context.getTipoPorta(), context.getIdModulo(),
								ErroriIntegrazione.ERRORE_432_MESSAGGIO_XML_MALFORMATO.getErrore432_MessaggioRichiestaMalformato(true,requestMessage.getParsingError()), versioneSoap);
					} catch (Exception e1) {
						logCore.error("Creazione del messaggio di fault fallita");
					}
				}
			}
			// *** GB **
			try{
				req.close();
			}catch(Exception e){
				logCore.error("Request.close() error: "+e.getMessage(),e);
			}
			// *** GB ***
		}



		if(context.getMsgDiagnostico()!=null){
			msgDiag = context.getMsgDiagnostico();
		}
		if(context.getHeaderIntegrazioneRisposta()!=null){
			java.util.Enumeration<?> en = context.getHeaderIntegrazioneRisposta().keys();
	    	while(en.hasMoreElements()){
	    		String key = (String) en.nextElement();
	    		res.setHeader(key,context.getHeaderIntegrazioneRisposta().getProperty(key));
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

			// Invio la risposta
			if(responseMessage!=null){
				
				// force response code
				if(responseMessage.getForcedResponseCode()!=null){
					try{
						statoServletResponse = Integer.parseInt(responseMessage.getForcedResponseCode());
					}catch(Exception e){}
				}
				
				// Puo' capitare che il messaggio abbia avuto un errore di parsing, 
				// ma in alcuni test della testsuite capita che non venga sollevato 
				// nuovamente nei successivi accessi e venga serializzato un messaggio malformato. 
				// Faccio un check esplicito. In altre servlet non e' stato necessario.
								
				if(responseMessage.getParsingError() != null)
					throw responseMessage.getParsingError();
				
				// transfer length
				ServletUtils.setTransferLength(openSPCoopProperties.getTransferLengthModes_ricezioneBuste(), 
						req, res, responseMessage);
				
				//NOTA: Faccio la save per refreshare il ContentType. Necessario nel caso di attachment 
				// (implementazione SAAJ della SUN)
    			responseMessage.updateContentType();
				
				// content type
				String contentTypeRisposta = responseMessage.getContentType();
				if (contentTypeRisposta != null) 
					res.setContentType(contentTypeRisposta);
				else 
					throw new Exception("Risposta senza Content-type");

				// http status
				body = responseMessage.getSOAPBody();
				esito = protocolFactory.createEsitoBuilder().getEsito(responseMessage, proprietaErroreAppl);
				if(body!=null && body.hasFault()){
					statoServletResponse = 500;
					res.setStatus(500);
					descrizioneSoapFault = " ("+SoapUtils.toString(body.getFault(), false)+")";
				}
				
				// Il contentLenght, nel caso di TransferLengthModes.CONTENT_LENGTH e' gia' stato calcolato
				// con una writeTo senza consume. Riuso il solito metodo per evitare differenze di serializzazione
				// e cambiare quindi il content length effettivo.
				if(TransferLengthModes.CONTENT_LENGTH.equals(openSPCoopProperties.getTransferLengthModes_ricezioneBuste())){
					res.sendResponse(responseMessage, false);
				} else {
					res.sendResponse(responseMessage, true);
				}
				
			}else{
				statoServletResponse = protocolFactory.createProtocolManager().getHttpReturnCodeEmptyResponseOneWay();
				res.setStatus(statoServletResponse);
				httpEmptyResponse = true;
				esito = Esito.OK; // carico-vuoto
			}
			
		}catch(Exception e){
			// Errore nell'invio della risposta
			logCore.error("ErroreGenerale",e);
			erroreConsegnaRisposta = e;
			
			// Genero risposta con errore
			try{
				if(responseMessage != null && responseMessage.getParsingError()!=null){
					String msgErrore = responseMessage.getParsingError().getMessage();
					if(msgErrore==null){
						msgErrore = responseMessage.getParsingError().toString();
					}
					msgDiag.logErroreGenerico(responseMessage.getParsingError(), "Generale(risposta)");
					responseMessageError = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(),context.getTipoPorta(), context.getIdModulo(),
							ErroriIntegrazione.ERRORE_432_MESSAGGIO_XML_MALFORMATO.
								getErrore432_MessaggioRichiestaMalformato(false,responseMessage.getParsingError()), versioneSoap);
				} else {
					responseMessageError = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(),context.getTipoPorta(), context.getIdModulo(),
							ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.getErrore426_ServletError(false, e), versioneSoap);
				}
				// transfer length
				ServletUtils.setTransferLength(openSPCoopProperties.getTransferLengthModes_ricezioneBuste(), 
						req, res, responseMessageError);
								
				// content type
				String contentTypeRisposta = responseMessageError.getContentType();
				if (contentTypeRisposta != null) 
					res.setContentType(contentTypeRisposta);
				else 
					throw new Exception("Risposta errore senza Content-type");
				
				// http status (puo' essere 200 se il msg di errore e' un msg errore applicativo cnipa non in un soap fault)
				body = responseMessageError.getSOAPBody();
				esito = protocolFactory.createEsitoBuilder().getEsito(responseMessageError, proprietaErroreAppl);
				if(body!=null && body.hasFault()){
					statoServletResponse = 500;
					res.setStatus(500);
					descrizioneSoapFault = " ("+SoapUtils.toString(body.getFault(), false)+")";
				}
				
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
				res.setStatus(500);
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
			
		}
		finally{
			
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
					msgDiag.logPersonalizzato("consegnaMessaggioFallita");
					
				}else{
					if(httpEmptyResponse){
						msgDiag.logPersonalizzato("consegnaMessaggioNonPresente");
					}else{
						if(statoServletResponse==500)
							msgDiag.logPersonalizzato("consegnaMessaggioKoEffettuata");
						else
							msgDiag.logPersonalizzato("consegnaMessaggioOkEffettuata");
					}
				}
				
			}catch(Exception e){
				
				logCore.error("Chiusura stream non riuscita",e);
				
				// Risposta non ritornata al servizio applicativo, il socket verso il servizio applicativo era chiuso o cmq inutilizzabile
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, e.toString()); // NOTA: lasciare e.toString()
				
				msgDiag.logPersonalizzato("consegnaMessaggioFallita");
				
				erroreConsegnaRisposta = e;
				
			}
			
		}
		
		
		
		
		
		
		
		
		// *** Chiudo connessione verso PdD Destinazione per casi stateless ***
		String location = "...";
		try{
			IConnettore c = null;
			if(context.getIdMessage()!=null){
				c = RepositoryConnettori.removeConnettorePA(context.getIdMessage());
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


}



