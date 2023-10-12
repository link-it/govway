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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.monitor.engine.alarm.AlarmEngineConfig;
import org.openspcoop2.monitor.engine.alarm.utils.AllarmiUtils;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.Dialog;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConfigurazioneSistemaAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneSistemaAdd extends Action {



	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		
		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);

			// Preparo il menu
			confHelper.makeMenu();

			// Prendo il nome della porta
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Prendo la lista di aliases
			List<String> aliases = confCore.getJmxPdDAliases();
			if(aliases==null || aliases.isEmpty()){
				throw new DriverControlStationException("Pagina non prevista, la sezione configurazione non permette di accedere a questa pagina, se la configurazione non e' corretta");
			}
			
			String [] aliasNodi = confHelper.getParameterValues(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODI_CLUSTER);
			String alias = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
			String nomeCache = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_CACHE);
			String nomeMetodo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO);

			if(
				(alias==null || "".equals(alias))
				&&
				(aliases.size()>1)
			){
					
				boolean error = false;
				
				if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_ALL_CACHES.equals(nomeCache) &&
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO_RESET_ALL_CACHE_ALL_NODES.equals(nomeMetodo)) {
					// nessun vincolo
				}
				else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_SELECTED_CACHES.equals(nomeCache) &&
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO_RESET_ALL_CACHE_SELECTED_NODES.equals(nomeMetodo)) {
					if((aliasNodi==null || aliasNodi.length<=0)) {
						pd.setMessage("L'operazione richiesta richiede la selezione di almeno un "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER, MessageType.ERROR);
						error=true;
					}
				}
				else {
					if(!confHelper.isEditModeInProgress()) {
						if((aliasNodi==null || aliasNodi.length<=0)) {
							pd.setMessage("L'operazione richiesta richiede la selezione di un "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER, MessageType.ERROR);
							error=true;
						}
						else if(aliasNodi.length>1) {
							pd.setMessage("L'operazione richiesta richiede la selezione di un solo "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER+"; ne sono stati selezionati "+aliasNodi.length, MessageType.ERROR);
							error=true;
						}
						else {
							alias = aliasNodi[0];
						}
					}
				}

				if(error) {
					// setto la barra del titolo
					List<Parameter> lstParam = new ArrayList<>();

					lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME, 
							 null));

					ServletUtils.setPageDataTitle(pd, lstParam);
					
					// preparo i campi
					List<DataElement> dati = new ArrayList<>();
					dati.add(ServletUtils.getDataElementForEditModeFinished());
					
					pd.setLabelBottoneInvia(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME_ACCEDI);
					
					dati = confHelper.addConfigurazioneSistemaSelectListNodiCluster(dati, aliasNodi);
					
					pd.setDati(dati);
					
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
		
					return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
							ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_SISTEMA, 
							ForwardParams.ADD());
				}
					
			}
			
			
			String nomeParametroPostBack = confHelper.getPostBackElementName();
			
			
			if(alias==null || "".equals(alias)){
				
				if(aliases.size()==1){
					alias = aliases.get(0);
				}
				else{
				
					List<String> aliasesForResetAllCaches = null;
					
					if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_ALL_CACHES.equals(nomeCache) &&
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO_RESET_ALL_CACHE_ALL_NODES.equals(nomeMetodo)) {
						aliasesForResetAllCaches = new ArrayList<>();
						aliasesForResetAllCaches.addAll(aliases);
						aliasNodi = null; // svuoto per non creare confusione
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_SELECTED_CACHES.equals(nomeCache) &&
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO_RESET_ALL_CACHE_SELECTED_NODES.equals(nomeMetodo)) {
						aliasesForResetAllCaches = new ArrayList<>();
						if(aliasNodi!=null && aliasNodi.length>0) {
							aliasesForResetAllCaches.addAll(Arrays.asList(aliasNodi));
						}
						aliasNodi = null; // svuoto
					}
					
					if(aliasesForResetAllCaches!=null && !aliasesForResetAllCaches.isEmpty()) {
						boolean rilevatoErrore = false;
						StringBuilder messagePerOperazioneEffettuataSB = new StringBuilder();
						int index = 0;
						for (String aliasForResetCache : aliasesForResetAllCaches) {
							StringBuilder bfExternal = new StringBuilder();
							String descrizione = confCore.getJmxPdDDescrizione(aliasForResetCache);
							if(index>0) {
								bfExternal.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							}
							bfExternal.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER).append(" ").append(descrizione).append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							List<String> caches = confCore.getJmxPdDCaches(aliasForResetCache);
							if(caches!=null && !caches.isEmpty()){
								
								Map<String, List<String>> esitiCache = new HashMap<>();
								/**StringBuilder bfCaches = new StringBuilder();*/
								for (String cache : caches) {
																		
									String stato = null;
									try{
										stato = confCore.getInvoker().readJMXAttribute(aliasForResetCache,confCore.getJmxPdDConfigurazioneSistemaType(aliasForResetCache), 
												cache,
												confCore.getJmxPdDCacheNomeAttributoCacheAbilitata(aliasForResetCache));
										if(stato.equalsIgnoreCase("true")){
											stato = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ABILITATA;
										}
										else if(stato.equalsIgnoreCase("false")){
											stato = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DISABILITATA;
										}
										else{
											throw new DriverControlStationException("Stato ["+stato+"] sconosciuto");
										}
									}catch(Exception e){
										ControlStationCore.logError("Errore durante la lettura dello stato della cache ["+cache+"](jmxResourcePdD) (node:"+aliasForResetCache+"): "+e.getMessage(),e);
										stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
										rilevatoErrore = true;
										
										/**if(bfCaches.length()>0){
											bfCaches.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
										}
										bfCaches.append("- Cache ["+cache+"]: "+stato);*/
										
										addEsito(esitiCache, stato, cache);
									}
									
									if(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ABILITATA.equals(stato)){
										/**if(bfCaches.length()>0){
											bfCaches.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
										}*/
										String result = null;
										try{
											String nomeMetodoResetCache = confCore.getJmxPdDCacheNomeMetodoResetCache(aliasForResetCache);
											result = confCore.getInvoker().invokeJMXMethod(aliasForResetCache,confCore.getJmxPdDCacheType(aliasForResetCache), 
													cache,
													nomeMetodoResetCache);
										}catch(Exception e){
											String errorMessage = getPrefixErrorInvokeMethod(nomeMetodo)+"sulla cache ["+nomeCache+"] (node:"+aliasForResetCache+"): "+e.getMessage();
											ControlStationCore.getLog().error(errorMessage,e);
											result = errorMessage;
											rilevatoErrore = true;
										}
										/**bfCaches.append("- Cache ["+cache+"]: "+result);*/
										
										addEsito(esitiCache, result, cache);
									}
									
								}
								/**bfExternal.append(bfCaches.toString());*/
								bfExternal.append(toString(esitiCache));
								
								if(messagePerOperazioneEffettuataSB.length()>0){
									messagePerOperazioneEffettuataSB.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								}
								messagePerOperazioneEffettuataSB.append(bfExternal.toString());
							
							}
							index++;
						}
						if(messagePerOperazioneEffettuataSB!=null){
							if(rilevatoErrore)
								pd.setMessage(messagePerOperazioneEffettuataSB.toString());
							else 
								pd.setMessage(messagePerOperazioneEffettuataSB.toString(),Costanti.MESSAGE_TYPE_INFO);
						}
					}
						
					// setto la barra del titolo
					List<Parameter> lstParam = new ArrayList<>();

					lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME, 
							 null));

					ServletUtils.setPageDataTitle(pd, lstParam);
					
					// preparo i campi
					List<DataElement> dati = new ArrayList<>();
					dati.add(ServletUtils.getDataElementForEditModeFinished());
					
					pd.setLabelBottoneInvia(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME_ACCEDI);
					
					dati = confHelper.addConfigurazioneSistemaSelectListNodiCluster(dati, aliasNodi);
					
					pd.setDati(dati);
					
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
		
					return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
							ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_SISTEMA, 
							ForwardParams.ADD());
					
				}
				
			}
			
						
			String descrizioneAlias = confCore.getJmxPdDDescrizione(alias);
		
			String messagePerOperazioneEffettuata = null;
			boolean rilevatoErrore = false;
			
			if(nomeCache!=null && !"".equals(nomeCache) &&
					nomeMetodo!=null && !"".equals(nomeMetodo) &&
					
					// fix per evitare che si ricordi l'all cache, dopo una successiva operazione
					(nomeParametroPostBack==null || "".equals(nomeParametroPostBack))
					
					){
				
				String nomeMetodoResetCache = confCore.getJmxPdDCacheNomeMetodoResetCache(alias);
				String nomeMetodoResetConnettoriPrioritari = confCore.getJmxPdDConfigurazioneSistemaNomeMetodoResetConnettoriPrioritari(alias);
				boolean resetMultiplo = false;
				boolean resetConnettoriPrioritari = false;
				if(nomeMetodoResetCache!=null && nomeMetodoResetCache.equals(nomeMetodo)){
					if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_ALL_CACHES.equals(nomeCache)){
						resetMultiplo=true;
					}
				}
				else if(nomeMetodoResetConnettoriPrioritari!=null && nomeMetodoResetConnettoriPrioritari.equals(nomeMetodo)){
					resetConnettoriPrioritari = true;
				}
				
				if(resetMultiplo){
					Map<String, List<String>> esitiCache = new HashMap<>();
					/**StringBuilder bf = new StringBuilder();*/
					List<String> caches = confCore.getJmxPdDCaches(alias);
					if(caches!=null && !caches.isEmpty()){
						
						for (String cache : caches) {
							
							String stato = null;
							try{
								stato = confCore.getInvoker().readJMXAttribute(alias,confCore.getJmxPdDConfigurazioneSistemaType(alias), 
										cache,
										confCore.getJmxPdDCacheNomeAttributoCacheAbilitata(alias));
								if(stato.equalsIgnoreCase("true")){
									stato = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ABILITATA;
								}
								else if(stato.equalsIgnoreCase("false")){
									stato = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DISABILITATA;
								}
								else{
									throw new DriverControlStationException("Stato ["+stato+"] sconosciuto");
								}
							}catch(Exception e){
								ControlStationCore.logError("Errore durante la lettura dello stato della cache ["+cache+"](jmxResourcePdD): "+e.getMessage(),e);
								stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
								rilevatoErrore = true;
								
								/**if(bf.length()>0){
									bf.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								}
								bf.append("- Cache ["+cache+"]: "+stato);*/
								
								addEsito(esitiCache, stato, cache);
							}
							
							if(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ABILITATA.equals(stato)){
								/**if(bf.length()>0){
									bf.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								}*/
								String result = null;
								try{
									result = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDCacheType(alias), 
											cache,
											nomeMetodoResetCache);
								}catch(Exception e){
									String errorMessage = getPrefixErrorInvokeMethod(nomeMetodo)+"sulla cache ["+nomeCache+"]: "+e.getMessage();
									ControlStationCore.getLog().error(errorMessage,e);
									result = errorMessage;
									rilevatoErrore = true;
								}
								/**bf.append("Cache ["+cache+"]: "+result);*/
								addEsito(esitiCache, result, cache);
							}
						}
						/**messagePerOperazioneEffettuata = bf.toString();*/
						messagePerOperazioneEffettuata = toString(esitiCache);
					
					}
				}
				else{
					try{
						if(resetConnettoriPrioritari) {
							String result = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDCacheType(alias), 
									confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConsegnaContenutiApplicativi(alias),
									nomeMetodo,
									nomeCache);
							messagePerOperazioneEffettuata = "Coda ["+confCore.getConsegnaNotificaCodaLabel(nomeCache)+"]: "+result;
						}
						else {
							String result = confCore.getInvoker().invokeJMXMethod(alias,confCore.getJmxPdDCacheType(alias), 
									nomeCache,
									nomeMetodo);
							messagePerOperazioneEffettuata = "Cache ["+nomeCache+"]: "+result;
						}
					}catch(Exception e){
						String oggetto = "cache";
						if(resetConnettoriPrioritari) {
							oggetto = "coda";
						}
						String errorMessage = getPrefixErrorInvokeMethod(nomeMetodo)+"sulla "+oggetto+" ["+nomeCache+"]: "+e.getMessage();
						ControlStationCore.getLog().error(errorMessage,e);
						messagePerOperazioneEffettuata = errorMessage;
						rilevatoErrore = true;
					}
				}
			}
			
			String labelDialog = null;
			String noteDialog = null;
			String messageDialog = null;
			if(nomeParametroPostBack==null || "".equals(nomeParametroPostBack)){
				String allarmeOp = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_ALLARMI_ATTIVI_MANAGER);
				if(allarmeOp!=null && !"".equals(allarmeOp)){
					nomeParametroPostBack = allarmeOp;
				}
			}
			if(nomeParametroPostBack!=null && !"".equals(nomeParametroPostBack)){
			
				String nomeAttributo = null;
				String nomeAttributo2 = null;
				String nomeMetodoJmx = null;
				String nomeRisorsa = null;
				Object nuovoStato = null;
				String tipo = null;
				boolean startAllarmi = false;
				boolean stopAllarmi = false;
				boolean restartAllarmi = false;
				try{
					if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnostici(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
						tipo = "livello di severità dei diagnostici";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnosticiLog4j(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
						tipo = "livello di severità log4j dei diagnostici";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTracciamento(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE));
						tipo = "stato del tracciamento delle buste";
						labelDialog = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_INFO_TRACCIAMENTO+" "+ ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoDumpPD(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD));
						tipo = "stato del dump binario della Porta Delegata";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA.equals(nomeParametroPostBack)){
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA;
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoDumpPA(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA));
						tipo = "stato del dump binario della Porta Applicativa";
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FILE_TRACE_UPDATE.equals(nomeParametroPostBack)){
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FILE_TRACE_UPDATE);
						if(CostantiConfigurazione.ABILITATO.getValue().equals(nuovoStato)){
							nomeMetodoJmx = confCore.getJmxPdDConfigurazioneSistemaNomeMetodoUpdateFileTrace(alias);
							nomeRisorsa = confCore.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias);
							tipo = "update configurazione FileTrace";
							labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_FILE_TRACE_LABEL;
						}
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS_CODE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorStatusCode(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS_CODE));
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS_CODE;
						labelDialog = tipo;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_USE_STATUS_CODE_AS_SOAP_FAULT.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorUseStatusCodeAsFaultCode(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_USE_STATUS_CODE_AS_SOAP_FAULT));
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_USE_STATUS_CODE_AS_SOAP_FAULT;
						labelDialog = tipo;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_REQUEST.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalRequestError(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_REQUEST));
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_TYPE+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_REQUEST;
						labelDialog = tipo;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_RESPONSE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeBadResponse(alias);
						nomeAttributo2 = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalResponseError(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_RESPONSE));
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_TYPE+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_RESPONSE;
						labelDialog = tipo;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_INTERNAL_ERROR.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalError(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_INTERNAL_ERROR));
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_TYPE+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_INTERNAL_ERROR;
						labelDialog = tipo;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_DETAILS.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorDetails(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_DETAILS));
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_DETAILS+" '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_DETAILS+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_DETAILS;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE_ID.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorInstanceId(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE_ID));
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE+" '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE_ID+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE;
					}
	
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP_CODE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorGenerateHttpHeaderGovWayCode(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP_CODE));
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP+" '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP_CODE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PD.equals(nomeParametroPostBack)){
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PD);
						if(CostantiConfigurazione.ABILITATO.getValue().equals(nuovoStato)){
							nomeMetodoJmx = confCore.getJmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaDelegata(alias);
						}
						else{
							nomeMetodoJmx = confCore.getJmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaDelegata(alias);
						}
						nomeRisorsa = confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD(alias);
						tipo = "stato del servizio Porta Applicativa";
						labelDialog = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_SERVIZI+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PD;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PA.equals(nomeParametroPostBack)){
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PA);
						if(CostantiConfigurazione.ABILITATO.getValue().equals(nuovoStato)){
							nomeMetodoJmx = confCore.getJmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaApplicativa(alias);
						}
						else{
							nomeMetodoJmx = confCore.getJmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaApplicativa(alias);
						}
						nomeRisorsa = confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD(alias);
						tipo = "stato del servizio Integration Manager";
						labelDialog = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_SERVIZI+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PA;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_IM.equals(nomeParametroPostBack)){
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_IM);
						if(CostantiConfigurazione.ABILITATO.getValue().equals(nuovoStato)){
							nomeMetodoJmx = confCore.getJmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioIntegrationManager(alias);
						}
						else{
							nomeMetodoJmx = confCore.getJmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioIntegrationManager(alias);
						}
						nomeRisorsa = confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD(alias);
						tipo = "stato del servizio IntegrationManager";
						labelDialog = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_SERVIZI+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_IM;
					}
				
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CONSEGNA_CONTENUTI_APPLICATIVI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerConsegnaContenutiApplicativi(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CONSEGNA_CONTENUTI_APPLICATIVI);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_NOTIFICHE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_NOTIFICHE;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiVerificaConnessioniAttive(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_ORARIE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheOrarie(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_ORARIE);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_ORARIE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_ORARIE;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_GIORNALIERE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheGiornaliere(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_GIORNALIERE);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_GIORNALIERE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_GIORNALIERE;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_SETTIMANALI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheSettimanali(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_SETTIMANALI);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_SETTIMANALI+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_SETTIMANALI;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_MENSILI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheMensili(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_MENSILI);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_MENSILI+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_MENSILI;
					}
	
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiEliminati(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiScaduti(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_REPOSITORY_BUSTE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreRepositoryBuste(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_REPOSITORY_BUSTE);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_REPOSITORY_BUSTE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_REPOSITORY_BUSTE;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaCorrelazioneApplicativa(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiNonGestiti(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_PULIZIA_MESSAGGI_ANOMALI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestorePuliziaMessaggiAnomali(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_PULIZIA_MESSAGGI_ANOMALI);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_PULIZIA_MESSAGGI_ANOMALI+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_PULIZIA_MESSAGGI_ANOMALI;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_RISORSE_THREAD.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerMonitoraggioRisorseThread(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_RISORSE_THREAD);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_RISORSE_THREAD+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_RISORSE_THREAD;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_THRESHOLD_THREAD.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerThresholdThread(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_THRESHOLD_THREAD);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_THRESHOLD_THREAD+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_THRESHOLD_THREAD;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_EVENTI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerEventi(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_EVENTI);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_EVENTI+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_EVENTI;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_FILE_SYSTEM_RECOVERY.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerFileSystemRecovery(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_FILE_SYSTEM_RECOVERY);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_FILE_SYSTEM_RECOVERY+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_FILE_SYSTEM_RECOVERY;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteOnewayNonRiscontrate(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteAsincroneNonRiscontrate(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_REPOSITORY_STATEFUL_THREAD.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerRepositoryStatefulThread(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_REPOSITORY_STATEFUL_THREAD);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_REPOSITORY_STATEFUL_THREAD+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_REPOSITORY_STATEFUL_THREAD;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_OPERAZIONI_REMOTE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreOperazioniRemote(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_OPERAZIONI_REMOTE);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_OPERAZIONI_REMOTE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_OPERAZIONI_REMOTE;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_SVECCHIAMENTO_OPERAZIONI_REMOTE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerSvecchiamentoOperazioniRemote(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_SVECCHIAMENTO_OPERAZIONI_REMOTE);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_SVECCHIAMENTO_OPERAZIONI_REMOTE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_SVECCHIAMENTO_OPERAZIONI_REMOTE;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_ALLARMI_ATTIVI_START.equals(nomeParametroPostBack)){
						startAllarmi = true;
						tipo = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ALLARMI_ATTIVI_START;
						labelDialog = "Avvio "+ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ALLARMI_ATTIVI;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_ALLARMI_ATTIVI_STOP.equals(nomeParametroPostBack)){
						stopAllarmi = true;
						tipo = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ALLARMI_ATTIVI_STOP;
						labelDialog = "Fermo "+ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ALLARMI_ATTIVI;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_ALLARMI_ATTIVI_RESTART.equals(nomeParametroPostBack)){
						restartAllarmi = true;
						tipo = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ALLARMI_ATTIVI_RESTART;
						labelDialog = "Riavvio "+ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ALLARMI_ATTIVI;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_CHIAVI_PDND.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreChiaviPDND(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_CHIAVI_PDND);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_CHIAVI_PDND+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_CHIAVI_PDND+ " "+ ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_TIMERS_PDND;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_CACHE_CHIAVI_PDND.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreCacheChiaviPDND(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_CACHE_CHIAVI_PDND);
						tipo =ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_TIMER_PREFIX+"'"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_CACHE_CHIAVI_PDND+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_CACHE_CHIAVI_PDND+ " "+ ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_TIMERS_PDND;
					}
										
					if(nomeAttributo!=null){
						confCore.getInvoker().setJMXAttribute(alias,confCore.getJmxPdDCacheType(alias), 
							confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
							nomeAttributo, 
							nuovoStato);
						if(nomeAttributo2!=null) {
							confCore.getInvoker().setJMXAttribute(alias,confCore.getJmxPdDCacheType(alias), 
									confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
									nomeAttributo2, 
									nuovoStato);
						}
						String tmp = "Configurazione aggiornata con successo ("+tipo+"): "+nuovoStato;
						noteDialog = ConfigurazioneCostanti.TEMPORANEE;
						if(messagePerOperazioneEffettuata!=null){
							messagePerOperazioneEffettuata+="\n"+tmp;
						}
						else{
							messagePerOperazioneEffettuata = tmp;
						}
						/**messageDialog = "Configurazione aggiornata con successo";*/
						if(confCore.isClusterAsyncUpdate()) {
							messageDialog = JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO_PREFIX+JMXUtils.MSG_OPERAZIONE_REGISTRATA_SUCCESSO.replace(JMXUtils.MSG_OPERAZIONE_REGISTRATA_SUCCESSO_TEMPLATE_SECONDI, confCore.getClusterAsyncUpdateCheckInterval()+"");
						}
						else {
							messageDialog = JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
						}

					}
					else if(nomeMetodoJmx!=null){
						String tmp = confCore.getInvoker().invokeJMXMethod(alias, confCore.getJmxPdDCacheType(alias), 
								nomeRisorsa, 
								nomeMetodoJmx);
						if(!ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FILE_TRACE_UPDATE.equals(nomeParametroPostBack)){
							noteDialog = ConfigurazioneCostanti.PERSISTENTI;
						}
						if(messagePerOperazioneEffettuata!=null){
							messagePerOperazioneEffettuata+="\n"+tmp;
						}
						else{
							messagePerOperazioneEffettuata = tmp;
						}
						messageDialog = tmp;
					}
					else if(startAllarmi || stopAllarmi || restartAllarmi) {
						AlarmEngineConfig alarmEngineConfig = confCore.getAllarmiConfig();
						if(startAllarmi) {
							AllarmiUtils.startActiveThreads(ControlStationCore.getLog(), alarmEngineConfig);
						}
						else if(stopAllarmi) {
							AllarmiUtils.stopActiveThreads(ControlStationCore.getLog(), alarmEngineConfig);
						}
						else if(restartAllarmi) {
							AllarmiUtils.restartActiveThreads(ControlStationCore.getLog(), alarmEngineConfig);
						}
						messageDialog = labelDialog+" effettuato con successo";
						noteDialog = null;
						messagePerOperazioneEffettuata = "Operazione effettuata con successo ("+tipo+")";
						
						// Dormo qualche secondo per dare il tempo di fare lo stop/start dell'allarme
						Utilities.sleep(3000);
						
					}
				}catch(Exception e){
					String errorMessage = "Errore durante l'aggiornamento ("+tipo+"): "+e.getMessage();
					ControlStationCore.getLog().error(errorMessage,e);
					if(messagePerOperazioneEffettuata!=null){
						messagePerOperazioneEffettuata+="\n"+errorMessage;
					}
					else{
						messagePerOperazioneEffettuata = errorMessage;
					}
					rilevatoErrore = true;
					messageDialog = "Errore durante l'aggiornamento: "+e.getMessage();
				}
			}
			
			if(messagePerOperazioneEffettuata!=null){
				if(nomeParametroPostBack!=null && !"".equals(nomeParametroPostBack) && messageDialog!=null){
					Dialog dialog = new Dialog();
					
					dialog.setTitolo(rilevatoErrore ? Costanti.MESSAGE_TYPE_ERROR_TITLE : Costanti.MESSAGE_TYPE_WARN_TITLE);
					dialog.setHeaderRiga1(labelDialog);
					dialog.setHeaderRiga2(messageDialog);
					
					StringBuilder sbNoteDialog = new StringBuilder();
					if(noteDialog!=null) {
						sbNoteDialog.append(noteDialog);
						if(confCore.isClusterAsyncUpdate()) {
							sbNoteDialog.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						}
					}
					if(confCore.isClusterAsyncUpdate()) {
						sbNoteDialog.append(ConfigurazioneCostanti.CLOUD_DINAMICO);
					}

					if(sbNoteDialog.length()>0) {
						dialog.setNotaFinale(sbNoteDialog.toString());
					}
					
					String[][] bottoni = { 
							{ Costanti.LABEL_MONITOR_BUTTON_CHIUDI, "" }
							};
					
					pd.setBottoni(bottoni);
					
					pd.setDialog(dialog);	
				}
				else {
					if(rilevatoErrore)
						pd.setMessage(messagePerOperazioneEffettuata);
					else 
						pd.setMessage(messagePerOperazioneEffettuata,Costanti.MESSAGE_TYPE_INFO);
				}
			}
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<>();

			if(aliases.size()>1){
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD));
				lstParam.add(new Parameter(descrizioneAlias, 
						 null));
			}else{
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME, 
						 null));
			}

			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			List<DataElement> dati = new ArrayList<>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());

			dati = confHelper.addConfigurazioneSistema(dati, alias);

			pd.setDati(dati);

			// Il livello di log è modificabile!
			pd.disableOnlyButton();
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_SISTEMA, 
					ForwardParams.ADD());
			

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_SISTEMA, ForwardParams.ADD());
		}
	}
	
	private void addEsito(Map<String, List<String>> esitiCache, String esito, String cache) {
		List<String> caches = null;
		if(esitiCache.containsKey(esito)) {
			caches = esitiCache.get(esito);
		}
		else {
			caches = new ArrayList<>();
			esitiCache.put(esito, caches);
		}
		caches.add(cache);
	}
	private String toString(Map<String, List<String>> esitiCache) {
		if(esitiCache.size()==1) {
			return esitiCache.entrySet().iterator().next().getKey();
		}
		else {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String,List<String>> entry : esitiCache.entrySet()) {
				if(sb.length()>0){
					sb.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
				}
				List<String> caches = entry.getValue();
				sb.append("- Cache "+caches.toString()+": "+entry.getKey());
			}
			return sb.toString();
		}
	}
	
	private String getPrefixErrorInvokeMethod(String nomeMetodo) {
		return "Errore durante l'invocazione dell'operazione ["+nomeMetodo+"] "; 
	}
}
