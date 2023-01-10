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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**     
 * ConfigurazioneControlloTrafficoAttivazionePolicyList
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneControlloTrafficoAttivazionePolicyList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		//	String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);

			String ruoloPortaParam = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_RUOLO_PORTA);
			RuoloPolicy ruoloPorta = null;
			if(ruoloPortaParam!=null) {
				ruoloPorta = RuoloPolicy.toEnumConstant(ruoloPortaParam);
			}
			String nomePorta = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_NOME_PORTA);
			ServiceBinding serviceBinding = null;
			String serviceBindingParam = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_SERVICE_BINDING);
			if(serviceBindingParam!=null && !"".equals(serviceBindingParam)) {
				serviceBinding = ServiceBinding.valueOf(serviceBindingParam);
			}
			
			String idTab = confHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!confHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			String cambiaPosizione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POSIZIONE);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
			int idLista = Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY;
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			if(StringUtils.isNotEmpty(cambiaPosizione)) {
				
				String idPolicyS = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID);
				long idPolicyLong = Long.parseLong(idPolicyS);
				String risorsaPolicy = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA);
				
				ConsoleSearch ricercaPerRisorsa = new ConsoleSearch(true);
				ricercaPerRisorsa.addFilter(idLista, Filtri.FILTRO_TIPO_RISORSA_POLICY, risorsaPolicy);
				List<AttivazionePolicy> lista = confCore.attivazionePolicyList(ricercaPerRisorsa, ruoloPorta, nomePorta);

				if(lista != null && !lista.isEmpty()) {
					AttivazionePolicy regolaToMove = null;
					int index = 0;
					for (int i = 0; i < lista.size(); i++) {
						if(lista.get(i).getId().longValue() == idPolicyLong) {
							regolaToMove = lista.get(i);
							index = i;
							break;
						}
					}
					
					AttivazionePolicy regolaToSwitch = null; // se arrivo qua dovrebbe esistere sempre, e' la grafica che me lo assicura
					if(cambiaPosizione.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SU)) {
						if(index>0) {
							regolaToSwitch = lista.get((index-1));
						}
					}
					else {
						if(index<(lista.size()-1)) {
							regolaToSwitch = lista.get((index+1));
						}
					}
					
					if(regolaToMove!=null && regolaToSwitch!=null) {
						int posizioneAttuale = regolaToMove.getPosizione();
						regolaToMove.setPosizione(regolaToSwitch.getPosizione());
						regolaToSwitch.setPosizione(posizioneAttuale);
						
						List<Object> list = new ArrayList<>();
						list.add(regolaToMove);
						list.add(regolaToSwitch);
						confCore.performUpdateOperation(userLogin, confHelper.smista(), list.toArray(new Object[1]));
//						if(CostantiControlStation.VISUALIZZA_MESSAGGIO_CONFERMA_SPOSTAMENTO_POLICY) {
//							pd.setMessage(CostantiControlStation.MESSAGGIO_CONFERMA_REGOLA_POLICY_SPOSTATA_CORRETTAMENTE, MessageType.INFO);
//						}
						
						String msgCompletato = confHelper.eseguiResetJmx(TipoOperazione.CHANGE, ruoloPorta, nomePorta);
						if(msgCompletato!=null && !"".equals(msgCompletato)){
							pd.setMessage(msgCompletato,Costanti.MESSAGE_TYPE_INFO);
						}
					}
						
				}
			}
			
			
			// Preparo il menu
			confHelper.makeMenu();

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);
			
			List<TipoRisorsaPolicyAttiva> listaTipoRisorsa = 
					confHelper.gestisciCriteriFiltroRisorsaPolicy(ricerca, ruoloPorta, nomePorta);
			
			List<AttivazionePolicy> lista = confCore.attivazionePolicyList(ricerca, ruoloPorta, nomePorta);
			
			confHelper.prepareAttivazionePolicyList(ricerca, lista, listaTipoRisorsa, 
					idLista, ruoloPorta, nomePorta, serviceBinding); 
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY,
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, ForwardParams.LIST());
		}  
	}
	
}

