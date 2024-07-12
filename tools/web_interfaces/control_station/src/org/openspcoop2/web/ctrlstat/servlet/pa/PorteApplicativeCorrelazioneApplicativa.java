/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.config.TracciamentoConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.utils.TracciamentoCompatibilitaFiltroEsiti;
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
			
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String idTab = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteApplicativeHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			
			String tipoConfigurazione = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE);
			
			String tracciamentoStato = null;
			String oldTracciamentoStato = null;
			
			String statoDiagnostici = null;
			String severita = null;
			
			String scadcorr = null;
			
			boolean first = false;
			
			String dbStato = null;
			String dbStatoReqIn = null;
			String dbStatoReqOut = null;
			String dbStatoResOut = null;
			String dbStatoResOutComplete = null;
			boolean dbFiltroEsiti = false;
			
			String fsStato = null;
			String fsStatoReqIn = null;
			String fsStatoReqOut = null;
			String fsStatoResOut = null;
			String fsStatoResOutComplete = null;
			boolean fsFiltroEsiti = false;
			
			String nuovaConfigurazioneEsiti = null;
			String tracciamentoEsitiSelezionePersonalizzataOk = null;
			String tracciamentoEsitiSelezionePersonalizzataFault = null;
			String tracciamentoEsitiSelezionePersonalizzataFallite = null;
			String tracciamentoEsitiSelezionePersonalizzataScartate = null;
			String tracciamentoEsitiSelezionePersonalizzataRateLimiting = null;
			String tracciamentoEsitiSelezionePersonalizzataMax = null;
			String tracciamentoEsitiSelezionePersonalizzataCors = null;
			
			String tracciamentoEsitiSelezionePersonalizzataAll = null;
			boolean selectAll = false;
			
			String transazioniTempiElaborazione = null;
			String transazioniToken = null;
			
			String fileTraceStato = null;
			String fileTraceConfigFile = null;
			String fileTraceClient = null; 
			String fileTraceClientHdr = null; 
			String fileTraceClientBody = null;
			String fileTraceServer = null;
			String fileTraceServerHdr = null; 
			String fileTraceServerBody = null;
			
			if(tipoConfigurazione==null || StringUtils.isEmpty(tipoConfigurazione) 
					|| 
					ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO.equals(tipoConfigurazione)) {
				
				tracciamentoStato = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_TRACCIAMENTO_STATO);
				
				statoDiagnostici = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_RIDEFINITO);
				severita = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
				
				scadcorr = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SCADENZA_CORRELAZIONE_APPLICATIVA);
			}
			else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PORTA.equals(tipoConfigurazione)) {
						
				first = porteApplicativeHelper.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
				
				dbStato = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO);
				dbStatoReqIn = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_REQ_IN);
				dbStatoReqOut = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_REQ_OUT);
				dbStatoResOut = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_RES_OUT);
				dbStatoResOutComplete = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_RES_OUT_COMPLETE);
				String dbFiltroEsitiParam = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_FILTRA_ESITI);
				dbFiltroEsiti = ServletUtils.isCheckBoxEnabled(dbFiltroEsitiParam);
				
				fsStato = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO);
				fsStatoReqIn = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_REQ_IN);
				fsStatoReqOut = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_REQ_OUT);
				fsStatoResOut = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_RES_OUT);
				fsStatoResOutComplete = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_RES_OUT_COMPLETE);
				String fsFiltroEsitiParam = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_FILTRA_ESITI);
				fsFiltroEsiti = ServletUtils.isCheckBoxEnabled(fsFiltroEsitiParam);
				
				tracciamentoEsitiSelezionePersonalizzataOk = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_OK);
				tracciamentoEsitiSelezionePersonalizzataFault = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FAULT);
				tracciamentoEsitiSelezionePersonalizzataFallite = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE);
				tracciamentoEsitiSelezionePersonalizzataScartate = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_SCARTATE);
				tracciamentoEsitiSelezionePersonalizzataRateLimiting = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_RATE_LIMITING);
				tracciamentoEsitiSelezionePersonalizzataMax = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_MAX_REQUEST);
				tracciamentoEsitiSelezionePersonalizzataCors = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_CORS);
	
				tracciamentoEsitiSelezionePersonalizzataAll = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_ALL);
				selectAll = ServletUtils.isCheckBoxEnabled(tracciamentoEsitiSelezionePersonalizzataAll);
			
				transazioniTempiElaborazione = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRANSAZIONE_TEMPI);
				transazioniToken = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRANSAZIONE_TOKEN);
				
				fileTraceStato = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_STATO);
				fileTraceConfigFile = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_CONFIGURAZIONE);
				fileTraceClient = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_CLIENT);
				fileTraceClientHdr = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_CLIENT_HEADER);
				fileTraceClientBody = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_CLIENT_PAYLOAD);
				fileTraceServer = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_SERVER);
				fileTraceServerHdr = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_SERVER_HEADER);
				fileTraceServerBody = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_SERVER_PAYLOAD);
			}
			
			
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo il nome della porta
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			
			PortaApplicativa pde = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pde.getNome();
			
			if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PORTA.equals(tipoConfigurazione)) {
				nuovaConfigurazioneEsiti = porteApplicativeHelper.readConfigurazioneRegistrazioneEsitiFromHttpParameters(pde.getTracciamento()!=null ? pde.getTracciamento().getEsiti() : null, first);
			}
			
			List<Parameter> listParameter = new ArrayList<>();
			Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			listParameter.add(pIdSogg);
			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id);
			listParameter.add(pIdPorta);
			Parameter pIdNome = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, pde.getNome());
			listParameter.add(pIdNome);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			listParameter.add(pIdAsps);
			
			if(pde.getTracciamento()!=null && pde.getTracciamento().getStato()!=null && StatoFunzionalita.ABILITATO.equals(pde.getTracciamento().getStato())) {
				oldTracciamentoStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
			}
			else {
				oldTracciamentoStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
			}

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
			
			
			Configurazione config = null;
			
			
			if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PORTA.equals(tipoConfigurazione)) {
			
				boolean initDB = false;
				boolean initFileTrace = false;
				if(first && pde.getTracciamento()!=null) {
												
					if(pde.getTracciamento().getDatabase()!=null) {
						dbStato = (pde.getTracciamento().getDatabase().getStato()!=null) ? pde.getTracciamento().getDatabase().getStato().getValue() : 
							ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO;
						dbStatoReqIn = (pde.getTracciamento().getDatabase().getRequestIn()!=null) ? pde.getTracciamento().getDatabase().getRequestIn().getValue() : 
							ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_REQ_IN;
						dbStatoReqOut = (pde.getTracciamento().getDatabase().getRequestOut()!=null) ? pde.getTracciamento().getDatabase().getRequestOut().getValue() : 
							ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_REQ_OUT;
						dbStatoResOut = (pde.getTracciamento().getDatabase().getResponseOut()!=null) ? pde.getTracciamento().getDatabase().getResponseOut().getValue() : 
							ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_RES_OUT;
						dbStatoResOutComplete = (pde.getTracciamento().getDatabase().getResponseOutComplete()!=null) ? pde.getTracciamento().getDatabase().getResponseOutComplete().getValue() : 
							ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_RES_OUT_COMPLETE;
						dbFiltroEsiti = (pde.getTracciamento().getDatabase().getFiltroEsiti()!=null) ? StatoFunzionalita.ABILITATO.equals(pde.getTracciamento().getDatabase().getFiltroEsiti()) : 
							ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_FILTRA_ESITI;
						initDB=true;
					}
					
					if(pde.getTracciamento().getFiletrace()!=null) {
						fsStato = (pde.getTracciamento().getFiletrace().getStato()!=null) ? pde.getTracciamento().getFiletrace().getStato().getValue() : 
							ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO;
						fsStatoReqIn = (pde.getTracciamento().getFiletrace().getRequestIn()!=null) ? pde.getTracciamento().getFiletrace().getRequestIn().getValue() : 
							ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_REQ_IN;
						fsStatoReqOut = (pde.getTracciamento().getFiletrace().getRequestOut()!=null) ? pde.getTracciamento().getFiletrace().getRequestOut().getValue() : 
							ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_REQ_OUT;
						fsStatoResOut = (pde.getTracciamento().getFiletrace().getResponseOut()!=null) ? pde.getTracciamento().getFiletrace().getResponseOut().getValue() : 
							ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_RES_OUT;
						fsStatoResOutComplete = (pde.getTracciamento().getFiletrace().getResponseOutComplete()!=null) ? pde.getTracciamento().getFiletrace().getResponseOutComplete().getValue() : 
							ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_RES_OUT_COMPLETE;
						fsFiltroEsiti = (pde.getTracciamento().getFiletrace().getFiltroEsiti()!=null) ? StatoFunzionalita.ABILITATO.equals(pde.getTracciamento().getFiletrace().getFiltroEsiti()) : 
							ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_FILTRA_ESITI;
						initFileTrace=true;
					}
					
				}
				if(first && !initDB) {
					dbStato = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO;
					dbStatoReqIn = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_REQ_IN;
					dbStatoReqOut = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_REQ_OUT;
					dbStatoResOut = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_RES_OUT;
					dbStatoResOutComplete = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_RES_OUT_COMPLETE;
					dbFiltroEsiti = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_FILTRA_ESITI;
				}
				if(first && !initFileTrace) {
					fsStato = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO;
					fsStatoReqIn = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_REQ_IN;
					fsStatoReqOut = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_REQ_OUT;
					fsStatoResOut = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_RES_OUT;
					fsStatoResOutComplete = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_RES_OUT_COMPLETE;
					fsFiltroEsiti = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_FILTRA_ESITI;
				}
				
				if(nuovaConfigurazioneEsiti==null) {
					if(pde.getTracciamento()!=null && pde.getTracciamento().getEsiti()!=null) {
						nuovaConfigurazioneEsiti = pde.getTracciamento().getEsiti();
					}
					else {
						// prendo quella di default
						if(config==null) {
							config = porteApplicativeCore.getConfigurazioneGenerale();
						}
						nuovaConfigurazioneEsiti = config.getTracciamento()!=null && config.getTracciamento().getPortaApplicativa()!=null ? config.getTracciamento().getPortaApplicativa().getEsiti() : null;
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
				
				
				if(first &&
						pde.getTracciamento()!=null && pde.getTracciamento().getTransazioni()!=null) {
					if(pde.getTracciamento().getTransazioni().getTempiElaborazione()!=null) {
						transazioniTempiElaborazione = pde.getTracciamento().getTransazioni().getTempiElaborazione().toString();
					}
					if(pde.getTracciamento().getTransazioni().getToken()!=null) {
						transazioniToken = pde.getTracciamento().getTransazioni().getToken().toString();
					}
				}
				
				if( ConfigurazioneCostanti.PARAMETRO_FILETRACE_STATO.equals(porteApplicativeHelper.getPostBackElementName())) {
					if(fileTraceClient==null || StringUtils.isEmpty(fileTraceClient)) {
						fileTraceClient = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					if(fileTraceClientHdr==null || StringUtils.isEmpty(fileTraceClientHdr)) {
						fileTraceClientHdr = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					}
					if(fileTraceClientBody==null || StringUtils.isEmpty(fileTraceClientBody)) {
						fileTraceClientBody = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					}
					if(fileTraceServer==null || StringUtils.isEmpty(fileTraceServer)) {
						fileTraceServer = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					if(fileTraceServerHdr==null || StringUtils.isEmpty(fileTraceServerHdr)) {
						fileTraceServerHdr = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					}
					if(fileTraceServerBody==null || StringUtils.isEmpty(fileTraceServerBody)) {
						fileTraceServerBody = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					}
				}
				else if( first &&
						pde.getTracciamento()!=null) {
					if(pde.getTracciamento().getFiletraceConfig()!=null) {
						fileTraceStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
						fileTraceConfigFile = pde.getTracciamento().getFiletraceConfig().getConfig();
						if(pde.getTracciamento().getFiletraceConfig().getDumpIn()!=null) {
							fileTraceClient = StatoFunzionalita.ABILITATO.equals(pde.getTracciamento().getFiletraceConfig().getDumpIn().getStato()) ? 
									ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO : ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
							fileTraceClientHdr = StatoFunzionalita.ABILITATO.equals(pde.getTracciamento().getFiletraceConfig().getDumpIn().getHeader()) ? 
									ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO : ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
							fileTraceClientBody = StatoFunzionalita.ABILITATO.equals(pde.getTracciamento().getFiletraceConfig().getDumpIn().getPayload()) ? 
									ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO : ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
						}
						else {
							fileTraceClient = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
						}
						if(pde.getTracciamento().getFiletraceConfig().getDumpOut()!=null) {
							fileTraceServer = StatoFunzionalita.ABILITATO.equals(pde.getTracciamento().getFiletraceConfig().getDumpOut().getStato()) ? 
									ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO : ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
							fileTraceServerHdr = StatoFunzionalita.ABILITATO.equals(pde.getTracciamento().getFiletraceConfig().getDumpOut().getHeader()) ? 
									ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO : ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
							fileTraceServerBody = StatoFunzionalita.ABILITATO.equals(pde.getTracciamento().getFiletraceConfig().getDumpOut().getPayload()) ? 
									ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO : ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
						}
						else {
							fileTraceServer = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
						}
					}
					else {
						fileTraceStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
					}
				}
			}
				
			
			if(tipoConfigurazione==null || StringUtils.isEmpty(tipoConfigurazione) 
					|| 
					ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO.equals(tipoConfigurazione)) {
			
				if(tracciamentoStato==null) {
					if(pde.getTracciamento()!=null && pde.getTracciamento().getStato()!=null && StatoFunzionalita.ABILITATO.equals(pde.getTracciamento().getStato())) {
						tracciamentoStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
					}
					else {
						tracciamentoStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
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
			}
			
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			Parameter pMenu = null;
			String servlet = null;
			if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PORTA.equals(tipoConfigurazione)) {
				pMenu = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE, ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO);
				servlet = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA;
			}
			
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
			
			if(pMenu!=null) {
				List<Parameter> l = new ArrayList<>();
				l.addAll(listParameter);
				l.add(pMenu);
				lstParam.add(new Parameter(labelPerPorta,  servlet, l.toArray(new Parameter[1])));
			}
			else {
				lstParam.add(new Parameter(labelPerPorta,  null));
			}
			
			if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PORTA.equals(tipoConfigurazione)) {
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO_CONFIGURAZIONE, null));
			}
			

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// imposta menu' contestuale
			porteApplicativeHelper.impostaComandiMenuContestualePA(idsogg, idAsps);

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

				if(tipoConfigurazione==null || StringUtils.isEmpty(tipoConfigurazione) 
						|| 
						ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO.equals(tipoConfigurazione)) {
					
					porteApplicativeHelper.addToDatiTracciamentoTransazioni(dati, TipoOperazione.OTHER,
							tracciamentoStato, 
							PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, listParameter,
							oldTracciamentoStato);
					
					porteApplicativeHelper.addPortaSeveritaMessaggiDiagnosticiToDati(statoDiagnostici, severita, dati);
					
					dati = porteApplicativeHelper.addCorrelazioneApplicativaToDati(dati, false, riusoID, scadcorr, urlRichiesta.getValue(), urlRisposta.getValue(), contaListe, numCorrelazioneReq, numCorrelazioneRes);

				}
				else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PORTA.equals(tipoConfigurazione)) {
				
					porteApplicativeHelper.addToDatiConfigurazioneAvanzataTracciamento(dati, TipoOperazione.OTHER,
							dbStato,
							dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
							dbFiltroEsiti,
							fsStato,
							fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
							fsFiltroEsiti,
							true);
					
					TracciamentoConfigurazione database = porteApplicativeCore.buildTracciamentoConfigurazioneDatabase(dbStato,
							dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
							dbFiltroEsiti);
					TracciamentoConfigurazione filetrace = porteApplicativeCore.buildTracciamentoConfigurazioneFiletrace(fsStato,
							fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
							fsFiltroEsiti);
					TracciamentoCompatibilitaFiltroEsiti tracciamentoCompatibilitaFiltroEsiti = new TracciamentoCompatibilitaFiltroEsiti(database, filetrace);
					
					porteApplicativeHelper.addToDatiRegistrazioneEsiti(dati, TipoOperazione.OTHER, 
							nuovaConfigurazioneEsiti, 
							selectAll,
							tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
							tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataScartate, 
							tracciamentoEsitiSelezionePersonalizzataRateLimiting, tracciamentoEsitiSelezionePersonalizzataMax, tracciamentoEsitiSelezionePersonalizzataCors,
							tracciamentoCompatibilitaFiltroEsiti); 
					
					porteApplicativeHelper.addToDatiRegistrazioneTransazione(dati, TipoOperazione.OTHER, 
							transazioniTempiElaborazione, transazioniToken); 
					
					porteApplicativeHelper.addToDatiRegistrazioneConfigurazioneFileTrace(dati, 
							fileTraceStato, fileTraceConfigFile,
							fileTraceClient, fileTraceClientHdr, fileTraceClientBody,
							fileTraceServer, fileTraceServerHdr, fileTraceServerBody,
							tracciamentoCompatibilitaFiltroEsiti);
				
				}

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

				// Set First is false
				porteApplicativeHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
				// Set Tipo Operazione
				porteApplicativeHelper.addToDatiHiddenParameter(dati, ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE, tipoConfigurazione);
				
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

				if(tipoConfigurazione==null || StringUtils.isEmpty(tipoConfigurazione) 
						|| 
						ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO.equals(tipoConfigurazione)) {
					
					porteApplicativeHelper.addToDatiTracciamentoTransazioni(dati, TipoOperazione.OTHER,
							tracciamentoStato, 
							PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, listParameter,
							oldTracciamentoStato);
					
					porteApplicativeHelper.addPortaSeveritaMessaggiDiagnosticiToDati(statoDiagnostici, severita, dati);
					
					dati = porteApplicativeHelper.addCorrelazioneApplicativaToDati(dati, false, riusoID, scadcorr, urlRichiesta.getValue(), urlRisposta.getValue(), contaListe, numCorrelazioneReq, numCorrelazioneRes);

				}
				else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PORTA.equals(tipoConfigurazione)) {
				
					porteApplicativeHelper.addToDatiConfigurazioneAvanzataTracciamento(dati, TipoOperazione.OTHER,
							dbStato,
							dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
							dbFiltroEsiti,
							fsStato,
							fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
							fsFiltroEsiti,
							true);
					
					TracciamentoConfigurazione database = porteApplicativeCore.buildTracciamentoConfigurazioneDatabase(dbStato,
							dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
							dbFiltroEsiti);
					TracciamentoConfigurazione filetrace = porteApplicativeCore.buildTracciamentoConfigurazioneFiletrace(fsStato,
							fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
							fsFiltroEsiti);
					TracciamentoCompatibilitaFiltroEsiti tracciamentoCompatibilitaFiltroEsiti = new TracciamentoCompatibilitaFiltroEsiti(database, filetrace);
				
					porteApplicativeHelper.addToDatiRegistrazioneEsiti(dati, TipoOperazione.OTHER, 
							nuovaConfigurazioneEsiti, 
							selectAll,
							tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
							tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataScartate, 
							tracciamentoEsitiSelezionePersonalizzataRateLimiting, tracciamentoEsitiSelezionePersonalizzataMax, tracciamentoEsitiSelezionePersonalizzataCors,
							tracciamentoCompatibilitaFiltroEsiti); 
					
					porteApplicativeHelper.addToDatiRegistrazioneTransazione(dati, TipoOperazione.OTHER, 
							transazioniTempiElaborazione, transazioniToken); 
					
					porteApplicativeHelper.addToDatiRegistrazioneConfigurazioneFileTrace(dati, 
							fileTraceStato, fileTraceConfigFile,
							fileTraceClient, fileTraceClientHdr, fileTraceClientBody,
							fileTraceServer, fileTraceServerHdr, fileTraceServerBody,
							tracciamentoCompatibilitaFiltroEsiti);
				
				}
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

				// Set First is false
				porteApplicativeHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
				// Set Tipo Operazione
				porteApplicativeHelper.addToDatiHiddenParameter(dati, ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE, tipoConfigurazione);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA,
						ForwardParams.OTHER(""));
			}

			// perform update
			
			
			if(tipoConfigurazione==null || StringUtils.isEmpty(tipoConfigurazione) 
					|| 
					ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO.equals(tipoConfigurazione)) {
			
				// Cambio i dati della vecchia CorrelazioneApplicativa
				// Non ne creo una nuova, altrimenti mi perdo le vecchie entry
				if (ca != null)
					ca.setScadenza(scadcorr);
				pde.setCorrelazioneApplicativa(ca);
				
				PorteApplicativeUtilities.initTracciamento(pde, porteApplicativeCore, config,
						tracciamentoStato, statoDiagnostici, severita);
				
			}
			else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PORTA.equals(tipoConfigurazione)) {
				
				PorteApplicativeUtilities.setTracciamentoTransazioni(pde, porteApplicativeCore,
						dbStato,
						dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
						dbFiltroEsiti,
						fsStato,
						fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
						fsFiltroEsiti,
						nuovaConfigurazioneEsiti,
						transazioniTempiElaborazione, transazioniToken,
						fileTraceStato, fileTraceConfigFile,
						fileTraceClient, fileTraceClientHdr, fileTraceClientBody,
						fileTraceServer, fileTraceServerHdr, fileTraceServerBody);
			}

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pde);


			// preparo i campi
			List<DataElement> dati = new ArrayList<>();

			// Aggiorno valori
			pde = porteApplicativeCore.getPortaApplicativa(idInt);

			if(pde.getTracciamento()!=null && pde.getTracciamento().getStato()!=null && StatoFunzionalita.ABILITATO.equals(pde.getTracciamento().getStato())) {
				oldTracciamentoStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
			}
			else {
				oldTracciamentoStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
			}
			
			if(tipoConfigurazione==null || StringUtils.isEmpty(tipoConfigurazione) 
					|| 
					ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO.equals(tipoConfigurazione)) {
			
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

				porteApplicativeHelper.addToDatiTracciamentoTransazioni(dati, TipoOperazione.OTHER,
						tracciamentoStato, 
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, listParameter,
						oldTracciamentoStato);
				
				porteApplicativeHelper.addPortaSeveritaMessaggiDiagnosticiToDati(statoDiagnostici, severita, dati);
				
				dati = porteApplicativeHelper.addCorrelazioneApplicativaToDati(dati, false, riusoID, scadcorr, urlRichiesta.getValue(), urlRisposta.getValue(), contaListe, numCorrelazioneReq, numCorrelazioneRes);

			}
			else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PORTA.equals(tipoConfigurazione)) {
				porteApplicativeHelper.addToDatiConfigurazioneAvanzataTracciamento(dati, TipoOperazione.OTHER,
						dbStato,
						dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
						dbFiltroEsiti,
						fsStato,
						fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
						fsFiltroEsiti,
						true);
				
				TracciamentoConfigurazione database = porteApplicativeCore.buildTracciamentoConfigurazioneDatabase(dbStato,
						dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
						dbFiltroEsiti);
				TracciamentoConfigurazione filetrace = porteApplicativeCore.buildTracciamentoConfigurazioneFiletrace(fsStato,
						fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
						fsFiltroEsiti);
				TracciamentoCompatibilitaFiltroEsiti tracciamentoCompatibilitaFiltroEsiti = new TracciamentoCompatibilitaFiltroEsiti(database, filetrace);
			
				porteApplicativeHelper.addToDatiRegistrazioneEsiti(dati, TipoOperazione.OTHER, 
						nuovaConfigurazioneEsiti, 
						selectAll,
						tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
						tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataScartate, 
						tracciamentoEsitiSelezionePersonalizzataRateLimiting, tracciamentoEsitiSelezionePersonalizzataMax, tracciamentoEsitiSelezionePersonalizzataCors,
						tracciamentoCompatibilitaFiltroEsiti); 

				porteApplicativeHelper.addToDatiRegistrazioneTransazione(dati, TipoOperazione.OTHER, 
						transazioniTempiElaborazione, transazioniToken); 
				
				porteApplicativeHelper.addToDatiRegistrazioneConfigurazioneFileTrace(dati, 
						fileTraceStato, fileTraceConfigFile,
						fileTraceClient, fileTraceClientHdr, fileTraceClientBody,
						fileTraceServer, fileTraceServerHdr, fileTraceServerBody,
						tracciamentoCompatibilitaFiltroEsiti);
			
			}
			
			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

			// Set First is false
			porteApplicativeHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
			// Set Tipo Operazione
			porteApplicativeHelper.addToDatiHiddenParameter(dati, ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE, tipoConfigurazione);

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
