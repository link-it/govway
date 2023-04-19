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
package org.openspcoop2.web.ctrlstat.servlet.pa;

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
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.PortaApplicativa;
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
import org.openspcoop2.web.lib.users.dao.User;

/***
 * 
 * PorteApplicativeCorrelazioneApplicativa
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class PorteApplicativeCorrelazioneApplicativa extends Action {

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
			@SuppressWarnings("unused")
			User user = ServletUtils.getUserFromSession(request, session);

			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			
			String id = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
					
			String tracciamentoEsitiStato = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_TRACCIAMENTO_ESITO);
			String nuovaConfigurazioneEsiti = porteApplicativeHelper.readConfigurazioneRegistrazioneEsitiFromHttpParameters(null, false);
			String tracciamentoEsitiSelezionePersonalizzataOk = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_OK);
			String tracciamentoEsitiSelezionePersonalizzataFault = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FAULT);
			String tracciamentoEsitiSelezionePersonalizzataFallite = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE);
			String tracciamentoEsitiSelezionePersonalizzataScartate = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_SCARTATE);
			String tracciamentoEsitiSelezionePersonalizzataRateLimiting = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_RATE_LIMITING);
			String tracciamentoEsitiSelezionePersonalizzataMax = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_MAX_REQUEST);
			String tracciamentoEsitiSelezionePersonalizzataCors = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_CORS);

			String tracciamentoEsitiSelezionePersonalizzataAll = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_ALL);
			boolean selectAll = ServletUtils.isCheckBoxEnabled(tracciamentoEsitiSelezionePersonalizzataAll);
			
			String statoDiagnostici = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_RIDEFINITO);
			String severita = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
			
			String scadcorr = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SCADENZA_CORRELAZIONE_APPLICATIVA);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String idTab = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteApplicativeHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo il nome della porta
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			
			PortaApplicativa pde = porteApplicativeCore.getPortaApplicativa(idInt);
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
			
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			
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
							config = porteApplicativeCore.getConfigurazioneGenerale();
						}
						nuovaConfigurazioneEsiti = config.getTracciamento()!=null ? config.getTracciamento().getEsiti() : null;
						if(nuovaConfigurazioneEsiti == null || "".equals(nuovaConfigurazioneEsiti.trim())){
							StringBuilder bf = new StringBuilder();
							porteApplicativeHelper.getRegistrazioneEsiti(nuovaConfigurazioneEsiti, bf);
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
					List<Integer> listOk = porteApplicativeHelper.getListaEsitiOkSenzaCors(esiti);
					if(porteApplicativeHelper.isCompleteEnabled(attivi, listOk)) {
						tracciamentoEsitiSelezionePersonalizzataOk = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						isOkTotale = true;
					}
					else if(porteApplicativeHelper.isCompleteDisabled(attivi, listOk)) {
						tracciamentoEsitiSelezionePersonalizzataOk = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					else {
						tracciamentoEsitiSelezionePersonalizzataOk = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
					}
					
					boolean isFaultTotale = false;
					List<Integer> listFault = esiti.getEsitiCodeFaultApplicativo();
					if(porteApplicativeHelper.isCompleteEnabled(attivi, listFault)) {
						tracciamentoEsitiSelezionePersonalizzataFault = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						isFaultTotale = true;
					}
					else if(porteApplicativeHelper.isCompleteDisabled(attivi, listFault)) {
						tracciamentoEsitiSelezionePersonalizzataFault = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					else {
						tracciamentoEsitiSelezionePersonalizzataFault = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
					}
					
					boolean isFalliteSenzaMaxThreadsScartateTotale = false;
					List<Integer> listFalliteSenzaMax = porteApplicativeHelper.getListaEsitiFalliteSenza_RateLimiting_MaxThreads_Scartate(esiti);
					if(porteApplicativeHelper.isCompleteEnabled(attivi, listFalliteSenzaMax)) {
						tracciamentoEsitiSelezionePersonalizzataFallite = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						isFalliteSenzaMaxThreadsScartateTotale = true;
					}
					else if(porteApplicativeHelper.isCompleteDisabled(attivi, listFalliteSenzaMax)) {
						tracciamentoEsitiSelezionePersonalizzataFallite = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					else {
						tracciamentoEsitiSelezionePersonalizzataFallite = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
					}
					
					boolean isScartateTotale = false;
					List<Integer> listScartate = esiti.getEsitiCodeRichiestaScartate();
					if(porteApplicativeHelper.isCompleteEnabled(attivi, listScartate)) {
						tracciamentoEsitiSelezionePersonalizzataScartate = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						isScartateTotale = true;
					}
					else if(porteApplicativeHelper.isCompleteDisabled(attivi, listScartate)) {
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
					List<Integer> listCors = porteApplicativeHelper.getListaEsitiCors(esiti);
					if(porteApplicativeHelper.isCompleteEnabled(attivi, listCors)) {
						tracciamentoEsitiSelezionePersonalizzataCors = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						isCorsTotale = true;
					}
					else if(porteApplicativeHelper.isCompleteDisabled(attivi, listCors)) {
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
						config = porteApplicativeCore.getConfigurazioneGenerale();
					}
					severita = config.getMessaggiDiagnostici()!=null && config.getMessaggiDiagnostici().getSeverita()!=null ? 
							config.getMessaggiDiagnostici().getSeverita().getValue() : 
								null;
				}
			}
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRACCIAMENTO_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRACCIAMENTO,
						pde);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRACCIAMENTO_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,  null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);

			Parameter[] urlParms = { 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id)	,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg), new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME,pde.getNome()),
					 new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS,idAsps) };
			Parameter urlRichiesta = new Parameter("", PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST, urlParms);
			Parameter urlRisposta = new Parameter("", PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST , urlParms);

			if(	porteApplicativeHelper.isEditModeInProgress()){
				if ((scadcorr == null) && (ca != null)) {
					scadcorr = ca.getScadenza();
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				porteApplicativeHelper.addToDatiRegistrazioneEsiti(dati, TipoOperazione.OTHER, 
						tracciamentoEsitiStato, nuovaConfigurazioneEsiti, 
						selectAll,
						tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
						tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataScartate, 
						tracciamentoEsitiSelezionePersonalizzataRateLimiting, tracciamentoEsitiSelezionePersonalizzataMax, tracciamentoEsitiSelezionePersonalizzataCors); 
				
				porteApplicativeHelper.addPortaSeveritaMessaggiDiagnosticiToDati(statoDiagnostici, severita, dati);
				
				dati = porteApplicativeHelper.addCorrelazioneApplicativaToDati(dati, false, riusoID, scadcorr, urlRichiesta.getValue(), urlRisposta.getValue(), contaListe, numCorrelazioneReq, numCorrelazioneRes);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, ForwardParams.OTHER(""));
			}	

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.correlazioneApplicativaCheckData(TipoOperazione.OTHER,true,scadcorr);
			if(!isOk) {
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				porteApplicativeHelper.addToDatiRegistrazioneEsiti(dati, TipoOperazione.OTHER, 
						tracciamentoEsitiStato, nuovaConfigurazioneEsiti, 
						selectAll,
						tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
						tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataScartate, 
						tracciamentoEsitiSelezionePersonalizzataRateLimiting, tracciamentoEsitiSelezionePersonalizzataMax, tracciamentoEsitiSelezionePersonalizzataCors); 
				
				porteApplicativeHelper.addPortaSeveritaMessaggiDiagnosticiToDati(statoDiagnostici, severita, dati);
				
				dati = porteApplicativeHelper.addCorrelazioneApplicativaToDati(dati, false, riusoID, scadcorr, urlRichiesta.getValue(), urlRisposta.getValue(), contaListe, numCorrelazioneReq, numCorrelazioneRes);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA,
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

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pde);


			// preparo i campi
			List<DataElement> dati = new ArrayList<>();

			// Aggiorno valori MTOM request e response
			pde = porteApplicativeCore.getPortaApplicativa(idInt);

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

			porteApplicativeHelper.addToDatiRegistrazioneEsiti(dati, TipoOperazione.OTHER, 
					tracciamentoEsitiStato, nuovaConfigurazioneEsiti, 
					selectAll,
					tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
					tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataScartate, 
					tracciamentoEsitiSelezionePersonalizzataRateLimiting, tracciamentoEsitiSelezionePersonalizzataMax, tracciamentoEsitiSelezionePersonalizzataCors); 
			
			porteApplicativeHelper.addPortaSeveritaMessaggiDiagnosticiToDati(statoDiagnostici, severita, dati);
			
			dati = porteApplicativeHelper.addCorrelazioneApplicativaToDati(dati, false, riusoID, scadcorr, urlRichiesta.getValue(), urlRisposta.getValue(), contaListe, numCorrelazioneReq, numCorrelazioneRes);

			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

			pd.setDati(dati);
			
			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			dati.add(ServletUtils.getDataElementForEditModeFinished());
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA,
					ForwardParams.OTHER(""));
		}catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA , 
					ForwardParams.OTHER(""));
		} 
	}

}
