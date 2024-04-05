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

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;

/**     
 * Costanti
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {
	
	private Costanti() {}

	public static final String TIPOLOGIA = CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION;
	public static final String TIPOLOGIA_RETRIEVE = CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE;
	public static final String ATTRIBUTE_AUTHORITY = CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY;
	
	public static final String CHOICE_APPLICATIVO_MODI_VALUE = "applicativoModi";
	public static final String CHOICE_FRUIZIONE_MODI_VALUE = "fruizioneModi";
	
	
	// Context
		
	public static final MapKey<String> PDD_CONTEXT_TOKEN_POLICY = Map.newMapKey("TOKEN_POLICY");
	public static final MapKey<String> PDD_CONTEXT_TOKEN_REALM = Map.newMapKey("PDD_CONTEXT_TOKEN_REALM");
	public static final MapKey<String> PDD_CONTEXT_TOKEN_MESSAGE_ERROR_BODY_EMPTY = Map.newMapKey("PDD_CONTEXT_TOKEN_MESSAGE_ERROR_BODY_EMPTY");
	public static final MapKey<String> PDD_CONTEXT_TOKEN_MESSAGE_ERROR_GENERIC_MESSAGE = Map.newMapKey("PDD_CONTEXT_TOKEN_MESSAGE_ERROR_GENERIC_MESSAGE");
	public static final MapKey<String> PDD_CONTEXT_TOKEN_POSIZIONE = Map.newMapKey("TOKEN_POSIZIONE");
	public static final MapKey<String> PDD_CONTEXT_TOKEN_ESITO_DYNAMIC_DISCOVERY = Map.newMapKey("TOKEN_ESITO_DYNAMIC_DISCOVERY");
	public static final MapKey<String> PDD_CONTEXT_TOKEN_ESITO_VALIDAZIONE = Map.newMapKey("TOKEN_ESITO_VALIDAZIONE");
	public static final MapKey<String> PDD_CONTEXT_TOKEN_ESITO_INTROSPECTION = Map.newMapKey("TOKEN_ESITO_INTROSPECTION");
	public static final MapKey<String> PDD_CONTEXT_TOKEN_ESITO_USER_INFO = Map.newMapKey("TOKEN_ESITO_USER_INFO");
	public static final MapKey<String> PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE = Map.newMapKey("TOKEN_INFORMAZIONI_NORMALIZZATE");
	public static final MapKey<String> PDD_CONTEXT_TOKEN_INFORMAZIONI_PDND_CLIENT_READ = Map.newMapKey("TOKEN_INFORMAZIONI_PDND_READ");
	public static final MapKey<String> PDD_CONTEXT_ATTRIBUTI_INFORMAZIONI_NORMALIZZATE = Map.newMapKey("ATTRIBUTI_INFORMAZIONI_NORMALIZZATE");
	public static final MapKey<String> MSG_CONTEXT_TOKEN_FORWARD = Map.newMapKey("TOKEN_FORWARD"); // per salvarlo con il messaggio
	
    public static final MapKey<String> MODIPA_CONTEXT_AUDIT_DIGEST = Map.newMapKey("MODIPA_AUDIT_DIGEST");
    public static final MapKey<String> MODIPA_CONTEXT_AUDIT_DIGEST_ALGO = Map.newMapKey("MODIPA_AUDIT_DIGEST_ALGO");
	
	
	// Token Retrieve id
	
	public static final String RETRIEVE_TOKEN_PARSER_COLLECTION_ID = "retrieveTokenParserPropRefId";
	
	public static final String RETRIEVE_TOKEN_PARSER_TOKEN_TYPE = "token.parser.token_type";
	public static final String RETRIEVE_TOKEN_PARSER_ACCESS_TOKEN = "token.parser.access_token";
	public static final String RETRIEVE_TOKEN_PARSER_REFRESH_TOKEN = "token.parser.refresh_token";
	public static final String RETRIEVE_TOKEN_PARSER_SCOPE = "token.parser.scope";
	public static final String RETRIEVE_TOKEN_PARSER_EXPIRES_IN = "token.parser.expires_in";
	public static final String RETRIEVE_TOKEN_PARSER_EXPIRES_ON = "token.parser.expires_on";
	public static final String RETRIEVE_TOKEN_PARSER_REFRESH_EXPIRES_IN = "token.parser.refresh_expires_in";
	public static final String RETRIEVE_TOKEN_PARSER_REFRESH_EXPIRES_ON = "token.parser.refresh_expires_on";
	
	
	
	// Token Parser id

	public static final String DYNAMIC_DISCOVERY_PARSER_COLLECTION_ID = "dynamicDiscoveryParserPropRefId";
	public static final String VALIDAZIONE_JWT_TOKEN_PARSER_COLLECTION_ID = "validazioneJwtTokenParserPropRefId";
	public static final String INTROSPECTION_TOKEN_PARSER_COLLECTION_ID = "introspectionTokenParserPropRefId";
	public static final String USERINFO_TOKEN_PARSER_COLLECTION_ID = "userInfoTokenParserPropRefId";
	
	public static final String TOKEN_PARSER_ISSUER = "token.parser.issuer";
	public static final String TOKEN_PARSER_SUBJECT = "token.parser.subject";
	public static final String TOKEN_PARSER_AUDIENCE = "token.parser.audience";
	public static final String TOKEN_PARSER_EXPIRE = "token.parser.expire";
	public static final String TOKEN_PARSER_ISSUED_AT = "token.parser.issuedAt";
	public static final String TOKEN_PARSER_NOT_TO_BE_USED_BEFORE = "token.parser.notToBeUsedBefore";
	public static final String TOKEN_PARSER_JWT_IDENTIFIER = "token.parser.jwtIdentifier";
	public static final String TOKEN_PARSER_CLIENT_ID = "token.parser.clientId";
	public static final String TOKEN_PARSER_USERNAME = "token.parser.username";
	public static final String TOKEN_PARSER_SCOPE = "token.parser.scope";
	public static final String TOKEN_PARSER_ROLE = "token.parser.role";
	public static final String TOKEN_PARSER_USER_FULL_NAME = "token.parser.user.fullName";
	public static final String TOKEN_PARSER_USER_FIRST_NAME = "token.parser.user.firstName";
	public static final String TOKEN_PARSER_USER_MIDDLE_NAME = "token.parser.user.middleName";
	public static final String TOKEN_PARSER_USER_FAMILY_NAME = "token.parser.user.familyName";
	public static final String TOKEN_PARSER_USER_EMAIL = "token.parser.user.eMail";
	
	
	// Policy id
	
	public static final String GESTIONE_TOKEN_VALIDATION_ACTION_NONE = "NessunaValidazione";
	public static final String GESTIONE_TOKEN_VALIDATION_ACTION_JWT = "JWT";
	public static final String GESTIONE_TOKEN_VALIDATION_ACTION_INTROSPECTION = "Introspection";
	public static final String GESTIONE_TOKEN_VALIDATION_ACTION_USER_INFO = "UserInfo";
	
	public static final String GESTIONE_TOKEN_AUTENTICAZIONE_ISSUER="Issuer";
	public static final String GESTIONE_TOKEN_AUTENTICAZIONE_SUBJECT= "Subject";
	public static final String GESTIONE_TOKEN_AUTENTICAZIONE_CLIENT_ID="ClientId";
	public static final String GESTIONE_TOKEN_AUTENTICAZIONE_USERNAME="Username";
	public static final String GESTIONE_TOKEN_AUTENTICAZIONE_EMAIL="eMail";
	
	public static final String POLICY_REALM = "policy.realm";
	public static final String POLICY_MESSAGE_ERROR_BODY_EMPTY = "policy.messageError.bodyEmpty";
	public static final String POLICY_MESSAGE_ERROR_GENERIC_MESSAGE = "policy.messageError.genericMessage";
	
	public static final String POLICY_TOKEN_SOURCE = "policy.token.source";
	public static final String POLICY_TOKEN_SOURCE_RFC6750 = "RFC6750";
	public static final String POLICY_TOKEN_SOURCE_RFC6750_HEADER = "RFC6750_header";
	public static final String POLICY_TOKEN_SOURCE_RFC6750_FORM = "RFC6750_form";
	public static final String POLICY_TOKEN_SOURCE_RFC6750_URL = "RFC6750_url";
	public static final String POLICY_TOKEN_SOURCE_CUSTOM_HEADER = "CUSTOM_header";
	public static final String POLICY_TOKEN_SOURCE_CUSTOM_URL = "CUSTOM_url";
	public static final String POLICY_TOKEN_SOURCE_RFC6750_LABEL = "RFC 6750 - Bearer Token Usage";
	public static final String POLICY_TOKEN_SOURCE_RFC6750_HEADER_LABEL = "RFC 6750 - Bearer Token Usage (Authorization Request Header Field)\"";
	public static final String POLICY_TOKEN_SOURCE_RFC6750_FORM_LABEL = "RFC 6750 - Bearer Token Usage (Form-Encoded Body Parameter)";
	public static final String POLICY_TOKEN_SOURCE_RFC6750_URL_LABEL = "RFC 6750 - Bearer Token Usage (URI Query Parameter)";
	public static final String POLICY_TOKEN_SOURCE_CUSTOM_TEMPLATE_LABEL = "TEMPLATE";
	public static final String POLICY_TOKEN_SOURCE_CUSTOM_HEADER_LABEL = "Header HTTP '"+POLICY_TOKEN_SOURCE_CUSTOM_TEMPLATE_LABEL+"'";
	public static final String POLICY_TOKEN_SOURCE_CUSTOM_URL_LABEL = "Parametro URL '"+POLICY_TOKEN_SOURCE_CUSTOM_TEMPLATE_LABEL+"'";
	
	public static final String POLICY_TOKEN_SOURCE_CUSTOM_HEADER_NAME = "policy.token.source.header";
	public static final String POLICY_TOKEN_SOURCE_CUSTOM_URL_PROPERTY_NAME = "policy.token.source.queryParameter";
	
	public static final String POLICY_TOKEN_TYPE = "policy.token.type";
	public static final String POLICY_TOKEN_TYPE_OPAQUE = "opaque";
	public static final String POLICY_TOKEN_TYPE_JWS = "jws";
	public static final String POLICY_TOKEN_TYPE_JWE = "jwe";
	
	public static final String POLICY_STATO_ABILITATO = "true";
	public static final String POLICY_STATO_DISABILITATO = "false";
	
	public static final String POLICY_DISCOVERY_STATO = "policy.token.discovery";
	public static final String POLICY_DISCOVERY_CLAIMS_PARSER_TYPE = CostantiConfigurazione.POLICY_DYNAMIC_DISCOVERY_CLAIMS_PARSER_TYPE; 
	public static final String POLICY_DISCOVERY_CLAIMS_PARSER_TYPE_CUSTOM = CostantiConfigurazione.POLICY_DYNAMIC_DISCOVERY_CLAIMS_PARSER_TYPE_CUSTOM; 
	public static final String POLICY_DISCOVERY_CLAIMS_PARSER_CLASS_NAME = CostantiConfigurazione.POLICY_DYNAMIC_DISCOVERY_CLAIMS_PARSER_CLASS_NAME; 
	public static final String POLICY_DISCOVERY_CLAIMS_PARSER_PLUGIN_TYPE = CostantiConfigurazione.POLICY_DYNAMIC_DISCOVERY_CLAIMS_PARSER_PLUGIN_TYPE; 	
	public static final String POLICY_DISCOVERY_URL = "policy.discovery.endpoint.url";
	public static final String POLICY_DISCOVERY_JWK_CUSTOM = "policy.discovery.jwk";
	public static final String POLICY_DISCOVERY_INTROSPECTION_CUSTOM = "policy.discovery.introspection";
	public static final String POLICY_DISCOVERY_USERINFO_CUSTOM = "policy.discovery.userInfo";
	
	public static final String POLICY_ENDPOINT_HTTPS_STATO = "policy.endpoint.https.stato";
	public static final String POLICY_ENDPOINT_PROXY_STATO = "policy.endpoint.proxy.stato";
	public static final String POLICY_ENDPOINT_CONFIG = "endpointConfig";
	public static final String POLICY_ENDPOINT_SSL_CONFIG = "sslConfig";
	public static final String POLICY_ENDPOINT_SSL_CLIENT_CONFIG = "sslClientConfig";
	
	public static final String POLICY_VALIDAZIONE_STATO = "policy.validazioneJWT.stato";
	public static final String POLICY_VALIDAZIONE_SAVE_ERROR_IN_CACHE = "policy.validazioneJWT.saveErrorInCache";
	public static final String POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID = SecurityConstants.SIGNATURE_VERIFICATION_PROPERTY_REF_ID;
	public static final String POLICY_VALIDAZIONE_JWE_DECRYPT_PROP_REF_ID = SecurityConstants.DECRYPTION_PROPERTY_REF_ID;
	public static final String POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE = CostantiConfigurazione.POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE; 
	public static final String POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE_CUSTOM = CostantiConfigurazione.POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE_CUSTOM;
	public static final String POLICY_VALIDAZIONE_CLAIMS_PARSER_CLASS_NAME = CostantiConfigurazione.POLICY_VALIDAZIONE_CLAIMS_PARSER_CLASS_NAME;
	public static final String POLICY_VALIDAZIONE_CLAIMS_PARSER_PLUGIN_TYPE = CostantiConfigurazione.POLICY_VALIDAZIONE_CLAIMS_PARSER_PLUGIN_TYPE; 
	
	public static final String POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5C = "##useX5C##";
	public static final String POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5T = "##useX5T##";
	public static final String POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5C_X5T = "##useX5C-X5T##";

	public static final String POLICY_REQUEST_TOKEN_POSITION_AUTHORIZATION = "authorization";
	public static final String POLICY_REQUEST_TOKEN_POSITION_HEADER = "header";
	public static final String POLICY_REQUEST_TOKEN_POSITION_URL = "url";
	public static final String POLICY_REQUEST_TOKEN_POSITION_FORM = "form";
	
	public static final String POLICY_VALIDAZIONE_JWS_HEADER = "policy.validazioneJWT.header";
	public static final String POLICY_VALIDAZIONE_JWS_HEADER_TYP = "policy.validazioneJWT.header.typ";
	public static final String POLICY_VALIDAZIONE_JWS_HEADER_CTY = "policy.validazioneJWT.header.cty";
	public static final String POLICY_VALIDAZIONE_JWS_HEADER_ALG = "policy.validazioneJWT.header.alg";
	
	public static final String POLICY_INTROSPECTION_STATO = "policy.introspection.stato";
	public static final String POLICY_INTROSPECTION_SAVE_ERROR_IN_CACHE = "policy.introspection.saveErrorInCache";
	public static final String POLICY_INTROSPECTION_URL = "policy.introspection.endpoint.url";
	public static final String POLICY_INTROSPECTION_TIPO = "policy.introspection.tipo";
	public static final String POLICY_INTROSPECTION_HTTP_METHOD = "policy.introspection.httpMethod";
	public static final String POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION = "policy.introspection.requestTokenPosition";
	public static final String POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_HEADER_NAME = "policy.introspection.requestTokenPosition.header";
	public static final String POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_URL_PROPERTY_NAME = "policy.introspection.requestTokenPosition.queryParameter";
	public static final String POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_FORM_PROPERTY_NAME = "policy.introspection.requestTokenPosition.formParameter";
	public static final String POLICY_INTROSPECTION_CONTENT_TYPE = "policy.introspection.contentType";	
	public static final String POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE = CostantiConfigurazione.POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE; 
	public static final String POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE_CUSTOM = CostantiConfigurazione.POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE_CUSTOM; 
	public static final String POLICY_INTROSPECTION_CLAIMS_PARSER_CLASS_NAME = CostantiConfigurazione.POLICY_INTROSPECTION_CLAIMS_PARSER_CLASS_NAME; 
	public static final String POLICY_INTROSPECTION_CLAIMS_PARSER_PLUGIN_TYPE = CostantiConfigurazione.POLICY_INTROSPECTION_CLAIMS_PARSER_PLUGIN_TYPE; 
	public static final String POLICY_INTROSPECTION_AUTH_BASIC_STATO = "policy.introspection.endpoint.basic.stato";
	public static final String POLICY_INTROSPECTION_AUTH_BASIC_USERNAME = "policy.introspection.endpoint.basic.username";
	public static final String POLICY_INTROSPECTION_AUTH_BASIC_PASSWORD = "policy.introspection.endpoint.basic.password";
	public static final String POLICY_INTROSPECTION_AUTH_BEARER_STATO = "policy.introspection.endpoint.bearer.stato";
	public static final String POLICY_INTROSPECTION_AUTH_BEARER_TOKEN = "policy.introspection.endpoint.bearer.token";
	public static final String POLICY_INTROSPECTION_AUTH_SSL_STATO = "policy.introspection.endpoint.https.stato";
	
	public static final String POLICY_USER_INFO_STATO = "policy.userInfo.stato";
	public static final String POLICY_USER_INFO_SAVE_ERROR_IN_CACHE = "policy.userInfo.saveErrorInCache";
	public static final String POLICY_USER_INFO_URL = "policy.userInfo.endpoint.url";
	public static final String POLICY_USER_INFO_TIPO = "policy.userInfo.tipo";
	public static final String POLICY_USER_INFO_HTTP_METHOD = "policy.userInfo.httpMethod";
	public static final String POLICY_USER_INFO_REQUEST_TOKEN_POSITION = "policy.userInfo.requestTokenPosition";
	public static final String POLICY_USER_INFO_REQUEST_TOKEN_POSITION_HEADER_NAME = "policy.userInfo.requestTokenPosition.header";
	public static final String POLICY_USER_INFO_REQUEST_TOKEN_POSITION_URL_PROPERTY_NAME = "policy.userInfo.requestTokenPosition.queryParameter";
	public static final String POLICY_USER_INFO_REQUEST_TOKEN_POSITION_FORM_PROPERTY_NAME = "policy.userInfo.requestTokenPosition.formParameter";
	public static final String POLICY_USER_INFO_CONTENT_TYPE = "policy.userInfo.contentType";	
	public static final String POLICY_USER_INFO_CLAIMS_PARSER_TYPE = CostantiConfigurazione.POLICY_USER_INFO_CLAIMS_PARSER_TYPE; 
	public static final String POLICY_USER_INFO_CLAIMS_PARSER_TYPE_CUSTOM = CostantiConfigurazione.POLICY_USER_INFO_CLAIMS_PARSER_TYPE_CUSTOM; 
	public static final String POLICY_USER_INFO_CLAIMS_PARSER_CLASS_NAME = CostantiConfigurazione.POLICY_USER_INFO_CLAIMS_PARSER_CLASS_NAME; 
	public static final String POLICY_USER_INFO_CLAIMS_PARSER_PLUGIN_TYPE = CostantiConfigurazione.POLICY_USER_INFO_CLAIMS_PARSER_PLUGIN_TYPE; 
	public static final String POLICY_USER_INFO_AUTH_BASIC_STATO = "policy.userInfo.endpoint.basic.stato";
	public static final String POLICY_USER_INFO_AUTH_BASIC_USERNAME = "policy.userInfo.endpoint.basic.username";
	public static final String POLICY_USER_INFO_AUTH_BASIC_PASSWORD = "policy.userInfo.endpoint.basic.password";
	public static final String POLICY_USER_INFO_AUTH_BEARER_STATO = "policy.userInfo.endpoint.bearer.stato";
	public static final String POLICY_USER_INFO_AUTH_BEARER_TOKEN = "policy.userInfo.endpoint.bearer.token";
	public static final String POLICY_USER_INFO_AUTH_SSL_STATO = "policy.userInfo.endpoint.https.stato";
	
	public static final String POLICY_TOKEN_FORWARD_STATO = "policy.tokenForward.stato";
	
	public static final String POLICY_TOKEN_FORWARD_TRASPARENTE_STATO = "policy.tokenForward.trasparente.stato";
	public static final String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE = "policy.tokenForward.trasparente.mode";
	public static final String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_AS_RECEIVED = "asReceived";
	public static final String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_HEADER = "RFC6750_header";
	public static final String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_URL = "RFC6750_url";
	public static final String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER = "CUSTOM_header";
	public static final String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL = "CUSTOM_url";
	public static final String LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_AS_RECEIVED = "Come è stato ricevuto";
	public static final String LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_AS_RECEIVED_ORIGINALE = "Token Originale";
	public static final String LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_HEADER = "Header HTTP 'Authorization Bearer'";
	public static final String LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_URL = "Parametro URL 'access_token'";
	public static final String LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER = "Header HTTP ''{0}''";
	public static final String LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL = "Parametro URL ''{0}''";
	public static final String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER_NAME = "policy.tokenForward.trasparente.mode.header";
	public static final String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL_PARAMETER_NAME = "policy.tokenForward.trasparente.mode.queryParameter";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_STATO = "policy.tokenForward.infoRaccolte.stato";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE = "policy.tokenForward.infoRaccolte.mode";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_HEADERS = "op2header";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JSON = "op2json";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS = "op2jws";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS = "jws";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE = "jwe";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON = "json";
	public static final String LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_HEADERS = "GovWay Headers";
	public static final String LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JSON = "GovWay JSON";
	public static final String LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS = "GovWay JWS";
	public static final String LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS = "JWS";
	public static final String LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE = "JWE";
	public static final String LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON = "JSON";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_ENCODE_BASE64 = "policy.tokenForward.infoRaccolte.base64";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER = "CUSTOM_header";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_URL = "CUSTOM_url";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT = "policy.tokenForward.infoRaccolte.validazioneJWT";	
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE = "policy.tokenForward.infoRaccolte.validazioneJWT.mode";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE_HEADER_NAME = "policy.tokenForward.infoRaccolte.validazioneJWT.mode.header";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE_URL_PARAMETER_NAME = "policy.tokenForward.infoRaccolte.validazioneJWT.mode.queryParameter";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION = "policy.tokenForward.infoRaccolte.introspection";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE = "policy.tokenForward.infoRaccolte.introspection.mode";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE_HEADER_NAME = "policy.tokenForward.infoRaccolte.introspection.mode.header";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE_URL_PARAMETER_NAME = "policy.tokenForward.infoRaccolte.introspection.mode.queryParameter";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO = "policy.tokenForward.infoRaccolte.userInfo";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE = "policy.tokenForward.infoRaccolte.userInfo.mode";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE_HEADER_NAME = "policy.tokenForward.infoRaccolte.userInfo.mode.header";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE_URL_PARAMETER_NAME = "policy.tokenForward.infoRaccolte.userInfo.mode.queryParameter";
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_SIGNATURE_PROP_REF_ID = SecurityConstants.SIGNATURE_PROPERTY_REF_ID;
	public static final String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_ENCRYP_PROP_REF_ID = SecurityConstants.ENCRYPTION_PROPERTY_REF_ID;
	
	public static final String POLICY_RETRIEVE_TOKEN_PARSER_TYPE = "policy.retrieveToken.claimsParser";
	public static final String POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM = CostantiConfigurazione.POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM; 
	public static final String POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM_CYSTOM = CostantiConfigurazione.POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM_CYSTOM; 
	public static final String POLICY_RETRIEVE_TOKEN_PARSER_CLASS_NAME = CostantiConfigurazione.POLICY_RETRIEVE_TOKEN_PARSER_CLASS_NAME; 
	public static final String POLICY_RETRIEVE_TOKEN_PARSER_PLUGIN_TYPE = CostantiConfigurazione.POLICY_RETRIEVE_TOKEN_PARSER_PLUGIN_TYPE; 
	public static final String POLICY_RETRIEVE_TOKEN_MODE = "policy.retrieveToken.mode";
	public static final String POLICY_RETRIEVE_TOKEN_MODE_PDND = "policy.retrieveToken.jwt.pdnd";
	public static final String POLICY_RETRIEVE_TOKEN_URL = "policy.retrieveToken.endpoint.url";
	public static final String POLICY_RETRIEVE_TOKEN_RESPONSE_TYPE = "policy.retrieveToken.responseType";
	public static final String POLICY_RETRIEVE_TOKEN_AUTH_BASIC_STATO = "policy.retrieveToken.endpoint.basic.stato";
	public static final String POLICY_RETRIEVE_TOKEN_AUTH_BASIC_USERNAME = "policy.retrieveToken.endpoint.basic.username";
	public static final String POLICY_RETRIEVE_TOKEN_AUTH_BASIC_PASSWORD = "policy.retrieveToken.endpoint.basic.password";
	public static final String POLICY_RETRIEVE_TOKEN_AUTH_BASIC_AS_AUTHORIZATION_HEADER = "policy.retrieveToken.endpoint.basic.asAuthorizationHeader";
	public static final String POLICY_RETRIEVE_TOKEN_AUTH_BEARER_STATO = "policy.retrieveToken.endpoint.bearer.stato";
	public static final String POLICY_RETRIEVE_TOKEN_AUTH_BEARER_TOKEN = "policy.retrieveToken.endpoint.bearer.token";
	public static final String POLICY_RETRIEVE_TOKEN_AUTH_SSL_STATO = "policy.retrieveToken.endpoint.https.stato";
	public static final String POLICY_RETRIEVE_TOKEN_USERNAME = "policy.retrieveToken.username";
	public static final String POLICY_RETRIEVE_TOKEN_PASSWORD = "policy.retrieveToken.password";
	public static final String POLICY_RETRIEVE_TOKEN_SCOPES = "policy.retrieveToken.scope";
	public static final String POLICY_RETRIEVE_TOKEN_AUDIENCE = "policy.retrieveToken.audience";
	public static final String POLICY_RETRIEVE_TOKEN_FORM_CLIENT_ID = "policy.retrieveToken.formClientId";
	public static final String POLICY_RETRIEVE_TOKEN_FORM_CLIENT_ID_MODE = "policy.retrieveToken.formClientId.mode";
	public static final String POLICY_RETRIEVE_TOKEN_FORM_RESOURCE = "policy.retrieveToken.formResource";
	public static final String POLICY_RETRIEVE_TOKEN_FORM_PARAMETERS = "policy.retrieveToken.formParameters";
	public static final String POLICY_RETRIEVE_TOKEN_HTTP_METHOD = "policy.retrieveToken.httpMethod";
	public static final String POLICY_RETRIEVE_TOKEN_HTTP_CONTENT_TYPE = "policy.retrieveToken.httpContentType";
	public static final String POLICY_RETRIEVE_TOKEN_HTTP_HEADERS = "policy.retrieveToken.httpHeaders";
	public static final String POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD_TEMPLATE_TYPE = "policy.retrieveToken.httpPayloadTemplateType";
	public static final String POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD_TEMPLATE_TYPE_TEMPLATE = "template";
	public static final String POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD_TEMPLATE_TYPE_FREEMARKER_TEMPLATE = "freemarker-template";
	public static final String POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD_TEMPLATE_TYPE_VELOCITY_TEMPLATE = "velocity-template";
	public static final String POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD = "policy.retrieveToken.httpPayload";
	public static final String POLICY_RETRIEVE_TOKEN_SAVE_ERROR_IN_CACHE = "policy.retrieveToken.saveErrorInCache";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_CLIENT_ID_MODE= "policy.retrieveToken.jwt.clientId.mode";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_CLIENT_ID= "policy.retrieveToken.jwt.clientId";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_CLIENT_SECRET= "policy.retrieveToken.jwt.clientSecret";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_ISSUER= "policy.retrieveToken.jwt.issuer";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_ISSUER_MODE= "policy.retrieveToken.jwt.issuer.mode";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SUBJECT= "policy.retrieveToken.jwt.subject";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SUBJECT_MODE= "policy.retrieveToken.jwt.subject.mode";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_IDENTIFIER= "policy.retrieveToken.jwt.jti";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_AUDIENCE= "policy.retrieveToken.jwt.audience";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_EXPIRED_TTL_SECONDS= "policy.retrieveToken.jwt.expired";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_EXPIRED_TTL_SECONDS_DEFAULT_VALUE = "300";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_CLAIMS = "policy.retrieveToken.jwt.claims";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_PURPOSE_ID = "policy.retrieveToken.jwt.purposeId";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SESSION_INFO = "policy.retrieveToken.jwt.sessionInfo";
	
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_ALGORITHM= "policy.retrieveToken.jwt.signature.algorithm";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID = "policy.retrieveToken.jwt.signature.include.key.id";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_NOT_PRESENT = "false"; // per backward compatibility
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_ALIAS = "true"; // per backward compatibility
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_CLIENT_ID = "client_id";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_CUSTOM = "custom";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_APPLICATIVO_MODI = CHOICE_APPLICATIVO_MODI_VALUE ;
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_FRUIZIONE_MODI = CHOICE_FRUIZIONE_MODI_VALUE ;
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_VALUE = "policy.retrieveToken.jwt.signature.include.key.id.value";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_CERT = "policy.retrieveToken.jwt.signature.include.cert";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_URL = "policy.retrieveToken.jwt.signature.include.x509url";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_SHA1 = "policy.retrieveToken.jwt.signature.include.cert.sha1";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_SHA256 = "policy.retrieveToken.jwt.signature.include.cert.sha256";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_JOSE_CONTENT_TYPE = "policy.retrieveToken.jwt.signature.joseContentType";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_JOSE_TYPE = "policy.retrieveToken.jwt.signature.joseType";
	
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_TYPE= "policy.retrieveToken.jwt.signature.keystoreType";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_FILE= "policy.retrieveToken.jwt.signature.keystoreFile";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_FILE_PUBLIC_KEY= "policy.retrieveToken.jwt.signature.keystoreFilePublicKey";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYPAIR_ALGORITHM= "policy.retrieveToken.jwt.signature.keyPairAlgorithm";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_PASSWORD= "policy.retrieveToken.jwt.signature.keystorePassword";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEY_ALIAS= "policy.retrieveToken.jwt.signature.keyAlias";
	public static final String POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEY_PASSWORD= "policy.retrieveToken.jwt.signature.keyPassword";
	
	public static final String POLICY_RETRIEVE_TOKEN_FORWARD_MODE = "policy.tokenForward.mode";
	public static final String POLICY_RETRIEVE_TOKEN_FORWARD_MODE_RFC6750_HEADER = "RFC6750_header";
	public static final String POLICY_RETRIEVE_TOKEN_FORWARD_MODE_RFC6750_URL = "RFC6750_url";
	public static final String POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_HEADER = "CUSTOM_header";
	public static final String POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_URL = "CUSTOM_url";
	public static final String POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_HEADER_NAME = "policy.tokenForward.mode.header";
	public static final String POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_URL_PARAMETER_NAME = "policy.tokenForward.mode.queryParameter";

	
	// VALORE VUOTO
	
	public static final String POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED = "${undefined}";
	
	
	// VALORE KEYSTORE MODI
	    	
    public static final String KEYSTORE_TYPE_APPLICATIVO_MODI_VALUE = "applicativoModi";
    public static final String KEYSTORE_TYPE_APPLICATIVO_MODI_LABEL = "Definito nell'applicativo ModI";
    
    public static final String KEYSTORE_TYPE_FRUIZIONE_MODI_VALUE = "fruizioneModi";
    public static final String KEYSTORE_TYPE_FRUIZIONE_MODI_LABEL = "Definito nella fruizione ModI";
	
	
	// CLAIMS PDND
	
	public static final String PDND_PURPOSE_ID = "purposeId";
	public static final String PDND_SESSION_INFO = "sessionInfo";
	
	public static final String PDND_DNONCE = "dnonce";
	
	public static final String PDND_DIGEST = "digest";
	public static final String PDND_DIGEST_ALG = "alg";
	public static final String PDND_DIGEST_ALG_DEFAULT_VALUE = "SHA256";
	public static final String PDND_DIGEST_VALUE = "value";
	
	public static final String PDND_OAUTH2_RFC_6749_REQUEST_CLIENT_ID = "client_id";
	public static final String PDND_OAUTH2_RFC_6749_REQUEST_RESOURCE = "resource";
	
	
	// STANDARD
	
	public static final String RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN = "access_token";
	public static final String RFC6750_FORM_PARAMETER_ACCESS_TOKEN = "access_token";
	
	// ELEMENTI ID 
	
	public static final String ID_RETRIEVE_ENDPOINT_URL = "endpointURL";
	public static final String ID_RETRIEVE_AUTENTICAZIONE_USERNAME = "autenticazioneUsername";
	public static final String ID_RETRIEVE_AUTENTICAZIONE_PASSWORD = "autenticazionePassword";
	public static final String ID_RETRIEVE_CLIENT_ID = "autenticazioneEndpointBasicUsername";
	public static final String ID_RETRIEVE_CLIENT_ID_CUSTOM = "autenticazioneEndpointBasicUsernameCustom";
	public static final String ID_RETRIEVE_CLIENT_SECRET = "autenticazioneEndpointBasicPassword";
	public static final String ID_RETRIEVE_CLIENT_SECRET_CUSTOM = "autenticazioneEndpointBasicPasswordCustom";
	public static final String ID_RETRIEVE_BEARER_TOKEN = "autenticazioneEndpointBearerToken";
	public static final String ID_RETRIEVE_JWT_KID_VALUE = "jwtSignatureKidValue";
	public static final String ID_RETRIEVE_JWT_X5U = "jwtSignatureIncludeCertModeX5U";
	public static final String ID_RETRIEVE_JWT_CLIENT_ID_APPLICATIVO_MODI_CUSTOM = "jwtClientIdApplicativoModiChoiceInput";
	public static final String ID_RETRIEVE_JWT_CLIENT_ID = "jwtClientId";
	public static final String ID_RETRIEVE_JWT_AUDIENCE = "jwtAudience";
	public static final String ID_RETRIEVE_JWT_ISSUER = "jwtIssuer";
	public static final String ID_RETRIEVE_JWT_ISSUER_APPLICATIVO_MODI_CUSTOM = "jwtIssuerApplicativoModiChoiceInput";
	public static final String ID_RETRIEVE_JWT_SUBJECT = "jwtSubject";
	public static final String ID_RETRIEVE_JWT_SUBJECT_APPLICATIVO_MODI_CUSTOM = "jwtSubjectApplicativoModiChoiceInput";
	public static final String ID_RETRIEVE_JWT_IDENTIFIER = "jwtIdentifier";
	public static final String ID_RETRIEVE_JWT_CLAIMS = "jwtPayloadClaims";
	public static final String ID_RETRIEVE_JWT_PURPOSE_ID = "jwtPurposeID";
	public static final String ID_RETRIEVE_JWT_SESSION_INFO = "jwtSessionInfo";
	public static final String ID_RETRIEVE_SCOPE = "scope";
	public static final String ID_RETRIEVE_AUDIENCE = "audience";
	public static final String ID_RETRIEVE_FORM_CLIENT_ID = "formClientId";
	public static final String ID_RETRIEVE_FORM_CLIENT_ID_APPLICATIVO_MODI_CUSTOM = "formClientIdApplicativoModiChoiceInput";
	public static final String ID_RETRIEVE_FORM_RESOURCE = "formResource";
	public static final String ID_RETRIEVE_FORM_PARAMETERS = "formParameters";
	public static final String ID_RETRIEVE_HTTP_METHOD = "httpMethod";
	public static final String ID_RETRIEVE_HTTP_METHOD_PAYLOAD_DEFINED = "httpMethodPayloadDefined";
	public static final String ID_RETRIEVE_HTTP_CONTENT_TYPE = "httpContentType";
	public static final String ID_RETRIEVE_HTTP_HEADERS = "httpHeaders";
	public static final String ID_RETRIEVE_HTTP_PAYLOAD_TEMPLATE_TYPE = "httpPayloadTemplateType";
	public static final String ID_RETRIEVE_HTTP_TEMPLATE_PAYLOAD = "httpTemplatePayload";
	public static final String ID_RETRIEVE_HTTP_FREEMARKER_PAYLOAD = "httpFreemarkerPayload";
	public static final String ID_RETRIEVE_HTTP_VELOCITY_PAYLOAD = "httpVelocityPayload";
	
	
	// ELEMENTI SELECT
	
	public static final String ID_RETRIEVE_TOKEN_METHOD = "retrieveTokenMethod";
	public static final String ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL = "clientCredentials";
	public static final String ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD = "usernamePassword";
	public static final String ID_RETRIEVE_TOKEN_METHOD_RFC_7523_X509 = "rfc7523_x509";
	public static final String ID_RETRIEVE_TOKEN_METHOD_RFC_7523_CLIENT_SECRET = "rfc7523_clientSecret";
	public static final String ID_RETRIEVE_TOKEN_METHOD_CUSTOM = "custom";
	public static final String ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL_LABEL = "Client Credentials";
	public static final String ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD_LABEL = "Resource Owner Password Credentials";
	public static final String ID_RETRIEVE_TOKEN_METHOD_RFC_7523_X509_LABEL = "Signed JWT";
	public static final String ID_RETRIEVE_TOKEN_METHOD_RFC_7523_CLIENT_SECRET_LABEL = "Signed JWT with Client Secret";
	public static final String ID_RETRIEVE_TOKEN_METHOD_CUSTOM_LABEL = "Personalizzato";
	public static final String ID_RETRIEVE_TOKEN_JWT_EXPIRED_TTL_SECONDS= "jwtExpTtl";
	public static final String ID_RETRIEVE_TOKEN_JWT_SYMMETRIC_SIGN_ALGORITHM = "jwtSymmetricSignatureAlgorithm";
	public static final String ID_RETRIEVE_TOKEN_JWT_ASYMMETRIC_SIGN_ALGORITHM = "jwtAsymmetricSignatureAlgorithm";
	
	public static final String ID_INTROSPECTION_HTTP_METHOD = "introspectionHttpMethod";
	
	public static final String ID_USER_INFO_HTTP_METHOD = "userInfoHttpMethod";
	
	public static final String ID_TIPOLOGIA_HTTPS = "endpointHttpsTipologia";
	
	public static final String ID_JWS_SIGNATURE_ALGORITHM = "tokenForwardInfoRaccolteModeJWSSignature";
	
	public static final String ID_JWS_ENCRYPT_KEY_ALGORITHM = "tokenForwardInfoRaccolteModeJWEKeyAlgorithm";
	
	public static final String ID_JWS_ENCRYPT_CONTENT_ALGORITHM = "tokenForwardInfoRaccolteModeJWEContentAlgorithm";
	
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE = "validazioneJwtTruststoreType";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_FILE = "validazioneJwtTruststoreFile";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_PASSWORD = "validazioneJwtTruststorePassword";
	
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE = "validazioneJwtTruststoreTypeSelectCertificate";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_ALIAS = "alias";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_ALIAS = "Alias in TrustStore";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_ALIAS = "Per la validazione viene utilizzato il certificato nel truststore corrispondente all'alias indicato";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C = "x5c";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5C = "Certificate 'x5c' in Token";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_X5C = "Per la validazione viene utilizzato il certificato presente nel token, dopo averlo validato rispetto al truststore";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5T256 = "x5t#256";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5T256 = "SHA-256 Thumbprint 'x5t#256' in Token";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_X5T256 = "Per la validazione viene utilizzato il certificato nel truststore corrispondente al thumbprint presente nel token";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C_X5T256 = "x5c_x5t#256";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5C_X5T256 = "Certificate 'x5c' o SHA-256 Thumbprint 'x5t#256' in Token";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_X5C_X5T256 = "Per la validazione viene utilizzato il certificato presente nel token o recuperato dal truststore rispetto al thumbprint";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5U = "x5u";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5U = "URL Certificate 'x5u' in Token";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_X5U = "Per la validazione viene recuperato il certificato riferito dalla URL presente nel token e validato rispetto al truststore";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_KID = "kid";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_KID = "Key ID 'kid' in Token";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_KID = "Per la validazione viene utilizzato il certificato nel truststore con alias corrispondente al 'kid' presente nel token";
	private static final List<String> ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES = new ArrayList<>();
	public static List<String> getIdValidazioneJwtTruststoreTypeSelectCertificateValues() {
		return ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES;
	}
	static {
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_ALIAS);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5T256);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C_X5T256);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_KID);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5U);
	}
	private static final List<String> ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS = new ArrayList<>();
	public static List<String> getIdValidazioneJwtTruststoreTypeSelectCertificateLabels() {
		return ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS;
	}
	static {
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_ALIAS);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5C);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5T256);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5C_X5T256);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_KID);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5U);
	}
	
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY = "validazioneJwtTruststoreTypeSelectJWKPublicKey";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUE_ALIAS = "alias";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABEL_ALIAS = "Alias in TrustStore";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_NOTE_ALIAS = "Per la validazione viene utilizzata la chiave pubblica nel truststore JWKs corrispondente al kid indicato";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUE_KID = "kid";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABEL_KID = "Key ID 'kid' in Token";
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_NOTE_KID = "Per la validazione viene utilizzata la chiave pubblica nel truststore JWKs corrispondente al 'kid' presente nel token";
	private static final List<String> ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUES = new ArrayList<>();
	public static List<String> getIdValidazioneJwtTruststoreTypeSelectJwkPublicKeyValues() {
		return ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUES;
	}
	static {
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUE_ALIAS);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUE_KID);
	}
	private static final List<String> ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABELS = new ArrayList<>();
	public static List<String> getIdValidazioneJwtTruststoreTypeSelectJwkPublicKeyLabels() {
		return ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABELS;
	}
	static {
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABEL_ALIAS);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABEL_KID);
	}
	
	public static final String ID_VALIDAZIONE_JWT_TRUSTSTORE_OCSP_POLICY = "validazioneJwtTruststoreOcspPolicy";
	
	public static final String ID_VALIDAZIONE_JWT_KEYSTORE_TYPE = "validazioneJwtKeystoreType";
	public static final String ID_VALIDAZIONE_JWT_KEYSTORE_FILE = "validazioneJwtKeystoreFile";
	public static final String ID_VALIDAZIONE_JWT_KEYSTORE_PASSWORD = "validazioneJwtKeystorePassword";
	public static final String ID_VALIDAZIONE_JWT_KEYSTORE_PASSWORD_PRIVATE_KEY = "validazioneJwtKeystorePrivateKeyPassword";
	
	public static final String ID_HTTPS_TRUSTSTORE_TYPE = "endpointHttpsTruststoreType";
	public static final String ID_HTTPS_TRUSTSTORE_FILE = "endpointHttpsTruststoreFile";
	public static final String ID_HTTPS_TRUSTSTORE_PASSWORD = "endpointHttpsTruststorePassword";
	
	public static final String ID_HTTPS_KEYSTORE_TYPE = "endpointHttpsClientKeystoreType";
	public static final String ID_HTTPS_KEYSTORE_FILE = "endpointHttpsClientKeystoreFile";
	public static final String ID_HTTPS_KEYSTORE_PASSWORD = "endpointHttpsClientKeystorePassword";
	public static final String ID_HTTPS_KEYSTORE_PASSWORD_PRIVATE_KEY = "endpointHttpsClientPasswordChiavePrivata";
	
	public static final String ID_TOKEN_FORWARD_JWS_KEYSTORE_TYPE = "tokenForwardInfoRaccolteModeJWSKeystoreType";
	public static final String ID_TOKEN_FORWARD_JWS_KEYSTORE_FILE = "tokenForwardInfoRaccolteModeJWSKeystoreFile";
	public static final String ID_TOKEN_FORWARD_JWS_KEYSTORE_PASSWORD = "tokenForwardInfoRaccolteModeJWSKeystorePassword";
	public static final String ID_TOKEN_FORWARD_JWS_KEYSTORE_PASSWORD_PRIVATE_KEY = "tokenForwardInfoRaccolteModeJWSKeystorePrivateKeyPassword";
	
	public static final String ID_TOKEN_FORWARD_JWE_KEYSTORE_TYPE = "tokenForwardInfoRaccolteModeJWEContentKeystoreType";
	public static final String ID_TOKEN_FORWARD_JWE_KEYSTORE_FILE = "tokenForwardInfoRaccolteModeJWEContentKeystoreFile";
	public static final String ID_TOKEN_FORWARD_JWE_KEYSTORE_PASSWORD = "tokenForwardInfoRaccolteModeJWEContentKeystorePassword";
	public static final String ID_TOKEN_FORWARD_JWE_KEYSTORE_PASSWORD_PRIVATE_KEY = "tokenForwardInfoRaccolteModeJWEContentKeystorePrivateKeyPassword";
	
	public static final String ID_DYNAMIC_DISCOVERY_CUSTOM_PARSER_PLUGIN_CLASSNAME = "discoveryParserCustom";
	public static final String ID_VALIDAZIONE_JWT_CUSTOM_PARSER_PLUGIN_CLASSNAME = "validazioneJwtParserCustom";
	public static final String ID_INTROSPECTION_CUSTOM_PARSER_PLUGIN_CLASSNAME = "introspectionParserCustom";
	public static final String ID_USER_INFO_CUSTOM_PARSER_PLUGIN_CLASSNAME = "userInfoParserCustom";
	
	public static final String ID_DYNAMIC_DISCOVERY_CUSTOM_PARSER_PLUGIN_CHOICE = "discoveryParserCustomPluginChoice";
	public static final String ID_VALIDAZIONE_JWT_CUSTOM_PARSER_PLUGIN_CHOICE = "validazioneJwtParserCustomPluginChoice";
	public static final String ID_INTROSPECTION_CUSTOM_PARSER_PLUGIN_CHOICE = "introspectionParserCustomPluginChoice";
	public static final String ID_USER_INFO_CUSTOM_PARSER_PLUGIN_CHOICE = "userInfoParserCustomPluginChoice";
	
	public static final String ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE = "jwtKeystoreType";
	public static final String ID_NEGOZIAZIONE_JWT_KEYSTORE_FILE = "jwtKeystoreFile";
	public static final String ID_NEGOZIAZIONE_JWT_KEYSTORE_FILE_PRIVATE_KEY = "jwtKeystorePrivateKey";
	public static final String ID_NEGOZIAZIONE_JWT_KEYSTORE_FILE_PUBLIC_KEY = "jwtKeystorePublicKey";
	public static final String ID_NEGOZIAZIONE_JWT_KEYSTORE_PASSWORD = "jwtKeystorePassword";
	public static final String ID_NEGOZIAZIONE_JWT_KEYSTORE_ALIAS_PRIVATE_KEY = "jwtAliasChiavePrivata";
	public static final String ID_NEGOZIAZIONE_JWT_KEYSTORE_PASSWORD_PRIVATE_KEY = "jwtPasswordChiavePrivata";
	public static final String ID_NEGOZIAZIONE_JWT_KEYSTORE_PASSWORD_PRIVATE_KEY_OPZIONALE = "jwtPasswordChiavePrivataOpzionale";
		
	public static final String ID_NEGOZIAZIONE_CUSTOM_PARSER_PLUGIN_CLASSNAME = "customTokenParserCustomPlugin";
	public static final String ID_NEGOZIAZIONE_CUSTOM_PARSER_PLUGIN_CHOICE = "customTokenParserCustomPluginChoice";
	
	public static final String ID_AA_JWS_KEYSTORE_TYPE = "aaJWSKeystoreType";
	public static final String ID_AA_JWS_KEYSTORE_FILE = "aaJWSKeystoreFile";
	public static final String ID_AA_JWS_KEYSTORE_PASSWORD = "aaJWSKeystorePassword";
	public static final String ID_AA_JWS_KEYSTORE_PASSWORD_PRIVATE_KEY = "aaJWSKeystorePrivateKeyPassword";
	
	public static final String ID_AA_JWS_TRUSTSTORE_TYPE = "aaJWSTruststoreType";
	public static final String ID_AA_JWS_TRUSTSTORE_FILE = "aaJWSTruststoreFile";
	public static final String ID_AA_JWS_TRUSTSTORE_PASSWORD = "aaJWSTruststorePassword";
	public static final String ID_AA_JWS_TRUSTSTORE_TYPE_SELECT_CERTIFICATE = "aaJWSTruststoreTypeSelectCertificate";
	public static final String ID_AA_JWS_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY = "aaJWSTruststoreTypeSelectJWKPublicKey";
	public static final String ID_AA_JWS_TRUSTSTORE_OCSP_POLICY = "aaJWSTruststoreOcspPolicy";

}
