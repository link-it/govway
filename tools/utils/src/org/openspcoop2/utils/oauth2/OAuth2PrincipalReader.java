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

import java.util.Properties;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.credential.IPrincipalReader;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderException;
import org.slf4j.Logger;

/**
 * Implementazione dell'interfaccia {@link IPrincipalReader} che legge il principal dalla sessione, dopo che l'utenza e' stata autenticata con oauth2.
 * Durante la fase di lettura del principal dalla sessione viene effettuato anche il controllo di validata del token e se previsto viene fatto il refresh.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OAuth2PrincipalReader implements IPrincipalReader {

	private transient Logger log = null;
	private Properties properties = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OAuth2PrincipalReader(Logger log){
		this.log = log;
		this.log.debug("OAuth2PrincipalReader inizializzato.");
	}

	@Override
	public String getPrincipal(HttpServletRequest request) throws PrincipalReaderException {
		this.log.debug("Estrazione principal in corso...");
		HttpSession session = request.getSession(false);

		String principalClaim = this.properties.getProperty(OAuth2Costanti.PROP_OAUTH2_PRINCIPAL_CLAIM);

		// Se l’utente NON è autenticato e non siamo già in login
		boolean loggedIn = (session != null && session.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_USER_INFO) != null);

		if(loggedIn) {
			Oauth2UserInfo userInfo = (Oauth2UserInfo) session.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_USER_INFO);

			String principal = (String) userInfo.getMap().get(principalClaim);

			if (principal == null) {
				this.log.warn("Principal claim [{}] non trovato in userInfo: {}", principalClaim, userInfo.getMap());
				return null;
			}

			Long expiresAt = (Long) session.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_TOKEN_EXPIRES_AT);
			String refreshToken = (String) session.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_REFRESH_TOKEN);

			// token non scade mai o non ancora scaduto
			if (expiresAt == null || expiresAt < DateManager.getTimeMillis()) {
				this.log.debug("Username collegato: [{}]", principal);
				return principal;
			}

			// token scaduto, ma non ho un refresh token
			if (refreshToken == null) {
				this.log.warn("Token scaduto e non è disponibile un refresh token. L'utente deve effettuare nuovamente il login.");
				request.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ERROR_CODE, OAuth2Costanti.ERRORE_ACCESS_DENIED);
				request.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ERROR_DETAIL, "Token scaduto e non disponibile un refresh token.");
				return null;						
			}

			OAuth2Token oAuth2Token = refreshToken(request, refreshToken);

			// richiesta dei certificati
			Oauth2BaseResponse jwksResponse = OAuth2Utilities.getCertificati(this.log, this.properties); 

			// Verifica errori nella risposta
			if (jwksResponse.getReturnCode() != 200) {
				// Errore nel richiedere i certificati
				request.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ERROR_CODE, jwksResponse.getError());
				request.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ERROR_DETAIL, jwksResponse.getDescription());
				return null;
			}

			// validazione token (firma + claim configurati)
			boolean valida = OAuth2Utilities.isValidToken(this.log, this.properties, jwksResponse, oAuth2Token);

			if (!valida) {
				// Token ricevuto non valido
				logError(this.log, OAuth2Costanti.ERROR_MSG_TOKEN_RICEVUTO_NON_VALIDO);

				request.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ERROR_CODE, OAuth2Costanti.ERROR_MSG_TOKEN_RICEVUTO_NON_VALIDO);
				request.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ERROR_DETAIL, OAuth2Costanti.ERROR_MSG_TOKEN_RICEVUTO_NON_VALIDO);
				return null;
			}

			Oauth2UserInfo oauth2UserInfo = OAuth2Utilities.getUserInfo(this.log, this.properties, oAuth2Token);

			// Verifica errori nella risposta
			if (oauth2UserInfo.getReturnCode() != 200) {
				// Errore nel richiedere userinfo
				request.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ERROR_CODE, oauth2UserInfo.getError());
				request.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ERROR_DETAIL, oauth2UserInfo.getDescription());
				return null;
			}

			// Salva token e scadenza se vuoi gestire refresh
			session.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ACCESS_TOKEN, oAuth2Token.getAccessToken());
			session.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ID_TOKEN, oAuth2Token.getIdToken());
			session.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ACCESS_TOKEN_OBJ, oAuth2Token);
			session.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_REFRESH_TOKEN, oAuth2Token.getRefreshToken());
			session.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_TOKEN_EXPIRES_AT, oAuth2Token.getExpiresAt());
			session.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_USER_INFO, oauth2UserInfo);

			this.log.debug("Username collegato: [{}]", principal);
			return principal;

		}

		this.log.warn("Utenza non autenticata");
		return null;
	}

	private OAuth2Token refreshToken(HttpServletRequest request, String refreshToken) {
		OAuth2Token oAuth2Token = OAuth2Utilities.refreshToken(this.log, this.properties, refreshToken);

		// Verifica errori nella risposta
		if (oAuth2Token.getReturnCode() != 200) {
			request.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ERROR_CODE, oAuth2Token.getError());
			request.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ERROR_DETAIL, oAuth2Token.getDescription());
		}
		
		return oAuth2Token;
	}

	@Override
	public void init(Object... parametri) throws PrincipalReaderException {
		if(parametri!=null && parametri.length>0) {
			Object pObject = parametri[0];
			if(pObject instanceof Properties) {
				this.properties = (Properties) pObject;


			}
		}
	}

	private static void logError(Logger log, String msg) {
		logError(log, msg, null);
	}

	private static void logError(Logger log, String msg, Throwable e) {
		if(e != null) {
			log.error(msg,e);
		} else {
			log.error(msg);
		}
	}
}
