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



package org.openspcoop2.pdd.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import javax.xml.namespace.QName;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPHeaderElement;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rs.security.jose.common.KeyManagementUtils;
import org.apache.wss4j.dom.handler.WSHandler;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.AccessoDatiAttributeAuthority;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.AccessoDatiKeystore;
import org.openspcoop2.core.config.AccessoDatiRichieste;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.constants.CacheAlgorithm;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.constants.CodiceEventoStatoGateway;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.core.eventi.constants.TipoSeverita;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.message.AbstractBaseOpenSPCoop2Message;
import org.openspcoop2.message.AbstractBaseOpenSPCoop2MessageDynamicContent;
import org.openspcoop2.message.AttachmentsProcessingMode;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageFactory_impl;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.rest.AbstractLazyContent;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.monitor.engine.dynamic.CorePluginLoader;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazioneCoda;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.DBConsegneMessageBoxManager;
import org.openspcoop2.pdd.config.DBConsegnePreseInCaricoManager;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.DBStatisticheManager;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.DynamicClusterManager;
import org.openspcoop2.pdd.config.GeneralInstanceProperties;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.PDNDConfig;
import org.openspcoop2.pdd.config.PDNDConfigUtilities;
import org.openspcoop2.pdd.config.PddProperties;
import org.openspcoop2.pdd.config.PreLoadingConfig;
import org.openspcoop2.pdd.config.QueueManager;
import org.openspcoop2.pdd.config.SystemPropertiesManager;
import org.openspcoop2.pdd.config.dynamic.PddPluginLoader;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.FileSystemSerializer;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.GestoreRichieste;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.StatoServiziPdD;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;
import org.openspcoop2.pdd.core.autorizzazione.GestoreAutorizzazione;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.GestoreLoadBalancerCaching;
import org.openspcoop2.pdd.core.byok.BYOKMapProperties;
import org.openspcoop2.pdd.core.byok.DriverBYOK;
import org.openspcoop2.pdd.core.byok.DriverBYOKUtilities;
import org.openspcoop2.pdd.core.cache.GestoreCacheCleaner;
import org.openspcoop2.pdd.core.connettori.ConnettoreUtils;
import org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCOREConnectionManager;
import org.openspcoop2.pdd.core.controllo_traffico.ConfigurazioneGatewayControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.GestoreControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.INotify;
import org.openspcoop2.pdd.core.controllo_traffico.NotificatoreEventi;
import org.openspcoop2.pdd.core.controllo_traffico.policy.DatiStatisticiDAOManager;
import org.openspcoop2.pdd.core.controllo_traffico.policy.GestoreCacheControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.GestorePolicyAttive;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.GestorePolicyAttiveInMemory;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.HazelcastManager;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson.RedissonManager;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.eventi.GestoreEventi;
import org.openspcoop2.pdd.core.handlers.ExitContext;
import org.openspcoop2.pdd.core.handlers.GeneratoreCasualeDate;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InitContext;
import org.openspcoop2.pdd.core.integrazione.peer.RegexpPeerHeaderDescriptor;
import org.openspcoop2.pdd.core.jmx.AccessoRegistroServizi;
import org.openspcoop2.pdd.core.jmx.ConfigurazioneSistema;
import org.openspcoop2.pdd.core.jmx.GestoreRisorseJMXGovWay;
import org.openspcoop2.pdd.core.jmx.InformazioniStatoPorta;
import org.openspcoop2.pdd.core.jmx.InformazioniStatoPortaCache;
import org.openspcoop2.pdd.core.jmx.StatoServiziJMXResource;
import org.openspcoop2.pdd.core.keystore.GestoreKeystoreCaching;
import org.openspcoop2.pdd.core.keystore.RemoteStoreProviderDriver;
import org.openspcoop2.pdd.core.response_caching.GestoreCacheResponseCaching;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.DiagnosticInputStream;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.filetrace.FileTraceConfig;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.services.connector.ConnectorApplicativeThreadPool;
import org.openspcoop2.pdd.services.core.RicezioneBuste;
import org.openspcoop2.pdd.services.core.RicezioneContenutiApplicativi;
import org.openspcoop2.pdd.services.skeleton.IntegrationManager;
import org.openspcoop2.pdd.timers.TimerClusterDinamicoThread;
import org.openspcoop2.pdd.timers.TimerClusteredRateLimitingLocalCache;
import org.openspcoop2.pdd.timers.TimerConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.timers.TimerConsegnaContenutiApplicativiThread;
import org.openspcoop2.pdd.timers.TimerEventiThread;
import org.openspcoop2.pdd.timers.TimerException;
import org.openspcoop2.pdd.timers.TimerFileSystemRecoveryThread;
import org.openspcoop2.pdd.timers.TimerGestoreBusteNonRiscontrate;
import org.openspcoop2.pdd.timers.TimerGestoreBusteNonRiscontrateLib;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggiLib;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggiThread;
import org.openspcoop2.pdd.timers.TimerGestorePuliziaMessaggiAnomali;
import org.openspcoop2.pdd.timers.TimerGestorePuliziaMessaggiAnomaliLib;
import org.openspcoop2.pdd.timers.TimerGestorePuliziaMessaggiAnomaliThread;
import org.openspcoop2.pdd.timers.TimerGestoreRepositoryBuste;
import org.openspcoop2.pdd.timers.TimerGestoreRepositoryBusteLib;
import org.openspcoop2.pdd.timers.TimerGestoreRepositoryBusteThread;
import org.openspcoop2.pdd.timers.TimerLock;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerRepositoryStatefulThread;
import org.openspcoop2.pdd.timers.TimerState;
import org.openspcoop2.pdd.timers.TimerStatisticheLib;
import org.openspcoop2.pdd.timers.TimerStatisticheThread;
import org.openspcoop2.pdd.timers.TimerThresholdThread;
import org.openspcoop2.pdd.timers.TimerUtils;
import org.openspcoop2.pdd.timers.TipoLock;
import org.openspcoop2.pdd.timers.pdnd.TimerGestoreCacheChiaviPDND;
import org.openspcoop2.pdd.timers.pdnd.TimerGestoreCacheChiaviPDNDLib;
import org.openspcoop2.pdd.timers.pdnd.TimerGestoreChiaviPDND;
import org.openspcoop2.pdd.timers.pdnd.TimerGestoreChiaviPDNDLib;
import org.openspcoop2.pdd.timers.proxy.TimerGestoreOperazioniRemote;
import org.openspcoop2.pdd.timers.proxy.TimerGestoreOperazioniRemoteLib;
import org.openspcoop2.pdd.timers.proxy.TimerSvecchiamentoOperazioniRemote;
import org.openspcoop2.pdd.timers.proxy.TimerSvecchiamentoOperazioniRemoteLib;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.driver.repository.IGestoreRepository;
import org.openspcoop2.protocol.manifest.constants.ServiceBinding;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.registry.RegistroServiziReader;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.state.RequestConfig;
import org.openspcoop2.protocol.sdk.state.RequestThreadContext;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.keystore.cache.RemoteStoreClientInfoCache;
import org.openspcoop2.security.message.WsuIdAllocator;
import org.openspcoop2.security.message.engine.MessageSecurityFactory;
import org.openspcoop2.security.utils.ExternalPWCallback;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.certificate.CertificateFactory;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.ocsp.OCSPManager;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.digest.MessageDigestFactory;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.id.UniversallyUniqueIdentifierProducer;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.io.notifier.unblocked.PipedUnblockedStreamFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.json.YamlSnakeLimits;
import org.openspcoop2.utils.properties.MapProperties;
import org.openspcoop2.utils.random.RandomUtilities;
import org.openspcoop2.utils.resources.FileSystemMkdirConfig;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.security.ProviderUtils;
import org.openspcoop2.utils.semaphore.Semaphore;
import org.openspcoop2.utils.semaphore.SemaphoreConfiguration;
import org.openspcoop2.utils.semaphore.SemaphoreMapping;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XSDSchemaCollection;
import org.slf4j.Logger;

