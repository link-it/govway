package org.openspcoop2.web.monitor.core.dynamic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;

public class Statistiche implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ConfigurazioneStatistica> statistiche;
	private List<SelectItem> statisticheSelectItems;
	
	public List<ConfigurazioneStatistica> getStatistiche() {
		return this.statistiche;
	}
	
	public void setStatistiche(List<ConfigurazioneStatistica> statistiche) {
		this.statistiche = statistiche;
	}
	
	public void addStatistica(ConfigurazioneStatistica stat){
		if(this.statistiche == null)
			this.statistiche = new ArrayList<ConfigurazioneStatistica>();
		
		this.statistiche.add(stat);
	}
	
	public List<SelectItem> getStatisticheSelectItems() {
		
		if(this.statisticheSelectItems!=null)
			return this.statisticheSelectItems;
		
		this.statisticheSelectItems = new ArrayList<SelectItem>();
		
		for (ConfigurazioneStatistica stat : this.statistiche) {
			this.statisticheSelectItems.add(new SelectItem(stat.getLabel()));
		}
		
		return this.statisticheSelectItems;
	}
	
	public ConfigurazioneStatistica getStatisticaByLabel(String label){
		if(StringUtils.isNotEmpty(label))
			for (ConfigurazioneStatistica stat : this.statistiche) {
				if(label.equals(stat.getLabel()))
					return stat;
			}
		
		return null;
	}
}
