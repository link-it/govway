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

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * Servlet di callback per il flusso OAuth2.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BaseOAuth2CallbackServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected BaseOAuth2CallbackServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGetEngine(request, response, this.getLog(), this.getLoginProperties());
	}

	private void doGetEngine(HttpServletRequest request, HttpServletResponse response, Logger log, Properties loginProperties) {
		HttpSession session = request.getSession(false);
		try {

			String code = validaParametriRichiesta(request, session);

			OAuth2Token oAuth2Token = OAuth2Utilities.getToken(log, loginProperties, code);

			if (oAuth2Token.getReturnCode() != 200) {
				throw new Oauth2Exception( oAuth2Token.getError(), oAuth2Token.getDescription());
			}

			// richiesta dei certificati
			Oauth2BaseResponse jwksResponse = OAuth2Utilities.getCertificati(log, loginProperties); 

			// Verifica errori nella risposta
			if (jwksResponse.getReturnCode() != 200) {
				// Errore nel richiedere i certificati
				throw new Oauth2Exception( jwksResponse.getError(), jwksResponse.getDescription());
			}

			// validazione token
			boolean valida = OAuth2Utilities.isValidToken(log, jwksResponse, oAuth2Token);

			if (!valida) {
				// Token ricevuto non valido
				OAuth2Utilities.logError(log, OAuth2Costanti.ERROR_MSG_TOKEN_RICEVUTO_NON_VALIDO);
				
				throw new Oauth2Exception(OAuth2Costanti.ERROR_MSG_TOKEN_RICEVUTO_NON_VALIDO, OAuth2Costanti.ERROR_MSG_TOKEN_RICEVUTO_NON_VALIDO);
			}


			Oauth2UserInfo oauth2UserInfo = OAuth2Utilities.getUserInfo(log, loginProperties, oAuth2Token);

			// Verifica errori nella risposta
			if (oauth2UserInfo.getReturnCode() != 200) {
				// Errore nel richiedere userinfo
				throw new Oauth2Exception( oauth2UserInfo.getError(), oauth2UserInfo.getDescription());
			}

			// ricreo la sessione se non presente
			session = request.getSession(true);

			// Salva token e scadenza se vuoi gestire refresh
			session.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ACCESS_TOKEN, oAuth2Token.getAccessToken());
			session.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ID_TOKEN, oAuth2Token.getIdToken());
			session.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ACCESS_TOKEN_OBJ, oAuth2Token);
			session.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_REFRESH_TOKEN, oAuth2Token.getRefreshToken());
			session.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_TOKEN_EXPIRES_AT, oAuth2Token.getExpiresAt());
			session.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_USER_INFO, oauth2UserInfo);

			// 7) Redirect alla home o pagina protetta
			response.sendRedirect(this.getConsoleHome(request));
		} catch (IOException e) {
			log.error("Errore durante lo scambio del token OAuth2: " + e.getMessage(), e);
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			try {
				response.getWriter().write("Autenticazione Oauth2 non disponibile: si e' verificato un'errore durante lo scambio del token OAuth2: " + e.getMessage());
			} catch (IOException e1) {
				log.error("Errore durante lo scambio del token OAuth2: " + e1.getMessage(), e1);
			}
		} catch (Oauth2Exception e) {
			// Token ricevuto non valido
			OAuth2Utilities.logError(log, e.getErrorCode() + ": " + e.getErrorDetail());

			request.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ERROR_CODE, e.getErrorCode());
			request.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ERROR_DETAIL, e.getErrorDetail());
			try {
				response.sendRedirect(this.getConsoleError(request));
			} catch (IOException e1) {
				log.error("Errore durante esecuzione redirect: " + e1.getMessage(), e1);
			}
		}
	}

	private String validaParametriRichiesta(HttpServletRequest request, HttpSession session) throws Oauth2Exception {
		String code  = request.getParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_CODE);
		String state = request.getParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_STATE); // opzionale, ma consigliato
		String error = request.getParameter(OAuth2Costanti.PARAM_NAME_OAUTH2_ERROR); // in caso di errore)
		if (error != null) {
			// L'utente ha negato l'accesso o si è verificato un errore
			throw new Oauth2Exception(HttpServletResponse.SC_UNAUTHORIZED+"", "Accesso OAuth2 negato o errore: " + error);
		}

		if (code == null || code.isEmpty()) {
			throw new Oauth2Exception(HttpServletResponse.SC_BAD_REQUEST+"",  "Parametro 'code' mancante.");
		}

		// verifica state
		if (StringUtils.isNotBlank(state)) {
			String stateFromSession = session != null ? (String) session.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_OAUTH2_STATE) : null ;
			if(StringUtils.isBlank(stateFromSession) || !stateFromSession.equals(state)) {
				throw new Oauth2Exception(HttpServletResponse.SC_BAD_REQUEST+"", "Parametro 'state' non valido.");
			}
		}
		return code;
	}

	protected abstract Logger getLog();
	protected abstract Properties getLoginProperties();
	protected abstract String getConsoleHome(HttpServletRequest request); 
	protected abstract String getConsoleError(HttpServletRequest request);
}
