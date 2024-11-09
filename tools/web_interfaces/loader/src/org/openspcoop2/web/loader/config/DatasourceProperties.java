/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.web.loader.config;


import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;


/**
* ConsoleProperties
*
* @author Andrea Poli (apoli@link.it)
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
	private DatasourceProperties(String confDir,Logger log) throws UtilsException {

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
				throw new UtilsException("File '/loader.datasource.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			doError(e);
		}finally{
		    try{
				if(properties!=null)
				    properties.close();
		    }catch(Exception er){
		    	// close
		    }
		}

		this.reader = new DatasourceInstanceProperties(propertiesReader, this.log, confDir);
	}
	private void doError(Exception e) throws UtilsException {
		String msg = "Riscontrato errore durante la lettura del file 'loader.datasource.properties': "+e.getMessage();
		this.log.error(msg,e);
	    throw new UtilsException("ConsoleProperties initialize error: "+e.getMessage(),e);
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
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (DatasourceProperties.class) {
				throw new OpenSPCoop2ConfigurationException("DatasourceProperties non inizializzato");
			}
	    }
	    return DatasourceProperties.datasourceProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		DatasourceProperties.datasourceProperties.reader.setLocalObjectImplementation(prop);
	}








	/* ********  M E T O D I  ******** */

	private String readProperty(boolean required,String property) throws UtilsException{
		String tmp = this.reader.getValueConvertEnvProperties(property);
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
	
	public String getRegistroServiziDataSource() throws UtilsException{
		return this.readProperty(true, "registroServizi.dataSource");
	}
	
	public Properties getRegistroServiziDataSourceContext() throws UtilsException{
		return this.reader.readPropertiesConvertEnvProperties("registroServizi.dataSource.property.");
	}
	
	public String getRegistroServiziTipoDatabase() throws UtilsException{
		return this.readProperty(true, "registroServizi.tipoDatabase");
	}
	
	
	

	/* ----- Database di configurazione -------- */
	
	public String getConfigurazioneDataSource() throws UtilsException{
		return this.readProperty(true, "configPdD.dataSource");
	}
	
	public Properties getConfigurazioneDataSourceContext() throws UtilsException{
		return this.reader.readPropertiesConvertEnvProperties("configPdD.dataSource.property.");
	}
	
	public String getConfigurazioneTipoDatabase() throws UtilsException{
		return this.readProperty(true, "configPdD.tipoDatabase");
	}
	
}
