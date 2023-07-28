/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneGeneraleHandler;
import org.openspcoop2.core.config.ConfigurazioneHandler;
import org.openspcoop2.core.config.ConfigurazioneServiceHandlers;
import org.openspcoop2.core.config.constants.FaseServiceHandler;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.plugins.utils.handlers.ConfigurazioneHandlerBean;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConfigurazioneHandlersServizioDel
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneHandlersServizioDel extends Action {

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
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);
			
			TipoPdD ruoloPorta = null;
			String idPortaS = null;
			ServiceBinding serviceBinding = null;
			
			String idTab = confHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!confHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			String fase = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FASE);

			FaseServiceHandler faseSH = FaseServiceHandler.toEnumConstant(fase);		
			
			String objToRemove =confHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			List<Object> oggettiDaAggiornare = new ArrayList<>();
			List<ConfigurazioneHandler> listaDaAggiornare = null;
			Configurazione configurazione = confCore.getConfigurazioneGenerale();
			
			if(configurazione.getConfigurazioneHandler() == null)
				configurazione.setConfigurazioneHandler(new ConfigurazioneGeneraleHandler());
			
			if(configurazione.getConfigurazioneHandler().getService() == null)
				configurazione.getConfigurazioneHandler().setService(new ConfigurazioneServiceHandlers());
			
			switch (faseSH) {
			case EXIT:
				listaDaAggiornare =configurazione.getConfigurazioneHandler().getService().getExitList();
				break;
			case INIT:
				listaDaAggiornare =configurazione.getConfigurazioneHandler().getService().getInitList();
				break;
			case INTEGRATION_MANAGER_REQUEST:
				listaDaAggiornare =configurazione.getConfigurazioneHandler().getService().getIntegrationManagerRequestList();
				break;
			case INTEGRATION_MANAGER_RESPONSE:
				listaDaAggiornare =configurazione.getConfigurazioneHandler().getService().getIntegrationManagerResponseList();
				break;
			}
			oggettiDaAggiornare.add(configurazione);
			
			if(listaDaAggiornare!=null) {
				for(int j = listaDaAggiornare.size() -1; j >= 0 ; j--) {
					for (int i = 0; i < idsToRemove.size(); i++) {
						if(idsToRemove.get(i).equals(listaDaAggiornare.get(j).getTipo())) {
							listaDaAggiornare.remove(j);
						}
					}
				}
			}
			
			// update sul db
			confCore.performUpdateOperation(userLogin, confHelper.smista(), oggettiDaAggiornare.toArray(new Object[oggettiDaAggiornare.size()]));
			// Preparo il menu
			confHelper.makeMenu();
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.CONFIGURAZIONE_HANDLERS_SERVIZIO;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<ConfigurazioneHandlerBean> lista = confCore.handlersServizioList(ricerca, fase); 
			
			confHelper.prepareHandlersServizioList(ricerca, lista, ruoloPorta, idPortaS, serviceBinding, fase);
						
			if(ruoloPorta == null) {
				pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SERVICE_HANDLERS_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			}
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_SERVIZIO,
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_SERVIZIO, ForwardParams.DEL());
		}
	}
}
