package org.openspcoop2.core.mvc.properties.utils;

import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;

public class MultiPropertiesUtilities {

	public static Properties getDefaultProperties(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return mapProperties.get(org.openspcoop2.core.mvc.properties.utils.Costanti.NOME_MAPPA_PROPERTIES_DEFAULT);
	}
	
	public static Properties removeDefaultProperties(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		return mapProperties.remove(org.openspcoop2.core.mvc.properties.utils.Costanti.NOME_MAPPA_PROPERTIES_DEFAULT);
	}
	
	public static boolean isEnabled(Properties p, String propertyName) throws ProviderException, ProviderValidationException {
		boolean validazione = false;
		if(p.containsKey(propertyName)) {
			validazione = Boolean.valueOf(p.getProperty(propertyName));
		}
		return validazione;
	}
	
}
