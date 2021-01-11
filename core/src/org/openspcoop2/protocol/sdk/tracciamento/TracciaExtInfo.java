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

package org.openspcoop2.protocol.sdk.tracciamento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.tracciamento.Proprieta;
import org.openspcoop2.utils.MapEntry;

/**
 * Bean Contenente le informazioni relative alle sezioni contenenti informazioni extra
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TracciaExtInfo {

	private boolean empty;
	private String label;
	private List<Proprieta> proprieta = new ArrayList<Proprieta>();
	

	public boolean isEmpty() {
		return this.empty;
	}
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	
	public List<Proprieta> getProprieta() {
		return this.proprieta;
	}
	public Proprieta getProprieta(String name) {
		for (Proprieta proprietaCheck : this.proprieta) {
			if(proprietaCheck.getNome().equals(name)) {
				return proprietaCheck;
			}
		}
		return null;
	}
	public Map<String, String> getProprietaAsMap(){
		Map<String, String> map = new HashMap<String, String>();
		for (Proprieta proprietaCheck : this.proprieta) {
			map.put(proprietaCheck.getNome(), proprietaCheck.getValore());
		}
		return map;
	}
	public List<Map.Entry<String, String>> getProprietaAsMapEntry(){
		List<Map.Entry<String, String>> list = new ArrayList<>();
		for (Proprieta proprietaCheck : this.proprieta) {
			list.add(new MapEntry<String,String>(proprietaCheck.getNome(), proprietaCheck.getValore()));
		}
		return list;
	}
	public void setProprieta(List<Proprieta> proprieta) {
		this.proprieta = proprieta;
	}
	
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
}
