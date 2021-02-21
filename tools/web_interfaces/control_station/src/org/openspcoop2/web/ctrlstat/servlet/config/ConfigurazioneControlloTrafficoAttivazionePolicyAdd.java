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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.utils.ControlloTrafficoDriverUtils;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;


/**     
 * ConfigurazioneControlloTrafficoAttivazionePolicyAdd
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneControlloTrafficoAttivazionePolicyAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	
		
		TipoOperazione tipoOperazione = TipoOperazione.ADD;

		try {
			StringBuilder sbParsingError = new StringBuilder();
			
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			SoggettiCore soggettiCore = new SoggettiCore(confCore);
			PorteDelegateCore pdCore = new PorteDelegateCore(confCore);
			PorteApplicativeCore paCore = new PorteApplicativeCore(confCore);
			
			org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico = confCore.getConfigurazioneControlloTraffico();
			
			AttivazionePolicy policy = new AttivazionePolicy();
			policy.setFiltro(new AttivazionePolicyFiltro());
			policy.getFiltro().setRuoloPorta(RuoloPolicy.ENTRAMBI);
			policy.setGroupBy(new AttivazionePolicyRaggruppamento());
			
			// uso nome porta per capire se sono entrato per la prima volta nella schermata
			boolean first = confHelper.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
			
			//String id = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID); 
			
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
			
			String modalita = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA);
			if(modalita==null) {
				modalita = ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_DEFAULT;
			}
			boolean builtIn = ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_BUILT_IN.equals(modalita);
			
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
					String serialId = confCore.getNextPolicyInstanceSerialId(infoPolicy.getIdPolicy());
					policy.setIdActivePolicy(ControlloTrafficoDriverUtils.buildIdActivePolicy(infoPolicy.getIdPolicy(), serialId));
				}
			}
			
			// Dati Attivazione
			String errorAttivazione = confHelper.readDatiAttivazionePolicyFromHttpParameters(policy, first, tipoOperazione, infoPolicy);
			if(errorAttivazione!=null){
				confHelper.addParsingError(sbParsingError,errorAttivazione); 
			}
			
			if(ruoloPorta!=null) {
				
				String protocollo = null;
				String tipoSoggettoProprietario = null;
				String nomeSoggettoProprietario = null;
				if(RuoloPolicy.DELEGATA.equals(ruoloPorta)) {
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(nomePorta);
					PortaDelegata porta = pdCore.getPortaDelegata(idPD);
					protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(porta.getTipoSoggettoProprietario());
					// il tipo e nome serve per l'applicativo fruitore
					tipoSoggettoProprietario = porta.getTipoSoggettoProprietario();
					nomeSoggettoProprietario = porta.getNomeSoggettoProprietario();
				}
				else {
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(nomePorta);
					PortaApplicativa porta = paCore.getPortaApplicativa(idPA);
					protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(porta.getTipoSoggettoProprietario());
				}
				
				policy.getFiltro().setEnabled(true);
				policy.getFiltro().setProtocollo(protocollo);
				policy.getFiltro().setRuoloPorta(ruoloPorta);
				policy.getFiltro().setNomePorta(nomePorta);
				if(RuoloPolicy.DELEGATA.equals(ruoloPorta)) {
					policy.getFiltro().setTipoFruitore(tipoSoggettoProprietario);
					policy.getFiltro().setNomeFruitore(nomeSoggettoProprietario);
				}
				
			}
			
			// Preparo il menu
			confHelper.makeMenu();
			
			// setto la barra del titolo
			
			List<Parameter> lstParamPorta = null;
			if(ruoloPorta!=null) {
				lstParamPorta = confHelper.getTitleListAttivazionePolicy(ruoloPorta, nomePorta, serviceBinding, Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI);
			}
			
			List<Parameter> lstParam = null;
			if(lstParamPorta!=null) {
				lstParam = lstParamPorta;
			}
			else {
				lstParam = new ArrayList<Parameter>();
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_LIST));
				lstParam.add(ServletUtils.getParameterAggiungi());
			}
			
			List<InfoPolicy> infoPolicies = confCore.infoPolicyList(builtIn);
			
			// Se tipo = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// Controllo spostato dentro addAttivazionePolicyToDati
//				if(infoPolicies==null || infoPolicies.size()<=0){
//					pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_POSSIBILI_COMPLETATE_GLOBALI, MessageType.INFO);
//					pd.disableEditMode();
//					pd.setDati(dati);
//			
//					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
//
//					return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY,	ForwardParams.ADD());
//				}
				
				// Attivazione
				confHelper.addAttivazionePolicyToDati(dati, tipoOperazione, policy,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY, infoPolicies, ruoloPorta, nomePorta, serviceBinding, modalita);
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, 
						ForwardParams.ADD());
			}
			
			// Controlli sui campi immessi
			boolean isOk = confHelper.attivazionePolicyCheckData(tipoOperazione, configurazioneControlloTraffico, 
					policy,infoPolicy, ruoloPorta, nomePorta, serviceBinding, modalita);
			if (!isOk) {
				
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// Attivazione
				confHelper.addAttivazionePolicyToDati(dati, tipoOperazione, policy,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY, infoPolicies, ruoloPorta, nomePorta, serviceBinding, modalita);
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, 
						ForwardParams.ADD());
			}

			
			// aggiorno prossima posizione nella policy
			ConfigurazioneUtilities.updatePosizioneAttivazionePolicy(confCore, infoPolicy, policy, ruoloPorta, nomePorta);
			
			// insert sul db
			confCore.performCreateOperation(userLogin, confHelper.smista(), policy);
			
			String msgCompletato = confHelper.eseguiResetJmx(TipoOperazione.ADD, ruoloPorta, nomePorta);
			if(msgCompletato!=null && !"".equals(msgCompletato)){
				pd.setMessage(msgCompletato,Costanti.MESSAGE_TYPE_INFO);
			}
			
			// Preparo la lista
			int idLista = Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY;
			
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
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
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, ForwardParams.ADD());
		}  
	}
}
