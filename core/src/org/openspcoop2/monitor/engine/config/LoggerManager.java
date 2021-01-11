/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.config;

import org.openspcoop2.monitor.engine.constants.CostantiConfigurazione;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * LoggerManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoggerManager {

	private static Logger logger = LoggerWrapperFactory.getLogger(LoggerManager.class);
	
	private static boolean initialized = false;
	public static void initLogger(){
		if(LoggerManager.initialized==false){
			InputStream is = null;
			Properties config = null;
			try {
				File f = new File(CostantiConfigurazione.LOG4J_FILENAME);
				if(f.exists()){
					is = new FileInputStream(f);
				}
				else {
					f = new File("deploy"+File.separatorChar+"properties",CostantiConfigurazione.LOG4J_FILENAME);
					if(f.exists()){
						is = new FileInputStream(f);
					}
					else{
						// 
						is = LoggerManager.class.getResourceAsStream("/"+CostantiConfigurazione.LOG4J_FILENAME);
						if(is==null){
							throw new Exception("Configurazione del framework ["+CostantiConfigurazione.LOG4J_FILENAME+"] non trovata");
						}
					}
				}
				
				config = new Properties();
				config.load(is);
				LoggerWrapperFactory.setLogConfiguration(config);
				
				LoggerManager.logger.info("Sistema di logging correttamente inizializzato");
				LoggerManager.initialized = true;
				
			} catch (Exception e) {
				LoggerManager.logger.error("Errore durante l'inizializzazione del Sistema di Logging: "+e.getMessage(),e);
			}finally{
				try{
					if(is!=null)
						is.close();
				}catch(Exception eClose){}
			}
		}
	}
	
	static{
		LoggerManager.initLogger();
	}
	
}