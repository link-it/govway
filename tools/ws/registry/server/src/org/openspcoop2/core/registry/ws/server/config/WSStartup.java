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


package org.openspcoop2.core.registry.ws.server.config;

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
		
		initLog();
		
		initResources();

	}
	
	
	// LOG
	
	public static boolean initializedLog = false;
	
	public static synchronized String initLog(){
		
		String confDir = null;
		try{
			InputStream is = WSStartup.class.getResourceAsStream("/wsregistry.properties");
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
				initializedLog = true;
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
		if(initializedResources==false){
			
			String confDir = initLog();
			
			WSStartup.log.info("Inizializzazione ws registry in corso...");
			
			if(ServerProperties.initialize(confDir,WSStartup.log)==false){
				return;
			}
			
			if(BackendProperties.initialize(confDir,WSStartup.log)==false){
				return;
			}
			
			if(DriverRegistroServizi.initialize(WSStartup.log)==false){
				return;
			}
			
			initializedResources = true;
			
			WSStartup.log.info("Inizializzazione ws registry effettuata con successo.");
		}
	}

}
