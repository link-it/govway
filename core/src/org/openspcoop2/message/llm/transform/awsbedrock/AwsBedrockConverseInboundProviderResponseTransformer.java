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
package org.openspcoop2.message.llm.transform.awsbedrock;

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
 * Trasformatore inbound back-door: response JSON AWS Bedrock Converse → CanonicalChatResponse.
 *
 * <p>Coperture del prototipo:</p>
 * <ul>
 *   <li>{@code output.message.content[]} con blocchi {@code text} e {@code toolUse}</li>
 *   <li>{@code stopReason}: lenient sui valori sconosciuti (es. {@code guardrail_intervened},
 *       {@code content_filtered}) — ritornano null senza fallire</li>
 *   <li>{@code usage.inputTokens} / {@code usage.outputTokens}</li>
 * </ul>
 * <p>Bedrock NON restituisce {@code id} né {@code model}: i campi corrispondenti del canonical
 * restano null.</p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class AwsBedrockConverseInboundProviderResponseTransformer implements LLMInboundProviderResponseTransformer {

	private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {};

	@Override
	public String getProviderId() {
		return LLMProviders.AWS_BEDROCK;
	}

	@Override
	public CanonicalChatResponse transform(byte[] payload) throws LLMTransformException {
		if (payload == null || payload.length == 0) {
			throw new LLMTransformException("Payload Bedrock Converse response vuoto");
		}
		try {
			ObjectMapper mapper = JSONUtils.getObjectMapper();
			JsonNode root = mapper.readTree(payload);
			return parse(root, mapper);
		} catch (LLMTransformException e) {
			throw e;
		} catch (Exception e) {
			throw new LLMTransformException("Errore nel parsing della response Bedrock Converse: " + e.getMessage(), e);
		}
	}

	private CanonicalChatResponse parse(JsonNode root, ObjectMapper mapper) throws LLMTransformException {
		CanonicalChatResponse out = new CanonicalChatResponse();
		applyStopReason(root, out);
		applyContent(root, out, mapper);
		applyUsage(root, out);
		return out;
	}


	/* === sezioni della response === */

	private void applyStopReason(JsonNode root, CanonicalChatResponse out) {
		if (root.hasNonNull(AwsBedrockConverseFields.FIELD_STOP_REASON)) {
			CanonicalStopReason reason = CanonicalStopReason.tryFromValue(root.get(AwsBedrockConverseFields.FIELD_STOP_REASON).asText());
			if (reason != null) {
				out.setStopReason(reason);
			}
		}
	}

	private void applyContent(JsonNode root, CanonicalChatResponse out, ObjectMapper mapper) throws LLMTransformException {
		if (!root.hasNonNull(AwsBedrockConverseFields.FIELD_OUTPUT)) {
			return;
		}
		JsonNode output = root.get(AwsBedrockConverseFields.FIELD_OUTPUT);
		if (!output.hasNonNull(AwsBedrockConverseFields.FIELD_MESSAGE)) {
			return;
		}
		JsonNode message = output.get(AwsBedrockConverseFields.FIELD_MESSAGE);
		if (!message.hasNonNull(AwsBedrockConverseFields.FIELD_CONTENT) || !message.get(AwsBedrockConverseFields.FIELD_CONTENT).isArray()) {
			return;
		}
		List<CanonicalContentBlock> blocks = new ArrayList<>();
		for (JsonNode part : message.get(AwsBedrockConverseFields.FIELD_CONTENT)) {
			blocks.add(buildBlock(part, mapper));
		}
		out.setContent(blocks);
	}

	private CanonicalContentBlock buildBlock(JsonNode part, ObjectMapper mapper) throws LLMTransformException {
		if (part.hasNonNull(AwsBedrockConverseFields.BLOCK_TEXT)) {
			return new CanonicalTextBlock(part.get(AwsBedrockConverseFields.BLOCK_TEXT).asText());
		}
		if (part.hasNonNull(AwsBedrockConverseFields.BLOCK_TOOL_USE)) {
			return buildToolUse(part.get(AwsBedrockConverseFields.BLOCK_TOOL_USE), mapper);
		}
		throw new LLMTransformException("Tipo blocco response Bedrock non supportato nel prototipo: " + part.toString());
	}

	private CanonicalToolUseBlock buildToolUse(JsonNode toolUse, ObjectMapper mapper) throws LLMTransformException {
		String id = toolUse.hasNonNull(AwsBedrockConverseFields.FIELD_TOOL_USE_ID) ? toolUse.get(AwsBedrockConverseFields.FIELD_TOOL_USE_ID).asText() : null;
		String name = toolUse.hasNonNull(AwsBedrockConverseFields.FIELD_TOOL_USE_NAME) ? toolUse.get(AwsBedrockConverseFields.FIELD_TOOL_USE_NAME).asText() : null;
		Map<String, Object> input = null;
		if (toolUse.hasNonNull(AwsBedrockConverseFields.FIELD_TOOL_USE_INPUT)) {
			try {
				input = mapper.convertValue(toolUse.get(AwsBedrockConverseFields.FIELD_TOOL_USE_INPUT), MAP_TYPE);
			} catch (Exception e) {
				throw new LLMTransformException("input del toolUse response Bedrock non convertibile in Map: " + e.getMessage(), e);
			}
		}
		return new CanonicalToolUseBlock(id, name, input);
	}

	private void applyUsage(JsonNode root, CanonicalChatResponse out) {
		if (!root.hasNonNull(AwsBedrockConverseFields.FIELD_USAGE)) {
			return;
		}
		JsonNode usage = root.get(AwsBedrockConverseFields.FIELD_USAGE);
		Integer in = usage.hasNonNull(AwsBedrockConverseFields.FIELD_INPUT_TOKENS) ? usage.get(AwsBedrockConverseFields.FIELD_INPUT_TOKENS).asInt() : null;
		Integer outt = usage.hasNonNull(AwsBedrockConverseFields.FIELD_OUTPUT_TOKENS) ? usage.get(AwsBedrockConverseFields.FIELD_OUTPUT_TOKENS).asInt() : null;
		out.setUsage(new CanonicalUsage(in, outt));
	}
}
