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

import org.openspcoop2.pdd.core.token.TokenException;
import org.openspcoop2.pdd.core.token.parser.IDPoPParser;
import org.openspcoop2.utils.UtilsException;

/**
 * IJtiValidator - Interfaccia per validatori JTI DPoP con diverse strategie di storage
 *
 * Implementa pattern Strategy per permettere validazione JTI con backend intercambiabili:
 * - Disabled: nessuna validazione
 * - Local: cache in-memory Caffeine per-policy
 * - Distributed: storage Redis condiviso
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IJtiValidator {

	/**
	 * Valida e memorizza il JTI per prevenire replay attacks
	 *
	 * @param policyName Nome della policy (per namespace/isolation)
	 * @param jti JTI claim da validare
	 * @param dpopParser Parser DPoP contenente iat e altri claim
	 * @param toleranceMillis Tolerance totale in milliseconds (iat tolerance + future tolerance)
	 * @throws TokenException Se JTI già utilizzato (replay attack detected)
	 * @throws UtilsException Per errori tecnici (Redis down, cache full, etc.)
	 */
	void validateAndStore(String policyName, String jti, IDPoPParser dpopParser,
			long toleranceMillis) throws TokenException, UtilsException;

	/**
	 * Verifica disponibilità del validatore
	 *
	 * @return true se il validatore è disponibile e funzionante, false altrimenti
	 *         (es. Redis disconnesso per distributed validator)
	 */
	boolean isAvailable();

}
