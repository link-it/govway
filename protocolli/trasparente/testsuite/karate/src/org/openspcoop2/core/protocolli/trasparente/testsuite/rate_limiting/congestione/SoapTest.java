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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.SoapBodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.HttpUtilsException;
import org.w3c.dom.Element;
/**
 * 
 * @author Francesco Scarlato {scarlato@link.it}
 *
 */
public class SoapTest extends ConfigLoader {

	private static final String basePath = System.getProperty("govway_base_path");
	private static final int durataCongestione = Integer.valueOf(System.getProperty("rate_limiting.congestione.durata_congestione"));
	private static final int sogliaCongestione = Integer.valueOf(System.getProperty("soglia_congestione"));
	
	/**
	 * Questi primi due forse posso toglierli visto che gli altri quattro test comunque controllano
	 * la scrittura degli eventi di congestione sul db. 
	 */
	@Test
	public void congestioneAttivaErogazione() {
		congestioneAttiva(basePath + "/SoggettoInternoTest/NumeroRichiesteSoap/v1?sleep=2000");
	}
	
	
	@Test
	public void congestioneAttivaFruizione() {
		congestioneAttiva(basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteSoap/v1?sleep=2000");
	}
	
	@Test
	public void congestioneAttivaConViolazioneRLErogazione() {
		congestioneAttivaConViolazioneRL(basePath + "/SoggettoInternoTest/NumeroRichiesteSoap/v1?sleep=2000", "SoggettoInternoTest/NumeroRichiesteSoap/v1");
	}
	
	@Test
	public void congestioneAttivaConViolazioneRLFruizione() {
		congestioneAttivaConViolazioneRL(basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteSoap/v1?sleep=2000", "SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteSoap/v1");
	}
	
	@Test
	public void congestioneAttivaViolazioneRichiesteComplessiveErogazione() {
		String url = basePath + "/SoggettoInternoTest/NumeroRichiesteSoap/v1?sleep=2000";
		congestioneAttivaViolazioneRichiesteComplessive(url);
	}
	
	
	@Test
	public void congestioneAttivaViolazioneRichiesteComplessiveFruizione() {
		String url = basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteSoap/v1?sleep=2000";
		congestioneAttivaViolazioneRichiesteComplessive(url);
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
		rateLimitingInPresenzaDegrado("InPresenzaDegradoSoap", 4000);
	}

	
	@Test
	public void rateLimitingInPresenzaDegradoServizio() throws UtilsException, HttpUtilsException {
		rateLimitingInPresenzaDegrado("InPresenzaDegradoServizioSoap", 2100);
	}
	

	@Test
	public void rateLimitingInPresenzaDegradoECongestioneErogazione() { 
		rateLimitingInPresenzaDegradoECongestione(TipoServizio.EROGAZIONE, "InPresenzaDegradoECongestioneSoap", 4500);
	}
	
	@Test
	public void rateLimitingInPresenzaDegradoECongestioneFruizione() {
		rateLimitingInPresenzaDegradoECongestione(TipoServizio.FRUIZIONE, "InPresenzaDegradoECongestioneSoap", 4500);
	}
	
	
	/**
	 * Controlliamo che la policy di rate limiting venga attivata solo in caso di degrado prestazionale.

	 * @param tipoServizio
	 * @param erogazione
	 * @param attesa
	 */
	private void rateLimitingInPresenzaDegrado(String erogazione, int attesa) {
		
		final int maxRequests = 5;
		final PolicyAlias policy = PolicyAlias.ORARIO;
		final int windowSize = Utils.getPolicyWindowSize(policy);
		
		final String idPolicyErogazione = dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);
		final String idPolicyFruizione =  dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
				

		final String urlServizioErogazione = basePath + "/SoggettoInternoTest/"+erogazione+"/v1?sleep="+String.valueOf(attesa);
		final String urlServizioFruizione =  basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1?sleep="+String.valueOf(attesa);
		
		Utils.resetCounters(idPolicyErogazione);
		Utils.resetCounters(idPolicyFruizione);
		Utils.waitForPolicy(policy);
		Utils.checkConditionsNumeroRichieste(idPolicyErogazione, 0, 0, 0);
		Utils.checkConditionsNumeroRichieste(idPolicyFruizione, 0, 0, 0);
		

		HttpRequest requestErogazione = new HttpRequest();
		requestErogazione.setContentType("application/soap+xml");
		requestErogazione.setMethod(HttpRequestMethod.POST);
		requestErogazione.setUrl(urlServizioErogazione);
		requestErogazione.setContent(SoapBodies.get(policy).getBytes());
						
		Vector<HttpResponse> degradoResponsesErogazione = Utils.makeParallelRequests(requestErogazione, maxRequests+1);
		assertEquals(maxRequests+1, degradoResponsesErogazione.stream().filter( r -> r.getResultHTTPOperation() == 200).count());
		
		HttpRequest requestFruizione = new HttpRequest();
		requestFruizione.setContentType("application/soap+xml");
		requestFruizione.setMethod(HttpRequestMethod.POST);
		requestFruizione.setUrl(urlServizioFruizione);
		requestFruizione.setContent(SoapBodies.get(policy).getBytes());
		
		Vector<HttpResponse> degradoResponsesFruizione = Utils.makeParallelRequests(requestFruizione, maxRequests+1);
		assertEquals(maxRequests+1, degradoResponsesFruizione.stream().filter( r -> r.getResultHTTPOperation() == 200).count());
		
		
		// Attendo in modo che le statistiche vengano aggiornate e il degrado prestazionale
		// rilevato.
		Utils.waitForDbStats();
		
		// Faccio le ulteriori richieste per far scattare la policy sulla erogazione
		makeParallelRequests_and_checkFailedRequests(requestErogazione, windowSize, maxRequests, null);

		// Faccio le ulteriori richieste per far scattare la policy sulla fruizione
		makeParallelRequests_and_checkFailedRequests(requestFruizione, windowSize, maxRequests, null);
		
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
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1?sleep="+String.valueOf(attesa)
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1?sleep="+String.valueOf(attesa);
		
		// Utils.waitForDbStats();

		Utils.waitForZeroGovWayThreads();
		Utils.resetCounters(idPolicy);
		Utils.waitForPolicy(policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(urlServizio);
		request.setContent(SoapBodies.get(policy).getBytes());
		
		Vector<HttpResponse> degradoResponses = Utils.makeParallelRequests(request, maxRequests);
		
		assertEquals(maxRequests, degradoResponses.stream().filter( r -> r.getResultHTTPOperation() == 200).count());
		
		// Attendo 15 secondi in modo che le statistiche vengano aggiornate e il degrado prestazionale
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
		//org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.SoapTest.checkFailedRequests(blockedResponses, windowSize, maxRequests);
		makeParallelRequests_and_checkFailedRequests(request, windowSize, maxRequests, congestionRequest);

		
		// Aspetto che il degrado prestazionale passi e faccio ulteriori n richieste che non devono essere bloccate
		// perchè ancora in congestione
		/*Utils.waitForDbStats();
		
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(count);

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

		
		// Faccio n richieste che devono andare tutte bene perchè il sistema è solo in congestione
		Vector<HttpResponse> nonBlockedResponses = Utils.makeParallelRequests(request, maxRequests);
		
		assertEquals(maxRequests, nonBlockedResponses.stream().filter( r -> r.getResultHTTPOperation() == 200).count());
		
		
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logRateLimiting.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}*/
	}
	
	
	private static void makeParallelRequests_and_checkFailedRequests(HttpRequest request, int windowSize, int maxRequests, HttpRequest congestionRequest) {
		
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
					Utils.checkXLimitHeader(logRateLimiting, Headers.RequestSuccesfulLimit,r.getHeader(Headers.RequestSuccesfulLimit), maxRequests);			
					if ("true".equals(prop.getProperty("rl_check_limit_windows"))) {
						Map<Integer,Integer> windowMap = Map.of(windowSize,maxRequests);							
						Utils.checkXLimitWindows(r.getHeader(Headers.RequestSuccesfulLimit), maxRequests, windowMap);
					}
					assertTrue(Integer.valueOf(r.getHeader(Headers.RequestSuccesfulReset)) <= windowSize);			
					assertEquals(429, r.getResultHTTPOperation());
					
					Element element = Utils.buildXmlElement(r.getContent());
					Utils.matchLimitExceededSoap(element);
					
					assertEquals("0", r.getHeader(Headers.RequestSuccesfulRemaining));
					assertEquals(HeaderValues.LimitExceeded, r.getHeader(Headers.GovWayTransactionErrorType));
					assertEquals(HeaderValues.ReturnCodeTooManyRequests, r.getHeader(Headers.ReturnCode));
					assertNotEquals(null, r.getHeader(Headers.RetryAfter));
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
	 * 	Controlliamo che la policy di rate limiting venga applicata solo in
	 *  presenza di congestione.
	 *  
	 *	La policy di RL è: NumeroRichiesteCompletateConSuccesso.
	 */
	public void rateLimitingInPresenzaCongestione(TipoServizio tipoServizio) throws Exception {
		// EventiUtils.waitForDbEvents();
		Utils.waitForZeroGovWayThreads();


		final int maxRequests = 5;
		final String erogazione = "InPresenzaCongestioneSoap";
		final PolicyAlias policy = PolicyAlias.ORARIO;
		
		final String idPolicy = tipoServizio == TipoServizio.EROGAZIONE
				? dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy)
				: dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
		
		final String urlServizio = tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1";
				
		final BiConsumer<String,PolicyAlias> testToRun = tipoServizio == TipoServizio.EROGAZIONE
				? org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.SoapTest::testErogazione
				: org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.SoapTest::testFruizione;
		
		
		Utils.resetCounters(idPolicy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		Utils.waitForPolicy(policy);


		// Faccio n richieste per superare la policy e controllo che non scatti,
		// perchè la congestione non è attiva.
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(urlServizio);
		request.setContent(SoapBodies.get(policy).getBytes());
						
		Vector<HttpResponse> responses = Utils.makeRequestsAndCheckPolicy(request, maxRequests+1, idPolicy);
		
		// Controllo che non sia scattata la policy
		assertEquals( maxRequests+1, responses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		
		// Faccio attivare la congestione
		String url = basePath + "/SoggettoInternoTest/NumeroRichiesteSoap/v1?sleep="+durataCongestione;
		HttpRequest congestionRequest = new HttpRequest();
		congestionRequest.setContentType("application/soap+xml");
		congestionRequest.setMethod(HttpRequestMethod.POST);
		congestionRequest.setUrl(url);
		congestionRequest.setContent(SoapBodies.get(PolicyAlias.NO_POLICY).getBytes());
		
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
		
		responses = Utils.makeParallelRequests(request, maxRequests+1);
		
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
	 * 	Qui si testa la generazione dell'evento di congestione e del successivo evento
	 *  che segnala la violazione del massimo numero di richieste simultanee
	 */
	public void congestioneAttivaViolazioneRichiesteComplessive(String url) {
		
		
		final int sogliaRichiesteSimultanee = 15;
		EventiUtils.waitForDbEvents();
		Utils.waitForZeroGovWayThreads();


		
		String body = SoapBodies.get(PolicyAlias.NO_POLICY);
		
		LocalDateTime dataSpedizione = LocalDateTime.now();		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(url);
		request.setContent(body.getBytes());
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, sogliaRichiesteSimultanee+1);
		
		checkCongestioneAttivaViolazioneRichiesteComplessive(dataSpedizione, responses);
	}


	public static void checkCongestioneAttivaViolazioneRichiesteComplessive(LocalDateTime dataSpedizione,
			Vector<HttpResponse> responses) {
		
		EventiUtils.waitForDbEvents();
		Utils.waitForZeroGovWayThreads();

		
		//  Devo trovare tra le transazioni generate dalle richieste, almeno una transazione
		//  che abba la violazione della soglia di congestione e una transazione con la violazione
		//  della soglia di richieste simultanee globali
			

		var credenzialiToMatch = new ArrayList<String>(Arrays.asList(
				"##ControlloTraffico_SogliaCongestione_Violazione##",
				"##ControlloTraffico_NumeroMassimoRichiesteSimultanee_Violazione##"
				));
		

		credenzialiToMatch.removeIf( toCheck -> responses.stream()
				.anyMatch( r -> EventiUtils.testCredenzialiMittenteTransazione(
							r.getHeader(Headers.TransactionId),
							evento -> {						
								logRateLimiting.info(evento.toString());
								String credenziale = (String) evento.get("credenziale"); 
								return credenziale.contains(toCheck);
							}) 
						)
				);
		
		assertEquals(Arrays.asList(), credenzialiToMatch);

		List<Map<String, Object>> events = EventiUtils.getNotificheEventi(dataSpedizione);		
		logRateLimiting.info(events.toString());
		
		assertEquals(true, EventiUtils.findEventCongestioneViolata(events, logRateLimiting));
		assertEquals(true, EventiUtils.findEventCongestioneRisolta(events, logRateLimiting));
		
		boolean sogliaViolata =  events.stream()
				.anyMatch( ev -> {
					return  ev.get("tipo").equals("ControlloTraffico_NumeroMassimoRichiesteSimultanee") &&
							ev.get("codice").equals("Violazione") &&
							ev.get("severita").equals(1) && 
							ev.get("descrizione").equals("Superato il numero di richieste complessive (15) gestibili dalla PdD");
				});
		
		boolean violazioneRisolta = events.stream()
				.anyMatch( ev -> {
					return  ev.get("tipo").equals("ControlloTraffico_NumeroMassimoRichiesteSimultanee") &&
							ev.get("codice").equals("ViolazioneRisolta") &&
							ev.get("severita").equals(3); 
				}); 
		
		assertEquals(true, sogliaViolata);
		assertEquals(true, violazioneRisolta);
	}
	
	
	/**
	 * Controlla che il sistema entri effettivamente in congestione.
	 * 
	 */
	
	public void congestioneAttiva(String url) {
		String body = SoapBodies.get(PolicyAlias.NO_POLICY);
		EventiUtils.waitForDbEvents();
		Utils.waitForZeroGovWayThreads();

		LocalDateTime dataSpedizione = LocalDateTime.now();		
		
		HttpRequest request = new HttpRequest();	
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(url);
		request.setContent(body.getBytes());
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, sogliaCongestione+1);
		
		EventiUtils.checkEventiCongestioneAttiva(dataSpedizione, responses, logRateLimiting);		
	}
	
	
	/**
	 * Controlla che il sistema registri gli eventi di congestione
	 * e gli eventi di violazione di una policy di rate limiting.
	 * 
	 */
	public void congestioneAttivaConViolazioneRL(String url, String idServizio) {
		
		final int sogliaRichiesteSimultanee = 10;
		
		EventiUtils.waitForDbEvents();
		Utils.waitForZeroGovWayThreads();

		// Affinchè il test faccia scattare tutti e due gli eventi è necessario
		// che la soglia di congestione sia più bassa della soglia di RL
		assertTrue(sogliaRichiesteSimultanee > sogliaCongestione);
		
		String body = SoapBodies.get(PolicyAlias.RICHIESTE_SIMULTANEE);
		
		LocalDateTime dataSpedizione = LocalDateTime.now();		
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(url);
		request.setContent(body.getBytes());
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, sogliaRichiesteSimultanee+1);
		
		EventiUtils.checkEventiCongestioneAttivaConViolazioneRL(idServizio, dataSpedizione, Optional.of("RichiesteSimultanee"), responses, logRateLimiting);
	}
	

	

}
