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
import java.util.Map;

import org.openspcoop2.message.llm.CanonicalChatRequest;
import org.openspcoop2.message.llm.CanonicalContentBlock;
import org.openspcoop2.message.llm.CanonicalMessage;
import org.openspcoop2.message.llm.CanonicalRole;
import org.openspcoop2.message.llm.CanonicalTextBlock;
import org.openspcoop2.message.llm.CanonicalTool;
import org.openspcoop2.message.llm.CanonicalToolResultBlock;
import org.openspcoop2.message.llm.CanonicalToolUseBlock;
import org.openspcoop2.message.llm.stream.LLMProviderStreamTransport;
import org.openspcoop2.message.llm.transform.LLMOutboundProviderRequestTransformer;
import org.openspcoop2.message.llm.transform.LLMProviderRequest;
import org.openspcoop2.message.llm.transform.LLMProviders;
import org.openspcoop2.message.llm.transform.LLMTransformException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Trasformatore outbound provider: CanonicalChatRequest → JSON OpenAI Chat Completions.
 * <p>
 * Mapping principali:
 * </p>
 * <ul>
 *   <li>{@code system} top-level del canonical → primo messaggio con {@code role:"system"}</li>
 *   <li>messaggi block-based del canonical → forma flat OpenAI:
 *     <ul>
 *       <li>blocchi {@code text} concatenati → {@code message.content} stringa</li>
 *       <li>blocchi {@code tool_use} → {@code message.tool_calls[]} con
 *           {@code function.arguments} serializzato come stringa JSON</li>
 *       <li>blocchi {@code tool_result} → messaggio separato {@code role:"tool"} con
 *           {@code tool_call_id} e {@code content} stringa</li>
 *     </ul>
 *   </li>
 *   <li>{@code maxTokens} → {@code max_completion_tokens} (nuovo nome OpenAI; rimane
 *       il fallback {@code max_tokens} per i client legacy che lo richiedono)</li>
 *   <li>{@code stopSequences} → {@code stop}</li>
 *   <li>{@code tools} → {@code tools[]} OpenAI con wrap {@code type:"function"}</li>
 * </ul>
 * <p>
 * Header HTTP: nessuno aggiuntivo (l'autenticazione è gestita dal connettore GovWay).
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class OpenAIChatOutboundProviderRequestTransformer implements LLMOutboundProviderRequestTransformer {

	@Override
	public String getProviderId() {
		return LLMProviders.OPENAI;
	}

	@Override
	public String getProviderResourcePath(CanonicalChatRequest request) {
		return OpenAIChatFields.RESOURCE_PATH_CHAT_COMPLETIONS;
	}

	@Override
	public LLMProviderStreamTransport getProviderStreamTransport() {
		return LLMProviderStreamTransport.SSE;
	}

	@Override
	public LLMProviderRequest transform(CanonicalChatRequest request) throws LLMTransformException {
		if (request == null) {
			throw new LLMTransformException("CanonicalChatRequest nullo");
		}
		try {
			ObjectMapper mapper = JSONUtils.getObjectMapper();
			ObjectNode out = mapper.createObjectNode();
			applyBasicFields(request, out);
			out.set(OpenAIChatFields.FIELD_MESSAGES, buildMessages(request, mapper));
			applyToolsAndChoice(request, out, mapper);
			byte[] body = mapper.writeValueAsBytes(out);
			return new LLMProviderRequest(body, null);
		} catch (LLMTransformException e) {
			throw e;
		} catch (Exception e) {
			throw new LLMTransformException("Errore nella serializzazione canonical → OpenAI Chat Completions: " + e.getMessage(), e);
		}
	}


	/* === top-level === */

	private void applyBasicFields(CanonicalChatRequest in, ObjectNode out) {
		if (in.getModel() != null) {
			out.put(OpenAIChatFields.FIELD_MODEL, in.getModel());
		}
		if (in.getMaxTokens() != null) {
			out.put(OpenAIChatFields.FIELD_MAX_COMPLETION_TOKENS, in.getMaxTokens());
		}
		if (in.getTemperature() != null) {
			out.put(OpenAIChatFields.FIELD_TEMPERATURE, in.getTemperature());
		}
		if (in.getTopP() != null) {
			out.put(OpenAIChatFields.FIELD_TOP_P, in.getTopP());
		}
		applyStopSequences(in, out);
		if (in.getStream() != null) {
			out.put(OpenAIChatFields.FIELD_STREAM, in.getStream());
		}
	}

	private void applyStopSequences(CanonicalChatRequest in, ObjectNode out) {
		if (in.getStopSequences() == null || in.getStopSequences().isEmpty()) {
			return;
		}
		ArrayNode stop = out.putArray(OpenAIChatFields.FIELD_STOP);
		for (String s : in.getStopSequences()) {
			stop.add(s);
		}
	}


	/* === messaggi === */

	private ArrayNode buildMessages(CanonicalChatRequest in, ObjectMapper mapper) throws LLMTransformException {
		ArrayNode messages = mapper.createArrayNode();
		if (in.getSystem() != null && !in.getSystem().isEmpty()) {
			ObjectNode sys = mapper.createObjectNode();
			sys.put(OpenAIChatFields.FIELD_ROLE, OpenAIChatFields.ROLE_SYSTEM);
			sys.put(OpenAIChatFields.FIELD_CONTENT, in.getSystem());
			messages.add(sys);
		}
		List<CanonicalMessage> msgs = in.getMessages();
		if (msgs == null) {
			return messages;
		}
		for (CanonicalMessage m : msgs) {
			appendMessage(messages, m, mapper);
		}
		return messages;
	}

	private void appendMessage(ArrayNode messages, CanonicalMessage m, ObjectMapper mapper) throws LLMTransformException {
		CanonicalRole role = m.getRole();
		List<CanonicalContentBlock> blocks = m.getContent();

		// I blocchi tool_result diventano messaggi separati con role="tool" in OpenAI.
		// Vengono emessi PRIMA del messaggio "principale" per preservare l'ordine logico
		// di una conversazione con tool calling.
		StringBuilder text = new StringBuilder();
		List<CanonicalToolUseBlock> toolUses = new ArrayList<>();
		List<CanonicalToolResultBlock> toolResults = new ArrayList<>();
		accumulateBlocks(blocks, text, toolUses, toolResults);

		for (CanonicalToolResultBlock tr : toolResults) {
			messages.add(buildToolResultMessage(tr, mapper));
		}
		if (text.length() > 0 || !toolUses.isEmpty()) {
			messages.add(buildPrimaryMessage(role, text, toolUses, mapper));
		}
	}

	private void accumulateBlocks(List<CanonicalContentBlock> blocks, StringBuilder text,
			List<CanonicalToolUseBlock> toolUses, List<CanonicalToolResultBlock> toolResults) {
		if (blocks == null) {
			return;
		}
		for (CanonicalContentBlock b : blocks) {
			if (b instanceof CanonicalTextBlock cb) {
				if (cb.getText() != null) {
					text.append(cb.getText());
				}
			} else if (b instanceof CanonicalToolUseBlock cb) {
				toolUses.add(cb);
			} else if (b instanceof CanonicalToolResultBlock cb) {
				toolResults.add(cb);
			}
		}
	}

	private ObjectNode buildToolResultMessage(CanonicalToolResultBlock tr, ObjectMapper mapper) {
		ObjectNode m = mapper.createObjectNode();
		m.put(OpenAIChatFields.FIELD_ROLE, OpenAIChatFields.ROLE_TOOL);
		if (tr.getToolUseId() != null) {
			m.put(OpenAIChatFields.FIELD_TOOL_CALL_ID, tr.getToolUseId());
		}
		m.put(OpenAIChatFields.FIELD_CONTENT, tr.getContent() != null ? tr.getContent() : "");
		return m;
	}

	private ObjectNode buildPrimaryMessage(CanonicalRole role, StringBuilder text,
			List<CanonicalToolUseBlock> toolUses, ObjectMapper mapper) throws LLMTransformException {
		ObjectNode m = mapper.createObjectNode();
		m.put(OpenAIChatFields.FIELD_ROLE, role != null ? role.getValue() : OpenAIChatFields.ROLE_ASSISTANT);
		if (text.length() > 0) {
			m.put(OpenAIChatFields.FIELD_CONTENT, text.toString());
		} else {
			m.putNull(OpenAIChatFields.FIELD_CONTENT);
		}
		if (!toolUses.isEmpty()) {
			ArrayNode arr = mapper.createArrayNode();
			for (CanonicalToolUseBlock tu : toolUses) {
				arr.add(buildToolCall(tu, mapper));
			}
			m.set(OpenAIChatFields.FIELD_TOOL_CALLS, arr);
		}
		return m;
	}

	private ObjectNode buildToolCall(CanonicalToolUseBlock tu, ObjectMapper mapper) throws LLMTransformException {
		ObjectNode tc = mapper.createObjectNode();
		if (tu.getId() != null) {
			tc.put(OpenAIChatFields.FIELD_ID, tu.getId());
		}
		tc.put(OpenAIChatFields.FIELD_TYPE, OpenAIChatFields.TYPE_FUNCTION);
		ObjectNode function = tc.putObject(OpenAIChatFields.FIELD_FUNCTION);
		if (tu.getName() != null) {
			function.put(OpenAIChatFields.FIELD_NAME, tu.getName());
		}
		function.put(OpenAIChatFields.FIELD_ARGUMENTS, serializeArguments(tu.getInput(), mapper));
		return tc;
	}

	private String serializeArguments(Map<String, Object> input, ObjectMapper mapper) throws LLMTransformException {
		if (input == null) {
			return "{}";
		}
		try {
			return mapper.writeValueAsString(input);
		} catch (Exception e) {
			throw new LLMTransformException("Errore serializzando input del tool_use: " + e.getMessage(), e);
		}
	}


	/* === tools === */

	private void applyToolsAndChoice(CanonicalChatRequest in, ObjectNode out, ObjectMapper mapper) {
		List<CanonicalTool> tools = in.getTools();
		if (tools == null || tools.isEmpty()) {
			return;
		}
		ArrayNode arr = mapper.createArrayNode();
		for (CanonicalTool t : tools) {
			ObjectNode toolNode = mapper.createObjectNode();
			toolNode.put(OpenAIChatFields.FIELD_TYPE, OpenAIChatFields.TYPE_FUNCTION);
			ObjectNode function = toolNode.putObject(OpenAIChatFields.FIELD_FUNCTION);
			if (t.getName() != null) {
				function.put(OpenAIChatFields.FIELD_NAME, t.getName());
			}
			if (t.getDescription() != null) {
				function.put(OpenAIChatFields.FIELD_DESCRIPTION, t.getDescription());
			}
			if (t.getInputSchema() != null) {
				function.set(OpenAIChatFields.FIELD_PARAMETERS, mapper.valueToTree(t.getInputSchema()));
			}
			arr.add(toolNode);
		}
		out.set(OpenAIChatFields.FIELD_TOOLS, arr);
	}
}
