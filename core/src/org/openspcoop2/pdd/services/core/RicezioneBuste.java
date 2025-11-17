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



package org.openspcoop2.pdd.services.core;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.details.utils.XMLUtils;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiErogazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziAzioneNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.ParseException;
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
import org.openspcoop2.pdd.core.MTOMProcessor;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.StatoServiziPdD;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativi;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiException;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiRest;
import org.openspcoop2.pdd.core.autorizzazione.GestoreAutorizzazione;
import org.openspcoop2.pdd.core.autorizzazione.container.AutorizzazioneHttpServletRequest;
import org.openspcoop2.pdd.core.autorizzazione.container.IAutorizzazioneSecurityContainer;
import org.openspcoop2.pdd.core.autorizzazione.pa.DatiInvocazionePortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.pa.EsitoAutorizzazionePortaApplicativa;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiConfigurationException;
import org.openspcoop2.pdd.core.credenziali.IGestoreCredenziali;
import org.openspcoop2.pdd.core.dynamic.DynamicMapBuilderUtils;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InRequestContext;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.pdd.core.handlers.OutResponseContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazioneBusta;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePA;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePASoap;
import org.openspcoop2.pdd.core.integrazione.InRequestPAMessage;
import org.openspcoop2.pdd.core.integrazione.OutResponsePAMessage;
import org.openspcoop2.pdd.core.integrazione.UtilitiesIntegrazione;
import org.openspcoop2.pdd.core.node.INodeReceiver;
import org.openspcoop2.pdd.core.node.INodeSender;
import org.openspcoop2.pdd.core.node.NodeTimeoutException;
import org.openspcoop2.pdd.core.response_caching.HashGenerator;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.core.token.attribute_authority.EsitoRecuperoAttributi;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.core.token.attribute_authority.PolicyAttributeAuthority;
import org.openspcoop2.pdd.core.token.attribute_authority.pa.GestioneAttributeAuthority;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.EsitoLib;
import org.openspcoop2.pdd.mdb.GenericLibException;
import org.openspcoop2.pdd.mdb.ImbustamentoRisposte;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.mdb.InoltroBusteMessage;
import org.openspcoop2.pdd.mdb.InoltroRisposte;
import org.openspcoop2.pdd.mdb.Sbustamento;
import org.openspcoop2.pdd.mdb.SbustamentoMessage;
import org.openspcoop2.pdd.services.DirectVMProtocolInfo;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.ServicesUtils;
import org.openspcoop2.pdd.services.connector.AsyncResponseCallbackClientEvent;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.IAsyncResponseCallback;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.pdd.services.service.RicezioneBusteServiceUtils;
import org.openspcoop2.pdd.services.skeleton.IntegrationManager;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.pdd.timers.TimerLock;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerThresholdThread;
import org.openspcoop2.pdd.timers.TipoLock;
import org.openspcoop2.protocol.basic.registry.IdentificazionePortaApplicativa;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.builder.Imbustamento;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.mapping.IdentificazioneDinamicaException;
import org.openspcoop2.protocol.engine.mapping.InformazioniServizioURLMapping;
import org.openspcoop2.protocol.engine.validator.Validatore;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.FaseImbustamento;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.InitialIdConversationType;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.StatoFunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.dump.DumpException;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.StatelessMessage;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.protocol.sdk.tracciamento.EsitoElaborazioneMessaggioTracciato;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.protocol.sdk.validator.IValidatoreErrori;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.protocol.utils.ModIValidazioneSemanticaProfiloSicurezza;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContextParameters;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.engine.MessageSecurityFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.Credential;
import org.openspcoop2.utils.transport.http.CORSRequestType;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;


