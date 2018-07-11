/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.services.service;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.xml.soap.SOAPBody;

import org.apache.commons.io.output.NullOutputStream;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.IConnettore;
import org.openspcoop2.pdd.core.connettori.RepositoryConnettori;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.handlers.PreInRequestContext;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.DirectVMProtocolInfo;
import org.openspcoop2.pdd.services.DumpRaw;
import org.openspcoop2.pdd.services.ServicesUtils;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherErrorInfo;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherUtils;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.RicezioneBusteConnector;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.ConnectorOutMessage;
import org.openspcoop2.pdd.services.connector.messages.DirectVMConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.DirectVMConnectorOutMessage;
import org.openspcoop2.pdd.services.connector.messages.DumpRawConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.DumpRawConnectorOutMessage;
import org.openspcoop2.pdd.services.core.RicezioneBuste;
import org.openspcoop2.pdd.services.core.RicezioneBusteContext;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.protocol.basic.registry.ServiceIdentificationReader;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.builder.InformazioniErroriInfrastrutturali;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;


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

public class RicezioneBusteService  {

	public static OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getMessageFactory();

	private RicezioneBusteExternalErrorGenerator generatoreErrore;
	
	public RicezioneBusteService(RicezioneBusteExternalErrorGenerator generatoreErrore){
		this.generatoreErrore = generatoreErrore;
	}
	

