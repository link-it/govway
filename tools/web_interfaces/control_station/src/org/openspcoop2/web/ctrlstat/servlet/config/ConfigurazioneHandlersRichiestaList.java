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
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
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
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConfigurazioneHandlersRichiestaList
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneHandlersRichiestaList extends Action {

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
			String cambiaPosizione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_POSIZIONE);
			String idHandlerS = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_ID_HANDLER);

			FaseMessageHandler faseMH = FaseMessageHandler.toEnumConstant(fase);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
			if(StringUtils.isNotEmpty(cambiaPosizione)) {
				
				Long idHandler = Long.parseLong(idHandlerS);
				
				List<Object> oggettiDaAggiornare = new ArrayList<>();
				List<ConfigurazioneHandler> listaDaAggiornare = null;
				if(ruoloPorta !=null) {
					if(ruoloPorta.equals(TipoPdD.DELEGATA)) {
						PorteDelegateCore porteDelegateCore = new PorteDelegateCore(confCore);
						PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idPorta);
						
						if(portaDelegata.getConfigurazioneHandler() == null)
							portaDelegata.setConfigurazioneHandler(new ConfigurazionePortaHandler());
						
						if(portaDelegata.getConfigurazioneHandler().getRequest() == null)
							portaDelegata.getConfigurazioneHandler().setRequest(new ConfigurazioneMessageHandlers());
						
						switch (faseMH) {
						case IN:
							listaDaAggiornare = portaDelegata.getConfigurazioneHandler().getRequest().getInList();
							break;
						case IN_PROTOCOL_INFO:
							listaDaAggiornare = portaDelegata.getConfigurazioneHandler().getRequest().getInProtocolInfoList();
							break;
						case OUT:
							listaDaAggiornare = portaDelegata.getConfigurazioneHandler().getRequest().getOutList();
							break;
						case POST_OUT:
							listaDaAggiornare = portaDelegata.getConfigurazioneHandler().getRequest().getPostOutList();
							break;
						case PRE_IN:
							listaDaAggiornare = portaDelegata.getConfigurazioneHandler().getRequest().getPreInList();
							break;
						}
						oggettiDaAggiornare.add(portaDelegata);
					}
					else if(ruoloPorta.equals(TipoPdD.APPLICATIVA)) {
						PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(confCore);
						PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(idPorta);
						
						if(portaApplicativa.getConfigurazioneHandler() == null)
							portaApplicativa.setConfigurazioneHandler(new ConfigurazionePortaHandler());
						
						if(portaApplicativa.getConfigurazioneHandler().getRequest() == null)
							portaApplicativa.getConfigurazioneHandler().setRequest(new ConfigurazioneMessageHandlers());
						
						switch (faseMH) {
						case IN:
							listaDaAggiornare = portaApplicativa.getConfigurazioneHandler().getRequest().getInList();
							break;
						case IN_PROTOCOL_INFO:
							listaDaAggiornare = portaApplicativa.getConfigurazioneHandler().getRequest().getInProtocolInfoList();
							break;
						case OUT:
							listaDaAggiornare = portaApplicativa.getConfigurazioneHandler().getRequest().getOutList();
							break;
						case POST_OUT:
							listaDaAggiornare = portaApplicativa.getConfigurazioneHandler().getRequest().getPostOutList();
							break;
						case PRE_IN:
							listaDaAggiornare = portaApplicativa.getConfigurazioneHandler().getRequest().getPreInList();
							break;
						}	
						oggettiDaAggiornare.add(portaApplicativa);
					}
				} else {
					Configurazione configurazione = confCore.getConfigurazioneGenerale();
					
					if(configurazione.getConfigurazioneHandler() == null)
						configurazione.setConfigurazioneHandler(new ConfigurazioneGeneraleHandler());
					
					if(configurazione.getConfigurazioneHandler().getRequest() == null)
						configurazione.getConfigurazioneHandler().setRequest(new ConfigurazioneMessageHandlers());
					
					switch (faseMH) {
					case IN:
						listaDaAggiornare = configurazione.getConfigurazioneHandler().getRequest().getInList();
						break;
					case IN_PROTOCOL_INFO:
						listaDaAggiornare = configurazione.getConfigurazioneHandler().getRequest().getInProtocolInfoList();
						break;
					case OUT:
						listaDaAggiornare = configurazione.getConfigurazioneHandler().getRequest().getOutList();
						break;
					case POST_OUT:
						listaDaAggiornare = configurazione.getConfigurazioneHandler().getRequest().getPostOutList();
						break;
					case PRE_IN:
						listaDaAggiornare = configurazione.getConfigurazioneHandler().getRequest().getPreInList();
						break;
					}
					oggettiDaAggiornare.add(configurazione);
				}
				
				if(listaDaAggiornare!=null) {
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
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.CONFIGURAZIONE_HANDLERS_RICHIESTA;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<ConfigurazioneHandlerBean> lista = confCore.handlersRichiestaList(ricerca, fase, ruoloPorta, idPorta); 
			
			confHelper.prepareHandlersRichiestaList(ricerca, lista, ruoloPorta, idPortaS, serviceBinding, fase);
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_RICHIESTA,
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_RICHIESTA, ForwardParams.LIST());
		}
	}
}
