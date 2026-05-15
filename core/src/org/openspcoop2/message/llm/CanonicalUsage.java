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
 * Conteggio token consumati. Naming canonical allineato ad Anthropic
 * (input_tokens / output_tokens). I trasformatori outbound mappano sul naming OpenAI
 * (prompt_tokens / completion_tokens) quando richiesto.
 *
 * @author Andrea Poli (apoli@link.it)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CanonicalUsage {

	@JsonProperty("input_tokens")
	private Integer inputTokens;

	@JsonProperty("output_tokens")
	private Integer outputTokens;

	public CanonicalUsage() {
	}

	public CanonicalUsage(Integer inputTokens, Integer outputTokens) {
		this.inputTokens = inputTokens;
		this.outputTokens = outputTokens;
	}

	public Integer getInputTokens() {
		return this.inputTokens;
	}

	public void setInputTokens(Integer inputTokens) {
		this.inputTokens = inputTokens;
	}

	public Integer getOutputTokens() {
		return this.outputTokens;
	}

	public void setOutputTokens(Integer outputTokens) {
		this.outputTokens = outputTokens;
	}
}
