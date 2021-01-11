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
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
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
 * ConfigurazionePolicyGestioneTokenDel
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePolicyGestioneTokenDel extends Action {

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
			for (int i = 0; i < idsToRemove.size(); i++) {

				long idGenericProperties = Long.parseLong(idsToRemove.get(i));
				GenericProperties policy = confCore.getGenericProperties(idGenericProperties);
				
				IDGenericProperties idGP = new IDGenericProperties();
				idGP.setNome(policy.getNome());
				idGP.setTipologia(policy.getTipologia());

				boolean gpInUso = confCore.isGenericPropertiesInUso(idGP,whereIsInUso,normalizeObjectIds);

				if (gpInUso) {
					inUsoMessage.append(DBOggettiInUsoUtils.toString(idGP, whereIsInUso, true, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE));
					inUsoMessage.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);

				} else {
					confCore.performDeleteOperation(userLogin, confHelper.smista(), policy); 
				}
				
				/*
				boolean delete = true;
				boolean addMsg = false;
				
				// controllo che la policy non sia utilizzata in alcuna PD o PA o connettori
				List<PortaApplicativa> listaPA= confCore.listaPorteApplicativeUtilizzateDaPolicyGestioneToken(policy.getNome());
				List<PortaDelegata> listaPD = confCore.listaPorteDelegateUtilizzateDaPolicyGestioneToken(policy.getNome());
				boolean usedInConnettore = confCore.isPolicyNegoziazioneTokenUsedInConnettore(policy.getNome());
				boolean usedInPA = listaPA != null && listaPA.size() > 0;
				boolean usedInPD = listaPD != null && listaPD.size() > 0;
				
				StringBuilder bf = new StringBuilder();
				if(usedInPA || usedInPD || usedInConnettore) {
					
					delete = false;
					addMsg = true;
					
					// NOTA: se utilizzato nel connettore, non e' usata in fruizioni o erogazioni, sono policy differenti
					
					bf.append("La policy '"+policy.getNome()+"' risulta utilizzata");
					
					if(usedInPA) {
						bf.append(" in ");
						bf.append(listaPA.size());
						if(listaPA.size()==1){
							bf.append(" Erogazione");
						}else{
							bf.append(" Erogazioni");
						}
						if(usedInPD) {
							bf.append(" e");
						}
					}
					if(usedInPD) {
						bf.append(" in ");
						bf.append(listaPD.size());
						if(listaPD.size()==1){
							bf.append(" Fruizione");
						}else{
							bf.append(" Fruizioni");
						}
					}
				}
				
				
//				if(listaPA != null && listaPA.size() > 0) {
//					bf.append("<br/>").append(listaPA.size());
//					if(listaPA.size()<2){
//						bf.append(" Erogazione ");
//					}else{
//						bf.append(" Porte Applicative: ");
//					}
//					for (int j = 0; j < listaPA.size(); j++) {
//						if(j>0){
//							bf.append(", ");
//						}
//						bf.append(listaPA.get(j).getNome());
//					}
//					
//					delete = false;
//					addMsg = true;
//				}
//				
//				if(listaPD != null && listaPD.size() > 0) {
//					bf.append("<br/>").append(listaPD.size());
//					if(listaPD.size()<2){
//						bf.append(" Porta Delegata: ");
//					}else{
//						bf.append(" Porte Delegate: ");
//					}
//					for (int j = 0; j < listaPD.size(); j++) {
//						if(j>0){
//							bf.append(", ");
//						}
//						bf.append(listaPD.get(j).getNome());
//					}
//					
//					delete = false;
//					addMsg = true;
//				}
				
				if(addMsg) {
					if(delMsg.length()>0){
						delMsg.append("<br/>");
					}
					delMsg.append("- "+bf.toString());
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
				confCore.performDeleteOperation(userLogin, confHelper.smista(), (Object[]) elemToRemove.toArray(new GenericProperties[1])); 
			}
			
			
			String msgCompletato = ConfigurazioneCostanti.MESSAGGIO_CONFERMA_ELIMINAZIONE_POLICY_GESTIONE_TOKEN_OK;
			
			if(msgErrore!=null && !"".equals(msgErrore)){
				if(elemToRemove.size()>0){
					msgCompletato = msgCompletato+"<br/><br/>"+msgErrore;
				}
				else{
					msgCompletato = msgErrore;
				}
			}
			
			if(msgErrore!=null && !"".equals(msgErrore))
				pd.setMessage(msgCompletato);
			else
				pd.setMessage(msgCompletato,Costanti.MESSAGE_TYPE_INFO);
				*/

			}// chiudo for

			if (inUsoMessage.length()>0) {
				pd.setMessage(inUsoMessage.toString());
			}
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_GESTIONE_POLICY_TOKEN;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<String> tipologie = new ArrayList<>();
			tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN);
			tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_RETRIEVE_POLICY_TOKEN);
			
			List<GenericProperties> lista = confCore.gestorePolicyTokenList(idLista, tipologie, ricerca);
			
			confHelper.prepareGestorePolicyTokenList(ricerca, lista, idLista); 

			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.DEL());
		}  
	}
}
