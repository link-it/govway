/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_fallite_o_fault;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Vector;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.HeaderValues;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.SoapBodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.openspcoop2.pdd.core.dynamic.DynamicException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.w3c.dom.Element;

public class SoapTest extends ConfigLoader {
	
	final static int maxRequests = 5;
	final static int toBlockRequests = 4;
	
	@Test
	public void conteggioCorrettoErogazione() throws Exception {
		// Testo che non vengano conteggiate le richieste fallite e quelle completate con successo

		final PolicyAlias policy = PolicyAlias.GIORNALIERO;
		String erogazione = "RichiesteFalliteFaultSoap";		
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
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1?returnCode=500");
						
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, firstBatch);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, firstBatch, 0);
		
		// Faccio un batch di richieste che non devono essere conteggiate
		
		HttpRequest okRequest = new HttpRequest();
		okRequest.setContent(body.getBytes());
		okRequest.setContentType("application/soap+xml");
		okRequest.setMethod(HttpRequestMethod.POST);
		okRequest.addHeader(Headers.EchoInvoke, "Ok");
		okRequest.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1");
		
		Utils.makeParallelRequests(okRequest, maxRequests);
		
		// Faccio l'ultimo batch di richieste che devono essere conteggiate
		
		responses.addAll(Utils.makeParallelRequests(request, secondBatch));

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		Vector<HttpResponse>  failedResponses = Utils.makeParallelRequests(request, toBlockRequests);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);		
	}
	
	
	/* Questo non può essere fatto perchè le richieste da non conteggiare sono le richieste OK
	 * che non posso fare avvenire perchè ho bisogno di contattare TestService/echo, mentre sul
	 * connettore è definito TestService/ping
	 * @Test
	public void conteggioCorrettoFruizione() throws Exception {
		
	}*/
	
	
	@Test
	public void perMinutoErogazione() throws Exception {
		testErogazione("RichiesteFalliteFaultSoap", PolicyAlias.MINUTO);
	}
	
	@Test
	public void perMinutoDefaultErogazione() throws Exception {
		testErogazione("RichiesteFalliteFaultSoap", PolicyAlias.MINUTODEFAULT);
	}
	
	@Test
	public void orarioErogazione() throws Exception {
		testErogazione("RichiesteFalliteFaultSoap", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroErogazione() throws Exception {
		testErogazione("RichiesteFalliteFaultSoap", PolicyAlias.GIORNALIERO);
	}
	
	@Test
	public void perMinutoErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteFalliteFaultSoap", PolicyAlias.MINUTO);
	}
	
	@Test
	public void orarioErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteFalliteFaultSoap", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteFalliteFaultSoap", PolicyAlias.GIORNALIERO);
	}
	
	@Test
	public void perMinutoFruizione() throws Exception {
		testFruizione("RichiesteFalliteFaultSoap", PolicyAlias.MINUTO);
	}
	
	@Test
	public void perMinutoDefaultFruizione() throws Exception {
		testFruizione("RichiesteFalliteFaultSoap", PolicyAlias.MINUTODEFAULT);
	}
	
	@Test
	public void orarioFruizione() throws Exception {
		testFruizione("RichiesteFalliteFaultSoap", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroFruizione() throws Exception {
		testFruizione("RichiesteFalliteFaultSoap", PolicyAlias.GIORNALIERO);
	}
	
	@Test
	public void perMinutoFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteFalliteFaultSoap", PolicyAlias.MINUTO);
	}
	
	@Test
	public void orarioFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteFalliteFaultSoap", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteFalliteFaultSoap", PolicyAlias.GIORNALIERO);
	}
	
	
	public void testErogazione(String erogazione, PolicyAlias policy) throws Exception {
		
		int maxRequests = getMaxRequests(policy);
		int windowSize = Utils.getPolicyWindowSize(policy);
		
		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		Utils.waitForPolicy(policy);
		
		String body = SoapBodies.get(policy);
		
		// Faccio un batch di fallite e un batch di fault.
		int first_batch = maxRequests/2;
		int last_batch = maxRequests-first_batch;
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");		
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1?returnCode=500");
						
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, first_batch);
		
		for (var r : responses) {
			assertEquals(500, r.getResultHTTPOperation());
			
			Element element = Utils.buildXmlElement(r.getContent());
			Utils.matchApiUnavaliableSoap(element);			
		}
		
		request.setContentType("application/soap+xml");		
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1?fault=true");
		
		Vector<HttpResponse> faultedResponses = Utils.makeParallelRequests(request, last_batch);
		responses.addAll(faultedResponses);
		
		for (var r : faultedResponses) {
			assertEquals(500, r.getResultHTTPOperation());
			
			Element element = Utils.buildXmlElement(r.getContent());
			Utils.matchEchoFaultResponseSoap(element);			
		}

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		Vector<HttpResponse>  blockedResponses = Utils.makeParallelRequests(request, toBlockRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(blockedResponses, windowSize, maxRequests);		
	}
	
	
	public void testFruizione(String erogazione, PolicyAlias policy) throws Exception {
		
		int maxRequests = getMaxRequests(policy);
		int windowSize = Utils.getPolicyWindowSize(policy);
		
		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		Utils.waitForPolicy(policy);
		
		String body = SoapBodies.get(policy);
		
		// Faccio un batch di fallite e un batch di fault.
		int first_batch = maxRequests/2;
		int last_batch = maxRequests-first_batch;
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");		
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1?returnCode=500");
						
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, first_batch);
		
		for (var r : responses) {
			assertEquals(500, r.getResultHTTPOperation());
			
			Element element = Utils.buildXmlElement(r.getContent());
			Utils.matchApiUnavaliableSoap(element);			
		}
		
		request.setContentType("application/soap+xml");		
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1?fault=true");
		
		Vector<HttpResponse> faultedResponses = Utils.makeParallelRequests(request, last_batch);
		responses.addAll(faultedResponses);
		
		for (var r : faultedResponses) {
			assertEquals(500, r.getResultHTTPOperation());
			
			Element element = Utils.buildXmlElement(r.getContent());
			Utils.matchEchoFaultResponseSoap(element);			
		}

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		Vector<HttpResponse>  blockedResponses = Utils.makeParallelRequests(request, toBlockRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(blockedResponses, windowSize, maxRequests);		
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
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1?fault=true");
						
		Vector<HttpResponse> responses = Utils.makeRequestsAndCheckPolicy(request, maxRequests, idPolicy);
		Utils.checkHeaderRemaining(responses, Headers.FailedOrFaultRemaining, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		Vector<HttpResponse> failedResponses = Utils.makeParallelRequests(request, toBlockRequests);
		
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
						
		Vector<HttpResponse> responses = Utils.makeRequestsAndCheckPolicy(request, maxRequests, idPolicy);
		Utils.checkHeaderRemaining(responses, Headers.FailedOrFaultRemaining, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		Vector<HttpResponse> failedResponses = Utils.makeParallelRequests(request, toBlockRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);
	}
	
	
	
	private void checkFailedRequests(Vector<HttpResponse> responses, int windowSize, int maxRequests) throws Exception {
		
		for (var r: responses) {
			Utils.checkXLimitHeader(logRateLimiting, Headers.FailedOrFaultLimit, r.getHeader(Headers.FailedOrFaultLimit), maxRequests);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
				Utils.checkXLimitWindows(r.getHeader(Headers.FailedOrFaultLimit), maxRequests, windowMap);
			}
			assertTrue(Integer.valueOf(r.getHeader(Headers.FailedOrFaultReset)) <= windowSize);			
			assertEquals(429, r.getResultHTTPOperation());
			
			Element element = Utils.buildXmlElement(r.getContent());
			Utils.matchLimitExceededSoap(element);
			
			assertEquals("0", r.getHeader(Headers.FailedOrFaultRemaining));
			assertEquals(HeaderValues.LimitExceeded, r.getHeader(Headers.GovWayTransactionErrorType));
			assertEquals(HeaderValues.ReturnCodeTooManyRequests, r.getHeader(Headers.ReturnCode));
			assertNotEquals(null, r.getHeader(Headers.RetryAfter));
		}
		
	}


	private void checkOkRequests(Vector<HttpResponse> responses, int windowSize, int maxRequests) throws DynamicException {
	
		// Per ogni richiesta controllo gli headers e anche che il body
		// sia effettivamente un fault.
		for (var r: responses){
			
			Utils.checkXLimitHeader(logRateLimiting, Headers.FailedOrFaultLimit, r.getHeader(Headers.FailedOrFaultLimit), maxRequests);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
				Utils.checkXLimitWindows(r.getHeader(Headers.FailedOrFaultLimit), maxRequests, windowMap);
			}
			assertTrue(Integer.valueOf(r.getHeader(Headers.FailedOrFaultReset)) <= windowSize);					
			assertNotEquals(null, Integer.valueOf(r.getHeader(Headers.FailedOrFaultRemaining)));
		}
		
	}
	
	private static int getMaxRequests(PolicyAlias policy) {
		if (policy == PolicyAlias.MINUTODEFAULT)
			return 3;
		else
			return 5;
	}
}