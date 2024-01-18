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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_fallite_o_fault;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.List;

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
		String erogazione = "RichiesteFalliteFaultRest";		
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
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?problem=true");
						
		List<HttpResponse> responses = Utils.makeParallelRequests(request, firstBatch);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, firstBatch, 0);

		
		// Faccio un batch di richieste che non devono essere conteggiate
		
		HttpRequest okRequest = new HttpRequest();
		okRequest.setContentType("application/json");
		okRequest.setMethod(HttpRequestMethod.GET);
		okRequest.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path);
		
		Utils.makeParallelRequests(okRequest, maxRequests);
	
		
		// Faccio l'ultimo batch di richieste che devono essere conteggiate
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?returnCode=500");
		
		responses.addAll(Utils.makeParallelRequests(request, secondBatch));

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse>  failedResponses = Utils.makeParallelRequests(request, toFailRequests);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toFailRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);		
	}
	
	
	@Test
	public void conteggioCorrettoFruizione() throws Exception {
		// Testo che non vengano conteggiate le richieste fallite e quelle completate con successo

		final PolicyAlias policy = PolicyAlias.GIORNALIERO;
		String erogazione = "RichiesteFalliteFaultRest";		
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
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?problem=true");
						
		List<HttpResponse> responses = Utils.makeParallelRequests(request, firstBatch);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, firstBatch, 0);

		
		// Faccio un batch di richieste che non devono essere conteggiate
		
		HttpRequest okRequest = new HttpRequest();
		okRequest.setContentType("application/json");
		okRequest.setMethod(HttpRequestMethod.GET);
		okRequest.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path);
		
		Utils.makeParallelRequests(okRequest, maxRequests);
	
		
		// Faccio l'ultimo batch di richieste che devono essere conteggiate
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?returnCode=500");
		
		responses.addAll(Utils.makeParallelRequests(request, secondBatch));

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse>  failedResponses = Utils.makeParallelRequests(request, toFailRequests);

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toFailRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);		
	}
	
	
	@Test
	public void perMinutoErogazione() throws Exception {
		testErogazione("RichiesteFalliteFaultRest", PolicyAlias.MINUTO, null);
	}
	
	@Test
	public void perMinutoDefaultErogazione() throws Exception {
		perMinutoDefaultErogazione(null);
	}
	public void perMinutoDefaultErogazione(PolicyGroupByActiveThreadsType policyType) throws Exception {
		testErogazione("RichiesteFalliteFaultRest", PolicyAlias.MINUTODEFAULT, policyType);
	}
	
	@Test
	public void orarioErogazione() throws Exception {
		testErogazione("RichiesteFalliteFaultRest", PolicyAlias.ORARIO, null);
	}
	
	@Test
	public void giornalieroErogazione() throws Exception {
		testErogazione("RichiesteFalliteFaultRest", PolicyAlias.GIORNALIERO, null);
	}
	
	@Test
	public void perMinutoErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteFalliteFaultRest", PolicyAlias.MINUTO);
	}
	
	@Test
	public void orarioErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteFalliteFaultRest", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroErogazioneSequenziali() throws Exception {
		testErogazioneSequenziale("RichiesteFalliteFaultRest", PolicyAlias.GIORNALIERO);
	}

	
	@Test
	public void perMinutoFruizione() throws Exception {
		testFruizione("RichiesteFalliteFaultRest", PolicyAlias.MINUTO, null);
	}
	
	@Test
	public void perMinutoDefaultFruizione() throws Exception {
		perMinutoDefaultFruizione(null);
	}
	public void perMinutoDefaultFruizione(PolicyGroupByActiveThreadsType policyType) throws Exception {
		testFruizione("RichiesteFalliteFaultRest", PolicyAlias.MINUTODEFAULT, policyType);
	}
	
	@Test
	public void orarioFruizione() throws Exception {
		testFruizione("RichiesteFalliteFaultRest", PolicyAlias.ORARIO, null);
	}
	
	@Test
	public void giornalieroFruizione() throws Exception {
		testFruizione("RichiesteFalliteFaultRest", PolicyAlias.GIORNALIERO, null);
	}
	
	@Test
	public void perMinutoFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteFalliteFaultRest", PolicyAlias.MINUTO);
	}
	
	@Test
	public void orarioFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteFalliteFaultRest", PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroFruizioneSequenziali() throws Exception {
		testFruizioneSequenziale("RichiesteFalliteFaultRest", PolicyAlias.GIORNALIERO);
	}
	
	public void testErogazione(String erogazione, PolicyAlias policy, PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(policyType!=null && !policyType.isSupportedResource(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI)) {
			logRateLimiting.warn("Test numeroRichiesteFalliteOFaultErogazione con policy type '"+policyType+"' non effettuato poichè non supportato dal gestore");
			return;
		}
		
		try {
		
			// Facciamo un batch di richieste fallite e un batch di richieste con fault,
			// che devono essere tutte conteggiate
			int maxRequests = getMaxRequests(policy);
			int windowSize = Utils.getPolicyWindowSize(policy);
			String path = Utils.getPolicyPath(policy);
			
			dbUtils.setEngineTypeErogazione("SoggettoInternoTest", erogazione, policyType);
			
			long idErogazione = dbUtils.getIdErogazione("SoggettoInternoTest", erogazione);
			Utils.ripulisciRiferimentiCacheErogazione(idErogazione);
					
			HttpRequest toFailRequest = new HttpRequest();
			toFailRequest.setContentType("application/json");
			toFailRequest.setMethod(HttpRequestMethod.GET);
			toFailRequest.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?returnCode=500");
			
			HttpRequest toFaultRequest = new HttpRequest();
			toFaultRequest.setContentType("application/json");
			toFaultRequest.setMethod(HttpRequestMethod.GET);
			toFaultRequest.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?problem=true");
			
			// attivo nuovo motore
			Utils.makeParallelRequests(toFailRequest, 1);
			Utils.makeParallelRequests(toFaultRequest, 1);
			
			Utils.waitForPolicy(policy);
			
			String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
			Utils.resetCounters(idPolicy);
			
			idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI);
					
			int toFail = maxRequests/2;
			int toFault = maxRequests-toFail;
						
			List<HttpResponse> responses = null;
			if(policyType!=null && (policyType.isHazelcastCounters() || policyType.isRedisCounters())) {
				 // altrimenti a volte non funziona per via del parallelismo e del controllo che avviene una volta processata la risposta
				responses = Utils.makeSequentialRequests(toFailRequest, toFail);
			}
			else {
				responses = Utils.makeParallelRequests(toFailRequest, toFail);
			}
			
			List<HttpResponse> faultedResponses = null;
			if(policyType!=null && (policyType.isHazelcastCounters() || policyType.isRedisCounters())) {
				 // altrimenti a volte non funziona per via del parallelismo e del controllo che avviene una volta processata la risposta
				faultedResponses = Utils.makeSequentialRequests(toFaultRequest, toFault);
			}
			else {
				faultedResponses = Utils.makeParallelRequests(toFaultRequest, toFault); 
			}
			
			for (var r : faultedResponses) {
				JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(r.getContent()));
				Utils.matchEchoFaultResponseRest(jsonResp);
			}
			
			responses.addAll(faultedResponses);
	
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI);
			
			List<HttpResponse>  blockedResponses = null;
			if(policyType!=null && (policyType.isHazelcastCounters() || policyType.isRedisCounters())) {
				 // altrimenti a volte non funziona per via del parallelismo e del controllo che avviene una volta processata la risposta
				blockedResponses = Utils.makeSequentialRequests(toFailRequest, toFailRequests);
			}
			else {
				blockedResponses = Utils.makeParallelRequests(toFailRequest, toFailRequests);
			}
				
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toFailRequests, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI);
			
			if(org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.isJenkins() && policyType!=null && policyType.useNearCache()) {
				try {
					checkOkRequests(responses, windowSize, maxRequests, policyType);
				}catch(Throwable t) {
					System.out.println("WARNINIG JENKINS TEST FAILED testErogazione 'checkOkRequests' ("+policyType+"): "+t.getMessage());
				}
				try {
					checkFailedRequests(blockedResponses, windowSize, maxRequests, policyType);
				}catch(Throwable t) {
					System.out.println("WARNINIG JENKINS TEST FAILED testErogazione 'checkFailedRequests' ("+policyType+"): "+t.getMessage());
				}
			}
			else {
				checkOkRequests(responses, windowSize, maxRequests, policyType);
				checkFailedRequests(blockedResponses, windowSize, maxRequests, policyType);		
			}
			
		}finally {
			
			// ripristino
			
			dbUtils.setEngineTypeErogazione("SoggettoInternoTest", erogazione, null);
			
			long idErogazione = dbUtils.getIdErogazione("SoggettoInternoTest", erogazione);
			Utils.ripulisciRiferimentiCacheErogazione(idErogazione);
			
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
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?problem=true");
						
		List<HttpResponse> responses = Utils.makeRequestsAndCheckPolicy(request, maxRequests, idPolicy);
		Utils.checkHeaderRemaining(responses, Headers.FailedOrFaultRemaining, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse> failedResponses = Utils.makeParallelRequests(request, toFailRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toFailRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);
	}
	
	
	public void testFruizione(String erogazione, PolicyAlias policy, PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(policyType!=null && !policyType.isSupportedResource(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI)) {
			logRateLimiting.warn("Test numeroRichiesteFalliteOFaultFruizione con policy type '"+policyType+"' non effettuato poichè non supportato dal gestore");
			return;
		}
		
		try {
		
			// Facciamo un batch di richieste fallite e un batch di richieste con fault,
			// che devono essere tutte conteggiate
			int maxRequests = getMaxRequests(policy);
			int windowSize = Utils.getPolicyWindowSize(policy);
			String path = Utils.getPolicyPath(policy);
			
			dbUtils.setEngineTypeFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policyType);
			
			long idFruizione = dbUtils.getIdFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione);
			Utils.ripulisciRiferimentiCacheFruizione(idFruizione);
					
			HttpRequest toFailRequest = new HttpRequest();
			toFailRequest.setContentType("application/json");
			toFailRequest.setMethod(HttpRequestMethod.GET);
			toFailRequest.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?returnCode=500");
			
			HttpRequest toFaultRequest = new HttpRequest();
			toFaultRequest.setContentType("application/json");
			toFaultRequest.setMethod(HttpRequestMethod.GET);
			toFaultRequest.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?problem=true");
			
			// attivo nuovo motore
			Utils.makeParallelRequests(toFailRequest, 1);
			Utils.makeParallelRequests(toFaultRequest, 1);
			
			Utils.waitForPolicy(policy);
			
			String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
			Utils.resetCounters(idPolicy);
			
			idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI);
					
			int toFail = maxRequests/2;
			int toFault = maxRequests-toFail;
			
			List<HttpResponse> responses = null;
			if(policyType!=null && (policyType.isHazelcastCounters() || policyType.isRedisCounters())) {
				 // altrimenti a volte non funziona per via del parallelismo e del controllo che avviene una volta processata la risposta
				responses = Utils.makeSequentialRequests(toFailRequest, toFail);
			}
			else {
				responses = Utils.makeParallelRequests(toFailRequest, toFail);
			}
			
			List<HttpResponse> faultedResponses = null;
			if(policyType!=null && (policyType.isHazelcastCounters() || policyType.isRedisCounters())) {
				 // altrimenti a volte non funziona per via del parallelismo e del controllo che avviene una volta processata la risposta
				faultedResponses = Utils.makeSequentialRequests(toFaultRequest, toFault);
			}
			else {
				faultedResponses = Utils.makeParallelRequests(toFaultRequest, toFault); 
			}
			
			for (var r : faultedResponses) {
				JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(r.getContent()));
				Utils.matchEchoFaultResponseRest(jsonResp);
			}
			
			responses.addAll(faultedResponses);
	
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI);
			
			List<HttpResponse>  blockedResponses = null;
			if(policyType!=null && (policyType.isHazelcastCounters() || policyType.isRedisCounters())) {
				 // altrimenti a volte non funziona per via del parallelismo e del controllo che avviene una volta processata la risposta
				blockedResponses = Utils.makeSequentialRequests(toFailRequest, toFailRequests);
			}
			else {
				blockedResponses = Utils.makeParallelRequests(toFailRequest, toFailRequests);
			}
			
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toFailRequests, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI);
			
			if(org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.isJenkins() && policyType!=null && policyType.useNearCache()) {
				try {
					checkOkRequests(responses, windowSize, maxRequests, policyType);
				}catch(Throwable t) {
					System.out.println("WARNINIG JENKINS TEST FAILED testFruizione 'checkOkRequests' ("+policyType+"): "+t.getMessage());
				}
				try {
					checkFailedRequests(blockedResponses, windowSize, maxRequests, policyType);
				}catch(Throwable t) {
					System.out.println("WARNINIG JENKINS TEST FAILED testFruizione 'checkFailedRequests' ("+policyType+"): "+t.getMessage());
				}
			}
			else {
				checkOkRequests(responses, windowSize, maxRequests, policyType);
				checkFailedRequests(blockedResponses, windowSize, maxRequests, policyType);	
			}
			
		}finally {
			
			// ripristino
			
			dbUtils.setEngineTypeFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, null);
			
			long idFruizione = dbUtils.getIdFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione);
			Utils.ripulisciRiferimentiCacheFruizione(idFruizione);
			
		}
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
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?problem=true");
						
		List<HttpResponse> responses = Utils.makeRequestsAndCheckPolicy(request, maxRequests, idPolicy);
		Utils.checkHeaderRemaining(responses, Headers.FailedOrFaultRemaining, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		List<HttpResponse> failedResponses = Utils.makeParallelRequests(request, toFailRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, toFailRequests);
		
		checkOkRequests(responses, windowSize, maxRequests);
		checkFailedRequests(failedResponses, windowSize, maxRequests);
	}
	
	
	private void checkOkRequests(List<HttpResponse> responses, int windowSize, int maxRequests) {
		checkOkRequests(responses, windowSize, maxRequests, PolicyGroupByActiveThreadsType.LOCAL);
	}
	private void checkOkRequests(List<HttpResponse> responses, int windowSize, int maxRequests, PolicyGroupByActiveThreadsType policyType) {
		// Delle richieste ok Controllo lo header *-Limit, *-Reset e lo status code
		
		responses.forEach( r -> {
			
			Utils.checkXLimitHeader(logRateLimiting, Headers.FailedOrFaultLimit, r.getHeaderFirstValue(Headers.FailedOrFaultLimit), maxRequests);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
				Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.FailedOrFaultLimit), maxRequests, windowMap);
			}
			
			assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.FailedOrFaultReset)) <= windowSize);
			assertNotEquals(null, Integer.valueOf(r.getHeaderFirstValue(Headers.FailedOrFaultRemaining)));
			assertEquals(500, r.getResultHTTPOperation());			
		});
	}
	
	private void checkFailedRequests(List<HttpResponse> responses, int windowSize, int maxRequests) throws Exception {
		checkFailedRequests(responses, windowSize, maxRequests, PolicyGroupByActiveThreadsType.LOCAL);
	}
	private void checkFailedRequests(List<HttpResponse> responses, int windowSize, int maxRequests, PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(policyType!=null && policyType.isInconsistent()) {
			// numero troppo casuali
			return;
		}
		
		try {
			_checkFailedRequests(responses, windowSize, maxRequests, policyType);
		}catch(Throwable t) {
			if( (!org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.isJenkins()) || 
					(policyType==null || policyType.isExact())
			) {
				throw t;
			}
			else {
				// jenkins (essendo un ambiente con una sola CPU le metriche approssimate non sono facilmente verificabili)
				logRateLimiting.debug("Verifica checkFailedRequests fallita con policy '"+policyType+"' su ambiente jenkins: "+t.getMessage(),t);
			}
		}
	}
	private void _checkFailedRequests(List<HttpResponse> responses, int windowSize, int maxRequests, PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		for (var r: responses) {
			
			String idTransazione = r.getHeaderFirstValue("GovWay-Transaction-ID");
			String prefix = "(id:"+idTransazione+") ";
			
			Utils.checkXLimitHeader(logRateLimiting, Headers.FailedOrFaultLimit, r.getHeaderFirstValue(Headers.FailedOrFaultLimit), maxRequests);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
				Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.FailedOrFaultLimit), maxRequests, windowMap);
			}
			
			assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.FailedOrFaultReset)) <= windowSize);
			assertEquals((prefix+"expected:429 found:"+r.getResultHTTPOperation()), 429, r.getResultHTTPOperation());
						
			JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(r.getContent()));
			Utils.matchLimitExceededRest(jsonResp);
					
			assertEquals("0", r.getHeaderFirstValue(Headers.FailedOrFaultRemaining));
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
