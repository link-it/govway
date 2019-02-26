/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

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
			ServletUtils.setObjectIntoSession(session, new Boolean(true), ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI); 
			
			// ctrlstatHelper ch = new ctrlstatHelper (request, pd, con, session);
			ErogazioniHelper erogazioniHelper = new ErogazioniHelper(request, pd, session);
			// Preparo il menu
			erogazioniHelper.makeMenu();
			
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();

			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.SERVIZI;
						
			ricerca = erogazioniHelper.checkSearchParameters(idLista, ricerca);
			
			String tipologiaParameterName = AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE;
			String tipologia = erogazioniHelper.getParameter(tipologiaParameterName);
			if(tipologia==null) {
				// guardo se sto entrando da altri link fuori dal menu di sinistra
				// in tal caso e' gia' impostato
				tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
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
			
			erogazioniHelper.checkGestione(session, ricerca, idLista, tipologiaParameterName,true);
			
			String superUser   = ServletUtils.getUserLoginFromSession(session);
			PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();
			
			boolean [] permessi = new boolean[2];
			permessi[0] = pu.isServizi();
			permessi[1] = pu.isAccordiCooperazione();
			
			List<AccordoServizioParteSpecifica> lista = null;
			if(apsCore.isVisioneOggettiGlobale(superUser)){
				lista = apsCore.soggettiServizioList(null, ricerca,permessi, gestioneFruitori, gestioneErogatori);
			}else{
				lista = apsCore.soggettiServizioList(superUser, ricerca,permessi, gestioneFruitori, gestioneErogatori);
			}

			erogazioniHelper.prepareErogazioniList(ricerca, lista);

			String msg = erogazioniHelper.getParameter(Costanti.PARAMETER_NAME_MSG_ERROR_EXPORT);
			if(msg!=null && !"".equals(msg)){
				pd.setMessage("Errore durante esportazione: "+msg);
			}
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(session, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, 
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI,
					ForwardParams.LIST());
		}  
	}
}