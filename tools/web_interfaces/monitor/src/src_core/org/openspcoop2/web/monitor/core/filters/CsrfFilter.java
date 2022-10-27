package org.openspcoop2.web.monitor.core.filters;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

public class CsrfFilter implements Filter {

	/** Logger utilizzato per debug. * */
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	// configurazione filtro
	@SuppressWarnings("unused")
	private FilterConfig filterConfig = null;

	// pagina da mostrare all'utente al posto di quella richiesta
	private static final String jspErrore = "/commons/pages/welcome.jsf";
	
	// id delle variabili in sessione per il controllo del messaggio da
	// visualizzare all'utente
	private static final String msgErroreKey = "acclim";

	private static final String accLimKey = "acclimflag";
	
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
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			HttpSession session = request.getSession();
			
			String sessionTokenCSRF = CsrfFilter.leggiTokenCSRF(session);
			CsrfFilter.log.debug("Letto Token CSRF in sessione: ["+sessionTokenCSRF+"]");
			try {
				if(this.isRichiestaScrittura(request)) {
					String tokenCSRF = request.getParameter(Costanti.PARAMETRO_CSRF_TOKEN);
					CsrfFilter.log.debug("Ricevuto Token CSRF: ["+tokenCSRF+"]"); 

					boolean verificaTokenCSRF = ServletUtils.verificaTokenCSRF(tokenCSRF, sessionTokenCSRF, this.validitaTokenCsrf);

					// forzo la rigenerazione del token
					sessionTokenCSRF = null;
					
					if(!verificaTokenCSRF) {
						CsrfFilter.log.debug("Il Token CSRF ricevuto non e' valido."); 
						
						// setto gli attributi che riguardano il
						// messaggio da visualizzare all'utente
						session.setAttribute(msgErroreKey, Costanti.MESSAGGIO_ERRORE_CSRF_TOKEN_NON_VALIDO);
						session.setAttribute(accLimKey, true);
						// redirect
						response.sendRedirect(request.getContextPath()+ jspErrore);
						
						// blocco la catena di filtri
						return;
					}
				}
			} finally {
				if(sessionTokenCSRF == null) {
					String nuovoTokenCSRF = CsrfFilter.generaESalvaTokenCSRF(session);
					CsrfFilter.log.debug("Generato Nuovo Token CSRF: ["+nuovoTokenCSRF+"]");
				}
			}
			
			// faccio proseguire le chiamate ai filtri
			chain.doFilter(request, response);
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (ServletException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
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
		//		String uuId = UUID.randomUUID().toString().replace("-", ""); ServletUtils.generaTokenCSRF(uuId);
		String uuId = UUID.randomUUID().toString().replace("-", ""); 
		String nuovoToken = ServletUtils.generaTokenCSRF(uuId);
		session.setAttribute(Costanti.SESSION_ATTRIBUTE_CSRF_TOKEN, nuovoToken);
		return nuovoToken;
	}

	public static String leggiTokenCSRF(HttpSession session) {
		if(session == null) return null;
		return (String) session.getAttribute(Costanti.SESSION_ATTRIBUTE_CSRF_TOKEN);
	}

	private boolean isRichiestaScrittura(HttpServletRequest request) {
		// Le operazioni di scrittura sul monitor sono solo due:
		// salvataggio password utente e salvataggio profilo utente
		if(request.getParameter(Costanti.ID_BUTTON_SALVA_PROFILO) != null || request.getParameter(Costanti.ID_BUTTON_SALVA_PASSWORD) != null)  {
			return true;
		}

		return false;
	}
}
