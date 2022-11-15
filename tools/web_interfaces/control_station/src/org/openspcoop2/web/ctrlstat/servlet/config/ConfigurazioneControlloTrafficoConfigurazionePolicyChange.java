/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;


/**     
 * ConfigurazioneControlloTrafficoConfigurazionePolicyChange
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneControlloTrafficoConfigurazionePolicyChange extends Action {

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
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
			org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico = confCore.getConfigurazioneControlloTraffico();
			
			String id = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID);
			long idPolicy = Long.parseLong(id);
			ConfigurazionePolicy policy = confCore.getConfigurazionePolicy(idPolicy);
			String oldTipoRisorsa = policy.getRisorsa();
			
			boolean editMode = true;
			boolean editOnlyValueMode = false;
			long countPolicyAttiveConQualsiasiStato = 0;
			
			// uso nome porta per capire se sono entrato per la prima volta nella schermata
			boolean first = confHelper.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
			
			StringBuilder sbParsingError = new StringBuilder();
			// Dati Generali
			String errorDatiGenerali = confHelper.readDatiGeneraliPolicyFromHttpParameters(policy, first);
			if(errorDatiGenerali!=null){
				confHelper.addParsingError(sbParsingError,errorDatiGenerali);
			}
			
			// Valori di Soglia
			String errorValoriSoglia = confHelper.readValoriSogliaPolicyFromHttpParameters(policy, first);
			if(errorValoriSoglia!=null){
				confHelper.addParsingError(sbParsingError,errorValoriSoglia);
			}
			
			if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA.equals(confHelper.getPostBackElementName())) {
				if(TipoRisorsa.DIMENSIONE_MASSIMA_MESSAGGIO.equals(policy.getRisorsa())) {
					//if(policy.getValore()==null) {
					policy.setValore(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_DIMENSIONE_MASSIMA);
					//}
					//if(policy.getValore2()==null) {
					policy.setValore2(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_DIMENSIONE_MASSIMA);
					//}
				}
			}
			
			List<AttivazionePolicy> listPolicyAttiveConStatoDisabilitato = null;
			boolean updateValueInSeguitoModificaSogliaPolicy = false;
			// Read Informazioni riguardante la modifica anche di attivazioni di policy collegate
			String value = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_MODIFICATO_CON_ISTANZE_ATTIVE_RICHIESTA_MODIFICA);
			//System.out.println("CHECKKKKKK RICHIESTA ["+value+"]");
			if(value!=null && ServletUtils.isCheckBoxEnabled(value)) {
				updateValueInSeguitoModificaSogliaPolicy = true;
			}
			
			// Nome
			
			String nomePolicy = null;
			String oldNomeSuggeritoPolicy = null;
			if(first){
				nomePolicy = policy.getIdPolicy();
				String nomePolicySuggerita = confHelper.getNomeSuggerito(policy);
				if(nomePolicy==null || "".equals(nomePolicy)){
					nomePolicy = nomePolicySuggerita;
					oldNomeSuggeritoPolicy = nomePolicy;
				}
				else{
					if(nomePolicy.equals(nomePolicySuggerita)){
						oldNomeSuggeritoPolicy = nomePolicy;
					}
				}
			}else{
				nomePolicy = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NOME);
				
				oldNomeSuggeritoPolicy = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_OLD_NOME_SUGGERITO);
//				if(oldNomeSuggeritoPolicy!=null && !"".equals(oldNomeSuggeritoPolicy)){
//					if(cc.getOldNomeSuggeritoPolicy()==null){
//						cc.setOldNomeSuggeritoPolicy(oldNomeSuggeritoPolicy);
//					}
//				}
				
				if(nomePolicy!=null && !"".equals(nomePolicy)){
					if(nomePolicy.equals(oldNomeSuggeritoPolicy)){
						nomePolicy = confHelper.getNomeSuggerito(policy); // aggiorno suggerimento
						oldNomeSuggeritoPolicy = nomePolicy;
					}
				}
			}

			// Nome è l'id
			
			String oldIdPolicyS = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_OLD_ID);
			if(oldIdPolicyS!=null && !"".equals(oldIdPolicyS)){
				if(policy.getOldIdPolicy() ==null){
					IdPolicy oldIdPolicy = new IdPolicy();
					oldIdPolicy.setNome(oldIdPolicyS);
					policy.setOldIdPolicy(oldIdPolicy ); 
				}
			}
			if(policy.getIdPolicy()!=null){
				if(policy.getIdPolicy().equals(oldIdPolicyS)==false){
					oldIdPolicyS = policy.getIdPolicy();
					IdPolicy oldIdPolicy = new IdPolicy();
					oldIdPolicy.setNome(oldIdPolicyS);
					policy.setOldIdPolicy(oldIdPolicy ); 
				}
			}
			
			policy.setIdPolicy(nomePolicy);
			value = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_MODIFICATO_CON_ISTANZE_ATTIVE_MODIFICA_EFFETTIVA);
			//System.out.println("CHECKKKKKK UPDATE ["+value+"]");
			if(value!=null && ServletUtils.isCheckBoxEnabled(value)) {
				if(oldIdPolicyS !=null){
					listPolicyAttiveConStatoDisabilitato = confCore.findInUseAttivazioni(oldIdPolicyS, true); // Quelle disabilitate non hanno bisogno di essere aggiornate
				} else
					listPolicyAttiveConStatoDisabilitato = confCore.findInUseAttivazioni(id, true); // Quelle disabilitate non hanno bisogno di essere aggiornate
			}
			
			// Applicabilita'
			String errorApplicabilita = confHelper.readApplicabilitaPolicyFromHttpParameters(policy, first);
			if(errorApplicabilita!=null){
				confHelper.addParsingError(sbParsingError,errorApplicabilita);
			}
			
			// Setto nome/descrizione costruito attraverso gli altri parametri
			// Descrizione
			
			String descrizionePolicy = null;
			String oldDescrizioneSuggeritaPolicy = null;
			if(first){
				descrizionePolicy = policy.getDescrizione();
				String descrizionePolicySuggerita = confHelper.getDescrizioneSuggerita(policy);
				if(descrizionePolicy==null || "".equals(descrizionePolicy)){
					descrizionePolicy = descrizionePolicySuggerita;
					oldDescrizioneSuggeritaPolicy = descrizionePolicy;
				}
				else{
					if(descrizionePolicy.equals(descrizionePolicySuggerita)){
						oldDescrizioneSuggeritaPolicy = descrizionePolicy;
					}
				}
			}else{
				descrizionePolicy = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_DESCRIZIONE);
				oldDescrizioneSuggeritaPolicy = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_OLD_DESCRIZIONE_SUGGERITA);
//				if(oldDescrizioneSuggeritaPolicy!=null && !"".equals(oldDescrizioneSuggeritaPolicy)){
//					if(cc.getOldDescrizioneSuggeritaPolicy()==null){
//						cc.setOldDescrizioneSuggeritaPolicy(oldDescrizioneSuggeritaPolicy);
//					}
//				}
				
				if(descrizionePolicy!=null && !"".equals(descrizionePolicy)){
					if(descrizionePolicy.equals(oldDescrizioneSuggeritaPolicy)){
						descrizionePolicy = confHelper.getDescrizioneSuggerita(policy); // aggiorno suggerimento
						oldDescrizioneSuggeritaPolicy = descrizionePolicy;
					}
				}
			}
			
			policy.setDescrizione(descrizionePolicy);
			
			if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA.equals(confHelper.getPostBackElementName())) {
				if(TipoRisorsa.DIMENSIONE_MASSIMA_MESSAGGIO.equals(oldTipoRisorsa)) {
					policy.setIntervalloOsservazione(null);
				}
			}
			
			if(policy.isBuiltIn()) {
				editMode = false;
				editOnlyValueMode = true;
			}
			else {
				long count = confCore.countInUseAttivazioni(oldIdPolicyS, false);
				if(count>0){
					countPolicyAttiveConQualsiasiStato = count;
					editMode = false;
					
					long countAttivazioniNonDisabilite = confCore.countInUseAttivazioni(oldIdPolicyS, true);
					if(countAttivazioniNonDisabilite==0){
						editOnlyValueMode = true;
					}
					else {
						editOnlyValueMode = updateValueInSeguitoModificaSogliaPolicy;
					}
				}
			}
			
			
			// Preparo il menu
			confHelper.makeMenu();
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRO_POLICY, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY_LIST));
			lstParam.add(new Parameter(oldIdPolicyS,null));
			
			
			// Se tipo = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// Dati Generali
				confHelper.addConfigurazionePolicyToDati(dati, tipoOperazione, policy,	editMode, countPolicyAttiveConQualsiasiStato,oldNomeSuggeritoPolicy,oldDescrizioneSuggeritaPolicy,oldIdPolicyS);
				
				// Valori di Soglia
				confHelper.addConfigurazionePolicyValoriSoglia(dati, tipoOperazione, policy,	editMode, editOnlyValueMode);
				
				// Applicabilita'
				confHelper.addConfigurazionePolicyApplicabilitaToDati(dati, tipoOperazione, policy,	configurazioneControlloTraffico.getControlloTraffico(), editMode);
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
				
				if(!editMode && !editOnlyValueMode) {
					pd.disableEditMode();
				}
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY, 
						ForwardParams.CHANGE());
			}
			
			// Controlli sui campi immessi
			boolean isOk = confHelper.configurazionePolicyCheckData(sbParsingError, tipoOperazione, configurazioneControlloTraffico, policy, oldNomeSuggeritoPolicy,
					oldDescrizioneSuggeritaPolicy, oldIdPolicyS, listPolicyAttiveConStatoDisabilitato, updateValueInSeguitoModificaSogliaPolicy);
			if (!isOk) {
				
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// Dati Generali
				confHelper.addConfigurazionePolicyToDati(dati, tipoOperazione, policy, editMode, countPolicyAttiveConQualsiasiStato,oldNomeSuggeritoPolicy,oldDescrizioneSuggeritaPolicy,oldIdPolicyS); 
				
				// Valori di Soglia
				confHelper.addConfigurazionePolicyValoriSoglia(dati, tipoOperazione, policy, editMode, editOnlyValueMode);
				
				// Applicabilita'
				confHelper.addConfigurazionePolicyApplicabilitaToDati(dati, tipoOperazione, policy,	configurazioneControlloTraffico.getControlloTraffico(), editMode);
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
				
				if(!editMode && !editOnlyValueMode) {
					pd.disableEditMode();
				}
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY, 
						ForwardParams.CHANGE());
			}

			// insert sul db
			
			confCore.performUpdateOperation(userLogin, confHelper.smista(), policy);
			
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
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY, ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY, ForwardParams.CHANGE());
		}  
	}
}
