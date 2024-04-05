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

package org.openspcoop2.pdd.config;

import java.io.Serializable;

/**
 * ForwardProxyConfigurazione
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ForwardProxyConfigurazioneToken implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected boolean tokenDynamicDiscoveryEnabled = false;
	protected boolean tokenJwtValidationEnabled = false;
	protected boolean tokenIntrospectionEnabled = false;
	protected boolean tokenUserInfoEnabled = false;
	protected boolean tokenRetrieveEnabled = false;
	protected boolean attributeAuthorityEnabled = false;
	protected boolean attributeAuthorityResponseJwtValidationEnabled = false;
	
	
	public ForwardProxyConfigurazioneToken() {
		// nop
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("token-dynamic-discovery-enabled: ").append(this.tokenDynamicDiscoveryEnabled);
		sb.append("token-jwt-validation-enabled: ").append(this.tokenJwtValidationEnabled);
		sb.append("token-introspection-enabled: ").append(this.tokenIntrospectionEnabled);
		sb.append("token-userinfo-enabled: ").append(this.tokenUserInfoEnabled);
		sb.append("token-retrieve-enabled: ").append(this.tokenRetrieveEnabled);
		sb.append("attribute-authority-enabled: ").append(this.attributeAuthorityEnabled);
		sb.append("attribute-authority-response-jwt-validation-enabled: ").append(this.attributeAuthorityResponseJwtValidationEnabled);
		return sb.toString();
	}	
	
	public boolean isTokenDynamicDiscoveryEnabled() {
		return this.tokenDynamicDiscoveryEnabled;
	}
	public void setTokenDynamicDiscoveryEnabled(boolean tokenDynamicDiscoveryEnabled) {
		this.tokenDynamicDiscoveryEnabled = tokenDynamicDiscoveryEnabled;
	}

	public boolean isTokenJwtValidationEnabled() {
		return this.tokenJwtValidationEnabled;
	}
	public void setTokenJwtValidationEnabled(boolean tokenJwtValidationEnabled) {
		this.tokenJwtValidationEnabled = tokenJwtValidationEnabled;
	}
	
	public boolean isTokenIntrospectionEnabled() {
		return this.tokenIntrospectionEnabled;
	}
	public void setTokenIntrospectionEnabled(boolean tokenIntrospectionEnabled) {
		this.tokenIntrospectionEnabled = tokenIntrospectionEnabled;
	}

	public boolean isTokenUserInfoEnabled() {
		return this.tokenUserInfoEnabled;
	}
	public void setTokenUserInfoEnabled(boolean tokenUserInfoEnabled) {
		this.tokenUserInfoEnabled = tokenUserInfoEnabled;
	}

	public boolean isTokenRetrieveEnabled() {
		return this.tokenRetrieveEnabled;
	}
	public void setTokenRetrieveEnabled(boolean tokenRetrieveEnabled) {
		this.tokenRetrieveEnabled = tokenRetrieveEnabled;
	}

	public boolean isAttributeAuthorityEnabled() {
		return this.attributeAuthorityEnabled;
	}
	public void setAttributeAuthorityEnabled(boolean attributeAuthorityEnabled) {
		this.attributeAuthorityEnabled = attributeAuthorityEnabled;
	}
	
	public boolean isAttributeAuthorityResponseJwtValidationEnabled() {
		return this.attributeAuthorityResponseJwtValidationEnabled;
	}
	public void setAttributeAuthorityResponseJwtValidationEnabled(boolean attributeAuthorityResponseJwtValidationEnabled) {
		this.attributeAuthorityResponseJwtValidationEnabled = attributeAuthorityResponseJwtValidationEnabled;
	}
	
}
