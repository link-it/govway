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

import org.openspcoop2.pdd.core.token.PolicyGestioneToken;
import org.openspcoop2.pdd.core.token.TokenException;
import org.openspcoop2.pdd.core.token.dpop.jti.impl.DisabledJtiValidator;
import org.openspcoop2.pdd.core.token.dpop.jti.impl.DistributedJtiValidator;
import org.openspcoop2.pdd.core.token.dpop.jti.impl.LocalJtiValidator;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.slf4j.Logger;

/**
 * JtiValidatorFactory - Factory per creare IJtiValidator basato su configurazione policy
 *
 * Implementa pattern Strategy Factory per istanziare il validator appropriato:
 * - DISABLED -> DisabledJtiValidator (no-op)
 * - LOCAL -> LocalJtiValidator (Caffeine in-memory)
 * - DISTRIBUTED -> DistributedJtiValidator (Redis condiviso)
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JtiValidatorFactory {

	private static final Logger logger = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();

	private JtiValidatorFactory() {}

	/**
	 * Crea validator basato su configurazione policy
	 *
	 * @param policy PolicyGestioneToken contenente configurazione JTI
	 * @return IJtiValidator istanza appropriata per la strategia configurata
	 * @throws TokenException Se configurazione invalida
	 */
	public static IJtiValidator createValidator(PolicyGestioneToken policy) throws TokenException {

		if (policy == null) {
			throw new TokenException("PolicyGestioneToken is null - cannot create JTI validator");
		}

		// Leggi tipo validazione JTI dalla policy
		String jtiValidationType = policy.getValidazioneDPoPJtiValidation();
		if (jtiValidationType == null || jtiValidationType.trim().isEmpty()) {
			// Default: DISABLED se non configurato
			logger.debug("JTI validation type not configured - defaulting to DISABLED");
			jtiValidationType = TipoValidazioneJtiDPoP.DISABLED.name();
		}

		// Parse enum
		TipoValidazioneJtiDPoP tipo;
		try {
			tipo = TipoValidazioneJtiDPoP.valueOf(jtiValidationType.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new TokenException("Invalid JTI validation type: ["+jtiValidationType+"]. Valid values: DISABLED, LOCAL_REJECT, LOCAL_EVICT, DISTRIBUTED",e);
		}

		// Crea validator appropriato
		switch (tipo) {

		case DISABLED:
			logger.info("Creating DisabledJtiValidator for policy [{}]", policy.getName());
			return new DisabledJtiValidator();

		case LOCAL_REJECT:
			return createLocalValidator(policy, true); // failOnFull=true (Reject Policy)

		case LOCAL_EVICT:
			return createLocalValidator(policy, false); // failOnFull=false (LRU Policy)

		case DISTRIBUTED:
			logger.info("Creating DistributedJtiValidator for policy [{}]", policy.getName());
			return new DistributedJtiValidator();

		default:
			throw new TokenException("Unsupported JTI validation type: "+tipo);
		}
	}

	/**
	 * Crea LocalJtiValidator con configurazione estratta dalla policy
	 *
	 * TTL non più necessario: ogni entry ha TTL individuale calcolato da (iat + tolerance)
	 * come nel DistributedJtiValidator Redis.
	 *
	 * @param policy PolicyGestioneToken contenente configurazione
	 * @param failOnFull true per Reject Policy, false per LRU Policy
	 */
	private static LocalJtiValidator createLocalValidator(PolicyGestioneToken policy, boolean failOnFull) throws TokenException {

		// Leggi maxSize da validazioneDPoPJtiMaxSize (obbligatorio, nessun fallback)
		Long maxSize = policy.getValidazioneDPoPJtiMaxSize();
		if (maxSize == null || maxSize <= 0) {
			throw new TokenException("validazioneDPoPJtiMaxSize must be configured and > 0 for local JTI validation (policy: "+policy.getName()+")");
		}

		String policyType = failOnFull ? "Reject Policy" : "LRU Policy";
		logger.info("Creating LocalJtiValidator ({}) for policy [{}] with per-entry TTL, maxSize={}", policyType, policy.getName(), maxSize);
		return new LocalJtiValidator(maxSize, failOnFull);
	}

}
