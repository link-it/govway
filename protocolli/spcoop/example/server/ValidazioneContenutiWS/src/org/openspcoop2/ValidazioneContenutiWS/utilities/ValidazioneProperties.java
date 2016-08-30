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


package org.openspcoop2.ValidazioneContenutiWS.utilities;

import java.util.Properties;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * Reader delle proprieta' per l'accesso al Database
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ValidazioneProperties {

	/** Logger utilizzato per errori eventuali. */
	private static Logger log = LoggerWrapperFactory.getLogger("DatabaseProperties");



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'database.properties' */
	private Properties reader;

	/** Copia Statica */
	private static ValidazioneProperties databaseProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 */
	public ValidazioneProperties() throws Exception {

		/* ---- Lettura del cammino del file di configurazione ---- */
		this.reader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = ValidazioneProperties.class.getResourceAsStream("/validazione.properties");
			this.reader.load(properties);
			properties.close();
		}catch(java.io.IOException e) {
			ValidazioneProperties.log.error("Riscontrato errore durante la lettura del file 'validazione.properties': \n\n"+e.getMessage());
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
			throw new Exception("ClassName initialize error: "+e.getMessage());
		}

	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 */
	public static boolean initialize(){

		try {
			ValidazioneProperties.databaseProperties = new ValidazioneProperties();	
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}

	/**
	 * Ritorna l'indicazione se questa classe e' inizializzata
	 *
	 * @return indicazione se questa classe e' inizializzata
	 */
	public static synchronized boolean isInitialized(){
		if(ValidazioneProperties.databaseProperties!=null)
			return true;
		else
			return false;
	}
	
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di DatabaseProperties
	 */
	public static ValidazioneProperties getInstance(){
		if(ValidazioneProperties.databaseProperties==null)
			ValidazioneProperties.initialize();
		return ValidazioneProperties.databaseProperties;
	}






	
	
	
	
	
	/* ********  M E T O D I  ******** */

	/**
	 * Ritorna il nome del dataSource per l'accesso al database tracciamento
	 *
	 */
	public String getDataSource() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.tracciamento").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.tracciamento':"+e.getMessage();
			ValidazioneProperties.log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	/**
	 * Ritorna il contesto JNDI del dataSource per l'accesso al database tracciamento 
	 *
	 */
	public Properties getJNDIContext_DataSource() throws Exception{
		try{
			return ValidazioneProperties.readProperties("org.openspcoop2.datasource.tracciamento.property.",this.reader);
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.tracciamento.property.':"+e.getMessage();
			ValidazioneProperties.log.error(msgErrore);
			throw new Exception(msgErrore);
		}
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
			throw new Exception("Utilities.readProperties Riscontrato errore durante la lettura delle propriete con prefisso ["+prefix+"]: "+e.getMessage());
		}  
	}
	
	
	
	/**
	 * Ritorna il nome del dataSource per l'accesso al database tracciamento
	 *
	 */
	public boolean isTraceArrived() throws Exception{
		try{
			return Boolean.parseBoolean(this.reader.getProperty("org.openspcoop2.tracciamento.isArrived").trim());
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.tracciamento.isArrived':"+e.getMessage();
			ValidazioneProperties.log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
}
