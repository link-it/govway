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
package org.openspcoop2.web.monitor.core.filters;

import java.io.IOException;
import java.util.UUID;

import javax.faces.application.ViewExpiredException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

/**
 * Comparator
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class CsrfFilter implements Filter {

	/** Logger utilizzato per debug. * */
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	// configurazione filtro
	@SuppressWarnings("unused")
	private FilterConfig filterConfig = null;

	// pagina da mostrare all'utente al posto di quella richiesta
	private static final String JSP_ERRORE = "/commons/pages/welcome.jsf";
	
	// id delle variabili in sessione per il controllo del messaggio da
	// visualizzare all'utente
	private static final String MSG_ERRORE_KEY = "acclim";

	private static final String ACCLIM_KEY = "acclimflag";
	
	private Integer validitaTokenCsrf = null;

	/***************************************************************************
	 * Metodo destroy
	 */
	@Override
	public void destroy() {
		log.debug("DISTRUIZIONE FILTRO: CsrfFilter");
		this.filterConfig = null;
	}
	
	/***************************************************************************
	 * 
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession();
		try {
			String sessionTokenCSRF = CsrfFilter.leggiTokenCSRF(session);
			CsrfFilter.log.debug("Letto Token CSRF in sessione: [{}]", sessionTokenCSRF);
			try {
				if(this.isRichiestaScrittura(request)) {
					String tokenCSRF = request.getParameter(Costanti.PARAMETRO_CSRF_TOKEN);
					CsrfFilter.log.debug("Ricevuto Token CSRF: [{}]", tokenCSRF); 

					boolean verificaTokenCSRF = ServletUtils.verificaTokenCSRF(tokenCSRF, sessionTokenCSRF, this.validitaTokenCsrf);

					// forzo la rigenerazione del token
					sessionTokenCSRF = null;
					
					if(!verificaTokenCSRF) {
						CsrfFilter.log.debug("Il Token CSRF ricevuto non e' valido."); 
						
						// setto gli attributi che riguardano il
						// messaggio da visualizzare all'utente
						if(session!=null) {
							session.setAttribute(MSG_ERRORE_KEY, Costanti.MESSAGGIO_ERRORE_CSRF_TOKEN_NON_VALIDO);
							session.setAttribute(ACCLIM_KEY, true);
						}
						// redirect
						response.sendRedirect(request.getContextPath()+ JSP_ERRORE);
					}
				}
			} finally {
				if(sessionTokenCSRF == null) {
					String nuovoTokenCSRF = CsrfFilter.generaESalvaTokenCSRF(session);
					CsrfFilter.log.debug("Generato Nuovo Token CSRF: [{}]", nuovoTokenCSRF);
				}
			}
			
			// faccio proseguire le chiamate ai filtri
			chain.doFilter(request, response);
		} catch (ServletException e) {
			Throwable rootCause = e.getRootCause();
			if(rootCause != null){
				if (rootCause instanceof ViewExpiredException) { // This is true for any FacesException.

					Utility.setLoginBeanErrorMessage(session, CsrfFilter.class.getName(), null, HttpStatus.BAD_REQUEST.value());
					CsrfFilter.log.debug("Rilevata ViewExpiredException: [{}]", rootCause.getMessage()); 
					String redirPageUrl = request.getContextPath() + "/"+ "public/timeoutPage.jsf";
					response.sendRedirect(redirPageUrl);
				} else if (rootCause instanceof RuntimeException runtimeException) { // This is true for any FacesException.
					throw runtimeException; // Throw wrapped RuntimeException instead of ServletException.
				} else {
					throw e;
				}
			} else {
				throw e;
			}
		}
	}
	
	/***************************************************************************
	 * 
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		this.filterConfig = config;
		try {
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(log);
			this.validitaTokenCsrf = pddMonitorProperties.getValiditaTokenCsrf();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public static String generaESalvaTokenCSRF(HttpSession session) {
		String uuId = UUID.randomUUID().toString(); 
		String nuovoToken = ServletUtils.generaTokenCSRF(uuId);
		if(session != null) {
			session.setAttribute(Costanti.SESSION_ATTRIBUTE_CSRF_TOKEN, nuovoToken);
		}
		return nuovoToken;
	}

	public static String leggiTokenCSRF(HttpSession session) {
		if(session == null) return null;
		return (String) session.getAttribute(Costanti.SESSION_ATTRIBUTE_CSRF_TOKEN);
	}

	private boolean isRichiestaScrittura(HttpServletRequest request) {
		// Le operazioni di scrittura sul monitor sono solo due:
		// salvataggio password utente e salvataggio profilo utente
		return (request.getParameter(Costanti.ID_BUTTON_SALVA_PROFILO) != null || request.getParameter(Costanti.ID_BUTTON_SALVA_PASSWORD) != null);
	}
}
