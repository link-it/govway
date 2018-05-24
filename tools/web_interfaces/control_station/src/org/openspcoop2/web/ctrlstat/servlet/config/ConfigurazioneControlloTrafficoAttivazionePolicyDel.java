/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;


/***
 * 
 * @author pintori
 *
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

			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Preparo il menu
			confHelper.makeMenu();

			String objToRemove =confHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 

			// Elimino i filtri dal db
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);

			StringBuilder delMsg = new StringBuilder();
			List<AttivazionePolicy> elemToRemove = new ArrayList<AttivazionePolicy>();
			for (int i = 0; i < idsToRemove.size(); i++) {

				long idPolicy = Long.parseLong(idsToRemove.get(i));
				AttivazionePolicy policy = confCore.getAttivazionePolicy(idPolicy); 
				
				boolean delete = true;
				if(confHelper.isAllarmiModuleEnabled()){
					
					// throw new NotImplementedException("Da implementare quando verranno aggiunti gli allarmi."); 
					List<String> allarmiUtilizzanoPolicy = null;
//					List<String> allarmiUtilizzanoPolicy = confHelper.getAllIdAllarmiUseActivePolicy(policy.getIdActivePolicy());
					allarmiUtilizzanoPolicy = new ArrayList<String>();
					allarmiUtilizzanoPolicy.add("Allarme1");
					
					if(allarmiUtilizzanoPolicy!=null && allarmiUtilizzanoPolicy.size()>0){
						StringBuffer bf = new StringBuffer();
						bf.append("La policy '"+policy.getIdActivePolicy()+"' risulta utilizzata da ");
						bf.append(allarmiUtilizzanoPolicy.size());
						if(allarmiUtilizzanoPolicy.size()<2){
							bf.append(" allarme: ");
						}else{
							bf.append(" allarmi: ");
						}
						for (int j = 0; j < allarmiUtilizzanoPolicy.size(); j++) {
							if(j>0){
								bf.append(", ");
							}
							bf.append(allarmiUtilizzanoPolicy.get(j));
						}
						
						if(delMsg.length()>0){
							delMsg.append("<br/>");
						}
						delMsg.append("- "+bf.toString());
						delete = false;
					}
				}

				if(delete) {
					// aggiungo elemento alla lista di quelli da cancellare
					elemToRemove.add(policy);
				}
			}
			
			
			String msgErrore = "";
			if(delMsg.length()>0){
				if(elemToRemove.size()>0){
					msgErrore = "Non è stato possibile completare l'eliminazione di tutti gli elementi selezionati:<br/>"+delMsg.toString();
				}
				else{
					msgErrore = "Non è stato possibile eliminare gli elementi selezionati:<br/>"+delMsg.toString();
				}
			}
			
			if(elemToRemove .size() > 0) {
//			 	eseguo delete
				confCore.performDeleteOperation(userLogin, confHelper.smista(), (Object[]) elemToRemove.toArray(new AttivazionePolicy[1])); 
			}
			
			String msgCompletato = confHelper.eseguiResetJmx(TipoOperazione.DEL);
			
			if(msgCompletato!=null && !"".equals(msgCompletato)){
				if(msgErrore!=null && !"".equals(msgErrore)){
					if(elemToRemove.size()>0){
						msgCompletato = msgCompletato+"<br/><br/>"+msgErrore;
					}
					else{
						msgCompletato = msgErrore;
					}
				}
			}
			else{
				msgCompletato = msgErrore;
			}
			if(msgCompletato!=null && !"".equals(msgCompletato)){
				if(msgErrore!=null && !"".equals(msgErrore))
					pd.setMessage(msgCompletato);
				else
					pd.setMessage(msgCompletato,Costanti.MESSAGE_TYPE_INFO);
			}
			

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<AttivazionePolicy> lista = confCore.attivazionePolicyList(ricerca);

			confHelper.prepareAttivazionePolicyList(ricerca, lista, idLista);

			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, ForwardParams.DEL());
		}  
	}
}
