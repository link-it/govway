/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.listener;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.dao.DAOFactoryInstanceProperties;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.transazioni.utils.DumpUtils;
import org.openspcoop2.monitor.engine.dynamic.CorePluginLoader;
import org.openspcoop2.monitor.engine.dynamic.PluginLoader;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntimeProperties;
import org.openspcoop2.pdd.core.byok.BYOKMapProperties;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheJMXUtils;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.properties.CollectionProperties;
import org.openspcoop2.utils.properties.MapProperties;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.security.ProviderUtils;
import org.openspcoop2.web.monitor.core.config.ApplicationProperties;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsServiceCache;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsServiceCacheJmxDatiConfigurazione;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsServiceCacheJmxRicercheConfigurazione;
import org.openspcoop2.web.monitor.core.dao.RegistroPluginsService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

/**
 * AbstractConsoleStartupListener
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public abstract class AbstractConsoleStartupListener implements ServletContextListener{

	protected static Logger log = LoggerWrapperFactory.getLogger(AbstractConsoleStartupListener.class);
	public static Logger getLog() {
		return log;
	}
	protected static void logDebug(String msg) {
		if(AbstractConsoleStartupListener.log!=null) {
			AbstractConsoleStartupListener.log.debug(msg);
		}
	}
	protected static void logDebug(String msg, Throwable e) {
		if(AbstractConsoleStartupListener.log!=null) {
			AbstractConsoleStartupListener.log.debug(msg, e);
		}
	}
	protected static void logInfo(String msg) {
		if(AbstractConsoleStartupListener.log!=null) {
			AbstractConsoleStartupListener.log.info(msg);
		}
	}
	protected static void logWarn(String msg) {
		if(AbstractConsoleStartupListener.log!=null) {
			AbstractConsoleStartupListener.log.warn(msg);
		}
	}
	protected static void logError(String msg) {
		if(AbstractConsoleStartupListener.log!=null) {
			AbstractConsoleStartupListener.log.error(msg);
		}
	}
	protected static void logError(String msg, Throwable e) {
		if(AbstractConsoleStartupListener.log!=null) {
			AbstractConsoleStartupListener.log.error(msg,e);
		}
	}

	public static boolean initializedLog = false;

	/*
	 * APPLICATION
	 */
	private static final String DEFAULT_APPLICATION_NAME = "/"+Costanti.APP_PROPERTIES_PATH;
	private static final String DEFAULT_LOCAL_APPLICATION_NAME = "/"+Costanti.APP_PROPERTIES_LOCAL_PATH;
	private static final String DEFAULT_APPLICATION_PROPERTY_NAME = Costanti.APP_PROPERTIES;
	private String applicationProperties;
	public void setApplicationProperties(String applicationProperties) {
		this.applicationProperties = applicationProperties;
	}
	private String localApplicationProperties;
	public void setLocalApplicationProperties(String localApplicationProperties) {
		this.localApplicationProperties = localApplicationProperties;
	}
	private String localApplicationPropertyName;
	public void setLocalApplicationPropertyName(String localApplicationPropertyName) {
		this.localApplicationPropertyName = localApplicationPropertyName;
	}
	public String getApplicationProperties() {

		// se il loggerName non e' impostato restituisco il default
		if (this.applicationProperties == null
				|| "".equals(this.applicationProperties))
			return AbstractConsoleStartupListener.DEFAULT_APPLICATION_NAME;

		return this.applicationProperties;
	}
	public String getLocalApplicationProperties() {

		// se il loggerName non e' impostato restituisco il default
		if (this.localApplicationProperties == null
				|| "".equals(this.localApplicationProperties))
			return AbstractConsoleStartupListener.DEFAULT_LOCAL_APPLICATION_NAME;

		return this.localApplicationProperties;
	}
	public String getLocalApplicationPropertyName() {

		// se il loggerName non e' impostato restituisco il default
		if (this.localApplicationPropertyName == null
				|| "".equals(this.localApplicationPropertyName))
			return AbstractConsoleStartupListener.DEFAULT_APPLICATION_PROPERTY_NAME;

		return this.localApplicationPropertyName;
	}

	/*
	 * LOGGER
	 */
	private static final String DEFAULT_LOGGER_NAME = "/"+Costanti.APP_LOG_PROPERTIES_PATH;
	private static final String DEFAULT_LOCAL_LOGGER_NAME = "/"+Costanti.APP_LOG_PROPERTIES_LOCAL_PATH;
	private static final String DEFAULT_LOGGER_PROPERTY_NAME = Costanti.APP_LOG_PROPERTIES;
	private String logProperties;
	public void setLogProperties(String loggerName) {
		this.logProperties = loggerName;
	}
	private String localLogProperties;
	public void setLocalLogProperties(String localLog4jProperties) {
		this.localLogProperties = localLog4jProperties;
	}
	private String localLogPropertyName;
	public void setLocalLogPropertyName(String localLog4jPropertyName) {
		this.localLogPropertyName = localLog4jPropertyName;
	}
	public String getLoggerProperties() {

		// se il loggerName non e' impostato restituisco il default logger
		if (this.logProperties == null
				|| "".equals(this.logProperties))
			return AbstractConsoleStartupListener.DEFAULT_LOGGER_NAME;

		return this.logProperties;
	}
	public String getLocalLoggerProperties() {

		// se il loggerName non e' impostato restituisco il default logger
		if (this.localLogProperties == null
				|| "".equals(this.localLogProperties))
			return AbstractConsoleStartupListener.DEFAULT_LOCAL_LOGGER_NAME;

		return this.localLogProperties;
	}
	public String getLocalLoggerPropertyName() {

		// se il loggerName non e' impostato restituisco il default logger
		if (this.localLogPropertyName == null
				|| "".equals(this.localLogPropertyName))
			return AbstractConsoleStartupListener.DEFAULT_LOGGER_PROPERTY_NAME;

		return this.localLogPropertyName;
	}
	
	
	
	/*
	 * CACHE
	 */
	public static boolean debugCache_datiConfigurazione = false;
	public static DynamicUtilsServiceCache dynamicUtilsServiceCache_datiConfigurazione = null;
	public static boolean debugCache_ricercheConfigurazione = false;
	public static DynamicUtilsServiceCache dynamicUtilsServiceCache_ricercheConfigurazione = null;
	private static final String DEFAULT_CACHE_NAME = "/"+Costanti.APP_CACHE_PROPERTIES_PATH;
	private static final String DEFAULT_LOCAL_CACHE_NAME = "/"+Costanti.APP_CACHE_PROPERTIES_LOCAL_PATH;
	private static final String DEFAULT_CACHE_PROPERTY_NAME = Costanti.APP_CACHE_PROPERTIES;
	private String cacheProperties;
	public void setCacheProperties(String cacheName) {
		this.cacheProperties = cacheName;
	}
	private String localCacheProperties;
	public void setLocalCacheProperties(String localCacheProperties) {
		this.localCacheProperties = localCacheProperties;
	}
	private String localCachePropertyName;
	public void setLocalCachePropertyName(String localCachePropertyName) {
		this.localCachePropertyName = localCachePropertyName;
	}
	public String getCacheProperties() {

		// se il cacheName non e' impostato restituisco il default cache
		if (this.cacheProperties == null
				|| "".equals(this.cacheProperties))
			return AbstractConsoleStartupListener.DEFAULT_CACHE_NAME;

		return this.cacheProperties;
	}
	public String getLocalCacheProperties() {

		// se il cacheName non e' impostato restituisco il default cache
		if (this.localCacheProperties == null
				|| "".equals(this.localCacheProperties))
			return AbstractConsoleStartupListener.DEFAULT_LOCAL_CACHE_NAME;

		return this.localCacheProperties;
	}
	public String getLocalCachePropertyName() {

		// se il cacheName non e' impostato restituisco il default cache
		if (this.localCachePropertyName == null
				|| "".equals(this.localCachePropertyName))
			return AbstractConsoleStartupListener.DEFAULT_CACHE_PROPERTY_NAME;

		return this.localCachePropertyName;
	}
	
	
	private boolean reInitSecretMaps = false;
	public boolean isReInitSecretMaps() {
		return this.reInitSecretMaps;
	}
	
	private String getStringInitParameter(ServletContext ctx,String name){
		String value = ctx.getInitParameter(name);
		if(value==null || "".equals(value)){
			return null;
		}
		return value.trim();
	}


	@Override
	public void contextInitialized(ServletContextEvent evt) {
		ServletContext ctx = evt.getServletContext();

		// *** APPLICATION PROPERTIES ***

		this.setApplicationProperties(getStringInitParameter(ctx, "applicationProperties"));
		this.setLocalApplicationProperties(getStringInitParameter(ctx, "localApplicationProperties"));
		this.setLocalApplicationPropertyName(getStringInitParameter(ctx, "localApplicationPropertyName"));


		// carico il protocollo di default
		ApplicationProperties appProperties = null;
		try {
			ApplicationProperties.initialize(AbstractConsoleStartupListener.log, getApplicationProperties(), getLocalApplicationPropertyName(), getLocalApplicationProperties());
			appProperties = ApplicationProperties.getInstance(AbstractConsoleStartupListener.log);
		}catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione del ApplicationProperties: "
					+ e.getMessage();
			AbstractConsoleStartupListener.logError(msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}


		// *** LOGGER PROPERTIES ***

		try {

			boolean appendActualConfiguration = false;
			String tmpAppendActualConfiguration = appProperties.getProperty("appendLog4j", false, true);
			if(tmpAppendActualConfiguration!=null){
				appendActualConfiguration = "true".equalsIgnoreCase(tmpAppendActualConfiguration.trim());
			}
			
			this.setLogProperties(getStringInitParameter(ctx, "logProperties"));
			this.setLocalLogProperties(getStringInitParameter(ctx, "localLogProperties"));
			this.setLocalLogPropertyName(getStringInitParameter(ctx, "localLogPropertyName"));

			// leggo properties
			/**System.out.println("SET FILE CONF ["+fileConf+"]");
			System.out.println("INIT ["+getLocalDefaultLoggerName()+"]");*/
			InputStream is = AbstractConsoleStartupListener.class
					.getResourceAsStream(getLoggerProperties());
			Properties loggerProperties = new Properties();
			loggerProperties.load(is);

			// effettuo eventuale ovverride delle properties
			// leggo le properties eventualmente ridefinite
			CollectionProperties overrideProp = PropertiesUtilities
					.searchLocalImplementation(
							DAOFactoryInstanceProperties.OPENSPCOOP2_LOCAL_HOME, AbstractConsoleStartupListener.log,
							this.getLocalLoggerPropertyName(),
							this.getLocalLoggerProperties(),
							appProperties.getConfigurationDir());
			if (overrideProp != null) {
				Enumeration<?> proEnums = overrideProp.keys();
				while (proEnums.hasMoreElements()) {
					String key = (String) proEnums.nextElement();
					// effettuo l'ovverride
					loggerProperties.put(key, overrideProp.getProperty(key));
				}
			}

			// inizializzo il logger
			if(appendActualConfiguration){
				System.out.println("[govwayMonitor] Attendo inizializzazione GovWay prima di appender la configurazione Log4J ...");
				int i=0;
				int limit = 60;
				while(!OpenSPCoop2Startup.initialize && i<limit){
					Utilities.sleep(1000);
					i++;
					if(i%10==0){
						System.out.println("[govwayMonitor] Attendo inizializzazione GovWay ...");
					}
				}
				
				if(!OpenSPCoop2Startup.initialize){
					throw new UtilsException("[govwayMonitor] Inizializzazione GovWay non rilevata");
				}
				
				System.out.println("[govwayMonitor] Configurazione Log4J ...");
				LoggerWrapperFactory.setLogConfiguration(loggerProperties,true);
				System.out.println("[govwayMonitor] Configurazione Log4J aggiunta");
			}
			else{
				LoggerWrapperFactory.setLogConfiguration(loggerProperties);
			}

		} catch (Exception e) {
			String msgErrore = "Errore durante il caricamento del file di logging "
					+ getLoggerProperties()+": "+e.getMessage();
			AbstractConsoleStartupListener.logError(msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}


		
		
		// Map (environment)
		try {
			String mapConfig = appProperties.getEnvMapConfig();
			if(StringUtils.isNotEmpty(mapConfig)) {
				AbstractConsoleStartupListener.logInfo("Inizializzazione environment in corso...");
				MapProperties.initialize(AbstractConsoleStartupListener.log, mapConfig, appProperties.isEnvMapConfigRequired());
				MapProperties mapProperties = MapProperties.getInstance();
				mapProperties.initEnvironment();
				String msgInit = "Environment inizializzato con le variabili definite nel file '"+mapConfig+"'"+
						"\n\tJavaProperties: "+mapProperties.getJavaMap().keys()+
						"\n\tEnvProperties: "+mapProperties.getEnvMap().keys()+
						"\n\tObfuscateMode: "+mapProperties.getObfuscateModeDescription()+
						"\n\tObfuscatedJavaKeys: "+mapProperties.getObfuscatedJavaKeys()+
						"\n\tObfuscatedEnvKeys: "+mapProperties.getObfuscatedEnvKeys();
				AbstractConsoleStartupListener.logInfo(msgInit);
			}
		} catch (Exception e) {
			String msgErrore = "Inizializzazione ambiente non riuscita: "+e.getMessage();
			AbstractConsoleStartupListener.logError(msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}
		
		
		// Load Security Provider
		try {
			if(appProperties.isSecurityLoadBouncyCastle()) {
				ProviderUtils.addBouncyCastleAfterSun(true);
				AbstractConsoleStartupListener.logInfo("Aggiunto Security Provider org.bouncycastle.jce.provider.BouncyCastleProvider");
			}
		} catch (Exception e) {
			String msgErrore = "Errore durante l'aggiunta dei security provider: " + e.getMessage();
			AbstractConsoleStartupListener.logError(msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}
					
		// inizializzo HSM Manager
		try {
			String hsmConfig = appProperties.getHSMConfigurazione();
			if(StringUtils.isNotEmpty(hsmConfig)) {
				AbstractConsoleStartupListener.logInfo("Inizializzazione HSM in corso...");
				File f = new File(hsmConfig);
				HSMManager.init(f, appProperties.isHSMRequired(), log, false);
				HSMUtils.setHsmConfigurableKeyPassword(appProperties.isHSMKeyPasswordConfigurable());
				AbstractConsoleStartupListener.logInfo("Inizializzazione HSM effettuata con successo");
			}
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione del manager HSM: " + e.getMessage();
			AbstractConsoleStartupListener.logError(msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}
		
		// inizializzo BYOK Manager
		BYOKManager byokManager = null;
		try {
			String byokConfig = appProperties.getBYOKConfig();
			if(StringUtils.isNotEmpty(byokConfig)) {
				AbstractConsoleStartupListener.logInfo("Inizializzazione BYOK in corso...");
				File f = new File(byokConfig);
				BYOKManager.init(f, appProperties.isBYOKConfigRequired(), log);
				byokManager = BYOKManager.getInstance();
				String msgInit = "Gestore BYOK inizializzato;"+
						"\n\tHSM registrati: "+byokManager.getKeystoreTypes()+
						"\n\tSecurityEngine registrati: "+byokManager.getSecurityEngineTypes()+
						"\n\tGovWaySecurityEngine: "+byokManager.getSecurityEngineGovWayDescription();
				AbstractConsoleStartupListener.logInfo(msgInit);
			}
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione del manager BYOK: " + e.getMessage();
			AbstractConsoleStartupListener.logError(msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}
					
		// Secrets (environment)
		try {
			String secretsConfig = appProperties.getBYOKEnvSecretsConfig();
			if(byokManager!=null && StringUtils.isNotEmpty(secretsConfig)) {
				AbstractConsoleStartupListener.logInfo("Inizializzazione secrets in corso...");
				
				boolean useSecurityEngine = true;
				Map<String, Object> dynamicMap = new HashMap<>();
				DynamicInfo dynamicInfo = new  DynamicInfo();
				DynamicUtils.fillDynamicMap(log, dynamicMap, dynamicInfo);
				if(byokManager.isBYOKRemoteGovWayNodeUnwrapConfig()) {
					// i secrets cifrati verranno riletti quando i nodi sono attivi (verificato in InitRuntimeConfigReader)
					this.reInitSecretMaps = true;
					useSecurityEngine = false;
				}
				
				BYOKMapProperties.initialize(AbstractConsoleStartupListener.log, secretsConfig, appProperties.isBYOKEnvSecretsConfigRequired(), 
						useSecurityEngine, 
						dynamicMap, true);
				BYOKMapProperties secretsProperties = BYOKMapProperties.getInstance();
				secretsProperties.initEnvironment();
				String msgInit = "Environment inizializzato con i secrets definiti nel file '"+secretsConfig+"'"+
						"\n\tJavaProperties: "+secretsProperties.getJavaMap().keys()+
						"\n\tEnvProperties: "+secretsProperties.getEnvMap().keys()+
						"\n\tObfuscateMode: "+secretsProperties.getObfuscateModeDescription();
				AbstractConsoleStartupListener.logInfo(msgInit);
			}
		} catch (Exception e) {
			String msgErrore = "Inizializzazione ambiente (secrets) non riuscita: "+e.getMessage();
			AbstractConsoleStartupListener.logError(msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}		
		
		
		
		
		
		// *** CACHE PROPERTIES ***

		boolean isInitializeCache = false;
		boolean cacheEnabledDatiConfigurazione = false;
		boolean cacheEnabledRicercheConfigurazione = false;
		Logger logCore = LoggerManager.getPddMonitorCoreLogger();
		try {
			cacheEnabledDatiConfigurazione = appProperties.isAbilitataCache_datiConfigurazione();
			cacheEnabledRicercheConfigurazione = appProperties.isAbilitataCache_ricercheConfigurazione();
			if(cacheEnabledDatiConfigurazione || cacheEnabledRicercheConfigurazione) {
			
				this.setCacheProperties(getStringInitParameter(ctx, "cacheProperties"));
				this.setLocalCacheProperties(getStringInitParameter(ctx, "localCacheProperties"));
				this.setLocalCachePropertyName(getStringInitParameter(ctx, "localCachePropertyName"));
	
				isInitializeCache = Cache.initialize(AbstractConsoleStartupListener.log, logCore, 
						getCacheProperties(),
						appProperties.getConfigurationDir(), null, 
						org.openspcoop2.utils.Costanti.OPENSPCOOP2_LOCAL_HOME, getLocalCachePropertyName(), getLocalCacheProperties());
			}
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione della cache "
					+ getCacheProperties()+": "+e.getMessage();
			AbstractConsoleStartupListener.logError(msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}
		if((cacheEnabledDatiConfigurazione || cacheEnabledRicercheConfigurazione) && !isInitializeCache){
			String msgErrore = "Errore durante l'inizializzazione della cache "
					+ getCacheProperties()+": cache non inizializzata";
			AbstractConsoleStartupListener.logError(msgErrore);
			throw new UtilsRuntimeException(msgErrore);
		}
		if(cacheEnabledDatiConfigurazione) {
			try {
				debugCache_datiConfigurazione = appProperties.isDebugCache_datiConfigurazione();
				
				dynamicUtilsServiceCache_datiConfigurazione = new DynamicUtilsServiceCache(DynamicUtilsServiceCache.CACHE_NAME_DATI_CONFIGURAZIONE,
						logCore, appProperties.getDimensioneCache_datiConfigurazione(), appProperties.getAlgoritmoCache_datiConfigurazione(),
						appProperties.getItemIdleTimeCache_datiConfigurazione(), appProperties.getItemLifeSecondCache_datiConfigurazione());
				if(appProperties.isAbilitataJmxCache_datiConfigurazione()) {
					DynamicUtilsServiceCacheJmxDatiConfigurazione jmxObject = new DynamicUtilsServiceCacheJmxDatiConfigurazione();
					CacheJMXUtils.register(AbstractConsoleStartupListener.log,jmxObject,DynamicUtilsServiceCache.JMX_DOMAIN,jmxObject.getCacheWrapper().getCacheName());
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante la creazione della cache "
						+ getCacheProperties()+": "+e.getMessage();
				AbstractConsoleStartupListener.logError(
						//					throw new ServletException(
						msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
		}
		if(cacheEnabledRicercheConfigurazione) {
			try {
				debugCache_ricercheConfigurazione = appProperties.isDebugCache_ricercheConfigurazione();
				
				dynamicUtilsServiceCache_ricercheConfigurazione = new DynamicUtilsServiceCache(DynamicUtilsServiceCache.CACHE_NAME_RICERCHE_CONFIGURAZIONE,
						logCore, appProperties.getDimensioneCache_ricercheConfigurazione(), appProperties.getAlgoritmoCache_ricercheConfigurazione(),
						appProperties.getItemIdleTimeCache_ricercheConfigurazione(), appProperties.getItemLifeSecondCache_ricercheConfigurazione());
				if(appProperties.isAbilitataJmxCache_ricercheConfigurazione()) {
					DynamicUtilsServiceCacheJmxRicercheConfigurazione jmxObject = new DynamicUtilsServiceCacheJmxRicercheConfigurazione();
					CacheJMXUtils.register(AbstractConsoleStartupListener.log,jmxObject,DynamicUtilsServiceCache.JMX_DOMAIN,jmxObject.getCacheWrapper().getCacheName());
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante la creazione della cache "
						+ getCacheProperties()+": "+e.getMessage();
				AbstractConsoleStartupListener.logError(
						//					throw new ServletException(
						msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
		}
		
				
		

		// inizializza la ProtocolFactoryManager
		ConfigurazionePdD configPdD = null;
		try {

			configPdD = new ConfigurazionePdD();
			configPdD.setAttesaAttivaJDBC(-1);
			configPdD.setCheckIntervalJDBC(-1);
			configPdD.setLoader(new Loader(this.getClass().getClassLoader()));
			configPdD.setLog(AbstractConsoleStartupListener.log);
			ProtocolFactoryManager.initialize(AbstractConsoleStartupListener.log, configPdD,
					appProperties.getProtocolloDefault());

		} catch (Throwable e) {
			String msgErrore = "Errore durante l'inizializzazione del ProtocolFactoryManager: "
					+ e.getMessage();
			AbstractConsoleStartupListener.logError(msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}
		
		
		
		
		// inizializza la ExtendedInfoManager
		try {

			ExtendedInfoManager.initialize(configPdD.getLoader(), null, null, null);

		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione del ExtendedInfoManager: " + e.getMessage();
			AbstractConsoleStartupListener.logError(msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}
		
		
		// inizializza nodi runtime
		try {
			ConfigurazioneNodiRuntimeProperties backwardCompatibility = new ConfigurazioneNodiRuntimeProperties(appProperties.getJmxPdD_backwardCompatibilityPrefix(), 
					appProperties.getJmxPdD_backwardCompatibilityProperties());
			ConfigurazioneNodiRuntime.initialize(appProperties.getJmxPdD_externalConfiguration(), backwardCompatibility);
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione del gestore dei nodi run: " + e.getMessage();
			AbstractConsoleStartupListener.logError(msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}
		
		
		// inizializza il repository dei plugin
		try {
			if(appProperties.isPluginsEnabled()) {
				CorePluginLoader.initialize(configPdD.getLoader(), LoggerManager.getPddMonitorSqlLogger(),
						PluginLoader.class,
						new RegistroPluginsService(),
						appProperties.getPluginsSeconds());
			}
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione del loader dei plugins: " + e.getMessage();
			AbstractConsoleStartupListener.logError(msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}

		
		
		// inizializza DataMenager
		try {
			org.openspcoop2.utils.date.DateManager.initializeDataManager(org.openspcoop2.utils.date.SystemDate.class.getName(),null,log);
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione del DataManager: " + e.getMessage();
			AbstractConsoleStartupListener.logError(msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}

		
		// inizializzo accesso dati sul database per i dump
		try {
			DumpUtils.setThreshold_readInMemory(appProperties.getTransazioniDettaglioVisualizzazioneMessaggiThreshold());
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione del threshold per l'accesso ai dump: " + e.getMessage();
			AbstractConsoleStartupListener.logError(msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}
		
		

		AbstractConsoleStartupListener.logDebug("PddMonitor Console avviata correttamente.");
		
	}




	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if(AbstractConsoleStartupListener.log!=null)
			AbstractConsoleStartupListener.logInfo("Undeploy govwayMonitor Console in corso...");

		if(dynamicUtilsServiceCache_datiConfigurazione!=null || dynamicUtilsServiceCache_ricercheConfigurazione!=null) {
			CacheJMXUtils.unregister();
		}
		
		// chiusura repository dei plugin
		try {
			CorePluginLoader.close(AbstractConsoleStartupListener.log);
		} catch (Throwable e) {
			if(AbstractConsoleStartupListener.log!=null) {
				String msgErrore = "Errore durante la chiusura del loader dei plugins: " + e.getMessage();
				AbstractConsoleStartupListener.logError(msgErrore,e);
			}
		}
		
		if(AbstractConsoleStartupListener.log!=null)
			AbstractConsoleStartupListener.logInfo("Undeploy govwayMonitor Console effettuato.");

	}


}
