package org.openspcoop2.pdd.core.token;

public class Costanti {

	public final static String POLICY_NAME = "policy.name";
	
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
	
	
	// ELEMENTI SELECT
	
	public final static String ID_TIPOLOGIA_HTTPS = "endpointHttpsTipologia";
	
	public final static String ID_JWS_SIGNATURE_ALGORITHM = "tokenForwardInfoRaccolteModeJWSSignature";
	
	public final static String ID_JWS_ENCRYPT_KEY_ALGORITHM = "tokenForwardInfoRaccolteModeJWEKeyAlgorithm";
	
	public final static String ID_JWS_ENCRYPT_CONTENT_ALGORITHM = "tokenForwardInfoRaccolteModeJWEContentAlgorithm";
	
}
