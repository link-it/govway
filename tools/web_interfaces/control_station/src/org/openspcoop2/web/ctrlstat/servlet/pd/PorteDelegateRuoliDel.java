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


package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteDelegateRuoliDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateRuoliDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			
			String tokenList = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TOKEN_AUTHORIZATION);
			boolean isToken = tokenList!=null && !"".equals(tokenList) && Boolean.valueOf(tokenList);
			
			String objToRemove = porteDelegateHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			// Elimino il servizioApplicativo della porta delegata dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }

			// Prendo la porta delegata
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);

			for (int i = 0; i < idsToRemove.size(); i++) {

				String ruolo = idsToRemove.get(i);
				
				if(isToken) {
					if(pde.getAutorizzazioneToken()!=null && pde.getAutorizzazioneToken().getRuoli()!=null) {
						for (int j = 0; j < pde.getAutorizzazioneToken().getRuoli().sizeRuoloList(); j++) {
							if(pde.getAutorizzazioneToken().getRuoli().getRuolo(j).getNome().equals(ruolo)){
								pde.getAutorizzazioneToken().getRuoli().removeRuolo(j);
								break;
							}
						}
					}
				}
				else {
					for (int j = 0; j < pde.getRuoli().sizeRuoloList(); j++) {
						if(pde.getRuoli().getRuolo(j).getNome().equals(ruolo)){
							pde.getRuoli().removeRuolo(j);
							break;
						}
					}
				}
			}

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);

			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.PORTE_DELEGATE_RUOLI;
			if(isToken) {
				idLista = Liste.PORTE_DELEGATE_TOKEN_RUOLI;
			}

			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
			
			List<String> lista = isToken ?
					porteDelegateCore.portaDelegataRuoliTokenList(Integer.parseInt(id), ricerca)
					:
					porteDelegateCore.portaDelegataRuoliList(Integer.parseInt(id), ricerca);

			porteDelegateHelper.preparePorteDelegateRuoliList(pde.getNome(), ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			// Forward control to the specified success URI
		 	return ServletUtils.getStrutsForward(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_RUOLI,
		 			ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_RUOLI, 
					ForwardParams.DEL());
		} 
	}
}
