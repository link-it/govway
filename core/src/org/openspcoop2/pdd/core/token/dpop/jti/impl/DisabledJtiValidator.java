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

import org.openspcoop2.pdd.core.token.TokenException;
import org.openspcoop2.pdd.core.token.dpop.jti.IJtiValidator;
import org.openspcoop2.pdd.core.token.parser.IDPoPParser;
import org.openspcoop2.utils.UtilsException;

/**
 * DisabledJtiValidator - Implementazione no-op che disabilita la validazione JTI
 *
 * Usato quando l'utente configura validazioneDPoPJtiValidation=disabled.
 * Non effettua alcun controllo replay attack.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DisabledJtiValidator implements IJtiValidator {

	@Override
	public void validateAndStore(String policyName, String jti, IDPoPParser dpopParser,
			long toleranceMillis) throws TokenException, UtilsException {
		// No validation - JTI non verificato
		// Replay attacks NON prevenuti
	}

	@Override
	public boolean isAvailable() {
		return true; // Sempre disponibile (non fa nulla)
	}

}
