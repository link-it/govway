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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
	
	private static final int durata_simultanee = Integer.valueOf(System.getProperty("rate_limiting.numero_richieste.durata_simultanee"));
	
	public void richiestePerMinutoErogazione(boolean disclosure) throws Exception {
		logRateLimiting.info("Test richieste per minuto");
		final int maxRequests = 5;

		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.MINUTO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.MINUTO);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		// Aspetto lo scoccare del minuto
		
		Utils.waitForNewMinute();
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/NumeroRichiesteRest/v1/minuto");
						
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
		
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests);
		
		responses.addAll(Utils.makeParallelRequests(request, 5));
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5);	
		checkAssertionsNumeroRichieste(responses, maxRequests, 60, disclosure);
	}
	
	@Test
	public void richiestePerMinutoDefaultErogazione() throws Exception {
		this.richiestePerMinutoDefaultErogazione(null);
	}
	
	public void richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(policyType!=null && !policyType.isSupportedResource(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE)) {
			logRateLimiting.warn("Test numeroRichiesteFalliteErogazione con policy type '"+policyType+"' non effettuato poichè non supportato dal gestore");
			return;
		}
		
		try {
		
			logRateLimiting.info("Test richieste per minuto");
			final int maxRequests = 10;
	
			dbUtils.setEngineTypeErogazione("SoggettoInternoTest", "NumeroRichiesteRest", policyType);
			
			long idErogazione = dbUtils.getIdErogazione("SoggettoInternoTest", "NumeroRichiesteRest");
			Utils.ripulisciRiferimentiCacheErogazione(idErogazione);
					
			HttpRequest request = new HttpRequest();
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/NumeroRichiesteRest/v1/minuto-default");
			
			// attivo nuovo motore
			Utils.makeParallelRequests(request, 1);
			
			String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.MINUTODEFAULT);
			Utils.resetCounters(idPolicy);
			
			idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.MINUTODEFAULT);
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE );
			
			// Aspetto lo scoccare del minuto
			
			Utils.waitForNewMinute();
							
			Vector<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
			
			Utils.waitForZeroActiveRequests(idPolicy, maxRequests, policyType);
			
			responses.addAll(Utils.makeParallelRequests(request, 5));
			
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE);	
			checkAssertionsNumeroRichieste(responses, maxRequests, 60, false, policyType);
			
		}finally {
			
			// ripristino
			
			dbUtils.setEngineTypeErogazione("SoggettoInternoTest", "NumeroRichiesteRest", null);
			
			long idErogazione = dbUtils.getIdErogazione("SoggettoInternoTest", "NumeroRichiesteRest");
			Utils.ripulisciRiferimentiCacheErogazione(idErogazione);
			
		}
	}
	
	
	@Test
	public void richiestePerMinutoErogazioneNoDisclosure() throws Exception {
		this.richiestePerMinutoErogazione(false);
	}
	
	/*@Test
	public void richiestePerMinutoErogazioneDisclosure() throws Exception {
		Utils.toggleErrorDisclosure(true);
		this.richiestePerMinutoErogazione(true);
		Utils.toggleErrorDisclosure(false);
	}*/
	
	
	public void richiesteOrarieErogazione(boolean disclosure) throws Exception {
		logRateLimiting.info("Test richieste per ora");
		final int maxRequests = 10;

		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.ORARIO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.ORARIO);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		Utils.waitForNewHour();
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/NumeroRichiesteRest/v1/orario");
						
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
		
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests);
		
		responses.addAll(Utils.makeParallelRequests(request, 5));
		
		checkAssertionsNumeroRichieste(responses, maxRequests,3600, disclosure);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5);
	}
	
	@Test
	public void richiesteOrarieErogazioneNoDisclosure() throws Exception {
		this.richiesteOrarieErogazione(false);
	}
	
	
	/*@Test
	public void richiesteOrarieErogazioneDisclosure() throws Exception {
		Utils.toggleErrorDisclosure(true);
		this.richiesteOrarieErogazione(true);
		Utils.toggleErrorDisclosure(false);
	}*/
	
	
	public void richiesteGiornaliereErogazione(boolean disclosure) throws Exception {
		logRateLimiting.info("Test richieste per ora");
		final int maxRequests = 10;

		String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.GIORNALIERO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.GIORNALIERO);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		Utils.waitForNewDay();
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl( System.getProperty("govway_base_path") + "/SoggettoInternoTest/NumeroRichiesteRest/v1/giornaliero");
						
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
		
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests);
		
		responses.addAll(Utils.makeParallelRequests(request, 5));
		
		checkAssertionsNumeroRichieste(responses, maxRequests, 86400, disclosure);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5);
	}
	
	
	@Test
	public void richiesteGiornaliereErogazioneNoDisclosure() throws Exception {
		this.richiesteGiornaliereErogazione(false);
	}
	
	
	/*@Test
	public void richiesteGiornaliereErogazioneDisclosure() throws Exception {
		Utils.toggleErrorDisclosure(true);
		this.richiesteGiornaliereErogazione(true);
		Utils.toggleErrorDisclosure(false);
	}*/
	
	
	public void richiestePerMinutoFruizione(boolean disclosure) throws Exception {
		logRateLimiting.info("Test richieste per minuto fruizione");
		final int maxRequests = 5;

		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.MINUTO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.MINUTO);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		// Aspetto lo scoccare del minuto
		
		Utils.waitForNewMinute();
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteRest/v1/minuto");
						
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
		
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests);
		
		responses.addAll(Utils.makeParallelRequests(request, 5));
		
		checkAssertionsNumeroRichieste(responses, maxRequests, 60, disclosure);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5);
	}
	
	@Test
	public void richiestePerMinutoDefaultFruizione() throws Exception {
		richiestePerMinutoDefaultFruizione(null);
	}
	public void richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(policyType!=null && !policyType.isSupportedResource(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE)) {
			logRateLimiting.warn("Test numeroRichiesteFalliteFruizione con policy type '"+policyType+"' non effettuato poichè non supportato dal gestore");
			return;
		}
				
		try {
		
			logRateLimiting.info("Test richieste per minuto fruizione");
			final int maxRequests = 10;
	
			dbUtils.setEngineTypeFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest", policyType);
			
			long idFruizione = dbUtils.getIdFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest");
			Utils.ripulisciRiferimentiCacheFruizione(idFruizione);
					
			HttpRequest request = new HttpRequest();
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteRest/v1/minuto-default");
			
			// attivo nuovo motore
			Utils.makeParallelRequests(request, 1);
			
			
			String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.MINUTODEFAULT);
			Utils.resetCounters(idPolicy);
			
			idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.MINUTODEFAULT);
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE);
			
			// Aspetto lo scoccare del minuto
			
			Utils.waitForNewMinute();
							
			Vector<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
			
			Utils.waitForZeroActiveRequests(idPolicy, maxRequests, policyType);
			
			responses.addAll(Utils.makeParallelRequests(request, 5));
			
			checkAssertionsNumeroRichieste(responses, maxRequests, 60, false, policyType);
			Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5, policyType, TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE);
			
		}finally {
			
			// ripristino
			
			dbUtils.setEngineTypeFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest", null);
			
			long idFruizione = dbUtils.getIdFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest");
			Utils.ripulisciRiferimentiCacheFruizione(idFruizione);
			
		}
	}
	
	
	@Test
	public void richiestePerMinutoFruizioneNoDisclosure() throws Exception {
		this.richiestePerMinutoFruizione(false);
	}
	
	
	/*@Test
	public void richiestePerMinutoFruizioneDisclosure() throws Exception {
		Utils.toggleErrorDisclosure(true);
		this.richiestePerMinutoFruizione(true);
		Utils.toggleErrorDisclosure(false);
	}*/
	
	
	public void richiesteOrarieFruizione(boolean disclosure) throws Exception {
		logRateLimiting.info("Test richieste orarie fruizione");
		final int maxRequests = 10;

		
		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.ORARIO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.ORARIO);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		// Aspetto lo scoccare del minuto
		
		Utils.waitForNewHour();
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteRest/v1/orario");
						
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
		
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests);
		
		responses.addAll(Utils.makeParallelRequests(request, 5));
		
		checkAssertionsNumeroRichieste(responses, maxRequests, 3600, disclosure);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5);
	}
	
	
	@Test
	public void richiesteOrarieFruizioneNoDisclosure() throws Exception {
		this.richiesteOrarieFruizione(false);
	}
	
	
	/*@Test
	public void richiesteOrarieFruizioneDisclosure() throws Exception {
		Utils.toggleErrorDisclosure(true);
		this.richiesteOrarieFruizione(true);
		Utils.toggleErrorDisclosure(false);
	}*/
	
	
	public void richiesteGiornaliereFruizione(boolean disclosure) throws Exception {
		logRateLimiting.info("Test richieste giornaliere fruizione");
		final int maxRequests = 10;

		String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.GIORNALIERO);
		Utils.resetCounters(idPolicy);
		
		idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.GIORNALIERO);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		// Aspetto lo scoccare del minuto
		
		Utils.waitForNewDay();
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl( System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteRest/v1/giornaliero");
						
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
		
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests);
		
		responses.addAll(Utils.makeParallelRequests(request, 5));
		
		checkAssertionsNumeroRichieste(responses, maxRequests, 86400, disclosure);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 5);
	}
	
	
	@Test
	public void richiesteGiornaliereFruizioneNoDisclosure() throws Exception {
		this.richiesteGiornaliereFruizione(false);
	}
	
	
	/*@Test
	public void richiesteGiornaliereFruizioneDisclosure() throws Exception {
		Utils.toggleErrorDisclosure(true);
		this.richiesteGiornaliereFruizione(true);
		Utils.toggleErrorDisclosure(false);
	}*/
	
	

	@Test
	public void richiesteSimultaneeErogazione() throws Exception {
		richiesteSimultaneeErogazione(null);
	}
	public void richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(policyType!=null && !policyType.isSupportedResource(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE)) {
			logRateLimiting.warn("Test richiesteSimultaneeErogazione con policy type '"+policyType+"' non effettuato poichè non supportato dal gestore");
			return;
		}
		
		try {
		
			final int maxConcurrentRequests = 10;
			
			dbUtils.setEngineTypeErogazione("SoggettoInternoTest", "NumeroRichiesteRest", policyType);
			
			long idErogazione = dbUtils.getIdErogazione("SoggettoInternoTest", "NumeroRichiesteRest");
			Utils.ripulisciRiferimentiCacheErogazione(idErogazione);
					
			HttpRequest request = new HttpRequest();
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/NumeroRichiesteRest/v1/richieste-simultanee?sleep="+durata_simultanee);
			
			// attivo nuovo motore
			Utils.makeParallelRequests(request, 1);
			
			
			String idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.RICHIESTE_SIMULTANEE);
			Utils.resetCounters(idPolicy);
			
			idPolicy = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.RICHIESTE_SIMULTANEE);
			Commons.checkPreConditionsRichiesteSimultanee(idPolicy, policyType);
			
			Vector<HttpResponse> responses = Utils.makeParallelRequests(request, maxConcurrentRequests + 1);
			
			checkAssertionsRichiesteSimultanee(responses, maxConcurrentRequests, policyType);
			Commons.checkPostConditionsRichiesteSimultanee(idPolicy, policyType);
			
		}finally {
			
			// ripristino
			
			dbUtils.setEngineTypeErogazione("SoggettoInternoTest", "NumeroRichiesteRest", null);
			
			long idErogazione = dbUtils.getIdErogazione("SoggettoInternoTest", "NumeroRichiesteRest");
			Utils.ripulisciRiferimentiCacheErogazione(idErogazione);
			
		}
	}
	
	
	@Test
	public void richiesteSimultaneeFruizione() throws Exception {
		richiesteSimultaneeFruizione(null);
	}
	public void richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType policyType) throws Exception {
		
		if(policyType!=null && !policyType.isSupportedResource(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE)) {
			logRateLimiting.warn("Test richiesteSimultaneeErogazione con policy type '"+policyType+"' non effettuato poichè non supportato dal gestore");
			return;
		}
		
		try {
		
			final int maxConcurrentRequests = 10;
			
			dbUtils.setEngineTypeFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest", policyType);
			
			long idFruizione = dbUtils.getIdFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest");
			Utils.ripulisciRiferimentiCacheFruizione(idFruizione);
					
			HttpRequest request = new HttpRequest();
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteRest/v1/richieste-simultanee?sleep="+durata_simultanee);
			
			// attivo nuovo motore
			Utils.makeParallelRequests(request, 1);
			
			String idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.RICHIESTE_SIMULTANEE);
			Utils.resetCounters(idPolicy);
	
			idPolicy = dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest", PolicyAlias.RICHIESTE_SIMULTANEE);
			Commons.checkPreConditionsRichiesteSimultanee(idPolicy, policyType);
	
			Vector<HttpResponse> responses = Utils.makeParallelRequests(request, maxConcurrentRequests + 1);
			
			checkAssertionsRichiesteSimultanee(responses, maxConcurrentRequests, policyType);
			Commons.checkPostConditionsRichiesteSimultanee(idPolicy, policyType);
			
		}finally {
			
			// ripristino
			
			dbUtils.setEngineTypeFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest", null);
			
			long idFruizione = dbUtils.getIdFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", "NumeroRichiesteRest");
			Utils.ripulisciRiferimentiCacheFruizione(idFruizione);
			
		}
	}
	
	@Test
	public void richiesteSimultaneeGlobali() throws Exception {
		final int maxConcurrentRequests = Integer.valueOf(System.getProperty("soglia_richieste_simultanee"));
				
		// Aspetto che i threads attivi sul server siano 0
		Utils.waitForZeroGovWayThreads();
		
		// Effettuo le n richieste
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteRest/v1/no-policy?sleep="+durata_simultanee);
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, maxConcurrentRequests + 1);
		
		HttpResponse failedResponse = responses.stream().filter(r -> r.getResultHTTPOperation() == 429).findAny()
				.orElse(null);
		assertNotEquals(null,failedResponse);
		
		JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(failedResponse.getContent()));
		JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
		
		assertEquals("https://govway.org/handling-errors/429/TooManyRequests.html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
		assertEquals("TooManyRequests", jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
		assertEquals(429, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
		assertEquals("Too many requests detected", jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0));
		assertNotEquals(null, jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0));
		
		assertNotEquals(null, failedResponse.getHeaderFirstValue(Headers.RetryAfter));
		assertEquals(HeaderValues.TOO_MANY_REQUESTS, failedResponse.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
		Utils.checkHeaderTooManyRequest(failedResponse);

		
		Utils.waitForZeroGovWayThreads();		
	}
	
	
	/*@Test
	public void richiesteSimultaneeRipetuto() throws Exception {
		for(int i=0;i<20;i++) {
			richiesteSimultaneeErogazione();
		}
		
		for(int i=0;i<20;i++) {
			richiesteSimultaneeFruizione();
		}
	}*/

	
	private void checkAssertionsRichiesteSimultanee(Vector<HttpResponse> responses, int maxConcurrentRequests, PolicyGroupByActiveThreadsType policyType) throws Exception {
		// Tutte le richieste tranne 1 devono restituire 200
		
		assertEquals(maxConcurrentRequests, responses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());

		// Tutte le richieste devono avere lo header GovWay-RateLimit-ConcurrentRequest-Limit=10
		// Tutte le richieste devono avere lo header ConcurrentRequestsRemaining impostato ad un numero positivo		
		
		responses.forEach(r -> {
			assertEquals(String.valueOf(maxConcurrentRequests), r.getHeaderFirstValue(Headers.ConcurrentRequestsLimit));
			assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.ConcurrentRequestsRemaining)) >= 0);
		});
			
		// La richiesta fallita deve avere status code 429
		
		HttpResponse failedResponse = responses.stream().filter(r -> r.getResultHTTPOperation() == 429).findAny()
				.orElse(null);
		assertNotEquals(null,failedResponse);
		
		JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(failedResponse.getContent()));
		JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
		
		assertEquals("https://govway.org/handling-errors/429/TooManyRequests.html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
		assertEquals("TooManyRequests", jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
		assertEquals(429, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
		assertEquals("Too many requests detected", jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0));
		assertNotEquals(null, jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0));
		

		assertEquals("0", failedResponse.getHeaderFirstValue(Headers.ConcurrentRequestsRemaining));
		assertEquals(HeaderValues.TOO_MANY_REQUESTS, failedResponse.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
		Utils.checkHeaderTooManyRequest(failedResponse);
		assertNotEquals(null, failedResponse.getHeaderFirstValue(Headers.RetryAfter));
	}

	
	private void checkAssertionsNumeroRichieste(Vector<HttpResponse> responses, int maxRequests, int windowSize, boolean disclosure) throws Exception {
		checkAssertionsNumeroRichieste(responses, maxRequests, windowSize, disclosure, PolicyGroupByActiveThreadsType.LOCAL);
	}
	private void checkAssertionsNumeroRichieste(Vector<HttpResponse> responses, int maxRequests, int windowSize, boolean disclosure, PolicyGroupByActiveThreadsType policyType) throws Exception {

		if(policyType.isInconsistent()) {
			// numero troppo casuali
			return;
		}
		
		
		// Tutte le richieste devono avere lo header X-RateLimit-Reset impostato ad un numero
		// Tutte le richieste devono avere lo header X-RateLimit-Limit
		
		responses.forEach(r -> { 			
				
				Utils.checkXLimitHeader(logRateLimiting, Headers.RateLimitLimit, r.getHeaderFirstValue(Headers.RateLimitLimit), maxRequests);
				
				if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
					Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
					Utils.checkXLimitWindows(r.getHeaderFirstValue(Headers.RateLimitLimit), maxRequests, windowMap);
				}
				
				assertTrue(Integer.valueOf(r.getHeaderFirstValue(Headers.RateLimitReset)) <= windowSize);
				assertNotEquals(null, Integer.valueOf(r.getHeaderFirstValue(Headers.RateLimitRemaining)));
			});

		// Tutte le richieste tranne una devono restituire 200
		
		assertEquals(maxRequests,responses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());

		
		// La richiesta fallita deve avere status code 429
		
		HttpResponse failedResponse = responses.stream().filter(r -> r.getResultHTTPOperation() == 429).findAny()
				.orElse(null);
		assertNotEquals(null,failedResponse);
		
		JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(failedResponse.getContent()));
		JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
		logRateLimiting.info(new String(failedResponse.getContent()));
		
		assertEquals("https://govway.org/handling-errors/429/LimitExceeded.html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
		assertEquals("LimitExceeded", jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
		assertEquals(429, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
		assertNotEquals(null, jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0));
		
		if (disclosure) {
			assertEquals("Servizio Temporaneamente Non Erogabile [CP02]", jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0));			
		}
		else {
			assertEquals("Limit exceeded detected", jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0));
		}
		
		assertEquals("0", failedResponse.getHeaderFirstValue(Headers.RateLimitRemaining));
		assertEquals(HeaderValues.LIMIT_EXCEEDED, failedResponse.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
		Utils.checkHeaderTooManyRequest(failedResponse);
		assertNotEquals(null, failedResponse.getHeaderFirstValue(Headers.RetryAfter));

		// Lo header X-RateLimit-Remaining deve assumere tutti i
		// i valori possibili da 0 a maxRequests-1
		List<Integer> counters = responses.stream()
				.map(resp -> Integer.parseInt(resp.getHeaderFirstValue(Headers.RateLimitRemaining))).collect(Collectors.toList());
		logRateLimiting.debug("HEADER SIZE ("+policyType+"): ["+(counters!=null ? counters.size() : -1)+"] ["+counters+"]");
		if(policyType.isExact()) {
			assertTrue(IntStream.range(0, maxRequests).allMatch(v -> counters.contains(v)));
		}
		else {
			// andando in parallelo le richieste si sovrappongono e i numeri non si riescono a stabilire a priori
			List<Integer> find = new ArrayList<Integer>();
			for (Integer v : counters) {
				if(counters.contains(v) && !find.contains(v)) {
					find.add(v);
				}
			}
			//System.out.println("FIND ["+find+"]");
			assertTrue(find.size()>=2); // verifico una distribuzione almeno di 2 numeri
		}
	}
	
}
