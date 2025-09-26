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

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.oauth2.BaseOAuth2CallbackServlet;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.slf4j.Logger;

/**
 * OAuth2CallbackServlet
 *
 * @author Pintori Giuliano (giuliano.pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OAuth2CallbackServlet extends BaseOAuth2CallbackServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static Logger log = ControlStationLogger.getPddConsoleCoreLogger();
	
	public OAuth2CallbackServlet() {
		super();
	}
	
	@Override
	protected String getConsoleError(HttpServletRequest request) {
		String loginUtenteNonAutorizzatoRedirectUrl = null;
		
		try {
			loginUtenteNonAutorizzatoRedirectUrl = ConsoleProperties.getInstance().getLoginUtenteNonAutorizzatoRedirectUrl();
		} catch (UtilsException | OpenSPCoop2ConfigurationException e) {
			log.error("Errore durante la lettura delle properties: " + e.getMessage(),e);
		} 
				
		return StringUtils.isNotEmpty(loginUtenteNonAutorizzatoRedirectUrl) ? loginUtenteNonAutorizzatoRedirectUrl : request.getContextPath() + "/" + LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE;
	}
	
	@Override
	protected String getConsoleHome(HttpServletRequest request) {
		return  request.getContextPath() + "/oauth2/user";
//		return new Parameter("", request.getContextPath() + "/" + LoginCostanti.SERVLET_NAME_MESSAGE_PAGE,
//				new Parameter(Costanti.PARAMETER_MESSAGE_TEXT,LoginCostanti.LABEL_LOGIN_EFFETTUATO_CON_SUCCESSO),
//				new Parameter(Costanti.PARAMETER_MESSAGE_TYPE,MessageType.INFO_SINTETICO.toString())
//				).getValue();
	}
	
	@Override
	protected Properties getLoginProperties() {
		try {
			return ConsoleProperties.getInstance().getLoginProperties();
		} catch (UtilsException | OpenSPCoop2ConfigurationException e) {
			log.error("Errore durante la lettura delle properties: " + e.getMessage(),e);
		} 
		
		return new Properties();
	}
	
	@Override
	protected Logger getLog() {
		return log;
	}
}
