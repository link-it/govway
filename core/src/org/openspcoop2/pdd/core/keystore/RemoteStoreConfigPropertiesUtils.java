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
package org.openspcoop2.pdd.core.keystore;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.certificate.remote.RemoteKeyIdMode;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * RemoteStoreConfigPropertiesUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoreConfigPropertiesUtils {
	
	private RemoteStoreConfigPropertiesUtils() {}

	public static final String PROPERTY_STORE_NAME = "name";
	public static final String PROPERTY_STORE_LABEL = "label";
	
	public static final String PROPERTY_STORE_TOKEN_POLICY = "tokenPolicy";
	
	public static final String PROPERTY_STORE_URL = "baseUrl";
	
	public static final String PROPERTY_STORE_ID_MODE = "keyId.mode";
	public static final String PROPERTY_STORE_ID_PARAMETER_NAME = "keyId.parameter";
	
	public static final String PROPERTY_STORE_KEY_ALGORITHM = "keyAlgorithm";
	
	public static final String PROPERTY_READ_TIMEOUT = "readTimeout";
	public static final String PROPERTY_CONNECT_TIMEOUT = "connectTimeout";
	
	public static final String PROPERTY_HTTP_BASIC_USERNAME = "http.username";
	public static final String PROPERTY_HTTP_BASIC_PASSWORD = "http.password";
	
	public static final String PROPERTY_HTTPS_HOSTNAME_VERIFIER = "https.hostnameVerifier";
	public static final String PROPERTY_HTTPS_TRUST_ALL_CERTS = "https.trustAllCerts";
	public static final String PROPERTY_HTTPS_TRUST_STORE = "https.trustStore";
	public static final String PROPERTY_HTTPS_TRUST_STORE_PASSWORD = "https.trustStore.password";
	public static final String PROPERTY_HTTPS_TRUST_STORE_TYPE = "https.trustStore.type";
	public static final String PROPERTY_HTTPS_TRUST_STORE_CRL = "https.trustStore.crl";

	public static final String PROPERTY_HTTPS_KEY_STORE = "https.keyStore";
	public static final String PROPERTY_HTTPS_KEY_STORE_PASSWORD = "https.keyStore.password";
	public static final String PROPERTY_HTTPS_KEY_STORE_TYPE = "https.keyStore.type";
	public static final String PROPERTY_HTTPS_KEY_ALIAS = "https.key.alias";
	public static final String PROPERTY_HTTPS_KEY_PASSWORD = "https.key.password";

	public static final String PROPERTY_FORWARD_PROXY_URL = "forwardProxy.url";
	public static final String PROPERTY_FORWARD_PROXY_HEADER = "forwardProxy.header";
	public static final String PROPERTY_FORWARD_PROXY_QUERY_PARAMETER = "forwardProxy.queryParameter";
	public static final String PROPERTY_FORWARD_PROXY_BASE64 = "forwardProxy.base64";
	
	public static RemoteStoreConfig read(Properties p, RequestInfo requestInfo) throws KeystoreException {
		
		String storeName = getProperty(p, PROPERTY_STORE_NAME, true);
		
		RemoteStoreConfig config = new RemoteStoreConfig(storeName);
		
		String storeLabel = getProperty(p, PROPERTY_STORE_LABEL, false);
		if(storeLabel!=null && StringUtils.isNotEmpty(storeLabel)) {
			config.setStoreLabel(storeLabel);
		}
		
		String tokenPolicy = getProperty(p, PROPERTY_STORE_TOKEN_POLICY, false);
		if(tokenPolicy!=null && StringUtils.isNotEmpty(tokenPolicy)) {
			config.setTokenPolicy(tokenPolicy);
		}
		
		String storeUrl = getProperty(p, PROPERTY_STORE_URL, true);
		config.setBaseUrl(storeUrl);
		
		String idMode = getProperty(p, PROPERTY_STORE_ID_MODE, true);
		RemoteKeyIdMode keyIdMode = RemoteKeyIdMode.valueOf(idMode);
		config.setIdMode(keyIdMode);
		
		switch (keyIdMode) {
		case HEADER:
		case URL_PARAMETER:
			config.setParameterName(getProperty(p, PROPERTY_STORE_ID_PARAMETER_NAME, true));
			break;
		default:
			break;
		}
		
		String keyAlgo = getProperty(p, PROPERTY_STORE_KEY_ALGORITHM, false);
		if(keyAlgo!=null && StringUtils.isNotEmpty(keyAlgo)) {
			config.setKeyAlgorithm(keyAlgo);
		}

		config.setReadTimeout(getIntProperty(p, PROPERTY_READ_TIMEOUT, false, 15000));
		config.setConnectTimeout(getIntProperty(p, PROPERTY_CONNECT_TIMEOUT, false, HttpUtilities.HTTP_CONNECTION_TIMEOUT));
		
		String basicUsername = getProperty(p, PROPERTY_HTTP_BASIC_USERNAME, false);
		if(basicUsername!=null && StringUtils.isNotEmpty(basicUsername)) {
			config.setBasicUsername(basicUsername);
			
			String basicPassword = getProperty(p, PROPERTY_HTTP_BASIC_PASSWORD, false);
			if(basicPassword!=null && StringUtils.isNotEmpty(basicPassword)) {
				config.setBasicPassword(basicPassword);
			}
		}
		
		config.setHostnameVerifier(getBooleanProperty(p, PROPERTY_HTTPS_HOSTNAME_VERIFIER, false, true));	
		
		readTrustStoreConfig(config, p, requestInfo);
		
		readKeyStoreConfig(config, p, requestInfo);
		
		readForwardProxyConfig(config, p);
		
		return config;
	}
	
	private static void readTrustStoreConfig(RemoteStoreConfig config, Properties p, RequestInfo requestInfo) throws KeystoreException {
		config.setTrustAllCerts(getBooleanProperty(p, PROPERTY_HTTPS_TRUST_ALL_CERTS, false, false));	
		String trustStorePath = getProperty(p, PROPERTY_HTTPS_TRUST_STORE, false);	
		if(trustStorePath!=null && StringUtils.isNotEmpty(trustStorePath)) {
			String trustStorePassword = getProperty(p, PROPERTY_HTTPS_TRUST_STORE_PASSWORD, true);	
			String trustStoreType = getProperty(p, PROPERTY_HTTPS_TRUST_STORE_TYPE, false);
			if(trustStoreType==null || StringUtils.isEmpty(trustStoreType)) {
				trustStoreType = KeystoreType.JKS.getNome();
			}
			try {
				config.setTrustStore(GestoreKeystoreCaching.getMerlinTruststore(requestInfo, trustStorePath, trustStoreType, trustStorePassword).getTrustStore().getKeystore());
			}catch(Exception e) {
				throw new KeystoreException(e.getMessage(),e);
			}
			
			String crl = getProperty(p, PROPERTY_HTTPS_TRUST_STORE_CRL, false);
			if(crl!=null && StringUtils.isNotEmpty(crl)) {
				try {
					config.setCrlStore(GestoreKeystoreCaching.getCRLCertstore(requestInfo, crl).getCertStore());
				}catch(Exception e) {
					throw new KeystoreException(e.getMessage(),e);
				}
			}
		}
	}
	
	private static void readKeyStoreConfig(RemoteStoreConfig config, Properties p, RequestInfo requestInfo) throws KeystoreException {
		String keyStorePath = getProperty(p, PROPERTY_HTTPS_KEY_STORE, false);	
		if(keyStorePath!=null && StringUtils.isNotEmpty(keyStorePath)) {
			String keyStorePassword = getProperty(p, PROPERTY_HTTPS_KEY_STORE_PASSWORD, true);	
			String keyStoreType = getProperty(p, PROPERTY_HTTPS_KEY_STORE_TYPE, false);
			if(keyStoreType==null || StringUtils.isEmpty(keyStoreType)) {
				keyStoreType = KeystoreType.JKS.getNome();
			}
			
			String keyPassword = getProperty(p, PROPERTY_HTTPS_KEY_PASSWORD, true);	
			String keyAlias = getProperty(p, PROPERTY_HTTPS_KEY_ALIAS, false);	
			
			try {
				config.setKeyStore(GestoreKeystoreCaching.getMerlinKeystore(requestInfo, keyStorePath,keyStoreType,keyStorePassword,keyPassword).getKeyStore().getKeystore());
			}catch(Exception e) {
				throw new KeystoreException(e.getMessage(),e);
			}
			config.setKeyAlias(keyAlias);
			config.setKeyPassword(keyPassword);	
		}
	}
	
	private static void readForwardProxyConfig(RemoteStoreConfig config, Properties p) throws KeystoreException {
		String forwardProxyUrl = getProperty(p, PROPERTY_FORWARD_PROXY_URL, false);	
		if(forwardProxyUrl!=null && StringUtils.isNotEmpty(forwardProxyUrl)) {
			config.setForwardProxyUrl(forwardProxyUrl);
			config.setForwardProxyHeader(getProperty(p, PROPERTY_FORWARD_PROXY_HEADER, false));	
			config.setForwardProxyQueryParameter(getProperty(p, PROPERTY_FORWARD_PROXY_QUERY_PARAMETER, false));
			if(config.getForwardProxyHeader()==null && config.getForwardProxyQueryParameter()==null) {
				throw new KeystoreException("ForwardProxy property '"+PROPERTY_FORWARD_PROXY_URL+"' require '"+
						PROPERTY_FORWARD_PROXY_HEADER+"' o '"+
						PROPERTY_FORWARD_PROXY_QUERY_PARAMETER+"'");
			}
			config.setForwardProxyBase64(getBooleanProperty(p, PROPERTY_FORWARD_PROXY_BASE64, false, true));
		}
	}
	
	private static String getProperty(Properties p, String name, boolean required) throws KeystoreException {
		String tmp = p.getProperty(name);
		if(tmp!=null) {
			return tmp.trim();
		}
		else {
			if(required) {
				throw new KeystoreException("Property '"+name+"' notFound");
			}
			return null;
		}
	}
	private static boolean getBooleanProperty(Properties p, String name, boolean required, boolean defaultValue) throws KeystoreException {
		String tmp = getProperty(p, name, required);
		if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
			try {
				return Boolean.valueOf(tmp);
			}catch(Exception t) {
				throw new KeystoreException("Boolean property '"+name+"' invalid (found value:["+tmp+"]): "+t.getMessage(),t);
			}
		}
		return defaultValue;
	}
	private static int getIntProperty(Properties p, String name, boolean required, int defaultValue) throws KeystoreException {
		String tmp = getProperty(p, name, required);
		if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
			try {
				return Integer.valueOf(tmp);
			}catch(Exception t) {
				throw new KeystoreException("Boolean property '"+name+"' invalid (found value:["+tmp+"]): "+t.getMessage(),t);
			}
		}
		return defaultValue;
	}
}
