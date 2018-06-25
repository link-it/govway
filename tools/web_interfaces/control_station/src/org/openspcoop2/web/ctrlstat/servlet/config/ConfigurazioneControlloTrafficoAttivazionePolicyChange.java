/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;


/**     
 * ConfigurazioneControlloTrafficoAttivazionePolicyChange
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneControlloTrafficoAttivazionePolicyChange extends Action {

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
			StringBuilder sbParsingError = new StringBuilder();
			
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
			org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico = confCore.getConfigurazioneControlloTraffico();
			
			// uso nome porta per capire se sono entrato per la prima volta nella schermata
			boolean first = confHelper.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
			
			String id = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID); 
			long idPolicyL = Long.parseLong(id);
			AttivazionePolicy policy = confCore.getAttivazionePolicy(idPolicyL);
			
			// nome della Policy
			String idPolicy = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID);
			if(idPolicy!=null && !"".equals(idPolicy) && !"-".equals(idPolicy)){
				policy.setIdPolicy(idPolicy);
			}
			else{
				if(!first){
					policy.setIdPolicy(null);
				}
			}	
			
			InfoPolicy infoPolicy = null;
			if(policy.getIdPolicy()!=null){
				infoPolicy = confCore.getInfoPolicy(policy.getIdPolicy());
				
				if(TipoOperazione.ADD.equals(tipoOperazione) && infoPolicy!=null){
					int counter = confCore.getFreeCounterForGlobalPolicy(infoPolicy.getIdPolicy());
					policy.setIdActivePolicy(infoPolicy.getIdPolicy()+":"+counter);
				}
			}
			
			// Dati Attivazione
			String errorAttivazione = confHelper.readDatiAttivazionePolicyFromHttpParameters(policy, first, tipoOperazione, infoPolicy);
			if(errorAttivazione!=null){
				confHelper.addParsingError(sbParsingError,errorAttivazione); 
			}
			
			// Preparo il menu
			confHelper.makeMenu();
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_LINK, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_LIST));
			lstParam.add(new Parameter(policy.getIdPolicy(),null));
			
			List<InfoPolicy> infoPolicies = confCore.infoPolicyList();
			
			// Se tipo = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				if(infoPolicies==null || infoPolicies.size()<=0){
					pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_POSSIBILI_COMPLETATE_GLOBALI, MessageType.INFO);
					pd.disableEditMode();
					pd.setDati(dati);
			
					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY,	ForwardParams.CHANGE());
				}
				
				// Attivazione
				confHelper.addAttivazionePolicyToDati(dati, tipoOperazione, policy,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY, infoPolicies);
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, 
						ForwardParams.CHANGE());
			}
			
			// Controlli sui campi immessi
			boolean isOk = confHelper.attivazionePolicyCheckData(tipoOperazione, configurazioneControlloTraffico, policy,infoPolicy);
			if (!isOk) {
				
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// Attivazione
				confHelper.addAttivazionePolicyToDati(dati, tipoOperazione, policy,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY, infoPolicies);
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, 
						ForwardParams.CHANGE());
			}

			// insert sul db
			
			confCore.performUpdateOperation(userLogin, confHelper.smista(), policy);
			
			String msgCompletato = confHelper.eseguiResetJmx(TipoOperazione.CHANGE);
			if(msgCompletato!=null && !"".equals(msgCompletato)){
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
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, ForwardParams.CHANGE());
		}  
	}
}
