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

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.message.llm.CanonicalChatResponse;
import org.openspcoop2.message.llm.CanonicalContentBlock;
import org.openspcoop2.message.llm.CanonicalStopReason;
import org.openspcoop2.message.llm.CanonicalTextBlock;
import org.openspcoop2.message.llm.CanonicalToolUseBlock;
import org.openspcoop2.message.llm.CanonicalUsage;
import org.openspcoop2.message.llm.transform.LLMDialect;
import org.openspcoop2.message.llm.transform.LLMOutboundFrontDoorResponseTransformer;
import org.openspcoop2.message.llm.transform.LLMTransformException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Trasformatore outbound front-door: CanonicalChatResponse → JSON OpenAI Chat Completions response.
 * <p>
 * Lavoro non banale perché il canonical è block-based (Anthropic-like) mentre
 * OpenAI usa una forma flat con {@code message.content} stringa e
 * {@code message.tool_calls[]} parallelo.
 * </p>
 *
 * Mapping:
 * <ul>
 *   <li>blocchi {@code text} concatenati → {@code message.content} (null se solo tool_use)</li>
 *   <li>blocchi {@code tool_use} → {@code message.tool_calls[]} con
 *       {@code function.arguments} serializzato come stringa JSON</li>
 *   <li>{@code stop_reason} mappato a {@code finish_reason}
 *       (end_turn/stop_sequence→"stop", max_tokens→"length", tool_use→"tool_calls")</li>
 *   <li>{@code usage.input_tokens}→{@code prompt_tokens},
 *       {@code output_tokens}→{@code completion_tokens}, calcolato {@code total_tokens}</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class OpenAIChatOutboundFrontDoorResponseTransformer implements LLMOutboundFrontDoorResponseTransformer {


	@Override
	public LLMDialect getSupportedDialect() {
		return LLMDialect.OPENAI_CHAT_V1;
	}

	@Override
	public byte[] transform(CanonicalChatResponse response) throws LLMTransformException {
		if (response == null) {
			throw new LLMTransformException("CanonicalChatResponse nullo");
		}
		try {
			ObjectMapper mapper = JSONUtils.getObjectMapper();
			ObjectNode out = mapper.createObjectNode();
			applyBasicFields(response, out);
			out.set(OpenAIChatFields.FIELD_CHOICES, mapper.createArrayNode().add(buildChoice(response, mapper)));
			applyUsage(response, out, mapper);
			return mapper.writeValueAsBytes(out);
		} catch (LLMTransformException e) {
			throw e;
		} catch (Exception e) {
			throw new LLMTransformException("Errore nella serializzazione canonical → OpenAI Chat Completions response: " + e.getMessage(), e);
		}
	}


	/* === campi top-level === */

	private void applyBasicFields(CanonicalChatResponse in, ObjectNode out) {
		if (in.getId() != null) {
			out.put(OpenAIChatFields.FIELD_ID, in.getId());
		}
		out.put(OpenAIChatFields.FIELD_OBJECT, OpenAIChatFields.OBJECT_CHAT_COMPLETION);
		out.put(OpenAIChatFields.FIELD_CREATED, System.currentTimeMillis() / 1000L);
		if (in.getModel() != null) {
			out.put(OpenAIChatFields.FIELD_MODEL, in.getModel());
		}
	}

	private void applyUsage(CanonicalChatResponse in, ObjectNode out, ObjectMapper mapper) {
		CanonicalUsage u = in.getUsage();
		if (u == null) {
			return;
		}
		int p = u.getInputTokens() != null ? u.getInputTokens() : 0;
		int c = u.getOutputTokens() != null ? u.getOutputTokens() : 0;
		ObjectNode usage = mapper.createObjectNode();
		usage.put(OpenAIChatFields.FIELD_PROMPT_TOKENS, p);
		usage.put(OpenAIChatFields.FIELD_COMPLETION_TOKENS, c);
		usage.put(OpenAIChatFields.FIELD_TOTAL_TOKENS, p + c);
		out.set(OpenAIChatFields.FIELD_USAGE, usage);
	}


	/* === choice === */

	private ObjectNode buildChoice(CanonicalChatResponse in, ObjectMapper mapper) throws LLMTransformException {
		ObjectNode choice = mapper.createObjectNode();
		choice.put(OpenAIChatFields.FIELD_INDEX, 0);
		ObjectNode message = mapper.createObjectNode();
		buildOpenAIMessage(in, message, mapper);
		choice.set(OpenAIChatFields.FIELD_MESSAGE, message);
		String finishReason = mapFinishReason(in.getStopReason());
		if (finishReason != null) {
			choice.put(OpenAIChatFields.FIELD_FINISH_REASON, finishReason);
		}
		return choice;
	}

	private void buildOpenAIMessage(CanonicalChatResponse in, ObjectNode message, ObjectMapper mapper) throws LLMTransformException {
		message.put(OpenAIChatFields.FIELD_ROLE, OpenAIChatFields.ROLE_ASSISTANT);
		List<CanonicalContentBlock> blocks = in.getContent();
		if (blocks == null || blocks.isEmpty()) {
			message.putNull(OpenAIChatFields.FIELD_CONTENT);
			return;
		}
		StringBuilder text = new StringBuilder();
		List<CanonicalToolUseBlock> toolUses = new ArrayList<>();
		for (CanonicalContentBlock b : blocks) {
			accumulateBlock(b, text, toolUses);
		}
		applyMessageContent(message, text);
		applyMessageToolCalls(message, toolUses, mapper);
	}

	private void accumulateBlock(CanonicalContentBlock b, StringBuilder text, List<CanonicalToolUseBlock> toolUses) {
		if (b instanceof CanonicalTextBlock) {
			String t = ((CanonicalTextBlock) b).getText();
			if (t != null) {
				text.append(t);
			}
		} else if (b instanceof CanonicalToolUseBlock) {
			toolUses.add((CanonicalToolUseBlock) b);
		}
		// tool_result non dovrebbe apparire in una response: ignorato silenziosamente
	}

	private void applyMessageContent(ObjectNode message, StringBuilder text) {
		if (text.length() > 0) {
			message.put(OpenAIChatFields.FIELD_CONTENT, text.toString());
		} else {
			message.putNull(OpenAIChatFields.FIELD_CONTENT);
		}
	}

	private void applyMessageToolCalls(ObjectNode message, List<CanonicalToolUseBlock> toolUses, ObjectMapper mapper) throws LLMTransformException {
		if (toolUses.isEmpty()) {
			return;
		}
		ArrayNode arr = mapper.createArrayNode();
		for (CanonicalToolUseBlock tu : toolUses) {
			arr.add(buildToolCall(tu, mapper));
		}
		message.set(OpenAIChatFields.FIELD_TOOL_CALLS, arr);
	}

	private ObjectNode buildToolCall(CanonicalToolUseBlock tu, ObjectMapper mapper) throws LLMTransformException {
		ObjectNode tc = mapper.createObjectNode();
		if (tu.getId() != null) {
			tc.put(OpenAIChatFields.FIELD_ID, tu.getId());
		}
		tc.put(OpenAIChatFields.FIELD_TYPE, OpenAIChatFields.TYPE_FUNCTION);
		tc.set(OpenAIChatFields.FIELD_FUNCTION, buildFunctionObject(tu, mapper));
		return tc;
	}

	private ObjectNode buildFunctionObject(CanonicalToolUseBlock tu, ObjectMapper mapper) throws LLMTransformException {
		ObjectNode function = mapper.createObjectNode();
		if (tu.getName() != null) {
			function.put(OpenAIChatFields.FIELD_NAME, tu.getName());
		}
		function.put(OpenAIChatFields.FIELD_ARGUMENTS, serializeArguments(tu, mapper));
		return function;
	}

	private String serializeArguments(CanonicalToolUseBlock tu, ObjectMapper mapper) throws LLMTransformException {
		if (tu.getInput() == null) {
			return "{}";
		}
		try {
			return mapper.writeValueAsString(tu.getInput());
		} catch (Exception e) {
			throw new LLMTransformException("Errore serializzando input del tool_use: " + e.getMessage(), e);
		}
	}


	/* === finish_reason === */

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
