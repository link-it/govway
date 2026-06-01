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

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Mock backend usato da {@link ContentEncodingEngine}. Espone l'endpoint {@code /echo} in HTTP
 * plain (la sicurezza del trasporto non e' rilevante per la verifica del Content-Encoding e
 * semplifica il setup).
 *
 * <h2>Comportamento</h2>
 * <ul>
 *   <li>Riceve POST con body raw qualunque (gzip, deflate, x-gzip, br, plain): lo legge senza
 *       decomprimere e calcola alcuni metadati esposti come header response (vedi
 *       {@code received-*}).</li>
 *   <li>Echo del body letto: il body della response e' la stessa sequenza di byte ricevuta,
 *       eventualmente ri-compressa secondo l'header {@link #HEADER_REPLY_ENCODING}.</li>
 *   <li>Header request {@link #HEADER_REPLY_ENCODING}: se valorizzato a uno dei valori gestiti
 *       ({@code gzip|x-gzip|deflate-zlib|deflate-raw|br|identity|none}), pilota l'encoding della
 *       response. Per gli encoding non standard (es. {@code br}) il mock applica solo l'header
 *       senza comprimere realmente, cosi' il test puo' verificare il rifiuto lato GovWay.</li>
 * </ul>
 *
 * <p>Tutti gli header HTTP custom (sia request che response) sono in minuscolo, in modo da
 * essere robusti ai container che normalizzano gli header (es. Tomcat).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentEncodingMockServer implements Closeable {

	private static final String PATH = "/echo";

	/** Header request: dice al mock con quale encoding rispondere. */
	public static final String HEADER_REPLY_ENCODING = "govway-testsuite-reply-encoding";

	/** Valori ammessi per {@link #HEADER_REPLY_ENCODING}. */
	public static final String REPLY_ENCODING_NONE         = "none";
	public static final String REPLY_ENCODING_IDENTITY     = "identity";
	public static final String REPLY_ENCODING_GZIP         = "gzip";
	public static final String REPLY_ENCODING_X_GZIP       = "x-gzip";
	public static final String REPLY_ENCODING_DEFLATE_ZLIB = "deflate-zlib";
	public static final String REPLY_ENCODING_DEFLATE_RAW  = "deflate-raw";
	public static final String REPLY_ENCODING_BROTLI       = "br";

	/* Header response (echo) per asserzione lato test client. Tutti minuscoli. */
	public static final String HEADER_RECEIVED_CONTENT_ENCODING = "received-content-encoding";
	public static final String HEADER_RECEIVED_CONTENT_LENGTH   = "received-content-length";
	public static final String HEADER_RECEIVED_ACCEPT_ENCODING  = "received-accept-encoding";
	public static final String HEADER_RECEIVED_BODY_MAGIC       = "received-body-magic";
	public static final String HEADER_RECEIVED_BODY_BYTES       = "received-body-bytes";

	private final int port;
	private HttpServer server;
	private ExecutorService executor;

	public ContentEncodingMockServer(int port) {
		this.port = port;
	}

	public int getPort() {
		return this.port;
	}

	public synchronized void start() throws IOException {
		if (this.server != null) {
			return;
		}
		this.server = HttpServer.create(new InetSocketAddress("127.0.0.1", this.port), 0);
		this.executor = Executors.newCachedThreadPool();
		this.server.setExecutor(this.executor);
		this.server.createContext(PATH, new EchoHandler());
		this.server.start();
		System.out.println("ContentEncodingMockServer started on http://127.0.0.1:" + this.port + PATH);
	}

	@Override
	public synchronized void close() {
		stop();
	}

	public synchronized void stop() {
		if (this.server != null) {
			this.server.stop(0);
			this.server = null;
		}
		if (this.executor != null) {
			this.executor.shutdownNow();
			try {
				this.executor.awaitTermination(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			this.executor = null;
		}
		System.out.println("ContentEncodingMockServer stopped (port " + this.port + ")");
	}

	private static class EchoHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			try {
				/* Leggi body raw (senza decomprimere). */
				byte[] receivedBody;
				try {
					receivedBody = exchange.getRequestBody().readAllBytes();
				} catch (IOException e) {
					receivedBody = new byte[0];
				}

				/* Estrai header per i metadati di echo. */
				Headers reqHeaders = exchange.getRequestHeaders();
				String receivedContentEncoding = firstValue(reqHeaders, "Content-Encoding");
				String receivedContentLength   = firstValue(reqHeaders, "Content-Length");
				String receivedAcceptEncoding  = firstValue(reqHeaders, "Accept-Encoding");
				String replyEncoding           = firstValue(reqHeaders, HEADER_REPLY_ENCODING);

				/* Compone i metadati di echo come header response. */
				Headers respHeaders = exchange.getResponseHeaders();
				respHeaders.set(HEADER_RECEIVED_CONTENT_ENCODING, receivedContentEncoding != null ? receivedContentEncoding : "");
				respHeaders.set(HEADER_RECEIVED_CONTENT_LENGTH,   receivedContentLength != null   ? receivedContentLength   : "");
				respHeaders.set(HEADER_RECEIVED_ACCEPT_ENCODING,  receivedAcceptEncoding != null  ? receivedAcceptEncoding  : "");
				respHeaders.set(HEADER_RECEIVED_BODY_MAGIC,       toHex(receivedBody, 8));
				respHeaders.set(HEADER_RECEIVED_BODY_BYTES,       String.valueOf(receivedBody.length));

				/* Calcola il body di response a partire dal body ricevuto e dall'encoding richiesto. */
				byte[] responseBody = receivedBody;
				String responseContentEncoding = null;
				if (replyEncoding != null) {
					String enc = replyEncoding.trim().toLowerCase();
					switch (enc) {
						case REPLY_ENCODING_GZIP:
							responseBody = encodeGzip(receivedBody);
							responseContentEncoding = REPLY_ENCODING_GZIP;
							break;
						case REPLY_ENCODING_X_GZIP:
							responseBody = encodeGzip(receivedBody);
							responseContentEncoding = REPLY_ENCODING_X_GZIP;
							break;
						case REPLY_ENCODING_DEFLATE_ZLIB:
							responseBody = encodeDeflateZlib(receivedBody);
							responseContentEncoding = "deflate";
							break;
						case REPLY_ENCODING_DEFLATE_RAW:
							responseBody = encodeDeflateRaw(receivedBody);
							responseContentEncoding = "deflate";
							break;
						case REPLY_ENCODING_BROTLI:
							/* Non comprimiamo davvero in brotli: serve solo dichiarare l'encoding nell'header
							 * per far rifiutare la chiamata dal lato GovWay quando decompress e' attivo. */
							responseContentEncoding = REPLY_ENCODING_BROTLI;
							break;
						case REPLY_ENCODING_IDENTITY:
							responseContentEncoding = REPLY_ENCODING_IDENTITY;
							break;
						case REPLY_ENCODING_NONE:
						default:
							/* nessun Content-Encoding nella response */
							break;
					}
				}

				if (responseContentEncoding != null) {
					respHeaders.set("Content-Encoding", responseContentEncoding);
				}
				respHeaders.set("Content-Type", firstValueOrDefault(reqHeaders, "Content-Type", "application/octet-stream"));

				exchange.sendResponseHeaders(200, responseBody.length);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(responseBody);
				}
			} finally {
				exchange.close();
			}
		}
	}

	private static String firstValue(Headers headers, String name) {
		List<String> values = headers.get(name);
		return (values != null && !values.isEmpty()) ? values.get(0) : null;
	}

	private static String firstValueOrDefault(Headers headers, String name, String def) {
		String v = firstValue(headers, name);
		return (v != null) ? v : def;
	}

	/** Hex dei primi {@code maxBytes} byte di {@code buf}, separati da spazio (es. "1f 8b 08 00"). */
	private static String toHex(byte[] buf, int maxBytes) {
		if (buf == null || buf.length == 0) {
			return "";
		}
		int n = Math.min(maxBytes, buf.length);
		StringBuilder sb = new StringBuilder(n * 3);
		for (int i = 0; i < n; i++) {
			if (i > 0) sb.append(' ');
			sb.append(String.format("%02x", buf[i] & 0xff));
		}
		return sb.toString();
	}

	private static byte[] encodeGzip(byte[] input) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (GZIPOutputStream gz = new GZIPOutputStream(baos)) {
			gz.write(input);
		}
		return baos.toByteArray();
	}

	private static byte[] encodeDeflateZlib(byte[] input) throws IOException {
		/* Default di DeflaterOutputStream => zlib header (RFC 1950). */
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (DeflaterOutputStream d = new DeflaterOutputStream(baos)) {
			d.write(input);
		}
		return baos.toByteArray();
	}

	private static byte[] encodeDeflateRaw(byte[] input) throws IOException {
		/* nowrap=true => raw deflate RFC 1951. */
		Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (DeflaterOutputStream d = new DeflaterOutputStream(baos, deflater)) {
			d.write(input);
		} finally {
			deflater.end();
		}
		return baos.toByteArray();
	}
}
