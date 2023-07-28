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
import org.openspcoop2.core.config.ConfigurazioneMessageHandlers;
import org.openspcoop2.core.config.ConfigurazionePortaHandler;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.FaseMessageHandler;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.plugins.utils.handlers.ConfigurazioneHandlerBean;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConfigurazioneHandlersRispostaDel
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneHandlersRispostaDel extends Action {

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
			
			String ruoloPortaParam = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_RUOLO_PORTA);
			TipoPdD ruoloPorta = null;
			if(ruoloPortaParam!=null) {
				ruoloPorta = TipoPdD.toTipoPdD(ruoloPortaParam);
			}
			String idPortaS = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_ID_PORTA);
			Long idPorta = null;
			if(StringUtils.isNotBlank(idPortaS)) {
				idPorta = Long.parseLong(idPortaS);
			}
			ServiceBinding serviceBinding = null;
			String serviceBindingParam = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_SERVICE_BINDING);
			if(serviceBindingParam!=null && !"".equals(serviceBindingParam)) {
				serviceBinding = ServiceBinding.valueOf(serviceBindingParam);
			}
			
			String idTab = confHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!confHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			String fase = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FASE);
			FaseMessageHandler faseMH = FaseMessageHandler.toEnumConstant(fase);
			
			String objToRemove =confHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			List<Object> oggettiDaAggiornare = new ArrayList<>();
			List<ConfigurazioneHandler> listaDaAggiornare = null;
			if(ruoloPorta !=null) {
				if(ruoloPorta.equals(TipoPdD.DELEGATA)) {
					PorteDelegateCore porteDelegateCore = new PorteDelegateCore(confCore);
					PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idPorta);
					
					if(portaDelegata.getConfigurazioneHandler() == null)
						portaDelegata.setConfigurazioneHandler(new ConfigurazionePortaHandler());
					
					if(portaDelegata.getConfigurazioneHandler().getResponse() == null)
						portaDelegata.getConfigurazioneHandler().setResponse(new ConfigurazioneMessageHandlers());
					
					switch (faseMH) {
					case IN:
						listaDaAggiornare = portaDelegata.getConfigurazioneHandler().getResponse().getInList();
						break;
					case IN_PROTOCOL_INFO:
						listaDaAggiornare = portaDelegata.getConfigurazioneHandler().getResponse().getInProtocolInfoList();
						break;
					case OUT:
						listaDaAggiornare = portaDelegata.getConfigurazioneHandler().getResponse().getOutList();
						break;
					case POST_OUT:
						listaDaAggiornare = portaDelegata.getConfigurazioneHandler().getResponse().getPostOutList();
						break;
					case PRE_IN:
						listaDaAggiornare = portaDelegata.getConfigurazioneHandler().getResponse().getPreInList();
						break;
					}
					oggettiDaAggiornare.add(portaDelegata);
				}
				else if(ruoloPorta.equals(TipoPdD.APPLICATIVA)) {
					PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(confCore);
					PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(idPorta);
					
					if(portaApplicativa.getConfigurazioneHandler() == null)
						portaApplicativa.setConfigurazioneHandler(new ConfigurazionePortaHandler());
					
					if(portaApplicativa.getConfigurazioneHandler().getResponse() == null)
						portaApplicativa.getConfigurazioneHandler().setResponse(new ConfigurazioneMessageHandlers());
					
					switch (faseMH) {
					case IN:
						listaDaAggiornare = portaApplicativa.getConfigurazioneHandler().getResponse().getInList();
						break;
					case IN_PROTOCOL_INFO:
						listaDaAggiornare = portaApplicativa.getConfigurazioneHandler().getResponse().getInProtocolInfoList();
						break;
					case OUT:
						listaDaAggiornare = portaApplicativa.getConfigurazioneHandler().getResponse().getOutList();
						break;
					case POST_OUT:
						listaDaAggiornare = portaApplicativa.getConfigurazioneHandler().getResponse().getPostOutList();
						break;
					case PRE_IN:
						listaDaAggiornare = portaApplicativa.getConfigurazioneHandler().getResponse().getPreInList();
						break;
					}	
					oggettiDaAggiornare.add(portaApplicativa);
				}
			} else {
				Configurazione configurazione = confCore.getConfigurazioneGenerale();
				
				if(configurazione.getConfigurazioneHandler() == null)
					configurazione.setConfigurazioneHandler(new ConfigurazioneGeneraleHandler());
				
				if(configurazione.getConfigurazioneHandler().getResponse() == null)
					configurazione.getConfigurazioneHandler().setResponse(new ConfigurazioneMessageHandlers());
				
				switch (faseMH) {
				case IN:
					listaDaAggiornare = configurazione.getConfigurazioneHandler().getResponse().getInList();
					break;
				case IN_PROTOCOL_INFO:
					listaDaAggiornare = configurazione.getConfigurazioneHandler().getResponse().getInProtocolInfoList();
					break;
				case OUT:
					listaDaAggiornare = configurazione.getConfigurazioneHandler().getResponse().getOutList();
					break;
				case POST_OUT:
					listaDaAggiornare = configurazione.getConfigurazioneHandler().getResponse().getPostOutList();
					break;
				case PRE_IN:
					listaDaAggiornare = configurazione.getConfigurazioneHandler().getResponse().getPreInList();
					break;
				}
				oggettiDaAggiornare.add(configurazione);
			}
			
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

			int idLista = Liste.CONFIGURAZIONE_HANDLERS_RISPOSTA;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<ConfigurazioneHandlerBean> lista = confCore.handlersRispostaList(ricerca, fase, ruoloPorta, idPorta); 
			
			confHelper.prepareHandlersRispostaList(ricerca, lista, ruoloPorta, idPortaS, serviceBinding, fase);
						
			if(ruoloPorta == null) {
				pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MESSAGE_HANDLERS_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			}
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_RISPOSTA,
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_RISPOSTA, ForwardParams.DEL());
		}
	}
}
