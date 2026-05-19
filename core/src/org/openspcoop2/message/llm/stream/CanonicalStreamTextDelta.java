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
 * Incremento di testo da concatenare al blocco text corrente.
 * Anthropic {@code content_block_delta} con {@code delta.type=text_delta}.
 * OpenAI {@code data: {"choices":[{"delta":{"content":"<text>"}}]}}.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class CanonicalStreamTextDelta extends CanonicalStreamEvent {

	private int index;
	private String text;

	public CanonicalStreamTextDelta() {
		super("text_delta");
	}

	public CanonicalStreamTextDelta(int index, String text) {
		super("text_delta");
		this.index = index;
		this.text = text;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
