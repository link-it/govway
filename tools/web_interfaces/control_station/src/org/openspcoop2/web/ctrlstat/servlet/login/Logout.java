/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.utils.oauth2.OAuth2Costanti;
import org.openspcoop2.utils.oauth2.OAuth2Utilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * logout
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class Logout extends Action {

	

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo PageData
		PageData pd = generalHelper.initPageData();

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request,LoginCostanti.SERVLET_NAME_LOGIN);

		try{
			
			String loggedUser= ServletUtils.getUserLoginFromSession(session);
			LoginCore loginCore = new LoginCore();
			
			if(loggedUser!=null && !"".equals(loggedUser)){
				loginCore.performAuditLogout(loggedUser);
			}
			
			String idToken = (String) session.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ID_TOKEN);
			String oauth2LogoutUrl = loginCore.getLoginProperties().getProperty(OAuth2Costanti.PROP_OAUTH2_LOGOUT_ENDPOINT);
			
			LoginSessionUtilities.cleanLoginParametersSession(request, session);

			pd.setMessage(LoginCostanti.LABEL_LOGOUT_EFFETTUATO_CON_SUCCESSO,Costanti.MESSAGE_TYPE_INFO_SINTETICO);

			// Rimozione del cookie JSESSIONID
	        ServletUtils.removeCookieFromResponse(org.openspcoop2.web.lib.mvc.Costanti.COOKIE_NAME_JSESSIONID, request, response);

	        // Inizializzo di nuovo GeneralData, dopo aver rimosso
 			// dalla sessione la login dell'utente
 			gd = generalHelper.initGeneralData(request,LoginCostanti.SERVLET_NAME_LOGIN);

 			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
 			
 			String logoutUrlDestinazione = loginCore.getLogoutUrlDestinazione();
 			
 			if(idToken != null && oauth2LogoutUrl != null) {
				// preparazione dei parametri
				String redirPageUrl = StringUtils.isNotEmpty(logoutUrlDestinazione) ? 
						logoutUrlDestinazione :
							new Parameter("",
							ServletUtils.buildInternalRedirectUrl(request, LoginCostanti.SERVLET_NAME_LOGIN),
							new Parameter(Costanti.PARAMETER_MESSAGE_TEXT,LoginCostanti.LABEL_LOGOUT_EFFETTUATO_CON_SUCCESSO),
							new Parameter(Costanti.PARAMETER_MESSAGE_TYPE,MessageType.INFO_SINTETICO.toString())
							).getValue();
				
				String logoutUrl = OAuth2Utilities.creaUrlLogout(idToken, oauth2LogoutUrl, redirPageUrl);

				// se mi sono loggato con oauth2 e la configurazione oauth2 prevede un logoutUrl
				response.sendRedirect(logoutUrl);
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, LoginCostanti.OBJECT_NAME_LOGOUT, ForwardParams.LOGOUT()); // ??
			}
 			
			if(StringUtils.isBlank(logoutUrlDestinazione) 
					||
					(loginCore.isLoginApplication() && (idToken == null))
			) {
				// default login interno
 		        if(loginCore.isLoginApplication()) {
 		        	// Forward control to the specified success URI
 					return ServletUtils.getStrutsForwardEditModeFinished(mapping, LoginCostanti.OBJECT_NAME_LOGOUT, ForwardParams.LOGOUT());
 		        } else {
 		        // Forward control to the specified success URI
 		        	pd.setMostraLinkHome(true);
	    			return ServletUtils.getStrutsForwardEditModeFinished(mapping, LoginCostanti.OBJECT_NAME_LOGIN_MESSAGE_PAGE, ForwardParams.LOGOUT());
 		        }
 			} else {
 				// redirect verso la destinazione prevista
 				response.sendRedirect(logoutUrlDestinazione);
 				return ServletUtils.getStrutsForwardEditModeFinished(mapping, LoginCostanti.OBJECT_NAME_LOGOUT, ForwardParams.LOGOUT());
 			}
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					LoginCostanti.OBJECT_NAME_LOGOUT, ForwardParams.LOGOUT());
		}
	}
}
