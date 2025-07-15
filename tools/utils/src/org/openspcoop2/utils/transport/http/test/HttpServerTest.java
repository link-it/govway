/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;

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
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.protocol.HttpContext;
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
	private final HttpServer server;
	private final String username;
	private final String password;
	
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
				.register(LOCALHOST, "/print", this::printRequest);
		
		if (ctx != null) {
			serverBuilder.setSslContext(ctx);
		}
		
		this.server = serverBuilder.create();
		this.server.start();
		
		System.out.println("Mock server active port: " + getPort());
		System.out.println("Mock auth username: " + getUsername());
		System.out.println("Mock auth password: " + getPassword());
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
	
	public void printRequest(ClassicHttpRequest req, ClassicHttpResponse res, HttpContext ctx)
			throws IOException {
		
		
		System.out.println("------- REQUEST ---------");
		System.out.println("URI: " + req.getRequestUri());
		Arrays.stream(req.getHeaders())
			.forEach(hdr ->
				System.out.println("\t" + hdr.getName() + ": " + hdr.getValue()));
		
		System.out.println(req.getVersion());
		
		StringEntity out = new StringEntity("blah");
		
		res.setEntity(out);
		res.setCode(HttpServletResponse.SC_OK);
		res.close();
	}
	
	
}
