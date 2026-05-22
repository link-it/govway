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
package org.openspcoop2.message.llm.stream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Reader del transport binario AWS event-stream
 * (content-type {@code application/vnd.amazon.eventstream}), usato da Bedrock
 * Runtime per {@code InvokeModelWithResponseStream} e {@code ConverseStream}.
 *
 * <p>Formato di un frame:</p>
 * <pre>
 *   +--------------------+
 *   | TotalLen   (4B BE) |   include tutti i 4 campi (TotalLen .. MessageCrc)
 *   +--------------------+
 *   | HeadersLen (4B BE) |
 *   +--------------------+
 *   | PreludeCrc (4B BE) |   CRC-32 dei primi 8 byte (TotalLen + HeadersLen)
 *   +--------------------+
 *   | Headers (HeadersLen byte)
 *   +--------------------+
 *   | Payload (TotalLen - HeadersLen - 16 byte)
 *   +--------------------+
 *   | MessageCrc (4B BE) |   CRC-32 di tutti i byte precedenti del frame
 *   +--------------------+
 * </pre>
 * <p>Ogni header ha la forma {@code [NameLen:1B][Name][ValueType:1B][value]}.
 * Per Bedrock {@code ConverseStream} interessano solo gli header string (type 7):
 * {@code :event-type} (es. {@code messageStart}, {@code contentBlockDelta}) e
 * {@code :message-type} (di solito {@code event}; {@code exception}/{@code error}
 * indicano errore dal server).</p>
 *
 * <p>Per il prototipo i CRC non vengono validati: i frame arrivano da una
 * connessione TLS già autenticata via SigV4, l'overhead di validazione
 * aggiuntivo non aggiunge garanzie significative.</p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class AwsEventStreamReader implements LLMProviderStreamReader {

	/** Prelude = TotalLen(4) + HeadersLen(4) + PreludeCrc(4) = 12 byte. */
	private static final int PRELUDE_LEN = 12;

	/** Lunghezza fissa del trailer MessageCrc. */
	private static final int MESSAGE_CRC_LEN = 4;

	/**
	 * Dimensione massima ammessa per un singolo frame AWS event-stream. AWS limita i frame a 16 MB
	 * (sia per Bedrock ConverseStream sia per Kinesis); valori superiori indicano body corrotto o
	 * NON event-stream (es. AWS che risponde HTTP 4xx/5xx con JSON di errore: i primi 4 byte
	 * vengono interpretati come {@code TotalLen} BE uint32 e risultano in numeri enormi che
	 * causerebbero OOM allocando il buffer headers/payload).
	 */
	private static final int MAX_FRAME_SIZE = 16 * 1024 * 1024;

	/** Header standard AWS event-stream. */
	private static final String HEADER_EVENT_TYPE = ":event-type";
	private static final String HEADER_MESSAGE_TYPE = ":message-type";
	private static final String MESSAGE_TYPE_EVENT = "event";
	private static final String MESSAGE_TYPE_EXCEPTION = "exception";
	private static final String MESSAGE_TYPE_ERROR = "error";

	/** Codici dei value-type degli header (per saltare i campi che non leggiamo). */
	private static final int VT_BOOL_TRUE = 0;
	private static final int VT_BOOL_FALSE = 1;
	private static final int VT_BYTE = 2;
	private static final int VT_SHORT = 3;
	private static final int VT_INT = 4;
	private static final int VT_LONG = 5;
	private static final int VT_BYTE_BUF = 6;
	private static final int VT_STRING = 7;
	private static final int VT_TIMESTAMP = 8;
	private static final int VT_UUID = 9;

	private InputStream source;

	@Override
	public LLMProviderStreamTransport getTransport() {
		return LLMProviderStreamTransport.AWS_EVENT_STREAM;
	}

	@Override
	public void bind(InputStream source) {
		this.source = source;
	}

	@Override
	public LLMProviderRawChunk readNextChunk() throws IOException {
		if (this.source == null) {
			throw new IOException("AwsEventStreamReader non inizializzato: chiamare bind(InputStream) prima di readNextChunk()");
		}
		// Skippiamo silenziosamente i frame :message-type non interessanti (es. heartbeat)
		// e ritorniamo il primo frame "event" disponibile. EOF → null.
		while (true) {
			byte[] prelude = new byte[PRELUDE_LEN];
			int read = readFully(this.source, prelude, 0, PRELUDE_LEN);
			if (read == 0) {
				return null; // EOF pulito
			}
			if (read < PRELUDE_LEN) {
				throw new IOException("Frame AWS event-stream troncato: prelude di " + read + " byte (atteso " + PRELUDE_LEN + ")");
			}
			int totalLen = readInt32BE(prelude, 0);
			int headersLen = readInt32BE(prelude, 4);
			if (totalLen < PRELUDE_LEN + MESSAGE_CRC_LEN || totalLen > MAX_FRAME_SIZE
					|| headersLen < 0 || headersLen > totalLen - PRELUDE_LEN - MESSAGE_CRC_LEN) {
				// totalLen fuori range = body non in formato AWS event-stream (tipico errore HTTP 4xx/5xx
				// in cui AWS risponde con JSON puro: i primi 4 byte interpretati come uint32 BE risultano
				// in numeri enormi). Fail-fast con messaggio diagnostico, niente allocazione di buffer giganti.
				throw new IOException("Frame AWS event-stream malformato o body non event-stream: "
						+ "totalLen=" + totalLen + " (max " + MAX_FRAME_SIZE + "), headersLen=" + headersLen);
			}
			int payloadLen = totalLen - headersLen - PRELUDE_LEN - MESSAGE_CRC_LEN;

			byte[] headersBuf = new byte[headersLen];
			if (headersLen > 0 && readFully(this.source, headersBuf, 0, headersLen) < headersLen) {
				throw new IOException("Frame AWS event-stream troncato: headers");
			}
			byte[] payload = new byte[payloadLen];
			if (payloadLen > 0 && readFully(this.source, payload, 0, payloadLen) < payloadLen) {
				throw new IOException("Frame AWS event-stream troncato: payload");
			}
			byte[] crcBuf = new byte[MESSAGE_CRC_LEN];
			if (readFully(this.source, crcBuf, 0, MESSAGE_CRC_LEN) < MESSAGE_CRC_LEN) {
				throw new IOException("Frame AWS event-stream troncato: messageCrc");
			}

			ParsedHeaders parsed = parseHeaders(headersBuf);
			if (MESSAGE_TYPE_EXCEPTION.equals(parsed.messageType) || MESSAGE_TYPE_ERROR.equals(parsed.messageType)) {
				String payloadStr = new String(payload, StandardCharsets.UTF_8);
				throw new IOException("Bedrock event-stream " + parsed.messageType
						+ " (event-type=" + parsed.eventType + "): " + payloadStr);
			}
			if (!MESSAGE_TYPE_EVENT.equals(parsed.messageType)) {
				// :message-type sconosciuto o assente → skip al prossimo frame
				continue;
			}
			return new LLMProviderRawChunk(parsed.eventType, new String(payload, StandardCharsets.UTF_8));
		}
	}


	/**
	 * Legge un singolo frame AWS event-stream dallo stream e ritorna il payload come stringa UTF-8.
	 * A differenza di {@link #readNextChunk()}, non distingue tra {@code :message-type} {@code event}
	 * / {@code exception} / {@code error}: tutti i frame vengono ritornati come payload puro.
	 * <p>
	 * Pensato per il caso "errore HTTP 4xx/5xx in modalità streaming": Bedrock risponde con un
	 * singolo frame event-stream contenente JSON di errore (es. {@code {"message":"The provided
	 * model identifier is invalid."}}), e il chiamante vuole estrarre quel JSON per presentarlo al
	 * client senza i wrappi binari del prelude/headers.
	 * </p>
	 *
	 * @return payload del primo frame come stringa UTF-8, oppure {@code null} se lo stream è vuoto
	 *         (EOF al primo byte)
	 */
	public static String unwrapFirstFramePayload(InputStream source) throws IOException {
		byte[] prelude = new byte[PRELUDE_LEN];
		int read = readFully(source, prelude, 0, PRELUDE_LEN);
		if (read == 0) {
			return null;
		}
		if (read < PRELUDE_LEN) {
			throw new IOException("Frame AWS event-stream troncato: prelude di " + read + " byte");
		}
		int totalLen = readInt32BE(prelude, 0);
		int headersLen = readInt32BE(prelude, 4);
		if (totalLen < PRELUDE_LEN + MESSAGE_CRC_LEN || totalLen > MAX_FRAME_SIZE
				|| headersLen < 0 || headersLen > totalLen - PRELUDE_LEN - MESSAGE_CRC_LEN) {
			throw new IOException("Frame AWS event-stream malformato: totalLen=" + totalLen
					+ ", headersLen=" + headersLen);
		}
		int payloadLen = totalLen - headersLen - PRELUDE_LEN - MESSAGE_CRC_LEN;
		// Skippa headers (non ci interessano per il payload puro)
		if (headersLen > 0 && readFully(source, new byte[headersLen], 0, headersLen) < headersLen) {
			throw new IOException("Frame AWS event-stream troncato: headers");
		}
		byte[] payload = new byte[payloadLen];
		if (payloadLen > 0 && readFully(source, payload, 0, payloadLen) < payloadLen) {
			throw new IOException("Frame AWS event-stream troncato: payload");
		}
		if (readFully(source, new byte[MESSAGE_CRC_LEN], 0, MESSAGE_CRC_LEN) < MESSAGE_CRC_LEN) {
			throw new IOException("Frame AWS event-stream troncato: messageCrc");
		}
		return new String(payload, StandardCharsets.UTF_8);
	}


	/* === parsing helpers === */

	/** Wrapper degli header che ci interessano: event-type + message-type. */
	private static class ParsedHeaders {
		String eventType;
		String messageType;
	}

	private static ParsedHeaders parseHeaders(byte[] buf) throws IOException {
		ParsedHeaders out = new ParsedHeaders();
		int off = 0;
		while (off < buf.length) {
			if (off + 1 > buf.length) {
				throw new IOException("Header AWS event-stream: nameLen oltre il buffer (off=" + off + ")");
			}
			int nameLen = buf[off] & 0xff;
			off += 1;
			if (off + nameLen + 1 > buf.length) {
				throw new IOException("Header AWS event-stream: name oltre il buffer (off=" + off + ", nameLen=" + nameLen + ")");
			}
			String name = new String(buf, off, nameLen, StandardCharsets.US_ASCII);
			off += nameLen;
			int valueType = buf[off] & 0xff;
			off += 1;

			int valueSize = headerValueFixedSize(valueType);
			String stringValue = null;
			if (valueType == VT_STRING || valueType == VT_BYTE_BUF) {
				if (off + 2 > buf.length) {
					throw new IOException("Header AWS event-stream: valueLen oltre il buffer (name=" + name + ")");
				}
				int valueLen = ((buf[off] & 0xff) << 8) | (buf[off + 1] & 0xff);
				off += 2;
				if (off + valueLen > buf.length) {
					throw new IOException("Header AWS event-stream: value oltre il buffer (name=" + name + ")");
				}
				if (valueType == VT_STRING) {
					stringValue = new String(buf, off, valueLen, StandardCharsets.UTF_8);
				}
				off += valueLen;
			} else {
				if (off + valueSize > buf.length) {
					throw new IOException("Header AWS event-stream: value oltre il buffer (name=" + name + ", type=" + valueType + ")");
				}
				off += valueSize;
			}

			if (HEADER_EVENT_TYPE.equals(name) && stringValue != null) {
				out.eventType = stringValue;
			} else if (HEADER_MESSAGE_TYPE.equals(name) && stringValue != null) {
				out.messageType = stringValue;
			}
		}
		return out;
	}

	/** Dimensione fissa del value per i value-type non var-length; 0 se var-length. */
	private static int headerValueFixedSize(int valueType) {
		switch (valueType) {
			case VT_BOOL_TRUE:
			case VT_BOOL_FALSE:
				return 0;
			case VT_BYTE:
				return 1;
			case VT_SHORT:
				return 2;
			case VT_INT:
				return 4;
			case VT_LONG:
			case VT_TIMESTAMP:
				return 8;
			case VT_UUID:
				return 16;
			case VT_BYTE_BUF:
			case VT_STRING:
				return 0; // var-length: gestito separatamente
			default:
				return 0;
		}
	}

	private static int readInt32BE(byte[] buf, int off) {
		return ((buf[off] & 0xff) << 24)
				| ((buf[off + 1] & 0xff) << 16)
				| ((buf[off + 2] & 0xff) << 8)
				| (buf[off + 3] & 0xff);
	}

	/**
	 * Legge esattamente {@code len} byte dallo stream. Ritorna:
	 * <ul>
	 *   <li>{@code 0} se lo stream è alla fine al primo byte (EOF pulito)</li>
	 *   <li>il numero di byte letti se l'EOF arriva nel mezzo (frame troncato)</li>
	 *   <li>{@code len} se la lettura riesce completamente</li>
	 * </ul>
	 */
	private static int readFully(InputStream in, byte[] buf, int off, int len) throws IOException {
		int total = 0;
		while (total < len) {
			int n = in.read(buf, off + total, len - total);
			if (n < 0) {
				return total;
			}
			total += n;
		}
		return total;
	}
}
