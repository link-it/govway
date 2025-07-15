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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.Proxy.Type;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.core5.net.URIBuilder;
import org.bouncycastle.util.Arrays;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpLibrary;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.testng.Assert;

import jakarta.servlet.http.HttpServletResponse;

/**
 * HttpTest
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpTest {

	private static HttpServerTest server = null;
	private static HttpProxyTest proxy = null;
	
	public static void startServers() throws IOException {
		if (server != null) {
			server.close();
			proxy.close();
		}
		server = new HttpServerTest();
		proxy = new HttpProxyTest(server);
	}
	
	public static void stopServers() {
		if (server != null) {
			server.close();
			proxy.close();
		}
	}
	
	public static void main(String[] arg) throws IOException, UtilsException, URISyntaxException {		
				
		Object[][] params;
		
		HttpTest test = new HttpTest();
		try {
			startServers();

			test.testFile(HttpLibrary.HTTPCORE);
			test.testFile(HttpLibrary.URLCONNECTION);
			
			params = test.methodsHttpDataProvider();
			for (Object[] param : params)
				test.check((HttpRequest) param[0], (HttpResponse) param[1]);
			
			params = test.headersHttpDataProvider();
			for (Object[] param : params)
				test.check((HttpRequest) param[0], (HttpResponse) param[1]);
			
			params = test.paramsHttpDataProvider();
			for (Object[] param : params)
				test.check((HttpRequest) param[0], (HttpResponse) param[1]);
			
			test.testReadTimeout(HttpLibrary.HTTPCORE);
			test.testReadTimeout(HttpLibrary.URLCONNECTION);
			
			//test.testConnectionTimeout(HttpLibrary.HTTPCORE);
			//test.testConnectionTimeout(HttpLibrary.URLCONNECTION);
			
			test.testRedirect(HttpLibrary.URLCONNECTION, HttpServletResponse.SC_TEMPORARY_REDIRECT);
			test.testRedirect(HttpLibrary.HTTPCORE, HttpServletResponse.SC_TEMPORARY_REDIRECT);
			
			test.testRedirect(HttpLibrary.URLCONNECTION, HttpServletResponse.SC_MOVED_PERMANENTLY);
			test.testRedirect(HttpLibrary.HTTPCORE, HttpServletResponse.SC_MOVED_PERMANENTLY);
		
			test.testHttpProxy(HttpLibrary.URLCONNECTION);
			test.testHttpProxy(HttpLibrary.HTTPCORE);
			
			test.testThrottling(HttpLibrary.URLCONNECTION, 100, 100);
			test.testThrottling(HttpLibrary.HTTPCORE, 100, 100);

		} finally {
			stopServers();
		}
	}
	
	public HttpTest() {
		// ignore
	}
	
	public String createServerUrl(String path) {
		return String.format("http://localhost:%d%s", server.getPort(), path);
	}
	
	public HttpRequest createBaseRequest(HttpLibrary library) {
		HttpRequest req = new HttpRequest();
		req.setHttpLibrary(library);
		req.setUrl(createServerUrl("/print"));
		req.addHeader("User-Agent", "Java");
		req.addHeader("Accept-Encoding", "gzip, x-gzip, deflate");
		req.addHeader("Accept", "*/*");
		req.addHeader("Host", "localhost:" + server.getPort());
		req.setUsername(server.getUsername());
		req.setPassword(server.getPassword());
		req.setMethod(HttpRequestMethod.GET);
		
		return req;
	}
	
	/**
	 * Test di contorllo che l'utility comunque sia capace di leggere i file 
	 * del file system locale indipendentemente dalla libreria
	 * @param library
	 * @throws IOException
	 * @throws UtilsException
	 */
	private void testFile(HttpLibrary library) throws IOException, UtilsException {
		HttpRequest req = createBaseRequest(library);
		Path path = Files.createTempFile("test_http_utils", "_temp_file", PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-rw-rw-")));
		File file = path.toFile();
		String testContent = "contenuto di prova";
		
		try (OutputStream os = new FileOutputStream(file)) {
			os.write(testContent.getBytes());
		}
		
		req.setUrl(path.toUri().toString());
		
		HttpResponse res = HttpUtilities.httpInvoke(req);
		
		Assert.assertEquals(testContent, new String(res.getContent()));
	}
	
	/**
	 * Data provider per testare che tutti i vari metodi HTTP funzionino correttamente
	 * @return
	 */
	public Object[][] methodsHttpDataProvider() {
		
		HttpLibrary[] httpLibraries = {HttpLibrary.HTTPCORE, HttpLibrary.URLCONNECTION};
		
		Object[][] configs = {
				{HttpRequestMethod.GET, null, "get"}, 
				{HttpRequestMethod.POST, null, "post"},
				{HttpRequestMethod.DELETE, null, "delete"}, 
				{HttpRequestMethod.PUT, null, "put"}
		};
		
		Object[][] data = new Object[httpLibraries.length * configs.length][];
		
		int i = 0;
		for (HttpLibrary lib : httpLibraries) { 
			for (Object[] conf : configs) {
				HttpRequest req = createBaseRequest(lib);
				req.setMethod((HttpRequestMethod) conf[0]);
				req.setUrl(createServerUrl("/methods/" + req.getMethod().toString()));
				if (conf[1] != null)
					req.setContent(((String)conf[1]).getBytes());
				
				HttpResponse res = new HttpResponse();
				res.setResultHTTPOperation(200);
				if (conf[2] != null) {
					res.setContent(((String)conf[2]).getBytes());
				}
				
				data[i] = new Object[]{req, res};
				i++;
			}
		}
		
		return data;
	}
	
	/**
	 * Data provider per testare che gli header vengano iniviati correttamente
	 * @return
	 */
	public Object[][] headersHttpDataProvider() {
		
		HttpLibrary[] httpLibraries = {HttpLibrary.HTTPCORE, HttpLibrary.URLCONNECTION};
		
		
		List<Map<String, String>> headers = List.of(
				Map.of("testsuite-hdr1", "val1"),
				Map.of("testsuite-hdr2", "val1,val2,val3")
		);
		
		Object[][] data = new Object[httpLibraries.length * (headers.size() + 1)][];
		
		int i = 0;
		for (HttpLibrary lib : httpLibraries) { 
			for (Map<String, String> header : headers) {
				HttpRequest req = createBaseRequest(lib);
				req.setMethod(HttpRequestMethod.POST);
				req.setUrl(createServerUrl("/headers"));
				req.setContent("".getBytes());
				req.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
				
				for (Map.Entry<String, String> entry : header.entrySet())
					req.addHeader(entry.getKey(), entry.getValue());
				
				HttpResponse res = new HttpResponse();
				res.setResultHTTPOperation(200);
				res.setContent("out".getBytes());
				for (Map.Entry<String, String> entry : header.entrySet())
					res.addHeader("out-" + entry.getKey(), entry.getValue());
				
				data[i++] = new Object[]{req, res};
			}
			
			//controllo del transfer encoding chunked
			HttpRequest req = createBaseRequest(lib);
			req.setMethod(HttpRequestMethod.POST);
			req.setUrl(createServerUrl("/headers"));
			req.setContent("in".getBytes());
			req.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
			req.setForceTransferEncodingChunked(true);
			
			HttpResponse res = new HttpResponse();
			res.setResultHTTPOperation(200);
			res.setContent("out".getBytes());
			res.addHeader("out-" + HttpConstants.TRANSFER_ENCODING, HttpConstants.TRANSFER_ENCODING_VALUE_CHUNCKED);
			data[i++] = new Object[]{req, res};
		}
		
		return data;
	}
	
	/**
	 * Data provider per testare che tutti i paraemtri della query funzionino correttamente
	 * @return
	 */
	public Object[][] paramsHttpDataProvider() {
		
		HttpLibrary[] httpLibraries = {HttpLibrary.HTTPCORE, HttpLibrary.URLCONNECTION};
		
		
		List<Map<String, String>> params = List.of(
				Map.of("testsuite-param1", "val1"),
				Map.of("testsuite-param2", "val1,val2,val3")
		);
		
		Object[][] data = new Object[httpLibraries.length * params.size()][];
		
		int i = 0;
		for (HttpLibrary lib : httpLibraries) { 
			for (Map<String, String> param : params) {
				HttpRequest req = createBaseRequest(lib);
				req.setMethod(HttpRequestMethod.GET);
				req.setUrl(createServerUrl("/params"));
				for (Map.Entry<String, String> entry : param.entrySet())
					req.addParam(entry.getKey(), entry.getValue());
				
				HttpResponse res = new HttpResponse();
				res.setResultHTTPOperation(200);
				res.setContent("out".getBytes());
				for (Map.Entry<String, String> entry : param.entrySet())
					res.addHeader(entry.getKey(), entry.getValue());
				
				data[i] = new Object[]{req, res};
				i++;
			}
		}
		
		return data;
	}
	
	
	/*public void testConnectionTimeout(HttpLibrary lib) throws UtilsException {
		HttpRequest req = createBaseRequest(lib);
		req.setUrl("http://127.0.0.1:" + (server.getPort()+1) + "/readTimeout");
		req.addHeader("sleep", "100");
		req.setReadTimeout(1000);
		
		HttpResponse res = new HttpResponse();
		res.setContent("out".getBytes());
		res.setResultHTTPOperation(200);
		
		check(req, res);
		
		
		req = createBaseRequest(lib);
		req.setUrl("http://localhost:" + server.getPort() + "/readTimeout");
		req.addHeader("sleep", "1000");
		req.setReadTimeout(1000);
		
		res = new HttpResponse();
		res.setContent("out".getBytes());
		res.setResultHTTPOperation(200);
		
		try {
			check(req, res);
		} catch (UtilsException e) {
			Assert.assertTrue(e.getCause() instanceof SocketTimeoutException, "Aspettavo un eccezione di tipo: " + SocketTimeoutException.class.getCanonicalName() + ", ottenuta eccezione di tipo: " + e.getClass().getCanonicalName());
		}		
	}*/
	
	/**
	 * Test per controllare che il read timeout venga impostato e utilizzato
	 * correttamente
	 * @param lib
	 * @throws UtilsException
	 */
	public void testReadTimeout(HttpLibrary lib) throws UtilsException {
		
		// primo test il server si addormenta per un periodo inferiore rispetto al timeout
		HttpRequest req = createBaseRequest(lib);
		req.setUrl(createServerUrl("/readTimeout"));
		req.addHeader("sleep", "100");
		req.setReadTimeout(1000);
		
		HttpResponse res = new HttpResponse();
		res.setContent("out".getBytes());
		res.setResultHTTPOperation(200);
		
		check(req, res);
		
		
		// secondo test il server si addormenta per un periodo uguale al timeout
		req = createBaseRequest(lib);
		req.setUrl(createServerUrl("/readTimeout"));
		req.addHeader("sleep", "1000");
		req.setReadTimeout(1000);
		
		res = new HttpResponse();
		res.setContent("out".getBytes());
		res.setResultHTTPOperation(200);
		
		// controllo che l'eccezione lanciata sia di tipo socket timeout
		try {
			check(req, res);
		} catch (UtilsException e) {
			Assert.assertTrue(e.getCause() instanceof SocketTimeoutException, "Aspettavo un eccezione di tipo: " + SocketTimeoutException.class.getCanonicalName() + ", ottenuta eccezione di tipo: " + e.getClass().getCanonicalName());
		}
	}
	
	/**
	 * Test per controllare il funzionamento del redirect automatico cosi come il
	 * contorllo nel masismo numero di redirect impostato dalle system properties
	 * @param lib
	 * @param redirectType
	 * @throws UtilsException
	 * @throws URISyntaxException
	 */
	public void testRedirect(HttpLibrary lib, Integer redirectType) throws UtilsException, URISyntaxException {
		final String REDIRECT_FORMAT = createServerUrl("/redirect?maxHop=%d&redirectType=%d");
		Integer maxHop = 10;
		
		// controllo che se disabilitato (o di default) non vengano seguiti i redirect
		HttpRequest req = createBaseRequest(lib);
		req.setUrl(String.format(REDIRECT_FORMAT, maxHop, redirectType));
		
		HttpResponse res = new HttpResponse();
		URIBuilder resUri = new URIBuilder(req.getUrl());
		
		res.setContent("".getBytes());
		res.setResultHTTPOperation(redirectType);
		res.addHeader(HttpConstants.REDIRECT_LOCATION, resUri.setParameter("hop", "1").build().toString());
		
		check(req, res);
		
		// controllo che se abilito i redirect allora vengano seguiti
		req = createBaseRequest(lib);
		req.setUrl(String.format(REDIRECT_FORMAT, maxHop, redirectType));
		req.setFollowRedirects(true);
		
		res = new HttpResponse();
		
		res.setContent(String.valueOf(maxHop).getBytes());
		res.setResultHTTPOperation(200);
			
		check(req, res);
		
		// controllo che il comportamente di massimi redirect abbia 20 come massimo
		req = createBaseRequest(lib);
		maxHop = 50;
		req.setUrl(String.format(REDIRECT_FORMAT, maxHop, redirectType));
		req.setFollowRedirects(true);
		
		res = new HttpResponse();
		
		res.setContent(String.valueOf(maxHop).getBytes());
		res.setResultHTTPOperation(200);
		
		try {
			check(req, res);
		} catch (UtilsException e) {
			if (e.getCause() != null && (e.getCause() instanceof ClientProtocolException || e.getCause() instanceof ProtocolException)) {
				String message = e.getCause().getMessage();
				if (!message.matches("Maximum redirects \\(\\d+\\) exceeded")
						&& !message.matches("Server redirected too many times \\(\\d+\\)")) {
					Assert.fail("Eccezione non attesa nell'esecuzione della richiesta: " + message, e);
				}
			} else {
				Assert.fail("Eccezione non attesa nell'esecuzione della richiesta: " + e.getCause(), e);
			}
			
		}
	}
	
	/**
	 * Test che controlla il corretto funzionamento nel caso di proxy abilitati
	 * @param httpLibrary
	 * @throws UtilsException
	 */
	public void testHttpProxy(HttpLibrary httpLibrary) throws UtilsException {
		HttpRequest req = createBaseRequest(httpLibrary);
		req.setProxyHostname("127.0.0.1");
		req.setProxyPort(proxy.getPort());
		req.setProxyType(Type.HTTP);
		req.setProxyUsername(proxy.getUsername());
		req.setProxyPassword(proxy.getPassword());
		
		
		HttpResponse res = new HttpResponse();		
		res.setContent("proxied".getBytes());
		res.setResultHTTPOperation(200);

		check(req, res);
	}
	
	
	/**
	 * Test per il contorllo del throttling
	 */
	private final Random rnd = new Random();
	public void testThrottling(HttpLibrary httpLibrary, int throttlingSize, int throttlingMs) throws UtilsException {
		HttpRequest req = createBaseRequest(httpLibrary);
		req.setUrl("http://localhost:" + server.getPort() + "/throttling");
		req.setMethod(HttpRequestMethod.POST);
		
		byte[] content = new byte[10000];
		for (int i = 0; i < content.length; i++)
			content[i] = (byte) this.rnd.nextInt('a', 'z');
		
		req.setContent(content);
		req.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		
		req.setThrottlingSendByte(throttlingSize);
		req.setThrottlingSendMs(throttlingMs);
		
		HttpResponse res = new HttpResponse();		
		res.setContent("".getBytes());
		res.setResultHTTPOperation(200);

		long t0 = System.currentTimeMillis();

		check(req, res);
		
		long t1 = System.currentTimeMillis();
		
		long actualTime = (t1 - t0);
		long expectTime = (throttlingSize * throttlingMs);
		
		Assert.assertTrue(actualTime > 1.0 * expectTime);
		Assert.assertTrue(actualTime < 1.5 * expectTime);
	}
	
	private void checkUri(String expected, String actual) throws UtilsException {
		try {
			URIBuilder expectedUri = new URIBuilder(expected);
			URIBuilder actualUri = new URIBuilder(actual);
			
			Assert.assertEquals(expectedUri.build(), actualUri.build());
		} catch (URISyntaxException e) {
			throw new UtilsException(e);
		}
	}
	
	/**
	 * metodo di utilità utilizzato per controllare il corretto response http
	 * @param req
	 * @param expectedResponse
	 * @throws UtilsException
	 */
	private void check(HttpRequest req, HttpResponse expectedResponse) throws UtilsException {
		
		HttpResponse actualResponse = HttpUtilities.httpInvoke(req);
		
		
		String[] ignoreHeaders = {
				HttpConstants.SERVER,
				HttpConstants.RETURN_CODE,
				HttpConstants.CONNECTION,
				HttpConstants.CONTENT_LENGTH,
				HttpConstants.CONTENT_TYPE,
				"Date"
		};
		
		for (String ignoreHeader : ignoreHeaders) {
			if (actualResponse.getHeaderFirstValue(ignoreHeader) != null)
				expectedResponse.addHeader(ignoreHeader, actualResponse.getHeaderFirstValue(ignoreHeader));

		}

		System.out.println("content: " + new String(actualResponse.getContent()));
		System.out.println("code: " + actualResponse.getResultHTTPOperation());
		System.out.println("headers: ");
		actualResponse.getHeadersValues().entrySet().stream().forEach(e ->
				System.out.println("\t" + e.getKey() + ": " + String.join(",", e.getValue())));
		
		
		// controllo il codice di risposta
		Assert.assertEquals(actualResponse.getResultHTTPOperation(), expectedResponse.getResultHTTPOperation());
		
		// controllo gli headers
		Map<String, List<String>> expectedHeaders = expectedResponse.getHeadersValues();
		Map<String, List<String>> actualHeaders = actualResponse.getHeadersValues();
		
		if (expectedHeaders.size() != actualHeaders.size())
			Assert.fail("numero header attesi: " + expectedHeaders.size() + ", diverso dal numero di header ottenuti: " + actualHeaders.size());
		for (Map.Entry<String, List<String>> header : expectedHeaders.entrySet()) {
			// controllo che l'url sia equivalente
			if (header.getKey().equals(HttpConstants.REDIRECT_LOCATION)) {
				checkUri(header.getValue().getFirst(), actualHeaders.get(header.getKey()).getFirst());
			} else {
				Assert.assertEquals(actualHeaders.get(header.getKey()), header.getValue(), "header: " + header.getKey() + " atteso diverso da quello ottenuto");
			}
		}
		
		
		
		// controllo il contenuto
		byte[] actualContent = actualResponse.getContent();
		byte[] expectedContent = expectedResponse.getContent();
		if (!Arrays.areEqual(actualContent, expectedContent)) {
			Assert.fail("Il contenuto della risposta: \"" + new String(actualContent) + "\" risulta diverso da quello atteso: \"" + new String(expectedContent) + "\"");
		}
	}
}
