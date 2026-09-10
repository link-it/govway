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
 * Strategia di selezione dei tool richiesta dal client. Naming canonical allineato ad
 * Anthropic; i trasformatori outbound mappano sui valori dei singoli provider
 * (OpenAI {@code none/auto/required/function}, Bedrock {@code auto/any/tool}).
 *
 * @author Andrea Poli (apoli@link.it)
 */
public enum CanonicalToolChoiceMode {

	/** Il modello decide se e quale tool invocare. */
	AUTO("auto"),
	/** Il modello deve invocare uno qualsiasi dei tool dichiarati. */
	ANY("any"),
	/** Il modello deve invocare il tool indicato dal nome. */
	TOOL("tool"),
	/** Il modello non deve invocare tool. */
	NONE("none");

	private final String value;

	CanonicalToolChoiceMode(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return this.value;
	}

	/** Variante lenient: ritorna null per valori sconosciuti. */
	@JsonCreator
	public static CanonicalToolChoiceMode tryFromValue(String value) {
		if (value == null) {
			return null;
		}
		for (CanonicalToolChoiceMode m : values()) {
			if (m.value.equals(value)) {
				return m;
			}
		}
		return null;
	}
}
