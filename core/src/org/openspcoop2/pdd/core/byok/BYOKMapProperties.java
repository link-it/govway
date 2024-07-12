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
package org.openspcoop2.pdd.core.byok;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.certificate.byok.BYOKInstance;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.byok.BYOKMode;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;
import org.openspcoop2.utils.properties.MapProperties;
import org.slf4j.Logger;

/**
 * BYOKMapProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class BYOKMapProperties extends MapProperties {

	private static final String FILE_NAME = "govway.secrets.properties";
	
	private static final String PROP_KSM_PREFIX = "ksm.";
	private static final String ENV_KSM_PREFIX = MapProperties.ENV_PREFIX+PROP_KSM_PREFIX;
	private static final String JAVA_KSM_PREFIX = MapProperties.JAVA_PREFIX+PROP_KSM_PREFIX;
	
	private static final String PROP_SECURITY_PREFIX = "security.";
	private static final String ENV_SECURITY_PREFIX = MapProperties.ENV_PREFIX+PROP_SECURITY_PREFIX;
	private static final String JAVA_SECURITY_PREFIX = MapProperties.JAVA_PREFIX+PROP_SECURITY_PREFIX;
	
	// default true
	private static final String PROP_WRAPPED_PREFIX = "wrapped."; 
	private static final String ENV_WRAPPED_PREFIX = MapProperties.ENV_PREFIX+PROP_WRAPPED_PREFIX;
	private static final String JAVA_WRAPPED_PREFIX = MapProperties.JAVA_PREFIX+PROP_WRAPPED_PREFIX;
	
	// default false
	private static final String PROP_UNWRAP_AFTER_GOVWAY_STARTUP_PREFIX = "govway-run-only.unwrap-after-startup."; 
	private static final String ENV_UNWRAP_AFTER_GOVWAY_STARTUP_PREFIX = MapProperties.ENV_PREFIX+PROP_UNWRAP_AFTER_GOVWAY_STARTUP_PREFIX;
	private static final String JAVA_UNWRAP_AFTER_GOVWAY_STARTUP_PREFIX = MapProperties.JAVA_PREFIX+PROP_UNWRAP_AFTER_GOVWAY_STARTUP_PREFIX;
	
	private static final String UNWRAP_DEFAULT_MODE = "unwrap.default.mode";
	private static final String UNWRAP_DEFAULT_ID = "unwrap.default.id";
	private static final String UNWRAP_MODE_SECURITY = "security"; 
	private static final String UNWRAP_MODE_KSM = "ksm"; 
	
	// ksm.<id>.param.<paramName>=<paramValue>
	private static final String KSM_PREFIX = "ksm.";
	private static final String KSM_PARAM_PREFIX = ".param.";
	private static String getKsmParamPrefixPropertyName(String ksmId) {
		return KSM_PREFIX+ksmId+KSM_PARAM_PREFIX;
	}
		
	private static final String ERROR_SUFFIX_UNKNOW = "' unknown";
	
	private static final String ERROR_DEFAULT_MODE_NOT_FOUND = ") an unwrap mode has not been defined (security/ksm); specifying the mode is mandatory if a default unwrap mode is not defined.";
	
	private String defaultUnwrapId = null;
	private Boolean defaultUnwrapModeSecurity = null;
	
	private boolean useSecurityEngine = false;
	private String securityPolicy;
	private String securityRemotePolicy;
	
	private Map<String, DriverBYOK> mapDriverSecurity = new HashMap<>();
	
	private Map<String, Map<String, String>> mapKsmInput = new HashMap<>();
	
	private Map<String, Object> dynamicMap = null;
	private boolean checkJmxPrefixOperazioneNonRiuscita = false;
	
	private boolean existsUnwrapPropertiesAfterGovWayStartup = false;
	public boolean isExistsUnwrapPropertiesAfterGovWayStartup() {
		return this.existsUnwrapPropertiesAfterGovWayStartup;
	}
	private boolean isGovWayStarted = false; // usato per caricare variabili che devono essere trattate solo sui nodi run
	public boolean isGovWayStarted() {
		return this.isGovWayStarted;
	}
	public void setGovWayStarted(boolean isGovWayStarted) {
		this.isGovWayStarted = isGovWayStarted;
	}

	/** Copia Statica */
	private static BYOKMapProperties secretsProperties = null;
	
	public BYOKMapProperties(Logger log, boolean throwNotFound,
			boolean useSecurityEngine,
			Map<String, Object> dynamicMapParam, boolean checkJmxPrefixOperazioneNonRiuscita) throws UtilsException {
		this(log, FILE_NAME, throwNotFound, 
				useSecurityEngine,
				dynamicMapParam, checkJmxPrefixOperazioneNonRiuscita);
	}
	public BYOKMapProperties(Logger log, String fileName, boolean throwNotFound, 
			boolean useSecurityEngine,
			Map<String, Object> dynamicMapParam, boolean checkJmxPrefixOperazioneNonRiuscita) throws UtilsException {
		super(log, fileName, throwNotFound);
		
		String securityPolicyB = BYOKManager.getSecurityEngineGovWayPolicy();
		String securityRemotePolicyB = BYOKManager.getSecurityRemoteEngineGovWayPolicy();
		if(securityPolicyB!=null && StringUtils.isNotEmpty(securityPolicyB)) {
			this.securityPolicy = securityPolicyB;
		}
		if(securityRemotePolicyB!=null && StringUtils.isNotEmpty(securityRemotePolicyB)) {
			this.securityRemotePolicy = securityRemotePolicyB;
		}
		this.useSecurityEngine = useSecurityEngine;
		this.dynamicMap = dynamicMapParam;
		this.checkJmxPrefixOperazioneNonRiuscita = checkJmxPrefixOperazioneNonRiuscita;
	}
	
	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(Logger log, String fileName, boolean throwNotFound,  
			boolean useSecurityEngine,
			Map<String, Object> dynamicMapParam, boolean checkJmxPrefixOperazioneNonRiuscita){

		try {
			BYOKMapProperties.secretsProperties = new BYOKMapProperties(log, fileName, throwNotFound, 
					useSecurityEngine, 
					dynamicMapParam, checkJmxPrefixOperazioneNonRiuscita);	
		    return true;
		}
		catch(Exception e) {
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di BYOKMapProperties
	 * 
	 */
	public static BYOKMapProperties getInstance(){
	   return BYOKMapProperties.secretsProperties;
	}

	@Override
	public void loadPropertyInEnvironment(String key) throws UtilsException {
		
		this.loadDefaultUnwrap();
		
		if(key.startsWith(ENV_PREFIX) && key.length()>ENV_PREFIX.length() &&
				!key.startsWith(ENV_KSM_PREFIX) &&
				!key.startsWith(ENV_SECURITY_PREFIX) &&
				!key.startsWith(ENV_WRAPPED_PREFIX) &&
				!key.startsWith(ENV_UNWRAP_AFTER_GOVWAY_STARTUP_PREFIX)) {
			loadEnvPropertyInEnvironment(key);
		}
		else if(key.startsWith(JAVA_PREFIX) && key.length()>JAVA_PREFIX.length() &&
				!key.startsWith(JAVA_KSM_PREFIX) &&
				!key.startsWith(JAVA_SECURITY_PREFIX) &&
				!key.startsWith(JAVA_WRAPPED_PREFIX) &&
				!key.startsWith(JAVA_UNWRAP_AFTER_GOVWAY_STARTUP_PREFIX)) {
			loadJavaPropertyInEnvironment(key);
		}
	}
	public void loadEnvPropertyInEnvironment(String key) throws UtilsException {
		String envKey = key.substring(ENV_PREFIX.length());
		String value = this.reader.getValue_convertEnvProperties(key);
		
		if(this.isUnwrapAfterGovWayStartup(false,envKey)){
			this.existsUnwrapPropertiesAfterGovWayStartup = true;
			if(!this.isGovWayStarted) {
				return; // verrà effettuato l'unwrap dopo che GovWay ha completato lo startup
			}
		}
		
		if(this.isWrapped(false,envKey)) {
			processWrappedValue(key, envKey, value, 
					false); // envProperty
		}
		else {
			setEnvProperty(envKey, value);
		}
	}
	public void loadJavaPropertyInEnvironment(String key) throws UtilsException {
		String envKey = key.substring(JAVA_PREFIX.length());
		String value = this.reader.getValue_convertEnvProperties(key);
		
		if(this.isUnwrapAfterGovWayStartup(true,envKey)) {
			this.existsUnwrapPropertiesAfterGovWayStartup = true;
			if(!this.isGovWayStarted) {
				return; // verrà effettuato l'unwrap dopo che GovWay ha completato lo startup
			}
		}
		
		if(this.isWrapped(true,envKey)) {
			processWrappedValue(key, envKey, value, 
					true); // envProperty
		}
		else {
			setJavaProperty(envKey, value);
		}
	}
	private void processWrappedValue(String key, String envKey, String value, boolean javaProperty) throws UtilsException {
		String plainValue = null;
		String securityId = this.getUnwrapId(javaProperty, envKey);
		try {
			if(this.isWrappedBySecurity(javaProperty, envKey)) {
				String p = this.securityRemotePolicy!=null ? this.securityRemotePolicy : this.securityPolicy;
				if(!this.useSecurityEngine && securityId!=null && securityId.equals(p)) {
					// securityNonInizializzato, skip
					return;
				}
				plainValue = getDriverBYOK(securityId).unwrapAsString(value, securityId, true);
			}
			else {
				plainValue = unwrapByKsmId(value, securityId);
			}
		}catch(Exception e) {
			throw new UtilsException("["+key+"] "+e.getMessage(),e);
		}
		if(plainValue==null) {
			throw new UtilsException("("+key+") unwrapped value null");
		}
		else {
			if(javaProperty) {
				setJavaProperty(envKey, plainValue);
			}
			else{
				setEnvProperty(envKey, plainValue);
			}
		}
	}
	
	private void loadDefaultUnwrap() throws UtilsException {
		String tmp = this.reader.getValue_convertEnvProperties(UNWRAP_DEFAULT_ID);
		if(tmp!=null) {
			this.defaultUnwrapId = tmp.trim();
			if(StringUtils.isEmpty(this.defaultUnwrapId)) {
				this.defaultUnwrapId = null;
			}
		}
		
		if(this.defaultUnwrapId!=null) {
			tmp = this.reader.getValue_convertEnvProperties(UNWRAP_DEFAULT_MODE);
			if(tmp!=null && StringUtils.isNotEmpty(tmp.trim())) {
				if(UNWRAP_MODE_SECURITY.equals(tmp.trim())) {
					this.defaultUnwrapModeSecurity = true; 
				}
				else if(UNWRAP_MODE_KSM.equals(tmp.trim())) {
					this.defaultUnwrapModeSecurity = false; 
				}
				else {
					throw new UtilsException(UNWRAP_DEFAULT_MODE +" '"+tmp.trim()+ERROR_SUFFIX_UNKNOW);
				}
			}
			else {
				this.defaultUnwrapModeSecurity = true; // default by security id
			}
		}
	}
	
	private boolean isWrapped(boolean javaProperty, String key) throws UtilsException {
		if(this.reader==null) {
			return false; // non inizializzato
		}
		String prefix = javaProperty ? JAVA_WRAPPED_PREFIX : ENV_WRAPPED_PREFIX;
		String tmp = this.reader.getValue_convertEnvProperties(prefix+key);
		if(tmp!=null && StringUtils.isNotEmpty(tmp.trim())) {
			if("true".equals(tmp.trim())) {
				return true;
			}
			else if("false".equals(tmp.trim())) {
				return false;
			}
			else {
				throw new UtilsException(prefix+key+" with value '"+tmp.trim()+ERROR_SUFFIX_UNKNOW);
			}
		}
		else {
			return true; // default
		}
	}
	
	private boolean isUnwrapAfterGovWayStartup(boolean javaProperty, String key) throws UtilsException {
		if(this.reader==null) {
			return false; // non inizializzato
		}
		String prefix = javaProperty ? JAVA_UNWRAP_AFTER_GOVWAY_STARTUP_PREFIX : ENV_UNWRAP_AFTER_GOVWAY_STARTUP_PREFIX;
		String tmp = this.reader.getValue_convertEnvProperties(prefix+key);
		if(tmp!=null && StringUtils.isNotEmpty(tmp.trim())) {
			if("true".equals(tmp.trim())) {
				return true;
			}
			else if("false".equals(tmp.trim())) {
				return false;
			}
			else {
				throw new UtilsException(prefix+key+" with value '"+tmp.trim()+ERROR_SUFFIX_UNKNOW);
			}
		}
		else {
			return false; // default
		}
	}
	
	@Override
	protected boolean obfuscate(boolean java, String key) {
		try {
			return isWrapped(java, key);
		}catch(Exception e) {
			return true;
		}
	}
	
	private boolean isWrappedBySecurity(boolean javaProperty, String key) throws UtilsException {
		if(this.reader==null) {
			return false; // non inizializzato
		}
		String pSecurityName = javaProperty ? JAVA_SECURITY_PREFIX : ENV_SECURITY_PREFIX;
		String tmp = this.reader.getValue_convertEnvProperties(pSecurityName+key);
		if(tmp!=null && StringUtils.isNotEmpty(tmp.trim())) {
			return true;
		}
		else {
			String pKsmName = javaProperty ? JAVA_KSM_PREFIX : ENV_KSM_PREFIX;
			tmp = this.reader.getValue_convertEnvProperties(pKsmName+key);
			if(tmp!=null && StringUtils.isNotEmpty(tmp.trim())) {
				return false;
			}
			else if(this.defaultUnwrapModeSecurity!=null) {
				return this.defaultUnwrapModeSecurity.booleanValue();
			}
			else {
				String prefix = javaProperty ? JAVA_PREFIX : ENV_PREFIX;
				throw new UtilsException("("+prefix+key+ERROR_DEFAULT_MODE_NOT_FOUND);
			}
		}
	}
	
	private String getUnwrapId(boolean javaProperty, String key) throws UtilsException {
		if(this.reader==null) {
			return null; // non inizializzato
		}
		String pSecurityName = javaProperty ? JAVA_SECURITY_PREFIX : ENV_SECURITY_PREFIX;
		String tmp = this.reader.getValue_convertEnvProperties(pSecurityName+key);
		if(tmp!=null && StringUtils.isNotEmpty(tmp.trim())) {
			return tmp.trim();
		}
		else {
			String pKsmName = javaProperty ? JAVA_KSM_PREFIX : ENV_KSM_PREFIX;
			tmp = this.reader.getValue_convertEnvProperties(pKsmName+key);
			if(tmp!=null && StringUtils.isNotEmpty(tmp.trim())) {
				return tmp.trim();
			}
			else if(this.defaultUnwrapId!=null) {
				return this.defaultUnwrapId;
			}
			else {
				String prefix = javaProperty ? JAVA_PREFIX : ENV_PREFIX;
				throw new UtilsException("("+prefix+key+ERROR_DEFAULT_MODE_NOT_FOUND);
			}
		}
	}
	
	private DriverBYOK getDriverBYOK(String securityId) {
		if(!this.mapDriverSecurity.containsKey(securityId)) {
			initDriverBYOK(securityId);
		}
		return this.mapDriverSecurity.get(securityId);
	}
	private synchronized void initDriverBYOK(String securityId) {
		this.mapDriverSecurity.computeIfAbsent(securityId, k -> new DriverBYOK(this.log, this.securityPolicy, this.securityRemotePolicy,
				newDynamicMap(), this.checkJmxPrefixOperazioneNonRiuscita));
	}

	private Map<String, Object> newDynamicMap(){
		if(this.dynamicMap==null) {
			initDynamicMap();
		}
		// cro una nuova mappa per non sovrascrivere le varie variabili di input
		Map<String, Object> map = new HashMap<>();
		if(this.dynamicMap!=null && !this.dynamicMap.isEmpty()) {
			map.putAll(this.dynamicMap);
		}
		return map;
	}
	private synchronized void initDynamicMap() {
		if(this.dynamicMap==null) {
			this.dynamicMap = DriverBYOK.buildDynamicMap(this.log);
		}
	}
	
	private Map<String, String> readKsmInputMap(String ksmId) throws UtilsException{
		Map<String, String> map = new HashMap<>();
		Properties p = this.reader.readProperties_convertEnvProperties(getKsmParamPrefixPropertyName(ksmId));
		if(p!=null && !p.isEmpty()) {
			for (Map.Entry<Object,Object> entry : p.entrySet()) {
				if(entry.getKey() instanceof String && entry.getValue() instanceof String) {
					map.put((String)entry.getKey(), (String)entry.getValue());
				}
			}
		}
		return map;
	}
	private Map<String, String> getKsmInputMap(String ksmId) {
		if(!this.mapKsmInput.containsKey(ksmId)) {
			initKsmInputMap(ksmId);
		}
		return this.mapKsmInput.get(ksmId);
	}
	private synchronized void initKsmInputMap(String ksmId) {
		this.mapKsmInput.computeIfAbsent(ksmId, k -> {
			try {
				return readKsmInputMap(ksmId);
			} catch (UtilsException e) {
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
		});
	}
	
	private String unwrapByKsmId(String value, String ksmId) throws UtilsException {
		
		Map<String, String> inputMap = getKsmInputMap(ksmId);
		BYOKRequestParams params = DriverBYOK.getBYOKRequestParamsByKsmId(ksmId, inputMap, newDynamicMap());
		if(!BYOKMode.UNWRAP.equals(params.getConfig().getMode())) {
			throw new UtilsException("Ksm '"+ksmId+"' unusable in unwrap operation");
		}
		BYOKInstance instance = BYOKInstance.newInstance(this.log, params, value.getBytes());
		byte[] unwrappedValue = DriverBYOK.processInstance(instance, this.checkJmxPrefixOperazioneNonRiuscita);
		return new String(unwrappedValue);

	}
}
