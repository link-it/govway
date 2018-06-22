package org.openspcoop2.web.monitor.core.listener;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspcoop2.core.commons.dao.DAOFactoryInstanceProperties;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.CollectionProperties;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.web.monitor.core.config.ApplicationProperties;
import org.openspcoop2.web.monitor.core.core.InitServlet;
import org.openspcoop2.web.monitor.core.utils.Costanti;
import org.slf4j.Logger;


public abstract class AbstractConsoleStartupListener implements ServletContextListener{

	protected static Logger log = LoggerWrapperFactory.getLogger(AbstractConsoleStartupListener.class); 

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
			AbstractConsoleStartupListener.log.error(
					//					throw new ServletException(
					msgErrore,e);
			throw new RuntimeException(msgErrore,e);
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
			//System.out.println("SET FILE CONF ["+fileConf+"]");
			//System.out.println("INIT ["+getLocalDefaultLoggerName()+"]");
			InputStream is = InitServlet.class
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
				while(OpenSPCoop2Startup.initialize==false && i<limit){
					Utilities.sleep(1000);
					i++;
					if(i%10==0){
						System.out.println("[govwayMonitor] Attendo inizializzazione GovWay ...");
					}
				}
				
				if(OpenSPCoop2Startup.initialize==false){
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
			AbstractConsoleStartupListener.log.error(
					//					throw new ServletException(
					msgErrore,e);
			throw new RuntimeException(msgErrore,e);
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
			AbstractConsoleStartupListener.log.error(
					//					throw new ServletException(
					msgErrore,e);
			throw new RuntimeException(msgErrore,e);
		}
		
		
		
		
		// inizializza la ExtendedInfoManager
		try {

			ExtendedInfoManager.initialize(configPdD.getLoader(), null, null, null);

		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione del ExtendedInfoManager: " + e.getMessage();
			AbstractConsoleStartupListener.log.error(
					//					throw new ServletException(
					msgErrore,e);
			throw new RuntimeException(msgErrore,e);
		}
		
		
		
		// inizializza il repository dei plugin
		try {

			DynamicFactory.initialize(appProperties.getRepositoryJars());

		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione del Repository dei jars: " + e.getMessage();
			AbstractConsoleStartupListener.log.error(
					//					throw new ServletException(
					msgErrore,e);
			throw new RuntimeException(msgErrore,e);
		}

		
		
		// inizializza DataMenager
		try {
			org.openspcoop2.utils.date.DateManager.initializeDataManager(org.openspcoop2.utils.date.SystemDate.class.getName(),null,log);
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione del DataManager: " + e.getMessage();
			AbstractConsoleStartupListener.log.error(
					//					throw new ServletException(
					msgErrore,e);
			throw new RuntimeException(msgErrore,e);
		}


		AbstractConsoleStartupListener.log.debug("PddMonitor Console avviata correttamente.");
		
	}




	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if(AbstractConsoleStartupListener.log!=null)
			AbstractConsoleStartupListener.log.info("Undeploy govwayMonitor Console in corso...");

		if(AbstractConsoleStartupListener.log!=null)
			AbstractConsoleStartupListener.log.info("Undeploy govwayMonitor Console effettuato.");

	}


}
