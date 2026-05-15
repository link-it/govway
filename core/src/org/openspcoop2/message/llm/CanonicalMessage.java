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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Messaggio della conversazione: ruolo (user/assistant) + lista di blocchi tipizzati.
 * Il system prompt non è un messaggio: vive come campo top-level di CanonicalChatRequest
 * (allineamento con Anthropic Messages).
 *
 * @author Andrea Poli (apoli@link.it)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CanonicalMessage {

	private CanonicalRole role;
	private List<CanonicalContentBlock> content;

	public CanonicalMessage() {
	}

	public CanonicalMessage(CanonicalRole role, List<CanonicalContentBlock> content) {
		this.role = role;
		this.content = content;
	}

	public CanonicalRole getRole() {
		return this.role;
	}

	public void setRole(CanonicalRole role) {
		this.role = role;
	}

	public List<CanonicalContentBlock> getContent() {
		return this.content;
	}

	public void setContent(List<CanonicalContentBlock> content) {
		this.content = content;
	}

	public void addBlock(CanonicalContentBlock block) {
		if (this.content == null) {
			this.content = new ArrayList<>();
		}
		this.content.add(block);
	}
}