	public void process(ConnectorInMessage req, ConnectorOutMessage res) throws ConnectorException {
		
		// Timestamp
		Date dataAccettazioneRichiesta = DateManager.getDate();
		Date dataIngressoRichiesta = null;
		
		// IDModulo
		String idModulo = req.getIdModulo();
		IDService idModuloAsService = req.getIdModuloAsIDService();
		RequestInfo requestInfo = req.getRequestInfo();
		
		// Log
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = LoggerWrapperFactory.getLogger(idModulo);
				
		// GeneratoreErrore
		try{
			if(this.generatoreErrore==null){
				this.generatoreErrore = 
						new RicezioneBusteExternalErrorGenerator(logCore, RicezioneBusteConnector.ID_MODULO, requestInfo, null);
			}
		}catch(Exception e){
			String msg = "Inizializzazione Generatore Errore fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
			logCore.error(msg,e);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
					IntegrationError.INTERNAL_ERROR, e, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			RicezioneBusteServiceUtils.emitTransactionError(logCore, req, null, dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		//	Proprieta' OpenSPCoop
		OpenSPCoop2Properties openSPCoopProperties = OpenSPCoop2Properties.getInstance();
		if (openSPCoopProperties == null) {
			String msg = "Inizializzazione di OpenSPCoop non correttamente effettuata: OpenSPCoopProperties";
			logCore.error(msg);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore, 
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
					IntegrationError.INTERNAL_ERROR, null, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			RicezioneBusteServiceUtils.emitTransactionError(logCore, req, null, dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		// Configurazione Reader
		ConfigurazionePdDManager configPdDManager = null;
		try{
			configPdDManager = ConfigurazionePdDManager.getInstance();
			if(configPdDManager==null || configPdDManager.isInitializedConfigurazionePdDReader()==false){
				throw new Exception("ConfigurazionePdDManager not initialized");
			}
		}catch(Throwable e){
			String msg = "Inizializzazione di OpenSPCoop non correttamente effettuata: ConfigurazionePdDManager";
			logCore.error(msg);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore, 
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
					IntegrationError.INTERNAL_ERROR, e, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			RicezioneBusteServiceUtils.emitTransactionError(logCore, req, null, dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		// PddContext from servlet
		Object oPddContextFromServlet = req.getAttribute(CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP);
		PdDContext pddContextFromServlet = null;
		if(oPddContextFromServlet!=null){
			pddContextFromServlet = (PdDContext) oPddContextFromServlet;
		}
		
		// Configurazione Reader
		DumpRaw dumpRaw = null;
		try{
			if(configPdDManager.dumpBinarioPA()){
				dumpRaw = new DumpRaw(logCore, requestInfo.getIdentitaPdD(), idModulo, TipoPdD.APPLICATIVA);
				req = new DumpRawConnectorInMessage(logCore, req);
				res = new DumpRawConnectorOutMessage(logCore, res);
			}
		}catch(Throwable e){
			String msg = "Inizializzazione di OpenSPCoop non correttamente effettuata: DumpRaw";
			logCore.error(msg);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore, 
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
					IntegrationError.INTERNAL_ERROR, e, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			RicezioneBusteServiceUtils.emitTransactionError(logCore, req, pddContextFromServlet, dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		// Identifico Servizio per comprendere correttamente il messageType
		ServiceIdentificationReader serviceIdentificationReader = null;
		try{
			serviceIdentificationReader = ServicesUtils.getServiceIdentificationReader(logCore, requestInfo);
		}catch(Exception e){
			String msg = "Inizializzazione RegistryReader fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
			logCore.error(msg,e);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA),
					IntegrationError.INTERNAL_ERROR, e, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			RicezioneBusteServiceUtils.emitTransactionError(logCore, req, pddContextFromServlet, dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		// Provo a creare un context (per l'id di transazione nei diagnostici)
		RicezioneBusteContext context = null;
		IProtocolFactory<?> protocolFactory = null;
		try {
			context = new RicezioneBusteContext(idModuloAsService, dataAccettazioneRichiesta,requestInfo);
			protocolFactory = req.getProtocolFactory();
			if(openSPCoopProperties.isTransazioniEnabled()) {
				TransactionContext.createTransaction((String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
			}
		}catch(Throwable e) {
			context = null;
			protocolFactory = null;
			// non loggo l'errore tanto poi provo a ricreare il context subito dopo e li verra' registrato l'errore
		}
		
		// Logger dei messaggi diagnostici
		String nomePorta = requestInfo.getProtocolContext().getInterfaceName();
		MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(TipoPdD.APPLICATIVA,idModulo,nomePorta);
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
		if(context!=null && protocolFactory!=null) {
			msgDiag.setPddContext(context.getPddContext(), protocolFactory);
		}
		
		// Aggiorno RequestInfo
		ConnectorDispatcherErrorInfo cInfo = RicezioneBusteServiceUtils.updatePortaApplicativaRequestInfo(requestInfo, logCore, res,
				this.generatoreErrore, serviceIdentificationReader,msgDiag);
		if(cInfo!=null){
			RicezioneBusteServiceUtils.emitTransactionError(context, logCore, req, pddContextFromServlet, dataAccettazioneRichiesta, cInfo);
			return; // l'errore in response viene impostato direttamente dentro il metodo
		}
		req.updateRequestInfo(requestInfo);
		
		
		
		
		
		

		
		
		
		
		
		
		
		/* ------------  Lettura parametri della richiesta ------------- */
		
		
		//	Risposta Soap
		OpenSPCoop2Message responseMessage = null;

		// Proprieta errore applicativo
		ProprietaErroreApplicativo proprietaErroreAppl = null;

		// PostOutResponseContext
		PostOutResponseContext postOutResponseContext = null;

		PdDContext pddContext = null;
		OpenSPCoop2Message requestMessage = null;
		
		
		String protocol = null;
		MessageType messageTypeReq = null;
		ServiceBinding protocolServiceBinding = null;
		try{
			
			/* --------------- Creo il context che genera l'id univoco ----------------------- */
			
			if(protocolFactory==null) {
				protocolFactory = req.getProtocolFactory();
			}
			protocol = protocolFactory.getProtocol();
			
			protocolServiceBinding = requestInfo.getProtocolServiceBinding();
			
			proprietaErroreAppl = openSPCoopProperties.getProprietaGestioneErrorePD(protocolFactory.createProtocolManager());
			proprietaErroreAppl.setDominio(openSPCoopProperties.getIdentificativoPortaDefault(protocolFactory.getProtocol()));
			proprietaErroreAppl.setIdModulo(idModulo);
			
			if(context==null) {
				context = new RicezioneBusteContext(idModuloAsService, dataAccettazioneRichiesta,requestInfo);
			}
			context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME, protocolFactory.getProtocol());
			context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO, req.getRequestInfo());
			context.setTipoPorta(TipoPdD.APPLICATIVA);
			context.setIdModulo(idModulo);
			msgDiag.setPddContext(context.getPddContext(), protocolFactory);
			pddContext = context.getPddContext();
			
			try{
				if(openSPCoopProperties.isTransazioniEnabled()) {
					// NOTA: se gia' esiste con l'id di transazione, non viene ricreata
					TransactionContext.createTransaction((String)pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
				}
			}catch(Exception e){
				logCore.error("Errore durante la creazione della transazione",e);
			}
			
			try{
				msgDiag.logPersonalizzato("ricezioneRichiesta.firstLog");
			}catch(Exception e){
				logCore.error("Errore generazione diagnostico di ingresso",e);
			}
			
			if(dumpRaw!=null){
				dumpRaw.setPddContext(msgDiag.getPorta(), context.getPddContext());
				dumpRaw.serializeContext(context, protocol);
			}
			
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
			preInRequestContext.setRequestInfo(requestInfo);
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
				dataIngressoRichiesta = req.getDataIngressoRichiesta();
				context.setDataIngressoRichiesta(dataIngressoRichiesta);
			}
			
			

			
			
			
			
			
			/* ------------ Controllo ContentType -------------------- */
			
			msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.tipologiaMessaggio");
			
			messageTypeReq = requestInfo.getProtocolRequestMessageType();
			if(ServiceBinding.SOAP.equals(protocolServiceBinding) && messageTypeReq!=null){
				msgDiag.addKeyword(CostantiPdD.KEY_SOAP_VERSION, messageTypeReq.getMessageVersionAsString());
				msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE_ATTESO, SoapUtils.getSoapEnvelopeNS(messageTypeReq));
			}
			
			String contentTypeReq = req.getContentType();
			boolean contentTypeSupportato = messageTypeReq!=null;
			msgDiag.addKeyword(CostantiPdD.KEY_CONTENT_TYPES_ATTESI, 
					requestInfo.getBindingConfig().getContentTypesSupportedAsString(protocolServiceBinding, 
							MessageRole.REQUEST,requestInfo.getProtocolContext()));
			List<String> supportedContentTypes = requestInfo.getBindingConfig().getContentTypesSupported(protocolServiceBinding, 
					MessageRole.REQUEST,requestInfo.getProtocolContext()); 
			msgDiag.addKeyword(CostantiPdD.KEY_HTTP_HEADER, contentTypeReq);
			
			if(ServiceBinding.SOAP.equals(protocolServiceBinding)){	
				if(openSPCoopProperties.isControlloContentTypeAbilitatoRicezioneBuste() == false){
					if(!contentTypeSupportato){
						if(HttpConstants.CONTENT_TYPE_NON_PRESENTE.equals(contentTypeReq)){
							msgDiag.logPersonalizzato("contentType.notDefined");
						}else{
							msgDiag.logPersonalizzato("contentType.unsupported");
						}
						messageTypeReq = MessageType.SOAP_11;
						contentTypeReq = SoapUtils.getSoapContentTypeForMessageWithoutAttachments(messageTypeReq); // Forzo text/xml;
						logCore.warn("Content-Type non supportato, viene utilizzato forzatamente il tipo: "+contentTypeReq);
						contentTypeSupportato = true;
					}
				}
			}
			if(!contentTypeSupportato){
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				
				// Leggo content Type dall'header HTTP per capire se l'header proprio non esiste o se e' il valore errato.
				if(HttpConstants.CONTENT_TYPE_NON_PRESENTE.equals(contentTypeReq)){
					//ContentType del messaggio non presente
					msgDiag.logPersonalizzato("contentType.notDefined");
					responseMessage = this.generatoreErrore.buildErroreProcessamento(IntegrationError.BAD_REQUEST,
							ErroriIntegrazione.ERRORE_433_CONTENT_TYPE_NON_PRESENTE.getErrore433_ContentTypeNonPresente(supportedContentTypes));	
				}	
				else{
					//ContentType del messaggio non supportato
					msgDiag.logPersonalizzato("contentType.unsupported");
					responseMessage = this.generatoreErrore.buildErroreProcessamento(IntegrationError.BAD_REQUEST,
							ErroriIntegrazione.ERRORE_429_CONTENT_TYPE_NON_SUPPORTATO.getErrore429_ContentTypeNonSupportato(contentTypeReq,supportedContentTypes));	
				}
			}
			else{
				/* ------------  SoapAction check 1 (controlla che non sia null e ne ritorna il valore) ------------- */			
				String soapAction = null;
				try{
					if(ServiceBinding.SOAP.equals(protocolServiceBinding)){
						soapAction = req.getSOAPAction();
					}
				}catch(Exception e){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					throw e;
				}
				
				/* ------------ Costruzione Messaggio -------------------- */
				msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.inCorso");
				Utilities.printFreeMemory("RicezioneBuste - Pre costruzione richiesta");
				OpenSPCoop2MessageParseResult pr = req.getRequest(notifierInputStreamParams);
				if(pr.getParseException()!=null){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
				}
				requestMessage = pr.getMessage_throwParseException();
				dataIngressoRichiesta = req.getDataIngressoRichiesta();
				context.setDataIngressoRichiesta(dataIngressoRichiesta);
				Utilities.printFreeMemory("RicezioneBuste - Post costruzione richiesta");
				requestMessage.setProtocolName(protocolFactory.getProtocol());
				requestMessage.addContextProperty(org.openspcoop2.core.constants.Costanti.REQUEST_INFO,requestInfo); // serve nelle comunicazione non stateless (es. riscontro salvato) per poterlo rispedire
				requestMessage.addContextProperty(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE,pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)); // serve nelle comunicazione non stateless (es. riscontro salvato) per poterlo rispedire
								
				/* ------------ Controllo MustUnderstand -------------------- */
				String mustUnderstandError = null;
				try{
					if(ServiceBinding.SOAP.equals(protocolServiceBinding)){
						mustUnderstandError = ServicesUtils.checkMustUnderstand(requestMessage.castAsSoap(),protocolFactory);
					}
				}catch(Exception e){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					throw e;
				}
				
				/* ------------ Controllo Soap namespace -------------------- */
				String soapEnvelopeNamespaceVersionMismatch = null;
				try{
					if(ServiceBinding.SOAP.equals(protocolServiceBinding)){
						soapEnvelopeNamespaceVersionMismatch = ServicesUtils.checkSOAPEnvelopeNamespace(requestMessage.castAsSoap(), messageTypeReq);
					}
				}catch(Exception e){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					throw e;
				}
	
				/* ------------  SoapAction check 2 ------------- */
				if(soapAction!=null){
					if(openSPCoopProperties.checkSoapActionQuotedString_ricezioneBuste()){
						try{
							SoapUtils.checkSoapActionQuotedString(soapAction, messageTypeReq);
						}catch(Exception e){
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
							throw e;
						}
					}
				}

								
				/* ------------  Elaborazione ------------- */
				
				if(mustUnderstandError==null && soapEnvelopeNamespaceVersionMismatch==null){
					
					// Contesto di Richiesta
					context.setCredenziali(new Credenziali(req.getCredential()));
					context.setGestioneRisposta(true); // siamo in una servlet, la risposta deve essere aspettata
					context.setMessageRequest(requestMessage);
					context.setTracciamentoAbilitato(true);
					context.setUrlProtocolContext(requestInfo.getProtocolContext());
					context.setMsgDiagnostico(msgDiag);
	
					// Log elaborazione dati completata
					msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.completata");
			
					// Invocazione...
					RicezioneBuste gestoreRichiesta = new RicezioneBuste(context, this.generatoreErrore);
					gestoreRichiesta.process(req);
					responseMessage = context.getMessageResponse();
				}	
				else{
					if(mustUnderstandError!=null){
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, mustUnderstandError);
						msgDiag.logPersonalizzato("mustUnderstand.unknown");
						responseMessage = this.generatoreErrore.buildErroreProcessamento(IntegrationError.BAD_REQUEST,
								ErroriIntegrazione.ERRORE_427_MUSTUNDERSTAND_ERROR.getErrore427_MustUnderstandHeaders(mustUnderstandError));
					}
					else{
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
						msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE, soapEnvelopeNamespaceVersionMismatch);
						msgDiag.logPersonalizzato("soapEnvelopeNamespace.versionMismatch");
						responseMessage = this.generatoreErrore.buildErroreProcessamento(IntegrationError.BAD_REQUEST,
								ErroriIntegrazione.ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR.
									getErrore430_SoapNamespaceNonSupportato(messageTypeReq, soapEnvelopeNamespaceVersionMismatch));
					}
				}
			}
				
		} catch (Throwable e) {
			
			if(context==null){
				// Errore durante la generazione dell'id
				context = RicezioneBusteContext.newRicezioneBusteContext(idModuloAsService,dataAccettazioneRichiesta,requestInfo);
				context.setDataIngressoRichiesta(dataIngressoRichiesta);
				context.setTipoPorta(TipoPdD.APPLICATIVA);
				context.setIdModulo(idModulo);
				context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME, protocolFactory.getProtocol());
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
				tParsing = ParseExceptionUtils.getParseException(e);
			}
			
			// Genero risposta con errore
			String msgErrore = e.getMessage();
			if(msgErrore==null){
				msgErrore = e.toString();
			}
				
			// Ricerca org.xml.sax.SAXParseException || com.ctc.wstx.exc.WstxParsingException ||  com.ctc.wstx.exc.WstxUnexpectedCharException
			if(	ServiceBinding.SOAP.equals(protocolServiceBinding) &&
					messageTypeReq!=null &&
					(
					
						// Messaggio lanciato dallo streaming engine
						msgErrore.equals("Transport level information does not match with SOAP Message namespace URI") ||
						// ?
						msgErrore.equals("I dati ricevuti non rappresentano un messaggio SOAP 1.1 valido: ") ||
						// I seguenti due errori invece vengono lanciati dalle classi 'com/sun/xml/messaging/saaj/soap/ver1_2/SOAPPart1_2Impl.java' e com/sun/xml/messaging/saaj/soap/ver1_1/SOAPPart1_1Impl.java
						// Proprio nel caso il namespace non corrisponde al tipo atteso.
						msgErrore.equals("InputStream does not represent a valid SOAP 1.1 Message") || 
						msgErrore.equals("InputStream does not represent a valid SOAP 1.2 Message")
					)
				){
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE, "Impossibile recuperare il valore del namespace");
				msgDiag.logPersonalizzato("soapEnvelopeNamespace.versionMismatch");
				responseMessage = this.generatoreErrore.buildErroreProcessamento(IntegrationError.BAD_REQUEST,
						ErroriIntegrazione.ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR.
							getErrore430_SoapNamespaceNonSupportato(messageTypeReq,  "Impossibile recuperare il valore del namespace"));
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
				responseMessage = this.generatoreErrore.buildErroreIntestazione(IntegrationError.BAD_REQUEST,
						ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.getErrore432_MessaggioRichiestaMalformato(tParsing));
			}
			else if (e instanceof HandlerException) {
				logCore.error("ErroreGenerale (HandlerException)",e);
				HandlerException he = (HandlerException) e;
				if(he.isEmettiDiagnostico()) {
					msgDiag.logErroreGenerico(e, "Generale(richiesta-handler)");
				}
				ErroreIntegrazione errore = he.convertToErroreIntegrazione();
				if(errore==null) {
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Generale(richiesta)");
				}
				responseMessage = this.generatoreErrore.buildErroreProcessamento(IntegrationError.BAD_REQUEST,errore,e);
				he.customized(responseMessage);
			}
			else{
				logCore.error("ErroreGenerale",e);
				msgDiag.logErroreGenerico(e, "Generale(richiesta)");
				responseMessage = this.generatoreErrore.buildErroreProcessamento(IntegrationError.BAD_REQUEST,
						ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.getErrore426_ServletError(true, e), e);
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
				responseMessage =this.generatoreErrore.buildErroreIntestazione(IntegrationError.BAD_REQUEST,
							ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.getErrore432_MessaggioRichiestaMalformato(parseException.getParseException()));
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
				responseMessage = this.generatoreErrore.buildErroreProcessamento(IntegrationError.BAD_REQUEST,
						ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.getErrore440_MessaggioRispostaMalformato(parseException.getParseException()));
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
					credenziali = new Credenziali(req.getCredential());
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


		
		// Imposto risposta

		Date dataPrimaSpedizioneRisposta = DateManager.getDate();

		if(context.getMsgDiagnostico()!=null){
			msgDiag = context.getMsgDiagnostico();
		}
		if(context.getHeaderIntegrazioneRisposta()==null) {
			context.setHeaderIntegrazioneRisposta(new Properties());
		}
		ServicesUtils.setGovWayHeaderResponse(context.getHeaderIntegrazioneRisposta(), logCore, false, context.getPddContext());
		if(context.getHeaderIntegrazioneRisposta()!=null){
			java.util.Enumeration<?> en = context.getHeaderIntegrazioneRisposta().keys();
	    	while(en.hasMoreElements()){
	    		String key = (String) en.nextElement();
	    		res.setHeader(key,context.getHeaderIntegrazioneRisposta().getProperty(key));
	    	}	
		}
		if(context!=null && context.getProtocol()!=null){
			
			this.generatoreErrore.updateDominio(context.getIdentitaPdD());

			IDServizio idServizio = null;
			try{
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(context.getProtocol().getTipoServizio(), 
						context.getProtocol().getServizio(), 
						context.getProtocol().getErogatore(), 
						context.getProtocol().getVersioneServizio());
			}catch(Exception e){ 
				// non dovrebbe succedere eccezione}
			}
			if(idServizio!=null){
				idServizio.setAzione(context.getProtocol().getAzione());
				this.generatoreErrore.updateInformazioniCooperazione(context.getProtocol().getFruitore(), idServizio);
			}
			
			String servizioApplicativo = null;
			if(context.getIntegrazione()!=null){
				servizioApplicativo = context.getIntegrazione().getServizioApplicativoFruitore();
			}
			this.generatoreErrore.updateInformazioniCooperazione(servizioApplicativo);
			
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
				Object oIdTransazione = context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
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
		
		InformazioniErroriInfrastrutturali informazioniErrori = ServicesUtils.readInformazioniErroriInfrastrutturali(pddContext);
		
		OpenSPCoop2Message responseMessageError = null;
		EsitoTransazione esito = null;
		String descrizioneSoapFault = "";
		int statoServletResponse = 200;
		Throwable erroreConsegnaRisposta = null; 	
		boolean httpEmptyResponse = false;
		boolean erroreConnessioneClient = false;
		try{

			// Invio la risposta
			if(responseMessage!=null && !responseMessage.isForcedEmptyResponse() && (responseMessage.getForcedResponse()==null)){
					
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
				ServicesUtils.setTransferLength(openSPCoopProperties.getTransferLengthModes_ricezioneBuste(), 
						req, res, responseMessage);
				
				//NOTA: Faccio la save per refreshare il ContentType. Necessario nel caso di attachment 
				// (implementazione SAAJ della SUN)
    			responseMessage.updateContentType();
				
				// content type
    			ServicesUtils.setContentType(responseMessage, res);

				// http status
				boolean consume = true;
				if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding()) ){
					SOAPBody body = responseMessage.castAsSoap().getSOAPBody();
					if(body!=null && body.hasFault()){
						consume = false; // può essere usato nel post out response handler
						statoServletResponse = 500;
						descrizioneSoapFault = " ("+SoapUtils.toString(body.getFault(), false)+")";
					}
					else if(statoServletResponse==500) {
						// in SOAP 500 deve essere associato con un fault
						if(body!=null && SoapUtils.getFirstNotEmptyChildNode(body, false)!=null) {
							statoServletResponse = 200;
						}
						else {
							statoServletResponse = protocolFactory.createProtocolManager().getHttpReturnCodeEmptyResponseOneWay();
						}
					}
				}
				else if(responseMessage.castAsRest().isProblemDetailsForHttpApis_RFC7808() || 
						(MessageRole.FAULT.equals(responseMessage.getMessageRole()) &&
							(
							MessageType.XML.equals(responseMessage.getMessageType()) 
									|| 
							MessageType.JSON.equals(responseMessage.getMessageType())
							)
						)
					) {
					consume = false; // può essere usato nel post out response handler
				}
				res.setStatus(statoServletResponse);
				
				// esito calcolato prima del sendResponse, per non consumare il messaggio
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(), 
						statoServletResponse, requestInfo.getProtocolServiceBinding(),
						responseMessage, proprietaErroreAppl,informazioniErrori,
						(pddContext!=null ? pddContext.getContext() : null));
				
				// Il contentLenght, nel caso di TransferLengthModes.CONTENT_LENGTH e' gia' stato calcolato
				// con una writeTo senza consume. Riuso il solito metodo per evitare differenze di serializzazione
				// e cambiare quindi il content length effettivo.
				if(TransferLengthModes.CONTENT_LENGTH.equals(openSPCoopProperties.getTransferLengthModes_ricezioneBuste())){
					res.sendResponse(responseMessage, false);
				} else {
					res.sendResponse(responseMessage, consume);
				}
				
			}
			else if(responseMessage!=null && responseMessage.getForcedResponse()!=null) {
				byte[]response = responseMessage.getForcedResponse().getContent();
//				if(response==null) {
//					throw new Exception("Trovata configurazione 'forcedResponse' senza una vera risposta");
//				}
			
				if(response!=null && response.length<1024) {
					// Se il messaggio non è troppo grande lo aggiungo al diagnostico
					try {
						descrizioneSoapFault = "("+new String(response)+")";
					}catch(Throwable t) {
						descrizioneSoapFault = "";
					}
				}
				
				if(responseMessage.getForcedResponse().getHeaders()!=null &&
						responseMessage.getForcedResponse().getHeaders().size()>0) {
					java.util.Enumeration<?> en = responseMessage.getForcedResponse().getHeaders().keys();
			    	while(en.hasMoreElements()){
			    		String key = (String) en.nextElement();
			    		String value = null;
			    		try{
			    			value = responseMessage.getForcedResponse().getHeaders().getProperty(key);
			    			res.setHeader(key,value);
			    		}catch(Exception e){
			    			logCore.error("Response(Forced).setHeader("+key+","+value+") error: "+e.getMessage(),e);
			    		}
			    	}	
				}
				
				if(responseMessage.getForcedResponse().getContentType()!=null) {
					res.setContentType(responseMessage.getForcedResponse().getContentType());
				}
				
				if(responseMessage.getForcedResponse().getResponseCode()!=null) {
					try{
						statoServletResponse = Integer.parseInt(responseMessage.getForcedResponse().getResponseCode());
					}catch(Exception e){}
				}
				else if(responseMessage!=null && responseMessage.getForcedResponseCode()!=null) {
					try{
						statoServletResponse = Integer.parseInt(responseMessage.getForcedResponseCode());
					}catch(Exception e){}
				}
				res.setStatus(statoServletResponse);
				
				// esito calcolato prima del sendResponse, per non consumare il messaggio
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(), 
						statoServletResponse, requestInfo.getProtocolServiceBinding(),
						responseMessage, proprietaErroreAppl,informazioniErrori,
						(pddContext!=null ? pddContext.getContext() : null));
				
				if(response!=null) {
					res.sendResponse(response);
				}
				
			}
			else{
				if(responseMessage!=null && responseMessage.getForcedResponseCode()!=null) {
					try{
						statoServletResponse = Integer.parseInt(responseMessage.getForcedResponseCode());
					}catch(Exception e){}
				}
				else {
					statoServletResponse = protocolFactory.createProtocolManager().getHttpReturnCodeEmptyResponseOneWay();
				}
				res.setStatus(statoServletResponse);
				httpEmptyResponse = true;
				// carico-vuoto
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(), 
						statoServletResponse, requestInfo.getProtocolServiceBinding(),
						responseMessage, proprietaErroreAppl,informazioniErrori,
						(pddContext!=null ? pddContext.getContext() : null));
			}
			
		}catch(Throwable e){
			// Errore nell'invio della risposta
			logCore.error("ErroreGenerale",e);
			erroreConsegnaRisposta = e;
			
			erroreConnessioneClient = ServicesUtils.isConnessioneClientNonDisponibile(e);
			
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
					responseMessageError = this.generatoreErrore.buildErroreProcessamento(IntegrationError.INTERNAL_ERROR,
							ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
							getErrore440_MessaggioRispostaMalformato(parseException.getParseException()));
				} else {
					responseMessageError = this.generatoreErrore.buildErroreProcessamento(IntegrationError.INTERNAL_ERROR,
							ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.getErrore426_ServletError(false, e));
				}
				// transfer length
				ServicesUtils.setTransferLength(openSPCoopProperties.getTransferLengthModes_ricezioneBuste(), 
						req, res, responseMessageError);
								
				// content type
    			ServicesUtils.setContentType(responseMessageError, res);
				
				// http status (puo' essere 200 se il msg di errore e' un msg errore applicativo cnipa non in un soap fault)
				if(ServiceBinding.SOAP.equals(responseMessageError.getServiceBinding()) ){
					SOAPBody body = responseMessageError.castAsSoap().getSOAPBody();
					if(body!=null && body.hasFault()){
						statoServletResponse = 500;
						res.setStatus(statoServletResponse);
						descrizioneSoapFault = " ("+SoapUtils.toString(body.getFault(), false)+")";
					}
				}
				
				// esito calcolato prima del sendResponse, per non consumare il messaggio
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(), 
						statoServletResponse, requestInfo.getProtocolServiceBinding(),
						responseMessageError, proprietaErroreAppl, informazioniErrori_error,
						(pddContext!=null ? pddContext.getContext() : null));
				
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
					erroreConnessioneClient = ServicesUtils.isConnessioneClientNonDisponibile(error);
				}
				
