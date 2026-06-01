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
package org.openspcoop2.utils.transport.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Strumenti di inferenza per Content-Type 'multipart/related' non conformi a RFC 2387 §3.1
 * (parametro 'type' assente).
 *
 * Le strategie supportate (richiamate da {@link MultipartMissingTypeBehavior}) prevedono
 * sia la derivazione dichiarativa dalla versione SOAP nota della richiesta, sia
 * l'ispezione del payload alla ricerca del Content-Type della root part del multipart.
 * Quando si ispeziona il payload lo stream viene ricostruito tramite {@link SequenceInputStream}
 * cosicché i consumer a valle (parser, notifier, dump) vedano gli stessi byte nello stesso ordine.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class MultipartRelatedHeuristics {

	private MultipartRelatedHeuristics() {}


	/**
	 * Esito dell'ispezione del payload.
	 */
	public static final class PeekResult {

		private final String inferredType;
		private final InputStream reconstructed;
		private final int peekedBytes;

		PeekResult(String inferredType, InputStream reconstructed, int peekedBytes) {
			this.inferredType = inferredType;
			this.reconstructed = reconstructed;
			this.peekedBytes = peekedBytes;
		}

		/** @return media type della root part (es. "text/xml") oppure null se non determinato. */
		public String getInferredType() {
			return this.inferredType;
		}

		/** @return stream da utilizzare a valle; espone gli stessi byte dello stream originale. */
		public InputStream getReconstructed() {
			return this.reconstructed;
		}

		/** @return numero di byte effettivamente letti durante il peek. */
		public int getPeekedBytes() {
			return this.peekedBytes;
		}
	}


	/**
	 * Deriva il valore del parametro 'type' del Content-Type 'multipart/related' a partire dalla
	 * versione SOAP nota di un messaggio.
	 *
	 * @return "text/xml" per SOAP 1.1, "application/soap+xml" per SOAP 1.2, null altrimenti
	 */
	public static String inferTypeFromSoapVersion(String messageTypeName) {
		if (messageTypeName == null) {
			return null;
		}
		if ("SOAP_11".equalsIgnoreCase(messageTypeName)) {
			return HttpConstants.CONTENT_TYPE_SOAP_1_1;
		}
		if ("SOAP_12".equalsIgnoreCase(messageTypeName)) {
			return HttpConstants.CONTENT_TYPE_SOAP_1_2;
		}
		return null;
	}


	/**
	 * Estrae il valore del parametro 'boundary' da un Content-Type 'multipart/related'.
	 * @return il boundary senza virgolette, oppure null se non estraibile.
	 */
	public static String extractBoundary(String contentType) {
		if (contentType == null) {
			return null;
		}
		try {
			return ContentTypeUtilities.readMultipartBoundaryFromContentType(contentType);
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * Legge fino a <code>maxBytes</code> dallo stream originale alla ricerca del Content-Type
	 * della prima parte di un multipart/related individuata dal <code>boundary</code> indicato,
	 * quindi ricostruisce uno stream equivalente all'originale tramite {@link SequenceInputStream}.
	 *
	 * Il metodo si interrompe non appena il Content-Type viene determinato, anche se sono stati
	 * letti meno byte di <code>maxBytes</code>. <code>maxBytes</code> rappresenta quindi
	 * un upper bound difensivo, non la quantità sempre letta.
	 *
	 * @param src        stream sorgente, da consumare al massimo per <code>maxBytes</code> byte
	 * @param boundary   valore del parametro 'boundary' (senza il prefisso "--")
	 * @param maxBytes   numero massimo di byte ispezionabili
	 * @return un {@link PeekResult} con il media type dedotto (eventualmente null) e lo stream ricostruito
	 */
	public static PeekResult peekFirstPartContentType(InputStream src, String boundary, int maxBytes) throws IOException {
		if (src == null) {
			throw new IOException("InputStream is null");
		}
		if (maxBytes <= 0) {
			return new PeekResult(null, src, 0);
		}
		if (boundary == null || boundary.isEmpty()) {
			// niente boundary → non sappiamo cosa cercare: ritorniamo subito lo stream invariato
			return new PeekResult(null, src, 0);
		}

		ByteArrayOutputStream buf = new ByteArrayOutputStream(Math.min(maxBytes, 1024));
		byte[] chunk = new byte[512];
		int totalRead = 0;
		String inferred = null;
		byte[] marker = ("--" + boundary).getBytes(StandardCharsets.US_ASCII);

		// Si esce dal ciclo quando il Content-Type della root part è stato dedotto,
		// oppure è stato raggiunto il limite di byte ispezionabili, oppure lo stream
		// non ha più byte disponibili.
		while (totalRead < maxBytes && inferred == null) {
			int toRead = Math.min(chunk.length, maxBytes - totalRead);
			int n = src.read(chunk, 0, toRead);
			if (n <= 0) {
				// fine stream o nessun dato disponibile: usciamo per non bloccare
				break;
			}
			buf.write(chunk, 0, n);
			totalRead += n;
			inferred = tryExtractFirstPartContentType(buf.toByteArray(), marker);
		}

		byte[] peeked = buf.toByteArray();
		InputStream reconstructed;
		if (peeked.length > 0) {
			reconstructed = new SequenceInputStream(new ByteArrayInputStream(peeked), src);
		} else {
			reconstructed = src;
		}
		return new PeekResult(inferred, reconstructed, peeked.length);
	}


	private static String tryExtractFirstPartContentType(byte[] data, byte[] marker) {
		int boundaryIdx = indexOf(data, marker, 0);
		if (boundaryIdx < 0) {
			return null;
		}
		// posizione subito dopo il marker; il marker può essere seguito da
		// "--" (close delimiter) o da line break + header della parte
		int afterMarker = boundaryIdx + marker.length;
		// skip eventuali "--" del close delimiter (in questo caso non ci sono header dopo)
		if (afterMarker + 1 < data.length && data[afterMarker] == '-' && data[afterMarker + 1] == '-') {
			return null;
		}
		int headersStart = skipLineEnd(data, afterMarker);
		if (headersStart < 0) {
			// non abbiamo ancora ricevuto il line-end dopo il primo boundary
			return null;
		}
		int headersEnd = indexOfBlankLine(data, headersStart);
		if (headersEnd < 0) {
			// blocco header non ancora completo
			return null;
		}
		String headersBlock = new String(data, headersStart, headersEnd - headersStart, StandardCharsets.ISO_8859_1);
		String ctRaw = findHeaderValue(headersBlock, HttpConstants.CONTENT_TYPE);
		if (ctRaw == null) {
			// la specifica RFC 2046 non rende obbligatorio il Content-Type per le sotto-parti
			// (default = text/plain), ma per SOAP-with-Attachments la root part è sempre esplicita.
			return null;
		}
		try {
			return ContentTypeUtilities.readBaseTypeFromContentType(ctRaw);
		} catch (Exception e) {
			return null;
		}
	}

	private static int indexOf(byte[] data, byte[] pattern, int from) {
		if (pattern.length == 0) {
			return from;
		}
		int last = data.length - pattern.length;
		for (int i = from; i <= last; i++) {
			if (matchesAt(data, pattern, i)) {
				return i;
			}
		}
		return -1;
	}

	private static boolean matchesAt(byte[] data, byte[] pattern, int offset) {
		for (int j = 0; j < pattern.length; j++) {
			if (data[offset + j] != pattern[j]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Avanza l'indice passando l'eventuale "\r\n" o "\n" che termina la linea del boundary.
	 * Ritorna -1 se non viene trovato (il blocco non è ancora completo).
	 */
	private static int skipLineEnd(byte[] data, int from) {
		int i = from;
		// tollero whitespace LWSP residuo (es. trailing space) prima del CRLF
		while (i < data.length && (data[i] == ' ' || data[i] == '\t')) {
			i++;
		}
		if (i >= data.length) {
			return -1;
		}
		if (data[i] == '\r') {
			i++;
			if (i < data.length && data[i] == '\n') {
				i++;
			}
			return i;
		}
		if (data[i] == '\n') {
			return i + 1;
		}
		return -1;
	}

	/**
	 * Cerca l'inizio della "blank line" (\r\n\r\n o \n\n) che termina il blocco di header.
	 * Ritorna l'indice del primo \n della blank line stessa. Ritorna -1 se non trova.
	 */
	private static int indexOfBlankLine(byte[] data, int from) {
		for (int i = from; i < data.length; i++) {
			if (data[i] != '\n') {
				continue;
			}
			int next = i + 1;
			if (next < data.length && data[next] == '\r') {
				next++;
			}
			if (next < data.length && data[next] == '\n') {
				return i;
			}
		}
		return -1;
	}

	private static String findHeaderValue(String headersBlock, String headerName) {
		// parsing semplice: linee terminate da \r\n o \n, header-name seguito da ':'.
		// Non supporto folding RFC 5322 (continuation line) — in pratica non avviene nel Content-Type di una sotto-parte SOAP.
		int from = 0;
		int len = headersBlock.length();
		String target = headerName.toLowerCase();
		while (from < len) {
			int eol = headersBlock.indexOf('\n', from);
			int lineEnd = eol < 0 ? len : eol;
			int lineStart = from;
			// trim \r finale
			int actualEnd = lineEnd;
			if (actualEnd > lineStart && headersBlock.charAt(actualEnd - 1) == '\r') {
				actualEnd--;
			}
			String line = headersBlock.substring(lineStart, actualEnd);
			int colon = line.indexOf(':');
			if (colon > 0) {
				String name = line.substring(0, colon).trim().toLowerCase();
				if (name.equals(target)) {
					return line.substring(colon + 1).trim();
				}
			}
			if (eol < 0) {
				break;
			}
			from = eol + 1;
		}
		return null;
	}
}
