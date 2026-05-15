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
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Esito dell'esecuzione di un tool, restituito al modello in un turno successivo.
 * Anthropic: {"type":"tool_result","tool_use_id":"...","content":"...","is_error":bool}.
 * OpenAI: messaggio con role="tool" + tool_call_id + content stringa.
 * Il content qui è una stringa (può essere JSON serializzato dall'applicativo).
 *
 * @author Andrea Poli (apoli@link.it)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CanonicalToolResultBlock extends CanonicalContentBlock {

	@JsonProperty("tool_use_id")
	private String toolUseId;

	private String content;

	@JsonProperty("is_error")
	private Boolean isError;

	public CanonicalToolResultBlock() {
		super("tool_result");
	}

	public CanonicalToolResultBlock(String toolUseId, String content, Boolean isError) {
		super("tool_result");
		this.toolUseId = toolUseId;
		this.content = content;
		this.isError = isError;
	}

	public String getToolUseId() {
		return this.toolUseId;
	}

	public void setToolUseId(String toolUseId) {
		this.toolUseId = toolUseId;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Boolean getIsError() {
		return this.isError;
	}

	public void setIsError(Boolean isError) {
		this.isError = isError;
	}
}
