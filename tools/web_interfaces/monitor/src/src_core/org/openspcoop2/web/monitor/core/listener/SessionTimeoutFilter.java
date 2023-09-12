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
package org.openspcoop2.web.monitor.core.listener;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.web.monitor.core.utils.SessionUtils;
import org.slf4j.Logger;

/**
 * SessionTimeoutFilter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SessionTimeoutFilter implements Filter {

	private static Logger log = LoggerWrapperFactory.getLogger(SessionTimeoutFilter.class);

	private String loginPage = "pages/login.jsf";
	private String timeoutPage = "pages/timeoutPage.jsf";

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {

		if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;

			// is session expire control required for this request?
			if (isSessionControlRequiredForThisResource(httpServletRequest)) {

				// is session invalid?
				if (SessionUtils.isSessionInvalid(httpServletRequest)) {					
					String redirPageUrl = httpServletRequest.getContextPath() + "/";
					//se la pagina richiesta e' quella di login allora redirigo direttamente a quella, altrimenti a quella di timeout
					//redirPageUrl += StringUtils.contains(httpServletRequest.getRequestURI(), getLoginPage()) ? getLoginPage() : getTimeoutPage();
					redirPageUrl += getRedirPage(httpServletRequest);
					SessionTimeoutFilter.log.info("session is invalid! redirecting to page : {}", redirPageUrl);
					httpServletResponse.sendRedirect(redirPageUrl);
					return;
				}
			}
		}
		filterChain.doFilter(request, response);

	}

	/**
	 * 
	 * session shouldn't be checked for some pages. For example: for timeout page..
	 * Since we're redirecting to timeout page from this filter,
	 * if we don't disable session control for it, filter will again redirect to it
	 * and this will be result with an infinite loop... 
	 */
	private boolean isSessionControlRequiredForThisResource(HttpServletRequest httpServletRequest) {
		String requestPath = httpServletRequest.getRequestURI();

		boolean controlRequired = false;
		if(StringUtils.contains(requestPath, this.timeoutPage) || StringUtils.contains(requestPath, this.loginPage)){
			controlRequired = false;
		}else{
			controlRequired = true;
		}

		return controlRequired;
	}

	private String getRedirPage(HttpServletRequest req){
		String ctx = req.getContextPath();
		String reqUri = req.getRequestURI();
		
		String reqPage = StringUtils.remove(reqUri, ctx);
		
		String res = "";
		if("".equals(reqPage) || "/".equals(reqPage) || StringUtils.contains(reqPage, this.loginPage))
			res = this.loginPage;
		else
			res = this.timeoutPage;
		
		return res;
	}

}
