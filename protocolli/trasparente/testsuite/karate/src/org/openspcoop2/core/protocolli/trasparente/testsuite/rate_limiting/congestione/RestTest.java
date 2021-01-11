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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.HeaderValues;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.HttpUtilsException;

import net.minidev.json.JSONObject;

public class RestTest extends ConfigLoader {

	private static final String basePath = System.getProperty("govway_base_path");
	private static final int durataCongestione = Integer.valueOf(System.getProperty("rate_limiting.congestione.durata_congestione"));
	private static final int sogliaCongestione = Integer.valueOf(System.getProperty("soglia_congestione"));
	
	@Test
	public void congestioneAttivaErogazione() {
		congestioneAttiva(basePath + "/SoggettoInternoTest/NumeroRichiesteRest/v1/no-policy?sleep=2000");
	}
	
	
	@Test
	public void congestioneAttivaFruizione() {
		congestioneAttiva(basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteRest/v1/no-policy?sleep=2000");
	}
	
	
	@Test
	public void congestioneAttivaConViolazioneRLErogazione() {
		final String idServizio = "SoggettoInternoTest/NumeroRichiesteRest/v1";
		congestioneAttivaConViolazioneRL(basePath + "/SoggettoInternoTest/NumeroRichiesteRest/v1/richieste-simultanee/?sleep=2000", idServizio);
	}
	
	@Test
	public void congestioneAttivaConViolazioneRLFruizione() {
		final String idServizio = "SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteRest/v1";
		congestioneAttivaConViolazioneRL(basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteRest/v1/richieste-simultanee?sleep=2000", idServizio);
	}
	
	@Test
	public void congestioneAttivaViolazioneRichiesteComplessiveErogazione() {
		congestioneAttivaViolazioneRichiesteComplessive(basePath + "/SoggettoInternoTest/NumeroRichiesteRest/v1/no-policy?sleep=2000");
		
	}
	
	@Test
	public void congestioneAttivaViolazioneRichiesteComplessiveFruizione() {
		congestioneAttivaViolazioneRichiesteComplessive(basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteRest/v1/no-policy?sleep=2000");
		
	}
	
	@Test
	public void rateLimitingInPresenzaCongestioneErogazione() throws Exception {
		rateLimitingInPresenzaCongestione(TipoServizio.EROGAZIONE);
	}
	
	
	@Test
	public void rateLimitingInPresenzaCongestioneFruizione() throws Exception {
		rateLimitingInPresenzaCongestione(TipoServizio.FRUIZIONE);
	}
	
	@Test
	public void rateLimitingInPresenzaDegradoGlobale() throws UtilsException, HttpUtilsException {
		rateLimitingInPresenzaDegrado("InPresenzaDegradoRest", 4000);
	}
	
	@Test
	public void rateLimitingInPresenzaDegradoServizio() throws UtilsException, HttpUtilsException {
		rateLimitingInPresenzaDegrado("InPresenzaDegradoServizioRest", 2100);
	}
	
	@Test
	public void rateLimitingInPresenzaDegradoECongestioneErogazione() {
		rateLimitingInPresenzaDegradoECongestione(TipoServizio.EROGAZIONE, "InPresenzaDegradoECongestioneRest", 4500);
	}
	
	@Test
	public void rateLimitingInPresenzaDegradoECongestioneFruizione() {
		rateLimitingInPresenzaDegradoECongestione(TipoServizio.FRUIZIONE, "InPresenzaDegradoECongestioneRest", 4500);
	}

	

	/** 
	 * 	Controlliamo che la policy di rate limiting venga applicata solo in
	 *  presenza di congestione.
	 *  
	 *	La policy di RL è: NumeroRichiesteCompletateConSuccesso.
	 */
	public void rateLimitingInPresenzaCongestione(TipoServizio tipoServizio) throws Exception {
		
		final int maxRequests = 5;
		final String erogazione = "InPresenzaCongestioneRest";
		final PolicyAlias policy = PolicyAlias.ORARIO;
		
		final String idPolicy = tipoServizio == TipoServizio.EROGAZIONE
				? dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy)
				: dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		
		final String urlServizio = tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
				
		final BiConsumer<String,PolicyAlias> testToRun = tipoServizio == TipoServizio.EROGAZIONE
				? org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest::testErogazione
				: org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest::testFruizione;
				
		Utils.resetCounters(idPolicy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		Utils.waitForPolicy(policy);

		// Faccio n richieste per superare la policy e controllo che non scatti,
		// perchè la congestione non è attiva.
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(urlServizio);
						
		Vector<HttpResponse> responses = Utils.makeRequestsAndCheckPolicy(request, maxRequests+1, idPolicy);
		
		// Controllo che non sia scattata la policy
		assertEquals( maxRequests+1, responses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		
		// Faccio attivare la congestione
		
		String url = basePath + "/SoggettoInternoTest/NumeroRichiesteRest/v1/no-policy?sleep="+durataCongestione;
		HttpRequest congestionRequest = new HttpRequest();
		congestionRequest.setContentType("application/json");
		congestionRequest.setMethod(HttpRequestMethod.GET);
		congestionRequest.setUrl(url);
		
		// In realtà qui dovrei assicurarmi che anche a test fallito, il thread pool venga terminato.
		// Altrimenti restiamo in congestione con delle richieste ancora attive all'inizio del test successivo.
		
		int count = sogliaCongestione;
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(count);

		for (int i = 0; i < count; i++) {
			executor.execute(() -> {
				try {
					logRateLimiting.info(congestionRequest.getMethod() + " " + congestionRequest.getUrl());
					 HttpResponse r = HttpUtilities.httpInvoke(congestionRequest);
					 assertEquals(200, r.getResultHTTPOperation());
					logRateLimiting.info("Richiesta effettuata..");
				} catch (UtilsException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			});
		}
		
		// Aspetto che il sistema vada in congestione..
		org.openspcoop2.utils.Utilities.sleep(Long.parseLong(System.getProperty("congestion_delay")));
		
		responses = Utils.makeSequentialRequests(request, maxRequests+1);
		
		// Tutte le risposte devono essere bloccate, perchè siamo in congestione
		// e le richieste iniziali sono state conteggiate
		assertEquals( maxRequests+1, responses.stream().filter(r -> r.getResultHTTPOperation() == 429).count());
		Utils.waitForZeroActiveRequests(idPolicy, maxRequests+1);

		// Nel mentre siamo in congestione rieseguo per intero il test sul Numero Richieste Completate con successo
		testToRun.accept(erogazione, PolicyAlias.ORARIO);
				
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logRateLimiting.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		
		// Terminata la congestione faccio n richieste che non devono essere bloccate
		responses = Utils.makeParallelRequests(request, maxRequests+1);
		
		// Controllo che non sia scattata la policy
		assertEquals( maxRequests+1, responses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		
	}
	
	
	/**
	 * Controlliamo che la policy di rate limiting venga applicata solo
	 * in presenza di degrado prestazionale, facendo scattare il degrado
	 * superando il tempo medio risposta globale

 		La policy di RL è: NumeroRichiesteCompletateConSuccesso.
 		
	 */
	public void rateLimitingInPresenzaDegrado(String erogazione, int attesa) throws UtilsException, HttpUtilsException {
		final int maxRequests = 5;
		final PolicyAlias policy = PolicyAlias.ORARIO;
		final int windowSize = Utils.getPolicyWindowSize(policy);
		
		final String idPolicyErogazione = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		final String idPolicyFruizione =  dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
				

		final String urlServizioErogazione = basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario?sleep="+String.valueOf(attesa);
		final String urlServizioFruizione =  basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario?sleep="+String.valueOf(attesa);
		
		Utils.resetCounters(idPolicyErogazione);
		Utils.resetCounters(idPolicyFruizione);
		Utils.waitForPolicy(policy);
		Utils.checkConditionsNumeroRichieste(idPolicyErogazione, 0, 0, 0);
		Utils.checkConditionsNumeroRichieste(idPolicyFruizione, 0, 0, 0);

		HttpRequest requestErogazione = new HttpRequest();
		requestErogazione.setContentType("application/json");
		requestErogazione.setMethod(HttpRequestMethod.GET);
		requestErogazione.setUrl(urlServizioErogazione);
								
		Vector<HttpResponse> degradoResponsesErogazione = Utils.makeParallelRequests(requestErogazione, maxRequests+1);
		assertEquals(maxRequests+1, degradoResponsesErogazione.stream().filter( r -> r.getResultHTTPOperation() == 200).count());
		logRateLimiting.info(Utils.getPolicy(idPolicyErogazione));
		
		HttpRequest requestFruizione = new HttpRequest();
		requestFruizione.setContentType("application/json");
		requestFruizione.setMethod(HttpRequestMethod.GET);
		requestFruizione.setUrl(urlServizioFruizione);
						
		Vector<HttpResponse> degradoResponsesFruizione = Utils.makeParallelRequests(requestFruizione, maxRequests+1);
		assertEquals(maxRequests+1, degradoResponsesFruizione.stream().filter( r -> r.getResultHTTPOperation() == 200).count());
		
		logRateLimiting.info(Utils.getPolicy(idPolicyErogazione));

		// Attendo in modo che le statistiche vengano aggiornate e il degrado prestazionale
		// rilevato.
		Utils.waitForDbStats();
		
		// Faccio le ulteriori richieste per far scattare la policy sulla erogazione
		makeParallelRequests_and_checkFailedRequests(requestErogazione, windowSize, maxRequests, null);
		logRateLimiting.info(Utils.getPolicy(idPolicyErogazione));
		
		// Faccio le ulteriori richieste per far scattare la policy sulla fruizione
		makeParallelRequests_and_checkFailedRequests(requestFruizione, windowSize, maxRequests, null);
		logRateLimiting.info(Utils.getPolicy(idPolicyFruizione));
	}
	
	/**
	 * Controlliamo che la policy di rate limiting venga applicata solo
	 * se contemporaneamente attivi il degrado prestazionale, e la congestione
	 */
	public void rateLimitingInPresenzaDegradoECongestione(TipoServizio tipoServizio, String erogazione, int attesa) {
		
		final int maxRequests = 5;
		final PolicyAlias policy = PolicyAlias.ORARIO;
		final int windowSize = Utils.getPolicyWindowSize(policy);
		
		final String idPolicy = tipoServizio == TipoServizio.EROGAZIONE
				? dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy)
				: dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
				

		final String urlServizio = tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario?sleep="+String.valueOf(attesa)
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario?sleep="+String.valueOf(attesa);
		
		// Utils.waitForDbStats();
		
		Utils.resetCounters(idPolicy);
		Utils.waitForPolicy(policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(urlServizio);
				
		
		Vector<HttpResponse> degradoResponses = Utils.makeParallelRequests(request, maxRequests);
		
		assertEquals(maxRequests, degradoResponses.stream().filter( r -> r.getResultHTTPOperation() == 200).count());
		
		// Attendo in modo che le statistiche vengano aggiornate e il degrado prestazionale
		// rilevato.
		Utils.waitForDbStats();
		
		// Faccio n richieste che non devono ancora essere bloccate perchè non in congestione.
		logRateLimiting.info("Faccio n richieste parallele e nessuna viene bloccata perchè non ancora in congestione...");
		Vector<HttpResponse> stillNonBlockedResponses = Utils.makeParallelRequests(request, maxRequests);

		assertEquals(maxRequests, stillNonBlockedResponses.stream().filter( r -> r.getResultHTTPOperation() == 200).count());
		
		
		// Faccio attivare la congestione
		String url = basePath + "/SoggettoInternoTest/NumeroRichiesteRest/v1/no-policy?sleep="+durataCongestione;
		HttpRequest congestionRequest = new HttpRequest();
		congestionRequest.setContentType("application/json");
		congestionRequest.setMethod(HttpRequestMethod.GET);
		congestionRequest.setUrl(url);
		
		// faccio n richieste che devono essere tutte bloccate
		//Vector<HttpResponse> blockedResponses = Utils.makeParallelRequests(request, maxRequests);
		//org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkFailedRequests(blockedResponses, windowSize, maxRequests);
		makeParallelRequests_and_checkFailedRequests(request, windowSize, maxRequests, congestionRequest);
		
		logRateLimiting.info(Utils.getPolicy(idPolicy));
		
		// TODO: Adesso testare che una volta passato il degrado, con la sola 
		//	congestione le richieste non vengono bloccate.
	}
	
	private static void makeParallelRequests_and_checkFailedRequests(HttpRequest request, int windowSize, int maxRequests, HttpRequest congestionRequest) {
		
		JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
		
		// Siccome le policy hanno un applicabilita con degrado sono sull'intervallo statistico corrente, potrebbe succedere che nel momento in cui avviene la richiesta, 
		// il timer ha eliminato le informazioni statistiche sulla data corrente per aggiornarle.
		// Questo provoca che il tempo di risposta è 0 o basso poichè non considera i test effettuati
		// Il test in caso di fallimento verrà quindi ripetuto tre volete prima di assumere che sia fallito.
		// Si spera che in queste tre volte non si rientri nel caso limite.
		// A regime non ha senso fare una policy sull'intervallo statistico basato sul'intervallo corrente con finestra uguale a 1.
		// Lo si è fatto solo per motivi di test.
		
		boolean ok = false;
		
		for (int i = 0; i < 3; i++) {
						
			ThreadPoolExecutor executor = null;
			try {
				if(congestionRequest!=null) {
					// faccio richieste per attivare la congestione
					logRateLimiting.info("["+i+"] Faccio andare in congestione il sistema ...");
					executor = congestionRequest(congestionRequest);
					// Aspetto che il sistema vada in congestione..
					org.openspcoop2.utils.Utilities.sleep(Long.parseLong(System.getProperty("congestion_delay")));
				}
			
				logRateLimiting.info("["+i+"] Invocazione ...");
				Vector<HttpResponse> responses = Utils.makeParallelRequests(request, maxRequests);
				
				if(responses==null || responses.isEmpty() || responses.get(0)==null || responses.get(0).getHeader(Headers.RequestSuccesfulLimit)==null) {
					logRateLimiting.info("["+i+"] la risposta non contiene l'header '"+Headers.RequestSuccesfulLimit+"' ; riprovo tra 2 secondi attendendo che le statistiche girano");
					Utilities.sleep(2000);
					continue;
				}
				
				logRateLimiting.info("["+i+"] Verifico header ...");
			
				for (var r: responses) {
					Utils.checkXLimitHeader(logRateLimiting, Headers.RequestSuccesfulLimit, r.getHeader(Headers.RequestSuccesfulLimit), maxRequests);			
					if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
						Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
						Utils.checkXLimitWindows(r.getHeader(Headers.RequestSuccesfulLimit), maxRequests, windowMap);
					}
					assertTrue(Integer.valueOf(r.getHeader(Headers.RequestSuccesfulReset)) <= windowSize);
					assertEquals(429, r.getResultHTTPOperation());
					
					try {
						JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(r.getContent()));
						
						assertEquals("https://govway.org/handling-errors/429/LimitExceeded.html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
						assertEquals("LimitExceeded", jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
						assertEquals(429, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
						assertNotEquals(null, jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0));	
						assertEquals("Limit exceeded detected", jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0));
						
						assertEquals("0", r.getHeader(Headers.RequestSuccesfulRemaining));
						assertEquals(HeaderValues.LimitExceeded, r.getHeader(Headers.GovWayTransactionErrorType));
						assertEquals(HeaderValues.ReturnCodeTooManyRequests, r.getHeader(Headers.ReturnCode));
						assertNotEquals(null, r.getHeader(Headers.RetryAfter));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				ok = true;
				break;
			}finally {
				if(executor!=null) {
					try {
						executor.shutdown();
						executor.awaitTermination(20, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						logRateLimiting.error("Le richieste hanno impiegato più di venti secondi!");
						throw new RuntimeException(e);
					}
				}
			}
		}

		assertTrue(ok);
	}

	private static ThreadPoolExecutor congestionRequest(HttpRequest congestionRequest) {
		int count = sogliaCongestione;
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(count);

		for (int i = 0; i < count; i++) {
			executor.execute(() -> {
				try {
					logRateLimiting.info(congestionRequest.getMethod() + " " + congestionRequest.getUrl());
					 HttpResponse r = HttpUtilities.httpInvoke(congestionRequest);
					 assertEquals(200, r.getResultHTTPOperation());
					logRateLimiting.info("Richiesta effettuata..");
				} catch (UtilsException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			});
		}
		
		return executor;
	}
	
	
	/** 
	 * 	Qui si testa la generazione dell'evento di congestione e del successivo evento
	 *  che segnala la violazione del massimo numero di richieste simultanee
	 */
	public void congestioneAttivaViolazioneRichiesteComplessive(String url) {
			
		EventiUtils.waitForDbEvents();
				
		final int sogliaRichiesteSimultanee = Integer.valueOf(System.getProperty("soglia_richieste_simultanee"));
		
		LocalDateTime dataSpedizione = LocalDateTime.now();
		
		ZonedDateTime zdt = dataSpedizione.atZone(ZoneId.systemDefault());
		
		logRateLimiting.info(" Epoch Millis: " + zdt.toInstant().toEpochMilli());
		logRateLimiting.info(" Sistem Millis: " + System.currentTimeMillis());
		logRateLimiting.info(" LocalDateTimeDate: " + dataSpedizione);
		
		//Calendar dataSpedizione = Calendar.getInstance();
		//java.sql.Date dataSpedizione = new java.sql.Date(now.getTimeInMillis());
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(url);
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, sogliaRichiesteSimultanee+1);

		assertEquals(sogliaRichiesteSimultanee, responses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		
		SoapTest.checkCongestioneAttivaViolazioneRichiesteComplessive(dataSpedizione, responses);
	}		
	
	
	/**
	 * Controlla che il sistema registri gli eventi di congestione
	 * e gli eventi di violazione di una policy di rate limiting.
	 * 
	 */
	public void congestioneAttivaConViolazioneRL(String url, String idErogazione) {
		EventiUtils.waitForDbEvents();

		
		final int sogliaRichiesteSimultanee = 10;
		
		// Affinchè il test faccia scattare tutti e due gli eventi è necessario
		// che la soglia di congestione sia più bassa della soglia di RL
		assertTrue(sogliaRichiesteSimultanee > sogliaCongestione);
		
		LocalDateTime dataSpedizione = LocalDateTime.now();		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(url);
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, sogliaRichiesteSimultanee+1);
		
		EventiUtils.checkEventiCongestioneAttivaConViolazioneRL(idErogazione, dataSpedizione, Optional.empty(), responses, logRateLimiting);
	}
	
	
	/**
	 * Controlla che il sistema entri effettivamente in congestione.
	 * 
	 */
	
	public void congestioneAttiva(String url) {
		EventiUtils.waitForDbEvents();
		
		LocalDateTime dataSpedizione = LocalDateTime.now();
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(url);
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, sogliaCongestione+1);
		
		assertEquals(sogliaCongestione+1, responses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		EventiUtils.checkEventiCongestioneAttiva(dataSpedizione, responses, logRateLimiting);
	
	}
}
