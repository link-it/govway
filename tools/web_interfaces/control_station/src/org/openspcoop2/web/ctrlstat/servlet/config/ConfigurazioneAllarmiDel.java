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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.allarmi.constants.TipoAllarme;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.monitor.engine.alarm.utils.AllarmiUtils;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
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

		// Salvo il vecchio PageData
		// PageData pdold = (PageData) session.getAttribute("PageData");

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

			List<Object> allarmiToRemove = new ArrayList<>();
			
			List<String> urls = new ArrayList<String>();
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				id = Long.parseLong(idsToRemove.get(i));
				
				Allarme allarmeToRemove = new Allarme();
				allarmeToRemove.setId(id);
				
				ConfigurazioneAllarmeBean allarme = confCore.getAllarme(id);
				
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
					
					String prefixUrl = confCore.getAllarmiConfig().getAllarmiActiveServiceUrl();
					if(prefixUrl.endsWith("/")==false){
						prefixUrl = prefixUrl + "/";
					}
					prefixUrl = prefixUrl + allarme.getNome() + "?";
					urls.add(prefixUrl + confCore.getAllarmiConfig().getAllarmiActiveServiceUrl_SuffixStopAlarm());

					allarmiToRemove.add(allarmeToRemove);
			}
			
			/* ******** INVIO NOTIFICHE *************** */
			try {
				AllarmiUtils.sendToAllarmi(urls, ControlStationCore.getLog());
			} catch(Exception e) {
				pd.setMessage(MessageFormat.format(ConfigurazioneCostanti.MESSAGGIO_ERRORE_ALLARME_ELIMINATO_NOTIFICA_FALLITA,e.getMessage()));
			}
			
			

			Object[] oggetti = allarmiToRemove.toArray(new Plugin[allarmiToRemove.size()]); 
			confCore.performDeleteOperation(userLogin, confHelper.smista(), oggetti);
			// Preparo il menu
			confHelper.makeMenu();

			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_ALLARMI;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<ConfigurazioneAllarmeBean> lista = confCore.allarmiList(ricerca, ruoloPorta, nomePorta); 
			
			confHelper.prepareAllarmiList(ricerca, lista, ruoloPorta, nomePorta, serviceBinding);
						
			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA_SISTEMA_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ALLARMI,
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ALLARMI, ForwardParams.DEL());
		}
	}
}
