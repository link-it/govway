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
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * InterazioniAsincronePDNDDel
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InterazioniAsincronePDNDDel extends Action {

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
			
			String sorgente = helper.getParameter(InterazioniAsincronePDNDCostanti.PARAMETRO_PDND_INTERAZIONI_ASYNC_SORGENTE);
			List<String> sorgenti = core.getSorgentiDati();
			if(sorgente==null || !sorgenti.contains(sorgente)) {
				throw new IllegalArgumentException("Sorgente dati '"+sorgente+"' sconosciuta");
			}
			
			String objToRemove = helper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 
			List<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			for (String id : idsToRemove) {
				core.delete(Long.parseLong(id), sorgente);
			}
			
			// Preparo la lista
			int idLista = Liste.PDND_INTERAZIONI_ASYNC;
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			ricerca = helper.checkSearchParameters(idLista, ricerca);

			InterazioniAsincronePDNDFiltro filtro = InterazioniAsincronePDNDHelper.buildFiltro(ricerca);
			long count = core.count(filtro, sorgente);
			ricerca.setNumEntries(idLista, (int) count);
			List<InterazioneAsincronaPDND> lista = core.list(filtro, sorgente);
			
			helper.prepareInterazioniAsincronePDNDList(ricerca, lista, sorgente, sorgenti, core.getLabelSorgentiDati());
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, InterazioniAsincronePDNDCostanti.OBJECT_NAME_PDND_INTERAZIONI_ASYNC, ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					InterazioniAsincronePDNDCostanti.OBJECT_NAME_PDND_INTERAZIONI_ASYNC, ForwardParams.DEL());
		}
	}
}
