package org.openspcoop2.web.monitor.core.config;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;


/**
 * @author Poli Andrea (apoli@link.it)
 */
public class ApplicationProperties {

	
	/** Copia Statica */
	private static ApplicationProperties applicationProperties = null;

	public static synchronized void initialize(Logger log,
			String propertiesPath,String localPropertyName,String localPropertiesPath) throws Exception{

		if(ApplicationProperties.applicationProperties==null)
			ApplicationProperties.applicationProperties = new ApplicationProperties(log, propertiesPath, localPropertyName, localPropertiesPath);	

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
	public ApplicationProperties(Logger log,String propertiesPath,String localPropertyName,String localPropertiesPath) throws Exception{

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
	
		this.reader = new ApplicationInstanceProperties(propertiesReader, log, localPropertyName, localPropertiesPath);

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
		return this.getProperty("protocolloDefault", false, true);
	}
	public File getRepositoryJars() throws Exception{
		String tmp = this.getProperty("repositoryJars", false, true);
		if(tmp!=null){
			return new File(tmp);
		}
		return null;
	}
	
}
