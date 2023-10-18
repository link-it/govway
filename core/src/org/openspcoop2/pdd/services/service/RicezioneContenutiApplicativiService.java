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



package org.openspcoop2.pdd.services.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPBody;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreRichieste;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.IConnettore;
import org.openspcoop2.pdd.core.connettori.RepositoryConnettori;
import org.openspcoop2.pdd.core.controllo_traffico.SogliaDimensioneMessaggio;
import org.openspcoop2.pdd.core.controllo_traffico.SogliaReadTimeout;
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
import org.openspcoop2.pdd.services.connector.RicezioneContenutiApplicativiConnector;
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
import org.openspcoop2.utils.LimitedInputStream;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TimeoutIOException;
import org.openspcoop2.utils.TimeoutInputStream;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

/**
 * RicezioneContenutiApplicativiService
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */



public class RicezioneContenutiApplicativiService {

	
	private RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore;
	
	public RicezioneContenutiApplicativiService(RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore){
		this.generatoreErrore = generatoreErrore;
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
					SogliaReadTimeout soglia = new SogliaReadTimeout();
					soglia.setSogliaMs(openSPCoopProperties.getReadConnectionTimeout_ricezioneContenutiApplicativi());
					soglia.setConfigurazioneGlobale(true);
					soglia.setIdConfigurazione("GovWayCore");
					req.setRequestReadTimeout(soglia);
				}
				req.setThresholdContext(null, 
					openSPCoopProperties.getDumpBinarioInMemoryThreshold(), openSPCoopProperties.getDumpBinarioRepository());
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
						new RicezioneContenutiApplicativiInternalErrorGenerator(logCore, RicezioneContenutiApplicativiConnector.ID_MODULO, requestInfo);
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
		
		// Proprieta' OpenSPCoop
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
		
		// check requestInfo
		if(requestInfo==null) {
			String msg = "RequestInfo undefined";
			logCore.error(msg);
			return;
		}
		
		// Identifico Servizio per comprendere correttamente il messageType
		ServiceIdentificationReader serviceIdentificationReader = null;
		try{
			serviceIdentificationReader = ServicesUtils.getServiceIdentificationReader(logCore, requestInfo,
					configPdDManager.getRegistroServiziManager(), configPdDManager);
		}catch(Exception e){
			String msg = "Inizializzazione RegistryReader fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
			logCore.error(msg,e);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore,
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
					TransactionContext.createTransaction(idTransazione, "RicezioneContenutiApplicativi.1");
				}
				requestInfo.setIdTransazione(idTransazione);
				
				req.setThresholdContext((context!=null ? context.getPddContext(): null), 
						openSPCoopProperties.getDumpBinarioInMemoryThreshold(), openSPCoopProperties.getDumpBinarioRepository());
				
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
		
