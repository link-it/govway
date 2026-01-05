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
	private static final Object schedulerLock = new Object();

	/**
	 * Inizializza lo scheduler di cleanup se configurato (intervallo > 0).
	 * Thread-safe tramite sincronizzazione su schedulerLock.
	 * Chiamato automaticamente alla prima creazione di cache.
	 */
	private static void initCleanupSchedulerIfNeeded() {
		if (!schedulerInitialized.get()) {
			synchronized (schedulerLock) {
				if (schedulerInitialized.compareAndSet(false, true)) {
					initCleanupScheduler();
				}
			}
		}
	}
	private static void initCleanupScheduler() {
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
		synchronized (schedulerLock) {
			if (cleanupScheduler != null) {
				cleanupScheduler.shutdownNow();
				cleanupScheduler = null;
				schedulerInitialized.set(false);
			}
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

	/* ********** JMX Statistics Methods ********** */

	/**
	 * Lista i nomi delle policy per cui è attiva una cache DPoP JTI
	 *
	 * @param separator Separatore tra i nomi delle policy
	 * @return Lista dei nomi delle policy, separati dal separatore specificato
	 */
	public static String listPolicyNames(String separator) {
		if (cacheRegistry.isEmpty()) {
			return "Nessuna cache DPoP JTI attiva";
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String policyName : cacheRegistry.keySet()) {
			if (!first) {
				sb.append(separator);
			}
			sb.append(policyName);
			first = false;
		}
		return sb.toString();
	}

	/**
	 * Stima della memoria RAM occupata per ogni entry nella cache DPoP JTI.
	 *
	 * La stima di ~200 bytes per entry è calcolata come segue:
	 *
	 * 1. Chiave (String JTI, tipicamente UUID di 36 caratteri):
	 *    - char array: 36 * 2 bytes = 72 bytes
	 *    - String object overhead: ~24 bytes
	 *    - HashMap.Entry overhead: ~32 bytes
	 *    Subtotale chiave: ~128 bytes
	 *
	 * 2. Valore (JtiEntry):
	 *    - Date iat: ~24 bytes (object header + long internal)
	 *    - long toleranceMillis: 8 bytes
	 *    - Object overhead JtiEntry: ~16 bytes
	 *    Subtotale valore: ~48 bytes
	 *
	 * 3. Overhead Caffeine (reference, metadata):
	 *    - ~24 bytes per entry
	 *
	 * Totale stimato: ~200 bytes per entry
	 */
	private static final long ESTIMATED_BYTES_PER_JTI_ENTRY = 200L;

	/**
	 * Genera report statistiche per una singola policy
	 *
	 * @param policyName Nome della policy
	 * @return Report formattato con statistiche cache
	 */
	public static String printStatsForPolicy(String policyName) {
		if (policyName == null || policyName.isEmpty()) {
			return "Nome policy non specificato";
		}

		Cache<String, JtiEntry> cache = cacheRegistry.get(policyName);
		if (cache == null) {
			return "Cache DPoP JTI per la policy '" + policyName + "' non trovata o non inizializzata";
		}

		return formatCacheStats(policyName, cache, false);
	}

	/**
	 * Genera report statistiche aggregate di tutte le policy
	 *
	 * @return Report formattato con statistiche di tutte le cache
	 */
	public static String printStatsAllPolicies() {
		if (cacheRegistry.isEmpty()) {
			return "Nessuna cache DPoP JTI attiva";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("=== Statistiche Cache DPoP JTI (anti-replay) ===\n");
		sb.append("Numero policy attive: ").append(cacheRegistry.size()).append("\n\n");

		long totalEntries = 0;
		long totalEstimatedBytes = 0;

		for (Map.Entry<String, Cache<String, JtiEntry>> entry : cacheRegistry.entrySet()) {
			String name = entry.getKey();
			Cache<String, JtiEntry> cache = entry.getValue();

			sb.append(formatCacheStats(name, cache, true));
			sb.append("\n");

			long size = cache.estimatedSize();
			totalEntries += size;
			totalEstimatedBytes += size * ESTIMATED_BYTES_PER_JTI_ENTRY;
		}

		sb.append("=== Totale Globale ===\n");
		sb.append("Entries totali: ").append(totalEntries).append("\n");
		sb.append("RAM stimata totale: ").append(formatBytes(totalEstimatedBytes)).append("\n");

		return sb.toString();
	}

	private static String formatCacheStats(String policyName, Cache<String, JtiEntry> cache, boolean appendPolicyName) {
		CacheStats stats = cache.stats();
		long estimatedSize = cache.estimatedSize();
		long estimatedBytes = estimatedSize * ESTIMATED_BYTES_PER_JTI_ENTRY;

		StringBuilder sb = new StringBuilder();
		if(appendPolicyName) {
			sb.append("--- Policy: ").append(policyName).append(" ---\n");
		}
		sb.append("Entries: ").append(estimatedSize).append("\n");
		sb.append("RAM stimata: ").append(formatBytes(estimatedBytes));
		sb.append(" (~").append(ESTIMATED_BYTES_PER_JTI_ENTRY).append(" bytes/entry)\n");
		sb.append("Hit count: ").append(stats.hitCount()).append("\n");
		sb.append("Miss count: ").append(stats.missCount()).append("\n");
		sb.append("Hit rate: ").append(String.format("%.2f%%", stats.hitRate() * 100)).append("\n");
		sb.append("Eviction count: ").append(stats.evictionCount()).append("\n");
		sb.append("Load success count: ").append(stats.loadSuccessCount()).append("\n");
		sb.append("Load failure count: ").append(stats.loadFailureCount()).append("\n");

		return sb.toString();
	}

	private static String formatBytes(long bytes) {
		if (bytes < 1024) {
			return bytes + " B";
		} else if (bytes < 1024 * 1024) {
			return String.format("%.2f KB", bytes / 1024.0);
		} else if (bytes < 1024L * 1024 * 1024) {
			return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
		} else {
			return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
		}
	}

}
