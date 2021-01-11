/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;

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
}
