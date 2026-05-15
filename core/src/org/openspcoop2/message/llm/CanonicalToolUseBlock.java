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

import java.util.Map;

/**
 * Invocazione di tool emessa dall'assistente.
 * Anthropic: {"type":"tool_use","id":"...","name":"...","input":{...}}.
 * OpenAI: voce di tool_calls[] con function.name e function.arguments (stringa JSON).
 * L'inbound OpenAI parsa la stringa JSON degli arguments in Map per uniformare.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class CanonicalToolUseBlock extends CanonicalContentBlock {

	private String id;
	private String name;
	private Map<String, Object> input;

	public CanonicalToolUseBlock() {
		super("tool_use");
	}

	public CanonicalToolUseBlock(String id, String name, Map<String, Object> input) {
		super("tool_use");
		this.id = id;
		this.name = name;
		this.input = input;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Object> getInput() {
		return this.input;
	}

	public void setInput(Map<String, Object> input) {
		this.input = input;
	}
}
