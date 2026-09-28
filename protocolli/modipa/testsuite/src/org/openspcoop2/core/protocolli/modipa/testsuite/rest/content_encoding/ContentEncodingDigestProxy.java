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

package org.openspcoop2.core.protocolli.modipa.testsuite.rest.content_encoding;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Proxy tra le fruizioni e le erogazioni RestContentEncodingDigest* (extraTestBundle.zip).
 *
 * Il connettore di ogni fruizione punta a 'http://localhost:&lt;porta&gt;/&lt;NomeAPI&gt;': il proxy ricava l'API dal primo
 * segmento del path e inoltra all'erogazione corrispondente, trasferendo i byte del body così come ricevuti.
 * Registra in memoria la richiesta e la risposta transitate e, se richiesto dal test, modifica il campo MTIME
 * dello stream gzip della richiesta o della risposta (vedi {@link ContentEncodingDigestUtils#alterGzipMtime(byte[])}).
 * I test sono sequenziali: il proxy serve una richiesta alla volta.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentEncodingDigestProxy implements Closeable {

	/** Header non inoltrati: gestiti dal client HTTP o hop-by-hop. */
	private static final Set<String> HEADER_NON_INOLTRATI = new HashSet<>(Arrays.asList(
			"host", "content-length", "connection", "keep-alive", "transfer-encoding", "expect", "upgrade", "te", "trailer",
			"proxy-connection", "proxy-authenticate", "proxy-authorization"));

	private final int port;
	private final String urlErogazioni;
	private final Duration readTimeout;
	private HttpServer server;
	private ExecutorService executor;
	private HttpClient client;

	private boolean alterRequestGzip = false;
	private boolean alterResponseGzip = false;

	private ContentEncodingDigestExchange requestIn;
	private ContentEncodingDigestExchange requestOut;
	private ContentEncodingDigestExchange responseIn;
	private ContentEncodingDigestExchange responseOut;

	/**
	 * @param port porta di ascolto
	 * @param govwayBasePath base path di GovWay (es. http://localhost:8080/govway)
	 * @param soggettoErogatore soggetto erogatore delle API (es. DemoSoggettoErogatore)
	 * @param connectTimeoutMs connect timeout verso l'erogazione
	 * @param readTimeoutMs read timeout verso l'erogazione
	 */
	public ContentEncodingDigestProxy(int port, String govwayBasePath, String soggettoErogatore, int connectTimeoutMs, int readTimeoutMs) {
		this.port = port;
		this.urlErogazioni = govwayBasePath + "/rest/in/" + soggettoErogatore + "/";
		this.readTimeout = Duration.ofMillis(readTimeoutMs);
		this.client = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_1_1)
				.connectTimeout(Duration.ofMillis(connectTimeoutMs))
				.followRedirects(HttpClient.Redirect.NEVER)
				.build();
	}

	public synchronized void start() throws IOException {
		if (this.server != null) {
			return;
		}
		this.server = HttpServer.create(new InetSocketAddress("127.0.0.1", this.port), 0);
		this.executor = Executors.newCachedThreadPool();
		this.server.setExecutor(this.executor);
		this.server.createContext("/", this::handle);
		this.server.start();
		System.out.println("ContentEncodingDigestProxy started on http://127.0.0.1:" + this.port + " -> " + this.urlErogazioni);
	}

	@Override
	public synchronized void close() {
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
		this.client = null;
		System.out.println("ContentEncodingDigestProxy stopped (port " + this.port + ")");
	}

	/**
	 * Azzera quanto registrato e imposta le manomissioni per le invocazioni successive.
	 *
	 * @param alterRequestGzip modifica il campo MTIME del body gzip della richiesta inoltrata all'erogazione
	 * @param alterResponseGzip modifica il campo MTIME del body gzip della risposta restituita alla fruizione
	 */
	public synchronized void reset(boolean alterRequestGzip, boolean alterResponseGzip) {
		this.alterRequestGzip = alterRequestGzip;
		this.alterResponseGzip = alterResponseGzip;
		this.requestIn = null;
		this.requestOut = null;
		this.responseIn = null;
		this.responseOut = null;
	}

	/** Richiesta ricevuta dalla fruizione. */
	public synchronized ContentEncodingDigestExchange getRequestIn() {
		return this.requestIn;
	}
	/** Richiesta inoltrata all'erogazione (differisce da {@link #getRequestIn()} solo se manomessa). */
	public synchronized ContentEncodingDigestExchange getRequestOut() {
		return this.requestOut;
	}
	/** Risposta ricevuta dall'erogazione. */
	public synchronized ContentEncodingDigestExchange getResponseIn() {
		return this.responseIn;
	}
	/** Risposta restituita alla fruizione (differisce da {@link #getResponseIn()} solo se manomessa). */
	public synchronized ContentEncodingDigestExchange getResponseOut() {
		return this.responseOut;
	}

	private void handle(HttpExchange exchange) throws IOException {
		boolean headersSent = false;
		try {
			boolean alterReq;
			boolean alterRes;
			synchronized (this) {
				alterReq = this.alterRequestGzip;
				alterRes = this.alterResponseGzip;
			}

			// '/<NomeAPI>/resto/del/path' -> '<govway>/rest/in/<soggetto>/<NomeAPI>/v1/resto/del/path'
			String uri = exchange.getRequestURI().toString();
			String withoutSlash = uri.startsWith("/") ? uri.substring(1) : uri;
			int sep = withoutSlash.indexOf('/');
			String api = sep > 0 ? withoutSlash.substring(0, sep) : withoutSlash;
			String rest = sep > 0 ? withoutSlash.substring(sep) : "";
			String target = this.urlErogazioni + api + "/v1" + rest;

			byte[] reqBody = exchange.getRequestBody().readAllBytes();
			ContentEncodingDigestExchange reqIn = new ContentEncodingDigestExchange(exchange.getRequestMethod(), uri, exchange.getRequestHeaders(), reqBody);
			byte[] reqBodyOut = alterReq ? ContentEncodingDigestUtils.alterGzipMtime(reqBody) : reqBody;

			HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(target)).timeout(this.readTimeout);
			for (Map.Entry<String, List<String>> h : exchange.getRequestHeaders().entrySet()) {
				if (h.getKey() != null && !HEADER_NON_INOLTRATI.contains(h.getKey().toLowerCase())) {
					for (String v : h.getValue()) {
						builder.header(h.getKey(), v);
					}
				}
			}
			builder.method(exchange.getRequestMethod(),
					reqBodyOut.length > 0 ? HttpRequest.BodyPublishers.ofByteArray(reqBodyOut) : HttpRequest.BodyPublishers.noBody());
			HttpRequest request = builder.build();
			ContentEncodingDigestExchange reqOut = new ContentEncodingDigestExchange(request.method(), target, request.headers().map(), reqBodyOut);

			HttpResponse<byte[]> response;
			try {
				response = this.client.send(request, HttpResponse.BodyHandlers.ofByteArray());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IOException("Proxy interrupted: " + e.getMessage(), e);
			}
			byte[] resBody = response.body() != null ? response.body() : new byte[0];
			ContentEncodingDigestExchange resIn = new ContentEncodingDigestExchange(response.statusCode(), response.headers().map(), resBody);
			byte[] resBodyOut = alterRes ? ContentEncodingDigestUtils.alterGzipMtime(resBody) : resBody;

			for (Map.Entry<String, List<String>> h : response.headers().map().entrySet()) {
				if (h.getKey() != null && !h.getKey().startsWith(":") && !HEADER_NON_INOLTRATI.contains(h.getKey().toLowerCase())) {
					for (String v : h.getValue()) {
						exchange.getResponseHeaders().add(h.getKey(), v);
					}
				}
			}
			exchange.getResponseHeaders().set("Connection", "close");
			ContentEncodingDigestExchange resOut = new ContentEncodingDigestExchange(response.statusCode(), exchange.getResponseHeaders(), resBodyOut);

			synchronized (this) {
				this.requestIn = reqIn;
				this.requestOut = reqOut;
				this.responseIn = resIn;
				this.responseOut = resOut;
			}

			headersSent = true;
			exchange.sendResponseHeaders(response.statusCode(), resBodyOut.length == 0 ? -1 : resBodyOut.length);
			if (resBodyOut.length > 0) {
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(resBodyOut);
				}
			}
		} catch (RuntimeException | IOException e) {
			System.err.println("ContentEncodingDigestProxy error: " + e.getMessage());
			e.printStackTrace(System.err);
			if (!headersSent) {
				// errore del proxy, distinguibile dalle risposte di GovWay
				exchange.sendResponseHeaders(502, -1);
			}
		} finally {
			exchange.close();
		}
	}
}
