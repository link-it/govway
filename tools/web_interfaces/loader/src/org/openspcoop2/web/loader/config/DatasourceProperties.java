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



package org.openspcoop2.web.loader.config;


import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;


/**
* ConsoleProperties
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/


public class DatasourceProperties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'loader.datasource.properties' */
	private DatasourceInstanceProperties reader;

	/** Copia Statica */
	private static DatasourceProperties datasourceProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public DatasourceProperties(String confDir,Logger log) throws Exception {

		if(log!=null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger(DatasourceProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = DatasourceProperties.class.getResourceAsStream("/loader.datasource.properties");
			if(properties==null){
				throw new Exception("File '/loader.datasource.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'loader.datasource.properties': \n\n"+e.getMessage());
		    throw new Exception("ConsoleProperties initialize error: "+e.getMessage());
		}finally{
		    try{
				if(properties!=null)
				    properties.close();
		    }catch(Exception er){}
		}

		this.reader = new DatasourceInstanceProperties(propertiesReader, this.log, confDir);
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String confDir,Logger log){

		try {
		    DatasourceProperties.datasourceProperties = new DatasourceProperties(confDir,log);	
		    return true;
		}
		catch(Exception e) {
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di ClassNameProperties
	 * 
	 */
	public static DatasourceProperties getInstance() throws OpenSPCoop2ConfigurationException{
		if(DatasourceProperties.datasourceProperties==null){
	    	throw new OpenSPCoop2ConfigurationException("DatasourceProperties non inizializzato");
	    }
	    return DatasourceProperties.datasourceProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		DatasourceProperties.datasourceProperties.reader.setLocalObjectImplementation(prop);
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

	

	
	/* ----- Database del Registro dei Servizi -------- */
	
	public String getRegistroServizi_DataSource() throws UtilsException{
		return this.readProperty(true, "registroServizi.dataSource");
	}
	
	public Properties getRegistroServizi_DataSourceContext() throws UtilsException{
		return this.reader.readProperties_convertEnvProperties("registroServizi.dataSource.property.");
	}
	
	public String getRegistroServizi_TipoDatabase() throws UtilsException{
		return this.readProperty(true, "registroServizi.tipoDatabase");
	}
	
	
	

	/* ----- Database di configurazione -------- */
	
	public String getConfigurazione_DataSource() throws UtilsException{
		return this.readProperty(true, "configPdD.dataSource");
	}
	
	public Properties getConfigurazione_DataSourceContext() throws UtilsException{
		return this.reader.readProperties_convertEnvProperties("configPdD.dataSource.property.");
	}
	
	public String getConfigurazione_TipoDatabase() throws UtilsException{
		return this.readProperty(true, "configPdD.tipoDatabase");
	}
	
}
