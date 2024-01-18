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
package org.openspcoop2.web.ctrlstat.servlet.remote_stores;

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
import org.openspcoop2.pdd.core.keystore.RemoteStoreKeyEntry;
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
 * RemoteStoresKeysDel
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoresKeysDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {
			RemoteStoresHelper remoteStoresHelper = new RemoteStoresHelper(request, pd, session);

			RemoteStoresCore remoteStoresCore = new RemoteStoresCore();

			// Preparo il menu
			remoteStoresHelper.makeMenu();
			
			String objToRemove =remoteStoresHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			
			String filterRemoteStoreId = remoteStoresHelper.getParameter(RemoteStoresCostanti.PARAMETRO_REMOTE_STORE_ID);
			long remoteStoreId = Long.parseLong(filterRemoteStoreId);
			
			boolean deleteAlmostOneEntry = false;
			
			for (int i = 0; i < idsToRemove.size(); i++) {
				RemoteStoreKeyEntry remoteStoreKeyEntry = remoteStoresCore.getRemoteStoreKeyEntry(Long.parseLong(idsToRemove.get(i)));
				remoteStoresCore.performDeleteOperation(userLogin, remoteStoresHelper.smista(), remoteStoreKeyEntry);
				deleteAlmostOneEntry = true;
			}
			
			int idLista = Liste.REMOTE_STORE_KEY;
			
			if(deleteAlmostOneEntry) {
				ServletUtils.removeRisultatiRicercaFromSession(request, session, idLista);
			}
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			ricerca = remoteStoresHelper.checkSearchParameters(idLista, ricerca);

			List<RemoteStoreKeyEntry> lista = remoteStoresCore.remoteStoreKeysList(ricerca, remoteStoreId);

			ServletUtils.setRisultatiRicercaIntoSession(request, session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
			
			remoteStoresHelper.prepareRemoteStoreKeysList(ricerca, lista, remoteStoreId);
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, RemoteStoresCostanti.OBJECT_NAME_REMOTE_STORES_KEYS, ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					RemoteStoresCostanti.OBJECT_NAME_REMOTE_STORES_KEYS, ForwardParams.DEL());
		}
	}
}
