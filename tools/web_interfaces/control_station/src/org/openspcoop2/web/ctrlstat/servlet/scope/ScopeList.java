/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.scope;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ScopeList
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ScopeList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ScopeHelper scopeHelper = new ScopeHelper(request, pd, session);

			ScopeCore scopeCore = new ScopeCore();

			// Preparo il menu
			scopeHelper.makeMenu();

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.SCOPE;

			// poiche' esistono filtri che hanno necessita di postback salvo in sessione
			List<Scope> lista = null;
			if(!ServletUtils.isSearchDone(scopeHelper)) {
				lista = ServletUtils.getRisultatiRicercaFromSession(session, idLista, Scope.class);
			}
			
			ricerca = scopeHelper.checkSearchParameters(idLista, ricerca);
			
			scopeHelper.clearFiltroSoggettoByPostBackProtocollo(ScopeHelper.POSIZIONE_FILTRO_PROTOCOLLO, ricerca, idLista);
			
			if(lista==null) {
				boolean filtroSoggetto = false;
				List<String> protocolli = scopeCore.getProtocolli(session,false);
				if(protocolli!=null && protocolli.size()==1) { // dovrebbe essere l'unico caso in cui un soggetto multitenant è selezionato
					filtroSoggetto = true;
				}
				if(filtroSoggetto) {
					ricerca.addFilter(idLista, Filtri.FILTRO_SOGGETTO, scopeHelper.getSoggettoMultitenantSelezionato());
				}
				
				String userLogin = ServletUtils.getUserLoginFromSession(session);
				if(scopeCore.isVisioneOggettiGlobale(userLogin)){
					lista = scopeCore.scopeList(null, ricerca);
				}else{
					lista = scopeCore.scopeList(userLogin, ricerca);
				}
			}
			
			if(!scopeHelper.isPostBackFilterElement()) {
				ServletUtils.setRisultatiRicercaIntoSession(session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
			}
			
			scopeHelper.prepareScopeList(ricerca, lista);
			
			String msg = scopeHelper.getParameter(Costanti.PARAMETER_NAME_MSG_ERROR_EXPORT);
			if(msg!=null && !"".equals(msg)){
				pd.setMessage("Errore durante esportazione: "+msg);
			}
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(session, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					ScopeCostanti.OBJECT_NAME_SCOPE,
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ScopeCostanti.OBJECT_NAME_SCOPE, ForwardParams.LIST());
		}
	}
}
