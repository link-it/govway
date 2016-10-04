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
package org.openspcoop2.example.pdd.server.testservice;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * Startup
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Startup implements ServletContextListener { 

	public static Logger logPing;
	public static Logger logEcho;
	public static Logger logStressTest;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try{
			
			LoggerWrapperFactory.setLogConfiguration(Startup.class.getResource("/testService.log4j2.properties"));			
			logPing = LoggerWrapperFactory.getLogger("openspcoop2.ping");
			logPing.info("avviato con successo");
			logEcho = LoggerWrapperFactory.getLogger("openspcoop2.echo");
			logEcho.info("avviato con successo");
			logStressTest = LoggerWrapperFactory.getLogger("openspcoop2.stress");
			logStressTest.info("avviato con successo");
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
	}

}