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

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.constants.PluginCostanti;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita;
import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.lib.mvc.DataElement;
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

	public final static boolean addTransazioni = false;
	public final static boolean addRicerche = false;
	public final static boolean addStatistiche = false;
	public final static boolean addAllarmi = false;
	
	public static List<String> getValuesTipoPlugin() {
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
		
		if(addRicerche) {
			valori.add(TipoPlugin.RICERCA.toString());
		}
		if(addStatistiche) {
			valori.add(TipoPlugin.STATISTICA.toString());
		}
		if(addTransazioni) {
			valori.add(TipoPlugin.TRANSAZIONE.toString());
		}
		
		return valori;
	}
	
	public static List<String> getLabelsTipoPlugin() {
		List<String> labeles = new ArrayList<>();
		List<String> values = getValuesTipoPlugin();
		
		for (String value : values) {
			String label = tipoPluginToLabel(TipoPlugin.toEnumConstant(value));
			if(label != null) {
				labeles.add(label);
			}
		}
		
		return labeles;
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
					
					sb.append(prop.getValore());
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
			case BEHAVIOUR:
			case CONNETTORE:
			case RATE_LIMITING:
			case RICERCA:
			case STATISTICA:
			case TRANSAZIONE:
				return null;
			}
		}
		
		return null;
	}
	
	public static TipoPlugin getTipoPluginDefault() {
		//return TipoPlugin.AUTENTICAZIONE;
		return null;
	}
	
	public static List<PluginProprietaCompatibilita> getApplicabilitaClassePlugin(TipoPlugin tipoPlugin, String ruolo, String shTipo, String mhTipo, String mhRuolo) {
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
			case BEHAVIOUR:
			case CONNETTORE:
			case RATE_LIMITING:
			case RICERCA:
			case STATISTICA:
			case TRANSAZIONE:
				break;
			}
			
		return lista;
	}
	
	public static Vector<DataElement> getSezioneDinamicaClassePlugin(Vector<DataElement> dati, TipoPlugin tipoPlugin, String ruolo, String shTipo, String mhTipo, String mhRuolo,
			boolean integrationManagerEnabled) {
			switch (tipoPlugin) {
			case AUTENTICAZIONE:
			case AUTORIZZAZIONE:
			case AUTORIZZAZIONE_CONTENUTI:
			case INTEGRAZIONE:
				DataElement deRuolo = new DataElement();
				
				deRuolo.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_RUOLO);
				deRuolo.setLabels(PluginCostanti.FILTRO_RUOLO_VALORI);
				deRuolo.setValues(PluginCostanti.FILTRO_RUOLO_VALORI);
				deRuolo.setType(DataElementType.SELECT);
				deRuolo.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_RUOLO);
				deRuolo.setSelected(ruolo);
				dati.addElement(deRuolo);
				
				break;
			case SERVICE_HANDLER:{
				DataElement deShTipo = new DataElement();
				deShTipo.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_SERVICE_HANDLER);
				List<String> values = integrationManagerEnabled ? PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_CON_INTEGRATION_MANAGER : PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_SENZA_INTEGRATION_MANAGER;
				List<String> labeles = integrationManagerEnabled ? PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL_CON_INTEGRATION_MANAGER : PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL_SENZA_INTEGRATION_MANAGER;
				deShTipo.setLabels(labeles);
				deShTipo.setValues(values);
				deShTipo.setType(DataElementType.SELECT);
				deShTipo.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_SERVICE_HANDLER);
				deShTipo.setSelected(shTipo);
				dati.addElement(deShTipo);
				break;
			}
			case MESSAGE_HANDLER:{
				
				DataElement deMhRuolo = new DataElement();
				
				deMhRuolo.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_RUOLO_MESSAGE_HANDLER);
				deMhRuolo.setLabels(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_LABELES);
				deMhRuolo.setValues(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORI);
				deMhRuolo.setType(DataElementType.SELECT);
				deMhRuolo.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_RUOLO_MESSAGE_HANDLER);
				deMhRuolo.setSelected(mhRuolo);
				dati.addElement(deMhRuolo);
				
				DataElement deMhTipo = new DataElement();
				deMhTipo.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_FASE_MESSAGE_HANDLER);
				boolean isRisposta = PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RISPOSTA.equals(mhRuolo);
				List<String> values = isRisposta ? PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RISPOSTA : PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RICHIESTA;
				List<String> labeles = isRisposta ? PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_RISPOSTA : PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_RICHIESTA;
				deMhTipo.setLabels(labeles);
				deMhTipo.setValues(values);
				deMhTipo.setType(DataElementType.SELECT);
				deMhTipo.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_FASE_MESSAGE_HANDLER);
				deMhTipo.setSelected(mhTipo);
				dati.addElement(deMhTipo);
				
				break;
			}
			case ALLARME:
			case BEHAVIOUR:
			case CONNETTORE:
			case RATE_LIMITING:
			case RICERCA:
			case STATISTICA:
			case TRANSAZIONE:
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
				// una property con nome = Ruolo
				for (PluginProprietaCompatibilita prop : plugin.getPluginProprietaCompatibilitaList()) {
					if(prop.getNome().equals(nome))
						return prop.getValore();
				}
				return null;
			case SERVICE_HANDLER:
				// nome handler
				for (PluginProprietaCompatibilita prop : plugin.getPluginProprietaCompatibilitaList()) {
					if(prop.getNome().equals(nome))
						return prop.getValore();
				}
				return null;
			case MESSAGE_HANDLER:
				// message handler e ruolo messa ge handler
				for (PluginProprietaCompatibilita prop : plugin.getPluginProprietaCompatibilitaList()) {
					if(prop.getNome().equals(nome))
						return prop.getValore();
				}
				return null;
			case ALLARME:
			case BEHAVIOUR:
			case CONNETTORE:
			case RATE_LIMITING:
			case RICERCA:
			case STATISTICA:
			case TRANSAZIONE:
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
					PluginCostanti.FILTRO_RUOLO_VALORI, PluginCostanti.FILTRO_RUOLO_VALORI, false, size);
			break;
		case SERVICE_HANDLER:{
			
			List<String> values = integrationManagerEnabled ? PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_CON_INTEGRATION_MANAGER : PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_SENZA_INTEGRATION_MANAGER;
			List<String> labeles = integrationManagerEnabled ? PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL_CON_INTEGRATION_MANAGER : PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL_SENZA_INTEGRATION_MANAGER;
			
			String filtroShTipo = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_SERVICE_HANDLER_NOME);
			addFilterTipoPlugin(pd, log, PluginCostanti.FILTRO_SERVICE_HANDLER_NOME, PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL, filtroShTipo, 
					values, labeles, false, size);
			break;
		}
		case MESSAGE_HANDLER:{
			// message handler e ruolo
			
			String filtroMhRuolo = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME);
			addFilterTipoPlugin(pd, log, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_LABEL, filtroMhRuolo, 
					PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORI, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_LABELES, true, size);
					
			boolean isRisposta = PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RISPOSTA.equals(filtroMhRuolo);
			
			List<String> values = isRisposta ? PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RISPOSTA : PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RICHIESTA;
			List<String> labeles = isRisposta ? PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_RISPOSTA : PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_RICHIESTA;
			
			String filtroMhTipo = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME);
			addFilterTipoPlugin(pd, log, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL, filtroMhTipo, 
					values, labeles, false, size);
			
			break;
		}
		case ALLARME:
		case BEHAVIOUR:
		case CONNETTORE:
		case RATE_LIMITING:
		case RICERCA:
		case STATISTICA:
		case TRANSAZIONE:
			break;
		}
		
	}
	
	public static void addFilterTipoPlugin(PageData pd, Logger log, String nomeFiltro, String labelFiltro, String valoreFiltro, List<String> valuesFiltro, List<String> labelsFiltro, boolean postBack, int size) throws Exception{
		try {
			
			int length = 1;
			if(valuesFiltro!=null && valuesFiltro.size()>0) {
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
