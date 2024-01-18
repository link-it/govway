/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.cache;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * Cache
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Cache {

	public static boolean DEBUG_CACHE = false; 
	
	private static Map<String, ICacheImpl> caches = new HashMap<String, ICacheImpl>();
	
	public static void setLog4jSystem() {
		JCS3CacheImpl.setLog4jSystem();
	}
	
	public static boolean initialize(Logger logConsole,Logger logCore,
			String cachePropertiesName,
			String rootDirectory,Properties objectProperties,
			String OPENSPCOOP2_LOCAL_HOME,String OPENSPCOOP2_CACHE_PROPERTIES,String OPENSPCOOP2_CACHE_LOCAL_PATH){
		return JCS3CacheImpl.initialize(logConsole, logCore, 
				cachePropertiesName, 
				rootDirectory, objectProperties, 
				OPENSPCOOP2_LOCAL_HOME, OPENSPCOOP2_CACHE_PROPERTIES, OPENSPCOOP2_CACHE_LOCAL_PATH);
	}
	
	public static String printStatistics(String separatorStat, String separatorCache) throws UtilsException {
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			printStatistics(bout,separatorStat,separatorCache);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public static void printStatistics(OutputStream out, String separatorStat, String separatorCache) throws UtilsException {
		try{
			if(Cache.caches!=null && !Cache.caches.isEmpty()) {
				List<String> keys = new ArrayList<>();
				keys.addAll(Cache.caches.keySet());
				Collections.sort(keys);
				for (String key : keys) {
					try {
						ICacheImpl cache = Cache.caches.get(key);
						if(cache!=null) {
							String s = cache.printStats(separatorStat);
							out.write(s.getBytes());
							out.write(separatorCache.getBytes());
						}
					}catch(Throwable t) {}
				}
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	private ICacheImpl cache = null;
	
	public Cache(CacheType cacheType, String name) throws UtilsException{
		switch (cacheType) {
		case JCS:
			this.cache = new JCS3CacheImpl(name);
			break;
		case LimitedHashMap:
			this.cache = new LimitedHashMapCacheImpl(name);
			break;
		case EH:
			this.cache = new EHCacheImpl(name);
			break;
		}
	}
		
	public static boolean DEFAULT_DISABLE_SYNCRONIZED_GET = false;
	public static boolean DISABLE_SYNCRONIZED_GET = true;
	public static void disableSyncronizedGetAsDefault() {
		DEFAULT_DISABLE_SYNCRONIZED_GET = DISABLE_SYNCRONIZED_GET;
	}
	public static boolean isDisableSyncronizedGetAsDefault() {
		return DEFAULT_DISABLE_SYNCRONIZED_GET;
	}
	
	@SuppressWarnings("deprecation")
	@Deprecated
	public void disableSyncronizedGet() throws UtilsException {
		if(this.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		this.cache.disableSyncronizedGet();
	}
	@SuppressWarnings("deprecation")
	@Deprecated
	public boolean isDisableSyncronizedGet() throws UtilsException {
		if(this.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		return this.cache.isDisableSyncronizedGet();
	}
	
	@SuppressWarnings("deprecation")
	@Deprecated
	public void enableDebugSystemOut() throws UtilsException {
		if(this.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		this.cache.enableDebugSystemOut();
	}
	@SuppressWarnings("deprecation")
	@Deprecated
	boolean isEnableDebugSystemOut() throws UtilsException {
		if(this.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		return this.cache.isEnableDebugSystemOut();
	}
	

	public int getCacheSize() {
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		return this.cache.getCacheSize();
	}
	public void setCacheSize(int cacheSize) {
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		this.cache.setCacheSize(cacheSize);
	}

	public CacheAlgorithm getCacheAlgoritm() {
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		return this.cache.getCacheAlgoritm();
	}
	public void setCacheAlgoritm(CacheAlgorithm cacheAlgoritm) {
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		this.cache.setCacheAlgoritm(cacheAlgoritm);
	}

	public long getItemIdleTime() throws UtilsException {
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		return this.cache.getItemIdleTime();
	}
	public void setItemIdleTime(long itemIdleTimeCache) throws UtilsException {
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		this.cache.setItemIdleTime(itemIdleTimeCache);
	}

	public long getItemLifeTime() throws UtilsException {
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		return this.cache.getItemLifeTime();
	}
	public boolean isEternal() throws UtilsException {
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		return this.cache.isEternal();
	}
	public void setItemLifeTime(long itemLifeTimeCache) throws UtilsException {
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		this.cache.setItemLifeTime(itemLifeTimeCache);
	}
	
	public void build() throws UtilsException{
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		this.cache.build();
		Cache.caches.put(this.cache.getName(), this.cache);
	}

	
	
	
	public void clear() throws UtilsException{
		if(this.cache!=null){
			try{
				this.cache.clear();
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}

	public Object get(String key){
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		if(DEBUG_CACHE) {
			System.out.println("GET @"+this.cache.getName()+" ["+key+"]");
		}
		return this.cache.get(key);
	}
	
	public void remove(String key) throws UtilsException{
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		try{
			this.cache.remove(key);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	
	public void put(String key,org.openspcoop2.utils.cache.CacheResponse value) throws UtilsException{
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		try{
			this.cache.put(key, value);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void put(String key,Serializable value) throws UtilsException{
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		try{
			this.cache.put(key, value);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public int getItemCount()  throws UtilsException {
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		return this.cache.getItemCount();
	}
	
	public List<String> keys() throws UtilsException {
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		return this.cache.keys();
	}
	
	public String printStats(String separator) throws UtilsException {
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		return this.cache.printStats(separator);
	}
	public void printStats(OutputStream out, String separator) throws UtilsException {
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		this.cache.printStats(out, separator);
	}
		
	@Override
	public String toString(){
		if(this.cache==null) {
			//throw new RuntimeException("Cache not initialized");
			return "NonDisponibile";
		}
		return this.cache.toString();
	}
	public String printKeys(String separator) throws UtilsException{
		if(this.cache==null) {
			throw new RuntimeException("Cache not initialized");
		}
		return this.cache.printKeys(separator);
	}


}
