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

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;


/**     
 * ConfigurazioneControlloTrafficoAttivazionePolicyDel
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneControlloTrafficoAttivazionePolicyDel extends Action {

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

			// Preparo il menu
			confHelper.makeMenu();
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			String ruoloPortaParam = confHelper.getParametroRuoloPolicy(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_RUOLO_PORTA);
			RuoloPolicy ruoloPorta = null;
			if(ruoloPortaParam!=null) {
				ruoloPorta = RuoloPolicy.toEnumConstant(ruoloPortaParam);
			}
			String nomePorta = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_NOME_PORTA);
			ServiceBinding serviceBinding = null;
			String serviceBindingParam = confHelper.getParametroServiceBinding(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_SERVICE_BINDING);
			if(serviceBindingParam!=null && !"".equals(serviceBindingParam)) {
				serviceBinding = ServiceBinding.valueOf(serviceBindingParam);
			}
			
			String objToRemove =confHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 

			// Elimino i filtri dal db
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);

			List<AttivazionePolicy> policyDaEliminare = new ArrayList<AttivazionePolicy>();
			StringBuilder inUsoMessage = new StringBuilder();
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				long idPolicy = Long.parseLong(idsToRemove.get(i));
				AttivazionePolicy policy = confCore.getAttivazionePolicy(idPolicy); 
				
				policyDaEliminare.add(policy);
				
			}
				
			List<AttivazionePolicy> policyRimosse = new ArrayList<AttivazionePolicy>();
			ConfigurazioneUtilities.deleteAttivazionePolicy(policyDaEliminare, confHelper, confCore, userLogin, inUsoMessage, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE, policyRimosse);
			
			String msgCompletato = confHelper.eseguiResetJmx(TipoOperazione.DEL, ruoloPorta, nomePorta);
			
			if(msgCompletato!=null && !"".equals(msgCompletato)){
				if(inUsoMessage.length()>0){
					if(policyRimosse.size()>0){
						msgCompletato = msgCompletato+"<br/><br/>"+inUsoMessage.toString();
					}
					else{
						msgCompletato = inUsoMessage.toString();
					}
				}
			}
			else{
				msgCompletato = inUsoMessage.toString();
			}
			if(msgCompletato!=null && !"".equals(msgCompletato)){
				if(inUsoMessage.length()>0)
					pd.setMessage(msgCompletato);
				else
					pd.setMessage(msgCompletato,Costanti.MESSAGE_TYPE_INFO);
			}
			

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY;

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
			return ServletUtils.getStrutsForward (mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, ForwardParams.DEL());
		}  
	}
}
