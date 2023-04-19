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
import java.text.MessageFormat;
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

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

/**     
 * HeadersFilter
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HeadersFilter implements Filter {

	/** Logger utilizzato per debug. * */
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	// configurazione filtro
	@SuppressWarnings("unused")
	private FilterConfig filterConfig = null;
	
	private String cspHeaderValue;
	private String xContentTypeOptionsHeaderValue = null;
	private String xXssProtectionHeaderValue = null;
	private String xFrameOptionsHeaderValue = null;

	@Override
	public void init(FilterConfig filterConfig) {

		this.filterConfig = filterConfig;
		try {
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(log);
			this.cspHeaderValue = pddMonitorProperties.getCspHeaderValue();
			this.xContentTypeOptionsHeaderValue = pddMonitorProperties.getXContentTypeOptionsHeaderValue();
			this.xFrameOptionsHeaderValue = pddMonitorProperties.getXFrameOptionsHeaderValue();
			this.xXssProtectionHeaderValue = pddMonitorProperties.getXXssProtectionHeaderValue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			
			// Gestione vulnerabilita' Content Security Policy
			this.gestioneContentSecurityPolicy(request, response); 

			// Aggiungo header
			this.gestioneXContentTypeOptions(request, response);
            
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
		
		if(nonceValue == null) {
			nonceValue = UUID.randomUUID().toString().replace("-", "");
			salvaNonceValueInSessione(session, nonceValue);
		}
		
		request.setAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE, nonceValue);
		
		if(StringUtils.isNoneBlank(this.cspHeaderValue)) {
			response.setHeader(HttpConstants.HEADER_NAME_CONTENT_SECURITY_POLICY, MessageFormat.format(this.cspHeaderValue, nonceValue, nonceValue));
//			response.setHeader(HttpConstants.HEADER_NAME_CONTENT_SECURITY_POLICY_REPORT_ONLY, MessageFormat.format(this.cspHeaderValue, nonceValue, nonceValue));
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
	
	private void gestioneXContentTypeOptions(HttpServletRequest request, HttpServletResponse response) {
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
