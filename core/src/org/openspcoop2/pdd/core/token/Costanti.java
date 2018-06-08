package org.openspcoop2.pdd.core.token;

import org.openspcoop2.security.message.constants.SecurityConstants;

public class Costanti {

	public final static String TIPOLOGIA = "gestionePolicyToken";
	
	
	// Context
	
	public final static String PDD_CONTEXT_TOKEN_REALM = "PDD_CONTEXT_TOKEN_REALM";
	public final static String PDD_CONTEXT_TOKEN_MESSAGE_ERROR_BODY_EMPTY = "PDD_CONTEXT_TOKEN_MESSAGE_ERROR_BODY_EMPTY";
	public final static String PDD_CONTEXT_TOKEN_MESSAGE_ERROR_GENERIC_MESSAGE = "PDD_CONTEXT_TOKEN_MESSAGE_ERROR_GENERIC_MESSAGE";
	public final static String PDD_CONTEXT_TOKEN_POSIZIONE = "TOKEN_POSIZIONE";
	public final static String PDD_CONTEXT_TOKEN_ESITO_VALIDAZIONE = "TOKEN_ESITO_VALIDAZIONE";
	public final static String PDD_CONTEXT_TOKEN_ESITO_INTROSPECTION = "TOKEN_ESITO_INTROSPECTION";
	public final static String PDD_CONTEXT_TOKEN_ESITO_USER_INFO = "TOKEN_ESITO_USER_INFO";
	public final static String PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE = "TOKEN_INFORMAZIONI_NORMALIZZATE";
	public final static String MSG_CONTEXT_TOKEN_FORWARD = "TOKEN_FORWARD"; // per salvarlo con il messaggio
	
	
	// Policy id
	
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
	public final static String POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE = "policy.validazioneJWT.claimsParser.className";
	public final static String POLICY_VALIDAZIONE_CLAIMS_PARSER_CLASS_NAME = "policy.validazioneJWT.claimsParser.className";

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
	public final static String POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE = "policy.introspection.claimsParser.className";
	public final static String POLICY_INTROSPECTION_CLAIMS_PARSER_CLASS_NAME = "policy.introspection.claimsParser.className";
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
	public final static String POLICY_USER_INFO_CLAIMS_PARSER_TYPE = "policy.userInfo.claimsParser.className";
	public final static String POLICY_USER_INFO_CLAIMS_PARSER_CLASS_NAME = "policy.userInfo.claimsParser.className";
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
	
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_STATO = "policy.tokenForward.infoRaccolte.stato";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE = "policy.tokenForward.infoRaccolte.mode";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_HEADERS = "op2header";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JSON = "op2json";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS = "op2jws";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS = "jws";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE = "jwe";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON = "json";
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
	
	
	
	// STANDARD
	
	public final static String RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN = "access_token";
	public final static String RFC6750_FORM_PARAMETER_ACCESS_TOKEN = "access_token";
	
	// ELEMENTI SELECT
	
	public final static String ID_INTROSPECTION_HTTP_METHOD = "introspectionHttpMethod";
	
	public final static String ID_USER_INFO_HTTP_METHOD = "userInfoHttpMethod";
	
	public final static String ID_TIPOLOGIA_HTTPS = "endpointHttpsTipologia";
	
	public final static String ID_JWS_SIGNATURE_ALGORITHM = "tokenForwardInfoRaccolteModeJWSSignature";
	
	public final static String ID_JWS_ENCRYPT_KEY_ALGORITHM = "tokenForwardInfoRaccolteModeJWEKeyAlgorithm";
	
	public final static String ID_JWS_ENCRYPT_CONTENT_ALGORITHM = "tokenForwardInfoRaccolteModeJWEContentAlgorithm";
	
}
