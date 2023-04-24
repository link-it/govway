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

package org.openspcoop2.security.keystore.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	
	private Map<String, KeystoreCacheEntry<T>> cacheMap = new ConcurrentHashMap<>();
	private Cache cacheJCS = null;
	private final org.openspcoop2.utils.Semaphore lockCache = new org.openspcoop2.utils.Semaphore(this.getClass().getSimpleName());

	public void setKeystoreCacheParameters(int cacheLifeSecond,int cacheSize){
		this.cacheLifeSecond = cacheLifeSecond;
		this.cacheSize = cacheSize;
	}
	public void setCacheJCS(int cacheLifeSecond,Cache cacheJCS) {
		this.cacheLifeSecond = cacheLifeSecond;
		this.cacheJCS = cacheJCS;
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
			/**System.out.println("NON trovato ["+key+"], creo...");*/
			return initKeystore(keyParam, params);
		}
		else {
			/**System.out.println("GIA PRESENTE ["+key+"] ESTRAGGO");*/
			return estraiKeystore(o);
		}
	}
	
	public T getKeystore(byte[] keystore) throws SecurityException{
		String keyParam = buildKeyCacheFromBytes(keystore);
		return getKeystore(keyParam);
	}
	public T getKeystoreAndCreateIfNotExists(byte[] keystore,Object ... params) throws SecurityException{
		String keyParam = buildKeyCacheFromBytes(keystore);
		List<Object> lArgs = new ArrayList<>();
		lArgs.add(keystore);
		if(params!=null && params.length>0) {
			lArgs.add(Arrays.asList(params));
		}
		return getKeystoreAndCreateIfNotExists(keyParam, lArgs.toArray());
	}
	
	public abstract T createKeystore(String key,Object ... params) throws SecurityException;
	public abstract String getPrefixKey();
	
	
	/* UTILITY */
	
	public static String buildKeyCacheFromBytes(byte[] keystore) throws SecurityException {
		if(keystore==null) {
			throw new SecurityException("Keystore undefined");
		}
		return DigestUtils.sha256Hex(keystore);
	}
	
	@SuppressWarnings("unchecked")
	private KeystoreCacheEntry<T> getObjectFromCache(String keyParam) {
		String keyCache = this.getPrefixKey() + keyParam;
		KeystoreCacheEntry<T> o = null;
		if(this.cacheJCS!=null) {
			Object object = this.cacheJCS.get(keyCache);
			if(object!=null) {
				o = (KeystoreCacheEntry<T>) object;
			}
		}
		else {
			o = this.cacheMap.get(keyCache);
		}
		return o;
	}
	
	private T initKeystore(String keyParam,Object ... params) throws SecurityException{
		
		this.lockCache.acquireThrowRuntime("initKeystore");
		try {
			String keyCache = this.getPrefixKey() + keyParam;
			KeystoreCacheEntry<T> o = this.getObjectFromCache(keyCache);
			if(o==null) {
				T keystore = createKeystore(keyParam, params);
				KeystoreCacheEntry<T> cacheEntry = new KeystoreCacheEntry<>();
				cacheEntry.setKey(keyCache);
				cacheEntry.setKeystore(keystore);
				cacheEntry.setDate(DateManager.getDate());
				if(this.cacheJCS!=null) {
					try {
						this.cacheJCS.put(keyCache, cacheEntry);
					}catch(Exception e) {
						throw new SecurityException(e.getMessage(),e);
					}
				}
				else {
					this.cacheMap.put(keyCache, cacheEntry);
				}
				/**System.out.println("CREATO ["+key+"] !!!! DATA["+cacheEntry.getDate()+"]"); */
				return keystore;
			}
			else {
				return estraiKeystore(o);
			}
		}finally {
			this.lockCache.release("initKeystore");
		}
		
	}
	
	private void removeKeystore(String key) throws SecurityException{
		
		this.lockCache.acquireThrowRuntime("removeKeystore");
		try {
			if(this.cacheJCS!=null) {
				try {
					this.cacheJCS.remove(key);
				}catch(Exception e) {
					throw new SecurityException(e.getMessage(),e);
				}
			}
			else {
				this.cacheMap.remove(key);
			}
		}finally {
			this.lockCache.release("removeKeystore");
		}
		
	}
	
	private T estraiKeystore(KeystoreCacheEntry<T> entry) throws SecurityException{
		
		T keystore = entry.getKeystore();
		if(this.cacheLifeSecond>-1){
			long scadenza = entry.getDate().getTime()+this.cacheLifeSecond*1000;
			long now = DateManager.getTimeMillis();
			if(scadenza<now){
				/**System.out.println("SCADUTO PER DATA ["+entry.getKey()+"] ["+scadenza+"] ["+now+"]");*/
				removeKeystore(entry.getKey());
			}
		}
		if(this.cacheJCS==null &&
			this.cacheSize>-1 &&
			this.cacheMap.size()>this.cacheSize){
			/**System.out.println("ECCEDUTA DIMENSIONE ["+entry.getKey()+"]");*/
			clearCacheMap();
		}
		return keystore;
	}
	private void clearCacheMap(){
		this.lockCache.acquireThrowRuntime("clearCacheMap");
		try {
			this.cacheMap.clear();
		}finally {
			this.lockCache.release("clearCacheMap");
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
