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


package org.openspcoop2.core.config.rs.server.config;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.web.ctrlstat.core.Connettori;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
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

public class Startup implements ServletContextListener {

	private static Logger log = null;
	
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(Startup.log!=null)
			Startup.log.info("Undeploy webService in corso...");

		if(Startup.log!=null)
			Startup.log.info("Undeploy webService effettuato.");

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		Startup.initLog();
		
		Startup.initResources();

	}
	
	
	// LOG
	
	public static boolean initializedLog = false;
	
	public static synchronized String initLog(){
		
		String confDir = null;
		try{
			InputStream is = Startup.class.getResourceAsStream("/rs-api-config.properties");
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
		
		if(Startup.initializedLog==false){
			
			try{
				Startup.log = LoggerWrapperFactory.getLogger(Startup.class);
				LoggerProperties.initialize(Startup.log, confDir, null);
				Startup.initializedLog = true;
				Startup.log = LoggerProperties.getLoggerCore();
				
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		
		return confDir;
	}
	
	
	// RESOURCES
	
	public static boolean initializedResources = false;
	
	public static synchronized void initResources(){
		if(Startup.initializedResources==false){
			
			String confDir = Startup.initLog();
			
			Startup.log.info("Inizializzazione rs api config in corso...");
			
			if(ServerProperties.initialize(confDir,Startup.log)==false){
				return;
			}
			
			if(DatasourceProperties.initialize(confDir,Startup.log)==false){
				return;
			}
			try {
				if(org.openspcoop2.web.ctrlstat.config.DatasourceProperties.initialize(DatasourceProperties.getInstance().getPropertiesConsole(),Startup.log)==false){
					return;
				}
			}catch(Exception e) {
				Startup.log.error("Inizializzazione database console fallita: "+e.getMessage(),e);
			}
			
			Startup.log.info("Inizializzazione ExtendedInfoManager in corso...");
			try{
				ExtendedInfoManager.initialize(new Loader(), null, null, null);
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
			Startup.log.info("Inizializzazione ExtendedInfoManager effettuata con successo");
			
			Startup.log.info("Inizializzazione Connettori in corso...");
			try{
				Connettori.initialize(log, true, confDir, ServerProperties.getInstance().getProtocolloDefault());
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
			Startup.log.info("Inizializzazione Connettori effettuata con successo");
			
			Startup.log.info("Inizializzazione Risorse Statiche Console in corso...");
			try {
				ServerProperties serverProperties = ServerProperties.getInstance();
				
				ControlStationCore.setUtenzePasswordEncryptEngine_apiMode(serverProperties.getUtenzeCryptConfig());
				
				ControlStationCore.setApplicativiPasswordEncryptEngine_apiMode(serverProperties.getApplicativiCryptConfig());
				if(serverProperties.isApplicativiBasicPasswordEnableConstraints()) {
					ControlStationCore.setApplicativiPasswordVerifierEngine_apiMode(serverProperties.getApplicativiPasswordVerifier());
				}
				
				ControlStationCore.setSoggettiPasswordEncryptEngine_apiMode(serverProperties.getSoggettiCryptConfig());
				if(serverProperties.isSoggettiBasicPasswordEnableConstraints()) {
					ControlStationCore.setSoggettiPasswordVerifierEngine_apiMode(serverProperties.getSoggettiPasswordVerifier());
				}
				
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			}
			Startup.log.info("Inizializzazione Risorse Statiche Console effettuata con successo");
			
			Startup.initializedResources = true;
			
			Startup.log.info("Inizializzazione rs api config effettuata con successo.");
		}
	}

}
