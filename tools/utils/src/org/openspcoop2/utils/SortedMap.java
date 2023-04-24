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
package org.openspcoop2.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SortMap
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SortedMap<T> {

	protected List<String> list = new ArrayList<>(); // per preservare l'ordine
	protected Map<String, T> map = new HashMap<String, T>();
	
	public int size(){
		return this.list.size();
	}
	
	public List<String> keys(){
		return this.list;
	}
	
	public void add(String key, T archive) throws UtilsException{
		if(this.map.containsKey(key)){
			throw new UtilsException("Archivio con chiave ["+key+"] gia' precedentemente caricato");
		}
		this.list.add(key);
		this.map.put(key, archive);
	}
	
	public T get(int index){
		String key = this.list.get(index);
		return this.map.get(key);
	}
	public T get(String key){
		return this.map.get(key);
	}
	
	public T remove(int index){
		String key = this.list.remove(index);
		return this.map.remove(key);
	}
	public T remove(String key){
		for (int i = 0; i < this.list.size(); i++) {
			if(this.list.get(i).equals(key)){
				this.list.remove(i);
				break;
			}
		}
		return this.map.remove(key);
	}
	
	public boolean containsKey(String key){
		return this.map.containsKey(key);
	}
	public boolean containsValue(T value){
		return this.map.containsValue(value);
	}
	
	public boolean isEmpty() {
		return this.map.isEmpty();
	}
	
}
