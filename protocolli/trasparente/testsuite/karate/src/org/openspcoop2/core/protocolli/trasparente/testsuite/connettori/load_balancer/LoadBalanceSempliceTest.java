/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.ID_CONNETTORE_REPLY_PREFIX;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.delayRichiesteBackground;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.printMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;


/**
 * @author Francesco Scarlato (scarlato@link.it)
 * 
 * 
 * 	6 Connettori per erogazione.
 *	ID = 0, 1, 2, 3					=> Connettori funzionanti
 *	ID = 'connettore-disabilitato'	=> Connettore disabilitato
 *	ID = 'connettore-rotto'			=> Connettore con errore di connessione
	
 *	I nomi invece sono:
 *	Connettore0, Connettore1, Connettore2, Connettore3, ConnettoreDisabilitato, ConnettoreRotto
	
 *	Testo anche che al connettore disabilitato non venga instradato nulla, ogni politica ha un connettore disabilitato.
 */
public class LoadBalanceSempliceTest extends ConfigLoader {

	// Per adesso eseguo i test sempre con -Dload=false -Ddelete=false
	
	public static final String CONNETTORE_0 = "0";
	public static final String CONNETTORE_1 = "1";
	public static final String CONNETTORE_2 = "2";
	public static final String CONNETTORE_3 = "3";
	public static final String CONNETTORE_ROTTO = "connettore-rotto";
	public static final String CONNETTORE_DISABILITATO = "connettore-disabilitato";
	
