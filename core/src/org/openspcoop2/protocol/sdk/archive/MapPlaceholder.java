/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import java.util.Enumeration;
import java.util.Hashtable;

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
	
	private Hashtable<String, String> map = new Hashtable<String, String>();

	public Hashtable<String, String> getMap() {
		return this.map;
	}
	
	public void put(String key,String value){
		if(this.map.containsKey(key)){
			this.map.remove(key);
		}
		this.map.put(key, value);
	}
	
	public void putAll(MapPlaceholder map){
		Enumeration<String> keys = map.map.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			this.put(key,map.map.get(key));
		}
	}
	
	public int size(){
		return this.map.size();
	}
	
	public byte[] replace(byte[]xml){
		String xmlAsString = new String(xml);
		Enumeration<String> enumKeys = this.map.keys();
		while (enumKeys.hasMoreElements()) {
			String key = (String) enumKeys.nextElement();
			
			while(xmlAsString.contains("@"+key+"@")){
				xmlAsString = xmlAsString.replace("@"+key+"@", this.map.get(key));
			}
		}
		return xmlAsString.getBytes();
	}
}
