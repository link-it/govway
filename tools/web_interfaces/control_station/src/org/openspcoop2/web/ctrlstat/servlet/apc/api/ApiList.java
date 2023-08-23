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
package org.openspcoop2.web.ctrlstat.servlet.apc.api;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ApiList
 *
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiList  extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

	
		try {
			ApiHelper apiHelper = new ApiHelper(request, pd, session);

			String tipoAccordo = apiHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;
			
			apiHelper.makeMenu();

			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			
			// Controllo i criteri di ricerca e recupero eventuali parametri
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.ACCORDI;
			
			// poiche' esistono filtri che hanno necessita di postback salvo in sessione
			List<AccordoServizioParteComuneSintetico> lista = null;
			if(apcCore.isRegistroServiziLocale()){
				if(!ServletUtils.isSearchDone(apiHelper)) {
					lista = ServletUtils.getRisultatiRicercaFromSession(request, session, idLista,  AccordoServizioParteComuneSintetico.class);
				}
			}

			ricerca = apiHelper.checkSearchParameters(idLista, ricerca);
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
//			long before = org.openspcoop2.utils.date.DateManager.getTimeMillis();
			if(lista==null) {
				lista = AccordiServizioParteComuneUtilities.accordiList(apcCore, userLogin, ricerca, tipoAccordo);
			}
//			long after = org.openspcoop2.utils.date.DateManager.getTimeMillis();
//			System.out.println("READ: "+org.openspcoop2.utils.Utilities.convertSystemTimeIntoString_millisecondi((after-before), true));

			if(!apiHelper.isPostBackFilterElement()) {
				ServletUtils.setRisultatiRicercaIntoSession(request, session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
			}
			
//			before = org.openspcoop2.utils.date.DateManager.getTimeMillis();
			apiHelper.prepareApiList(lista, ricerca, tipoAccordo);
//			after = org.openspcoop2.utils.date.DateManager.getTimeMillis();
//			System.out.println("PRESENTATION: "+org.openspcoop2.utils.Utilities.convertSystemTimeIntoString_millisecondi((after-before), true));

			String msg = apiHelper.getParameter(Costanti.PARAMETER_NAME_MSG_ERROR_EXPORT);
			if(msg!=null && !"".equals(msg)){
				pd.setMessage("Errore durante esportazione: "+msg);
			}
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, ApiCostanti.OBJECT_NAME_APC_API, ForwardParams.LIST());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ApiCostanti.OBJECT_NAME_APC_API, ForwardParams.LIST());
		}
	}

}
