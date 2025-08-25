/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
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
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.plugins.utils.handlers.ConfigurazioneHandlerBean;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**     
 * ConfigurazioneHandlersRispostaAdd
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneHandlersRispostaAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	
		
		TipoOperazione tipoOperazione = TipoOperazione.ADD;
		
		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);
			
			// Preparo il menu
			confHelper.makeMenu();
			
			// controllo primo accesso
			boolean first = confHelper.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FIRST_TIME);
					
			String ruoloPortaParam = confHelper.getParametroTipoPdD(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_RUOLO_PORTA);
			TipoPdD ruoloPorta = null;
			if(ruoloPortaParam!=null) {
				ruoloPorta = TipoPdD.toTipoPdD(ruoloPortaParam);
			}
			
			String idPortaS = confHelper.getParametroLong(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_ID_PORTA);
			Long idPorta = null;
			if(StringUtils.isNotBlank(idPortaS)) {
				idPorta = Long.parseLong(idPortaS);
			}
			
			ServiceBinding serviceBinding = null;
			String serviceBindingParam = confHelper.getParametroServiceBinding(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_SERVICE_BINDING);
			if(serviceBindingParam!=null && !"".equals(serviceBindingParam)) {
				serviceBinding = ServiceBinding.valueOf(serviceBindingParam);
			}
			String nomePlugin = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_PLUGIN);
			String fase = confHelper.getParametroFaseMessageHandler(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FASE);
			String stato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_STATO);
			
			FaseMessageHandler faseMH = FaseMessageHandler.toEnumConstant(fase);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
			List<String> tipiPluginGiaUtilizzati = null;
			ConsoleSearch ricercaPluginUilizzati = new ConsoleSearch(true);
			List<ConfigurazioneHandlerBean> listaEsistenti = confCore.handlersRispostaList(ricercaPluginUilizzati, fase, ruoloPorta, idPorta); 
			if(listaEsistenti != null && !listaEsistenti.isEmpty()) {
				tipiPluginGiaUtilizzati = new ArrayList<>();
				
				for (ConfigurazioneHandlerBean configurazioneHandlerBean : listaEsistenti) {
					tipiPluginGiaUtilizzati.add(configurazioneHandlerBean.getPlugin().getTipo());
				}
			}
						
			List<Parameter> lstParamSession = new ArrayList<>();

			Parameter parRuoloPorta = null;
			if(ruoloPorta!=null) {
				parRuoloPorta = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_RUOLO_PORTA, ruoloPorta.getTipo());
				lstParamSession.add(parRuoloPorta);
			}
			Parameter parIdPorta = null;
			if(idPortaS!=null) {
				parIdPorta = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_ID_PORTA, idPortaS);
				lstParamSession.add(parIdPorta);
			}
			Parameter parServiceBinding = null;
			if(serviceBinding!=null) {
				parServiceBinding = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_SERVICE_BINDING, serviceBinding.name());
				lstParamSession.add(parServiceBinding);
			}
			Parameter parTipologia = null;
			if(fase!=null) {
				parTipologia = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FASE, fase);
				lstParamSession.add(parTipologia);
			}
			
			String labelHandler = confHelper.getLabelTipologiaFromFaseMessageHandler(fase,false);
			String labelHandlerDi = labelHandler + " di ";
			String servletListURL = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_HANDLERS_RISPOSTA_LIST;
			String messaggioValoriNonDisponibili = MessageFormat.format(ConfigurazioneCostanti.LABEL_HANDLER_PLUGIN_NON_DISPONIBILI_PER_LA_FASE, labelHandler);
			String messaggioHandlerRispostaDuplicato = null;
			String tipo = null;
			Plugin plugin = null; 
			
			if(nomePlugin != null) {
				plugin = confCore.getPlugin(TipoPlugin.MESSAGE_HANDLER, nomePlugin, false); 
			}
			
			if(plugin != null) {
				messaggioHandlerRispostaDuplicato = MessageFormat.format(ConfigurazioneCostanti.MESSAGGIO_HANDLER_RISPOSTA_DUPLICATO, labelHandler, plugin.getLabel());
				tipo = plugin.getTipo();
			}
			
			List<Parameter> lstParamPorta = null;
			if(ruoloPorta!=null) {
				lstParamPorta = confHelper.getTitleListHandler(fase, ruoloPorta, idPortaS, serviceBinding, Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, 
						servletListURL, labelHandlerDi, labelHandler);
			}
			
			// setto la barra del titolo
			List<Parameter> lstParam = null;
			if(lstParamPorta!=null) {
				lstParam = lstParamPorta;
			}
			else {
				lstParam = new ArrayList<>();
				
				if(!lstParamSession.isEmpty()) {
					lstParam.add(new Parameter(labelHandler, servletListURL, lstParamSession.toArray(new Parameter[lstParamSession.size()])));
				} else {
					lstParam.add(new Parameter(labelHandler, servletListURL));
				}
				lstParam.add(ServletUtils.getParameterAggiungi());
			}
			
			if(ruoloPorta == null) {
				lstParam.add(0,new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			}
			
			// Se tipo = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				if(first) {
					nomePlugin = "";
					stato = StatoFunzionalita.ABILITATO.getValue();
				}
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				confHelper.addHandlerRispostaToDati(dati, tipoOperazione, null, nomePlugin, stato, ruoloPorta, idPortaS, serviceBinding, fase, tipiPluginGiaUtilizzati, messaggioValoriNonDisponibili); 
				
				if(pd.getMessage() != null && pd.isDisableEditMode()) {
					dati = new ArrayList<>();
				}
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FIRST_TIME);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_RISPOSTA, ForwardParams.ADD());
			}
			
			// Controlli sui campi immessi
			boolean isOk = confHelper.handlerRispostaCheckData(TipoOperazione.ADD, null, nomePlugin, stato, ruoloPorta, idPorta, fase, tipo, messaggioHandlerRispostaDuplicato);
			if (!isOk) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				confHelper.addHandlerRispostaToDati(dati, tipoOperazione, null, nomePlugin, stato, ruoloPorta, idPortaS, serviceBinding, fase, tipiPluginGiaUtilizzati, messaggioValoriNonDisponibili);
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FIRST_TIME);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_RISPOSTA, 
						ForwardParams.ADD());
			}
			
			ConfigurazioneHandler handler = new ConfigurazioneHandler();
			
			handler.setTipo(plugin.getTipo());
			handler.setStato(StatoFunzionalita.toEnumConstant(stato));
			int posizione = confCore.getMaxPosizioneHandlersRisposta(fase,ruoloPorta,idPorta) + 1;
			handler.setPosizione(posizione);
			
			List<Object> oggettiDaAggiornare = new ArrayList<>();
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
						portaDelegata.getConfigurazioneHandler().getResponse().addIn(handler);
						break;
					case IN_PROTOCOL_INFO:
						portaDelegata.getConfigurazioneHandler().getResponse().addInProtocolInfo(handler);
						break;
					case OUT:
						portaDelegata.getConfigurazioneHandler().getResponse().addOut(handler);
						break;
					case POST_OUT:
						portaDelegata.getConfigurazioneHandler().getResponse().addPostOut(handler);
						break;
					case PRE_IN:
						portaDelegata.getConfigurazioneHandler().getResponse().addPreIn(handler);
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
						portaApplicativa.getConfigurazioneHandler().getResponse().addIn(handler);
						break;
					case IN_PROTOCOL_INFO:
						portaApplicativa.getConfigurazioneHandler().getResponse().addInProtocolInfo(handler);
						break;
					case OUT:
						portaApplicativa.getConfigurazioneHandler().getResponse().addOut(handler);
						break;
					case POST_OUT:
						portaApplicativa.getConfigurazioneHandler().getResponse().addPostOut(handler);
						break;
					case PRE_IN:
						portaApplicativa.getConfigurazioneHandler().getResponse().addPreIn(handler);
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
					configurazione.getConfigurazioneHandler().getResponse().addIn(handler);
					break;
				case IN_PROTOCOL_INFO:
					configurazione.getConfigurazioneHandler().getResponse().addInProtocolInfo(handler);
					break;
				case OUT:
					configurazione.getConfigurazioneHandler().getResponse().addOut(handler);
					break;
				case POST_OUT:
					configurazione.getConfigurazioneHandler().getResponse().addPostOut(handler);
					break;
				case PRE_IN:
					configurazione.getConfigurazioneHandler().getResponse().addPreIn(handler);
					break;
				}
				oggettiDaAggiornare.add(configurazione);
			}
			
			// insert sul db
			confCore.performUpdateOperation(userLogin, confHelper.smista(), oggettiDaAggiornare.toArray(new Object[oggettiDaAggiornare.size()]));
			
			// Preparo la lista
			int idLista = Liste.CONFIGURAZIONE_HANDLERS_RISPOSTA;
			
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<ConfigurazioneHandlerBean> lista = confCore.handlersRispostaList(ricerca, fase, ruoloPorta, idPorta); 
			
			confHelper.prepareHandlersRispostaList(ricerca, lista, ruoloPorta, idPortaS, serviceBinding, fase);
			
			if(ruoloPorta == null) {
				pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MESSAGE_HANDLERS_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			}
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_RISPOSTA, ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_RISPOSTA, ForwardParams.ADD());
		}  
	}
}			
