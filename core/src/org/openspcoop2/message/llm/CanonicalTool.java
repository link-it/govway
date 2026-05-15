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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Definizione di un tool (function) dichiarato nella richiesta.
 * Anthropic: {"name":"...","description":"...","input_schema":{JSON Schema}}.
 * OpenAI:    {"type":"function","function":{"name":"...","description":"...","parameters":{JSON Schema}}}.
 * Il canonical normalizza sulla forma Anthropic-like.
 *
 * @author Andrea Poli (apoli@link.it)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CanonicalTool {

	private String name;
	private String description;

	@JsonProperty("input_schema")
	private Map<String, Object> inputSchema;

	public CanonicalTool() {
	}

	public CanonicalTool(String name, String description, Map<String, Object> inputSchema) {
		this.name = name;
		this.description = description;
		this.inputSchema = inputSchema;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, Object> getInputSchema() {
		return this.inputSchema;
	}

	public void setInputSchema(Map<String, Object> inputSchema) {
		this.inputSchema = inputSchema;
	}
}
