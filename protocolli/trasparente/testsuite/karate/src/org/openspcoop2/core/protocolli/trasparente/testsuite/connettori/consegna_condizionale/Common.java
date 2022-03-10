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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.DbUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* Common
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Common {
	
	public static final String CONNETTORE_0 = "Connettore0";
	public static final String CONNETTORE_1 = "Connettore1";
	public static final String CONNETTORE_2 = "Connettore2";
	public static final String CONNETTORE_3 = "Connettore3";
	public static final String CONNETTORE_ROTTO = "ConnettoreRotto";
	public static final String CONNETTORE_DISABILITATO = "ConnettoreDisabilitato";
	public static String CONNETTORE_ID_FALLITA = "ConnettoreIdentificazioneFallita";
	public static String CONNETTORE_ID_NON_TROVATO = "ConnettoreNessunConnettoreTrovato";

	public static final String POOL_0 = "Pool0";
	public static final String POOL_1 = "Pool1";
	public static final String POOL_2 = "Pool2";
	public static final String POOL_ROTTO = "PoolRotto";
	public static final String POOL_LOCALHOST = "PoolLocalhost";
	public static final String POOL_IDENTIFICAZIONE_FALLITA = "Pool-Identificazione-Fallita";
	public static final String POOL_IDENTIFICAZIONE_FALLITA_NO_CONNETTORI = "Pool-Identificazione-Fallita-No-Connettori";


	public static final String ID_CONNETTORE_REPLY_PREFIX = "GovWay-TestSuite-";
	
	public static final String HEADER_ID_CONNETTORE = ID_CONNETTORE_REPLY_PREFIX + "id_connettore";
	
	public static final String HEADER_ID_SESSIONE = ID_CONNETTORE_REPLY_PREFIX + "ID-Sessione";
	
	public static final String HEADER_ID_CONDIZIONE = ID_CONNETTORE_REPLY_PREFIX+"Connettore";

	public static final String HEADER_ID_TRANSAZIONE = "GovWay-Transaction-ID";

	public static final int durataBloccante = Integer
			.valueOf(System.getProperty("connettori.load_balancer.least_connections.durata_bloccante"));

	public static final int durataBloccanteLunga = Integer
			.valueOf(System.getProperty("connettori.load_balancer.least_connections.durata_bloccante_lunga"));
	
	public static final int delayRichiesteBackground = Integer
			.valueOf(System.getProperty("connettori.load_balancer.least_connections.delay_richieste_background"));
	
	public static final int richiesteTestRandom = Integer
			.valueOf(System.getProperty("connettori.load_balancer.numero_richieste_random"));
	
	public static final int maxAge = Integer
			.valueOf(System.getProperty("connettori.load_balancer.sessione_sticky.max_age")) * 1000;	// Lo trasformo in millisecondi
	
	public static final int intervalloEsclusione = Integer
			.valueOf(System.getProperty("connettori.load_balancer.health_check.intervallo_esclusione")) * 1000;	// Lo trasformo in millisecondi
	
	public static final int sogliaRichiesteSimultanee = Integer
			.valueOf(System.getProperty("soglia_richieste_simultanee"));
	
	public static final int richiesteParallele = sogliaRichiesteSimultanee;
	
	public static List<String> IDSessioni = Arrays
			.asList(UUID.randomUUID().toString(),
					UUID.randomUUID().toString());
	
	public static final Map<String,Integer> pesiConnettoriStandard = Map
			.of(CONNETTORE_0, 1,
				CONNETTORE_1, 1,
				CONNETTORE_2, 2,
				CONNETTORE_3, 3);

	public static final List<String> connettoriAbilitati = Arrays
			.asList(CONNETTORE_0,
					CONNETTORE_1, 
					CONNETTORE_2,
					CONNETTORE_3);
	
	public static final Set<String> setConnettoriAbilitati = new HashSet<>(connettoriAbilitati);
	
	public static final List<String> filtriPoolRotto = Arrays.asList("PoolRotto-Filtro0", "PoolRotto-Filtro1");
	 
	
	public static Map<String,List<String>> filtriPools = Map
			.of(POOL_0, Arrays.asList("Pool0-Filtro0"),
				POOL_1, Arrays.asList("Pool1-Filtro0"),
				POOL_2, Arrays.asList("Pool2-Filtro0"));

	public static List<String> pools = Arrays
		.asList(POOL_0, POOL_1, POOL_2);
	

	public static Map<String,List<String>> connettoriPools = Map
		.of(POOL_0, Arrays.asList(CONNETTORE_0,CONNETTORE_1,CONNETTORE_2, CONNETTORE_DISABILITATO),
			POOL_1, Arrays.asList(CONNETTORE_1,CONNETTORE_2,CONNETTORE_3, CONNETTORE_DISABILITATO),
			POOL_2, Arrays.asList(CONNETTORE_2,CONNETTORE_3,CONNETTORE_0, CONNETTORE_DISABILITATO),
			POOL_ROTTO, Arrays.asList(CONNETTORE_0,CONNETTORE_1,CONNETTORE_2, CONNETTORE_ROTTO, CONNETTORE_DISABILITATO),
			POOL_LOCALHOST, Arrays.asList(CONNETTORE_0,CONNETTORE_1,CONNETTORE_2, CONNETTORE_DISABILITATO),
			POOL_IDENTIFICAZIONE_FALLITA, connettoriAbilitati,
			POOL_IDENTIFICAZIONE_FALLITA_NO_CONNETTORI, connettoriAbilitati);

	
	public static void printMap(Map<String, Integer> howManys) {
		var logger = ConfigLoader.getLoggerCore();
		
		logger.info("Map di connettori: ");
		for (var e : howManys.entrySet()) {
			logger.info(e.getKey() + ": " + e.getValue());
		}
	}
	
	
	/**
	 * Esegue `count` richieste `request` parallele, alloca soglia_richieste_simultanee
	 * thread concorrenti. Solo da questo differisce dalla
	 * 	org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.makeParallelRequests
	 * ma non posso modificarla perchè è appunto usata in test di rate limiting 
	 * 
	 */
	public static Vector<HttpResponse> makeParallelRequests(HttpRequest request, int count) {
		
		if (count < 0) {
			throw new IllegalArgumentException("Request count must be > 0");
		} else if (count == 0) {
			return new Vector<>();
		}
		
		int nthreads = Integer.valueOf(System.getProperty("soglia_richieste_simultanee"));
		var logger = ConfigLoader.getLoggerCore();

		final Vector<HttpResponse> responses = new Vector<>();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nthreads);

		for (int i = 0; i < count; i++) {
			executor.execute(() -> {
				try {
					logger.info(request.getMethod() + " " + request.getUrl());
					responses.add(HttpUtilities.httpInvoke(request));
					logger.info("Richiesta effettuata..");
				} catch (UtilsException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			});
		}
		
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		
		logger.info("RESPONSES: ");
		responses.forEach(r -> {
			logger.info("statusCode: " + r.getResultHTTPOperation());
			logger.info("headers: " + r.getHeadersValues());
		});

		return responses;
	}
	
	
	public static Vector<Future<HttpResponse>> makeBackgroundRequests(List<HttpRequest> requests, int count,
			int request_delay) {
		final Vector<Future<HttpResponse>> oldFutureResponses = new Vector<>();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(sogliaRichiesteSimultanee);
		var logger = ConfigLoader.getLoggerCore();
	
		for (int i = 0; i < count; i++) {
			
			if (request_delay > 0) {
				org.openspcoop2.utils.Utilities.sleep(request_delay);
			}
			int index = i % requests.size();
			
			var future = executor.submit(() -> {
				try {
					var request = requests.get(index);
					logger.info(request.getMethod() + " " + request.getUrl());
					HttpResponse resp = HttpUtilities.httpInvoke(request);
					logger.info("Richiesta effettuata..");
					return resp;
				} catch (UtilsException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			});
			oldFutureResponses.add(future);
		}
		return oldFutureResponses;
	}


	public static Map<String, Integer> contaConnettoriUtilizzati(List<HttpResponse> responses) {
		Map<String, Integer> howManys = new HashMap<>();
		
		for (var response : responses) {
			String id_connettore = Common.getIdConnettore(response);						
			assertNotEquals(null, id_connettore);
			howManys.put(id_connettore, howManys.getOrDefault(id_connettore, 0)+1);
		}
		return howManys;
	}


	public static HttpRequest buildRequest_Semplice(String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-header-http"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+ID_CONNETTORE_REPLY_PREFIX);
		return request;
	}
	

	public static String getIdConnettore(HttpResponse response) {
		if (response.getResultHTTPOperation() == 200) {				 
			return response.getHeaderFirstValue(HEADER_ID_CONNETTORE);
		} else {
			return CONNETTORE_ROTTO;
		}		
	}


	public static void checkRoundRobin(List<HttpResponse> responses, Set<String> connettori) {
		// A ciascun connettore devono essere arrivate almeno nRequests/nConnettori richieste
		// (nRequests % nConnettori) connettori hanno una richiesta in più
		// Tutti i connettori indicati nel secondo parametro devono essere stati raggiunti.		
		int nRequests = responses.size();
		int nConnettori = connettori.size();
		int minRequests = nRequests / nConnettori;
		int restoRequests = nRequests % nConnettori;
		int nDiPiu = 0;		// Quanti connettori sono stati raggiunti da al più 
		
		var howManys = contaConnettoriUtilizzati(responses);
		printMap(howManys);
		
		for (var connettore : howManys.keySet()) {
			// Ogni connettore raggiunto deve essere nel set dei connettori
			assertTrue(connettori.contains(connettore));
		}
		
		for (var connettore : connettori) {
			// Tutti i connettori devono essere stati raggiunti
			assertTrue(howManys.containsKey(connettore));
			int q = howManys.get(connettore);
			assertTrue(q>=minRequests);
			assertTrue(q<=minRequests+1);
			if (q==minRequests+1) {
				nDiPiu++;
			}
		}
		
		assertEquals(restoRequests,nDiPiu);
	}


	public static void checkAllResponsesSameConnettore(List<HttpResponse> responses) {
		checkAll200(responses);
		String connettore = responses.get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertNotEquals(null, connettore);
		for(var resp : responses) {
			assertEquals(connettore, resp.getHeaderFirstValue(HEADER_ID_CONNETTORE));
		}
	}
	

	public static void checkAll200(List<HttpResponse> responses) {
		checkResponsesStatus(responses, 200);
	}
	

	public static void checkResponsesStatus(List<HttpResponse> responses, int status) {
		for (var resp : responses) {
			ConfigLoader.getLoggerCore().debug("Checking response status code for transaction: " + resp.getHeaderFirstValue(HEADER_ID_TRANSAZIONE));
			assertEquals(status, resp.getResultHTTPOperation());
		}
	}
	
	
	public static void fermaRiconsegne(DbUtils dbUtils) {
		String query = "UPDATE messaggi set proprietario='GestoreMessaggi'";
		dbUtils.update(query);
		
	}
	
	public static void fermaRiconsegne(DbUtils dbUtils, String transazione) {
		String query = "UPDATE messaggi set proprietario='GestoreMessaggi' WHERE id_transazione='"+transazione+"'";
		dbUtils.update(query);		
	}
	
}
