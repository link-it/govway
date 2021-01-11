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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.tempo_medio_risposta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Vector;

import org.junit.Test;
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

public class RestTest extends ConfigLoader {
	
	final static String erogazione = "TempoMedioRispostaRest";
	
	@Test
	public void perMinutoErogazione() throws Exception {
		testErogazione(PolicyAlias.MINUTO);
	}
	
	@Test
	public void perMinutoDefaultErogazione() throws Exception {
		testErogazione(PolicyAlias.MINUTODEFAULT);
	}
	
	@Test
	public void orarioErogazione() throws Exception {
		testErogazione(PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroErogazione() throws Exception {
		testErogazione(PolicyAlias.GIORNALIERO);
	}
	
	@Test
	public void perMinutoFruizione() throws Exception {
		testFruizione(PolicyAlias.MINUTO);
	}
	
	@Test
	public void perMinutoDefaultFruizione() throws Exception {
		testFruizione(PolicyAlias.MINUTODEFAULT);
	}
	
	@Test
	public void orarioFruizione() throws Exception {
		testFruizione(PolicyAlias.ORARIO);
	}
	
	@Test
	public void giornalieroFruizione() throws Exception {
		testFruizione(PolicyAlias.GIORNALIERO);
	}
	
	public void testErogazione(PolicyAlias policy) throws Exception {
		
		final int windowSize = Utils.getPolicyWindowSize(policy);
		final int soglia = getSoglia(policy);
		final int small_delay = soglia/2;
		final int small_delay_count = 2;
		final int big_delay = (small_delay_count+1)*soglia - small_delay*(small_delay_count) + 20;
		
		
		String path = Utils.getPolicyPath(policy);
		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Commons.checkPreConditionsTempoMedioRisposta(idPolicy);
		
		Utils.waitForPolicy(policy);
		
		// Faccio prima 3 richieste che passano
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?sleep="+small_delay );
		
		Vector<HttpResponse> notBlockedResponses = Utils.makeParallelRequests(request, small_delay_count);
		
		// Poi faccio una richiesta che fa scattare la policy
		
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?sleep="+big_delay );
		
		notBlockedResponses.addAll(Utils.makeParallelRequests(request, 1));
		
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, small_delay_count+1, 0);
		
		// Poi faccio n richieste che non passano
		
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?sleep="+small_delay );
		
		Vector<HttpResponse> blockedResponses = Utils.makeParallelRequests(request, small_delay_count);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, small_delay_count+1, small_delay_count);
		checkPassedRequests(notBlockedResponses, windowSize, soglia);
		checkBlockedRequests(blockedResponses, windowSize, soglia);
	}
	
	
	public void testFruizione(PolicyAlias policy) throws Exception {
		
		final int windowSize = Utils.getPolicyWindowSize(policy);
		final int soglia = getSoglia(policy);
		final int small_delay = soglia/2;
		final int small_delay_count = 2;
		final int big_delay = (small_delay_count+1)*soglia - small_delay*(small_delay_count) + 20;

		String path = Utils.getPolicyPath(policy);
		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Commons.checkPreConditionsTempoMedioRisposta(idPolicy);
		
		Utils.waitForPolicy(policy);

		
		// Faccio prima richieste che passano
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?sleep="+small_delay );
		
		Vector<HttpResponse> notBlockedResponses = Utils.makeParallelRequests(request, small_delay_count);
		
		// Poi faccio una richiesta che fa scattare la policy
		
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?sleep="+big_delay );
		
		notBlockedResponses.add(Utils.makeRequest(request));
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, small_delay_count+1, 0);
		
		// Poi faccio n richieste che non passano
		
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/"+path+"?sleep="+small_delay );
		
		Vector<HttpResponse> blockedResponses = Utils.makeParallelRequests(request, small_delay_count);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, small_delay_count+1, small_delay_count);
		checkPassedRequests(notBlockedResponses, windowSize, soglia);
		checkBlockedRequests(blockedResponses, windowSize, soglia);
		
	}
	
	
	private void checkPassedRequests(Vector<HttpResponse> responses, int windowSize, int soglia) {
		// Delle richieste ok Controllo lo header *-Limit, *-Reset e lo status code
		
		responses.forEach( r -> {
			
			Utils.checkXLimitHeader(logRateLimiting, Headers.AvgTimeResponseLimit, r.getHeader(Headers.AvgTimeResponseLimit), soglia);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,soglia);							
				Utils.checkXLimitWindows(r.getHeader(Headers.AvgTimeResponseLimit), soglia, windowMap);
			}
			
			assertTrue(Integer.valueOf(r.getHeader(Headers.AvgTimeResponseReset)) <= windowSize);
			assertEquals(200, r.getResultHTTPOperation());			
		});
	}
	
	private void checkBlockedRequests(Vector<HttpResponse> responses, int windowSize, int soglia) throws Exception {
		
		
		for (var r: responses) {
			Utils.checkXLimitHeader(logRateLimiting, Headers.AvgTimeResponseLimit, r.getHeader(Headers.AvgTimeResponseLimit), soglia);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,soglia);							
				Utils.checkXLimitWindows(r.getHeader(Headers.AvgTimeResponseLimit), soglia, windowMap);
			}
			
			assertTrue(Integer.valueOf(r.getHeader(Headers.AvgTimeResponseReset)) <= windowSize);
			assertEquals(429, r.getResultHTTPOperation());
			
			JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(r.getContent()));
			Utils.matchLimitExceededRest(jsonResp);
			
			assertEquals(HeaderValues.LimitExceeded, r.getHeader(Headers.GovWayTransactionErrorType));
			assertEquals(HeaderValues.ReturnCodeTooManyRequests, r.getHeader(Headers.ReturnCode));
			assertNotEquals(null, r.getHeader(Headers.RetryAfter));
		}	
	}
	
	private int getSoglia(PolicyAlias policy) {
		if ( policy == PolicyAlias.MINUTODEFAULT )
			return 2000;
		else
			return 1000;
	}

}
