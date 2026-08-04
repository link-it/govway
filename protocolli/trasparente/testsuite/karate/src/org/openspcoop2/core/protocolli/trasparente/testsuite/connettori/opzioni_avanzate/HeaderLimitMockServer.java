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
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.impl.bootstrap.HttpAsyncServer;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.http.nio.AsyncRequestConsumer;
import org.apache.hc.core5.http.nio.AsyncServerRequestHandler;
import org.apache.hc.core5.http.nio.entity.BasicAsyncEntityConsumer;
import org.apache.hc.core5.http.nio.entity.StringAsyncEntityProducer;
import org.apache.hc.core5.http.nio.support.BasicRequestConsumer;
import org.apache.hc.core5.http.nio.support.BasicResponseProducer;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.impl.nio.bootstrap.H2ServerBootstrap;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;

/**
 * Mock backend usato da {@link HeaderLimitEngine} per verificare i limiti che il connettore
 * applica durante la lettura degli header HTTP della risposta.
 *
 * <h2>Protocolli</h2>
 * Il mock e' realizzato con la libreria 'httpcore5-h2' in cleartext ed espone <strong>due porte</strong>,
 * una per protocollo:
 * <ul>
 *   <li>{@link #getPort()}: HTTP/1.1;</li>
 *   <li>{@link #getPortHttp2()}: HTTP/2 'prior knowledge', ovvero senza upgrade e senza ALPN.</li>
 * </ul>
 * Due porte distinte sono necessarie perche' su cleartext la libreria non effettua alcun riconoscimento
 * del preface HTTP/2: la version policy e' una caratteristica del server, non della singola connessione,
 * quindi con {@link HttpVersionPolicy#NEGOTIATE} verrebbe sempre servito HTTP/1.1 (la negoziazione
 * automatica presuppone l'ALPN, disponibile solo su TLS) e con {@link HttpVersionPolicy#FORCE_HTTP_2}
 * verrebbe rifiutata qualunque richiesta HTTP/1.1.
 * Il connettore GovWay deve quindi essere configurato con la porta corrispondente al protocollo
 * desiderato e, per l'HTTP/2, con la proprieta' 'connettori.httpVersionPolicy=FORCE_HTTP_2'.
 * Il protocollo effettivamente utilizzato viene riportato al test tramite l'header response
 * {@link #HEADER_RECEIVED_PROTOCOL_VERSION}.
 *
 * <h2>Comportamento</h2>
 * <ul>
 *   <li>Header request {@link #HEADER_REPLY_HEADER_BYTES}: dimensione, in bytes, del valore del
 *       singolo header di risposta {@link #HEADER_BIG}. Con un valore minore o uguale a 0
 *       l'header non viene generato.</li>
 *   <li>Header request {@link #HEADER_REPLY_HEADER_COUNT}: numero di header di risposta
 *       {@link #HEADER_MULTI_PREFIX}N da generare, ognuno di dimensione trascurabile.</li>
 *   <li>Vengono sempre restituiti gli header di riepilogo {@link #HEADER_RECEIVED_HEADER_BYTES},
 *       {@link #HEADER_RECEIVED_HEADER_COUNT} e {@link #HEADER_RECEIVED_PROTOCOL_VERSION}, di
 *       dimensione trascurabile, su cui il test puo' asserire anche quando gli header
 *       sovradimensionati vengono eliminati da GovWay tramite trasformazione.</li>
 * </ul>
 *
 * <p>Tutti gli header HTTP custom (sia request che response) sono in minuscolo, sia per essere
 * robusti ai container che normalizzano gli header sia perche' l'HTTP/2 ammette solamente nomi
 * di header in minuscolo.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HeaderLimitMockServer implements Closeable {

	/** Path dell'endpoint esposto; il gateway accoda il path della risorsa invocata (es. '/echo/http1'). */
	private static final String PATH = "/echo";

	/**
	 * Pattern di registrazione del handler: viene gestita qualunque richiesta, poiche' il gateway
	 * accoda alla location del connettore il path della risorsa REST invocata.
	 */
	private static final String PATH_PATTERN = "*";

	/**
	 * Hostname canonico dichiarato al 'RequestRouter' della libreria.
	 * Deve corrispondere all'authority inviata dal gateway (header 'Host'), altrimenti il router
	 * risponde '421 Misdirected Request' senza invocare il handler: per default, infatti, viene
	 * utilizzato l'hostname canonico della macchina locale. La porta non e' rilevante, poiche' la
	 * libreria utilizza un resolver che la ignora.
	 * I connettori delle configurazioni di test devono quindi essere registrati su questo indirizzo.
	 */
	private static final String CANONICAL_HOST_NAME = "127.0.0.1";

	/** Header request: dimensione in bytes del valore del singolo header di risposta sovradimensionato. */
	public static final String HEADER_REPLY_HEADER_BYTES = "govway-testsuite-reply-header-bytes";
	/** Header request: numero di header di risposta da generare. */
	public static final String HEADER_REPLY_HEADER_COUNT = "govway-testsuite-reply-header-count";

	/** Header response: header di dimensione pilotata da {@link #HEADER_REPLY_HEADER_BYTES}. */
	public static final String HEADER_BIG = "govway-testsuite-big-header";
	/** Header response: prefisso degli header generati in numero pilotato da {@link #HEADER_REPLY_HEADER_COUNT}. */
	public static final String HEADER_MULTI_PREFIX = "govway-testsuite-multi-header-";

	/* Header response di riepilogo, di dimensione trascurabile, per asserzione lato test client. */
	public static final String HEADER_RECEIVED_PROTOCOL_VERSION = "received-protocol-version";
	public static final String HEADER_RECEIVED_HEADER_BYTES = "received-reply-header-bytes";
	public static final String HEADER_RECEIVED_HEADER_COUNT = "received-reply-header-count";

	/** Body restituito dal mock. */
	public static final String BODY = "{\"headerLimit\":\"echo\"}";

	private final int port;
	private final int portHttp2;
	private HttpAsyncServer server;
	private HttpAsyncServer serverHttp2;

	public HeaderLimitMockServer(int port, int portHttp2) {
		this.port = port;
		this.portHttp2 = portHttp2;
	}

	/** Porta che serve le richieste HTTP/1.1. */
	public int getPort() {
		return this.port;
	}

	/** Porta che serve le richieste HTTP/2 'prior knowledge'. */
	public int getPortHttp2() {
		return this.portHttp2;
	}

	public synchronized void start() throws IOException {
		if (this.server != null) {
			return;
		}
		EchoHandler handler = new EchoHandler();
		this.server = buildServer(handler, HttpVersionPolicy.FORCE_HTTP_1);
		this.serverHttp2 = buildServer(handler, HttpVersionPolicy.FORCE_HTTP_2);
		listen(this.server, this.port);
		listen(this.serverHttp2, this.portHttp2);
		System.out.println("HeaderLimitMockServer started on http://" + CANONICAL_HOST_NAME + ":" + this.port + PATH
				+ " (HTTP/1.1) e http://" + CANONICAL_HOST_NAME + ":" + this.portHttp2 + PATH + " (HTTP/2)");
	}

	private static HttpAsyncServer buildServer(EchoHandler handler, HttpVersionPolicy versionPolicy) {
		return H2ServerBootstrap.bootstrap()
				.setVersionPolicy(versionPolicy)
				.setCanonicalHostName(CANONICAL_HOST_NAME)
				.setIOReactorConfig(IOReactorConfig.custom()
						.setSoTimeout(Timeout.ofSeconds(30))
						/* SO_REUSEADDR, che nella libreria e' disattivato per default: senza di esso
						 * il bind fallisce con 'Address already in use' quando sulla porta risultano
						 * connessioni in TIME_WAIT, situazione normale se il mock viene riavviato
						 * subito dopo l'esecuzione di una precedente classe di test. */
						.setSoReuseAddress(true)
						.build())
				/* Il mock non deve applicare alcun limite in lettura sulle richieste ricevute:
				 * i limiti sotto test sono quelli applicati da GovWay in lettura della risposta. */
				.setHttp1Config(Http1Config.custom()
						.setMaxLineLength(0)
						.setMaxHeaderCount(0)
						.build())
				.setH2Config(H2Config.custom()
						.setMaxHeaderListSize(16777215)
						.build())
				.register(PATH_PATTERN, handler)
				.create();
	}

	private static void listen(HttpAsyncServer server, int port) throws IOException {
		server.start();
		try {
			server.listen(new InetSocketAddress(CANONICAL_HOST_NAME, port), URIScheme.HTTP).get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException(e.getMessage(), e);
		} catch (ExecutionException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public synchronized void close() {
		stop();
	}

	public synchronized void stop() {
		if (this.server != null) {
			this.server.close(CloseMode.IMMEDIATE);
			this.server = null;
		}
		if (this.serverHttp2 != null) {
			this.serverHttp2.close(CloseMode.IMMEDIATE);
			this.serverHttp2 = null;
		}
	}

	private static class EchoHandler implements AsyncServerRequestHandler<Message<HttpRequest, byte[]>> {

		@Override
		public AsyncRequestConsumer<Message<HttpRequest, byte[]>> prepare(HttpRequest request, EntityDetails entityDetails, HttpContext context) {
			return new BasicRequestConsumer<>(entityDetails != null ? new BasicAsyncEntityConsumer() : null);
		}

		@Override
		public void handle(Message<HttpRequest, byte[]> message, ResponseTrigger responseTrigger, HttpContext context) throws HttpException, IOException {

			HttpRequest request = message.getHead();

			int replyHeaderBytes = intHeader(request, HEADER_REPLY_HEADER_BYTES);
			int replyHeaderCount = intHeader(request, HEADER_REPLY_HEADER_COUNT);

			HttpResponse response = new BasicHttpResponse(HttpStatus.SC_OK);

			response.addHeader(HEADER_RECEIVED_PROTOCOL_VERSION, protocolVersion(request, context));
			response.addHeader(HEADER_RECEIVED_HEADER_BYTES, String.valueOf(replyHeaderBytes));
			response.addHeader(HEADER_RECEIVED_HEADER_COUNT, String.valueOf(replyHeaderCount));

			if (replyHeaderBytes > 0) {
				response.addHeader(HEADER_BIG, buildHeaderValue(replyHeaderBytes));
			}
			for (int i = 0; i < replyHeaderCount; i++) {
				response.addHeader(HEADER_MULTI_PREFIX + i, "v" + i);
			}

			responseTrigger.submitResponse(
					new BasicResponseProducer(response, new StringAsyncEntityProducer(BODY, ContentType.APPLICATION_JSON)),
					context);
		}

		private static String protocolVersion(HttpRequest request, HttpContext context) {
			ProtocolVersion version = request.getVersion();
			if (version == null) {
				version = HttpCoreContext.castOrCreate(context).getProtocolVersion();
			}
			return version != null ? version.toString() : "unknown";
		}

		private static int intHeader(HttpRequest request, String headerName) throws HttpException {
			Header header = request.getFirstHeader(headerName);
			if (header == null || header.getValue() == null || header.getValue().trim().isEmpty()) {
				return 0;
			}
			try {
				return Integer.parseInt(header.getValue().trim());
			} catch (NumberFormatException e) {
				throw new HttpException("Header '" + headerName + "' non numerico: " + header.getValue(), e);
			}
		}

		/**
		 * Costruisce un valore di header lungo esattamente 'bytes' caratteri US-ASCII,
		 * utilizzando soli caratteri ammessi in un field value HTTP.
		 */
		private static String buildHeaderValue(int bytes) {
			StringBuilder sb = new StringBuilder(bytes);
			while (sb.length() < bytes) {
				sb.append('A');
			}
			return sb.toString();
		}
	}
}
