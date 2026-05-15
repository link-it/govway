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

/**
 * Blocco di contenuto di tipo testo. Equivalente a {"type":"text","text":"..."}
 * in Anthropic Messages. Per OpenAI Chat Completions, content stringa viene
 * convertito in singolo CanonicalTextBlock dall'inbound transformer.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class CanonicalTextBlock extends CanonicalContentBlock {

	private String text;

	public CanonicalTextBlock() {
		super("text");
	}

	public CanonicalTextBlock(String text) {
		super("text");
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
