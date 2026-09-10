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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.message.llm.CanonicalErrorType;
import org.openspcoop2.message.llm.stream.CanonicalStreamContentBlockStart;
import org.openspcoop2.message.llm.stream.CanonicalStreamContentBlockStop;
import org.openspcoop2.message.llm.stream.CanonicalStreamError;
import org.openspcoop2.message.llm.stream.CanonicalStreamEvent;
import org.openspcoop2.message.llm.stream.CanonicalStreamMessageDelta;
import org.openspcoop2.message.llm.stream.CanonicalStreamMessageStart;
import org.openspcoop2.message.llm.stream.CanonicalStreamMessageStop;
import org.openspcoop2.message.llm.stream.CanonicalStreamTextDelta;
import org.openspcoop2.message.llm.stream.LLMProviderRawChunk;
import org.openspcoop2.message.llm.stream.SseStreamReader;

/**
 * Smoke test del flow streaming OpenAI Chat Completions: costruisce in memoria una
 * sequenza SSE rappresentativa (primo chunk con role → delta di testo → chunk con
 * finish_reason → chunk 'usage-only' → [DONE]) e verifica il mapping su canonical
 * events, in particolare che l'usage finale venga veicolato nel {@code message_delta}
 * che precede il {@code message_stop} (forma attesa dai dialetti front-door).
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class OpenAIChatStreamSmokeTest {

	private static final String CHUNK_ENVELOPE = "\"id\":\"chatcmpl-abc\",\"object\":\"chat.completion.chunk\",\"created\":1757000000,\"model\":\"gpt-4o-mini\"";

	public static void main(String[] args) throws Exception {
		runFullStreamScenario();
		runFinishReasonWithoutUsageChunk();
		runMidStreamError();
		System.out.println("OpenAIChatStreamSmokeTest: ALL OK");
	}

	/**
	 * Scenario tipico con {@code stream_options.include_usage=true} (sempre forzato da GovWay):
	 * l'usage arriva nel chunk finale senza choices, dopo il chunk con finish_reason.
	 */
	public static void runFullStreamScenario() throws Exception {
		StringBuilder wire = new StringBuilder();
		appendChunk(wire, "\"choices\":[{\"index\":0,\"delta\":{\"role\":\"assistant\",\"content\":\"\"},\"finish_reason\":null}]");
		appendChunk(wire, "\"choices\":[{\"index\":0,\"delta\":{\"content\":\"Hello\"},\"finish_reason\":null}]");
		appendChunk(wire, "\"choices\":[{\"index\":0,\"delta\":{\"content\":\" world\"},\"finish_reason\":null}]");
		appendChunk(wire, "\"choices\":[{\"index\":0,\"delta\":{},\"finish_reason\":\"stop\"}]");
		appendChunk(wire, "\"choices\":[],\"usage\":{\"prompt_tokens\":12,\"completion_tokens\":3,\"total_tokens\":15}");
		wire.append("data: [DONE]\n\n");

		List<CanonicalStreamEvent> events = consumeStream(wire.toString());

		// Atteso:
		//   1. CanonicalStreamMessageStart(id=chatcmpl-abc, model=gpt-4o-mini)
		//   2. CanonicalStreamContentBlockStart(index=0, type=text)  -- IMPLICIT
		//   3. CanonicalStreamTextDelta(index=0, text="Hello")
		//   4. CanonicalStreamTextDelta(index=0, text=" world")
		//   5. CanonicalStreamContentBlockStop(index=0)
		//   6. CanonicalStreamMessageDelta(stopReason=end_turn, usage=12/3)  -- emesso sul chunk usage-only
		//   7. CanonicalStreamMessageStop()
		assertEqualsInt("event count", 7, events.size());

		assertInstanceOf("evt#1", CanonicalStreamMessageStart.class, events.get(0));
		CanonicalStreamMessageStart start = (CanonicalStreamMessageStart) events.get(0);
		assertEqualsString("evt#1 id", "chatcmpl-abc", start.getId());
		assertEqualsString("evt#1 model", "gpt-4o-mini", start.getModel());
		assertInstanceOf("evt#2", CanonicalStreamContentBlockStart.class, events.get(1));
		assertInstanceOf("evt#3", CanonicalStreamTextDelta.class, events.get(2));
		assertEqualsString("evt#3 text", "Hello", ((CanonicalStreamTextDelta) events.get(2)).getText());
		assertInstanceOf("evt#4", CanonicalStreamTextDelta.class, events.get(3));
		assertEqualsString("evt#4 text", " world", ((CanonicalStreamTextDelta) events.get(3)).getText());
		assertInstanceOf("evt#5", CanonicalStreamContentBlockStop.class, events.get(4));
		assertInstanceOf("evt#6", CanonicalStreamMessageDelta.class, events.get(5));
		CanonicalStreamMessageDelta md = (CanonicalStreamMessageDelta) events.get(5);
		assertEqualsString("evt#6 stopReason", "end_turn",
				md.getStopReason() != null ? md.getStopReason().getValue() : null);
		if (md.getUsage() == null) {
			throw new RuntimeException("evt#6 usage atteso non null");
		}
		assertEqualsInt("evt#6 inputTokens", 12, md.getUsage().getInputTokens());
		assertEqualsInt("evt#6 outputTokens", 3, md.getUsage().getOutputTokens());
		assertInstanceOf("evt#7", CanonicalStreamMessageStop.class, events.get(6));
	}

	/**
	 * Backend che non supporta {@code stream_options}: nessun chunk usage-only dopo il
	 * finish_reason. La chiusura del messaggio deve comunque essere emessa dal flush.
	 */
	public static void runFinishReasonWithoutUsageChunk() throws Exception {
		StringBuilder wire = new StringBuilder();
		appendChunk(wire, "\"choices\":[{\"index\":0,\"delta\":{\"role\":\"assistant\",\"content\":\"Hi\"},\"finish_reason\":null}]");
		appendChunk(wire, "\"choices\":[{\"index\":0,\"delta\":{},\"finish_reason\":\"length\"}]");
		wire.append("data: [DONE]\n\n");

		List<CanonicalStreamEvent> events = consumeStream(wire.toString());

		assertEqualsInt("event count", 6, events.size());
		assertInstanceOf("evt#5", CanonicalStreamMessageDelta.class, events.get(4));
		CanonicalStreamMessageDelta md = (CanonicalStreamMessageDelta) events.get(4);
		assertEqualsString("evt#5 stopReason", "max_tokens",
				md.getStopReason() != null ? md.getStopReason().getValue() : null);
		assertInstanceOf("evt#6", CanonicalStreamMessageStop.class, events.get(5));
	}


	/**
	 * Errore segnalato a stream avviato: il chunk con il solo campo {@code error} deve essere
	 * propagato come {@link CanonicalStreamError} e non silenziosamente ignorato.
	 */
	public static void runMidStreamError() throws Exception {
		StringBuilder wire = new StringBuilder();
		appendChunk(wire, "\"choices\":[{\"index\":0,\"delta\":{\"role\":\"assistant\",\"content\":\"Hi\"},\"finish_reason\":null}]");
		wire.append("data: {\"error\":{\"message\":\"The server had an error\",\"type\":\"server_error\",\"param\":null,\"code\":null}}\n\n");

		List<CanonicalStreamEvent> events = consumeStream(wire.toString());

		assertEqualsInt("event count", 4, events.size());
		assertInstanceOf("evt#4", CanonicalStreamError.class, events.get(3));
		CanonicalStreamError err = (CanonicalStreamError) events.get(3);
		assertEqualsString("evt#4 message", "The server had an error", err.getError().getMessage());
		assertEqualsString("evt#4 type", CanonicalErrorType.API_ERROR.name(), err.getError().getType().name());
	}


	/* === helpers di scenario === */

	private static void appendChunk(StringBuilder wire, String body) {
		wire.append("data: {").append(CHUNK_ENVELOPE).append(",").append(body).append("}\n\n");
	}

	private static List<CanonicalStreamEvent> consumeStream(String wire) throws Exception {
		SseStreamReader reader = new SseStreamReader();
		reader.bind(new ByteArrayInputStream(wire.getBytes(StandardCharsets.UTF_8)));
		OpenAIChatChunkDecoder decoder = new OpenAIChatChunkDecoder();
		List<CanonicalStreamEvent> all = new ArrayList<>();
		LLMProviderRawChunk chunk;
		while ((chunk = reader.readNextChunk()) != null) {
			all.addAll(decoder.decode(chunk));
		}
		all.addAll(decoder.flush());
		return all;
	}


	/* === assertions minimal === */

	private static void assertEqualsInt(String label, int expected, Integer actual) {
		if (actual == null || expected != actual) {
			throw new RuntimeException("Assertion failed [" + label + "]: expected=" + expected + ", actual=" + actual);
		}
	}

	private static void assertEqualsString(String label, String expected, String actual) {
		if (expected == null ? actual != null : !expected.equals(actual)) {
			throw new RuntimeException("Assertion failed [" + label + "]: expected=[" + expected + "], actual=[" + actual + "]");
		}
	}

	private static void assertInstanceOf(String label, Class<?> expected, Object actual) {
		if (actual == null || !expected.isInstance(actual)) {
			throw new RuntimeException("Assertion failed [" + label + "]: expected=" + expected.getSimpleName()
					+ ", actual=" + (actual != null ? actual.getClass().getSimpleName() : "null"));
		}
	}
}
