package org.openspcoop2.pdd.core.token;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;

public class TokenUtilities {

	public static Properties getDefaultProperties(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return MultiPropertiesUtilities.getDefaultProperties(mapProperties);
	}
	
	public static boolean isValidazioneEnabled(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return isValidazioneEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isValidazioneEnabled(Properties pDefault) throws ProviderException, ProviderValidationException {
		return isEnabled(pDefault, Costanti.POLICY_VALIDAZIONE_STATO);
	}
	
	public static boolean isIntrospectionEnabled(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return isIntrospectionEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isIntrospectionEnabled(Properties pDefault) throws ProviderException, ProviderValidationException {
		return isEnabled(pDefault, Costanti.POLICY_INTROSPECTION_STATO);
	}
	
	public static boolean isUserInfoEnabled(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return isUserInfoEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isUserInfoEnabled(Properties pDefault) throws ProviderException, ProviderValidationException {
		return isEnabled(pDefault, Costanti.POLICY_USER_INFO_STATO);
	}
	
	public static boolean isTokenForwardEnabled(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return isTokenForwardEnabled(getDefaultProperties(mapProperties));
	}
	public static boolean isTokenForwardEnabled(Properties pDefault) throws ProviderException, ProviderValidationException {
		return isEnabled(pDefault, Costanti.POLICY_TOKEN_FORWARD_STATO);
	}
	
	public static boolean isEnabled(Properties p, String propertyName) throws ProviderException, ProviderValidationException {
		return MultiPropertiesUtilities.isEnabled(p, propertyName);
	}
	
	
	public static PolicyGestioneToken convertTo(GenericProperties gp, GestioneToken gestioneToken) throws Exception { 
	
		PolicyGestioneToken policy = new PolicyGestioneToken();
		policy.setName(gp.getNome());
		policy.setDescrizione(gp.getDescrizione());
		
		HashMap<String, String> properties = new HashMap<>();
		for (Property pConfig : gp.getPropertyList()) {
			properties.put(pConfig.getNome(), pConfig.getValore());
		}
		Map<String, Properties> multiProperties = DBPropertiesUtils.toMultiMap(properties);
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
		
		return policy;

	}
}
