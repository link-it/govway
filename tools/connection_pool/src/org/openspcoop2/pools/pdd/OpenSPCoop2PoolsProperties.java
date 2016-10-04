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


import java.util.Properties;
import java.io.File;

import org.slf4j.Logger;


/**
 * Contiene un lettore del file di proprieta' di OpenSPCoop.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class OpenSPCoop2PoolsProperties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;


	/** Copia Statica */
	private static OpenSPCoop2PoolsProperties openspcoop2PoolsProperties = null;




	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'openspcoop2_pools.properties' */
	private Properties reader;






	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public OpenSPCoop2PoolsProperties() throws Exception{

		this.log = OpenSPCoop2PoolsStartup.getLogger();
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		this.reader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = OpenSPCoop2PoolsProperties.class.getResourceAsStream("/openspcoop2_pools.properties");
			this.reader.load(properties);
			properties.close();
		}catch(java.io.IOException e) {
			this.log.error("Riscontrato errore durante la lettura del file 'openspcoop2_pools.properties': \n\n"+e.getMessage());
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
			throw new Exception("OpenSPCoop2PoolsProperties initialize error: "+e.getMessage());
		}	
	}

	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(){

		try {
			OpenSPCoop2PoolsProperties.openspcoop2PoolsProperties = new OpenSPCoop2PoolsProperties();	
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
	public static OpenSPCoop2PoolsProperties getInstance(){
		if(OpenSPCoop2PoolsProperties.openspcoop2PoolsProperties==null)
			OpenSPCoop2PoolsProperties.initialize();	
		return OpenSPCoop2PoolsProperties.openspcoop2PoolsProperties;
	}






	/* ********  VALIDATORE FILE PROPERTY  ******** */

	/**
	 * Effettua una validazione delle proprieta' impostate nel file OpenSPCoop.properties.   
	 *
	 * @return true se la validazione ha successo, false altrimenti.
	 * 
	 */
	public boolean validaConfigurazione() {	
		try{  
			if( getPathOpenSPCoop2Pools()==null ){
				return false;
			}
			if( (new File( getPathOpenSPCoop2Pools())).exists() == false ){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pools.pdd.location'. \n Il file indicato non esiste ["+getPathOpenSPCoop2Pools()+"].");
				return false;
			}
			
			return true;
			
		}catch(java.lang.Exception e) {
			this.log.error("Riscontrato errore durante la validazione lettura della proprieta': "+e.getMessage());
			return false;
		}
	}

	
	
	
	
	

	/* ********  POOLS DI OPENSPCOOP  ******** */

	/**
	 * Restituisce la location della configurazione dei pools di OpenSPCoop,
	 *
	 * @return il path della configurazione dei pools in caso di ricerca con successo, null altrimenti.
	 * 
	 */
	private static String pathOpenSPCoop2Pools = null;
	public String getPathOpenSPCoop2Pools() {	
		if(OpenSPCoop2PoolsProperties.pathOpenSPCoop2Pools==null){
			try{  
				String indirizzo = this.reader.getProperty("org.openspcoop2.pools.config.location"); 
				indirizzo = indirizzo.trim();
	
				OpenSPCoop2PoolsProperties.pathOpenSPCoop2Pools = indirizzo;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop  : 'org.openspcoop2.pools.config.location'");
				OpenSPCoop2PoolsProperties.pathOpenSPCoop2Pools = null;
			}
		}
		
		return OpenSPCoop2PoolsProperties.pathOpenSPCoop2Pools;
	} 

	
	
	
	
	/* ******* GESTORE JMX *********** */
	/**
	 * Restituisce l'indicazione se istanziare le risorse JMX
	 *
	 * @return Restituisce Restituisce l'indicazione se istanziare le risorse JMX
	 * 
	 */
	private static Boolean isRisorseJMXAbilitate = null;
	public boolean isRisorseJMXAbilitate(){
		if(OpenSPCoop2PoolsProperties.isRisorseJMXAbilitate==null){
			try{  
				String value = this.reader.getProperty("org.openspcoop2.pools.jmx.enable"); 
				value = value.trim();
	
				OpenSPCoop2PoolsProperties.isRisorseJMXAbilitate = Boolean.parseBoolean(value);
	
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta': 'org.openspcoop2.pools.jmx.enable'");
				OpenSPCoop2PoolsProperties.isRisorseJMXAbilitate = false;
			}
		}
		
		return OpenSPCoop2PoolsProperties.isRisorseJMXAbilitate;
	}
	
	/**
	 * Restituisce il Nome JNDI del MBeanServer
	 *
	 * @return il Nome JNDI del MBeanServer
	 * 
	 */
	private static String jndiNameMBeanServer = null;
	public String getJNDIName_MBeanServer() {	
		if(OpenSPCoop2PoolsProperties.jndiNameMBeanServer==null){
			try{ 
				String name = null;
				name = this.reader.getProperty("org.openspcoop2.pools.jmx.jndi.mbeanServer");
				if(name!=null)
					name = name.trim();
				OpenSPCoop2PoolsProperties.jndiNameMBeanServer = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta': 'org.openspcoop2.pools.jmx.jndi.mbeanServer'");
				OpenSPCoop2PoolsProperties.jndiNameMBeanServer = null;
			}    
		}
		
		return OpenSPCoop2PoolsProperties.jndiNameMBeanServer;
	}

	/**
	 * Restituisce le proprieta' da utilizzare con il contesto JNDI di lookup, se impostate.
	 *
	 * @return proprieta' da utilizzare con il contesto JNDI di lookup.
	 * 
	 */
	private static java.util.Properties jndiContextMBeanServer = null;
	public java.util.Properties getJNDIContext_MBeanServer() {
		if(OpenSPCoop2PoolsProperties.jndiContextMBeanServer==null){
			java.util.Properties prop = new java.util.Properties();
			try{ 
	
				prop = OpenSPCoop2PoolsProperties.readProperties("org.openspcoop2.pools.jmx.jndi.property.",this.reader);
				OpenSPCoop2PoolsProperties.jndiContextMBeanServer = prop;
	
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle propriete' JNDI: 'org.openspcoop2.pools.jmx.jndi.property.*'");
				OpenSPCoop2PoolsProperties.jndiContextMBeanServer = null;
			}    
		}
		
		return OpenSPCoop2PoolsProperties.jndiContextMBeanServer;
	}

	/**
	 * Legge le proprieta' che possiedono un nome che inizia con un determinato prefisso
	 * 
	 * @param prefix
	 * @param sorgente java.util.Properties
	 * @return java.util.Properties
	 * @throws BinaryStreamNotSupportedException
	 */
	public static java.util.Properties readProperties (String prefix,java.util.Properties sorgente)throws Exception{
		java.util.Properties prop = new java.util.Properties();
		try{ 
			java.util.Enumeration<?> en = sorgente.propertyNames();
			for (; en.hasMoreElements() ;) {
				String property = (String) en.nextElement();
				if(property.startsWith(prefix)){
					String key = (property.substring(prefix.length()));
					if(key != null)
						key = key.trim();
					String value = sorgente.getProperty(property);
					if(value!=null)
						value = value.trim();
					if(key!=null && value!=null)
						prop.setProperty(key,value);
				}
			}
			return prop;
		}catch(java.lang.Exception e) {
			throw new Exception("Utilities.readProperties Riscontrato errore durante la lettura delle propriete con prefisso ["+prefix+"]: "+e.getMessage(),e);
		}  
	}
}

