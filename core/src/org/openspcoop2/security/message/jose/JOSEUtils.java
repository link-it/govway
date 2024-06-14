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


package org.openspcoop2.security.message.jose;

import java.net.Proxy;
import java.net.URI;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rt.security.rs.RSSecurityConstants;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.BYOKUnwrapManager;
import org.openspcoop2.security.keystore.CRLCertstore;
import org.openspcoop2.security.keystore.HttpStore;
import org.openspcoop2.security.keystore.KeyPairStore;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.MerlinTruststore;
import org.openspcoop2.security.keystore.PublicKeyStore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.KeystoreUtils;
import org.openspcoop2.utils.certificate.byok.BYOKProvider;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;
import org.openspcoop2.utils.certificate.remote.IRemoteStoreProvider;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWTOptions;
import org.openspcoop2.utils.security.JwtHeaders;
import org.openspcoop2.utils.transport.http.HttpForwardProxyConfig;
import org.openspcoop2.utils.transport.http.HttpForwardProxyOptions;
import org.openspcoop2.utils.transport.http.HttpOptions;
import org.openspcoop2.utils.transport.http.HttpProxyOptions;
import org.openspcoop2.utils.transport.http.HttpsOptions;
import org.slf4j.Logger;

/**     
 * JOSEUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JOSEUtils {

	private JOSEUtils() {}
	
	public static JOSESerialization toJOSESerialization(String mode) throws SecurityException {
		if(SecurityConstants.SIGNATURE_MODE_JSON.equals(mode)) {
			return JOSESerialization.JSON;
		}
		else if(SecurityConstants.SIGNATURE_MODE_COMPACT.equals(mode)) {
			return JOSESerialization.COMPACT;
		} 
		else {
			throw new SecurityException("Mode '"+mode+"' unsupported");
		}
	}
	
	public static JwtHeaders getJwtHeaders(Map<String, Object> properties, OpenSPCoop2Message message) throws SecurityException {
		return getJwtHeaders(properties, message, null, null, null);
	}
	public static JwtHeaders getJwtHeaders(Map<String, Object> properties, OpenSPCoop2Message message,
			String alias) throws SecurityException {
		return getJwtHeaders(properties, message, alias, null, null);
	}
	public static JwtHeaders getJwtHeaders(Map<String, Object> properties, OpenSPCoop2Message message,
			String alias, JWKSet jwkSet) throws SecurityException {
		return getJwtHeaders(properties, message, alias, jwkSet, null);
	}
	public static JwtHeaders getJwtHeaders(Map<String, Object> properties, OpenSPCoop2Message message,
			String alias, KeyStore keystore) throws SecurityException {
		return getJwtHeaders(properties, message, alias, null, keystore);
	}
	private static JwtHeaders getJwtHeaders(Map<String, Object> properties, OpenSPCoop2Message message,
			String alias, JWKSet jwkSet, 
			KeyStore keystore) throws SecurityException {
		JwtHeaders hdrs = new JwtHeaders();
		
		if(properties.containsKey(SecurityConstants.JOSE_KID)) {
			String value = (String) properties.get(SecurityConstants.JOSE_KID);
			if(value!=null) {
				value = value.trim();
			}
			if(SecurityConstants.JOSE_KID_TRUE.equalsIgnoreCase(value)) {
				hdrs.setKid(alias);
			}
		}
		else if(properties.containsKey(SecurityConstants.JOSE_KID_CUSTOM)) {
			String value = (String) properties.get(SecurityConstants.JOSE_KID_CUSTOM);
			if(value!=null) {
				value = value.trim();
				hdrs.setKid(value);
			}
		}
		
		if(properties.containsKey(SecurityConstants.JOSE_INCLUDE_CERT)) {
			String value = (String) properties.get(SecurityConstants.JOSE_INCLUDE_CERT);
			if(value!=null) {
				value = value.trim();
			}
			if(SecurityConstants.JOSE_INCLUDE_CERT_TRUE.equalsIgnoreCase(value)) {
				try {
					if(alias==null) {
						throw new SecurityException("Alias undefined (required for include cert)");
					}
					if(jwkSet!=null) {
						hdrs.setJwKey(jwkSet.getJsonWebKeys(), alias);
					}
					else if(keystore!=null) {
						
						boolean certChain = false;
						if(properties.containsKey(SecurityConstants.JOSE_INCLUDE_CERT_CHAIN)) {
							String valueChain = (String) properties.get(SecurityConstants.JOSE_INCLUDE_CERT_CHAIN);
							if(valueChain!=null) {
								valueChain = valueChain.trim();
							}
							if(SecurityConstants.JOSE_INCLUDE_CERT_CHAIN_TRUE.equalsIgnoreCase(valueChain)) {
								certChain = true;
							}
						}
						
						/**
						System.out.println("\n\n==============================================");
						System.out.println("ALIAS: ["+alias+"]");
						Certificate[] chain = keystore.getCertificateChain(alias);
						if(chain!=null) {
							System.out.println("CHAINS: ["+chain.length+"]");
						}
						for (int i = 0; i < chain.length; i++) {
							System.out.println("CHAIN["+i+"]: ["+(((X509Certificate)chain[i]).getSubjectX500Principal().toString()+"]"));
						}
						System.out.println("CERT: ["+(((X509Certificate)keystore.getCertificate(alias)).getSubjectX500Principal().toString()+"]"));
						*/
						if(certChain) {
							Certificate[] certificateChain = keystore.getCertificateChain(alias);
							if(certificateChain!=null && certificateChain.length>0) {
								for (int i = 0; i < certificateChain.length; i++) {
									hdrs.addX509cert((X509Certificate)certificateChain[i]);
								}
							}
							else {
								hdrs.addX509cert((X509Certificate)keystore.getCertificate(alias));
							}
						}
						else {
							hdrs.addX509cert((X509Certificate)keystore.getCertificate(alias));
						}
						hdrs.setAddX5C(true);
					}
				}catch(Exception e) {
					throw new SecurityException(e.getMessage(),e);
				}
			}
		}
		
		if(properties.containsKey(SecurityConstants.JOSE_INCLUDE_CERT_SHA)) {
			String value = (String) properties.get(SecurityConstants.JOSE_INCLUDE_CERT_SHA);
			if(value!=null) {
				value = value.trim();
			}
			if(hdrs.getX509c()==null || hdrs.getX509c().isEmpty()) {
				if(keystore==null) {
					throw new SecurityException("Keystore undefined (required for digest '"+value+"')");
				}
				if(alias==null) {
					throw new SecurityException("Alias undefined (required for digest '"+value+"')");
				}
				Certificate cert = null;
				try {
					cert = keystore.getCertificate(alias);
				}catch(Exception e) {
					throw new SecurityException(e.getMessage(),e);
				}
				if(cert==null) {
					throw new SecurityException("Certificate with alias '"+alias+"' not found (required for digest '"+value+"')");
				}
				hdrs.addX509cert((X509Certificate)cert);
			}
			if(SecurityConstants.JOSE_INCLUDE_CERT_SHA_1.equalsIgnoreCase(value)) {
				try {
					hdrs.setX509IncludeCertSha1(true);
				}catch(Exception e) {
					throw new SecurityException(e.getMessage(),e);
				}
			}
			else if(SecurityConstants.JOSE_INCLUDE_CERT_SHA_256.equalsIgnoreCase(value)) {
				try {
					hdrs.setX509IncludeCertSha256(true);
				}catch(Exception e) {
					throw new SecurityException(e.getMessage(),e);
				}
			}
			else {
				throw new SecurityException("Value '"+value+"' unknowkn for property '"+SecurityConstants.JOSE_INCLUDE_CERT_SHA+"'");
			}
		}
		
		if(properties.containsKey(SecurityConstants.JOSE_CONTENT_TYPE)) {
			String value = (String) properties.get(SecurityConstants.JOSE_CONTENT_TYPE);
			if(value!=null) {
				value = value.trim();
			}
			if(SecurityConstants.JOSE_CONTENT_TYPE_TRUE.equalsIgnoreCase(value)) {
				try {
					hdrs.setContentType(message.getContentType());
				}catch(Exception e) {
					throw new SecurityException(e.getMessage(),e);
				}
			}
		}
		
		if(properties.containsKey(SecurityConstants.JOSE_TYPE)) {
			String value = (String) properties.get(SecurityConstants.JOSE_TYPE);
			if(value!=null) {
				value = value.trim();
			}
			if(value!=null && !StringUtils.isEmpty(value)) {
				hdrs.setType(value);
			}
		}
		
		if(properties.containsKey(SecurityConstants.JOSE_X509_URL)) {
			String value = (String) properties.get(SecurityConstants.JOSE_X509_URL);
			if(value!=null) {
				value = value.trim();
			}
			if(value!=null && !StringUtils.isEmpty(value)) {
				try {
					hdrs.setX509Url(new URI(value));
				}catch(Exception e) {
					throw new SecurityException(e.getMessage(),e);
				}
			}
		}
		
		if(properties.containsKey(SecurityConstants.JOSE_JWK_SET_URL)) {
			String value = (String) properties.get(SecurityConstants.JOSE_JWK_SET_URL);
			if(value!=null) {
				value = value.trim();
			}
			if(value!=null && !StringUtils.isEmpty(value)) {
				try {
					hdrs.setJwkUrl(new URI(value));
				}catch(Exception e) {
					throw new SecurityException(e.getMessage(),e);
				}
			}
		}
		
		if(properties.containsKey(SecurityConstants.JOSE_CRITICAL_HEADERS)) {
			String value = (String) properties.get(SecurityConstants.JOSE_CRITICAL_HEADERS);
			if(value!=null) {
				value = value.trim();
			}
			if(value!=null && !StringUtils.isEmpty(value)) {
				if(value.contains(SecurityConstants.JOSE_CRITICAL_HEADERS_SEPARATOR)) {
					String [] tmp = value.split(SecurityConstants.JOSE_CRITICAL_HEADERS_SEPARATOR);
					for (String v : tmp) {
						if(v!=null) {
							v = v.trim();
							if(!StringUtils.isEmpty(v)) {
								hdrs.addCriticalHeader(v);
							}
						}
					}
				}
				else {
					hdrs.addCriticalHeader(value);
				}
			}
		}
		
		try {
			Properties pConvert = new Properties();
			pConvert.putAll(properties);
			Properties pExts = Utilities.readProperties(SecurityConstants.JOSE_EXT_HEADER_PREFIX, pConvert);
			if(pExts!=null && pExts.size()>0) {
				Enumeration<?> names = pExts.propertyNames();
				while (names.hasMoreElements()) {
					Object oName = names.nextElement();
					if(oName instanceof String) {
						String name = (String) oName;
						if(name!=null) {
							name = name.trim();
							if(name.endsWith(SecurityConstants.JOSE_EXT_HEADER_SUFFIX_NAME)) {
								String hdrName = pExts.getProperty(name);
								String confName = name.substring(0, name.indexOf(SecurityConstants.JOSE_EXT_HEADER_SUFFIX_NAME));
								String nameValue = confName+SecurityConstants.JOSE_EXT_HEADER_SUFFIX_VALUE;
								String hdrValue = pExts.getProperty(nameValue);
								if(hdrValue==null) {
									throw new SecurityException("Property '"+SecurityConstants.JOSE_EXT_HEADER_PREFIX+nameValue+"' not found");
								}
								hdrs.addExtension(hdrName, hdrValue);
							}
						}
					}
				}
			}
		}catch(Exception e) {
			throw new SecurityException(e.getMessage(),e);
		}
		
		return hdrs;
		
		
	}

	
	public static boolean useJwtHeadersMapProperties(Map<String, Properties> properties, JWTOptions options) {
		Properties defaultProperties = MultiPropertiesUtilities.getDefaultProperties(properties);
		return useJwtHeaders(defaultProperties, options);
	}
	public static boolean useJwtHeadersMap(Map<String, Object> propertiesParam, JWTOptions options) {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return useJwtHeaders(properties, options);
	}
	public static boolean useJwtHeaders(Properties properties, JWTOptions options) {
	
		boolean useJwtHeaders = false;
		if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS)) {
			String value = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS);
			if(value!=null) {
				value = value.trim();
				useJwtHeaders = SecurityConstants.JOSE_USE_HEADERS_TRUE.equalsIgnoreCase(value);
			}
			
			if(useJwtHeaders) {
				
				if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_X5C)) {
					value = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_X5C);
					if(value!=null) {
						value = value.trim();
						options.setPermitUseHeaderX5C(SecurityConstants.JOSE_USE_HEADERS_TRUE.equalsIgnoreCase(value));
					}
				}
				
				if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_X5U)) {
					value = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_X5U);
					if(value!=null) {
						value = value.trim();
						options.setPermitUseHeaderX5U(SecurityConstants.JOSE_USE_HEADERS_TRUE.equalsIgnoreCase(value));
					}
				}
				
				if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_X5T)) {
					value = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_X5T);
					if(value!=null) {
						value = value.trim();
						options.setPermitUseHeaderX5T(SecurityConstants.JOSE_USE_HEADERS_TRUE.equalsIgnoreCase(value));
					}
				}
				
				if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_X5T_256)) {
					value = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_X5T_256);
					if(value!=null) {
						value = value.trim();
						options.setPermitUseHeaderX5T_256(SecurityConstants.JOSE_USE_HEADERS_TRUE.equalsIgnoreCase(value));
					}
				}
				
				if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_JWK)) {
					value = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_JWK);
					if(value!=null) {
						value = value.trim();
						options.setPermitUseHeaderJWK(SecurityConstants.JOSE_USE_HEADERS_TRUE.equalsIgnoreCase(value));
					}
				}
				
				if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_JKU)) {
					value = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_JKU);
					if(value!=null) {
						value = value.trim();
						options.setPermitUseHeaderJKU(SecurityConstants.JOSE_USE_HEADERS_TRUE.equalsIgnoreCase(value));
					}
				}
				
				if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_KID)) {
					value = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_KID);
					if(value!=null) {
						value = value.trim();
						options.setPermitUseHeaderKID(SecurityConstants.JOSE_USE_HEADERS_TRUE.equalsIgnoreCase(value));
					}
				}
				
			}
		}
		
		return useJwtHeaders;
	}
	

	public static Properties toSslConfigJwtUrlHeader(Map<String, Object> propertiesParam) {
		Properties properties = null;
		if(propertiesParam.containsKey(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_FILE)) {
			String keystoreFile = (String) propertiesParam.get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_FILE);
			if(keystoreFile!=null) {
				
				properties = new Properties();
				
				properties.put(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_FILE, keystoreFile);
				
				String keystoreType = (String) propertiesParam.get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_TYPE);
				if(keystoreType!=null) {
					properties.put(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_TYPE, keystoreType);
				}
				
				String keystorePassword = (String) propertiesParam.get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_PASSWORD);
				if(keystorePassword!=null) {
					properties.put(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_PASSWORD, keystorePassword);
				}
			}
		}
		return properties;
	}
	public static KeyStore readTrustStoreSsl(RequestInfo requestInfo, Map<String, Object> propertiesParam) throws SecurityException {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return readTrustStoreSsl(requestInfo, properties);
	}
	public static KeyStore readTrustStoreSsl(RequestInfo requestInfo, Properties properties) throws SecurityException {
		return readTrustStoreEngine(requestInfo, properties, SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_FILE, 
				SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_TYPE, 
				SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_PASSWORD);
	}

	public static KeyStore readTrustStoreJwtX509Cert(RequestInfo requestInfo, Map<String, Object> propertiesParam) throws SecurityException {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return readTrustStoreJwtX509Cert(requestInfo, properties);
	}
	public static KeyStore readTrustStoreJwtX509Cert(RequestInfo requestInfo, Properties properties) throws SecurityException {
		return readTrustStoreEngine(requestInfo, properties, SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_FILE, 
				SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_TYPE, 
				SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_PASSWORD);
	}

	public static boolean isJWKSetTrustStore(Map<String, Object> propertiesParam) {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return isJWKSetTrustStore(properties);
	}
	public static boolean isJWKSetTrustStore(Properties properties) {
		if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_TYPE)) {
			String truststoreType = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_TYPE);
			if(truststoreType!=null) {
				return SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(truststoreType);
			}
		}
		return false;
	}
		
	public static JWKSet readTrustStoreJwtJsonWebKeysCert(RequestInfo requestInfo, Map<String, Object> propertiesParam) throws SecurityException {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return readTrustStoreJwtJsonWebKeysCert(requestInfo, properties);
	}
	public static JWKSet readTrustStoreJwtJsonWebKeysCert(RequestInfo requestInfo, Properties properties) throws SecurityException {
		
		if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_FILE)) {
			String truststoreFile = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_FILE);
			if(truststoreFile!=null) {
				truststoreFile = truststoreFile.trim();
			}
			else {
				throw new SecurityException("Truststore value in property '"+SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_FILE+"' is null");
			}
			try {
				return GestoreKeystoreCache.getJwkSetStore(requestInfo, truststoreFile).getJwkSet();
			}catch(Exception e) {
				throw new SecurityException(e.getMessage(),e);
			}
		}
		
		return null;
	}
	
	public static boolean isPublicKeyTrustStore(Map<String, Object> propertiesParam) {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return isPublicKeyTrustStore(properties);
	}
	public static boolean isPublicKeyTrustStore(Properties properties) {
		if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_TYPE)) {
			String truststoreType = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_TYPE);
			if(truststoreType!=null) {
				return SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(truststoreType);
			}
		}
		return false;
	}
	
	public static JWKSet readTrustStorePublicKey(RequestInfo requestInfo, Map<String, Object> propertiesParam) throws SecurityException {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return readTrustStorePublicKey(requestInfo, properties);
	}
	public static JWKSet readTrustStorePublicKey(RequestInfo requestInfo, Properties properties) throws SecurityException {
		
		if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_FILE)) {
			String truststoreFile = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_FILE);
			if(truststoreFile!=null) {
				truststoreFile = truststoreFile.trim();
			}
			else {
				throw new SecurityException("Truststore value in property '"+SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_FILE+"' is null");
			}
			String truststoreAlgo = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_KEY_PAIR_ALGORITHM);
			if(truststoreAlgo!=null) {
				truststoreAlgo = truststoreAlgo.trim();
			}
			else {
				throw new SecurityException("Key algorithm value in property '"+SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_KEY_PAIR_ALGORITHM+"' is null");
			}
			try {
				return GestoreKeystoreCache.getPublicKeyStore(requestInfo, truststoreFile, truststoreAlgo).getJwkSet();
			}catch(Exception e) {
				throw new SecurityException(e.getMessage(),e);
			}
		}
		
		return null;
	}
	
	public static boolean isRemoteStore(Map<String, Object> propertiesParam) {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return isRemoteStore(properties);
	}
	public static boolean isRemoteStore(Properties properties) {
		try {
			IRemoteStoreProvider provider = null; 
			if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_REMOTE_STORE_PROVIDER)) {
				provider = (IRemoteStoreProvider) properties.get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_REMOTE_STORE_PROVIDER);
			}
			
			RemoteKeyType keyType = null; 
			if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_REMOTE_STORE_KEY_TYPE)) {
				keyType = (RemoteKeyType) properties.get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_REMOTE_STORE_KEY_TYPE);
			}
			
			RemoteStoreConfig config = null; 
			if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_REMOTE_STORE_CONFIG)) {
				config = (RemoteStoreConfig) properties.get(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_REMOTE_STORE_CONFIG);
			}
			
			return provider!=null && keyType!=null && config!=null;
			
		}catch(Exception e) {
			return false;
		}
		
	}
	
	
	public static boolean isKeyPairKeystore(Map<String, Object> propertiesParam) {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return isKeyPairKeystore(properties);
	}
	public static boolean isKeyPairKeystore(Properties properties) {
		if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_TYPE)) {
			String keystoreType = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_TYPE);
			if(keystoreType!=null) {
				return SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType);
			}
		}
		return false;
	}
	
	public static boolean isJWKSetKeyStore(Map<String, Object> propertiesParam) {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return isJWKSetKeyStore(properties);
	}
	public static boolean isJWKSetKeyStore(Properties properties) {
		if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_TYPE)) {
			String truststoreType = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_TYPE);
			if(truststoreType!=null) {
				return SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(truststoreType);
			}
		}
		return false;
	}
	
	public static JWKSet readKeyStoreJwtJsonWebKeysCert(RequestInfo requestInfo, Map<String, Object> propertiesParam) throws SecurityException {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return readKeyStoreJwtJsonWebKeysCert(requestInfo, properties);
	}
	public static JWKSet readKeyStoreJwtJsonWebKeysCert(RequestInfo requestInfo, Properties properties) throws SecurityException {
		
		if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_FILE)) {
			String keystoreFile = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_FILE);
			if(keystoreFile!=null) {
				keystoreFile = keystoreFile.trim();
			}
			else {
				throw new SecurityException("Keystore value in property '"+SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_FILE+"' is null");
			}
			try {
				return GestoreKeystoreCache.getJwkSetStore(requestInfo, keystoreFile).getJwkSet();
			}catch(Exception e) {
				throw new SecurityException(e.getMessage(),e);
			}
		}
		
		return null;
	}
		
	public static KeyStore readKeyStoreJwtX509Cert(RequestInfo requestInfo, Map<String, Object> propertiesParam) throws SecurityException {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return readKeyStoreJwtX509Cert(requestInfo, properties);
	}
	public static KeyStore readKeyStoreJwtX509Cert(RequestInfo requestInfo, Properties properties) throws SecurityException {
		return readTrustStoreEngine(requestInfo, properties, SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_FILE, 
				SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_TYPE, 
				SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_PASSWORD);
	}
	
	public static Map<String, String> covertToJwtX509CertMapAliasPassword(Map<String, Object> propertiesParam) throws SecurityException, UtilsException {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return readJwtX509CertMapAliasPassword(properties);
	}
	public static Map<String, String> readJwtX509CertMapAliasPassword(Properties properties) throws SecurityException, UtilsException {
		try {
			HashMap<String, String> map = new HashMap<>();
			Properties pMap = Utilities.readProperties(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PW, properties);
			if(pMap!=null && pMap.size()>0) {
				Enumeration<?> names = pMap.propertyNames();
				while (names.hasMoreElements()) {
					Object oName = names.nextElement();
					if(oName instanceof String) {
						String name = (String) oName;
						name = name.trim();
						if(name.endsWith(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PW_SUFFIX_ALIAS)) {
							String alias = pMap.getProperty(name);
							String confName = name.substring(0, name.indexOf(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PW_SUFFIX_ALIAS));
							String namePassword = confName+SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PW_SUFFIX_PW;
							String password = pMap.getProperty(namePassword);
							if(password==null) {
								throw new SecurityException("Property '"+SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PW+namePassword+"' not found");
							}
							map.put(alias, password);
						}
					}
				}
			}
			if(!map.isEmpty()) {
				return map;
			}
			else {
				return null;
			}
		}catch(Exception e) {
			throw new SecurityException(e.getMessage(),e);
		}
	}
		
	private static KeyStore readTrustStoreEngine(RequestInfo requestInfo, Properties properties, String file, String type, String password) throws SecurityException {
		
		KeyStore keystore = null;
		
		String requiredPropertyPrefix = "Required property '";
		
		if(properties.containsKey(file)) {
			String keystoreFile = (String) properties.get(file);
			if(keystoreFile!=null) {
				keystoreFile = keystoreFile.trim();
				
				if(!properties.containsKey(type)) {
					throw new SecurityException(requiredPropertyPrefix+type+"' not found");
				}
				String keystoreType = (String) properties.get(type);
				if(keystoreType==null) {
					throw new SecurityException(requiredPropertyPrefix+type+"' is empty");
				}
				keystoreType = keystoreType.trim();
				
				if(!properties.containsKey(password)) {
					throw new SecurityException(requiredPropertyPrefix+password+"' not found");
				}
				String keystorePassword = (String) properties.get(password);
				if(keystorePassword==null) {
					throw new SecurityException(requiredPropertyPrefix+password+"' is empty");
				}
				keystorePassword = keystorePassword.trim();
				
				keystore = GestoreKeystoreCache.getMerlinTruststore(requestInfo, keystoreFile, keystoreType, keystorePassword).getTrustStore();
			}
		}
		
		return keystore;
	}
	
	public static final String HTTP_PROTOCOL = "http";
	public static final String HTTPS_PROTOCOL = "https";
	
	public static void injectKeystore(RequestInfo requestInfo, Map<String, Object> dynamicMap, Properties properties, Logger log) {
		try {
			injectKeystore(requestInfo, dynamicMap, properties, log, false);
		}catch(SecurityException e) {
			// ignore per default
		}
	}
	public static void injectKeystore(RequestInfo requestInfo, Map<String, Object> dynamicMap, Properties properties, Logger log, boolean throwError) throws SecurityException {
		
		if(log==null) {
			log = LoggerWrapperFactory.getLogger(JOSEUtils.class);
		}
		
		if(properties!=null && properties.containsKey(SecurityConstants.JOSE_KEYSTORE_FILE)) {
			
			String file = properties.getProperty(SecurityConstants.JOSE_KEYSTORE_FILE);
			String type = properties.getProperty(SecurityConstants.JOSE_KEYSTORE_TYPE);
			if(type==null) {
				type = SecurityConstants.KEYSTORE_TYPE_JKS_VALUE;
			}
			String password = properties.getProperty(SecurityConstants.JOSE_KEYSTORE_PSWD);
			boolean passwordDefined = (password!=null && !"".equals(password));
			
			String byokPropertyName =  SecurityConstants.JOSE_KEYSTORE_BYOK_POLICY;
			String byokProperty = properties.getProperty(byokPropertyName);
			BYOKRequestParams byokParams = null;
			BYOKUnwrapManager byokManager = null;
			if(BYOKProvider.isPolicyDefined(byokProperty)){
				try {
					byokParams = BYOKProvider.getBYOKRequestParamsByUnwrapBYOKPolicy(byokProperty, 
							dynamicMap!=null ? dynamicMap : new HashMap<>() );
					byokManager = new BYOKUnwrapManager(byokProperty, byokParams);
				}catch(Exception e) {
					String error = "Errore durante istanziazione del byok unwrap manager '"+byokProperty+"': "+e.getMessage();
					log.error(error,e);
					if(throwError) {
						throw new SecurityException(error, e);
					}
				}
			}
			
			if(file!=null && !"".equals(file) ) {
				
				if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(type)) {
					
					String privateKeyPassword = properties.getProperty(RSSecurityConstants.RSSEC_KEY_PSWD);
					
					String algorithmPropertyName =  SecurityConstants.JOSE_KEYSTORE_KEY_ALGORITHM;
					String algorithmProperty = properties.getProperty(algorithmPropertyName);
					
					String publicKeyPropertyName =  SecurityConstants.JOSE_KEYSTORE_PUBLIC_KEY;
					String publicKeyProperty = properties.getProperty(publicKeyPropertyName);
					if(publicKeyProperty==null || "".equals(publicKeyProperty) ) {
						String error = "Errore durante l'accesso al keyPair '"+file+"': property public key file ("+publicKeyPropertyName+") undefined";
						log.error(error);
						if(throwError) {
							throw new SecurityException(error);
						}
					}
					else {
						KeyPairStore keyPair = null;
						if(file.startsWith(HTTP_PROTOCOL) || file.startsWith(HTTPS_PROTOCOL)) {
							byte[] privateKey = readHttpStore(properties, requestInfo, file, log, throwError);
						
							byte[] publicKey = null;
							if(publicKeyProperty.startsWith(HTTP_PROTOCOL) || publicKeyProperty.startsWith(HTTPS_PROTOCOL)) {
								publicKey = readHttpStore(properties, requestInfo, publicKeyProperty, log, throwError);
							}
							else {
								String error = "Errore durante l'accesso al keyPair '"+file+"': property public key file ("+publicKeyPropertyName+") expected as http resource: '"+publicKeyProperty+"'";
								log.error(error);
								if(throwError) {
									throw new SecurityException(error);
								}
							}
							
							try {
								keyPair = new KeyPairStore(privateKey, publicKey, privateKeyPassword, algorithmProperty, byokParams);
							}catch(Exception e) {
								String error = "Errore durante istanziazione del keyPair (http resource) '"+file+"'/'"+publicKeyProperty+"': "+e.getMessage();
								log.error(error,e);
								if(throwError) {
									throw new SecurityException(error, e);
								}
							}
						}
						else {
							try {
								keyPair = GestoreKeystoreCache.getKeyPairStore(requestInfo, file, publicKeyProperty, privateKeyPassword, algorithmProperty, byokParams);
							}catch(Exception e) {
								String error = "Errore durante istanziazione del keyPair '"+file+"'/'"+publicKeyProperty+"': "+e.getMessage();
								log.error(error,e);
								if(throwError) {
									throw new SecurityException(error, e);
								}
							}
						}
						if(keyPair!=null) {
							try {
								String jwkSet = keyPair.getJwkSet().getJson();
								String jwkSetKid = keyPair.getJwkSetKid();
								
								properties.remove(SecurityConstants.JOSE_KEYSTORE_FILE);
								properties.remove(SecurityConstants.JOSE_KEYSTORE_PSWD);
								properties.remove(algorithmPropertyName);
								properties.remove(publicKeyPropertyName);
								properties.remove(RSSecurityConstants.RSSEC_KEY_PSWD);
								properties.remove(SecurityConstants.JOSE_KEYSTORE_KEY_ALIAS);
								properties.remove(SecurityConstants.JOSE_KEYSTORE_TYPE);
								properties.put(SecurityConstants.JOSE_KEYSTORE_JWKSET, jwkSet);
								properties.put(SecurityConstants.JOSE_KEYSTORE_KEY_ALIAS, jwkSetKid);
								properties.put(SecurityConstants.JOSE_KEYSTORE_TYPE, SecurityConstants.KEYSTORE_TYPE_JWK_VALUE);
							}catch(Exception e) {
								String error = "Errore durante istanziazione del keyPair '"+file+"'/'"+publicKeyProperty+"': "+e.getMessage();
								log.error(error,e);
								if(throwError) {
									throw new SecurityException(error, e);
								}
							}
						}
					}
					
				}
				
				else if(SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(type)) {
					
					String algorithmPropertyName =  SecurityConstants.JOSE_KEYSTORE_KEY_ALGORITHM;
					String algorithmProperty = properties.getProperty(algorithmPropertyName);
					
					PublicKeyStore publicKeyStore = null;
					if(file.startsWith(HTTP_PROTOCOL) || file.startsWith(HTTPS_PROTOCOL)) {
						byte[] publicKey = readHttpStore(properties, requestInfo, file, log, throwError);
											
						try {
							publicKeyStore = new PublicKeyStore(publicKey, algorithmProperty);
						}catch(Exception e) {
							String error = "Errore durante istanziazione della chiave pubblica (http resource) '"+file+"': "+e.getMessage();
							log.error(error,e);
							if(throwError) {
								throw new SecurityException(error, e);
							}
						}
					}
					else {
						try {
							publicKeyStore = GestoreKeystoreCache.getPublicKeyStore(requestInfo, file, algorithmProperty);
						}catch(Exception e) {
							String error = "Errore durante istanziazione della chiave pubblica '"+file+"': "+e.getMessage();
							log.error(error,e);
							if(throwError) {
								throw new SecurityException(error, e);
							}
						}
					}
					if(publicKeyStore!=null) {
						try {
							String jwkSet = publicKeyStore.getJwkSet().getJson();
							String jwkSetKid = publicKeyStore.getJwkSetKid();
							
							properties.remove(SecurityConstants.JOSE_KEYSTORE_FILE);
							properties.remove(SecurityConstants.JOSE_KEYSTORE_PSWD);
							properties.remove(algorithmPropertyName);
							properties.remove(RSSecurityConstants.RSSEC_KEY_PSWD);
							properties.remove(SecurityConstants.JOSE_KEYSTORE_KEY_ALIAS);
							properties.remove(SecurityConstants.JOSE_KEYSTORE_TYPE);
							properties.put(SecurityConstants.JOSE_KEYSTORE_JWKSET, jwkSet);
							properties.put(SecurityConstants.JOSE_KEYSTORE_KEY_ALIAS, jwkSetKid);
							properties.put(SecurityConstants.JOSE_KEYSTORE_TYPE, SecurityConstants.KEYSTORE_TYPE_JWK_VALUE);
						}catch(Exception e) {
							String error = "Errore durante istanziazione della chiave pubblica '"+file+"': "+e.getMessage();
							log.error(error,e);
							if(throwError) {
								throw new SecurityException(error, e);
							}
						}
					}
					
				}
				
				else if( passwordDefined || SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(type)) {
					
					if(file.startsWith(HTTP_PROTOCOL) || file.startsWith(HTTPS_PROTOCOL)) {
						
						byte[]content = readHttpStore(properties, requestInfo, file, log, throwError);
						
						if(byokManager!=null) {
							try {
								content = byokManager.unwrap(content);
							}catch(Exception e) {
								String error = "Errore durante l'unwrap del keystore ottenuto via http '"+file+"': "+e.getMessage();
								log.error(error,e);
								if(throwError) {
									throw new SecurityException(error, e);
								}
							}
						}
						
						if(content!=null) {
							if(SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(type)) {
								properties.remove(SecurityConstants.JOSE_KEYSTORE_FILE);
								properties.remove(SecurityConstants.JOSE_KEYSTORE_PSWD);
								properties.put(SecurityConstants.JOSE_KEYSTORE_JWKSET, new String(content));
							}
							else {
								java.security.KeyStore keystore = null;
								try {
									keystore = KeystoreUtils.readKeystore(content, type, password);
								}catch(Exception e) {
									String error = "Errore durante istanziazione del keystore ottenuto via http '"+file+"': "+e.getMessage();
									log.error(error,e);
									keystore = null;
									if(throwError) {
										throw new SecurityException(error, e);
									}
								}
								if(keystore!=null) {
									properties.remove(SecurityConstants.JOSE_KEYSTORE_FILE);
									// properties.remove(SecurityConstants.JOSE_KEYSTORE_TYPE); non va rimosso, serve per jceks
									properties.remove(SecurityConstants.JOSE_KEYSTORE_PSWD);
									properties.put(SecurityConstants.JOSE_KEYSTORE, keystore);
								}
							}
						}
					}
					else {
						if(SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(type)) {
							String jwkSet = null;
							try {
								jwkSet = GestoreKeystoreCache.getJwkSetStore(requestInfo, file, byokParams).getJwkSetContent();
							}catch(Exception e) {
								String error = "Errore durante l'accesso al jwk set '"+file+"': "+e.getMessage();
								log.error(error,e);
								if(throwError) {
									throw new SecurityException(error, e);
								}
							}
							if(jwkSet!=null) {
								properties.remove(SecurityConstants.JOSE_KEYSTORE_FILE);
								properties.remove(SecurityConstants.JOSE_KEYSTORE_PSWD);
								properties.put(SecurityConstants.JOSE_KEYSTORE_JWKSET, jwkSet);
							}
						}
						else {
							java.security.KeyStore keystore = null;
							try {
								MerlinKeystore merlinKeystore = GestoreKeystoreCache.getMerlinKeystore(requestInfo, file, type, password, byokParams);
								if(merlinKeystore==null) {
									throw new SecurityException("Keystore '"+file+"' undefined");
								}
								KeyStore keystoreUtils = merlinKeystore.getKeyStore();
								if(keystoreUtils==null) {
									throw new SecurityException("Keystore '"+file+"' undefined");
								}
								keystore = keystoreUtils.getKeystore();
							}catch(Exception e) {
								String error = "Errore durante l'accesso al keystore '"+file+"': "+e.getMessage();
								log.error(error,e);
								if(throwError) {
									throw new SecurityException(error, e);
								}
							}
							if(keystore!=null) {
								properties.remove(SecurityConstants.JOSE_KEYSTORE_FILE);
								// properties.remove(SecurityConstants.JOSE_KEYSTORE_TYPE); non va rimosso, serve per jceks
								properties.remove(SecurityConstants.JOSE_KEYSTORE_PSWD);
								properties.put(SecurityConstants.JOSE_KEYSTORE, keystore);
							}
						}
					}
				}
			}
		}
		
	}
	
	public static byte[] readHttpStore(Properties properties, RequestInfo requestInfo, String file, Logger log, boolean throwError) throws SecurityException {
		return readHttpStore( properties, requestInfo, file, log, throwError, false);
	}
	public static byte[] readHttpStore(Properties properties, RequestInfo requestInfo, String file, Logger log, boolean throwError, boolean forceNoCache) throws SecurityException {
		byte[]content = null;
		try {
			boolean trustAll = false;
			String trustStoreAllSslProperty = properties.getProperty(JOSECostanti.ID_TRUSTSTORE_SSL_KEYSTORE_TRUSTALL);
			trustAll = trustStoreAllSslProperty!=null && "true".equalsIgnoreCase(trustStoreAllSslProperty);
			
			String trustStoreSslPropertyName = JOSECostanti.ID_TRUSTSTORE_SSL_KEYSTORE_FILE;
			String trustStoreSslProperty =  properties.getProperty(trustStoreSslPropertyName);
			MerlinTruststore trustStoreSsl = null;
			if(trustStoreSslProperty!=null) {
				String trustStoreSslPasswordPropertyName = JOSECostanti.ID_TRUSTSTORE_SSL_KEYSTORE_PASSWORD;
				String trustStoreSslTypePropertyName = JOSECostanti.ID_TRUSTSTORE_SSL_KEYSTORE_TYPE;
				String trustStoreSslPasswordProperty = properties.getProperty(trustStoreSslPasswordPropertyName);
				String trustStoreSslTypeProperty = properties.getProperty(trustStoreSslTypePropertyName);
				if(trustStoreSslPasswordProperty==null) {
					throw new SecurityException("TrustStore ssl password undefined");
				}
				if(trustStoreSslTypeProperty==null) {
					throw new SecurityException("TrustStore ssl type undefined");
				}
				trustStoreSsl = GestoreKeystoreCache.getMerlinTruststore(requestInfo, trustStoreSslProperty, trustStoreSslTypeProperty, trustStoreSslPasswordProperty);
			}
			
			String trustStoreSslCrlPropertyName =  JOSECostanti.ID_TRUSTSTORE_SSL_KEYSTORE_CRL;
			String trustStoreSslCrlProperty =  properties.getProperty(trustStoreSslCrlPropertyName);
			CRLCertstore crlStore = null;
			if(trustStoreSslCrlProperty!=null) {
				crlStore = GestoreKeystoreCache.getCRLCertstore(requestInfo, trustStoreSslCrlProperty);
			}
			
			String trustStoreSslConnectionTimeoutPropertyName =  JOSECostanti.ID_TRUSTSTORE_SSL_KEYSTORE_CONNECTION_TIMEOUT;
			String trustStoreSslReadTimeoutPropertyName =  JOSECostanti.ID_TRUSTSTORE_SSL_KEYSTORE_READ_TIMEOUT;
			String trustStoreSslConnectionTimeoutProperty =  properties.getProperty(trustStoreSslConnectionTimeoutPropertyName);
			String trustStoreSslReadTimeoutProperty =  properties.getProperty(trustStoreSslReadTimeoutPropertyName);
			Integer connectionTimeout = null;
			Integer readTimeout = null;
			if(trustStoreSslConnectionTimeoutProperty!=null && trustStoreSslReadTimeoutProperty!=null) {
				connectionTimeout = Integer.valueOf(trustStoreSslConnectionTimeoutProperty);
				readTimeout = Integer.valueOf(trustStoreSslReadTimeoutProperty);
			}
			
			List<HttpOptions> list = new ArrayList<>();
			
			String trustStoreSslHostnameVerifier =  properties.getProperty(JOSECostanti.ID_TRUSTSTORE_SSL_KEYSTORE_HOSTNAME_VERIFIER);
			if(trustStoreSslHostnameVerifier!=null && StringUtils.isNotEmpty(trustStoreSslHostnameVerifier)) {
				HttpsOptions op = new HttpsOptions();
				op.setHostnameVerifier("true".equals(trustStoreSslHostnameVerifier)); // lascio default a false
				list.add(op);
			}
			
			String forwardProxyEndpoint = properties.getProperty(JOSECostanti.ID_FORWARD_PROXY_ENDPOINT);
			if(forwardProxyEndpoint!=null && StringUtils.isNotEmpty(forwardProxyEndpoint)) {
				HttpForwardProxyOptions op = new HttpForwardProxyOptions();
				op.setForwardProxyEndpoint(forwardProxyEndpoint);
				
				HttpForwardProxyConfig c = new HttpForwardProxyConfig();
				String forwardProxyHeader = properties.getProperty(JOSECostanti.ID_FORWARD_PROXY_HEADER);
				String forwardProxyQuery = properties.getProperty(JOSECostanti.ID_FORWARD_PROXY_QUERY);
				if(forwardProxyHeader!=null && StringUtils.isNotEmpty(forwardProxyHeader)) {
					c.setHeader(forwardProxyHeader);
					String forwardProxyHeaderBase64 = properties.getProperty(JOSECostanti.ID_FORWARD_PROXY_HEADER_BASE64);
					if(forwardProxyHeaderBase64!=null && StringUtils.isNotEmpty(forwardProxyHeaderBase64)) {
						c.setHeaderBase64("false".equals(forwardProxyHeaderBase64)); // default true
					}
				}
				else if(forwardProxyQuery!=null && StringUtils.isNotEmpty(forwardProxyQuery)) {
					c.setQuery(forwardProxyQuery);
					String forwardProxyQueryBase64 = properties.getProperty(JOSECostanti.ID_FORWARD_PROXY_QUERY_BASE64);
					if(forwardProxyQueryBase64!=null && StringUtils.isNotEmpty(forwardProxyQueryBase64)) {
						c.setQueryBase64("false".equals(forwardProxyQueryBase64)); // default true
					}
				}
				else {
					throw new SecurityException("ForwardProxy header o query required");
				}
				op.setForwardProxyConfig(c);
				
				list.add(op);
			}
			
			String proxyType = properties.getProperty(JOSECostanti.ID_PROXY_TYPE);
			if(proxyType!=null && StringUtils.isNotEmpty(proxyType)) {
				HttpProxyOptions op = new HttpProxyOptions();
				if(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP.equals(proxyType)){
					op.setProxyType(Proxy.Type.HTTP);
				}
				else if(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTPS.equals(proxyType)){
					op.setProxyType(Proxy.Type.HTTP); // voluto
				}
				else{
					throw new SecurityException("Proxy type '"+proxyType+"' unsupported");
				}
				
				String proxyHostname = properties.getProperty(JOSECostanti.ID_PROXY_HOSTNAME);
				if(proxyHostname!=null && StringUtils.isNotEmpty(proxyHostname)) {
					op.setProxyHostname(proxyHostname);
				}
				else{
					throw new SecurityException("Proxy hostname undefined");
				}
				
				String proxyPort = properties.getProperty(JOSECostanti.ID_PROXY_HOSTNAME);
				if(proxyPort!=null && StringUtils.isNotEmpty(proxyPort)) {
					try {
						op.setProxyPort(Integer.valueOf(proxyPort));
					}catch(Exception e) {
						throw new SecurityException("Proxy port '"+proxyPort+"' invalid: "+e.getMessage());
					}
				}
				else{
					throw new SecurityException("Proxy port undefined");
				}
				
				String proxyUsername = properties.getProperty(JOSECostanti.ID_PROXY_USERNAME);
				if(proxyUsername!=null && StringUtils.isNotEmpty(proxyUsername)) {
					op.setProxyUsername(proxyUsername);
					String proxyPassword = properties.getProperty(JOSECostanti.ID_PROXY_USERNAME);
					if(proxyPassword!=null) {
						op.setProxyPassword(proxyPassword);
					}
				}
				
				list.add(op);
			}
			
			HttpOptions [] options = null;
			if(!list.isEmpty()) {
				options = list.toArray(new HttpOptions[1]);
			}
			if(forceNoCache) {
				if(connectionTimeout!=null && readTimeout!=null) {
					if(trustStoreSsl!=null) {
						if(crlStore!=null) {
							content = new HttpStore(file, connectionTimeout, readTimeout, trustStoreSsl, crlStore, options).getStoreBytes();
						}
						else {
							content = new HttpStore(file, connectionTimeout, readTimeout, trustStoreSsl, options).getStoreBytes();
						}
					}
					else if(trustAll) {
						content = new HttpStore(file, connectionTimeout, readTimeout, trustAll, options).getStoreBytes();
					}
					else {
						content = new HttpStore(file, connectionTimeout, readTimeout, options).getStoreBytes();
					}
				}
				else {
					if(trustStoreSsl!=null) {
						if(crlStore!=null) {
							content = new HttpStore(file, trustStoreSsl, crlStore, options).getStoreBytes();
						}
						else {
							content = new HttpStore(file, trustStoreSsl, options).getStoreBytes();
						}
					}
					else if(trustAll) {
						content = new HttpStore(file, trustAll, options).getStoreBytes();
					}
					else {
						content = new HttpStore(file, options).getStoreBytes();
					}
				}
			}
			else {
				if(connectionTimeout!=null && readTimeout!=null) {
					if(trustStoreSsl!=null) {
						if(crlStore!=null) {
							content = GestoreKeystoreCache.getHttpStore(requestInfo, file, connectionTimeout, readTimeout, trustStoreSsl, crlStore, options).getStoreBytes();
						}
						else {
							content = GestoreKeystoreCache.getHttpStore(requestInfo, file, connectionTimeout, readTimeout, trustStoreSsl, options).getStoreBytes();
						}
					}
					else if(trustAll) {
						content = GestoreKeystoreCache.getHttpStore(requestInfo, file, connectionTimeout, readTimeout, trustAll, options).getStoreBytes();
					}
					else {
						content = GestoreKeystoreCache.getHttpStore(requestInfo, file, connectionTimeout, readTimeout, options).getStoreBytes();
					}
				}
				else {
					if(trustStoreSsl!=null) {
						if(crlStore!=null) {
							content = GestoreKeystoreCache.getHttpStore(requestInfo, file, trustStoreSsl, crlStore, options).getStoreBytes();
						}
						else {
							content = GestoreKeystoreCache.getHttpStore(requestInfo, file, trustStoreSsl, options).getStoreBytes();
						}
					}
					else if(trustAll) {
						content = GestoreKeystoreCache.getHttpStore(requestInfo, file, trustAll, options).getStoreBytes();
					}
					else {
						content = GestoreKeystoreCache.getHttpStore(requestInfo, file, options).getStoreBytes();
					}
				}
			}
		}catch(Exception e) {
			String error = "Errore durante l'accesso al keystore via http '"+file+"': "+e.getMessage();
			log.error(error,e);
			if(throwError) {
				throw new SecurityException(error, e);
			}
		}
		return content;
	}
}
