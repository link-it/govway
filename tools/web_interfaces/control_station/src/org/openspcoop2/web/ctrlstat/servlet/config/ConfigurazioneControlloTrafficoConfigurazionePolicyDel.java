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
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;


/**     
 * ConfigurazioneControlloTrafficoConfigurazionePolicyDel
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneControlloTrafficoConfigurazionePolicyDel extends Action {

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
			StringBuilder inUsoMessage = new StringBuilder();
			HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
			boolean normalizeObjectIds = !confHelper.isModalitaCompleta();
			List<ConfigurazionePolicy> elemToRemove = new ArrayList<ConfigurazionePolicy>();
			boolean delBuiltIn = false;
			boolean delUtente = false;
			for (int i = 0; i < idsToRemove.size(); i++) {
				boolean delete = true;
				long idPolicy = Long.parseLong(idsToRemove.get(i));
				ConfigurazionePolicy policy = confCore.getConfigurazionePolicy(idPolicy);
				IdPolicy idPolicyObject = new IdPolicy();
				idPolicyObject.setNome(policy.getIdPolicy());
				
				if(policy.isBuiltIn()) {
					
					inUsoMessage.append("- ");
					inUsoMessage.append(policy.getIdPolicy());
					inUsoMessage.append(" non eliminabile");
					inUsoMessage.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
					
					delete = false;
					
					delBuiltIn = true;
				}
				else {
					
					boolean policyInUso = confCore.isRateLimitingPolicyInUso(idPolicyObject,whereIsInUso,normalizeObjectIds);

					if (policyInUso) {
						inUsoMessage.append(DBOggettiInUsoUtils.toString(idPolicyObject, whereIsInUso, true, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE));
						inUsoMessage.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						
						delete = false;
						
						// Con il dettaglio di dove viene utilizzato, non serve piu' questo messaggio
						//delUtente = true;
					}
					
//					long configurazioneUtilizzata = confCore.countInUseAttivazioni(policy.getIdPolicy());
//					
//					if(configurazioneUtilizzata >0){
//						if(delMsg.length()>0){
//							delMsg.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
//						}
//						delMsg.append("- ");
//						delMsg.append(policy.getIdPolicy());
//						delMsg.append(" viene utilizzata in ");
//						delMsg.append(configurazioneUtilizzata);
//						if(configurazioneUtilizzata >1)
//							delMsg.append(" istanze di ");
//						else
//							delMsg.append(" istanza di ");
//						delMsg.append(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING);
//						delete = false;
//						
//						delUtente = true;
//					}
				}

				if(delete) {
					// aggiungo elemento alla lista di quelli da cancellare
					elemToRemove.add(policy);
				}
			}
			
			if(inUsoMessage.length() > 0){
				inUsoMessage.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
				if(delBuiltIn) {
					inUsoMessage.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE).append("!Attenzione! Le policy '"+CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_BUILT_IN+"' non possono essere eliminate.");
				}
				if(delUtente) {
					if(delBuiltIn) {
						inUsoMessage.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
					}
					inUsoMessage.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE).append("Per poter eliminare una policy '"+CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_UTENTE+"' dal registro è necessario prima eliminare tutte le sue istanze esistenti in "+ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING);
				}
			}
			
			
			if(elemToRemove .size() > 0) {
//			 	eseguo delete
				confCore.performDeleteOperation(userLogin, confHelper.smista(), (Object[]) elemToRemove.toArray(new ConfigurazionePolicy[1])); 
			}
			
			// alcuni elementi non sono stati eliminati
			if(inUsoMessage.length() > 0) {
				pd.setMessage(inUsoMessage.toString()); 
			} else {
				pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO_MODIFICATA_CON_SUCCESSO_SENZA_RIAVVIO_RICHIESTO,MessageType.INFO); 
			}

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<ConfigurazionePolicy> lista = confCore.configurazionePolicyList(ricerca);

			confHelper.prepareConfigurazionePolicyList(ricerca, lista, idLista);

			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY, ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY, ForwardParams.DEL());
		}  
	}
}
