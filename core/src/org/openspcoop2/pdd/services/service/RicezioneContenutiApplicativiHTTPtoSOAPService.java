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



package org.openspcoop2.pdd.services.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPElement;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.monitor.sdk.transaction.FaseTracciamento;
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
import org.openspcoop2.pdd.logger.transazioni.ConfigurazioneTracciamento;
import org.openspcoop2.pdd.logger.transazioni.InformazioniTransazione;
import org.openspcoop2.pdd.logger.transazioni.TracciamentoManager;
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
import org.openspcoop2.protocol.basic.builder.EsitoBuilder;
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
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.LimitExceededIOException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TimeoutIOException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.TransportRequestContext;
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

public class RicezioneContenutiApplicativiHTTPtoSOAPService implements IRicezioneService, IAsyncResponseCallback {

	private RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore;
	
	public static void forceXmlResponse(RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore) {
		generatoreErrore.setForceMessageTypeResponse(MessageType.XML); // forzo xml
		if(generatoreErrore.getProprietaErroreAppl()!=null) {
			generatoreErrore.getProprietaErroreAppl().setFaultAsXML(true); // siamo in una richiesta http senza SOAP, un SoapFault non ha senso
		}
	}
	
	public RicezioneContenutiApplicativiHTTPtoSOAPService(RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore){
		this.generatoreErrore = generatoreErrore;
		if(this.generatoreErrore!=null) {
			RicezioneContenutiApplicativiHTTPtoSOAPService.forceXmlResponse(this.generatoreErrore);
		}
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
	private byte[] inputBody = null;
	private OpenSPCoop2Message requestMessage = null;
	private OpenSPCoop2Message responseMessage = null;
	
	private Logger logCore = null;
	private MsgDiagnostico msgDiag=null;
	private DumpRaw dumpRaw = null;
	
	private String idModulo = null;
	private PostOutResponseContext postOutResponseContext = null;
	private String idTransazione = null;
	
	@Override
	public void process(ConnectorInMessage reqParam, ConnectorOutMessage resParam, Date dataAccettazioneRichiestaParam, boolean async) throws ConnectorException {
		
		this.req = reqParam;
		this.res = resParam;
		this.dataAccettazioneRichiesta = dataAccettazioneRichiestaParam;
		
		// IDModulo
		this.idModulo = this.req.getIdModulo();
		IDService idModuloAsService = this.req.getIdModuloAsIDService();
		this.requestInfo = this.req.getRequestInfo();

		// Log
		this.logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.logCore==null)
			this.logCore = LoggerWrapperFactory.getLogger(this.idModulo);

		this.openSPCoopProperties = OpenSPCoop2Properties.getInstance();
		

		/* ------------  PreInHandler (PreInAcceptRequestContext) ------------- */
		
		PreInAcceptRequestContext preInAcceptRequestContext = null;
		SogliaReadTimeout sogliaReadTimeout = null;
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
					soglia.setUseContentLengthHeader(this.openSPCoopProperties.isLimitedInputStreamUseContentLength());
					soglia.setUseContentLengthHeaderAcceptZeroValue(this.openSPCoopProperties.isLimitedInputStreamUseContentLengthAcceptZeroValue());
					soglia.setPolicyGlobale(true);
					soglia.setNomePolicy(CostantiPdD.GOVWAY_CORE);
					soglia.setIdPolicyConGruppo(CostantiPdD.GOVWAY_CORE);
					this.req.setRequestLimitedStream(soglia);
				}
				if(this.openSPCoopProperties.isConnettoriUseTimeoutInputStream()) {
					sogliaReadTimeout = new SogliaReadTimeout();
					sogliaReadTimeout.setSogliaMs(this.openSPCoopProperties.getReadConnectionTimeoutRicezioneContenutiApplicativi());
					sogliaReadTimeout.setConfigurazioneGlobale(true);
					sogliaReadTimeout.setIdConfigurazione(CostantiPdD.GOVWAY_CORE);
					this.req.setRequestReadTimeout(sogliaReadTimeout);
				}
				this.req.setThresholdContext(null, 
						this.openSPCoopProperties.getDumpBinarioInMemoryThreshold(), this.openSPCoopProperties.getDumpBinarioRepository());
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
						new RicezioneContenutiApplicativiInternalErrorGenerator(this.logCore, RicezioneContenutiApplicativiHTTPtoSOAPConnector.ID_MODULO, this.requestInfo);
				this.generatoreErrore.getProprietaErroreAppl().setFaultAsXML(true); // siamo in una richiesta http senza SOAP, un SoapFault non ha senso
			}
		}catch(Exception e){
			String msg = "Inizializzazione Generatore Errore fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
			this.logCore.error(msg,e);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(this.requestInfo, this.generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
					IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, e, null, this.res, this.logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			this.res.close(false);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.logCore, this.req, null, this.dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		//	Proprieta' OpenSPCoop
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
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.logCore, this.req, null, this.dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		// Configurazione Reader
		ConfigurazionePdDManager configPdDManager = null;
		try{
			configPdDManager = ConfigurazionePdDManager.getInstance();
			if(configPdDManager==null || !configPdDManager.isInitializedConfigurazionePdDReader()){
				throw new CoreException("ConfigurazionePdDManager not initialized");
			}
		}catch(Throwable e){
			String msg = "Inizializzazione di GovWay non correttamente effettuata: ConfigurazionePdDManager";
			this.logCore.error(msg);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(this.requestInfo, this.generatoreErrore, 
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
						IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, e, null, this.res, this.logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			this.res.close(false);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.logCore, this.req, null, this.dataAccettazioneRichiesta, cInfo);
			return;
		}
			
		// PddContext from servlet
		Object oPddContextFromServlet = null;
		try{
			oPddContextFromServlet = this.req.getAttribute(CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP.getValue());
		}catch(Exception e){
			this.logCore.error("req.getAttribute("+CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP+") error: "+e.getMessage(),e);
		}
		PdDContext pddContextFromServlet = null;
		if(oPddContextFromServlet!=null){
			pddContextFromServlet = (PdDContext) oPddContextFromServlet;
		}
				
		// check requestInfo
		if(this.requestInfo==null) {
			this.res.close(false);
			String msg = "RequestInfo undefined";
			this.logCore.error(msg);
			return;
		}
		
		// Identifico Servizio per comprendere correttamente il messageType
		ServiceIdentificationReader serviceIdentificationReader = null;
		try{
			serviceIdentificationReader = ServicesUtils.getServiceIdentificationReader(this.logCore, this.requestInfo,
					configPdDManager.getRegistroServiziManager(), configPdDManager);
		}catch(Exception e){
			String msg = "Inizializzazione RegistryReader fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
			this.logCore.error(msg,e);
			ConnectorDispatcherErrorInfo cInfo =  ConnectorDispatcherUtils.doError(this.requestInfo, this.generatoreErrore,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA),
						IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, e, null, this.res, this.logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			this.res.close(false);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.logCore, this.req, pddContextFromServlet, this.dataAccettazioneRichiesta, cInfo);
			return;
		}
	
		// Provo a creare un context (per l'id di transazione nei diagnostici)
		try {
			this.context = new RicezioneContenutiApplicativiContext(idModuloAsService,this.dataAccettazioneRichiesta,this.requestInfo);
			this.protocolFactory = this.req.getProtocolFactory();
			this.idTransazione = (String)this.context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		}catch(Throwable e) {
			this.context = null;
			this.protocolFactory = null;
			// non loggo l'errore tanto poi provo a ricreare il context subito dopo e li verra' registrato l'errore
		}

		try{
			GestoreRichieste.readRequestConfig(this.requestInfo);
		}catch(Exception e){
			String msg = "GestoreRichieste readRequestConfig fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
			this.logCore.error(msg,e);
			ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(this.requestInfo, this.generatoreErrore,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, e, null, this.res, this.logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			this.res.close(false);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.logCore, this.req, pddContextFromServlet, this.dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		if(this.idTransazione!=null) {
			try {
				if(this.openSPCoopProperties.isTransazioniEnabled()) {
					TransactionContext.createTransaction(this.idTransazione,"RicezioneContenutiApplicativiHTTPtoSOAP.1");
				}
				this.requestInfo.setIdTransazione(this.idTransazione);
				
				this.req.setThresholdContext((this.context!=null ? this.context.getPddContext(): null), 
						this.openSPCoopProperties.getDumpBinarioInMemoryThreshold(), this.openSPCoopProperties.getDumpBinarioRepository());
			
			}catch(Throwable e) {
				this.context = null;
				this.protocolFactory = null;
				// non loggo l'errore tanto poi provo a ricreare il context subito dopo e li verra' registrato l'errore
			}
		}
		
		// Logger dei messaggi diagnostici
		String nomePorta = this.requestInfo.getProtocolContext().getInterfaceName();
		this.msgDiag = MsgDiagnostico.newInstance(TipoPdD.DELEGATA,this.idModulo,nomePorta,this.requestInfo);
		this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
		if(this.context!=null && this.protocolFactory!=null) {
			this.msgDiag.setPddContext(this.context.getPddContext(), this.protocolFactory);
		}
		
		try{
			this.msgDiag.logPersonalizzato("ricezioneRichiesta.firstLog");
		}catch(Exception e){
			this.logCore.error("Errore generazione diagnostico di ingresso",e);
		}
		
		try{
			this.req.setDiagnosticProducer(this.context!=null ? this.context.getPddContext(): null, this.msgDiag);
		}catch(Throwable e){
			this.logCore.error("Errore registrazione diagnostico sulla richiesta",e);
		}
		
		// emitDiagnostic preAccept handler
		GestoreHandlers.emitDiagnostic(this.msgDiag, preInAcceptRequestContext, this.context!=null ? this.context.getPddContext() : null, 
				this.logCore, this.logCore);
		
		// Aggiorno RequestInfo
		try{
			this.msgDiag.mediumDebug("Accesso configurazione della richiesta in corso...");
		}catch(Exception e){
			this.logCore.error(CostantiPdD.GOVWAY_CORE_ERRORE_GENERAZIONE_DIAGNOSTICO,e);
		}
		ConnectorDispatcherInfo cInfo = RicezioneContenutiApplicativiServiceUtils.updatePortaDelegataRequestInfo(this.requestInfo, this.logCore, this.req, this.res,
				this.generatoreErrore, serviceIdentificationReader, this.msgDiag, 
				this.context!=null ? this.context.getPddContext(): null);
		if(cInfo!=null){
			this.res.close(false);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.context, this.logCore, this.req, pddContextFromServlet, this.dataAccettazioneRichiesta, cInfo);
			return; // l'errore in response viene impostato direttamente dentro il metodo
		}
		this.req.updateRequestInfo(this.requestInfo);	

		// Timeout e DumpRaw
		try{
			try{
				this.msgDiag.mediumDebug("Lettura configurazione dump binario ...");
			}catch(Exception e){
				this.logCore.error(CostantiPdD.GOVWAY_CORE_ERRORE_GENERAZIONE_DIAGNOSTICO,e);
			}
			boolean dumpBinario = configPdDManager.dumpBinarioPD();
			PortaDelegata pd = null;
			if(this.requestInfo!=null && this.requestInfo.getProtocolContext()!=null && this.requestInfo.getProtocolContext().getInterfaceName()!=null) {
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(this.requestInfo.getProtocolContext().getInterfaceName());
				pd = configPdDManager.getPortaDelegataSafeMethod(idPD, this.requestInfo);
			}

			// Limited
			try{
				this.msgDiag.mediumDebug("Lettura configurazione dimensione massima della richiesta ...");
			}catch(Exception e){
				this.logCore.error(CostantiPdD.GOVWAY_CORE_ERRORE_GENERAZIONE_DIAGNOSTICO,e);
			}
			String azione = (this.requestInfo!=null && this.requestInfo.getIdServizio()!=null) ? this.requestInfo.getIdServizio().getAzione() : null;
			SoglieDimensioneMessaggi limitedInputStream = configPdDManager.getSoglieLimitedInputStream(pd, azione, this.idModulo,
					(this.context!=null && this.context.getPddContext()!=null) ? this.context.getPddContext() : null, 
							this.requestInfo,
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
			try{
				this.msgDiag.mediumDebug("Lettura configurazione timeout per la lettura della richiesta ...");
			}catch(Exception e){
				this.logCore.error(CostantiPdD.GOVWAY_CORE_ERRORE_GENERAZIONE_DIAGNOSTICO,e);
			}
			boolean useTimeoutInputStream = configPdDManager.isConnettoriUseTimeoutInputStream(pd);
			if(useTimeoutInputStream) {
				sogliaReadTimeout = configPdDManager.getRequestReadTimeout(pd,
						this.requestInfo,
						this.protocolFactory, 
						this.context!=null ? this.context.getPddContext() : null,
								null);
				if(sogliaReadTimeout!=null && sogliaReadTimeout.getSogliaMs()>0) {
					this.req.setRequestReadTimeout(sogliaReadTimeout);
				}
				else {
					this.req.disableReadTimeout();
				}
			}
			else {
				this.req.disableReadTimeout();
			}
			
			// DumpRaw
			try{
				this.msgDiag.mediumDebug("Lettura configurazione dump ...");
			}catch(Exception e){
				this.logCore.error(CostantiPdD.GOVWAY_CORE_ERRORE_GENERAZIONE_DIAGNOSTICO,e);
			}
			DumpConfigurazione dumpConfigurazione = configPdDManager.getDumpConfigurazione(pd);
			ConfigurazioneTracciamento configurazioneTracciamento = new ConfigurazioneTracciamento(this.logCore, configPdDManager, pd);
			boolean fileTraceHeaders = configurazioneTracciamento.isTransazioniFileTraceDumpBinarioHeaderEnabled();
			boolean fileTracePayload = configurazioneTracciamento.isTransazioniFileTraceDumpBinarioPayloadEnabled();
			this.dumpRaw = new DumpRaw(this.logCore, this.requestInfo.getIdentitaPdD(), this.idModulo, TipoPdD.DELEGATA, 
					dumpBinario, 
					dumpConfigurazione,
					fileTraceHeaders, fileTracePayload);
			if(this.dumpRaw.isActiveDumpRichiesta()) {
				this.req = new DumpRawConnectorInMessage(this.logCore, this.req, 
						(this.context!=null ? this.context.getPddContext(): null), 
						this.openSPCoopProperties.getDumpBinarioInMemoryThreshold(), this.openSPCoopProperties.getDumpBinarioRepository());
			}
			if(this.dumpRaw.isActiveDumpRisposta()) {
				this.res = new DumpRawConnectorOutMessage(this.logCore, this.res, 
						(this.context!=null ? this.context.getPddContext(): null), 
						this.openSPCoopProperties.getDumpBinarioInMemoryThreshold(), this.openSPCoopProperties.getDumpBinarioRepository(),
						this.dumpRaw);
			}
		}catch(Throwable e){
			String msg = "Inizializzazione di GovWay non correttamente effettuata: DumpRaw";
			this.logCore.error(msg,  e);
			cInfo = ConnectorDispatcherUtils.doError(this.requestInfo, this.generatoreErrore, 
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
						IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, e, null, this.res, this.logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			this.res.close(false);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.logCore, this.req, pddContextFromServlet, this.dataAccettazioneRichiesta, cInfo);
			return;
		}
		
		// Riporto in context il timeout della richiesta
		if(this.context!=null && this.context.getPddContext()!=null &&
			sogliaReadTimeout!=null && sogliaReadTimeout.getSogliaMs()>0) {
				this.context.getPddContext().put(CostantiPdD.REQUEST_READ_TIMEOUT, sogliaReadTimeout.getSogliaMs());
		}
		
		// Questo servizio è invocabile solo con API Soap
		if(!ServiceBinding.SOAP.equals(this.requestInfo.getIntegrationServiceBinding())){
			String msg = "Servizio utilizzabile solamente con API SOAP, riscontrata API REST";
			this.logCore.error(msg);
			ConnectorDispatcherErrorInfo cInfoError =  ConnectorDispatcherUtils.doError(this.requestInfo, this.generatoreErrore,
					ErroriIntegrazione.ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL.getErrore439_FunzionalitaNotSupportedByProtocol(msg, this.protocolFactory),
					IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL, null, null, this.res, this.logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
			this.res.close(false);
			RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.context, this.logCore, this.req, pddContextFromServlet, this.dataAccettazioneRichiesta, cInfoError);
			return;
		}
		
		
		
		
		
		
		/* ------------  Lettura parametri della richiesta ------------- */
				
		String errorImbustamentoSoapNonRiuscito = null;
		MessageType messageTypeReq = null;
		String protocol = null;
		
		boolean completeProcess = false;
		try{
			
			/* --------------- Creo il context che genera l'id univoco ----------------------- */
			
			try{
				this.msgDiag.mediumDebug("Creazione contesto ...");
			}catch(Exception e){
				this.logCore.error(CostantiPdD.GOVWAY_CORE_ERRORE_GENERAZIONE_DIAGNOSTICO,e);
			}
			
			if(this.protocolFactory==null) {
				this.protocolFactory = this.req.getProtocolFactory();
			}
			protocol = this.protocolFactory.getProtocol();
			
			if(this.context==null) {
				this.context = new RicezioneContenutiApplicativiContext(idModuloAsService,this.dataAccettazioneRichiesta,this.requestInfo);
			}
			if(preInAcceptRequestContext!=null && preInAcceptRequestContext.getPreContext()!=null && !preInAcceptRequestContext.getPreContext().isEmpty()) {
				this.context.getPddContext().addAll(preInAcceptRequestContext.getPreContext(), false);
			}
			this.context.setTipoPorta(TipoPdD.DELEGATA);
			this.context.setForceFaultAsXML(true); // siamo in una richiesta http senza SOAP, un SoapFault non ha senso
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
			this.msgDiag.setPddContext(this.context.getPddContext(),this.protocolFactory);		
			this.pddContext = this.context.getPddContext();
			
			try{
				if(this.openSPCoopProperties.isTransazioniEnabled()) {
					// NOTA: se gia' esiste con l'id di transazione, non viene ricreata
					TransactionContext.createTransaction((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE),"RicezioneContenutiApplicativiHTTPtoSOAP.2");
				}
			}catch(Exception e){
				this.logCore.error("Errore durante la creazione della transazione",e);
			}
			
			try{
				this.msgDiag.logPersonalizzato("ricezioneRichiesta.firstAccessRequestStream");
			}catch(Exception e){
				this.logCore.error("Errore generazione diagnostico di ingresso (stream access)",e);
			}
			
			if(this.dumpRaw!=null && this.dumpRaw.isActiveDump()){
				this.dumpRaw.setPddContext(this.msgDiag.getPorta(), this.context.getPddContext());
				this.dumpRaw.serializeContext(this.context, protocol);
			}
			
			DirectVMConnectorInMessage vm = null;
			if(this.req instanceof DirectVMConnectorInMessage directvmconnectorinmessage){
				vm = directvmconnectorinmessage;
			}
			else if(this.req instanceof DumpRawConnectorInMessage dumprawconnectorinmessage &&
					dumprawconnectorinmessage.getWrappedConnectorInMessage() instanceof DirectVMConnectorInMessage directvmconnectorInmessage ){
				vm = directvmconnectorInmessage;
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
			Map<String, Object> transportContext = new HashMap<>();
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
			
			// Controllo Content Length se attiva una policy di rate limiting
			this.req.checkContentLengthLimit();
			
			// Lettura richiesta con il dump
			if(this.dumpRaw!=null && this.dumpRaw.isActiveDumpRichiesta()){
				this.dumpRaw.serializeRequest(((DumpRawConnectorInMessage)this.req), false, notifierInputStreamParams);
				this.dataIngressoRichiesta = this.req.getDataIngressoRichiesta();
				this.context.setDataIngressoRichiesta(this.dataIngressoRichiesta);
			}

			
			
			


			
			
			
			
			/* ------------ Controllo ContentType -------------------- */
			
			this.msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.tipologiaMessaggio");
			messageTypeReq = MessageType.SOAP_11; // rendere parametrico ?
			
			
			
			
			/* ------------  Imbustamento Messaggio di Richiesta ------------- */
			boolean imbustamentoConAttachment = false;
			String tipoAttachment =  HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
			// HeaderTrasporto
			String imb = TransportUtils.getFirstValue(this.req.getHeaderValues(this.openSPCoopProperties.getTunnelSOAPKeyWord_headerTrasporto()));
			if(imb!=null && "true".equals(imb.trim())){
				imbustamentoConAttachment = true;
				String mime = TransportUtils.getFirstValue(this.req.getHeaderValues(this.openSPCoopProperties.getTunnelSOAPKeyWordMimeType_headerTrasporto()));
				if(mime!=null) {
					tipoAttachment = mime.trim();
				}
			}
			if(imb==null){
				// Proprieta FORMBased
				imb = TransportUtils.getFirstValue(this.req.getParameterValues(this.openSPCoopProperties.getTunnelSOAPKeyWord_urlBased()));
				if(imb!=null && "true".equals(imb.trim())){
					imbustamentoConAttachment = true;
					// lettura eventuale tipo di attachment
					String mime = TransportUtils.getFirstValue(this.req.getParameterValues(this.openSPCoopProperties.getTunnelSOAPKeyWordMimeType_urlBased())); 
					if(mime!=null){
						tipoAttachment = mime.trim();
					}
				}
			}
			if(imb==null &&
				// Vedo se fosse indicato nel transport Context
				transportContext.get(this.openSPCoopProperties.getTunnelSOAPKeyWord_urlBased())!=null &&
				"true".equalsIgnoreCase((String)transportContext.get(this.openSPCoopProperties.getTunnelSOAPKeyWord_urlBased()))){
				imbustamentoConAttachment = true;
				// lettura eventuale tipo di attachment
				if(transportContext.get(this.openSPCoopProperties.getTunnelSOAPKeyWordMimeType_urlBased())!=null){
					tipoAttachment = (String) transportContext.get(this.openSPCoopProperties.getTunnelSOAPKeyWordMimeType_urlBased());
				}
			}

			String tipoLetturaRisposta = null;
			try{
				Utilities.printFreeMemory("RicezioneContenutiApplicativiHTTPtoSOAP - Pre costruzione richiesta");
				this.msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.inCorso");
				DumpByteArrayOutputStream bout = this.req.getRequest();
				if(bout!=null && bout.size()>0) {
					this.inputBody = bout.toByteArray();
					bout.clearResources();
					bout=null;
				}
				if( this.inputBody == null || this.inputBody.length<=0 ){
					throw new CoreException("Ricevuto nessun contenuto da imbustare");
				}
				this.req.close();
				this.dataIngressoRichiesta = this.req.getDataIngressoRichiesta();
				this.context.setDataIngressoRichiesta(this.dataIngressoRichiesta);
				
				if(imbustamentoConAttachment){
					tipoLetturaRisposta = "Costruzione messaggio SOAP per Tunnel con mimeType "+tipoAttachment;
					this.requestMessage = TunnelSoapUtils.imbustamentoMessaggioConAttachment(org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(this.logCore,this.requestInfo, MessageRole.REQUEST),
							messageTypeReq,MessageRole.REQUEST,this.inputBody,tipoAttachment,
							MailcapActivationReader.existsDataContentHandler(tipoAttachment),this.req.getContentType(), this.openSPCoopProperties.getHeaderSoapActorIntegrazione());					
					this.requestMessage.setTransportRequestContext(this.requestInfo.getProtocolContext());				
				}else{
					tipoLetturaRisposta = "Imbustamento messaggio in un messaggio SOAP";
					String contentTypeForEnvelope = null; // renderlo parametrico soprattutto per soap1.2
					/** OLD String soapAction = "\"OpenSPCoop2\""; */ 
					String soapAction = "\"GovWay\"";
					OpenSPCoop2MessageParseResult pr = org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(this.logCore,this.requestInfo, MessageRole.REQUEST).
							envelopingMessage(messageTypeReq, contentTypeForEnvelope, soapAction, 
							this.requestInfo.getProtocolContext(), this.inputBody, notifierInputStreamParams, 
							this.openSPCoopProperties.getAttachmentsProcessingMode(), 
							this.openSPCoopProperties.isDeleteInstructionTargetMachineXml(),
							this.openSPCoopProperties.useSoapMessageReader(), this.openSPCoopProperties.getSoapMessageReaderBufferThresholdKb());
					if(pr.getParseException()!=null){
						this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
					}
					this.requestMessage = pr.getMessage_throwParseException();
				}
				
				if(this.requestInfo.getProtocolContext().getHeaders()==null) {
					this.requestInfo.getProtocolContext().setHeaders(new HashMap<>());
				}
				this.requestInfo.getProtocolContext().removeHeader(HttpConstants.CONTENT_TYPE);
				TransportUtils.setHeader(this.requestInfo.getProtocolContext().getHeaders(),HttpConstants.CONTENT_TYPE, this.requestMessage.getContentType());
				this.requestInfo.setIntegrationRequestMessageType(this.requestMessage.getMessageType());
				
				Utilities.printFreeMemory("RicezioneContenutiApplicativiHTTPtoSOAP - Post costruzione richiesta");
				this.requestMessage.setProtocolName(this.protocolFactory.getProtocol());
				this.requestMessage.setTransactionId(PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, this.pddContext));
				this.requestMessage.addContextProperty(org.openspcoop2.core.constants.Costanti.REQUEST_INFO,this.requestInfo); // serve nelle comunicazione non stateless (es. riscontro salvato) per poterlo rispedire
				this.requestMessage.addContextProperty(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE,this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)); // serve nelle comunicazione non stateless (es. riscontro salvato) per poterlo rispedire
				Object nomePortaInvocataObject = this.context.getPddContext().getObject(CostantiPdD.NOME_PORTA_INVOCATA);
				if(nomePortaInvocataObject instanceof String) {
					this.requestMessage.addContextProperty(CostantiPdD.NOME_PORTA_INVOCATA, nomePortaInvocataObject );
				}
								
			}catch(Exception e){
				this.logCore.error(tipoLetturaRisposta +" con errore: "+e.getMessage(),e);
				errorImbustamentoSoapNonRiuscito=tipoLetturaRisposta +" con errore: "+e.getMessage();
				throw e;
			}
			
			
			/* --------------- SecurityToken --------------- */
			try {
				if(this.requestInfo!=null && this.requestInfo.getProtocolContext()!=null && this.requestInfo.getProtocolContext().getCredential()!=null && 
						this.requestInfo.getProtocolContext().getCredential().getCertificate()!=null &&
						this.requestInfo.getProtocolContext().getCredential().getCertificate().getCertificate()!=null) {
					SecurityTokenUtilities.newSecurityToken(this.pddContext);
				}
			}catch(Exception e){
				this.logCore.error("Costruzione SecurityToken non riuscito: "+e.getMessage(),e);
			}
			
			/* ------------  Elaborazione ------------- */
		
			// Contesto di Richiesta
			this.context.setCredenziali(new Credenziali(this.req.getCredential()));
			this.context.setGestioneRisposta(true); // siamo in una servlet, la risposta deve essere aspettata
			this.context.setInvocazionePDPerRiferimento(false); // la PD con questa servlet non effettuera' mai invocazioni per riferimento.
			this.context.setMessageRequest(this.requestMessage);
			this.context.setUrlProtocolContext(this.requestInfo.getProtocolContext());
			this.context.setMsgDiagnostico(this.msgDiag);
					
			// Log elaborazione dati completata
			this.msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.completata");
			
			// se il tracciamento lo prevedo emetto un log
			registraTracciaInRequest();
			
			// Invocazione...
			RicezioneContenutiApplicativi gestoreRichiesta = new RicezioneContenutiApplicativi(this.context, this.generatoreErrore, 
					async ? this : null);
			gestoreRichiesta.process(this.req);
			completeProcess = true;
						
		} catch (Throwable e) {
			
			if(this.context==null){
				// Errore durante la generazione dell'id
				this.context = RicezioneContenutiApplicativiContext.newRicezioneContenutiApplicativiContext(idModuloAsService,this.dataAccettazioneRichiesta,this.requestInfo);
				this.context.setDataIngressoRichiesta(this.dataIngressoRichiesta);
				this.context.setTipoPorta(TipoPdD.DELEGATA);
				this.context.setForceFaultAsXML(true); // siamo in una richiesta http senza SOAP, un SoapFault non ha senso
				this.context.setIdModulo(this.idModulo);
				this.context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME, this.protocolFactory.getProtocol());
				this.pddContext = this.context.getPddContext();
				this.msgDiag.setPddContext(this.pddContext,this.protocolFactory);
				if(this.postOutResponseContext!=null){
					this.postOutResponseContext.setPddContext(this.pddContext);
				}
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
			if(errorImbustamentoSoapNonRiuscito!=null){
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				this.logCore.error("ImbustamentoSOAP",e);
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
				this.msgDiag.logErroreGenerico(errorImbustamentoSoapNonRiuscito+"  "+msgErrore, "ImbustamentoSOAP");
				
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
				if( parseException!=null && parseException.getSourceException()!=null &&
						TimeoutIOException.isTimeoutIOException(parseException.getSourceException())) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
				}
				else if( parseException!=null && parseException.getSourceException()!=null &&
						LimitExceededIOException.isLimitExceededIOException(parseException.getSourceException())) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
				}
				
				this.responseMessage = this.generatoreErrore.build(this.pddContext,integrationFunctionError,
						ErroriIntegrazione.ERRORE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA.
						getErrore422_MessaggioSOAPNonGenerabileTramiteImbustamentoSOAP(errorImbustamentoSoapNonRiuscito),tMessage,null);
			}
			else if(tParsing!=null){
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				String msgErrore = tParsing.getMessage();
				if(msgErrore==null){
					msgErrore = tParsing.toString();
				}
				this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
				this.logCore.error(MsgDiagnosticiProperties.MSG_DIAG_PARSING_EXCEPTION_RICHIESTA,e);
				this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_PARSING_EXCEPTION_RICHIESTA);
				
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
				if( parseException!=null && parseException.getSourceException()!=null &&
						TimeoutIOException.isTimeoutIOException(parseException.getSourceException())) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
				}
				else if( parseException!=null && parseException.getSourceException()!=null &&
						LimitExceededIOException.isLimitExceededIOException(parseException.getSourceException())) {
					integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
				}
				
				this.responseMessage = this.generatoreErrore.build(this.pddContext,integrationFunctionError,
						ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.
						getErrore432_MessaggioRichiestaMalformato(tParsing),tParsing,null);
			}
			else if (e instanceof HandlerException he) {
				this.logCore.error("ErroreGenerale (HandlerException)",e);
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
			else{
				this.logCore.error("ErroreGenerale",e);
				this.msgDiag.logErroreGenerico(e, "Generale(richiesta)");
				this.responseMessage = this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.BAD_REQUEST,
						ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.
						getErrore426_ServletError(true, e),e,null);
			}
		}
		finally{

			try {
				if(!completeProcess || !async) {
					this.completeEngine(AsyncResponseCallbackClientEvent.NONE,completeProcess);
				}

			}finally {
				try {
					GestoreRichieste.saveRequestConfig(this.requestInfo);
				}catch(Throwable e) {
					this.logCore.error("Errore durante il salvataggio dei dati della richiesta: "+e.getMessage(),e);
				}	
			}
		}
	}

	@Override
	public void asyncComplete(AsyncResponseCallbackClientEvent clientEvent, Object ... args) throws ConnectorException { // Questo metodo verrà chiamato dalla catena di metodi degli oggetti (IAsyncResponseCallback) fatta scaturire dal response callback dell'Async Client NIO
		this.completeEngine(clientEvent, true);
	}
	private void completeEngine(AsyncResponseCallbackClientEvent clientEvent, boolean completeProcess) throws ConnectorException {

		if(completeProcess) {
			this.responseMessage = this.context.getMessageResponse();
		}


		// Finally Request	
		if((this.requestMessage!=null && this.requestMessage.getParseException() != null) || 
				(this.pddContext!=null && this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION))){
			this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
			ParseException parseException = null;
			if( this.requestMessage!=null && this.requestMessage.getParseException() != null ){
				parseException = this.requestMessage.getParseException();
			}
			else{
				parseException = (ParseException) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
			}
			String msgErrore = null;
			if (parseException != null && parseException.getParseException() != null)
				msgErrore = parseException.getParseException().getMessage();
			if(msgErrore==null){
				msgErrore = parseException.getParseException().toString();
			}
			this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
			
			if (parseException != null)
				this.logCore.error(MsgDiagnosticiProperties.MSG_DIAG_PARSING_EXCEPTION_RICHIESTA,parseException.getSourceException());
			this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_PARSING_EXCEPTION_RICHIESTA);
			
			IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
			if( parseException!=null && parseException.getSourceException()!=null &&
					TimeoutIOException.isTimeoutIOException(parseException.getSourceException())) {
				integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
			}
			else if( parseException!=null && parseException.getSourceException()!=null &&
					LimitExceededIOException.isLimitExceededIOException(parseException.getSourceException())) {
				integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
			} else if (parseException != null) {
				this.responseMessage = this.generatoreErrore.build(this.pddContext, integrationFunctionError,
						ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.
						getErrore432_MessaggioRichiestaMalformato(parseException.getParseException()),
						parseException.getParseException(),null);
			}
		}
		else if( (this.responseMessage!=null && this.responseMessage.getParseException() != null) ||
				(this.pddContext!=null && this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION))){
			if(this.pddContext!=null) {
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
			}
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
			this.logCore.error(MsgDiagnosticiProperties.MSG_DIAG_PARSING_EXCEPTION_RISPOSTA,parseException.getSourceException());
			this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_PARSING_EXCEPTION_RISPOSTA);
			this.responseMessage = this.generatoreErrore.build(this.pddContext, IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT,
					ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
					getErrore440_MessaggioRispostaMalformato(parseException.getParseException()),
					parseException.getParseException(),null);
		}
		
		try{
			// Se non sono stati recuperati i dati delle url, provo a recuperarli
			URLProtocolContext urlProtocolContext = this.context!=null ? this.context.getUrlProtocolContext() : null;
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
		}catch(Throwable t){
			// ignore
		}
		try{
			Credenziali credenziali = this.context!=null ? this.context.getCredenziali() : null;
			if(credenziali==null){
				credenziali = new Credenziali(this.req.getCredential());
			}
			if(credenziali!=null && this.pddContext!=null){
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CREDENZIALI_INVOCAZIONE, credenziali.toString());
			}
		}catch(Throwable t){
			// ignore
		}
		
		// *** GB ***
		try{
			this.req.close();
		}catch(Exception e){
			this.logCore.error("Request.close() error: "+e.getMessage(),e);
		}
		// *** GB ***
		
			
			
		// Imposto risposta

		Date dataPrimaSpedizioneRisposta = DateManager.getDate();
		Date dataRispostaSpedita = null; 
		Transazione transazioneDaAggiornare = null;
		
		if(this.context != null) {
			if(this.context.getMsgDiagnostico()!=null)
				this.msgDiag = this.context.getMsgDiagnostico();
			if(this.context.getResponseHeaders()==null)
				this.context.setResponseHeaders(new HashMap<>());
			
			ServicesUtils.setGovWayHeaderResponse(this.requestMessage!=null ? this.requestMessage.getServiceBinding() : this.requestInfo.getProtocolServiceBinding(),
					this.responseMessage, this.openSPCoopProperties,
					this.context.getResponseHeaders(), this.logCore, true, this.context.getPddContext(), this.requestInfo);
		}
		
		if(this.context != null && this.context.getResponseHeaders()!=null){
			Iterator<String> keys = this.context.getResponseHeaders().keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
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
			    			this.logCore.error("Response."+verbo+"Header("+key+","+value+") set failed: "+e.getMessage(),e);
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
				// non dovrebbe succedere eccezione
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
		if(this.res instanceof DirectVMConnectorOutMessage directvmconnectoroutmessage){
			vm = directvmconnectoroutmessage;
		}
		else if(this.req instanceof DumpRawConnectorOutMessage dumprawconnectoroutmessage &&
				dumprawconnectoroutmessage.getWrappedConnectorOutMessage() instanceof DirectVMConnectorOutMessage directvmconnectorOutmessage ){
			vm = directvmconnectorOutmessage;
		}
		if(vm!=null &&
			this.context!=null && this.context.getPddContext()!=null){
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
		
		InformazioniErroriInfrastrutturali informazioniErrori = ServicesUtils.readInformazioniErroriInfrastrutturali(this.pddContext);
		
		EsitoTransazione esito = null;
		String descrizioneSoapFault = "";
		int statoServletResponse = 200;
		Throwable erroreConsegnaRisposta = null; 
		boolean httpEmptyResponse = false;
		long lengthOutResponse = -1;
		boolean erroreConnessioneClient = false;
		boolean sendInvoked = false;
		boolean registraTracciaOutResponse = false;
		try{
			if(this.responseMessage!=null && !this.responseMessage.isForcedEmptyResponse() && (this.responseMessage.getForcedResponse()==null)){
					
				// force response code
				boolean forced = false;
				if(this.responseMessage.getForcedResponseCode()!=null){
					try{
						statoServletResponse = Integer.parseInt(this.responseMessage.getForcedResponseCode());
						forced = true;
					}catch(Exception e){
						// ignore
					}
				}
				
				if(ServiceBinding.SOAP.equals(this.responseMessage.getServiceBinding())) {
										
					SOAPBody body = this.responseMessage.castAsSoap().getSOAPBody();
					String contentTypeRisposta = null;
					byte[] risposta = null;
					if(body!=null && body.hasFault()){
						statoServletResponse = 500; // cmq e' un errore come l'errore applicativo
						String msgError = SoapUtils.safe_toString(this.responseMessage.getFactory(), body.getFault(), false, this.logCore);
						/**risposta=msgError.getBytes();*/
						org.openspcoop2.message.xml.MessageXMLUtils xmlUtils = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(this.responseMessage.getFactory());
						risposta=xmlUtils.toByteArray(body.getFault(), true);
						/**System.out.println("ELABORATO:"+new String(risposta));*/
						contentTypeRisposta = this.responseMessage.getContentType();
						descrizioneSoapFault = " ("+msgError+")";	
					}else{
						risposta=TunnelSoapUtils.sbustamentoMessaggio(this.responseMessage);
						if(risposta==null || risposta.length<=0){
							// Si puo' entrare in questo caso, se nel messaggio Soap vi era presente solo l'header
							risposta = null;
							if(!forced)
								statoServletResponse = 202;
						}else{
							
							SOAPElement child = SoapUtils.getNotEmptyFirstChildSOAPElement(body);
							if(child!=null &&
								this.protocolFactory.createErroreApplicativoBuilder().isErroreApplicativo(child)){
								statoServletResponse = 500;
							}
							
							// Non serve la updateContentType. Il messaggio e' gia' stato serializzato ed il cType e' corretto.  
							if(TunnelSoapUtils.isTunnelOpenSPCoopSoap(this.responseMessage.getFactory(), body)){
								contentTypeRisposta = TunnelSoapUtils.getContentTypeTunnelOpenSPCoopSoap(body);
							}else{
								contentTypeRisposta = this.responseMessage.getContentType();
							}
						}
					}
					
					// transfer length
					if(risposta!=null){
						lengthOutResponse = risposta.length;
						ServicesUtils.setTransferLength(this.openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi(), 
								this.req, this.res, Long.valueOf(risposta.length));
					}
					
					// httpstatus
					this.res.setStatus(statoServletResponse);
					
					// content type
					if(contentTypeRisposta!=null){
						this.res.setContentType(contentTypeRisposta);
					}
					
					// esito calcolato prima del sendResponse, per non consumare il messaggio
					esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(), 
							statoServletResponse, this.requestInfo.getIntegrationServiceBinding(),
							this.responseMessage, this.context.getProprietaErroreAppl(),informazioniErrori,
							this.pddContext);
					
					// httpHeaders
					this.res.sendResponseHeaders(this.responseMessage);					
					
					// se il tracciamento lo prevedo emetto un log
					registraTracciaOutResponse = true;
					transazioneDaAggiornare = registraTracciaOutResponse(dataPrimaSpedizioneRisposta, dataRispostaSpedita,
							esito, statoServletResponse,
							erroreConsegnaRisposta, lengthOutResponse);
					
					// contenuto
					if(risposta!=null){
						sendInvoked = true;
						this.res.sendResponse(DumpByteArrayOutputStream.newInstance(risposta));
					}
				}
				else {
					
					// transfer length
					ServicesUtils.setTransferLength(this.openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi(), 
							this.req, this.res, this.responseMessage);
					
					// content type
					// Alcune implementazioni richiedono di aggiornare il Content-Type
					this.responseMessage.updateContentType();
					ServicesUtils.setContentType(this.responseMessage, this.res);
					
					// http status
					boolean consume = true;
					if(this.responseMessage.castAsRest().isProblemDetailsForHttpApis_RFC7807() || 
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
						if(contentAsString!=null && StringUtils.isNotEmpty(contentAsString)) {
							descrizioneSoapFault = " ("+contentAsString+")";
						}
					}
					this.res.setStatus(statoServletResponse);
					
					// esito calcolato prima del sendResponse, per non consumare il messaggio
					esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(), 
							statoServletResponse, this.requestInfo.getIntegrationServiceBinding(),
							this.responseMessage, this.context.getProprietaErroreAppl(), informazioniErrori,
							this.pddContext);
					
					// se il tracciamento lo prevedo emetto un log
					registraTracciaOutResponse = true;
					transazioneDaAggiornare = registraTracciaOutResponse(dataPrimaSpedizioneRisposta, dataRispostaSpedita,
							esito, statoServletResponse,
							erroreConsegnaRisposta, lengthOutResponse);
					
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
				
			}
			else if(this.responseMessage!=null && this.responseMessage.getForcedResponse()!=null) {
				byte[]response = this.responseMessage.getForcedResponse().getContent();
				/**if(response==null) {
					throw new Exception("Trovata configurazione 'forcedResponse' senza una vera risposta");
				}*/
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
				
				if(this.responseMessage.getForcedResponse().getHeadersValues()!=null &&
						this.responseMessage.getForcedResponse().getHeadersValues().size()>0) {
					Iterator<String> keys = this.responseMessage.getForcedResponse().getHeadersValues().keySet().iterator();
					while (keys.hasNext()) {
						String key = keys.next();
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
					}catch(Exception e){
						// ignore
					}
				}
				else if(this.responseMessage!=null && this.responseMessage.getForcedResponseCode()!=null) {
					try{
						statoServletResponse = Integer.parseInt(this.responseMessage.getForcedResponseCode());
					}catch(Exception e){
						// ignore
					}
				}
				this.res.setStatus(statoServletResponse);
				
				if (this.context != null) {
					// esito calcolato prima del sendResponse, per non consumare il messaggio
					esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(), 
							statoServletResponse, this.requestInfo.getIntegrationServiceBinding(),
							this.responseMessage, this.context.getProprietaErroreAppl(),informazioniErrori,
							this.pddContext);
				}
				
				// se il tracciamento lo prevedo emetto un log
				registraTracciaOutResponse = true;
				transazioneDaAggiornare = registraTracciaOutResponse(dataPrimaSpedizioneRisposta, dataRispostaSpedita,
						esito, statoServletResponse,
						erroreConsegnaRisposta, lengthOutResponse);
				
				if(response!=null) {
					sendInvoked = true;
					this.res.sendResponse(DumpByteArrayOutputStream.newInstance(response));
				}
				
			}			
			else{
				// httpstatus
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
				esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(), 
						statoServletResponse, this.requestInfo.getIntegrationServiceBinding(),
						this.responseMessage, this.context.getProprietaErroreAppl(),informazioniErrori,
						this.pddContext);
				// carico-vuoto
				
				// se il tracciamento lo prevedo emetto un log
				registraTracciaOutResponse = true;
				transazioneDaAggiornare = registraTracciaOutResponse(dataPrimaSpedizioneRisposta, dataRispostaSpedita,
						esito, statoServletResponse,
						erroreConsegnaRisposta, lengthOutResponse);

			}
			
		}catch(Throwable e){
			this.logCore.error("ErroreGenerale",e);
			erroreConsegnaRisposta = e;
			
			erroreConnessioneClient = ServicesUtils.isConnessioneClientNonDisponibile(e);
			
			try{
				esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
			}catch(Exception eBuildError){
				esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
			}
			
			// Genero risposta con errore
			try{
				if(!sendInvoked) {
					// nel caso sia già stato inoltrata una risposta non e' più possibile modificarlo cosi come tutti gli header etc...	
					byte [] rispostaErrore = null;
					List<Integer> returnCode = new ArrayList<>();
					InformazioniErroriInfrastrutturali informazioniErroriInfrastrutturaliError = null;
					if( (this.responseMessage!=null && this.responseMessage.getParseException() != null) ||
							(this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION))){
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
						this.logCore.error(MsgDiagnosticiProperties.MSG_DIAG_PARSING_EXCEPTION_RISPOSTA,parseException.getSourceException());
						this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_PARSING_EXCEPTION_RISPOSTA);
											
						rispostaErrore = this.generatoreErrore.buildAsByteArray(this.pddContext, IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT,
								ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
								getErrore440_MessaggioRispostaMalformato(parseException.getParseException()),
								returnCode);
						
						informazioniErroriInfrastrutturaliError = new InformazioniErroriInfrastrutturali();
						informazioniErroriInfrastrutturaliError.setContenutoRispostaNonRiconosciuto(true);
					} 
					else{
						IntegrationFunctionError ife = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR;
						if(e instanceof HandlerException he &&
							he.getIntegrationFunctionError()!=null) {
							ife = he.getIntegrationFunctionError();
						}
						rispostaErrore = this.generatoreErrore.buildAsByteArray(this.pddContext, ife,
								ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.
								getErrore426_ServletError(false, e),
								returnCode);
					}
					
					// transfer length
					lengthOutResponse = rispostaErrore.length;
					ServicesUtils.setTransferLength(this.openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi(), 
							this.req, this.res, Long.valueOf(rispostaErrore.length));
					
					// httpstatus
					//statoServletResponse = 500; // Nella servlet con sbustamento non devo ritornare 500
					statoServletResponse = 200;
					if(returnCode!=null && !returnCode.isEmpty()){
						statoServletResponse = returnCode.get(0);
					}
					this.res.setStatus(statoServletResponse);
					
					// esito calcolato prima del sendResponse, per non consumare il messaggio
					if(informazioniErroriInfrastrutturaliError!=null) {
						esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(),
								statoServletResponse, this.requestInfo.getIntegrationServiceBinding(),
								null, this.context.getProprietaErroreAppl(), informazioniErroriInfrastrutturaliError,
								this.pddContext);
					}
					
					// content type
					this.res.setContentType("text/xml");
					
					// se il tracciamento lo prevedo emetto un log
					// se ho già provato prima a tracciare non lo faccio un'altra volta
					if(!registraTracciaOutResponse) {
						transazioneDaAggiornare = registraTracciaOutResponse(dataPrimaSpedizioneRisposta, dataRispostaSpedita,
							esito, statoServletResponse,
							erroreConsegnaRisposta, lengthOutResponse);
					}
					
					// contenuto
					this.res.sendResponse(DumpByteArrayOutputStream.newInstance(rispostaErrore));
				}
						
			}catch(Throwable error){
				
				if(!erroreConnessioneClient){
					erroreConnessioneClient = ServicesUtils.isConnessioneClientNonDisponibile(error);
				}
				
				this.logCore.error("Generazione di un risposta errore non riuscita",error);
				statoServletResponse = 500; // ERRORE EFFETTIVO!
				try{
					this.res.setStatus(500);
				}catch(Exception eStatus){
					this.logCore.error("Response.setStatus(500) error: "+eStatus.getMessage(),eStatus);
				}
				byte[] ris = error.toString().getBytes();
				try{
					this.res.sendResponse(DumpByteArrayOutputStream.newInstance(ris));
				}catch(Exception erroreStreamChiuso){ 
					erroreConnessioneClient = true;
					//se lo stream non e' piu' disponibile non si potra' consegnare alcuna risposta
				}
				lengthOutResponse = ris.length;
			}
			
		}
		finally{
			if(!sendInvoked) {
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
				this.res.close(clientEvent,true);
				
				dataRispostaSpedita = DateManager.getDate();
				
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
				
			} finally {
				if(dataRispostaSpedita==null) {
					dataRispostaSpedita = DateManager.getDate();
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
		else if(EsitoTransazioneName.OK.equals(esito.getName()) && this.context!=null && this.context.getPddContext()!=null && this.context.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.EMESSI_DIAGNOSTICI_ERRORE)) {
			// caso di errore generato durante il tracciamento dopo aver calcolato l'esito, in cui non viene sollevata una eccezione
			esito = ServicesUtils.updateEsitoConAnomalie(esito, this.logCore, this.protocolFactory);
		}
		
		
		
		
		
		
		
		// *** Chiudo connessione verso PdD Destinazione per casi stateless ***
		String location = "...";
		try{
			IConnettore c = null;
			if(this.context!=null && this.context.getPddContext()!=null && this.context.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) {
				this.idTransazione = (String)this.context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			}
			if(this.idTransazione!=null) {
			/**if(this.context.getIdMessage()!=null){*/
				c = RepositoryConnettori.removeConnettorePD(
						/**this.context.getIdMessage()*/
						this.idTransazione
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
			try {
				updateContext(dataPrimaSpedizioneRisposta, dataRispostaSpedita,
						esito, statoServletResponse,
						erroreConsegnaRisposta, lengthOutResponse);
				this.postOutResponseContext.setTransazioneDaAggiornare(transazioneDaAggiornare);
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
		// *** GB ***
		

	}


	private void updateContext(Date dataPrimaSpedizioneRisposta, Date dataRispostaSpedita,
			EsitoTransazione esito, int statoServletResponse,
			Throwable erroreConsegnaRisposta, long lengthOutResponse) throws ConnectorException {
		if(this.postOutResponseContext!=null){
			this.postOutResponseContext.getPddContext().addObject(CostantiPdD.DATA_ACCETTAZIONE_RICHIESTA, this.dataAccettazioneRichiesta);
			if(this.dataIngressoRichiesta!=null){
				this.postOutResponseContext.getPddContext().addObject(CostantiPdD.DATA_INGRESSO_RICHIESTA, this.dataIngressoRichiesta);
			}
			this.postOutResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
			this.postOutResponseContext.setDataPrimaSpedizioneRisposta(dataPrimaSpedizioneRisposta);
			this.postOutResponseContext.setDataRispostaSpedita(dataRispostaSpedita);
			if(erroreConsegnaRisposta==null){
				this.postOutResponseContext.setEsito(esito);
			}else{
				try{
					esito = this.protocolFactory.createEsitoBuilder().getEsito(this.req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
				}catch(Exception eBuildError){
					esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
				}
				this.postOutResponseContext.setEsito(esito);
			}
			this.postOutResponseContext.setReturnCode(statoServletResponse);
			this.postOutResponseContext.setResponseHeaders(this.context.getResponseHeaders());
			this.postOutResponseContext.setProtocollo(this.context.getProtocol());
			this.postOutResponseContext.setIntegrazione(this.context.getIntegrazione());
			if(this.context.getTipoPorta()!=null)
				this.postOutResponseContext.setTipoPorta(this.context.getTipoPorta());	
			this.postOutResponseContext.setIdModulo(this.idModulo);
			
			if(this.inputBody!=null){
				this.postOutResponseContext.setInputRequestMessageSize(Long.valueOf(this.inputBody.length));
			}
			if(this.requestMessage!=null){
				/**this.postOutResponseContext.setInputRequestMessageSize(this.requestMessage.getIncomingMessageContentLength());*/
				this.postOutResponseContext.setOutputRequestMessageSize(this.requestMessage.getOutgoingMessageContentLength());
			}else{
				this.postOutResponseContext.setInputRequestMessageSize(this.req.getContentLength()+0l);
			}
			
			if(erroreConsegnaRisposta==null && this.responseMessage!=null  && !this.responseMessage.isForcedEmptyResponse() && this.responseMessage.getForcedResponse()==null){
				this.postOutResponseContext.setInputResponseMessageSize(this.responseMessage.getIncomingMessageContentLength());
				this.postOutResponseContext.setOutputResponseMessageSize(lengthOutResponse); // sbustata!
				this.postOutResponseContext.setMessaggio(this.responseMessage);
			}
			else if(this.responseMessage!=null && this.responseMessage.getForcedResponse()!=null &&
					this.responseMessage.getForcedResponse().getContent()!=null) {
				this.postOutResponseContext.setInputResponseMessageSize(this.responseMessage.getIncomingMessageContentLength());
				this.postOutResponseContext.setOutputResponseMessageSize((long) this.responseMessage.getForcedResponse().getContent().length);
			}	
		}		
	}

	private Transazione registraTracciaOutResponse(Date dataPrimaSpedizioneRisposta, Date dataRispostaSpedita,
			EsitoTransazione esito, int statoServletResponse,
			Throwable erroreConsegnaRisposta, long lengthOutResponse) throws HandlerException {

		try {
		
			if(this.postOutResponseContext!=null) {
				updateContext(dataPrimaSpedizioneRisposta, dataRispostaSpedita,
						esito, statoServletResponse,
						erroreConsegnaRisposta, lengthOutResponse);
				
				TracciamentoManager tracciamentoManager = new TracciamentoManager(FaseTracciamento.OUT_RESPONSE);
				if(!tracciamentoManager.isTransazioniEnabled()) {
					return null;
				}
				
				InformazioniTransazione info = new InformazioniTransazione(this.postOutResponseContext);
				
				tracciamentoManager.invoke(info, this.postOutResponseContext.getEsito(), this.context.getResponseHeaders(), this.msgDiag);
				
				return info.getTransazioneDaAggiornare();
			}

		}catch(Exception e) {
			ServicesUtils.processTrackingException(e, this.postOutResponseContext.getLogCore(), FaseTracciamento.OUT_RESPONSE, this.context.getPddContext());
		}
		
		return null;
	}
	
	private void registraTracciaInRequest() throws HandlerException{
		
		try {
		
			TracciamentoManager tracciamentoManager = new TracciamentoManager(FaseTracciamento.IN_REQUEST);
			if(!tracciamentoManager.isTransazioniEnabled()) {
				return;
			}
				
			InformazioniTransazione info = new InformazioniTransazione();
			info.setContext(this.context.getPddContext());
			info.setTipoPorta(this.context.getTipoPorta());
			info.setProtocolFactory(this.protocolFactory);
			info.setProtocollo(this.context.getProtocol());
			info.setIntegrazione(this.context.getIntegrazione());
			info.setIdModulo(this.context.getIdModulo());
			
			TransportRequestContext transportRequestContext = null;
			if(this.context.getMessageRequest()!=null) {
				transportRequestContext = this.context.getMessageRequest().getTransportRequestContext();
			}
			String esitoContext = EsitoBuilder.getTipoContext(transportRequestContext, EsitiProperties.getInstance(this.logCore, this.protocolFactory), this.logCore);
			
			tracciamentoManager.invoke(info, esitoContext, this.msgDiag);
			
		}catch(Exception e) {
			ServicesUtils.processTrackingException(e, this.logCore, FaseTracciamento.IN_REQUEST, this.context.getPddContext());
		}
		
	}
}
