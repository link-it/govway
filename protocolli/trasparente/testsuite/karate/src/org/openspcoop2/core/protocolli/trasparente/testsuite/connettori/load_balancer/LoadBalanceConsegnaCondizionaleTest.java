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
import static org.junit.Assert.assertTrue;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.ConsegnaCondizionaleByFiltroTest.buildRequests_HeaderHttp;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.CONNETTORE_0;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.CONNETTORE_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.CONNETTORE_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.CONNETTORE_3;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.CONNETTORE_DISABILITATO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.CONNETTORE_ROTTO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.ConsegnaCondizionaleByFiltroTest;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * LoadBalanceConsegnaCondizionaleTest
 * 
 * @author Francesco Scarlato (scarlato@link.it)
 *
 * TODO: Devo fare anche identificazione condizione fallita e nessun connettore utilizzabile?
 * 		SI.
 * TODO: prefisso e suffisso
 * TODO: Dire ad andrea che qui la strategia usata è sempre round robin, se vuole la si può modificare
 * 		e usare una strategia diversa per ogni tipo di consegna condizionale.
 * 
 * La Consegna Condizionale sul Load Balancer identifica l'insieme di connettori
 * che parteciperanno alla strategia di load balancing.
 * 
 * Le strategie di load balancing vengono testate in LoadBalanceSempliceTest.java
 * Qui testiamo l'integrazione fra le due funzionalità mantenendo fissa la strategia di load balancing
 * su un semplice Round Robin.
 * Si suppone quindi che il codice per la selezione dell'insieme dei connettori sia indipendente 
 * dalla politica di load balancing scelta. 
 * 
 * I test seguono il seguente schema:
 * 		- 3 pool di connettori, identificati dai filtri: Pool0-Filtro0, Pool0-Filtro1, Pool1.Filtro0 ecc..
 * 			- Pool0: Connettore0, Connettore1, Connettore2, ConnettoreDisabilitato
 * 			- Pool1: Connettore1, Connettore2, Connettore3, ConnettoreDisabilitato
 * 			- Pool2: Connettore2, Connettore3, Connettore0, ConnettoreDisabilitato
 * 			- PoolRotto: Connettore0, Connettore1, Connettore2, ConnettoreRotto, ConnettoreDisabilitato
 * 
 * 		- Ciascun pool viene raggiunto in maniera concorrente da più richieste
		  e in maniera concorrente viene raggiunto anche ciascun pool
 *  
 * 		- L'implementazione di govway tratta l'insieme di connettori identificato per una richiesta come un un'unica 
 * 			unità sulla quale poi applicare la strategia di load balancing. Perciò anche se i vari pools condividono 
 * 			connettori non è necessario resettare la cache dopo ogni pool di richieste. Se un connettore
 * 			è stato raggiunto perchè facente parte del pool A, allora le richieste al pool B non terranno conto di questo, applicando
 * 			di fatto una strategia di load balancing a parte su ogni singolo insieme di connettori. 
 */

public class LoadBalanceConsegnaCondizionaleTest extends ConfigLoader {
	
	public static final String POOL_0 = "Pool0";
	public static final String POOL_1 = "Pool1";
	public static final String POOL_2 = "Pool2";
	public static final String POOL_ROTTO = "PoolRotto";
	public static final String POOL_LOCALHOST = "PoolLocalhost";
	
	public static List<String> pools = Arrays.asList(
			POOL_0, POOL_1, POOL_2	// Non includo il pool rotto per velocizzare i test
			);
	
	public static Map<String,List<String>> connettoriPools = Map.of(
			POOL_0, Arrays.asList(CONNETTORE_0,CONNETTORE_1,CONNETTORE_2, CONNETTORE_DISABILITATO),
			POOL_1, Arrays.asList(CONNETTORE_1,CONNETTORE_2,CONNETTORE_3, CONNETTORE_DISABILITATO),
			POOL_2, Arrays.asList(CONNETTORE_2,CONNETTORE_3,CONNETTORE_0, CONNETTORE_DISABILITATO),
			POOL_ROTTO, Arrays.asList(CONNETTORE_0,CONNETTORE_1,CONNETTORE_2, CONNETTORE_ROTTO, CONNETTORE_DISABILITATO),
			POOL_LOCALHOST, Arrays.asList(CONNETTORE_0,CONNETTORE_1,CONNETTORE_2, CONNETTORE_DISABILITATO)
		);
	
	public static Map<String,List<String>> filtriPools = Map.of(
			POOL_0, Arrays.asList("Pool0-Filtro0", "Pool0-Filtro1"),
			POOL_1, Arrays.asList("Pool1-Filtro0", "Pool1-Filtro1"),
			POOL_2, Arrays.asList("Pool2-Filtro0", "Pool2-Filtro1")
	//		POOL_ROTTO, Arrays.asList("PoolRotto-Filtro0", "PoolRotto-Filtro1")
		);
			
	
	
	@Test
	public void headerHttp() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleHeaderHttp";
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					buildRequests_HeaderHttp(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}

	
	@Test
	public void urlInvocazione() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleUrlInvocazione";
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					ConsegnaCondizionaleByFiltroTest.buildRequests_UrlInvocazione(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}


