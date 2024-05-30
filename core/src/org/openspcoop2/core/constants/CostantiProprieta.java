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
package org.openspcoop2.core.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * CostantiProprieta
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiProprieta {
	
	private CostantiProprieta() {}

	// mvc
	public static final String KEY_PROPERTIES_CUSTOM_SEPARATOR = "_@@@_";
	public static final String KEY_PROPERTIES_DEFAULT_SEPARATOR = "_@@_";
	
	// commons
	public static final String RS_SECURITY_KEYSTORE_PASSWORD = "rs.security.keystore.password";
	public static final String RS_SECURITY_KEY_PASSWORD = "rs.security.key.password";
	public static final String RS_SECURITY_KEYSTORE_TLS_PASSWORD = "rs.security.keystore.password.ssl";
	
	
	
	
	/**public static final String MESSAGE_SECURITY_ID = "messageSecurity";
	
	private static List<String> messageSecurityProperties = new ArrayList<>();
	public static List<String> getMessageSecurityProperties() {
		return messageSecurityProperties;
	}
	static {
		//messageSecurityProperties.add(POLICY_INTROSPECTION_AUTH_BASIC_PASSWORD);
	}*/
	
	
	
	public static final String TOKEN_VALIDATION_ID = "validationToken";
	
	public static final String POLICY_INTROSPECTION_AUTH_BASIC_PASSWORD = "policy.introspection.endpoint.basic.password";
	public static final String POLICY_INTROSPECTION_AUTH_BEARER_TOKEN = "policy.introspection.endpoint.bearer.token";
	public static final String POLICY_USER_INFO_AUTH_BASIC_PASSWORD = "policy.userInfo.endpoint.basic.password";
	public static final String POLICY_USER_INFO_AUTH_BEARER_TOKEN = "policy.userInfo.endpoint.bearer.token";
	
	private static List<String> tokenValidationProperties = new ArrayList<>();
	public static List<String> getTokenValidationProperties() {
		return tokenValidationProperties;
	}
	static {
		tokenValidationProperties.add(POLICY_INTROSPECTION_AUTH_BASIC_PASSWORD);
		tokenValidationProperties.add(POLICY_INTROSPECTION_AUTH_BEARER_TOKEN);
		tokenValidationProperties.add(RS_SECURITY_KEYSTORE_PASSWORD);
		tokenValidationProperties.add(RS_SECURITY_KEY_PASSWORD);
		tokenValidationProperties.add(RS_SECURITY_KEYSTORE_TLS_PASSWORD);
		tokenValidationProperties.add(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		tokenValidationProperties.add(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
		tokenValidationProperties.add(CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD);
		tokenValidationProperties.add(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD);
	}
	
	
	
	
	public static final String TOKEN_NEGOZIAZIONE_ID = "retrieveToken";
	
	public static final String POLICY_RETRIEVE_TOKEN_PASSWORD = "policy.retrieveToken.password";
	public static final String POLICY_RETRIEVE_TOKEN_AUTH_BASIC_PASSWORD = "policy.retrieveToken.endpoint.basic.password";
	public static final String POLICY_RETRIEVE_TOKEN_AUTH_BEARER_TOKEN = "policy.retrieveToken.endpoint.bearer.token";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_PASSWORD = "policy.retrieveToken.jwt.signature.keystorePassword";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEY_PASSWORD = "policy.retrieveToken.jwt.signature.keyPassword";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_CLIENT_SECRET= "policy.retrieveToken.jwt.clientSecret";
	
	private static List<String> tokenRetrieveProperties = new ArrayList<>();
	public static List<String> getTokenRetrieveProperties() {
		return tokenRetrieveProperties;
	}
	static {
		tokenRetrieveProperties.add(POLICY_RETRIEVE_TOKEN_PASSWORD);
		tokenRetrieveProperties.add(POLICY_RETRIEVE_TOKEN_AUTH_BASIC_PASSWORD);
		tokenRetrieveProperties.add(POLICY_RETRIEVE_TOKEN_AUTH_BEARER_TOKEN);
		tokenRetrieveProperties.add(POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_PASSWORD);
		tokenRetrieveProperties.add(POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEY_PASSWORD);
		tokenRetrieveProperties.add(POLICY_RETRIEVE_TOKEN_JWT_CLIENT_SECRET);
		tokenRetrieveProperties.add(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		tokenRetrieveProperties.add(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
		tokenRetrieveProperties.add(CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD);
		tokenRetrieveProperties.add(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD);
	}
	
	
	
	public static final String ATTRIBUTE_AUTHORITY_ID = "attributeAuthority";
	
	public static final String AA_AUTH_BASIC_PASSWORD = "policy.endpoint.basic.password";
	public static final String AA_AUTH_BEARER_TOKEN = "policy.endpoint.bearer.token";
	public static final String AA_REQUEST_JWT_SIGN_KEYSTORE_PASSWORD = "policy.attributeAuthority.request.jws.keystore.password";
	public static final String AA_REQUEST_JWT_SIGN_KEY_PASSWORD = "policy.attributeAuthority.request.jws.key.password";

	private static List<String> attributeAuthorityProperties = new ArrayList<>();
	public static List<String> getAttributeAuthorityProperties() {
		return attributeAuthorityProperties;
	}
	static {
		attributeAuthorityProperties.add(AA_AUTH_BASIC_PASSWORD);
		attributeAuthorityProperties.add(AA_AUTH_BEARER_TOKEN);
		attributeAuthorityProperties.add(AA_REQUEST_JWT_SIGN_KEYSTORE_PASSWORD);
		attributeAuthorityProperties.add(AA_REQUEST_JWT_SIGN_KEY_PASSWORD);
		attributeAuthorityProperties.add(RS_SECURITY_KEYSTORE_PASSWORD);
		attributeAuthorityProperties.add(RS_SECURITY_KEY_PASSWORD);
		attributeAuthorityProperties.add(RS_SECURITY_KEYSTORE_TLS_PASSWORD);
		attributeAuthorityProperties.add(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		attributeAuthorityProperties.add(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
		attributeAuthorityProperties.add(CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD);
		attributeAuthorityProperties.add(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD);
	}
}
