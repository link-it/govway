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
package org.openspcoop2.utils.transport.http;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.PropertiesReader;

/**
 * HttpRequestConfig
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpRequestConfig {
	
	private final PropertiesReader props;
	private final Function<String, String> propSupplier;
	private String prefix;
	
	private static final String PROP_URL = "baseUrl";
	private static final String PROP_LIBRARY = "httpLibrary";
	private static final String PROP_READ_TIMEOUT = "readTimeout";
	private static final String PROP_CONNECTION_TIMEOUT = "connectTimeout";
	private static final String PROP_HTTP_USERNAME = "http.username";
	private static final String PROP_HTTP_PASSWORD = "http.password";
	private static final String PROP_HTTP_QUERY = "http.queryParameters";
	private static final String PROP_HTTP_HEADER = "http.headers";
	private static final String PROP_HTTPS_HOSTNAME_VERIFIER = "https.hostnameVerifier";
	private static final String PROP_HTTPS_TRUST_ALL_CERTS = "https.trustAllCerts";
	private static final String PROP_HTTPS_TRUSTSTORE = "https.trustStore";
	private static final String PROP_HTTPS_TRUSTSTORE_PASSWORD = "https.trustStore.password";
	private static final String PROP_HTTPS_TRUSTSTORE_TYPE = "https.trustStore.type";
	private static final String PROP_HTTPS_CRL = "https.trustStore.crl";
	private static final String PROP_HTTPS_KEYSTORE = "https.keyStore";
	private static final String PROP_HTTPS_KEYSTORE_PASSWORD = "https.keyStore.password";
	private static final String PROP_HTTPS_KEYSTORE_TYPE = "https.keyStore.type";
	private static final String PROP_HTTPS_KEY_ALIAS = "https.key.alias";
	private static final String PROP_HTTPS_KEY_PASSWORD = "https.key.password";
	
	public HttpRequestConfig(String prefix, PropertiesReader props) {
		this.prefix = prefix;
		this.props = props;
		this.propSupplier = null;
	}
	
	public HttpRequestConfig(String prefix, UnaryOperator<String> props) {
		this.prefix = prefix;
		this.props = null;
		this.propSupplier = props;
	}
	
	
	private String getProperty(String code, String key) {
		return getProperty(code, key, true);
	}
	
	private String getProperty(String code, String baseKey, boolean searchDefault) {
		String value = null;
		String key;
		
		if (code == null)
			key = this.prefix + "." + baseKey;
		else
			key = this.prefix + "." + code + "." + baseKey;

		if (this.props != null){
			try {
				value = this.props.getValue(key);
			} catch (UtilsException e) {
				value = null;
			}
		}
		
		if (this.propSupplier != null) {
			value = this.propSupplier.apply(key);
		}
		
		if (!searchDefault || value != null)
			return value;
		return this.getProperty(null, baseKey, false);
	}

	
	private void fillHttpProperties(HttpRequest req, String code) {
		req.setUsername(getProperty(code, PROP_HTTP_USERNAME));
		req.setPassword(getProperty(code, PROP_HTTP_PASSWORD));
	}
	
	private void fillHttpsProperties(HttpRequest req, String code) {
		String hostnameVerifier = getProperty(code, PROP_HTTPS_HOSTNAME_VERIFIER);
		if (hostnameVerifier != null)
			req.setHostnameVerifier(Boolean.valueOf(hostnameVerifier));
		String trustAllCerts =  getProperty(code, PROP_HTTPS_TRUST_ALL_CERTS);
		if (trustAllCerts != null)
			req.setTrustAllCerts(Boolean.valueOf(trustAllCerts));
		req.setTrustStorePath(getProperty(code, PROP_HTTPS_TRUSTSTORE));
		req.setTrustStorePassword(getProperty(code, PROP_HTTPS_TRUSTSTORE_PASSWORD));
		req.setTrustStoreType(getProperty(code, PROP_HTTPS_TRUSTSTORE_TYPE));
		req.setCrlPath(getProperty(code, PROP_HTTPS_CRL));
		
		req.setKeyStorePath(getProperty(code, PROP_HTTPS_KEYSTORE));
		req.setKeyStorePassword(getProperty(code, PROP_HTTPS_KEYSTORE_PASSWORD));
		req.setKeyStoreType(getProperty(code, PROP_HTTPS_KEYSTORE_TYPE));
		req.setKeyAlias(getProperty(code, PROP_HTTPS_KEY_ALIAS));
		req.setKeyPassword(getProperty(code, PROP_HTTPS_KEY_PASSWORD));
	}
	
	public HttpRequest getBaseRequest() {
		return this.getBaseRequest(null);
	}

	public HttpRequest getBaseRequest(String code) {
		HttpRequest req = new HttpRequest();
		
		req.setUrl(getProperty(code, PROP_URL));
		
		String connectTimeout = getProperty(code, PROP_CONNECTION_TIMEOUT);
		if (!"false".equals(connectTimeout))
			req.setConnectTimeout(Integer.valueOf(connectTimeout));
		
		String readTimeout = getProperty(code, PROP_READ_TIMEOUT);
		if (!"false".equals(readTimeout))
			req.setReadTimeout(Integer.valueOf(readTimeout));
		
		fillHttpProperties(req, code);
		fillHttpsProperties(req, code);
		
		String queryParameters = getProperty(code, PROP_HTTP_QUERY);
		if (queryParameters != null) {
			Arrays.stream(queryParameters.split(";"))
				.map(str -> str.split(":"))
				.forEach(arr -> req.addParam(arr[0], arr[1]));
		}
		
		String headers = getProperty(code, PROP_HTTP_HEADER);
		if (headers != null) {
			Arrays.stream(headers.split(";"))
			.map(str -> str.split(":"))
			.forEach(arr -> req.addHeader(arr[0], arr[1]));
		}
		
		String libName = getProperty(code, PROP_LIBRARY);
		if (libName != null) {
			req.setHttpLibrary(HttpLibrary.fromName(libName));
		}
		
		return req;
	}
	
}