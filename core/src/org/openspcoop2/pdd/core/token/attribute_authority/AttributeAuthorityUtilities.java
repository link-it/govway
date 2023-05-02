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


package org.openspcoop2.pdd.core.token.attribute_authority;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.cxf.rt.security.rs.RSSecurityConstants;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.pdd.core.token.TokenException;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.utils.certificate.KeystoreType;

/**     
 * AttributeAuthorityUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttributeAuthorityUtilities {
	
	private AttributeAuthorityUtilities() {}
	
	public static KeystoreParams getRequestJwsKeystoreParams(GenericProperties gp) throws TokenException {
		PolicyAttributeAuthority policy = AttributeAuthorityUtilities.convertTo(gp);
		return getRequestJwsKeystoreParams(policy);
	}
	public static KeystoreParams getRequestJwsKeystoreParams(PolicyAttributeAuthority policy) throws TokenException {
	
		if(!policy.isRequestJws()) {
			throw new TokenException("La configurazione nell'AttributeAuthority "+policy.getName()+" non definisce il tipo di richiesta come JWS");
		}
		
		String keystoreType = policy.getRequestJwtSignKeystoreType();
		if(keystoreType==null) {
			throw new TokenException("JWS Signature keystore type undefined");
		}
		String keystoreFile = policy.getRequestJwtSignKeystoreFile();
		if(keystoreFile==null) {
			throw new TokenException("JWS Signature keystore file undefined");
		}
		String keystorePassword = policy.getRequestJwtSignKeystorePassword();
		if(keystorePassword==null && 
				!SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreType) && 
				!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType) && 
				!SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
			throw new TokenException("JWS Signature keystore password undefined");
		}
		String keyAlias = policy.getRequestJwtSignKeyAlias();
		if(keyAlias==null && 
				!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType) && 
				!SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
			throw new TokenException("JWS Signature key alias undefined");
		}
		String keyPassword = policy.getRequestJwtSignKeyPassword();
		if(keyPassword==null && 
				!SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreType) && 
				!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType) && 
				!SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
			throw new TokenException("JWS Signature key password undefined");
		}
		
		KeystoreParams keystoreParams = new KeystoreParams();
		keystoreParams.setPath(keystoreFile);
		keystoreParams.setType(keystoreType);
		keystoreParams.setPassword(keystorePassword);
		keystoreParams.setKeyAlias(keyAlias);
		keystoreParams.setKeyPassword(keyPassword);
		
		fillKeyPairParamters(keystoreParams, keystoreType, policy);
		
		return keystoreParams;
		
	}
	
	private static void fillKeyPairParamters(KeystoreParams keystoreParams, String keystoreType, PolicyAttributeAuthority policy) throws TokenException {
		if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType)) {
			String keystorePublicKeyFile = policy.getRequestJwtSignKeystoreFilePublicKey();
			if(keystorePublicKeyFile==null) {
				throw new TokenException("JWT Signature public key file undefined");
			}
			keystoreParams.setKeyPairPublicKeyPath(keystorePublicKeyFile);
		}
		
		if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType)
				||
			SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
			String keyPairAlgorithm = policy.getRequestJwtSignKeystoreFileAlgorithm();
			if(keyPairAlgorithm==null) {
				throw new TokenException("JWT Signature key pair algorithm undefined");
			}
			keystoreParams.setKeyPairAlgorithm(keyPairAlgorithm);
		}
	}
	
	public static KeystoreParams getResponseJwsKeystoreParams(GenericProperties gp) throws TokenException {
		PolicyAttributeAuthority policy = AttributeAuthorityUtilities.convertTo(gp);
		return getResponseJwsKeystoreParams(policy);
	}
	public static KeystoreParams getResponseJwsKeystoreParams(PolicyAttributeAuthority policy) throws TokenException {
	
		if(!policy.isResponseJws()) {
			throw new TokenException("La configurazione nell'AttributeAuthority "+policy.getName()+" non definisce il tipo di risposta come JWS");
		}
		Properties p = policy.getProperties().get(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID);
		if(p!=null && p.containsKey(RSSecurityConstants.RSSEC_KEY_STORE_FILE)) {
			KeystoreParams keystoreParams = new KeystoreParams();
			keystoreParams.setPath(p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_FILE));
			String type = p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if(type==null) {
				type = KeystoreType.JKS.getNome();
			}
			keystoreParams.setType(type);
			keystoreParams.setPassword(p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_PSWD));
			keystoreParams.setKeyAlias(p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS));
			keystoreParams.setKeyPassword(p.getProperty(RSSecurityConstants.RSSEC_KEY_PSWD));
			
			fillKeyPairParamters(keystoreParams, type, p);
			
			return keystoreParams;
		}
		return null;
		
	}
	private static void fillKeyPairParamters(KeystoreParams keystoreParams, String type, Properties p) throws TokenException {
		if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(type)) {
			String keystorePublicKeyFile = p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_FILE+".public");
			if(keystorePublicKeyFile==null) {
				throw new TokenException("Public key file undefined");
			}
			keystoreParams.setKeyPairPublicKeyPath(keystorePublicKeyFile);
		}
		
		if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(type)
				||
			SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(type)) {
			String keyPairAlgorithm = p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_FILE+".algorithm");
			if(keyPairAlgorithm==null) {
				throw new TokenException("Key pair algorithm undefined");
			}
			keystoreParams.setKeyPairAlgorithm(keyPairAlgorithm);
		}
	}
	
	public static PolicyAttributeAuthority convertTo(GenericProperties gp) throws TokenException { 
		
		PolicyAttributeAuthority policy = new PolicyAttributeAuthority();
		policy.setName(gp.getNome());
		policy.setDescrizione(gp.getDescrizione());
		
		HashMap<String, String> properties = new HashMap<>();
		for (Property pConfig : gp.getPropertyList()) {
			properties.put(pConfig.getNome(), pConfig.getValore());
		}
		try {
			Map<String, Properties> multiProperties = DBPropertiesUtils.toMultiMap(properties);
			policy.setProperties(multiProperties);
		}catch(Exception e) {
			throw new TokenException(e.getMessage(),e);
		}
		
		return policy;

	}
}
