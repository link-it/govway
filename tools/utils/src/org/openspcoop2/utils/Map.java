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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Map
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Map<T> extends HashMap<MapKey<String>, T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2577197242840238762L;

	
	private static int uniqueIndex = 0;
	private static org.openspcoop2.utils.Semaphore semaphore_getUniqueSerialNumber = new org.openspcoop2.utils.Semaphore("Map");
	private static int getUniqueIndex(){
		semaphore_getUniqueSerialNumber.acquireThrowRuntime("getUniqueIndex");
		try {
			
			if((Map.uniqueIndex+1) > Integer.MAX_VALUE){
				throw new RuntimeException("Max id reached");
			} 
			Map.uniqueIndex++;
			return Map.uniqueIndex;
			
		}finally {
			semaphore_getUniqueSerialNumber.release("getUniqueIndex");
		}
	}
	
	
	private static HashMap<String, MapKey<String>> _internal_map = new HashMap<String, MapKey<String>>();
		
	private static org.openspcoop2.utils.Semaphore semaphore_newMapKey = new org.openspcoop2.utils.Semaphore("Map");
	private static MapKey<String> _newMapKey(String key){
		semaphore_newMapKey.acquireThrowRuntime("newMapKey");
		try {
			if(_internal_map.containsKey(key)==false) {
				int uniqueIndex = getUniqueIndex();
				MapKey<String> mapKey = new MapKey<String>(key, uniqueIndex);
				_internal_map.put(key, mapKey);
				return mapKey;
			}
			else {
				return _internal_map.get(key);
			}
		}finally {
			semaphore_newMapKey.release("newMapKey");
		}
	}
	
	public static MapKey<String> newMapKey(String key) {
		if(_internal_map.containsKey(key)==false) {
			 return _newMapKey(key);
		}
		else {
			return _internal_map.get(key);
		}
	}
	
	
	

	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}
	
	
	public T addObject(MapKey<String> key,T value){
		if(key!=null && value!=null) {
			if(value instanceof MapKey<?>) {
				throw new RuntimeException("Add object of type 'MapKey<>' in the map not allowed");
			}
			return super.put(key, value);
		}
		return null;
	}
	@Deprecated
	public T addObject(String key,T value){
		if(key==null) {
			return null;
		}
		return addObject(Map.newMapKey(key), value);
	}
	@Override
	public T put(MapKey<String> key, T value) {
		return addObject(key, value);
	}
	@Deprecated
	public T put(String key, T value) {
		return addObject(key, value);
	}

	@Override
	public void putAll(java.util.Map<? extends MapKey<String>, ? extends T> m) {
		if(m!=null && !m.isEmpty()) {
			for (MapKey<String> key : m.keySet()) {
				addObject(key, m.get(key));
			}
		}
	}
	@Deprecated
	public void putAllObjects(java.util.Map<String, ? extends T> m) {
		if(m!=null && !m.isEmpty()) {
			for (String key : m.keySet()) {
				addObject(Map.newMapKey(key), m.get(key));
			}
		}
	}
	
	public void addAll(Map<T> map,boolean overwriteIfExists){
		List<MapKey<String>> keys = map.keys();
		if(keys!=null) {
			for (MapKey<String> key : keys) {
				if(this.containsKey(key)){
					if(overwriteIfExists){
						this.removeObject(key);
						this.addObject(key, map.getObject(key));
					}
				}
				else{
					this.addObject(key, map.getObject(key));
				}
			}
		}
	}
	
	
	public T getObject(MapKey<String> key){
		if(key!=null)
			return super.get(key);
		else
			return null;
	}
	public T get(MapKey<String> key){
		return getObject(key);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Deprecated
	@Override
	public T get(Object key) {
		if(key==null) {
			return null;
		}
		if(key instanceof MapKey<?>) {
			return (T) getObject((MapKey) key);
		}
		else if(key instanceof String) {
			return getObject(Map.newMapKey((String)key));
		}
		else {
			throw new RuntimeException("Object key '"+key.getClass().getName()+"' unsopported");
		}
	}
	@Override
	@Deprecated
	public T getOrDefault(Object key, T defaultValue) {
		T o = this.get(key);
		if(o!=null) {
			return o;
		}
		return defaultValue;
	}
	
	
	public T removeObject(MapKey<String> key){
		if(key!=null)
			return super.remove(key);
		else
			return null;
	}
	public T remove(MapKey<String> key){
		return removeObject(key);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Deprecated
	@Override
	public T remove(Object key) {
		if(key==null) {
			return null;
		}
		if(key instanceof MapKey<?>) {
			return (T) removeObject((MapKey) key);
		}
		else if(key instanceof String) {
			return removeObject(Map.newMapKey((String)key));
		}
		else {
			throw new RuntimeException("Object key '"+key.getClass().getName()+"' unsopported");
		}
	}
	@Deprecated
	@Override
	public boolean remove(Object key, Object value) {
		T o = this.remove(key);
		return value.equals(o);
	}
	
	
	
	public List<MapKey<String>> keys(){
		List<MapKey<String>> keys = null;
		if(!this.isEmpty()) {
			keys = new ArrayList<MapKey<String>>();
			for (MapKey<String> key : super.keySet()) {
				keys.add(key);
			}
		}
		return keys;
	}
	@Deprecated
	public List<String> keysAsString(){
		List<String> keys = null;
		if(!this.isEmpty()) {
			keys = new ArrayList<String>();
			for (MapKey<String> key : super.keySet()) {
				keys.add(key.getValue());
			}
		}
		return keys;
	}
	
	
	public boolean containsKey(MapKey<String> key){
		return super.containsKey(key);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Deprecated
	@Override
	public boolean containsKey(Object key) {
		if(key==null) {
			return false;
		}
		if(key instanceof MapKey<?>) {
			return this.containsKey((MapKey) key);
		}
		else if(key instanceof String) {
			return this.containsKey(Map.newMapKey((String)key));
		}
		else {
			throw new RuntimeException("Object key '"+key.getClass().getName()+"' unsopported");
		}
	}
	

}
