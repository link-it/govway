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

package org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni;

import java.util.List;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ErogazioniList
 * 
 * @author Andrea Poli (poli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ErogazioniList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ErogazioniHelper erogazioniHelper = new ErogazioniHelper(request, pd, session);
			
			ServletUtils.setObjectIntoSession(request, session, Boolean.valueOf(true), ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI); 
			// Preparo il menu
			erogazioniHelper.makeMenu();
			
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();

			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.SERVIZI;
			
			// poiche' esistono filtri che hanno necessita di postback salvo in sessione
			List<AccordoServizioParteSpecifica> lista = null;
			if(!ServletUtils.isSearchDone(erogazioniHelper)) {
				lista = ServletUtils.getRisultatiRicercaFromSession(request, session, idLista,  AccordoServizioParteSpecifica.class);
			}
			
			ricerca = erogazioniHelper.checkSearchParameters(idLista, ricerca);
			
			erogazioniHelper.clearFiltroSoggettoByPostBackProtocollo(0, ricerca, idLista);
			
			String tipologiaParameterName = AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE;
			String tipologia = erogazioniHelper.getParametroTipologiaErogazione(tipologiaParameterName);
			if(tipologia==null) {
				// guardo se sto entrando da altri link fuori dal menu di sinistra
				// in tal caso e' gia' impostato
				tipologia = ServletUtils.getObjectFromSession(request, session, String.class, tipologiaParameterName);
			}
			boolean gestioneFruitori = false;
			boolean gestioneErogatori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
				else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					gestioneErogatori = true;
				}
			}
			
			erogazioniHelper.checkGestione(request, session, ricerca, idLista, tipologiaParameterName,true);
			
			String superUser   = ServletUtils.getUserLoginFromSession(session);
			PermessiUtente pu = ServletUtils.getUserFromSession(request, session).getPermessi();
			
			boolean [] permessi = new boolean[2];
			permessi[0] = pu.isServizi();
			permessi[1] = pu.isAccordiCooperazione();
			
/**			long before = org.openspcoop2.utils.date.DateManager.getTimeMillis();*/
			if(lista==null) {
				if(apsCore.isVisioneOggettiGlobale(superUser)){
					lista = apsCore.soggettiServizioList(null, ricerca,permessi, gestioneFruitori, gestioneErogatori);
				}else{
					lista = apsCore.soggettiServizioList(superUser, ricerca,permessi, gestioneFruitori, gestioneErogatori);
				}
			}
/**			long after = org.openspcoop2.utils.date.DateManager.getTimeMillis();
//			System.out.println("READ: "+org.openspcoop2.utils.Utilities.convertSystemTimeIntoString_millisecondi((after-before), true));*/

			if(!erogazioniHelper.isPostBackFilterElement()) {
				ServletUtils.setRisultatiRicercaIntoSession(request, session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
			}
			
/**			before = org.openspcoop2.utils.date.DateManager.getTimeMillis();*/
			erogazioniHelper.prepareErogazioniList(ricerca, lista);
/**			after = org.openspcoop2.utils.date.DateManager.getTimeMillis();
//			System.out.println("PRESENTATION: "+org.openspcoop2.utils.Utilities.convertSystemTimeIntoString_millisecondi((after-before), true));*/

			String msg = erogazioniHelper.getParameter(Costanti.PARAMETER_NAME_MSG_ERROR_EXPORT);
			if(msg!=null && !"".equals(msg)){
				pd.setMessage("Errore durante esportazione: "+msg);
			}
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, 
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI,
					ForwardParams.LIST());
		}  
	}
}