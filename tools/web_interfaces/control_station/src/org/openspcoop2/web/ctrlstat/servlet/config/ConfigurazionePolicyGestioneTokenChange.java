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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.utils.ConfigManager;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.mvc.properties.beans.ConfigBean;

/**     
 * ConfigurazionePolicyGestioneTokenChange
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePolicyGestioneTokenChange extends Action {

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
			// Preparo il menu
			confHelper.makeMenu();
			
			String id = confHelper.getParametroLong(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_ID);
			String nome = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_NOME);
			String descrizione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_DESCRIZIONE);
			String tipo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPO);
			
			String infoType = confHelper.getParametroInfoType(ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
			if(infoType==null) {
				infoType = ServletUtils.getObjectFromSession(request, session, String.class, ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
			}
			boolean attributeAuthority = ConfigurazioneCostanti.isConfigurazioneAttributeAuthority(infoType);
			
			String resetElementoCacheS = confHelper.getParameter(CostantiControlStation.PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE);
			boolean resetElementoCache = ServletUtils.isCheckBoxEnabled(resetElementoCacheS);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
			Properties mapId = attributeAuthority ?
					confCore.getAttributeAuthorityTipologia() :
					confCore.getTokenPolicyTipologia();
			
			GenericProperties genericProperties = confCore.getGenericProperties(Long.parseLong(id));
			
			PropertiesSourceConfiguration propertiesSourceConfiguration = attributeAuthority ? 
					confCore.getAttributeAuthorityPropertiesSourceConfiguration() :
					confCore.getPolicyGestioneTokenPropertiesSourceConfiguration();
			
			ConfigManager configManager = ConfigManager.getinstance(ControlStationCore.getLog());
			configManager.leggiConfigurazioni(propertiesSourceConfiguration, true);

			List<String> nomiConfigurazioniPolicyGestioneToken = configManager.getNomiConfigurazioni(propertiesSourceConfiguration);
			List<String> labelConfigurazioniPolicyGestioneToken = configManager.convertToLabel(propertiesSourceConfiguration, nomiConfigurazioniPolicyGestioneToken);
			
			String[] propConfigPolicyGestioneTokenLabelList = labelConfigurazioniPolicyGestioneToken.toArray(new String[labelConfigurazioniPolicyGestioneToken.size()]);
			String[] propConfigPolicyGestioneTokenList= nomiConfigurazioniPolicyGestioneToken.toArray(new String[nomiConfigurazioniPolicyGestioneToken.size()]);
			
			Config configurazione = configManager.getConfigurazione(propertiesSourceConfiguration, genericProperties.getTipo()); 
			
			Map<String, Properties> mappaDB = confCore.readGestorePolicyTokenPropertiesConfiguration(genericProperties.getId()); 
			
			ConfigBean configurazioneBean = confCore.leggiConfigurazione(configurazione, mappaDB);
			
			if(nome != null) { // la prima volta non sovrascrivo la configurazione con i valori letti dai parametri
				confHelper.aggiornaConfigurazioneProperties(configurazioneBean);
			} else {
				// reset di eventuali configurazioni salvate in sessione
				ServletUtils.removeConfigurazioneBeanFromSession(request, session, configurazioneBean.getId());
			}
			
			configurazioneBean.updateConfigurazione(configurazione);
			ServletUtils.saveConfigurazioneBeanIntoSession(request, session, configurazioneBean, configurazioneBean.getId());
			
			// reset elemento dalla cache
			if(resetElementoCache) {
				
				// Uso lo stessoAlias
				List<String> aliases = confCore.getJmxPdDAliases();
				String alias = null;
				if(aliases!=null && !aliases.isEmpty()) {
					alias = aliases.get(0);
				}
				
				String metodo = null;
				if(attributeAuthority) {
					metodo = confCore.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAttributeAuthority(alias);
				}
				else {
					boolean validazione = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN.equals(genericProperties.getTipologia());
					boolean negoziazione = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_RETRIEVE_POLICY_TOKEN.equals(genericProperties.getTipologia());
					if(validazione) {
						metodo = confCore.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyValidazione(alias);
					}
					else if(negoziazione) {
						metodo = confCore.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyNegoziazione(alias);
					}
					else {
						throw new Exception("Tipologia '"+genericProperties.getTipologia()+"' non supportata");
					}
				}
				
				String labelPolicy = genericProperties.getNome();
				confCore.invokeJmxMethodAllNodesAndSetResult(pd, confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
						metodo,
						MessageFormat.format(CostantiControlStation.LABEL_ELIMINATO_CACHE_SUCCESSO,labelPolicy),
						MessageFormat.format(CostantiControlStation.LABEL_ELIMINATO_CACHE_FALLITO_PREFIX,labelPolicy),
						genericProperties.getId());				
				
				String resetFromLista = confHelper.getParameter(CostantiControlStation.PARAMETRO_RESET_CACHE_FROM_LISTA);
				boolean arrivoDaLista = "true".equalsIgnoreCase(resetFromLista);
				
				if(arrivoDaLista) {
					
					// preparo lista
				
					ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

					String infoTypeA = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
					String infoTypeSession = ServletUtils.getObjectFromSession(request, session, String.class, ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
					if(infoTypeA==null) {
						infoTypeA = infoTypeSession;
					}
					else {
						ServletUtils.setObjectIntoSession(request, session, infoTypeA, ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
					}
					attributeAuthority = ConfigurazioneCostanti.isConfigurazioneAttributeAuthority(infoTypeA);
					
					// reset di eventuali configurazioni salvate in sessione
					mapId = attributeAuthority ?
							confCore.getAttributeAuthorityTipologia() :
							confCore.getTokenPolicyTipologia();
					if(mapId!=null && !mapId.isEmpty()) {
						propertiesSourceConfiguration = attributeAuthority ? 
								confCore.getAttributeAuthorityPropertiesSourceConfiguration() :
								confCore.getPolicyGestioneTokenPropertiesSourceConfiguration();
						configManager.leggiConfigurazioni(propertiesSourceConfiguration, true);
						for (Object oTipo : mapId.keySet()) {
							if(oTipo instanceof String) {
								String ti = (String) oTipo;
								Config config = configManager.getConfigurazione(propertiesSourceConfiguration, ti);
								ServletUtils.removeConfigurazioneBeanFromSession(request, session, config.getId());
							}
						}
					}
					
					int idLista = attributeAuthority ? Liste.CONFIGURAZIONE_GESTIONE_ATTRIBUTE_AUTHORITY : Liste.CONFIGURAZIONE_GESTIONE_POLICY_TOKEN;
					
					ricerca = confHelper.checkSearchParameters(idLista, ricerca);

					List<String> tipologie = new ArrayList<>();
					if(attributeAuthority) {
						tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_ATTRIBUTE_AUTHORITY);
					}
					else {
						tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN);
						tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_RETRIEVE_POLICY_TOKEN);
					}
					
					List<GenericProperties> lista = confCore.gestorePolicyTokenList(idLista, tipologie, ricerca);
					
					confHelper.prepareGestorePolicyTokenList(ricerca, lista, idLista); 
					
					// salvo l'oggetto ricerca nella sessione
					ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
					
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
					// Forward control to the specified success URI
					return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.CHANGE());
					
				}
			}
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<>();

			String label = attributeAuthority ?
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY :
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN;
			
			lstParam.add(new Parameter(label, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_LIST));
			lstParam.add(new Parameter(genericProperties.getNome(),null));
		
			// Se tipo = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				if(nome == null) {
					nome = genericProperties.getNome();
					descrizione = genericProperties.getDescrizione();
					tipo = genericProperties.getTipo();
				}
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addPolicyGestioneTokenToDati(tipoOperazione,dati,id,nome,descrizione,tipo,propConfigPolicyGestioneTokenLabelList,propConfigPolicyGestioneTokenList,
						attributeAuthority, genericProperties);
				
				dati = confHelper.addPropertiesConfigToDati(tipoOperazione,dati, tipo, configurazioneBean,false);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN,ForwardParams.CHANGE());
			}
			
			// Controlli sui campi immessi
			String tipologia = mapId.getProperty(tipo);
			boolean isOk = confHelper.policyGestioneTokenCheckData(tipoOperazione, nome,descrizione,tipo,tipologia);
			
			if (isOk) {
				isOk = confHelper.checkPropertiesConfigurationData(tipoOperazione, configurazioneBean, nome,descrizione,configurazione);
			}
			if (!isOk) {
				
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addPolicyGestioneTokenToDati(tipoOperazione,dati,id,nome,descrizione,tipo,propConfigPolicyGestioneTokenLabelList,propConfigPolicyGestioneTokenList,
						attributeAuthority, genericProperties);
						
				dati = confHelper.addPropertiesConfigToDati(tipoOperazione,dati, tipo, configurazioneBean,false);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.CHANGE());
			}
			
			// update sul db
			GenericProperties policy = new GenericProperties();
			policy.setNome(nome);
			policy.setDescrizione(descrizione);
			policy.setTipologia(tipologia);
			policy.setTipo(tipo);
			policy.setId(genericProperties.getId()); 
			
			policy.setProprietaOggetto(genericProperties.getProprietaOggetto());
			
			Map<String, Properties> mappaDestinazione = configurazioneBean.getPropertiesMap();	
			Map<String, String> map = DBPropertiesUtils.toMap(mappaDestinazione);
			
			for (String propKey : map.keySet()) {
				Property property = new Property();
				property.setNome(propKey);
				property.setValore(map.get(propKey));
				
				policy.getPropertyList().add(property);
			}
			
			confCore.performUpdateOperation(userLogin, confHelper.smista(), policy);
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = attributeAuthority ? Liste.CONFIGURAZIONE_GESTIONE_ATTRIBUTE_AUTHORITY : Liste.CONFIGURAZIONE_GESTIONE_POLICY_TOKEN;
			
			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<String> tipologie = new ArrayList<>();
			if(attributeAuthority) {
				tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_ATTRIBUTE_AUTHORITY);
			}
			else {
				tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN);
				tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_RETRIEVE_POLICY_TOKEN);
			}
			
			List<GenericProperties> lista = confCore.gestorePolicyTokenList(idLista, tipologie, ricerca);
			
			confHelper.prepareGestorePolicyTokenList(ricerca, lista, idLista); 
			
			// reset di eventuali configurazioni salvate in sessione
			ServletUtils.removeConfigurazioneBeanFromSession(request, session, configurazioneBean.getId());
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.CHANGE());
		}  
	}
}
