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

import org.openspcoop2.message.llm.CanonicalError;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Smoke test della normalizzazione degli errori: verifica che il body di errore nativo di
 * ciascun provider supportato, e il problem detail generato dal gateway stesso, vengano
 * riscritti nell'envelope di errore atteso dal client, per entrambi i dialetti front-door.
 * Un errore deve arrivare al client nella forma del dialetto con cui il client si è
 * affacciato, qualunque ne sia l'origine; sulle API non LLM nulla deve essere convertito.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMErrorMappingSmokeTest {

	private static final String PROBLEM_TOO_MANY_REQUESTS = "{\"type\":\"https://govway.org/handling-errors/429/TooManyRequests.html\","
			+ "\"title\":\"TooManyRequests\",\"status\":429,\"detail\":\"Numero di richieste consentite superato\"}";

	public static void main(String[] args) throws Exception {
		runBedrockErrorToAnthropic();
		runOpenAIErrorToAnthropic();
		runAnthropicErrorToOpenAI();
		runNonJsonErrorBody();
		runGatewayErrorToAnthropic();
		runGatewayErrorToOpenAI();
		runGatewayErrorNonLLMApiIsUntouched();
		System.out.println("LLMErrorMappingSmokeTest: ALL OK");
	}

	/** Bedrock risponde con {@code {"message":...}}: verso un client Anthropic serve l'envelope Anthropic. */
	public static void runBedrockErrorToAnthropic() throws Exception {
		JsonNode out = map(LLMProviders.AWS_BEDROCK, 400,
				"{\"message\":\"The provided model identifier is invalid.\"}", LLMDialect.ANTHROPIC_MESSAGES_V1);
		assertEqualsString("type", "error", out.path("type").asText());
		assertEqualsString("error.type", "invalid_request_error", out.path("error").path("type").asText());
		assertEqualsString("error.message", "The provided model identifier is invalid.", out.path("error").path("message").asText());
	}

	/** OpenAI risponde con {@code {"error":{...}}}: il code nativo viene preservato nel messaggio. */
	public static void runOpenAIErrorToAnthropic() throws Exception {
		JsonNode out = map(LLMProviders.OPENAI, 401,
				"{\"error\":{\"message\":\"Incorrect API key provided\",\"type\":\"invalid_request_error\",\"code\":\"invalid_api_key\"}}",
				LLMDialect.ANTHROPIC_MESSAGES_V1);
		assertEqualsString("error.type", "authentication_error", out.path("error").path("type").asText());
		assertEqualsString("error.message", "invalid_api_key: Incorrect API key provided", out.path("error").path("message").asText());
	}

	/** Verso un client OpenAI l'envelope è quello OpenAI, con param e code sempre presenti. */
	public static void runAnthropicErrorToOpenAI() throws Exception {
		JsonNode out = map(LLMProviders.ANTHROPIC, 529,
				"{\"type\":\"error\",\"error\":{\"type\":\"overloaded_error\",\"message\":\"Overloaded\"}}",
				LLMDialect.OPENAI_CHAT_V1);
		assertEqualsString("error.type", "server_error", out.path("error").path("type").asText());
		assertEqualsString("error.message", "Overloaded", out.path("error").path("message").asText());
		assertEqualsString("error.code", "overloaded_error", out.path("error").path("code").asText());
		if (!out.path("error").has("param")) {
			throw new RuntimeException("Assertion failed [error.param]: campo assente");
		}
	}

	/** Body non JSON (es. pagina di errore di un intermediario): il testo grezzo non va perso. */
	public static void runNonJsonErrorBody() throws Exception {
		JsonNode out = map(LLMProviders.AWS_BEDROCK, 502, "<html>Bad gateway</html>", LLMDialect.ANTHROPIC_MESSAGES_V1);
		assertEqualsString("error.type", "api_error", out.path("error").path("type").asText());
		assertEqualsString("error.message", "<html>Bad gateway</html>", out.path("error").path("message").asText());
	}


	/**
	 * Errore generato dal gateway (violazione policy di rate limiting): il problem detail
	 * RFC7807 va restituito al client nell'envelope Anthropic.
	 */
	public static void runGatewayErrorToAnthropic() throws Exception {
		JsonNode out = mapGatewayError(LLMDialect.ANTHROPIC_MESSAGES_V1, 429, PROBLEM_TOO_MANY_REQUESTS);
		assertEqualsString("type", "error", out.path("type").asText());
		assertEqualsString("error.type", "rate_limit_error", out.path("error").path("type").asText());
		assertEqualsString("error.message", "TooManyRequests: Numero di richieste consentite superato",
				out.path("error").path("message").asText());
	}

	/** Stesso errore del gateway, client OpenAI: envelope OpenAI. */
	public static void runGatewayErrorToOpenAI() throws Exception {
		JsonNode out = mapGatewayError(LLMDialect.OPENAI_CHAT_V1, 429, PROBLEM_TOO_MANY_REQUESTS);
		assertEqualsString("error.type", "invalid_request_error", out.path("error").path("type").asText());
		assertEqualsString("error.message", "Numero di richieste consentite superato", out.path("error").path("message").asText());
		assertEqualsString("error.code", "TooManyRequests", out.path("error").path("code").asText());
	}

	/**
	 * API non LLM: il contesto non dichiara alcun dialetto, la conversione non deve avvenire
	 * e il problem detail originale deve restare inalterato.
	 */
	public static void runGatewayErrorNonLLMApiIsUntouched() throws Exception {
		org.openspcoop2.utils.Map<Object> context = new org.openspcoop2.utils.Map<>();
		byte[] out = LLMFrontDoorErrorEnvelope.convert(context, PROBLEM_TOO_MANY_REQUESTS.getBytes(StandardCharsets.UTF_8), 429);
		if (out != null) {
			throw new RuntimeException("Assertion failed [api non LLM]: attesa nessuna conversione, ottenuto " + new String(out, StandardCharsets.UTF_8));
		}
		if (LLMFrontDoorErrorEnvelope.convert(null, PROBLEM_TOO_MANY_REQUESTS.getBytes(StandardCharsets.UTF_8), 429) != null) {
			throw new RuntimeException("Assertion failed [context null]: attesa nessuna conversione");
		}
	}


	/* === helpers === */

	private static JsonNode mapGatewayError(LLMDialect dialect, int httpStatus, String problemDetail) throws Exception {
		org.openspcoop2.utils.Map<Object> context = new org.openspcoop2.utils.Map<>();
		context.addObject(org.openspcoop2.message.llm.LLMContextKeys.PDD_CTX_LLM_FORMATO, dialect);
		byte[] out = LLMFrontDoorErrorEnvelope.convert(context, problemDetail.getBytes(StandardCharsets.UTF_8), httpStatus);
		if (out == null) {
			throw new RuntimeException("Assertion failed [conversione errore gateway]: nessun envelope prodotto");
		}
		return JSONUtils.getObjectMapper().readTree(out);
	}

	private static JsonNode map(String providerId, int httpStatus, String providerBody, LLMDialect dialect) throws Exception {
		CanonicalError error = LLMTransformerRegistry.getInboundProviderResponseTransformer(providerId)
				.transformError(providerBody.getBytes(StandardCharsets.UTF_8), httpStatus);
		byte[] out = LLMTransformerRegistry.getOutboundFrontDoorResponseTransformer(dialect).transformError(error);
		return JSONUtils.getObjectMapper().readTree(out);
	}

	private static void assertEqualsString(String label, String expected, String actual) {
		if (expected == null ? actual != null : !expected.equals(actual)) {
			throw new RuntimeException("Assertion failed [" + label + "]: expected=[" + expected + "], actual=[" + actual + "]");
		}
	}
}