		try{
			req.setDiagnosticProducer(context!=null ? context.getPddContext(): null, msgDiag);
		}catch(Throwable e){
			logCore.error("Errore registrazione diagnostico sulla richiesta",e);
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
					
		// Timeout, DimensioneMessaggi e DumpRaw
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
				pd = configPdDManager.getPortaDelegata_SafeMethod(idPD,requestInfo);
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
				SogliaReadTimeout timeout = configPdDManager.getRequestReadTimeout(pd,
						requestInfo,
						protocolFactory, 
						context!=null ? context.getPddContext() : null,
						null);
				if(timeout!=null && timeout.getSogliaMs()>0) {
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
			dumpRaw = new DumpRaw(logCore,requestInfo.getIdentitaPdD(), idModulo, TipoPdD.DELEGATA, 
					dumpBinario, 
					dumpConfigurazione,
					fileTrace_headers, fileTrace_payload);
			if(dumpRaw.isActiveDumpRichiesta()) {
				req = new DumpRawConnectorInMessage(logCore, req, 
						(context!=null ? context.getPddContext(): null), 
						openSPCoopProperties.getDumpBinarioInMemoryThreshold(), openSPCoopProperties.getDumpBinarioRepository());
			}
			if(dumpRaw.isActiveDumpRisposta()) {
				res = new DumpRawConnectorOutMessage(logCore, res, 
						(context!=null ? context.getPddContext(): null), 
						openSPCoopProperties.getDumpBinarioInMemoryThreshold(), openSPCoopProperties.getDumpBinarioRepository(),
						dumpRaw);
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
		
		// API Soap supporta solo POST e ?wsdl
		if(ServiceBinding.SOAP.equals(requestInfo.getIntegrationServiceBinding())){
			HttpRequestMethod method = null;
			if(req!=null && req.getURLProtocolContext()!=null && req.getURLProtocolContext().getRequestType()!=null) {
				try {
					method = HttpRequestMethod.valueOf(req.getURLProtocolContext().getRequestType());
				}catch(Exception e) {
					// ignore
				}
			}
			if(method!=null && !HttpRequestMethod.POST.equals(method)){
				if(ServicesUtils.isRequestWsdl(req, logCore)) {
					try {
						ServicesUtils.writeWsdl(res, requestInfo, RicezioneContenutiApplicativiConnector.ID_SERVICE, serviceIdentificationReader, logCore);
					}catch(Exception e) {
						String msg = "Lettura wsdl fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
						logCore.error(msg,e);
						cInfo = ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),
									IntegrationFunctionError.INTERNAL_REQUEST_ERROR, e, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
						// nel caso di wsdl request non emetto la transazione
						//RicezioneContenutiApplicativiServiceUtils.emitTransaction(context,logCore, req, pddContextFromServlet, dataAccettazioneRichiesta, cInfo);
					}finally {
						// FIX devo però rilasciare dalla memoria la transazione:
						if(openSPCoopProperties.isTransazioniEnabled()) {
							idTransazione = (String)context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
							TransactionContext.removeTransaction(idTransazione);
						}
					}
					return;
				}
				else {
					String msg = "Metodo http '"+method+"' non supportato dall'API SOAP invocata";
					logCore.error(msg);
					ConnectorDispatcherErrorInfo cInfoError =  ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore,
							ErroriIntegrazione.ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL.getErrore439_FunzionalitaNotSupportedByProtocol(msg, protocolFactory),
							IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL, null, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
					RicezioneContenutiApplicativiServiceUtils.emitTransaction(context,logCore, req, pddContextFromServlet, dataAccettazioneRichiesta, cInfoError);
					return;
				}
			}
		}
		
		


		
		/* ------------  Lettura parametri della richiesta ------------- */

		//	Risposta Soap
		OpenSPCoop2Message responseMessage = null;
				
		// PostOutResponseContext
		PostOutResponseContext postOutResponseContext = null;
		
		PdDContext pddContext = null;
		OpenSPCoop2Message requestMessage = null;
		String protocol = null;
		MessageType messageTypeReq = null;
		ServiceBinding integrationServiceBinding = null;
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
					
			integrationServiceBinding = requestInfo.getIntegrationServiceBinding();
			
			if(context==null) {
				context = new RicezioneContenutiApplicativiContext(idModuloAsService,dataAccettazioneRichiesta,requestInfo);
			}
			if(preInAcceptRequestContext!=null && preInAcceptRequestContext.getPreContext()!=null && !preInAcceptRequestContext.getPreContext().isEmpty()) {
				context.getPddContext().addAll(preInAcceptRequestContext.getPreContext(), false);
			}
			context.setTipoPorta(TipoPdD.DELEGATA);
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
			context.setProprietaErroreAppl(this.generatoreErrore.getProprietaErroreAppl());
			msgDiag.setPddContext(context.getPddContext(), protocolFactory);
			pddContext = context.getPddContext();
			
			try{
				if(openSPCoopProperties.isTransazioniEnabled()) {
					// NOTA: se gia' esiste con l'id di transazione, non viene ricreata
					TransactionContext.createTransaction((String)pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE), "RicezioneContenutiApplicativi.2");
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
			Map<String, Object> transportContext = new HashMap<>();
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
				dumpRaw.serializeRequest(((DumpRawConnectorInMessage)req), true, notifierInputStreamParams);
				dataIngressoRichiesta = req.getDataIngressoRichiesta();
				context.setDataIngressoRichiesta(dataIngressoRichiesta);
			}
			
			
				
			
			
			/* ------------ Controllo ContentType -------------------- */
			
			msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.tipologiaMessaggio");
			
			messageTypeReq = requestInfo.getIntegrationRequestMessageType();
			if(ServiceBinding.SOAP.equals(integrationServiceBinding) && messageTypeReq!=null){
				msgDiag.addKeyword(CostantiPdD.KEY_SOAP_VERSION, messageTypeReq.getMessageVersionAsString());
				msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE_ATTESO, SoapUtils.getSoapEnvelopeNS(messageTypeReq));
			}
			
			String contentTypeReq = req.getContentType();
			boolean contentTypeSupportato = messageTypeReq!=null;
			msgDiag.addKeyword(CostantiPdD.KEY_CONTENT_TYPES_ATTESI, 
					requestInfo.getBindingConfig().getContentTypesSupportedAsString(integrationServiceBinding, 
							MessageRole.REQUEST,requestInfo.getProtocolContext()));
			List<String> supportedContentTypes = requestInfo.getBindingConfig().getContentTypesSupported(integrationServiceBinding, 
					MessageRole.REQUEST,requestInfo.getProtocolContext()); 
			msgDiag.addKeyword(CostantiPdD.KEY_HTTP_HEADER, contentTypeReq);
			
			if(ServiceBinding.SOAP.equals(integrationServiceBinding)){
				if(openSPCoopProperties.isControlloContentTypeAbilitatoRicezioneContenutiApplicativi() == false){
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
					responseMessage = this.generatoreErrore.build(pddContext, IntegrationFunctionError.CONTENT_TYPE_NOT_PROVIDED,
							ErroriIntegrazione.ERRORE_433_CONTENT_TYPE_NON_PRESENTE.
							getErrore433_ContentTypeNonPresente(supportedContentTypes),null,null);
				}
				else{
					//ContentType del messaggio non supportato
					msgDiag.logPersonalizzato("contentType.unsupported");
					responseMessage = this.generatoreErrore.build(pddContext, IntegrationFunctionError.CONTENT_TYPE_NOT_SUPPORTED,
							ErroriIntegrazione.ERRORE_429_CONTENT_TYPE_NON_SUPPORTATO.
							getErrore429_ContentTypeNonSupportato(contentTypeReq,supportedContentTypes),null,null);
				}
			}
			else{
				/* ------------ Check Charset ------------- */
				try {
					boolean checkEnabled = false;
					List<String> ctDefault = null;
					if(ServiceBinding.SOAP.equals(integrationServiceBinding)){
						if(openSPCoopProperties.isControlloCharsetContentTypeAbilitatoRicezioneContenutiApplicativiSoap()) {
							checkEnabled = true;
							ctDefault = openSPCoopProperties.getControlloCharsetContentTypeAbilitatoRicezioneContenutiApplicativiSoap();
						}
					}
					else {
						if(openSPCoopProperties.isControlloCharsetContentTypeAbilitatoRicezioneContenutiApplicativiRest()) {
							checkEnabled = true;
							ctDefault = openSPCoopProperties.getControlloCharsetContentTypeAbilitatoRicezioneContenutiApplicativiRest();
						}
					}
					if(checkEnabled) {
						ServicesUtils.checkCharset(contentTypeReq, ctDefault, msgDiag, true, TipoPdD.DELEGATA);
					}
					
				}catch(Throwable t) {
					logCore.error("Avvenuto errore durante il controllo del charset della richiesta: "+t.getMessage(),t);
				}
				
				/* ------------  SoapAction check 1 (controlla che non sia null e ne ritorna il valore) ------------- */			
				String soapAction = null;
				try{
					if(ServiceBinding.SOAP.equals(integrationServiceBinding)){
						soapAction = req.getSOAPAction();
					}
				}catch(Exception e){
					if(dataIngressoRichiesta==null) {
						dataIngressoRichiesta=DateManager.getDate();
					}
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					throw e;
				}
				
				/* ------------ Costruzione Messaggio -------------------- */
				msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.inCorso");
				Utilities.printFreeMemory("RicezioneContenutiApplicativi - Pre costruzione richiesta");
				OpenSPCoop2MessageParseResult pr = req.getRequest(notifierInputStreamParams);
				if(pr.getParseException()!=null){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
				}
				dataIngressoRichiesta = req.getDataIngressoRichiesta();
				context.setDataIngressoRichiesta(dataIngressoRichiesta);
				requestMessage = pr.getMessage_throwParseException();
				Utilities.printFreeMemory("RicezioneContenutiApplicativi - Post costruzione richiesta");
				requestMessage.setProtocolName(protocolFactory.getProtocol());
				requestMessage.setTransactionId(PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext));
				requestMessage.addContextProperty(org.openspcoop2.core.constants.Costanti.REQUEST_INFO,requestInfo); // serve nelle comunicazione non stateless (es. riscontro salvato) per poterlo rispedire
				requestMessage.addContextProperty(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE,pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)); // serve nelle comunicazione non stateless (es. riscontro salvato) per poterlo rispedire
				Object nomePortaInvocataObject = context.getPddContext().getObject(CostantiPdD.NOME_PORTA_INVOCATA);
				if(nomePortaInvocataObject!=null && nomePortaInvocataObject instanceof String) {
					requestMessage.addContextProperty(CostantiPdD.NOME_PORTA_INVOCATA, (String) nomePortaInvocataObject );
				}
								
