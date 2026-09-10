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
 * Selezione dei tool richiesta dal client ({@code tool_choice}). La forma serializzata
 * coincide con quella Anthropic ({@code {"type":"tool","name":"..."}}), coerentemente con
 * il resto del modello canonical.
 *
 * @author Andrea Poli (apoli@link.it)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CanonicalToolChoice {

	@JsonProperty("type")
	private CanonicalToolChoiceMode mode;

	/** Nome del tool da invocare, valorizzato solo con {@link CanonicalToolChoiceMode#TOOL}. */
	private String name;

	public CanonicalToolChoice() {
	}

	public CanonicalToolChoice(CanonicalToolChoiceMode mode) {
		this.mode = mode;
	}

	public CanonicalToolChoice(CanonicalToolChoiceMode mode, String name) {
		this.mode = mode;
		this.name = name;
	}

	public CanonicalToolChoiceMode getMode() {
		return this.mode;
	}

	public void setMode(CanonicalToolChoiceMode mode) {
		this.mode = mode;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
