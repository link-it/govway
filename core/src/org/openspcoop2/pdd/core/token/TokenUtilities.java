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


package org.openspcoop2.pdd.core.token;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rt.security.rs.RSSecurityConstants;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.Search;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.mvc.properties.Item;
import org.openspcoop2.core.mvc.properties.constants.ItemType;
import org.openspcoop2.core.mvc.properties.provider.ExternalResources;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.plugins.utils.PluginsDriverUtils;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.utils.AbstractSecurityProvider;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.utils.certificate.KeystoreType;

/**     
 * TokenUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TokenUtilities {
	
	private TokenUtilities() {}

	public static Properties getDefaultProperties(Map<String, Properties> mapProperties) {
		return MultiPropertiesUtilities.getDefaultProperties(mapProperties);
	}
	public static Properties getValidazioneJwtClaimsMappingProperties(Map<String, Properties> mapProperties) {
		return mapProperties.get(Costanti.VALIDAZIONE_JWT_TOKEN_PARSER_COLLECTION_ID);
	}
	public static Properties getIntrospectionClaimsMappingProperties(Map<String, Properties> mapProperties) {
		return mapProperties.get(Costanti.INTROSPECTION_TOKEN_PARSER_COLLECTION_ID);
	}
	public static Properties getUserInfoClaimsMappingProperties(Map<String, Properties> mapProperties) {
		return mapProperties.get(Costanti.USERINFO_TOKEN_PARSER_COLLECTION_ID);
	}
	
	public static Properties getRetrieveResponseClaimsMappingProperties(Map<String, Properties> mapProperties) {
		return mapProperties.get(Costanti.RETRIEVE_TOKEN_PARSER_COLLECTION_ID);
	}
	
	public static List<String> getClaims(Properties p, String name) {
		String v = p.getProperty(name);
		List<String> l = new ArrayList<>();
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
	
	public static Map<String, Properties> getMultiProperties(GenericProperties gp) throws ProviderException {
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
	
	public static boolean isValidazioneEnabled(Map<String, Properties> mapProperties) {
		return isValidazioneEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isValidazioneEnabled(Properties pDefault) {
		return isEnabled(pDefault, Costanti.POLICY_VALIDAZIONE_STATO);
	}
	public static boolean isValidazioneEnabled(GenericProperties gp) throws ProviderException {
		Map<String, Properties> multiProperties = getMultiProperties(gp);
		return isValidazioneEnabled(multiProperties);
	}
		
	public static boolean isIntrospectionEnabled(Map<String, Properties> mapProperties) {
		return isIntrospectionEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isIntrospectionEnabled(Properties pDefault) {
		return isEnabled(pDefault, Costanti.POLICY_INTROSPECTION_STATO);
	}
	public static boolean isIntrospectionEnabled(GenericProperties gp) throws ProviderException {
		Map<String, Properties> multiProperties = getMultiProperties(gp);
		return isIntrospectionEnabled(multiProperties);
	}
	
	public static boolean isUserInfoEnabled(Map<String, Properties> mapProperties) {
		return isUserInfoEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isUserInfoEnabled(Properties pDefault) {
		return isEnabled(pDefault, Costanti.POLICY_USER_INFO_STATO);
	}
	public static boolean isUserInfoEnabled(GenericProperties gp) throws ProviderException {
		Map<String, Properties> multiProperties = getMultiProperties(gp);
		return isUserInfoEnabled(multiProperties);
	}
	
	public static boolean isTokenForwardEnabled(Map<String, Properties> mapProperties) {
		return isTokenForwardEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isTokenForwardEnabled(Properties pDefault) {
		return isEnabled(pDefault, Costanti.POLICY_TOKEN_FORWARD_STATO);
	}
	public static boolean isTokenForwardEnabled(GenericProperties gp) throws ProviderException {
		Map<String, Properties> multiProperties = getMultiProperties(gp);
		return isTokenForwardEnabled(multiProperties);
	}
	
	public static boolean isEnabled(Properties p, String propertyName) {
		return MultiPropertiesUtilities.isEnabled(p, propertyName);
	}
	
	private static String getPrefixPolicy(GenericProperties gp) {
		return "La configurazione nella policy "+gp.getNome();
	}
	
	public static KeystoreParams getValidazioneJwtKeystoreParams(GenericProperties gp) throws TokenException, ProviderException, ProviderValidationException {
		PolicyGestioneToken policy = TokenUtilities.convertTo(gp, new GestioneToken());
		if(!TokenUtilities.isValidazioneEnabled(gp)) {
			throw new TokenException(getPrefixPolicy(gp)+" non utilizza la funzionalità di validazione JWT");
		}
		return getValidazioneJwtKeystoreParams(policy);
	}
	public static KeystoreParams getValidazioneJwtKeystoreParams(PolicyGestioneToken policy) throws TokenException {
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
	
	public static KeystoreParams getForwardToJwtKeystoreParams(GenericProperties gp) throws TokenException, ProviderException, ProviderValidationException {
		PolicyGestioneToken policy = TokenUtilities.convertTo(gp, new GestioneToken());
		if(!TokenUtilities.isTokenForwardEnabled(gp) || !policy.isForwardTokenInformazioniRaccolte()) {
			throw new TokenException(getPrefixPolicy(gp)+" non utilizza la funzionalità di forward delle informazioni raccolte del token");
		}
		return getForwardToJwtKeystoreParams(policy);
	}
	public static KeystoreParams getForwardToJwtKeystoreParams(PolicyGestioneToken policy) throws TokenException {
		String forwardInformazioniRaccolteMode = policy.getForwardTokenInformazioniRaccolteMode();
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
	
	public static PolicyGestioneToken convertTo(GenericProperties gp, GestioneToken gestioneToken) throws ProviderException, ProviderValidationException { 
	
		PolicyGestioneToken policy = new PolicyGestioneToken();
		policy.setName(gp.getNome());
		policy.setDescrizione(gp.getDescrizione());
		
		Map<String, Properties> multiProperties = TokenUtilities.getMultiProperties(gp);
		policy.setProperties(multiProperties);
		
		policy.setTokenOpzionale(false);
		
		policy.setValidazioneJWT(false);
		policy.setValidazioneJWTWarningOnly(false);
		
		policy.setIntrospection(false);
		policy.setIntrospectionWarningOnly(false);
		
		policy.setUserInfo(false);
		policy.setUserInfoWarningOnly(false);
		
		policy.setForwardToken(false);
		
		if(gestioneToken!=null) {
			fill(policy, gestioneToken, multiProperties);
		}
		
		return policy;

	}
	private static void fill(PolicyGestioneToken policy, GestioneToken gestioneToken, Map<String, Properties> multiProperties) {
		if(gestioneToken.getTokenOpzionale()!=null) {
			policy.setTokenOpzionale(org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneToken.getTokenOpzionale()));
		}
		
		boolean validazioneEnabledDaPolicy = isValidazioneEnabled(multiProperties);
		if(validazioneEnabledDaPolicy &&
			gestioneToken.getValidazione()!=null) {
			switch (gestioneToken.getValidazione()) {
			case ABILITATO:
				policy.setValidazioneJWT(true);
				policy.setValidazioneJWTWarningOnly(false);
				break;
			case WARNING_ONLY:
				policy.setValidazioneJWT(true);
				policy.setValidazioneJWTWarningOnly(true);
				break;
			case DISABILITATO:
				policy.setValidazioneJWT(false);
				policy.setValidazioneJWTWarningOnly(false);
				break;
			}
		}
		
		boolean introspectionEnabledDaPolicy = isIntrospectionEnabled(multiProperties);
		if(introspectionEnabledDaPolicy &&
			gestioneToken.getIntrospection()!=null) {
			switch (gestioneToken.getIntrospection()) {
			case ABILITATO:
				policy.setIntrospection(true);
				policy.setIntrospectionWarningOnly(false);
				break;
			case WARNING_ONLY:
				policy.setIntrospection(true);
				policy.setIntrospectionWarningOnly(true);
				break;
			case DISABILITATO:
				policy.setIntrospection(false);
				policy.setIntrospectionWarningOnly(false);
				break;
			}
		}
		
		boolean userInfoEnabledDaPolicy = isUserInfoEnabled(multiProperties);
		if(userInfoEnabledDaPolicy &&
			gestioneToken.getUserInfo()!=null) {
			switch (gestioneToken.getUserInfo()) {
			case ABILITATO:
				policy.setUserInfo(true);
				policy.setUserInfoWarningOnly(false);
				break;
			case WARNING_ONLY:
				policy.setUserInfo(true);
				policy.setUserInfoWarningOnly(true);
				break;
			case DISABILITATO:
				policy.setUserInfo(false);
				policy.setUserInfoWarningOnly(false);
				break;
			}
		}
		
		boolean forwardEnabledDaPolicy = isTokenForwardEnabled(multiProperties);
		if(forwardEnabledDaPolicy &&
			gestioneToken.getForward()!=null) {
			policy.setForwardToken(org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneToken.getForward()));
		}
	}
	
	
	public static List<String> getClaimValues(Object value) {
		List<String> lRet = null;
		if(value!=null) {
			if(value instanceof List<?>) {
				List<?> l = (List<?>) value;
				return getClaimValues(l);
			}
			else {
				String sValue = value.toString();
				List<String> l = new ArrayList<>();
				l.add(sValue);
				return l;
			}
		}
		return lRet;
	}
	private static List<String> getClaimValues(List<?> l) {
		List<String> lRet = null;
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
		return lRet;
	}
	
	public static String getClaimValuesAsString(List<String> claimValues) {
		StringBuilder claimValueSB = new StringBuilder();
		if(claimValues==null || claimValues.isEmpty()) {
			return null;
		}
		if(claimValues.size()>1) {
			for (String c : claimValues) {
				if(claimValueSB.length()>0) {
					claimValueSB.append(",");
				}
				claimValueSB.append(c);
			}
		}
		else {
			claimValueSB.append(claimValues.get(0));
		}
		return claimValueSB.length()>0 ? claimValueSB.toString() : null;
	}
	
	public static String getClaimAsString(Map<String, Serializable> claims, String claim) {
		List<String> l = getClaimAsList(claims, claim);
		if(l==null || l.isEmpty()) {
			return null;
		}
		return TokenUtilities.getClaimValuesAsString(l);
	}
	public static List<String> getClaimAsList(Map<String, Serializable> claims, String claim) {
		List<String> l = null;
		Object o = claims.get(claim);
		if(o==null) {
			return l;
		}
		l = TokenUtilities.getClaimValues(o);
		List<String> lRet = null;
		if(l!=null && l.isEmpty()) {
			return lRet;
		}
		return l;
	}
	
	public static String getFirstClaimAsString(Map<String, Serializable> claims, List<String> names) {
		for (String name : names) {
			String claim = getClaimAsString(claims, name);
			if(claim!=null) {
				return claim;
			}
		}
		return null;
	}
	public static List<String> getFirstClaimAsList(Map<String, Serializable> claims, List<String> names) {
		List<String> lRet = null;
		for (String name : names) {
			List<String> l = getClaimAsList(claims, name);
			if(l!=null && !l.isEmpty()) {
				return l;
			}
		}
		return lRet;
	}
	
	public static KeystoreParams getSignedJwtKeystoreParams(GenericProperties gp) throws Exception {
		PolicyNegoziazioneToken policy = TokenUtilities.convertTo(gp);
		return getSignedJwtKeystoreParams(policy);
	}
	public static KeystoreParams getSignedJwtKeystoreParams(PolicyNegoziazioneToken policy) throws TokenException {
	
		if(!policy.isRfc7523x509Grant()) {
			throw new TokenException("La configurazione nella policy "+policy.getName()+" non utilizza la funzionalità SignedJWT (RFC 7523)");
		}
		
		String keystoreType = policy.getJwtSignKeystoreType();
		if(keystoreType==null) {
			throw new TokenException("JWT Signature keystore type undefined");
		}
		String keystoreFile = policy.getJwtSignKeystoreFile();
		if(keystoreFile==null) {
			throw new TokenException("JWT Signature keystore file undefined");
		}
		String keystorePassword = policy.getJwtSignKeystorePassword();
		if(keystorePassword==null && 
				!SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreType) && 
				!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType) && 
				!SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
			throw new TokenException("JWT Signature keystore password undefined");
		}
		String keyAlias = policy.getJwtSignKeyAlias();
		if(keyAlias==null && 
			!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType) && 
			!SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
			throw new TokenException("JWT Signature key alias undefined");
		}
		
		String keyPassword = policy.getJwtSignKeyPassword();
		if(keyPassword==null && 
				!SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreType) && 
				!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType) && 
				!SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
			throw new TokenException("JWT Signature key password undefined");
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
	
	private static void fillKeyPairParamters(KeystoreParams keystoreParams, String keystoreType, PolicyNegoziazioneToken policy) throws TokenException {
		if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType)) {
			String keystorePublicKeyFile = policy.getJwtSignKeystoreFilePublicKey();
			if(keystorePublicKeyFile==null) {
				throw new TokenException("JWT Signature public key file undefined");
			}
			keystoreParams.setKeyPairPublicKeyPath(keystorePublicKeyFile);
		}
		
		if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType)
				||
			SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
			String keyPairAlgorithm = policy.getJwtSignKeyPairAlgorithm();
			if(keyPairAlgorithm==null) {
				throw new TokenException("JWT Signature key pair algorithm undefined");
			}
			keystoreParams.setKeyPairAlgorithm(keyPairAlgorithm);
		}
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
				if(oClaim instanceof String) {
					String claim = (String) oClaim;
					String value = claims.getProperty(claim);
					validateClaims(oggetto, elemento, denyClaims, checkSpazi, claim, value);
				}
			}
		}
	}
	private static void validateClaims(String oggetto, String elemento, List<String> denyClaims, boolean checkSpazi,
			String claim, String value) throws ProviderValidationException {
		String indicato = "indicato nel campo '"+elemento+"'";
		if(denyClaims.contains(claim) || denyClaims.contains(claim.toLowerCase())) {
			throw new ProviderValidationException(oggetto+" '"+claim+"', "+indicato+", non può essere configurato");
		}
		if(value==null || StringUtils.isEmpty(value)) {
			throw new ProviderValidationException(oggetto+" '"+claim+"', "+indicato+", non è valorizzato");
		}
		if(checkSpazi) {
			if(value.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nel "+oggetto+" '"+claim+"', "+indicato+"");
			}
		}
		else {
			if(value.startsWith(" ")) {
				throw new ProviderValidationException("Il valore del "+oggetto+" '"+claim+"', "+indicato+", non deve iniziare con uno spazio");
			}
			if(value.endsWith(" ")) {
				throw new ProviderValidationException("Il valore del "+oggetto+" '"+claim+"', "+indicato+", non deve terminare con uno spazio");
			}
		}
	}
	
	public static String deleteSignature(String token) {
		// verifico che sia un JWT
		if(token.contains(".")) {
			String [] tmp = token.split("\\.");
			if(tmp!=null && tmp.length==3 &&
				tmp[2]!=null) {
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
		return token;
	}
	public static Map<String, Serializable> replaceTokenInMapByValue(Map<String, Serializable> claims, String valueOriginale, String newValue) {
		
		Map<String, Serializable> newMap = new HashMap<>();
		
		if(claims!=null && !claims.isEmpty()) {
			for (Map.Entry<String,Serializable> entry : claims.entrySet()) {
				String key = entry.getKey();
				Serializable o = claims.get(key);
				if(o instanceof String && valueOriginale.equals(o)) {
					newMap.put(key, newValue);
				}
				else {
					newMap.put(key, o);
				}
			}
		}
		
		return newMap;
	}
	
	private static final String EXTERNAL_RESOURCE_UNDEFINED = "External resource undefined";
	private static final String RECUPERO_PLUGIN_REGISTRATI_FALLITO_PREFIX = "Recupero plugin registrati fallito: ";
	
	public static List<String> getTokenPluginValues(ExternalResources externalResources, TipoPlugin tipoPlugin) throws ProviderException{
		return getTokenPluginList(externalResources, tipoPlugin, true);
	}
	public static List<String> getTokenPluginLabels(ExternalResources externalResources, TipoPlugin tipoPlugin) throws ProviderException{
		return getTokenPluginList(externalResources, tipoPlugin, false);
	}
	private static List<String> getTokenPluginList(ExternalResources externalResources, TipoPlugin tipoPlugin, boolean value) throws ProviderException{
		if(externalResources==null) {
			throw new ProviderException(EXTERNAL_RESOURCE_UNDEFINED);
		}
		ISearch ricerca = new Search(true);
		ricerca.addFilter(Liste.CONFIGURAZIONE_PLUGINS_CLASSI,  Filtri.FILTRO_TIPO_PLUGIN_CLASSI, tipoPlugin.toString());
		List<Plugin> list = null;
		try {
			list = PluginsDriverUtils.pluginsClassiList(ricerca, externalResources.getConnection(), externalResources.getLog(), externalResources.getTipoDB());
		}catch(Exception t) {
			throw new ProviderException(RECUPERO_PLUGIN_REGISTRATI_FALLITO_PREFIX+t.getMessage(),t);
		}
		List<String> values = new ArrayList<>();
		values.add(CostantiConfigurazione.POLICY_ID_NON_DEFINITA);
		if(list!=null && !list.isEmpty()) {
			for (Plugin plugin : list) {
				if(plugin.isStato()) {
					if(value) {
						values.add(plugin.getTipo());
					}
					else {
						values.add(plugin.getLabel());
					}
				}
			}
		}
		
		return values;
	}
	
	public static String dynamicUpdateTokenPluginChoice(ExternalResources externalResources, TipoPlugin tipoPlugin, Item item, String actualValue) {
		try {
			if(externalResources==null) {
				throw new ProviderException(EXTERNAL_RESOURCE_UNDEFINED);
			}
			ISearch ricerca = new Search(true);
			ricerca.addFilter(Liste.CONFIGURAZIONE_PLUGINS_CLASSI,  Filtri.FILTRO_TIPO_PLUGIN_CLASSI, tipoPlugin.toString());
			List<Plugin> listTmp = pluginsClassiList(ricerca, externalResources);
			List<Plugin> list = null;
			if(listTmp!=null && !listTmp.isEmpty()) {
				list = new ArrayList<>();
				for (Plugin p : listTmp) {
					if(p.isStato()) {	
						list.add(p);
					}
				}
			}
			if(list==null || list.isEmpty()) {
				item.setType(ItemType.HIDDEN);
				item.setValue(CostantiConfigurazione.POLICY_ID_NON_DEFINITA);
				return CostantiConfigurazione.POLICY_ID_NON_DEFINITA;
			}
			else {
				item.setType(ItemType.SELECT);
				item.setValue(actualValue);
				return actualValue;
			}
		}catch(Exception t) {
			// ignore
			return actualValue;
		}
	}
	private static List<Plugin> pluginsClassiList(ISearch ricerca, ExternalResources externalResources) throws ProviderException{
		try {
			return PluginsDriverUtils.pluginsClassiList(ricerca, externalResources.getConnection(), externalResources.getLog(), externalResources.getTipoDB());
		}catch(Exception t) {
			throw new ProviderException(RECUPERO_PLUGIN_REGISTRATI_FALLITO_PREFIX+t.getMessage(),t);
		}
	}
	public static String dynamicUpdateTokenPluginClassName(ExternalResources externalResources, TipoPlugin tipoPlugin, 
			List<?> items, Map<String, String> mapNameValue, Item item, 
			String idChoice, String actualValue) {
		try {
			if(externalResources==null) {
				throw new ProviderException(EXTERNAL_RESOURCE_UNDEFINED);
			}
			
			List<Plugin> list = fillListTipoPlugin(externalResources, tipoPlugin);
			
			return dynamicUpdateTokenPluginClassName(list, 
					items, mapNameValue, item, 
					idChoice, actualValue);
			
		}catch(Exception t) {
			// ignore
		}
		return actualValue;
	}
	private static List<Plugin> fillListTipoPlugin(ExternalResources externalResources, TipoPlugin tipoPlugin) throws ProviderException {
		ISearch ricerca = new Search(true);
		ricerca.addFilter(Liste.CONFIGURAZIONE_PLUGINS_CLASSI,  Filtri.FILTRO_TIPO_PLUGIN_CLASSI, tipoPlugin.toString());
		List<Plugin> listTmp = pluginsClassiList(ricerca, externalResources);
		List<Plugin> list = null;
		if(listTmp!=null && !listTmp.isEmpty()) {
			list = new ArrayList<>();
			for (Plugin p : listTmp) {
				if(p.isStato()) {	
					list.add(p);
				}
			}
		}
		return list;
	}
	private static String dynamicUpdateTokenPluginClassName(List<Plugin> list, 
			List<?> items, Map<String, String> mapNameValue, Item item, 
			String idChoice, String actualValue) {
		if(list!=null && !list.isEmpty()) {
			item.setRequired(false);
			
			if(actualValue==null) {
				item.setType(ItemType.HIDDEN);
				item.setValue(CostantiConfigurazione.POLICY_ID_NON_DEFINITA);
				return CostantiConfigurazione.POLICY_ID_NON_DEFINITA;
			}
			else {
				String pluginSelected = AbstractSecurityProvider.readValue(idChoice, items, mapNameValue);
				if(pluginSelected!=null && !StringUtils.isEmpty(pluginSelected) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(pluginSelected)) {
					item.setType(ItemType.HIDDEN);
					item.setValue(CostantiConfigurazione.POLICY_ID_NON_DEFINITA);
					return CostantiConfigurazione.POLICY_ID_NON_DEFINITA;
				}
			}
			
			if(StringUtils.isNotEmpty(actualValue) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(actualValue)) {
				item.setType(ItemType.TEXT);
			}
		}
		else {
			item.setType(ItemType.TEXT);
			item.setRequired(true);
		}
		return actualValue;
	}
	
	
	
	
	public static HashMap<String, Serializable> toHashMapSerializable(Map<String, Serializable> map) {
		HashMap<String, Serializable> mapNull = null;
		if(map instanceof HashMap) {
			return (HashMap<String, Serializable>) map;
		}
		else if(map!=null) {
			HashMap<String, Serializable> sMap = new HashMap<>();
			for (Map.Entry<String,Serializable> entry : map.entrySet()) {
				sMap.put(entry.getKey(), entry.getValue());
			}
		}
		return mapNull;
	}
}
