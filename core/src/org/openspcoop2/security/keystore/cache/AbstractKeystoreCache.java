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

package org.openspcoop2.security.keystore.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;

/**
 * AbstractKeystoreCache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractKeystoreCache<T extends Serializable> {

	private int cacheLifeSecond = -1;
	private int cacheSize = -1;
	
	private Hashtable<String, KeystoreCacheEntry<T>> cache_hashtable = new Hashtable<String, KeystoreCacheEntry<T>>();
	private Cache cache_jcs = null;

	public void setKeystoreCacheParameters(int cacheLifeSecond,int cacheSize){
		this.cacheLifeSecond = cacheLifeSecond;
		this.cacheSize = cacheSize;
	}
	public void setCacheJCS(int cacheLifeSecond,Cache cache_jcs) {
		this.cacheLifeSecond = cacheLifeSecond;
		this.cache_jcs = cache_jcs;
	}
	public void updateCacheLifeSecond(int cacheLifeSecond) {
		this.cacheLifeSecond = cacheLifeSecond;
	}
	
	
	
	public T getKeystore(String keyParam) throws SecurityException{
		KeystoreCacheEntry<T> o = this.getObjectFromCache(keyParam);
		if(o==null){
			throw new SecurityException("Keystore with key ["+keyParam+"] not found");
		}
		else {
			return estraiKeystore(o);
		}
	}
	public T getKeystoreAndCreateIfNotExists(String keyParam,Object ... params) throws SecurityException{
		KeystoreCacheEntry<T> o = this.getObjectFromCache(keyParam);
		if(o==null){
			//System.out.println("NON trovato ["+key+"], creo...");
			return initKeystore(keyParam, params);
		}
		else {
			//System.out.println("GIA PRESENTE ["+key+"] ESTRAGGO");
			return estraiKeystore(o);
		}
	}
	
	public T getKeystore(byte[] keystore) throws SecurityException{
		String keyParam = buildKeyCacheFromBytes(keystore);
		return getKeystore(keyParam);
	}
	public T getKeystoreAndCreateIfNotExists(byte[] keystore,Object ... params) throws SecurityException{
		String keyParam = buildKeyCacheFromBytes(keystore);
		List<Object> lArgs = new ArrayList<Object>();
		lArgs.add(keystore);
		if(params!=null && params.length>0) {
			for (Object param : params) {
				lArgs.add(param);
			}
		}
		return getKeystoreAndCreateIfNotExists(keyParam, lArgs.toArray());
	}
	
	public abstract T createKeystore(String key,Object ... params) throws SecurityException;
	public abstract String getPrefixKey();
	
	
	/* UTILITY */
	
	private String buildKeyCacheFromBytes(byte[] keystore) throws SecurityException {
		if(keystore==null) {
			throw new SecurityException("Keystore undefined");
		}
		return DigestUtils.sha1Hex(keystore);
	}
	
	@SuppressWarnings("unchecked")
	private KeystoreCacheEntry<T> getObjectFromCache(String keyParam) {
		String keyCache = this.getPrefixKey() + keyParam;
		KeystoreCacheEntry<T> o = null;
		if(this.cache_jcs!=null) {
			Object object = this.cache_jcs.get(keyCache);
			if(object!=null) {
				o = (KeystoreCacheEntry<T>) object;
			}
		}
		else {
			o = this.cache_hashtable.get(keyCache);
		}
		return o;
	}
	
	private T initKeystore(String keyParam,Object ... params) throws SecurityException{
		
		Object sincronizedObject = null;
		if(this.cache_jcs!=null) {
			sincronizedObject = this.cache_jcs;
		}
		else {
			sincronizedObject = this.cache_hashtable;
		}
		synchronized (sincronizedObject) {
			String keyCache = this.getPrefixKey() + keyParam;
			KeystoreCacheEntry<T> o = this.getObjectFromCache(keyCache);
			if(o==null) {
				T keystore = createKeystore(keyParam, params);
				KeystoreCacheEntry<T> cacheEntry = new KeystoreCacheEntry<T>();
				cacheEntry.setKey(keyCache);
				cacheEntry.setKeystore(keystore);
				cacheEntry.setDate(DateManager.getDate());
				if(this.cache_jcs!=null) {
					try {
						this.cache_jcs.put(keyCache, cacheEntry);
					}catch(Exception e) {
						throw new SecurityException(e.getMessage(),e);
					}
				}
				else {
					this.cache_hashtable.put(keyCache, cacheEntry);
				}
				//System.out.println("CREATO ["+key+"] !!!! DATA["+cacheEntry.getDate()+"]");
				return keystore;
			}
			else {
				return estraiKeystore(o);
			}
		}
		
	}
	
	private void removeKeystore(String key) throws SecurityException{
		
		Object sincronizedObject = null;
		if(this.cache_jcs!=null) {
			sincronizedObject = this.cache_jcs;
		}
		else {
			sincronizedObject = this.cache_hashtable;
		}
		synchronized (sincronizedObject) {
			if(this.cache_jcs!=null) {
				try {
					this.cache_jcs.remove(key);
				}catch(Exception e) {
					throw new SecurityException(e.getMessage(),e);
				}
			}
			else {
				this.cache_hashtable.remove(key);
			}
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
		if(this.cache_jcs==null) {
			if(this.cacheSize>-1){
				if(this.cache_hashtable.size()>this.cacheSize){
					//System.out.println("ECCEDUTA DIMENSIONE ["+entry.getKey()+"]");
					clearCacheHashtable();
				}
			}
		}
		return keystore;
	}
	private void clearCacheHashtable(){
		synchronized (this.cache_hashtable) {
			this.cache_hashtable.clear();
		}
	}
	
	
	
	
}
class KeystoreCacheEntry<T extends Serializable> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("Data ").append(DateUtils.getSimpleDateFormatMs().format(this.date)).append("\n");
		bf.append(this.keystore.toString());
		return bf.toString();
	}
	
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
