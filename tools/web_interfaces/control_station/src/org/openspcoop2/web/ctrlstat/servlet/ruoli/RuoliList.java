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


package org.openspcoop2.web.ctrlstat.servlet.ruoli;

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
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * RuoliList
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class RuoliList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			RuoliHelper ruoliHelper = new RuoliHelper(request, pd, session);

			RuoliCore ruoliCore = new RuoliCore();

			// Preparo il menu
			ruoliHelper.makeMenu();

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.RUOLI;
			
			// poiche' esistono filtri che hanno necessita di postback salvo in sessione
			List<Ruolo> lista = null;
			if(!ServletUtils.isSearchDone(ruoliHelper)) {
				lista = ServletUtils.getRisultatiRicercaFromSession(session, idLista,  Ruolo.class);
			}

			ricerca = ruoliHelper.checkSearchParameters(idLista, ricerca);
			
			ruoliHelper.clearFiltroSoggettoByPostBackProtocollo(RuoliHelper.POSIZIONE_FILTRO_PROTOCOLLO, ricerca, idLista);
			
			if(lista==null) {
				boolean filtroSoggetto = false;
				String filterApiContesto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_API_CONTESTO);
				if(!Filtri.FILTRO_API_CONTESTO_VALUE_SOGGETTI.equals(filterApiContesto) && ruoliHelper.isSoggettoMultitenantSelezionato()) {
					List<String> protocolli = ruoliCore.getProtocolli(session,false);
					if(protocolli!=null && protocolli.size()==1) { // dovrebbe essere l'unico caso in cui un soggetto multitenant è selezionato
						filtroSoggetto = true;
					}
				}
				if(filtroSoggetto) {
					ricerca.addFilter(idLista, Filtri.FILTRO_SOGGETTO, ruoliHelper.getSoggettoMultitenantSelezionato());
				}
				
				String userLogin = ServletUtils.getUserLoginFromSession(session);
				if(ruoliCore.isVisioneOggettiGlobale(userLogin)){
					lista = ruoliCore.ruoliList(null, ricerca);
				}else{
					lista = ruoliCore.ruoliList(userLogin, ricerca);
				}
			}
			
			if(!ruoliHelper.isPostBackFilterElement()) {
				ServletUtils.setRisultatiRicercaIntoSession(session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
			}
			
			ruoliHelper.prepareRuoliList(ricerca, lista);
			
			String msg = ruoliHelper.getParameter(Costanti.PARAMETER_NAME_MSG_ERROR_EXPORT);
			if(msg!=null && !"".equals(msg)){
				pd.setMessage("Errore durante esportazione: "+msg);
			}
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(session, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					RuoliCostanti.OBJECT_NAME_RUOLI,
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					RuoliCostanti.OBJECT_NAME_RUOLI, ForwardParams.LIST());
		}
	}
}
