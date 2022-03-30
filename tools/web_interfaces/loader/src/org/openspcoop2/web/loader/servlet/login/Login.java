/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.loader.servlet.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.loader.core.Costanti;
import org.openspcoop2.web.loader.core.LoaderCore;
import org.openspcoop2.web.loader.servlet.GeneralHelper;
import org.openspcoop2.web.loader.servlet.LoaderHelper;

/**
 * Login
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class Login extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo PageData
		PageData pd = generalHelper.initPageData();

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
		
			LoaderHelper loaderHelper = new LoaderHelper(request, pd, session);
	
			String userLogin =  request.getParameter(Costanti.PARAMETRO_UTENTE_LOGIN);
			// String password = request.getParameter("password");
	
			// Se login = null, devo visualizzare la pagina per l'inserimento dati
			if (userLogin == null) {
				
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, Costanti.OBJECT_NAME_LOGIN, ForwardParams.LOGIN());
			}
	
			// Controlli sui campi immessi
			boolean isOk = loaderHelper.loginCheckData(true);
			if (!isOk) {
	
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, Costanti.OBJECT_NAME_LOGIN, ForwardParams.LOGIN());
				
			}
	
			ServletUtils.setUserLoginIntoSession(session, userLogin);
			
			// Preparo il menu
			loaderHelper.makeMenu();
	
			pd.setMessage(Costanti.LABEL_LOGIN_EFFETTUATO_CON_SUCCESSO,MessageType.INFO_SINTETICO);
	
			// Inizializzo di nuovo GeneralData, dopo aver messo
			// in sessione la login dell'utente
			gd = generalHelper.initGeneralData(request);
	
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, Costanti.OBJECT_NAME_LOGIN, ForwardParams.LOGIN());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(LoaderCore.log, e, pd, session, gd, mapping, 
					Costanti.OBJECT_NAME_LOGIN, ForwardParams.LOGIN());
		}
	}
}
