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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

/**
 * Mock server HTTPS che simula il comportamento di https://sse.dev/test:
 * GET /test risponde con Content-Type 'text/event-stream' ed emette un evento
 * 'data: {...}' ogni circa 2 secondi finche' il client resta connesso.
 *
 * Utilizzato in ambiente Jenkins, dove il dominio sse.dev non e' raggiungibile.
 * L'endpoint del connettore di GovWay deve iniziare con 'https://', quindi il
 * mock e' esposto in HTTPS riusando lo stesso keystore 'erogatore.jks' impiegato
 * dagli altri test (cert self-signed: il truststore lato connettore GovWay deve
 * accettarlo, oppure essere configurato in modalita' trust-all).
 *
 * @author Poli Andrea (apoli@link.it)
 *
 */
public class SSEDevMockServer {

	private static final String PATH = "/test";
	private static final long EVENT_INTERVAL_MS = 2_000L;
	private static final long MAX_CONNECTION_DURATION_MS = 60_000L;

	private final int port;
	private final String keystorePath;
	private final String keystorePassword;
	private HttpsServer server;
	private ExecutorService executor;

	public SSEDevMockServer(int port, String keystorePath, String keystorePassword) {
		this.port = port;
		this.keystorePath = keystorePath;
		this.keystorePassword = keystorePassword;
	}

	public int getPort() {
		return this.port;
	}

	public synchronized void start() throws IOException, GeneralSecurityException {
		if (this.server != null) {
			return;
		}
		SSLContext sslContext = buildSslContext();
		this.server = HttpsServer.create(new InetSocketAddress("127.0.0.1", this.port), 0);
		this.server.setHttpsConfigurator(new HttpsConfigurator(sslContext));
		this.executor = Executors.newCachedThreadPool();
		this.server.setExecutor(this.executor);
		this.server.createContext(PATH, new SSEHandler());
		this.server.start();
		System.out.println("SSEDevMockServer started on https://127.0.0.1:" + this.port + PATH);
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
		System.out.println("SSEDevMockServer stopped (port " + this.port + ")");
	}

	private SSLContext buildSslContext() throws IOException, GeneralSecurityException {
		KeyStore ks = KeyStore.getInstance("JKS");
		try (FileInputStream fis = new FileInputStream(this.keystorePath)) {
			ks.load(fis, this.keystorePassword.toCharArray());
		}
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, this.keystorePassword.toCharArray());
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kmf.getKeyManagers(), null, null);
		return sslContext;
	}

	private static class SSEHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			try {
				if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
					exchange.sendResponseHeaders(405, -1);
					return;
				}
				Headers h = exchange.getResponseHeaders();
				h.set("Content-Type", "text/event-stream;charset=UTF-8");
				h.set("Cache-Control", "no-cache");
				h.set("Connection", "keep-alive");
				/* 0 = chunked, response aperta finche' il client resta connesso */
				exchange.sendResponseHeaders(200, 0);

				try (OutputStream os = exchange.getResponseBody()) {
					int counter = 0;
					long start = System.currentTimeMillis();
					while (!Thread.currentThread().isInterrupted()) {
						counter++;
						String payload = "{\"testing\":true,\"sse\":\"dev\",\"now\":"
								+ System.currentTimeMillis() + ",\"counter\":" + counter + "}";
						os.write(("data: " + payload + "\n\n").getBytes(StandardCharsets.UTF_8));
						os.flush();
						Thread.sleep(EVENT_INTERVAL_MS);
						if (System.currentTimeMillis() - start > MAX_CONNECTION_DURATION_MS) {
							break;
						}
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (IOException e) {
					/* client disconnesso: comportamento atteso */
				}
			} finally {
				exchange.close();
			}
		}
	}
}
