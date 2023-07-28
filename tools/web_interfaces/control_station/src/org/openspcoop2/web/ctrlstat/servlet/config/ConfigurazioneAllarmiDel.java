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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.allarmi.constants.TipoAllarme;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.monitor.engine.alarm.AlarmEngineConfig;
import org.openspcoop2.monitor.engine.alarm.utils.AllarmiUtils;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConfigurazioneAllarmiDel
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneAllarmiDel extends Action {

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

			String objToRemove =confHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			Long id = null;

			List<Allarme> allarmiToRemove = new ArrayList<>();
			StringBuilder inUsoMessage = new StringBuilder();
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				id = Long.parseLong(idsToRemove.get(i));
				
				Allarme allarme = confCore.getAllarmeSenzaPlugin(id);
				
				allarmiToRemove.add(allarme);
				
			}
			
			List<Allarme> allarmiRimossi = new ArrayList<Allarme>();
			ConfigurazioneUtilities.deleteAllarmi(allarmiToRemove, confHelper, confCore, userLogin, inUsoMessage, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE, allarmiRimossi);
			
			List<String> allarmiAttivi = new ArrayList<>();
			if(!allarmiRimossi.isEmpty()) {
			
				for (Allarme allarme : allarmiRimossi) {
										
					if(TipoAllarme.PASSIVO.equals(allarme.getTipoAllarme())){
						// NOTA: il tipo di allarme non è modificabile.
						ControlStationCore.getLog().debug("Allarme ["+allarme.getNome()+"] è passivo. Non viene effettuata alcuna rimozione su Allarmi.war");
						continue;
					}
					if(allarme.getEnabled()==0){
						// NOTA: il tipo di allarme non è modificabile.
						ControlStationCore.getLog().debug("Allarme ["+allarme.getNome()+"] è disabilitato. Non viene effettuata alcuna rimozione su Allarmi.war");
						continue;
					}
						
					allarmiAttivi.add(allarme.getNome());
					
				}
				 
			}
			
			/* ******** INVIO NOTIFICHE *************** */
			String pdMessage = null;
			try {
				if(!allarmiAttivi.isEmpty()) {
					
					AlarmEngineConfig alarmEngineConfig = confCore.getAllarmiConfig();
					
					AllarmiUtils.stopActiveThreads(allarmiAttivi, ControlStationCore.getLog(), alarmEngineConfig);
				}
			} catch(Exception e) {
				String errorMsg = MessageFormat.format(ConfigurazioneCostanti.MESSAGGIO_ERRORE_ALLARME_ELIMINATO_NOTIFICA_FALLITA,e.getMessage());
				ControlStationCore.getLog().error(errorMsg, e);
				pd.setMessage(errorMsg);
				pdMessage = errorMsg;
			}
			
			

			Object[] oggetti = allarmiRimossi.toArray(new Allarme[allarmiRimossi.size()]); 
			confCore.performDeleteOperation(userLogin, confHelper.smista(), oggetti);
			// Preparo il menu
			confHelper.makeMenu();

			if(inUsoMessage.length()>0) {
				if(pdMessage!=null) {
					pd.setMessage(pdMessage+
							org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
							org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
							inUsoMessage.toString());
				}
				else {
					pd.setMessage(inUsoMessage.toString());
				}
			}
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.CONFIGURAZIONE_ALLARMI;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<ConfigurazioneAllarmeBean> lista = confCore.allarmiList(ricerca, ruoloPorta, nomePorta); 
			
			confHelper.prepareAllarmiList(ricerca, lista, ruoloPorta, nomePorta, serviceBinding);
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ALLARMI,
					ForwardParams.DEL());
		} catch (Throwable e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ALLARMI, ForwardParams.DEL());
		}
	}
}
