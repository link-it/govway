/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import org.jminix.console.tool.StandaloneMiniConsole;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.AccessoDatiKeystore;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.constants.CacheAlgorithm;
import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.constants.CodiceEventoStatoGateway;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.core.eventi.constants.TipoSeverita;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.message.AttachmentsProcessingMode;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageFactory_impl;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.DBStatisticheManager;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.GeneralInstanceProperties;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.PddProperties;
import org.openspcoop2.pdd.config.PreLoadingConfig;
import org.openspcoop2.pdd.config.QueueManager;
import org.openspcoop2.pdd.config.SystemPropertiesManager;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.FileSystemSerializer;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.StatoServiziPdD;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;
import org.openspcoop2.pdd.core.autorizzazione.GestoreAutorizzazione;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.GestoreLoadBalancerCaching;
import org.openspcoop2.pdd.core.controllo_traffico.GestoreControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.NotificatoreEventi;
import org.openspcoop2.pdd.core.controllo_traffico.policy.DatiStatisticiDAOManager;
import org.openspcoop2.pdd.core.controllo_traffico.policy.GestoreCacheControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.GestorePolicyAttive;
import org.openspcoop2.pdd.core.eventi.GestoreEventi;
import org.openspcoop2.pdd.core.handlers.ExitContext;
import org.openspcoop2.pdd.core.handlers.GeneratoreCasualeDate;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InitContext;
import org.openspcoop2.pdd.core.jmx.AccessoRegistroServizi;
import org.openspcoop2.pdd.core.jmx.ConfigurazioneSistema;
import org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX;
import org.openspcoop2.pdd.core.jmx.InformazioniStatoPorta;
import org.openspcoop2.pdd.core.jmx.InformazioniStatoPortaCache;
import org.openspcoop2.pdd.core.jmx.StatoServiziJMXResource;
import org.openspcoop2.pdd.core.keystore.GestoreKeystoreCaching;
import org.openspcoop2.pdd.core.response_caching.GestoreCacheResponseCaching;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.filetrace.FileTraceConfig;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.services.core.RicezioneBuste;
import org.openspcoop2.pdd.services.core.RicezioneContenutiApplicativi;
import org.openspcoop2.pdd.services.skeleton.IntegrationManager;
import org.openspcoop2.pdd.timers.TimerConsegnaContenutiApplicativiThread;
import org.openspcoop2.pdd.timers.TimerEventiThread;
import org.openspcoop2.pdd.timers.TimerFileSystemRecoveryThread;
import org.openspcoop2.pdd.timers.TimerGestoreBusteNonRiscontrate;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggiThread;
import org.openspcoop2.pdd.timers.TimerGestorePuliziaMessaggiAnomali;
import org.openspcoop2.pdd.timers.TimerGestorePuliziaMessaggiAnomaliThread;
import org.openspcoop2.pdd.timers.TimerGestoreRepositoryBuste;
import org.openspcoop2.pdd.timers.TimerGestoreRepositoryBusteThread;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerRepositoryStatefulThread;
import org.openspcoop2.pdd.timers.TimerStatisticheThread;
import org.openspcoop2.pdd.timers.TimerThresholdThread;
import org.openspcoop2.pdd.timers.TimerUtils;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.driver.repository.IGestoreRepository;
import org.openspcoop2.protocol.manifest.constants.ServiceBinding;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.registry.RegistroServiziReader;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.message.engine.MessageSecurityFactory;
import org.openspcoop2.security.utils.ExternalPWCallback;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.resources.FileSystemMkdirConfig;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;



