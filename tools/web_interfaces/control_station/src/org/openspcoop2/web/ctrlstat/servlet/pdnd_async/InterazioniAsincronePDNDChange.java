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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.pdd.core.pdnd.async.InterazioneAsincronaPDND;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.ctrlstat.servlet.remote_stores.RemoteStoresCostanti;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * Visualizzazione del dettaglio di un'interazione (sola lettura)
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InterazioniAsincronePDNDChange extends Action {

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
			
			String idS = helper.getParameter(InterazioniAsincronePDNDCostanti.PARAMETRO_PDND_INTERAZIONI_ASYNC_ID);
			long id = Long.parseLong(idS);
			
			InterazioneAsincronaPDND interazione = core.get(id, sorgente);
			if(interazione==null) {
				throw new IllegalArgumentException("Interazione con id '"+id+"' non trovata (potrebbe essere stata eliminata)");
			}
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, 
					new Parameter(RemoteStoresCostanti.LABEL_CACHE_PDND, RemoteStoresCostanti.SERVLET_NAME_CACHE_PDND),
					new Parameter(InterazioniAsincronePDNDCostanti.LABEL_PDND_INTERAZIONI_ASYNC, InterazioniAsincronePDNDCostanti.SERVLET_NAME_PDND_INTERAZIONI_ASYNC_LIST),
					new Parameter(interazione.getInteractionId(), null));
			
			// preparo i campi
			List<DataElement> dati = new ArrayList<>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());

			dati = helper.addInterazioneAsincronaPDNDToDati(interazione, sorgente, dati);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping, InterazioniAsincronePDNDCostanti.OBJECT_NAME_PDND_INTERAZIONI_ASYNC, ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					InterazioniAsincronePDNDCostanti.OBJECT_NAME_PDND_INTERAZIONI_ASYNC, ForwardParams.CHANGE());
		}
	}
}
