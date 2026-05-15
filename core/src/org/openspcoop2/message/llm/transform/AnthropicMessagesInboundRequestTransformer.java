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
package org.openspcoop2.message.llm.transform;

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
		if (root.hasNonNull("model")) {
			out.setModel(root.get("model").asText());
		}
		if (root.hasNonNull("system")) {
			out.setSystem(root.get("system").asText());
		}
		applyGenerationSettings(root, out);
		applyMessages(root, out, mapper);
		applyTools(root, out, mapper);
		return out;
	}


	/* === sezioni della request === */

	private void applyGenerationSettings(JsonNode root, CanonicalChatRequest out) {
		if (root.hasNonNull("max_tokens")) {
			out.setMaxTokens(root.get("max_tokens").asInt());
		}
		if (root.hasNonNull("temperature")) {
			out.setTemperature(root.get("temperature").asDouble());
		}
		if (root.hasNonNull("top_p")) {
			out.setTopP(root.get("top_p").asDouble());
		}
		if (root.hasNonNull("stream")) {
			out.setStream(root.get("stream").asBoolean());
		}
		applyStopSequences(root, out);
	}

	private void applyStopSequences(JsonNode root, CanonicalChatRequest out) {
		if (!root.hasNonNull("stop_sequences") || !root.get("stop_sequences").isArray()) {
			return;
		}
		List<String> stops = new ArrayList<>();
		for (JsonNode s : root.get("stop_sequences")) {
			stops.add(s.asText());
		}
		if (!stops.isEmpty()) {
			out.setStopSequences(stops);
		}
	}

	private void applyMessages(JsonNode root, CanonicalChatRequest out, ObjectMapper mapper) throws LLMTransformException {
		if (!root.hasNonNull("messages") || !root.get("messages").isArray()) {
			return;
		}
		List<CanonicalMessage> list = new ArrayList<>();
		for (JsonNode m : root.get("messages")) {
			list.add(buildMessage(m, mapper));
		}
		out.setMessages(list);
	}

	private CanonicalMessage buildMessage(JsonNode m, ObjectMapper mapper) throws LLMTransformException {
		String role = m.hasNonNull("role") ? m.get("role").asText() : null;
		CanonicalRole canonicalRole = role != null ? CanonicalRole.fromValue(role) : null;
		if (canonicalRole == null) {
			throw new LLMTransformException("Ruolo Anthropic non valido o assente: " + role);
		}
		List<CanonicalContentBlock> blocks = new ArrayList<>();
		if (m.hasNonNull("content")) {
			populateContent(m.get("content"), blocks, mapper);
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
		String type = part.hasNonNull("type") ? part.get("type").asText() : null;
		if (type == null) {
			throw new LLMTransformException("Blocco Anthropic senza 'type'");
		}
		switch (type) {
			case "text":
				return new CanonicalTextBlock(part.hasNonNull("text") ? part.get("text").asText() : "");
			case "tool_use":
				return buildToolUse(part, mapper);
			case "tool_result":
				return buildToolResult(part);
			default:
				throw new LLMTransformException("Tipo blocco Anthropic non supportato nel prototipo: " + type);
		}
	}

	private CanonicalToolUseBlock buildToolUse(JsonNode part, ObjectMapper mapper) throws LLMTransformException {
		String id = part.hasNonNull("id") ? part.get("id").asText() : null;
		String name = part.hasNonNull("name") ? part.get("name").asText() : null;
		Map<String, Object> input = null;
		if (part.hasNonNull("input")) {
			try {
				input = mapper.convertValue(part.get("input"), MAP_TYPE);
			} catch (Exception e) {
				throw new LLMTransformException("input del tool_use non convertibile in Map: " + e.getMessage(), e);
			}
		}
		return new CanonicalToolUseBlock(id, name, input);
	}

	private CanonicalToolResultBlock buildToolResult(JsonNode part) {
		String toolUseId = part.hasNonNull("tool_use_id") ? part.get("tool_use_id").asText() : null;
		String content = extractContent(part);
		Boolean isError = part.hasNonNull("is_error") ? part.get("is_error").asBoolean() : null;
		return new CanonicalToolResultBlock(toolUseId, content, isError);
	}

	private String extractContent(JsonNode part) {
		if (!part.hasNonNull("content")) {
			return null;
		}
		JsonNode c = part.get("content");
		return c.isTextual() ? c.asText() : c.toString();
	}


	/* === tools === */

	private void applyTools(JsonNode root, CanonicalChatRequest out, ObjectMapper mapper) {
		if (!root.hasNonNull("tools") || !root.get("tools").isArray()) {
			return;
		}
		List<CanonicalTool> list = new ArrayList<>();
		for (JsonNode t : root.get("tools")) {
			list.add(buildTool(t, mapper));
		}
		if (!list.isEmpty()) {
			out.setTools(list);
		}
	}

	private CanonicalTool buildTool(JsonNode t, ObjectMapper mapper) {
		String name = t.hasNonNull("name") ? t.get("name").asText() : null;
		String description = t.hasNonNull("description") ? t.get("description").asText() : null;
		Map<String, Object> schema = null;
		if (t.hasNonNull("input_schema")) {
			schema = mapper.convertValue(t.get("input_schema"), MAP_TYPE);
		}
		return new CanonicalTool(name, description, schema);
	}
}
