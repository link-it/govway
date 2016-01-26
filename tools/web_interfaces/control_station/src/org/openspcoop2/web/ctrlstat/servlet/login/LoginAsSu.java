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
 * loginAsSu
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class LoginAsSu extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo PageData
		PageData pd = generalHelper.initPageData();

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request,LoginCostanti.SERVLET_NAME_LOGIN);

		try {
			// Annullo quanto letto sull'auditing
			ControlStationCore.clearAuditManager();
			
			LoginHelper loginHelper = new LoginHelper(request, pd, session);
	
			String login = request.getParameter(LoginCostanti.PARAMETRO_LOGIN_LOGIN);
	
			// Se login = null, devo visualizzare la pagina per l'inserimento dati
			if (login == null) {
	
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd, true);
	
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, LoginCostanti.OBJECT_NAME_LOGIN_AS_SU, ForwardParams.LOGIN());
			}
	
			// Controlli sui campi immessi
			boolean isOk = loginHelper.loginCheckData(LoginTipologia.WITHOUT_PASSWORD);
			if (!isOk) {
				
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd, true);
	
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, LoginCostanti.OBJECT_NAME_LOGIN_AS_SU, ForwardParams.LOGIN());
			}
					
			String loginOLD = ServletUtils.getUserLoginFromSession(session);
			
			LoginCore loginCore = new LoginCore();
			
			LoginSessionUtilities.setLoginParametersSession(session, loginCore, login);
			
			loginCore.performAuditLogout(loginOLD);
			loginCore.performAuditLogin(login);
			
			// refresh page-data
			gd = generalHelper.initGeneralData(request,LoginCostanti.SERVLET_NAME_LOGIN);
			
			// Preparo il menu
			loginHelper.makeMenu();
	
			pd.setMessage(LoginCostanti.LABEL_LOGIN_EFFETTUATO_CON_SUCCESSO);
	
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd, true);
	
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, LoginCostanti.OBJECT_NAME_LOGIN_AS_SU, ForwardParams.LOGIN());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					LoginCostanti.OBJECT_NAME_LOGIN_AS_SU, ForwardParams.LOGIN());
		} 
	}
}
