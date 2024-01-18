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


package org.openspcoop2.web.ctrlstat.servlet.aps;

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
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * serviziPorteAppDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaPorteApplicativeDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		PageData pdold = ServletUtils.getPageDataFromSession(request, session);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			/*
			 * Validate the request parameters specified by the user Note: Basic
			 * field validation done in porteDomForm.java Business logic
			 * validation done in porteDomAdd.java
			 */
			String id = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			long idServizioLong = Long.parseLong(id);
			String objToRemove = apsHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			// gli id contenuti nell'array sono i nomi delle porte applicative
			// da rimuovere
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			// prendo l'id del soggetto erogatore lo propago
			// lo metto nel pd come campo hidden
			pd.setHidden(pdold.getHidden());

			// Preparo il menu
			apsHelper.makeMenu();

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();

			// Prendo l'id del soggetto erogatore del servizio
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServizioLong);
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 

			String superUser   = ServletUtils.getUserLoginFromSession(session);

			StringBuilder inUsoMessage = new StringBuilder();
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				// ricevo come parametro l'id della pa associata al mapping da cancellare
				IDPortaApplicativa idPortaApplicativa = new IDPortaApplicativa();
				idPortaApplicativa.setNome(idsToRemove.get(i)); 
				
				AccordiServizioParteSpecificaUtilities.deleteAccordoServizioParteSpecificaPorteApplicative(idPortaApplicativa, idServizio, 
						superUser, apsCore, apsHelper, inUsoMessage);
				
			}// for
			
			if (inUsoMessage.length()>0) {
				pd.setMessage(inUsoMessage.toString());
			}

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.CONFIGURAZIONE_EROGAZIONE;

			ricerca = apsHelper.checkSearchParameters(idLista, ricerca);

			
			List<MappingErogazionePortaApplicativa> lista = apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), ricerca);
			apsHelper.prepareServiziConfigurazioneList(lista, id, null, ricerca);
			
			// reset posizione tab
			if(!apsHelper.isModalitaCompleta())
				ServletUtils.setObjectIntoSession(request, session, "0", CostantiControlStation.PARAMETRO_ID_TAB);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			ForwardParams fwP = apsHelper.isModalitaCompleta() ? ForwardParams.DEL() : AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE;
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE, fwP);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,
					ForwardParams.DEL());
		}  
	}
}
