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
package org.openspcoop2.message.llm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Ruolo di un messaggio nella conversazione canonical.
 * Allineato al modello Anthropic Messages: 'user' e 'assistant'.
 * Il system prompt vive come campo top-level di CanonicalChatRequest, non come ruolo del messaggio.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public enum CanonicalRole {

	USER("user"),
	ASSISTANT("assistant");

	private final String value;

	CanonicalRole(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return this.value;
	}

	@JsonCreator
	public static CanonicalRole fromValue(String value) {
		if (value == null) {
			return null;
		}
		for (CanonicalRole r : values()) {
			if (r.value.equals(value)) {
				return r;
			}
		}
		throw new IllegalArgumentException("CanonicalRole sconosciuto: " + value);
	}
}
