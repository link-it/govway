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
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * SystemPropertyAdd
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
			
			String alias = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
			String nomeCache = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_CACHE);
			String nomeMetodo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO);
			
			String nomeParametroPostBack = confHelper.getPostBackElementName();
			
			
			if(alias==null || "".equals(alias)){
				
				if(aliases.size()==1){
					alias = aliases.get(0);
				}
				else{
				
					if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_ALL_CACHES.equals(nomeCache) &&
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO_RESET_ALL_CACHE_ALL_NODES.equals(nomeMetodo)) {
						boolean rilevatoErrore = false;
						String messagePerOperazioneEffettuata = "";
						int index = 0;
						for (String aliasForResetCache : aliases) {
							StringBuffer bfExternal = new StringBuffer();
							String descrizione = confCore.getJmxPdD_descrizione(aliasForResetCache);
							if(index>0) {
								bfExternal.append("<BR/>");
							}
							bfExternal.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER).append(" ").append(descrizione).append("<BR/>");
							List<String> caches = confCore.getJmxPdD_caches(aliasForResetCache);
							if(caches!=null && caches.size()>0){
								
								StringBuffer bfCaches = new StringBuffer();
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
					
					dati = confHelper.addConfigurazioneSistemaSelectListNodiCluster(dati);
					
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
					nomeMetodo!=null && !"".equals(nomeMetodo)){
				
				String nomeMetodoResetCache = confCore.getJmxPdD_cache_nomeMetodo_resetCache(alias);
				boolean resetMultiplo = false;
				if(nomeMetodoResetCache!=null && nomeMetodoResetCache.equals(nomeMetodo)){
					if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_ALL_CACHES.equals(nomeCache)){
						resetMultiplo=true;
					}
				}
				
				if(resetMultiplo){
					StringBuffer bf = new StringBuffer();
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
						String result = confCore.invokeJMXMethod(confCore.getGestoreRisorseJMX(alias),alias,confCore.getJmxPdD_cache_type(alias), 
								nomeCache,
								nomeMetodo);
						messagePerOperazioneEffettuata = "Cache ["+nomeCache+"]: "+result;
					}catch(Exception e){
						String errorMessage = "Errore durante l'invocazione dell'operazione ["+nomeMetodo+"] sulla cache ["+nomeCache+"]: "+e.getMessage();
						ControlStationCore.getLog().error(errorMessage,e);
						messagePerOperazioneEffettuata = errorMessage;
						rilevatoErrore = true;
					}
				}
			}
			
			if(nomeParametroPostBack!=null && !"".equals(nomeParametroPostBack)){
			
				String nomeAttributo = null;
				String nomeMetodoJmx = null;
				Object nuovoStato = null;
				String tipo = null;
				try{
					if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnostici(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
						tipo = "livello di severità dei diagnostici";
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnosticiLog4j(alias);
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
						tipo = "livello di severità log4j dei diagnostici";
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_tracciamento(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE));
						tipo = "stato del tracciamento delle buste";
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_dumpPD(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD));
						tipo = "stato del dump binario della Porta Delegata";
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA.equals(nomeParametroPostBack)){
						nomeAttributo = confCore.getJmxPdD_configurazioneSistema_nomeAttributo_dumpPA(alias);
						nuovoStato = CostantiConfigurazione.ABILITATO.getValue().equals(confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA));
						tipo = "stato del dump binario della Porta Applicativa";
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PD.equals(nomeParametroPostBack)){
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PD);
						if(CostantiConfigurazione.ABILITATO.getValue().equals(nuovoStato)){
							nomeMetodoJmx = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaDelegata(alias);
						}
						else{
							nomeMetodoJmx = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaDelegata(alias);
						}
						tipo = "stato del servizio Porta Applicativa";
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PA.equals(nomeParametroPostBack)){
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PA);
						if(CostantiConfigurazione.ABILITATO.getValue().equals(nuovoStato)){
							nomeMetodoJmx = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaApplicativa(alias);
						}
						else{
							nomeMetodoJmx = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaApplicativa(alias);
						}
						tipo = "stato del servizio Integration Manager";
					}
					else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_IM.equals(nomeParametroPostBack)){
						nuovoStato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_IM);
						if(CostantiConfigurazione.ABILITATO.getValue().equals(nuovoStato)){
							nomeMetodoJmx = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioIntegrationManager(alias);
						}
						else{
							nomeMetodoJmx = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioIntegrationManager(alias);
						}
						tipo = "stato del servizio Porta Delegata";
					}
				
					if(nomeAttributo!=null){
						confCore.setJMXAttribute(confCore.getGestoreRisorseJMX(alias),alias,confCore.getJmxPdD_cache_type(alias), 
							confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
							nomeAttributo, 
							nuovoStato);
						String tmp = "Configurazione aggiornata con successo ("+tipo+"): "+nuovoStato;
						tmp += ConfigurazioneCostanti.TEMPORANEE;
						if(messagePerOperazioneEffettuata!=null){
							messagePerOperazioneEffettuata+="\n"+tmp;
						}
						else{
							messagePerOperazioneEffettuata = tmp;
						}
					}
					else if(nomeMetodoJmx!=null){
						String tmp = confCore.invokeJMXMethod(confCore.getGestoreRisorseJMX(alias),alias, confCore.getJmxPdD_cache_type(alias), 
								confCore.getJmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD(alias), 
								nomeMetodoJmx);
						tmp += ConfigurazioneCostanti.PERSISTENTI;
						if(messagePerOperazioneEffettuata!=null){
							messagePerOperazioneEffettuata+="\n"+tmp;
						}
						else{
							messagePerOperazioneEffettuata = tmp;
						}
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
				}
			}
			
			if(messagePerOperazioneEffettuata!=null){
				if(rilevatoErrore)
					pd.setMessage(messagePerOperazioneEffettuata);
				else 
					pd.setMessage(messagePerOperazioneEffettuata,Costanti.MESSAGE_TYPE_INFO);
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
