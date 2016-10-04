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


package org.openspcoop2.utils.resources;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.collections.iterators.IteratorEnumeration;

/**
 * HashMapReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MapReader<K,V> {

	protected Map<K, V> javaMap;
	protected boolean readCallsNotSynchronized = false;
	protected FastHashMap fastMap;
	
	public MapReader(Map<K, V> map, boolean readCallsNotSynchronized){
		this.readCallsNotSynchronized = readCallsNotSynchronized;
		if(this.readCallsNotSynchronized){
			
			// NOTE: This class is not cross-platform. 
			// Using it may cause unexpected failures on some architectures. 
			// It suffers from the same problems as the double-checked locking idiom. 
			// In particular, the instruction that clones the internal collection and the instruction that sets the internal reference to the clone 
			// can be executed or perceived out-of-order. 
			// This means that any read operation might fail unexpectedly, 
			// as it may be reading the state of the internal collection before the internal collection is fully formed. 
			// For more information on the double-checked locking idiom, see the Double-Checked Locking Idiom Is Broken Declaration.
			// http://commons.apache.org/proper/commons-collections/javadocs/api-3.2.1/index.html
			// http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
			
			// In pratica non effettuare mai delle PUT dopo aver creato l'oggetto.
			// Usare solo in lettura.
			// Questo garantisce che non si presenta il problema sopra descritto
			
			this.fastMap = new FastHashMap(map);
			this.fastMap.setFast(true);
//			Enumeration<?> keys = this.javaProperties.keys();
//			while (keys.hasMoreElements()) {
//				Object key = (Object) keys.nextElement();
//				this.fastProperties.put(key, properties.get(key));
//			}
		}
		else{
			this.javaMap = map;
		}
	}
	
	public V get(K key) {
		return this.getValue(key);
	}
	
	@SuppressWarnings("unchecked")
	public V getValue(K key) {
		Object o = null;
		if(this.readCallsNotSynchronized){
			o = this.fastMap.get(key);
		}else{
			o = this.javaMap.get(key);
		}
		if(o==null){
			return null;
		}
		else{
			return (V) o;
		}
	}
	
	@SuppressWarnings("unchecked")
	public java.util.Enumeration<K> keys(){
		if(this.readCallsNotSynchronized){
			return (java.util.Enumeration<K>) (new IteratorEnumeration(this.fastMap.keySet().iterator()));
		}else{
			return (java.util.Enumeration<K>) (new IteratorEnumeration(this.javaMap.keySet().iterator()));
		}
	}
	
	@SuppressWarnings("unchecked")
	public Collection<V> values(){
		if(this.readCallsNotSynchronized){
			return (Collection<V>) this.fastMap.values();
		}else{
			return this.javaMap.values();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Enumeration<V> elements(){
		return (Enumeration<V>) new IteratorEnumeration(this.values().iterator());
	}
	
	public boolean containsKey(K key){
		if(this.readCallsNotSynchronized){
			return this.fastMap.containsKey(key);
		}else{
			return this.javaMap.containsKey(key);
		}
	}
	
	public int size(){
		if(this.readCallsNotSynchronized){
			return this.fastMap.size();
		}else{
			return this.javaMap.size();
		}
	}
}
