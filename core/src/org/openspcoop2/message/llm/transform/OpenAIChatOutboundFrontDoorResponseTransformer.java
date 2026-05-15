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

import org.openspcoop2.message.llm.CanonicalChatResponse;
import org.openspcoop2.message.llm.CanonicalContentBlock;
import org.openspcoop2.message.llm.CanonicalStopReason;
import org.openspcoop2.message.llm.CanonicalTextBlock;
import org.openspcoop2.message.llm.CanonicalToolUseBlock;
import org.openspcoop2.message.llm.CanonicalUsage;
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

	private static final String OBJECT_TYPE = "chat.completion";
	private static final String ROLE_ASSISTANT = "assistant";
	private static final String FUNCTION_TYPE = "function";

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
			out.set("choices", mapper.createArrayNode().add(buildChoice(response, mapper)));
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
			out.put("id", in.getId());
		}
		out.put("object", OBJECT_TYPE);
		out.put("created", System.currentTimeMillis() / 1000L);
		if (in.getModel() != null) {
			out.put("model", in.getModel());
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
		usage.put("prompt_tokens", p);
		usage.put("completion_tokens", c);
		usage.put("total_tokens", p + c);
		out.set("usage", usage);
	}


	/* === choice === */

	private ObjectNode buildChoice(CanonicalChatResponse in, ObjectMapper mapper) throws LLMTransformException {
		ObjectNode choice = mapper.createObjectNode();
		choice.put("index", 0);
		ObjectNode message = mapper.createObjectNode();
		buildOpenAIMessage(in, message, mapper);
		choice.set("message", message);
		String finishReason = mapFinishReason(in.getStopReason());
		if (finishReason != null) {
			choice.put("finish_reason", finishReason);
		}
		return choice;
	}

	private void buildOpenAIMessage(CanonicalChatResponse in, ObjectNode message, ObjectMapper mapper) throws LLMTransformException {
		message.put("role", ROLE_ASSISTANT);
		List<CanonicalContentBlock> blocks = in.getContent();
		if (blocks == null || blocks.isEmpty()) {
			message.putNull("content");
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
			message.put("content", text.toString());
		} else {
			message.putNull("content");
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
		message.set("tool_calls", arr);
	}

	private ObjectNode buildToolCall(CanonicalToolUseBlock tu, ObjectMapper mapper) throws LLMTransformException {
		ObjectNode tc = mapper.createObjectNode();
		if (tu.getId() != null) {
			tc.put("id", tu.getId());
		}
		tc.put("type", FUNCTION_TYPE);
		tc.set("function", buildFunctionObject(tu, mapper));
		return tc;
	}

	private ObjectNode buildFunctionObject(CanonicalToolUseBlock tu, ObjectMapper mapper) throws LLMTransformException {
		ObjectNode function = mapper.createObjectNode();
		if (tu.getName() != null) {
			function.put("name", tu.getName());
		}
		function.put("arguments", serializeArguments(tu, mapper));
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
				return "length";
			case TOOL_USE:
				return "tool_calls";
			case END_TURN:
			case STOP_SEQUENCE:
			default:
				return "stop";
		}
	}
}
