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
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
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
import org.openspcoop2.utils.cache.Constants;
import org.openspcoop2.utils.certificate.CertificateInfo;
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

	private GestoreRichieste() {}
	
	private static boolean useCache = true;
	public static boolean isUseCache() {
		return useCache;
	}
	public static void setUseCache(boolean useCache) {
		GestoreRichieste.useCache = useCache;
	}

	/** Chiave della cache per il Gestore delle Richieste  */
	public static final String GESTORE_RICHIESTE_PREFIX_CACHE_NAME = "gestoreRichieste-";
	private static final String GESTORE_RICHIESTE_API_CACHE_NAME = GESTORE_RICHIESTE_PREFIX_CACHE_NAME+"API";
	private static final String GESTORE_RICHIESTE_RATE_LIMITING_CACHE_NAME = GESTORE_RICHIESTE_PREFIX_CACHE_NAME+"RateLimiting";
	private static final String GESTORE_RICHIESTE_FRUITORI_CACHE_NAME = GESTORE_RICHIESTE_PREFIX_CACHE_NAME+"Fruitori";
	
	private static Cache cacheApi = null;
	private static Cache cacheRateLimiting = null;
	private static Cache cacheFruitori = null;
	private static Map<String, Cache> caches = null;
	private static List<String> cacheKeys = null;
	private static synchronized void initCacheKeys() {
		if(cacheKeys==null) {
			cacheKeys = new ArrayList<>();
			cacheKeys.add(GESTORE_RICHIESTE_API_CACHE_NAME);
			cacheKeys.add(GESTORE_RICHIESTE_RATE_LIMITING_CACHE_NAME);
			cacheKeys.add(GESTORE_RICHIESTE_FRUITORI_CACHE_NAME);
		}
	}
	
	private static final org.openspcoop2.utils.Semaphore lockCache = new org.openspcoop2.utils.Semaphore(GESTORE_RICHIESTE_API_CACHE_NAME);
	private static final org.openspcoop2.utils.Semaphore lockCache_rateLimiting = new org.openspcoop2.utils.Semaphore(GESTORE_RICHIESTE_RATE_LIMITING_CACHE_NAME);
	private static final org.openspcoop2.utils.Semaphore lockCache_fruitori = new org.openspcoop2.utils.Semaphore(GESTORE_RICHIESTE_FRUITORI_CACHE_NAME);
	
	/** Logger log */
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
						/** String funzione = cacheKey.substring(cacheKey.indexOf("-")+1, cacheKey.length());
						sb.append(funzione).append(separator); */
						sb.append(cache.printStats(separator));
					}
				}
				return sb.toString();
			}
			else{
				throw new CoreException(Constants.MSG_CACHE_NON_ABILITATA);
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
				GestoreRichieste.cacheApi = new Cache(CacheType.JCS, GESTORE_RICHIESTE_API_CACHE_NAME); // lascio JCS come default abilitato via jmx
				GestoreRichieste.cacheRateLimiting = new Cache(CacheType.JCS, GESTORE_RICHIESTE_RATE_LIMITING_CACHE_NAME); // lascio JCS come default abilitato via jmx
				GestoreRichieste.cacheFruitori = new Cache(CacheType.JCS, GESTORE_RICHIESTE_FRUITORI_CACHE_NAME); // lascio JCS come default abilitato via jmx
				
				GestoreRichieste.cacheApi.build();
				GestoreRichieste.cacheRateLimiting.build();
				GestoreRichieste.cacheFruitori.build();
				
				GestoreRichieste.caches = new HashMap<>();
				GestoreRichieste.caches.put(GESTORE_RICHIESTE_API_CACHE_NAME, GestoreRichieste.cacheApi);
				GestoreRichieste.caches.put(GESTORE_RICHIESTE_RATE_LIMITING_CACHE_NAME, GestoreRichieste.cacheRateLimiting);
				GestoreRichieste.caches.put(GESTORE_RICHIESTE_FRUITORI_CACHE_NAME, GestoreRichieste.cacheFruitori);
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
			disabilitaCacheEngine();
		}
	}
	private static synchronized void disabilitaCacheEngine() throws CoreException{
		if(GestoreRichieste.cacheKeys!=null) {
			try{
				GestoreRichieste.cacheApi.clear();
				GestoreRichieste.cacheApi = null;
				
				GestoreRichieste.cacheRateLimiting.clear();
				GestoreRichieste.cacheRateLimiting = null;
				
				GestoreRichieste.cacheFruitori.clear();
				GestoreRichieste.cacheFruitori = null;
				
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
						sb.append( cache.printKeys(separator).replaceFirst(Constants.MSG_CACHE,Constants.MSG_CACHE_PREFIX+nomeCache ) );
					}catch(Exception e){
						throw new CoreException(e.getMessage(),e);
					}
				}
			}
			return sb.toString();
		}else{
			throw new CoreException(Constants.MSG_CACHE_NON_ABILITATA);
		}
	}
	/**
	 * non viene gestito correttamente la chiave rispetto alla cache, potrebbe essere presente la solita chiave
	 */
	@Deprecated
	public static List<String> listKeysCache() throws CoreException{
		if(GestoreRichieste.cacheKeys!=null && !GestoreRichieste.cacheKeys.isEmpty()){
			List<String> keys = new ArrayList<>();
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
			throw new CoreException(Constants.MSG_CACHE_NON_ABILITATA);
		}
	}
	public static String getObjectCache(String key) throws CoreException{
		if(GestoreRichieste.cacheKeys!=null && !GestoreRichieste.cacheKeys.isEmpty()){
			try{
				Object o = null;
				for (String cacheKey : GestoreRichieste.cacheKeys) {
					
					// trucco per avere il valore di una precisa cache				
					if(key.startsWith(Constants.MSG_CACHE_PREFIX)) {
						
						String nomeCache = cacheKey;
						if(nomeCache.startsWith(GestoreRichieste.GESTORE_RICHIESTE_PREFIX_CACHE_NAME)) {
							nomeCache = nomeCache.substring(GestoreRichieste.GESTORE_RICHIESTE_PREFIX_CACHE_NAME.length());
						}
						nomeCache = Constants.MSG_CACHE_PREFIX+nomeCache+" ";
						
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
			throw new CoreException(Constants.MSG_CACHE_NON_ABILITATA);
		}
	}
	public static Object getRawObjectCache(String key) throws CoreException{
		if(GestoreRichieste.cacheKeys!=null && !GestoreRichieste.cacheKeys.isEmpty()){
			try{
				Object o = null;
				for (String cacheKey : GestoreRichieste.cacheKeys) {
					
					// trucco per avere il valore di una precisa cache		
					if(key.startsWith(Constants.MSG_CACHE_PREFIX)) {
						
						String nomeCache = cacheKey;
						if(nomeCache.startsWith(GestoreRichieste.GESTORE_RICHIESTE_PREFIX_CACHE_NAME)) {
							nomeCache = nomeCache.substring(GestoreRichieste.GESTORE_RICHIESTE_PREFIX_CACHE_NAME.length());
						}
						nomeCache = Constants.MSG_CACHE_PREFIX+nomeCache+" ";
						
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
				return getRawObjectCacheEngine(o);
			}catch(Exception e){
				throw new CoreException(e.getMessage(),e);
			}
		}else{
			throw new CoreException(Constants.MSG_CACHE_NON_ABILITATA);
		}
	}
	public static Object getRawObjectCacheEngine(Object o) throws CoreException{
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
					if(key.startsWith(Constants.MSG_CACHE_PREFIX)) {
						
						String nomeCache = cacheKey;
						if(nomeCache.startsWith(GestoreRichieste.GESTORE_RICHIESTE_PREFIX_CACHE_NAME)) {
							nomeCache = nomeCache.substring(GestoreRichieste.GESTORE_RICHIESTE_PREFIX_CACHE_NAME.length());
						}
						nomeCache = Constants.MSG_CACHE_PREFIX+nomeCache+" ";
						
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
			throw new CoreException(Constants.MSG_CACHE_NON_ABILITATA);
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
			long idleTime, long itemLifeSecond, Logger log) throws UtilsException, CoreException {
		
		if(log!=null)
			log.info("Inizializzazione cache GestoreRichieste");

		initCacheKeys();
		
		GestoreRichieste.cacheApi = new Cache(cacheType, GESTORE_RICHIESTE_API_CACHE_NAME); 
		GestoreRichieste.cacheRateLimiting = new Cache(cacheType, GESTORE_RICHIESTE_RATE_LIMITING_CACHE_NAME);
		GestoreRichieste.cacheFruitori = new Cache(cacheType, GESTORE_RICHIESTE_FRUITORI_CACHE_NAME); 
		
		GestoreRichieste.caches = new HashMap<>();
		GestoreRichieste.caches.put(GESTORE_RICHIESTE_API_CACHE_NAME, GestoreRichieste.cacheApi);
		GestoreRichieste.caches.put(GESTORE_RICHIESTE_RATE_LIMITING_CACHE_NAME, GestoreRichieste.cacheRateLimiting);
		GestoreRichieste.caches.put(GESTORE_RICHIESTE_FRUITORI_CACHE_NAME, GestoreRichieste.cacheFruitori);
		
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

		GestoreRichieste.cacheApi.build();
		GestoreRichieste.cacheRateLimiting.build();
		GestoreRichieste.cacheFruitori.build();
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
					if(cache!=null &&
						(!cache.isDisableSyncronizedGet())
						) {
						isDisabled = false;
						break;
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
	
	public static void removeRateLimitingConfigGlobale() throws UtilsException {
		if(GestoreRichieste.cacheRateLimiting!=null) {
			GestoreRichieste.cacheRateLimiting.clear();
		}
	}
	
	public static void removeRateLimitingConfig(TipoPdD tipoPdD, String nomePorta) throws UtilsException, CoreException {
		
		if(GestoreRichieste.cacheRateLimiting!=null) {
			List<String> keyForClean = new ArrayList<>();
			List<String> keys = GestoreRichieste.cacheRateLimiting.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheRateLimiting.get(key));
						if(o instanceof RequestRateLimitingConfig) {
							RequestRateLimitingConfig rl = (RequestRateLimitingConfig) o;
							if(tipoPdD.equals(rl.getTipoPdD()) && nomePorta.equals(rl.getNomePorta())) {
								keyForClean.add(key);
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cacheRateLimiting.remove(key);
				}
			}
		}
		
	}

	
	
	/*----------------- CLEANER --------------------*/
	
	public static void removeApi(IDAccordo idAccordo) throws UtilsException, CoreException {
		
		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		// RequestConfig
		{
			List<String> keyForClean = new ArrayList<>();
			List<String> keys = GestoreRichieste.cacheApi.keys();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheApi.get(key));
						if(o instanceof RequestConfig) {
							RequestConfig rc = (RequestConfig) o;
							IDAccordo idCheck = null;
							try {
								idCheck = idAccordoFactory.getIDAccordoFromAccordo(rc.getAspc());
							}catch(Exception e) {
								throw new CoreException(e.getMessage(),e);
							}
							if(rc.getAspc()!=null && idAccordo.equals(idCheck)) {
								keyForClean.add(key);
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					GestoreRichieste.cacheApi.remove(key);
				}
			}
		}
		
	}
	public static void removeErogazione(IDServizio idServizio) throws UtilsException, CoreException {
		removeErogazioneRequestConfig(idServizio);
		removeErogazioneRequestRateLimitingConfig(idServizio);
	}
	private static void removeErogazioneRequestConfig(IDServizio idServizio) throws UtilsException, CoreException {
		// RequestConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheApi.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheApi.get(key));
					if(o instanceof RequestConfig) {
						RequestConfig rc = (RequestConfig) o;
						if(rc.getIdServizio()!=null && idServizio.equals(rc.getIdServizio(),false)) {
							keyForClean.add(key);
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheApi.remove(key);
			}
		}
	}
	private static void removeErogazioneRequestRateLimitingConfig(IDServizio idServizio) throws UtilsException, CoreException {
		// RequestRateLimitingConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheRateLimiting.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheRateLimiting.get(key));
					if(o instanceof RequestRateLimitingConfig) {
						RequestRateLimitingConfig rl = (RequestRateLimitingConfig) o;
						if(rl.getIdServizio()!=null && idServizio.equals(rl.getIdServizio(),false)) {
							keyForClean.add(key);
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheRateLimiting.remove(key);
			}
		}
	}
	
	public static void removeFruizione(IDSoggetto fruitore, IDServizio idServizio) throws UtilsException, CoreException {
		removeFruizioneRequestConfig(fruitore, idServizio);
		removeFruizioneRequestRateLimitingConfig(fruitore, idServizio);
		removeFruizioneRequestFruitore(fruitore, idServizio);
	}
	private static void removeFruizioneRequestConfig(IDSoggetto fruitore, IDServizio idServizio) throws UtilsException, CoreException {	
		// RequestConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheApi.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheApi.get(key));
					if(o instanceof RequestConfig) {
						RequestConfig rc = (RequestConfig) o;
						if(rc.getIdServizio()!=null && idServizio.equals(rc.getIdServizio(),false) &&
								rc.getIdFruitore()!=null && fruitore.equals(rc.getIdFruitore())) {
							keyForClean.add(key);
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheApi.remove(key);
			}
		}
	}
	private static void removeFruizioneRequestRateLimitingConfig(IDSoggetto fruitore, IDServizio idServizio) throws UtilsException, CoreException {
		// RequestRateLimitingConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheRateLimiting.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheRateLimiting.get(key));
					if(o instanceof RequestRateLimitingConfig) {
						RequestRateLimitingConfig rl = (RequestRateLimitingConfig) o;
						if(rl.getIdServizio()!=null && idServizio.equals(rl.getIdServizio(),false) &&
								rl.getIdFruitore()!=null && fruitore.equals(rl.getIdFruitore())) {
							keyForClean.add(key);
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheRateLimiting.remove(key);
			}
		}
	}
	private static void removeFruizioneRequestFruitore(IDSoggetto fruitore, IDServizio idServizio) throws UtilsException, CoreException {
		// RequestFruitore
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheFruitori.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheFruitori.get(key));
					if(o instanceof RequestFruitore) {
						RequestFruitore rf = (RequestFruitore) o;
						if(rf.getIdSoggettoFruitore()!=null && fruitore.equals(rf.getIdSoggettoFruitore())) {
							keyForClean.add(key);
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheFruitori.remove(key);
			}
		}
	}
	
	public static void removePortaApplicativa(IDPortaApplicativa idPA) throws UtilsException, CoreException {
		removePortaApplicativaRequestConfig(idPA);
		removePortaApplicativaRequestRateLimitingConfig(idPA) ;
	}
	private static void removePortaApplicativaRequestConfig(IDPortaApplicativa idPA) throws UtilsException, CoreException {
		// RequestConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheApi.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheApi.get(key));
					if(o instanceof RequestConfig) {
						RequestConfig rc = (RequestConfig) o;
						if(rc.getIdPortaApplicativaDefault()!=null && idPA.equals(rc.getIdPortaApplicativaDefault())) {
							keyForClean.add(key);
						}
						else if(rc.getIdPortaApplicativa()!=null && idPA.equals(rc.getIdPortaApplicativa())) {
							keyForClean.add(key);
						}
						else if(rc.getPortaApplicativaDefault()!=null && rc.getPortaApplicativaDefault().getNome()!=null && idPA!=null && 
								rc.getPortaApplicativaDefault().getNome().equals(idPA.getNome())) {
							keyForClean.add(key);
						}
						else if(rc.getPortaApplicativa()!=null && rc.getPortaApplicativa().getNome()!=null && idPA!=null && 
								rc.getPortaApplicativa().getNome().equals(idPA.getNome())) {
							keyForClean.add(key);
						}
						else {
							
							boolean find = false;
							if(rc.getListPorteApplicativeByFiltroRicerca()!=null && !rc.getListPorteApplicativeByFiltroRicerca().isEmpty()) {
								for (String keyID : rc.getListPorteApplicativeByFiltroRicerca().keySet()) {
									List<IDPortaApplicativa> ids =  rc.getListPorteApplicativeByFiltroRicerca().get(keyID);
									if(ids!=null && ids.contains(idPA)) {
										find = true;
										break;
									}
								}
							}
							if(!find &&
								rc.getListMappingErogazionePortaApplicativa()!=null && !rc.getListMappingErogazionePortaApplicativa().isEmpty()) {
								for (MappingErogazionePortaApplicativa mapping : rc.getListMappingErogazionePortaApplicativa()) {
									if(mapping!=null && mapping.getIdPortaApplicativa()!=null && mapping.getIdPortaApplicativa().equals(idPA)) {
										find = true;
										break;
									}
								}
							}
							if(find) {
								keyForClean.add(key);
							}
							
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheApi.remove(key);
			}
		}
	}
	private static void removePortaApplicativaRequestRateLimitingConfig(IDPortaApplicativa idPA) throws UtilsException, CoreException {
		// RequestRateLimitingConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheRateLimiting.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheRateLimiting.get(key));
					if(o instanceof RequestRateLimitingConfig) {
						RequestRateLimitingConfig rl = (RequestRateLimitingConfig) o;
						if(TipoPdD.APPLICATIVA.equals(rl.getTipoPdD()) && idPA.getNome().equals(rl.getNomePorta())) {
							keyForClean.add(key);
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheRateLimiting.remove(key);
			}
		}
	}
	
	public static void removeConnettore(IDConnettore idConnettore)throws UtilsException, CoreException {
		
		// RequestConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheApi.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheApi.get(key));
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
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheApi.remove(key);
			}
		}
		
	}
	
	public static void removePortaDelegata(IDPortaDelegata idPD) throws UtilsException, CoreException {
		removePortaDelegataRequestConfig(idPD);
		removePortaDelegataRequestRateLimitingConfig(idPD);
	}
	private static void removePortaDelegataRequestConfig(IDPortaDelegata idPD) throws UtilsException, CoreException {
		// RequestConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheApi.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheApi.get(key));
					if(o instanceof RequestConfig) {
						RequestConfig rc = (RequestConfig) o;
						if(rc.getIdPortaDelegataDefault()!=null && idPD.equals(rc.getIdPortaDelegataDefault())) {
							keyForClean.add(key);
						}
						else if(rc.getIdPortaDelegata()!=null && idPD.equals(rc.getIdPortaDelegata())) {
							keyForClean.add(key);
						}
						else if(rc.getPortaDelegataDefault()!=null && rc.getPortaDelegataDefault().getNome()!=null && idPD!=null && 
								rc.getPortaDelegataDefault().getNome().equals(idPD.getNome())) {
							keyForClean.add(key);
						}
						else if(rc.getPortaDelegata()!=null && rc.getPortaDelegata().getNome()!=null && idPD!=null && 
								rc.getPortaDelegata().getNome().equals(idPD.getNome())) {
							keyForClean.add(key);
						}
						else {
							
							boolean find = false;
							if(rc.getListPorteDelegateByFiltroRicerca()!=null && !rc.getListPorteDelegateByFiltroRicerca().isEmpty()) {
								for (String keyID : rc.getListPorteDelegateByFiltroRicerca().keySet()) {
									List<IDPortaDelegata> ids =  rc.getListPorteDelegateByFiltroRicerca().get(keyID);
									if(ids!=null && ids.contains(idPD)) {
										find = true;
										break;
									}
								}
							}
							if(!find &&
								rc.getListMappingFruizionePortaDelegata()!=null && !rc.getListMappingFruizionePortaDelegata().isEmpty()) {
								for (MappingFruizionePortaDelegata mapping : rc.getListMappingFruizionePortaDelegata()) {
									if(mapping!=null && mapping.getIdPortaDelegata()!=null && mapping.getIdPortaDelegata().equals(idPD)) {
										find = true;
										break;
									}
								}
							}
							if(find) {
								keyForClean.add(key);
							}
							
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheApi.remove(key);
			}
		}
	}
	private static void removePortaDelegataRequestRateLimitingConfig(IDPortaDelegata idPD) throws UtilsException, CoreException {	
		// RequestRateLimitingConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheRateLimiting.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheRateLimiting.get(key));
					if(o instanceof RequestRateLimitingConfig) {
						RequestRateLimitingConfig rl = (RequestRateLimitingConfig) o;
						if(TipoPdD.DELEGATA.equals(rl.getTipoPdD()) && idPD.getNome().equals(rl.getNomePorta())) {
							keyForClean.add(key);
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheRateLimiting.remove(key);
			}
		}
	}
	
	public static void removePdd(String portaDominio) throws UtilsException, CoreException {
		
		// RequestConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheApi.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheApi.get(key));
					if(o instanceof RequestConfig) {
						RequestConfig rc = (RequestConfig) o;
						if(
								(rc.getSoggettoErogatorePdd()!=null && rc.getSoggettoErogatorePdd().getNome()!=null && portaDominio.equals(rc.getSoggettoErogatorePdd().getNome()))
								||
								(rc.getSoggettoFruitorePdd()!=null && rc.getSoggettoFruitorePdd().getNome()!=null && portaDominio.equals(rc.getSoggettoFruitorePdd().getNome()))
							) {
							keyForClean.add(key);
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheApi.remove(key);
			}
		}
		
	}
	
	public static void removeSoggetto(IDSoggetto idSoggetto) throws UtilsException, CoreException {
		removeSoggettoRequestConfig(idSoggetto);
		removeSoggettoRequestRateLimitingConfig(idSoggetto);
		removeSoggettoRequestFruitore(idSoggetto);
	}
	private static void removeSoggettoRequestConfig(IDSoggetto idSoggetto) throws UtilsException, CoreException {	
		// RequestConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheApi.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheApi.get(key));
					if(o instanceof RequestConfig) {
						RequestConfig rc = (RequestConfig) o;
						if(
								(rc.getIdServizio()!=null && rc.getIdServizio().getSoggettoErogatore()!=null && idSoggetto.equals(rc.getIdServizio().getSoggettoErogatore()))
								||
								(rc.getIdFruitore()!=null && idSoggetto.equals(rc.getIdFruitore()))
							) {
							keyForClean.add(key);
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheApi.remove(key);
			}
		}
	}
	private static void removeSoggettoRequestRateLimitingConfig(IDSoggetto idSoggetto) throws UtilsException, CoreException {
		// RequestRateLimitingConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheRateLimiting.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheRateLimiting.get(key));
					if(o instanceof RequestRateLimitingConfig) {
						RequestRateLimitingConfig rl = (RequestRateLimitingConfig) o;
						if(
								(rl.getIdServizio()!=null && rl.getIdServizio().getSoggettoErogatore()!=null && idSoggetto.equals(rl.getIdServizio().getSoggettoErogatore()))
								||
								(rl.getIdFruitore()!=null && idSoggetto.equals(rl.getIdFruitore()))
							) {
							keyForClean.add(key);
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheRateLimiting.remove(key);
			}
		}
	}
	private static void removeSoggettoRequestFruitore(IDSoggetto idSoggetto) throws UtilsException, CoreException {
		// RequestFruitore
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheFruitori.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheFruitori.get(key));
					if(o instanceof RequestFruitore) {
						RequestFruitore rf = (RequestFruitore) o;
						if(
								(rf.getIdServizioApplicativoFruitore()!=null && rf.getIdServizioApplicativoFruitore().getIdSoggettoProprietario()!=null && idSoggetto.equals(rf.getIdServizioApplicativoFruitore().getIdSoggettoProprietario()))
								||
								(rf.getIdSoggettoFruitore()!=null && idSoggetto.equals(rf.getIdSoggettoFruitore()))
							) {
							keyForClean.add(key);
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheFruitori.remove(key);
			}
		}
	}
	
	public static void removeApplicativo(IDServizioApplicativo idApplicativo) throws UtilsException, CoreException {
		removeApplicativoRequestConfig(idApplicativo);
		removeApplicativoRequestFruitore(idApplicativo);
	}
	private static void removeApplicativoRequestConfig(IDServizioApplicativo idApplicativo) throws UtilsException, CoreException {
		// RequestConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheApi.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheApi.get(key));
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
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheApi.remove(key);
			}
		}
	}
	private static void removeApplicativoRequestFruitore(IDServizioApplicativo idApplicativo) throws UtilsException, CoreException {	
		// RequestFruitore
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheFruitori.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheFruitori.get(key));
					if(o instanceof RequestFruitore) {
						RequestFruitore rf = (RequestFruitore) o;
						if(rf.getIdServizioApplicativoFruitore()!=null && idApplicativo.equals(rf.getIdServizioApplicativoFruitore())) {
							keyForClean.add(key);
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheFruitori.remove(key);
			}
		}
	}
	
	
	public static void removeRuolo(IDRuolo idRuolo) throws UtilsException, CoreException {
		// RequestConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheApi.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheApi.get(key));
					if(o instanceof RequestConfig) {
						RequestConfig rc = (RequestConfig) o;
						
						List<String> ruoloKeys = rc.getRuoloKeys();
						if(ruoloKeys!=null && ruoloKeys.contains(idRuolo.getNome())) {
							keyForClean.add(key);
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheApi.remove(key);
			}
		}
	}
	
	public static void removeScope(IDScope idScope) throws UtilsException, CoreException {
		// RequestConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheApi.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheApi.get(key));
					if(o instanceof RequestConfig) {
						RequestConfig rc = (RequestConfig) o;
						
						List<String> scopeKeys = rc.getScopeKeys();
						if(scopeKeys!=null && scopeKeys.contains(idScope.getNome())) {
							keyForClean.add(key);
						}
					}
				}
			}
		}
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheApi.remove(key);
			}
		}
	}
	
	
	public static void removeGenericProperties(IDGenericProperties idGP) throws UtilsException, CoreException {
		// RequestConfig
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreRichieste.cacheApi.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String key : keys) {
				if(key!=null) {
					Object o =  getRawObjectCacheEngine(GestoreRichieste.cacheApi.get(key));
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
		if(keyForClean!=null && !keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				GestoreRichieste.cacheApi.remove(key);
			}
		}
	}
	
	
	

	
	// ******** RICHIESTE **********
	
	private static final String KEY_IN_MEMORY_ONLY = "@@InMemoryOnly@@";
	private static final String INIT_SEPARATOR = "\n\n=======================";
	private static final String END_SEPARATOR = "=======================";
	
	public static void setRequestConfigInMemory(RequestInfo requestInfo) {
		if(org.openspcoop2.utils.cache.Cache.DEBUG_CACHE) {
			System.out.println(INIT_SEPARATOR);
			System.out.println("Creato RequestConfig in ram");
			System.out.println(END_SEPARATOR);
		}
					
		RequestConfig rc = new RequestConfig();
		rc.setKey(KEY_IN_MEMORY_ONLY);
		rc.setCached(false);
		requestInfo.setRequestConfig(rc);
		
		RequestRateLimitingConfig rcRT = new RequestRateLimitingConfig();
		rcRT.setKey(KEY_IN_MEMORY_ONLY);
		rcRT.setCached(false);
		requestInfo.setRequestRateLimitingConfig(rcRT);
		
		RequestThreadContext rt = new RequestThreadContext(KEY_IN_MEMORY_ONLY, logger!=null ? logger : logConsole);
		requestInfo.setRequestThreadContext(rt);
	}
	
	public static void readRequestConfig(RequestInfo requestInfo) {
		
		if(!useCache) {
			setRequestConfigInMemory(requestInfo);
			return;
		}
		
		String key = buildKey(requestInfo, null, null);
		if(key==null) {
			return;
		}
		readRequestConfigEngine(requestInfo, key, null);
		readRequestRateLimitingConfigEngine(requestInfo, key);
		requestInfo.setRequestThreadContext(RequestThreadContext.getRequestThreadContext());
		if(requestInfo.getRequestThreadContext()!=null) {
			// ripulisco precedenti assegnamenti
			requestInfo.getRequestThreadContext().setRequestFruitoreTrasportoInfo(null);
			requestInfo.getRequestThreadContext().setRequestFruitoreTokenInfo(null);
		}
		
		if(org.openspcoop2.utils.cache.Cache.DEBUG_CACHE) {
			System.out.println(INIT_SEPARATOR);
			System.out.println("Creato RequestConfig con chiave '"+key+"'");
			System.out.println("\tRequestThreadContext '"+requestInfo.getRequestThreadContext().gettName()+"'");
			System.out.println(END_SEPARATOR);
		}
	}
	
	public static void updateRequestConfig(RequestInfo requestInfo, ServiceBinding serviceBinding, OpenSPCoop2MessageSoapStreamReader soapStreamReader) {
		
		if(!useCache) {
			return; // solo in memory, la differenza sull'azione non è importante
		}
		
		if(requestInfo==null || requestInfo.getRequestConfig()==null) {
			return;
		}
		String oldKey = requestInfo.getRequestConfig().getKey();
		String key = buildKey(requestInfo, serviceBinding, soapStreamReader);
		if(key==null) {
			return;
		}
		if(key.equals(oldKey)) {
			return; // non e' stato aggiunto il soapStreamReader o modifiche derivanti all'azione specifica
		}
		
		if(org.openspcoop2.utils.cache.Cache.DEBUG_CACHE) {
			System.out.println(INIT_SEPARATOR);
			System.out.println("AGGIORNO RequestConfig");
			System.out.println("OLD: "+oldKey);
			System.out.println("NEW: "+key);
			System.out.println(END_SEPARATOR);
		}
		
		RequestConfig rcOld = requestInfo.getRequestConfig();
		
		// Aggiorno nel contesto nuovi oggetti con nuova chiave
		readRequestConfigEngine(requestInfo, key, rcOld);
		readRequestRateLimitingConfigEngine(requestInfo, key);

		requestInfo.setPreRequestConfig(rcOld);

	}
	
	private static void readRequestConfigEngine(RequestInfo requestInfo, String key, RequestConfig srcClone) {
		
		if(cacheApi==null) {
			return;
		}
		
		org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) cacheApi.get(key);
		if(response != null &&
			response.getObject()!=null){
			RequestConfig rc = (RequestConfig) response.getObject(); 
			/**System.out.println("TROVATA IN CACHE ("+rc.isCached()+")");*/
			if(rc.getIdServizio()!=null) {
				rc.setIdServizio(rc.getIdServizio().clone()); // per l'azione
			}
			requestInfo.setRequestConfig(rc);
			return;
		}
		
		/**System.out.println("CONFIG NON TROVATA IN CACHE");*/
		RequestConfig rc = new RequestConfig();
		if(srcClone!=null) {
			rc.copyFrom(srcClone);
		}
		else {
			rc = new RequestConfig();
		}
		rc.setKey(key);
		rc.setCached(false);
		requestInfo.setRequestConfig(rc);
		
	}
	
	private static void readRequestRateLimitingConfigEngine(RequestInfo requestInfo, String key) {
		
		if(cacheRateLimiting==null) {
			return;
		}
		
		org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) cacheRateLimiting.get(key);
		if(response != null &&
			response.getObject()!=null){
			RequestRateLimitingConfig rc = (RequestRateLimitingConfig) response.getObject(); 
			/** System.out.println("TROVATA IN CACHE ("+rc.isCached()+")"); */
			requestInfo.setRequestRateLimitingConfig(rc);
			return;
		}
		
		/** System.out.println("RATE LIMIT NON TROVATA IN CACHE"); */
		RequestRateLimitingConfig rc = new RequestRateLimitingConfig();
		rc.setKey(key);
		rc.setCached(false);
		requestInfo.setRequestRateLimitingConfig(rc);
		
	}
	
	public static void saveRequestConfig(RequestInfo requestInfo)throws UtilsException, CoreException {
		
		if(!useCache) {
			return;
		}
		
		if(requestInfo==null) {
			return;
		}		
		
		String idTransazione = requestInfo.getIdTransazione();
		
		if(cacheApi!=null && requestInfo.getPreRequestConfig()!=null && !requestInfo.getPreRequestConfig().isCached()) {
			lockCache.acquire("savePreRequestConfig", idTransazione);
			try {
				String key = requestInfo.getPreRequestConfig().getKey();
				org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) cacheApi.get(key);
				if(response == null){
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					requestInfo.getPreRequestConfig().setCached(true);
					responseCache.setObject(requestInfo.getPreRequestConfig());
					cacheApi.put(key,responseCache);
					/** System.out.println("SALVATA IN CACHE con chiave ["+requestInfo.getPreRequestConfig().getKey()+"]"); */
				}
				else {
					/** System.out.println("NON SALVATA IN CACHE, GIA PRESENTE"); */
				}
				
			}finally {
				lockCache.release("savePreRequestConfig", idTransazione);
			}
		}
		
		if(cacheApi!=null && requestInfo.getRequestConfig()!=null && !requestInfo.getRequestConfig().isCached()) {
			lockCache.acquire("saveRequestConfig", idTransazione);
			try {
				String key = requestInfo.getRequestConfig().getKey();
				org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) cacheApi.get(key);
				if(response == null){
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					requestInfo.getRequestConfig().setCached(true);
					responseCache.setObject(requestInfo.getRequestConfig());
					cacheApi.put(key,responseCache);
					/** System.out.println("SALVATA IN CACHE con chiave ["+requestInfo.getRequestConfig().getKey()+"]");*/
				}
				else {
					/**System.out.println("NON SALVATA IN CACHE, GIA PRESENTE");*/
				}
				
			}finally {
				lockCache.release("saveRequestConfig", idTransazione);
			}
		}
		
		if(cacheRateLimiting!=null && requestInfo.getRequestRateLimitingConfig()!=null && !requestInfo.getRequestRateLimitingConfig().isCached()) {
			lockCache_rateLimiting.acquire("saveRequestRateLimitingConfig", idTransazione);
			try {
				String key = requestInfo.getRequestConfig().getKey();
				org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) cacheRateLimiting.get(key);
				if(response == null){
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					requestInfo.getRequestRateLimitingConfig().setCached(true);
					responseCache.setObject(requestInfo.getRequestRateLimitingConfig());
					cacheRateLimiting.put(key,responseCache);
					/** System.out.println("RATE LIMIT SALVATA IN CACHE con chiave ["+requestInfo.getRequestConfig().getKey()+"]");*/
				}
				else {
					/** System.out.println("RATE LIMIT NON SALVATA IN CACHE, GIA PRESENTE");*/
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
	
	
	private static String buildKey(RequestInfo requestInfo, ServiceBinding serviceBinding, OpenSPCoop2MessageSoapStreamReader soapStreamReader) {
		
		// Lo scopo e' individuare solo la porta applicativa/delegata di default e specifica (azione), il servizio e l'api
		// NOTA: 
		//     Se durante la gestione (in *ServiceUtils, tramite la chiamata del metodo RequestInfoConfigUtilities.checkRequestInfoConfig) 
		//     viene rilevata una identificazione dell'azione CONTENT_BASED,PROTOCOL_BASED,INPUT_BASED,DELEGATED_BY, la gestione della richiesta tramite url non viene attivata.
		//     Inoltre se si rileva una API SOAP con il riconoscimento dell'azione basata su wsdl (quindi rootElement), e tale informazione non è disponibile nel soapStreamReader
		//     la gestione della richiesta tramite url non viene attivata.
		
		StringBuilder bf = new StringBuilder();
		
		List<String> headerInKey = new ArrayList<>();
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
			else {
				return null;
			}
			
			if(requestInfo.getProtocolContext()!=null){
				if(bf.length()>0) {
					bf.append("\t");
				}
				// i parametri non servono per identificare una risorsa
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
		else {
			return null;
		}
		
		if( ServiceBinding.SOAP.equals(serviceBinding) && soapStreamReader!=null) {
			if(soapStreamReader.getRootElementLocalName()!=null && soapStreamReader.getRootElementNamespace()!=null) {
				if(bf.length()>0) {
					bf.append("\t");
				}
				bf.append("soapRootElement:{").
					append(soapStreamReader.getRootElementNamespace()).
					append("}").
					append(soapStreamReader.getRootElementLocalName());
			}
			else {
				return null;
			}
		}
		
		return bf.toString();
	}
	
	
	// ******** FRUITORI **********
	private static final String FRUITORE_TRASPORTO = "trasparto";
	private static final String FRUITORE_TOKEN = "token";
	private static final String FRUITORE_TOKEN_MODI = "token-modi";
	public static RequestFruitore readFruitoreTrasporto(RequestInfo requestInfo, IDSoggetto idSoggetto, IDServizioApplicativo servizioApplicativo) {
		return readFruitoreEngine(requestInfo, idSoggetto, servizioApplicativo, null, FRUITORE_TRASPORTO, true, false);
	}
	public static RequestFruitore readFruitoreToken(RequestInfo requestInfo, IDSoggetto idSoggetto, IDServizioApplicativo servizioApplicativo) {
		return readFruitoreEngine(requestInfo, idSoggetto, servizioApplicativo, null, FRUITORE_TOKEN, false, false);
	}
	public static RequestFruitore readFruitoreTokenModI(RequestInfo requestInfo, String certificateKey) {
		return readFruitoreEngine(requestInfo, null, null, certificateKey, FRUITORE_TOKEN_MODI, false, true);
	}
	private static RequestFruitore readFruitoreEngine(RequestInfo requestInfo, IDSoggetto idSoggetto, IDServizioApplicativo servizioApplicativo, String certificateKey, 
			String tipo, boolean trasporto, boolean tokenModi) {
		String key = null;
		if(tokenModi) {		
			key = buildKey(null, null, certificateKey, tipo, tokenModi);
		}
		else {
			key = buildKey(idSoggetto, servizioApplicativo,null, tipo, tokenModi);
		}
		if(key==null &&
			useCache) {
			return null;
		}
		if(useCache) {
			readFruitoreEngine(requestInfo, key, trasporto);
		}
		if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null) {
			if(trasporto) {
				if(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo()!=null) {
					return requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo();
				}	
				else {
					return null;
				}
			}
			else {
				if(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo()!=null) {
					return requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo();
				}	
				else {
					return null;
				}
			}
		}
		else {
			return null;
		}
	}
		
	public static RequestFruitore readFruitoreEngine(RequestInfo requestInfo, String key, boolean trasporto) {
		
		if(requestInfo==null || requestInfo.getRequestThreadContext()==null) {
			return null;
		}
		
		if(cacheFruitori==null) {
			return null;
		}
		
		org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) cacheFruitori.get(key);
		if(response != null &&
			response.getObject()!=null){
			RequestFruitore rf = (RequestFruitore) response.getObject(); 
			/** System.out.println("TROVATO FUITORE IN CACHE ("+rf.isCached()+")");*/
			if(trasporto) {
				requestInfo.getRequestThreadContext().setRequestFruitoreTrasportoInfo(rf);
			}
			else {
				requestInfo.getRequestThreadContext().setRequestFruitoreTokenInfo(rf);
			}
			return rf;
		}
		
		/** System.out.println("FRUITORE NON TROVATO IN CACHE");*/
		return null;
		
	}
	
	public static void saveRequestFruitoreTrasporto(RequestInfo requestInfo, RequestFruitore rf)throws UtilsException {
		 saveRequestFruitoreEngine(requestInfo, rf, FRUITORE_TRASPORTO, true, false);
	}
	public static void saveRequestFruitoreToken(RequestInfo requestInfo, RequestFruitore rf)throws UtilsException {
		 saveRequestFruitoreEngine(requestInfo, rf, FRUITORE_TOKEN, false, false);
	}
	public static void saveRequestFruitoreTokenModI(RequestInfo requestInfo, RequestFruitore rf)throws UtilsException {
		 saveRequestFruitoreEngine(requestInfo, rf, FRUITORE_TOKEN_MODI, false, true);
	}
	private static void saveRequestFruitoreEngine(RequestInfo requestInfo, RequestFruitore rf, 
			String tipo, boolean trasporto, boolean tokenModi) throws UtilsException {
		if(requestInfo==null || requestInfo.getRequestThreadContext()==null || rf==null) {
			return;
		}	
		
		String key = null;
		if(useCache) {
			if(tokenModi) {
				if(rf.getCertificateKey()==null) {
					return;
				}
				key = buildKey(null, null, rf.getCertificateKey(), tipo, tokenModi);
			}
			else {
				if(rf.getIdSoggettoFruitore()==null) {
					return;
				}
				key = buildKey(rf.getIdSoggettoFruitore(), rf.getIdServizioApplicativoFruitore(), null, tipo, tokenModi);
			}
			if(key==null) {
				return;
			}
		}
		else {
			key=KEY_IN_MEMORY_ONLY;
		}
		
		rf.setKey(key);
		rf.setCached(false);
		if(trasporto) {
			requestInfo.getRequestThreadContext().setRequestFruitoreTrasportoInfo(rf);
		}
		else {
			requestInfo.getRequestThreadContext().setRequestFruitoreTokenInfo(rf);
		}
		
		
		if(!useCache || cacheFruitori==null) {
			return; // salvo solo nel thread context
		}
		
		String idTransazione = requestInfo.getIdTransazione();
		
		RequestFruitore requestFruitore = null;
		if(trasporto) {
			requestFruitore = requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo();
		}
		else {
			requestFruitore = requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo();
		}
		
		if(!requestFruitore.isCached()) {
			lockCache_fruitori.acquire("saveRequestFruitore", idTransazione);
			try {
				org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) cacheFruitori.get(key);
				if(response == null){
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					requestFruitore.setCached(true);
					responseCache.setObject(requestFruitore);
					cacheFruitori.put(key,responseCache);
					/** System.out.println("FRUITORE SALVATO IN CACHE con chiave ["+requestInfo.getRequestThreadContext().getRequestFruitoreInfo().getKey()+"]"); */
				}
				else {
					/** System.out.println("FUITORE NON SALVATO IN CACHE, GIA PRESENTE"); */
				}
				
			}finally {
				lockCache_fruitori.release("saveRequestFruitore", idTransazione);
			}
		}
		
	}
	
	private static String buildKey(IDSoggetto idSoggetto, IDServizioApplicativo servizioApplicativo, String certificateKey, String tipo, boolean tokenModi) {
				
		StringBuilder bf = new StringBuilder();
		bf.append("auth:").append(tipo);
		
		if(tokenModi) {
			if(certificateKey==null) {
				return null;
			}
			bf.append(" cert:");
			bf.append(certificateKey);
		}
		else {
			if(idSoggetto==null) {
				return null;
			}
			bf.append(" soggetto:");
			bf.append(idSoggetto.toString());
			
			bf.append(" sa:");
			if(servizioApplicativo!=null) {
				bf.append(servizioApplicativo.toFormatString());
			}
			else {
				bf.append("-");
			}
		}
		
		return bf.toString();
	}
	
	public static String toCertificateKey(CertificateInfo certificateInfo) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if(certificateInfo.getSubject()!=null) {
			sb.append("subject:");
			sb.append(certificateInfo.getSubject().toString());
		}
		else {
			return null;
		}
		if(certificateInfo.getIssuer()!=null) {
			sb.append(" issuer:");
			sb.append(certificateInfo.getIssuer().toString());
		}
		else {
			return null;
		}
		if(certificateInfo.getSerialNumber()!=null) {
			sb.append(" serialNumber:");
			sb.append(certificateInfo.getSerialNumber());
		}
		else {
			return null;
		}
		sb.append("]");
		return sb.toString();
	}
}
