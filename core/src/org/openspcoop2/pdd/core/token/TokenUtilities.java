/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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


package org.openspcoop2.pdd.core.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rt.security.rs.RSSecurityConstants;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;
import org.openspcoop2.utils.certificate.KeystoreParams;

/**     
 * TokenUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TokenUtilities {

	public static Properties getDefaultProperties(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return MultiPropertiesUtilities.getDefaultProperties(mapProperties);
	}
	public static Properties getValidazioneJwtClaimsMappingProperties(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return mapProperties.get(Costanti.VALIDAZIONE_JWT_TOKEN_PARSER_COLLECTION_ID);
	}
	public static Properties getIntrospectionClaimsMappingProperties(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return mapProperties.get(Costanti.INTROSPECTION_TOKEN_PARSER_COLLECTION_ID);
	}
	public static Properties getUserInfoClaimsMappingProperties(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return mapProperties.get(Costanti.USERINFO_TOKEN_PARSER_COLLECTION_ID);
	}
	
	public static List<String> getClaims(Properties p, String name) {
		String v = p.getProperty(name);
		List<String> l = new ArrayList<String>();
		if(v.contains(",")) {
			String [] tmp = v.split(",");
			for (String s : tmp) {
				if(StringUtils.isNotEmpty(s.trim())) {
					l.add(s.trim());
				}
			}
		}
		else {
			l.add(v.trim());
		}
		return l;
	}
	
	public static Map<String, Properties> getMultiProperties(GenericProperties gp) throws ProviderException, ProviderValidationException {
		Map<String, Properties> multiProperties = null;
		try {
			HashMap<String, String> properties = new HashMap<>();
			for (Property pConfig : gp.getPropertyList()) {
				properties.put(pConfig.getNome(), pConfig.getValore());
			}
			multiProperties = DBPropertiesUtils.toMultiMap(properties);
		}catch(Exception e) {
			throw new ProviderException(e.getMessage(),e);
		}
		return multiProperties;
	}
	
	public static boolean isValidazioneEnabled(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return isValidazioneEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isValidazioneEnabled(Properties pDefault) throws ProviderException, ProviderValidationException {
		return isEnabled(pDefault, Costanti.POLICY_VALIDAZIONE_STATO);
	}
	public static boolean isValidazioneEnabled(GenericProperties gp) throws ProviderException, ProviderValidationException {
		Map<String, Properties> multiProperties = getMultiProperties(gp);
		return isValidazioneEnabled(multiProperties);
	}
		
	public static boolean isIntrospectionEnabled(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return isIntrospectionEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isIntrospectionEnabled(Properties pDefault) throws ProviderException, ProviderValidationException {
		return isEnabled(pDefault, Costanti.POLICY_INTROSPECTION_STATO);
	}
	public static boolean isIntrospectionEnabled(GenericProperties gp) throws ProviderException, ProviderValidationException {
		Map<String, Properties> multiProperties = getMultiProperties(gp);
		return isIntrospectionEnabled(multiProperties);
	}
	
	public static boolean isUserInfoEnabled(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return isUserInfoEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isUserInfoEnabled(Properties pDefault) throws ProviderException, ProviderValidationException {
		return isEnabled(pDefault, Costanti.POLICY_USER_INFO_STATO);
	}
	public static boolean isUserInfoEnabled(GenericProperties gp) throws ProviderException, ProviderValidationException {
		Map<String, Properties> multiProperties = getMultiProperties(gp);
		return isUserInfoEnabled(multiProperties);
	}
	
	public static boolean isTokenForwardEnabled(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return isTokenForwardEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isTokenForwardEnabled(Properties pDefault) throws ProviderException, ProviderValidationException {
		return isEnabled(pDefault, Costanti.POLICY_TOKEN_FORWARD_STATO);
	}
	public static boolean isTokenForwardEnabled(GenericProperties gp) throws ProviderException, ProviderValidationException {
		Map<String, Properties> multiProperties = getMultiProperties(gp);
		return isTokenForwardEnabled(multiProperties);
	}
	
	public static boolean isEnabled(Properties p, String propertyName) throws ProviderException, ProviderValidationException {
		return MultiPropertiesUtilities.isEnabled(p, propertyName);
	}
	
	
	public static KeystoreParams getValidazioneJwtKeystoreParams(GenericProperties gp) throws Exception {
		PolicyGestioneToken policy = TokenUtilities.convertTo(gp, new GestioneToken());
		if(!TokenUtilities.isValidazioneEnabled(gp)) {
			throw new Exception("La configurazione nella policy "+gp.getNome()+" non utilizza la funzionalità di validazione JWT");
		}
		return getValidazioneJwtKeystoreParams(policy);
	}
	public static KeystoreParams getValidazioneJwtKeystoreParams(PolicyGestioneToken policy) throws Exception {
		String tokenType = policy.getTipoToken();
		Properties p = null;
		if(org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_TYPE_JWS.equals(tokenType)) {
			// JWS Compact   			
			p = policy.getProperties().get(org.openspcoop2.pdd.core.token.Costanti.POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID);
		}
		else if(org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_TYPE_JWE.equals(tokenType)) {
			// JWE Compact
			p = policy.getProperties().get(org.openspcoop2.pdd.core.token.Costanti.POLICY_VALIDAZIONE_JWE_DECRYPT_PROP_REF_ID);
		}
		if(p!=null && p.containsKey(RSSecurityConstants.RSSEC_KEY_STORE_FILE)) {
			KeystoreParams keystoreParams = new KeystoreParams();
			keystoreParams.setPath(p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_FILE));
			String type = p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if(type==null) {
				type = "jks";
			}
			keystoreParams.setType(type);
			keystoreParams.setPassword(p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_PSWD));
			keystoreParams.setKeyAlias(p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS));
			keystoreParams.setKeyPassword(p.getProperty(RSSecurityConstants.RSSEC_KEY_PSWD));
			return keystoreParams;
		}
		return null;
	}
	
	public static KeystoreParams getForwardToJwtKeystoreParams(GenericProperties gp) throws Exception {
		PolicyGestioneToken policy = TokenUtilities.convertTo(gp, new GestioneToken());
		if(!TokenUtilities.isTokenForwardEnabled(gp) || !policy.isForwardToken_informazioniRaccolte()) {
			throw new DriverConfigurazioneException("La configurazione nella policy "+gp.getNome()+" non utilizza la funzionalità di forward delle informazioni raccolte del token");
		}
		return getForwardToJwtKeystoreParams(policy);
	}
	public static KeystoreParams getForwardToJwtKeystoreParams(PolicyGestioneToken policy) throws Exception {
		String forwardInformazioniRaccolteMode = policy.getForwardToken_informazioniRaccolteMode();
		Properties p = null;
		if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS.equals(forwardInformazioniRaccolteMode) ||
				Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(forwardInformazioniRaccolteMode)) {
			// JWS Compact   			
			p = policy.getProperties().get(org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_SIGNATURE_PROP_REF_ID);
		}
		else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(forwardInformazioniRaccolteMode)) {
			// JWE Compact
			p = policy.getProperties().get(org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_ENCRYP_PROP_REF_ID);
		}
		if(p!=null && p.containsKey(RSSecurityConstants.RSSEC_KEY_STORE_FILE)) {
			KeystoreParams keystoreParams = new KeystoreParams();
			keystoreParams.setPath(p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_FILE));
			String type = p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_TYPE);
			if(type==null) {
				type = "jks";
			}
			keystoreParams.setType(type);
			keystoreParams.setPassword(p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_PSWD));
			keystoreParams.setKeyAlias(p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS));
			keystoreParams.setKeyPassword(p.getProperty(RSSecurityConstants.RSSEC_KEY_PSWD));
			return keystoreParams;
		}
		return null;
	}
	
	public static PolicyGestioneToken convertTo(GenericProperties gp, GestioneToken gestioneToken) throws Exception { 
	
		PolicyGestioneToken policy = new PolicyGestioneToken();
		policy.setName(gp.getNome());
		policy.setDescrizione(gp.getDescrizione());
		
		Map<String, Properties> multiProperties = TokenUtilities.getMultiProperties(gp);
		policy.setProperties(multiProperties);
		
		policy.setTokenOpzionale(false);
		
		policy.setValidazioneJWT(false);
		policy.setValidazioneJWT_warningOnly(false);
		
		policy.setIntrospection(false);
		policy.setIntrospection_warningOnly(false);
		
		policy.setUserInfo(false);
		policy.setUserInfo_warningOnly(false);
		
		policy.setForwardToken(false);
		
		if(gestioneToken!=null) {
			
			if(gestioneToken.getTokenOpzionale()!=null) {
				switch (gestioneToken.getTokenOpzionale()) {
				case ABILITATO:
					policy.setTokenOpzionale(true);
					break;
				case DISABILITATO:
					policy.setTokenOpzionale(false);
					break;
				}
			}
			
			boolean validazioneEnabledDaPolicy = isValidazioneEnabled(multiProperties);
			if(validazioneEnabledDaPolicy) {
				if(gestioneToken.getValidazione()!=null) {
					switch (gestioneToken.getValidazione()) {
					case ABILITATO:
						policy.setValidazioneJWT(true);
						policy.setValidazioneJWT_warningOnly(false);
						break;
					case WARNING_ONLY:
						policy.setValidazioneJWT(true);
						policy.setValidazioneJWT_warningOnly(true);
						break;
					case DISABILITATO:
						policy.setValidazioneJWT(false);
						policy.setValidazioneJWT_warningOnly(false);
						break;
					}
				}
			}
			
			boolean introspectionEnabledDaPolicy = isIntrospectionEnabled(multiProperties);
			if(introspectionEnabledDaPolicy) {
				if(gestioneToken.getIntrospection()!=null) {
					switch (gestioneToken.getIntrospection()) {
					case ABILITATO:
						policy.setIntrospection(true);
						policy.setIntrospection_warningOnly(false);
						break;
					case WARNING_ONLY:
						policy.setIntrospection(true);
						policy.setIntrospection_warningOnly(true);
						break;
					case DISABILITATO:
						policy.setIntrospection(false);
						policy.setIntrospection_warningOnly(false);
						break;
					}
				}
			}
			
			boolean userInfoEnabledDaPolicy = isUserInfoEnabled(multiProperties);
			if(userInfoEnabledDaPolicy) {
				if(gestioneToken.getUserInfo()!=null) {
					switch (gestioneToken.getUserInfo()) {
					case ABILITATO:
						policy.setUserInfo(true);
						policy.setUserInfo_warningOnly(false);
						break;
					case WARNING_ONLY:
						policy.setUserInfo(true);
						policy.setUserInfo_warningOnly(true);
						break;
					case DISABILITATO:
						policy.setUserInfo(false);
						policy.setUserInfo_warningOnly(false);
						break;
					}
				}
			}
			
			boolean forwardEnabledDaPolicy = isTokenForwardEnabled(multiProperties);
			if(forwardEnabledDaPolicy) {
				if(gestioneToken.getForward()!=null) {
					switch (gestioneToken.getForward()) {
					case ABILITATO:
						policy.setForwardToken(true);
						break;
					case DISABILITATO:
						policy.setForwardToken(false);
						break;
					}
				}
			}
			
		}
		
		return policy;

	}
	
	
	public static List<String> getClaimValues(Object value) {
		if(value!=null) {
			if(value instanceof List<?>) {
				List<?> l = (List<?>) value;
				if(!l.isEmpty()) {
					List<String> lString = new ArrayList<>();
					for (Object o : l) {
						if(o!=null) {
							lString.add(o.toString());
						}
					}
					if(!lString.isEmpty()) {
						return lString;
					}
				}
			}
			else {
				String sValue = value.toString();
				List<String> l = new ArrayList<>();
				l.add(sValue);
				return l;
			}
		}
		return null;
	}
	
	public static String getClaimValuesAsString(List<String> claimValues) {
		String claimValue = null;
		if(claimValues==null || claimValues.isEmpty()) {
			return null;
		}
		if(claimValues.size()>1) {
			for (String c : claimValues) {
				if(claimValue!=null) {
					claimValue = claimValue +","+c;
				}
				else {
					claimValue = c;
				}
			}
		}
		else {
			claimValue = claimValues.get(0);
		}
		return claimValue;
	}
	
	public static String getClaimAsString(Map<String, Object> claims, String claim) {
		List<String> l = getClaimAsList(claims, claim);
		if(l==null || l.isEmpty()) {
			return null;
		}
		String claimValue = TokenUtilities.getClaimValuesAsString(l);
		return claimValue;
	}
	public static List<String> getClaimAsList(Map<String, Object> claims, String claim) {
		Object o = claims.get(claim);
		if(o==null) {
			return null;
		}
		List<String> l = TokenUtilities.getClaimValues(o);
		if(l==null || l.isEmpty()) {
			return null;
		}
		return l;
	}
	
	public static String getFirstClaimAsString(Map<String, Object> claims, List<String> names) {
		for (String name : names) {
			String claim = getClaimAsString(claims, name);
			if(claim!=null) {
				return claim;
			}
		}
		return null;
	}
	public static List<String> getFirstClaimAsList(Map<String, Object> claims, List<String> names) {
		for (String name : names) {
			List<String> l = getClaimAsList(claims, name);
			if(l!=null && !l.isEmpty()) {
				return l;
			}
		}
		return null;
	}
	
	public static KeystoreParams getSignedJwtKeystoreParams(GenericProperties gp) throws Exception {
		PolicyNegoziazioneToken policy = TokenUtilities.convertTo(gp);
		return getSignedJwtKeystoreParams(policy);
	}
	public static KeystoreParams getSignedJwtKeystoreParams(PolicyNegoziazioneToken policy) throws Exception {
	
		if(!policy.isRfc7523_x509_Grant()) {
			throw new DriverConfigurazioneException("La configurazione nella policy "+policy.getName()+" non utilizza la funzionalità SignedJWT (RFC 7523)");
		}
		
		String keystoreType = policy.getJwtSignKeystoreType();
		if(keystoreType==null) {
			throw new Exception("JWT Signature keystore type undefined");
		}
		String keystoreFile = policy.getJwtSignKeystoreFile();
		if(keystoreFile==null) {
			throw new Exception("JWT Signature keystore file undefined");
		}
		String keystorePassword = policy.getJwtSignKeystorePassword();
		if(keystorePassword==null && !"jwk".equalsIgnoreCase(keystoreType)) {
			throw new Exception("JWT Signature keystore password undefined");
		}
		String keyAlias = policy.getJwtSignKeyAlias();
		if(keyAlias==null) {
			throw new Exception("JWT Signature key alias undefined");
		}
		String keyPassword = policy.getJwtSignKeyPassword();
		if(keyPassword==null) {
			throw new Exception("JWT Signature key password undefined");
		}
		
		KeystoreParams keystoreParams = new KeystoreParams();
		keystoreParams.setPath(keystoreFile);
		keystoreParams.setType(keystoreType);
		keystoreParams.setPassword(keystorePassword);
		keystoreParams.setKeyAlias(keyAlias);
		keystoreParams.setKeyPassword(keyPassword);
		return keystoreParams;
		
	}
	
	public static PolicyNegoziazioneToken convertTo(GenericProperties gp) throws Exception { 
		
		PolicyNegoziazioneToken policy = new PolicyNegoziazioneToken();
		policy.setName(gp.getNome());
		policy.setDescrizione(gp.getDescrizione());
		
		HashMap<String, String> properties = new HashMap<>();
		for (Property pConfig : gp.getPropertyList()) {
			properties.put(pConfig.getNome(), pConfig.getValore());
		}
		Map<String, Properties> multiProperties = DBPropertiesUtils.toMultiMap(properties);
		policy.setProperties(multiProperties);
		
		return policy;

	}
	
	public static void checkClaims(String oggetto, Properties claims, String elemento, List<String> denyClaims, boolean checkSpazi) throws ProviderValidationException {
		if(claims!=null && !claims.isEmpty()) {
			for (Object oClaim : claims.keySet()) {
				if(oClaim !=null && oClaim instanceof String) {
					String claim = (String) oClaim;
					String value = claims.getProperty(claim);
					
					if(denyClaims.contains(claim) || denyClaims.contains(claim.toLowerCase())) {
						throw new ProviderValidationException("Il "+oggetto+" '"+claim+"', indicato nel campo '"+elemento+"', non può essere configurato");
					}
					if(value==null || StringUtils.isEmpty(value)) {
						throw new ProviderValidationException("Il "+oggetto+" '"+claim+"', indicato nel campo '"+elemento+"', non è valorizzato");
					}
					if(checkSpazi) {
						if(value.contains(" ")) {
							throw new ProviderValidationException("Non indicare spazi nel "+oggetto+" '"+claim+"', indicato nel campo '"+elemento+"'");
						}
					}
					else {
						if(value.startsWith(" ")) {
							throw new ProviderValidationException("Il valore del "+oggetto+" '"+claim+"', indicato nel campo '"+elemento+"', non deve iniziare con uno spazio");
						}
						if(value.endsWith(" ")) {
							throw new ProviderValidationException("Il valore del "+oggetto+" '"+claim+"', indicato nel campo '"+elemento+"', non deve terminare con uno spazio");
						}
					}
				}
			}
		}
	}
	
	public static String deleteSignature(String token) {
		// verifico che sia un JWT
		if(token.contains(".")) {
			String [] tmp = token.split("\\.");
			if(tmp!=null && tmp.length==3) {
				if(tmp[2]!=null) {
					if(tmp[0]!=null && tmp[1]!=null) {
						return tmp[0]+"."+tmp[1]+".==SIGNATURE==";
					}
					else if(tmp[0]==null && tmp[1]!=null) {
						return "."+tmp[1]+".==SIGNATURE==";
					}
					else if(tmp[0]!=null && tmp[1]==null) {
						return tmp[0]+"..==SIGNATURE==";
					}
					else {
						return "..==SIGNATURE==";
					}
				}
			}
		}
		return token;
	}
	public static void replaceTokenInMap(Map<String, Object> claims, String originale, String newToken) {
		if(claims!=null && !claims.isEmpty()) {
			List<String> keyDaSostituire = new ArrayList<String>();
			for (String key : claims.keySet()) {
				Object o = claims.get(key);
				if(o !=null && o instanceof String && originale.equals(o)) {
					keyDaSostituire.add(key);
				}
			}
			while(keyDaSostituire.size()>0) {
				String key = keyDaSostituire.remove(0);
				claims.remove(key);
				claims.put(key, newToken);
			}
		}
	}
}
