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
package org.openspcoop2.web.ctrlstat.servlet.login;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.oauth2.OAuth2Costanti;
import org.openspcoop2.utils.oauth2.OAuth2Utilities;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.springframework.http.HttpStatus;

/**
 * OAuth2LoginStartServlet
 *
 * @author Pintori Giuliano (giuliano.pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OAuth2LoginStartServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public OAuth2LoginStartServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
		engineDoGet(httpServletRequest, httpServletResponse);
	}

	private void engineDoGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		// login utenza
		GeneralHelper generalHelper = null;
		Properties loginProperties = null;

		HttpSession session = httpServletRequest.getSession();
		try {
			loginProperties = ConsoleProperties.getInstance().getLoginProperties();


			String state = UUID.randomUUID().toString();

			// Gestione PKCE (Proof Key for Code Exchange)
			String codeChallenge = null;
			String codeChallengeMethod = null;
			if (OAuth2Utilities.isPkceEnabled(loginProperties)) {
				// Genera code_verifier e salva in sessione
				String codeVerifier = OAuth2Utilities.generateCodeVerifier();
				session.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_CODE_VERIFIER, codeVerifier);

				// Calcola code_challenge dal code_verifier
				codeChallengeMethod = OAuth2Utilities.getPkceMethod(loginProperties);
				codeChallenge = OAuth2Utilities.generateCodeChallenge(codeVerifier, codeChallengeMethod);

				ControlStationCore.logDebug("[OAuth2LoginStart] PKCE enabled, method: " + codeChallengeMethod);
			}

			// 1) Costruisci l'URL di autorizzazione con parametri PKCE se abilitato
			String authorizationUrl = OAuth2Utilities.getURLLoginOAuth2(loginProperties, state, codeChallenge, codeChallengeMethod);

			session.setAttribute(OAuth2Costanti.ATTRIBUTE_NAME_OAUTH2_STATE, state);

			// Log debug sessione iniziale
			ControlStationCore.logDebug("[OAuth2LoginStart] Session ID: " + session.getId() + ", State salvato: " + state);

			httpServletResponse.sendRedirect(authorizationUrl);
		} catch (IOException e) {
			ControlStationCore.logError("Si e' verificato un errore il login OAuth2, impossibile autenticare l'utente: " + e.getMessage(),e);
			try {
			AuthorizationFilter.setErrorMsg(generalHelper, session, httpServletRequest, httpServletResponse, LoginCostanti.INFO_JSP, 
					LoginCostanti.LABEL_LOGIN_ERRORE, httpServletRequest.getServletContext(), HttpStatus.SERVICE_UNAVAILABLE);
			} catch (ServletException | IOException e1) {
				ControlStationCore.logError("Errore durante esecuzione redirect: " + e1.getMessage(), e1);
			}
		} catch (UtilsException | OpenSPCoop2ConfigurationException e) {
			ControlStationCore.logError("Errore durante la lettura delle properties: " + e.getMessage(),e);
			httpServletResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			try {
				httpServletResponse.getWriter().write(OAuth2Costanti.ERROR_MSG_AUTENTICAZIONE_OAUTH2_NON_DISPONIBILE_SI_E_VERIFICATO_UN_ERRORE + e.getMessage());
			} catch (IOException e1) {
				ControlStationCore.logError("Errore durante esecuzione redirect: " + e1.getMessage(), e1);
			}
		}
	}
}
