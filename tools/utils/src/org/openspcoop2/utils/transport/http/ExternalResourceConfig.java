/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.security.KeyStore;
import java.security.cert.CertStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ExternalResourceConfig
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExternalResourceConfig {

	// !! NOTA !!: se si aggiunge un field, bisogna gestirlo nel metodo in org.openspcoop2.utils.certificate.remote.RemoteStoreConfig.newInstanceMultitenant()
	
	protected int readTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
	protected int connectTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
	
	protected List<Integer> returnCode = new ArrayList<>();
	public ExternalResourceConfig (){
		this.returnCode.add(200);
	}

	protected String basicUsername;
	protected String basicPassword;
	
	protected boolean hostnameVerifier = true;
	
	protected boolean trustAllCerts = false;

	protected KeyStore trustStore;
	
	protected CertStore crlStore;
	
	protected KeyStore keyStore;
	protected String keyAlias;
	protected String keyPassword;
	
	protected String forwardProxyUrl;
	protected String forwardProxyHeader;
	protected String forwardProxyQueryParameter;
	protected boolean forwardProxyBase64;

	protected Map<String, String> headers;
	protected Map<String, String> queryParameters;
	
	// multi-tenant
	protected Map<String, String> multiTenantBasicUsername;
	protected Map<String, String> multiTenantBasicPassword;
	protected Map<String, Map<String, String>> multiTenantHeaders;
	protected Map<String, Map<String, String>> multiTenantQueryParameters;
	
	// !! NOTA !!: se si aggiunge un field, bisogna gestirlo nel metodo in org.openspcoop2.utils.certificate.remote.RemoteStoreConfig.newInstanceMultitenant()
	
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
	
	public List<Integer> getReturnCode() {
		return this.returnCode;
	}

	public void setReturnCode(List<Integer> returnCode) {
		this.returnCode = returnCode;
	}
	
	public String getBasicUsername() {
		return this.basicUsername;
	}

	public void setBasicUsername(String basicUsername) {
		this.basicUsername = basicUsername;
	}

	public String getBasicPassword() {
		return this.basicPassword;
	}

	public void setBasicPassword(String basicPassword) {
		this.basicPassword = basicPassword;
	}
	
	public boolean isHostnameVerifier() {
		return this.hostnameVerifier;
	}

	public void setHostnameVerifier(boolean hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
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

	public void setTrustStore(KeyStore trustStore) {
		this.trustStore = trustStore;
	}

	public CertStore getCrlStore() {
		return this.crlStore;
	}

	public void setCrlStore(CertStore crlStore) {
		this.crlStore = crlStore;
	}

	public KeyStore getKeyStore() {
		return this.keyStore;
	}

	public void setKeyStore(KeyStore keyStore) {
		this.keyStore = keyStore;
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
	
	public String getForwardProxyUrl() {
		return this.forwardProxyUrl;
	}

	public void setForwardProxyUrl(String forwardProxyUrl) {
		this.forwardProxyUrl = forwardProxyUrl;
	}

	public String getForwardProxyHeader() {
		return this.forwardProxyHeader;
	}

	public void setForwardProxyHeader(String forwardProxyHeader) {
		this.forwardProxyHeader = forwardProxyHeader;
	}

	public String getForwardProxyQueryParameter() {
		return this.forwardProxyQueryParameter;
	}

	public void setForwardProxyQueryParameter(String forwardProxyQueryParameter) {
		this.forwardProxyQueryParameter = forwardProxyQueryParameter;
	}

	public boolean isForwardProxyBase64() {
		return this.forwardProxyBase64;
	}

	public void setForwardProxyBase64(boolean forwardProxyBase64) {
		this.forwardProxyBase64 = forwardProxyBase64;
	}
	
	public Map<String, String> getHeaders() {
		return this.headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Map<String, String> getQueryParameters() {
		return this.queryParameters;
	}

	public void setQueryParameters(Map<String, String> queryParameters) {
		this.queryParameters = queryParameters;
	}
	
	public Map<String, String> getMultiTenantBasicUsername() {
		return this.multiTenantBasicUsername;
	}

	public void setMultiTenantBasicUsername(Map<String, String> multiTenantBasicUsername) {
		this.multiTenantBasicUsername = multiTenantBasicUsername;
	}

	public Map<String, String> getMultiTenantBasicPassword() {
		return this.multiTenantBasicPassword;
	}

	public void setMultiTenantBasicPassword(Map<String, String> multiTenantBasicPassword) {
		this.multiTenantBasicPassword = multiTenantBasicPassword;
	}

	public Map<String, Map<String, String>> getMultiTenantHeaders() {
		return this.multiTenantHeaders;
	}

	public void setMultiTenantHeaders(Map<String, Map<String, String>> multiTenantHeaders) {
		this.multiTenantHeaders = multiTenantHeaders;
	}

	public Map<String, Map<String, String>> getMultiTenantQueryParameters() {
		return this.multiTenantQueryParameters;
	}

	public void setMultiTenantQueryParameters(Map<String, Map<String, String>> multiTenantQueryParameters) {
		this.multiTenantQueryParameters = multiTenantQueryParameters;
	}
}
