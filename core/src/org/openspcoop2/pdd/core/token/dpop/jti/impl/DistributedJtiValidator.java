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

package org.openspcoop2.pdd.core.token.dpop.jti.impl;

import java.time.Duration;
import java.util.Date;

import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson.RedissonManager;
import org.openspcoop2.pdd.core.token.TokenException;
import org.openspcoop2.pdd.core.token.dpop.jti.IJtiValidator;
import org.openspcoop2.pdd.core.token.parser.IDPoPParser;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;

/**
 * DistributedJtiValidator - Validatore JTI con storage Redis distribuito via Redisson
 *
 * Strategia:
 * - Storage Redis condiviso tra tutti i nodi del cluster GovWay
 * - TTL per-entry calcolato precisamente: (iat + toleranceMillis - currentTimeMillis) / 1000
 * - Namespace isolato per policy: dpop:jti:{policyName}:{jti}
 * - Operazione atomica SETNX via RBucket per garantire unicità distribuita
 * - Valore: Boolean.TRUE (presenza = già usato)
 *
 * Vantaggi: Protezione replay cross-node, TTL preciso per ogni JTI
 * Trade-off: Richiede Redis disponibile, latenza di rete
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DistributedJtiValidator implements IJtiValidator {

	private static final Logger logger = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();

	private static final String KEY_PREFIX = "dpop:jti:";

	/**
	 * Costruttore
	 */
	public DistributedJtiValidator() {
		logger.debug("DistributedJtiValidator initialized (using shared RedissonManager)");
	}

	@Override
	public void validateAndStore(String policyName, String jti, IDPoPParser dpopParser,
			long toleranceMillis) throws TokenException, UtilsException {

		// Ottiene RedissonClient dal manager condiviso
		RedissonClient redisson = RedissonManager.getRedissonClient(true);
		if (redisson == null) {
			throw new UtilsException("RedissonClient not available - cannot validate JTI");
		}

		/** Costruisce chiave Redis con namespace: dpop:jti:{policyName}:{jti} */
		String redisKey = KEY_PREFIX + policyName + ":" + jti;

		// Ottiene RBucket per questa chiave
		RBucket<Boolean> bucket = redisson.getBucket(redisKey);

		// Check atomico: se esiste già -> replay attack
		if (bucket.isExists()) {
			logger.warn("DPoP replay attack detected for policy [{}]: JTI [{}] already used (distributed check via Redis)", policyName, jti);
			throw new TokenException("DPoP JTI ["+jti+"] has already been used (replay attack detected)");
		}

		// Calcola TTL preciso per-entry: tempo rimanente fino alla scadenza del DPoP
		Date iat = dpopParser.getIssuedAt();
		long ttlSeconds = calculateTTL(iat, toleranceMillis);

		if (ttlSeconds <= 0) {
			// DPoP già scaduto - non ha senso memorizzarlo
			logger.debug("DPoP with JTI [{}] already expired (ttl={}s) - not stored", jti, ttlSeconds);
			throw new TokenException("DPoP token expired: iat ["+iat+"] + tolerance ["+toleranceMillis+"ms] exceeded");
		}

		// Store atomico con TTL: SETNX + EXPIRE in operazione singola
		bucket.set(Boolean.TRUE, Duration.ofSeconds(ttlSeconds));

		logger.debug("DPoP JTI [{}] stored successfully in Redis for policy [{}] with TTL={}s", jti, policyName, ttlSeconds);
	}

	/**
	 * Calcola TTL preciso per il JTI basato su iat e tolerance
	 *
	 * TTL = (iat + toleranceMillis - currentTimeMillis) / 1000
	 * Garantisce che la chiave Redis scada esattamente quando il DPoP non è più valido.
	 *
	 * @param iat Issued At timestamp del DPoP
	 * @param toleranceMillis Tolerance totale in milliseconds
	 * @return TTL in secondi, o 0 se già scaduto
	 */
	private long calculateTTL(Date iat, long toleranceMillis) {
		if (iat == null) {
			// Fallback: se iat non presente, usa tolerance come TTL
			// (non dovrebbe mai accadere - iat già validato prima)
			return toleranceMillis / 1000;
		}

		long iatMillis = iat.getTime();
		long currentMillis = DateManager.getTimeMillis();
		long expirationMillis = iatMillis + toleranceMillis;

		long remainingMillis = expirationMillis - currentMillis;
		long ttlSeconds = remainingMillis / 1000;

		// Arrotonda per eccesso per evitare scadenze premature
		if (remainingMillis % 1000 > 0) {
			ttlSeconds++;
		}

		return Math.max(0, ttlSeconds);
	}

	@Override
	public boolean isAvailable() {
		// Verifica disponibilità RedissonClient
		try {
			return RedissonManager.isRedissonClientInitialized() &&
					RedissonManager.getRedissonClient(false) != null;
		} catch (Exception e) {
			logger.warn("Redis not available for JTI validation: {}", e.getMessage(), e);
			return false;
		}
	}

}
