/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.pdd.monitor.ws.server.config;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;
/**
 * Questa classe si occupa di inizializzare tutte le risorse necessarie al webService.
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class WSStartup implements ServletContextListener {

	private static Logger log = null;
	
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(WSStartup.log!=null)
			WSStartup.log.info("Undeploy webService in corso...");

		if(WSStartup.log!=null)
			WSStartup.log.info("Undeploy webService effettuato.");

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		WSStartup.initLog();
		
		WSStartup.initResources();

	}
	
	
	// LOG
	
	public static boolean initializedLog = false;
	
	public static synchronized String initLog(){
		
		String confDir = null;
		try{
			InputStream is = WSStartup.class.getResourceAsStream("/wsmonitor.properties");
			try{
				if(is!=null){
					Properties p = new Properties();
					p.load(is);
					confDir = p.getProperty("confDirectory");
					if(confDir!=null){
						confDir = confDir.trim();
					}
				}
			}finally{
				try{
					if(is!=null){
						is.close();
					}
				}catch(Exception eClose){}
			}

		}catch(Exception e){}
		
		if(WSStartup.initializedLog==false){
			
			try{
				WSStartup.log = LoggerWrapperFactory.getLogger(WSStartup.class);
				LoggerProperties.initialize(WSStartup.log, confDir, null);
				WSStartup.initializedLog = true;
				WSStartup.log = LoggerProperties.getLoggerWS();
				
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		
		return confDir;
	}
	
	
	// RESOURCES
	
	public static boolean initializedResources = false;
	
	public static synchronized void initResources(){
		if(WSStartup.initializedResources==false){
			
			String confDir = WSStartup.initLog();
			
			WSStartup.log.info("Inizializzazione ws monitor in corso...");
			
			if(ServerProperties.initialize(confDir,WSStartup.log)==false){
				return;
			}
			
			if(DatasourceProperties.initialize(confDir,WSStartup.log)==false){
				return;
			}
			
			if(DriverMonitor.initialize(WSStartup.log)==false){
				return;
			}
			
			WSStartup.initializedResources = true;
			
			WSStartup.log.info("Inizializzazione ws monitor effettuata con successo.");
		}
	}

}
