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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.message.llm.stream.AwsEventStreamReader;
import org.openspcoop2.message.llm.stream.CanonicalStreamContentBlockStart;
import org.openspcoop2.message.llm.stream.CanonicalStreamContentBlockStop;
import org.openspcoop2.message.llm.stream.CanonicalStreamEvent;
import org.openspcoop2.message.llm.stream.CanonicalStreamMessageDelta;
import org.openspcoop2.message.llm.stream.CanonicalStreamMessageStart;
import org.openspcoop2.message.llm.stream.CanonicalStreamMessageStop;
import org.openspcoop2.message.llm.stream.CanonicalStreamTextDelta;
import org.openspcoop2.message.llm.stream.LLMProviderRawChunk;

/**
 * Smoke test del flow streaming Bedrock: costruisce in memoria una sequenza di
 * frame AWS event-stream rappresentativa di una response {@code converse-stream}
 * (messageStart → contentBlockDelta×2 → contentBlockStop → messageStop → metadata),
 * li dà in pasto a {@link AwsEventStreamReader} per il parsing binario e a
 * {@link AwsBedrockConverseChunkDecoder} per il mapping su canonical events,
 * verificando il numero/tipo/contenuto degli eventi emessi.
 * <p>
 * I CRC non sono validati dal reader (sono zero in questi frame sintetici).
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class AwsBedrockConverseStreamSmokeTest {

	public static void main(String[] args) throws Exception {
		runFullStreamScenario();
		runUnknownEventTypeIsSkipped();
		System.out.println("AwsBedrockConverseStreamSmokeTest: ALL OK");
	}

	/**
	 * Scenario tipico: messaggio assistant con due delta di testo e poi stop+metadata.
	 * Verifica che il flusso emetta gli eventi canonical attesi nell'ordine giusto,
	 * incluso l'implicit {@code content_block_start} che il decoder iniettato per il
	 * primo {@code contentBlockDelta(text)} (Bedrock non lo manda per i blocchi text).
	 */
	public static void runFullStreamScenario() throws Exception {
		ByteArrayOutputStream wire = new ByteArrayOutputStream();
		writeEventFrame(wire, "messageStart", "{\"role\":\"assistant\"}");
		writeEventFrame(wire, "contentBlockDelta", "{\"contentBlockIndex\":0,\"delta\":{\"text\":\"Hello\"}}");
		writeEventFrame(wire, "contentBlockDelta", "{\"contentBlockIndex\":0,\"delta\":{\"text\":\" world\"}}");
		writeEventFrame(wire, "contentBlockStop", "{\"contentBlockIndex\":0}");
		writeEventFrame(wire, "messageStop", "{\"stopReason\":\"end_turn\"}");
		writeEventFrame(wire, "metadata", "{\"usage\":{\"inputTokens\":12,\"outputTokens\":3,\"totalTokens\":15}}");

		List<CanonicalStreamEvent> events = consumeStream(wire.toByteArray());

		// Atteso:
		//   1. CanonicalStreamMessageStart(role=assistant)
		//   2. CanonicalStreamContentBlockStart(index=0, type=text)  -- IMPLICIT
		//   3. CanonicalStreamTextDelta(index=0, text="Hello")
		//   4. CanonicalStreamTextDelta(index=0, text=" world")
		//   5. CanonicalStreamContentBlockStop(index=0)
		//   6. CanonicalStreamMessageDelta(stopReason=end_turn)
		//   7. CanonicalStreamMessageStop()
		//   8. CanonicalStreamMessageDelta(usage=12/3)
		assertEqualsInt("event count", 8, events.size());

		assertInstanceOf("evt#1", CanonicalStreamMessageStart.class, events.get(0));
		assertInstanceOf("evt#2", CanonicalStreamContentBlockStart.class, events.get(1));
		CanonicalStreamContentBlockStart start = (CanonicalStreamContentBlockStart) events.get(1);
		assertEqualsString("evt#2 blockType", "text", start.getBlockType());
		assertInstanceOf("evt#3", CanonicalStreamTextDelta.class, events.get(2));
		assertEqualsString("evt#3 text", "Hello", ((CanonicalStreamTextDelta) events.get(2)).getText());
		assertInstanceOf("evt#4", CanonicalStreamTextDelta.class, events.get(3));
		assertEqualsString("evt#4 text", " world", ((CanonicalStreamTextDelta) events.get(3)).getText());
		assertInstanceOf("evt#5", CanonicalStreamContentBlockStop.class, events.get(4));
		assertInstanceOf("evt#6", CanonicalStreamMessageDelta.class, events.get(5));
		CanonicalStreamMessageDelta md1 = (CanonicalStreamMessageDelta) events.get(5);
		assertEqualsString("evt#6 stopReason", "end_turn",
				md1.getStopReason() != null ? md1.getStopReason().getValue() : null);
		assertInstanceOf("evt#7", CanonicalStreamMessageStop.class, events.get(6));
		assertInstanceOf("evt#8", CanonicalStreamMessageDelta.class, events.get(7));
		CanonicalStreamMessageDelta md2 = (CanonicalStreamMessageDelta) events.get(7);
		if (md2.getUsage() == null) {
			throw new RuntimeException("evt#8 usage atteso non null");
		}
		assertEqualsInt("evt#8 inputTokens", 12, md2.getUsage().getInputTokens());
		assertEqualsInt("evt#8 outputTokens", 3, md2.getUsage().getOutputTokens());
	}

	/**
	 * Forward compatibility: un event-type sconosciuto deve essere ignorato senza
	 * fallire il decoder (ritorna lista vuota).
	 */
	public static void runUnknownEventTypeIsSkipped() throws Exception {
		ByteArrayOutputStream wire = new ByteArrayOutputStream();
		writeEventFrame(wire, "newFancyEvent_2027", "{\"anything\":true}");
		writeEventFrame(wire, "messageStop", "{\"stopReason\":\"end_turn\"}");
		List<CanonicalStreamEvent> events = consumeStream(wire.toByteArray());
		// 0 dal primo (sconosciuto), 2 dal messageStop (delta + stop) → 2 totale
		assertEqualsInt("count su unknown+stop", 2, events.size());
		assertInstanceOf("evt#1 (atteso messageDelta)", CanonicalStreamMessageDelta.class, events.get(0));
		assertInstanceOf("evt#2 (atteso messageStop)", CanonicalStreamMessageStop.class, events.get(1));
	}


	/* === helpers di scenario === */

	private static List<CanonicalStreamEvent> consumeStream(byte[] wire) throws Exception {
		AwsEventStreamReader reader = new AwsEventStreamReader();
		reader.bind(new ByteArrayInputStream(wire));
		AwsBedrockConverseChunkDecoder decoder = new AwsBedrockConverseChunkDecoder();
		List<CanonicalStreamEvent> all = new ArrayList<>();
		LLMProviderRawChunk chunk;
		while ((chunk = reader.readNextChunk()) != null) {
			all.addAll(decoder.decode(chunk));
		}
		return all;
	}


	/* === frame builder (AWS event-stream binario, CRC zero perché il reader non li valida) === */

	private static void writeEventFrame(ByteArrayOutputStream out, String eventType, String payloadJson) {
		byte[] payloadBytes = payloadJson.getBytes(StandardCharsets.UTF_8);

		ByteArrayOutputStream hbuf = new ByteArrayOutputStream();
		writeStringHeader(hbuf, ":event-type", eventType);
		writeStringHeader(hbuf, ":message-type", "event");
		writeStringHeader(hbuf, ":content-type", "application/json");
		byte[] headers = hbuf.toByteArray();

		int totalLen = 12 /* prelude */ + headers.length + payloadBytes.length + 4 /* messageCrc */;
		writeInt32BE(out, totalLen);
		writeInt32BE(out, headers.length);
		writeInt32BE(out, 0); // preludeCrc — reader prototipo non valida
		out.write(headers, 0, headers.length);
		out.write(payloadBytes, 0, payloadBytes.length);
		writeInt32BE(out, 0); // messageCrc — reader prototipo non valida
	}

	private static void writeStringHeader(ByteArrayOutputStream out, String name, String value) {
		byte[] nameBytes = name.getBytes(StandardCharsets.US_ASCII);
		byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
		out.write(nameBytes.length & 0xff);
		out.write(nameBytes, 0, nameBytes.length);
		out.write(7 & 0xff); // value type = STRING
		out.write((valueBytes.length >> 8) & 0xff);
		out.write(valueBytes.length & 0xff);
		out.write(valueBytes, 0, valueBytes.length);
	}

	private static void writeInt32BE(ByteArrayOutputStream out, int v) {
		out.write((v >> 24) & 0xff);
		out.write((v >> 16) & 0xff);
		out.write((v >> 8) & 0xff);
		out.write(v & 0xff);
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
					+ ", actual=" + (actual == null ? "null" : actual.getClass().getSimpleName()));
		}
	}
}
