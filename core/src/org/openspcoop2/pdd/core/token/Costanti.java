package org.openspcoop2.pdd.core.token;

public class Costanti {

	public final static String TIPOLOGIA = "gestionePolicyToken";
	
	public final static String POLICY_REALM = "policy.realm";
	public final static String POLICY_GENERIC_ERROR = "policy.genericError";
	
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
	
	public final static String POLICY_STATO_ABILITATO = "true";
	public final static String POLICY_STATO_DISABILITATO = "false";
	
	public final static String POLICY_VALIDAZIONE_STATO = "policy.validazioneJWT.stato";
	public final static String POLICY_VALIDAZIONE_TIPO = "policy.validazioneJWT.tipo";
	public final static String POLICY_VALIDAZIONE_TIPO_JWS = "JWS";
	public final static String POLICY_VALIDAZIONE_TIPO_JWE = "JWE";
	
	public final static String POLICY_INTROSPECTION_STATO = "policy.introspection.stato";
	public final static String POLICY_INTROSPECTION_URL = "policy.introspection.endpoint.url";
	
	public final static String POLICY_USER_INFO_STATO = "policy.userInfo.stato";
	public final static String POLICY_USER_INFO_URL = "policy.userInfo.endpoint.url";
	
	public final static String POLICY_TOKEN_FORWARD_STATO = "policy.tokenForward.stato";
	public final static String POLICY_TOKEN_FORWARD_TRASPARENTE_STATO = "policy.tokenForward.trasparente.stato";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_STATO = "policy.tokenForward.infoRaccolte.stato";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION = "policy.tokenForward.infoRaccolte.introspection";
	public final static String POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO = "policy.tokenForward.infoRaccolte.userInfo";
	
	
	
	// STANDARD
	
	public final static String RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN = "access_token";
	public final static String RFC6750_FORM_PARAMETER_ACCESS_TOKEN = "access_token";
	
	// ELEMENTI SELECT
	
	public final static String ID_TIPOLOGIA_HTTPS = "endpointHttpsTipologia";
	
	public final static String ID_JWS_SIGNATURE_ALGORITHM = "tokenForwardInfoRaccolteModeJWSSignature";
	
	public final static String ID_JWS_ENCRYPT_KEY_ALGORITHM = "tokenForwardInfoRaccolteModeJWEKeyAlgorithm";
	
	public final static String ID_JWS_ENCRYPT_CONTENT_ALGORITHM = "tokenForwardInfoRaccolteModeJWEContentAlgorithm";
	
}
