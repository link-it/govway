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

/**
 * Chiusura del blocco corrente. Specifico Anthropic {@code content_block_stop}.
 * In OpenAI Chat Completions non c'è un evento corrispondente.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class CanonicalStreamContentBlockStop extends CanonicalStreamEvent {

	private int index;

	public CanonicalStreamContentBlockStop() {
		super("content_block_stop");
	}

	public CanonicalStreamContentBlockStop(int index) {
		super("content_block_stop");
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
