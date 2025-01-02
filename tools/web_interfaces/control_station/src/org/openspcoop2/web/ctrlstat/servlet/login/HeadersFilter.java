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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.security.InputSanitizerProperties;
import org.openspcoop2.web.lib.mvc.security.SecurityProperties;
import org.openspcoop2.web.lib.mvc.security.Validatore;
import org.openspcoop2.web.lib.mvc.security.SecurityWrappedHttpServletRequest;
import org.openspcoop2.web.lib.mvc.security.SecurityWrappedHttpServletResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import org.jsoup.safety.Safelist;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities;


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
			Properties consoleSecurityConfiguration = this.core.getConsoleSecurityConfiguration();
			SecurityProperties.init(consoleSecurityConfiguration, log);
			Properties consoleInputSanitizerConfiguration = this.core.getConsoleInputSanitizerConfiguration();
			InputSanitizerProperties.init(consoleInputSanitizerConfiguration, log);
			Validatore.init(SecurityProperties.getInstance(), InputSanitizerProperties.getInstance(), log);
			
		} catch (Exception e) {
			log.error("Errore durante il caricamento iniziale: " + e.getMessage(), e);
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		GeneralHelper generalHelper = null;
		try {	
			// dump della richiesta prima di passarla al tool di validazione
			dumpRichiesta(request, response);
			
			SecurityWrappedHttpServletRequest seqReq = new SecurityWrappedHttpServletRequest(request, log);
			
			SecurityWrappedHttpServletResponse seqRes = new SecurityWrappedHttpServletResponse(response, log);
			
			// Gestione vulnerabilita' Content Security Policy
			this.gestioneContentSecurityPolicy(seqReq, seqRes); 

			// Aggiungo header
			this.gestioneXContentTypeOptions(seqReq, seqRes);
			
			chain.doFilter(seqReq, seqRes);
		} catch (Exception e) {
			ControlStationCore.logError("Errore rilevato durante l'headersFilter",e);
			try{
				HttpSession session = request.getSession();

				if(generalHelper==null) {
					try{
						generalHelper = new GeneralHelper(session);
					}catch(Exception eClose){
						ControlStationCore.logError("Errore rilevato durante l'headersFilter (reInit General Helper)",e);
					}
				}
				AuthorizationFilter.setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_ERRORE, this.filterConfig, HttpStatus.SERVICE_UNAVAILABLE);
				// return so that we do not chain to other filters
				return;
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

		String uuId = UUID.randomUUID().toString();
		request.setAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE, uuId);

		if(StringUtils.isNoneBlank(this.core.getCspHeaderValue())) {
			response.setHeader(HttpConstants.HEADER_NAME_CONTENT_SECURITY_POLICY, MessageFormat.format(this.core.getCspHeaderValue(), uuId, uuId));
			//			response.setHeader(HttpConstants.HEADER_NAME_CONTENT_SECURITY_POLICY_REPORT_ONLY, MessageFormat.format(this.core.getCspHeaderValue(), uuId, uuId));
		}
	}

	private void gestioneXContentTypeOptions(HttpServletRequest request, HttpServletResponse response) {
		if(StringUtils.isNoneBlank(this.core.getXFrameOptionsHeaderValue())) {
			response.setHeader(HttpConstants.HEADER_NAME_X_FRAME_OPTIONS, this.core.getXFrameOptionsHeaderValue());
		}
		if(StringUtils.isNoneBlank(this.core.getXXssProtectionHeaderValue())) {
			response.setHeader(HttpConstants.HEADER_NAME_X_XSS_PROTECTION, this.core.getXXssProtectionHeaderValue());
		}
		if(StringUtils.isNoneBlank(this.core.getXContentTypeOptionsHeaderValue())) {
			response.setHeader(HttpConstants.HEADER_NAME_X_CONTENT_TYPE_OPTIONS, this.core.getXContentTypeOptionsHeaderValue());
		}
	}
	
	private void dumpRichiesta(HttpServletRequest request,	HttpServletResponse response) {
		
        // Crea la safelist personalizzata
        Safelist customSafelist = InputSanitizerProperties.getInstance().getSafelist();
		
		// Itera su tutti i parametri della richiesta
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            List<String> parametriCorretti = new ArrayList<>();

            // Sanifica ogni valore del parametro
            for (int i = 0; i < paramValues.length; i++) {
                parametriCorretti.add(Entities.unescape(Jsoup.parse(Jsoup.clean(paramValues[i], customSafelist)).body().html()));
            }
           
            log.debug("AAAAAA [{}] Valori Originali [{}]", paramName, StringUtils.join(paramValues, "|")); 
            log.debug("AAAAAA [{}] Valori Corretti [{}]", paramName, StringUtils.join(parametriCorretti, "|"));
        }

	}
}
