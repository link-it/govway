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

/**
 * TipoValidazioneJtiDPoP - Strategia di validazione JTI per prevenire replay attacks
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoValidazioneJtiDPoP {

	/**
	 * Validazione JTI disabilitata - nessun controllo replay
	 */
	DISABLED,

	/**
	 * Validazione JTI locale con Reject Policy - cache in-memory Caffeine per-policy
	 * Quando cache piena: blocca ulteriori richieste (fail-safe)
	 * Adatta per deployment single-node con traffico prevedibile
	 */
	LOCAL_REJECT,

	/**
	 * Validazione JTI locale con LRU Policy - cache in-memory Caffeine per-policy
	 * Quando cache piena: rimuove entry meno recenti (rischio replay se sotto-dimensionata)
	 * Adatta per deployment single-node quando si preferisce best-effort
	 */
	LOCAL_EVICT,

	/**
	 * Validazione JTI distribuita - storage Redis condiviso
	 * Nessun limite pratico di dimensione, TTL preciso per-entry
	 * Adatta per deployment cluster per protezione replay cross-node
	 */
	DISTRIBUTED

}
