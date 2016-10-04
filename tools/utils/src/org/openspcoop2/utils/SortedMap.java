/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.utils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * SortMap
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SortedMap<T> {

	protected List<String> list = new ArrayList<String>(); // per preservare l'ordine
	protected Hashtable<String, T> map = new Hashtable<String, T>();
	
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
		return this.map.contains(value);
	}
	
}
