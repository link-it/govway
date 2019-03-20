/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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


package org.openspcoop2.core.monitor.rs.server.config;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;
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
			InputStream is = Startup.class.getResourceAsStream("/rs-api-monitor.properties");
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
			
			Startup.log.info("Inizializzazione rs api monitor in corso...");
			
			if(ServerProperties.initialize(confDir,Startup.log)==false){
				return;
			}
			
			if(DatasourceProperties.initialize(confDir,Startup.log)==false){
				return;
			}
			
			Startup.log.info("Inizializzazione DBManager in corso...");
			try{
				DatasourceProperties dbProperties = DatasourceProperties.getInstance();
				DBManager.initialize(dbProperties.getDbDataSource(), dbProperties.getDbDataSourceContext(),
						dbProperties.getDbTipoDatabase(), dbProperties.isShowSql());
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
			Startup.log.info("Inizializzazione DBManager effettuata con successo");
			
			Startup.log.info("Inizializzazione ExtendedInfoManager in corso...");
			try{
				ExtendedInfoManager.initialize(new Loader(), null, null, null);
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
			Startup.log.info("Inizializzazione ExtendedInfoManager effettuata con successo");
			
			Startup.log.info("Inizializzazione ProtocolFactoryManager in corso...");
			ServerProperties properties = null;
			try {
				properties = ServerProperties.getInstance();
				ConfigurazionePdD configPdD = new ConfigurazionePdD();
				configPdD.setAttesaAttivaJDBC(-1);
				configPdD.setCheckIntervalJDBC(-1);
				configPdD.setLoader(new Loader(Startup.class.getClassLoader()));
				configPdD.setLog(Startup.log);
				ProtocolFactoryManager.initialize(Startup.log, configPdD,
						properties.getProtocolloDefault());
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			}
			Startup.log.info("ProtocolFactoryManager DBManager effettuata con successo");
			
						
			Startup.initializedResources = true;
			
			Startup.log.info("Inizializzazione rs api monitor effettuata con successo.");
		}
	}

}
