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


package org.openspcoop2.web.ctrlstat.servlet.apc;

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
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * accordiDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ApiHelper apcHelper = new ApiHelper(request, pd, session);
			Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, request, false).getValue();
			
			String objToRemove = apcHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;
			
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			String userLogin = (String) ServletUtils.getUserLoginFromSession(session);
			
			StringBuilder inUsoMessage = new StringBuilder();
			
			boolean deleteAlmostOneApi = false;
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				AccordoServizioParteComune as = apcCore.getAccordoServizioFull(Long.parseLong(idsToRemove.get(i)));
				
				boolean deleted = AccordiServizioParteComuneUtilities.deleteAccordoServizioParteComune(as, userLogin, apcCore, apcHelper, inUsoMessage, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
				if(deleted) {
					deleteAlmostOneApi = true;
				}
				

			}// chiudo for
			
			if(deleteAlmostOneApi) {
				ServletUtils.removeRisultatiRicercaFromSession(request, session, Liste.ACCORDI);
			}

			if (inUsoMessage.length()>0) {
				pd.setMessage(inUsoMessage.toString());
			}

			// con.commit();
			// con.setAutoCommit(true);

			// Preparo il menu
			apcHelper.makeMenu();

			// preparo la lista
			List<AccordoServizioParteComuneSintetico> lista = AccordiServizioParteComuneUtilities.accordiList(apcCore, userLogin, ricerca, tipoAccordo);
			
			if(isModalitaVistaApiCustom) {
				apcHelper.prepareApiList(lista, ricerca, tipoAccordo); 
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				return ServletUtils.getStrutsForward(mapping, ApiCostanti.OBJECT_NAME_APC_API, ForwardParams.DEL());
			}
			
			apcHelper.prepareAccordiList(lista, ricerca, tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.DEL());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.DEL());
		} 

	}
}
