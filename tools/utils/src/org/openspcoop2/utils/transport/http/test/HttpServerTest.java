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

package org.openspcoop2.utils.transport.http.test;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.impl.bootstrap.HttpServer;
import org.apache.hc.core5.http.impl.bootstrap.ServerBootstrap;
import org.apache.hc.core5.http.io.HttpFilterChain;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.HttpFilterChain.ResponseTrigger;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.bouncycastle.util.encoders.Base64;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

import jakarta.servlet.http.HttpServletResponse;

/**
 * HttpServerTest
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpServerTest implements Closeable {

	private static final String LOCALHOST = "localhost";

	/**
	 * Plaintext che gli endpoint '/contentEncoding/*' restituiscono dopo aver applicato
	 * l'encoding richiesto. I test asseriscono su questa stringa una volta decompressa.
	 */
	public static final String PLAIN_BODY_CONTENT_ENCODING_TEST = "GovWay content-encoding round-trip OK";

	/**
	 * Nome dell'header di response che echeggia l'Accept-Encoding ricevuto in richiesta,
	 * usato dai test per verificare l'iniezione lato client.
	 */
	public static final String ECHO_ACCEPT_ENCODING_HEADER = "x-received-accept-encoding";

	/**
	 * Test-only mode: il path '/contentEncoding/deflate-zlib' codifica il body in deflate
	 * con header zlib RFC 1950 (default di java.util.zip.DeflaterOutputStream). Il
	 * Content-Encoding sul wire e' comunque 'deflate' (RFC standard); il suffisso '-zlib'
	 * serve solo a selezionare la modalita' di encoding lato server in fase di test.
	 */
	public static final String CONTENT_ENCODING_TEST_MODE_DEFLATE_ZLIB =
			HttpConstants.CONTENT_ENCODING_VALUE_DEFLATE + "-zlib";

	/**
	 * Test-only mode: il path '/contentEncoding/deflate-raw' codifica il body in deflate
	 * raw RFC 1951 (Deflater con nowrap=true). Sul wire arriva sempre come
	 * 'Content-Encoding: deflate'; il suffisso '-raw' seleziona la variante lato server.
	 */
	public static final String CONTENT_ENCODING_TEST_MODE_DEFLATE_RAW =
			HttpConstants.CONTENT_ENCODING_VALUE_DEFLATE + "-raw";

	private final HttpServer server;
	private final String username;
	private final String password;
	private final boolean httpsEnabled;
	
	public HttpServerTest() throws IOException {
		this(null);
	}
	
	public HttpServerTest(SSLContext ctx) throws IOException {
		

		SocketConfig socketConfig = SocketConfig.custom()
				.setSoTimeout(Timeout.ofSeconds(15))
				.setTcpNoDelay(true)
				.setSoReuseAddress(true)
				.build();
		
		this.username = new Random().ints(10, 'a', 'z')
				.mapToObj(v -> String.valueOf((char)v))
				.collect(Collectors.joining());
		
		this.password = new Random().ints(10, 'a', 'z')
				.mapToObj(v -> String.valueOf((char)v))
				.collect(Collectors.joining());
				
		ServerBootstrap serverBuilder = ServerBootstrap.bootstrap()
				.setListenerPort(-1)
				.setSocketConfig(socketConfig)
				.addFilterFirst("auth", this::handleAuth)
				.register(LOCALHOST, "/headers", this::handleTestHeaders)
				.register(LOCALHOST, "/redirect", this::handleTestRedirect)
				.register(LOCALHOST, "/readTimeout", this::handleTestReadTimeout)
				.register(LOCALHOST, "/connectTimeout", this::handleTestHeaders)
				.register(LOCALHOST, "/params", this::handleTestParams)
				.register(LOCALHOST, "/methods/*", this::handleTestMethods)
				.register(LOCALHOST, "/proxy", this::handleTestProxy)
				.register(LOCALHOST, "/throttling", this::handleTestThrottling)
				.register(LOCALHOST, "/contentEncoding/*", this::handleTestContentEncoding)
				.register(LOCALHOST, "/print", this::printRequest);
		
		if (ctx != null) {
			serverBuilder.setSslContext(ctx)
				.setSslSetupHandler(params -> params.setWantClientAuth(true));
		}
		this.httpsEnabled = (ctx != null);
		
		this.server = serverBuilder.create();
		this.server.start();
	}
	
	@Override
	public void close() {
		this.server.stop();
		this.server.close(CloseMode.GRACEFUL);
		this.server.initiateShutdown();
		
		try {
			this.server.awaitTermination(TimeValue.MAX_VALUE);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	public int getPort() {
		return this.server.getLocalPort();
	}
	
	public String getUrl() {
		return "http://127.0.0.1:" + this.getPort();
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}

	public void handleTestMethods(ClassicHttpRequest req, ClassicHttpResponse res, HttpContext ctx) throws IOException {		
		String[] segments = req.getRequestUri().split("/");
		String responseContent = segments[segments.length - 1].toLowerCase();
		
		if (req.getMethod().toLowerCase().equals(responseContent)) {
			StringEntity out = new StringEntity(responseContent);
			res.setEntity(out);
			res.setCode(HttpServletResponse.SC_OK);
		} else {
			res.setCode(HttpServletResponse.SC_BAD_REQUEST);
		}
		res.close();
	}
	
	
	public void handleAuth(ClassicHttpRequest req, ResponseTrigger trigger, HttpContext ctx, HttpFilterChain chain) throws HttpException, IOException {
		Header auth = req.getHeader(HttpConstants.AUTHORIZATION);
		String rawCredential = auth.getValue().replace(HttpConstants.AUTHORIZATION_PREFIX_BASIC, "");
		String[] credential = new String(Base64.decode(rawCredential)).split(":");
		String usernameReq = credential[0];
		String passwordReq = credential[1];
		
		if (usernameReq.equals(getUsername()) && passwordReq.equals(getPassword()))
			chain.proceed(req, trigger, ctx);
		else 
			trigger.sendInformation(new BasicClassicHttpResponse(HttpServletResponse.SC_UNAUTHORIZED));
	}
	
	public void handleTestHeaders(ClassicHttpRequest req, ClassicHttpResponse res, HttpContext ctx) throws IOException {
		StringEntity out = new StringEntity("out");
		res.setEntity(out);
		res.setCode(HttpServletResponse.SC_OK);
		
		for (Header hdr : req.getHeaders()) {
			if (hdr.getName().startsWith("testsuite-") || hdr.getName().equals(HttpConstants.TRANSFER_ENCODING))
				res.setHeader("out-" + hdr.getName(), hdr.getValue());
		}
		res.close();
	}
	
	public void handleTestReadTimeout(ClassicHttpRequest req, ClassicHttpResponse res, HttpContext ctx) throws IOException {
		
		try {
			Utilities.sleep(Integer.valueOf(req.getHeader("sleep").getValue()));
		} catch (ProtocolException e) {
			// ignore
		}
		
		StringEntity out = new StringEntity("out");
		res.setEntity(out);
		res.setCode(HttpServletResponse.SC_OK);
		res.close();
	}
	
	public void handleTestRedirect(ClassicHttpRequest req, ClassicHttpResponse res, HttpContext ctx) throws IOException {		
		
		try {
			URIBuilder uriBuilder = new URIBuilder(req.getUri());
			Map<String, String> params = uriBuilder.getQueryParams().stream()
					.collect(Collectors.toMap(e -> e.getName(), e -> e.getValue()));
			
			Integer hop = Integer.valueOf(Objects.requireNonNullElse(params.get("hop"), "0"));
			Integer maxHop = Integer.valueOf(Objects.requireNonNullElse(params.get("maxHop"), "-1"));

			String redirectType = params.get("redirectType");
			
			if (hop.equals(maxHop)) {
				res.setEntity(new StringEntity(hop.toString()));
				res.setCode(HttpServletResponse.SC_OK);
			} else {
				uriBuilder.setParameter("hop", String.valueOf(hop + 1));
				
				res.setEntity(null);
				res.setHeader(new BasicHeader(HttpConstants.REDIRECT_LOCATION, uriBuilder.build()));
				res.setCode(Integer.valueOf(redirectType));
			}
		
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
		
		res.close();
	}
	
	public void handleTestParams(ClassicHttpRequest req, ClassicHttpResponse res, HttpContext ctx) throws IOException {
		StringEntity out = new StringEntity("out");
		res.setEntity(out);
		res.setCode(HttpServletResponse.SC_OK);
		
		try { 
			String[] query = req.getUri().getQuery().split("&");
			for (String param : query) {
				String[] entry = param.split("=");
				if (entry.length == 2 && entry[0].startsWith("testsuite-"))
					res.setHeader(new BasicHeader(entry[0], entry[1]));
			}
		} catch (URISyntaxException e) { 
			throw new IOException(e);
		}
		res.close();
	}
	
	
	public void handleTestProxy(ClassicHttpRequest req, ClassicHttpResponse res, HttpContext ctx)
			throws IOException {
		
		try {
			Header proxied = req.getHeader("Proxied");
			if (proxied != null
					&& proxied.getValue() != null
					&& proxied.getValue().equals("true")) {
				StringEntity out = new StringEntity("proxied");
				res.setEntity(out);
				res.setCode(HttpServletResponse.SC_OK);
				res.close();
			} else {
				StringEntity out = new StringEntity("no proxy header present");
				res.setEntity(out);
				res.setCode(HttpServletResponse.SC_BAD_REQUEST);
				res.close();
			}
		} catch (ProtocolException e) {
			throw new IOException(e);
		}
	}
	
	public void handleTestThrottling(ClassicHttpRequest req, ClassicHttpResponse res, HttpContext ctx)
			throws IOException {
		
		try {
			
			Header transferEncoding = req.getHeader(HttpConstants.TRANSFER_ENCODING);
			if (transferEncoding != null 
					&& !transferEncoding.getValue().equals(HttpConstants.TRANSFER_ENCODING_VALUE_CHUNCKED)) {
				throw new IOException("Errore, il transfer encoding dovrebbe esssere di tipo chunked per i throttled stream");
			}
				
			try(InputStream is = req.getEntity().getContent()) {
				is.readAllBytes();
			}
						
			StringEntity entity = new StringEntity("");
			res.setCode(200);
			res.setEntity(entity);
			res.close();
			
		} catch (ProtocolException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Endpoint per il test della decompressione automatica. Il path finale ('gzip',
	 * 'x-gzip', 'deflate-zlib', 'deflate-raw') decide la modalita' di codifica del body.
	 * Il body in chiaro e' sempre {@link #PLAIN_BODY_CONTENT_ENCODING_TEST}. Il valore
	 * 'Accept-Encoding' ricevuto in richiesta viene echeggiato come header di response
	 * {@link #ECHO_ACCEPT_ENCODING_HEADER} per consentire ai test di asserire che il
	 * client abbia iniettato l'header come previsto.
	 */
	public void handleTestContentEncoding(ClassicHttpRequest req, ClassicHttpResponse res, HttpContext ctx) throws IOException {

		String[] segments = req.getRequestUri().split("/");
		String mode = segments[segments.length - 1].toLowerCase();

		byte[] payload;
		String contentEncodingHeader;
		if (HttpConstants.CONTENT_ENCODING_VALUE_GZIP.equals(mode)) {
			payload = gzipEncode(PLAIN_BODY_CONTENT_ENCODING_TEST);
			contentEncodingHeader = HttpConstants.CONTENT_ENCODING_VALUE_GZIP;
		} else if (HttpConstants.CONTENT_ENCODING_VALUE_X_GZIP.equals(mode)) {
			payload = gzipEncode(PLAIN_BODY_CONTENT_ENCODING_TEST);
			contentEncodingHeader = HttpConstants.CONTENT_ENCODING_VALUE_X_GZIP;
		} else if (CONTENT_ENCODING_TEST_MODE_DEFLATE_ZLIB.equals(mode)) {
			payload = deflateEncodeZlib(PLAIN_BODY_CONTENT_ENCODING_TEST);
			contentEncodingHeader = HttpConstants.CONTENT_ENCODING_VALUE_DEFLATE;
		} else if (CONTENT_ENCODING_TEST_MODE_DEFLATE_RAW.equals(mode)) {
			payload = deflateEncodeRaw(PLAIN_BODY_CONTENT_ENCODING_TEST);
			contentEncodingHeader = HttpConstants.CONTENT_ENCODING_VALUE_DEFLATE;
		} else {
			/*
			 * Wildcard mode: claim un Content-Encoding qualunque (br, zstd, compress,
			 * o valori non standard) inviando il body in chiaro. Usato dai test per
			 * verificare che il client opt-in lanci un'eccezione 'Unsupported
			 * Content-Encoding' quando l'encoding non e' gestito.
			 */
			payload = PLAIN_BODY_CONTENT_ENCODING_TEST.getBytes(StandardCharsets.UTF_8);
			contentEncodingHeader = mode;
		}

		// echo dell'Accept-Encoding ricevuto per asserzioni lato client
		Header receivedAcceptEncoding;
		try {
			receivedAcceptEncoding = req.getHeader(HttpConstants.ACCEPT_ENCODING);
		} catch (ProtocolException e) {
			receivedAcceptEncoding = null;
		}
		res.setHeader(new BasicHeader(ECHO_ACCEPT_ENCODING_HEADER,
				receivedAcceptEncoding != null ? receivedAcceptEncoding.getValue() : ""));

		res.setHeader(new BasicHeader(HttpConstants.CONTENT_ENCODING, contentEncodingHeader));
		res.setEntity(new ByteArrayEntity(payload, null));
		res.setCode(HttpServletResponse.SC_OK);
		res.close();
	}

	private static byte[] gzipEncode(String plain) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (GZIPOutputStream gz = new GZIPOutputStream(baos)) {
			gz.write(plain.getBytes(StandardCharsets.UTF_8));
		}
		return baos.toByteArray();
	}

	private static byte[] deflateEncodeZlib(String plain) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// DeflaterOutputStream con Deflater di default => header zlib RFC 1950 (CMF=0x78)
		try (DeflaterOutputStream def = new DeflaterOutputStream(baos)) {
			def.write(plain.getBytes(StandardCharsets.UTF_8));
		}
		return baos.toByteArray();
	}

	private static byte[] deflateEncodeRaw(String plain) throws IOException {
		// nowrap=true => raw deflate RFC 1951 (nessun header zlib)
		Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (DeflaterOutputStream def = new DeflaterOutputStream(baos, deflater)) {
			def.write(plain.getBytes(StandardCharsets.UTF_8));
		} finally {
			deflater.end();
		}
		return baos.toByteArray();
	}

	public void printRequest(ClassicHttpRequest req, ClassicHttpResponse res, HttpContext ctx)
			throws IOException {
		
		
		if (this.httpsEnabled) {
			HttpCoreContext core = HttpCoreContext.cast(ctx);
			SSLSession ssl = core.getSSLSession(); // dettagli TLS della connessione
			
			try {
				ssl.getPeerPrincipal();
				res.addHeader("verified", true);
			} catch (SSLPeerUnverifiedException e) {
				res.addHeader("verified", false);
			}
		}
		
		StringEntity out = new StringEntity("blah");
		
		res.setEntity(out);
		res.setCode(HttpServletResponse.SC_OK);
		res.close();
	}
	
	
}
