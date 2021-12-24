/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.config;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.constants.PluginCostanti;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConfigurazionePluginsClassiList
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazionePluginsClassiList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);

			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Preparo il menu
			confHelper.makeMenu();
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_PLUGINS_CLASSI;

			// poiche' esistono filtri che hanno necessita di postback salvo in sessione
			List<Plugin> lista = null;
			if(!ServletUtils.isSearchDone(confHelper)) {
				lista = ServletUtils.getRisultatiRicercaFromSession(session, idLista, Plugin.class);
			}
			
			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			clearFiltriByPostBackTipoPlugin(confHelper, 0, ricerca, idLista);
			
			if(lista==null) {
				lista = confCore.pluginsClassiList(ricerca);
			}
			
			if(!confHelper.isPostBackFilterElement()) {
				ServletUtils.setRisultatiRicercaIntoSession(session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
			}
			
			confHelper.preparePluginsClassiList(ricerca, lista); 
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(session, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_CLASSI,
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_CLASSI, ForwardParams.LIST());
		}
	}
	
	private void clearFiltriByPostBackTipoPlugin(ConfigurazioneHelper confHelper, int posizioneFiltroTipoPlugin, ISearch ricerca, int idLista) throws Exception {
		String postBackElement = confHelper.getPostBackElementName();
		if((Costanti.PARAMETRO_FILTER_VALUE+posizioneFiltroTipoPlugin).equals(postBackElement)) {
			ricerca.clearFilter(idLista, PluginCostanti.FILTRO_RUOLO_NOME);
			ricerca.clearFilter(idLista, PluginCostanti.FILTRO_SERVICE_HANDLER_NOME);
			ricerca.clearFilter(idLista, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME);
			ricerca.clearFilter(idLista, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME);
			ricerca.clearFilter(idLista, PluginCostanti.FILTRO_APPLICABILITA_NOME);
		}
	}
}
