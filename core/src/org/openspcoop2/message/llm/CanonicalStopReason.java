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
 * Motivo di terminazione della generazione, normalizzato sul vocabolario Anthropic.
 * I trasformatori outbound mappano sul valore equivalente del dialetto richiesto:
 * <ul>
 *   <li>END_TURN     → OpenAI "stop"</li>
 *   <li>MAX_TOKENS   → OpenAI "length"</li>
 *   <li>STOP_SEQUENCE→ OpenAI "stop"</li>
 *   <li>TOOL_USE     → OpenAI "tool_calls"</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public enum CanonicalStopReason {

	END_TURN("end_turn"),
	MAX_TOKENS("max_tokens"),
	STOP_SEQUENCE("stop_sequence"),
	TOOL_USE("tool_use");

	private final String value;

	CanonicalStopReason(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return this.value;
	}

	@JsonCreator
	public static CanonicalStopReason fromValue(String value) {
		if (value == null) {
			return null;
		}
		for (CanonicalStopReason r : values()) {
			if (r.value.equals(value)) {
				return r;
			}
		}
		throw new IllegalArgumentException("CanonicalStopReason sconosciuto: " + value);
	}

	/**
	 * Versione lenient di {@link #fromValue(String)}: ritorna null per valori
	 * sconosciuti invece di lanciare. Utile per i trasformatori response, dove
	 * un nuovo valore introdotto dal provider non deve far fallire l'intera
	 * pipeline.
	 */
	public static CanonicalStopReason tryFromValue(String value) {
		if (value == null) {
			return null;
		}
		for (CanonicalStopReason r : values()) {
			if (r.value.equals(value)) {
				return r;
			}
		}
		return null;
	}
}
