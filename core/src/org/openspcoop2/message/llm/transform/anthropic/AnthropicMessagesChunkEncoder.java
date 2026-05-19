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

import java.nio.charset.StandardCharsets;

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
import org.openspcoop2.message.llm.transform.LLMDialect;
import org.openspcoop2.message.llm.transform.LLMOutboundFrontDoorChunkEncoder;
import org.openspcoop2.message.llm.transform.LLMTransformException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Encoder canonical → chunk SSE Anthropic Messages streaming.
 * Mapping ~1:1 con il modello canonical (che è già ispirato ad Anthropic),
 * quindi è di fatto un emettitore di eventi nativi. Stateless: una sola istanza
 * può servire più stream concorrenti.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class AnthropicMessagesChunkEncoder implements LLMOutboundFrontDoorChunkEncoder {

	@Override
	public LLMDialect getSupportedDialect() {
		return LLMDialect.ANTHROPIC_MESSAGES_V1;
	}

	@Override
	public byte[] encode(CanonicalStreamEvent event) throws LLMTransformException {
		if (event == null) {
			return new byte[0];
		}
		try {
			if (event instanceof CanonicalStreamMessageStart e) {
				return wrap(AnthropicMessagesFields.EVENT_MESSAGE_START, buildMessageStartData(e));
			}
			if (event instanceof CanonicalStreamContentBlockStart e) {
				return wrap(AnthropicMessagesFields.EVENT_CONTENT_BLOCK_START, buildContentBlockStartData(e));
			}
			if (event instanceof CanonicalStreamTextDelta e) {
				return wrap(AnthropicMessagesFields.EVENT_CONTENT_BLOCK_DELTA, buildTextDeltaData(e));
			}
			if (event instanceof CanonicalStreamToolUseDelta e) {
				return wrap(AnthropicMessagesFields.EVENT_CONTENT_BLOCK_DELTA, buildToolUseDeltaData(e));
			}
			if (event instanceof CanonicalStreamContentBlockStop e) {
				return wrap(AnthropicMessagesFields.EVENT_CONTENT_BLOCK_STOP, buildContentBlockStopData(e));
			}
			if (event instanceof CanonicalStreamMessageDelta e) {
				return wrap(AnthropicMessagesFields.EVENT_MESSAGE_DELTA, buildMessageDeltaData(e));
			}
			if (event instanceof CanonicalStreamMessageStop) {
				return wrap(AnthropicMessagesFields.EVENT_MESSAGE_STOP, buildMessageStopData());
			}
			if (event instanceof CanonicalStreamPing) {
				return wrap(AnthropicMessagesFields.EVENT_PING, buildPingData());
			}
			return new byte[0];
		} catch (Exception e) {
			throw new LLMTransformException("Errore nell'encoding chunk Anthropic Messages: " + e.getMessage(), e);
		}
	}

	@Override
	public byte[] terminator() {
		// Anthropic non ha un marker di chiusura aggiuntivo: il message_stop chiude lo stream.
		return new byte[0];
	}


	/* === data payload builders === */

	private ObjectNode buildMessageStartData(CanonicalStreamMessageStart e) {
		ObjectMapper m = JSONUtils.getObjectMapper();
		ObjectNode root = m.createObjectNode();
		root.put(AnthropicMessagesFields.FIELD_TYPE, AnthropicMessagesFields.EVENT_MESSAGE_START);
		ObjectNode msg = root.putObject(AnthropicMessagesFields.FIELD_MESSAGE);
		if (e.getId() != null) {
			msg.put(AnthropicMessagesFields.FIELD_ID, e.getId());
		}
		msg.put(AnthropicMessagesFields.FIELD_TYPE, AnthropicMessagesFields.FIELD_MESSAGE);
		CanonicalRole role = e.getRole();
		msg.put(AnthropicMessagesFields.FIELD_ROLE, role != null ? role.getValue() : AnthropicMessagesFields.ROLE_ASSISTANT);
		msg.putArray(AnthropicMessagesFields.FIELD_CONTENT);
		if (e.getModel() != null) {
			msg.put(AnthropicMessagesFields.FIELD_MODEL, e.getModel());
		}
		msg.putNull(AnthropicMessagesFields.FIELD_STOP_REASON);
		msg.putNull(AnthropicMessagesFields.FIELD_STOP_SEQUENCE);
		applyUsage(msg.putObject(AnthropicMessagesFields.FIELD_USAGE), e.getUsage());
		return root;
	}

	private ObjectNode buildContentBlockStartData(CanonicalStreamContentBlockStart e) {
		ObjectMapper m = JSONUtils.getObjectMapper();
		ObjectNode root = m.createObjectNode();
		root.put(AnthropicMessagesFields.FIELD_TYPE, AnthropicMessagesFields.EVENT_CONTENT_BLOCK_START);
		root.put(AnthropicMessagesFields.FIELD_INDEX, e.getIndex());
		ObjectNode block = root.putObject(AnthropicMessagesFields.FIELD_CONTENT_BLOCK);
		String bt = e.getBlockType() != null ? e.getBlockType() : AnthropicMessagesFields.BLOCK_TYPE_TEXT;
		block.put(AnthropicMessagesFields.FIELD_TYPE, bt);
		if (AnthropicMessagesFields.BLOCK_TYPE_TOOL_USE.equals(bt)) {
			if (e.getToolUseId() != null) {
				block.put(AnthropicMessagesFields.FIELD_ID, e.getToolUseId());
			}
			if (e.getToolUseName() != null) {
				block.put(AnthropicMessagesFields.FIELD_NAME, e.getToolUseName());
			}
			block.putObject(AnthropicMessagesFields.FIELD_INPUT);
		} else {
			block.put(AnthropicMessagesFields.FIELD_TEXT, "");
		}
		return root;
	}

	private ObjectNode buildTextDeltaData(CanonicalStreamTextDelta e) {
		ObjectMapper m = JSONUtils.getObjectMapper();
		ObjectNode root = m.createObjectNode();
		root.put(AnthropicMessagesFields.FIELD_TYPE, AnthropicMessagesFields.EVENT_CONTENT_BLOCK_DELTA);
		root.put(AnthropicMessagesFields.FIELD_INDEX, e.getIndex());
		ObjectNode delta = root.putObject(AnthropicMessagesFields.FIELD_DELTA);
		delta.put(AnthropicMessagesFields.FIELD_TYPE, AnthropicMessagesFields.DELTA_TYPE_TEXT);
		delta.put(AnthropicMessagesFields.FIELD_TEXT, e.getText() != null ? e.getText() : "");
		return root;
	}

	private ObjectNode buildToolUseDeltaData(CanonicalStreamToolUseDelta e) {
		ObjectMapper m = JSONUtils.getObjectMapper();
		ObjectNode root = m.createObjectNode();
		root.put(AnthropicMessagesFields.FIELD_TYPE, AnthropicMessagesFields.EVENT_CONTENT_BLOCK_DELTA);
		root.put(AnthropicMessagesFields.FIELD_INDEX, e.getIndex());
		ObjectNode delta = root.putObject(AnthropicMessagesFields.FIELD_DELTA);
		delta.put(AnthropicMessagesFields.FIELD_TYPE, AnthropicMessagesFields.DELTA_TYPE_INPUT_JSON);
		delta.put(AnthropicMessagesFields.FIELD_PARTIAL_JSON, e.getPartialJsonInput() != null ? e.getPartialJsonInput() : "");
		return root;
	}

	private ObjectNode buildContentBlockStopData(CanonicalStreamContentBlockStop e) {
		ObjectMapper m = JSONUtils.getObjectMapper();
		ObjectNode root = m.createObjectNode();
		root.put(AnthropicMessagesFields.FIELD_TYPE, AnthropicMessagesFields.EVENT_CONTENT_BLOCK_STOP);
		root.put(AnthropicMessagesFields.FIELD_INDEX, e.getIndex());
		return root;
	}

	private ObjectNode buildMessageDeltaData(CanonicalStreamMessageDelta e) {
		ObjectMapper m = JSONUtils.getObjectMapper();
		ObjectNode root = m.createObjectNode();
		root.put(AnthropicMessagesFields.FIELD_TYPE, AnthropicMessagesFields.EVENT_MESSAGE_DELTA);
		ObjectNode delta = root.putObject(AnthropicMessagesFields.FIELD_DELTA);
		CanonicalStopReason sr = e.getStopReason();
		if (sr != null) {
			delta.put(AnthropicMessagesFields.FIELD_STOP_REASON, sr.getValue());
		} else {
			delta.putNull(AnthropicMessagesFields.FIELD_STOP_REASON);
		}
		if (e.getStopSequence() != null) {
			delta.put(AnthropicMessagesFields.FIELD_STOP_SEQUENCE, e.getStopSequence());
		} else {
			delta.putNull(AnthropicMessagesFields.FIELD_STOP_SEQUENCE);
		}
		applyUsage(root.putObject(AnthropicMessagesFields.FIELD_USAGE), e.getUsage());
		return root;
	}

	private ObjectNode buildMessageStopData() {
		ObjectMapper m = JSONUtils.getObjectMapper();
		ObjectNode root = m.createObjectNode();
		root.put(AnthropicMessagesFields.FIELD_TYPE, AnthropicMessagesFields.EVENT_MESSAGE_STOP);
		return root;
	}

	private ObjectNode buildPingData() {
		ObjectMapper m = JSONUtils.getObjectMapper();
		ObjectNode root = m.createObjectNode();
		root.put(AnthropicMessagesFields.FIELD_TYPE, AnthropicMessagesFields.EVENT_PING);
		return root;
	}

	private void applyUsage(ObjectNode usageNode, CanonicalUsage u) {
		// Anthropic richiede sempre input_tokens e output_tokens come interi
		// (validatori client side rifiutano usage:{}): defaultiamo a 0 quando il
		// provider upstream non li manda nel chunk corrente.
		int in = (u != null && u.getInputTokens() != null) ? u.getInputTokens() : 0;
		int out = (u != null && u.getOutputTokens() != null) ? u.getOutputTokens() : 0;
		usageNode.put(AnthropicMessagesFields.FIELD_INPUT_TOKENS, in);
		usageNode.put(AnthropicMessagesFields.FIELD_OUTPUT_TOKENS, out);
	}


	/* === SSE framing === */

	private byte[] wrap(String eventType, ObjectNode dataPayload) throws Exception {
		String json = JSONUtils.getObjectMapper().writeValueAsString(dataPayload);
		StringBuilder sb = new StringBuilder(eventType.length() + json.length() + 20);
		sb.append("event: ").append(eventType).append('\n');
		sb.append("data: ").append(json).append("\n\n");
		return sb.toString().getBytes(StandardCharsets.UTF_8);
	}
}