/**
 * Implementazione del punto di Startup dell'applicazione WEB
 * 
 * @author Marcello Spadafora (ma.spadafora@finsiel.it)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class OpenSPCoop2Startup implements ServletContextListener {

	/** Logger utilizzato per segnalazione di errori. */
	private static Logger log = LoggerWrapperFactory.getLogger("govway.startup");

	/** Variabile che indica il Nome del modulo attuale di OpenSPCoop */
	private static final String ID_MODULO = "InizializzazioneRisorse";

	/** Indicazione se sta avvendendo un contextDestroyed */
	public static boolean contextDestroyed = false;

	/** Context della Servlet */
	ServletContext servletContext;

	/** Indicazione su una corretta inizializzazione */
	public static boolean initialize = false;
	
	/** Indicazione su una corretta inizializzazione della configurazione del Logging (Utile per l'integrazione con il pool utils via reflection) */
	public static boolean initializeLog = false;

	/** Timer per la gestione della funzionalita' 'RiscontriScaduti' */
	private TimerGestoreBusteNonRiscontrate timerRiscontri;

	/** Timer per l'eliminazione dei messaggi gestiti */
	private TimerGestoreMessaggi timerEliminazioneMsg;
	private TimerGestoreMessaggiThread threadEliminazioneMsg;

	/** Timer per l'eliminazione dei messaggi anomali */
	private TimerGestorePuliziaMessaggiAnomali timerPuliziaMsgAnomali;
	private TimerGestorePuliziaMessaggiAnomaliThread threadPuliziaMsgAnomali;

	/** Timer per l'eliminazione delle buste gestite */
	private TimerGestoreRepositoryBuste timerRepositoryBuste;
	private TimerGestoreRepositoryBusteThread threadRepositoryBuste;

	/** Gestore Threshold */
	private TimerThresholdThread timerThreshold = null;

	/** Gestore Monitoraggio Risorse */
	private TimerMonitoraggioRisorseThread timerMonitoraggioRisorse = null;

	/** Timer per la gestione di riconsegna ContenutiApplicativi */
	private TimerConsegnaContenutiApplicativiThread threadConsegnaContenutiApplicativi;
	public static TimerConsegnaContenutiApplicativiThread threadConsegnaContenutiApplicativiRef;
	
	/** Gestore risorse JMX */
	private GestoreRisorseJMX gestoreRisorseJMX = null;
	public static GestoreRisorseJMX gestoreRisorseJMX_staticInstance = null;
	
	/** Gestore eventi */
	private GestoreEventi gestoreEventi = null;
	private TimerEventiThread threadEventi;
	
	/** Timer FileSystemRecovery */
	private TimerFileSystemRecoveryThread threadFileSystemRecovery = null;
	
	/** Timer FileSystemRecovery */
	private TimerRepositoryStatefulThread threadRepositoryStateful = null;
	
	/** Timer per la generazione delle statistiche */
	private TimerStatisticheThread threadGenerazioneStatisticheOrarie;
	private TimerStatisticheThread threadGenerazioneStatisticheGiornaliere;
	private TimerStatisticheThread threadGenerazioneStatisticheSettimanali;
	private TimerStatisticheThread threadGenerazioneStatisticheMensili;
	
	/** indicazione se è un server j2ee */
	private boolean serverJ2EE = false;
	protected long startDate ;

	/** PdDContext */
	private PdDContext pddContext = new PdDContext();
	
	/** OpenSPCoopStartupThread */
	private OpenSPCoopStartupThread th;
	
	/** Jminix StandaloneMiniConsole */
	private static StandaloneMiniConsole jminixStandaloneConsole;

	/**
	 * Startup dell'applicazione WEB di OpenSPCoop
	 *
	 * @param sce Servlet Context Event
	 * 
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {

		OpenSPCoop2Startup.contextDestroyed = false;
		
		this.startDate = System.currentTimeMillis();
		
		/* ------  Ottiene il servletContext --------*/
		this.servletContext = sce.getServletContext();
		this.th = new OpenSPCoopStartupThread();
		new Thread(this.th).start();

	}

	class OpenSPCoopStartupThread implements Runnable {

		private void logError(String msg){
			this.logError(msg,null);
		}
		private void logError(String msg,Exception e){
			if(e==null)
				OpenSPCoop2Startup.log.error(msg);
			else
				OpenSPCoop2Startup.log.error(msg,e);
			if(OpenSPCoop2Logger.isLoggerOpenSPCoopConsoleStartupAgganciatoLog()){
				// per farlo finire anche sul server.log
				System.err.println(msg);
				if(e!=null){
					e.printStackTrace(System.err);
				}
			}
		}
		

		public OpenSPCoopStartupThread() {                        
		}

		@Override
		public void run() {

			
			
			
			/* ------------ LogConsole -------------------- */
			// Inizializza lo startup log, utile per web spheare
			if(OpenSPCoop2Logger.initializeLogConsole(OpenSPCoop2Startup.log) == false){
				return;
			}
			OpenSPCoop2Startup.log = LoggerWrapperFactory.getLogger("govway.startup");
			
			
			
			
			


			
					
			
			
			
			

			/* ------------- Inizializzo ClassNameProperties di OpenSPCoop --------------- */
			if( ClassNameProperties.initialize(true) == false){
				this.logError("Riscontrato errore durante l'inizializzazione del reader di 'govway.classRegistry.properties'");
				return;
			}
			ClassNameProperties classNameReader = ClassNameProperties.getInstance();




			
			
			
			
			
			
			/* ------------- Loader --------------- */
			GeneralInstanceProperties instanceProperties = new GeneralInstanceProperties();
			Object [] o = null;
			Properties openspcoopP = null;
			Properties classNameP = null;
			Properties loggerP = null;
			Properties localConfig = null;
			Properties cacheP = null;
			try{
				o = instanceProperties.reads(OpenSPCoop2Startup.log);
			}catch(Exception e){
				e.printStackTrace();
			}
			try{
				if(o!=null){
					Loader.initialize((java.lang.ClassLoader)o[0]);
					try{
						openspcoopP=(Properties)o[1];
						classNameP=(Properties)o[2];
						loggerP = (Properties)o[4];
						localConfig = (Properties)o[5];
						cacheP = (Properties)o[7];
					}catch(Exception e){}
				}else{
					Loader.initialize();
				}
			}catch(Exception e){
				this.logError("Loader non istanziato: "+e.getMessage(),e);
				return;
			}

			
			







			/* ------------- Verifica Proprieta' di OpenSPCoop --------------- */
			if( OpenSPCoop2Properties.initialize(openspcoopP) == false){
				this.logError("Riscontrato errore durante l'inizializzazione del reader di 'govway.properties'");
				return;
			}
			OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
			try{
				propertiesReader.checkOpenSPCoopHome();
			}catch(Exception e){
				this.logError(e.getMessage(),e);
				if(StatoFunzionalitaConWarning.ABILITATO.equals(propertiesReader.getCheckOpenSPCoopHome())){
					return;
				}
			}
			classNameReader.refreshLocalProperties(classNameP,propertiesReader.getRootDirectory()); // prima della validazione
			if(o!=null){
				if(propertiesReader.validaConfigurazione((java.lang.ClassLoader)o[0]) == false){
					return;
				}
				if(classNameReader.validaConfigurazione((java.lang.ClassLoader)o[0], propertiesReader.getDatabaseType()) == false){
					return;
				}
			}else{
				if(propertiesReader.validaConfigurazione(null) == false){
					return;
				}
				if(classNameReader.validaConfigurazione(null, propertiesReader.getDatabaseType()) == false){
					return;
				}
			}
			
			OpenSPCoop2Startup.this.serverJ2EE = propertiesReader.isServerJ2EE();
			if(propertiesReader.getClassLoader()!=null){
				try{
					Loader.update(propertiesReader.getClassLoader());
				}catch(Exception e){
					this.logError("Loader non aggiornato: "+e.getMessage(),e);
					return;
				}	
			}
			Loader loader = Loader.getInstance();


			
			
			
			
			
			
			
			/* ------------- Inizializzo Errori di OpenSPCoop --------------- */
			try {
				ErroriProperties.initialize(propertiesReader.getRootDirectory(), log, loader);
			}catch(Exception e) {
				this.logError("Riscontrato errore durante l'inizializzazione del reader 'errori.properties'");
				return;
			}
			
			
			
			
			
			
			
			
			/*
			 * 	Necessario in jboss7 per evitare errore 'error constructing MAC: java.lang.SecurityException: JCE cannot authenticate the provider BC'
			 *  se vengono utilizzati keystore P12.
			 *  Il codice  
			 *  	<resource-root path="WEB-INF/lib/bcprov-ext-jdk15on-1.64.jar" use-physical-code-source="true"/>
			 *  all'interno del file jboss-deployment-structure.xml non è più sufficiente da quanto è stato necessario
			 *  introdurre il codice sottostante 'org.apache.wss4j.dom.engine.WSSConfig.init' 
			 *  e di conseguenza tutta la configurazione del modulo 'deployment.custom.javaee.api'
			 *  per risolvere il problema java.lang.NoSuchMethodError: org.apache.xml.security.utils.I18n.init 
			 */
			// NOTA: il caricamento di BouncyCastleProvider DEVE essere effettuato prima dell'inizializzazione 'org.apache.wss4j.dom.engine.WSSConfig.init' 
			if(propertiesReader.isLoadBouncyCastle()){ 
				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
				OpenSPCoop2Startup.log.info("Aggiunto Security Provider org.bouncycastle.jce.provider.BouncyCastleProvider");
			}
			
			/* ------------ 
			 * Inizializzazione Resource Bundle:
			 * - org/apache/xml/security/resource/xmlsecurity_en.properties (xmlsec-2.1.2.jar)
			 * - org/apache/xml/security/resource/xmlsecurity_de.properties (xmlsec-2.1.2.jar)
			 * - messages/wss4j_errors.properties (wss4j-ws-security-common-2.2.2.jar)
			 * 
			 * L'inizializzazione di questa classe DEVE essere all'inizio altrimenti si puo' incorrere in errori tipo il seguente:
			 * Caused by: org.apache.wss4j.common.ext.WSSecurityException: No message with ID "noUserCertsFound" found in resource bundle "org/apache/xml/security/resource/xmlsecurity"
			 *
			 * Il motivo risiede nel fatto che org.apache.wss4j.common.ext.WSSecurityException lancia una eccezione con id "noUserCertsFound".
			 * Tale eccezione di fatto estende la classe org/apache/xml/security/exceptions/XMLSecurityException che utilizza il proprio resource bundle
			 * per risolvere l'id. Tale classe utilizza normalmente il properties 'org/apache/xml/security/resource/xmlsecurity_en.properties'
			 * Mentre l'id 'noUserCertsFound' e' dentro il properties 'messages/wss4j_errors.properties'
			 * Pero' xmlsec permette di inizializzare il resource bundle da usare anche grazie ad un metodo dove viene fornito l'intero resource bundle.
			 * Questo avviene in xmlsec-2.0.7/src/main/java/org/apache/xml/security/utils/I18n.java metodo init(ResourceBundle resourceBundle)
			 * L'inizializzazione avviene pero' solamente una volta. Quindi se qualche altra libreria l'inizializza prima, poi il metodo init diventa una nop.
			 * Tale init viene quindi richiamata dalla classe org.apache.wss4j.dom.engine.WSSConfig.init che prepara un resource bundle
			 * contenente sia il contenuto originale del properties 'org/apache/xml/security/resource/xmlsecurity_en.properties' che 
			 * aggiungendo il contenuto del properties 'messages/wss4j_errors.properties'
			 *
			 * -------------------- */
			try{
				org.apache.wss4j.dom.engine.WSSConfig.init();
			}catch(Exception e){
				this.logError("Inizializzazione org.apache.wss4j.dom.engine.WSSConfig.init",e);
				return;
			}
			
			/* ------------ 
			 * Disabilita il log di errore prodotto da freemarker
			 * https://freemarker.apache.org/docs/api/freemarker/log/Logger.html#SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY
			 * deprecato: freemarker.log.Logger.selectLoggerLibrary(freemarker.log.Logger.LIBRARY_NONE);
			 */
			try{
				System.setProperty(freemarker.log.Logger.SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY,freemarker.log.Logger.LIBRARY_NAME_NONE);
			}catch(Exception e){
				this.logError("Inizializzazione org.apache.wss4j.dom.engine.WSSConfig.init",e);
				return;
			}
			
			
			
			
			



			/* ------------- Inizializzo il sistema di Logging di OpenSPCoop --------------- */
			boolean isInitializeLogger = false;
			isInitializeLogger = OpenSPCoop2Logger.initialize(OpenSPCoop2Startup.log,propertiesReader.getRootDirectory(),loggerP);
			if(isInitializeLogger == false){
				return;
			}
			Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			OpenSPCoop2Startup.log = LoggerWrapperFactory.getLogger("govway.startup");
			
			Utilities.log = logCore;
			Utilities.freeMemoryLog = propertiesReader.getFreeMemoryLog();
			
			OpenSPCoop2Startup.initializeLog = true;
			
			if(propertiesReader.isLoggerSaajDisabilitato()) {
				//java.util.logging.Logger logSaaj = java.util.logging.Logger.getLogger(com.sun.xml.messaging.saaj.util.LogDomainConstants.SOAP_DOMAIN
				//																		"com.sun.xml.messaging.saaj.soap.LocalStrings");
				java.util.logging.Logger logSaaj = java.util.logging.Logger.getLogger(com.sun.xml.messaging.saaj.util.LogDomainConstants.MODULE_TOPLEVEL_DOMAIN);
				logSaaj.setLevel(Level.OFF);
				logSaaj.severe("Il logger utilizzato nel package '"+com.sun.xml.messaging.saaj.util.LogDomainConstants.MODULE_TOPLEVEL_DOMAIN+"' e' stato disabilitato; questo messaggio non deve essere visualizzato");
				logSaaj.severe("SAAJ0511.soap.cannot.create.envelope"); // serve per caricare il logger con il local string
				OpenSPCoop2Startup.log.info("Il logger utilizzato nel package '"+com.sun.xml.messaging.saaj.util.LogDomainConstants.MODULE_TOPLEVEL_DOMAIN+"' e' stato disabilitato");
			}

			
			
			


			/* ------------- Pdd.properties --------------- */
			String locationPddProperties = null;
			if( propertiesReader.getLocationPddProperties()!=null ){
				locationPddProperties = propertiesReader.getLocationPddProperties();
			}
			if( PddProperties.initialize(locationPddProperties,propertiesReader.getRootDirectory()) == false){
				this.logError("Riscontrato errore durante l'inizializzazione del reader di 'govway.pdd.properties'");
				return;
			}
			if(o!=null){
				try{
					PddProperties.updateLocalImplementation((Properties)o[3]);
				}catch(Exception e){}
			}
			try{
				OpenSPCoop2Properties.updatePddPropertiesReader(PddProperties.getInstance());
			}catch(Exception e){
				this.logError("Riscontrato errore durante l'assegnamento del pddPropertiesReader a OpenSPCoopPropertiesReader: "+e.getMessage(),e);
				return;
			}










			/* ------------- MsgDiagnostici.properties --------------- */
			if( MsgDiagnosticiProperties.initialize(null,propertiesReader.getRootDirectory()) == false){
				this.logError("Riscontrato errore durante l'inizializzazione del reader di 'govway.msgDiagnostici.properties'");
				return;
			}
			if(o!=null){
				try{
					MsgDiagnosticiProperties.updateLocalImplementation((Properties)o[6]);
				}catch(Exception e){}
			}
			MsgDiagnosticiProperties msgDiagProperties = MsgDiagnosticiProperties.getInstance();
			if(msgDiagProperties.initializeMsgDiagnosticiPersonalizzati() == false){
				return;
			}





			
			
			
			/* ------------- Cache.properties --------------- */
			boolean isInitializeCache = Cache.initialize(OpenSPCoop2Startup.log, logCore, 
					CostantiPdD.OPENSPCOOP2_CACHE_DEFAULT_PROPERTIES_NAME,
					propertiesReader.getRootDirectory(), cacheP, 
					CostantiPdD.OPENSPCOOP2_LOCAL_HOME, CostantiPdD.OPENSPCOOP2_CACHE_PROPERTIES, CostantiPdD.OPENSPCOOP2_CACHE_LOCAL_PATH);
			if(isInitializeCache == false){
				this.logError("Riscontrato errore durante l'inizializzazione della cache di OpenSPCoop.");
				return;
			}
			
			
			
			
			/* ------------- Inizializzo Configurazione FileTrace --------------- */
			try {
				if(propertiesReader.isTransazioniFileTraceEnabled()) {
					FileTraceConfig.init(propertiesReader.getTransazioniFileTraceConfig());
				}
			}catch(Exception e) {
				this.logError("Riscontrato errore durante l'inizializzazione del FileTrace");
				return;
			}

			
			
			
			/* ----------- Inizializzazione Risorse DOM/SOAP ------------ */
			List<OpenSPCoop2MessageFactory> messageFactory = new ArrayList<OpenSPCoop2MessageFactory>();
			try{
				// TransazioneContext
				TransactionContext.initGestioneStateful();
				OpenSPCoop2Startup.log.info("TransactionContext.gestioneStateful: "+propertiesReader.isTransazioniStatefulEnabled());
				TransactionContext.initResources();
				OpenSPCoop2Startup.log.info("TransactionContext.type (sync:"+propertiesReader.isConfigurazioneCache_transactionContext_accessiSynchronized()+"): "+TransactionContext.getTransactionContextType());
				
				// Cache
				if(!propertiesReader.isConfigurazioneCache_accessiSynchronized()) {
					Cache.disableSyncronizedGetAsDefault();
				}
				OpenSPCoop2Startup.log.info("Cache.disableSyncronizedGetAsDefault: "+Cache.isDisableSyncronizedGetAsDefault());
				
				// MessageFactory
				OpenSPCoop2MessageFactory.setMessageFactoryImpl(classNameReader.getOpenSPCoop2MessageFactory(propertiesReader.getOpenspcoop2MessageFactory()));
				OpenSPCoop2MessageFactory.initDefaultMessageFactory(true);
								
				// MessageSecurity
				MessageSecurityFactory.setMessageSecurityContextClassName(classNameReader.getMessageSecurityContext(propertiesReader.getMessageSecurityContext()));
				MessageSecurityFactory.setMessageSecurityDigestReaderClassName(classNameReader.getMessageSecurityDigestReader(propertiesReader.getMessageSecurityDigestReader()));
				
				List<String> tipiMessageFactory = new ArrayList<String>();
				List<String> classiMessageFactory = new ArrayList<String>();
				String factoryDefault = "@DEFAULT@";
				tipiMessageFactory.add(factoryDefault);
				OpenSPCoop2MessageFactory defaultMessageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
				classiMessageFactory.add(defaultMessageFactory.getClass().getName());
				messageFactory.add(defaultMessageFactory);
				
				String [] tmp_tipiMessageFactory = classNameReader.getOpenSPCoop2MessageFactory();
				if(tmp_tipiMessageFactory!=null && tmp_tipiMessageFactory.length>0) {
					OpenSPCoop2Startup.log.info("Analizzo "+tmp_tipiMessageFactory.length+" message factories ...");
					for (int i = 0; i < tmp_tipiMessageFactory.length; i++) {
						String tipo = tmp_tipiMessageFactory[i];
						String classe = classNameReader.getOpenSPCoop2MessageFactory(tipo);
						if(!classiMessageFactory.contains(classe)) {
							OpenSPCoop2MessageFactory factory = (OpenSPCoop2MessageFactory) loader.newInstance(classe);
							tipiMessageFactory.add(tipo);
							messageFactory.add(factory);
							classiMessageFactory.add(classe);
							OpenSPCoop2Startup.log.info("Registrazione '"+tipo+"' corrispondente alla classe '"+classe+"' terminata");
						}
						else {
							OpenSPCoop2Startup.log.info("Registrazione '"+tipo+"' corrispondente alla classe '"+classe+"' non effettuata. La stessa classe risulta già associata ad altri tipi.");
						}
					}
				}
				
				for (int l = 0; l < tipiMessageFactory.size(); l++) {
					String tipo = tipiMessageFactory.get(l);
					String classe = classiMessageFactory.get(l);
					OpenSPCoop2MessageFactory factory = messageFactory.get(l);
					
					try{
					
						OpenSPCoop2Startup.log.info("Inizializzazione '"+tipo+"' corrispondente alla classe '"+classe+"' ... ");
					
						// XML
						org.openspcoop2.message.xml.XMLUtils xmlUtils = org.openspcoop2.message.xml.XMLUtils.getInstance(factory);
						// XML - XERCES
						xmlUtils.initDocumentBuilderFactory();
						xmlUtils.initDatatypeFactory();
//						xmlUtils.initSAXParserFactory();
//						xmlUtils.initXMLEventFactory();
						xmlUtils.initSchemaFactory();
						// XML - XALAN
						xmlUtils.initTransformerFactory();
						xmlUtils.initXPathFactory();
						// INIT - OTHER
						xmlUtils.initCalendarConverter();
						
						// SOAP
						factory.getSoapFactory11();
						factory.getSoapFactory12();
						factory.getSoapMessageFactory();
						
						// Log
						// stampo comunque saaj factory
						OpenSPCoop2Startup.log.info("OpenSPCoop MessageFactory (open:"+OpenSPCoop2MessageFactory_impl.class.getName().equals(factory.getClass().getName())+"): "+factory.getClass().getName());
						if(propertiesReader.isPrintInfoFactory()){
							MessageType [] mt = MessageType.values();
							for (int i = 0; i < mt.length; i++) {
								OpenSPCoop2Startup.log.info("OpenSPCoop Message ["+mt[i].name()+"]: "+factory.createEmptyMessage(mt[i],MessageRole.NONE).getClass().getName());	
							}
							if( factory.getSoapFactory11()!=null)
								OpenSPCoop2Startup.log.info("SOAP1.1 Factory: "+ factory.getSoapFactory11().getClass().getName());
							else
								OpenSPCoop2Startup.log.info("SOAP1.1 Factory: not implemented");
							if(factory.getSoapFactory12()!=null){
								OpenSPCoop2Startup.log.info("SOAP1.2 Factory: "+ factory.getSoapFactory12().getClass().getName());
							}else{
								OpenSPCoop2Startup.log.info("SOAP1.2 Factory: not implemented");
							}
							OpenSPCoop2Startup.log.info("SOAP MessageFactory: "+factory.getSoapMessageFactory().getClass().getName());
			
							// XML - XERCES
							OpenSPCoop2Startup.log.info("XERCES - DocumentFactory: "+xmlUtils.getDocumentBuilderFactory().getClass().getName());
							OpenSPCoop2Startup.log.info("XERCES - DatatypeFactory: "+xmlUtils.getDatatypeFactory().getClass().getName());
//							OpenSPCoop2Startup.log.info("XERCES - SAXParserFactory: "+xmlUtils.getSAXParserFactory().getClass().getName());
//							OpenSPCoop2Startup.log.info("XERCES - XMLEventFactory: "+xmlUtils.getXMLEventFactory().getClass().getName());
							OpenSPCoop2Startup.log.info("XERCES - SchemaFactory: "+xmlUtils.getSchemaFactory().getClass().getName());
							
							// XML - XALAN
							OpenSPCoop2Startup.log.info("XALAN - TransformerFactory: "+xmlUtils.getTransformerFactory().getClass().getName());
							OpenSPCoop2Startup.log.info("XALAN - XPathFactory: "+xmlUtils.getXPathFactory().getClass().getName());

						}
						if(propertiesReader.isPrintInfoMessageSecurity()){
							OpenSPCoop2Startup.log.info("MessageSecurity Context: "+MessageSecurityFactory.messageSecurityContextImplClass);
							OpenSPCoop2Startup.log.info("MessageSecurity DigestReader: "+MessageSecurityFactory.messageSecurityDigestReaderImplClass);
							OpenSPCoop2Startup.log.info("MessageSecurity (SoapBox) EncryptedDataHeaderBlock: "+factory.createEmptyMessage(MessageType.SOAP_11,MessageRole.NONE).castAsSoap().getEncryptedDataHeaderBlockClass());
							OpenSPCoop2Startup.log.info("MessageSecurity (SoapBox) ProcessPartialEncryptedMessage: "+factory.createEmptyMessage(MessageType.SOAP_11,MessageRole.NONE).castAsSoap().getProcessPartialEncryptedMessageClass());
							OpenSPCoop2Startup.log.info("MessageSecurity (SoapBox) getSignPartialMessageProcessor: "+factory.createEmptyMessage(MessageType.SOAP_11,MessageRole.NONE).castAsSoap().getSignPartialMessageProcessorClass());
						}
												
						// Inizializzo Operazioni "Costose"
						// Serve per abbassare la latenza del primo messaggio, altrimenti queste operazioni all'interno del metodo di costruzione dell'header costano sui 50ms
						OpenSPCoop2Message msgTest = factory.createEmptyMessage(MessageType.SOAP_11, MessageRole.REQUEST);
						SOAPHeader hdr = msgTest.castAsSoap().getSOAPHeader();
						if(hdr==null){
							hdr = msgTest.castAsSoap().getSOAPPart().getEnvelope().addHeader();
						}
						String namespaceTest = "http://initialize.openspcoop.org/test";
						String prefixTest = "op2";
						QName nameTest = new QName(namespaceTest, "Prova", prefixTest);
						SOAPHeaderElement testHeader = msgTest.castAsSoap().newSOAPHeaderElement(hdr, nameTest);
						testHeader.setActor("http://initialize.openspcoop.org/test");
						testHeader.setMustUnderstand(true);
						SOAPElement eGovIntestazioneMsg = testHeader.addChildElement("Test",prefixTest,namespaceTest);
						if(eGovIntestazioneMsg!=null){
							eGovIntestazioneMsg.toString();
						}
					
						OpenSPCoop2Startup.log.info("Inizializzazione '"+tipo+"' corrispondente alla classe '"+classe+"' effettuata");
						
					} catch(Exception e) {
						this.logError("Inizializzazione '"+tipo+"' corrispondente alla classe '"+classe+"' fallita: "+e.getMessage(),e);
						return;
					}
				}
								
			} catch(Exception e) {
				this.logError("Inizializzazione Message/DOM/SOAP: "+e.getMessage(), e);
				return;
			}
			
			
			
			
			





			/*----------- Inizializzazione DateManager  --------------*/
			try{
				String tipoClass = classNameReader.getDateManager(propertiesReader.getTipoDateManager());
				DateManager.initializeDataManager(tipoClass, propertiesReader.getDateManagerProperties(),logCore);
				OpenSPCoop2Startup.log.info("Inizializzazione DateManager: "+propertiesReader.getTipoDateManager());
				
				DateUtils.setDEFAULT_DATA_ENGINE_TYPE(propertiesReader.getTipoDateTimeFormat());
				OpenSPCoop2Startup.log.info("Inizializzazione DateTimeFormat: "+propertiesReader.getTipoDateTimeFormat());
			}catch(Exception e){
				this.logError("Riscontrato errore durante l'inizializzazione del DataManager: "+e.getMessage(),e);
				return;
			}





			/* -------------- Inizializzo DBManager --------------- */

			String erroreDB = null;
			try{
				DBManager.initialize(propertiesReader.getJNDIName_DataSource(),
						propertiesReader.getJNDIContext_DataSource());
			}catch(Exception e){
				erroreDB = e.getMessage();
				OpenSPCoop2Startup.log.info("Datasource non inizializzato... riprovo");
				//msgDiag.logStartupError(e, "DatabaseManager");
				logCore.error(erroreDB,e);
			}

			int count = 0;
			boolean trovato = DBManager.isInitialized();						
			while (!trovato && (count < 600000)) {
				try {
					DBManager.initialize(propertiesReader.getJNDIName_DataSource(),
							propertiesReader.getJNDIContext_DataSource());
				} catch (Exception e) {
					erroreDB = e.getMessage();
					OpenSPCoop2Startup.log.info("Attendo inizializzazione del Datasource ... "+erroreDB);
					logCore.error(erroreDB,e);
				}

				if (DBManager.isInitialized()) {
					trovato = true;
				} else {
					count += 10000;
					Utilities.sleep(10000);
				}
			}

			if (DBManager.isInitialized()) {
				OpenSPCoop2Startup.log.info("Inizializzazione DBManager effettuata con successo.");
			} else {
				OpenSPCoop2Startup.log.error("Inizializzazione DBManager non effettuata", new Exception(erroreDB));
				//msgDiag.logStartupError(new Exception(erroreDB), "DatabaseManager");
				return;
			}

			if( JDBCUtilities.isTransactionIsolationNone(DBManager.getTransactionIsolationLevel()) )
				OpenSPCoop2Startup.log.info("Database TransactionLevel is NONE");
			else if(JDBCUtilities.isTransactionIsolationReadCommitted(DBManager.getTransactionIsolationLevel()) )
				OpenSPCoop2Startup.log.info("Database TransactionLevel is READ_COMMITTED");
			else if(JDBCUtilities.isTransactionIsolationReadUncommitted(DBManager.getTransactionIsolationLevel()) )
				OpenSPCoop2Startup.log.info("Database TransactionLevel is READ_UNCOMMITTED");
			else if(JDBCUtilities.isTransactionIsolationRepeatableRead(DBManager.getTransactionIsolationLevel()) )
				OpenSPCoop2Startup.log.info("Database TransactionLevel is REPEATABLE_READ");
			else if(JDBCUtilities.isTransactionIsolationSerializable(DBManager.getTransactionIsolationLevel()) )
				OpenSPCoop2Startup.log.info("Database TransactionLevel is SERIALIZABLE");
			else if(JDBCUtilities.isTransactionIsolationSqlServerSnapshot(DBManager.getTransactionIsolationLevel()) )
				OpenSPCoop2Startup.log.info("Database TransactionLevel is SQLSERVER SNAPSHOT");
			else {
				OpenSPCoop2Startup.log.error("TransactionLevel associato alla connessione non conosciuto");
				//			msgDiag.logStartupError("TransactionLevel associato alla connessione non conosciuto","DatabaseManager");
				return;
			}

			// Assegno datasource a Libreria per gestione stateless
			try{
				org.openspcoop2.protocol.utils.IDSerialGenerator.init(DBManager.getInstance().getDataSource());
			}catch(Exception e){
				OpenSPCoop2Startup.log.error("Inizializzazione datasource libreria serial generator", e);
				//msgDiag.logStartupError(e, "Inizializzazione datasource libreria");
				return;
			}

			// GestoreTransazioni
			try{
				if(propertiesReader.isTransazioniUsePddRuntimeDatasource()) {
					DBTransazioniManager.init(DBManager.getInstance(), logCore, propertiesReader.getDatabaseType());
				}
				else {
					DBTransazioniManager.init(propertiesReader.getTransazioniDatasource(), propertiesReader.getTransazioniDatasourceJndiContext(), 
							logCore, propertiesReader.getDatabaseType(), 
							propertiesReader.isTransazioniDatasourceUseDBUtils(), propertiesReader.isRisorseJMXAbilitate());
				}
			}catch(Exception e){
				OpenSPCoop2Startup.log.error("Inizializzazione DBTransazioniManager", e);
				return;
			}

			// GestoreStatistiche
			try {
				if(propertiesReader.isStatisticheGenerazioneEnabled()){
					if(propertiesReader.isStatisticheUsePddRuntimeDatasource()) {
						DBStatisticheManager.init(DBManager.getInstance(), logCore, propertiesReader.getDatabaseType());
					}
					else if(propertiesReader.isStatisticheUseTransazioniDatasource()) {
						DBStatisticheManager.init(DBTransazioniManager.getInstance(), logCore, propertiesReader.getDatabaseType());
					}
					else {
						DBStatisticheManager.init(propertiesReader.getStatisticheDatasource(), propertiesReader.getStatisticheDatasourceJndiContext(), 
								logCore, propertiesReader.getDatabaseType(), 
								propertiesReader.isStatisticheDatasourceUseDBUtils(), propertiesReader.isRisorseJMXAbilitate());
					}
				}
			}catch(Exception e){
				OpenSPCoop2Startup.log.error("Inizializzazione DBStatisticheManager", e);
				return;
			}


			/*----------- Inizializzazione Generatore di ClusterID  --------------*/
			String clusterID = null;
			try{
				String tipoGeneratoreClusterID = propertiesReader.getTipoIDManager();
				String classClusterID = null;
				if(CostantiConfigurazione.NONE.equals(tipoGeneratoreClusterID)){
					clusterID = propertiesReader.getClusterId(false);
					if(clusterID!=null){
						classClusterID = "org.openspcoop2.utils.id.ClusterIdentifierGenerator";
					}
				}else{
					classClusterID = classNameReader.getUniqueIdentifier(tipoGeneratoreClusterID);
				}
				
				if(classClusterID==null){
					UniqueIdentifierManager.disabilitaGenerazioneUID();
				}else{
				
					UniqueIdentifierManager.inizializzaUniqueIdentifierManager(classClusterID,propertiesReader.getClusterId(false));
					
					OpenSPCoop2Startup.log.info("UUID Generator: "+classClusterID);
					
					if(propertiesReader.generazioneDateCasualiLogAbilitato()){
						GeneratoreCasualeDate.init(OpenSPCoop2Properties.getGenerazioneDateCasualiLog_dataInizioIntervallo(), 
								OpenSPCoop2Properties.getGenerazioneDateCasualiLog_dataFineIntervallo(),
								OpenSPCoop2Startup.log);
						OpenSPCoop2Startup.log.info("Abilitata generazione date casuali");
					}
				}
								
			}catch(Exception e){
				this.logError("Riscontrato errore durante l'inizializzazione del generatore di identificatori unici: "+e.getMessage(),e);
				return;
			}











			/* ---------  Inizializzazione Configurazione di OpenSPCoop --------------------- */

			// Inizializza extended info
			try{
				ExtendedInfoManager.initialize(true, loader, propertiesReader.getExtendedInfoConfigurazione(), 
						propertiesReader.getExtendedInfoPortaDelegata(), propertiesReader.getExtendedInfoPortaApplicativa());
			}catch(Exception e){
				this.logError("Riscontrato errore durante l'inizializzazione del componente ExtendedInfoManager: "+e.getMessage(),e);
				return;
			}
			// Inizializza il file ConfigReader che gestisce la configurazione di OpenSPCoop
			AccessoConfigurazionePdD accessoConfigurazione = null;
			try{
				accessoConfigurazione = propertiesReader.getAccessoConfigurazionePdD();
			}catch(Exception e){
				this.logError("Riscontrato errore durante la lettura della modalita' di accesso alla configurazione di OpenSPCoop: "+e.getMessage(),e);
				return;
			}
			boolean isInitializeConfig = ConfigurazionePdDReader.initialize(accessoConfigurazione,logCore,OpenSPCoop2Startup.log,localConfig,
					propertiesReader.getJNDIName_DataSource(), false,
					propertiesReader.isDSOp2UtilsEnabled(), propertiesReader.isRisorseJMXAbilitate(),
					propertiesReader.isConfigurazioneCache_ConfigPrefill(), propertiesReader.getCryptConfigAutenticazioneApplicativi());
			if(isInitializeConfig == false){
				this.logError("Riscontrato errore durante l'inizializzazione della configurazione di OpenSPCoop.");
				return;
			}
			ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance();
			if(msgDiagProperties.checkValoriFiltriMsgDiagnostici(OpenSPCoop2Startup.log)==false){
				return;
			}
			OpenSPCoop2Startup.log.info("ConfigurazionePdD, refresh: "+propertiesReader.isConfigurazioneDinamica());









			/*----------- Inizializzazione libreria --------------*/
			try{
				IGestoreRepository repository = null;
//				if(propertiesReader.getDatabaseType()!=null){
//					repository = GestoreRepositoryFactory.createRepositoryBuste(propertiesReader.getDatabaseType());
//				}else{
				// Il tipo deve sempre essere letto da openspcoop.properties, altrimenti la testsuite che richiede il tipo di default non viene mai eseguita.
				String tipoClass = classNameReader.getRepositoryBuste(propertiesReader.getGestoreRepositoryBuste());
				repository = (IGestoreRepository) loader.newInstance(tipoClass);
				org.openspcoop2.protocol.engine.Configurazione.init(
						propertiesReader.getGestioneSerializableDB_CheckInterval(),
						repository,propertiesReader.getDatabaseType(),logCore);
			}catch(Exception e){
				this.logError("Riscontrato errore durante l'inizializzazione della libreria del protocollo: "+e.getMessage());
				logCore.error("Riscontrato errore durante l'inizializzazione della libreria del protocollo: "+e.getMessage(),e);
				return;
			}









			/* -------------- Inizializzo MsgDiagnostico -------------------- */
			MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(OpenSPCoop2Startup.ID_MODULO);


			
			
			
			
			
			
			
			
			/*----------- Inizializzazione SystemProperties --------------*/
			try{
				SystemPropertiesManager spm = new SystemPropertiesManager(configurazionePdDManager,logCore);
				spm.updateSystemProperties();
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione proprieta' di sistema");
				return;
			}
			
			
			
			
			
			
			/*----------- Inizializzazione Autorizzazione --------------*/
			try{
				AccessoDatiAutorizzazione datiAutorizzazione = configurazionePdDManager.getAccessoDatiAutorizzazione();
				if(datiAutorizzazione!=null && datiAutorizzazione.getCache()!=null){
					
					int dimensioneCache = -1;
					if(datiAutorizzazione.getCache().getDimensione()!=null){
						try{
							dimensioneCache = Integer.parseInt(datiAutorizzazione.getCache().getDimensione());
						}catch(Exception e){
							throw new Exception("Parametro 'dimensioneCache' errato per la cache di accesso ai dati di autorizzazione");
						}
					}
					
					String algoritmo = null;
					if(datiAutorizzazione.getCache().getAlgoritmo()!=null){
						algoritmo = datiAutorizzazione.getCache().getAlgoritmo().toString();
					}
					
					long idleTime = -1;
					if(datiAutorizzazione.getCache().getItemIdleTime()!=null){
						try{
							idleTime = Integer.parseInt(datiAutorizzazione.getCache().getItemIdleTime());
						}catch(Exception e){
							throw new Exception("Parametro 'idleTime' errato per la cache di accesso ai dati di autorizzazione");
						}
					}
					
					long itemLifeSecond = -1;
					if(datiAutorizzazione.getCache().getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(datiAutorizzazione.getCache().getItemLifeSecond());
						}catch(Exception e){
							throw new Exception("Parametro 'itemLifeSecond' errato per la cache di accesso ai dati di autorizzazione");
						}
					}

					GestoreAutorizzazione.initialize(dimensioneCache, algoritmo, idleTime, itemLifeSecond, logCore);
				}
				else{
					GestoreAutorizzazione.initialize(logCore);
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"Gestore Autorizzazione");
				return;
			}
			
			
			
			
			
			/*----------- Inizializzazione Autenticazione --------------*/
			try{
				AccessoDatiAutenticazione datiAutenticazione = configurazionePdDManager.getAccessoDatiAutenticazione();
				if(datiAutenticazione!=null && datiAutenticazione.getCache()!=null){
					
					int dimensioneCache = -1;
					if(datiAutenticazione.getCache().getDimensione()!=null){
						try{
							dimensioneCache = Integer.parseInt(datiAutenticazione.getCache().getDimensione());
						}catch(Exception e){
							throw new Exception("Parametro 'dimensioneCache' errato per la cache di accesso ai dati di autenticazione");
						}
					}
					
					String algoritmo = null;
					if(datiAutenticazione.getCache().getAlgoritmo()!=null){
						algoritmo = datiAutenticazione.getCache().getAlgoritmo().toString();
					}
					
					long idleTime = -1;
					if(datiAutenticazione.getCache().getItemIdleTime()!=null){
						try{
							idleTime = Integer.parseInt(datiAutenticazione.getCache().getItemIdleTime());
						}catch(Exception e){
							throw new Exception("Parametro 'idleTime' errato per la cache di accesso ai dati di autenticazione");
						}
					}
					
					long itemLifeSecond = -1;
					if(datiAutenticazione.getCache().getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(datiAutenticazione.getCache().getItemLifeSecond());
						}catch(Exception e){
							throw new Exception("Parametro 'itemLifeSecond' errato per la cache di accesso ai dati di autenticazione");
						}
					}

					GestoreAutenticazione.initialize(dimensioneCache, algoritmo, idleTime, itemLifeSecond, logCore);
				}
				else{
					GestoreAutenticazione.initialize(logCore);
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"Gestore Autenticazione");
				return;
			}
			
			
			
			
			
			
			/*----------- Inizializzazione GestoreToken --------------*/
			try{
				AccessoDatiGestioneToken datiGestioneToken = configurazionePdDManager.getAccessoDatiGestioneToken();
				if(datiGestioneToken!=null && datiGestioneToken.getCache()!=null){
					
					int dimensioneCache = -1;
					if(datiGestioneToken.getCache().getDimensione()!=null){
						try{
							dimensioneCache = Integer.parseInt(datiGestioneToken.getCache().getDimensione());
						}catch(Exception e){
							throw new Exception("Parametro 'dimensioneCache' errato per la cache di accesso ai dati dei token");
						}
					}
					
					String algoritmo = null;
					if(datiGestioneToken.getCache().getAlgoritmo()!=null){
						algoritmo = datiGestioneToken.getCache().getAlgoritmo().toString();
					}
					
					long idleTime = -1;
					if(datiGestioneToken.getCache().getItemIdleTime()!=null){
						try{
							idleTime = Integer.parseInt(datiGestioneToken.getCache().getItemIdleTime());
						}catch(Exception e){
							throw new Exception("Parametro 'idleTime' errato per la cache di accesso ai dati dei token");
						}
					}
					
					long itemLifeSecond = -1;
					if(datiGestioneToken.getCache().getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(datiGestioneToken.getCache().getItemLifeSecond());
						}catch(Exception e){
							throw new Exception("Parametro 'itemLifeSecond' errato per la cache di accesso ai dati dei token");
						}
					}

					GestoreToken.initialize(dimensioneCache, algoritmo, idleTime, itemLifeSecond, logCore);
				}
				else{
					GestoreToken.initialize(logCore);
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"Gestore Token");
				return;
			}
			
			
			
			
			
			
			
			
			/*----------- Inizializzazione ResponseCaching --------------*/
			try{
				GestoreCacheResponseCaching.initialize();
				
				org.openspcoop2.core.config.Cache responseCachingCacheConfig = configurazionePdDManager.getConfigurazioneResponseCachingCache();
				if(responseCachingCacheConfig!=null){
					
					int dimensioneCache = -1;
					if(responseCachingCacheConfig.getDimensione()!=null){
						try{
							dimensioneCache = Integer.parseInt(responseCachingCacheConfig.getDimensione());
						}catch(Exception e){
							throw new Exception("Parametro 'dimensioneCache' errato per la cache di response caching");
						}
					}
					
					String algoritmo = null;
					if(responseCachingCacheConfig.getAlgoritmo()!=null){
						algoritmo = responseCachingCacheConfig.getAlgoritmo().toString();
					}
					
					long idleTime = -1;
					if(responseCachingCacheConfig.getItemIdleTime()!=null){
						try{
							idleTime = Integer.parseInt(responseCachingCacheConfig.getItemIdleTime());
						}catch(Exception e){
							throw new Exception("Parametro 'idleTime' errato per la cache di response caching");
						}
					}
					
					long itemLifeSecond = -1;
					if(responseCachingCacheConfig.getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(responseCachingCacheConfig.getItemLifeSecond());
						}catch(Exception e){
							throw new Exception("Parametro 'itemLifeSecond' errato per la cache di response caching");
						}
					}

					GestoreCacheResponseCaching.initialize(dimensioneCache, algoritmo, idleTime, itemLifeSecond, logCore);
				}
				else{
					GestoreCacheResponseCaching.initialize(logCore);
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"Gestore Response Caching");
				return;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			/* ----------- Inizializzazione Keystore Security ------------ */
			
			try{
				AccessoDatiKeystore keystoreCacheConfig = configurazionePdDManager.getAccessoDatiKeystore();
				if(keystoreCacheConfig!=null && keystoreCacheConfig.getCache()!=null){
					
					int dimensioneCache = -1;
					if(keystoreCacheConfig.getCache().getDimensione()!=null){
						try{
							dimensioneCache = Integer.parseInt(keystoreCacheConfig.getCache().getDimensione());
						}catch(Exception e){
							throw new Exception("Parametro 'dimensioneCache' errato per la cache di accesso ai keystore");
						}
					}
					
					String algoritmo = null;
					if(keystoreCacheConfig.getCache().getAlgoritmo()!=null){
						algoritmo = keystoreCacheConfig.getCache().getAlgoritmo().toString();
					}
					
					long idleTime = -1;
					if(keystoreCacheConfig.getCache().getItemIdleTime()!=null){
						try{
							idleTime = Integer.parseInt(keystoreCacheConfig.getCache().getItemIdleTime());
						}catch(Exception e){
							throw new Exception("Parametro 'idleTime' errato per la cache di accesso ai keystore");
						}
					}
					
					long itemLifeSecond = -1;
					if(keystoreCacheConfig.getCache().getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(keystoreCacheConfig.getCache().getItemLifeSecond());
						}catch(Exception e){
							throw new Exception("Parametro 'itemLifeSecond' errato per la cache di accesso ai keystore");
						}
					}
					
					long itemCrlLifeSecond = -1;
					if(keystoreCacheConfig.getCrlItemLifeSecond()!=null){
						try{
							itemCrlLifeSecond = Integer.parseInt(keystoreCacheConfig.getCrlItemLifeSecond());
						}catch(Exception e){
							throw new Exception("Parametro 'itemCrlLifeSecond' errato per la cache di accesso ai keystore");
						}
					}

					GestoreKeystoreCaching.initialize(dimensioneCache, algoritmo, idleTime, itemLifeSecond, logCore);
					
					if(itemCrlLifeSecond>0) {
						GestoreKeystoreCaching.setCacheCrlLifeSeconds(itemCrlLifeSecond, logCore);
					}
				}
				else{
					GestoreKeystoreCaching.initialize(logCore);
					
					// Provo ad utilizzare la cache alternativa in memoria
					GestoreKeystoreCache.setKeystoreCacheParameters(propertiesReader.isAbilitataCacheMessageSecurityKeystore(),
							propertiesReader.getItemLifeSecondCacheMessageSecurityKeystore(), 
							propertiesReader.getDimensioneCacheMessageSecurityKeystore());
					OpenSPCoop2Startup.log.info("MessageSecurity Keystore Cache In-Memory enabled["+propertiesReader.isAbilitataCacheMessageSecurityKeystore()+"] itemLifeSecond["+
							propertiesReader.getItemLifeSecondCacheMessageSecurityKeystore()+"] size["+
							propertiesReader.getDimensioneCacheMessageSecurityKeystore()+"]");
					
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"Gestore Response Caching");
				return;
			}
			
			
			
			
			
			
			
			
			
			/*----------- Inizializzazione Cache per Consegna Applicativi --------------*/
			// Viene fatta prima, perchè questi valori vengono letti dalle inforamzioni JMX sotto.
			try{
				org.openspcoop2.core.config.Cache consegnaApplicativiCacheConfig = configurazionePdDManager.getConfigurazioneConsegnaApplicativiCache();
				if(consegnaApplicativiCacheConfig!=null){
					
					int dimensioneCache = -1;
					if(consegnaApplicativiCacheConfig.getDimensione()!=null){
						try{
							dimensioneCache = Integer.parseInt(consegnaApplicativiCacheConfig.getDimensione());
						}catch(Exception e){
							throw new Exception("Parametro 'dimensioneCache' errato per la cache di response caching");
						}
					}
					
					String algoritmo = null;
					if(consegnaApplicativiCacheConfig.getAlgoritmo()!=null){
						algoritmo = consegnaApplicativiCacheConfig.getAlgoritmo().toString();
					}
					
					long idleTime = -1;
					if(consegnaApplicativiCacheConfig.getItemIdleTime()!=null){
						try{
							idleTime = Integer.parseInt(consegnaApplicativiCacheConfig.getItemIdleTime());
						}catch(Exception e){
							throw new Exception("Parametro 'idleTime' errato per la cache di response caching");
						}
					}
					
					long itemLifeSecond = -1;
					if(consegnaApplicativiCacheConfig.getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(consegnaApplicativiCacheConfig.getItemLifeSecond());
						}catch(Exception e){
							throw new Exception("Parametro 'itemLifeSecond' errato per la cache di response caching");
						}
					}

					GestoreLoadBalancerCaching.initialize(dimensioneCache, algoritmo, idleTime, itemLifeSecond, logCore);
				}
				else{
					GestoreLoadBalancerCaching.initialize(logCore);
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"Gestore Consegna Applicativi Cache");
				return;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			/* ----------- Inizializzazione Gestore Risorse JMX (le risorse jmx vengono registrate in seguito) ------------ */
			try{
				if(propertiesReader.isRisorseJMXAbilitate()){
					if(propertiesReader.getJNDIName_MBeanServer()!=null){
						OpenSPCoop2Startup.this.gestoreRisorseJMX = new GestoreRisorseJMX(propertiesReader.getJNDIName_MBeanServer(),
								propertiesReader.getJNDIContext_MBeanServer());
					}else{
						OpenSPCoop2Startup.this.gestoreRisorseJMX = new GestoreRisorseJMX();
					}
					OpenSPCoop2Startup.gestoreRisorseJMX_staticInstance = OpenSPCoop2Startup.this.gestoreRisorseJMX;
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"Gestore Risorse JMX");
				return;
			}
			
			
			
			
			
			
			
			

			
			
			/* ------------- Inizializzazione Protocolli ----------- */
			ProtocolFactoryManager protocolFactoryManager = null;
			try{
				ConfigurazionePdD configPdD = new ConfigurazionePdD();
				configPdD.setConfigurationDir(propertiesReader.getRootDirectory());
				configPdD.setLoader(loader);
				configPdD.setAttesaAttivaJDBC(propertiesReader.getGestioneSerializableDB_AttesaAttiva());
				configPdD.setCheckIntervalJDBC(propertiesReader.getGestioneSerializableDB_CheckInterval());
				configPdD.setTipoDatabase(TipiDatabase.toEnumConstant(propertiesReader.getDatabaseType()));
				configPdD.setLog(logCore);
				
				Costanti.initHTTP_HEADER_GOVWAY_ERROR_STATUS(propertiesReader.getErroriHttpHeaderGovWayStatus());
				Costanti.initHTTP_HEADER_GOVWAY_ERROR_TYPE(propertiesReader.getErroriHttpHeaderGovWayType());
				Costanti.initHTTP_HEADER_GOVWAY_ERROR_CODE(propertiesReader.getErroriHttpHeaderGovWayCode());
				
				Costanti.TRANSACTION_ERROR_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE = propertiesReader.isErroriSoapUseGovWayStatusAsFaultCode();
				Costanti.TRANSACTION_ERROR_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE = propertiesReader.isErroriSoapHttpHeaderGovWayCodeEnabled();
								
				Costanti.TRANSACTION_ERROR_STATUS_ABILITATO = propertiesReader.isErroriGovWayStatusEnabled();
				Costanti.TRANSACTION_ERROR_INSTANCE_ID_ABILITATO = propertiesReader.isErroriGovWayInstanceEnabled();
				Costanti.TRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS = propertiesReader.isErroriGovWayForceSpecificDetails();
				Costanti.TRANSACTION_ERROR_SOAP_FAULT_ADD_FAULT_DETAILS_WITH_PROBLEM_RFC7807 = propertiesReader.isErroriGovWayFaultDetailsWithProblemRFC7807();
				
				Costanti.initPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE(propertiesReader.isProblemRFC7807_enrichTitleAsGovWayType());
				Costanti.initPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE(propertiesReader.isProblemRFC7807_enrichTitleAsGovWayType_camelCaseDecode());
				Costanti.initPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM(propertiesReader.isProblemRFC7807_enrichTitleAsGovWayType_customClaim());
				Costanti.initPROBLEM_RFC7807_GOVWAY_TRANSACTION_ID(propertiesReader.getProblemRFC7807_transactionId_claim());
				Costanti.initPROBLEM_RFC7807_GOVWAY_CODE(propertiesReader.getProblemRFC7807_code_claim());
				Costanti.initPROBLEM_RFC7807_GOVWAY_TYPE(propertiesReader.getProblemRFC7807_type_claim());
								
				ProtocolFactoryManager.initialize(OpenSPCoop2Startup.log, configPdD, propertiesReader.getDefaultProtocolName());
				// forzo update logger. (viene caricato dopo il log della console)
				ProtocolFactoryManager.updateLogger(logCore);
				protocolFactoryManager = ProtocolFactoryManager.getInstance();
				// Update protocolLogger
				boolean isInitializeLoggerProtocol = OpenSPCoop2Logger.initializeProtocolLogger(OpenSPCoop2Startup.log, true, propertiesReader.getRootDirectory(),loggerP); 
				if(isInitializeLoggerProtocol == false){
					return;
				}
				// Initialize Protocols
				protocolFactoryManager.initializeAllProtocols();
				
				// Inizializzazione schemi per altri message factory
				if(messageFactory.size()>1) {
					// La factory alla prima posizione, e' la factory di default già inizializzata
					for (int i = 1; i < messageFactory.size(); i++) {
						OpenSPCoop2MessageFactory factory = messageFactory.get(i);
						Enumeration<String> protocolli = ProtocolFactoryManager.getInstance().getProtocolNames();
						while (protocolli.hasMoreElements()) {
							String protocollo = (String) protocolli.nextElement();
							if(ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).createValidazioneConSchema(null).initialize(factory)==false) {
								throw new Exception("Inizializzazione validatore con schemi per il protocollo '"+protocollo+"' e messagefactory '"+factory.getClass().getName()+"' fallita");
							}
						}
					}
				}
				
				OpenSPCoop2Startup.log.info("ProtocolFactory default: "+protocolFactoryManager.getDefaultProtocolFactory().getProtocol());
			} catch(Exception e) {
				this.logError("Initialize ProtocolFactoryManager failed: "+e.getMessage());
				logCore.error("Initialize ProtocolFactoryManager failed: "+e.getMessage(),e);
				msgDiag.logStartupError("Riscontrato errore durante l'inizializzazione del ProtocolFactoryManager","initProtocolFactoryManager");
				return;
			}
			
			
			
			


			/* ------------- Inizializzo il sistema di Logging per gli appender personalizzati --------------- */
			boolean isInitializeAppender = false;
			isInitializeAppender = OpenSPCoop2Logger.initializeMsgDiagnosticiOpenSPCoopAppender(configurazionePdDManager.getOpenSPCoopAppender_MsgDiagnostici());
			if(isInitializeAppender == false){
				return;
			}
			isInitializeAppender = OpenSPCoop2Logger.initializeTracciamentoOpenSPCoopAppender(configurazionePdDManager.getOpenSPCoopAppender_Tracciamento());
			if(isInitializeAppender == false){
				return;
			}
			isInitializeAppender = OpenSPCoop2Logger.initializeDumpOpenSPCoopAppender(configurazionePdDManager.getOpenSPCoopAppender_Dump());
			if(isInitializeAppender == false){
				return;
			}





			






			/* -------------- Inizializzo QueueManager --------------- */
			if(OpenSPCoop2Startup.this.serverJ2EE){
				if( CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_JMS.equals(propertiesReader.getNodeReceiver()) 
						|| CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_JMS.equals(propertiesReader.getNodeSender()) ){

					// Connection Factory
					try{
						QueueManager.initialize(propertiesReader.getJNDIName_ConnectionFactory(),
								propertiesReader.getJNDIContext_ConnectionFactory());
					}catch(Exception e){
						msgDiag.logStartupError(e, "QueueManager.initConnectionFactory");
						return;
					}
					OpenSPCoop2Startup.log.info("Inizializzazione connectionFactoryJMS ["+propertiesReader.getJNDIName_ConnectionFactory()+"] effettuata.");

					// Code di Ricezione
					if( CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_JMS.equals(propertiesReader.getNodeReceiver()) 
					){
						try{
							QueueManager.initializeQueueNodeReceiver(propertiesReader.getJNDIContext_CodeInterne());
						}catch(Exception e){
							msgDiag.logStartupError(e, "QueueManager.initQueueNodeReceiver");
							return;
						}
						OpenSPCoop2Startup.log.info("Inizializzazione code JMS per la ricezione di messaggi nell'infrastruttura di OpenSPCoop, effettuata.");
					}

					// Code di Spedizione
					//if( CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_JMS.equals(propertiesReader.getNodeSender()) 
					//){
					try{
						QueueManager.initializeQueueNodeSender(propertiesReader.getJNDIContext_CodeInterne());
					}catch(Exception e){
						msgDiag.logStartupError(e, "QueueManager.initQueueNodeSender");
						return;
					}
					OpenSPCoop2Startup.log.info("Inizializzazione code JMS per la spedizione di messaggi nell'infrastruttura di OpenSPCoop, effettuata.");
				}
			}


			
			






			/* ----------- Inizializzazione Registro dei Servizi ---------------- */

			// Check configurazione registro dei Servizi
			if( configurazionePdDManager.getAccessoRegistroServizi() == null ){
				msgDiag.logStartupError("Riscontrato errore durante la lettura dei valori associati al registro dei servizi di OpenSPCoop.", "Lettura configurazione PdD");
				return;
			}

			// Controllo la consistenza dei registri di tipo DB
			AccessoRegistro accessoRegistro = configurazionePdDManager.getAccessoRegistroServizi();
			for(int i=0; i<accessoRegistro.sizeRegistroList(); i++){	
				AccessoRegistroRegistro registro = accessoRegistro.getRegistro(i);
				if(CostantiConfigurazione.REGISTRO_DB.equals(registro.getTipo().toString())){
					// Il tipo del DB e' obbligatorio.
					// Controllo che vi sia o
					// - come prefisso del datasource: tipoDatabase@datasource
					// - come tipo di database della porta di dominio.
					if(registro.getLocation().indexOf("@")!=-1){
						// estrazione tipo database
						try{
							String tipoDatabase = DBUtils.estraiTipoDatabaseFromLocation(registro.getLocation());
							String location = registro.getLocation().substring(registro.getLocation().indexOf("@")+1);
							accessoRegistro.getRegistro(i).setLocation(location);
							accessoRegistro.getRegistro(i).setTipoDatabase(tipoDatabase);
						}catch(Exception e){
							msgDiag.logStartupError(e, "Inizializzazione registro dei servizi di OpenSPCoop ["+registro.getNome()+"]; analisi del tipo di database (tipoDatabase@datasource)");
							return;
						}
					}else{
						if(propertiesReader.getDatabaseType()==null){
							msgDiag.logStartupError("Il Registro dei Servizi di tipo ["+CostantiConfigurazione.REGISTRO_DB+"] richiede la definizione del tipo di database indicato o come prefisso della location (tipoDB@datasource) o attraverso la proprieta' 'org.openspcoop.pdd.repository.tipoDatabase' della porta di dominio",
									"Inizializzazione registro dei servizi di OpenSPCoop ["+registro.getNome()+"]; analisi del tipo di database");
							return;
						}else{
							accessoRegistro.getRegistro(i).setTipoDatabase(propertiesReader.getDatabaseType());
						}
					}
				}
			}

			// inizializzo registri
			boolean isInitializeRegistro = 
				RegistroServiziReader.initialize(accessoRegistro,
						logCore,OpenSPCoop2Startup.log,propertiesReader.isControlloRisorseRegistriRaggiungibilitaTotale(),
						propertiesReader.isReadObjectStatoBozza(),propertiesReader.getJNDIName_DataSource(),
						propertiesReader.isDSOp2UtilsEnabled(), propertiesReader.isRisorseJMXAbilitate(),
						propertiesReader.isConfigurazioneCache_RegistryPrefill(), propertiesReader.getCryptConfigAutenticazioneSoggetti());
			if(isInitializeRegistro == false){
				msgDiag.logStartupError("Inizializzazione fallita","Accesso registro/i dei servizi");
				return;
			}
			if(propertiesReader.isConfigurazioneCache_RegistryPrefill() && propertiesReader.isConfigurazioneCache_ConfigPrefill()){
				try{
					ConfigurazionePdDReader.prefillCacheConInformazioniRegistro(OpenSPCoop2Startup.log);
				}catch(Exception e){
					msgDiag.logStartupError(e,"Prefill ConfigurazionePdD e Registro Reader fallita");
					return;
				}
			}

			// Inizializza il reader del registro dei Servizi utilizzato nella configurazione
