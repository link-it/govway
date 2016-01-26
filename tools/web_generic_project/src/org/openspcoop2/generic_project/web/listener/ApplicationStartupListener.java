 /*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.web.listener;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/****
* ApplicationStartupListener classe astratta base che descrive un Listener da agganciare all'avvio di un applicazione web.
* 
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
 *
 */
public abstract class ApplicationStartupListener  implements ServletContextListener{

	public static boolean initializedLog = false;
	
	public static final String APP_LOG_PROPERTIES_PATH = "application.logger.properties";
	public static final String APP_LOG_PROPERTIES_LOCAL_PATH = "application_local.logger.properties";
	public static final String APP_LOG_PROPERTIES = "APPLICATION_LOG_PROPERTIES";
	
	public final static String APP_PROPERTIES_PATH = "application.properties";
	public final static String APP_PROPERTIES_LOCAL_PATH = "application_local.properties";
	public final static String APP_PROPERTIES = "APPLICATION_PROPERTIES";
	
	/*
	 * APPLICATION
	 */
	private static final String DEFAULT_APPLICATION_NAME = "/"+APP_PROPERTIES_PATH;
	private static final String DEFAULT_LOCAL_APPLICATION_NAME = "/"+APP_PROPERTIES_LOCAL_PATH;
	private static final String DEFAULT_APPLICATION_PROPERTY_NAME = APP_PROPERTIES;
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
			return ApplicationStartupListener.DEFAULT_APPLICATION_NAME;

		return this.applicationProperties;
	}
	public String getLocalApplicationProperties() {

		// se il loggerName non e' impostato restituisco il default
		if (this.localApplicationProperties == null
				|| "".equals(this.localApplicationProperties))
			return ApplicationStartupListener.DEFAULT_LOCAL_APPLICATION_NAME;

		return this.localApplicationProperties;
	}
	public String getLocalApplicationPropertyName() {

		// se il loggerName non e' impostato restituisco il default
		if (this.localApplicationPropertyName == null
				|| "".equals(this.localApplicationPropertyName))
			return ApplicationStartupListener.DEFAULT_APPLICATION_PROPERTY_NAME;

		return this.localApplicationPropertyName;
	}
	
	/*
	 * LOGGER
	 */
	private static final String DEFAULT_LOGGER_NAME = "/"+APP_LOG_PROPERTIES_PATH;
	private static final String DEFAULT_LOCAL_LOGGER_NAME = "/"+APP_LOG_PROPERTIES_LOCAL_PATH;
	private static final String DEFAULT_LOGGER_PROPERTY_NAME = APP_LOG_PROPERTIES;
	private String loggerProperties;
	public void setLoggerProperties(String loggerName) {
		this.loggerProperties = loggerName;
	}
	private String localLoggerProperties;
	public void setLocalLoggerProperties(String localLog4jProperties) {
		this.localLoggerProperties = localLog4jProperties;
	}
	private String localLoggerPropertyName;
	public void setLocalLoggerPropertyName(String localLog4jPropertyName) {
		this.localLoggerPropertyName = localLog4jPropertyName;
	}
	public String getLoggerProperties() {

		// se il loggerName non e' impostato restituisco il default logger
		if (this.loggerProperties == null
				|| "".equals(this.loggerProperties))
			return ApplicationStartupListener.DEFAULT_LOGGER_NAME;

		return this.loggerProperties;
	}
	public String getLocalLoggerProperties() {

		// se il loggerName non e' impostato restituisco il default logger
		if (this.localLoggerProperties == null
				|| "".equals(this.localLoggerProperties))
			return ApplicationStartupListener.DEFAULT_LOCAL_LOGGER_NAME;

		return this.localLoggerProperties;
	}
	public String getLocalLoggerPropertyName() {

		// se il loggerName non e' impostato restituisco il default logger
		if (this.localLoggerPropertyName == null
				|| "".equals(this.localLoggerPropertyName))
			return ApplicationStartupListener.DEFAULT_LOGGER_PROPERTY_NAME;

		return this.localLoggerPropertyName;
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
//				ApplicationProperties appProperties = null;
//				try {
//					ApplicationProperties.initialize(ConsoleStartupListener.log, getApplicationProperties(), getLocalApplicationPropertyName(), getLocalApplicationProperties());
//					appProperties = ApplicationProperties.getInstance(ConsoleStartupListener.log);
//				}catch (Exception e) {
//					ConsoleStartupListener.log.error(
////					throw new ServletException(
//							"Errore durante l'inizializzazione del ApplicationProperties "
//									+ e.getMessage(),e);
//				}

				
				// *** LOGGER PROPERTIES ***
				
				try {
								
					this.setLoggerProperties(getStringInitParameter(ctx, "loggerProperties"));
					this.setLocalLoggerProperties(getStringInitParameter(ctx, "localLoggerProperties"));
					this.setLocalLoggerPropertyName(getStringInitParameter(ctx, "localLoggerPropertyName"));
					
					// leggo log4j properties
					//System.out.println("SET FILE CONF ["+fileConf+"]");
					//System.out.println("INIT ["+getLocalDefaultLoggerName()+"]");
					InputStream is = ApplicationStartupListener.class.getResourceAsStream(getLoggerProperties());
					Properties prop = new Properties();
					prop.load(is);

					// effettuo eventuale ovverride delle properties
					// leggo le properties eventualmente ridefinite
//					Properties overrideProp = PropertiesUtilities
//							.searchLocalImplementation(
//									DAOFactoryInstanceProperties.OPENSPCOOP2_LOCAL_HOME, ConsoleStartupListener.log,
//									this.getLocalLoggerPropertyName(),
//									this.getLocalLoggerProperties(),
//									appProperties.getConfigurationDir());
//					if (overrideProp != null) {
//						Enumeration<Object> proEnums = overrideProp.keys();
//						while (proEnums.hasMoreElements()) {
//							String key = (String) proEnums.nextElement();
//							// effettuo l'ovverride
//							prop.put(key, overrideProp.getProperty(key));
//						}
//					}
					
					// inizializzo il logger
//					PropertyConfigurator.configure(prop);
					
				} catch (Exception e) {
//					ConsoleStartupListener.log.error(
//					throw new ServletException(
//							"Errore durante il caricamento del file di logging "
//									+ getLoggerProperties());
				}

//				ConsoleStartupListener.log.debug("Console avviata correttamente.");
	}
	
	
	
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
//		if(ConsoleStartupListener.log!=null)
//			ConsoleStartupListener.log.info("Undeploy Console in corso...");

//		if(ConsoleStartupListener.log!=null)
//			ConsoleStartupListener.log.info("Undeploy Console effettuato.");
		
	}
}
