/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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


package org.openspcoop2.pdd.services.core;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiFruizione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziAzioneNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziCorrelatoNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziPortTypeNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.message.soap.mtom.MtomXomReference;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazioneCanaliNodo;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.config.dynamic.PddPluginLoader;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.CORSFilter;
import org.openspcoop2.pdd.core.CORSWrappedHttpServletResponse;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreCorrelazioneApplicativa;
import org.openspcoop2.pdd.core.GestoreCorrelazioneApplicativaConfig;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.GestoreMessaggiException;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.LocalForwardEngine;
import org.openspcoop2.pdd.core.LocalForwardParameter;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.StatoServiziPdD;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativi;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiException;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiRest;
import org.openspcoop2.pdd.core.autorizzazione.GestoreAutorizzazione;
import org.openspcoop2.pdd.core.autorizzazione.container.AutorizzazioneHttpServletRequest;
import org.openspcoop2.pdd.core.autorizzazione.container.IAutorizzazioneSecurityContainer;
import org.openspcoop2.pdd.core.autorizzazione.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.pdd.core.autorizzazione.pd.EsitoAutorizzazionePortaDelegata;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiConfigurationException;
import org.openspcoop2.pdd.core.credenziali.IGestoreCredenziali;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InRequestContext;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.pdd.core.handlers.OutResponseContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePD;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePDSoap;
import org.openspcoop2.pdd.core.integrazione.InRequestPDMessage;
import org.openspcoop2.pdd.core.integrazione.OutResponsePDMessage;
import org.openspcoop2.pdd.core.node.INodeReceiver;
import org.openspcoop2.pdd.core.node.INodeSender;
import org.openspcoop2.pdd.core.node.NodeTimeoutException;
import org.openspcoop2.pdd.core.response_caching.HashGenerator;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.attribute_authority.EsitoRecuperoAttributi;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.core.token.attribute_authority.PolicyAttributeAuthority;
import org.openspcoop2.pdd.core.token.attribute_authority.pd.GestioneAttributeAuthority;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.EsitoLib;
import org.openspcoop2.pdd.mdb.ImbustamentoMessage;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.mdb.SbustamentoRisposte;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.ServicesUtils;
import org.openspcoop2.pdd.services.connector.AsyncResponseCallbackClientEvent;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.IAsyncResponseCallback;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.pdd.services.skeleton.IntegrationManager;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.pdd.timers.TimerLock;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerThresholdThread;
import org.openspcoop2.pdd.timers.TipoLock;
import org.openspcoop2.protocol.basic.registry.IdentificazionePortaDelegata;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.builder.Imbustamento;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.mapping.IdentificazioneDinamicaException;
import org.openspcoop2.protocol.engine.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.StatoFunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.dump.DumpException;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.http.CORSRequestType;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Implementazione del servizio RicezioneContenutiApplicativi di OpenSPCoop
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RicezioneContenutiApplicativi implements IAsyncResponseCallback {

	/**
	 * Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop
	 * rappresentato da questa classe
	 */
	public static final String ID_MODULO = "RicezioneContenutiApplicativi";
	
	private static void logDebug(Logger logCore, String msg) {
		logCore.debug(msg);
	}
	private static void logDebug(Logger logCore, String msg, Throwable e) {
		logCore.debug(msg, e);
	}
	private static void logInfo(Logger logCore, String msg) {
		logCore.info(msg);
	}
	private static void logError(Logger logCore, String msg) {
		logCore.error(msg);
	}
	private static void logError(Logger logCore, String msg, Throwable e) {
		logCore.error(msg,e);
	}
	
	private static final String ERRORE_NON_GESTITO = "Errore non gestito";

	/** Indicazione se sono state inizializzate le variabili del servizio */
	public static boolean initializeService = false;

	/** IGestoreIntegrazionePD: lista di gestori, ordinati per priorita' minore */
	private static String[] defaultGestoriIntegrazionePD = null;
	private static java.util.concurrent.ConcurrentHashMap<String, String[]> defaultPerProtocolloGestoreIntegrazionePD = null;

	/** IGestoreCredenziali: lista di gestori delle credenziali */
	private static String [] tipiGestoriCredenziali = null;
	
	/**
	 * Inizializzatore del servizio RicezioneContenutiApplicativi
	 * 
	 * @throws Exception
	 */
	public static synchronized void initializeService(
			ClassNameProperties className,
			OpenSPCoop2Properties propertiesReader, Logger logCore)
			throws CoreException, ProtocolException {
		if (RicezioneContenutiApplicativi.initializeService)
			return; // inizializzato da un altro thread

		String effettuataSuffix = " effettuata.";
		
		Loader loader = Loader.getInstance();
		PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
		
		// Inizializzazione NodeSender
		String classTypeNodeSender = className.getNodeSender(propertiesReader.getNodeSender());
		try {
			INodeSender nodeSender = (INodeSender) loader.newInstance(classTypeNodeSender);
			nodeSender.toString();
			logInfo(logCore, "Inizializzazione gestore NodeSender di tipo "	+ classTypeNodeSender + effettuataSuffix);
		} catch (Exception e) {
			throw new CoreException(
					"Riscontrato errore durante il caricamento della classe 'NodeSender' ["+ classTypeNodeSender
							+ "] da utilizzare per la spedizione nell'infrastruttura: "	+ e.getMessage());
		}

		// Inizializzazione NodeReceiver
		String classType = className.getNodeReceiver(propertiesReader.getNodeReceiver());
		try {
			INodeReceiver nodeReceiver = (INodeReceiver) loader.newInstance(classType);
			nodeReceiver.toString();
			logInfo(logCore, "Inizializzazione gestore NodeReceiver di tipo "+ classType + effettuataSuffix);
		} catch (Exception e) {
			throw new CoreException(
					"Riscontrato errore durante il caricamento della classe 'NodeReceiver' ["+ classType
							+ "] da utilizzare per la ricezione dall'infrastruttura: "+ e.getMessage());
		}

		// Inizializzo IGestoreIntegrazionePD list
		RicezioneContenutiApplicativi.defaultGestoriIntegrazionePD = propertiesReader.getTipoIntegrazionePD();
		for (int i = 0; i < RicezioneContenutiApplicativi.defaultGestoriIntegrazionePD.length; i++) {
			try {
				IGestoreIntegrazionePD gestore = pluginLoader.newIntegrazionePortaDelegata(RicezioneContenutiApplicativi.defaultGestoriIntegrazionePD[i]);
				gestore.toString();
				logInfo(logCore, "Inizializzazione gestore dati di integrazione per le fruizioni di tipo "
								+ RicezioneContenutiApplicativi.defaultGestoriIntegrazionePD[i]	+ effettuataSuffix);
			} catch (Exception e) {
				throw new CoreException(e.getMessage(),e);
			}
		}
		
		// Inizializzo IGestoreIntegrazionePD per protocollo
		RicezioneContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePD = new java.util.concurrent.ConcurrentHashMap<>();
		Enumeration<String> enumProtocols = ProtocolFactoryManager.getInstance().getProtocolNames();
		while (enumProtocols.hasMoreElements()) {
			String protocol = enumProtocols.nextElement();
			String[] tipiIntegrazionePD = propertiesReader.getTipoIntegrazionePD(protocol);
			if(tipiIntegrazionePD!=null && tipiIntegrazionePD.length>0){
				List<String> tipiIntegrazionePerProtocollo = new ArrayList<>();
				for (int i = 0; i < tipiIntegrazionePD.length; i++) {
					try {
						IGestoreIntegrazionePD gestore = pluginLoader.newIntegrazionePortaDelegata(tipiIntegrazionePD[i]);
						gestore.toString();
						tipiIntegrazionePerProtocollo.add(tipiIntegrazionePD[i]);
						logInfo(logCore, "Inizializzazione gestore dati di integrazione (protocollo: "+protocol+") per le fruizioni di tipo "
								+ tipiIntegrazionePD[i]	+ effettuataSuffix);
					} catch (Exception e) {
						throw new CoreException(e.getMessage(),e);
					}
				}
				if(!tipiIntegrazionePerProtocollo.isEmpty()){
					RicezioneContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePD.put(protocol, tipiIntegrazionePerProtocollo.toArray(new String[1]));
				}
			}
		}

		// Inizializzo GestoriCredenziali PD
		RicezioneContenutiApplicativi.tipiGestoriCredenziali = propertiesReader.getTipoGestoreCredenzialiPD();
		if(RicezioneContenutiApplicativi.tipiGestoriCredenziali!=null){
			for (int i = 0; i < RicezioneContenutiApplicativi.tipiGestoriCredenziali.length; i++) {
				classType = className.getGestoreCredenziali(RicezioneContenutiApplicativi.tipiGestoriCredenziali[i]);
				try {
					IGestoreCredenziali gestore = (IGestoreCredenziali)loader.newInstance(classType);
					gestore.toString();
					logInfo(logCore, "Inizializzazione gestore credenziali di tipo "
							+ RicezioneContenutiApplicativi.tipiGestoriCredenziali[i]	+ effettuataSuffix);
				} catch (Exception e) {
					throw new CoreException(
							"Riscontrato errore durante il caricamento della classe 'IGestoreCredenziali' ["+ classType
							+ "] da utilizzare per la gestione delle credenziali di tipo ["
							+ RicezioneContenutiApplicativi.tipiGestoriCredenziali[i]+ "]: " + e.getMessage());
				}
			}
		}
		
		RicezioneContenutiApplicativi.initializeService = true;
	}



	/** Generatore Errori */
	private RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore;
	
	/** Contesto della richiesta */
	private RicezioneContenutiApplicativiContext msgContext;
	
	/** Async Response Callback */
	private IAsyncResponseCallback asyncResponseCallback;

	private ConfigurazionePdDManager configurazionePdDReader;
	private RegistroServiziManager registroServiziReader;
	private MsgDiagnostico msgDiag;
	private Logger logCore;

	private PdDContext context;
	
	private String idMessageRequest;
	private Busta bustaRichiesta;
	private IDSoggetto identitaPdD;
	private GestoreMessaggi msgRequest;
	
	private String servizioApplicativo;
	
	private HashMap<String, Object> internalObjects = null;
	private InRequestContext inRequestContext = null;
	private RicezioneContenutiApplicativiGestioneRisposta parametriGestioneRisposta;
	private ImbustamentoMessage imbustamentoMSG;

	private OpenSPCoop2Message requestMessage;
	
	private OpenSPCoopState openspcoopstate = null;
	private IProtocolFactory<?> protocolFactory = null;

	private PortaDelegata portaDelegata;
	private IDSoggetto soggettoFruitore;
	private IDServizio idServizio;
	
	private HeaderIntegrazione headerIntegrazioneRichiesta;
	private HeaderIntegrazione headerIntegrazioneRisposta;
	private String[] tipiIntegrazionePD;
	
	private volatile boolean portaStateless = false;
	private boolean oneWayVers11 = false;
	private volatile boolean richiestaAsincronaSimmetricaStateless;

	private volatile boolean localForward;

	private volatile boolean asyncWait = false;
	
	public RicezioneContenutiApplicativi(
			RicezioneContenutiApplicativiContext context,
			RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore,
			IAsyncResponseCallback asyncResponseCallback) {
		this.msgContext = context;
		this.generatoreErrore = generatoreErrore;
		this.asyncResponseCallback = asyncResponseCallback;
	}
	
	public void process(Object ... params) throws ConnectorException {
		try {
			this.internalProcess(params);
		}finally {
			if(this.asyncResponseCallback!=null && !this.asyncWait) {
				this.asyncResponseCallback.asyncComplete(AsyncResponseCallbackClientEvent.NONE);
			}
		}
	}
	
	@Override
	public void asyncComplete(AsyncResponseCallbackClientEvent clientEvent, Object ... args) throws ConnectorException { // Questo metodo verrà chiamato dalla catena di metodi degli oggetti (IAsyncResponseCallback) fatta scaturire dal response callback dell'Async Client NIO
		
		if(this.asyncResponseCallback==null) {
			throw new ConnectorException("Async context not active");
		}
		
		if(args==null || args.length<1) {
			throw new ConnectorException("Async context invalid (EsitoLib not found)");
		}
		Object esito = args[0];
		if(! (esito instanceof EsitoLib)) {
			throw new ConnectorException("Async context invalid (EsitoLib with uncorrect type '"+esito.getClass().getName()+"')");
		}
		this.esitoStatelessAfterSendRequest = (EsitoLib) esito;
		
		this.statelessComplete(true);
		
		this.asyncResponseCallback.asyncComplete(clientEvent);
	}
	

	
	private void internalProcess(Object ... params) {
				
		
		// ------------- dati generali -----------------------------
		
		// Context
		this.context = this.msgContext.getPddContext();
		
		// Logger
		this.logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if (this.logCore == null) {
			this.logCore = LoggerWrapperFactory.getLogger(RicezioneContenutiApplicativi.ID_MODULO);
		}
		
		// MsgDiagnostico
		this.msgDiag = this.msgContext.getMsgDiagnostico();
		
		// Messaggio
		this.requestMessage = this.msgContext.getMessageRequest();
		if (this.requestMessage == null) {
			setSOAPFault(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.logCore, this.msgDiag, new Exception("Request message is null"), "LetturaMessaggioRichiesta");
			return;
		}
		
		
		
		
		// ------------- in-handler -----------------------------
		try{
			if(this.context==null) {
				throw new CoreException("Context is null");
			}
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String)this.context.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
		}catch(Exception e){
			setSOAPFault(IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, this.logCore, this.msgDiag, e, "ProtocolFactoryInstance");
			return;
		}
		this.inRequestContext = new InRequestContext(this.logCore,this.protocolFactory, null);
		// TipoPorta
		this.inRequestContext.setTipoPorta(TipoPdD.DELEGATA);
		this.inRequestContext.setIdModulo(this.msgContext.getIdModulo());
		// Informazioni connettore ingresso
		InfoConnettoreIngresso connettore = new InfoConnettoreIngresso();
		connettore.setCredenziali(this.msgContext.getCredenziali());
		if(this.msgContext.getUrlProtocolContext()!=null && 
				this.msgContext.getUrlProtocolContext().getHttpServletRequest()!=null){
			OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance(); // Puo' non essere inizializzato
			if(properties!=null){
				String tipo = properties.getRealContainerCustom();
				if(tipo!=null) {
					try {
						ClassNameProperties className = ClassNameProperties.getInstance();
						Loader loader = Loader.getInstance();
						// Check tipi registrati
						String tipoClass = className.getRealmContainerCustom(tipo);
						IAutorizzazioneSecurityContainer authEngine = (IAutorizzazioneSecurityContainer) loader.newInstance(tipoClass);
						authEngine.init(this.msgContext.getUrlProtocolContext().getHttpServletRequest(), 
								this.context, this.protocolFactory);
						AutorizzazioneHttpServletRequest httpServletRequestAuth = new AutorizzazioneHttpServletRequest(this.msgContext.getUrlProtocolContext().getHttpServletRequest(), authEngine);
						this.msgContext.getUrlProtocolContext().updateHttpServletRequest(httpServletRequestAuth);					
					}catch(Exception e){
						setSOAPFault(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.logCore, this.msgDiag, e, "AutorizzazioneSecurityContainerInstance");
						return;
					}
				}
			}
		}
		connettore.setUrlProtocolContext(this.msgContext.getUrlProtocolContext());
		if(ServiceBinding.SOAP.equals(this.requestMessage.getServiceBinding())){
			try{
				connettore.setSoapAction(this.requestMessage.castAsSoap().getSoapAction());
			}catch(Exception e){
				setSOAPFault(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.logCore,this.msgDiag, e, "LetturaSoapAction");
				return;
			}
		}
		connettore.setFromLocation(this.msgContext.getSourceLocation());
		this.inRequestContext.setConnettore(connettore);
		// Data accettazione richiesta
		this.inRequestContext.setDataAccettazioneRichiesta(this.msgContext.getDataAccettazioneRichiesta());
		// Data ingresso richiesta
		this.inRequestContext.setDataElaborazioneMessaggio(this.msgContext.getDataIngressoRichiesta());
		// PdDContext
		this.inRequestContext.setPddContext(this.context);
		// Dati Messaggio
		this.inRequestContext.setMessaggio(this.requestMessage);
		// Invoke handler
		try{
			GestoreHandlers.inRequest(this.inRequestContext, this.msgDiag, this.logCore);
		}catch(HandlerException e){
			setSOAPFault(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.logCore,this.msgDiag, e, e.getIdentitaHandler());
			return;
		}catch(Exception e){
			setSOAPFault(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.logCore,this.msgDiag, e, "InvocazioneInRequestHandler");
			return;
		}
		
		
		
		
		
			
		// ------------- process -----------------------------
		this.internalObjects = new HashMap<>();
		try{
			processEngine(this.internalObjects,params);
		} catch(TracciamentoException e){
			setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore,this.msgDiag, e, "TracciamentoNonRiuscito");
			return;
		} catch(DumpException e){
			setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore,this.msgDiag, e, DumpException.DUMP_NON_RIUSCITO);
			return;
		} catch(ProtocolException e){
			setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore,this.msgDiag, e, "InstanziazioneProtocolFactoryNonRiuscita");
			return;
		} 

		if(this.asyncResponseCallback==null) {
			this._processComplete(false);
		}
	}
	private void _processComplete(boolean invokedFromAsyncConnector) {
		
		// ogni blocco di codice controllare che non sia già stato invocato per via della doppia invocazione '_processComplete'
		
		if(!this.internalObjects.containsKey(CostantiPdD.PROCESS_COMPLETE_SET_TIPO)) {
			try{
				if(invokedFromAsyncConnector) {
					// debug
				}
				
				if(this.context!=null  && this.msgContext.getIntegrazione()!=null){
					if(this.context.containsKey(CostantiPdD.TIPO_PROCESSAMENTO_MTOM_RICHIESTA)){
						this.msgContext.getIntegrazione().setTipoProcessamentoMtomXopRichiesta(
								(String)this.context.getObject(CostantiPdD.TIPO_PROCESSAMENTO_MTOM_RICHIESTA));
					}
					if(this.context.containsKey(CostantiPdD.TIPO_PROCESSAMENTO_MTOM_RISPOSTA)){
						this.msgContext.getIntegrazione().setTipoProcessamentoMtomXopRisposta(
								(String)this.context.getObject(CostantiPdD.TIPO_PROCESSAMENTO_MTOM_RISPOSTA));
					}
					if(this.context.containsKey(CostantiPdD.TIPO_SICUREZZA_MESSAGGIO_RICHIESTA)){
						this.msgContext.getIntegrazione().setTipoMessageSecurityRichiesta(
								(String)this.context.getObject(CostantiPdD.TIPO_SICUREZZA_MESSAGGIO_RICHIESTA));
					}
					if(this.context.containsKey(CostantiPdD.TIPO_SICUREZZA_MESSAGGIO_RISPOSTA)){
						this.msgContext.getIntegrazione().setTipoMessageSecurityRisposta(
								(String)this.context.getObject(CostantiPdD.TIPO_SICUREZZA_MESSAGGIO_RISPOSTA));
					}
				}
			}catch(Exception e){
				setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore,this.msgDiag, e, "FinalizeIntegrationContextRicezioneContenutiApplicativi");
				return;
			}
			this.internalObjects.put(CostantiPdD.PROCESS_COMPLETE_SET_TIPO, true);
		}
		
		
		

		
		
		
		
		// ------------- Dump richiesta in ingresso -----------------------------
		if(!this.internalObjects.containsKey(CostantiPdD.DUMP_RICHIESTA_EFFETTUATO) &&
			Dump.isSistemaDumpDisponibile()){
			try{
				if(this.configurazionePdDReader==null) {
					this.configurazionePdDReader = ConfigurazionePdDManager.getInstance();
				}
				
				if(!this.internalObjects.containsKey(CostantiPdD.DUMP_CONFIG)) {
					URLProtocolContext urlProtocolContext = this.msgContext.getUrlProtocolContext();
					if(urlProtocolContext!=null && urlProtocolContext.getInterfaceName()!=null) {
						IDPortaDelegata identificativoPortaDelegata = new IDPortaDelegata();
						identificativoPortaDelegata.setNome(urlProtocolContext.getInterfaceName());
						this.portaDelegata = this.configurazionePdDReader.getPortaDelegataSafeMethod(identificativoPortaDelegata, this.msgContext.getRequestInfo());
						if(this.portaDelegata!=null) {
							DumpConfigurazione dumpConfig = this.configurazionePdDReader.getDumpConfigurazione(this.portaDelegata);
							this.internalObjects.put(CostantiPdD.DUMP_CONFIG, dumpConfig);
						}
					}
				}
				
				OpenSPCoop2Message msgRichiesta = this.inRequestContext.getMessaggio();
				if (msgRichiesta!=null) {
					
					Dump dumpApplicativo = getDump(this.configurazionePdDReader, this.protocolFactory, this.internalObjects, this.msgDiag.getPorta());
					dumpApplicativo.dumpRichiestaIngresso(msgRichiesta, 
							this.inRequestContext.getConnettore().getUrlProtocolContext());
				}
			}catch(DumpException e){
				setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore,this.msgDiag, e, DumpException.DUMP_NON_RIUSCITO);
				return;
			}catch(Exception e){
				// Se non riesco ad accedere alla configurazione sicuramente gia' nel messaggio di risposta e' presente l'errore di PdD non correttamente inizializzata
			}
		}
		
		
		
		
		
		// ------------- out-handler -----------------------------
		OutResponseContext outResponseContext = null;
		if(!this.internalObjects.containsKey(CostantiPdD.PROCESS_COMPLETE_GESTORE_OUT_RESPONSE_HANDLER)) {
			outResponseContext = new OutResponseContext(this.logCore,this.protocolFactory, null);
			// TipoPorta
			outResponseContext.setTipoPorta(this.msgContext.getTipoPorta());
			outResponseContext.setIdModulo(this.msgContext.getIdModulo());
			// DataUscitaMessaggio
			outResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
			// PddContext
			outResponseContext.setPddContext(this.inRequestContext.getPddContext());
			// Informazioni protocollo e di integrazione
			outResponseContext.setProtocollo(this.msgContext.getProtocol());
			outResponseContext.setIntegrazione(this.msgContext.getIntegrazione());
			// Header di trasporto della risposta
			outResponseContext.setResponseHeaders(this.msgContext.getResponseHeaders());
			// Messaggio
			OpenSPCoop2Message msgResponse = this.msgContext.getMessageResponse();
			outResponseContext.setMessaggio(msgResponse);
			// Invoke handler
			try{
				GestoreHandlers.outResponse(outResponseContext, this.msgDiag, this.logCore);
			}catch(HandlerException e){
				setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore,this.msgDiag, e, e.getIdentitaHandler());
				return;
			}catch(Exception e){
				setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore,this.msgDiag, e, "InvocazioneOutResponseHandler");
				return;
			}
			this.internalObjects.put(CostantiPdD.PROCESS_COMPLETE_GESTORE_OUT_RESPONSE_HANDLER, true);
		}
		

		
		
		
		// ---------------- fine gestione ------------------------------
		OpenSPCoop2Message msgRisposta = null;
		if(outResponseContext!=null) {
			try{
				msgRisposta = outResponseContext.getMessaggio();
				boolean rispostaPresente = true;
				OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance(); // Puo' non essere inizializzato
				if(properties!=null){
					rispostaPresente = ServicesUtils.verificaRispostaRelazioneCodiceTrasporto202(this.protocolFactory,OpenSPCoop2Properties.getInstance(), msgRisposta,true);
				}
				if(rispostaPresente){
					this.msgContext.setMessageResponse(msgRisposta);
				}else{
					this.msgContext.setMessageResponse(null);
					msgRisposta = null;
				}
			}catch(Exception e){
				setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore,this.msgDiag, e, "FineGestioneRicezioneContenutiApplicativi");
				return;
			}
		}
		
		
		
		
		
		
		
		
		
		// ------------- Dump risposta in uscita-----------------------------
		if(!this.internalObjects.containsKey(CostantiPdD.DUMP_RISPOSTA_EFFETTUATO) &&
				Dump.isSistemaDumpDisponibile() &&
				outResponseContext!=null){
			try{
				if(this.configurazionePdDReader==null) {
					this.configurazionePdDReader = ConfigurazionePdDManager.getInstance();
				}
				if (msgRisposta!=null) {
					
					Dump dumpApplicativo = getDump(this.configurazionePdDReader, this.protocolFactory, this.internalObjects, this.msgDiag.getPorta());
					if(outResponseContext.getResponseHeaders()==null) {
						outResponseContext.setResponseHeaders(new HashMap<>());
					}
					Map<String, List<String>> propertiesTrasporto = outResponseContext.getResponseHeaders();
					ServicesUtils.setGovWayHeaderResponse(this.requestMessage.getServiceBinding(),
							msgRisposta, OpenSPCoop2Properties.getInstance(),
							propertiesTrasporto, this.logCore, true, outResponseContext.getPddContext(), this.msgContext.getRequestInfo());
					dumpApplicativo.dumpRispostaUscita(msgRisposta, 
							this.inRequestContext.getConnettore().getUrlProtocolContext(), 
							outResponseContext.getResponseHeaders());
					this.internalObjects.put(CostantiPdD.DUMP_RISPOSTA_EFFETTUATO, true);
				}
			}catch(DumpException e){
				setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore,this.msgDiag, e, DumpException.DUMP_NON_RIUSCITO);
			}catch(Exception e){
				logError(this.logCore, e.getMessage(),e);
				// Se non riesco ad accedere alla configurazione sicuramente gia' nel messaggio di risposta e' presente l'errore di PdD non correttamente inizializzata
			}
		}
		
		
	}
	
	private Dump getDump(ConfigurazionePdDManager configurazionePdDReader,
			IProtocolFactory<?> protocolFactory,
			HashMap<String, Object> internalObjects,
			String nomePorta) throws DumpException, DriverRegistroServiziException {
		
		DumpConfigurazione dumpConfig = null;
		if(internalObjects.containsKey(CostantiPdD.DUMP_CONFIG)) {
			dumpConfig = (DumpConfigurazione) internalObjects.get(CostantiPdD.DUMP_CONFIG); // dovrebbe essere stata impostata per la pd/pa specifica
		}
		else {
			dumpConfig = configurazionePdDReader.getDumpConfigurazionePortaDelegata();
		}
		
		ProtocolContext protocolContext = this.msgContext.getProtocol();
		URLProtocolContext urlProtocolContext = this.msgContext.getUrlProtocolContext();
		IDSoggetto soggettoErogatore = null;
		IDSoggetto fruitore = null;
		IDSoggetto dominio = null;
		String idRichiesta = null;
		if(protocolContext!=null) {
			if(protocolContext.getTipoServizio()!=null && protocolContext.getServizio()!=null && protocolContext.getVersioneServizio()!=null &&
				protocolContext.getErogatore()!=null && protocolContext.getErogatore().getTipo()!=null && protocolContext.getErogatore().getNome()!=null) {
				this.idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(protocolContext.getTipoServizio(), protocolContext.getServizio(), 
						protocolContext.getErogatore(), protocolContext.getVersioneServizio());
			}
			dominio = protocolContext.getDominio();
			idRichiesta = protocolContext.getIdRichiesta();
			if(protocolContext.getFruitore()!=null && protocolContext.getFruitore().getTipo()!=null && protocolContext.getFruitore().getNome()!=null) {
				fruitore = protocolContext.getFruitore();
			}
		}
		
		if(
				(dominio == null || fruitore==null || this.idServizio == null) &&
				(urlProtocolContext!=null && urlProtocolContext.getInterfaceName()!=null) 
				){
			IDPortaDelegata identificativoPortaDelegata = new IDPortaDelegata();
			identificativoPortaDelegata.setNome(urlProtocolContext.getInterfaceName());
			this.portaDelegata = null;
			try {
				this.portaDelegata = configurazionePdDReader.getPortaDelegataSafeMethod(identificativoPortaDelegata, this.msgContext.getRequestInfo());
			}catch(Exception e) {
				// ignore
			}
			if(this.portaDelegata!=null) {
				// Aggiorno tutti
				soggettoErogatore = new IDSoggetto(this.portaDelegata.getSoggettoErogatore().getTipo(),this.portaDelegata.getSoggettoErogatore().getNome());
				if(this.portaDelegata.getServizio()!=null) {
					this.idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(this.portaDelegata.getServizio().getTipo(),this.portaDelegata.getServizio().getNome(), 
								soggettoErogatore, this.portaDelegata.getServizio().getVersione());
				}
				dominio = new IDSoggetto(this.portaDelegata.getTipoSoggettoProprietario(), this.portaDelegata.getNomeSoggettoProprietario());
				fruitore = new IDSoggetto(this.portaDelegata.getTipoSoggettoProprietario(), this.portaDelegata.getNomeSoggettoProprietario());
				try {
					dominio.setCodicePorta(RegistroServiziManager.getInstance().getDominio(dominio, null, protocolFactory, this.msgContext.getRequestInfo()));
				}catch(Exception e) {
					dominio = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(protocolFactory.getProtocol(), this.msgContext.getRequestInfo());
				}
			}
		}
		if(this.idServizio!=null) {
			if(protocolContext!=null && protocolContext.getAzione()!=null) {
				this.idServizio.setAzione(protocolContext.getAzione());
			}
			else if(this.msgContext.getRequestInfo()!=null && 
					this.msgContext.getRequestInfo().getIdServizio()!=null && this.msgContext.getRequestInfo().getIdServizio().getAzione()!=null) {
				this.idServizio.setAzione(this.msgContext.getRequestInfo().getIdServizio().getAzione());
			}
		}
		if(dominio==null) {
			dominio = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(protocolFactory.getProtocol(), this.msgContext.getRequestInfo());
		}

		Dump dumpApplicativo = null;
		if(this.idServizio!=null){
			dumpApplicativo = new Dump(dominio,
					this.msgContext.getIdModulo(), 
					idRichiesta, fruitore, this.idServizio,
					this.msgContext.getTipoPorta(), nomePorta, this.msgContext.getPddContext(),
					null,null,
					dumpConfig);
		}else{
			dumpApplicativo = new Dump(dominio,
					this.msgContext.getIdModulo(),this.msgContext.getTipoPorta(), nomePorta,this.msgContext.getPddContext(),
					null,null,
					dumpConfig);
		}
		
		return  dumpApplicativo;
	}
	
	private void setSOAPFault(IntegrationFunctionError integrationFunctionError, Logger logCore, MsgDiagnostico msgDiag, Exception e, String posizione){
		
		HandlerException he = null;
		if(e instanceof HandlerException cast){
			he = cast;
		}
		
		if(msgDiag!=null){
			if(he!=null){
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e, posizione);
				}
			}else{
				msgDiag.logErroreGenerico(e, posizione);
			}
		}
		else {
			if(e!=null) {
				logError(logCore, posizione+": "+e.getMessage(),e);
			}
			else {
				logError(logCore, posizione);
			}
		}
		
		IntegrationFunctionError ifError = integrationFunctionError;
		if(he!=null && he.getIntegrationFunctionError()!=null) {
			ifError = he.getIntegrationFunctionError();
		}
		ErroreIntegrazione erroreIntegrazioneGenerato = null;
		if(he!=null){
			erroreIntegrazioneGenerato = he.convertToErroreIntegrazione();
		}
		
		if (this.msgContext.isGestioneRisposta()) {
			String posizioneFault = null;
			if(e!=null) {
				posizioneFault = posizione+": "+e.getMessage();
			}
			else {
				posizioneFault = posizione;
			}
			if(erroreIntegrazioneGenerato==null) {
				erroreIntegrazioneGenerato = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(posizioneFault);
			}
			OpenSPCoop2Message messageFault = this.generatoreErrore.build(this.msgContext.getPddContext(), ifError, 
					erroreIntegrazioneGenerato,
					e, null);
			this.msgContext.setMessageResponse(messageFault);
		}
	}
	
	private boolean checkInizializzazione(Logger logCore,
			PdDContext pddContext) {
		if (!OpenSPCoop2Startup.initialize) {
			String msgErrore = "Inizializzazione di GovWay non correttamente effettuata";
			logError(logCore, "["+ RicezioneContenutiApplicativi.ID_MODULO+ "]  "+msgErrore);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"InizializzazioneGovWay");
				}
			}catch(Exception t){logError(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (InizializzazioneGovWay): "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA),null,null)));
			}
			return false;
		}
		if (!TimerMonitoraggioRisorseThread.isRisorseDisponibili()) {
			String msgErrore = "Risorse di sistema non disponibili: "+ TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile().getMessage();
			logError(logCore, "["+ RicezioneContenutiApplicativi.ID_MODULO+ "]  "+msgErrore,TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile());
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"InizializzazioneRisorseGovWay");
				}
			}catch(Exception t){logError(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (InizializzazioneRisorseGovWay): "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_532_RISORSE_NON_DISPONIBILI),null,null)));
			}
			return false;
		}
		if (!TimerThresholdThread.freeSpace) {
			String msgErrore = "Non sono disponibili abbastanza risorse per la gestione della richiesta";
			logError(logCore, "["+ RicezioneContenutiApplicativi.ID_MODULO+ "]  "+msgErrore);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"DisponibilitaRisorseGovWay");
				}
			}catch(Exception t){logError(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (DisponibilitaRisorseGovWay): "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_533_RISORSE_DISPONIBILI_LIVELLO_CRITICO),null,null)));
			}
			return false;
		}
		if (!Tracciamento.tracciamentoDisponibile) {
			String msgErrore = "Tracciatura non disponibile: "+ Tracciamento.motivoMalfunzionamentoTracciamento.getMessage();
			logError(logCore, "["+ RicezioneContenutiApplicativi.ID_MODULO
					+ "]  "+msgErrore,Tracciamento.motivoMalfunzionamentoTracciamento);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"Tracciamento");
				}
			}catch(Exception t){logError(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (Tracciamento): "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_545_TRACCIATURA_NON_FUNZIONANTE),null,null)));
			}
			return false;
		}
		if (!MsgDiagnostico.gestoreDiagnosticaDisponibile) {
			String msgErrore = "Sistema di diagnostica non disponibile: "+ MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage();
			logError(logCore, "["+ RicezioneContenutiApplicativi.ID_MODULO
					+ "]  "+msgErrore,MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			try{
				// provo ad emetter un diagnostico lo stesso (molto probabilmente non ci riuscirà essendo proprio la risorsa diagnostica non disponibile)
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"Diagnostica");
				}
			}catch(Exception t){logDebug(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (Diagnostica): "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_546_DIAGNOSTICA_NON_FUNZIONANTE),null,null)));
			}
			return false;
		}
		if (!Dump.isSistemaDumpDisponibile()) {
			String msgErrore = "Sistema di dump dei contenuti applicativi non disponibile: "+ Dump.getMotivoMalfunzionamentoDump().getMessage();
			logError(logCore, "["+ RicezioneContenutiApplicativi.ID_MODULO
					+ "]  "+msgErrore,Dump.getMotivoMalfunzionamentoDump());
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"Dump");
				}
			}catch(Exception t){logError(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (Dump): "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_547_DUMP_CONTENUTI_APPLICATIVI_NON_FUNZIONANTE),null,null)));
			}
			return false;
		}
		// Check Configurazione (XML)
		this.configurazionePdDReader = ConfigurazionePdDManager.getInstance();
		try{
			this.configurazionePdDReader.verificaConsistenzaConfigurazione();	
		}catch(Exception e){
			String msgErrore = "Riscontrato errore durante la verifica della consistenza della configurazione";
			logError(logCore, "["+ RicezioneContenutiApplicativi.ID_MODULO
					+ "]  "+msgErrore,e);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"CheckConfigurazioneGovWay");
				}
			}catch(Exception t){logError(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (CheckConfigurazioneGovWay): "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),null,null)));
			}
			return false;
		}
		// Check RegistroServizi (XML)
		this.registroServiziReader = RegistroServiziManager.getInstance();
		try{
			this.registroServiziReader.verificaConsistenzaRegistroServizi();
		}catch(Exception e){
			String msgErrore = "Riscontrato errore durante la verifica del registro dei servizi";
			logError(logCore, "["+ RicezioneContenutiApplicativi.ID_MODULO
					+ "]  "+msgErrore,e);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"CheckRegistroServizi");
				}
			}catch(Exception t){logError(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (CheckRegistroServizi): "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_534_REGISTRO_DEI_SERVIZI_NON_DISPONIBILE),null,null)));
			}
			return false;
		}
		
		return true;
	}

	private void processEngine(HashMap<String, Object> internalObjects,Object ... params) 
			throws TracciamentoException, DumpException, ProtocolException {

	
		
		/* ------------ Lettura parametri della richiesta ------------- */

		// Messaggio di ingresso
		this.requestMessage = this.inRequestContext.getMessaggio();
				
		// Logger
		this.logCore = this.inRequestContext.getLogCore();
		
		// ID Transazione
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, this.inRequestContext.getPddContext());
		
		// RequestInfo
		RequestInfo requestInfo = this.msgContext.getRequestInfo();
		
		// Parametri della porta delegata invocata
		URLProtocolContext urlProtocolContext = this.msgContext.getUrlProtocolContext();

		// Credenziali utilizzate nella richiesta
		Credenziali credenziali = this.msgContext.getCredenziali();

		// Autenticazione/Autorizzazione registrate
		ClassNameProperties className = ClassNameProperties.getInstance();

		// PropertiesReader
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
		PdDContext pddContext = this.inRequestContext.getPddContext();
		if (propertiesReader == null) {
			String msg = "Inizializzazione di GovWay non correttamente effettuata: OpenSPCoopProperties";
			logError(this.logCore, msg);
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse(this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), null, null));
			}
			return;
		}

		if(requestInfo==null) {
			String msg = "Inizializzazione di GovWay non correttamente effettuata: RequestInfo is null";
			logError(this.logCore, msg);
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse(this.generatoreErrore.build(pddContext, IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg), null, null));
			}
			return;
		}
		
		// IdentificativoPdD
		this.identitaPdD = requestInfo.getIdentitaPdD();

		// ProtocolFactory
		this.protocolFactory = requestInfo.getProtocolFactory();
		IProtocolManager protocolManager = this.protocolFactory.createProtocolManager();
		
		// ProprietaErroreApplicativo
		ProprietaErroreApplicativo proprietaErroreAppl = propertiesReader
				.getProprietaGestioneErrorePD(protocolManager);
		proprietaErroreAppl.setDominio(this.identitaPdD.getCodicePorta());
		proprietaErroreAppl.setIdModulo(this.msgContext.getIdModulo());
		if(this.msgContext.isForceFaultAsXML()){
			proprietaErroreAppl.setFaultAsXML(true); // es. se siamo in una richiesta http senza SOAP, un SoapFault non ha senso
		}
		this.msgContext.setProprietaErroreAppl(proprietaErroreAppl);
		this.generatoreErrore.updateProprietaErroreApplicativo(proprietaErroreAppl);
				
		// MESSAGGIO DI LIBRERIA
		this.imbustamentoMSG = new ImbustamentoMessage();
		
		// Context di risposta
		this.msgContext.setProtocol(new ProtocolContext());
		this.msgContext.getProtocol().setDominio(this.msgContext.getIdentitaPdD());
		this.msgContext.setIntegrazione(new IntegrationContext());

		
		
		
		
		
		/* ------------ Controllo inizializzazione OpenSPCoop ------------------ */
		if(!checkInizializzazione(this.logCore,pddContext)) {
			return;
		}
		
		
		
		// Logger dei messaggi diagnostici
		String nomePorta = null;
		if(requestInfo.getProtocolContext().getInterfaceName()!=null){
			nomePorta = requestInfo.getProtocolContext().getInterfaceName();
		}
		else{
			nomePorta = urlProtocolContext.getFunctionParameters() + "_urlInvocazione("+ urlProtocolContext.getUrlInvocazione_formBased() + ")";
		}
		this.msgDiag = MsgDiagnostico.newInstance(TipoPdD.DELEGATA,this.identitaPdD, this.msgContext.getIdModulo(),nomePorta,requestInfo,this.configurazionePdDReader);
		if(this.msgDiag==null) {
			String msg = "Inizializzazione di GovWay non correttamente effettuata: MsgDiagnostico is null";
			logError(this.logCore, msg);
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse(this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), null, null));
			}
			return;
		}
		this.msgContext.setMsgDiagnostico(this.msgDiag); // aggiorno msg diagnostico
		this.msgDiag.setPddContext(this.inRequestContext.getPddContext(), this.protocolFactory);
		this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
		

		// set credenziali
		setCredenziali(credenziali, this.msgDiag);

		// inizializzazione risorse statiche
		try {
			if (!RicezioneContenutiApplicativi.initializeService) {
				this.msgDiag.mediumDebug("Inizializzazione risorse statiche...");
				RicezioneContenutiApplicativi.initializeService(className,propertiesReader, this.logCore);
			}
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"InizializzazioneRisorseServizioRicezioneContenutiApplicativi");
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA),e,null)));
			}
			return;
		}

		// Loader classi dinamiche
		Loader loader = Loader.getInstance();
		PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
		
		// ConnectorInMessage
		@SuppressWarnings("unused")
		ConnectorInMessage connectorInMessage = null;
		if(params!=null){
			for (int i = 0; i < params.length; i++) {
				if(params[i] instanceof ConnectorInMessage ci ){
					connectorInMessage = ci;
					break;
				}
			}
		}
		
		Transaction transaction = null;
		try{
			transaction = TransactionContext.getTransaction(idTransazione);
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e,"getTransaction");
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),e,null)));
			}
			return;
		}
		
		
		

		// --------- OPENSPCOOPSTATE ---------
		try{ // finally in fondo, vedi  #try-finally-openspcoopstate#
			
		this.msgDiag.mediumDebug("Inizializzazione connessione al database...");
		try {
			this.openspcoopstate = new OpenSPCoopStateful();
			this.openspcoopstate.setUseConnection(false); // gestione stateless per default
			this.openspcoopstate.initResource(this.identitaPdD, this.msgContext.getIdModulo(), idTransazione);
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"openspcoopstate.initResource()");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION), e,null)));
			}
			return;
		}
		
		// Refresh reader
		this.registroServiziReader = this.registroServiziReader.refreshState(this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta());
		this.configurazionePdDReader = this.configurazionePdDReader.refreshState(this.registroServiziReader);
		this.msgDiag.updateState(this.configurazionePdDReader);
		
		
		
		
		

		
		

		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Identificazione porta delegata e soggetto fruitore...");
		
		/* ------------ Identificazione Porta Delegata e SoggettoFruitore */
		this.msgDiag.mediumDebug("Identificazione porta delegata e soggetto fruitore...");
		IDPortaDelegata identificativoPortaDelegata = null;
		this.portaDelegata = null;
		String nomeUtilizzatoPerErrore = null;
		try {
			if(urlProtocolContext.getInterfaceName()!=null) {
				identificativoPortaDelegata = new IDPortaDelegata();
				identificativoPortaDelegata.setNome(urlProtocolContext.getInterfaceName());
				this.portaDelegata = this.configurazionePdDReader.getPortaDelegata(identificativoPortaDelegata, requestInfo);
				nomeUtilizzatoPerErrore = identificativoPortaDelegata.getNome();
			}
			else {
				throw new CoreException("InterfaceName non presente");
			}
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"getPorta");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_502_IDENTIFICAZIONE_PORTA), e,null)));
			}
			return;
		}

		// Raccolgo dati
		this.soggettoFruitore = null;
		try {
			if(this.portaDelegata!=null) {
				this.soggettoFruitore = new IDSoggetto(this.portaDelegata.getTipoSoggettoProprietario(), this.portaDelegata.getNomeSoggettoProprietario());
				this.soggettoFruitore.setCodicePorta(this.configurazionePdDReader.getIdentificativoPorta(this.soggettoFruitore, this.protocolFactory, requestInfo));
			}
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"getIdentificativoPorta");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_502_IDENTIFICAZIONE_PORTA), e,null)));
			}
			return;
		}
		if(identificativoPortaDelegata.getIdentificativiFruizione()==null) {
			identificativoPortaDelegata.setIdentificativiFruizione(new IdentificativiFruizione());
		}
		identificativoPortaDelegata.getIdentificativiFruizione().setSoggettoFruitore(this.soggettoFruitore);
		if(this.soggettoFruitore!=null) {
			this.identitaPdD = this.soggettoFruitore; // la PdD Assume l'identita del soggetto
		}
		this.msgContext.getProtocol().setDominio(this.identitaPdD);
		this.msgContext.setIdentitaPdD(this.identitaPdD);
		// che possiede la Porta Delegata ID Porta Delegata
		this.msgContext.getIntegrazione().setIdPD(identificativoPortaDelegata);
		// altri contesti
		this.msgDiag.setDominio(this.identitaPdD); // imposto anche il dominio nel msgDiag
		this.msgDiag.setFruitore(this.soggettoFruitore);
		this.msgDiag.addKeyword(CostantiPdD.KEY_PORTA_DELEGATA, identificativoPortaDelegata.getNome());
		this.msgDiag.addKeywords(this.soggettoFruitore);
		proprietaErroreAppl.setDominio(this.identitaPdD.getCodicePorta()); // imposto
		// requestInfo
		requestInfo.setIdentitaPdD(this.identitaPdD);
		// anche il dominio per gli errori
		this.msgContext.setProprietaErroreAppl(proprietaErroreAppl);
		// GeneratoreErrore
		this.generatoreErrore.updateDominio(this.identitaPdD);
		this.generatoreErrore.updateProprietaErroreApplicativo(proprietaErroreAppl);
	
		
		
		
		
		

		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Lettura header di integrazione...");
		

		/* --------------- Header Integrazione --------------- */
		this.msgDiag.mediumDebug("Lettura header di integrazione...");
		this.headerIntegrazioneRichiesta = null;
		if (this.msgContext.getHeaderIntegrazioneRichiesta() != null)
			this.headerIntegrazioneRichiesta = this.msgContext.getHeaderIntegrazioneRichiesta(); // prendo quello dell'IntegrationManager
		else
			this.headerIntegrazioneRichiesta = new HeaderIntegrazione(idTransazione);
		this.headerIntegrazioneRisposta = null;
		this.tipiIntegrazionePD = null;
		try {
			this.tipiIntegrazionePD = this.configurazionePdDReader.getTipiIntegrazione(this.portaDelegata);
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e, "getTipiIntegrazione(pd)");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		if (this.tipiIntegrazionePD == null){
			if(RicezioneContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePD.containsKey(this.protocolFactory.getProtocol()))
				this.tipiIntegrazionePD = RicezioneContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePD.get(this.protocolFactory.getProtocol());
			else
				this.tipiIntegrazionePD = RicezioneContenutiApplicativi.defaultGestoriIntegrazionePD;
		}

		InRequestPDMessage inRequestPDMessage = new InRequestPDMessage();
		inRequestPDMessage.setBustaRichiesta(null); // non lo si conosce l'aggiorno appena letto le informazioni dal registro
		inRequestPDMessage.setMessage(this.requestMessage);
		inRequestPDMessage.setUrlProtocolContext(this.msgContext.getUrlProtocolContext());
		inRequestPDMessage.setPortaDelegata(this.portaDelegata);
		inRequestPDMessage.setSoggettoPropeprietarioPortaDelegata(this.soggettoFruitore);
		for (int i = 0; i < this.tipiIntegrazionePD.length; i++) {
			try {
				
				IGestoreIntegrazionePD gestore = pluginLoader.newIntegrazionePortaDelegata(this.tipiIntegrazionePD[i]);
				if(gestore!=null){
					String classType = null;
					try {
						classType = gestore.getClass().getName();
						AbstractCore.init(gestore, pddContext, this.protocolFactory);
					} catch (Exception e) {
						throw new CoreException(
								"Riscontrato errore durante l'inizializzazione della classe (GestoreIntegrazionePD) ["+ classType
										+ "] da utilizzare per la gestione dell'integrazione delle fruizioni di tipo ["+ this.tipiIntegrazionePD[i] + "]: " + e.getMessage());
					}
					gestore.readInRequestHeader(this.headerIntegrazioneRichiesta,inRequestPDMessage);
				}  else {
					this.msgDiag.logErroreGenerico("Lettura Gestore header di integrazione ["
									+ this.tipiIntegrazionePD[i]+ "]  non riuscita: non inizializzato", 
									"gestoriIntegrazionePD.get("+this.tipiIntegrazionePD[i]+")");
				}
			} catch (Exception e) {
				logDebug(this.logCore, "Errore durante la lettura dell'header di integrazione ["+ this.tipiIntegrazionePD[i]
								+ "]: "+ e.getMessage(),e);
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,this.tipiIntegrazionePD[i]);
				this.msgDiag.addKeywordErroreProcessamento(e);
				this.msgDiag.logPersonalizzato("headerIntegrazione.letturaFallita");
			}
		}


		
		

		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Raccolta dati Gestione CORS ...");
		// NOTA: i dati CORS sono memorizzati solamente nella porta principale e non in quelle di eventuali azioni delegate.
		//       deve quindi essere recuperata prima di sostituire la pd con una più specifica
		CorsConfigurazione cors = null;
		HttpServletRequest httpServletRequest = null;
		boolean effettuareGestioneCORS = false;
		try {
			if(requestInfo!=null && requestInfo.getProtocolContext()!=null) {
				httpServletRequest = requestInfo.getProtocolContext().getHttpServletRequest();	
			}
			
			if(httpServletRequest!=null && HttpRequestMethod.OPTIONS.name().equalsIgnoreCase(httpServletRequest.getMethod())) {
				
				Object nomePortaObject = pddContext.getObject(CostantiPdD.NOME_PORTA_INVOCATA);
				String nomePortaS = null;
				if(nomePortaObject instanceof String s) {
					nomePortaS = s;
				}
				PortaDelegata pdDefault = null;
				if(nomePortaS!=null) {
					IDPortaDelegata idPDdefault = new IDPortaDelegata();
					idPDdefault.setNome(nomePortaS);
					pdDefault = this.configurazionePdDReader.getPortaDelegataSafeMethod(idPDdefault, requestInfo);
				}
				if(pdDefault!=null) {
					cors = this.configurazionePdDReader.getConfigurazioneCORS(pdDefault);
				}
				else if(this.portaDelegata!=null) {
					cors = this.configurazionePdDReader.getConfigurazioneCORS(this.portaDelegata);
				}
				else {
					cors = this.configurazionePdDReader.getConfigurazioneCORS();
				}
			}
			else {
				cors = new CorsConfigurazione();
				cors.setStato(StatoFunzionalita.DISABILITATO);
			}
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e, "configurazionePdDReader.getConfigurazioneCORS(pd)");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Lettura servizio associato alla PD invocata...");
		
		
		

		/*
		 * ------------- Lettura azione associato alla PD invocata ed eventuale aggiornamento della pd utilizzata ------------
		 */
		this.msgDiag.mediumDebug("Lettura azione associato alla PD invocata...");
		String idModuloInAttesa = null;
		if (this.msgContext.isGestioneRisposta())
			idModuloInAttesa = this.msgContext.getIdModulo();
		RichiestaDelegata richiestaDelegata = new RichiestaDelegata(
				identificativoPortaDelegata, null,
				idModuloInAttesa, proprietaErroreAppl, this.identitaPdD);
		richiestaDelegata.setIntegrazione(this.msgContext.getIntegrazione());
		richiestaDelegata.setProtocol(this.msgContext.getProtocol());
		this.idServizio = null;
		try {
			if(this.portaDelegata==null) {
				throw new CoreException("PortaDelgata non trovata");
			}
			IDSoggetto soggettoErogatore = new IDSoggetto(this.portaDelegata.getSoggettoErogatore().getTipo(),this.portaDelegata.getSoggettoErogatore().getNome());
			this.idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(this.portaDelegata.getServizio().getTipo(),this.portaDelegata.getServizio().getNome(), 
					soggettoErogatore, this.portaDelegata.getServizio().getVersione());
			if(requestInfo.getIdServizio()!=null && requestInfo.getIdServizio().getAzione()!=null){
				// gia identificata
				this.idServizio.setAzione(requestInfo.getIdServizio().getAzione());
				// aggiorno anche codice porta erogatore gia' identificato
				if(requestInfo.getIdServizio().getSoggettoErogatore()!=null) {
					this.idServizio.getSoggettoErogatore().setCodicePorta(requestInfo.getIdServizio().getSoggettoErogatore().getCodicePorta());
				}
			}
			else{
				this.idServizio.setAzione(this.configurazionePdDReader.getAzione(this.portaDelegata, urlProtocolContext, requestInfo, this.requestMessage, null,
						this.headerIntegrazioneRichiesta, this.msgContext.getIdModulo().endsWith(IntegrationManager.ID_MODULO), this.protocolFactory));
			}

		} catch (IdentificazioneDinamicaException e) {
			
			boolean throwFault = true;
			if(StatoFunzionalita.ABILITATO.equals(cors.getStato()) && this.msgContext.isGestioneRisposta()) {
				throwFault = false;
			}
			if(throwFault) {
			
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.OPERAZIONE_NON_INDIVIDUATA, "true");
				
				this.msgDiag.addKeywordErroreProcessamento(e);
				this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IDENTIFICAZIONE_DINAMICA_AZIONE_NON_RIUSCITA);
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.OPERATION_UNDEFINED,
							ErroriIntegrazione.ERRORE_403_AZIONE_NON_IDENTIFICATA.getErroreIntegrazione(),e,null)));
				}
				return;
			}
			else {
				effettuareGestioneCORS = true;
			}
				
		} catch (Exception e) {
			this.msgDiag.addKeywordErroreProcessamento(e);
			this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IDENTIFICAZIONE_DINAMICA_AZIONE_NON_RIUSCITA);
			logError(this.logCore, this.msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_IDENTIFICAZIONE_DINAMICA_AZIONE_NON_RIUSCITA),e);
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		try {
			if(this.idServizio!=null && this.idServizio.getSoggettoErogatore()!=null &&
					this.idServizio.getSoggettoErogatore().getCodicePorta()==null) {
				this.idServizio.getSoggettoErogatore().setCodicePorta(this.registroServiziReader.getDominio(this.idServizio.getSoggettoErogatore(), null, this.protocolFactory, requestInfo));
			}
						
			// aggiorno idServizio
			richiestaDelegata.setIdServizio(this.idServizio);
			if(identificativoPortaDelegata.getIdentificativiFruizione()!=null) {
				identificativoPortaDelegata.getIdentificativiFruizione().setIdServizio(this.idServizio);
			}
		
			// aggiorno informazioni dell'header di integrazione della risposta
			this.headerIntegrazioneRisposta = new HeaderIntegrazione(idTransazione);
			if(this.soggettoFruitore!=null) {
				this.headerIntegrazioneRisposta.getBusta().setTipoMittente(this.soggettoFruitore.getTipo());
				this.headerIntegrazioneRisposta.getBusta().setMittente(this.soggettoFruitore.getNome());
			}
			this.headerIntegrazioneRisposta.getBusta().setTipoDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getTipo());
			this.headerIntegrazioneRisposta.getBusta().setDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getNome());
			this.headerIntegrazioneRisposta.getBusta().setTipoServizio(richiestaDelegata.getIdServizio().getTipo());
			this.headerIntegrazioneRisposta.getBusta().setServizio(richiestaDelegata.getIdServizio().getNome());
			this.headerIntegrazioneRisposta.getBusta().setVersioneServizio(richiestaDelegata.getIdServizio().getVersione());
			this.headerIntegrazioneRisposta.getBusta().setAzione(richiestaDelegata.getIdServizio().getAzione());
			if (this.headerIntegrazioneRichiesta!=null && this.headerIntegrazioneRichiesta.getBusta() != null
					&& this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null) {
				// per profilo asincrono asimmetrico e simmetrico
				this.headerIntegrazioneRisposta.getBusta().setRiferimentoMessaggio(this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio());
			}
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e, "configurazionePdDReader.letturaDatiServizioServizioNonRiuscita(pd)");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		this.msgDiag.setServizio(richiestaDelegata.getIdServizio());
		this.msgDiag.addKeywords(richiestaDelegata.getIdServizio());
		
		
		
		
		
		// Gestione CORS
		
		if(!effettuareGestioneCORS && StatoFunzionalita.ABILITATO.equals(cors.getStato()) &&
				richiestaDelegata!=null && richiestaDelegata.getIdServizio()!=null &&
				richiestaDelegata.getIdServizio().getAzione()==null) {
			if(ServiceBinding.REST.equals(requestInfo.getIntegrationServiceBinding())) {
				// in rest una risorsa deve essere riconsociuta
				if(StatoFunzionalita.ABILITATO.equals(cors.getStato()) && this.msgContext.isGestioneRisposta()) {
					effettuareGestioneCORS = true;
				}
			}
			else {
				boolean azioneErrata = false;
				Servizio infoServizio = null;
				try {
					infoServizio = this.registroServiziReader.getInfoServizio(this.soggettoFruitore, richiestaDelegata.getIdServizio(),null,false, true, requestInfo);
				} catch (DriverRegistroServiziAzioneNotFound e) {
					azioneErrata = true;
				} catch (Exception e) {
					// ignore
				}
				if (infoServizio == null) {
					try {
						infoServizio = this.registroServiziReader.getInfoServizioCorrelato(this.soggettoFruitore,richiestaDelegata.getIdServizio(),null, true, requestInfo);
					} catch (DriverRegistroServiziAzioneNotFound e) {
						azioneErrata = true;
					} catch (Exception e) {
						// ignore
					}
				}
				if(azioneErrata) {
					effettuareGestioneCORS = true;
				}
			}
		}
		
		boolean corsTrasparente = false;
				
		if(!effettuareGestioneCORS) {
			if(pddContext.containsKey(CostantiPdD.CORS_PREFLIGHT_REQUEST_SOAP)) {
				effettuareGestioneCORS = true;
			}
			else {
				// devo verificare se si tratta di una azione matched poichè è stato inserito un tipo http method 'qualsiasi'
				if(propertiesReader.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneContenutiApplicativi() &&
					cors!=null && 
					StatoFunzionalita.ABILITATO.equals(cors.getStato()) &&
					TipoGestioneCORS.GATEWAY.equals(cors.getTipo()) &&
					this.msgContext.isGestioneRisposta() &&
					this.idServizio!=null && this.idServizio.getAzione()!=null) {
					try {
						RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance();
						AccordoServizioParteSpecifica asps = registroServiziManager.getAccordoServizioParteSpecifica(this.idServizio, null, false, requestInfo);
						if(asps!=null) {
							AccordoServizioParteComune aspc = registroServiziManager.getAccordoServizioParteComune(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()), null, false, false, requestInfo);
							if(aspc!=null && org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(aspc.getServiceBinding()) &&
								aspc.sizeResourceList()>0) {
								for (Resource resource : aspc.getResourceList()) {
									if(this.idServizio.getAzione().equals(resource.getNome())) {
										if(resource.getMethod()==null){
											effettuareGestioneCORS = true;
										}
										break;
									}
								}
							}
						}
					}catch(Exception tIgnore) {
						// ignore
					}
				}
			}
		}
		
		if(effettuareGestioneCORS) {
			
			if(TipoGestioneCORS.GATEWAY.equals(cors.getTipo())) {
				
				CORSFilter corsFilter = new CORSFilter(this.logCore, cors);
				try {
					CORSWrappedHttpServletResponse res = new CORSWrappedHttpServletResponse(false);
					corsFilter.doCORS(httpServletRequest, res, CORSRequestType.PRE_FLIGHT, true);
					if(this.msgContext.getResponseHeaders()==null) {
						this.msgContext.setResponseHeaders(new HashMap<>());
					}
					this.msgContext.getResponseHeaders().putAll(res.getHeadersValues());
					this.msgContext.setMessageResponse(res.buildMessage());
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CORS_PREFLIGHT_REQUEST_VIA_GATEWAY, "true");
				}catch(Exception e) {
					// un eccezione non dovrebbe succedere
					this.msgDiag.logErroreGenerico(e, "gestioneCORS(pd)");
					this.openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
					}
					return;
				}
				
				this.openspcoopstate.releaseResource();
				return;
					
			}
			else {
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CORS_PREFLIGHT_REQUEST_TRASPARENTE, "true");
				corsTrasparente = true;
				
			}
			
		}
		
		
		
		
		

		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Autorizzazione canale ...");
		ConfigurazioneCanaliNodo configurazioneCanaliNodo = null;
		try {	
			configurazioneCanaliNodo = this.configurazionePdDReader.getConfigurazioneCanaliNodo(); 
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e, "configurazionePdDReader.getConfigurazioneCanaliNodo()");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		boolean canaleNonAutorizzato = false;
		try {
			if(configurazioneCanaliNodo!=null && configurazioneCanaliNodo.isEnabled()) {
			
				this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
				this.msgDiag.logPersonalizzato("autorizzazioneCanale.inCorso");
				
				String canaleApiInvocata = null;
				if(this.portaDelegata!=null) {
					String canalePorta = this.portaDelegata.getCanale();
					if(canalePorta!=null && !"".equals(canalePorta)) {
						canaleApiInvocata = canalePorta;
					}
					else {
						try {
							AccordoServizioParteSpecifica asps = this.registroServiziReader.getAccordoServizioParteSpecifica(this.idServizio, null, false, requestInfo);
							if(asps!=null) {
								AccordoServizioParteComune aspc = this.registroServiziReader.getAccordoServizioParteComune(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()), null, false, false, requestInfo);
								if(aspc!=null) {
									String canaleApi = aspc.getCanale();
									if(canaleApi!=null && !"".equals(canaleApi)) {
										canaleApiInvocata = canaleApi;
									}
								}
							}
						}catch(DriverRegistroServiziNotFound notFound) {
							// saranno segnalati altri errori dovuti al non riconoscimento del servizio
						}
					}
					
					if(canaleApiInvocata==null || "".equals(canaleApiInvocata)) {
						canaleApiInvocata = configurazioneCanaliNodo.getCanaleDefault();
					}
					
					if(!configurazioneCanaliNodo.getCanaliNodo().contains(canaleApiInvocata)) {
						canaleNonAutorizzato = true;
						String dettaglio=" (nodo '"+configurazioneCanaliNodo.getIdNodo()+"':"+configurazioneCanaliNodo.getCanaliNodo()+" api-invocata:"+canaleApiInvocata+")";
						this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, dettaglio);
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTORIZZAZIONE, "true");
						throw new CoreException("L'API invocata richiede un canale differente da quelli associati al nodo; invocazione non autorizzata");
					}
					else {
						this.msgDiag.logPersonalizzato("autorizzazioneCanale.effettuata");
					}
					
				}
				/**else {
					// saranno segnalati altri errori dovuti al non riconoscimento della porta
				}*/
				
			}
		} catch (Exception e) {
			
			String msgErrore = e.getMessage();
			
			this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
			this.msgDiag.logPersonalizzato("autorizzazioneCanale.fallita");
			
			ErroreIntegrazione errore = canaleNonAutorizzato ? ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA_ANONIMO.getErrore404_AutorizzazioneFallitaServizioApplicativoAnonimo(msgErrore) : 
				ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
			IntegrationFunctionError integrationFunctionError = canaleNonAutorizzato ? IntegrationFunctionError.AUTHORIZATION_DENY : IntegrationFunctionError.INTERNAL_REQUEST_ERROR;

			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError, errore, e,null)));
			}
			return;
		}
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Identificazione PD specifica per azione del servizio ...");
		
		this.msgDiag.mediumDebug("Lettura azione associato alla PD invocata...");
		if(richiestaDelegata.getIdServizio().getAzione()!=null && this.portaDelegata!=null) {
			// verifico se esiste una porta delegata piu' specifica
			IdentificazionePortaDelegata identificazione = new IdentificazionePortaDelegata(this.logCore, this.protocolFactory, 
					this.registroServiziReader, this.configurazionePdDReader, requestInfo,
					this.portaDelegata);
			String action = richiestaDelegata.getIdServizio().getAzione();
			if(identificazione.find(action)) {
				IDPortaDelegata idPDAction = identificazione.getIDPortaDelegata(action);
				if(idPDAction!=null) {
					
					this.requestMessage.addContextProperty(CostantiPdD.NOME_PORTA_INVOCATA, this.portaDelegata.getNome()); // prima di aggiornare la porta delegata
					
					identificativoPortaDelegata = idPDAction;
					this.portaDelegata = identificazione.getPortaDelegata(action);
					nomeUtilizzatoPerErrore = "Configurazione specifica per l'azione '"+action+"', porta '"+ identificativoPortaDelegata.getNome()+"'";
										
					// aggiornao dati che possiede la Porta Delegata ID Porta Delegata
					this.msgContext.getIntegrazione().setIdPD(identificativoPortaDelegata);
					this.msgDiag.addKeyword(CostantiPdD.KEY_PORTA_DELEGATA, identificativoPortaDelegata.getNome());
					this.msgDiag.updatePorta(identificativoPortaDelegata.getNome(), requestInfo);
					richiestaDelegata.setIdPortaDelegata(identificativoPortaDelegata);
					if(this.requestMessage.getTransportRequestContext()!=null) {
						this.requestMessage.getTransportRequestContext().setInterfaceName(identificativoPortaDelegata.getNome());
					}
					
					pddContext.removeObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_CONFIGURAZIONE);
					try {
						Map<String, String> configProperties = this.configurazionePdDReader.getProprietaConfigurazione(this.portaDelegata);
			            if (configProperties != null && !configProperties.isEmpty()) {
			               pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_CONFIGURAZIONE, configProperties);
			            }
					}catch(Exception e) {
						logError(this.logCore, "Errore durante la lettura delle proprietà di configurazione della porta delegata [" + this.portaDelegata.getNome() + "]: " + e.getMessage(), e);
					}
				}
			}else {
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.API_NON_INDIVIDUATA, "true");
				this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, identificazione.getErroreIntegrazione().getDescrizione(this.protocolFactory));
				this.msgDiag.logPersonalizzato("portaDelegataNonEsistente");
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					IntegrationFunctionError integrationFunctionError = null;
					if(CodiceErroreIntegrazione.CODICE_401_PORTA_INESISTENTE.equals(identificazione.getErroreIntegrazione().getCodiceErrore())){
						integrationFunctionError = IntegrationFunctionError.API_OUT_UNKNOWN;
					}else{
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError, identificazione.getErroreIntegrazione(),null,null)));
				}
				return;
			}
		}
		
		
		
		


		
		
		
		
		// ------------- Informazioni Integrazione -----------------------------
		
		this.msgDiag.mediumDebug("Aggiungo informazioni di integrazione dinamica nel contesto ...");
				
		try {
			if(this.portaDelegata!=null) {
				this.configurazionePdDReader.setInformazioniIntegrazioneDinamiche(this.logCore, urlProtocolContext, pddContext, this.portaDelegata);
			}
		} 
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e, "setInformazioniIntegrazioneDinamiche");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.BAD_REQUEST,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		
		
		
		
		
		
		
		// ------------- Dump richiesta-----------------------------
		
		this.msgDiag.mediumDebug("Dump richiesta ...");
		
		DumpConfigurazione dumpConfig = null;
		try {
			dumpConfig = this.configurazionePdDReader.getDumpConfigurazione(this.portaDelegata);
			internalObjects.put(CostantiPdD.DUMP_CONFIG, dumpConfig);
		} 
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e, "readDumpConfigurazione");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		
		Dump dumpApplicativo = new Dump(this.identitaPdD,
				this.msgContext.getIdModulo(), null,
				this.soggettoFruitore, richiestaDelegata.getIdServizio(),
				this.msgContext.getTipoPorta(), this.msgDiag.getPorta(), this.inRequestContext.getPddContext(),
				this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta(),
				dumpConfig);
		dumpApplicativo.dumpRichiestaIngresso(this.requestMessage,this.inRequestContext.getConnettore().getUrlProtocolContext());
		internalObjects.put(CostantiPdD.DUMP_RICHIESTA_EFFETTUATO, true);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Autenticazione ...");
		
		

		/* --------------- Gestione credenziali --------------- */
		if(RicezioneContenutiApplicativi.tipiGestoriCredenziali!=null){
			this.msgDiag.mediumDebug("Gestione personalizzata delle credenziali...");
			
			for (int i = 0; i < RicezioneContenutiApplicativi.tipiGestoriCredenziali.length; i++) {
				try {
								
					IGestoreCredenziali gestore = null;
					String classType = null;
					try {
						classType = className.getGestoreCredenziali(RicezioneContenutiApplicativi.tipiGestoriCredenziali[i]);
						gestore = (IGestoreCredenziali)loader.newInstance(classType);
						AbstractCore.init(gestore, pddContext, this.protocolFactory);
					} catch (Exception e) {
						throw new CoreException(
								"Riscontrato errore durante il caricamento della classe (IGestoreCredenziali) ["+ classType
								+ "] da utilizzare per la gestione delle credenziali di tipo ["
								+ RicezioneContenutiApplicativi.tipiGestoriCredenziali[i]+ "]: " + e.getMessage());
					}
					
					if (gestore != null) {
						Credenziali credenzialiRitornate = gestore.elaborazioneCredenziali(this.identitaPdD, this.inRequestContext.getConnettore(), this.requestMessage);
						if(credenzialiRitornate==null){
							throw new CoreException("Credenziali non ritornate");
						}
						if(!this.inRequestContext.getConnettore().getCredenziali().equals(credenzialiRitornate)){
							String nuoveCredenziali = credenzialiRitornate.toString();
							if(nuoveCredenziali.length()>0) {
								nuoveCredenziali = nuoveCredenziali.substring(0,(nuoveCredenziali.length()-1));
							}
							this.msgDiag.addKeyword(CostantiPdD.KEY_NUOVE_CREDENZIALI,nuoveCredenziali);
							String identita = gestore.getIdentitaGestoreCredenziali();
							if(identita==null){
								identita = "Gestore delle credenziali di tipo "+RicezioneContenutiApplicativi.tipiGestoriCredenziali[i];
							}
							this.msgDiag.addKeyword(CostantiPdD.KEY_IDENTITA_GESTORE_CREDENZIALI, identita);
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.IDENTITA_GESTORE_CREDENZIALI, identita);
							this.msgDiag.logPersonalizzato("gestoreCredenziali.nuoveCredenziali");
							// update credenziali
							this.inRequestContext.getConnettore().setCredenziali(credenzialiRitornate);
							credenziali = credenzialiRitornate;
							setCredenziali(credenziali, this.msgDiag);	
						}
					} else {
						throw new CoreException("non inizializzato");
					}
				} 
				catch (Exception e) {
					logError(this.logCore, "Errore durante l'identificazione delle credenziali ["+ RicezioneContenutiApplicativi.tipiGestoriCredenziali[i]
					         + "]: "+ e.getMessage(),e);
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_GESTORE_CREDENZIALI,RicezioneContenutiApplicativi.tipiGestoriCredenziali[i]);
					this.msgDiag.addKeywordErroreProcessamento(e);
					this.msgDiag.logPersonalizzato("gestoreCredenziali.errore");
					ErroreIntegrazione errore = null;
					IntegrationFunctionError integrationFunctionError = null;
					String wwwAuthenticateErrorHeader = null;
					if(e instanceof GestoreCredenzialiConfigurationException ge){
						errore = ErroriIntegrazione.ERRORE_431_GESTORE_CREDENZIALI_ERROR.
								getErrore431_ErroreGestoreCredenziali(RicezioneContenutiApplicativi.tipiGestoriCredenziali[i], e);
						integrationFunctionError = ge.getIntegrationFunctionError();
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE, "true");
						wwwAuthenticateErrorHeader = ge.getWwwAuthenticateErrorHeader();
					}else{
						errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_548_GESTORE_CREDENZIALI_NON_FUNZIONANTE);
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					this.openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						OpenSPCoop2Message errorMsg = this.generatoreErrore.build(pddContext,integrationFunctionError, errore,e,null);
						if(wwwAuthenticateErrorHeader!=null) {
							errorMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
						}
						this.msgContext.setMessageResponse(errorMsg);
					}
					return;
				}
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/* ------------ GestioneToken ------------- */
	
		RicezioneContenutiApplicativiGestioneToken gestioneToken = new RicezioneContenutiApplicativiGestioneToken(this.msgDiag, this.logCore,
				this.portaDelegata, identificativoPortaDelegata,
				this.requestMessage,
				this.msgContext, this.generatoreErrore, this.inRequestContext,
				this.configurazionePdDReader,
				pddContext, idTransazione,
				this.openspcoopstate, transaction, requestInfo,
				this.protocolFactory,
				this.identitaPdD);
	
		GestioneTokenAutenticazione gestioneTokenAutenticazione = null;
		String token = null;
		if(!gestioneToken.process()) {
			return;
		}
		gestioneTokenAutenticazione = gestioneToken.getGestioneTokenAutenticazione();
		token = gestioneToken.getToken();
		
		
		
		
		
		
		
		
		
		
		/* ------------ Autenticazione ------------- */
		
		RicezioneContenutiApplicativiGestioneAutenticazione gestioneAutenticazione = new RicezioneContenutiApplicativiGestioneAutenticazione(this.msgDiag, this.logCore,
				this.portaDelegata, identificativoPortaDelegata,
				this.soggettoFruitore, credenziali, gestioneTokenAutenticazione, richiestaDelegata, proprietaErroreAppl,
				this.requestMessage,
				this.msgContext, this.generatoreErrore, this.inRequestContext, this.headerIntegrazioneRichiesta,
				this.configurazionePdDReader, this.registroServiziReader,
				pddContext, idTransazione, this.identitaPdD,
				this.openspcoopstate, transaction, requestInfo,
				this.protocolFactory);
		
		IDServizioApplicativo idApplicativoToken = null;
		ServizioApplicativo sa = null;
		this.servizioApplicativo = null;
		InformazioniToken informazioniTokenNormalizzate = null;
		
		if(!gestioneAutenticazione.process()) {
			return;
		}
		
		idApplicativoToken = gestioneAutenticazione.getIdApplicativoToken();
		sa = gestioneAutenticazione.getSa();
		this.servizioApplicativo = gestioneAutenticazione.getServizioApplicativo();
		informazioniTokenNormalizzate = gestioneAutenticazione.getInformazioniTokenNormalizzate();
		
		
		
		

		
		
		
		/* --------------- Verifica tipo soggetto fruitore e tipo servizio rispetto al canale utilizzato --------------- */
		this.msgDiag.mediumDebug("Verifica canale utilizzato...");
		List<String> tipiSoggettiSupportatiCanale = this.protocolFactory.createProtocolConfiguration().getTipiSoggetti();
		List<String> tipiServiziSupportatiCanale = this.protocolFactory.createProtocolConfiguration().getTipiServizi(this.requestMessage.getServiceBinding());
		// Nota: se qualche informazione e' null verranno segnalati altri errori
		if(this.soggettoFruitore!=null && this.soggettoFruitore.getTipo()!=null && 
				!tipiSoggettiSupportatiCanale.contains(this.soggettoFruitore.getTipo())){
			this.msgDiag.logPersonalizzato("protocolli.tipoSoggetto.fruitore.unsupported");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL,
						ErroriIntegrazione.ERRORE_436_TIPO_SOGGETTO_FRUITORE_NOT_SUPPORTED_BY_PROTOCOL.
						getErrore436_TipoSoggettoFruitoreNotSupportedByProtocol(this.soggettoFruitore,this.protocolFactory),null,null)));
			}
			return;
		}
		if(richiestaDelegata!=null && richiestaDelegata.getIdServizio()!=null &&
				richiestaDelegata.getIdServizio().getSoggettoErogatore()!=null && 
				richiestaDelegata.getIdServizio().getSoggettoErogatore().getTipo()!=null &&
				!tipiSoggettiSupportatiCanale.contains(richiestaDelegata.getIdServizio().getSoggettoErogatore().getTipo())){
			this.msgDiag.logPersonalizzato("protocolli.tipoSoggetto.erogatore.unsupported");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL,
						ErroriIntegrazione.ERRORE_437_TIPO_SOGGETTO_EROGATORE_NOT_SUPPORTED_BY_PROTOCOL.
						getErrore437_TipoSoggettoErogatoreNotSupportedByProtocol(richiestaDelegata.getIdServizio().getSoggettoErogatore(),this.protocolFactory),null,null)));
			}
			return;
		}
		if(richiestaDelegata!=null && richiestaDelegata.getIdServizio()!=null &&
				richiestaDelegata.getIdServizio().getTipo()!=null && 
				!tipiServiziSupportatiCanale.contains(richiestaDelegata.getIdServizio().getTipo())){
			this.msgDiag.logPersonalizzato("protocolli.tipoServizio.unsupported");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL,
						ErroriIntegrazione.ERRORE_438_TIPO_SERVIZIO_NOT_SUPPORTED_BY_PROTOCOL.
						getErrore438_TipoServizioNotSupportedByProtocol(this.requestMessage.getServiceBinding(),
								richiestaDelegata.getIdServizio(),this.protocolFactory),null,null)));
			}
			return;
		}
		if(idApplicativoToken!=null && idApplicativoToken.getIdSoggettoProprietario()!=null &&
				idApplicativoToken.getIdSoggettoProprietario().getTipo()!=null && 
				!tipiSoggettiSupportatiCanale.contains(idApplicativoToken.getIdSoggettoProprietario().getTipo())){
			this.msgDiag.logPersonalizzato("protocolli.tipoSoggetto.applicativoToken.unsupported");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL,
						ErroriIntegrazione.ERRORE_449_TIPO_SOGGETTO_APPLICATIVO_TOKEN_NOT_SUPPORTED_BY_PROTOCOL.
							getErrore449_TipoSoggettoApplicativoTokenNotSupportedByProtocol(idApplicativoToken,this.protocolFactory),null,null)));
			}
			return;
		}
		
		
		
		
		
		
		
		/*
		 * ---------------- Verifico che il servizio di RicezioneContenutiApplicativi sia abilitato ---------------------
		 */
		boolean serviceIsEnabled = false;
		boolean portaEnabled = false;
		Exception serviceIsEnabledExceptionProcessamento = null;
		try{
			serviceIsEnabled = StatoServiziPdD.isEnabledPortaDelegata(this.soggettoFruitore, richiestaDelegata.getIdServizio());
			if(serviceIsEnabled){
				portaEnabled = this.configurazionePdDReader.isPortaAbilitata(this.portaDelegata);
			}
		}catch(Exception e){
			serviceIsEnabledExceptionProcessamento = e;
		}
		if (!serviceIsEnabled || !portaEnabled  || serviceIsEnabledExceptionProcessamento!=null) {
			ErroreIntegrazione errore = null;
			IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.API_SUSPEND;
			if(serviceIsEnabledExceptionProcessamento!=null){
				logError(this.logCore, "["+ RicezioneContenutiApplicativi.ID_MODULO+ "] Identificazione stato servizio di ricezione contenuti applicativi non riuscita: "+serviceIsEnabledExceptionProcessamento.getMessage(),serviceIsEnabledExceptionProcessamento);
				this.msgDiag.logErroreGenerico("Identificazione stato servizio di ricezione contenuti applicativi non riuscita", "PD");
				errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione();
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			}else{
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_SOSPENSIONE, "true");
								
				String msg = "Servizio di ricezione contenuti applicativi disabilitato";
				if(serviceIsEnabled){
					msg = "Porta Delegata ["+nomeUtilizzatoPerErrore+"] disabilitata";
					errore = ErroriIntegrazione.ERRORE_446_PORTA_SOSPESA.getErroreIntegrazione();
				}
				else {
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_550_PD_SERVICE_NOT_ACTIVE);
				}
				logError(this.logCore, "["+ RicezioneContenutiApplicativi.ID_MODULO+ "] "+msg);
				this.msgDiag.logErroreGenerico(msg, "PD");
			}
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				OpenSPCoop2Message errorOpenSPCoopMsg = this.generatoreErrore.build(pddContext,integrationFunctionError,
						errore,serviceIsEnabledExceptionProcessamento,null);
				if(IntegrationFunctionError.API_SUSPEND.equals(integrationFunctionError) &&
						propertiesReader.isEnabledServiceUnavailableRetryAfterPDSuspend() && 
						propertiesReader.getServiceUnavailableRetryAfterSecondsPDSuspend()!=null &&
						propertiesReader.getServiceUnavailableRetryAfterSecondsPDSuspend()>0) {
					int seconds = propertiesReader.getServiceUnavailableRetryAfterSecondsPDSuspend();
					if(propertiesReader.getServiceUnavailableRetryAfterSecondsRandomBackoffPDSuspend()!=null &&
							propertiesReader.getServiceUnavailableRetryAfterSecondsRandomBackoffPDSuspend()>0) {
						seconds = seconds + ServicesUtils.getRandom().nextInt(propertiesReader.getServiceUnavailableRetryAfterSecondsRandomBackoffPDSuspend());
					}
					errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.RETRY_AFTER, seconds+"");
				}
				this.msgContext.setMessageResponse(errorOpenSPCoopMsg);
			}
			return;
		}
		
		
		
		
		
		
		
		
		
		
		/* ------------ Gestione Attribute Authority ------------- */
		
		this.msgDiag.mediumDebug("Gestione Attribute Authority...");
		List<AttributeAuthority> attributeAuthorities = null;
		if(this.portaDelegata!=null) {
			attributeAuthorities = this.portaDelegata.getAttributeAuthorityList();
		}
		this.msgContext.getIntegrazione().setAttributeAuthoritiesFromObjectList(attributeAuthorities);
		
		if (attributeAuthorities == null || attributeAuthorities.isEmpty()) {

			this.msgDiag.logPersonalizzato("gestioneAADisabilitata");
			
		} else {

			transaction.getTempiElaborazione().startAttributeAuthority();
			
			try {
				this.msgDiag.logPersonalizzato("gestioneAAInCorso");
				
				org.openspcoop2.pdd.core.token.attribute_authority.pd.DatiInvocazionePortaDelegata datiInvocazione = new org.openspcoop2.pdd.core.token.attribute_authority.pd.DatiInvocazionePortaDelegata();
				datiInvocazione.setInfoConnettoreIngresso(this.inRequestContext.getConnettore());
				datiInvocazione.setState(this.openspcoopstate.getStatoRichiesta());
				datiInvocazione.setIdModulo(this.inRequestContext.getIdModulo());
				datiInvocazione.setMessage(this.requestMessage);
				Busta busta = new Busta(this.protocolFactory.getProtocol());
				if(this.soggettoFruitore!=null) {
					busta.setTipoMittente(this.soggettoFruitore.getTipo());
					busta.setMittente(this.soggettoFruitore.getNome());
				}
				if(richiestaDelegata!=null && richiestaDelegata.getIdServizio()!=null) {
					if(richiestaDelegata.getIdServizio().getSoggettoErogatore()!=null) {
						busta.setTipoDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getTipo());
						busta.setDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getNome());
					}
					busta.setTipoServizio(richiestaDelegata.getIdServizio().getTipo());
					busta.setServizio(richiestaDelegata.getIdServizio().getNome());
					busta.setVersioneServizio(richiestaDelegata.getIdServizio().getVersione());
					busta.setAzione(richiestaDelegata.getIdServizio().getAzione());
					if(sa!=null) {
						busta.setServizioApplicativoFruitore(sa.getNome());
					}
				}
				datiInvocazione.setBusta(busta);
				datiInvocazione.setRequestInfo(requestInfo);
				datiInvocazione.setIdPD(identificativoPortaDelegata);
				datiInvocazione.setPd(this.portaDelegata);		
				
				GestioneAttributeAuthority gestioneAAEngine = new GestioneAttributeAuthority(this.logCore, idTransazione, pddContext, this.protocolFactory);
				List<InformazioniAttributi> esitiValidiRecuperoAttributi = new ArrayList<>();
				
				for (AttributeAuthority aa : attributeAuthorities) {
					
					try {
						this.msgDiag.addKeyword(CostantiPdD.KEY_ATTRIBUTE_AUTHORITY_NAME, aa.getNome());
						this.msgDiag.addKeyword(CostantiPdD.KEY_ATTRIBUTE_AUTHORITY_ENDPOINT, "-");
						
						PolicyAttributeAuthority policyAttributeAuthority = this.configurazionePdDReader.getPolicyAttributeAuthority(false, aa.getNome(), requestInfo);
						datiInvocazione.setPolicyAttributeAuthority(policyAttributeAuthority);
				
						GestoreToken.validazioneConfigurazione(policyAttributeAuthority); // assicura che la configurazione sia corretta
						
						this.msgDiag.addKeyword(CostantiPdD.KEY_ATTRIBUTE_AUTHORITY_ENDPOINT, policyAttributeAuthority.getEndpoint());
						
						this.msgDiag.logPersonalizzato("gestioneAAInCorso.retrieve");
						
						EsitoRecuperoAttributi esitoRecuperoAttributi = gestioneAAEngine.readAttributes(datiInvocazione);
						if(esitoRecuperoAttributi.isValido()) {
							
							StringBuilder attributiRecuperati = new StringBuilder();
							if(esitoRecuperoAttributi.getInformazioniAttributi()!=null && 
									esitoRecuperoAttributi.getInformazioniAttributi().getAttributes()!=null &&
									!esitoRecuperoAttributi.getInformazioniAttributi().getAttributes().isEmpty()) {
								for (String attrName : esitoRecuperoAttributi.getInformazioniAttributi().getAttributesNames()) {
									if(attributiRecuperati.length()>0) {
										attributiRecuperati.append(",");
									}
									attributiRecuperati.append(attrName);
								}
							}
							this.msgDiag.addKeyword(CostantiPdD.KEY_ATTRIBUTES, attributiRecuperati.toString());
							
							if(esitoRecuperoAttributi.isInCache()) {
								this.msgDiag.logPersonalizzato("gestioneAAInCorso.retrieve.completataSuccesso.inCache");
							}
							else {
								this.msgDiag.logPersonalizzato("gestioneAAInCorso.retrieve.completataSuccesso");
							}
							
							esitiValidiRecuperoAttributi.add(esitoRecuperoAttributi.getInformazioniAttributi());
						}
						else {
							
							this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, esitoRecuperoAttributi.getDetails());
							this.msgDiag.logPersonalizzato("gestioneAAInCorso.retrieve.fallita");
		
							String msgErrore = "processo di gestione dell'attribute authority ["+ aa.getNome() + "] fallito: " + esitoRecuperoAttributi.getDetails();
							if(esitoRecuperoAttributi.getEccezioneProcessamento()!=null) {
								logError(this.logCore, msgErrore,esitoRecuperoAttributi.getEccezioneProcessamento());
							}
							else {
								logError(this.logCore, msgErrore);
							}							
						}
						
					} catch (Throwable e) {
						
						this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
						this.msgDiag.logPersonalizzato("gestioneAAInCorso.retrieve.fallita");
						String msgErrore = "processo di gestione dell'attribute authority ["+ aa.getNome() + "] fallito: " + e.getMessage();
						logError(this.logCore, msgErrore,e);
						
					}
				}
				
				InformazioniAttributi informazioniAttributiNormalizzati = null;
				if(esitiValidiRecuperoAttributi!=null && !esitiValidiRecuperoAttributi.isEmpty()) {
					informazioniAttributiNormalizzati = GestoreToken.normalizeInformazioniAttributi(esitiValidiRecuperoAttributi, attributeAuthorities);
					informazioniAttributiNormalizzati.setValid(true);
				}
				if(informazioniAttributiNormalizzati!=null) {
					pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_ATTRIBUTI_INFORMAZIONI_NORMALIZZATE, informazioniAttributiNormalizzati);
					
					if(informazioniTokenNormalizzate!=null) {
						informazioniTokenNormalizzate.setAa(informazioniAttributiNormalizzati);
					}
					else {
						transaction.setInformazioniAttributi(informazioniAttributiNormalizzati);
					}
				}
				
				this.msgDiag.logPersonalizzato("gestioneAACompletata");
				
			} catch (Throwable e) {
				
				this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
				this.msgDiag.logPersonalizzato("gestioneAAFallita");
				logError(this.logCore, "processo di gestione delle attribute authorities fallito: " + e.getMessage(),e);
				
			}
			finally {
				transaction.getTempiElaborazione().endAttributeAuthority();
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Inizializzazione connessione al database...");
		
		
		
		/*
		 * ---------------- Inizializzazione Contesto di gestione della Richiesta ---------------------
		 */
		this.idMessageRequest = null;
		
		// Correlazione Applicativa
		this.msgDiag.mediumDebug("Gestione correlazione applicativa...");
		CorrelazioneApplicativa correlazionePD = null;
		try {
			correlazionePD = this.configurazionePdDReader.getCorrelazioneApplicativa(this.portaDelegata);
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"getCorrelazioneApplicativa(pd)");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		
		Busta busta = null;
		try {
			busta = new Busta(this.protocolFactory.getProtocol());
			if(this.soggettoFruitore!=null) {
				busta.setTipoMittente(this.soggettoFruitore.getTipo());
				busta.setMittente(this.soggettoFruitore.getNome());
			}
			if(richiestaDelegata!=null && richiestaDelegata.getIdServizio()!=null) {
				if(richiestaDelegata.getIdServizio().getSoggettoErogatore()!=null) {
					busta.setTipoDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getTipo());
					busta.setDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getNome());
				}
				busta.setTipoServizio(richiestaDelegata.getIdServizio().getTipo());
				busta.setServizio(richiestaDelegata.getIdServizio().getNome());
				busta.setVersioneServizio(richiestaDelegata.getIdServizio().getVersione());
				busta.setAzione(richiestaDelegata.getIdServizio().getAzione());
				if(sa!=null) {
					busta.setServizioApplicativoFruitore(sa.getNome());
				}
			}
		}catch(Exception t) {
			// ignore
		}
		
		GestoreCorrelazioneApplicativaConfig caConfig = new GestoreCorrelazioneApplicativaConfig();
		caConfig.setState(this.openspcoopstate.getStatoRichiesta());
		caConfig.setAlog(this.logCore);
		caConfig.setSoggettoFruitore(richiestaDelegata.getIdSoggettoFruitore());
		caConfig.setIdServizio(richiestaDelegata.getIdServizio());
		caConfig.setBusta(busta);
		caConfig.setServizioApplicativo(this.servizioApplicativo);
		caConfig.setProtocolFactory(this.protocolFactory);
		caConfig.setTransaction(transaction);
		caConfig.setPddContext(pddContext);
		caConfig.setPd(this.portaDelegata);
		GestoreCorrelazioneApplicativa correlazioneApplicativa = new GestoreCorrelazioneApplicativa(caConfig);
		
		boolean correlazioneEsistente = false;
		String idCorrelazioneApplicativa = null;
		if (correlazionePD != null) {
			try {
				correlazioneApplicativa
						.verificaCorrelazione(correlazionePD, urlProtocolContext,this.requestMessage,this.headerIntegrazioneRichiesta, 
								this.msgContext.getIdModulo().endsWith(IntegrationManager.ID_MODULO));
				idCorrelazioneApplicativa = correlazioneApplicativa.getIdCorrelazione();
				
				if(correlazioneApplicativa.isRiusoIdentificativo()) {
					
					if(this.openspcoopstate.resourceReleased()) {
						// inizializzo
						this.openspcoopstate.setUseConnection(true);
						this.openspcoopstate.initResource(this.identitaPdD, this.msgContext.getIdModulo(), idTransazione);
						correlazioneApplicativa.updateState(this.openspcoopstate.getStatoRichiesta());
					}
					
					correlazioneEsistente = correlazioneApplicativa.verificaCorrelazioneIdentificativoRichiesta();
				}
				
				if (correlazioneEsistente) {
					// Aggiornamento id richiesta
					this.idMessageRequest = correlazioneApplicativa.getIdBustaCorrelato();

					// Aggiornamento Informazioni Protocollo
					this.msgDiag.setIdMessaggioRichiesta(this.idMessageRequest);
					
					// MsgDiagnostico
					this.msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA, idCorrelazioneApplicativa);
					this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, this.idMessageRequest);
					this.msgDiag.logPersonalizzato("correlazioneApplicativaEsistente");
				}
			} catch (Exception e) {
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA, "true");
				
				this.msgDiag.logErroreGenerico(e,"CorrelazioneApplicativa");
				logError(this.logCore, "Riscontrato errore durante il controllo di correlazione applicativa: "+ e.getMessage(),e);
				
				ErroreIntegrazione errore = null;
				if(correlazioneApplicativa!=null){
					errore = correlazioneApplicativa.getErrore();
				}
				if(errore==null){
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA);
				}

				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					IntegrationFunctionError integrationFunctionError = null;
					if(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.equals(errore.getCodiceErrore())){
						integrationFunctionError = IntegrationFunctionError.APPLICATION_CORRELATION_IDENTIFICATION_REQUEST_FAILED;
					}
					else{
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError,errore,e,null)));
				}
				return;
			}
		}

		if (!correlazioneEsistente) {

			// Costruzione ID.
			this.msgDiag.mediumDebug("Costruzione identificativo...");
			try {
				
				Imbustamento imbustatore = new Imbustamento(this.logCore, this.protocolFactory,this.openspcoopstate.getStatoRichiesta());
				this.idMessageRequest = 
					imbustatore.buildID(this.identitaPdD, 
							(String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE), 
							propertiesReader.getGestioneSerializableDBAttesaAttiva(),
							propertiesReader.getGestioneSerializableDBCheckInterval(),
							RuoloMessaggio.RICHIESTA);
				if (this.idMessageRequest == null) {
					throw new CoreException("Identificativo non costruito.");
				}
				// Aggiornamento Informazioni Protocollo
				this.msgDiag.setIdMessaggioRichiesta(this.idMessageRequest);
				
			} catch (Exception e) {
				this.msgDiag.logErroreGenerico(e,"imbustatore.buildID(idMessageRequest)");
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_507_COSTRUZIONE_IDENTIFICATIVO), e,null)));
				}
				return;
			}
			this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, this.idMessageRequest);
			
			// Creazione Correlazione Applicativa
			if (correlazionePD!=null && idCorrelazioneApplicativa!=null && correlazioneApplicativa.isRiusoIdentificativo()) {
				this.msgDiag.mediumDebug("Applicazione correlazione applicativa...");
				try {
					// Applica correlazione
					correlazioneApplicativa.applicaCorrelazione(correlazionePD,idCorrelazioneApplicativa, this.idMessageRequest);
												
					// MsgDiagnostico
					this.msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA, idCorrelazioneApplicativa);
					this.msgDiag.logPersonalizzato("correlazioneApplicativaInstaurata");
				} catch (Exception e) {
					this.msgDiag.logErroreGenerico(e,"CreazioneCorrelazioneApplicativa");
					
					ErroreIntegrazione errore = null;
					if(correlazioneApplicativa!=null){
						errore = correlazioneApplicativa.getErrore();
					}
					if(errore==null){
						errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA);
					}
					
					this.openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						IntegrationFunctionError integrationFunctionError = null;
						if(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.equals(errore.getCodiceErrore())){
							integrationFunctionError = IntegrationFunctionError.APPLICATION_CORRELATION_IDENTIFICATION_REQUEST_FAILED;
						}
						else{
							integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
						}
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError,errore,null,null)));
					}
					return;
				}
			}
		}
		richiestaDelegata.setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
		this.msgDiag.setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
		this.msgContext.getIntegrazione().setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
		this.msgContext.setIdMessage(this.idMessageRequest);
		this.msgContext.getProtocol().setIdRichiesta(this.idMessageRequest);
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Gestione header integrazione della risposta...");
		
		
		/*
		 * ------- Imposta Risposta dell'Header Trasporto o se l'invocazione e' stata attiva dall'IntegrationManager -------
		 */
		this.msgDiag.mediumDebug("Gestione header integrazione della risposta...");
		this.headerIntegrazioneRisposta.getBusta().setID(this.idMessageRequest);
		boolean containsHeaderIntegrazioneTrasporto = false;
		for (String gestore : defaultGestoriIntegrazionePD) {
			if(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO.equals(gestore)){
				containsHeaderIntegrazioneTrasporto = true;
			}
		}
		if (containsHeaderIntegrazioneTrasporto
				|| this.msgContext.getIdModulo().startsWith(RicezioneContenutiApplicativi.ID_MODULO+ IntegrationManager.ID_MODULO)) {
			try {
				Map<String, List<String>> propertiesIntegrazioneRisposta = new HashMap<>();

				IGestoreIntegrazionePD gestore = pluginLoader.newIntegrazionePortaDelegata(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO);
				if(gestore!=null){
					String classType = null;
					try {
						classType = gestore.getClass().getName();
						AbstractCore.init(gestore, pddContext, this.protocolFactory);
					} catch (Exception e) {
						throw new CoreException(
								"Riscontrato errore durante l'inizializzazione della classe (IGestoreIntegrazionePD) ["+ classType
										+ "] da utilizzare per la gestione dell'integrazione delle fruizioni di tipo ["+ CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO + "]: " + e.getMessage());
					}

					OutResponsePDMessage outResponsePDMessage = new OutResponsePDMessage();
					outResponsePDMessage.setPortaDelegata(this.portaDelegata);
					outResponsePDMessage.setHeaders(propertiesIntegrazioneRisposta);
					outResponsePDMessage.setServizio(richiestaDelegata.getIdServizio());
					outResponsePDMessage.setSoggettoMittente(this.soggettoFruitore);
					gestore.setOutResponseHeader(this.headerIntegrazioneRisposta, outResponsePDMessage);
					this.msgContext.setResponseHeaders(propertiesIntegrazioneRisposta);
				}
			} catch (Exception e) {
				this.msgDiag.logErroreGenerico(e,"setHeaderIntegrazioneRisposta");
			}
		}

		
		
		this.finalEngine(transaction, sa, identificativoPortaDelegata, richiestaDelegata, 
				idCorrelazioneApplicativa, inRequestPDMessage, token, corsTrasparente, nomeUtilizzatoPerErrore,
				credenziali);
		

		}finally{ // try vedi  #try-finally-openspcoopstate#
			try{
				if(this.openspcoopstate!=null){
					this.openspcoopstate.forceFinallyReleaseResource();
				}
			}catch(Throwable e){
				if(this.msgDiag!=null){
					try{
						this.msgDiag.logErroreGenerico(e, "Rilascio risorsa");
					}catch(Throwable eLog){
						this.logCore.error("Diagnostico errore per Rilascio risorsa: "+eLog.getMessage(),eLog);
					}
				}
				else{
					this.logCore.error("Rilascio risorsa: "+e.getMessage(),e);
				}
			}
		}
	}
	
	private void finalEngine(Transaction transaction, ServizioApplicativo sa, IDPortaDelegata identificativoPortaDelegata, RichiestaDelegata richiestaDelegata, 
			String idCorrelazioneApplicativa, InRequestPDMessage inRequestPDMessage, String token, boolean corsTrasparente, String nomeUtilizzatoPerErrore,
			Credenziali credenziali) throws ProtocolException {
		
		PdDContext pddContext = this.inRequestContext.getPddContext();
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
		ITraduttore traduttore = this.protocolFactory.createTraduttore();
		IProtocolConfiguration protocolConfig = this.protocolFactory.createProtocolConfiguration();
		
		// Data Ingresso Richiesta
		Date dataIngressoRichiesta = this.msgContext.getDataIngressoRichiesta();
				
		// ID Transazione
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, this.inRequestContext.getPddContext());
		
		// RequestInfo
		RequestInfo requestInfo = this.msgContext.getRequestInfo();
		
		// Autenticazione/Autorizzazione registrate
		ClassNameProperties className = ClassNameProperties.getInstance();
		
		// Loader classi dinamiche
		Loader loader = Loader.getInstance();
		PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
		
		/*
		  * --------- Informazioni protocollo ----------
		 */
		IBustaBuilder<?> bustaBuilder = this.protocolFactory.createBustaBuilder(this.openspcoopstate.getStatoRichiesta());
		this.idServizio = richiestaDelegata.getIdServizio();	
		this.msgContext.getProtocol().setFruitore(this.soggettoFruitore);	
		this.msgContext.getProtocol().setErogatore(this.idServizio.getSoggettoErogatore());		
		this.msgContext.getProtocol().setTipoServizio(this.idServizio.getTipo());
		this.msgContext.getProtocol().setServizio(this.idServizio.getNome());
		this.msgContext.getProtocol().setVersioneServizio(this.idServizio.getVersione());
		this.msgContext.getProtocol().setAzione(this.idServizio.getAzione());
		this.msgContext.getProtocol().setIdRichiesta(this.idMessageRequest);
		
		
		
		
		/*
		  * --------- Dati di identificazione ----------
		 */
		
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.setNome(this.servizioApplicativo);
		idServizioApplicativo.setIdSoggettoProprietario(this.soggettoFruitore);
		
		
		
		

		
	
		
		
		
		/* -------------- Identificazione servizio ------------------ */
		String infoSearch = null;
		try{
			infoSearch = IDServizioFactory.getInstance().getUriFromIDServizio(this.idServizio);
		}catch(Exception e){
			infoSearch = this.idServizio.toString(false);
		}
		if (this.idServizio.getAzione() != null)
			infoSearch = infoSearch + " azione " + this.idServizio.getAzione();
		
		// Cerco nome del registro su cui cercare
		this.msgDiag.addKeyword(CostantiPdD.KEY_INFO_SERVIZIO_BUSTA,infoSearch );
		this.msgDiag.mediumDebug("Ricerca nome registro ["+infoSearch+"]...");
		String nomeRegistroForSearch = null;
		try {
			nomeRegistroForSearch = this.configurazionePdDReader.getRegistroForImbustamento(this.soggettoFruitore, this.idServizio, false, requestInfo);
		} catch (Exception e) {
			logError(this.logCore, "Connettore associato al servizio non trovato: "+e.getMessage(),e);
			this.msgDiag.addKeywordErroreProcessamento(e,"connettore associato al servizio non trovato");
			this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_REGISTRO_RICERCA_SERVIZIO_FALLITA);
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_514_ROUTING_CONFIGURATION_ERROR), e,null)));
			}
			return;
		}

		
		// Gestisco riferimento asincrono
		String riferimentoServizioCorrelatoRicercaSolamenteServizioCorrelato = null;
		boolean supportoProfiliAsincroni = protocolConfig.isSupportato(requestInfo.getProtocolServiceBinding(),ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)
				|| protocolConfig.isSupportato(requestInfo.getProtocolServiceBinding(),ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
		if(supportoProfiliAsincroni) {
			if (this.headerIntegrazioneRichiesta.getBusta() != null && this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null)
				riferimentoServizioCorrelatoRicercaSolamenteServizioCorrelato = this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio();		
			if(riferimentoServizioCorrelatoRicercaSolamenteServizioCorrelato==null &&
				// FIX compatibilita integrazione asincroni con versioni precedente a 1.4
				// L'integrazione era possibile anche tramite info integrazione 'Collaborazione'
				propertiesReader.isIntegrazioneAsincroniConIdCollaborazioneEnabled() &&
				this.headerIntegrazioneRichiesta.getBusta() != null && this.headerIntegrazioneRichiesta.getBusta().getIdCollaborazione() != null){
				// utilizzo l'informazione come integrazione asincrona SOLO se il servizio e' correlato.
				Servizio infoServizioTmpVerificaCorrelato = null;
				try{
					infoServizioTmpVerificaCorrelato = this.registroServiziReader.getInfoServizioCorrelato(this.soggettoFruitore,this.idServizio, nomeRegistroForSearch, true, requestInfo);
				}catch(Exception e){
					logDebug(this.logCore, "Verifica servizio ["+infoSearch+"] se e' correlato, fallita: "+e.getMessage());
					try{
						infoServizioTmpVerificaCorrelato = this.registroServiziReader.getInfoServizioAzioneCorrelata(this.soggettoFruitore, this.idServizio,nomeRegistroForSearch, true, requestInfo);
					}catch(Exception eCorrelato){
						logDebug(this.logCore, "Verifica servizio ["+infoSearch+"] se e' correlato rispetto all'azione, fallita: "+e.getMessage());
					}
				}
				if(infoServizioTmpVerificaCorrelato!=null){
					// Il servizio e' correlato!
					riferimentoServizioCorrelatoRicercaSolamenteServizioCorrelato = this.headerIntegrazioneRichiesta.getBusta().getIdCollaborazione();
				}	
			}
		}
		if (riferimentoServizioCorrelatoRicercaSolamenteServizioCorrelato != null) {
			infoSearch = "Servizio correlato " + infoSearch;
		} else {
			infoSearch = "Servizio " + infoSearch;
		}
		infoSearch = "Ricerca nel registro dei servizi di: " + infoSearch;
		if (riferimentoServizioCorrelatoRicercaSolamenteServizioCorrelato != null)
			infoSearch = infoSearch + " (idServizioCorrelato: "+ riferimentoServizioCorrelatoRicercaSolamenteServizioCorrelato + ")";
		this.msgDiag.addKeyword(CostantiPdD.KEY_INFO_SERVIZIO_BUSTA,infoSearch );
		this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioInCorso");
		
		// Effettuo ricerca
		boolean isServizioCorrelato = false;
		String invocazioneAzioneErrata = null;
		String portTypeErrato = null;
		boolean ricercaConErrore = false;
		boolean servizioCorrelatoNonTrovato = false;
		boolean servizioNonTrovato = false;
		Servizio infoServizio = null;
		Exception eServiceNotFound = null;
		try {
			// RiferimentoServizioCorrelato presente: cerco solo come servizio
			// correlato.
			if (riferimentoServizioCorrelatoRicercaSolamenteServizioCorrelato != null) {

				String erroreRicerca = null;

				// Ricerca come servizio correlato
				this.msgDiag.mediumDebug("Ricerca servizio correlato ...");
				try {
					infoServizio = this.registroServiziReader.getInfoServizioCorrelato(this.soggettoFruitore,this.idServizio, nomeRegistroForSearch, true, requestInfo);
					isServizioCorrelato = true;
				} catch (DriverRegistroServiziAzioneNotFound e) {
					boolean throwFault = true;
					if(corsTrasparente) {
						try {
							infoServizio = this.registroServiziReader.getInfoServizioCorrelato(this.soggettoFruitore,this.idServizio, nomeRegistroForSearch, false, requestInfo);
							isServizioCorrelato = true;
							throwFault = false;
						}catch(Throwable ignore) {
							// ignore
						}
					}
					if(throwFault) {
						invocazioneAzioneErrata = e.getMessage();
						throw e;
					}
				} catch (DriverRegistroServiziNotFound e) {
					erroreRicerca = "\nRicerca come servizio correlato-> "+ e.getMessage();
				}

				// Ricerca come servizio e azione correlata se ho un profilo
				// asincrono asimmetrico (check del profilo interno al metodo)
				if (infoServizio == null && (this.idServizio.getAzione() != null)) {
					this.msgDiag.mediumDebug("Ricerca servizio con azione correlata...");
					try {
						infoServizio = this.registroServiziReader.getInfoServizioAzioneCorrelata(this.soggettoFruitore, this.idServizio,nomeRegistroForSearch, true, requestInfo);
						isServizioCorrelato = true;
					} catch (DriverRegistroServiziAzioneNotFound e) {
						boolean throwFault = true;
						if(corsTrasparente) {
							try {
								infoServizio = this.registroServiziReader.getInfoServizioAzioneCorrelata(this.soggettoFruitore, this.idServizio,nomeRegistroForSearch, false, requestInfo);
								isServizioCorrelato = true;
								throwFault = false;
							}catch(Throwable ignore) {
								// ignore
							}
						}
						if(throwFault) {
							invocazioneAzioneErrata = e.getMessage();
							throw e;
						}
					} catch (DriverRegistroServiziNotFound e) {
						erroreRicerca = erroreRicerca+ "\nRicerca come servizio correlato -> "+ e.getMessage();
					}
				}

				// Se non trovato genero errore
				this.msgDiag.highDebug("Controllo dati individuati ...");
				if (infoServizio == null && erroreRicerca == null)
					throw new DriverRegistroServiziNotFound(
							"Servizio Correlato non trovato ne tramite la normale ricerca, ne tramite la ricerca per azione correlata (solo se profilo e' asincrono asimmetrico)");
				else if (infoServizio == null)
					throw new DriverRegistroServiziNotFound(erroreRicerca);

			}

			// RiferimentoServizioCorrelato non presente: cerco sia come
			// servizio, che casomai come servizio correlato.
			else {
				String erroreRicerca = null;

				// Ricerca come servizio
				this.msgDiag.mediumDebug("Ricerca servizio ...");
				try {
					infoServizio = this.registroServiziReader.getInfoServizio(this.soggettoFruitore, this.idServizio,nomeRegistroForSearch,true, true, requestInfo);
				} catch (DriverRegistroServiziAzioneNotFound e) {
					boolean throwFault = true;
					if(corsTrasparente) {
						try {
							infoServizio = this.registroServiziReader.getInfoServizio(this.soggettoFruitore, this.idServizio,nomeRegistroForSearch,true, false, requestInfo);
							throwFault = false;
						}catch(Throwable ignore) {
							// ignore
						}
					}
					if(throwFault) {
						invocazioneAzioneErrata = e.getMessage();
						throw e;
					}
				} catch (DriverRegistroServiziNotFound e) {
					erroreRicerca = "\nRicerca come servizio -> "+ e.getMessage();
				}

				// Ricerca come servizio correlato
				if (infoServizio == null) {
					this.msgDiag.mediumDebug("Ricerca servizio correlato...");
					try {
						infoServizio = this.registroServiziReader.getInfoServizioCorrelato(this.soggettoFruitore,this.idServizio, nomeRegistroForSearch, true, requestInfo);
						isServizioCorrelato = true;
					} catch (DriverRegistroServiziAzioneNotFound e) {
						boolean throwFault = true;
						if(corsTrasparente) {
							try {
								infoServizio = this.registroServiziReader.getInfoServizioCorrelato(this.soggettoFruitore,this.idServizio, nomeRegistroForSearch, false, requestInfo);
								isServizioCorrelato = true;
								throwFault = false;
							}catch(Throwable ignore) {
								// ignore
							}
						}
						if(throwFault) {
							invocazioneAzioneErrata = e.getMessage();
							throw e;
						}
					} catch (DriverRegistroServiziNotFound e) {
						erroreRicerca = erroreRicerca+ "\nRicerca come servizio correlato -> "+ e.getMessage();
					}
				}

				// Se non trovato genero errore
				if (infoServizio == null && erroreRicerca == null)
					throw new DriverRegistroServiziNotFound(
							"Servizio non trovato ne tramite la normale ricerca, ne tramite la ricerca per servizio correlato");
				else if (infoServizio == null)
					throw new DriverRegistroServiziNotFound(erroreRicerca);

			}
		} catch (DriverRegistroServiziNotFound e) {
			eServiceNotFound = e;
			this.msgDiag.addKeywordErroreProcessamento(e);
			this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_REGISTRO_RICERCA_SERVIZIO_FALLITA);
			servizioNonTrovato = true;
		} catch (DriverRegistroServiziAzioneNotFound e) {
			eServiceNotFound = e;
			this.msgDiag.addKeywordErroreProcessamento(e);
			this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_REGISTRO_RICERCA_SERVIZIO_FALLITA);
			// viene impostata la variabile invocazioneAzioneErrata
		} catch (DriverRegistroServiziPortTypeNotFound e) {
			eServiceNotFound = e;
			this.msgDiag.addKeywordErroreProcessamento(e,"configurazione registro dei servizi errata");
			this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_REGISTRO_RICERCA_SERVIZIO_FALLITA);
			portTypeErrato = "Configurazione del registro dei Servizi errata: "+ e.getMessage();
		} catch(DriverRegistroServiziCorrelatoNotFound e){
			eServiceNotFound = e;
			this.msgDiag.addKeywordErroreProcessamento(e,"correlazione asincrona non rilevata");
			this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_REGISTRO_RICERCA_SERVIZIO_FALLITA);
			servizioCorrelatoNonTrovato = true;
		} 
		catch (Exception e) {
			eServiceNotFound = e;
			this.msgDiag.addKeywordErroreProcessamento(e,"errore generale");
			this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_REGISTRO_RICERCA_SERVIZIO_FALLITA);
			logError(this.logCore, "Ricerca servizio fallita",e);
			ricercaConErrore = true;
		}

		// Segnalo eventuali errori di servizio non trovato / errato
		if (infoServizio == null) {
			if (!servizioNonTrovato && !ricercaConErrore && !servizioCorrelatoNonTrovato && invocazioneAzioneErrata == null) {
				this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "servizio non esistente" );
				this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_REGISTRO_RICERCA_SERVIZIO_FALLITA);
				servizioNonTrovato = true;
			}
			ErroreIntegrazione erroreIntegrazione = null;
			IntegrationFunctionError integrationFunctionError = null;
			if (invocazioneAzioneErrata != null) {
				
				String azione = "";
				if(this.idServizio.getAzione()!=null) {
					azione = "(azione:"+ this.idServizio.getAzione()+ ") ";
				}
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.OPERAZIONE_NON_INDIVIDUATA, "true");
				
				erroreIntegrazione = ErroriIntegrazione.ERRORE_423_SERVIZIO_CON_AZIONE_SCORRETTA.
						getErrore423_ServizioConAzioneScorretta(azione+ invocazioneAzioneErrata);
				integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
			} else if (portTypeErrato != null) {
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(portTypeErrato, CodiceErroreIntegrazione.CODICE_540_REGISTRO_SERVIZI_MAL_CONFIGURATO);
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			} else if (servizioNonTrovato) {
				erroreIntegrazione = ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione();
				integrationFunctionError = IntegrationFunctionError.NOT_FOUND;
			} else if (ricercaConErrore) {
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_534_REGISTRO_DEI_SERVIZI_NON_DISPONIBILE);
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			} else if (servizioCorrelatoNonTrovato){
				erroreIntegrazione = ErroriIntegrazione.ERRORE_408_SERVIZIO_CORRELATO_NON_TROVATO.getErroreIntegrazione();
				//integrationFunctionError = IntegrationFunctionError.CORRELATION_INFORMATION_NOT_FOUND; in questo caso si tratta di un errore nella configurazione
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			} 
			else {
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO);
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			}
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError,erroreIntegrazione,eServiceNotFound,null)));
			}
			return;
		}
		this.msgDiag.highDebug("Ricerca servizio terminata");
		infoServizio.setCorrelato(isServizioCorrelato);
		this.msgContext.getProtocol().setProfiloCollaborazione(infoServizio.getProfiloDiCollaborazione(),null); // il valore verra' serializzato solo successivamente nella busta
		this.msgDiag.addKeyword(CostantiPdD.KEY_PROFILO_COLLABORAZIONE, traduttore.toString(infoServizio.getProfiloDiCollaborazione()));
		if(infoServizio!=null && infoServizio.getIdAccordo()!=null){
			this.msgContext.getProtocol().setIdAccordo(infoServizio.getIdAccordo());
			richiestaDelegata.setIdAccordo(infoServizio.getIdAccordo());
			try{
				this.idServizio.setUriAccordoServizioParteComune(IDAccordoFactory.getInstance().getUriFromIDAccordo(infoServizio.getIdAccordo()));
			}catch(Exception e){
				// ignore
			}
		}
		this.msgDiag.highDebug("Convert infoServizio to Busta ...");
		this.bustaRichiesta = infoServizio.convertToBusta(this.protocolFactory.getProtocol(), this.soggettoFruitore);
		if(sa!=null) {
			this.bustaRichiesta.setServizioApplicativoFruitore(sa.getNome());
		}
		this.msgDiag.highDebug("Convert infoServizio to Busta terminata");
		inRequestPDMessage.setBustaRichiesta(this.bustaRichiesta);
		
		// Aggiorno eventuale valore dipendete dal profilo (PDC)
		if(this.msgContext.getProtocol()!=null && this.idServizio.getVersione()!=null &&
			(
					(this.msgContext.getProtocol().getVersioneServizio()==null) 
					||
					(this.msgContext.getProtocol().getVersioneServizio().intValue()!=this.idServizio.getVersione().intValue())
			) 
		){
			this.msgContext.getProtocol().setVersioneServizio(this.idServizio.getVersione());
		}
		

		
		
		
		
		
		
		/* ------------ Autorizzazione ------------- */
		
		String tipoAutorizzazione = null;
		String tipoAutorizzazioneContenuto = null;
		try {
			tipoAutorizzazione = this.configurazionePdDReader.getAutorizzazione(this.portaDelegata);
			tipoAutorizzazioneContenuto = this.configurazionePdDReader.getAutorizzazioneContenuto(this.portaDelegata);
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e, "letturaAutenticazioneTokenAutorizzazione");
			logError(this.logCore, this.msgDiag.getMessaggio_replaceKeywords("letturaAutenticazioneTokenAutorizzazione"),e);
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		
		DatiInvocazionePortaDelegata datiInvocazione = new DatiInvocazionePortaDelegata();
		datiInvocazione.setBusta(this.bustaRichiesta);
		datiInvocazione.setToken(token);
		datiInvocazione.setPddContext(pddContext);
		datiInvocazione.setInfoConnettoreIngresso(this.inRequestContext.getConnettore());
		datiInvocazione.setIdServizio(richiestaDelegata.getIdServizio());
		datiInvocazione.setState(this.openspcoopstate.getStatoRichiesta());
		datiInvocazione.setIdPD(identificativoPortaDelegata);
		datiInvocazione.setPd(this.portaDelegata);		
		datiInvocazione.setIdServizioApplicativo(idServizioApplicativo);
		datiInvocazione.setServizioApplicativo(sa);
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Autorizzazione del servizio applicativo...");
		

		this.msgDiag.mediumDebug("Autorizzazione del servizio applicativo...");
		this.msgContext.getIntegrazione().setTipoAutorizzazione(tipoAutorizzazione);
		if(tipoAutorizzazione!=null){
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE, tipoAutorizzazione);
		}
		if (!CostantiConfigurazione.AUTORIZZAZIONE_NONE.equalsIgnoreCase(tipoAutorizzazione)) {
			
			transaction.getTempiElaborazione().startAutorizzazione();
			try {
			
				this.msgDiag.logPersonalizzato("autorizzazioneInCorso");
				
				ErroreIntegrazione errore = null;
				Exception eAutorizzazione = null;
				OpenSPCoop2Message errorMessageAutorizzazione = null;
				String wwwAuthenticateErrorHeader = null;
				boolean detailsSet = false;
				IntegrationFunctionError integrationFunctionError = null;
				try {						
					EsitoAutorizzazionePortaDelegata esito = 
							GestoreAutorizzazione.verificaAutorizzazionePortaDelegata(tipoAutorizzazione, datiInvocazione, pddContext, this.protocolFactory, this.requestMessage, this.logCore); 
					CostantiPdD.addKeywordInCache(this.msgDiag, esito.isEsitoPresenteInCache(),
							pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE);
					if(esito.getDetails()==null){
						this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}else{
						this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " ("+esito.getDetails()+")");
					}
					detailsSet = true;
					if (!esito.isAutorizzato()) {
						errore = esito.getErroreIntegrazione();
						eAutorizzazione = esito.getEccezioneProcessamento();
						errorMessageAutorizzazione = esito.getErrorMessage();
						wwwAuthenticateErrorHeader = esito.getWwwAuthenticateErrorHeader();
						integrationFunctionError = esito.getIntegrationFunctionError();
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTORIZZAZIONE, "true");
					}
					else{
						this.msgDiag.logPersonalizzato("autorizzazioneEffettuata");
					}
					
				} catch (Exception e) {
					CostantiPdD.addKeywordInCache(this.msgDiag, false,
							pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE);
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento("processo di autorizzazione ["
									+ tipoAutorizzazione + "] fallito, " + e.getMessage(),CodiceErroreIntegrazione.CODICE_504_AUTORIZZAZIONE);
					eAutorizzazione = e;
					logError(this.logCore, "processo di autorizzazione ["
							+ tipoAutorizzazione + "] fallito, " + e.getMessage(),e);
				}
				if (errore != null) {
					if(!detailsSet) {
						this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}
					String descrizioneErrore = null;
					try{
						descrizioneErrore = errore.getDescrizione(this.protocolFactory);
						this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, descrizioneErrore);
					}catch(Exception e){
						logError(this.logCore, "getDescrizione Error:"+e.getMessage(),e);
					}
					this.msgDiag.logPersonalizzato("servizioApplicativoFruitore.nonAutorizzato");
					String errorMsg =  "Riscontrato errore durante il processo di Autorizzazione per il messaggio con identificativo ["+this.idMessageRequest+"]: "+descrizioneErrore;
					if(eAutorizzazione!=null){
						logError(this.logCore, errorMsg,eAutorizzazione);
					}
					else{
						logError(this.logCore, errorMsg);
					}
					this.openspcoopstate.releaseResource();
	
					if (this.msgContext.isGestioneRisposta()) {
						
						if(errorMessageAutorizzazione!=null) {
							this.msgContext.setMessageResponse(errorMessageAutorizzazione);
						}
						else {
						
							if(CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.equals(errore.getCodiceErrore()) ||
									CodiceErroreIntegrazione.CODICE_445_TOKEN_AUTORIZZAZIONE_FALLITA.equals(errore.getCodiceErrore())){
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.AUTHORIZATION;
								}
							}else{
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
								}
							}
							
							OpenSPCoop2Message errorOpenSPCoopMsg = (this.generatoreErrore.build(pddContext,integrationFunctionError,errore,
									eAutorizzazione,null));
	
							if(wwwAuthenticateErrorHeader!=null) {
								errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
							}
							
							this.msgContext.setMessageResponse(errorOpenSPCoopMsg);
							
						}
					}
					return;
				}
			}finally {
				transaction.getTempiElaborazione().endAutorizzazione();
			}
		}
		else{
			this.msgDiag.logPersonalizzato("autorizzazioneDisabilitata");
		}
		
		
		
		
		
		
		
		
	
		
		
		
		/* -------- Controlli Header di Integrazione ------------- */
		
		if(infoServizio.getIdRiferimentoRichiesta() &&
				protocolConfig.isIntegrationInfoRequired(TipoPdD.DELEGATA, requestInfo.getProtocolServiceBinding(),FunzionalitaProtocollo.RIFERIMENTO_ID_RICHIESTA)) {
			String riferimentoRichiesta = null;
			if (this.headerIntegrazioneRichiesta!=null &&
					this.headerIntegrazioneRichiesta.getBusta() != null && this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null) {
				riferimentoRichiesta = this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio();
				this.msgContext.getProtocol().setRiferimentoAsincrono(riferimentoRichiesta);
			}
			if(riferimentoRichiesta==null) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < this.tipiIntegrazionePD.length; i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(this.tipiIntegrazionePD[i]);
				}
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPI_INTEGRAZIONE ,bf.toString() );
				this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"riferimentoIdRichiesta.nonFornito");
				ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_442_RIFERIMENTO_ID_MESSAGGIO.getErroreIntegrazione();
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.CORRELATION_INFORMATION_NOT_FOUND;
				
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError,erroreIntegrazione,null,null)));
				}
				return;
				
			}
		}
		
		
		
		
		
		/* -------- Profilo di Gestione ------------- */
		try {
			String profiloGestione = this.registroServiziReader.getProfiloGestioneFruizioneServizio(this.idServizio,nomeRegistroForSearch, requestInfo);
			richiestaDelegata.setProfiloGestione(profiloGestione);
			this.msgDiag.mediumDebug("Profilo di gestione ["+ RicezioneContenutiApplicativi.ID_MODULO+ "] della busta: " + profiloGestione);
		} catch (Exception e) {
			this.msgDiag.addKeywordErroreProcessamento(e,"analisi del profilo di gestione fallita");
			this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_REGISTRO_RICERCA_SERVIZIO_FALLITA);
			logError(this.logCore, "Identificazione Profilo Gestione fallita",e);
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,null)));
			}
			return;
		}
		
		
		
		
		/* -------- OpenSPCoop2Message Update ------------- */
		try {
			this.msgDiag.mediumDebug("Aggiornamento del messaggio");
			this.requestMessage = this.protocolFactory.createProtocolManager().updateOpenSPCoop2MessageRequest(this.requestMessage, this.bustaRichiesta,
					this.protocolFactory.getCachedRegistryReader(this.registroServiziReader, requestInfo));
		} catch (Exception e) {
			this.msgDiag.addKeywordErroreProcessamento(e,"Aggiornamento messaggio fallito");
			this.msgDiag.logErroreGenerico(e,"ProtocolManager.updateOpenSPCoop2Message");
			logError(this.logCore, "ProtocolManager.updateOpenSPCoop2Message error: "+e.getMessage(),e);
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,null)));
			}
			return;
		}
		
		
		
		
		/* -------- Indirizzi Risposta  ------------- */
		
		String indirizzoFruitore = null;
		String indirizzoErogatore = null;
		IProtocolConfiguration protocolConfiguration = this.protocolFactory.createProtocolConfiguration();
		if(protocolConfiguration.isSupportoIndirizzoRisposta()){
			try {
				Connettore connettoreFruitore = null;
				try{
					connettoreFruitore = this.registroServiziReader.getConnettore(this.soggettoFruitore, nomeRegistroForSearch, requestInfo);
				}catch(org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound dNotFound){
					// ignore
				}
				if(connettoreFruitore!=null && !CostantiConfigurazione.DISABILITATO.equals(connettoreFruitore.getTipo()) &&
					connettoreFruitore.getProperties()!=null && connettoreFruitore.getProperties().containsKey(CostantiConnettori.CONNETTORE_LOCATION)){
					indirizzoFruitore = connettoreFruitore.getProperties().get(CostantiConnettori.CONNETTORE_LOCATION);
				}
				this.msgDiag.mediumDebug("Indirizzo Risposta del soggetto fruitore ["+ this.soggettoFruitore+ "]: " + indirizzoFruitore);
				
				Connettore connettoreErogatore = null;
				try{
					connettoreErogatore = this.registroServiziReader.getConnettore(this.idServizio.getSoggettoErogatore(), nomeRegistroForSearch, requestInfo);
				}catch(org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound dNotFound){
					// ignore
				}
				if(connettoreErogatore!=null && !CostantiConfigurazione.DISABILITATO.equals(connettoreErogatore.getTipo()) &&
					connettoreErogatore.getProperties()!=null && connettoreErogatore.getProperties().containsKey(CostantiConnettori.CONNETTORE_LOCATION)){
					indirizzoErogatore = connettoreErogatore.getProperties().get(CostantiConnettori.CONNETTORE_LOCATION);
				}
				this.msgDiag.mediumDebug("Indirizzo Risposta del soggetto erogatore ["+ this.idServizio.getSoggettoErogatore()+ "]: " + indirizzoErogatore);
				
			} catch (Exception e) {
				this.msgDiag.addKeywordErroreProcessamento(e,"recupero degli indirizzi di risposta per i soggetti fallita");
				this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_REGISTRO_RICERCA_SERVIZIO_FALLITA);
				logError(this.logCore, "Identificazione Indirizzo Risposta fallita",e);
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,null)));
				}
				return;
			}
			this.msgContext.getProtocol().setIndirizzoFruitore(indirizzoFruitore);
			this.msgContext.getProtocol().setIndirizzoErogatore(indirizzoErogatore);
		}
		
		
		/* ---- Ricerca registro dei servizi terminata con successo --- */
		this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioEffettuata");
		
		
		

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// ------------- in-protocol-handler -----------------------------
		try{
			InRequestProtocolContext inRequestProtocolContext = new InRequestProtocolContext(this.inRequestContext);
			if(inRequestProtocolContext.getStato()==null) {
				inRequestProtocolContext.setStato(this.openspcoopstate.getStatoRichiesta());
			}
			if(inRequestProtocolContext.getConnettore()!=null){
				inRequestProtocolContext.getConnettore().setCredenziali(credenziali);
			}
			inRequestProtocolContext.setProtocollo(this.msgContext.getProtocol());
			inRequestProtocolContext.setIntegrazione(this.msgContext.getIntegrazione());
			GestoreHandlers.inRequestProtocol(inRequestProtocolContext, this.msgDiag, this.logCore);
		}catch(Exception e){		
			ErroreIntegrazione erroreIntegrazione = null;
			IntegrationFunctionError integrationFunctionError = null; 
			if(e instanceof HandlerException he){
				if(he.isEmettiDiagnostico()){
					this.msgDiag.logErroreGenerico(e,he.getIdentitaHandler());
				}
				logError(this.logCore, "Gestione InRequestProtocolHandler non riuscita ("+he.getIdentitaHandler()+"): "	+ he);
				if(this.msgContext.isGestioneRisposta()){
					erroreIntegrazione = he.convertToErroreIntegrazione();
					integrationFunctionError = he.getIntegrationFunctionError();
				}
			}else{
				this.msgDiag.logErroreGenerico(e,"InvocazioneInRequestHandler");
				logError(this.logCore, "Gestione InRequestProtocolHandler non riuscita: "	+ e);
			}
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				if(erroreIntegrazione==null){
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_558_HANDLER_IN_PROTOCOL_REQUEST);
				}
				if(integrationFunctionError==null) {
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				OpenSPCoop2Message responseMessageError = this.generatoreErrore.build(pddContext,integrationFunctionError,erroreIntegrazione,e,null);
				if(e instanceof HandlerException he){
					he.customized(responseMessageError);
				}
				this.msgContext.setMessageResponse(responseMessageError);
			}
			return;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		/* -------------------------- Implementazione PdD Soggetti busta -------------------------------*/
		String implementazionePdDMittente = null;
		String implementazionePdDDestinatario = null;
		String idPdDMittente = null;
		String idPdDDestinatario = null;
		this.msgDiag.mediumDebug("Ricerca implementazione della porta di dominio dei soggetti...");
		try{
			implementazionePdDMittente = this.registroServiziReader.getImplementazionePdD(this.soggettoFruitore, null, requestInfo);
			implementazionePdDDestinatario = this.registroServiziReader.getImplementazionePdD(this.idServizio.getSoggettoErogatore(), null, requestInfo);
			idPdDMittente = this.registroServiziReader.getIdPortaDominio(this.soggettoFruitore, null, requestInfo);
			idPdDDestinatario = this.registroServiziReader.getIdPortaDominio(this.idServizio.getSoggettoErogatore(), null, requestInfo);
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e,"ricercaImplementazioniPdD");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_534_REGISTRO_DEI_SERVIZI_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		this.msgDiag.mediumDebug("ImplementazionePdD soggetto ("+this.soggettoFruitore.toString()+") e' ["+implementazionePdDMittente+"], soggetto ("
				+this.idServizio.getSoggettoErogatore().toString()+") e' ["+implementazionePdDDestinatario+"]");
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Controllo validazione xsd abilitata/disabilitata...");
		
		
		
		/* ------------ Validazione Contenuti Applicativi ------------- */
		this.msgDiag.mediumDebug("Controllo validazione xsd abilitata/disabilitata...");
		ValidazioneContenutiApplicativi validazioneContenutoApplicativoApplicativo = null;
		List<Proprieta> proprietaPorta = null;
		try {
			validazioneContenutoApplicativoApplicativo = this.configurazionePdDReader.getTipoValidazioneContenutoApplicativo(this.portaDelegata,implementazionePdDDestinatario, true);
			proprietaPorta = this.portaDelegata.getProprietaList();
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"getTipoValidazioneContenutoApplicativo(pd,"+implementazionePdDDestinatario+")");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		if(validazioneContenutoApplicativoApplicativo!=null && validazioneContenutoApplicativoApplicativo.getTipo()!=null){
			String tipo = ValidatoreMessaggiApplicativi.getTipo(validazioneContenutoApplicativoApplicativo);
			this.msgContext.getIntegrazione().setTipoValidazioneContenuti(tipo);
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_CONTENUTI, tipo);
			this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,"");
		}
		if (
				(validazioneContenutoApplicativoApplicativo!=null)
				&&
				(
						CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getStato())
						|| 
						CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())
				)
			) {

			transaction.getTempiElaborazione().startValidazioneRichiesta();
			ByteArrayInputStream binXSD = null;
			try {
				this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaInCorso");
				
				boolean readInterface = CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo());
						
				if(ServiceBinding.SOAP.equals(this.requestMessage.getServiceBinding())){
				
					// Accept mtom message
					List<MtomXomReference> xomReferences = null;
					if(StatoFunzionalita.ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getAcceptMtomMessage())){
						this.msgDiag.mediumDebug("Validazione xsd della richiesta (mtomFastUnpackagingForXSDConformance)...");
						if(ServiceBinding.SOAP.equals(this.requestMessage.getServiceBinding())==false){
							throw new Exception("Funzionalita 'AcceptMtomMessage' valida solamente per Service Binding SOAP");
						}
						xomReferences = this.requestMessage.castAsSoap().mtomFastUnpackagingForXSDConformance();
					}
					
					// Init Validatore
					this.msgDiag.mediumDebug("Validazione xsd della richiesta (initValidator)...");
					ValidatoreMessaggiApplicativi validatoreMessaggiApplicativi = new ValidatoreMessaggiApplicativi(
							this.registroServiziReader, richiestaDelegata.getIdServizio(), this.requestMessage,readInterface,
							propertiesReader.isValidazioneContenutiApplicativiRpcLiteralXsiTypeGestione(),
							proprietaPorta,
							pddContext);
				
					// Validazione WSDL
					if (CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo())
						|| CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo())) {
						this.msgDiag.mediumDebug("Validazione wsdl della richiesta ...");
						validatoreMessaggiApplicativi.validateWithWsdlLogicoImplementativo(true);
					}
					
					// Validazione XSD
					this.msgDiag.mediumDebug("Validazione xsd della richiesta (validazione)...");
					validatoreMessaggiApplicativi.validateWithWsdlDefinitorio(true);
	
					// Validazione WSDL (Restore Original Document)
					if (
							(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo())
									|| 
							CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo()))
							&&
							(propertiesReader.isValidazioneContenutiApplicativiRpcLiteralXsiTypeGestione() &&
							propertiesReader.isValidazioneContenutiApplicativiRpcLiteralXsiTypeRipulituraDopoValidazione())
						){
						this.msgDiag.mediumDebug("Ripristino elementi modificati per supportare validazione wsdl della richiesta ...");
						validatoreMessaggiApplicativi.restoreOriginalDocument(true);
					}
					
					// Ripristino struttura messaggio con xom
					if(xomReferences!=null && !xomReferences.isEmpty()){
						this.msgDiag.mediumDebug("Validazione xsd della richiesta (mtomRestoreAfterXSDConformance)...");
						if(!ServiceBinding.SOAP.equals(this.requestMessage.getServiceBinding())){
							throw new CoreException("Funzionalita 'AcceptMtomMessage' valida solamente per Service Binding SOAP");
						}
						this.requestMessage.castAsSoap().mtomRestoreAfterXSDConformance(xomReferences);
					}
					
				}
				else {
					
					// Init Validatore
					this.msgDiag.mediumDebug("Validazione della richiesta (initValidator)...");
					ValidatoreMessaggiApplicativiRest validatoreMessaggiApplicativi = 
						new ValidatoreMessaggiApplicativiRest(this.registroServiziReader, richiestaDelegata.getIdServizio(), this.requestMessage, readInterface, proprietaPorta,
								this.protocolFactory, pddContext);
					
					if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.equals(validazioneContenutoApplicativoApplicativo.getTipo()) &&
							this.requestMessage.castAsRest().hasContent()) {
						
						// Validazione XSD
						this.msgDiag.mediumDebug("Validazione xsd della richiesta ...");
						validatoreMessaggiApplicativi.validateWithSchemiXSD(true);
						
					}
					else {
						
						// Validazione Interface
						validatoreMessaggiApplicativi.validateRequestWithInterface(false);
						
					}
					
				}
				
				this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaEffettuata");
				
			} catch (ValidatoreMessaggiApplicativiException ex) {
				this.msgDiag.addKeywordErroreProcessamento(ex);
				logError(this.logCore, "[ValidazioneContenutiApplicativi Richiesta] "+ex.getMessage(),ex);
				if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
					this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita.warningOnly");
				}
				else {
					this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita");
				}
				if (!CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
					
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RICHIESTA, "true");
					
					// validazione abilitata
					this.openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						IntegrationFunctionError integrationFunctionError = null;
						if(ex.getErrore()!=null &&
								(
										//CodiceErroreIntegrazione.CODICE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA.equals(ex.getErrore().getCodiceErrore()) ||
										CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA.equals(ex.getErrore().getCodiceErrore()) 
								)
							){
							integrationFunctionError = IntegrationFunctionError.INVALID_REQUEST_CONTENT;
						}
						else{
							integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
						}
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError, ex.getErrore(),ex,null)));
					}
					return;
				}
			} catch (Exception ex) {
				this.msgDiag.addKeywordErroreProcessamento(ex);
				logError(this.logCore, "Riscontrato errore durante la validazione xsd della richiesta applicativa",ex);
				if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
					this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita.warningOnly");
				}
				else {
					this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita");
				}
				if (!CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
					
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RICHIESTA, "true");
					
					// validazione abilitata
					this.openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA),ex,null)));
					}
					return;
				}
			} finally {
				transaction.getTempiElaborazione().endValidazioneRichiesta();
				if (binXSD != null) {
					try {
						binXSD.close();
					} catch (Exception e) {
						// ignore
					}
				}
			}
		}
		else{
			this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaDisabilitata");
		}

		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Autorizzazione del servizio applicativo...");
		
		
		
		/* ------------ Autorizzazione per Contenuto ------------- */
		this.msgDiag.mediumDebug("Autorizzazione del servizio applicativo...");
		this.msgContext.getIntegrazione().setTipoAutorizzazioneContenuto(tipoAutorizzazioneContenuto);
		if(tipoAutorizzazioneContenuto!=null){
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE_CONTENUTO, tipoAutorizzazioneContenuto);
		}
		if (!CostantiConfigurazione.AUTORIZZAZIONE_NONE.equalsIgnoreCase(tipoAutorizzazioneContenuto)) {
			
			transaction.getTempiElaborazione().startAutorizzazioneContenuti();
			try {
			
				this.msgDiag.logPersonalizzato("autorizzazioneContenutiApplicativiInCorso");
				
				ErroreIntegrazione errore = null;
				Exception eAutorizzazione = null;
				boolean detailsSet = false;
				IntegrationFunctionError integrationFunctionError = null;
				try {
					// Controllo Autorizzazione
					EsitoAutorizzazionePortaDelegata esito = 
							GestoreAutorizzazione.verificaAutorizzazioneContenutoPortaDelegata(tipoAutorizzazioneContenuto, datiInvocazione, pddContext, this.protocolFactory, this.requestMessage, this.logCore);
					CostantiPdD.addKeywordInCache(this.msgDiag, esito.isEsitoPresenteInCache(),
							pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE_CONTENUTI);
					if(esito.getDetails()==null){
						this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}else{
						this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " ("+esito.getDetails()+")");
					}
					detailsSet = true;
					if (!esito.isAutorizzato()) {
						errore = esito.getErroreIntegrazione();
						eAutorizzazione = esito.getEccezioneProcessamento();
						integrationFunctionError = esito.getIntegrationFunctionError();
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTORIZZAZIONE, "true");
					}
					else{
						this.msgDiag.logPersonalizzato("autorizzazioneContenutiApplicativiEffettuata");
					}
				} catch (Exception e) {
					CostantiPdD.addKeywordInCache(this.msgDiag, false,
							pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE_CONTENUTI);
					String msgErroreAutorizzazione = "processo di autorizzazione ["
							+ tipoAutorizzazioneContenuto + "] fallito, " + e.getMessage();
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(msgErroreAutorizzazione, CodiceErroreIntegrazione.CODICE_542_AUTORIZZAZIONE_CONTENUTO);
					eAutorizzazione = e;
					logError(this.logCore, msgErroreAutorizzazione,e);
				}
				if (errore != null) {
					if(!detailsSet) {
						this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}
					this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, errore.getDescrizione(this.protocolFactory));
					this.msgDiag.logPersonalizzato("servizioApplicativoFruitore.contenuto.nonAutorizzato");
					this.openspcoopstate.releaseResource();
	
					if (this.msgContext.isGestioneRisposta()) {
						if(CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.equals(errore.getCodiceErrore()) || 
								CodiceErroreIntegrazione.CODICE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA.equals(errore.getCodiceErrore())){
							if(integrationFunctionError==null) {
								integrationFunctionError = IntegrationFunctionError.CONTENT_AUTHORIZATION_DENY;
							}
						}else{
							if(integrationFunctionError==null) {
								integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
							}
						}
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError,errore,
								eAutorizzazione,null)));
					}
					return;
				}
			}finally {
				transaction.getTempiElaborazione().endAutorizzazioneContenuti();
			}
		}
		else{
			this.msgDiag.logPersonalizzato("autorizzazioneContenutiApplicativiDisabilitata");
		}
		
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Controllo tipo di invocazione (riferimento/normale)...");
		
		
		
		/*
		 * ------------ Check tipo di invocazione PD (x riferimento) -------------
		 */
		this.msgDiag.mediumDebug("Controllo tipo di invocazione (riferimento/normale)...");
		boolean invocazionePDPerRiferimento = false;
		try {
			invocazionePDPerRiferimento = this.configurazionePdDReader.invocazionePortaDelegataPerRiferimento(sa);
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"invocazionePortaDelegataPerRiferimento(sa)");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		if (invocazionePDPerRiferimento) {
			if (!this.msgContext.isInvocazionePDPerRiferimento()) {
				this.msgDiag.logPersonalizzato("portaDelegataInvocabilePerRiferimento.riferimentoNonPresente");
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.BAD_REQUEST,
							ErroriIntegrazione.ERRORE_412_PD_INVOCABILE_SOLO_PER_RIFERIMENTO.
							getErroreIntegrazione(),null,null)));
				}
				return;
			}
			
			try {
				if(this.openspcoopstate.resourceReleased()) {
					// inizializzo
					this.openspcoopstate.setUseConnection(true);
					this.openspcoopstate.initResource(this.identitaPdD, this.msgContext.getIdModulo(), idTransazione);
				}
			}catch (Exception e) {
				this.msgDiag.logErroreGenerico(e,"openspcoopstate.initResource() 'invocazionePerRiferimento'");
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION), e,null)));
				}
				return;
			}
			
			// eventuale sbustamento delle informazioni di protocollo se richieste dal servizio applicativo
			GestoreMessaggi gestoreMessaggio = new GestoreMessaggi(this.openspcoopstate, true,this.msgContext.getIdInvocazionePDPerRiferimento(),Costanti.INBOX,this.msgDiag,this.msgContext.getPddContext());
			try{
				boolean sbustamentoInformazioniProtocollo =
						gestoreMessaggio.sbustamentoInformazioniProtocollo(this.servizioApplicativo,false);
				if(sbustamentoInformazioniProtocollo){
					// attachments non gestiti!
					ProprietaManifestAttachments proprietaManifest = propertiesReader.getProprietaManifestAttachments("standard");
					proprietaManifest.setGestioneManifest(false);
					ProtocolMessage protocolMessage = bustaBuilder.sbustamento(this.requestMessage, pddContext,
							this.bustaRichiesta, RuoloMessaggio.RICHIESTA, proprietaManifest,
							FaseSbustamento.PRE_INVIO_RICHIESTA_PER_RIFERIMENTO, requestInfo.getIntegrationServiceBinding(), requestInfo.getBindingConfig());
					if(protocolMessage!=null) {
						this.requestMessage = protocolMessage.getMessage(); // updated
					}
				}
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e,"invocazionePortaDelegataPerRiferimento.sbustamentoProtocolHeader()");
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
				}
				return;		
			}
			
		} else {
			if (this.msgContext.isInvocazionePDPerRiferimento()) {
				this.msgDiag.logPersonalizzato("portaDelegataInvocabileNormalmente.riferimentoPresente");
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.BAD_REQUEST,
							ErroriIntegrazione.ERRORE_413_PD_INVOCABILE_SOLO_SENZA_RIFERIMENTO.
							getErroreIntegrazione(),null,null)));
				}
				return;
			}
		}

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Controllo non esistenza di una busta ...");
		
		
		
		
		/* ------------ Controllo che il messaggio non contenga una busta */
		this.msgDiag.mediumDebug("Controllo non esistenza di una busta ...");
		ValidazioneSintattica validatoreSintattico = new ValidazioneSintattica(pddContext, this.openspcoopstate.getStatoRichiesta(),this.requestMessage, this.protocolFactory);
		boolean esisteProtocolloMsgRichiesta = false;
		boolean esisteProtocolloMsgRichiestaExit = false;
		try{
			esisteProtocolloMsgRichiesta = validatoreSintattico.
					verifyProtocolPresence(this.msgContext.getTipoPorta(),infoServizio.getProfiloDiCollaborazione(),RuoloMessaggio.RICHIESTA);
		} catch (Exception e){
			this.msgDiag.logErroreGenerico(e,"controlloEsistenzaBusta");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,null)));
			}
			esisteProtocolloMsgRichiestaExit = true;
		} finally {
			if(esisteProtocolloMsgRichiesta) {
				this.msgDiag.logPersonalizzato("richiestaContenenteBusta");
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTEROPERABILITY_PROFILE_REQUEST_ALREADY_EXISTS,
							ErroriIntegrazione.ERRORE_420_BUSTA_PRESENTE_RICHIESTA_APPLICATIVA.
							getErroreIntegrazione(),null,null)));
				}
				esisteProtocolloMsgRichiestaExit = true;
			}
			// *** GB ***
			if(validatoreSintattico!=null){
				validatoreSintattico.setHeaderSOAP(null);
			}
			validatoreSintattico = null;
			// *** GB ***
		}
		if(esisteProtocolloMsgRichiestaExit) {
			return;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Recupero configurazione per salvataggio risposta in cache ...");
		this.msgDiag.mediumDebug("Recupero configurazione per salvataggio risposta in cache ...");
		try{
			ResponseCachingConfigurazione responseCachingConfig = this.configurazionePdDReader.getConfigurazioneResponseCaching(this.portaDelegata);
			if(responseCachingConfig!=null && StatoFunzionalita.ABILITATO.equals(responseCachingConfig.getStato())) {
				
				transaction.getTempiElaborazione().startResponseCachingCalcoloDigest();
				try {
				
					this.msgDiag.mediumDebug("Calcolo digest per salvataggio risposta ...");
					
					HashGenerator hashGenerator = new HashGenerator(propertiesReader.getCachingResponseDigestAlgorithm());
					String digest = hashGenerator.buildKeyCache(this.requestMessage, requestInfo, responseCachingConfig);
					this.requestMessage.addContextProperty(CostantiPdD.RESPONSE_CACHE_REQUEST_DIGEST, digest);
				
				}finally {
					transaction.getTempiElaborazione().endResponseCachingCalcoloDigest();
				}
			}
		} catch (Exception e){
			this.msgDiag.logErroreGenerico(e,"calcoloDigestSalvataggioRisposta");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_561_DIGEST_REQUEST),e,null)));
			}
			return;
		} 
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Update/Delete Header di integrazione se necessario");
		
		
		
		
		
		/*
		 * ------- Update/Delete Header di integrazione se necessario -------
		 */
		// L'header di integrazione SOAP non e' cancellato se presente.
		// Deve allora essere aggiornato ai valori letti
		
		for (int i = 0; i < this.tipiIntegrazionePD.length; i++) {
			try {
				IGestoreIntegrazionePD gestore = pluginLoader.newIntegrazionePortaDelegata(this.tipiIntegrazionePD[i]);
				if(gestore!=null){
					String classType = null;
					try {
						classType = gestore.getClass().getName();
						AbstractCore.init(gestore, pddContext, this.protocolFactory);
					} catch (Exception e) {
						throw new CoreException(
								"Riscontrato errore durante l'inizializzazione della classe (IGestoreIntegrazionePD) ["+ classType
										+ "] da utilizzare per la gestione dell'integrazione delle fruizioni (Update/Delete) di tipo ["+ this.tipiIntegrazionePD[i] + "]: " + e.getMessage());
					}
					if (gestore instanceof IGestoreIntegrazionePDSoap gestoreIntegrazionePDSoap) {
						if(propertiesReader.deleteHeaderIntegrazioneRequestPD()){
							// delete
							gestoreIntegrazionePDSoap.deleteInRequestHeader(inRequestPDMessage);
						}
						else{
							// update
							String servizioApplicativoDaInserireHeader = null;
							if(!CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(this.servizioApplicativo)){
								servizioApplicativoDaInserireHeader = this.servizioApplicativo;
							}
							gestoreIntegrazionePDSoap.updateInRequestHeader(inRequestPDMessage, this.idServizio, 
									this.idMessageRequest, servizioApplicativoDaInserireHeader, idCorrelazioneApplicativa);
						}
					} 
				}
			} catch (Exception e) {
				if(propertiesReader.deleteHeaderIntegrazioneRequestPD()){
					this.msgDiag.logErroreGenerico(e,"deleteHeaderIntegrazione("+ this.tipiIntegrazionePD[i]+")");
				}else{
					this.msgDiag.logErroreGenerico(e,"updateHeaderIntegrazione("+ this.tipiIntegrazionePD[i]+")");
				}
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_541_GESTIONE_HEADER_INTEGRAZIONE),e,null)));
				}
				return;
			}
		}
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Gestione Funzionalita' speciali per Attachments (Manifest)");
		
		
		
		
		
		/*
		 * ------------ Gestione Funzionalita' speciali per Attachments (Manifest) -------------
		 */
		boolean allegaBody = false;
		boolean scartaBody = false;
		try {
			allegaBody = this.configurazionePdDReader.isAllegaBody(this.portaDelegata);
			scartaBody = this.configurazionePdDReader.isScartaBody(this.portaDelegata);
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"configurazionePdDReader.isAllega/ScartaBody(pd)");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		if (scartaBody) {
			IntegrationFunctionError integrationFunctionError = null;
			try {
				if(!ServiceBinding.SOAP.equals(this.requestMessage.getServiceBinding())){
					integrationFunctionError = IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL;
					throw new CoreException("Funzionalita 'ScartaBody' valida solamente per Service Binding SOAP");
				}
				
				// E' permesso SOLO per messaggi con attachment
				if (this.requestMessage.castAsSoap().countAttachments() <= 0) {
					throw new CoreException("La funzionalita' e' permessa solo per messaggi SOAP With Attachments");
				}
			} catch (Exception e) {
				this.msgDiag.addKeywordErroreProcessamento(e);
				this.msgDiag.logPersonalizzato("funzionalitaScartaBodyNonEffettuabile");
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					if(integrationFunctionError==null) {
						integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					}
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError,
							ErroriIntegrazione.ERRORE_425_SCARTA_BODY.
							getErrore425_ScartaBody(e.getMessage()),e,null)));
				}
				return;
			}
		}
		if (allegaBody) {
			try {
				TunnelSoapUtils.allegaBody(this.requestMessage, propertiesReader.getHeaderSoapActorIntegrazione());
			} catch (Exception e) {
				this.msgDiag.addKeywordErroreProcessamento(e);
				this.msgDiag.logPersonalizzato("funzionalitaAllegaBodyNonEffettuabile");
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.BAD_REQUEST,
							ErroriIntegrazione.ERRORE_424_ALLEGA_BODY.
							getErrore424_AllegaBody(e.getMessage()), e,null)));
				}
				return;
			}
		}

		
		
		
		
		
		
		
		
		
		
		/* ------------- Modalita' di gestione ---------------------------- */
		
		// Versione OneWay 
		boolean oneWayStateless = false;
		boolean oneWayVersione11 = false;
		// Versione Sincrono 
		boolean sincronoStateless = false;
		// Asincrono stateless
		boolean asincronoStateless = false;
		
		// Gestione stateless
		this.portaStateless = false; // vero se almeno uno dei precedenti e' vero

		try {

			if(propertiesReader.isServerJ2EE()==null || !propertiesReader.isServerJ2EE().booleanValue()){
				// Stateless obbligatorio in server di tipo web (non j2ee)
				oneWayStateless = true;
				sincronoStateless = true;
				asincronoStateless = true;
			}
			else if (ProfiloDiCollaborazione.ONEWAY.equals(infoServizio.getProfiloDiCollaborazione())) {
				oneWayStateless = this.configurazionePdDReader.isModalitaStateless(this.portaDelegata, infoServizio.getProfiloDiCollaborazione());
			} else if (ProfiloDiCollaborazione.SINCRONO.equals(infoServizio.getProfiloDiCollaborazione())) {
				sincronoStateless = this.configurazionePdDReader.isModalitaStateless(this.portaDelegata, infoServizio.getProfiloDiCollaborazione());
			} else if(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione()) ||
					ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione())){
				asincronoStateless = this.configurazionePdDReader.isModalitaStateless(this.portaDelegata, infoServizio.getProfiloDiCollaborazione());
			}

			oneWayVersione11 = propertiesReader.isGestioneOnewayStateful_1_1()
				&& ProfiloDiCollaborazione.ONEWAY.equals(infoServizio.getProfiloDiCollaborazione())
					&& !oneWayStateless;

			if (oneWayStateless || sincronoStateless || asincronoStateless || oneWayVersione11) {
				this.openspcoopstate = OpenSPCoopState.toStateless(((OpenSPCoopStateful)this.openspcoopstate), this.openspcoopstate.isUseConnection());
				this.portaStateless = true;
				this.msgContext.getIntegrazione().setGestioneStateless(!oneWayVersione11);
			}else{
				this.msgContext.getIntegrazione().setGestioneStateless(false);
			}
			
			if(
					(!this.portaStateless || oneWayVersione11)
					&&
					(
							!this.openspcoopstate.isUseConnection() && 
							(this.openspcoopstate instanceof OpenSPCoopStateful || oneWayVersione11)
					) 
					&&
					this.openspcoopstate.resourceReleased()
				) {
				// inizializzo
				this.openspcoopstate.setUseConnection(true);
				this.openspcoopstate.initResource(this.identitaPdD, this.msgContext.getIdModulo(), idTransazione);
			}

		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneStatelessStateful");
			logError(this.logCore, "Analisi modalita di gestione STATEFUL/STATELESS non riuscita: "+ e);
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}

		
		
		
		
		

		
		
		
		/* ------------- Modalita' di forward ---------------------------- */
		
		// Versione OneWay 
		this.localForward = false;
		LocalForwardEngine localForwardEngine = null;
		LocalForwardParameter localForwardParameter = null;
		PortaApplicativa pa = null;
		try {

			this.localForward = this.configurazionePdDReader.isLocalForwardMode(this.portaDelegata);
			
			if(this.localForward){
								
				String erroreConfigurazione = null;
				
				String prefix = "( Servizio "+IDServizioFactory.getInstance().getUriFromIDServizio(this.idServizio)+" ) ";
				
				if(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione()) ||
						ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione())){
					erroreConfigurazione = "profilo di collaborazione "+infoServizio.getProfiloDiCollaborazione().getEngineValue()+" non supportato";							
				}
				
				if(erroreConfigurazione==null &&
					ProfiloDiCollaborazione.SINCRONO.equals(infoServizio.getProfiloDiCollaborazione()) &&
					!sincronoStateless){
					erroreConfigurazione = "profilo di collaborazione "+infoServizio.getProfiloDiCollaborazione().getEngineValue()+" non supportato nella modalità stateful";	
				}
				
				if(erroreConfigurazione==null &&
					!this.configurazionePdDReader.existsSoggetto(this.idServizio.getSoggettoErogatore(), requestInfo)){
					erroreConfigurazione = "il soggetto erogatore non risulta essere gestito localmente dalla Porta";
				}
				
				RichiestaApplicativa ra = null;
				IDPortaApplicativa idPA = null;
				if(erroreConfigurazione==null){

					String nomePA = this.configurazionePdDReader.getLocalForwardNomePortaApplicativa(this.portaDelegata);
					if(nomePA==null){
						try{
							List<PortaApplicativa> list = this.configurazionePdDReader.getPorteApplicative(this.idServizio, false);
							if(list.isEmpty()){
								throw new DriverConfigurazioneNotFound("NotFound");
							}
							if(list.size()>1){
								StringBuilder bf = new StringBuilder();
								for (PortaApplicativa portaApplicativa : list) {
									if(bf.length()>0) {
										bf.append(",");
									}
									bf.append(portaApplicativa.getNome());
								}
								throw new CoreException("Esiste più di una porta applicativa indirizzabile tramite il servizio ["+this.idServizio+"] indicato nella porta delegata ["+
										nomeUtilizzatoPerErrore+"]: "+bf.toString());
							}
							idPA = this.configurazionePdDReader.convertToIDPortaApplicativa(list.get(0));
						}catch(DriverConfigurazioneNotFound n){
							erroreConfigurazione = "Non esiste alcuna porta applicativa indirizzabile tramite il servizio ["+this.idServizio+"] indicato nella porta delegata ["+
									nomeUtilizzatoPerErrore+"]";
						}catch(Exception e){
							erroreConfigurazione = e.getMessage();
						}
					}
					else{
						try{
							idPA = this.configurazionePdDReader.getIDPortaApplicativa(nomePA, requestInfo, this.protocolFactory);
						}catch(Exception e){
							erroreConfigurazione = e.getMessage();
						}
					}
				}
				
				if(erroreConfigurazione==null){
					ra = new RichiestaApplicativa(this.soggettoFruitore,this.idServizio.getSoggettoErogatore(), idPA);
					ra.setIntegrazione(this.msgContext.getIntegrazione());
					ra.setProtocol(this.msgContext.getProtocol());
					pa = this.configurazionePdDReader.getPortaApplicativaSafeMethod(ra.getIdPortaApplicativa(), requestInfo);
					if(pa.sizeServizioApplicativoList()<=0){
						erroreConfigurazione = "non risultano registrati servizi applicativi erogatori associati alla porta applicativa ("+pa.getNome()
								+") relativa al servizio richiesto";
					}
				}
				
				
				if(erroreConfigurazione!=null){
					erroreConfigurazione = prefix + erroreConfigurazione;
					this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO,erroreConfigurazione);
					this.msgDiag.logPersonalizzato("localForward.configError");
					
					this.openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
								ErroriIntegrazione.ERRORE_435_LOCAL_FORWARD_CONFIG_NON_VALIDA.
								getErrore435_LocalForwardConfigNonValida(erroreConfigurazione),null,null)));
					}
					return;
				}
				
				localForwardParameter = new LocalForwardParameter();
				localForwardParameter.setLog(this.logCore);
				localForwardParameter.setConfigurazionePdDReader(this.configurazionePdDReader);
				localForwardParameter.setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
				localForwardParameter.setIdentitaPdD(this.identitaPdD);
				localForwardParameter.setIdModulo(this.msgContext.getIdModulo());
				localForwardParameter.setIdRequest(this.idMessageRequest);
				localForwardParameter.setImplementazionePdDDestinatario(implementazionePdDDestinatario);
				localForwardParameter.setImplementazionePdDMittente(implementazionePdDMittente);
				localForwardParameter.setIdPdDMittente(idPdDMittente);
				localForwardParameter.setIdPdDDestinatario(idPdDDestinatario);
				localForwardParameter.setInfoServizio(infoServizio);
				localForwardParameter.setMsgDiag(this.msgDiag);
				localForwardParameter.setOpenspcoopstate(this.openspcoopstate);
				localForwardParameter.setPddContext(this.inRequestContext.getPddContext());
				localForwardParameter.setProtocolFactory(this.protocolFactory);
				localForwardParameter.setRichiestaDelegata(richiestaDelegata);
				localForwardParameter.setStateless(this.portaStateless);
				localForwardParameter.setOneWayVersione11(oneWayVersione11);
				localForwardParameter.setIdPortaApplicativaIndirizzata(idPA);
				
				localForwardEngine = new LocalForwardEngine(localForwardParameter);
							
			}
			
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneLocalForward");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_554_LOCAL_FORWARD_ERROR),e,null)));
			}
			return;
		}
		
		
		if(this.localForward){
			try {
				if(!localForwardEngine.processRequest(this.requestMessage)){
					this.openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse(localForwardEngine.getResponseMessageError());
					}
					return;
				}
				if(localForwardEngine.getRequestMessageAfterProcess()!=null){
					// Messaggio aggiornato
					this.requestMessage = localForwardEngine.getRequestMessageAfterProcess();
				}
			} catch (Exception e) {
				this.msgDiag.logErroreGenerico(e,"GestioneLocalForward.processRequest");
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_555_LOCAL_FORWARD_PROCESS_REQUEST_ERROR),e,null)));
				}
				return;
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Controllo presenza del messaggio gia' in gestione...");
		
		
		
		
		
		/* ---------------- Controllo esistenza messaggio --------------------- */
		this.msgDiag.mediumDebug("Controllo presenza del messaggio gia' in gestione...");
		this.msgRequest = null;
		String tipoMessaggio = Costanti.OUTBOX;
		if(this.localForward){
			tipoMessaggio = Costanti.INBOX;
		}
		this.msgRequest = new GestoreMessaggi(this.openspcoopstate, true,this.idMessageRequest, tipoMessaggio, this.msgDiag, this.inRequestContext.getPddContext());
		this.msgRequest.setOneWayVersione11(oneWayVersione11);
		RepositoryBuste repositoryBuste = new RepositoryBuste(this.openspcoopstate.getStatoRichiesta(), true, this.protocolFactory);
		try {
			if (this.msgRequest.existsMessage_noCache()) {

				// Se il proprietario attuale e' GestoreMessaggi, forzo
				// l'eliminazione e continuo a processare il messaggio.
				String proprietarioMessaggio = this.msgRequest.getProprietario(this.msgContext.getIdModulo());
				if (TimerGestoreMessaggi.ID_MODULO.equals(proprietarioMessaggio)) {
					this.msgDiag.logPersonalizzato("messaggioInGestione.marcatoDaEliminare");
					String msg = this.msgDiag.getMessaggio_replaceKeywords("messaggioInGestione.marcatoDaEliminare");
					if(propertiesReader.isMsgGiaInProcessamentoUseLock()) {
						this.msgRequest._deleteMessageWithLock(msg,propertiesReader.getMsgGiaInProcessamentoAttesaAttiva(),propertiesReader.getMsgGiaInProcessamentoCheckInterval());
					}
					else {
						this.msgRequest.deleteMessageByNow();
					}
				}

				// Altrimenti genero errore messaggio precedente ancora in
				// processamento
				else {
					this.msgDiag.addKeyword(CostantiPdD.KEY_PROPRIETARIO_MESSAGGIO, proprietarioMessaggio);
					this.msgDiag.logPersonalizzato("messaggioInGestione");
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.RICHIESTA_DUPLICATA, "true");
					this.openspcoopstate.releaseResource(); 
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.CONFLICT_IN_QUEUE,
								ErroriIntegrazione.ERRORE_537_BUSTA_GIA_RICEVUTA.get537_BustaGiaRicevuta(this.idMessageRequest),null,null)));
					}
					return;
				}
			}
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"ControlloPresenzaMessaggioGiaInGestione");
			logError(this.logCore, "Controllo/gestione presenza messaggio gia in gestione non riuscito",e);
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_537_BUSTA_GIA_RICEVUTA), e,null)));
			}
			return;
		}
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Registrazione messaggio di richiesta nel RepositoryMessaggi...");
		
		
		
		
		
		/*
		 * ---------------- Creo sessione di gestione del messaggio ricevuto Il
		 * messaggio viene assegnato al modulo 'Imbustamento' (futuro
		 * proprietario) ---------------------
		 */
		this.msgDiag.mediumDebug("Registrazione messaggio di richiesta nel RepositoryMessaggi...");
		IProtocolVersionManager moduleManager = this.protocolFactory.createProtocolVersionManager(richiestaDelegata.getProfiloGestione());
		this.richiestaAsincronaSimmetricaStateless = false;		
		try {
			
			// In caso di richiestaAsincronaSimmetrica e openspcoop stateless,
			// Devo comunque salvare le informazioni sul msg della richiesta.
			// Tali informazioni servono per il check nel modulo RicezioneBuste, per verificare di gestire la risposta
			// solo dopo aver terminato di gestire la richiesta con relativa ricevuta.
			if(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione()) && this.portaStateless){
				
				if (StatoFunzionalitaProtocollo.ABILITATA.equals(moduleManager.getCollaborazione(infoServizio))) {
					// Se presente riferimentoMessaggio utilizzo quello come riferimento asincrono per la risposta.
					if (this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null) {
						this.richiestaAsincronaSimmetricaStateless = false;
					} else if (this.headerIntegrazioneRichiesta.getBusta().getIdCollaborazione() != null) {
						// Utilizzo Collaborazione come riferimentoServizioCorrelato
						// Tanto nelle linee guida non possono esistere piu' istanze con la stessa collaborazione, e' stata deprecata.
						// Per igni istanza asincrona (richiesta/risposta) la richiesta genera una collaborazione a capostipite
						this.richiestaAsincronaSimmetricaStateless = false;
					}else{
						this.richiestaAsincronaSimmetricaStateless = true;
					}
				} else {
					this.richiestaAsincronaSimmetricaStateless = this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() == null;
				}

			}
			
			
			// Salvataggio messaggio
			this.msgRequest.registraMessaggio(this.requestMessage, dataIngressoRichiesta,
					(oneWayStateless || sincronoStateless || asincronoStateless),
					idCorrelazioneApplicativa);	
			if(this.localForward){
				this.msgRequest.aggiornaProprietarioMessaggio(org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi.ID_MODULO);
			}else{
				this.msgRequest.aggiornaProprietarioMessaggio(org.openspcoop2.pdd.mdb.Imbustamento.ID_MODULO);
			}
						
			if(this.richiestaAsincronaSimmetricaStateless){
				if(this.openspcoopstate.resourceReleased()) {
					// inizializzo
					this.openspcoopstate.setUseConnection(true);
					this.openspcoopstate.initResource(this.identitaPdD, this.msgContext.getIdModulo(), idTransazione);
				}
				this.msgRequest.registraInformazioniMessaggio_statelessEngine(dataIngressoRichiesta, org.openspcoop2.pdd.mdb.Imbustamento.ID_MODULO,
						idCorrelazioneApplicativa);
			}
			
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"msgRequest.aggiornaProprietarioMessaggio");
			this.msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata su fileSystem
			// Rilascio Connessione DB
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_508_SAVE_REQUEST_MSG), e,null)));
			}
			return;
		}

		this.msgDiag.mediumDebug("Registrazione busta di richiesta nel RepositoryBuste...");
		try {
			if( (!this.portaStateless) || oneWayVersione11){
				// E' gia registrata se siamo in un contesto di correlazione applicativa				
				if (repositoryBuste.isRegistrata(this.idMessageRequest,tipoMessaggio)) {
					try{
						if(this.localForward){
							repositoryBuste.aggiornaBustaIntoInBox(this.idMessageRequest, this.soggettoFruitore, richiestaDelegata.getIdServizio(), 
									propertiesReader.getRepositoryIntervalloScadenzaMessaggi(), 
									infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
						}else{
							repositoryBuste.aggiornaBustaIntoOutBox(this.idMessageRequest, this.soggettoFruitore, richiestaDelegata.getIdServizio(),
									propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),
									infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
						}
						repositoryBuste.impostaUtilizzoPdD(this.idMessageRequest,tipoMessaggio);
					}catch(Exception e){
						if(propertiesReader.isMsgGiaInProcessamentoUseLock()) {
							String tipo = Costanti.OUTBOX;
							if(this.localForward){
								tipo = Costanti.INBOX;	
							}
							String causa = "Aggiornamento dati busta con id ["+this.idMessageRequest+"] tipo["+tipo+"] non riuscito: "+e.getMessage();
							try{
								GestoreMessaggi.acquireLock(this.msgRequest,TimerLock.newInstance(TipoLock._getLockGestioneRepositoryMessaggi()),this.msgDiag, causa, propertiesReader.getMsgGiaInProcessamentoAttesaAttiva(), propertiesReader.getMsgGiaInProcessamentoCheckInterval());
								// errore che puo' avvenire a causa del Timer delle Buste (vedi spiegazione in classe GestoreMessaggi.deleteMessageWithLock)
								// Si riesegue tutto il codice isRegistrata e update o create con il lock. Stavolta se avviene un errore non e' dovuto al timer.
								if (repositoryBuste.isRegistrata(this.idMessageRequest,tipoMessaggio)) {
									if(this.localForward){
										repositoryBuste.aggiornaBustaIntoInBox(this.idMessageRequest, this.soggettoFruitore, richiestaDelegata.getIdServizio(), 
												propertiesReader.getRepositoryIntervalloScadenzaMessaggi(), 
												infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
									}else{
										repositoryBuste.aggiornaBustaIntoOutBox(this.idMessageRequest, this.soggettoFruitore, richiestaDelegata.getIdServizio(),
												propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),
												infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
									}
									repositoryBuste.impostaUtilizzoPdD(this.idMessageRequest,tipoMessaggio);
								}
								else {
									if(this.localForward){
										repositoryBuste.registraBustaIntoInBox(this.idMessageRequest, this.soggettoFruitore, richiestaDelegata.getIdServizio(),
												propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),
												infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
									}
									else{
										repositoryBuste.registraBustaIntoOutBox(this.idMessageRequest,this.soggettoFruitore, richiestaDelegata.getIdServizio(),
												propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),
												infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
									}
								}
							}finally{
								try{
									GestoreMessaggi.releaseLock(this.msgRequest,TimerLock.newInstance(TipoLock._getLockGestioneRepositoryMessaggi()),this.msgDiag, causa);
								}catch(Exception eUnlock){
									// ignore
								}
							}
						}
						else {
							throw e;
						}
					}
				} else {
					if(this.localForward){
						repositoryBuste.registraBustaIntoInBox(this.idMessageRequest, this.soggettoFruitore, richiestaDelegata.getIdServizio(),
								propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),
								infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
					}
					else{
						repositoryBuste.registraBustaIntoOutBox(this.idMessageRequest,this.soggettoFruitore, richiestaDelegata.getIdServizio(),
								propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),
								infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
					}
				}
				Integrazione infoIntegrazione = new Integrazione();
				infoIntegrazione.setIdModuloInAttesa(this.msgContext.getIdModulo());
				infoIntegrazione.setNomePorta(richiestaDelegata.getIdPortaDelegata().getNome());
				infoIntegrazione.setServizioApplicativo(richiestaDelegata.getServizioApplicativo());
				repositoryBuste.aggiornaInfoIntegrazione(this.idMessageRequest,tipoMessaggio,infoIntegrazione);
			}
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"registrazioneAggiornamentoBusta");
			this.msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata su fileSystem
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO), e,null)));
			}
			return;
		}

		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Invio messaggio al modulo di Imbustamento...");
		
		
		
		
		
		
		/* ------------ Spedizione a modulo Imbustamento/ConsegnaContenutiApplicativi ------------- */
		String nextModulo = org.openspcoop2.pdd.mdb.Imbustamento.ID_MODULO;
		if(this.localForward){
			this.msgDiag.addKeyword(CostantiPdD.KEY_PORTA_APPLICATIVA,pa.getNome());
			if(this.idServizio.getAzione()==null){
				this.msgDiag.addKeyword(CostantiPdD.KEY_AZIONE_BUSTA_RICHIESTA,"non presente");
			}
			this.msgDiag.logPersonalizzato("localForward.logInfo");
			nextModulo = ConsegnaContenutiApplicativi.ID_MODULO;
		}else{
			this.msgDiag.mediumDebug("Invio messaggio al modulo di Imbustamento...");
		}
		try {
			
			// set tipologia di filtro duplicati
			if(proprietaPorta!=null && !proprietaPorta.isEmpty()) {
				boolean filtroDuplicatiTestEnabled = CostantiProprieta.isFiltroDuplicatiTestEnabled(proprietaPorta, false); // filtro duplicati usato per test
				if(filtroDuplicatiTestEnabled) {
					pddContext.addObject(CostantiPdD.FILTRO_DUPLICATI_TEST, filtroDuplicatiTestEnabled);
				}
			}
			
			// Creazione ImbustamentoMessage
			this.msgDiag.highDebug("Creazione ObjectMessage for send nell'infrastruttura.");

			Serializable msgJMS = null;
			
			if(this.localForward){
			
				localForwardParameter.setRepositoryBuste(repositoryBuste);
				String portaDelegataAttuale = localForwardParameter.getMsgDiag().getPorta();
				localForwardParameter.getMsgDiag().updatePorta(TipoPdD.APPLICATIVA,localForwardParameter.getIdPortaApplicativaIndirizzata().getNome(), requestInfo);
				localForwardEngine.updateLocalForwardParameter(localForwardParameter);
				
				localForwardEngine.sendRequest(this.msgRequest);
				
				// ripristino
				localForwardParameter.getMsgDiag().updatePorta(TipoPdD.DELEGATA,portaDelegataAttuale, requestInfo);
				
			}
			else{
				this.imbustamentoMSG.setRichiestaDelegata(richiestaDelegata);
				this.imbustamentoMSG.setInfoServizio(infoServizio);
				this.imbustamentoMSG.setOneWayVersione11(oneWayVersione11);
				if (this.headerIntegrazioneRichiesta.getBusta() != null) {
					// RiferimentoServizioCorrelato
					String riferimentoServizioCorrelato = moduleManager.getIdCorrelazioneAsincrona(
							this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio(), this.headerIntegrazioneRichiesta.getBusta().getIdCollaborazione());
					if (riferimentoServizioCorrelato != null) {
						// Se presente riferimentoMessaggio utilizzo quello.
						this.imbustamentoMSG.setRiferimentoServizioCorrelato(riferimentoServizioCorrelato);
					}
					// Collaborazione
					if (this.headerIntegrazioneRichiesta.getBusta().getIdCollaborazione() != null)
						this.imbustamentoMSG.setIdCollaborazione(this.headerIntegrazioneRichiesta.getBusta().getIdCollaborazione());
					// RiferimentoMessaggio
					if (this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null)
						this.imbustamentoMSG.setIdRiferimentoMessaggio(this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio());
				}
				// Implemnentazione della porta erogatrice
				this.imbustamentoMSG.setImplementazionePdDSoggettoMittente(implementazionePdDMittente);
				this.imbustamentoMSG.setImplementazionePdDSoggettoDestinatario(implementazionePdDDestinatario);
				// Indirizzo soggetti
				this.imbustamentoMSG.setIndirizzoSoggettoMittente(indirizzoFruitore);
				this.imbustamentoMSG.setIndirizzoSoggettoDestinatario(indirizzoErogatore);
				// PddContext
				this.imbustamentoMSG.setPddContext(this.inRequestContext.getPddContext());
			
				msgJMS = this.imbustamentoMSG;
			}

			if (!this.portaStateless) {
				logDebug(this.logCore, RicezioneContenutiApplicativi.ID_MODULO+ " :eseguo send verso "+nextModulo+"...");
				
				String classTypeNodeSender = null;
				INodeSender nodeSender = null;
				try {
					classTypeNodeSender = className.getNodeSender(propertiesReader.getNodeSender());
					nodeSender = (INodeSender) loader.newInstance(classTypeNodeSender);
				} catch (Exception e) {
					throw new CoreException(
							"Riscontrato errore durante il caricamento della classe ["+ classTypeNodeSender
									+ "] da utilizzare per la spedizione nell'infrastruttura: "	+ e.getMessage());
				}
				
				// send JMS solo STATEFUL
				nodeSender.send(msgJMS,nextModulo, this.msgDiag,
						this.identitaPdD, this.msgContext.getIdModulo(), this.idMessageRequest, this.msgRequest);
				logDebug(this.logCore, RicezioneContenutiApplicativi.ID_MODULO+ " :send verso "+nextModulo+" effettuata");
			}

		} catch (Exception e) {
			logError(this.logCore, "Spedizione->"+nextModulo+" non riuscita",e);
			this.msgDiag.logErroreGenerico(e,"GenericLib.nodeSender.send("+nextModulo+")");
			this.msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata precedentemente
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_512_SEND),e,null)));
			}
			return;
		}

		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Commit delle operazioni per la gestione della richiesta...");
		
		
		
		
		/* ------------ Commit/Rilascia connessione al DB ------------- */
		this.msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta...");
		try {
			// Commit
			this.openspcoopstate.commit();
			logDebug(this.logCore, RicezioneContenutiApplicativi.ID_MODULO+ " :RicezioneContenutiApplicativi commit eseguito");
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"openspcoopstate.commit");
			this.msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata precedentemente
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_506_COMMIT_JDBC),e,null)));
			}
			return;
		}

		if (!this.portaStateless) {
			// Aggiornamento cache messaggio
			if (this.msgRequest != null)
				this.msgRequest.addMessaggiIntoCache_readFromTable(RicezioneContenutiApplicativi.ID_MODULO, "richiesta");
			// Aggiornamento cache proprietario messaggio
			if (this.msgRequest != null)
				this.msgRequest.addProprietariIntoCache_readFromTable(RicezioneContenutiApplicativi.ID_MODULO, "richiesta",null, false);

			// Rilascia connessione al DB
			this.msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta effettuato, rilascio della connessione...");
			this.openspcoopstate.releaseResource();
		}

		
		
		
		
	
		
		
		
		/* ---------- Parametri Gestione risposta ------------- */
		this.parametriGestioneRisposta =
			new RicezioneContenutiApplicativiGestioneRisposta();

		this.parametriGestioneRisposta.setPropertiesReader(propertiesReader);
				
		this.parametriGestioneRisposta.setPddContext(this.inRequestContext.getPddContext());
				
		

		
		
		
		/*
		 * ---------------- STATELESS OR Stateful v11 -------------
		 */
		if (this.portaStateless) {
			
			//  Durante le invocazioni non deve essere utilizzata la connessione al database 
			(this.openspcoopstate).setUseConnection(false);
			boolean resultRequest = comportamentoStatelessRichiesta(this.imbustamentoMSG);
			if (!resultRequest){
				this.openspcoopstate.releaseResource();
				return;
			}
			/**else{
				// ripristino utilizzo connessione al database
				((OpenSPCoopStateless)this.openspcoopstate).setUseConnection(true);
			}*/
		}
		
		if(this.asyncResponseCallback==null) {
			this.statelessComplete(false);
		}
	}
	
	private void statelessComplete(boolean invokedFromAsyncConnector) {
		
		try {
		
		/*
		 * ---------------- STATELESS OR Stateful v11 -------------
		 */
		if (this.portaStateless) {
			
			boolean resultResponse = comportamentoStatelessRisposta(this.imbustamentoMSG);
			if (!resultResponse){
				this.openspcoopstate.releaseResource();
				return;
			}
				
			// ripristino utilizzo connessione al database
			(this.openspcoopstate).setUseConnection(true);
		}
			
		// refresh risorse con nuovi stati
		this.registroServiziReader = this.registroServiziReader.refreshState(this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta());
		this.configurazionePdDReader = this.configurazionePdDReader.refreshState(this.registroServiziReader);
		this.msgDiag.updateState(this.configurazionePdDReader);
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Gestione risposta...");
		
		
		/* ------------ GestioneRisposta non effettuata ------------- */
		this.msgDiag.mediumDebug("Gestione risposta...");
		if (!this.msgContext.isGestioneRisposta()) {
			if(this.portaStateless)
				this.openspcoopstate.releaseResource();
			return;
		}

		gestioneRisposta();
		this.msgDiag.mediumDebug("Lavoro Terminato.");

		}finally{ // try vedi  #try-finally-openspcoopstate#
			try{
				if(this.openspcoopstate!=null){
					this.openspcoopstate.forceFinallyReleaseResource();
				}
			}catch(Throwable e){
				if(this.msgDiag!=null){
					try{
						this.msgDiag.logErroreGenerico(e, "Rilascio risorsa");
					}catch(Throwable eLog){
						logError(this.logCore, "Diagnostico errore per Rilascio risorsa: "+eLog.getMessage(),eLog);
					}
				}
				else{
					logError(this.logCore, "Rilascio risorsa: "+e.getMessage(),e);
				}
			}
			
			this._processComplete(invokedFromAsyncConnector);
		}
	}

	
	private void setCredenziali(Credenziali credenziali,MsgDiagnostico msgDiag){
		if (credenziali != null) {
			if (credenziali.getUsername() != null){
				msgDiag.setServizioApplicativo("username("+ credenziali.getUsername() + ")");
			}else if (credenziali.getSubject() != null){
				msgDiag.setServizioApplicativo("subject("+ credenziali.getSubject() + ")");
			}else if (credenziali.getPrincipal() != null){
				msgDiag.setServizioApplicativo("principal("+ credenziali.getPrincipal() + ")");
			}
			else{
				msgDiag.setServizioApplicativo(null);
			}
		}
		
		String credenzialiFornite = "";
		if(credenziali!=null){
			credenzialiFornite = credenziali.toString();
		}
		msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, credenzialiFornite);
	}
	
	
	/*
	 * Gestione stateless
	 */
	
	private EsitoLib esitoStatelessAfterSendRequest;
	
	private boolean comportamentoStatelessRichiesta(ImbustamentoMessage imbustamentoMSG)  {

		
		/* ------- Lettura parametri ---------- */
		
		EsitoLib esito;
		
		OpenSPCoopStateless openspcoopstateless = (OpenSPCoopStateless) this.openspcoopstate;
		
		OpenSPCoop2Properties propertiesReader = this.parametriGestioneRisposta.getPropertiesReader();
		
		this.portaStateless = true;
		
		boolean rinegoziamentoConnessione = 
			propertiesReader.isRinegoziamentoConnessione(this.msgContext.getProtocol().getProfiloCollaborazione()) && (!this.oneWayVers11);
		
		// ID Transazione
		@SuppressWarnings("unused")
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, imbustamentoMSG.getPddContext());
		
		PdDContext pddContext = this.parametriGestioneRisposta.getPddContext();
		
		if(this.localForward){
			
			
			// E' RicezioneContenutiApplicativi se siamo in oneway11 con presa in carico
			if( ConsegnaContenutiApplicativi.ID_MODULO.equals( (openspcoopstateless).getDestinatarioRequestMsgLib() )
					&&
				 (openspcoopstateless).getDestinatarioResponseMsgLib()==null ){
						
				
				
				/* ------------ Rilascio risorsa se e' presente rinegoziamento delle risorse ------------------ */
				// Rinegozio la connessione SOLO se siamo in oneway o sincrono stateless puro (non oneway11)
				if( rinegoziamentoConnessione ){
					this.msgDiag.highDebug("ConsegnaContenutiApplicativi stateless (commit) ...");
					openspcoopstateless.setUseConnection(true);
					try{
						openspcoopstateless.commit();
					}catch(Exception e){
						// ignore
					}
					openspcoopstateless.releaseResource();
					openspcoopstateless.setUseConnection(false);
				}
				
				
				
				/*
				 * ---------------------- CONSEGNA CONTENUTI APPLICATIVI ------------------
				 */
		
				ConsegnaContenutiApplicativi consegnaContenutiLib = null;
				try {
					consegnaContenutiLib = new ConsegnaContenutiApplicativi(this.logCore);
					esito = consegnaContenutiLib.onMessage(openspcoopstateless, this.registroServiziReader, this.configurazionePdDReader);
					if(esito.getStatoInvocazione()==EsitoLib.ERRORE_NON_GESTITO){
						if(esito.getErroreNonGestito()!=null)
							throw esito.getErroreNonGestito();
						else
							throw new CoreException(ERRORE_NON_GESTITO);
					}
				} catch (Throwable e) {
					this.msgDiag.logErroreGenerico(e,"Stateless.ConsegnaContenutiApplicativi");
					logError(this.logCore, "Errore Generale durante la gestione stateless (ConsegnaContenutiApplicativi): "+e.getMessage(),e);
					this.msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata precedentemente
					openspcoopstateless.setUseConnection(true);
					openspcoopstateless.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,null)));
					}
					return false;
				}
				
				if (esito.getStatoInvocazione() != EsitoLib.OK) {
					// ripristino utilizzo connessione al database
					openspcoopstateless.setUseConnection(true);
					gestioneRisposta();
					return false;
				}
				
			}
	

			
		}
		
		else{ 
			/*------------------------------- IMBUSTAMENTO -------------------------------------------*/
			this.msgDiag.highDebug("Imbustamento stateless ...");
			org.openspcoop2.pdd.mdb.Imbustamento imbustamentoLib = null;
			openspcoopstateless.setMessageLib(imbustamentoMSG);
			openspcoopstateless.setIDMessaggioSessione(this.idMessageRequest);
			try {
				imbustamentoLib = new org.openspcoop2.pdd.mdb.Imbustamento(this.logCore);
				this.msgDiag.highDebug("Imbustamento stateless (invoco) ...");
				esito = imbustamentoLib.onMessage(openspcoopstateless, this.registroServiziReader, this.configurazionePdDReader);
				this.msgDiag.highDebug("Imbustamento stateless (analizzo esito) ...");
				if(esito.getStatoInvocazione()==EsitoLib.ERRORE_NON_GESTITO){
					if(esito.getErroreNonGestito()!=null)
						throw esito.getErroreNonGestito();
					else
						throw new CoreException(ERRORE_NON_GESTITO);
				}
			} catch (Throwable e) {
				this.msgDiag.logErroreGenerico(e,"Stateless.Imbustamento");
				logError(this.logCore, "Errore Generale durante la gestione stateless (Imbustamento): "+e.getMessage(),e);
				this.msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata precedentemente
				openspcoopstateless.setUseConnection(true);
				openspcoopstateless.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,null)));
				}
				return false;
			}
	
			// Se la chiamata alla libreria imbustamento non ha dato errori procedo con le altre librerie
			// Altrimenti faccio gestire l'errore prodotto dalla libreria
			if (esito.getStatoInvocazione() != EsitoLib.OK) {
				// ripristino utilizzo connessione al database
				openspcoopstateless.setUseConnection(true);
				gestioneRisposta();
				this.msgDiag.highDebug("Imbustamento stateless (terminato:false)");
				return false;
			}
	
			// Gestione oneway versione 11
			if (openspcoopstateless.getDestinatarioResponseMsgLib() != null
					&& openspcoopstateless.getDestinatarioResponseMsgLib().startsWith(RicezioneContenutiApplicativi.ID_MODULO)
					&& propertiesReader.isGestioneOnewayStateful_1_1()){
				this.msgDiag.highDebug("Imbustamento stateless (terminato:true)");
				return true;
			}
	
			/* ------------ Rilascio risorsa se e' presente rinegoziamento delle risorse ------------------ */
			// Rinegozio la connessione SOLO se siamo in oneway o sincrono stateless puro (non oneway11)
			if( rinegoziamentoConnessione ){
				this.msgDiag.highDebug("Imbustamento stateless (commit) ...");
				openspcoopstateless.setUseConnection(true);
				try{
					openspcoopstateless.commit();
				}catch(Exception e){
					// ignore
				}
				openspcoopstateless.releaseResource();
				openspcoopstateless.setUseConnection(false);
			}
			
			this.msgDiag.highDebug("Imbustamento stateless terminato");
			
			
			
			
			/*
			 * ---------------------- INOLTRO BUSTE ------------------
			 */
	
			this.msgDiag.highDebug("InoltroBuste stateless ...");
			InoltroBuste inoltroBusteLib = null;
			try {
				inoltroBusteLib = new InoltroBuste(this.logCore);
				this.msgDiag.highDebug("InoltroBuste stateless (invoco) ...");
				this.esitoStatelessAfterSendRequest = inoltroBusteLib.onMessage(openspcoopstateless, this.registroServiziReader, this.configurazionePdDReader, 
						this.asyncResponseCallback!=null ? this : null);
				if(this.asyncResponseCallback!=null) {
					this.asyncWait = true;
				}
			} catch (Throwable e) {
				this.msgDiag.logErroreGenerico(e,"Stateless.InoltroBuste");
				this.logCore.error("Errore Generale durante la gestione stateless (InoltroBuste): "+e.getMessage(),e);
				this.msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata precedentemente
				openspcoopstateless.setUseConnection(true);
				openspcoopstateless.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,null)));
				}
				return false;
			}
		}
		
		return true;
	}
	
	
	private boolean comportamentoStatelessRisposta(ImbustamentoMessage imbustamentoMSG) {
							
		if(!this.localForward){
		
			
			/* ------- Lettura parametri ---------- */
			
			EsitoLib esito;
			
			OpenSPCoopStateless openspcoopstateless = (OpenSPCoopStateless) this.openspcoopstate;
		
			// Gestione oneway versione 11
			if (openspcoopstateless.getDestinatarioResponseMsgLib()!=null &&
					openspcoopstateless.getDestinatarioResponseMsgLib().startsWith(
					RicezioneContenutiApplicativi.ID_MODULO)){
				this.msgDiag.highDebug("Imbustamento not stateless (terminato:true)");
				return true;
			}
			
			OpenSPCoop2Properties propertiesReader = this.parametriGestioneRisposta.getPropertiesReader();
						
			this.portaStateless = true;
			
			boolean rinegoziamentoConnessione = 
				propertiesReader.isRinegoziamentoConnessione(this.msgContext.getProtocol().getProfiloCollaborazione()) && (!this.oneWayVers11);
	
			@SuppressWarnings("unused")
			String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, imbustamentoMSG.getPddContext());
			
			PdDContext pddContext = this.parametriGestioneRisposta.getPddContext();
			
			/*
			 * ---------------------- INOLTRO BUSTE ------------------
			 */
	
			this.msgDiag.highDebug("InoltroBuste stateless ...");
			try {
				esito = this.esitoStatelessAfterSendRequest;
				this.msgDiag.highDebug("InoltroBuste stateless (analizzo esito) ...");
				if(esito.getStatoInvocazione()==EsitoLib.ERRORE_NON_GESTITO){
					if(esito.getErroreNonGestito()!=null)
						throw esito.getErroreNonGestito();
					else
						throw new CoreException(ERRORE_NON_GESTITO);
				}
			} catch (Throwable e) {
				this.msgDiag.logErroreGenerico(e,"Stateless.InoltroBuste");
				this.logCore.error("Errore Generale durante la gestione stateless: "+e.getMessage(),e);
				this.msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata precedentemente
				openspcoopstateless.setUseConnection(true);
				openspcoopstateless.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,null)));
				}
				return false;
			}
			
			if (esito.getStatoInvocazione() != EsitoLib.OK) {
				// ripristino utilizzo connessione al database
				openspcoopstateless.setUseConnection(true);
				gestioneRisposta();
				this.msgDiag.highDebug("InoltroBuste stateless (terminato:false)");
				return false;
			}
	
			// Gestione oneway versione 11
			if (openspcoopstateless.getDestinatarioResponseMsgLib()!=null &&
					openspcoopstateless.getDestinatarioResponseMsgLib().startsWith(
					RicezioneContenutiApplicativi.ID_MODULO)){
				this.msgDiag.highDebug("InoltroBuste stateless (terminato:true)");
				return true;
			}
			
			this.msgDiag.highDebug("InoltroBuste stateless terminato");
			
			
			
			
	
			/*--------------- SBUSTAMENTO RISPOSTE ---------------- */
	
			this.msgDiag.highDebug("SbustamentoRisposte stateless ...");
			SbustamentoRisposte sbustamentoRisposteLib = null;
			boolean erroreSbustamentoRisposta = false;
			try {
				sbustamentoRisposteLib = new SbustamentoRisposte(this.logCore);
				/* Verifico che non abbia rilasciato la connessione, se si la riprendo */
				if( rinegoziamentoConnessione && openspcoopstateless.resourceReleased()){
					/** per default disabilitato 
					msgDiag.highDebug("SbustamentoRisposte stateless (initResourceDB) ...");
					openspcoopstate.setUseConnection(true);
					openspcoopstate.initResource(parametriGestioneRisposta.getIdentitaPdD(), SbustamentoRisposte.ID_MODULO, idTransazione);
					openspcoopstate.setUseConnection(false);
					 */
					// update states
					this.registroServiziReader = this.registroServiziReader.refreshState(openspcoopstateless.getStatoRichiesta(),openspcoopstateless.getStatoRisposta());
					this.configurazionePdDReader = this.configurazionePdDReader.refreshState(this.registroServiziReader);
					this.msgDiag.updateState(this.configurazionePdDReader);
				}
				this.msgDiag.highDebug("SbustamentoRisposte stateless (invoco) ...");
				esito = sbustamentoRisposteLib.onMessage(openspcoopstateless, this.registroServiziReader, this.configurazionePdDReader);
				this.msgDiag.highDebug("SbustamentoRisposte stateless (analizzo esito) ...");
				if(esito.getStatoInvocazione()==EsitoLib.ERRORE_NON_GESTITO){
					if(esito.getErroreNonGestito()!=null)
						throw esito.getErroreNonGestito();
					else
						throw new CoreException(ERRORE_NON_GESTITO);
				}
			} catch (Throwable e) {
				erroreSbustamentoRisposta = true;
				this.msgDiag.logErroreGenerico(e,"Stateless.SbustamentoRisposte");
				logError(this.logCore, "Errore Generale durante la gestione stateless: "+e.getMessage(),e);
				this.msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata precedentemente
				openspcoopstateless.setUseConnection(true);
				openspcoopstateless.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,null)));
				}
				return false;
			}finally{
				/* Se devo rinegoziare la connessione, la rilascio */
				if( (rinegoziamentoConnessione) && (!erroreSbustamentoRisposta) ){
					this.msgDiag.highDebug("SbustamentoRisposte stateless (commit) ...");
					openspcoopstateless.setUseConnection(true);
					try{
						openspcoopstateless.commit();
					}catch(Exception e){
						// ignore
					}
					openspcoopstateless.releaseResource();
					openspcoopstateless.setUseConnection(false);
				}
			}
	
			if (esito.getStatoInvocazione() != EsitoLib.OK) {
				// ripristino utilizzo connessione al database
				openspcoopstateless.setUseConnection(true);
				gestioneRisposta();
				this.msgDiag.highDebug("SbustamentoRisposte stateless (terminato:false)");
				return false;
			}

			this.msgDiag.highDebug("SbustamentoRisposte stateless terminato");
			
		}
		
		

		return true;
	}

	
	
	
	
	
	
	
	
	
	
	/*--------------------------------------- GESTIONE RISPOSTA --------------------------- */

	/* Gestisce le risposte applicative di Ok o di errore */
	private void gestioneRisposta(){
			
			
		/* ------- Lettura parametri ---------- */
		OpenSPCoop2Properties propertiesReader = this.parametriGestioneRisposta.getPropertiesReader();
		
		PdDContext pddContext = this.parametriGestioneRisposta.getPddContext();
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext);
		RequestInfo requestInfo = (RequestInfo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
				
		Loader loader = Loader.getInstance();
		PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
		
		boolean errorOccursSetResponse = false;

		/* ------------ GestioneRisposta ------------- */
		String idMessageResponse = null;
		String idCollaborazioneResponse = null;
		ProfiloDiCollaborazione profiloCollaborazione = null;
		String profiloCollaborazioneValue = null;
		OpenSPCoop2Message responseMessage = null;
		String idCorrelazioneApplicativaRisposta = null;
		try {

			if (this.portaStateless || this.oneWayVers11) {
				
				// Gestione stateless
				
				RicezioneContenutiApplicativiMessage ricezioneContenutiApplicativiMSG = 
					(RicezioneContenutiApplicativiMessage) (this.openspcoopstate).getMessageLib();
				idMessageResponse = ricezioneContenutiApplicativiMSG.getIdBustaRisposta();
				idCollaborazioneResponse = ricezioneContenutiApplicativiMSG.getIdCollaborazione();
				profiloCollaborazione = ricezioneContenutiApplicativiMSG.getProfiloCollaborazione();
				profiloCollaborazioneValue = ricezioneContenutiApplicativiMSG.getProfiloCollaborazioneValue();

				responseMessage = ((OpenSPCoopStateless) this.openspcoopstate).getRispostaMsg();
				
				idCorrelazioneApplicativaRisposta = ((OpenSPCoopStateless) this.openspcoopstate).getIDCorrelazioneApplicativaRisposta();
				
				if(!ProfiloDiCollaborazione.ONEWAY.equals(profiloCollaborazione)){
					this.msgContext.getProtocol().setIdRisposta(idMessageResponse);
				}
				this.msgContext.getProtocol().setCollaborazione(idCollaborazioneResponse);

				// Aggiornamento Informazioni Protocollo
				this.msgDiag.setIdMessaggioRisposta(idMessageResponse);
				this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, idMessageResponse);
			}

			else {

				// Gestione stateful
				
				try {
					responseMessage = MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
							requestInfo.getIntegrationRequestMessageType(), MessageRole.RESPONSE);

					String classType = null;
					INodeReceiver nodeReceiver = null;
					try {
						classType = ClassNameProperties.getInstance().getNodeReceiver(propertiesReader.getNodeReceiver());
						nodeReceiver = (INodeReceiver) loader.newInstance(classType);
					} catch (Exception e) {
						throw new CoreException(
								"Riscontrato errore durante il caricamento della classe ["+ classType
										+ "] da utilizzare per la ricezione dall'infrastruttura: "+ e.getMessage());
					}
					
					this.msgDiag.mediumDebug("Attesa/lettura risposta...");
					RicezioneContenutiApplicativiMessage ricezioneContenutiApplicativiMSG = 
						(RicezioneContenutiApplicativiMessage) nodeReceiver.receive(
								this.msgDiag, this.identitaPdD,this.msgContext.getIdModulo(),this.idMessageRequest,
									propertiesReader.getNodeReceiverTimeoutRicezioneContenutiApplicativi(),
									propertiesReader.getNodeReceiverCheckInterval());
					idMessageResponse = ricezioneContenutiApplicativiMSG.getIdBustaRisposta();
					idCollaborazioneResponse = ricezioneContenutiApplicativiMSG.getIdCollaborazione();
					profiloCollaborazione = ricezioneContenutiApplicativiMSG.getProfiloCollaborazione();
					profiloCollaborazioneValue = ricezioneContenutiApplicativiMSG.getProfiloCollaborazioneValue();

					// aggiorno pddContext
					pddContext = ricezioneContenutiApplicativiMSG.getPddContext();
					if(pddContext!=null){
						List<MapKey<String>> enumPddContext = pddContext.keys();
						if(enumPddContext!=null && !enumPddContext.isEmpty()) {
							for (MapKey<String> key : enumPddContext) {
								/**System.out.println("AGGIORNO KEY CONTENTUI ["+key+"]");*/
								this.msgContext.getPddContext().addObject(key, pddContext.getObject(key));
							}
						}
					}
					
					if(!ProfiloDiCollaborazione.ONEWAY.equals(profiloCollaborazione)){
						this.msgContext.getProtocol().setIdRisposta(idMessageResponse);
					}
					this.msgContext.getProtocol().setCollaborazione(idCollaborazioneResponse);

				} catch (Exception e) {

					logError(this.logCore, "Gestione risposta ("+ this.msgContext.getIdModulo()+ ") con errore", e);
					this.msgDiag.logErroreGenerico(e,"GestioneRispostaErroreGenerale");
					
					// per la gestione del timeout ho bisogno di una connessione al database
					// In caso di Timeout elimino messaggi di richiesta ancora in processamento.
					if (e instanceof NodeTimeoutException) {

						// Get Connessione al DB
						try {
							this.openspcoopstate.updateResource(idTransazione);
						} catch (Exception eDB) {
							this.msgDiag.logErroreGenerico(e,"openspcoopstate.updateResource()");
							this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION),eDB,
									((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))));
							return;
						}

						// Eliminazione msg di richiesta
						try {
							this.msgDiag.logPersonalizzato("timeoutRicezioneRisposta");
							this.msgRequest.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
							this.openspcoopstate.commit();
						} catch (Exception eDel) {
							this.msgDiag.logErroreGenerico(eDel,"EliminazioneMessaggioScadutoTimeoutRicezioneRisposta");
						}
						// Rilascio connessione
						this.openspcoopstate.releaseResource();
					}

					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_513_RECEIVE), e, 
							((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))));
					return;
				}

				// Aggiornamento Informazioni Protocollo
				this.msgDiag.setIdMessaggioRisposta(idMessageResponse);
				this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, idMessageResponse);

				/* ------------ Re-ottengo Connessione al DB -------------- */
				this.msgDiag.mediumDebug("Richiesta connessione al database per la gestione della risposta...");
				try {
					this.openspcoopstate.updateResource(idTransazione);
				} catch (Exception e) {
					this.msgDiag.logErroreGenerico(e,"openspcoopstate.updateResource()");
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION), e,
							((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))));
					return;
				}
				this.msgRequest.updateOpenSPCoopState(this.openspcoopstate);

				/*
				 * ------------ Lettura Contenuto Messaggio (mapping in Message) --------------
				 */
				this.msgDiag.mediumDebug("Lettura messaggio di risposta...");
				GestoreMessaggi msgResponse = new GestoreMessaggi(this.openspcoopstate, false, idMessageResponse, Costanti.INBOX,this.msgDiag,this.msgContext.pddContext);
				try {
					responseMessage = msgResponse.getMessage();
					if(responseMessage!=null && this.msgContext.getPddContext()!=null) {
						Object o = responseMessage.getContextProperty(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO);
						if(o instanceof Boolean) {
							this.msgContext.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
						}
						o = responseMessage.getContextProperty(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
						if(o instanceof ParseException) {
							this.msgContext.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, o);
						}
						
						o = responseMessage.getContextProperty(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO);
						if(o instanceof Boolean) {
							this.msgContext.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
						}
						o = responseMessage.getContextProperty(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
						if(o instanceof ParseException) {
							this.msgContext.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, o);
						}
					}
					idCorrelazioneApplicativaRisposta = msgResponse.getIDCorrelazioneApplicativaRisposta();
					
					/**tempiAttraversamentoGestioneMessaggi = msgResponse.getTempiAttraversamentoPdD();
					if (tempiAttraversamentoGestioneMessaggi != null
							&& (tempiAttraversamentoGestioneMessaggi.getRicezioneMsgIngresso()==null || tempiAttraversamentoGestioneMessaggi.getSpedizioneMessaggioIngresso()==null)) {
						TempiAttraversamentoPDD dRichiesta = this.msgRequest.getTempiAttraversamentoPdD();
						if (dRichiesta != null) {
							tempiAttraversamentoGestioneMessaggi.setSpedizioneMessaggioIngresso(dRichiesta.getSpedizioneMessaggioIngresso());
						}
					}
					
					dimensioneMessaggiAttraversamentoGestioneMessaggi = msgResponse.getDimensioneMessaggiAttraversamentoPdD();*/

				} catch (GestoreMessaggiException e) {
					this.msgDiag.logErroreGenerico(e,"msgResponse.getMessage()");
					this.openspcoopstate.releaseResource();
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_511_READ_RESPONSE_MSG), e,
							((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))));
					return;
				}

			}
		} catch (Exception e) {
			logError(this.logCore, "ErroreGenerale", e);
			this.msgDiag.logErroreGenerico(e,"ErroreGenerale");
			this.openspcoopstate.releaseResource();
			this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(), e,
					((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))
					));
			errorOccursSetResponse = true;
		}

		
		
		/* ---- Aggiorno informazioni correlazione applicativa risposta ---- */
		this.msgDiag.setIdCorrelazioneRisposta(idCorrelazioneApplicativaRisposta);
		if(this.msgContext.getProtocol()!=null){
			this.msgContext.getProtocol().setProfiloCollaborazione(profiloCollaborazione, profiloCollaborazioneValue);
		}
		if(this.msgContext.getIntegrazione()!=null)
			this.msgContext.getIntegrazione().setIdCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
			
		
		
		
		/* ----- Header Integrazione ------ */
		this.msgDiag.mediumDebug("Gestione header di integrazione messaggio di risposta...");
		this.headerIntegrazioneRisposta.getBusta().setIdCollaborazione(idCollaborazioneResponse);
		this.headerIntegrazioneRisposta.getBusta().setProfiloDiCollaborazione(profiloCollaborazione);

		// -- REFRESH Impostation Risposta dell'Header Trasporto o se l'invocazione e' stata attiva dall'IntegrationManager --
		// Refresh necessario in seguito alla potenziale impostazione della collaborazione e Profilo di Collaborazione
		// ed eventuali altre future informazioni non subito disponibili
		
		String jtiIdModIRequest = null;
		Object bustaRispostaObject = null;
		if(pddContext!=null) {
			if(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.MODI_JTI_REQUEST_ID)) {
				jtiIdModIRequest = (String) pddContext.get(org.openspcoop2.core.constants.Costanti.MODI_JTI_REQUEST_ID);
			}
			if(pddContext.containsKey(CostantiPdD.BUSTA_RISPOSTA)) {
				bustaRispostaObject = pddContext.getObject(CostantiPdD.BUSTA_RISPOSTA);
			}
		}
		if(jtiIdModIRequest!=null && StringUtils.isNotEmpty(jtiIdModIRequest) && !jtiIdModIRequest.equals(this.idMessageRequest)) {
			this.headerIntegrazioneRisposta.getBusta().setID(jtiIdModIRequest);
		}
		else {
			this.headerIntegrazioneRisposta.getBusta().setID(this.idMessageRequest);
		}
		OutResponsePDMessage outResponsePDMessage = new OutResponsePDMessage();
		outResponsePDMessage.setBustaRichiesta(this.bustaRichiesta);
		Busta bustaRisposta = null;
		if(bustaRispostaObject instanceof Busta b){
			bustaRisposta = b;
		}
		else if(bustaRispostaObject == null && Costanti.SDI_PROTOCOL_NAME.equalsIgnoreCase(this.protocolFactory.getProtocol()) &&
			// Failover condizionale: se bustaRisposta è null e il protocollo è SDI,
			// verifica se esistono NomeFile e IdentificativoSdI salvati nel context
			// per permettere il recupero di questi valori anche in caso di errore
			pddContext != null) {
			// Recupera NomeFile
			if(pddContext.containsKey(CostantiPdD.BUSTA_RICHIESTA_SDI_NOME_FILE)) {
				Object nomeFileSDIObject = pddContext.getObject(CostantiPdD.BUSTA_RICHIESTA_SDI_NOME_FILE);
				if(nomeFileSDIObject instanceof String s) {
					outResponsePDMessage.getBustaRichiesta().addProperty("NomeFile", s);
				}
			}
			// Recupera IdentificativoSdI
			if(pddContext.containsKey(CostantiPdD.BUSTA_RICHIESTA_SDI_IDENTIFICATIVO_SDI)) {
				Object idSdiObject = pddContext.getObject(CostantiPdD.BUSTA_RICHIESTA_SDI_IDENTIFICATIVO_SDI);
				if(idSdiObject instanceof String s) {
					outResponsePDMessage.getBustaRichiesta().addProperty("IdentificativoSdI", s);
				}
			}
		}
		if(bustaRisposta != null &&
			// aggiungo proprieta' (vengono serializzate negli header di integrazione)
			bustaRisposta.sizeProperties()>0){
			String[]propertyNames = bustaRisposta.getPropertiesNames();
			for (int i = 0; i < propertyNames.length; i++) {
				outResponsePDMessage.getBustaRichiesta().addProperty(propertyNames[i], bustaRisposta.getProperty(propertyNames[i]));
			}
		}
		outResponsePDMessage.setMessage(responseMessage);
		outResponsePDMessage.setPortaDelegata(this.portaDelegata);
		Map<String, List<String>> propertiesIntegrazioneRisposta = new HashMap<>();
		outResponsePDMessage.setHeaders(propertiesIntegrazioneRisposta);
		outResponsePDMessage.setServizio(this.idServizio);
		outResponsePDMessage.setSoggettoMittente(this.soggettoFruitore);

		if (this.msgContext.getIdModulo().startsWith(RicezioneContenutiApplicativi.ID_MODULO+ IntegrationManager.ID_MODULO)) {
			try {
				IGestoreIntegrazionePD gestore = pluginLoader.newIntegrazionePortaDelegata(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO);
				if(gestore!=null){
					String classType = null;
					try {
						classType = gestore.getClass().getName();
						AbstractCore.init(gestore, pddContext, this.protocolFactory);
					} catch (Exception e) {
						throw new CoreException(
								"Riscontrato errore durante l'inizializzazione della classe (IGestoreIntegrazionePD) ["+ classType
										+ "] da utilizzare per la gestione dell'integrazione delle fruizioni (Risposta IM) di tipo ["+ CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO + "]: " + e.getMessage());
					}

					gestore.setOutResponseHeader(this.headerIntegrazioneRisposta, outResponsePDMessage);
				}
			} catch (Exception e) {
				this.msgDiag.logErroreGenerico(e,"setHeaderIntegrazioneRisposta");
			}
		}

		// HeaderIntegrazione
		for (int i = 0; i < this.tipiIntegrazionePD.length; i++) {
			try {
				IGestoreIntegrazionePD gestore = pluginLoader.newIntegrazionePortaDelegata(this.tipiIntegrazionePD[i]);
				if(gestore!=null){
					String classType = null;
					try {
						classType = gestore.getClass().getName();
						AbstractCore.init(gestore, pddContext, this.protocolFactory);
					} catch (Exception e) {
						throw new CoreException(
								"Riscontrato errore durante l'inizializzazione della classe (IGestoreIntegrazionePD) ["+ classType
										+ "] da utilizzare per la gestione dell'integrazione delle fruizioni (Risposta) di tipo ["+ this.tipiIntegrazionePD[i] + "]: " + e.getMessage());
					}
					if(gestore instanceof IGestoreIntegrazionePDSoap){
						if(propertiesReader.processHeaderIntegrazionePDResponse(false)){
							if(propertiesReader.deleteHeaderIntegrazioneResponsePD()){
								if(responseMessage==null){
									responseMessage = MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
											requestInfo.getIntegrationRequestMessageType(), MessageRole.RESPONSE);
									outResponsePDMessage.setMessage(responseMessage);
								}
								gestore.setOutResponseHeader(this.headerIntegrazioneRisposta,outResponsePDMessage);
							}else{
								// gia effettuato l'update dell'header in InoltroBuste
							}
						}else{
							if(responseMessage==null){
								responseMessage = MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
										requestInfo.getIntegrationRequestMessageType(), MessageRole.RESPONSE);
								outResponsePDMessage.setMessage(responseMessage);
							}
							gestore.setOutResponseHeader(this.headerIntegrazioneRisposta,outResponsePDMessage);
						}
					}else{
						gestore.setOutResponseHeader(this.headerIntegrazioneRisposta,outResponsePDMessage);
					}
				} else {
					throw new CoreException("Gestore non inizializzato");
				}
			} catch (Exception e) {
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,this.tipiIntegrazionePD[i]);
				this.msgDiag.addKeywordErroreProcessamento(e);
				this.msgDiag.logPersonalizzato("headerIntegrazione.creazioneFallita");
				logError(this.logCore, this.msgDiag.getMessaggio_replaceKeywords("headerIntegrazione.creazioneFallita"), e);
			}
		}

		// Imposto header di trasporto per la risposta
		this.msgContext.setResponseHeaders(propertiesIntegrazioneRisposta);
		
		
		
		
		
		
		
		/* ----- Eliminazione SIL (Stateful) ------ */
		
		if (!this.portaStateless)
			eliminaSIL((OpenSPCoopStateful) this.openspcoopstate, this.msgDiag, this.idMessageRequest, idMessageResponse,
					this.servizioApplicativo);

		
		
		
		/* ----- Aggiornamento proprietario (Stateless puro, no gestione oneway) ------ */
		if (this.portaStateless && !this.oneWayVers11) {
			this.msgDiag.mediumDebug("Aggiorno proprietario messaggio richiesta ...");
			try {
				/* Lo stateless che non è onewayVersione11 non salva niente su database */
				// A meno che non siamo in asincrono simmetrico richiesta stateless
				// In caso di richiestaAsincronaSimmetrica e openspcoop stateless,
				// Devo comunque salvare le informazioni sul msg della ricevuta alla richiesta.
				// Tali informazioni servono per il check nel modulo RicezioneBuste, per verificare di gestire la risposta
				// solo dopo aver terminato di gestire la richiesta con relativa ricevuta.
				if(this.richiestaAsincronaSimmetricaStateless){
					boolean resourceReleased = this.openspcoopstate.resourceReleased();
					if(resourceReleased){
						(this.openspcoopstate).setUseConnection(true);
						this.openspcoopstate.updateResource(idTransazione);
					}
					GestoreMessaggi msgResponse = new GestoreMessaggi(this.openspcoopstate, false, idMessageResponse, Costanti.INBOX,this.msgDiag,this.msgContext.pddContext);
					msgResponse.setReadyForDrop(true);
					msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
					msgResponse.setReadyForDrop(false);
					
					// Devo eliminare anche la richiesta (nel caso in cui non sia arrivata la validazione della ricevuta)
					this.msgRequest.updateOpenSPCoopState(this.openspcoopstate);
					this.msgRequest.setReadyForDrop(true);
					this.msgRequest.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
					this.msgRequest.setReadyForDrop(false);
				}
				
				// Committo modifiche (I commit servono per eventuali modifiche ai duplicati)
				this.openspcoopstate.commit();
				
			} catch (Exception e) {
				logError(this.logCore, "Errore durante l'aggiornamento del proprietario al GestoreMessaggi (Stateless)", e);
				this.msgDiag.logErroreGenerico(e, "openspcoopstate.commit(stateless risposta)");
				this.openspcoopstate.releaseResource();
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(), e,
						((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))));
				errorOccursSetResponse = true;
			}
		}

		
		
		/* ----- OneWay stateful/stateless ------ */
		
		if (this.oneWayVers11) {
			this.msgDiag.mediumDebug("Commit della gestione oneWay stateful/stateless...");
			try {
				// Committo modifiche
				this.openspcoopstate.commit();
			} catch (Exception e) {
				logError(this.logCore, "Riscontrato errore durante il commit della gestione oneWay stateful/stateless", e);
				this.msgDiag.logErroreGenerico(e, "openspcoopstate.commit(oneway1.1 risposta)");
				this.openspcoopstate.releaseResource();
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(), e,
						((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))));
				errorOccursSetResponse = true;
			}
		}

		
		
		
		
		/* ----- Terminazione gestione richiesta ------ */
		
		// Rilascio connessione al DB
		this.msgDiag.mediumDebug("Rilascio connessione al database...");
		this.openspcoopstate.releaseResource();

		// Risposta
		if (profiloCollaborazione != null) {
			if (profiloCollaborazione.equals(ProfiloDiCollaborazione.SINCRONO)) {
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "risposta sincrona");
				this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_RISPOSTA_APPLICATIVA);
			} else if (profiloCollaborazione.equals(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO)) {
				if (this.headerIntegrazioneRichiesta != null
						&& this.headerIntegrazioneRichiesta.getBusta() != null
						&& this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null) {
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "ricevuta di una risposta asincrona simmetrica");
				} else {
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "ricevuta di una richiesta asincrona simmetrica");
				}
				this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_RISPOSTA_APPLICATIVA);
			} else if (profiloCollaborazione.equals(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)) {
				if (this.headerIntegrazioneRichiesta != null
						&& this.headerIntegrazioneRichiesta.getBusta() != null
						&& this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null) {
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "ricevuta di una risposta asincrona asimmetrica");
				} else {
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "ricevuta di una richiesta asincrona asimmetrica");
				}
				this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_RISPOSTA_APPLICATIVA);
			}
		}

		
		
		
		this.msgDiag.mediumDebug("Imposto risposta nel context...");
		if(!errorOccursSetResponse){
			this.msgContext.setMessageResponse(responseMessage);
		}

	}

	
	
	
	
	/* -------------- UTILITY -------------- */
	
	private void eliminaSIL(OpenSPCoopStateful openspcoopstate,
			MsgDiagnostico msgDiag, String idMessageRequest, String idMessageResponse,
			String servizioApplicativo) {
		// Elimino SIL destinatario a cui ho consegnato il messaggio
		msgDiag.mediumDebug("Eliminazione SIL destinatario del messaggio nella tabelle MSG_SERVIZI_APPLICATIVI...");
		if (openspcoopstate.getConnectionDB() != null) {
			try {
				// GestoreMessaggi gestoreEliminazioneDestinatario = new
				// GestoreMessaggi(openspcoopstate,
				/** false,idMessageResponse,Costanti.INBOX,msgDiag);*/
				GestoreMessaggi gestoreEliminazioneDestinatario = new GestoreMessaggi(openspcoopstate, false, idMessageResponse, Costanti.INBOX,msgDiag,this.msgContext.pddContext);
				gestoreEliminazioneDestinatario.eliminaDestinatarioMessaggio(servizioApplicativo, idMessageRequest);
			} catch (Exception e) {
				msgDiag.logErroreGenerico(e, "gestoreEliminazioneDestinatario.eliminaDestinatarioMessaggio("+servizioApplicativo+")");
			}
		} else {
			msgDiag.logErroreGenerico("Connessione non disponibile", "gestoreEliminazioneDestinatario.eliminaDestinatarioMessaggio("+servizioApplicativo+")");
		}
	}

}
