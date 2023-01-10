/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.protocol.sdk.archive;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * MapPlaceholder
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MapPlaceholder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> map = new HashMap<String, String>();

	public Map<String, String> getMap() {
		return this.map;
	}
	
	public void put(String key,String value){
		if(this.map.containsKey(key)){
			this.map.remove(key);
		}
		this.map.put(key, value);
	}
	
	public void putAll(MapPlaceholder map){
		for (String key : map.map.keySet()) {
			this.put(key,map.map.get(key));
		}
	}
	
	public int size(){
		return this.map.size();
	}
	
	public byte[] replace(byte[]xml){
		String xmlAsString = new String(xml);
		for (String key : this.map.keySet()) {
			
			while(xmlAsString.contains("@"+key+"@")){
				xmlAsString = xmlAsString.replace("@"+key+"@", this.map.get(key));
			}
		}
		return xmlAsString.getBytes();
	}
}
