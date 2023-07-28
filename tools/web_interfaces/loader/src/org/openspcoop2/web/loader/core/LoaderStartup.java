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


package org.openspcoop2.web.loader.core;

import java.io.InputStream;
import java.util.Properties;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementParameter;
import org.openspcoop2.web.loader.config.DatasourceProperties;
import org.openspcoop2.web.loader.config.LoaderProperties;
import org.slf4j.Logger;

/**
 * Questa classe si occupa di inizializzare tutte le risorse necessarie alla
 * govwayConsole.
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class LoaderStartup implements ServletContextListener {

	//private static String IDMODULO = "ControlStation";
	private static Logger log = null;
	private static boolean initialized = false;

	public static boolean isInitialized() {
		return LoaderStartup.initialized;
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(LoaderStartup.log==null){
			LoaderStartup.log = LoggerWrapperFactory.getLogger("govway.loader");
		}
		LoaderStartup.log.info("Undeploy loader in corso...");

		LoaderStartup.initialized = false;

		LoaderStartup.log.info("Undeploy loader effettuato.");

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		String confDir = null;
		boolean appendActualConfiguration = false;
		try{
			InputStream is = LoaderStartup.class.getResourceAsStream("/loader.properties");
			try{
				if(is!=null){
					Properties p = new Properties();
					p.load(is);
					
					confDir = p.getProperty("confDirectory");
					if(confDir!=null){
						confDir = confDir.trim();
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
				}catch(Exception eClose){
					// close
				}
			}

		}catch(Exception e){
			// ignore
		}
		
		try{
			LoaderLogger.initialize(LoaderStartup.log, confDir, null, appendActualConfiguration);
			LoaderStartup.log = LoggerWrapperFactory.getLogger("govway.loader");
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		
		LoaderStartup.log.info("Inizializzazione loader in corso...");
		
		LoaderProperties.initialize(confDir,LoaderStartup.log);
		
		DatasourceProperties.initialize(confDir,LoaderStartup.log);
		
		LoaderStartup.log.info("Inizializzazione loader effettuata con successo.");
		
		LoaderStartup.log.info("Inizializzazione ExtendedInfoManager in corso...");
		try{
			ExtendedInfoManager.initialize(new Loader(), null, null, null);
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		LoaderStartup.log.info("Inizializzazione ExtendedInfoManager effettuata con successo");
		
		LoaderStartup.log.info("Inizializzazione DataElement in corso...");
		try{
			LoaderProperties loaderProperties = LoaderProperties.getInstance();
			int consoleLunghezzaLabel = loaderProperties.getConsoleLunghezzaLabel();
			DataElementParameter dep = new DataElementParameter();
			dep.setSize(consoleLunghezzaLabel);
			DataElement.initialize(dep);
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		LoaderStartup.log.info("Inizializzazione DataElement effettuata con successo");

	}

	public static void setInitialized(boolean initialized) {
		LoaderStartup.initialized = initialized;
	}

}
