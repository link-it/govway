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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_fallite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Vector;

import org.junit.Test;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.HeaderValues;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;

import net.minidev.json.JSONObject;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {
	
	final static int maxRequests = 5;
	final static int toFailRequests = 4;
	
	@Test
	public void conteggioCorrettoErogazione() throws Exception {
		// Testo che non vengano conteggiate le richieste fallite e quelle completate con successo

		final PolicyAlias policy = PolicyAlias.GIORNALIERO;
		String erogazione = "RichiesteFalliteRest";		
		int windowSize = Utils.getPolicyWindowSize(policy);
		String path = Utils.getPolicyPath(policy);
		
		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		Utils.waitForPolicy(policy);
		
		int firstBatch = maxRequests/2;
		int secondBatch = maxRequests - firstBatch;
		
		// Faccio il primo batch di richieste che devono essere conteggiate
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?returnCode=500");
						
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, firstBatch);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, firstBatch, 0);

		
		// Faccio un batch di richieste che non devono essere conteggiate
		
		HttpRequest okRequest = new HttpRequest();
		okRequest.setContentType("application/json");
		okRequest.setMethod(HttpRequestMethod.GET);
		okRequest.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path);
		
		Utils.makeParallelRequests(okRequest, maxRequests);
		
		HttpRequest failRequest = new HttpRequest();
		failRequest.setContentType("application/json");
		failRequest.setMethod(HttpRequestMethod.GET);
		failRequest.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?problem=true");
		
		Utils.makeParallelRequests(failRequest, maxRequests);
		
		// Faccio l'ultimo batch di richieste che devono essere conteggiate
		
		responses.addAll(Utils.makeParallelRequests(request, secondBatch));

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		Vector<HttpResponse>  failedResponses = Utils.makeParallelRequests(request, toFailRequests);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toFailRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);		
	}
	
	
	@Test
	public void conteggioCorrettoFruizione() throws Exception {
		// Testo che non vengano conteggiate le richieste fallite e quelle completate con successo

		final PolicyAlias policy = PolicyAlias.GIORNALIERO;
		String erogazione = "RichiesteFalliteRest";		
		int windowSize = Utils.getPolicyWindowSize(policy);
		String path = Utils.getPolicyPath(policy);
		
		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		Utils.waitForPolicy(policy);
		
		int firstBatch = maxRequests/2;
		int secondBatch = maxRequests - firstBatch;
		
		// Faccio il primo batch di richieste che devono essere conteggiate
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?returnCode=500");
						
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, firstBatch);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, firstBatch, 0);

		
		// Faccio un batch di richieste che non devono essere conteggiate
		
		HttpRequest okRequest = new HttpRequest();
		okRequest.setContentType("application/json");
		okRequest.setMethod(HttpRequestMethod.GET);
		okRequest.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path);
		
		Utils.makeParallelRequests(okRequest, maxRequests);
		
		HttpRequest failRequest = new HttpRequest();
		failRequest.setContentType("application/json");
		failRequest.setMethod(HttpRequestMethod.GET);
		failRequest.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?problem=true");
		
		Utils.makeParallelRequests(failRequest, maxRequests);
		
		// Faccio l'ultimo batch di richieste che devono essere conteggiate
		
		responses.addAll(Utils.makeParallelRequests(request, secondBatch));

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		Vector<HttpResponse>  failedResponses = Utils.makeParallelRequests(request, toFailRequests);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toFailRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);		
	}
	
	
	@Test
	public void perMinutoErogazione() throws Exception {
		testErogazione("RichiesteFalliteRest", PolicyAlias.MINUTO, null);
	}
	
	@Test
	public void perMinutoDefaultErogazione() throws Exception {
		perMinutoDefaultErogazione(null);
	}
	public void perMinutoDefaultErogazione(PolicyGroupByActiveThreadsType policyType) throws Exception {
		testErogazione("RichiesteFalliteRest", PolicyAlias.MINUTODEFAULT, policyType);
	}
	
	@Test
	public void orarioErogazione() throws Exception {
		testErogazione("RichiesteFalliteRest", PolicyAlias.ORARIO, null);
	}
	
	@Test
	public void giornalieroErogazione() throws Exception {
		testErogazione("RichiesteFalliteRest", PolicyAlias.GIORNALIERO, null);
	}
	
	@Test
	public void perMinutoErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteFalliteRest", PolicyAlias.MINUTO);
	}
	
	@Test
	public void orarioErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteFalliteRest", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteFalliteRest", PolicyAlias.GIORNALIERO);
	}
	
	@Test
	public void perMinutoFruizione() throws Exception {
		testFruizione("RichiesteFalliteRest", PolicyAlias.MINUTO, null);
	}
	

	@Test
	public void perMinutoDefaultFruizione() throws Exception {
		perMinutoDefaultFruizione(null);
	}
	public void perMinutoDefaultFruizione(PolicyGroupByActiveThreadsType policyType) throws Exception {
		testFruizione("RichiesteFalliteRest", PolicyAlias.MINUTODEFAULT, policyType);
	}
	
	@Test
	public void orarioFruizione() throws Exception {
		testFruizione("RichiesteFalliteRest", PolicyAlias.ORARIO, null);
	}
	
	@Test
	public void giornalieroFruizione() throws Exception {
		testFruizione("RichiesteFalliteRest", PolicyAlias.GIORNALIERO, null);
	}
	
	@Test
	public void perMinutoFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteFalliteRest", PolicyAlias.MINUTO);
	}
	
	@Test
	public void orarioFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteFalliteRest", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteFalliteRest", PolicyAlias.GIORNALIERO);
	}
	
	
	
	public void testErogazione(String erogazione, PolicyAlias policy, PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(policyType!=null && !policyType.isSupportedResource(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE)) {
			logRateLimiting.warn("Test numeroRichiesteFalliteErogazione con policy type '"+policyType+"' non effettuato poichè non supportato dal gestore");
			return;
		}
		
		try {
		
			int maxRequests = getMaxRequests(policy);
			int windowSize = Utils.getPolicyWindowSize(policy);
			String path = Utils.getPolicyPath(policy);
			
			dbUtils.setEngineTypeErogazione("SoggettoInternoTest", erogazione, policyType);
			
			long idErogazione = dbUtils.getIdErogazione("SoggettoInternoTest", erogazione);
			Utils.ripulisciRiferimentiCacheErogazione(idErogazione);
					
			HttpRequest request = new HttpRequest();
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?returnCode=500");
			
			// attivo nuovo motore
			Utils.makeParallelRequests(request, 1);
			
			Utils.waitForPolicy(policy);
			
			String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
			Utils.resetCounters(idPolicy);
			
			idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE);
					
			Vector<HttpResponse> responses = null;
			if(policyType!=null && (policyType.isHazelcastCounters() || policyType.isRedisCounters())) {
//				responses = new Vector<HttpResponse>();
//				 // altrimenti a volte non funziona per via del parallelismo e del controllo che avviene una volta processata la risposta
//				for (int i = 0; i < maxRequests; i++) {
//					Vector<HttpResponse> tmp = Utils.makeSequentialRequests(request, 1);
//					responses.addAll(tmp);
//					Utilities.sleep(1000);
//				}
				responses = Utils.makeSequentialRequests(request, maxRequests);
			}
			else {
				responses = Utils.makeParallelRequests(request, maxRequests);
			}
				
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE);
			
			Vector<HttpResponse>  failedResponses = null;
			if(policyType!=null && (policyType.isHazelcastCounters() || policyType.isRedisCounters())) {
//				failedResponses = new Vector<HttpResponse>();
//				 // altrimenti a volte non funziona per via del parallelismo e del controllo che avviene una volta processata la risposta
//				for (int i = 0; i < toFailRequests; i++) {
//					Vector<HttpResponse> tmp = Utils.makeSequentialRequests(request, 1);
//					failedResponses.addAll(tmp);
//					Utilities.sleep(1000);
//				}
				failedResponses = Utils.makeSequentialRequests(request, toFailRequests);
			}
			else {
				failedResponses = Utils.makeParallelRequests(request, toFailRequests);
			}
	
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toFailRequests, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE);
			
			if(org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.isJenkins() && policyType!=null && policyType.useNearCache()) {
				try {
					checkOkRequests(responses, windowSize, maxRequests, policyType);
				}catch(Throwable t) {
					System.out.println("WARNINIG JENKINS TEST FAILED testErogazione 'checkOkRequests' ("+policyType+"): "+t.getMessage());
				}
				try {
					checkFailedRequests(failedResponses, windowSize, maxRequests, policyType);
				}catch(Throwable t) {
					System.out.println("WARNINIG JENKINS TEST FAILED testErogazione 'checkFailedRequests' ("+policyType+"): "+t.getMessage());
				}
			}
			else {
				checkOkRequests(responses, windowSize, maxRequests, policyType);
				checkFailedRequests(failedResponses, windowSize, maxRequests, policyType);
			}
			
		}finally {
			
			// ripristino
			
			dbUtils.setEngineTypeErogazione("SoggettoInternoTest", erogazione, null);
			
			long idErogazione = dbUtils.getIdErogazione("SoggettoInternoTest", erogazione);
			Utils.ripulisciRiferimentiCacheErogazione(idErogazione);
			
		}
	}
	
	
	public void testFruizione(String erogazione, PolicyAlias policy, PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(policyType!=null && !policyType.isSupportedResource(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE)) {
			logRateLimiting.warn("Test numeroRichiesteFalliteFruizione con policy type '"+policyType+"' non effettuato poichè non supportato dal gestore");
			return;
		}
		
		try {
		
			int maxRequests = getMaxRequests(policy);
			int windowSize = Utils.getPolicyWindowSize(policy);
			String path = Utils.getPolicyPath(policy);
			
			dbUtils.setEngineTypeFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policyType);
			
			long idFruizione = dbUtils.getIdFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione);
			Utils.ripulisciRiferimentiCacheFruizione(idFruizione);
					
			HttpRequest request = new HttpRequest();
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?returnCode=500");
			
			// attivo nuovo motore
			Utils.makeParallelRequests(request, 1);
			
			Utils.waitForPolicy(policy);
			
			String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
			Utils.resetCounters(idPolicy);
			
			idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE);
					
			Vector<HttpResponse> responses = null;
			if(policyType!=null && (policyType.isHazelcastCounters() || policyType.isRedisCounters())) {
//				responses = new Vector<HttpResponse>();
//				 // altrimenti a volte non funziona per via del parallelismo e del controllo che avviene una volta processata la risposta
//				for (int i = 0; i < maxRequests; i++) {
//					Vector<HttpResponse> tmp = Utils.makeSequentialRequests(request, 1);
//					responses.addAll(tmp);
//					Utilities.sleep(1000);
//				}
				responses = Utils.makeSequentialRequests(request, maxRequests);
			}
			else {
				responses = Utils.makeParallelRequests(request, maxRequests);
			}
	
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE);
			
			Vector<HttpResponse>  failedResponses = null;
			if(policyType!=null && (policyType.isHazelcastCounters() || policyType.isRedisCounters())) {
//				failedResponses = new Vector<HttpResponse>();
//				 // altrimenti a volte non funziona per via del parallelismo e del controllo che avviene una volta processata la risposta
//				for (int i = 0; i < toFailRequests; i++) {
//					Vector<HttpResponse> tmp = Utils.makeSequentialRequests(request, 1);
//					failedResponses.addAll(tmp);
//					Utilities.sleep(1000);
//				}
				failedResponses = Utils.makeSequentialRequests(request, toFailRequests);
			}
			else {
				failedResponses = Utils.makeParallelRequests(request, toFailRequests);
			}
	
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toFailRequests, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE);
			
			if(org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.isJenkins() && policyType!=null && policyType.useNearCache()) {
				try {
					checkOkRequests(responses, windowSize, maxRequests, policyType);
				}catch(Throwable t) {
					System.out.println("WARNINIG JENKINS TEST FAILED testFruizione 'checkOkRequests' ("+policyType+"): "+t.getMessage());
				}
				try {
					checkFailedRequests(failedResponses, windowSize, maxRequests, policyType);
				}catch(Throwable t) {
					System.out.println("WARNINIG JENKINS TEST FAILED testFruizione 'checkFailedRequests' ("+policyType+"): "+t.getMessage());
				}
			}
			else {
				checkOkRequests(responses, windowSize, maxRequests, policyType);
				checkFailedRequests(failedResponses, windowSize, maxRequests, policyType);	
			}
			
		}finally {
			
			// ripristino
			
			dbUtils.setEngineTypeFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, null);
			
			long idFruizione = dbUtils.getIdFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione);
			Utils.ripulisciRiferimentiCacheFruizione(idFruizione);
			
		}
	}
	
	
	public void testErogazioneSequenziale(String erogazione, PolicyAlias policy) throws Exception {
		
		int windowSize = Utils.getPolicyWindowSize(policy);
		String path = Utils.getPolicyPath(policy);
		
		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		Utils.waitForPolicy(policy);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?returnCode=500");
						
		Vector<HttpResponse> responses = Utils.makeRequestsAndCheckPolicy(request, maxRequests, idPolicy);
		Utils.checkHeaderRemaining(responses, Headers.FailedRemaining, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		Vector<HttpResponse> failedResponses = Utils.makeParallelRequests(request, toFailRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toFailRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);
	}
	
	
	public void testFruizioneSequenziale(String erogazione, PolicyAlias policy) throws Exception {
		
		int windowSize = Utils.getPolicyWindowSize(policy);
		String path = Utils.getPolicyPath(policy);
		
		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		Utils.waitForPolicy(policy);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?returnCode=500");
						
		Vector<HttpResponse> responses = Utils.makeRequestsAndCheckPolicy(request, maxRequests, idPolicy);
		Utils.checkHeaderRemaining(responses, Headers.FailedRemaining, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		Vector<HttpResponse> failedResponses = Utils.makeParallelRequests(request, toFailRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toFailRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);
	}
	
	
	private void checkOkRequests(Vector<HttpResponse> responses, int windowSize, int maxRequests) {
		checkOkRequests(responses, windowSize, maxRequests, PolicyGroupByActiveThreadsType.LOCAL);
	}
	private void checkOkRequests(Vector<HttpResponse> responses, int windowSize, int maxRequests, PolicyGroupByActiveThreadsType policyType) {
		// Delle richieste ok Controllo lo header *-Limit, *-Reset e lo status code
		
		responses.forEach( r -> {
			
			Utils.checkXLimitHeader(logRateLimiting, Headers.FailedLimit, r.getHeaderFirstValue(Headers.FailedLimit), maxRequests);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
				Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.FailedLimit), maxRequests, windowMap);
			}
			
			assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.FailedReset)) <= windowSize);
			assertNotEquals(null, Integer.valueOf(r.getHeaderFirstValue(Headers.FailedRemaining)));
			assertEquals(500, r.getResultHTTPOperation());			
		});
	}
	
	private void checkFailedRequests(Vector<HttpResponse> responses, int windowSize, int maxRequests) throws Exception {
		checkFailedRequests(responses, windowSize, maxRequests, PolicyGroupByActiveThreadsType.LOCAL);
	}
	private void checkFailedRequests(Vector<HttpResponse> responses, int windowSize, int maxRequests, PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(policyType!=null && policyType.isInconsistent()) {
			// numero troppo casuali
			return;
		}
		
		JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
		
		for (var r: responses) {
			Utils.checkXLimitHeader(logRateLimiting, Headers.FailedLimit, r.getHeaderFirstValue(Headers.FailedLimit), maxRequests);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
				Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.FailedLimit), maxRequests, windowMap);
			}
			
			assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.FailedReset)) <= windowSize);
			assertEquals(429, r.getResultHTTPOperation());
			
			JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(r.getContent()));
			
			assertEquals("https://govway.org/handling-errors/429/LimitExceeded.html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
			assertEquals("LimitExceeded", jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
			assertEquals(429, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
			assertNotEquals(null, jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0));	
			assertEquals("Limit exceeded detected", jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0));
			
			assertEquals("0", r.getHeaderFirstValue(Headers.FailedRemaining));
			assertEquals(HeaderValues.LIMIT_EXCEEDED, r.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
			Utils.checkHeaderTooManyRequest(r);
			assertNotEquals(null, r.getHeaderFirstValue(Headers.RetryAfter));
		}	
	}
	
	private static int getMaxRequests(PolicyAlias policy) {
		if (policy == PolicyAlias.MINUTODEFAULT)
			return 3;
		else
			return 5;
	}

}
