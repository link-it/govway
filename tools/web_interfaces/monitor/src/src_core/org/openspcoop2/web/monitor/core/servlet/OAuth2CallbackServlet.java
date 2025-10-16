package org.openspcoop2.web.monitor.core.servlet;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.oauth2.BaseOAuth2CallbackServlet;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.filters.PrincipalFilter;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

public class OAuth2CallbackServlet extends BaseOAuth2CallbackServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	public OAuth2CallbackServlet() {
		super();
	}
	
	@Override
	protected String getConsoleError(HttpServletRequest request) {
		String loginUtenteNonAutorizzatoRedirectUrl = null;
		
		try {
			loginUtenteNonAutorizzatoRedirectUrl = PddMonitorProperties.getInstance(log).getLoginUtenteNonAutorizzatoRedirectUrl();
		} catch (UtilsException e) {
			log.error("Errore durante la lettura delle properties: " + e.getMessage(),e);
		} 
				
		return StringUtils.isNotEmpty(loginUtenteNonAutorizzatoRedirectUrl) ? loginUtenteNonAutorizzatoRedirectUrl : 
			request.getContextPath() + PrincipalFilter.REDIRECT_ERRORE_DEFAULT;
	}
	
	@Override
	protected String getConsoleHome(HttpServletRequest request) {
		return  request.getContextPath() + "/oauth2/user";
	}
	
	@Override
	protected Properties getLoginProperties() {
		try {
			return PddMonitorProperties.getInstance(log).getLoginProperties();
		} catch (UtilsException e) {
			log.error("Errore durante la lettura delle properties: " + e.getMessage(),e);
		} 
		
		return new Properties();
	}
	
	@Override
	protected Logger getLog() {
		return log;
	}
}
