/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.utils.cache.test;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.cache.CacheJMXUtils;

/**
 * Esempio di inizializzazione
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class ExampleServletInitListener implements ServletContextListener {

	private static boolean initialized = false;
	public static boolean isInitialized() {
		return ExampleServletInitListener.initialized;
	}
	
	public static final String CACHE_CONFIG = "config";
	public static final String CACHE_REGISTRY = "registry";
	
	public static ExampleConfigCacheWrapper configManager;
		
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		
		try{
			
			configManager = new ExampleConfigCacheWrapper(true, LoggerWrapperFactory.getLogger(ExampleServletInitListener.class));
			CacheJMXUtils.register(LoggerWrapperFactory.getLogger(ExampleServletInitListener.class),new ExampleConfigCacheJmx());

		}catch(Exception e){}
				
		
		initialized = true;
		
	}

	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		CacheJMXUtils.unregister();
		System.out.println("Undeploy effettuato.");

	}
}
