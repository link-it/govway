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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.tls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* RestTest
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class HttpMethodsTestEngine extends ConfigLoader {
	
	private static final String TYPE_HTTP = "http";
	private static final String TYPE_HTTPS = "https";
	private static final String TYPE_HTTPS_CONNETTORE = "https-connettore";
	
	private HttpLibraryMode mode;
	
	public HttpMethodsTestEngine() {
		this.mode = null;
	}
	
	public HttpMethodsTestEngine(HttpLibraryMode mode) {
		this.mode = mode;
	}
	
	
	@Test
	public void deleteHttp() throws Exception {
		test(TYPE_HTTP, HttpRequestMethod.DELETE);
	}
	@Test
	public void deleteHttps() throws Exception {
		test(TYPE_HTTPS, HttpRequestMethod.DELETE);
	}
	@Test
	public void deleteHttpsConnettore() throws Exception {
		test(TYPE_HTTPS_CONNETTORE, HttpRequestMethod.DELETE);
	}
	
	
	@Test
	public void getHttp() throws Exception {
		test(TYPE_HTTP, HttpRequestMethod.GET);
	}
	@Test
	public void getHttps() throws Exception {
		test(TYPE_HTTPS, HttpRequestMethod.GET);
	}
	@Test
	public void getHttpsConnettore() throws Exception {
		test(TYPE_HTTPS_CONNETTORE, HttpRequestMethod.GET);
	}
	
	
	@Test
	public void postHttp() throws Exception {
		test(TYPE_HTTP, HttpRequestMethod.POST);
	}
	@Test
	public void postHttps() throws Exception {
		test(TYPE_HTTPS, HttpRequestMethod.POST);
	}
	@Test
	public void postHttpsConnettore() throws Exception {
		test(TYPE_HTTPS_CONNETTORE, HttpRequestMethod.POST);
	}
	
	
	@Test
	public void putHttp() throws Exception {
		test(TYPE_HTTP, HttpRequestMethod.PUT);
	}
	@Test
	public void putHttps() throws Exception {
		test(TYPE_HTTPS, HttpRequestMethod.PUT);
	}
	@Test
	public void putHttpsConnettore() throws Exception {
		test(TYPE_HTTPS_CONNETTORE, HttpRequestMethod.PUT);
	}
	
	
	@Test
	public void headHttp() throws Exception {
		test(TYPE_HTTP, HttpRequestMethod.HEAD);
	}
	@Test
	public void headHttps() throws Exception {
		test(TYPE_HTTPS, HttpRequestMethod.HEAD);
	}
	@Test
	public void headHttpsConnettore() throws Exception {
		test(TYPE_HTTPS_CONNETTORE, HttpRequestMethod.HEAD);
	}
	
	
	@Test
	public void linkHttp() throws Exception {
		test(TYPE_HTTP, HttpRequestMethod.LINK);
	}
	@Test
	public void linkHttps() throws Exception {
		test(TYPE_HTTPS, HttpRequestMethod.LINK);
	}
	@Test
	public void linkHttpsConnettore() throws Exception {
		test(TYPE_HTTPS_CONNETTORE, HttpRequestMethod.LINK);
	}
	
	
	@Test
	public void unlinkHttp() throws Exception {
		test(TYPE_HTTP, HttpRequestMethod.UNLINK);
	}
	@Test
	public void unlinkHttps() throws Exception {
		test(TYPE_HTTPS, HttpRequestMethod.UNLINK);
	}
	@Test
	public void unlinkHttpsConnettore() throws Exception {
		test(TYPE_HTTPS_CONNETTORE, HttpRequestMethod.UNLINK);
	}
	
	
	@Test
	public void optionsHttp() throws Exception {
		test(TYPE_HTTP, HttpRequestMethod.OPTIONS);
	}
	@Test
	public void optionsHttps() throws Exception {
		test(TYPE_HTTPS, HttpRequestMethod.OPTIONS);
	}
	@Test
	public void optionsHttpsConnettore() throws Exception {
		test(TYPE_HTTPS_CONNETTORE, HttpRequestMethod.OPTIONS);
	}
	
	
	@Test
	public void traceHttp() throws Exception {
		test(TYPE_HTTP, HttpRequestMethod.TRACE);
	}
	@Test
	public void traceHttps() throws Exception {
		test(TYPE_HTTPS, HttpRequestMethod.TRACE);
	}
	@Test
	public void traceHttpsConnettore() throws Exception {
		test(TYPE_HTTPS_CONNETTORE, HttpRequestMethod.TRACE);
	}
	
	
	@Test
	public void patchHttp() throws Exception {
		test(TYPE_HTTP, HttpRequestMethod.PATCH);
	}
	@Test
	public void patchHttps() throws Exception {
		test(TYPE_HTTPS, HttpRequestMethod.PATCH);
	}
	@Test
	public void patchHttpsConnettore() throws Exception {
		test(TYPE_HTTPS_CONNETTORE, HttpRequestMethod.PATCH);
	}
	
	
	
	
	private HttpResponse test(String tipoTest, HttpRequestMethod httpMethod) throws Exception {
		String url = System.getProperty("govway_base_path") + "/in/SoggettoInternoTest/TestHttpMethods/v1/test";
		
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		
		request.setMethod(httpMethod);
		request.addHeader("test-method", httpMethod.name());
		request.addHeader("test-tipo", tipoTest);
		
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		HttpBodyParameters body = new HttpBodyParameters(httpMethod, contentType);
		if(body.isDoOutput()) {
			request.setContentType(contentType);
			request.setContent(content);
		}
		File fInput = null;
		try {
			if(body.isDoInput()) {
				fInput = File.createTempFile("test", ".json");
				FileSystemUtilities.writeFile(fInput, content);
				request.addHeader("test-response", fInput.getAbsolutePath());
			}
			
			request.setUrl(url);
			
			if (this.mode != null)
				this.mode.patchRequest(request);
			
			HttpResponse response = HttpUtilities.httpInvoke(request);
	
			assertEquals(200, response.getResultHTTPOperation());
			
			if(body.isDoInput()) {
				assertEquals(contentType, response.getContentType());
				String requestContent = new String(content);
				assertNotNull(response.getContent());
				String responseContent = new String(response.getContent());
				assertEquals(requestContent, responseContent);
			}
			
			String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
			assertNotNull(idTransazione);
			
			long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
	
			DBVerifier.verify(idTransazione, esitoExpected, this.mode);
					
			return response;
		}finally {
			if(fInput!=null) {
				fInput.delete();
			}
		}
		
	}
	
	
}
