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
package org.openspcoop2.message.llm.transform.anthropic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.message.llm.CanonicalChatRequest;
import org.openspcoop2.message.llm.CanonicalContentBlock;
import org.openspcoop2.message.llm.CanonicalMessage;
import org.openspcoop2.message.llm.CanonicalRole;
import org.openspcoop2.message.llm.CanonicalTextBlock;
import org.openspcoop2.message.llm.CanonicalTool;
import org.openspcoop2.message.llm.CanonicalToolResultBlock;
import org.openspcoop2.message.llm.CanonicalToolUseBlock;
import org.openspcoop2.message.llm.transform.LLMDialect;
import org.openspcoop2.message.llm.transform.LLMInboundRequestTransformer;
import org.openspcoop2.message.llm.transform.LLMTransformException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Trasformatore inbound: Anthropic Messages request → CanonicalChatRequest.
 * Il mapping è quasi 1:1 perché il canonical è modellato sulla forma Anthropic
 * (block-based, system top-level, ruoli user/assistant).
 *
 * Coperture del prototipo: text, tool_use, tool_result. Non supportati: image, document.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class AnthropicMessagesInboundRequestTransformer implements LLMInboundRequestTransformer {

	private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {};

	@Override
	public LLMDialect getSupportedDialect() {
		return LLMDialect.ANTHROPIC_MESSAGES_V1;
	}

	@Override
	public CanonicalChatRequest transform(byte[] payload) throws LLMTransformException {
		if (payload == null || payload.length == 0) {
			throw new LLMTransformException("Payload Anthropic Messages vuoto");
		}
		try {
			ObjectMapper mapper = JSONUtils.getObjectMapper();
			JsonNode root = mapper.readTree(payload);
			return parse(root, mapper);
		} catch (LLMTransformException e) {
			throw e;
		} catch (Exception e) {
			throw new LLMTransformException("Errore nel parsing della request Anthropic Messages: " + e.getMessage(), e);
		}
	}

	private CanonicalChatRequest parse(JsonNode root, ObjectMapper mapper) throws LLMTransformException {
		CanonicalChatRequest out = new CanonicalChatRequest();
		out.setSourceDialect(LLMDialect.ANTHROPIC_MESSAGES_V1.getValue());
		if (root.hasNonNull(AnthropicMessagesFields.FIELD_MODEL)) {
			out.setModel(root.get(AnthropicMessagesFields.FIELD_MODEL).asText());
		}
		if (root.hasNonNull(AnthropicMessagesFields.FIELD_SYSTEM)) {
			out.setSystem(extractSystemText(root.get(AnthropicMessagesFields.FIELD_SYSTEM)));
		}
		applyGenerationSettings(root, out);
		applyMessages(root, out, mapper);
		applyTools(root, out, mapper);
		return out;
	}


	/* === sezioni della request === */

	private void applyGenerationSettings(JsonNode root, CanonicalChatRequest out) {
		if (root.hasNonNull(AnthropicMessagesFields.FIELD_MAX_TOKENS)) {
			out.setMaxTokens(root.get(AnthropicMessagesFields.FIELD_MAX_TOKENS).asInt());
		}
		if (root.hasNonNull(AnthropicMessagesFields.FIELD_TEMPERATURE)) {
			out.setTemperature(root.get(AnthropicMessagesFields.FIELD_TEMPERATURE).asDouble());
		}
		if (root.hasNonNull(AnthropicMessagesFields.FIELD_TOP_P)) {
			out.setTopP(root.get(AnthropicMessagesFields.FIELD_TOP_P).asDouble());
		}
		if (root.hasNonNull(AnthropicMessagesFields.FIELD_STREAM)) {
			out.setStream(root.get(AnthropicMessagesFields.FIELD_STREAM).asBoolean());
		}
		applyStopSequences(root, out);
	}

	private void applyStopSequences(JsonNode root, CanonicalChatRequest out) {
		if (!root.hasNonNull(AnthropicMessagesFields.FIELD_STOP_SEQUENCES) || !root.get(AnthropicMessagesFields.FIELD_STOP_SEQUENCES).isArray()) {
			return;
		}
		List<String> stops = new ArrayList<>();
		for (JsonNode s : root.get(AnthropicMessagesFields.FIELD_STOP_SEQUENCES)) {
			stops.add(s.asText());
		}
		if (!stops.isEmpty()) {
			out.setStopSequences(stops);
		}
	}

	private void applyMessages(JsonNode root, CanonicalChatRequest out, ObjectMapper mapper) throws LLMTransformException {
		if (!root.hasNonNull(AnthropicMessagesFields.FIELD_MESSAGES) || !root.get(AnthropicMessagesFields.FIELD_MESSAGES).isArray()) {
			return;
		}
		List<CanonicalMessage> list = new ArrayList<>();
		for (JsonNode m : root.get(AnthropicMessagesFields.FIELD_MESSAGES)) {
			list.add(buildMessage(m, mapper));
		}
		out.setMessages(list);
	}

	private CanonicalMessage buildMessage(JsonNode m, ObjectMapper mapper) throws LLMTransformException {
		String role = m.hasNonNull(AnthropicMessagesFields.FIELD_ROLE) ? m.get(AnthropicMessagesFields.FIELD_ROLE).asText() : null;
		CanonicalRole canonicalRole = role != null ? CanonicalRole.fromValue(role) : null;
		if (canonicalRole == null) {
			throw new LLMTransformException("Ruolo Anthropic non valido o assente: " + role);
		}
		List<CanonicalContentBlock> blocks = new ArrayList<>();
		if (m.hasNonNull(AnthropicMessagesFields.FIELD_CONTENT)) {
			populateContent(m.get(AnthropicMessagesFields.FIELD_CONTENT), blocks, mapper);
		}
		return new CanonicalMessage(canonicalRole, blocks);
	}

	private void populateContent(JsonNode content, List<CanonicalContentBlock> blocks, ObjectMapper mapper) throws LLMTransformException {
		if (content.isTextual()) {
			blocks.add(new CanonicalTextBlock(content.asText()));
			return;
		}
		if (!content.isArray()) {
			throw new LLMTransformException("Content Anthropic deve essere stringa o array");
		}
		for (JsonNode part : content) {
			blocks.add(buildBlock(part, mapper));
		}
	}

	private CanonicalContentBlock buildBlock(JsonNode part, ObjectMapper mapper) throws LLMTransformException {
		String type = part.hasNonNull(AnthropicMessagesFields.FIELD_TYPE) ? part.get(AnthropicMessagesFields.FIELD_TYPE).asText() : null;
		if (type == null) {
			throw new LLMTransformException("Blocco Anthropic senza 'type'");
		}
		switch (type) {
			case AnthropicMessagesFields.BLOCK_TYPE_TEXT:
				return new CanonicalTextBlock(part.hasNonNull(AnthropicMessagesFields.FIELD_TEXT) ? part.get(AnthropicMessagesFields.FIELD_TEXT).asText() : "");
			case AnthropicMessagesFields.BLOCK_TYPE_TOOL_USE:
				return buildToolUse(part, mapper);
			case AnthropicMessagesFields.BLOCK_TYPE_TOOL_RESULT:
				return buildToolResult(part);
			default:
				throw new LLMTransformException("Tipo blocco Anthropic non supportato nel prototipo: " + type);
		}
	}

	private CanonicalToolUseBlock buildToolUse(JsonNode part, ObjectMapper mapper) throws LLMTransformException {
		String id = part.hasNonNull(AnthropicMessagesFields.FIELD_ID) ? part.get(AnthropicMessagesFields.FIELD_ID).asText() : null;
		String name = part.hasNonNull(AnthropicMessagesFields.FIELD_NAME) ? part.get(AnthropicMessagesFields.FIELD_NAME).asText() : null;
		Map<String, Object> input = null;
		if (part.hasNonNull(AnthropicMessagesFields.FIELD_INPUT)) {
			try {
				input = mapper.convertValue(part.get(AnthropicMessagesFields.FIELD_INPUT), MAP_TYPE);
			} catch (Exception e) {
				throw new LLMTransformException("input del tool_use non convertibile in Map: " + e.getMessage(), e);
			}
		}
		return new CanonicalToolUseBlock(id, name, input);
	}

	private CanonicalToolResultBlock buildToolResult(JsonNode part) {
		String toolUseId = part.hasNonNull(AnthropicMessagesFields.FIELD_TOOL_USE_ID) ? part.get(AnthropicMessagesFields.FIELD_TOOL_USE_ID).asText() : null;
		String content = extractContent(part);
		Boolean isError = part.hasNonNull(AnthropicMessagesFields.FIELD_IS_ERROR) ? part.get(AnthropicMessagesFields.FIELD_IS_ERROR).asBoolean() : null;
		return new CanonicalToolResultBlock(toolUseId, content, isError);
	}

	private String extractContent(JsonNode part) {
		if (!part.hasNonNull(AnthropicMessagesFields.FIELD_CONTENT)) {
			return null;
		}
		JsonNode c = part.get(AnthropicMessagesFields.FIELD_CONTENT);
		return c.isTextual() ? c.asText() : c.toString();
	}


	/* === tools === */

	private void applyTools(JsonNode root, CanonicalChatRequest out, ObjectMapper mapper) {
		if (!root.hasNonNull(AnthropicMessagesFields.FIELD_TOOLS) || !root.get(AnthropicMessagesFields.FIELD_TOOLS).isArray()) {
			return;
		}
		List<CanonicalTool> list = new ArrayList<>();
		for (JsonNode t : root.get(AnthropicMessagesFields.FIELD_TOOLS)) {
			list.add(buildTool(t, mapper));
		}
		if (!list.isEmpty()) {
			out.setTools(list);
		}
	}

	private CanonicalTool buildTool(JsonNode t, ObjectMapper mapper) {
		String name = t.hasNonNull(AnthropicMessagesFields.FIELD_NAME) ? t.get(AnthropicMessagesFields.FIELD_NAME).asText() : null;
		String description = t.hasNonNull(AnthropicMessagesFields.FIELD_DESCRIPTION) ? t.get(AnthropicMessagesFields.FIELD_DESCRIPTION).asText() : null;
		Map<String, Object> schema = null;
		if (t.hasNonNull(AnthropicMessagesFields.FIELD_INPUT_SCHEMA)) {
			schema = mapper.convertValue(t.get(AnthropicMessagesFields.FIELD_INPUT_SCHEMA), MAP_TYPE);
		}
		return new CanonicalTool(name, description, schema);
	}

	/**
	 * Anthropic accetta {@code system} sia come stringa singola sia come array di
	 * blocchi {@code {type:"text", text:"..."}}. Concatena il testo dei blocchi
	 * di tipo {@code text} se l'input è un array, altrimenti tratta come stringa.
	 */
	private String extractSystemText(JsonNode system) {
		if (system == null) {
			return null;
		}
		if (system.isTextual()) {
			return system.asText();
		}
		if (system.isArray()) {
			StringBuilder sb = new StringBuilder();
			for (JsonNode block : system) {
				if (block.hasNonNull(AnthropicMessagesFields.FIELD_TYPE)
						&& AnthropicMessagesFields.BLOCK_TYPE_TEXT.equals(block.get(AnthropicMessagesFields.FIELD_TYPE).asText())
						&& block.hasNonNull(AnthropicMessagesFields.FIELD_TEXT)) {
					if (sb.length() > 0) {
						sb.append("\n\n");
					}
					sb.append(block.get(AnthropicMessagesFields.FIELD_TEXT).asText());
				}
			}
			return sb.length() > 0 ? sb.toString() : null;
		}
		return null;
	}
}
