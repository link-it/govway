/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;
import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;
import org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin;
import org.openspcoop2.monitor.engine.statistic.StatisticByResource;
import org.openspcoop2.monitor.engine.statistic.StatisticByState;

/**
 * StatisticsServiceLibrary
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsServiceLibrary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BasicServiceLibrary basicServiceLibrary;
	private List<ConfigurazioneStatistica> statisticsActionLibrary;
	private List<ConfigurazioneStatistica> statisticsActionAllLibrary;
	private boolean pluginStatiTransazioniEnabled = false; // plugin org.openspcoop2.monitor.engine.plugins.statistic.StatisticByState
	private List<String> pluginRisorseTransazioni = new ArrayList<String>(); // plugin org.openspcoop2.monitor.engine.plugins.statistic.StatisticByResource

	public BasicServiceLibrary getBasicServiceLibrary() {
		return this.basicServiceLibrary;
	}
	public void setBasicServiceLibrary(BasicServiceLibrary basicServiceLibrary) {
		this.basicServiceLibrary = basicServiceLibrary;
	}
	public List<ConfigurazioneStatistica> getStatisticsActionLibrary() {
		return this.statisticsActionLibrary;
	}
	public void setStatisticsActionLibrary(List<ConfigurazioneStatistica> searchActionLibrary) {
		this.statisticsActionLibrary = searchActionLibrary;
	}
	public List<ConfigurazioneStatistica> getStatisticsActionAllLibrary() {
		return this.statisticsActionAllLibrary;
	}
	public void setStatisticsActionAllLibrary(
			List<ConfigurazioneStatistica> searchActionAllLibrary) {
		this.statisticsActionAllLibrary = searchActionAllLibrary;
	}
	public boolean isPluginStatiTransazioniEnabled() {
		return this.pluginStatiTransazioniEnabled;
	}
	public void setPluginStatiTransazioniEnabled(boolean pluginStatiTransazioniEnabled) {
		this.pluginStatiTransazioniEnabled = pluginStatiTransazioniEnabled;
	}
	public List<String> getPluginRisorseTransazioni() {
		return this.pluginRisorseTransazioni;
	}
	public void setPluginRisorseTransazioni(List<String> pluginRisorseTransazioni) {
		this.pluginRisorseTransazioni = pluginRisorseTransazioni;
	}



	// ****** UTILS *************** 

	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();

		if(this.basicServiceLibrary==null){
			bf.append("BasicServiceLibrary: notDefined");
			bf.append("\n");
		}
		else{
			bf.append(this.basicServiceLibrary.toString());
		}

			
		if(this.getStatisticsActionLibrary()!=null){
			for (int i = 0; i < this.getStatisticsActionLibrary().size(); i++) {
				bf.append("StatisticsActionLibrary["+i+"]: ");
				bf.append("id[").
				append(this.getStatisticsActionLibrary().get(i).getIdConfigurazioneStatistica()).
				append("] plugin.className[").
				append(this.getStatisticsActionLibrary().get(i).getPlugin().getClassName()).
				append("] plugin.description[").
				append(this.getStatisticsActionLibrary().get(i).getPlugin().getDescrizione()).
				append("] plugin.label[").
				append(this.getStatisticsActionLibrary().get(i).getPlugin().getLabel()).
				append("] label[").
				append(this.getStatisticsActionLibrary().get(i).getLabel()).
				append("] enabled[").
				append(this.getStatisticsActionLibrary().get(i).isEnabled()).
				append("]");
				bf.append("\n");
			}
		}else{
			bf.append("StatisticsActionLibrary: ");
			bf.append("-");
		}
		bf.append("\n");

		
		if(this.getStatisticsActionAllLibrary()!=null){
			for (int i = 0; i < this.getStatisticsActionAllLibrary().size(); i++) {
				bf.append("StatisticsActionAllLibrary["+i+"]: ");
				bf.append("id[").
				append(this.getStatisticsActionAllLibrary().get(i).getIdConfigurazioneStatistica()).
				append("] plugin.className[").
				append(this.getStatisticsActionAllLibrary().get(i).getPlugin().getClassName()).
				append("] plugin.description[").
				append(this.getStatisticsActionAllLibrary().get(i).getPlugin().getDescrizione()).
				append("] plugin.label[").
				append(this.getStatisticsActionAllLibrary().get(i).getPlugin().getLabel()).
				append("] label[").
				append(this.getStatisticsActionAllLibrary().get(i).getLabel()).
				append("] enabled[").
				append(this.getStatisticsActionAllLibrary().get(i).isEnabled()).
				append("]");
				bf.append("\n");
			}
		}else{
			bf.append("StatisticsActionAllLibrary: ");
			bf.append("-");
		}
		bf.append("\n");


		return bf.toString();
	}


	public List<ConfigurazioneStatistica> mergeServiceActionSearchLibrary(boolean onlyEnabled, boolean orderByLabel) throws Exception{

		Hashtable<String,ConfigurazioneStatistica> plugins = new Hashtable<String,ConfigurazioneStatistica>();

		if(this.pluginStatiTransazioniEnabled){
			ConfigurazioneStatistica conf = this.buildNewConfigurazioneStatistica(StatisticByState.ID, StatisticByState.LABEL, 
					StatisticByState.DESCRIZIONE,StatisticByState.class);
			plugins.put(conf.getPlugin().getClassName(), conf);
		}
		
		if(this.pluginRisorseTransazioni!=null && this.pluginRisorseTransazioni.size()>0){
			ConfigurazioneStatistica conf = this.buildNewConfigurazioneStatistica(StatisticByResource.ID, StatisticByResource.LABEL, 
					StatisticByResource.DESCRIZIONE,StatisticByResource.class);
			plugins.put(conf.getPlugin().getClassName(), conf);
		}
		
		// Leggo le risorse associate all'azione specifica
		if(this.statisticsActionLibrary!=null && this.statisticsActionLibrary.size()>0){
			for (ConfigurazioneStatistica configurazioneStatistica : this.statisticsActionLibrary) {
				if(!onlyEnabled || configurazioneStatistica.isEnabled()){
					plugins.put(configurazioneStatistica.getPlugin().getClassName(), configurazioneStatistica);
				}
			}
		}

		// Leggo le risorse associate all'azione '*'
		if(this.statisticsActionAllLibrary!=null && this.statisticsActionAllLibrary.size()>0){
			for (ConfigurazioneStatistica configurazioneStatistica : this.statisticsActionAllLibrary) {

				String idRisorsa = configurazioneStatistica.getPlugin().getClassName();
				if(plugins.containsKey(idRisorsa)==false){

					// inserisco solo le risorse il cui idPlugin non siano già stati inseriti per l'azione specifica

					if(!onlyEnabled || configurazioneStatistica.isEnabled()){
						plugins.put(configurazioneStatistica.getPlugin().getClassName(), configurazioneStatistica);
					}
				}

			}
		}

		List<ConfigurazioneStatistica> list = new ArrayList<ConfigurazioneStatistica>();
		if(orderByLabel){
			List<String> sortedKey = new ArrayList<String>();
			Hashtable<String, ConfigurazioneStatistica> mapLabelToCorrelazioneStatistica = new Hashtable<String, ConfigurazioneStatistica>();
			Enumeration<String> keys = plugins.keys();
			int count = 0; // lo uso per gestire eventuali label identiche.
			while (keys.hasMoreElements()) {
				String className = (String) keys.nextElement();
				ConfigurazioneStatistica cs = plugins.get(className);
				String labelKey = cs.getLabel()+count;
				sortedKey.add(labelKey);
				mapLabelToCorrelazioneStatistica.put(labelKey, cs);
				count++;
			}
			Collections.sort(sortedKey);
			for (String sortKey : sortedKey) {
				list.add(mapLabelToCorrelazioneStatistica.get(sortKey));
			}
		}
		else{
			list.addAll(plugins.values());
		}
		return list;
	}
	

	private ConfigurazioneStatistica buildNewConfigurazioneStatistica(String id,String label,String descrizione,Class<?> classPlugin){
		ConfigurazioneStatistica conf = new ConfigurazioneStatistica();
		conf.setEnabled(true);
		conf.setIdConfigurazioneStatistica(id);
		conf.setLabel(label);
		InfoPlugin infoPlugin = new InfoPlugin();
		infoPlugin.setClassName(classPlugin.getName());
		infoPlugin.setDescrizione(descrizione);
		infoPlugin.setLabel(label);
		infoPlugin.setTipo(TipoPlugin.STATISTICA.getValue());
		conf.setPlugin(infoPlugin);
		return conf;
	}
}
