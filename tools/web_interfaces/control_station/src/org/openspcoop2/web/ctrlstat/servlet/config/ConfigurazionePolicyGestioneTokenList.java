/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.utils.ConfigManager;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**     
 * ConfigurazionePolicyGestioneTokenList
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePolicyGestioneTokenList extends Action {

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
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			String infoType = confHelper.getParametroInfoType(ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
			String infoTypeSession = ServletUtils.getObjectFromSession(request, session, String.class, ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
			if(infoType==null) {
				infoType = infoTypeSession;
			}
			else {
				ServletUtils.setObjectIntoSession(request, session, infoType, ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
			}
			boolean attributeAuthority = ConfigurazioneCostanti.isConfigurazioneAttributeAuthority(infoType);
			boolean llmProvider = ConfigurazioneCostanti.isConfigurazioneLLMProvider(infoType);
			boolean llmModel = ConfigurazioneCostanti.isConfigurazioneLLMModel(infoType);
			boolean llmProviderBinding = ConfigurazioneCostanti.isConfigurazioneLLMProviderBinding(infoType);

			// reset di eventuali configurazioni salvate in sessione
			Properties mapId;
			if (attributeAuthority) {
				mapId = confCore.getAttributeAuthorityTipologia();
			} else if (llmProvider) {
				mapId = confCore.getLlmProviderTipologia();
			} else if (llmModel) {
				mapId = confCore.getLlmModelTipologia();
			} else if (llmProviderBinding) {
				mapId = confCore.getLlmProviderBindingTipologia();
			} else {
				mapId = confCore.getTokenPolicyTipologia();
			}
			if(mapId!=null && !mapId.isEmpty()) {
				PropertiesSourceConfiguration propertiesSourceConfiguration;
				if (attributeAuthority) {
					propertiesSourceConfiguration = confCore.getAttributeAuthorityPropertiesSourceConfiguration();
				} else if (llmProvider || llmModel || llmProviderBinding) {
					propertiesSourceConfiguration = confCore.getLlmPropertiesSourceConfiguration();
				} else {
					propertiesSourceConfiguration = confCore.getPolicyGestioneTokenPropertiesSourceConfiguration();
				}
				ConfigManager configManager = ConfigManager.getinstance(ControlStationCore.getLog());
				configManager.leggiConfigurazioni(propertiesSourceConfiguration, true);
				for (Object oTipo : mapId.keySet()) {
					if(oTipo instanceof String) {
						String tipo = (String) oTipo;
						Config config = configManager.getConfigurazione(propertiesSourceConfiguration, tipo);
						ServletUtils.removeConfigurazioneBeanFromSession(request, session, config.getId());
					}
				}
			}

			int idLista;
			if (attributeAuthority) {
				idLista = Liste.CONFIGURAZIONE_GESTIONE_ATTRIBUTE_AUTHORITY;
			} else if (llmProvider) {
				idLista = Liste.CONFIGURAZIONE_GESTIONE_LLM_PROVIDER;
			} else if (llmModel) {
				idLista = Liste.CONFIGURAZIONE_GESTIONE_LLM_MODEL;
			} else if (llmProviderBinding) {
				idLista = Liste.CONFIGURAZIONE_GESTIONE_LLM_PROVIDER_BINDING;
			} else {
				idLista = Liste.CONFIGURAZIONE_GESTIONE_POLICY_TOKEN;
			}

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<String> tipologie = new ArrayList<>();
			if(attributeAuthority) {
				tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_ATTRIBUTE_AUTHORITY);
			}
			else if(llmProvider) {
				tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_LLM_PROVIDER);
			}
			else if(llmModel) {
				tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_LLM_MODEL);
			}
			else if(llmProviderBinding) {
				tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_LLM_PROVIDER_BINDING);
			}
			else {
				tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN);
				tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_RETRIEVE_POLICY_TOKEN);
			}
			
			// Mapping FILTRO_LLM_* logico -> property name concreto (lookup nel driver via EXISTS).
			java.util.Map<String,String> propertyFilters = buildLlmPropertyFilters(ricerca, idLista, llmProvider, llmModel, llmProviderBinding);

			List<GenericProperties> lista = confCore.gestorePolicyTokenList(idLista, tipologie, ricerca, propertyFilters);

			confHelper.prepareGestorePolicyTokenList(ricerca, lista, idLista);
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN,
					ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.LIST());
		}
	}

	/**
	 * Traduce i filtri logici LLM (chiavi {@code Filtri.FILTRO_LLM_*}) salvati nella ricerca
	 * in coppie {nome property concreto, valore selezionato}, da passare al driver SQL
	 * (che applica una clausola EXISTS su {@code generic_property} per ogni entry).
	 *
	 * <p>Il valore "qualsiasi" viene scartato (nessun filtro).</p>
	 */
	private static java.util.Map<String,String> buildLlmPropertyFilters(ConsoleSearch ricerca, int idLista,
			boolean llmProvider, boolean llmModel, boolean llmProviderBinding) {
		java.util.LinkedHashMap<String,String> propertyFilters = new java.util.LinkedHashMap<>();
		if (llmProvider) {
			putIfSelected(propertyFilters, ricerca, idLista,
					org.openspcoop2.core.commons.Filtri.FILTRO_LLM_PROVIDER_TYPE,
					org.openspcoop2.pdd.core.llm.provider.Costanti.LLM_PROVIDER_TYPE);
		}
		else if (llmModel) {
			putIfSelected(propertyFilters, ricerca, idLista,
					org.openspcoop2.core.commons.Filtri.FILTRO_LLM_MODEL_FAMILY,
					org.openspcoop2.pdd.core.llm.provider.Costanti.LLM_MODEL_FAMILY);
			putIfSelected(propertyFilters, ricerca, idLista,
					org.openspcoop2.core.commons.Filtri.FILTRO_LLM_MODEL_MODALITY,
					org.openspcoop2.pdd.core.llm.provider.Costanti.LLM_MODEL_MODALITY);
		}
		else if (llmProviderBinding) {
			putIfSelected(propertyFilters, ricerca, idLista,
					org.openspcoop2.core.commons.Filtri.FILTRO_LLM_PROVIDER_BINDING_PROVIDER,
					org.openspcoop2.pdd.core.llm.provider.Costanti.LLM_PROVIDER_BINDING_PROVIDER);
			putIfSelected(propertyFilters, ricerca, idLista,
					org.openspcoop2.core.commons.Filtri.FILTRO_LLM_PROVIDER_BINDING_MODEL,
					org.openspcoop2.pdd.core.llm.provider.Costanti.LLM_PROVIDER_BINDING_MODEL);
		}
		return propertyFilters;
	}

	private static void putIfSelected(java.util.Map<String,String> sink, ConsoleSearch ricerca, int idLista,
			String filterKey, String propertyName) {
		String v = org.openspcoop2.core.commons.SearchUtils.getFilter(ricerca, idLista, filterKey);
		if (v != null && !v.isEmpty()
				&& !CostantiControlStation.DEFAULT_VALUE_PARAMETRO_TIPO_TOKEN_POLICY_QUALSIASI.equals(v)) {
			sink.put(propertyName, v);
		}
	}

}

