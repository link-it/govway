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

/**
 * Voce della cache LLM ({@link GestoreCacheLlm}) con scadenza assoluta per-elemento.
 *
 * <p>La guardia globale (dimensione max/LRU/idle) è gestita dalla cache JCS; la durata
 * assoluta del singolo elemento è invece portata qui e verificata ad ogni get, così ogni
 * chiamante (es. il vault PII di sessione) può imporre il proprio TTL (dal LLM Provider
 * Binding) indipendentemente dalla config globale della cache.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LlmCacheEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Serializable value;
	/** epoch millis di scadenza assoluta; {@code <=0} significa nessuna scadenza per-elemento */
	private final long expiresAtMillis;

	public LlmCacheEntry(Serializable value, long expiresAtMillis) {
		this.value = value;
		this.expiresAtMillis = expiresAtMillis;
	}

	public Serializable getValue() {
		return this.value;
	}

	public long getExpiresAtMillis() {
		return this.expiresAtMillis;
	}

	public boolean isExpired() {
		return this.expiresAtMillis > 0 && System.currentTimeMillis() > this.expiresAtMillis;
	}
}
