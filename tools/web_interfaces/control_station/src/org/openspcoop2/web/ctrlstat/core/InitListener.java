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


package org.openspcoop2.web.ctrlstat.core;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.XMLDiff;
import org.openspcoop2.monitor.engine.alarm.AlarmConfigProperties;
import org.openspcoop2.monitor.engine.alarm.AlarmEngineConfig;
import org.openspcoop2.monitor.engine.alarm.AlarmManager;
import org.openspcoop2.monitor.engine.dynamic.CorePluginLoader;
import org.openspcoop2.monitor.engine.dynamic.PluginLoader;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntimeProperties;
import org.openspcoop2.pdd.core.byok.BYOKMapProperties;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.logger.filetrace.FileTraceGovWayState;
import org.openspcoop2.pdd.services.ServicesUtils;
import org.openspcoop2.protocol.basic.archive.BasicArchive;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Semaphore;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.byok.BYOKProvider;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.certificate.ocsp.OCSPManager;
import org.openspcoop2.utils.json.YamlSnakeLimits;
import org.openspcoop2.utils.properties.MapProperties;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.security.ProviderUtils;
import org.openspcoop2.utils.xml.XMLDiffImplType;
import org.openspcoop2.utils.xml.XMLDiffOptions;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.config.DatasourceProperties;
import org.openspcoop2.web.ctrlstat.config.RegistroServiziRemotoProperties;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB_LIB;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneRegistroPluginsReader;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementParameter;
import org.slf4j.Logger;

