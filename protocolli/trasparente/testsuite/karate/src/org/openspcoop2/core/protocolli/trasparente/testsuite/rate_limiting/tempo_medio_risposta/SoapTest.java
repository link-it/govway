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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.tempo_medio_risposta;

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
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {

	final static String erogazione = "TempoMedioRispostaSoap";
	
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
		
		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		Commons.checkPreConditionsTempoMedioRisposta(idPolicy);
		
		Utils.waitForPolicy(policy);
		
		// Faccio prima 3 richieste che passano
		
		String body = SoapBodies.get(policy);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1?sleep="+small_delay );
		
		List<HttpResponse> notBlockedResponses = Utils.makeParallelRequests(request, small_delay_count);
		
		// Poi faccio una richiesta che fa scattare la policy
		
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1?sleep="+big_delay );
		
		notBlockedResponses.add(HttpUtilities.httpInvoke(request));
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, small_delay_count+1, 0);
		
		// Poi faccio n richieste che non passano
		
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+erogazione+"/v1?sleep="+small_delay );
		
		List<HttpResponse> blockedResponses = Utils.makeParallelRequests(request, small_delay_count);
		
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
		
		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		Commons.checkPreConditionsTempoMedioRisposta(idPolicy);
		
		Utils.waitForPolicy(policy);
		
		// Faccio prima 3 richieste che passano
		
		String body = SoapBodies.get(policy);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1?sleep="+small_delay );
		
		List<HttpResponse> notBlockedResponses = Utils.makeParallelRequests(request, small_delay_count);
		
		// Poi faccio una richiesta che fa scattare la policy
		
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1?sleep="+big_delay );
		
		notBlockedResponses.add(HttpUtilities.httpInvoke(request));
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, small_delay_count+1, 0);
		
		// Poi faccio n richieste che non passano
		
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(body.getBytes());
		request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1?sleep="+small_delay );
		
		List<HttpResponse> blockedResponses = Utils.makeParallelRequests(request, small_delay_count);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, small_delay_count+1, small_delay_count);
		checkPassedRequests(notBlockedResponses, windowSize, soglia);
		checkBlockedRequests(blockedResponses, windowSize, soglia);
	}

	
	private void checkPassedRequests(List<HttpResponse> responses, int windowSize, int soglia) {
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
	
	private void checkBlockedRequests(List<HttpResponse> responses, int windowSize, int soglia) throws Exception {
		
		for (var r: responses) {
			Utils.checkXLimitHeader(logRateLimiting, Headers.AvgTimeResponseLimit, r.getHeaderFirstValue(Headers.AvgTimeResponseLimit), soglia);			
			if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
				Map<Integer,Integer> windowMap = Map.of(windowSize,soglia);							
				Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.AvgTimeResponseLimit), soglia, windowMap);
			}
			
			assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.AvgTimeResponseReset)) <= windowSize);
			assertEquals(429, r.getResultHTTPOperation());
	
			//org.w3c.dom.Element element = Utils.buildXmlElement(r.getContent());
			Utils.matchLimitExceededSoap(r.getContent());
			
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
