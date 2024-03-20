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

import java.net.Proxy;
import java.security.KeyStore;
import java.security.cert.CertStore;

/**
 * Classe che contiene la risposta http
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpRequest extends AbstractHttp {

	private String url;
	
	private int readTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
	private int connectTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
	
	private String username;
	private String password;
	
	private HttpRequestMethod method;
	
	private Boolean followRedirects;
		
	private boolean trustAllCerts = false;
	private KeyStore trustStore;
	private boolean trustStoreHsm;
	private String trustStorePath;
	private String trustStorePassword;
	private String trustStoreType;
	
	private CertStore crlStore;
	private String crlPath;
	
	private IOCSPValidator ocspValidator;
	
	private KeyStore keyStore;
	private String keyStorePath;
	private String keyStorePassword;
	private String keyStoreType;
	private String keyAlias;
	private String keyPassword;
	
	private boolean secureRandom = false;
	private String secureRandomAlgorithm = null;
	
	private boolean hostnameVerifier = false; // nelle versioni precedenti era configurato disabilitato direttamente in HttpUtilities
	
	// throttling send bytes every ms
	private Integer throttlingSendMs;
	private Integer throttlingSendByte;
	
	private String forwardProxyEndpoint;
	private HttpForwardProxyConfig forwardProxyConfig;
	
	private Proxy.Type proxyType = null;
	private String proxyHostname = null;
	private int proxyPort;
	private String proxyUsername;
	private String proxyPassword;
	
	private boolean disconnect = true;
	
	public Boolean getFollowRedirects() {
		return this.followRedirects;
	}

	public void setFollowRedirects(Boolean followRedirects) {
		this.followRedirects = followRedirects;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getReadTimeout() {
		return this.readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getConnectTimeout() {
		return this.connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public HttpRequestMethod getMethod() {
		return this.method;
	}

	public void setMethod(HttpRequestMethod method) {
		this.method = method;
	}
	
	public boolean isTrustAllCerts() {
		return this.trustAllCerts;
	}

	public void setTrustAllCerts(boolean trustAllCerts) {
		this.trustAllCerts = trustAllCerts;
	}
	
	public String getTrustStorePath() {
		return this.trustStorePath;
	}

	public void setTrustStorePath(String trustStorePath) {
		this.trustStorePath = trustStorePath;
	}

	public String getTrustStorePassword() {
		return this.trustStorePassword;
	}

	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	public String getTrustStoreType() {
		return this.trustStoreType;
	}

	public void setTrustStoreType(String trustStoreType) {
		this.trustStoreType = trustStoreType;
	}
	
	public KeyStore getTrustStore() {
		return this.trustStore;
	}
	
	public boolean isTrustStoreHsm() {
		return this.trustStoreHsm;
	}

	public void setTrustStore(KeyStore trustStore) {
		setTrustStore(trustStore, false);
	}
	public void setTrustStore(KeyStore trustStore, boolean hsm) {
		this.trustStore = trustStore;
		this.trustStoreHsm = hsm;
	}
	
	public CertStore getCrlStore() {
		return this.crlStore;
	}

	public void setCrlStore(CertStore crlStore) {
		this.crlStore = crlStore;
	}

	public String getCrlPath() {
		return this.crlPath;
	}

	public void setCrlPath(String crlPath) {
		this.crlPath = crlPath;
	}

	public IOCSPValidator getOcspValidator() {
		return this.ocspValidator;
	}

	public void setOcspValidator(IOCSPValidator ocspValidator) {
		this.ocspValidator = ocspValidator;
	}
	
	public boolean isSecureRandom() {
		return this.secureRandom;
	}

	public void setSecureRandom(boolean secureRandom) {
		this.secureRandom = secureRandom;
	}

	public String getSecureRandomAlgorithm() {
		return this.secureRandomAlgorithm;
	}

	public void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
		this.secureRandomAlgorithm = secureRandomAlgorithm;
	}
	
	public boolean isHostnameVerifier() {
		return this.hostnameVerifier;
	}

	public void setHostnameVerifier(boolean hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
	}
	
	public Integer getThrottlingSendMs() {
		return this.throttlingSendMs;
	}

	public void setThrottlingSendMs(Integer throttlingSendMs) {
		this.throttlingSendMs = throttlingSendMs;
	}

	public Integer getThrottlingSendByte() {
		return this.throttlingSendByte;
	}

	public void setThrottlingSendByte(Integer throttlingSendByte) {
		this.throttlingSendByte = throttlingSendByte;
	}
	
	public boolean isDisconnect() {
		return this.disconnect;
	}

	public void setDisconnect(boolean disconnect) {
		this.disconnect = disconnect;
	}
	
	public KeyStore getKeyStore() {
		return this.keyStore;
	}

	public void setKeyStore(KeyStore keyStore) {
		this.keyStore = keyStore;
	}

	public String getKeyStorePath() {
		return this.keyStorePath;
	}

	public void setKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
	}

	public String getKeyStorePassword() {
		return this.keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public String getKeyStoreType() {
		return this.keyStoreType;
	}

	public void setKeyStoreType(String keyStoreType) {
		this.keyStoreType = keyStoreType;
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
	
	public String getForwardProxyEndpoint() {
		return this.forwardProxyEndpoint;
	}

	public void setForwardProxyEndpoint(String forwardProxyEndpoint) {
		this.forwardProxyEndpoint = forwardProxyEndpoint;
	}
	
	public HttpForwardProxyConfig getForwardProxyConfig() {
		return this.forwardProxyConfig;
	}

	public void setForwardProxyConfig(HttpForwardProxyConfig forwardProxyConfig) {
		this.forwardProxyConfig = forwardProxyConfig;
	}
	
	public Proxy.Type getProxyType() {
		return this.proxyType;
	}

	public void setProxyType(Proxy.Type proxyType) {
		this.proxyType = proxyType;
	}

	public String getProxyHostname() {
		return this.proxyHostname;
	}

	public void setProxyHostname(String proxyHostname) {
		this.proxyHostname = proxyHostname;
	}

	public int getProxyPort() {
		return this.proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUsername() {
		return this.proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return this.proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}
}
