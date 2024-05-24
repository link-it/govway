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



package org.openspcoop2.utils.properties;


import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.slf4j.Logger;


/**
 * MapProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class MapProperties {	

	private static final String FILE_NAME = "govway.map.properties";
	
	public static final String ENV_PREFIX = "env.";
	public static final String JAVA_PREFIX = "java.";
	
	public static final String OBFUSCATED_ENV_KEYS = "obfuscated.env.keys";
	public static final String OBFUSCATED_JAVA_KEYS = "obfuscated.java.keys";
	
	public static final String OBFUSCATED_MODE = "obfuscated.mode";
	public static final String OBFUSCATED_MODE_NON_INIZIALIZZATO = "not initialized";
	public static final String OBFUSCATED_MODE_NONE = "none";
	public static final String OBFUSCATED_MODE_DIGEST = "digest";
	public static final String OBFUSCATED_MODE_STATIC = "static";
	
	public static final String OBFUSCATED_DIGEST = "obfuscated.digest";
	public static final String OBFUSCATED_DIGEST_DEFAULT = "SHA-256";
	
	public static final String OBFUSCATED_STATIC = "obfuscated.static";
	public static final String OBFUSCATED_STATIC_DEFAULT = "******";
	
	
	
	
	/** Logger utilizzato per errori eventuali. */
	protected Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'govway.map.properties' */
	protected PropertiesReader reader;
	protected String content;

	/** Copia Statica */
	private static MapProperties mapProperties = null;

	/** Variabili impostate */
	protected SortedMap<String> envMap = new SortedMap<>();
	protected SortedMap<String> javaMap = new SortedMap<>();
	public SortedMap<String> getEnvMap() {
		return this.envMap;
	}
	public SortedMap<String> getJavaMap() {
		return this.javaMap;
	}
	

	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public MapProperties(Logger log, boolean throwNotFound) throws UtilsException {
		this(log, FILE_NAME, throwNotFound);
	}
	public MapProperties(Logger log, String fileName, boolean throwNotFound) throws UtilsException {

		if(log==null) {
			this.log = LoggerWrapperFactory.getLogger(MapProperties.class);
		}
		else {
			this.log = log;
		}
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = readProperties(fileName);
		
		if(propertiesReader!=null) {
			this.reader = new PropertiesReader(propertiesReader, true);
		}
		else if(throwNotFound){
			throw new UtilsException("Config '"+fileName+"' not found");
		}
		
	}
	private Properties readProperties(String fileName) throws UtilsException {
		File fCheck = new File(fileName);
		if(fCheck.exists()) {
			if(!fCheck.canRead()) {
				throw new UtilsException("File '"+fCheck.getAbsolutePath()+"' cannot read");
			}
			try{
				this.content = FileSystemUtilities.readFile(fCheck);
			}catch(Exception e) {
				throw new UtilsException("File '"+fCheck.getAbsolutePath()+"' read failed: "+e.getMessage(),e);
			}
		}
		else {
			String uri = fileName;
			if(!fileName.startsWith("/")) {
				uri = "/" + fileName;
			}
			try(java.io.InputStream properties = MapProperties.class.getResourceAsStream(uri)){
				if(properties!=null) {
					this.content = Utilities.getAsString(properties, Charset.UTF_8.getValue());
				}
			}catch(Exception e) {
				throw new UtilsException("Uri '"+uri+"' access failed: "+e.getMessage(),e);
			}
		}
		
		Properties props = null;
		if(this.content!=null) {
			props = new Properties();
			try(Reader properties = new StringReader(this.content)){
				props.load(properties);
			}catch(Exception e) {
				throw new UtilsException("Config '"+fileName+"' access failed: "+e.getMessage(),e);
			}
		}
		
		return props;
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(Logger log, String fileName, boolean throwNotFound){

		try {
		    MapProperties.mapProperties = new MapProperties(log, fileName, throwNotFound);	
		    return true;
		}
		catch(Exception e) {
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di MapProperties
	 * 
	 */
	public static MapProperties getInstance(){
	   return MapProperties.mapProperties;
	}
    
	
	public void initEnvironment() throws UtilsException {	
		if(this.content!=null) {
			SortedMap<String> convertTextToProperties = PropertiesUtilities.convertTextToSortedMap(this.content, false);
			if(convertTextToProperties!=null && !convertTextToProperties.isEmpty()) {
				List<String> keys = convertTextToProperties.keys();
				if(keys!=null && !keys.isEmpty()) {
					for (String key : keys) {
						loadPropertyInEnvironment(key);
					}
				}
			}
		}
	}
	
	public void loadPropertyInEnvironment(String key) throws UtilsException {
		if(key.startsWith(ENV_PREFIX) && key.length()>ENV_PREFIX.length()) {
			String envKey = key.substring(ENV_PREFIX.length());
			String value = this.reader.getValue_convertEnvProperties(key);
			setEnvProperty(envKey, value);
		}
		else if(key.startsWith(JAVA_PREFIX) && key.length()>JAVA_PREFIX.length()) {
			String envKey = key.substring(JAVA_PREFIX.length());
			String value = this.reader.getValue_convertEnvProperties(key);
			setJavaProperty(envKey, value);
		}
	}

	protected void setJavaProperty(String envKey, String value) throws UtilsException {
		if(value!=null) {
			System.setProperty(envKey, value);
			
			// per preservare l'ordine
			if(this.javaMap.containsKey(envKey)) {
				this.javaMap.remove(envKey);
			}
			this.javaMap.put(envKey, value);
		}
	}
	
	protected void setEnvProperty(String envKey, String value) throws UtilsException {
		if(value!=null) {
			Utilities.setEnvProperty(envKey, value);
			
			// per preservare l'ordine
			if(this.envMap.containsKey(envKey)) {
				this.envMap.remove(envKey);
			}
			this.envMap.put(envKey, value);
		}
	}
	
	public List<String> getObfuscatedEnvKeys() throws UtilsException{
		return getObfuscatedKeys(OBFUSCATED_ENV_KEYS);
	}
	public List<String> getObfuscatedJavaKeys() throws UtilsException{
		return getObfuscatedKeys(OBFUSCATED_JAVA_KEYS);
	}
	private List<String> getObfuscatedKeys(String pName) throws UtilsException{
		List<String> l = new ArrayList<>();
		if(this.reader==null) {
			return l; // non inizializzato
		}
		if(isObfuscatedModeEnabled()) {
			String value = this.reader.getValue_convertEnvProperties(pName);
			if(value!=null && StringUtils.isNotEmpty(value.trim())) {
				value = value.trim();
				if(value.contains(",")) {
					fillObfuscatedKeys(value, l);
				}
				else {
					l.add(value);
				}
			}
		}
		return l;
	}
	private void fillObfuscatedKeys(String value, List<String> l) {
		String [] tmp = value.split(",");
		if(tmp!=null && tmp.length>0) {
			for (String t : tmp) {
				if(t!=null && StringUtils.isNotEmpty(t.trim())) {
					l.add(t);
				}
			}
		}
	}
	
	public boolean isObfuscatedModeEnabled() throws UtilsException {
		if(this.reader==null) {
			return false; // non inizializzato
		}
		String value = this.reader.getValue_convertEnvProperties(OBFUSCATED_MODE);
		if(value!=null && StringUtils.isNotEmpty(value.trim())) {
			value = value.trim();
			if(OBFUSCATED_MODE_DIGEST.equals(value) || (OBFUSCATED_MODE_STATIC.equals(value))) {
				 return true;
			}
			else if(OBFUSCATED_MODE_NONE.equals(value)) {
				return false;
			}
			else {
				throw new UtilsException("["+OBFUSCATED_MODE+"] unknown mode '"+value+"'");
			}
		}
		else {
			return true; // default
		}
	}
	
	public String getObfuscateModeDescription() throws UtilsException {
		if(this.reader==null) {
			return OBFUSCATED_MODE_NON_INIZIALIZZATO; // non inizializzato
		}
		String obfuscateMode = OBFUSCATED_MODE_NONE;
		if(this.isObfuscatedModeStatic()) {
			obfuscateMode = OBFUSCATED_MODE_STATIC+" ("+this.getObfuscatedModeStaticValue()+")";
		}
		else if(this.isObfuscatedModeDigest()) {
			obfuscateMode = OBFUSCATED_MODE_DIGEST+" ("+this.getObfuscatedModeDigestAlgo()+")";
		}
		return obfuscateMode;
	}	
	
	public boolean isObfuscatedModeStatic() throws UtilsException {
		return getObfuscatedModeStaticValue()!=null;
	}
	public String getObfuscatedModeStaticValue() throws UtilsException {
		if(this.reader==null) {
			return null; // non inizializzato
		}
		String value = this.reader.getValue_convertEnvProperties(OBFUSCATED_MODE);
		if(value!=null && StringUtils.isNotEmpty(value.trim())) {
			value = value.trim();
			if(OBFUSCATED_MODE_STATIC.equals(value)) {
				String s = this.reader.getValue_convertEnvProperties(OBFUSCATED_STATIC);
				if(s!=null && StringUtils.isNotEmpty(s.trim())) {
					return s.trim();
				}
				else {
					return OBFUSCATED_STATIC_DEFAULT; // default
				}
			}
			else if(OBFUSCATED_MODE_DIGEST.equals(value) || (OBFUSCATED_MODE_NONE.equals(value))) {
				return null;
			}
			else {
				throw new UtilsException("["+OBFUSCATED_MODE+"] unknown property value '"+value+"'");
			}
		}
		else {
			return null; 
		}
	}
	
	public boolean isObfuscatedModeDigest() throws UtilsException {
		return getObfuscatedModeDigestAlgo()!=null;
	}
	public String getObfuscatedModeDigestAlgo() throws UtilsException {
		if(this.reader==null) {
			return null; // non inizializzato
		}
		String value = this.reader.getValue_convertEnvProperties(OBFUSCATED_MODE);
		if(value!=null && StringUtils.isNotEmpty(value.trim())) {
			value = value.trim();
			if(OBFUSCATED_MODE_DIGEST.equals(value)) {
				String s = this.reader.getValue_convertEnvProperties(OBFUSCATED_DIGEST);
				if(s!=null && StringUtils.isNotEmpty(s.trim())) {
					return s.trim();
				}
				else {
					return OBFUSCATED_DIGEST_DEFAULT; // default
				}
			}
			else if(OBFUSCATED_MODE_STATIC.equals(value) || (OBFUSCATED_MODE_NONE.equals(value))) {
				return null;
			}
			else {
				throw new UtilsException("["+OBFUSCATED_MODE+"] unknown property value '"+value+"'");
			}
		}
		else {
			return OBFUSCATED_DIGEST_DEFAULT; // default
		}
	}

	protected boolean obfuscate(boolean java, String key) {
		if(java || key!=null) {
			// parametri forniti nel caso venga re-implementato il metodo
		}
		return true;
	}
	
	public String obfuscateJavaProperty(String key, String value) throws UtilsException {
		return obfuscate(true, key, value);
	}
	public String obfuscateEnvProperty(String key, String value) throws UtilsException {
		return obfuscate(false, key, value);
	}
	protected String obfuscate(boolean java, String key, String value) throws UtilsException {
		if(!this.obfuscate(java, key)) {
			return value;
		}
		if(!isObfuscatedModeEnabled() || value==null || StringUtils.isEmpty(value)) {
			return value;
		}
		if(this.isObfuscatedModeStatic()) {
			return this.getObfuscatedModeStaticValue();
		}
		else if(this.isObfuscatedModeDigest()) {
			try {
				return Base64Utilities.encodeAsString(MessageDigest.getInstance(this.getObfuscatedModeDigestAlgo()).digest(value.getBytes()));
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
		else {
			throw new UtilsException("["+OBFUSCATED_MODE+"] unsupported mode");
		}
	}
}
