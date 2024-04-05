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
package org.openspcoop2.pdd.core.keystore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.certificate.remote.RemoteKeyIdMode;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.properties.PropertiesReader;
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
	public static final String PROPERTY_HTTP_BASIC_USERNAME_DISABLE_VALUE = "#none#";
	public static final String PROPERTY_HTTP_BASIC_PASSWORD = "http.password";

	public static final String PROPERTY_HTTP_HEADER_PREFIX = "http.header.";
	
	public static final String PROPERTY_HTTP_QUERY_PARAMETER_PREFIX = "http.queryParameter.";
	
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
	
	public static final String PROPERTY_MULTITENANT = "multiTenant";
	
	public static final String PROPERTY_MULTITENANT_BASEURL_DEFAULT_STRING = "multiTenant.baseUrl.defaultString";
	public static final String PROPERTY_MULTITENANT_BASEURL_PLACEHOLDER = "multiTenant.baseUrl.placeholder";
	public static final String PROPERTY_MULTITENANT_BASEURL_TENANT_STRING = "multiTenant.baseUrl.tenantString";
	
	public static final String PROPERTY_MULTITENANT_HTTP_BASIC_USERNAME_PREFIX = "multiTenant.http.username.";
	public static final String PROPERTY_MULTITENANT_HTTP_BASIC_PASSWORD_PREFIX = "multiTenant.http.password.";

	public static final String PROPERTY_MULTITENANT_HTTP_HEADER_PREFIX = "multiTenant.http.header.";
	
	public static final String PROPERTY_MULTITENANT_HTTP_QUERY_PARAMETER_PREFIX = "multiTenant.http.queryParameter.";
	
	
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
		if(basicUsername!=null && StringUtils.isNotEmpty(basicUsername) && !PROPERTY_HTTP_BASIC_USERNAME_DISABLE_VALUE.equals(basicUsername)) {
			config.setBasicUsername(basicUsername);
			
			String basicPassword = getProperty(p, PROPERTY_HTTP_BASIC_PASSWORD, false);
			if(basicPassword!=null && StringUtils.isNotEmpty(basicPassword)) {
				config.setBasicPassword(basicPassword);
			}
		}
		
		PropertiesReader pReader = new PropertiesReader(p, false);
		
		config.setHeaders(read(pReader, PROPERTY_HTTP_HEADER_PREFIX, config.getHeaders()));
		
		config.setQueryParameters(read(pReader, PROPERTY_HTTP_QUERY_PARAMETER_PREFIX, config.getQueryParameters()));
			
		config.setHostnameVerifier(getBooleanProperty(p, PROPERTY_HTTPS_HOSTNAME_VERIFIER, false, true));	
		
		readTrustStoreConfig(config, p, requestInfo);
		
		readKeyStoreConfig(config, p, requestInfo);
		
		readForwardProxyConfig(config, p);
		
		List<String> multi = readMultitenant(p);
		if(multi!=null && !multi.isEmpty()) {
			setMultitenant(config, multi, p, pReader);
		}
		
		return config;
	}
	
	private static Map<String, String> read(PropertiesReader pReader, String pName, Map<String, String> map) throws KeystoreException {
		Properties properties = null;
		try {
			properties = pReader.readProperties_convertEnvProperties(pName);
		}catch(Exception e) {
			throw new KeystoreException("Property '"+pName+".*' read failed: "+e.getMessage(),e);
		}
		if(properties!=null && !properties.isEmpty()) {
			for (Map.Entry<Object,Object> entry : properties.entrySet()) {
				if(entry.getKey() instanceof String && entry.getValue() instanceof String) {
					String name = (String) entry.getKey();
					String value = (String) entry.getValue();
					if(map==null) {
						map = new HashMap<>();
					}
					map.put(name, value);
				}
			}
		}
		return map;
	}
	
	private static List<String> readMultitenant(Properties p) throws KeystoreException{
		List<String> l = new ArrayList<>();
		String multi = getProperty(p, PROPERTY_MULTITENANT, false);
		if(multi==null || StringUtils.isEmpty(multi.trim())) {
			return l;
		}
		multi = multi.trim();
		if(!multi.contains(",")) {
			l.add(multi);
			return l;
		}
		String [] tmp = multi.split(",");
		if(tmp!=null && tmp.length>0) {
			for (String s : tmp) {
				if(s!=null && StringUtils.isNotEmpty(s.trim())) {
					l.add(s.trim());
				}
			}
		}
		return l;
	}
	
	private static void setMultitenant(RemoteStoreConfig config, List<String> multi, Properties p, PropertiesReader pReader) throws KeystoreException {
		config.setMultitenant(true);
		config.setBaseUrlMultitenantDefaultString(getProperty(p, PROPERTY_MULTITENANT_BASEURL_DEFAULT_STRING, true));
		config.setBaseUrlMultitenantPlaceholder(getProperty(p, PROPERTY_MULTITENANT_BASEURL_PLACEHOLDER, true));
		config.setBaseUrlMultitenantTenantString(getProperty(p, PROPERTY_MULTITENANT_BASEURL_TENANT_STRING, true));
		
		for (String tenant : multi) {
			setMultitenantHttpBasic(config, tenant, p);
			setMultitenantHttp(config, tenant, pReader);
		}
	}
	private static void setMultitenantHttpBasic(RemoteStoreConfig config, String tenant, Properties p) throws KeystoreException {
		String username = getProperty(p, PROPERTY_MULTITENANT_HTTP_BASIC_USERNAME_PREFIX+tenant, false);
		if(username!=null) {
			if(config.getMultiTenantBasicUsername()==null) {
				config.setMultiTenantBasicUsername(new HashMap<>());
			}
			config.getMultiTenantBasicUsername().put(tenant, username);
		}
		
		String password = getProperty(p, PROPERTY_MULTITENANT_HTTP_BASIC_PASSWORD_PREFIX+tenant, false);
		if(password!=null) {
			if(config.getMultiTenantBasicPassword()==null) {
				config.setMultiTenantBasicPassword(new HashMap<>());
			}
			config.getMultiTenantBasicPassword().put(tenant, password);
		}
	}
	private static void setMultitenantHttp(RemoteStoreConfig config, String tenant, PropertiesReader pReader) throws KeystoreException {
		Map<String, String> header = read(pReader, PROPERTY_MULTITENANT_HTTP_HEADER_PREFIX+tenant+".", config.getMultiTenantHeaders()!=null ? config.getMultiTenantHeaders().get(tenant) : null);
		if(header!=null && !header.isEmpty()) {
			if(config.getMultiTenantHeaders()==null) {
				config.setMultiTenantHeaders(new HashMap<>());
			}
			config.getMultiTenantHeaders().put(tenant, header);
		}
		
		Map<String, String> query = read(pReader, PROPERTY_MULTITENANT_HTTP_QUERY_PARAMETER_PREFIX+tenant+".", config.getMultiTenantQueryParameters()!=null ? config.getMultiTenantQueryParameters().get(tenant) : null);
		if(query!=null && !query.isEmpty()) {
			if(config.getMultiTenantQueryParameters()==null) {
				config.setMultiTenantQueryParameters(new HashMap<>());
			}
			config.getMultiTenantQueryParameters().put(tenant, query);
		}
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
