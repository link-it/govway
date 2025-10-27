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
package org.openspcoop2.web.ctrlstat.servlet.login;

import java.text.MessageFormat;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

/**     
 * HeadersFilter
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HeadersFilter implements Filter {

	private FilterConfig filterConfig = null;
	private ControlStationCore core = null;
	private static Logger log = ControlStationLogger.getPddConsoleCoreLogger();

	@Override
	public void init(FilterConfig filterConfig) {

		this.filterConfig = filterConfig;
		try {
			this.core = new ControlStationCore();
		} catch (Exception e) {
			log.error("Errore durante il caricamento iniziale: " + e.getMessage(), e);
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		try {
			// Gestione vulnerabilita' Content Security Policy
			this.gestioneContentSecurityPolicy(request, response);

			// Gestione Cache-Control per risorse statiche
			gestioneCacheControl(request, response, log);

			chain.doFilter(request, response);
		} catch (Exception e) {
			ControlStationCore.logError("Errore rilevato durante l'headersFilter",e);
			try{
				HttpSession session = request.getSession();

				GeneralHelper generalHelper = null;
				try{
					generalHelper = new GeneralHelper(session);
				}catch(Exception eClose){
					ControlStationCore.logError("Errore rilevato durante l'headersFilter (reInit General Helper)",e);
				}
				AuthorizationFilter.setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_ERRORE, this.filterConfig.getServletContext(), HttpStatus.INTERNAL_SERVER_ERROR);
			}catch(Exception eClose){
				ControlStationCore.logError("Errore rilevato durante l'headersFilter (segnalazione errore)",e);
			}
		}
	}

	@Override
	public void destroy() {
		this.filterConfig = null;
	}

	private void gestioneContentSecurityPolicy(HttpServletRequest request, HttpServletResponse response) {
		// Per abilitare l'esecuzione solo degli script che vogliamo far eseguire, si genera un UUID random e si assegna ai tag script con attributo src e a gli script inline nelle pagine
		// L'id degli script abilitati e' indicato all'interno del campo script-src

		String uuId = UUID.randomUUID().toString().replace("-", "");
		request.setAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE, uuId);

		if(StringUtils.isNoneBlank(this.core.getCspHeaderValue())) {
			response.setHeader(HttpConstants.HEADER_NAME_CONTENT_SECURITY_POLICY, MessageFormat.format(this.core.getCspHeaderValue(), uuId, uuId));
//			response.setHeader(HttpConstants.HEADER_NAME_CONTENT_SECURITY_POLICY_REPORT_ONLY, MessageFormat.format(this.core.getCspHeaderValue(), uuId, uuId))
		}
	}

	private static void gestioneCacheControl(HttpServletRequest request, HttpServletResponse response, Logger log) {
		// Gestione Cache-Control per risorse statiche (CSS, JS, fonts, images)
		String requestUri = request.getRequestURI();

		if (requestUri != null) {
			// Array delle directory delle risorse statiche da controllare
			String[] staticResourceDirs = {
				CostantiControlStation.IMAGES_DIR,
				CostantiControlStation.CSS_DIR,
				CostantiControlStation.FONTS_DIR,
				CostantiControlStation.JS_DIR
			};

			// Verifica se la risorsa e' statica (css, js, fonts, images)
			if (ServletUtils.isStaticResource(requestUri, staticResourceDirs)) {
				// Per risorse statiche: disabilita la cache
				// no-cache: richiede rivalidazione prima di usare la copia cachata
				// no-store: impedisce completamente il caching
				// must-revalidate: forza la rivalidazione delle risorse scadute
				log.debug("Impostazione header Cache-Control per risorsa statica: {}", requestUri);
				response.setHeader(HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_HTTP_1_1_DISABLE_CACHE);
				response.setHeader(HttpConstants.CACHE_STATUS_HTTP_1_0, HttpConstants.CACHE_STATUS_HTTP_1_0_DISABLE_CACHE);
				response.setDateHeader(HttpConstants.CACHE_STATUS_PROXY_EXPIRES, HttpConstants.CACHE_STATUS_PROXY_EXPIRES_DISABLE_CACHE);
			}
		}
	}
}
