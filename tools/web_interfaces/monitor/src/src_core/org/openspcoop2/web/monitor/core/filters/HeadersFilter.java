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

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

	@Override
	public void init(FilterConfig filterConfig) {

		this.filterConfig = filterConfig;
		try {
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(log);
			this.cspHeaderValue = pddMonitorProperties.getCspHeaderValue();
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
		
		String uuId = UUID.randomUUID().toString().replace("-", "");
		request.setAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE, uuId);
		
		if(StringUtils.isNoneBlank(this.cspHeaderValue)) {
			response.setHeader(HttpConstants.HEADER_NAME_CONTENT_SECURITY_POLICY, MessageFormat.format(this.cspHeaderValue, uuId, uuId));
//			response.setHeader(HttpConstants.HEADER_NAME_CONTENT_SECURITY_POLICY_REPORT_ONLY, MessageFormat.format(this.cspHeaderValue, uuId, uuId));
		}
	}
}
