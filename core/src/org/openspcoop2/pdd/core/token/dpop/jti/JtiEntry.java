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

import java.util.Date;

/**
 * JtiEntry - Value object per cache Caffeine con TTL per-entry
 *
 * Contiene i dati necessari per calcolare expiration precisa di ogni JTI:
 * - iat: timestamp emissione DPoP
 * - toleranceMillis: tolerance totale (TTL policy + iat tolerance)
 *
 * Usato da Caffeine Expiry per calcolare TTL individuale:
 * expiration = iat + toleranceMillis
 *
 * Immutabile per thread-safety.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JtiEntry {

	private final Date iat;
	private final long toleranceMillis;

	/**
	 * Costruttore
	 *
	 * @param iat Issued At timestamp del DPoP token
	 * @param toleranceMillis Tolerance totale in millisecondi (TTL policy + iat tolerance)
	 */
	public JtiEntry(Date iat, long toleranceMillis) {
		this.iat = iat;
		this.toleranceMillis = toleranceMillis;
	}

	/**
	 * @return Issued At timestamp
	 */
	public Date getIat() {
		return this.iat;
	}

	/**
	 * @return Tolerance totale in millisecondi
	 */
	public long getToleranceMillis() {
		return this.toleranceMillis;
	}

	/**
	 * Calcola timestamp di scadenza assoluto
	 *
	 * @return iat + toleranceMillis in millisecondi
	 */
	public long getExpirationMillis() {
		if (this.iat == null) {
			// Fallback: scade dopo tolerance dalla creazione entry
			return System.currentTimeMillis() + this.toleranceMillis;
		}
		return this.iat.getTime() + this.toleranceMillis;
	}

}
