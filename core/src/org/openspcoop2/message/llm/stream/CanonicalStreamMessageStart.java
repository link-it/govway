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
package org.openspcoop2.message.llm.stream;

import org.openspcoop2.message.llm.CanonicalRole;
import org.openspcoop2.message.llm.CanonicalUsage;

/**
 * Inizio della response in streaming: trasporta id/model/role e (se disponibile)
 * un usage iniziale. Anthropic {@code message_start}, OpenAI primo chunk con
 * {@code delta.role:"assistant"} (id/model presenti come campi top-level del chunk).
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class CanonicalStreamMessageStart extends CanonicalStreamEvent {

	private String id;
	private String model;
	private CanonicalRole role;
	private CanonicalUsage usage;

	public CanonicalStreamMessageStart() {
		super("message_start");
	}

	public CanonicalStreamMessageStart(String id, String model, CanonicalRole role, CanonicalUsage usage) {
		super("message_start");
		this.id = id;
		this.model = model;
		this.role = role;
		this.usage = usage;
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

	public CanonicalRole getRole() {
		return this.role;
	}

	public void setRole(CanonicalRole role) {
		this.role = role;
	}

	public CanonicalUsage getUsage() {
		return this.usage;
	}

	public void setUsage(CanonicalUsage usage) {
		this.usage = usage;
	}
}
