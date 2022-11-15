/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.utils.ConfigManager;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
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
import org.openspcoop2.web.lib.mvc.properties.utils.ReadPropertiesUtilities;


/**     
 * ConfigurazionePolicyGestioneTokenAdd
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePolicyGestioneTokenAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	
		
		TipoOperazione tipoOperazione = TipoOperazione.ADD;
		
		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);
			// Preparo il menu
			confHelper.makeMenu();
			
			String id = "";
			String nome = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_NOME);
			String descrizione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_DESCRIZIONE);
			String tipo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPO);
			
			String infoType = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
			if(infoType==null) {
				infoType = ServletUtils.getObjectFromSession(request, session, String.class, ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
			}
			boolean attributeAuthority = ConfigurazioneCostanti.isConfigurazioneAttributeAuthority(infoType);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
			boolean forceIdEnabled = attributeAuthority ? 
					confCore.isAttributeAuthorityForceIdEnabled() :
					confCore.isTokenPolicyForceIdEnabled() ;
			Properties mapId = attributeAuthority ?
					confCore.getAttributeAuthorityTipologia() :
					confCore.getTokenPolicyTipologia();
			String forceId = null;
			if(forceIdEnabled) {
				forceId = attributeAuthority ? 
						confCore.getAttributeAuthorityForceId() :
						confCore.getTokenPolicyForceId();
			} 
			
			PropertiesSourceConfiguration propertiesSourceConfiguration = attributeAuthority ? 
					confCore.getAttributeAuthorityPropertiesSourceConfiguration() :
					confCore.getPolicyGestioneTokenPropertiesSourceConfiguration();
			
			ConfigManager configManager = ConfigManager.getinstance(ControlStationCore.getLog());
			configManager.leggiConfigurazioni(propertiesSourceConfiguration, true);

			List<String> nomiConfigurazioniPolicyGestioneToken = configManager.getNomiConfigurazioni(propertiesSourceConfiguration);
			List<String> labelConfigurazioniPolicyGestioneToken = configManager.convertToLabel(propertiesSourceConfiguration, nomiConfigurazioniPolicyGestioneToken);
			
			List<String> propConfigPolicyGestioneTokenLabelListTmp = new ArrayList<String>(); 
			propConfigPolicyGestioneTokenLabelListTmp.add(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO);
			propConfigPolicyGestioneTokenLabelListTmp.addAll(labelConfigurazioniPolicyGestioneToken);
			
			List<String>  propConfigPolicyGestioneTokenListTmp = new ArrayList<String>(); 
			propConfigPolicyGestioneTokenListTmp.add(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO);
			propConfigPolicyGestioneTokenListTmp.addAll(nomiConfigurazioniPolicyGestioneToken);
			
			String[] propConfigPolicyGestioneTokenLabelList = propConfigPolicyGestioneTokenLabelListTmp.toArray(new String[propConfigPolicyGestioneTokenLabelListTmp.size()]);
			String[] propConfigPolicyGestioneTokenList= propConfigPolicyGestioneTokenListTmp.toArray(new String[propConfigPolicyGestioneTokenListTmp.size()]);
			
			String postBackElementName = confHelper.getPostBackElementName();
			
			Config configurazione = null;
			ConfigBean configurazioneBean = null;
			
			if(tipo == null) {
				if(forceIdEnabled)
					tipo = forceId;
			}
			
			String tipologia = null;
			if(forceIdEnabled) {
				tipologia = mapId.getProperty(forceId);
			}
			if(tipo != null && !tipo.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)) {
				configurazione = configManager.getConfigurazione(propertiesSourceConfiguration, tipo);
			
				configurazioneBean = ReadPropertiesUtilities.leggiConfigurazione(configurazione, null);
				
				if(postBackElementName != null && postBackElementName.equals(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPO)) {
					// reset di eventuali configurazioni salvate in sessione
					ServletUtils.removeConfigurazioneBeanFromSession(request, session, configurazioneBean.getId());
				}
				
				confHelper.aggiornaConfigurazioneProperties(configurazioneBean);
				
				configurazioneBean.updateConfigurazione(configurazione);
				ServletUtils.saveConfigurazioneBeanIntoSession(request, session, configurazioneBean, configurazioneBean.getId());
			
				if(!forceIdEnabled) {
					tipologia = mapId.getProperty(tipo);
					if(tipologia==null) {
						throw new Exception("Mapping tipologia token per tipo '"+tipo+"' inesistente");
					}
				}
			}
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			String label = attributeAuthority ?
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY :
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN;
			
			lstParam.add(new Parameter(label, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_LIST));
			lstParam.add(ServletUtils.getParameterAggiungi());
			
			// Se tipo = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				if(nome == null) {
					nome = "";
					descrizione = "";
					
					if(tipo == null) {
						if(!forceIdEnabled)
							tipo = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
						else
							tipo = forceId;
					}
				}
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addPolicyGestioneTokenToDati(tipoOperazione,dati,id,nome,descrizione,tipo,propConfigPolicyGestioneTokenLabelList,propConfigPolicyGestioneTokenList,
						attributeAuthority, null);
				
				dati = confHelper.addPropertiesConfigToDati(tipoOperazione,dati, tipo, configurazioneBean,false);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, 
						ForwardParams.ADD());
			}
			
			// Controlli sui campi immessi
			boolean isOk = confHelper.policyGestioneTokenCheckData(tipoOperazione, nome,descrizione,tipo,tipologia);
			
			if (isOk) {
				isOk = confHelper.checkPropertiesConfigurationData(tipoOperazione, configurazioneBean, nome,descrizione,configurazione);
			}
			if (!isOk) {
				
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addPolicyGestioneTokenToDati(tipoOperazione,dati,id,nome,descrizione,tipo,propConfigPolicyGestioneTokenLabelList,propConfigPolicyGestioneTokenList,
						attributeAuthority, null);
						
				dati = confHelper.addPropertiesConfigToDati(tipoOperazione,dati, tipo, configurazioneBean,false);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.ADD());
			}

			// insert sul db
			GenericProperties policy = new GenericProperties();
			policy.setNome(nome);
			policy.setDescrizione(descrizione);
			policy.setTipologia(tipologia);
			policy.setTipo(tipo);
			
			Map<String, Properties> mappaDestinazione = configurazioneBean.getPropertiesMap();	
			Map<String, String> map = DBPropertiesUtils.toMap(mappaDestinazione);
			
			for (String propKey : map.keySet()) {
				Property property = new Property();
				property.setNome(propKey);
				property.setValore(map.get(propKey));
				
				policy.getPropertyList().add(property);
			}
			
			confCore.performCreateOperation(userLogin, confHelper.smista(), policy);
			
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
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.ADD());
		}  
	}
}
