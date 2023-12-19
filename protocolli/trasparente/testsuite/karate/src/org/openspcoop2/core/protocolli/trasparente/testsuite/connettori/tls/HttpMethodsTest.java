/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
public class HttpMethodsTest extends ConfigLoader {
	
	@Test
	public void deleteHttp() throws Exception {
		test("http", HttpRequestMethod.DELETE);
	}
	@Test
	public void deleteHttps() throws Exception {
		test("https", HttpRequestMethod.DELETE);
	}
	@Test
	public void deleteHttpsConnettore() throws Exception {
		test("https-connettore", HttpRequestMethod.DELETE);
	}
	
	
	@Test
	public void getHttp() throws Exception {
		test("http", HttpRequestMethod.GET);
	}
	@Test
	public void getHttps() throws Exception {
		test("https", HttpRequestMethod.GET);
	}
	@Test
	public void getHttpsConnettore() throws Exception {
		test("https-connettore", HttpRequestMethod.GET);
	}
	
	
	@Test
	public void postHttp() throws Exception {
		test("http", HttpRequestMethod.POST);
	}
	@Test
	public void postHttps() throws Exception {
		test("https", HttpRequestMethod.POST);
	}
	@Test
	public void postHttpsConnettore() throws Exception {
		test("https-connettore", HttpRequestMethod.POST);
	}
	
	
	@Test
	public void putHttp() throws Exception {
		test("http", HttpRequestMethod.PUT);
	}
	@Test
	public void putHttps() throws Exception {
		test("https", HttpRequestMethod.PUT);
	}
	@Test
	public void putHttpsConnettore() throws Exception {
		test("https-connettore", HttpRequestMethod.PUT);
	}
	
	
	@Test
	public void headHttp() throws Exception {
		test("http", HttpRequestMethod.HEAD);
	}
	@Test
	public void headHttps() throws Exception {
		test("https", HttpRequestMethod.HEAD);
	}
	@Test
	public void headHttpsConnettore() throws Exception {
		test("https-connettore", HttpRequestMethod.HEAD);
	}
	
	
	@Test
	public void linkHttp() throws Exception {
		test("http", HttpRequestMethod.LINK);
	}
	@Test
	public void linkHttps() throws Exception {
		test("https", HttpRequestMethod.LINK);
	}
	@Test
	public void linkHttpsConnettore() throws Exception {
		test("https-connettore", HttpRequestMethod.LINK);
	}
	
	
	@Test
	public void unlinkHttp() throws Exception {
		test("http", HttpRequestMethod.UNLINK);
	}
	@Test
	public void unlinkHttps() throws Exception {
		test("https", HttpRequestMethod.UNLINK);
	}
	@Test
	public void unlinkHttpsConnettore() throws Exception {
		test("https-connettore", HttpRequestMethod.UNLINK);
	}
	
	
	@Test
	public void optionsHttp() throws Exception {
		test("http", HttpRequestMethod.OPTIONS);
	}
	@Test
	public void optionsHttps() throws Exception {
		test("https", HttpRequestMethod.OPTIONS);
	}
	@Test
	public void optionsHttpsConnettore() throws Exception {
		test("https-connettore", HttpRequestMethod.OPTIONS);
	}
	
	
	@Test
	public void traceHttp() throws Exception {
		test("http", HttpRequestMethod.TRACE);
	}
	@Test
	public void traceHttps() throws Exception {
		test("https", HttpRequestMethod.TRACE);
	}
	@Test
	public void traceHttpsConnettore() throws Exception {
		test("https-connettore", HttpRequestMethod.TRACE);
	}
	
	
	@Test
	public void patchHttp() throws Exception {
		test("http", HttpRequestMethod.PATCH);
	}
	@Test
	public void patchHttps() throws Exception {
		test("https", HttpRequestMethod.PATCH);
	}
	@Test
	public void patchHttpsConnettore() throws Exception {
		test("https-connettore", HttpRequestMethod.PATCH);
	}
	
	
	
	
	private static HttpResponse test(String tipoTest, HttpRequestMethod httpMethod) throws Exception {
		
		String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/TestHttpMethods/v1/test";
		
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
	
			DBVerifier.verify(idTransazione, esitoExpected, null);
					
			return response;
		}finally {
			if(fInput!=null) {
				fInput.delete();
			}
		}
		
	}
	
	
}
