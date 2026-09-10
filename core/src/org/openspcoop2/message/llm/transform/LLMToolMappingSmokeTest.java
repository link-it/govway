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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openspcoop2.message.llm.CanonicalChatRequest;
import org.openspcoop2.message.llm.CanonicalMessage;
import org.openspcoop2.message.llm.CanonicalRole;
import org.openspcoop2.message.llm.CanonicalTextBlock;
import org.openspcoop2.message.llm.CanonicalToolResultBlock;
import org.openspcoop2.message.llm.CanonicalToolUseBlock;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Smoke test del supporto ai tool lungo tutto il percorso: dichiarazione dei tool, ciclo
 * tool_use/tool_result e tool_choice, dai due dialetti front-door verso i tre provider
 * supportati. Ogni provider ha una forma propria (Anthropic {@code tools[]}+{@code input_schema},
 * OpenAI {@code tools[].function}+messaggi {@code role:tool}, Bedrock {@code toolConfig.toolSpec}),
 * quindi la copertura serve a evitare che un tool dichiarato dal client venga silenziosamente
 * scartato verso uno di essi.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMToolMappingSmokeTest {

	private static final String ANTHROPIC_REQUEST_WITH_TOOL = "{\"model\":\"m\",\"max_tokens\":100,"
			+ "\"messages\":[{\"role\":\"user\",\"content\":\"stato pratica?\"}],"
			+ "\"tools\":[{\"name\":\"consultazione-isee\",\"description\":\"consulta isee\","
			+ "\"input_schema\":{\"type\":\"object\",\"properties\":{\"cf\":{\"type\":\"string\"}},\"required\":[\"cf\"]}}]";

	public static void main(String[] args) throws Exception {
		runToolsDeclaration();
		runToolCallRoundTrip();
		runToolChoiceForcedTool();
		runToolChoiceRequiredFromOpenAIClient();
		runToolChoiceNone();
		System.out.println("LLMToolMappingSmokeTest: ALL OK");
	}

	/** Un tool dichiarato dal client deve arrivare a tutti e tre i provider, nella forma di ciascuno. */
	public static void runToolsDeclaration() throws Exception {
		CanonicalChatRequest canonical = fromAnthropicClient("}");

		JsonNode anthropic = toProvider(LLMProviders.ANTHROPIC, canonical);
		assertEqualsString("anthropic tools[0].name", "consultazione-isee", anthropic.path("tools").path(0).path("name").asText());
		assertTrue("anthropic tools[0].input_schema", anthropic.path("tools").path(0).path("input_schema").has("properties"));

		JsonNode openai = toProvider(LLMProviders.OPENAI, canonical);
		assertEqualsString("openai tools[0].type", "function", openai.path("tools").path(0).path("type").asText());
		assertEqualsString("openai tools[0].function.name", "consultazione-isee",
				openai.path("tools").path(0).path("function").path("name").asText());
		assertTrue("openai tools[0].function.parameters", openai.path("tools").path(0).path("function").path("parameters").has("properties"));

		JsonNode bedrock = toProvider(LLMProviders.AWS_BEDROCK, canonical);
		assertEqualsString("bedrock toolConfig.tools[0].toolSpec.name", "consultazione-isee",
				bedrock.path("toolConfig").path("tools").path(0).path("toolSpec").path("name").asText());
		assertTrue("bedrock toolSpec.inputSchema.json",
				bedrock.path("toolConfig").path("tools").path(0).path("toolSpec").path("inputSchema").path("json").has("properties"));
	}

	/** Ciclo dell'agent: il tool_use dell'assistant e il tool_result dell'utente vanno tradotti in entrambi i sensi. */
	public static void runToolCallRoundTrip() throws Exception {
		Map<String, Object> input = new LinkedHashMap<>();
		input.put("cf", "RSSMRA80A01H501U");
		CanonicalChatRequest canonical = new CanonicalChatRequest();
		canonical.setModel("m");
		canonical.setMaxTokens(100);
		canonical.setMessages(Arrays.asList(
				new CanonicalMessage(CanonicalRole.USER, Arrays.asList(new CanonicalTextBlock("stato pratica?"))),
				new CanonicalMessage(CanonicalRole.ASSISTANT, Arrays.asList(new CanonicalToolUseBlock("call_1", "consultazione-isee", input))),
				new CanonicalMessage(CanonicalRole.USER, Arrays.asList(new CanonicalToolResultBlock("call_1", "{\"isee\":\"12000\"}", null)))));

		JsonNode anthropic = toProvider(LLMProviders.ANTHROPIC, canonical);
		assertEqualsString("anthropic tool_use", "tool_use", anthropic.path("messages").path(1).path("content").path(0).path("type").asText());
		assertEqualsString("anthropic tool_result", "tool_result", anthropic.path("messages").path(2).path("content").path(0).path("type").asText());
		assertEqualsString("anthropic tool_use_id", "call_1", anthropic.path("messages").path(2).path("content").path(0).path("tool_use_id").asText());

		JsonNode openai = toProvider(LLMProviders.OPENAI, canonical);
		assertEqualsString("openai tool_calls[0].id", "call_1", openai.path("messages").path(1).path("tool_calls").path(0).path("id").asText());
		assertEqualsString("openai messaggio tool_result role", "tool", openai.path("messages").path(2).path("role").asText());
		assertEqualsString("openai tool_call_id", "call_1", openai.path("messages").path(2).path("tool_call_id").asText());

		JsonNode bedrock = toProvider(LLMProviders.AWS_BEDROCK, canonical);
		assertEqualsString("bedrock toolUse.toolUseId", "call_1",
				bedrock.path("messages").path(1).path("content").path(0).path("toolUse").path("toolUseId").asText());
		assertEqualsString("bedrock toolResult.toolUseId", "call_1",
				bedrock.path("messages").path(2).path("content").path(0).path("toolResult").path("toolUseId").asText());
		assertEqualsString("bedrock toolResult.content[0].text", "{\"isee\":\"12000\"}",
				bedrock.path("messages").path(2).path("content").path(0).path("toolResult").path("content").path(0).path("text").asText());
	}

	/** tool_choice che forza un tool specifico, dichiarato da un client Anthropic. */
	public static void runToolChoiceForcedTool() throws Exception {
		CanonicalChatRequest canonical = fromAnthropicClient(",\"tool_choice\":{\"type\":\"tool\",\"name\":\"consultazione-isee\"}}");

		assertEqualsString("canonical tool_choice", "tool", canonical.getToolChoice().getMode().getValue());
		assertEqualsString("anthropic tool_choice.type", "tool",
				toProvider(LLMProviders.ANTHROPIC, canonical).path("tool_choice").path("type").asText());
		JsonNode openai = toProvider(LLMProviders.OPENAI, canonical);
		assertEqualsString("openai tool_choice.type", "function", openai.path("tool_choice").path("type").asText());
		assertEqualsString("openai tool_choice.function.name", "consultazione-isee",
				openai.path("tool_choice").path("function").path("name").asText());
		assertEqualsString("bedrock toolChoice.tool.name", "consultazione-isee",
				toProvider(LLMProviders.AWS_BEDROCK, canonical).path("toolConfig").path("toolChoice").path("tool").path("name").asText());
	}

	/** Un client OpenAI che chiede 'required' equivale al canonical 'any' e all'{@code any} di Bedrock. */
	public static void runToolChoiceRequiredFromOpenAIClient() throws Exception {
		String request = "{\"model\":\"m\",\"messages\":[{\"role\":\"user\",\"content\":\"stato pratica?\"}],"
				+ "\"tools\":[{\"type\":\"function\",\"function\":{\"name\":\"consultazione-isee\","
				+ "\"parameters\":{\"type\":\"object\",\"properties\":{}}}}],\"tool_choice\":\"required\"}";
		CanonicalChatRequest canonical = LLMTransformerRegistry.getInboundRequestTransformer(LLMDialect.OPENAI_CHAT_V1)
				.transform(request.getBytes(StandardCharsets.UTF_8));

		assertEqualsString("canonical tool_choice", "any", canonical.getToolChoice().getMode().getValue());
		assertEqualsString("anthropic tool_choice.type", "any",
				toProvider(LLMProviders.ANTHROPIC, canonical).path("tool_choice").path("type").asText());
		assertEqualsString("openai tool_choice", "required",
				toProvider(LLMProviders.OPENAI, canonical).path("tool_choice").asText());
		assertTrue("bedrock toolChoice.any",
				toProvider(LLMProviders.AWS_BEDROCK, canonical).path("toolConfig").path("toolChoice").has("any"));
	}

	/**
	 * tool_choice 'none': Bedrock non lo prevede, l'unico modo per impedire l'invocazione dei
	 * tool e' non dichiararli, quindi il toolConfig non viene emesso.
	 */
	public static void runToolChoiceNone() throws Exception {
		CanonicalChatRequest canonical = fromAnthropicClient(",\"tool_choice\":{\"type\":\"none\"}}");

		assertEqualsString("anthropic tool_choice.type", "none",
				toProvider(LLMProviders.ANTHROPIC, canonical).path("tool_choice").path("type").asText());
		assertEqualsString("openai tool_choice", "none",
				toProvider(LLMProviders.OPENAI, canonical).path("tool_choice").asText());
		assertTrue("bedrock toolConfig assente", toProvider(LLMProviders.AWS_BEDROCK, canonical).path("toolConfig").isMissingNode());
	}


	/* === helpers === */

	private static CanonicalChatRequest fromAnthropicClient(String tail) throws Exception {
		String request = ANTHROPIC_REQUEST_WITH_TOOL + tail;
		return LLMTransformerRegistry.getInboundRequestTransformer(LLMDialect.ANTHROPIC_MESSAGES_V1)
				.transform(request.getBytes(StandardCharsets.UTF_8));
	}

	private static JsonNode toProvider(String providerId, CanonicalChatRequest canonical) throws Exception {
		byte[] body = LLMTransformerRegistry.getOutboundProviderRequestTransformer(providerId).transform(canonical).getBody();
		return JSONUtils.getObjectMapper().readTree(body);
	}

	private static void assertEqualsString(String label, String expected, String actual) {
		if (expected == null ? actual != null : !expected.equals(actual)) {
			throw new RuntimeException("Assertion failed [" + label + "]: expected=[" + expected + "], actual=[" + actual + "]");
		}
	}

	private static void assertTrue(String label, boolean condition) {
		if (!condition) {
			throw new RuntimeException("Assertion failed [" + label + "]");
		}
	}
}
