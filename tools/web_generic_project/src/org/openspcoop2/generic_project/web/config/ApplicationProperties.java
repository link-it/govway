/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.generic_project.web.config;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;


/**
 * ApplicationProperties
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApplicationProperties {

	
	/** Copia Statica */
	private static ApplicationProperties applicationProperties = null;

	public static synchronized void initialize(Logger log,String OPENSPCOOP2_LOCAL_HOME,
			String propertiesPath,String localPropertyName,String localPropertiesPath) throws Exception{

		if(ApplicationProperties.applicationProperties==null)
			ApplicationProperties.applicationProperties = new ApplicationProperties(log,OPENSPCOOP2_LOCAL_HOME, propertiesPath, localPropertyName, localPropertiesPath);	

	}

	public static ApplicationProperties getInstance(Logger log) throws Exception{

		if(ApplicationProperties.applicationProperties==null)
			throw new Exception("Properties not initialized");

		return ApplicationProperties.applicationProperties;
	}
	
	
	
	
	
	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'server.properties' */
	private ApplicationInstanceProperties reader;





	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public ApplicationProperties(Logger log,String OPENSPCOOP2_LOCAL_HOME,String propertiesPath,String localPropertyName,String localPropertiesPath) throws Exception{

		/* ---- Lettura del cammino del file di configurazione ---- */

		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = ApplicationProperties.class.getResourceAsStream(propertiesPath);
			if(properties==null){
				throw new Exception("Properties "+propertiesPath+" not found");
			}
			propertiesReader.load(properties);
			properties.close();
		}catch(java.io.IOException e) {
			log.error("Riscontrato errore durante la lettura del file '"+propertiesPath+"': "+e.getMessage(),e);
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
			throw e;
		}	
	
		this.reader = new ApplicationInstanceProperties(OPENSPCOOP2_LOCAL_HOME,propertiesReader, log, localPropertyName, localPropertiesPath);

	}

	
	
	
	
	/* ********  P R O P E R T I E S  ******** */

	public Enumeration<?> keys(){
		return this.reader.propertyNames();
	}
	
	public String getProperty(String name,boolean required, boolean convertEnvProperty) throws Exception{
		String tmp = null;
		if(convertEnvProperty){
			tmp = this.reader.getValue_convertEnvProperties(name);
		}else{
			tmp = this.reader.getValue(name);
		}
		if(tmp==null){
			if(required){
				throw new Exception("Property ["+name+"] not found");
			}
		}
		if(tmp!=null){
			return tmp.trim();
		}else{
			return null;
		}
	}
	
	public Properties readProperties(String prefix) throws Exception{
		return this.reader.readProperties_convertEnvProperties(prefix);
	}
	 

	
	public String getConfigurationDir() throws Exception{
		return this.getProperty("confDirectory", true, true);
	}
	public String getProtocolloDefault() throws Exception{
		return this.getProperty("protocolloDefault", false, false);
	}
	public File getRepositoryJars() throws Exception{
		String tmp = this.getProperty("repositoryJars", false, false);
		if(tmp!=null){
			return new File(tmp);
		}
		return null;
	}
	
}
