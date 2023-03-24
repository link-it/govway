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

	public final static String TIPOLOGIA = CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION;
	public final static String TIPOLOGIA_RETRIEVE = CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE;
	public final static String ATTRIBUTE_AUTHORITY = CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY;
	
	public static final String CHOICE_APPLICATIVO_MODI_VALUE = "applicativoModi";
	public static final String CHOICE_FRUIZIONE_MODI_VALUE = "fruizioneModi";
	
	
	// Context
		
	public final static MapKey<String> PDD_CONTEXT_TOKEN_REALM = Map.newMapKey("PDD_CONTEXT_TOKEN_REALM");
	public final static MapKey<String> PDD_CONTEXT_TOKEN_MESSAGE_ERROR_BODY_EMPTY = Map.newMapKey("PDD_CONTEXT_TOKEN_MESSAGE_ERROR_BODY_EMPTY");
	public final static MapKey<String> PDD_CONTEXT_TOKEN_MESSAGE_ERROR_GENERIC_MESSAGE = Map.newMapKey("PDD_CONTEXT_TOKEN_MESSAGE_ERROR_GENERIC_MESSAGE");
	public final static MapKey<String> PDD_CONTEXT_TOKEN_POSIZIONE = Map.newMapKey("TOKEN_POSIZIONE");
	public final static MapKey<String> PDD_CONTEXT_TOKEN_ESITO_VALIDAZIONE = Map.newMapKey("TOKEN_ESITO_VALIDAZIONE");
	public final static MapKey<String> PDD_CONTEXT_TOKEN_ESITO_INTROSPECTION = Map.newMapKey("TOKEN_ESITO_INTROSPECTION");
	public final static MapKey<String> PDD_CONTEXT_TOKEN_ESITO_USER_INFO = Map.newMapKey("TOKEN_ESITO_USER_INFO");
	public final static MapKey<String> PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE = Map.newMapKey("TOKEN_INFORMAZIONI_NORMALIZZATE");
	public final static MapKey<String> PDD_CONTEXT_ATTRIBUTI_INFORMAZIONI_NORMALIZZATE = Map.newMapKey("ATTRIBUTI_INFORMAZIONI_NORMALIZZATE");
	public final static MapKey<String> MSG_CONTEXT_TOKEN_FORWARD = Map.newMapKey("TOKEN_FORWARD"); // per salvarlo con il messaggio
	
	
	// Token Retrieve id
	
	public final static String RETRIEVE_TOKEN_PARSER_COLLECTION_ID = "retrieveTokenParserPropRefId";
	
	public final static String RETRIEVE_TOKEN_PARSER_TOKEN_TYPE = "token.parser.token_type";
	public final static String RETRIEVE_TOKEN_PARSER_ACCESS_TOKEN = "token.parser.access_token";
	public final static String RETRIEVE_TOKEN_PARSER_REFRESH_TOKEN = "token.parser.refresh_token";
	public final static String RETRIEVE_TOKEN_PARSER_SCOPE = "token.parser.scope";
	public final static String RETRIEVE_TOKEN_PARSER_EXPIRES_IN = "token.parser.expires_in";
	public final static String RETRIEVE_TOKEN_PARSER_EXPIRES_ON = "token.parser.expires_on";
	public final static String RETRIEVE_TOKEN_PARSER_REFRESH_EXPIRES_IN = "token.parser.refresh_expires_in";
	public final static String RETRIEVE_TOKEN_PARSER_REFRESH_EXPIRES_ON = "token.parser.refresh_expires_on";
	
	
	
	// Token Parser id
	
	public final static String VALIDAZIONE_JWT_TOKEN_PARSER_COLLECTION_ID = "validazioneJwtTokenParserPropRefId";
	public final static String INTROSPECTION_TOKEN_PARSER_COLLECTION_ID = "introspectionTokenParserPropRefId";
	public final static String USERINFO_TOKEN_PARSER_COLLECTION_ID = "userInfoTokenParserPropRefId";
	
	public final static String TOKEN_PARSER_ISSUER = "token.parser.issuer";
	public final static String TOKEN_PARSER_SUBJECT = "token.parser.subject";
	public final static String TOKEN_PARSER_AUDIENCE = "token.parser.audience";
	public final static String TOKEN_PARSER_EXPIRE = "token.parser.expire";
	public final static String TOKEN_PARSER_ISSUED_AT = "token.parser.issuedAt";
	public final static String TOKEN_PARSER_NOT_TO_BE_USED_BEFORE = "token.parser.notToBeUsedBefore";
	public final static String TOKEN_PARSER_JWT_IDENTIFIER = "token.parser.jwtIdentifier";
	public final static String TOKEN_PARSER_CLIENT_ID = "token.parser.clientId";
	public final static String TOKEN_PARSER_USERNAME = "token.parser.username";
	public final static String TOKEN_PARSER_SCOPE = "token.parser.scope";
	public final static String TOKEN_PARSER_ROLE = "token.parser.role";
	public final static String TOKEN_PARSER_USER_FULL_NAME = "token.parser.user.fullName";
	public final static String TOKEN_PARSER_USER_FIRST_NAME = "token.parser.user.firstName";
	public final static String TOKEN_PARSER_USER_MIDDLE_NAME = "token.parser.user.middleName";
	public final static String TOKEN_PARSER_USER_FAMILY_NAME = "token.parser.user.familyName";
	public final static String TOKEN_PARSER_USER_EMAIL = "token.parser.user.eMail";
	
	
	// Policy id
	
	public final static String GESTIONE_TOKEN_VALIDATION_ACTION_NONE = "NessunaValidazione";
	public final static String GESTIONE_TOKEN_VALIDATION_ACTION_JWT = "JWT";
	public final static String GESTIONE_TOKEN_VALIDATION_ACTION_INTROSPECTION = "Introspection";
	public final static String GESTIONE_TOKEN_VALIDATION_ACTION_USER_INFO = "UserInfo";
	
	public final static String GESTIONE_TOKEN_AUTENTICAZIONE_ISSUER="Issuer";
	public final static String GESTIONE_TOKEN_AUTENTICAZIONE_SUBJECT= "Subject";
	public final static String GESTIONE_TOKEN_AUTENTICAZIONE_CLIENT_ID="ClientId";
	public final static String GESTIONE_TOKEN_AUTENTICAZIONE_USERNAME="Username";
	public final static String GESTIONE_TOKEN_AUTENTICAZIONE_EMAIL="eMail";
	
	public final static String POLICY_REALM = "policy.realm";
	public final static String POLICY_MESSAGE_ERROR_BODY_EMPTY = "policy.messageError.bodyEmpty";
	public final static String POLICY_MESSAGE_ERROR_GENERIC_MESSAGE = "policy.messageError.genericMessage";
	
	public final static String POLICY_TOKEN_SOURCE = "policy.token.source";
	public final static String POLICY_TOKEN_SOURCE_RFC6750 = "RFC6750";
	public final static String POLICY_TOKEN_SOURCE_RFC6750_HEADER = "RFC6750_header";
	public final static String POLICY_TOKEN_SOURCE_RFC6750_FORM = "RFC6750_form";
	public final static String POLICY_TOKEN_SOURCE_RFC6750_URL = "RFC6750_url";
	public final static String POLICY_TOKEN_SOURCE_CUSTOM_HEADER = "CUSTOM_header";
	public final static String POLICY_TOKEN_SOURCE_CUSTOM_URL = "CUSTOM_url";
	public final static String POLICY_TOKEN_SOURCE_RFC6750_LABEL = "RFC 6750 - Bearer Token Usage";
	public final static String POLICY_TOKEN_SOURCE_RFC6750_HEADER_LABEL = "RFC 6750 - Bearer Token Usage (Authorization Request Header Field)\"";
	public final static String POLICY_TOKEN_SOURCE_RFC6750_FORM_LABEL = "RFC 6750 - Bearer Token Usage (Form-Encoded Body Parameter)";
	public final static String POLICY_TOKEN_SOURCE_RFC6750_URL_LABEL = "RFC 6750 - Bearer Token Usage (URI Query Parameter)";
	public final static String POLICY_TOKEN_SOURCE_CUSTOM_TEMPLATE_LABEL = "TEMPLATE";
	public final static String POLICY_TOKEN_SOURCE_CUSTOM_HEADER_LABEL = "Header HTTP '"+POLICY_TOKEN_SOURCE_CUSTOM_TEMPLATE_LABEL+"'";
	public final static String POLICY_TOKEN_SOURCE_CUSTOM_URL_LABEL = "Parametro URL '"+POLICY_TOKEN_SOURCE_CUSTOM_TEMPLATE_LABEL+"'";
	
	public final static String POLICY_TOKEN_SOURCE_CUSTOM_HEADER_NAME = "policy.token.source.header";
	public final static String POLICY_TOKEN_SOURCE_CUSTOM_URL_PROPERTY_NAME = "policy.token.source.queryParameter";
	
	public final static String POLICY_TOKEN_TYPE = "policy.token.type";
	public final static String POLICY_TOKEN_TYPE_OPAQUE = "opaque";
	public final static String POLICY_TOKEN_TYPE_JWS = "jws";
	public final static String POLICY_TOKEN_TYPE_JWE = "jwe";
	
	public final static String POLICY_STATO_ABILITATO = "true";
	public final static String POLICY_STATO_DISABILITATO = "false";
	
	public final static String POLICY_ENDPOINT_HTTPS_STATO = "policy.endpoint.https.stato";
	public final static String POLICY_ENDPOINT_PROXY_STATO = "policy.endpoint.proxy.stato";
	public final static String POLICY_ENDPOINT_CONFIG = "endpointConfig";
	public final static String POLICY_ENDPOINT_SSL_CONFIG = "sslConfig";
	public final static String POLICY_ENDPOINT_SSL_CLIENT_CONFIG = "sslClientConfig";
	
	public final static String POLICY_VALIDAZIONE_STATO = "policy.validazioneJWT.stato";
	public final static String POLICY_VALIDAZIONE_SAVE_ERROR_IN_CACHE = "policy.validazioneJWT.saveErrorInCache";
	public final static String POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID = SecurityConstants.SIGNATURE_VERIFICATION_PROPERTY_REF_ID;
	public final static String POLICY_VALIDAZIONE_JWE_DECRYPT_PROP_REF_ID = SecurityConstants.DECRYPTION_PROPERTY_REF_ID;
	public final static String POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE = CostantiConfigurazione.POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE; //"policy.validazioneJWT.claimsParser";
	public final static String POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE_CUSTOM = CostantiConfigurazione.POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE_CUSTOM; //"CUSTOM";
	public final static String POLICY_VALIDAZIONE_CLAIMS_PARSER_CLASS_NAME = CostantiConfigurazione.POLICY_VALIDAZIONE_CLAIMS_PARSER_CLASS_NAME; //"policy.validazioneJWT.claimsParser.className";
	public final static String POLICY_VALIDAZIONE_CLAIMS_PARSER_PLUGIN_TYPE = CostantiConfigurazione.POLICY_VALIDAZIONE_CLAIMS_PARSER_PLUGIN_TYPE; //"policy.validazioneJWT.claimsParser.pluginType";
	
	public final static String POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5C = "##useX5C##";
	public final static String POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5T = "##useX5T##";
	public final static String POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5C_X5T = "##useX5C-X5T##";

	public final static String POLICY_REQUEST_TOKEN_POSITION_AUTHORIZATION = "authorization";
	public final static String POLICY_REQUEST_TOKEN_POSITION_HEADER = "header";
	public final static String POLICY_REQUEST_TOKEN_POSITION_URL = "url";
	public final static String POLICY_REQUEST_TOKEN_POSITION_FORM = "form";
	
	public final static String POLICY_INTROSPECTION_STATO = "policy.introspection.stato";
	public final static String POLICY_INTROSPECTION_SAVE_ERROR_IN_CACHE = "policy.introspection.saveErrorInCache";
	public final static String POLICY_INTROSPECTION_URL = "policy.introspection.endpoint.url";
	public final static String POLICY_INTROSPECTION_TIPO = "policy.introspection.tipo";
	public final static String POLICY_INTROSPECTION_HTTP_METHOD = "policy.introspection.httpMethod";
	public final static String POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION = "policy.introspection.requestTokenPosition";
	public final static String POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_HEADER_NAME = "policy.introspection.requestTokenPosition.header";
	public final static String POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_URL_PROPERTY_NAME = "policy.introspection.requestTokenPosition.queryParameter";
	public final static String POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_FORM_PROPERTY_NAME = "policy.introspection.requestTokenPosition.formParameter";
	public final static String POLICY_INTROSPECTION_CONTENT_TYPE = "policy.introspection.contentType";	
	public final static String POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE = CostantiConfigurazione.POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE; //"policy.introspection.claimsParser";
	public final static String POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE_CUSTOM = CostantiConfigurazione.POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE_CUSTOM; //"CUSTOM";
	public final static String POLICY_INTROSPECTION_CLAIMS_PARSER_CLASS_NAME = CostantiConfigurazione.POLICY_INTROSPECTION_CLAIMS_PARSER_CLASS_NAME; //"policy.introspection.claimsParser.className";
	public final static String POLICY_INTROSPECTION_CLAIMS_PARSER_PLUGIN_TYPE = CostantiConfigurazione.POLICY_INTROSPECTION_CLAIMS_PARSER_PLUGIN_TYPE; //"policy.introspection.claimsParser.pluginType";
	public final static String POLICY_INTROSPECTION_AUTH_BASIC_STATO = "policy.introspection.endpoint.basic.stato";
	public final static String POLICY_INTROSPECTION_AUTH_BASIC_USERNAME = "policy.introspection.endpoint.basic.username";
	public final static String POLICY_INTROSPECTION_AUTH_BASIC_PASSWORD = "policy.introspection.endpoint.basic.password";
	public final static String POLICY_INTROSPECTION_AUTH_BEARER_STATO = "policy.introspection.endpoint.bearer.stato";
	public final static String POLICY_INTROSPECTION_AUTH_BEARER_TOKEN = "policy.introspection.endpoint.bearer.token";
	public final static String POLICY_INTROSPECTION_AUTH_SSL_STATO = "policy.introspection.endpoint.https.stato";
	
	public final static String POLICY_USER_INFO_STATO = "policy.userInfo.stato";
	public final static String POLICY_USER_INFO_SAVE_ERROR_IN_CACHE = "policy.userInfo.saveErrorInCache";
	public final static String POLICY_USER_INFO_URL = "policy.userInfo.endpoint.url";
	public final static String POLICY_USER_INFO_TIPO = "policy.userInfo.tipo";
	public final static String POLICY_USER_INFO_HTTP_METHOD = "policy.userInfo.httpMethod";
	public final static String POLICY_USER_INFO_REQUEST_TOKEN_POSITION = "policy.userInfo.requestTokenPosition";
	public final static String POLICY_USER_INFO_REQUEST_TOKEN_POSITION_HEADER_NAME = "policy.userInfo.requestTokenPosition.header";
	public final static String POLICY_USER_INFO_REQUEST_TOKEN_POSITION_URL_PROPERTY_NAME = "policy.userInfo.requestTokenPosition.queryParameter";
	public final static String POLICY_USER_INFO_REQUEST_TOKEN_POSITION_FORM_PROPERTY_NAME = "policy.userInfo.requestTokenPosition.formParameter";
	public final static String POLICY_USER_INFO_CONTENT_TYPE = "policy.userInfo.contentType";	
	public final static String POLICY_USER_INFO_CLAIMS_PARSER_TYPE = CostantiConfigurazione.POLICY_USER_INFO_CLAIMS_PARSER_TYPE; //"policy.userInfo.claimsParser";
	public final static String POLICY_USER_INFO_CLAIMS_PARSER_TYPE_CUSTOM = CostantiConfigurazione.POLICY_USER_INFO_CLAIMS_PARSER_TYPE_CUSTOM; //"CUSTOM";
	public final static String POLICY_USER_INFO_CLAIMS_PARSER_CLASS_NAME = CostantiConfigurazione.POLICY_USER_INFO_CLAIMS_PARSER_CLASS_NAME; //"policy.userInfo.claimsParser.className";
	public final static String POLICY_USER_INFO_CLAIMS_PARSER_PLUGIN_TYPE = CostantiConfigurazione.POLICY_USER_INFO_CLAIMS_PARSER_PLUGIN_TYPE; //"policy.userInfo.claimsParser.pluginType";
	public final static String POLICY_USER_INFO_AUTH_BASIC_STATO = "policy.userInfo.endpoint.basic.stato";
	public final static String POLICY_USER_INFO_AUTH_BASIC_USERNAME = "policy.userInfo.endpoint.basic.username";
	public final static String POLICY_USER_INFO_AUTH_BASIC_PASSWORD = "policy.userInfo.endpoint.basic.password";
	public final static String POLICY_USER_INFO_AUTH_BEARER_STATO = "policy.userInfo.endpoint.bearer.stato";
	public final static String POLICY_USER_INFO_AUTH_BEARER_TOKEN = "policy.userInfo.endpoint.bearer.token";
	public final static String POLICY_USER_INFO_AUTH_SSL_STATO = "policy.userInfo.endpoint.https.stato";
	
	public final static String POLICY_TOKEN_FORWARD_STATO = "policy.tokenForward.stato";
	
	public final static String POLICY_TOKEN_FORWARD_TRASPARENTE_STATO = "policy.tokenForward.trasparente.stato";
	public final static String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE = "policy.tokenForward.trasparente.mode";
	public final static String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_AS_RECEIVED = "asReceived";
	public final static String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_HEADER = "RFC6750_header";
	public final static String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_URL = "RFC6750_url";
	public final static String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER = "CUSTOM_header";
	public final static String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL = "CUSTOM_url";
	public final static String LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_AS_RECEIVED = "Come è stato ricevuto";
	public final static String LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_AS_RECEIVED_ORIGINALE = "Token Originale";
	public final static String LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_HEADER = "Header HTTP 'Authorization Bearer'";
	public final static String LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_URL = "Parametro URL 'access_token'";
	public final static String LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER = "Header HTTP ''{0}''";
	public final static String LABEL_POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL = "Parametro URL ''{0}''";
	public final static String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER_NAME = "policy.tokenForward.trasparente.mode.header";
	public final static String POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL_PARAMETER_NAME = "policy.tokenForward.trasparente.mode.queryParameter";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_STATO = "policy.tokenForward.infoRaccolte.stato";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE = "policy.tokenForward.infoRaccolte.mode";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_HEADERS = "op2header";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JSON = "op2json";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS = "op2jws";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS = "jws";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE = "jwe";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON = "json";
	public final static String LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_HEADERS = "GovWay Headers";
	public final static String LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JSON = "GovWay JSON";
	public final static String LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS = "GovWay JWS";
	public final static String LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS = "JWS";
	public final static String LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE = "JWE";
	public final static String LABEL_POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON = "JSON";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_ENCODE_BASE64 = "policy.tokenForward.infoRaccolte.base64";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER = "CUSTOM_header";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_URL = "CUSTOM_url";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT = "policy.tokenForward.infoRaccolte.validazioneJWT";	
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE = "policy.tokenForward.infoRaccolte.validazioneJWT.mode";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE_HEADER_NAME = "policy.tokenForward.infoRaccolte.validazioneJWT.mode.header";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE_URL_PARAMETER_NAME = "policy.tokenForward.infoRaccolte.validazioneJWT.mode.queryParameter";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION = "policy.tokenForward.infoRaccolte.introspection";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE = "policy.tokenForward.infoRaccolte.introspection.mode";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE_HEADER_NAME = "policy.tokenForward.infoRaccolte.introspection.mode.header";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE_URL_PARAMETER_NAME = "policy.tokenForward.infoRaccolte.introspection.mode.queryParameter";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO = "policy.tokenForward.infoRaccolte.userInfo";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE = "policy.tokenForward.infoRaccolte.userInfo.mode";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE_HEADER_NAME = "policy.tokenForward.infoRaccolte.userInfo.mode.header";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE_URL_PARAMETER_NAME = "policy.tokenForward.infoRaccolte.userInfo.mode.queryParameter";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_SIGNATURE_PROP_REF_ID = SecurityConstants.SIGNATURE_PROPERTY_REF_ID;
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_ENCRYP_PROP_REF_ID = SecurityConstants.ENCRYPTION_PROPERTY_REF_ID;
	
	public final static String POLICY_RETRIEVE_TOKEN_PARSER_TYPE = "policy.retrieveToken.claimsParser";
	public final static String POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM = CostantiConfigurazione.POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM; //"policy.retrieveToken.claimsParser.custom";
	public final static String POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM_CYSTOM = CostantiConfigurazione.POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM_CYSTOM; //"CUSTOM";
	public final static String POLICY_RETRIEVE_TOKEN_PARSER_CLASS_NAME = CostantiConfigurazione.POLICY_RETRIEVE_TOKEN_PARSER_CLASS_NAME; //"policy.retrieveToken.claimsParser.className";
	public final static String POLICY_RETRIEVE_TOKEN_PARSER_PLUGIN_TYPE = CostantiConfigurazione.POLICY_RETRIEVE_TOKEN_PARSER_PLUGIN_TYPE; //"policy.retrieveToken.claimsParser.pluginType";
	public final static String POLICY_RETRIEVE_TOKEN_MODE = "policy.retrieveToken.mode";
	public final static String POLICY_RETRIEVE_TOKEN_MODE_PDND = "policy.retrieveToken.jwt.pdnd";
	public final static String POLICY_RETRIEVE_TOKEN_URL = "policy.retrieveToken.endpoint.url";
	public final static String POLICY_RETRIEVE_TOKEN_RESPONSE_TYPE = "policy.retrieveToken.responseType";
	public final static String POLICY_RETRIEVE_TOKEN_AUTH_BASIC_STATO = "policy.retrieveToken.endpoint.basic.stato";
	public final static String POLICY_RETRIEVE_TOKEN_AUTH_BASIC_USERNAME = "policy.retrieveToken.endpoint.basic.username";
	public final static String POLICY_RETRIEVE_TOKEN_AUTH_BASIC_PASSWORD = "policy.retrieveToken.endpoint.basic.password";
	public final static String POLICY_RETRIEVE_TOKEN_AUTH_BASIC_AS_AUTHORIZATION_HEADER = "policy.retrieveToken.endpoint.basic.asAuthorizationHeader";
	public final static String POLICY_RETRIEVE_TOKEN_AUTH_BEARER_STATO = "policy.retrieveToken.endpoint.bearer.stato";
	public final static String POLICY_RETRIEVE_TOKEN_AUTH_BEARER_TOKEN = "policy.retrieveToken.endpoint.bearer.token";
	public final static String POLICY_RETRIEVE_TOKEN_AUTH_SSL_STATO = "policy.retrieveToken.endpoint.https.stato";
	public final static String POLICY_RETRIEVE_TOKEN_USERNAME = "policy.retrieveToken.username";
	public final static String POLICY_RETRIEVE_TOKEN_PASSWORD = "policy.retrieveToken.password";
	public final static String POLICY_RETRIEVE_TOKEN_SCOPES = "policy.retrieveToken.scope";
	public final static String POLICY_RETRIEVE_TOKEN_AUDIENCE = "policy.retrieveToken.audience";
	public final static String POLICY_RETRIEVE_TOKEN_FORM_CLIENT_ID = "policy.retrieveToken.formClientId";
	public final static String POLICY_RETRIEVE_TOKEN_FORM_CLIENT_ID_MODE = "policy.retrieveToken.formClientId.mode";
	public final static String POLICY_RETRIEVE_TOKEN_FORM_RESOURCE = "policy.retrieveToken.formResource";
	public final static String POLICY_RETRIEVE_TOKEN_FORM_PARAMETERS = "policy.retrieveToken.formParameters";
	public final static String POLICY_RETRIEVE_TOKEN_HTTP_METHOD = "policy.retrieveToken.httpMethod";
	public final static String POLICY_RETRIEVE_TOKEN_HTTP_CONTENT_TYPE = "policy.retrieveToken.httpContentType";
	public final static String POLICY_RETRIEVE_TOKEN_HTTP_HEADERS = "policy.retrieveToken.httpHeaders";
	public final static String POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD_TEMPLATE_TYPE = "policy.retrieveToken.httpPayloadTemplateType";
	public final static String POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD_TEMPLATE_TYPE_TEMPLATE = "template";
	public final static String POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD_TEMPLATE_TYPE_FREEMARKER_TEMPLATE = "freemarker-template";
	public final static String POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD_TEMPLATE_TYPE_VELOCITY_TEMPLATE = "velocity-template";
	public final static String POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD = "policy.retrieveToken.httpPayload";
	public final static String POLICY_RETRIEVE_TOKEN_SAVE_ERROR_IN_CACHE = "policy.retrieveToken.saveErrorInCache";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_CLIENT_ID_MODE= "policy.retrieveToken.jwt.clientId.mode";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_CLIENT_ID= "policy.retrieveToken.jwt.clientId";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_CLIENT_SECRET= "policy.retrieveToken.jwt.clientSecret";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_ISSUER= "policy.retrieveToken.jwt.issuer";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_ISSUER_MODE= "policy.retrieveToken.jwt.issuer.mode";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SUBJECT= "policy.retrieveToken.jwt.subject";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SUBJECT_MODE= "policy.retrieveToken.jwt.subject.mode";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_IDENTIFIER= "policy.retrieveToken.jwt.jti";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_AUDIENCE= "policy.retrieveToken.jwt.audience";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_EXPIRED_TTL_SECONDS= "policy.retrieveToken.jwt.expired";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_EXPIRED_TTL_SECONDS_DEFAULT_VALUE = "300";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_CLAIMS = "policy.retrieveToken.jwt.claims";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_PURPOSE_ID = "policy.retrieveToken.jwt.purposeId";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SESSION_INFO = "policy.retrieveToken.jwt.sessionInfo";
	
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_ALGORITHM= "policy.retrieveToken.jwt.signature.algorithm";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID = "policy.retrieveToken.jwt.signature.include.key.id";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_NOT_PRESENT = "false"; // per backward compatibility
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_ALIAS = "true"; // per backward compatibility
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_CLIENT_ID = "client_id";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_CUSTOM = "custom";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_APPLICATIVO_MODI = CHOICE_APPLICATIVO_MODI_VALUE ;
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_MODE_FRUIZIONE_MODI = CHOICE_FRUIZIONE_MODI_VALUE ;
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_KEY_ID_VALUE = "policy.retrieveToken.jwt.signature.include.key.id.value";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_CERT = "policy.retrieveToken.jwt.signature.include.cert";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_URL = "policy.retrieveToken.jwt.signature.include.x509url";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_SHA1 = "policy.retrieveToken.jwt.signature.include.cert.sha1";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_INCLUDE_X509_SHA256 = "policy.retrieveToken.jwt.signature.include.cert.sha256";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_JOSE_CONTENT_TYPE = "policy.retrieveToken.jwt.signature.joseContentType";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_JOSE_TYPE = "policy.retrieveToken.jwt.signature.joseType";
	
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_TYPE= "policy.retrieveToken.jwt.signature.keystoreType";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_FILE= "policy.retrieveToken.jwt.signature.keystoreFile";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_PASSWORD= "policy.retrieveToken.jwt.signature.keystorePassword";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEY_ALIAS= "policy.retrieveToken.jwt.signature.keyAlias";
	public final static String POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEY_PASSWORD= "policy.retrieveToken.jwt.signature.keyPassword";
	
	public final static String POLICY_RETRIEVE_TOKEN_FORWARD_MODE = "policy.tokenForward.mode";
	public final static String POLICY_RETRIEVE_TOKEN_FORWARD_MODE_RFC6750_HEADER = "RFC6750_header";
	public final static String POLICY_RETRIEVE_TOKEN_FORWARD_MODE_RFC6750_URL = "RFC6750_url";
	public final static String POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_HEADER = "CUSTOM_header";
	public final static String POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_URL = "CUSTOM_url";
	public final static String POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_HEADER_NAME = "policy.tokenForward.mode.header";
	public final static String POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_URL_PARAMETER_NAME = "policy.tokenForward.mode.queryParameter";

	
	// VALORE VUOTO
	
	public final static String POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED = "${undefined}";
	
	
	// VALORE KEYSTORE MODI
	    	
    public static final String KEYSTORE_TYPE_APPLICATIVO_MODI_VALUE = "applicativoModi";
    public static final String KEYSTORE_TYPE_APPLICATIVO_MODI_LABEL = "Definito nell'applicativo ModI";
    
    public static final String KEYSTORE_TYPE_FRUIZIONE_MODI_VALUE = "fruizioneModi";
    public static final String KEYSTORE_TYPE_FRUIZIONE_MODI_LABEL = "Definito nella fruizione ModI";
	
	
	// CLAIMS PDND
	
	public final static String PDND_PURPOSE_ID = "purposeId";
	public final static String PDND_SESSION_INFO = "sessionInfo";
	
	public final static String PDND_OAUTH2_RFC_6749_REQUEST_CLIENT_ID = "client_id";
	public final static String PDND_OAUTH2_RFC_6749_REQUEST_RESOURCE = "resource";
	
	
	// STANDARD
	
	public final static String RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN = "access_token";
	public final static String RFC6750_FORM_PARAMETER_ACCESS_TOKEN = "access_token";
	
	// ELEMENTI ID 
	
	public final static String ID_RETRIEVE_ENDPOINT_URL = "endpointURL";
	public final static String ID_RETRIEVE_AUTENTICAZIONE_USERNAME = "autenticazioneUsername";
	public final static String ID_RETRIEVE_AUTENTICAZIONE_PASSWORD = "autenticazionePassword";
	public final static String ID_RETRIEVE_CLIENT_ID = "autenticazioneEndpointBasicUsername";
	public final static String ID_RETRIEVE_CLIENT_ID_CUSTOM = "autenticazioneEndpointBasicUsernameCustom";
	public final static String ID_RETRIEVE_CLIENT_SECRET = "autenticazioneEndpointBasicPassword";
	public final static String ID_RETRIEVE_CLIENT_SECRET_CUSTOM = "autenticazioneEndpointBasicPasswordCustom";
	public final static String ID_RETRIEVE_BEARER_TOKEN = "autenticazioneEndpointBearerToken";
	public final static String ID_RETRIEVE_JWT_KID_VALUE = "jwtSignatureKidValue";
	public final static String ID_RETRIEVE_JWT_X5U = "jwtSignatureIncludeCertModeX5U";
	public final static String ID_RETRIEVE_JWT_CLIENT_ID_APPLICATIVO_MODI_CUSTOM = "jwtClientIdApplicativoModiChoiceInput";
	public final static String ID_RETRIEVE_JWT_CLIENT_ID = "jwtClientId";
	public final static String ID_RETRIEVE_JWT_AUDIENCE = "jwtAudience";
	public final static String ID_RETRIEVE_JWT_ISSUER = "jwtIssuer";
	public final static String ID_RETRIEVE_JWT_ISSUER_APPLICATIVO_MODI_CUSTOM = "jwtIssuerApplicativoModiChoiceInput";
	public final static String ID_RETRIEVE_JWT_SUBJECT = "jwtSubject";
	public final static String ID_RETRIEVE_JWT_SUBJECT_APPLICATIVO_MODI_CUSTOM = "jwtSubjectApplicativoModiChoiceInput";
	public final static String ID_RETRIEVE_JWT_IDENTIFIER = "jwtIdentifier";
	public final static String ID_RETRIEVE_JWT_CLAIMS = "jwtPayloadClaims";
	public final static String ID_RETRIEVE_JWT_PURPOSE_ID = "jwtPurposeID";
	public final static String ID_RETRIEVE_JWT_SESSION_INFO = "jwtSessionInfo";
	public final static String ID_RETRIEVE_SCOPE = "scope";
	public final static String ID_RETRIEVE_AUDIENCE = "audience";
	public final static String ID_RETRIEVE_FORM_CLIENT_ID = "formClientId";
	public final static String ID_RETRIEVE_FORM_CLIENT_ID_APPLICATIVO_MODI_CUSTOM = "formClientIdApplicativoModiChoiceInput";
	public final static String ID_RETRIEVE_FORM_RESOURCE = "formResource";
	public final static String ID_RETRIEVE_FORM_PARAMETERS = "formParameters";
	public final static String ID_RETRIEVE_HTTP_METHOD = "httpMethod";
	public final static String ID_RETRIEVE_HTTP_METHOD_PAYLOAD_DEFINED = "httpMethodPayloadDefined";
	public final static String ID_RETRIEVE_HTTP_CONTENT_TYPE = "httpContentType";
	public final static String ID_RETRIEVE_HTTP_HEADERS = "httpHeaders";
	public final static String ID_RETRIEVE_HTTP_PAYLOAD_TEMPLATE_TYPE = "httpPayloadTemplateType";
	public final static String ID_RETRIEVE_HTTP_TEMPLATE_PAYLOAD = "httpTemplatePayload";
	public final static String ID_RETRIEVE_HTTP_FREEMARKER_PAYLOAD = "httpFreemarkerPayload";
	public final static String ID_RETRIEVE_HTTP_VELOCITY_PAYLOAD = "httpVelocityPayload";
	
	
	// ELEMENTI SELECT
	
	public final static String ID_RETRIEVE_TOKEN_METHOD = "retrieveTokenMethod";
	public final static String ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL = "clientCredentials";
	public final static String ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD = "usernamePassword";
	public final static String ID_RETRIEVE_TOKEN_METHOD_RFC_7523_X509 = "rfc7523_x509";
	public final static String ID_RETRIEVE_TOKEN_METHOD_RFC_7523_CLIENT_SECRET = "rfc7523_clientSecret";
	public final static String ID_RETRIEVE_TOKEN_METHOD_CUSTOM = "custom";
	public final static String ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL_LABEL = "Client Credentials";
	public final static String ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD_LABEL = "Resource Owner Password Credentials";
	public final static String ID_RETRIEVE_TOKEN_METHOD_RFC_7523_X509_LABEL = "Signed JWT";
	public final static String ID_RETRIEVE_TOKEN_METHOD_RFC_7523_CLIENT_SECRET_LABEL = "Signed JWT with Client Secret";
	public final static String ID_RETRIEVE_TOKEN_METHOD_CUSTOM_LABEL = "Personalizzato";
	public final static String ID_RETRIEVE_TOKEN_JWT_EXPIRED_TTL_SECONDS= "jwtExpTtl";
	public final static String ID_RETRIEVE_TOKEN_JWT_SYMMETRIC_SIGN_ALGORITHM = "jwtSymmetricSignatureAlgorithm";
	public final static String ID_RETRIEVE_TOKEN_JWT_ASYMMETRIC_SIGN_ALGORITHM = "jwtAsymmetricSignatureAlgorithm";
	
	public final static String ID_INTROSPECTION_HTTP_METHOD = "introspectionHttpMethod";
	
	public final static String ID_USER_INFO_HTTP_METHOD = "userInfoHttpMethod";
	
	public final static String ID_TIPOLOGIA_HTTPS = "endpointHttpsTipologia";
	
	public final static String ID_JWS_SIGNATURE_ALGORITHM = "tokenForwardInfoRaccolteModeJWSSignature";
	
	public final static String ID_JWS_ENCRYPT_KEY_ALGORITHM = "tokenForwardInfoRaccolteModeJWEKeyAlgorithm";
	
	public final static String ID_JWS_ENCRYPT_CONTENT_ALGORITHM = "tokenForwardInfoRaccolteModeJWEContentAlgorithm";
	
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE = "validazioneJwtTruststoreType";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_FILE = "validazioneJwtTruststoreFile";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_PASSWORD = "validazioneJwtTruststorePassword";
	
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE = "validazioneJwtTruststoreTypeSelectCertificate";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_ALIAS = "alias";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_ALIAS = "Alias in TrustStore";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_ALIAS = "Per la validazione viene utilizzato il certificato nel truststore corrispondente all'alias indicato";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C = "x5c";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5C = "Certificate 'x5c' in Token";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_X5C = "Per la validazione viene utilizzato il certificato presente nel token, dopo averlo validato rispetto al truststore";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5T256 = "x5t#256";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5T256 = "SHA-256 Thumbprint 'x5t#256' in Token";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_X5T256 = "Per la validazione viene utilizzato il certificato nel truststore corrispondente al thumbprint presente nel token";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C_X5T256 = "x5c_x5t#256";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5C_X5T256 = "Certificate 'x5c' o SHA-256 Thumbprint 'x5t#256' in Token";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_X5C_X5T256 = "Per la validazione viene utilizzato il certificato presente nel token o recuperato dal truststore rispetto al thumbprint";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5U = "x5u";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5U = "URL Certificate 'x5u' in Token";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_X5U = "Per la validazione viene recuperato il certificato riferito dalla URL presente nel token e validato rispetto al truststore";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_KID = "kid";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_KID = "Key ID 'kid' in Token";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_NOTE_KID = "Per la validazione viene utilizzato il certificato nel truststore con alias corrispondente al 'kid' presente nel token";
	public final static List<String> ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES = new ArrayList<>();
	static {
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_ALIAS);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5T256);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C_X5T256);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_KID);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5U);
	}
	public final static List<String> ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS = new ArrayList<>();
	static {
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_ALIAS);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5C);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5T256);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5C_X5T256);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_KID);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_LABEL_X5U);
	}
	
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY = "validazioneJwtTruststoreTypeSelectJWKPublicKey";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUE_ALIAS = "alias";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABEL_ALIAS = "Alias in TrustStore";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_NOTE_ALIAS = "Per la validazione viene utilizzata la chiave pubblica nel truststore JWKs corrispondente al kid indicato";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUE_KID = "kid";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABEL_KID = "Key ID 'kid' in Token";
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_NOTE_KID = "Per la validazione viene utilizzata la chiave pubblica nel truststore JWKs corrispondente al 'kid' presente nel token";
	public final static List<String> ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUES = new ArrayList<>();
	static {
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUE_ALIAS);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUES.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_VALUE_KID);
	}
	public final static List<String> ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABELS = new ArrayList<>();
	static {
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABEL_ALIAS);
		ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABELS.add(ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY_LABEL_KID);
	}
	
	public final static String ID_VALIDAZIONE_JWT_TRUSTSTORE_OCSP_POLICY = "validazioneJwtTruststoreOcspPolicy";
	
	public final static String ID_VALIDAZIONE_JWT_KEYSTORE_TYPE = "validazioneJwtKeystoreType";
	public final static String ID_VALIDAZIONE_JWT_KEYSTORE_FILE = "validazioneJwtKeystoreFile";
	public final static String ID_VALIDAZIONE_JWT_KEYSTORE_PASSWORD = "validazioneJwtKeystorePassword";
	public final static String ID_VALIDAZIONE_JWT_KEYSTORE_PASSWORD_PRIVATE_KEY = "validazioneJwtKeystorePrivateKeyPassword";
	
	public final static String ID_HTTPS_TRUSTSTORE_TYPE = "endpointHttpsTruststoreType";
	public final static String ID_HTTPS_TRUSTSTORE_FILE = "endpointHttpsTruststoreFile";
	public final static String ID_HTTPS_TRUSTSTORE_PASSWORD = "endpointHttpsTruststorePassword";
	
	public final static String ID_HTTPS_KEYSTORE_TYPE = "endpointHttpsClientKeystoreType";
	public final static String ID_HTTPS_KEYSTORE_FILE = "endpointHttpsClientKeystoreFile";
	public final static String ID_HTTPS_KEYSTORE_PASSWORD = "endpointHttpsClientKeystorePassword";
	public final static String ID_HTTPS_KEYSTORE_PASSWORD_PRIVATE_KEY = "endpointHttpsClientPasswordChiavePrivata";
	
	public final static String ID_TOKEN_FORWARD_JWS_KEYSTORE_TYPE = "tokenForwardInfoRaccolteModeJWSKeystoreType";
	public final static String ID_TOKEN_FORWARD_JWS_KEYSTORE_FILE = "tokenForwardInfoRaccolteModeJWSKeystoreFile";
	public final static String ID_TOKEN_FORWARD_JWS_KEYSTORE_PASSWORD = "tokenForwardInfoRaccolteModeJWSKeystorePassword";
	public final static String ID_TOKEN_FORWARD_JWS_KEYSTORE_PASSWORD_PRIVATE_KEY = "tokenForwardInfoRaccolteModeJWSKeystorePrivateKeyPassword";
	
	public final static String ID_TOKEN_FORWARD_JWE_KEYSTORE_TYPE = "tokenForwardInfoRaccolteModeJWEContentKeystoreType";
	public final static String ID_TOKEN_FORWARD_JWE_KEYSTORE_FILE = "tokenForwardInfoRaccolteModeJWEContentKeystoreFile";
	public final static String ID_TOKEN_FORWARD_JWE_KEYSTORE_PASSWORD = "tokenForwardInfoRaccolteModeJWEContentKeystorePassword";
	public final static String ID_TOKEN_FORWARD_JWE_KEYSTORE_PASSWORD_PRIVATE_KEY = "tokenForwardInfoRaccolteModeJWEContentKeystorePrivateKeyPassword";
	
	public final static String ID_VALIDAZIONE_JWT_CUSTOM_PARSER_PLUGIN_CLASSNAME = "validazioneJwtParserCustom";
	public final static String ID_INTROSPECTION_CUSTOM_PARSER_PLUGIN_CLASSNAME = "introspectionParserCustom";
	public final static String ID_USER_INFO_CUSTOM_PARSER_PLUGIN_CLASSNAME = "userInfoParserCustom";
	
	public final static String ID_VALIDAZIONE_JWT_CUSTOM_PARSER_PLUGIN_CHOICE = "validazioneJwtParserCustomPluginChoice";
	public final static String ID_INTROSPECTION_CUSTOM_PARSER_PLUGIN_CHOICE = "introspectionParserCustomPluginChoice";
	public final static String ID_USER_INFO_CUSTOM_PARSER_PLUGIN_CHOICE = "userInfoParserCustomPluginChoice";
	
	public final static String ID_NEGOZIAZIONE_JWT_KEYSTORE_TYPE = "jwtKeystoreType";
	public final static String ID_NEGOZIAZIONE_JWT_KEYSTORE_FILE = "jwtKeystoreFile";
	public final static String ID_NEGOZIAZIONE_JWT_KEYSTORE_PASSWORD = "jwtKeystorePassword";
	public final static String ID_NEGOZIAZIONE_JWT_KEYSTORE_ALIAS_PRIVATE_KEY = "jwtAliasChiavePrivata";
	public final static String ID_NEGOZIAZIONE_JWT_KEYSTORE_PASSWORD_PRIVATE_KEY = "jwtPasswordChiavePrivata";
		
	public final static String ID_NEGOZIAZIONE_CUSTOM_PARSER_PLUGIN_CLASSNAME = "customTokenParserCustomPlugin";
	public final static String ID_NEGOZIAZIONE_CUSTOM_PARSER_PLUGIN_CHOICE = "customTokenParserCustomPluginChoice";
	
	public final static String ID_AA_JWS_KEYSTORE_TYPE = "aaJWSKeystoreType";
	public final static String ID_AA_JWS_KEYSTORE_FILE = "aaJWSKeystoreFile";
	public final static String ID_AA_JWS_KEYSTORE_PASSWORD = "aaJWSKeystorePassword";
	public final static String ID_AA_JWS_KEYSTORE_PASSWORD_PRIVATE_KEY = "aaJWSKeystorePrivateKeyPassword";
	
	public final static String ID_AA_JWS_TRUSTSTORE_TYPE = "aaJWSTruststoreType";
	public final static String ID_AA_JWS_TRUSTSTORE_FILE = "aaJWSTruststoreFile";
	public final static String ID_AA_JWS_TRUSTSTORE_PASSWORD = "aaJWSTruststorePassword";
	public final static String ID_AA_JWS_TRUSTSTORE_TYPE_SELECT_CERTIFICATE = "aaJWSTruststoreTypeSelectCertificate";
	public final static String ID_AA_JWS_TRUSTSTORE_TYPE_SELECT_JWK_PUBLIC_KEY = "aaJWSTruststoreTypeSelectJWKPublicKey";
	public final static String ID_AA_JWS_TRUSTSTORE_OCSP_POLICY = "aaJWSTruststoreOcspPolicy";

}
