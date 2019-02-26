/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.core.dynamic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;

/**
 * Statistiche
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
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
