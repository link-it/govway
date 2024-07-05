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
	public static final String RS_SECURITY_BYOK_POLICY = "rs.security.keystore.file.byok";
	
	public static final String RS_SECURITY_KEYSTORE_PASSWORD = "rs.security.keystore.password";
	public static final String RS_SECURITY_KEY_PASSWORD = "rs.security.key.password";
	public static final String RS_SECURITY_KEYSTORE_TLS_PASSWORD = "rs.security.keystore.password.ssl";
	
	public static final String MERLIN_SECURITY_KEYSTORE_PASSWORD = "org.apache.ws.security.crypto.merlin.keystore.password";
		
	
	
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
		tokenValidationProperties.add(POLICY_USER_INFO_AUTH_BASIC_PASSWORD);
		tokenValidationProperties.add(POLICY_USER_INFO_AUTH_BEARER_TOKEN);
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
	
	
	
	
	public static final String MESSAGE_SECURITY_JOSE_RECEIVER_ENCRYPT_ID = "messageSecurity.jose.receiver.encrypt";
	public static final String MESSAGE_SECURITY_JOSE_RECEIVER_SIGNATURE_ID = "messageSecurity.jose.receiver.signature";
	public static final String MESSAGE_SECURITY_JOSE_SENDER_ENCRYPT_ID = "messageSecurity.jose.sender.encrypt";
	public static final String MESSAGE_SECURITY_JOSE_SENDER_SIGNATURE_ID = "messageSecurity.jose.sender.signature";
	
	public static final String MESSAGE_SECURITY_XML_RECEIVER_ENCRYPT_ID = "messageSecurity.xml.receiver.encrypt";
	public static final String MESSAGE_SECURITY_XML_RECEIVER_SIGNATURE_ID = "messageSecurity.xml.receiver.signature";
	public static final String MESSAGE_SECURITY_XML_SENDER_ENCRYPT_ID = "messageSecurity.xml.sender.encrypt";
	public static final String MESSAGE_SECURITY_XML_SENDER_SIGNATURE_ID = "messageSecurity.xml.sender.signature";
	
	public static final String MESSAGE_SECURITY_WSS4J_RECEIVER_ENCRYPT_ID = "messageSecurity.wss4j.receiver.encrypt";
	public static final String MESSAGE_SECURITY_WSS4J_RECEIVER_SAML_ID = "messageSecurity.wss4j.receiver.saml";
	public static final String MESSAGE_SECURITY_WSS4J_RECEIVER_SIGNATURE_ID = "messageSecurity.wss4j.receiver.signature";
	public static final String MESSAGE_SECURITY_WSS4J_RECEIVER_TIMESTAMP_ID = "messageSecurity.wss4j.receiver.timestamp";
	public static final String MESSAGE_SECURITY_WSS4J_RECEIVER_USERNAME_TOKEN_ID = "messageSecurity.wss4j.receiver.usernameToken";
	public static final String MESSAGE_SECURITY_WSS4J_SENDER_ENCRYPT_ID = "messageSecurity.wss4j.sender.encrypt";
	public static final String MESSAGE_SECURITY_WSS4J_SENDER_SAML_ID = "messageSecurity.wss4j.sender.saml";
	public static final String MESSAGE_SECURITY_WSS4J_SENDER_SIGNATURE_ID = "messageSecurity.wss4j.sender.signature";
	public static final String MESSAGE_SECURITY_WSS4J_SENDER_TIMESTAMP_ID = "messageSecurity.wss4j.sender.timestamp";
	public static final String MESSAGE_SECURITY_WSS4J_SENDER_USERNAME_TOKEN_ID = "messageSecurity.wss4j.sender.usernameToken";
	
	private static List<String> messageSecurityIds = new ArrayList<>();
	public static List<String> getMessageSecurityIds() {
		return messageSecurityIds;
	}
	static {
		messageSecurityIds.add(MESSAGE_SECURITY_JOSE_RECEIVER_ENCRYPT_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_JOSE_RECEIVER_SIGNATURE_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_JOSE_SENDER_ENCRYPT_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_JOSE_SENDER_SIGNATURE_ID);
		
		messageSecurityIds.add(MESSAGE_SECURITY_XML_RECEIVER_ENCRYPT_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_XML_RECEIVER_SIGNATURE_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_XML_SENDER_ENCRYPT_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_XML_SENDER_SIGNATURE_ID);
		
		messageSecurityIds.add(MESSAGE_SECURITY_WSS4J_RECEIVER_ENCRYPT_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_WSS4J_RECEIVER_SAML_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_WSS4J_RECEIVER_SIGNATURE_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_WSS4J_RECEIVER_TIMESTAMP_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_WSS4J_RECEIVER_USERNAME_TOKEN_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_WSS4J_SENDER_ENCRYPT_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_WSS4J_SENDER_SAML_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_WSS4J_SENDER_SIGNATURE_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_WSS4J_SENDER_TIMESTAMP_ID);
		messageSecurityIds.add(MESSAGE_SECURITY_WSS4J_SENDER_USERNAME_TOKEN_ID);
	}
	
	public static final String MESSAGE_SECURITY_JOSE_KEYSTORE_PASSWORD = "joseUseHeaders.keystore.password";
	public static final String MESSAGE_SECURITY_JOSE_KEY1_PASSWORD = "joseUseHeaders.key.1.password";
	public static final String MESSAGE_SECURITY_JOSE_KEY2_PASSWORD = "joseUseHeaders.key.2.password";
	public static final String MESSAGE_SECURITY_JOSE_KEY3_PASSWORD = "joseUseHeaders.key.3.password";
	public static final String MESSAGE_SECURITY_JOSE_KEY4_PASSWORD = "joseUseHeaders.key.4.password";
	public static final String MESSAGE_SECURITY_JOSE_KEY5_PASSWORD = "joseUseHeaders.key.5.password";
	public static final String MESSAGE_SECURITY_JOSE_KEY6_PASSWORD = "joseUseHeaders.key.6.password";
	public static final String MESSAGE_SECURITY_JOSE_KEY7_PASSWORD = "joseUseHeaders.key.7.password";
	public static final String MESSAGE_SECURITY_JOSE_KEY8_PASSWORD = "joseUseHeaders.key.8.password";
	public static final String MESSAGE_SECURITY_JOSE_KEY9_PASSWORD = "joseUseHeaders.key.9.password";
	public static final String MESSAGE_SECURITY_JOSE_KEY10_PASSWORD = "joseUseHeaders.key.10.password";
	public static final String MESSAGE_SECURITY_JOSE_TRUSTSTORE_PASSWORD = "joseUseHeaders.truststore.password";
	
	private static List<String> messageSecurityJoseProperties = new ArrayList<>();
	static {
		messageSecurityJoseProperties.add(MESSAGE_SECURITY_JOSE_KEYSTORE_PASSWORD);
		messageSecurityJoseProperties.add(MESSAGE_SECURITY_JOSE_KEY1_PASSWORD);
		messageSecurityJoseProperties.add(MESSAGE_SECURITY_JOSE_KEY2_PASSWORD);
		messageSecurityJoseProperties.add(MESSAGE_SECURITY_JOSE_KEY3_PASSWORD);
		messageSecurityJoseProperties.add(MESSAGE_SECURITY_JOSE_KEY4_PASSWORD);
		messageSecurityJoseProperties.add(MESSAGE_SECURITY_JOSE_KEY5_PASSWORD);
		messageSecurityJoseProperties.add(MESSAGE_SECURITY_JOSE_KEY6_PASSWORD);
		messageSecurityJoseProperties.add(MESSAGE_SECURITY_JOSE_KEY7_PASSWORD);
		messageSecurityJoseProperties.add(MESSAGE_SECURITY_JOSE_KEY8_PASSWORD);
		messageSecurityJoseProperties.add(MESSAGE_SECURITY_JOSE_KEY9_PASSWORD);
		messageSecurityJoseProperties.add(MESSAGE_SECURITY_JOSE_KEY10_PASSWORD);
		messageSecurityJoseProperties.add(MESSAGE_SECURITY_JOSE_TRUSTSTORE_PASSWORD);
		messageSecurityJoseProperties.add(RS_SECURITY_KEYSTORE_PASSWORD);
		messageSecurityJoseProperties.add(RS_SECURITY_KEY_PASSWORD);
		messageSecurityJoseProperties.add(RS_SECURITY_KEYSTORE_TLS_PASSWORD);
	}
	
	
	public static final String MESSAGE_SECURITY_XML_DECRYPTION_PASSWORD = "decryptionPassword";
	public static final String MESSAGE_SECURITY_XML_ENCRYPTION_PASSWORD = "encryptionPassword";
	public static final String MESSAGE_SECURITY_XML_SIGNATURE_PASSWORD = "signaturePassword";
	
	private static List<String> messageSecurityXmlProperties = new ArrayList<>();
	static {
		messageSecurityXmlProperties.add(MESSAGE_SECURITY_XML_DECRYPTION_PASSWORD);
		messageSecurityXmlProperties.add(MESSAGE_SECURITY_XML_ENCRYPTION_PASSWORD);
		messageSecurityXmlProperties.add(MESSAGE_SECURITY_XML_SIGNATURE_PASSWORD);
		messageSecurityXmlProperties.add(MERLIN_SECURITY_KEYSTORE_PASSWORD);
	}
	
	
	public static final String MESSAGE_SECURITY_WSS4J_DECRYPTION_PASSWORD = "decryptionPassword";
	public static final String MESSAGE_SECURITY_WSS4J_ENCRYPTION_PASSWORD = "encryptionPassword";
	public static final String MESSAGE_SECURITY_WSS4J_SIGNATURE_PASSWORD = "signaturePassword";
	public static final String MESSAGE_SECURITY_WSS4J_USERNAME_TOKEN_PASSWORD = "usernameTokenPassword";
	
	private static List<String> messageSecurityWss4jProperties = new ArrayList<>();
	static {
		messageSecurityWss4jProperties.add(MESSAGE_SECURITY_WSS4J_DECRYPTION_PASSWORD);
		messageSecurityWss4jProperties.add(MESSAGE_SECURITY_WSS4J_ENCRYPTION_PASSWORD);
		messageSecurityWss4jProperties.add(MESSAGE_SECURITY_WSS4J_SIGNATURE_PASSWORD);
		messageSecurityWss4jProperties.add(MESSAGE_SECURITY_WSS4J_USERNAME_TOKEN_PASSWORD);
		messageSecurityWss4jProperties.add(MERLIN_SECURITY_KEYSTORE_PASSWORD);
	}
	
	
	public static List<String> getMessageSecurityProperties(String id) {
		List<String> l = null;
		if(MESSAGE_SECURITY_JOSE_RECEIVER_ENCRYPT_ID.equals(id) ||
				MESSAGE_SECURITY_JOSE_RECEIVER_SIGNATURE_ID.equals(id) ||
				MESSAGE_SECURITY_JOSE_SENDER_ENCRYPT_ID.equals(id) ||
				MESSAGE_SECURITY_JOSE_SENDER_SIGNATURE_ID.equals(id)){
			l = messageSecurityJoseProperties;
		}
		else if(MESSAGE_SECURITY_XML_RECEIVER_ENCRYPT_ID.equals(id) ||
				MESSAGE_SECURITY_XML_RECEIVER_SIGNATURE_ID.equals(id) ||
				MESSAGE_SECURITY_XML_SENDER_ENCRYPT_ID.equals(id) ||
				MESSAGE_SECURITY_XML_SENDER_SIGNATURE_ID.equals(id)){
			l = messageSecurityXmlProperties;
		}
		else if(MESSAGE_SECURITY_WSS4J_RECEIVER_ENCRYPT_ID.equals(id) ||
				MESSAGE_SECURITY_WSS4J_RECEIVER_SAML_ID.equals(id) ||
				MESSAGE_SECURITY_WSS4J_RECEIVER_SIGNATURE_ID.equals(id) ||
				MESSAGE_SECURITY_WSS4J_RECEIVER_TIMESTAMP_ID.equals(id) ||
				MESSAGE_SECURITY_WSS4J_RECEIVER_USERNAME_TOKEN_ID.equals(id) ||
				MESSAGE_SECURITY_WSS4J_SENDER_ENCRYPT_ID.equals(id) ||
				MESSAGE_SECURITY_WSS4J_SENDER_SAML_ID.equals(id) ||
				MESSAGE_SECURITY_WSS4J_SENDER_SIGNATURE_ID.equals(id) ||
				MESSAGE_SECURITY_WSS4J_SENDER_TIMESTAMP_ID.equals(id) ||
				MESSAGE_SECURITY_WSS4J_SENDER_USERNAME_TOKEN_ID.equals(id)){
			l = messageSecurityWss4jProperties;
		}
		return l;
	}
	
}
