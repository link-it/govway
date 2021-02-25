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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.utils.handlers.ConfigurazioneHandlerBean;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**     
 * ConfigurazioneHandlersServizioChange
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneHandlersServizioChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	
		
		TipoOperazione tipoOperazione = TipoOperazione.CHANGE;
		
		try {
			
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);
			
			// controllo primo accesso
			boolean first = confHelper.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FIRST_TIME);
			
			String idHandlerS = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_ID_HANDLER);
			
			TipoPdD ruoloPorta = null;
			String idPortaS = null;
			Long idPorta = null;
			ServiceBinding serviceBinding = null;
			String nomePlugin = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_PLUGIN);
			String fase = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FASE);
			String stato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_STATO);
			
			FaseServiceHandler faseSH = FaseServiceHandler.toEnumConstant(fase);		
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			Long idHandler = Long.parseLong(idHandlerS);
			
			ConfigurazioneHandlerBean handler = confCore.getHandlerServizio(fase, idHandler);
			
			Plugin plugin = handler.getPlugin();
			String tipo = plugin.getTipo();
			List<String> tipiPluginGiaUtilizzati = null;
			
			// Preparo il menu
			confHelper.makeMenu();
			
			List<Parameter> lstParamSession = new ArrayList<Parameter>();

			Parameter parTipologia = null;
			if(fase!=null) {
				parTipologia = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FASE, fase);
				lstParamSession.add(parTipologia);
			}
			
			String labelHandler = confHelper.getLabelTipologiaFromFaseMessageHandler(fase,true);
			String servletListURL = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_HANDLERS_SERVIZIO_LIST;
			String messaggioValoriNonDisponibili = MessageFormat.format(ConfigurazioneCostanti.LABEL_HANDLER_PLUGIN_NON_DISPONIBILI_PER_LA_FASE, labelHandler);
			String messaggioHandlerServizioDuplicato = null;

			// setto la barra del titolo
			List<Parameter> lstParam = null;
			lstParam  = new ArrayList<Parameter>();
			
			if(lstParamSession.size() > 0) {
				lstParam.add(new Parameter(labelHandler, servletListURL, lstParamSession.toArray(new Parameter[lstParamSession.size()])));
			} else {
				lstParam.add(new Parameter(labelHandler, servletListURL));
			}
			lstParam.add(new Parameter(plugin.getLabel(), null));
			lstParam.add(0,new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			
			// Se tipo = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				if(first) {
					nomePlugin = plugin.getTipo();
					stato = handler.getStato().getValue();
				}
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				confHelper.addHandlerServizioToDati(dati, tipoOperazione, idHandlerS, nomePlugin, stato, ruoloPorta, idPortaS, serviceBinding, fase, tipiPluginGiaUtilizzati, messaggioValoriNonDisponibili); 
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALLARMI_FIRST_TIME);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_SERVIZIO, ForwardParams.CHANGE());
			}
				
			// Controlli sui campi immessi
			boolean isOk = confHelper.handlerServizioCheckData(tipoOperazione, handler, nomePlugin, stato, ruoloPorta, idPorta, fase, tipo, messaggioHandlerServizioDuplicato);
			
			if (!isOk) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				confHelper.addHandlerServizioToDati(dati, tipoOperazione, idHandlerS, nomePlugin, stato, ruoloPorta, idPortaS, serviceBinding, fase, tipiPluginGiaUtilizzati, messaggioValoriNonDisponibili); 
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALLARMI_FIRST_TIME);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_SERVIZIO, 
						ForwardParams.CHANGE());
			}
				
			handler.setStato(StatoFunzionalita.toEnumConstant(stato));
			
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
			
			for (ConfigurazioneHandler handlerToCheck : listaDaAggiornare) {
				if(handlerToCheck.getTipo().equals(handler.getTipo())) {
					confHelper.updateHandler(handlerToCheck, handler, tipoOperazione);
				}
			}
			
			// update sul db
			confCore.performUpdateOperation(userLogin, confHelper.smista(), oggettiDaAggiornare.toArray(new Object[oggettiDaAggiornare.size()]));
			
			// Preparo la lista
			int idLista = Liste.CONFIGURAZIONE_HANDLERS_SERVIZIO;
			
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<ConfigurazioneHandlerBean> lista = confCore.handlersServizioList(ricerca, fase); 
			
			confHelper.prepareHandlersServizioList(ricerca, lista, ruoloPorta, idPortaS, serviceBinding, fase);
			
			if(ruoloPorta == null) {
				pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SERVICE_HANDLERS_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			}
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_SERVIZIO, ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_HANDLERS_SERVIZIO, ForwardParams.CHANGE());
		}  
	}
}			
