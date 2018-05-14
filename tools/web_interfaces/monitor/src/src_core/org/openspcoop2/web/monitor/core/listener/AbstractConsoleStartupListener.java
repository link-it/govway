package org.openspcoop2.web.monitor.core.listener;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.properties.CollectionProperties;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.resources.Loader;

import it.link.pdd.core.DAO;
import org.openspcoop2.core.commons.dao.DAOFactoryInstanceProperties;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.web.monitor.core.config.ApplicationProperties;
import org.openspcoop2.web.monitor.core.core.InitServlet;
import org.openspcoop2.web.monitor.core.dao.DAOFactoryInitializer;
import org.openspcoop2.web.monitor.core.utils.Costanti;
import it.link.pdd.tools.license.LicenseModuleReader;
import it.link.pddoe.license.PddOE;
import it.link.pddoe.license.constants.CodiceProdotto;
import it.link.pddoe.license.constants.TipoModulo;
import it.link.pddoe.license.constants.TipoProdotto;
import it.link.pddoe.license.utils.ConverterUtilities;

public abstract class AbstractConsoleStartupListener implements ServletContextListener{

	public static Boolean licenseReadStartup = null;
	public static PddOE license = null;
	public static String titoloPddMonitor = null;
	
	public static boolean isEnabledModule(TipoModulo tipoModulo) throws Exception{
		if(AbstractConsoleStartupListener.license==null){
			return false;
		}
		return LicenseModuleReader.isEnabledModule(AbstractConsoleStartupListener.license, tipoModulo);
	}
	public static boolean isEnabledAlmostOneModule() throws Exception{
		if(AbstractConsoleStartupListener.license==null){
			return false;
		}
		return LicenseModuleReader.isEnabledAlmostOneModule(AbstractConsoleStartupListener.license);
	}
	public static TipoProdotto getLicenseProductType() throws Exception{
		if(AbstractConsoleStartupListener.license==null){
			return null;
		}
		return ConverterUtilities.toTipoProdotto(CodiceProdotto.toEnumConstant(AbstractConsoleStartupListener.license.getProdotto().getCodice()));
	}

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


	private ConsoleStartupThread th;

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

			this.setLogProperties(getStringInitParameter(ctx, "logProperties"));
			this.setLocalLogProperties(getStringInitParameter(ctx, "localLogProperties"));
			this.setLocalLogPropertyName(getStringInitParameter(ctx, "localLogPropertyName"));

