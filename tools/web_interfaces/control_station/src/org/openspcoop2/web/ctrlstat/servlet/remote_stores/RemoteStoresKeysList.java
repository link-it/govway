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
package org.openspcoop2.web.ctrlstat.servlet.remote_stores;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.pdd.core.keystore.RemoteStore;
import org.openspcoop2.pdd.core.keystore.RemoteStoreKeyEntry;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * RemoteStoresKeysList
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoresKeysList extends Action {

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
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			// controllo che ci siano a disposizione dei remote stores
			List<RemoteStore> remoteStoresList = remoteStoresCore.remoteStoresList();
			
			if(remoteStoresList == null || remoteStoresList.isEmpty()) {
				pd.setMessage(RemoteStoresCostanti.LABEL_NESSUN_REMOTE_STORE_PRESENTE, Costanti.MESSAGE_TYPE_INFO);
				pd.disableEditMode();
				
				// salvo l'oggetto ricerca nella sessione
				ServletUtils.setSearchObjectIntoSession(request, session, ricerca);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				// Forward control to the specified success URI
				return ServletUtils.getStrutsForward (mapping, 
						RemoteStoresCostanti.OBJECT_NAME_REMOTE_STORES_KEYS,
						ForwardParams.LIST());
			}

			int idLista = Liste.REMOTE_STORE_KEY;
			
			// poiche' esistono filtri che hanno necessita di postback salvo in sessione
			List<RemoteStoreKeyEntry> lista = null;
			if(!ServletUtils.isSearchDone(remoteStoresHelper)) {
				lista = ServletUtils.getRisultatiRicercaFromSession(request, session, idLista, RemoteStoreKeyEntry.class);
			}

			ricerca = remoteStoresHelper.checkSearchParameters(idLista, ricerca);
			
			// lettura del remote store corrente dai filtri di ricerca
			String filterRemoteStoreId = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_REMOTE_STORE_ID);
			// primo ingresso, seleziono il primo elemento della lista di remote stores
			long remoteStoreId = -1;
			if(StringUtils.isEmpty(filterRemoteStoreId)) {
				remoteStoreId = remoteStoresList.get(0).getId();
			} else {
				remoteStoreId = Long.parseLong(filterRemoteStoreId);
			}
			
			if(lista==null) {
				lista = remoteStoresCore.remoteStoreKeysList(ricerca, remoteStoreId);
			}
			
			if(!remoteStoresHelper.isPostBackFilterElement()) {
				ServletUtils.setRisultatiRicercaIntoSession(request, session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
			}
			
			remoteStoresHelper.prepareRemoteStoreKeysList(ricerca, lista, remoteStoreId);
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					RemoteStoresCostanti.OBJECT_NAME_REMOTE_STORES_KEYS,
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					RemoteStoresCostanti.OBJECT_NAME_REMOTE_STORES_KEYS, ForwardParams.LIST());
		}
	}
}
