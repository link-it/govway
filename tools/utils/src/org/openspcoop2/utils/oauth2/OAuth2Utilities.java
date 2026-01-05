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

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.random.RandomGenerator;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWTOptions;
import org.openspcoop2.utils.security.JWTParser;
import org.openspcoop2.utils.security.JsonVerifySignature;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
 * Classe di utilities per le chiamate verso il servizio autenticazione Oauth
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OAuth2Utilities {

	private OAuth2Utilities() { /*static only*/ }

	public static String addFirstParameter(String name, String value) {
		return "?" + getParameter(name, value);
	}
	
	public static String addParameter(String name, String value) {
		return "&" + getParameter(name, value);
	}
	
	public static String getParameter(String name, String value) {
		return name + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	/**
	 * Genera un code_verifier PKCE secondo RFC 7636.
	 * Il code_verifier è una stringa random di alta entropia di lunghezza tra 43 e 128 caratteri,
	 * usando caratteri unreserved [A-Z] / [a-z] / [0-9] / "-" / "." / "_" / "~"
	 *
	 * @return code_verifier generato (64 caratteri)
	 */
	public static String generateCodeVerifier() {
		RandomGenerator randomGenerator = new RandomGenerator(true);
		byte[] code = new byte[48]; // 48 bytes = 64 caratteri in base64url
		randomGenerator.nextRandomBytes(code);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(code);
	}

	/**
	 * Calcola il code_challenge dal code_verifier usando SHA-256.
	 * code_challenge = BASE64URL(SHA256(ASCII(code_verifier)))
	 *
	 * @param codeVerifier Il code_verifier generato
	 * @return code_challenge calcolato
	 * @throws UtilsException se SHA-256 non è disponibile
	 */
	public static String generateCodeChallenge(String codeVerifier) throws UtilsException {
		return generateCodeChallenge(codeVerifier, OAuth2Costanti.CODE_CHALLENGE_METHOD_S256);
	}

	/**
	 * Calcola il code_challenge dal code_verifier usando il metodo specificato.
	 *
	 * Metodi supportati (RFC 7636):
	 * - S256: code_challenge = BASE64URL(SHA256(ASCII(code_verifier))) [RACCOMANDATO]
	 * - plain: code_challenge = code_verifier
	 *
	 * @param codeVerifier Il code_verifier generato
	 * @param method Il metodo da usare: "S256" o "plain"
	 * @return code_challenge calcolato
	 * @throws UtilsException se il metodo non è supportato o SHA-256 non è disponibile
	 */
	public static String generateCodeChallenge(String codeVerifier, String method) throws UtilsException {
		if (OAuth2Costanti.CODE_CHALLENGE_METHOD_PLAIN.equalsIgnoreCase(method)) {
			// Metodo plain: code_challenge = code_verifier
			return codeVerifier;
		} else if (OAuth2Costanti.CODE_CHALLENGE_METHOD_S256.equalsIgnoreCase(method)) {
			// Metodo S256: code_challenge = BASE64URL(SHA256(code_verifier))
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
				return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
			} catch (NoSuchAlgorithmException e) {
				throw new UtilsException("SHA-256 algorithm not available for PKCE code challenge generation", e);
			}
		} else {
			throw new UtilsException("Unsupported PKCE method: " + method + ". Supported methods are: S256, plain");
		}
	}

	/**
	 * Verifica se PKCE è abilitato nelle properties.
	 *
	 * @param loginProperties Properties di configurazione OAuth2
	 * @return true se PKCE è abilitato, false altrimenti (default: false)
	 */
	public static boolean isPkceEnabled(Properties loginProperties) {
		String pkceEnabled = getProperty(loginProperties, OAuth2Costanti.PROP_OAUTH2_PKCE_ENABLED);
		return pkceEnabled != null && Boolean.parseBoolean(pkceEnabled);
	}

	/**
	 * Restituisce il metodo PKCE configurato nelle properties.
	 *
	 * @param loginProperties Properties di configurazione OAuth2
	 * @return Il metodo configurato ("S256" o "plain"), default: "S256"
	 */
	public static String getPkceMethod(Properties loginProperties) {
		String method = getProperty(loginProperties, OAuth2Costanti.PROP_OAUTH2_PKCE_METHOD);
		if (method == null || method.isEmpty()) {
			return OAuth2Costanti.CODE_CHALLENGE_METHOD_S256; // Default: S256 (raccomandato)
		}
		// Normalizza il metodo (case-insensitive)
		if (OAuth2Costanti.CODE_CHALLENGE_METHOD_PLAIN.equalsIgnoreCase(method)) {
			return OAuth2Costanti.CODE_CHALLENGE_METHOD_PLAIN;
		}
		return OAuth2Costanti.CODE_CHALLENGE_METHOD_S256;
	}

	public static String getURLLoginOAuth2(Properties loginProperties, String state) throws UtilsException {
		return getURLLoginOAuth2(loginProperties, state, null);
	}

	public static String getURLLoginOAuth2(Properties loginProperties, String state, String codeChallenge) throws UtilsException {
		return getURLLoginOAuth2(loginProperties, state, codeChallenge, null);
	}

	public static String getURLLoginOAuth2(Properties loginProperties, String state, String codeChallenge, String codeChallengeMethod) throws UtilsException {
		String authorizationEndpoint = checkAndReturnValue(loginProperties, OAuth2Costanti.PROP_OAUTH2_AUTHORIZATION_ENDPOINT);
		String clientId = checkAndReturnValue(loginProperties, OAuth2Costanti.PROP_OAUTH2_CLIENT_ID);
		String callbackUri = checkAndReturnValue(loginProperties, OAuth2Costanti.PROP_OAUTH2_REDIRECT_URL);
		String scope = checkAndReturnValue(loginProperties, OAuth2Costanti.PROP_OAUTH2_SCOPE);

		String url = authorizationEndpoint +
				addFirstParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_RESPONSE_TYPE, "code") +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_REDIRECT_URI, callbackUri) +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_CLIENT_ID, clientId) +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_SCOPE, scope) +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_STATE, state);

		// Aggiungi parametri PKCE se code_challenge è fornito
		if (codeChallenge != null && !codeChallenge.isEmpty()) {
			url += addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_CODE_CHALLENGE, codeChallenge);
			// Usa il metodo fornito o default S256
			String method = codeChallengeMethod != null ? codeChallengeMethod : OAuth2Costanti.CODE_CHALLENGE_METHOD_S256;
			url += addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_CODE_CHALLENGE_METHOD, method);
		}

		return url;
	}
	private static String checkAndReturnValue(Properties loginProperties, String pName) throws UtilsException {
		String value = getProperty(loginProperties, pName);
		if(value==null || StringUtils.isEmpty(value)) {
			throw new UtilsException("Undefined property '"+pName+"'");
		}
		return value;
	}
	private static String getProperty(Properties loginProperties, String pName) {
		String value = loginProperties.getProperty(pName);
		return value!=null ? value.trim() : null;
	}

	private static void injectHttpConfig(HttpRequest req, Properties loginProperties) {
		String hostnameVerifier = getProperty(loginProperties, OAuth2Costanti.PROP_OAUTH2_HTTPS_HOSTNAME_VERIFIER);
		if (hostnameVerifier != null)
			req.setHostnameVerifier(Boolean.valueOf(hostnameVerifier));
		String trustAllCerts =  getProperty(loginProperties, OAuth2Costanti.PROP_OAUTH2_HTTPS_TRUST_ALL_CERTS);
		if (trustAllCerts != null)
			req.setTrustAllCerts(Boolean.valueOf(trustAllCerts));
		req.setTrustStorePath(getProperty(loginProperties, OAuth2Costanti.PROP_OAUTH2_HTTPS_TRUSTSTORE));
		req.setTrustStorePassword(getProperty(loginProperties, OAuth2Costanti.PROP_OAUTH2_HTTPS_TRUSTSTORE_PASSWORD));
		req.setTrustStoreType(getProperty(loginProperties, OAuth2Costanti.PROP_OAUTH2_HTTPS_TRUSTSTORE_TYPE));
		req.setCrlPath(getProperty(loginProperties, OAuth2Costanti.PROP_OAUTH2_HTTPS_TRUSTSTORE_CRL));
		
		req.setKeyStorePath(getProperty(loginProperties, OAuth2Costanti.PROP_OAUTH2_HTTPS_KEYSTORE));
		req.setKeyStorePassword(getProperty(loginProperties, OAuth2Costanti.PROP_OAUTH2_HTTPS_KEYSTORE_PASSWORD));
		req.setKeyStoreType(getProperty(loginProperties, OAuth2Costanti.PROP_OAUTH2_HTTPS_KEYSTORE_TYPE));
		req.setKeyAlias(getProperty(loginProperties, OAuth2Costanti.PROP_OAUTH2_HTTPS_KEY_ALIAS));
		req.setKeyPassword(getProperty(loginProperties, OAuth2Costanti.PROP_OAUTH2_HTTPS_KEY_PASSWORD));
	}
	
	public static OAuth2Token getToken(Logger log, Properties loginProperties, String code) {
		return getToken(log, loginProperties, code, null);
	}

	public static OAuth2Token getToken(Logger log, Properties loginProperties, String code, String codeVerifier) {

		String tokenEndpoint = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_TOKEN_ENDPOINT);
		String clientId = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_CLIENT_ID);
		String callbackUri = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_REDIRECT_URL);

		String requestTokenBody =
				getParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_GRANT_TYPE, "authorization_code") +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_CODE, code) +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_REDIRECT_URI, callbackUri) +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_CLIENT_ID, clientId);

		// Aggiungi code_verifier se PKCE è abilitato
		if (codeVerifier != null && !codeVerifier.isEmpty()) {
			requestTokenBody += addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_CODE_VERIFIER, codeVerifier);
		}

		HttpRequest httpRequest = new HttpRequest();

		httpRequest.setMethod(HttpRequestMethod.POST);
		httpRequest.setUrl(tokenEndpoint);
		httpRequest.setContent(requestTokenBody.getBytes()); 
		httpRequest.setContentType(OAuth2Costanti.MEDIA_TYPE_APPLICATION_X_WWW_FORM_URLENCODED);
		httpRequest.setFollowRedirects(false);
		httpRequest.setReadTimeout(getReadTimeout(log, loginProperties));
		httpRequest.setConnectTimeout(getConnectionTimeout(log, loginProperties));
		httpRequest.setDisconnect(false); // Non chiudere la connessione subito, serve per leggere la risposta
		httpRequest.addHeader(OAuth2Costanti.HEADER_NAME_ACCEPT, OAuth2Costanti.MEDIA_TYPE_APPLICATION_JSON);

		injectHttpConfig(httpRequest, loginProperties);
		
		OAuth2Token response = new OAuth2Token();
		try {
			// chiamo servizio token
			HttpResponse httpResponse = HttpUtilities.httpInvoke(httpRequest);

			response.setReturnCode(httpResponse.getResultHTTPOperation());

			String responseBody = new String(httpResponse.getContent());
			Map<String,Serializable> map = JSONUtils.getInstance().convertToMap(log, responseBody, responseBody);

			response.setMap(map);
			response.setRaw(responseBody);

			// Verifica errori nella risposta
			if (httpResponse.getResultHTTPOperation() != 200) {
				String tokenError = (String) map.get(OAuth2Costanti.FIELD_NAME_ERROR);
				String tokenErrorDescription = (String) map.get(OAuth2Costanti.FIELD_NAME_ERROR_DESCRIPTION);
				logError(log, MessageFormat.format("Errore durante l''acquisizione del token: {0}, {1}", tokenError, tokenErrorDescription));

				response.setError(tokenError);
				response.setDescription(tokenErrorDescription);
			}

			// estraggo token
			String accessToken = (String) map.get(OAuth2Costanti.FIELD_NAME_ACCESS_TOKEN);

			JWTParser jwtParser = new JWTParser(accessToken);

			String kid = jwtParser.getHeaderClaim(OAuth2Costanti.FIELD_NAME_KID);
			String alg = jwtParser.getHeaderClaim(OAuth2Costanti.FIELD_NAME_ALGORITHM);

			response.setAccessToken(accessToken);
			response.setKid(kid);
			response.setAlg(alg);

			String expireInS = (String) map.get(OAuth2Costanti.FIELD_NAME_EXPIRES_IN);
			response.setExpiresIn(Long.parseLong(expireInS));
			response.setRefreshToken((String) map.get(OAuth2Costanti.FIELD_NAME_REFRESH_TOKEN));
			response.setScope((String) map.get(OAuth2Costanti.FIELD_NAME_SCOPE));
			response.setTokenType((String) map.get(OAuth2Costanti.FIELD_NAME_TOKEN_TYPE));
			response.setIdToken((String) map.get(OAuth2Costanti.FIELD_NAME_ID_TOKEN));
			if(response.getExpiresIn() != null) {
				response.setExpiresAt(DateManager.getTimeMillis() + response.getExpiresIn() * 1000);
			}

		} catch (UtilsException e) {
			logError(log, "Errore durante l'acquisizione del token: " + e.getMessage(), e);
			response.setReturnCode(500);
			response.setError("Errore durante l'acquisizione del token");
			response.setDescription(e.getMessage());
		}

		return response;
	}

	public static Oauth2BaseResponse getCertificati(Logger log, Properties loginProperties) {
		HttpRequest jwksHttpRequest = new HttpRequest();

		String jwksEndpoint = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_JWKS_ENDPOINT);

		jwksHttpRequest.setMethod(HttpRequestMethod.GET);
		jwksHttpRequest.setUrl(jwksEndpoint);
		jwksHttpRequest.setFollowRedirects(false);
		jwksHttpRequest.setReadTimeout(getReadTimeout(log, loginProperties));
		jwksHttpRequest.setConnectTimeout(getConnectionTimeout(log, loginProperties));
		jwksHttpRequest.setDisconnect(false); // Non chiudere la connessione subito, serve per leggere la risposta

		injectHttpConfig(jwksHttpRequest, loginProperties);
		
		Oauth2BaseResponse response = new Oauth2BaseResponse();
		try {
			HttpResponse jwksHttpResponse = HttpUtilities.httpInvoke(jwksHttpRequest);
			String jwksResponseBody = new String(jwksHttpResponse.getContent());
			Map<String,Serializable> jwksMap = JSONUtils.getInstance().convertToMap(log, jwksResponseBody, jwksResponseBody);

			response.setReturnCode(jwksHttpResponse.getResultHTTPOperation());

			response.setMap(jwksMap);
			response.setRaw(jwksResponseBody);

			// Verifica errori nella risposta
			if (jwksHttpResponse.getResultHTTPOperation() != 200) {
				// Errore nel richiedere i certificati
				String tokenError = (String) jwksMap.get(OAuth2Costanti.FIELD_NAME_ERROR);
				String tokenErrorDescription = (String) jwksMap.get(OAuth2Costanti.FIELD_NAME_ERROR_DESCRIPTION);
				OAuth2Utilities.logError(log, "Errore durante la lettura dei certificati: " + tokenError + ", " + tokenErrorDescription);

				response.setError(tokenError);
				response.setDescription(tokenErrorDescription);
			}
		} catch (UtilsException e) {
			logError(log, "Errore durante la lettura dei certificati: " + e.getMessage(), e);
			response.setReturnCode(500);
			response.setError("Errore durante la lettura dei certificati");
			response.setDescription(e.getMessage());
		}

		return response;
	}

	public static Oauth2UserInfo getUserInfo(Logger log, Properties loginProperties, OAuth2Token oAuth2Token) {
		HttpRequest userInfoHttpRequest = new HttpRequest();

		String userInfoEndpoint = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_USER_INFO_ENDPOINT);

		userInfoHttpRequest.setMethod(HttpRequestMethod.GET);
		userInfoHttpRequest.setUrl(userInfoEndpoint);
		userInfoHttpRequest.setFollowRedirects(false);
		userInfoHttpRequest.setReadTimeout(getReadTimeout(log, loginProperties));
		userInfoHttpRequest.setConnectTimeout(getConnectionTimeout(log, loginProperties));
		userInfoHttpRequest.setDisconnect(false); // Non chiudere la connessione subito, serve per leggere la risposta
		userInfoHttpRequest.addHeader(OAuth2Costanti.HEADER_NAME_ACCEPT, OAuth2Costanti.MEDIA_TYPE_APPLICATION_JSON);
		userInfoHttpRequest.addHeader(OAuth2Costanti.HEADER_NAME_AUTHORIZATION, "Bearer " + oAuth2Token.getAccessToken());

		injectHttpConfig(userInfoHttpRequest, loginProperties);
		
		Oauth2UserInfo response = new Oauth2UserInfo();
		try {
			HttpResponse userInfoHttpResponse = HttpUtilities.httpInvoke(userInfoHttpRequest);

			String userInfoResponseBody = new String(userInfoHttpResponse.getContent());
			Map<String,Serializable> userInfoMap = JSONUtils.getInstance().convertToMap(log, userInfoResponseBody, userInfoResponseBody);

			response.setReturnCode(userInfoHttpResponse.getResultHTTPOperation());

			response.setMap(userInfoMap);
			response.setRaw(userInfoResponseBody);

			// Verifica errori nella risposta
			if (userInfoHttpResponse.getResultHTTPOperation() != 200) {
				// Errore nel richiedere userinfo
				String tokenError = (String) userInfoMap.get(OAuth2Costanti.FIELD_NAME_ERROR);
				String tokenErrorDescription = (String) userInfoMap.get(OAuth2Costanti.FIELD_NAME_ERROR_DESCRIPTION);
				OAuth2Utilities.logError(log, "Errore durante la lettura delle informazioni utente: " + tokenError + ", " + tokenErrorDescription);

				response.setError(tokenError);
				response.setDescription(tokenErrorDescription);
			}

		} catch (UtilsException e) {
			logError(log, "Errore durante la lettura delle informazioni utente: " + e.getMessage(), e);
			response.setReturnCode(500);
			response.setError("Errore durante la lettura delle informazioni utente");
			response.setDescription(e.getMessage());
		}

		return response;
	}

	/**
	 * Valida un token OAuth2 verificando la firma JWT.
	 * Questo metodo esegue solo la verifica della firma crittografica.
	 *
	 * @param log Logger
	 * @param jwksResponse Response contenente i certificati JWKS
	 * @param oAuth2Token Token OAuth2 da validare
	 * @return true se la firma è valida, false altrimenti
	 */
	public static boolean isValidToken(Logger log, Oauth2BaseResponse jwksResponse, OAuth2Token oAuth2Token) {
		// estraggo info dal token
		String accessToken = oAuth2Token.getAccessToken();
		String kid = oAuth2Token.getKid();
		String algoritm = oAuth2Token.getAlg();

		JWTOptions optionsVerify = new JWTOptions(JOSESerialization.COMPACT);

		try {
			JWKSet jwkSet = new JWKSet(jwksResponse.getRaw());

			JsonVerifySignature	jsonVerify = new JsonVerifySignature(jwkSet.getJsonWebKeys(), false, kid, algoritm, optionsVerify);

			return jsonVerify.verify(accessToken);
		} catch (UtilsException e) {
			logError(log, "Errore durante la verifica del token: " + e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Valida un token OAuth2 verificando sia la firma JWT che i claim configurati.
	 * Questo metodo esegue una validazione completa che include:
	 * 1. Verifica della firma crittografica (tramite JWKS)
	 * 2. Validazione dei claim configurati nelle properties (oauth2.claims.*)
	 *
	 * @param log Logger
	 * @param loginProperties Properties contenenti la configurazione OAuth2 (include oauth2.claims.*)
	 * @param jwksResponse Response contenente i certificati JWKS
	 * @param oAuth2Token Token OAuth2 da validare
	 * @return true se sia firma che claim sono validi, false altrimenti
	 */
	public static boolean isValidToken(Logger log, Properties loginProperties,
			Oauth2BaseResponse jwksResponse, OAuth2Token oAuth2Token) {

		// 1. Verifica firma JWT
		boolean signatureValid = isValidToken(log, jwksResponse, oAuth2Token);
		if (!signatureValid) {
			logError(log, "Validazione token fallita: firma JWT non valida");
			return false;
		}

		// 2. Verifica claim configurati (se presenti)
		Oauth2ClaimValidator claimValidator = new Oauth2ClaimValidator(log, loginProperties);
		Oauth2ClaimValidator.ValidationResult claimResult = claimValidator.validate(oAuth2Token.getAccessToken());

		if (!claimResult.isValid()) {
			logError(log, "Validazione token fallita: claim non validi - " + claimResult.getErrorsAsString());
			return false;
		}

		// Token valido sia per firma che per claim
		if (log != null) {
			log.debug("Token validato con successo (firma + claim)");
		}
		return true;
	}

	public static OAuth2Token refreshToken(Logger log, Properties loginProperties, String refreshToken) {

		String tokenEndpoint = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_TOKEN_ENDPOINT);
		String clientId = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_CLIENT_ID);

		String refreshTokenBody =
				getParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_GRANT_TYPE, "refresh_token") +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_REFRESH_TOKEN, refreshToken) +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_CLIENT_ID, clientId);

		HttpRequest httpRequest = new HttpRequest();

		httpRequest.setMethod(HttpRequestMethod.POST);
		httpRequest.setUrl(tokenEndpoint);
		httpRequest.setContent(refreshTokenBody.getBytes()); 
		httpRequest.setContentType(OAuth2Costanti.MEDIA_TYPE_APPLICATION_X_WWW_FORM_URLENCODED);
		httpRequest.setFollowRedirects(false);
		httpRequest.setReadTimeout(getReadTimeout(log, loginProperties));
		httpRequest.setConnectTimeout(getConnectionTimeout(log, loginProperties));
		httpRequest.setDisconnect(false); // Non chiudere la connessione subito, serve per leggere la risposta
		httpRequest.addHeader(OAuth2Costanti.HEADER_NAME_ACCEPT, OAuth2Costanti.MEDIA_TYPE_APPLICATION_JSON);

		injectHttpConfig(httpRequest, loginProperties);
		
		OAuth2Token response = new OAuth2Token();
		try {
			// chiamo servizio token
			HttpResponse httpResponse = HttpUtilities.httpInvoke(httpRequest);

			response.setReturnCode(httpResponse.getResultHTTPOperation());

			String responseBody = new String(httpResponse.getContent());
			Map<String,Serializable> map = JSONUtils.getInstance().convertToMap(log, responseBody, responseBody);

			response.setMap(map);
			response.setRaw(responseBody);

			// Verifica errori nella risposta
			if (httpResponse.getResultHTTPOperation() != 200) {
				String tokenError = (String) map.get(OAuth2Costanti.FIELD_NAME_ERROR);
				String tokenErrorDescription = (String) map.get(OAuth2Costanti.FIELD_NAME_ERROR_DESCRIPTION);
				logError(log, "Errore durante l'acquisizione del token: " + tokenError + ", " + tokenErrorDescription);

				response.setError(tokenError);
				response.setDescription(tokenErrorDescription);
			}

			// estraggo token
			String accessToken = (String) map.get(OAuth2Costanti.FIELD_NAME_ACCESS_TOKEN);
			JWTParser jwtParser = new JWTParser(accessToken);

			String kid = jwtParser.getHeaderClaim(OAuth2Costanti.FIELD_NAME_KID);
			String alg = jwtParser.getHeaderClaim(OAuth2Costanti.FIELD_NAME_ALGORITHM);

			response.setAccessToken(accessToken);
			response.setKid(kid);
			response.setAlg(alg);

			String expireInS = (String) map.get(OAuth2Costanti.FIELD_NAME_EXPIRES_IN);
			response.setExpiresIn(Long.parseLong(expireInS));
			response.setRefreshToken((String) map.get(OAuth2Costanti.FIELD_NAME_REFRESH_TOKEN));
			response.setScope((String) map.get(OAuth2Costanti.FIELD_NAME_SCOPE));
			response.setTokenType((String) map.get(OAuth2Costanti.FIELD_NAME_TOKEN_TYPE));
			response.setIdToken((String) map.get(OAuth2Costanti.FIELD_NAME_ID_TOKEN));
			if(response.getExpiresIn() != null) {
				response.setExpiresAt(DateManager.getTimeMillis() + response.getExpiresIn() * 1000);
			}

		} catch (UtilsException e) {
			logError(log, "Errore durante il refresh del token: " + e.getMessage(), e);
			response.setReturnCode(500);
			response.setError("Errore durante il refresh del token");
			response.setDescription(e.getMessage());
		}

		return response;
	}

	public static void logError(Logger log, String msg) {
		logError(log, msg, null);
	}

	public static void logError(Logger log, String msg, Throwable e) {
		if(e != null) {
			log.error(msg,e);
		} else {
			log.error(msg);
		}
	}

	private static int getReadTimeout(Logger log, Properties loginProperties) {
		String readTimeout = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_READ_TIMEOUT);
		if (readTimeout != null && !readTimeout.isEmpty()) {
			try {
				return Integer.parseInt(readTimeout);
			} catch (NumberFormatException e) {
				logError(log, "Errore nel parsing del read timeout: " + e.getMessage(), e);
			}
		}
		return 15000; // Default read timeout
	}

	private static int getConnectionTimeout(Logger log, Properties loginProperties) {
		String connectTimeout = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_CONNECT_TIMEOUT);
		if (connectTimeout != null && !connectTimeout.isEmpty()) {
			try {
				return Integer.parseInt(connectTimeout);
			} catch (NumberFormatException e) {
				logError(log, "Errore nel parsing del connection timeout: " + e.getMessage(), e);
			}
		}
		return 10000; // Default connection timeout
	}

	public static String creaUrlLogout(String idToken, String oauth2LogoutUrl, String redirPageUrl) {
		return oauth2LogoutUrl +
				addFirstParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_ID_TOKEN_HINT, idToken) +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_POST_LOGOUT_REDIRECT_URI, redirPageUrl);
	}
}
