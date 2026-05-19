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
 * Apertura di un blocco di contenuto (text / tool_use / ...) all'interno della
 * response streaming. Concept specifico Anthropic Messages: in OpenAI Chat
 * Completions è implicito (un'unica content stringa).
 * <p>
 * {@code toolUseId} e {@code toolUseName} valorizzati solo per blocchi tool_use,
 * permettono al front-door encoder di emettere il primo chunk OpenAI con
 * {@code tool_calls[i].id} e {@code tool_calls[i].function.name}.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class CanonicalStreamContentBlockStart extends CanonicalStreamEvent {

	private int index;
	private String blockType;
	private String toolUseId;
	private String toolUseName;

	public CanonicalStreamContentBlockStart() {
		super("content_block_start");
	}

	public CanonicalStreamContentBlockStart(int index, String blockType) {
		super("content_block_start");
		this.index = index;
		this.blockType = blockType;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getBlockType() {
		return this.blockType;
	}

	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}

	public String getToolUseId() {
		return this.toolUseId;
	}

	public void setToolUseId(String toolUseId) {
		this.toolUseId = toolUseId;
	}

	public String getToolUseName() {
		return this.toolUseName;
	}

	public void setToolUseName(String toolUseName) {
		this.toolUseName = toolUseName;
	}
}
