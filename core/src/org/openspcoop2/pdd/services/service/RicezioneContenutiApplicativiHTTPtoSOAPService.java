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



package org.openspcoop2.pdd.services.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreRichieste;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.IConnettore;
import org.openspcoop2.pdd.core.connettori.RepositoryConnettori;
import org.openspcoop2.pdd.core.controllo_traffico.SogliaDimensioneMessaggio;
import org.openspcoop2.pdd.core.controllo_traffico.SoglieDimensioneMessaggi;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.handlers.PreInAcceptRequestContext;
import org.openspcoop2.pdd.core.handlers.PreInRequestContext;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.DirectVMProtocolInfo;
import org.openspcoop2.pdd.services.DumpRaw;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.ServicesUtils;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherErrorInfo;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherInfo;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherUtils;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.RicezioneContenutiApplicativiHTTPtoSOAPConnector;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.ConnectorOutMessage;
import org.openspcoop2.pdd.services.connector.messages.DirectVMConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.DirectVMConnectorOutMessage;
import org.openspcoop2.pdd.services.connector.messages.DumpRawConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.DumpRawConnectorOutMessage;
import org.openspcoop2.pdd.services.core.RicezioneContenutiApplicativi;
import org.openspcoop2.pdd.services.core.RicezioneContenutiApplicativiContext;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.protocol.basic.registry.ServiceIdentificationReader;
import org.openspcoop2.protocol.engine.SecurityTokenUtilities;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.builder.InformazioniErroriInfrastrutturali;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.LimitExceededIOException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TimeoutIOException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**
 * Servlet che serve per creare un tunnel da un servizio che non conosce SOAP verso OpenSPCoop (che utilizza SOAP)
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RicezioneContenutiApplicativiHTTPtoSOAPService  {

	private RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore;
	
	public static void forceXmlResponse(RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore) {
		generatoreErrore.setForceMessageTypeResponse(MessageType.XML); // forzo xml
		if(generatoreErrore.getProprietaErroreAppl()!=null) {
			generatoreErrore.getProprietaErroreAppl().setFaultAsXML(true); // siamo in una richiesta http senza SOAP, un SoapFault non ha senso
		}
	}
	
	public RicezioneContenutiApplicativiHTTPtoSOAPService(RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore){
		this.generatoreErrore = generatoreErrore;
		RicezioneContenutiApplicativiHTTPtoSOAPService.forceXmlResponse(this.generatoreErrore);
	}
	
	public void process(ConnectorInMessage req, ConnectorOutMessage res, Date dataAccettazioneRichiesta) throws ConnectorException {
		
		// Timestamp
		Date dataIngressoRichiesta = null;
		
		// IDModulo
		String idModulo = req.getIdModulo();
		IDService idModuloAsService = req.getIdModuloAsIDService();
		RequestInfo requestInfo = req.getRequestInfo();

		// Log
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = LoggerWrapperFactory.getLogger(idModulo);

		OpenSPCoop2Properties openSPCoopProperties = OpenSPCoop2Properties.getInstance();
		

		/* ------------  PreInHandler (PreInAcceptRequestContext) ------------- */
		
		PreInAcceptRequestContext preInAcceptRequestContext = null;
		if (openSPCoopProperties != null && OpenSPCoop2Startup.initialize) {
			
			// build context
			preInAcceptRequestContext = new PreInAcceptRequestContext();
			preInAcceptRequestContext.setTipoPorta(TipoPdD.DELEGATA);
			preInAcceptRequestContext.setIdModulo(idModulo);
			preInAcceptRequestContext.setRequestInfo(requestInfo);	
			preInAcceptRequestContext.setLogCore(logCore);
			
			// valori che verranno aggiornati dopo
			try {
				if(openSPCoopProperties.isConnettoriUseLimitedInputStream()) {
					SogliaDimensioneMessaggio soglia = new SogliaDimensioneMessaggio();
					soglia.setSogliaKb(openSPCoopProperties.getLimitedInputStreamThresholdKb());
					soglia.setPolicyGlobale(true);
					soglia.setNomePolicy("GovWayCore");
					soglia.setIdPolicyConGruppo("GovWayCore");
					req.setRequestLimitedStream(soglia);
				}
				if(openSPCoopProperties.isConnettoriUseTimeoutInputStream()) {
					req.setRequestReadTimeout(openSPCoopProperties.getReadConnectionTimeout_ricezioneContenutiApplicativi());
				}
				req.setThresholdContext(null, 
					openSPCoopProperties.getDumpBinario_inMemoryThreshold(), openSPCoopProperties.getDumpBinario_repository());
			}catch(Throwable t) {
				logCore.error(t.getMessage(),t);
			}
			preInAcceptRequestContext.setReq(req);
			
			// invocazione handler
			GestoreHandlers.preInRequest(preInAcceptRequestContext, logCore, logCore);
			
		}
		
		
		// GeneratoreErrore
		try{
			if(this.generatoreErrore==null){
				this.generatoreErrore = 
						new RicezioneContenutiApplicativiInternalErrorGenerator(logCore, RicezioneContenutiApplicativiHTTPtoSOAPConnector.ID_MODULO, requestInfo);
				this.generatoreErrore.getProprietaErroreAppl().setFaultAsXML(true); // siamo in una richiesta http senza SOAP, un SoapFault non ha senso
			}
		}catch(Exception e){
			String msg = "Inizializzazione Generatore Errore fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
			logCore.error(msg,e);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
					IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, e, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(logCore, req, null, dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		//	Proprieta' OpenSPCoop
		if (!OpenSPCoop2Startup.initialize || openSPCoopProperties == null) {
			String msg = "Inizializzazione di GovWay non correttamente effettuata: OpenSPCoopProperties";
			if(!OpenSPCoop2Startup.initialize) {
				msg = "Inizializzazione di GovWay non correttamente effettuata";
			}
			logCore.error(msg);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore, 
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
						IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, null, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(logCore, req, null, dataAccettazioneRichiesta, cInfo);
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
			String msg = "Inizializzazione di GovWay non correttamente effettuata: ConfigurazionePdDManager";
			logCore.error(msg);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore, 
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
						IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, e, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(logCore, req, null, dataAccettazioneRichiesta, cInfo);
			return;
		}
			
		// PddContext from servlet
		Object oPddContextFromServlet = null;
		try{
			oPddContextFromServlet = req.getAttribute(CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP.getValue());
		}catch(Exception e){
			logCore.error("req.getAttribute("+CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP+") error: "+e.getMessage(),e);
		}
		PdDContext pddContextFromServlet = null;
		if(oPddContextFromServlet!=null){
			pddContextFromServlet = (PdDContext) oPddContextFromServlet;
		}
					
		// Identifico Servizio per comprendere correttamente il messageType
		ServiceIdentificationReader serviceIdentificationReader = null;
		try{
			serviceIdentificationReader = ServicesUtils.getServiceIdentificationReader(logCore, requestInfo,
					configPdDManager.getRegistroServiziManager(), configPdDManager);
		}catch(Exception e){
			String msg = "Inizializzazione RegistryReader fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
			logCore.error(msg,e);
			ConnectorDispatcherErrorInfo cInfo =  ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA),
						IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, e, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(logCore, req, pddContextFromServlet, dataAccettazioneRichiesta, cInfo);
			return;
		}
	
		// Provo a creare un context (per l'id di transazione nei diagnostici)
		RicezioneContenutiApplicativiContext context = null;
		IProtocolFactory<?> protocolFactory = null;
		String idTransazione = null;
		try {
			context = new RicezioneContenutiApplicativiContext(idModuloAsService,dataAccettazioneRichiesta,requestInfo);
			protocolFactory = req.getProtocolFactory();
			idTransazione = (String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		}catch(Throwable e) {
			context = null;
			protocolFactory = null;
			// non loggo l'errore tanto poi provo a ricreare il context subito dopo e li verra' registrato l'errore
		}

		try{
			GestoreRichieste.readRequestConfig(requestInfo);
		}catch(Exception e){
			String msg = "GestoreRichieste readRequestConfig fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
			logCore.error(msg,e);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, e, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(logCore, req, pddContextFromServlet, dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		if(idTransazione!=null) {
			try {
				if(openSPCoopProperties.isTransazioniEnabled()) {
					TransactionContext.createTransaction(idTransazione,"RicezioneContenutiApplicativiHTTPtoSOAP.1");
				}
				requestInfo.setIdTransazione(idTransazione);
				
				req.setThresholdContext((context!=null ? context.getPddContext(): null), 
						openSPCoopProperties.getDumpBinario_inMemoryThreshold(), openSPCoopProperties.getDumpBinario_repository());
			
			}catch(Throwable e) {
				context = null;
				protocolFactory = null;
				// non loggo l'errore tanto poi provo a ricreare il context subito dopo e li verra' registrato l'errore
			}
		}
		
		// Logger dei messaggi diagnostici
		String nomePorta = requestInfo.getProtocolContext().getInterfaceName();
		MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(TipoPdD.DELEGATA,idModulo,nomePorta,requestInfo);
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
		if(context!=null && protocolFactory!=null) {
			msgDiag.setPddContext(context.getPddContext(), protocolFactory);
		}
		
		try{
			msgDiag.logPersonalizzato("ricezioneRichiesta.firstLog");
		}catch(Exception e){
			logCore.error("Errore generazione diagnostico di ingresso",e);
		}
		
		// emitDiagnostic preAccept handler
		GestoreHandlers.emitDiagnostic(msgDiag, preInAcceptRequestContext, context!=null ? context.getPddContext() : null, 
				logCore, logCore);
		
		// Aggiorno RequestInfo
		try{
			msgDiag.mediumDebug("Accesso configurazione della richiesta in corso...");
		}catch(Exception e){
			logCore.error("Errore generazione diagnostico",e);
		}
		ConnectorDispatcherInfo cInfo = RicezioneContenutiApplicativiServiceUtils.updatePortaDelegataRequestInfo(requestInfo, logCore, req, res,
				this.generatoreErrore, serviceIdentificationReader, msgDiag, 
				context!=null ? context.getPddContext(): null);
		if(cInfo!=null){
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(context, logCore, req, pddContextFromServlet, dataAccettazioneRichiesta, cInfo);
			return; // l'errore in response viene impostato direttamente dentro il metodo
		}
		req.updateRequestInfo(requestInfo);	

		// Timeout e DumpRaw
		DumpRaw dumpRaw = null;
		try{
			try{
				msgDiag.mediumDebug("Lettura configurazione dump binario ...");
			}catch(Exception e){
				logCore.error("Errore generazione diagnostico",e);
			}
			boolean dumpBinario = configPdDManager.dumpBinarioPD();
			PortaDelegata pd = null;
			if(requestInfo!=null && requestInfo.getProtocolContext()!=null && requestInfo.getProtocolContext().getInterfaceName()!=null) {
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(requestInfo.getProtocolContext().getInterfaceName());
				pd = configPdDManager.getPortaDelegata_SafeMethod(idPD, requestInfo);
			}

			// Limited
			try{
				msgDiag.mediumDebug("Lettura configurazione dimensione massima della richiesta ...");
			}catch(Exception e){
				logCore.error("Errore generazione diagnostico",e);
			}
			String azione = (requestInfo!=null && requestInfo.getIdServizio()!=null) ? requestInfo.getIdServizio().getAzione() : null;
			SoglieDimensioneMessaggi limitedInputStream = configPdDManager.getSoglieLimitedInputStream(pd, azione, idModulo,
					(context!=null && context.getPddContext()!=null) ? context.getPddContext() : null, 
					requestInfo,
					protocolFactory, logCore);
			if(limitedInputStream!=null) {
				req.setRequestLimitedStream(limitedInputStream.getRichiesta());
				if(context!=null && context.getPddContext()!=null) {
					context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.LIMITED_STREAM, limitedInputStream.getRisposta());
				}
			}
			else {
				if(!openSPCoopProperties.isLimitedInputStreamThresholdDefined()) {
					req.disableLimitedStream();
				}
			}
			
			// Timeout
			try{
				msgDiag.mediumDebug("Lettura configurazione timeout per la lettura della richiesta ...");
			}catch(Exception e){
				logCore.error("Errore generazione diagnostico",e);
			}
			boolean useTimeoutInputStream = configPdDManager.isConnettoriUseTimeoutInputStream(pd);
			if(useTimeoutInputStream) {
				int timeout = configPdDManager.getRequestReadTimeout(pd);
				if(timeout>0) {
					req.setRequestReadTimeout(timeout);
				}
				else {
					req.disableReadTimeout();
				}
			}
			else {
				req.disableReadTimeout();
			}
			
			// DumpRaw
			try{
				msgDiag.mediumDebug("Lettura configurazione dump ...");
			}catch(Exception e){
				logCore.error("Errore generazione diagnostico",e);
			}
			DumpConfigurazione dumpConfigurazione = configPdDManager.getDumpConfigurazione(pd);
			boolean fileTrace_headers = configPdDManager.isTransazioniFileTraceEnabled(pd) && configPdDManager.isTransazioniFileTraceDumpBinarioHeadersEnabled(pd);
			boolean fileTrace_payload = configPdDManager.isTransazioniFileTraceEnabled(pd) && configPdDManager.isTransazioniFileTraceDumpBinarioPayloadEnabled(pd);
			dumpRaw = new DumpRaw(logCore, requestInfo.getIdentitaPdD(), idModulo, TipoPdD.DELEGATA, 
					dumpBinario, 
					dumpConfigurazione,
					fileTrace_headers, fileTrace_payload);
			if(dumpRaw.isActiveDumpRichiesta()) {
				req = new DumpRawConnectorInMessage(logCore, req, 
						(context!=null ? context.getPddContext(): null), 
						openSPCoopProperties.getDumpBinario_inMemoryThreshold(), openSPCoopProperties.getDumpBinario_repository());
			}
			if(dumpRaw.isActiveDumpRisposta()) {
				res = new DumpRawConnectorOutMessage(logCore, res, 
						(context!=null ? context.getPddContext(): null), 
						openSPCoopProperties.getDumpBinario_inMemoryThreshold(), openSPCoopProperties.getDumpBinario_repository());
			}
		}catch(Throwable e){
			String msg = "Inizializzazione di GovWay non correttamente effettuata: DumpRaw";
			logCore.error(msg,  e);
			cInfo = ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore, 
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
						IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, e, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(logCore, req, pddContextFromServlet, dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		// Questo servizio è invocabile solo con API Soap
		if(!ServiceBinding.SOAP.equals(requestInfo.getIntegrationServiceBinding())){
			String msg = "Servizio utilizzabile solamente con API SOAP, riscontrata API REST";
			logCore.error(msg);
			ConnectorDispatcherErrorInfo cInfoError =  ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore,
					ErroriIntegrazione.ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL.getErrore439_FunzionalitaNotSupportedByProtocol(msg, protocolFactory),
					IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL, null, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(context, logCore, req, pddContextFromServlet, dataAccettazioneRichiesta, cInfoError);
			return;
		}
		
		
		
		
		
		
		/* ------------  Lettura parametri della richiesta ------------- */
		
		// PostOutResponseContext
		PostOutResponseContext postOutResponseContext = null;
		
		PdDContext pddContext = null;
		String errorImbustamentoSoapNonRiuscito = null;
		MessageType messageTypeReq = null;
		OpenSPCoop2Message requestMessage = null;
		OpenSPCoop2Message responseMessage = null;
		String protocol = null;
		byte[] inputBody = null;
		
		try{
			
			/* --------------- Creo il context che genera l'id univoco ----------------------- */
			
			try{
				msgDiag.mediumDebug("Creazione contesto ...");
			}catch(Exception e){
				logCore.error("Errore generazione diagnostico",e);
			}
			
			if(protocolFactory==null) {
				protocolFactory = req.getProtocolFactory();
			}
			protocol = protocolFactory.getProtocol();
			
			if(context==null) {
				context = new RicezioneContenutiApplicativiContext(idModuloAsService,dataAccettazioneRichiesta,requestInfo);
			}
			if(preInAcceptRequestContext!=null && preInAcceptRequestContext.getPreContext()!=null && !preInAcceptRequestContext.getPreContext().isEmpty()) {
				context.getPddContext().addAll(preInAcceptRequestContext.getPreContext(), false);
			}
			context.setTipoPorta(TipoPdD.DELEGATA);
			context.setForceFaultAsXML(true); // siamo in una richiesta http senza SOAP, un SoapFault non ha senso
			context.setIdModulo(idModulo);
			context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME, protocolFactory.getProtocol());
			context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO, req.getRequestInfo());
			RicezionePropertiesConfig rConfig = RicezioneContenutiApplicativiServiceUtils.readPropertiesConfig(req.getRequestInfo(), logCore,null);
			if(rConfig!=null) {
	            if (rConfig.getApiImplementation() != null && !rConfig.getApiImplementation().isEmpty()) {
	               context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_CONFIGURAZIONE, rConfig.getApiImplementation());
	            }
	            if (rConfig.getSoggettoFruitore() != null && !rConfig.getSoggettoFruitore().isEmpty()) {
	            	context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_FRUITORE, rConfig.getSoggettoFruitore());
	            }
	            if (rConfig.getSoggettoErogatore() != null && !rConfig.getSoggettoErogatore().isEmpty()) {
	            	context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_EROGATORE, rConfig.getSoggettoErogatore());
	            }
			}
			msgDiag.setPddContext(context.getPddContext(),protocolFactory);		
			pddContext = context.getPddContext();
			
			try{
				if(openSPCoopProperties.isTransazioniEnabled()) {
					// NOTA: se gia' esiste con l'id di transazione, non viene ricreata
					TransactionContext.createTransaction((String)pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE),"RicezioneContenutiApplicativiHTTPtoSOAP.2");
				}
			}catch(Exception e){
				logCore.error("Errore durante la creazione della transazione",e);
			}
			
			try{
				msgDiag.logPersonalizzato("ricezioneRichiesta.firstAccessRequestStream");
			}catch(Exception e){
				logCore.error("Errore generazione diagnostico di ingresso (stream access)",e);
			}
			
			if(dumpRaw!=null && dumpRaw.isActiveDump()){
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
			postOutResponseContext = new PostOutResponseContext(logCore,protocolFactory);
			postOutResponseContext.setTipoPorta(TipoPdD.DELEGATA);
			postOutResponseContext.setPddContext(pddContext);
			postOutResponseContext.setIdModulo(idModulo);
			
			
			
			
			/* ------------  PreInHandler ------------- */
			
			// build context
			PreInRequestContext preInRequestContext = new PreInRequestContext(pddContext);
			if(pddContextFromServlet!=null){
				preInRequestContext.getPddContext().addAll(pddContextFromServlet, true);
			}
			preInRequestContext.setTipoPorta(TipoPdD.DELEGATA);
			preInRequestContext.setIdModulo(idModulo);
			preInRequestContext.setProtocolFactory(protocolFactory);
			preInRequestContext.setRequestInfo(requestInfo);
			Map<String, Object> transportContext = new HashMap<String, Object>();
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
			
			if(dumpRaw!=null && dumpRaw.isActiveDumpRichiesta()){
				dumpRaw.serializeRequest(((DumpRawConnectorInMessage)req), false, notifierInputStreamParams);
				dataIngressoRichiesta = req.getDataIngressoRichiesta();
				context.setDataIngressoRichiesta(dataIngressoRichiesta);
			}

			
			
			


			
			
			
			
			/* ------------ Controllo ContentType -------------------- */
			
			msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.tipologiaMessaggio");
			messageTypeReq = MessageType.SOAP_11; // TODO: rendere parametrico ?
			
			
			
			
			/* ------------  Imbustamento Messaggio di Richiesta ------------- */
			boolean imbustamentoConAttachment = false;
			String tipoAttachment =  HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
			// HeaderTrasporto
			String imb = TransportUtils.getFirstValue(req.getHeaderValues(openSPCoopProperties.getTunnelSOAPKeyWord_headerTrasporto()));
			if(imb!=null && "true".equals(imb.trim())){
				imbustamentoConAttachment = true;
				String mime = TransportUtils.getFirstValue(req.getHeaderValues(openSPCoopProperties.getTunnelSOAPKeyWordMimeType_headerTrasporto()));
				if(mime!=null) {
					tipoAttachment = mime.trim();
				}
			}
			if(imb==null){
				// Proprieta FORMBased
				imb = TransportUtils.getFirstValue(req.getParameterValues(openSPCoopProperties.getTunnelSOAPKeyWord_urlBased()));
				if(imb!=null && "true".equals(imb.trim())){
					imbustamentoConAttachment = true;
					// lettura eventuale tipo di attachment
					String mime = TransportUtils.getFirstValue(req.getParameterValues(openSPCoopProperties.getTunnelSOAPKeyWordMimeType_urlBased())); 
					if(mime!=null){
						tipoAttachment = mime.trim();
					}
				}
			}
			if(imb==null){
				// Vedo se fosse indicato nel transport Context
				if(transportContext.get(openSPCoopProperties.getTunnelSOAPKeyWord_urlBased())!=null){
					if("true".equalsIgnoreCase((String)transportContext.get(openSPCoopProperties.getTunnelSOAPKeyWord_urlBased()))){
						imbustamentoConAttachment = true;
						// lettura eventuale tipo di attachment
						if(transportContext.get(openSPCoopProperties.getTunnelSOAPKeyWordMimeType_urlBased())!=null){
							tipoAttachment = (String) transportContext.get(openSPCoopProperties.getTunnelSOAPKeyWordMimeType_urlBased());
						}
					}
				}
			}

			String tipoLetturaRisposta = null;
			try{
				Utilities.printFreeMemory("RicezioneContenutiApplicativiHTTPtoSOAP - Pre costruzione richiesta");
				msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.inCorso");
				DumpByteArrayOutputStream bout = req.getRequest();
				if(bout!=null && bout.size()>0) {
					inputBody = bout.toByteArray();
					bout.clearResources();
					bout=null;
				}
				if( inputBody == null || inputBody.length<=0 ){
					throw new Exception("Ricevuto nessun contenuto da imbustare");
				}
				req.close();
				dataIngressoRichiesta = req.getDataIngressoRichiesta();
				context.setDataIngressoRichiesta(dataIngressoRichiesta);
				
				if(imbustamentoConAttachment){
					tipoLetturaRisposta = "Costruzione messaggio SOAP per Tunnel con mimeType "+tipoAttachment;
					requestMessage = TunnelSoapUtils.imbustamentoMessaggioConAttachment(org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(logCore,requestInfo, MessageRole.REQUEST),
							messageTypeReq,MessageRole.REQUEST,inputBody,tipoAttachment,
							MailcapActivationReader.existsDataContentHandler(tipoAttachment),req.getContentType(), openSPCoopProperties.getHeaderSoapActorIntegrazione());					
					requestMessage.setTransportRequestContext(requestInfo.getProtocolContext());				
				}else{
					tipoLetturaRisposta = "Imbustamento messaggio in un messaggio SOAP";
					String contentTypeForEnvelope = null; // todo renderlo parametrico soprattutto per soap1.2
					//String soapAction = "\"OpenSPCoop2\""; // todo renderlo parametrico
					String soapAction = "\"GovWay\""; // todo renderlo parametrico
					OpenSPCoop2MessageParseResult pr = org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(logCore,requestInfo, MessageRole.REQUEST).
							envelopingMessage(messageTypeReq, contentTypeForEnvelope, soapAction, 
							requestInfo.getProtocolContext(), inputBody, notifierInputStreamParams, 
							openSPCoopProperties.getAttachmentsProcessingMode(), 
							openSPCoopProperties.isDeleteInstructionTargetMachineXml(),
							openSPCoopProperties.useSoapMessageReader(), openSPCoopProperties.getSoapMessageReaderBufferThresholdKb());
					if(pr.getParseException()!=null){
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
					}
					requestMessage = pr.getMessage_throwParseException();
				}
				
				if(requestInfo.getProtocolContext().getHeaders()==null) {
					requestInfo.getProtocolContext().setHeaders(new HashMap<String,List<String>>());
				}
				requestInfo.getProtocolContext().removeHeader(HttpConstants.CONTENT_TYPE);
				TransportUtils.setHeader(requestInfo.getProtocolContext().getHeaders(),HttpConstants.CONTENT_TYPE, requestMessage.getContentType());
				requestInfo.setIntegrationRequestMessageType(requestMessage.getMessageType());
				
				Utilities.printFreeMemory("RicezioneContenutiApplicativiHTTPtoSOAP - Post costruzione richiesta");
				requestMessage.setProtocolName(protocolFactory.getProtocol());
				requestMessage.setTransactionId(PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext));
				requestMessage.addContextProperty(org.openspcoop2.core.constants.Costanti.REQUEST_INFO,requestInfo); // serve nelle comunicazione non stateless (es. riscontro salvato) per poterlo rispedire
				requestMessage.addContextProperty(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE,pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)); // serve nelle comunicazione non stateless (es. riscontro salvato) per poterlo rispedire
				Object nomePortaInvocataObject = context.getPddContext().getObject(CostantiPdD.NOME_PORTA_INVOCATA);
				if(nomePortaInvocataObject!=null && nomePortaInvocataObject instanceof String) {
					requestMessage.addContextProperty(CostantiPdD.NOME_PORTA_INVOCATA, (String) nomePortaInvocataObject );
				}
								
			}catch(Exception e){
				logCore.error(tipoLetturaRisposta +" con errore: "+e.getMessage(),e);
				errorImbustamentoSoapNonRiuscito=tipoLetturaRisposta +" con errore: "+e.getMessage();
				throw e;
			}
			
			
			/* --------------- SecurityToken --------------- */
			try {
				if(requestInfo!=null && requestInfo.getProtocolContext()!=null && requestInfo.getProtocolContext().getCredential()!=null && 
						requestInfo.getProtocolContext().getCredential().getCertificate()!=null &&
						requestInfo.getProtocolContext().getCredential().getCertificate().getCertificate()!=null) {
					SecurityTokenUtilities.newSecurityToken(pddContext);
				}
			}catch(Exception e){
				logCore.error("Costruzione SecurityToken non riuscito: "+e.getMessage(),e);
			}
			
			/* ------------  Elaborazione ------------- */
		
			// Contesto di Richiesta
			context.setCredenziali(new Credenziali(req.getCredential()));
			context.setGestioneRisposta(true); // siamo in una servlet, la risposta deve essere aspettata
			context.setInvocazionePDPerRiferimento(false); // la PD con questa servlet non effettuera' mai invocazioni per riferimento.
			context.setMessageRequest(requestMessage);
			context.setUrlProtocolContext(requestInfo.getProtocolContext());
			context.setMsgDiagnostico(msgDiag);
					
			// Log elaborazione dati completata
			msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.completata");
			
			// Invocazione...
			RicezioneContenutiApplicativi gestoreRichiesta = new RicezioneContenutiApplicativi(context, this.generatoreErrore);
			gestoreRichiesta.process(req);
			responseMessage = context.getMessageResponse();
						
		} catch (Throwable e) {
			
			if(context==null){
				// Errore durante la generazione dell'id
				context = RicezioneContenutiApplicativiContext.newRicezioneContenutiApplicativiContext(idModuloAsService,dataAccettazioneRichiesta,requestInfo);
				context.setDataIngressoRichiesta(dataIngressoRichiesta);
				context.setTipoPorta(TipoPdD.DELEGATA);
				context.setForceFaultAsXML(true); // siamo in una richiesta http senza SOAP, un SoapFault non ha senso
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
			ParseException parseException = null;
			if(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)){
				parseException = (ParseException) pddContext.removeObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
				if(parseException!=null) {
					tParsing = parseException.getParseException();
				}
			}
			if(tParsing==null && (requestMessage==null || requestMessage.getParseException() == null)){
				tParsing = ParseExceptionUtils.getParseException(e);
			}
					
			// Genero risposta con errore
			if(errorImbustamentoSoapNonRiuscito!=null){
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				logCore.error("ImbustamentoSOAP",e);
				Throwable tMessage = null;
				if(tParsing!=null){
					tMessage = tParsing;
				}
				else{
					tMessage = e;
				}
				String msgErrore = tMessage.getMessage();
				if(msgErrore==null){
					msgErrore = tMessage.toString();
				}
				msgDiag.logErroreGenerico(errorImbustamentoSoapNonRiuscito+"  "+msgErrore, "ImbustamentoSOAP");
				
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
				if( parseException!=null && parseException.getSourceException()!=null &&
						TimeoutIOException.isTimeoutIOException(parseException.getSourceException())) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
				}
				else if( parseException!=null && parseException.getSourceException()!=null &&
						LimitExceededIOException.isLimitExceededIOException(parseException.getSourceException())) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
				}
				
				responseMessage = this.generatoreErrore.build(pddContext,integrationFunctionError,
						ErroriIntegrazione.ERRORE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA.
						getErrore422_MessaggioSOAPNonGenerabileTramiteImbustamentoSOAP(errorImbustamentoSoapNonRiuscito),tMessage,null);
			}
			else if(tParsing!=null){
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				String msgErrore = tParsing.getMessage();
				if(msgErrore==null){
					msgErrore = tParsing.toString();
				}
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
				logCore.error("parsingExceptionRichiesta",e);
				msgDiag.logPersonalizzato("parsingExceptionRichiesta");
				
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
				if( parseException!=null && parseException.getSourceException()!=null &&
						TimeoutIOException.isTimeoutIOException(parseException.getSourceException())) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
				}
				else if( parseException!=null && parseException.getSourceException()!=null &&
						LimitExceededIOException.isLimitExceededIOException(parseException.getSourceException())) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
				}
				
				responseMessage = this.generatoreErrore.build(pddContext,integrationFunctionError,
						ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.
						getErrore432_MessaggioRichiestaMalformato(tParsing),tParsing,null);
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
				IntegrationFunctionError integrationError = he.getIntegrationFunctionError();
				if(integrationError==null) {
					integrationError = IntegrationFunctionError.BAD_REQUEST;
				}
				responseMessage = this.generatoreErrore.build(pddContext,integrationError,errore,e,null);
				he.customized(responseMessage);
			}
			else{
				logCore.error("ErroreGenerale",e);
				msgDiag.logErroreGenerico(e, "Generale(richiesta)");
				responseMessage = this.generatoreErrore.build(pddContext,IntegrationFunctionError.BAD_REQUEST,
						ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.
						getErrore426_ServletError(true, e),e,null);
			}
		}
		finally{

			try {
				GestoreRichieste.saveRequestConfig(requestInfo);
			}catch(Throwable e) {
				logCore.error("Errore durante il salvataggio dei dati della richiesta: "+e.getMessage(),e);
			}			

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
				
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
				if( parseException!=null && parseException.getSourceException()!=null &&
						TimeoutIOException.isTimeoutIOException(parseException.getSourceException())) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
				}
				else if( parseException!=null && parseException.getSourceException()!=null &&
						LimitExceededIOException.isLimitExceededIOException(parseException.getSourceException())) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
				}
				
				responseMessage = this.generatoreErrore.build(pddContext, integrationFunctionError,
						ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.
						getErrore432_MessaggioRichiestaMalformato(parseException.getParseException()),
						parseException.getParseException(),null);
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
				responseMessage = this.generatoreErrore.build(pddContext, IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT,
						ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
						getErrore440_MessaggioRispostaMalformato(parseException.getParseException()),
						parseException.getParseException(),null);
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
			
			// *** GB ***
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
		if(context.getResponseHeaders()==null) {
			context.setResponseHeaders(new HashMap<String,List<String>>());
		}
		ServicesUtils.setGovWayHeaderResponse(responseMessage, openSPCoopProperties,
				context.getResponseHeaders(), logCore, true, context.getPddContext(), requestInfo);
		if(context.getResponseHeaders()!=null){
			Iterator<String> keys = context.getResponseHeaders().keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				List<String> values = context.getResponseHeaders().get(key);
				if(values!=null && !values.isEmpty()) {
					for (int i = 0; i < values.size(); i++) {
						String value = values.get(i);
						String verbo = "";
						try{
							if(i==0) {
								verbo = "set";
								res.setHeader(key,value);
							}
							else {
								verbo = "add";
								res.addHeader(key,value);
							}
			    		}catch(Exception e){
			    			logCore.error("Response."+verbo+"Header("+key+","+value+") error: "+e.getMessage(),e);
			    		}	
					}
				}
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
			
			this.generatoreErrore.updateProprietaErroreApplicativo(context.getProprietaErroreAppl());
			
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
		
		EsitoTransazione esito = null;
		String descrizioneSoapFault = "";
		int statoServletResponse = 200;
		Throwable erroreConsegnaRisposta = null; 
		boolean httpEmptyResponse = false;
		long lengthOutResponse = -1;
		boolean erroreConnessioneClient = false;
		boolean sendInvoked = false;
		try{
			if(responseMessage!=null && !responseMessage.isForcedEmptyResponse() && (responseMessage.getForcedResponse()==null)){
					
				// force response code
				boolean forced = false;
				if(responseMessage.getForcedResponseCode()!=null){
					try{
						statoServletResponse = Integer.parseInt(responseMessage.getForcedResponseCode());
						forced = true;
					}catch(Exception e){}
				}
				
				if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())) {
										
					SOAPBody body = responseMessage.castAsSoap().getSOAPBody();
					String contentTypeRisposta = null;
					byte[] risposta = null;
					if(body!=null && body.hasFault()){
						statoServletResponse = 500; // cmq e' un errore come l'errore applicativo
						String msgError = SoapUtils.safe_toString(responseMessage.getFactory(), body.getFault(), false, logCore);
						//risposta=msgError.getBytes();
						org.openspcoop2.message.xml.XMLUtils xmlUtils = org.openspcoop2.message.xml.XMLUtils.getInstance(responseMessage.getFactory());
						risposta=xmlUtils.toByteArray(body.getFault(), true);
						//System.out.println("ELABORATO:"+new String(risposta));
						contentTypeRisposta = responseMessage.getContentType();
						descrizioneSoapFault = " ("+msgError+")";	
					}else{
						risposta=TunnelSoapUtils.sbustamentoMessaggio(responseMessage);
						if(risposta==null || risposta.length<=0){
							// Si puo' entrare in questo caso, se nel messaggio Soap vi era presente solo l'header
							risposta = null;
							if(!forced)
								statoServletResponse = 202;
						}else{
							
							SOAPElement child = SoapUtils.getNotEmptyFirstChildSOAPElement(body);
							if(child!=null){
								if(protocolFactory.createErroreApplicativoBuilder().isErroreApplicativo(child)){
									statoServletResponse = 500;
								}
							}
							
							// Non serve la updateContentType. Il messaggio e' gia' stato serializzato ed il cType e' corretto.  
							if(TunnelSoapUtils.isTunnelOpenSPCoopSoap(responseMessage.getFactory(), body)){
								contentTypeRisposta = TunnelSoapUtils.getContentTypeTunnelOpenSPCoopSoap(body);
							}else{
								contentTypeRisposta = responseMessage.getContentType();
							}
						}
					}
					
					// transfer length
					if(risposta!=null){
						lengthOutResponse = risposta.length;
						ServicesUtils.setTransferLength(openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi(), 
								req, res, Long.valueOf(risposta.length));
					}
					
					// httpstatus
					res.setStatus(statoServletResponse);
					
					// content type
					if(contentTypeRisposta!=null){
						res.setContentType(contentTypeRisposta);
					}
					
					// esito calcolato prima del sendResponse, per non consumare il messaggio
					esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(), 
							statoServletResponse, requestInfo.getIntegrationServiceBinding(),
							responseMessage, context.getProprietaErroreAppl(),informazioniErrori,
							pddContext);
					
					// httpHeaders
					res.sendResponseHeaders(responseMessage);					
					
					// contenuto
					if(risposta!=null){
						sendInvoked = true;
						res.sendResponse(DumpByteArrayOutputStream.newInstance(risposta));
					}
				}
				else {
					
					// transfer length
					ServicesUtils.setTransferLength(openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi(), 
							req, res, responseMessage);
					
					// content type
					// Alcune implementazioni richiedono di aggiornare il Content-Type
					responseMessage.updateContentType();
					ServicesUtils.setContentType(responseMessage, res);
					
					// http status
					boolean consume = true;
					if(responseMessage.castAsRest().isProblemDetailsForHttpApis_RFC7807() || 
							(MessageRole.FAULT.equals(responseMessage.getMessageRole()) &&
								(
								MessageType.XML.equals(responseMessage.getMessageType()) 
										|| 
								MessageType.JSON.equals(responseMessage.getMessageType())
								)
							)
						) {
						consume = false; // può essere usato nel post out response handler
						String contentAsString = null;
						try {
							contentAsString = responseMessage.castAsRest().getContentAsString();
						}catch(Throwable t) {
							logCore.error("Parsing errore non riuscito: "+t.getMessage(),t);
						}
						if(contentAsString!=null && StringUtils.isNotEmpty(contentAsString)) {
							descrizioneSoapFault = " ("+contentAsString+")";
						}
					}
					res.setStatus(statoServletResponse);
					
					// esito calcolato prima del sendResponse, per non consumare il messaggio
					esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(), 
							statoServletResponse, requestInfo.getIntegrationServiceBinding(),
							responseMessage, context.getProprietaErroreAppl(), informazioniErrori,
							pddContext);
					
					// contenuto
					Utilities.printFreeMemory("RicezioneContenutiApplicativiDirect - Pre scrittura risposta");
					
					// Il contentLenght, nel caso di TransferLengthModes.CONTENT_LENGTH e' gia' stato calcolato
					// con una writeTo senza consume. Riuso il solito metodo per evitare differenze di serializzazione
					// e cambiare quindi il content length effettivo.
					sendInvoked = true;
					if(TransferLengthModes.CONTENT_LENGTH.equals(openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi())){
						res.sendResponse(responseMessage, false);
					} else {
						res.sendResponse(responseMessage, consume);
					}
					Utilities.printFreeMemory("RicezioneContenutiApplicativiDirect - Post scrittura risposta");
					
				}
				
			}
			else if(responseMessage!=null && responseMessage.getForcedResponse()!=null) {
				byte[]response = responseMessage.getForcedResponse().getContent();
//				if(response==null) {
//					throw new Exception("Trovata configurazione 'forcedResponse' senza una vera risposta");
//				}
				if(response!=null) {
					lengthOutResponse = response.length;
				}
			
				if(response!=null && response.length<1024) {
					// Se il messaggio non è troppo grande lo aggiungo al diagnostico
					try {
						descrizioneSoapFault = "("+new String(response)+")";
					}catch(Throwable t) {
						descrizioneSoapFault = "";
					}
				}
				
				if(responseMessage.getForcedResponse().getHeadersValues()!=null &&
						responseMessage.getForcedResponse().getHeadersValues().size()>0) {
					Iterator<String> keys = responseMessage.getForcedResponse().getHeadersValues().keySet().iterator();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						List<String> values = responseMessage.getForcedResponse().getHeadersValues().get(key);
						if(values!=null && !values.isEmpty()) {
							for (int i = 0; i < values.size(); i++) {
								String value = values.get(i);
								String verbo = "";
								try{
									if(i==0) {
										verbo = "set";
										res.setHeader(key,value);
									}
									else {
										verbo = "add";
										res.addHeader(key,value);
									}
					    		}catch(Exception e){
					    			logCore.error("Response(Forced)."+verbo+"Header("+key+","+value+") error: "+e.getMessage(),e);
					    		}	
							}
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
						statoServletResponse, requestInfo.getIntegrationServiceBinding(),
						responseMessage, context.getProprietaErroreAppl(),informazioniErrori,
						pddContext);
				
				if(response!=null) {
					sendInvoked = true;
					res.sendResponse(DumpByteArrayOutputStream.newInstance(response));
				}
				
			}			
			else{
				// httpstatus
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
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(), 
						statoServletResponse, requestInfo.getIntegrationServiceBinding(),
						responseMessage, context.getProprietaErroreAppl(),informazioniErrori,
						pddContext);
				// carico-vuoto
			}
			
		}catch(Throwable e){
			logCore.error("ErroreGenerale",e);
			erroreConsegnaRisposta = e;
			
			erroreConnessioneClient = ServicesUtils.isConnessioneClientNonDisponibile(e);
			
			try{
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
			}catch(Exception eBuildError){
				esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
			}
			
			// Genero risposta con errore
			try{
				if(sendInvoked==false) {
					// nel caso sia già stato inoltrata una risposta non e' più possibile modificarlo cosi come tutti gli header etc...	
					byte [] rispostaErrore = null;
					List<Integer> returnCode = new ArrayList<Integer>();
					InformazioniErroriInfrastrutturali informazioniErrori_error = null;
					if( (responseMessage!=null && responseMessage.getParseException() != null) ||
							(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION))){
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
											
						rispostaErrore = this.generatoreErrore.buildAsByteArray(pddContext, IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT,
								ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
								getErrore440_MessaggioRispostaMalformato(parseException.getParseException()),
								returnCode);
						
						informazioniErrori_error = new InformazioniErroriInfrastrutturali();
						informazioniErrori_error.setContenutoRispostaNonRiconosciuto(true);
					} 
					else{
						rispostaErrore = this.generatoreErrore.buildAsByteArray(pddContext, IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
								ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.
								getErrore426_ServletError(false, e),
								returnCode);
					}
					
					// transfer length
					lengthOutResponse = rispostaErrore.length;
					ServicesUtils.setTransferLength(openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi(), 
							req, res, Long.valueOf(rispostaErrore.length));
					
					// httpstatus
					//statoServletResponse = 500; // Nella servlet con sbustamento non devo ritornare 500
					statoServletResponse = 200;
					if(returnCode!=null && returnCode.size()>0){
						statoServletResponse = returnCode.get(0);
					}
					res.setStatus(statoServletResponse);
					
					// esito calcolato prima del sendResponse, per non consumare il messaggio
					if(informazioniErrori_error!=null) {
						esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(),
								statoServletResponse, requestInfo.getIntegrationServiceBinding(),
								null, context.getProprietaErroreAppl(), informazioniErrori_error,
								pddContext);
					}
					
					// content type
					res.setContentType("text/xml");
					
					// contenuto
					res.sendResponse(DumpByteArrayOutputStream.newInstance(rispostaErrore));
				}
						
			}catch(Throwable error){
				
				if(!erroreConnessioneClient){
					erroreConnessioneClient = ServicesUtils.isConnessioneClientNonDisponibile(error);
				}
				
				logCore.error("Generazione di un risposta errore non riuscita",error);
				statoServletResponse = 500; // ERRORE EFFETTIVO!
				try{
					res.setStatus(500);
				}catch(Exception eStatus){
					logCore.error("Response.setStatus(500) error: "+eStatus.getMessage(),eStatus);
				}
				byte[] ris = error.toString().getBytes();
				try{
					res.sendResponse(DumpByteArrayOutputStream.newInstance(ris));
				}catch(Exception erroreStreamChiuso){ 
					erroreConnessioneClient = true;
					//se lo stream non e' piu' disponibile non si potra' consegnare alcuna risposta
				}
				lengthOutResponse = ris.length;
			}
			
		}
		finally{
			if(sendInvoked==false) {
				// nel caso sia già stato inoltrata una risposta non e' più possibile modificarlo cosi come tutti gli header etc...			
				statoServletResponse = res.getResponseStatus(); // puo' essere "trasformato" da api engine
			}
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
						if(statoServletResponse>=300)
							msgDiag.logPersonalizzato("consegnaRispostaApplicativaKoEffettuata");
						else
							msgDiag.logPersonalizzato("consegnaRispostaApplicativaOkEffettuata");
					}
				}
				
			}catch(Exception e){
				
				erroreConnessioneClient = true;
				
				logCore.error("Chiusura stream non riuscita",e);
				
				// Risposta non ritornata al servizio applicativo, il socket verso il servizio applicativo era chiuso o cmq inutilizzabile
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, e.toString()); // NOTA: lasciare e.toString()
				
				msgDiag.logPersonalizzato("consegnaRispostaApplicativaFallita");
				
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
			
			if(dumpRaw!=null && dumpRaw.isActiveDumpRisposta()){
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
			if(context!=null && context.getPddContext()!=null && context.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) {
				idTransazione = (String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			}
			if(idTransazione!=null) {
			//if(context.getIdMessage()!=null){
				c = RepositoryConnettori.removeConnettorePD(
						//context.getIdMessage()
						idTransazione
						);
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
				if(erroreConsegnaRisposta==null){
					postOutResponseContext.setEsito(esito);
				}else{
					try{
						esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
					}catch(Exception eBuildError){
						esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
					}
					postOutResponseContext.setEsito(esito);
				}
				postOutResponseContext.setReturnCode(statoServletResponse);
				postOutResponseContext.setResponseHeaders(context.getResponseHeaders());
				postOutResponseContext.setProtocollo(context.getProtocol());
				postOutResponseContext.setIntegrazione(context.getIntegrazione());
				if(context.getTipoPorta()!=null)
					postOutResponseContext.setTipoPorta(context.getTipoPorta());	
				postOutResponseContext.setIdModulo(idModulo);
				
				if(inputBody!=null){
					postOutResponseContext.setInputRequestMessageSize(Long.valueOf(inputBody.length));
				}
				if(requestMessage!=null){
					//postOutResponseContext.setInputRequestMessageSize(requestMessage.getIncomingMessageContentLength());
					postOutResponseContext.setOutputRequestMessageSize(requestMessage.getOutgoingMessageContentLength());
				}else{
					postOutResponseContext.setInputRequestMessageSize(req.getContentLength()+0l);
				}
				
				if(erroreConsegnaRisposta==null && responseMessage!=null  && !responseMessage.isForcedEmptyResponse() && responseMessage.getForcedResponse()==null){
					postOutResponseContext.setInputResponseMessageSize(responseMessage.getIncomingMessageContentLength());
					postOutResponseContext.setOutputResponseMessageSize(lengthOutResponse); // sbustata!
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
		// *** GB ***
		
		return;

	}


}