//			try{
//				configurazionePdDReader.initializeRegistroServiziReader();
//			}catch(Exception e){
//				msgDiag.logStartupError(e,"Inizializzazione Reader per il registro dei servizi utilizzato nella configurazione");
//				return;
//			}


			
			
			
			
			
			
			
			/* ------------ PreLoading ------------- */
			
			if(propertiesReader.getConfigPreLoadingLocale()!=null) {
				try{
					
					Object oConfig = ConfigurazionePdDReader.getDriverConfigurazionePdD();
					Object oRegistry = RegistroServiziReader.getDriverRegistroServizi().values().iterator().next();
					if( (oConfig instanceof DriverConfigurazioneDB) && (oRegistry instanceof DriverRegistroServiziDB) ) {
						PreLoadingConfig preLoading = new PreLoadingConfig(logCore, logCore, propertiesReader.getDefaultProtocolName(),
								propertiesReader.getIdentitaPortaDefault(propertiesReader.getDefaultProtocolName()));
						preLoading.loadConfig(propertiesReader.getConfigPreLoadingLocale());
					}
										
				}catch(Exception e){
					this.logError("Riscontrato errore durante il preloading della configurazione di OpenSPCoop: "+e.getMessage(),e);
					return;
				}
			}











			/* ---------------- Validazione semantica -----------------------*/

			// Configurazione
			boolean validazioneSemanticaConfigurazione = false;
			if(CostantiConfigurazione.CONFIGURAZIONE_XML.equalsIgnoreCase(accessoConfigurazione.getTipo())){
				validazioneSemanticaConfigurazione = propertiesReader.isValidazioneSemanticaConfigurazioneStartupXML();
			}else{
				validazioneSemanticaConfigurazione = propertiesReader.isValidazioneSemanticaConfigurazioneStartup();
			}
			if(validazioneSemanticaConfigurazione){
				try{
					ConfigurazionePdDManager.getInstance().validazioneSemantica(classNameReader.getConnettore(), 
							classNameReader.getMsgDiagnosticoOpenSPCoopAppender(),
							classNameReader.getTracciamentoOpenSPCoopAppender(),
							classNameReader.getDumpOpenSPCoopAppender(),
							classNameReader.getAutenticazionePortaDelegata(), classNameReader.getAutenticazionePortaApplicativa(), 
							classNameReader.getAutorizzazionePortaDelegata(), classNameReader.getAutorizzazionePortaApplicativa(),
							classNameReader.getAutorizzazioneContenutoPortaDelegata(),classNameReader.getAutorizzazioneContenutoPortaApplicativa(),
							classNameReader.getIntegrazionePortaDelegata(),
							classNameReader.getIntegrazionePortaApplicativa(),
							propertiesReader.isValidazioneSemanticaConfigurazioneStartupXML(),
							propertiesReader.isValidazioneSemanticaConfigurazioneStartup(),
							true, OpenSPCoop2Startup.log);
					if(propertiesReader.isValidazioneSemanticaConfigurazioneStartupXML()){
						ConfigurazionePdDManager.getInstance().setValidazioneSemanticaModificaConfigurazionePdDXML(classNameReader.getConnettore(), 
								classNameReader.getMsgDiagnosticoOpenSPCoopAppender(),
								classNameReader.getTracciamentoOpenSPCoopAppender(),
								classNameReader.getDumpOpenSPCoopAppender(),
								classNameReader.getAutenticazionePortaDelegata(), classNameReader.getAutenticazionePortaApplicativa(), 
								classNameReader.getAutorizzazionePortaDelegata(), classNameReader.getAutorizzazionePortaApplicativa(),
								classNameReader.getAutorizzazioneContenutoPortaDelegata(),classNameReader.getAutorizzazioneContenutoPortaApplicativa(),
								classNameReader.getIntegrazionePortaDelegata(),
								classNameReader.getIntegrazionePortaApplicativa());
					}
				}catch(Exception e){
					msgDiag.logStartupError(e,"Validazione semantica della configurazione");
					return;
				}
			}

			// Registro dei Servizi
			try{
				RegistroServiziManager.getInstance().validazioneSemantica(true, propertiesReader.isValidazioneSemanticaRegistroServiziCheckURI(), 
						protocolFactoryManager.getOrganizationTypesAsArray(),
						protocolFactoryManager.getServiceTypesAsArray(ServiceBinding.SOAP),
						protocolFactoryManager.getServiceTypesAsArray(ServiceBinding.REST),
						classNameReader.getConnettore(),
						propertiesReader.isValidazioneSemanticaRegistroServiziStartupXML(),
						propertiesReader.isValidazioneSemanticaRegistroServiziStartup(),OpenSPCoop2Startup.log);		
				if(propertiesReader.isValidazioneSemanticaRegistroServiziStartupXML()){
					RegistroServiziManager.getInstance().setValidazioneSemanticaModificaRegistroServiziXML(propertiesReader.isValidazioneSemanticaRegistroServiziCheckURI(), 
							protocolFactoryManager.getOrganizationTypesAsArray(),
							protocolFactoryManager.getServiceTypesAsArray(ServiceBinding.SOAP),
							protocolFactoryManager.getServiceTypesAsArray(ServiceBinding.REST),
							classNameReader.getConnettore());
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"Validazione semantica del registro dei servizi");
				return;
			}





		
		
		
		
			/* ----------- Inizializzo GestoreMessaggi Cache ------------ */
			try{
				GestoreMessaggi.initialize();
			}catch(Exception e){
				msgDiag.logStartupError(e,"Cache GestoreMessaggi");
				return;
			}
		
		
		
		
			
			/* ----------- Inizializzazione MessageSecurity ------------ */
			try{
				String wssPropertiesFileExternalPWCallback = propertiesReader.getExternalPWCallbackPropertyFile();
				if(wssPropertiesFileExternalPWCallback!=null){
					ExternalPWCallback.initialize(wssPropertiesFileExternalPWCallback);
				}
				
				// DEVE essere effettuata all'inizio, vedi sopra.
				//org.apache.wss4j.dom.engine.WSSConfig.init();
				
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione MessageSecurity");
				return;
			}

			
			
			
			
			
		
		
		
		
			/* ----------- Inizializzazione Servizi ------------ */
			try{
				StatoServiziPdD.initialize();
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione stato servizi");
				return;
			}
			try{
				RicezioneContenutiApplicativi.initializeService(configurazionePdDManager, classNameReader, propertiesReader, logCore);
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione servizio RicezioneContenutiApplicativi");
				return;
			}
			try{
				RicezioneBuste.initializeService(configurazionePdDManager,classNameReader, propertiesReader, logCore);
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione servizio RicezioneBuste");
				return;
			}
			try{
				InoltroBuste.initializeService(classNameReader, propertiesReader);
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione servizio InoltroBuste");
				return;
			}
			try{
				ConsegnaContenutiApplicativi.initializeService(classNameReader, propertiesReader);
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione servizio ConsegnaContenutiApplicativi");
				return;
			}
			try{
				IDAccordoFactory.getInstance();
				IDAccordoCooperazioneFactory.getInstance();
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione factory ID");
				return;
			}
			
			
			
			
			/* ----------- Inizializzazione Mailcap Activation per Gestione Attachments ------------ */
			try{
				MailcapActivationReader.initDataContentHandler(OpenSPCoop2Startup.log,propertiesReader.isTunnelSOAP_loadMailcap());
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione DataContentHandler (MET-INF/mailcap)");
				return;
			}
		
		
			
			
			
			
			
			
			
			/* ----------- Directory ------------ */
			try{
				
				FileSystemMkdirConfig configMkdir = new FileSystemMkdirConfig();
				configMkdir.setCheckCanWrite(true);
				configMkdir.setCheckCanRead(true);
				configMkdir.setCheckCanExecute(false);
				configMkdir.setCrateParentIfNotExists(true);
				
				// logDir (sarebbe meglio se fosse creata dall'utente)
				List<File> listFiles = OpenSPCoop2Logger.getLogDirs();
				if(listFiles!=null && listFiles.size()>0) {
					for (File file : listFiles) {
						if(file.exists()==false){
							// Il Log può non funzionare
							String msg = "WARNING: Log dir ["+file.getAbsolutePath()+"] non trovata. La directory verrà creata ma è possibile che serva un ulteriore riavvio dell'Application Server";
							log.warn(msg);
							System.out.println(msg);
						}
						FileSystemUtilities.mkdir(file, configMkdir);
					}
				}

				// messageRepository
				if(propertiesReader.isRepositoryOnFS()) {
					File dir = new File(propertiesReader.getRepositoryDirectory());
					FileSystemUtilities.mkdir(dir, configMkdir);
				}
				
				// https
				if(propertiesReader.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_consegnaContenutiApplicativi()) {
					File dir = propertiesReader.getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi();
					configMkdir.setCheckCanWrite(false);
					FileSystemUtilities.mkdir(dir, configMkdir);
					configMkdir.setCheckCanWrite(true);
				}
				if(propertiesReader.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_inoltroBuste()) {
					File dir = propertiesReader.getConnettoreHttp_urlHttps_repository_inoltroBuste();
					configMkdir.setCheckCanWrite(false);
					FileSystemUtilities.mkdir(dir, configMkdir);
					configMkdir.setCheckCanWrite(true);
				}
				
				// recovery
				File dirRecovery = propertiesReader.getFileSystemRecovery_repository();
				FileSystemUtilities.mkdir(dirRecovery, configMkdir);
				FileSystemSerializer fs = FileSystemSerializer.getInstance();
				FileSystemUtilities.mkdir(fs.getDirTransazioni().getAbsolutePath(), configMkdir);
				FileSystemUtilities.mkdir(fs.getDirTransazioneApplicativoServer().getAbsolutePath(), configMkdir);
				FileSystemUtilities.mkdir(fs.getDirDiagnostici().getAbsolutePath(), configMkdir);
				FileSystemUtilities.mkdir(fs.getDirTracce().getAbsolutePath(), configMkdir);
				FileSystemUtilities.mkdir(fs.getDirDump().getAbsolutePath(), configMkdir);
				FileSystemUtilities.mkdir(fs.getDirEventi().getAbsolutePath(), configMkdir);
				
				// dumpNotRealTime
				if(propertiesReader.isDumpNonRealtime_fileSystemMode()) {
					File dir = propertiesReader.getDumpNonRealtime_repository();
					FileSystemUtilities.mkdir(dir, configMkdir);
				}
				
				// attachments
				AttachmentsProcessingMode attachProcessingMode = propertiesReader.getAttachmentsProcessingMode();
				if(attachProcessingMode!=null && attachProcessingMode.getFileRepository()!=null){
					File dir = attachProcessingMode.getFileRepository();
					FileSystemUtilities.mkdir(dir, configMkdir);
				}
				
				// sdkFramework
				File sdkFrameworkDir = propertiesReader.getMonitorSDK_repositoryJars();
				if(sdkFrameworkDir!=null){
					configMkdir.setCheckCanWrite(false);
					FileSystemUtilities.mkdir(sdkFrameworkDir, configMkdir);
					configMkdir.setCheckCanWrite(true);
				}
				
				// controlloTraffico
				if(propertiesReader.isControlloTrafficoEnabled()){
					File dirCT = propertiesReader.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository();
					if(dirCT!=null){
						FileSystemUtilities.mkdir(dirCT, configMkdir);
					}
				}
				
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione Directory");
				return;
			}
			
			
			
			
			
			
			
			
			/* ----------- Gestori utilizzati dal Controllo Traffico ------------ */
			if(propertiesReader.isControlloTrafficoEnabled()){
						
				try{
					propertiesReader.initConfigurazioneControlloTraffico(loader, ProtocolFactoryManager.getInstance().getProtocolNamesAsList());
				}catch(Exception e){
					msgDiag.logStartupError(e,"Inizializzazione Configurazione ControlloTraffico");
					return;
				}
				
				Logger logControlloTraffico = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(propertiesReader.isControlloTrafficoDebug());
				
				// Cache ControlloTraffico DatiStatistici
				try{
					ConfigurazioneGenerale configurazioneControlloTraffico = configurazionePdDManager.getConfigurazioneControlloTraffico();
					if(configurazioneControlloTraffico.getCache()!=null && configurazioneControlloTraffico.getCache().isCache()){
						GestoreCacheControlloTraffico.abilitaCache(configurazioneControlloTraffico.getCache().getSize(),
								CacheAlgorithm.LRU.equals(configurazioneControlloTraffico.getCache().getAlgorithm()),
								configurazioneControlloTraffico.getCache().getIdleTime(), 
								configurazioneControlloTraffico.getCache().getLifeTime(), 
								logCore);
						logControlloTraffico.info("Cache ControlloTraffico inizializzata");
					}
				}catch(Exception e){
					msgDiag.logStartupError(e,"Inizializzazione Cache ControlloTraffico");
					return;
				}
					
				// Gestore dei Dati Statistici
				org.openspcoop2.pdd.core.controllo_traffico.ConfigurazioneControlloTraffico confControlloTraffico = null;
				try{
					confControlloTraffico = propertiesReader.getConfigurazioneControlloTraffico();

					DatiStatisticiDAOManager.initialize(confControlloTraffico);
					
					GestoreControlloTraffico.initialize(confControlloTraffico.isErroreGenerico());
					
					GestoreCacheControlloTraffico.initialize(confControlloTraffico);
					
					GestorePolicyAttive.initialize(logControlloTraffico, propertiesReader.getControlloTrafficoGestorePolicyTipo(),
							propertiesReader.getControlloTrafficoGestorePolicyWSUrl());
					
				}catch(Exception e){
					msgDiag.logStartupError(e,"Inizializzazione Gestori del ControlloTraffico");
					return;
				}
				
				File fDati = null;
				try{
					File fRepository = propertiesReader.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository();
					if(fRepository!=null){
						if(fRepository.exists()==false){
							throw new Exception("Directory ["+fRepository.getAbsolutePath()+"] not exists");
						}
						if(fRepository.isDirectory()==false){
							throw new Exception("File ["+fRepository.getAbsolutePath()+"] is not directory");
						}
						if(fRepository.canRead()==false){
							throw new Exception("File ["+fRepository.getAbsolutePath()+"] cannot read");
						}
						if(fRepository.canWrite()==false){
							throw new Exception("File ["+fRepository.getAbsolutePath()+"] cannot write");
						}
						fDati = new File(fRepository, org.openspcoop2.core.controllo_traffico.constants.Costanti.controlloTrafficoImage);
						if(fDati.exists() && fDati.canRead() && fDati.length()>0){
							FileInputStream fin = new FileInputStream(fDati);
							GestorePolicyAttive.getInstance().initialize(fin,confControlloTraffico);
							fDati.delete();
						}
					}
				}catch(Exception e){
					String img = null;
					if(fDati!=null){
						img = fDati.getAbsolutePath();
					}
					logControlloTraffico.error("Inizializzazione dell'immagine ["+img+"] per il Controllo del Traffico non riuscita: "+e.getMessage(),e);
					logCore.error("Inizializzazione dell'immagine ["+img+"] per il Controllo del Traffico non riuscita: "+e.getMessage(),e);
					msgDiag.logStartupError(e,"Inizializzazione Immagine del ControlloTraffico");
					return;
				}
				
				boolean force = true;
				OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(force).info("Motore di gestione del Controllo del Traffico avviato correttamente");
			}
			
			
			
			
			
			
			
			
			
			// *** Repository MonitorSDK ***
			try{
				DynamicFactory.initialize(propertiesReader.getMonitorSDK_repositoryJars());
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione DynamicFactory");
				return;
			}
			
			
			
			
		
		
		
		
	
		
		
		
		
		
		
			/* ----------- Inizializzazione Risorse JMX ------------ */
			if( OpenSPCoop2Startup.this.gestoreRisorseJMX!=null ){
				// MBean ConfigurazionePdD
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanConfigurazionePdD();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - configurazione");
				}
				// MBean Registro dei Servizi
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanAccessoRegistroServizi();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - accesso al Registro dei Servizi");
				}
				// MBean Monitoraggio Risorse
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanMonitoraggioRisorse();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - monitoraggio delle Risorse");
				}
				// MBean Autorizzazione
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanAutorizzazione();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - dati di autorizzazione");
				}
				// MBean Autenticazione
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanAutenticazione();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - dati di autenticazione");
				}
				// MBean GestioneToken
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanGestioneToken();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - dati di gestione dei token");
				}
				// MBean GestioneResponseCaching
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanResponseCaching();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - risposte salvate in cache");
				}
				// MBean GestioneKeystoreCaching
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanKeystoreCaching();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - keystore salvate in cache");
				}
				// MBean GestioneConsegnaApplicativi
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanConsegnaApplicativi();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - gestione consegna applicativi");
				}
				// MBean RepositoryMessaggi
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanRepositoryMessaggi();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - repository dei messaggi");
				}
				// MBean StatoServiziPdD
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanStatoServiziPdD();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - stato servizi");
				}
				// MBean StatistichePdD
				try{
					if(propertiesReader.isStatisticheViaJmx()) {
						OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanStatistichePdD();
					}
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - statistiche");
				}
				// MBean SystemPropertiesPdD
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanSystemPropertiesPdD();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - proprietà di sistema");
				}
				// MBean ConfigurazioneSistema
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanConfigurazioneSistema();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - configurazione di sistema");
				}
				if(propertiesReader.isControlloTrafficoEnabled()){
					// MBean ControlloTraffico
					try{
						OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanControlloTraffico();
					}catch(Exception e){
						msgDiag.logStartupError(e,"RisorsaJMX - Controllo del Traffico");
					}
				}
				
			}


			
			
			
			
			/* ----------- Log Configurazione di Sistema ------------ */
			
			InformazioniStatoPorta informazioniStatoPorta = new InformazioniStatoPorta();
			List<InformazioniStatoPortaCache> informazioniStatoPortaCache = new ArrayList<InformazioniStatoPortaCache>();
			
			AccessoRegistroServizi infoRegistroServizi = new AccessoRegistroServizi();
			InformazioniStatoPortaCache informazioniStatoPortaCache_registro = new InformazioniStatoPortaCache(CostantiPdD.JMX_REGISTRO_SERVIZI, infoRegistroServizi.isCacheAbilitata());
			if(infoRegistroServizi.isCacheAbilitata()){
				informazioniStatoPortaCache_registro.setStatoCache(infoRegistroServizi.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCache_registro);
			
			org.openspcoop2.pdd.core.jmx.ConfigurazionePdD infoConfigurazione = new org.openspcoop2.pdd.core.jmx.ConfigurazionePdD();
			InformazioniStatoPortaCache informazioniStatoPortaCache_config = new InformazioniStatoPortaCache(CostantiPdD.JMX_CONFIGURAZIONE_PDD, infoConfigurazione.isCacheAbilitata());
			if(infoConfigurazione.isCacheAbilitata()){
				informazioniStatoPortaCache_config.setStatoCache(infoConfigurazione.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCache_config);
			
			org.openspcoop2.pdd.core.jmx.EngineAutorizzazione infoAutorizzazioneDati = new org.openspcoop2.pdd.core.jmx.EngineAutorizzazione();
			InformazioniStatoPortaCache informazioniStatoPortaCache_autorizzazioneDati = new InformazioniStatoPortaCache(CostantiPdD.JMX_AUTORIZZAZIONE, infoAutorizzazioneDati.isCacheAbilitata());
			if(infoAutorizzazioneDati.isCacheAbilitata()){
				informazioniStatoPortaCache_autorizzazioneDati.setStatoCache(infoAutorizzazioneDati.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCache_autorizzazioneDati);
			
			org.openspcoop2.pdd.core.jmx.EngineAutenticazione infoAutenticazioneDati = new org.openspcoop2.pdd.core.jmx.EngineAutenticazione();
			InformazioniStatoPortaCache informazioniStatoPortaCache_autenticazioneDati = new InformazioniStatoPortaCache(CostantiPdD.JMX_AUTENTICAZIONE, infoAutenticazioneDati.isCacheAbilitata());
			if(infoAutenticazioneDati.isCacheAbilitata()){
				informazioniStatoPortaCache_autenticazioneDati.setStatoCache(infoAutenticazioneDati.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCache_autenticazioneDati);
			
			org.openspcoop2.pdd.core.jmx.EngineGestioneToken infoGestioneTokenDati = new org.openspcoop2.pdd.core.jmx.EngineGestioneToken();
			InformazioniStatoPortaCache informazioniStatoPortaCache_gestioneTokenDati = new InformazioniStatoPortaCache(CostantiPdD.JMX_TOKEN, infoGestioneTokenDati.isCacheAbilitata());
			if(infoGestioneTokenDati.isCacheAbilitata()){
				informazioniStatoPortaCache_gestioneTokenDati.setStatoCache(infoGestioneTokenDati.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCache_gestioneTokenDati);
			
			org.openspcoop2.pdd.core.jmx.EngineResponseCaching infoResponseCaching = new org.openspcoop2.pdd.core.jmx.EngineResponseCaching();
			InformazioniStatoPortaCache informazioniStatoPortaCache_responseCaching = new InformazioniStatoPortaCache(CostantiPdD.JMX_RESPONSE_CACHING, infoResponseCaching.isCacheAbilitata());
			if(infoResponseCaching.isCacheAbilitata()){
				informazioniStatoPortaCache_responseCaching.setStatoCache(infoResponseCaching.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCache_responseCaching);
			
			org.openspcoop2.pdd.core.jmx.EngineKeystoreCaching infoKeystoreCaching = new org.openspcoop2.pdd.core.jmx.EngineKeystoreCaching();
			InformazioniStatoPortaCache informazioniStatoPortaCache_keystoreCaching = new InformazioniStatoPortaCache(CostantiPdD.JMX_KEYSTORE_CACHING, infoKeystoreCaching.isCacheAbilitata());
			if(infoKeystoreCaching.isCacheAbilitata()){
				informazioniStatoPortaCache_keystoreCaching.setStatoCache(infoKeystoreCaching.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCache_keystoreCaching);
			
			org.openspcoop2.pdd.core.jmx.GestoreConsegnaApplicativi infoGestoreConsegnaApplicativi = new org.openspcoop2.pdd.core.jmx.GestoreConsegnaApplicativi();
			InformazioniStatoPortaCache informazioniStatoPortaCache_gestoreConsegnaApplicativi = new InformazioniStatoPortaCache(CostantiPdD.JMX_LOAD_BALANCER, infoGestoreConsegnaApplicativi.isCacheAbilitata());
			if(infoGestoreConsegnaApplicativi.isCacheAbilitata()){
				informazioniStatoPortaCache_gestoreConsegnaApplicativi.setStatoCache(infoGestoreConsegnaApplicativi.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCache_gestoreConsegnaApplicativi);
			
			org.openspcoop2.pdd.core.jmx.RepositoryMessaggi infoRepositoryMessaggi = new org.openspcoop2.pdd.core.jmx.RepositoryMessaggi();
			InformazioniStatoPortaCache informazioniStatoPortaCache_repositoryMessaggi = new InformazioniStatoPortaCache(CostantiPdD.JMX_REPOSITORY_MESSAGGI, infoRepositoryMessaggi.isCacheAbilitata());
			if(infoRepositoryMessaggi.isCacheAbilitata()){
				informazioniStatoPortaCache_repositoryMessaggi.setStatoCache(infoRepositoryMessaggi.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCache_repositoryMessaggi);
			
			ConfigurazioneSistema infoConfigSistema = new ConfigurazioneSistema();
			StatoServiziJMXResource statoServiziPdD = new StatoServiziJMXResource();
			OpenSPCoop2Logger.getLoggerOpenSPCoopConfigurazioneSistema().
				info(informazioniStatoPorta.formatStatoPorta(infoConfigSistema.getVersionePdD(), 
						infoConfigSistema.getVersioneBaseDati(), infoConfigSistema.getDirectoryConfigurazione(), 
						infoConfigSistema.getVersioneJava(), infoConfigSistema.getVendorJava(), infoConfigSistema.getMessageFactory(),
						statoServiziPdD.getComponentePD(), statoServiziPdD.getComponentePD_abilitazioniPuntuali(), statoServiziPdD.getComponentePD_disabilitazioniPuntuali(),
						statoServiziPdD.getComponentePA(), statoServiziPdD.getComponentePA_abilitazioniPuntuali(), statoServiziPdD.getComponentePA_disabilitazioniPuntuali(),
						statoServiziPdD.getComponenteIM(),
						LogLevels.toOpenSPCoop2(configurazionePdDManager.getSeverita_msgDiagnostici(),true),
						LogLevels.toOpenSPCoop2(configurazionePdDManager.getSeveritaLog4J_msgDiagnostici(),true),
						OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato, OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato, OpenSPCoop2Logger.loggerIntegrationManagerAbilitato,
						configurazionePdDManager.tracciamentoBuste(), 
						configurazionePdDManager.dumpBinarioPD(), configurazionePdDManager.dumpBinarioPA(),
						OpenSPCoop2Logger.loggerTracciamentoAbilitato, OpenSPCoop2Logger.loggerDumpAbilitato,
						propertiesReader.getFileTraceGovWayState().toString(),
						ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST(), 
						(ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE() && ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR()),
						ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR(),
						Costanti.TRANSACTION_ERROR_STATUS_ABILITATO, Costanti.TRANSACTION_ERROR_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE,
						Costanti.TRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS, Costanti.TRANSACTION_ERROR_INSTANCE_ID_ABILITATO, Costanti.TRANSACTION_ERROR_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE,
						infoConfigSistema.getInformazioniDatabase(), infoConfigSistema.getInformazioniAltriDatabase(),
						infoConfigSistema.getInformazioniSSL(true,true),
						infoConfigSistema.getInformazioniCryptographyKeyLength(),
						infoConfigSistema.getInformazioniCharset(),
						infoConfigSistema.getInformazioniInternazionalizzazione(true),
						infoConfigSistema.getInformazioniTimeZone(true),
						infoConfigSistema.getInformazioniProprietaJava(true, true, false),
						infoConfigSistema.getInformazioniProprietaJava(true, false, true),
						infoConfigSistema.getInformazioniProprietaSistema(),
						infoConfigSistema.getPluginProtocols(), 
						infoConfigSistema.getInformazioniInstallazione(),
						informazioniStatoPortaCache.toArray(new InformazioniStatoPortaCache[1])));

			
			
			
			
			
	
			
			
			
			// Inizializzazione Init Handler
			try{
				InitContext initContext = new InitContext();
				initContext.setLogCore(logCore);
				initContext.setLogConsole(OpenSPCoop2Startup.log);
				initContext.setPddContext(OpenSPCoop2Startup.this.pddContext);
				GestoreHandlers.init(initContext, msgDiag,OpenSPCoop2Startup.log); // per avere gli init stampati su server log
			}catch(HandlerException e){
				if(e.isEmettiDiagnostico()){
					msgDiag.logStartupError(e,e.getIdentitaHandler());
				}
				else{
					logCore.error(e.getMessage(),e);
				}
				return;
			}catch(Exception e){
				msgDiag.logStartupError(e,"InitHandler");
				return;
			}
			
			
			
			
			



			// Inizializzazione Timer per il check delle risorse
			try{
				if( propertiesReader.isAbilitatoControlloRisorseConfigurazione() ||
						propertiesReader.isAbilitatoControlloValidazioneSemanticaConfigurazione() ||
						propertiesReader.isAbilitatoControlloRisorseDB() ||
						propertiesReader.isAbilitatoControlloRisorseJMS() ||
						propertiesReader.isAbilitatoControlloRisorseMsgDiagnosticiPersonalizzati() || 
						propertiesReader.isAbilitatoControlloRisorseRegistriServizi() ||
						propertiesReader.isAbilitatoControlloValidazioneSemanticaRegistriServizi() ||
						propertiesReader.isAbilitatoControlloRisorseTracciamentiPersonalizzati()){
					OpenSPCoop2Startup.this.timerMonitoraggioRisorse = new TimerMonitoraggioRisorseThread();
					OpenSPCoop2Startup.this.timerMonitoraggioRisorse.start();
					OpenSPCoop2Startup.log.info("Inizializzo Timer per il Monitoraggio delle Risorse");
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"TimerMonitoraggioRisorse");
				return;
			}





			// Inizializzazione Timer per il check del Threshold
			try{
				String [] tipiThreshold = propertiesReader.getRepositoryThresholdTypes();
				if(tipiThreshold!=null){
					OpenSPCoop2Startup.this.timerThreshold = new TimerThresholdThread();
					OpenSPCoop2Startup.this.timerThreshold.start();
					OpenSPCoop2Startup.log.info("Inizializzo Timer per il Controllo dei Threshold sulle risorse");
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"TimerThreshold");
				return;
			}






			// Inizializzazione delle risorse esterne terminata
			OpenSPCoop2Startup.initialize = true;
			Utilities.sleep(1000);








			/* -------- Check deploy timer ------ */
			boolean gestoreBusteNonRiscontrate = false;
			boolean gestoreMessaggi = false;
			boolean gestorePuliziaMessaggiAnomali = false;
			boolean gestoreRepository=false;
			if(OpenSPCoop2Startup.this.serverJ2EE){
				long scadenzaWhile = System.currentTimeMillis() + propertiesReader.getTimerEJBDeployTimeout();
				GestoreJNDI jndi = null;
				if(propertiesReader.getJNDIContext_TimerEJB()==null)
					jndi = new GestoreJNDI();
				else
					jndi = new GestoreJNDI(propertiesReader.getJNDIContext_TimerEJB());

				while( (System.currentTimeMillis() < scadenzaWhile)
						&& 
						(gestoreBusteNonRiscontrate==false
								|| gestoreMessaggi==false
								|| gestorePuliziaMessaggiAnomali==false
								|| gestoreRepository==false)){

					gestoreBusteNonRiscontrate = false;
					gestoreMessaggi = false;
					gestorePuliziaMessaggiAnomali = false;
					gestoreRepository=false;

					// check Timer Gestore Riscontri
					if(propertiesReader.isTimerGestoreRiscontriRicevuteAbilitato()){
						try{
							String nomeJNDI = propertiesReader.getJNDITimerEJBName().get(TimerGestoreBusteNonRiscontrate.ID_MODULO);
							OpenSPCoop2Startup.log.info("Inizializzo EJB gestore riscontri ["+nomeJNDI+"]");
							jndi.lookup(nomeJNDI);
							gestoreBusteNonRiscontrate = true;
						}catch(Exception e){
							this.logError("Search EJB gestore riscontri non trovato: "+e.getMessage(),e);
							try {
								Utilities.sleep((new java.util.Random()).nextInt(propertiesReader.getTimerEJBDeployCheckInterval())); // random da 0ms a TransactionManagerCheckInterval ms
							}catch(Exception eRandom){}
							continue;
						}
					}else{
						gestoreBusteNonRiscontrate = true;
					}

					//  check Timer Gestore Messaggi
					if(propertiesReader.isTimerGestoreMessaggiAbilitato()){
						try{
							String nomeJNDI = propertiesReader.getJNDITimerEJBName().get(TimerGestoreMessaggi.ID_MODULO);
							OpenSPCoop2Startup.log.info("Inizializzo EJB gestore messaggi ["+nomeJNDI+"]");
							jndi.lookup(nomeJNDI);
							gestoreMessaggi = true;
						}catch(Exception e){
							this.logError("Search EJB gestore messaggi non trovato: "+e.getMessage(),e);
							try{
								Utilities.sleep((new java.util.Random()).nextInt(propertiesReader.getTimerEJBDeployCheckInterval())); // random da 0ms a TransactionManagerCheckInterval ms
							}catch(Exception eRandom){}
							continue;
						}
					}else{
						gestoreMessaggi = true;
					}

					//  check Timer Gestore Pulizia Messaggi Anomali
					if(propertiesReader.isTimerGestorePuliziaMessaggiAnomaliAbilitato()){
						try{
							String nomeJNDI = propertiesReader.getJNDITimerEJBName().get(TimerGestorePuliziaMessaggiAnomali.ID_MODULO);
							OpenSPCoop2Startup.log.info("Inizializzo EJB gestore pulizia messaggi anomali ["+nomeJNDI+"]");
							jndi.lookup(nomeJNDI);
							gestorePuliziaMessaggiAnomali = true;
						}catch(Exception e){
							this.logError("Search EJB pulizia messaggi anomali non trovato: "+e.getMessage(),e);
							try{
								Utilities.sleep((new java.util.Random()).nextInt(propertiesReader.getTimerEJBDeployCheckInterval())); // random da 0ms a TransactionManagerCheckInterval ms
							}catch(Exception eRandom){}
							continue;
						}
					}else{
						gestorePuliziaMessaggiAnomali = true;
					}


					//  check Timer Gestore Repository
					if(propertiesReader.isTimerGestoreRepositoryBusteAbilitato()){
						try{
							String nomeJNDI = propertiesReader.getJNDITimerEJBName().get(TimerGestoreRepositoryBuste.ID_MODULO);
							OpenSPCoop2Startup.log.info("Inizializzo EJB gestore repository ["+nomeJNDI+"]");
							jndi.lookup(nomeJNDI);
							gestoreRepository = true;
						}catch(Exception e){
							this.logError("Search EJB gestore repository non trovato: "+e.getMessage(),e);
							try{
								Utilities.sleep((new java.util.Random()).nextInt(propertiesReader.getTimerEJBDeployCheckInterval())); // random da 0ms a TransactionManagerCheckInterval ms
							}catch(Exception eRandom){}
							continue;
						}
					}else{
						gestoreRepository = true;
					}
				}
			}






			/* ------------ Avvia il thread per la gestione dei Riscontri Scaduti  ------------ */
			if(OpenSPCoop2Startup.this.serverJ2EE){
				if(propertiesReader.isTimerGestoreRiscontriRicevuteAbilitato()){
					if(gestoreBusteNonRiscontrate){
						try{
							OpenSPCoop2Startup.this.timerRiscontri = TimerUtils.createTimerGestoreBusteNonRiscontrate();
						}catch(Exception e){
							msgDiag.logStartupError(e,"Creazione timer '"+TimerGestoreBusteNonRiscontrate.ID_MODULO+"'");
						}
						if(OpenSPCoop2Startup.this.timerRiscontri != null) {
							try {
								OpenSPCoop2Startup.this.timerRiscontri.start();
							} catch (RemoteException e) {
								msgDiag.logStartupError(e,"Avvio timer '"+TimerGestoreBusteNonRiscontrate.ID_MODULO+"'");
							}
						}else{
							msgDiag.logStartupError("timer is null","Avvio timer '"+TimerGestoreBusteNonRiscontrate.ID_MODULO+"'");
						}
					}else{
						msgDiag.logStartupError("timer non esiste nell'application server","Avvio timer '"+TimerGestoreBusteNonRiscontrate.ID_MODULO+"'");
					}
				}else{
					msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_RISCONTRI_RICEVUTE);
					msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI, TimerGestoreBusteNonRiscontrate.ID_MODULO);
					msgDiag.logPersonalizzato("disabilitato");
					msgDiag.setPrefixMsgPersonalizzati(null);
				}
			}









			/* ------------ Avvia il thread per l'eliminazione dei messaggi  ------------ */
			if(propertiesReader.isTimerGestoreMessaggiAbilitato()){
				if(OpenSPCoop2Startup.this.serverJ2EE){
					if(gestoreMessaggi){
						try{
							OpenSPCoop2Startup.this.timerEliminazioneMsg = TimerUtils.createTimerGestoreMessaggi();
						}catch(Exception e){
							msgDiag.logStartupError(e,"Creazione timer '"+TimerGestoreMessaggi.ID_MODULO+"'");
						}
						if(OpenSPCoop2Startup.this.timerEliminazioneMsg != null) {
							try {
								OpenSPCoop2Startup.this.timerEliminazioneMsg.start();
							} catch (RemoteException e) {
								msgDiag.logStartupError(e,"Avvio timer '"+TimerGestoreMessaggi.ID_MODULO+"'");
							}
						}else{
							msgDiag.logStartupError("timer is null","Avvio timer '"+TimerGestoreMessaggi.ID_MODULO+"'");
						}
					}else{
						msgDiag.logStartupError("timer non esiste nell'application server","Avvio timer '"+TimerGestoreMessaggi.ID_MODULO+"'");
					}
				}else{
					try{
						OpenSPCoop2Startup.this.threadEliminazioneMsg = new TimerGestoreMessaggiThread();
						OpenSPCoop2Startup.this.threadEliminazioneMsg.start();
					}catch(Exception e){
						msgDiag.logStartupError(e,"Avvio timer (thread) '"+TimerGestoreMessaggi.ID_MODULO+"'");
					}
				}
			}else{
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI);
				msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI, TimerGestoreMessaggi.ID_MODULO);
				msgDiag.logPersonalizzato("disabilitato");
				msgDiag.setPrefixMsgPersonalizzati(null);
			}









			/* ------------ Avvia il thread per l'eliminazione dei messaggi anomali ------------ */
			if(propertiesReader.isTimerGestorePuliziaMessaggiAnomaliAbilitato()){
				if(OpenSPCoop2Startup.this.serverJ2EE){
					if(gestorePuliziaMessaggiAnomali){
						try{
							OpenSPCoop2Startup.this.timerPuliziaMsgAnomali = TimerUtils.createTimerGestorePuliziaMessaggiAnomali();
						}catch(Exception e){
							msgDiag.logStartupError(e,"Creazione timer '"+TimerGestorePuliziaMessaggiAnomali.ID_MODULO+"'");
						}
						if(OpenSPCoop2Startup.this.timerPuliziaMsgAnomali != null) {
							try {
								OpenSPCoop2Startup.this.timerPuliziaMsgAnomali.start();
							} catch (RemoteException e) {
								msgDiag.logStartupError(e,"Avvio timer '"+TimerGestorePuliziaMessaggiAnomali.ID_MODULO+"'");
							}
						}else{
							msgDiag.logStartupError("timer is null","Avvio timer '"+TimerGestorePuliziaMessaggiAnomali.ID_MODULO+"'");
						}
					}else{
						msgDiag.logStartupError("timer non esiste nell'application server","Avvio timer '"+TimerGestorePuliziaMessaggiAnomali.ID_MODULO+"'");
					}
				}else{
					try{
						OpenSPCoop2Startup.this.threadPuliziaMsgAnomali = new TimerGestorePuliziaMessaggiAnomaliThread();
						OpenSPCoop2Startup.this.threadPuliziaMsgAnomali.start();
					}catch(Exception e){
						msgDiag.logStartupError(e,"Avvio timer (thread) '"+TimerGestorePuliziaMessaggiAnomali.ID_MODULO+"'");
					}
				}
			}else{
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI_INCONSISTENTI);
				msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_INCONSISTENTI, TimerGestorePuliziaMessaggiAnomali.ID_MODULO);
				msgDiag.logPersonalizzato("disabilitato");
				msgDiag.setPrefixMsgPersonalizzati(null);
			}







			/* ------------ Avvia il thread per l'eliminazione delle buste  ------------ */
			if(propertiesReader.isTimerGestoreRepositoryBusteAbilitato()){
				if(OpenSPCoop2Startup.this.serverJ2EE){
					if(gestoreRepository){
						try{
							OpenSPCoop2Startup.this.timerRepositoryBuste = TimerUtils.createTimerGestoreRepositoryBuste();
						}catch(Exception e){
							msgDiag.logStartupError(e,"Creazione timer '"+TimerGestoreRepositoryBuste.ID_MODULO+"'");
						}
						if(OpenSPCoop2Startup.this.timerRepositoryBuste != null) {
							try {
								OpenSPCoop2Startup.this.timerRepositoryBuste.start();
							} catch (RemoteException e) {
								msgDiag.logStartupError(e,"Avvio timer '"+TimerGestoreRepositoryBuste.ID_MODULO+"'");
							}
						}else{
							msgDiag.logStartupError("timer is null","Avvio timer '"+TimerGestoreRepositoryBuste.ID_MODULO+"'");
						}
					}else{
						msgDiag.logStartupError("timer non esiste nell'application server","Avvio timer '"+TimerGestoreRepositoryBuste.ID_MODULO+"'");
					}
				}else{
					try{
						OpenSPCoop2Startup.this.threadRepositoryBuste = new TimerGestoreRepositoryBusteThread();
						OpenSPCoop2Startup.this.threadRepositoryBuste.start();
					}catch(Exception e){
						msgDiag.logStartupError(e,"Avvio timer (thread) '"+TimerGestoreRepositoryBuste.ID_MODULO+"'");
					}
				}
			}else{
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_REPOSITORY_BUSTE);
				msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_REPOSITORY_BUSTE, TimerGestoreRepositoryBuste.ID_MODULO);
				msgDiag.logPersonalizzato("disabilitato");
				msgDiag.setPrefixMsgPersonalizzati(null);
			}




			
			
			
			
			
			/* ------------ Avvia il thread per la riconsegna dei messaggi per ContenutiApplicativi  ------------ */
			if(OpenSPCoop2Startup.this.serverJ2EE==false){
				if(propertiesReader.isTimerConsegnaContenutiApplicativiAbilitato()){
					try{
						OpenSPCoop2Startup.this.threadConsegnaContenutiApplicativi = new TimerConsegnaContenutiApplicativiThread();
						OpenSPCoop2Startup.this.threadConsegnaContenutiApplicativi.start();
						OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRef = OpenSPCoop2Startup.this.threadConsegnaContenutiApplicativi;
					}catch(Exception e){
						msgDiag.logStartupError(e,"Avvio timer (thread) '"+TimerConsegnaContenutiApplicativiThread.ID_MODULO+"'");
					}
				}else{
					msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI);
					msgDiag.addKeyword(CostantiPdD.KEY_TIMER, TimerConsegnaContenutiApplicativiThread.ID_MODULO);
					msgDiag.logPersonalizzato("disabilitato");
					msgDiag.setPrefixMsgPersonalizzati(null);
				}
			}
			
			
			
			
			
			
			/* ------------ Avvia il thread per la generazione delle statistiche  ------------ */
			if(propertiesReader.isStatisticheGenerazioneEnabled()){
				
				// stat orarie
				String idTimerStatOrarie = "Timer"+TipoIntervalloStatistico.STATISTICHE_ORARIE.getValue();
				if(propertiesReader.isStatisticheGenerazioneBaseOrariaEnabled()) {
					try{
						OpenSPCoop2Startup.this.threadGenerazioneStatisticheOrarie = 
								new TimerStatisticheThread(propertiesReader.getStatisticheOrarieGenerazioneTimerIntervalSeconds(), TipoIntervalloStatistico.STATISTICHE_ORARIE);
						OpenSPCoop2Startup.this.threadGenerazioneStatisticheOrarie.start();
					}catch(Exception e){
						msgDiag.logStartupError(e,"Avvio timer (thread) '"+idTimerStatOrarie+"'");
					}
				}else{
					msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE);
					msgDiag.addKeyword(CostantiPdD.KEY_TIMER, idTimerStatOrarie);
					msgDiag.logPersonalizzato("disabilitato");
					msgDiag.setPrefixMsgPersonalizzati(null);
				}
				
				// stat giornaliere
				String idTimerStatGiornaliere = "Timer"+TipoIntervalloStatistico.STATISTICHE_GIORNALIERE.getValue();
				if(propertiesReader.isStatisticheGenerazioneBaseGiornalieraEnabled()) {
					try{
						OpenSPCoop2Startup.this.threadGenerazioneStatisticheGiornaliere = 
								new TimerStatisticheThread(propertiesReader.getStatisticheGiornaliereGenerazioneTimerIntervalSeconds(), TipoIntervalloStatistico.STATISTICHE_GIORNALIERE);
						OpenSPCoop2Startup.this.threadGenerazioneStatisticheGiornaliere.start();
					}catch(Exception e){
						msgDiag.logStartupError(e,"Avvio timer (thread) '"+idTimerStatGiornaliere+"'");
					}
				}else{
					msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE);
					msgDiag.addKeyword(CostantiPdD.KEY_TIMER, idTimerStatGiornaliere);
					msgDiag.logPersonalizzato("disabilitato");
					msgDiag.setPrefixMsgPersonalizzati(null);
				}
				
				// stat settimanali
				String idTimerStatSettimanali = "Timer"+TipoIntervalloStatistico.STATISTICHE_SETTIMANALI.getValue();
				if(propertiesReader.isStatisticheGenerazioneBaseSettimanaleEnabled()) {
					try{
						OpenSPCoop2Startup.this.threadGenerazioneStatisticheSettimanali = 
								new TimerStatisticheThread(propertiesReader.getStatisticheSettimanaliGenerazioneTimerIntervalSeconds(), TipoIntervalloStatistico.STATISTICHE_SETTIMANALI);
						OpenSPCoop2Startup.this.threadGenerazioneStatisticheSettimanali.start();
					}catch(Exception e){
						msgDiag.logStartupError(e,"Avvio timer (thread) '"+idTimerStatSettimanali+"'");
					}
				}else{
					msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE);
					msgDiag.addKeyword(CostantiPdD.KEY_TIMER, idTimerStatSettimanali);
					msgDiag.logPersonalizzato("disabilitato");
					msgDiag.setPrefixMsgPersonalizzati(null);
				}
				
				// stat mensili
				String idTimerStatMensili = "Timer"+TipoIntervalloStatistico.STATISTICHE_MENSILI.getValue();
				if(propertiesReader.isStatisticheGenerazioneBaseMensileEnabled()) {
					try{
						OpenSPCoop2Startup.this.threadGenerazioneStatisticheMensili = 
								new TimerStatisticheThread(propertiesReader.getStatisticheMensiliGenerazioneTimerIntervalSeconds(), TipoIntervalloStatistico.STATISTICHE_MENSILI);
						OpenSPCoop2Startup.this.threadGenerazioneStatisticheMensili.start();
					}catch(Exception e){
						msgDiag.logStartupError(e,"Avvio timer (thread) '"+idTimerStatMensili+"'");
					}
				}else{
					msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE);
					msgDiag.addKeyword(CostantiPdD.KEY_TIMER, idTimerStatMensili);
					msgDiag.logPersonalizzato("disabilitato");
					msgDiag.setPrefixMsgPersonalizzati(null);
				}
			}
			else{
				// Tutti i timers sono disabilitati
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE);
				msgDiag.addKeyword(CostantiPdD.KEY_TIMER, TimerStatisticheThread.ID_MODULO);
				msgDiag.logPersonalizzato("disabilitato");
				msgDiag.setPrefixMsgPersonalizzati(null);
			}
			
			
			
			
			/* ------------ Avvia il thread per la gestione Stateful delle transazioni ------------ */
			Logger forceLogTransazioniStateful = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniStateful(true);
			try{
				boolean debug = propertiesReader.isTransazioniStatefulDebug();
				
				Logger logTransazioniStateful = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniStateful(debug);
							
				if(propertiesReader.isTransazioniStatefulEnabled()){
					if(debug)
						logTransazioniStateful.debug("Avvio inizializzazione thread per gestione transazioni stateful ...");
					OpenSPCoop2Startup.this.threadRepositoryStateful = new TimerRepositoryStatefulThread();
					OpenSPCoop2Startup.this.threadRepositoryStateful.start();
					forceLogTransazioniStateful.info("Thread per la gestione transazioni stateful avviato correttamente");
				}
				else{
					forceLogTransazioniStateful.info("Thread per la gestione transazioni stateful disabilitato");
				}
			
			}catch(Exception e){
				String msgError = "Inizializzazione thread per la gestione transazioni stateful non riuscita: "+e.getMessage();
				forceLogTransazioniStateful.error(msgError,e);
				msgDiag.logStartupError(e,"Inizializzazione Gesotre Transazioni Stateful");
				return;
			}
			
			
			
			
			/* ------------ Avvia il thread per il Recovery FileSystem ------------ */
			Logger forceLogRecoveryFileSystem = OpenSPCoop2Logger.getLoggerOpenSPCoopFileSystemRecovery(true);
			try{
				boolean debug = propertiesReader.isFileSystemRecoveryDebug();
				
				Logger logRecoveryFS = OpenSPCoop2Logger.getLoggerOpenSPCoopFileSystemRecovery(debug);
							
				if(propertiesReader.isFileSystemRecoveryTimerEnabled()){
					if(debug)
						logRecoveryFS.debug("Avvio inizializzazione thread per recovery da file system ...");
					OpenSPCoop2Startup.this.threadFileSystemRecovery = new TimerFileSystemRecoveryThread(logRecoveryFS,
							OpenSPCoop2Logger.getLoggerOpenSPCoopFileSystemRecoverySql(debug));
					OpenSPCoop2Startup.this.threadFileSystemRecovery.start();
					forceLogRecoveryFileSystem.info("Thread per la gestione transazioni stateful avviato correttamente");
				}
				else{
					forceLogRecoveryFileSystem.info("Thread per il recovery da file system disabilitato");
				}
			
			}catch(Exception e){
				String msgError = "Inizializzazione thread per il recovery da file system non riuscita: "+e.getMessage();
				forceLogRecoveryFileSystem.error(msgError,e);
				msgDiag.logStartupError(e,"Inizializzazione Recovery da FileSystem");
				return;
			}
			
			
			
	
			
			/* ------------ Eventi (ed in oltre avvia il thread) ------------ */
			Logger forceLogEventi = OpenSPCoop2Logger.getLoggerOpenSPCoopEventi(true);
			try{
				if(propertiesReader.isEventiEnabled()){
					OpenSPCoop2Startup.this.gestoreEventi = GestoreEventi.getInstance();
					boolean debugEventi = propertiesReader.isEventiDebug();
					Logger logEventi = OpenSPCoop2Logger.getLoggerOpenSPCoopEventi(debugEventi);
					
					if(propertiesReader.isEventiRegistrazioneStatoPorta()){
						Evento evento = new Evento();
						evento.setTipo(TipoEvento.STATO_GATEWAY.getValue());
						evento.setCodice(CodiceEventoStatoGateway.START.getValue());
						evento.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.INFO));
						evento.setClusterId(clusterID);
						OpenSPCoop2Startup.this.gestoreEventi.log(evento);
					}
					
					// Timer
					if(propertiesReader.isEventiTimerEnabled()){
						try{
							if(debugEventi)
								logEventi.debug("Avvio inizializzazione thread per Eventi ...");
							OpenSPCoop2Startup.this.threadEventi = new TimerEventiThread(logEventi);
							OpenSPCoop2Startup.this.threadEventi.start();
							forceLogEventi.info("Thread per gli Eventi avviato correttamente");
						}catch(Exception e){
							throw new HandlerException("Avvio timer degli eventi fallito: "+e.getMessage(),e);
						}
					}
					else{
						forceLogEventi.info("Thread per gli Eventi disabilitato");
					}
				}
			}catch(Exception e){
				String msgError = "Inizializzazione gestore eventi: "+e.getMessage();
				forceLogEventi.error(msgError,e);
				msgDiag.logStartupError(e,"Inizializzazione GestoreEventi");
				return;
			}
			
			
			
			
			
			
			
			
			
			

			/* ------------ Jminix StandaloneMiniConsole  ------------ */
			if(propertiesReader.getPortJminixConsole()!=null){
				try{
					jminixStandaloneConsole = new StandaloneMiniConsole(propertiesReader.getPortJminixConsole());
					log.info("JminixStandaloneConsole correttamente avviata");
					logCore.info("JminixStandaloneConsole correttamente avviata");
				}catch(Throwable e){
					logCore.error("Errore durante l'avvio della jminixStandaloneConsole: "+e.getMessage(),e);
					msgDiag.logStartupError(e,"Inizializzazione JminixStandaloneConsole");
				}
			}
			
			
			



			/* ------------ OpenSPCoop startup terminato  ------------ */
			long endDate = System.currentTimeMillis();
			long secondStarter = (endDate - OpenSPCoop2Startup.this.startDate) / 1000;
			msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_OPENSPCOOP_STARTUP);
			msgDiag.addKeyword(CostantiPdD.KEY_VERSIONE_PORTA, propertiesReader.getPddDetailsForLog());
			msgDiag.addKeyword(CostantiPdD.KEY_TEMPO_AVVIO, secondStarter+" secondi");
			OpenSPCoop2Startup.log.info(propertiesReader.getPddDetailsForLog()+" avviata correttamente in "+secondStarter+" secondi.");
			if(OpenSPCoop2Logger.isLoggerOpenSPCoopConsoleStartupAgganciatoLog()){
				// per farlo finire anche sul server.log
				System.out.println(propertiesReader.getPddDetailsForLog()+" avviata correttamente in "+secondStarter+" secondi.");
			}
			msgDiag.logPersonalizzato("pdd");
			MsgDiagnostico msgIM = MsgDiagnostico.newInstance(IntegrationManager.ID_MODULO);
			msgIM.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_OPENSPCOOP_STARTUP);
			msgIM.addKeyword(CostantiPdD.KEY_VERSIONE_PORTA, propertiesReader.getPddDetailsForLog());
			msgIM.addKeyword(CostantiPdD.KEY_TEMPO_AVVIO, secondStarter+" secondi");
			msgIM.logPersonalizzato("IntegrationManager");
			

		}

	}
	
	/**
	 * Undeploy dell'applicazione WEB di OpenSPCoop
	 *
	 * @param sce Servlet Context Event
	 * 
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {

		OpenSPCoop2Startup.contextDestroyed = true;
		
		OpenSPCoop2Properties properties = null;
		try {
			properties = OpenSPCoop2Properties.getInstance();
		}catch(Throwable e){}
		
		// Eventi
		try{
			String clusterID = properties.getClusterId(false);
			boolean debugEventi = properties.isEventiDebug();
			Logger logEventi = OpenSPCoop2Logger.getLoggerOpenSPCoopEventi(debugEventi);
			Evento eventoShutdown = null;
			try{
				if(OpenSPCoop2Startup.this.gestoreEventi!=null){
					if(properties.isEventiRegistrazioneStatoPorta()){
						eventoShutdown = new Evento();
						eventoShutdown.setTipo(TipoEvento.STATO_GATEWAY.getValue());
						eventoShutdown.setCodice(CodiceEventoStatoGateway.STOP.getValue());
						eventoShutdown.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.INFO));
						eventoShutdown.setClusterId(clusterID);
						OpenSPCoop2Startup.this.gestoreEventi.log(eventoShutdown,true);
					}
				}
			}catch(Exception e){
				// L'errore puo' avvenire poiche' lo shutdown puo' anche disattivare il datasource
				logEventi.debug("Errore durante la segnalazione di shutdown ('Emissione Evento'): "+e.getMessage(),e);
				if(eventoShutdown!=null){
					try{
				    	if(eventoShutdown.getOraRegistrazione()==null){
				    		eventoShutdown.setOraRegistrazione(DateManager.getDate());
				    	}
				    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
				    	eventoShutdown.writeTo(bout, WriteToSerializerType.XML_JAXB);
				    	bout.flush();
				    	bout.close();
						FileSystemSerializer.getInstance().registraEvento(bout.toByteArray(), eventoShutdown.getOraRegistrazione());
					}catch(Exception eSerializer){
						logEventi.error("Errore durante la registrazione su file system dell'evento: "+eSerializer.getMessage(),eSerializer);
					}
				}
			}
	
			if(properties.isEventiTimerEnabled()){
				try{	
					if(debugEventi)
						logEventi.debug("Recupero thread per gli Eventi ...");
					if(OpenSPCoop2Startup.this.threadEventi!=null){
						OpenSPCoop2Startup.this.threadEventi.setStop(true);
						if(debugEventi)
							logEventi.debug("Richiesto stop al thread per gli Eventi");
					}else{
						throw new Exception("Thread per gli Eventi non trovato");
					}
				}catch(Exception e){
					if(logEventi!=null){
						if(debugEventi)
							logEventi.error("Errore durante la gestione dell'exit (ThreadEventi): "+e.getMessage(),e);
					}
				}
			}
		}catch(Throwable e){}
		
		// Recovery FileSystem
		try {
			if(properties.isFileSystemRecoveryTimerEnabled()){
				boolean debugRecoveryFileSystem = properties.isFileSystemRecoveryDebug();
				Logger logRecoveryFileSystem = OpenSPCoop2Logger.getLoggerOpenSPCoopFileSystemRecovery(debugRecoveryFileSystem);
				if(debugRecoveryFileSystem)
					logRecoveryFileSystem.debug("Recupero thread per il recovery da file system ...");
				if(OpenSPCoop2Startup.this.threadFileSystemRecovery!=null){
					OpenSPCoop2Startup.this.threadFileSystemRecovery.setStop(true);
					if(debugRecoveryFileSystem)
						logRecoveryFileSystem.debug("Richiesto stop al thread per il recovery da file system");
				}else{
					throw new Exception("Thread per il recovery da file system non trovato");
				}	
			}
		}catch(Throwable e){}
		
		// GestoreTransazioniStateful
		try {
			if(properties.isTransazioniStatefulEnabled()){
				boolean debugTransazioniStateful = properties.isTransazioniStatefulDebug();
				Logger logTransazioniStateful = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniStateful(debugTransazioniStateful);
				if(debugTransazioniStateful)
					logTransazioniStateful.debug("Recupero thread per la gestione delle transazioni stateful ...");
				if(OpenSPCoop2Startup.this.threadFileSystemRecovery!=null){
					OpenSPCoop2Startup.this.threadFileSystemRecovery.setStop(true);
					if(debugTransazioniStateful)
						logTransazioniStateful.debug("Richiesto stop al thread per la gestione delle transazioni stateful");
				}else{
					throw new Exception("Thread per la gestione delle transazioni stateful non trovato");
				}	
			}
		}catch(Throwable e){}

		// Statistiche
		try{
			if(properties.isStatisticheGenerazioneEnabled()){
				boolean debugStatistiche = properties.isStatisticheGenerazioneDebug();
				Logger logStatistiche = OpenSPCoop2Logger.getLoggerOpenSPCoopStatistiche(debugStatistiche);
				
				if(debugStatistiche)
					logStatistiche.debug("Recupero thread per la generazione delle statistiche orarie ...");
				if(OpenSPCoop2Startup.this.threadGenerazioneStatisticheOrarie!=null){
					OpenSPCoop2Startup.this.threadGenerazioneStatisticheOrarie.setStop(true);
					if(debugStatistiche)
						logStatistiche.debug("Richiesto stop al thread per la generazione delle statistiche orarie");
				}else{
					if(properties.isStatisticheGenerazioneBaseOrariaEnabled()) {
						throw new Exception("Thread per la generazione delle statistiche orarie non trovato");
					}
				}	
				
				if(debugStatistiche)
					logStatistiche.debug("Recupero thread per la generazione delle statistiche giornaliere ...");
				if(OpenSPCoop2Startup.this.threadGenerazioneStatisticheGiornaliere!=null){
					OpenSPCoop2Startup.this.threadGenerazioneStatisticheGiornaliere.setStop(true);
					if(debugStatistiche)
						logStatistiche.debug("Richiesto stop al thread per la generazione delle statistiche giornaliere");
				}else{
					if(properties.isStatisticheGenerazioneBaseGiornalieraEnabled()) {
						throw new Exception("Thread per la generazione delle statistiche giornaliere non trovato");
					}
				}
				
				if(debugStatistiche)
					logStatistiche.debug("Recupero thread per la generazione delle statistiche settimanali ...");
				if(OpenSPCoop2Startup.this.threadGenerazioneStatisticheSettimanali!=null){
					OpenSPCoop2Startup.this.threadGenerazioneStatisticheSettimanali.setStop(true);
					if(debugStatistiche)
						logStatistiche.debug("Richiesto stop al thread per la generazione delle statistiche settimanali");
				}else{
					if(properties.isStatisticheGenerazioneBaseSettimanaleEnabled()) {
						throw new Exception("Thread per la generazione delle statistiche settimanali non trovato");
					}
				}
				
				if(debugStatistiche)
					logStatistiche.debug("Recupero thread per la generazione delle statistiche mensili ...");
				if(OpenSPCoop2Startup.this.threadGenerazioneStatisticheMensili!=null){
					OpenSPCoop2Startup.this.threadGenerazioneStatisticheMensili.setStop(true);
					if(debugStatistiche)
						logStatistiche.debug("Richiesto stop al thread per la generazione delle statistiche mensili");
				}else{
					if(properties.isStatisticheGenerazioneBaseMensileEnabled()) {
						throw new Exception("Thread per la generazione delle statistiche mensili non trovato");
					}
				}	
				
			}
		}catch (Throwable e) {}
		
		// ExitHandler
		try{
			ExitContext context = new ExitContext();
			context.setPddContext(this.pddContext);
			context.setLogConsole(OpenSPCoop2Startup.log);
			context.setLogCore(OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			GestoreHandlers.exit(context);
		}catch(Throwable e){}
		
		// Gestione Stato ControlloTraffico
		if(properties.isControlloTrafficoEnabled()){
			OutputStream out = null;
			Logger logControlloTraffico = null;
			try{
				File fRepository = properties.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository();
				if(fRepository!=null){
					
					logControlloTraffico = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(properties.isControlloTrafficoDebug());
					
					if(fRepository.exists()==false){
						throw new Exception("Directory ["+fRepository.getAbsolutePath()+"] not exists");
					}
					if(fRepository.isDirectory()==false){
						throw new Exception("File ["+fRepository.getAbsolutePath()+"] is not directory");
					}
					if(fRepository.canRead()==false){
						throw new Exception("File ["+fRepository.getAbsolutePath()+"] cannot read");
					}
					if(fRepository.canWrite()==false){
						throw new Exception("File ["+fRepository.getAbsolutePath()+"] cannot write");
					}		
					
					File fDati = new File(fRepository, org.openspcoop2.core.controllo_traffico.constants.Costanti.controlloTrafficoImage);
					out = new FileOutputStream(fDati, false); // se già esiste lo sovrascrive
					GestorePolicyAttive.getInstance().serialize(out);
					out.flush();
					out.close();
					out = null;
					
					boolean inizializzazioneAttiva = false;
					// Il meccanismo di ripristino dell'immagine degli eventi non sembra funzionare
					// Lascio comunque il codice se in futuro si desidera approfindire la questione
					if(inizializzazioneAttiva) {
						fDati = new File(fRepository, org.openspcoop2.core.controllo_traffico.constants.Costanti.controlloTrafficoEventiImage);
						NotificatoreEventi.getInstance().serialize(fDati);
					}
					
				}
			}catch(Throwable e){
				if(logControlloTraffico!=null){
					logControlloTraffico.error("Errore durante la terminazione del Controllo del Traffico: "+e.getMessage(),e);
				}
			}finally{
				try{
					if(out!=null){
						out.flush();
					}
				}catch(Exception eClose){}
				try{
					if(out!=null){
						out.close();
					}
				}catch(Exception eClose){}
			}
		}
		
		// Fermo timer
		if(this.serverJ2EE){
			try {
				if(this.timerRiscontri!=null)
					this.timerRiscontri.stop();
			} catch (Throwable e) {}
			try {
				if(this.timerEliminazioneMsg!=null)
					this.timerEliminazioneMsg.stop();
			} catch (Throwable e) {}
			try {
				if(this.timerPuliziaMsgAnomali!=null)
					this.timerPuliziaMsgAnomali.stop();
			} catch (Throwable e) {}
			try {
				if(this.timerRepositoryBuste!=null)
					this.timerRepositoryBuste.stop();
			} catch (Throwable e) {}
		}else{
			try{
				if(this.threadEliminazioneMsg!=null)
					this.threadEliminazioneMsg.setStop(true);
			}catch (Throwable e) {}
			try{
				if(this.threadPuliziaMsgAnomali!=null)
					this.threadPuliziaMsgAnomali.setStop(true);
			}catch (Throwable e) {}
			try{
				if(this.threadRepositoryBuste!=null)
					this.threadRepositoryBuste.setStop(true);
			}catch (Throwable e) {}
			try{
				if(this.threadConsegnaContenutiApplicativi!=null)
					this.threadConsegnaContenutiApplicativi.setStop(true);
			}catch (Throwable e) {}
		}

		// fermo timer Monitoraggio Risorse
		try{
			if(this.timerMonitoraggioRisorse!=null)
				this.timerMonitoraggioRisorse.setStop(true);
		}catch (Throwable e) {}

		// fermo timer Threshold
		try{
			if(this.timerThreshold!=null)
				this.timerThreshold.setStop(true);
		}catch (Throwable e) {}

		// Rilascio risorse JMX
		if(this.gestoreRisorseJMX!=null){
			this.gestoreRisorseJMX.unregisterMBeans();
		}

		// DataManger
		DateManager.close();

		// Jminix StandaloneMiniConsole
		try{
			if(jminixStandaloneConsole!=null){
				jminixStandaloneConsole.shutdown();
			}
		}catch (Throwable e) {}
		
		// Attendo qualche secondo
		Utilities.sleep(2000);
	}



}
