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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.tempo_complessivo_risposta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Map;
import java.util.Vector;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.HeaderValues;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import net.minidev.json.JSONObject;

public class RestTest extends ConfigLoader {
	
	
	@Test
	public void perMinutoErogazione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "TempoComplessivoRisposta", PolicyAlias.MINUTO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "TempoComplessivoRisposta", PolicyAlias.MINUTO);
		Commons.checkPreConditionsTempoComplessivoRisposta(idPolicy); 
		
		// Aspetto lo scoccare del minuto
		
		Utils.waitForNewMinute();
	
		Vector<HttpResponse> responses = makeRequests(System.getProperty("govway_base_path") + "/SoggettoInternoTest/TempoComplessivoRisposta/v1/minuto?sleep=2000", idPolicy);
		
		checkAssertions(responses, 1, 60);
		
		Commons.checkPostConditionsTempoComplessivoRisposta(idPolicy);		
	}
	
	
	@Test
	public void orarioErogazione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "TempoComplessivoRisposta", PolicyAlias.ORARIO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "TempoComplessivoRisposta", PolicyAlias.ORARIO);
		Commons.checkPreConditionsTempoComplessivoRisposta(idPolicy); 
				
		Utils.waitForNewHour();
		
		Vector<HttpResponse> responses = makeRequests(System.getProperty("govway_base_path") + "/SoggettoInternoTest/TempoComplessivoRisposta/v1/orario?sleep=2000", idPolicy);

		checkAssertions(responses, 1, 3600);
		
		Commons.checkPostConditionsTempoComplessivoRisposta(idPolicy);		
	}
	
	
	@Test
	public void giornalieroErogazione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "TempoComplessivoRisposta", PolicyAlias.GIORNALIERO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "TempoComplessivoRisposta", PolicyAlias.GIORNALIERO);
		Commons.checkPreConditionsTempoComplessivoRisposta(idPolicy); 
				
		Utils.waitForNewDay();
		
		Vector<HttpResponse> responses = makeRequests(System.getProperty("govway_base_path") + "/SoggettoInternoTest/TempoComplessivoRisposta/v1/giornaliero?sleep=2000",idPolicy);

		checkAssertions(responses, 1, 86400);
		
		Commons.checkPostConditionsTempoComplessivoRisposta(idPolicy);		
	}
	
	
	@Test
	public void perMinutoFruizione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "TempoComplessivoRisposta", PolicyAlias.MINUTO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "TempoComplessivoRisposta", PolicyAlias.MINUTO);
		Commons.checkPreConditionsTempoComplessivoRisposta(idPolicy); 
				
		Utils.waitForNewMinute();
	
		Vector<HttpResponse> responses = makeRequests(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TempoComplessivoRisposta/v1/minuto?sleep=2000",idPolicy);
		
		checkAssertions(responses, 1, 60);
		
		Commons.checkPostConditionsTempoComplessivoRisposta(idPolicy);		
	}
	
	
	@Test
	public void orarioFruizione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "TempoComplessivoRisposta", PolicyAlias.ORARIO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "TempoComplessivoRisposta", PolicyAlias.ORARIO);
		Commons.checkPreConditionsTempoComplessivoRisposta(idPolicy); 
				
		Utils.waitForNewMinute();
	
		Vector<HttpResponse> responses = makeRequests(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TempoComplessivoRisposta/v1/orario?sleep=2000",idPolicy);
		
		checkAssertions(responses, 1, 3600);
		
		Commons.checkPostConditionsTempoComplessivoRisposta(idPolicy);		
	}
	
	
	@Test
	public void giornalieroFruizione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "TempoComplessivoRisposta", PolicyAlias.GIORNALIERO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "TempoComplessivoRisposta", PolicyAlias.GIORNALIERO);
		Commons.checkPreConditionsTempoComplessivoRisposta(idPolicy); 
				
		Utils.waitForNewMinute();
	
		Vector<HttpResponse> responses = makeRequests(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TempoComplessivoRisposta/v1/giornaliero?sleep=2000", idPolicy);
		
		checkAssertions(responses, 1, 86400);
		
		Commons.checkPostConditionsTempoComplessivoRisposta(idPolicy);		
	}


		
	private Vector<HttpResponse> makeRequests(String url, String idPolicy) throws UtilsException {
		
		Vector<HttpResponse> responses = new Vector<>();
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl( url );
								
		HttpResponse response = HttpUtilities.httpInvoke(request);
		responses.add(response);
		logRateLimiting.info("Request: " + request.getUrl());
		logRateLimiting.info("ResponseStatus: " + response.getResultHTTPOperation());
		logRateLimiting.info("ResponseHeaders:\n" + response.getHeaders());
		logRateLimiting.info("ResponseBody: " + new String(response.getContent()));
		
		Utils.waitForZeroActiveRequests(idPolicy, 1);
		
		response = HttpUtilities.httpInvoke(request);
		responses.add(response);
		logRateLimiting.info("Request: " + request.getUrl());
		logRateLimiting.info("ResponseStatus: " + response.getResultHTTPOperation());
		logRateLimiting.info("ResponseHeaders:\n" + response.getHeaders());
		logRateLimiting.info("ResponseBody: " + new String(response.getContent()));
		
		return responses;
	}
	
	private void checkAssertions(Vector<HttpResponse> responses, int maxSeconds, int windowSize) throws Exception {
				
		responses.forEach(r -> { 			
				assertNotEquals(null,Integer.valueOf(r.getHeader(Headers.RateLimitTimeResponseQuotaReset)));
				Utils.checkXLimitHeader(logRateLimiting, Headers.RateLimitTimeResponseQuotaLimit, r.getHeader(Headers.RateLimitTimeResponseQuotaLimit), maxSeconds);
				
				if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
					Map<Integer,Integer> windowMap = Map.of(windowSize,maxSeconds);							
					Utils.checkXLimitWindows(r.getHeader(Headers.RateLimitTimeResponseQuotaLimit), maxSeconds, windowMap);
				}
			});

		// Tutte le richieste tranne una devono restituire 200
		
		assertEquals(1, responses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());

		
		// La richiesta fallita deve avere status code 429
		
		HttpResponse failedResponse = responses.stream().filter(r -> r.getResultHTTPOperation() == 429).findAny()
				.orElse(null);
		assertNotEquals(null,failedResponse);
		
		JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(failedResponse.getContent()));
		Utils.matchLimitExceededRest(jsonResp);
		
		assertEquals("0", failedResponse.getHeader(Headers.RateLimitTimeResponseQuotaRemaining));
		assertEquals(HeaderValues.LimitExceeded, failedResponse.getHeader(Headers.GovWayTransactionErrorType));
		assertEquals(HeaderValues.ReturnCodeTooManyRequests, failedResponse.getHeader(Headers.ReturnCode));
		assertNotEquals(null, failedResponse.getHeader(Headers.RetryAfter));
	}
	
	

}
