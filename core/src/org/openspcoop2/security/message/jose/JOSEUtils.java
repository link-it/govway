/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rt.security.rs.RSSecurityConstants;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.CRLCertstore;
import org.openspcoop2.security.keystore.MerlinTruststore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWTOptions;
import org.openspcoop2.utils.security.JwtHeaders;
import org.slf4j.Logger;

/**     
 * JOSEUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JOSEUtils {

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
	
	public static JwtHeaders getJwtHeaders(Hashtable<String, Object> properties, OpenSPCoop2Message message) throws SecurityException {
		return getJwtHeaders(properties, message, null, null, null);
	}
	public static JwtHeaders getJwtHeaders(Hashtable<String, Object> properties, OpenSPCoop2Message message,
			String alias) throws SecurityException {
		return getJwtHeaders(properties, message, alias, null, null);
	}
	public static JwtHeaders getJwtHeaders(Hashtable<String, Object> properties, OpenSPCoop2Message message,
			String alias, JWKSet jwkSet) throws SecurityException {
		return getJwtHeaders(properties, message, alias, jwkSet, null);
	}
	public static JwtHeaders getJwtHeaders(Hashtable<String, Object> properties, OpenSPCoop2Message message,
			String alias, KeyStore keystore) throws SecurityException {
		return getJwtHeaders(properties, message, alias, null, keystore);
	}
	private static JwtHeaders getJwtHeaders(Hashtable<String, Object> properties, OpenSPCoop2Message message,
			String alias, JWKSet jwkSet, 
			KeyStore keystore) throws SecurityException {
		JwtHeaders hdrs = new JwtHeaders();
		
		if(properties.containsKey(SecurityConstants.JOSE_KID)) {
			String value = (String) properties.get(SecurityConstants.JOSE_KID);
			if(value!=null) {
				value = value.trim();
			}
			if(SecurityConstants.JOSE_KID_TRUE.equalsIgnoreCase(value)) {
				try {
					hdrs.setKid(alias);
				}catch(Exception e) {
					throw new SecurityException(e.getMessage(),e);
				}
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
						throw new Exception("Alias undefined (required for include cert)");
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
						
						/*
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
					Object oName = (Object) names.nextElement();
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

	
	public static boolean useJwtHeaders(Map<String, Properties> properties, JWTOptions options) throws ProviderException, ProviderValidationException {
		Properties defaultProperties = MultiPropertiesUtilities.getDefaultProperties(properties);
		return useJwtHeaders(defaultProperties, options);
	}
	public static boolean useJwtHeaders(Hashtable<String, Object> propertiesParam, JWTOptions options) {
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
	

	public static Properties toSslConfigJwtUrlHeader(Hashtable<String, Object> propertiesParam) throws SecurityException, UtilsException {
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
	public static KeyStore readTrustStoreSsl(Hashtable<String, Object> propertiesParam) throws SecurityException, UtilsException {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return readTrustStoreSsl(properties);
	}
	public static KeyStore readTrustStoreSsl(Properties properties) throws SecurityException, UtilsException {
		return _readTrustStore(properties, SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_FILE, 
				SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_TYPE, 
				SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_PASSWORD);
	}

	public static KeyStore readTrustStoreJwtX509Cert(Hashtable<String, Object> propertiesParam) throws SecurityException, UtilsException {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return readTrustStoreJwtX509Cert(properties);
	}
	public static KeyStore readTrustStoreJwtX509Cert(Properties properties) throws SecurityException, UtilsException {
		return _readTrustStore(properties, SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_FILE, 
				SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_TYPE, 
				SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_PASSWORD);
	}

	public static boolean isJWKSetKeystore(Hashtable<String, Object> propertiesParam) {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return isJWKSetKeystore(properties);
	}
	public static boolean isJWKSetKeystore(Properties properties) {
		if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_TYPE)) {
			String keystoreType = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_TYPE);
			if(keystoreType!=null) {
				return "jwk".equalsIgnoreCase(keystoreType);
			}
		}
		return false;
	}
	
	public static JWKSet readKeyStoreJwtJsonWebKeysCert(Hashtable<String, Object> propertiesParam) throws SecurityException, UtilsException {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return readKeyStoreJwtJsonWebKeysCert(properties);
	}
	public static JWKSet readKeyStoreJwtJsonWebKeysCert(Properties properties) throws SecurityException, UtilsException {
		
		if(properties.containsKey(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_FILE)) {
			String keystoreFile = (String) properties.get(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_FILE);
			if(keystoreFile!=null) {
				keystoreFile = keystoreFile.trim();
			}
			try {
				return new JWKSet(FileSystemUtilities.readFile(keystoreFile));
			}catch(Exception e) {
				throw new SecurityException(e.getMessage(),e);
			}
		}
		
		return null;
	}
	
	public static KeyStore readKeyStoreJwtX509Cert(Hashtable<String, Object> propertiesParam) throws SecurityException, UtilsException {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return readKeyStoreJwtX509Cert(properties);
	}
	public static KeyStore readKeyStoreJwtX509Cert(Properties properties) throws SecurityException, UtilsException {
		return _readTrustStore(properties, SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_FILE, 
				SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_TYPE, 
				SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_PASSWORD);
	}
	
	public static HashMap<String, String> readJwtX509Cert_mapAliasPassword(Hashtable<String, Object> propertiesParam) throws SecurityException, UtilsException {
		Properties properties = new Properties();
		properties.putAll(propertiesParam);
		return readJwtX509Cert_mapAliasPassword(properties);
	}
	public static HashMap<String, String> readJwtX509Cert_mapAliasPassword(Properties properties) throws SecurityException, UtilsException {
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			Properties pMap = Utilities.readProperties(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PASSWORD, properties);
			if(pMap!=null && pMap.size()>0) {
				Enumeration<?> names = pMap.propertyNames();
				while (names.hasMoreElements()) {
					Object oName = (Object) names.nextElement();
					if(oName instanceof String) {
						String name = (String) oName;
						if(name!=null) {
							name = name.trim();
							if(name.endsWith(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PASSWORD_SUFFIX_ALIAS)) {
								String alias = pMap.getProperty(name);
								String confName = name.substring(0, name.indexOf(SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PASSWORD_SUFFIX_ALIAS));
								String namePassword = confName+SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PASSWORD_SUFFIX_PASSWORD;
								String password = pMap.getProperty(namePassword);
								if(password==null) {
									throw new SecurityException("Property '"+SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PASSWORD+namePassword+"' not found");
								}
								map.put(alias, password);
							}
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
		
	private static KeyStore _readTrustStore(Properties properties, String file, String type, String password) throws SecurityException, UtilsException {
		
		KeyStore keystore = null;
		
		if(properties.containsKey(file)) {
			String keystoreFile = (String) properties.get(file);
			if(keystoreFile!=null) {
				keystoreFile = keystoreFile.trim();
				
				if(!properties.containsKey(type)) {
					throw new SecurityException("Required property '"+type+"' not found");
				}
				String keystoreType = (String) properties.get(type);
				if(keystoreType==null) {
					throw new SecurityException("Required property '"+type+"' is empty");
				}
				keystoreType = keystoreType.trim();
				
				if(!properties.containsKey(password)) {
					throw new SecurityException("Required property '"+password+"' not found");
				}
				String keystorePassword = (String) properties.get(password);
				if(keystorePassword==null) {
					throw new SecurityException("Required property '"+password+"' is empty");
				}
				keystorePassword = keystorePassword.trim();
				
				keystore = new KeyStore(GestoreKeystoreCache.getMerlinTruststore(keystoreFile, keystoreType, keystorePassword).getTrustStore());
			}
		}
		
		return keystore;
	}
	
	public static void injectKeystore(Properties properties, Logger log) {
		
		if(log==null) {
			log = LoggerWrapperFactory.getLogger(JOSEUtils.class);
		}
		
		if(properties!=null && properties.containsKey(RSSecurityConstants.RSSEC_KEY_STORE_FILE)) {
			
			String file = properties.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_FILE);
			String type = properties.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if(type==null) {
				type = "jks";
			}
			String password = properties.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_PSWD);
			boolean passwordDefined = (password!=null && !"".equals(password));
			
			if(file!=null && !"".equals(file) && 
					(passwordDefined || "jwk".equalsIgnoreCase(type))) {
				
				if(file!=null && (file.startsWith("http") || file.startsWith("https"))) {
					
					byte[]content = null;
					try {
						String _trustStoreSslProperty =  RSSecurityConstants.RSSEC_KEY_STORE_FILE+".ssl";
						String trustStoreSslProperty =  properties.getProperty(_trustStoreSslProperty);
						MerlinTruststore trustStoreSsl = null;
						if(trustStoreSslProperty!=null) {
							String _trustStoreSslPasswordProperty =  RSSecurityConstants.RSSEC_KEY_STORE_PSWD+".ssl";
							String _trustStoreSslTypeProperty =  RSSecurityConstants.RSSEC_KEY_STORE_TYPE+".ssl";
							String trustStoreSslPasswordProperty =  properties.getProperty(_trustStoreSslPasswordProperty);
							String trustStoreSslTypeProperty =  properties.getProperty(_trustStoreSslTypeProperty);
							if(trustStoreSslPasswordProperty==null) {
								throw new Exception("TrustStore ssl password undefined");
							}
							if(trustStoreSslTypeProperty==null) {
								throw new Exception("TrustStore ssl type undefined");
							}
							trustStoreSsl = GestoreKeystoreCache.getMerlinTruststore(trustStoreSslProperty, trustStoreSslTypeProperty, trustStoreSslPasswordProperty);
						}
						
						String _trustStoreSslCrlProperty =  RSSecurityConstants.RSSEC_KEY_STORE+".crl.ssl";
						String trustStoreSslCrlProperty =  properties.getProperty(_trustStoreSslCrlProperty);
						CRLCertstore crlStore = null;
						if(trustStoreSslCrlProperty!=null) {
							crlStore = GestoreKeystoreCache.getCRLCertstore(trustStoreSslCrlProperty);
						}
						
						String _trustStoreSslConnectionTimeoutProperty =  RSSecurityConstants.RSSEC_KEY_STORE+".ssl.connectionTimeout";
						String _trustStoreSslReadTimeoutProperty =  RSSecurityConstants.RSSEC_KEY_STORE+".ssl.readTimeout";
						String trustStoreSslConnectionTimeoutProperty =  properties.getProperty(_trustStoreSslConnectionTimeoutProperty);
						String trustStoreSslReadTimeoutProperty =  properties.getProperty(_trustStoreSslReadTimeoutProperty);
						Integer connectionTimeout = null;
						Integer readTimeout = null;
						if(trustStoreSslConnectionTimeoutProperty!=null && trustStoreSslReadTimeoutProperty!=null) {
							connectionTimeout = Integer.valueOf(trustStoreSslConnectionTimeoutProperty);
							readTimeout = Integer.valueOf(trustStoreSslReadTimeoutProperty);
						}
						
						if(connectionTimeout!=null && readTimeout!=null) {
							if(trustStoreSsl!=null) {
								if(crlStore!=null) {
									content = GestoreKeystoreCache.getHttpStore(file, connectionTimeout, readTimeout, trustStoreSsl, crlStore).getStoreBytes();
								}
								else {
									content = GestoreKeystoreCache.getHttpStore(file, connectionTimeout, readTimeout, trustStoreSsl).getStoreBytes();
								}
							}
							else {
								content = GestoreKeystoreCache.getHttpStore(file, connectionTimeout, readTimeout).getStoreBytes();
							}
						}
						else {
							if(trustStoreSsl!=null) {
								if(crlStore!=null) {
									content = GestoreKeystoreCache.getHttpStore(file, trustStoreSsl, crlStore).getStoreBytes();
								}
								else {
									content = GestoreKeystoreCache.getHttpStore(file, trustStoreSsl).getStoreBytes();
								}
							}
							else {
								content = GestoreKeystoreCache.getHttpStore(file).getStoreBytes();
							}
						}
					}catch(Throwable e) {
						log.error("Errore durante l'accesso al keystore via http '"+file+"': "+e.getMessage(),e);
					}
					if(content!=null) {
						if("jwk".equalsIgnoreCase(type)) {
							properties.remove(RSSecurityConstants.RSSEC_KEY_STORE_FILE);
							properties.remove(RSSecurityConstants.RSSEC_KEY_STORE_PSWD);
							properties.put(JoseConstants.RSSEC_KEY_STORE_JWKSET, new String(content));
						}
						else {
							java.security.KeyStore keystore = null;
							try {
								keystore = java.security.KeyStore.getInstance(type);
								try(ByteArrayInputStream bin = new ByteArrayInputStream(content)){
									keystore.load(bin, password.toCharArray());
								}
							}catch(Throwable e) {
								log.error("Errore durante istanziazione del keystore ottenuto via http '"+file+"': "+e.getMessage(),e);
								keystore = null;
							}
							if(keystore!=null) {
								properties.remove(RSSecurityConstants.RSSEC_KEY_STORE_FILE);
								// properties.remove(JoseConstants.RSSEC_KEY_STORE_TYPE); non va rimosso, serve per jceks
								properties.remove(RSSecurityConstants.RSSEC_KEY_STORE_PSWD);
								properties.put(RSSecurityConstants.RSSEC_KEY_STORE, keystore);
							}
						}
					}
				}
				else {
					if("jwk".equalsIgnoreCase(type)) {
						String jwkSet = null;
						try {
							jwkSet = GestoreKeystoreCache.getJwkSetStore(file).getJwkSetContent();
						}catch(Throwable e) {
							log.error("Errore durante l'accesso al jwk set '"+file+"': "+e.getMessage(),e);
						}
						if(jwkSet!=null) {
							properties.remove(RSSecurityConstants.RSSEC_KEY_STORE_FILE);
							properties.remove(RSSecurityConstants.RSSEC_KEY_STORE_PSWD);
							properties.put(JoseConstants.RSSEC_KEY_STORE_JWKSET, jwkSet);
						}
					}
					else {
						java.security.KeyStore keystore = null;
						try {
							keystore = GestoreKeystoreCache.getMerlinKeystore(file, type, password).getKeyStore();
						}catch(Throwable e) {
							log.error("Errore durante l'accesso al keystore '"+file+"': "+e.getMessage(),e);
						}
						if(keystore!=null) {
							properties.remove(RSSecurityConstants.RSSEC_KEY_STORE_FILE);
							// properties.remove(JoseConstants.RSSEC_KEY_STORE_TYPE); non va rimosso, serve per jceks
							properties.remove(RSSecurityConstants.RSSEC_KEY_STORE_PSWD);
							properties.put(RSSecurityConstants.RSSEC_KEY_STORE, keystore);
						}
					}
				}
			}
		}
		
	}
}
