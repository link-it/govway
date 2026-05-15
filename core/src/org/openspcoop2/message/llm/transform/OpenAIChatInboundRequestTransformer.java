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
 * Trasformatore inbound: OpenAI Chat Completions request → CanonicalChatRequest.
 *
 * Coperture del prototipo:
 * <ul>
 *   <li>messages: role=system|user|assistant|tool, content stringa, tool_calls[]</li>
 *   <li>tools: type=function con function.{name,description,parameters}</li>
 *   <li>max_completion_tokens (preferito) / max_tokens (legacy), temperature, top_p, stop, stream</li>
 * </ul>
 *
 * Non gestiti nel MVP: content array multimodale (image_url, input_audio),
 * tool_choice, response_format, reasoning_effort, n, seed, logprobs.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class OpenAIChatInboundRequestTransformer implements LLMInboundRequestTransformer {

	private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {};

	@Override
	public LLMDialect getSupportedDialect() {
		return LLMDialect.OPENAI_CHAT_V1;
	}

	@Override
	public CanonicalChatRequest transform(byte[] payload) throws LLMTransformException {
		if (payload == null || payload.length == 0) {
			throw new LLMTransformException("Payload OpenAI Chat Completions vuoto");
		}
		try {
			ObjectMapper mapper = JSONUtils.getObjectMapper();
			JsonNode root = mapper.readTree(payload);
			return parse(root, mapper);
		} catch (LLMTransformException e) {
			throw e;
		} catch (Exception e) {
			throw new LLMTransformException("Errore nel parsing della request OpenAI Chat Completions: " + e.getMessage(), e);
		}
	}

	private CanonicalChatRequest parse(JsonNode root, ObjectMapper mapper) throws LLMTransformException {
		CanonicalChatRequest out = new CanonicalChatRequest();
		out.setSourceDialect(LLMDialect.OPENAI_CHAT_V1.getValue());
		applyBasicSettings(root, out);
		applyStopSequences(root, out);
		applyMessages(root, out);
		applyTools(root, out, mapper);
		return out;
	}


	/* === sezioni della request === */

	private void applyBasicSettings(JsonNode root, CanonicalChatRequest out) {
		if (root.hasNonNull("model")) {
			out.setModel(root.get("model").asText());
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
		applyMaxTokens(root, out);
	}

	private void applyMaxTokens(JsonNode root, CanonicalChatRequest out) {
		// OpenAI preferisce max_completion_tokens; max_tokens è legacy ma ancora diffuso
		if (root.hasNonNull("max_completion_tokens")) {
			out.setMaxTokens(root.get("max_completion_tokens").asInt());
		} else if (root.hasNonNull("max_tokens")) {
			out.setMaxTokens(root.get("max_tokens").asInt());
		}
	}

	private void applyStopSequences(JsonNode root, CanonicalChatRequest out) {
		if (!root.hasNonNull("stop")) {
			return;
		}
		JsonNode stop = root.get("stop");
		List<String> stops = new ArrayList<>();
		if (stop.isTextual()) {
			stops.add(stop.asText());
		} else if (stop.isArray()) {
			for (JsonNode s : stop) {
				stops.add(s.asText());
			}
		}
		if (!stops.isEmpty()) {
			out.setStopSequences(stops);
		}
	}

	private void applyMessages(JsonNode root, CanonicalChatRequest out) throws LLMTransformException {
		if (!root.hasNonNull("messages") || !root.get("messages").isArray()) {
			return;
		}
		List<CanonicalMessage> canonical = new ArrayList<>();
		StringBuilder system = new StringBuilder();
		for (JsonNode m : root.get("messages")) {
			handleMessage(m, canonical, system);
		}
		out.setMessages(canonical);
		if (system.length() > 0) {
			out.setSystem(system.toString());
		}
	}

	private void handleMessage(JsonNode m, List<CanonicalMessage> canonical, StringBuilder system) throws LLMTransformException {
		String role = m.hasNonNull("role") ? m.get("role").asText() : null;
		if (role == null) {
			throw new LLMTransformException("Messaggio OpenAI privo di campo 'role'");
		}
		switch (role) {
			case "system":
				appendSystem(system, m);
				break;
			case "user":
				canonical.add(buildUserMessage(m));
				break;
			case "assistant":
				canonical.add(buildAssistantMessage(m));
				break;
			case "tool":
				canonical.add(buildToolResultMessage(m));
				break;
			default:
				throw new LLMTransformException("Ruolo OpenAI non supportato: " + role);
		}
	}

	private void applyTools(JsonNode root, CanonicalChatRequest out, ObjectMapper mapper) throws LLMTransformException {
		if (!root.hasNonNull("tools") || !root.get("tools").isArray()) {
			return;
		}
		List<CanonicalTool> tools = new ArrayList<>();
		for (JsonNode t : root.get("tools")) {
			tools.add(buildTool(t, mapper));
		}
		if (!tools.isEmpty()) {
			out.setTools(tools);
		}
	}


	/* === costruzione messaggi per ruolo === */

	private void appendSystem(StringBuilder buf, JsonNode m) {
		if (!m.hasNonNull("content")) {
			return;
		}
		JsonNode content = m.get("content");
		String text = content.isTextual() ? content.asText() : content.toString();
		if (buf.length() > 0) {
			buf.append("\n\n");
		}
		buf.append(text);
	}

	private CanonicalMessage buildUserMessage(JsonNode m) throws LLMTransformException {
		List<CanonicalContentBlock> blocks = new ArrayList<>();
		if (m.hasNonNull("content")) {
			populateUserContent(m.get("content"), blocks);
		}
		return new CanonicalMessage(CanonicalRole.USER, blocks);
	}

	private void populateUserContent(JsonNode content, List<CanonicalContentBlock> blocks) throws LLMTransformException {
		if (content.isTextual()) {
			blocks.add(new CanonicalTextBlock(content.asText()));
			return;
		}
		if (!content.isArray()) {
			throw new LLMTransformException("Content OpenAI per role=user deve essere stringa o array");
		}
		for (JsonNode part : content) {
			addUserContentBlock(part, blocks);
		}
	}

	private void addUserContentBlock(JsonNode part, List<CanonicalContentBlock> blocks) throws LLMTransformException {
		String type = part.hasNonNull("type") ? part.get("type").asText() : null;
		if ("text".equals(type) && part.hasNonNull("text")) {
			blocks.add(new CanonicalTextBlock(part.get("text").asText()));
			return;
		}
		throw new LLMTransformException("Tipo di content OpenAI non supportato nel prototipo: " + type);
	}

	private CanonicalMessage buildAssistantMessage(JsonNode m) throws LLMTransformException {
		List<CanonicalContentBlock> blocks = new ArrayList<>();
		appendAssistantText(m, blocks);
		appendAssistantToolCalls(m, blocks);
		return new CanonicalMessage(CanonicalRole.ASSISTANT, blocks);
	}

	private void appendAssistantText(JsonNode m, List<CanonicalContentBlock> blocks) {
		if (!m.hasNonNull("content")) {
			return;
		}
		JsonNode content = m.get("content");
		if (!content.isTextual()) {
			return;
		}
		String text = content.asText();
		if (!text.isEmpty()) {
			blocks.add(new CanonicalTextBlock(text));
		}
	}

	private void appendAssistantToolCalls(JsonNode m, List<CanonicalContentBlock> blocks) throws LLMTransformException {
		if (!m.hasNonNull("tool_calls") || !m.get("tool_calls").isArray()) {
			return;
		}
		for (JsonNode tc : m.get("tool_calls")) {
			blocks.add(buildToolUseBlock(tc));
		}
	}

	private CanonicalToolUseBlock buildToolUseBlock(JsonNode tc) throws LLMTransformException {
		String id = tc.hasNonNull("id") ? tc.get("id").asText() : null;
		JsonNode function = tc.get("function");
		if (function == null) {
			throw new LLMTransformException("tool_calls OpenAI senza campo 'function'");
		}
		String name = function.hasNonNull("name") ? function.get("name").asText() : null;
		Map<String, Object> input = parseFunctionArguments(function);
		return new CanonicalToolUseBlock(id, name, input);
	}

	private Map<String, Object> parseFunctionArguments(JsonNode function) throws LLMTransformException {
		if (!function.hasNonNull("arguments")) {
			return null;
		}
		String argsRaw = function.get("arguments").asText();
		if (argsRaw == null || argsRaw.isEmpty()) {
			return null;
		}
		try {
			return JSONUtils.getObjectMapper().readValue(argsRaw, MAP_TYPE);
		} catch (Exception e) {
			throw new LLMTransformException("arguments del tool_call non è JSON valido: " + e.getMessage(), e);
		}
	}

	private CanonicalMessage buildToolResultMessage(JsonNode m) throws LLMTransformException {
		String toolCallId = m.hasNonNull("tool_call_id") ? m.get("tool_call_id").asText() : null;
		if (toolCallId == null) {
			throw new LLMTransformException("Messaggio role=tool senza 'tool_call_id'");
		}
		String content = extractToolResultContent(m);
		List<CanonicalContentBlock> blocks = new ArrayList<>();
		blocks.add(new CanonicalToolResultBlock(toolCallId, content, null));
		return new CanonicalMessage(CanonicalRole.USER, blocks);
	}

	private String extractToolResultContent(JsonNode m) {
		if (!m.hasNonNull("content")) {
			return null;
		}
		JsonNode c = m.get("content");
		return c.isTextual() ? c.asText() : c.toString();
	}


	/* === tools === */

	private CanonicalTool buildTool(JsonNode t, ObjectMapper mapper) throws LLMTransformException {
		JsonNode function = t.get("function");
		if (function == null) {
			throw new LLMTransformException("Tool OpenAI senza campo 'function'");
		}
		String name = function.hasNonNull("name") ? function.get("name").asText() : null;
		String description = function.hasNonNull("description") ? function.get("description").asText() : null;
		Map<String, Object> parameters = null;
		if (function.hasNonNull("parameters")) {
			parameters = mapper.convertValue(function.get("parameters"), MAP_TYPE);
		}
		return new CanonicalTool(name, description, parameters);
	}
}