				logCore.error("Generazione di un risposta errore non riuscita",error);
				statoServletResponse = 500;
				res.setStatus(500);
				try{
					responseMessageError = this.generatoreErrore.buildFault(error);
					//res.sendResponse(responseMessageError, true);
					res.sendResponse(responseMessageError, false); // può essere usato nel post out response handler
				}catch(Exception  eError){
					if(!erroreConnessioneClient){
						erroreConnessioneClient = ServicesUtils.isConnessioneClientNonDisponibile(eError);
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
						if(statoServletResponse>=300)
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
				postOutResponseContext.getPddContext().addObject(CostantiPdD.DATA_ACCETTAZIONE_RICHIESTA, dataAccettazioneRichiesta);
				if(dataIngressoRichiesta!=null){
					postOutResponseContext.getPddContext().addObject(CostantiPdD.DATA_INGRESSO_RICHIESTA, dataIngressoRichiesta);
				}
				postOutResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
				postOutResponseContext.setDataPrimaSpedizioneRisposta(dataPrimaSpedizioneRisposta);
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
						if(responseMessage!=null && !responseMessage.isForcedEmptyResponse() && responseMessage.getForcedResponse()==null){
							postOutResponseContext.setInputResponseMessageSize(responseMessage.getIncomingMessageContentLength());
							postOutResponseContext.setOutputResponseMessageSize(responseMessage.getOutgoingMessageContentLength());
							postOutResponseContext.setMessaggio(responseMessage);
						}
					}
					postOutResponseContext.setErroreConsegna(erroreConsegnaRisposta.toString()); // NOTA: lasciare e.toString()
				}
				else if(responseMessage!=null && !responseMessage.isForcedEmptyResponse() && responseMessage.getForcedResponse()==null){
					postOutResponseContext.setInputResponseMessageSize(responseMessage.getIncomingMessageContentLength());
					postOutResponseContext.setOutputResponseMessageSize(responseMessage.getOutgoingMessageContentLength());
					postOutResponseContext.setMessaggio(responseMessage);
				}
				else if(responseMessage!=null && responseMessage.getForcedResponse()!=null &&
						responseMessage.getForcedResponse().getContent()!=null) {
					postOutResponseContext.setInputResponseMessageSize(responseMessage.getIncomingMessageContentLength());
					postOutResponseContext.setOutputResponseMessageSize((long) responseMessage.getForcedResponse().getContent().length);
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
		responseMessage = null;
		responseMessageError = null;
		// *** GB ***
		
		return;
	}


}