	@Test
	public void parametroUrl() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleParametroUrl";
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					ConsegnaCondizionaleByFiltroTest.buildRequests_ParametroUrl(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}
	
	
	@Test
	public void contenuto() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleContenuto";

		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					ConsegnaCondizionaleByFiltroTest.buildRequests_Contenuto(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}

	
	@Test
	public void XForwardedFor() throws UtilsException {
		final String erogazione = "LoadBalanceConsegnaCondizionaleXForwardedFor";
		var forwardingHeaders = HttpUtilities.getClientAddressHeaders();

		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					ConsegnaCondizionaleByFiltroTest.buildRequests_ForwardedFor(e.getValue(), forwardingHeaders, erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}
	
	
	@Test
	public void clientIp() {
		/**
		 * Non ho modo di cambiare l'indirizzo ip sorgente, per cui verifico semplicemente
		 * che tutto vada a finire nello stesso connettore.
		 */
		final String erogazione = "LoadBalanceConsegnaCondizionaleClientIp";
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		var responses = Utils.makeParallelRequests(request, 15);
		
		checkResponses(Map.of(POOL_LOCALHOST, responses));
	}
	
	
	@Test
	public void template() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleTemplate";
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					ConsegnaCondizionaleByFiltroTest.buildRequests_Template(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}
	
	
	@Test
	public void freemarkerTemplate() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleFreemarkerTemplate";
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					ConsegnaCondizionaleByFiltroTest.buildRequests_FreemarkerTemplate(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}
	
	
	@Test
	public void velocityTemplate() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleVelocityTemplate";
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					ConsegnaCondizionaleByFiltroTest.buildRequests_VelocityTemplate(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}
	
	
	@Test
	public void regole() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleRegole";
		
		// Configurazione Filtro Regola Statica: Pool2-Filtro1
		//	Va sui connettori del pool2
		
		HttpRequest requestIdentificazioneStatica = new HttpRequest();
		requestIdentificazioneStatica.setMethod(HttpRequestMethod.GET);
		requestIdentificazioneStatica.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-statica"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		

		HttpRequest requestClientIp = new HttpRequest();
		requestClientIp.setMethod(HttpRequestMethod.GET);
		requestClientIp.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-client-ip"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		// TODO
		
	}
	
	
	/**
	 * Verifica che le richieste siano arrivate ai pool indicati e che nei pool
	 * indicati il round robin sia avvenuto correttamente.
	 *  
	 * @param responsesByPool
	 */

	private static void checkResponses(Map<String, List<HttpResponse>> responsesByPool) {
		for(String pool : responsesByPool.keySet()) {
			List<HttpResponse> responses = responsesByPool.get(pool);
			List<String> connettoriPool = connettoriPools.get(pool);

			// Verifico che le richieste siano arrivate ai pools adeguati
			for (var resp : responses) {
				String connettore_utilizzato = resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
				assertTrue(connettoriPool.contains(connettore_utilizzato));
				assertEquals(200, resp.getResultHTTPOperation());
			}

			// Verifico la strategia di round robin per il pool
			// Per questo pool ho.
			// - responses.size() richieste smistate dalla strategia round robin
			// - connettori.size() pool che partecipano al round robin
			// - responses.size() / connettori.size() richieste che mi aspetto per ciascun connettore del pool
			// - responses.size() % connettori.size() richieste andate su altri connettori.
			//  	secondo la politica round robin non posso avere un connettore che ha ricevuto 
			// 		due richieste in più rispetto ad un altro, quindi testo questa condizione.
			
			int nrequests = responses.size();
			int nconnettori = connettoriPool.size();
			int requests_per_connettore = nrequests / nconnettori;
			int min_req = Integer.MAX_VALUE;
			int max_req = Integer.MIN_VALUE;
			Map<String,Integer> connettoriUtilizzati = contaConnettoriUtilizzati(responses);
			
			for(var connettore : connettoriUtilizzati.keySet()) {
				int n = connettoriUtilizzati.get(connettore);
				assertTrue(n >= requests_per_connettore);
				if(n<min_req) min_req = n;
				if(n>max_req) max_req = n;
			}
			assertTrue(max_req-min_req <= 1);
		}
	}


	private Map<String, List<HttpResponse>> makeBatchedRequests(Map<String, List<HttpRequest>> requestsByPool) {
		Map<String, List<HttpResponse>> responsesByPool = new ConcurrentHashMap<>();
		for(var e : requestsByPool.keySet()) {
			responsesByPool.put(e, new Vector<HttpResponse>());
		}
		
		int nthreads = 15;
		assertTrue(nthreads <= Integer.valueOf(System.getProperty("soglia_richieste_simultanee")));		
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nthreads);
		
		int i = 0;
		int pool_index = 0;
		int npools = requestsByPool.keySet().size();
		
		while(i<nthreads) {
			String pool = pools.get(pool_index%npools);
			var requests = requestsByPool.get(pool);
			int req_index = i % 2;
			
			executor.execute(() -> {
				HttpRequest request = requests.get(req_index);
				responsesByPool.get(pool).add(Utils.makeRequest(request));
				requests.add(request);
			});
			
			// Ogni due richieste cambio pool, chiaramente se ho meno di due richieste
			// per pool il test crasha dando errore alla requests.remove(0)
			i++;
			if (i%2 == 0) {
				pool_index++;
			}
		}
		
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logCore.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		return responsesByPool;
	}


	private static Map<String, Integer> contaConnettoriUtilizzati(List<HttpResponse> responses) {
		Map<String,Integer> ret = new HashMap<>();
		for (var r : responses) {
			String connettore = r.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
			ret.put(connettore, ret.getOrDefault(connettore, 0) + 1);			
		}
		return ret;
	}
	

}
