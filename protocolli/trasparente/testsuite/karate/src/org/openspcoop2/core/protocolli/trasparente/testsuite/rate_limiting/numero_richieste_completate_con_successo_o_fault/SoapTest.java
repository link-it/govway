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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo_o_fault;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.List;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.HeaderValues;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.SoapBodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {
	
	final static int maxRequests = 5;
	final static int toBlockRequests = 4;
	
	
	@Test
	public void conteggioCorrettoErogazione() throws Exception {
		// Testo che non vengano conteggiate le richieste fallite e quelle completate con successo

		final PolicyAlias policy = PolicyAlias.GIORNALIERO;
		String erogazione = "RichiesteCompletateConSuccessoFaultSoap";		
		int windowSize = Utils.getPolicyWindowSize(policy);
		
		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		Utils.waitForPolicy(policy);
		
		int firstBatch = maxRequests/2;
		int secondBatch = maxRequests - firstBatch;
		
		// Faccio il primo batch di richieste che devono essere conteggiate
		String body = SoapBodies.get(policy);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");		
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1");
						
		List<HttpResponse> responses = Utils.makeParallelRequests(request, firstBatch);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, firstBatch, 0);
		
		// Faccio un batch di richieste che non devono essere conteggiate
		
		HttpRequest koRequest = new HttpRequest();
		koRequest.setContent(body.getBytes());
		koRequest.setContentType("application/soap+xml");
		koRequest.setMethod(HttpRequestMethod.POST);
		koRequest.addHeader(Headers.EchoInvoke, "Error");
		koRequest.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1?returnCode=500");
		
		Utils.makeParallelRequests(koRequest, maxRequests);
				
		// Faccio l'ultimo batch di richieste che devono essere conteggiate
		
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/?fault=true");
		List<HttpResponse> responsesFault = Utils.makeParallelRequests(request, secondBatch);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse>  failedResponses = Utils.makeParallelRequests(request, toBlockRequests);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFaultRequests(responsesFault, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);		
	}
	
	
	@Test
	public void conteggioCorrettoFruizione() throws Exception {
		// Testo che non vengano conteggiate le richieste fallite e quelle completate con successo

		final PolicyAlias policy = PolicyAlias.GIORNALIERO;
		String erogazione = "RichiesteCompletateConSuccessoFaultSoap";		
		int windowSize = Utils.getPolicyWindowSize(policy);
		
		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		Utils.waitForPolicy(policy);
		
		int firstBatch = maxRequests/2;
		int secondBatch = maxRequests - firstBatch;
		
		// Faccio il primo batch di richieste che devono essere conteggiate
		String body = SoapBodies.get(policy);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");		
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1?fault=true");
						
		List<HttpResponse> responsesFault = Utils.makeParallelRequests(request, firstBatch);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, firstBatch, 0);
		
		// Faccio un batch di richieste che non devono essere conteggiate
		
		HttpRequest koRequest = new HttpRequest();
		koRequest.setContent(body.getBytes());
		koRequest.setContentType("application/soap+xml");
		koRequest.setMethod(HttpRequestMethod.POST);
		koRequest.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/?redirect=true");
		
		Utils.makeParallelRequests(koRequest, maxRequests);
		
		// Faccio l'ultimo batch di richieste che devono essere conteggiate
		
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1");
		List<HttpResponse> responses = Utils.makeParallelRequests(request, secondBatch);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse>  failedResponses = Utils.makeParallelRequests(request, toBlockRequests);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
		checkFaultRequests(responsesFault, windowSize, maxRequests);
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);		
	}
	
	
	@Test
	public void perMinutoErogazione() throws Exception {
		testErogazione("RichiesteCompletateConSuccessoFaultSoap", PolicyAlias.MINUTO);
	}
	
	@Test
	public void perMinutoDefaultErogazione() throws Exception {
		testErogazione("RichiesteCompletateConSuccessoFaultSoap", PolicyAlias.MINUTODEFAULT);
	}
	
	@Test
	public void orarioErogazione() throws Exception {
		testErogazione("RichiesteCompletateConSuccessoFaultSoap", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroErogazione() throws Exception {
		testErogazione("RichiesteCompletateConSuccessoFaultSoap", PolicyAlias.GIORNALIERO);
	}
	
	@Test
	public void perMinutoErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteCompletateConSuccessoFaultSoap", PolicyAlias.MINUTO);
	}
	
	@Test
	public void orarioErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteCompletateConSuccessoFaultSoap", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteCompletateConSuccessoFaultSoap", PolicyAlias.GIORNALIERO);
	}
	
	@Test
	public void perMinutoFruizione() throws Exception {
		testFruizione("RichiesteCompletateConSuccessoFaultSoap", PolicyAlias.MINUTO);
	}
	
	@Test
	public void perMinutoDefaultFruizione() throws Exception {
		testFruizione("RichiesteCompletateConSuccessoFaultSoap", PolicyAlias.MINUTODEFAULT);
	}
	
	@Test
	public void orarioFruizione() throws Exception {
		testFruizione("RichiesteCompletateConSuccessoFaultSoap", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroFruizione() throws Exception {
		testFruizione("RichiesteCompletateConSuccessoFaultSoap", PolicyAlias.GIORNALIERO);
	}
	
	@Test
	public void perMinutoFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteCompletateConSuccessoFaultSoap", PolicyAlias.MINUTO);
	}
	
	@Test
	public void orarioFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteCompletateConSuccessoFaultSoap", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteCompletateConSuccessoFaultSoap", PolicyAlias.GIORNALIERO);
	}
	
	
	public static void testErogazione(String erogazione, PolicyAlias policy) {
		
		int maxRequests = getMaxRequests(policy);
		int windowSize = Utils.getPolicyWindowSize(policy);
		
		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		Utils.waitForPolicy(policy);
		
		String body = SoapBodies.get(policy);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");		
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1");
		
		HttpRequest faultRequest = new HttpRequest();
		faultRequest.setContentType("application/soap+xml");		
		faultRequest.setMethod(HttpRequestMethod.POST);
		faultRequest.setContent(body.getBytes());
		faultRequest.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1?fault=true");

		int toOk = maxRequests/2;
		int toFault = maxRequests-toOk;
		
		List<HttpResponse> responses = Utils.makeParallelRequests(request, toOk);
		List<HttpResponse> faultResponses = Utils.makeParallelRequests(faultRequest, toFault);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse>  failedResponses = Utils.makeParallelRequests(request, toBlockRequests);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFaultRequests(faultResponses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);		
	}
	
	
	public static void testFruizione(String erogazione, PolicyAlias policy) {
		
		int maxRequests = getMaxRequests(policy);
		int windowSize = Utils.getPolicyWindowSize(policy);
		
		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		Utils.waitForPolicy(policy);
		
		String body = SoapBodies.get(policy);
		
		HttpRequest faultRequest = new HttpRequest();
		faultRequest.setContentType("application/soap+xml");		
		faultRequest.setMethod(HttpRequestMethod.POST);
		faultRequest.setContent(body.getBytes());
		faultRequest.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1?fault=true");
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");		
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1");
						
		int toOk = maxRequests/2;
		int toFault = maxRequests-toOk;
		
		List<HttpResponse> faultResponses = Utils.makeParallelRequests(faultRequest, toFault);
		List<HttpResponse> responses = Utils.makeParallelRequests(request, toOk);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse>  failedResponses = Utils.makeParallelRequests(request, toBlockRequests);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
		checkFaultRequests(faultResponses, windowSize, maxRequests);
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);		
	}
	
	
	public void testErogazioneSequenziale(String erogazione, PolicyAlias policy) throws Exception {
		
		int windowSize = Utils.getPolicyWindowSize(policy);
		
		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		Utils.waitForPolicy(policy);
		
		String body = SoapBodies.get(policy);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");		
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1");
						
		List<HttpResponse> responses = Utils.makeRequestsAndCheckPolicy(request, maxRequests, idPolicy);
		Utils.checkHeaderRemaining(responses, Headers.RequestSuccesfulOrFaultRemaining, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse> failedResponses = Utils.makeParallelRequests(request, toBlockRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);
	}
	
	public void testFruizioneSequenziale(String erogazione, PolicyAlias policy) throws Exception {
		
		int windowSize = Utils.getPolicyWindowSize(policy);
		
		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		Utils.waitForPolicy(policy);
		
		String body = SoapBodies.get(policy);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");		
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1?fault=true");
						
		List<HttpResponse> responses = Utils.makeRequestsAndCheckPolicy(request, maxRequests, idPolicy);
		Utils.checkHeaderRemaining(responses, Headers.RequestSuccesfulOrFaultRemaining, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse> failedResponses = Utils.makeParallelRequests(request, toBlockRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
		checkFaultRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);
	}
	
	
	public static void checkFailedRequests(List<HttpResponse> responses, int windowSize, int maxRequests) {
		
		for (var r: responses) {
			Utils.checkXLimitHeader(logRateLimiting, Headers.RequestSuccesfulOrFaultLimit,r.getHeaderFirstValue(Headers.RequestSuccesfulOrFaultLimit), maxRequests);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
				Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.RequestSuccesfulOrFaultLimit), maxRequests, windowMap);
			}
			assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.RequestSuccesfulOrFaultReset)) <= windowSize);			
			assertEquals(429, r.getResultHTTPOperation());
			
			//org.w3c.dom.Element element = Utils.buildXmlElement(r.getContent());
			Utils.matchLimitExceededSoap(r.getContent());
			
			assertEquals("0", r.getHeaderFirstValue(Headers.RequestSuccesfulOrFaultRemaining));
			assertEquals(HeaderValues.LIMIT_EXCEEDED, r.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
			Utils.checkHeaderTooManyRequest(r);
			assertNotEquals(null, r.getHeaderFirstValue(Headers.RetryAfter));
		}
		
	}


	public static void checkOkRequests(List<HttpResponse> responses, int windowSize, int maxRequests) {
	
		// Per ogni richiesta controllo gli headers e anche che il body
		// sia effettivamente un fault.
		for (var r: responses){
			
			Utils.checkXLimitHeader(logRateLimiting, Headers.RequestSuccesfulOrFaultLimit, r.getHeaderFirstValue(Headers.RequestSuccesfulOrFaultLimit), maxRequests);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
				Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.RequestSuccesfulOrFaultLimit), maxRequests, windowMap);
			}
			
			assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.RequestSuccesfulOrFaultReset)) <= windowSize);
			
			assertEquals(200, r.getResultHTTPOperation());

			assertNotEquals(null, Integer.valueOf(r.getHeaderFirstValue(Headers.RequestSuccesfulOrFaultRemaining)));
		}
	}
	
	public static void checkFaultRequests(List<HttpResponse> responses, int windowSize, int maxRequests) {
		
		// Per ogni richiesta controllo gli headers e anche che il body
		// sia effettivamente un fault.
		for (var r: responses){
			
			Utils.checkXLimitHeader(logRateLimiting, Headers.RequestSuccesfulOrFaultLimit, r.getHeaderFirstValue(Headers.RequestSuccesfulOrFaultLimit), maxRequests);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
				Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.RequestSuccesfulOrFaultLimit), maxRequests, windowMap);
			}
			
			assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.RequestSuccesfulOrFaultReset)) <= windowSize);
			
			assertEquals(500, r.getResultHTTPOperation());

			assertNotEquals(null, Integer.valueOf(r.getHeaderFirstValue(Headers.RequestSuccesfulOrFaultRemaining)));
		}
	}
	
	private static int getMaxRequests(PolicyAlias policy) {
		if (policy == PolicyAlias.MINUTODEFAULT)
			return 10;
		else
			return 5;
	}
}