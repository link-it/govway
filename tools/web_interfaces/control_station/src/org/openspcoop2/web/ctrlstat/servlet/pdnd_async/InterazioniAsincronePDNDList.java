/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.pdnd_async;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.pdd.core.pdnd.async.InterazioneAsincronaPDND;
import org.openspcoop2.pdd.core.pdnd.async.InterazioniAsincronePDNDFiltro;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * InterazioniAsincronePDNDList
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InterazioniAsincronePDNDList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			InterazioniAsincronePDNDHelper helper = new InterazioniAsincronePDNDHelper(request, pd, session);

			InterazioniAsincronePDNDCore core = new InterazioniAsincronePDNDCore();

			// Preparo il menu
			helper.makeMenu();
			
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			List<String> sorgenti = core.getSorgentiDati();
			if(sorgenti == null || sorgenti.isEmpty()) {
				pd.setMessage(InterazioniAsincronePDNDCostanti.LABEL_NESSUNA_SORGENTE_DATI, Costanti.MESSAGE_TYPE_INFO);
				pd.disableEditMode();
				ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				return ServletUtils.getStrutsForward (mapping, 
						InterazioniAsincronePDNDCostanti.OBJECT_NAME_PDND_INTERAZIONI_ASYNC,
						ForwardParams.LIST());
			}

			int idLista = Liste.PDND_INTERAZIONI_ASYNC;
			ricerca = helper.checkSearchParameters(idLista, ricerca);
			
			String sorgente = InterazioniAsincronePDNDHelper.getSorgente(ricerca, sorgenti);
			InterazioniAsincronePDNDFiltro filtro = InterazioniAsincronePDNDHelper.buildFiltro(ricerca);
			
			long count = core.count(filtro, sorgente);
			ricerca.setNumEntries(idLista, (int) count);
			List<InterazioneAsincronaPDND> lista = core.list(filtro, sorgente);
			
			helper.prepareInterazioniAsincronePDNDList(ricerca, lista, sorgente, sorgenti, core.getLabelSorgentiDati());
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					InterazioniAsincronePDNDCostanti.OBJECT_NAME_PDND_INTERAZIONI_ASYNC,
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					InterazioniAsincronePDNDCostanti.OBJECT_NAME_PDND_INTERAZIONI_ASYNC, ForwardParams.LIST());
		}
	}
}
