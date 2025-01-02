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

package org.openspcoop2.core.transazioni.ws.server.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
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

	public static void initialize(Logger logConsole,String rootDirectory,Properties objectProperties) throws IOException, UtilsException{

		// Originale
		java.util.Properties loggerProperties = new java.util.Properties();
		java.io.File loggerFile = null;
		if(rootDirectory!=null)
			loggerFile = new java.io.File(rootDirectory+"wstransazioni.log4j2.properties");
		else
			loggerFile = new java.io.File("wstransazioni.log4j2.properties");
		if(loggerFile .exists() == false ){
			loggerProperties.load(LoggerProperties.class.getResourceAsStream("/wstransazioni.log4j2.properties"));
		}else{
			FileInputStream fin = null;
			try{
				fin = new java.io.FileInputStream(loggerFile);
				loggerProperties.load(fin);
			}finally{
				try{
					if(fin!=null){
						fin.close();
					}
				}catch(Exception eClose){
					// close
				}
			}
		}

		// File Local Implementation
		CollectionProperties loggerPropertiesRidefinito =  
				PropertiesUtilities.searchLocalImplementation(ConstantsEnv.OPENSPCOOP2_LOCAL_HOME,logConsole, ConstantsEnv.OPENSPCOOP2_LOGGER_PROPERTIES, ConstantsEnv.OPENSPCOOP2_LOGGER_LOCAL_PATH,  rootDirectory);
		if(loggerPropertiesRidefinito!=null && loggerPropertiesRidefinito.size()>0){
			Enumeration<?> ridefinito = loggerPropertiesRidefinito.keys();
			while (ridefinito.hasMoreElements()) {
				String key = (String) ridefinito.nextElement();
				String value = (String) loggerPropertiesRidefinito.get(key);
				if(loggerProperties.containsKey(key)){
					//Object o = 
					loggerProperties.remove(key);
				}
				loggerProperties.put(key, value);
				//System.out.println("CHECK NUOVO VALORE: "+loggerProperties.get(key));
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
				//System.out.println("CHECK NUOVO VALORE: "+loggerProperties.get(key));
			}
		}

		LoggerWrapperFactory.setLogConfiguration(loggerProperties);

	}

	
	public static Logger getLoggerWS(){
		if(WSStartup.initializedLog==false){
			WSStartup.initLog();
		}
		return LoggerWrapperFactory.getLogger("transazioni.ws");
	}
	
	public static Logger getLoggerDAO(){
		return LoggerWrapperFactory.getLogger("transazioni.dao");
	}
}
