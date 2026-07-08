/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.llm.cache;

import java.io.Serializable;
import java.util.function.Supplier;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.utils.Semaphore;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.cache.CacheType;
import org.slf4j.Logger;

/**
 * Gestore della cache generica dei dati LLM (region JCS {@code llmCache}).
 *
 * <p>Cache DB-backed modellata sulla "Cache (Risposte)" ma <b>senza life-time globale</b>:
 * la durata assoluta di ogni elemento è per-elemento (vedi {@link LlmCacheEntry}), tipicamente
 * imposta dal LLM Provider Binding (es. {@code piiSessionTtlSeconds} per il vault PII di
 * sessione). La config globale fornisce solo dimensione max, algoritmo (LRU/MRU) e idle-time
 * come guardia di memoria.
 *
 * <p>È volutamente generica (chiave String → valore Serializable) per poter ospitare, oltre al
 * vault PII di sessione, eventuali futuri dati LLM. I diversi usi devono adottare chiavi
 * namespaced (es. {@code piiVault:<sessionId>}).
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class GestoreCacheLlm {

	private GestoreCacheLlm() {}

	/** Nome della region JCS (vedi govway.jcs.properties) */
	private static final String LLM_CACHE_NAME = "llmCache";

	private static Cache cache = null;
	private static final Semaphore lockCache = new Semaphore("GestoreCacheLlm");

	/* --------------- Stato Cache --------------------*/

	public static boolean isCacheAbilitata() {
		return cache != null;
	}

	public static void resetCache() throws UtilsException {
		if (cache != null) {
			cache.clear();
		}
	}

	public static String printStatsCache(String separator) throws UtilsException {
		if (cache == null) {
			throw new UtilsException("Cache non abilitata");
		}
		return cache.printStats(separator);
	}

	public static String listKeysCache(String separator) throws UtilsException {
		if (cache == null) {
			throw new UtilsException("Cache non abilitata");
		}
		return cache.printKeys(separator);
	}

	public static String getObjectCache(String key) throws UtilsException {
		if (cache == null) {
			throw new UtilsException("Cache non abilitata");
		}
		Object o = cache.get(key);
		return (o != null) ? o.toString() : ("oggetto con chiave [" + key + "] non presente");
	}

	public static void removeObjectCache(String key) throws UtilsException {
		if (cache == null) {
			throw new UtilsException("Cache non abilitata");
		}
		cache.remove(key);
	}

	/* --------------- Abilitazione/Disabilitazione (anche via JMX) --------------------*/

	public static void abilitaCache() throws UtilsException {
		if (cache != null) {
			throw new UtilsException("Cache già abilitata");
		}
		cache = new Cache(CacheType.JCS, LLM_CACHE_NAME);
		// Nessun life-time globale: la scadenza assoluta è per-elemento (LlmCacheEntry).
		cache.setItemLifeTime(-1);
		cache.build();
	}

	public static void abilitaCache(Long dimensioneCache, Boolean algoritmoCacheLRU, Long itemIdleTime, Logger log) throws UtilsException {
		if (cache != null) {
			throw new UtilsException("Cache già abilitata");
		}
		int dimensione = (dimensioneCache != null) ? dimensioneCache.intValue() : -1;
		initCache(CacheType.JCS, dimensione, (algoritmoCacheLRU == null || algoritmoCacheLRU.booleanValue()), itemIdleTime, log);
	}

	public static void disabilitaCache() throws UtilsException {
		if (cache == null) {
			throw new UtilsException("Cache già disabilitata");
		}
		cache.clear();
		cache = null;
	}

	/* --------------- Inizializzazione da configurazione --------------------*/

	/** Inizializzazione senza cache abilitata (default: la cache LLM resta disabilitata). */
	public static void initialize(Logger log) {
		if (log != null && log.isDebugEnabled()) {
			log.debug("Cache LLM non abilitata");
		}
	}

	public static void initialize(CacheType cacheType, int dimensioneCache, String algoritmoCache, long idleTime, Logger log) throws UtilsException {
		boolean lru = (algoritmoCache == null) || CostantiConfigurazione.CACHE_LRU.toString().equalsIgnoreCase(algoritmoCache);
		initCache(cacheType, dimensioneCache, lru, (idleTime > 0 ? idleTime : null), log);
	}

	private static synchronized void initCache(CacheType cacheType, Integer dimensioneCache, boolean algoritmoCacheLRU, Long itemIdleTime, Logger alog) throws UtilsException {

		cache = new Cache(cacheType != null ? cacheType : CacheType.JCS, LLM_CACHE_NAME);

		if (dimensioneCache != null && dimensioneCache > 0) {
			if (alog != null) {
				alog.info("Dimensione della cache (LLM) impostata al valore: {}", dimensioneCache);
			}
			cache.setCacheSize(dimensioneCache);
		}

		if (alog != null) {
			alog.info("Algoritmo di cache (LLM) impostato al valore: {}", algoritmoCacheLRU ? "LRU" : "MRU");
		}
		cache.setCacheAlgoritm(algoritmoCacheLRU ? CacheAlgorithm.LRU : CacheAlgorithm.MRU);

		if (itemIdleTime != null && itemIdleTime > 0) {
			if (alog != null) {
				alog.info("Attributo 'IdleTime' (LLM) impostato al valore: {}", itemIdleTime);
			}
			cache.setItemIdleTime(itemIdleTime);
		}

		// Nessun life-time globale: forziamo 'eternal' a livello JCS (indipendente dai default di region),
		// la scadenza assoluta di ogni elemento è per-elemento (LlmCacheEntry, dal TTL del binding).
		cache.setItemLifeTime(-1);

		cache.build();
	}

	/* ********************** ENGINE (get/put per-elemento con TTL) ************************** */

	/**
	 * Inserisce un valore con TTL assoluto per-elemento.
	 * @param ttlSeconds durata assoluta in secondi; {@code <=0} = nessuna scadenza per-elemento.
	 */
	public static void put(String key, Serializable value, long ttlSeconds) throws UtilsException {
		if (cache == null) {
			throw new UtilsException("Cache LLM non abilitata");
		}
		if (key == null) {
			throw new UtilsException("Chiave non definita");
		}
		long expiresAt = (ttlSeconds > 0) ? (System.currentTimeMillis() + (ttlSeconds * 1000L)) : 0L;
		cache.put(key, new LlmCacheEntry(value, expiresAt));
	}

	/**
	 * Restituisce il valore associato alla chiave se presente e non scaduto (per-elemento),
	 * altrimenti {@code null} (rimuovendo l'eventuale voce scaduta). Se la cache è disabilitata
	 * ritorna {@code null} (fallback silenzioso).
	 */
	public static Serializable get(String key) {
		if (cache == null || key == null) {
			return null;
		}
		Object o = cache.get(key);
		if (o instanceof LlmCacheEntry) {
			LlmCacheEntry entry = (LlmCacheEntry) o;
			if (entry.isExpired()) {
				try {
					cache.remove(key);
				} catch (UtilsException e) {
					// best effort
				}
				return null;
			}
			return entry.getValue();
		}
		return null;
	}

	public static void remove(String key) {
		if (cache == null || key == null) {
			return;
		}
		try {
			cache.remove(key);
		} catch (UtilsException e) {
			// best effort
		}
	}

	/**
	 * Recupera il valore associato alla chiave, creandolo (via {@code factory}) e inserendolo in
	 * cache con TTL assoluto per-elemento se assente. Usa il pattern GovWay: get fuori dal semaforo,
	 * poi (se miss) get dentro al semaforo e infine add in cache, cosi' richieste concorrenti sulla
	 * stessa chiave condividono la stessa istanza. Se la cache e' disabilitata restituisce comunque
	 * un nuovo valore dalla factory (fallback per-transazione, nessun inserimento).
	 */
	public static Serializable getOrCreate(String key, long ttlSeconds, Supplier<Serializable> factory) throws UtilsException {
		if (cache == null || key == null) {
			return (factory != null) ? factory.get() : null;
		}
		// get fuori dal semaforo
		Serializable v = get(key);
		if (v != null) {
			return v;
		}
		SemaphoreLock lock = lockCache.acquire("getOrCreate");
		try {
			// get dentro al semaforo (un altro thread potrebbe averlo creato nel frattempo)
			v = get(key);
			if (v != null) {
				return v;
			}
			// add in cache
			v = (factory != null) ? factory.get() : null;
			if (v != null) {
				put(key, v, ttlSeconds);
			}
			return v;
		} finally {
			lockCache.release(lock, "getOrCreate");
		}
	}
}
