/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.negoziazione;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
* AuthorizationServerTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class NegoziazioneTest extends ConfigLoader {

	public final static String api_negoziazione = "TestNegoziazioneToken";
	
	
	@Test
	public void clientid_clientsecret() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		_test(logCore, api_negoziazione, "clientid_clientsecret", null);
	}
	
	@Test
	public void clientid_clientsecret2() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-azione", "clientid_clientsecret2");
		headers.put("test-user", "ClientIDVerificaNegoziazioneConSecret2");
		headers.put("test-scope", "testNegoziazioneScope2");
		headers.put("test-audience", "testNegoziazioneAudience");
		
		_test(logCore, api_negoziazione, "clientid_clientsecret2", headers);
	}
	
	@Test
	public void clientid_clientsecret3() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-azione", "clientid_clientsecret3");
		
		_test(logCore, api_negoziazione, "clientid_clientsecret3", headers);
	}

	@Test
	public void user_password() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-azione", "user_password");
		headers.put("test-username", "ResourceOwnerPasswordUsername");
		
		_test(logCore, api_negoziazione, "user_password", headers);
	}
	
	@Test
	public void signedJWT_clientSecret() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-azione", "signedJWT_clientSecret");
		headers.put("test-suffix", "DYNAMIC");
		headers.put("test-decode-position", "0");
		
		_test(logCore, api_negoziazione, "signedJWT_clientSecret", headers);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		headers = new HashMap<String, String>();
		headers.put("test-azione", "signedJWT_clientSecret");
		headers.put("test-suffix", "DYNAMIC");
		headers.put("test-decode-position", "1");
		
		_test(logCore, api_negoziazione, "signedJWT_clientSecret", headers);
	}
	
	@Test
	public void signedJWT_clientSecret2() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-azione", "signedJWT_clientSecret2");
		headers.put("test-suffix", "DYNAMIC");
		headers.put("test-decode-position", "0");
		
		_test(logCore, api_negoziazione, "signedJWT_clientSecret2", headers);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		headers = new HashMap<String, String>();
		headers.put("test-azione", "signedJWT_clientSecret2");
		headers.put("test-suffix", "DYNAMIC");
		headers.put("test-decode-position", "1");
		
		_test(logCore, api_negoziazione, "signedJWT_clientSecret2", headers);
	}
	
	@Test
	public void signedJWT_clientSecret3() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-azione", "signedJWT_clientSecret3");
		headers.put("test-suffix", "DYNAMIC");
		headers.put("test-decode-position", "0");
		
		_test(logCore, api_negoziazione, "signedJWT_clientSecret3", headers);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		headers = new HashMap<String, String>();
		headers.put("test-azione", "signedJWT_clientSecret3");
		headers.put("test-suffix", "DYNAMIC");
		headers.put("test-decode-position", "1");
		
		_test(logCore, api_negoziazione, "signedJWT_clientSecret3", headers);
	}
	
	@Test
	public void signedJWT() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-azione", "signedJWT");
		headers.put("test-suffix", "DYNAMIC");
		headers.put("test-decode-position", "0");
		
		_test(logCore, api_negoziazione, "signedJWT", headers);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		headers = new HashMap<String, String>();
		headers.put("test-azione", "signedJWT");
		headers.put("test-suffix", "DYNAMIC");
		headers.put("test-decode-position", "1");
		
		_test(logCore, api_negoziazione, "signedJWT", headers);
	}
	
	@Test
	public void signedJWT2() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-azione", "signedJWT2");
		headers.put("test-suffix", "DYNAMIC");
		headers.put("test-decode-position", "0");
		
		_test(logCore, api_negoziazione, "signedJWT2", headers);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		headers = new HashMap<String, String>();
		headers.put("test-azione", "signedJWT2");
		headers.put("test-suffix", "DYNAMIC");
		headers.put("test-decode-position", "1");
		
		_test(logCore, api_negoziazione, "signedJWT2", headers);
	}
	
	@Test
	public void signedJWT3() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-azione", "signedJWT3");
		headers.put("test-suffix", "DYNAMIC");
		headers.put("test-decode-position", "0");
		
		_test(logCore, api_negoziazione, "signedJWT3", headers);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		headers = new HashMap<String, String>();
		headers.put("test-azione", "signedJWT3");
		headers.put("test-suffix", "DYNAMIC");
		headers.put("test-decode-position", "1");
		
		_test(logCore, api_negoziazione, "signedJWT3", headers);
	}
	
	
	
	private static HttpResponse _test(Logger logCore, String api, String operazione,
			Map<String, String> headers) throws Exception {
		
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		
		String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		
		if(headers!=null && !headers.isEmpty()) {
			for (String hdrName : headers.keySet()) {
				request.addHeader(hdrName, headers.get(hdrName));
			}
		}
		
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		request.setContent(content);
		request.setUrl(url);
						
		HttpResponse response = null;
		try {
			//System.out.println("INVOKE ["+request.getUrl()+"]");
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		long esitoExpected = EsitiProperties.getInstance(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		verifyOk(response, 200, contentType);
						
		DBVerifier.verify(idTransazione, esitoExpected, null);
		
		return response;
		
	}
	
	public static void verifyOk(HttpResponse response, int code, String expectedContentType) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(expectedContentType, response.getContentType());
		
	}
}