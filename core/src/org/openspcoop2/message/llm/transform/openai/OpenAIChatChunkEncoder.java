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

import java.nio.charset.StandardCharsets;

import org.openspcoop2.message.llm.CanonicalStopReason;
import org.openspcoop2.message.llm.CanonicalUsage;
import org.openspcoop2.message.llm.stream.CanonicalStreamEvent;
import org.openspcoop2.message.llm.stream.CanonicalStreamMessageDelta;
import org.openspcoop2.message.llm.stream.CanonicalStreamMessageStart;
import org.openspcoop2.message.llm.stream.CanonicalStreamTextDelta;
import org.openspcoop2.message.llm.transform.LLMDialect;
import org.openspcoop2.message.llm.transform.LLMOutboundFrontDoorChunkEncoder;
import org.openspcoop2.message.llm.transform.LLMTransformException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Encoder canonical → chunk SSE OpenAI Chat Completions streaming.
 * <p>
 * Schema OpenAI di riferimento per ogni chunk:
 * </p>
 * <pre>
 * data: {"id":"chatcmpl-...","object":"chat.completion.chunk","created":...,"model":"...",
 *        "choices":[{"index":0,"delta":{...},"finish_reason":null}]}
 * </pre>
 * <p>
 * L'encoder è <strong>stateful</strong>: trattiene id/model/created acquisiti dal primo
 * {@link CanonicalStreamMessageStart} per propagarli su tutti i chunk successivi.
 * Una nuova istanza va creata per ogni stream.
 * </p>
 * <p>
 * Mapping MVP (chat sincrona):
 * </p>
 * <ul>
 *   <li>{@link CanonicalStreamMessageStart} → primo chunk con {@code delta:{"role":"assistant"}}</li>
 *   <li>{@link CanonicalStreamTextDelta} → chunk con {@code delta:{"content":"<text>"}}</li>
 *   <li>{@link CanonicalStreamMessageDelta} → chunk finale con {@code finish_reason} e usage</li>
 *   <li>Altri eventi canonical → nessun chunk emesso (no-op)</li>
 *   <li>{@link #terminator()} → {@code data: [DONE]\n\n}</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class OpenAIChatChunkEncoder implements LLMOutboundFrontDoorChunkEncoder {

	private static final byte[] TERMINATOR_BYTES = ("data: " + OpenAIChatFields.STREAM_DONE_MARKER + "\n\n").getBytes(StandardCharsets.UTF_8);

	private String id;
	private String model;
	private long created;

	@Override
	public LLMDialect getSupportedDialect() {
		return LLMDialect.OPENAI_CHAT_V1;
	}

	@Override
	public byte[] encode(CanonicalStreamEvent event) throws LLMTransformException {
		if (event == null) {
			return new byte[0];
		}
		try {
			if (event instanceof CanonicalStreamMessageStart messageStart) {
				captureState(messageStart);
				return buildChunk(buildRoleDelta(), null);
			}
			if (event instanceof CanonicalStreamTextDelta textDelta) {
				return buildChunk(buildContentDelta(textDelta.getText()), null);
			}
			if (event instanceof CanonicalStreamMessageDelta messageDelta) {
				return buildChunk(emptyDelta(), messageDelta);
			}
			// content_block_start/stop, tool_use_delta, ping, message_stop:
			// niente chunk OpenAI da emettere (data:[DONE] viene da terminator()).
			return new byte[0];
		} catch (Exception e) {
			throw new LLMTransformException("Errore nell'encoding chunk OpenAI Chat Completions: " + e.getMessage(), e);
		}
	}

	@Override
	public byte[] terminator() {
		return TERMINATOR_BYTES;
	}


	/* === stato === */

	private void captureState(CanonicalStreamMessageStart event) {
		this.id = event.getId();
		this.model = event.getModel();
		this.created = System.currentTimeMillis() / 1000L;
	}


	/* === costruzione chunk === */

	private byte[] buildChunk(ObjectNode delta, CanonicalStreamMessageDelta finalDelta) throws Exception {
		ObjectMapper mapper = JSONUtils.getObjectMapper();
		ObjectNode chunk = mapper.createObjectNode();
		if (this.id != null) {
			chunk.put(OpenAIChatFields.FIELD_ID, this.id);
		}
		chunk.put(OpenAIChatFields.FIELD_OBJECT, OpenAIChatFields.OBJECT_CHAT_COMPLETION_CHUNK);
		chunk.put(OpenAIChatFields.FIELD_CREATED, this.created > 0 ? this.created : System.currentTimeMillis() / 1000L);
		if (this.model != null) {
			chunk.put(OpenAIChatFields.FIELD_MODEL, this.model);
		}
		ArrayNode choices = chunk.putArray(OpenAIChatFields.FIELD_CHOICES);
		ObjectNode choice = choices.addObject();
		choice.put(OpenAIChatFields.FIELD_INDEX, 0);
		choice.set(OpenAIChatFields.FIELD_DELTA, delta);
		applyFinishReason(choice, finalDelta);
		applyUsage(chunk, finalDelta);
		return wrapAsSseData(mapper.writeValueAsString(chunk));
	}

	private void applyFinishReason(ObjectNode choice, CanonicalStreamMessageDelta finalDelta) {
		String finishReason = finalDelta != null ? mapFinishReason(finalDelta.getStopReason()) : null;
		if (finishReason != null) {
			choice.put(OpenAIChatFields.FIELD_FINISH_REASON, finishReason);
		} else {
			choice.putNull(OpenAIChatFields.FIELD_FINISH_REASON);
		}
	}

	private void applyUsage(ObjectNode chunk, CanonicalStreamMessageDelta finalDelta) {
		if (finalDelta == null || finalDelta.getUsage() == null) {
			return;
		}
		CanonicalUsage u = finalDelta.getUsage();
		int p = u.getInputTokens() != null ? u.getInputTokens() : 0;
		int c = u.getOutputTokens() != null ? u.getOutputTokens() : 0;
		ObjectNode usage = chunk.putObject(OpenAIChatFields.FIELD_USAGE);
		usage.put(OpenAIChatFields.FIELD_PROMPT_TOKENS, p);
		usage.put(OpenAIChatFields.FIELD_COMPLETION_TOKENS, c);
		usage.put(OpenAIChatFields.FIELD_TOTAL_TOKENS, p + c);
	}


	/* === delta builders === */

	private ObjectNode buildRoleDelta() {
		ObjectNode delta = JSONUtils.getObjectMapper().createObjectNode();
		delta.put(OpenAIChatFields.FIELD_ROLE, OpenAIChatFields.ROLE_ASSISTANT);
		return delta;
	}

	private ObjectNode buildContentDelta(String text) {
		ObjectNode delta = JSONUtils.getObjectMapper().createObjectNode();
		delta.put(OpenAIChatFields.FIELD_CONTENT, text != null ? text : "");
		return delta;
	}

	private ObjectNode emptyDelta() {
		return JSONUtils.getObjectMapper().createObjectNode();
	}


	/* === SSE framing + finish_reason mapping === */

	private byte[] wrapAsSseData(String jsonChunk) {
		StringBuilder sb = new StringBuilder(jsonChunk.length() + 10);
		sb.append("data: ").append(jsonChunk).append("\n\n");
		return sb.toString().getBytes(StandardCharsets.UTF_8);
	}

	private String mapFinishReason(CanonicalStopReason sr) {
		if (sr == null) {
			return null;
		}
		switch (sr) {
			case MAX_TOKENS:
				return OpenAIChatFields.FINISH_REASON_LENGTH;
			case TOOL_USE:
				return OpenAIChatFields.FINISH_REASON_TOOL_CALLS;
			case END_TURN:
			case STOP_SEQUENCE:
			default:
				return OpenAIChatFields.FINISH_REASON_STOP;
		}
	}
}
