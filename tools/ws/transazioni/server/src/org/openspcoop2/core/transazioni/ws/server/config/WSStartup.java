/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;
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
			InputStream is = WSStartup.class.getResourceAsStream("/wstransazioni.properties");
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
				}catch(Exception eClose){
					// close
				}
			}

		}catch(Exception e){
			// ignore
		}
		
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
			
			WSStartup.log.info("Inizializzazione ws transazioni in corso...");
			
			if(ServerProperties.initialize(confDir,WSStartup.log)==false){
				return;
			}
			
			if(DatasourceProperties.initialize(confDir,WSStartup.log)==false){
				return;
			}
			
			ConfigurazionePdD config = new ConfigurazionePdD();
			config.setLoader(new Loader());
			config.setLog(WSStartup.log);
			try{
				ProtocolFactoryManager.initialize(WSStartup.log, config, ServerProperties.getInstance().readProperty(false, "protocolloDefault"));
			}catch(Exception e){
				WSStartup.log.error(e.getMessage(),e);
				return;
			}
			
			if(DriverTransazioni.initialize(WSStartup.log)==false){
				return;
			}
			
			WSStartup.initializedResources = true;
			
			WSStartup.log.info("Inizializzazione ws transazioni effettuata con successo.");
		}
	}

}
