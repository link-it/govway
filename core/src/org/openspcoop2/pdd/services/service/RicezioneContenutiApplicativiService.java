/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import org.openspcoop2.pdd.services.connector.AsyncResponseCallbackClientEvent;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherErrorInfo;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherInfo;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherUtils;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.IAsyncResponseCallback;
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
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.builder.InformazioniErroriInfrastrutturali;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
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



public class RicezioneContenutiApplicativiService implements IRicezioneService, IAsyncResponseCallback {

	
	private RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore;
	
	public RicezioneContenutiApplicativiService(RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore){
		this.generatoreErrore = generatoreErrore;
	}
	
	private RicezioneContenutiApplicativiContext context = null;
	private IProtocolFactory<?> protocolFactory = null;
	private OpenSPCoop2Properties openSPCoopProperties = null;
	
	private ConnectorInMessage req = null;
	private ConnectorOutMessage res = null;
	private Date dataAccettazioneRichiesta = null;
	private Date dataIngressoRichiesta = null;
	
	private RequestInfo requestInfo = null;
	private PdDContext pddContext = null;
	private OpenSPCoop2Message requestMessage = null;
	private OpenSPCoop2Message responseMessage = null;
	
	private Logger logCore = null;
	private MsgDiagnostico msgDiag=null;
	private DumpRaw dumpRaw = null;
	
	private String idModulo = null;
	private IDService idModuloAsService = null;
	private PostOutResponseContext postOutResponseContext = null;

	@Override
	public void process(ConnectorInMessage reqParam, ConnectorOutMessage resParam, Date dataAccettazioneRichiesta, boolean async) throws ConnectorException {

		this.req = reqParam;
		this.res = resParam;
		this.dataAccettazioneRichiesta = dataAccettazioneRichiesta;
		
		// IDModulo
		this.idModulo = this.req.getIdModulo();
		this.idModuloAsService = this.req.getIdModuloAsIDService();
		this.requestInfo = this.req.getRequestInfo();
				
		// Log
		this.logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.logCore==null)
			this.logCore = LoggerWrapperFactory.getLogger(this.idModulo);

		this.openSPCoopProperties = OpenSPCoop2Properties.getInstance();
		

		/* ------------  PreInHandler (PreInAcceptRequestContext) ------------- */
		
		PreInAcceptRequestContext preInAcceptRequestContext = null;
		if (this.openSPCoopProperties != null && OpenSPCoop2Startup.initialize) {
		
			// build context
			preInAcceptRequestContext = new PreInAcceptRequestContext();
			preInAcceptRequestContext.setTipoPorta(TipoPdD.DELEGATA);
			preInAcceptRequestContext.setIdModulo(this.idModulo);
			preInAcceptRequestContext.setRequestInfo(this.requestInfo);	
			preInAcceptRequestContext.setLogCore(this.logCore);
			
			// valori che verranno aggiornati dopo
			try {
				if(this.openSPCoopProperties.isConnettoriUseLimitedInputStream()) {
					SogliaDimensioneMessaggio soglia = new SogliaDimensioneMessaggio();
					soglia.setSogliaKb(this.openSPCoopProperties.getLimitedInputStreamThresholdKb());
					soglia.setPolicyGlobale(true);
					soglia.setNomePolicy("GovWayCore");
					soglia.setIdPolicyConGruppo("GovWayCore");
					this.req.setRequestLimitedStream(soglia);
				}
				if(this.openSPCoopProperties.isConnettoriUseTimeoutInputStream()) {
					this.req.setRequestReadTimeout(this.openSPCoopProperties.getReadConnectionTimeout_ricezioneContenutiApplicativi());
				}
				this.req.setThresholdContext(null, 
					this.openSPCoopProperties.getDumpBinario_inMemoryThreshold(), this.openSPCoopProperties.getDumpBinario_repository());
			}catch(Throwable t) {
				this.logCore.error(t.getMessage(),t);
			}
			preInAcceptRequestContext.setReq(this.req);
			
			// invocazione handler
			GestoreHandlers.preInRequest(preInAcceptRequestContext, this.logCore, this.logCore);
			
		}
		
		
		
