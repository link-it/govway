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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Richiesta chat nel modello canonical interno.
 * Forma block-based ispirata ad Anthropic Messages, estesa con campi opzionali
 * specifici di OpenAI (per il MVP nessuno, verranno aggiunti incrementalmente).
 *
 * Il canonical NON ha un enum del dialetto sorgente: la traccia del dialetto di ingresso
 * vive come stringa libera in {@code sourceDialect}, solo per audit/debug.
 *
 * @author Andrea Poli (apoli@link.it)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CanonicalChatRequest {

	/** Alias del modello richiesto dal client (es. "claude-opus-4-7"). Routing lo risolve sul binding. */
	private String model;

	/** System prompt (Anthropic-style, campo top-level distinto dai messaggi). */
	private String system;

	/** Conversazione: lista di CanonicalMessage user/assistant. */
	private List<CanonicalMessage> messages;

	/** Tool dichiarati per la function calling. */
	private List<CanonicalTool> tools;

	@JsonProperty("max_tokens")
	private Integer maxTokens;

	private Double temperature;

	@JsonProperty("top_p")
	private Double topP;

	@JsonProperty("stop_sequences")
	private List<String> stopSequences;

	private Boolean stream;

	/**
	 * Identificatore del dialetto di ingresso (es. "openai_chat_v1", "anthropic_messages_v1").
	 * Solo audit/debug. Non usato per logica di trasformazione: le decisioni di routing
	 * derivano dal {@code FormatoSpecifica} dell'API GovWay.
	 */
	@JsonIgnore
	private String sourceDialect;

	public CanonicalChatRequest() {
	}

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSystem() {
		return this.system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public List<CanonicalMessage> getMessages() {
		return this.messages;
	}

	public void setMessages(List<CanonicalMessage> messages) {
		this.messages = messages;
	}

	public List<CanonicalTool> getTools() {
		return this.tools;
	}

	public void setTools(List<CanonicalTool> tools) {
		this.tools = tools;
	}

	public Integer getMaxTokens() {
		return this.maxTokens;
	}

	public void setMaxTokens(Integer maxTokens) {
		this.maxTokens = maxTokens;
	}

	public Double getTemperature() {
		return this.temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Double getTopP() {
		return this.topP;
	}

	public void setTopP(Double topP) {
		this.topP = topP;
	}

	public List<String> getStopSequences() {
		return this.stopSequences;
	}

	public void setStopSequences(List<String> stopSequences) {
		this.stopSequences = stopSequences;
	}

	public Boolean getStream() {
		return this.stream;
	}

	public void setStream(Boolean stream) {
		this.stream = stream;
	}

	public String getSourceDialect() {
		return this.sourceDialect;
	}

	public void setSourceDialect(String sourceDialect) {
		this.sourceDialect = sourceDialect;
	}
}
