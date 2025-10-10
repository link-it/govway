/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.json.JSONUtils;
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

	public static String getURLLoginOAuth2(Properties loginProperties, String state) {
		String authorizationEndpoint = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_AUTHORIZATION_ENDPOINT);
		String clientId = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_CLIENT_ID);
		String callbackUri = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_REDIRECT_URL);
		String scope = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_SCOPE);
		
		return authorizationEndpoint +
				addFirstParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_RESPONSE_TYPE, "code") +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_REDIRECT_URI, callbackUri) +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_CLIENT_ID, clientId) +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_SCOPE, scope) +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_STATE, state);
	}

	public static OAuth2Token getToken(Logger log, Properties loginProperties, String code) {

		String tokenEndpoint = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_TOKEN_ENDPOINT);
		String clientId = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_CLIENT_ID);
		String callbackUri = loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_REDIRECT_URL);

		String requestTokenBody =
				getParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_GRANT_TYPE, "authorization_code") +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_CODE, code) +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_REDIRECT_URI, callbackUri) +
				addParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_CLIENT_ID, clientId);

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
