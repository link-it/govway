/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.oauth2;

/**
 * Classe costanti.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OAuth2Costanti {

	private OAuth2Costanti() {/* static only */}
	
	private static final String SCOPE = "scope";
	
	public static final String HEADER_NAME_AUTHORIZATION = "Authorization";
	public static final String MEDIA_TYPE_APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String MEDIA_TYPE_APPLICATION_JSON = "application/json";
	public static final String HEADER_NAME_ACCEPT = "Accept";
	
	public static final String PROP_OAUTH2_PREFIX = "oauth2.";
	public static final String PROP_OAUTH2_AUTHORIZATION_ENDPOINT = PROP_OAUTH2_PREFIX + "authorization.endpoint";
	public static final String PROP_OAUTH2_TOKEN_ENDPOINT = PROP_OAUTH2_PREFIX + "token.endpoint";
	public static final String PROP_OAUTH2_USER_INFO_ENDPOINT = PROP_OAUTH2_PREFIX + "userInfo.endpoint";
	public static final String PROP_OAUTH2_JWKS_ENDPOINT = PROP_OAUTH2_PREFIX + "jwks.endpoint";
	public static final String PROP_OAUTH2_LOGOUT_ENDPOINT = PROP_OAUTH2_PREFIX + "logout.endpoint";
	public static final String PROP_OAUTH2_CLIENT_ID = PROP_OAUTH2_PREFIX + "clientId";
	public static final String PROP_OAUTH2_CLIENT_SECRET = PROP_OAUTH2_PREFIX + "clientSecret";
	public static final String PROP_OAUTH2_REDIRECT_URL = PROP_OAUTH2_PREFIX + "redirectUri";
	public static final String PROP_OAUTH2_SCOPE = PROP_OAUTH2_PREFIX + SCOPE;
	public static final String PROP_OAUTH2_PRINCIPAL_CLAIM = PROP_OAUTH2_PREFIX + "principalClaim";
	public static final String PROP_OAUTH2_CLAIMS_VALIDATION = PROP_OAUTH2_PREFIX + "claims.validation.";
	public static final String PROP_OAUTH2_PKCE_ENABLED = PROP_OAUTH2_PREFIX + "pkce.enabled";
	public static final String PROP_OAUTH2_PKCE_METHOD = PROP_OAUTH2_PREFIX + "pkce.method";

	public static final String PROP_OAUTH2_READ_TIMEOUT = PROP_OAUTH2_PREFIX + "readTimeout";
	public static final String PROP_OAUTH2_CONNECT_TIMEOUT = PROP_OAUTH2_PREFIX + "connectTimeout";

	// Truststore HTTPS
	public static final String PROP_OAUTH2_HTTPS_HOSTNAME_VERIFIER = PROP_OAUTH2_PREFIX + "https.hostnameVerifier";
	public static final String PROP_OAUTH2_HTTPS_TRUST_ALL_CERTS = PROP_OAUTH2_PREFIX + "https.trustAllCerts";
	public static final String PROP_OAUTH2_HTTPS_TRUSTSTORE = PROP_OAUTH2_PREFIX + "https.trustStore";
	public static final String PROP_OAUTH2_HTTPS_TRUSTSTORE_PASSWORD = PROP_OAUTH2_PREFIX + "https.trustStore.password";
	public static final String PROP_OAUTH2_HTTPS_TRUSTSTORE_TYPE = PROP_OAUTH2_PREFIX + "https.trustStore.type";
	public static final String PROP_OAUTH2_HTTPS_TRUSTSTORE_CRL = PROP_OAUTH2_PREFIX + "https.trustStore.crl";

	// Keystore HTTPS
	public static final String PROP_OAUTH2_HTTPS_KEYSTORE = PROP_OAUTH2_PREFIX + "https.keyStore";
	public static final String PROP_OAUTH2_HTTPS_KEYSTORE_PASSWORD = PROP_OAUTH2_PREFIX + "https.keyStore.password";
	public static final String PROP_OAUTH2_HTTPS_KEYSTORE_TYPE = PROP_OAUTH2_PREFIX + "https.keyStore.type";
	public static final String PROP_OAUTH2_HTTPS_KEY_ALIAS = PROP_OAUTH2_PREFIX + "https.key.alias";
	public static final String PROP_OAUTH2_HTTPS_KEY_PASSWORD = PROP_OAUTH2_PREFIX + "https.key.password";

	
	/** Parametri */
	
	public static final String PARAM_NAME_OAUTH2_CLIENT_ID = "client_id";
	public static final String PARAM_NAME_OAUTH2_CLIENT_SECRET = "client_secret";
	public static final String PARAM_NAME_OAUTH2_REDIRECT_URI = "redirect_uri";
	public static final String PARAM_NAME_OAUTH2_SCOPE = SCOPE;
	public static final String PARAM_NAME_OAUTH2_CODE = "code";
	public static final String PARAM_NAME_OAUTH2_STATE = "state";
	public static final String PARAM_NAME_OAUTH2_GRANT_TYPE = "grant_type";
	public static final String PARAM_NAME_OAUTH2_RESPONSE_TYPE = "response_type";
	public static final String PARAM_NAME_OAUTH2_ERROR = "error";
	public static final String PARAM_NAME_OAUTH2_ID_TOKEN_HINT = "id_token_hint";
	public static final String PARAM_NAME_OAUTH2_POST_LOGOUT_REDIRECT_URI = "post_logout_redirect_uri";
	public static final String PARAM_NAME_OAUTH2_REFRESH_TOKEN = "refresh_token";
	public static final String PARAM_NAME_PRINCIPAL_ERROR_MSG = "principalErrorMsg";

	// PKCE (RFC 7636)
	public static final String PARAM_NAME_OAUTH2_CODE_CHALLENGE = "code_challenge";
	public static final String PARAM_NAME_OAUTH2_CODE_CHALLENGE_METHOD = "code_challenge_method";
	public static final String PARAM_NAME_OAUTH2_CODE_VERIFIER = "code_verifier";
	public static final String CODE_CHALLENGE_METHOD_S256 = "S256";
	public static final String CODE_CHALLENGE_METHOD_PLAIN = "plain";

	/** Attributi sessione / request */
	public static final String ATTRIBUTE_NAME_ERROR_MESSAGE = "oauth2ErrorMessage";
	public static final String ATTRIBUTE_NAME_ERROR_CODE = "oauth2ErrorCode";
	public static final String ATTRIBUTE_NAME_ERROR_DETAIL = "oauth2ErrorDetail";
	public static final String ATTRIBUTE_NAME_ACCESS_TOKEN = "oauthAccessToken";
	public static final String ATTRIBUTE_NAME_ACCESS_TOKEN_OBJ = "oauthTokenObj";
	public static final String ATTRIBUTE_NAME_REFRESH_TOKEN = "oauthRefreshToken";
	public static final String ATTRIBUTE_NAME_TOKEN_EXPIRES_AT = "oauthExpiresAt";
	public static final String ATTRIBUTE_NAME_USER_INFO = "oauth2UserInfo";
	public static final String ATTRIBUTE_NAME_OAUTH2_STATE = "oauth2state";
	public static final String ATTRIBUTE_NAME_ID_TOKEN = "oauthIdToken";
	public static final String ATTRIBUTE_NAME_CODE_VERIFIER = "oauth2code_verifier";


	/** Field json */
	public static final String FIELD_NAME_ACCESS_TOKEN = "access_token";
	public static final String FIELD_NAME_EXPIRES_IN = "expires_in";
	public static final String FIELD_NAME_REFRESH_TOKEN = "refresh_token";
	public static final String FIELD_NAME_SCOPE = SCOPE;
	public static final String FIELD_NAME_TOKEN_TYPE = "token_type";
	public static final String FIELD_NAME_ID_TOKEN = "id_token";
	public static final String FIELD_NAME_KID = "kid";
	public static final String FIELD_NAME_ALGORITHM = "alg";
	public static final String FIELD_NAME_ERROR = "error";
	public static final String FIELD_NAME_ERROR_DESCRIPTION = "error_description";
	
	
	/** Costanti casi errore */

	public static final String ERRORE_INVALID_REQUEST = "invalid_request";
	public static final String ERRORE_UNAUTHORIZED_CLIENT = "unauthorized_client";
	public static final String ERRORE_ACCESS_DENIED = "access_denied";
	public static final String ERRORE_UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";
	public static final String ERRORE_INVALID_SCOPE = "invalid_scope";
	public static final String ERRORE_INSUFFICIENT_SCOPE = "insufficient_scope";
	public static final String ERRORE_INVALID_TOKEN = "invalid_token";
	public static final String ERRORE_SERVER_ERROR = "server_error";
	public static final String ERRORE_TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";
	public static final String ERRORE_INVALID_CLIENT = "invalid_client";
	public static final String ERRORE_INVALID_GRANT = "invalid_grant";
	public static final String ERRORE_UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
	public static final String ERRORE_UNSUPPORTED_TOKEN_TYPE = "unsupported_token_type";
	public static final String ERRORE_INVALID_REDIRECT_URI = "invalid_redirect_uri";
	
	public static final String ERROR_MSG_TOKEN_RICEVUTO_NON_VALIDO = "Token ricevuto non valido";
	public static final String ERROR_MSG_ERRORE_DURANTE_LA_LETTURA_DELLE_PROPERTIES = "Errore durante la lettura delle properties: ";
	public static final String ERROR_MSG_AUTENTICAZIONE_OAUTH2_NON_DISPONIBILE_SI_E_VERIFICATO_UN_ERRORE = "Autenticazione Oauth2 non disponibile: si e' verificato un'errore: ";
	public static final String ERROR_MSG_LOGIN_ERRORE_INTERNO = "Il sistema non riesce ad autenticare l'utente.";
	public static final String ERROR_MSG_IMPOSSIBILE_AUTENTICARE_UTENTE = "Impossibile autenticare l'utente: ";
}
