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


package org.openspcoop2.security.message.utils;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.Item;
import org.openspcoop2.core.mvc.properties.constants.ItemType;
import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.certificate.ocsp.OCSPProvider;

/**     
 * AbstractSecurityProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractSecurityProvider implements IProvider {

	private boolean asTruststore = false; // indicazione se questo provider serve per un keystore o truststore
	
	public boolean isAsTruststore() {
		return this.asTruststore;
	}
	public void useAsKeystore() {
		this.asTruststore = false;
	}
	public void useAsTruststore() {
		this.asTruststore = true;
	}
	
	private OCSPProvider ocspProvider;

	public AbstractSecurityProvider() {
		this.ocspProvider = new OCSPProvider();
	}
	
	
	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {

	}

	@Override
	public List<String> getValues(String id) throws ProviderException {
		if(SecurityConstants.KEYSTORE_TYPE.equals(id)) {
			return SecurityConstants.getTIPOLOGIE_KEYSTORE_values(this.asTruststore);
		}
		else if(SecurityConstants.SECRETKEYSTORE_TYPE.equals(id)) {
			return SecurityConstants.getTIPOLOGIE_SECRETKEYSTORE_values();
		}
		else if(SecurityConstants.TRUSTSTORE_TYPE.equals(id)) {
			return SecurityConstants.getTIPOLOGIE_KEYSTORE_values(true);
		}
		else if(SecurityConstants.TRUSTSTORE_OCSP_POLICY.equals(id) ||
				SecurityConstants.KEYSTORE_OCSP_POLICY.equals(id)) {
			return this.ocspProvider.getValues();
		}
		
		return null;
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		if(SecurityConstants.KEYSTORE_TYPE.equals(id)) {
			return SecurityConstants.getTIPOLOGIE_KEYSTORE_labels(this.asTruststore);
		}
		else if(SecurityConstants.SECRETKEYSTORE_TYPE.equals(id)) {
			return SecurityConstants.getTIPOLOGIE_SECRETKEYSTORE_labels();
		}
		else if(SecurityConstants.TRUSTSTORE_TYPE.equals(id)) {
			return SecurityConstants.getTIPOLOGIE_KEYSTORE_labels(true);
		}
		else if(SecurityConstants.TRUSTSTORE_OCSP_POLICY.equals(id) ||
				SecurityConstants.KEYSTORE_OCSP_POLICY.equals(id)) {
			return this.ocspProvider.getLabels();
		}
		
		return this.getValues(id);
	}
	
	@Override
	public String getDefault(String id) throws ProviderException {

		return null;
	}

	@Override
	public String dynamicUpdate(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		if(SecurityConstants.KEYSTORE_FILE.equals(item.getName()) || 
				SecurityConstants.SECRETKEYSTORE_FILE.equals(item.getName()) || 
				SecurityConstants.TRUSTSTORE_FILE.equals(item.getName())) {
			
			String type = SecurityConstants.KEYSTORE_TYPE;
			if(SecurityConstants.SECRETKEYSTORE_FILE.equals(item.getName())) {
				type = SecurityConstants.SECRETKEYSTORE_TYPE;
			}
			else if(SecurityConstants.TRUSTSTORE_FILE.equals(item.getName())) {
				type = SecurityConstants.TRUSTSTORE_TYPE;
			}
			
			return processStoreFile(type, items, mapNameValue, item, actualValue);
		}
		else if(SecurityConstants.KEYSTORE_PASSWORD.equals(item.getName()) || 
				SecurityConstants.SECRETKEYSTORE_PASSWORD.equals(item.getName()) || 
				SecurityConstants.TRUSTSTORE_PASSWORD.equals(item.getName())) {
			
			String type = SecurityConstants.KEYSTORE_TYPE;
			if(SecurityConstants.SECRETKEYSTORE_PASSWORD.equals(item.getName())) {
				type = SecurityConstants.SECRETKEYSTORE_TYPE;
			}
			else if(SecurityConstants.TRUSTSTORE_PASSWORD.equals(item.getName())) {
				type = SecurityConstants.TRUSTSTORE_TYPE;
			}
			
			return processStorePassword(type, items, mapNameValue, item, actualValue);
		}
		else if(SecurityConstants.KEYSTORE_PRIVATE_KEY_PASSWORD.equals(item.getName()) ||
				SecurityConstants.SECRETKEYSTORE_PRIVATE_KEY_PASSWORD.equals(item.getName())) {
			if(!HSMUtils.HSM_CONFIGURABLE_KEY_PASSWORD) {
				
				String type = SecurityConstants.KEYSTORE_TYPE;
				if(SecurityConstants.SECRETKEYSTORE_PRIVATE_KEY_PASSWORD.equals(item.getName())) {
					type = SecurityConstants.SECRETKEYSTORE_TYPE;
				}
				
				return processStoreKeyPassword(type, items, mapNameValue, item, actualValue);
			}
		}
		else if(SecurityConstants.TRUSTSTORE_OCSP_POLICY.equals(item.getName()) ||
				SecurityConstants.KEYSTORE_OCSP_POLICY.equals(item.getName())) {
			if(!this.ocspProvider.isOcspEnabled()) {
				item.setValue("");
				item.setType(ItemType.HIDDEN);
			}
		}
		
		return actualValue;
	}
	
	public static String readValue(String identificativo, List<?> items, Map<String, String> mapNameValue) {
		String value = null;
		if(items!=null && !items.isEmpty()) {
			for (Object itemCheck : items) {
				//System.out.println("CHECK ["+itemCheck.getClass().getName()+"]");
				if(itemCheck instanceof Item) {
					Item listItem = (Item) itemCheck;
					
					if(identificativo.equals(listItem.getName())) {
						value = mapNameValue.get(identificativo);
						break;
					}
				}
			}
		}
		return value;
	}
	
	public static String processStoreFile(String type, List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		if(items!=null && !items.isEmpty()) {
			for (Object itemCheck : items) {
				//System.out.println("CHECK ["+itemCheck.getClass().getName()+"]");
				if(itemCheck instanceof Item) {
					Item listItem = (Item) itemCheck;
					
					boolean find = false;
					String value = null;
					if(type.equals(listItem.getName())) {
						find = true;
						value = mapNameValue.get(type);
					}
											
					if(find) {
						//System.out.println("TROVATO TYPE ["+mapNameValue.get(SecurityConstants.KEYSTORE_TYPE)+"]");
						if(value!=null && HSMUtils.isKeystoreHSM((String)value)) {
							//System.out.println("SET HIDDEN ["+HSMUtils.KEYSTORE_HSM_PREFIX+value+"]");
							item.setValue(HSMUtils.KEYSTORE_HSM_PREFIX+value);
							item.setType(ItemType.HIDDEN);
							return item.getValue();
						}
						else {
							item.setValue(actualValue);
							item.setType(ItemType.TEXTAREA);
							return item.getValue();
						}
						//break;
					}
				}
			}
		}
		return actualValue;
	}
	public static String processStorePassword(String type, List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		if(items!=null && !items.isEmpty()) {
			for (Object itemCheck : items) {
				//System.out.println("CHECK ["+itemCheck.getClass().getName()+"]");
				if(itemCheck instanceof Item) {
					Item listItem = (Item) itemCheck;
					boolean find = false;
					String value = null;
					if(type.equals(listItem.getName())) {
						find = true;
						value = mapNameValue.get(type);
					}
											
					if(find) {
						//System.out.println("TROVATO TYPE ["+mapNameValue.get(SecurityConstants.KEYSTORE_TYPE)+"]");
						if(value!=null && HSMUtils.isKeystoreHSM((String)value)) {
							//System.out.println("SET HIDDEN ["+HSMUtils.KEYSTORE_HSM_PREFIX+value+"]");
							item.setValue(HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED);
							item.setType(ItemType.HIDDEN);
							return item.getValue();
						}
						else {
							item.setValue(actualValue);
							item.setType(ItemType.TEXT);
							return item.getValue();
						}
						//break;
					}
				}
			}
		}
		return actualValue;
	}
	public static String processStoreKeyPassword(String type, List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
		if(items!=null && !items.isEmpty()) {
			for (Object itemCheck : items) {
				//System.out.println("CHECK ["+itemCheck.getClass().getName()+"]");
				if(itemCheck instanceof Item) {
					Item listItem = (Item) itemCheck;
					boolean find = false;
					String value = null;
					if(type.equals(listItem.getName())) {
						find = true;
						value = mapNameValue.get(type);
					}
											
					if(find) {
						//System.out.println("TROVATO TYPE ["+mapNameValue.get(SecurityConstants.KEYSTORE_TYPE)+"]");
						if(value!=null && HSMUtils.isKeystoreHSM((String)value)) {
							//System.out.println("SET HIDDEN ["+HSMUtils.KEYSTORE_HSM_PREFIX+value+"]");
							item.setValue(HSMUtils.KEYSTORE_HSM_PRIVATE_KEY_PASSWORD_UNDEFINED);
							item.setType(ItemType.HIDDEN);
							return item.getValue();
						}
						else {
							item.setValue(actualValue);
							item.setType(ItemType.TEXT);
							return item.getValue();
						}
						//break;
					}
				}
			}
		}
		return actualValue;
	}
}