import com.sun.xml.messaging.saaj.soap.MessageImpl;



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
	private static final String LOG_CATEGORY_STARTUP = "govway.startup";
	private static Logger log = LoggerWrapperFactory.getLogger(LOG_CATEGORY_STARTUP);
	public static void logStartupInfo(String msg) {
		OpenSPCoop2Startup.log.info(msg);
	}
	public static void logStartupError(String msg, Exception e) {
		OpenSPCoop2Startup.log.error(msg,e);
	}

	/** Variabile che indica il Nome del modulo attuale di OpenSPCoop */
	private static final String ID_MODULO = "InizializzazioneRisorse";

	/** Indicazione se sta avvendendo un contextDestroyed */
	public static boolean contextDestroyed = false;
	public static synchronized void setContextDestroyed(boolean value) {
		contextDestroyed = value;
	}

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
	private Map<String, TimerConsegnaContenutiApplicativiThread> threadConsegnaContenutiApplicativiMap;
	public static Map<String, TimerConsegnaContenutiApplicativiThread> threadConsegnaContenutiApplicativiRefMap;
	
	/** Gestore risorse JMX */
	private GestoreRisorseJMXGovWay gestoreRisorseJMX = null;
	public static GestoreRisorseJMXGovWay gestoreRisorseJMX_staticInstance = null;
	
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
	private TimerStatisticheThread threadPdndTracciamentoGenerazione;
	private TimerStatisticheThread threadPdndTracciamentoPubblicazione;
	
	/** Timer per la gestione delle chiavi da PDND */
	private TimerGestoreChiaviPDND threadGestoreChiaviPDND;
	private TimerGestoreCacheChiaviPDND threadGestoreCacheChiaviPDND;
	private boolean threadGestoreChiaviPDNDEnabled = false;
	
	/** Timer per la gestione delle operazioni remote in un cluster dinamico */
	private TimerGestoreOperazioniRemote threadGestoreOperazioniRemote;
	private TimerSvecchiamentoOperazioniRemote threadSvecchiamentoOperazioniRemote;
	private boolean threadGestoreOperazioniRemoteEnabled = false;
	
	/** DynamicCluster */
	private static TimerClusterDinamicoThread threadClusterDinamico;
	
	/** Clustered Rate Limiting Timer */
	private static TimerClusteredRateLimitingLocalCache timerClusteredRateLimitingLocalCache;
		
	/** UUIDProducer */
	private UniversallyUniqueIdentifierProducer universallyUniqueIdentifierProducer;
	
	/** indicazione se è un server j2ee */
	private boolean serverJ2EE = false;
	protected long startDate ;

	/** PdDContext */
	private PdDContext pddContext = new PdDContext();
	
	/** OpenSPCoopStartupThread */
	private OpenSPCoopStartupThread th;

	/**
	 * Startup dell'applicazione WEB di OpenSPCoop
	 *
	 * @param sce Servlet Context Event
	 * 
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {

		OpenSPCoop2Startup.setContextDestroyed(false);
		
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
		private void logError(String msg,Throwable e){
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
			if(!OpenSPCoop2Logger.initializeLogConsole(OpenSPCoop2Startup.log)){
				return;
			}
			OpenSPCoop2Startup.log = LoggerWrapperFactory.getLogger(LOG_CATEGORY_STARTUP);
			
			
			
			
			


			
					
			
			
			
			

			/* ------------- Inizializzo ClassNameProperties di OpenSPCoop --------------- */
			if( !ClassNameProperties.initialize(true)){
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
				e.printStackTrace(System.err);
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
					}catch(Exception e){
						// ignore
					}
				}else{
					Loader.initialize();
				}
			}catch(Exception e){
				this.logError("Loader non istanziato: "+e.getMessage(),e);
				return;
			}

			
			







			/* ------------- Proprieta' di OpenSPCoop --------------- */
			if( !OpenSPCoop2Properties.initialize(openspcoopP)){
				this.logError("Riscontrato errore durante l'inizializzazione del reader di 'govway.properties'");
				return;
			}
			OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
			// Di seguito vengono attivati gli engine che richiedono di essere caricati prima della validazione del file di proprietà
			
			
			
			
			
			
			
			/* ----------- Map (environment) ------------ */
			try {
				String mapConfig = propertiesReader.getEnvMapConfig();
				if(StringUtils.isNotEmpty(mapConfig)) {
					MapProperties.initialize(OpenSPCoop2Startup.log, mapConfig, propertiesReader.isEnvMapConfigRequired());
					MapProperties mapProperties = MapProperties.getInstance();
					mapProperties.initEnvironment();
					String msgInit = "Environment inizializzato con le variabili definite nel file '"+mapConfig+"'"+
							"\n\tJavaProperties: "+mapProperties.getJavaMap().keys()+
							"\n\tEnvProperties: "+mapProperties.getEnvMap().keys()+
							"\n\tObfuscateMode: "+mapProperties.getObfuscateModeDescription()+
							"\n\tObfuscatedJavaKeys: "+mapProperties.getObfuscatedJavaKeys()+
							"\n\tObfuscatedEnvKeys: "+mapProperties.getObfuscatedEnvKeys();
					OpenSPCoop2Startup.log.info(msgInit);
				}
			} catch (Exception e) {
				OpenSPCoop2Startup.log.error("Inizializzazione ambiente non riuscita: "+e.getMessage(),e);
				return;
			}
			
			
			
			
			
			
			
			/* ----------- BouncyCastle ------------ */
			if(propertiesReader.isLoadBouncyCastle()){ 
				ProviderUtils.addBouncyCastleAfterSun(true);
				OpenSPCoop2Startup.logStartupInfo("Aggiunto Security Provider org.bouncycastle.jce.provider.BouncyCastleProvider");
				
				if(propertiesReader.getBouncyCastleSecureRandomAlgorithm()!=null) {
			        try{
				        SecureRandom secureRandom = SecureRandom.getInstance(propertiesReader.getBouncyCastleSecureRandomAlgorithm());
				        CryptoServicesRegistrar.setSecureRandom(secureRandom);
				        OpenSPCoop2Startup.logStartupInfo("Aggiunto default SecureRandom '"+secureRandom.getAlgorithm()+"' in CryptoServicesRegistrar di Bouncycastle");
			        }catch(Exception e){
						this.logError("Inizializzazione SecureRandom in BouncyCastle fallita",e);
						return;
					}
				}
				else {
					SecureRandom secureRandom = CryptoServicesRegistrar.getSecureRandom();
					if(secureRandom!=null) {
						OpenSPCoop2Startup.logStartupInfo("SecureRandom used in CryptoServicesRegistrar di Bouncycastle: '"+secureRandom.getAlgorithm()+"'");
					}
				}				
			}
			
			
			
			
			
			
			/* ----------- Gestori HSM ------------ */
			try {
				String hsmConfig = propertiesReader.getHSMConfig();
				if(StringUtils.isNotEmpty(hsmConfig)) {
					File f = new File(hsmConfig);
					HSMManager.init(f, propertiesReader.isHSMConfigRequired(), OpenSPCoop2Startup.log, true);
					HSMManager hsmManager = HSMManager.getInstance();
					hsmManager.providerInit(OpenSPCoop2Startup.log, propertiesReader.isHSMConfigUniqueProviderInstance());
					String msgInit = "Gestore HSM inizializzato; keystore registrati: "+hsmManager.getKeystoreTypes();
					OpenSPCoop2Startup.log.info(msgInit);
				}
			} catch (Exception e) {
				OpenSPCoop2Startup.log.error("Inizializzazione Gestore HSM non riuscita: "+e.getMessage(),e);
				return;
			}
			
			
			
			
			
			
			/* ----------- Gestori OCSP ------------ */
			try {
				String ocspConfig = propertiesReader.getOCSPConfig();
				if(StringUtils.isNotEmpty(ocspConfig)) {
					File f = new File(ocspConfig);
					OCSPManager.init(f, propertiesReader.isOCSPConfigRequired(), propertiesReader.isOCSPConfigLoadDefault(), OpenSPCoop2Startup.log);
					OCSPManager ocspManager = OCSPManager.getInstance();
					String msgInit = "Gestore OCSP inizializzato; policy registrate: "+ocspManager.getOCSPConfigTypes();
					OpenSPCoop2Startup.log.info(msgInit);
				}
			} catch (Exception e) {
				OpenSPCoop2Startup.log.error("Inizializzazione Gestore OCSP non riuscita: "+e.getMessage(),e);
				return;
			}
			
			
			
			
			
			
			/* ----------- Gestori BYOK ------------ */
			BYOKManager byokManager = null;
			try {
				String byokConfig = propertiesReader.getBYOKConfig();
				if(StringUtils.isNotEmpty(byokConfig)) {
					File f = new File(byokConfig);
					BYOKManager.init(f, propertiesReader.isBYOKConfigRequired(), OpenSPCoop2Startup.log);
					byokManager = BYOKManager.getInstance();
					String msgInit = "Gestore BYOK inizializzato;"+
							"\n\tHSM registrati: "+byokManager.getKeystoreTypes()+
							"\n\tSecurityEngine registrati: "+byokManager.getSecurityEngineTypes()+
							"\n\tGovWaySecurityEngine: "+byokManager.getSecurityEngineGovWayDescription();
					OpenSPCoop2Startup.log.info(msgInit);
				}
			} catch (Exception e) {
				OpenSPCoop2Startup.log.error("Inizializzazione Gestore BYOK non riuscita: "+e.getMessage(),e);
				return;
			}
			
	
			
			
			
			
			/* ----------- Secrets (environment) ------------ */
			try {
				String secretsConfig = propertiesReader.getBYOKEnvSecretsConfig();
				if(byokManager!=null && StringUtils.isNotEmpty(secretsConfig)) {
					BYOKMapProperties.initialize(OpenSPCoop2Startup.log, secretsConfig, propertiesReader.isBYOKEnvSecretsConfigRequired(), 
							true,
							null, false);
					BYOKMapProperties secretsProperties = BYOKMapProperties.getInstance();
					secretsProperties.setGovWayStarted(false);
					secretsProperties.initEnvironment();
					boolean existsUnwrapPropertiesAfterGovWayStartup = secretsProperties.isExistsUnwrapPropertiesAfterGovWayStartup();
					String msgInit = "Environment inizializzato con i secrets definiti nel file '"+secretsConfig+"'"+
							"\n\tJavaProperties: "+secretsProperties.getJavaMap().keys()+
							"\n\tEnvProperties: "+secretsProperties.getEnvMap().keys()+
							"\n\tObfuscateMode: "+secretsProperties.getObfuscateModeDescription()+
							"\n\tExistsUnwrapPropertiesAfterGovWayStartup: "+existsUnwrapPropertiesAfterGovWayStartup;
					OpenSPCoop2Startup.log.info(msgInit);
				}
			} catch (Exception e) {
				OpenSPCoop2Startup.log.error("Inizializzazione ambiente (secrets) non riuscita: "+e.getMessage(),e);
				return;
			}
			
			
			
			
			
			
			
			/* ------------- Verifica Proprieta' di OpenSPCoop --------------- */
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
				if(!propertiesReader.validaConfigurazione((java.lang.ClassLoader)o[0])){
					return;
				}
				if(!classNameReader.validaConfigurazione((java.lang.ClassLoader)o[0], propertiesReader.getDatabaseType())){
					return;
				}
			}else{
				if(!propertiesReader.validaConfigurazione(null)){
					return;
				}
				if(!classNameReader.validaConfigurazione(null, propertiesReader.getDatabaseType())){
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
			 * Nella classe ./src/main/java/com/sun/xml/messaging/saaj/soap/EnvelopeFactory.java 
			 * viene inizializzato un pool di sax parser che vengono usati per parsare il messaggio.
			 * Questo poichè ogni parser non può essere utilizzato simultaneamente da thread diversi, altrimenti si ottiene l'errore "org.xml.sax.SAXException: FWK005 parse may not be called while parsing"
			 * Per default viene inizializzato con un pool di 5
			 * public class EnvelopeFactory {
			 *    private static final String SAX_PARSER_POOL_SIZE_PROP_NAME = "com.sun.xml.messaging.saaj.soap.saxParserPoolSize";
			 *	  private static final int DEFAULT_SAX_PARSER_POOL_SIZE = 5;
			 **/
			System.setProperty("com.sun.xml.messaging.saaj.soap.saxParserPoolSize", propertiesReader.getSoapMessageSaajSaxParserPoolSize()+"");
			OpenSPCoop2Startup.logStartupInfo("saaj.soap.saxParserPoolSize="+propertiesReader.getSoapMessageSaajSaxParserPoolSize());
			
			/*
			 * ApacheXMLDSig: usato da wssecurity come XMLSignatureFactory, come si può vedere nella classe 'WSSecSignature'
			 **/
			if(propertiesReader.isLoadApacheXMLDSig()) {
				String providerName_ApacheXMLDSig = "ApacheXMLDSig";
				Provider currentProvider = Security.getProvider(providerName_ApacheXMLDSig);
				if (currentProvider != null) {
					Security.removeProvider(providerName_ApacheXMLDSig);
				}
				Security.insertProviderAt(new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI(),2); // lasciare alla posizione 1 il provider 'SUN'
				OpenSPCoop2Startup.logStartupInfo("Aggiunto Security Provider ApacheXMLDSig");
			}
	        			
			/*
			 * 	Necessario su wildfly per evitare errore 'error constructing MAC: java.lang.SecurityException: JCE cannot authenticate the provider BC'
			 *  se vengono utilizzati keystore P12.
			 *  Il codice  
			 *  	<resource-root path="WEB-INF/lib/bcprov-ext-jdk15on-1.69.jar" use-physical-code-source="true"/>
			 *  all'interno del file jboss-deployment-structure.xml non è più sufficiente da quanto è stato necessario
			 *  introdurre il codice sottostante 'org.apache.wss4j.dom.engine.WSSConfig.init' 
			 *  e di conseguenza tutta la configurazione del modulo 'deployment.custom.javaee.api'
			 *  per risolvere il problema java.lang.NoSuchMethodError: org.apache.xml.security.utils.I18n.init 
			 */
			// NOTA: il caricamento di BouncyCastleProvider DEVE essere effettuato prima dell'inizializzazione 'org.apache.wss4j.dom.engine.WSSConfig.init' 
			/**Spostato sopra prima del BYOK/HSM
			 * if(propertiesReader.isLoadBouncyCastle()){ 
				ProviderUtils.addBouncyCastleAfterSun(true);
				OpenSPCoop2Startup.logStartupInfo("Aggiunto Security Provider org.bouncycastle.jce.provider.BouncyCastleProvider");
				
				if(propertiesReader.getBouncyCastleSecureRandomAlgorithm()!=null) {
			        try{
				        SecureRandom secureRandom = SecureRandom.getInstance(propertiesReader.getBouncyCastleSecureRandomAlgorithm());
				        CryptoServicesRegistrar.setSecureRandom(secureRandom);
				        OpenSPCoop2Startup.logStartupInfo("Aggiunto default SecureRandom '"+secureRandom.getAlgorithm()+"' in CryptoServicesRegistrar di Bouncycastle");
			        }catch(Exception e){
						this.logError("Inizializzazione SecureRandom in BouncyCastle fallita",e);
						return;
					}
				}
				else {
					SecureRandom secureRandom = CryptoServicesRegistrar.getSecureRandom();
					if(secureRandom!=null) {
						OpenSPCoop2Startup.logStartupInfo("SecureRandom used in CryptoServicesRegistrar di Bouncycastle: '"+secureRandom.getAlgorithm()+"'");
					}
				}				
			}*/
			if(propertiesReader.isUseBouncyCastleProviderForCertificate()) {
				OpenSPCoop2Startup.logStartupInfo("Add Bouncycastle in CertificateFactory");
				CertificateFactory.setUseBouncyCastleProvider(true);
			}
			if(propertiesReader.isUseBouncyCastleProviderForMessageDigest()) {
				OpenSPCoop2Startup.logStartupInfo("Add Bouncycastle in MessageDigestFactory");
				MessageDigestFactory.setUseBouncyCastleProvider(true);
			}
			if(propertiesReader.isUseBouncyCastleProviderForWss4jCryptoMerlin()) {
				OpenSPCoop2Startup.logStartupInfo("Add Bouncycastle in keystore.Merlin provider");
				org.openspcoop2.security.keystore.MerlinProvider.setUseBouncyCastleProvider(true);
			}
			
			DBUtils.setKeystoreJksPasswordRequired(propertiesReader.isConfigurazioneKeystoreJksPasswordRequired());
			DBUtils.setKeystoreJksKeyPasswordRequired(propertiesReader.isConfigurazioneKeystoreJksKeyPasswordRequired());
			DBUtils.setKeystorePkcs12PasswordRequired(propertiesReader.isConfigurazioneKeystorePkcs12PasswordRequired());
			DBUtils.setKeystorePkcs12KeyPasswordRequired(propertiesReader.isConfigurazioneKeystorePkcs12KeyPasswordRequired());
			DBUtils.setTruststoreJksPasswordRequired(propertiesReader.isConfigurazioneTruststoreJksPasswordRequired());
			DBUtils.setTruststorePkcs12PasswordRequired(propertiesReader.isConfigurazioneTruststorePkcs12PasswordRequired());
			// disabilito anche nelle librerie
			if(!propertiesReader.isConfigurazioneKeystoreJksPasswordRequired() || !propertiesReader.isConfigurazioneTruststoreJksPasswordRequired()) {
				KeyManagementUtils.setKeystoreJksPasswordRequired(false);
			}
			if(!propertiesReader.isConfigurazioneKeystoreJksKeyPasswordRequired()) {
				KeyManagementUtils.setKeystoreJksKeyPasswordRequired(false);
			}
			if(!propertiesReader.isConfigurazioneKeystorePkcs12PasswordRequired() || !propertiesReader.isConfigurazioneTruststorePkcs12PasswordRequired()) {
				KeyManagementUtils.setKeystorePkcs12PasswordRequired(false);
			}
			if(!propertiesReader.isConfigurazioneKeystorePkcs12KeyPasswordRequired()) {
				KeyManagementUtils.setKeystorePkcs12KeyPasswordRequired(false);
			}
			OpenSPCoop2Startup.logStartupInfo("KeyManagementUtils.keystoreJksPasswordRequired="+KeyManagementUtils.isKeystoreJksPasswordRequired());
			OpenSPCoop2Startup.logStartupInfo("KeyManagementUtils.keystoreJksKeyPasswordRequired="+KeyManagementUtils.isKeystoreJksKeyPasswordRequired());
			OpenSPCoop2Startup.logStartupInfo("KeyManagementUtils.keystorePkcs12PasswordRequired="+KeyManagementUtils.isKeystorePkcs12PasswordRequired());
			OpenSPCoop2Startup.logStartupInfo("KeyManagementUtils.keystorePkcs12KeyPasswordRequired="+KeyManagementUtils.isKeystorePkcs12KeyPasswordRequired());
			if(!propertiesReader.isConfigurazioneKeystoreJksPasswordRequired() || !propertiesReader.isConfigurazioneTruststoreJksPasswordRequired() ||
					!propertiesReader.isConfigurazioneKeystorePkcs12PasswordRequired() || !propertiesReader.isConfigurazioneTruststorePkcs12PasswordRequired()) {
				WSHandler.setKeystorePasswordRequired(false);
			}
			OpenSPCoop2Startup.logStartupInfo("WSHandler.keystorePasswordRequired="+WSHandler.isKeystorePasswordRequired());
			
			StringBuilder sb = new StringBuilder();
			Provider[] providerList = Security.getProviders();
	        sb.append("Security Providers disponibili sono "+providerList.length+":\n");
	        for (int i = 0; i < providerList.length; i++) {
	        	sb.append("[" + (i + 1) + "] - Name:"+ providerList[i].getName()+"\n");
	        }
	        OpenSPCoop2Startup.logStartupInfo(sb.toString());
	        
	        if(propertiesReader.getSecurityEgd()!=null) {
	        	System.setProperty("java.security.egd", propertiesReader.getSecurityEgd());
	        	OpenSPCoop2Startup.logStartupInfo("Aggiunta proprietà java.security.egd="+propertiesReader.getSecurityEgd());
	        }
	        			
			/* ------------ 
			 * Inizializzazione Resource Bundle:
			 * - org/apache/xml/security/resource/xmlsecurity_en.properties (xmlsec-2.3.0.jar)
			 * - org/apache/xml/security/resource/xmlsecurity_de.properties (xmlsec-2.3.0.jar)
			 * - messages/wss4j_errors.properties (wss4j-ws-security-common-2.4.1.jar)
			 * 
			 * L'inizializzazione di questa classe DEVE essere all'inizio altrimenti si puo' incorrere in errori tipo il seguente:
			 * Caused by: org.apache.wss4j.common.ext.WSSecurityException: No message with ID "noUserCertsFound" found in resource bundle "org/apache/xml/security/resource/xmlsecurity"
			 *
			 * Il motivo risiede nel fatto che org.apache.wss4j.common.ext.WSSecurityException lancia una eccezione con id "noUserCertsFound".
			 * Tale eccezione di fatto estende la classe org/apache/xml/security/exceptions/XMLSecurityException che utilizza il proprio resource bundle
			 * per risolvere l'id. Tale classe utilizza normalmente il properties 'org/apache/xml/security/resource/xmlsecurity_en.properties'
			 * Mentre l'id 'noUserCertsFound' e' dentro il properties 'messages/wss4j_errors.properties'
			 * Pero' xmlsec permette di inizializzare il resource bundle da usare anche grazie ad un metodo dove viene fornito l'intero resource bundle.
			 * Questo avviene in xmlsec-2.3.0/src/main/java/org/apache/xml/security/utils/I18n.java metodo init(ResourceBundle resourceBundle)
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
			if(propertiesReader.getWsuIdSecureRandomAlgorithm()!=null) {
				WsuIdAllocator.setSecureRandomAlgorithm(propertiesReader.getWsuIdSecureRandomAlgorithm());
				OpenSPCoop2Startup.logStartupInfo("SecureRandom used in WsuIdAllocator per WS-Security: '"+propertiesReader.getWsuIdSecureRandomAlgorithm()+"'");
			}
			
			/* ------------ 
			 * Disabilita il log di errore prodotto da freemarker
			 * https://freemarker.apache.org/docs/api/freemarker/log/Logger.html#SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY
			 * deprecato: freemarker.log.Logger.selectLoggerLibrary(freemarker.log.Logger.LIBRARY_NONE);
			 */
			try{
				System.setProperty(freemarker.log.Logger.SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY,freemarker.log.Logger.LIBRARY_NAME_NONE);
			}catch(Exception e){
				this.logError("Inizializzazione "+freemarker.log.Logger.SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY,e);
				return;
			}
			
			/* ------------ 
			 * Disabilita la cache del motore JsonPath, altrimenti pattern che contengono il 'concat' vengono cachati e se applicati su messaggi differenti ritornano lo stesso risultato.
			 */
			try{
				if(!propertiesReader.isJsonPathCacheEnabled()){ 
					JsonPathExpressionEngine.disableCacheJsonPathEngine();
					OpenSPCoop2Startup.logStartupInfo("Disabilitata cache (NOOPCache) in engine com.jayway.jsonpath.spi.cache.CacheProvider (JsonPath)");
				}
			}catch(Exception e){
				this.logError("Cache JsonPathEngine disabilitata",e);
				return;
			}
			
			if(propertiesReader.isHttpDisableKeepAlive()) {
				/* ------------ 
				 * Disabilita KeepAlive
				 */
				try{
					System.setProperty("http.keepAlive","false");
					OpenSPCoop2Startup.logStartupInfo("Impostazione http.keepAlive=false effettuata");
				}catch(Exception e){
					this.logError("Impostazione http.keepAlive=false non riuscita",e);
					return;
				}
			}
			
			// Inizializzo Semaphore
			org.openspcoop2.utils.Semaphore.setDefaultLockAcquisitionTimeoutMs(propertiesReader.getSemaphoreTimeoutMS());
			org.openspcoop2.utils.Semaphore.setDefaultLockHoldTimeoutMs(propertiesReader.getSemaphoreHoldTimeoutMS());
			if(propertiesReader.getSemaphoreHoldTimeoutMS()>0) {
				org.openspcoop2.utils.SemaphoreLock.initScheduledExecutorService();
			}
			org.openspcoop2.utils.Semaphore.setDefaultDebug(propertiesReader.isSemaphoreDebug());
			org.openspcoop2.utils.Semaphore.setSemaphoreType(propertiesReader.getSemaphoreType());
			org.openspcoop2.utils.Semaphore.setFair(propertiesReader.isSemaphoreFair());
			OpenSPCoop2Startup.logStartupInfo("Impostazione semaphore acquisitionTimeoutMs="+org.openspcoop2.utils.Semaphore.getDefaultLockAcquisitionTimeoutMs()+
					" holdTimeoutMs="+org.openspcoop2.utils.Semaphore.getDefaultLockHoldTimeoutMs()+
					" scheduledExecutorServiceEnabled="+org.openspcoop2.utils.SemaphoreLock.isInitializedScheduledExecutorService()+
					" debug="+org.openspcoop2.utils.Semaphore.isDefaultDebug()+
					" type="+org.openspcoop2.utils.Semaphore.getSemaphoreType()+
					" fair="+org.openspcoop2.utils.Semaphore.isFair());
			
			// Inizializzo Controlli connessioni
			Logger logR = OpenSPCoop2Logger.getLoggerOpenSPCoopResources()!=null ? OpenSPCoop2Logger.getLoggerOpenSPCoopResources() : log;
			ServicesUtils.initCheckConnectionDB(logR, propertiesReader.isJdbcCloseConnectionCheckIsClosed(), propertiesReader.isJdbcCloseConnectionCheckAutocommit());
			



			/* ------------- Inizializzo il sistema di Logging di OpenSPCoop --------------- */
			boolean isInitializeLogger = false;
			isInitializeLogger = OpenSPCoop2Logger.initialize(OpenSPCoop2Startup.log,propertiesReader.getRootDirectory(),loggerP,
					propertiesReader.isAllarmiEnabled());
			if(!isInitializeLogger){
				return;
			}
			Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			OpenSPCoop2Startup.log = LoggerWrapperFactory.getLogger(LOG_CATEGORY_STARTUP);
			
			Utilities.setLog(logCore);
			Utilities.setFreeMemoryLog(propertiesReader.getFreeMemoryLog());
			
			OpenSPCoop2Startup.initializeLog = true;
			
			if(propertiesReader.isLoggerSaajDisabilitato()) {
				/**java.util.logging.Logger logSaaj = java.util.logging.Logger.getLogger(com.sun.xml.messaging.saaj.util.LogDomainConstants.SOAP_DOMAIN
																						"com.sun.xml.messaging.saaj.soap.LocalStrings");*/
				java.util.logging.Logger logSaaj = java.util.logging.Logger.getLogger(com.sun.xml.messaging.saaj.util.LogDomainConstants.MODULE_TOPLEVEL_DOMAIN);
				logSaaj.setLevel(Level.OFF);
				logSaaj.severe("Il logger utilizzato nel package '"+com.sun.xml.messaging.saaj.util.LogDomainConstants.MODULE_TOPLEVEL_DOMAIN+"' e' stato disabilitato; questo messaggio non deve essere visualizzato");
				logSaaj.severe("SAAJ0511.soap.cannot.create.envelope"); // serve per caricare il logger con il local string
				OpenSPCoop2Startup.logStartupInfo("Il logger utilizzato nel package '"+com.sun.xml.messaging.saaj.util.LogDomainConstants.MODULE_TOPLEVEL_DOMAIN+"' e' stato disabilitato");
			}
			
			RequestThreadContext.setLog(OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori());

			org.openspcoop2.utils.Semaphore.setLogDebug(OpenSPCoop2Logger.getLoggerOpenSPCoopResources());
			
			


			/* ------------- Pdd.properties --------------- */
			String locationPddProperties = null;
			if( propertiesReader.getLocationPddProperties()!=null ){
				locationPddProperties = propertiesReader.getLocationPddProperties();
			}
			if( !PddProperties.initialize(locationPddProperties,propertiesReader.getRootDirectory())){
				this.logError("Riscontrato errore durante l'inizializzazione del reader di 'govway.pdd.properties'");
				return;
			}
			if(o!=null){
				try{
					PddProperties.updateLocalImplementation((Properties)o[3]);
				}catch(Exception e){
					// ignore
				}
			}
			try{
				OpenSPCoop2Properties.updatePddPropertiesReader(PddProperties.getInstance());
			}catch(Exception e){
				this.logError("Riscontrato errore durante l'assegnamento del pddPropertiesReader a OpenSPCoopPropertiesReader: "+e.getMessage(),e);
				return;
			}










			/* ------------- MsgDiagnostici.properties --------------- */
			if( !MsgDiagnosticiProperties.initialize(null,propertiesReader.getRootDirectory())){
				this.logError("Riscontrato errore durante l'inizializzazione del reader di 'govway.msgDiagnostici.properties'");
				return;
			}
			if(o!=null){
				try{
					MsgDiagnosticiProperties.updateLocalImplementation((Properties)o[6]);
				}catch(Exception e){
					// ignore
				}
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
					FileTraceConfig.init(propertiesReader.getTransazioniFileTraceConfig(), true);
				}
			}catch(Exception e) {
				this.logError("Riscontrato errore durante l'inizializzazione del FileTrace",e);
				return;
			}

			
			
			
			/* ----------- Inizializzazione Risorse DOM/SOAP ------------ */
			List<OpenSPCoop2MessageFactory> messageFactory = new ArrayList<>();
			try{
				// TransazioneContext
				TransactionContext.initGestioneStateful();
				OpenSPCoop2Startup.logStartupInfo("TransactionContext.gestioneStateful: "+propertiesReader.isTransazioniStatefulEnabled());
				TransactionContext.initResources();
				OpenSPCoop2Startup.logStartupInfo("TransactionContext.type (sync:"+propertiesReader.isConfigurazioneCache_transactionContext_accessiSynchronized()+"): "+TransactionContext.getTransactionContextType());
				
				// Cache
				if(!propertiesReader.isConfigurazioneCache_accessiSynchronized()) {
					Cache.disableSyncronizedGetAsDefault();
				}
				OpenSPCoop2Startup.logStartupInfo("Cache.disableSyncronizedGetAsDefault: "+Cache.isDisableSyncronizedGetAsDefault());
				Cache.DEBUG_CACHE=propertiesReader.isConfigurazioneCacheDebug();
				OpenSPCoop2Startup.logStartupInfo("Cache.DEBUG: "+Cache.DEBUG_CACHE);
				GestoreRichieste.setUseCache(propertiesReader.isConfigurazioneCacheRequestManagerUseCache());
				OpenSPCoop2Startup.logStartupInfo("Cache.requestManager.useCache: "+GestoreRichieste.isUseCache());
				
				
				// MessageFactory
				OpenSPCoop2MessageFactory.setMessageFactoryImpl(classNameReader.getOpenSPCoop2MessageFactory(propertiesReader.getOpenspcoop2MessageFactory()));
				OpenSPCoop2MessageFactory.initDefaultMessageFactory(true);
				
				// Buffer Dump
				if(propertiesReader.getDumpBufferImpl()!=null && !"".equals(propertiesReader.getDumpBufferImpl())) {
					DumpByteArrayOutputStream.setClassImpl(propertiesReader.getDumpBufferImpl());
				}
				OpenSPCoop2Startup.logStartupInfo("DumpByteArrayOutputStream buffer implementation: "+DumpByteArrayOutputStream.getClassImpl());
				OpenSPCoop2Startup.logStartupInfo("DumpBinario set buffer threshold: "+propertiesReader.getDumpBinarioInMemoryThreshold());
				AbstractBaseOpenSPCoop2MessageDynamicContent.setSoglia(propertiesReader.getDumpBinarioInMemoryThreshold());
				OpenSPCoop2Startup.logStartupInfo("DumpBinario set buffer repository: "+propertiesReader.getDumpBinarioRepository());
				AbstractBaseOpenSPCoop2MessageDynamicContent.setRepositoryFile(propertiesReader.getDumpBinarioRepository());
				
				// SoapBuffer
				boolean soapReader = propertiesReader.useSoapMessageReader();
				OpenSPCoop2Startup.logStartupInfo("SOAPMessageReader enabled="+soapReader);
				AbstractBaseOpenSPCoop2MessageDynamicContent.setSoapReader(soapReader);
				OpenSPCoop2Startup.logStartupInfo("SOAPMessageReader set buffer threshold (kb): "+propertiesReader.getSoapMessageReaderBufferThresholdKb());
				AbstractBaseOpenSPCoop2MessageDynamicContent.setSoapReaderBufferThresholdKb(propertiesReader.getSoapMessageReaderBufferThresholdKb());
				if(soapReader) {
					OpenSPCoop2Startup.logStartupInfo("SOAPMessageReader headerOptimization enabled="+propertiesReader.useSoapMessageReaderHeaderOptimization());
					OpenSPCoop2MessageSoapStreamReader.SOAP_HEADER_OPTIMIZATION_ENABLED=propertiesReader.useSoapMessageReaderHeaderOptimization();
				}
				
				// SoapPassthrough
				boolean soapPassthroughImpl = propertiesReader.useSoapMessagePassthrough();
				OpenSPCoop2Startup.logStartupInfo("OpenSPCoop2MessageFactory_impl soapPassthroughImpl="+soapPassthroughImpl);
				OpenSPCoop2MessageFactory_impl.setSoapPassthroughImpl(soapPassthroughImpl);
								
				// Locale SOAPFault String
				Locale localeSoapFaultString = propertiesReader.getLocaleSOAPFaultString();
				if(localeSoapFaultString!=null) {
					OpenSPCoop2Startup.logStartupInfo("Locale SOAPFault String: "+localeSoapFaultString);
					SoapUtils.setSoapFaultStringLocale(localeSoapFaultString);
				}
				
				// ContentType per SOAP 1.2 (ulteriori Content-Type oltre a application/soap+xml e application/soap+fastinfoset)
				MessageImpl.alternativeAcceptedContentType1_2 = propertiesReader.getAlternativeContentTypeSoap12();
				if(MessageImpl.alternativeAcceptedContentType1_2!=null && !MessageImpl.alternativeAcceptedContentType1_2.isEmpty()) {
					StringBuilder sbCT = new StringBuilder();
					for (String alternativeContentType : MessageImpl.alternativeAcceptedContentType1_2) {
						if(sbCT.length()>0) {
							sbCT.append(", ");
						}
						sbCT.append(alternativeContentType);
					}
					OpenSPCoop2Startup.logStartupInfo("Registrati ulteriori '"+MessageImpl.alternativeAcceptedContentType1_2.size()+"' content-type associabili ai messaggi SOAP 1.2: "+sbCT.toString());
					
				}
				
				// DiagnosticInputStream
				DiagnosticInputStream.setSetDateEmptyStream(propertiesReader.isConnettoriUseDiagnosticInputStreamSetDateEmptyStream());
				OpenSPCoop2Startup.logStartupInfo("DiagnosticInputStream isSetDateEmptyStream: "+DiagnosticInputStream.isSetDateEmptyStream());
				
				// ForceUseNioInAsyncChannelWithBIOOnlyLibrary
				ConnettoreUtils.setForceUseHttpCore5NioInAsyncChannelWithHttpUrlConnectionLibrarySetting(propertiesReader.isConnettoriForceUseNioInAsyncChannelWithBIOOnlyLibrary());
				OpenSPCoop2Startup.logStartupInfo(""
						+ "ForceUseHttpCore5NioInAsyncChannelWithHttpUrlConnectionLibrarySetting: "+ConnettoreUtils.isForceUseHttpCore5NioInAsyncChannelWithHttpUrlConnectionLibrarySetting());
				
				// PipeUnblockedStream
				if(propertiesReader.getPipedUnblockedStreamClassName()!=null) {
					PipedUnblockedStreamFactory.setImplementation(propertiesReader.getPipedUnblockedStreamClassName());
				}
				
				// RestMultipartLazy
				AbstractLazyContent.BUILD_LAZY=propertiesReader.useRestMultipartLazy();
				OpenSPCoop2Startup.logStartupInfo("OpenSPCoop2Content lazy="+AbstractLazyContent.BUILD_LAZY);
				
				// MessageSecurity
				MessageSecurityFactory.setMessageSecurityContextClassName(classNameReader.getMessageSecurityContext(propertiesReader.getMessageSecurityContext()));
				MessageSecurityFactory.setMessageSecurityDigestReaderClassName(classNameReader.getMessageSecurityDigestReader(propertiesReader.getMessageSecurityDigestReader()));
				
				List<String> tipiMessageFactory = new ArrayList<>();
				List<String> classiMessageFactory = new ArrayList<>();
				String factoryDefault = "@DEFAULT@";
				tipiMessageFactory.add(factoryDefault);
				OpenSPCoop2MessageFactory defaultMessageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
				classiMessageFactory.add(defaultMessageFactory.getClass().getName());
				messageFactory.add(defaultMessageFactory);
				
				String [] tmp_tipiMessageFactory = classNameReader.getOpenSPCoop2MessageFactory();
				if(tmp_tipiMessageFactory!=null && tmp_tipiMessageFactory.length>0) {
					OpenSPCoop2Startup.logStartupInfo("Analizzo "+tmp_tipiMessageFactory.length+" message factories ...");
					for (int i = 0; i < tmp_tipiMessageFactory.length; i++) {
						String tipo = tmp_tipiMessageFactory[i];
						String classe = classNameReader.getOpenSPCoop2MessageFactory(tipo);
						if(!classiMessageFactory.contains(classe)) {
							OpenSPCoop2MessageFactory factory = (OpenSPCoop2MessageFactory) loader.newInstance(classe);
							tipiMessageFactory.add(tipo);
							messageFactory.add(factory);
							classiMessageFactory.add(classe);
							OpenSPCoop2Startup.logStartupInfo("Registrazione '"+tipo+"' corrispondente alla classe '"+classe+"' terminata");
						}
						else {
							OpenSPCoop2Startup.logStartupInfo("Registrazione '"+tipo+"' corrispondente alla classe '"+classe+"' non effettuata. La stessa classe risulta già associata ad altri tipi.");
						}
					}
				}
				
				AbstractXMLUtils.DISABLE_DTDs = !propertiesReader.isXmlFactoryDTDsEnabled();
				OpenSPCoop2Startup.logStartupInfo("XMLUtils - DISABLE_DTDs: "+AbstractXMLUtils.DISABLE_DTDs);
				
				DynamicUtils.setXsltProcessAsDomSource(propertiesReader.isXsltProcessAsDOMSource());
				OpenSPCoop2Startup.logStartupInfo("DynamicUtils - XSLT_PROCESS_AS_DOMSOURCE: "+DynamicUtils.isXsltProcessAsDomSource());
				
				Properties yamlSnakeLimits = propertiesReader.getYamlSnakeLimits();
				if(yamlSnakeLimits!=null && !yamlSnakeLimits.isEmpty()) {
					YamlSnakeLimits.initialize(OpenSPCoop2Startup.log, yamlSnakeLimits);
				}
				
				for (int l = 0; l < tipiMessageFactory.size(); l++) {
					String tipo = tipiMessageFactory.get(l);
					String classe = classiMessageFactory.get(l);
					OpenSPCoop2MessageFactory factory = messageFactory.get(l);
					
					try{
					
						OpenSPCoop2Startup.logStartupInfo("Inizializzazione '"+tipo+"' corrispondente alla classe '"+classe+"' ... ");
					
						// XML
						org.openspcoop2.message.xml.MessageXMLUtils xmlUtils = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(factory);
						// XML - XERCES
						xmlUtils.initDocumentBuilderFactory();
						xmlUtils.initDatatypeFactory();
						xmlUtils.initSAXParserFactory();
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
						
						/*
						 * Le seguenti inizializzazione servono alle varie factory per evitare di scorrere il classloader a cercare i jar che contengono l'implementazione da utilizzare.
						 * Lo scan del classloader comporta l'apertura dei vari file jar e quindi il decadimento delle performance poichè i thread risultano bloccati sulla chiamata di apertura del jar
						 * 
						 * Esempio di stack che riporta il problema:
						 * java.util.zip.ZipFile.open(Native Method)
						 * java.util.zip.ZipFile.(ZipFile.java:219)
						 * ...
						 * org.apache.catalina.webresources.AbstractArchiveResourceSet.openJarFile(AbstractArchiveResourceSet.java:308)
						 * org.apache.catalina.webresources.AbstractSingleArchiveResourceSet.getArchiveEntry(AbstractSingleArchiveResourceSet.java:93)
						 * ....
						 * org.apache.catalina.webresources.Cache.getResource(Cache.java:69)
						 * org.apache.catalina.webresources.StandardRoot.getResource(StandardRoot.java:216)
						 * org.apache.catalina.webresources.StandardRoot.getClassLoaderResource(StandardRoot.java:225)
						 * org.apache.catalina.loader.WebappClassLoaderBase.getResourceAsStream(WebappClassLoaderBase.java:1067)
						 * ...
						 * org.apache.xerces.parsers.SecuritySupport.getResourceAsStream(Unknown Source)
						 * org.apache.xerces.parsers.ObjectFactory.findJarServiceProvider(Unknown Source)
						 * org.apache.xerces.parsers.ObjectFactory.createObject(Unknown Source)
						 * org.apache.xerces.parsers.DOMParser.(Unknown Source)
						 * org.apache.xerces.jaxp.DocumentBuilderImpl.(Unknown Source)
						 * org.apache.xerces.jaxp.DocumentBuilderFactoryImpl.newDocumentBuilder(Unknown Source)
						 * 
						 * Ogni factory prima di avviare la ricerca dei jar del service provider, dispone di una proprietà java che se valorizzata
						 * viene usata per istanziare immediatamente il provider con la classe indicata.
						 **/
						System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration",org.apache.xerces.parsers.XIncludeAwareParserConfiguration.class.getName());
						System.setProperty("javax.xml.transform.TransformerFactory",org.apache.xalan.processor.TransformerFactoryImpl.class.getName());
						System.setProperty("javax.xml.xpath.XPathFactory:"+XPathFactory.DEFAULT_OBJECT_MODEL_URI,org.apache.xpath.jaxp.XPathFactoryImpl.class.getName());
						System.setProperty("org.apache.xml.dtm.DTMManager",org.apache.xml.dtm.ref.DTMManagerDefault.class.getName());
						
						// Log
						// stampo comunque saaj factory
						String factoryClassName = OpenSPCoop2MessageFactory_impl.class.getName()+"";
						OpenSPCoop2Startup.logStartupInfo("OpenSPCoop MessageFactory (open:"+factoryClassName.equals(factory.getClass().getName())+"): "+factory.getClass().getName());
						if(propertiesReader.isPrintInfoFactory()){
							MessageType [] mt = MessageType.values();
							for (int i = 0; i < mt.length; i++) {
								OpenSPCoop2Startup.logStartupInfo("OpenSPCoop Message ["+mt[i].name()+"]: "+factory.createEmptyMessage(mt[i],MessageRole.NONE).getClass().getName());	
							}
							if( factory.getSoapFactory11()!=null)
								OpenSPCoop2Startup.logStartupInfo("SOAP1.1 Factory: "+ factory.getSoapFactory11().getClass().getName());
							else
								OpenSPCoop2Startup.logStartupInfo("SOAP1.1 Factory: not implemented");
							if(factory.getSoapFactory12()!=null){
								OpenSPCoop2Startup.logStartupInfo("SOAP1.2 Factory: "+ factory.getSoapFactory12().getClass().getName());
							}else{
								OpenSPCoop2Startup.logStartupInfo("SOAP1.2 Factory: not implemented");
							}
							OpenSPCoop2Startup.logStartupInfo("SOAP MessageFactory: "+factory.getSoapMessageFactory().getClass().getName());
			
							// XML - XERCES
							OpenSPCoop2Startup.logStartupInfo("XERCES - DocumentFactory: "+xmlUtils.getDocumentBuilderFactory().getClass().getName());
							OpenSPCoop2Startup.logStartupInfo("XERCES - DatatypeFactory: "+xmlUtils.getDatatypeFactory().getClass().getName());
							OpenSPCoop2Startup.logStartupInfo("XERCES - SAXParserFactory: "+xmlUtils.getSAXParserFactory().getClass().getName());
//							OpenSPCoop2Startup.logStartupInfo("XERCES - XMLEventFactory: "+xmlUtils.getXMLEventFactory().getClass().getName());
							OpenSPCoop2Startup.logStartupInfo("XERCES - SchemaFactory: "+xmlUtils.getSchemaFactory().getClass().getName());
							
							// XML - XALAN
							OpenSPCoop2Startup.logStartupInfo("XALAN - TransformerFactory: "+xmlUtils.getTransformerFactory().getClass().getName());
							OpenSPCoop2Startup.logStartupInfo("XALAN - XPathFactory: "+xmlUtils.getXPathFactory().getClass().getName());

						}
						if(propertiesReader.isPrintInfoMessageSecurity()){
							OpenSPCoop2Startup.logStartupInfo("MessageSecurity Context: "+MessageSecurityFactory.messageSecurityContextImplClass);
							OpenSPCoop2Startup.logStartupInfo("MessageSecurity DigestReader: "+MessageSecurityFactory.messageSecurityDigestReaderImplClass);
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
					
						OpenSPCoop2Startup.logStartupInfo("Inizializzazione '"+tipo+"' corrispondente alla classe '"+classe+"' effettuata");
						
					} catch(Exception e) {
						this.logError("Inizializzazione '"+tipo+"' corrispondente alla classe '"+classe+"' fallita: "+e.getMessage(),e);
						return;
					}
				}
								
				// inizializzo cache per gli headers peer
				RegexpPeerHeaderDescriptor.initCache(propertiesReader.getHeadersPeerRegexpCacheSize());
				
			} catch(Exception e) {
				this.logError("Inizializzazione Message/DOM/SOAP: "+e.getMessage(), e);
				return;
			}
			
			
			
			
			





			/*----------- Inizializzazione DateManager  --------------*/
			try{
				String tipoClass = classNameReader.getDateManager(propertiesReader.getTipoDateManager());
				DateManager.initializeDataManager(tipoClass, propertiesReader.getDateManagerProperties(),logCore);
				OpenSPCoop2Startup.logStartupInfo("Inizializzazione DateManager: "+propertiesReader.getTipoDateManager());
				
				DateUtils.setDEFAULT_DATA_ENGINE_TYPE(propertiesReader.getTipoDateTimeFormat());
				OpenSPCoop2Startup.logStartupInfo("Inizializzazione DateTimeFormat: "+propertiesReader.getTipoDateTimeFormat());
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
				OpenSPCoop2Startup.logStartupInfo("Datasource non inizializzato... riprovo");
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
					OpenSPCoop2Startup.logStartupInfo("Attendo inizializzazione del Datasource ... "+erroreDB);
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
				OpenSPCoop2Startup.logStartupInfo("Inizializzazione DBManager effettuata con successo.");
			} else {
				OpenSPCoop2Startup.log.error("Inizializzazione DBManager non effettuata", new Exception(erroreDB));
				//msgDiag.logStartupError(new Exception(erroreDB), "DatabaseManager");
				return;
			}

			if( JDBCUtilities.isTransactionIsolationNone(DBManager.getTransactionIsolationLevel()) )
				OpenSPCoop2Startup.logStartupInfo("Database TransactionLevel is NONE");
			else if(JDBCUtilities.isTransactionIsolationReadCommitted(DBManager.getTransactionIsolationLevel()) )
				OpenSPCoop2Startup.logStartupInfo("Database TransactionLevel is READ_COMMITTED");
			else if(JDBCUtilities.isTransactionIsolationReadUncommitted(DBManager.getTransactionIsolationLevel()) )
				OpenSPCoop2Startup.logStartupInfo("Database TransactionLevel is READ_UNCOMMITTED");
			else if(JDBCUtilities.isTransactionIsolationRepeatableRead(DBManager.getTransactionIsolationLevel()) )
				OpenSPCoop2Startup.logStartupInfo("Database TransactionLevel is REPEATABLE_READ");
			else if(JDBCUtilities.isTransactionIsolationSerializable(DBManager.getTransactionIsolationLevel()) )
				OpenSPCoop2Startup.logStartupInfo("Database TransactionLevel is SERIALIZABLE");
			else if(JDBCUtilities.isTransactionIsolationSqlServerSnapshot(DBManager.getTransactionIsolationLevel()) )
				OpenSPCoop2Startup.logStartupInfo("Database TransactionLevel is SQLSERVER SNAPSHOT");
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
				if(propertiesReader.isStatisticheGenerazioneEnabled() || propertiesReader.isControlloTrafficoEnabled()){
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
			
			if(OpenSPCoop2Startup.this.serverJ2EE==false){
				if(propertiesReader.isTimerConsegnaContenutiApplicativiAbilitato()){
			
					// GestoreConsegnePreseInCarico - Smistatore
					try{
						if(propertiesReader.isTimerConsegnaContenutiApplicativi_smistatore_runtime_useRuntimeManager()) {
							DBConsegnePreseInCaricoManager.initSmistatore(DBManager.getInstance(), logCore, propertiesReader.getDatabaseType());
						}
						else {
							DBConsegnePreseInCaricoManager.initSmistatore(propertiesReader.getTimerConsegnaContenutiApplicativi_smistatore_runtime_dataSource(), propertiesReader.getTimerConsegnaContenutiApplicativi_smistatore_runtime_dataSourceJndiContext(), 
									logCore, propertiesReader.getDatabaseType(), 
									propertiesReader.isTimerConsegnaContenutiApplicativi_smistatore_runtime_dataSource_useDBUtils(), propertiesReader.isRisorseJMXAbilitate());
						}
					}catch(Exception e){
						OpenSPCoop2Startup.log.error("Inizializzazione GestoreConsegnePreseInCarico.smistatore", e);
						return;
					}
					
					// GestoreConsegnePreseInCarico - Runtime
					try{
						if(propertiesReader.isTimerConsegnaContenutiApplicativi_runtime_useRuntimeManager()) {
							DBConsegnePreseInCaricoManager.initRuntime(DBManager.getInstance(), logCore, propertiesReader.getDatabaseType());
						}
						else {
							DBConsegnePreseInCaricoManager.initRuntime(propertiesReader.getTimerConsegnaContenutiApplicativi_runtime_dataSource(), propertiesReader.getTimerConsegnaContenutiApplicativi_runtime_dataSourceJndiContext(), 
									logCore, propertiesReader.getDatabaseType(), 
									propertiesReader.isTimerConsegnaContenutiApplicativi_runtime_dataSource_useDBUtils(), propertiesReader.isRisorseJMXAbilitate());
						}
					}catch(Exception e){
						OpenSPCoop2Startup.log.error("Inizializzazione GestoreConsegnePreseInCarico.runtime", e);
						return;
					}
					
					// GestoreConsegnePreseInCarico - Transazioni
					try{
						if(propertiesReader.isTimerConsegnaContenutiApplicativi_transazioni_useTransactionManager()) {
							DBConsegnePreseInCaricoManager.initTransazioni(DBTransazioniManager.getInstance(), logCore, propertiesReader.getDatabaseType());
						}
						else {
							DBConsegnePreseInCaricoManager.initTransazioni(propertiesReader.getTimerConsegnaContenutiApplicativi_transazioni_dataSource(), propertiesReader.getTimerConsegnaContenutiApplicativi_transazioni_dataSourceJndiContext(), 
									logCore, propertiesReader.getDatabaseType(), 
									propertiesReader.isTimerConsegnaContenutiApplicativi_transazioni_dataSource_useDBUtils(), propertiesReader.isRisorseJMXAbilitate());
						}
					}catch(Exception e){
						OpenSPCoop2Startup.log.error("Inizializzazione GestoreConsegnePreseInCarico.transazioni", e);
						return;
					}
					
				}
			}
			if(propertiesReader.isIntegrationManagerEnabled()) {
				// GestoreConsegneMessageBox - Runtime
				try{
					if(propertiesReader.isIntegrationManager_runtime_useRuntimeManager()) {
						DBConsegneMessageBoxManager.initRuntime(DBManager.getInstance(), logCore, propertiesReader.getDatabaseType());
					}
					else if(propertiesReader.isIntegrationManager_runtime_useConsegnePreseInCaricoManager()) {
						DBConsegnePreseInCaricoManager instance = DBConsegnePreseInCaricoManager.getInstanceRuntime();
						if(instance==null) {
							throw new CoreException("DBConsegnePreseInCaricoManager-runtime richiesto dalla configurazione del servizio MessageBox non risulta attivo");
						}
						DBConsegneMessageBoxManager.initRuntime(instance, logCore, propertiesReader.getDatabaseType());
					}
					else {
						DBConsegneMessageBoxManager.initRuntime(propertiesReader.getIntegrationManager_runtime_dataSource(), propertiesReader.getIntegrationManager_runtime_dataSourceJndiContext(), 
								logCore, propertiesReader.getDatabaseType(), 
								propertiesReader.isIntegrationManager_runtime_dataSource_useDBUtils(), propertiesReader.isRisorseJMXAbilitate());
					}
				}catch(Exception e){
					OpenSPCoop2Startup.log.error("Inizializzazione GestoreConsegneMessageBox.runtime", e);
					return;
				}
				
				// GestoreConsegneMessageBox - Transazioni
				try{
					if(propertiesReader.isIntegrationManager_transazioni_useTransactionManager()) {
						DBConsegneMessageBoxManager.initTransazioni(DBTransazioniManager.getInstance(), logCore, propertiesReader.getDatabaseType());
					}
					else if(propertiesReader.isIntegrationManager_transazioni_useConsegnePreseInCaricoManager()) {
						DBConsegnePreseInCaricoManager instance = DBConsegnePreseInCaricoManager.getInstanceTransazioni();
						if(instance==null) {
							throw new CoreException("DBConsegnePreseInCaricoManager-transazioni richiesto dalla configurazione del servizio MessageBox non risulta attivo");
						}
						DBConsegneMessageBoxManager.initTransazioni(instance, logCore, propertiesReader.getDatabaseType());
					}
					else {
						DBConsegneMessageBoxManager.initTransazioni(propertiesReader.getIntegrationManager_transazioni_dataSource(), propertiesReader.getIntegrationManager_transazioni_dataSourceJndiContext(), 
								logCore, propertiesReader.getDatabaseType(), 
								propertiesReader.isIntegrationManager_transazioni_dataSource_useDBUtils(), propertiesReader.isRisorseJMXAbilitate());
					}
				}catch(Exception e){
					OpenSPCoop2Startup.log.error("Inizializzazione GestoreConsegneMessageBox.transazioni", e);
					return;
				}
			}

			
			/*----------- Inizializzazione Generatore di ClusterID  --------------*/
			String clusterID = null;
			try{
				clusterID = propertiesReader.getClusterId(false);
				
				String tipoGeneratoreClusterID = propertiesReader.getTipoIDManager();
				String classClusterID = null;
				if(CostantiConfigurazione.NONE.equals(tipoGeneratoreClusterID)){
					if(clusterID!=null){
						classClusterID = org.openspcoop2.utils.id.ClusterIdentifierGenerator.class.getName();
					}
				}else{
					classClusterID = classNameReader.getUniqueIdentifier(tipoGeneratoreClusterID);
				}
				
				if(classClusterID==null){
					UniqueIdentifierManager.disabilitaGenerazioneUID();
				}else{
					
					Object [] paramsObject = null;
					List<String> params = propertiesReader.getIDManagerParameters();
					if(params!=null && !params.isEmpty()) {
						paramsObject = new Object [params.size()];
						for (int i = 0; i < params.size(); i++) {
							paramsObject[i] = params.get(i);
						}
					}
					else {
						if(org.openspcoop2.utils.id.ClusterIdentifierGenerator.class.getName().equals(classClusterID)) {
							String idCluster = propertiesReader.getClusterId(false);
							if(idCluster!=null) {
								paramsObject = new Object [1];
								paramsObject[0] = idCluster;
							}
						}
					}
					UniqueIdentifierManager.inizializzaUniqueIdentifierManager(propertiesReader.useIDManagerWithThreadLocal(),classClusterID,paramsObject);
					OpenSPCoop2Startup.logStartupInfo("UUID Generator: "+classClusterID);
					
					if(propertiesReader.generazioneDateCasualiLogAbilitato()){
						GeneratoreCasualeDate.init(propertiesReader.getGenerazioneDateCasualiLogDataInizioIntervallo(), 
								propertiesReader.getGenerazioneDateCasualiLogDataFineIntervallo(),
								OpenSPCoop2Startup.log);
						OpenSPCoop2Startup.logStartupInfo("Abilitata generazione date casuali");
					}
					
					// BufferProducer avviato in fondo allo startup
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
					propertiesReader.isConfigurazioneCache_ConfigPrefill(), propertiesReader.getCryptConfigAutenticazioneApplicativi(),
					propertiesReader.getCacheTypeConfig());
			if(!isInitializeConfig){
				this.logError("Riscontrato errore durante l'inizializzazione della configurazione di OpenSPCoop.");
				return;
			}
			ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance();
			if(msgDiagProperties.checkValoriFiltriMsgDiagnostici(OpenSPCoop2Startup.log)==false){
				return;
			}
			OpenSPCoop2Startup.logStartupInfo("ConfigurazionePdD, refresh: "+propertiesReader.isConfigurazioneDinamica());





			
			
			/*----------- Inizializzazione Configurazione Cluster Dinamica --------------*/
			try{
				if(propertiesReader.isClusterDinamico()) {
					if(propertiesReader.isUseHashClusterId()) {
						propertiesReader.updateClusterId();
					}
					OpenSPCoop2Startup.logStartupInfo("Nodo avviato in modalità cluster dinamica con identificativo '"+propertiesReader.getClusterId(false)+"' ("+propertiesReader.getCluster_id_preCodificaHash()+")");
					
					// aggiorno
					clusterID = propertiesReader.getClusterId(false);
				}
				
				boolean rateLimitingGestioneCluster = false;
				if(propertiesReader.isControlloTrafficoEnabled()) {
					List<PolicyGroupByActiveThreadsType> list = configurazionePdDManager.getTipiGestoreRateLimiting();
					rateLimitingGestioneCluster = list!=null && list.contains(PolicyGroupByActiveThreadsType.LOCAL_DIVIDED_BY_NODES);
				}
				
				if(propertiesReader.isClusterDinamico() || rateLimitingGestioneCluster) {	
					DynamicClusterManager.initStaticInstance();
					DynamicClusterManager.getInstance().setRateLimitingGestioneCluster(rateLimitingGestioneCluster);
					DynamicClusterManager.getInstance().register(OpenSPCoop2Startup.log);
				}
			}catch(Exception e){
				this.logError("Riscontrato errore durante l'inizializzazione della configurazione relativa al cluster dinamico: "+e.getMessage(),e);
				return;
			}
			
			
			




			/*----------- Inizializzazione libreria --------------*/
			try{
				IGestoreRepository repository = null;
				// Il tipo deve sempre essere letto da openspcoop.properties, altrimenti la testsuite che richiede il tipo di default non viene mai eseguita.
				String tipoClass = classNameReader.getRepositoryBuste(propertiesReader.getGestoreRepositoryBuste());
				repository = (IGestoreRepository) loader.newInstance(tipoClass);
				org.openspcoop2.protocol.engine.Configurazione.init(
						propertiesReader.getGestioneSerializableDBCheckInterval(),
						repository,propertiesReader.getDatabaseType(),logCore);
			}catch(Exception e){
				this.logError("Riscontrato errore durante l'inizializzazione della libreria del protocollo: "+e.getMessage());
				logCore.error("Riscontrato errore durante l'inizializzazione della libreria del protocollo: "+e.getMessage(),e);
				return;
			}









			/* -------------- Inizializzo MsgDiagnostico -------------------- */
			MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(OpenSPCoop2Startup.ID_MODULO);


			
			
			
			
			
			
			/* -------------- Creazione lock entry non esistenti -------------------- */
			if(!TimerUtils.createEmptyLockTimers(propertiesReader, ID_MODULO, OpenSPCoop2Logger.getLoggerOpenSPCoopTimers(), true)) {
				msgDiag.logStartupError("Inizializzazione lock per i timer fallita","Inizializzazione Lock");
				return;
			}
			
			/* -------------- Rilascio lock -------------------- */
			// Il rilascio serve a ripulire eventuali lock presi e non rilasciati durante lo shutdown, poiche' la connessione non era piu' disponibile o vi e' stato un kill
			TimerUtils.relaseLockTimers(propertiesReader, ID_MODULO, OpenSPCoop2Logger.getLoggerOpenSPCoopTimers(), true);
			
			
			
			
			
			
			
			
			
			
			
			
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
							throw new CoreException("Parametro 'dimensioneCache' errato per la cache di accesso ai dati di autorizzazione");
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
							throw new CoreException("Parametro 'idleTime' errato per la cache di accesso ai dati di autorizzazione");
						}
					}
					
					long itemLifeSecond = -1;
					if(datiAutorizzazione.getCache().getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(datiAutorizzazione.getCache().getItemLifeSecond());
						}catch(Exception e){
							throw new CoreException("Parametro 'itemLifeSecond' errato per la cache di accesso ai dati di autorizzazione");
						}
					}

					GestoreAutorizzazione.initialize(propertiesReader.getCacheTypeAuthorization(), dimensioneCache, algoritmo, idleTime, itemLifeSecond, logCore);
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
							throw new CoreException("Parametro 'dimensioneCache' errato per la cache di accesso ai dati di autenticazione");
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
							throw new CoreException("Parametro 'idleTime' errato per la cache di accesso ai dati di autenticazione");
						}
					}
					
					long itemLifeSecond = -1;
					if(datiAutenticazione.getCache().getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(datiAutenticazione.getCache().getItemLifeSecond());
						}catch(Exception e){
							throw new CoreException("Parametro 'itemLifeSecond' errato per la cache di accesso ai dati di autenticazione");
						}
					}

					GestoreAutenticazione.initialize(propertiesReader.getCacheTypeAuthentication(), dimensioneCache, algoritmo, idleTime, itemLifeSecond, logCore);
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
							throw new CoreException("Parametro 'dimensioneCache' errato per la cache di accesso ai dati dei token");
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
							throw new CoreException("Parametro 'idleTime' errato per la cache di accesso ai dati dei token");
						}
					}
					
					long itemLifeSecond = -1;
					if(datiGestioneToken.getCache().getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(datiGestioneToken.getCache().getItemLifeSecond());
						}catch(Exception e){
							throw new CoreException("Parametro 'itemLifeSecond' errato per la cache di accesso ai dati dei token");
						}
					}

					GestoreToken.initializeGestioneToken(propertiesReader.getCacheTypeToken(), dimensioneCache, algoritmo, idleTime, itemLifeSecond, logCore);
				}
				else{
					GestoreToken.initializeGestioneToken(logCore);
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"Gestore Token");
				return;
			}
			
			
			
			
			
			
			/*----------- Inizializzazione AttributeAuthority --------------*/
			try{
				AccessoDatiAttributeAuthority datiAttributeAuthority = configurazionePdDManager.getAccessoDatiAttributeAuthority();
				if(datiAttributeAuthority!=null && datiAttributeAuthority.getCache()!=null){
					
					int dimensioneCache = -1;
					if(datiAttributeAuthority.getCache().getDimensione()!=null){
						try{
							dimensioneCache = Integer.parseInt(datiAttributeAuthority.getCache().getDimensione());
						}catch(Exception e){
							throw new CoreException("Parametro 'dimensioneCache' errato per la cache di accesso ai dati dei token");
						}
					}
					
					String algoritmo = null;
					if(datiAttributeAuthority.getCache().getAlgoritmo()!=null){
						algoritmo = datiAttributeAuthority.getCache().getAlgoritmo().toString();
					}
					
					long idleTime = -1;
					if(datiAttributeAuthority.getCache().getItemIdleTime()!=null){
						try{
							idleTime = Integer.parseInt(datiAttributeAuthority.getCache().getItemIdleTime());
						}catch(Exception e){
							throw new CoreException("Parametro 'idleTime' errato per la cache di accesso ai dati dei token");
						}
					}
					
					long itemLifeSecond = -1;
					if(datiAttributeAuthority.getCache().getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(datiAttributeAuthority.getCache().getItemLifeSecond());
						}catch(Exception e){
							throw new CoreException("Parametro 'itemLifeSecond' errato per la cache di accesso ai dati dei token");
						}
					}

					GestoreToken.initializeAttributeAuthority(propertiesReader.getCacheTypeAttributeAuthority(), dimensioneCache, algoritmo, idleTime, itemLifeSecond, logCore);
				}
				else{
					GestoreToken.initializeAttributeAuthority(logCore);
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"AttributeAuthority");
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
							throw new CoreException("Parametro 'dimensioneCache' errato per la cache di response caching");
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
							throw new CoreException("Parametro 'idleTime' errato per la cache di response caching");
						}
					}
					
					long itemLifeSecond = -1;
					if(responseCachingCacheConfig.getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(responseCachingCacheConfig.getItemLifeSecond());
						}catch(Exception e){
							throw new CoreException("Parametro 'itemLifeSecond' errato per la cache di response caching");
						}
					}

					GestoreCacheResponseCaching.initialize(propertiesReader.getCacheTypeResponseCaching(), dimensioneCache, algoritmo, idleTime, itemLifeSecond, logCore);
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
							throw new CoreException("Parametro 'dimensioneCache' errato per la cache di accesso ai keystore");
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
							throw new CoreException("Parametro 'idleTime' errato per la cache di accesso ai keystore");
						}
					}
					
					long itemLifeSecond = -1;
					if(keystoreCacheConfig.getCache().getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(keystoreCacheConfig.getCache().getItemLifeSecond());
						}catch(Exception e){
							throw new CoreException("Parametro 'itemLifeSecond' errato per la cache di accesso ai keystore");
						}
					}
					
					long itemCrlLifeSecond = -1;
					if(keystoreCacheConfig.getCrlItemLifeSecond()!=null){
						try{
							itemCrlLifeSecond = Integer.parseInt(keystoreCacheConfig.getCrlItemLifeSecond());
						}catch(Exception e){
							throw new CoreException("Parametro 'itemCrlLifeSecond' errato per la cache di accesso ai keystore");
						}
					}

					GestoreKeystoreCaching.initialize(propertiesReader.getCacheTypeKeystore(), dimensioneCache, algoritmo, idleTime, itemLifeSecond, logCore);
					
					if(itemCrlLifeSecond>0) {
						GestoreKeystoreCaching.setCacheCrlLifeSeconds(itemCrlLifeSecond);
					}
				}
				else{
					GestoreKeystoreCaching.initialize(logCore);
					
					// Provo ad utilizzare la cache alternativa in memoria
					GestoreKeystoreCache.setKeystoreCacheParameters(propertiesReader.isAbilitataCacheMessageSecurityKeystore(),
							propertiesReader.getItemLifeSecondCacheMessageSecurityKeystore(), 
							propertiesReader.getDimensioneCacheMessageSecurityKeystore());
					OpenSPCoop2Startup.logStartupInfo("MessageSecurity Keystore Cache In-Memory enabled["+propertiesReader.isAbilitataCacheMessageSecurityKeystore()+"] itemLifeSecond["+
							propertiesReader.getItemLifeSecondCacheMessageSecurityKeystore()+"] size["+
							propertiesReader.getDimensioneCacheMessageSecurityKeystore()+"]");
					
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"Gestore Keystore Caching");
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
							throw new CoreException("Parametro 'dimensioneCache' errato per la cache di response caching");
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
							throw new CoreException("Parametro 'idleTime' errato per la cache di response caching");
						}
					}
					
					long itemLifeSecond = -1;
					if(consegnaApplicativiCacheConfig.getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(consegnaApplicativiCacheConfig.getItemLifeSecond());
						}catch(Exception e){
							throw new CoreException("Parametro 'itemLifeSecond' errato per la cache di response caching");
						}
					}

					GestoreLoadBalancerCaching.initialize(propertiesReader.getCacheTypeLoadBalancer(), dimensioneCache, algoritmo, idleTime, itemLifeSecond, logCore);
				}
				else{
					GestoreLoadBalancerCaching.initialize(logCore);
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"Gestore Consegna Applicativi Cache");
				return;
			}
			
			
			
			
			
			
			
			
			
			/* ----------- Inizializzazione Gestore Richieste ------------ */
			
			try{
				AccessoDatiRichieste gestoreRichiesteCacheConfig = configurazionePdDManager.getAccessoDatiRichieste();
				if(gestoreRichiesteCacheConfig!=null && gestoreRichiesteCacheConfig.getCache()!=null){
					
					int dimensioneCache = -1;
					if(gestoreRichiesteCacheConfig.getCache().getDimensione()!=null){
						try{
							dimensioneCache = Integer.parseInt(gestoreRichiesteCacheConfig.getCache().getDimensione());
						}catch(Exception e){
							throw new CoreException("Parametro 'dimensioneCache' errato per la cache di accesso ai dati delle richieste");
						}
					}
					
					String algoritmo = null;
					if(gestoreRichiesteCacheConfig.getCache().getAlgoritmo()!=null){
						algoritmo = gestoreRichiesteCacheConfig.getCache().getAlgoritmo().toString();
					}
					
					long idleTime = -1;
					if(gestoreRichiesteCacheConfig.getCache().getItemIdleTime()!=null){
						try{
							idleTime = Integer.parseInt(gestoreRichiesteCacheConfig.getCache().getItemIdleTime());
						}catch(Exception e){
							throw new CoreException("Parametro 'idleTime' errato per la cache di accesso ai dati delle richieste");
						}
					}
					
					long itemLifeSecond = -1;
					if(gestoreRichiesteCacheConfig.getCache().getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(gestoreRichiesteCacheConfig.getCache().getItemLifeSecond());
						}catch(Exception e){
							throw new CoreException("Parametro 'itemLifeSecond' errato per la cache di accesso ai dati delle richieste");
						}
					}
					
					GestoreRichieste.initCacheGestoreRichieste(propertiesReader.getCacheTypeRequestManager(), dimensioneCache, algoritmo,
							idleTime, itemLifeSecond, logCore);
					
				}
				else{
					GestoreRichieste.initialize(logCore);					
				}
				
				
				RequestConfig.setUseCacheForExternalResource(propertiesReader.isConfigurazioneCacheRequestManagerExternalResourceSaveInCache());
				OpenSPCoop2Startup.logStartupInfo("RequestConfig, useCacheForExternalResource: "+RequestConfig.isUseCacheForExternalResource());
				
				RequestConfig.setUseCacheForOCSPResponse(propertiesReader.isConfigurazioneCacheRequestManagerOCSPResponseSaveInCache());
				OpenSPCoop2Startup.logStartupInfo("RequestConfig, useCacheForOCSPResponse: "+RequestConfig.isUseCacheForOCSPResponse());
				
				RequestConfig.setUseCacheForRemoteStore(propertiesReader.isConfigurazioneCacheRequestManagerRemoteStoreSaveInCache());
				OpenSPCoop2Startup.logStartupInfo("RequestConfig, useCacheForRemoteStore: "+RequestConfig.isUseCacheForRemoteStore());
				
				
			}catch(Exception e){
				msgDiag.logStartupError(e,"Gestore DatiRichieste Caching");
				return;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			/* ----------- Inizializzazione Gestore Risorse JMX (le risorse jmx vengono registrate in seguito) ------------ */
			try{
				if(propertiesReader.isRisorseJMXAbilitate()){
					if(propertiesReader.getJNDIName_MBeanServer()!=null){
						OpenSPCoop2Startup.this.gestoreRisorseJMX = new GestoreRisorseJMXGovWay(propertiesReader.getJNDIName_MBeanServer(),
								propertiesReader.getJNDIContext_MBeanServer());
					}else{
						OpenSPCoop2Startup.this.gestoreRisorseJMX = new GestoreRisorseJMXGovWay();
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
				configPdD.setAttesaAttivaJDBC(propertiesReader.getGestioneSerializableDBAttesaAttiva());
				configPdD.setCheckIntervalJDBC(propertiesReader.getGestioneSerializableDBCheckInterval());
				configPdD.setTipoDatabase(TipiDatabase.toEnumConstant(propertiesReader.getDatabaseType()));
				configPdD.setLog(logCore);
				
				Costanti.initHTTP_HEADER_GOVWAY_ERROR_STATUS(propertiesReader.getErroriHttpHeaderGovWayStatus());
				Costanti.initHTTP_HEADER_GOVWAY_ERROR_TYPE(propertiesReader.getErroriHttpHeaderGovWayType());
				Costanti.initHTTP_HEADER_GOVWAY_ERROR_CODE(propertiesReader.getErroriHttpHeaderGovWayCode());
				
				Costanti.setTRANSACTION_ERROR_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE(propertiesReader.isErroriSoapUseGovWayStatusAsFaultCode());
				Costanti.setTRANSACTION_ERROR_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE(propertiesReader.isErroriSoapHttpHeaderGovWayCodeEnabled());
								
				Costanti.setTRANSACTION_ERROR_STATUS_ABILITATO(propertiesReader.isErroriGovWayStatusEnabled());
				Costanti.setTRANSACTION_ERROR_INSTANCE_ID_ABILITATO(propertiesReader.isErroriGovWayInstanceEnabled());
				Costanti.setTRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS(propertiesReader.isErroriGovWayForceSpecificDetails());
				Costanti.setTRANSACTION_ERROR_SOAP_FAULT_ADD_FAULT_DETAILS_WITH_PROBLEM_RFC7807(propertiesReader.isErroriGovWayFaultDetailsWithProblemRFC7807());
				
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
				if(!isInitializeLoggerProtocol){
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
							String protocollo = protocolli.nextElement();
							if(ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).createValidazioneConSchema(null).initialize(factory)==false) {
								throw new CoreException("Inizializzazione validatore con schemi per il protocollo '"+protocollo+"' e messagefactory '"+factory.getClass().getName()+"' fallita");
							}
						}
					}
				}
				
				// Inizializza RemoteStore
				Enumeration<String> protocolli = ProtocolFactoryManager.getInstance().getProtocolNames();
				while (protocolli.hasMoreElements()) {
					String protocollo = protocolli.nextElement();
					List<RemoteStoreConfig> lRSC = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).createProtocolConfiguration().getRemoteStoreConfig();
					if(lRSC!=null && !lRSC.isEmpty()) {
						for (RemoteStoreConfig remoteStoreConfig : lRSC) {
							RemoteStoreProviderDriver.initialize(logCore, remoteStoreConfig);
							OpenSPCoop2Startup.logStartupInfo("Inizializzazione RemoteStoreProvider ["+remoteStoreConfig.getStoreLabel()+"] effettuata.");
						}
					}
				}
				
				// Inizializzazione SignalHub
				if(ProtocolFactoryManager.getInstance().existsProtocolFactory(org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROTOCOL_NAME)) {
					CostantiDB.setServiziDigestEnabled(ModIUtils.isSignalHubEnabled());
				}
				
				OpenSPCoop2Startup.logStartupInfo("ProtocolFactory default: "+protocolFactoryManager.getDefaultProtocolFactory().getProtocol());
			} catch(Exception e) {
				this.logError("Initialize ProtocolFactoryManager failed: "+e.getMessage());
				logCore.error("Initialize ProtocolFactoryManager failed: "+e.getMessage(),e);
				msgDiag.logStartupError("Riscontrato errore durante l'inizializzazione del ProtocolFactoryManager","initProtocolFactoryManager");
				return;
			}
			
			
			
			
			
			
			/* ------------- Verifica Proprieta' di OpenSPCoop che richiedono l'inizializzazione del ProtocolFactoryManager --------------- */
			if(o!=null){
				if(propertiesReader.validaConfigurazioneDopoInizializzazioneProtocolManager((java.lang.ClassLoader)o[0]) == false){
					return;
				}
			}else{
				if(propertiesReader.validaConfigurazioneDopoInizializzazioneProtocolManager(null) == false){
					return;
				}
			}
			
			
			


			/* ------------- Inizializzo il sistema di Logging per gli appender personalizzati --------------- */
			boolean isInitializeAppender = false;
			isInitializeAppender = OpenSPCoop2Logger.initializeMsgDiagnosticiOpenSPCoopAppender(configurazionePdDManager.getOpenSPCoopAppenderMessaggiDiagnostici());
			if(!isInitializeAppender){
				return;
			}
			isInitializeAppender = OpenSPCoop2Logger.initializeTracciamentoOpenSPCoopAppender(configurazionePdDManager.getOpenSPCoopAppenderTracciamento());
			if(!isInitializeAppender){
				return;
			}
			isInitializeAppender = OpenSPCoop2Logger.initializeDumpOpenSPCoopAppender(configurazionePdDManager.getOpenSPCoopAppenderDump());
			if(!isInitializeAppender){
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
					OpenSPCoop2Startup.logStartupInfo("Inizializzazione connectionFactoryJMS ["+propertiesReader.getJNDIName_ConnectionFactory()+"] effettuata.");

					// Code di Ricezione
					if( CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_JMS.equals(propertiesReader.getNodeReceiver()) 
					){
						try{
							QueueManager.initializeQueueNodeReceiver(propertiesReader.getJNDIContext_CodeInterne());
						}catch(Exception e){
							msgDiag.logStartupError(e, "QueueManager.initQueueNodeReceiver");
							return;
						}
						OpenSPCoop2Startup.logStartupInfo("Inizializzazione code JMS per la ricezione di messaggi nell'infrastruttura di OpenSPCoop, effettuata.");
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
					OpenSPCoop2Startup.logStartupInfo("Inizializzazione code JMS per la spedizione di messaggi nell'infrastruttura di OpenSPCoop, effettuata.");
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
			DriverBYOK driverBYOK = null;
			try{
				driverBYOK = DriverBYOKUtilities.newInstanceDriverBYOKRuntimeNode(logCore, false, true);
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione driver BYOK fallita");
				return;
			}
			boolean isInitializeRegistro = 
				RegistroServiziReader.initialize(accessoRegistro,
						logCore,OpenSPCoop2Startup.log,propertiesReader.isControlloRisorseRegistriRaggiungibilitaTotale(),
						propertiesReader.isReadObjectStatoBozza(),propertiesReader.getJNDIName_DataSource(),
						propertiesReader.isDSOp2UtilsEnabled(), propertiesReader.isRisorseJMXAbilitate(),
						propertiesReader.isConfigurazioneCache_RegistryPrefill(), propertiesReader.getCryptConfigAutenticazioneSoggetti(),
						propertiesReader.getCacheTypeRegistry(), driverBYOK);
			if(!isInitializeRegistro){
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
			/**try{
				configurazionePdDReader.initializeRegistroServiziReader();
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione Reader per il registro dei servizi utilizzato nella configurazione");
				return;
			}*/


			
			
			
			
			
			
			
			/* ------------ PreLoading ------------- */
			
			if(propertiesReader.getConfigPreLoadingLocale()!=null) {
				try{
					
					Object oConfig = ConfigurazionePdDReader.getDriverConfigurazionePdD();
					Object oRegistry = RegistroServiziReader.getDriverRegistroServizi().values().iterator().next();
					if( (oConfig instanceof DriverConfigurazioneDB) && (oRegistry instanceof DriverRegistroServiziDB) ) {
						PreLoadingConfig preLoading = new PreLoadingConfig(logCore, logCore, propertiesReader.getDefaultProtocolName(),
								propertiesReader.getIdentitaPortaDefault(propertiesReader.getDefaultProtocolName(), null));
						
						TimerLock timerLock = new TimerLock(TipoLock.STARTUP);
						InfoStatistics semaphore_statistics = null;
						Semaphore semaphore = null;
						Logger logTimer = OpenSPCoop2Logger.getLoggerOpenSPCoopTimers();
						if(propertiesReader.isTimerLockByDatabase()) {
							semaphore_statistics = new InfoStatistics();

							SemaphoreConfiguration config = GestoreMessaggi.newSemaphoreConfiguration(propertiesReader.getStartupLockMaxLife(), 
									propertiesReader.getStartupLockIdleTime());

							TipiDatabase databaseType = TipiDatabase.toEnumConstant(propertiesReader.getDatabaseType());
							try {
								semaphore = new Semaphore(semaphore_statistics, SemaphoreMapping.newInstance(timerLock.getIdLock()), 
										config, databaseType, logTimer);
							}catch(Exception e) {
								throw new TimerException(e.getMessage(),e);
							}
						}
						
						OpenSPCoopStateful openspcoopstate = new OpenSPCoopStateful();
						try {
							openspcoopstate.initResource(propertiesReader.getIdentitaPortaDefaultWithoutProtocol(),ID_MODULO, null);
							Connection connectionDB = ((StateMessage)openspcoopstate.getStatoRichiesta()).getConnectionDB();
						
							String causa = "Preloading Configuration";
							try {
								GestoreMessaggi.acquireLock(
										semaphore, connectionDB, timerLock,
										msgDiag, causa, 
										propertiesReader.getStartupGetLockAttesaAttiva(), 
										propertiesReader.getStartupGetLockCheckInterval());
								
								preLoading.loadConfig(propertiesReader.getConfigPreLoadingLocale());
								
							}finally{
								try{
									GestoreMessaggi.releaseLock(
											semaphore, connectionDB, timerLock,
											msgDiag, causa);
								}catch(Exception e){
									// ignore
								}
							}
							
						}finally{
							if(openspcoopstate!=null)
								openspcoopstate.releaseResource();
						}
							
					}
										
				}catch(Exception e){
					this.logError("Riscontrato errore durante il preloading della configurazione di OpenSPCoop: "+e.getMessage(),e);
					return;
				}
			}







			// *** Repository plugins ***
			try{
				if(propertiesReader.isConfigurazionePluginsEnabled()) {
					CorePluginLoader.initialize(loader, OpenSPCoop2Logger.getLoggerOpenSPCoopPlugins(propertiesReader.isConfigurazionePluginsDebug()),
							PddPluginLoader.class,
							configurazionePdDManager.getRegistroPluginsReader(),
							propertiesReader.getConfigurazionePluginsSeconds());
				}
				else {
					CorePluginLoader.initialize(loader, OpenSPCoop2Logger.getLoggerOpenSPCoopPlugins(propertiesReader.isConfigurazionePluginsDebug()),
							PddPluginLoader.class);
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione plugins");
				return;
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

			// Libreria di validazione xsd
			try {
				XSDSchemaCollection.setSerializeXSDSchemiBuildSchemaSuccessDefault(propertiesReader.isValidazioneContenutiApplicativiXsdBuildSchemaSuccessSerializeXSDCollection());
				XSDSchemaCollection.setSerializeXSDSchemiBuildSchemaErrorDefault(propertiesReader.isValidazioneContenutiApplicativiXsdBuildSchemaErrorSerializeXSDCollection());
				XSDSchemaCollection.setSerializeXSDSchemiBuildSchemaDefaultDir(propertiesReader.getValidazioneContenutiApplicativiXsdBuildSchemaSerializeXSDCollectionDir());
								
				XSDSchemaCollection s = new XSDSchemaCollection();
				OpenSPCoop2Startup.logStartupInfo("XSDSchemaCollection buildSchemaSuccess:"+s.isSerializeXSDSchemiBuildSchemaSuccess());
				OpenSPCoop2Startup.logStartupInfo("XSDSchemaCollection buildSchemaError:"+s.isSerializeXSDSchemiBuildSchemaError());
				OpenSPCoop2Startup.logStartupInfo("XSDSchemaCollection buildSchemaDir:"+s.getSerializeXSDSchemiBuildSchemaDir());
			}catch(Exception e){
				msgDiag.logStartupError(e,"Configurazione libreria di validazione xsd");
				return;
			}
			
			// Libreria di validazione wsdl
			try {
				boolean normalizeNamespaceXSITypeDefault = propertiesReader.isValidazioneContenutiApplicativiRpcAddNamespaceXSITypeIfNotExists()
						||
						propertiesReader.isValidazioneContenutiApplicativiDocumentAddNamespaceXSITypeIfNotExists()
						||
						propertiesReader.isValidazioneContenutiApplicativiXsdAddNamespaceXSITypeIfNotExists();
				AbstractBaseOpenSPCoop2Message.setNormalizeNamespaceXSITypeDefault(normalizeNamespaceXSITypeDefault);
				OpenSPCoop2Message msg = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createEmptyMessage(MessageType.SOAP_11, MessageRole.REQUEST);
				if(msg instanceof AbstractBaseOpenSPCoop2Message) {
					AbstractBaseOpenSPCoop2Message a = (AbstractBaseOpenSPCoop2Message) msg;
					OpenSPCoop2Startup.logStartupInfo("WSDLValidator addNamespaceXSITypeIfNotExists:"+a.isNormalizeNamespaceXSIType());
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"Configurazione libreria di validazione wsdl");
				return;
			}
			
			// Libreria di validazione openapi4j
			try {
				org.openapi4j.schema.validator.v3.ValidationOptions.VALIDATE_BASE64_VALUES=propertiesReader.isValidazioneContenutiApplicativiOpenApiOpenapi4jValidateBase64Values();
				org.openapi4j.parser.validation.v3.OpenApi3Validator.VALIDATE_URI_REFERENCE_AS_URL = propertiesReader.isValidazioneContenutiApplicativiOpenApiOpenapi4jValidateUriReferenceAsUrl();
				org.openapi4j.schema.validator.v3.FormatValidator.setDateTimeAllowLowerCaseTZ(propertiesReader.isValidazioneContenutiApplicativiOpenApiDateTimeAllowLowerCaseTZ());
				org.openapi4j.schema.validator.v3.FormatValidator.setDateTimeAllowSpaceSeparator(propertiesReader.isValidazioneContenutiApplicativiOpenApiDateTimeAllowSpaceSeparator());
			}catch(Exception e){
				msgDiag.logStartupError(e,"Configurazione libreria di validazione openapi4j");
				return;
			}
			
			// Libreria di validazione generica delle dae
			try {
				DateUtils.setDateTimeAllowLowerCaseTZ(propertiesReader.isValidazioneContenutiApplicativiOpenApiDateTimeAllowLowerCaseTZ());
				DateUtils.setDateTimeAllowSpaceSeparator(propertiesReader.isValidazioneContenutiApplicativiOpenApiDateTimeAllowSpaceSeparator());
			}catch(Exception e){
				msgDiag.logStartupError(e,"Configurazione libreria di validazione DateUtils");
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
				RicezioneContenutiApplicativi.initializeService(classNameReader, propertiesReader, logCore);
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione servizio RicezioneContenutiApplicativi");
				return;
			}
			try{
				RicezioneBuste.initializeService(classNameReader, propertiesReader, logCore);
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
			
			
			
			
			
			
			/* ----------- Inizializzazione Connettori ------------ */
			try{
				ConnectorApplicativeThreadPool.initialize(propertiesReader);				
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione Applicative Thread Pool per i connettori");
				return;
			}
			
			/* ----------- Inizializzazione BIO Client ------------ */
			try{
				org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCOREConnectionManager.initialize();
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione BIO Client Manager");
				return;
			}
			
			/* ----------- Inizializzazione NIO Client ------------ */
			try{
				if(propertiesReader.isNIOEnabled()) {
					org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPCOREConnectionManager.initialize();
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione NIO Client Manager");
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
			FileSystemMkdirConfig configMkdir = null;
			try{
				
				configMkdir = new FileSystemMkdirConfig();
				configMkdir.setCheckCanWrite(true);
				configMkdir.setCheckCanRead(true);
				configMkdir.setCheckCanExecute(false);
				configMkdir.setCrateParentIfNotExists(true);
				
				// logDir (sarebbe meglio se fosse creata dall'utente)
				List<File> listFiles = OpenSPCoop2Logger.getLogDirs();
				if(listFiles!=null && !listFiles.isEmpty()) {
					for (File file : listFiles) {
						if(!file.exists()){
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
				if(propertiesReader.isConnettoreHttpUrlHttpsOverrideDefaultConfigurationConsegnaContenutiApplicativi()) {
					File dir = propertiesReader.getConnettoreHttpUrlHttpsRepositoryConsegnaContenutiApplicativi();
					configMkdir.setCheckCanWrite(false);
					FileSystemUtilities.mkdir(dir, configMkdir);
					configMkdir.setCheckCanWrite(true);
				}
				if(propertiesReader.isConnettoreHttpUrlHttpsOverrideDefaultConfigurationInoltroBuste()) {
					File dir = propertiesReader.getConnettoreHttpUrlHttpsRepositoryInoltroBuste();
					configMkdir.setCheckCanWrite(false);
					FileSystemUtilities.mkdir(dir, configMkdir);
					configMkdir.setCheckCanWrite(true);
				}
				
				// recovery
				File dirRecovery = propertiesReader.getFileSystemRecoveryRepository();
				FileSystemUtilities.mkdir(dirRecovery, configMkdir);
				FileSystemSerializer fs = FileSystemSerializer.getInstance();
				FileSystemUtilities.mkdir(fs.getDirTransazioni().getAbsolutePath(), configMkdir);
				FileSystemUtilities.mkdir(fs.getDirTransazioneApplicativoServer().getAbsolutePath(), configMkdir);
				FileSystemUtilities.mkdir(fs.getDirTransazioneApplicativoServerConsegnaTerminata().getAbsolutePath(), configMkdir);
				FileSystemUtilities.mkdir(fs.getDirDiagnostici().getAbsolutePath(), configMkdir);
				FileSystemUtilities.mkdir(fs.getDirTracce().getAbsolutePath(), configMkdir);
				FileSystemUtilities.mkdir(fs.getDirDump().getAbsolutePath(), configMkdir);
				FileSystemUtilities.mkdir(fs.getDirEventi().getAbsolutePath(), configMkdir);
				
				// dumpBinario
				File dirBinario = propertiesReader.getDumpBinarioRepository();
				FileSystemUtilities.mkdir(dirBinario, configMkdir);
								
				// dumpNotRealTime
				if(propertiesReader.isDumpNonRealtimeFileSystemMode()) {
					File dir = propertiesReader.getDumpNonRealtimeRepository();
					FileSystemUtilities.mkdir(dir, configMkdir);
				}
				
				// attachments
				AttachmentsProcessingMode attachProcessingMode = propertiesReader.getAttachmentsProcessingMode();
				if(attachProcessingMode!=null && attachProcessingMode.getFileRepository()!=null){
					File dir = attachProcessingMode.getFileRepository();
					FileSystemUtilities.mkdir(dir, configMkdir);
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
					ConfigurazioneGenerale configurazioneControlloTraffico = configurazionePdDManager.getConfigurazioneControlloTraffico(null);
					if(configurazioneControlloTraffico.getCache()!=null && configurazioneControlloTraffico.getCache().isCache()){
						GestoreCacheControlloTraffico.initializeCache(propertiesReader.getCacheTypeTrafficControl(),configurazioneControlloTraffico.getCache().getSize(),
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
				org.openspcoop2.pdd.core.controllo_traffico.ConfigurazioneGatewayControlloTraffico confControlloTraffico = null;
				try{
					confControlloTraffico = propertiesReader.getConfigurazioneControlloTraffico();

					DatiStatisticiDAOManager.initialize(confControlloTraffico);
				}catch(Exception e){
					msgDiag.logStartupError(e,"Inizializzazione Gestori Dati Statistici del ControlloTraffico");
					return;
				}
				
				// Gestore Controllo Traffico
				try{
					GestoreControlloTraffico.initialize(confControlloTraffico.isErroreGenerico());
					GestoreCacheControlloTraffico.initialize(confControlloTraffico);
				}catch(Exception e){
					msgDiag.logStartupError(e,"Inizializzazione Gestori del ControlloTraffico");
					return;
				}
				
				// HazelcastManager
				if(propertiesReader.isHazelcastEngineEnabled()) {
					setSystemProperties(org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.Costanti.SECURITY_RECOMMENDATIONS, 
							propertiesReader.isHazelcastSecurityRecommendationsEnabled() ? 
									org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.Costanti.SECURITY_RECOMMENDATIONS_ENABLED :
									org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.Costanti.SECURITY_RECOMMENDATIONS_DISABLED);
					
					boolean diag = propertiesReader.isHazelcastDiagnosticsEnabled();
					setSystemProperties(org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.Costanti.DIAGNOSTICS, 
							diag ? 
									org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.Costanti.DIAGNOSTICS_ENABLED :
									org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.Costanti.DIAGNOSTICS_DISABLED);
					if(diag) {
						try{
							File hazelcastDiagnosticDir = propertiesReader.getHazelcastDiagnosticsDirectory();
							FileSystemUtilities.mkdir(hazelcastDiagnosticDir, configMkdir);
							setSystemProperties(org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.Costanti.DIAGNOSTICS_DIRECTORY, hazelcastDiagnosticDir.getAbsolutePath());
							setSystemProperties(org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.Costanti.DIAGNOSTICS_DIRECTORY_MAX_ROLLED_FILE_COUNT, propertiesReader.getHazelcastDiagnosticsMaxRolledFileCount()+"");
							setSystemProperties(org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.Costanti.DIAGNOSTICS_DIRECTORY_MAX_FILE_SIZE_MB, propertiesReader.getHazelcastDiagnosticsMaxFileSizeMb()+"");
						}catch(Exception e){
							msgDiag.logStartupError(e,"Inizializzazione Hazelcast diagnostic configuration");
							return;
						}
					}
					
					try{
						Map<PolicyGroupByActiveThreadsType,String> config = new HashMap<>();
						config.put(PolicyGroupByActiveThreadsType.HAZELCAST_MAP, propertiesReader.getControlloTrafficoGestorePolicyInMemoryHazelCastMapConfigPath());
						config.put(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE, propertiesReader.getControlloTrafficoGestorePolicyInMemoryHazelCastNearCacheConfigPath());
						config.put(PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE, propertiesReader.getControlloTrafficoGestorePolicyInMemoryHazelCastLocalCacheConfigPath());
						config.put(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP, propertiesReader.getControlloTrafficoGestorePolicyInMemoryHazelCastNearCacheUnsafeSyncMapConfigPath());
						config.put(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP, propertiesReader.getControlloTrafficoGestorePolicyInMemoryHazelCastNearCacheUnsafeAsyncMapConfigPath());
						config.put(PolicyGroupByActiveThreadsType.HAZELCAST_REPLICATED_MAP, propertiesReader.getControlloTrafficoGestorePolicyInMemoryHazelCastReplicatedMapConfigPath());
						config.put(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG, propertiesReader.getControlloTrafficoGestorePolicyInMemoryHazelCastAtomicLongConfigPath());
						config.put(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG_ASYNC, propertiesReader.getControlloTrafficoGestorePolicyInMemoryHazelCastAtomicLongAsyncConfigPath());
						config.put(PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER, propertiesReader.getControlloTrafficoGestorePolicyInMemoryHazelCastPNCounterConfigPath());
						HazelcastManager.initialize(log, logControlloTraffico, config, 
								propertiesReader.getControlloTrafficoGestorePolicyInMemoryHazelCastGroupId(),
								propertiesReader.getControlloTrafficoGestorePolicyInMemoryHazelCastSharedConfig());
					}catch(Exception e){
						msgDiag.logStartupError(e,"Inizializzazione HazelcastManager");
						return;
					}
				}
				
				// RedisManager
				if(propertiesReader.isRedisEngineEnabled()) {
					try{
						RedissonManager.initialize(log, logControlloTraffico, propertiesReader.getControlloTrafficoGestorePolicyInMemoryRedisConnectionUrl());
					}catch(Exception e){
						msgDiag.logStartupError(e,"Inizializzazione RedisManager");
						return;
					}
				}
				
				// Gestore RateLimiting
				List<PolicyGroupByActiveThreadsType> listGestorePolicyRT = null;
				try{
					List<PolicyGroupByActiveThreadsType> listConfig = configurazionePdDManager.getTipiGestoreRateLimiting();
					List<PolicyGroupByActiveThreadsType> listStartup = null;
					if(listConfig!=null && !listConfig.isEmpty()) {
						listStartup = new ArrayList<PolicyGroupByActiveThreadsType>();
						for (PolicyGroupByActiveThreadsType type : listConfig) {
							if(propertiesReader.isControlloTrafficoGestorePolicyInMemoryTypeLazyInitialization(type)) {
								log.debug("Gestore Policy di RateLimiting '"+type+"' trovato nella configurazione non inizializzato (lazy); verrà attivato alla prima richiesta.");	
							}
							else {
								listStartup.add(type);
							}
						}
					}
					GestorePolicyAttive.initialize(log, logControlloTraffico, propertiesReader.getControlloTrafficoGestorePolicyTipo(),
							propertiesReader.getControlloTrafficoGestorePolicyWSUrl(),
							listStartup);
					listGestorePolicyRT = GestorePolicyAttive.getTipiGestoriAttivi();
				}catch(Exception e){
					msgDiag.logStartupError(e,"Inizializzazione Gestori Policy di Rate Limiting");
					return;
				}
				if(listGestorePolicyRT!=null && !listGestorePolicyRT.isEmpty()) {
					for (PolicyGroupByActiveThreadsType type : listGestorePolicyRT) {
						
						if(type.isRedis() && !RedissonManager.isRedissonClientInitialized()) {
							// se arrivo qua, significa che nella proprietà di openspcoop ho accettato questa condizione.
							// chiamarente se Redis server non e' disponibile, non posso neanche inizializzarlo
							continue;
						}
						
						File fDati = null;
						try{
							File fRepository = propertiesReader.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository();
							if(fRepository!=null){
								if(!fRepository.exists()){
									throw new CoreException("Directory ["+fRepository.getAbsolutePath()+"] not exists");
								}
								if(!fRepository.isDirectory()){
									throw new CoreException("File ["+fRepository.getAbsolutePath()+"] is not directory");
								}
								if(!fRepository.canRead()){
									throw new CoreException("File ["+fRepository.getAbsolutePath()+"] cannot read");
								}
								if(!fRepository.canWrite()){
									throw new CoreException("File ["+fRepository.getAbsolutePath()+"] cannot write");
								}
								fDati = new File(fRepository, GestorePolicyAttive.getControlloTrafficoImage(type));
								if(fDati.exists() && fDati.canRead() && fDati.length()>0){
									FileInputStream fin = new FileInputStream(fDati);
									GestorePolicyAttive.getInstance(type).initialize(fin,confControlloTraffico);
									if(!fDati.delete()) {
										// ignore
									}
								}
							}
						}catch(Throwable e){
							String img = null;
							if(fDati!=null){
								img = fDati.getAbsolutePath();
							}
							logControlloTraffico.error("Inizializzazione dell'immagine ["+img+"] per il Gestore delle Policy di RateLimiting (tipo:"+type+") non riuscita: "+e.getMessage(),e);
							logCore.error("Inizializzazione dell'immagine ["+img+"] per il Gestore delle Policy di RateLimiting (tipo:"+type+" non riuscita: "+e.getMessage(),e);
							msgDiag.logStartupError(e,"Inizializzazione Immagine delle Policy di RateLimiting (tipo:"+type+")");
							return;
						}
					}
					
					// ripulisco vecchi contatori rimasti in memoria
					for (PolicyGroupByActiveThreadsType type : listGestorePolicyRT) {
						try{
							GestorePolicyAttive.getInstance(type).cleanOldActiveThreadsPolicy();
						}catch(Throwable e){
							logControlloTraffico.error("Pulizia dei dati presenti nella memoria del gestore delle Policy di RateLimiting (tipo:"+type+") non riuscita: "+e.getMessage(),e);
							logCore.error("Pulizia dei dati presenti nella memoria del gestore delle Policy di RateLimiting (tipo:"+type+") non riuscita: "+e.getMessage(),e);
							msgDiag.logStartupError(e,"Pulizia dei dati presenti nella memoria del gestore delle Policy di RateLimiting (tipo:"+type+")");
						}
					}
					
				}
				
				boolean force = true;
				OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(force).info("Motore di gestione del Controllo del Traffico avviato correttamente");
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
				// MBean AttributeAuthority
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanAttributeAuthority();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - dati raccolti tramite attribute authority");
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
				// MBean GestoreRichieste
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanGestioneRichieste();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - gestione richieste");
				}
			}


			
			
			
			
			/* ----------- Log Configurazione di Sistema ------------ */
			
			InformazioniStatoPorta informazioniStatoPorta = new InformazioniStatoPorta();
			List<InformazioniStatoPortaCache> informazioniStatoPortaCache = new ArrayList<>();
			
			AccessoRegistroServizi infoRegistroServizi = new AccessoRegistroServizi();
			InformazioniStatoPortaCache informazioniStatoPortaCacheRegistro = new InformazioniStatoPortaCache(CostantiPdD.JMX_REGISTRO_SERVIZI, infoRegistroServizi.isCacheAbilitata());
			if(infoRegistroServizi.isCacheAbilitata()){
				informazioniStatoPortaCacheRegistro.setStatoCache(infoRegistroServizi.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCacheRegistro);
			
			org.openspcoop2.pdd.core.jmx.ConfigurazionePdD infoConfigurazione = new org.openspcoop2.pdd.core.jmx.ConfigurazionePdD();
			InformazioniStatoPortaCache informazioniStatoPortaCacheConfig = new InformazioniStatoPortaCache(CostantiPdD.JMX_CONFIGURAZIONE_PDD, infoConfigurazione.isCacheAbilitata());
			if(infoConfigurazione.isCacheAbilitata()){
				informazioniStatoPortaCacheConfig.setStatoCache(infoConfigurazione.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCacheConfig);
			
			org.openspcoop2.pdd.core.jmx.EngineAutorizzazione infoAutorizzazioneDati = new org.openspcoop2.pdd.core.jmx.EngineAutorizzazione();
			InformazioniStatoPortaCache informazioniStatoPortaCacheAutorizzazioneDati = new InformazioniStatoPortaCache(CostantiPdD.JMX_AUTORIZZAZIONE, infoAutorizzazioneDati.isCacheAbilitata());
			if(infoAutorizzazioneDati.isCacheAbilitata()){
				informazioniStatoPortaCacheAutorizzazioneDati.setStatoCache(infoAutorizzazioneDati.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCacheAutorizzazioneDati);
			
			org.openspcoop2.pdd.core.jmx.EngineAutenticazione infoAutenticazioneDati = new org.openspcoop2.pdd.core.jmx.EngineAutenticazione();
			InformazioniStatoPortaCache informazioniStatoPortaCacheAutenticazioneDati = new InformazioniStatoPortaCache(CostantiPdD.JMX_AUTENTICAZIONE, infoAutenticazioneDati.isCacheAbilitata());
			if(infoAutenticazioneDati.isCacheAbilitata()){
				informazioniStatoPortaCacheAutenticazioneDati.setStatoCache(infoAutenticazioneDati.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCacheAutenticazioneDati);
			
			org.openspcoop2.pdd.core.jmx.EngineGestioneToken infoGestioneTokenDati = new org.openspcoop2.pdd.core.jmx.EngineGestioneToken();
			InformazioniStatoPortaCache informazioniStatoPortaCacheGestioneTokenDati = new InformazioniStatoPortaCache(CostantiPdD.JMX_TOKEN, infoGestioneTokenDati.isCacheAbilitata());
			if(infoGestioneTokenDati.isCacheAbilitata()){
				informazioniStatoPortaCacheGestioneTokenDati.setStatoCache(infoGestioneTokenDati.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCacheGestioneTokenDati);
			
			org.openspcoop2.pdd.core.jmx.EngineAttributeAuthority infoAttributeAuthorityDati = new org.openspcoop2.pdd.core.jmx.EngineAttributeAuthority();
			InformazioniStatoPortaCache informazioniStatoPortaCacheAttributeAuthorityDati = new InformazioniStatoPortaCache(CostantiPdD.JMX_ATTRIBUTE_AUTHORITY, infoAttributeAuthorityDati.isCacheAbilitata());
			if(infoAttributeAuthorityDati.isCacheAbilitata()){
				informazioniStatoPortaCacheAttributeAuthorityDati.setStatoCache(infoAttributeAuthorityDati.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCacheAttributeAuthorityDati);
			
			org.openspcoop2.pdd.core.jmx.EngineResponseCaching infoResponseCaching = new org.openspcoop2.pdd.core.jmx.EngineResponseCaching();
			InformazioniStatoPortaCache informazioniStatoPortaCacheResponseCaching = new InformazioniStatoPortaCache(CostantiPdD.JMX_RESPONSE_CACHING, infoResponseCaching.isCacheAbilitata());
			if(infoResponseCaching.isCacheAbilitata()){
				informazioniStatoPortaCacheResponseCaching.setStatoCache(infoResponseCaching.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCacheResponseCaching);
			
			org.openspcoop2.pdd.core.jmx.EngineKeystoreCaching infoKeystoreCaching = new org.openspcoop2.pdd.core.jmx.EngineKeystoreCaching();
			InformazioniStatoPortaCache informazioniStatoPortaCacheKeystoreCaching = new InformazioniStatoPortaCache(CostantiPdD.JMX_KEYSTORE_CACHING, infoKeystoreCaching.isCacheAbilitata());
			if(infoKeystoreCaching.isCacheAbilitata()){
				informazioniStatoPortaCacheKeystoreCaching.setStatoCache(infoKeystoreCaching.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCacheKeystoreCaching);
			
			org.openspcoop2.pdd.core.jmx.GestoreConsegnaApplicativi infoGestoreConsegnaApplicativi = new org.openspcoop2.pdd.core.jmx.GestoreConsegnaApplicativi();
			InformazioniStatoPortaCache informazioniStatoPortaCacheGestoreConsegnaApplicativi = new InformazioniStatoPortaCache(CostantiPdD.JMX_LOAD_BALANCER, infoGestoreConsegnaApplicativi.isCacheAbilitata());
			if(infoGestoreConsegnaApplicativi.isCacheAbilitata()){
				informazioniStatoPortaCacheGestoreConsegnaApplicativi.setStatoCache(infoGestoreConsegnaApplicativi.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCacheGestoreConsegnaApplicativi);
			
			org.openspcoop2.pdd.core.jmx.GestoreRichieste infoGestoreRichieste = new org.openspcoop2.pdd.core.jmx.GestoreRichieste();
			InformazioniStatoPortaCache informazioniStatoPortaCacheGestoreRichieste = new InformazioniStatoPortaCache(CostantiPdD.JMX_GESTORE_RICHIESTE, infoGestoreRichieste.isCacheAbilitata());
			if(infoGestoreRichieste.isCacheAbilitata()){
				informazioniStatoPortaCacheGestoreRichieste.setStatoCache(infoGestoreRichieste.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCacheGestoreRichieste);
			
			org.openspcoop2.pdd.core.jmx.RepositoryMessaggi infoRepositoryMessaggi = new org.openspcoop2.pdd.core.jmx.RepositoryMessaggi();
			InformazioniStatoPortaCache informazioniStatoPortaCacheRepositoryMessaggi = new InformazioniStatoPortaCache(CostantiPdD.JMX_REPOSITORY_MESSAGGI, infoRepositoryMessaggi.isCacheAbilitata());
			if(infoRepositoryMessaggi.isCacheAbilitata()){
				informazioniStatoPortaCacheRepositoryMessaggi.setStatoCache(infoRepositoryMessaggi.printStatCache());
			}
			informazioniStatoPortaCache.add(informazioniStatoPortaCacheRepositoryMessaggi);
			
			ConfigurazioneSistema infoConfigSistema = new ConfigurazioneSistema();
			ConfigurazioneSistema.setIncludePassword(propertiesReader.isConfigurazioneSistema_javaProperties_showPassword());
			StatoServiziJMXResource statoServiziPdD = new StatoServiziJMXResource();
			OpenSPCoop2Logger.getLoggerOpenSPCoopConfigurazioneSistema().
				info(informazioniStatoPorta.formatStatoPorta(infoConfigSistema.getVersionePdD(), 
						infoConfigSistema.getVersioneBaseDati(), infoConfigSistema.getDirectoryConfigurazione(), 
						infoConfigSistema.getVersioneJava(), infoConfigSistema.getVendorJava(), infoConfigSistema.getMessageFactory(),
						statoServiziPdD.getComponentePD(), statoServiziPdD.getComponentePD_abilitazioniPuntuali(), statoServiziPdD.getComponentePD_disabilitazioniPuntuali(),
						statoServiziPdD.getComponentePA(), statoServiziPdD.getComponentePA_abilitazioniPuntuali(), statoServiziPdD.getComponentePA_disabilitazioniPuntuali(),
						statoServiziPdD.getComponenteIM(),
						LogLevels.toOpenSPCoop2(configurazionePdDManager.getSeveritaMessaggiDiagnostici(),true),
						LogLevels.toOpenSPCoop2(configurazionePdDManager.getSeveritaLog4JMessaggiDiagnostici(),true),
						OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato, OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato, OpenSPCoop2Logger.loggerIntegrationManagerAbilitato,
						configurazionePdDManager.tracciamentoBuste(), 
						configurazionePdDManager.dumpBinarioPD(), configurazionePdDManager.dumpBinarioPA(),
						OpenSPCoop2Logger.loggerTracciamentoAbilitato, OpenSPCoop2Logger.loggerDumpAbilitato,
						propertiesReader.getFileTraceGovWayState().toString(),
						ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST(), 
						(ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE() && ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR()),
						ErroriProperties.isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR(),
						Costanti.isTRANSACTION_ERROR_STATUS_ABILITATO(), Costanti.isTRANSACTION_ERROR_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE(),
						Costanti.isTRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS(), Costanti.isTRANSACTION_ERROR_INSTANCE_ID_ABILITATO(), Costanti.isTRANSACTION_ERROR_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE(),
						infoConfigSistema.getInformazioniDatabase(), infoConfigSistema.getInformazioniAltriDatabase(),
						infoConfigSistema.getInformazioniSSL(true,true,true,true),
						infoConfigSistema.getInformazioniCryptographyKeyLength(),
						infoConfigSistema.getInformazioniCharset(),
						infoConfigSistema.getInformazioniInternazionalizzazione(true),
						infoConfigSistema.getInformazioniTimeZone(true),
						infoConfigSistema.getInformazioniProprietaJava(true, true, false, ConfigurazioneSistema.isIncludePassword()),
						infoConfigSistema.getInformazioniProprietaJava(true, false, true, ConfigurazioneSistema.isIncludePassword()),
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
					TimerMonitoraggioRisorseThread.setSTATE( TimerState.ENABLED );
					OpenSPCoop2Startup.logStartupInfo("Inizializzo Timer per il Monitoraggio delle Risorse");
				}
			}catch(Exception e){
				msgDiag.logStartupError(e,"TimerMonitoraggioRisorse");
				return;
			}





			// Inizializzazione Timer per il check del Threshold
			try{
				List<String> tipiThreshold = propertiesReader.getRepositoryThresholdTypes();
				if(tipiThreshold!=null && !tipiThreshold.isEmpty()){
					OpenSPCoop2Startup.this.timerThreshold = new TimerThresholdThread();
					OpenSPCoop2Startup.this.timerThreshold.start();
					TimerThresholdThread.setSTATE( TimerState.ENABLED );
					OpenSPCoop2Startup.logStartupInfo("Inizializzo Timer per il Controllo dei Threshold sulle risorse");
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
							OpenSPCoop2Startup.logStartupInfo("Inizializzo EJB gestore riscontri ["+nomeJNDI+"]");
							jndi.lookup(nomeJNDI);
							gestoreBusteNonRiscontrate = true;
						}catch(Exception e){
							this.logError("Search EJB gestore riscontri non trovato: "+e.getMessage(),e);
							try {
								Utilities.sleep((RandomUtilities.getRandom()).nextInt(propertiesReader.getTimerEJBDeployCheckInterval())); // random da 0ms a TransactionManagerCheckInterval ms
							}catch(Exception eRandom){
								// ignore
							}
							continue;
						}
					}else{
						gestoreBusteNonRiscontrate = true;
					}

					//  check Timer Gestore Messaggi
					if(propertiesReader.isTimerGestoreMessaggiAbilitato()){
						try{
							String nomeJNDI = propertiesReader.getJNDITimerEJBName().get(TimerGestoreMessaggi.ID_MODULO);
							OpenSPCoop2Startup.logStartupInfo("Inizializzo EJB gestore messaggi ["+nomeJNDI+"]");
							jndi.lookup(nomeJNDI);
							gestoreMessaggi = true;
						}catch(Exception e){
							this.logError("Search EJB gestore messaggi non trovato: "+e.getMessage(),e);
							try{
								Utilities.sleep((RandomUtilities.getRandom()).nextInt(propertiesReader.getTimerEJBDeployCheckInterval())); // random da 0ms a TransactionManagerCheckInterval ms
							}catch(Exception eRandom){
								// ignore
							}
							continue;
						}
					}else{
						gestoreMessaggi = true;
					}

					//  check Timer Gestore Pulizia Messaggi Anomali
					if(propertiesReader.isTimerGestorePuliziaMessaggiAnomaliAbilitato()){
						try{
							String nomeJNDI = propertiesReader.getJNDITimerEJBName().get(TimerGestorePuliziaMessaggiAnomali.ID_MODULO);
							OpenSPCoop2Startup.logStartupInfo("Inizializzo EJB gestore pulizia messaggi anomali ["+nomeJNDI+"]");
							jndi.lookup(nomeJNDI);
							gestorePuliziaMessaggiAnomali = true;
						}catch(Exception e){
							this.logError("Search EJB pulizia messaggi anomali non trovato: "+e.getMessage(),e);
							try{
								Utilities.sleep((RandomUtilities.getRandom()).nextInt(propertiesReader.getTimerEJBDeployCheckInterval())); // random da 0ms a TransactionManagerCheckInterval ms
							}catch(Exception eRandom){
								// ignore
							}
							continue;
						}
					}else{
						gestorePuliziaMessaggiAnomali = true;
					}


					//  check Timer Gestore Repository
					if(propertiesReader.isTimerGestoreRepositoryBusteAbilitato()){
						try{
							String nomeJNDI = propertiesReader.getJNDITimerEJBName().get(TimerGestoreRepositoryBuste.ID_MODULO);
							OpenSPCoop2Startup.logStartupInfo("Inizializzo EJB gestore repository ["+nomeJNDI+"]");
							jndi.lookup(nomeJNDI);
							gestoreRepository = true;
						}catch(Exception e){
							this.logError("Search EJB gestore repository non trovato: "+e.getMessage(),e);
							try{
								Utilities.sleep((RandomUtilities.getRandom()).nextInt(propertiesReader.getTimerEJBDeployCheckInterval())); // random da 0ms a TransactionManagerCheckInterval ms
							}catch(Exception eRandom){
								// ignore
							}
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
								TimerGestoreBusteNonRiscontrateLib.setSTATE_ONEWAY( TimerState.ENABLED );
								TimerGestoreBusteNonRiscontrateLib.setSTATE_ASINCRONI( TimerState.ENABLED );
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
								TimerGestoreMessaggiLib.setSTATE_MESSAGGI_ELIMINATI( propertiesReader.isTimerGestoreMessaggiPuliziaMessaggiEliminatiAbilitata() ? TimerState.ENABLED : TimerState.DISABLED );
								TimerGestoreMessaggiLib.setSTATE_MESSAGGI_SCADUTI( propertiesReader.isTimerGestoreMessaggiPuliziaMessaggiScadutiAbilitata() ? TimerState.ENABLED : TimerState.DISABLED );
								TimerGestoreMessaggiLib.setSTATE_MESSAGGI_NON_GESTITI( propertiesReader.isTimerGestoreMessaggiPuliziaMessaggiNonGestitiAbilitata() ? TimerState.ENABLED : TimerState.DISABLED );
								TimerGestoreMessaggiLib.setSTATE_CORRELAZIONE_APPLICATIVA( propertiesReader.isTimerGestoreMessaggiPuliziaCorrelazioneApplicativaAbilitata() ? TimerState.ENABLED : TimerState.DISABLED );
								TimerGestoreMessaggiLib.setSTATE_VERIFICA_CONNESSIONI_ATTIVE( propertiesReader.isTimerGestoreMessaggiVerificaConnessioniAttive() ? TimerState.ENABLED : TimerState.DISABLED );
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
						TimerGestoreMessaggiLib.setSTATE_MESSAGGI_ELIMINATI( propertiesReader.isTimerGestoreMessaggiPuliziaMessaggiEliminatiAbilitata() ? TimerState.ENABLED : TimerState.DISABLED );
						TimerGestoreMessaggiLib.setSTATE_MESSAGGI_SCADUTI( propertiesReader.isTimerGestoreMessaggiPuliziaMessaggiScadutiAbilitata() ? TimerState.ENABLED : TimerState.DISABLED );
						TimerGestoreMessaggiLib.setSTATE_MESSAGGI_NON_GESTITI( propertiesReader.isTimerGestoreMessaggiPuliziaMessaggiNonGestitiAbilitata() ? TimerState.ENABLED : TimerState.DISABLED );
						TimerGestoreMessaggiLib.setSTATE_CORRELAZIONE_APPLICATIVA( propertiesReader.isTimerGestoreMessaggiPuliziaCorrelazioneApplicativaAbilitata() ? TimerState.ENABLED : TimerState.DISABLED );
						TimerGestoreMessaggiLib.setSTATE_VERIFICA_CONNESSIONI_ATTIVE( propertiesReader.isTimerGestoreMessaggiVerificaConnessioniAttive() ? TimerState.ENABLED : TimerState.DISABLED );
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
								TimerGestorePuliziaMessaggiAnomaliLib.setSTATE( TimerState.ENABLED );
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
						TimerGestorePuliziaMessaggiAnomaliLib.setSTATE( TimerState.ENABLED );
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
								TimerGestoreRepositoryBusteLib.setSTATE( propertiesReader.isTimerGestoreRepositoryBusteAbilitatoInitialState() ? TimerState.ENABLED : TimerState.DISABLED );
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
						TimerGestoreRepositoryBusteLib.setSTATE( propertiesReader.isTimerGestoreRepositoryBusteAbilitatoInitialState() ? TimerState.ENABLED : TimerState.DISABLED );
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
					OpenSPCoop2Startup.this.threadConsegnaContenutiApplicativiMap = new HashMap<String, TimerConsegnaContenutiApplicativiThread>();
					List<String> code = propertiesReader.getTimerConsegnaContenutiApplicativiCode();
					try{
						for (String coda : code) {
							ConfigurazioneCoda configurazioneCoda = propertiesReader.getTimerConsegnaContenutiApplicativiConfigurazioneCoda(coda);
							TimerConsegnaContenutiApplicativiThread timer = new TimerConsegnaContenutiApplicativiThread(configurazioneCoda);
							timer.start();
							OpenSPCoop2Startup.this.threadConsegnaContenutiApplicativiMap.put(coda, timer);
						}
						TimerConsegnaContenutiApplicativi.setSTATE( TimerState.ENABLED );
					}catch(Exception e){
						msgDiag.logStartupError(e,"Avvio timer (thread) '"+TimerConsegnaContenutiApplicativiThread.ID_MODULO+"'");
					}
					OpenSPCoop2Startup.threadConsegnaContenutiApplicativiRefMap = OpenSPCoop2Startup.this.threadConsegnaContenutiApplicativiMap;
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
						TimerStatisticheLib.setSTATE_STATISTICHE_ORARIE( TimerState.ENABLED );
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
						TimerStatisticheLib.setSTATE_STATISTICHE_GIORNALIERE( TimerState.ENABLED );
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
						TimerStatisticheLib.setSTATE_STATISTICHE_SETTIMANALI( TimerState.ENABLED );
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
						TimerStatisticheLib.setSTATE_STATISTICHE_MENSILI( TimerState.ENABLED );
					}catch(Exception e){
						msgDiag.logStartupError(e,"Avvio timer (thread) '"+idTimerStatMensili+"'");
					}
				}else{
					msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE);
					msgDiag.addKeyword(CostantiPdD.KEY_TIMER, idTimerStatMensili);
					msgDiag.logPersonalizzato("disabilitato");
					msgDiag.setPrefixMsgPersonalizzati(null);
				}
				
				// pdnd generazione tracciamento
				String idTimerPdndTracciamentoGenerazione = "Timer"+TipoIntervalloStatistico.PDND_GENERAZIONE_TRACCIAMENTO.getValue();
				if(ModIUtils.isTracingPDNDEnabledSafe() && propertiesReader.isStatistichePdndTracciamentoGenerazioneEnabled()) {
					try{
						OpenSPCoop2Startup.this.threadPdndTracciamentoGenerazione = 
								new TimerStatisticheThread(propertiesReader.getStatistichePdndTracciamentoGenerazioneTimerIntervalSeconds(), TipoIntervalloStatistico.PDND_GENERAZIONE_TRACCIAMENTO);
						OpenSPCoop2Startup.this.threadPdndTracciamentoGenerazione.start();
						TimerStatisticheLib.setSTATE_PDND_TRACCIAMENTO_GENERAZIONE(TimerState.ENABLED);
					}catch(Exception e){
						msgDiag.logStartupError(e,"Avvio timer (thread) '"+idTimerPdndTracciamentoGenerazione+"'");
					}
				}else{
					msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE);
					msgDiag.addKeyword(CostantiPdD.KEY_TIMER, idTimerPdndTracciamentoGenerazione);
					msgDiag.logPersonalizzato("disabilitato");
					msgDiag.setPrefixMsgPersonalizzati(null);
				}
				
				// pdnd generazione pubblicazione
				String idTimerPdndTracciamentoPubblicazione = "Timer"+TipoIntervalloStatistico.PDND_PUBBLICAZIONE_TRACCIAMENTO.getValue();
				if(ModIUtils.isTracingPDNDEnabledSafe() && propertiesReader.isStatistichePdndTracciamentoPubblicazioneEnabled()) {
					try{
						OpenSPCoop2Startup.this.threadPdndTracciamentoPubblicazione = 
								new TimerStatisticheThread(propertiesReader.getStatistichePdndTracciamentoPubblicazioneTimerIntervalSeconds(), TipoIntervalloStatistico.PDND_PUBBLICAZIONE_TRACCIAMENTO);
						OpenSPCoop2Startup.this.threadPdndTracciamentoPubblicazione.start();
						TimerStatisticheLib.setSTATE_PDND_TRACCIAMENTO_PUBBLICAZIONE(TimerState.ENABLED);
					}catch(Exception e){
						msgDiag.logStartupError(e,"Avvio timer (thread) '"+idTimerPdndTracciamentoPubblicazione+"'");
					}
				}else{
					msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_STATISTICHE);
					msgDiag.addKeyword(CostantiPdD.KEY_TIMER, idTimerPdndTracciamentoPubblicazione);
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
			
			
			
		
			
			/* ------------ Avvia il thread per la gestione delle chiavi PDND ------------ */
			if(protocolFactoryManager.existsProtocolFactory(CostantiLabel.MODIPA_PROTOCOL_NAME)) { // verifico che esista su PDND
				
				RemoteStoreProviderDriver.setKeyMaxLifeMinutes(propertiesReader.getGestoreChiaviPDNDkeysMaxLifeMinutes());
				OpenSPCoop2Startup.logStartupInfo("PDND Key max life minutes: "+RemoteStoreProviderDriver.getKeyMaxLifeMinutes());
				
				RemoteStoreClientInfoCache.setClientDetailsMaxLifeMinutes(propertiesReader.getGestoreChiaviPDNDclientInfoMaxLifeMinutes());
				OpenSPCoop2Startup.logStartupInfo("PDND ClientId details max life minutes: "+RemoteStoreClientInfoCache.getClientDetailsMaxLifeMinutes());
				
				RemoteStoreClientInfoCache.setClientDetailsCacheFallbackMaxLifeMinutes(propertiesReader.getGestoreChiaviPDNDclientInfoCacheFallbackMaxLifeMinutes());
				OpenSPCoop2Startup.logStartupInfo("PDND ClientId details max life minutes (cache fallback): "+RemoteStoreClientInfoCache.getClientDetailsCacheFallbackMaxLifeMinutes());
				
				
				List<PDNDConfig> listRemoteConfig = null;
				try {
					listRemoteConfig = PDNDConfigUtilities.getRemoteStoreConfig(propertiesReader);
				}catch(Exception e){
					String msgError = "Inizializzazione thread per la gestione delle chiavi PDND non riuscita: "+e.getMessage();
					msgDiag.logStartupError(e,msgError);
					return;
				}
				
				if(listRemoteConfig!=null && !listRemoteConfig.isEmpty() && propertiesReader.isGestoreChiaviPDNDEnabled()) {
					
					OpenSPCoop2Startup.this.threadGestoreChiaviPDNDEnabled = true;
					
					try{
						OpenSPCoop2Startup.this.threadGestoreChiaviPDND = 
								new TimerGestoreChiaviPDND(propertiesReader.getGestoreChiaviPDNDeventsKeysTimerIntervalloSecondi(), listRemoteConfig);
						OpenSPCoop2Startup.this.threadGestoreChiaviPDND.start();
						TimerGestoreChiaviPDNDLib.setState( TimerState.ENABLED );
					}catch(Exception e){
						msgDiag.logStartupError(e,"Avvio timer (thread) '"+TimerGestoreChiaviPDND.ID_MODULO+"'");
					}
					
					try{
						OpenSPCoop2Startup.this.threadGestoreCacheChiaviPDND = 
								new TimerGestoreCacheChiaviPDND(propertiesReader.getGestoreChiaviPDNDcacheKeysTimerIntervalloSecondi(), listRemoteConfig);
						OpenSPCoop2Startup.this.threadGestoreCacheChiaviPDND.start();
						TimerGestoreCacheChiaviPDNDLib.setState( TimerState.ENABLED );
					}catch(Exception e){
						msgDiag.logStartupError(e,"Avvio timer (thread) '"+TimerGestoreCacheChiaviPDND.ID_MODULO+"'");
					}
				}
				else {
					msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_CHIAVI_PDND);
					
					msgDiag.addKeyword(CostantiPdD.KEY_TIMER, TimerGestoreChiaviPDND.ID_MODULO);
					msgDiag.addKeyword(CostantiPdD.KEY_REMOTE_STORE, "-");
					msgDiag.logPersonalizzato("disabilitato");
					
					msgDiag.addKeyword(CostantiPdD.KEY_TIMER, TimerGestoreCacheChiaviPDND.ID_MODULO);
					msgDiag.addKeyword(CostantiPdD.KEY_REMOTE_STORE, "-");
					msgDiag.logPersonalizzato("disabilitato");
					
					msgDiag.setPrefixMsgPersonalizzati(null);
				}
				
			}
			
			
			
			
			
			
			/* ------------ Avvia il thread per la gestione delle operazioni remote in un cluster dinamico ------------ */
			
			if(propertiesReader.isProxyReadJMXResourcesEnabled() && propertiesReader.isProxyReadJMXResourcesAsyncProcessByTimer()) {
				
				OpenSPCoop2Startup.this.threadGestoreOperazioniRemoteEnabled = true;
				
				try{
					OpenSPCoop2Startup.this.threadGestoreOperazioniRemote = 
							new TimerGestoreOperazioniRemote(propertiesReader.getProxyReadJMXResourcesAsyncProcessByTimerCheckInterval());
					OpenSPCoop2Startup.this.threadGestoreOperazioniRemote.start();
					TimerGestoreOperazioniRemoteLib.setState( TimerState.ENABLED );
				}catch(Exception e){
					msgDiag.logStartupError(e,"Avvio timer (thread) '"+TimerGestoreOperazioniRemote.ID_MODULO+"'");
				}
				
				try{
					OpenSPCoop2Startup.this.threadSvecchiamentoOperazioniRemote = 
							new TimerSvecchiamentoOperazioniRemote(propertiesReader.getProxyReadJMXResourcesAsyncProcessByTimerCheckOldRecordInterval());
					OpenSPCoop2Startup.this.threadSvecchiamentoOperazioniRemote.start();
					TimerSvecchiamentoOperazioniRemoteLib.setState( TimerState.ENABLED );
				}catch(Exception e){
					msgDiag.logStartupError(e,"Avvio timer (thread) '"+TimerSvecchiamentoOperazioniRemote.ID_MODULO+"'");
				}
			}
			else {
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_OPERAZIONI_ASINCRONE);
				
				msgDiag.addKeyword(CostantiPdD.KEY_TIMER, TimerGestoreOperazioniRemote.ID_MODULO);
				msgDiag.logPersonalizzato("disabilitato");
				
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_SVECCHIAMENTO_OPERAZIONI_ASINCRONE);
				
				msgDiag.addKeyword(CostantiPdD.KEY_TIMER, TimerSvecchiamentoOperazioniRemote.ID_MODULO);
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
					TimerRepositoryStatefulThread.setSTATE( TimerState.ENABLED );
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
					TimerFileSystemRecoveryThread.setSTATE( TimerState.ENABLED );
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
							TimerEventiThread.setSTATE( TimerState.ENABLED );
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
			
			/* ------------ Evento start per ControlloTraffico ------------ */
			try{
				if(propertiesReader.isControlloTrafficoEnabled() && propertiesReader.isAllarmiEnabled()) {
					
					ConfigurazioneGatewayControlloTraffico config = propertiesReader.getConfigurazioneControlloTraffico();
					if(config.isNotifierEnabled()) {
					
						INotify notifier = config.getNotifier();
						if(notifier.isNotifichePassiveAttive()) {
							
							boolean debug = propertiesReader.isAllarmiDebug();
							notifier.notificaGatewayRiavviato(OpenSPCoop2Logger.getLoggerOpenSPCoopAllarmi(debug), debug);
							
						}
					}
				}
			}catch(Exception e){
				String msgError = "Segnalazione gateway ripartito al controllo del traffico non riuscita: "+e.getMessage();
				forceLogEventi.error(msgError,e);
				msgDiag.logStartupError(e,"Segnalazione GovWayStarted ControlloTraffico");
				return;
			}
					
			
			
			
			
			
			/*----------- Inizializzazione Thread per Configurazione Cluster Dinamica --------------*/
			try{
				boolean rateLimitingGestioneCluster = false;
				if(propertiesReader.isControlloTrafficoEnabled()) {
					rateLimitingGestioneCluster = GestorePolicyAttive.isAttivo(PolicyGroupByActiveThreadsType.LOCAL_DIVIDED_BY_NODES);
				}
				if(propertiesReader.isClusterDinamico() || rateLimitingGestioneCluster) {	
					startTimerClusterDinamicoThread();
				}
			}catch(Exception e){
				this.logError("Riscontrato errore durante l'inizializzazione del thread che aggiorna il cluster dinamico: "+e.getMessage(),e);
				return;
			}
			
			
			
			
			/*----------- Buffer UUID --------------*/
			try{
				if(UniqueIdentifierManager.isBufferSupported() && propertiesReader.getIDManagerBufferSize()>0) {
					UniversallyUniqueIdentifierProducer.initialize(propertiesReader.getIDManagerBufferSize(), OpenSPCoop2Logger.getLoggerOpenSPCoopTimers());
					OpenSPCoop2Startup.this.universallyUniqueIdentifierProducer = UniversallyUniqueIdentifierProducer.getInstance();
					OpenSPCoop2Startup.this.universallyUniqueIdentifierProducer.start();
					OpenSPCoop2Startup.logStartupInfo("Thread per la produzione di un buffer di uuid avviato correttamente");
				}
			}catch(Exception e){
				this.logError("Riscontrato errore durante l'inizializzazione del thread che produce e mantiene in un buffer gli uuid: "+e.getMessage(),e);
				return;
			}
			
			
			
			
			/* ----------- Inizializzazione Cache Cleaner ------------ */
			try{
				GestoreCacheCleaner.initialize();
			}catch(Exception e){
				this.logError("Riscontrato errore durante l'inizializzazione del ripulitore delle cache: "+e.getMessage(),e);
				return;
			}
			
			
			




			/* ------------ OpenSPCoop startup terminato  ------------ */
			long endDate = System.currentTimeMillis();
			long secondStarter = (endDate - OpenSPCoop2Startup.this.startDate) / 1000;
			msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_OPENSPCOOP_STARTUP);
			msgDiag.addKeyword(CostantiPdD.KEY_VERSIONE_PORTA, propertiesReader.getPddDetailsForLog());
			msgDiag.addKeyword(CostantiPdD.KEY_TEMPO_AVVIO, secondStarter+" secondi");
			OpenSPCoop2Startup.logStartupInfo(propertiesReader.getPddDetailsForLog()+" avviata correttamente in "+secondStarter+" secondi.");
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
	
	private static void setSystemProperties(String propName, String propValue) {
		System.setProperty(propName, propValue);
		OpenSPCoop2Startup.logStartupInfo("Hazelcast - '"+propName+"': "+System.getProperty(propName));
	}
	
	public static void startTimerClusterDinamicoThread() throws Exception {
		if(OpenSPCoop2Startup.threadClusterDinamico == null) {
			initTimerClusterDinamicoThread();
		}
	}
	private static synchronized void initTimerClusterDinamicoThread() throws Exception {
		if(OpenSPCoop2Startup.threadClusterDinamico == null) {
			Logger forceLogEventi = OpenSPCoop2Logger.getLoggerOpenSPCoopEventi(true);
			OpenSPCoop2Startup.threadClusterDinamico = new TimerClusterDinamicoThread(OpenSPCoop2Logger.getLoggerOpenSPCoopTimers());
			OpenSPCoop2Startup.threadClusterDinamico.start();
			TimerClusterDinamicoThread.setSTATE( TimerState.ENABLED );
			forceLogEventi.info("Thread per l'aggiornamento del cluster avviato correttamente");
		}
	}
	
	public static boolean isStartedTimerClusteredRateLimitingLocalCache() {
		return OpenSPCoop2Startup.timerClusteredRateLimitingLocalCache!=null;
	}
	public static void startTimerClusteredRateLimitingLocalCache(GestorePolicyAttiveInMemory gestore) throws Exception {
		if(OpenSPCoop2Startup.timerClusteredRateLimitingLocalCache == null) {
			initTimerClusteredRateLimitingLocalCache(gestore);
		}
	}
	private static synchronized void initTimerClusteredRateLimitingLocalCache(GestorePolicyAttiveInMemory gestore) throws Exception {
		if(OpenSPCoop2Startup.timerClusteredRateLimitingLocalCache == null) {
			OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
			Logger logControlloTraffico = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(true);//properties.isControlloTrafficoDebug());
			OpenSPCoop2Startup.timerClusteredRateLimitingLocalCache = new TimerClusteredRateLimitingLocalCache(OpenSPCoop2Logger.getLoggerOpenSPCoopTimers(), gestore);
			OpenSPCoop2Startup.timerClusteredRateLimitingLocalCache.setTimeout(properties.getControlloTrafficoGestorePolicyInMemoryHazelcastLocalCacheTimerUpdate());
			OpenSPCoop2Startup.timerClusteredRateLimitingLocalCache.start();
			logControlloTraffico.info("Thread per l'aggiornamento della LocalCache tramite Hazelcast avviato correttamente");
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

		OpenSPCoop2Startup.setContextDestroyed(true);
		
		OpenSPCoop2Properties properties = null;
		try {
			properties = OpenSPCoop2Properties.getInstance();
		}catch(Throwable e){
			// ignore
		}
		
		
		// Connettori 
		try{
			ConnectorApplicativeThreadPool.shutdown();				
		}catch(Throwable e){
			// ignore
		}
		try{
			ConnettoreHTTPCOREConnectionManager.stop();
		}catch(Throwable e){
			// ignore
		}
		
		
		// ID Cluster
		try{
			if(OpenSPCoop2Startup.threadClusterDinamico!=null){
				OpenSPCoop2Startup.threadClusterDinamico.setStop(true);
			}
		}catch(Throwable e){
			// ignore
		}
		try{
			boolean rateLimitingGestioneCluster = false;
			if(properties!=null && properties.isControlloTrafficoEnabled()) {
				rateLimitingGestioneCluster = GestorePolicyAttive.isAttivo(PolicyGroupByActiveThreadsType.LOCAL_DIVIDED_BY_NODES);
			}
			if( (properties!=null && properties.isClusterDinamico()) || rateLimitingGestioneCluster) {	
				DynamicClusterManager.getInstance().unregister(OpenSPCoop2Startup.log);
			}
		}catch(Throwable e){
			// ignore
		}
		
		
		// Eventi
		try{
			if(properties!=null) {
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
							throw new CoreException("Thread per gli Eventi non trovato");
						}
					}catch(Exception e){
						if(logEventi!=null){
							if(debugEventi)
								logEventi.error("Errore durante la gestione dell'exit (ThreadEventi): "+e.getMessage(),e);
						}
					}
				}
			}
		}catch(Throwable e){
			// ignore
		}
		
		// Recovery FileSystem
		try {
			if(properties!=null && properties.isFileSystemRecoveryTimerEnabled()){
				boolean debugRecoveryFileSystem = properties.isFileSystemRecoveryDebug();
				Logger logRecoveryFileSystem = OpenSPCoop2Logger.getLoggerOpenSPCoopFileSystemRecovery(debugRecoveryFileSystem);
				if(debugRecoveryFileSystem)
					logRecoveryFileSystem.debug("Recupero thread per il recovery da file system ...");
				if(OpenSPCoop2Startup.this.threadFileSystemRecovery!=null){
					OpenSPCoop2Startup.this.threadFileSystemRecovery.setStop(true);
					if(debugRecoveryFileSystem)
						logRecoveryFileSystem.debug("Richiesto stop al thread per il recovery da file system");
				}else{
					throw new CoreException("Thread per il recovery da file system non trovato");
				}	
			}
		}catch(Throwable e){
			// ignore
		}
		
		// GestoreTransazioniStateful
		try {
			if(properties!=null && properties.isTransazioniStatefulEnabled()){
				boolean debugTransazioniStateful = properties.isTransazioniStatefulDebug();
				Logger logTransazioniStateful = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniStateful(debugTransazioniStateful);
				if(debugTransazioniStateful)
					logTransazioniStateful.debug("Recupero thread per la gestione delle transazioni stateful ...");
				if(OpenSPCoop2Startup.this.threadRepositoryStateful!=null){
					OpenSPCoop2Startup.this.threadRepositoryStateful.setStop(true);
					if(debugTransazioniStateful)
						logTransazioniStateful.debug("Richiesto stop al thread per la gestione delle transazioni stateful");
				}else{
					throw new CoreException("Thread per la gestione delle transazioni stateful non trovato");
				}	
			}
		}catch(Throwable e){
			// ignore
		}

		// Statistiche
		try{
			if(properties!=null && properties.isStatisticheGenerazioneEnabled()){
				boolean debugStatistiche = properties.isStatisticheGenerazioneDebug();
				
				Logger logStatisticheOrarie = OpenSPCoop2Logger.getLoggerOpenSPCoopStatistiche(TipoIntervalloStatistico.STATISTICHE_ORARIE, debugStatistiche);
				if(debugStatistiche)
					logStatisticheOrarie.debug("Recupero thread per la generazione delle statistiche orarie ...");
				if(OpenSPCoop2Startup.this.threadGenerazioneStatisticheOrarie!=null){
					OpenSPCoop2Startup.this.threadGenerazioneStatisticheOrarie.setStop(true);
					if(debugStatistiche)
						logStatisticheOrarie.debug("Richiesto stop al thread per la generazione delle statistiche orarie");
				}else{
					if(properties.isStatisticheGenerazioneBaseOrariaEnabled()) {
						throw new CoreException("Thread per la generazione delle statistiche orarie non trovato");
					}
				}	
				
				Logger logStatisticheGiornaliere = OpenSPCoop2Logger.getLoggerOpenSPCoopStatistiche(TipoIntervalloStatistico.STATISTICHE_GIORNALIERE, debugStatistiche);
				if(debugStatistiche)
					logStatisticheGiornaliere.debug("Recupero thread per la generazione delle statistiche giornaliere ...");
				if(OpenSPCoop2Startup.this.threadGenerazioneStatisticheGiornaliere!=null){
					OpenSPCoop2Startup.this.threadGenerazioneStatisticheGiornaliere.setStop(true);
					if(debugStatistiche)
						logStatisticheGiornaliere.debug("Richiesto stop al thread per la generazione delle statistiche giornaliere");
				}else{
					if(properties.isStatisticheGenerazioneBaseGiornalieraEnabled()) {
						throw new CoreException("Thread per la generazione delle statistiche giornaliere non trovato");
					}
				}
				
				Logger logStatisticheSettimanali = OpenSPCoop2Logger.getLoggerOpenSPCoopStatistiche(TipoIntervalloStatistico.STATISTICHE_SETTIMANALI, debugStatistiche);
				if(debugStatistiche)
					logStatisticheSettimanali.debug("Recupero thread per la generazione delle statistiche settimanali ...");
				if(OpenSPCoop2Startup.this.threadGenerazioneStatisticheSettimanali!=null){
					OpenSPCoop2Startup.this.threadGenerazioneStatisticheSettimanali.setStop(true);
					if(debugStatistiche)
						logStatisticheSettimanali.debug("Richiesto stop al thread per la generazione delle statistiche settimanali");
				}else{
					if(properties.isStatisticheGenerazioneBaseSettimanaleEnabled()) {
						throw new CoreException("Thread per la generazione delle statistiche settimanali non trovato");
					}
				}
				
				Logger logStatisticheMensili = OpenSPCoop2Logger.getLoggerOpenSPCoopStatistiche(TipoIntervalloStatistico.STATISTICHE_MENSILI, debugStatistiche);
				if(debugStatistiche)
					logStatisticheMensili.debug("Recupero thread per la generazione delle statistiche mensili ...");
				if(OpenSPCoop2Startup.this.threadGenerazioneStatisticheMensili!=null){
					OpenSPCoop2Startup.this.threadGenerazioneStatisticheMensili.setStop(true);
					if(debugStatistiche)
						logStatisticheMensili.debug("Richiesto stop al thread per la generazione delle statistiche mensili");
				}else{
					if(properties.isStatisticheGenerazioneBaseMensileEnabled()) {
						throw new CoreException("Thread per la generazione delle statistiche mensili non trovato");
					}
				}
				
				Logger logPdndTracciamentoGenerazione = OpenSPCoop2Logger.getLoggerOpenSPCoopStatistiche(TipoIntervalloStatistico.PDND_GENERAZIONE_TRACCIAMENTO, debugStatistiche);
				if(debugStatistiche)
					logPdndTracciamentoGenerazione.debug("Recupero thread per la generazione dei tracciamenti PDND ...");
				if(OpenSPCoop2Startup.this.threadPdndTracciamentoGenerazione!=null){
					OpenSPCoop2Startup.this.threadPdndTracciamentoGenerazione.setStop(true);
					if(debugStatistiche)
						logPdndTracciamentoGenerazione.debug("Richiesto stop al thread per la generazione dei tracciamenti PDND");
				}else{
					if(properties.isStatistichePdndTracciamentoGenerazioneEnabled()) {
						throw new CoreException("Thread per la generazione dei tracciamenti PDND non trovato");
					}
				}
				
				Logger logPdndTracciamentoPubblicazione = OpenSPCoop2Logger.getLoggerOpenSPCoopStatistiche(TipoIntervalloStatistico.PDND_PUBBLICAZIONE_TRACCIAMENTO, debugStatistiche);
				if(debugStatistiche)
					logPdndTracciamentoPubblicazione.debug("Recupero thread per la pubblicazione dei tracciamenti PDND ...");
				if(OpenSPCoop2Startup.this.threadPdndTracciamentoPubblicazione!=null){
					OpenSPCoop2Startup.this.threadPdndTracciamentoPubblicazione.setStop(true);
					if(debugStatistiche)
						logPdndTracciamentoPubblicazione.debug("Richiesto stop al thread per la pubblicazione dei tracciamenti PDND");
				}else{
					if(properties.isStatistichePdndTracciamentoPubblicazioneEnabled()) {
						throw new CoreException("Thread per la pubblicazione dei tracciamenti PDND non trovato");
					}
				}
				
			}
		}catch (Throwable e) {
			// ignore
		}
		
		// Timer per la gestione delle chiavi da PDND 
		
		try{
			if(properties!=null && OpenSPCoop2Startup.this.threadGestoreChiaviPDNDEnabled){
				boolean debug = properties.isGestoreChiaviPDNDDebug();
			
				Logger logGestorePDND = OpenSPCoop2Logger.getLoggerOpenSPCoopGestoreChiaviPDND(debug);
				if(debug)
					logGestorePDND.debug("Recupero thread per la gestione delle chiavi PDND ...");
				if(OpenSPCoop2Startup.this.threadGestoreChiaviPDND!=null){
					OpenSPCoop2Startup.this.threadGestoreChiaviPDND.setStop(true);
					if(debug)
						logGestorePDND.debug("Richiesto stop al thread per la gestione delle chiavi PDND");
				}else{
					throw new CoreException("Thread per la gestione delle chiavi PDND non trovato");
				}	
			}
		}catch (Throwable e) {
			// ignore
		}
		try{
			if(properties!=null && OpenSPCoop2Startup.this.threadGestoreChiaviPDNDEnabled){
				boolean debug = properties.isGestoreChiaviPDNDDebug();
			
				Logger logGestorePDND = OpenSPCoop2Logger.getLoggerOpenSPCoopGestoreChiaviPDND(debug);
				if(debug)
					logGestorePDND.debug("Recupero thread per la gestione della cache delle chiavi PDND ...");
				if(OpenSPCoop2Startup.this.threadGestoreCacheChiaviPDND!=null){
					OpenSPCoop2Startup.this.threadGestoreCacheChiaviPDND.setStop(true);
					if(debug)
						logGestorePDND.debug("Richiesto stop al thread per la gestione della cache delle chiavi PDND");
				}else{
					throw new CoreException("Thread per gestione della cache delle chiavi PDND non trovato");
				}	
			}
		}catch (Throwable e) {
			// ignore
		}
		
		// Timer per la gestione delle operazioni remote in un cluster dinamico
		
		try{
			if(properties!=null && OpenSPCoop2Startup.this.threadGestoreOperazioniRemoteEnabled){
				boolean debug = properties.isProxyReadJMXResourcesAsyncProcessByTimerDebug();
			
				Logger logGestoreOperazioniRemote = OpenSPCoop2Logger.getLoggerOpenSPCoopGestoreOperazioniRemote(debug);
				if(debug)
					logGestoreOperazioniRemote.debug("Recupero thread per la gestione delle operazioni remote ...");
				if(OpenSPCoop2Startup.this.threadGestoreOperazioniRemote!=null){
					OpenSPCoop2Startup.this.threadGestoreOperazioniRemote.setStop(true);
					if(debug)
						logGestoreOperazioniRemote.debug("Richiesto stop al thread per la gestione delle operazioni remote");
				}else{
					throw new CoreException("Thread per la gestione delle operazioni remote non trovato");
				}	
			}
		}catch (Throwable e) {
			// ignore
		}
		try{
			if(properties!=null && OpenSPCoop2Startup.this.threadGestoreOperazioniRemoteEnabled){
				boolean debug = properties.isProxyReadJMXResourcesAsyncProcessByTimerDebug();
			
				Logger logGestoreOperazioniRemote = OpenSPCoop2Logger.getLoggerOpenSPCoopGestoreOperazioniRemote(debug);
				if(debug)
					logGestoreOperazioniRemote.debug("Recupero thread per lo svecchiamento delle operazioni remote ...");
				if(OpenSPCoop2Startup.this.threadSvecchiamentoOperazioniRemote!=null){
					OpenSPCoop2Startup.this.threadSvecchiamentoOperazioniRemote.setStop(true);
					if(debug)
						logGestoreOperazioniRemote.debug("Richiesto stop al thread per lo svecchiamento delle operazioni remote");
				}else{
					throw new CoreException("Thread per lo svecchiamento delle operazioni remote non trovato");
				}	
			}
		}catch (Throwable e) {
			// ignore
		}
		
		// ExitHandler
		try{
			ExitContext context = new ExitContext();
			context.setPddContext(this.pddContext);
			context.setLogConsole(OpenSPCoop2Startup.log);
			context.setLogCore(OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			GestoreHandlers.exit(context);
		}catch(Throwable e){
			// ignore
		}
		
		// Gestione Stato ControlloTraffico
		if(properties!=null && properties.isControlloTrafficoEnabled()){
			OutputStream out = null;
			Logger logControlloTraffico = null;
			List<PolicyGroupByActiveThreadsType> tipiGestorePolicyRateLimiting = null;
			try{
				logControlloTraffico = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(properties.isControlloTrafficoDebug());
				tipiGestorePolicyRateLimiting = GestorePolicyAttive.getTipiGestoriAttivi();
			}catch(Throwable e){
				if(logControlloTraffico!=null){
					logControlloTraffico.error("Errore durante la terminazione dei gestori delle policy di Rate Limiting: "+e.getMessage(),e);
				}
			}
			if(tipiGestorePolicyRateLimiting!=null && !tipiGestorePolicyRateLimiting.isEmpty()) {
				for (PolicyGroupByActiveThreadsType type : tipiGestorePolicyRateLimiting) {
					try{
						File fRepository = properties.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository();
						if(fRepository!=null){
							if(fRepository.exists()==false){
								throw new CoreException("Directory ["+fRepository.getAbsolutePath()+"] not exists");
							}
							if(fRepository.isDirectory()==false){
								throw new CoreException("File ["+fRepository.getAbsolutePath()+"] is not directory");
							}
							if(fRepository.canRead()==false){
								throw new CoreException("File ["+fRepository.getAbsolutePath()+"] cannot read");
							}
							if(fRepository.canWrite()==false){
								throw new CoreException("File ["+fRepository.getAbsolutePath()+"] cannot write");
							}		
							
							File fDati = new File(fRepository, GestorePolicyAttive.getControlloTrafficoImage(type));
							out = new FileOutputStream(fDati, false); // se già esiste lo sovrascrive
							GestorePolicyAttive.getInstance(type).serialize(out);
							out.flush();
							out.close();
							out = null;
							
							boolean inizializzazioneAttiva = false;
							// Il meccanismo di ripristino dell'immagine degli eventi non sembra funzionare
							// Lascio comunque il codice se in futuro si desidera approfindire la questione
							if(inizializzazioneAttiva) {
								fDati = new File(fRepository, GestorePolicyAttive.getControlloTrafficoEventiImage(type));
								NotificatoreEventi.getInstance().serialize(fDati);
							}
							
						}
					}catch(Throwable e){
						if(logControlloTraffico!=null){
							logControlloTraffico.error("Errore durante la terminazione dei gestori delle policy di Rate Limiting: "+e.getMessage(),e);
						}
					}finally{
						try{
							if(out!=null){
								out.flush();
							}
						}catch(Exception eClose){
							// ignore
						}
						try{
							if(out!=null){
								out.close();
							}
						}catch(Exception eClose){
							// ignore
						}
					}
				}
			}
			
			if (OpenSPCoop2Startup.timerClusteredRateLimitingLocalCache != null) {
				OpenSPCoop2Startup.timerClusteredRateLimitingLocalCache.setStop(true);		
			}
		}
		
		// Fermo timer runtime
		if(this.serverJ2EE){
			try {
				if(this.timerRiscontri!=null)
					this.timerRiscontri.stop();
			} catch (Throwable e) {
				// ignore
			}
			try {
				if(this.timerEliminazioneMsg!=null)
					this.timerEliminazioneMsg.stop();
			} catch (Throwable e) {
				// ignore
			}
			try {
				if(this.timerPuliziaMsgAnomali!=null)
					this.timerPuliziaMsgAnomali.stop();
			} catch (Throwable e) {
				// ignore
			}
			try {
				if(this.timerRepositoryBuste!=null)
					this.timerRepositoryBuste.stop();
			} catch (Throwable e) {
				// ignore
			}
		}else{
			try{
				if(this.threadEliminazioneMsg!=null) {
					this.threadEliminazioneMsg.setStop(true);
				}
			}catch (Throwable e) {
				// ignore
			}
			try{
				if(this.threadPuliziaMsgAnomali!=null)
					this.threadPuliziaMsgAnomali.setStop(true);
			}catch (Throwable e) {
				// ignore
			}
			try{
				if(this.threadRepositoryBuste!=null)
					this.threadRepositoryBuste.setStop(true);
			}catch (Throwable e) {
				// ignore
			}
		}
		try{
			if(this.threadConsegnaContenutiApplicativiMap!=null && !this.threadConsegnaContenutiApplicativiMap.isEmpty()) {
				for (String coda : this.threadConsegnaContenutiApplicativiMap.keySet()) {
					TimerConsegnaContenutiApplicativiThread timer = this.threadConsegnaContenutiApplicativiMap.get(coda);
					timer.setStop(true);
				}
			}
		}catch (Throwable e) {
			// ignore
		}

		// fermo timer Monitoraggio Risorse
		try{
			if(this.timerMonitoraggioRisorse!=null)
				this.timerMonitoraggioRisorse.setStop(true);
		}catch (Throwable e) {
			// ignore
		}

		// fermo timer Threshold
		try{
			if(this.timerThreshold!=null)
				this.timerThreshold.setStop(true);
		}catch (Throwable e) {
			// ignore
		}

		// Rilascio risorse JMX
		if(this.gestoreRisorseJMX!=null){
			this.gestoreRisorseJMX.unregisterMBeans();
		}

		// Verifico che i timer siano conclusi prima di rilasciare i lock
		try{
			if(this.threadEventi!=null)
				this.threadEventi.waitShutdown();
		}catch (Throwable e) {
			// ignore
		}
		try{
			if(this.threadFileSystemRecovery!=null)
				this.threadFileSystemRecovery.waitShutdown();
		}catch (Throwable e) {
			// ignore
		}
		try{
			if(this.threadRepositoryStateful!=null)
				this.threadRepositoryStateful.waitShutdown();
		}catch (Throwable e) {
			// ignore
		}
		try{
			if(this.threadGenerazioneStatisticheOrarie!=null)
				this.threadGenerazioneStatisticheOrarie.waitShutdown();
		}catch (Throwable e) {
			// ignore
		}
		try{
			if(this.threadGenerazioneStatisticheGiornaliere!=null)
				this.threadGenerazioneStatisticheGiornaliere.waitShutdown();
		}catch (Throwable e) {
			// ignore
		}
		try{
			if(this.threadGenerazioneStatisticheSettimanali!=null)
				this.threadGenerazioneStatisticheSettimanali.waitShutdown();
		}catch (Throwable e) {
			// ignore
		}
		try{
			if(this.threadGenerazioneStatisticheMensili!=null)
				this.threadGenerazioneStatisticheMensili.waitShutdown();
		}catch (Throwable e) {
			// ignore
		}
		if(this.serverJ2EE){ // TODO ATTESA ATTIVA CHE SI FERMINO PER J2EE
			Utilities.sleep(5000); // aspetto che i timer terminano la loro gestione.
		}
		else {
			try{
				if(this.threadEliminazioneMsg!=null) {
					this.threadEliminazioneMsg.waitShutdown();
				}
			}catch (Throwable e) {
				// ignore
			}
			try{
				if(this.threadPuliziaMsgAnomali!=null) {
					this.threadPuliziaMsgAnomali.waitShutdown();
				}
			}catch (Throwable e) {
				// ignore
			}
			try{
				if(this.threadRepositoryBuste!=null) {
					this.threadRepositoryBuste.waitShutdown();
				}
			}catch (Throwable e) {
				// ignore
			}
		}
		try{
			if(this.threadConsegnaContenutiApplicativiMap!=null && !this.threadConsegnaContenutiApplicativiMap.isEmpty()) {
				for (String coda : this.threadConsegnaContenutiApplicativiMap.keySet()) {
					TimerConsegnaContenutiApplicativiThread timer = this.threadConsegnaContenutiApplicativiMap.get(coda);
					timer.waitShutdown();
				}
			}
		}catch (Throwable e) {
			// ignore
		}
		try{
			if(this.timerMonitoraggioRisorse!=null) {
				this.timerMonitoraggioRisorse.waitShutdown();
			}
		}catch (Throwable e) {
			// ignore
		}
		try{
			if(this.timerThreshold!=null) {
				this.timerThreshold.waitShutdown();
			}
		}catch (Throwable e) {
			// ignore
		}
		try{
			if(OpenSPCoop2Startup.threadClusterDinamico!=null)
				OpenSPCoop2Startup.threadClusterDinamico.waitShutdown();
		}catch (Throwable e) {
			// ignore
		}
		try{
			if(OpenSPCoop2Startup.timerClusteredRateLimitingLocalCache!=null)
				OpenSPCoop2Startup.timerClusteredRateLimitingLocalCache.waitShutdown();
		}catch (Throwable e) {
			// ignore
		}
		
		// Rilascio lock (da fare dopo che i timer sono stati fermati)
		// L'errore puo' avvenire poiche' lo shutdown puo' anche disattivare il datasource
		boolean logErrorConnection = false;
		TimerUtils.relaseLockTimers(properties, ID_MODULO, OpenSPCoop2Logger.getLoggerOpenSPCoopTimers(), logErrorConnection);
		
		// UniversallyUniqueIdentifierProducer (fermo dopo lo stop di tutte le altre attivita)
		try{
			if(OpenSPCoop2Startup.this.universallyUniqueIdentifierProducer!=null){
				OpenSPCoop2Startup.this.universallyUniqueIdentifierProducer.setStop(true);
				OpenSPCoop2Startup.this.universallyUniqueIdentifierProducer.waitShutdown();
			}
		}catch(Throwable e){
			// ignore
		}
		
		// DataManger
		DateManager.close();

		// *** Repository plugins ***
		try{
			CorePluginLoader.close(OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
		}catch(Throwable e){
			// ignore
		}
		
		// *** Hazelcast ***
		if(properties!=null && properties.isControlloTrafficoEnabled()){
			HazelcastManager.close();
		}
		
		// *** Semaphore **
		try{
			org.openspcoop2.utils.SemaphoreLock.releaseScheduledExecutorService();
		}catch(Throwable e){
			// ignore
		}
		
		// Attendo qualche secondo
		Utilities.sleep(2000);
	}



}