		// GeneratoreErrore
		try{
			if(this.generatoreErrore==null){
				this.generatoreErrore = 
						new RicezioneContenutiApplicativiInternalErrorGenerator(this.logCore, RicezioneContenutiApplicativiConnector.ID_MODULO, this.requestInfo);
			}
		}catch(Exception e){
			String msg = "Inizializzazione Generatore Errore fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
			this.logCore.error(msg,e);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(this.requestInfo, this.generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
					IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, e, null, this.res, this.logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			this.res.close(false);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.logCore, this.req, null, dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		// Proprieta' OpenSPCoop
		if (!OpenSPCoop2Startup.initialize || this.openSPCoopProperties == null) {
			String msg = "Inizializzazione di GovWay non correttamente effettuata: OpenSPCoopProperties";
			if(!OpenSPCoop2Startup.initialize) {
				msg = "Inizializzazione di GovWay non correttamente effettuata";
			}
			this.logCore.error(msg);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(this.requestInfo, this.generatoreErrore, 
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
						IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, null, null, this.res, this.logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			this.res.close(false);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.logCore, this.req, null, dataAccettazioneRichiesta, cInfo);
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
			this.logCore.error(msg);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(this.requestInfo, this.generatoreErrore, 
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
						IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, e, null, this.res, this.logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			this.res.close(false);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.logCore, this.req, null, dataAccettazioneRichiesta, cInfo);
			return;
		}
			
		// PddContext from servlet
		Object oPddContextFromServlet = null;
		try{
			oPddContextFromServlet = this.req.getAttribute(CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP);
		}catch(Exception e){
			this.logCore.error("this.req.getAttribute("+CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP+") error: "+e.getMessage(),e);
		}
		PdDContext pddContextFromServlet = null;
		if(oPddContextFromServlet!=null){
			pddContextFromServlet = (PdDContext) oPddContextFromServlet;
		}
				
		// Identifico Servizio per comprendere correttamente il messageType
		ServiceIdentificationReader serviceIdentificationReader = null;
		try{
			serviceIdentificationReader = ServicesUtils.getServiceIdentificationReader(this.logCore, this.requestInfo,
					configPdDManager.getRegistroServiziManager(), configPdDManager);
		}catch(Exception e){
			String msg = "Inizializzazione RegistryReader fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
			this.logCore.error(msg,e);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(this.requestInfo, this.generatoreErrore,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA),
						IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, e, null, this.res, this.logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			this.res.close(false);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.logCore, this.req, pddContextFromServlet, dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		// Provo a creare un context (per l'id di transazione nei diagnostici)
		try {
			this.context = new RicezioneContenutiApplicativiContext(this.idModuloAsService,dataAccettazioneRichiesta,this.requestInfo);
			this.protocolFactory = this.req.getProtocolFactory();
			String idTransazione = (String)this.context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			if(this.openSPCoopProperties.isTransazioniEnabled()) {
				TransactionContext.createTransaction(idTransazione, "RicezioneContenutiApplicativi.1");
			}
			this.requestInfo.setIdTransazione(idTransazione);
			
			this.req.setThresholdContext((this.context!=null ? this.context.getPddContext(): null), 
					this.openSPCoopProperties.getDumpBinario_inMemoryThreshold(), this.openSPCoopProperties.getDumpBinario_repository());
			
		}catch(Throwable e) {
			this.context = null;
			this.protocolFactory = null;
			// non loggo l'errore tanto poi provo a ricreare il context subito dopo e li verra' registrato l'errore
		}
		
		// Logger dei messaggi diagnostici
		String nomePorta = this.requestInfo.getProtocolContext().getInterfaceName();
		this.msgDiag = MsgDiagnostico.newInstance(TipoPdD.DELEGATA,this.idModulo,nomePorta);
		this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
		if(this.context!=null && this.protocolFactory!=null) {
			this.msgDiag.setPddContext(this.context.getPddContext(), this.protocolFactory);
		}
		
		// emitDiagnostic preAccept handler
		GestoreHandlers.emitDiagnostic(this.msgDiag, preInAcceptRequestContext, this.context!=null ? this.context.getPddContext() : null, 
				this.logCore, this.logCore);
		
		// Aggiorno RequestInfo
		ConnectorDispatcherInfo cInfo = RicezioneContenutiApplicativiServiceUtils.updatePortaDelegataRequestInfo(this.requestInfo, this.logCore, this.req, this.res,
				this.generatoreErrore, serviceIdentificationReader, this.msgDiag, 
				this.context!=null ? this.context.getPddContext(): null);
		if(cInfo!=null){
			this.res.close(false);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.context, this.logCore, this.req, pddContextFromServlet, dataAccettazioneRichiesta, cInfo);
			return; // l'errore in response viene impostato direttamente dentro il metodo
		}
		this.req.updateRequestInfo(this.requestInfo);
					
		// Timeout, DimensioneMessaggi e DumpRaw
		try{
			boolean dumpBinario = configPdDManager.dumpBinarioPD();
			PortaDelegata pd = null;
			if(this.requestInfo!=null && this.requestInfo.getProtocolContext()!=null && this.requestInfo.getProtocolContext().getInterfaceName()!=null) {
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(this.requestInfo.getProtocolContext().getInterfaceName());
				pd = configPdDManager.getPortaDelegata_SafeMethod(idPD);
			}

			// Limited
			String azione = (this.requestInfo!=null && this.requestInfo.getIdServizio()!=null) ? this.requestInfo.getIdServizio().getAzione() : null;
			SoglieDimensioneMessaggi limitedInputStream = configPdDManager.getSoglieLimitedInputStream(pd, azione, this.idModulo,
					(this.context!=null && this.context.getPddContext()!=null) ? this.context.getPddContext() : null, 
					(this.requestInfo!=null) ? this.requestInfo.getProtocolContext() : null,
					this.protocolFactory, this.logCore);
			if(limitedInputStream!=null) {
				this.req.setRequestLimitedStream(limitedInputStream.getRichiesta());
				if(this.context!=null && this.context.getPddContext()!=null) {
					this.context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.LIMITED_STREAM, limitedInputStream.getRisposta());
				}
			}
			else {
				if(!this.openSPCoopProperties.isLimitedInputStreamThresholdDefined()) {
					this.req.disableLimitedStream();
				}
			}
			
			// Timeout
			boolean useTimeoutInputStream = configPdDManager.isConnettoriUseTimeoutInputStream(pd);
			if(useTimeoutInputStream) {
				int timeout = configPdDManager.getRequestReadTimeout(pd);
				if(timeout>0) {
					this.req.setRequestReadTimeout(timeout);
				}
				else {
					this.req.disableReadTimeout();
				}
			}
			else {
				this.req.disableReadTimeout();
			}
			
			// DumpRaw
			DumpConfigurazione dumpConfigurazione = configPdDManager.getDumpConfigurazione(pd);
			boolean fileTrace = configPdDManager.isTransazioniFileTraceEnabled(pd) && configPdDManager.isTransazioniFileTraceDumpBinarioEnabled(pd);
			this.dumpRaw = new DumpRaw(this.logCore,this.requestInfo.getIdentitaPdD(), this.idModulo, TipoPdD.DELEGATA, 
					dumpBinario, 
					dumpConfigurazione,
					fileTrace);
			if(this.dumpRaw.isActiveDumpRichiesta()) {
				this.req = new DumpRawConnectorInMessage(this.logCore, this.req, 
						(this.context!=null ? this.context.getPddContext(): null), 
						this.openSPCoopProperties.getDumpBinario_inMemoryThreshold(), this.openSPCoopProperties.getDumpBinario_repository());
			}
			if(this.dumpRaw.isActiveDumpRisposta()) {
				this.res = new DumpRawConnectorOutMessage(this.logCore, this.res, 
						(this.context!=null ? this.context.getPddContext(): null), 
						this.openSPCoopProperties.getDumpBinario_inMemoryThreshold(), this.openSPCoopProperties.getDumpBinario_repository());
			}
		}catch(Throwable e){
			String msg = "Inizializzazione di GovWay non correttamente effettuata: DumpRaw";
			this.logCore.error(msg,  e);
			cInfo = ConnectorDispatcherUtils.doError(this.requestInfo, this.generatoreErrore, 
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
						IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, e, null, this.res, this.logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			this.res.close(false);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.logCore, this.req, pddContextFromServlet, dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		// API Soap supporta solo POST e ?wsdl
		if(ServiceBinding.SOAP.equals(this.requestInfo.getIntegrationServiceBinding())){
			HttpRequestMethod method = null;
			if(this.req!=null && this.req.getURLProtocolContext()!=null && this.req.getURLProtocolContext().getRequestType()!=null) {
				try {
					method = HttpRequestMethod.valueOf(this.req.getURLProtocolContext().getRequestType());
				}catch(Exception e) {}
			}
			if(method!=null && !HttpRequestMethod.POST.equals(method)){
				if(ServicesUtils.isRequestWsdl(this.req, this.logCore)) {
					try {
						ServicesUtils.writeWsdl(this.res, this.requestInfo, RicezioneContenutiApplicativiConnector.ID_SERVICE, serviceIdentificationReader, this.logCore);
					}catch(Exception e) {
						String msg = "Lettura wsdl fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
						this.logCore.error(msg,e);
						cInfo = ConnectorDispatcherUtils.doError(this.requestInfo, this.generatoreErrore,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),
									IntegrationFunctionError.INTERNAL_REQUEST_ERROR, e, null, this.res, this.logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
						// nel caso di wsdl request non emetto la transazione
						//RicezioneContenutiApplicativiServiceUtils.emitTransaction(context,logCore, req, pddContextFromServlet, dataAccettazioneRichiesta, cInfo);
					}finally {
						// FIX devo però rilasciare dalla memoria la transazione:
						if(this.openSPCoopProperties.isTransazioniEnabled()) {
							String idTransazione = (String)this.context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
							TransactionContext.removeTransaction(idTransazione);
						}
					}
					this.res.close(false);
					return;
				}
				else {
					String msg = "Metodo http '"+method+"' non supportato dall'API SOAP invocata";
					this.logCore.error(msg);
					ConnectorDispatcherErrorInfo cInfoError =  ConnectorDispatcherUtils.doError(this.requestInfo, this.generatoreErrore,
							ErroriIntegrazione.ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL.getErrore439_FunzionalitaNotSupportedByProtocol(msg, this.protocolFactory),
							IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL, null, null, this.res, this.logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
					this.res.close(false);
					RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.context,this.logCore, this.req, pddContextFromServlet, dataAccettazioneRichiesta, cInfoError);
					return;
				}
			}
		}
		
		


		
		/* ------------  Lettura parametri della richiesta ------------- */
				
		String protocol = null;
		MessageType messageTypeReq = null;
		ServiceBinding integrationServiceBinding = null;
		boolean completeProcess = false;
		try{
			
			/* --------------- Creo il context che genera l'id univoco ----------------------- */
			
			if(this.protocolFactory==null) {
				this.protocolFactory = this.req.getProtocolFactory();
			}
			protocol = this.protocolFactory.getProtocol();
					
			integrationServiceBinding = this.requestInfo.getIntegrationServiceBinding();
			
			if(this.context==null) {
				this.context = new RicezioneContenutiApplicativiContext(this.idModuloAsService,dataAccettazioneRichiesta,this.requestInfo);
			}
			if(preInAcceptRequestContext!=null && preInAcceptRequestContext.getPreContext()!=null && !preInAcceptRequestContext.getPreContext().isEmpty()) {
				this.context.getPddContext().addAll(preInAcceptRequestContext.getPreContext(), false);
			}
			this.context.setTipoPorta(TipoPdD.DELEGATA);
			this.context.setIdModulo(this.idModulo);
			this.context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME, this.protocolFactory.getProtocol());
			this.context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO, this.req.getRequestInfo());
			RicezionePropertiesConfig rConfig = RicezioneContenutiApplicativiServiceUtils.readPropertiesConfig(this.req.getRequestInfo(), this.logCore,null);
			if(rConfig!=null) {
	            if (rConfig.getApiImplementation() != null && !rConfig.getApiImplementation().isEmpty()) {
	            	this.context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_CONFIGURAZIONE, rConfig.getApiImplementation());
	            }
	            if (rConfig.getSoggettoFruitore() != null && !rConfig.getSoggettoFruitore().isEmpty()) {
	            	this.context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_FRUITORE, rConfig.getSoggettoFruitore());
	            }
	            if (rConfig.getSoggettoErogatore() != null && !rConfig.getSoggettoErogatore().isEmpty()) {
	            	this.context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_EROGATORE, rConfig.getSoggettoErogatore());
	            }
			}
			this.context.setProprietaErroreAppl(this.generatoreErrore.getProprietaErroreAppl());
			this.msgDiag.setPddContext(this.context.getPddContext(), this.protocolFactory);
			this.pddContext = this.context.getPddContext();
			
			try{
				if(this.openSPCoopProperties.isTransazioniEnabled()) {
					// NOTA: se gia' esiste con l'id di transazione, non viene ricreata
					TransactionContext.createTransaction((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE), "RicezioneContenutiApplicativi.2");
				}
			}catch(Exception e){
				this.logCore.error("Errore durante la creazione della transazione",e);
			}
			
			try{
				this.msgDiag.logPersonalizzato("ricezioneRichiesta.firstLog");
			}catch(Exception e){
				this.logCore.error("Errore generazione diagnostico di ingresso",e);
			}
			
			if(this.dumpRaw!=null && this.dumpRaw.isActiveDump()){
				this.dumpRaw.setPddContext(this.msgDiag.getPorta(), this.context.getPddContext());
				this.dumpRaw.serializeContext(this.context, protocol);
			}
			
			DirectVMConnectorInMessage vm = null;
			if(this.req instanceof DirectVMConnectorInMessage){
				vm = (DirectVMConnectorInMessage) this.req;
			}
			else if(this.req instanceof DumpRawConnectorInMessage){
				if( ((DumpRawConnectorInMessage)this.req).getWrappedConnectorInMessage() instanceof DirectVMConnectorInMessage ){
					vm = (DirectVMConnectorInMessage) ((DumpRawConnectorInMessage)this.req).getWrappedConnectorInMessage();
				}
			}
			if(vm!=null && vm.getDirectVMProtocolInfo()!=null){
				vm.getDirectVMProtocolInfo().setInfo(this.pddContext);
			}
			
			
			
			
			/* ------------  PostOutResponseContext ------------- */
			
			this.postOutResponseContext = new PostOutResponseContext(this.logCore,this.protocolFactory);
			this.postOutResponseContext.setTipoPorta(TipoPdD.DELEGATA);
			this.postOutResponseContext.setPddContext(this.pddContext);
			this.postOutResponseContext.setIdModulo(this.idModulo);
			
			
			
			
			/* ------------  PreInHandler ------------- */
			
			// build context
			PreInRequestContext preInRequestContext = new PreInRequestContext(this.pddContext);
			if(pddContextFromServlet!=null){
				preInRequestContext.getPddContext().addAll(pddContextFromServlet, true);
			}
			preInRequestContext.setTipoPorta(TipoPdD.DELEGATA);
			preInRequestContext.setIdModulo(this.idModulo);
			preInRequestContext.setProtocolFactory(this.protocolFactory);
			preInRequestContext.setRequestInfo(this.requestInfo);
			Map<String, Object> transportContext = new HashMap<String, Object>();
			transportContext.put(PreInRequestContext.SERVLET_REQUEST, this.req);
			transportContext.put(PreInRequestContext.SERVLET_RESPONSE, this.res);
			preInRequestContext.setTransportContext(transportContext);	
			preInRequestContext.setLogCore(this.logCore);
			
			// invocazione handler
			GestoreHandlers.preInRequest(preInRequestContext, this.msgDiag, this.logCore);
			
			// aggiungo eventuali info inserite nel preInHandler
			this.pddContext.addAll(preInRequestContext.getPddContext(), false);
			
			// Lettura risposta parametri NotifierInputStream
			NotifierInputStreamParams notifierInputStreamParams = preInRequestContext.getNotifierInputStreamParams();
			this.context.setNotifierInputStreamParams(notifierInputStreamParams);
			
			if(this.dumpRaw!=null && this.dumpRaw.isActiveDumpRichiesta()){
				this.dumpRaw.serializeRequest(((DumpRawConnectorInMessage)this.req), true, notifierInputStreamParams);
				this.dataIngressoRichiesta = this.req.getDataIngressoRichiesta();
				this.context.setDataIngressoRichiesta(this.dataIngressoRichiesta);
			}
			
			
				
			
			
			/* ------------ Controllo ContentType -------------------- */
			
			this.msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.tipologiaMessaggio");
			
			messageTypeReq = this.requestInfo.getIntegrationRequestMessageType();
			if(ServiceBinding.SOAP.equals(integrationServiceBinding) && messageTypeReq!=null){
				this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_VERSION, messageTypeReq.getMessageVersionAsString());
				this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE_ATTESO, SoapUtils.getSoapEnvelopeNS(messageTypeReq));
			}
			
			String contentTypeReq = this.req.getContentType();
			boolean contentTypeSupportato = messageTypeReq!=null;
			this.msgDiag.addKeyword(CostantiPdD.KEY_CONTENT_TYPES_ATTESI, 
					this.requestInfo.getBindingConfig().getContentTypesSupportedAsString(integrationServiceBinding, 
							MessageRole.REQUEST,this.requestInfo.getProtocolContext()));
			List<String> supportedContentTypes = this.requestInfo.getBindingConfig().getContentTypesSupported(integrationServiceBinding, 
					MessageRole.REQUEST,this.requestInfo.getProtocolContext()); 
			this.msgDiag.addKeyword(CostantiPdD.KEY_HTTP_HEADER, contentTypeReq);
			
			if(ServiceBinding.SOAP.equals(integrationServiceBinding)){
				if(this.openSPCoopProperties.isControlloContentTypeAbilitatoRicezioneContenutiApplicativi() == false){
					if(!contentTypeSupportato){
						if(HttpConstants.CONTENT_TYPE_NON_PRESENTE.equals(contentTypeReq)){
							this.msgDiag.logPersonalizzato("contentType.notDefined");
						}else{
							this.msgDiag.logPersonalizzato("contentType.unsupported");
						}
						messageTypeReq = MessageType.SOAP_11;
						contentTypeReq = SoapUtils.getSoapContentTypeForMessageWithoutAttachments(messageTypeReq); // Forzo text/xml;
						this.logCore.warn("Content-Type non supportato, viene utilizzato forzatamente il tipo: "+contentTypeReq);
						contentTypeSupportato = true;
					}
				}
			}
			if(!contentTypeSupportato){
				
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				
				// Leggo content Type dall'header HTTP per capire se l'header proprio non esiste o se e' il valore errato.
				if(HttpConstants.CONTENT_TYPE_NON_PRESENTE.equals(contentTypeReq)){
					//ContentType del messaggio non presente
					this.msgDiag.logPersonalizzato("contentType.notDefined");
					this.responseMessage = this.generatoreErrore.build(this.pddContext, IntegrationFunctionError.CONTENT_TYPE_NOT_PROVIDED,
							ErroriIntegrazione.ERRORE_433_CONTENT_TYPE_NON_PRESENTE.
							getErrore433_ContentTypeNonPresente(supportedContentTypes),null,null);
				}
				else{
					//ContentType del messaggio non supportato
					this.msgDiag.logPersonalizzato("contentType.unsupported");
					this.responseMessage = this.generatoreErrore.build(this.pddContext, IntegrationFunctionError.CONTENT_TYPE_NOT_SUPPORTED,
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
						if(this.openSPCoopProperties.isControlloCharsetContentTypeAbilitatoRicezioneContenutiApplicativiSoap()) {
							checkEnabled = true;
							ctDefault = this.openSPCoopProperties.getControlloCharsetContentTypeAbilitatoRicezioneContenutiApplicativiSoap();
						}
					}
					else {
						if(this.openSPCoopProperties.isControlloCharsetContentTypeAbilitatoRicezioneContenutiApplicativiRest()) {
							checkEnabled = true;
							ctDefault = this.openSPCoopProperties.getControlloCharsetContentTypeAbilitatoRicezioneContenutiApplicativiRest();
						}
					}
					if(checkEnabled) {
						ServicesUtils.checkCharset(contentTypeReq, ctDefault, this.msgDiag, true, TipoPdD.DELEGATA);
					}
					
				}catch(Throwable t) {
					this.logCore.error("Avvenuto errore durante il controllo del charset della richiesta: "+t.getMessage(),t);
				}
				
				/* ------------  SoapAction check 1 (controlla che non sia null e ne ritorna il valore) ------------- */			
				String soapAction = null;
				try{
					if(ServiceBinding.SOAP.equals(integrationServiceBinding)){
						soapAction = this.req.getSOAPAction();
					}
				}catch(Exception e){
					if(this.dataIngressoRichiesta==null) {
						this.dataIngressoRichiesta=DateManager.getDate();
					}
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					throw e;
				}
				
				/* ------------ Costruzione Messaggio -------------------- */
				this.msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.inCorso");
				Utilities.printFreeMemory("RicezioneContenutiApplicativi - Pre costruzione richiesta");
				OpenSPCoop2MessageParseResult pr = this.req.getRequest(notifierInputStreamParams);
				if(pr.getParseException()!=null){
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
				}
				this.dataIngressoRichiesta = this.req.getDataIngressoRichiesta();
				this.context.setDataIngressoRichiesta(this.dataIngressoRichiesta);
				this.requestMessage = pr.getMessage_throwParseException();
				Utilities.printFreeMemory("RicezioneContenutiApplicativi - Post costruzione richiesta");
				this.requestMessage.setProtocolName(this.protocolFactory.getProtocol());
				this.requestMessage.setTransactionId(PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, this.pddContext));
				this.requestMessage.addContextProperty(org.openspcoop2.core.constants.Costanti.REQUEST_INFO,this.requestInfo); // serve nelle comunicazione non stateless (es. riscontro salvato) per poterlo rispedire
				this.requestMessage.addContextProperty(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE,this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)); // serve nelle comunicazione non stateless (es. riscontro salvato) per poterlo rispedire
				Object nomePortaInvocataObject = this.context.getPddContext().getObject(CostantiPdD.NOME_PORTA_INVOCATA);
				if(nomePortaInvocataObject!=null && nomePortaInvocataObject instanceof String) {
					this.requestMessage.addContextProperty(CostantiPdD.NOME_PORTA_INVOCATA, (String) nomePortaInvocataObject );
				}
								
				/* ------------ Controllo Soap namespace -------------------- */
				String soapEnvelopeNamespaceVersionMismatch = null;
				try{
					if(ServiceBinding.SOAP.equals(integrationServiceBinding)){
						soapEnvelopeNamespaceVersionMismatch = ServicesUtils.checkSOAPEnvelopeNamespace(this.requestMessage.castAsSoap(), messageTypeReq);
					}
				}catch(Exception e){
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					throw e;
				}
				
				/* ------------ Controllo MustUnderstand -------------------- */
				String mustUnderstandError = null;
				if(soapEnvelopeNamespaceVersionMismatch==null) {
					try{
						if(ServiceBinding.SOAP.equals(integrationServiceBinding)){
							mustUnderstandError = ServicesUtils.checkMustUnderstand(this.requestMessage.castAsSoap(),this.protocolFactory);
						}
					}catch(Exception e){
						this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
						throw e;
					}
				}
				
				/* ------------  SoapAction check 2 ------------- */
				if(soapAction!=null){
					if(this.openSPCoopProperties.checkSoapActionQuotedString_ricezioneContenutiApplicativi()){
						try{
							SoapUtils.checkSoapActionQuotedString(soapAction, messageTypeReq);
						}catch(Exception e){
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
							throw e;
						}
					}
				}
				
							
				/* ------------  Elaborazione ------------- */
			
				if(mustUnderstandError==null && soapEnvelopeNamespaceVersionMismatch==null){
				
					// Contesto di Richiesta
					this.context.setCredenziali(new Credenziali(this.req.getCredential()));
					this.context.setGestioneRisposta(true); // siamo in una servlet, la risposta deve essere aspettata
					this.context.setInvocazionePDPerRiferimento(false); // la PD con questa servlet non effettuera' mai invocazioni per riferimento.
					this.context.setMessageRequest(this.requestMessage);
					this.context.setUrlProtocolContext(this.requestInfo.getProtocolContext());
					this.context.setMsgDiagnostico(this.msgDiag);
	
					// Log elaborazione dati completata
					this.msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.completata");
			
					// Invocazione...
					RicezioneContenutiApplicativi gestoreRichiesta = new RicezioneContenutiApplicativi(this.context, this.generatoreErrore, 
							async ? this : null);
					gestoreRichiesta.process(this.req);
					completeProcess = true;
				}	
				else{
					if(mustUnderstandError!=null){
						this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
						this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, mustUnderstandError);
						this.msgDiag.logPersonalizzato("mustUnderstand.unknown");
						this.responseMessage = this.generatoreErrore.build(this.pddContext, IntegrationFunctionError.SOAP_MUST_UNDERSTAND_UNKNOWN,
								ErroriIntegrazione.ERRORE_427_MUSTUNDERSTAND_ERROR.
								getErrore427_MustUnderstandHeaders(mustUnderstandError),null,null);
					}
					else{
						this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
						this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE, soapEnvelopeNamespaceVersionMismatch);
						this.msgDiag.logPersonalizzato("soapEnvelopeNamespace.versionMismatch");
						this.responseMessage = this.generatoreErrore.build(this.pddContext, IntegrationFunctionError.SOAP_VERSION_MISMATCH,
								ErroriIntegrazione.ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR.
								getErrore430_SoapNamespaceNonSupportato(messageTypeReq, soapEnvelopeNamespaceVersionMismatch),null,null);
					}
				}
			}
			
		}
		catch (Throwable e) {
			
			if(this.context==null){
				// Errore durante la generazione dell'id
				this.context = RicezioneContenutiApplicativiContext.newRicezioneContenutiApplicativiContext(this.idModuloAsService,dataAccettazioneRichiesta,this.requestInfo);
				this.context.setDataIngressoRichiesta(this.dataIngressoRichiesta);
				this.context.setTipoPorta(TipoPdD.DELEGATA);
				this.context.setIdModulo(this.idModulo);
				this.context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME, this.protocolFactory.getProtocol());
				this.pddContext = this.context.getPddContext();
				if(this.postOutResponseContext!=null){
					this.postOutResponseContext.setPddContext(this.pddContext);
				}
				this.msgDiag.setPddContext(this.pddContext, this.protocolFactory);
			}
			
			// Se viene lanciata una eccezione, riguarda la richiesta, altrimenti è gestita dopo nel finally.
			Throwable tParsing = null;
			ParseException parseException = null;
			if(this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)){
				parseException = (ParseException) this.pddContext.removeObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);			
				if(parseException!=null) {
					tParsing = parseException.getParseException();
				}
			}
			if(tParsing==null && (this.requestMessage==null || this.requestMessage.getParseException() == null)){
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
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_ENVELOPE_NAMESPACE, "Impossibile recuperare il valore del namespace");
				this.msgDiag.logPersonalizzato("soapEnvelopeNamespace.versionMismatch");
				this.responseMessage = this.generatoreErrore.build(this.pddContext, IntegrationFunctionError.SOAP_VERSION_MISMATCH,
						ErroriIntegrazione.ERRORE_430_SOAP_ENVELOPE_NAMESPACE_ERROR.
						getErrore430_SoapNamespaceNonSupportato(messageTypeReq, "Impossibile recuperare il valore del namespace"),e,null);
			} else if(tParsing!=null){
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				msgErrore = tParsing.getMessage();
				if(msgErrore==null){
					msgErrore = tParsing.toString();
				}
				this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
				this.logCore.error("parsingExceptionRichiesta",e);
				this.msgDiag.logPersonalizzato("parsingExceptionRichiesta");
				
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
				if( parseException!=null && parseException.getSourceException()!=null &&
						TimeoutIOException.isTimeoutIOException(parseException.getSourceException())) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
				}
				else if( parseException!=null && parseException.getSourceException()!=null &&
						LimitExceededIOException.isLimitExceededIOException(parseException.getSourceException())) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
				}
				
				this.responseMessage = this.generatoreErrore.build(this.pddContext, integrationFunctionError,
						ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.
						getErrore432_MessaggioRichiestaMalformato(tParsing),tParsing,null);
			} 
			else if (e instanceof HandlerException) {
				this.logCore.error("ErroreGenerale (HandlerException)",e);
				HandlerException he = (HandlerException) e;
				if(he.isEmettiDiagnostico()) {
					this.msgDiag.logErroreGenerico(e, "Generale(richiesta-handler)");
				}
				ErroreIntegrazione errore = he.convertToErroreIntegrazione();
				if(errore==null) {
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Generale(richiesta)");
				}
				IntegrationFunctionError integrationError = he.getIntegrationFunctionError();
				if(integrationError==null) {
					integrationError = IntegrationFunctionError.BAD_REQUEST;
				}
				this.responseMessage = this.generatoreErrore.build(this.pddContext,integrationError,errore,e,null);
				he.customized(this.responseMessage);
			}
			else {
				this.logCore.error("ErroreGenerale",e);
				this.msgDiag.logErroreGenerico(e, "Generale(richiesta)");
				this.responseMessage = this.generatoreErrore.build(this.pddContext, IntegrationFunctionError.BAD_REQUEST,
						ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.
						getErrore426_ServletError(true, e),e,null);
			}
		}
		finally{
			
			if(!completeProcess || !async) {
				this._complete(AsyncResponseCallbackClientEvent.NONE,completeProcess);
			}

		}

	}

	@Override
	public void asyncComplete(AsyncResponseCallbackClientEvent clientEvent, Object ... args) throws ConnectorException { // Questo metodo verrà chiamato dalla catena di metodi degli oggetti (IAsyncResponseCallback) fatta scaturire dal response callback dell'Async Client NIO
		this._complete(clientEvent, true);
	}
	private void _complete(AsyncResponseCallbackClientEvent clientEvent, boolean completeProcess) throws ConnectorException {

		if(completeProcess) {
			this.responseMessage = this.context.getMessageResponse();
		}


		// Finally Request	
		String requestReadTimeout = null;
		String responseReadTimeout = null;
		if(this.pddContext!=null && this.pddContext.containsKey(TimeoutInputStream.ERROR_MSG_KEY)) {
			String timeoutMessage = PdDContext.getValue(TimeoutInputStream.ERROR_MSG_KEY, this.pddContext);
			if(timeoutMessage!=null && timeoutMessage.startsWith(CostantiPdD.PREFIX_TIMEOUT_REQUEST)) {
				requestReadTimeout = timeoutMessage;
			}
			else if(timeoutMessage!=null && timeoutMessage.startsWith(CostantiPdD.PREFIX_TIMEOUT_RESPONSE)) {
				responseReadTimeout = timeoutMessage;
			}
		}
		String requestLimitExceeded = null;
		String responseLimitExceeded = null;
		if(this.pddContext!=null && this.pddContext.containsKey(LimitedInputStream.ERROR_MSG_KEY)) {
			String limitedExceededMessage = PdDContext.getValue(LimitedInputStream.ERROR_MSG_KEY, this.pddContext);
			if(limitedExceededMessage!=null && limitedExceededMessage.startsWith(CostantiPdD.PREFIX_LIMITED_REQUEST)) {
				requestLimitExceeded = limitedExceededMessage;
			}
			else if(limitedExceededMessage!=null && limitedExceededMessage.startsWith(CostantiPdD.PREFIX_LIMITED_RESPONSE)) {
				responseLimitExceeded = limitedExceededMessage;
			}
		}
		
		if((this.requestMessage!=null && this.requestMessage.getParseException() != null) || 
				(this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)) ||
				requestReadTimeout!=null ||
				requestLimitExceeded!=null){
			this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
			
			ParseException parseException = null;
			Throwable tParsing = null;
			Throwable sParsing = null;
			String msgErrore = null;
			if(requestReadTimeout != null) {
				tParsing = (TimeoutIOException) this.pddContext.getObject(TimeoutInputStream.EXCEPTION_KEY);
				sParsing = tParsing;
				msgErrore = tParsing.getMessage();
			}
			else if(requestLimitExceeded != null) {
				tParsing = (LimitExceededIOException) this.pddContext.getObject(LimitedInputStream.EXCEPTION_KEY);
				sParsing = tParsing;
				msgErrore = tParsing.getMessage();
			}
			else if( this.requestMessage!=null && this.requestMessage.getParseException() != null ){
				parseException = this.requestMessage.getParseException();
				tParsing = parseException.getParseException();
				sParsing = parseException.getSourceException();
				msgErrore = tParsing.getMessage();
			}
			else {
				parseException = (ParseException) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
				tParsing = parseException.getParseException();
				sParsing = parseException.getSourceException();
				msgErrore = tParsing.getMessage();
			}			
			
			if(msgErrore==null && tParsing!=null){
				msgErrore = tParsing.toString();
			}
			this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
			this.logCore.error("parsingExceptionRichiesta",sParsing);
			this.msgDiag.logPersonalizzato("parsingExceptionRichiesta");
			
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
			
			this.responseMessage = this.generatoreErrore.build(this.pddContext, integrationFunctionError,
					ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.
					getErrore432_MessaggioRichiestaMalformato(tParsing),
					tParsing,null);
		}
		else if( (this.responseMessage!=null && this.responseMessage.getParseException() != null) ||
				(this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)) ||
				responseReadTimeout!=null ||
				responseLimitExceeded!=null){
			this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
			
			ParseException parseException = null;
			Throwable tParsing = null;
			Throwable sParsing = null;
			String msgErrore = null;
			if(responseReadTimeout != null) {
				tParsing = (TimeoutIOException) this.pddContext.getObject(TimeoutInputStream.EXCEPTION_KEY);
				sParsing = tParsing;
				msgErrore = tParsing.getMessage();
			}
			else if(responseLimitExceeded != null) {
				tParsing = (LimitExceededIOException) this.pddContext.getObject(LimitedInputStream.EXCEPTION_KEY);
				sParsing = tParsing;
				msgErrore = tParsing.getMessage();
			}
			else if( this.responseMessage!=null && this.responseMessage.getParseException() != null ){
				parseException = this.responseMessage.getParseException();
				tParsing = parseException.getParseException();
				sParsing = parseException.getSourceException();
				msgErrore = tParsing.getMessage();
			}
			else{
				parseException = (ParseException) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
				tParsing = parseException.getParseException();
				sParsing = parseException.getSourceException();
				msgErrore = tParsing.getMessage();
			}

			if(msgErrore==null && tParsing!=null){
				msgErrore = tParsing.toString();
			}			
			this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
			this.logCore.error("parsingExceptionRisposta",sParsing);
			
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
			
			this.msgDiag.logPersonalizzato("parsingExceptionRisposta");
			this.responseMessage = this.generatoreErrore.build(this.pddContext, integrationFunctionError,
					ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
					getErrore440_MessaggioRispostaMalformato(tParsing),
					tParsing,null);
		}
			
			
		try{
			// Se non sono stati recuperati i dati delle url, provo a recuperarli
			URLProtocolContext urlProtocolContext = this.context.getUrlProtocolContext();
			if(urlProtocolContext==null){
				urlProtocolContext = this.req.getURLProtocolContext();
			}
			if(urlProtocolContext!=null){
				String urlInvocazione = urlProtocolContext.getUrlInvocazione_formBased();
				if(urlProtocolContext.getFunction()!=null){
					urlInvocazione = "["+urlProtocolContext.getFunction()+"] "+urlInvocazione;
				}
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.URL_INVOCAZIONE, urlInvocazione);
			}
		}catch(Throwable t){}
		try{
			Credenziali credenziali = this.context.getCredenziali();
			if(credenziali==null){
				credenziali = new Credenziali(this.req.getCredential());
			}
			if(credenziali!=null){
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CREDENZIALI_INVOCAZIONE, credenziali.toString());
			}
		}catch(Throwable t){}
			
		
		try{
			this.req.close();
		}catch(Exception e){
			this.logCore.error("Request.close() error: "+e.getMessage(),e);
		}
		
		
		
		
		
		
		// Imposto risposta

		Date dataPrimaSpedizioneRisposta = DateManager.getDate();
		
		if(this.context.getMsgDiagnostico()!=null){
			this.msgDiag = this.context.getMsgDiagnostico();
		}
		if(this.context.getResponseHeaders()==null) {
			this.context.setResponseHeaders(new HashMap<String, List<String>>());
		}
		ServicesUtils.setGovWayHeaderResponse(this.responseMessage, this.openSPCoopProperties,
				this.context.getResponseHeaders(), this.logCore, true, this.context.getPddContext(), this.requestInfo.getProtocolContext());
		if(this.context.getResponseHeaders()!=null){
			Iterator<String> keys = this.context.getResponseHeaders().keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				List<String> values = this.context.getResponseHeaders().get(key);
				if(values!=null && !values.isEmpty()) {
					for (int i = 0; i < values.size(); i++) {
						String value = values.get(i);
						String verbo = "";
						try{
							if(i==0) {
								verbo = "set";
								this.res.setHeader(key,value);
							}
							else {
								verbo = "add";
								this.res.addHeader(key,value);
							}
			    		}catch(Exception e){
			    			this.logCore.error("Response."+verbo+"Header("+key+","+value+") error: "+e.getMessage(),e);
			    		}	
					}
				}
	    	}	
		}
		if(this.context!=null && this.context.getProtocol()!=null){
			
			this.generatoreErrore.updateDominio(this.context.getIdentitaPdD());
		
			IDServizio idServizio = null;
			try{
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(this.context.getProtocol().getTipoServizio(), 
						this.context.getProtocol().getServizio(), 
						this.context.getProtocol().getErogatore(), 
						this.context.getProtocol().getVersioneServizio());
			}catch(Exception e){ 
				// non dovrebbe succedere eccezione}
			}
			if(idServizio!=null){
				idServizio.setAzione(this.context.getProtocol().getAzione());
				this.generatoreErrore.updateInformazioniCooperazione(this.context.getProtocol().getFruitore(), idServizio);
			}
							
			String servizioApplicativo = null;
			if(this.context.getIntegrazione()!=null){
				servizioApplicativo = this.context.getIntegrazione().getServizioApplicativoFruitore();
			}
			this.generatoreErrore.updateInformazioniCooperazione(servizioApplicativo);
			
			this.generatoreErrore.updateProprietaErroreApplicativo(this.context.getProprietaErroreAppl());

		}
		DirectVMConnectorOutMessage vm = null;
		if(this.res instanceof DirectVMConnectorOutMessage){
			vm = (DirectVMConnectorOutMessage) this.res;
		}
		else if(this.req instanceof DumpRawConnectorOutMessage){
			if( ((DumpRawConnectorOutMessage)this.res).getWrappedConnectorOutMessage() instanceof DirectVMConnectorOutMessage ){
				vm = (DirectVMConnectorOutMessage) ((DumpRawConnectorOutMessage)this.res).getWrappedConnectorOutMessage();
			}
		}
		if(vm!=null){
			if(this.context!=null && this.context.getPddContext()!=null){
				DirectVMProtocolInfo pInfo = new DirectVMProtocolInfo();
				Object oIdTransazione = this.context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
				if(oIdTransazione!=null){
					pInfo.setIdTransazione((String)oIdTransazione);
				}
				if(this.context.getProtocol()!=null){
					if(this.context.getProtocol().getIdRichiesta()!=null){
						pInfo.setIdMessaggioRichiesta(this.context.getProtocol().getIdRichiesta());
					}
					if(this.context.getProtocol().getIdRisposta()!=null){
						pInfo.setIdMessaggioRisposta(this.context.getProtocol().getIdRisposta());
					}
				}
				vm.setDirectVMProtocolInfo(pInfo);
			}
		}
		
		InformazioniErroriInfrastrutturali informazioniErrori = ServicesUtils.readInformazioniErroriInfrastrutturali(this.pddContext);
		
		OpenSPCoop2Message responseMessageError = null;
		EsitoTransazione esito = null;
		String descrizioneSoapFault = "";
		int statoServletResponse = 200;
		Throwable erroreConsegnaRisposta = null;
		boolean httpEmptyResponse = false;
		boolean erroreConnessioneClient = false;
		boolean erroreResponsePayloadTooLarge = false;
		boolean sendInvoked = false;
		try{
			if(this.responseMessage!=null && !this.responseMessage.isForcedEmptyResponse() && (this.responseMessage.getForcedResponse()==null)){
					
				// force response code
				if(this.responseMessage.getForcedResponseCode()!=null){
					try{
						statoServletResponse = Integer.parseInt(this.responseMessage.getForcedResponseCode());
					}catch(Exception e){}
				}
								
				// transfer length
				ServicesUtils.setTransferLength(this.openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi(), 
						this.req, this.res, this.responseMessage);
				
				// content type
				// Alcune implementazioni richiedono di aggiornare il Content-Type
				this.responseMessage.updateContentType();
				ServicesUtils.setContentType(this.responseMessage, this.res);
				
				// http status
				boolean consume = true;
				if(ServiceBinding.SOAP.equals(this.responseMessage.getServiceBinding()) ){
					//SOAPBody body = this.responseMessage.castAsSoap().getSOAPBody();
					OpenSPCoop2SoapMessage soapMessage = this.responseMessage.castAsSoap();
					if(soapMessage.hasSOAPFault()){
						consume = false; // può essere usato nel post out response handler
						statoServletResponse = 500;
						descrizioneSoapFault = " ("+SoapUtils.safe_toString(this.responseMessage.getFactory(), soapMessage.getSOAPBody().getFault(), false, this.logCore)+")";
					}
					else if(statoServletResponse==500) {
						// in SOAP 500 deve essere associato con un fault
						if(!soapMessage.isSOAPBodyEmpty()) {
							statoServletResponse = 200;
						}
						else {
							statoServletResponse = this.protocolFactory.createProtocolManager().getHttpReturnCodeEmptyResponseOneWay();
						}
					}
				}
				else if(this.responseMessage.castAsRest().isProblemDetailsForHttpApis_RFC7807() || 
						(MessageRole.FAULT.equals(this.responseMessage.getMessageRole()) &&
							(
							MessageType.XML.equals(this.responseMessage.getMessageType()) 
									|| 
							MessageType.JSON.equals(this.responseMessage.getMessageType())
							)
						)
					) {
					consume = false; // può essere usato nel post out response handler
					String contentAsString = null;
					try {
						contentAsString = this.responseMessage.castAsRest().getContentAsString();
					}catch(Throwable t) {
						this.logCore.error("Parsing errore non riuscito: "+t.getMessage(),t);
					}
					descrizioneSoapFault = " ("+contentAsString+")";
				}
				this.res.setStatus(statoServletResponse);
				
				// esito calcolato prima del sendResponse, per non consumare il messaggio
				esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(), 
						statoServletResponse, this.requestInfo.getIntegrationServiceBinding(),
						this.responseMessage, this.context.getProprietaErroreAppl(), informazioniErrori,
						(this.pddContext!=null ? this.pddContext.getContext() : null));
				
				// contenuto
				Utilities.printFreeMemory("RicezioneContenutiApplicativiDirect - Pre scrittura risposta");
				
				// Il contentLenght, nel caso di TransferLengthModes.CONTENT_LENGTH e' gia' stato calcolato
				// con una writeTo senza consume. Riuso il solito metodo per evitare differenze di serializzazione
				// e cambiare quindi il content length effettivo.
				sendInvoked = true;
				if(TransferLengthModes.CONTENT_LENGTH.equals(this.openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi())){
					this.res.sendResponse(this.responseMessage, false);
				} else {
					this.res.sendResponse(this.responseMessage, consume);
				}
				Utilities.printFreeMemory("RicezioneContenutiApplicativiDirect - Post scrittura risposta");


			}
			
			else if(this.responseMessage!=null && this.responseMessage.getForcedResponse()!=null) {
				byte[]response = this.responseMessage.getForcedResponse().getContent();
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
				
				if(this.responseMessage.getForcedResponse().getHeadersValues()!=null &&
						this.responseMessage.getForcedResponse().getHeadersValues().size()>0) {
					Iterator<String> keys = this.responseMessage.getForcedResponse().getHeadersValues().keySet().iterator();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						List<String> values = this.responseMessage.getForcedResponse().getHeadersValues().get(key);
						if(values!=null && !values.isEmpty()) {
							for (int i = 0; i < values.size(); i++) {
								String value = values.get(i);
								String verbo = "";
								try{
									if(i==0) {
										verbo = "set";
										this.res.setHeader(key,value);
									}
									else {
										verbo = "add";
										this.res.addHeader(key,value);
									}
					    		}catch(Exception e){
					    			this.logCore.error("Response(Forced)."+verbo+"Header("+key+","+value+") error: "+e.getMessage(),e);
					    		}	
							}
						}
			    	}	
				}
				
				if(this.responseMessage.getForcedResponse().getContentType()!=null) {
					this.res.setContentType(this.responseMessage.getForcedResponse().getContentType());
				}
				
				if(this.responseMessage.getForcedResponse().getResponseCode()!=null) {
					try{
						statoServletResponse = Integer.parseInt(this.responseMessage.getForcedResponse().getResponseCode());
					}catch(Exception e){}
				}
				else if(this.responseMessage!=null && this.responseMessage.getForcedResponseCode()!=null) {
					try{
						statoServletResponse = Integer.parseInt(this.responseMessage.getForcedResponseCode());
					}catch(Exception e){}
				}
				this.res.setStatus(statoServletResponse);
				
				// esito calcolato prima del sendResponse, per non consumare il messaggio
				esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(), 
						statoServletResponse, this.requestInfo.getIntegrationServiceBinding(),
						this.responseMessage, this.context.getProprietaErroreAppl(),informazioniErrori,
						(this.pddContext!=null ? this.pddContext.getContext() : null));
				
				if(response!=null) {
					sendInvoked = true;
					this.res.sendResponse(DumpByteArrayOutputStream.newInstance(response));
				}
				
			}
			else{
				if(this.responseMessage!=null && this.responseMessage.getForcedResponseCode()!=null) {
					try{
						statoServletResponse = Integer.parseInt(this.responseMessage.getForcedResponseCode());
					}catch(Exception e){}
				}
				else {
					statoServletResponse = this.protocolFactory.createProtocolManager().getHttpReturnCodeEmptyResponseOneWay();
				}
				this.res.setStatus(statoServletResponse);
				httpEmptyResponse = true;
				// carico-vuoto gestito all'interno
				esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(), 
						statoServletResponse, this.requestInfo.getIntegrationServiceBinding(),
						this.responseMessage, this.context.getProprietaErroreAppl(), informazioniErrori,
						(this.pddContext!=null ? this.pddContext.getContext() : null));
			}
		}catch(Throwable e){			
			this.logCore.error("ErroreGenerale",e);
			erroreConsegnaRisposta = e;
						
			erroreConnessioneClient = ServicesUtils.isConnessioneClientNonDisponibile(e);
			if(!erroreConnessioneClient && ServicesUtils.isConnessioneServerReadTimeout(e)) {
				erroreConnessioneClient = true; // non e' stato possibile consegnare tutta la risposta. Il client ha ricevuto 200 ma non ha ricevuto la risposta per intero
				erroreConsegnaRisposta = new CoreException("Connessione con il backend dell'API non più disponibile: "+e.getMessage(),e);
			}
			if(!erroreConnessioneClient && ServicesUtils.isResponsePayloadTooLarge(e)) {
				erroreResponsePayloadTooLarge = true;  // non e' stato possibile consegnare tutta la risposta. Il client ha ricevuto 200 ma non ha ricevuto la risposta per intero
				erroreConsegnaRisposta = new CoreException("Risposta ricevuta dal backend dell'API non gestibile: "+e.getMessage(),e);
			}
			
			// Genero risposta con errore
			try{
				if(sendInvoked==false) {
					// nel caso sia già stato inoltrata una risposta non e' più possibile modificarlo cosi come tutti gli header etc...	
					InformazioniErroriInfrastrutturali informazioniErrori_error = new InformazioniErroriInfrastrutturali();
					if( (this.responseMessage!=null && this.responseMessage.getParseException() != null) ||
							(this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION))){
						informazioniErrori_error.setContenutoRispostaNonRiconosciuto(true);
						ParseException parseException = null;
						if( this.responseMessage!=null && this.responseMessage.getParseException() != null ){
							parseException = this.responseMessage.getParseException();
						}
						else{
							parseException = (ParseException) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
						}
						String msgErrore = parseException.getParseException().getMessage();
						if(msgErrore==null){
							msgErrore = parseException.getParseException().toString();
						}
						this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
						this.logCore.error("parsingExceptionRisposta",parseException.getSourceException());
						this.msgDiag.logPersonalizzato("parsingExceptionRisposta");
						responseMessageError = this.generatoreErrore.build(this.pddContext, IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT,
								ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
								getErrore440_MessaggioRispostaMalformato(parseException.getParseException()),
								parseException.getParseException(),null);
					} else{
						responseMessageError = this.generatoreErrore.build(this.pddContext, IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
								ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.
								getErrore426_ServletError(false, e),
								e,null);
					}
					
					// transfer length
					ServicesUtils.setTransferLength(this.openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi(), 
							this.req, this.res, responseMessageError);
					
					// content type
					ServicesUtils.setContentType(responseMessageError, this.res);
					
					// http status
					if(responseMessageError!=null && responseMessageError.getForcedResponseCode()!=null) {
						try{
							statoServletResponse = Integer.parseInt(responseMessageError.getForcedResponseCode());
							this.res.setStatus(statoServletResponse);
						}catch(Exception eStatus){}
					}
					if(ServiceBinding.SOAP.equals(responseMessageError.getServiceBinding()) ){
						SOAPBody body = responseMessageError.castAsSoap().getSOAPBody();
						if(body!=null && body.hasFault()){
							statoServletResponse = 500;
							this.res.setStatus(statoServletResponse);
							descrizioneSoapFault = " ("+SoapUtils.safe_toString(responseMessageError.getFactory(), body.getFault(), false, this.logCore)+")";
						}
					}
					
					// esito calcolato prima del sendResponse, per non consumare il messaggio
					esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(),
							statoServletResponse, this.requestInfo.getIntegrationServiceBinding(),
							responseMessageError, this.context.getProprietaErroreAppl(), informazioniErrori_error,
							(this.pddContext!=null ? this.pddContext.getContext() : null));
					
					// contenuto
					// Il contentLenght, nel caso di TransferLengthModes.CONTENT_LENGTH e' gia' stato calcolato
					// con una writeTo senza consume. Riuso il solito metodo per evitare differenze di serializzazione
					// e cambiare quindi il content length effettivo.
					if(TransferLengthModes.CONTENT_LENGTH.equals(this.openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi())){
						this.res.sendResponse(responseMessageError, false);
					} else {
						//this.res.sendResponse(responseMessageError, true);
						this.res.sendResponse(responseMessageError, false); // può essere usato nel post out response handler
					}
				}
																
			}catch(Throwable error){
				
				if(!erroreConnessioneClient){
					erroreConnessioneClient = ServicesUtils.isConnessioneClientNonDisponibile(error);
				}
				
				this.logCore.error("Generazione di un risposta errore non riuscita",error);
				statoServletResponse = 500;
				try{
					responseMessageError = this.generatoreErrore.buildFault(error, this.pddContext);
					if(responseMessageError!=null && responseMessageError.getForcedResponseCode()!=null) {
						try{
							statoServletResponse = Integer.parseInt(responseMessageError.getForcedResponseCode());
						}catch(Exception eStatus){}
					}
					
					try{
						this.res.setStatus(statoServletResponse);
					}catch(Exception eStatus){
						this.logCore.error("Response.setStatus("+statoServletResponse+") error: "+eStatus.getMessage(),eStatus);
					}
					
					this.res.sendResponse(responseMessageError, false); // può essere usato nel post out response handler
				}catch(Throwable  eError){
					if(!erroreConnessioneClient){
						erroreConnessioneClient = ServicesUtils.isConnessioneClientNonDisponibile(eError);
					}
					try {
						this.res.setStatus(statoServletResponse);
					}catch(Throwable t) {}
					try{
						this.res.sendResponse(DumpByteArrayOutputStream.newInstance(error.toString().getBytes()));
					}catch(Exception erroreStreamChiuso){ 
						erroreConnessioneClient = true;
						//se lo stream non e' piu' disponibile non si potra' consegnare alcuna risposta
					}
				}
				try{
					esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
				}catch(Exception eBuildError){
					esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
				}
			}
			
		}finally{
						
			if(sendInvoked==false) {
				// nel caso sia già stato inoltrata una risposta non e' più possibile modificarlo cosi come tutti gli header etc...
				statoServletResponse = this.res.getResponseStatus(); // puo' essere "trasformato" da api engine
			}
			this.msgDiag.addKeyword(CostantiPdD.KEY_CODICE_CONSEGNA, ""+statoServletResponse);
			this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, descrizioneSoapFault);
			
			try{

				// Flush and close response
				// NOTA: per poter ottenere l'errore di BrokenPipe sempre, deve essere disabilitato il socketBufferOutput sul servlet container.
				// Se non lo si disabilta, l'errore viene ritornato solo se il messaggio supera la dimensione del buffer (default: 8192K)
				// Ad esempio in tomcat utilizzare (socketBuffer="-1"): 
				//    <Connector protocol="HTTP/1.1" port="8080" address="${jboss.bind.address}" 
	            //       connectionTimeout="20000" redirectPort="8443" socketBuffer="-1" />
				this.res.flush(true);
				this.res.close(clientEvent, true);
				
				// Emetto diagnostico
				if(erroreConsegnaRisposta!=null){
					
					// Risposta non ritornata al servizio applicativo, il socket verso il servizio applicativo era chiuso o cmq inutilizzabile
					this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, erroreConsegnaRisposta.toString()); // NOTA: lasciare e.toString()
					this.msgDiag.logPersonalizzato("consegnaRispostaApplicativaFallita");
					
				}else{
					if(httpEmptyResponse){
						this.msgDiag.logPersonalizzato("consegnaRispostaApplicativaVuota");
					}else{
						if(statoServletResponse>=300)
							this.msgDiag.logPersonalizzato("consegnaRispostaApplicativaKoEffettuata");
						else
							this.msgDiag.logPersonalizzato("consegnaRispostaApplicativaOkEffettuata");
					}
				}
				
			}catch(Exception e){
				
				erroreConnessioneClient = true;
				
				this.logCore.error("Chiusura stream non riuscita",e);
				
				// Risposta non ritornata al servizio applicativo, il socket verso il servizio applicativo era chiuso o cmq inutilizzabile
				this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, e.toString()); // NOTA: lasciare e.toString()
				
				this.msgDiag.logPersonalizzato("consegnaRispostaApplicativaFallita");
				
				erroreConsegnaRisposta = e;
				
				if(esito!=null){
					if(EsitoTransazioneName.OK.equals(esito.getName())){
						// non è ok, essendo andato in errore il flush
						try{
							esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
						}catch(Exception eBuildError){
							esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
						}
					}
				}
				else{
					// non dovrebbe mai essere null
				}
				
			}
			
			if(this.dumpRaw!=null && this.dumpRaw.isActiveDumpRisposta()){
				this.dumpRaw.serializeResponse(((DumpRawConnectorOutMessage)this.res));
			}
		}
		
		if(erroreConnessioneClient){
			// forzo esito errore connessione client
			try{
				esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_CONNESSIONE_CLIENT_NON_DISPONIBILE);
			}catch(Exception eBuildError){
				esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
			}
		}
		else if(erroreResponsePayloadTooLarge){
			// forzo esito errore dovuto alla policy di rate limiting
			try{
				esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(),EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA);
			}catch(Exception eBuildError){
				esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
			}
		}
		
		
		
		
		
		
		// *** Chiudo connessione verso PdD Destinazione per casi stateless ***
		String location = "...";
		try{
			IConnettore c = null;
			String idTransazione = null;
			if(this.context!=null && this.context.getPddContext()!=null && this.context.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) {
				idTransazione = (String)this.context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			}
			if(idTransazione!=null) {
			//if(this.context.getIdMessage()!=null){
				c = RepositoryConnettori.removeConnettorePD(
						//this.context.getIdMessage()
						idTransazione
						);
			}
			if(c!=null){
				location = c.getLocation();
				c.disconnect();
			}
		}catch(Exception e){
			this.msgDiag.logDisconnectError(e, location);
		}
		
		

		
		
		
		
		
		
		
		/* ------------  PostOutResponseHandler ------------- */
		
		if(this.postOutResponseContext!=null){
			try{
				this.postOutResponseContext.getPddContext().addObject(CostantiPdD.DATA_ACCETTAZIONE_RICHIESTA, this.dataAccettazioneRichiesta);
				if(this.dataIngressoRichiesta!=null){
					this.postOutResponseContext.getPddContext().addObject(CostantiPdD.DATA_INGRESSO_RICHIESTA, this.dataIngressoRichiesta);
				}
				this.postOutResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
				this.postOutResponseContext.setDataPrimaSpedizioneRisposta(dataPrimaSpedizioneRisposta);
				this.postOutResponseContext.setEsito(esito);
				this.postOutResponseContext.setReturnCode(statoServletResponse);
				this.postOutResponseContext.setResponseHeaders(this.context.getResponseHeaders());
				this.postOutResponseContext.setProtocollo(this.context.getProtocol());
				this.postOutResponseContext.setIntegrazione(this.context.getIntegrazione());
				if(this.context.getTipoPorta()!=null)
					this.postOutResponseContext.setTipoPorta(this.context.getTipoPorta());	
				this.postOutResponseContext.setIdModulo(this.idModulo);
				
				if(this.requestMessage!=null){
					long incomingRequestMessageContentLength = this.requestMessage.getIncomingMessageContentLength();
					long outgoingRequestMessageContentLenght = this.requestMessage.getOutgoingMessageContentLength();
					if(incomingRequestMessageContentLength<0){
						int cl = this.req.getContentLength();
						if(cl>0){
							//System.out.println("HTTP");
							incomingRequestMessageContentLength = cl + 0l;
						}
						else{
							//System.out.println("FLUSH");
							// forzo la lettura del messaggio per impostare la dimensione della richiesta
							try{
								this.requestMessage.writeTo(NullOutputStream.NULL_OUTPUT_STREAM, true);
							}catch(Exception eFlush){}
							incomingRequestMessageContentLength = this.requestMessage.getIncomingMessageContentLength();
						}
					}
					this.postOutResponseContext.setInputRequestMessageSize(incomingRequestMessageContentLength);
					this.postOutResponseContext.setOutputRequestMessageSize(outgoingRequestMessageContentLenght);
				}else{
					this.postOutResponseContext.setInputRequestMessageSize(this.req.getContentLength()+0l);
				}
				
				if(erroreConsegnaRisposta!=null){
					if(responseMessageError!=null){
						this.postOutResponseContext.setInputResponseMessageSize(responseMessageError.getIncomingMessageContentLength());
						this.postOutResponseContext.setOutputResponseMessageSize(responseMessageError.getOutgoingMessageContentLength());
						this.postOutResponseContext.setMessaggio(responseMessageError);
					}else{
						if(this.responseMessage!=null && !this.responseMessage.isForcedEmptyResponse() && this.responseMessage.getForcedResponse()==null){
							this.postOutResponseContext.setInputResponseMessageSize(this.responseMessage.getIncomingMessageContentLength());
							this.postOutResponseContext.setOutputResponseMessageSize(this.responseMessage.getOutgoingMessageContentLength());
							this.postOutResponseContext.setMessaggio(this.responseMessage);
						}
					}
					this.postOutResponseContext.setErroreConsegna(erroreConsegnaRisposta.toString()); // NOTA: lasciare e.toString()
				}
				else if(this.responseMessage!=null && !this.responseMessage.isForcedEmptyResponse() && this.responseMessage.getForcedResponse()==null){
					this.postOutResponseContext.setInputResponseMessageSize(this.responseMessage.getIncomingMessageContentLength());
					this.postOutResponseContext.setOutputResponseMessageSize(this.responseMessage.getOutgoingMessageContentLength());
					this.postOutResponseContext.setMessaggio(this.responseMessage);
				}
				else if(this.responseMessage!=null && this.responseMessage.getForcedResponse()!=null &&
						this.responseMessage.getForcedResponse().getContent()!=null) {
					this.postOutResponseContext.setInputResponseMessageSize(this.responseMessage.getIncomingMessageContentLength());
					this.postOutResponseContext.setOutputResponseMessageSize((long) this.responseMessage.getForcedResponse().getContent().length);
				}
								
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e,"postOutResponse, preparazione contesto");
			}
			
			GestoreHandlers.postOutResponse(this.postOutResponseContext, this.msgDiag, this.logCore);
		}
		
		
		
		
		
		
		
		
		
		
		// *** Rilascio risorse NotifierInputStream ***
		
		// request
		try{
			if(this.requestMessage!=null && this.requestMessage.getNotifierInputStream()!=null){
				this.requestMessage.getNotifierInputStream().close();
			}
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e,"Rilascio risorse NotifierInputStream richiesta");
		}
		
		// response
		try{
			if(this.responseMessage!=null && this.responseMessage.getNotifierInputStream()!=null){
				this.responseMessage.getNotifierInputStream().close();
			}
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e,"Rilascio risorse NotifierInputStream risposta");
		}
		
		
		
		
		
		
		
		
		
		// *** GB ***
		this.requestMessage = null;
		this.responseMessage = null;
		responseMessageError = null;
		// *** GB ***
		
		return;

	}
	
}
