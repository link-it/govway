/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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



package org.openspcoop2.pdd.services;

import java.rmi.RemoteException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import org.slf4j.Logger;
import org.jminix.console.tool.StandaloneMiniConsole;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageFactory_impl;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.GeneralInstanceProperties;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.PddProperties;
import org.openspcoop2.pdd.config.QueueManager;
import org.openspcoop2.pdd.config.SystemPropertiesManager;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.StatoServiziPdD;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;
import org.openspcoop2.pdd.core.autorizzazione.GestoreAutorizzazione;
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
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.core.RicezioneBuste;
import org.openspcoop2.pdd.services.core.RicezioneContenutiApplicativi;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.services.skeleton.IntegrationManager;
import org.openspcoop2.pdd.timers.TimerConsegnaContenutiApplicativiThread;
import org.openspcoop2.pdd.timers.TimerGestoreBusteNonRiscontrate;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggiThread;
import org.openspcoop2.pdd.timers.TimerGestorePuliziaMessaggiAnomali;
import org.openspcoop2.pdd.timers.TimerGestorePuliziaMessaggiAnomaliThread;
import org.openspcoop2.pdd.timers.TimerGestoreRepositoryBuste;
import org.openspcoop2.pdd.timers.TimerGestoreRepositoryBusteThread;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorse;
import org.openspcoop2.pdd.timers.TimerThreshold;
import org.openspcoop2.pdd.timers.TimerUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.driver.repository.IGestoreRepository;
import org.openspcoop2.protocol.manifest.constants.ServiceBinding;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.registry.RegistroServiziReader;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.message.engine.MessageSecurityFactory;
import org.openspcoop2.security.utils.ExternalPWCallback;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.resources.Loader;



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
	private static Logger log = LoggerWrapperFactory.getLogger("openspcoop2.startup");

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
	private TimerThreshold timerThreshold = null;

	/** Gestore Monitoraggio Risorse */
	private TimerMonitoraggioRisorse timerMonitoraggioRisorse = null;

	/** Timer per la gestione di riconsegna ContenutiApplicativi */
	private TimerConsegnaContenutiApplicativiThread threadConsegnaContenutiApplicativi;
	
	/** Gestore risorse JMX */
	private GestoreRisorseJMX gestoreRisorseJMX = null;
	public static GestoreRisorseJMX gestoreRisorseJMX_staticInstance = null;

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
			OpenSPCoop2Startup.log = LoggerWrapperFactory.getLogger("openspcoop2.startup");
			
			
			
			
			

			
			
			
			

			/* ------------- Inizializzo ClassNameProperties di OpenSPCoop --------------- */
			if( ClassNameProperties.initialize() == false){
				this.logError("Riscontrato errore durante l'inizializzazione del reader di 'openspcoop2.classRegistry.properties'");
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
				this.logError("Riscontrato errore durante l'inizializzazione del reader di 'openspcoop2.properties'");
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
			}else{
				if(propertiesReader.validaConfigurazione(null) == false){
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


			
			
			
			
			
			
			
			
			
			/*
			 * 	Necessario in jboss7 per evitare errore 'error constructing MAC: java.lang.SecurityException: JCE cannot authenticate the provider BC'
			 *  se vengono utilizzati keystore P12.
			 *  Il codice  
			 *  	<resource-root path="WEB-INF/lib/bcprov-ext-jdk15on-155.jar" use-physical-code-source="true"/>
			 *  all'interno del file jboss-deployment-structure.xml non è più sufficente da quanto è stato necessario
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
			 * - org/apache/xml/security/resource/xmlsecurity_en.properties (xmlsec-2.0.7.jar)
			 * - org/apache/xml/security/resource/xmlsecurity_de.properties (xmlsec-2.0.7.jar)
			 * - messages/wss4j_errors.properties (wss4j-ws-security-common-2.1.7.jar)
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
			
			
			
			
			
			
			
			
			



			/* ------------- Inizializzo il sistema di Logging di OpenSPCoop --------------- */
			boolean isInitializeLogger = false;
			isInitializeLogger = OpenSPCoop2Logger.initialize(OpenSPCoop2Startup.log,propertiesReader.getRootDirectory(),loggerP);
			if(isInitializeLogger == false){
				return;
			}
			Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			OpenSPCoop2Startup.log = LoggerWrapperFactory.getLogger("openspcoop2.startup");
			
			Utilities.log = logCore;
			Utilities.freeMemoryLog = propertiesReader.getFreeMemoryLog();
			
			OpenSPCoop2Startup.initializeLog = true;

			
			
			


			/* ------------- Pdd.properties --------------- */
			String locationPddProperties = null;
			if( propertiesReader.getLocationPddProperties()!=null ){
				locationPddProperties = propertiesReader.getLocationPddProperties();
			}
			if( PddProperties.initialize(locationPddProperties,propertiesReader.getRootDirectory()) == false){
				this.logError("Riscontrato errore durante l'inizializzazione del reader di 'openspcoop2.pdd.properties'");
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
				this.logError("Riscontrato errore durante l'inizializzazione del reader di 'openspcoop2.msgDiagnostici.properties'");
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
			
			
			
			


			
			
			
			/* ----------- Inizializzazione Risorse DOM/SOAP ------------ */
			try{
				// MessageFactory
				OpenSPCoop2MessageFactory.setMessageFactoryImpl(classNameReader.getOpenSPCoop2MessageFactory(propertiesReader.getOpenspcoop2MessageFactory()));
				OpenSPCoop2MessageFactory.initMessageFactory(true);
								
				// MessageSecurity
				MessageSecurityFactory.setMessageSecurityContextClassName(classNameReader.getMessageSecurityContext(propertiesReader.getMessageSecurityContext()));
				MessageSecurityFactory.setMessageSecurityDigestReaderClassName(classNameReader.getMessageSecurityDigestReader(propertiesReader.getMessageSecurityDigestReader()));
				GestoreKeystoreCache.setKeystoreCacheParameters(propertiesReader.isAbilitataCacheMessageSecurityKeystore(),
						propertiesReader.getItemLifeSecondCacheMessageSecurityKeystore(), propertiesReader.getDimensioneCacheMessageSecurityKeystore());
				
				// XML
				org.openspcoop2.message.xml.XMLUtils xmlUtils = org.openspcoop2.message.xml.XMLUtils.getInstance();
				// XML - XERCES
				xmlUtils.initDocumentBuilderFactory();
				xmlUtils.initDatatypeFactory();
//				xmlUtils.initSAXParserFactory();
//				xmlUtils.initXMLEventFactory();
				xmlUtils.initSchemaFactory();
				// XML - XALAN
				xmlUtils.initTransformerFactory();
				xmlUtils.initXPathFactory();
				// INIT - OTHER
				xmlUtils.initCalendarConverter();
				
				// SOAP
				OpenSPCoop2MessageFactory.getMessageFactory().getSoapFactory11();
				OpenSPCoop2MessageFactory.getMessageFactory().getSoapFactory12();
				OpenSPCoop2MessageFactory.getMessageFactory().getSoapMessageFactory();
				
				// Log
				OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getMessageFactory();
				// stampo comunque saaj factory
				OpenSPCoop2Startup.log.info("OpenSPCoop MessageFactory (open:"+OpenSPCoop2MessageFactory_impl.class.getName().equals(factory.getClass().getName())+"): "+factory.getClass().getName());
				if(propertiesReader.isPrintInfoFactory()){
					MessageType [] mt = MessageType.values();
					for (int i = 0; i < mt.length; i++) {
						OpenSPCoop2Startup.log.info("OpenSPCoop Message ["+mt[i].name()+"]: "+factory.createEmptyMessage(mt[i],MessageRole.NONE).getClass().getName());	
					}
					if( OpenSPCoop2MessageFactory.getMessageFactory().getSoapFactory11()!=null)
						OpenSPCoop2Startup.log.info("SOAP1.1 Factory: "+ OpenSPCoop2MessageFactory.getMessageFactory().getSoapFactory11().getClass().getName());
					else
						OpenSPCoop2Startup.log.info("SOAP1.1 Factory: not implemented");
					if(OpenSPCoop2MessageFactory.getMessageFactory().getSoapFactory12()!=null){
						OpenSPCoop2Startup.log.info("SOAP1.2 Factory: "+ OpenSPCoop2MessageFactory.getMessageFactory().getSoapFactory12().getClass().getName());
					}else{
						OpenSPCoop2Startup.log.info("SOAP1.2 Factory: not implemented");
					}
					OpenSPCoop2Startup.log.info("SOAP MessageFactory: "+OpenSPCoop2MessageFactory.getMessageFactory().getSoapMessageFactory().getClass().getName());
	
					// XML - XERCES
					OpenSPCoop2Startup.log.info("XERCES - DocumentFactory: "+xmlUtils.getDocumentBuilderFactory().getClass().getName());
					OpenSPCoop2Startup.log.info("XERCES - DatatypeFactory: "+xmlUtils.getDatatypeFactory().getClass().getName());
//					OpenSPCoop2Startup.log.info("XERCES - SAXParserFactory: "+xmlUtils.getSAXParserFactory().getClass().getName());
//					OpenSPCoop2Startup.log.info("XERCES - XMLEventFactory: "+xmlUtils.getXMLEventFactory().getClass().getName());
					OpenSPCoop2Startup.log.info("XERCES - SchemaFactory: "+xmlUtils.getSchemaFactory().getClass().getName());
					
					// XML - XALAN
					OpenSPCoop2Startup.log.info("XALAN - TransformerFactory: "+xmlUtils.getTransformerFactory().getClass().getName());
					OpenSPCoop2Startup.log.info("XALAN - XPathFactory: "+xmlUtils.getXPathFactory().getClass().getName());

				}
				if(propertiesReader.isPrintInfoMessageSecurity()){
					OpenSPCoop2Startup.log.info("MessageSecurity Context: "+MessageSecurityFactory.messageSecurityContextImplClass);
					OpenSPCoop2Startup.log.info("MessageSecurity DigestReader: "+MessageSecurityFactory.messageSecurityDigestReaderImplClass);
					OpenSPCoop2Startup.log.info("MessageSecurity Keystore Cache enabled["+propertiesReader.isAbilitataCacheMessageSecurityKeystore()+"] itemLifeSecond["+
							propertiesReader.getItemLifeSecondCacheMessageSecurityKeystore()+"] size["+
							propertiesReader.getDimensioneCacheMessageSecurityKeystore()+"]");
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
				
			} catch(Exception e) {
				this.logError("Inizializzazione Message/DOM/SOAP: "+e.getMessage(), e);
				return;
			}
			
			
			
			
			





			/*----------- Inizializzazione DateManager  --------------*/
			try{
				String tipoClass = classNameReader.getDateManager(propertiesReader.getTipoDateManager());
				DateManager.initializeDataManager(tipoClass, propertiesReader.getDateManagerProperties(),logCore);
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




			/*----------- Inizializzazione Generatore di ClusterID  --------------*/
			try{
				String tipoGeneratoreClusterID = propertiesReader.getTipoIDManager();
				String classClusterID = null;
				if(CostantiConfigurazione.NONE.equals(tipoGeneratoreClusterID)){
					String clusterID = propertiesReader.getClusterId(false);
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
					
					if(propertiesReader.generazioneDateCasualiLogAbilitato()){
						GeneratoreCasualeDate.init(OpenSPCoop2Properties.getGenerazioneDateCasualiLog_dataInizioIntervallo(), 
								OpenSPCoop2Properties.getGenerazioneDateCasualiLog_dataFineIntervallo(),
								OpenSPCoop2Startup.log);
						
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
					propertiesReader.isConfigurazioneCache_ConfigPrefill());
			if(isInitializeConfig == false){
				this.logError("Riscontrato errore durante l'inizializzazione della configurazione di OpenSPCoop.");
				return;
			}
			ConfigurazionePdDManager configurazionePdDReader = ConfigurazionePdDManager.getInstance();
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
			MsgDiagnostico msgDiag = new MsgDiagnostico(OpenSPCoop2Startup.ID_MODULO);


			
			
			
			
			
			
			
			
			/*----------- Inizializzazione SystemProperties --------------*/
			try{
				SystemPropertiesManager spm = new SystemPropertiesManager(configurazionePdDReader,logCore);
				spm.updateSystemProperties();
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione proprieta' di sistema");
				return;
			}
			
			
			
			
			
			
			/*----------- Inizializzazione Autorizzazione --------------*/
			try{
				AccessoDatiAutorizzazione datiAutorizzazione = configurazionePdDReader.getAccessoDatiAutorizzazione();
				if(datiAutorizzazione!=null && datiAutorizzazione.getCache()!=null){
					
					int dimensioneCache = -1;
					if(datiAutorizzazione.getCache().getDimensione()!=null){
						try{
							dimensioneCache = Integer.parseInt(datiAutorizzazione.getCache().getDimensione());
						}catch(Exception e){
							throw new Exception("Parametro 'dimensioneCache' errato per la cache di accesso di ai dati di autorizzazione");
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
							throw new Exception("Parametro 'idleTime' errato per la cache di accesso di ai dati di autorizzazione");
						}
					}
					
					long itemLifeSecond = -1;
					if(datiAutorizzazione.getCache().getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(datiAutorizzazione.getCache().getItemLifeSecond());
						}catch(Exception e){
							throw new Exception("Parametro 'itemLifeSecond' errato per la cache di accesso di ai dati di autorizzazione");
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
				AccessoDatiAutenticazione datiAutenticazione = configurazionePdDReader.getAccessoDatiAutenticazione();
				if(datiAutenticazione!=null && datiAutenticazione.getCache()!=null){
					
					int dimensioneCache = -1;
					if(datiAutenticazione.getCache().getDimensione()!=null){
						try{
							dimensioneCache = Integer.parseInt(datiAutenticazione.getCache().getDimensione());
						}catch(Exception e){
							throw new Exception("Parametro 'dimensioneCache' errato per la cache di accesso di ai dati di autenticazione");
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
							throw new Exception("Parametro 'idleTime' errato per la cache di accesso di ai dati di autenticazione");
						}
					}
					
					long itemLifeSecond = -1;
					if(datiAutenticazione.getCache().getItemLifeSecond()!=null){
						try{
							itemLifeSecond = Integer.parseInt(datiAutenticazione.getCache().getItemLifeSecond());
						}catch(Exception e){
							throw new Exception("Parametro 'itemLifeSecond' errato per la cache di accesso di ai dati di autenticazione");
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
				OpenSPCoop2Startup.log.info("ProtocolFactory default: "+protocolFactoryManager.getDefaultProtocolFactory().getProtocol());
			} catch(Exception e) {
				this.logError("Initialize ProtocolFactoryManager failed: "+e.getMessage());
				logCore.error("Initialize ProtocolFactoryManager failed: "+e.getMessage(),e);
				msgDiag.logStartupError("Riscontrato errore durante l'inizializzazione del ProtocolFactoryManager","initProtocolFactoryManager");
				return;
			}
			
			
			
			


			/* ------------- Inizializzo il sistema di Logging per gli appender personalizzati --------------- */
			boolean isInitializeAppender = false;
			isInitializeAppender = OpenSPCoop2Logger.initializeMsgDiagnosticiOpenSPCoopAppender(configurazionePdDReader.getOpenSPCoopAppender_MsgDiagnostici());
			if(isInitializeAppender == false){
				return;
			}
			isInitializeAppender = OpenSPCoop2Logger.initializeTracciamentoOpenSPCoopAppender(configurazionePdDReader.getOpenSPCoopAppender_Tracciamento());
			if(isInitializeAppender == false){
				return;
			}
			isInitializeAppender = OpenSPCoop2Logger.initializeDumpOpenSPCoopAppender(configurazionePdDReader.getOpenSPCoopAppender_Dump());
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
			if( configurazionePdDReader.getAccessoRegistroServizi() == null ){
				msgDiag.logStartupError("Riscontrato errore durante la lettura dei valori associati al registro dei servizi di OpenSPCoop.", "Lettura configurazione PdD");
				return;
			}

			// Controllo la consistenza dei registri di tipo DB
			AccessoRegistro accessoRegistro = configurazionePdDReader.getAccessoRegistroServizi();
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
						propertiesReader.isConfigurazioneCache_RegistryPrefill());
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













			/* ---------------- Validazione semantica -----------------------*/

			// Configurazione della Porta di Dominio
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
			
		
		
		
		
		
			/* ----------- Inizializzazione Servizi Porta di Dominio ------------ */
			try{
				StatoServiziPdD.initialize();
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione stato servizi Porta di Dominio");
				return;
			}
			try{
				RicezioneContenutiApplicativi.initializeService(configurazionePdDReader, classNameReader, propertiesReader, logCore);
			}catch(Exception e){
				msgDiag.logStartupError(e,"Inizializzazione servizio RicezioneContenutiApplicativi");
				return;
			}
			try{
				RicezioneBuste.initializeService(configurazionePdDReader,classNameReader, propertiesReader, logCore);
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
		
		
		
		
		
		
	
		
		
		
		
		
		
			/* ----------- Inizializzazione Risorse JMX ------------ */
			if( OpenSPCoop2Startup.this.gestoreRisorseJMX!=null ){
				// MBean ConfigurazionePdD
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanConfigurazionePdD();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - configurazione della Porta di Dominio");
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
					msgDiag.logStartupError(e,"RisorsaJMX - stato servizi della Porta di Dominio");
				}
				// MBean StatistichePdD
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanStatistichePdD();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - statistiche della Porta di Dominio");
				}
				// MBean SystemPropertiesPdD
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanSystemPropertiesPdD();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - proprietà di sistema della Porta di Dominio");
				}
				// MBean ConfigurazioneSistema
				try{
					OpenSPCoop2Startup.this.gestoreRisorseJMX.registerMBeanConfigurazioneSistema();
				}catch(Exception e){
					msgDiag.logStartupError(e,"RisorsaJMX - configurazione di sistema della Porta di Dominio");
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
						LogLevels.toOpenSPCoop2(configurazionePdDReader.getSeverita_msgDiagnostici(),true),
						LogLevels.toOpenSPCoop2(configurazionePdDReader.getSeveritaLog4J_msgDiagnostici(),true),
						OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato, OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato, OpenSPCoop2Logger.loggerIntegrationManagerAbilitato,
						configurazionePdDReader.tracciamentoBuste(), configurazionePdDReader.dumpMessaggi(),
						configurazionePdDReader.dumpBinarioPD(), configurazionePdDReader.dumpBinarioPA(),
						OpenSPCoop2Logger.loggerTracciamentoAbilitato, OpenSPCoop2Logger.loggerDumpAbilitato,
						infoConfigSistema.getInformazioniDatabase(),
						infoConfigSistema.getInformazioniSSL(true,true),
						infoConfigSistema.getInformazioniCryptographyKeyLength(),
						infoConfigSistema.getInformazioniInternazionalizzazione(true),
						infoConfigSistema.getInformazioniTimeZone(true),
						infoConfigSistema.getPluginProtocols(), 
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
					OpenSPCoop2Startup.this.timerMonitoraggioRisorse = new TimerMonitoraggioRisorse();
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
					OpenSPCoop2Startup.this.timerThreshold = new TimerThreshold();
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
			
			
			
			

			/* ------------ Jminix StandaloneMiniConsole  ------------ */
			if(propertiesReader.getPortJminixConsole()!=null){
				try{
					jminixStandaloneConsole = new StandaloneMiniConsole(propertiesReader.getPortJminixConsole());
					log.info("JminixStandaloneConsole correttamente avviata");
					logCore.info("JminixStandaloneConsole correttamente avviata");
				}catch(Throwable e){
					logCore.error("Errore durante l'avvio della jminixStandaloneConsole: "+e.getMessage(),e);
				}
			}
			



			/* ------------ OpenSPCoop startup terminato  ------------ */
			long endDate = System.currentTimeMillis();
			long secondStarter = (endDate - OpenSPCoop2Startup.this.startDate) / 1000;
			msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_OPENSPCOOP_STARTUP);
			msgDiag.addKeyword(CostantiPdD.KEY_VERSIONE_PORTA, propertiesReader.getPddDetailsForLog());
			msgDiag.addKeyword(CostantiPdD.KEY_TEMPO_AVVIO, secondStarter+" secondi");
			OpenSPCoop2Startup.log.info("Porta di Dominio "+propertiesReader.getPddDetailsForLog()+" avviata correttamente in "+secondStarter+" secondi.");
			if(OpenSPCoop2Logger.isLoggerOpenSPCoopConsoleStartupAgganciatoLog()){
				// per farlo finire anche sul server.log
				System.out.println("Porta di Dominio "+propertiesReader.getPddDetailsForLog()+" avviata correttamente in "+secondStarter+" secondi.");
			}
			msgDiag.logPersonalizzato("pdd");
			MsgDiagnostico msgIM = new MsgDiagnostico(IntegrationManager.ID_MODULO);
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
		
		// ExitHandler
		try{
			ExitContext context = new ExitContext();
			context.setPddContext(this.pddContext);
			context.setLogConsole(OpenSPCoop2Startup.log);
			context.setLogCore(OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			GestoreHandlers.exit(context);
		}catch(Throwable e){}
		
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
