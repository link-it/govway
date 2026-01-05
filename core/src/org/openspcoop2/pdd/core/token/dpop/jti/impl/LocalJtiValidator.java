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

package org.openspcoop2.pdd.core.token.dpop.jti.impl;

import java.util.Date;

import org.openspcoop2.pdd.core.token.TokenException;
import org.openspcoop2.pdd.core.token.dpop.jti.IJtiValidator;
import org.openspcoop2.pdd.core.token.dpop.jti.JtiEntry;
import org.openspcoop2.pdd.core.token.dpop.jti.LocalJtiCacheManager;
import org.openspcoop2.pdd.core.token.parser.IDPoPParser;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

import com.github.benmanes.caffeine.cache.Cache;

/**
 * LocalJtiValidator - Validatore JTI con cache in-memory Caffeine per-policy
 *
 * Strategia:
 * - Una cache Caffeine dedicata per ogni policy (via LocalJtiCacheManager)
 * - TTL per-entry: ogni JTI scade esattamente a (iat + tolerance) come Redis
 * - MaxSize dimensionato per policy: (req/sec) × TTL × safety_margin
 * - Chiave cache: solo JTI (no prefix policy - cache già dedicata)
 * - Valore: JtiEntry (contiene iat + tolerance per calcolo TTL preciso)
 *
 * TTL preciso garantisce:
 * - No over-retention (entry scade quando DPoP non più valido)
 * - No falsi positivi replay attack (entry scadute non presenti)
 * - Coerenza con DistributedJtiValidator (Redis)
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LocalJtiValidator implements IJtiValidator {

	private static final Logger logger = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();

	private final long maxSize;
	private final boolean failOnFull;

	/**
	 * Costruttore
	 *
	 * @param maxSize Dimensione massima cache (da validazioneDPoPJtiMaxSize)
	 * @param failOnFull true per Reject Policy (blocca richieste se piena), false per LRU Policy (evict automatico)
	 */
	public LocalJtiValidator(long maxSize, boolean failOnFull) {
		this.maxSize = maxSize;
		this.failOnFull = failOnFull;
		String policy = failOnFull ? "Reject Policy" : "LRU Policy";
		logger.debug("LocalJtiValidator initialized with maxSize={}, policy={}", maxSize, policy);
	}

	@Override
	public void validateAndStore(String policyName, String jti, IDPoPParser dpopParser,
			long toleranceMillis) throws TokenException, UtilsException {

		// Lazy initialization: ottiene o crea cache per questa policy
		Cache<String, JtiEntry> cache = LocalJtiCacheManager.getOrCreateCache(policyName, this.maxSize);

		// Check replay: se JTI già presente -> replay attack
		JtiEntry alreadyUsed = cache.getIfPresent(jti);
		if (alreadyUsed != null) {
			// Replay attack detected
			logger.warn("DPoP replay attack detected for policy [{}]: JTI [{}] already used", policyName, jti);
			throw new TokenException("DPoP JTI ["+jti+"] has already been used (replay attack detected)");
		}

		// Check capacità cache SE Reject Policy attiva
		if (this.failOnFull) {
			long currentSize = cache.estimatedSize();
			if (currentSize >= this.maxSize) {
				logger.error("JTI cache full for policy [{}]: size={}/{} - rejecting JTI [{}]", policyName, currentSize, this.maxSize, jti);
				throw new TokenException("DPoP JTI validation failed: local cache capacity exceeded ("+currentSize+"/"+this.maxSize+" entries). Consider increasing cache size or switching to distributed validation");
			}
		}
		// ALTRIMENTI (LRU Policy): Caffeine fa eviction automatica, nessun check necessario

		// Validazione temporale: verifica che iat + toleranceMillis non sia scaduto
		// Nota: iat validation già fatta in validazioneDPoPIat(), qui ulteriore check per coerenza TTL
		Date iat = dpopParser.getIssuedAt();
		if (iat != null) {
			long iatMillis = iat.getTime();
			long currentMillis = DateManager.getTimeMillis();
			long expirationMillis = iatMillis + toleranceMillis;

			if (currentMillis > expirationMillis) {
				// DPoP già scaduto - non ha senso memorizzarlo
				logger.debug("DPoP with JTI [{}] already expired (iat={}, expiration={}, now={}) - not stored", jti, iatMillis, expirationMillis, currentMillis);
				throw new TokenException("DPoP token expired: iat ["+iat+"] + tolerance ["+toleranceMillis+"ms] exceeded");
			}
		}

		// Store JTI con entry contenente iat + tolerance per TTL preciso
		JtiEntry entry = new JtiEntry(iat, toleranceMillis);
		cache.put(jti, entry);
		logger.debug("DPoP JTI [{}] stored successfully for policy [{}] (expires at {}ms)", jti, policyName, entry.getExpirationMillis());
	}

	@Override
	public boolean isAvailable() {
		// Cache in-memory sempre disponibile (non dipende da risorse esterne)
		return true;
	}

}
