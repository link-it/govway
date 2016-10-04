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



package org.openspcoop2.pools.pdd;


import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.openspcoop2.pools.pdd.jmx.GestoreRisorseJMX;
import org.openspcoop2.utils.LoggerWrapperFactory;


/**
 * Implementazione del punto di Startup dell'applicazione WEB
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class OpenSPCoop2PoolsStartup implements ServletContextListener {


	/** Variabile che indica il Nome del modulo attuale di OpenSPCoop */
	@SuppressWarnings("unused")
	private static final String ID_MODULO = "Inizializzazione Risorse OpenSPCoop (Pools)";

	/** Logger utilizzato per segnalazione di errori. */
	private static Logger logger = //LogUtilities.getLogger("InizializzazioneOpenSPCoop");
			null;
	
	private static Logger loggerConsole = //LogUtilities.getLogger(OpenSPCoop2PoolsStartup.ID_MODULO);
			null;

	
	/** Context della Servlet */
	ServletContext servletContext;

	/** Indicazione su una corretta inizializzazione */
	public static boolean initialize = false;
	
	/** Gestore Risorse di Sistema */
	private GestoreRisorseSistema gestoreRisorseSistema;

	/** Gestore risorse JMX */
	private GestoreRisorseJMX gestoreRisorseJMX = null;
	
		/**
	 * Startup dell'applicazione WEB di OpenSPCoop
	 *
	 * @param sce Servlet Context Event
	 * 
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {

		long startDate = System.currentTimeMillis();

		/* ------  Ottiene il servletContext --------*/
		this.servletContext = sce.getServletContext();


		
		
		
		/* ------------- Inizializzo il sistema di Logging di OpenSPCoop --------------- */
		try{
			java.util.Properties loggerProperties = new java.util.Properties();
			loggerProperties.load(OpenSPCoop2PoolsStartup.class.getResourceAsStream("/openspcoop2_pools.log4j2.properties"));
			
			boolean appendActualConfiguration = false;
			try{
				InputStream is = OpenSPCoop2PoolsStartup.class.getResourceAsStream("/openspcoop2_pools.properties");
				try{
					if(is!=null){
						Properties p = new Properties();
						p.load(is);
						
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
			
			if(appendActualConfiguration){
				System.out.println("[OpenSPCoop2Pools] Attendo inizializzazione PdDOpenSPCoop prima di appender la configurazione Log4J ...");
				int i=0;
				int limit = 60;
				Class<?> openspcoop2Startup = Class.forName("org.openspcoop2.pdd.services.OpenSPCoop2Startup");
				Object o = openspcoop2Startup.getField("initializeLog").get(null);
				boolean initialize = false;
				if(o!=null){
					initialize = (Boolean) o;
				}
				while(initialize==false && i<limit){
					try{
						Thread.sleep(1000);
					}catch(Exception e){}
					
					o = openspcoop2Startup.getField("initializeLog").get(null);
					if(o!=null){
						initialize = (Boolean) o;
					}
					
					i++;
					if(i%10==0){
						System.out.println("[OpenSPCoop2Pools] Attendo inizializzazione PdDOpenSPCoop ...");
					}
				}
				
				if(initialize==false){
					throw new Exception("[OpenSPCoop2Pools] Inizializzazione OpenSPCoop non rilevata");
				}
				
				System.out.println("[OpenSPCoop2Pools] Configurazione Log4J ...");
				LoggerWrapperFactory.setLogConfiguration(loggerProperties,true);
				System.out.println("[OpenSPCoop2Pools] Configurazione Log4J aggiunta");
			}
			else{
				LoggerWrapperFactory.setLogConfiguration(loggerProperties);
			}		
						
		}catch(Exception e){
			OpenSPCoop2PoolsStartup.loggerConsole = LoggerWrapperFactory.getLogger("openspcoop2Pools");
			OpenSPCoop2PoolsStartup.loggerConsole.error("Inizializzazione logger non riuscita",e);
		}
			
		
		OpenSPCoop2PoolsStartup.logger = LoggerWrapperFactory.getLogger("openspcoop2Pools");
		OpenSPCoop2PoolsStartup.loggerConsole = LoggerWrapperFactory.getLogger("openspcoop2Pools");

		
		
		
		/* ------------- Verifica Proprieta' per l'impostazione delle risorse --------------- */
		if( OpenSPCoop2PoolsProperties.initialize() == false){
			OpenSPCoop2PoolsStartup.logger.error("Riscontrato errore durante l'inizializzazione del reader di 'openspcoop2_pools.properties'");
			OpenSPCoop2PoolsStartup.loggerConsole.error("Riscontrato errore durante l'inizializzazione del reader di 'openspcoop2_pools.properties'");
			return;
		}
		OpenSPCoop2PoolsProperties propertiesReader = OpenSPCoop2PoolsProperties.getInstance();
		if(propertiesReader.validaConfigurazione() == false){
			return;
		}

		

		/* ----------- Inizializzo risorse di sistema --------------- */
		String tipo = "xml";
		if( GestoreRisorseSistema.initialize(tipo, propertiesReader.getPathOpenSPCoop2Pools()) == false){
			OpenSPCoop2PoolsStartup.logger.error("Riscontrato errore durante l'inizializzazione dei pool delle risorse di sistema (Pools)");
			OpenSPCoop2PoolsStartup.loggerConsole.error("Riscontrato errore durante l'inizializzazione dei pool delle risorse di sistema (Pools)");
				return;
		}
		this.gestoreRisorseSistema = GestoreRisorseSistema.getInstance();
		try{
			this.gestoreRisorseSistema.createRisorseSistema();
		}catch(Exception e){
			OpenSPCoop2PoolsStartup.logger.error("Riscontrato errore durante la creazione/bind dei pool delle risorse di sistema (Pools): "+e.getMessage(),e);
			OpenSPCoop2PoolsStartup.loggerConsole.error("Riscontrato errore durante la creazione/bind dei pool delle risorse di sistema (Pools): "+e.getMessage());
			return;
		}
		
		
		
		/* ----------- Inizializzazione Gestore Risorse JMX ------------ */
		try{
			if(propertiesReader.isRisorseJMXAbilitate()){
				if(propertiesReader.getJNDIName_MBeanServer()!=null){
					this.gestoreRisorseJMX = new GestoreRisorseJMX(propertiesReader.getJNDIName_MBeanServer(),
							propertiesReader.getJNDIContext_MBeanServer());
				}else{
					this.gestoreRisorseJMX = new GestoreRisorseJMX();
				}
			}
		}catch(Exception e){
			OpenSPCoop2PoolsStartup.logger.error("Riscontrato errore durante l'inizializzazione del Gestore di Risorse JMX: "+e.getMessage());
			OpenSPCoop2PoolsStartup.loggerConsole.error("Riscontrato errore durante l'inizializzazione del Gestore di Risorse JMX: "+e.getMessage());
		}
		if( this.gestoreRisorseJMX!=null ){
			// MBean Monitoraggio Risorse Datasource
			try{
				if(GestoreRisorseSistema.getJNDINameDBPool()!=null)
					this.gestoreRisorseJMX.registerMBeanMonitoraggioRisorseDataSources(GestoreRisorseSistema.getJNDINameDBPool());
			}catch(Exception e){
				OpenSPCoop2PoolsStartup.logger.error("Riscontrato errore durante l'inizializzazione della risorsa JMX che gestisce il monitoraggio delle risorse dei datasources: "+e.getMessage());
				OpenSPCoop2PoolsStartup.loggerConsole.error("Riscontrato errore durante l'inizializzazione della risorsa JMX che gestisce il monitoraggio delle risorse dei datasources: "+e.getMessage());
			}
			// MBean Monitoraggio Risorse connection factories
			try{
				if(GestoreRisorseSistema.getJNDINameJMSPool()!=null)
					this.gestoreRisorseJMX.registerMBeanMonitoraggioRisorseConnectionFactories(GestoreRisorseSistema.getJNDINameJMSPool());
			}catch(Exception e){
				OpenSPCoop2PoolsStartup.logger.error("Riscontrato errore durante l'inizializzazione della risorsa JMX che gestisce il monitoraggio delle risorse delle connection factories: "+e.getMessage());
				OpenSPCoop2PoolsStartup.loggerConsole.error("Riscontrato errore durante l'inizializzazione della risorsa JMX che gestisce il monitoraggio delle risorse delle connection factories: "+e.getMessage());
			}
		}
		
		
		/* ------------ OpenSPCoop startup terminato  ------------ */
		long endDate = System.currentTimeMillis();
		long secondStarter = (endDate - startDate) / 1000;
		if(secondStarter>0){
			OpenSPCoop2PoolsStartup.logger.info("Pool di Risorse OpenSPCoop avviato correttamente in "+secondStarter+" secondi.");
			OpenSPCoop2PoolsStartup.loggerConsole.info("Pool di Risorse OpenSPCoop avviato correttamente in "+secondStarter+" secondi.");
		}else{
			OpenSPCoop2PoolsStartup.logger.info("Pool di Risorse OpenSPCoop avviato correttamente.");
			OpenSPCoop2PoolsStartup.loggerConsole.info("Pool di Risorse OpenSPCoop avviato correttamente.");
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
		
		int i = 0;
		while(i < 30000){
			try{
				Thread.sleep(5000);
				i = i + 5000;
				int secondiRimasti = ((30000-i)/1000);
				if(secondiRimasti>0)
					System.out.println("Attendo undeploy della Porta di Dominio (ancora "+secondiRimasti+" secondi)...");
			}catch(Exception e){}
		}
		System.out.println("Undeploy OpenSPCoop2 Pools ...");
				
		
		// Rilascio risorse JMX
		if(this.gestoreRisorseJMX!=null){
			this.gestoreRisorseJMX.unregisterMBeans();
		}
				
		// Rilascio risorse di sistema
		if(this.gestoreRisorseSistema!=null)
			this.gestoreRisorseSistema.deleteRisorseSistema();
		
		
	}


	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return OpenSPCoop2PoolsStartup.logger;
	}


	/**
	 * @return the loggerConsole
	 */
	public static Logger getLoggerConsole() {
		return OpenSPCoop2PoolsStartup.loggerConsole;
	}



}
