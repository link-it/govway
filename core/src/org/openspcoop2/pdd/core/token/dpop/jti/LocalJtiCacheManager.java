/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.core.token.dpop.jti;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

/**
 * LocalJtiCacheManager - Gestisce registry di cache Caffeine dedicate per-policy
 *
 * Ogni policy ha la propria cache isolata per evitare interferenze tra policy diverse.
 * Cache configurate con TTL per-entry (expireAfter) e maxSize dimensionato per policy.
 *
 * TTL per-entry: ogni JTI scade esattamente a (iat + tolerance), garantendo precisione
 * come nel DistributedJtiValidator Redis.
 *
 * Thread-safe: usa ConcurrentHashMap per registry e computeIfAbsent per lazy initialization.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LocalJtiCacheManager {

	private LocalJtiCacheManager() {}

	private static final Logger logger = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();

	/**
	 * Registry: policyName -> cache Caffeine dedicata
	 * ConcurrentHashMap garantisce thread-safety per accesso concorrente
	 */
	private static final Map<String, Cache<String, JtiEntry>> cacheRegistry = new ConcurrentHashMap<>();

	/**
	 * Background scheduler per cleanup periodico delle entry scadute.
	 * Necessario perché Caffeine usa lazy expiration e estimatedSize() conta anche entry scadute.
	 * Un solo thread daemon per tutte le cache - overhead minimo.
	 */
	private static ScheduledExecutorService cleanupScheduler = null;
	private static final AtomicBoolean schedulerInitialized = new AtomicBoolean(false);

	/**
	 * Inizializza lo scheduler di cleanup se configurato (intervallo > 0).
	 * Thread-safe tramite AtomicBoolean: garantisce inizializzazione singola.
	 * Chiamato automaticamente alla prima creazione di cache.
	 */
	private static void initCleanupSchedulerIfNeeded() {
		if (schedulerInitialized.compareAndSet(false, true)) {
			try {
				int intervalSeconds = OpenSPCoop2Properties.getInstance().getGestioneTokenDPoPJtiLocalCacheCleanupIntervalSeconds();
				if (intervalSeconds > 0) {
					cleanupScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
						Thread t = new Thread(r, "JTI-Cache-Cleanup");
						t.setDaemon(true);
						return t;
					});
					cleanupScheduler.scheduleAtFixedRate(() -> {
						try {
							if (!cacheRegistry.isEmpty()) {
								cacheRegistry.values().forEach(Cache::cleanUp);
							}
						} catch (Exception e) {
							logger.error("Error during JTI cache cleanup: {}", e.getMessage(), e);
						}
					}, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
					logger.info("JTI cache cleanup scheduler started with interval {} seconds", intervalSeconds);
				} else {
					logger.info("JTI cache cleanup scheduler disabled (interval=0)");
				}
			} catch (Exception e) {
				logger.error("Failed to initialize JTI cache cleanup scheduler: {}", e.getMessage(), e);
				schedulerInitialized.set(false);
			}
		}
	}

	/**
	 * Ottiene o crea cache dedicata per la policy specificata
	 *
	 * Thread-safe tramite computeIfAbsent: garantisce inizializzazione singola anche con accesso concorrente.
	 *
	 * Cache configurata con TTL per-entry: ogni JTI scade esattamente a (entry.iat + entry.toleranceMillis).
	 * Garantisce precisione temporale identica al DistributedJtiValidator Redis.
	 *
	 * @param policyName Nome della policy (usato come chiave registry)
	 * @param maxSize Dimensione massima cache (da validazioneDPoPJtiMaxSize)
	 * @return Cache Caffeine dedicata per la policy
	 */
	public static Cache<String, JtiEntry> getOrCreateCache(String policyName, long maxSize) {
		initCleanupSchedulerIfNeeded();
		return cacheRegistry.computeIfAbsent(policyName, key -> {
			logger.info("Creating Caffeine JTI cache for policy [{}] with per-entry TTL, maxSize={}", policyName, maxSize);
			return Caffeine.newBuilder()
					.expireAfter(new Expiry<String, JtiEntry>() {
						@Override
						public long expireAfterCreate(String jti, JtiEntry entry, long currentTime) {
							// Calcola TTL preciso per questa entry: (iat + tolerance - now)
							long expirationMillis = entry.getExpirationMillis();
							long currentMillis = DateManager.getTimeMillis();
							long remainingMillis = expirationMillis - currentMillis;

							// Converti in nanoseconds per Caffeine (minimo 0)
							return TimeUnit.MILLISECONDS.toNanos(Math.max(0, remainingMillis));
						}

						@Override
						public long expireAfterUpdate(String jti, JtiEntry entry, long currentTime, long currentDuration) {
							// JTI non vengono mai aggiornati, solo creati
							return currentDuration;
						}

						@Override
						public long expireAfterRead(String jti, JtiEntry entry, long currentTime, long currentDuration) {
							// TTL non cambia su lettura (non è LRU temporale)
							return currentDuration;
						}
					})
					.maximumSize(maxSize)
					.recordStats() // Abilita statistiche per monitoring
					.build();
		});
	}

	/**
	 * Rimuove cache per policy specifica (es. durante riconfigurazione policy)
	 *
	 * @param policyName Nome della policy da rimuovere
	 */
	public static void removeCache(String policyName) {
		Cache<String, JtiEntry> removed = cacheRegistry.remove(policyName);
		if (removed != null) {
			removed.invalidateAll();
			removed.cleanUp();
			logger.info("Removed and cleaned up JTI cache for policy [{}]", policyName);
		}
	}

	/**
	 * Shutdown globale: ferma scheduler, pulisce tutte le cache e svuota registry
	 * Chiamato durante shutdown applicazione
	 */
	public static void shutdownAll() {
		logger.info("Shutting down all JTI caches ({} policies)", cacheRegistry.size());
		if (cleanupScheduler != null) {
			cleanupScheduler.shutdownNow();
			cleanupScheduler = null;
			schedulerInitialized.set(false);
		}
		cacheRegistry.forEach((policyName, cache) -> {
			cache.invalidateAll();
			cache.cleanUp();
		});
		cacheRegistry.clear();
	}

	/**
	 * Ottiene statistiche aggregate di tutte le cache (per monitoring/diagnostics)
	 *
	 * @return Mappa policyName -> CacheStats
	 */
	public static Map<String, CacheStats> getAllStats() {
		Map<String, CacheStats> stats = new ConcurrentHashMap<>();
		cacheRegistry.forEach((policyName, cache) -> stats.put(policyName, cache.stats()));
		return stats;
	}

	/**
	 * Verifica se esiste cache per policy specifica
	 *
	 * @param policyName Nome della policy
	 * @return true se cache già inizializzata
	 */
	public static boolean hasCacheForPolicy(String policyName) {
		return cacheRegistry.containsKey(policyName);
	}

	/**
	 * Ottiene cache per policy specifica (per debug/monitoring)
	 *
	 * @param policyName Nome della policy
	 * @return Cache se esiste, null altrimenti
	 */
	public static Cache<String, JtiEntry> getCache(String policyName) {
		return cacheRegistry.get(policyName);
	}

}
