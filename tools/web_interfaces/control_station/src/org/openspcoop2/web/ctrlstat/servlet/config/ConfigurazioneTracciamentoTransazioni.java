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
package org.openspcoop2.web.ctrlstat.servlet.config;

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
import org.openspcoop2.core.config.ConfigurazioneTracciamentoPorta;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.TracciamentoConfigurazione;
import org.openspcoop2.core.config.TracciamentoConfigurazioneFiletrace;
import org.openspcoop2.core.config.TracciamentoConfigurazioneFiletraceConnector;
import org.openspcoop2.core.config.Transazioni;
import org.openspcoop2.core.config.constants.Severita;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.utils.TracciamentoCompatibilitaFiltroEsiti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiConfigUtils;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ConfigurazioneTracciamentoTransazioni
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConfigurazioneTracciamentoTransazioni extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		TipoOperazione tipoOperazione = TipoOperazione.OTHER;
		
		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);
			
			// Preparo il menu
			confHelper.makeMenu();
			
			Boolean contaListe = ServletUtils.getContaListeFromSession(session);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
			Configurazione oldConfigurazione = confCore.getConfigurazioneGenerale();
			
			// Stato [si usa per capire se sono entrato per la prima volta nella schermata]		
			boolean first = confHelper.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
			
			String tipoConfigurazione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE);
			
			String severita = null;
			String severitaLog4j = null;
			String registrazioneTracce = null;
			
			ConfigurazioneTracciamentoPorta oldConfigurazioneTracciamento = null;
			
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
			
			String dumpApplicativo = null;
			String dumpPD = null;
			String dumpPA = null;
			
			if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO.equals(tipoConfigurazione)) {
				
				severita = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
				severitaLog4j = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
				registrazioneTracce = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE);
				
			}
			else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PD.equals(tipoConfigurazione)
					||
					ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PA.equals(tipoConfigurazione)) {
				
				dbStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO);
				dbStatoReqIn = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_REQ_IN);
				dbStatoReqOut = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_REQ_OUT);
				dbStatoResOut = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_RES_OUT);
				dbStatoResOutComplete = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_RES_OUT_COMPLETE);
				String dbFiltroEsitiParam = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_FILTRA_ESITI);
				dbFiltroEsiti = ServletUtils.isCheckBoxEnabled(dbFiltroEsitiParam);
				
				fsStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO);
				fsStatoReqIn = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_REQ_IN);
				fsStatoReqOut = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_REQ_OUT);
				fsStatoResOut = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_RES_OUT);
				fsStatoResOutComplete = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_RES_OUT_COMPLETE);
				String fsFiltroEsitiParam = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_FILTRA_ESITI);
				fsFiltroEsiti = ServletUtils.isCheckBoxEnabled(fsFiltroEsitiParam);
				
				if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PD.equals(tipoConfigurazione)) {
					oldConfigurazioneTracciamento = oldConfigurazione.getTracciamento().getPortaDelegata();
				}
				else {
					oldConfigurazioneTracciamento = oldConfigurazione.getTracciamento().getPortaApplicativa();
				}
				nuovaConfigurazioneEsiti = confHelper.readConfigurazioneRegistrazioneEsitiFromHttpParameters(oldConfigurazioneTracciamento!=null ? oldConfigurazioneTracciamento.getEsiti() : null, first);
				
				tracciamentoEsitiSelezionePersonalizzataOk = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_OK);
				tracciamentoEsitiSelezionePersonalizzataFault = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FAULT);
				tracciamentoEsitiSelezionePersonalizzataFallite = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE);
				tracciamentoEsitiSelezionePersonalizzataScartate = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_SCARTATE);
				tracciamentoEsitiSelezionePersonalizzataRateLimiting = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_RATE_LIMITING);
				tracciamentoEsitiSelezionePersonalizzataMax = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_MAX_REQUEST);
				tracciamentoEsitiSelezionePersonalizzataCors = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_CORS);
				
				tracciamentoEsitiSelezionePersonalizzataAll = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_ALL);
				selectAll = ServletUtils.isCheckBoxEnabled(tracciamentoEsitiSelezionePersonalizzataAll);
				
				transazioniTempiElaborazione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRANSAZIONE_TEMPI);
				transazioniToken = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRANSAZIONE_TOKEN);
				
				fileTraceStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_STATO);
				fileTraceConfigFile = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_CONFIGURAZIONE);
				fileTraceClient = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_CLIENT);
				fileTraceClientHdr = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_CLIENT_HEADER);
				fileTraceClientBody = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_CLIENT_PAYLOAD);
				fileTraceServer = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_SERVER);
				fileTraceServerHdr = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_SERVER_HEADER);
				fileTraceServerBody = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_FILETRACE_SERVER_PAYLOAD);
			}
			else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_REGISTRAZIONE_MESSAGGI.equals(tipoConfigurazione)) {
				
				dumpApplicativo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_APPLICATIVO);
				dumpPD = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD);
				dumpPA = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA);
			}
			
					
			
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<>();
			Parameter pMenu = null;
			String servlet = null;
			if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PD.equals(tipoConfigurazione)
						||
						ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PA.equals(tipoConfigurazione)) {
				pMenu = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE, ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO);
				servlet = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI;
			}
			if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_REGISTRAZIONE_MESSAGGI.equals(tipoConfigurazione)) {
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_MESSAGGI, null));
			}
			else {
				if(pMenu!=null) {
					lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, servlet, pMenu));
				}
				else {
					lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, null));
				}
			}
			
			if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PA.equals(tipoConfigurazione)){
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO_CONFIGURAZIONE_EROGAZIONI, null));
			}
			else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PD.equals(tipoConfigurazione)){
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO_CONFIGURAZIONE_FRUIZIONI, null));
			}
			
			
			// edit in progress
			if (confHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO.equals(tipoConfigurazione)) {
					if(severita == null) {
						if(oldConfigurazione.getMessaggiDiagnostici().getSeverita()!=null)
							severita = oldConfigurazione.getMessaggiDiagnostici().getSeverita().toString();
						if(oldConfigurazione.getMessaggiDiagnostici().getSeveritaLog4j()!=null)
							severitaLog4j = oldConfigurazione.getMessaggiDiagnostici().getSeveritaLog4j().toString();
						if(oldConfigurazione.getTracciamento().getStato()!=null)
							registrazioneTracce = oldConfigurazione.getTracciamento().getStato().toString();
					}
				}
				else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PD.equals(tipoConfigurazione)
						||
						ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PA.equals(tipoConfigurazione)) {
					
					boolean initDB = false;
					boolean initFileTrace = false;
					if(first && oldConfigurazioneTracciamento!=null) {
													
						if(oldConfigurazioneTracciamento.getDatabase()!=null) {
							dbStato = (oldConfigurazioneTracciamento.getDatabase().getStato()!=null) ? oldConfigurazioneTracciamento.getDatabase().getStato().getValue() : 
								ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO;
							dbStatoReqIn = (oldConfigurazioneTracciamento.getDatabase().getRequestIn()!=null) ? oldConfigurazioneTracciamento.getDatabase().getRequestIn().getValue() : 
								ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_REQ_IN;
							dbStatoReqOut = (oldConfigurazioneTracciamento.getDatabase().getRequestOut()!=null) ? oldConfigurazioneTracciamento.getDatabase().getRequestOut().getValue() : 
								ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_REQ_OUT;
							dbStatoResOut = (oldConfigurazioneTracciamento.getDatabase().getResponseOut()!=null) ? oldConfigurazioneTracciamento.getDatabase().getResponseOut().getValue() : 
								ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_RES_OUT;
							dbStatoResOutComplete = (oldConfigurazioneTracciamento.getDatabase().getResponseOutComplete()!=null) ? oldConfigurazioneTracciamento.getDatabase().getResponseOutComplete().getValue() : 
								ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_STATO_RES_OUT_COMPLETE;
							dbFiltroEsiti = (oldConfigurazioneTracciamento.getDatabase().getFiltroEsiti()!=null) ? StatoFunzionalita.ABILITATO.equals(oldConfigurazioneTracciamento.getDatabase().getFiltroEsiti()) : 
								ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_DATABASE_FILTRA_ESITI;
							initDB=true;
						}
						
						if(oldConfigurazioneTracciamento.getFiletrace()!=null) {
							fsStato = (oldConfigurazioneTracciamento.getFiletrace().getStato()!=null) ? oldConfigurazioneTracciamento.getFiletrace().getStato().getValue() : 
								ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO;
							fsStatoReqIn = (oldConfigurazioneTracciamento.getFiletrace().getRequestIn()!=null) ? oldConfigurazioneTracciamento.getFiletrace().getRequestIn().getValue() : 
								ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_REQ_IN;
							fsStatoReqOut = (oldConfigurazioneTracciamento.getFiletrace().getRequestOut()!=null) ? oldConfigurazioneTracciamento.getFiletrace().getRequestOut().getValue() : 
								ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_REQ_OUT;
							fsStatoResOut = (oldConfigurazioneTracciamento.getFiletrace().getResponseOut()!=null) ? oldConfigurazioneTracciamento.getFiletrace().getResponseOut().getValue() : 
								ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_RES_OUT;
							fsStatoResOutComplete = (oldConfigurazioneTracciamento.getFiletrace().getResponseOutComplete()!=null) ? oldConfigurazioneTracciamento.getFiletrace().getResponseOutComplete().getValue() : 
								ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_AVANZATA_TRACCIAMENTO_FILETRACE_STATO_RES_OUT_COMPLETE;
							fsFiltroEsiti = (oldConfigurazioneTracciamento.getFiletrace().getFiltroEsiti()!=null) ? StatoFunzionalita.ABILITATO.equals(oldConfigurazioneTracciamento.getFiletrace().getFiltroEsiti()) : 
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
						List<Integer> listOk = confHelper.getListaEsitiOkSenzaCors(esiti);
						if(confHelper.isCompleteEnabled(attivi, listOk)) {
							tracciamentoEsitiSelezionePersonalizzataOk = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
							isOkTotale = true;
						}
						else if(confHelper.isCompleteDisabled(attivi, listOk)) {
							tracciamentoEsitiSelezionePersonalizzataOk = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
						}
						else {
							tracciamentoEsitiSelezionePersonalizzataOk = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
						}
						
						boolean isFaultTotale = false;
						List<Integer> listFault = esiti.getEsitiCodeFaultApplicativo();
						if(confHelper.isCompleteEnabled(attivi, listFault)) {
							tracciamentoEsitiSelezionePersonalizzataFault = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
							isFaultTotale = true;
						}
						else if(confHelper.isCompleteDisabled(attivi, listFault)) {
							tracciamentoEsitiSelezionePersonalizzataFault = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
						}
						else {
							tracciamentoEsitiSelezionePersonalizzataFault = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
						}
						
						boolean isFalliteSenzaMaxThreadsScartateTotale = false;
						List<Integer> listFalliteSenzaMaxThreadsScartate = confHelper.getListaEsitiFalliteSenza_RateLimiting_MaxThreads_Scartate(esiti);
						if(confHelper.isCompleteEnabled(attivi, listFalliteSenzaMaxThreadsScartate)) {
							tracciamentoEsitiSelezionePersonalizzataFallite = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
							isFalliteSenzaMaxThreadsScartateTotale = true;
						}
						else if(confHelper.isCompleteDisabled(attivi, listFalliteSenzaMaxThreadsScartate)) {
							tracciamentoEsitiSelezionePersonalizzataFallite = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
						}
						else {
							tracciamentoEsitiSelezionePersonalizzataFallite = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
						}
						
						boolean isScartateTotale = false;
						List<Integer> listScartate = esiti.getEsitiCodeRichiestaScartate();
						if(confHelper.isCompleteEnabled(attivi, listScartate)) {
							tracciamentoEsitiSelezionePersonalizzataScartate = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
							isScartateTotale = true;
						}
						else if(confHelper.isCompleteDisabled(attivi, listScartate)) {
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
						List<Integer> listCors = confHelper.getListaEsitiCors(esiti);
						if(confHelper.isCompleteEnabled(attivi, listCors)) {
							tracciamentoEsitiSelezionePersonalizzataCors = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
							isCorsTotale = true;
						}
						else if(confHelper.isCompleteDisabled(attivi, listCors)) {
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
						oldConfigurazioneTracciamento!=null && oldConfigurazioneTracciamento.getTransazioni()!=null) {
						if(oldConfigurazioneTracciamento.getTransazioni().getTempiElaborazione()!=null) {
							transazioniTempiElaborazione = oldConfigurazioneTracciamento.getTransazioni().getTempiElaborazione().toString();
						}
						if(oldConfigurazioneTracciamento.getTransazioni().getToken()!=null) {
							transazioniToken = oldConfigurazioneTracciamento.getTransazioni().getToken().toString();
						}
					}
					
					if( ConfigurazioneCostanti.PARAMETRO_FILETRACE_STATO.equals(confHelper.getPostBackElementName())) {
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
							oldConfigurazioneTracciamento!=null) {
						if(oldConfigurazioneTracciamento.getFiletraceConfig()!=null) {
							fileTraceStato = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
							fileTraceConfigFile = oldConfigurazioneTracciamento.getFiletraceConfig().getConfig();
							if(oldConfigurazioneTracciamento.getFiletraceConfig().getDumpIn()!=null) {
								fileTraceClient = StatoFunzionalita.ABILITATO.equals(oldConfigurazioneTracciamento.getFiletraceConfig().getDumpIn().getStato()) ? 
										ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO : ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
								fileTraceClientHdr = StatoFunzionalita.ABILITATO.equals(oldConfigurazioneTracciamento.getFiletraceConfig().getDumpIn().getHeader()) ? 
										ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO : ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
								fileTraceClientBody = StatoFunzionalita.ABILITATO.equals(oldConfigurazioneTracciamento.getFiletraceConfig().getDumpIn().getPayload()) ? 
										ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO : ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
							}
							else {
								fileTraceClient = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
							}
							if(oldConfigurazioneTracciamento.getFiletraceConfig().getDumpOut()!=null) {
								fileTraceServer = StatoFunzionalita.ABILITATO.equals(oldConfigurazioneTracciamento.getFiletraceConfig().getDumpOut().getStato()) ? 
										ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO : ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
								fileTraceServerHdr = StatoFunzionalita.ABILITATO.equals(oldConfigurazioneTracciamento.getFiletraceConfig().getDumpOut().getHeader()) ? 
										ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO : ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
								fileTraceServerBody = StatoFunzionalita.ABILITATO.equals(oldConfigurazioneTracciamento.getFiletraceConfig().getDumpOut().getPayload()) ? 
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
				else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_REGISTRAZIONE_MESSAGGI.equals(tipoConfigurazione)) {
					if(first &&
						oldConfigurazione.getDump().getStato()!=null) {
						dumpApplicativo = oldConfigurazione.getDump().getStato().toString();
					}
					if(first) {
						if(oldConfigurazione.getDump().getDumpBinarioPortaDelegata()!=null)
							dumpPD = oldConfigurazione.getDump().getDumpBinarioPortaDelegata().toString();
						if(oldConfigurazione.getDump().getDumpBinarioPortaApplicativa()!=null)
							dumpPA = oldConfigurazione.getDump().getDumpBinarioPortaApplicativa().toString();
					}
				}
				

				
				
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
				// Set Tipo Operazione
				confHelper.addToDatiHiddenParameter(dati, ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE, tipoConfigurazione);
				
				if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO.equals(tipoConfigurazione)) {
					
					confHelper.addConfigurazioneTracciamentoToDati(dati);
					
					confHelper.addMessaggiDiagnosticiToDati(severita, severitaLog4j, oldConfigurazione, dati, contaListe);

					confHelper.addTracciamentoToDati(registrazioneTracce, oldConfigurazione, dati, contaListe);
					
				}
				else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PD.equals(tipoConfigurazione)
						||
						ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PA.equals(tipoConfigurazione)) {
					
					confHelper.addToDatiConfigurazioneAvanzataTracciamento(dati, tipoOperazione,
							dbStato,
							dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
							dbFiltroEsiti,
							fsStato,
							fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
							fsFiltroEsiti,
							false);
					
					TracciamentoConfigurazione database = confCore.buildTracciamentoConfigurazioneDatabase(dbStato,
							dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
							dbFiltroEsiti);
					TracciamentoConfigurazione filetrace = confCore.buildTracciamentoConfigurazioneFiletrace(fsStato,
							fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
							fsFiltroEsiti);
					TracciamentoCompatibilitaFiltroEsiti tracciamentoCompatibilitaFiltroEsiti = new TracciamentoCompatibilitaFiltroEsiti(database, filetrace);
					
					confHelper.addToDatiRegistrazioneEsiti(dati, tipoOperazione, nuovaConfigurazioneEsiti, 
							selectAll,
							tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
							tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataScartate,  
							tracciamentoEsitiSelezionePersonalizzataRateLimiting, tracciamentoEsitiSelezionePersonalizzataMax, tracciamentoEsitiSelezionePersonalizzataCors,
							tracciamentoCompatibilitaFiltroEsiti); 
					
					confHelper.addToDatiRegistrazioneTransazione(dati, tipoOperazione, 
							transazioniTempiElaborazione, transazioniToken); 
					
					confHelper.addToDatiRegistrazioneConfigurazioneFileTrace(dati, 
							fileTraceStato, fileTraceConfigFile,
							fileTraceClient, fileTraceClientHdr, fileTraceClientBody,
							fileTraceServer, fileTraceServerHdr, fileTraceServerBody,
							tracciamentoCompatibilitaFiltroEsiti);
					
				}
				else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_REGISTRAZIONE_MESSAGGI.equals(tipoConfigurazione)) {
					
					confHelper.addRegistrazioneMessaggiToDati(dumpApplicativo, dumpPD, dumpPA, oldConfigurazione, dati, contaListe);
				
				}
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI,	ForwardParams.OTHER(""));
			}
			
			// Controlli sui campi immessi
			boolean isOk = confHelper.checkConfigurazioneTracciamento(tipoOperazione, nuovaConfigurazioneEsiti, tipoConfigurazione);
			if (!isOk) {
				
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
				// Set Tipo Operazione
				confHelper.addToDatiHiddenParameter(dati, ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE, tipoConfigurazione);
				
				if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO.equals(tipoConfigurazione)) {
					
					confHelper.addConfigurazioneTracciamentoToDati(dati);
					
					confHelper.addMessaggiDiagnosticiToDati(severita, severitaLog4j, oldConfigurazione, dati, contaListe);

					confHelper.addTracciamentoToDati(registrazioneTracce, oldConfigurazione, dati, contaListe);
					
				}
				else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PD.equals(tipoConfigurazione)
						||
						ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PA.equals(tipoConfigurazione)) {
					
					confHelper.addToDatiConfigurazioneAvanzataTracciamento(dati, tipoOperazione,
							dbStato,
							dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
							dbFiltroEsiti,
							fsStato,
							fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
							fsFiltroEsiti,
							false);
					
					TracciamentoConfigurazione database = confCore.buildTracciamentoConfigurazioneDatabase(dbStato,
							dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
							dbFiltroEsiti);
					TracciamentoConfigurazione filetrace = confCore.buildTracciamentoConfigurazioneFiletrace(fsStato,
							fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
							fsFiltroEsiti);
					TracciamentoCompatibilitaFiltroEsiti tracciamentoCompatibilitaFiltroEsiti = new TracciamentoCompatibilitaFiltroEsiti(database, filetrace);
					
					confHelper.addToDatiRegistrazioneEsiti(dati, tipoOperazione, nuovaConfigurazioneEsiti, 
							selectAll,
							tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
							tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataScartate,  
							tracciamentoEsitiSelezionePersonalizzataRateLimiting, tracciamentoEsitiSelezionePersonalizzataMax, tracciamentoEsitiSelezionePersonalizzataCors,
							tracciamentoCompatibilitaFiltroEsiti); 
					
					confHelper.addToDatiRegistrazioneTransazione(dati, tipoOperazione, 
							transazioniTempiElaborazione, transazioniToken); 
					
					confHelper.addToDatiRegistrazioneConfigurazioneFileTrace(dati, 
							fileTraceStato, fileTraceConfigFile,
							fileTraceClient, fileTraceClientHdr, fileTraceClientBody,
							fileTraceServer, fileTraceServerHdr, fileTraceServerBody,
							tracciamentoCompatibilitaFiltroEsiti);
							
				}
				else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_REGISTRAZIONE_MESSAGGI.equals(tipoConfigurazione)) {
					
					confHelper.addRegistrazioneMessaggiToDati(dumpApplicativo, dumpPD, dumpPA, oldConfigurazione, dati, contaListe); 
				
				}
				
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI,	ForwardParams.OTHER(""));
			}
			
			Configurazione newConfigurazione = confCore.getConfigurazioneGenerale();
			
			if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO.equals(tipoConfigurazione)) {
				
				if (newConfigurazione.getMessaggiDiagnostici() != null) {
					newConfigurazione.getMessaggiDiagnostici().setSeverita(Severita.toEnumConstant(severita));
					newConfigurazione.getMessaggiDiagnostici().setSeveritaLog4j(Severita.toEnumConstant(severitaLog4j));
				} else {
					MessaggiDiagnostici md = new MessaggiDiagnostici();
					md.setSeverita(Severita.toEnumConstant(severita));
					md.setSeveritaLog4j(Severita.toEnumConstant(severitaLog4j));
					newConfigurazione.setMessaggiDiagnostici(md);
				}
				
				if (newConfigurazione.getTracciamento() != null) {
					newConfigurazione.getTracciamento().setStato(StatoFunzionalita.toEnumConstant(registrazioneTracce));
				} else {
					Tracciamento t = new Tracciamento();
					t.setStato(StatoFunzionalita.toEnumConstant(registrazioneTracce));
					newConfigurazione.setTracciamento(t);
				}
				
			}
			else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PD.equals(tipoConfigurazione)
					||
					ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PA.equals(tipoConfigurazione)) {
				
				ConfigurazioneTracciamentoPorta porta = null;
				if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PD.equals(tipoConfigurazione)) {
					porta = newConfigurazione.getTracciamento().getPortaDelegata();
				}
				else {
					porta = newConfigurazione.getTracciamento().getPortaApplicativa();
				}
				if(porta==null) {
					porta = new ConfigurazioneTracciamentoPorta();
					if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PD.equals(tipoConfigurazione)) {
						newConfigurazione.getTracciamento().setPortaDelegata(porta);
					}
					else {
						newConfigurazione.getTracciamento().setPortaApplicativa(porta);
					}
				}
				
				TracciamentoConfigurazione database = confCore.buildTracciamentoConfigurazioneDatabase(dbStato,
						dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
						dbFiltroEsiti);
				porta.setDatabase(database);
				
				TracciamentoConfigurazione filetrace = confCore.buildTracciamentoConfigurazioneFiletrace(fsStato,
						fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
						fsFiltroEsiti);
				porta.setFiletrace(filetrace);
				
				if(StringUtils.isEmpty(nuovaConfigurazioneEsiti)) {
					porta.setEsiti(EsitiConfigUtils.TUTTI_ESITI_DISABILITATI+"");
				}
				else {
					porta.setEsiti(nuovaConfigurazioneEsiti);
				}
				
				if(porta.getTransazioni()==null) {
					porta.setTransazioni( new Transazioni() );
				}
				porta.getTransazioni().setTempiElaborazione(StatoFunzionalita.toEnumConstant(transazioniTempiElaborazione));
				porta.getTransazioni().setToken(StatoFunzionalita.toEnumConstant(transazioniToken));
				
				if(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO.equals(fileTraceStato)) {
					porta.setFiletraceConfig(new TracciamentoConfigurazioneFiletrace());
					porta.getFiletraceConfig().setConfig(fileTraceConfigFile);
					
					porta.getFiletraceConfig().setDumpIn(new TracciamentoConfigurazioneFiletraceConnector());			
					porta.getFiletraceConfig().getDumpIn().setStato(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(fileTraceClient) ?
							StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
					porta.getFiletraceConfig().getDumpIn().setHeader(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(fileTraceClientHdr) ?
							StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
					porta.getFiletraceConfig().getDumpIn().setPayload(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(fileTraceClientBody) ?
							StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
					
					porta.getFiletraceConfig().setDumpOut(new TracciamentoConfigurazioneFiletraceConnector());			
					porta.getFiletraceConfig().getDumpOut().setStato(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(fileTraceServer) ?
							StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
					porta.getFiletraceConfig().getDumpOut().setHeader(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(fileTraceServerHdr) ?
							StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
					porta.getFiletraceConfig().getDumpOut().setPayload(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(fileTraceServerBody) ?
							StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
				}
				else {
					porta.setFiletraceConfig(null);
				}
				
			}
			else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_REGISTRAZIONE_MESSAGGI.equals(tipoConfigurazione)) {
				
				if (newConfigurazione.getDump() != null) {
					newConfigurazione.getDump().setStato(StatoFunzionalita.toEnumConstant(dumpApplicativo));
					newConfigurazione.getDump().setDumpBinarioPortaDelegata(StatoFunzionalita.toEnumConstant(dumpPD));
					newConfigurazione.getDump().setDumpBinarioPortaApplicativa(StatoFunzionalita.toEnumConstant(dumpPA));
				} else {
					Dump d = new Dump();
					d.setStato(StatoFunzionalita.toEnumConstant(dumpApplicativo));
					d.setDumpBinarioPortaDelegata(StatoFunzionalita.toEnumConstant(dumpPD));
					d.setDumpBinarioPortaApplicativa(StatoFunzionalita.toEnumConstant(dumpPA));
					newConfigurazione.setDump(d);
				}
				
				if(StatoFunzionalita.DISABILITATO.equals(newConfigurazione.getDump().getStato())) {
					newConfigurazione.getDump().setConfigurazionePortaApplicativa(null);
					newConfigurazione.getDump().setConfigurazionePortaDelegata(null);
				}
				
			}
			
			
			confCore.performUpdateOperation(userLogin, confHelper.smista(), newConfigurazione);

			// Preparo la lista
			if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_REGISTRAZIONE_MESSAGGI.equals(tipoConfigurazione)) {
				pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DUMP_CONFIGURAZIONE_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			}
			else {
				pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO_ESITI_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			}
			
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// preparo i campi
			List<DataElement> dati = new ArrayList<>();
			
			// ricarico la configurazione
			newConfigurazione = confCore.getConfigurazioneGenerale();
			
			if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO.equals(tipoConfigurazione)) {
				
				if(newConfigurazione.getMessaggiDiagnostici().getSeverita()!=null)
					severita = newConfigurazione.getMessaggiDiagnostici().getSeverita().toString();
				if(newConfigurazione.getMessaggiDiagnostici().getSeveritaLog4j()!=null)
					severitaLog4j = newConfigurazione.getMessaggiDiagnostici().getSeveritaLog4j().toString();
				if(newConfigurazione.getTracciamento().getStato()!=null)
					registrazioneTracce = newConfigurazione.getTracciamento().getStato().toString();
				
			}
			else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PD.equals(tipoConfigurazione)
					||
					ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PA.equals(tipoConfigurazione)) {
				
				if(newConfigurazione.getTransazioni()!=null) {
					if(newConfigurazione.getTransazioni().getTempiElaborazione()!=null) {
						transazioniTempiElaborazione = newConfigurazione.getTransazioni().getTempiElaborazione().toString();
					}
					if(newConfigurazione.getTransazioni().getToken()!=null) {
						transazioniToken = newConfigurazione.getTransazioni().getToken().toString();
					}
				}
				
			}
			else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_REGISTRAZIONE_MESSAGGI.equals(tipoConfigurazione)) {
				
				if(newConfigurazione.getDump().getStato()!=null)
					dumpApplicativo = newConfigurazione.getDump().getStato().toString();
				if(newConfigurazione.getDump().getDumpBinarioPortaDelegata()!=null)
					dumpPD = newConfigurazione.getDump().getDumpBinarioPortaDelegata().toString();
				if(newConfigurazione.getDump().getDumpBinarioPortaApplicativa()!=null)
					dumpPA = newConfigurazione.getDump().getDumpBinarioPortaApplicativa().toString();
				
			}

			
			// Set First is false
			confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
			// Set Tipo Operazione
			confHelper.addToDatiHiddenParameter(dati, ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE, tipoConfigurazione);
			
			if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO.equals(tipoConfigurazione)) {
				
				confHelper.addConfigurazioneTracciamentoToDati(dati);
				
				confHelper.addMessaggiDiagnosticiToDati(severita, severitaLog4j, newConfigurazione, dati, contaListe);

				confHelper.addTracciamentoToDati(registrazioneTracce, newConfigurazione, dati, contaListe);
				
			}
			else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PD.equals(tipoConfigurazione)
					||
					ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_TRACCIAMENTO_PA.equals(tipoConfigurazione)) {
			
				confHelper.addToDatiConfigurazioneAvanzataTracciamento(dati, tipoOperazione,
						dbStato,
						dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
						dbFiltroEsiti,
						fsStato,
						fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
						fsFiltroEsiti,
						false);
				
				TracciamentoConfigurazione database = confCore.buildTracciamentoConfigurazioneDatabase(dbStato,
						dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
						dbFiltroEsiti);
				TracciamentoConfigurazione filetrace = confCore.buildTracciamentoConfigurazioneFiletrace(fsStato,
						fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
						fsFiltroEsiti);
				TracciamentoCompatibilitaFiltroEsiti tracciamentoCompatibilitaFiltroEsiti = new TracciamentoCompatibilitaFiltroEsiti(database, filetrace);
				
				confHelper.addToDatiRegistrazioneEsiti(dati, tipoOperazione, newConfigurazione.getTracciamento().getEsiti(), 
						selectAll,
						tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
						tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataScartate,  
						tracciamentoEsitiSelezionePersonalizzataRateLimiting, tracciamentoEsitiSelezionePersonalizzataMax, tracciamentoEsitiSelezionePersonalizzataCors,
						tracciamentoCompatibilitaFiltroEsiti); 
				
				confHelper.addToDatiRegistrazioneTransazione(dati, tipoOperazione, 
						transazioniTempiElaborazione, transazioniToken); 
				
				confHelper.addToDatiRegistrazioneConfigurazioneFileTrace(dati, 
						fileTraceStato, fileTraceConfigFile,
						fileTraceClient, fileTraceClientHdr, fileTraceClientBody,
						fileTraceServer, fileTraceServerHdr, fileTraceServerBody,
						tracciamentoCompatibilitaFiltroEsiti);
				
			}
			else if(ConfigurazioneCostanti.VALORE_PARAMETRO_CONFIGURAZIONE_TIPO_OPERAZIONE_REGISTRAZIONE_MESSAGGI.equals(tipoConfigurazione)) {
			
				confHelper.addRegistrazioneMessaggiToDati(dumpApplicativo, dumpPD, dumpPA, newConfigurazione, dati, contaListe);
				
			}
			
			
			
			pd.setDati(dati);
			pd.disableEditMode();

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI, ForwardParams.OTHER(""));
		}
	}
}
