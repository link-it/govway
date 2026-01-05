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

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ExceptionListener;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.impl.bootstrap.HttpServer;
import org.apache.hc.core5.http.impl.bootstrap.ServerBootstrap;
import org.apache.hc.core5.http.io.HttpFilterChain;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.HttpFilterChain.ResponseTrigger;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.bouncycastle.util.encoders.Base64;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import jakarta.servlet.http.HttpServletResponse;

/**
 * HttpProxyTest
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpProxyTest implements Closeable {
	
	private final HttpServerTest backend;
	private final HttpServer server;
	private final String username;
	private final String password;
	
	public HttpProxyTest(HttpServerTest backend) throws IOException {
		
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
		
		this.server = ServerBootstrap.bootstrap()
				.setListenerPort(-1)
				.setSslContext(null)
				.setSocketConfig(socketConfig)
				.setExceptionListener(new ExceptionListener() {
					
					@Override
					public void onError(HttpConnection arg0, Exception arg1) {
						//ignore
					}
					
					@Override
					public void onError(Exception arg0) {
						//ignore
					}
				})
				.addFilterFirst("auth", this::handleAuth)
				.register("localhost", "*", this::handle)
				.create();
		
		this.backend = backend;
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
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void handleAuth(ClassicHttpRequest req, ResponseTrigger trigger, HttpContext ctx, HttpFilterChain chain) throws HttpException, IOException {
		Header auth = req.getHeader(HttpConstants.PROXY_AUTHORIZATION);
		String rawCredential = auth.getValue().replace(HttpConstants.AUTHORIZATION_PREFIX_BASIC, "");
		String[] credential = new String(Base64.decode(rawCredential)).split(":");
		String usernameReq = credential[0];
		String passwordReq = credential[1];
				
		if (usernameReq.equals(getUsername()) && passwordReq.equals(getPassword()))
			chain.proceed(req, trigger, ctx);
		else 
			trigger.sendInformation(new BasicClassicHttpResponse(HttpServletResponse.SC_UNAUTHORIZED));
	}
	
	public void handle(ClassicHttpRequest req, ClassicHttpResponse res, HttpContext ctx)
			throws IOException {
		try {
			executeProxy(req, res);
		} catch (Exception e) {
			e.printStackTrace();
			StringEntity out = new StringEntity(e.getMessage());
			res.setEntity(out);
			res.setCode(HttpServletResponse.SC_BAD_REQUEST);
			res.close();
		}
	}
	
	private void executeProxy(ClassicHttpRequest req, ClassicHttpResponse res) throws UtilsException, IOException {
		String url = this.backend.getUrl() + "/proxy";
		
		HttpRequest proxiedRequest = new HttpRequest();
		for (Header hdr : req.getHeaders()) {
			if (!hdr.getName().startsWith("Proxy"))
				proxiedRequest.addHeader(hdr.getName(), hdr.getValue());
		}
		proxiedRequest.addHeader("Proxied", "true");
		proxiedRequest.setUrl(url);
		proxiedRequest.setMethod(HttpRequestMethod.valueOf(req.getMethod()));
		
		
		HttpResponse proxiedResponse = HttpUtilities.httpInvoke(proxiedRequest);
		
		ByteArrayEntity out = new ByteArrayEntity(
				proxiedResponse.getContent(), 
				ContentType.parse(proxiedResponse.getContentType()));
		
		for (Map.Entry<String, List<String>> hdr : proxiedResponse.getHeadersValues().entrySet()) {
			if (hdr.getKey().equals(HttpConstants.CONTENT_TYPE))
				res.setHeader(hdr.getKey(), String.join(",", hdr.getValue()));
		}

		res.setEntity(out);
		res.setCode(proxiedResponse.getResultHTTPOperation());
		res.close();
	}
	
	
}
