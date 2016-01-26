/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
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
			
			LoginSessionUtilities.cleanLoginParametersSession(session);
	
			pd.setMessage(LoginCostanti.LABEL_LOGOUT_EFFETTUATO_CON_SUCCESSO);
			
			// Controllo CooKies
//			Cookie[] cookies = request.getCookies();
//			ControlStationCore.logDebug("Analisi Cookies Request: ");
//	        for(int i = 0; i< cookies.length ; ++i){
//	        	String name = cookies[i].getName();
//	        	String value = cookies[i].getValue();
//	        	String path = cookies[i].getPath();
//	        	String durata = ""+cookies[i].getMaxAge();
//	        	ControlStationCore.logDebug("Cookie Name: ["+name+"] Value: ["+value+"] Path: ["+path+"] MaxAge: ["+durata+"]");
//	        }
	        
			// Rimozione del cookie JSESSIONID
	        ServletUtils.removeCookieFromResponse(org.openspcoop2.web.lib.mvc.Costanti.COOKIE_NAME_JSESSIONID, request, response);
		
			// Inizializzo di nuovo GeneralData, dopo aver rimosso
			// dalla sessione la login dell'utente
			gd = generalHelper.initGeneralData(request,LoginCostanti.SERVLET_NAME_LOGIN);
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd, true);
	
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, LoginCostanti.OBJECT_NAME_LOGOUT, ForwardParams.LOGOUT());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					LoginCostanti.OBJECT_NAME_LOGOUT, ForwardParams.LOGOUT());
		}
	}
}
