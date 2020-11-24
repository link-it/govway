/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.ruoli;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * RuoliDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class RuoliDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		// PageData pdold = (PageData) session.getAttribute("PageData");

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {
			RuoliHelper ruoliHelper = new RuoliHelper(request, pd, session);

			String objToRemove =ruoliHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			RuoliCore ruoliCore = new RuoliCore();

			StringBuilder inUsoMessage = new StringBuilder();
			
			boolean deleteAlmostOneRole = false;
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				Ruolo ruolo = ruoliCore.getRuolo(idsToRemove.get(i));
				
				boolean deleted = RuoliUtilities.deleteRuolo(ruolo, userLogin, ruoliCore, ruoliHelper, inUsoMessage, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
				if(deleted) {
					deleteAlmostOneRole = true;
				}
				
			}// chiudo for

			if(deleteAlmostOneRole) {
				ServletUtils.removeRisultatiRicercaFromSession(session, Liste.RUOLI);
			}
			
			if (inUsoMessage.length()>0) {
				pd.setMessage(inUsoMessage.toString());
			}
			

			// Preparo il menu
			ruoliHelper.makeMenu();

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<Ruolo> lista = null;
			if(ruoliCore.isVisioneOggettiGlobale(userLogin)){
				lista = ruoliCore.ruoliList(null, ricerca);
			}else{
				lista = ruoliCore.ruoliList(userLogin, ricerca);
			}
			
			ruoliHelper.prepareRuoliList(ricerca, lista);
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					RuoliCostanti.OBJECT_NAME_RUOLI,
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					RuoliCostanti.OBJECT_NAME_RUOLI, ForwardParams.DEL());
		}
	}
}
