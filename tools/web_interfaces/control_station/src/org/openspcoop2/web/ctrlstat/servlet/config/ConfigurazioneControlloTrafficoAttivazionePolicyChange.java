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
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.utils.PolicyUtilities;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
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
			PorteDelegateCore pdCore = new PorteDelegateCore(confCore);
			
			org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico = confCore.getConfigurazioneControlloTraffico();
			
			// uso nome porta per capire se sono entrato per la prima volta nella schermata
			boolean first = confHelper.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
			
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
			
			String id = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID); 
			long idPolicyL = Long.parseLong(id);
			AttivazionePolicy policy = confCore.getAttivazionePolicy(idPolicyL);
			
			// Fix retrocompatibilita dove il nome non era obbligatorio.
			policy.setAlias(PolicyUtilities.getNomeActivePolicy(policy.getAlias(), policy.getIdActivePolicy()));
			
			// nome della Policy
			String idPolicy = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID);
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
			List<Parameter> lstParamPorta = null;
			if(ruoloPorta!=null) {
				lstParamPorta = confHelper.getTitleListAttivazionePolicy(ruoloPorta, nomePorta, serviceBinding, 
						PolicyUtilities.getNomeActivePolicy(policy.getAlias(), policy.getIdPolicy())); 
			}
			
			List<Parameter> lstParam = null;
			if(lstParamPorta!=null) {
				lstParam = lstParamPorta;
			}
			else {
				lstParam = new ArrayList<Parameter>();
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_LIST));
				lstParam.add(new Parameter(PolicyUtilities.getNomeActivePolicy(policy.getAlias(), policy.getIdPolicy()),null));
			}
			
			//List<InfoPolicy> infoPolicies = confCore.infoPolicyList();
			List<InfoPolicy> infoPolicies = new ArrayList<>();
			infoPolicies.add(infoPolicy);
			
			// Se tipo = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
								
				// Attivazione
				confHelper.addAttivazionePolicyToDati(dati, tipoOperazione, policy,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY, infoPolicies, ruoloPorta, nomePorta, serviceBinding, null);
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, 
						ForwardParams.CHANGE());
			}
			
			// Controlli sui campi immessi
			String _modalita = infoPolicy.isBuiltIn() ? ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_BUILT_IN :
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CUSTOM;
			boolean isOk = confHelper.attivazionePolicyCheckData(tipoOperazione, configurazioneControlloTraffico, 
					policy,infoPolicy, ruoloPorta, nomePorta, serviceBinding, _modalita);
			if (!isOk) {
				
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// Attivazione
				confHelper.addAttivazionePolicyToDati(dati, tipoOperazione, policy,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY, infoPolicies, ruoloPorta, nomePorta, serviceBinding, null);
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, 
						ForwardParams.CHANGE());
			}

			// insert sul db
			
			if(RuoloPolicy.DELEGATA.equals(ruoloPorta)) {
				String tipoSoggettoProprietario = null;
				String nomeSoggettoProprietario = null;
				if(RuoloPolicy.DELEGATA.equals(ruoloPorta)) {
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(nomePorta);
					PortaDelegata porta = pdCore.getPortaDelegata(idPD);
					// il tipo e nome serve per l'applicativo fruitore
					tipoSoggettoProprietario = porta.getTipoSoggettoProprietario();
					nomeSoggettoProprietario = porta.getNomeSoggettoProprietario();
				}
				policy.getFiltro().setTipoFruitore(tipoSoggettoProprietario);
				policy.getFiltro().setNomeFruitore(nomeSoggettoProprietario);
			}
			
			confCore.performUpdateOperation(userLogin, confHelper.smista(), policy);
			
			String msgCompletato = confHelper.eseguiResetJmx(TipoOperazione.CHANGE, ruoloPorta, nomePorta);
			if(msgCompletato!=null && !"".equals(msgCompletato)){
				pd.setMessage(msgCompletato,Costanti.MESSAGE_TYPE_INFO);
			}
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY;
			
			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<TipoRisorsaPolicyAttiva> listaTipoRisorsa = 
					confHelper.gestisciCriteriFiltroRisorsaPolicy(ricerca, ruoloPorta, nomePorta);
			
			List<AttivazionePolicy> lista = confCore.attivazionePolicyList(ricerca, ruoloPorta, nomePorta);
			
			confHelper.prepareAttivazionePolicyList(ricerca, lista, listaTipoRisorsa,
					idLista, ruoloPorta, nomePorta, serviceBinding); 
			
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
