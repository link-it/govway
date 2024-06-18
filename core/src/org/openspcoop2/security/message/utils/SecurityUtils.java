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
package org.openspcoop2.security.message.utils;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.constants.CostantiProprieta;
import org.openspcoop2.security.keystore.MerlinProvider;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.certificate.KeystoreParams;

/**     
 * SecurityUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityUtils {
	
	private SecurityUtils() {}

	public static List<KeystoreParams> readRequestKeystoreParams(PortaApplicativa porta) {
		List<KeystoreParams> listKP = new ArrayList<>();
		if(porta!=null && porta.getMessageSecurity()!=null && porta.getMessageSecurity().getRequestFlow()!=null &&
				porta.getMessageSecurity().getRequestFlow().sizeParameterList()>0) {
			readStoreParams(listKP, porta.getMessageSecurity().getRequestFlow().getParameterList());
		}
		return listKP;
	}
	
	public static List<KeystoreParams> readResponseKeystoreParams(PortaApplicativa porta) {
		List<KeystoreParams> listKP = new ArrayList<>();
		if(porta!=null && porta.getMessageSecurity()!=null && porta.getMessageSecurity().getResponseFlow()!=null &&
				porta.getMessageSecurity().getResponseFlow().sizeParameterList()>0) {
			readStoreParams(listKP, porta.getMessageSecurity().getResponseFlow().getParameterList());
		}
		return listKP;
	}
	
	public static List<KeystoreParams> readRequestKeystoreParams(PortaDelegata porta) {
		List<KeystoreParams> listKP = new ArrayList<>();
		if(porta!=null && porta.getMessageSecurity()!=null && porta.getMessageSecurity().getRequestFlow()!=null &&
				porta.getMessageSecurity().getRequestFlow().sizeParameterList()>0) {
			readStoreParams(listKP, porta.getMessageSecurity().getRequestFlow().getParameterList());
		}
		return listKP;
	}
	
	public static List<KeystoreParams> readResponseKeystoreParams(PortaDelegata porta) {
		List<KeystoreParams> listKP = new ArrayList<>();
		if(porta!=null && porta.getMessageSecurity()!=null && porta.getMessageSecurity().getResponseFlow()!=null &&
				porta.getMessageSecurity().getResponseFlow().sizeParameterList()>0) {
			readStoreParams(listKP, porta.getMessageSecurity().getResponseFlow().getParameterList());
		}
		return listKP;
	}
	
	private static void readStoreParams(List<KeystoreParams> listKP, List<MessageSecurityFlowParameter> list) {
		
		readKestoreParamsJose(listKP, list);
		readTruststoreParamsJoseTls(listKP, list);
		readTruststoreParamsJoseUseHeaders(listKP, list);
		readKeystoreParamsJoseUseHeaders(listKP, list);
		
		readKeystoreParamsMerlin(listKP, list);
		
	}
	
	
	// JOSE
	
	private static void readKestoreParamsJose(List<KeystoreParams> listKP, List<MessageSecurityFlowParameter> list) {
		String pathJose = readProperty(list, SecurityConstants.JOSE_KEYSTORE_FILE);
		if(pathJose!=null && StringUtils.isNotEmpty(pathJose)) {
			// jose
			KeystoreParams kp = new KeystoreParams();
			kp.setPath(pathJose);
			kp.setType(readProperty(list, SecurityConstants.JOSE_KEYSTORE_TYPE));
			kp.setPassword(readProperty(list, SecurityConstants.JOSE_KEYSTORE_PSWD));
			
			kp.setCrls(readProperty(list, SecurityConstants.JOSE_KEYSTORE_CRL));
			kp.setOcspPolicy(readProperty(list, SecurityConstants.JOSE_KEYSTORE_OCSP_POLICY));
			
			kp.setByokPolicy(readProperty(list, SecurityConstants.JOSE_KEYSTORE_BYOK_POLICY));
			
			kp.setKeyAlias(readProperty(list, SecurityConstants.JOSE_KEYSTORE_KEY_ALIAS));
			kp.setKeyPassword(readProperty(list, SecurityConstants.JOSE_KEYSTORE_KEY_PSWD));
			
			kp.setKeyPairPublicKeyPath(readProperty(list, SecurityConstants.JOSE_KEYSTORE_PUBLIC_KEY));
			kp.setKeyPairAlgorithm(readProperty(list, SecurityConstants.JOSE_KEYSTORE_KEY_ALGORITHM));
			
			listKP.add(kp);
		}
	}
	
	private static void readTruststoreParamsJoseTls(List<KeystoreParams> listKP, List<MessageSecurityFlowParameter> list) {
		String pathJose = readProperty(list, SecurityConstants.JOSE_TRUSTSTORE_SSL_FILE);
		if(pathJose!=null && StringUtils.isNotEmpty(pathJose)) {
			// jose
			KeystoreParams kp = new KeystoreParams();
			kp.setPath(pathJose);
			kp.setType(readProperty(list, SecurityConstants.JOSE_TRUSTSTORE_SSL_TYPE));
			kp.setPassword(readProperty(list, SecurityConstants.JOSE_TRUSTSTORE_SSL_PSWD));
			
			kp.setCrls(readProperty(list, SecurityConstants.JOSE_TRUSTSTORE_SSL_CRL));
			kp.setOcspPolicy(readProperty(list, SecurityConstants.JOSE_TRUSTSTORE_SSL_OCSP));
			
			kp.setDescription("TLS TrustStore");
			
			listKP.add(kp);
		}
	}
	
	private static void readTruststoreParamsJoseUseHeaders(List<KeystoreParams> listKP, List<MessageSecurityFlowParameter> list) {
		String pathJose = readProperty(list, SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_FILE);
		if(pathJose!=null && StringUtils.isNotEmpty(pathJose)) {
			// jose
			KeystoreParams kp = new KeystoreParams();
			kp.setPath(pathJose);
			kp.setType(readProperty(list, SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_TYPE));
			kp.setPassword(readProperty(list, SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_PASSWORD));
			
			kp.setCrls(readProperty(list, SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_CRL));
			kp.setOcspPolicy(readProperty(list, SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_OCSP));
			
			kp.setKeyPairAlgorithm(readProperty(list, SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_KEY_PAIR_ALGORITHM));
			
			kp.setDescription("TrustStore Certificati X.509");
			
			listKP.add(kp);
		}    
	}
	
	private static void readKeystoreParamsJoseUseHeaders(List<KeystoreParams> listKP, List<MessageSecurityFlowParameter> list) {
		String pathJose = readProperty(list, SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_FILE);
		if(pathJose!=null && StringUtils.isNotEmpty(pathJose)) {
			
			for (int i = 1; i < 11; i++) {
			
				String user = readProperty(list, SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PW+i+SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PW_SUFFIX_ALIAS);
				if(user!=null) {
					// jose
					KeystoreParams kp = new KeystoreParams();
					kp.setPath(pathJose);
					kp.setType(readProperty(list, SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_TYPE));
					kp.setPassword(readProperty(list, SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_PASSWORD));
					
					kp.setKeyAlias(user);
					kp.setKeyPassword(readProperty(list, SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PW+i+SecurityConstants.JOSE_USE_HEADERS_KEYSTORE_MAP_ALIAS_PW_SUFFIX_PW));
					
					kp.setDescription("KeyStore Certificati X.509");
					
					listKP.add(kp);
				}
				
			}
			
		}    
	}
	
	
	// MERLIN
	
	private static void readKeystoreParamsMerlin(List<KeystoreParams> listKP, List<MessageSecurityFlowParameter> list) {
		
		String prefix = readPrefix(list);
		if(prefix==null || StringUtils.isEmpty(prefix)) {
			return;
		}
		
		String pathKeystore = readProperty(list, prefix+org.apache.wss4j.common.crypto.Merlin.KEYSTORE_FILE);
		if(pathKeystore==null || StringUtils.isEmpty(pathKeystore)) {
			pathKeystore = readProperty(list, prefix+org.apache.wss4j.common.crypto.Merlin.OLD_KEYSTORE_FILE);
		}
		
		if(pathKeystore!=null && StringUtils.isNotEmpty(pathKeystore)) {
			readKeystoreParamsMerlin(listKP, list, prefix, pathKeystore);
		}    
	}
	private static void readKeystoreParamsMerlin(List<KeystoreParams> listKP, List<MessageSecurityFlowParameter> list, String prefix, String pathKeystore) {
		KeystoreParams kp = new KeystoreParams();
		kp.setPath(pathKeystore);
		
		String keyStoreType = readProperty(list, prefix+org.apache.wss4j.common.crypto.Merlin.KEYSTORE_TYPE);
		if (keyStoreType == null) {
			keyStoreType = KeyStore.getDefaultType();
		}
		kp.setType(keyStoreType);
		kp.setPassword(readProperty(list, prefix+org.apache.wss4j.common.crypto.Merlin.KEYSTORE_PASSWORD));
		
		String crlLocations = readProperty(list, prefix+org.apache.wss4j.common.crypto.Merlin.X509_CRL_FILE);
		if (crlLocations == null || StringUtils.isEmpty(crlLocations)) {
			crlLocations = readProperty(list, SecurityConstants.SIGNATURE_CRL);
		}
		kp.setCrls(crlLocations);
		kp.setOcspPolicy(readProperty(list, SecurityConstants.SIGNATURE_OCSP));
		
		String keyStoreByokPolicy = readProperty(list, prefix+org.apache.wss4j.common.crypto.Merlin.KEYSTORE_FILE+MerlinProvider.SUFFIX_BYOK);
		if (keyStoreByokPolicy == null || StringUtils.isEmpty(keyStoreByokPolicy)) {
			keyStoreByokPolicy = readProperty(list, prefix+org.apache.wss4j.common.crypto.Merlin.OLD_KEYSTORE_FILE+MerlinProvider.SUFFIX_BYOK);
		}
		kp.setByokPolicy(keyStoreByokPolicy);
		
		listKP.add(kp);
		
		readKeystoreAliasParamsMerlin(list, kp);
	}
	private static void readKeystoreAliasParamsMerlin(List<MessageSecurityFlowParameter> list, KeystoreParams kp) {
		if(readKeystoreSignatureAliasParamsMerlin(list,  kp)) {
			return;
		}
		if(readKeystoreEncryptAliasParamsMerlin(list, kp)) {
			return;
		}
		if(readKeystoreUserAliasParamsMerlin(list, kp)) {
			/**return;*/
		}
	}
	private static boolean readKeystoreSignatureAliasParamsMerlin(List<MessageSecurityFlowParameter> list, KeystoreParams kp) {
		String alias = readProperty(list, SecurityConstants.SIGNATURE_USER);
		if (alias != null && StringUtils.isNotEmpty(alias)) {
			kp.setKeyAlias(alias);
			kp.setKeyPassword(readProperty(list, SecurityConstants.SIGNATURE_PASSWORD));
			return true;
		}
		return false;
	}
	private static boolean readKeystoreEncryptAliasParamsMerlin(List<MessageSecurityFlowParameter> list, KeystoreParams kp) {	
		String alias = readProperty(list, SecurityConstants.ENCRYPTION_USER);
		if (alias != null && StringUtils.isNotEmpty(alias)) {
			kp.setKeyAlias(alias);
			String pwd = readProperty(list, SecurityConstants.ENCRYPTION_PASSWORD);
			if (pwd == null || StringUtils.isEmpty(pwd)) {
				pwd = readProperty(list, SecurityConstants.DECRYPTION_PASSWORD);
			}
			kp.setKeyPassword(pwd);
			return true;
		}
		
		alias = readProperty(list, SecurityConstants.DECRYPTION_USER);
		if (alias != null && StringUtils.isNotEmpty(alias)) {
			kp.setKeyAlias(alias);
			String pwd = readProperty(list, SecurityConstants.DECRYPTION_PASSWORD);
			if (pwd == null || StringUtils.isEmpty(pwd)) {
				pwd = readProperty(list, SecurityConstants.ENCRYPTION_PASSWORD);
			}
			kp.setKeyPassword(pwd);
			return true;
		}
		return false;
	}
	private static boolean readKeystoreUserAliasParamsMerlin(List<MessageSecurityFlowParameter> list, KeystoreParams kp) {	
		
		String alias = readProperty(list, SecurityConstants.USER);
		if (alias != null && StringUtils.isNotEmpty(alias)) {
			kp.setKeyAlias(alias);
			String pwd = readProperty(list, SecurityConstants.SIGNATURE_PASSWORD);
			if (pwd == null || StringUtils.isEmpty(pwd)) {
				pwd = readProperty(list, SecurityConstants.ENCRYPTION_PASSWORD);
			}
			if (pwd == null || StringUtils.isEmpty(pwd)) {
				pwd = readProperty(list, SecurityConstants.DECRYPTION_PASSWORD);
			}
			kp.setKeyPassword(pwd);
			return true;
		}
		return false;
	}
	
	private static String readPrefix(List<MessageSecurityFlowParameter> list) {
		if(list==null || list.isEmpty()) {
			return null;
		}
		String prefix = null;
		for (MessageSecurityFlowParameter p : list) {
			String propKey = p.getNome();
			if (startsWith(propKey,org.apache.wss4j.common.crypto.Merlin.PREFIX)) {
				prefix = org.apache.wss4j.common.crypto.Merlin.PREFIX;
				return prefix;
			} else if (startsWith(propKey,org.apache.wss4j.common.crypto.Merlin.OLD_PREFIX)) {
				prefix = org.apache.wss4j.common.crypto.Merlin.OLD_PREFIX;
				return prefix;
			}
		}
		return prefix;
	}
	private static boolean startsWith(String propKey, String check) {
		return propKey.startsWith(check)
				||
				(propKey.contains(CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR) && isStartsWithConfidentialPropertyCustomSeparator(propKey,check))
				||
				(propKey.contains(CostantiProprieta.KEY_PROPERTIES_DEFAULT_SEPARATOR) && isStartsWithConfidentialPropertyDefaultSeparator(propKey,check));
	}
	
	
	// UTILITY
	
	private static String readProperty(List<MessageSecurityFlowParameter> list, String name) {
		if(list!=null && !list.isEmpty()) {
			for (MessageSecurityFlowParameter messageSecurityFlowParameter : list) {
				if(isProperty(messageSecurityFlowParameter, name)) {
					if(messageSecurityFlowParameter.getValore()!=null && StringUtils.isNotEmpty(messageSecurityFlowParameter.getValore())) {
						return messageSecurityFlowParameter.getValore();
					}
					return null;
				}
			}
		}
		return null;
	}
	private static boolean isProperty(MessageSecurityFlowParameter messageSecurityFlowParameter, String name) {
		return (messageSecurityFlowParameter.getNome()!=null && 
				(
						messageSecurityFlowParameter.getNome().equals(name)
						||
						(messageSecurityFlowParameter.getNome().contains(CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR) && isConfidentialPropertyCustomSeparator(messageSecurityFlowParameter.getNome(),name))
						||
						(messageSecurityFlowParameter.getNome().contains(CostantiProprieta.KEY_PROPERTIES_DEFAULT_SEPARATOR) && isConfidentialPropertyDefaultSeparator(messageSecurityFlowParameter.getNome(),name))
				)
		);
	}	
	private static boolean isConfidentialPropertyCustomSeparator(String nome, String check) {
		String [] tmp = nome.split(CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR);
		return (tmp!=null && tmp.length>1 && tmp[1]!=null &&
			tmp[1].equals(check));
	}
	private static boolean isConfidentialPropertyDefaultSeparator(String nome, String check) {
		String [] tmp = nome.split(CostantiProprieta.KEY_PROPERTIES_DEFAULT_SEPARATOR);
		return (tmp!=null && tmp.length>1 && tmp[1]!=null &&
			tmp[1].equals(check)) ;
	}
	private static boolean isStartsWithConfidentialPropertyCustomSeparator(String nome, String check) {
		String [] tmp = nome.split(CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR);
		return (tmp!=null && tmp.length>1 && tmp[1]!=null &&
			tmp[1].startsWith(check));
	}
	private static boolean isStartsWithConfidentialPropertyDefaultSeparator(String nome, String check) {
		String [] tmp = nome.split(CostantiProprieta.KEY_PROPERTIES_DEFAULT_SEPARATOR);
		return (tmp!=null && tmp.length>1 && tmp[1]!=null &&
			tmp[1].startsWith(check)) ;
	}
}