				/* ------------ Controllo Soap namespace -------------------- */
				String soapEnvelopeNamespaceVersionMismatch = null;
				try{
					if(ServiceBinding.SOAP.equals(integrationServiceBinding)){
						soapEnvelopeNamespaceVersionMismatch = ServicesUtils.checkSOAPEnvelopeNamespace(requestMessage.castAsSoap(), messageTypeReq);
					}
				}catch(Exception e){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					throw e;
				}
				
				/* ------------ Controllo MustUnderstand -------------------- */
				String mustUnderstandError = null;
				if(soapEnvelopeNamespaceVersionMismatch==null) {
					try{
						if(ServiceBinding.SOAP.equals(integrationServiceBinding)){
							mustUnderstandError = ServicesUtils.checkMustUnderstand(requestMessage.castAsSoap(),protocolFactory);
						}
					}catch(Exception e){
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
						throw e;
					}
				}
				
				/* ------------  SoapAction check 2 ------------- */
				if(soapAction!=null){
					if(openSPCoopProperties.checkSoapActionQuotedString_ricezioneContenutiApplicativi()){
						try{
							SoapUtils.checkSoapActionQuotedString(soapAction, messageTypeReq);
						}catch(Exception e){
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
							throw e;
						}
					}
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
			
				if(mustUnderstandError==null && soapEnvelopeNamespaceVersionMismatch==null){
				
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
				}	
				else{
					if(mustUnderstandError!=null){
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, mustUnderstandError);
						msgDiag.logPersonalizzato("mustUnderstand.unknown");
						responseMessage = this.generatoreErrore.build(pddContext, IntegrationFunctionError.SOAP_MUST_UNDERSTAND_UNKNOWN,
								ErroriIntegrazione.ERRORE_427_MUSTUNDERSTAND_ERROR.
								getErrore427_MustUnderstandHeaders(mustUnderstandError),null,null);
					}
					else{
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
						msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE, soapEnvelopeNamespaceVersionMismatch);
						msgDiag.logPersonalizzato("soapEnvelopeNamespace.versionMismatch");
						responseMessage = this.generatoreErrore.build(pddContext, IntegrationFunctionError.SOAP_VERSION_MISMATCH,
								ErroriIntegrazione.ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR.
								getErrore430_SoapNamespaceNonSupportato(messageTypeReq, soapEnvelopeNamespaceVersionMismatch),null,null);
					}
				}
			}
			
		}
		catch (Throwable e) {
			
			if(context==null){
				// Errore durante la generazione dell'id
				context = RicezioneContenutiApplicativiContext.newRicezioneContenutiApplicativiContext(idModuloAsService,dataAccettazioneRichiesta,requestInfo);
				context.setDataIngressoRichiesta(dataIngressoRichiesta);
				context.setTipoPorta(TipoPdD.DELEGATA);
				context.setIdModulo(idModulo);
				context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME, protocolFactory.getProtocol());
				pddContext = context.getPddContext();
				if(postOutResponseContext!=null){
					postOutResponseContext.setPddContext(pddContext);
				}
				msgDiag.setPddContext(pddContext, protocolFactory);
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
			String msgErrore = e.getMessage();
			if(msgErrore==null){
				msgErrore = e.toString();
			}
			if(	ServiceBinding.SOAP.equals(integrationServiceBinding) &&
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
				responseMessage = this.generatoreErrore.build(pddContext, IntegrationFunctionError.SOAP_VERSION_MISMATCH,
						ErroriIntegrazione.ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR.
						getErrore430_SoapNamespaceNonSupportato(messageTypeReq, "Impossibile recuperare il valore del namespace"),e,null);
			} else if(tParsing!=null){
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				msgErrore = tParsing.getMessage();
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
				
				responseMessage = this.generatoreErrore.build(pddContext, integrationFunctionError,
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
			else {
				logCore.error("ErroreGenerale",e);
				msgDiag.logErroreGenerico(e, "Generale(richiesta)");
				responseMessage = this.generatoreErrore.build(pddContext, IntegrationFunctionError.BAD_REQUEST,
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

			String requestReadTimeout = null;
			String responseReadTimeout = null;
			if(pddContext!=null && pddContext.containsKey(TimeoutInputStream.ERROR_MSG_KEY)) {
				String timeoutMessage = PdDContext.getValue(TimeoutInputStream.ERROR_MSG_KEY, pddContext);
				if(timeoutMessage!=null && timeoutMessage.startsWith(CostantiPdD.PREFIX_TIMEOUT_REQUEST)) {
					requestReadTimeout = timeoutMessage;
				}
				else if(timeoutMessage!=null && timeoutMessage.startsWith(CostantiPdD.PREFIX_TIMEOUT_RESPONSE)) {
					responseReadTimeout = timeoutMessage;
				}
			}
			String requestLimitExceeded = null;
			String responseLimitExceeded = null;
			if(pddContext!=null && pddContext.containsKey(LimitedInputStream.ERROR_MSG_KEY)) {
				String limitedExceededMessage = PdDContext.getValue(LimitedInputStream.ERROR_MSG_KEY, pddContext);
				if(limitedExceededMessage!=null && limitedExceededMessage.startsWith(CostantiPdD.PREFIX_LIMITED_REQUEST)) {
					requestLimitExceeded = limitedExceededMessage;
				}
				else if(limitedExceededMessage!=null && limitedExceededMessage.startsWith(CostantiPdD.PREFIX_LIMITED_RESPONSE)) {
					responseLimitExceeded = limitedExceededMessage;
				}
			}
			
			if((requestMessage!=null && requestMessage.getParseException() != null) || 
					(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)) ||
					requestReadTimeout!=null ||
					requestLimitExceeded!=null){
				if(pddContext!=null) {
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				}
				
				ParseException parseException = null;
				Throwable tParsing = null;
				Throwable sParsing = null;
				String msgErrore = null;
				if(requestReadTimeout != null) {
					tParsing = (TimeoutIOException) pddContext.getObject(TimeoutInputStream.EXCEPTION_KEY);
					sParsing = tParsing;
					msgErrore = tParsing.getMessage();
				}
				else if(requestLimitExceeded != null) {
					tParsing = (LimitExceededIOException) pddContext.getObject(LimitedInputStream.EXCEPTION_KEY);
					sParsing = tParsing;
					msgErrore = tParsing.getMessage();
				}
				else if( requestMessage!=null && requestMessage.getParseException() != null ){
					parseException = requestMessage.getParseException();
					tParsing = parseException.getParseException();
					sParsing = parseException.getSourceException();
					msgErrore = tParsing.getMessage();
				}
				else {
					parseException = (ParseException) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
					tParsing = parseException.getParseException();
					sParsing = parseException.getSourceException();
					msgErrore = tParsing.getMessage();
				}			
				
				if(msgErrore==null && tParsing!=null){
					msgErrore = tParsing.toString();
				}
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
				logCore.error("parsingExceptionRichiesta",sParsing);
				msgDiag.logPersonalizzato("parsingExceptionRichiesta");
				
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
				if(requestReadTimeout!=null) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
				}
				else if(requestLimitExceeded!=null) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
				}
				else if( parseException!=null && sParsing!=null &&
						TimeoutIOException.isTimeoutIOException(sParsing)) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
				}
				else if( parseException!=null && sParsing!=null &&
						LimitExceededIOException.isLimitExceededIOException(sParsing)) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
				}
				
				responseMessage = this.generatoreErrore.build(pddContext, integrationFunctionError,
						ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.
						getErrore432_MessaggioRichiestaMalformato(tParsing),
						tParsing,null);
			}
			else if( (responseMessage!=null && responseMessage.getParseException() != null) ||
					(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)) ||
					responseReadTimeout!=null ||
					responseLimitExceeded!=null){
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
				
				ParseException parseException = null;
				Throwable tParsing = null;
				Throwable sParsing = null;
				String msgErrore = null;
				if(responseReadTimeout != null) {
					tParsing = (TimeoutIOException) pddContext.getObject(TimeoutInputStream.EXCEPTION_KEY);
					sParsing = tParsing;
					msgErrore = tParsing.getMessage();
				}
				else if(responseLimitExceeded != null) {
					tParsing = (LimitExceededIOException) pddContext.getObject(LimitedInputStream.EXCEPTION_KEY);
					sParsing = tParsing;
					msgErrore = tParsing.getMessage();
				}
				else if( responseMessage!=null && responseMessage.getParseException() != null ){
					parseException = responseMessage.getParseException();
					tParsing = parseException.getParseException();
					sParsing = parseException.getSourceException();
					msgErrore = tParsing.getMessage();
				}
				else{
					parseException = (ParseException) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
					tParsing = parseException.getParseException();
					sParsing = parseException.getSourceException();
					msgErrore = tParsing.getMessage();
				}

				if(msgErrore==null && tParsing!=null){
					msgErrore = tParsing.toString();
				}			
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
				logCore.error("parsingExceptionRisposta",sParsing);
				
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT;
				if(responseReadTimeout!=null) {
					integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
				}
				else if(responseLimitExceeded!=null) {
					integrationFunctionError = IntegrationFunctionError.RESPONSE_SIZE_EXCEEDED;
				}
				else if(sParsing!=null &&
						TimeoutIOException.isTimeoutIOException(sParsing)) {
					integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
				}
				else if(sParsing!=null &&
						LimitExceededIOException.isLimitExceededIOException(sParsing)) {
					integrationFunctionError = IntegrationFunctionError.RESPONSE_SIZE_EXCEEDED;
				}
				
				msgDiag.logPersonalizzato("parsingExceptionRisposta");
				responseMessage = this.generatoreErrore.build(pddContext, integrationFunctionError,
						ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
						getErrore440_MessaggioRispostaMalformato(tParsing),
						tParsing,null);
			}
			
			
			try{
				// Se non sono stati recuperati i dati delle url, provo a recuperarli
				URLProtocolContext urlProtocolContext = context!=null ? context.getUrlProtocolContext() : null;
				if(urlProtocolContext==null){
					urlProtocolContext = req.getURLProtocolContext();
				}
				if(urlProtocolContext!=null && pddContext!=null){
					String urlInvocazione = urlProtocolContext.getUrlInvocazione_formBased();
					if(urlProtocolContext.getFunction()!=null){
						urlInvocazione = "["+urlProtocolContext.getFunction()+"] "+urlInvocazione;
					}
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.URL_INVOCAZIONE, urlInvocazione);
				}
			}catch(Throwable t){
				// ignore
			}
			try{
				Credenziali credenziali = context!=null ? context.getCredenziali() : null;
				if(credenziali==null){
					credenziali = new Credenziali(req.getCredential());
				}
				if(credenziali!=null && pddContext!=null){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CREDENZIALI_INVOCAZIONE, credenziali.toString());
				}
			}catch(Throwable t){
				// ignore
			}
				
			
			try{
				req.close();
			}catch(Exception e){
				logCore.error("Request.close() error: "+e.getMessage(),e);
			}
		}
		
		// Imposto risposta

		Date dataPrimaSpedizioneRisposta = DateManager.getDate();
		Date dataRispostaSpedita = null; 
		
		if(context.getMsgDiagnostico()!=null){
			msgDiag = context.getMsgDiagnostico();
		}
		if(context.getResponseHeaders()==null) {
			context.setResponseHeaders(new HashMap<>());
		}
		ServicesUtils.setGovWayHeaderResponse(requestMessage!=null ? requestMessage.getServiceBinding() : requestInfo.getProtocolServiceBinding(),
				responseMessage, openSPCoopProperties,
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
		
		OpenSPCoop2Message responseMessageError = null;
		EsitoTransazione esito = null;
		String descrizioneSoapFault = "";
		int statoServletResponse = 200;
		Throwable erroreConsegnaRisposta = null;
		boolean httpEmptyResponse = false;
		boolean erroreConnessioneClient = false;
		boolean erroreResponseTimeout = false;
		boolean erroreResponsePayloadTooLarge = false;
		boolean sendInvoked = false;
		try{
			if(responseMessage!=null && !responseMessage.isForcedEmptyResponse() && (responseMessage.getForcedResponse()==null)){
					
				// force response code
				if(responseMessage.getForcedResponseCode()!=null){
					try{
						statoServletResponse = Integer.parseInt(responseMessage.getForcedResponseCode());
					}catch(Exception e){
						// ignore
					}
				}
								
				// transfer length
				ServicesUtils.setTransferLength(openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi(), 
						req, res, responseMessage);
				
				// content type
				// Alcune implementazioni richiedono di aggiornare il Content-Type
				responseMessage.updateContentType();
				ServicesUtils.setContentType(responseMessage, res);
				
				// http status
				boolean consume = true;
				if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding()) ){
					//SOAPBody body = responseMessage.castAsSoap().getSOAPBody();
					OpenSPCoop2SoapMessage soapMessage = responseMessage.castAsSoap();
					if(soapMessage.hasSOAPFault()){
						consume = false; // può essere usato nel post out response handler
						statoServletResponse = 500;
						descrizioneSoapFault = " ("+SoapUtils.safe_toString(responseMessage.getFactory(), soapMessage.getSOAPBody().getFault(), false, logCore)+")";
					}
					else if(statoServletResponse==500) {
						// in SOAP 500 deve essere associato con un fault
						if(!soapMessage.isSOAPBodyEmpty()) {
							statoServletResponse = 200;
						}
						else {
							statoServletResponse = protocolFactory.createProtocolManager().getHttpReturnCodeEmptyResponseOneWay();
						}
					}
				}
				else if(responseMessage.castAsRest().isProblemDetailsForHttpApis_RFC7807() || 
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
					}catch(Exception e){
						// ignore
					}
				}
				else if(responseMessage!=null && responseMessage.getForcedResponseCode()!=null) {
					try{
						statoServletResponse = Integer.parseInt(responseMessage.getForcedResponseCode());
					}catch(Exception e){
						// ignore
					}
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
				if(responseMessage!=null && responseMessage.getForcedResponseCode()!=null) {
					try{
						statoServletResponse = Integer.parseInt(responseMessage.getForcedResponseCode());
					}catch(Exception e){
						// ignore
					}
				}
				else {
					statoServletResponse = protocolFactory.createProtocolManager().getHttpReturnCodeEmptyResponseOneWay();
				}
				res.setStatus(statoServletResponse);
				httpEmptyResponse = true;
				// carico-vuoto gestito all'interno
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(), 
						statoServletResponse, requestInfo.getIntegrationServiceBinding(),
						responseMessage, context.getProprietaErroreAppl(), informazioniErrori,
						pddContext);
			}
		}catch(Throwable e){			
			logCore.error("ErroreGenerale",e);
			erroreConsegnaRisposta = e;
						
			erroreConnessioneClient = ServicesUtils.isConnessioneClientNonDisponibile(e);
			if(!erroreConnessioneClient && ServicesUtils.isConnessioneServerReadTimeout(e)) {
				erroreResponseTimeout = true; // non e' stato possibile consegnare tutta la risposta. Il client ha ricevuto 200 ma non ha ricevuto la risposta per intero
				erroreConsegnaRisposta = new CoreException("Connessione con il backend dell'API non più disponibile: "+e.getMessage(),e);
			}
			if(!erroreConnessioneClient && !erroreResponseTimeout && ServicesUtils.isResponsePayloadTooLarge(e)) {
				erroreResponsePayloadTooLarge = true;  // non e' stato possibile consegnare tutta la risposta. Il client ha ricevuto 200 ma non ha ricevuto la risposta per intero
				erroreConsegnaRisposta = new CoreException("Risposta ricevuta dal backend dell'API non gestibile: "+e.getMessage(),e);
			}
			
			// Genero risposta con errore
			try{
				if(sendInvoked==false) {
					// nel caso sia già stato inoltrata una risposta non e' più possibile modificarlo cosi come tutti gli header etc...	
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
						responseMessageError = this.generatoreErrore.build(pddContext, IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT,
								ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
								getErrore440_MessaggioRispostaMalformato(parseException.getParseException()),
								parseException.getParseException(),null);
					} else{
						responseMessageError = this.generatoreErrore.build(pddContext, IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
								ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.
								getErrore426_ServletError(false, e),
								e,null);
					}
					
					// transfer length
					ServicesUtils.setTransferLength(openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi(), 
							req, res, responseMessageError);
					
					// content type
					ServicesUtils.setContentType(responseMessageError, res);
					
					// http status
					if(responseMessageError!=null && responseMessageError.getForcedResponseCode()!=null) {
						try{
							statoServletResponse = Integer.parseInt(responseMessageError.getForcedResponseCode());
							res.setStatus(statoServletResponse);
						}catch(Exception eStatus){
							// ignore
						}
					}
					if(responseMessageError!=null && ServiceBinding.SOAP.equals(responseMessageError.getServiceBinding()) ){
						SOAPBody body = responseMessageError.castAsSoap().getSOAPBody();
						if(body!=null && body.hasFault()){
							statoServletResponse = 500;
							res.setStatus(statoServletResponse);
							descrizioneSoapFault = " ("+SoapUtils.safe_toString(responseMessageError.getFactory(), body.getFault(), false, logCore)+")";
						}
					}
					
					// esito calcolato prima del sendResponse, per non consumare il messaggio
					esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(),
							statoServletResponse, requestInfo.getIntegrationServiceBinding(),
							responseMessageError, context.getProprietaErroreAppl(), informazioniErrori_error,
							pddContext);
					
					// contenuto
					// Il contentLenght, nel caso di TransferLengthModes.CONTENT_LENGTH e' gia' stato calcolato
					// con una writeTo senza consume. Riuso il solito metodo per evitare differenze di serializzazione
					// e cambiare quindi il content length effettivo.
					//if(TransferLengthModes.CONTENT_LENGTH.equals(openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi())){
					//	res.sendResponse(responseMessageError, false);
					//} else {
						//res.sendResponse(responseMessageError, true);
						res.sendResponse(responseMessageError, false); // può essere usato nel post out response handler
					//}
				}
																
			}catch(Throwable error){
				
				if(!erroreConnessioneClient){
					erroreConnessioneClient = ServicesUtils.isConnessioneClientNonDisponibile(error);
				}
				
				logCore.error("Generazione di un risposta errore non riuscita",error);
				statoServletResponse = 500;
				try{
					responseMessageError = this.generatoreErrore.buildFault(error, pddContext);
					if(responseMessageError!=null && responseMessageError.getForcedResponseCode()!=null) {
						try{
							statoServletResponse = Integer.parseInt(responseMessageError.getForcedResponseCode());
						}catch(Exception eStatus){
							// ignore
						}
					}
					
					try{
						res.setStatus(statoServletResponse);
					}catch(Exception eStatus){
						logCore.error("Response.setStatus("+statoServletResponse+") error: "+eStatus.getMessage(),eStatus);
					}
					
					res.sendResponse(responseMessageError, false); // può essere usato nel post out response handler
				}catch(Throwable  eError){
					if(!erroreConnessioneClient){
						erroreConnessioneClient = ServicesUtils.isConnessioneClientNonDisponibile(eError);
					}
					try {
						res.setStatus(statoServletResponse);
					}catch(Throwable t) {
						// ignore
					}
					try{
						res.sendResponse(DumpByteArrayOutputStream.newInstance(error.toString().getBytes()));
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
			
		}finally{
						
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
				
				dataRispostaSpedita = DateManager.getDate();
				
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
				
			} finally {
				if(dataRispostaSpedita==null) {
					dataRispostaSpedita = DateManager.getDate();
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
		else if(erroreResponseTimeout){
			// forzo esito errore response timeout
			try{
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_RESPONSE_TIMEOUT);
			}catch(Exception eBuildError){
				esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
			}
		}
		else if(erroreResponsePayloadTooLarge){
			// forzo esito errore dovuto alla policy di rate limiting
			try{
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(),EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA);
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
				postOutResponseContext.setDataRispostaSpedita(dataRispostaSpedita);
				postOutResponseContext.setEsito(esito);
				postOutResponseContext.setReturnCode(statoServletResponse);
				postOutResponseContext.setResponseHeaders(context.getResponseHeaders());
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
								requestMessage.writeTo(NullOutputStream.NULL_OUTPUT_STREAM, true);
							}catch(Exception eFlush){
								// ignore
							}
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