			// leggo properties
			//System.out.println("SET FILE CONF ["+fileConf+"]");
			//System.out.println("INIT ["+getLocalDefaultLoggerName()+"]");
			InputStream is = InitServlet.class
					.getResourceAsStream(getLoggerProperties());
			Properties prop = new Properties();
			prop.load(is);

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
					prop.put(key, overrideProp.getProperty(key));
				}
			}

			// inizializzo il logger
			LoggerWrapperFactory.setLogConfiguration(prop);

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


		// Inizializzaizone asincrona
		this.th = new ConsoleStartupThread();
		new Thread(this.th).start();
	}




	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if(AbstractConsoleStartupListener.log!=null)
			AbstractConsoleStartupListener.log.info("Undeploy pddMonitor Console in corso...");

		if(AbstractConsoleStartupListener.log!=null)
			AbstractConsoleStartupListener.log.info("Undeploy pddMonitor Console effettuato.");

	}


	class ConsoleStartupThread implements Runnable {
		public ConsoleStartupThread() {                        
		}

		@Override
		public void run() {



			try{
				updatePddOE_License(null);
			}catch (Exception e) {
				AbstractConsoleStartupListener.log.error(
						//						throw new ServletException(
						"Errore durante l'inizializzazione della licenza "
						+ e.getMessage(),e);
			}

			AbstractConsoleStartupListener.licenseReadStartup = true;
			AbstractConsoleStartupListener.log.debug("PddMonitor Console avviata correttamente.");

		}
	}


	public static void updatePddOE_License(byte [] licenzaCliente) throws Exception{

		ApplicationProperties appProperties = ApplicationProperties.getInstance(log);
		String classLicenseValidator = appProperties.getProperty("pddMonitorLicenseValidator", true, true);
		if(classLicenseValidator==null){
			throw new Exception("ClassLicenseValidator ["+classLicenseValidator+"] not found");
		}

		// Classe per validare la licenza
		Class<?> c = null;
		try{
			c = Class.forName(classLicenseValidator);
		}catch(Exception e){
			throw new Exception("[ClassForName] "+e.getMessage(),e);
		}
		//System.out.println("Class["+classLicenseValidator+"]");
		Method m = null;
		try{
			Method [] ms =c.getMethods();
			if(ms==null || ms.length<=0){
				throw new Exception("Non esistono metodi");
			}
			for (int i = 0; i < ms.length; i++) {
				Type[]types = ms[i].getGenericParameterTypes();

				if(types!=null && types.length==3){
	
					//System.out.println("Metodo potenziale ["+types[0].toString()+"] ["+types[1].toString()+"] ["+types[2].toString()+"]");

					if(licenzaCliente!=null){
						if( (types[0].toString().equals("class org.slf4j.Logger") || types[0].toString().equals("interface org.slf4j.Logger")) && 
								(types[1].toString().equals("class [B")) && 
								(types[2].toString().equals("boolean")) ){
							m = ms[i];
							break;
						}
					}else{
						if( (types[0].toString().equals("class org.slf4j.Logger") || types[0].toString().equals("interface org.slf4j.Logger")) && 
								(types[1].toString().equals("class org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager")) && 
								(types[2].toString().equals("boolean")) ){
							m = ms[i];
							break;
						}
					}
				}

			}

			if(m==null){
				throw new Exception("Metodo non trovato (licenzaCliente:"+(licenzaCliente!=null)+")");
			}

			//			if(licenzaCliente!=null){
			//				m = c.getMethod("validateLicense", Logger.class, byte[].class, boolean.class);
			//			}
			//			else{
			//				m = c.getMethod("validateLicense", Logger.class, org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager.class, boolean.class);
			//			}

		}catch(Exception e){
			throw new Exception("[getMethod] "+e.getMessage(),e);
		}
		Object o = null;
		try{
			if(licenzaCliente!=null){
				o = m.invoke(null, log, licenzaCliente,true);
			}else{
				o = m.invoke(null, log, DAOFactoryInitializer.getInstanceDAOFactory().getServiceManager(DAO.UTILS),true);
			}
		}catch(Exception e){
			throw new Exception("[invoke] "+e.getMessage(),e);
		}
		if(o==null){
			throw new Exception("License not found");
		}

		// Recupero dati licenza
		Method mDatiLicenza = null;
		try{
			mDatiLicenza = o.getClass().getMethod("getDatiLicenzaAttiva");
		}catch(Exception e){
			throw new Exception("[getMethod_DatiLicenzaAttiva] "+e.getMessage(),e);
		}
		Object oDatiLicenza = null;
		try{
			oDatiLicenza = mDatiLicenza.invoke(o);
		}catch(Exception e){
			throw new Exception("[invoke_DatiLicenzaAttiva] "+e.getMessage(),e);
		}
		if(oDatiLicenza==null){
			throw new Exception("LicenseInfo not found");
		}
		if( !(oDatiLicenza instanceof PddOE) ){
			throw new Exception("LicenseInfo with wrong type ["+oDatiLicenza.getClass().getName()+"], excepected: "+PddOE.class.getName());
		}
		AbstractConsoleStartupListener.license = (PddOE) oDatiLicenza;

		// Recupero titolo PddMonitor
		Method mTitoloPddMonitor = null;
		try{
			mTitoloPddMonitor = o.getClass().getMethod("getTitlePddMonitor");
		}catch(Exception e){
			throw new Exception("[getMethod_TitoloPddMonitor] "+e.getMessage(),e);
		}
		Object oTitoloPddMonitor = null;
		try{
			oTitoloPddMonitor = mTitoloPddMonitor.invoke(o);
		}catch(Exception e){
			throw new Exception("[invoke_TitoloPddMonitor] "+e.getMessage(),e);
		}
		if(oTitoloPddMonitor==null){
			throw new Exception("TitoloPddMonitor not found");
		}
		if( !(oTitoloPddMonitor instanceof String) ){
			throw new Exception("TitoloPddMonitor with wrong type ["+oTitoloPddMonitor.getClass().getName()+"], excepected: "+String.class.getName());
		}
		AbstractConsoleStartupListener.titoloPddMonitor = (String) oTitoloPddMonitor;


	}

	public static void resetPddOE_License() {
		AbstractConsoleStartupListener.license = null;
		AbstractConsoleStartupListener.titoloPddMonitor = null;
	}
}
