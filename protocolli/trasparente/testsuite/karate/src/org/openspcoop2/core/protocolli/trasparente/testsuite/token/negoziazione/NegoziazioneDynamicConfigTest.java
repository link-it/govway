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

import java.util.Date;
import java.util.Properties;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;
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

	public static final String api_negoziazione_dynamic_config = "TestNegoziazioneTokenDynamicConfig";
		
	
	@Test
	public void test_clientApplication_property_full() throws Exception {
		testDynamicConfigFruizione("ApplicativoTestDynamicConfig1", 
				"PurposeIdApplicativoTestDynamicConfig1",
				"TESTApplicativoTestDynamicConfig1",
				"KIDCUSTOM",
				null);
	}
	
	@Test
	public void test_clientApplication_property_without_organization() throws Exception {
		testDynamicConfigFruizione("ApplicativoTestDynamicConfig2", 
				"PurposeIdApplicativoTestDynamicConfig2",
				"TESTApplicativoTestDynamicConfig2",
				"KIDCUSTOM",
				null);
	}
	
	@Test
	public void test_clientApplication_property_default() throws Exception {
		testDynamicConfigFruizione("ApplicativoTestDynamicConfig3", 
				"VALORE_PURPOSEID_DEFAULT",
				"VALORE_DEFAULT",
				"KIDCUSTOM",
				null);
	}
	
	@Test
	public void test_clientApplication_property_undefined() throws Exception {
		testDynamicConfigFruizione("ApplicativoTestDynamicConfig4", 
				null,
				null,
				null,
				"Proprieta' 'issuer.gwt' contiene un valore non corretto: Placeholder [{dynamicConfig:clientApplicationSearch(clientId)}] resolution failed: method [org.openspcoop2.pdd.core.dynamic.DynamicConfig.getClientApplicationSearch(clientId)] return null object");
	}
	
	
	private void testDynamicConfigFruizione(String username, 
			String expectedPurposeId, String expectedClientId, String expectedKID, 
			String errorMessage) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		boolean error =  errorMessage!=null;
		String diagnostico = null;
		if(error) {
			diagnostico = errorMessage;
		}
		
		// Test HDR
		
		HttpResponse response = _test(TipoServizio.FRUIZIONE, username, 
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
		
		_test(TipoServizio.FRUIZIONE, username, 
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
	
	
	
	
	@Test
	public void test_tokenClientApplication_property_full() throws Exception {
		testDynamicConfigErogazione("ApplicativoTokenTestDynamicConfig1", 
				"PurposeIdApplicativoTokenTestDynamicConfig1",
				"TESTApplicativoTokenTestDynamicConfig1",
				"KIDCUSTOM",
				null);
	}
	
	@Test
	public void test_tokenClientApplication_property_without_organization() throws Exception {
		testDynamicConfigErogazione("ApplicativoTokenTestDynamicConfig2", 
				"PurposeIdApplicativoTokenTestDynamicConfig2",
				"TESTApplicativoTokenTestDynamicConfig2",
				"KIDCUSTOM",
				null);
	}
	
	@Test
	public void test_tokenClientApplication_property_default() throws Exception {
		testDynamicConfigErogazione("ApplicativoTokenTestDynamicConfig3", 
				"VALORE_PURPOSEID_DEFAULT",
				"VALORE_DEFAULT",
				"KIDCUSTOM",
				null);
	}
	
	@Test
	public void test_tokenClientApplication_property_undefined() throws Exception {
		testDynamicConfigErogazione("ApplicativoTokenTestDynamicConfig4", 
				null,
				null,
				null,
				"Proprieta' 'issuer.gwt' contiene un valore non corretto: Placeholder [{dynamicConfig:tokenClientApplicationSearch(clientId)}] resolution failed: method [org.openspcoop2.pdd.core.dynamic.DynamicConfig.getTokenClientApplicationSearch(clientId)] return null object");
	}
	
	private void testDynamicConfigErogazione(String username, 
			String expectedPurposeId, String expectedClientId, String expectedKID, 
			String errorMessage) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		boolean error =  errorMessage!=null;
		String diagnostico = null;
		if(error) {
			diagnostico = errorMessage;
		}
		
		// Test HDR
		
		HttpResponse response = _test(TipoServizio.EROGAZIONE, username, 
				expectedPurposeId, expectedClientId, expectedKID,
				error,
				diagnostico,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-TokenDynamicConfig\"");
		String idRichiestaOriginale_0 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
				
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache
		
		_test(TipoServizio.EROGAZIONE, username, 
				expectedPurposeId, expectedClientId, expectedKID,
				error,
				diagnostico,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-TokenDynamicConfig\"",
				"\"transactionId\":\""+idRichiestaOriginale_0+"\""
				);
		
	}
	
	
	
	
	protected static HttpResponse _test(TipoServizio tipoServizio, String username,
			String expectedPurposeId, String expectedClientId, String expectedKID,
			boolean error, String diagnostico,
			String ... tokenInfoCheck) throws Exception {
		
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		
		String api = api_negoziazione_dynamic_config;
		String operazione = "signedJWT";
		
		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		
		if(tipoServizio == TipoServizio.EROGAZIONE) {
			String jwt = buildJWT(username);
			request.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+jwt);
		}
		else {
			request.setUsername(username);
			request.setPassword("123456");
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
			
			long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_NEGOZIAZIONE_TOKEN);
			verifyOk(response, 503, HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);
			
			if(tipoServizio == TipoServizio.EROGAZIONE) {
				DBVerifier.verify(idTransazione, esitoExpected, diagnostico,
						"\"type\":\"validated_token\"",
						"\"valid\":true");
			}
			else {
				DBVerifier.verify(idTransazione, esitoExpected, diagnostico);
			}
			
		}
		
		return response;
		
	}
	
	public static void verifyOk(HttpResponse response, int code, String expectedContentType) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(expectedContentType, response.getContentType());
		
	}
	
	
	private static String buildJson(String cliendId) throws Exception {
			
		String prefix = "TEST";		
		String audience = "testAudience";
		
		String issuer = "testAuthEnte";
		String subject = "10623542342342323";
		String jti = "33aa1676-1f9e-34e2-8515-0cfca111a188";
		Date now = DateManager.getDate();
		Date campione = new Date( (now.getTime()/1000)*1000);
		Date iat = new Date(campione.getTime());
		Date nbf = new Date(campione.getTime() - (1000*20));
		Date exp = new Date(campione.getTime() + (1000*60));
				
		String aud = "\""+prefix+"aud\":[\""+audience+"\"]";
		String jsonInput = 
				"{ "+aud+",";
		
		String clientId = "\""+prefix+"client_id\":\""+cliendId+"\"";
		jsonInput = jsonInput+
			clientId+" ,";

		String sub = "\""+prefix+"sub\":\""+subject+"\"";
		jsonInput = jsonInput+
				sub+" , ";

		String iss ="\""+prefix+"iss\":\""+issuer+"\"";
		jsonInput = jsonInput+
				iss+" , ";
		
		String iatJson = "\""+prefix+"iat\":"+(iat.getTime()/1000)+""; 
		jsonInput = jsonInput +
				iatJson + " , ";
		
		String nbfJson = "\""+prefix+"nbf\":"+(nbf.getTime()/1000)+"";
		jsonInput = jsonInput +
				nbfJson+ " , ";
		
		String expJson = "\""+prefix+"exp\":"+(exp.getTime()/1000)+"";
		jsonInput = jsonInput +
				expJson + " ,  ";
				
		String jtiS = "\""+prefix+"jti\":\""+jti+"\"";
		jsonInput = jsonInput +
				" "+jtiS+"}";
		
		//System.out.println("TOKEN ["+jsonInput+"]");
		
		return jsonInput;
	}
	private static String buildJWT(String clientId) throws Exception {
		
		String jsonInput = buildJson(clientId); 
		
		//System.out.println("TOKEN ["+jsonInput+"]");
		
		Properties props = new Properties();
		props.put("rs.security.keystore.type","JKS");
		String password = "openspcoop";
		props.put("rs.security.keystore.file", "/etc/govway/keys/erogatore.jks");
		props.put("rs.security.keystore.alias","erogatore");
		props.put("rs.security.keystore.password",password);
		props.put("rs.security.key.password",password);

		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
			
		props.put("rs.security.signature.algorithm","RS256");
		props.put("rs.security.signature.include.cert","false");
		props.put("rs.security.signature.include.key.id","true");
		props.put("rs.security.signature.include.public.key","false");
		props.put("rs.security.signature.include.cert.sha1","false");
		props.put("rs.security.signature.include.cert.sha256","false");
			
		JsonSignature jsonSignature = new JsonSignature(props, options);
		String token = jsonSignature.sign(jsonInput);
		//System.out.println(token);
			
		return token;		
		
	}
}