/**
 * Contiene la definizione del modulo 'RicezioneBuste', il quale e' un
 * modulo dell'infrastruttura OpenSPCoop.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RicezioneBuste implements IAsyncResponseCallback {

	/** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
	public static final String ID_MODULO = "RicezioneBuste";

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
	
	private static final String INIZIALIZZAZIONE_RISORSE = "InizializzazioneRisorseServizioRicezioneBuste";
	private static final String SOGGETTO_FRUITORE_NON_IDENTIFICATO = "Soggetto fruitore non identificato";
	
	/** Indicazione se sono state inizializzate le variabili del servizio */
	public static boolean initializeService = false;

	/** IGestoreIntegrazionePA: lista di gestori, ordinati per priorita' minore */
	public static String[] defaultGestoriIntegrazionePA = null;
	public static java.util.concurrent.ConcurrentHashMap<String, String[]> defaultPerProtocolloGestoreIntegrazionePA = null;
	
	/** IGestoreCredenziali: lista di gestori delle credenziali */
	private static String [] tipiGestoriCredenziali = null;
		
	/**
	 * Inizializzatore del servizio RicezioneBuste
	 * @throws ProtocolException 
	 * 
	 * @throws Exception
	 */
	public static synchronized void initializeService(ClassNameProperties className,OpenSPCoop2Properties propertiesReader,Logger logCore) throws CoreException, ProtocolException{
		if(RicezioneBuste.initializeService)
			return; // inizializzato da un altro thread

		String effettuataSuffix = " effettuata.";
		
		Loader loader = Loader.getInstance();
		PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
		
		// Inizializzazione NodeSender
		String classTypeNodeSender = className.getNodeSender(propertiesReader.getNodeSender());
		try{
			INodeSender nodeSender = (INodeSender) loader.newInstance(classTypeNodeSender);
			nodeSender.toString();
			logInfo(logCore, "Inizializzazione gestore NodeSender di tipo "+classTypeNodeSender+effettuataSuffix);
		}catch(Exception e){
			throw new CoreException("Riscontrato errore durante il caricamento della classe 'NodeSender' ["+classTypeNodeSender+
					"] da utilizzare per la spedizione nell'infrastruttura: "+e.getMessage());
		}

		// Inizializzazione NodeReceiver
		String classType = className.getNodeReceiver(propertiesReader.getNodeReceiver());
		try{
			INodeReceiver nodeReceiver = (INodeReceiver) loader.newInstance(classType);
			nodeReceiver.toString();
			logInfo(logCore, "Inizializzazione gestore NodeReceiver di tipo "+classType+effettuataSuffix);
		}catch(Exception e){
			throw new CoreException("Riscontrato errore durante il caricamento della classe 'NodeReceiver' ["+classType+
					"] da utilizzare per la ricezione dall'infrastruttura: "+e.getMessage());
		}

		// Inizializzo IGestoreIntegrazionePA list
		String [] tipiIntegrazioneDefault = propertiesReader.getTipoIntegrazionePA();
		List<String> s = new ArrayList<>();
		for (int i = 0; i < tipiIntegrazioneDefault.length; i++) {
			try {
				IGestoreIntegrazionePA gestore = pluginLoader.newIntegrazionePortaApplicativa(tipiIntegrazioneDefault[i]);
				gestore.toString();
				s.add(tipiIntegrazioneDefault[i]);
				logInfo(logCore, "Inizializzazione gestore dati di integrazione 'IGestoreIntegrazionePA' per le erogazioni di tipo "
						+ tipiIntegrazioneDefault[i]	+ effettuataSuffix);
			} catch (Exception e) {
				throw new CoreException(e.getMessage(),e);
			}
		}
		if(!s.isEmpty()){
			RicezioneBuste.defaultGestoriIntegrazionePA = s.toArray(new String[1]);
		}
		
		// Inizializzo IGestoreIntegrazionePA per protocollo
		RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA = new java.util.concurrent.ConcurrentHashMap<>();
		Enumeration<String> enumProtocols = ProtocolFactoryManager.getInstance().getProtocolNames();
		while (enumProtocols.hasMoreElements()) {
			String protocol = enumProtocols.nextElement();
			String [] tipiIntegrazionePA = propertiesReader.getTipoIntegrazionePA(protocol);
			if(tipiIntegrazionePA!=null && tipiIntegrazionePA.length>0){
				List<String> tipiIntegrazionePerProtocollo = new ArrayList<>();
				for (int i = 0; i < tipiIntegrazionePA.length; i++) {
					try {
						IGestoreIntegrazionePA gestore = pluginLoader.newIntegrazionePortaApplicativa(tipiIntegrazionePA[i]);
						gestore.toString();
						tipiIntegrazionePerProtocollo.add(tipiIntegrazionePA[i]);
						logInfo(logCore, "Inizializzazione gestore dati di integrazione (protocollo: "+protocol+") per le erogazioni di tipo "
								+ tipiIntegrazionePA[i]	+ effettuataSuffix);
					} catch (Exception e) {
						throw new CoreException(e.getMessage(),e);
					}
				}
				if(!tipiIntegrazionePerProtocollo.isEmpty()){
					RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.put(protocol, tipiIntegrazionePerProtocollo.toArray(new String[1]));
				}
			}
		}
		
		// Inizializzo GestoriCredenziali PA
		RicezioneBuste.tipiGestoriCredenziali = propertiesReader.getTipoGestoreCredenzialiPA();
		if(RicezioneBuste.tipiGestoriCredenziali!=null){
			for (int i = 0; i < RicezioneBuste.tipiGestoriCredenziali.length; i++) {
				classType = className.getGestoreCredenziali(RicezioneBuste.tipiGestoriCredenziali[i]);
				try {
					IGestoreCredenziali gestore = (IGestoreCredenziali)loader.newInstance(classType);
					gestore.toString();
					logInfo(logCore, "Inizializzazione gestore credenziali di tipo "
							+ RicezioneBuste.tipiGestoriCredenziali[i]	+ effettuataSuffix);
				} catch (Exception e) {
					throw new CoreException(
							"Riscontrato errore durante il caricamento della classe 'IGestoreCredenziali' ["+ classType
							+ "] da utilizzare per la gestione delle credenziali di tipo ["
							+ RicezioneBuste.tipiGestoriCredenziali[i]+ "]: " + e.getMessage());
				}
			}
		}

		RicezioneBuste.initializeService = true;
	}


	/** Generatore Errori */
	private RicezioneBusteExternalErrorGenerator generatoreErrore;
	
	/** Contesto della richiesta */
	private RicezioneBusteContext msgContext;
	private RicezioneBusteGeneratoreBustaErrore ricezioneBusteGeneratoreBustaErrore;
	
	/** Async Response Callback */
	private IAsyncResponseCallback asyncResponseCallback;
	
	private Logger logCore;
	private SbustamentoMessage sbustamentoMSG;
	private ConfigurazionePdDManager configurazionePdDReader;
	private RegistroServiziManager registroServiziReader;
	private MsgDiagnostico msgDiag;
	
	private PdDContext pddContext;
	private String idTransazione;
	private Transaction transaction = null;
	
	private String idMessageRequest;
	private Busta bustaRichiesta;
	private java.util.List<Eccezione> erroriValidazione;
	private java.util.List<Eccezione> erroriProcessamento;
	private OpenSPCoop2Message requestMessage;
	private volatile boolean isMessaggioErroreProtocollo;
	private volatile boolean bustaDiServizio;
	private volatile boolean mittenteAnonimo = false;
	private String implementazionePdDMittente;
	private RuoloBusta ruoloBustaRicevuta;
	private String versioneProtocollo;
	private IDSoggetto identitaPdD;
	
	private IDSoggetto soggettoFruitore = null;
	private IDServizio idServizio = null;
		
	private TipoPdD tipoPorta;
	private PortaApplicativa pa = null;
	private PortaDelegata pd = null;
		
	private volatile boolean oneWayVersione11;
	private volatile boolean oneWayStateless;
	private volatile boolean sincronoStateless;
	private volatile boolean asincronoStateless;
	private volatile boolean portaStateless;
	private volatile boolean routingStateless;
	private volatile boolean functionAsRouter;
	private volatile boolean newConnectionForResponse;
	private volatile boolean utilizzoIndirizzoTelematico;
	
	private OpenSPCoopState openspcoopstate = null;
	private Tracciamento tracciamento = null;
	private String correlazioneApplicativa = null;
	private GestoreMessaggi msgRequest = null;
	private RepositoryBuste repositoryBuste = null;
	
	private PdDContext context;
	
	private HashMap<String, Object> internalObjects = null;
	private InRequestContext inRequestContext = null;
	private RicezioneBusteParametriGenerazioneBustaErrore parametriGenerazioneBustaErrore = null;
	private RicezioneBusteParametriInvioBustaErrore parametriInvioBustaErrore = null;
	
	// Imposto un context di Base (utilizzato anche  per la successiva spedizione della risposta)
	private MessageSecurityContext messageSecurityContext = null;
	// Proprieta' FlowParameter MTOM / Security relative alla spedizione della risposta
	private FlowProperties flowPropertiesResponse = null;
	
	private Integrazione infoIntegrazione;
	
	private HeaderIntegrazione headerIntegrazioneRichiesta;
	private String[] tipiIntegrazionePA = null;
	
	private IProtocolFactory<?> protocolFactory;
	private IProtocolVersionManager moduleManager;
	private ITraduttore traduttore;
	private volatile boolean readQualifiedAttribute;
	private ProprietaManifestAttachments proprietaManifestAttachments;

	private volatile boolean asyncWait = false;
	
	public RicezioneBuste(RicezioneBusteContext context,
			RicezioneBusteExternalErrorGenerator generatoreErrore,
			IAsyncResponseCallback asyncResponseCallback){
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
		
		this._statelessComplete(true);
		
		this.asyncResponseCallback.asyncComplete(clientEvent);
	}
	
	private void internalProcess(Object ... params) {

		
		

		// ------------- dati generali -----------------------------

		// Context
		this.context = this.msgContext.getPddContext();
		
		// Logger
		this.logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.logCore==null){
			this.logCore = LoggerWrapperFactory.getLogger(RicezioneBuste.ID_MODULO);
		}
	
		// MsgDiagnostico
		this.msgDiag = this.msgContext.getMsgDiagnostico();
		
		// Messaggio
		this.requestMessage = this.msgContext.getMessageRequest();
		if(this.requestMessage==null){
			setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.logCore, this.msgDiag, new Exception("Request message is null"), "LetturaMessaggioRichiesta");
		}

		
		
		
		
		
		// ------------- in-handler -----------------------------
		
		try{
			if(this.context==null) {
				throw new CoreException("Context is null");
			}
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String)this.context.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
		}catch(Exception e){
			setSOAPFaultProcessamento(IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, this.logCore, this.msgDiag, e, "ProtocolFactoryInstance");
			return;
		}
		this.inRequestContext = new InRequestContext(this.logCore,this.protocolFactory,null);
		// TipoPorta
		this.inRequestContext.setTipoPorta(TipoPdD.APPLICATIVA);
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
						setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.logCore, this.msgDiag, e, "AutorizzazioneSecurityContainerInstance");
						return;
					}
				}
			}
		}
		connettore.setUrlProtocolContext(this.msgContext.getUrlProtocolContext());	
		if(this.requestMessage!=null && ServiceBinding.SOAP.equals(this.requestMessage.getServiceBinding())){
			try{
				connettore.setSoapAction(this.requestMessage.castAsSoap().getSoapAction());
			}catch(Exception e){
				setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.logCore, this.msgDiag, e, "LetturaSoapAction");
				return;
			}
		}
		connettore.setFromLocation(this.msgContext.getSourceLocation());
		this.inRequestContext.setConnettore(connettore);
		// Data accettazione richiesta
		this.inRequestContext.setDataAccettazioneRichiesta(this.msgContext.getDataAccettazioneRichiesta());
		// Data ingresso richiesta
		this.inRequestContext.setDataElaborazioneMessaggio(this.msgContext.getDataIngressoRichiesta());
		// PddContext
		this.inRequestContext.setPddContext(this.context);
		// Dati Messaggio
		this.inRequestContext.setMessaggio(this.requestMessage);
		// Invoke handler
		try{
			GestoreHandlers.inRequest(this.inRequestContext, this.msgDiag, this.logCore);
		}catch(HandlerException e){
			setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.logCore, this.msgDiag, e, e.getIdentitaHandler());
			return;
		}catch(Exception e){
			setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.logCore, this.msgDiag, e, "InvocazioneInRequestHandler");
			return;
		}

		
		
		
		
		
		
		// ------------- process -----------------------------
		this.internalObjects = new HashMap<>();
		try{
			processEngine(this.internalObjects,params);
		} catch(TracciamentoException tracciamentoException){
			setSOAPFaultProcessamento(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore, this.msgDiag, tracciamentoException, "TracciamentoNonRiuscito");
			return;
		} catch(DumpException e){
			setSOAPFaultProcessamento(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore, this.msgDiag, e, DumpException.DUMP_NON_RIUSCITO);
			return;
		} catch(ProtocolException protocolException){
			setSOAPFaultProcessamento(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore, this.msgDiag, protocolException, "ProtocolFactoryNonInstanziata");
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
				setSOAPFaultProcessamento(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore,this.msgDiag, e, "FinalizeIntegrationContextRicezioneBuste");
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
						IDPortaApplicativa identificativoPortaApplicativa = new IDPortaApplicativa();
						identificativoPortaApplicativa.setNome(urlProtocolContext.getInterfaceName());
						PortaApplicativa portaApplicativa = this.configurazionePdDReader.getPortaApplicativaSafeMethod(identificativoPortaApplicativa, this.msgContext.getRequestInfo());
						if(portaApplicativa!=null) {
							DumpConfigurazione dumpConfig = this.configurazionePdDReader.getDumpConfigurazione(portaApplicativa);
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
			}catch(DumpException dumpException){
				setSOAPFaultProcessamento(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore, this.msgDiag, dumpException, DumpException.DUMP_NON_RIUSCITO);
				return;
			}catch(Exception e){
				// Se non riesco ad accedere alla configurazione sicuramente gia' nel messaggio di risposta e' presente l'errore di PdD non correttamente inizializzata
			}
		}
		
		
		
		
		
		
		
		
		// ------------- out-handler -----------------------------
		OutResponseContext outResponseContext = null;
		if(!this.internalObjects.containsKey(CostantiPdD.PROCESS_COMPLETE_GESTORE_OUT_RESPONSE_HANDLER)) {
			outResponseContext = new OutResponseContext(this.logCore,this.protocolFactory,null);
			// TipoPorta
			outResponseContext.setTipoPorta(this.msgContext.getTipoPorta());
			outResponseContext.setIdModulo(this.msgContext.getIdModulo());
			// DataUscitaMessaggio
			outResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
			// PddContext
			outResponseContext.setPddContext(this.inRequestContext.getPddContext());
			// Informazioni busta e di integrazione
			outResponseContext.setProtocollo(this.msgContext.getProtocol());
			outResponseContext.setIntegrazione(this.msgContext.getIntegrazione());
			// Header di trasporto della risposta
			outResponseContext.setResponseHeaders(this.msgContext.getResponseHeaders());
			// Messaggio
			OpenSPCoop2Message msgResponse = this.msgContext.getMessageResponse();
			outResponseContext.setMessaggio(msgResponse);
			// Invoke handler
			try{
				GestoreHandlers.outResponse(outResponseContext,this.msgDiag, this.logCore);
			}catch(HandlerException e){
				setSOAPFaultProcessamento(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore, this.msgDiag, e, e.getIdentitaHandler());
				return;
			}catch(Exception e){
				setSOAPFaultProcessamento(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore, this.msgDiag, e, "InvocazioneOutResponseHandler");
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
				setSOAPFaultProcessamento(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore, this.msgDiag, e, "FineGestioneRicezioneBuste");
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
					ServiceBinding sb = this.requestMessage!=null ? this.requestMessage.getServiceBinding() : this.msgContext.getRequestInfo().getProtocolServiceBinding();
					ServicesUtils.setGovWayHeaderResponse(sb,
							msgRisposta, OpenSPCoop2Properties.getInstance(),
							propertiesTrasporto, this.logCore, false, outResponseContext.getPddContext(), this.msgContext.getRequestInfo());
					dumpApplicativo.dumpRispostaUscita(msgRisposta, 
							this.inRequestContext.getConnettore().getUrlProtocolContext(), 
							outResponseContext.getResponseHeaders());
					this.internalObjects.put(CostantiPdD.DUMP_RISPOSTA_EFFETTUATO, true);
				}
			}catch(DumpException dumpException){
				setSOAPFaultProcessamento(AbstractErrorGenerator.getIntegrationInternalError(this.context), this.logCore, this.msgDiag, dumpException, DumpException.DUMP_NON_RIUSCITO);
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
			dumpConfig = configurazionePdDReader.getDumpConfigurazionePortaApplicativa();
		}
		
		ProtocolContext protocolContext = this.msgContext.getProtocol();
		URLProtocolContext urlProtocolContext = this.msgContext.getUrlProtocolContext();
		IDSoggetto soggettoErogatore = null;
		IDServizio idServizioForDump = null;
		IDSoggetto fruitore = null;
		IDSoggetto dominio = null;
		String idRichiesta = null;
		if(protocolContext!=null) {
			if(protocolContext.getTipoServizio()!=null && protocolContext.getServizio()!=null && protocolContext.getVersioneServizio()!=null &&
				protocolContext.getErogatore()!=null && protocolContext.getErogatore().getTipo()!=null && protocolContext.getErogatore().getNome()!=null) {
				idServizioForDump = IDServizioFactory.getInstance().getIDServizioFromValues(protocolContext.getTipoServizio(), protocolContext.getServizio(), 
						protocolContext.getErogatore(), protocolContext.getVersioneServizio());
			}
			dominio = protocolContext.getDominio();
			idRichiesta = protocolContext.getIdRichiesta();
			if(protocolContext.getFruitore()!=null && protocolContext.getFruitore().getTipo()!=null && protocolContext.getFruitore().getNome()!=null) {
				fruitore = protocolContext.getFruitore();
			}
		}
		
		if(
				(dominio == null || fruitore==null || idServizioForDump == null)
				&&
				(urlProtocolContext!=null && urlProtocolContext.getInterfaceName()!=null) 
			){
			IDPortaApplicativa identificativoPortaApplicativa = new IDPortaApplicativa();
			identificativoPortaApplicativa.setNome(urlProtocolContext.getInterfaceName());
			PortaApplicativa portaApplicativa = null;
			try {
				portaApplicativa = configurazionePdDReader.getPortaApplicativaSafeMethod(identificativoPortaApplicativa, this.msgContext.getRequestInfo());
			}catch(Exception e) {
				// ignore
			}
			if(portaApplicativa!=null) {
				// Aggiorno tutti
				soggettoErogatore = new IDSoggetto(portaApplicativa.getTipoSoggettoProprietario(), portaApplicativa.getNomeSoggettoProprietario());
				if(portaApplicativa.getServizio()!=null) {
					idServizioForDump = IDServizioFactory.getInstance().getIDServizioFromValues(portaApplicativa.getServizio().getTipo(),portaApplicativa.getServizio().getNome(), 
								soggettoErogatore, portaApplicativa.getServizio().getVersione());
				}
				dominio = new IDSoggetto(portaApplicativa.getTipoSoggettoProprietario(), portaApplicativa.getNomeSoggettoProprietario());
				try {
					dominio.setCodicePorta(RegistroServiziManager.getInstance().getDominio(dominio, null, protocolFactory, this.msgContext.getRequestInfo()));
				}catch(Exception e) {
					dominio = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(protocolFactory.getProtocol(), this.msgContext.getRequestInfo());
				}
			}
		}
		if(idServizioForDump!=null) {
			if(protocolContext!=null && protocolContext.getAzione()!=null) {
				idServizioForDump.setAzione(protocolContext.getAzione());
			}
			else if(this.msgContext.getRequestInfo()!=null && 
					this.msgContext.getRequestInfo().getIdServizio()!=null && this.msgContext.getRequestInfo().getIdServizio().getAzione()!=null) {
				idServizioForDump.setAzione(this.msgContext.getRequestInfo().getIdServizio().getAzione());
			}
		}
		if(dominio==null) {
			dominio = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(protocolFactory.getProtocol(), this.msgContext.getRequestInfo());
		}
		
		Dump dumpApplicativo = null;
		if(protocolContext!=null){
			dumpApplicativo = new Dump(dominio,
					this.msgContext.getIdModulo(), 
					idRichiesta, fruitore, idServizioForDump,
					this.msgContext.getTipoPorta(),nomePorta,this.msgContext.getPddContext(),
					null,null,
					dumpConfig);
		}else{
			dumpApplicativo = new Dump(dominio,
					this.msgContext.getIdModulo(),this.msgContext.getTipoPorta(),nomePorta,this.msgContext.getPddContext(),
					null,null,
					dumpConfig);
		}
		return dumpApplicativo;
	}
	
	// processamento quando non sono disponibili le risorse (es. MsgDiagnostico)
	private void setSOAPFaultProcessamento(IntegrationFunctionError integrationFunctionError, ErroreIntegrazione erroreIntegrazione){
		setSOAPFaultEngine(integrationFunctionError, null, null, null, null, erroreIntegrazione, null, false);
	}
	private void setSOAPFaultProcessamento(IntegrationFunctionError integrationFunctionError, ErroreIntegrazione erroreIntegrazione, Exception e){
		setSOAPFaultEngine(integrationFunctionError, null, null, e, null, erroreIntegrazione, null, false);
	}
	// processamento
	private void setSOAPFaultProcessamento(IntegrationFunctionError integrationFunctionError, Logger logCore, MsgDiagnostico msgDiag, Exception e, String posizione){
		setSOAPFaultEngine(integrationFunctionError, logCore, msgDiag, e, null, null, posizione, false);
	}
	private void setSOAPFaultProcessamento(IntegrationFunctionError integrationFunctionError, Logger logCore, MsgDiagnostico msgDiag, ErroreIntegrazione erroreIntegrazione, Exception e, String posizione){
		setSOAPFaultEngine(integrationFunctionError, logCore, msgDiag, e, null, erroreIntegrazione, posizione, false);
	}
	// intestazione
	private void setSOAPFaultIntestazione(IntegrationFunctionError integrationFunctionError, ErroreCooperazione erroreCooperazione){
		setSOAPFaultEngine(integrationFunctionError, null, null, null, erroreCooperazione, null, null, true);
	}
	@SuppressWarnings("unused")
	private void setSOAPFaultIntestazione(IntegrationFunctionError integrationFunctionError, ErroreIntegrazione erroreIntegrazione){
		setSOAPFaultEngine(integrationFunctionError, null, null, null, null, erroreIntegrazione, null, true);
	}
	
	private void setSOAPFaultEngine(IntegrationFunctionError integrationFunctionError, Logger logCore, MsgDiagnostico msgDiag, Exception e, 
			ErroreCooperazione erroreCooperazione, ErroreIntegrazione erroreIntegrazione, String posizioneErrore, 
			boolean validazione) {
				
		HandlerException he = null;
		if(e instanceof HandlerException heCast){
			he = heCast;
		}
		
		Context contextFault = this.msgContext.getPddContext();
		
		if(msgDiag!=null){
			if(he!=null){
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e, posizioneErrore);
				}
			}else{
				String descrizioneErrore = null;
				if(erroreCooperazione != null){
					try{
						descrizioneErrore = erroreCooperazione.getDescrizione(this.generatoreErrore.getProtocolFactory());
					}catch(Exception t){
						// ignore
					}
				}
				if(descrizioneErrore==null && erroreIntegrazione!=null) {
					try{
						descrizioneErrore = erroreIntegrazione.getDescrizione(this.generatoreErrore.getProtocolFactory());
					}catch(Exception t){
						// ignore
					}
				}
				if(descrizioneErrore==null) {
					descrizioneErrore = posizioneErrore;
				}
				msgDiag.logErroreGenerico(descrizioneErrore, posizioneErrore); // nota: non emette informazioni sul core
				if(logCore!=null){
					if(e!=null) {
						logError(logCore, descrizioneErrore+": "+e.getMessage(),e);
					}
					else {
						logError(logCore, descrizioneErrore);
					}
				}
			}
		}
		else if(logCore!=null){
			if(e!=null) {
				logError(logCore, posizioneErrore+": "+e.getMessage(),e);
			}
			else {
				logError(logCore, posizioneErrore);
			}
		}
		if (this.msgContext.isGestioneRisposta()) {
			OpenSPCoop2Message messageFault = null;
			if(validazione){
				if(erroreCooperazione != null){
					String descrizioneErrore = null;
					try{
						descrizioneErrore = erroreCooperazione.getDescrizione(this.generatoreErrore.getProtocolFactory());
						messageFault = this.generatoreErrore.buildErroreIntestazione(contextFault, integrationFunctionError, 
								erroreCooperazione.getCodiceErrore(), descrizioneErrore);
					}catch(Exception eP){
						messageFault = this.generatoreErrore.buildFault(eP, contextFault);
					}
				}else{
					messageFault = this.generatoreErrore.buildErroreIntestazione(contextFault,integrationFunctionError, 
							erroreIntegrazione);
				}
			}else{
				if(erroreIntegrazione!=null){
					messageFault = this.generatoreErrore.buildErroreProcessamento(contextFault,integrationFunctionError, 
							erroreIntegrazione,e);
				}
				else if(e!=null){
					IntegrationFunctionError ifError = integrationFunctionError;
					ErroreIntegrazione erroreIntegrazioneGenerato = null;
					if(he!=null){
						erroreIntegrazioneGenerato = he.convertToErroreIntegrazione();
						if(he.getIntegrationFunctionError()!=null) {
							ifError = he.getIntegrationFunctionError();
						}
					}
					if(erroreIntegrazioneGenerato==null) {
						erroreIntegrazioneGenerato = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(posizioneErrore);
					}
					messageFault = this.generatoreErrore.buildErroreProcessamento(contextFault,ifError, 
							erroreIntegrazioneGenerato,e);
					if(he!=null){
						he.customized(messageFault);
					}
				}else{
					messageFault = this.generatoreErrore.buildErroreProcessamento(contextFault,integrationFunctionError, 
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(posizioneErrore));
				}
			}
			// Lo imposto sempre, perche' puo' servire nell'handler
			this.msgContext.setMessageResponse(messageFault);
		}
	}
	
	
	private boolean checkInizializzazione(Logger logCore) {
		if( !OpenSPCoop2Startup.initialize){
			String msgErrore = "Inizializzazione di GovWay non correttamente effettuata";
			logError(logCore, "["+RicezioneBuste.ID_MODULO+"]  "+msgErrore);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"InizializzazioneGovWay");
				}
			}catch(Exception t){logError(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (InizializzazioneGovWay): "+t.getMessage(),t);}
			setSOAPFaultProcessamento(IntegrationFunctionError.GOVWAY_NOT_INITIALIZED,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA));
			return false;
		}
		if( !TimerMonitoraggioRisorseThread.isRisorseDisponibili()){
			String msgErrore = "Risorse di sistema non disponibili: "+ TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile().getMessage();
			logError(logCore, "["+RicezioneBuste.ID_MODULO+"]  "+msgErrore,TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile());
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"InizializzazioneRisorseGovWay");
				}
			}catch(Exception t){logError(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (InizializzazioneRisorseGovWay): "+t.getMessage(),t);}
			setSOAPFaultProcessamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_532_RISORSE_NON_DISPONIBILI));
			return false;
		}
		if( !TimerThresholdThread.freeSpace){
			String msgErrore = "Non sono disponibili abbastanza risorse per la gestione della richiesta";
			logError(logCore, "["+RicezioneBuste.ID_MODULO+"]  "+msgErrore);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"DisponibilitaRisorseGovWay");
				}
			}catch(Exception t){logError(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (DisponibilitaRisorseGovWay): "+t.getMessage(),t);}
			setSOAPFaultProcessamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_533_RISORSE_DISPONIBILI_LIVELLO_CRITICO));
			return false;
		}
		if( !Tracciamento.tracciamentoDisponibile){
			String msgErrore = "Tracciatura non disponibile: "+ Tracciamento.motivoMalfunzionamentoTracciamento.getMessage();
			logError(logCore, "["+RicezioneBuste.ID_MODULO+"]  "+msgErrore,Tracciamento.motivoMalfunzionamentoTracciamento);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"Tracciamento");
				}
			}catch(Exception t){logError(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (Tracciamento): "+t.getMessage(),t);}
			setSOAPFaultProcessamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_545_TRACCIATURA_NON_FUNZIONANTE));
			return false;
		}
		if( !MsgDiagnostico.gestoreDiagnosticaDisponibile){
			String msgErrore = "Sistema di diagnostica non disponibile: "+ MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage();
			logError(logCore, "["+RicezioneBuste.ID_MODULO+"]  "+msgErrore,MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			try{
				// provo ad emetter un diagnostico lo stesso (molto probabilmente non ci riuscirà essendo proprio la risorsa diagnostica non disponibile)
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"Diagnostica");
				}
			}catch(Exception t){logDebug(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (Diagnostica): "+t.getMessage(),t);}
			setSOAPFaultProcessamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_546_DIAGNOSTICA_NON_FUNZIONANTE));
			return false;
		}
		if( !Dump.isSistemaDumpDisponibile()){
			String msgErrore = "Sistema di dump dei contenuti applicativi non disponibile: "+ Dump.getMotivoMalfunzionamentoDump().getMessage();
			logError(logCore, "["+RicezioneBuste.ID_MODULO+"]  "+msgErrore,Dump.getMotivoMalfunzionamentoDump());
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"Dump");
				}
			}catch(Exception t){logError(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (Dump): "+t.getMessage(),t);}
			setSOAPFaultProcessamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_547_DUMP_CONTENUTI_APPLICATIVI_NON_FUNZIONANTE));
			return false;
		}
		// Check Configurazione (XML)
		this.configurazionePdDReader = ConfigurazionePdDManager.getInstance();
		try{
			this.configurazionePdDReader.verificaConsistenzaConfigurazione();
		}catch(Exception e){
			String msgErrore = "Riscontrato errore durante la verifica della consistenza della configurazione";
			logError(logCore, "["+RicezioneBuste.ID_MODULO+"]  "+msgErrore,e);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"CheckConfigurazioneGovWay");
				}
			}catch(Exception t){logError(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (CheckConfigurazioneGovWay): "+t.getMessage(),t);}
			setSOAPFaultProcessamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e);
			return false;
		}
		// Check RegistroServizi (XML)
		this.registroServiziReader = RegistroServiziManager.getInstance();
		try{
			this.registroServiziReader.verificaConsistenzaRegistroServizi();
		}catch(Exception e){
			String msgErrore = "Riscontrato errore durante la verifica del registro dei servizi";
			logError(logCore, "["+ RicezioneBuste.ID_MODULO+ "]  "+msgErrore,e);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"CheckRegistroServizi");
				}
			}catch(Exception t){logError(logCore, "Emissione diagnostico per errore inizializzazione non riuscita (CheckRegistroServizi): "+t.getMessage(),t);}
			setSOAPFaultProcessamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_534_REGISTRO_DEI_SERVIZI_NON_DISPONIBILE),e);
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
		this.idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, this.inRequestContext.getPddContext());
		
		// Loader
		Loader loader = Loader.getInstance();
		PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
		
		// RequestInfo
		RequestInfo requestInfo = this.msgContext.getRequestInfo();
		
		
		/* ------------ Controllo inizializzazione OpenSPCoop  ------------------ */
		if(!checkInizializzazione(this.logCore)) {
			return;
		}





		/* ------------- Inizializzazione Risorse  ------------------- */

		// Credenziali utilizzate nella richiesta
		Credenziali credenziali = this.msgContext.getCredenziali();


		// OpenSPCoop Properties
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
		
		// Classi registrate
		ClassNameProperties className = ClassNameProperties.getInstance();

		if(requestInfo==null) {
			setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,null,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento("RequesInfo is null"),null,
					INIZIALIZZAZIONE_RISORSE);
			return;
		}
		
		//	IdentificativoPdD
		this.identitaPdD = requestInfo.getIdentitaPdD();

		
		// ConnectorInMessage
		ConnectorInMessage connectorInMessage = null;
		if(params!=null){
			for (int i = 0; i < params.length; i++) {
				if(params[i] instanceof ConnectorInMessage cast ){
					connectorInMessage = cast;
					break;
				}
			}
		}
		
		// ProtocolFactory
		this.protocolFactory = requestInfo.getProtocolFactory();
		this.pddContext = this.inRequestContext.getPddContext();
		this.traduttore = this.protocolFactory.createTraduttore();
		
		//	Logger dei messaggi diagnostici
		String nomePorta = null;
		if(requestInfo.getProtocolContext().getInterfaceName()!=null){
			nomePorta = requestInfo.getProtocolContext().getInterfaceName();
		}
		else{
			nomePorta = this.inRequestContext.getConnettore().getUrlProtocolContext().getFunctionParameters() + "_urlInvocazione("+ this.inRequestContext.getConnettore().getUrlProtocolContext().getUrlInvocazione_formBased() + ")";
		}
		this.msgDiag = MsgDiagnostico.newInstance(TipoPdD.APPLICATIVA,this.identitaPdD,this.msgContext.getIdModulo(),nomePorta,requestInfo,this.configurazionePdDReader);
		if(this.msgDiag==null) {
			setSOAPFaultProcessamento(IntegrationFunctionError.GOVWAY_NOT_INITIALIZED,this.logCore,null,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento("MsgDiagnostico is null",CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA),null,
					INIZIALIZZAZIONE_RISORSE);
			return;
		}
		this.msgContext.setMsgDiagnostico(this.msgDiag); // aggiorno msg diagnostico
		this.msgDiag.setPddContext(this.inRequestContext.getPddContext(), this.protocolFactory);
		this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
			
		// Parametri della porta applicativa invocata
		URLProtocolContext urlProtocolContext = this.msgContext.getUrlProtocolContext();
		
		// setCredenziali
		setCredenziali(credenziali, this.msgDiag);
		
		// inizializzazione risorse statiche
		try{
			if(!RicezioneBuste.initializeService){
				this.msgDiag.mediumDebug("Inizializzazione risorse statiche...");
				RicezioneBuste.initializeService(className, propertiesReader,this.logCore);
			}
		}catch(Exception e){
			setSOAPFaultProcessamento(IntegrationFunctionError.GOVWAY_NOT_INITIALIZED,this.logCore,this.msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA),e,
					INIZIALIZZAZIONE_RISORSE);
			return;
		}
		
		// Imposto header di risposta
		Map<String, List<String>> headerRisposta = new HashMap<>();
		UtilitiesIntegrazione utilitiesHttpRisposta = UtilitiesIntegrazione.getInstancePAResponse(this.logCore);
		try{
			utilitiesHttpRisposta.setInfoProductTransportProperties(headerRisposta);
		}catch(Exception e){
			setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(e),e,
					"InizializzazioneHeaderRisposta");
			return;
		}
		this.msgContext.setResponseHeaders(headerRisposta);

		// OPENSPCOOPSTATE 
		try{ // finally in fondo, vedi  #try-finally-openspcoopstate#

		// Messaggio di libreria 
		this.sbustamentoMSG = new SbustamentoMessage();
		InoltroBusteMessage inoltroMSG = new InoltroBusteMessage();

		// Context di risposta
		this.msgContext.setProtocol(new ProtocolContext());
		this.msgContext.getProtocol().setDominio(this.msgContext.getIdentitaPdD());
		this.msgContext.setIntegrazione(new IntegrationContext());
		

		// DBManager 
		this.msgDiag.mediumDebug("Richiesta connessione al database...");
		try{
			this.openspcoopstate = new OpenSPCoopStateful();
			this.openspcoopstate.setUseConnection(false); // gestione stateless per default
			this.openspcoopstate.initResource(this.identitaPdD, this.msgContext.getIdModulo(),this.idTransazione);
		}catch(Exception e){
			setSOAPFaultProcessamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,this.logCore,this.msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION),e,
					"openspcoopstate.initResource()");
			return;
		}
		
		// Refresh reader
		this.registroServiziReader = this.registroServiziReader.refreshState(this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta());
		this.configurazionePdDReader = this.configurazionePdDReader.refreshState(this.registroServiziReader);
		this.msgDiag.updateState(this.configurazionePdDReader);

		// Dati precedentemente raccolti
		String servizioApplicativoFruitore = null;
		if(!this.msgContext.isTracciamentoAbilitato()){
			// e' gia stata effettuata un'analisi precedentemente
			servizioApplicativoFruitore = this.msgContext.getIdentitaServizioApplicativoFruitore();
			this.msgContext.getIntegrazione().setServizioApplicativoFruitore(servizioApplicativoFruitore);
			this.correlazioneApplicativa = this.msgContext.getIdCorrelazioneApplicativa();
			this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, servizioApplicativoFruitore);
			this.msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA, this.correlazioneApplicativa);
			this.generatoreErrore.updateInformazioniCooperazione(servizioApplicativoFruitore);
		}

		// Transaction
		try{
			this.transaction = TransactionContext.getTransaction(this.idTransazione);
		}catch(Exception e){
			setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),e,
					"getTransaction");
			return;
		}
		
		
		
		
		// Messaggio di generazione Errore Protocollo
		this.parametriGenerazioneBustaErrore = new RicezioneBusteParametriGenerazioneBustaErrore();
		this.parametriGenerazioneBustaErrore.setOpenspcoop(this.openspcoopstate);
		this.parametriGenerazioneBustaErrore.setIdentitaPdD(this.identitaPdD);
		this.parametriGenerazioneBustaErrore.setMsgDiag(this.msgDiag);
		this.parametriGenerazioneBustaErrore.setPropertiesReader(propertiesReader);
		this.parametriGenerazioneBustaErrore.setLogCore(this.logCore);
		this.parametriGenerazioneBustaErrore.setCorrelazioneApplicativa(this.correlazioneApplicativa);
		this.parametriGenerazioneBustaErrore.setServizioApplicativoFruitore(servizioApplicativoFruitore);
		this.parametriGenerazioneBustaErrore.setImplementazionePdDMittente(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
		this.parametriGenerazioneBustaErrore.setImplementazionePdDDestinatario(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
		
		
		// Messaggio di invio Errore Protocollo
		this.parametriInvioBustaErrore = new RicezioneBusteParametriInvioBustaErrore();
		this.parametriInvioBustaErrore.setOpenspcoop(this.openspcoopstate);
		this.parametriInvioBustaErrore.setIdentitaPdD(this.identitaPdD);
		this.parametriInvioBustaErrore.setMsgDiag(this.msgDiag);
		this.parametriInvioBustaErrore.setPropertiesReader(propertiesReader);
		this.parametriInvioBustaErrore.setLogCore(this.logCore);
		this.parametriInvioBustaErrore.setCorrelazioneApplicativa(this.correlazioneApplicativa);
		this.parametriInvioBustaErrore.setServizioApplicativoFruitore(servizioApplicativoFruitore);
		this.parametriInvioBustaErrore.setImplementazionePdDMittente(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
		this.parametriInvioBustaErrore.setImplementazionePdDDestinatario(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
		
		this.parametriInvioBustaErrore.setNewConnectionForResponse(false);
		this.parametriInvioBustaErrore.setUtilizzoIndirizzoTelematico(false);
		this.parametriInvioBustaErrore.setFunctionAsRouter(false);
		this.parametriInvioBustaErrore.setOnewayVersione11(false);
		this.parametriInvioBustaErrore.setPddContext(this.inRequestContext.getPddContext());
		
		// RicezioneBusteUtils
		RicezioneBusteUtils ricezioneBusteUtils = new RicezioneBusteUtils();
		
		

		
		

		
		
		
		
		
		
		
		
		
		/* ------------ Identificazione IDServizio  ------------- */	
		
		this.msgDiag.mediumDebug("Identificazione IDServizio ...");
		
		this.idServizio = requestInfo.getIdServizio();
		if(this.idServizio==null){
			// avviene solamente se abbiamo invocazioni speciali con contextURL
			// provo ad individuarlo con il protocollo
			String idBusta = null;
			String profiloBusta = null;
			try{
				Busta busta = this.protocolFactory.createValidazioneSintattica(this.openspcoopstate.getStatoRichiesta()).getBusta_senzaControlli(this.requestMessage);
				if(busta==null){
					throw new CoreException("Protocollo non individuato nel messaggio");
				}
				idBusta = busta.getID();
				profiloBusta = busta.getProfiloDiCollaborazioneValue();
				
				if(busta.getTipoDestinatario()==null){
					throw new CoreException("TipoDestinatario non individuato nel messaggio");
				}
				if(busta.getDestinatario()==null){
					throw new CoreException("Destinatario non individuato nel messaggio");
				}
				if(busta.getTipoServizio()==null){
					throw new CoreException("TipoServizio non individuato nel messaggio");
				}
				if(busta.getServizio()==null){
					throw new CoreException("Servizio non individuato nel messaggio");
				}
				if(busta.getVersioneServizio()==null){
					throw new CoreException("VersioneServizio non individuato nel messaggio");
				}
				this.idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(busta.getTipoServizio(), busta.getServizio(), 
						busta.getTipoDestinatario(), busta.getDestinatario(), 
						busta.getVersioneServizio());
				this.idServizio.getSoggettoErogatore().setCodicePorta(this.registroServiziReader.getDominio(this.idServizio.getSoggettoErogatore(), null, this.protocolFactory, requestInfo));
				this.idServizio.setAzione(busta.getAzione());
				requestInfo.setIdServizio(this.idServizio);
				
				List<PortaApplicativa> listPa = this.configurazionePdDReader.getPorteApplicative(this.idServizio, false);
				/**if(listPa.size()<=0){
					throw new Exception("Non esiste alcuna porta applicativa indirizzabile tramite il servizio ["+idServizio+"]");
				}*/
				// NOTA: la pa potra' essere null nei casi di profili asincroni
				if(!listPa.isEmpty()){
					if(listPa.size()>1)
						throw new CoreException("Esiste più di una porta applicativa indirizzabile tramite il servizio ["+this.idServizio+"]");
					this.pa = listPa.get(0);
					
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(this.pa.getNome());
					idPA.setIdentificativiErogazione(new IdentificativiErogazione());
					idPA.getIdentificativiErogazione().setIdServizio(this.idServizio);
					RicezioneBusteServiceUtils.updatePortaApplicativaRequestInfo(requestInfo, this.logCore, 
							this.requestMessage,
							this.generatoreErrore, 
							ServicesUtils.getServiceIdentificationReader(this.logCore, requestInfo, this.registroServiziReader, this.configurazionePdDReader), this.msgDiag, 
							urlProtocolContext, idPA,
							this.pddContext);
					/**requestInfo.getProtocolContext().setInterfaceName(pa.getNome());*/
				}
				
			}catch(Exception e){
				
				logDebug(this.logCore, e.getMessage(),e); // lascio come debug puo' essere utile
				
				/**boolean checkAsSecondaFaseAsincrono = false;
				try{
					if(idServizio!=null){
						IProtocolConfiguration config = this.protocolFactory.createProtocolConfiguration();
						if(config.isSupportato(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO) || 
								config.isSupportato(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO)	) {
							Busta busta = this.protocolFactory.createValidazioneSintattica().getBusta_senzaControlli(requestMessage);
							if(busta!=null && busta.getProfiloDiCollaborazione()!=null) {
								if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(busta.getProfiloDiCollaborazione()) ||
										org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(busta.getProfiloDiCollaborazione())
									) {
									if(busta.getRiferimentoMessaggio()!=null) {
										checkAsSecondaFaseAsincrono = true;
									}
								}
							}
						}
					}
				}catch(Exception eAsincronoCheck){
					logError(logCore, "Errore durante il controllo della presenza di un profilo asincrono: "+eAsincronoCheck.getMessage(),eAsincronoCheck);
				}
				
				if(checkAsSecondaFaseAsincrono==false) {*/
				
				ServiceBindingConfiguration bindingConfig = requestInfo.getBindingConfig();
				if(!bindingConfig.existsContextUrlMapping()){
					IntegrationFunctionError integrationFunctionError = null;
					if(this.idServizio!=null){
						this.msgDiag.addKeywords(this.idServizio);
						if(this.idServizio.getAzione()==null){
							this.msgDiag.addKeyword(CostantiPdD.KEY_AZIONE_BUSTA_RICHIESTA, "-");
						}
						this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idBusta!=null ? idBusta : "-");
						this.msgDiag.addKeyword(CostantiPdD.KEY_PROFILO_COLLABORAZIONE, profiloBusta!=null ? profiloBusta : "-");
						this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente.identificazionePerServizio");
						integrationFunctionError = IntegrationFunctionError.NOT_FOUND;
					}
					else{
						this.msgDiag.addKeywordErroreProcessamento(e);
						this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
						integrationFunctionError = IntegrationFunctionError.API_IN_UNKNOWN;
					}
					
					// passo volutamente null come msgDiag poichè ho generato prima il diagnostico
					setSOAPFaultProcessamento(integrationFunctionError,this.logCore,null,
							ErroriIntegrazione.ERRORE_450_PA_INESISTENTE.getErroreIntegrazione(),e,
							"IdentificazioneIDServizio");
					this.openspcoopstate.releaseResource();
					return;
				}
			}
		}
		else{
			// L'interface name DEVE essere presente in questo caso
			try{
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(requestInfo.getProtocolContext().getInterfaceName());
				this.pa = this.configurazionePdDReader.getPortaApplicativaSafeMethod(idPA, requestInfo);
				// NOTA: la pa potra' essere null nei casi di profili asincroni
			}catch(Exception e){
				setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
							"getPA");
				this.openspcoopstate.releaseResource();
				return;
			}
		}
		if(this.pa!=null){
			this.msgDiag.updatePorta(this.pa.getNome(), requestInfo);
		}

		
		
		
		
		
		
		
		/* ------------ Identificazione Router Function  ------------- */	
		
		// PdD Function: router o normale PdD
		this.functionAsRouter = false;
		boolean soggettoVirtuale = false;
		this.msgDiag.mediumDebug("Esamina modalita' di ricezione (PdD/Router/SoggettoVirtuale)...");
		boolean existsSoggetto = false;
		try{
			if(this.idServizio!=null && this.idServizio.getSoggettoErogatore()!=null) {
				existsSoggetto = this.configurazionePdDReader.existsSoggetto(this.idServizio.getSoggettoErogatore(), requestInfo);
			}
		}catch(Exception e){
			setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
					"existsSoggetto("+this.idServizio.getSoggettoErogatore().toString()+")");
			this.openspcoopstate.releaseResource();
			return;
		}
		if(!existsSoggetto){
			// La PdD non gestisce il soggetto destinatario della busta.
			// Controllo adesso che sia abilitata la funzione di Router per la PdD, altrimenti nel successivo
			// modulo verra' generato un errore di soggetto non gestito.
			this.msgDiag.mediumDebug("Raccolta identita router...");
			boolean routerFunctionActive = false;
			try{
				routerFunctionActive = this.configurazionePdDReader.routerFunctionActive();
			}catch(Exception e){
				setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_514_ROUTING_CONFIGURATION_ERROR),e,
						"ErroreCheckFunzionalitaRouterAttiva");
				this.openspcoopstate.releaseResource();
				return;
			}		
			if(routerFunctionActive){
				this.functionAsRouter = true;	
				try{
					this.identitaPdD = this.configurazionePdDReader.getRouterIdentity(this.protocolFactory, requestInfo);
				}catch(Exception e){
					setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_514_ROUTING_CONFIGURATION_ERROR),e,
							"ErroreRiconoscimentoIdentitaRouter");
					this.openspcoopstate.releaseResource();
					return;
				}
			}
			// else: assume identita di default.
		}else{
			// identita assume quella dell'erogatore
			this.msgDiag.mediumDebug("Raccolta identita porta di dominio...");
			String dominio = null;
			try{
				dominio = this.configurazionePdDReader.getIdentificativoPorta(this.idServizio.getSoggettoErogatore(),this.protocolFactory, requestInfo);
				if(dominio==null){
					throw new CoreException("Dominio is null");
				}
			}catch(Exception e){
				setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
						"ErroreRiconoscimentoIdentitaPdD");
				this.openspcoopstate.releaseResource();
				return;
			}
			this.identitaPdD = new IDSoggetto(this.idServizio.getSoggettoErogatore().getTipo(),
					this.idServizio.getSoggettoErogatore().getNome(),dominio);
		}
		if(this.functionAsRouter){
			this.msgContext.setTipoPorta(TipoPdD.ROUTER);
		}
		
		// Context
		this.msgContext.setIdentitaPdD(this.identitaPdD);
		// GeneratoreErrore
		this.generatoreErrore.updateDominio(this.identitaPdD);
		// Raccolta dati Busta Ricevuta
		this.parametriGenerazioneBustaErrore.setIdentitaPdD(this.identitaPdD);
		this.parametriInvioBustaErrore.setIdentitaPdD(this.identitaPdD);
		this.parametriInvioBustaErrore.setFunctionAsRouter(this.functionAsRouter);
		// requestInfo
		requestInfo.setIdentitaPdD(this.identitaPdD);
		// altri
		this.msgDiag.setDominio(this.identitaPdD); // imposto anche il dominio nel msgDiag
		this.parametriGenerazioneBustaErrore.setMsgDiag(this.msgDiag);
		this.parametriInvioBustaErrore.setMsgDiag(this.msgDiag);
		
		
		
		
		
		
		
		
		
		
		
		/* --------------- Header Integrazione (viene letto solo se tracciamento e' abilitato altrimenti significa che il punto di ingresso ha gia gestito la lettura) --------------- */
		this.msgDiag.mediumDebug("Header integrazione...");
		if(propertiesReader.processHeaderIntegrazionePARequest(this.functionAsRouter)){
			if(this.functionAsRouter ){
				this.msgDiag.highDebug("Header integrazione (Default gestori integrazione Router)");
				if(RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.containsKey(this.protocolFactory.getProtocol()))
					this.tipiIntegrazionePA = RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.get(this.protocolFactory.getProtocol());
				else
					this.tipiIntegrazionePA = RicezioneBuste.defaultGestoriIntegrazionePA;
			}else{
				this.msgDiag.highDebug("Header integrazione (Gestori integrazione...)");
				if(this.pa!=null && this.msgContext.isTracciamentoAbilitato()){
					this.msgDiag.mediumDebug("Lettura header di integrazione...");
					try {
						this.tipiIntegrazionePA = this.configurazionePdDReader.getTipiIntegrazione(this.pa);
					} catch (Exception e) {
						setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
								"configurazionePdDReader.getTipiIntegrazione(pa)");
						this.openspcoopstate.releaseResource();
						return;
					}
				}
				this.msgDiag.highDebug("Header integrazione (Gestori integrazione terminato)");
				if (this.tipiIntegrazionePA == null){
					if(RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.containsKey(this.protocolFactory.getProtocol()))
						this.tipiIntegrazionePA = RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.get(this.protocolFactory.getProtocol());
					else
						this.tipiIntegrazionePA = RicezioneBuste.defaultGestoriIntegrazionePA;
				}
			}
		}
		this.headerIntegrazioneRichiesta = new HeaderIntegrazione(this.idTransazione);
		InRequestPAMessage inRequestPAMessage = null;
		if(this.tipiIntegrazionePA!=null){
			this.msgDiag.highDebug("Header integrazione (Impostazione ...)");
			inRequestPAMessage = new InRequestPAMessage();
			inRequestPAMessage.setMessage(this.requestMessage);
			inRequestPAMessage.setUrlProtocolContext(this.msgContext.getUrlProtocolContext());
			inRequestPAMessage.setPortaApplicativa(this.pa);
			inRequestPAMessage.setServizio(this.idServizio);
			for (int i = 0; i < this.tipiIntegrazionePA.length; i++) {
				try {
					IGestoreIntegrazionePA gestore = pluginLoader.newIntegrazionePortaApplicativa(this.tipiIntegrazionePA[i]);
					if(gestore!=null){
						String classType = null;
						try {
							classType = gestore.getClass().getName();
							AbstractCore.init(gestore, this.pddContext, this.protocolFactory);
						} catch (Exception e) {
							throw new CoreException(
									"Riscontrato errore durante l'inizializzazione della classe (IGestoreIntegrazionePA) ["+ classType
											+ "] da utilizzare per la gestione dell'integrazione delle erogazioni di tipo ["+ this.tipiIntegrazionePA[i] + "]: " + e.getMessage());
						}
					
						gestore.readInRequestHeader(this.headerIntegrazioneRichiesta, inRequestPAMessage);
					}
					else {
						this.msgDiag.logErroreGenerico("Gestore ["
								+ this.tipiIntegrazionePA[i]+ "], per la lettura dell'header di integrazione, non inizializzato",
								"gestoriIntegrazionePASoap.get("+this.tipiIntegrazionePA[i]+")");
					}		
				} catch (Exception e) {
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,this.tipiIntegrazionePA[i]);
					this.msgDiag.addKeywordErroreProcessamento(e);
					this.msgDiag.logPersonalizzato("headerIntegrazione.letturaFallita");
				}
			}
			if(this.headerIntegrazioneRichiesta!=null){
				this.msgDiag.highDebug("Header integrazione (set context...)");
				if(this.msgContext.getIdentitaServizioApplicativoFruitore()!=null)
					servizioApplicativoFruitore = this.msgContext.getIdentitaServizioApplicativoFruitore();
				else
					servizioApplicativoFruitore = this.headerIntegrazioneRichiesta.getServizioApplicativo();

				this.parametriGenerazioneBustaErrore.setServizioApplicativoFruitore(servizioApplicativoFruitore);
				this.parametriInvioBustaErrore.setServizioApplicativoFruitore(servizioApplicativoFruitore);
				this.generatoreErrore.updateInformazioniCooperazione(servizioApplicativoFruitore);
				
				if(this.msgContext.getIdCorrelazioneApplicativa()!=null)
					this.correlazioneApplicativa = this.msgContext.getIdCorrelazioneApplicativa();
				else
					this.correlazioneApplicativa = this.headerIntegrazioneRichiesta.getIdApplicativo();
				this.parametriGenerazioneBustaErrore.setCorrelazioneApplicativa(this.correlazioneApplicativa);
				this.parametriInvioBustaErrore.setCorrelazioneApplicativa(this.correlazioneApplicativa);
				
				this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, servizioApplicativoFruitore);
				this.msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA, this.correlazioneApplicativa);
				
				this.msgContext.getIntegrazione().setIdCorrelazioneApplicativa(this.correlazioneApplicativa);
				this.msgContext.getIntegrazione().setServizioApplicativoFruitore(servizioApplicativoFruitore);
				
				this.msgDiag.highDebug("Header integrazione (set context ok)");
			}
		}
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneBuste - Autorizzazione canale ...");
		ConfigurazioneCanaliNodo configurazioneCanaliNodo = null;
		try {
			if(!this.functionAsRouter) {
				configurazioneCanaliNodo = this.configurazionePdDReader.getConfigurazioneCanaliNodo();
			}
		} catch (Exception e) {
			setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
					"configurazionePdDReader.getConfigurazioneCanaliNodo()");
			this.openspcoopstate.releaseResource();
			return;
		}
		boolean canaleNonAutorizzato = false;
		try {
			if(configurazioneCanaliNodo!=null && configurazioneCanaliNodo.isEnabled()) {
			
				this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
				this.msgDiag.logPersonalizzato("autorizzazioneCanale.inCorso");
				
				String canaleApiInvocata = null;
				if(this.pa!=null) {
					String canalePorta = this.pa.getCanale();
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
						this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTORIZZAZIONE, "true");
						throw new CoreException("L'API invocata richiede un canale differente da quelli associati al nodo; invocazione non autorizzata");
					}
					else {
						this.msgDiag.logPersonalizzato("autorizzazioneCanale.effettuata");
					}
					
				}
				else {
					// saranno segnalati altri errori dovuti al non riconoscimento della porta
				}
				
			}
		} catch (Exception e) {
			
			String msgErrore = e.getMessage();
			
			this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
			this.msgDiag.logPersonalizzato("autorizzazioneCanale.fallita");
			
			if(canaleNonAutorizzato) {
				logError(this.logCore, e.getMessage(),e);
				setSOAPFaultIntestazione(IntegrationFunctionError.AUTHORIZATION_DENY,
						ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(msgErrore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
			}
			else {
				setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,null,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
						"autorizzazioneCanale");
			}
			this.openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneBuste - Raccolta dati Gestione CORS ...");
		// NOTA: i dati CORS sono memorizzati solamente nella porta principale e non in quelle di eventuali azioni delegate.
		//       deve quindi essere recuperata prima di sostituire la pa con una più specifica
		CorsConfigurazione cors = null;
		HttpServletRequest httpServletRequest = null;
		boolean effettuareGestioneCORS = false;
		try {
			if(requestInfo!=null && requestInfo.getProtocolContext()!=null) {
				httpServletRequest = requestInfo.getProtocolContext().getHttpServletRequest();	
			}
			
			if(httpServletRequest!=null && HttpRequestMethod.OPTIONS.name().equalsIgnoreCase(httpServletRequest.getMethod())) {
				
				Object nomePortaObject = this.pddContext.getObject(CostantiPdD.NOME_PORTA_INVOCATA);
				String nomePortaS = null;
				if(nomePortaObject instanceof String s) {
					nomePortaS = s;
				}
				PortaApplicativa paDefault = null;
				if(nomePortaS!=null) {
					IDPortaApplicativa idPAdefault = new IDPortaApplicativa();
					idPAdefault.setNome(nomePortaS);
					paDefault = this.configurazionePdDReader.getPortaApplicativaSafeMethod(idPAdefault, requestInfo);
				}
				if(paDefault!=null) {
					cors = this.configurazionePdDReader.getConfigurazioneCORS(paDefault);
				}
				else if(this.pa!=null) {
					cors = this.configurazionePdDReader.getConfigurazioneCORS(this.pa);
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
			setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
					"configurazionePdDReader.getConfigurazioneCORS(pa)");
			this.openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		
		
		/* ------------ Identificazione Azione  ------------- */	
		
		try{
			if(this.pa!=null && this.idServizio!=null){
				if(requestInfo.getIdServizio()!=null && requestInfo.getIdServizio().getAzione()!=null){
					// gia identificata
					this.idServizio.setAzione(requestInfo.getIdServizio().getAzione());
				}
				else{
					this.idServizio.setAzione(this.configurazionePdDReader.getAzione(this.pa, urlProtocolContext, requestInfo, this.requestMessage, null,
							this.headerIntegrazioneRichiesta, this.msgContext.getIdModulo().endsWith(IntegrationManager.ID_MODULO), this.protocolFactory));
				}
				requestInfo.setIdServizio(this.idServizio);
			}
		}catch(Exception e){
			
			boolean throwFault = true;
			if(StatoFunzionalita.ABILITATO.equals(cors.getStato()) && this.msgContext.isGestioneRisposta()) {
				throwFault = false;
			}
			if(throwFault) {
			
				IntegrationFunctionError integrationFunctinError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				if(e instanceof IdentificazioneDinamicaException) {
					
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.OPERAZIONE_NON_INDIVIDUATA, "true");
										
					integrationFunctinError = IntegrationFunctionError.OPERATION_UNDEFINED;
				}
				
				setSOAPFaultProcessamento(integrationFunctinError,this.logCore,this.msgDiag,
						ErroriIntegrazione.ERRORE_403_AZIONE_NON_IDENTIFICATA.getErroreIntegrazione(),e,
						"identificazioneDinamicaAzionePortaApplicativa");
				this.openspcoopstate.releaseResource();
				return;
				
			}
			else {
				effettuareGestioneCORS = true;
			}
			
		}
		
		
		
		
		

		
		
		
		Utilities.printFreeMemory("RicezioneBuste - Identificazione PA specifica per azione del servizio ...");
		
		this.msgDiag.mediumDebug("Lettura azione associato alla PA invocata...");
		if(this.idServizio!=null && this.idServizio.getAzione()!=null && this.pa!=null) {
			// verifico se esiste una porta applicativa piu' specifica
			IdentificazionePortaApplicativa identificazione = new IdentificazionePortaApplicativa(this.logCore, this.protocolFactory, 
					this.registroServiziReader, this.configurazionePdDReader, requestInfo,
					this.pa);
			String action = this.idServizio.getAzione();
			if(identificazione.find(action)) {
				IDPortaApplicativa idPAAction = identificazione.getIDPortaApplicativa(action);
				if(idPAAction!=null) {
					
					this.requestMessage.addContextProperty(CostantiPdD.NOME_PORTA_INVOCATA, this.pa.getNome()); // prima di aggiornare la porta applicativa
										
					this.pa = identificazione.getPortaApplicativa(action);
					this.msgDiag.addKeyword(CostantiPdD.KEY_PORTA_APPLICATIVA, this.pa.getNome());
					this.msgDiag.updatePorta(this.pa.getNome(), requestInfo);
					if(this.requestMessage.getTransportRequestContext()!=null) {
						this.requestMessage.getTransportRequestContext().setInterfaceName(this.pa.getNome());
					}
					
					this.pddContext.removeObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_CONFIGURAZIONE);
					try {
						Map<String, String> configProperties = this.configurazionePdDReader.getProprietaConfigurazione(this.pa);
			            if (configProperties != null && !configProperties.isEmpty()) {
			               this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_CONFIGURAZIONE, configProperties);
			            }
					}catch(Exception e) {
						logError(this.logCore, "Errore durante la lettura delle proprietà di configurazione della porta applicativa [" + this.pa.getNome() + "]: " + e.getMessage(), e);
					}
				}
			}else {
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.API_NON_INDIVIDUATA, "true");
				
				this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, identificazione.getErroreIntegrazione().getDescrizione(this.protocolFactory));
				this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");	
				
				// passo volutamente null come msgDiag poichè ho generato prima il diagnostico
				IntegrationFunctionError integrationFunctionError = null;
				if(CodiceErroreIntegrazione.CODICE_401_PORTA_INESISTENTE.equals(identificazione.getErroreIntegrazione().getCodiceErrore())){
					integrationFunctionError = IntegrationFunctionError.API_IN_UNKNOWN;
				}else{
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				setSOAPFaultProcessamento(integrationFunctionError,this.logCore,null,
						identificazione.getErroreIntegrazione(),null,
						"IdentificazionePASpecificaPerAzione");
				this.openspcoopstate.releaseResource();
				return;
			}
		}
		
		
		

		
		
		
		
		// ------------- Informazioni Integrazione -----------------------------
		
		this.msgDiag.mediumDebug("Aggiungo informazioni di integrazione dinamica nel contesto ...");
				
		try {
			if(this.pa!=null) {
				this.configurazionePdDReader.setInformazioniIntegrazioneDinamiche(this.logCore, urlProtocolContext, this.pddContext, this.pa);
			}
		} 
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e, "setInformazioniIntegrazioneDinamiche");
			setSOAPFaultProcessamento(IntegrationFunctionError.BAD_REQUEST,this.logCore, 
					null, //msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
					"setInformazioniIntegrazioneDinamiche");
			this.openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		
		
		
		
		
		// ------------- Dump richiesta-----------------------------
			
		this.msgDiag.mediumDebug("Dump richiesta ...");
		
		DumpConfigurazione dumpConfig = null;
		try {
			if(this.pa!=null) {
				dumpConfig = this.configurazionePdDReader.getDumpConfigurazione(this.pa);
			}
			else {
				dumpConfig = this.configurazionePdDReader.getDumpConfigurazionePortaApplicativa();
			}
			internalObjects.put(CostantiPdD.DUMP_CONFIG, dumpConfig);
		} 
		catch (Exception e) {
			setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
					"readDumpConfigurazione");
			this.openspcoopstate.releaseResource();
			return;
		}
		
		Dump dumpApplicativo = new Dump(this.identitaPdD,
				this.msgContext.getIdModulo(),  null,
				null, this.idServizio,
				this.msgContext.getTipoPorta(),this.msgDiag.getPorta(),this.inRequestContext.getPddContext(),
				this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta(),
				dumpConfig);
		dumpApplicativo.dumpRichiestaIngresso(this.requestMessage,this.inRequestContext.getConnettore().getUrlProtocolContext());
		internalObjects.put(CostantiPdD.DUMP_RICHIESTA_EFFETTUATO, true);
		
		
		
		
		
		
		
		
		
		
		
		
		


		/* ------------  
		 * URL Mapping 
		 * (Identificazione dati sul soggetto fruitore e sulle funzionalità di protocollo ed identificativo di protocollo)
		 * In questo punto l'idServizio contiene tutte le informazioni necessarie per accedere al registro se le funzionalità di protocollo sono statiche
		 * Deve contenere anche l'azione, visto che quest'ultima influenza il profili di collaborazione 
		 * ------------- */	
		
		Servizio infoServizio = null;
		String id = null;
		boolean generazioneListaTrasmissioni = false;
		InformazioniServizioURLMapping is = null;
		boolean identitaServizioValida = false;
		String nomeRegistroForSearch = null; // qualsiasi registro
		try{
			is = new InformazioniServizioURLMapping(this.requestMessage,this.protocolFactory,urlProtocolContext,
					this.logCore, this.msgContext.getIdModuloAsIDService(),
					propertiesReader.getCustomContexts());
			logDebug(this.logCore, "InformazioniServizioTramiteURLMapping: "+is.toString());		
			
		
			Credential identity = null;
						
			// Read Identity
			if(is.existsIdentityBasedIdentificationMode() &&
				connectorInMessage!=null) {
				identity = connectorInMessage.getCredential();
			}
			
			// Refresh dati su mittente
			this.soggettoFruitore = new IDSoggetto();
			IDSoggetto headerIntegrazioneRichiestaSoggettoMittente = null;
			if(this.headerIntegrazioneRichiesta!=null && this.headerIntegrazioneRichiesta.getBusta()!=null){
				headerIntegrazioneRichiestaSoggettoMittente = new IDSoggetto(this.headerIntegrazioneRichiesta.getBusta().getTipoMittente(), 
						this.headerIntegrazioneRichiesta.getBusta().getMittente());
			}
			is.refreshDati(this.soggettoFruitore, identity, headerIntegrazioneRichiestaSoggettoMittente);
			
			// Reimposto a null se il refresh non ha trovato dati.
			if(this.soggettoFruitore.getTipo()==null && this.soggettoFruitore.getNome()==null){
				this.soggettoFruitore = null;
			}			
			
			// Aggiorno domini dei soggetti se completamente ricostruiti tramite url mapping differente da plugin based
			if(this.soggettoFruitore!=null && this.soggettoFruitore.getTipo()!=null && this.soggettoFruitore.getNome()!=null){
				try {
					this.soggettoFruitore.setCodicePorta(this.registroServiziReader.getDominio(this.soggettoFruitore, nomeRegistroForSearch, this.protocolFactory, requestInfo));
				} catch (Exception e) {
					// ignore
					/**e.printStackTrace();*/
				}
			}
			requestInfo.setFruitore(this.soggettoFruitore);
			
			// Check id
			boolean identitaSoggettoErogatoreValida = this.idServizio!=null &&
					this.idServizio.getSoggettoErogatore()!=null && 
					this.idServizio.getSoggettoErogatore().getTipo()!=null &&
					this.idServizio.getSoggettoErogatore().getNome()!=null;
			identitaServizioValida = identitaSoggettoErogatoreValida && 
					this.idServizio!=null && this.idServizio.getNome()!=null && this.idServizio.getTipo()!=null && this.idServizio.getVersione()!=null;
						
			// ID Protocollo
			id = null;
			if(is.isStaticBasedIdentificationMode_IdProtocol()){
				Imbustamento imbustamento = new Imbustamento(this.logCore, this.protocolFactory, this.openspcoopstate.getStatoRichiesta());
				IDSoggetto idSoggetto = null;
				if(identitaSoggettoErogatoreValida) {
					idSoggetto = this.idServizio.getSoggettoErogatore();
				}
				else {
					idSoggetto = propertiesReader.getIdentitaPortaDefault(this.protocolFactory.getProtocol(), requestInfo);
				}
				id = 
					imbustamento.buildID(idSoggetto, 
							(String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE), 
							propertiesReader.getGestioneSerializableDBAttesaAttiva(),
							propertiesReader.getGestioneSerializableDBCheckInterval(),
							RuoloMessaggio.RICHIESTA);
			}
			
			// Lista trasmissioni
			generazioneListaTrasmissioni = is.isGenerateListaTrasmissione();
			
			// InfoServizio (NOTA: lasciare per ultimo)
			if(is.isStaticBasedIdentificationMode_InfoProtocol()){
				if(identitaServizioValida) {
					infoServizio = this.registroServiziReader.getInfoServizio(this.soggettoFruitore, this.idServizio,nomeRegistroForSearch,true, true, requestInfo);
				}
				else {
					infoServizio = new Servizio(); // se l'id servizio non e' valido poi viene segnalato dal motore della validazione
				}
			}
			else{
				infoServizio = new Servizio();
			}
		}
		catch(DriverRegistroServiziAzioneNotFound e){
			
			boolean throwFault = true;
			if(StatoFunzionalita.ABILITATO.equals(cors.getStato()) && this.msgContext.isGestioneRisposta()) {
				throwFault = false;
				if(TipoGestioneCORS.TRASPARENTE.equals(cors.getTipo())) {
					// per poter continuare l'elaborazione ho bisogno dell'id servizio
					try {
						if(is.isStaticBasedIdentificationMode_InfoProtocol() &&
							identitaServizioValida) {
							infoServizio = this.registroServiziReader.getInfoServizio(this.soggettoFruitore, this.idServizio,nomeRegistroForSearch,true, false, requestInfo);
						}
					}catch(Exception eGetInfoServizio) {
						throwFault = true;
					}
				}
			}
			if(throwFault) {
			
				String azione = "";
				if(this.idServizio.getAzione()!=null) {
					azione = "(azione:"+ this.idServizio.getAzione()+ ") ";
				}
				
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.OPERAZIONE_NON_INDIVIDUATA, "true");
				
				setSOAPFaultProcessamento(IntegrationFunctionError.OPERATION_UNDEFINED,this.logCore,this.msgDiag,
						ErroriIntegrazione.ERRORE_423_SERVIZIO_CON_AZIONE_SCORRETTA.
						getErrore423_ServizioConAzioneScorretta(azione+ e.getMessage()),e,
						"readProtocolInfo");
				this.openspcoopstate.releaseResource();
				return;
				
			}
			else {
				effettuareGestioneCORS = true;
			}
			
		}
		catch(Exception e){
			setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
					"readProtocolInfo");
			this.openspcoopstate.releaseResource();
			return;
		}
		
		
		
		// Gestione CORS
		
		if(!effettuareGestioneCORS) {
			if(this.pddContext.containsKey(CostantiPdD.CORS_PREFLIGHT_REQUEST_SOAP)) {
				effettuareGestioneCORS = true;
			}
			else {
				// devo verificare se si tratta di una azione matched poichè è stato inserito un tipo http method 'qualsiasi'
				if(propertiesReader.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneBuste() &&
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
					CORSWrappedHttpServletResponse res = new CORSWrappedHttpServletResponse(true);
					corsFilter.doCORS(httpServletRequest, res, CORSRequestType.PRE_FLIGHT, true);
					if(this.msgContext.getResponseHeaders()==null) {
						this.msgContext.setResponseHeaders(new HashMap<>());
					}
					this.msgContext.getResponseHeaders().putAll(res.getHeadersValues());
					this.msgContext.setMessageResponse(res.buildMessage());
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CORS_PREFLIGHT_REQUEST_VIA_GATEWAY, "true");
				}catch(Exception e) {
					// un eccezione non dovrebbe succedere
					setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
							"gestioneCORS");
					this.openspcoopstate.releaseResource();
					return;
				}
				
				this.openspcoopstate.releaseResource();
				return;
					
			}
			else {
				
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CORS_PREFLIGHT_REQUEST_TRASPARENTE, "true");
				
			}
			
		}
	

		
		
		
		

		
		
		
		/* ------------  Busta con i dati identificati tramite PA e URLMapping ------------- */	
		
		Busta bustaURLMapping = null;
		try{
			// Build Busta
			bustaURLMapping = new Busta(this.protocolFactory,infoServizio, this.soggettoFruitore, 
					this.idServizio!=null ? this.idServizio.getSoggettoErogatore() : null, 
					id, generazioneListaTrasmissioni);
			if(infoServizio!=null && infoServizio.getCollaborazione() &&
				// in questo punto sara' true solo se infoServizio è stato letto dal registro E siamo in 'isStaticBasedIdentificationMode_InfoProtocol'
				this.headerIntegrazioneRichiesta!=null && this.headerIntegrazioneRichiesta.getBusta()!=null && 
				this.headerIntegrazioneRichiesta.getBusta().getIdCollaborazione()!=null) {
				bustaURLMapping.setCollaborazione(this.headerIntegrazioneRichiesta.getBusta().getIdCollaborazione());
			}
			if(infoServizio!=null && infoServizio.getIdRiferimentoRichiesta() &&
				// in questo punto sara' true solo se infoServizio è stato letto dal registro E siamo in 'isStaticBasedIdentificationMode_InfoProtocol'
				this.headerIntegrazioneRichiesta!=null && this.headerIntegrazioneRichiesta.getBusta()!=null &&
				this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio()!=null) {
				bustaURLMapping.setRiferimentoMessaggio(this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio());
				this.msgContext.getProtocol().setRiferimentoAsincrono(this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio());
			}
			TipoOraRegistrazione tipoOraRegistrazione = propertiesReader.getTipoTempoBusta(null);
			bustaURLMapping.setTipoOraRegistrazione(tipoOraRegistrazione, this.traduttore.toString(tipoOraRegistrazione));
			if(bustaURLMapping.sizeListaTrasmissioni()>0){
				for (Trasmissione trasmissione : bustaURLMapping.getListaTrasmissioni()) {
					trasmissione.setTempo(tipoOraRegistrazione, this.traduttore.toString(tipoOraRegistrazione));
				}
			}
			bustaURLMapping.setServizioApplicativoFruitore(servizioApplicativoFruitore);
		}catch(Exception e){
			setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
					"bustaURLMapping");
			this.openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		

		
		
		
		
		
		
		
		

		
				
		/* ------------  Processamento Busta Ricevuta ------------- */	
		
		this.ricezioneBusteGeneratoreBustaErrore = new RicezioneBusteGeneratoreBustaErrore(this.msgContext, this.generatoreErrore);
		
		// ValidazioneSintattica
		this.msgDiag.mediumDebug("Validazione busta ricevuta in corso...");
		ProprietaValidazione properties = new ProprietaValidazione();
		this.readQualifiedAttribute = propertiesReader.isReadQualifiedAttribute(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
		
		Validatore validatore = new Validatore(this.requestMessage,this.pddContext,properties, this.openspcoopstate.getStatoRichiesta(),this.readQualifiedAttribute, this.protocolFactory);
		
		
		this.msgDiag.logPersonalizzato("validazioneSintattica");
		if(!validatore.validazioneSintattica(bustaURLMapping, true)){

			// Provo a reperire il dominio se ho l'informazione sul destinatario valida
			Busta erroreIntestazione = null;
			boolean setDestinatarioDefault = true;
			if( validatore.getBustaErroreHeaderIntestazione()!=null){
				erroreIntestazione = validatore.getBustaErroreHeaderIntestazione();
				
				// Imposto Identificativo Richiesta
				if(erroreIntestazione.getID()!=null){
					this.msgDiag.setIdMessaggioRichiesta(erroreIntestazione.getID());
					this.msgContext.getProtocol().setIdRichiesta(erroreIntestazione.getID());
				}
				
				IProtocolManager protocolManager = this.protocolFactory.createProtocolManager();
				if( (!protocolManager.getKeywordMittenteSconosciuto().equals(erroreIntestazione.getDestinatario())) &&
						(!protocolManager.getKeywordTipoMittenteSconosciuto().equals(erroreIntestazione.getTipoDestinatario()))
				){
					try{
						String dominioTmp = this.configurazionePdDReader.getIdentificativoPorta(new IDSoggetto(erroreIntestazione.getTipoDestinatario(),
								erroreIntestazione.getDestinatario()),this.protocolFactory, requestInfo);
						if(dominioTmp!=null){
							this.identitaPdD.setCodicePorta(dominioTmp);
							this.identitaPdD.setTipo(erroreIntestazione.getTipoDestinatario());
							this.identitaPdD.setNome(erroreIntestazione.getDestinatario());
							setDestinatarioDefault = false;
							this.parametriGenerazioneBustaErrore.setIdentitaPdD(this.identitaPdD);
							this.parametriInvioBustaErrore.setIdentitaPdD(this.identitaPdD);
						}
					}catch(Exception e){
						// ignore
					}	
				}
				
				// Imposto i domini corretti, se sono stati impostati dei mittenti e tipi mittenti esistenti
				if(erroreIntestazione.getMittente()!=null && erroreIntestazione.getTipoMittente()!=null){
					try{
						String dominio = this.registroServiziReader.getDominio(new IDSoggetto(erroreIntestazione.getTipoMittente(), erroreIntestazione.getMittente()), null, this.protocolFactory, requestInfo);
						if(dominio!=null)
							erroreIntestazione.setIdentificativoPortaMittente(dominio);
					}catch(Exception e){
						// ignore
					}
				}
				if(erroreIntestazione.getDestinatario()!=null && erroreIntestazione.getTipoDestinatario()!=null){
					try{
						String dominio = this.registroServiziReader.getDominio(new IDSoggetto(erroreIntestazione.getTipoDestinatario(), erroreIntestazione.getDestinatario()), null, this.protocolFactory, requestInfo);
						if(dominio!=null)
							erroreIntestazione.setIdentificativoPortaDestinatario(dominio);
					}catch(Exception e){
						// ignore
					}
				}
			}


			// Provo a tracciare/dumpare la busta di richiesta arrivata malformata
			try{
				if(erroreIntestazione!=null){
					this.msgDiag.addKeywords(erroreIntestazione,true);
					if(erroreIntestazione.getMittente()!=null || erroreIntestazione.getTipoMittente()!=null){
						this.msgDiag.logPersonalizzato("ricezioneMessaggio");
					}
					else{
						this.msgDiag.logPersonalizzato("ricezioneMessaggio.mittenteAnonimo");
					}
				}

				if(this.msgContext.isTracciamentoAbilitato() && erroreIntestazione!=null){
					this.msgDiag.mediumDebug("Tracciamento busta di richiesta...");

					// Tracciamento richiesta
					Tracciamento tracciamentoErroreIntestazione = new Tracciamento(this.identitaPdD,
							this.msgContext.getIdModulo(),
							this.inRequestContext.getPddContext(),
							this.msgContext.getTipoPorta(),this.msgDiag.getPorta(),
							this.configurazionePdDReader);
					
					erroreIntestazione.setServizioApplicativoFruitore(servizioApplicativoFruitore);
					String dettaglioErrore = null;
					if(erroreIntestazione!=null){
						this.msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, erroreIntestazione.toStringListaEccezioni(this.protocolFactory));
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_BUSTA, CostantiPdD.TIPO_VALIDAZIONE_BUSTA_SINTATTICA);
						dettaglioErrore = this.msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_VALIDAZIONE_NON_RIUSCITA);
					}
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(dettaglioErrore);
					
					// Prima di tracciare elimino la lista di eccezioni
					List<Eccezione> eccezioniRiscontrate = erroreIntestazione.cloneListaEccezioni();
					while(erroreIntestazione.sizeListaEccezioni()>0){
						erroreIntestazione.removeEccezione(0);
					}
					
					// Tracciamento Busta Ricevuta
					tracciamentoErroreIntestazione.registraRichiesta(this.requestMessage,null,validatore.getHeaderProtocollo_senzaControlli(),erroreIntestazione,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
							this.correlazioneApplicativa);
					
					// Riaggiungo eccezioni riscontrate per tracciare risposta
					while(!eccezioniRiscontrate.isEmpty()){
						erroreIntestazione.addEccezione(eccezioniRiscontrate.remove(0));
					}
				}
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e,"TracciamentoBustaMalformata");
				logError(this.logCore, "Riscontrato errore durante il tracciamento della busta malformata ricevuta",e);
			}

			if(erroreIntestazione!=null){
				this.msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, erroreIntestazione.toStringListaEccezioni(this.protocolFactory));
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_BUSTA, CostantiPdD.TIPO_VALIDAZIONE_BUSTA_SINTATTICA);
				this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_VALIDAZIONE_NON_RIUSCITA);
				
				if(this.msgContext.isGestioneRisposta()){

					if(setDestinatarioDefault){
						// Non sono riuscito a prendere il dominio del destinatario.
						// Usero' come mittente della busta quella della porta di dominio di default
						erroreIntestazione.setDestinatario(this.identitaPdD.getNome());
						erroreIntestazione.setTipoDestinatario(this.identitaPdD.getTipo());
						erroreIntestazione.setIdentificativoPortaDestinatario(this.identitaPdD.getCodicePorta());
					}

					Tracciamento tracciamentoErroreIntestazione = new Tracciamento(this.identitaPdD,
							this.msgContext.getIdModulo(),
							this.inRequestContext.getPddContext(),
							this.msgContext.getTipoPorta(),this.msgDiag.getPorta(),
							this.configurazionePdDReader);
					
					this.parametriGenerazioneBustaErrore.setTracciamento(tracciamentoErroreIntestazione);
					this.parametriGenerazioneBustaErrore.setBusta(erroreIntestazione);
					this.parametriGenerazioneBustaErrore.setError(erroreIntestazione.cloneListaEccezioni());
					
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					if(validatore.getErrore_integrationFunctionError()!=null) {
						integrationFunctionError = validatore.getErrore_integrationFunctionError();
					}
					this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
					
					OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreValidazione(this.parametriGenerazioneBustaErrore);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreValidazione
					
					this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
					this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
				}
			}else{
				
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
				if(validatore.getErrore_integrationFunctionError()!=null) {
					integrationFunctionError = validatore.getErrore_integrationFunctionError();
				}
				try{
					this.msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, validatore.getErrore().getDescrizione(this.protocolFactory));
				}catch(Exception e){
					logError(this.logCore, "getDescrizione BadRequest Error:"+e.getMessage(),e);
				}
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_BUSTA, CostantiPdD.TIPO_VALIDAZIONE_BUSTA_SINTATTICA);
				this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_VALIDAZIONE_NON_RIUSCITA);
				setSOAPFaultIntestazione(integrationFunctionError, validatore.getErrore());
			}
			this.openspcoopstate.releaseResource();
			return;
		}
				
		this.idServizio = validatore.getIDServizio();
		this.bustaRichiesta = validatore.getBusta();
		BustaRawContent<?> soapHeaderElement = validatore.getHeaderProtocollo();
		
		if(this.bustaRichiesta!=null) {
			try{
				if(infoServizio.getCollaborazione() && this.bustaRichiesta.getCollaborazione()==null) {
					InitialIdConversationType initial = this.protocolFactory.createProtocolConfiguration().isGenerateInitialIdConversation(TipoPdD.APPLICATIVA, FunzionalitaProtocollo.COLLABORAZIONE);
					if(InitialIdConversationType.ID_TRANSAZIONE.equals(initial)) {
						this.bustaRichiesta.setCollaborazione(this.idTransazione);
					}
					else if(InitialIdConversationType.ID_MESSAGGIO.equals(initial)) {
						this.bustaRichiesta.setCollaborazione(this.bustaRichiesta.getID());
					}
				}
			}catch(Exception e){
				setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,this.logCore,this.msgDiag,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
						"setCollaborazione");
				this.openspcoopstate.releaseResource();
				return;
			}
		}
		
		
		
		
		
		
		
		/* ------------  Controllo che i dati ottenuti dal validatore ed i dati ottenuti dalla RequestInfo coincidano ------------- */	
		
		
		// Controllo che i dati ottenuti dal validatore ed i dati ottenuti dalla RequestInfo coincidano
		if(requestInfo.getIdServizio()!=null){
			// Altrimenti i dati l'ho letti dalla busta e coincidono per forza
			String oggetto = null;
			String datoBusta = null;
			String datoPA = null;
			String locationPA = requestInfo.getProtocolContext().getInterfaceName();
			if(!requestInfo.getIdServizio().getTipo().equals(this.idServizio.getTipo())){
				oggetto = "Tipo del servizio";
				datoBusta = this.idServizio.getTipo();
				datoPA = requestInfo.getIdServizio().getTipo();
			}
			else if(!requestInfo.getIdServizio().getNome().equals(this.idServizio.getNome())){
				oggetto = "Servizio";
				datoBusta = this.idServizio.getNome();
				datoPA = requestInfo.getIdServizio().getNome();
			}
			else if(requestInfo.getIdServizio().getVersione().intValue() != this.idServizio.getVersione().intValue()){
				oggetto = "VersioneServizio";
				datoBusta = this.idServizio.getVersione().intValue()+"";
				datoPA = requestInfo.getIdServizio().getVersione().intValue()+"";
			}
			else if(requestInfo.getIdServizio().getAzione()!=null && !requestInfo.getIdServizio().getAzione().equals(this.idServizio.getAzione())){
				oggetto = "Azione";
				datoBusta = this.idServizio.getAzione();
				datoPA = requestInfo.getIdServizio().getAzione();
			}
			else if(!requestInfo.getIdServizio().getSoggettoErogatore().getTipo().equals(this.idServizio.getSoggettoErogatore().getTipo())){
				oggetto = "Tipo del soggetto erogatore";
				datoBusta = this.idServizio.getSoggettoErogatore().getTipo();
				datoPA = requestInfo.getIdServizio().getSoggettoErogatore().getTipo();
			}
			else if(!requestInfo.getIdServizio().getSoggettoErogatore().getNome().equals(this.idServizio.getSoggettoErogatore().getNome())){
				oggetto = "Soggetto erogatore";
				datoBusta = this.idServizio.getSoggettoErogatore().getNome();
				datoPA = requestInfo.getIdServizio().getSoggettoErogatore().getNome();
			}
			if(oggetto!=null){
				setSOAPFaultProcessamento(IntegrationFunctionError.BAD_REQUEST,this.logCore,this.msgDiag,
						ErroriIntegrazione.ERRORE_455_DATI_BUSTA_DIFFERENTI_PA_INVOCATA.
							getErrore455DatiBustaDifferentiDatiPAInvocata(oggetto, datoBusta, datoPA, locationPA),null,
						"ConfrontoDatiBustaConDatiInvocazionePortaApplicativa");
				this.openspcoopstate.releaseResource();
				return;
			}
		}
		
		
		
		
		
		
		
		/* ----------- Ruolo Busta Ricevuta ------------ */
		
		if(!this.functionAsRouter){
			
			if( validatore.getBusta()!=null && 
					(
							(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(validatore.getBusta().getProfiloDiCollaborazione())) ||
							(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(validatore.getBusta().getProfiloDiCollaborazione()))
					)
					&&
					this.openspcoopstate.resourceReleased()
				) {
				try{
					// inizializzo
					this.openspcoopstate.setUseConnection(true);
					this.openspcoopstate.initResource(this.identitaPdD, this.msgContext.getIdModulo(), this.idTransazione);
					validatore.updateState(this.openspcoopstate.getStatoRichiesta());
				}catch(Exception e){
					this.msgDiag.logErroreGenerico(e,"validator.getRuoloBustaRicevuta(false) initResources");
					setSOAPFaultProcessamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,this.logCore,this.msgDiag,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION),e,
							"initDatabaseResource");
					this.openspcoopstate.releaseResource();
					return;
				}
			}
			
			this.msgDiag.mediumDebug("Lettura Ruolo Busta...");
			try{
				this.ruoloBustaRicevuta = validatore.getRuoloBustaRicevuta(requestInfo.getProtocolServiceBinding(),false);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e,"validator.getRuoloBustaRicevuta(false)");
			}
			
			boolean checkConnection=false;
			if( validatore.getBusta()!=null) {
				if(!org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(validatore.getBusta().getProfiloDiCollaborazione())){
					if(!RuoloBusta.RICHIESTA.equals(this.ruoloBustaRicevuta)){
						checkConnection=true;
					}
				}
				else {
					if(validatore.getBusta().getSequenza()!=-1 && validatore.getBusta().getCollaborazione()!=null) {
						checkConnection=true;
					}
				}
			}
			if(checkConnection && this.openspcoopstate.resourceReleased()) {
				try{
					// inizializzo
					this.openspcoopstate.setUseConnection(true);
					this.openspcoopstate.initResource(this.identitaPdD, this.msgContext.getIdModulo(), this.idTransazione);
					validatore.updateState(this.openspcoopstate.getStatoRichiesta());
				}catch(Exception e){
					this.msgDiag.logErroreGenerico(e,"validator.getRuoloBustaRicevuta(false) initResources after read role");
					setSOAPFaultProcessamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,this.logCore,this.msgDiag,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION),e,
							"initDatabaseResource");
					this.openspcoopstate.releaseResource();
					return;
				}	
			}
		}
		
		
		
		
		
		
		
		
		/* -------- Lettura Porta Applicativa 
		 * (Il vero controllo sull'esistenza della Porta Applicativa viene effettuato in Sbustamento, poiche' dipende dal profilo) ------------- */
		// per profili asincroni
		IDPortaDelegata idPD = null;
		IDPortaApplicativa idPA = null;
		String servizioApplicativoErogatoreAsincronoSimmetricoRisposta = null;
		boolean asincronoSimmetricoRisposta = false;
		if(!this.functionAsRouter && this.idServizio!=null){
			this.msgDiag.mediumDebug("Lettura porta applicativa/delegata...");
			try{

				/* ----------- Identificazione profilo -------------- */
				if(     (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) ||
						org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) &&
						!(RuoloBusta.RICHIESTA.equals(this.ruoloBustaRicevuta.toString())) )
				{
					this.msgDiag.highDebug("Lettura porta applicativa/delegata (Asincrono)...");
					// La validazione non deve essere effettuata se abbiamo una ricevuta asincrona, 'modalita' asincrona'
					ProfiloDiCollaborazione profiloCollaborazione = new ProfiloDiCollaborazione(this.openspcoopstate.getStatoRichiesta(),this.protocolFactory);

					if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) {	

						asincronoSimmetricoRisposta = true;
						
						//	Risposta Asincrona
						RepositoryBuste repository = new RepositoryBuste(this.openspcoopstate.getStatoRichiesta(), true, this.protocolFactory);
						Integrazione integrazione = null;
						if(this.bustaRichiesta.getRiferimentoMessaggio()!=null){
							integrazione = repository.getInfoIntegrazioneFromOutBox(this.bustaRichiesta.getRiferimentoMessaggio());
						}else{
							// LineeGuida (Collaborazione)
							integrazione = repository.getInfoIntegrazioneFromOutBox(this.bustaRichiesta.getCollaborazione());
						}
						servizioApplicativoErogatoreAsincronoSimmetricoRisposta = integrazione.getServizioApplicativo();
						idPD = new IDPortaDelegata();
						idPD.setNome(integrazione.getNomePorta());
						this.pd = this.configurazionePdDReader.getPortaDelegataSafeMethod(idPD, requestInfo);
						idPD = this.configurazionePdDReader.convertToIDPortaDelegata(this.pd); // per aggiungere informazioni sugli identificativi
						
					}
					// Profilo Asincrono Asimmetrico
					else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) {	

						///	Risposta Asincrona
						if(RuoloBusta.RISPOSTA.equals(this.ruoloBustaRicevuta.toString())){
							// ConversioneServizio.
							IDServizio idServizioOriginale = null;
							if(this.bustaRichiesta.getRiferimentoMessaggio()!=null){
								idServizioOriginale = profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRisposta(this.bustaRichiesta.getRiferimentoMessaggio());
							}else{
								// LineeGuida (Collaborazione)
								idServizioOriginale = profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRisposta(this.bustaRichiesta.getCollaborazione());
							}
							this.overwriteIdSoggetto(idServizioOriginale, this.idServizio.getSoggettoErogatore());
								
							if(this.pa==null){
								this.pa = ricezioneBusteUtils.getPortaApplicativa(this.configurazionePdDReader, idServizioOriginale);
							}
							
						}
						// Ricevuta alla richiesta/risposta.
						else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(this.ruoloBustaRicevuta.toString()) || 
								RuoloBusta.RICEVUTA_RISPOSTA.equals(this.ruoloBustaRicevuta.toString()) ){	
							RepositoryBuste repository = new RepositoryBuste(this.openspcoopstate.getStatoRichiesta(), true, this.protocolFactory);
							Integrazione integrazione = null;
							if(this.bustaRichiesta.getRiferimentoMessaggio()!=null){
								integrazione = repository.getInfoIntegrazioneFromOutBox(this.bustaRichiesta.getRiferimentoMessaggio());
							}else{
								// LineeGuida (Collaborazione)
								integrazione = repository.getInfoIntegrazioneFromOutBox(this.bustaRichiesta.getCollaborazione());
							}
							idPD = new IDPortaDelegata();
							idPD.setNome(integrazione.getNomePorta());
							this.pd = this.configurazionePdDReader.getPortaDelegataSafeMethod(idPD, requestInfo);
							idPD = this.configurazionePdDReader.convertToIDPortaDelegata(this.pd); // per aggiungere informazioni sugli identificativi
						}
						
					}
					// Chiudo eventuali prepared statement, che non voglio eseguire.
					((StateMessage)this.openspcoopstate.getStatoRichiesta()).closePreparedStatement();
				}else{
					this.msgDiag.highDebug("Lettura porta applicativa/delegata (Normale)...");
					if(this.pa==null){
						this.pa = ricezioneBusteUtils.getPortaApplicativa(this.configurazionePdDReader, this.idServizio);
					}
				}
				
				// Aggiungo identita servizio applicativi
				if(this.pa!=null){
					idPA = this.configurazionePdDReader.convertToIDPortaApplicativa(this.pa);
					this.msgDiag.updatePorta(this.pa.getNome(), requestInfo);
					for(int i=0; i<this.pa.sizeServizioApplicativoList();i++){
						this.msgContext.getIntegrazione().addServizioApplicativoErogatore(this.pa.getServizioApplicativo(i).getNome());
					}
				}
				else if(servizioApplicativoErogatoreAsincronoSimmetricoRisposta!=null){
					// E' l'erogatore della risposta asincrona!
					this.msgContext.getIntegrazione().addServizioApplicativoErogatore(servizioApplicativoErogatoreAsincronoSimmetricoRisposta);
				}
				
				// Aggiungo identificativo porta applicativa/delegata
				if(idPA!=null){
					this.msgContext.getIntegrazione().setIdPA(idPA);
				}
				else if(idPD!=null){
					this.msgContext.getIntegrazione().setIdPD(idPD);
				}
				
				this.msgDiag.highDebug("Lettura porta applicativa/delegata terminato");
				
			}catch(Exception e){
				if(  !(e instanceof DriverConfigurazioneNotFound) ) {
					this.msgDiag.logErroreGenerico(e,"letturaPorta");
					
					setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento("LetturaPorta",CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e);
					return;

				}
			}
		}

		this.msgDiag.highDebug("Lettura porta applicativa/delegata terminato impostazione context");

		
		
		
		
		
		
		
		
		
		/*
		 * ---------------- Aggiornamento dati raccolti (PreAutenticazione) ---------------------
		 */
		
		//		 Aggiornamento Informazioni protocollo
		this.msgDiag.setIdMessaggioRichiesta(validatore.getBusta().getID());
		this.msgContext.setIdMessage(validatore.getBusta().getID());
		this.msgDiag.setServizio(this.idServizio);
		this.msgDiag.addKeywords(validatore.getBusta(), true);
		this.parametriGenerazioneBustaErrore.setMsgDiag(this.msgDiag);
		this.parametriInvioBustaErrore.setMsgDiag(this.msgDiag);

		
		this.tracciamento = new Tracciamento(this.identitaPdD,
				this.msgContext.getIdModulo(),
				this.inRequestContext.getPddContext(),
				this.msgContext.getTipoPorta(),this.msgDiag.getPorta(),
				this.configurazionePdDReader);
		this.parametriGenerazioneBustaErrore.setTracciamento(this.tracciamento);
		
		
		
		
		
		
		
		/* --------------- Gestione credenziali --------------- */
		if(RicezioneBuste.tipiGestoriCredenziali!=null){
			this.msgDiag.mediumDebug("Gestione personalizzata delle credenziali...");
			
			for (int i = 0; i < RicezioneBuste.tipiGestoriCredenziali.length; i++) {
				try {
					
					IGestoreCredenziali gestore = null;
					String classType = null;
					try {
						classType = className.getGestoreCredenziali(RicezioneBuste.tipiGestoriCredenziali[i]);
						gestore = (IGestoreCredenziali)loader.newInstance(classType);
						AbstractCore.init(gestore, this.pddContext, this.protocolFactory);
					} catch (Exception e) {
						throw new CoreException(
								"Riscontrato errore durante il caricamento della classe (IGestoreCredenziali) ["+ classType
								+ "] da utilizzare per la gestione delle credenziali di tipo ["
								+ RicezioneBuste.tipiGestoriCredenziali[i]+ "]: " + e.getMessage());
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
								identita = "Gestore delle credenziali di tipo "+RicezioneBuste.tipiGestoriCredenziali[i];
							}
							this.msgDiag.addKeyword(CostantiPdD.KEY_IDENTITA_GESTORE_CREDENZIALI, identita);
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.IDENTITA_GESTORE_CREDENZIALI, identita);
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
					logError(this.logCore, "Errore durante l'identificazione delle credenziali ["+ RicezioneBuste.tipiGestoriCredenziali[i]
					         + "]: "+ e.getMessage(),e);
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_GESTORE_CREDENZIALI,RicezioneBuste.tipiGestoriCredenziali[i]);
					this.msgDiag.addKeywordErroreProcessamento(e);
					this.msgDiag.logPersonalizzato("gestoreCredenziali.errore");
					ErroreIntegrazione msgErroreIntegrazione = null;
					String wwwAuthenticateErrorHeader = null;
					if(e instanceof GestoreCredenzialiConfigurationException ge){
						this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(ge.getIntegrationFunctionError());
						msgErroreIntegrazione = 
								ErroriIntegrazione.ERRORE_431_GESTORE_CREDENZIALI_ERROR.
									getErrore431_ErroreGestoreCredenziali(RicezioneBuste.tipiGestoriCredenziali[i],e);
						this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE, "true");
						wwwAuthenticateErrorHeader = ge.getWwwAuthenticateErrorHeader();
					}else{
						msgErroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_548_GESTORE_CREDENZIALI_NON_FUNZIONANTE);
					}
					
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(this.msgDiag.getMessaggio_replaceKeywords("gestoreCredenziali.errore"));
						this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
								this.correlazioneApplicativa);
					}
					if(this.msgContext.isGestioneRisposta()){
						
						this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
						this.parametriGenerazioneBustaErrore.setErroreIntegrazione(msgErroreIntegrazione);
						OpenSPCoop2Message errorMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
						if(wwwAuthenticateErrorHeader!=null) {
							errorMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
						}
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
						this.parametriInvioBustaErrore.setOpenspcoopMsg(errorMsg);
						this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
						this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
					}
					this.openspcoopstate.releaseResource();
					return;
					
				}
			}
		}
		
		
		
		
		
		
		
		
		
		
		/* ------------ GestioneToken ------------- */
		
		RicezioneBusteGestioneToken gestioneToken = new RicezioneBusteGestioneToken(this.msgDiag, this.logCore,
				this.tracciamento, this.correlazioneApplicativa, soapHeaderElement, this.bustaRichiesta,
				this.pa, idPA, this.pd, idPD,
				this.requestMessage,
				asincronoSimmetricoRisposta,
				this.msgContext, this.generatoreErrore, this.inRequestContext,
				this.configurazionePdDReader,
				this.pddContext, this.idTransazione,
				this.openspcoopstate, this.transaction, requestInfo,
				this.protocolFactory,
				this.identitaPdD,
				this.parametriGenerazioneBustaErrore, this.parametriInvioBustaErrore);
		
		if(!gestioneToken.process()) {
			return;
		}
		
		
		
		
		
		
		
		
		
		
		
		/* ------------ Riconciliazione ID Messaggio con quello ricevuto nel token oAuth ------------- */
		
		if(Costanti.MODIPA_PROTOCOL_NAME.equals(this.protocolFactory.getProtocol())) {
			ModIValidazioneSemanticaProfiloSicurezza modIValidazioneSemanticaProfiloSicurezza = new ModIValidazioneSemanticaProfiloSicurezza(this.bustaRichiesta, true);
			if(modIValidazioneSemanticaProfiloSicurezza.isSicurezzaTokenOauth()) {
				boolean useJtiAuthorization = ModIUtils.useJtiAuthorizationObject(this.requestMessage);
				if(useJtiAuthorization) {
					String jti = TokenUtilities.readJtiFromInformazioniToken(this.pddContext);
					if(jti!=null && StringUtils.isNotEmpty(jti)) {
						ModIUtils.replaceBustaIdWithJtiTokenId(modIValidazioneSemanticaProfiloSicurezza, jti);
						this.msgDiag.updateKeywordIdMessaggioRichiesta(this.bustaRichiesta.getID());
					}
				}
			}
		}
		
		
		
		
		
		
		this.midProcess(validatore, soapHeaderElement, infoServizio, properties, servizioApplicativoFruitore, 
				inRequestPAMessage, gestioneToken, idPA, idPD, inoltroMSG, asincronoSimmetricoRisposta, soggettoVirtuale, credenziali);
		
		} finally{ // try vedi  #try-finally-openspcoopstate#
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


	private void midProcess(
			Validatore validatore, 
			BustaRawContent<?> soapHeaderElement,
			Servizio infoServizio, 
			ProprietaValidazione properties, 
			String servizioApplicativoFruitore, 
			InRequestPAMessage inRequestPAMessage,
			RicezioneBusteGestioneToken gestioneToken,
			IDPortaApplicativa idPA,
			IDPortaDelegata idPD,
			InoltroBusteMessage inoltroMSG,
			boolean asincronoSimmetricoRisposta,
			boolean soggettoVirtuale,
			Credenziali credenziali) throws TracciamentoException, ProtocolException {

		RicezioneBusteUtils ricezioneBusteUtils = new RicezioneBusteUtils();
		
		// RequestInfo
		RequestInfo requestInfo = this.msgContext.getRequestInfo();
		
		// OpenSPCoop Properties
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
		
		// Parametri della porta applicativa invocata
		URLProtocolContext urlProtocolContext = this.msgContext.getUrlProtocolContext();
				
		PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
		GestioneTokenAutenticazione gestioneTokenAutenticazione = gestioneToken.getGestioneTokenAutenticazione();
		
		/*
		 * ---------------- Mittente / Autenticazione ---------------------
		 */
		
		RicezioneBusteGestioneAutenticazione gestioneAutenticazione = new RicezioneBusteGestioneAutenticazione(this.msgDiag, this.logCore,
				this.tracciamento, this.correlazioneApplicativa, soapHeaderElement, this.bustaRichiesta,
				this.pa, idPA, this.pd, idPD,
				this.soggettoFruitore, credenziali, servizioApplicativoFruitore, gestioneTokenAutenticazione,
				this.requestMessage,
				asincronoSimmetricoRisposta, this.functionAsRouter,
				this.msgContext, this.generatoreErrore, this.inRequestContext,
				this.configurazionePdDReader, this.registroServiziReader,
				this.pddContext, this.idTransazione, this.identitaPdD,
				this.openspcoopstate, this.transaction, requestInfo,
				this.protocolFactory, validatore,
				this.parametriGenerazioneBustaErrore, this.parametriInvioBustaErrore);
		
		boolean soggettoFruitoreIdentificatoTramiteProtocollo = false;
		boolean soggettoAutenticato = false;
		
		if(!gestioneAutenticazione.process()) {
			return;
		}
		
		soggettoFruitoreIdentificatoTramiteProtocollo = gestioneAutenticazione.isSoggettoFruitoreIdentificatoTramiteProtocollo();
		soggettoAutenticato = gestioneAutenticazione.isSoggettoAutenticato();
		this.soggettoFruitore = gestioneAutenticazione.getSoggettoFruitore();
		servizioApplicativoFruitore = gestioneAutenticazione.getServizioApplicativoFruitore();
		
		
		
		
		
		
	
		
		
		/*
		 * ---------------- Aggiornamento dati raccolti (PostAutenticazione) ---------------------
		 */
			
		Trasmissione trasmissioneSoggettoAutenticato = null; 
		if(soggettoAutenticato || soggettoFruitoreIdentificatoTramiteProtocollo){
			validatore.getBusta().setTipoMittente(this.soggettoFruitore.getTipo());
			validatore.getBusta().setMittente(this.soggettoFruitore.getNome());
			if(validatore.getBusta().sizeListaTrasmissioni()>0){
				for (int i = 0; i < validatore.getBusta().sizeListaTrasmissioni(); i++) {
					Trasmissione trasmissione = validatore.getBusta().getTrasmissione(i);
					if(trasmissione.getOrigine()==null && trasmissione.getTipoOrigine()==null && trasmissione.getIdentificativoPortaOrigine()==null){
						trasmissioneSoggettoAutenticato = trasmissione;
						trasmissione.setTipoOrigine(this.soggettoFruitore.getTipo());
						trasmissione.setOrigine(this.soggettoFruitore.getNome());
					}
				}
			}
			validatore.setMittente(this.soggettoFruitore);
			this.bustaRichiesta = validatore.getBusta();
			this.msgDiag.addKeywords(validatore.getBusta(), true);
		}
				
		// VM ProtocolInfo (se siamo arrivati da un canale VM)
		if(this.pddContext!=null && this.bustaRichiesta!=null)
			DirectVMProtocolInfo.setInfoFromContext(this.pddContext, this.bustaRichiesta);
			
		// Se non impostati, imposto i domini
		org.openspcoop2.pdd.core.Utilities.refreshIdentificativiPorta(this.bustaRichiesta, requestInfo.getIdentitaPdD(), this.registroServiziReader, this.protocolFactory, requestInfo);
		if(this.soggettoFruitore != null){
			if(this.soggettoFruitore.getCodicePorta()==null){
				this.soggettoFruitore.setCodicePorta(this.bustaRichiesta.getIdentificativoPortaMittente());
			}
			if(trasmissioneSoggettoAutenticato!=null && trasmissioneSoggettoAutenticato.getIdentificativoPortaOrigine()==null){
				trasmissioneSoggettoAutenticato.setIdentificativoPortaOrigine(this.bustaRichiesta.getIdentificativoPortaMittente());
			}
			this.msgDiag.setFruitore(this.soggettoFruitore);
		}
		if(this.idServizio!=null && this.idServizio.getSoggettoErogatore()!=null &&
			this.idServizio.getSoggettoErogatore().getCodicePorta()==null){
			this.idServizio.getSoggettoErogatore().setCodicePorta(this.bustaRichiesta.getIdentificativoPortaDestinatario());
		}
		
		if(servizioApplicativoFruitore!=null){
			// overriding busta
			if(this.bustaRichiesta!=null) {
				this.bustaRichiesta.setServizioApplicativoFruitore(servizioApplicativoFruitore);
			}
		}
		else{
			// altrimenti se è valorizzato internamente alla busta (poichè previsto dal protocollo (es. pdc)) e non capito tramite informazioni di integrazione uso quello
			if(this.bustaRichiesta!=null && this.bustaRichiesta.getServizioApplicativoFruitore()!=null){
				servizioApplicativoFruitore = this.bustaRichiesta.getServizioApplicativoFruitore();
			}
		}
		this.idMessageRequest = this.bustaRichiesta!=null ? this.bustaRichiesta.getID() : null;

		this.tipoPorta = TipoPdD.APPLICATIVA;
		if(this.functionAsRouter)
			this.tipoPorta = TipoPdD.ROUTER;
		this.msgContext.getProtocol().setDominio(this.identitaPdD);
		this.msgContext.setIdentitaPdD(this.identitaPdD);
		this.msgContext.setTipoPorta(this.tipoPorta);
		this.msgContext.getProtocol().setFruitore(this.soggettoFruitore);
		if(this.bustaRichiesta!=null){
			this.msgContext.getProtocol().setIndirizzoFruitore(this.bustaRichiesta.getIndirizzoMittente());
		}
		if(this.idServizio!=null && this.idServizio.getSoggettoErogatore()!=null){
			this.msgContext.getProtocol().setErogatore(this.idServizio.getSoggettoErogatore());
		}
		if(this.bustaRichiesta!=null){
			this.msgContext.getProtocol().setIndirizzoErogatore(this.bustaRichiesta.getIndirizzoDestinatario());
		}
		if(this.idServizio!=null) {
			this.msgContext.getProtocol().setTipoServizio(this.idServizio.getTipo());
			this.msgContext.getProtocol().setServizio(this.idServizio.getNome());
			this.msgContext.getProtocol().setVersioneServizio(this.idServizio.getVersione());
			this.msgContext.getProtocol().setAzione(this.idServizio.getAzione()); 
		}
		this.msgContext.getProtocol().setIdRichiesta(this.idMessageRequest);
		if(this.bustaRichiesta!=null) {
			this.msgContext.getProtocol().setProfiloCollaborazione(this.bustaRichiesta.getProfiloDiCollaborazione(),this.bustaRichiesta.getProfiloDiCollaborazioneValue());
			this.msgContext.getProtocol().setCollaborazione(this.bustaRichiesta.getCollaborazione());
		}




		
		
		
		
		
		

		
		/*
		 * ---------------- Verifico che il servizio di RicezioneBuste sia abilitato ---------------------
		 */
		boolean serviceIsEnabled = false;
		boolean portaEnabled = false;
		Exception serviceIsEnabledExceptionProcessamento = null;
		try{
			serviceIsEnabled = StatoServiziPdD.isEnabledPortaApplicativa(this.soggettoFruitore, this.idServizio);
			if(serviceIsEnabled){
				// verifico la singola porta
				if(this.pa!=null){
					portaEnabled = this.configurazionePdDReader.isPortaAbilitata(this.pa);
				}
				else{
					portaEnabled = this.configurazionePdDReader.isPortaAbilitata(this.pd);
				}
			}
		}catch(Exception e){
			serviceIsEnabledExceptionProcessamento = e;
		}
		if (!serviceIsEnabled || !portaEnabled || serviceIsEnabledExceptionProcessamento!=null) {
			ErroreIntegrazione errore = null;
			String esito = null;
			IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.API_SUSPEND;
			if(serviceIsEnabledExceptionProcessamento!=null){
				logError(this.logCore, "["+ RicezioneBuste.ID_MODULO+ "] Identificazione stato servizio di ricezione buste non riuscita: "+serviceIsEnabledExceptionProcessamento.getMessage(),serviceIsEnabledExceptionProcessamento);
				this.msgDiag.logErroreGenerico("Identificazione stato servizio di ricezione buste non riuscita", "PA");
				esito = "["+ RicezioneBuste.ID_MODULO+ "] Identificazione stato servizio di ricezione buste non riuscita";
				errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione();
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			}else{
				
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_SOSPENSIONE, "true");
				
				String msg = "Servizio di ricezione buste disabilitato";
				if(serviceIsEnabled){
					if(this.pa!=null){
						msg = "Porta Applicativa ["+this.pa.getNome()+"] disabilitata";
					}
					else{
						msg = "Porta Delegata ["+this.pd.getNome()+"] disabilitata";
					}
					errore = ErroriIntegrazione.ERRORE_446_PORTA_SOSPESA.getErroreIntegrazione();
				}
				else {
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_551_PA_SERVICE_NOT_ACTIVE);
				}
				logError(this.logCore, "["+ RicezioneBuste.ID_MODULO+ "] "+msg);
				this.msgDiag.logErroreGenerico(msg, "PA");
				esito = "["+ RicezioneBuste.ID_MODULO+ "] "+msg;
			}
			// Tracciamento richiesta: non ancora registrata
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(esito);
				this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
						Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
						this.correlazioneApplicativa);
			}
			if(this.msgContext.isGestioneRisposta()){

				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);							
				this.parametriGenerazioneBustaErrore.setErroreIntegrazione(errore);
				this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);

				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,serviceIsEnabledExceptionProcessamento);
				
				if(IntegrationFunctionError.API_SUSPEND.equals(integrationFunctionError) &&
						propertiesReader.isEnabledServiceUnavailableRetryAfterPASuspend() && 
						propertiesReader.getServiceUnavailableRetryAfterSecondsPASuspend()!=null &&
						propertiesReader.getServiceUnavailableRetryAfterSecondsPASuspend()>0) {
					int seconds = propertiesReader.getServiceUnavailableRetryAfterSecondsPASuspend();
					if(propertiesReader.getServiceUnavailableRetryAfterSecondsRandomBackoffPASuspend()!=null &&
							propertiesReader.getServiceUnavailableRetryAfterSecondsRandomBackoffPASuspend()>0) {
						seconds = seconds + ServicesUtils.getRandom().nextInt(propertiesReader.getServiceUnavailableRetryAfterSecondsRandomBackoffPASuspend());
					}
					errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.RETRY_AFTER, seconds+"");
				}
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

			}
			this.openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		
		
		
		
		
		/* ------------ Gestione Attribute Authority ------------- */
		
		this.msgDiag.mediumDebug("Gestione Attribute Authority...");
		List<AttributeAuthority> attributeAuthorities = null;
		if(this.pa!=null) {
			attributeAuthorities = this.pa.getAttributeAuthorityList();
		}
		else if(this.pd!=null) {
			attributeAuthorities = this.pd.getAttributeAuthorityList();
		}
		this.msgContext.getIntegrazione().setAttributeAuthoritiesFromObjectList(attributeAuthorities);
		
		if (attributeAuthorities == null || attributeAuthorities.isEmpty()) {

			if(!asincronoSimmetricoRisposta) {
				this.msgDiag.logPersonalizzato("gestioneAADisabilitata");
			}
			
		} else {

			this.transaction.getTempiElaborazione().startAttributeAuthority();
			
			try {
				this.msgDiag.logPersonalizzato("gestioneAAInCorso");
				
				org.openspcoop2.pdd.core.token.attribute_authority.pa.DatiInvocazionePortaApplicativa datiInvocazione = new org.openspcoop2.pdd.core.token.attribute_authority.pa.DatiInvocazionePortaApplicativa();
				datiInvocazione.setInfoConnettoreIngresso(this.inRequestContext.getConnettore());
				datiInvocazione.setState(this.openspcoopstate.getStatoRichiesta());
				datiInvocazione.setIdModulo(this.inRequestContext.getIdModulo());
				datiInvocazione.setMessage(this.requestMessage);
				datiInvocazione.setBusta(this.bustaRichiesta);
				datiInvocazione.setRequestInfo(requestInfo);
				datiInvocazione.setIdPA(idPA);
				datiInvocazione.setPa(this.pa);	
				datiInvocazione.setIdPD(idPD);
				datiInvocazione.setPd(this.pd);		
				
				GestioneAttributeAuthority gestioneAAEngine = new GestioneAttributeAuthority(this.logCore, this.idTransazione, this.pddContext, this.protocolFactory);
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
					this.pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_ATTRIBUTI_INFORMAZIONI_NORMALIZZATE, informazioniAttributiNormalizzati);
					
					InformazioniToken informazioniTokenNormalizzate = null;
					if(this.pddContext.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE)) {
						informazioniTokenNormalizzate = (InformazioniToken) this.pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
					}
					
					if(informazioniTokenNormalizzate!=null) {
						informazioniTokenNormalizzate.setAa(informazioniAttributiNormalizzati);
					}
					else {
						this.transaction.setInformazioniAttributi(informazioniAttributiNormalizzati);
					}
				}
				
				this.msgDiag.logPersonalizzato("gestioneAACompletata");

			} catch (Throwable e) {
				
				this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
				this.msgDiag.logPersonalizzato("gestioneAAFallita");
				logError(this.logCore, "processo di gestione delle attribute authorities fallito: " + e.getMessage(),e);
				
			}
			finally {
				this.transaction.getTempiElaborazione().endAttributeAuthority();
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		/* -------- OpenSPCoop2Message Update ------------- */
		try {
			this.msgDiag.mediumDebug("Aggiornamento del messaggio");
			this.requestMessage = this.protocolFactory.createProtocolManager().updateOpenSPCoop2MessageRequest(this.requestMessage, this.bustaRichiesta,
					this.protocolFactory.getCachedRegistryReader(this.registroServiziReader, requestInfo));
		} catch (Exception e) {
			// Emetto log, non ancora emesso
			boolean mittenteEsistente = validatore.getBusta()!=null && 
					(validatore.getBusta().getMittente()!=null || validatore.getBusta().getTipoMittente()!=null); 
			this.msgDiag.emitRicezioneMessaggioModuloBuste(this.msgDiag, this.pddContext, !mittenteEsistente);
			
			this.msgDiag.addKeywordErroreProcessamento(e,"Aggiornamento messaggio fallito");
			this.msgDiag.logErroreGenerico(e,"ProtocolManager.updateOpenSPCoop2Message");
			logError(this.logCore, "ProtocolManager.updateOpenSPCoop2Message error: "+e.getMessage(),e);
			
			// Tracciamento richiesta: non ancora registrata
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("ProtocolManager.updateOpenSPCoop2Message, non riuscito: "+e.getMessage());
				this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
						Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
						this.correlazioneApplicativa);
			}
			if(this.msgContext.isGestioneRisposta()){

				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.
					setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO));
				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

			}
			this.openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		
		

		

		// Modalita' gestione risposta (Sincrona/Fault/Ricevute...)
		// Per i profili diversi dal sincrono e' possibile impostare dove far ritornare l'errore
		this.newConnectionForResponse = false; 
		this.utilizzoIndirizzoTelematico = false;		
		if(!this.functionAsRouter){

			// Calcolo newConnectionForResponse in caso di asincroni
			if( org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) ||
					org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())	){
				this.newConnectionForResponse = this.configurazionePdDReader.newConnectionForResponse();
				this.utilizzoIndirizzoTelematico = this.configurazionePdDReader.isUtilizzoIndirizzoTelematico();
				this.parametriInvioBustaErrore.setNewConnectionForResponse(this.newConnectionForResponse);
				this.parametriInvioBustaErrore.setUtilizzoIndirizzoTelematico(this.utilizzoIndirizzoTelematico);
			}


			/* ------------
			   Controllo effettuato in caso di ricezione di una risposta Asincrona Simmetrica, o richiesta-stato Asincrona Asimmetrica
			   In questo caso, se e' ancora in corso la gestione della richiesta o della ricevuta alla richiesta, 
			   prima di procedere con la gestione della risposta/richiesta-stato, devono termiare le precedenti richieste/ricevute.
			   ------------
			 */
			if(!validatore.isErroreProtocollo() && !validatore.isBustaDiServizio()){
				try{
					long scadenzaControllo = DateManager.getTimeMillis() + propertiesReader.getTimeoutBustaRispostaAsincrona();
					int checkIntervalControllo = propertiesReader.getCheckIntervalBustaRispostaAsincrona();
					boolean attendiTerminazioneRichiesta = 
						this.gestioneRispostaAsincronaCheckPresenzaRichiesta(scadenzaControllo,checkIntervalControllo, this.bustaRichiesta, 
								this.openspcoopstate, this.msgDiag, this.newConnectionForResponse, this.inRequestContext.getPddContext());
					boolean attendiTerminazioneRicevutaRichiesta = 
						this.gestioneRispostaAsincronaCheckPresenzaRicevutaRichiesta(scadenzaControllo,checkIntervalControllo, this.bustaRichiesta, 
								this.openspcoopstate, this.msgDiag, this.newConnectionForResponse, this.inRequestContext.getPddContext());

					ErroreIntegrazione msgErroreIntegrazione = null;
					String motivoErrore = null;
					if(attendiTerminazioneRichiesta){
						this.msgDiag.logPersonalizzato("attesaFineProcessamento.richiestaAsincrona.timeoutScaduto");
						msgErroreIntegrazione = ErroriIntegrazione.ERRORE_538_RICHIESTA_ASINCRONA_ANCORA_IN_PROCESSAMENTO.getErroreIntegrazione();
					}else if(attendiTerminazioneRicevutaRichiesta){
						this.msgDiag.logPersonalizzato("attesaFineProcessamento.ricevutaRichiestaAsincrona.timeoutScaduto");
						msgErroreIntegrazione = ErroriIntegrazione.ERRORE_539_RICEVUTA_RICHIESTA_ASINCRONA_ANCORA_IN_PROCESSAMENTO.getErroreIntegrazione();
					}
					if(attendiTerminazioneRichiesta || attendiTerminazioneRicevutaRichiesta){
						// Tracciamento richiesta: non ancora registrata
						if(this.msgContext.isTracciamentoAbilitato()){
							EsitoElaborazioneMessaggioTracciato esitoTraccia = 
									EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(motivoErrore);
							this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
									Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
									this.correlazioneApplicativa);
						}
						if(this.msgContext.isGestioneRisposta()){

							this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
							this.parametriGenerazioneBustaErrore.setErroreIntegrazione(msgErroreIntegrazione);

							OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,null);
							
							// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
							this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
							this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
							this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

						}
						this.openspcoopstate.releaseResource();
						return;
					}

				}catch(Exception e){

					// Emetto log, non ancora emesso
					boolean mittenteEsistente = validatore.getBusta()!=null && 
							(validatore.getBusta().getMittente()!=null || validatore.getBusta().getTipoMittente()!=null); 
					this.msgDiag.emitRicezioneMessaggioModuloBuste(this.msgDiag, this.pddContext, !mittenteEsistente);
					
					this.msgDiag.logErroreGenerico(e,"checkPresenzaRichiestaRicevutaAsincronaAncoraInGestione");
					logError(this.logCore, "Controllo presenza richieste/ricevuteRichieste ancora in gestione " +
							"correlate alla risposta/richiesta-stato asincrona simmetrica/asimmetrica arrivata, non riuscito",e);
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Controllo presenza richieste/ricevuteRichieste ancora in gestione " +
										"correlate alla risposta/richiesta-stato asincrona simmetrica/asimmetrica arrivata, non riuscito: "+e.getMessage());
						this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
								this.correlazioneApplicativa);
					}
					if(this.msgContext.isGestioneRisposta()){

						this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
						this.parametriGenerazioneBustaErrore.
							setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
						OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

						this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
						this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

					}
					this.openspcoopstate.releaseResource();
					return;
				}
			}
			
			/* ----------- Scenario Cooperazione ------------ */
			if(this.ruoloBustaRicevuta!=null){
				try{
					String scenarioCooperazione = null;
					if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) {	
						scenarioCooperazione = Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO;
					}
					else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) {	
						scenarioCooperazione = Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO;
					}
					else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) {	
						if(RuoloBusta.RICHIESTA.equals(this.ruoloBustaRicevuta.toString())){
							scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO;
						}
						else if(RuoloBusta.RISPOSTA.equals(this.ruoloBustaRicevuta.toString())){
							scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA;
						}
						else{
							// sono ricevute asincrone
						}
					}
					else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) {	
						if(RuoloBusta.RICHIESTA.equals(this.ruoloBustaRicevuta.toString())){
							scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO;
						}
						else if(RuoloBusta.RISPOSTA.equals(this.ruoloBustaRicevuta.toString())){
							scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING;
						}
						else{
							// sono ricevute asincrone
						}
					}
					this.msgContext.getProtocol().setScenarioCooperazione(scenarioCooperazione);
				}catch(Exception e){
					this.msgDiag.logErroreGenerico(e,"setScenarioCooperazione");
				}
			}

		}



		
		
		
		
		
		
		
		
		/* -------------------------- Implementazione PdD Soggetti busta -------------------------------*/
		this.implementazionePdDMittente = null;
		String implementazionePdDDestinatario = null;
		String idPdDMittente = null;
		String idPdDDestinatario = null;
		this.msgDiag.mediumDebug("Ricerca implementazione della porta di dominio dei soggetti...");
		boolean validazioneIDBustaCompleta = true;
		try{
			if(this.soggettoFruitore!=null){
				this.implementazionePdDMittente = this.registroServiziReader.getImplementazionePdD(this.soggettoFruitore, null, requestInfo);
			}
			if(this.idServizio!=null && this.idServizio.getSoggettoErogatore()!=null){
				implementazionePdDDestinatario = this.registroServiziReader.getImplementazionePdD(this.idServizio.getSoggettoErogatore(), null, requestInfo);
			}
			if(this.soggettoFruitore!=null){
				idPdDMittente = this.registroServiziReader.getIdPortaDominio(this.soggettoFruitore, null, requestInfo);
			}
			if(this.idServizio!=null && this.idServizio.getSoggettoErogatore()!=null){
				idPdDDestinatario = this.registroServiziReader.getIdPortaDominio(this.idServizio.getSoggettoErogatore(), null, requestInfo);
			}
			this.parametriGenerazioneBustaErrore.setImplementazionePdDMittente(this.implementazionePdDMittente);
			this.parametriGenerazioneBustaErrore.setImplementazionePdDDestinatario(implementazionePdDDestinatario);
			this.parametriInvioBustaErrore.setImplementazionePdDMittente(this.implementazionePdDMittente);
			this.parametriInvioBustaErrore.setImplementazionePdDDestinatario(implementazionePdDDestinatario);
			
			properties.setValidazioneConSchema(this.configurazionePdDReader.isLivelloValidazioneRigido(this.implementazionePdDMittente));
			properties.setValidazioneProfiloCollaborazione(this.configurazionePdDReader.isValidazioneProfiloCollaborazione(this.implementazionePdDMittente));
			if(this.openspcoopstate!=null) {
				properties.setRuntimeState(this.openspcoopstate.getStatoRichiesta());
				if(propertiesReader.isTransazioniUsePddRuntimeDatasource()) {
					properties.setTracceState(this.openspcoopstate.getStatoRichiesta());
				}
			}
			validatore.setProprietaValidazione(properties); // update
			
			MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
			contextParameters.setUseActorDefaultIfNotDefined(propertiesReader.isGenerazioneActorDefault(this.implementazionePdDMittente));
			contextParameters.setActorDefault(propertiesReader.getActorDefault(this.implementazionePdDMittente));
			contextParameters.setLog(this.logCore);
			contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_SERVER);
			contextParameters.setPrefixWsuId(propertiesReader.getPrefixWsuId());
			contextParameters.setRemoveAllWsuIdRef(propertiesReader.isRemoveAllWsuIdRef());
			contextParameters.setIdFruitore(this.soggettoFruitore);
			contextParameters.setPddFruitore(idPdDMittente);
			contextParameters.setIdServizio(this.idServizio);
			contextParameters.setPddErogatore(idPdDDestinatario);
			this.messageSecurityContext = new MessageSecurityFactory().getMessageSecurityContext(contextParameters);
			this.parametriGenerazioneBustaErrore.setMessageSecurityContext(this.messageSecurityContext);
			
			this.proprietaManifestAttachments = propertiesReader.getProprietaManifestAttachments(this.implementazionePdDMittente);
			this.readQualifiedAttribute = propertiesReader.isReadQualifiedAttribute(this.implementazionePdDMittente);
			validazioneIDBustaCompleta = propertiesReader.isValidazioneIDBustaCompleta(this.implementazionePdDMittente);
			
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e,"ricercaImplementazionePdDSoggettiBusta");
			// Tracciamento richiesta: non ancora registrata
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Errore durante la ricerca dell'implementazione della porta di dominio dei soggetti: "+e.getMessage());
				this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
						Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
						this.correlazioneApplicativa);
			}
			if(this.msgContext.isGestioneRisposta()){
				
				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.
				setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
			}
			this.openspcoopstate.releaseResource();
			return;
		}
		if(this.soggettoFruitore!=null){
			this.msgDiag.mediumDebug("ImplementazionePdD soggetto fruitore ("+this.soggettoFruitore.toString()+"): ["+this.implementazionePdDMittente+"]");
		}
		if(this.idServizio!=null && this.idServizio.getSoggettoErogatore()!=null){
			this.msgDiag.mediumDebug("ImplementazionePdD soggetto erogatore ("+this.idServizio.getSoggettoErogatore().toString()+"): ["+implementazionePdDDestinatario+"]");
		}
		
		
		
		
		
		
		




		
		/* -------- Manifest attachments ------------- */
		// per profili asincroni
		if(!this.functionAsRouter){
			this.msgDiag.mediumDebug("Lettura manifest attachments impostato in porta applicativa/delegata...");
			try{

				// Validazione manifest attachments
				this.msgDiag.highDebug("Lettura porta applicativa/delegata (Set)...");
				if(this.pa!=null){
					properties.setValidazioneManifestAttachments(
							this.configurazionePdDReader.isValidazioneManifestAttachments(this.implementazionePdDMittente) &&
							this.configurazionePdDReader.isGestioneManifestAttachments(this.pa,this.protocolFactory));
				}
				else if(this.pd!=null){
					properties.setValidazioneManifestAttachments(
							this.configurazionePdDReader.isValidazioneManifestAttachments(implementazionePdDDestinatario) &&
							this.configurazionePdDReader.isGestioneManifestAttachments(this.pd,this.protocolFactory));
				}
				else{
					properties.setValidazioneManifestAttachments(
							this.configurazionePdDReader.isValidazioneManifestAttachments(this.implementazionePdDMittente));
				}
				
				this.msgDiag.highDebug("Lettura porta applicativa/delegata terminato");
				
			}catch(Exception e){
				if(  !(e instanceof DriverConfigurazioneNotFound) ) {
					this.msgDiag.logErroreGenerico(e,"letturaPortaManifestAttachments");
					
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Errore durante la lettura della porta applicativa/delegata per la gestione ManifestAttachments: "+e.getMessage());
						this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
								this.correlazioneApplicativa);
					}
					if(this.msgContext.isGestioneRisposta()){
						
						this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
						this.parametriGenerazioneBustaErrore.
						setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
						OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

						this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
						this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

					}
					this.openspcoopstate.releaseResource();
					return;
				}
			}
			this.msgDiag.highDebug("Lettura manifest attachments impostato in porta applicativa/delegata terminato");
		}

		






		
		
		
		// Configurazione Richiesta Applicativa
		String idModuloInAttesa = null;
		if(this.msgContext.isGestioneRisposta())
			idModuloInAttesa = this.msgContext.getIdModulo();
		RichiestaApplicativa richiestaApplicativa = null;
		if(idPA!=null) {
			richiestaApplicativa = new RichiestaApplicativa(this.soggettoFruitore,
					idModuloInAttesa,this.identitaPdD,idPA);
			if(this.idServizio!=null && this.idServizio.getAzione()!=null && 
					idPA!=null && idPA.getIdentificativiErogazione()!=null && idPA.getIdentificativiErogazione().getIdServizio()!=null &&
					idPA.getIdentificativiErogazione().getIdServizio().getAzione()==null) {
				idPA.getIdentificativiErogazione().getIdServizio().setAzione(this.idServizio.getAzione());
			}
		}
		else {
			// Scenario Asincrono Simmetrico - Risposta
			richiestaApplicativa = new RichiestaApplicativa(this.soggettoFruitore,
					idModuloInAttesa,this.identitaPdD,this.idServizio); 
		}
		richiestaApplicativa.setFiltroProprietaPorteApplicative(this.msgContext.getProprietaFiltroPortaApplicativa());
		richiestaApplicativa.setIntegrazione(this.msgContext.getIntegrazione());
		richiestaApplicativa.setProtocol(this.msgContext.getProtocol());
		
		
		


		
		
		
		
		
		// ------------- Controllo funzionalita di protocollo richieste siano compatibili con il protocollo -----------------------------
		try{
			// NOTA: Usare getIntegrationServiceBinding poichè le funzionalità si riferiscono al tipo di integrazione scelta
			
			IProtocolConfiguration protocolConfiguration = this.protocolFactory.createProtocolConfiguration();
			if(this.bustaRichiesta!=null && this.bustaRichiesta.getProfiloDiCollaborazione()!=null && 
					!org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.UNKNOWN.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) &&
				!protocolConfiguration.isSupportato(requestInfo.getIntegrationServiceBinding(),this.bustaRichiesta.getProfiloDiCollaborazione())){
				throw new CoreException("Profilo di Collaborazione ["+this.bustaRichiesta.getProfiloDiCollaborazione().getEngineValue()+"]");
			}
			// NOTA:  FiltroDuplicati, consegnaAffidabile, idCollaborazione, consegnaInOrdine verificato in sbustamento.
			if(this.bustaRichiesta!=null && this.bustaRichiesta.getScadenza()!=null &&
				!protocolConfiguration.isSupportato(requestInfo.getIntegrationServiceBinding(),FunzionalitaProtocollo.SCADENZA)){
				throw new CoreException(FunzionalitaProtocollo.SCADENZA.getEngineValue());
			}
			
			if(this.configurazionePdDReader.isGestioneManifestAttachments(this.pa,this.protocolFactory) &&
				!protocolConfiguration.isSupportato(requestInfo.getIntegrationServiceBinding(),FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
				throw new CoreException(FunzionalitaProtocollo.MANIFEST_ATTACHMENTS.getEngineValue());
			}			
		}catch(Exception e){	
			this.msgDiag.addKeywordErroreProcessamento(e);
			this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"protocolli.funzionalita.unsupported");
			// Tracciamento richiesta: non ancora registrata
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(this.msgDiag.getMessaggio_replaceKeywords("protocolli.funzionalita.unsupported"));
				this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
						Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
						this.correlazioneApplicativa);
			}
			if(this.msgContext.isGestioneRisposta()){
				
				this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL);
				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.
				setErroreIntegrazione(ErroriIntegrazione.ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL.
						getErrore439_FunzionalitaNotSupportedByProtocol(e.getMessage(), this.protocolFactory));
				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

			}
			this.openspcoopstate.releaseResource();
			return;
		}
		
		


	
		




		/* ------------- Modalita' di gestione ---------------------------- */
		//Versione OneWay 
		this.oneWayStateless = false;
		this.oneWayVersione11 = false;
		// Versione Sincrono 
		this.sincronoStateless = false;
		// Asincrono stateless
		this.asincronoStateless = false;

		// Gestione stateless
		this.portaStateless = false; // vero se almeno uno dei 3 precedenti e' vero

		// Routing stateless
		this.routingStateless = false;

		try {
			
			if(!this.functionAsRouter){
				
				if(propertiesReader.isServerJ2EE()==null || !propertiesReader.isServerJ2EE().booleanValue()){
					// Stateless obbligatorio in server di tipo web (non j2ee)
					this.oneWayStateless = true;
					this.sincronoStateless = true;
					this.asincronoStateless = true;
				}
				else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(this.bustaRichiesta.getProfiloDiCollaborazione())){
					if(!soggettoVirtuale) {
						this.oneWayStateless = this.configurazionePdDReader.isModalitaStateless(this.pa,this.bustaRichiesta.getProfiloDiCollaborazione());
					}
				}
				else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())){	
					this.sincronoStateless = this.configurazionePdDReader.isModalitaStateless(this.pa,this.bustaRichiesta.getProfiloDiCollaborazione());
				}else if(
						(
								org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) 
								||
								org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())
						)
						&&
						// Le ricevute arrivano solo con connection-new (possibile solo con stateful)
						(!RuoloBusta.RICEVUTA_RICHIESTA.equals(this.ruoloBustaRicevuta.toString())) &&
						(!RuoloBusta.RICEVUTA_RISPOSTA.equals(this.ruoloBustaRicevuta.toString()))
						) {
					if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) && 
							this.pd!=null){
						// Risposta
						this.asincronoStateless = this.configurazionePdDReader.isModalitaStateless(this.pd, this.bustaRichiesta.getProfiloDiCollaborazione());
					}else{
						this.asincronoStateless = this.configurazionePdDReader.isModalitaStateless(this.pa, this.bustaRichiesta.getProfiloDiCollaborazione());
					}
				}

				this.oneWayVersione11 = propertiesReader.isGestioneOnewayStateful_1_1() &&  
				org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(this.bustaRichiesta.getProfiloDiCollaborazione())&& !this.oneWayStateless ; 

				if ( this.oneWayStateless || this.sincronoStateless || this.asincronoStateless || this.oneWayVersione11 ) {
					this.portaStateless = true;		
					this.msgContext.getIntegrazione().setGestioneStateless(!this.oneWayVersione11);
				}else{
					this.msgContext.getIntegrazione().setGestioneStateless(false);
				}
				
				if(
						(!this.portaStateless || this.oneWayVersione11) &&
						!this.openspcoopstate.isUseConnection() && 
						(this.openspcoopstate instanceof OpenSPCoopStateful || this.oneWayVersione11) &&
						this.openspcoopstate.resourceReleased()
					) {
					// inizializzo
					this.openspcoopstate.setUseConnection(true);
					this.openspcoopstate.initResource(this.identitaPdD, this.msgContext.getIdModulo(), this.idTransazione);
				}
				
			}else{
				this.routingStateless = CostantiConfigurazione.ABILITATO.equals(propertiesReader.getStatelessRouting());
				this.msgContext.getIntegrazione().setGestioneStateless(this.routingStateless);
				
				if(
						!this.routingStateless &&
						!this.openspcoopstate.isUseConnection() && 
						(this.openspcoopstate instanceof OpenSPCoopStateful) &&
						this.openspcoopstate.resourceReleased()
					) {
					// inizializzo
					this.openspcoopstate.setUseConnection(true);
					this.openspcoopstate.initResource(this.identitaPdD, this.msgContext.getIdModulo(), this.idTransazione);
				}
			}
			
			this.parametriInvioBustaErrore.setOnewayVersione11(this.oneWayVersione11);
			
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"AnalisiModalitaGestioneStatefulStateless");
			logError(this.logCore, "Analisi modalita di gestione STATEFUL/STATELESS non riuscita: "	+ e);
			if (this.msgContext.isGestioneRisposta()) {
				
				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

			}
			this.openspcoopstate.releaseResource();
			return;
		}


		// UPDATE: Modalita' gestione risposta (Sincrona/Fault/Ricevute...)
		// Per i profili diversi dal sincrono e' possibile impostare dove far ritornare l'errore
		if(
				(this.bustaRichiesta!=null) &&
				(!org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) &&
				(!this.oneWayStateless) && 
				(!this.sincronoStateless) && 
				(!this.asincronoStateless) && 
				(!this.routingStateless) 
			){
			// New connection puo' essere usata solo se non siamo in modalita stateless
			this.newConnectionForResponse = this.configurazionePdDReader.newConnectionForResponse();
		}
		this.parametriInvioBustaErrore.setNewConnectionForResponse(this.newConnectionForResponse);
		if( (!this.oneWayStateless) && (!this.sincronoStateless) && (!this.asincronoStateless) && (!this.routingStateless)){
			// New connection puo' essere usata solo se non siamo in modalita stateless
			this.utilizzoIndirizzoTelematico = this.configurazionePdDReader.isUtilizzoIndirizzoTelematico();
		}
		this.parametriInvioBustaErrore.setUtilizzoIndirizzoTelematico(this.utilizzoIndirizzoTelematico);

		/* --------- Rilascio connessione se agisco come router stateless --------------- */
		if(this.routingStateless){
			this.openspcoopstate.releaseResource();
		} 

		this.infoIntegrazione = new Integrazione();
		this.infoIntegrazione.setIdModuloInAttesa(this.msgContext.getIdModulo());
		this.infoIntegrazione.setStateless(this.portaStateless);
		this.parametriGenerazioneBustaErrore.setIntegrazione(this.infoIntegrazione);




		


		

		
		
		
		
		
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
			if (this.msgContext.isGestioneRisposta()) {
				
				if(erroreIntegrazione==null){
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_558_HANDLER_IN_PROTOCOL_REQUEST);
				}
				if(integrationFunctionError==null) {
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				
				this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.setErroreIntegrazione(erroreIntegrazione);
				OpenSPCoop2Message errorMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				if(e instanceof HandlerException he){
					he.customized(errorMsg);
				}
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
			}
			this.openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		
		
		
		

		// Validazione Semantica (se modalita router non attiva)
		if(!this.functionAsRouter){


			/* ----------- Raccolta FlowParameter MTOM / Security ------------ */
			this.msgDiag.mediumDebug("Raccolta FlowParameter MTOM / Security proprieta...");
			MTOMProcessor mtomProcessor = null;
			FlowProperties flowPropertiesRequest = null;
			try{
				
				// read flow Properties
				flowPropertiesRequest = ricezioneBusteUtils.getFlowPropertiesRequest(this.requestMessage, this.bustaRichiesta, this.configurazionePdDReader, 
						((StateMessage)this.openspcoopstate.getStatoRichiesta()), this.msgDiag,this.logCore,propertiesReader,
						this.ruoloBustaRicevuta,this.implementazionePdDMittente,requestInfo, this.inRequestContext.getPddContext(),this.pa);				
				this.flowPropertiesResponse = ricezioneBusteUtils.getFlowPropertiesResponse(this.requestMessage, this.bustaRichiesta, this.configurazionePdDReader, 
						((StateMessage)this.openspcoopstate.getStatoRichiesta()), this.msgDiag,this.logCore,propertiesReader,
						this.ruoloBustaRicevuta,this.implementazionePdDMittente,requestInfo, this.inRequestContext.getPddContext(),this.pa,
						flowPropertiesRequest);
				this.parametriGenerazioneBustaErrore.setFlowPropertiesResponse(this.flowPropertiesResponse);
				
				// init message security context
				if(flowPropertiesRequest!=null && flowPropertiesRequest.messageSecurity!=null && 
						flowPropertiesRequest.messageSecurity.getFlowParameters()!=null &&
					flowPropertiesRequest.messageSecurity.getFlowParameters().size() > 0){
					this.messageSecurityContext.setIncomingProperties(flowPropertiesRequest.messageSecurity.getFlowParameters());
				}
				
				// init mtom processor
				mtomProcessor = new MTOMProcessor(flowPropertiesRequest.mtom, flowPropertiesRequest.messageSecurity, 
						this.tipoPorta, this.msgDiag, this.logCore, this.pddContext);
				
				this.msgDiag.mediumDebug("Raccolta FlowParameter MTOM / Security completata con successo");
				
			}catch(Exception e){

				// Emetto log, non ancora emesso
				boolean mittenteEsistente = validatore.getBusta()!=null && 
						(validatore.getBusta().getMittente()!=null || validatore.getBusta().getTipoMittente()!=null); 
				this.msgDiag.emitRicezioneMessaggioModuloBuste(this.msgDiag, this.pddContext, !mittenteEsistente);
			
				this.msgDiag.logErroreGenerico(e,"RaccoltaFlowParameter_MTOM_Security");
				
				// Tracciamento richiesta: non ancora registrata
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Riscontrato errore durante la lettura delle proprieta' di MTOM / SecurityMessage: "+e.getMessage());
					this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
							this.correlazioneApplicativa);
				}
				if(this.msgContext.isGestioneRisposta()){
					
					this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
					this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
					OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

					this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
					this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

				}
				this.openspcoopstate.releaseResource();
				return;
			}

			
			
			
			/* ----------- MTOM Processor BeforeSecurity ------------ */
			this.msgDiag.mediumDebug("MTOM Processor [BeforeSecurity]...");
			try{
				mtomProcessor.mtomBeforeSecurity(this.requestMessage, flowPropertiesRequest.tipoMessaggio);
			}catch(Exception e){
				// L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
				/**this.msgDiag.logErroreGenerico(e,"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");*/
				
				// Tracciamento richiesta: non ancora registrata
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Riscontrato errore durante la gestione MTOM(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+"): "+e.getMessage());
					this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
							this.correlazioneApplicativa);
				}
				if(this.msgContext.isGestioneRisposta()){
					
					this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
					this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR));
					this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.ATTACHMENTS_PROCESSING_REQUEST_FAILED);
					OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

					this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
					this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

				}
				this.openspcoopstate.releaseResource();
				return;
			}
			

			/* ----------- Validazione Semantica (viene anche applicata la sicurezza) ------------ */
			
			this.msgDiag.logPersonalizzato("validazioneSemantica.beforeSecurity");
			boolean presenzaRichiestaProtocollo = validatore.validazioneSemantica_beforeMessageSecurity(requestInfo.getProtocolServiceBinding(),false, null, requestInfo);
			
			if(!validatore.isRilevatiErroriDuranteValidazioneSemantica()){
				
				try{
					if(org.openspcoop2.security.message.engine.WSSUtilities.isNormalizeToSaajImpl(this.messageSecurityContext)){
						this.msgDiag.mediumDebug("Normalize to saajImpl");
						/**System.out.println("RicezioneBuste.request.normalize");*/
						this.requestMessage = this.requestMessage.normalizeToSaajImpl();
						
						validatore.updateMsg(this.requestMessage);
					}
				}catch(Exception e){
					this.msgDiag.logErroreGenerico(e,"NormalizeRequestToSaajImpl");
					
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Riscontrato errore durante la gestione NormalizeRequestToSaajImpl: "+e.getMessage());
						this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
								this.correlazioneApplicativa);
					}
					if(this.msgContext.isGestioneRisposta()){
						
						this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
						this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
						if(validatore.getErrore_integrationFunctionError()!=null) {
							this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(validatore.getErrore_integrationFunctionError());
						}
						OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

						this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
						this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

					}
					this.openspcoopstate.releaseResource();
					return;
				}
				
				this.msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di richiesta ...");
				presenzaRichiestaProtocollo = validatore.validazioneSemantica_messageSecurity_readSecurityInfo(this.messageSecurityContext);
				this.msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di richiesta completata con successo");
			
				if(!validatore.isRilevatiErroriDuranteValidazioneSemantica()){				
					if(this.messageSecurityContext!= null && this.messageSecurityContext.getIncomingProperties() != null && this.messageSecurityContext.getIncomingProperties().size() > 0){
					
						String tipoSicurezza = SecurityConstants.convertActionToString(this.messageSecurityContext.getIncomingProperties());
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoSicurezza);
						this.pddContext.addObject(CostantiPdD.TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoSicurezza);
						
						this.msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaInCorso");
						
						StringBuilder bfErroreSecurity = new StringBuilder();
						DynamicMapBuilderUtils.injectDynamicMap(this.bustaRichiesta, requestInfo, this.pddContext, this.logCore);
						presenzaRichiestaProtocollo = validatore.validazioneSemantica_messageSecurity_process(this.messageSecurityContext, bfErroreSecurity,
								this.transaction!=null ? this.transaction.getTempiElaborazione() : null,
										true);
						
						if(bfErroreSecurity.length()>0){
							this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , bfErroreSecurity.toString() );
							this.msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaInErrore");
						}
						else{
							this.msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaEffettuato");
						}
					}	
					else{
						this.msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaDisabilitato");
					}
				}
			
				if(!validatore.isRilevatiErroriDuranteValidazioneSemantica()){
					
					this.msgDiag.logPersonalizzato("validazioneSemantica.afterSecurity");
					presenzaRichiestaProtocollo = validatore.validazioneSemantica_afterMessageSecurity(this.proprietaManifestAttachments, validazioneIDBustaCompleta, requestInfo);
			
				}
			}

			if(!presenzaRichiestaProtocollo){

				// Emetto log, non ancora emesso
				boolean mittenteEsistente = validatore.getBusta()!=null && 
						(validatore.getBusta().getMittente()!=null || validatore.getBusta().getTipoMittente()!=null); 
				this.msgDiag.emitRicezioneMessaggioModuloBuste(this.msgDiag, this.pddContext, !mittenteEsistente);
				
				try{
					this.msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, validatore.getErrore().getDescrizione(this.protocolFactory));
				}catch(Exception e){
					logError(this.logCore, "getDescrizione protocollo Error:"+e.getMessage(),e);
				}
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_BUSTA, CostantiPdD.TIPO_VALIDAZIONE_BUSTA_SEMANTICA);
				this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_VALIDAZIONE_NON_RIUSCITA);
				
				// Tracciamento richiesta: non ancora registrata
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(this.msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_VALIDAZIONE_NON_RIUSCITA));
					this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
							this.correlazioneApplicativa);
				}
				if(this.msgContext.isGestioneRisposta()){
					
					this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
					this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO));
					if(validatore.getErrore_integrationFunctionError()!=null) {
						this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(validatore.getErrore_integrationFunctionError());
					}
					OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,validatore.getEccezioneProcessamentoValidazioneSemantica());
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

					this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
					this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

				}
				this.openspcoopstate.releaseResource();
				return;
			}
			
			
			/* ----------- MTOM Processor AfterSecurity ------------ */
			this.msgDiag.mediumDebug("MTOM Processor [AfterSecurity]...");
			try{
				mtomProcessor.mtomAfterSecurity(this.requestMessage, flowPropertiesRequest.tipoMessaggio);
			}catch(Exception e){
				// L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
				/**this.msgDiag.logErroreGenerico(e,"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");*/
				
				// Tracciamento richiesta: non ancora registrata
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Riscontrato errore durante la gestione MTOM(AfterSec-"+mtomProcessor.getMTOMProcessorType()+"): "+e.getMessage());
					this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
							this.correlazioneApplicativa);
				}
				if(this.msgContext.isGestioneRisposta()){
					
					this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
					this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR));
					this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.ATTACHMENTS_PROCESSING_REQUEST_FAILED);
					OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

					this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
					this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

				}
				this.openspcoopstate.releaseResource();
				return;
			}
			
		}
		this.erroriValidazione = validatore.getEccezioniValidazione();
		this.erroriProcessamento =validatore.getEccezioniProcessamento();
		IntegrationFunctionError integrationFunctionErrorValidazione = validatore.getErrore_integrationFunctionError(); 
		SecurityInfo securityInfoRequest = validatore.getSecurityInfo();
		
		this.isMessaggioErroreProtocollo = validatore.isErroreProtocollo();
		// Se ho un msg Errore e' interessante solo utilizzare mittente/destinatario poiche' il servizio non esistera'
		// La busta e' stata invertita tra mittente e destinatario
		if(this.isMessaggioErroreProtocollo && this.idServizio!=null){
			this.cleanDatiServizio(this.idServizio);
			this.idServizio.setAzione(null);
		}
		this.bustaDiServizio = validatore.isBustaDiServizio();
		if(validatore.getInfoServizio()!=null && validatore.getInfoServizio().getIdAccordo()!=null){
			this.msgContext.getProtocol().setIdAccordo(validatore.getInfoServizio().getIdAccordo());
			richiestaApplicativa.setIdAccordo(validatore.getInfoServizio().getIdAccordo());
		}
		else if(infoServizio!=null && infoServizio.getIdAccordo()!=null){
			this.msgContext.getProtocol().setIdAccordo(infoServizio.getIdAccordo());
			richiestaApplicativa.setIdAccordo(infoServizio.getIdAccordo());
		}
		
		// Aggiorno eventuale valore ProfiloCollaborazione dipendete dal profilo (PDC)
		if(this.bustaRichiesta!=null && this.msgContext.getProtocol()!=null) {
			this.msgContext.getProtocol().setProfiloCollaborazione(this.bustaRichiesta.getProfiloDiCollaborazione(),this.bustaRichiesta.getProfiloDiCollaborazioneValue());
			if(
					(this.bustaRichiesta!=null) &&
					(this.bustaRichiesta.getVersioneServizio()!=null) && 
					(this.bustaRichiesta.getVersioneServizio().intValue()>0) && 
					(this.msgContext.getProtocol().getVersioneServizio()!=null) &&
					(this.bustaRichiesta.getVersioneServizio().intValue()!=this.msgContext.getProtocol().getVersioneServizio().intValue())
				) {
				this.msgContext.getProtocol().setVersioneServizio(this.bustaRichiesta.getVersioneServizio());
			}
		}

		
		
		
		
		
		/* ------------ Riconciliazione Mittente per caso intermediario ------------- */
		if(Costanti.MODIPA_PROTOCOL_NAME.equals(this.protocolFactory.getProtocol()) &&
				this.pddContext.containsKey(CostantiPdD.INTERMEDIARIO)) {
				Object o = this.pddContext.get(CostantiPdD.INTERMEDIARIO);
				if(o instanceof IDSoggetto intermediario &&
					this.soggettoFruitore!=null && this.soggettoFruitore.equals(intermediario) && this.bustaRichiesta!=null &&
					this.bustaRichiesta.getMittente()!=null && !this.bustaRichiesta.getMittente().equals(intermediario.getNome())) {
					this.soggettoFruitore.setNome(this.bustaRichiesta.getMittente());
					this.soggettoFruitore.setCodicePorta(this.bustaRichiesta.getIdentificativoPortaMittente());
					if(this.msgDiag!=null) {
						this.msgDiag.setFruitore(this.soggettoFruitore);
					}
					if(this.msgContext.getProtocol()!=null) {
						this.msgContext.getProtocol().setFruitore(this.soggettoFruitore);
					}
				}
		}
		
		
		
		
		
		/* ------------ Riconciliazione ID Messaggio con quello ricevuto nel token oAuth ------------- */
		
		if(Costanti.MODIPA_PROTOCOL_NAME.equals(this.protocolFactory.getProtocol()) &&
			this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.MODI_JTI_REQUEST_ID_UPDATE_DIAGNOSTIC)) {
			Object o = this.pddContext.get(org.openspcoop2.core.constants.Costanti.MODI_JTI_REQUEST_ID_UPDATE_DIAGNOSTIC);
			if(o instanceof String s) {
				if (this.msgDiag != null)
					this.msgDiag.updateKeywordIdMessaggioRichiesta(s);
				this.idMessageRequest = (String)o;
				this.msgContext.getProtocol().setIdRichiesta(this.idMessageRequest);
			}
		}
		
		
		
		
		
		
		
		
		/* -------- Controlli Header di Integrazione ------------- */
		
		IProtocolConfiguration protocolConfig = this.protocolFactory.createProtocolConfiguration();
		if(
				(infoServizio!=null) && 
				(infoServizio.getIdRiferimentoRichiesta()) &&
				(protocolConfig.isIntegrationInfoRequired(TipoPdD.APPLICATIVA, requestInfo.getProtocolServiceBinding(),FunzionalitaProtocollo.RIFERIMENTO_ID_RICHIESTA))
			) {
			String riferimentoRichiesta = null;
			if (this.headerIntegrazioneRichiesta!=null &&
					this.headerIntegrazioneRichiesta.getBusta() != null && this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null) {
				riferimentoRichiesta = this.headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio();
			}
			if(riferimentoRichiesta==null) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < this.tipiIntegrazionePA.length; i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(this.tipiIntegrazionePA[i]);
				}
				
				if (this.msgDiag != null) {
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIPI_INTEGRAZIONE ,bf.toString() );
					this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"riferimentoIdRichiesta.nonFornito");
				}
				
				// Tracciamento richiesta: non ancora registrata
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(this.msgDiag.getMessaggio(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"riferimentoIdRichiesta.nonFornito"));
					this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
							this.correlazioneApplicativa);
				}
				
				if(this.msgContext.isGestioneRisposta()){
					
					ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_442_RIFERIMENTO_ID_MESSAGGIO.getErroreIntegrazione();
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.CORRELATION_INFORMATION_NOT_FOUND;
					
					this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
					this.parametriGenerazioneBustaErrore.setErroreIntegrazione(erroreIntegrazione);
					this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
					OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore, null);

					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
					this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
				}
				this.openspcoopstate.releaseResource();
				return;

				
			}
		}
		
		
		
		
		
		
		/* ----------- Sbustamento ------------ */
		BustaRawContent<?> headerProtocolloRichiesta = null;
		if(!this.functionAsRouter && 
				!this.isMessaggioErroreProtocollo && 
				this.erroriProcessamento.isEmpty() && this.erroriValidazione.isEmpty() &&
				!this.bustaDiServizio){
			this.msgDiag.highDebug("Tipo Messaggio Richiesta prima dello sbustamento ["+FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RICHIESTA
					+"] ["+this.requestMessage.getClass().getName()+"]");
			org.openspcoop2.protocol.engine.builder.Sbustamento sbustatore = new org.openspcoop2.protocol.engine.builder.Sbustamento(this.protocolFactory,this.openspcoopstate.getStatoRichiesta());
			ProtocolMessage protocolMessage = sbustatore.sbustamento(this.requestMessage,this.pddContext,
					this.bustaRichiesta,
					RuoloMessaggio.RICHIESTA,properties.isValidazioneManifestAttachments(),this.proprietaManifestAttachments,
					FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RICHIESTA, requestInfo);
			if(protocolMessage!=null) {
				if(protocolMessage.isUseBustaRawContentReadByValidation()) {
					headerProtocolloRichiesta = soapHeaderElement;
				}
				else {
					headerProtocolloRichiesta = protocolMessage.getBustaRawContent();
				}
				this.requestMessage = protocolMessage.getMessage(); // updated
			}
			this.msgDiag.highDebug("Tipo Messaggio Richiesta dopo lo sbustamento ["+FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RICHIESTA
					+"] ["+(this.requestMessage!=null ? this.requestMessage.getClass().getName() : null)+"]");
		}
		else{
			headerProtocolloRichiesta = soapHeaderElement;
		}
		
		
		
		
		
		/* --------------- CorrelazioneApplicativa (Lato PA se definita) ------------------- 
		 * NOTA: Vi e' l'overwrite dell'informazione sulla correlazione applicativa eventualmente letta prima dall'header di trasporto
		 *        opzione comunuque per default disabitata.
		 * --------------- */
		if(!this.functionAsRouter && 
				!this.isMessaggioErroreProtocollo && 
				this.erroriProcessamento.isEmpty() && this.erroriValidazione.isEmpty() &&
				!this.bustaDiServizio &&
				this.pa!=null && this.pa.getCorrelazioneApplicativa()!=null){
		
			GestoreCorrelazioneApplicativa gestoreCorrelazioneApplicativa = null;
			try {
				GestoreCorrelazioneApplicativaConfig caConfig = new GestoreCorrelazioneApplicativaConfig();
				caConfig.setState(this.openspcoopstate.getStatoRichiesta());
				caConfig.setAlog(this.logCore);
				caConfig.setSoggettoFruitore(this.soggettoFruitore);
				caConfig.setIdServizio(this.idServizio);
				caConfig.setBusta(this.bustaRichiesta);
				caConfig.setServizioApplicativo(servizioApplicativoFruitore);
				caConfig.setProtocolFactory(this.protocolFactory);
				caConfig.setTransaction(this.transaction);
				caConfig.setPddContext(this.pddContext);
				caConfig.setPa(this.pa);
				gestoreCorrelazioneApplicativa = new GestoreCorrelazioneApplicativa(caConfig);
				
				gestoreCorrelazioneApplicativa.verificaCorrelazione(this.pa.getCorrelazioneApplicativa(), urlProtocolContext, this.requestMessage, 
						this.headerIntegrazioneRichiesta, false);
				
				if(gestoreCorrelazioneApplicativa.getIdCorrelazione()!=null)
					this.correlazioneApplicativa = gestoreCorrelazioneApplicativa.getIdCorrelazione();
				
			}catch(Exception e){
				
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA, "true");
				
				this.msgDiag.logErroreGenerico(e,"CorrelazioneApplicativa("+this.bustaRichiesta.getID()+")");
				logError(this.logCore, "Riscontrato errore durante la correlazione applicativa ["+this.bustaRichiesta.getID()+"]",e);
				
				// Tracciamento richiesta: non ancora registrata
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Riscontrato errore durante la correlazione applicativa ["+this.bustaRichiesta.getID()+"]: "+e.getMessage());
					this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
							this.correlazioneApplicativa);
				}
				
				if(this.msgContext.isGestioneRisposta()){
					
					ErroreIntegrazione errore = null;
					if(gestoreCorrelazioneApplicativa!=null){
						errore = gestoreCorrelazioneApplicativa.getErrore();
					}
					if(errore==null){
						errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA);
					}
					
					this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
					this.parametriGenerazioneBustaErrore.setErroreIntegrazione(errore);
					IntegrationFunctionError integrationFunctionError = null;
					if(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.equals(errore.getCodiceErrore())){
						integrationFunctionError = IntegrationFunctionError.APPLICATION_CORRELATION_IDENTIFICATION_REQUEST_FAILED;
					}
					else{
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
					OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);

					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
					this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
				}
				this.openspcoopstate.releaseResource();
				return;
			}
		}
		else{
			// L'informazione nel caso di functionAsRouter o isMessaggioErrore o erroriProcessamento.size()>0 o erroriValidazione.size()>0 o bustaDiServizio non e' significativa
			// Invece nel caso di pa==null o di pa.getCorrelazioneApplicativa==null l'informazione eventualmente cmq letta prima non deve essere usata:
			this.correlazioneApplicativa = null;
		}
		
		
		
		
		
		
		
		
		
		
		
		/* --------------- Header Integrazione SOAP (aggiornamento/eliminazione) -------------------  */
		if(this.tipiIntegrazionePA!=null){
			
			if(inRequestPAMessage!=null){
				inRequestPAMessage.setBustaRichiesta(this.bustaRichiesta);
				inRequestPAMessage.setSoggettoMittente(this.soggettoFruitore);
			}
			
			for (int i = 0; i < this.tipiIntegrazionePA.length; i++) {
				try {
					IGestoreIntegrazionePA gestore = pluginLoader.newIntegrazionePortaApplicativa(this.tipiIntegrazionePA[i]);
					if(gestore!=null){
						String classType = null;
						try {
							classType = gestore.getClass().getName();
							AbstractCore.init(gestore, this.pddContext, this.protocolFactory);
						} catch (Exception e) {
							throw new CoreException(
									"Riscontrato errore durante l'inizializzazione della classe ["+ classType
											+ "] da utilizzare per la gestione dell'integrazione delle erogazioni (aggiornamento/eliminazione) di tipo ["+ this.tipiIntegrazionePA[i] + "]: " + e.getMessage());
						}
						if (gestore instanceof IGestoreIntegrazionePASoap gestorePASoap) {
							if(propertiesReader.deleteHeaderIntegrazioneRequestPA()){
								gestorePASoap.deleteInRequestHeader(inRequestPAMessage);
							}
							else{
								gestorePASoap.updateInRequestHeader(inRequestPAMessage, this.idMessageRequest, servizioApplicativoFruitore, this.correlazioneApplicativa);
							}
						} 	
					}
				} catch (Exception e) {
					this.msgDiag.logErroreGenerico(e,"HeaderIntegrazione("+this.tipiIntegrazionePA[i]+")");
					
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Riscontrato errore durante la gestione dell'Header di Integrazione("+this.tipiIntegrazionePA[i]+"): "+e.getMessage());
						this.tracciamento.registraRichiesta(this.requestMessage,null,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
								this.correlazioneApplicativa);
					}
					
					if(this.msgContext.isGestioneRisposta()){
						
						this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
						this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_541_GESTIONE_HEADER_INTEGRAZIONE));
						OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);

						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
						this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
						this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
					}
					this.openspcoopstate.releaseResource();
					return;
				}
			}
		}
		
		
		
		
		
		
		
		
		

		

		/* ----------- Correlazione nei messaggi diagnostici ---------------*/
		this.msgDiag.setIdCorrelazioneApplicativa(this.correlazioneApplicativa);
		if(this.msgContext.getIntegrazione()!=null)
			this.msgContext.getIntegrazione().setIdCorrelazioneApplicativa(this.correlazioneApplicativa);
		this.parametriGenerazioneBustaErrore.setMsgDiag(this.msgDiag);
		this.parametriInvioBustaErrore.setMsgDiag(this.msgDiag);
		
		
		
		
		
		
		
		
		
		
		
		/* ----------- Emetto msg diagnostico di ricezione Busta ----------------------*/
		DettaglioEccezione dettaglioEccezione = null;
		this.mittenteAnonimo = false;
		if(validatore.getBusta()!=null && 
				(validatore.getBusta().getMittente()!=null || validatore.getBusta().getTipoMittente()!=null)){
			this.mittenteAnonimo = false;
		}
		else{
			this.mittenteAnonimo = true;
		}
		if(this.isMessaggioErroreProtocollo){
			if(validatore.isMessaggioErroreIntestazione()){
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA_ERRORE, CostantiPdD.TIPO_MESSAGGIO_BUSTA_ERRORE_INTESTAZIONE);
			}else{
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA_ERRORE, CostantiPdD.TIPO_MESSAGGIO_BUSTA_ERRORE_PROCESSAMENTO);
			}
			this.msgDiag.logPersonalizzato("ricezioneMessaggioErrore");
			
			// Esamino se e' presente un elemento DettaglioEccezione
			try{
				dettaglioEccezione = XMLUtils.getDettaglioEccezione(this.logCore,this.requestMessage);
			}catch(Exception e){
				logError(this.logCore, "Errore durante l'analisi del dettaglio dell'eccezione",e);
			}
			
		}else{
			this.msgDiag.emitRicezioneMessaggioModuloBuste(this.msgDiag, this.pddContext, this.mittenteAnonimo);
		}












		/* -------- Profilo di Gestione ------------- */
		this.versioneProtocollo = validatore.getProfiloGestione();
		this.msgDiag.mediumDebug("Profilo di gestione ["+RicezioneBuste.ID_MODULO+"] della busta: "+this.versioneProtocollo);
		richiestaApplicativa.setProfiloGestione(this.versioneProtocollo);
		this.parametriGenerazioneBustaErrore.setProfiloGestione(this.versioneProtocollo);
		this.moduleManager = this.protocolFactory.createProtocolVersionManager(this.versioneProtocollo);
		if( !this.functionAsRouter &&
			// Riferimento messaggio con un profilo sincrono non può essere ricevuto in questo contesto.
			org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) && 
			this.bustaRichiesta.getRiferimentoMessaggio()!=null &&
			!this.isMessaggioErroreProtocollo &&// aggiunto !isMessaggioErrore poiche' una busta di eccezione puo' contenenere in effetti il rifMsg
			(this.ruoloBustaRicevuta==null || !this.ruoloBustaRicevuta.equals(RuoloBusta.RICHIESTA)) // aggiunto questo controllo perchè i protocolli con id riferimento richiesta hanno il rif messaggio, ma il ruolo rimane RICHIESTA
			){ 
			Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALIDO.getErroreCooperazione(), this.protocolFactory);
			this.erroriValidazione.add(ecc);
		}

















		/* -------- Gestione errori ------------- */
		// Se sono in modalita router: se vi sono errori di processamento/validazione, ritorno subito l'errore.
		// altrimenti: se sono presenti errore di processamento, ritorno subito l'errore.
		this.msgDiag.mediumDebug("Gestione errori...");
		if( (!this.erroriProcessamento.isEmpty()) || (this.functionAsRouter && (!this.erroriValidazione.isEmpty())) ){
			StringBuilder errore = new StringBuilder();
			for(int k=0; k<this.erroriProcessamento.size();k++){
				Eccezione erroreProcessamento = this.erroriProcessamento.get(k);
				try{
					errore.append("Processamento["+this.traduttore.toString(erroreProcessamento.getCodiceEccezione(),erroreProcessamento.getSubCodiceEccezione())+"] "+this.erroriProcessamento.get(k).getDescrizione(this.protocolFactory)+"\n");
				}catch(Exception e){
					logError(this.logCore, "getDescrizione failure:"+e.getMessage(),e);
				}
			}
			for(int k=0; k<this.erroriValidazione.size();k++){
				Eccezione erroreValidazione = this.erroriValidazione.get(k);
				try{
					errore.append("Validazione["+this.traduttore.toString(erroreValidazione.getCodiceEccezione(),erroreValidazione.getSubCodiceEccezione())+"] "+this.erroriValidazione.get(k).getDescrizione(this.protocolFactory)+"\n");
				}catch(Exception e){
					logError(this.logCore, "getDescrizione errore validazione fallita:"+e.getMessage(),e);
				}
			}
			this.msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, errore.toString());
			this.msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_ECCEZIONI, (this.erroriProcessamento.size()+this.erroriValidazione.size())+"" );
			this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_VALIDAZIONE_RILEVATA_BUSTA_NON_CORRETTA);
			// Tracciamento richiesta: non ancora registrata
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(this.msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_VALIDAZIONE_RILEVATA_BUSTA_NON_CORRETTA));
				this.tracciamento.registraRichiesta(this.requestMessage,securityInfoRequest,soapHeaderElement,this.bustaRichiesta,esitoTraccia,
						Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
						this.correlazioneApplicativa);
			}
			if(this.msgContext.isGestioneRisposta()){
				OpenSPCoop2Message errorOpenSPCoopMsg = null;
				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				if(!this.erroriProcessamento.isEmpty()){
					// inserisco in erroriProcessamento anche erroriValidazione
					for(int j=0; j<this.erroriValidazione.size(); j++){
						this.erroriProcessamento.add(this.erroriValidazione.get(j));
					}

					this.parametriGenerazioneBustaErrore.setError(this.erroriProcessamento);
					errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,null);

				}else{

					this.parametriGenerazioneBustaErrore.setError(this.erroriValidazione);
					if(integrationFunctionErrorValidazione!=null) {
						this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionErrorValidazione);
					}
					errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreValidazione(this.parametriGenerazioneBustaErrore);

				}
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

			}
			this.openspcoopstate.releaseResource();
			return;
		}











		
		
		
		/* -------- Tracciamento ------------- */
		if(this.msgContext.isTracciamentoAbilitato()){
			this.msgDiag.mediumDebug("Tracciamento busta di richiesta...");
			
			EsitoElaborazioneMessaggioTracciato esitoTraccia = null;
			if( (!this.erroriProcessamento.isEmpty()) || (!this.erroriValidazione.isEmpty()) ){
				
				boolean foundErroriGravi = false;
				
				String dettaglioErrore = null;
				StringBuilder eccBuffer = new StringBuilder();
				for(int k = 0; k < this.erroriProcessamento.size() ; k++){
					Eccezione er = this.erroriProcessamento.get(k);
					if(k>0)
						eccBuffer.append(" ");
					eccBuffer.append(er.toString(this.protocolFactory));
					eccBuffer.append(";");
					
					if(this.moduleManager.isIgnoraEccezioniLivelloNonGrave()){
						if(er.getRilevanza()!=null){
							if(LivelloRilevanza.isEccezioneLivelloGrave(er.getRilevanza())){
								foundErroriGravi = true;
							}
						}else{
							foundErroriGravi = true;
						}
					}else{
						foundErroriGravi = true;
					}
				}
				for(int k = 0; k < this.erroriValidazione.size() ; k++){
					Eccezione er = this.erroriValidazione.get(k);
					if(eccBuffer.length()>0)
						eccBuffer.append(" ");
					eccBuffer.append(er.toString(this.protocolFactory));
					eccBuffer.append(";");
					
					if(this.moduleManager.isIgnoraEccezioniLivelloNonGrave()){
						if(er.getRilevanza()!=null){
							if(LivelloRilevanza.isEccezioneLivelloGrave(er.getRilevanza())){
								foundErroriGravi = true;
							}
						}else{
							foundErroriGravi = true;
						}
					}else{
						foundErroriGravi = true;
					}
				}
				this.msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_ECCEZIONI, this.erroriProcessamento.size()+this.erroriValidazione.size()+"");
				this.msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, eccBuffer.toString());
				if(this.isMessaggioErroreProtocollo){
					dettaglioErrore = this.msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"validazioneBustaErrore.listaEccezioniMalformata");
				}else{
					dettaglioErrore = this.msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_VALIDAZIONE_RILEVATA_BUSTA_NON_CORRETTA);
				}
				if(foundErroriGravi){
					esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(dettaglioErrore);
				}
				else{
					esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioRicevuto(dettaglioErrore);
				}
			}
			else{
				esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioRicevuto();
			}
			
			// Tracciamento Busta Ricevuta
			this.tracciamento.registraRichiesta(this.requestMessage,securityInfoRequest,headerProtocolloRichiesta,this.bustaRichiesta,esitoTraccia,
					Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
					this.correlazioneApplicativa);
		}













		/* -------- Check Mittente o Destinatario o Servizio conosciuto o identificativo errato ---------------- */
		this.msgDiag.mediumDebug("Controllo mittente/destinatario/servizio (se sconosciuto, risposta immediata sulla connessione)...");
		for(int k = 0; k < this.erroriValidazione.size() ; k++){
			Eccezione er = this.erroriValidazione.get(k);
			if( CodiceErroreCooperazione.isEccezioneMittente(er.getCodiceEccezione()) ||			
				CodiceErroreCooperazione.isEccezioneDestinatario(er.getCodiceEccezione()) ||	
				CodiceErroreCooperazione.isEccezioneServizio(er.getCodiceEccezione()) ||	
				CodiceErroreCooperazione.isEccezioneIdentificativoMessaggio(er.getCodiceEccezione()) ||	
				CodiceErroreCooperazione.isEccezioneSicurezzaAutorizzazione(er.getCodiceEccezione()) ){

				if(!this.functionAsRouter &&
					// Può esistere un errore mittente, che non è altro che una segnalazione sull'indirizzo telematico
					this.moduleManager.isEccezioniLivelloInfoAbilitato()&&
					LivelloRilevanza.INFO.equals(er.getRilevanza())
					) {
					continue;
				}

				StringBuilder eccBuffer = new StringBuilder();
				for(int j = 0; j < this.erroriValidazione.size() ; j++){
					if(j>0)
						eccBuffer.append("\n");
					eccBuffer.append(this.erroriValidazione.get(j).toString(this.protocolFactory));
				}
				this.msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, eccBuffer.toString());
				this.msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_ECCEZIONI, this.erroriValidazione.size()+"" );
				this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,MsgDiagnosticiProperties.MSG_DIAG_VALIDAZIONE_RILEVATA_BUSTA_NON_CORRETTA);
				if(this.msgContext.isGestioneRisposta()){
					
					this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
					List<Eccezione> errs = new ArrayList<>();
					for(int add = 0; add < this.erroriValidazione.size() ; add++){
						errs.add(this.erroriValidazione.get(add));
					}
					this.parametriGenerazioneBustaErrore.setError(errs);
					
					if(integrationFunctionErrorValidazione!=null) {
						this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionErrorValidazione);
					}
					else if(CodiceErroreCooperazione.isEccezioneSicurezzaAutorizzazione(er.getCodiceEccezione())) {
						this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.AUTHORIZATION);
					}
					else {
						this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.INVALID_INTEROPERABILITY_PROFILE_REQUEST);
					}
					
					OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreValidazione(this.parametriGenerazioneBustaErrore);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					// se il mittente e' sconosciuto non e' possibile utilizzare l'invio su di una nuova connessione,
					// a meno della presenza dell'indirizzo telematico, poiche' non esistera un connettore su cui spedire

					this.parametriInvioBustaErrore.setUtilizzoIndirizzoTelematico(false);
					this.parametriInvioBustaErrore.setNewConnectionForResponse(false);
					this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
					this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

				}
				this.openspcoopstate.releaseResource();
				return;
			}
		}
		
		this.finalProcess(validatore, servizioApplicativoFruitore, gestioneToken, idPA, idPD, inoltroMSG, gestioneAutenticazione, 
				implementazionePdDDestinatario, richiestaApplicativa, dettaglioEccezione, integrationFunctionErrorValidazione,
				credenziali);
	}



	private void finalProcess(
			Validatore validatore, 
			String servizioApplicativoFruitore, 
			RicezioneBusteGestioneToken gestioneToken,
			IDPortaApplicativa idPA,
			IDPortaDelegata idPD,
			InoltroBusteMessage inoltroMSG,
			RicezioneBusteGestioneAutenticazione gestioneAutenticazione,
			String implementazionePdDDestinatario,
			RichiestaApplicativa richiestaApplicativa,
			DettaglioEccezione dettaglioEccezione,
			IntegrationFunctionError integrationFunctionErrorValidazione,
			Credenziali credenziali) throws ProtocolException {
				
		// RequestInfo
		RequestInfo requestInfo = this.msgContext.getRequestInfo();
		
		// OpenSPCoop Properties
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
				
		// Loader
		Loader loader = Loader.getInstance();
		
		String token = gestioneToken.getToken();
		
		// Data Ingresso Richiesta
		Date dataIngressoRichiesta = this.msgContext.getDataIngressoRichiesta();

		ClassNameProperties className = ClassNameProperties.getInstance();
		
		boolean soggettoAutenticato = gestioneAutenticazione.isSoggettoAutenticato();
		boolean supportatoAutenticazioneSoggetti = gestioneAutenticazione.isSupportatoAutenticazioneSoggetti();
		IDServizioApplicativo idApplicativoToken = gestioneAutenticazione.getIdApplicativoToken();
		boolean autenticazioneOpzionale = gestioneAutenticazione.isAutenticazioneOpzionale();



		/* -------- Check Autorizzazione (Se non ho ricevuto un msg Errore Protocollo)---------------- */
		
		// Guardo se vi sono eccezioni di validazione
		// Se ci sono eccezioni di processamento vengono ritornate prima
		boolean eccezioniValidazioni = false;
		for(int k = 0; k < this.erroriValidazione.size() ; k++){
			Eccezione er = this.erroriValidazione.get(k);
			if(!this.functionAsRouter &&
				// Check profilo linee guida 1.0
				// Può esistere un errore, che non è altro che una segnalazione sull'indirizzo telematico
				this.moduleManager.isEccezioniLivelloInfoAbilitato() &&
				LivelloRilevanza.INFO.equals(er.getRilevanza())
				){
				// nop
			}
			else {
				eccezioniValidazioni = true;
				break;
			}
		}
		
		this.msgDiag.mediumDebug("Autorizzazione ...");
		String tipoAutorizzazione = null;
		try{
			if(this.functionAsRouter){
				tipoAutorizzazione = propertiesReader.getTipoAutorizzazioneBuste();
			}
			else{
				if(this.pa!=null){
					tipoAutorizzazione = this.configurazionePdDReader.getAutorizzazione(this.pa);
				}
				// Non ha senso effettuare l'autorizzazione basato sulla PD
				/**else{
					tipoAutorizzazione = this.configurazionePdDReader.getAutorizzazione(this.pd);
				}*/
			}
		}catch(Exception notFound){
			// ignore
		}
		boolean isAttivoAutorizzazioneBuste = tipoAutorizzazione!=null && !CostantiConfigurazione.AUTORIZZAZIONE_NONE.equalsIgnoreCase(tipoAutorizzazione);
		this.msgContext.getIntegrazione().setTipoAutorizzazione(tipoAutorizzazione);
		if(tipoAutorizzazione!=null){
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE, tipoAutorizzazione);
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE_BUSTE, tipoAutorizzazione);
		}
		if(!isAttivoAutorizzazioneBuste){
			this.msgDiag.logPersonalizzato("autorizzazioneBusteDisabilitata");
		}

		DatiInvocazionePortaApplicativa datiInvocazione = null;
		
		if(isAttivoAutorizzazioneBuste &&
				!this.isMessaggioErroreProtocollo &&
				!this.bustaDiServizio &&
				!eccezioniValidazioni){	
			
			this.transaction.getTempiElaborazione().startAutorizzazione();
			try{

				this.msgDiag.mediumDebug("Autorizzazione di tipo ["+tipoAutorizzazione+"]...");

				//	Controllo Autorizzazione
				String identitaMittente = null;
				if(credenziali!=null && !soggettoAutenticato &&
					!"".equals(credenziali.toString())){
					identitaMittente = credenziali.toString();
					if(identitaMittente.endsWith(" ")){
						identitaMittente = identitaMittente.substring(0, identitaMittente.length()-1);	
					}
				}
				String subjectMessageSecurity = null;
				if(this.messageSecurityContext!=null){
					subjectMessageSecurity = this.messageSecurityContext.getSubject();
				}



				/* --- Calcolo idServizio e fruitore ---- */

				IDServizio idServizioPerAutorizzazione = getIdServizioPerAutorizzazione(this.idServizio, this.soggettoFruitore, this.functionAsRouter, this.bustaRichiesta, this.ruoloBustaRicevuta); 
				IDSoggetto idSoggettoMittentePerAutorizzazione = getIDSoggettoMittentePerAutorizzazione(this.idServizio, this.soggettoFruitore, this.functionAsRouter,this. bustaRichiesta, this.ruoloBustaRicevuta, supportatoAutenticazioneSoggetti);
				Soggetto soggettoMittentePerAutorizzazione = null;
				if(idSoggettoMittentePerAutorizzazione!=null){
					soggettoMittentePerAutorizzazione = this.registroServiziReader.getSoggetto(idSoggettoMittentePerAutorizzazione, null, requestInfo);
				}

				String tipoMessaggio = "messaggio";
				if(this.ruoloBustaRicevuta!=null &&
					(RuoloBusta.RICEVUTA_RICHIESTA.equals(this.ruoloBustaRicevuta.toString()) || RuoloBusta.RICEVUTA_RISPOSTA.equals(this.ruoloBustaRicevuta.toString()))
					){
					tipoMessaggio = "ricevuta asincrona";
				}
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA, tipoMessaggio);
				if(idSoggettoMittentePerAutorizzazione!=null){
					this.msgDiag.addKeyword(CostantiPdD.KEY_MITTENTE_E_SERVIZIO_DA_AUTORIZZARE, "fruitore ["+idSoggettoMittentePerAutorizzazione.toString()+"] -> servizio ["+idServizioPerAutorizzazione.toString()+"]");
				}
				else{
					this.msgDiag.addKeyword(CostantiPdD.KEY_MITTENTE_E_SERVIZIO_DA_AUTORIZZARE, "servizio ["+idServizioPerAutorizzazione.toString()+"]");
				}
				if(identitaMittente!=null)
					this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, " credenzialiMittente "+identitaMittente);
				else
					this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, "");
				if(servizioApplicativoFruitore!=null)
					this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE_MSG, " identitaServizioApplicativoFruitore ["+servizioApplicativoFruitore+"]");
				else
					this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE_MSG, "");
				if(subjectMessageSecurity!=null)
					this.msgDiag.addKeyword(CostantiPdD.KEY_SUBJECT_MESSAGE_SECURITY_MSG, " subjectMessageSecurity ["+subjectMessageSecurity+"]");
				else
					this.msgDiag.addKeyword(CostantiPdD.KEY_SUBJECT_MESSAGE_SECURITY_MSG, "");
				this.msgDiag.logPersonalizzato("autorizzazioneBusteInCorso");
				
				IDServizioApplicativo identitaServizioApplicativoFruitore = new IDServizioApplicativo();
				identitaServizioApplicativoFruitore.setNome(servizioApplicativoFruitore);
				identitaServizioApplicativoFruitore.setIdSoggettoProprietario(idSoggettoMittentePerAutorizzazione);
				
				datiInvocazione = new DatiInvocazionePortaApplicativa();
				datiInvocazione.setBusta(this.bustaRichiesta);
				datiInvocazione.setToken(token);
				datiInvocazione.setPddContext(this.pddContext);
				datiInvocazione.setInfoConnettoreIngresso(this.inRequestContext.getConnettore());
				datiInvocazione.setIdServizio(idServizioPerAutorizzazione);
				datiInvocazione.setState(this.openspcoopstate.getStatoRichiesta());
				datiInvocazione.setRequestInfo(requestInfo);
				datiInvocazione.setCredenzialiPdDMittente(credenziali);
				datiInvocazione.setIdentitaServizioApplicativoFruitore(identitaServizioApplicativoFruitore);
				datiInvocazione.setSubjectServizioApplicativoFruitoreFromMessageSecurityHeader(subjectMessageSecurity);
				datiInvocazione.setIdPA(idPA);
				datiInvocazione.setPa(this.pa);
				datiInvocazione.setIdPD(idPD);
				datiInvocazione.setPd(this.pd);
				datiInvocazione.setIdSoggettoFruitore(idSoggettoMittentePerAutorizzazione);
				datiInvocazione.setSoggettoFruitore(soggettoMittentePerAutorizzazione);
				datiInvocazione.setRuoloBusta(this.ruoloBustaRicevuta);
				
				EsitoAutorizzazionePortaApplicativa esito = 
						GestoreAutorizzazione.verificaAutorizzazionePortaApplicativa(tipoAutorizzazione, 
								datiInvocazione, this.pddContext, this.protocolFactory, this.requestMessage, this.logCore);
				CostantiPdD.addKeywordInCache(this.msgDiag, esito.isEsitoPresenteInCache(),
						this.pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE);
				if(esito.getDetails()==null){
					this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
				}else{
					this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " ("+esito.getDetails()+")");
				}
				if(!esito.isAutorizzato()){
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTORIZZAZIONE, "true");
					String descrizioneErrore = null;
					try{
						if(esito.getErroreCooperazione()!=null){
							descrizioneErrore = esito.getErroreCooperazione().getDescrizione(this.protocolFactory);
						}
						else{
							descrizioneErrore = esito.getErroreIntegrazione().getDescrizione(this.protocolFactory);
						}
						this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, descrizioneErrore);
					}catch(Exception e){
						logError(this.logCore, "getDescrizione Error:"+e.getMessage(),e);
					}
					this.msgDiag.addKeyword(CostantiPdD.KEY_POSIZIONE_ERRORE, this.traduttore.toString(esito.getErroreCooperazione().getCodiceErrore()));
					this.msgDiag.logPersonalizzato("autorizzazioneBusteFallita");
					String errorMsg =  "Riscontrato errore durante il processo di Autorizzazione per il messaggio con identificativo ["+this.bustaRichiesta.getID()+"]: "+descrizioneErrore;
					if(esito.getEccezioneProcessamento()!=null){
						logError(this.logCore, errorMsg,esito.getEccezioneProcessamento());
					}
					else{
						logError(this.logCore, errorMsg);
					}
					if(this.msgContext.isGestioneRisposta()){
						
						if(esito.getErrorMessage()!=null) {
							this.msgContext.setMessageResponse(esito.getErrorMessage());
						}
						else {
						
							OpenSPCoop2Message errorOpenSPCoopMsg = null;
							this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
							IntegrationFunctionError integrationFunctionError = esito.getIntegrationFunctionError();
							if(esito.getErroreCooperazione()!=null){
								this.parametriGenerazioneBustaErrore.setErroreCooperazione(esito.getErroreCooperazione());
								if(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.equals(esito.getErroreCooperazione().getCodiceErrore()) || 
										CodiceErroreCooperazione.SICUREZZA_FALSIFICAZIONE_MITTENTE.equals(esito.getErroreCooperazione().getCodiceErrore()) ||
										CodiceErroreCooperazione.SICUREZZA_TOKEN_AUTORIZZAZIONE_FALLITA.equals(esito.getErroreCooperazione().getCodiceErrore())){
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.AUTHORIZATION;
									}
									this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
									errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreValidazione(this.parametriGenerazioneBustaErrore);
								}
								// Else necessario per Certificazione DigitPA tramite Router
								else if(CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO.equals(esito.getErroreCooperazione().getCodiceErrore())){
									this.parametriGenerazioneBustaErrore.setErroreCooperazione(
											ErroriCooperazione.SERVIZIO_SCONOSCIUTO.getErroreCooperazione()); // in modo da utilizzare la posizione standard.
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.NOT_FOUND;
									}
									this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
									errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreValidazione(this.parametriGenerazioneBustaErrore);
								}
								else{
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
									}
									this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
									errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore, esito.getEccezioneProcessamento());
								}
							}
							else{
								this.parametriGenerazioneBustaErrore.setErroreIntegrazione(esito.getErroreIntegrazione());
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
								}
								this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
								errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore, esito.getEccezioneProcessamento());
							}
							
							if(esito.getWwwAuthenticateErrorHeader()!=null) {
								errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, esito.getWwwAuthenticateErrorHeader());
							}
	
							// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
							this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
							this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
							this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
							
						}
					}
					this.openspcoopstate.releaseResource();
					return;
				}else{
					this.msgDiag.logPersonalizzato("autorizzazioneBusteEffettuata");
				}
			}catch(Exception e){
				CostantiPdD.addKeywordInCache(this.msgDiag, false,
						this.pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE);
				this.msgDiag.logErroreGenerico(e,"AutorizzazioneMessaggio("+this.bustaRichiesta.getID()+")");
				logError(this.logCore, "Riscontrato errore durante il processo di Autorizzazione per il messaggio con identificativo ["+this.bustaRichiesta.getID()+"]",e);
				if(this.msgContext.isGestioneRisposta()){
					
					this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
					this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_504_AUTORIZZAZIONE));
					
					OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);

					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
					this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
				}
				this.openspcoopstate.releaseResource();
				return;
			}
			finally {
				this.transaction.getTempiElaborazione().endAutorizzazione();
			}
		}







		
		
		
		
		
		
		
		
		/* --------------- Verifica tipo soggetto fruitore e tipo servizio rispetto al canale utilizzato 
		 * (NOTA!: non sportarlo prima senno non si supera eventuali certificazioni) --------------- */
		this.msgDiag.mediumDebug("Verifica canale utilizzato...");
		List<String> tipiSoggettiSupportatiCanale = this.protocolFactory.createProtocolConfiguration().getTipiSoggetti();
		List<String> tipiServiziSupportatiCanale = this.protocolFactory.createProtocolConfiguration().getTipiServizi(this.requestMessage.getServiceBinding());
		ErroreCooperazione erroreVerificaTipoByProtocol = null;
		// Nota: se qualche informazione e' null verranno segnalati altri errori
		if(this.soggettoFruitore!=null && this.soggettoFruitore.getTipo()!=null && 
				!tipiSoggettiSupportatiCanale.contains(this.soggettoFruitore.getTipo())){
			if(autenticazioneOpzionale && !propertiesReader.isAutorizzazioneBustaAutenticazioneOpzionaleSoggettoFruitoreProfiloInteroperabilitaDifferenteServizioBloccaRichiesta()) {
				this.msgDiag.logPersonalizzato("protocolli.tipoSoggetto.fruitore.unsupported.warning");
			}
			else {
				this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL);
				this.msgDiag.logPersonalizzato("protocolli.tipoSoggetto.fruitore.unsupported");
				erroreVerificaTipoByProtocol = ErroriCooperazione.TIPO_MITTENTE_NON_VALIDO.getErroreCooperazione();
			}
		}
		else if(this.idServizio!=null && this.idServizio.getSoggettoErogatore()!=null && this.idServizio.getSoggettoErogatore().getTipo()!=null &&
				!tipiSoggettiSupportatiCanale.contains(this.idServizio.getSoggettoErogatore().getTipo())){
			this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL);
			this.msgDiag.logPersonalizzato("protocolli.tipoSoggetto.erogatore.unsupported");
			erroreVerificaTipoByProtocol = ErroriCooperazione.TIPO_DESTINATARIO_NON_VALIDO.getErroreCooperazione();
		}
		else if(this.idServizio!=null && this.idServizio.getTipo()!=null && 
				!tipiServiziSupportatiCanale.contains(this.idServizio.getTipo())){
			this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL);
			this.msgDiag.logPersonalizzato("protocolli.tipoServizio.unsupported");
			erroreVerificaTipoByProtocol = ErroriCooperazione.TIPO_SERVIZIO_NON_VALIDO.getErroreCooperazione();
		}
		else if(idApplicativoToken!=null && idApplicativoToken.getIdSoggettoProprietario()!=null &&
				idApplicativoToken.getIdSoggettoProprietario().getTipo()!=null && 
				!tipiSoggettiSupportatiCanale.contains(idApplicativoToken.getIdSoggettoProprietario().getTipo())){
			this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL);
			this.msgDiag.logPersonalizzato("protocolli.tipoSoggetto.applicativoToken.unsupported");
			erroreVerificaTipoByProtocol = ErroriCooperazione.TIPO_MITTENTE_NON_VALIDO.getErroreCooperazione();
		}
			
		if(erroreVerificaTipoByProtocol!=null){
			if(this.msgContext.isGestioneRisposta()){

				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);							
				this.parametriGenerazioneBustaErrore.setErroreCooperazione(erroreVerificaTipoByProtocol);

				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreValidazione(this.parametriGenerazioneBustaErrore);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

			}
			this.openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		
		
		
		
		




		/* ------------ Validazione Contenuti Applicativi e Autorizzazione per Contenuto  ------------- */
		ValidazioneContenutiApplicativi validazioneContenutoApplicativoApplicativo = null;
		List<Proprieta> proprietaPorta = null;
		String tipoAutorizzazionePerContenuto = null;
		if(!this.functionAsRouter && 
				!this.isMessaggioErroreProtocollo && 
				this.erroriProcessamento.isEmpty() && this.erroriValidazione.isEmpty() &&
				!this.bustaDiServizio &&
				this.pa!=null){
			this.msgDiag.mediumDebug("Controllo abilitazione validazione XSD della richiesta...");
			boolean isRicevutaAsincronaModalitaAsincrona = false;
			try{


				/* ----------- Identificazione profilo -------------- */
				if(     (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) ||
						org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) &&
						(this.bustaRichiesta.getRiferimentoMessaggio()!=null) )
				{
					this.msgDiag.mediumDebug("Controllo abilitazione validazione XSD della richiesta (check asincrono)...");
					// La validazione non deve essere effettuata se abbiamo una ricevuta asincrona, 'modalita' asincrona'

					if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) {	

						//	Richiesta Asincrona
						if(this.bustaRichiesta.getRiferimentoMessaggio()==null){
							validazioneContenutoApplicativoApplicativo = this.configurazionePdDReader.getTipoValidazioneContenutoApplicativo(this.pa,this.implementazionePdDMittente, true);
							proprietaPorta = this.pa.getProprietaList();
							tipoAutorizzazionePerContenuto = this.configurazionePdDReader.getAutorizzazioneContenuto(this.pa);
						}else{
							//	Risposta Asincrona
							if(RuoloBusta.RISPOSTA.equals(this.ruoloBustaRicevuta.toString())){
								validazioneContenutoApplicativoApplicativo = this.configurazionePdDReader.getTipoValidazioneContenutoApplicativo(this.pd,this.implementazionePdDMittente, true);
								proprietaPorta = this.pd.getProprietaList();
								// NOTA: deve essere registrato un tipo di autorizzazione per contenuto busta uguale al tipo di autorizzazione utilizzato lato servizi applicativi.
								tipoAutorizzazionePerContenuto = this.configurazionePdDReader.getAutorizzazioneContenuto(this.pd);
							}
							//	Ricevuta alla richiesta/risposta.
							else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(this.ruoloBustaRicevuta.toString()) || 
									RuoloBusta.RICEVUTA_RISPOSTA.equals(this.ruoloBustaRicevuta.toString()) ){	
								if( this.configurazionePdDReader.ricevutaAsincronaSimmetricaAbilitata(this.pd))	{
									isRicevutaAsincronaModalitaAsincrona = true;	
								}else{
									validazioneContenutoApplicativoApplicativo = this.configurazionePdDReader.getTipoValidazioneContenutoApplicativo(this.pd,this.implementazionePdDMittente, true);
									proprietaPorta = this.pd.getProprietaList();
									// NOTA: deve essere registrato un tipo di autorizzazione per contenuto busta uguale al tipo di autorizzazione utilizzato lato servizi applicativi.
									tipoAutorizzazionePerContenuto = this.configurazionePdDReader.getAutorizzazioneContenuto(this.pd);
								}
							}
						}
					}
					// Profilo Asincrono Asimmetrico
					else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) {	

						//	Richiesta Asincrona
						if(this.bustaRichiesta.getRiferimentoMessaggio()==null){
							validazioneContenutoApplicativoApplicativo = this.configurazionePdDReader.getTipoValidazioneContenutoApplicativo(this.pa,this.implementazionePdDMittente, true);
							proprietaPorta = this.pa.getProprietaList();
							tipoAutorizzazionePerContenuto = this.configurazionePdDReader.getAutorizzazioneContenuto(this.pa);
						}else{
							//	Risposta Asincrona
							if(RuoloBusta.RISPOSTA.equals(this.ruoloBustaRicevuta.toString())){
								validazioneContenutoApplicativoApplicativo = this.configurazionePdDReader.getTipoValidazioneContenutoApplicativo(this.pa,this.implementazionePdDMittente, true);
								proprietaPorta = this.pa.getProprietaList();
								tipoAutorizzazionePerContenuto = this.configurazionePdDReader.getAutorizzazioneContenuto(this.pa);
							}
							// Ricevuta alla richiesta/risposta.
							else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(this.ruoloBustaRicevuta.toString()) || 
									RuoloBusta.RICEVUTA_RISPOSTA.equals(this.ruoloBustaRicevuta.toString()) ){	
								if( this.configurazionePdDReader.ricevutaAsincronaAsimmetricaAbilitata(this.pd))	{
									isRicevutaAsincronaModalitaAsincrona = true;	
								}else{
									validazioneContenutoApplicativoApplicativo = this.configurazionePdDReader.getTipoValidazioneContenutoApplicativo(this.pd,this.implementazionePdDMittente, true);
									proprietaPorta = this.pd.getProprietaList();
									// NOTA: deve essere registrato un tipo di autorizzazione per contenuto busta uguale al tipo di autorizzazione utilizzato lato servizi applicativi.
									tipoAutorizzazionePerContenuto = this.configurazionePdDReader.getAutorizzazioneContenuto(this.pd);
								}
							}
						}
					}
					// Chiudo eventuali prepared statement, che non voglio eseguire.
					((StateMessage)this.openspcoopstate.getStatoRichiesta()).closePreparedStatement();
				}else{
					this.msgDiag.mediumDebug("Controllo abilitazione validazione dei contenuti applicativi della richiesta...");
					validazioneContenutoApplicativoApplicativo = this.configurazionePdDReader.getTipoValidazioneContenutoApplicativo(this.pa,this.implementazionePdDMittente, true);
					proprietaPorta = this.pa.getProprietaList();
					tipoAutorizzazionePerContenuto = this.configurazionePdDReader.getAutorizzazioneContenuto(this.pa);
				}
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e,"getTipoValidazione/Autorizzazione ContenutoApplicativo");
				if(this.msgContext.isGestioneRisposta()){
					
					this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
					this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
					OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
					this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
				}
				this.openspcoopstate.releaseResource();
				return;
			}
			if(!isRicevutaAsincronaModalitaAsincrona){

				if(validazioneContenutoApplicativoApplicativo!=null && validazioneContenutoApplicativoApplicativo.getTipo()!=null){
					String tipo = ValidatoreMessaggiApplicativi.getTipo(validazioneContenutoApplicativoApplicativo);
					this.msgContext.getIntegrazione().setTipoValidazioneContenuti(tipo);
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_CONTENUTI, tipo);
					this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,"");
				}
				
				// VALIDAZIONE CONTENUTI APPLICATIVI
				
				if(
						(validazioneContenutoApplicativoApplicativo!=null)
						&&
						(
								CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getStato())
								||
								CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())
						)
					){
				
					this.transaction.getTempiElaborazione().startValidazioneRichiesta();
					ByteArrayInputStream binXSD = null;
					try{
												
						this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaInCorso");
						
						boolean readInterface = CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo());
								
						if(ServiceBinding.SOAP.equals(this.requestMessage.getServiceBinding())){
						
							// Accept mtom message
							List<MtomXomReference> xomReferences = null;
							if(StatoFunzionalita.ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getAcceptMtomMessage())){
								this.msgDiag.mediumDebug("Validazione xsd della richiesta (mtomFastUnpackagingForXSDConformance)...");
								if(!ServiceBinding.SOAP.equals(this.requestMessage.getServiceBinding())){
									throw new CoreException("Funzionalita 'AcceptMtomMessage' valida solamente per Service Binding SOAP");
								}
								xomReferences = this.requestMessage.castAsSoap().mtomFastUnpackagingForXSDConformance();
							}
							
							// Init Validatore
							this.msgDiag.mediumDebug("Validazione della richiesta (initValidator)...");
							ValidatoreMessaggiApplicativi validatoreMessaggiApplicativi = 
								new ValidatoreMessaggiApplicativi(this.registroServiziReader,this.idServizio,this.requestMessage,readInterface,
										propertiesReader.isValidazioneContenutiApplicativiRpcLiteralXsiTypeGestione(),
										proprietaPorta,
										this.pddContext);
	
							// Validazione WSDL 
							if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo()) 
									||
									CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo())
							){
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
									propertiesReader.isValidazioneContenutiApplicativiRpcLiteralXsiTypeGestione() &&
									propertiesReader.isValidazioneContenutiApplicativiRpcLiteralXsiTypeRipulituraDopoValidazione()
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
								new ValidatoreMessaggiApplicativiRest(this.registroServiziReader, this.idServizio, this.requestMessage, readInterface, proprietaPorta,
										this.protocolFactory, this.pddContext);
							
							if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.equals(validazioneContenutoApplicativoApplicativo.getTipo()) &&
									this.requestMessage.castAsRest().hasContent()) {
								
								// Validazione XSD
								this.msgDiag.mediumDebug("Validazione xsd della richiesta ...");
								validatoreMessaggiApplicativi.validateWithSchemiXSD(true);
								
							}
							else {
								
								// Validazione Interface
								validatoreMessaggiApplicativi.validateRequestWithInterface(true);
								
							}
							
						}
						
						this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaEffettuata");
	
					}catch(ValidatoreMessaggiApplicativiException ex){
						this.msgDiag.addKeywordErroreProcessamento(ex);
						logError(this.logCore, "[ValidazioneContenutiApplicativi Richiesta] "+ex.getMessage(),ex);
						if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
							this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita.warningOnly");
						}
						else {
							this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita");
						}
						if(!CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())){
							
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RICHIESTA, "true");
							
							// validazione abilitata
							if(this.msgContext.isGestioneRisposta()){
								
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
								this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
								this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
								this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ex.getErrore());
								OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,ex);
								
								// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
								this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
								this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
								this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
							}
							this.openspcoopstate.releaseResource();
							return;
						}
					}catch(Exception ex){
						this.msgDiag.addKeywordErroreProcessamento(ex);
						logError(this.logCore, "Riscontrato errore durante la validazione dei contenuti applicativi (richiesta applicativa)",ex);
						if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
							this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita.warningOnly");
						}
						else {
							this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita");
						}
						if(!CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())){
							
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RICHIESTA, "true");
							
							// validazione abilitata
							if(this.msgContext.isGestioneRisposta()){
								
								this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
								this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA));
	
								OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,ex);
								
								// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
								this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
								this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
								this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
							}
							this.openspcoopstate.releaseResource();
							return;
						}
					}finally{
						this.transaction.getTempiElaborazione().endValidazioneRichiesta();
						if(binXSD!=null){
							try{
								binXSD.close();
							}catch(Exception e){
								// ignore
							}
						}
					}
				}
				else{
					this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaDisabilitata");
				}
			}
			
			
			
			// AUTORIZZAZIONE PER CONTENUTO
			this.msgContext.getIntegrazione().setTipoAutorizzazioneContenuto(tipoAutorizzazionePerContenuto);
			if(tipoAutorizzazionePerContenuto!=null){
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE_CONTENUTO, tipoAutorizzazionePerContenuto);
			}
			if (!CostantiConfigurazione.AUTORIZZAZIONE_NONE.equalsIgnoreCase(tipoAutorizzazionePerContenuto)) {
				
				this.transaction.getTempiElaborazione().startAutorizzazioneContenuti();
				try {
					
					String identitaMittente = null;
					if(credenziali!=null && !soggettoAutenticato &&
						!"".equals(credenziali.toString())){
						identitaMittente = credenziali.toString();
						if(identitaMittente.endsWith(" ")){
							identitaMittente = identitaMittente.substring(0, identitaMittente.length()-1);	
						}
					}
					String subjectMessageSecurity = null;
					if(this.messageSecurityContext!=null){
						subjectMessageSecurity = this.messageSecurityContext.getSubject();
					}
					
					IDServizio idServizioPerAutorizzazione = getIdServizioPerAutorizzazione(this.idServizio, this.soggettoFruitore, this.functionAsRouter, this.bustaRichiesta, this.ruoloBustaRicevuta); 
					IDSoggetto idSoggettoMittentePerAutorizzazione = getIDSoggettoMittentePerAutorizzazione(this.idServizio, this.soggettoFruitore, this.functionAsRouter, this.bustaRichiesta, this.ruoloBustaRicevuta, supportatoAutenticazioneSoggetti);
					Soggetto soggettoMittentePerAutorizzazione = null;
					if(idSoggettoMittentePerAutorizzazione!=null){
						soggettoMittentePerAutorizzazione = this.registroServiziReader.getSoggetto(idSoggettoMittentePerAutorizzazione, null, requestInfo);
					}
					
					String tipoMessaggio = "messaggio";
					if(RuoloBusta.RICEVUTA_RICHIESTA.equals(this.ruoloBustaRicevuta.toString()) || RuoloBusta.RICEVUTA_RISPOSTA.equals(this.ruoloBustaRicevuta.toString())){
						tipoMessaggio = "ricevuta asincrona";
					}
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA, tipoMessaggio);
					if(idSoggettoMittentePerAutorizzazione!=null){
						this.msgDiag.addKeyword(CostantiPdD.KEY_MITTENTE_E_SERVIZIO_DA_AUTORIZZARE, "fruitore ["+idSoggettoMittentePerAutorizzazione.toString()+"] -> servizio ["+idServizioPerAutorizzazione.toString()+"]");
					}
					else{
						this.msgDiag.addKeyword(CostantiPdD.KEY_MITTENTE_E_SERVIZIO_DA_AUTORIZZARE, "servizio ["+idServizioPerAutorizzazione.toString()+"]");
					}
					if(identitaMittente!=null)
						this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, " credenzialiMittente "+identitaMittente);
					else
						this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, "");
					if(servizioApplicativoFruitore!=null)
						this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE_MSG, " identitaServizioApplicativoFruitore ["+servizioApplicativoFruitore+"]");
					else
						this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE_MSG, "");
					if(subjectMessageSecurity!=null)
						this.msgDiag.addKeyword(CostantiPdD.KEY_SUBJECT_MESSAGE_SECURITY_MSG, " subjectMessageSecurity ["+subjectMessageSecurity+"]");
					else
						this.msgDiag.addKeyword(CostantiPdD.KEY_SUBJECT_MESSAGE_SECURITY_MSG, "");
					this.msgDiag.logPersonalizzato("autorizzazioneContenutiBusteInCorso");
					
					if(datiInvocazione==null){
						
						IDServizioApplicativo identitaServizioApplicativoFruitore = new IDServizioApplicativo();
						identitaServizioApplicativoFruitore.setNome(servizioApplicativoFruitore);
						identitaServizioApplicativoFruitore.setIdSoggettoProprietario(idSoggettoMittentePerAutorizzazione);
						
						datiInvocazione = new DatiInvocazionePortaApplicativa();
						datiInvocazione.setBusta(this.bustaRichiesta);
						datiInvocazione.setToken(token);
						datiInvocazione.setPddContext(this.pddContext);
						datiInvocazione.setInfoConnettoreIngresso(this.inRequestContext.getConnettore());
						datiInvocazione.setIdServizio(idServizioPerAutorizzazione);
						datiInvocazione.setState(this.openspcoopstate.getStatoRichiesta());
						datiInvocazione.setRequestInfo(requestInfo);
						datiInvocazione.setCredenzialiPdDMittente(credenziali);
						datiInvocazione.setIdentitaServizioApplicativoFruitore(identitaServizioApplicativoFruitore);
						datiInvocazione.setSubjectServizioApplicativoFruitoreFromMessageSecurityHeader(subjectMessageSecurity);
						datiInvocazione.setIdPA(idPA);
						datiInvocazione.setPa(this.pa);
						datiInvocazione.setIdPD(idPD);
						datiInvocazione.setPd(this.pd);
						datiInvocazione.setIdSoggettoFruitore(idSoggettoMittentePerAutorizzazione);
						datiInvocazione.setSoggettoFruitore(soggettoMittentePerAutorizzazione);
						datiInvocazione.setRuoloBusta(this.ruoloBustaRicevuta);
					}
					
					// Controllo Autorizzazione
					EsitoAutorizzazionePortaApplicativa esito = 
							GestoreAutorizzazione.verificaAutorizzazioneContenutoPortaApplicativa(tipoAutorizzazionePerContenuto, datiInvocazione, this.pddContext, this.protocolFactory, this.requestMessage, this.logCore);
					CostantiPdD.addKeywordInCache(this.msgDiag, esito.isEsitoPresenteInCache(),
							this.pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE_CONTENUTI);
					if(esito.getDetails()==null){
						this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}else{
						this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " ("+esito.getDetails()+")");
					}
					if(!esito.isAutorizzato()){
						this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTORIZZAZIONE, "true");
						try{
							this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, esito.getErroreCooperazione().getDescrizione(this.protocolFactory));
						}catch(Exception e){
							logError(this.logCore, "getDescrizione Error:"+e.getMessage(),e);
						}
						this.msgDiag.addKeyword(CostantiPdD.KEY_POSIZIONE_ERRORE, this.traduttore.toString(esito.getErroreCooperazione().getCodiceErrore()));
						this.msgDiag.logPersonalizzato("autorizzazioneContenutiBusteFallita");
						if(this.msgContext.isGestioneRisposta()){
							OpenSPCoop2Message errorMsg = null;
							this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
							this.parametriGenerazioneBustaErrore.setErroreCooperazione(esito.getErroreCooperazione());
							IntegrationFunctionError integrationFunctionError = esito.getIntegrationFunctionError();
							if(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.equals(esito.getErroreCooperazione().getCodiceErrore()) 
									|| CodiceErroreCooperazione.SICUREZZA_FALSIFICAZIONE_MITTENTE.equals(esito.getErroreCooperazione().getCodiceErrore())){
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.CONTENT_AUTHORIZATION_DENY;
								}
								this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
								errorMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreValidazione(this.parametriGenerazioneBustaErrore);
							}
							else{
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
								}
								this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
								errorMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,esito.getEccezioneProcessamento());
							}

							// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
							this.parametriInvioBustaErrore.setOpenspcoopMsg(errorMsg);
							this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
							this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
						}
						
						this.openspcoopstate.releaseResource();
						return;
					}else{
						this.msgDiag.logPersonalizzato("autorizzazioneContenutiBusteEffettuata");
					}

				}catch(Exception ex){
					CostantiPdD.addKeywordInCache(this.msgDiag, false,
							this.pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE_CONTENUTI);
					this.msgDiag.logErroreGenerico(ex,"AutorizzazioneContenuto Messaggio("+this.bustaRichiesta.getID()+")");
					logError(this.logCore, "Riscontrato errore durante il processo di Autorizzazione del Contenuto per il messaggio con identificativo ["+this.bustaRichiesta.getID()+"]",ex);
					if(this.msgContext.isGestioneRisposta()){
						
						this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
						this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_542_AUTORIZZAZIONE_CONTENUTO));
						OpenSPCoop2Message errorMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,ex);

						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
						this.parametriInvioBustaErrore.setOpenspcoopMsg(errorMsg);
						this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
						this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
					}
					this.openspcoopstate.releaseResource();
					return;
				}
				finally {
					this.transaction.getTempiElaborazione().endAutorizzazioneContenuti();
				}
			}
			else{
				this.msgDiag.logPersonalizzato("autorizzazioneContenutiBusteDisabilitata");
			}
		}



		







		Utilities.printFreeMemory("RicezioneBuste - Recupero configurazione per salvataggio risposta in cache ...");
		this.msgDiag.mediumDebug("Recupero configurazione per salvataggio risposta in cache ...");
		try{
			ResponseCachingConfigurazione responseCachingConfig = null;
			if(this.pa!=null) {
				responseCachingConfig = this.configurazionePdDReader.getConfigurazioneResponseCaching(this.pa);
			}
			else {
				responseCachingConfig = this.configurazionePdDReader.getConfigurazioneResponseCaching();
			}
			if(responseCachingConfig!=null && StatoFunzionalita.ABILITATO.equals(responseCachingConfig.getStato())) {
				
				this.transaction.getTempiElaborazione().startResponseCachingCalcoloDigest();
				try {
				
					this.msgDiag.mediumDebug("Calcolo digest per salvataggio risposta ...");
					
					HashGenerator hashGenerator = new HashGenerator(propertiesReader.getCachingResponseDigestAlgorithm());
					String digest = hashGenerator.buildKeyCache(this.requestMessage, requestInfo, responseCachingConfig);
					this.requestMessage.addContextProperty(CostantiPdD.RESPONSE_CACHE_REQUEST_DIGEST, digest);
				
				}finally {
					this.transaction.getTempiElaborazione().endResponseCachingCalcoloDigest();
				}
			}
		} catch (Exception e){
			this.msgDiag.logErroreGenerico(e,"calcoloDigestSalvataggioRisposta");
			logError(this.logCore, "Calcolo Digest Salvataggio Risposta non riuscito: "	+ e);
			if (this.msgContext.isGestioneRisposta()) {
				
				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_561_DIGEST_REQUEST));

				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
			}
			this.openspcoopstate.releaseResource();
			return;
		}
		
		
		
		





		/* ------------- Modalita' di gestione ---------------------------- */
		try {
			if ( this.oneWayStateless || this.sincronoStateless || this.asincronoStateless || this.oneWayVersione11 || this.routingStateless ) {
				this.openspcoopstate = OpenSPCoopState.toStateless(((OpenSPCoopStateful)this.openspcoopstate), true);
				this.parametriGenerazioneBustaErrore.setOpenspcoop(this.openspcoopstate);
				this.parametriInvioBustaErrore.setOpenspcoop(this.openspcoopstate);
			}
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"OpenSPCoopState.toStateless");
			logError(this.logCore, "Creazione stato STATEFUL/STATELESS non riuscita: "	+ e);
			if (this.msgContext.isGestioneRisposta()) {
				
				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));

				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
			}
			this.openspcoopstate.releaseResource();
			return;
		}
















		/* -------- Gestione Richiesta ---------- */
		String tipoMsg = Costanti.INBOX;
		if(this.functionAsRouter)
			tipoMsg = Costanti.OUTBOX;
		this.msgRequest = new GestoreMessaggi(this.openspcoopstate, true, this.idMessageRequest, tipoMsg,this.msgDiag, this.inRequestContext.getPddContext());
		this.msgRequest.setOneWayVersione11(this.oneWayVersione11);
		this.msgRequest.setRoutingStateless(this.routingStateless);
		this.repositoryBuste = new RepositoryBuste(this.openspcoopstate.getStatoRichiesta(), true, this.protocolFactory);














		/* ------------
	   Controllo di non aver gia' registrato la richiesta 
	   (se non l'ho registrata, non significa che non l'abbia gia' ricevuta, puo' darsi che l'abbia ricevuta
	   e gia' completata di elaborare. 
	   Il controllo dell'history in Sbustamento effettuera' poi questo controllo e generera' un eventuale riscontro)
	   L'unico caso in cui posso ricevere piu' di una copia di una busta dovrebbe avvenire con profili asincroni in modalita asincrona
	   e con profili oneWay. In questo caso se ricevo una busta che sto gia' processando gestisco il caso particolare.
	   Negli altri casi emetto anche un messaggio Errore di msg gia ricevuto.
	   ------------
		 */
		this.msgDiag.mediumDebug("Controllo presenza del messaggio gia' in gestione...");
		if( (!this.functionAsRouter) || (!this.routingStateless) ){
			try{
				if(  this.msgRequest.existsMessage_noCache() ){

					// Se il proprietario attuale e' GestoreMessaggi, forzo l'eliminazione e continuo a processare il messaggio.
					String proprietarioMessaggio = this.msgRequest.getProprietario(this.msgContext.getIdModulo());
					if(TimerGestoreMessaggi.ID_MODULO.equals(proprietarioMessaggio)){
						this.msgDiag.logPersonalizzato("messaggioInGestione.marcatoDaEliminare");
						String msg = this.msgDiag.getMessaggio_replaceKeywords("messaggioInGestione.marcatoDaEliminare");
						if(propertiesReader.isMsgGiaInProcessamentoUseLock()) {
							this.msgRequest._deleteMessageWithLock(msg,propertiesReader.getMsgGiaInProcessamentoAttesaAttiva(),
								propertiesReader.getMsgGiaInProcessamentoCheckInterval());
						}
						else {
							this.msgRequest.deleteMessageByNow();
						}
					}

					// Altrimenti gestisco il duplicato
					else{

						boolean rispostaModalitaSincrona = org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) ||
						(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) && this.configurazionePdDReader.ricevutaAsincronaSimmetricaAbilitata(this.pa)) ||
						(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) && this.configurazionePdDReader.ricevutaAsincronaAsimmetricaAbilitata(this.pa));

						// Se la modalita e' sincrona non deve essere possibile ricevere una busta due volte con lo stesso identificativo
						if(rispostaModalitaSincrona){
							this.msgDiag.addKeyword(CostantiPdD.KEY_PROPRIETARIO_MESSAGGIO, proprietarioMessaggio);
							this.msgDiag.logPersonalizzato("messaggioInGestione.gestioneSincrona");
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.RICHIESTA_DUPLICATA, "true");
							if(this.msgContext.isGestioneRisposta()){
								
								this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
								this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_537_BUSTA_GIA_RICEVUTA.get537_BustaGiaRicevuta(this.idMessageRequest));
								this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.CONFLICT_IN_QUEUE);
								OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,null);
								
								// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
								this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
								this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
								this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
							}
							this.openspcoopstate.releaseResource();
							return;
						}

						else{
							// gestione risposta gia presente
							// se profiloTrasmissione=ALPIUUNAVOLTA
							//     Se il proprietario attuale e' il modulo consegna contenuti applicativi posso ritornare un soap vuoto, (o riscontro/ricevuta a seconda del profilo)
							//     Altrimenti provo ad aspettare un tempo ragionevole e ogni tanto rileggo il proprietario. Non appena raggiunge il modulo consegna contenuti applicativi
							//     posso ritornare un soap vuoto (o riscontro/ricevuta a seconda del profilo)
							// altrimenti provo ad aspettare che il messaggio non esista piu' o abbia cmq come proprietario GestoreMessaggi per poi eliminarlo.
							// 
							// Se scade il timeout cmq genero un errore di busta gia' in gestione.
							this.msgDiag.addKeyword(CostantiPdD.KEY_PROPRIETARIO_MESSAGGIO, proprietarioMessaggio);
							this.msgDiag.logPersonalizzato("messaggioInGestione.gestioneAsincrona");
							long scadenzaWhile = DateManager.getTimeMillis() + propertiesReader.getMsgGiaInProcessamentoAttesaAttiva();
							boolean isErroreMsgGiaRicevuto = true;
							boolean msgAttesaFineProcessamento = false;
							int millisecondiTrascorsi = 0;
							while( DateManager.getTimeMillis() < scadenzaWhile  ){

								proprietarioMessaggio = this.msgRequest.getProprietario(this.msgContext.getIdModulo());

								if( (Inoltro.SENZA_DUPLICATI.equals(this.bustaRichiesta.getInoltro())) || 
										(this.msgContext.isForzaFiltroDuplicati_msgGiaInProcessamento())  ){

									// Se sono entrato in questo controllo vuole dire che prima esisteva un msg che aveva come proprietario Sbustamento o RicezioneBuste.
									// Quindi se ho raggiunto ConsegnaContenutiApplicativi, GestoreMessaggi o non esiste piu' il messaggio, vuole dire che e' stato elaborato.
									if(ConsegnaContenutiApplicativi.ID_MODULO.equals(proprietarioMessaggio) || 
											TimerGestoreMessaggi.ID_MODULO.equals(proprietarioMessaggio) ||
											(!this.msgRequest.existsMessage_noCache()) ){
										
										this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.RICHIESTA_DUPLICATA, "true");
										
										if(this.msgContext.isGestioneRisposta()){
											this.msgContext.setMessageResponse(this.ricezioneBusteGeneratoreBustaErrore.generaRispostaMsgGiaRicevuto(!this.msgContext.isForzaFiltroDuplicati_msgGiaInProcessamento(),
													this.bustaRichiesta,this.infoIntegrazione, this.msgDiag, this.openspcoopstate, this.logCore, propertiesReader,
													this.versioneProtocollo,this.ruoloBustaRicevuta,this.implementazionePdDMittente,this.protocolFactory,
													this.identitaPdD,this.idTransazione,loader,this.oneWayVersione11,this.implementazionePdDMittente,
													this.tracciamento,
													this.correlazioneApplicativa,
													this.pddContext, IntegrationFunctionError.CONFLICT));
										}
										this.openspcoopstate.releaseResource();
										return;
									}

									if(!msgAttesaFineProcessamento){
										this.msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, ""+(propertiesReader.getMsgGiaInProcessamentoAttesaAttiva()/1000));
										this.msgDiag.logPersonalizzato("messaggioInGestione.attesaFineProcessamento.filtroDuplicatiAbilitato");
										msgAttesaFineProcessamento = true;
									}

								}else{

									if(!msgAttesaFineProcessamento){
										this.msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, ""+(propertiesReader.getMsgGiaInProcessamentoAttesaAttiva()/1000));
										this.msgDiag.logPersonalizzato("messaggioInGestione.attesaFineProcessamento.filtroDuplicatiDisabilitato");
										msgAttesaFineProcessamento = true;
									}

									boolean existsMessage = this.msgRequest.existsMessage_noCache();
									if(!existsMessage){
										this.msgDiag.logPersonalizzato("messaggioInGestione.attesaFineProcessamento.filtroDuplicatiDisabilitato.attesaTerminata");
										isErroreMsgGiaRicevuto = false;
										break;
									}

									if(TimerGestoreMessaggi.ID_MODULO.equals(proprietarioMessaggio)){
										this.msgDiag.logPersonalizzato("messaggioInGestione.attesaFineProcessamento.filtroDuplicatiDisabilitato.forzoEliminazione");
										String msg = this.msgDiag.getMessaggio_replaceKeywords("messaggioInGestione.attesaFineProcessamento.filtroDuplicatiDisabilitato.forzoEliminazione");
										if(propertiesReader.isMsgGiaInProcessamentoUseLock()) {
											this.msgRequest._deleteMessageWithLock(msg,propertiesReader.getMsgGiaInProcessamentoAttesaAttiva()-millisecondiTrascorsi,
													propertiesReader.getMsgGiaInProcessamentoCheckInterval());
										}
										else {
											this.msgRequest.deleteMessageByNow();
										}
										isErroreMsgGiaRicevuto = false;
										break;
									}

								}

								Utilities.sleep(propertiesReader.getMsgGiaInProcessamentoCheckInterval());
								millisecondiTrascorsi = millisecondiTrascorsi + propertiesReader.getMsgGiaInProcessamentoCheckInterval();

							}
							if(isErroreMsgGiaRicevuto){
								this.msgDiag.logPersonalizzato("messaggioInGestione.attesaFineProcessamento.timeoutScaduto");
								this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.RICHIESTA_DUPLICATA, "true");
								if(this.msgContext.isGestioneRisposta()){
									
									this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
									this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_537_BUSTA_GIA_RICEVUTA.get537_BustaGiaRicevuta(this.idMessageRequest));
									this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.CONFLICT_IN_QUEUE);
									OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,null);
									
									// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
									this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
									this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
									this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore,false);
								}
								this.openspcoopstate.releaseResource();
								return;
							}
						}
					}
				}
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e,"ControlloPresenzaMessaggioGiaInGestione");
				logError(this.logCore, "Controllo/gestione presenza messaggio gia in gestione non riuscito",e);
				if(this.msgContext.isGestioneRisposta()){
					
					this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
					this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));

					OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
					this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
				}
				this.openspcoopstate.releaseResource();
				return;
			}
		}





















		/* ----------------   Creo sessione di gestione del messaggio ricevuto  --------------------- */
		this.msgDiag.mediumDebug("Registrazione messaggio di richiesta nel RepositoryMessaggi...");
		try{
			this.msgRequest.registraMessaggio(this.requestMessage,dataIngressoRichiesta, 
					(this.oneWayStateless || this.sincronoStateless || this.asincronoStateless || this.routingStateless),
					this.correlazioneApplicativa);
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e,"msgRequest.registraMessaggio");
			if(this.msgContext.isGestioneRisposta()){
				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_508_SAVE_REQUEST_MSG));

				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
			}
			this.msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata su fileSystem
			this.openspcoopstate.releaseResource();
			return;
		}












		/* ----------------   Salvo busta ricevuta (se non l'ho gia' ricevuta)   --------------------- */
		this.msgDiag.mediumDebug("Registrazione busta di richiesta nel RepositoryBuste...");
		try{
			if( (!this.oneWayStateless) && (!this.sincronoStateless) && (!this.asincronoStateless)  && (!this.routingStateless) ){
				if(this.repositoryBuste.isRegistrata(this.bustaRichiesta.getID(),tipoMsg)){
					try{
						this.repositoryBuste.aggiornaBusta(this.bustaRichiesta,tipoMsg,propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),this.erroriValidazione);
						this.repositoryBuste.impostaUtilizzoPdD(this.bustaRichiesta.getID(),tipoMsg);
					}catch(Exception e){
						if(propertiesReader.isMsgGiaInProcessamentoUseLock()) {
							String causa = "Aggiornamento dati busta con id ["+this.bustaRichiesta.getID()+"] tipo["+tipoMsg+"] non riuscito: "+e.getMessage();
							try{
								GestoreMessaggi.acquireLock(this.msgRequest,TimerLock.newInstance(TipoLock._getLockGestioneRepositoryMessaggi()), this.msgDiag, causa, propertiesReader.getMsgGiaInProcessamentoAttesaAttiva(), propertiesReader.getMsgGiaInProcessamentoCheckInterval());
								// errore che puo' avvenire a causa del Timer delle Buste (vedi spiegazione in classe GestoreMessaggi.deleteMessageWithLock)
								// Si riesegue tutto il codice isRegistrata e update o create con il lock. Stavolta se avviene un errore non e' dovuto al timer.
								if(this.repositoryBuste.isRegistrata(this.bustaRichiesta.getID(),tipoMsg)){
									this.repositoryBuste.aggiornaBusta(this.bustaRichiesta,tipoMsg,propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),this.erroriValidazione);
									this.repositoryBuste.impostaUtilizzoPdD(this.bustaRichiesta.getID(),tipoMsg);
								}
								else{
									this.repositoryBuste.registraBusta(this.bustaRichiesta, tipoMsg, this.erroriValidazione, propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
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

				}
				else{
					this.repositoryBuste.registraBusta(this.bustaRichiesta, tipoMsg, this.erroriValidazione, propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
				}
				this.repositoryBuste.aggiornaInfoIntegrazione(this.bustaRichiesta.getID(),tipoMsg,this.infoIntegrazione);
			}else{
				((StatelessMessage)this.openspcoopstate.getStatoRichiesta()).setBusta(this.bustaRichiesta);
			}
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e,"repositoryBuste.registraBusta");
			if(this.msgContext.isGestioneRisposta()){
				
				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO));

				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);	
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
			}
			this.msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata su fileSystem
			this.openspcoopstate.releaseResource();
			return;
		}




		

		
		
		
		






		/* ------------  Forward a Sbustamento o InoltroBuste (a seconda se il modulo assume funzione di Router) ------------- */
		try{
			if(this.functionAsRouter){ // solo stateful
				this.msgDiag.mediumDebug("Invio messaggio al modulo di InoltroBuste (router)...");
				if (this.msgRequest != null)
					this.msgRequest.aggiornaProprietarioMessaggio(org.openspcoop2.pdd.mdb.InoltroBuste.ID_MODULO);
				// Creazione InoltroBuste
				this.msgDiag.highDebug("Creazione ObjectMessage for send nell'infrastruttura.");
				inoltroMSG.setBusta(this.bustaRichiesta);
				RichiestaDelegata rd = new RichiestaDelegata(this.soggettoFruitore);
				rd.setDominio(this.identitaPdD);
				rd.setIdModuloInAttesa(this.msgContext.getIdModulo());
				rd.setIdServizio(this.idServizio);
				rd.setIdCorrelazioneApplicativa(this.correlazioneApplicativa);
				rd.setServizioApplicativo(servizioApplicativoFruitore);
				rd.setIntegrazione(this.msgContext.getIntegrazione());
				rd.setProtocol(this.msgContext.getProtocol());
				inoltroMSG.setRichiestaDelegata(rd);
				inoltroMSG.setImplementazionePdDSoggettoMittente(this.implementazionePdDMittente);
				inoltroMSG.setImplementazionePdDSoggettoDestinatario(implementazionePdDDestinatario);
				inoltroMSG.setPddContext(this.inRequestContext.getPddContext());

				// send jms solo x il comportamento stateful
				if (!this.routingStateless) {
					
					String classTypeNodeSender = null;
					INodeSender nodeSender = null;
					try{
						classTypeNodeSender = className.getNodeSender(propertiesReader.getNodeSender());
						nodeSender = (INodeSender) loader.newInstance(classTypeNodeSender);
						AbstractCore.init(nodeSender, this.pddContext, this.protocolFactory);
					}catch(Exception e){
						throw new CoreException("Riscontrato errore durante il caricamento della classe (INodeSender) ["+classTypeNodeSender+
								"] per la spedizione nell'infrastruttura: "+e.getMessage());
					}
					
					nodeSender.send(inoltroMSG, org.openspcoop2.pdd.mdb.InoltroBuste.ID_MODULO, this.msgDiag, 
							this.identitaPdD,this.msgContext.getIdModulo(), this.idMessageRequest, this.msgRequest);
				}
			}else{
				this.msgDiag.mediumDebug("Invio messaggio al modulo di Sbustamento...");
				this.msgRequest.aggiornaProprietarioMessaggio(org.openspcoop2.pdd.mdb.Sbustamento.ID_MODULO);

				// set tipologia di filtro duplicati
				if(proprietaPorta!=null && !proprietaPorta.isEmpty()) {
					boolean filtroDuplicatiTestEnabled = CostantiProprieta.isFiltroDuplicatiTestEnabled(proprietaPorta, false); // filtro duplicati usato per test
					if(filtroDuplicatiTestEnabled) {
						this.pddContext.addObject(CostantiPdD.FILTRO_DUPLICATI_TEST, filtroDuplicatiTestEnabled);
					}
				}
				
				// setto parametri SbustamentoMessage
				this.msgDiag.highDebug("Creazione ObjectMessage for send nell'infrastruttura.");
				richiestaApplicativa.setIdCorrelazioneApplicativa(this.correlazioneApplicativa);
				richiestaApplicativa.setIdentitaServizioApplicativoFruitore(servizioApplicativoFruitore);
				this.sbustamentoMSG.setRichiestaApplicativa(richiestaApplicativa);
				this.sbustamentoMSG.setBusta(this.bustaRichiesta);
				this.sbustamentoMSG.setErrors(this.erroriValidazione, integrationFunctionErrorValidazione);
				this.sbustamentoMSG.setMessaggioErroreProtocollo(this.isMessaggioErroreProtocollo);
				this.sbustamentoMSG.setIsBustaDiServizio(this.bustaDiServizio);
				this.sbustamentoMSG.setServizioCorrelato(validatore.getServizioCorrelato());
				this.sbustamentoMSG.setTipoServizioCorrelato(validatore.getTipoServizioCorrelato());
				this.sbustamentoMSG.setVersioneServizioCorrelato(validatore.getVersioneServizioCorrelato());
				this.sbustamentoMSG.setRuoloBustaRicevuta(this.ruoloBustaRicevuta);
				this.sbustamentoMSG.setOneWayVersione11(this.oneWayVersione11);
				this.sbustamentoMSG.setStateless((this.oneWayStateless || this.sincronoStateless || this.asincronoStateless));
				this.sbustamentoMSG.setImplementazionePdDSoggettoMittente(this.implementazionePdDMittente);
				this.sbustamentoMSG.setImplementazionePdDSoggettoDestinatario(implementazionePdDDestinatario);
				this.sbustamentoMSG.setPddContext(this.inRequestContext.getPddContext());
				this.sbustamentoMSG.setDettaglioEccezione(dettaglioEccezione);
				
				if(validatore.getInfoServizio()!=null){
					this.sbustamentoMSG.setFiltroDuplicatiRichiestoAccordo(Inoltro.SENZA_DUPLICATI.equals(validatore.getInfoServizio().getInoltro()));
					if(StatoFunzionalitaProtocollo.REGISTRO.equals(this.moduleManager.getConsegnaAffidabile(this.bustaRichiesta)))
						this.sbustamentoMSG.setConfermaRicezioneRichiestoAccordo(validatore.getInfoServizio().getConfermaRicezione());
					if(StatoFunzionalitaProtocollo.REGISTRO.equals(this.moduleManager.getConsegnaInOrdine(this.bustaRichiesta)))	
						this.sbustamentoMSG.setConsegnaOrdineRichiestoAccordo(validatore.getInfoServizio().getOrdineConsegna());
				}

				// send jms solo x il comportamento stateful
				if (!this.portaStateless) {
					logDebug(this.logCore, RicezioneBuste.ID_MODULO + " :eseguo send a sbustamento");
					
					String classTypeNodeSender = null;
					INodeSender nodeSender = null;
					try{
						classTypeNodeSender = className.getNodeSender(propertiesReader.getNodeSender());
						nodeSender = (INodeSender) loader.newInstance(classTypeNodeSender);
						AbstractCore.init(nodeSender, this.pddContext, this.protocolFactory);
					}catch(Exception e){
						throw new CoreException("Riscontrato errore durante il caricamento della classe ["+classTypeNodeSender+
								"] da utilizzare per la spedizione nell'infrastruttura: "+e.getMessage());
					}
					
					nodeSender.send(this.sbustamentoMSG, org.openspcoop2.pdd.mdb.Sbustamento.ID_MODULO, this.msgDiag, 
							this.identitaPdD,this.msgContext.getIdModulo(), this.idMessageRequest, this.msgRequest);
					logDebug(this.logCore, RicezioneBuste.ID_MODULO + " :send a sbustamento eseguita");
				}
			}

		} catch (Exception e) {	
			if(this.functionAsRouter){
				logError(this.logCore, "Spedizione->InoltroBuste(router) non riuscita",e);
				this.msgDiag.logErroreGenerico(e,"GenericLib.nodeSender.send(InoltroBuste)");
			}else{
				logError(this.logCore, "Spedizione->Sbustamento non riuscita",e);
				this.msgDiag.logErroreGenerico(e,"GenericLib.nodeSender.send(Sbustamento)");
			}
			if(this.msgContext.isGestioneRisposta()){
				
				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_512_SEND));
				
				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
			}
			this.msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata su fileSystem
			this.openspcoopstate.releaseResource();
			return;
		}






		/* ------------  Commit connessione al DB (RichiestaSalvata) ------------- */
		this.msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta...");
		try{
			this.openspcoopstate.commit();
			logDebug(this.logCore, RicezioneBuste.ID_MODULO+ " :RicezioneBuste commit eseguito");
		}catch (Exception e) {	
			this.msgDiag.logErroreGenerico(e,"openspcoopstate.commit()");
			if(this.msgContext.isGestioneRisposta()){
				
				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_506_COMMIT_JDBC));

				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
			}
			this.msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata 
			this.openspcoopstate.releaseResource();
			return;
		}

		// *** GB ***
		if(validatore!=null){
			if(validatore.getValidatoreSintattico()!=null){
				validatore.getValidatoreSintattico().setHeaderSOAP(null);
			}
			validatore.setValidatoreSintattico(null);
		}
		validatore = null;
		// *** GB ***
		
		if ( !this.portaStateless && !this.routingStateless  ) {

			// Aggiornamento cache messaggio
			if(this.msgRequest!=null)
				this.msgRequest.addMessaggiIntoCache_readFromTable(RicezioneBuste.ID_MODULO, "richiesta");

			// Aggiornamento cache proprietario messaggio
			if(this.msgRequest!=null)
				this.msgRequest.addProprietariIntoCache_readFromTable(RicezioneBuste.ID_MODULO, "richiesta",null,this.functionAsRouter);

			// Rilascia connessione al DB 
			this.msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta effettuato, rilascio della connessione...");
			this.openspcoopstate.releaseResource();
		}




		
		
		
		/* FIX bug riferimentoMessaggio errato, contiene un id generato nel flusso di risposta (e poi non usato in seguito a errori) invece di quello della richiesta */
		this.bustaRichiesta = this.bustaRichiesta.newInstance(); // Per evitare che mi venga modificato da ImbustamentoRisposte (Non ho capito il motivo)
		
		
		
		
		


		/* ------------------------------ STATELESS  ROUTING -------------------------------- */
		if(this.routingStateless){
			this.openspcoopstate.setMessageLib(inoltroMSG);
			this.openspcoopstate.setIDMessaggioSessione(this.idMessageRequest);

			// Durante le invocazioni non deve essere utilizzata la connessione al database 
			this.openspcoopstate.setUseConnection(false);

			// InoltroBuste 
			InoltroBuste lib = null;
			try{
				lib = new InoltroBuste(this.logCore);
				EsitoLib esito = lib.onMessage(this.openspcoopstate);

				if ( esito.getStatoInvocazione() == EsitoLib.OK ||
						esito.getStatoInvocazione() == EsitoLib.ERRORE_GESTITO ){
					this.msgDiag.mediumDebug("Invocazione libreria InoltroBuste riuscito con esito: "+esito.getStatoInvocazione());
				}
				else if (esito.getStatoInvocazione() == EsitoLib.ERRORE_NON_GESTITO ) {
					throw new CoreException("Errore non gestito dalla libreria");
				}
				/**else {
					throw new Exception("Esito libreria sconosciuto");
				}*/
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e,"GestioneRoutingStateless");
				logError(this.logCore, "Errore Generale durante la gestione del routing stateless: "+e.getMessage(),e);

				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						getErroreIntegrazione());

				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.openspcoopstate.setUseConnection(true);
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
				this.openspcoopstate.releaseResource();
				return;
			}

			// ripristino utilizzo connessione al database
			this.openspcoopstate.setUseConnection(true);
		}




		/* ------------------------------ STATELESS  NO-ROUTING -------------------------------- */
		/* Il ciclo seguente chiama tutte le librerie della gestione MDB di openspcoop, fino a quando non viene restituito un messaggio
		 * di libreria destinato a questo modulo oppure fino a che non si verifica un errore.
		 * Se la chiamata di libreria ci restituisce uno stato d'errore ed e' stata settata una risposta da inviare al mittente
		 * usciamo dal ciclo e gestiamo la risposta esattamente come facciamo se tutte le librerie sono andate a buon fine. Se ci 
		 * viene restituito un errore senza messaggio di risposta, ne creiamo uno noi (generico) e lo inviamo al mittente
		 */ 

		if (this.portaStateless) {
			
			boolean resultRequest = this.comportamentoStatelessRichiesta();
			if (!resultRequest){
				this.openspcoopstate.releaseResource();
				return;
			}
			
		}

		if(this.asyncResponseCallback==null || this.terminataGestioneStatelessErrorBeforeAsyncrConnettore) {
			this._statelessComplete(false);
		}
	}
	
	private void _statelessComplete(boolean invokedFromAsyncConnector) {
		
		try {
		
		/*
		 * ---------------- STATELESS OR Stateful v11 -------------
		 */
		if (this.portaStateless && !this.terminataGestioneStateless) {
			
			boolean resultResponse = this.comportamentoStatelessRisposta(invokedFromAsyncConnector);
			if (!resultResponse){
				this.openspcoopstate.releaseResource();
				return;
			}
				
			// ripristino utilizzo connessione al database
			this.openspcoopstate.setUseConnection(true);
		}
		
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
		
		// refresh risorse con nuovi stati
		this.registroServiziReader = this.registroServiziReader.refreshState(this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta());
		this.configurazionePdDReader = this.configurazionePdDReader.refreshState(this.registroServiziReader);
		this.tracciamento.updateState(this.configurazionePdDReader);
		this.msgDiag.updateState(this.configurazionePdDReader);

		
		

		/* ------------  GestioneRisposta non effettuata ------------- */
		this.msgDiag.mediumDebug("Gestione risposta...");
		if(!this.msgContext.isGestioneRisposta()){
			if(this.portaStateless)
				this.openspcoopstate.releaseResource();
			return;
		}
		
		boolean richiestaRispostaProtocollo = true;
		/* -------Scenario Routing -------------
		 * Una busta e 'potenzialmente ricevibile da questo modulo se:
		 * - profilo sincrono
		 * - impostazione della newConnectionForResponse = false
		 * Se ricevo una busta non sincrona e newConnectionForResponse=true ho quindi terminato.
		 * 
		 * Una busta NON DEVE cmq esssre ricevuta.
		 * Es. puo' darsi che la PdD finale mandi il sincrono su di una nuova connessione.
		 */
		
		if(this.functionAsRouter){
			if( (!org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) &&
					this.newConnectionForResponse ) {
				this.msgContext.setMessageResponse(MessageUtilities.buildEmptyMessage(this.requestMessage.getFactory(),this.requestMessage.getMessageType(),MessageRole.RESPONSE));
				return;
			}
			richiestaRispostaProtocollo = false;
		}
		/* -------Scenario Normale -------------
		 * Una busta e' potenzialmente ricevibile da questo modulo se:
		 * - indTelematicoAbilitato && indTelematicoMittente==null
		 * - indTelematicoNonAbilitato
		 * - profilo sincrono (e valgono i punti sopra degli indirizzi telematici)
		 * - profilo non sincrono && newConnectionForReponse = false
		 * Se ricevuo una busta che possiede un indTelMittente e la gestione e' abilitata ho quindi terminato.
		 * Se ricevo una busta non sincrona e newConnectionForResponse=true ho quindi terminato.
		 *  
		 * Una busta DEVE essere ricevuta quindi, superati i controlli sopra se:
		 * - profilo oneway && confermaRicezione  (poiche' se vado su newConnection o indTelematico sono gia' uscito)
		 * - profilo sincrono (poiche' se vi e' un indTelematico sono gia' uscito)
		 * Quindi una risposta dopo i controlli sopra e' sempre aspettata, a meno di eccezioni:
		 * Se ricevo una busta Errore 
		 * Se ricevo una busta di Servizio
		 * Se viene richiesta un profilo OneWay senza confermaRicezione
		 */
		else{
			if(
					(
							this.utilizzoIndirizzoTelematico && this.bustaRichiesta.getIndirizzoMittente()!=null &&
							this.moduleManager.isUtilizzoIndirizzoSoggettoPresenteBusta() &&
							(!this.oneWayStateless) && (!this.sincronoStateless) && (!this.asincronoStateless)
					)
					||
					(
							(!org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) &&
							this.newConnectionForResponse &&
							(!this.oneWayStateless) && (!this.sincronoStateless) && (!this.asincronoStateless)
					)
			) {
				this.msgContext.setMessageResponse(MessageUtilities.buildEmptyMessage(this.requestMessage.getFactory(),this.requestMessage.getMessageType(),MessageRole.RESPONSE));
				if(this.portaStateless){
					this.openspcoopstate.releaseResource();
				}
				return;
			} else {
				if ( 
						this.isMessaggioErroreProtocollo
						||
						this.bustaDiServizio
						||
						( 
								StatoFunzionalitaProtocollo.DISABILITATA.equals(this.moduleManager.getConsegnaAffidabile(this.bustaRichiesta)) ||
								(!propertiesReader.isGestioneRiscontri(this.implementazionePdDMittente)) ||
								(
										org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) 
										&&
										(!this.bustaRichiesta.isConfermaRicezione())
								)
						)
						||
						( 
								org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) ||
								org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) )
					) {
					richiestaRispostaProtocollo = false;
				} 
			}
		}



		
		
		/* ---------- Parametri Gestione risposta ------------- */
		
		RicezioneBusteGestioneRisposta parametriGestioneRisposta = 
				new RicezioneBusteGestioneRisposta();
		
		parametriGestioneRisposta.setPropertiesReader(propertiesReader);
				
		parametriGestioneRisposta.setRichiestaRispostaProtocollo(richiestaRispostaProtocollo);
		
		parametriGestioneRisposta.setPddContext(this.inRequestContext.getPddContext());





		
		/* ------------  Lettura parametri del messaggio ricevuto e ValidityCheck -------------- */
		try {
			gestioneRisposta(parametriGestioneRisposta);
		} catch (Exception e) {
			logError(this.logCore, "ErroreGenerale",e);
			this.msgDiag.logErroreGenerico(e, "Generale");
			
			this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
			this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					getErroreIntegrazione());
			
			OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
			
			// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
			this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
			this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
			this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
			this.openspcoopstate.releaseResource();
			return;
		}
		
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

	private boolean comportamentoStatelessRichiesta() {
		return _comportamentoStateless(true, false);
	}
	private boolean comportamentoStatelessRisposta(boolean invokedFromAsyncConnector) {
		return _comportamentoStateless(false, invokedFromAsyncConnector);
	}
	private EsitoLib esitoStatelessAfterSendRequest;
	private volatile boolean terminataGestioneStateless = false;
	private volatile boolean terminataGestioneStatelessErrorBeforeAsyncrConnettore = false;
	private boolean _comportamentoStateless(boolean gestioneRichiesta, boolean invokedFromAsyncConnector) {
		
		boolean esitoReturn = true;
		
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
		
		if(gestioneRichiesta) {
			this.openspcoopstate.setMessageLib(this.sbustamentoMSG);
		}
		this.openspcoopstate.setIDMessaggioSessione(this.idMessageRequest);

		// Durante le invocazioni non deve essere utilizzata la connessione al database 
		this.openspcoopstate.setUseConnection(false);

		EsitoLib esito = new EsitoLib();
		try {
						
			boolean jumpTocheckEsitoConsegna = false;
			if(gestioneRichiesta) {
				((OpenSPCoopStateless)this.openspcoopstate).setDestinatarioResponseMsgLib(""); // Verra aggiornato dalle librerie
			}
			else {
				esito = this.esitoStatelessAfterSendRequest;
				jumpTocheckEsitoConsegna = true;
			}

			while (!((OpenSPCoopStateless)this.openspcoopstate).getDestinatarioResponseMsgLib().startsWith(RicezioneBuste.ID_MODULO)) {

				boolean libreriaSbustamento = false;
				
				if (  ((OpenSPCoopStateless)this.openspcoopstate).getDestinatarioRequestMsgLib().startsWith(Sbustamento.ID_MODULO) ) {
					libreriaSbustamento = true;
				}
				
				boolean libreriaConsegnaContenutiRichiesta = false;
				if (  ((OpenSPCoopStateless)this.openspcoopstate).getDestinatarioRequestMsgLib().startsWith(ConsegnaContenutiApplicativi.ID_MODULO) ) {
					libreriaConsegnaContenutiRichiesta = true;
				}
				boolean libreriaImbustamentoRisposte = false;
				if ( !gestioneRichiesta && ((OpenSPCoopStateless)this.openspcoopstate).getDestinatarioResponseMsgLib().startsWith(ImbustamentoRisposte.ID_MODULO) ) {
					libreriaImbustamentoRisposte = true;
				}
				
				if(!jumpTocheckEsitoConsegna) {
									
					if (  ((OpenSPCoopStateless)this.openspcoopstate).getDestinatarioResponseMsgLib().startsWith(ImbustamentoRisposte.ID_MODULO) &&
						/* Verifico che non abbia rilasciato la connessione, se si la riprendo */
						( propertiesReader.isRinegoziamentoConnessione(this.bustaRichiesta.getProfiloDiCollaborazione()) && 
								(this.oneWayStateless || this.sincronoStateless || this.asincronoStateless) 
								&& this.openspcoopstate.resourceReleased())
						){
						/** per default disabilitato 
						((OpenSPCoopStateless)this.openspcoopstate).setUseConnection(true);
						((OpenSPCoopStateless)this.openspcoopstate).initResource(this.identitaPdD, ImbustamentoRisposte.ID_MODULO, this.idTransazione);
						((OpenSPCoopStateless)this.openspcoopstate).setUseConnection(false);
						*/
						// update states
						this.registroServiziReader = this.registroServiziReader.refreshState(this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta());
						this.configurazionePdDReader = this.configurazionePdDReader.refreshState(this.registroServiziReader);
						this.tracciamento.updateState(this.configurazionePdDReader);
						this.msgDiag.updateState(this.configurazionePdDReader);
					}
	
					esito = chiamaLibreria(((OpenSPCoopStateless)this.openspcoopstate), this.logCore,
							this.registroServiziReader, this.configurazionePdDReader);
					
					if(gestioneRichiesta && libreriaConsegnaContenutiRichiesta) {
						return true;
					}
				
				}
								
				if ( this.oneWayVersione11 && this.newConnectionForResponse && 
						esito.getStatoInvocazione() == EsitoLib.OK &&
						libreriaSbustamento) {

					((OpenSPCoopStateless)this.openspcoopstate).setDestinatarioResponseMsgLib(RicezioneBuste.ID_MODULO);

					// Ho finito di gestire la richiesta in questo caso.
					this.openspcoopstate.setUseConnection(true);
					this.openspcoopstate.commit();
					this.openspcoopstate.setUseConnection(false);
				}

				if ( esito.getStatoInvocazione() == EsitoLib.ERRORE_GESTITO ) {

					if(this.oneWayVersione11 && this.newConnectionForResponse &&
							libreriaSbustamento ){

						// Ho finito di gestire la richiesta in questo caso.
						this.openspcoopstate.setUseConnection(true);
						this.openspcoopstate.commit();
						this.openspcoopstate.setUseConnection(false);
					}

					if(libreriaSbustamento) {
						this.terminataGestioneStatelessErrorBeforeAsyncrConnettore = true;
					}
					
					this.terminataGestioneStateless = true;
					
					break;
				} 
				
				else if (esito.getStatoInvocazione() == EsitoLib.ERRORE_NON_GESTITO ) {
					throw new CoreException("Errore non gestito dalla libreria");
				}
				
				if(libreriaSbustamento &&
					// invocazione della libreriaSbustamento terminata, posso rilasciare la connessione se sono in stateless puro
					( propertiesReader.isRinegoziamentoConnessione(this.bustaRichiesta.getProfiloDiCollaborazione()) && (this.oneWayStateless || this.sincronoStateless || this.asincronoStateless) )
					){
					this.openspcoopstate.setUseConnection(true);
					this.openspcoopstate.commit();
					this.openspcoopstate.releaseResource();
					this.openspcoopstate.setUseConnection(false);
				}
				
				if(jumpTocheckEsitoConsegna) {
					jumpTocheckEsitoConsegna = false;
					
					if(libreriaImbustamentoRisposte && invokedFromAsyncConnector) {
						// avanzo di modulo
						((OpenSPCoopStateless)this.openspcoopstate).setDestinatarioRequestMsgLib(""); // azzero per poter invocare imbustamento risposta in metodo chiamaLibreria
					}
				}
				
				/**else {
					throw new Exception("Esito libreria sconosciuto");
				}*/
			}
		}catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneStateless");
			this.logCore.error("Errore Generale durante la gestione stateless: "+e.getMessage(),e);

			this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
			this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					getErroreIntegrazione());
			
			OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
			
			// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
			this.openspcoopstate.setUseConnection(true);
			this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
			this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
			this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
			this.openspcoopstate.releaseResource();
			return false;
		}

		if(!gestioneRichiesta) {
			this.terminataGestioneStateless = true;
		}
		
		if(!gestioneRichiesta || this.terminataGestioneStateless) {
			// ripristino utilizzo connessione al database
			this.openspcoopstate.setUseConnection(true);
		}
		
		return esitoReturn;
	}

	private void setCredenziali(Credenziali credenziali,MsgDiagnostico msgDiag){
		String credenzialiFornite = "";
		if(credenziali!=null){
			credenzialiFornite = credenziali.toString();
		}
		msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI, credenzialiFornite);
	}

	private void gestioneRisposta(RicezioneBusteGestioneRisposta parametriGestioneRisposta) throws Exception{

		/* ------- Lettura parametri ---------- */
		
		OpenSPCoop2Properties propertiesReader = parametriGestioneRisposta.getPropertiesReader();
				
		/** --- HeaderIntegrazione headerIntegrazioneRichiesta = parametriGestioneRisposta.getHeaderIntegrazioneRichiesta();
		String[] tipiIntegrazionePA = parametriGestioneRisposta.getTipiIntegrazionePA(); --- */
		if(this.headerIntegrazioneRichiesta!=null) {
			// nop
		}
		
		boolean richiestaRispostaProtocollo = parametriGestioneRisposta.isRichiestaRispostaProtocollo();
		
		PdDContext pddContextGestioneRisposta = parametriGestioneRisposta.getPddContext();
		if(pddContextGestioneRisposta==null) {
			pddContextGestioneRisposta = this.pddContext;
		}
		if(this.idTransazione==null) {
			this.idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContextGestioneRisposta);
		}
		RequestInfo requestInfo = (RequestInfo) pddContextGestioneRisposta.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		
		
		Loader loader = Loader.getInstance();
		PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
		
		GestoreMessaggi msgResponse = null;
		OpenSPCoop2Message responseMessage = MessageUtilities.buildEmptyMessage(this.requestMessage.getFactory(),this.requestMessage.getMessageType(),MessageRole.RESPONSE);

		Busta bustaRisposta = null;
		String idMessaggioSblocco = null;

		boolean contenutoRispostaPresente = false;

		RicezioneBusteMessage ricezioneBusteMSG = null;
		String idCorrelazioneApplicativaRisposta = null;
		try{
			this.msgDiag.mediumDebug("Attesa/lettura risposta...");

			if ( !this.portaStateless && !this.routingStateless  ) {
				
				String classType = null;
				INodeReceiver nodeReceiver = null;
				try{
					classType = ClassNameProperties.getInstance().getNodeReceiver(propertiesReader.getNodeReceiver());
					nodeReceiver = (INodeReceiver) loader.newInstance(classType);
					AbstractCore.init(nodeReceiver, pddContextGestioneRisposta, this.protocolFactory);
				}catch(Exception e){
					throw new CoreException("Riscontrato errore durante il caricamento della classe ["+classType+
							"] da utilizzare per la ricezione dall'infrastruttura: "+e.getMessage());
				}
				
				ricezioneBusteMSG =	(RicezioneBusteMessage) nodeReceiver.receive(this.msgDiag,this.identitaPdD,this.msgContext.getIdModulo(),
						this.idMessageRequest,propertiesReader.getNodeReceiverTimeoutRicezioneBuste(),
						propertiesReader.getNodeReceiverCheckInterval());
				
				// aggiorno pddContext
				pddContextGestioneRisposta = ricezioneBusteMSG.getPddContext();
				if(pddContextGestioneRisposta!=null){
					List<MapKey<String>> enumPddContext = pddContextGestioneRisposta.keys();
					if(enumPddContext!=null && !enumPddContext.isEmpty()) {
						for (MapKey<String> key : enumPddContext) {
							/** -- System.out.println("AGGIORNO KEY BUSTE ["+key+"]");*/ 
							this.msgContext.getPddContext().addObject(key, pddContextGestioneRisposta.getObject(key));
						}
					}
				}
			}
			else{
				ricezioneBusteMSG = (RicezioneBusteMessage) this.openspcoopstate.getMessageLib();
			}


			contenutoRispostaPresente = ricezioneBusteMSG.getBustaRisposta()!=null;
			if( richiestaRispostaProtocollo && (!contenutoRispostaPresente)  ){
				throw new CoreException("Risposta attesa e non ritornata: ErroreInterno");
			}
			// Leggo il contenuto
			if(contenutoRispostaPresente){
				bustaRisposta = ricezioneBusteMSG.getBustaRisposta();

				this.msgContext.getProtocol().setIdRisposta(bustaRisposta.getID());
				this.msgContext.getProtocol().setCollaborazione(bustaRisposta.getCollaborazione());

			}else{
				idMessaggioSblocco = ricezioneBusteMSG.getIdMessaggioSblocco();
			}

		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneRisposta("+this.msgContext.getIdModulo()+")");
			logError(this.logCore, "Gestione risposta ("+this.msgContext.getIdModulo()+") con errore",e);
			
			// per la costruzione dell'errore ho bisogno di una connessione al database
			if ( !this.portaStateless && !this.routingStateless  ) {
				try{
					this.msgDiag.mediumDebug("Richiesta connessione al database per la gestione della risposta...");
					this.openspcoopstate.updateResource(this.idTransazione);
				}catch(Exception eDB){
					setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,this.logCore,this.msgDiag,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION),eDB,
							"openspcoopstate.updateDatabaseResource");
					return;
				}
			}

			// In caso di Timeout elimino messaggi di richiesta ancora in processamento.
			if(e instanceof NodeTimeoutException) {
				try{
					this.msgDiag.logPersonalizzato("timeoutRicezioneRisposta");
					this.msgRequest.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
					this.openspcoopstate.commit();
				}catch(Exception eDel){
					this.msgDiag.logErroreGenerico(eDel.getMessage(),"EliminazioneMessaggioScadutoTimeoutRicezioneRisposta");
				}
			}

			// Spedisco errore
			this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
			this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_513_RECEIVE));

			OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
			
			// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
			this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
			this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
			this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

			// rilascio connessione e ritorno messaggio
			this.openspcoopstate.releaseResource();
			return;
		}



		//	Aggiornamento Informazioni
		if(contenutoRispostaPresente){
			this.msgDiag.setIdMessaggioRisposta(bustaRisposta.getID());
			this.msgDiag.addKeywords(bustaRisposta, false);
		}else{
			this.msgDiag.setIdMessaggioRisposta(idMessaggioSblocco);
		}
		this.parametriGenerazioneBustaErrore.setMsgDiag(this.msgDiag);
		this.parametriInvioBustaErrore.setMsgDiag(this.msgDiag);
		
		if ( !this.portaStateless && !this.routingStateless  ) {

			/* ------------  Re-ottengo Connessione al DB -------------- */
			try{
				this.msgDiag.mediumDebug("Richiesta connessione al database per la gestione della risposta...");
				this.openspcoopstate.updateResource(this.idTransazione);
			}catch(Exception e){
				setSOAPFaultProcessamento(IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,this.logCore,this.msgDiag,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION),e,
						"openspcoopstate.updateDatabaseResource");
				return;
			}

			// MsgResponse
			if(contenutoRispostaPresente){
				msgResponse = new GestoreMessaggi(this.openspcoopstate, false, bustaRisposta.getID(),Costanti.OUTBOX,this.msgDiag,pddContextGestioneRisposta);
			}else{
				msgResponse = new GestoreMessaggi(this.openspcoopstate, false,idMessaggioSblocco,Costanti.OUTBOX,this.msgDiag,pddContextGestioneRisposta);
			}
			/**tempiAttraversamentoGestioneMessaggi = msgResponse.getTempiAttraversamentoPdD();
			dimensioneMessaggiAttraversamentoGestioneMessaggi = msgResponse.getDimensioneMessaggiAttraversamentoPdD();*/


			/* ------------  Lettura Contenuto Messaggio (mapping in Message)  -------------- */
			if(contenutoRispostaPresente || this.functionAsRouter){
				this.msgDiag.mediumDebug("Lettura messaggio di risposta...");
				try{
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
				}catch(Exception e){
					// Il router potrebbe ricevere il SOAPFault da reinoltrare
					if( (!this.functionAsRouter) || (contenutoRispostaPresente) ){
						this.msgDiag.logErroreGenerico(e,"msgResponse.getMessage()");
						
						this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
						this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_511_READ_RESPONSE_MSG));

						OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
						this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
						this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
						this.openspcoopstate.releaseResource();
						return;
					}
				}
			}
		}

		else {
			responseMessage = ((OpenSPCoopStateless)this.openspcoopstate).getRispostaMsg();
			idCorrelazioneApplicativaRisposta = ((OpenSPCoopStateless)this.openspcoopstate).getIDCorrelazioneApplicativaRisposta();
			if(responseMessage!=null){
				this.parametriGenerazioneBustaErrore.setParseException(responseMessage.getParseException());
			}
			/**tempiAttraversamentoGestioneMessaggi = 
				((OpenSPCoopStateless) openspcoopstate).getTempiAttraversamentoPDD();
			dimensioneMessaggiAttraversamentoGestioneMessaggi = 
				((OpenSPCoopStateless) openspcoopstate).getDimensioneMessaggiAttraversamentoPDD();*/
		}

		/* ---- Aggiorno informazioni correlazione applicativa risposta ---- */
		this.parametriGenerazioneBustaErrore.setCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
		this.parametriInvioBustaErrore.setCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
		this.msgDiag.setIdCorrelazioneRisposta(idCorrelazioneApplicativaRisposta);
		if(this.msgContext.getIntegrazione()!=null)
			this.msgContext.getIntegrazione().setIdCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);

		// Gestione Busta di Risposta
		if(contenutoRispostaPresente){

			/* ----------- Trasmissione ------------------ */
			Trasmissione tras = null;
			if( propertiesReader.isGenerazioneListaTrasmissioni(this.implementazionePdDMittente)){
				this.msgDiag.mediumDebug("Impostazione trasmissione nella busta di risposta...");
				// Tracciamento in busta
				tras = new Trasmissione();
				
				// origine
				tras.setOrigine(this.identitaPdD.getNome());
				tras.setTipoOrigine(this.identitaPdD.getTipo());
				tras.setIdentificativoPortaOrigine(this.identitaPdD.getCodicePorta());
				
				// trasmissione
				// Cerco destinatario con identita che sto assumendo (l'origine di quella trasmissione e' la destinazione di questa!)
				// che come mittente non possieda il mittente attuale della busta (senno potrebbe essere il potenziale
				// precedente hop che ha aggiunto una trasmissione da lui a questo hop)
				for(int i=0;i<this.bustaRichiesta.sizeListaTrasmissioni();i++){
					if( this.identitaPdD.getTipo().equals(this.bustaRichiesta.getTrasmissione(i).getTipoDestinazione()) &&
							this.identitaPdD.getNome().equals(this.bustaRichiesta.getTrasmissione(i).getDestinazione()) ){
						boolean tipoOrigineValido = true;
						try {
							this.traduttore.toProtocolOrganizationType(this.bustaRichiesta.getTrasmissione(i).getTipoOrigine());
						}catch(Exception e) {
							tipoOrigineValido = false;
						}
						if(tipoOrigineValido) {
							tras.setDestinazione(this.bustaRichiesta.getTrasmissione(i).getOrigine());
							tras.setTipoDestinazione(this.bustaRichiesta.getTrasmissione(i).getTipoOrigine());
						}
					}
				}
				if(tras.getDestinazione()==null || tras.getTipoDestinazione()==null){
					tras.setDestinazione(bustaRisposta.getDestinatario());
					tras.setTipoDestinazione(bustaRisposta.getTipoDestinatario());
				}
				try{
					String dominio = this.registroServiziReader.getDominio(new IDSoggetto(tras.getTipoDestinazione(),tras.getDestinazione()), null, this.protocolFactory, requestInfo);
					tras.setIdentificativoPortaDestinazione(dominio);
				}catch(Exception e){
					// ignore
				}
				
				// oraRegistrazione
				tras.setOraRegistrazione(bustaRisposta.getOraRegistrazione());
				tras.setTempo(propertiesReader.getTipoTempoBusta(this.implementazionePdDMittente));
				
				bustaRisposta.addTrasmissione(tras);
			}



			/* ------------  Gestione Funzionalita' speciali per Attachments (Manifest) ------------- */	
			boolean scartaBody = false;
			if(!this.functionAsRouter){
				boolean allegaBody = this.configurazionePdDReader.isAllegaBody(this.pa);
				if(allegaBody){
					// E' stato effettuato prima l'inserimento del body come allegato.
					// Forzo lo scartamento.
					scartaBody = true;
				}else{
					scartaBody = this.configurazionePdDReader.isScartaBody(this.pa);
				}
			}








			/* ------------  Aggiunta eccezioni di livello info riscontrate dalla validazione, se profilo e' lineeGuida1.1 ------------- */	
			if(!this.functionAsRouter &&
				bustaRisposta.sizeListaEccezioni()==0 && this.moduleManager.isIgnoraEccezioniLivelloNonGrave()){
				for(int i=0; i<this.erroriValidazione.size();i++){
					Eccezione ec = this.erroriValidazione.get(i);
					if(LivelloRilevanza.INFO.equals(ec.getRilevanza())){
						bustaRisposta.addEccezione(ec);
					}
				}
				if(this.erroriProcessamento!=null) {
					// ignore
				}
			}








			/* ------------  Imbustamento (Prima della Sicurezza)  ------------- */	
			
			this.msgDiag.mediumDebug("Imbustamento della risposta...");
			BustaRawContent<?> headerBustaRisposta = null;
			boolean gestioneManifestRisposta = false;
			Imbustamento imbustatore = null;
			try{
				if(this.functionAsRouter){
					gestioneManifestRisposta = this.configurazionePdDReader.isGestioneManifestAttachments();
				}else{
					if( org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) &&
							RuoloBusta.RISPOSTA.equals(this.ruoloBustaRicevuta.toString()) &&
							this.pd!=null){ // devo generare la ricevuta alla risposta
						gestioneManifestRisposta = this.configurazionePdDReader.isGestioneManifestAttachments(this.pd,this.protocolFactory);
					}else{
						gestioneManifestRisposta = this.configurazionePdDReader.isGestioneManifestAttachments(this.pa,this.protocolFactory);
					}
				}
				imbustatore = new Imbustamento(this.logCore, this.protocolFactory,this.openspcoopstate.getStatoRichiesta());
				if(this.functionAsRouter && 
						!( this.identitaPdD.getTipo().equals(bustaRisposta.getTipoMittente()) && this.identitaPdD.getNome().equals(bustaRisposta.getMittente()) ) 
				){
					// Aggiungo trasmissione solo se la busta e' stata generata dalla porta di dominio destinataria della richiesta.
					// Se il mittente e' il router, logicamente la busta sara' un errore generato dal router
					if( propertiesReader.isGenerazioneListaTrasmissioni(this.implementazionePdDMittente)){
						this.msgDiag.highDebug("Tipo Messaggio Risposta prima dell'imbustamento ["+responseMessage.getClass().getName()+"]");
						ProtocolMessage protocolMessage = imbustatore.addTrasmissione(responseMessage, tras, this.readQualifiedAttribute, 
								FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
						if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
							headerBustaRisposta = protocolMessage.getBustaRawContent();
							responseMessage = protocolMessage.getMessage(); // updated
						}
						this.msgDiag.highDebug("Tipo Messaggio Risposta dopo l'imbustamento ["+responseMessage.getClass().getName()+"]");
					}
					else{
						Validatore v = new Validatore(responseMessage,pddContextGestioneRisposta,this.openspcoopstate.getStatoRichiesta(), this.logCore, this.protocolFactory);
						headerBustaRisposta = v.getHeaderProtocollo_senzaControlli();
					}
				}else{
					this.msgDiag.highDebug("Tipo Messaggio Risposta prima dell'imbustamento ["+responseMessage.getClass().getName()+"]");
					ProtocolMessage protocolMessage = imbustatore.imbustamentoRisposta(responseMessage,pddContextGestioneRisposta,
							bustaRisposta,this.bustaRichiesta,
							this.infoIntegrazione,gestioneManifestRisposta,scartaBody,this.proprietaManifestAttachments,
							FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
					if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
						headerBustaRisposta = protocolMessage.getBustaRawContent();
						responseMessage = protocolMessage.getMessage(); // updated
					}
					this.msgDiag.highDebug("Tipo Messaggio Risposta dopo l'imbustamento ["+responseMessage.getClass().getName()+"]");
				}
			}catch(Exception e){
				
				// STATEFUL
				if (msgResponse!=null && !this.portaStateless && !this.routingStateless){ 
					try {
						// Aggiorno proprietario Messaggio
						this.msgDiag.mediumDebug("Aggiornamento proprietario messaggio risposta (statefull) ...");
						msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
					}catch(Throwable t) {
						// ignore
					}
				}
				
				if(this.functionAsRouter && 
						!( this.identitaPdD.getTipo().equals(bustaRisposta.getTipoMittente()) && this.identitaPdD.getNome().equals(bustaRisposta.getMittente()) ) 
				){
					this.msgDiag.logErroreGenerico(e,"imbustatore.pre-security.addTrasmissione(risposta)");
				}else{
					this.msgDiag.logErroreGenerico(e,"imbustatore.pre-security-imbustamento(risposta)");
				}

				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO));
				this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.INTEROPERABILITY_PROFILE_ENVELOPING_RESPONSE_FAILED);
				
				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				
				if(responseMessage!=null && responseMessage.getParseException()!=null){
					errorOpenSPCoopMsg.setParseException(responseMessage.getParseException());
				}
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
				this.openspcoopstate.releaseResource();
				return;
			}




			
			
			
			
			/* ------------ Init MTOM Processor  -------------- */
			MTOMProcessor mtomProcessor = null;
			if(!this.functionAsRouter &&
				this.flowPropertiesResponse!=null){
				this.msgDiag.mediumDebug("init MTOM Processor ...");
				mtomProcessor = new MTOMProcessor(this.flowPropertiesResponse.mtom, this.flowPropertiesResponse.messageSecurity, 
						this.tipoPorta, this.msgDiag, this.logCore, pddContextGestioneRisposta);
			}
			
			
			
			
			
			/* ------------ MTOM Processor BeforeSecurity  -------------- */
			if(mtomProcessor!=null){
				try{
					mtomProcessor.mtomBeforeSecurity(responseMessage, RuoloMessaggio.RISPOSTA);
				}catch(Exception e){
					
					// STATEFUL
					if (msgResponse!=null && !this.portaStateless && !this.routingStateless){ 
						try {
							// Aggiorno proprietario Messaggio
							this.msgDiag.mediumDebug("Aggiornamento proprietario messaggio risposta (before security) ...");
							msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
						}catch(Throwable t) {
							// ignore
						}
					}
					
					// L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
					/**msgDiag.logErroreGenerico(e,"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");*/
					
					this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
					this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR));
					this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.ATTACHMENTS_PROCESSING_RESPONSE_FAILED);
					
					OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
					this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
					this.openspcoopstate.releaseResource();
					return;
				}
			}




			/* ------------ Message-Security -------------- */
			if(!this.functionAsRouter){
				if(this.flowPropertiesResponse != null && this.flowPropertiesResponse.messageSecurity!=null && 
						this.flowPropertiesResponse.messageSecurity.getFlowParameters() !=null &&
						this.flowPropertiesResponse.messageSecurity.getFlowParameters().size() > 0){
					try{
						// Aggiorno valori dinamici
						this.configurazionePdDReader.updateMessageSecurityForSender(this.flowPropertiesResponse.messageSecurity, this.logCore, responseMessage, this.bustaRichiesta, requestInfo, pddContextGestioneRisposta,
								this.flowPropertiesResponse.messageSecurityRequest);
						
						this.messageSecurityContext.setFunctionAsClient(SecurityConstants.SECURITY_CLIENT);
						this.messageSecurityContext.setOutgoingProperties(this.flowPropertiesResponse.messageSecurity.getFlowParameters());
						
						String tipoSicurezza = SecurityConstants.convertActionToString(this.messageSecurityContext.getOutgoingProperties());
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoSicurezza);
						pddContextGestioneRisposta.addObject(CostantiPdD.TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoSicurezza);
						
						this.msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaInCorso");
						
						if(org.openspcoop2.security.message.engine.WSSUtilities.isNormalizeToSaajImpl(this.messageSecurityContext)){
							this.msgDiag.mediumDebug("Normalize to saajImpl");
							/**System.out.println("RicezioneBuste.response.normalize");*/
							responseMessage = responseMessage.normalizeToSaajImpl();
						}
						
						DynamicMapBuilderUtils.injectDynamicMap(this.bustaRichiesta, requestInfo, pddContextGestioneRisposta, this.logCore);
						if(!this.messageSecurityContext.processOutgoing(responseMessage,pddContextGestioneRisposta,
								this.transaction!=null ? this.transaction.getTempiElaborazione() : null)){
							
							this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , this.messageSecurityContext.getMsgErrore() );
							this.msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaInErrore");
							
							this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
							this.parametriGenerazioneBustaErrore.setErroreCooperazione(ErroriCooperazione.MESSAGE_SECURITY.
									getErroreMessageSecurity(this.messageSecurityContext.getMsgErrore(), this.messageSecurityContext.getCodiceErrore()));
							this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.MESSAGE_SECURITY_RESPONSE_FAILED);
							
							OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreValidazione(this.parametriGenerazioneBustaErrore);
							
							// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
							this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
							this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
							this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
							this.openspcoopstate.releaseResource();
							return;
						}
						else{
							this.msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaEffettuato");
						}
						
					}catch(Exception e){
						
						// STATEFUL
						if (msgResponse!=null && !this.portaStateless && !this.routingStateless){ 
							try {
								// Aggiorno proprietario Messaggio
								this.msgDiag.mediumDebug("Aggiornamento proprietario messaggio risposta (security) ...");
								msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
							}catch(Throwable t) {
								// ignore
							}
						}
						
						this.msgDiag.addKeywordErroreProcessamento(e);
						this.msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaInErrore");
						logError(this.logCore, "[MessageSecurityResponse]" + e.getMessage(),e);
						
						this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
						this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));

						OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
						this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
						this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
						this.openspcoopstate.releaseResource();
						return;
					}
				}
				else{
					this.msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaDisabilitato");
				}
			}

			
			
			
			/* ------------ MTOM Processor AfterSecurity  -------------- */
			if(mtomProcessor!=null){
				try{
					mtomProcessor.mtomAfterSecurity(responseMessage, RuoloMessaggio.RISPOSTA);
				}catch(Exception e){
					
					// STATEFUL
					if (msgResponse!=null && !this.portaStateless && !this.routingStateless){ 
						try {
							// Aggiorno proprietario Messaggio
							this.msgDiag.mediumDebug("Aggiornamento proprietario messaggio risposta (after security) ...");
							msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
						}catch(Throwable t) {
							// ignore
						}
					}
					
					// L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
					/**msgDiag.logErroreGenerico(e,"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");*/
					
					this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
					this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR));
					this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.ATTACHMENTS_PROCESSING_RESPONSE_FAILED);
					
					OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
					this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
					this.openspcoopstate.releaseResource();
					return;
				}
			}
			
			
			
			
			
			/* ------------  Imbustamento (Dopo della Sicurezza)  ------------- */	
			
			this.msgDiag.mediumDebug("Imbustamento della risposta dopo la sicurezza...");
			try{
				if(this.functionAsRouter && 
						!( this.identitaPdD.getTipo().equals(bustaRisposta.getTipoMittente()) && this.identitaPdD.getNome().equals(bustaRisposta.getMittente()) ) 
				){
					// Aggiungo trasmissione solo se la busta e' stata generata dalla porta di dominio destinataria della richiesta.
					// Se il mittente e' il router, logicamente la busta sara' un errore generato dal router
					if( propertiesReader.isGenerazioneListaTrasmissioni(this.implementazionePdDMittente)){
						this.msgDiag.highDebug("Tipo Messaggio Risposta prima dell'imbustamento (after-security) ["+responseMessage.getClass().getName()+"]");
						ProtocolMessage protocolMessage = imbustatore.addTrasmissione(responseMessage, tras, this.readQualifiedAttribute, 
								FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
						if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
							headerBustaRisposta = protocolMessage.getBustaRawContent();
							responseMessage = protocolMessage.getMessage(); // updated
						}
						this.msgDiag.highDebug("Tipo Messaggio Risposta dopo l'imbustamento (after-security) ["+responseMessage.getClass().getName()+"]");
					}
					// else gia' effettuato nella precedente fase pre-sicurezza
				}else{
					this.msgDiag.highDebug("Tipo Messaggio Risposta prima dell'imbustamento (after-security) ["+responseMessage.getClass().getName()+"]");
					ProtocolMessage protocolMessage = imbustatore.imbustamentoRisposta(responseMessage,pddContextGestioneRisposta,
							bustaRisposta,this.bustaRichiesta,
							this.infoIntegrazione,gestioneManifestRisposta,scartaBody,this.proprietaManifestAttachments,
							FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
					if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
						headerBustaRisposta = protocolMessage.getBustaRawContent();
						responseMessage = protocolMessage.getMessage(); // updated
					}
					this.msgDiag.highDebug("Tipo Messaggio Risposta dopo l'imbustamento (after-security) ["+responseMessage.getClass().getName()+"]");
				}
			}catch(Exception e){
				
				// STATEFUL
				if (msgResponse!=null && !this.portaStateless && !this.routingStateless){ 
					try {
						// Aggiorno proprietario Messaggio
						this.msgDiag.mediumDebug("Aggiornamento proprietario messaggio risposta ...");
						msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
					}catch(Throwable t) {
						// ignore
					}
				}
				
				if(this.functionAsRouter && 
						!( this.identitaPdD.getTipo().equals(bustaRisposta.getTipoMittente()) && this.identitaPdD.getNome().equals(bustaRisposta.getMittente()) ) 
				){
					this.msgDiag.logErroreGenerico(e,"imbustatore.after-security.addTrasmissione(risposta)");
				}else{
					this.msgDiag.logErroreGenerico(e,"imbustatore.after-security-imbustamento(risposta)");
				}

				OpenSPCoop2Message errorOpenSPCoopMsg = null;
				
				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.INTEROPERABILITY_PROFILE_ENVELOPING_RESPONSE_FAILED);
				if(e instanceof ProtocolException pe && pe.isInteroperabilityError() ) {
					this.parametriGenerazioneBustaErrore.setErroreCooperazione(ErroriCooperazione.ERRORE_GENERICO_PROTOCOLLO_NON_CORRETTO.
							getErroreCooperazione(e.getMessage()));
					errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreValidazione(this.parametriGenerazioneBustaErrore);
				}
				else {
					this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO));
					errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				}

				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
				this.openspcoopstate.releaseResource();
				return;
			}
			
			
			

			/* ---------- Tracciamento Busta Ricevuta ------------- */
			this.msgDiag.mediumDebug("Tracciamento busta di risposta...");
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioInviato();
				SecurityInfo securityInfoResponse  = null;
				if(!this.functionAsRouter &&
					this.messageSecurityContext!=null && this.messageSecurityContext.getDigestReader(responseMessage.getFactory())!=null){
					IValidazioneSemantica validazioneSemantica = this.protocolFactory.createValidazioneSemantica(this.openspcoopstate.getStatoRichiesta());
					securityInfoResponse = validazioneSemantica.readSecurityInformation(this.messageSecurityContext.getDigestReader(responseMessage.getFactory()),responseMessage);
				}
				this.tracciamento.registraRisposta(responseMessage,securityInfoResponse,headerBustaRisposta,bustaRisposta,esitoTraccia,
						Tracciamento.createLocationString(false,this.msgContext.getSourceLocation()),
						this.correlazioneApplicativa,idCorrelazioneApplicativaRisposta);
			}
			IValidatoreErrori validatoreErrori = this.protocolFactory.createValidatoreErrori(this.openspcoopstate.getStatoRichiesta());
			IProtocolManager protocolManager = this.protocolFactory.createProtocolManager();
			ProprietaValidazioneErrori pValidazioneErrori = new ProprietaValidazioneErrori();
			pValidazioneErrori.setIgnoraEccezioniNonGravi(protocolManager.isIgnoraEccezioniNonGravi());
			pValidazioneErrori.setVersioneProtocollo(this.versioneProtocollo);
			if( validatoreErrori.isBustaErrore(bustaRisposta,responseMessage,pValidazioneErrori) ) {
				if(this.mittenteAnonimo){
					this.msgDiag.logPersonalizzato("generazioneMessaggioErroreRisposta.mittenteAnonimo");
				}
				else{
					this.msgDiag.logPersonalizzato("generazioneMessaggioErroreRisposta");
				}
			}else{
				if(this.mittenteAnonimo){
					this.msgDiag.logPersonalizzato("generazioneMessaggioRisposta.mittenteAnonimo");
				}
				else{
					this.msgDiag.logPersonalizzato("generazioneMessaggioRisposta");
				}
			}
				

			/* --------	Elimino accesso daPdD --------- */
			this.msgDiag.mediumDebug("Eliminazione accesso da PdD...");
			this.repositoryBuste.eliminaUtilizzoPdDFromOutBox(bustaRisposta.getID());

		}

		
		
		
		
		
		
		
		/* ----- Header Integrazione ------ */
		if(this.pa!=null){
			this.msgDiag.mediumDebug("Gestione header di integrazione messaggio di risposta...");
			HeaderIntegrazione headerIntegrazioneRisposta = new HeaderIntegrazione(this.idTransazione);
			headerIntegrazioneRisposta.setBusta(new HeaderIntegrazioneBusta());
			headerIntegrazioneRisposta.getBusta().setTipoMittente(this.bustaRichiesta.getTipoMittente());
			headerIntegrazioneRisposta.getBusta().setMittente(this.bustaRichiesta.getMittente());
			headerIntegrazioneRisposta.getBusta().setTipoDestinatario(this.bustaRichiesta.getTipoDestinatario());
			headerIntegrazioneRisposta.getBusta().setDestinatario(this.bustaRichiesta.getDestinatario());
			headerIntegrazioneRisposta.getBusta().setTipoServizio(this.bustaRichiesta.getTipoServizio());
			headerIntegrazioneRisposta.getBusta().setServizio(this.bustaRichiesta.getServizio());
			headerIntegrazioneRisposta.getBusta().setVersioneServizio(this.bustaRichiesta.getVersioneServizio());
			headerIntegrazioneRisposta.getBusta().setAzione(this.bustaRichiesta.getAzione());
			if(this.bustaRichiesta.getCollaborazione()!=null) {
				headerIntegrazioneRisposta.getBusta().setIdCollaborazione(this.bustaRichiesta.getCollaborazione());
			}
			else if(bustaRisposta!=null && bustaRisposta.getCollaborazione()!=null) {
				headerIntegrazioneRisposta.getBusta().setIdCollaborazione(bustaRisposta.getCollaborazione());
			}
			headerIntegrazioneRisposta.getBusta().setID(this.bustaRichiesta.getID());
			headerIntegrazioneRisposta.getBusta().setProfiloDiCollaborazione(this.bustaRichiesta.getProfiloDiCollaborazione());
			headerIntegrazioneRisposta.setIdApplicativo(this.correlazioneApplicativa);
				
			String[] tipiIntegrazionePAResponse = null;
			this.msgDiag.mediumDebug("Header integrazione...");
			if(this.functionAsRouter ){
				this.msgDiag.highDebug("Header integrazione (Default gestori integrazione Router)");
				if(RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.containsKey(this.protocolFactory.getProtocol()))
					tipiIntegrazionePAResponse = RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.get(this.protocolFactory.getProtocol());
				else
					tipiIntegrazionePAResponse = RicezioneBuste.defaultGestoriIntegrazionePA;
			}else{
				this.msgDiag.highDebug("Header integrazione (Gestori integrazione...)");
				if(this.pa!=null && this.msgContext.isTracciamentoAbilitato()){
					this.msgDiag.mediumDebug("Lettura header di integrazione...");
					try {
						tipiIntegrazionePAResponse = this.configurazionePdDReader.getTipiIntegrazione(this.pa);
					} catch (Exception e) {
						this.msgDiag.logErroreGenerico(e,"configurazionePdDReader.getTipiIntegrazione(pa)");
						
						this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
						this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));

						OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
						this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
						this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
						this.openspcoopstate.releaseResource();
						return;
					}
					this.msgDiag.highDebug("Header integrazione (Gestori integrazione terminato)");
					if (tipiIntegrazionePAResponse == null){
						if(RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.containsKey(this.protocolFactory.getProtocol()))
							tipiIntegrazionePAResponse = RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.get(this.protocolFactory.getProtocol());
						else
							tipiIntegrazionePAResponse = RicezioneBuste.defaultGestoriIntegrazionePA;
					}
				}
			}
			
			OutResponsePAMessage outResponsePAMessage = new OutResponsePAMessage();
			outResponsePAMessage.setBustaRichiesta(this.bustaRichiesta);
			outResponsePAMessage.setMessage(responseMessage);
			Map<String, List<String>> propertiesIntegrazioneRisposta = new HashMap<>();
			outResponsePAMessage.setHeaders(propertiesIntegrazioneRisposta);
			outResponsePAMessage.setPortaDelegata(this.pd);
			outResponsePAMessage.setPortaApplicativa(this.pa);
			outResponsePAMessage.setSoggettoMittente(this.soggettoFruitore);
			outResponsePAMessage.setServizio(this.idServizio);
			
			if(tipiIntegrazionePAResponse!=null) {
				for (int i = 0; i < tipiIntegrazionePAResponse.length; i++) {
					try {
						IGestoreIntegrazionePA gestore = pluginLoader.newIntegrazionePortaApplicativa(tipiIntegrazionePAResponse[i]);
						if(gestore!=null){
							String classType = null;
							try {
								classType = gestore.getClass().getName();
								AbstractCore.init(gestore, pddContextGestioneRisposta, this.protocolFactory);
							} catch (Exception e) {
								throw new CoreException(
										"Riscontrato errore durante l'inizializzazione della classe ["+ classType
												+ "] da utilizzare per la gestione dell'integrazione delle erogazioni (Risposta Update/Delete) di tipo ["+ tipiIntegrazionePAResponse[i] + "]: " + e.getMessage());
							}
							if(gestore instanceof IGestoreIntegrazionePASoap){
								if(propertiesReader.deleteHeaderIntegrazioneResponsePA()){
									if(responseMessage==null){
										responseMessage = MessageUtilities.buildEmptyMessage(this.requestMessage.getFactory(),this.requestMessage.getMessageType(), MessageRole.RESPONSE);
										outResponsePAMessage.setMessage(responseMessage);
									}
									gestore.setOutResponseHeader(headerIntegrazioneRisposta,outResponsePAMessage);
								}else{
									// gia effettuato l'update dell'header in InoltroBuste
								}
							}else{
								if(responseMessage==null){
									responseMessage = MessageUtilities.buildEmptyMessage(this.requestMessage.getFactory(),this.requestMessage.getMessageType(), MessageRole.RESPONSE);
									outResponsePAMessage.setMessage(responseMessage);
								}
								gestore.setOutResponseHeader(headerIntegrazioneRisposta,outResponsePAMessage);
							}
						} else {
							throw new CoreException("Gestore non inizializzato");
						}
							
					} catch (Exception e) {
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazionePAResponse[i]);
						this.msgDiag.addKeywordErroreProcessamento(e);
						this.msgDiag.logPersonalizzato("headerIntegrazione.creazioneFallita");
						logError(this.logCore, this.msgDiag.getMessaggio_replaceKeywords("headerIntegrazione.creazioneFallita"), e);
					}
				}
			}
			
			// Imposto header di trasporto per la risposta
			this.msgContext.setResponseHeaders(propertiesIntegrazioneRisposta);
			
		}			
			
			



		// STATELESS
		if (this.oneWayStateless || this.sincronoStateless || this.asincronoStateless) {
			this.msgDiag.mediumDebug("Aggiorno proprietario messaggio richiesta ...");
			try {
				/* Lo stateless che non è onewayVersione11 non salva niente su database */
				/**msgRequest.setReadyForDrop(true);
				msgRequest.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
				repositoryBuste.eliminaBustaStatelessFromInBox(idMessageRequest);*/

				// Committo modifiche (I commit servono per eventuali modifiche ai duplicati)
				this.openspcoopstate.commit();
			} catch (Exception e) {
				logError(this.logCore, "Riscontrato errore durante l'aggiornamento proprietario messaggio", e);
				this.msgDiag.logErroreGenerico(e, "openspcoopstate.commit(stateless risposta)");
				
				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						getErroreIntegrazione());
				
				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
				this.openspcoopstate.releaseResource();
				return;
			}
		}



		/* ----- OneWay stateful/stateless ibrido ------ */
		if (this.oneWayVersione11) {
			this.msgDiag.mediumDebug("Commit della gestione oneWay stateful/stateless ibrido ...");
			try {
				// Committo modifiche
				this.openspcoopstate.commit();
			} catch (Exception e) {
				logError(this.logCore, "Riscontrato errore durante il commit della gestione oneWay stateful/stateless ibrido", e);
				this.msgDiag.logErroreGenerico(e, "openspcoopstate.commit(oneway1.1 risposta)");
				
				this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
				this.parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						getErroreIntegrazione());
				
				OpenSPCoop2Message errorOpenSPCoopMsg = this.ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
				this.ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
				this.openspcoopstate.releaseResource();
				return;
			}
		}


		// STATEFUL
		if (!this.portaStateless && !this.routingStateless){ 

			/* ---------- Gestione Transazione Modulo ---------------- */	    
			// Aggiorno proprietario Messaggio
			this.msgDiag.mediumDebug("Aggiornamento proprietario messaggio...");
			if(msgResponse!=null) {
				msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
			}

			// Commit JDBC
			this.msgDiag.mediumDebug("Commit delle operazioni per la gestione della risposta...");
			this.openspcoopstate.commit();

			// Aggiornamento cache messaggio
			if(msgResponse!=null)
				msgResponse.addMessaggiIntoCache_readFromTable(RicezioneBuste.ID_MODULO, "risposta");

			// Aggiornamento cache proprietario messaggio
			if(msgResponse!=null)
				msgResponse.addProprietariIntoCache_readFromTable(RicezioneBuste.ID_MODULO, "risposta",this.idMessageRequest,this.functionAsRouter);

		}





		// Rilascio connessione al DB
		this.msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta effettuato, rilascio della connessione...");
		this.openspcoopstate.releaseResource();

		// Risposta 
		this.msgDiag.mediumDebug("Imposto risposta nel context...");
		this.msgContext.setMessageResponse(responseMessage);
		this.msgDiag.mediumDebug("Lavoro Terminato.");



	
	}
	
	







	private boolean gestioneRispostaAsincronaCheckPresenzaRichiesta(long scadenzaControllo,int checkInterval,Busta bustaRichiesta,
			IOpenSPCoopState openspcoopstate,MsgDiagnostico msgDiag,boolean newConnectionForResponse,
			PdDContext pddContext) throws ProtocolException, GestoreMessaggiException {
		boolean attendiTerminazioneRichiesta = false;

		if(this.protocolFactory==null) {
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
		}
		
		// Puo' potenzialmente essere una ricevuta alla richiesta (new connection for response)
		// o alla risposta (e cmq la risposta l'ho generata io e quindi e' chiaro che ho finito di gestire la richiesta)
		boolean isRicevutaRichiesta = false;
		boolean isRicevutaRisposta = false;
		if(newConnectionForResponse &&
			( ((org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) 
					||
					(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()))) &&
					bustaRichiesta.getRiferimentoMessaggio()!=null) 
			){	
			// Le ricevute hanno il riferimento messaggio anche nelle linee guida!
			ProfiloDiCollaborazione profiloCollaborazione = new ProfiloDiCollaborazione(openspcoopstate.getStatoRichiesta(),this.protocolFactory);
			isRicevutaRichiesta = profiloCollaborazione.asincrono_isRicevutaRichiesta(bustaRichiesta.getRiferimentoMessaggio());
			isRicevutaRisposta =  profiloCollaborazione.asincrono_isRicevutaRisposta(bustaRichiesta.getRiferimentoMessaggio());
		}


		while( DateManager.getTimeMillis() < scadenzaControllo ){

			msgDiag.mediumDebug("RICHIESTA  NOW["+DateManager.getTimeMillis()+"] < SCADENZA["+scadenzaControllo+"]");

			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	
				if( (bustaRichiesta.getRiferimentoMessaggio()!=null) || 
						( bustaRichiesta.getCollaborazione()!=null && (!bustaRichiesta.getCollaborazione().equals(bustaRichiesta.getID())) ) 
				){

					if( 
							( !newConnectionForResponse ) 
							|| 
							( (!isRicevutaRichiesta) && (!isRicevutaRisposta) ) 
					){	
						String idRichiesta = bustaRichiesta.getRiferimentoMessaggio();
						if(idRichiesta==null){
							// LineeGuida non sara' presente il riferimentoMessaggio ma l'elemento collaborazione
							idRichiesta = bustaRichiesta.getCollaborazione();
						}
						GestoreMessaggi checkRichiesta = new GestoreMessaggi(openspcoopstate, false, idRichiesta,Costanti.OUTBOX,msgDiag,pddContext);
						attendiTerminazioneRichiesta =  checkRichiesta.existsMessageInProcessamento();
						if(!attendiTerminazioneRichiesta)
							break;
						/**else{
							String proprietario = checkRichiesta.getProprietario(RicezioneBuste.ID_MODULO);
							msgDiag.infoOpenSPCoop("RICHIESTA  ID["+bustaRichiesta.getRiferimentoMessaggio()+"] OUTBOX attendiTerminazioneRichiesta["+attendiTerminazioneRichiesta+"] PROPRIETARIO["+proprietario+"]");
						}*/
					}else{
						break; // si tratta di una ricevuta alla richiesta o alla risposta asincrona simmetrica in un caso di newConnectionForResponse
					}
				}else{
					break; // non si tratta di una risposta asincrona simmetrica, ma di una richiesta
				}
			}else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {
				if( (bustaRichiesta.getRiferimentoMessaggio()!=null) || 
						( bustaRichiesta.getCollaborazione()!=null && (!bustaRichiesta.getCollaborazione().equals(bustaRichiesta.getID())) ) 
				){

					if( 
							( !newConnectionForResponse ) 
							|| 
							( (!isRicevutaRichiesta) && (!isRicevutaRisposta) ) 
					){	
						String idRichiesta = bustaRichiesta.getRiferimentoMessaggio();
						if(idRichiesta==null){
							// LineeGuida non sara' presente il riferimentoMessaggio ma l'elemento collaborazione
							idRichiesta = bustaRichiesta.getCollaborazione();
						}
						GestoreMessaggi checkRichiesta = 
							new GestoreMessaggi(openspcoopstate, false, idRichiesta,Costanti.INBOX,msgDiag,pddContext);
						attendiTerminazioneRichiesta =  checkRichiesta.existsMessageInProcessamento();
						if(!attendiTerminazioneRichiesta)
							break;
						/**else{
							String proprietario = checkRichiesta.getProprietario(RicezioneBuste.ID_MODULO);
							msgDiag.infoOpenSPCoop("RICHIESTA  ID["+bustaRichiesta.getRiferimentoMessaggio()+"] INBOX attendiTerminazioneRichiesta["+attendiTerminazioneRichiesta+"] PROPRIETARIO["+proprietario+"]");
						}*/
					}else{
						break; // si tratta di una ricevuta alla richiesta o risposta asincrona asimmetrica in un caso di newConnectionForResponse
					}
				}else{
					break; // non si tratta di una richiesta-stato asincrona asimmetrica, ma di una richiesta
				}
			}else{
				break;
			}

			if(bustaRichiesta.getProfiloDiCollaborazione()!=null){
				msgDiag.mediumDebug("Busta di risposta con profilo ["+bustaRichiesta.getProfiloDiCollaborazione().getEngineValue()+"] non gestibile, si attende il completamento" +
						" della gestione della richiesta");
			}
			else{
				msgDiag.mediumDebug("Busta di risposta con profilo null?? non gestibile, si attende il completamento" +
						" della gestione della richiesta");
			}

			Utilities.sleep(checkInterval);
		}
		return attendiTerminazioneRichiesta;
	}

	private boolean gestioneRispostaAsincronaCheckPresenzaRicevutaRichiesta(long scadenzaControllo,int checkInterval,Busta bustaRichiesta,
			IOpenSPCoopState openspcoopstate,MsgDiagnostico msgDiag,boolean newConnectionForResponse,
			PdDContext pddContext) throws ProtocolException, GestoreMessaggiException{
		boolean attendiTerminazioneRicevutaRichiesta = false;

		if(this.protocolFactory==null) {
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
		}
		
		// Puo' potenzialmente essere una ricevuta alla richiesta (new connection for response)
		// o alla risposta (e cmq la risposta l'ho generata io e quindi e' chiaro che ho finito di gestire la richiesta)
		boolean isRicevutaRichiesta = false;
		boolean isRicevutaRisposta = false;
		if(newConnectionForResponse &&
			( ((org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) 
					||
					(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()))) &&
					bustaRichiesta.getRiferimentoMessaggio()!=null) 
			){
			// Le ricevute hanno il riferimento messaggio anche nelle linee guida!
			ProfiloDiCollaborazione profiloCollaborazione = new ProfiloDiCollaborazione(openspcoopstate.getStatoRichiesta(),this.protocolFactory);
			isRicevutaRichiesta = profiloCollaborazione.asincrono_isRicevutaRichiesta(bustaRichiesta.getRiferimentoMessaggio());
			isRicevutaRisposta =  profiloCollaborazione.asincrono_isRicevutaRisposta(bustaRichiesta.getRiferimentoMessaggio());
		}

		while( DateManager.getTimeMillis() < scadenzaControllo ){

			msgDiag.mediumDebug("RICEVUTA RICHIESTA  NOW["+DateManager.getTimeMillis()+"] < SCADENZA["+scadenzaControllo+"]");

			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	
				if( (bustaRichiesta.getRiferimentoMessaggio()!=null) || 
						( bustaRichiesta.getCollaborazione()!=null && (!bustaRichiesta.getCollaborazione().equals(bustaRichiesta.getID())) ) 
				){

					if( 
							( !newConnectionForResponse ) 
							|| 
							( (!isRicevutaRichiesta) && (!isRicevutaRisposta) ) 
					){
						String idRicevutaRichiesta = bustaRichiesta.getRiferimentoMessaggio();
						if(idRicevutaRichiesta==null){
							// LineeGuida non sara' presente il riferimentoMessaggio ma l'elemento collaborazione
							idRicevutaRichiesta = bustaRichiesta.getCollaborazione();
						}
						GestoreMessaggi checkRicevutaRichiesta = new GestoreMessaggi(openspcoopstate, false, idRicevutaRichiesta,Costanti.INBOX,msgDiag,pddContext);
						attendiTerminazioneRicevutaRichiesta =  checkRicevutaRichiesta.existsMessageInProcessamentoByReference();
						if(!attendiTerminazioneRicevutaRichiesta)
							break;
						/**else{
							String proprietario = checkRicevutaRichiesta.getProprietario(RicezioneBuste.ID_MODULO);
							msgDiag.infoOpenSPCoop("RICHIESTA  ID["+bustaRichiesta.getRiferimentoMessaggio()+"] INBOX attendiTerminazioneRichiesta["+attendiTerminazioneRicevutaRichiesta+"] PROPRIETARIO["+proprietario+"]");
						}*/
					}else{
						break; // si tratta di una ricevuta alla richiesta o risposta asincrona simmetrica in un caso di newConnectionForResponse
					}
				}else{
					break; // non si tratta di una risposta asincrona simmetrica, ma di una richiesa
				}
			}else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {
				if( (bustaRichiesta.getRiferimentoMessaggio()!=null) || 
						( bustaRichiesta.getCollaborazione()!=null && (!bustaRichiesta.getCollaborazione().equals(bustaRichiesta.getID())) ) 
				){

					if( 
							( !newConnectionForResponse ) 
							|| 
							( (!isRicevutaRichiesta) && (!isRicevutaRisposta) ) 
					){
						String idRicevutaRichiesta = bustaRichiesta.getRiferimentoMessaggio();
						if(idRicevutaRichiesta==null){
							// LineeGuida non sara' presente il riferimentoMessaggio ma l'elemento collaborazione
							idRicevutaRichiesta = bustaRichiesta.getCollaborazione();
						}
						GestoreMessaggi checkRicevutaRichiesta = new GestoreMessaggi(openspcoopstate, false, idRicevutaRichiesta,Costanti.OUTBOX,msgDiag,pddContext);
						attendiTerminazioneRicevutaRichiesta =  checkRicevutaRichiesta.existsMessageInProcessamentoByReference();
						if(!attendiTerminazioneRicevutaRichiesta)
							break;
						/**else{
							String proprietario = checkRicevutaRichiesta.getProprietario(RicezioneBuste.ID_MODULO);
							msgDiag.infoOpenSPCoop("RICHIESTA  ID["+bustaRichiesta.getRiferimentoMessaggio()+"] OUTBOX attendiTerminazioneRichiesta["+attendiTerminazioneRicevutaRichiesta+"] PROPRIETARIO["+proprietario+"]");
						}*/
					}else{
						break; // si tratta di una ricevuta alla richiesta o risposta asincrona asimmetrica in un caso di newConnectionForResponse
					}
				}else{
					break; // non si tratta di una richiesta-stato asincrona asimmetrica, ma di una richiesta
				}
			}else{
				break;
			}

			if(bustaRichiesta.getProfiloDiCollaborazione()!=null)
				msgDiag.mediumDebug("Busta di risposta con profilo ["+bustaRichiesta.getProfiloDiCollaborazione().getEngineValue()+"] non gestibile, si attende il completamento" +
				" della gestione della ricevuta alla richiesta");
			else{
				msgDiag.mediumDebug("Busta di risposta con profilo null??? non gestibile, si attende il completamento" +
						" della gestione della ricevuta alla richiesta");
			}

			Utilities.sleep(checkInterval);
		}

		return attendiTerminazioneRicevutaRichiesta;
	}


	

	private EsitoLib chiamaLibreria(OpenSPCoopStateless openspcoopstate, Logger log,
			RegistroServiziManager registroServiziManager, ConfigurazionePdDManager configurazionePdDManager) throws OpenSPCoopStateException, GenericLibException{

		/* --------------------------- SBUSTAMENTO ------------------------ */
		if (openspcoopstate.getDestinatarioRequestMsgLib().startsWith(Sbustamento.ID_MODULO)) {
			Sbustamento lib = new Sbustamento(log);
			return lib.onMessage(openspcoopstate, registroServiziManager, configurazionePdDManager);	
		}

		/* ---------------------------ConsegnaContenutiApplicativi----------------------------- */
		else if (openspcoopstate.getDestinatarioRequestMsgLib().startsWith(ConsegnaContenutiApplicativi.ID_MODULO)) {
			ConsegnaContenutiApplicativi lib = new ConsegnaContenutiApplicativi(log);
			EsitoLib result = lib.onMessage(openspcoopstate, registroServiziManager, configurazionePdDManager, 
					this.asyncResponseCallback!=null ? this : null);
			if(this.asyncResponseCallback!=null) {
				this.asyncWait = true;
			}
			this.esitoStatelessAfterSendRequest = result;
			if (result.getStatoInvocazione()==EsitoLib.OK) openspcoopstate.setDestinatarioRequestMsgLib("");
			return result;
		}

		/* ----------------------        -ImbustamentoRisposte         ------------------------------- */
		else if (openspcoopstate.getDestinatarioResponseMsgLib().startsWith(ImbustamentoRisposte.ID_MODULO)) {
			ImbustamentoRisposte lib = new ImbustamentoRisposte(log);
			return lib.onMessage(openspcoopstate, registroServiziManager, configurazionePdDManager);
		}

		/* ---------------------       -InoltroRisposte----------------------------- */
		else if (openspcoopstate.getDestinatarioResponseMsgLib().startsWith(InoltroRisposte.ID_MODULO)) {
			InoltroRisposte lib = new InoltroRisposte(log);
			return lib.onMessage(openspcoopstate, registroServiziManager, configurazionePdDManager);
		}

		else throw new OpenSPCoopStateException(RicezioneBuste.ID_MODULO + ".chiamaLibreria: nome libreria non valido");

	}
	
	
	private IDServizio getIdServizioPerAutorizzazione(IDServizio idServizio,IDSoggetto soggettoFruitore,
			boolean functionAsRouter,Busta bustaRichiesta,RuoloBusta ruoloBustaRicevuta) throws CoreException, DriverRegistroServiziException {


		IDSoggetto soggettoDestinatarioPerAutorizzazione = null;
		if(functionAsRouter){
			soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
					idServizio.getSoggettoErogatore().getCodicePorta());
		}
		else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
				org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
			soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
					idServizio.getSoggettoErogatore().getCodicePorta());
		}else if (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ){
			if(ruoloBustaRicevuta==null){
				// router
				soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
						idServizio.getSoggettoErogatore().getCodicePorta());
			}
			else{
				if(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString()) || RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
					soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
							idServizio.getSoggettoErogatore().getCodicePorta());
				}else{
					if(soggettoFruitore==null){
						throw new CoreException(SOGGETTO_FRUITORE_NON_IDENTIFICATO);
					}
					soggettoDestinatarioPerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
							soggettoFruitore.getCodicePorta());
				}	
			}
		}else if (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ){
			if(ruoloBustaRicevuta==null){
				// router
				soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
						idServizio.getSoggettoErogatore().getCodicePorta());
			}
			else{
				if(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString()) || RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
					soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
							idServizio.getSoggettoErogatore().getCodicePorta());
				}else{
					if(soggettoFruitore==null){
						throw new CoreException(SOGGETTO_FRUITORE_NON_IDENTIFICATO);
					}
					soggettoDestinatarioPerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
							soggettoFruitore.getCodicePorta());
				}
			}
		}else{
			soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
					idServizio.getSoggettoErogatore().getCodicePorta());
		}

		IDServizio idServizioPerAutorizzazione = IDServizioFactory.getInstance().
				getIDServizioFromValues(idServizio.getTipo(), idServizio.getNome(), soggettoDestinatarioPerAutorizzazione, idServizio.getVersione());
		idServizioPerAutorizzazione.setUriAccordoServizioParteComune(idServizio.getUriAccordoServizioParteComune());
		idServizioPerAutorizzazione.setAzione(idServizio.getAzione());
		idServizioPerAutorizzazione.setTipologia(idServizio.getTipologia());
		
		return idServizioPerAutorizzazione;
	}
	
	private IDSoggetto getIDSoggettoMittentePerAutorizzazione(IDServizio idServizio,IDSoggetto soggettoFruitore,
			boolean functionAsRouter,Busta bustaRichiesta,RuoloBusta ruoloBustaRicevuta, boolean supportatoAutenticazioneSoggetti) throws CoreException{
		IDSoggetto soggettoMittentePerAutorizzazione = null;
		if(functionAsRouter){
			if(soggettoFruitore==null){
				throw new CoreException(SOGGETTO_FRUITORE_NON_IDENTIFICATO);
			}
			soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
					soggettoFruitore.getCodicePorta());
		}
		else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
				org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
			if(soggettoFruitore==null){
				if(supportatoAutenticazioneSoggetti){
					return null; // invocazione anonima
				}else{
					throw new CoreException(SOGGETTO_FRUITORE_NON_IDENTIFICATO);		
				}
			}
			soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
					soggettoFruitore.getCodicePorta());
		}else if (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ){
			if(ruoloBustaRicevuta==null){
				if(soggettoFruitore==null){
					if(supportatoAutenticazioneSoggetti){
						return null; // invocazione anonima
					}else{
						throw new CoreException(SOGGETTO_FRUITORE_NON_IDENTIFICATO);		
					}
				}
				soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
						soggettoFruitore.getCodicePorta());
			}else{
				if(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString()) || RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
					if(soggettoFruitore==null){
						if(supportatoAutenticazioneSoggetti){
							return null; // invocazione anonima
						}else{
							throw new CoreException(SOGGETTO_FRUITORE_NON_IDENTIFICATO);		
						}
					}
					soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
							soggettoFruitore.getCodicePorta());
				}else{
					soggettoMittentePerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
							idServizio.getSoggettoErogatore().getCodicePorta());
				}		
			}
		}else if (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ){
			if(ruoloBustaRicevuta==null){
				if(soggettoFruitore==null){
					if(supportatoAutenticazioneSoggetti){
						return null; // invocazione anonima
					}else{
						throw new CoreException(SOGGETTO_FRUITORE_NON_IDENTIFICATO);		
					}
				}
				soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
						soggettoFruitore.getCodicePorta());
			}else{
				if(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString()) || RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
					if(soggettoFruitore==null){
						if(supportatoAutenticazioneSoggetti){
							return null; // invocazione anonima
						}else{
							throw new CoreException(SOGGETTO_FRUITORE_NON_IDENTIFICATO);		
						}
					}
					soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
							soggettoFruitore.getCodicePorta());
				}else{
					soggettoMittentePerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
							idServizio.getSoggettoErogatore().getCodicePorta());
				}
			}
		}else{
			if(soggettoFruitore==null){
				if(supportatoAutenticazioneSoggetti){
					return null; // invocazione anonima
				}else{
					throw new CoreException(SOGGETTO_FRUITORE_NON_IDENTIFICATO);		
				}
			}
			soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
					soggettoFruitore.getCodicePorta());
		}
		return soggettoMittentePerAutorizzazione;
	}
	
	
	@SuppressWarnings("deprecation")
	private void overwriteIdSoggetto(IDServizio idServizio, IDSoggetto idSoggetto){
		idServizio.setSoggettoErogatore(idSoggetto);
	}
	@SuppressWarnings("deprecation")
	private void cleanDatiServizio(IDServizio idServizio){
		idServizio.setVersione(null);
		idServizio.setNome(null);
		idServizio.setTipo(null);
	}
}


