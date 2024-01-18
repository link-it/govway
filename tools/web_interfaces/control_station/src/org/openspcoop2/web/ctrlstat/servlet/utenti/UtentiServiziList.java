/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.utenti;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.dao.User;

/**     
 * UtentiServiziList
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UtentiServiziList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			UtentiHelper utentiHelper = new UtentiHelper(request, pd, session);
			String nomesu = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
			
			// Preparo il menu
			utentiHelper.makeMenu();
	
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session,ConsoleSearch.class);
	
			int idLista = Liste.UTENTI_SERVIZI;
	
			ricerca = utentiHelper.checkSearchParameters(idLista, ricerca);
	
			UtentiCore utentiCore = new UtentiCore();
			User user = utentiCore.getUser(nomesu);
			List<IDServizio> lista = utentiCore.utentiServiziList(nomesu, ricerca);
	
			utentiHelper.prepareUtentiServiziList(ricerca, lista, user);
	
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, UtentiCostanti.OBJECT_NAME_UTENTI_SERVIZI, ForwardParams.LIST());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					UtentiCostanti.OBJECT_NAME_UTENTI_SERVIZI, ForwardParams.LIST());
		} 
	}

}
