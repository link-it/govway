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


package org.openspcoop2.utils.transport.http;

import java.io.Serializable;
import java.security.KeyStore;
import java.security.cert.CertStore;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.LoggerBuffer;
import org.slf4j.Logger;


/**
 * SSLConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SSLConfig implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// AUTENTICAZIONE SERVER:
	// TrustAllCerts
	private boolean trustAllCerts = false;
	// TrustStore
	private transient KeyStore trustStore;
	private transient boolean trustStoreHsm;
	// Path del trustStore che contiene il certificato del server.
	private String trustStoreLocation;
	// Password del trustStore che contiene il certificato del server.
	private String trustStorePassword;
	// the standard name of the requested trust management algorithm
	private String trustManagementAlgorithm;
	// tipo del truststore
	private String trustStoreType;
	// CRLs
	private String trustStoreCRLsLocation;
	// CertStore
	private transient CertStore trustStoreCRLs;
	// OCSP Policy
	private String trustStoreOCSPPolicy;
	
	
	// AUTENTICAZIONE CLIENT:
	// KeyStore
	private transient KeyStore keyStore;
	private transient boolean keyStoreHsm;
	// Path del keyStore che contiene il certificato del client e la chiave privata del client.
	private String keyStoreLocation;
	// Password del keyStore che contiene il certificato del client
	private String keyStorePassword;
	// Alias della chiave privata
	private String keyAlias;
	// Password della chiave privata
	private String keyPassword;
	// the standard name of the requested key management algorithm
	private String keyManagementAlgorithm;
	// tipo del keystore
	private String keyStoreType;
	// BYOK Policy
	private String keyStoreBYOKPolicy;

	// HostName verifier
	private boolean hostnameVerifier = true;
	// Eventuale classe da utilizzare per effettuare hostnameVerifier al posto di quella di default
	private String classNameHostnameVerifier;
	
	// TipologiaSSL
	private String sslType= null;
	
	// Use Secure Random
	private boolean secureRandomSet = false;
	private boolean secureRandom = false;
	private String secureRandomAlgorithm = null;
	
	// Utilities
	private StringBuilder sbError;
	private StringBuilder sbDebug;
	private transient Logger logger;
	
	// Dynamic Map
	private Map<String,Object> dynamicMap;
	
	@Override
	public String toString() {
		return this.toString(false);
	}
	public String toString(boolean includePassword) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("sslType=").append(this.sslType);
		sb.append(" ");
		
		sb.append("secureRandom=").append(this.secureRandom);
		sb.append(" ");
		if(this.secureRandomAlgorithm!=null) {
			sb.append("secureRandomAlgorithm=").append(this.secureRandomAlgorithm);
			sb.append(" ");
		}
		
		sb.append("hostnameVerifier=").append(this.hostnameVerifier);
		sb.append(" ");
		if(this.classNameHostnameVerifier!=null) {
			sb.append("classNameHostnameVerifier=").append(this.classNameHostnameVerifier);
			sb.append(" ");
		}
		
		sb.append("trustAllCerts=").append(this.trustAllCerts);
		sb.append(" ");
		if(!this.trustAllCerts) {
			sb.append("trustStoreLocation=").append(this.trustStoreLocation);
			sb.append(" ");
			sb.append("trustStoreType=").append(this.trustStoreType);
			sb.append(" ");
			sb.append("trustStorePassword=").append(includePassword? this.trustStorePassword : hidePassword(this.trustStorePassword) );
			sb.append(" ");
			sb.append("trustManagementAlgorithm=").append(this.trustManagementAlgorithm);
			sb.append(" ");
			sb.append("trustStoreCRLsLocation=").append(this.trustStoreCRLsLocation);
			sb.append(" ");
			sb.append("trustStoreOCSPPolicy=").append(this.trustStoreOCSPPolicy);
			sb.append(" ");
		}
		else if(this.trustStoreOCSPPolicy!=null && StringUtils.isNotEmpty(this.trustStoreOCSPPolicy)){
			sb.append("trustStoreCRLsLocation=").append(this.trustStoreCRLsLocation);
			sb.append(" ");
			sb.append("trustStoreOCSPPolicy=").append(this.trustStoreOCSPPolicy);
			sb.append(" ");
		}
		
		if(this.keyStoreLocation!=null) {
			sb.append("keyStoreLocation=").append(this.keyStoreLocation);
			sb.append(" ");
			sb.append("keyStoreType=").append(this.keyStoreType);
			sb.append(" ");
			sb.append("keyStorePassword=").append(includePassword? this.keyStorePassword : hidePassword(this.keyStorePassword) );
			sb.append(" ");
			sb.append("keyStoreBYOKPolicy=").append(this.keyStoreBYOKPolicy);
			sb.append(" ");
			sb.append("keyAlias=").append(this.keyAlias);
			sb.append(" ");
			sb.append("keyPassword=").append(includePassword? this.keyPassword : hidePassword(this.keyPassword) );
			sb.append(" ");
			sb.append("keyManagementAlgorithm=").append(this.keyManagementAlgorithm);
			sb.append(" ");
		}
		else {
			sb.append("keyStore=disabled");
			sb.append(" ");
		}
		
		return sb.toString();
	}
	private String hidePassword(String value) {
		return (value!=null) ? "***" : "unset" ;
	}

	public boolean isTrustAllCerts() {
		return this.trustAllCerts;
	}

	public void setTrustAllCerts(boolean trustAllCerts) {
		this.trustAllCerts = trustAllCerts;
	}
	
	public KeyStore getTrustStore() {
		return this.trustStore;
	}
	public boolean isTrustStoreHsm() {
		return this.trustStoreHsm;
	}

	public void setTrustStore(KeyStore trustStore) {
		this.setTrustStore(trustStore, false);
	}
	public void setTrustStore(KeyStore trustStore, boolean hsm) {
		this.trustStore = trustStore;
		this.trustStoreHsm = hsm;
	}

	public KeyStore getKeyStore() {
		return this.keyStore;
	}
	public boolean isKeyStoreHsm() {
		return this.keyStoreHsm;
	}

	public void setKeyStore(KeyStore keyStore) {
		this.setKeyStore(keyStore, false);
	}
	public void setKeyStore(KeyStore keyStore, boolean hsm) {
		this.keyStore = keyStore;
		this.keyStoreHsm = hsm;
	}
	
	public String getTrustStoreLocation() {
		return this.trustStoreLocation;
	}

	public void setTrustStoreLocation(String trustStoreLocation) {
		this.trustStoreLocation = trustStoreLocation;
	}

	public String getTrustStorePassword() {
		return this.trustStorePassword;
	}

	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	public String getTrustManagementAlgorithm() {
		return this.trustManagementAlgorithm;
	}

	public void setTrustManagementAlgorithm(String trustManagementAlgorithm) {
		this.trustManagementAlgorithm = trustManagementAlgorithm;
	}

	public String getTrustStoreType() {
		return this.trustStoreType;
	}

	public void setTrustStoreType(String trustStoreType) {
		this.trustStoreType = trustStoreType;
	}
	
	public String getTrustStoreCRLsLocation() {
		return this.trustStoreCRLsLocation;
	}

	public void setTrustStoreCRLsLocation(String trustStoreCRLsLocation) {
		this.trustStoreCRLsLocation = trustStoreCRLsLocation;
	}

	public CertStore getTrustStoreCRLs() {
		return this.trustStoreCRLs;
	}

	public void setTrustStoreCRLs(CertStore trustStoreCRLs) {
		this.trustStoreCRLs = trustStoreCRLs;
	}

	public String getTrustStoreOCSPPolicy() {
		return this.trustStoreOCSPPolicy;
	}
	public void setTrustStoreOCSPPolicy(String trustStoreOCSPPolicy) {
		this.trustStoreOCSPPolicy = trustStoreOCSPPolicy;
	}
	
	public String getKeyStoreLocation() {
		return this.keyStoreLocation;
	}

	public void setKeyStoreLocation(String keyStoreLocation) {
		this.keyStoreLocation = keyStoreLocation;
	}

	public String getKeyStorePassword() {
		return this.keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public String getKeyAlias() {
		return this.keyAlias;
	}

	public void setKeyAlias(String keyAlias) {
		this.keyAlias = keyAlias;
	}
	
	public String getKeyPassword() {
		return this.keyPassword;
	}

	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}

	public String getKeyManagementAlgorithm() {
		return this.keyManagementAlgorithm;
	}

	public void setKeyManagementAlgorithm(String keyManagementAlgorithm) {
		this.keyManagementAlgorithm = keyManagementAlgorithm;
	}

	public String getKeyStoreType() {
		return this.keyStoreType;
	}

	public void setKeyStoreType(String keyStoreType) {
		this.keyStoreType = keyStoreType;
	}

	public String getKeyStoreBYOKPolicy() {
		return this.keyStoreBYOKPolicy;
	}
	
	public void setKeyStoreBYOKPolicy(String keyStoreBYOKPolicy) {
		this.keyStoreBYOKPolicy = keyStoreBYOKPolicy;
	}
	
	public boolean isHostnameVerifier() {
		return this.hostnameVerifier;
	}

	public void setHostnameVerifier(boolean hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
	}

	public String getClassNameHostnameVerifier() {
		return this.classNameHostnameVerifier;
	}

	public void setClassNameHostnameVerifier(String classNameHostnameVerifier) {
		this.classNameHostnameVerifier = classNameHostnameVerifier;
	}

	public String getSslType() {
		return this.sslType;
	}

	public void setSslType(String sslType) {
		this.sslType = sslType;
	}
	
	public boolean isSecureRandom() {
		return this.secureRandom;
	}

	public void setSecureRandom(boolean secureRandom) {
		this.secureRandom = secureRandom;
		this.secureRandomSet = true;
	}

	public boolean isSecureRandomSet() {
		return this.secureRandomSet;
	}

	public String getSecureRandomAlgorithm() {
		return this.secureRandomAlgorithm;
	}

	public void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
		this.secureRandomAlgorithm = secureRandomAlgorithm;
	}
	
	public StringBuilder getSbError() {
		return this.sbError;
	}

	public void setSbError(StringBuilder sbError) {
		this.sbError = sbError;
	}

	public StringBuilder getSbDebug() {
		return this.sbDebug;
	}

	public void setSbDebug(StringBuilder sbDebug) {
		this.sbDebug = sbDebug;
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public LoggerBuffer getLoggerBuffer() {
		LoggerBuffer lb = new LoggerBuffer();
		lb.setSbDebug(this.sbDebug);
		lb.setSbError(this.sbError);
		lb.setLogErrorInDebug(true);
		return lb;
	}
	public LoggerBuffer getLog4jBuffer() {
		LoggerBuffer lb = new LoggerBuffer();
		lb.setLogError(this.logger);
		if(this.sbDebug!=null) {
			lb.setLogDebug(this.logger);
		}
		return lb;
	}
	
	public Map<String, Object> getDynamicMap() {
		return this.dynamicMap;
	}
	public void setDynamicMap(Map<String, Object> dynamicMap) {
		this.dynamicMap = dynamicMap;
	}
}