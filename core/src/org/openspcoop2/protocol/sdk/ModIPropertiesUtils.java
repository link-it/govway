/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.sdk;

import java.lang.reflect.Method;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.slf4j.Logger;

/**     
 * ModIPropertiesUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class ModIPropertiesUtils {
	
	private ModIPropertiesUtils() {}

	// Usare anzi i metodi in ModI Utils

	public static final String CLASS_MODIPA_PROPERTIES = "org.openspcoop2.protocol.modipa.config.ModIProperties";
	public static final String CLASS_MODIPA_PROPERTIES_GET_INSTANCE_METHOD = "getInstance";
	
	public static Object getModiProperties() throws ProtocolException {
		try {
			Class<?> modiPropertiesClass = Class.forName(CLASS_MODIPA_PROPERTIES);
			Method mGetInstance = modiPropertiesClass.getMethod(CLASS_MODIPA_PROPERTIES_GET_INSTANCE_METHOD);
			return mGetInstance.invoke(null);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}

	
	public static String getHeaderModI() throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGetRestSecurityTokenHeaderModI = instance.getClass().getMethod("getRestSecurityTokenHeaderModI");
			return (String) mGetRestSecurityTokenHeaderModI.invoke(instance);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public static boolean isTokenOAuthUseJtiIntegrityAsMessageId() throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGetIsTokenOAuthUseJtiIntegrityAsMessageId = instance.getClass().getMethod("isTokenOAuthUseJtiIntegrityAsMessageId");
			return (Boolean) mGetIsTokenOAuthUseJtiIntegrityAsMessageId.invoke(instance);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	
	private static final String API_PDND_CLIENT_CONFIG_METHOD = "getAPIPDNDClientConfig";
	public static ModIPDNDClientConfig getAPIPDNDClientConfig() throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGetAPIPDNDClientConfig = instance.getClass().getMethod(API_PDND_CLIENT_CONFIG_METHOD);
			return (ModIPDNDClientConfig) mGetAPIPDNDClientConfig.invoke(instance);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public static ModIPDNDClientConfig getAPIPDNDClientConfig(Logger log) throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGetAPIPDNDClientConfig = instance.getClass().getMethod(API_PDND_CLIENT_CONFIG_METHOD, Logger.class);
			return (ModIPDNDClientConfig) mGetAPIPDNDClientConfig.invoke(instance, log);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public static ModIPDNDClientConfig getAPIPDNDClientConfig(String details) throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGetAPIPDNDClientConfig = instance.getClass().getMethod(API_PDND_CLIENT_CONFIG_METHOD, String.class);
			return (ModIPDNDClientConfig) mGetAPIPDNDClientConfig.invoke(instance, details);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public static ModIPDNDClientConfig getAPIPDNDClientConfig(String details, Logger log) throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGetAPIPDNDClientConfig = instance.getClass().getMethod(API_PDND_CLIENT_CONFIG_METHOD, String.class, Logger.class);
			return (ModIPDNDClientConfig) mGetAPIPDNDClientConfig.invoke(instance, details, log);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	
	private static final String API_PDND_ORGANIZATION_CONFIG_METHOD = "getAPIPDNDOrganizationConfig";
	public static ModIPDNDOrganizationConfig getAPIPDNDOrganizationConfig() throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGetAPIPDNDOrganizationConfig = instance.getClass().getMethod(API_PDND_ORGANIZATION_CONFIG_METHOD);
			return (ModIPDNDOrganizationConfig) mGetAPIPDNDOrganizationConfig.invoke(instance);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public static ModIPDNDOrganizationConfig getAPIPDNDOrganizationConfig(Logger log) throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGetAPIPDNDOrganizationConfig = instance.getClass().getMethod(API_PDND_ORGANIZATION_CONFIG_METHOD, Logger.class);
			return (ModIPDNDOrganizationConfig) mGetAPIPDNDOrganizationConfig.invoke(instance, log);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public static ModIPDNDOrganizationConfig getAPIPDNDOrganizationConfig(String details) throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGetAPIPDNDOrganizationConfig = instance.getClass().getMethod(API_PDND_ORGANIZATION_CONFIG_METHOD, String.class);
			return (ModIPDNDOrganizationConfig) mGetAPIPDNDOrganizationConfig.invoke(instance, details);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public static ModIPDNDOrganizationConfig getAPIPDNDOrganizationConfig(String details, Logger log) throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGetAPIPDNDOrganizationConfig = instance.getClass().getMethod(API_PDND_ORGANIZATION_CONFIG_METHOD, String.class, Logger.class);
			return (ModIPDNDOrganizationConfig) mGetAPIPDNDOrganizationConfig.invoke(instance, details, log);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	
	public static IDAccordo buildSignalHubPushIdAPI(IDSoggetto idSoggettoDefault) throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGetSignalHubApiName = instance.getClass().getMethod("getSignalHubApiName");
			Method mGetSignalHubApiVersion = instance.getClass().getMethod("getSignalHubApiVersion");
			return IDAccordoFactory.getInstance().getIDAccordoFromValues((String) mGetSignalHubApiName.invoke(instance), idSoggettoDefault, (Integer) mGetSignalHubApiVersion.invoke(instance));
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public static boolean isSignalHubEnabled() throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mIsSignalHubEnabled = instance.getClass().getMethod("isSignalHubEnabled");
			return (Boolean) mIsSignalHubEnabled.invoke(instance);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public static boolean isTracingPDNDEnabled() throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mIsTracingPDNDEnabled = instance.getClass().getMethod("isTracingPDNDEnabled");
			return (Boolean) mIsTracingPDNDEnabled.invoke(instance);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public static boolean isTracingPDNDEnabledSafe() {
		try {
			return isTracingPDNDEnabled();
		}catch(Exception e) {
			return false;
		}
	}
		
	@SuppressWarnings("unchecked")
	public static List<RemoteStoreConfig> getRemoteStoreConfig() throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGetRemoteStoreConfig = instance.getClass().getMethod("getRemoteStoreConfig");
			return (List<RemoteStoreConfig>) mGetRemoteStoreConfig.invoke(instance);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public static RemoteKeyType getRemoteKeyType(String name) throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGetRemoteKey = instance.getClass().getMethod("getRemoteKeyType",String.class);
			return (RemoteKeyType) mGetRemoteKey.invoke(instance,name);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public static KeystoreParams getSicurezzaMessaggioCertificatiTrustStore() throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGet = instance.getClass().getMethod("getSicurezzaMessaggioCertificatiTrustStore");
			return (KeystoreParams) mGet.invoke(instance);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public static KeystoreParams getSicurezzaMessaggioSslTrustStore() throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGet = instance.getClass().getMethod("getSicurezzaMessaggioSslTrustStore");
			return (KeystoreParams) mGet.invoke(instance);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public static KeystoreParams getSicurezzaMessaggioCertificatiKeyStore() throws ProtocolException {
		try {
			Object instance = getModiProperties();
			Method mGet = instance.getClass().getMethod("getSicurezzaMessaggioCertificatiKeyStore");
			return (KeystoreParams) mGet.invoke(instance);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
}
