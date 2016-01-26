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

package org.openspcoop2.security.keystore.cache;

import java.util.Date;
import java.util.Hashtable;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.date.DateManager;

/**
 * AbstractKeystoreCache
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractKeystoreCache<T> {

	private int cacheLifeSecond = -1;
	private int cacheSize = -1;
	
	private Hashtable<String, KeystoreCacheEntry<T>> cache = new Hashtable<String, KeystoreCacheEntry<T>>();

	public void setKeystoreCacheParameters(int cacheLifeSecond,int cacheSize){
		this.cacheLifeSecond = cacheLifeSecond;
		this.cacheSize = cacheSize;
	}
	
	public boolean existsKeystore(String key){
		return this.cache.containsKey(key);
	}
	public T getKeystore(String key) throws SecurityException{
		KeystoreCacheEntry<T> o = this.cache.get(key);
		if(o==null){
			throw new SecurityException("Keystore with key ["+key+"] not found");
		}
		else {
			return estraiKeystore(o);
		}
	}
	public T getKeystoreAndCreateIfNotExists(String key,Object ... params) throws SecurityException{
		KeystoreCacheEntry<T> o = this.cache.get(key);
		if(o==null){
			//System.out.println("NON trovato ["+key+"], creo...");
			return initKeystore(key, params);
		}
		else {
			//System.out.println("GIA PRESENTE ["+key+"] ESTRAGGO");
			return estraiKeystore(o);
		}
	}
	public abstract T createKeystore(String key,Object ... params) throws SecurityException;
	
	
	/* UTILITY */
	
	private synchronized T initKeystore(String key,Object ... params) throws SecurityException{
		if(this.cache.containsKey(key)==false){
			T keystore = createKeystore(key, params);
			KeystoreCacheEntry<T> cacheEntry = new KeystoreCacheEntry<T>();
			cacheEntry.setKey(key);
			cacheEntry.setKeystore(keystore);
			cacheEntry.setDate(DateManager.getDate());
			this.cache.put(key, cacheEntry);
			//System.out.println("CREATO ["+key+"] !!!! DATA["+cacheEntry.getDate()+"]");
			return keystore;
		}
		else{
			return estraiKeystore(this.cache.get(key));
		}
	}
	private T estraiKeystore(KeystoreCacheEntry<T> entry) throws SecurityException{
		
		T keystore = entry.getKeystore();
		if(this.cacheLifeSecond>-1){
			long scadenza = entry.getDate().getTime()+this.cacheLifeSecond*1000;
			long now = DateManager.getTimeMillis();
			if(scadenza<now){
				//System.out.println("SCADUTO PER DATA ["+entry.getKey()+"] ["+scadenza+"] ["+now+"]");
				removeKeystore(entry.getKey());
			}
		}
		if(this.cacheSize>-1){
			if(this.cache.size()>this.cacheSize){
				//System.out.println("ECCEDUTA DIMENSIONE ["+entry.getKey()+"]");
				clearKeystore();
			}
		}
		return keystore;
	}
	private synchronized void removeKeystore(String key){
		if(this.cache.containsKey(key)){
			this.cache.remove(key);
		}
	}
	private synchronized void clearKeystore(){
		this.cache.clear();
	}
	
	
	
	
}
class KeystoreCacheEntry<T> {
	
	private String key;
	private T keystore;
	private Date date;
	
	public T getKeystore() {
		return this.keystore;
	}
	public void setKeystore(T keystore) {
		this.keystore = keystore;
	}
	public Date getDate() {
		return this.date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
}
