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
package org.openspcoop2.utils.certificate.remote;

import java.util.ArrayList;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.transport.http.ExternalResourceConfig;

/**
 * RemoteStoreConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoreConfig extends ExternalResourceConfig {

	private String storeName;
	private String storeLabel;
	
	private String tokenPolicy; 
	
	private String baseUrl;
	
	private boolean multitenant;
	private String baseUrlMultitenantDefaultString;
	private String baseUrlMultitenantPlaceholder;
	private String baseUrlMultitenantTenantString;
	
	private RemoteKeyIdMode idMode;
	private String parameterName; // in caso di query o parameter
	private String keyAlgorithm = KeyUtils.ALGO_RSA;
	
	public RemoteStoreConfig(String storeName) {
		super();
		this.storeName = storeName;
		this.storeLabel = storeName; // inizialmente uguale
	}
	
	public RemoteStoreConfig newInstanceMultitenant(String tenant) throws UtilsException {
				
		if(!this.multitenant) {
			throw new UtilsException("Multi-tenant disabled");
		}
		
		RemoteStoreConfig cloned = new RemoteStoreConfig(this.storeName+"");
		cloned.storeLabel = this.storeLabel!=null ? (this.storeLabel+"") : this.storeName;
		
		cloned.tokenPolicy = this.tokenPolicy!=null ? (this.tokenPolicy+"") : null;
		
		cloned.baseUrl = this.baseUrl!=null ? (this.baseUrl+"") : null;
		if(cloned.baseUrl!=null && this.baseUrlMultitenantDefaultString!=null && this.baseUrlMultitenantPlaceholder!=null && this.baseUrlMultitenantTenantString!=null) {
			
			String newString = this.baseUrlMultitenantTenantString.replace(this.baseUrlMultitenantPlaceholder, tenant);
			cloned.baseUrl = cloned.baseUrl.replace(this.baseUrlMultitenantDefaultString, newString);
			
		}
		
		cloned.idMode = this.idMode;
		cloned.parameterName = this.parameterName!=null ? (this.parameterName+"") : null;
		cloned.keyAlgorithm = this.keyAlgorithm!=null ? (this.keyAlgorithm+"") : KeyUtils.ALGO_RSA;
		
		// riporto dati ExternalResourceConfig
		this.newInstanceMultitenant(cloned, tenant);
		
		return cloned;
	}
	
	private void newInstanceMultitenant(RemoteStoreConfig cloned, String tenant) {
		
		cloned.readTimeout = this.readTimeout;
		cloned.connectTimeout = this.connectTimeout;
		
		if(this.returnCode!=null) {
			cloned.returnCode = new ArrayList<>();
			for (Integer intValue : this.returnCode) {
				if(intValue!=null) {
					cloned.returnCode.add(intValue);
				}
			}
		}
		
		cloned.basicUsername = this.basicUsername!=null ? (this.basicUsername+"") : null;
		cloned.basicPassword = this.basicPassword!=null ? (this.basicPassword+"") : null;
		
		cloned.hostnameVerifier = this.hostnameVerifier;
		
		cloned.trustAllCerts = this.trustAllCerts;
		
		cloned.trustStore = this.trustStore;
		
		cloned.crlStore = this.crlStore;
		
		cloned.keyStore = this.keyStore;
		cloned.keyAlias = this.keyAlias!=null ? (this.keyAlias+"") : null;
		cloned.keyPassword = this.keyPassword!=null ? (this.keyPassword+"") : null;
		
		cloned.forwardProxyUrl = this.forwardProxyUrl!=null ? (this.forwardProxyUrl+"") : null;
		cloned.forwardProxyHeader = this.forwardProxyHeader!=null ? (this.forwardProxyHeader+"") : null;
		cloned.forwardProxyQueryParameter = this.forwardProxyQueryParameter!=null ? (this.forwardProxyQueryParameter+"") : null;
		cloned.forwardProxyBase64 = this.forwardProxyBase64;
		
		cloned.headers = RemoteStoreConfigMultiTenantUtils.newMapInstance(this.headers);
		cloned.queryParameters = RemoteStoreConfigMultiTenantUtils.newMapInstance(this.queryParameters);
		
		/**cloned.multiTenantBasicUsername = RemoteStoreConfigMultiTenantUtils.newMapInstance(this.multiTenantBasicUsername);
		cloned.multiTenantBasicPassword = RemoteStoreConfigMultiTenantUtils.newMapInstance(this.multiTenantBasicPassword);
		
		cloned.multiTenantHeaders = RemoteStoreConfigMultiTenantUtils.newMultiMapInstance(this.multiTenantHeaders);
		cloned.multiTenantQueryParameters = RemoteStoreConfigMultiTenantUtils.newMultiMapInstance(this.multiTenantQueryParameters);*/
		
		// Devo sovrascrivere
		cloned.basicUsername = RemoteStoreConfigMultiTenantUtils.getMultitenant(this.multiTenantBasicUsername, tenant, cloned.basicUsername);
		cloned.basicPassword = RemoteStoreConfigMultiTenantUtils.getMultitenant(this.multiTenantBasicPassword, tenant, cloned.basicPassword);
		cloned.headers = RemoteStoreConfigMultiTenantUtils.getMultitenant(this.multiTenantHeaders, tenant, cloned.headers);
		cloned.queryParameters = RemoteStoreConfigMultiTenantUtils.getMultitenant(this.multiTenantQueryParameters, tenant, cloned.queryParameters);
		
		// imposto comunque l'indicazione che si tratta di un multitenant
		cloned.multitenant=true;
	}
	
	public String getStoreName() {
		return this.storeName;
	}
	
	public String getStoreLabel() {
		return this.storeLabel;
	}

	public void setStoreLabel(String storeLabel) {
		this.storeLabel = storeLabel;
	}
	
	public String getTokenPolicy() {
		return this.tokenPolicy;
	}

	public void setTokenPolicy(String tokenPolicy) {
		this.tokenPolicy = tokenPolicy;
	}
	
	public String getKeyAlgorithm() {
		return this.keyAlgorithm;
	}

	public void setKeyAlgorithm(String keyAlgorithm) {
		this.keyAlgorithm = keyAlgorithm;
	}

	public String getBaseUrl() {
		return this.baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public RemoteKeyIdMode getIdMode() {
		return this.idMode;
	}

	public void setIdMode(RemoteKeyIdMode idMode) {
		this.idMode = idMode;
	}

	public String getParameterName() {
		return this.parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	
	public boolean isMultitenant() {
		return this.multitenant;
	}

	public void setMultitenant(boolean multitenant) {
		this.multitenant = multitenant;
	}
	
	public String getBaseUrlMultitenantDefaultString() {
		return this.baseUrlMultitenantDefaultString;
	}

	public void setBaseUrlMultitenantDefaultString(String baseUrlMultitenantDefaultString) {
		this.baseUrlMultitenantDefaultString = baseUrlMultitenantDefaultString;
	}

	public String getBaseUrlMultitenantPlaceholder() {
		return this.baseUrlMultitenantPlaceholder;
	}

	public void setBaseUrlMultitenantPlaceholder(String baseUrlMultitenantPlaceholder) {
		this.baseUrlMultitenantPlaceholder = baseUrlMultitenantPlaceholder;
	}

	public String getBaseUrlMultitenantTenantString() {
		return this.baseUrlMultitenantTenantString;
	}

	public void setBaseUrlMultitenantTenantString(String baseUrlMultitenantTenantString) {
		this.baseUrlMultitenantTenantString = baseUrlMultitenantTenantString;
	}
}
