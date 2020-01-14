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
package org.openspcoop2.web.monitor.core.core;

import org.openspcoop2.core.commons.dao.DAOFactoryInstanceProperties;
import org.openspcoop2.web.monitor.core.config.ApplicationProperties;
import org.openspcoop2.web.monitor.core.utils.Costanti;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.properties.CollectionProperties;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.resources.Loader;

/**
 * InitServlet
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class InitServlet extends HttpServlet {

	private static Logger log = LoggerWrapperFactory.getLogger(InitServlet.class);


	private static final long serialVersionUID = 1L;
	
	
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
			return InitServlet.DEFAULT_APPLICATION_NAME;

		return this.applicationProperties;
	}
	public String getLocalApplicationProperties() {

		// se il loggerName non e' impostato restituisco il default
		if (this.localApplicationProperties == null
				|| "".equals(this.localApplicationProperties))
			return InitServlet.DEFAULT_LOCAL_APPLICATION_NAME;

		return this.localApplicationProperties;
	}
	public String getLocalApplicationPropertyName() {

		// se il loggerName non e' impostato restituisco il default
		if (this.localApplicationPropertyName == null
				|| "".equals(this.localApplicationPropertyName))
			return InitServlet.DEFAULT_APPLICATION_PROPERTY_NAME;

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
			return InitServlet.DEFAULT_LOGGER_NAME;

		return this.logProperties;
	}
	public String getLocalLoggerProperties() {

		// se il loggerName non e' impostato restituisco il default logger
		if (this.localLogProperties == null
				|| "".equals(this.localLogProperties))
			return InitServlet.DEFAULT_LOCAL_LOGGER_NAME;

		return this.localLogProperties;
	}
	public String getLocalLoggerPropertyName() {

		// se il loggerName non e' impostato restituisco il default logger
		if (this.localLogPropertyName == null
				|| "".equals(this.localLogPropertyName))
			return InitServlet.DEFAULT_LOGGER_PROPERTY_NAME;

		return this.localLogPropertyName;
	}
	

	private String getStringInitParameter(ServletConfig config,String name){
		String value = config.getInitParameter(name);
		if(value==null || "".equals(value)){
			return null;
		}
		return value.trim();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		// *** APPLICATION PROPERTIES ***
		
		this.setApplicationProperties(getStringInitParameter(config, "applicationProperties"));
		this.setLocalApplicationProperties(getStringInitParameter(config, "localApplicationProperties"));
		this.setLocalApplicationPropertyName(getStringInitParameter(config, "localApplicationPropertyName"));

		
		// carico il protocollo di default
		ApplicationProperties appProperties = null;
		try {
			ApplicationProperties.initialize(InitServlet.log, getApplicationProperties(), getLocalApplicationPropertyName(), getLocalApplicationProperties());
			appProperties = ApplicationProperties.getInstance(InitServlet.log);
		}catch (Exception e) {
			throw new ServletException(
					"Errore durante l'inizializzazione del ApplicationProperties "
							+ e.getMessage(),e);
		}

		
		// *** LOGGER PROPERTIES ***
		
		try {
						
			this.setLogProperties(getStringInitParameter(config, "logProperties"));
			this.setLocalLogProperties(getStringInitParameter(config, "localLogProperties"));
			this.setLocalLogPropertyName(getStringInitParameter(config, "localLogPropertyName"));
			
			// leggo log properties
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
							DAOFactoryInstanceProperties.OPENSPCOOP2_LOCAL_HOME, InitServlet.log,
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
			throw new ServletException(
					"Errore durante il caricamento del file di logging "
							+ getLoggerProperties());
		}

		

		// inizializza la ProtocolFactoryManager
		try {

			ConfigurazionePdD configPdD = new ConfigurazionePdD();
			configPdD.setAttesaAttivaJDBC(-1);
			configPdD.setCheckIntervalJDBC(-1);
			configPdD.setLoader(new Loader(this.getClass().getClassLoader()));
			configPdD.setLog(InitServlet.log);
			ProtocolFactoryManager.initialize(InitServlet.log, configPdD,
					appProperties.getProtocolloDefault());

		} catch (Exception e) {
			throw new ServletException(
					"Errore durante l'inizializzazione del ProtocolFactoryManager "
							+ e.getMessage(),e);
		}

		InitServlet.log.debug("PddMonitor Console avviata correttamente.");
	}

	public synchronized static void initLog(){
		
	}
}
