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

import java.util.Collections;
import java.util.List;

import org.openspcoop2.message.llm.CanonicalRole;
import org.openspcoop2.message.llm.CanonicalStopReason;
import org.openspcoop2.message.llm.CanonicalUsage;
import org.openspcoop2.message.llm.stream.CanonicalStreamContentBlockStart;
import org.openspcoop2.message.llm.stream.CanonicalStreamContentBlockStop;
import org.openspcoop2.message.llm.stream.CanonicalStreamEvent;
import org.openspcoop2.message.llm.stream.CanonicalStreamMessageDelta;
import org.openspcoop2.message.llm.stream.CanonicalStreamMessageStart;
import org.openspcoop2.message.llm.stream.CanonicalStreamMessageStop;
import org.openspcoop2.message.llm.stream.CanonicalStreamPing;
import org.openspcoop2.message.llm.stream.CanonicalStreamTextDelta;
import org.openspcoop2.message.llm.stream.CanonicalStreamToolUseDelta;
import org.openspcoop2.message.llm.stream.LLMProviderRawChunk;
import org.openspcoop2.message.llm.transform.LLMInboundProviderChunkDecoder;
import org.openspcoop2.message.llm.transform.LLMProviders;
import org.openspcoop2.message.llm.transform.LLMTransformException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Decoder semantico Anthropic Messages streaming → canonical stream events.
 * <p>
 * Eventi Anthropic gestiti:
 * </p>
 * <ul>
 *   <li>{@code message_start} → {@link CanonicalStreamMessageStart}
 *       (estrae id, model, role, usage iniziale)</li>
 *   <li>{@code content_block_start} → {@link CanonicalStreamContentBlockStart}
 *       (per blocchi tool_use copia id e name)</li>
 *   <li>{@code ping} → {@link CanonicalStreamPing}</li>
 *   <li>{@code content_block_delta} con {@code text_delta} → {@link CanonicalStreamTextDelta}</li>
 *   <li>{@code content_block_delta} con {@code input_json_delta} → {@link CanonicalStreamToolUseDelta}</li>
 *   <li>{@code content_block_stop} → {@link CanonicalStreamContentBlockStop}</li>
 *   <li>{@code message_delta} → {@link CanonicalStreamMessageDelta}
 *       (stop_reason + usage finale)</li>
 *   <li>{@code message_stop} → {@link CanonicalStreamMessageStop}</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class AnthropicMessagesChunkDecoder implements LLMInboundProviderChunkDecoder {

	@Override
	public String getProviderId() {
		return LLMProviders.ANTHROPIC;
	}

	@Override
	public List<CanonicalStreamEvent> decode(LLMProviderRawChunk chunk) throws LLMTransformException {
		if (chunk == null) {
			return Collections.emptyList();
		}
		String eventType = chunk.getEventType();
		String payload = chunk.getDataPayload();
		if (eventType == null) {
			// chunk SSE senza event: type — Anthropic emette sempre event:, quindi
			// ignoriamo eventuali keepalive senza tipo.
			return Collections.emptyList();
		}
		try {
			JsonNode root = (payload != null && !payload.isEmpty())
					? JSONUtils.getObjectMapper().readTree(payload)
					: null;
			return decodeByEventType(eventType, root);
		} catch (LLMTransformException e) {
			throw e;
		} catch (Exception e) {
			throw new LLMTransformException("Errore nel parsing del chunk Anthropic (event=" + eventType + "): " + e.getMessage(), e);
		}
	}

	private List<CanonicalStreamEvent> decodeByEventType(String eventType, JsonNode root) throws LLMTransformException {
		switch (eventType) {
			case AnthropicMessagesFields.EVENT_MESSAGE_START:
				return Collections.singletonList(decodeMessageStart(root));
			case AnthropicMessagesFields.EVENT_CONTENT_BLOCK_START:
				return Collections.singletonList(decodeContentBlockStart(root));
			case AnthropicMessagesFields.EVENT_PING:
				return Collections.<CanonicalStreamEvent>singletonList(new CanonicalStreamPing());
			case AnthropicMessagesFields.EVENT_CONTENT_BLOCK_DELTA:
				return decodeContentBlockDelta(root);
			case AnthropicMessagesFields.EVENT_CONTENT_BLOCK_STOP:
				return Collections.singletonList(decodeContentBlockStop(root));
			case AnthropicMessagesFields.EVENT_MESSAGE_DELTA:
				return Collections.singletonList(decodeMessageDelta(root));
			case AnthropicMessagesFields.EVENT_MESSAGE_STOP:
				return Collections.<CanonicalStreamEvent>singletonList(new CanonicalStreamMessageStop());
			default:
				// Eventi non riconosciuti: ignora silenziosamente per essere tolleranti
				// a estensioni Anthropic future.
				return Collections.emptyList();
		}
	}

	private CanonicalStreamMessageStart decodeMessageStart(JsonNode root) {
		CanonicalStreamMessageStart ev = new CanonicalStreamMessageStart();
		if (root == null || !root.hasNonNull(AnthropicMessagesFields.FIELD_MESSAGE)) {
			return ev;
		}
		JsonNode m = root.get(AnthropicMessagesFields.FIELD_MESSAGE);
		if (m.hasNonNull(AnthropicMessagesFields.FIELD_ID)) {
			ev.setId(m.get(AnthropicMessagesFields.FIELD_ID).asText());
		}
		if (m.hasNonNull(AnthropicMessagesFields.FIELD_MODEL)) {
			ev.setModel(m.get(AnthropicMessagesFields.FIELD_MODEL).asText());
		}
		if (m.hasNonNull(AnthropicMessagesFields.FIELD_ROLE)) {
			ev.setRole(CanonicalRole.tryFromValue(m.get(AnthropicMessagesFields.FIELD_ROLE).asText()));
		}
		ev.setUsage(parseUsage(m.path(AnthropicMessagesFields.FIELD_USAGE)));
		return ev;
	}

	private CanonicalStreamContentBlockStart decodeContentBlockStart(JsonNode root) {
		CanonicalStreamContentBlockStart ev = new CanonicalStreamContentBlockStart();
		if (root == null) {
			return ev;
		}
		if (root.hasNonNull(AnthropicMessagesFields.FIELD_INDEX)) {
			ev.setIndex(root.get(AnthropicMessagesFields.FIELD_INDEX).asInt());
		}
		JsonNode b = root.path(AnthropicMessagesFields.FIELD_CONTENT_BLOCK);
		if (b.hasNonNull(AnthropicMessagesFields.FIELD_TYPE)) {
			ev.setBlockType(b.get(AnthropicMessagesFields.FIELD_TYPE).asText());
		}
		if (b.hasNonNull(AnthropicMessagesFields.FIELD_ID)) {
			ev.setToolUseId(b.get(AnthropicMessagesFields.FIELD_ID).asText());
		}
		if (b.hasNonNull(AnthropicMessagesFields.FIELD_NAME)) {
			ev.setToolUseName(b.get(AnthropicMessagesFields.FIELD_NAME).asText());
		}
		return ev;
	}

	private List<CanonicalStreamEvent> decodeContentBlockDelta(JsonNode root) {
		if (root == null) {
			return Collections.emptyList();
		}
		int index = root.path(AnthropicMessagesFields.FIELD_INDEX).asInt(0);
		JsonNode delta = root.path(AnthropicMessagesFields.FIELD_DELTA);
		String deltaType = delta.path(AnthropicMessagesFields.FIELD_TYPE).asText(null);
		if (AnthropicMessagesFields.DELTA_TYPE_TEXT.equals(deltaType)) {
			return Collections.<CanonicalStreamEvent>singletonList(
					new CanonicalStreamTextDelta(index, delta.path(AnthropicMessagesFields.FIELD_TEXT).asText("")));
		}
		if (AnthropicMessagesFields.DELTA_TYPE_INPUT_JSON.equals(deltaType)) {
			return Collections.<CanonicalStreamEvent>singletonList(
					new CanonicalStreamToolUseDelta(index, delta.path(AnthropicMessagesFields.FIELD_PARTIAL_JSON).asText("")));
		}
		// thinking_delta e altri delta type futuri: ignora silenziosamente
		return Collections.emptyList();
	}

	private CanonicalStreamContentBlockStop decodeContentBlockStop(JsonNode root) {
		int index = (root != null && root.hasNonNull(AnthropicMessagesFields.FIELD_INDEX)) ? root.get(AnthropicMessagesFields.FIELD_INDEX).asInt() : 0;
		return new CanonicalStreamContentBlockStop(index);
	}

	private CanonicalStreamMessageDelta decodeMessageDelta(JsonNode root) {
		CanonicalStreamMessageDelta ev = new CanonicalStreamMessageDelta();
		if (root == null) {
			return ev;
		}
		JsonNode delta = root.path(AnthropicMessagesFields.FIELD_DELTA);
		if (delta.hasNonNull(AnthropicMessagesFields.FIELD_STOP_REASON)) {
			ev.setStopReason(CanonicalStopReason.tryFromValue(delta.get(AnthropicMessagesFields.FIELD_STOP_REASON).asText()));
		}
		if (delta.hasNonNull(AnthropicMessagesFields.FIELD_STOP_SEQUENCE)) {
			ev.setStopSequence(delta.get(AnthropicMessagesFields.FIELD_STOP_SEQUENCE).asText());
		}
		ev.setUsage(parseUsage(root.path(AnthropicMessagesFields.FIELD_USAGE)));
		return ev;
	}

	private CanonicalUsage parseUsage(JsonNode usageNode) {
		if (usageNode == null || usageNode.isMissingNode() || usageNode.isNull()) {
			return null;
		}
		Integer in = usageNode.hasNonNull(AnthropicMessagesFields.FIELD_INPUT_TOKENS) ? usageNode.get(AnthropicMessagesFields.FIELD_INPUT_TOKENS).asInt() : null;
		Integer out = usageNode.hasNonNull(AnthropicMessagesFields.FIELD_OUTPUT_TOKENS) ? usageNode.get(AnthropicMessagesFields.FIELD_OUTPUT_TOKENS).asInt() : null;
		if (in == null && out == null) {
			return null;
		}
		return new CanonicalUsage(in, out);
	}

}
