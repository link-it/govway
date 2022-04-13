/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDConnettore;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.pdd.config.ConfigurazionePdD;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.state.RequestConfig;
import org.openspcoop2.protocol.sdk.state.RequestFruitore;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.RequestRateLimitingConfig;
import org.openspcoop2.protocol.sdk.state.RequestThreadContext;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.cache.CacheResponse;
import org.openspcoop2.utils.cache.CacheType;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**
 * GestoreRichieste
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreRichieste {

	/** Chiave della cache per il Gestore delle Richieste  */
	public static final String GESTORE_RICHIESTE_PREFIX_CACHE_NAME = "gestoreRichieste-";
	private static final String GESTORE_RICHIESTE_API_CACHE_NAME = GESTORE_RICHIESTE_PREFIX_CACHE_NAME+"API";
	private static final String GESTORE_RICHIESTE_RATE_LIMITING_CACHE_NAME = GESTORE_RICHIESTE_PREFIX_CACHE_NAME+"RateLimiting";
	private static final String GESTORE_RICHIESTE_FRUITORI_CACHE_NAME = GESTORE_RICHIESTE_PREFIX_CACHE_NAME+"Fruitori";
	
	private static Cache cache_api = null;
	private static Cache cache_rateLimiting = null;
	private static Cache cache_fruitori = null;
	private static Map<String, Cache> caches = null;
	private static List<String> cacheKeys = null;
	private static synchronized void initCacheKeys() {
		if(cacheKeys==null) {
			cacheKeys = new ArrayList<String>();
			cacheKeys.add(GESTORE_RICHIESTE_API_CACHE_NAME);
			cacheKeys.add(GESTORE_RICHIESTE_RATE_LIMITING_CACHE_NAME);
			cacheKeys.add(GESTORE_RICHIESTE_FRUITORI_CACHE_NAME);
		}
	}
	
	private static final org.openspcoop2.utils.Semaphore lockCache = new org.openspcoop2.utils.Semaphore(GESTORE_RICHIESTE_API_CACHE_NAME);
	private static final org.openspcoop2.utils.Semaphore lockCache_rateLimiting = new org.openspcoop2.utils.Semaphore(GESTORE_RICHIESTE_RATE_LIMITING_CACHE_NAME);
	private static final org.openspcoop2.utils.Semaphore lockCache_fruitori = new org.openspcoop2.utils.Semaphore(GESTORE_RICHIESTE_FRUITORI_CACHE_NAME);
	
	/** Logger log */
	@SuppressWarnings("unused")
	private static Logger logger = null;
	private static Logger logConsole = OpenSPCoop2Logger.getLoggerOpenSPCoopConsole();
	
	
	/* --------------- Cache --------------------*/
	public static void resetCache() throws CoreException{
		if(GestoreRichieste.cacheKeys!=null && !GestoreRichieste.cacheKeys.isEmpty()){
			try{
				for (String cacheKey : GestoreRichieste.cacheKeys) {
					Cache cache = GestoreRichieste.caches.get(cacheKey);
					if(cache!=null) {
						cache.clear();
					}
				}
			}catch(Exception e){
				throw new CoreException(e.getMessage(),e);
			}
		}
	}
	public static String printStatsCache(String separator) throws CoreException{
		try{
			if(GestoreRichieste.cacheKeys!=null && !GestoreRichieste.cacheKeys.isEmpty()){
				StringBuilder sb = new StringBuilder();
				for (String cacheKey : GestoreRichieste.cacheKeys) {
					Cache cache = GestoreRichieste.caches.get(cacheKey);
					if(cache!=null) {
						if(sb.length()>0) {
							sb.append(separator);
						}
						//String funzione = cacheKey.substring(cacheKey.indexOf("-")+1, cacheKey.length());
						//sb.append(funzione).append(separator);
						sb.append(cache.printStats(separator));
					}
				}
				return sb.toString();
			}
			else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new CoreException("Visualizzazione Statistiche riguardante la cache dei dati di gestione delle richieste non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws CoreException{
		if(GestoreRichieste.cacheKeys!=null)
			throw new CoreException("Cache gia' abilitata");
		else{
			try{
				initCacheKeys();
				GestoreRichieste.cache_api = new Cache(CacheType.JCS, GESTORE_RICHIESTE_API_CACHE_NAME); // lascio JCS come default abilitato via jmx
				GestoreRichieste.cache_rateLimiting = new Cache(CacheType.JCS, GESTORE_RICHIESTE_RATE_LIMITING_CACHE_NAME); // lascio JCS come default abilitato via jmx
				GestoreRichieste.cache_fruitori = new Cache(CacheType.JCS, GESTORE_RICHIESTE_FRUITORI_CACHE_NAME); // lascio JCS come default abilitato via jmx
				
				GestoreRichieste.cache_api.build();
				GestoreRichieste.cache_rateLimiting.build();
				GestoreRichieste.cache_fruitori.build();
				
				GestoreRichieste.caches = new HashMap<String, Cache>();
				GestoreRichieste.caches.put(GESTORE_RICHIESTE_API_CACHE_NAME, GestoreRichieste.cache_api);
				GestoreRichieste.caches.put(GESTORE_RICHIESTE_RATE_LIMITING_CACHE_NAME, GestoreRichieste.cache_rateLimiting);
				GestoreRichieste.caches.put(GESTORE_RICHIESTE_FRUITORI_CACHE_NAME, GestoreRichieste.cache_fruitori);
			}catch(Exception e){
				throw new CoreException(e.getMessage(),e);
			}
		}
	}
	public static void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond) throws CoreException{
		if(GestoreRichieste.cacheKeys!=null)
			throw new CoreException("Cache già abilitata");
		else{
			try{
				int dimensioneCacheInt = -1;
				if(dimensioneCache!=null){
					dimensioneCacheInt = dimensioneCache.intValue();
				}
				
				String algoritmoCache = null;
				if(algoritmoCacheLRU!=null){
					if(algoritmoCacheLRU)
						 algoritmoCache = CostantiConfigurazione.CACHE_LRU.toString();
					else
						 algoritmoCache = CostantiConfigurazione.CACHE_MRU.toString();
				}else{
					algoritmoCache = CostantiConfigurazione.CACHE_LRU.toString();
				}
				
				long itemIdleTimeLong = -1;
				if(itemIdleTime!=null){
					itemIdleTimeLong = itemIdleTime;
				}
				
				long itemLifeSecondLong = -1;
				if(itemLifeSecond!=null){
					itemLifeSecondLong = itemLifeSecond;
				}
				
				GestoreRichieste.initCacheGestoreRichieste(CacheType.JCS, dimensioneCacheInt, algoritmoCache, itemIdleTimeLong, itemLifeSecondLong, null); // lascio JCS come default abilitato via jmx
			}catch(Exception e){
				throw new CoreException(e.getMessage(),e);
			}
		}
	}
	public static void disabilitaCache() throws CoreException{
		if(GestoreRichieste.cacheKeys==null)
			throw new CoreException("Cache già disabilitata");
		else{
			try{
				GestoreRichieste.cache_api.clear();
				GestoreRichieste.cache_api = null;
				
				GestoreRichieste.cache_rateLimiting.clear();
				GestoreRichieste.cache_rateLimiting = null;
				
				GestoreRichieste.cache_fruitori.clear();
				GestoreRichieste.cache_fruitori = null;
				
				GestoreRichieste.caches.clear();
				GestoreRichieste.caches = null;
				
				GestoreRichieste.cacheKeys.clear();
				GestoreRichieste.cacheKeys = null;
			}catch(Exception e){
				throw new CoreException(e.getMessage(),e);
			}
		}
	}
	public static boolean isCacheAbilitata(){
		return GestoreRichieste.cacheKeys != null;
	}
	public static String listKeysCache(String separator) throws CoreException{
		if(GestoreRichieste.cacheKeys!=null && !GestoreRichieste.cacheKeys.isEmpty()){
			StringBuilder sb = new StringBuilder();
			for (String cacheKey : GestoreRichieste.cacheKeys) {
				Cache cache = GestoreRichieste.caches.get(cacheKey);
				if(cache!=null) {
					if(sb.length()>0) {
						sb.append(separator);
					}
					try{
						String nomeCache = cacheKey;
						if(nomeCache.startsWith(GestoreRichieste.GESTORE_RICHIESTE_PREFIX_CACHE_NAME)) {
							nomeCache = nomeCache.substring(GestoreRichieste.GESTORE_RICHIESTE_PREFIX_CACHE_NAME.length());
						}
						sb.append( cache.printKeys(separator).replaceFirst("Cache","Cache-"+nomeCache ) );
					}catch(Exception e){
						throw new CoreException(e.getMessage(),e);
					}
				}
			}
			return sb.toString();
		}else{
			throw new CoreException("Cache non abilitata");
		}
	}
	@Deprecated
	// non viene gestito correttamente la chiave rispetto alla cache, potrebbe essere presente la solita chiave
	public static List<String> listKeysCache() throws CoreException{
		if(GestoreRichieste.cacheKeys!=null && !GestoreRichieste.cacheKeys.isEmpty()){
			List<String> keys = new ArrayList<String>();
			for (String cacheKey : GestoreRichieste.cacheKeys) {
				Cache cache = GestoreRichieste.caches.get(cacheKey);
				if(cache!=null) {
					try{
						List<String> k = cache.keys();
						if(k!=null && !k.isEmpty()) {
							keys.addAll(k);
						}
					}catch(Exception e){
						throw new CoreException(e.getMessage(),e);
					}
				}
			}
			return keys;
		}else{
			throw new CoreException("Cache non abilitata");
		}
	}
	public static String getObjectCache(String key) throws CoreException{
		if(GestoreRichieste.cacheKeys!=null && !GestoreRichieste.cacheKeys.isEmpty()){
			try{
				Object o = null;
				for (String cacheKey : GestoreRichieste.cacheKeys) {
					
					// trucco per avere il valore di una precisa cache				
					if(key.startsWith("Cache-")) {
						
						String nomeCache = cacheKey;
						if(nomeCache.startsWith(GestoreRichieste.GESTORE_RICHIESTE_PREFIX_CACHE_NAME)) {
							nomeCache = nomeCache.substring(GestoreRichieste.GESTORE_RICHIESTE_PREFIX_CACHE_NAME.length());
						}
						nomeCache = "Cache-"+nomeCache+" ";
						
						if(!key.startsWith(nomeCache)) {
							continue; // cerco in altra cache
						}
						else {
							key = key.substring(nomeCache.length());
						}
					}
					
					Cache cache = GestoreRichieste.caches.get(cacheKey);
					if(cache!=null) {
						o = cache.get(key);
						if(o!=null) {
							break;
						}
					}
				}
				if(o!=null){
					return o.toString();
				}else{
					return "oggetto con chiave ["+key+"] non presente";
				}
			}catch(Exception e){
				throw new CoreException(e.getMessage(),e);
			}
		}else{
			throw new CoreException("Cache non abilitata");
		}
	}
	public static Object getRawObjectCache(String key) throws CoreException{
		if(GestoreRichieste.cacheKeys!=null && !GestoreRichieste.cacheKeys.isEmpty()){
			try{
				Object o = null;
				for (String cacheKey : GestoreRichieste.cacheKeys) {
					
					// trucco per avere il valore di una precisa cache		
					if(key.startsWith("Cache-")) {
						
						String nomeCache = cacheKey;
						if(nomeCache.startsWith(GestoreRichieste.GESTORE_RICHIESTE_PREFIX_CACHE_NAME)) {
							nomeCache = nomeCache.substring(GestoreRichieste.GESTORE_RICHIESTE_PREFIX_CACHE_NAME.length());
						}
						nomeCache = "Cache-"+nomeCache+" ";
						
						if(!key.startsWith(nomeCache)) {
							continue; // cerco in altra cache
						}
						else {
							key = key.substring(nomeCache.length());
						}
					}
					
					Cache cache = GestoreRichieste.caches.get(cacheKey);
					if(cache!=null) {
						o = cache.get(key);
						if(o!=null) {
							break;
						}
					}
				}
				return _getRawObjectCache(o);
			}catch(Exception e){
				throw new CoreException(e.getMessage(),e);
			}
		}else{
			throw new CoreException("Cache non abilitata");
		}
	}
	public static Object _getRawObjectCache(Object o) throws CoreException{
		if(o!=null){
			if(o instanceof CacheResponse) {
				CacheResponse cR = (CacheResponse) o;
				if(cR.getObject()!=null) {
					o = cR.getObject();
				}
				else if(cR.getException()!=null) {
					o = cR.getException();
				}
			}
			return o;
		}else{
			return null;
		}
	}
	public static void removeObjectCache(String key) throws CoreException{
		if(GestoreRichieste.cacheKeys!=null && !GestoreRichieste.cacheKeys.isEmpty()){
			try{
				for (String cacheKey : GestoreRichieste.cacheKeys) {
					
					// trucco per avere il valore di una precisa cache		
					if(key.startsWith("Cache-")) {
						
						String nomeCache = cacheKey;
						if(nomeCache.startsWith(GestoreRichieste.GESTORE_RICHIESTE_PREFIX_CACHE_NAME)) {
							nomeCache = nomeCache.substring(GestoreRichieste.GESTORE_RICHIESTE_PREFIX_CACHE_NAME.length());
						}
						nomeCache = "Cache-"+nomeCache+" ";
						
						if(!key.startsWith(nomeCache)) {
							continue; // cerco in altra cache
						}
						else {
							key = key.substring(nomeCache.length());
						}
					}
					
					Cache cache = GestoreRichieste.caches.get(cacheKey);
					if(cache!=null) {
						cache.remove(key);
					}
				}
			}catch(Exception e){
				throw new CoreException(e.getMessage(),e);
			}
		}else{
			throw new CoreException("Cache non abilitata");
		}
	}
	


	/*----------------- INIZIALIZZAZIONE --------------------*/
	public static void initialize(Logger log) throws Exception{
		GestoreRichieste.initialize(null, false, -1,null,-1l,-1l, log);
	}
	public static void initialize(CacheType cacheType, int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{
		GestoreRichieste.initialize(cacheType, true, dimensioneCache,algoritmoCache,idleTime,itemLifeSecond, log);
	}

	private static void initialize(CacheType cacheType, boolean cacheAbilitata,int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{

		// Inizializzo log
		GestoreRichieste.logger = log;
		
		// Inizializzazione Cache
		if(cacheAbilitata){
			GestoreRichieste.initCacheGestoreRichieste(cacheType, dimensioneCache, algoritmoCache, idleTime, itemLifeSecond, log);
		}

	}


	public static void initCacheGestoreRichieste(CacheType cacheType, int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception {
		
		if(log!=null)
			log.info("Inizializzazione cache GestoreRichieste");

		initCacheKeys();
		
		GestoreRichieste.cache_api = new Cache(cacheType, GESTORE_RICHIESTE_API_CACHE_NAME); 
		GestoreRichieste.cache_rateLimiting = new Cache(cacheType, GESTORE_RICHIESTE_RATE_LIMITING_CACHE_NAME);
		GestoreRichieste.cache_fruitori = new Cache(cacheType, GESTORE_RICHIESTE_FRUITORI_CACHE_NAME); 
		
		GestoreRichieste.caches = new HashMap<String, Cache>();
		GestoreRichieste.caches.put(GESTORE_RICHIESTE_API_CACHE_NAME, GestoreRichieste.cache_api);
		GestoreRichieste.caches.put(GESTORE_RICHIESTE_RATE_LIMITING_CACHE_NAME, GestoreRichieste.cache_rateLimiting);
		GestoreRichieste.caches.put(GESTORE_RICHIESTE_FRUITORI_CACHE_NAME, GestoreRichieste.cache_fruitori);
		
		if( (dimensioneCache>0) ||
				(algoritmoCache != null) ){

			if( dimensioneCache>0 ){
				try{
					// divido per il numero di cache interne
					int dimensioneCacheInterna = dimensioneCache / GestoreRichieste.cacheKeys.size();
					
					String msg = "Dimensione della cache (GestoreRichieste) impostata al valore: "+dimensioneCache+" ("+GestoreRichieste.cacheKeys.size()+" cache interne di "+dimensioneCacheInterna+" elementi ciascuna)";
					if(log!=null)
						log.info(msg);
					GestoreRichieste.logConsole.info(msg);
					for (String cacheKey : GestoreRichieste.cacheKeys) {
						Cache cache = GestoreRichieste.caches.get(cacheKey);
						cache.setCacheSize(dimensioneCacheInterna);
					}
				}catch(Exception error){
					throw new CoreException("Parametro errato per la dimensione della cache (Gestore Messaggi): "+error.getMessage(),error);
				}
			}
			if(algoritmoCache != null ){
				String msg = "Algoritmo di cache (GestoreRichieste) impostato al valore: "+algoritmoCache;
				if(log!=null)
					log.info(msg);
				GestoreRichieste.logConsole.info(msg);
				CacheAlgorithm ca = null;
				if(CostantiConfigurazione.CACHE_MRU.toString().equalsIgnoreCase(algoritmoCache)) {
					ca = CacheAlgorithm.MRU;
				}else {
					ca = CacheAlgorithm.LRU;
				}
				for (String cacheKey : GestoreRichieste.cacheKeys) {
					Cache cache = GestoreRichieste.caches.get(cacheKey);
					cache.setCacheAlgoritm(ca);
				}
			}

		}

		if( idleTime > 0  ){
			try{
				String msg = "Attributo 'IdleTime' (GestoreRichieste) impostato al valore: "+idleTime;
				if(log!=null)
					log.info(msg);
				GestoreRichieste.logConsole.info(msg);
				for (String cacheKey : GestoreRichieste.cacheKeys) {
					Cache cache = GestoreRichieste.caches.get(cacheKey);
					cache.setItemIdleTime(idleTime);
				}
			}catch(Exception error){
				throw new CoreException("Parametro errato per l'attributo 'IdleTime' (Gestore Messaggi): "+error.getMessage(),error);
			}
		}

		try{
			String msg = "Attributo 'MaxLifeSecond' (GestoreRichieste) impostato al valore: "+itemLifeSecond;
			if(log!=null)
				log.info(msg);
			GestoreRichieste.logConsole.info(msg);
			for (String cacheKey : GestoreRichieste.cacheKeys) {
				Cache cache = GestoreRichieste.caches.get(cacheKey);
				cache.setItemLifeTime(itemLifeSecond);
			}
		}catch(Exception error){
			throw new CoreException("Parametro errato per l'attributo 'MaxLifeSecond' (Gestore Messaggi): "+error.getMessage(),error);
		}

		GestoreRichieste.cache_api.build();
		GestoreRichieste.cache_rateLimiting.build();
		GestoreRichieste.cache_fruitori.build();
	}
	
	@SuppressWarnings("deprecation")
	@Deprecated
	public static void disableSyncronizedGet() throws UtilsException {
		if(GestoreRichieste.cacheKeys!=null && !GestoreRichieste.cacheKeys.isEmpty()){
			try{
				for (String cacheKey : GestoreRichieste.cacheKeys) {
					Cache cache = GestoreRichieste.caches.get(cacheKey);
					if(cache!=null) {
						cache.disableSyncronizedGet();
					}
				}
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}else{
			throw new UtilsException("Cache disabled");
		}
	}
	@SuppressWarnings("deprecation")
	@Deprecated
	public static boolean isDisableSyncronizedGet() throws UtilsException {
		if(GestoreRichieste.cacheKeys!=null && !GestoreRichieste.cacheKeys.isEmpty()){
			try{
				boolean isDisabled = true;
				for (String cacheKey : GestoreRichieste.cacheKeys) {
					Cache cache = GestoreRichieste.caches.get(cacheKey);
					if(cache!=null) {
						if(!cache.isDisableSyncronizedGet()) {
							isDisabled = false;
							break;
						}
					}
				}
				return isDisabled;
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}else{
			throw new UtilsException("Cache disabled");
		}
	}
	

	
	
	
	/*----------------- CLEANER - RATELIMITING --------------------*/
	
	public static void removeRateLimitingConfigGlobale() throws Exception {
		if(GestoreRichieste.cache_rateLimiting!=null) {
			GestoreRichieste.cache_rateLimiting.clear();
		}
	}
	
	public static void removeRateLimitingConfig(TipoPdD tipoPdD, String nomePorta) throws Exception {
		
		if(GestoreRichieste.cache_rateLimiting!=null) {
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_rateLimiting.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_rateLimiting.get(key));
						if(o!=null) {
							if(o instanceof RequestRateLimitingConfig) {
								RequestRateLimitingConfig rl = (RequestRateLimitingConfig) o;
								if(tipoPdD.equals(rl.getTipoPdD()) && nomePorta.equals(rl.getNomePorta())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_rateLimiting.remove(key);
				}
			}
		}
		
	}

	
	
	/*----------------- CLEANER --------------------*/
	
	public static void removeApi(IDAccordo idAccordo) throws Exception {
		
		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		// RequestConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_api.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_api.get(key));
						if(o!=null) {
							if(o instanceof RequestConfig) {
								RequestConfig rc = (RequestConfig) o;
								if(rc.getAspc()!=null && idAccordo.equals(idAccordoFactory.getIDAccordoFromAccordo(rc.getAspc()))) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_api.remove(key);
				}
			}
		}
		
	}
	public static void removeErogazione(IDServizio idServizio) throws Exception {
		
		// RequestConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_api.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_api.get(key));
						if(o!=null) {
							if(o instanceof RequestConfig) {
								RequestConfig rc = (RequestConfig) o;
								if(rc.getIdServizio()!=null && idServizio.equals(rc.getIdServizio())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_api.remove(key);
				}
			}
		}
		
		// RequestRateLimitingConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_rateLimiting.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_rateLimiting.get(key));
						if(o!=null) {
							if(o instanceof RequestRateLimitingConfig) {
								RequestRateLimitingConfig rl = (RequestRateLimitingConfig) o;
								if(rl.getIdServizio()!=null && idServizio.equals(rl.getIdServizio())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_rateLimiting.remove(key);
				}
			}
		}
		
	}
	public static void removeFruizione(IDSoggetto fruitore, IDServizio idServizio) throws Exception {
		
		// RequestConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_api.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_api.get(key));
						if(o!=null) {
							if(o instanceof RequestConfig) {
								RequestConfig rc = (RequestConfig) o;
								if(rc.getIdServizio()!=null && idServizio.equals(rc.getIdServizio()) &&
										rc.getIdFruitore()!=null && fruitore.equals(rc.getIdFruitore())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_api.remove(key);
				}
			}
		}
		
		// RequestRateLimitingConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_rateLimiting.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_rateLimiting.get(key));
						if(o!=null) {
							if(o instanceof RequestRateLimitingConfig) {
								RequestRateLimitingConfig rl = (RequestRateLimitingConfig) o;
								if(rl.getIdServizio()!=null && idServizio.equals(rl.getIdServizio()) &&
										rl.getIdFruitore()!=null && fruitore.equals(rl.getIdFruitore())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_rateLimiting.remove(key);
				}
			}
		}
		
		// RequestFruitore
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_fruitori.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_fruitori.get(key));
						if(o!=null) {
							if(o instanceof RequestFruitore) {
								RequestFruitore rf = (RequestFruitore) o;
								if(rf.getIdSoggettoFruitore()!=null && fruitore.equals(rf.getIdSoggettoFruitore())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_fruitori.remove(key);
				}
			}
		}
	}
	
	public static void removePortaApplicativa(IDPortaApplicativa idPA) throws Exception {
		
		// RequestConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_api.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_api.get(key));
						if(o!=null) {
							if(o instanceof RequestConfig) {
								RequestConfig rc = (RequestConfig) o;
								if(rc.getIdPortaApplicativaDefault()!=null && idPA.equals(rc.getIdPortaApplicativaDefault())) {
									keyForClean.add(key);
								}
								else if(rc.getIdPortaApplicativa()!=null && idPA.equals(rc.getIdPortaApplicativa())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_api.remove(key);
				}
			}
		}
		
		// RequestRateLimitingConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_rateLimiting.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_rateLimiting.get(key));
						if(o!=null) {
							if(o instanceof RequestRateLimitingConfig) {
								RequestRateLimitingConfig rl = (RequestRateLimitingConfig) o;
								if(TipoPdD.APPLICATIVA.equals(rl.getTipoPdD()) && idPA.getNome().equals(rl.getNomePorta())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_rateLimiting.remove(key);
				}
			}
		}
		
	}
	
	public static void removeConnettore(IDConnettore idConnettore)throws Exception {
		
		// RequestConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_api.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_api.get(key));
						if(o!=null) {
							if(o instanceof RequestConfig) {
								RequestConfig rc = (RequestConfig) o;
								if(rc.getIdServizio()!=null && rc.getIdServizio().getSoggettoErogatore()!=null && idConnettore.getIdSoggettoProprietario().equals(rc.getIdServizio().getSoggettoErogatore())) {
									ServizioApplicativo sa = rc.getServizioApplicativoErogatore(idConnettore.getNome());
									if(sa!=null) {
										keyForClean.add(key);
									}
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_api.remove(key);
				}
			}
		}
		
	}
	
	public static void removePortaDelegata(IDPortaDelegata idPD) throws Exception {
		
		// RequestConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_api.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_api.get(key));
						if(o!=null) {
							if(o instanceof RequestConfig) {
								RequestConfig rc = (RequestConfig) o;
								if(rc.getIdPortaDelegataDefault()!=null && idPD.equals(rc.getIdPortaDelegataDefault())) {
									keyForClean.add(key);
								}
								else if(rc.getIdPortaDelegata()!=null && idPD.equals(rc.getIdPortaDelegata())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_api.remove(key);
				}
			}
		}
		
		// RequestRateLimitingConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_rateLimiting.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_rateLimiting.get(key));
						if(o!=null) {
							if(o instanceof RequestRateLimitingConfig) {
								RequestRateLimitingConfig rl = (RequestRateLimitingConfig) o;
								if(TipoPdD.DELEGATA.equals(rl.getTipoPdD()) && idPD.getNome().equals(rl.getNomePorta())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_rateLimiting.remove(key);
				}
			}
		}
		
	}
	
	public static void removePdd(String portaDominio) throws Exception {
		
		// RequestConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_api.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_api.get(key));
						if(o!=null) {
							if(o instanceof RequestConfig) {
								RequestConfig rc = (RequestConfig) o;
								if(rc.getSoggettoErogatorePdd()!=null && rc.getSoggettoErogatorePdd().getNome()!=null && portaDominio.equals(rc.getSoggettoErogatorePdd().getNome())) {
									keyForClean.add(key);
								}
								else if(rc.getSoggettoFruitorePdd()!=null && rc.getSoggettoFruitorePdd().getNome()!=null && portaDominio.equals(rc.getSoggettoFruitorePdd().getNome())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_api.remove(key);
				}
			}
		}
		
	}
	
	public static void removeSoggetto(IDSoggetto idSoggetto) throws Exception {
		
		// RequestConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_api.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_api.get(key));
						if(o!=null) {
							if(o instanceof RequestConfig) {
								RequestConfig rc = (RequestConfig) o;
								if(rc.getIdServizio()!=null && rc.getIdServizio().getSoggettoErogatore()!=null && idSoggetto.equals(rc.getIdServizio().getSoggettoErogatore())) {
									keyForClean.add(key);
								}
								else if(rc.getIdFruitore()!=null && idSoggetto.equals(rc.getIdFruitore())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_api.remove(key);
				}
			}
		}
		
		// RequestRateLimitingConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_rateLimiting.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_rateLimiting.get(key));
						if(o!=null) {
							if(o instanceof RequestRateLimitingConfig) {
								RequestRateLimitingConfig rl = (RequestRateLimitingConfig) o;
								if(rl.getIdServizio()!=null && rl.getIdServizio().getSoggettoErogatore()!=null && idSoggetto.equals(rl.getIdServizio().getSoggettoErogatore())) {
									keyForClean.add(key);
								}
								else if(rl.getIdFruitore()!=null && idSoggetto.equals(rl.getIdFruitore())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_rateLimiting.remove(key);
				}
			}
		}
		
		// RequestFruitore
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_fruitori.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_fruitori.get(key));
						if(o!=null) {
							if(o instanceof RequestFruitore) {
								RequestFruitore rf = (RequestFruitore) o;
								if(rf.getIdServizioApplicativoFruitore()!=null && rf.getIdServizioApplicativoFruitore().getIdSoggettoProprietario()!=null && idSoggetto.equals(rf.getIdServizioApplicativoFruitore().getIdSoggettoProprietario())) {
									keyForClean.add(key);
								}
								else if(rf.getIdSoggettoFruitore()!=null && idSoggetto.equals(rf.getIdSoggettoFruitore())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_fruitori.remove(key);
				}
			}
		}
		
	}
	
	public static void removeApplicativo(IDServizioApplicativo idApplicativo) throws Exception {
		
		// RequestConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_api.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_api.get(key));
						if(o!=null) {
							if(o instanceof RequestConfig) {
								RequestConfig rc = (RequestConfig) o;
								if(rc.getIdServizio()!=null && rc.getIdServizio().getSoggettoErogatore()!=null && idApplicativo.getIdSoggettoProprietario().equals(rc.getIdServizio().getSoggettoErogatore())) {
									ServizioApplicativo sa = rc.getServizioApplicativoErogatore(idApplicativo.getNome());
									if(sa!=null) {
										keyForClean.add(key);
									}
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_api.remove(key);
				}
			}
		}
		
		// RequestFruitore
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_fruitori.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_fruitori.get(key));
						if(o!=null) {
							if(o instanceof RequestFruitore) {
								RequestFruitore rf = (RequestFruitore) o;
								if(rf.getIdServizioApplicativoFruitore()!=null && idApplicativo.equals(rf.getIdServizioApplicativoFruitore())) {
									keyForClean.add(key);
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_fruitori.remove(key);
				}
			}
		}		
		
	}
	
	
	public static void removeRuolo(IDRuolo idRuolo) throws Exception {
		// RequestConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_api.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_api.get(key));
						if(o!=null) {
							if(o instanceof RequestConfig) {
								RequestConfig rc = (RequestConfig) o;
								
								List<String> ruoloKeys = rc.getRuoloKeys();
								if(ruoloKeys!=null && ruoloKeys.contains(idRuolo.getNome())) {
									keyForClean.add(key);
									continue;
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_api.remove(key);
				}
			}
		}
	}
	
	public static void removeScope(IDScope idScope) throws Exception {
		// RequestConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_api.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_api.get(key));
						if(o!=null) {
							if(o instanceof RequestConfig) {
								RequestConfig rc = (RequestConfig) o;
								
								List<String> scopeKeys = rc.getScopeKeys();
								if(scopeKeys!=null && scopeKeys.contains(idScope.getNome())) {
									keyForClean.add(key);
									continue;
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_api.remove(key);
				}
			}
		}
	}
	
	
	public static void removeGenericProperties(IDGenericProperties idGP) throws Exception {
		// RequestConfig
		{
			List<String> keyForClean = new ArrayList<String>();
			List<String> keys = GestoreRichieste.cache_api.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  _getRawObjectCache(GestoreRichieste.cache_api.get(key));
						if(o!=null) {
							if(o instanceof RequestConfig) {
								RequestConfig rc = (RequestConfig) o;
								
								if(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA.equals(idGP.getTipologia())) {
									Object oT = rc.getPolicyValidazioneToken(idGP.getNome());
									if(oT!=null) {
										keyForClean.add(key);
										continue;
									}
								}
								
								if(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA_RETRIEVE.equals(idGP.getTipologia())) {
									Object oT = rc.getPolicyNegoziazioneToken(idGP.getNome());
									if(oT!=null) {
										keyForClean.add(key);
										continue;
									}
								}
								
								if(org.openspcoop2.pdd.core.token.Costanti.ATTRIBUTE_AUTHORITY.equals(idGP.getTipologia())) {
									Object oT = rc.getAttributeAuthority(idGP.getNome());
									if(oT!=null) {
										keyForClean.add(key);
										continue;
									}
								}
								
								List<String> forwardProxyKeys = (rc.getForwardProxyEnabled()!=null && rc.getForwardProxyEnabled()) ? rc.getForwardProxyKeys() : null;
								String forwardProxyGP = ConfigurazionePdD._toKey_ForwardProxyConfigSuffix(idGP);
								if(forwardProxyKeys!=null && forwardProxyKeys.contains(forwardProxyGP)) {
									keyForClean.add(key);
									continue;
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cache_api.remove(key);
				}
			}
		}
	}
	
	
	

	
	// ******** RICHIESTE **********
	
	public static void readRequestConfig(RequestInfo requestInfo) {
		String key = buildKey(requestInfo);
		if(key==null) {
			return;
		}
		_readRequestConfig(requestInfo, key);
		_readRequestRateLimitingConfig(requestInfo, key);
		requestInfo.setRequestThreadContext(RequestThreadContext.getRequestThreadContext());
	}
	
	public static void _readRequestConfig(RequestInfo requestInfo, String key) {
		
		if(cache_api==null) {
			return;
		}
		
		org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) cache_api.get(key);
		if(response != null){
			if(response.getObject()!=null){
				RequestConfig rc = (RequestConfig) response.getObject(); 
				//System.out.println("TROVATA IN CACHE ("+rc.isCached()+")");
				if(rc.getIdServizio()!=null) {
					rc.setIdServizio(rc.getIdServizio().clone()); // per l'azione
				}
				requestInfo.setRequestConfig(rc);
				return;
			}
		}
		
		//System.out.println("CONFIG NON TROVATA IN CACHE");
		RequestConfig rc = new RequestConfig();
		rc.setKey(key);
		rc.setCached(false);
		requestInfo.setRequestConfig(rc);
		return;
		
	}
	
	public static void _readRequestRateLimitingConfig(RequestInfo requestInfo, String key) {
		
		if(cache_rateLimiting==null) {
			return;
		}
		
		org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) cache_rateLimiting.get(key);
		if(response != null){
			if(response.getObject()!=null){
				RequestRateLimitingConfig rc = (RequestRateLimitingConfig) response.getObject(); 
				//System.out.println("TROVATA IN CACHE ("+rc.isCached()+")");
				requestInfo.setRequestRateLimitingConfig(rc);
				return;
			}
		}
		
		//System.out.println("RATE LIMIT NON TROVATA IN CACHE");
		RequestRateLimitingConfig rc = new RequestRateLimitingConfig();
		rc.setKey(key);
		rc.setCached(false);
		requestInfo.setRequestRateLimitingConfig(rc);
		return;
		
	}
	
	public static void saveRequestConfig(RequestInfo requestInfo)throws Exception {
		
		if(requestInfo==null) {
			return;
		}		
		
		String idTransazione = requestInfo.getIdTransazione();
		
		
		if(requestInfo.getRequestConfig()!=null && !requestInfo.getRequestConfig().isCached()) {
			lockCache.acquire("saveRequestConfig", idTransazione);
			try {
				String key = requestInfo.getRequestConfig().getKey();
				org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) cache_api.get(key);
				if(response == null){
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					requestInfo.getRequestConfig().setCached(true);
					responseCache.setObject(requestInfo.getRequestConfig());
					cache_api.put(key,responseCache);
					//System.out.println("SALVATA IN CACHE con chiave ["+requestInfo.getRequestConfig().getKey()+"]");
				}
				else {
					//System.out.println("NON SALVATA IN CACHE, GIA PRESENTE");
				}
				
			}finally {
				lockCache.release("saveRequestConfig", idTransazione);
			}
		}
		
		if(requestInfo.getRequestRateLimitingConfig()!=null && !requestInfo.getRequestRateLimitingConfig().isCached()) {
			lockCache_rateLimiting.acquire("saveRequestRateLimitingConfig", idTransazione);
			try {
				String key = requestInfo.getRequestConfig().getKey();
				org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) cache_rateLimiting.get(key);
				if(response == null){
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					requestInfo.getRequestRateLimitingConfig().setCached(true);
					responseCache.setObject(requestInfo.getRequestRateLimitingConfig());
					cache_rateLimiting.put(key,responseCache);
					//System.out.println("RATE LIMIT SALVATA IN CACHE con chiave ["+requestInfo.getRequestConfig().getKey()+"]");
				}
				else {
					//System.out.println("RATE LIMIT NON SALVATA IN CACHE, GIA PRESENTE");
				}
				
			}finally {
				lockCache_rateLimiting.release("saveRequestRateLimitingConfig", idTransazione);
			}
		}
		
		if(requestInfo.getRequestThreadContext()!=null) {
			requestInfo.getRequestThreadContext().clear();
			requestInfo.setRequestThreadContext(null);
		}
	}
	
	
	private static String buildKey(RequestInfo requestInfo) {
		
		// Lo scopo e' individuare solo la porta applicativa/delegata di default e specifica (azione), il servizio e l'api
		// Se durante la gestione (in *ServiceUtils) viene rilevata una identificazione dell'azione CONTENT_BASED,PROTOCOL_BASED,INPUT_BASED,DELEGATED_BY, la gestione della richiesta tramite url non viene attivata.
		
		StringBuilder bf = new StringBuilder();
		
		List<String> headerInKey = new ArrayList<String>();
		headerInKey.add(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION);
		
		if(requestInfo.getProtocolContext()!=null) {
			
			// NOTA il meccanismo NON deve funzionare se non viene fornita il nome di una porta applicativa come per SPCoop
			if(requestInfo.getProtocolContext().getFunctionParameters()==null || "".equals(requestInfo.getProtocolContext().getFunctionParameters().trim())) {
				return null;
			}
			
			if(requestInfo.getProtocolContext().getRequestType()!=null){
				if(bf.length()>0) {
					bf.append("\t");
				}
				bf.append("Method:").append(requestInfo.getProtocolContext().getRequestType());
			}
			if(requestInfo.getProtocolContext()!=null){
				if(bf.length()>0) {
					bf.append("\t");
				}
				// i parametri non servono per identificare una risorsa
				//bf.append("URL:").append(requestInfo.getProtocolContext().getUrlInvocazione_formBased());
				bf.append("URL:").append(requestInfo.getProtocolContext().getUrlInvocazioneWithoutParameters());
			}
			if(!headerInKey.isEmpty()) {
				for (String header : headerInKey) {
					String s = requestInfo.getProtocolContext().getHeader_compactMultipleValues(header);
					if(s!=null) {
						if(bf.length()>0) {
							bf.append("\t");
						}
						bf.append(header).append(":").append(s);
					}
				}
			}

		}
		
		return bf.toString();
	}
	
	
	// ******** FRUITORI **********
	
	public static RequestFruitore readFruitore(RequestInfo requestInfo, IDSoggetto idSoggetto, IDServizioApplicativo servizioApplicativo) {
		String key = buildKey(idSoggetto, servizioApplicativo);
		if(key==null) {
			return null;
		}
		_readFruitore(requestInfo, key);
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreInfo()!=null) {
			return requestInfo.getRequestThreadContext().getRequestFruitoreInfo();
		}
		else {
			return null;
		}
	}
	
	public static RequestFruitore _readFruitore(RequestInfo requestInfo, String key) {
		
		if(requestInfo==null || requestInfo.getRequestThreadContext()==null) {
			return null;
		}
		
		if(cache_fruitori==null) {
			return null;
		}
		
		org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) cache_fruitori.get(key);
		if(response != null){
			if(response.getObject()!=null){
				RequestFruitore rf = (RequestFruitore) response.getObject(); 
				//System.out.println("TROVATO FUITORE IN CACHE ("+rf.isCached()+")");
				requestInfo.getRequestThreadContext().setRequestFruitoreInfo(rf);
				return rf;
			}
		}
		
		//System.out.println("FRUITORE NON TROVATO IN CACHE");
		return null;
		
	}
	
	public static void saveRequestFruitore(RequestInfo requestInfo, RequestFruitore rf)throws Exception {
		if(requestInfo==null || requestInfo.getRequestThreadContext()==null || rf==null || rf.getIdSoggettoFruitore()==null) {
			return;
		}	
		
		String key = buildKey(rf.getIdSoggettoFruitore(), rf.getIdServizioApplicativoFruitore());
		if(key==null) {
			return;
		}
		
		rf.setKey(key);
		rf.setCached(false);
		requestInfo.getRequestThreadContext().setRequestFruitoreInfo(rf);
		
		
		if(cache_fruitori==null) {
			return; // salvo solo nel thread context
		}
		
		String idTransazione = requestInfo.getIdTransazione();
		
		if(!requestInfo.getRequestThreadContext().getRequestFruitoreInfo().isCached()) {
			lockCache_fruitori.acquire("saveRequestFruitore", idTransazione);
			try {
				org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) cache_fruitori.get(key);
				if(response == null){
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					requestInfo.getRequestThreadContext().getRequestFruitoreInfo().setCached(true);
					responseCache.setObject(requestInfo.getRequestThreadContext().getRequestFruitoreInfo());
					cache_fruitori.put(key,responseCache);
					//System.out.println("FRUITORE SALVATO IN CACHE con chiave ["+requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getKey()+"]");
				}
				else {
					//System.out.println("FUITORE NON SALVATO IN CACHE, GIA PRESENTE");
				}
				
			}finally {
				lockCache_fruitori.release("saveRequestFruitore", idTransazione);
			}
		}
		
	}
	
	private static String buildKey(IDSoggetto idSoggetto, IDServizioApplicativo servizioApplicativo) {
		
		if(idSoggetto==null) {
			return null;
		}
		
		StringBuilder bf = new StringBuilder();
		bf.append(idSoggetto.toString());
		
		bf.append(" sa:");
		if(servizioApplicativo!=null) {
			bf.append(servizioApplicativo.toFormatString());
		}
		else {
			bf.append("-");
		}
		
		return bf.toString();
	}
}
