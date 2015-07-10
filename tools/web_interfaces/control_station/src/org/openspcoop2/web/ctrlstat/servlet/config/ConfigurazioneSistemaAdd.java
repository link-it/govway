/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCostanti;
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
			
			String alias = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
			String nomeCache = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_CACHE);
			String nomeMetodo = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO);
			
			
			if(alias==null || "".equals(alias)){
				
				if(aliases.size()==1){
					alias = aliases.get(0);
				}
				else{
				
					// setto la barra del titolo
					List<Parameter> lstParam = new ArrayList<Parameter>();

					lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
					lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA, 
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
								}
								bf.append("Cache ["+cache+"]: "+result);
							}
						}
						pd.setMessage(bf.toString());
					
					}
				}
				else{
					try{
						String result = confCore.invokeJMXMethod(confCore.getGestoreRisorseJMX(alias),alias,confCore.getJmxPdD_cache_type(alias), 
								nomeCache,
								nomeMetodo);
						pd.setMessage("Cache ["+nomeCache+"]: "+result);
					}catch(Exception e){
						String errorMessage = "Errore durante l'invocazione dell'operazione ["+nomeMetodo+"] sulla cache ["+nomeCache+"]: "+e.getMessage();
						ControlStationCore.getLog().error(errorMessage,e);
						pd.setMessage(errorMessage);
					}
				}
			}
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			if(confCore.isSinglePdD()){
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
				if(aliases.size()>1){
					lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA, 
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD));
					lstParam.add(new Parameter(descrizioneAlias, 
							 null));
				}else{
					lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA, 
							 null));
				}
			}else{
				lstParam.add(new Parameter(PddCostanti.LABEL_PORTE_DI_DOMINIO, PddCostanti.SERVLET_NAME_PDD_LIST));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA, 
						 null));
			}

			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			dati = confHelper.addConfigurazioneSistema(dati, alias);

			pd.setDati(dati);

			pd.disableEditMode();
			
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
