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

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConfigurazioneAllarmiList
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneAllarmiList extends Action {

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
			
			String ruoloPortaParam = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALLARMI_RUOLO_PORTA);
			RuoloPorta ruoloPorta = null;
			if(ruoloPortaParam!=null) {
				ruoloPorta = RuoloPorta.toEnumConstant(ruoloPortaParam);
			}
			String nomePorta = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALLARMI_NOME_PORTA);
			ServiceBinding serviceBinding = null;
			String serviceBindingParam = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALLARMI_SERVICE_BINDING);
			if(serviceBindingParam!=null && !"".equals(serviceBindingParam)) {
				serviceBinding = ServiceBinding.valueOf(serviceBindingParam);
			}
			
			String idTab = confHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!confHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}

			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Preparo il menu
			confHelper.makeMenu();
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(request, session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_ALLARMI;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<ConfigurazioneAllarmeBean> lista = confCore.allarmiList(ricerca, ruoloPorta, nomePorta); 
			
			confHelper.prepareAllarmiList(ricerca, lista, ruoloPorta, nomePorta, serviceBinding);
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ALLARMI,
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ALLARMI, ForwardParams.LIST());
		}
	}
}
