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
package org.openspcoop2.web.monitor.core.config;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.UtilsException;
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
			}catch(Exception er){
				// close
			}
			throw e;
		}	
	
		this.reader = new ApplicationInstanceProperties(propertiesReader, log, localPropertyName, localPropertiesPath);

	}

	
	
	
	
	/* ********  P R O P E R T I E S  ******** */

	private String getPropertyPrefix(String property) {
		return "Property ["+property+"] ";
	}
	private String getMessageUncorrectValue(String tmp) {
		return "with uncorrect value ["+tmp+"]"; 
	}
	
	public Enumeration<?> keys(){
		return this.reader.propertyNames();
	}
	
	public String readProperty(boolean required,String property) throws UtilsException{
		return getProperty(property, required, true);
	}
	public String getProperty(String name,boolean required, boolean convertEnvProperty) throws UtilsException{
		String tmp = null;
		if(convertEnvProperty){
			tmp = this.reader.getValueConvertEnvProperties(name);
		}else{
			tmp = this.reader.getValue(name);
		}
		if(tmp==null &&
			required){
			throw new UtilsException(getPropertyPrefix(name)+"not found");
		}
		if(tmp!=null){
			return tmp.trim();
		}else{
			return null;
		}
	}
	
	public BooleanNullable readBooleanProperty(boolean required,String property) throws UtilsException{
		String tmp = this.getProperty(property,required,true);
		if(tmp==null && !required) {
			return BooleanNullable.NULL(); // se e' required viene sollevata una eccezione dal metodo readProperty
		}
		if(!"true".equalsIgnoreCase(tmp) && !"false".equalsIgnoreCase(tmp)){
			throw new UtilsException(getPropertyPrefix(property)+getMessageUncorrectValue(tmp)+" (true/value expected)");
		}
		return Boolean.parseBoolean(tmp) ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
	}
	public boolean parse(BooleanNullable b, boolean defaultValue) {
		return (b!=null && b.getValue()!=null) ? b.getValue() : defaultValue;
	}
	
	public Properties readProperties(String prefix) throws UtilsException{
		return this.reader.readPropertiesConvertEnvProperties(prefix);
	}
	
	public Integer readIntegerProperty(boolean required,String property) throws UtilsException{
		String tmp = this.getProperty(property,required,true);
		if(tmp==null && !required) {
			return null; // se e' required viene sollevata una eccezione dal metodo readProperty
		}
		try{
			return Integer.parseInt(tmp);
		}catch(Exception e){
			throw new UtilsException(getPropertyPrefix(property)+getMessageUncorrectValue(tmp)+" (int value expected)");
		}
	}
	public Long readLongProperty(boolean required,String property) throws UtilsException{
		String tmp = this.getProperty(property,required,true);
		if(tmp==null && !required) {
			return null; // se e' required viene sollevata una eccezione dal metodo readProperty
		}
		try{
			return Long.parseLong(tmp);
		}catch(Exception e){
			throw new UtilsException(getPropertyPrefix(property)+getMessageUncorrectValue(tmp)+" (long value expected)");
		}
	}

	
	public String getConfigurationDir() throws UtilsException{
		return this.getProperty("confDirectory", true, true);
	}
	public String getProtocolloDefault() throws UtilsException{
		return this.getProperty("protocolloDefault", false, true);
	}
	public File getRepositoryJars() throws UtilsException{
		String tmp = this.getProperty("repositoryJars", false, true);
		if(tmp!=null){
			return new File(tmp);
		}
		return null;
	}
	
	public boolean isAbilitataCache_datiConfigurazione() throws UtilsException{
		String cacheEnabled = this.getProperty("cache.datiConfigurazione.enable", true, true);
		return "true".equalsIgnoreCase(cacheEnabled);
	}
	public boolean isAbilitataJmxCache_datiConfigurazione() throws UtilsException{
		String cacheEnabled = this.getProperty("cache.datiConfigurazione.jmx.enable", false, true);
		return "true".equalsIgnoreCase(cacheEnabled);
	}
	public boolean isDebugCache_datiConfigurazione() throws UtilsException{
		String cacheEnabled = this.getProperty("cache.datiConfigurazione.debug", false, true);
		return "true".equalsIgnoreCase(cacheEnabled);
	}
	
	public CacheAlgorithm getAlgoritmoCache_datiConfigurazione() throws UtilsException{
		String cacheV = this.getProperty("cache.datiConfigurazione.algoritmo", false, true);
		if(cacheV!=null && StringUtils.isNotEmpty(cacheV)) {
			return CacheAlgorithm.valueOf(cacheV.toUpperCase());
		}
		return null;
	}
	
	public Integer getDimensioneCache_datiConfigurazione() throws UtilsException{
		return _getIntegerValueCache("cache.datiConfigurazione.dimensione");
	}
	public Integer getItemIdleTimeCache_datiConfigurazione() throws UtilsException{
		return _getIntegerValueCache("cache.datiConfigurazione.itemIdleTime");
	}
	public Integer getItemLifeSecondCache_datiConfigurazione() throws UtilsException{
		return _getIntegerValueCache("cache.datiConfigurazione.itemLifeSecond");
	}
	
	
	public boolean isAbilitataCache_ricercheConfigurazione() throws UtilsException{
		String cacheEnabled = this.getProperty("cache.ricercheConfigurazione.enable", true, true);
		return "true".equalsIgnoreCase(cacheEnabled);
	}
	public boolean isAbilitataJmxCache_ricercheConfigurazione() throws UtilsException{
		String cacheEnabled = this.getProperty("cache.ricercheConfigurazione.jmx.enable", false, true);
		return "true".equalsIgnoreCase(cacheEnabled);
	}
	public boolean isDebugCache_ricercheConfigurazione() throws UtilsException{
		String cacheEnabled = this.getProperty("cache.ricercheConfigurazione.debug", false, true);
		return "true".equalsIgnoreCase(cacheEnabled);
	}
	
	public CacheAlgorithm getAlgoritmoCache_ricercheConfigurazione() throws UtilsException{
		String cacheV = this.getProperty("cache.ricercheConfigurazione.algoritmo", false, true);
		if(cacheV!=null && StringUtils.isNotEmpty(cacheV)) {
			return CacheAlgorithm.valueOf(cacheV.toUpperCase());
		}
		return null;
	}
	
	public Integer getDimensioneCache_ricercheConfigurazione() throws UtilsException{
		return _getIntegerValueCache("cache.ricercheConfigurazione.dimensione");
	}
	public Integer getItemIdleTimeCache_ricercheConfigurazione() throws UtilsException{
		return _getIntegerValueCache("cache.ricercheConfigurazione.itemIdleTime");
	}
	public Integer getItemLifeSecondCache_ricercheConfigurazione() throws UtilsException{
		return _getIntegerValueCache("cache.ricercheConfigurazione.itemLifeSecond");
	}
	
	
	private Integer _getIntegerValueCache(String name) throws UtilsException{
		String cacheV = this.getProperty(name, false, true);
		if(cacheV!=null && StringUtils.isNotEmpty(cacheV)) {
			Integer i = Integer.valueOf(cacheV);
			if(i.intValue()>0) {
				return i;
			}
		}
		return null;
	}
	
	public String getJmxPdD_externalConfiguration() throws UtilsException{
		return this.getProperty("configurazioni.risorseJmxPdd.configurazioneNodiRun",false,true);
	}
	
	public String getJmxPdD_backwardCompatibilityPrefix() {
		return "configurazioni.risorseJmxPdd.";
	}
	
	public Properties getJmxPdD_backwardCompatibilityProperties() throws UtilsException{
		
		String prefix = getJmxPdD_backwardCompatibilityPrefix();
		
		Properties p = new Properties();
		Enumeration<?> en = this.reader.propertyNames();
		while (en.hasMoreElements()) {
			Object object = en.nextElement();
			if(object instanceof String) {
				String key = (String) object;
				if(key.contains(prefix)) {
					String newKey = key.replace(prefix, "");
					p.put(newKey, this.reader.getValueConvertEnvProperties(key));
				}
			}
		}
		return p;
		
	}
	
	
	public boolean isPluginsEnabled() throws UtilsException{
		String cacheEnabled = this.getProperty("plugins.enabled", true, true);
		return "true".equalsIgnoreCase(cacheEnabled);
	}
	public Integer getPluginsSeconds() throws UtilsException{
		String cacheV = this.getProperty("plugins.seconds", false, true);
		if(cacheV!=null && StringUtils.isNotEmpty(cacheV)) {
			Integer i = Integer.valueOf(cacheV);
			if(i.intValue()>0) {
				return i;
			}
		}
		return 300;
	}
	
	
	public int getTransazioniDettaglioVisualizzazioneMessaggiThreshold() throws UtilsException{
		return Integer.valueOf(this.getProperty("transazioni.dettaglio.visualizzazioneMessaggi.threshold", true, true));
	}
	
	
	public boolean isSecurityLoadBouncyCastle() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "security.addBouncyCastleProvider");
		return this.parse(b, false);
	}

	public String getEnvMapConfig() throws UtilsException{
		return this.readProperty(false, "env.map.config");
	}
	public boolean isEnvMapConfigRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "env.map.required");
		return this.parse(b, false);
	}
	
	public String getHSMConfigurazione() throws UtilsException{
		return this.readProperty(false, "hsm.config");
	}
	public boolean isHSMRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "hsm.required");
		return this.parse(b, false);
	}
	public boolean isHSMKeyPasswordConfigurable() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "hsm.keyPassword");
		return this.parse(b, false);
	}
	
	public String getBYOKConfig() throws UtilsException{
		return this.readProperty(false, "byok.config");
	}
	public boolean isBYOKConfigRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "byok.required");
		return this.parse(b, false);
	}
	public String getBYOKEnvSecretsConfig() throws UtilsException{
		return this.readProperty(false, "byok.env.secrets.config");
	}
	public boolean isBYOKEnvSecretsConfigRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "byok.env.secrets.required");
		return this.parse(b, false);
	}
}
