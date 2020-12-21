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


package org.openspcoop2.web.ctrlstat.servlet.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConfigurazionePluginsArchiviList
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazionePluginsArchiviList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);
		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);

			String cambiaPosizione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_POSIZIONE);
			String nome = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_NOME);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Preparo il menu
			confHelper.makeMenu();
			
			if(StringUtils.isNotEmpty(cambiaPosizione)) {
				
				List<Object> pluginsDaSpostare =new ArrayList<>();
				
				RegistroPlugin regolaToMove = confCore.getDatiRegistroPlugin(nome);
				
				int posizioneAttuale = regolaToMove.getPosizione();
				int posizioneNuova = cambiaPosizione.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SU) ? (posizioneAttuale - 1) : (posizioneAttuale + 1);
				
				RegistroPlugin regolaToSwitch = confCore.getDatiRegistroPluginFromPosizione(posizioneNuova);
				
				regolaToMove.setPosizione(posizioneNuova);
				regolaToSwitch.setPosizione(posizioneAttuale);
				
				pluginsDaSpostare.add(regolaToMove);
				pluginsDaSpostare.add(regolaToSwitch);
				
				confCore.performUpdateOperation(userLogin, confHelper.smista(), pluginsDaSpostare.toArray(new Object[pluginsDaSpostare.size()]));
				if(CostantiControlStation.VISUALIZZA_MESSAGGIO_CONFERMA_SPOSTAMENTO_PLUGINS_ARCHIVI)
					pd.setMessage(CostantiControlStation.MESSAGGIO_CONFERMA_PLUGINS_ARCHIVIO_SPOSTATO_CORRETTAMENTE, MessageType.INFO);
			}

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_PLUGINS_ARCHIVI;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<RegistroPlugin> lista = confCore.pluginsArchiviList(ricerca);  
			
			confHelper.preparePluginsArchiviList(ricerca, lista); 
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(session, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI,
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI, ForwardParams.LIST());
		}
	}
}
