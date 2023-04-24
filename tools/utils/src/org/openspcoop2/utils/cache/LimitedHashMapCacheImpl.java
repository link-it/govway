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

package org.openspcoop2.utils.cache;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openspcoop2.utils.UtilsException;

/**
 * LimitedHashMapCacheImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LimitedHashMapCacheImpl extends AbstractCacheImpl {


	private Map<String, Serializable> cache = null;
	private int maxSize = -1;
	private int maxLifeTime = -1; // secondi
	

	public LimitedHashMapCacheImpl(String name) throws UtilsException{
		this(name, Cache.DEFAULT_DISABLE_SYNCRONIZED_GET);
	}
	@Deprecated
	private LimitedHashMapCacheImpl(String name, boolean disableSyncronizedGet) throws UtilsException{
		super(CacheType.LimitedHashMap, name);
	}
	
	
	//  *** Inizializzazione ***
	
	@Override
	public int getCacheSize() {
		return this.maxSize;
	}
	@Override
	public void setCacheSize(int cacheSize) {
		this.maxSize = cacheSize;
	}
	
	@Override
	public CacheAlgorithm getCacheAlgoritm() {
		return CacheAlgorithm.LRU;
	}
	@Override
	public void setCacheAlgoritm(CacheAlgorithm cacheAlgoritm) {
		// unsupported
	}
	
	@Override
	public long getItemIdleTime() throws UtilsException{
		return -1;
	}
	@Override
	public void setItemIdleTime(long itemIdleTimeCache) throws UtilsException{
		// unsupported
	}

	@Override
	public long getItemLifeTime() throws UtilsException{
		return this.maxLifeTime;
	}
	@Override
	public void setItemLifeTime(long itemLifeTimeCache) throws UtilsException{
		this.maxLifeTime = (int) itemLifeTimeCache;
	}
	
	@Override
	public void build() throws UtilsException{
		if ( this.maxSize <= 0 && this.maxLifeTime > 0 )
			throw new UtilsException( "Cannot use maxLifeTime without maxSize" );
		if ( this.maxSize <= 0 ) {
			this.cache = new ConcurrentHashMap<String, Serializable>();
		}
		else {
			this.cache = new LimitedHashMap<String, Serializable>( this.cacheName, this.maxSize, this.maxLifeTime );
		}
	}
	
	
	
	
	
	//  *** Gestione ***
	
	@Override
	public void clear() throws UtilsException{
		this.cache.clear();
		if ( this.maxSize > 0 ) {
			this.build(); // reinizializzo
		}
	}
	
	@Override
	public Object get(String key){
		return this.cache.get(key);
	}
	
	@Override
	public void remove(String key) throws UtilsException{
		try{
			this.cache.remove(key);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	
	@Override
	public void put(String key,org.openspcoop2.utils.cache.CacheResponse value) throws UtilsException{
		try{
			this.cache.put(key, value);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	@Override
	public void put(String key,Serializable value) throws UtilsException{
		try{
			this.cache.put(key, value);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	@Override
	public int getItemCount()  throws UtilsException {
		return this.cache.size();
	}
	
	@Override
	public List<String> keys() throws UtilsException {
		try{
			List<String> keys = new ArrayList<>();
			keys.addAll(this.cache.keySet());
			return keys;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	

	
	
	//  *** Info ***
	
	@Override
	public void printStats(OutputStream out, String separator) throws UtilsException{
		this._printStats(out,separator,true);
	}
	
	
}
