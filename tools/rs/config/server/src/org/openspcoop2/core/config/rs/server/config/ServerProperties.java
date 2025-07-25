/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.slf4j.Logger;

/**     
 * ServerProperties
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServerProperties  {

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;
	
	/** Reader delle proprieta' impostate nel file 'rs-api-config.properties' */
	private ServerInstanceProperties reader;
	
	/** Copia Statica */
	private static ServerProperties serverProperties = null;
	
	
	private ServerProperties(String confDir,Logger log) throws UtilsException {

		if(log!=null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger(ServerProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		try (java.io.InputStream properties = DatasourceProperties.class.getResourceAsStream("/rs-api-config.properties");){  
			if(properties==null){
				throw new UtilsException("File '/rs-api-config.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			logAndThrow("Riscontrato errore durante la lettura del file 'rs-api-config.properties': "+e.getMessage(),e);
		}

		this.reader = new ServerInstanceProperties(propertiesReader, this.log, confDir);
	}
	
	private void logAndThrow(String msg, Exception e) throws UtilsException {
		this.log.error(msg,e);
	    throw new UtilsException(msg,e);
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String confDir,Logger log){

		try {
		    ServerProperties.serverProperties = new ServerProperties(confDir,log);	
		    return true;
		}
		catch(Exception e) {
			log.error("Errore durante l'inizializzazione del ServerProperties: "+e.getMessage(),e);		   
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di Properties
	 * 
	 */
	public static ServerProperties getInstance() throws UtilsException{
		if(ServerProperties.serverProperties==null){
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (ServerProperties.class) {
				throw new UtilsException("ServerProperties non inizializzato");
			}
	    }
	    return ServerProperties.serverProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		ServerProperties.serverProperties.reader.setLocalObjectImplementation(prop);
	}








	/* ********  M E T O D I  ******** */

	private boolean parse(BooleanNullable b, boolean defaultValue) {
		return (b!=null && b.getValue()!=null) ? b.getValue() : defaultValue;
	}
	
	public String readProperty(boolean required,String property) throws UtilsException{
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
	
	private BooleanNullable readBooleanProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		if(tmp==null && !required) {
			return BooleanNullable.NULL(); // se e' required viene sollevata una eccezione dal metodo readProperty
		}
		if(!"true".equalsIgnoreCase(tmp) && !"false".equalsIgnoreCase(tmp)){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (true/value expected)");
		}
		return Boolean.parseBoolean(tmp) ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
	}
	
	public Properties getProperties() throws UtilsException{
		Properties p = new Properties();
		Enumeration<?> names = this.reader.propertyNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			p.put(name, this.reader.getValueConvertEnvProperties(name));
		}
		return p;
	}
	
	private AuthorizationConfig authConfig = null;
	private synchronized void initAuthorizationConfig() throws UtilsException {
		if(this.authConfig==null) {
			this.authConfig = new AuthorizationConfig(getProperties());
		}
	}
	public AuthorizationConfig getAuthorizationConfig() throws UtilsException {
		if(this.authConfig==null) {
			this.initAuthorizationConfig();
		}
		return this.authConfig;
	}

	
	public String getConfDirectory() throws UtilsException {
		return this.readProperty(false, "confDirectory");
	}
	public String getProtocolloDefault() throws UtilsException {
		return this.readProperty(true, "protocolloDefault");
	}
	
	public boolean isJdbcCloseConnectionCheckIsClosed() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(true, "jdbc.closeConnection.checkIsClosed");
		return this.parse(b, true);
	}
	public boolean isJdbcCloseConnectionCheckAutocommit() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(true, "jdbc.closeConnection.checkAutocommit");
		return this.parse(b, true);
	}
	
	public boolean isEnabledAutoMapping() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "enableAutoMapping"));
	}
	
	public boolean isEnabledAutoMappingEstraiXsdSchemiFromWsdlTypes() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "enableAutoMapping_estraiXsdSchemiFromWsdlTypes"));
	}
	
	public boolean isValidazioneDocumenti() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "validazioneDocumenti"));
	}
	
	public boolean isUpdateInterfacciaApiUpdateIfExists() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "updateInterfacciaApi.updateIfExists"));
	}
	
	public boolean isUpdateInterfacciaApiDeleteIfNotFound() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "updateInterfacciaApi.deleteIfNotFound"));
	}
	
	public boolean isSoggettiApplicativiCredenzialiBasicPermitSameCredentials() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "soggettiApplicativi.credenzialiBasic.permitSameCredentials"));
	}
	public boolean isSoggettiApplicativiCredenzialiSslPermitSameCredentials() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "soggettiApplicativi.credenzialiSsl.permitSameCredentials"));
	}
	public boolean isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "soggettiApplicativi.credenzialiPrincipal.permitSameCredentials"));
	}
	
	public boolean isKeystoreJksPasswordRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "keystore.jks.passwordRequired");
		return parse(b, true);
	}
	public boolean isKeystoreJksKeyPasswordRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "keystore.jks.key.passwordRequired");
		return parse(b, true);
	}
	public boolean isKeystorePkcs12PasswordRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "keystore.pkcs12.passwordRequired");
		return parse(b, true);
	}
	public boolean isKeystorePkcs12KeyPasswordRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "keystore.pkcs12.key.passwordRequired");
		return parse(b, true);
	}
	
	public boolean isTruststoreJksPasswordRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "truststore.jks.passwordRequired");
		return parse(b, true);
	}
	public boolean isTruststorePkcs12PasswordRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "truststore.pkcs12.passwordRequired");
		return parse(b, true);
	}
	
	public Properties getApiYamlSnakeLimits() throws UtilsException{

		String pName = "api.yaml.snakeLimits";
		
		try{  
			Properties pNull = null;
			
			String file = this.readProperty(false, pName);
			if(file!=null && StringUtils.isNotEmpty(file)) {
				File f = new File(file);
				if(f.exists()) {
					if(!f.isFile()) {
						throw new UtilsException("Il file indicato '"+f.getAbsolutePath()+"' non è un file");
					}
					if(!f.canRead()) {
						throw new UtilsException("Il file indicato '"+f.getAbsolutePath()+"' non è accessibile in lettura");
					}
					try(InputStream is = new FileInputStream(f)){
						Properties p = new Properties();
						p.load(is);
						if (!p.isEmpty()){
							return p;
						}
					}
				}
			}
		
			return pNull;
			
		}catch(java.lang.Exception e) {
			throw new UtilsException("Proprieta' '"+pName+"' non impostate, errore:"+e.getMessage(),e);
		}
	}
	
	public boolean isDelete404() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "delete_404"));
	}
	
	public boolean isFindall404() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "findall_404"));
	}
	
	public boolean isConfigurazioneAllarmiEnabled() throws UtilsException{
		Boolean b = Boolean.parseBoolean(this.readProperty(true, "allarmi.enabled"));
		return b!=null && b.booleanValue();
	}
	public String getAllarmiConfigurazione() throws UtilsException{
		return this.readProperty(true, "allarmi.configurazione");
	}
	
	
	public boolean isSecurityLoadBouncyCastleProvider() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "security.addBouncyCastleProvider");
		return parse(b, false);
	}
	
	
	public String getEnvMapConfig() throws UtilsException{
		return this.readProperty(false, "env.map.config");
	}
	public boolean isEnvMapConfigRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "env.map.required");
		return this.parse(b, false);
	}
	
	
	public String getHSMConfigurazione() throws UtilsException {
		return this.readProperty(false, "hsm.config");
	}
	public boolean isHSMRequired() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "hsm.required"));
	}
	public boolean isHSMKeyPasswordConfigurable() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "hsm.keyPassword");
		return this.parse(b, false);
	}
	
	
	
	public String getBYOKConfigurazione() throws UtilsException{
		return this.readProperty(false, "byok.config");
	}
	public boolean isBYOKRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "byok.required");
		return parse(b, false);
	}
	public String getBYOKEnvSecretsConfig() throws UtilsException{
		return this.readProperty(false, "byok.env.secrets.config");
	}
	public boolean isBYOKEnvSecretsConfigRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "byok.env.secrets.required");
		return this.parse(b, false);
	}
	
	
	
	public String getOCSPConfigurazione() throws UtilsException {
		return this.readProperty(false, "ocsp.config");
	}
	public boolean isOCSPRequired() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "ocsp.required"));
	}	
	public boolean isOCSPLoadDefault() throws UtilsException {
		String p = this.readProperty(false, "ocsp.loadDefault");
		if(p!=null && StringUtils.isNotEmpty(p)) {
			return Boolean.parseBoolean(p);
		}
		return true;
	}	

	
	public String getConfigurazioneNodiRuntime() throws UtilsException{
		return this.readProperty(false, "configurazioni.configurazioneNodiRun");
	}
	
	
	
	public String getSoggettoDefault(String protocollo) throws UtilsException {
		String p = this.readProperty(false, protocollo+".soggetto");
		if(p!=null) {
			return p;
		}
		return this.readProperty(true, "soggetto");
	}


	public org.openspcoop2.utils.service.context.ContextConfig getContextConfig() throws UtilsException {
		org.openspcoop2.utils.service.context.ContextConfig config = new org.openspcoop2.utils.service.context.ContextConfig();
		config.setClusterId(this.readProperty(false, "clusterId"));
		config.setDump(Boolean.parseBoolean(this.readProperty(true, "dump")));
		config.setEmitTransaction(Boolean.parseBoolean(this.readProperty(true, "transaction")));
		config.setServiceType(this.readProperty(false, "service.type"));
		config.setServiceName(this.readProperty(false, "service.name"));
		config.setServiceVersion(Integer.parseInt(this.readProperty(false, "service.version")));
		return config;
	}
	
	
	public Properties getConsolePasswordCryptConfig() throws UtilsException {
		return this.reader.readPropertiesConvertEnvProperties("console.password.");
	}
	
	public boolean isConsolePasswordCryptBackwardCompatibility() throws UtilsException {
		return "true".equalsIgnoreCase(this.readProperty(true, "console.password.crypt.backwardCompatibility"));
	}

	
	/* ----- Gestione Password ------- */
	
	// Utenze
	
	public String getUtenzePassword() throws UtilsException{
		return this.readProperty(true, "utenze.password");
	}
	private static CryptConfig utenzeCryptConfig = null;
	private static synchronized void initUtenzeCryptConfig(String p) throws UtilsException {
		if(utenzeCryptConfig==null) {
			utenzeCryptConfig = new CryptConfig(p);
		}
	}
	public CryptConfig getUtenzeCryptConfig() throws UtilsException {
		if(utenzeCryptConfig==null) {
			initUtenzeCryptConfig(getUtenzePassword());
		}
		return utenzeCryptConfig;
	}
	
	// Applicativi
	
	public String getApplicativiPassword() throws UtilsException{
		return this.readProperty(true, "applicativi.password");
	}
	private static CryptConfig applicativiCryptConfig = null;
	private static synchronized void initApplicativiCryptConfig(String p) throws UtilsException {
		if(applicativiCryptConfig==null) {
			applicativiCryptConfig = new CryptConfig(p);
		}
	}
	public CryptConfig getApplicativiCryptConfig() throws UtilsException {
		if(applicativiCryptConfig==null) {
			initApplicativiCryptConfig(getApplicativiPassword());
		}
		return applicativiCryptConfig;
	}
	
	public int getApplicativiApiKeyPasswordGeneratedLength() throws UtilsException{
		return Integer.valueOf(this.readProperty(true, "applicativi.api_key.passwordGenerated.length"));
	}
	
	public boolean isApplicativiBasicPasswordEnableConstraints() throws UtilsException{
		return "true".equalsIgnoreCase(this.readProperty(true, "applicativi.basic.password.enableConstraints"));
	}
	private static PasswordVerifier applicativiPasswordVerifier = null;
	private static synchronized void initApplicativiPasswordVerifier(String p) throws UtilsException {
		if(applicativiPasswordVerifier==null) {
			applicativiPasswordVerifier = new PasswordVerifier(p);
		}
	}
	public PasswordVerifier getApplicativiPasswordVerifier() throws UtilsException {
		if(applicativiPasswordVerifier==null) {
			initApplicativiPasswordVerifier(getApplicativiPassword());
		}
		return applicativiPasswordVerifier;
	}
	
	// Soggetti
	
	public String getSoggettiPassword() throws UtilsException{
		return this.readProperty(true, "soggetti.password");
	}
	private static CryptConfig soggettiCryptConfig = null;
	private static synchronized void initSoggettiCryptConfig(String p) throws UtilsException {
		if(soggettiCryptConfig==null) {
			soggettiCryptConfig = new CryptConfig(p);
		}
	}
	public CryptConfig getSoggettiCryptConfig() throws UtilsException {
		if(soggettiCryptConfig==null) {
			initSoggettiCryptConfig(getSoggettiPassword());
		}
		return soggettiCryptConfig;
	}
	
	public int getSoggettiApiKeyPasswordGeneratedLength() throws UtilsException{
		return Integer.valueOf(this.readProperty(true, "soggetti.api_key.passwordGenerated.length"));
	}
	
	public boolean isSoggettiBasicPasswordEnableConstraints() throws UtilsException{
		return "true".equalsIgnoreCase(this.readProperty(true, "soggetti.basic.password.enableConstraints"));
	}
	private static PasswordVerifier soggettiPasswordVerifier = null;
	private static synchronized void initSoggettiPasswordVerifier(String p) throws UtilsException {
		if(soggettiPasswordVerifier==null) {
			soggettiPasswordVerifier = new PasswordVerifier(p);
		}
	}
	public PasswordVerifier getSoggettiPasswordVerifier() throws UtilsException {
		if(soggettiPasswordVerifier==null) {
			initSoggettiPasswordVerifier(getSoggettiPassword());
		}
		return soggettiPasswordVerifier;
	}
	
	public Properties getConsoleSecurityConfiguration() throws UtilsException{
		return this.reader.readPropertiesConvertEnvProperties("console.security.");
	}
	public Properties getConsoleInputSanitizerConfiguration() throws UtilsException{
		return this.reader.readPropertiesConvertEnvProperties("console.inputSanitizer.");
	}
	
}
