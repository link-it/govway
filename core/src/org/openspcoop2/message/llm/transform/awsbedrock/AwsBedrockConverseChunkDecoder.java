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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.openspcoop2.message.llm.transform.LLMInboundProviderChunkDecoder;
import org.openspcoop2.message.llm.transform.LLMProviders;
import org.openspcoop2.message.llm.transform.LLMTransformException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Decoder semantico dei chunk del transport AWS event-stream per Bedrock Converse Stream.
 * <p>
 * Mapping degli eventi Bedrock su {@link CanonicalStreamEvent}:
 * </p>
 * <table border="1">
 *   <tr><th>Bedrock event-type</th><th>Canonical chunk(s)</th></tr>
 *   <tr><td>{@code messageStart}</td><td>{@link CanonicalStreamMessageStart}</td></tr>
 *   <tr><td>{@code contentBlockStart}</td><td>{@link CanonicalStreamContentBlockStart} (solo per blocchi {@code toolUse})</td></tr>
 *   <tr><td>{@code contentBlockDelta}</td><td>{@link CanonicalStreamTextDelta} o {@link CanonicalStreamToolUseDelta}<br>
 *       per il primo delta {@code text} di un indice viene emesso un implicit {@link CanonicalStreamContentBlockStart}
 *       (Bedrock non lo manda per i blocchi text, lo aggiungiamo per uniformità con il flow Anthropic e per
 *       semplificare l'encoder front-door)</td></tr>
 *   <tr><td>{@code contentBlockStop}</td><td>{@link CanonicalStreamContentBlockStop}</td></tr>
 *   <tr><td>{@code messageStop}</td><td>{@link CanonicalStreamMessageDelta} (stopReason) + {@link CanonicalStreamMessageStop}</td></tr>
 *   <tr><td>{@code metadata}</td><td>{@link CanonicalStreamMessageDelta} (usage)</td></tr>
 * </table>
 *
 * <p>Stateful: tiene traccia degli indici di content block per i quali è già stato emesso
 * un implicit text-block-start, perciò il registry registra una factory (nuova istanza
 * per ogni stream).</p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class AwsBedrockConverseChunkDecoder implements LLMInboundProviderChunkDecoder {

	/* Event types emessi da Bedrock ConverseStream. */
	private static final String EVT_MESSAGE_START = "messageStart";
	private static final String EVT_CONTENT_BLOCK_START = "contentBlockStart";
	private static final String EVT_CONTENT_BLOCK_DELTA = "contentBlockDelta";
	private static final String EVT_CONTENT_BLOCK_STOP = "contentBlockStop";
	private static final String EVT_MESSAGE_STOP = "messageStop";
	private static final String EVT_METADATA = "metadata";

	/* Field names dei payload JSON degli eventi. */
	private static final String F_ROLE = "role";
	private static final String F_CONTENT_BLOCK_INDEX = "contentBlockIndex";
	private static final String F_START = "start";
	private static final String F_DELTA = "delta";
	private static final String F_TEXT = "text";
	private static final String F_TOOL_USE = "toolUse";
	private static final String F_TOOL_USE_ID = "toolUseId";
	private static final String F_NAME = "name";
	private static final String F_INPUT = "input";
	private static final String F_STOP_REASON = "stopReason";
	private static final String F_USAGE = "usage";
	private static final String F_INPUT_TOKENS = "inputTokens";
	private static final String F_OUTPUT_TOKENS = "outputTokens";

	/** Block type emesso nel CanonicalStreamContentBlockStart implicit per i blocchi text. */
	private static final String BLOCK_TYPE_TEXT = "text";
	private static final String BLOCK_TYPE_TOOL_USE = "tool_use";

	/** Indici dei content block per cui abbiamo già emesso un implicit text-block-start in questo stream. */
	private final Set<Integer> textBlockStartEmitted = new HashSet<>();

	@Override
	public String getProviderId() {
		return LLMProviders.AWS_BEDROCK;
	}

	@Override
	public List<CanonicalStreamEvent> decode(LLMProviderRawChunk chunk) throws LLMTransformException {
		if (chunk == null || chunk.getEventType() == null || chunk.getDataPayload() == null) {
			return Collections.emptyList();
		}
		String eventType = chunk.getEventType();
		try {
			ObjectMapper mapper = JSONUtils.getObjectMapper();
			JsonNode root = mapper.readTree(chunk.getDataPayload());
			switch (eventType) {
				case EVT_MESSAGE_START:
					return decodeMessageStart(root);
				case EVT_CONTENT_BLOCK_START:
					return decodeContentBlockStart(root);
				case EVT_CONTENT_BLOCK_DELTA:
					return decodeContentBlockDelta(root);
				case EVT_CONTENT_BLOCK_STOP:
					return decodeContentBlockStop(root);
				case EVT_MESSAGE_STOP:
					return decodeMessageStop(root);
				case EVT_METADATA:
					return decodeMetadata(root);
				default:
					// event-type sconosciuto: skip senza errore (forward compatibility)
					return Collections.emptyList();
			}
		} catch (Exception e) {
			throw new LLMTransformException("Errore nel decoding del chunk Bedrock event-type=" + eventType + ": " + e.getMessage(), e);
		}
	}


	/* === handlers per event-type === */

	private List<CanonicalStreamEvent> decodeMessageStart(JsonNode root) {
		CanonicalStreamMessageStart out = new CanonicalStreamMessageStart();
		if (root.hasNonNull(F_ROLE)) {
			out.setRole(CanonicalRole.tryFromValue(root.get(F_ROLE).asText()));
		}
		// Bedrock non manda id/model nel messageStart (id non esiste; model è nella URL della request)
		return Collections.singletonList(out);
	}

	private List<CanonicalStreamEvent> decodeContentBlockStart(JsonNode root) {
		int index = readContentBlockIndex(root);
		JsonNode start = root.get(F_START);
		if (start != null && start.has(F_TOOL_USE)) {
			JsonNode tu = start.get(F_TOOL_USE);
			CanonicalStreamContentBlockStart out = new CanonicalStreamContentBlockStart(index, BLOCK_TYPE_TOOL_USE);
			if (tu.hasNonNull(F_TOOL_USE_ID)) {
				out.setToolUseId(tu.get(F_TOOL_USE_ID).asText());
			}
			if (tu.hasNonNull(F_NAME)) {
				out.setToolUseName(tu.get(F_NAME).asText());
			}
			return Collections.singletonList(out);
		}
		// start sconosciuto: skip
		return Collections.emptyList();
	}

	private List<CanonicalStreamEvent> decodeContentBlockDelta(JsonNode root) {
		int index = readContentBlockIndex(root);
		JsonNode delta = root.get(F_DELTA);
		if (delta == null) {
			return Collections.emptyList();
		}
		if (delta.hasNonNull(F_TEXT)) {
			String text = delta.get(F_TEXT).asText();
			List<CanonicalStreamEvent> events = new ArrayList<>(2);
			// Bedrock non emette contentBlockStart per i blocchi text: emettiamo un implicit
			// una sola volta per indice, per uniformità con il flow Anthropic.
			if (this.textBlockStartEmitted.add(index)) {
				events.add(new CanonicalStreamContentBlockStart(index, BLOCK_TYPE_TEXT));
			}
			events.add(new CanonicalStreamTextDelta(index, text));
			return events;
		}
		if (delta.has(F_TOOL_USE)) {
			JsonNode tu = delta.get(F_TOOL_USE);
			String partial = tu.hasNonNull(F_INPUT) ? tu.get(F_INPUT).asText() : "";
			return Collections.singletonList((CanonicalStreamEvent) new CanonicalStreamToolUseDelta(index, partial));
		}
		return Collections.emptyList();
	}

	private List<CanonicalStreamEvent> decodeContentBlockStop(JsonNode root) {
		int index = readContentBlockIndex(root);
		return Collections.singletonList((CanonicalStreamEvent) new CanonicalStreamContentBlockStop(index));
	}

	private List<CanonicalStreamEvent> decodeMessageStop(JsonNode root) {
		CanonicalStopReason reason = root.hasNonNull(F_STOP_REASON)
				? CanonicalStopReason.tryFromValue(root.get(F_STOP_REASON).asText())
				: null;
		// Anthropic-style: prima un message_delta con stopReason, poi un message_stop "vuoto".
		List<CanonicalStreamEvent> events = new ArrayList<>(2);
		events.add(new CanonicalStreamMessageDelta(reason, null));
		events.add(new CanonicalStreamMessageStop());
		return events;
	}

	private List<CanonicalStreamEvent> decodeMetadata(JsonNode root) {
		if (!root.hasNonNull(F_USAGE)) {
			return Collections.emptyList();
		}
		JsonNode usage = root.get(F_USAGE);
		Integer in = usage.hasNonNull(F_INPUT_TOKENS) ? usage.get(F_INPUT_TOKENS).asInt() : null;
		Integer outt = usage.hasNonNull(F_OUTPUT_TOKENS) ? usage.get(F_OUTPUT_TOKENS).asInt() : null;
		return Collections.singletonList((CanonicalStreamEvent) new CanonicalStreamMessageDelta(null, new CanonicalUsage(in, outt)));
	}


	/* === utility === */

	private static int readContentBlockIndex(JsonNode root) {
		return root.hasNonNull(F_CONTENT_BLOCK_INDEX) ? root.get(F_CONTENT_BLOCK_INDEX).asInt() : 0;
	}
}
