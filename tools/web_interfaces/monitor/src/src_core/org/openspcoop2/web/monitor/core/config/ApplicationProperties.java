/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.config;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.slf4j.Logger;


/**
 * ApplicationProperties
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
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
	
	public boolean isAbilitataCache_datiConfigurazione() throws Exception{
		String cacheEnabled = this.getProperty("cache.datiConfigurazione.enable", true, true);
		return "true".equalsIgnoreCase(cacheEnabled);
	}
	public boolean isAbilitataJmxCache_datiConfigurazione() throws Exception{
		String cacheEnabled = this.getProperty("cache.datiConfigurazione.jmx.enable", false, true);
		return "true".equalsIgnoreCase(cacheEnabled);
	}
	public boolean isDebugCache_datiConfigurazione() throws Exception{
		String cacheEnabled = this.getProperty("cache.datiConfigurazione.debug", false, true);
		return "true".equalsIgnoreCase(cacheEnabled);
	}
	
	public CacheAlgorithm getAlgoritmoCache_datiConfigurazione() throws Exception{
		String cacheV = this.getProperty("cache.datiConfigurazione.algoritmo", false, true);
		if(cacheV!=null && StringUtils.isNotEmpty(cacheV)) {
			return CacheAlgorithm.valueOf(cacheV.toUpperCase());
		}
		return null;
	}
	
	public Integer getDimensioneCache_datiConfigurazione() throws Exception{
		return _getIntegerValueCache("cache.datiConfigurazione.dimensione");
	}
	public Integer getItemIdleTimeCache_datiConfigurazione() throws Exception{
		return _getIntegerValueCache("cache.datiConfigurazione.itemIdleTime");
	}
	public Integer getItemLifeSecondCache_datiConfigurazione() throws Exception{
		return _getIntegerValueCache("cache.datiConfigurazione.itemLifeSecond");
	}
	
	
	public boolean isAbilitataCache_ricercheConfigurazione() throws Exception{
		String cacheEnabled = this.getProperty("cache.ricercheConfigurazione.enable", true, true);
		return "true".equalsIgnoreCase(cacheEnabled);
	}
	public boolean isAbilitataJmxCache_ricercheConfigurazione() throws Exception{
		String cacheEnabled = this.getProperty("cache.ricercheConfigurazione.jmx.enable", false, true);
		return "true".equalsIgnoreCase(cacheEnabled);
	}
	public boolean isDebugCache_ricercheConfigurazione() throws Exception{
		String cacheEnabled = this.getProperty("cache.ricercheConfigurazione.debug", false, true);
		return "true".equalsIgnoreCase(cacheEnabled);
	}
	
	public CacheAlgorithm getAlgoritmoCache_ricercheConfigurazione() throws Exception{
		String cacheV = this.getProperty("cache.ricercheConfigurazione.algoritmo", false, true);
		if(cacheV!=null && StringUtils.isNotEmpty(cacheV)) {
			return CacheAlgorithm.valueOf(cacheV.toUpperCase());
		}
		return null;
	}
	
	public Integer getDimensioneCache_ricercheConfigurazione() throws Exception{
		return _getIntegerValueCache("cache.ricercheConfigurazione.dimensione");
	}
	public Integer getItemIdleTimeCache_ricercheConfigurazione() throws Exception{
		return _getIntegerValueCache("cache.ricercheConfigurazione.itemIdleTime");
	}
	public Integer getItemLifeSecondCache_ricercheConfigurazione() throws Exception{
		return _getIntegerValueCache("cache.ricercheConfigurazione.itemLifeSecond");
	}
	
	
	private Integer _getIntegerValueCache(String name) throws Exception{
		String cacheV = this.getProperty(name, false, true);
		if(cacheV!=null && StringUtils.isNotEmpty(cacheV)) {
			Integer i = Integer.valueOf(cacheV);
			if(i.intValue()>0) {
				return i;
			}
		}
		return null;
	}
	
	
	public boolean isPluginsEnabled() throws Exception{
		String cacheEnabled = this.getProperty("plugins.enabled", true, true);
		return "true".equalsIgnoreCase(cacheEnabled);
	}
	public Integer getPluginsSeconds() throws Exception{
		String cacheV = this.getProperty("plugins.seconds", false, true);
		if(cacheV!=null && StringUtils.isNotEmpty(cacheV)) {
			Integer i = Integer.valueOf(cacheV);
			if(i.intValue()>0) {
				return i;
			}
		}
		return 300;
	}
}