/**
 * Questa classe si occupa di inizializzare tutte le risorse necessarie alla
 * govwayConsole.
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class InitListener implements ServletContextListener {

	protected static Logger log = null;
	public static Logger getLog() {
		return log;
	}
	public static void setLog(Logger log) {
		InitListener.log = log;
	}
	static void logDebug(String msg) {
		if(InitListener.log!=null) {
			InitListener.log.debug(msg);
		}
	}
	static void logDebug(String msg, Throwable e) {
		if(InitListener.log!=null) {
			InitListener.log.debug(msg, e);
		}
	}
	static void logInfo(String msg) {
		if(InitListener.log!=null) {
			InitListener.log.info(msg);
		}
	}
	static void logError(String msg) {
		if(InitListener.log!=null) {
			InitListener.log.error(msg);
		}
	}
	static void logError(String msg, Throwable e) {
		if(InitListener.log!=null) {
			InitListener.log.error(msg,e);
		}
	}

	private static final Semaphore semaphoreInitListener = new Semaphore("InitListener");
	private static boolean initialized = false;
	static {
		InitListener.log = LoggerWrapperFactory.getLogger(InitListener.class);
	}

	public static boolean isInitialized() {
		return InitListener.initialized;
	}
	public static void setInitialized(boolean initialized) {
		InitListener.initialized = initialized;
	}

	private static FileTraceGovWayState fileTraceGovWayState;
	static void setFileTraceGovWayState(FileTraceGovWayState fileTraceGovWayState) {
		InitListener.fileTraceGovWayState = fileTraceGovWayState;
	}
	public static FileTraceGovWayState getFileTraceGovWayState() {
		return fileTraceGovWayState;
	}

	private GestoreConsistenzaDati gestoreConsistenzaDati;
	private InitRuntimeConfigReader initRuntimeConfigReader;
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		InitListener.logInfo("Undeploy govwayConsole in corso...");

		InitListener.setInitialized(false);
		
        // Fermo i Gestori
		if(this.gestoreConsistenzaDati!=null){
			this.gestoreConsistenzaDati.setStop(true);
			int limite = 60;
			int index = 0;
			while(GestoreConsistenzaDati.gestoreConsistenzaDatiInEsecuzione && index<limite){
				Utilities.sleep(1000);
				index++;
			}
		}
		if(this.initRuntimeConfigReader!=null) {
			this.initRuntimeConfigReader.setStop(true);
		}

		// chiusura repository dei plugin
		try {
			CorePluginLoader.close(InitListener.log);
		} catch (Exception e) {
			String msgErrore = "Errore durante la chiusura del loader dei plugins: " + e.getMessage();
			InitListener.logError(msgErrore,e);
		}
		
		InitListener.logInfo("Undeploy govwayConsole effettuato.");

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		SemaphoreLock lock = semaphoreInitListener.acquireThrowRuntime("contextInitialized");
		try {
			
			if(InitListener.initialized) {
				return;
			}
			
			String confDir = null;
			String confPropertyName = null;
			String confLocalPathPrefix = null;
			boolean appendActualConfiguration = false;
			try{
				try (InputStream is = InitListener.class.getResourceAsStream("/console.properties");){
					if(is!=null){
						Properties p = new Properties();
						p.load(is);
						
						confDir = p.getProperty("confDirectory");
						if(confDir!=null){
							confDir = confDir.trim();
						}
						
						confPropertyName = p.getProperty("confPropertyName");
						if(confPropertyName!=null){
							confPropertyName = confPropertyName.trim();
						}
						
						confLocalPathPrefix = p.getProperty("confLocalPathPrefix");
						if(confLocalPathPrefix!=null){
							confLocalPathPrefix = confLocalPathPrefix.trim();
						}
						
						String tmpAppendActualConfiguration = p.getProperty("appendLog4j");
						if(tmpAppendActualConfiguration!=null){
							appendActualConfiguration = "true".equalsIgnoreCase(tmpAppendActualConfiguration.trim());
						}
					}
				}
	
			}catch(Exception e){
				// ignore
			}
					
			try{
				ControlStationLogger.initialize(InitListener.log, confDir, confPropertyName, confLocalPathPrefix, null, appendActualConfiguration);
				InitListener.setLog(ControlStationLogger.getPddConsoleCoreLogger());
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			
			
			InitListener.logInfo("Inizializzazione resources (properties) govwayConsole in corso...");
			ConsoleProperties consoleProperties = null;
			try{
			
				if(!ConsoleProperties.initialize(confDir, confPropertyName, confLocalPathPrefix,InitListener.log)){
					throw new UtilsException("ConsoleProperties not initialized");
				}
				consoleProperties = ConsoleProperties.getInstance();
				
				if(!DatasourceProperties.initialize(confDir, confPropertyName, confLocalPathPrefix,InitListener.log)){
					throw new UtilsException("DatasourceProperties not initialized");
				}
				
				if(
					(consoleProperties.isSinglePddRegistroServiziLocale()==null || (!consoleProperties.isSinglePddRegistroServiziLocale().booleanValue()))
					&&
					(!RegistroServiziRemotoProperties.initialize(confDir, confPropertyName, confLocalPathPrefix,InitListener.log))
					){
					throw new UtilsException("RegistroServiziRemotoProperties not initialized");
				}
				
							
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			InitListener.logInfo("Inizializzazione resources (properties) govwayConsole effettuata con successo.");
	
			
			// Inizializzo Controlli connessioni
			try {
				Logger logR = ControlStationLogger.getPddConsoleCoreLogger()!=null ? ControlStationLogger.getPddConsoleCoreLogger() : InitListener.log;
				ServicesUtils.initCheckConnectionDB(logR, consoleProperties.isJdbcCloseConnectionCheckIsClosed(), consoleProperties.isJdbcCloseConnectionCheckAutocommit());
				
				DriverControlStationDB.setCheckLogger(logR);
				DriverControlStationDB.setCheckIsClosed(consoleProperties.isJdbcCloseConnectionCheckIsClosed());
				DriverControlStationDB.setCheckAutocommit(consoleProperties.isJdbcCloseConnectionCheckAutocommit());
				DBManager.setCheckLogger(logR);
				DBManager.setCheckIsClosed(consoleProperties.isJdbcCloseConnectionCheckIsClosed());
				DBManager.setCheckAutocommit(consoleProperties.isJdbcCloseConnectionCheckAutocommit());
			} catch (Exception e) {
				String msgErrore = "Inizializzazione controlli connessione non riuscita: "+e.getMessage();
				InitListener.logError(msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
						
			// Map (environment)
			try {
				String mapConfig = consoleProperties.getEnvMapConfig();
				if(StringUtils.isNotEmpty(mapConfig)) {
					InitListener.logInfo("Inizializzazione environment in corso...");
					MapProperties.initialize(InitListener.log, mapConfig, consoleProperties.isEnvMapConfigRequired());
					MapProperties mapProperties = MapProperties.getInstance();
					mapProperties.initEnvironment();
					String msgInit = "Environment inizializzato con le variabili definite nel file '"+mapConfig+"'"+
							"\n\tJavaProperties: "+mapProperties.getJavaMap().keys()+
							"\n\tEnvProperties: "+mapProperties.getEnvMap().keys()+
							"\n\tObfuscateMode: "+mapProperties.getObfuscateModeDescription()+
							"\n\tObfuscatedJavaKeys: "+mapProperties.getObfuscatedJavaKeys()+
							"\n\tObfuscatedEnvKeys: "+mapProperties.getObfuscatedEnvKeys();
					InitListener.logInfo(msgInit);
				}
			} catch (Exception e) {
				String msgErrore = "Inizializzazione ambiente non riuscita: "+e.getMessage();
				InitListener.logError(msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
			
			// Load Security Provider
			try {
				if(consoleProperties.isSecurityLoadBouncyCastle()) {
					ProviderUtils.addBouncyCastleAfterSun(true);
					InitListener.logInfo("Aggiunto Security Provider org.bouncycastle.jce.provider.BouncyCastleProvider");
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'aggiunta dei security provider: " + e.getMessage();
				InitListener.logError(msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
						
			// inizializzo HSM Manager
			try {
				String hsmConfig = consoleProperties.getHSMConfigurazione();
				if(StringUtils.isNotEmpty(hsmConfig)) {
					InitListener.logInfo("Inizializzazione HSM in corso...");
					File f = new File(hsmConfig);
					HSMManager.init(f, consoleProperties.isHSMRequired(), log, false);
					HSMUtils.setHsmConfigurableKeyPassword(consoleProperties.isHSMKeyPasswordConfigurable());
					InitListener.logInfo("Inizializzazione HSM effettuata con successo");
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del manager HSM: " + e.getMessage();
				InitListener.logError(msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
					
			// inizializzo OCSP Manager
			try {
				String ocspConfig = consoleProperties.getOCSPConfigurazione();
				if(StringUtils.isNotEmpty(ocspConfig)) {
					InitListener.logInfo("Inizializzazione OCSP in corso...");
					File f = new File(ocspConfig);
					OCSPManager.init(f, consoleProperties.isOCSPRequired(), consoleProperties.isOCSPLoadDefault(), log);
					InitListener.logInfo("Inizializzazione OCSP effettuata con successo");
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del manager OCSP: " + e.getMessage();
				InitListener.logError(msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
			
			// inizializzo BYOK Manager
			BYOKManager byokManager = null;
			try {
				String byokConfig = consoleProperties.getBYOKConfig();
				if(StringUtils.isNotEmpty(byokConfig)) {
					InitListener.logInfo("Inizializzazione BYOK in corso...");
					File f = new File(byokConfig);
					BYOKManager.init(f, consoleProperties.isBYOKConfigRequired(), log);
					byokManager = BYOKManager.getInstance();
					BYOKProvider.setUnwrapKeystoreFileEnabled(consoleProperties.isConsoleBYOKShowUnwrapPolicy());
					String msgInit = "Gestore BYOK inizializzato;"+
							"\n\tHSM registrati: "+byokManager.getKeystoreTypes()+
							"\n\tSecurityEngine registrati: "+byokManager.getSecurityEngineTypes()+
							"\n\tGovWaySecurityEngine: "+byokManager.getSecurityEngineGovWayDescription()+
							"\n\tVisualizza informazioni cifrate: "+consoleProperties.isVisualizzaInformazioniCifrate()+
							"\n\tVisualizza policy unwrap: "+consoleProperties.isConsoleBYOKShowUnwrapPolicy();
					InitListener.logInfo(msgInit);
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del manager BYOK: " + e.getMessage();
				InitListener.logError(msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
						
			// Secrets (environment)
			boolean reInitSecretMaps = false;
			try {
				String secretsConfig = consoleProperties.getBYOKEnvSecretsConfig();
				if(byokManager!=null && StringUtils.isNotEmpty(secretsConfig)) {
					InitListener.logInfo("Inizializzazione secrets in corso...");
					
					boolean useSecurityEngine = true;
					Map<String, Object> dynamicMap = new HashMap<>();
					DynamicInfo dynamicInfo = new  DynamicInfo();
					DynamicUtils.fillDynamicMap(log, dynamicMap, dynamicInfo);
					if(byokManager.isBYOKRemoteGovWayNodeUnwrapConfig()) {
						// i secrets cifrati verranno riletti quando i nodi sono attivi (verificato in InitRuntimeConfigReader)
						reInitSecretMaps = true;
						useSecurityEngine = false;
					}
					
					BYOKMapProperties.initialize(InitListener.log, secretsConfig, consoleProperties.isBYOKEnvSecretsConfigRequired(), 
							useSecurityEngine, 
							dynamicMap, true);
					BYOKMapProperties secretsProperties = BYOKMapProperties.getInstance();
					secretsProperties.initEnvironment();
					String msgInit = "Environment inizializzato con i secrets definiti nel file '"+secretsConfig+"'"+
							"\n\tJavaProperties: "+secretsProperties.getJavaMap().keys()+
							"\n\tEnvProperties: "+secretsProperties.getEnvMap().keys()+
							"\n\tObfuscateMode: "+secretsProperties.getObfuscateModeDescription();
					InitListener.logInfo(msgInit);
				}
			} catch (Exception e) {
				String msgErrore = "Inizializzazione ambiente (secrets) non riuscita: "+e.getMessage();
				InitListener.logError(msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}		
			
			// inizializza nodi runtime
			// !!NOTA!!: eventuali secrets riferiti nella ConfigurazioneNodiRuntimeProperties devono essere definiti tramite kms o tramite security che non invocano i nodi govway run
			InitListener.logInfo("Inizializzazione NodiRuntime in corso...");
			try {
				ConfigurazioneNodiRuntimeProperties backwardCompatibility = new ConfigurazioneNodiRuntimeProperties(consoleProperties.getJmxPdDBackwardCompatibilityPrefix(), 
						consoleProperties.getJmxPdDBackwardCompatibilityProperties());
				ConfigurazioneNodiRuntime.initialize(consoleProperties.getJmxPdDExternalConfiguration(), backwardCompatibility);
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del gestore dei nodi run: " + e.getMessage();
				InitListener.logError(msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
			InitListener.logInfo("Inizializzazione NodiRuntime effettuata con successo");
			
			InitListener.logInfo("Inizializzazione ExtendedInfoManager in corso...");
			try{
				ExtendedInfoManager.initialize(new Loader(), 
						consoleProperties.getExtendedInfoDriverConfigurazione(), 
						consoleProperties.getExtendedInfoDriverPortaDelegata(), 
						consoleProperties.getExtendedInfoDriverPortaApplicativa());
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			InitListener.logInfo("Inizializzazione ExtendedInfoManager effettuata con successo");
			
			InitListener.logInfo("Inizializzazione resources govwayConsole in corso...");
			try{
			
				Connettori.initialize(InitListener.log);
				
				DriverControlStationDB_LIB.initialize(InitListener.log);
							
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			InitListener.logInfo("Inizializzazione resources govwayConsole effettuata con successo.");
			
			InitListener.logInfo("Inizializzazione YAML Limits in corso...");
			try{
				Properties yamlSnakeLimits = consoleProperties.getApiYamlSnakeLimits();
				if(yamlSnakeLimits!=null && !yamlSnakeLimits.isEmpty()) {
					YamlSnakeLimits.initialize(InitListener.log, yamlSnakeLimits);
				}
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			InitListener.logInfo("Inizializzazione YAML Limits con successo");
			
			InitListener.logInfo("Inizializzazione XMLDiff in corso...");
			try{
				XMLDiff diff = new XMLDiff(OpenSPCoop2MessageFactory.getDefaultMessageFactory());
				diff.initialize(XMLDiffImplType.XML_UNIT, new XMLDiffOptions());
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			InitListener.logInfo("Inizializzazione XMLDiff effettuata con successo");

			try{
				// Notes on Apache Commons FileUpload 1.3.3
				// Regarding potential security problems with the class called DiskFileItem, it is true, that this class exists, 
				// and can be serialized/deserialized in FileUpload versions, up to, and including 1.3.2. 
				// ...
				// Beginning with 1.3.3, the class DiskFileItem is still implementing the interface java.io.Serializable. 
				// In other words, it still declares itself as serializable, and deserializable to the JVM. 
				// In practice, however, an attempt to deserialize an instance of DiskFileItem will trigger an Exception. 
				// In the unlikely case, that your application depends on the deserialization of DiskFileItems, 
				// you can revert to the previous behaviour by setting the system property "org.apache.commons.fileupload.disk.DiskFileItem.serializable" to "true".
				// 
				// Purtroppo la classe 'org.govway.struts.upload.FormFile', all'interna utilizza DiskFileItem per serializzare le informazioni.
				// Tale classe viene usata nel meccanismo di import/export nei metodi writeFormFile e readFormFile della classe org.openspcoop2.web.ctrlstat.servlet.archivi.ImporterUtils
				// Per questo motivo si riabilita' l'opzione!
				InitListener.logInfo("Inizializzazione DiskFileItem (opzione serializable), in corso...");
				System.setProperty("org.apache.commons.fileupload.disk.DiskFileItem.serializable", "true");
				InitListener.logInfo("Inizializzazione DiskFileItem (opzione serializable), effettuata.");
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			
			InitListener.logInfo("Inizializzazione keystore config in corso...");
			try{
				DBUtils.setKeystoreJksPasswordRequired(consoleProperties.isKeystoreJksPasswordRequired());
				DBUtils.setKeystoreJksKeyPasswordRequired(consoleProperties.isKeystoreJksKeyPasswordRequired());
				DBUtils.setKeystorePkcs12PasswordRequired(consoleProperties.isKeystorePkcs12PasswordRequired());
				DBUtils.setKeystorePkcs12KeyPasswordRequired(consoleProperties.isKeystorePkcs12KeyPasswordRequired());
				DBUtils.setTruststoreJksPasswordRequired(consoleProperties.isTruststoreJksPasswordRequired());
				DBUtils.setTruststorePkcs12PasswordRequired(consoleProperties.isTruststorePkcs12PasswordRequired());
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			InitListener.logInfo("Inizializzazione keystore config effettuata con successo");
			
			try{
				if(consoleProperties.isGestoreConsistenzaDatiEnabled()){
					this.gestoreConsistenzaDati = new GestoreConsistenzaDati(consoleProperties.isGestoreConsistenzaDatiForceCheckMapping());
	                new Thread(this.gestoreConsistenzaDati).start();
	                InitListener.logInfo("Gestore Controllo Consistenza Dati avviato con successo.");
				}
				else{
					InitListener.logInfo("Gestore Controllo Consistenza Dati disabilitato.");
				}
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			
			InitListener.logInfo("Inizializzazione DataElement in corso...");
			try{
				int consoleLunghezzaLabel = consoleProperties.getConsoleLunghezzaLabel();
				int numeroColonneTextArea = consoleProperties.getConsoleNumeroColonneDefaultTextArea();
				DataElementParameter dep = new DataElementParameter();
				dep.setSize(consoleLunghezzaLabel);
				dep.setCols(numeroColonneTextArea); 
				DataElement.initialize(dep);
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			InitListener.logInfo("Inizializzazione DataElement effettuata con successo");
			
			ServletContext servletContext = sce.getServletContext();
	
			InputStream isFont = null;
	
			try{
				String fontFileName = ConsoleProperties.getInstance().getConsoleFont();
				
				InitListener.logDebug("Caricato Font dal file: ["+fontFileName+"] in corso... ");
				
				isFont = servletContext.getResourceAsStream("/fonts/"+ fontFileName);
	
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				Font fontCaricato = Font.createFont(Font.PLAIN, isFont);
				
				InitListener.logDebug("Caricato Font: ["+fontCaricato.getName()+"] FontName: ["+fontCaricato.getFontName()+"] FontFamily: ["+fontCaricato.getFamily()+"] FontStyle: ["+fontCaricato.getStyle()+"]");
				
				ge.registerFont(fontCaricato);
	
				InitListener.logDebug("Check Graphics Environment: is HeadeLess ["+java.awt.GraphicsEnvironment.isHeadless()+"]");
	
				InitListener.logDebug("Elenco Nomi Font disponibili: " + Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
				
				ConsoleProperties.getInstance().setConsoleFontName(fontCaricato.getName());
				ConsoleProperties.getInstance().setConsoleFontFamilyName(fontCaricato.getFamily());
				ConsoleProperties.getInstance().setConsoleFontStyle(fontCaricato.getStyle());
				
				InitListener.logDebug("Caricato Font dal file: ["+fontFileName+"] completato.");
			}catch (Exception e) {
				InitListener.logError(e.getMessage(),e);
			} finally {
				if(isFont != null){
					try {	isFont.close(); } catch (IOException e) {	
						// ignore
					}
				}
			}
							
			// inizializza il repository dei plugin
			try {
				if(consoleProperties.isConfigurazionePluginsEnabled()!=null && consoleProperties.isConfigurazionePluginsEnabled().booleanValue()) {
					CorePluginLoader.initialize(new Loader(), InitListener.log,
							PluginLoader.class,
							new ConfigurazioneRegistroPluginsReader(new ControlStationCore()), 
							consoleProperties.getPluginsSeconds());
				}
				
				if(consoleProperties.isConfigurazioneAllarmiEnabled()!=null && consoleProperties.isConfigurazioneAllarmiEnabled().booleanValue()) {
					AlarmEngineConfig alarmEngineConfig = AlarmConfigProperties.getAlarmConfiguration(InitListener.log, consoleProperties.getAllarmiConfigurazione(), consoleProperties.getConfDirectory());
					AlarmManager.setAlarmEngineConfig(alarmEngineConfig);
					CostantiDB.setAllarmiEnabled(true);
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del loader dei plugins: " + e.getMessage();
				InitListener.logError(
						//					throw new ServletException(
						msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
			
			// inizializzo OpenAPIValidator
			try {
				org.openapi4j.parser.validation.v3.OpenApi3Validator.VALIDATE_URI_REFERENCE_AS_URL = consoleProperties.isApiOpenAPIValidateUriReferenceAsUrl();
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del validatore OpenAPI: " + e.getMessage();
				InitListener.logError(
						//					throw new ServletException(
						msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
			
			// Basic Archive
			try {
				BasicArchive.setNormalizeDescription255(consoleProperties.isApiDescriptionTruncate255());
				BasicArchive.setNormalizeDescription4000(consoleProperties.isApiDescriptionTruncate4000());
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del BasicArchive: " + e.getMessage();
				InitListener.logError(
						//					throw new ServletException(
						msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
			
			// SignalHub
			try {
				// Inizializzazione SignalHub
				if(ProtocolFactoryManager.getInstance().existsProtocolFactory(org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROTOCOL_NAME)) {
					CostantiDB.setServiziDigestEnabled(ModIUtils.isSignalHubEnabled());
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del SignalHub: " + e.getMessage();
				InitListener.logError(
						//					throw new ServletException(
						msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
			
			// InitRuntimeConfigReader
			try{
				this.initRuntimeConfigReader = new InitRuntimeConfigReader(consoleProperties, reInitSecretMaps);
				this.initRuntimeConfigReader.start();
				InitListener.logInfo("RuntimeConfigReader avviato con successo.");
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del RuntimeConfigReader: " + e.getMessage();
				InitListener.logError(
						msgErrore,e);
				//throw new UtilsRuntimeException(msgErrore,e); non sollevo l'eccezione, e' solo una informazione informativa, non voglio mettere un vincolo che serve per forza un nodo acceso
			}
			
			InitListener.setInitialized(true);
		}finally {
			semaphoreInitListener.release(lock, "contextInitialized");
		}
	}

}
