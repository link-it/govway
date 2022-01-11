package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;

// Query Freemarker Template: ${query["govway-testsuite-id_connettore_request"]}
// Query Template: ${query:govway-testsuite-id_connettore_request}
// Query Velocity Template: $query["govway-testsuite-id_connettore_request"]
// Query UrlInvocazione: .+govway-testsuite-id_connettore_request=([^&]*).*
// Query contenuto: $.id_connettore_request
// Query HeaderHttp: GovWay-TestSuite-id_connettore

public class Common {
	public static final String CONNETTORE_0 = "Connettore0";
	public static final String CONNETTORE_1 = "Connettore1";
	public static final String CONNETTORE_2 = "Connettore2";
	public static final String CONNETTORE_3 = "Connettore3";
	public static final String CONNETTORE_ROTTO = "ConnettoreRotto";
	public static final String CONNETTORE_DISABILITATO = "ConnettoreDisabilitato";
	public static final String HEADER_CONDIZIONE = "GovWay-TestSuite-Connettore";

	public static final String ID_CONNETTORE_REPLY_PREFIX = "GovWay-TestSuite-";
	public static final String HEADER_ID_CONNETTORE = ID_CONNETTORE_REPLY_PREFIX + "id_connettore";

	public static final int durataBloccante = Integer
			.valueOf(System.getProperty("connettori.load_balancer.least_connections.durata_bloccante"));

	public static final int delayRichiesteBackground = Integer
			.valueOf(System.getProperty("connettori.load_balancer.least_connections.delay_richieste_background"));

	public static final List<String> connettoriAbilitati = Arrays.asList(
			Common.CONNETTORE_0,
			Common.CONNETTORE_1, 
			Common.CONNETTORE_2,
			Common.CONNETTORE_3);
	
	
	public static void printMap(Map<String, Integer> howManys) {
		for (var e : howManys.entrySet()) {
			System.out.println(e.getKey() + ": " + e.getValue());
		}
	}

	
	// TODO: Forse questi posso rimetterli dentro ConsegnaCondizionaleFiltroNome
	public static void matchResponsesWithConnettori(List<String> connettori,
			Vector<Vector<HttpResponse>> responsesByConnettore) {
		for (int i = 0; i < connettori.size(); i++) {
			String connettoreRichiesta = connettori.get(i);
			
			/*if (connettoreRichiesta.equals(CONNETTORE_DISABILITATO)) {
				for (var response : responsesByConnettore.get(i)) {
					assertEquals(400,response.getResultHTTPOperation());
				}
			} else if (connettoreRichiesta.equals(CONNETTORE_ROTTO)) {
				for (var response : responsesByConnettore.get(i)) {
					assertEquals(400,response.getResultHTTPOperation());
				}
			} else {*/
				for (var response : responsesByConnettore.get(i)) {
					String connettoreRisposta = response.getHeaderFirstValue(HEADER_ID_CONNETTORE);
					assertEquals(connettoreRichiesta, connettoreRisposta);
				}
			//}
		}
	}

	/*
	 * Esegue un thread per ogni richiesta e per ogni thread esegue
	 * requests_per_batch richieste
	 * 
	 * Restituisce le risposte raggruppate per richiesta, e.g: il primo vettore di
	 * risposte corrisponde al batch di richieste fatte per la prima richiesta della
	 * lista `requests`
	 * TODO: Credo posso rimetterlo in ConsegnaCondizionaleFiltroNome
	 */
	public static Vector<Vector<HttpResponse>> makeBatchedRequests(List<HttpRequest> requests, int requests_per_batch) {

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(requests.size());
		var ret = new Vector<Vector<HttpResponse>>(requests.size());

		for (int i = 0; i < requests.size(); i++) {
			ret.add(new Vector<>());
			int index = i;

			executor.execute(() -> {
				ret.get(index).addAll(Utils.makeSequentialRequests(requests.get(index), requests_per_batch));
			});
		}

		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// logRateLimiting.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}

		return ret;
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


	public static String getIdConnettore(HttpResponse response) {
		if (response.getResultHTTPOperation() == 200) {				 
			return response.getHeaderFirstValue(HEADER_ID_CONNETTORE);
		} else {
			return LoadBalanceSempliceTest.CONNETTORE_ROTTO;
		}		
	}

	
}
