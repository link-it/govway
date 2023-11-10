package org.openspcoop2.web.ctrlstat.servlet.remote_stores;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.pdd.core.keystore.RemoteStore;
import org.openspcoop2.pdd.core.keystore.RemoteStoreKeyEntry;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * RemoteStoresKeysChange
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoresKeysChange extends Action {

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
			
			String eemoteStoreIdS = remoteStoresHelper.getParameter(RemoteStoresCostanti.PARAMETRO_REMOTE_STORE_ID);
			long remoteStoreId = Long.parseLong(eemoteStoreIdS);
			
			String idS = remoteStoresHelper.getParameter(RemoteStoresCostanti.PARAMETRO_REMOTE_STORE_KEY_ID);
			long id = Long.parseLong(idS);
			
			RemoteStoreKeyEntry remoteStoreKeyEntry = remoteStoresCore.getRemoteStoreKeyEntry(id);
			
			// nome della breadcrumbs principale, se ci sono piu' store visualizzo quello selezionato nei filtri
			List<RemoteStore> remoteStoresList = remoteStoresCore.remoteStoresList();

			String labelNomeCache = RemoteStoresCostanti.LABEL_CACHE_PDND;
			
			if(remoteStoresList.size() > 1) {
				String nomeStore = remoteStoresList.stream().filter(f -> f.getId() == remoteStoreId).map(f -> f.getNome()).collect(Collectors.toList()).get(0);
				labelNomeCache = RemoteStoresCostanti.getBreadCrumbRemoteStore(nomeStore);
			}
			
			String labelNomeEntry = remoteStoreKeyEntry.getClientId() != null ? remoteStoreKeyEntry.getClientId() : remoteStoreKeyEntry.getKid();
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle_ServletChange(pd, labelNomeCache, RemoteStoresCostanti.SERVLET_NAME_REMOTE_STORES_KEYS_LIST, labelNomeEntry);
			
			// preparo i campi
			List<DataElement> dati = new ArrayList<>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());

			dati = remoteStoresHelper.addRemoteStoreKeyToDati(remoteStoreId, id, remoteStoreKeyEntry, dati);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping, RemoteStoresCostanti.OBJECT_NAME_REMOTE_STORES_KEYS, ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					RemoteStoresCostanti.OBJECT_NAME_REMOTE_STORES_KEYS, ForwardParams.CHANGE());
		}
	}
}
