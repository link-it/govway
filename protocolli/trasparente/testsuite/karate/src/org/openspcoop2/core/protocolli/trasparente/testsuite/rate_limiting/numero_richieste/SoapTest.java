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


package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

/**
* SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {
	
	
	private static final int durata_simultanee = Integer.valueOf(System.getProperty("rate_limiting.numero_richieste.durata_simultanee"));

	
	@Test 
	public void richiesteSimultaneeErogazione() throws Exception {
		final int maxConcurrentRequests = 10;
		
		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.RICHIESTE_SIMULTANEE);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.RICHIESTE_SIMULTANEE);
		Commons.checkPreConditionsRichiesteSimultanee(idPolicy);
		
		String body = SoapBodies.get(PolicyAlias.RICHIESTE_SIMULTANEE);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/NumeroRichiesteSoap/v1?sleep="+durata_simultanee);
		request.setContent(body.getBytes());
		
		List<HttpResponse> responses = Utils.makeParallelRequests(request, maxConcurrentRequests + 1);
		
		checkAssertionsRichiesteSimultanee(responses, maxConcurrentRequests);
		Commons.checkPostConditionsRichiesteSimultanee(idPolicy);
	}
	
	
	@Test
	public void richiesteSimultaneeFruizione() throws Exception {		
		final int maxConcurrentRequests = 10;
		
		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.RICHIESTE_SIMULTANEE);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.RICHIESTE_SIMULTANEE);
		Commons.checkPreConditionsRichiesteSimultanee(idPolicy);
		
		String body = SoapBodies.get(PolicyAlias.RICHIESTE_SIMULTANEE);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteSoap/v1?sleep="+durata_simultanee);
		request.setContent(body.getBytes());
		
		List<HttpResponse> responses = Utils.makeParallelRequests(request, maxConcurrentRequests + 1);
		
		checkAssertionsRichiesteSimultanee(responses, maxConcurrentRequests);
		Commons.checkPostConditionsRichiesteSimultanee(idPolicy);
	}
	
	@Test
	public void richiesteSimultaneeGlobali() throws Exception {
		final int maxConcurrentRequests = Integer.valueOf(System.getProperty("soglia_richieste_simultanee"));
				
		// Aspetto che i threads attivi sul server siano 0
		Utils.waitForZeroGovWayThreads();
		
		String body = SoapBodies.get(null);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteSoap/v1?sleep="+durata_simultanee);
		request.setContent(body.getBytes());
		
		List<HttpResponse> responses = Utils.makeParallelRequests(request, maxConcurrentRequests + 1);

		assertEquals(maxConcurrentRequests, responses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		// La richiesta fallita deve avere status code 429
		
		HttpResponse failedResponse = responses.stream().filter(r -> r.getResultHTTPOperation() == 429).findAny()
				.orElse(null);
		assertNotEquals(null, failedResponse);
		
		String bodyResp = new String(failedResponse.getContent());
		logRateLimiting.info(bodyResp);
		
		//org.w3c.dom.Element element = Utils.buildXmlElement(failedResponse.getContent());
		Utils.matchTooManyRequestsSoap(failedResponse.getContent());
		
		assertEquals(HeaderValues.TOO_MANY_REQUESTS, failedResponse.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
		Utils.checkHeaderTooManyRequest(failedResponse);
		assertNotEquals(null, failedResponse.getHeaderFirstValue(Headers.RetryAfter));		
		
		Utils.waitForZeroGovWayThreads();		
	}

	
	@Test
	public void richiestePerMinutoErogazione() throws Exception {
		logRateLimiting.info("Test richieste per minuto erogazione...");
		final int maxRequests = 5;

		// Resetto la policy di RL
		
		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.MINUTO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.MINUTO);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		// Aspetto lo scoccare del minuto

		Utils.waitForNewMinute();		
		
		String body = SoapBodies.get(PolicyAlias.MINUTO);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/NumeroRichiesteSoap/v1");
		request.setContent(body.getBytes());
		
		List<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
		
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests);
		
		responses.addAll(Utils.makeParallelRequests(request, 5));
		
		checkAssertionsNumeroRichieste(responses, maxRequests, 60);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5);				
	}
	
	
	@Test
	public void richiestePerMinutoDefaultErogazione() throws Exception {
		logRateLimiting.info("Test richieste per minuto erogazione...");
		final int maxRequests = 10;

		// Resetto la policy di RL
		
		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.MINUTODEFAULT);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.MINUTODEFAULT);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		// Aspetto lo scoccare del minuto

		Utils.waitForNewMinute();		
		
		String body = SoapBodies.get(PolicyAlias.MINUTODEFAULT);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/NumeroRichiesteSoap/v1");
		request.setContent(body.getBytes());
		
		List<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
		
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests);
		
		responses.addAll(Utils.makeParallelRequests(request, 5));
		
		checkAssertionsNumeroRichieste(responses, maxRequests, 60);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5);				
	}
	
	
	@Test
	public void richiesteOrarieErogazione() throws Exception {
		logRateLimiting.info("Test richieste orarie erogazione...");
		final int maxRequests = 10;

		// Resetto la policy di RL
		
		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.ORARIO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.ORARIO);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);

		
		// Aspetto lo scoccare del minuto

		Utils.waitForNewHour();		
		
		String body = SoapBodies.get(PolicyAlias.ORARIO);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/NumeroRichiesteSoap/v1");
		request.setContent(body.getBytes());
		
		List<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
		
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests);
		
		responses.addAll(Utils.makeParallelRequests(request, 5));
		
		checkAssertionsNumeroRichieste(responses, maxRequests, 3600);	
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5);
	}
	
	
	@Test
	public void richiesteGiornaliereErogazione() throws Exception {
		logRateLimiting.info("Test richieste giornaliere erogazione...");
		final int maxRequests = 10;

		// Resetto la policy di RL
		
		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.GIORNALIERO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.GIORNALIERO);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);

		
		// Aspetto lo scoccare del minuto

		Utils.waitForNewMinute();		
		
		String body = SoapBodies.get(PolicyAlias.GIORNALIERO);		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/NumeroRichiesteSoap/v1");
		request.setContent(body.getBytes());
		
		List<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
		
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests);
		
		responses.addAll(Utils.makeParallelRequests(request, 5));
		
		checkAssertionsNumeroRichieste(responses, maxRequests, 86400);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5);				
	}
	
	
	@Test
	public void richiestePerMinutoFruizione() throws Exception {
		logRateLimiting.info("Test richieste per minuto fruizione...");
		final int maxRequests = 5;

		// Resetto la policy di RL
		
		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.MINUTO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.MINUTO);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);

		
		// Aspetto lo scoccare del minuto

		Utils.waitForNewMinute();		
		
		String body = SoapBodies.get(PolicyAlias.MINUTO);
		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteSoap/v1");
		request.setContent(body.getBytes());
		
		List<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
		
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests);
		
		responses.addAll(Utils.makeParallelRequests(request, 5));
		
		checkAssertionsNumeroRichieste(responses, maxRequests, 60);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5);
	}
	
	
	@Test
	public void richiestePerMinutoDefaultFruizione() throws Exception {
		logRateLimiting.info("Test richieste per minuto fruizione...");
		final int maxRequests = 10;

		// Resetto la policy di RL
		
		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.MINUTODEFAULT);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.MINUTODEFAULT);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);

		
		// Aspetto lo scoccare del minuto

		Utils.waitForNewMinute();		
		
		String body = SoapBodies.get(PolicyAlias.MINUTODEFAULT);
		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteSoap/v1");
		request.setContent(body.getBytes());
		
		List<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
		
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests);
		
		responses.addAll(Utils.makeParallelRequests(request, 5));
		
		checkAssertionsNumeroRichieste(responses, maxRequests, 60);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5);
	}
	
	
	@Test
	public void richiesteOrarieFruizione() throws Exception {
		logRateLimiting.info("Test richieste orarie fruizione...");
		final int maxRequests = 10;

		// Resetto la policy di RL
		
		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.ORARIO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.ORARIO);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);

		
		// Aspetto lo scoccare dell'ora

		Utils.waitForNewHour();		
		
		String body = SoapBodies.get(PolicyAlias.ORARIO);		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteSoap/v1");
		request.setContent(body.getBytes());
		
		List<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
		
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests);
		
		responses.addAll(Utils.makeParallelRequests(request, 5));
		
		checkAssertionsNumeroRichieste(responses, maxRequests, 3600);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5);
	}
	
	
	@Test
	public void richiesteGiornaliereFruizione() throws Exception {
		logRateLimiting.info("Test richieste giornaliere fruizione...");
		final int maxRequests = 10;

		// Resetto la policy di RL
		
		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.GIORNALIERO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteSoap", PolicyAlias.GIORNALIERO);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);


		// Aspetto lo scoccare del nuovo giorno

		Utils.waitForNewDay();		
		
		String body = SoapBodies.get(PolicyAlias.GIORNALIERO);		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteSoap/v1");
		request.setContent(body.getBytes());
		
		List<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
		
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests);
		
		responses.addAll(Utils.makeParallelRequests(request, 5));
		
		checkAssertionsNumeroRichieste(responses, maxRequests, 86400);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5);
	}
	
	
	private void checkAssertionsNumeroRichieste(List<HttpResponse> responses, int maxRequests, int windowSize) throws DynamicException {

		// Tutte le richieste devono avere lo header X-RateLimit-Reset impostato ad un numero
		// Tutte le richieste devono avere lo header X-RateLimit-Limit
		
		responses.forEach(r -> { 			
				assertTrue( Integer.valueOf(r.getHeaderFirstValue(Headers.RateLimitReset)) != null);
				Utils.checkXLimitHeader(logRateLimiting, Headers.RateLimitLimit, r.getHeaderFirstValue(Headers.RateLimitLimit), maxRequests);
				
				
				if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
					Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
					Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.RateLimitLimit), maxRequests, windowMap);
				}
			});

		
		// Tutte le richieste tranne una devono restituire 200
		
		assertEquals(maxRequests, responses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		
		// La richiesta fallita deve avere status code 429
		
		HttpResponse failedResponse = responses.stream().filter(r -> r.getResultHTTPOperation() == 429).findAny()
				.orElse(null);
		assertNotEquals(null, failedResponse);
		
		String body = new String(failedResponse.getContent());
		logRateLimiting.info(body);
		
		//org.w3c.dom.Element element = Utils.buildXmlElement(failedResponse.getContent());
		
		Utils.matchLimitExceededSoap(failedResponse.getContent());	
		
		assertEquals("0", failedResponse.getHeaderFirstValue(Headers.RateLimitRemaining));
		assertEquals(HeaderValues.LIMIT_EXCEEDED, failedResponse.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
		Utils.checkHeaderTooManyRequest(failedResponse);
		assertNotEquals(null, failedResponse.getHeaderFirstValue(Headers.RetryAfter));
		
		// Lo header X-RateLimit-Remaining deve assumere tutti i
		// i valori possibili da 0 a maxRequests-1
		
		List<Integer> counters = responses.stream()
				.map(resp -> Integer.parseInt(resp.getHeaderFirstValue(Headers.RateLimitRemaining))).collect(Collectors.toList());
		assertTrue(IntStream.range(0, maxRequests).allMatch(v -> counters.contains(v)));		
	}
	
	
	private void checkAssertionsRichiesteSimultanee(List<HttpResponse> responses, int maxConcurrentRequests) throws Exception {
		// Tutte le richieste tranne 1 devono restituire 200
		
		assertEquals(maxConcurrentRequests, responses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());

		// Tutte le richieste devono avere lo header GovWay-RateLimit-ConcurrentRequest-Limit=10
		// Tutte le richieste devono avere lo header ConcurrentRequestsRemaining impostato ad un numero positivo		
		
		responses.forEach(r -> {
			assertTrue(r.getHeaderFirstValue(Headers.ConcurrentRequestsLimit).equals(String.valueOf(maxConcurrentRequests)));
			assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.ConcurrentRequestsRemaining)) >= 0);
		});
			
		// La richiesta fallita deve avere status code 429
		
		HttpResponse failedResponse = responses.stream().filter(r -> r.getResultHTTPOperation() == 429).findAny()
				.orElse(null);
		assertNotEquals(null, failedResponse);
		
		String body = new String(failedResponse.getContent());
		logRateLimiting.info(body);
		
		//org.w3c.dom.Element element = Utils.buildXmlElement(failedResponse.getContent());
		Utils.matchTooManyRequestsSoap(failedResponse.getContent());
		
		assertEquals("0", failedResponse.getHeaderFirstValue(Headers.ConcurrentRequestsRemaining));
		assertEquals(HeaderValues.TOO_MANY_REQUESTS, failedResponse.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
		Utils.checkHeaderTooManyRequest(failedResponse);
		assertNotEquals(null, failedResponse.getHeaderFirstValue(Headers.RetryAfter));
	}

}
