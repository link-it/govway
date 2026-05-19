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
package org.openspcoop2.message.llm.transform.openai;

import org.openspcoop2.message.llm.transform.LLMInboundProviderResponseTransformer;
import org.openspcoop2.message.llm.transform.LLMProviders;
import org.openspcoop2.message.llm.transform.LLMTransformException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.message.llm.CanonicalChatResponse;
import org.openspcoop2.message.llm.CanonicalContentBlock;
import org.openspcoop2.message.llm.CanonicalStopReason;
import org.openspcoop2.message.llm.CanonicalTextBlock;
import org.openspcoop2.message.llm.CanonicalToolUseBlock;
import org.openspcoop2.message.llm.CanonicalUsage;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Trasformatore inbound provider: response JSON OpenAI Chat Completions → CanonicalChatResponse.
 *
 * Mapping (sync, non-streaming):
 * <ul>
 *   <li>{@code choices[0].message.content} (string) → CanonicalTextBlock</li>
 *   <li>{@code choices[0].message.tool_calls[]} → CanonicalToolUseBlock con
 *       {@code function.arguments} deserializzato da stringa JSON a Map</li>
 *   <li>{@code finish_reason}: "stop"→END_TURN, "length"→MAX_TOKENS, "tool_calls"→TOOL_USE</li>
 *   <li>{@code usage.prompt_tokens}→inputTokens, {@code usage.completion_tokens}→outputTokens</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class OpenAIChatInboundProviderResponseTransformer implements LLMInboundProviderResponseTransformer {

	private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {};

	@Override
	public String getProviderId() {
		return LLMProviders.OPENAI;
	}

	@Override
	public CanonicalChatResponse transform(byte[] payload) throws LLMTransformException {
		if (payload == null || payload.length == 0) {
			throw new LLMTransformException("Payload OpenAI Chat Completions response vuoto");
		}
		try {
			ObjectMapper mapper = JSONUtils.getObjectMapper();
			JsonNode root = mapper.readTree(payload);
			return parse(root, mapper);
		} catch (LLMTransformException e) {
			throw e;
		} catch (Exception e) {
			throw new LLMTransformException("Errore nel parsing della response OpenAI Chat Completions: " + e.getMessage(), e);
		}
	}

	private CanonicalChatResponse parse(JsonNode root, ObjectMapper mapper) throws LLMTransformException {
		CanonicalChatResponse out = new CanonicalChatResponse();
		if (root.hasNonNull(OpenAIChatFields.FIELD_ID)) {
			out.setId(root.get(OpenAIChatFields.FIELD_ID).asText());
		}
		if (root.hasNonNull(OpenAIChatFields.FIELD_MODEL)) {
			out.setModel(root.get(OpenAIChatFields.FIELD_MODEL).asText());
		}
		applyChoice(root, out, mapper);
		applyUsage(root, out);
		return out;
	}


	/* === choices[0] === */

	private void applyChoice(JsonNode root, CanonicalChatResponse out, ObjectMapper mapper) throws LLMTransformException {
		if (!root.hasNonNull(OpenAIChatFields.FIELD_CHOICES)
				|| !root.get(OpenAIChatFields.FIELD_CHOICES).isArray()
				|| root.get(OpenAIChatFields.FIELD_CHOICES).isEmpty()) {
			return;
		}
		JsonNode choice = root.get(OpenAIChatFields.FIELD_CHOICES).get(0);
		applyFinishReason(choice, out);
		applyMessage(choice, out, mapper);
	}

	private void applyFinishReason(JsonNode choice, CanonicalChatResponse out) {
		if (!choice.hasNonNull(OpenAIChatFields.FIELD_FINISH_REASON)) {
			return;
		}
		String fr = choice.get(OpenAIChatFields.FIELD_FINISH_REASON).asText();
		CanonicalStopReason mapped = mapFinishReason(fr);
		if (mapped != null) {
			out.setStopReason(mapped);
		}
	}

	private CanonicalStopReason mapFinishReason(String fr) {
		if (fr == null) {
			return null;
		}
		switch (fr) {
			case OpenAIChatFields.FINISH_REASON_STOP:
				return CanonicalStopReason.END_TURN;
			case OpenAIChatFields.FINISH_REASON_LENGTH:
				return CanonicalStopReason.MAX_TOKENS;
			case OpenAIChatFields.FINISH_REASON_TOOL_CALLS:
			case OpenAIChatFields.FINISH_REASON_FUNCTION_CALL:
				return CanonicalStopReason.TOOL_USE;
			default:
				return null;
		}
	}

	private void applyMessage(JsonNode choice, CanonicalChatResponse out, ObjectMapper mapper) throws LLMTransformException {
		if (!choice.hasNonNull(OpenAIChatFields.FIELD_MESSAGE)) {
			return;
		}
		JsonNode message = choice.get(OpenAIChatFields.FIELD_MESSAGE);
		List<CanonicalContentBlock> blocks = new ArrayList<>();
		addContentBlock(message, blocks);
		addToolCallBlocks(message, blocks, mapper);
		if (!blocks.isEmpty()) {
			out.setContent(blocks);
		}
	}

	private void addContentBlock(JsonNode message, List<CanonicalContentBlock> blocks) {
		if (!message.hasNonNull(OpenAIChatFields.FIELD_CONTENT)) {
			return;
		}
		JsonNode content = message.get(OpenAIChatFields.FIELD_CONTENT);
		if (content.isTextual()) {
			String text = content.asText();
			if (!text.isEmpty()) {
				blocks.add(new CanonicalTextBlock(text));
			}
		}
		// content array (vision o multi-part) non gestito nel prototipo
	}

	private void addToolCallBlocks(JsonNode message, List<CanonicalContentBlock> blocks, ObjectMapper mapper) throws LLMTransformException {
		if (!message.hasNonNull(OpenAIChatFields.FIELD_TOOL_CALLS)
				|| !message.get(OpenAIChatFields.FIELD_TOOL_CALLS).isArray()) {
			return;
		}
		for (JsonNode tc : message.get(OpenAIChatFields.FIELD_TOOL_CALLS)) {
			blocks.add(buildToolUse(tc, mapper));
		}
	}

	private CanonicalToolUseBlock buildToolUse(JsonNode tc, ObjectMapper mapper) throws LLMTransformException {
		String id = tc.hasNonNull(OpenAIChatFields.FIELD_ID) ? tc.get(OpenAIChatFields.FIELD_ID).asText() : null;
		String name = null;
		Map<String, Object> input = null;
		if (tc.hasNonNull(OpenAIChatFields.FIELD_FUNCTION)) {
			JsonNode function = tc.get(OpenAIChatFields.FIELD_FUNCTION);
			if (function.hasNonNull(OpenAIChatFields.FIELD_NAME)) {
				name = function.get(OpenAIChatFields.FIELD_NAME).asText();
			}
			if (function.hasNonNull(OpenAIChatFields.FIELD_ARGUMENTS)) {
				input = parseArguments(function.get(OpenAIChatFields.FIELD_ARGUMENTS), mapper);
			}
		}
		return new CanonicalToolUseBlock(id, name, input);
	}

	private Map<String, Object> parseArguments(JsonNode arguments, ObjectMapper mapper) throws LLMTransformException {
		try {
			if (arguments.isTextual()) {
				String s = arguments.asText();
				if (s.isEmpty()) {
					return null;
				}
				return mapper.readValue(s, MAP_TYPE);
			}
			// alcune implementazioni OpenAI-compatible mandano già un object: gestito comunque
			return mapper.convertValue(arguments, MAP_TYPE);
		} catch (Exception e) {
			throw new LLMTransformException("arguments del tool_call non convertibili in Map: " + e.getMessage(), e);
		}
	}


	/* === usage === */

	private void applyUsage(JsonNode root, CanonicalChatResponse out) {
		if (!root.hasNonNull(OpenAIChatFields.FIELD_USAGE)) {
			return;
		}
		JsonNode usage = root.get(OpenAIChatFields.FIELD_USAGE);
		Integer in = usage.hasNonNull(OpenAIChatFields.FIELD_PROMPT_TOKENS) ? usage.get(OpenAIChatFields.FIELD_PROMPT_TOKENS).asInt() : null;
		Integer outt = usage.hasNonNull(OpenAIChatFields.FIELD_COMPLETION_TOKENS) ? usage.get(OpenAIChatFields.FIELD_COMPLETION_TOKENS).asInt() : null;
		out.setUsage(new CanonicalUsage(in, outt));
	}
}
