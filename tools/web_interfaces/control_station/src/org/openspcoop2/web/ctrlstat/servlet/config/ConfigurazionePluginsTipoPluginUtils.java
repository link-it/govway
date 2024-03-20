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

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.constants.PluginCostanti;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.PluginProprietaCompatibilita;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementInfo;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.slf4j.Logger;

/**     
 * ConfigurazionePluginsTipoPluginUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePluginsTipoPluginUtils {
	
	private ConfigurazionePluginsTipoPluginUtils() {}

	public static final boolean ADD_TRANSAZIONI = false;
	public static final boolean ADD_RICERCHE = false;
	public static final boolean ADD_STATISTICHE = false;
	
	public static List<String> getValuesTipoPlugin(boolean addAllarmi) {
		List<String> valori = new ArrayList<>();
		
		valori.add(TipoPlugin.AUTENTICAZIONE.toString());
		
		valori.add(TipoPlugin.AUTORIZZAZIONE.toString());
		valori.add(TipoPlugin.AUTORIZZAZIONE_CONTENUTI.toString());
		
		valori.add(TipoPlugin.RATE_LIMITING.toString());
		
		if(addAllarmi) {
			valori.add(TipoPlugin.ALLARME.toString());
		}
		
		valori.add(TipoPlugin.INTEGRAZIONE.toString());
		
		valori.add(TipoPlugin.CONNETTORE.toString());
		
		valori.add(TipoPlugin.BEHAVIOUR.toString());
		
		valori.add(TipoPlugin.MESSAGE_HANDLER.toString());
		valori.add(TipoPlugin.SERVICE_HANDLER.toString());
		
		valori.add(TipoPlugin.TOKEN_DYNAMIC_DISCOVERY.toString());
		
		valori.add(TipoPlugin.TOKEN_VALIDAZIONE.toString());
		
		valori.add(TipoPlugin.TOKEN_NEGOZIAZIONE.toString());
		
		valori.add(TipoPlugin.ATTRIBUTE_AUTHORITY.toString());
		
		if(ADD_RICERCHE) {
			valori.add(TipoPlugin.RICERCA.toString());
		}
		if(ADD_STATISTICHE) {
			valori.add(TipoPlugin.STATISTICA.toString());
		}
		if(ADD_TRANSAZIONI) {
			valori.add(TipoPlugin.TRANSAZIONE.toString());
		}
				
		return valori;
	}
	
	public static List<String> getLabelsTipoPlugin(boolean addAllarmi) {
		List<String> labels = new ArrayList<>();
		List<String> values = getValuesTipoPlugin(addAllarmi);
		
		for (String value : values) {
			String label = tipoPluginToLabel(TipoPlugin.toEnumConstant(value));
			if(label != null) {
				labels.add(label);
			}
		}
		
		return labels;
	}

	public static String tipoPluginToLabel(TipoPlugin value) {
		switch (value) {
		case ALLARME:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_ALLARME;
		case AUTENTICAZIONE:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_AUTENTICAZIONE;
		case AUTORIZZAZIONE:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_AUTORIZZAZIONE;
		case AUTORIZZAZIONE_CONTENUTI:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_AUTORIZZAZIONE_CONTENUTI;
		case BEHAVIOUR:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_BEHAVIOUR;
		case CONNETTORE:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_CONNETTORE;
		case INTEGRAZIONE:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_INTEGRAZIONE;
		case MESSAGE_HANDLER:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_MESSAGE_HANDLER;
		case RATE_LIMITING:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_RATE_LIMITING;
		case RICERCA:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_RICERCA;
		case SERVICE_HANDLER:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_SERVICE_HANDLER;
		case STATISTICA:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_STATISTICA;
		case TRANSAZIONE:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_TRANSAZIONE;
		case TOKEN_DYNAMIC_DISCOVERY:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_TOKEN_DYNAMIC_DISCOVERY;
		case TOKEN_VALIDAZIONE:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_TOKEN_VALIDAZIONE;
		case TOKEN_NEGOZIAZIONE:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_TOKEN_NEGOZIAZIONE;
		case ATTRIBUTE_AUTHORITY:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE_ATTRIBUTE_AUTHORITY;
		}
		return null;
	}
		
	public static String getApplicabilitaClassePlugin(Plugin plugin) {
		if(plugin.sizePluginProprietaCompatibilitaList() > 0) {
			TipoPlugin tipoPlugin = TipoPlugin.toEnumConstant(plugin.getTipoPlugin());
			
			StringBuilder sb = new StringBuilder();
			
			switch (tipoPlugin) {
			case AUTENTICAZIONE:
			case AUTORIZZAZIONE:
			case AUTORIZZAZIONE_CONTENUTI:
			case INTEGRAZIONE:
				// una property con nome = Ruolo
				for (PluginProprietaCompatibilita prop : plugin.getPluginProprietaCompatibilitaList()) {
					if(sb.length() > 0)
						sb.append(", ");
					
					if(PluginCostanti.FILTRO_RUOLO_VALORE_ENTRAMBI.equals(prop.getValore())) {
						sb.append(PluginCostanti.FILTRO_RUOLO_LABEL_ENTRAMBI);
					}
					else {
						sb.append(prop.getValore());
					}
				}
				return sb.toString();
			case SERVICE_HANDLER:
				// nome handler
				for (PluginProprietaCompatibilita prop : plugin.getPluginProprietaCompatibilitaList()) {
					if(sb.length() > 0)
						sb.append(", ");
					
					if(PluginCostanti.FILTRO_SERVICE_HANDLER_VALORE_INIT.equalsIgnoreCase(prop.getValore())) {
						sb.append(PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL_INIT);
					}
					else if(PluginCostanti.FILTRO_SERVICE_HANDLER_VALORE_EXIT.equalsIgnoreCase(prop.getValore())) {
						sb.append(PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL_EXIT);
					}
					else if(PluginCostanti.FILTRO_SERVICE_HANDLER_VALORE_INTEGRATION_MANAGER_REQUEST.equalsIgnoreCase(prop.getValore())) {
						sb.append(PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL_INTEGRATION_MANAGER_REQUEST);
					}
					else if(PluginCostanti.FILTRO_SERVICE_HANDLER_VALORE_INTEGRATION_MANAGER_RESPONSE.equalsIgnoreCase(prop.getValore())) {
						sb.append(PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL_INTEGRATION_MANAGER_RESPONSE);
					}
					else {
						sb.append(prop.getValore());
					}
				}
				return sb.toString();
			case MESSAGE_HANDLER:
				// message handler e ruolo messa ge handler
				String tipoMessaggio = null;
				String faseProcessamento = null;
				for (PluginProprietaCompatibilita prop : plugin.getPluginProprietaCompatibilitaList()) {
					if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME.equalsIgnoreCase(prop.getNome())) {
						if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_PRE_IN.equalsIgnoreCase(prop.getValore())) {
							faseProcessamento =PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_PRE_IN_SHORT;
						}
						else if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_IN.equalsIgnoreCase(prop.getValore())) {
							faseProcessamento =PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_IN_SHORT;
						}
						else if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_IN_PROTOCOL_INFO.equalsIgnoreCase(prop.getValore())) {
							faseProcessamento =PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_IN_PROTOCOL_INFO_SHORT;
						}
						else if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_OUT.equalsIgnoreCase(prop.getValore())) {
							faseProcessamento =PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_OUT_SHORT;
						}
						else if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_POST_OUT.equalsIgnoreCase(prop.getValore())) {
							faseProcessamento =PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_POST_OUT_SHORT;
						}
						else {
							faseProcessamento =prop.getValore();
						}
					}
					else {
						if(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RICHIESTA.equals(prop.getValore())) {
							tipoMessaggio = PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_LABEL_RICHIESTA;
						}
						else {
							tipoMessaggio = PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_LABEL_RISPOSTA;
						}
					}
				}
				if(sb.length() > 0)
					sb.append(", ");
					
				sb.append(tipoMessaggio).append(" (").append(faseProcessamento).append(")");
				return sb.toString();
			case ALLARME:
				// una property con nome = Applicabilita
				for (PluginProprietaCompatibilita prop : plugin.getPluginProprietaCompatibilitaList()) {
					if(sb.length() > 0)
						sb.append(", ");
					
					if(PluginCostanti.FILTRO_APPLICABILITA_VALORE_IMPLEMENTAZIONE_API.equals(prop.getValore())) {
						sb.append(PluginCostanti.FILTRO_APPLICABILITA_LABEL_IMPLEMENTAZIONE_API);
					}
					else {
						sb.append(prop.getValore());
					}
				}
				return sb.toString();
			case BEHAVIOUR:
			case CONNETTORE:
			case RATE_LIMITING:
			case RICERCA:
			case STATISTICA:
			case TRANSAZIONE:
			case TOKEN_DYNAMIC_DISCOVERY:
			case TOKEN_VALIDAZIONE:
			case TOKEN_NEGOZIAZIONE:
			case ATTRIBUTE_AUTHORITY:
				return null;
			}
		}
		
		return null;
	}
	
	public static TipoPlugin getTipoPluginDefault() {
		return null;
	}
	
	public static List<PluginProprietaCompatibilita> getApplicabilitaClassePlugin(TipoPlugin tipoPlugin, String ruolo, String shTipo, String mhTipo, String mhRuolo, String applicabilita) {
		List<PluginProprietaCompatibilita> lista = new ArrayList<>();
			
			switch (tipoPlugin) {
			case AUTENTICAZIONE:
			case AUTORIZZAZIONE:
			case AUTORIZZAZIONE_CONTENUTI:
			case INTEGRAZIONE:
				PluginProprietaCompatibilita propRuolo = new PluginProprietaCompatibilita();

				propRuolo.setNome(PluginCostanti.FILTRO_RUOLO_NOME);
				propRuolo.setValore(ruolo);
				
				lista.add(propRuolo);
				break;
			case SERVICE_HANDLER:
				PluginProprietaCompatibilita propShTipo = new PluginProprietaCompatibilita();
				
				propShTipo.setNome(PluginCostanti.FILTRO_SERVICE_HANDLER_NOME);
				propShTipo.setValore(shTipo);
				
				lista.add(propShTipo);
				break;
			case MESSAGE_HANDLER:
				PluginProprietaCompatibilita propMhTipo = new PluginProprietaCompatibilita();
				
				propMhTipo.setNome(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME);
				propMhTipo.setValore(mhTipo);
				
				lista.add(propMhTipo);
				
				PluginProprietaCompatibilita propMhRuolo = new PluginProprietaCompatibilita();
				
				propMhRuolo.setNome(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME);
				propMhRuolo.setValore(mhRuolo);
				
				lista.add(propMhRuolo);
				break;
			case ALLARME:
				PluginProprietaCompatibilita propApplicabilita = new PluginProprietaCompatibilita();

				propApplicabilita.setNome(PluginCostanti.FILTRO_APPLICABILITA_NOME);
				propApplicabilita.setValore(applicabilita);
				
				lista.add(propApplicabilita);
				break;
			case BEHAVIOUR:
			case CONNETTORE:
			case RATE_LIMITING:
			case RICERCA:
			case STATISTICA:
			case TRANSAZIONE:
			case TOKEN_DYNAMIC_DISCOVERY:
			case TOKEN_VALIDAZIONE:
			case TOKEN_NEGOZIAZIONE:
			case ATTRIBUTE_AUTHORITY:
				break;
			}
			
		return lista;
	}
	
	public static void addInfoClassePlugin(DataElement de, TipoPlugin tipoPlugin, String ruolo, String shTipo, String mhTipo, String mhRuolo,
			boolean integrationManagerEnabled) {
		
		String titolo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_CLASS_NAME;
		DataElementInfo info = new DataElementInfo(titolo);
		
		String infoSingola = DynamicHelperCostanti.PLUGIN_CLASSNAME_INFO_SINGOLA;
		String infoMultiplaAnd = DynamicHelperCostanti.PLUGIN_CLASSNAME_INFO_MULTIPLA_AND;
		String infoMultiplaOr = DynamicHelperCostanti.PLUGIN_CLASSNAME_INFO_MULTIPLA_OR;
		
		List<String> listBody = new ArrayList<>();
		
		switch (tipoPlugin) {
		case AUTENTICAZIONE:
			
			if(ruolo==null || StringUtils.isEmpty(ruolo) || !PluginCostanti.FILTRO_RUOLO_VALORI.contains(ruolo)) {
				ruolo = PluginCostanti.FILTRO_RUOLO_VALORI.get(0);
			}
			
			if(Filtri.FILTRO_RUOLO_VALORE_EROGAZIONE.equals(ruolo)) {
				info.setHeaderBody(infoSingola);
				listBody.add(org.openspcoop2.pdd.core.autenticazione.pa.IAutenticazionePortaApplicativa.class.getName());
			}
			else if(Filtri.FILTRO_RUOLO_VALORE_FRUIZIONE.equals(ruolo)) {
				info.setHeaderBody(infoSingola);
				listBody.add(org.openspcoop2.pdd.core.autenticazione.pd.IAutenticazionePortaDelegata.class.getName());
			}
			else {
				info.setHeaderBody(infoMultiplaAnd);
				listBody.add(org.openspcoop2.pdd.core.autenticazione.pa.IAutenticazionePortaApplicativa.class.getName());
				listBody.add(org.openspcoop2.pdd.core.autenticazione.pd.IAutenticazionePortaDelegata.class.getName());
			}
			break;
		case AUTORIZZAZIONE:
			
			if(ruolo==null || StringUtils.isEmpty(ruolo) || !PluginCostanti.FILTRO_RUOLO_VALORI.contains(ruolo)) {
				ruolo = PluginCostanti.FILTRO_RUOLO_VALORI.get(0);
			}
			
			if(Filtri.FILTRO_RUOLO_VALORE_EROGAZIONE.equals(ruolo)) {
				info.setHeaderBody(infoSingola);
				listBody.add(org.openspcoop2.pdd.core.autorizzazione.pa.IAutorizzazionePortaApplicativa.class.getName());
			}
			else if(Filtri.FILTRO_RUOLO_VALORE_FRUIZIONE.equals(ruolo)) {
				info.setHeaderBody(infoSingola);
				listBody.add(org.openspcoop2.pdd.core.autorizzazione.pd.IAutorizzazionePortaDelegata.class.getName());
			}
			else {
				info.setHeaderBody(infoMultiplaAnd);
				listBody.add(org.openspcoop2.pdd.core.autorizzazione.pa.IAutorizzazionePortaApplicativa.class.getName());
				listBody.add(org.openspcoop2.pdd.core.autorizzazione.pd.IAutorizzazionePortaDelegata.class.getName());
			}
			break;
		case AUTORIZZAZIONE_CONTENUTI:
			
			if(ruolo==null || StringUtils.isEmpty(ruolo) || !PluginCostanti.FILTRO_RUOLO_VALORI.contains(ruolo)) {
				ruolo = PluginCostanti.FILTRO_RUOLO_VALORI.get(0);
			}
			
			if(Filtri.FILTRO_RUOLO_VALORE_EROGAZIONE.equals(ruolo)) {
				info.setHeaderBody(infoSingola);
				listBody.add(org.openspcoop2.pdd.core.autorizzazione.pa.IAutorizzazioneContenutoPortaApplicativa.class.getName());
			}
			else if(Filtri.FILTRO_RUOLO_VALORE_FRUIZIONE.equals(ruolo)) {
				info.setHeaderBody(infoSingola);
				listBody.add(org.openspcoop2.pdd.core.autorizzazione.pd.IAutorizzazioneContenutoPortaDelegata.class.getName());
			}
			else {
				info.setHeaderBody(infoMultiplaAnd);
				listBody.add(org.openspcoop2.pdd.core.autorizzazione.pa.IAutorizzazioneContenutoPortaApplicativa.class.getName());
				listBody.add(org.openspcoop2.pdd.core.autorizzazione.pd.IAutorizzazioneContenutoPortaDelegata.class.getName());
			}
			break;
		case INTEGRAZIONE:
			
			if(ruolo==null || StringUtils.isEmpty(ruolo) || !PluginCostanti.FILTRO_RUOLO_VALORI.contains(ruolo)) {
				ruolo = PluginCostanti.FILTRO_RUOLO_VALORI.get(0);
			}
			
			if(Filtri.FILTRO_RUOLO_VALORE_EROGAZIONE.equals(ruolo)) {
				info.setHeaderBody(infoMultiplaOr);
				listBody.add(org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePA.class.getName());
				listBody.add(org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePASoap.class.getName());
			}
			else if(Filtri.FILTRO_RUOLO_VALORE_FRUIZIONE.equals(ruolo)) {
				info.setHeaderBody(infoMultiplaOr);
				listBody.add(org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePD.class.getName());
				listBody.add(org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePDSoap.class.getName());
			}
			else {
				info.setHeaderBody(infoMultiplaAnd);
				listBody.add(org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePA.class.getName() + "(o versione Soap)");
				listBody.add(org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePD.class.getName() + "(o versione Soap)");
			}
			break;
		case RATE_LIMITING:
			info.setHeaderBody(infoSingola);
			listBody.add(org.openspcoop2.pdd.core.controllo_traffico.plugins.IRateLimiting.class.getName());
			break;
		case CONNETTORE:
			info.setHeaderBody(infoSingola);
			listBody.add(org.openspcoop2.pdd.core.connettori.IConnettore.class.getName());
			break;
		case ALLARME:
			info.setHeaderBody(infoSingola);
			listBody.add(org.openspcoop2.monitor.sdk.plugins.IAlarmProcessing.class.getName());
			break;
		case BEHAVIOUR:
			info.setHeaderBody(infoSingola);
			listBody.add(org.openspcoop2.pdd.core.behaviour.IBehaviour.class.getName());
			break;
		case SERVICE_HANDLER:{
			
			List<String> values = integrationManagerEnabled ? PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_CON_INTEGRATION_MANAGER : PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_SENZA_INTEGRATION_MANAGER;
			if(shTipo==null || StringUtils.isEmpty(shTipo) || !values.contains(shTipo)) {
				shTipo = values.get(0);
			}
			
			if(PluginCostanti.FILTRO_SERVICE_HANDLER_VALORE_INIT.equals(shTipo)) {
				info.setHeaderBody(infoSingola);
				listBody.add(org.openspcoop2.pdd.core.handlers.InitHandler.class.getName());
			}
			else if(PluginCostanti.FILTRO_SERVICE_HANDLER_VALORE_EXIT.equals(shTipo)) {
				info.setHeaderBody(infoSingola);
				listBody.add(org.openspcoop2.pdd.core.handlers.ExitHandler.class.getName());
			}
			else if(PluginCostanti.FILTRO_SERVICE_HANDLER_VALORE_INTEGRATION_MANAGER_REQUEST.equals(shTipo)) {
				info.setHeaderBody(infoSingola);
				listBody.add(org.openspcoop2.pdd.core.handlers.IntegrationManagerRequestHandler.class.getName());
			}
			else if(PluginCostanti.FILTRO_SERVICE_HANDLER_VALORE_INTEGRATION_MANAGER_RESPONSE.equals(shTipo)) {
				info.setHeaderBody(infoSingola);
				listBody.add(org.openspcoop2.pdd.core.handlers.IntegrationManagerResponseHandler.class.getName());
			}
			break;
		}
		case MESSAGE_HANDLER:{
			
			if(mhRuolo==null || StringUtils.isEmpty(mhRuolo) || !PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORI.contains(mhRuolo)) {
				mhRuolo = PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORI.get(0);
			}
			boolean isRisposta = PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RISPOSTA.equals(mhRuolo);
			List<String> values = isRisposta ? PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RISPOSTA : PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RICHIESTA;
			if(mhTipo==null || StringUtils.isEmpty(mhTipo) || !values.contains(mhTipo)) {
				mhTipo = values.get(0);
			}
			
			if(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RICHIESTA.equals(mhRuolo)) {
				if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_PRE_IN.equals(mhTipo)) {
					info.setHeaderBody(infoSingola);
					listBody.add(org.openspcoop2.pdd.core.handlers.PreInRequestHandler.class.getName());
				}
				else if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_IN.equals(mhTipo)) {
					info.setHeaderBody(infoSingola);
					listBody.add(org.openspcoop2.pdd.core.handlers.InRequestHandler.class.getName());
				}
				else if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_IN_PROTOCOL_INFO.equals(mhTipo)) {
					info.setHeaderBody(infoSingola);
					listBody.add(org.openspcoop2.pdd.core.handlers.InRequestProtocolHandler.class.getName());
				}
				else if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_OUT.equals(mhTipo)) {
					info.setHeaderBody(infoSingola);
					listBody.add(org.openspcoop2.pdd.core.handlers.OutRequestHandler.class.getName());
				}
				else if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_POST_OUT.equals(mhTipo)) {
					info.setHeaderBody(infoSingola);
					listBody.add(org.openspcoop2.pdd.core.handlers.PostOutRequestHandler.class.getName());
				}
			}
			else {
				if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_PRE_IN.equals(mhTipo)) {
					info.setHeaderBody(infoSingola);
					listBody.add(org.openspcoop2.pdd.core.handlers.PreInResponseHandler.class.getName());
				}
				else if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_IN.equals(mhTipo)) {
					info.setHeaderBody(infoSingola);
					listBody.add(org.openspcoop2.pdd.core.handlers.InResponseHandler.class.getName());
				}
				else if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_OUT.equals(mhTipo)) {
					info.setHeaderBody(infoSingola);
					listBody.add(org.openspcoop2.pdd.core.handlers.OutResponseHandler.class.getName());
				}
				else if(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_POST_OUT.equals(mhTipo)) {
					info.setHeaderBody(infoSingola);
					listBody.add(org.openspcoop2.pdd.core.handlers.PostOutResponseHandler.class.getName());
				}
			}
			break;
		}
		case RICERCA:
		case STATISTICA:
		case TRANSAZIONE:
			break;
		case TOKEN_DYNAMIC_DISCOVERY:
			info.setHeaderBody(infoSingola);
			listBody.add(org.openspcoop2.pdd.core.token.parser.IDynamicDiscoveryParser.class.getName());
			break;
		case TOKEN_VALIDAZIONE:
			info.setHeaderBody(infoSingola);
			listBody.add(org.openspcoop2.pdd.core.token.parser.ITokenParser.class.getName());
			break;
		case TOKEN_NEGOZIAZIONE:
			info.setHeaderBody(infoSingola);
			listBody.add(org.openspcoop2.pdd.core.token.parser.INegoziazioneTokenParser.class.getName());
			break;
		case ATTRIBUTE_AUTHORITY:
			info.setHeaderBody(infoSingola);
			listBody.add(org.openspcoop2.pdd.core.token.attribute_authority.IRetrieveAttributeAuthorityResponseParser.class.getName());
			break;
		}
		
		if(!listBody.isEmpty()) {
			info.setListBody(listBody);
			de.setInfo(info);
		}
		
	} 
	
	public static List<DataElement> getSezioneDinamicaClassePlugin(List<DataElement> dati, TipoPlugin tipoPlugin, String ruolo, String shTipo, String mhTipo, String mhRuolo, String applicabilita,
			boolean integrationManagerEnabled) {
			switch (tipoPlugin) {
			case AUTENTICAZIONE:
			case AUTORIZZAZIONE:
			case AUTORIZZAZIONE_CONTENUTI:
			case INTEGRAZIONE:
				DataElement deRuolo = new DataElement();
				deRuolo.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_RUOLO);
				deRuolo.setLabels(PluginCostanti.FILTRO_RUOLO_LABELS);
				deRuolo.setValues(PluginCostanti.FILTRO_RUOLO_VALORI);
				deRuolo.setType(DataElementType.SELECT);
				deRuolo.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_RUOLO);
				deRuolo.setSelected(ruolo);
				deRuolo.setPostBack(true); // serve per 'info' del className
				dati.add(deRuolo);
				
				break;
			case SERVICE_HANDLER:{
				DataElement deShTipo = new DataElement();
				deShTipo.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_SERVICE_HANDLER);
				List<String> values = integrationManagerEnabled ? PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_CON_INTEGRATION_MANAGER : PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_SENZA_INTEGRATION_MANAGER;
				List<String> labels = integrationManagerEnabled ? PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL_CON_INTEGRATION_MANAGER : PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL_SENZA_INTEGRATION_MANAGER;
				deShTipo.setLabels(labels);
				deShTipo.setValues(values);
				deShTipo.setType(DataElementType.SELECT);
				deShTipo.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_SERVICE_HANDLER);
				deShTipo.setSelected(shTipo);
				deShTipo.setPostBack(true); // serve per 'info' del className
				dati.add(deShTipo);
				break;
			}
			case MESSAGE_HANDLER:{
				
				DataElement deMhRuolo = new DataElement();
				
				deMhRuolo.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_RUOLO_MESSAGE_HANDLER);
				deMhRuolo.setLabels(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_LABELS);
				deMhRuolo.setValues(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORI);
				deMhRuolo.setType(DataElementType.SELECT);
				deMhRuolo.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_RUOLO_MESSAGE_HANDLER);
				deMhRuolo.setSelected(mhRuolo);
				deMhRuolo.setPostBack(true); // serve per 'info' del className
				dati.add(deMhRuolo);
				
				DataElement deMhTipo = new DataElement();
				deMhTipo.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_FASE_MESSAGE_HANDLER);
				boolean isRisposta = PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RISPOSTA.equals(mhRuolo);
				List<String> values = isRisposta ? PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RISPOSTA : PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RICHIESTA;
				List<String> labels = isRisposta ? PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_RISPOSTA : PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_RICHIESTA;
				deMhTipo.setLabels(labels);
				deMhTipo.setValues(values);
				deMhTipo.setType(DataElementType.SELECT);
				deMhTipo.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_FASE_MESSAGE_HANDLER);
				deMhTipo.setSelected(mhTipo);
				deMhTipo.setPostBack(true); // serve per 'info' del className
				dati.add(deMhTipo);
				
				break;
			}
			case ALLARME:
				
				DataElement deApplicabilita = new DataElement();
				deApplicabilita.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_APPLICABILITA);
				deApplicabilita.setLabels(PluginCostanti.FILTRO_APPLICABILITA_LABELS);
				deApplicabilita.setValues(PluginCostanti.FILTRO_APPLICABILITA_VALORI);
				deApplicabilita.setType(DataElementType.SELECT);
				deApplicabilita.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_APPLICABILITA);
				deApplicabilita.setSelected(applicabilita);
				deApplicabilita.setPostBack(false); // non serve per 'info' del className, non cambia
				dati.add(deApplicabilita);
				
				break;
				
			case BEHAVIOUR:
			case CONNETTORE:
			case RATE_LIMITING:
			case RICERCA:
			case STATISTICA:
			case TRANSAZIONE:
			case TOKEN_DYNAMIC_DISCOVERY:
			case TOKEN_VALIDAZIONE:
			case TOKEN_NEGOZIAZIONE:
			case ATTRIBUTE_AUTHORITY:
				break;
			}
			
		return dati;
	}
	
	public static String getValoreProprieta(Plugin plugin, String nome) {
		if(plugin.sizePluginProprietaCompatibilitaList() > 0) {
			TipoPlugin tipoPlugin = TipoPlugin.toEnumConstant(plugin.getTipoPlugin());
			
			switch (tipoPlugin) {
			case AUTENTICAZIONE:
			case AUTORIZZAZIONE:
			case AUTORIZZAZIONE_CONTENUTI:
			case INTEGRAZIONE:
			case SERVICE_HANDLER:
			case MESSAGE_HANDLER:
			case ALLARME:
				// una property con nome = Ruolo
				for (PluginProprietaCompatibilita prop : plugin.getPluginProprietaCompatibilitaList()) {
					if(prop.getNome().equals(nome))
						return prop.getValore();
				}
				return null;
			case BEHAVIOUR:
			case CONNETTORE:
			case RATE_LIMITING:
			case RICERCA:
			case STATISTICA:
			case TRANSAZIONE:
			case TOKEN_DYNAMIC_DISCOVERY:
			case TOKEN_VALIDAZIONE:
			case TOKEN_NEGOZIAZIONE:
			case ATTRIBUTE_AUTHORITY:
				return null;
			}
		}
		
		return null;
	}
	
	public static void addFiltriSpecificiTipoPlugin(PageData pd, Logger log, ISearch ricerca, int idLista, String valoreTipoPlugin , int size,
			boolean integrationManagerEnabled) throws Exception {
		TipoPlugin tipoPlugin = TipoPlugin.toEnumConstant(valoreTipoPlugin);
		
		switch (tipoPlugin) {
		case AUTENTICAZIONE:
		case AUTORIZZAZIONE:
		case AUTORIZZAZIONE_CONTENUTI:
		case INTEGRAZIONE:
			String filtroRuolo = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_RUOLO_NOME);
			addFilterTipoPlugin(pd, log, PluginCostanti.FILTRO_RUOLO_NOME, PluginCostanti.FILTRO_RUOLO_LABEL, filtroRuolo, 
					PluginCostanti.FILTRO_RUOLO_VALORI_SENZA_ENTRAMBI, PluginCostanti.FILTRO_RUOLO_LABELS_SENZA_ENTRAMBI, false, size);
			break;
		case SERVICE_HANDLER:{
			
			List<String> values = integrationManagerEnabled ? PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_CON_INTEGRATION_MANAGER : PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_SENZA_INTEGRATION_MANAGER;
			List<String> labels = integrationManagerEnabled ? PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL_CON_INTEGRATION_MANAGER : PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL_SENZA_INTEGRATION_MANAGER;
			
			String filtroShTipo = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_SERVICE_HANDLER_NOME);
			addFilterTipoPlugin(pd, log, PluginCostanti.FILTRO_SERVICE_HANDLER_NOME, PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL, filtroShTipo, 
					values, labels, false, size);
			break;
		}
		case MESSAGE_HANDLER:{
			// message handler e ruolo
			
			String filtroMhRuolo = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME);
			addFilterTipoPlugin(pd, log, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_LABEL, filtroMhRuolo, 
					PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORI, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_LABELS, true, size);
					
			boolean isRisposta = PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RISPOSTA.equals(filtroMhRuolo);
			
			List<String> values = isRisposta ? PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RISPOSTA : PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RICHIESTA;
			List<String> labels = isRisposta ? PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_RISPOSTA : PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_RICHIESTA;
			
			String filtroMhTipo = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME);
			addFilterTipoPlugin(pd, log, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL, filtroMhTipo, 
					values, labels, false, size);
			
			break;
		}
		case ALLARME:
			String filtroApplicabilita = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_APPLICABILITA_NOME);
			addFilterTipoPlugin(pd, log, PluginCostanti.FILTRO_APPLICABILITA_NOME, PluginCostanti.FILTRO_APPLICABILITA_LABEL, filtroApplicabilita, 
					PluginCostanti.FILTRO_APPLICABILITA_VALORI_SENZA_QUALSIASI, PluginCostanti.FILTRO_APPLICABILITA_LABELS_SENZA_QUALSIASI, false, size);
			break;
		case BEHAVIOUR:
		case CONNETTORE:
		case RATE_LIMITING:
		case RICERCA:
		case STATISTICA:
		case TRANSAZIONE:
		case TOKEN_DYNAMIC_DISCOVERY:
		case TOKEN_VALIDAZIONE:
		case TOKEN_NEGOZIAZIONE:
		case ATTRIBUTE_AUTHORITY:
			break;
		}
		
	}
	
	public static void addFilterTipoPlugin(PageData pd, Logger log, String nomeFiltro, String labelFiltro, String valoreFiltro, List<String> valuesFiltro, List<String> labelsFiltro, boolean postBack, int size) throws Exception{
		try {
			
			int length = 1;
			if(valuesFiltro!=null && !valuesFiltro.isEmpty()) {
				length+=valuesFiltro.size();
			}
			String [] values = new String[length];
			String [] labels = new String[length];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_RUOLO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_RUOLO_QUALSIASI;
			if(valuesFiltro!=null && valuesFiltro.size()>0) {
				for (int i =0; i < valuesFiltro.size() ; i ++) {
					labels[i+1] = labelsFiltro.get(i);
					values[i+1] = valuesFiltro.get(i);
				}
			}
			
			pd.addFilter(nomeFiltro, labelFiltro, valoreFiltro, values, labels, postBack, size);
			
		} catch (Exception e) {
			log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
}
