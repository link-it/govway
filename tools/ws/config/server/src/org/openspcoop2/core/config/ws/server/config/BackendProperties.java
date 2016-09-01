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



package org.openspcoop2.core.config.ws.server.config;


import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;


/**
* BackendProperties
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/


public class BackendProperties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'wsconfig.datasource.properties' */
	private BackendInstanceProperties reader;

	/** Copia Statica */
	private static BackendProperties backendProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public BackendProperties(String confDir,Logger log) throws Exception {

		if(log!=null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger(BackendProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = BackendProperties.class.getResourceAsStream("/wsconfig.datasource.properties");
			if(properties==null){
				throw new Exception("File '/wsconfig.datasource.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'wsconfig.datasource.properties': \n\n"+e.getMessage());
		    throw new Exception("WSConfigProperties initialize error: "+e.getMessage());
		}finally{
		    try{
				if(properties!=null)
				    properties.close();
		    }catch(Exception er){}
		}

		this.reader = new BackendInstanceProperties(propertiesReader, this.log, confDir);
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String confDir,Logger log){

		try {
		    BackendProperties.backendProperties = new BackendProperties(confDir,log);	
		    return true;
		}
		catch(Exception e) {
			log.error("Errore durante l'inizializzazione del BackendProperties: "+e.getMessage(),e);
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di Properties
	 * 
	 */
	public static BackendProperties getInstance() throws UtilsException{
		if(BackendProperties.backendProperties==null){
	    	throw new UtilsException("DatasourceProperties non inizializzato");
	    }
	    return BackendProperties.backendProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		BackendProperties.backendProperties.reader.setLocalObjectImplementation(prop);
	}








	/* ********  M E T O D I  ******** */

	private String readProperty(boolean required,String property) throws UtilsException{
		String tmp = this.reader.getValue_convertEnvProperties(property);
		if(tmp==null){
			if(required){
				throw new UtilsException("Property ["+property+"] not found");
			}
			else{
				return null;
			}
		}else{
			return tmp.trim();
		}
	}

	

	
	/* ----- Tipo -------- */
	
	public String getTipoConfigurazione() throws UtilsException{
		return this.readProperty(true, "tipo");
	}
	
	
	
	
	
	/* ----- DB -------- */
	
	public String getDbDataSource() throws UtilsException{
		return this.readProperty(true, "db.dataSource");
	}
	
	public Properties getDbDataSourceContext() throws UtilsException{
		return this.reader.readProperties_convertEnvProperties("db.dataSource.property.");
	}
	
	public String getDbTipoDatabase() throws UtilsException{
		return this.readProperty(true, "db.tipoDatabase");
	}
	

	
	
	/* ----- UDDI -------- */
	
	public String getUddiInquiryURL() throws UtilsException{
		return this.readProperty(true, "uddi.inquiryURL");
	}
	
	public String getUddiPublishURL() throws UtilsException{
		return this.readProperty(true, "uddi.publishURL");
	}
	
	public String getUddiUser() throws UtilsException{
		return this.readProperty(true, "uddi.user");
	}
	
	public String getUddiPassword() throws UtilsException{
		return this.readProperty(true, "uddi.password");
	}
	
	public String getUddiWebUrlPrefix() throws UtilsException{
		return this.readProperty(true, "uddi.web.urlPrefix");
	}
	
	public String getUddiWebPathPrefix() throws UtilsException{
		return this.readProperty(true, "uddi.web.pathPrefix");
	}
	
	
	
	/* ----- WEB -------- */
		
	public String getWebUrlPrefix() throws UtilsException{
		return this.readProperty(true, "web.urlPrefix");
	}
	
	public String getWebPathPrefix() throws UtilsException{
		return this.readProperty(true, "web.pathPrefix");
	}
}
