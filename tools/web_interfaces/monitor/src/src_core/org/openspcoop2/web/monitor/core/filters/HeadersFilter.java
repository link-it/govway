/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Properties;
import java.util.UUID;

import javax.faces.application.ViewExpiredException;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.security.SecurityProperties;
import org.openspcoop2.web.lib.mvc.security.SecurityWrappedHttpServletRequest;
import org.openspcoop2.web.lib.mvc.security.SecurityWrappedHttpServletResponse;
import org.openspcoop2.web.lib.mvc.security.Validatore;
import org.openspcoop2.web.lib.mvc.security.exception.ValidationException;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**     
 * HeadersFilter
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HeadersFilter implements Filter {

	private static final String IL_PARAMETRO_0_CONTIENE_UN_VALORE_NON_VALIDO_1 = "Il parametro [{0}] contiene un valore non valido [{1}].";

	/** Logger utilizzato per debug. * */
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	// configurazione filtro
	private FilterConfig filterConfig = null;

	private String cspHeaderValue;
	private String xContentTypeOptionsHeaderValue = null;
	private String xXssProtectionHeaderValue = null;
	private String xFrameOptionsHeaderValue = null;
	private String jQueryVersion = null; 

	// messaggio da visualizzare per l'utente
	private static final String MSG_AUTH_ERRORE = "Autorizzazione negata.";

	// pagina da mostrare all'utente al posto di quella richiesta
	public static final String JSP_ERRORE = "/commons/pages/welcome.jsf";
	public static final String JSP_LOGIN = "/public/login.jsf";

	// id delle variabili in sessione per il controllo del messaggio da visualizzare all'utente
	public static final String MSG_ERRORE_KEY = "acclimHF";
	public static final String ACCLIM_KEY = "acclimflagHF";
	

	@Override
	public void init(FilterConfig filterConfig) {

		this.filterConfig = filterConfig;
		this.jQueryVersion = this.filterConfig.getInitParameter(Costanti.FILTER_INIT_PARAMETER_JQUERY_VERSION);
		try {
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(log);
			this.cspHeaderValue = pddMonitorProperties.getCspHeaderValue();
			this.xContentTypeOptionsHeaderValue = pddMonitorProperties.getXContentTypeOptionsHeaderValue();
			this.xFrameOptionsHeaderValue = pddMonitorProperties.getXFrameOptionsHeaderValue();
			this.xXssProtectionHeaderValue = pddMonitorProperties.getXXssProtectionHeaderValue();

			Properties consoleSecurityConfiguration = pddMonitorProperties.getConsoleSecurityConfiguration();
			SecurityProperties.init(consoleSecurityConfiguration, log);
			Validatore.init(SecurityProperties.getInstance(), log);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws java.io.IOException, jakarta.servlet.ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		SecurityWrappedHttpServletRequest seqReq = new SecurityWrappedHttpServletRequest(request, log);
		HttpSession session = seqReq.getSession();

		SecurityWrappedHttpServletResponse seqRes = new SecurityWrappedHttpServletResponse(response, log);
		
		String urlRichiesta = request.getServletPath();
		
		boolean utenteLoggato = Utility.isUtenteLoggato(session);
		Utility.setLoginBeanErrorMessage(session, HeadersFilter.class.getName(), null, null);
		
		try {
			// set versione jQuery
			seqReq.setAttribute(Costanti.REQUEST_ATTRIBUTE_JQUERY_VERSION, this.jQueryVersion);

			// Gestione vulnerabilita' Content Security Policy
			this.gestioneContentSecurityPolicy(seqReq, seqRes); 

			// Aggiungo header
			this.gestioneXContentTypeOptions(seqRes);

			// Validazione sintassi parametri
			this.validazioneSintatticaParametri(seqReq);

			// all'interno della pagina chiamata nel redirect mostro il messaggio di info.
			if (urlRichiesta.indexOf("/pages/welcome.jsf") != -1) {
				boolean used = session.getAttribute(ACCLIM_KEY) != null && ((Boolean) session.getAttribute(ACCLIM_KEY));

				// prima volta che viene richiamata la pagina welcome dopo la
				// redirect
				// indico che ho usato la informazione.
				if (used) {
					Utility.setLoginBeanErrorMessage(session, HeadersFilter.class.getName(), MSG_AUTH_ERRORE, HttpStatus.BAD_REQUEST.value());
					session.setAttribute(ACCLIM_KEY, false);
				} else {
					// non visualizzo il messaggio
					session.setAttribute(ACCLIM_KEY, false);
					session.setAttribute(MSG_ERRORE_KEY, null);
				}
			}
			
			chain.doFilter(seqReq, seqRes);
		} catch(ValidationException e){
			log.error("Errore di validazione dei parametri: " + e.getMessage() + ", redirect verso pagina di errore: "	+ request.getContextPath() + JSP_ERRORE, e);

			if(utenteLoggato){
				// setto gli attributi che riguardano il
				// messaggio da visualizzare all'utente
				session.setAttribute(MSG_ERRORE_KEY, MSG_AUTH_ERRORE);
				session.setAttribute(ACCLIM_KEY, true);
				// redirect
				response.sendRedirect(request.getContextPath()+ JSP_ERRORE);
			} else {
				// utente non loggato va alla pagina di login
				// redirect
				response.sendRedirect(request.getContextPath()+ JSP_LOGIN);
			}
		} catch (ServletException e) {
			Throwable rootCause = e.getRootCause();
			if(rootCause != null){
				if (rootCause instanceof ViewExpiredException) { // This is true for any FacesException.

					Utility.setLoginBeanErrorMessage(session, HeadersFilter.class.getName(), null, HttpStatus.BAD_REQUEST.value());
					log.debug("Rilevata ViewExpiredException: [{}]", rootCause.getMessage()); 
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

	private void validazioneSintatticaParametri(SecurityWrappedHttpServletRequest seqReq) throws ValidationException {
		String requestUri = seqReq.getRequestURI();

		// Ottieni il percorso dalla richiesta URI
		Path path = Paths.get(requestUri);

		// Ottieni l'ultimo sotto-path es. pagina.jsf
		String lastPathSegment = path.getFileName().toString();

		// Ottenere tutti i parametri dalla richiesta
		Enumeration<String> paramNames = seqReq.getParameterNames();

		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			String parameterValueFiltrato = seqReq.getParameter(paramName);
			String parameterValueOriginale = Validatore.getInstance().getParametroOriginale(seqReq, paramName);

			// validazione generica
			if(!ServletUtils.checkParametro(seqReq, paramName)) {
				throw new ValidationException(MessageFormat.format(IL_PARAMETRO_0_CONTIENE_UN_VALORE_NON_VALIDO_1, paramName, parameterValueOriginale));
			}

			// validazione parametro csrf
			if(paramName.equals(Costanti.PARAMETRO_CSRF_TOKEN) && !ServletUtils.checkCsrfParameter(seqReq, paramName)) {
				throw new ValidationException(MessageFormat.format(IL_PARAMETRO_0_CONTIENE_UN_VALORE_NON_VALIDO_1, paramName, parameterValueOriginale));
			}

			// valdiazione parametro controllo grafici svg
			if(paramName.endsWith(BrowserFilter.PARAMETRO_SVG) && !ServletUtils.checkBooleanParameter(seqReq, paramName)){
				throw new ValidationException(MessageFormat.format(IL_PARAMETRO_0_CONTIENE_UN_VALORE_NON_VALIDO_1, paramName, parameterValueOriginale));
			}

			// validazione attacco path traversal, la forma dei valori utilizzari e' pagina.jsf, /pagina.jsf o \pagina.jsf con pagina.jsf che coincide con il contextpath della richiesta
			log.trace("Verifica attacco Path traversal per il parametro [{}], valore [{}], lastpath [{}]", paramName, parameterValueFiltrato, lastPathSegment);
			// evito il check per i parametri vuoti sulla root dell'applicazione
			if(!StringUtils.isEmpty(parameterValueFiltrato) &&         
					(
							lastPathSegment.equals(parameterValueFiltrato) 
							|| (("\\"+ lastPathSegment).equals(parameterValueFiltrato))
							|| (("/" + lastPathSegment).equals(parameterValueFiltrato))
							) 
					){
				throw new ValidationException(MessageFormat.format(IL_PARAMETRO_0_CONTIENE_UN_VALORE_NON_VALIDO_1, paramName, parameterValueOriginale));
			}
		}
	}

	@Override
	public void destroy() {
		log.debug("DISTRUIZIONE FILTRO: HeadersFilter");
		this.filterConfig = null;
	}

	private void gestioneContentSecurityPolicy(HttpServletRequest request, HttpServletResponse response) {
		// Per abilitare l'esecuzione solo degli script che vogliamo far eseguire, si genera un UUID random e si assegna ai tag script con attributo src e a gli script inline nelle pagine
		// L'id degli script abilitati e' indicato all'interno del campo script-src
		HttpSession session = request.getSession();
		String nonceValue = leggiNonceValue(session);
		
		log.debug("AAAAA Trovato nonce in sessione: {}",nonceValue);

		if(nonceValue == null) {
			String crsfCheck = request.getParameter("_crsfCheck");
			if(crsfCheck == null) {
				nonceValue = UUID.randomUUID().toString().replace("-", "");
				log.debug("AAAAA Generato nonce: {}",nonceValue);
			} else {
				nonceValue = crsfCheck;
				log.debug("AAAAA recuperato nonce: {}",nonceValue);
			}
			salvaNonceValueInSessione(session, nonceValue);
		}

		request.setAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE, nonceValue);
		log.debug("AAAAA Salvato nonce nella request: {}",nonceValue);

		if(StringUtils.isNoneBlank(this.cspHeaderValue)) {
			response.setHeader(HttpConstants.HEADER_NAME_CONTENT_SECURITY_POLICY, MessageFormat.format(this.cspHeaderValue, nonceValue, nonceValue));
			//			response.setHeader(HttpConstants.HEADER_NAME_CONTENT_SECURITY_POLICY_REPORT_ONLY, MessageFormat.format(this.cspHeaderValue, nonceValue, nonceValue))
		}
	}

	public static String leggiNonceValue(HttpSession session) {
		if(session == null) return null;
		return (String) session.getAttribute(Costanti.SESSION_ATTRIBUTE_CSP_RANDOM_NONCE);
	}

	public static void salvaNonceValueInSessione(HttpSession session, String nonceValue) {
		if(session == null) return;
		session.setAttribute(Costanti.SESSION_ATTRIBUTE_CSP_RANDOM_NONCE, nonceValue);
	}

	private void gestioneXContentTypeOptions(HttpServletResponse response) {
		if(StringUtils.isNoneBlank(this.xFrameOptionsHeaderValue)) {
			response.setHeader(HttpConstants.HEADER_NAME_X_FRAME_OPTIONS, this.xFrameOptionsHeaderValue);
		}
		if(StringUtils.isNoneBlank(this.xXssProtectionHeaderValue)) {
			response.setHeader(HttpConstants.HEADER_NAME_X_XSS_PROTECTION, this.xXssProtectionHeaderValue);
		}
		if(StringUtils.isNoneBlank(this.xContentTypeOptionsHeaderValue)) {
			response.setHeader(HttpConstants.HEADER_NAME_X_CONTENT_TYPE_OPTIONS, this.xContentTypeOptionsHeaderValue);
		}
	}

}
