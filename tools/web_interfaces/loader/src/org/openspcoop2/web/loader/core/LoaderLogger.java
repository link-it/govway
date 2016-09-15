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

package org.openspcoop2.web.loader.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.CollectionProperties;
import org.openspcoop2.utils.resources.PropertiesUtilities;

/**
 * LoaderLogger
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoaderLogger {

	public static void initialize(Logger logConsole,String rootDirectory,Properties objectProperties,boolean appendActualConfiguration) throws IOException, UtilsException{

		// Originale
		java.util.Properties loggerProperties = new java.util.Properties();
		java.io.File loggerFile = null;
		if(rootDirectory!=null)
			loggerFile = new java.io.File(rootDirectory+"loader.log4j2.properties");
		else
			loggerFile = new java.io.File("loader.log4j2.properties");
		if(loggerFile .exists() == false ){
			loggerProperties.load(OpenSPCoop2Logger.class.getResourceAsStream("/loader.log4j2.properties"));
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
				}catch(Exception eClose){}
			}
		}

		// File Local Implementation
		CollectionProperties loggerPropertiesRidefinito =  
				PropertiesUtilities.searchLocalImplementation(CostantiPdD.OPENSPCOOP2_LOCAL_HOME,logConsole, Costanti.OPENSPCOOP2_LOGGER_PROPERTIES, Costanti.OPENSPCOOP2_LOGGER_LOCAL_PATH,  rootDirectory);
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

		if(appendActualConfiguration){
			System.out.println("[LoaderConsole] Attendo inizializzazione PdDOpenSPCoop prima di appender la configurazione Log4J ...");
			int i=0;
			int limit = 60;
			while(OpenSPCoop2Startup.initialize==false && i<limit){
				try{
					Thread.sleep(1000);
				}catch(Exception e){}
				i++;
				if(i%10==0){
					System.out.println("[LoaderConsole] Attendo inizializzazione PdDOpenSPCoop ...");
				}
			}
			
			if(OpenSPCoop2Startup.initialize==false){
				throw new UtilsException("[LoaderConsole] Inizializzazione OpenSPCoop non rilevata");
			}
			
			System.out.println("[LoaderConsole] Configurazione Log4J ...");
			LoggerWrapperFactory.setLogConfiguration(loggerProperties,true);
			System.out.println("[LoaderConsole] Configurazione Log4J aggiunta");
		}
		else{
			LoggerWrapperFactory.setLogConfiguration(loggerProperties);
		}
		


	}

}
