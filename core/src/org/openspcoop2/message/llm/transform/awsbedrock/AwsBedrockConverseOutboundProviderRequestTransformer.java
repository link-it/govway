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
import java.util.Collections;
import java.util.List;

import org.openspcoop2.message.llm.CanonicalChatRequest;
import org.openspcoop2.message.llm.CanonicalContentBlock;
import org.openspcoop2.message.llm.CanonicalMessage;
import org.openspcoop2.message.llm.CanonicalTextBlock;
import org.openspcoop2.message.llm.CanonicalToolResultBlock;
import org.openspcoop2.message.llm.CanonicalToolUseBlock;
import org.openspcoop2.message.llm.stream.LLMProviderStreamTransport;
import org.openspcoop2.message.llm.transform.LLMOutboundProviderRequestTransformer;
import org.openspcoop2.message.llm.transform.LLMProviderRequest;
import org.openspcoop2.message.llm.transform.LLMProviders;
import org.openspcoop2.message.llm.transform.LLMTransformException;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.TransportUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Trasformatore outbound back-door: CanonicalChatRequest → JSON AWS Bedrock Converse API.
 * <p>
 * Bedrock Converse non condivide la forma block-based di Anthropic: i field sono camelCase,
 * gli inference parameters sono raggruppati in {@code inferenceConfig}, il system prompt
 * è un array di {@code {"text": "..."}} top-level, il modelId va nel path
 * ({@code /model/{modelId}/converse}) e NON nel body. La firma SigV4 è applicata dal
 * connettore (vedi Token Policy AWS V4), non da questo trasformatore.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class AwsBedrockConverseOutboundProviderRequestTransformer implements LLMOutboundProviderRequestTransformer {

	/** Default applicato se la request canonical non specifica max_tokens. */
	private static final int DEFAULT_MAX_TOKENS = 4096;

	@Override
	public String getProviderId() {
		return LLMProviders.AWS_BEDROCK;
	}

	/**
	 * Bedrock instrada il modello nel path: {@code /model/{modelId}/converse} (o
	 * {@code /converse-stream} se streaming). Il {@code modelId} arriva dal canonical
	 * {@code request.getModel()}; eventuale routing/alias resolution è già stato fatto
	 * a monte dagli handler GovWay (front-door → canonical).
	 */
	@Override
	public String getProviderResourcePath(CanonicalChatRequest request) {
		String modelId = (request != null && request.getModel() != null) ? request.getModel() : "";
		boolean streaming = request != null && Boolean.TRUE.equals(request.getStream());
		String suffix = streaming ? AwsBedrockConverseFields.RESOURCE_PATH_SUFFIX_STREAM
				: AwsBedrockConverseFields.RESOURCE_PATH_SUFFIX_SYNC;
		// I modelId Bedrock contengono ':' (es. anthropic.claude-3-5-haiku-20241022-v1:0) che
		// va URL-encoded come %3A nel path: AWS Bedrock altrimenti tronca il modelId al primo ':'.
		String encodedModelId = TransportUtils.urlEncodePath(modelId, Charset.UTF_8.getValue());
		return AwsBedrockConverseFields.RESOURCE_PATH_PREFIX + encodedModelId + suffix;
	}

	@Override
	public LLMProviderStreamTransport getProviderStreamTransport() {
		return LLMProviderStreamTransport.AWS_EVENT_STREAM;
	}

	@Override
	public LLMProviderRequest transform(CanonicalChatRequest request) throws LLMTransformException {
		if (request == null) {
			throw new LLMTransformException("CanonicalChatRequest nullo");
		}
		try {
			ObjectMapper mapper = JSONUtils.getObjectMapper();
			ObjectNode out = mapper.createObjectNode();

			out.set(AwsBedrockConverseFields.FIELD_MESSAGES, buildMessages(mapper, request.getMessages()));
			if (request.getSystem() != null && !request.getSystem().isEmpty()) {
				out.set(AwsBedrockConverseFields.FIELD_SYSTEM, buildSystem(mapper, request.getSystem()));
			}
			ObjectNode inference = buildInferenceConfig(mapper, request);
			if (inference != null) {
				out.set(AwsBedrockConverseFields.FIELD_INFERENCE_CONFIG, inference);
			}

			byte[] body = mapper.writeValueAsBytes(out);
			// Nessun header statico: Content-Type lo imposta GovWay; Authorization arriva via SigV4 dalla Token Policy.
			return new LLMProviderRequest(body, Collections.emptyMap());
		} catch (LLMTransformException e) {
			throw e;
		} catch (Exception e) {
			throw new LLMTransformException("Errore nella serializzazione canonical → Bedrock Converse: " + e.getMessage(), e);
		}
	}


	/* === sezioni del body === */

	private ArrayNode buildMessages(ObjectMapper mapper, List<CanonicalMessage> messages) throws LLMTransformException {
		ArrayNode array = mapper.createArrayNode();
		if (messages == null) {
			return array;
		}
		for (CanonicalMessage m : messages) {
			ObjectNode msg = mapper.createObjectNode();
			if (m.getRole() != null) {
				msg.put(AwsBedrockConverseFields.FIELD_ROLE, m.getRole().getValue());
			}
			msg.set(AwsBedrockConverseFields.FIELD_CONTENT, buildContentBlocks(mapper, m.getContent()));
			array.add(msg);
		}
		return array;
	}

	private ArrayNode buildContentBlocks(ObjectMapper mapper, List<CanonicalContentBlock> blocks) throws LLMTransformException {
		ArrayNode out = mapper.createArrayNode();
		if (blocks == null) {
			return out;
		}
		for (CanonicalContentBlock b : blocks) {
			out.add(buildContentBlock(mapper, b));
		}
		return out;
	}

	private ObjectNode buildContentBlock(ObjectMapper mapper, CanonicalContentBlock b) throws LLMTransformException {
		if (b instanceof CanonicalTextBlock) {
			ObjectNode node = mapper.createObjectNode();
			node.put(AwsBedrockConverseFields.BLOCK_TEXT, ((CanonicalTextBlock) b).getText());
			return node;
		}
		if (b instanceof CanonicalToolUseBlock) {
			CanonicalToolUseBlock t = (CanonicalToolUseBlock) b;
			ObjectNode inner = mapper.createObjectNode();
			if (t.getId() != null) {
				inner.put(AwsBedrockConverseFields.FIELD_TOOL_USE_ID, t.getId());
			}
			if (t.getName() != null) {
				inner.put(AwsBedrockConverseFields.FIELD_TOOL_USE_NAME, t.getName());
			}
			if (t.getInput() != null) {
				inner.set(AwsBedrockConverseFields.FIELD_TOOL_USE_INPUT, mapper.valueToTree(t.getInput()));
			}
			ObjectNode node = mapper.createObjectNode();
			node.set(AwsBedrockConverseFields.BLOCK_TOOL_USE, inner);
			return node;
		}
		if (b instanceof CanonicalToolResultBlock) {
			// Schema toolResult Bedrock: { "toolUseId": "...", "content": [{"text": "..."}] }.
			// Per il prototipo non lo emettiamo: i clienti correnti non lo usano in input.
			throw new LLMTransformException("CanonicalToolResultBlock non ancora supportato in outbound Bedrock");
		}
		throw new LLMTransformException("Tipo blocco canonical non supportato per Bedrock: " + b.getClass().getSimpleName());
	}

	private ArrayNode buildSystem(ObjectMapper mapper, String system) {
		ArrayNode array = mapper.createArrayNode();
		ObjectNode entry = mapper.createObjectNode();
		entry.put(AwsBedrockConverseFields.BLOCK_TEXT, system);
		array.add(entry);
		return array;
	}

	private ObjectNode buildInferenceConfig(ObjectMapper mapper, CanonicalChatRequest request) {
		ObjectNode inference = mapper.createObjectNode();
		Integer maxTokens = request.getMaxTokens() != null ? request.getMaxTokens() : DEFAULT_MAX_TOKENS;
		inference.put(AwsBedrockConverseFields.FIELD_MAX_TOKENS, maxTokens);
		if (request.getTemperature() != null) {
			inference.put(AwsBedrockConverseFields.FIELD_TEMPERATURE, request.getTemperature());
		}
		if (request.getTopP() != null) {
			inference.put(AwsBedrockConverseFields.FIELD_TOP_P, request.getTopP());
		}
		if (request.getStopSequences() != null && !request.getStopSequences().isEmpty()) {
			ArrayNode stops = mapper.createArrayNode();
			List<String> seqs = new ArrayList<>(request.getStopSequences());
			for (String s : seqs) {
				stops.add(s);
			}
			inference.set(AwsBedrockConverseFields.FIELD_STOP_SEQUENCES, stops);
		}
		return inference;
	}
}
