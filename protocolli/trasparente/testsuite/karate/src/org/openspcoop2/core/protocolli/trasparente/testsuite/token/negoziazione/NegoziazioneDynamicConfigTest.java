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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.negoziazione;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* AuthorizationServerTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class NegoziazioneDynamicConfigTest extends ConfigLoader {

	public final static String api_negoziazione_dynamic_config = "TestNegoziazioneTokenDynamicConfig";
		
	
	@Test
	public void test_property_full() throws Exception {
		testDynamicConfig("ApplicativoTestDynamicConfig1", 
				"PurposeIdApplicativoTestDynamicConfig1",
				"TESTApplicativoTestDynamicConfig1",
				"KIDCUSTOM",
				null);
	}
	
	@Test
	public void test_property_without_organization() throws Exception {
		testDynamicConfig("ApplicativoTestDynamicConfig2", 
				"PurposeIdApplicativoTestDynamicConfig2",
				"TESTApplicativoTestDynamicConfig2",
				"KIDCUSTOM",
				null);
	}
	
	@Test
	public void test_property_default() throws Exception {
		testDynamicConfig("ApplicativoTestDynamicConfig3", 
				"VALORE_PURPOSEID_DEFAULT",
				"VALORE_DEFAULT",
				"KIDCUSTOM",
				null);
	}
	
	@Test
	public void test_property_undefined() throws Exception {
		testDynamicConfig("ApplicativoTestDynamicConfig4", 
				null,
				null,
				null,
				"Proprieta' 'issuer.gwt' contiene un valore non corretto: Placeholder [{dynamicConfig:clientApplicationSearch(clientId)}] resolution failed: method [org.openspcoop2.pdd.core.dynamic.DynamicConfig.getClientApplicationSearch(clientId)] return null object");
	}
	
	
	private void testDynamicConfig(String username, 
			String expectedPurposeId, String expectedClientId, String expectedKID, 
			String errorMessage) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		boolean error =  errorMessage!=null;
		String diagnostico = null;
		if(error) {
			diagnostico = errorMessage;
		}
		
		// Test HDR
		
		HttpResponse response = _test( username, 
				expectedPurposeId, expectedClientId, expectedKID,
				error,
				diagnostico,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-PurposeIdDynamic\"");
		String idRichiestaOriginale_0 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
				
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache
		
		_test( username, 
				expectedPurposeId, expectedClientId, expectedKID,
				error,
				diagnostico,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-PurposeIdDynamic\"",
				"\"transactionId\":\""+idRichiestaOriginale_0+"\""
				);
		
	}
	
	
	
	
	
	
	
	protected static HttpResponse _test(String username,
			String expectedPurposeId, String expectedClientId, String expectedKID,
			boolean error, String diagnostico,
			String ... tokenInfoCheck) throws Exception {
		
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		
		String api = api_negoziazione_dynamic_config;
		String operazione = "signedJWT";
		String url = System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		
		request.setUsername(username);
		request.setPassword("123456");
		
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
		
		if(!error) {
		
			long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
			verifyOk(response, 200, contentType);
						
			DBVerifier.verify(idTransazione, esitoExpected, null, tokenInfoCheck);
			
			String token = DBVerifier.readTokenInfo(idTransazione);
			
			//System.out.println("TOKEN '"+token+"'");
			
			String jwtSigned = JsonPathExpressionEngine.extractAndConvertResultAsString(token, "$..token", logCore);
			//System.out.println("jwtSigned '"+jwtSigned+"'");
			assertNotNull(jwtSigned);

			String [] split = jwtSigned.split("\\.");
			if(split.length!=3) {
				throw new Exception("Token JWT con lunghezza scorretta: "+split.length);
			}
			
			assertNotNull(split[0]);
			assertNotNull(split[1]);
			assertNotNull(split[2]);
			
			//System.out.println("TOKEN PAYLOAD '"+split[1]+"'");
			
			String decoded = new String( Base64Utilities.decode(split[1]) );
			
			//System.out.println("TOKEN PAYLOAD DECODED '"+decoded+"'");
			
			String purposeId = JsonPathExpressionEngine.extractAndConvertResultAsString(decoded, "$..purposeId", logCore);
			//System.out.println("PURPOSE ID '"+purposeId+"'");
			assertEquals("check purposeId per idTransazione '"+idTransazione+"'", expectedPurposeId, purposeId);
			
			String clientId = JsonPathExpressionEngine.extractAndConvertResultAsString(decoded, "$..client_id", logCore);
			//System.out.println("CLIENT ID '"+clientId+"'");
			assertEquals("check clientId per idTransazione '"+idTransazione+"'", expectedClientId, clientId);
			
			String sub = JsonPathExpressionEngine.extractAndConvertResultAsString(decoded, "$..sub", logCore);
			//System.out.println("SUB '"+sub+"'");
			assertEquals("check sub per idTransazione '"+idTransazione+"'", expectedClientId, sub);
			
			String iss = JsonPathExpressionEngine.extractAndConvertResultAsString(decoded, "$..iss", logCore);
			//System.out.println("ISS '"+iss+"'");
			assertEquals("check iss per idTransazione '"+idTransazione+"'", expectedClientId, iss);
			
			String decodedHdr = new String( Base64Utilities.decode(split[0]) );
			
			//System.out.println("TOKEN HEADER DECODED '"+decodedHdr+"'");
			
			String kid = JsonPathExpressionEngine.extractAndConvertResultAsString(decodedHdr, "$..kid", logCore);
			//System.out.println("KID '"+kid+"'");
			assertEquals("check kid per idTransazione '"+idTransazione+"'", expectedKID, kid);
			
		}
		else {
			
			long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE);
			verifyOk(response, 503, HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);
			
			DBVerifier.verify(idTransazione, esitoExpected, diagnostico);
			
		}
		
		return response;
		
	}
	
	public static void verifyOk(HttpResponse response, int code, String expectedContentType) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(expectedContentType, response.getContentType());
		
	}
}