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
package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaTracciamento;
import org.openspcoop2.core.config.constants.Severita;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiConfigUtils;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/***
 * 
 * PorteDelegateCorrelazioneApplicativa
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class PorteDelegateCorrelazioneApplicativa extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try{
			Boolean contaListe = ServletUtils.getContaListeFromSession(session);
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			String id = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			
			String tracciamentoEsitiStato = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_TRACCIAMENTO_ESITO);
			String nuovaConfigurazioneEsiti = porteDelegateHelper.readConfigurazioneRegistrazioneEsitiFromHttpParameters(null, false);
			String tracciamentoEsitiSelezionePersonalizzataOk = porteDelegateHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_OK);
			String tracciamentoEsitiSelezionePersonalizzataFault = porteDelegateHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FAULT);
			String tracciamentoEsitiSelezionePersonalizzataFallite = porteDelegateHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE);
			String tracciamentoEsitiSelezionePersonalizzataScartate = porteDelegateHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_SCARTATE);
			String tracciamentoEsitiSelezionePersonalizzataRateLimiting = porteDelegateHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_RATE_LIMITING);
			String tracciamentoEsitiSelezionePersonalizzataMax = porteDelegateHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_MAX_REQUEST);
			String tracciamentoEsitiSelezionePersonalizzataCors = porteDelegateHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_CORS);

			String tracciamentoEsitiSelezionePersonalizzataAll = porteDelegateHelper.getParametroBoolean(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_ALL);
			boolean selectAll = ServletUtils.isCheckBoxEnabled(tracciamentoEsitiSelezionePersonalizzataAll);
			
			String statoDiagnostici = porteDelegateHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_RIDEFINITO);
			String severita = porteDelegateHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
			
			String scadcorr = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SCADENZA_CORRELAZIONE_APPLICATIVA);
			String idAsps = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idFruizione = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null) 
				idFruizione = "";
			
			String idTab = porteDelegateHelper.getParametroInteger(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteDelegateHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}

			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = pde.getNome();

			// stato correlazione applicativa
			int numCorrelazioneReq = 0;
			int numCorrelazioneRes = 0;

			CorrelazioneApplicativa ca = pde.getCorrelazioneApplicativa();
			if (ca != null)
				numCorrelazioneReq = ca.sizeElementoList();

			if (pde.getCorrelazioneApplicativaRisposta() != null)
				numCorrelazioneRes = pde.getCorrelazioneApplicativaRisposta().sizeElementoList();

			boolean riusoID = false;
			if(numCorrelazioneReq>0){
				for (int i = 0; i < numCorrelazioneReq; i++) {
					if(StatoFunzionalita.ABILITATO.equals(pde.getCorrelazioneApplicativa().getElemento(i).getRiusoIdentificativo())){
						riusoID = true;
						break;
					}
				}
			}
			
			String postBackElementName = porteDelegateHelper.getPostBackElementName();
			
			Configurazione config = null;
			boolean firstTracciamento = false;
			if(tracciamentoEsitiStato==null) {
				firstTracciamento = true;
				if(pde.getTracciamento()!=null && pde.getTracciamento().getEsiti()!=null) {
					tracciamentoEsitiStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
				}
				else {
					tracciamentoEsitiStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
				}
			}
			else if(CostantiControlStation.PARAMETRO_PORTE_TRACCIAMENTO_ESITO.equals(postBackElementName)) {
				firstTracciamento = true;
			}
			
			if(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO.equals(tracciamentoEsitiStato)) {
				if(nuovaConfigurazioneEsiti==null && firstTracciamento) {
					if(pde.getTracciamento()!=null && pde.getTracciamento().getEsiti()!=null) {
						nuovaConfigurazioneEsiti = pde.getTracciamento().getEsiti();
					}
					else {
						// prendo quella di default
						if(config==null) {
							config = porteDelegateCore.getConfigurazioneGenerale();
						}
						nuovaConfigurazioneEsiti = config.getTracciamento()!=null ? config.getTracciamento().getEsiti() : null;
						if(nuovaConfigurazioneEsiti == null || "".equals(nuovaConfigurazioneEsiti.trim())){
							StringBuilder bf = new StringBuilder();
							porteDelegateHelper.getRegistrazioneEsiti(nuovaConfigurazioneEsiti, bf);
							if(bf.length()>0){
								nuovaConfigurazioneEsiti = bf.toString();
							}
						}
					}
				}
				if(tracciamentoEsitiSelezionePersonalizzataOk==null) {
					
					List<String> attivi = new ArrayList<>();
					if(nuovaConfigurazioneEsiti!=null){
						String [] tmp = nuovaConfigurazioneEsiti.split(",");
						if(tmp!=null){
							for (int i = 0; i < tmp.length; i++) {
								attivi.add(tmp[i].trim());
							}
						}
					}
					
					EsitiProperties esiti = EsitiConfigUtils.getEsitiPropertiesForConfiguration(ControlStationCore.getLog());
					
					boolean isOkTotale = false;
					List<Integer> listOk = porteDelegateHelper.getListaEsitiOkSenzaCors(esiti);
					if(porteDelegateHelper.isCompleteEnabled(attivi, listOk)) {
						tracciamentoEsitiSelezionePersonalizzataOk = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						isOkTotale = true;
					}
					else if(porteDelegateHelper.isCompleteDisabled(attivi, listOk)) {
						tracciamentoEsitiSelezionePersonalizzataOk = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					else {
						tracciamentoEsitiSelezionePersonalizzataOk = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
					}
					
					boolean isFaultTotale = false;
					List<Integer> listFault = esiti.getEsitiCodeFaultApplicativo();
					if(porteDelegateHelper.isCompleteEnabled(attivi, listFault)) {
						tracciamentoEsitiSelezionePersonalizzataFault = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						isFaultTotale = true;
					}
					else if(porteDelegateHelper.isCompleteDisabled(attivi, listFault)) {
						tracciamentoEsitiSelezionePersonalizzataFault = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					else {
						tracciamentoEsitiSelezionePersonalizzataFault = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
					}
					
					boolean isFalliteSenzaMaxThreadsScartateTotale = false;
					List<Integer> listFalliteSenzaMax = porteDelegateHelper.getListaEsitiFalliteSenza_RateLimiting_MaxThreads_Scartate(esiti);
					if(porteDelegateHelper.isCompleteEnabled(attivi, listFalliteSenzaMax)) {
						tracciamentoEsitiSelezionePersonalizzataFallite = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						isFalliteSenzaMaxThreadsScartateTotale = true;
					}
					else if(porteDelegateHelper.isCompleteDisabled(attivi, listFalliteSenzaMax)) {
						tracciamentoEsitiSelezionePersonalizzataFallite = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					else {
						tracciamentoEsitiSelezionePersonalizzataFallite = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
					}
					
					boolean isScartateTotale = false;
					List<Integer> listScartate = esiti.getEsitiCodeRichiestaScartate();
					if(porteDelegateHelper.isCompleteEnabled(attivi, listScartate)) {
						tracciamentoEsitiSelezionePersonalizzataScartate = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						isScartateTotale = true;
					}
					else if(porteDelegateHelper.isCompleteDisabled(attivi, listScartate)) {
						tracciamentoEsitiSelezionePersonalizzataScartate = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					else {
						tracciamentoEsitiSelezionePersonalizzataScartate = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
					}
					
					boolean isRateLimiting = false;
					if(attivi.contains((esiti.convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA)+""))) {
						tracciamentoEsitiSelezionePersonalizzataRateLimiting = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						isRateLimiting = true;
					}	
					else {
						tracciamentoEsitiSelezionePersonalizzataRateLimiting = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					
					boolean isMaxThreads = false;
					if(attivi.contains((esiti.convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_MAX_THREADS)+""))) {
						tracciamentoEsitiSelezionePersonalizzataMax = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						isMaxThreads = true;
					}	
					else {
						tracciamentoEsitiSelezionePersonalizzataMax = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					
					boolean isCorsTotale = false;
					List<Integer> listCors = porteDelegateHelper.getListaEsitiCors(esiti);
					if(porteDelegateHelper.isCompleteEnabled(attivi, listCors)) {
						tracciamentoEsitiSelezionePersonalizzataCors = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						isCorsTotale = true;
					}
					else if(porteDelegateHelper.isCompleteDisabled(attivi, listCors)) {
						tracciamentoEsitiSelezionePersonalizzataCors = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					else {
						tracciamentoEsitiSelezionePersonalizzataCors = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
					}
					
					selectAll = isOkTotale && isFaultTotale && 
							isFalliteSenzaMaxThreadsScartateTotale && isScartateTotale && 
							isRateLimiting && isMaxThreads && isCorsTotale;
					
				}
			}
			if(statoDiagnostici==null) {
				if(pde.getTracciamento()!=null && pde.getTracciamento().getSeverita()!=null) {
					statoDiagnostici = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
				}
				else {
					statoDiagnostici = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
				}
			}
			if(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO.equals(statoDiagnostici) && severita==null) {
				// prendo quella di default
				if(pde.getTracciamento()!=null && pde.getTracciamento().getSeverita()!=null) {
					severita = pde.getTracciamento().getSeverita().getValue();
				}
				else {
					if(config==null) {
						config = porteDelegateCore.getConfigurazioneGenerale();
					}
					severita = config.getMessaggiDiagnostici()!=null && config.getMessaggiDiagnostici().getSeverita()!=null ? 
							config.getMessaggiDiagnostici().getSeverita().getValue() : 
								null;
				}
			}
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFruizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRACCIAMENTO_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRACCIAMENTO,
						pde);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRACCIAMENTO_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,  null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);

			Parameter[] urlParms = { pId,pIdSoggetto,pIdAsps,pIdFruizione };
			Parameter urlRichiesta = new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST, urlParms);
			Parameter urlRisposta = new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST , urlParms);

			if(	porteDelegateHelper.isEditModeInProgress()){
				if ((scadcorr == null) && (ca != null)) {
					scadcorr = ca.getScadenza();
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				porteDelegateHelper.addToDatiRegistrazioneEsiti(dati, TipoOperazione.OTHER, 
						tracciamentoEsitiStato, nuovaConfigurazioneEsiti, 
						selectAll,
						tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
						tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataScartate,
						tracciamentoEsitiSelezionePersonalizzataRateLimiting, tracciamentoEsitiSelezionePersonalizzataMax, tracciamentoEsitiSelezionePersonalizzataCors); 
				
				porteDelegateHelper.addPortaSeveritaMessaggiDiagnosticiToDati(statoDiagnostici, severita, dati);
				
				dati = porteDelegateHelper.addCorrelazioneApplicativaToDati(dati, true, riusoID, scadcorr, urlRichiesta.getValue(), urlRisposta.getValue(), contaListe, numCorrelazioneReq, numCorrelazioneRes);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA, ForwardParams.OTHER(""));
			}	

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.correlazioneApplicativaCheckData(TipoOperazione.OTHER,true,scadcorr);
			if(!isOk) {
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				porteDelegateHelper.addToDatiRegistrazioneEsiti(dati, TipoOperazione.OTHER, 
						tracciamentoEsitiStato, nuovaConfigurazioneEsiti, 
						selectAll,
						tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
						tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataScartate, 
						tracciamentoEsitiSelezionePersonalizzataRateLimiting, tracciamentoEsitiSelezionePersonalizzataMax, tracciamentoEsitiSelezionePersonalizzataCors); 
				
				porteDelegateHelper.addPortaSeveritaMessaggiDiagnosticiToDati(statoDiagnostici, severita, dati);
				
				dati = porteDelegateHelper.addCorrelazioneApplicativaToDati(dati,true, riusoID, scadcorr, urlRichiesta.getValue(), urlRisposta.getValue(), contaListe, numCorrelazioneReq, numCorrelazioneRes);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, id, idsogg, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA,
						ForwardParams.OTHER(""));
			}

			// perform update
			// Cambio i dati della vecchia CorrelazioneApplicativa
			// Non ne creo una nuova, altrimenti mi perdo le vecchie entry
			if (ca != null)
				ca.setScadenza(scadcorr);
			pde.setCorrelazioneApplicativa(ca);

			if(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO.equals(statoDiagnostici) || CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO.equals(tracciamentoEsitiStato)) {
				PortaTracciamento portaTracciamento = new PortaTracciamento();
				if(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO.equals(statoDiagnostici)) {
					portaTracciamento.setSeverita(Severita.toEnumConstant(severita));
				}
				if(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO.equals(tracciamentoEsitiStato)) {
					if(StringUtils.isEmpty(nuovaConfigurazioneEsiti)) {
						portaTracciamento.setEsiti(EsitiConfigUtils.TUTTI_ESITI_DISABILITATI+"");
					}
					else {
						portaTracciamento.setEsiti(nuovaConfigurazioneEsiti);
					}
				}
				pde.setTracciamento(portaTracciamento);
			}
			else {
				pde.setTracciamento(null);
			}
			
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);


			// preparo i campi
			List<DataElement> dati = new ArrayList<>();

			// Aggiorno valori MTOM request e response
			pde = porteDelegateCore.getPortaDelegata(idInt);

			// stato correlazione applicativa
			numCorrelazioneReq = 0;
			numCorrelazioneRes = 0;

			ca = pde.getCorrelazioneApplicativa();
			if (ca != null)
				numCorrelazioneReq = ca.sizeElementoList();

			if (pde.getCorrelazioneApplicativaRisposta() != null)
				numCorrelazioneRes = pde.getCorrelazioneApplicativaRisposta().sizeElementoList();

			riusoID = false;
			if(numCorrelazioneReq>0){
				for (int i = 0; i < numCorrelazioneReq; i++) {
					if(StatoFunzionalita.ABILITATO.equals(pde.getCorrelazioneApplicativa().getElemento(i).getRiusoIdentificativo())){
						riusoID = true;
						break;
					}
				}
			}

			porteDelegateHelper.addToDatiRegistrazioneEsiti(dati, TipoOperazione.OTHER, 
					tracciamentoEsitiStato, nuovaConfigurazioneEsiti, 
					selectAll,
					tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
					tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataScartate, 
					tracciamentoEsitiSelezionePersonalizzataRateLimiting, tracciamentoEsitiSelezionePersonalizzataMax, tracciamentoEsitiSelezionePersonalizzataCors); 
			
			porteDelegateHelper.addPortaSeveritaMessaggiDiagnosticiToDati(statoDiagnostici, severita, dati);
			
			dati = porteDelegateHelper.addCorrelazioneApplicativaToDati(dati, true,riusoID, scadcorr, urlRichiesta.getValue(), urlRisposta.getValue(), contaListe, numCorrelazioneReq, numCorrelazioneRes);

			dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, id, idsogg, null, idAsps, 
					idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

			pd.setDati(dati);
						
			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			dati.add(ServletUtils.getDataElementForEditModeFinished());
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA,
					ForwardParams.OTHER(""));
		}catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA , 
					ForwardParams.OTHER(""));
		} 
	}
}
