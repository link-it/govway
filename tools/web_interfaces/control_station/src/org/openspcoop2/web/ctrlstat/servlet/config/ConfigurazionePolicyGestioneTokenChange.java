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
import org.openspcoop2.web.ctrlstat.core.Search;
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
			
			String id = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_ID);
			String nome = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_NOME);
			String descrizione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_DESCRIZIONE);
			String tipo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPO);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			GenericProperties genericProperties = confCore.getGenericProperties(Long.parseLong(id));
			
			PropertiesSourceConfiguration propertiesSourceConfiguration = confCore.getPolicyGestioneTokenPropertiesSourceConfiguration();
			
			ConfigManager configManager = ConfigManager.getinstance(ControlStationCore.getLog());
			configManager.leggiConfigurazioni(propertiesSourceConfiguration, true);

			List<String> nomiConfigurazioniPolicyGestioneToken = configManager.getNomiConfigurazioni(propertiesSourceConfiguration);
			List<String> labelConfigurazioniPolicyGestioneToken = configManager.convertToLabel(propertiesSourceConfiguration, nomiConfigurazioniPolicyGestioneToken);
			
			String[] propConfigPolicyGestioneTokenLabelList = labelConfigurazioniPolicyGestioneToken.toArray(new String[labelConfigurazioniPolicyGestioneToken.size()]);
			String[] propConfigPolicyGestioneTokenList= nomiConfigurazioniPolicyGestioneToken.toArray(new String[nomiConfigurazioniPolicyGestioneToken.size()]);
			
			Config configurazione = configManager.getConfigurazione(propertiesSourceConfiguration, genericProperties.getTipo()); 
			
			Map<String, Properties> mappaDB = confCore.readGestorePolicyTokenPropertiesConfiguration(genericProperties.getId()); 
			
			ConfigBean configurazioneBean = ReadPropertiesUtilities.leggiConfigurazione(configurazione, mappaDB);
			
			if(nome != null) { // la prima volta non sovrascrivo la configurazione con i valori letti dai parametri
				confHelper.aggiornaConfigurazioneProperties(configurazioneBean);
			} else {
				// reset di eventuali configurazioni salvate in sessione
				ServletUtils.removeConfigurazioneBeanFromSession(session, configurazioneBean.getId());
			}
			
			configurazioneBean.updateConfigurazione(configurazione);
			ServletUtils.saveConfigurazioneBeanIntoSession(session, configurazioneBean, configurazioneBean.getId());
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_LIST));
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
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addPolicyGestioneTokenToDati(tipoOperazione,dati,id,nome,descrizione,tipo,propConfigPolicyGestioneTokenLabelList,propConfigPolicyGestioneTokenList);
				
				dati = confHelper.addPropertiesConfigToDati(tipoOperazione,dati, tipo, configurazioneBean,false);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN,ForwardParams.CHANGE());
			}
			
			// Controlli sui campi immessi
			String tipologia = confCore.getTokenPolicyTipologia().getProperty(tipo);
			boolean isOk = confHelper.policyGestioneTokenCheckData(tipoOperazione, nome,descrizione,tipo,tipologia);
			
			if (isOk) {
				isOk = confHelper.checkPropertiesConfigurationData(tipoOperazione, configurazioneBean, configurazione);
			}
			if (!isOk) {
				
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addPolicyGestioneTokenToDati(tipoOperazione,dati,id,nome,descrizione,tipo,propConfigPolicyGestioneTokenLabelList,propConfigPolicyGestioneTokenList);
						
				dati = confHelper.addPropertiesConfigToDati(tipoOperazione,dati, tipo, configurazioneBean,false);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.CHANGE());
			}
			
			// update sul db
			GenericProperties policy = new GenericProperties();
			policy.setNome(nome);
			policy.setDescrizione(descrizione);
			policy.setTipologia(tipologia);
			policy.setTipo(tipo);
			policy.setId(genericProperties.getId()); 
			
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
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_GESTIONE_POLICY_TOKEN;
			
			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<String> tipologie = new ArrayList<>();
			tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN);
			tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_RETRIEVE_POLICY_TOKEN);		
			
			List<GenericProperties> lista = confCore.gestorePolicyTokenList(idLista, tipologie, ricerca);
			
			confHelper.prepareGestorePolicyTokenList(ricerca, lista, idLista); 
			
			// reset di eventuali configurazioni salvate in sessione
			ServletUtils.removeConfigurazioneBeanFromSession(session, configurazioneBean.getId());
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.CHANGE());
		}  
	}
}
