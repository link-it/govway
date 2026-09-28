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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Backend delle erogazioni RestContentEncodingDigest* (extraTestBundle.zip).
 *
 * Registra in memoria l'ultima richiesta ricevuta (byte così come arrivati, senza decomprimere)
 * e restituisce la risposta impostata dal test tramite {@link #setResponse(int, String, String, byte[])}.
 * I test sono sequenziali: il mock serve una richiesta alla volta.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentEncodingDigestMockServer implements Closeable {

	private final int port;
	private HttpServer server;
	private ExecutorService executor;

	private int responseStatus = 200;
	private String responseContentType = ContentEncodingDigestUtils.CONTENT_TYPE_JSON;
	private String responseContentEncoding = null;
	private byte[] responseBody = new byte[0];

	private ContentEncodingDigestExchange lastRequest;

	public ContentEncodingDigestMockServer(int port) {
		this.port = port;
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
		System.out.println("ContentEncodingDigestMockServer started on http://127.0.0.1:" + this.port);
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
		System.out.println("ContentEncodingDigestMockServer stopped (port " + this.port + ")");
	}

	/**
	 * Imposta la risposta restituita dalle invocazioni successive e azzera l'ultima richiesta registrata.
	 *
	 * @param status codice HTTP
	 * @param contentType Content-Type (null per non inviarlo)
	 * @param contentEncoding Content-Encoding (null per non inviarlo)
	 * @param body byte del body così come devono essere inviati (già compressi se contentEncoding è valorizzato)
	 */
	public synchronized void setResponse(int status, String contentType, String contentEncoding, byte[] body) {
		this.responseStatus = status;
		this.responseContentType = contentType;
		this.responseContentEncoding = contentEncoding;
		this.responseBody = body != null ? body : new byte[0];
		this.lastRequest = null;
	}

	public synchronized ContentEncodingDigestExchange getLastRequest() {
		return this.lastRequest;
	}

	private void handle(HttpExchange exchange) throws IOException {
		try {
			byte[] received = exchange.getRequestBody().readAllBytes();
			int status;
			String contentType;
			String contentEncoding;
			byte[] body;
			synchronized (this) {
				this.lastRequest = new ContentEncodingDigestExchange(exchange.getRequestMethod(), exchange.getRequestURI().toString(),
						exchange.getRequestHeaders(), received);
				status = this.responseStatus;
				contentType = this.responseContentType;
				contentEncoding = this.responseContentEncoding;
				body = this.responseBody;
			}
			if (contentType != null) {
				exchange.getResponseHeaders().set(ContentEncodingDigestUtils.HEADER_CONTENT_TYPE, contentType);
			}
			if (contentEncoding != null) {
				exchange.getResponseHeaders().set(ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING, contentEncoding);
			}
			// connessione non riutilizzata: evita errori sporadici su connessioni del pool chiuse dal mock
			exchange.getResponseHeaders().set("Connection", "close");
			exchange.sendResponseHeaders(status, body.length == 0 ? -1 : body.length);
			if (body.length > 0) {
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(body);
				}
			}
		} finally {
			exchange.close();
		}
	}
}
