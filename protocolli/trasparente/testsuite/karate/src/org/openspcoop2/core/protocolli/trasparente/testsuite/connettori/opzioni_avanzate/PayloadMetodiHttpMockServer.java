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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * PayloadMetodiHttpMockServer: backend che descrive, tramite header della risposta, la richiesta
 * ricevuta dal gateway (metodo, contenuto e Content-Type), consentendo ai test di verificare
 * cosa il connettore ha effettivamente inoltrato per ciascun metodo HTTP.
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PayloadMetodiHttpMockServer implements Closeable {

	/** Path dell'endpoint esposto; il gateway accoda il path della risorsa invocata (es. '/echo/standard'). */
	private static final String PATH = "/echo";

	/** Header response: metodo HTTP della richiesta ricevuta. */
	public static final String HEADER_RECEIVED_METHOD = "received-method";
	/** Header response: numero di bytes del contenuto ricevuto. */
	public static final String HEADER_RECEIVED_BODY_LENGTH = "received-body-length";
	/** Header response: contenuto ricevuto codificato in base64 (presente solo se il contenuto non e' vuoto). */
	public static final String HEADER_RECEIVED_BODY = "received-body";
	/** Header response: Content-Type della richiesta ricevuta, o {@link #VALUE_ASSENTE} se non presente. */
	public static final String HEADER_RECEIVED_CONTENT_TYPE = "received-content-type";

	public static final String VALUE_ASSENTE = "assente";

	private final int port;
	private HttpServer server;
	private ExecutorService executor;

	public PayloadMetodiHttpMockServer(int port) {
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
		System.out.println("PayloadMetodiHttpMockServer started on http://127.0.0.1:" + this.port + PATH);
	}

	public synchronized void stop() {
		if (this.server != null) {
			this.server.stop(0);
			this.server = null;
		}
		if (this.executor != null) {
			this.executor.shutdownNow();
			try {
				this.executor.awaitTermination(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			this.executor = null;
		}
	}

	@Override
	public void close() {
		stop();
	}

	private static class EchoHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			try {
				String method = exchange.getRequestMethod();

				byte[] body;
				try (InputStream is = exchange.getRequestBody()) {
					body = is.readAllBytes();
				}
				String contentType = exchange.getRequestHeaders().getFirst("Content-Type");

				Headers responseHeaders = exchange.getResponseHeaders();
				responseHeaders.add(HEADER_RECEIVED_METHOD, method);
				responseHeaders.add(HEADER_RECEIVED_BODY_LENGTH, String.valueOf(body.length));
				if (body.length > 0) {
					responseHeaders.add(HEADER_RECEIVED_BODY, Base64.getEncoder().encodeToString(body));
				}
				responseHeaders.add(HEADER_RECEIVED_CONTENT_TYPE, contentType != null ? contentType : VALUE_ASSENTE);

				if ("HEAD".equalsIgnoreCase(method)) {
					// La risposta ad una HEAD non possiede contenuto
					exchange.sendResponseHeaders(200, -1);
					return;
				}

				byte[] response = ("{\"method\":\"" + method + "\",\"bodyLength\":" + body.length + "}").getBytes(StandardCharsets.UTF_8);
				responseHeaders.add("Content-Type", "application/json");
				exchange.sendResponseHeaders(200, response.length);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(response);
				}
			} finally {
				exchange.close();
			}
		}
	}
}
