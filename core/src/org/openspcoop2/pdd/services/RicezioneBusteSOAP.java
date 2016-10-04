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



package org.openspcoop2.pdd.services;

import java.sql.Timestamp;
import java.util.Hashtable;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.io.output.NullOutputStream;
import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.ParseException;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
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
import org.openspcoop2.protocol.engine.builder.Imbustamento;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.builder.InformazioniErroriInfrastrutturali;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.utils.LoggerWrapperFactory;
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
		IDService idModuloAsService = req.getIdModuloAsIDService();
		
		// Timestamp
		Timestamp dataIngressoMessaggio = DateManager.getTimestamp();
		
		// Log
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = LoggerWrapperFactory.getLogger(idModulo);
		
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
			}catch(Throwable e){
				logCore.error("Errore generazione SOAPFault",e);
				throw new ConnectorException("Inizializzazione di OpenSPCoop non correttamente effettuata: OpenSPCoopProperties");
			}
		}
		
		// Configurazione Reader
		ConfigurazionePdDManager configPdDManager = null;
		DumpRaw dumpRaw = null;
		try{
			configPdDManager = ConfigurazionePdDManager.getInstance();
			if(configPdDManager==null || configPdDManager.isInitializedConfigurazionePdDReader()==false){
				throw new Exception("ConfigurazionePdDManager not initialized");
			}
			if(configPdDManager.dumpBinarioPA()){
				dumpRaw = new DumpRaw(logCore,false);
				req = new DumpRawConnectorInMessage(logCore, req);
				res = new DumpRawConnectorOutMessage(logCore, res);
			}
		}catch(Throwable e){
			logCore.error("Inizializzazione di OpenSPCoop non correttamente effettuata: ConfigurazionePdDManager");
			try{
				OpenSPCoop2Message msg = OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(SOAPVersion.SOAP11, "ErroreInizializzazioneConfigurazionePdDManager"); 
				res.setStatus(500);
				res.sendResponse(msg, true);
				res.flush(false);
				res.close(false);
				return;
			}catch(Throwable eError){
				logCore.error("Errore generazione SOAPFault",e);
				throw new ConnectorException("Inizializzazione di OpenSPCoop non correttamente effettuata: ConfigurazionePdDManager");
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
			
			context = new RicezioneBusteContext(idModuloAsService, dataIngressoMessaggio,openSPCoopProperties.getIdentitaPortaDefault(protocolFactory.getProtocol()));
			context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
			context.setTipoPorta(TipoPdD.APPLICATIVA);
			context.setIdModulo(idModulo);
			msgDiag.setPddContext(context.getPddContext(), protocolFactory);
			pddContext = context.getPddContext();
			
			try{
				msgDiag.logPersonalizzato("ricezioneRichiesta.firstLog");
			}catch(Exception e){
				logCore.error("Errore generazione diagnostico di ingresso",e);
			}
			
			if(dumpRaw!=null){
				dumpRaw.serializeContext(context, protocol);
			}
			
			protocolErroreBuilder = new Imbustamento(logCore, protocolFactory);
			
			DirectVMConnectorInMessage vm = null;
			if(req instanceof DirectVMConnectorInMessage){
				vm = (DirectVMConnectorInMessage) req;
			}
			else if(req instanceof DumpRawConnectorInMessage){
				if( ((DumpRawConnectorInMessage)req).getWrappedConnectorInMessage() instanceof DirectVMConnectorInMessage ){
					vm = (DirectVMConnectorInMessage) ((DumpRawConnectorInMessage)req).getWrappedConnectorInMessage();
				}
			}
			if(vm!=null && vm.getDirectVMProtocolInfo()!=null){
				vm.getDirectVMProtocolInfo().setInfo(pddContext);
			}
			
			
			
			
			
			/* ------------  PostOutResponseContext ------------- */
			postOutResponseContext = new PostOutResponseContext(logCore, protocolFactory);
			postOutResponseContext.setTipoPorta(TipoPdD.APPLICATIVA);
			postOutResponseContext.setPddContext(pddContext);
			postOutResponseContext.setIdModulo(idModulo);
			
			
			
					
			/* ------------  PreInHandler ------------- */
			
			// build context
			PreInRequestContext preInRequestContext = new PreInRequestContext(pddContext);
			if(pddContextFromServlet!=null){
				preInRequestContext.getPddContext().addAll(pddContextFromServlet, true);
			}
			preInRequestContext.setTipoPorta(TipoPdD.APPLICATIVA);
			preInRequestContext.setIdModulo(idModulo);
			preInRequestContext.setProtocolFactory(protocolFactory);
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
			
			if(dumpRaw!=null){
				dumpRaw.serializeRequest(((DumpRawConnectorInMessage)req), true, notifierInputStreamParams);
			}
			
			

			
			
			
			
			
			/* ------------ Controllo ContentType -------------------- */
			
			msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.tipologiaMessaggio");
			
			String contentTypeReq = null;
			try{
				contentTypeReq = req.getContentType();
				versioneSoap = ServletUtils.getVersioneSoap(logCore,contentTypeReq);
			}catch(Exception e){
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				throw e;
			}
			
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
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				
				// Leggo content Type dall'header HTTP per capire se l'header proprio non esiste o se e' il valore errato.
				if(CostantiPdD.CONTENT_TYPE_NON_PRESENTE.equals(contentTypeReq)){
					//ContentType del messaggio non presente
					msgDiag.logPersonalizzato("contentType.notDefined");
					responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(), context.getTipoPorta(), context.getIdModulo(), 
							ErroriIntegrazione.ERRORE_433_CONTENT_TYPE_NON_PRESENTE.getErrore433_ContentTypeNonPresente(versioneSoap,supportedContentTypes),
							versioneSoap, openSPCoopProperties.isForceSoapPrefixCompatibilitaOpenSPCoopV1());	
				}	
				else{
					//ContentType del messaggio non supportato
					msgDiag.logPersonalizzato("contentType.unsupported");
					responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(),  context.getTipoPorta(),context.getIdModulo(), 
							ErroriIntegrazione.ERRORE_429_CONTENT_TYPE_NON_SUPPORTATO.getErrore429_ContentTypeNonSupportato(versioneSoap, 
									contentTypeReq,supportedContentTypes), 
									versioneSoap, openSPCoopProperties.isForceSoapPrefixCompatibilitaOpenSPCoopV1());	
				}
			}
			else{
				/* ------------  SoapAction check 1 (controlla che non sia null e ne ritorna il valore) ------------- */			
				String soapAction = null;
				try{
					soapAction = req.getSOAPAction(versioneSoap, contentTypeReq);
				}catch(Exception e){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					throw e;
				}
				
				/* ------------ Costruzione Messaggio -------------------- */
				msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.inCorso");
				Utilities.printFreeMemory("RicezioneBuste - Pre costruzione richiesta");
				OpenSPCoop2MessageParseResult pr = req.getRequest(notifierInputStreamParams, contentTypeReq);
				if(pr.getParseException()!=null){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
				}
				requestMessage = pr.getMessage_throwParseException();
				Utilities.printFreeMemory("RicezioneBuste - Post costruzione richiesta");
				requestMessage.setProtocolName(protocolFactory.getProtocol());
				
				/* ------------ Controllo MustUnderstand -------------------- */
				String mustUnderstandError = null;
				try{
					mustUnderstandError = ServletUtils.checkMustUnderstand((SOAPMessage) requestMessage,protocolFactory);
				}catch(Exception e){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					throw e;
				}
				
				/* ------------ Controllo Soap namespace -------------------- */
				String soapEnvelopeNamespaceVersionMismatch = null;
				try{
					soapEnvelopeNamespaceVersionMismatch = ServletUtils.checkSOAPEnvelopeNamespace((SOAPMessage) requestMessage, versioneSoap);
				}catch(Exception e){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					throw e;
				}
				
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
						try{
							ServletUtils.checkSoapActionQuotedString(soapAction, versioneSoap);
						}catch(Exception e){
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
							throw e;
						}
					}
					context.setSoapAction(soapAction);
					requestMessage.setProperty(org.openspcoop2.message.Costanti.SOAP_ACTION, soapAction);
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
	
				// Log elaborazione dati completata
				msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.completata");
				
				// Controllo se c'è stato un errore nel bypass dei MustUnderstand
				if(mustUnderstandError==null && soapEnvelopeNamespaceVersionMismatch==null){
					// Processo la richiesta e ottengo la risposta
					RicezioneBuste gestoreRichiesta = new RicezioneBuste(context);
					gestoreRichiesta.process(req);
					responseMessage = context.getMessageResponse();
				}
				else{
					if(mustUnderstandError!=null){
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, mustUnderstandError);
						msgDiag.logPersonalizzato("mustUnderstand.unknown");
						responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(), context.getTipoPorta(),context.getIdModulo(), 
								ErroriIntegrazione.ERRORE_427_MUSTUNDERSTAND_ERROR.getErrore427_MustUnderstandHeaders(mustUnderstandError), 
								versioneSoap, openSPCoopProperties.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
					}
					else{
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
						msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE, soapEnvelopeNamespaceVersionMismatch);
						msgDiag.logPersonalizzato("soapEnvelopeNamespace.versionMismatch");
						responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(), context.getTipoPorta(),context.getIdModulo(),
								ErroriIntegrazione.ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR.
									getErrore430_SoapNamespaceNonSupportato(versioneSoap, soapEnvelopeNamespaceVersionMismatch), 
									versioneSoap, openSPCoopProperties.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
					}
				}
			}
				
		} catch (ProtocolException e) {
			try{
				// Capita nell'instanziazione della ProtocolFactory. Uso la factory basic per costruire l'errore.
				context = RicezioneBusteContext.newRicezioneBusteContext(idModuloAsService,dataIngressoMessaggio, openSPCoopProperties.getIdentitaPortaDefault(protocol));
				pddContext = context.getPddContext();
				if(postOutResponseContext!=null){
					postOutResponseContext.setPddContext(pddContext);
				}
				msgDiag.setPddContext(pddContext,ProtocolFactoryManager.getInstance().getDefaultProtocolFactory());
				msgDiag.logErroreGenerico(e, "MessaggioRichiestaMalformato");
				responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_intestazione(context.getIdentitaPdD(), context.getTipoPorta(),context.getIdModulo(),
						ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.getErrore426_ServletError(true, e), 
						versioneSoap, openSPCoopProperties.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
			}catch(ProtocolException ep){
				throw new ConnectorException(ep.getMessage(),ep);
			}
		} catch (Throwable e) {
			
			if(context==null){
				// Errore durante la generazione dell'id
				context = RicezioneBusteContext.newRicezioneBusteContext(idModuloAsService,dataIngressoMessaggio,openSPCoopProperties.getIdentitaPortaDefault(protocol));
				context.setTipoPorta(TipoPdD.APPLICATIVA);
				context.setIdModulo(idModulo);
				context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
				pddContext = context.getPddContext();
				msgDiag.setPddContext(pddContext,protocolFactory);
				if(postOutResponseContext!=null){
					postOutResponseContext.setPddContext(pddContext);
				}
			}

			// Se viene lanciata una eccezione, riguarda la richiesta, altrimenti è gestita dopo nel finally.
			Throwable tParsing = null;
			if(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)){
				tParsing = ((ParseException) pddContext.removeObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)).getParseException();
			}
			if(tParsing==null && (requestMessage==null || requestMessage.getParseException() == null)){
				tParsing = MessageUtils.getParseException(e);
			}
			
			// Genero risposta con errore
			String msgErrore = e.getMessage();
			if(msgErrore==null){
				msgErrore = e.toString();
			}
			try {
				
				// Ricerca org.xml.sax.SAXParseException || com.ctc.wstx.exc.WstxParsingException ||  com.ctc.wstx.exc.WstxUnexpectedCharException
				if(		// Messaggio lanciato dallo streaming engine
						msgErrore.equals("Transport level information does not match with SOAP Message namespace URI") ||
						// ?
						msgErrore.equals("I dati ricevuti non rappresentano un messaggio SOAP 1.1 valido: ") ||
						// I seguenti due errori invece vengono lanciati dalle classi 'com/sun/xml/messaging/saaj/soap/ver1_2/SOAPPart1_2Impl.java' e com/sun/xml/messaging/saaj/soap/ver1_1/SOAPPart1_1Impl.java
						// Proprio nel caso il namespace non corrisponde al tipo atteso.
						msgErrore.equals("InputStream does not represent a valid SOAP 1.1 Message") || 
						msgErrore.equals("InputStream does not represent a valid SOAP 1.2 Message")){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE, "Impossibile recuperare il valore del namespace");
					msgDiag.logPersonalizzato("soapEnvelopeNamespace.versionMismatch");
					responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(),context.getTipoPorta(), context.getIdModulo(),
							ErroriIntegrazione.ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR.
								getErrore430_SoapNamespaceNonSupportato(versioneSoap,  "Impossibile recuperare il valore del namespace"), 
								versioneSoap, openSPCoopProperties.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
				}
				else if(tParsing!=null){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					msgErrore = tParsing.getMessage();
					if(msgErrore==null){
						msgErrore = tParsing.toString();
					}
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
					logCore.error("parsingExceptionRichiesta",e);
					msgDiag.logPersonalizzato("parsingExceptionRichiesta");
					// Richiesto da certificazione DigitPA
					responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_intestazione(context.getIdentitaPdD(),context.getTipoPorta(), context.getIdModulo(),
							ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.getErrore432_MessaggioRichiestaMalformato(tParsing), 
							versioneSoap, openSPCoopProperties.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
				}else{
					logCore.error("ErroreGenerale",e);
					msgDiag.logErroreGenerico(e, "Generale(richiesta)");
					responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(),context.getTipoPorta(), context.getIdModulo(),
							ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.getErrore426_ServletError(true, e), e, 
							versioneSoap, openSPCoopProperties.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
				}
			} catch (Exception ee) {
				logCore.error("Creazione del messaggio di fault fallita");
				responseMessage = OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(versioneSoap, "ErroreCreazioneMessaggioFault: " + ee.getMessage());
			}
		}
		finally{
			
			if((requestMessage!=null && requestMessage.getParseException() != null) || 
					(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION))){
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				ParseException parseException = null;
				if( requestMessage!=null && requestMessage.getParseException() != null ){
					parseException = requestMessage.getParseException();
				}
				else{
					parseException = (ParseException) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
				}
				String msgErrore = parseException.getParseException().getMessage();
				if(msgErrore==null){
					msgErrore = parseException.getParseException().toString();
				}
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
				logCore.error("parsingExceptionRichiesta",parseException.getSourceException());
				msgDiag.logPersonalizzato("parsingExceptionRichiesta");
				try{
					responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_intestazione(context.getIdentitaPdD(),context.getTipoPorta(), context.getIdModulo(),
							ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.getErrore432_MessaggioRichiestaMalformato(parseException.getParseException()), 
							versioneSoap, openSPCoopProperties.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
				}catch(Exception eBuildError){
					logCore.error("Creazione del messaggio di fault 432 fallita",eBuildError);
					responseMessage = OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(versioneSoap, eBuildError);
				}
			}
			else if( (responseMessage!=null && responseMessage.getParseException() != null) ||
					(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION))){
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
				ParseException parseException = null;
				if( responseMessage!=null && responseMessage.getParseException() != null ){
					parseException = responseMessage.getParseException();
				}
				else{
					parseException = (ParseException) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
				}
				String msgErrore = parseException.getParseException().getMessage();
				if(msgErrore==null){
					msgErrore = parseException.getParseException().toString();
				}
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
				logCore.error("parsingExceptionRisposta",parseException.getSourceException());
				msgDiag.logPersonalizzato("parsingExceptionRisposta");
				try{
					responseMessage = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(),context.getTipoPorta(), context.getIdModulo(),
							ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.getErrore440_MessaggioRispostaMalformato(parseException.getParseException()), 
							versioneSoap, openSPCoopProperties.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
				}catch(Exception eBuildError){
					logCore.error("Creazione del messaggio di fault 440 fallita",eBuildError);
					responseMessage = OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(versioneSoap, eBuildError);
				}
			}
			
			try{
				// Se non sono stati recuperati i dati delle url, provo a recuperarli
				URLProtocolContext urlProtocolContext = context.getUrlProtocolContext();
				if(urlProtocolContext==null){
					urlProtocolContext = req.getURLProtocolContext();
				}
				if(urlProtocolContext!=null){
					String urlInvocazione = urlProtocolContext.getUrlInvocazione_formBased();
					if(urlProtocolContext.getFunction()!=null){
						urlInvocazione = "["+urlProtocolContext.getFunction()+"] "+urlInvocazione;
					}
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.URL_INVOCAZIONE, urlInvocazione);
				}
			}catch(Throwable t){}
			try{
				Credenziali credenziali = context.getCredenziali();
				if(credenziali==null){
					credenziali = req.getCredenziali();
				}
				if(credenziali!=null){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CREDENZIALI_INVOCAZIONE, credenziali.toString());
				}
			}catch(Throwable t){}
			
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
		DirectVMConnectorOutMessage vm = null;
		if(res instanceof DirectVMConnectorOutMessage){
			vm = (DirectVMConnectorOutMessage) res;
		}
		else if(req instanceof DumpRawConnectorOutMessage){
			if( ((DumpRawConnectorOutMessage)res).getWrappedConnectorOutMessage() instanceof DirectVMConnectorOutMessage ){
				vm = (DirectVMConnectorOutMessage) ((DumpRawConnectorOutMessage)res).getWrappedConnectorOutMessage();
			}
		}
		if(vm!=null){
			if(context!=null && context.getPddContext()!=null){
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
		
		InformazioniErroriInfrastrutturali informazioniErrori = ServletUtils.readInformazioniErroriInfrastrutturali(pddContext);
		
		OpenSPCoop2Message responseMessageError = null;
		SOAPBody body = null;
		EsitoTransazione esito = null;
		String descrizioneSoapFault = "";
		int statoServletResponse = 200;
		Throwable erroreConsegnaRisposta = null; 	
		boolean httpEmptyResponse = false;
		boolean erroreConnessioneClient = false;
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
				// ma in alcuni test della testsuite capita che non venga sollevata
				// nuovamente nei successivi accessi e venga serializzato un messaggio malformato. 
				// Faccio un check esplicito. In altre servlet non e' stato necessario.
								
				if(responseMessage.getParseException() != null)
					throw responseMessage.getParseException().getSourceException(); // viene gestito a modo dopo nel catch
				
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
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(), responseMessage, proprietaErroreAppl,informazioniErrori,
						(pddContext!=null ? pddContext.getContext() : null));
				boolean consume = true;
				if(body!=null && body.hasFault()){
					consume = false; // può essere usato nel post out response handler
					statoServletResponse = 500;
					descrizioneSoapFault = " ("+SoapUtils.toString(body.getFault(), false)+")";
				}
				res.setStatus(statoServletResponse);
				
				// Il contentLenght, nel caso di TransferLengthModes.CONTENT_LENGTH e' gia' stato calcolato
				// con una writeTo senza consume. Riuso il solito metodo per evitare differenze di serializzazione
				// e cambiare quindi il content length effettivo.
				if(TransferLengthModes.CONTENT_LENGTH.equals(openSPCoopProperties.getTransferLengthModes_ricezioneBuste())){
					res.sendResponse(responseMessage, false);
				} else {
					res.sendResponse(responseMessage, consume);
				}
				
			}else{
				statoServletResponse = protocolFactory.createProtocolManager().getHttpReturnCodeEmptyResponseOneWay();
				res.setStatus(statoServletResponse);
				httpEmptyResponse = true;
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(), responseMessage, proprietaErroreAppl,informazioniErrori,
						(pddContext!=null ? pddContext.getContext() : null));
				// carico-vuoto
			}
			
		}catch(Throwable e){
			// Errore nell'invio della risposta
			logCore.error("ErroreGenerale",e);
			erroreConsegnaRisposta = e;
			
			erroreConnessioneClient = ServletUtils.isConnessioneClientNonDisponibile(e);
			
			// Genero risposta con errore
			try{
				InformazioniErroriInfrastrutturali informazioniErrori_error = new InformazioniErroriInfrastrutturali();
				if( (responseMessage!=null && responseMessage.getParseException() != null) ||
						(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION))){
					informazioniErrori_error.setContenutoRispostaNonRiconosciuto(true);
					ParseException parseException = null;
					if( responseMessage!=null && responseMessage.getParseException() != null ){
						parseException = responseMessage.getParseException();
					}
					else{
						parseException = (ParseException) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
					}
					String msgErrore = parseException.getParseException().getMessage();
					if(msgErrore==null){
						msgErrore = parseException.getParseException().toString();
					}
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
					logCore.error("parsingExceptionRisposta",parseException.getSourceException());
					msgDiag.logPersonalizzato("parsingExceptionRisposta");
					responseMessageError = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(),context.getTipoPorta(), context.getIdModulo(),
							ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
							getErrore440_MessaggioRispostaMalformato(parseException.getParseException()), 
								versioneSoap, openSPCoopProperties.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
				} else {
					responseMessageError = protocolErroreBuilder.buildSoapFaultProtocollo_processamento(context.getIdentitaPdD(),context.getTipoPorta(), context.getIdModulo(),
							ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.getErrore426_ServletError(false, e), 
							versioneSoap, openSPCoopProperties.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
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
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(), responseMessageError, proprietaErroreAppl, informazioniErrori_error,
						(pddContext!=null ? pddContext.getContext() : null));
				if(body!=null && body.hasFault()){
					statoServletResponse = 500;
					res.setStatus(500);
					descrizioneSoapFault = " ("+SoapUtils.toString(body.getFault(), false)+")";
				}
				
				// Il contentLenght, nel caso di TransferLengthModes.CONTENT_LENGTH e' gia' stato calcolato
				// con una writeTo senza consume. Riuso il solito metodo per evitare differenze di serializzazione
				// e cambiare quindi il content length effettivo.
				if(TransferLengthModes.CONTENT_LENGTH.equals(openSPCoopProperties.getTransferLengthModes_ricezioneBuste())){
					res.sendResponse(responseMessageError, false);
				} else {
					//res.sendResponse(responseMessageError, true);
					res.sendResponse(responseMessageError, false); // può essere usato nel post out response handler
				}
			
			}catch(Exception error){
				
				if(!erroreConnessioneClient){
					erroreConnessioneClient = ServletUtils.isConnessioneClientNonDisponibile(error);
				}
				
				logCore.error("Generazione di un risposta errore non riuscita",error);
				statoServletResponse = 500;
				res.setStatus(500);
				try{
					responseMessageError = OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(versioneSoap, error);
					//res.sendResponse(responseMessageError, true);
					res.sendResponse(responseMessageError, false); // può essere usato nel post out response handler
				}catch(Exception  eError){
					if(!erroreConnessioneClient){
						erroreConnessioneClient = ServletUtils.isConnessioneClientNonDisponibile(eError);
					}
					try{
						res.sendResponse(error.toString().getBytes());
					}catch(Exception erroreStreamChiuso){ 
						erroreConnessioneClient = true;
						//se lo stream non e' piu' disponibile non si potra' consegnare alcuna risposta
					}
				}
				try{
					esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
				}catch(Exception eBuildError){
					esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
				}
			}
			
		}
		finally{
			
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
				
				erroreConnessioneClient = true;
				
				logCore.error("Chiusura stream non riuscita",e);
				
				// Risposta non ritornata al servizio applicativo, il socket verso il servizio applicativo era chiuso o cmq inutilizzabile
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, e.toString()); // NOTA: lasciare e.toString()
				
				msgDiag.logPersonalizzato("consegnaMessaggioFallita");
				
				erroreConsegnaRisposta = e;
				
				if(esito!=null){
					if(EsitoTransazioneName.OK.equals(esito.getName())){
						// non è ok, essendo andato in errore il flush
						try{
							esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
						}catch(Exception eBuildError){
							esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
						}
					}
				}
				else{
					// non dovrebbe mai essere null
				}
				
			}
			
			if(dumpRaw!=null){
				dumpRaw.serializeResponse(((DumpRawConnectorOutMessage)res));
			}
		}
		
		if(erroreConnessioneClient){
			// forzo esito errore connessione client
			try{
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_CONNESSIONE_CLIENT_NON_DISPONIBILE);
			}catch(Exception eBuildError){
				esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
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
				postOutResponseContext.getPddContext().addObject(CostantiPdD.DATA_INGRESSO_MESSAGGIO_RICHIESTA, dataIngressoMessaggio);
				postOutResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
				postOutResponseContext.setEsito(esito);
				postOutResponseContext.setReturnCode(statoServletResponse);
				postOutResponseContext.setPropertiesRispostaTrasporto(context.getHeaderIntegrazioneRisposta());
				postOutResponseContext.setProtocollo(context.getProtocol());
				postOutResponseContext.setIntegrazione(context.getIntegrazione());
				if(context.getTipoPorta()!=null)
					postOutResponseContext.setTipoPorta(context.getTipoPorta());	
				postOutResponseContext.setIdModulo(idModulo);
				
				if(requestMessage!=null){
					long incomingRequestMessageContentLength = requestMessage.getIncomingMessageContentLength();
					long outgoingRequestMessageContentLenght = requestMessage.getOutgoingMessageContentLength();
					if(incomingRequestMessageContentLength<0){
						int cl = req.getContentLength();
						if(cl>0){
							//System.out.println("HTTP");
							incomingRequestMessageContentLength = cl + 0l;
						}
						else{
							//System.out.println("FLUSH");
							// forzo la lettura del messaggio per impostare la dimensione della richiesta
							try{
								requestMessage.writeTo(new NullOutputStream(), true);
							}catch(Exception eFlush){}
							incomingRequestMessageContentLength = requestMessage.getIncomingMessageContentLength();
						}
					}
					postOutResponseContext.setInputRequestMessageSize(incomingRequestMessageContentLength);
					postOutResponseContext.setOutputRequestMessageSize(outgoingRequestMessageContentLenght);
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



