/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_fallite;

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
import org.openspcoop2.pdd.core.dynamic.DynamicException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.w3c.dom.Element;

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
	final static int totalRequests = maxRequests + toBlockRequests;
	
	@Test
	public void conteggioCorrettoErogazione() throws Exception {
		// Testo che non vengano conteggiate le richieste fallite e quelle completate con successo

		final PolicyAlias policy = PolicyAlias.GIORNALIERO;
		String erogazione = "RichiesteFalliteSoap";		
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
						
		List<HttpResponse> responses = Utils.makeParallelRequests(request, firstBatch);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, firstBatch, 0);
		
		// Faccio un batch di richieste che non devono essere conteggiate
		
		HttpRequest okRequest = new HttpRequest();
		okRequest.setContent(body.getBytes());
		okRequest.setContentType("application/soap+xml");
		okRequest.setMethod(HttpRequestMethod.POST);
		okRequest.addHeader(Headers.EchoInvoke, "OkOrFault");
		okRequest.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1");
		
		Utils.makeParallelRequests(okRequest, maxRequests);
		
		HttpRequest failRequest = new HttpRequest();
		failRequest.setContent(body.getBytes());
		failRequest.setContentType("application/soap+xml");
		failRequest.setMethod(HttpRequestMethod.POST);
		failRequest.addHeader(Headers.EchoInvoke, "OkOrFault");
		failRequest.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1?fault=true");
		
		Utils.makeParallelRequests(failRequest, maxRequests);
		
		// Faccio l'ultimo batch di richieste che devono essere conteggiate
		
		responses.addAll(Utils.makeParallelRequests(request, secondBatch));

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse>  failedResponses = Utils.makeParallelRequests(request, toBlockRequests);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);		
	}
	
	
	@Test
	public void conteggioCorrettoFruizione() throws Exception {
		// Testo che non vengano conteggiate le richieste fallite e quelle completate con successo

		final PolicyAlias policy = PolicyAlias.GIORNALIERO;
		String erogazione = "RichiesteFalliteSoap";		
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
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1?returnCode=500");
						
		List<HttpResponse> responses = Utils.makeParallelRequests(request, firstBatch);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, firstBatch, 0);
		
		// Faccio un batch di richieste che non devono essere conteggiate
		
		HttpRequest okRequest = new HttpRequest();
		okRequest.setContent(body.getBytes());
		okRequest.setContentType("application/soap+xml");
		okRequest.setMethod(HttpRequestMethod.POST);
		okRequest.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/?fault=true");
		
		Utils.makeParallelRequests(okRequest, maxRequests);
		
		/*
			 Sulle fruizioni non avendo i connettori condizionali non riesco a farmi rispondere 200 perchè il connettore va su /ping, che non
			 restituisce nessun body.
		failRequest.setContent(body.getBytes());
		failRequest.setContentType("application/soap+xml");
		failRequest.setMethod(HttpRequestMethod.POST);
		failRequest.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/");
		
		
		Utils.makeParallelRequests(failRequest, maxRequests);
		*/
		
		// Faccio l'ultimo batch di richieste che devono essere conteggiate
		
		responses.addAll(Utils.makeParallelRequests(request, secondBatch));

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse>  failedResponses = Utils.makeParallelRequests(request, toBlockRequests);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);		
	}
	
	@Test
	public void perMinutoErogazione() throws Exception {
		testErogazione("RichiesteFalliteSoap", PolicyAlias.MINUTO);
	}
	
	@Test
	public void perMinutoDefaultErogazione() throws Exception {
		testErogazione("RichiesteFalliteSoap", PolicyAlias.MINUTODEFAULT);
	}
	
	
	@Test
	public void orarioErogazione() throws Exception {
		testErogazione("RichiesteFalliteSoap", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroErogazione() throws Exception {
		testErogazione("RichiesteFalliteSoap", PolicyAlias.GIORNALIERO);
	}
	
	@Test
	public void perMinutoErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteFalliteSoap", PolicyAlias.MINUTO);
	}
	
	@Test
	public void orarioErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteFalliteSoap", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteFalliteSoap", PolicyAlias.GIORNALIERO);
	}
	
	@Test
	public void perMinutoFruizione() throws Exception {
		testFruizione("RichiesteFalliteSoap", PolicyAlias.MINUTO);
	}
	
	@Test
	public void perMinutoDefaultFruizione() throws Exception {
		testFruizione("RichiesteFalliteSoap", PolicyAlias.MINUTO);
	}
	
	@Test
	public void orarioFruizione() throws Exception {
		testFruizione("RichiesteFalliteSoap", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroFruizione() throws Exception {
		testFruizione("RichiesteFalliteSoap", PolicyAlias.GIORNALIERO);
	}
	
	@Test
	public void perMinutoFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteFalliteSoap", PolicyAlias.MINUTO);
	}
	
	@Test
	public void orarioFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteFalliteSoap", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteFalliteSoap", PolicyAlias.GIORNALIERO);
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
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");		
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1?returnCode=500");
						
		List<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse>  failedResponses = Utils.makeParallelRequests(request, toBlockRequests);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
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
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1?returnCode=500");
						
		List<HttpResponse> responses = Utils.makeRequestsAndCheckPolicy(request, maxRequests, idPolicy);
		Utils.checkHeaderRemaining(responses, Headers.FailedRemaining, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse> failedResponses = Utils.makeParallelRequests(request, toBlockRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);
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
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1?returnCode=500");
						
		List<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse>  failedResponses = Utils.makeParallelRequests(request, toBlockRequests);

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
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1?returnCode=500");
						
		List<HttpResponse> responses = Utils.makeRequestsAndCheckPolicy(request, maxRequests, idPolicy);
		Utils.checkHeaderRemaining(responses, Headers.FailedRemaining, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse> failedResponses = Utils.makeParallelRequests(request, toBlockRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toBlockRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);
	}
	
	
	private void checkFailedRequests(List<HttpResponse> responses, int windowSize, int maxRequests) throws Exception {
		
		for (var r: responses) {
			Utils.checkXLimitHeader(logRateLimiting, Headers.FailedLimit, r.getHeaderFirstValue(Headers.FailedLimit), maxRequests);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
				Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.FailedLimit), maxRequests, windowMap);
			}
			assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.FailedReset)) <= windowSize);			
			assertEquals(429, r.getResultHTTPOperation());
			
			//Element element = Utils.buildXmlElement(r.getContent());
			Utils.matchLimitExceededSoap(r.getContent());
			
			assertEquals("0", r.getHeaderFirstValue(Headers.FailedRemaining));
			assertEquals(HeaderValues.LIMIT_EXCEEDED, r.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
			Utils.checkHeaderTooManyRequest(r);
			assertNotEquals(null, r.getHeaderFirstValue(Headers.RetryAfter));
		}
		
	}


	private void checkOkRequests(List<HttpResponse> responses, int windowSize, int maxRequests) throws DynamicException {
	
		// Per ogni richiesta controllo gli headers e anche che il body
		// sia effettivamente un fault.
		for (var r: responses){
			
			String idTransazione = r.getHeaderFirstValue(Headers.TransactionId);
			String content = null;
			if(r.getContent()!=null) {
				content = new String(r.getContent());
			}
			logRateLimiting.debug("Verifico risposta della transazione '"+idTransazione+"' ("+content+") ...");
			
			Utils.checkXLimitHeader(logRateLimiting, Headers.FailedLimit, r.getHeaderFirstValue(Headers.FailedLimit), maxRequests);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
				Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.FailedLimit), maxRequests, windowMap);
			}
			
			assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.FailedReset)) <= windowSize);
			assertNotEquals(null, Integer.valueOf(r.getHeaderFirstValue(Headers.FailedRemaining)));
			assertEquals(500, r.getResultHTTPOperation());
			
			Element element = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.buildXmlElement(r.getContent());
			Utils.matchApiUnavaliableSoap(idTransazione, element);
			
			logRateLimiting.debug("Verifico dati della transazione '"+idTransazione+"' ok");
		}
		
	}
	
	private static int getMaxRequests(PolicyAlias policy) {
		if (policy == PolicyAlias.MINUTODEFAULT)
			return 3;
		else
			return 5;
	}

}
