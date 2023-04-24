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
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.core.internal.statistics.DefaultStatisticsService;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.openspcoop2.utils.UtilsException;

/**
 * EHCacheImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EHCacheImpl extends AbstractCacheImpl {

	private static CacheManager cacheManager;
	private static StatisticsService statisticsService;
	private static synchronized void initCacheManager() {
		if(cacheManager==null) {
			statisticsService = new DefaultStatisticsService();
			cacheManager = CacheManagerBuilder.newCacheManagerBuilder().using(statisticsService).build();
			cacheManager.init();
		}
	}

	private Cache<String, Serializable> cache = null;
	private int maxSize = -1;
	private int maxLifeTime = -1; // secondi
	

	public EHCacheImpl(String name) throws UtilsException{
		this(name, org.openspcoop2.utils.cache.Cache.DEFAULT_DISABLE_SYNCRONIZED_GET);
	}
	@Deprecated
	private EHCacheImpl(String name, boolean disableSyncronizedGet) throws UtilsException{
		super(CacheType.EH, name);
		if(cacheManager==null) {
			initCacheManager();
		}
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
		
		ResourcePoolsBuilder rpb = ResourcePoolsBuilder.newResourcePoolsBuilder();
		if(this.maxSize>0) {
			rpb = rpb.heap(this.maxSize, EntryUnit.ENTRIES);
		}
		
		CacheConfigurationBuilder<String,Serializable> ccb = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Serializable.class, rpb);
		if(this.maxLifeTime>0) {
			ccb = ccb.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(this.maxLifeTime)));
		}
		
		CacheConfiguration<String, Serializable> cacheConfiguration = ccb.build();
    	this.cache = cacheManager.createCache(this.cacheName, cacheConfiguration);
	}
	
	
	
	
	
	//  *** Gestione ***
	
	@Override
	public void clear() throws UtilsException{
		this.cache.clear();
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
		CacheStatistics ehCacheStat = statisticsService.getCacheStatistics(this.cacheName);
		return (int) ehCacheStat.getTierStatistics().get("OnHeap").getMappings();//nb element in heap tier
	}
	
	@Override
	public List<String> keys() throws UtilsException {
		try{
			Set<String> set = new HashSet<>();
			this.cache.forEach(entry -> set.add(entry.getKey()));
			List<String> keys = new ArrayList<>();
			keys.addAll(set);
			return keys;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	

	
	
	//  *** Info ***
	
	@Override
	public void printStats(OutputStream out, String separator) throws UtilsException{
		
		try{
			
			StringBuilder bf = new StringBuilder();
			bf.append(this._printStats(separator, true));
			
			CacheStatistics ehCacheStat = statisticsService.getCacheStatistics(this.cacheName);
			
			bf.append("PutCount:");
			bf.append(ehCacheStat.getCachePuts());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("HitCount(Aux):");
			bf.append(ehCacheStat.getCacheHits());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("Evictions:");
			bf.append(ehCacheStat.getCacheEvictions());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("MissCount(Expired):");
			bf.append(ehCacheStat.getCacheExpirations());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("MissCount(NotFound):");
			bf.append(ehCacheStat.getCacheMisses());
			bf.append(" ");
			
			out.write(bf.toString().getBytes());
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	/*
	 * Non funziona!
	@Override
	protected long getByteCount() {
		CacheStatistics ehCacheStat = statisticsService.getCacheStatistics(this.cacheName);
		return ehCacheStat.getTierStatistics().get("OnHeap").getAllocatedByteSize();
	}
	*/
}
