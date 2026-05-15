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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Risposta chat nel modello canonical interno.
 * Forma block-based ispirata ad Anthropic Messages: la risposta è una lista
 * di blocchi (text e/o tool_use) prodotti dal modello.
 *
 * @author Andrea Poli (apoli@link.it)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CanonicalChatResponse {

	/** Identificativo della risposta restituito dal provider (es. "msg_01ABCD..."). */
	private String id;

	/** Modello che ha effettivamente prodotto la risposta. */
	private String model;

	/** Blocchi di contenuto della risposta. Il ruolo implicito è ASSISTANT. */
	private List<CanonicalContentBlock> content;

	@JsonProperty("stop_reason")
	private CanonicalStopReason stopReason;

	@JsonProperty("stop_sequence")
	private String stopSequence;

	private CanonicalUsage usage;

	public CanonicalChatResponse() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<CanonicalContentBlock> getContent() {
		return this.content;
	}

	public void setContent(List<CanonicalContentBlock> content) {
		this.content = content;
	}

	public CanonicalStopReason getStopReason() {
		return this.stopReason;
	}

	public void setStopReason(CanonicalStopReason stopReason) {
		this.stopReason = stopReason;
	}

	public String getStopSequence() {
		return this.stopSequence;
	}

	public void setStopSequence(String stopSequence) {
		this.stopSequence = stopSequence;
	}

	public CanonicalUsage getUsage() {
		return this.usage;
	}

	public void setUsage(CanonicalUsage usage) {
		this.usage = usage;
	}
}
