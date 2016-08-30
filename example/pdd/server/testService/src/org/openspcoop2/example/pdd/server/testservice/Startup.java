package org.openspcoop2.example.pdd.server.testservice;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

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