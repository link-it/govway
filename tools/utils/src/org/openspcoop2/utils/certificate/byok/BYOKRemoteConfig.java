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

package org.openspcoop2.utils.certificate.byok;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * BYOKRemoteConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKRemoteConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3572589461109860459L;
			
	private String httpEndpoint;
	private String httpMethod;
	
	private Map<String,String> httpHeaders;
	
	private String httpPayloadPath;
	private String httpPayloadInLine;
	
	private String httpUsername;
	private String httpPassword;
	
	private Integer httpConnectionTimeout;
	private Integer httpReadTimeout;

	private boolean https = false;
	
	private boolean httpsHostnameVerifier = false;
	
	private boolean httpsServerAuth = false;
	private String httpsServerAuthTrustStorePath;
	private String httpsServerAuthTrustStoreType;
	private String httpsServerAuthTrustStorePassword;
	private String httpsServerAuthTrustStoreCrls;
	private String httpsServerAuthTrustStoreOcspPolicy;
	
	private boolean httpsClientAuth = false;
	private String httpsClientAuthKeyStorePath;
	private String httpsClientAuthKeyStoreType;
	private String httpsClientAuthKeyStorePassword;
	private String httpsClientAuthKeyAlias;
	private String httpsClientAuthKeyPassword;
	
	
	protected BYOKRemoteConfig(String id, Properties p, Logger log) throws UtilsException {
				
		if(p==null || p.isEmpty()) {
			log.error("Properties is null");
			throw new UtilsException("Properties '"+BYOKCostanti.PROPERTY_PREFIX+id+".*' undefined");
		}
		
		this.httpEndpoint = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTP_ENDPOINT, true);
		this.httpMethod = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTP_METHOD, true);
		
		initHttpHeader(p);
			
		this.httpPayloadPath = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTP_PAYLOAD_PATH, false);	
		this.httpPayloadInLine = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTP_PAYLOAD_INLINE, false);	
			
		this.httpUsername = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTP_USERNAME, false);
		this.httpPassword = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTP_PASSWORD, false);	
		
		this.httpConnectionTimeout = BYOKConfig.getIntegerProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTP_CONNECTION_TIMEOUT, false);	
		this.httpReadTimeout = BYOKConfig.getIntegerProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTP_READ_TIMEOUT, false);	

		this.https = BYOKConfig.getBooleanProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTPS, false, false);	
		
		this.httpsHostnameVerifier = BYOKConfig.getBooleanProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTPS_VERIFICA_HOSTNAME, false, this.https);
		
		this.httpsServerAuth = BYOKConfig.getBooleanProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER, false, this.https);
		this.httpsServerAuthTrustStorePath = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_TRUSTSTORE_PATH, false);
		this.httpsServerAuthTrustStoreType = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_TRUSTSTORE_TYPE, this.httpsServerAuth);
		this.httpsServerAuthTrustStorePassword = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_TRUSTSTORE_PASSWORD, false);
		this.httpsServerAuthTrustStoreCrls = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_CRLS, false);
		this.httpsServerAuthTrustStoreOcspPolicy = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_OCSP_POLICY, false);
		
		this.httpsClientAuth = BYOKConfig.getBooleanProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT, false, false);
		this.httpsClientAuthKeyStorePath = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEYSTORE_PATH, false);
		this.httpsClientAuthKeyStoreType = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEYSTORE_TYPE, this.httpsClientAuth);
		this.httpsClientAuthKeyStorePassword = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEYSTORE_PASSWORD, false);
		this.httpsClientAuthKeyAlias = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEY_ALIAS, false);
		this.httpsClientAuthKeyPassword = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEY_PASSWORD, false);
		
	}

	private void initHttpHeader(Properties p) {
		this.httpHeaders = new HashMap<>();
		Enumeration<?> enKeys = p.keys();
		while (enKeys.hasMoreElements()) {
			Object object = enKeys.nextElement();
			if(object instanceof String) {
				String key = (String) object;
				if(key.startsWith(BYOKCostanti.PROPERTY_SUFFIX_HTTP_HEADER) && key.length()>BYOKCostanti.PROPERTY_SUFFIX_HTTP_HEADER.length()) {
					String name = key.substring(BYOKCostanti.PROPERTY_SUFFIX_HTTP_HEADER.length());
					String value = p.getProperty(key); 
					this.httpHeaders.put(name, value);
				}
			}
		}
		
	}
	
	
	public String getHttpEndpoint() {
		return this.httpEndpoint;
	}
	public String getHttpMethod() {
		return this.httpMethod;
	}
	
	public Map<String, String> getHttpHeaders() {
		return this.httpHeaders;
	}

	public String getHttpPayloadInLine() {
		return this.httpPayloadInLine;
	}
	public String getHttpPayloadPath() {
		return this.httpPayloadPath;
	}

	public String getHttpUsername() {
		return this.httpUsername;
	}
	public String getHttpPassword() {
		return this.httpPassword;
	}

	public Integer getHttpConnectionTimeout() {
		return this.httpConnectionTimeout;
	}
	public Integer getHttpReadTimeout() {
		return this.httpReadTimeout;
	}

	public boolean isHttps() {
		return this.https;
	}
	public boolean isHttpsHostnameVerifier() {
		return this.httpsHostnameVerifier;
	}

	public boolean isHttpsServerAuth() {
		return this.httpsServerAuth;
	}
	public String getHttpsServerAuthTrustStorePath() {
		return this.httpsServerAuthTrustStorePath;
	}
	public String getHttpsServerAuthTrustStoreType() {
		return this.httpsServerAuthTrustStoreType;
	}
	public String getHttpsServerAuthTrustStorePassword() {
		return this.httpsServerAuthTrustStorePassword;
	}
	public String getHttpsServerAuthTrustStoreCrls() {
		return this.httpsServerAuthTrustStoreCrls;
	}
	public String getHttpsServerAuthTrustStoreOcspPolicy() {
		return this.httpsServerAuthTrustStoreOcspPolicy;
	}

	public boolean isHttpsClientAuth() {
		return this.httpsClientAuth;
	}
	public String getHttpsClientAuthKeyStorePath() {
		return this.httpsClientAuthKeyStorePath;
	}
	public String getHttpsClientAuthKeyStoreType() {
		return this.httpsClientAuthKeyStoreType;
	}
	public String getHttpsClientAuthKeyStorePassword() {
		return this.httpsClientAuthKeyStorePassword;
	}
	public String getHttpsClientAuthKeyAlias() {
		return this.httpsClientAuthKeyAlias;
	}
	public String getHttpsClientAuthKeyPassword() {
		return this.httpsClientAuthKeyPassword;
	}
}
