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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.tempo_medio_risposta;

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
import org.openspcoop2.utils.Utilities;
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
	
	final static String erogazione = "TempoMedioRispostaRest";
	
	@Test
	public void perMinutoErogazione() throws Exception {
		testErogazione(PolicyAlias.MINUTO, null);
	}
	
	@Test
	public void perMinutoDefaultErogazione() throws Exception {
		perMinutoDefaultErogazione(null);
	}
	public void perMinutoDefaultErogazione(PolicyGroupByActiveThreadsType policyType) throws Exception {
		testErogazione(PolicyAlias.MINUTODEFAULT, policyType);
	}
	
	@Test
	public void orarioErogazione() throws Exception {
		testErogazione(PolicyAlias.ORARIO, null);
	}
	
	@Test
	public void giornalieroErogazione() throws Exception {
		testErogazione(PolicyAlias.GIORNALIERO, null);
	}
	
	@Test
	public void perMinutoFruizione() throws Exception {
		testFruizione(PolicyAlias.MINUTO, null);
	}
	
	@Test
	public void perMinutoDefaultFruizione() throws Exception {
		perMinutoDefaultFruizione(null);
	}
	public void perMinutoDefaultFruizione(PolicyGroupByActiveThreadsType policyType) throws Exception {
		testFruizione(PolicyAlias.MINUTODEFAULT, policyType);
	}
	
	@Test
	public void orarioFruizione() throws Exception {
		testFruizione(PolicyAlias.ORARIO, null);
	}
	
	@Test
	public void giornalieroFruizione() throws Exception {
		testFruizione(PolicyAlias.GIORNALIERO, null);
	}
	
	public void testErogazione(PolicyAlias policy, PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(policyType!=null && !policyType.isSupportedResource(TipoRisorsaPolicyAttiva.TEMPO_COMPLESSIVO_RISPOSTA)) {
			logRateLimiting.warn("Test tempoMedioRispostaErogazione con policy type '"+policyType+"' non effettuato poichè non supportato dal gestore");
			return;
		}
		
		try {
		
			final int windowSize = Utils.getPolicyWindowSize(policy);
			final int soglia = getSoglia(policy);
			final int small_delay = soglia/2;
			final int small_delay_count = 2;
			final int big_delay = (small_delay_count+1)*soglia - small_delay*(small_delay_count) + 20;
			
			dbUtils.setEngineTypeErogazione("SoggettoInternoTest", erogazione, policyType);
			
			long idErogazione = dbUtils.getIdErogazione("SoggettoInternoTest", erogazione);
			Utils.ripulisciRiferimentiCacheErogazione(idErogazione);
					
			String path = Utils.getPolicyPath(policy);
			HttpRequest request = new HttpRequest();
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?sleep="+small_delay );
			
			// attivo nuovo motore
			Utils.makeParallelRequests(request, 1);
			
			Utils.waitForPolicy(policy);
			
			String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
			Utils.resetCounters(idPolicy);
			
			idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
			Commons.checkPreConditionsTempoMedioRisposta(idPolicy, policyType);
			
			
			// Faccio prima 3 richieste che passano
				
			List<HttpResponse> notBlockedResponses = null;
			if(policyType!=null && (policyType.isHazelcastCounters() || policyType.isRedisCounters() || PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE.equals(policyType))) {
				 // altrimenti a volte non funziona per via del parallelismo e del controllo che avviene una volta processata la risposta
				notBlockedResponses = Utils.makeSequentialRequests(request, small_delay_count);
			}
			else {
				notBlockedResponses = Utils.makeParallelRequests(request, small_delay_count);
			}
			
			// Poi faccio una richiesta che fa scattare la policy
			Utilities.sleep(1000);
					
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?sleep="+big_delay );
			
			notBlockedResponses.addAll(Utils.makeParallelRequests(request, 1));
			
			Utilities.sleep(1000);
			
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, small_delay_count+1, 0, policyType, TipoRisorsaPolicyAttiva.TEMPO_MEDIO_RISPOSTA);
			
			// Poi faccio n richieste che non passano
			
			Utilities.sleep(2000); // aspetto che le precedenti richieste vengano registrate
			
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?sleep="+small_delay );
			
			List<HttpResponse> blockedResponses = Utils.makeParallelRequests(request, small_delay_count);
			
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, small_delay_count+1, small_delay_count, policyType, TipoRisorsaPolicyAttiva.TEMPO_MEDIO_RISPOSTA);
			
			if(org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.isJenkins() && policyType!=null && policyType.useNearCache()) {
				try {
					checkPassedRequests(notBlockedResponses, windowSize, soglia, policyType);
				}catch(Throwable t) {
					System.out.println("WARNINIG JENKINS TEST FAILED "+this.getClass().getName()+".testErogazione 'checkPassedRequests' ("+policyType+"): "+t.getMessage());
				}
				try {
					checkBlockedRequests(blockedResponses, windowSize, soglia, policyType);
				}catch(Throwable t) {
					System.out.println("WARNINIG JENKINS TEST FAILED "+this.getClass().getName()+".testErogazione 'checkBlockedRequests' ("+policyType+"): "+t.getMessage());
				}
			}
			else {
				checkPassedRequests(notBlockedResponses, windowSize, soglia, policyType);
				checkBlockedRequests(blockedResponses, windowSize, soglia, policyType);
			}
			
		}finally {
			
			// ripristino
			
			dbUtils.setEngineTypeErogazione("SoggettoInternoTest", erogazione, null);
			
			long idErogazione = dbUtils.getIdErogazione("SoggettoInternoTest", erogazione);
			Utils.ripulisciRiferimentiCacheErogazione(idErogazione);
			
		}
	}
	
	
	public void testFruizione(PolicyAlias policy, PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(policyType!=null && !policyType.isSupportedResource(TipoRisorsaPolicyAttiva.TEMPO_COMPLESSIVO_RISPOSTA)) {
			logRateLimiting.warn("Test tempoMedioRispostaFruizione con policy type '"+policyType+"' non effettuato poichè non supportato dal gestore");
			return;
		}
		
		try {
		
			final int windowSize = Utils.getPolicyWindowSize(policy);
			final int soglia = getSoglia(policy);
			final int small_delay = soglia/2;
			final int small_delay_count = 2;
			final int big_delay = (small_delay_count+1)*soglia - small_delay*(small_delay_count) + 20;
	
			String path = Utils.getPolicyPath(policy);
			
			dbUtils.setEngineTypeFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policyType);
			
			long idFruizione = dbUtils.getIdFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione);
			Utils.ripulisciRiferimentiCacheFruizione(idFruizione);
					
			HttpRequest request = new HttpRequest();
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?sleep="+small_delay );
			
			// attivo nuovo motore
			Utils.makeParallelRequests(request, 1);
			
			Utils.waitForPolicy(policy);
			
			String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
			Utils.resetCounters(idPolicy);
			
			idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
			Commons.checkPreConditionsTempoMedioRisposta(idPolicy, policyType);
			
			
			
			// Faccio prima richieste che passano
			List<HttpResponse> notBlockedResponses = null;
			if(policyType!=null && (policyType.isHazelcastCounters() || policyType.isRedisCounters() || PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE.equals(policyType))) {
				 // altrimenti a volte non funziona per via del parallelismo e del controllo che avviene una volta processata la risposta
				notBlockedResponses = Utils.makeSequentialRequests(request, small_delay_count);
			}
			else {
				notBlockedResponses = Utils.makeParallelRequests(request, small_delay_count);
			}
			
			// Poi faccio una richiesta che fa scattare la policy
			Utilities.sleep(1000);
			
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?sleep="+big_delay );
			
			notBlockedResponses.addAll(Utils.makeParallelRequests(request, 1));
			
			Utilities.sleep(1000);
			
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, small_delay_count+1, 0, policyType, TipoRisorsaPolicyAttiva.TEMPO_MEDIO_RISPOSTA);
			
			// Poi faccio n richieste che non passano
			
			Utilities.sleep(2000); // aspetto che le precedenti richieste vengano registrate
			
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?sleep="+small_delay );
			
			List<HttpResponse> blockedResponses = Utils.makeParallelRequests(request, small_delay_count);
			
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, small_delay_count+1, small_delay_count, policyType, TipoRisorsaPolicyAttiva.TEMPO_MEDIO_RISPOSTA);
			
			if(org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.isJenkins() && policyType!=null && policyType.useNearCache()) {
				try {
					checkPassedRequests(notBlockedResponses, windowSize, soglia, policyType);
				}catch(Throwable t) {
					System.out.println("WARNINIG JENKINS TEST FAILED "+this.getClass().getName()+".testFruizione 'checkPassedRequests' ("+policyType+"): "+t.getMessage());
				}
				try {
					checkBlockedRequests(blockedResponses, windowSize, soglia, policyType);
				}catch(Throwable t) {
					System.out.println("WARNINIG JENKINS TEST FAILED "+this.getClass().getName()+".testFruizione 'checkBlockedRequests' ("+policyType+"): "+t.getMessage());
				}
			}
			else {
				checkPassedRequests(notBlockedResponses, windowSize, soglia, policyType);
				checkBlockedRequests(blockedResponses, windowSize, soglia, policyType);
			}
			
		}finally {
			
			// ripristino
	
			dbUtils.setEngineTypeFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, null);
			
			long idFruizione = dbUtils.getIdFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione);
			Utils.ripulisciRiferimentiCacheFruizione(idFruizione);
			
		}
		
	}
	
	private void checkPassedRequests(List<HttpResponse> responses, int windowSize, int soglia, PolicyGroupByActiveThreadsType policyType) {
		
		if(policyType!=null && policyType.isInconsistent()) {
			// numero troppo casuali
			return;
		}
		
		try {
			_checkPassedRequests(responses, windowSize, soglia, policyType);
		}catch(Throwable t) {
			if( (!org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.isJenkins()) || 
					(policyType==null || policyType.isExact())
			) {
				throw t;
			}
			else {
				// jenkins (essendo un ambiente con una sola CPU le metriche approssimate non sono facilmente verificabili)
				logRateLimiting.debug("Verifica _checkPassedRequests fallita con policy '"+policyType+"' su ambiente jenkins: "+t.getMessage(),t);
			}
		}
	}
	
	private void _checkPassedRequests(List<HttpResponse> responses, int windowSize, int soglia, PolicyGroupByActiveThreadsType policyType) {
		
		// Delle richieste ok Controllo lo header *-Limit, *-Reset e lo status code
		
		responses.forEach( r -> {
			
			Utils.checkXLimitHeader(logRateLimiting, Headers.AvgTimeResponseLimit, r.getHeaderFirstValue(Headers.AvgTimeResponseLimit), soglia);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,soglia);							
				Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.AvgTimeResponseLimit), soglia, windowMap);
			}
			
			assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.AvgTimeResponseReset)) <= windowSize);
			assertEquals(200, r.getResultHTTPOperation());			
		});
	}
	
	private void checkBlockedRequests(List<HttpResponse> responses, int windowSize, int soglia, PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(policyType!=null && policyType.isInconsistent()) {
			// numero troppo casuali
			return;
		}
		
		try {
			_checkBlockedRequests(responses, windowSize, soglia, policyType);
		}catch(Throwable t) {
			if( (!org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.isJenkins()) || 
					(policyType==null || policyType.isExact())
			) {
				throw t;
			}
			else {
				// jenkins (essendo un ambiente con una sola CPU le metriche approssimate non sono facilmente verificabili)
				logRateLimiting.debug("Verifica _checkBlockedRequests fallita con policy '"+policyType+"' su ambiente jenkins: "+t.getMessage(),t);
			}
		}
	}
	
	private void _checkBlockedRequests(List<HttpResponse> responses, int windowSize, int soglia, PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		for (var r: responses) {
			Utils.checkXLimitHeader(logRateLimiting, Headers.AvgTimeResponseLimit, r.getHeaderFirstValue(Headers.AvgTimeResponseLimit), soglia);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,soglia);							
				Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.AvgTimeResponseLimit), soglia, windowMap);
			}
			
			assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.AvgTimeResponseReset)) <= windowSize);
			assertEquals(429, r.getResultHTTPOperation());
			
			JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(r.getContent()));
			Utils.matchLimitExceededRest(jsonResp);
			
			assertEquals(HeaderValues.LIMIT_EXCEEDED, r.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
			Utils.checkHeaderTooManyRequest(r);
			assertNotEquals(null, r.getHeaderFirstValue(Headers.RetryAfter));
		}	
	}
	
	private int getSoglia(PolicyAlias policy) {
		if ( policy == PolicyAlias.MINUTODEFAULT )
			return 2000;
		else
			return 1000;
	}

}
