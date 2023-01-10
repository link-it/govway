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
package org.openspcoop2.pdd.core.response_caching;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.cache.CacheType;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.slf4j.Logger;

/**     
 * GestoreCacheResponseCaching
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreCacheResponseCaching {

	/** Chiave della cache per l'autenticazione Buste  */
	private static final String RESPONSE_CACHING_CACHE_NAME = "responseCaching";
	/** Cache */
	private static Cache cache = null;
	private static final org.openspcoop2.utils.Semaphore lock_cache = new org.openspcoop2.utils.Semaphore("GestoreCacheResponseCaching");
	

	/* --------------- Cache --------------------*/
	public static boolean isCacheAbilitata() {
		return cache!=null;
	}
	public static void resetCache() throws Exception{
		try{
			if(cache!=null){
				cache.clear();
			}
		}catch(Exception e){
			throw new Exception("Reset della cache per i dati contenenti le risposte salvate non riuscita: "+e.getMessage(),e);
		}
	}
	public static String printStatsCache(String separator) throws Exception{
		try{
			if(cache!=null){
				try{
					return cache.printStats(separator);
				}catch(Exception e){
					throw new Exception(e.getMessage(),e);
				}
			}else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new Exception("Visualizzazione Statistiche riguardante la cache per i dati contenenti le risposte salvate non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws Exception{
		try{
			if(cache!=null)
				throw new Exception("Cache gia' abilitata");
			else{
				_abilitaCache();
			}
		}catch(Exception e){
			throw new Exception("Abilitazione cache per i dati contenenti le risposte salvate non riuscita: "+e.getMessage(),e);
		}
	}
	private static synchronized void _abilitaCache() throws Exception{
		try{
			if(cache==null) {
				cache = new Cache(CacheType.JCS, RESPONSE_CACHING_CACHE_NAME);  // lascio JCS come default abilitato via jmx
				cache.build();
			}
		}catch(Exception e){
			throw new Exception("Abilitazione cache per i dati contenenti le risposte salvate non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond, Logger log) throws Exception{
		try{
			if(cache!=null)
				throw new Exception("Cache gia' abilitata");
			else{
				int dimensione = -1;
				if(dimensioneCache!=null){
					dimensione = dimensioneCache.intValue();
				}
				initCache(CacheType.JCS, dimensione, algoritmoCacheLRU, itemIdleTime, itemLifeSecond, log);  // lascio JCS come default abilitato via jmx
			}
		}catch(Exception e){
			throw new Exception("Abilitazione cache per i dati contenenti le risposte salvate non riuscita: "+e.getMessage(),e);
		}
	}
	public static void disabilitaCache() throws Exception{
		try{
			if(cache==null)
				throw new Exception("Cache gia' disabilitata");
			else{
				_disabilitaCache();
			}
		}catch(Exception e){
			throw new Exception("Disabilitazione cache per i dati contenenti le risposte salvate non riuscita: "+e.getMessage(),e);
		}
	}	
	private static synchronized void _disabilitaCache() throws Exception{
		try{
			if(cache!=null) {
				cache.clear();
				cache = null;
			}
		}catch(Exception e){
			throw new Exception("Disabilitazione cache per i dati contenenti le risposte salvate non riuscita: "+e.getMessage(),e);
		}
	}	
	public static String listKeysCache(String separator) throws Exception{
		try{
			if(cache!=null){
				try{
					return cache.printKeys(separator);
				}catch(Exception e){
					throw new Exception(e.getMessage(),e);
				}
			}else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new Exception("Visualizzazione chiavi presenti nella cache per i dati contenenti le risposte salvate non riuscita: "+e.getMessage(),e);
		}
	}
	
	public static String getObjectCache(String key) throws Exception{
		try{
			if(cache!=null){
				try{
					Object o = cache.get(key);
					if(o!=null){
						return o.toString();
					}else{
						return "oggetto con chiave ["+key+"] non presente";
					}
				}catch(Exception e){
					throw new Exception(e.getMessage(),e);
				}
			}else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new Exception("Visualizzazione oggetto presente nella cache per i dati contenenti le risposte salvate non riuscita: "+e.getMessage(),e);
		}
	}
	
	public static void removeObjectCache(String key) throws Exception{
		try{
			if(cache!=null){
				try{
					cache.remove(key);
				}catch(Exception e){
					throw new Exception(e.getMessage(),e);
				}
			}else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new Exception("Rimozione oggetto presente nella cache per i dati contenenti le risposte salvate non riuscita: "+e.getMessage(),e);
		}
	}
	

	/*----------------- INIZIALIZZAZIONE --------------------*/

	public static void initialize(Logger log) throws Exception{
		GestoreCacheResponseCaching.initialize(null, false, -1,null,-1l,-1l, log);
	}
	public static void initialize(CacheType cacheType, int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{
		GestoreCacheResponseCaching.initialize(cacheType, true, dimensioneCache,algoritmoCache,idleTime,itemLifeSecond, log);
	}

	private static void initialize(CacheType cacheType, boolean cacheAbilitata,int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{

		// Inizializzazione Cache
		if(cacheAbilitata){
			GestoreCacheResponseCaching.initCache(cacheType, dimensioneCache, algoritmoCache, idleTime, itemLifeSecond, log);
		}

	}
	private static void initCache(CacheType cacheType, Integer dimensioneCache,String algoritmoCache,Long itemIdleTime,Long itemLifeSecond,Logger alog) throws Exception{
		initCache(cacheType, dimensioneCache, CostantiConfigurazione.CACHE_LRU.toString().equalsIgnoreCase(algoritmoCache), itemIdleTime, itemLifeSecond, alog);
	}
	
	private static void initCache(CacheType cacheType, Integer dimensioneCache,boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond,Logger alog) throws Exception{
		
		cache = new Cache(cacheType, RESPONSE_CACHING_CACHE_NAME);
	
		// dimensione
		if(dimensioneCache!=null && dimensioneCache>0){
			try{
				String msg = "Dimensione della cache (ResponseCaching) impostata al valore: "+dimensioneCache;
				alog.info(msg);
				cache.setCacheSize(dimensioneCache);
			}catch(Exception error){
				String msg = "Parametro errato per la dimensione della cache (ResponseCaching): "+error.getMessage();
				alog.error(msg);
				throw new Exception(msg,error);
			}
		}
		
		// algoritno
		String msg = "Algoritmo di cache (ResponseCaching) impostato al valore: LRU";
		if(!algoritmoCacheLRU){
			msg = "Algoritmo di cache (ResponseCaching) impostato al valore: MRU";
		}
		alog.info(msg);
		if(!algoritmoCacheLRU)
			cache.setCacheAlgoritm(CacheAlgorithm.MRU);
		else
			cache.setCacheAlgoritm(CacheAlgorithm.LRU);
		
		
		// idle time
		if(itemIdleTime!=null && itemIdleTime>0){
			try{
				msg = "Attributo 'IdleTime' (ResponseCaching) impostato al valore: "+itemIdleTime;
				alog.info(msg);
				cache.setItemIdleTime(itemIdleTime);
			}catch(Exception error){
				msg = "Parametro errato per l'attributo 'IdleTime' (ResponseCaching): "+error.getMessage();
				alog.error(msg);
				throw new Exception(msg,error);
			}
		}
		
		// LifeSecond
		long longItemLife = -1; 
		if(itemLifeSecond!=null && itemLifeSecond>0){
			longItemLife = itemLifeSecond.longValue();
		}
		try{
			msg = "Attributo 'MaxLifeSecond' (ResponseCaching) impostato al valore: "+longItemLife;
			alog.info(msg);
			cache.setItemLifeTime(longItemLife);
		}catch(Exception error){
			msg = "Parametro errato per l'attributo 'MaxLifeSecond' (ResponseCaching): "+error.getMessage();
			alog.error(msg);
			throw new Exception(msg,error);
		}
		
		cache.build();
	}
	
	
	@SuppressWarnings("deprecation")
	@Deprecated
	public static void disableSyncronizedGet() throws UtilsException {
		if(cache==null) {
			throw new UtilsException("Cache disabled");
		}
		cache.disableSyncronizedGet();
	}
	@SuppressWarnings("deprecation")
	@Deprecated
	public static boolean isDisableSyncronizedGet() throws UtilsException {
		if(cache==null) {
			throw new UtilsException("Cache disabled");
		}
		return cache.isDisableSyncronizedGet();
	}
	
	
	
	private static GestoreCacheResponseCaching staticInstance = null;
	public static synchronized void initialize() throws Exception{
		if(staticInstance==null){
			staticInstance = new GestoreCacheResponseCaching();
		}
	}
	public static GestoreCacheResponseCaching getInstance() throws Exception{
		if(staticInstance==null){
			throw new Exception("GestoreCacheResponseCaching non inizializzato");
		}
		return staticInstance;
	}
	
	private Logger log;
	
	public GestoreCacheResponseCaching() throws Exception{
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
	}
	

	
	
	
	
	
	
	
	/* ********************** ENGINE ************************** */
	
	public String save(String digest, ResponseCached response) throws Exception{
		
		String uuid = null;
		try{

			if(cache==null) {
				throw new Exception("Cache per il salvataggio delle risposte non abilitata");
			}
			
			if(digest == null)
				throw new Exception("Digest non definito");
			if(response == null)
				throw new Exception("Risposta non definita");
			
			String digestKey = formatKeyDigest(digest);
			
			// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
			Object o = cache.get(digestKey);
			if(o!=null) {
				org.openspcoop2.utils.cache.CacheResponse responseCache = 
						(org.openspcoop2.utils.cache.CacheResponse)  o;
				return ((ResponseCached)responseCache.getObject()).getUuid(); // already saved concurrent thread
			}
			
			//synchronized (cache) {
			lock_cache.acquire("save");
			try {
				
				o = cache.get(digestKey);
				if(o!=null) {
					org.openspcoop2.utils.cache.CacheResponse responseCache = 
							(org.openspcoop2.utils.cache.CacheResponse)  o;
					return ((ResponseCached)responseCache.getObject()).getUuid(); // already saved concurrent thread
				}
				
				uuid = UniqueIdentifierManager.newUniqueIdentifier().getAsString();
				String uuidKey = formatKeyUUID(uuid);
				
				try{	
					cache.put(uuidKey,digestKey);
				}catch(UtilsException e){
					this.log.error("Errore durante l'inserimento in cache con chiave ["+uuidKey+"] valore["+digestKey+"]: "+e.getMessage());
				}
				
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					response.setUuid(uuid);
					response.setDigest(digest);
					responseCache.setObject((java.io.Serializable)response);
					cache.put(digestKey,responseCache);
				}catch(UtilsException e){
					this.log.error("Errore durante l'inserimento in cache con chiave ["+digestKey+"] della risposta: "+e.getMessage());
					try {
						cache.remove(uuidKey); // precedentemente aggiunto
					}catch(Throwable tIgnore) {}
				}
				
			}finally {
				lock_cache.release("save");
			}

		}
		catch(Exception e){
			this.log.error(e.getMessage(),e);
			throw new Exception("Salvataggio in Cache fallito: "+e.getMessage(),e);
		}

		return uuid;
		
	}
	
	
	
	public ResponseCached readByUUID(String uuid) throws Exception{
		
		try{

			if(cache==null) {
				throw new Exception("Cache per il salvataggio delle risposte non abilitata");
			}
			
			if(uuid == null)
				throw new Exception("UUID non definito");

			String uuidKey = formatKeyUUID(uuid);
			
			// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
			
			Object oDigest = cache.get(uuidKey);
			if(oDigest==null) {
				return null; // messaggio non precedentemente salvato
			}
			
			String digest = (String) oDigest;
			String digestKey = formatKeyDigest(digest);
			
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) cache.get(digestKey);
			if(response == null || response.getObject()==null){
				// la rimozione la faccio in modalita' sincronizzata
			}
			else {
				ResponseCached responseCached = (ResponseCached) response.getObject();
				Date now = DateManager.getDate();
				if(now.after(responseCached.getScadenza())) {
					// la rimozione la faccio in modalita' sincronizzata
				}
				else {
					return responseCached;
				}
			}
			
			//synchronized (cache) {
			lock_cache.acquire("readByUUID");
			try {
				
				oDigest = cache.get(uuidKey);
				if(oDigest==null) {
					return null; // messaggio non precedentemente salvato
				}
				digest = (String) oDigest;
				digestKey = formatKeyDigest(digest);
			
				response = 
						(org.openspcoop2.utils.cache.CacheResponse) cache.get(digestKey);
				if(response == null || response.getObject()==null){
					this.log.error("In cache non è presente il mapping uuid ("+uuidKey+") - digest ("+digestKey+") ma poi non esiste l'oggetto??");
					cache.remove(uuidKey);
					return null;
				}
				
				ResponseCached responseCached = (ResponseCached) response.getObject();
				Date now = DateManager.getDate();
				if(now.after(responseCached.getScadenza())) {
					SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
					String scadenza = dateformat.format(responseCached.getScadenza());
					String nowS = dateformat.format(now);
					this.log.debug("In cache è presente un messaggio [uuid ("+uuidKey+") - digest ("+digestKey+")] scaduto (scadenza:"+scadenza+") (now:"+nowS+")");
					cache.remove(uuidKey);
					cache.remove(digestKey);
					return null;
				}
				
				return responseCached;
				
			}finally {
				lock_cache.release("readByUUID");
			}

		}
		catch(Exception e){
			this.log.error(e.getMessage(),e);
			throw new Exception("Lettura messaggio in Cache fallito: "+e.getMessage(),e);
		}
		
	}
	
	public void removeByUUID(String uuid) throws Exception{
		
		try{

			if(cache==null) {
				throw new Exception("Cache per il salvataggio delle risposte non abilitata");
			}
			
			if(uuid == null)
				throw new Exception("UUID non definito");

			String uuidKey = formatKeyUUID(uuid);
			
			// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
			
			Object oDigest = cache.get(uuidKey);
			if(oDigest==null) {
				return; // messaggio non precedentemente salvato
			}
						
			//synchronized (cache) {
			lock_cache.acquire("removeByUUID");
			try {
				
				oDigest = cache.get(uuidKey);
				if(oDigest==null) {
					return; // messaggio non precedentemente salvato
				}
				String digest = (String) oDigest;
				String digestKey = formatKeyDigest(digest);
			
				org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) cache.get(digestKey);
				if(response == null || response.getObject()==null){
					this.log.error("In cache non è presente il mapping uuid ("+uuidKey+") - digest ("+digestKey+") ma poi non esiste l'oggetto??");
					cache.remove(uuidKey);
					return;
				}
				
				cache.remove(uuidKey);
				cache.remove(digestKey);
				
			}finally{
				lock_cache.release("removeByUUID");
			}

		}
		catch(Exception e){
			this.log.error(e.getMessage(),e);
			throw new Exception("Rimozione messaggio in Cache fallito: "+e.getMessage(),e);
		}
		
	}
	
	public ResponseCached readByDigest(String digest) throws Exception{
		
		try{

			if(cache==null) {
				throw new Exception("Cache per il salvataggio delle risposte non abilitata");
			}
			
			if(digest == null)
				throw new Exception("Digest non definito");

			String digestKey = formatKeyDigest(digest);
				
			
			// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
			
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) cache.get(digestKey);
			if(response == null || response.getObject()==null){
				return null;
			}
			
			ResponseCached responseCached = (ResponseCached) response.getObject();
			Date now = DateManager.getDate();
			if(now.after(responseCached.getScadenza())) {
				// effettuo il controllo in modalita' sincronizzazione per fare le rimozioni
			}
			else {
				return responseCached;
			}
						
			//synchronized (cache) {
			lock_cache.acquire("readByDigest");
			try {
				
				response = 
						(org.openspcoop2.utils.cache.CacheResponse) cache.get(digestKey);
				if(response == null || response.getObject()==null){
					return null;
				}
				
				responseCached = (ResponseCached) response.getObject();
				now = DateManager.getDate();
				if(now.after(responseCached.getScadenza())) {
					String uuidKey = formatKeyUUID(responseCached.getUuid());
					SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
					String scadenza = dateformat.format(responseCached.getScadenza());
					String nowS = dateformat.format(now);
					this.log.debug("In cache è presente un messaggio [uuid ("+uuidKey+") - digest ("+digestKey+")] scaduto (scadenza:"+scadenza+") (now:"+nowS+")");
					cache.remove(uuidKey);
					cache.remove(digestKey);
					return null;
				}

				return responseCached;
				
			}finally {
				lock_cache.release("readByDigest");
			}

		}
		catch(Exception e){
			this.log.error(e.getMessage(),e);
			throw new Exception("Lettura messaggio in Cache fallito: "+e.getMessage(),e);
		}
		
	}
	
	
	public static String formatKeyUUID(String uuid) {
		String prefix = "uuid:";
		if(uuid.startsWith(prefix)==false) {
			return prefix+uuid;
		}
		else {
			return uuid;
		}
	}
	
	public static String formatKeyDigest(String digest) {
		String prefix = "digest:";
		if(digest.startsWith(prefix)==false) {
			return prefix+digest;
		}
		else {
			return digest;
		}
	}
}
