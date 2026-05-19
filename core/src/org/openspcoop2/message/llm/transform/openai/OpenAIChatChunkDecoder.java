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

import org.openspcoop2.message.llm.transform.LLMInboundProviderChunkDecoder;
import org.openspcoop2.message.llm.transform.LLMProviders;
import org.openspcoop2.message.llm.transform.LLMTransformException;

import java.util.ArrayList;
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
import org.openspcoop2.message.llm.stream.CanonicalStreamTextDelta;
import org.openspcoop2.message.llm.stream.CanonicalStreamToolUseDelta;
import org.openspcoop2.message.llm.stream.LLMProviderRawChunk;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Decoder semantico OpenAI Chat Completions streaming → canonical stream events.
 * <p>
 * OpenAI emette tutti i chunk con lo stesso schema "chat.completion.chunk" e usa
 * sempre il framing SSE con event-type assente (data-only). Eventi gestiti:
 * </p>
 * <ul>
 *   <li>chunk con {@code delta.role:"assistant"} (primo chunk) → {@link CanonicalStreamMessageStart}
 *       (id e model presenti come campi top-level del chunk)</li>
 *   <li>chunk con {@code delta.content} (stringa) → {@link CanonicalStreamTextDelta}</li>
 *   <li>chunk con {@code delta.tool_calls[]} → {@link CanonicalStreamContentBlockStart} (al primo
 *       chunk del tool_call, quando arriva id e function.name) e {@link CanonicalStreamToolUseDelta}
 *       (per i frammenti successivi di function.arguments)</li>
 *   <li>chunk con {@code finish_reason} valorizzato → emette {@link CanonicalStreamContentBlockStop} per
 *       il blocco aperto e {@link CanonicalStreamMessageDelta} con stop_reason + usage,
 *       seguito da {@link CanonicalStreamMessageStop}</li>
 *   <li>payload {@code [DONE]} → ignorato (il transition message_stop è già stato emesso al finish_reason)</li>
 * </ul>
 * <p>
 * Il decoder è <strong>stateful</strong>: tiene traccia del primo chunk (per emettere
 * message_start una volta sola), dei tool_call iniziati (per emettere content_block_start
 * solo al primo chunk di ogni tool_call) e dell'eventuale blocco di testo aperto.
 * Una nuova istanza deve essere creata per ogni stream.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class OpenAIChatChunkDecoder implements LLMInboundProviderChunkDecoder {

	private boolean messageStartEmitted;
	private boolean textBlockOpened;
	private final java.util.Set<Integer> toolCallStartedIndexes = new java.util.HashSet<>();
	private final java.util.List<Integer> openContentBlockIndexes = new java.util.ArrayList<>();

	@Override
	public String getProviderId() {
		return LLMProviders.OPENAI;
	}

	@Override
	public List<CanonicalStreamEvent> decode(LLMProviderRawChunk chunk) throws LLMTransformException {
		if (chunk == null) {
			return Collections.emptyList();
		}
		String payload = chunk.getDataPayload();
		if (payload == null || payload.isEmpty()) {
			return Collections.emptyList();
		}
		if (OpenAIChatFields.STREAM_DONE_MARKER.equals(payload.trim())) {
			// la chiusura logica del messaggio è già stata emessa al finish_reason
			return Collections.emptyList();
		}
		try {
			JsonNode root = JSONUtils.getObjectMapper().readTree(payload);
			return decodeChunk(root);
		} catch (Exception e) {
			throw new LLMTransformException("Errore nel parsing del chunk OpenAI Chat Completions: " + e.getMessage(), e);
		}
	}


	/* === parsing del chunk === */

	private List<CanonicalStreamEvent> decodeChunk(JsonNode root) {
		List<CanonicalStreamEvent> events = new ArrayList<>();
		JsonNode choice = firstChoice(root);
		if (choice == null) {
			// chunk solo-usage / heartbeat: nulla da emettere
			return events;
		}
		maybeEmitMessageStart(root, choice, events);
		JsonNode delta = choice.path(OpenAIChatFields.FIELD_DELTA);
		decodeContentDelta(delta, events);
		decodeToolCallsDelta(delta, events);
		maybeEmitFinishReason(root, choice, events);
		return events;
	}

	private JsonNode firstChoice(JsonNode root) {
		if (!root.hasNonNull(OpenAIChatFields.FIELD_CHOICES)
				|| !root.get(OpenAIChatFields.FIELD_CHOICES).isArray()
				|| root.get(OpenAIChatFields.FIELD_CHOICES).isEmpty()) {
			return null;
		}
		return root.get(OpenAIChatFields.FIELD_CHOICES).get(0);
	}


	/* === message_start === */

	private void maybeEmitMessageStart(JsonNode root, JsonNode choice, List<CanonicalStreamEvent> events) {
		if (this.messageStartEmitted) {
			return;
		}
		this.messageStartEmitted = true;
		CanonicalStreamMessageStart ev = new CanonicalStreamMessageStart();
		if (root.hasNonNull(OpenAIChatFields.FIELD_ID)) {
			ev.setId(root.get(OpenAIChatFields.FIELD_ID).asText());
		}
		if (root.hasNonNull(OpenAIChatFields.FIELD_MODEL)) {
			ev.setModel(root.get(OpenAIChatFields.FIELD_MODEL).asText());
		}
		JsonNode delta = choice.path(OpenAIChatFields.FIELD_DELTA);
		if (delta.hasNonNull(OpenAIChatFields.FIELD_ROLE)) {
			ev.setRole(CanonicalRole.tryFromValue(delta.get(OpenAIChatFields.FIELD_ROLE).asText()));
		} else {
			ev.setRole(CanonicalRole.ASSISTANT);
		}
		events.add(ev);
	}


	/* === text delta === */

	private void decodeContentDelta(JsonNode delta, List<CanonicalStreamEvent> events) {
		if (!delta.hasNonNull(OpenAIChatFields.FIELD_CONTENT)) {
			return;
		}
		JsonNode content = delta.get(OpenAIChatFields.FIELD_CONTENT);
		if (!content.isTextual()) {
			// alcuni endpoint OpenAI-compatible mandano content come array di parti
			return;
		}
		String text = content.asText();
		if (text.isEmpty()) {
			return;
		}
		ensureTextBlockOpen(events);
		events.add(new CanonicalStreamTextDelta(textBlockIndex(), text));
	}

	private void ensureTextBlockOpen(List<CanonicalStreamEvent> events) {
		if (this.textBlockOpened) {
			return;
		}
		this.textBlockOpened = true;
		int index = textBlockIndex();
		CanonicalStreamContentBlockStart blockStart = new CanonicalStreamContentBlockStart(index, OpenAIChatFields.BLOCK_TYPE_TEXT);
		events.add(blockStart);
		this.openContentBlockIndexes.add(index);
	}

	private int textBlockIndex() {
		return 0;
	}


	/* === tool_use delta === */

	private void decodeToolCallsDelta(JsonNode delta, List<CanonicalStreamEvent> events) {
		if (!delta.hasNonNull(OpenAIChatFields.FIELD_TOOL_CALLS)
				|| !delta.get(OpenAIChatFields.FIELD_TOOL_CALLS).isArray()) {
			return;
		}
		for (JsonNode tc : delta.get(OpenAIChatFields.FIELD_TOOL_CALLS)) {
			int index = tc.hasNonNull(OpenAIChatFields.FIELD_INDEX) ? tc.get(OpenAIChatFields.FIELD_INDEX).asInt() : 0;
			// indici tool_use distinti dai blocchi di testo: traslo di +1 in canonical
			int canonicalIndex = index + 1;
			JsonNode function = tc.path(OpenAIChatFields.FIELD_FUNCTION);
			if (!this.toolCallStartedIndexes.contains(index)
					&& (tc.hasNonNull(OpenAIChatFields.FIELD_ID) || function.hasNonNull(OpenAIChatFields.FIELD_NAME))) {
				this.toolCallStartedIndexes.add(index);
				CanonicalStreamContentBlockStart blockStart = new CanonicalStreamContentBlockStart(canonicalIndex, OpenAIChatFields.BLOCK_TYPE_TOOL_USE);
				if (tc.hasNonNull(OpenAIChatFields.FIELD_ID)) {
					blockStart.setToolUseId(tc.get(OpenAIChatFields.FIELD_ID).asText());
				}
				if (function.hasNonNull(OpenAIChatFields.FIELD_NAME)) {
					blockStart.setToolUseName(function.get(OpenAIChatFields.FIELD_NAME).asText());
				}
				events.add(blockStart);
				this.openContentBlockIndexes.add(canonicalIndex);
			}
			if (function.hasNonNull(OpenAIChatFields.FIELD_ARGUMENTS)) {
				String partial = function.get(OpenAIChatFields.FIELD_ARGUMENTS).asText();
				if (!partial.isEmpty()) {
					events.add(new CanonicalStreamToolUseDelta(canonicalIndex, partial));
				}
			}
		}
	}


	/* === finish_reason / chiusura === */

	private void maybeEmitFinishReason(JsonNode root, JsonNode choice, List<CanonicalStreamEvent> events) {
		if (!choice.hasNonNull(OpenAIChatFields.FIELD_FINISH_REASON)) {
			return;
		}
		// emetto content_block_stop per i blocchi ancora aperti
		for (Integer idx : this.openContentBlockIndexes) {
			events.add(new CanonicalStreamContentBlockStop(idx));
		}
		this.openContentBlockIndexes.clear();

		CanonicalStreamMessageDelta md = new CanonicalStreamMessageDelta();
		md.setStopReason(mapFinishReason(choice.get(OpenAIChatFields.FIELD_FINISH_REASON).asText()));
		md.setUsage(parseUsage(root.path(OpenAIChatFields.FIELD_USAGE)));
		events.add(md);
		events.add(new CanonicalStreamMessageStop());
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

	private CanonicalUsage parseUsage(JsonNode usageNode) {
		if (usageNode == null || usageNode.isMissingNode() || usageNode.isNull()) {
			return null;
		}
		Integer in = usageNode.hasNonNull(OpenAIChatFields.FIELD_PROMPT_TOKENS) ? usageNode.get(OpenAIChatFields.FIELD_PROMPT_TOKENS).asInt() : null;
		Integer out = usageNode.hasNonNull(OpenAIChatFields.FIELD_COMPLETION_TOKENS) ? usageNode.get(OpenAIChatFields.FIELD_COMPLETION_TOKENS).asInt() : null;
		if (in == null && out == null) {
			return null;
		}
		return new CanonicalUsage(in, out);
	}

}
