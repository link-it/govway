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

package org.openspcoop2.core.config.rs.server.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.utils.Costanti;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.LoggerFactory;
import org.openspcoop2.utils.logger.config.DiagnosticConfig;
import org.openspcoop2.utils.logger.config.Log4jConfig;
import org.openspcoop2.utils.logger.config.MultiLoggerConfig;
import org.openspcoop2.utils.logger.log4j.Log4jLoggerWithApplicationContext;
import org.openspcoop2.utils.properties.CollectionProperties;
import org.openspcoop2.utils.properties.PropertiesUtilities;

/**
 * LoggerProperties
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoggerProperties {
	
	private LoggerProperties() {}

	public static void initialize(Logger logConsole,String rootDirectory,Properties objectProperties) throws IOException, UtilsException{

		// Originale
		java.util.Properties loggerProperties = new java.util.Properties();
		java.io.File loggerFile = null;
		if(rootDirectory!=null)
			loggerFile = new java.io.File(rootDirectory+"rs-api-config.log4j2.properties");
		else
			loggerFile = new java.io.File("rs-api-config.log4j2.properties");
		if(!loggerFile .exists() ){
			loggerProperties.load(LoggerProperties.class.getResourceAsStream("/rs-api-config.log4j2.properties"));
		}else{
			try (FileInputStream fin = new java.io.FileInputStream(loggerFile);){
				loggerProperties.load(fin);
			}
		}

		// File Local Implementation
		CollectionProperties loggerPropertiesRidefinito =  
				PropertiesUtilities.searchLocalImplementation(Costanti.OPENSPCOOP2_LOCAL_HOME,logConsole, ConstantsEnv.OPENSPCOOP2_LOGGER_PROPERTIES, ConstantsEnv.OPENSPCOOP2_LOGGER_LOCAL_PATH,  rootDirectory);
		if(loggerPropertiesRidefinito!=null && loggerPropertiesRidefinito.size()>0){
			Enumeration<?> ridefinito = loggerPropertiesRidefinito.keys();
			while (ridefinito.hasMoreElements()) {
				String key = (String) ridefinito.nextElement();
				String value = loggerPropertiesRidefinito.get(key);
				if(loggerProperties.containsKey(key)){
					//Object o = 
					loggerProperties.remove(key);
				}
				loggerProperties.put(key, value);
				/**System.out.println("CHECK NUOVO VALORE ["+key+"]: "+loggerProperties.get(key));*/
			}
		}

		// File Object Implementation
		if(objectProperties!=null && objectProperties.size()>0){
			Enumeration<?> ridefinito = objectProperties.keys();
			while (ridefinito.hasMoreElements()) {
				String key = (String) ridefinito.nextElement();
				String value = (String) objectProperties.get(key);
				if(loggerProperties.containsKey(key)){
					//Object o = 
					loggerProperties.remove(key);
				}
				loggerProperties.put(key, value);
				/**System.out.println("CHECK NUOVO VALORE ["+key+"]: "+loggerProperties.get(key));*/
			}
		}

		// DIAGNOSTIC CONFIGURATION
		DiagnosticConfig diagnosticConfig = DiagnosticConfig.newEmptyDiagnosticConfig();
		diagnosticConfig.setThrowExceptionPlaceholderFailedResolution(true);
				
				
		// LOG4J CONFIGURATION
		Log4jConfig log4jConfig = new Log4jConfig();
		log4jConfig.setLog4jConfigProperties(loggerProperties);

		// MULTILOGGER
				
		MultiLoggerConfig mConfig = new MultiLoggerConfig();
		
		mConfig.setDiagnosticConfig(diagnosticConfig);
				
		/**mConfig.setDiagnosticSeverityFilter(Severity.DEBUG_HIGH);
		mConfig.setEventSeverityFilter(Severity.INFO);*/
				
		mConfig.setLog4jLoggerEnabled(true);
		mConfig.setLog4jConfig(log4jConfig);
				
		mConfig.setDbLoggerEnabled(false);
		/**mConfig.setDatabaseConfig(dbConfig);*/
			
		try {
			LoggerFactory.initialize(Log4jLoggerWithApplicationContext.class.getName(),
					mConfig);
			
			LoggerFactory.newLogger(); // inizializza la configurazione log4j
			/**LoggerWrapperFactory.setLogConfiguration(loggerProperties);*/
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
	}

	
	public static Logger getLoggerCore(){
		if(!Startup.initializedLog){
			Startup.initLog();
		}
		return LoggerWrapperFactory.getLogger("config.core");
	}
	
	public static Logger getLoggerDAO(){
		return LoggerWrapperFactory.getLogger("config.dao");
	}
}