	// TODO: Armonizza con gli altri connettori di ConsegnaCondizionale
	private static String getIdConnettore(HttpResponse response) {
		if (response.getResultHTTPOperation() == 200) {				 
			return response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
		} else {
			return CONNETTORE_ROTTO;
		}		
	}

	
	public static Map<String, Integer> contaConnettoriUtilizzati(List<HttpResponse> responses) {
		Map<String, Integer> howManys = new HashMap<>();
		
		for (var response : responses) {
			String id_connettore = getIdConnettore(response);						
			assertNotEquals(null, id_connettore);
			howManys.put(id_connettore, howManys.getOrDefault(id_connettore, 0)+1);
		}
		return howManys;
	}

	
	@Test
	public void roundRobin() {
		/*
		 * La richiesta viene instradata ai connettori in maniera circolare, uno dopo
		 * l'altro. Faccio 15 richieste, che devono andare 4 per connettore, anche
		 * quelle con errore di connessione.
		 */

		final String erogazione = "LoadBalanceRoundRobin";

		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+ID_CONNETTORE_REPLY_PREFIX);

		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 15);

		// Ho 5 connettori buoni, mi aspetto tre richieste per ognuno.
		Map<String, Integer> howManys = contaConnettoriUtilizzati(responses);

		for (var results : howManys.entrySet()) {
			assertEquals(Integer.valueOf(3), results.getValue());
		}

	}

	
	@Test
	public void weightedRoundRobin() {
		/*
		 * Ogni connettore prende una percentuale di richieste
		 */

		final String erogazione = "LoadBalanceWeightedRoundRobin";

		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+ID_CONNETTORE_REPLY_PREFIX);

		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 15);
		Map<String, Integer> howManys = contaConnettoriUtilizzati(responses);

		// 		PESI:
		// Connettore0 => 1
		// Connettore1 => 2
		// Connettore2 => 3
		// Connettore3 => 4
		// ConnettoreRotto => 5
		// Essendo il peso totale 15 e Avendo 15 richieste, devo riempire tutti i connettori per un numero di richieste
		// pari al loro peso
		
		for (var results : howManys.entrySet()) {
			if (results.getKey().equals(CONNETTORE_ROTTO)) {
				assertEquals(Integer.valueOf(5), results.getValue());
			} else {			
				Integer expected = Integer.valueOf(results.getKey()) + 1;
				assertEquals(expected, results.getValue());
			}
		}
	}
	

	@Test
	public void random() {
		/*
		 * Le richieste vengono distribuite casualmente, faccio tre pool da 15 richieste simultanee: controllo che in ogni
		 * pool nessun connettore vinca sempre su tutti, e richiedo che durante i tre pool almeno ciascun connettore
		 * deve essere raggiunto.
		 * Per 'pool' si intendono 15 richieste simultanee 
		 */
		
		final String erogazione = "LoadBalanceRandom";
		final int nRequests = 15;
		
		Set<String> connettoriRaggiunti = new HashSet<>();
		for(int i=0;i<4;i++) {
			
			HttpRequest request = new HttpRequest();
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
					+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+ID_CONNETTORE_REPLY_PREFIX);

			Vector<HttpResponse> responses = Utils.makeParallelRequests(request, nRequests);
			Map<String, Integer> howManys = contaConnettoriUtilizzati(responses);
			
			assertNotEquals(1, howManys.keySet().size());
			
			for (var results : howManys.entrySet()) {
				connettoriRaggiunti.add(results.getKey());
			}			
		}
		
		assertEquals(5, connettoriRaggiunti.size());			
	}
	
	
	@Test
	public void weightedRandom() {
		/*
		 * Le richieste vengono distribuite casualmente considerando il peso di ciascun connettore.
		 * Controllo che su un carico abbastanza alto di richieste, le relazioni d'ordine tra il numero 
		 * di richieste gestite segua i pesi.
		 * 
		 */
		
		// 		PESI:
		// Connettore0 => 1
		// Connettore1 => 2
		// Connettore2 => 3
		// Connettore3 => 4
		// ConnettoreRotto => 5
		
		final String erogazione = "LoadBalanceWeightedRandom";
		final int maxParallelRequests = 15;
		final int requestsPerThread = 5;
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+ID_CONNETTORE_REPLY_PREFIX);
		
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, maxParallelRequests, requestsPerThread);
		Map<String, Integer> howManys = contaConnettoriUtilizzati(responses);
		
		assertEquals(true, howManys.get(CONNETTORE_ROTTO) > howManys.get(CONNETTORE_3));
		assertEquals(true, howManys.get(CONNETTORE_3) > howManys.get(CONNETTORE_2));
		assertEquals(true, howManys.get(CONNETTORE_2) > howManys.get(CONNETTORE_1));
		assertEquals(true, howManys.get(CONNETTORE_1) > howManys.get(CONNETTORE_0));
	}
	
	
	@Test
	public void sourceIpHash() {
		/* 
		 * L'indirizzo ip del client viene hashato e in base a questo valore viene identificato 
		 * il connettore. 
		 */
		
		final String erogazione = "LoadBalanceSourceIpHash";
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+ID_CONNETTORE_REPLY_PREFIX);

		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 15);	
		Map<String, Integer> howManys = contaConnettoriUtilizzati(responses);
		
		// In questo caso controlliamo che un unico connettore venga raggiunto.
	
		printMap(howManys);
		assertEquals(1, howManys.keySet().size());
	}
		
	
	@Test
	public void sourceIpHashWithHeader_ForwardedFor() throws UtilsException {
		/* 
		 * Viene fatto l'hash dell'indirizzo ip del client e del valore dello header 
		 * Forwarded-For e in base a questo valore viene identificato il connettore.
		 * 
		 *  TODO: Se il valore dello header non è un indirizzo ip, lo hash butta tutto sullo stesso connettore, VERIFICA.
		 */

		final String erogazione = "LoadBalanceSourceIpHash";
		final int nthreads = 15;
		final int reqsPerThread = 4;
		List<String> forwardedHeaders = HttpUtilities.getClientAddressHeaders();
		
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nthreads);

		Vector<HttpResponse> wholeResponses = new Vector<>();
		
		for (int i = 0; i < nthreads; i++) {

			final String forwardedHeader = forwardedHeaders.get(i%forwardedHeaders.size()); 
			final String forwardedFor = "212.6"+i+".76.98";
			
			executor.execute(() -> {
				
				HttpRequest request = new HttpRequest();
				request.setContentType("application/json");
				request.setMethod(HttpRequestMethod.GET);
				request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
						+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+ID_CONNETTORE_REPLY_PREFIX);
				request.addHeader(forwardedHeader, forwardedFor);
											
				Vector<HttpResponse> responses = Utils.makeSequentialRequests(request, reqsPerThread);				
				Map<String, Integer> howManys = contaConnettoriUtilizzati(responses);
				
				// Controlliamo all'interno del thread che un unico connettore venga raggiunto,
				// perchè le richieste di un thread hanno tutte lo stesso header Forwarded-For
				if(howManys.keySet().size() != 1) {
					printMap(howManys);
				}
				assertEquals(1, howManys.keySet().size());
				
				wholeResponses.addAll(responses);
			});
		}
		
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logCore.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		
		// Considero un errore Se con 15 hash diversi ho raggiunto sempre lo stesso connettore.
		Map<String, Integer> howManys = contaConnettoriUtilizzati(wholeResponses);
		printMap(howManys);
		assertNotEquals(1, howManys.keySet().size());
		
	}
	
	
	@Test
	public void leastConnections() {
		/**
		 * La richiesta viene instradata al connettore con il minimo numero di
		 * connessioni attive.
		 * 
		 * L'erogazione ha il connettore rotto disabilitato in modo da non interferire con il parametro sleep
		 * destinato al server di echo. Se la richiesta venisse instradata al connettore rotto non avremmo la pausa
		 * desiderata.
		 * 
	
		 * Test 1: Test effettivo della funzionalità. Faccio 5 richieste parallele
		 * con sleep e attendo la terminazione. Mi assicuro che siano stati raggiunti
		 * tutti e 4 i connettori.
		 */ 
		
		final String erogazione = "LoadBalanceLeastConnections";
		
		HttpRequest requestBlocking = new HttpRequest();
		requestBlocking.setContentType("application/json");
		requestBlocking.setMethod(HttpRequestMethod.GET);
		requestBlocking.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?sleep="+org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.durataBloccante +
				"&replyQueryParameter=id_connettore&replyPrefixQueryParameter="
				+ ID_CONNETTORE_REPLY_PREFIX);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test" +
				"&replyQueryParameter=id_connettore&replyPrefixQueryParameter="
				+ ID_CONNETTORE_REPLY_PREFIX);
		
		
		// Lancio una richiesta in backgroun per tenere "occupato" un connettore a caso.
		var blockingRespFuture = Utils.makeBackgroundRequest(requestBlocking);
		
		// Dopo faccio 3 richieste parallele che devono raggiungere gli altri 3 connettori attivi
		Vector<HttpResponse> responses = Utils.makeParallelRequests(request, 3);
		Map<String, Integer> howManys = contaConnettoriUtilizzati(responses);
		HttpResponse blockingResp;
		try {
			blockingResp = blockingRespFuture.get();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		// Il connettore "bloccato" non deve essere stato raggiunto da queste richieste.
		assertEquals(false, howManys.containsKey(getIdConnettore(blockingResp)));
		
		 /* Poi variante parallela: 20 richieste parallele con sleep e mi aspetto che si
		 * comporti esattamente come un round robin: una richiesta alla volta per
		 * connettore.
		 */
		//
		// Inizio 3 richieste con una sleep sui primi 3
	}
	
	
	@Test
	public void leastConnectionsAsRoundRobin() {
		// Facendo delle richieste in background con sleep, una dopo l'altra, 
		// la strategia deve comportarsi come un weighted round robin con tutti i pesi a 1
		
		final String erogazione = "LoadBalanceLeastConnections";
		
		HttpRequest requestBlocking = new HttpRequest();
		requestBlocking.setContentType("application/json");
		requestBlocking.setMethod(HttpRequestMethod.GET);
		requestBlocking.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?sleep="+org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.durataBloccante +
				"&replyQueryParameter=id_connettore&replyPrefixQueryParameter="
				+ ID_CONNETTORE_REPLY_PREFIX);
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test" +
				"&replyQueryParameter=id_connettore&replyPrefixQueryParameter="
				+ ID_CONNETTORE_REPLY_PREFIX);
		
		
		// Occupo Quattro connettori, ne deve restare libero uno solo
		var futureBlockingResponses = Utils.makeBackgroundRequests(requestBlocking, 4, delayRichiesteBackground);
		
		Vector<HttpResponse> responses = new Vector<>();
		responses = Utils.awaitResponses(futureBlockingResponses);
		Map<String, Integer> howManys = contaConnettoriUtilizzati(responses);
		
		assertEquals(4, howManys.keySet().size());
		
		
		// Adesso tengo due connettori occupati per più tempo e mando due richieste
		// sugli altri due, poi altre due sempre sugli altri due.
		// Devo aver sempre e solo raggiunto gli altri due
		HttpRequest requestBlockingLong = new HttpRequest();
		requestBlockingLong.setContentType("application/json");
		requestBlockingLong.setMethod(HttpRequestMethod.GET);
		requestBlockingLong.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?sleep="+org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.durataBloccante+2000 +
				"&replyQueryParameter=id_connettore&replyPrefixQueryParameter="
				+ ID_CONNETTORE_REPLY_PREFIX);
		
		var futureLongBlockResponse = Utils.makeBackgroundRequests(requestBlockingLong, 2, delayRichiesteBackground);	// TODO: rendere il delay un parametro

		
	}


}