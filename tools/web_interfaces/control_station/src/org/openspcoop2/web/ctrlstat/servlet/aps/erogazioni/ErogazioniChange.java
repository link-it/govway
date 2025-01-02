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

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ErogazioniChange
 * 
 * @author Andrea Poli (poli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ErogazioniChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE;
		
		try {
			ErogazioniHelper apsHelper = new ErogazioniHelper(request, pd, session);
			
			// Preparo il menu
			apsHelper.makeMenu();
			
			String id = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			long idInt  = Long.parseLong(id);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idInt);
			
			String tipoSoggettoFruitore = apsHelper.getParametroTipoSoggetto(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE);
			String nomeSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE);
			IDSoggetto idSoggettoFruitore = null;
			if(tipoSoggettoFruitore!=null && !"".equals(tipoSoggettoFruitore) &&
					nomeSoggettoFruitore!=null && !"".equals(nomeSoggettoFruitore)) {
				idSoggettoFruitore = new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore);
			}
			
			// Rimuovo eventuali salvataggi fatti accedendo in precedenza
			ServletUtils.removeRisultatiRicercaFromSession(request, session, Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI);
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			ricerca.setSearchString(Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, "");
			ricerca.clearFilter(Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, Filtri.FILTRO_CONNETTORE_TIPO);
			ricerca.clearFilter(Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, Filtri.FILTRO_CONNETTORE_TIPO_PLUGIN);
			ricerca.clearFilter(Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, Filtri.FILTRO_CONNETTORE_TOKEN_POLICY);
			ricerca.clearFilter(Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, Filtri.FILTRO_CONNETTORE_ENDPOINT);
			ricerca.clearFilter(Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, Filtri.FILTRO_CONNETTORE_KEYSTORE);
			ricerca.clearFilter(Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, Filtri.FILTRO_CONNETTORE_DEBUG);
			SearchUtils.clearFilter(ricerca, Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_FILTRO_HIDDEN_TAB_SELEZIONATO);
			
			
			String resetElementoCacheS = apsHelper.getParametroBoolean(CostantiControlStation.PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE);
			boolean resetElementoCache = ServletUtils.isCheckBoxEnabled(resetElementoCacheS);
			
			boolean resetElementoCacheDettaglio = false;
			String postBackElementName = apsHelper.getPostBackElementName();
			if(postBackElementName != null && postBackElementName.equals(CostantiControlStation.PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE)) {
				resetElementoCacheDettaglio = true;
			}
			
			if(resetElementoCache || resetElementoCacheDettaglio) {
				// reset elemento dalla cache
				return apsHelper.prepareErogazioneChangeResetCache(mapping, gd, ricerca, tipoOp, asps, idSoggettoFruitore);
			}
			
			apsHelper.prepareErogazioneChange(tipoOp, asps, idSoggettoFruitore);
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.CHANGE());
		}  

	}
}