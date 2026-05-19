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

import org.openspcoop2.message.llm.CanonicalChatResponse;
import org.openspcoop2.message.llm.CanonicalContentBlock;
import org.openspcoop2.message.llm.CanonicalStopReason;
import org.openspcoop2.message.llm.CanonicalTextBlock;
import org.openspcoop2.message.llm.CanonicalToolUseBlock;
import org.openspcoop2.message.llm.CanonicalUsage;
import org.openspcoop2.message.llm.transform.LLMInboundProviderResponseTransformer;
import org.openspcoop2.message.llm.transform.LLMProviders;
import org.openspcoop2.message.llm.transform.LLMTransformException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Trasformatore inbound provider: response JSON Anthropic Messages → CanonicalChatResponse.
 *
 * Coperture del prototipo:
 * <ul>
 *   <li>content blocks: text, tool_use (no thinking, no extended response types)</li>
 *   <li>stop_reason: lenient sui valori sconosciuti (ritorna null senza fallire)</li>
 *   <li>usage: input_tokens / output_tokens</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class AnthropicInboundProviderResponseTransformer implements LLMInboundProviderResponseTransformer {

	private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {};

	@Override
	public String getProviderId() {
		return LLMProviders.ANTHROPIC;
	}

	@Override
	public CanonicalChatResponse transform(byte[] payload) throws LLMTransformException {
		if (payload == null || payload.length == 0) {
			throw new LLMTransformException("Payload Anthropic Messages response vuoto");
		}
		try {
			ObjectMapper mapper = JSONUtils.getObjectMapper();
			JsonNode root = mapper.readTree(payload);
			return parse(root, mapper);
		} catch (LLMTransformException e) {
			throw e;
		} catch (Exception e) {
			throw new LLMTransformException("Errore nel parsing della response Anthropic Messages: " + e.getMessage(), e);
		}
	}

	private CanonicalChatResponse parse(JsonNode root, ObjectMapper mapper) throws LLMTransformException {
		CanonicalChatResponse out = new CanonicalChatResponse();
		if (root.hasNonNull(AnthropicMessagesFields.FIELD_ID)) {
			out.setId(root.get(AnthropicMessagesFields.FIELD_ID).asText());
		}
		if (root.hasNonNull(AnthropicMessagesFields.FIELD_MODEL)) {
			out.setModel(root.get(AnthropicMessagesFields.FIELD_MODEL).asText());
		}
		applyStopReason(root, out);
		applyContent(root, out, mapper);
		applyUsage(root, out);
		return out;
	}


	/* === sezioni della response === */

	private void applyStopReason(JsonNode root, CanonicalChatResponse out) {
		if (root.hasNonNull(AnthropicMessagesFields.FIELD_STOP_REASON)) {
			CanonicalStopReason reason = CanonicalStopReason.tryFromValue(root.get(AnthropicMessagesFields.FIELD_STOP_REASON).asText());
			if (reason != null) {
				out.setStopReason(reason);
			}
		}
		if (root.hasNonNull(AnthropicMessagesFields.FIELD_STOP_SEQUENCE)) {
			out.setStopSequence(root.get(AnthropicMessagesFields.FIELD_STOP_SEQUENCE).asText());
		}
	}

	private void applyContent(JsonNode root, CanonicalChatResponse out, ObjectMapper mapper) throws LLMTransformException {
		if (!root.hasNonNull(AnthropicMessagesFields.FIELD_CONTENT) || !root.get(AnthropicMessagesFields.FIELD_CONTENT).isArray()) {
			return;
		}
		List<CanonicalContentBlock> blocks = new ArrayList<>();
		for (JsonNode part : root.get(AnthropicMessagesFields.FIELD_CONTENT)) {
			blocks.add(buildBlock(part, mapper));
		}
		out.setContent(blocks);
	}

	private CanonicalContentBlock buildBlock(JsonNode part, ObjectMapper mapper) throws LLMTransformException {
		String type = part.hasNonNull(AnthropicMessagesFields.FIELD_TYPE) ? part.get(AnthropicMessagesFields.FIELD_TYPE).asText() : null;
		if (type == null) {
			throw new LLMTransformException("Blocco Anthropic response senza 'type'");
		}
		switch (type) {
			case AnthropicMessagesFields.BLOCK_TYPE_TEXT:
				return new CanonicalTextBlock(part.hasNonNull(AnthropicMessagesFields.FIELD_TEXT) ? part.get(AnthropicMessagesFields.FIELD_TEXT).asText() : "");
			case AnthropicMessagesFields.BLOCK_TYPE_TOOL_USE:
				return buildToolUse(part, mapper);
			default:
				throw new LLMTransformException("Tipo blocco response Anthropic non supportato nel prototipo: " + type);
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
				throw new LLMTransformException("input del tool_use response non convertibile in Map: " + e.getMessage(), e);
			}
		}
		return new CanonicalToolUseBlock(id, name, input);
	}

	private void applyUsage(JsonNode root, CanonicalChatResponse out) {
		if (!root.hasNonNull(AnthropicMessagesFields.FIELD_USAGE)) {
			return;
		}
		JsonNode usage = root.get(AnthropicMessagesFields.FIELD_USAGE);
		Integer in = usage.hasNonNull(AnthropicMessagesFields.FIELD_INPUT_TOKENS) ? usage.get(AnthropicMessagesFields.FIELD_INPUT_TOKENS).asInt() : null;
		Integer outt = usage.hasNonNull(AnthropicMessagesFields.FIELD_OUTPUT_TOKENS) ? usage.get(AnthropicMessagesFields.FIELD_OUTPUT_TOKENS).asInt() : null;
		out.setUsage(new CanonicalUsage(in, outt));
	}
}
