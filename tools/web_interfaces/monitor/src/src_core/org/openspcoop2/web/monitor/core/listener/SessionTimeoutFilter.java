package org.openspcoop2.web.monitor.core.listener;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

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
				if (isSessionInvalid(httpServletRequest)) {					
					String redirPageUrl = httpServletRequest.getContextPath() + "/";
					//se la pagina richiesta e' quella di login allora redirigo direttamente a quella, altrimenti a quella di timeout
					//redirPageUrl += StringUtils.contains(httpServletRequest.getRequestURI(), getLoginPage()) ? getLoginPage() : getTimeoutPage();
					redirPageUrl += getRedirPage(httpServletRequest);
					SessionTimeoutFilter.log.info("session is invalid! redirecting to page : " + redirPageUrl);
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

	private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
		boolean sessionInValid = (httpServletRequest.getRequestedSessionId() != null)
		&& !httpServletRequest.isRequestedSessionIdValid();
		return sessionInValid;
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
