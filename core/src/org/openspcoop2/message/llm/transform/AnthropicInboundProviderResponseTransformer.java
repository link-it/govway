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
		if (root.hasNonNull("id")) {
			out.setId(root.get("id").asText());
		}
		if (root.hasNonNull("model")) {
			out.setModel(root.get("model").asText());
		}
		applyStopReason(root, out);
		applyContent(root, out, mapper);
		applyUsage(root, out);
		return out;
	}


	/* === sezioni della response === */

	private void applyStopReason(JsonNode root, CanonicalChatResponse out) {
		if (root.hasNonNull("stop_reason")) {
			CanonicalStopReason reason = CanonicalStopReason.tryFromValue(root.get("stop_reason").asText());
			if (reason != null) {
				out.setStopReason(reason);
			}
		}
		if (root.hasNonNull("stop_sequence")) {
			out.setStopSequence(root.get("stop_sequence").asText());
		}
	}

	private void applyContent(JsonNode root, CanonicalChatResponse out, ObjectMapper mapper) throws LLMTransformException {
		if (!root.hasNonNull("content") || !root.get("content").isArray()) {
			return;
		}
		List<CanonicalContentBlock> blocks = new ArrayList<>();
		for (JsonNode part : root.get("content")) {
			blocks.add(buildBlock(part, mapper));
		}
		out.setContent(blocks);
	}

	private CanonicalContentBlock buildBlock(JsonNode part, ObjectMapper mapper) throws LLMTransformException {
		String type = part.hasNonNull("type") ? part.get("type").asText() : null;
		if (type == null) {
			throw new LLMTransformException("Blocco Anthropic response senza 'type'");
		}
		switch (type) {
			case "text":
				return new CanonicalTextBlock(part.hasNonNull("text") ? part.get("text").asText() : "");
			case "tool_use":
				return buildToolUse(part, mapper);
			default:
				throw new LLMTransformException("Tipo blocco response Anthropic non supportato nel prototipo: " + type);
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
				throw new LLMTransformException("input del tool_use response non convertibile in Map: " + e.getMessage(), e);
			}
		}
		return new CanonicalToolUseBlock(id, name, input);
	}

	private void applyUsage(JsonNode root, CanonicalChatResponse out) {
		if (!root.hasNonNull("usage")) {
			return;
		}
		JsonNode usage = root.get("usage");
		Integer in = usage.hasNonNull("input_tokens") ? usage.get("input_tokens").asInt() : null;
		Integer outt = usage.hasNonNull("output_tokens") ? usage.get("output_tokens").asInt() : null;
		out.setUsage(new CanonicalUsage(in, outt));
	}
}
