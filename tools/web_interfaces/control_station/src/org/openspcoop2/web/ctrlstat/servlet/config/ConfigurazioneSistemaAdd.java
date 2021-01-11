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
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCostanti;
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
			List<String> aliases = confCore.getJmxPdD_aliases();
			if(aliases==null || aliases.size()<=0){
				throw new Exception("Pagina non prevista, la sezione configurazione non permette di accedere a questa pagina, se la configurazione non e' corretta");
			}
			
			String [] aliasNodi = confHelper.getParameterValues(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODI_CLUSTER);
			String alias = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
			String nomeCache = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_CACHE);
			String nomeMetodo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO);

			if(alias==null || "".equals(alias)){
				if(aliases.size()>1){
					
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
						List<Parameter> lstParam = new ArrayList<Parameter>();
	
						//lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
						lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME, 
								 null));
	
						ServletUtils.setPageDataTitle(pd, lstParam);
						
						// preparo i campi
						Vector<DataElement> dati = new Vector<DataElement>();
						dati.addElement(ServletUtils.getDataElementForEditModeFinished());
						
						pd.setLabelBottoneInvia(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME_ACCEDI);
						
						dati = confHelper.addConfigurazioneSistemaSelectListNodiCluster(dati, aliasNodi);
						
						pd.setDati(dati);
						
						//pd.disableEditMode();
						
						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
						return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
								ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_SISTEMA, 
								ForwardParams.ADD());
					}
					
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
						aliasesForResetAllCaches = new ArrayList<String>();
						aliasesForResetAllCaches.addAll(aliases);
						aliasNodi = null; // svuoto per non creare confusione
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_SELECTED_CACHES.equals(nomeCache) &&
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO_RESET_ALL_CACHE_SELECTED_NODES.equals(nomeMetodo)) {
						aliasesForResetAllCaches = new ArrayList<String>();
						for (int i = 0; i < aliasNodi.length; i++) {
							aliasesForResetAllCaches.add(aliasNodi[i]);
						}
						aliasNodi = null; // svuoto
					}
					
					if(aliasesForResetAllCaches!=null && !aliasesForResetAllCaches.isEmpty()) {
						boolean rilevatoErrore = false;
						String messagePerOperazioneEffettuata = "";
						int index = 0;
						for (String aliasForResetCache : aliasesForResetAllCaches) {
							StringBuilder bfExternal = new StringBuilder();
							String descrizione = confCore.getJmxPdD_descrizione(aliasForResetCache);
							if(index>0) {
								bfExternal.append("<BR/>");
							}
							bfExternal.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER).append(" ").append(descrizione).append("<BR/>");
							List<String> caches = confCore.getJmxPdD_caches(aliasForResetCache);
							if(caches!=null && caches.size()>0){
								
								StringBuilder bfCaches = new StringBuilder();
								for (String cache : caches) {
																		
									String stato = null;
									try{
										stato = confCore.readJMXAttribute(confCore.getGestoreRisorseJMX(aliasForResetCache), aliasForResetCache,confCore.getJmxPdD_configurazioneSistema_type(aliasForResetCache), 
												cache,
												confCore.getJmxPdD_cache_nomeAttributo_cacheAbilitata(aliasForResetCache));
										if(stato.equalsIgnoreCase("true")){
											stato = "abilitata";
										}
										else if(stato.equalsIgnoreCase("false")){
											stato = "disabilitata";
										}
										else{
											throw new Exception("Stato ["+stato+"] sconosciuto");
										}
									}catch(Exception e){
										ControlStationCore.logError("Errore durante la lettura dello stato della cache ["+cache+"](jmxResourcePdD) (node:"+aliasForResetCache+"): "+e.getMessage(),e);
										stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
										rilevatoErrore = true;
										if(bfCaches.length()>0){
											bfCaches.append("<BR/>");
										}
										bfCaches.append("- Cache ["+cache+"]: "+stato);
									}
									
									if("abilitata".equals(stato)){
										if(bfCaches.length()>0){
											bfCaches.append("<BR/>");
										}
										String result = null;
										try{
											String nomeMetodoResetCache = confCore.getJmxPdD_cache_nomeMetodo_resetCache(aliasForResetCache);
											result = confCore.invokeJMXMethod(confCore.getGestoreRisorseJMX(aliasForResetCache),aliasForResetCache,confCore.getJmxPdD_cache_type(aliasForResetCache), 
													cache,
													nomeMetodoResetCache);
										}catch(Exception e){
											String errorMessage = "Errore durante l'invocazione dell'operazione ["+nomeMetodo+"] sulla cache ["+nomeCache+"] (node:"+aliasForResetCache+"): "+e.getMessage();
											ControlStationCore.getLog().error(errorMessage,e);
											result = errorMessage;
											rilevatoErrore = true;
										}
										bfCaches.append("- Cache ["+cache+"]: "+result);
									}
									
								}
								bfExternal.append(bfCaches.toString());
								
								if(messagePerOperazioneEffettuata.length()>0){
									messagePerOperazioneEffettuata+= "<BR/>";
								}
								messagePerOperazioneEffettuata+= bfExternal.toString();
							
							}
							index++;
						}
						if(messagePerOperazioneEffettuata!=null){
							if(rilevatoErrore)
								pd.setMessage(messagePerOperazioneEffettuata);
							else 
								pd.setMessage(messagePerOperazioneEffettuata,Costanti.MESSAGE_TYPE_INFO);
						}
					}
						
					// setto la barra del titolo
					List<Parameter> lstParam = new ArrayList<Parameter>();

					//lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
					lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME, 
							 null));

					ServletUtils.setPageDataTitle(pd, lstParam);
					
					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();
					dati.addElement(ServletUtils.getDataElementForEditModeFinished());
					
					pd.setLabelBottoneInvia(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME_ACCEDI);
					
					dati = confHelper.addConfigurazioneSistemaSelectListNodiCluster(dati, aliasNodi);
					
					pd.setDati(dati);
					
					//pd.disableEditMode();
					
					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
		
					return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
							ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_SISTEMA, 
							ForwardParams.ADD());
					
				}
				
			}
			
						
			String descrizioneAlias = confCore.getJmxPdD_descrizione(alias);
		
			String messagePerOperazioneEffettuata = null;
			boolean rilevatoErrore = false;
			
			if(nomeCache!=null && !"".equals(nomeCache) &&
					nomeMetodo!=null && !"".equals(nomeMetodo) &&
					
					// fix per evitare che si ricordi l'all cache, dopo una successiva operazione
					(nomeParametroPostBack==null || "".equals(nomeParametroPostBack))
					
					){
				
				String nomeMetodoResetCache = confCore.getJmxPdD_cache_nomeMetodo_resetCache(alias);
				String nomeMetodoResetConnettoriPrioritari = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_resetConnettoriPrioritari(alias);
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
					StringBuilder bf = new StringBuilder();
					List<String> caches = confCore.getJmxPdD_caches(alias);
					if(caches!=null && caches.size()>0){
						
						for (String cache : caches) {
							
							String stato = null;
							try{
								stato = confCore.readJMXAttribute(confCore.getGestoreRisorseJMX(alias), alias,confCore.getJmxPdD_configurazioneSistema_type(alias), 
										cache,
										confCore.getJmxPdD_cache_nomeAttributo_cacheAbilitata(alias));
								if(stato.equalsIgnoreCase("true")){
									stato = "abilitata";
								}
								else if(stato.equalsIgnoreCase("false")){
									stato = "disabilitata";
								}
								else{
									throw new Exception("Stato ["+stato+"] sconosciuto");
								}
							}catch(Exception e){
								ControlStationCore.logError("Errore durante la lettura dello stato della cache ["+cache+"](jmxResourcePdD): "+e.getMessage(),e);
								stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
								rilevatoErrore = true;
							}
							
							if("abilitata".equals(stato)){
								if(bf.length()>0){
									bf.append("<BR/>");
								}
								String result = null;
								try{
									result = confCore.invokeJMXMethod(confCore.getGestoreRisorseJMX(alias),alias,confCore.getJmxPdD_cache_type(alias), 
											cache,
											nomeMetodoResetCache);
								}catch(Exception e){
									String errorMessage = "Errore durante l'invocazione dell'operazione ["+nomeMetodo+"] sulla cache ["+nomeCache+"]: "+e.getMessage();
									ControlStationCore.getLog().error(errorMessage,e);
									result = errorMessage;
									rilevatoErrore = true;
								}
								bf.append("Cache ["+cache+"]: "+result);
							}
						}
						messagePerOperazioneEffettuata = bf.toString();
					
					}
				}
				else{
					try{
						if(resetConnettoriPrioritari) {
							String result = confCore.invokeJMXMethod(confCore.getGestoreRisorseJMX(alias),alias,confCore.getJmxPdD_cache_type(alias), 
									confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConsegnaContenutiApplicativi(alias),
									nomeMetodo,
									nomeCache);
							messagePerOperazioneEffettuata = "Coda ["+confCore.getConsegnaNotificaCodaLabel(nomeCache)+"]: "+result;
						}
						else {
							String result = confCore.invokeJMXMethod(confCore.getGestoreRisorseJMX(alias),alias,confCore.getJmxPdD_cache_type(alias), 
									nomeCache,
									nomeMetodo);
							messagePerOperazioneEffettuata = "Cache ["+nomeCache+"]: "+result;
						}
					}catch(Exception e){
						String oggetto = "cache";
						if(resetConnettoriPrioritari) {
							oggetto = "coda";
						}
						String errorMessage = "Errore durante l'invocazione dell'operazione ["+nomeMetodo+"] sulla "+oggetto+" ["+nomeCache+"]: "+e.getMessage();
						ControlStationCore.getLog().error(errorMessage,e);
						messagePerOperazioneEffettuata = errorMessage;
						rilevatoErrore = true;
					}
				}
			}
			
			String labelDialog = null;
			String noteDialog = null;
			String messageDialog = null;
			if(nomeParametroPostBack!=null && !"".equals(nomeParametroPostBack)){
			
				String nomeAttributo = null;
				String nomeAttributo_2 = null;
				String nomeMetodoJmx = null;
				String nomeRisorsa = null;
				Object nuovoStato = null;
				String tipo = null;
				try{
					if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnostici(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
						tipo = "livello di severità dei diagnostici";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnosticiLog4j(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
						tipo = "livello di severità log4j dei diagnostici";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_tracciamento(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE));
						tipo = "stato del tracciamento delle buste";
						labelDialog = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_INFO_TRACCIAMENTO+" "+ ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_dumpPD(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD));
						tipo = "stato del dump binario della Porta Delegata";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA.equals(nomeParametroPostBack)){
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA;
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_dumpPA(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA));
						tipo = "stato del dump binario della Porta Applicativa";
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FILE_TRACE_UPDATE.equals(nomeParametroPostBack)){
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FILE_TRACE_UPDATE);
						if(CostantiConfigurazione.ABILITATO.getValue().equals(nuovoStato)){
							nomeMetodoJmx = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_updateFileTrace(alias);
							nomeRisorsa = confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias);
							tipo = "update configurazione FileTrace";
							labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_FILE_TRACE_LABEL;
						}
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS_CODE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorStatusCode(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS_CODE));
						//tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS_CODE;
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS_CODE;
						labelDialog = tipo;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_USE_STATUS_CODE_AS_SOAP_FAULT.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorUseStatusCodeAsFaultCode(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_USE_STATUS_CODE_AS_SOAP_FAULT));
						//tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_USE_STATUS_CODE_AS_SOAP_FAULT;
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_USE_STATUS_CODE_AS_SOAP_FAULT;
						labelDialog = tipo;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_REQUEST.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalRequestError(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_REQUEST));
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_TYPE+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_REQUEST;
						labelDialog = tipo;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_RESPONSE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeBadResponse(alias);
						nomeAttributo_2 = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalResponseError(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_RESPONSE));
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_TYPE+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_RESPONSE;
						labelDialog = tipo;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_INTERNAL_ERROR.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalError(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_INTERNAL_ERROR));
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_TYPE+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_INTERNAL_ERROR;
						labelDialog = tipo;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_DETAILS.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorDetails(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_DETAILS));
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_DETAILS+" '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_DETAILS+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_DETAILS;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE_ID.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorInstanceId(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE_ID));
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE+" '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE_ID+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE;
					}
	
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP_CODE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorGenerateHttpHeaderGovWayCode(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP_CODE));
						tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP+" '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP_CODE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PD.equals(nomeParametroPostBack)){
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PD);
						if(CostantiConfigurazione.ABILITATO.getValue().equals(nuovoStato)){
							nomeMetodoJmx = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaDelegata(alias);
						}
						else{
							nomeMetodoJmx = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaDelegata(alias);
						}
						nomeRisorsa = confCore.getJmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD(alias);
						tipo = "stato del servizio Porta Applicativa";
						labelDialog = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_SERVIZI+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PD;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PA.equals(nomeParametroPostBack)){
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PA);
						if(CostantiConfigurazione.ABILITATO.getValue().equals(nuovoStato)){
							nomeMetodoJmx = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaApplicativa(alias);
						}
						else{
							nomeMetodoJmx = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaApplicativa(alias);
						}
						nomeRisorsa = confCore.getJmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD(alias);
						tipo = "stato del servizio Integration Manager";
						labelDialog = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_SERVIZI+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PA;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_IM.equals(nomeParametroPostBack)){
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_IM);
						if(CostantiConfigurazione.ABILITATO.getValue().equals(nuovoStato)){
							nomeMetodoJmx = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioIntegrationManager(alias);
						}
						else{
							nomeMetodoJmx = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioIntegrationManager(alias);
						}
						nomeRisorsa = confCore.getJmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD(alias);
						tipo = "stato del servizio IntegrationManager";
						labelDialog = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_SERVIZI+" "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_IM;
					}
				
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CONSEGNA_CONTENUTI_APPLICATIVI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerConsegnaContenutiApplicativi(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CONSEGNA_CONTENUTI_APPLICATIVI);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_NOTIFICHE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_NOTIFICHE;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiVerificaConnessioniAttive(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_ORARIE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerStatisticheOrarie(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_ORARIE);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_ORARIE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_ORARIE;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_GIORNALIERE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerStatisticheGiornaliere(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_GIORNALIERE);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_GIORNALIERE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_GIORNALIERE;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_SETTIMANALI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerStatisticheSettimanali(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_SETTIMANALI);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_SETTIMANALI+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_SETTIMANALI;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_MENSILI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerStatisticheMensili(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_MENSILI);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_MENSILI+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_MENSILI;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiPuliziaMessaggiEliminati(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiPuliziaMessaggiScaduti(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_REPOSITORY_BUSTE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreRepositoryBuste(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_REPOSITORY_BUSTE);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_REPOSITORY_BUSTE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_REPOSITORY_BUSTE;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiPuliziaCorrelazioneApplicativa(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiPuliziaMessaggiNonGestiti(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_PULIZIA_MESSAGGI_ANOMALI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestorePuliziaMessaggiAnomali(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_PULIZIA_MESSAGGI_ANOMALI);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_PULIZIA_MESSAGGI_ANOMALI+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_PULIZIA_MESSAGGI_ANOMALI;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_RISORSE_THREAD.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerMonitoraggioRisorseThread(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_RISORSE_THREAD);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_RISORSE_THREAD+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_RISORSE_THREAD;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_THRESHOLD_THREAD.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerThresholdThread(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_THRESHOLD_THREAD);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_THRESHOLD_THREAD+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_THRESHOLD_THREAD;
					}
					
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_EVENTI.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerEventi(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_EVENTI);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_EVENTI+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_EVENTI;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_FILE_SYSTEM_RECOVERY.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerFileSystemRecovery(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_FILE_SYSTEM_RECOVERY);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_FILE_SYSTEM_RECOVERY+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_FILE_SYSTEM_RECOVERY;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreBusteOnewayNonRiscontrate(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreBusteAsincroneNonRiscontrate(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE;
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_REPOSITORY_STATEFUL_THREAD.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerRepositoryStatefulThread(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_REPOSITORY_STATEFUL_THREAD);
						tipo ="stato del timer '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_REPOSITORY_STATEFUL_THREAD+"'";
						labelDialog = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TIMER_PREFIX+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_REPOSITORY_STATEFUL_THREAD;
					}

					
					if(nomeAttributo!=null){
						confCore.setJMXAttribute(confCore.getGestoreRisorseJMX(alias),alias,confCore.getJmxPdD_cache_type(alias), 
							confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
							nomeAttributo, 
							nuovoStato);
						if(nomeAttributo_2!=null) {
							confCore.setJMXAttribute(confCore.getGestoreRisorseJMX(alias),alias,confCore.getJmxPdD_cache_type(alias), 
									confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
									nomeAttributo_2, 
									nuovoStato);
						}
						String tmp = "Configurazione aggiornata con successo ("+tipo+"): "+nuovoStato;
						//tmp += ConfigurazioneCostanti.TEMPORANEE;
						noteDialog = ConfigurazioneCostanti.TEMPORANEE;
						if(messagePerOperazioneEffettuata!=null){
							messagePerOperazioneEffettuata+="\n"+tmp;
						}
						else{
							messagePerOperazioneEffettuata = tmp;
						}
						messageDialog = "Configurazione aggiornata con successo";
					}
					else if(nomeMetodoJmx!=null){
						String tmp = confCore.invokeJMXMethod(confCore.getGestoreRisorseJMX(alias),alias, confCore.getJmxPdD_cache_type(alias), 
								nomeRisorsa, 
								nomeMetodoJmx);
						if(!ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FILE_TRACE_UPDATE.equals(nomeParametroPostBack)){
							//tmp += ConfigurazioneCostanti.PERSISTENTI;
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
					//dialog.setIcona(icona);
					dialog.setHeaderRiga1(labelDialog);
					dialog.setHeaderRiga2(messageDialog);
					
					dialog.setNotaFinale(noteDialog);
					
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
			List<Parameter> lstParam = new ArrayList<Parameter>();

			if(confCore.isSinglePdD()){
				//lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
				if(aliases.size()>1){
					lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME, 
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD));
					lstParam.add(new Parameter(descrizioneAlias, 
							 null));
				}else{
					lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME, 
							 null));
				}
			}else{
				lstParam.add(new Parameter(PddCostanti.LABEL_PORTE_DI_DOMINIO, PddCostanti.SERVLET_NAME_PDD_LIST));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME, 
						 null));
			}

			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			dati = confHelper.addConfigurazioneSistema(dati, alias);

			pd.setDati(dati);

			// Il livello di log è modificabile!
			pd.disableOnlyButton();
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_SISTEMA, 
					ForwardParams.ADD());
			

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_SISTEMA, ForwardParams.ADD());
		}
	}
}
