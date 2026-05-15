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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Blocco di contenuto del modello canonical. Struttura block-based ispirata
 * ad Anthropic Messages: ogni messaggio contiene una lista di blocchi tipizzati.
 * Per il prototipo MVP supportiamo tre tipi: text, tool_use, tool_result.
 * In futuro: image, document, thinking.
 *
 * @author Andrea Poli (apoli@link.it)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type",
		visible = true)
@JsonSubTypes({
		@JsonSubTypes.Type(value = CanonicalTextBlock.class, name = "text"),
		@JsonSubTypes.Type(value = CanonicalToolUseBlock.class, name = "tool_use"),
		@JsonSubTypes.Type(value = CanonicalToolResultBlock.class, name = "tool_result")
})
public abstract class CanonicalContentBlock {

	private final String type;

	protected CanonicalContentBlock(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}
}
