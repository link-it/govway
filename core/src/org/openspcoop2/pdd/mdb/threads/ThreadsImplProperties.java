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

package org.openspcoop2.pdd.mdb.threads;


import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.PropertiesReader;

/**
 * Contiene un lettore del file di proprieta' di Threads.
 *
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 **/


public class ThreadsImplProperties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;


	/** Copia Statica */
	private static ThreadsImplProperties threadsproperties = null;




	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'openspcoop2.properties' */
	private PropertiesReader reader;






	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public ThreadsImplProperties() throws Exception{

		if(ThreadsStartup.initialize)
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		else
			this.log = LoggerWrapperFactory.getLogger(ThreadsImplProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = ThreadsImplProperties.class.getResourceAsStream("/threads.properties");
			propertiesReader.load(properties);
			properties.close();
		}catch(java.io.IOException e) {
			this.log.error("Riscontrato errore durante la lettura del file 'threads.properties': \n\n"+e.getMessage());
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
			throw new Exception("ThreadsProperties initialize error: "+e.getMessage(),e);
		}	
		this.reader = new PropertiesReader(propertiesReader,true);

	}

	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(){

		try {
			ThreadsImplProperties.threadsproperties = new ThreadsImplProperties();	
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di OpenSPCoopProperties
	 * 
	 */
	public static ThreadsImplProperties getInstance(){
		if(ThreadsImplProperties.threadsproperties==null)
			ThreadsImplProperties.initialize();	
		return ThreadsImplProperties.threadsproperties;
	}

	private static String pool_type = null;
	public String getPoolType(){
		if(ThreadsImplProperties.pool_type==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("it.link.threads.pool_type");
				if (name != null) {
					name = name.trim();
					ThreadsImplProperties.pool_type = name;
				}
				else {
					this.log.warn("Proprieta it.link.threads.pool_type' non impostata, viene utilizzato il default="+CostantiThreads.POOL_TYPE);
					ThreadsImplProperties.pool_type =  CostantiThreads.POOL_TYPE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta it.link.threads.pool_type' non impostata, viene utilizzato il default="+CostantiThreads.POOL_TYPE+", errore:"+e.getMessage());
				ThreadsImplProperties.pool_type = CostantiThreads.POOL_TYPE;
			}   
		}
		return ThreadsImplProperties.pool_type;
	}
	
	private static Integer pool_depth = null;
	public int getPoolDepth(){
		if(ThreadsImplProperties.pool_depth==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("it.link.threads.pool_depth");
				if (name != null) {
					name = name.trim();
					ThreadsImplProperties.pool_depth = Integer.parseInt(name);
				}
				else {
					this.log.warn("Proprieta it.link.threads.pool_depth' non impostata, viene utilizzato il default="+CostantiThreads.POOL_DEPTH);
					ThreadsImplProperties.pool_depth =  CostantiThreads.POOL_DEPTH;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta it.link.threads.pool_depth' non impostata, viene utilizzato il default="+CostantiThreads.POOL_DEPTH+", errore:"+e.getMessage());
				ThreadsImplProperties.pool_depth = CostantiThreads.POOL_DEPTH;
			}   
		}
		return ThreadsImplProperties.pool_depth;
	}

	private static String coda_type = null;
	public String getCodaType(){
		if(ThreadsImplProperties.coda_type==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("it.link.threads.coda_type");
				if (name != null) {
					name = name.trim();
					ThreadsImplProperties.coda_type = name;
				}
				else {
					this.log.warn("Proprieta it.link.threads.coda_type' non impostata, viene utilizzato il default="+CostantiThreads.POOL_TYPE);
					ThreadsImplProperties.coda_type =  CostantiThreads.POOL_TYPE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta it.link.threads.coda_type' non impostata, viene utilizzato il default="+CostantiThreads.POOL_TYPE+", errore:"+e.getMessage());
				ThreadsImplProperties.coda_type = CostantiThreads.POOL_TYPE;
			}   
		}
		return ThreadsImplProperties.coda_type;
	}
	
	private static Integer coda_depth = null;
	public int getCodaDepth(){
		if(ThreadsImplProperties.coda_depth==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("it.link.threads.coda_depth");
				if (name != null) {
					name = name.trim();
					ThreadsImplProperties.coda_depth = Integer.parseInt(name);
				}
				else {
					this.log.warn("Proprieta it.link.threads.coda_depth' non impostata, viene utilizzato il default="+CostantiThreads.POOL_DEPTH);
					ThreadsImplProperties.coda_depth =  CostantiThreads.POOL_DEPTH;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta it.link.threads.coda_depth' non impostata, viene utilizzato il default="+CostantiThreads.POOL_DEPTH+", errore:"+e.getMessage());
				ThreadsImplProperties.coda_depth = CostantiThreads.POOL_DEPTH;
			}   
		}
		return ThreadsImplProperties.coda_depth;
	}
	
	
	/* ************** REDELIVERY  ********************* */

	private static Boolean redelivery_status = null;
	public boolean isRedeliveryAttivo(){
		if(ThreadsImplProperties.redelivery_status==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("it.link.threads.redelivery_status");
				if (name != null) {
					name = name.trim();
					ThreadsImplProperties.redelivery_status = java.lang.Boolean.parseBoolean(name);
				}
				else {
					this.log.warn("Proprieta it.link.threads.redelivery_status' non impostata, viene utilizzato il default="+CostantiThreads.REDELIVERY_STATUS);
					ThreadsImplProperties.redelivery_status =  CostantiThreads.REDELIVERY_STATUS;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta it.link.threads.redelivery_status' non impostata, viene utilizzato il default="+CostantiThreads.REDELIVERY_STATUS+", errore:"+e.getMessage());
				ThreadsImplProperties.redelivery_status = CostantiThreads.REDELIVERY_STATUS;
			}   
		}
		return ThreadsImplProperties.redelivery_status;
	}
	
	private static Long redelivery_delay = null;
	public long getRedeliveryDelay(){
		if(ThreadsImplProperties.redelivery_delay==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("it.link.threads.redelivery_delay");
				if (name != null) {
					name = name.trim();
					long time = java.lang.Long.parseLong(name);
					ThreadsImplProperties.redelivery_delay = time;
				}
				else {
					this.log.warn("Proprieta it.link.threads.redelivery_delay' non impostata, viene utilizzato il default="+CostantiThreads.REDELIVERY_DELAY);
					ThreadsImplProperties.redelivery_delay =  CostantiThreads.REDELIVERY_DELAY;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta it.link.threads.redelivery_delay' non impostata, viene utilizzato il default="+CostantiThreads.REDELIVERY_DELAY+", errore:"+e.getMessage());
				ThreadsImplProperties.redelivery_delay = CostantiThreads.REDELIVERY_DELAY;
			}   
		}
		return ThreadsImplProperties.redelivery_delay;
	}

	private static Integer redelivery_count = null;
	public int getRedeliveryCount(){
		if(ThreadsImplProperties.redelivery_count==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("it.link.threads.redelivery_count");
				if (name != null) {
					name = name.trim();
					ThreadsImplProperties.redelivery_count = Integer.parseInt(name);
				}
				else {
					this.log.warn("Proprieta it.link.threads.redelivery_count' non impostata, viene utilizzato il default="+CostantiThreads.REDELIVERY_COUNT);
					ThreadsImplProperties.redelivery_count =  CostantiThreads.REDELIVERY_COUNT;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta it.link.threads.redelivery_count' non impostata, viene utilizzato il default="+CostantiThreads.REDELIVERY_COUNT+", errore:"+e.getMessage());
				ThreadsImplProperties.redelivery_count = CostantiThreads.REDELIVERY_COUNT;
			}   
		}
		return ThreadsImplProperties.redelivery_count;
	}

	/** Imposta il tempo di attesa del producer prima della ricerca di un nuovo messaggio */
	private static long attesa_producer = -1;
	public long getAttesaProducer(){
		if(ThreadsImplProperties.attesa_producer==-1){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("it.link.threads.attesa_producer");
				if (name != null) {
					name = name.trim();
					ThreadsImplProperties.attesa_producer = Long.parseLong(name);
				}
				else {
					this.log.warn("Proprieta it.link.threads.attesa_producer' non impostata, viene utilizzato il default="+CostantiThreads.ATTESA_PRODUCER);
					ThreadsImplProperties.attesa_producer =  CostantiThreads.ATTESA_PRODUCER;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta it.link.threads.attesa_producer' non impostata, viene utilizzato il default="+CostantiThreads.ATTESA_PRODUCER+", errore:"+e.getMessage());
				ThreadsImplProperties.attesa_producer = CostantiThreads.ATTESA_PRODUCER;
			}   
		}
		return ThreadsImplProperties.attesa_producer;
	}

}




