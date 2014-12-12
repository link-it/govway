/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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
package org.openspcoop2.web.ctrlstat.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.resources.PropertiesUtilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;

/**
 * ControlStationLogger
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ControlStationLogger {

	public static void initialize(Logger logConsole,String rootDirectory,Properties objectProperties) throws IOException{

		// Originale
		java.util.Properties loggerProperties = new java.util.Properties();
		java.io.File loggerFile = null;
		if(rootDirectory!=null)
			loggerFile = new java.io.File(rootDirectory+"console.log4j.properties");
		else
			loggerFile = new java.io.File("console.log4j.properties");
		if(loggerFile .exists() == false ){
			loggerProperties.load(OpenSPCoop2Logger.class.getResourceAsStream("/console.log4j.properties"));
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
		java.util.Properties loggerPropertiesRidefinito =  
				PropertiesUtilities.searchLocalImplementation(CostantiPdD.OPENSPCOOP2_LOCAL_HOME,logConsole, CostantiControlStation.OPENSPCOOP2_LOGGER_PROPERTIES, CostantiControlStation.OPENSPCOOP2_LOGGER_LOCAL_PATH, rootDirectory);
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

		PropertyConfigurator.configure(loggerProperties);


	}

	public static Logger getPddConsoleCoreLogger(){
		 return Logger.getLogger("pddConsole.core");
	}
	
	public static Logger getSmistatoreLogger(){
		 return Logger.getLogger("pddConsole.gestori.smistatore");
	}
	
	public static Logger getGestorePddLogger(){
		 return Logger.getLogger("pddConsole.gestori.config");
	}
	
	public static Logger getGestoreGELogger(){
		 return Logger.getLogger("pddConsole.gestori.gestoreEventi");
	}
	
	public static Logger getGestoreRegistroLogger(){
		 return Logger.getLogger("pddConsole.gestori.registry");
	}
	
	public static Logger getGestoreAutorizzazioneLogger(){
		 return Logger.getLogger("pddConsole.gestori.auth");
	}
	
	public static Logger getDriverDBPddConsoleLogger(){
		return Logger.getLogger(CostantiControlStation.DRIVER_DB_PDD_CONSOLE_LOGGER);
	}
	
	public static Logger getSincronizzatoreLogger(){
		 return Logger.getLogger("pddConsole.sincronizzatore");
	}
	
}
