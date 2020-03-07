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

package org.openspcoop2.security.message.saml;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.joda.time.DateTime;
import org.openspcoop2.security.keystore.KeystoreConstants;
import org.openspcoop2.security.message.constants.SecurityConstants;


/**
 * SAMLUtilities
 * 	
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SAMLUtilities {

	public static DateTime minutesOperator(DateTime dateTime,Integer intValue){
		DateTime dateTimeRes = dateTime;
		if(intValue!=null){
			if(intValue.intValue() == 0){
				dateTimeRes = dateTime;
			}
			else if(intValue.intValue() > 0){
				dateTimeRes = dateTime.plusMinutes(intValue.intValue());
			}
			else if(intValue.intValue() < 0){
				dateTimeRes = dateTime.minusMinutes(intValue.intValue()*-1);
			}
		}
		return dateTimeRes;
	}

	public static void injectSignaturePropRefIdIntoSamlConfig(Hashtable<String,Object> wssProperties) throws Exception {
		
		if (wssProperties != null && wssProperties.size() > 0) {
			
			// preprocess per saml
			for (Enumeration<?> e = wssProperties.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				Object oValue = wssProperties.get(key);
				String value = null;
				if(oValue!=null && oValue instanceof String) {
					value = (String) oValue;
				}
				
				if(SecurityConstants.SIGNATURE_PARTS.equals(key)) {
					if(value.contains(SecurityConstants.SAML_NAMESPACE_TEMPLATE)) {
						String samlVersion = null;
						if(wssProperties.containsKey(SecurityConstants.SAML_PROF_REF_ID)) {
							Object o = wssProperties.get(SecurityConstants.SAML_PROF_REF_ID);
							if(o instanceof Properties) {
								Properties samlConfig = (Properties) o;
								if(samlConfig.containsKey(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_VERSION)) {
									samlVersion = (String) samlConfig.getProperty(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_VERSION);
								}
							}
						}
						if(samlVersion==null) {
							throw new Exception("SAML version undefined");
						}
						if(SecurityConstants.SAML_VERSION_XMLCONFIG_ID_VALUE_20.equals(samlVersion)) {
							while(value.contains(SecurityConstants.SAML_NAMESPACE_TEMPLATE)) {
								value = value.replace(SecurityConstants.SAML_NAMESPACE_TEMPLATE, SAMLConstants.SAML_20_NAMESPACE);
							}
						}
						else {
							while(value.contains(SecurityConstants.SAML_NAMESPACE_TEMPLATE)) {
								value = value.replace(SecurityConstants.SAML_NAMESPACE_TEMPLATE, SAMLConstants.SAML_11_NAMESPACE);
							}
						}
						wssProperties.remove(key);
						wssProperties.put(key, value);
					}
				}
				else if(SecurityConstants.SAML_SIGNATURE_PARAM_CONVERTO_INTO_SAML_CONFIG.equals(key) || 
						SecurityConstants.SAML_SIGNATURE_PARAM_CONVERTO_INTO_SAML_CONFIG_HOLDER_OF_KEY.equals(key) ) {
					if(value!=null) {
						if(wssProperties.containsKey(value)) {
							
							// raccolgo parametri 
							Properties signaturePropRefiId = null;
							String signatureAlias = null;
							String signaturePassword = null;
							if(wssProperties.containsKey(SecurityConstants.SIGNATURE_PROPERTY_REF_ID)) {
								Object o = wssProperties.get(SecurityConstants.SIGNATURE_PROPERTY_REF_ID);
								if(o instanceof Properties) {
									signaturePropRefiId = (Properties) o;
								}
							}
							if(wssProperties.containsKey(SecurityConstants.SIGNATURE_USER)) {
								Object o = wssProperties.get(SecurityConstants.SIGNATURE_USER);
								if(o instanceof String) {
									signatureAlias = (String) o;
								}
							}
							else if(wssProperties.containsKey(SecurityConstants.USER)) {
								Object o = wssProperties.get(SecurityConstants.USER);
								if(o instanceof String) {
									signatureAlias = (String) o;
								}
							}
							if(wssProperties.containsKey(SecurityConstants.SIGNATURE_PASSWORD)) {
								Object o = wssProperties.get(SecurityConstants.SIGNATURE_PASSWORD);
								if(o instanceof String) {
									signaturePassword = (String) o;
								}
							}
							
							// algoritmi
							String signatureAlgorithm = null;
							String signatureDigestAlgorithm = null;
							String signatureC14nAlgorithmExclusive = null;
							if(SecurityConstants.SAML_SIGNATURE_PARAM_CONVERTO_INTO_SAML_CONFIG.equals(key)) {
								if(wssProperties.containsKey(SecurityConstants.SIGNATURE_ALGORITHM)) {
									Object o = wssProperties.get(SecurityConstants.SIGNATURE_ALGORITHM);
									if(o instanceof String) {
										signatureAlgorithm = (String) o;
									}
								}
								if(wssProperties.containsKey(SecurityConstants.SIGNATURE_DIGEST_ALGORITHM)) {
									Object o = wssProperties.get(SecurityConstants.SIGNATURE_DIGEST_ALGORITHM);
									if(o instanceof String) {
										signatureDigestAlgorithm = (String) o;
									}
								}
								if(wssProperties.containsKey(SecurityConstants.SIGNATURE_C14N_ALGORITHM)) {
									Object o = wssProperties.get(SecurityConstants.SIGNATURE_C14N_ALGORITHM);
									if(o instanceof String) {
										signatureC14nAlgorithmExclusive = (String) o;
									}
								}
							}
							
							// Prelevo saml comfig
							Object o = wssProperties.get(value);
							if(o instanceof Properties) {
								Properties samlConfig = (Properties) o;
								
								String type = signaturePropRefiId.getProperty("org.apache.ws.security.crypto.merlin.keystore.type");
								if(type!=null) {
									if(SecurityConstants.SAML_SIGNATURE_PARAM_CONVERTO_INTO_SAML_CONFIG.equals(key)) {
										samlConfig.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_CRYPTO_PROP_KEYSTORE_TYPE, type);
									}
									else {
										samlConfig.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_CRYPTO_PROPERTIES_KEYSTORE_TYPE, type);
									}
								}
								String file = signaturePropRefiId.getProperty("org.apache.ws.security.crypto.merlin.file");
								if(file==null) {
									throw new Exception("Keystore file in signaturePropRefId undefined");
								}
								if(SecurityConstants.SAML_SIGNATURE_PARAM_CONVERTO_INTO_SAML_CONFIG.equals(key)) {
									samlConfig.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_CRYPTO_PROP_KEYSTORE_FILE, file);
								}
								else {
									samlConfig.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_CRYPTO_PROPERTIES_KEYSTORE_FILE, file);
								}
								String password = signaturePropRefiId.getProperty("org.apache.ws.security.crypto.merlin.keystore.password");
								if(password==null) {
									throw new Exception("Keystore password in signaturePropRefId undefined");
								}
								if(SecurityConstants.SAML_SIGNATURE_PARAM_CONVERTO_INTO_SAML_CONFIG.equals(key)) {
									samlConfig.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_CRYPTO_PROP_KEYSTORE_PASSWORD, password);
								}
								else {
									samlConfig.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_CRYPTO_PROPERTIES_KEYSTORE_PASSWORD, password);
								}
								
								if(signatureAlias==null) {
									throw new Exception("Signature alias undefined");
								}
								if(SecurityConstants.SAML_SIGNATURE_PARAM_CONVERTO_INTO_SAML_CONFIG.equals(key)) {
									samlConfig.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_KEY_NAME, signatureAlias);
								}
								else {
									samlConfig.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_CRYPTO_ALIAS, signatureAlias);
								}
								
								if(SecurityConstants.SAML_SIGNATURE_PARAM_CONVERTO_INTO_SAML_CONFIG.equals(key)) {
									if(signaturePassword==null) {
										throw new Exception("Signature password undefined");
									}
									samlConfig.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_KEY_PASSWORD, signaturePassword);
									
									if(signatureAlgorithm!=null) {
										samlConfig.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_SIGNATURE_ALGORITHM, signatureAlgorithm);
									}
									if(signatureDigestAlgorithm!=null) {
										samlConfig.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_SIGNATURE_DIGEST_ALGORITHM, signatureDigestAlgorithm);
									}
									if(signatureC14nAlgorithmExclusive!=null) {
										samlConfig.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_SIGNATURE_CANONICALIZATION_ALGORITHM, signatureC14nAlgorithmExclusive);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	public static Properties convertToMerlinProperties(String keystoreType,String keystoreFile, String keystorePassword, boolean useKeystoreCache) throws Exception {
		Properties p = new Properties();
		if(useKeystoreCache) {
			p.put(KeystoreConstants.PROPERTY_PROVIDER, KeystoreConstants.PROVIDER_GOVWAY);
		}
		else {
			p.put(KeystoreConstants.PROPERTY_PROVIDER, KeystoreConstants.OLD_PROVIDER_DEFAULT);
		}
		if(keystoreType!=null) {
			p.put(KeystoreConstants.PROPERTY_KEYSTORE_TYPE, keystoreType);
		}
		if(keystoreFile==null) {
			throw new Exception("Keystore file undefined");
		}
		p.put(KeystoreConstants.PROPERTY_KEYSTORE_PATH, keystoreFile);
		if(keystorePassword==null) {
			throw new Exception("Keystore file undefined");
		}
		p.put(KeystoreConstants.PROPERTY_KEYSTORE_PASSWORD, keystorePassword);
		return p;
	}
}
