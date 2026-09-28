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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * Utilità comuni alle batterie di test sulla gestione del Content-Encoding
 * ({@link ProblemContentEncodingEngine}, {@link ContentTypeContentEncodingEngine},
 * {@link SoapContentEncodingEngine}, {@link FunzionalitaContentEncodingEngine}).
 *
 * <p>Gli encoding sono descritti dalla codifica realmente applicata ai byte e dal valore dichiarato
 * nell'header Content-Encoding, con la stessa semantica di {@link ContentEncodingMockServer} per la
 * risposta e di {@link #encodeForRequest(byte[], Enc)} per la richiesta.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentEncodingTestUtils {

	private ContentEncodingTestUtils() {}

	/** Esito atteso lato interpretazione del contenuto. */
	public enum Kind {
		/** Contenuto in chiaro o con un encoding supportato e byte validi. */
		PARSED,
		/** Content-Encoding non supportato (byte opachi). */
		UNSUPPORTED,
		/** Content-Encoding supportato ma byte non decomprimibili. */
		DECOMPRESSION_FAILED
	}

	/**
	 * Encoding: codifica realmente applicata ai byte (valori di {@link ContentEncodingMockServer}),
	 * eventuale valore dichiarato nell'header Content-Encoding diverso da quello standard, esito atteso.
	 */
	public enum Enc {
		NONE(ContentEncodingMockServer.REPLY_ENCODING_NONE, null, Kind.PARSED),
		IDENTITY(ContentEncodingMockServer.REPLY_ENCODING_IDENTITY, null, Kind.PARSED),
		GZIP(ContentEncodingMockServer.REPLY_ENCODING_GZIP, null, Kind.PARSED),
		X_GZIP(ContentEncodingMockServer.REPLY_ENCODING_X_GZIP, null, Kind.PARSED),
		DEFLATE_ZLIB(ContentEncodingMockServer.REPLY_ENCODING_DEFLATE_ZLIB, null, Kind.PARSED),
		DEFLATE_RAW(ContentEncodingMockServer.REPLY_ENCODING_DEFLATE_RAW, null, Kind.PARSED),
		/** Il match dell'encoding è case-insensitive. */
		GZIP_UPPERCASE(ContentEncodingMockServer.REPLY_ENCODING_GZIP, "GZIP", Kind.PARSED),
		/* Encoding non supportati: byte opachi (gzip) dichiarati con l'encoding non supportato. */
		BR(ContentEncodingMockServer.REPLY_ENCODING_GZIP, "br", Kind.UNSUPPORTED),
		ZSTD(ContentEncodingMockServer.REPLY_ENCODING_GZIP, "zstd", Kind.UNSUPPORTED),
		COMPRESS(ContentEncodingMockServer.REPLY_ENCODING_GZIP, "compress", Kind.UNSUPPORTED),
		X_COMPRESS(ContentEncodingMockServer.REPLY_ENCODING_GZIP, "x-compress", Kind.UNSUPPORTED),
		/** Doppia codifica reale 'deflate, gzip': non supportata. */
		DEFLATE_GZIP(ContentEncodingMockServer.REPLY_ENCODING_DEFLATE_GZIP, null, Kind.UNSUPPORTED),
		UNKNOWN(ContentEncodingMockServer.REPLY_ENCODING_GZIP, "govway-unknown", Kind.UNSUPPORTED),
		/* Encoding supportato ma byte non validi. */
		GZIP_INVALID(ContentEncodingMockServer.REPLY_ENCODING_NONE, "gzip", Kind.DECOMPRESSION_FAILED),
		GZIP_TRUNCATED(ContentEncodingMockServer.REPLY_ENCODING_GZIP_TRUNCATED, null, Kind.DECOMPRESSION_FAILED);

		public final String replyEncoding;
		public final String declaredOverride;
		public final Kind kind;
		Enc(String replyEncoding, String declaredOverride, Kind kind) {
			this.replyEncoding = replyEncoding;
			this.declaredOverride = declaredOverride;
			this.kind = kind;
		}
		/** true se i byte sono decodificabili lato test con {@link ContentEncodingTestUtils#clientDecode}. */
		public boolean isClientDecodable() {
			return this.kind == Kind.PARSED;
		}
		/** true se il contenuto è in chiaro (nessuna codifica effettiva). */
		public boolean isPlain() {
			return this == NONE || this == IDENTITY;
		}
	}

	public static String buildUrl(TipoServizio tipo, String api, String resource) {
		String base = System.getProperty("govway_base_path");
		if (TipoServizio.EROGAZIONE.equals(tipo)) {
			return base + "/in/SoggettoInternoTest/" + api + "/v1/" + resource;
		}
		return base + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/" + api + "/v1/" + resource;
	}

	/** Valore dell'header Content-Encoding dichiarato dal mock per l'encoding indicato ("" = nessun header). */
	public static String declaredEncoding(Enc enc) {
		if (enc.declaredOverride != null) {
			return enc.declaredOverride;
		}
		switch (enc) {
		case NONE:
			return "";
		case DEFLATE_ZLIB:
		case DEFLATE_RAW:
			return HttpConstants.CONTENT_ENCODING_VALUE_DEFLATE;
		case DEFLATE_GZIP:
			return "deflate, gzip";
		case GZIP_TRUNCATED:
			return HttpConstants.CONTENT_ENCODING_VALUE_GZIP;
		default:
			return enc.replyEncoding;
		}
	}

	/** Content-Encoding dichiarato dal client per la richiesta ("" = nessun header). */
	public static String requestDeclaredEncoding(Enc enc) {
		return declaredEncoding(enc);
	}

	/** Codifica lato client del body di richiesta, con la stessa semantica del mock per la risposta. */
	public static byte[] encodeForRequest(byte[] plain, Enc enc) throws IOException {
		switch (enc) {
		case NONE:
		case IDENTITY:
		case GZIP_INVALID:
			return plain;
		case DEFLATE_ZLIB:
			return deflate(plain, false);
		case DEFLATE_RAW:
			return deflate(plain, true);
		case DEFLATE_GZIP:
			return gzip(deflate(plain, false));
		case GZIP_TRUNCATED:
			byte[] gz = gzip(plain);
			return Arrays.copyOf(gz, gz.length / 2);
		default:
			/* gzip, x-gzip, GZIP e gli encoding non supportati (byte gzip opachi) */
			return gzip(plain);
		}
	}

	public static byte[] gzip(byte[] input) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (GZIPOutputStream gz = new GZIPOutputStream(baos)) {
			gz.write(input);
		}
		return baos.toByteArray();
	}

	public static byte[] deflate(byte[] input, boolean raw) throws IOException {
		Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, raw);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (DeflaterOutputStream d = new DeflaterOutputStream(baos, deflater)) {
			d.write(input);
		} finally {
			deflater.end();
		}
		return baos.toByteArray();
	}

	public static byte[] clientDecode(byte[] encoded, Enc enc) throws IOException {
		switch (enc) {
		case GZIP:
		case X_GZIP:
		case GZIP_UPPERCASE:
			try (GZIPInputStream gz = new GZIPInputStream(new ByteArrayInputStream(encoded))) {
				return gz.readAllBytes();
			}
		case DEFLATE_ZLIB:
			try (InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(encoded))) {
				return in.readAllBytes();
			}
		case DEFLATE_RAW:
			try (InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(encoded), new Inflater(true))) {
				return in.readAllBytes();
			}
		default:
			return encoded;
		}
	}

	public static String toHex(byte[] buf, int maxBytes) {
		StringBuilder sb = new StringBuilder();
		if (buf == null) {
			return "";
		}
		for (int i = 0; i < Math.min(maxBytes, buf.length); i++) {
			if (i > 0) {
				sb.append(' ');
			}
			sb.append(String.format("%02x", buf[i] & 0xff));
		}
		return sb.toString();
	}

	/** Il body ricevuto dal client deve coincidere byte per byte con quello inviato dal mock, Content-Encoding compreso. */
	public static void verifyBodyIdentico(HttpResponse response, String label) {
		verifyBodyIdentico(response, true, label);
	}
	/**
	 * @param contentEncodingPropagato false quando la configurazione non propaga gli header HTTP (es. SOAP nella testsuite):
	 *        per un contenuto in chiaro il Content-Encoding (es. 'identity') può quindi non essere presente
	 */
	public static void verifyBodyIdentico(HttpResponse response, boolean contentEncodingPropagato, String label) {
		byte[] body = response.getContent() != null ? response.getContent() : new byte[0];
		String sentSha = response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_SENT_BODY_SHA256);
		String sentBytes = response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_SENT_BODY_BYTES);
		String sentCE = response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_SENT_CONTENT_ENCODING);
		assertNotNull(label + " manca header " + ContentEncodingMockServer.HEADER_SENT_BODY_SHA256, sentSha);
		assertEquals(label + " dimensione del body alterata (inviati dal backend " + sentBytes + " bytes)",
				sentBytes, String.valueOf(body.length));
		assertEquals(label + " body alterato rispetto a quello inviato dal backend (primi byte ricevuti: " + toHex(body, 8) + ")",
				sentSha, ContentEncodingMockServer.sha256Hex(body));
		String ce = response.getHeaderFirstValue(HttpConstants.CONTENT_ENCODING);
		if (sentCE == null || sentCE.isEmpty()) {
			assertTrue(label + " Content-Encoding inatteso: " + ce, ce == null || ce.isEmpty());
		} else if (!contentEncodingPropagato && HttpConstants.CONTENT_ENCODING_VALUE_IDENTITY.equalsIgnoreCase(sentCE)) {
			assertTrue(label + " Content-Encoding inatteso: " + ce, ce == null || ce.isEmpty() || HttpConstants.CONTENT_ENCODING_VALUE_IDENTITY.equalsIgnoreCase(ce));
		} else {
			assertEquals(label + " Content-Encoding non preservato", sentCE, ce);
		}
	}

	public static void verifyNoContentEncoding(HttpResponse response, String label) {
		String ce = response.getHeaderFirstValue(HttpConstants.CONTENT_ENCODING);
		assertTrue(label + " atteso nessun Content-Encoding nella risposta al client, ricevuto: " + ce,
				ce == null || ce.isEmpty() || HttpConstants.CONTENT_ENCODING_VALUE_IDENTITY.equalsIgnoreCase(ce));
	}

	/**
	 * Verifica, tramite gli header di echo del mock, che il backend abbia ricevuto esattamente i byte e il
	 * Content-Encoding inviati dal client.
	 */
	public static void verifyBackendRicevutoIdentico(HttpResponse response, byte[] onWire, Enc enc, String label) {
		verifyBackendRicevutoIdentico(response, onWire, enc, true, label);
	}
	/**
	 * @param contentEncodingPropagato false quando la configurazione non propaga gli header HTTP (es. SOAP nella testsuite):
	 *        per un contenuto in chiaro il Content-Encoding (es. 'identity') può quindi non arrivare al backend
	 */
	public static void verifyBackendRicevutoIdentico(HttpResponse response, byte[] onWire, Enc enc, boolean contentEncodingPropagato, String label) {
		String receivedCE = valueOrEmpty(response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_CONTENT_ENCODING));
		if (!contentEncodingPropagato && enc.isPlain()) {
			assertTrue(label + " Content-Encoding ricevuto dal backend inatteso: " + receivedCE,
					receivedCE.isEmpty() || HttpConstants.CONTENT_ENCODING_VALUE_IDENTITY.equalsIgnoreCase(receivedCE));
		}
		else {
			assertEquals(label + " Content-Encoding ricevuto dal backend", requestDeclaredEncoding(enc), receivedCE);
		}
		assertEquals(label + " dimensione del body ricevuto dal backend", String.valueOf(onWire.length),
				response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_BODY_BYTES));
		assertEquals(label + " primi byte del body ricevuto dal backend", toHex(onWire, 8),
				valueOrEmpty(response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_BODY_MAGIC)));
		assertEquals(label + " body ricevuto dal backend alterato rispetto a quello inviato dal client (sha256)",
				ContentEncodingMockServer.sha256Hex(onWire), response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_BODY_SHA256));
	}

	/** Verifica che il backend abbia ricevuto il contenuto in chiaro, senza Content-Encoding. */
	public static void verifyBackendRicevutoInChiaro(HttpResponse response, byte[] plain, String label) {
		String receivedCE = response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_CONTENT_ENCODING);
		assertTrue(label + " atteso nessun Content-Encoding ricevuto dal backend; ricevuto: " + receivedCE,
				receivedCE == null || receivedCE.isEmpty());
		assertEquals(label + " dimensione del body in chiaro ricevuto dal backend", String.valueOf(plain.length),
				response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_BODY_BYTES));
		assertEquals(label + " primi byte del body in chiaro ricevuto dal backend", toHex(plain, 8),
				valueOrEmpty(response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_BODY_MAGIC)));
		assertEquals(label + " body in chiaro ricevuto dal backend diverso dall'originale (sha256)",
				ContentEncodingMockServer.sha256Hex(plain), response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_BODY_SHA256));
	}

	/* Nuovo diagnostico (analisi, sez. 13 D5 e sez. 14 S2): accesso al contenuto compresso senza decompressione abilitata.
	 * I frammenti devono restare allineati a govway.msgDiagnostici.properties. */
	public static final String DIAG_CONTENUTO_COMPRESSO_PREFIX = "content is not accessible because it is compressed (Content-Encoding: ";
	public static final String DIAG_ABILITARE_DECOMPRESSIONE = "enable decompression of the ";

	/**
	 * Verifica la presenza del diagnostico che segnala il contenuto compresso non accessibile e suggerisce
	 * di abilitare la decompressione.
	 */
	public static void verifyDiagnosticoContenutoCompresso(String idTransazione, String contentEncoding) throws Exception {
		DBVerifier.existsDiagnostico(idTransazione,
				DIAG_CONTENUTO_COMPRESSO_PREFIX + contentEncoding + ")");
		DBVerifier.existsDiagnostico(idTransazione,
				DIAG_ABILITARE_DECOMPRESSIONE);
	}

	public static String valueOrEmpty(String v) {
		return v != null ? v : "";
	}
}
