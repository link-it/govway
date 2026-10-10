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
package org.openspcoop2.web.ctrlstat.servlet.remote_stores;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pdnd_async.InterazioniAsincronePDNDCostanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * Pagina di ingresso 'Cache PDND': collegamenti alle informazioni conservate da GovWay e ottenute tramite la PDND
 * (chiavi pubbliche e client; interazioni degli scambi di dati asincroni)
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CachePdnd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			RemoteStoresHelper remoteStoresHelper = new RemoteStoresHelper(request, pd, session);
			RemoteStoresCore remoteStoresCore = new RemoteStoresCore();

			// Preparo il menu
			remoteStoresHelper.makeMenu();
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, new Parameter(RemoteStoresCostanti.LABEL_CACHE_PDND, null));
			
			List<DataElement> dati = new ArrayList<>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());
			
			DataElement de = new DataElement();
			de.setLabel(RemoteStoresCostanti.LABEL_SEZIONE_INFORMAZIONI_PDND);
			de.setType(DataElementType.TITLE);
			dati.add(de);
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(RemoteStoresCostanti.SERVLET_NAME_REMOTE_STORES_KEYS_LIST);
			de.setValue(RemoteStoresCostanti.LABEL_CHIAVI_CLIENT);
			dati.add(de);
			
			// le interazioni asincrone sono previste solamente con il profilo ModI e sono lette dalla base dati runtime locale
			if(remoteStoresCore.isProfiloModIPAEnabled() && remoteStoresCore.isSinglePdD()) {
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(InterazioniAsincronePDNDCostanti.SERVLET_NAME_PDND_INTERAZIONI_ASYNC_LIST);
				de.setValue(InterazioniAsincronePDNDCostanti.LABEL_PDND_INTERAZIONI_ASYNC);
				dati.add(de);
			}
			
			pd.setDati(dati);
			pd.disableEditMode();

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping, RemoteStoresCostanti.OBJECT_NAME_CACHE_PDND, ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					RemoteStoresCostanti.OBJECT_NAME_CACHE_PDND, ForwardParams.CHANGE());
		}
	}
}
