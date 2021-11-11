/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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




package org.openspcoop2.pdd.core.connettori;



import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.slf4j.Logger;

/**
 * Reader delle proprieta' di personalizzazione della spedizione attraverso il connettore JMS
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreJMSProperties {

	/** Logger utilizzato per errori eventuali. */
	private static Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();


	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'govway.jmsPublisher.properties' */
	private Properties reader;

	/** Copia Statica */
	private static ConnettoreJMSProperties connettoreJMSProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public ConnettoreJMSProperties() throws Exception {

		/* ---- Lettura del cammino del file di configurazione ---- */
		this.reader = new Properties();
		java.io.InputStream properties = null;
		try{  
		    properties = ConnettoreJMSProperties.class.getResourceAsStream("/govway.jmsPublisher.properties");
		    this.reader.load(properties);
		    properties.close();
		}catch(java.io.IOException e) {
		    ConnettoreJMSProperties.log.error("Riscontrato errore durante la lettura del file 'govway.jmsPublisher.properties': \n\n"+e.getMessage());
		    try{
			if(properties!=null)
			    properties.close();
		    }catch(Exception er){}
		    throw new Exception("ConnettoreJMSProperties initialize error: "+e.getMessage());
		}

	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(){

		try {
		    ConnettoreJMSProperties.connettoreJMSProperties = new ConnettoreJMSProperties();	
		    return true;
		}
		catch(Exception e) {
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di Properties
	 * 
	 */
	public static ConnettoreJMSProperties getInstance(){
	    if(ConnettoreJMSProperties.connettoreJMSProperties==null)
	    	ConnettoreJMSProperties.initialize();
	    return ConnettoreJMSProperties.connettoreJMSProperties;
	}
    









	/* ********  M E T O D I  ******** */

	/**
	 * Restituisce una lista di identificatori dei servizi di pubblicazione definiti
	 *
	 * @return lista di identificatori dei servizi di pubblicazione definiti
	 * 
	 */
	public Map<String,IDServizio> getIDServizi_Pubblicazione() {	
		Map<String,IDServizio> servizi= new HashMap<String,IDServizio>();
	    try{ 
		// Raccolta servizi
		java.util.List<String> idServizi = new java.util.ArrayList<String>();	
		java.util.Enumeration<?> en = this.reader.propertyNames();
		for (; en.hasMoreElements() ;) {
		    String property = (String) en.nextElement();
		    if(property.startsWith("org.openspcoop.pubblicazione.")){
			String key = (property.substring("org.openspcoop.pubblicazione.".length()));
			int indexOf = key.indexOf(".");
			key = key.substring(0,indexOf);
			if(key != null)
			    key = key.trim();
			if(idServizi.contains(key)==false)
			    idServizi.add(key);
		    }
		}
		
		for(int i=0; i<idServizi.size(); i++){
		    //log.info("Raccolta variabili per servizio ["+idServizi.get(i)+"]");
			
		    IDSoggetto idSoggetto = new IDSoggetto();
		    idSoggetto.setTipo((String)this.reader.get("org.openspcoop.pubblicazione."+idServizi.get(i)+".tipoSoggettoErogatore"));
		    idSoggetto.setNome((String)this.reader.get("org.openspcoop.pubblicazione."+idServizi.get(i)+".soggettoErogatore"));
			
		    String tipoServizio = (String)this.reader.get("org.openspcoop.pubblicazione."+idServizi.get(i)+".tipoServizio");
		    String nomeServizio = (String)this.reader.get("org.openspcoop.pubblicazione."+idServizi.get(i)+".servizio");
		    Integer versioneServizio = Integer.parseInt(((String)this.reader.get("org.openspcoop.pubblicazione."+idServizi.get(i)+".versioneServizio")));
		    IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipoServizio, nomeServizio, idSoggetto, versioneServizio);
		    
		    //log.info("Servizio ["+IDServizioFactory.getInstance().getUriFromIDServizio(idServizio)+"]");
		    servizi.put(idServizi.get(i),idServizio);
		}
		
		return servizi;
		
	    }catch(java.lang.Exception e) {
		ConnettoreJMSProperties.log.error("Riscontrato errore durante la lettura dei servizi pubblicatori : 'org.openspcoop.pubblicazione.*'");
		return null;
	    }  
	}
	
	/**
	 * Restituisce le proprieta' da utilizzare con il contesto JNDI di lookup, se impostate.
	 *
	 * @return proprieta' da utilizzare con il contesto JNDI di lookup.
	 * 
	 */
	public java.util.Properties getJNDIContext_Configurazione(String id) {	
		java.util.Properties prop = new java.util.Properties();
		try{ 
			String ricerca = "org.openspcoop.pubblicazione." + id +".jndi.contextProperty.";
			java.util.Enumeration<?> en = this.reader.propertyNames();
			for (; en.hasMoreElements() ;) {
				String property = (String) en.nextElement();
				if(property.startsWith(ricerca)){
					String key = (property.substring(ricerca.length()));
					if(key != null)
						key = key.trim();
					String value = this.reader.getProperty(property);
					if(value!=null)
						value = value.trim();
					if(key!=null && value!=null){
						prop.setProperty(key,value);
						//System.out.println("context ["+key+"] ["+value+"]");
					}
				}
			}
			return prop;

		}catch(java.lang.Exception e) {
			ConnettoreJMSProperties.log.error("Riscontrato errore durante la lettura delle propriete' JNDI per la configurazione: "+e.getMessage());
			return null;
		}    
	}
	
	/**
	 * Restituisce le proprieta' da utilizzare con il contesto JNDI di lookup, se impostate.
	 *
	 * @return proprieta' da utilizzare con il contesto JNDI di lookup.
	 * 
	 */
	public java.util.Properties getJNDIPool_Configurazione(String id) {	
		java.util.Properties prop = new java.util.Properties();
		try{ 
			String ricerca = "org.openspcoop.pubblicazione." + id +".jndi.poolProperty.";
			java.util.Enumeration<?> en = this.reader.propertyNames();
			for (; en.hasMoreElements() ;) {
				String property = (String) en.nextElement();
				if(property.startsWith(ricerca)){
					String key = (property.substring(ricerca.length()));
					if(key != null)
						key = key.trim();
					String value = this.reader.getProperty(property);
					if(value!=null)
						value = value.trim();
					if(key!=null && value!=null){
						prop.setProperty(key,value);
						//System.out.println("pool ["+key+"] ["+value+"]");
					}
				}
			}
			return prop;

		}catch(java.lang.Exception e) {
			ConnettoreJMSProperties.log.error("Riscontrato errore durante la lettura delle propriete' JNDI per la configurazione: "+e.getMessage());
			return null;
		}    
	}
	
	
	public String[] getClassNameSetPropertiesJMS(){
		try{ 
			String value = this.reader.getProperty("org.openspcoop.pubblicazione.setProprietaJMS.class");
			if(value!=null){
				value = value.trim();
				String [] classi = value.split(",");
				if(classi!=null){
					for(int i=0;i<classi.length;i++){
						classi[i] = classi[i].trim();
					}
				}
				return classi;
			}else
				return null;
		}catch(java.lang.Exception e) {
			ConnettoreJMSProperties.log.error("Riscontrato errore durante la lettura delle classi da utilizzare per il setting delle proprieta' JMS: "+e.getMessage());
			return null;
		}   
	}
	

}
	
	
