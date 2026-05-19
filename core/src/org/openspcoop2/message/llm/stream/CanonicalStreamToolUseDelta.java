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
 * Incremento JSON parziale dell'input di un blocco tool_use.
 * Anthropic {@code content_block_delta} con {@code delta.type=input_json_delta}.
 * OpenAI {@code data: {"choices":[{"delta":{"tool_calls":[{"index":i,"function":{"arguments":"<partial>"}}]}}]}}.
 * <p>
 * Il payload è uno string-fragment di JSON non necessariamente parsabile in isolamento:
 * il client deve concatenare i delta successivi per ottenere l'input completo del tool.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class CanonicalStreamToolUseDelta extends CanonicalStreamEvent {

	private int index;
	private String partialJsonInput;

	public CanonicalStreamToolUseDelta() {
		super("tool_use_delta");
	}

	public CanonicalStreamToolUseDelta(int index, String partialJsonInput) {
		super("tool_use_delta");
		this.index = index;
		this.partialJsonInput = partialJsonInput;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getPartialJsonInput() {
		return this.partialJsonInput;
	}

	public void setPartialJsonInput(String partialJsonInput) {
		this.partialJsonInput = partialJsonInput;
	}
}
