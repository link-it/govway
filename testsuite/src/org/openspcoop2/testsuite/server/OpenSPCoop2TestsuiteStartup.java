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



package org.openspcoop2.testsuite.server;

import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.db.DatabaseProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.DataContentHandlerManager;


/**
 * Implementazione del punto di Startup dell'applicazione WEB
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class OpenSPCoop2TestsuiteStartup implements ServletContextListener {

	/** Context della Servlet */
	ServletContext servletContext;

	/** ServerProperties */
	private TestSuiteProperties testsuiteProperties;
	/** Logger */
	private Logger log;
	
	/**
	 * Startup dell'applicazione WEB di OpenSPCoop
	 *
	 * @param sce Servlet Context Event
	 * 
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {

		// Inizializzazione TestSuiteProperties
		if(TestSuiteProperties.isInitialized()==false){
			// Server Properties
			TestSuiteProperties.initialize();
			// Logger
			try{
				LoggerWrapperFactory.setLogConfiguration(OpenSPCoop2TestsuiteStartup.class.getResource("/"+CostantiTestSuite.LOGGER_PROPERTIES));
			}catch(Exception e){
				throw new TestSuiteException("InitLogger",e);
			}
		}
		
		// Inizializzazione DatabaseProperties
		if(DatabaseProperties.isInitialized()==false){
			DatabaseProperties.initialize();
		}
		
		// Istanza di Server Properties
		this.testsuiteProperties = TestSuiteProperties.getInstance();
		
		// Istanza di logger
		this.log=LoggerWrapperFactory.getLogger("openspcoop2.testsuite");
		
		
		if(this.testsuiteProperties.loadMailcap()){
			
			DataContentHandlerManager dchManager = new DataContentHandlerManager(this.log);
			
			dchManager.initMailcap();	
		}
		
		try{
			DateManager.initializeDataManager(org.openspcoop2.utils.date.SystemDate.class.getName(), new Properties(), this.log);
		}catch(Exception e){
			this.log.error("Inizializzazione [DateManager] failed",e);
		}

	}

	
	/**
	 * Undeploy dell'applicazione WEB di OpenSPCoop
	 *
	 * @param sce Servlet Context Event
	 * 
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {

		
	}



}
