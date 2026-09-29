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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
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
 *   <li>Header request {@link #HEADER_REPLY_STATUS}: codice HTTP della response (default 200).</li>
 *   <li>Header request {@link #HEADER_REPLY_BODY_BASE64}: body (Base64) da restituire al posto dell'echo;
 *       viene comunque codificato secondo {@link #HEADER_REPLY_ENCODING}.</li>
 *   <li>Header request {@link #HEADER_REPLY_BODY_GENERATED_SIZE}: body JSON generato dal mock (vedi {@link #generateJsonBody(int)}),
 *       per i contenuti grandi che non possono essere veicolati in un header (limite 'maxHttpHeaderSize' dell'application server);
 *       viene comunque codificato secondo {@link #HEADER_REPLY_ENCODING}.</li>
 *   <li>Header response {@link #HEADER_INVOCATION_ID}: identificativo univoco dell'invocazione.</li>
 *   <li>Header request {@link #HEADER_REPLY_CONTENT_TYPE}: Content-Type della response (default: quello
 *       della request).</li>
 *   <li>Header request {@link #HEADER_REPLY_CONTENT_ENCODING}: se presente sostituisce il valore
 *       dell'header Content-Encoding della response, lasciando invariati i byte prodotti da
 *       {@link #HEADER_REPLY_ENCODING}. Permette di dichiarare encoding non supportati su byte opachi
 *       (es. gzip dichiarato 'br') o un encoding su byte non codificati (es. 'gzip' su body in chiaro).</li>
 *   <li>Header request con prefisso {@link #HEADER_ECHO_PREFIX}: restituiti nella response con
 *       prefisso {@link #HEADER_RECEIVED_PREFIX} (es. per verificare header aggiunti da GovWay).</li>
 *   <li>Header response {@link #HEADER_SENT_BODY_SHA256} / {@link #HEADER_SENT_BODY_BYTES} /
 *       {@link #HEADER_SENT_CONTENT_ENCODING}: descrivono esattamente cio' che il mock ha inviato, per
 *       verificare lato client che il body sia arrivato byte per byte.</li>
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
	/** Doppia codifica reale: deflate (zlib) e poi gzip, dichiarata 'deflate, gzip'. */
	public static final String REPLY_ENCODING_DEFLATE_GZIP = "deflate-gzip";
	/** Stream gzip valido nell'header ma troncato a metà: la decompressione fallisce (EOF). */
	public static final String REPLY_ENCODING_GZIP_TRUNCATED = "gzip-truncated";

	/** Header request: codice HTTP con cui rispondere (default 200). */
	public static final String HEADER_REPLY_STATUS = "govway-testsuite-reply-status";
	/** Header request: Content-Type della response (default: Content-Type della request). */
	public static final String HEADER_REPLY_CONTENT_TYPE = "govway-testsuite-reply-content-type";
	/** Header request: valore da usare per l'header Content-Encoding della response (override). */
	public static final String HEADER_REPLY_CONTENT_ENCODING = "govway-testsuite-reply-content-encoding";
	/** Header request: body (Base64) da restituire al posto dell'echo del body ricevuto. */
	public static final String HEADER_REPLY_BODY_BASE64 = "govway-testsuite-reply-body-base64";
	/** Header request: dimensione minima (byte) del body JSON che il mock genera e restituisce (vedi {@link #generateJsonBody(int)}). */
	public static final String HEADER_REPLY_BODY_GENERATED_SIZE = "govway-testsuite-reply-body-generated-size";

	/* Header response (echo) per asserzione lato test client. Tutti minuscoli e con prefisso 'govway-testsuite-',
	 * in white list anche sulle API SOAP (dove gli header di trasporto non vengono altrimenti inoltrati). */
	public static final String HEADER_RECEIVED_CONTENT_ENCODING = "govway-testsuite-received-content-encoding";
	public static final String HEADER_RECEIVED_CONTENT_LENGTH   = "govway-testsuite-received-content-length";
	public static final String HEADER_RECEIVED_ACCEPT_ENCODING  = "govway-testsuite-received-accept-encoding";
	public static final String HEADER_RECEIVED_BODY_MAGIC       = "govway-testsuite-received-body-magic";
	public static final String HEADER_RECEIVED_BODY_BYTES       = "govway-testsuite-received-body-bytes";
	/** SHA-256 dell'intero body ricevuto (senza decomprimere). */
	public static final String HEADER_RECEIVED_BODY_SHA256      = "govway-testsuite-received-body-sha256";
	public static final String HEADER_SENT_BODY_SHA256          = "govway-testsuite-sent-body-sha256";
	/** Gli header di request con questo prefisso vengono restituiti nella response con prefisso {@link #HEADER_RECEIVED_PREFIX}. */
	public static final String HEADER_ECHO_PREFIX               = "govway-testsuite-echo-";
	public static final String HEADER_RECEIVED_PREFIX           = "govway-testsuite-received-";
	public static final String HEADER_SENT_BODY_BYTES           = "govway-testsuite-sent-body-bytes";
	public static final String HEADER_SENT_CONTENT_ENCODING     = "govway-testsuite-sent-content-encoding";
	/** Identificativo univoco di ogni invocazione del mock (per riconoscere una risposta servita da cache). */
	public static final String HEADER_INVOCATION_ID             = "govway-testsuite-mock-invocation-id";

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

	/**
	 * Body JSON deterministico di almeno {@code minSize} byte, molto comprimibile. Usato dal mock con
	 * {@link #HEADER_REPLY_BODY_GENERATED_SIZE} e dai test per conoscere il contenuto atteso.
	 */
	public static byte[] generateJsonBody(int minSize) {
		StringBuilder sb = new StringBuilder("{\"id\":\"dimensione\",\"riempimento\":\"");
		while (sb.length() < minSize) {
			sb.append("abcdefghijklmnopqrstuvwxyz");
		}
		sb.append("\"}");
		return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
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
				String replyStatus             = firstValue(reqHeaders, HEADER_REPLY_STATUS);
				String replyContentType        = firstValue(reqHeaders, HEADER_REPLY_CONTENT_TYPE);
				String replyContentEncoding    = firstValue(reqHeaders, HEADER_REPLY_CONTENT_ENCODING);
				String replyBodyBase64         = firstValue(reqHeaders, HEADER_REPLY_BODY_BASE64);
				String replyBodyGeneratedSize  = firstValue(reqHeaders, HEADER_REPLY_BODY_GENERATED_SIZE);

				/* Compone i metadati di echo come header response. */
				Headers respHeaders = exchange.getResponseHeaders();
				respHeaders.set(HEADER_RECEIVED_CONTENT_ENCODING, receivedContentEncoding != null ? receivedContentEncoding : "");
				respHeaders.set(HEADER_RECEIVED_CONTENT_LENGTH,   receivedContentLength != null   ? receivedContentLength   : "");
				respHeaders.set(HEADER_RECEIVED_ACCEPT_ENCODING,  receivedAcceptEncoding != null  ? receivedAcceptEncoding  : "");
				respHeaders.set(HEADER_RECEIVED_BODY_MAGIC,       toHex(receivedBody, 8));
				respHeaders.set(HEADER_RECEIVED_BODY_BYTES,       String.valueOf(receivedBody.length));
				respHeaders.set(HEADER_RECEIVED_BODY_SHA256,      sha256Hex(receivedBody));
				for (String name : reqHeaders.keySet()) {
					if (name != null && name.toLowerCase().startsWith(HEADER_ECHO_PREFIX)) {
						respHeaders.set(HEADER_RECEIVED_PREFIX + name.toLowerCase(), firstValue(reqHeaders, name));
					}
				}

				/* Calcola il body di response a partire dal body ricevuto (o da quello indicato) e dall'encoding richiesto. */
				byte[] sourceBody;
				if (replyBodyGeneratedSize != null) {
					sourceBody = generateJsonBody(Integer.parseInt(replyBodyGeneratedSize.trim()));
				}
				else {
					sourceBody = replyBodyBase64 != null ? Base64.getDecoder().decode(replyBodyBase64.trim()) : receivedBody;
				}
				byte[] responseBody = sourceBody;
				String responseContentEncoding = null;
				if (replyEncoding != null) {
					String enc = replyEncoding.trim().toLowerCase();
					switch (enc) {
						case REPLY_ENCODING_GZIP:
							responseBody = encodeGzip(sourceBody);
							responseContentEncoding = REPLY_ENCODING_GZIP;
							break;
						case REPLY_ENCODING_X_GZIP:
							responseBody = encodeGzip(sourceBody);
							responseContentEncoding = REPLY_ENCODING_X_GZIP;
							break;
						case REPLY_ENCODING_DEFLATE_ZLIB:
							responseBody = encodeDeflateZlib(sourceBody);
							responseContentEncoding = "deflate";
							break;
						case REPLY_ENCODING_DEFLATE_RAW:
							responseBody = encodeDeflateRaw(sourceBody);
							responseContentEncoding = "deflate";
							break;
						case REPLY_ENCODING_DEFLATE_GZIP:
							responseBody = encodeGzip(encodeDeflateZlib(sourceBody));
							responseContentEncoding = "deflate, gzip";
							break;
						case REPLY_ENCODING_GZIP_TRUNCATED:
							byte[] gz = encodeGzip(sourceBody);
							responseBody = Arrays.copyOf(gz, gz.length / 2);
							responseContentEncoding = REPLY_ENCODING_GZIP;
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

				if (replyContentEncoding != null) {
					responseContentEncoding = replyContentEncoding;
				}
				if (responseContentEncoding != null) {
					respHeaders.set("Content-Encoding", responseContentEncoding);
				}
				if (replyContentType != null) {
					respHeaders.set("Content-Type", replyContentType);
				}
				else {
					respHeaders.set("Content-Type", firstValueOrDefault(reqHeaders, "Content-Type", "application/octet-stream"));
				}
				respHeaders.set(HEADER_INVOCATION_ID,          UUID.randomUUID().toString());
				respHeaders.set(HEADER_SENT_BODY_SHA256,       sha256Hex(responseBody));
				respHeaders.set(HEADER_SENT_BODY_BYTES,        String.valueOf(responseBody.length));
				respHeaders.set(HEADER_SENT_CONTENT_ENCODING,  responseContentEncoding != null ? responseContentEncoding : "");

				int status = 200;
				if (replyStatus != null) {
					status = Integer.parseInt(replyStatus.trim());
				}
				/* Connessione non riutilizzabile: il server HTTP del JDK chiude le connessioni keep-alive inattive e il pool
				 * dei connettori di GovWay potrebbe riusarne una già chiusa ('The target server failed to respond', 503),
				 * rendendo i test instabili. Con 'Connection: close' ogni invocazione usa una connessione nuova. */
				respHeaders.set("Connection", "close");
				exchange.sendResponseHeaders(status, responseBody.length);
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

	/** SHA-256 esadecimale (minuscolo) di {@code buf}. */
	public static String sha256Hex(byte[] buf) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(buf);
			StringBuilder sb = new StringBuilder(digest.length * 2);
			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xff));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
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
