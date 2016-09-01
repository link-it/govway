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


package org.openspcoop2.web.ctrlstat.core;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.message.XMLDiff;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.xml.XMLDiffImplType;
import org.openspcoop2.utils.xml.XMLDiffOptions;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.config.DatasourceProperties;
import org.openspcoop2.web.ctrlstat.config.RegistroServiziRemotoProperties;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB_LIB;
import org.openspcoop2.web.ctrlstat.gestori.GestoriStartupThread;
import org.openspcoop2.web.lib.queue.config.QueueProperties;

/**
 * Questa classe si occupa di inizializzare tutte le risorse necessarie alla
 * pddConsole.
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class InitListener implements ServletContextListener {

	//private static String IDMODULO = "ControlStation";
	protected static Logger log = null;
	private static boolean initialized = false;
	static {
		InitListener.log = LoggerWrapperFactory.getLogger(InitListener.class);
	}

	public static boolean isInitialized() {
		return InitListener.initialized;
	}

	private GestoriStartupThread gestoriStartupThread;
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		InitListener.log.info("Undeploy pddConsole in corso...");

		InitListener.initialized = false;
		
        // Fermo i Gestori
		if(this.gestoriStartupThread!=null){
			this.gestoriStartupThread.stopGestori();
		}

		InitListener.log.info("Undeploy pddConsole effettuato.");

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		String confDir = null;
		String confPropertyName = null;
		String confLocalPathPrefix = null;
		boolean appendActualConfiguration = false;
		try{
			InputStream is = InitListener.class.getResourceAsStream("/console.properties");
			try{
				if(is!=null){
					Properties p = new Properties();
					p.load(is);
					
					confDir = p.getProperty("confDirectory");
					if(confDir!=null){
						confDir = confDir.trim();
					}
					
					confPropertyName = p.getProperty("confPropertyName");
					if(confPropertyName!=null){
						confPropertyName = confPropertyName.trim();
					}
					
					confLocalPathPrefix = p.getProperty("confLocalPathPrefix");
					if(confLocalPathPrefix!=null){
						confLocalPathPrefix = confLocalPathPrefix.trim();
					}
					
					String tmpAppendActualConfiguration = p.getProperty("appendLog4j");
					if(tmpAppendActualConfiguration!=null){
						appendActualConfiguration = "true".equalsIgnoreCase(tmpAppendActualConfiguration.trim());
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
				
		try{
			ControlStationLogger.initialize(InitListener.log, confDir, confPropertyName, confLocalPathPrefix, null, appendActualConfiguration);
			InitListener.log = ControlStationLogger.getPddConsoleCoreLogger();
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		
		
		InitListener.log.info("Inizializzazione resources pddConsole in corso...");
		ConsoleProperties consoleProperties = null;
		try{
		
			if(ConsoleProperties.initialize(confDir, confPropertyName, confLocalPathPrefix,InitListener.log)==false){
				throw new Exception("ConsoleProperties not initialized");
			}
			consoleProperties = ConsoleProperties.getInstance();
			
			if(DatasourceProperties.initialize(confDir, confPropertyName, confLocalPathPrefix,InitListener.log)==false){
				throw new Exception("DatasourceProperties not initialized");
			}
			
			if(consoleProperties.isSinglePdD_RegistroServiziLocale()==false){
				if(RegistroServiziRemotoProperties.initialize(confDir, confPropertyName, confLocalPathPrefix,InitListener.log)==false){
					throw new Exception("RegistroServiziRemotoProperties not initialized");
				}
			}
			
			if(consoleProperties.isSinglePdD()==false){
				if(QueueProperties.initialize(confDir,InitListener.log)==false){
					throw new Exception("QueueProperties not initialized");
				}
			}
			
			Connettori.initialize(InitListener.log);
			
			DriverControlStationDB_LIB.initialize(InitListener.log);
						
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		InitListener.log.info("Inizializzazione resources pddConsole effettuata con successo.");

		
		InitListener.log.info("Inizializzazione ExtendedInfoManager in corso...");
		try{
			ExtendedInfoManager.initialize(new Loader(), 
					consoleProperties.getExtendedInfoDriverConfigurazione(), 
					consoleProperties.getExtendedInfoDriverPortaDelegata(), 
					consoleProperties.getExtendedInfoDriverPortaApplicativa());
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		InitListener.log.info("Inizializzazione ExtendedInfoManager effettuata con successo");
		
		InitListener.log.info("Inizializzazione XMLDiff in corso...");
		try{
			XMLDiff diff = new XMLDiff();
			diff.initialize(XMLDiffImplType.XML_UNIT, new XMLDiffOptions());
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		InitListener.log.info("Inizializzazione XMLDiff effettuata con successo");

		try{
			if(consoleProperties.isSinglePdD()==false){
				InitListener.log.info("Inizializzazione Gestori, della pddConsole Centralizzata, in corso...");
			
                this.gestoriStartupThread = new GestoriStartupThread();
                new Thread(this.gestoriStartupThread).start();
				
				InitListener.log.info("Inizializzazione Gestori, della pddConsole Centralizzata, effettuata con successo.");
			}
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		
	}

	public static void setInitialized(boolean initialized) {
		InitListener.initialized = initialized;
	}

}
