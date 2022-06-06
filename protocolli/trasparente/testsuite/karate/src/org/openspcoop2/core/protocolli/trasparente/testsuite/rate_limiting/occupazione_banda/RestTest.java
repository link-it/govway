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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.occupazione_banda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
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
	
	private static final int requestSizeBytes = 512;

	@Test
	public void perMinutoErogazione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaRest",
				PolicyAlias.MINUTO);
		Utils.resetCounters(idPolicy);

		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaRest", PolicyAlias.MINUTO);
		Commons.checkPreConditionsOccupazioneBanda(idPolicy);
		
		Utils.waitForNewMinute();
		
		// Faccio n-1 richieste parallele per testare eventuali race conditions sui contatori,
		// alla fine faccio l'ultima che fa scattare la policy

		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setContent(generatePayload(requestSizeBytes));
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/OccupazioneBandaRest/v1/minuto");

		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
		
		Utils.waitForZeroActiveRequests(idPolicy, 3);
		
		responses.addAll(Utils.makeSequentialRequests(request, 1));

		checkAssertions(responses, 5, 60);
		Commons.checkPostConditionsOccupazioneBanda(idPolicy);
	}
	
	
	@Test
	public void perMinutoDefaultErogazione() throws Exception {
		perMinutoDefaultErogazione(null);
	}
	public void perMinutoDefaultErogazione(PolicyGroupByActiveThreadsType policyType) throws Exception {

		if(policyType!=null && !policyType.isSupportedResource(TipoRisorsaPolicyAttiva.OCCUPAZIONE_BANDA)) {
			logRateLimiting.warn("Test occupazioneBandaErogazioni con policy type '"+policyType+"' non effettuato poichè non supportato dal gestore");
			return;
		}
		
		try {
		
			final int sogliaKb = 1024;
			final int requestToPass = 3;
			final int requestSizeBytes = (sogliaKb*1000/ (requestToPass-1))/4;
	
			dbUtils.setEngineTypeErogazione("SoggettoInternoTest", "OccupazioneBandaRest", policyType);
			
			long idErogazione = dbUtils.getIdErogazione("SoggettoInternoTest", "OccupazioneBandaRest");
			Utils.ripulisciRiferimentiCacheErogazione(idErogazione);
					
			HttpRequest request = new HttpRequest();
			request.setContentType("application/json");
			request.setContent(generatePayload(requestSizeBytes));
			request.setMethod(HttpRequestMethod.POST);
			request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/OccupazioneBandaRest/v1/minuto-default");
			
			// attivo nuovo motore
			Utils.makeParallelRequests(request, 1);
			
			
			String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaRest",
					PolicyAlias.MINUTODEFAULT);
			Utils.resetCounters(idPolicy);
	
			idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaRest", PolicyAlias.MINUTODEFAULT);
			Commons.checkPreConditionsOccupazioneBanda(idPolicy, policyType);
			
			Utils.waitForNewMinute();
			
			// Faccio n-1 richieste parallele per testare eventuali race conditions sui contatori,
			// alla fine faccio l'ultima che fa scattare la policy
	
			Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
			
			Utils.waitForZeroActiveRequests(idPolicy, 3, policyType);
			
			responses.addAll(Utils.makeSequentialRequests(request, 1));
	
			checkAssertions(responses, 1024, 60, policyType);
			Commons.checkPostConditionsOccupazioneBanda(idPolicy, policyType);
			
		}finally {
			
			// ripristino
			
			dbUtils.setEngineTypeErogazione("SoggettoInternoTest", "OccupazioneBandaRest", null);
			
			long idErogazione = dbUtils.getIdErogazione("SoggettoInternoTest", "OccupazioneBandaRest");
			Utils.ripulisciRiferimentiCacheErogazione(idErogazione);
			
		}
	}
	
	
	@Test
	public void orarioErogazione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaRest",	PolicyAlias.ORARIO);
		Utils.resetCounters(idPolicy);

		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaRest", PolicyAlias.ORARIO);
		Commons.checkPreConditionsOccupazioneBanda(idPolicy);
		
		Utils.waitForNewHour();
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setContent(generatePayload(requestSizeBytes));
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/OccupazioneBandaRest/v1/orario");

		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
		Utils.waitForZeroActiveRequests(idPolicy, 3);
		responses.addAll(Utils.makeSequentialRequests(request, 1));

		checkAssertions(responses, 5, 3600);
		Commons.checkPostConditionsOccupazioneBanda(idPolicy);
	}
	
	
	@Test
	public void giornalieroErogazione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaRest",	PolicyAlias.GIORNALIERO);
		Utils.resetCounters(idPolicy);

		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "OccupazioneBandaRest", PolicyAlias.GIORNALIERO);
		Commons.checkPreConditionsOccupazioneBanda(idPolicy);
		
		Utils.waitForNewDay();

		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setContent(generatePayload(requestSizeBytes));
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/OccupazioneBandaRest/v1/giornaliero");

		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
		Utils.waitForZeroActiveRequests(idPolicy, 3);
		responses.addAll(Utils.makeSequentialRequests(request, 1));

		checkAssertions(responses, 5, 86400);
		Commons.checkPostConditionsOccupazioneBanda(idPolicy);
	}
	
	
	@Test
	public void perMinutoFruizione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaRest", PolicyAlias.MINUTO);
		Utils.resetCounters(idPolicy);

		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaRest", PolicyAlias.MINUTO);
		Commons.checkPreConditionsOccupazioneBanda(idPolicy);
		
		Utils.waitForNewMinute();

		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setContent(generatePayload(requestSizeBytes));
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/OccupazioneBandaRest/v1/minuto");

		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
		Utils.waitForZeroActiveRequests(idPolicy, 3);
		responses.addAll(Utils.makeSequentialRequests(request, 1));

		checkAssertions(responses, 5, 60);
		Commons.checkPostConditionsOccupazioneBanda(idPolicy);
	}
	
	
	@Test
	public void perMinutoDefaultFruizione() throws Exception {
		perMinutoDefaultFruizione(null);
	}
	public void perMinutoDefaultFruizione(PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(policyType!=null && !policyType.isSupportedResource(TipoRisorsaPolicyAttiva.OCCUPAZIONE_BANDA)) {
			logRateLimiting.warn("Test occupazioneBandaFruizioni con policy type '"+policyType+"' non effettuato poichè non supportato dal gestore");
			return;
		}
		
		try {
		
			final int sogliaKb = 1024;
			final int requestToPass = 3;
			final int requestSizeBytes = (sogliaKb*1000/ (requestToPass-1))/4;
			
			dbUtils.setEngineTypeFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaRest", policyType);
			
			long idFruizione = dbUtils.getIdFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaRest");
			Utils.ripulisciRiferimentiCacheFruizione(idFruizione);
					
			HttpRequest request = new HttpRequest();
			request.setContentType("application/json");
			request.setContent(generatePayload(requestSizeBytes));
			request.setMethod(HttpRequestMethod.POST);
			request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/OccupazioneBandaRest/v1/minuto-default");
			
			// attivo nuovo motore
			Utils.makeParallelRequests(request, 1);
			
			String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaRest", PolicyAlias.MINUTODEFAULT);
			Utils.resetCounters(idPolicy);
	
			idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaRest", PolicyAlias.MINUTODEFAULT);
			Commons.checkPreConditionsOccupazioneBanda(idPolicy, policyType);
			
			Utils.waitForNewMinute();
	
			Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
			Utils.waitForZeroActiveRequests(idPolicy, 3, policyType);
			responses.addAll(Utils.makeSequentialRequests(request, 1));
	
			checkAssertions(responses, 1024, 60, policyType);
			Commons.checkPostConditionsOccupazioneBanda(idPolicy, policyType);
			
		}finally {
			
			// ripristino
			
			dbUtils.setEngineTypeFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaRest", null);
			
			long idFruizione = dbUtils.getIdFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaRest");
			Utils.ripulisciRiferimentiCacheFruizione(idFruizione);
			
		}
	}
	
	
	
	@Test
	public void orarioFruizione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaRest", PolicyAlias.ORARIO);
		Utils.resetCounters(idPolicy);

		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaRest", PolicyAlias.ORARIO);
		Commons.checkPreConditionsOccupazioneBanda(idPolicy);
		
		Utils.waitForNewHour();
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setContent(generatePayload(requestSizeBytes));
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/OccupazioneBandaRest/v1/orario");

		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
		Utils.waitForZeroActiveRequests(idPolicy, 3);
		responses.addAll(Utils.makeSequentialRequests(request, 1));

		checkAssertions(responses, 5, 3600);
		Commons.checkPostConditionsOccupazioneBanda(idPolicy);
	}
	
	
	@Test
	public void giornalieroFruizione() throws Exception {

		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaRest", PolicyAlias.GIORNALIERO);
		Utils.resetCounters(idPolicy);

		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "OccupazioneBandaRest", PolicyAlias.GIORNALIERO);
		Commons.checkPreConditionsOccupazioneBanda(idPolicy);
		
		Utils.waitForNewDay();

		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setContent(generatePayload(requestSizeBytes));
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/OccupazioneBandaRest/v1/giornaliero");

		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
		Utils.waitForZeroActiveRequests(idPolicy, 3);
		responses.addAll(Utils.makeSequentialRequests(request, 1));

		checkAssertions(responses, 5, 86400);
		Commons.checkPostConditionsOccupazioneBanda(idPolicy);
	}
	
	
	
	private void checkAssertions(Vector<HttpResponse> responses, int maxKb, int windowSize) throws Exception {
		checkAssertions(responses, maxKb, windowSize, PolicyGroupByActiveThreadsType.LOCAL);
	}
	private void checkAssertions(Vector<HttpResponse> responses, int maxKb, int windowSize, PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP.equals(policyType) ||
				PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP.equals(policyType)) {
			// numero troppo casuali
			return;
		}
		
		responses.forEach(r -> { 			
				assertNotEquals(null,Integer.valueOf(r.getHeaderFirstValue(Headers.BandWidthQuotaReset)));
				Utils.checkXLimitHeader(logRateLimiting, Headers.BandWidthQuotaLimit, r.getHeaderFirstValue(Headers.BandWidthQuotaLimit), maxKb);
				
				if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
					Map<Integer,Integer> windowMap = Map.of(windowSize,maxKb);							
					Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.BandWidthQuotaLimit), maxKb, windowMap);
				}
			});

		// Tutte le richieste tranne una devono restituire 200
		
		assertEquals(responses.size()-1, responses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());

		
		// La richiesta fallita deve avere status code 429
		
		HttpResponse failedResponse = responses.stream().filter(r -> r.getResultHTTPOperation() == 429).findAny()
				.orElse(null);
		assertNotEquals(null,failedResponse);
		
		JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(failedResponse.getContent()));
		JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
		
		assertEquals("https://govway.org/handling-errors/429/LimitExceeded.html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
		assertEquals("LimitExceeded", jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
		assertEquals(429, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
		assertNotEquals(null, jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0));	
		assertEquals("Limit exceeded detected", jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0));
		
		assertEquals("0", failedResponse.getHeaderFirstValue(Headers.BandWidthQuotaRemaining));
		assertEquals(HeaderValues.LIMIT_EXCEEDED, failedResponse.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
		Utils.checkHeaderTooManyRequest(failedResponse);
		assertNotEquals(null, failedResponse.getHeaderFirstValue(Headers.RetryAfter));
	}
	

	private byte[] generatePayload(int payloadSize) {
		byte[] ret = new byte[payloadSize];
		Arrays.fill(ret, (byte) 97);
		return ret;
	}

}
