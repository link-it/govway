/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GruppoConfigurazioni
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class GruppoConfigurazioni implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String label = null;
	List<ConfigurazioneGenerale> listaConfigurazioni = null;
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List<ConfigurazioneGenerale> getListaConfigurazioni() {
		return this.listaConfigurazioni;
	}
	public void setListaConfigurazioni(
			List<ConfigurazioneGenerale> listaConfigurazioni) {
		this.listaConfigurazioni = listaConfigurazioni;
	}

	public GruppoConfigurazioni(){

	}
	public GruppoConfigurazioni(String label, List<ConfigurazioneGenerale> lista){
		this.label = label;
		this.listaConfigurazioni = lista;

		int i = 0;			
		for (ConfigurazioneGenerale configurazioneGenerale : lista) {
			this.add("" + i, configurazioneGenerale);
			i++;

		}

	}

	public void add(String key, ConfigurazioneGenerale value) {
		this.objects.put(key, value);
	}

	private Map<String, ConfigurazioneGenerale> objects = new HashMap<String, ConfigurazioneGenerale>();

	public Map<String, ConfigurazioneGenerale> getObjectsMap() {
		return this.objects;
	}
}