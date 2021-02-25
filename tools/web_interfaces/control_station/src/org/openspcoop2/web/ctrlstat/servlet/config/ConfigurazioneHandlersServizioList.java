/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneGeneraleHandler;
import org.openspcoop2.core.config.ConfigurazioneHandler;
import org.openspcoop2.core.config.ConfigurazioneServiceHandlers;
import org.openspcoop2.core.config.constants.FaseServiceHandler;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.plugins.utils.handlers.ConfigurazioneHandlerBean;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConfigurazioneHandlersServizioList
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneHandlersServizioList extends Action {

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
			
			TipoPdD ruoloPorta = null;
			String idPortaS = null;
			ServiceBinding serviceBinding = null;
			
			String idTab = confHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!confHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			String fase = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FASE);
			String cambiaPosizione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_POSIZIONE);
			String idHandlerS = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_ID_HANDLER);

			FaseServiceHandler faseSH = FaseServiceHandler.toEnumConstant(fase);		
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
			if(StringUtils.isNotEmpty(cambiaPosizione)) {
				
				Long idHandler = Long.parseLong(idHandlerS);
				
				List<Object> oggettiDaAggiornare = new ArrayList<Object>();
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
				
				for(int j = 0; j < listaDaAggiornare.size() ; j++) {
					ConfigurazioneHandler handlerToMove = null;
					ConfigurazioneHandler handlerToCheck = listaDaAggiornare.get(j);
					if(handlerToCheck.getId().equals(idHandler)) {
						handlerToMove = handlerToCheck;
						
						int posizioneAttuale = handlerToMove.getPosizione();
						
						ConfigurazioneHandler handlerToSwitch = null;
						if(cambiaPosizione.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SU)) {
							handlerToSwitch = listaDaAggiornare.get(j-1);
						} else {
							handlerToSwitch = listaDaAggiornare.get(j+1);
						}
						int posizioneNuova = handlerToSwitch.getPosizione();
						
						handlerToMove.setPosizione(posizioneNuova);
						handlerToSwitch.setPosizione(posizioneAttuale);
						break;
					}
				}

				confCore.performUpdateOperation(userLogin, confHelper.smista(), oggettiDaAggiornare.toArray(new Object[oggettiDaAggiornare.size()]));
				if(ConfigurazioneCostanti.VISUALIZZA_MESSAGGIO_CONFERMA_SPOSTAMENTO_HANDLER) {
					pd.setMessage(ConfigurazioneCostanti.MESSAGGIO_CONFERMA_HANDLER_SPOSTATO_CORRETTAMENTE, MessageType.INFO);
				}
				else {
					if(ruoloPorta == null) {
						pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MESSAGE_HANDLERS_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
					}
				}
			}


			// Preparo il menu
			confHelper.makeMenu();
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_HANDLERS_SERVIZIO;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<ConfigurazioneHandlerBean> lista = confCore.handlersServizioList(ricerca, fase); 
			
			confHelper.prepareHandlersServizioList(ricerca, lista, ruoloPorta, idPortaS, serviceBinding, fase);
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(session, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_SERVIZIO,
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_SERVIZIO, ForwardParams.LIST());
		}
	}
}
