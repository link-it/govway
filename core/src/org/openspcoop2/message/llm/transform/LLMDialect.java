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
package org.openspcoop2.message.llm.transform;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Dialetto LLM front-door supportato dai trasformatori inbound/outbound.
 * <p>
 * Mantiene un'identità interna al modulo {@code message}, indipendente da
 * {@code FormatoSpecifica} (che vive nel modulo {@code registry}, compilato
 * successivamente). I valori stringa corrispondono uno-a-uno a quelli
 * registrati su {@code FormatoSpecifica.OPENAI_CHAT_V1.getValue()} e
 * {@code FormatoSpecifica.ANTHROPIC_MESSAGES_V1.getValue()}: il mapping
 * vive negli handler della pipeline.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public enum LLMDialect {

	OPENAI_CHAT_V1("openaiChatV1"),
	ANTHROPIC_MESSAGES_V1("anthropicMessagesV1");

	private final String value;

	LLMDialect(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return this.value;
	}

	/**
	 * Variante lenient: ritorna null per valori sconosciuti.
	 * Utile per il mapping FormatoSpecifica → LLMDialect lato handler.
	 */
	@JsonCreator
	public static LLMDialect fromValue(String value) {
		if (value == null) {
			return null;
		}
		for (LLMDialect d : values()) {
			if (d.value.equals(value)) {
				return d;
			}
		}
		return null;
	}
}
