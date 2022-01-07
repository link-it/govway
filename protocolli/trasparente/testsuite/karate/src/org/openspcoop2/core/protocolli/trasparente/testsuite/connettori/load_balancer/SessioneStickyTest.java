package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;

//Query Freemarker Template: ${query["govway-testsuite-id_sessione_request"]}
//Query Template: ${query:govway-testsuite-id_sessione_request}
//Query Velocity Template: $query["govway-testsuite-id_sessione_request"]
//Query UrlInvocazione: .+govway-testsuite-id_sessione_request=([^&]*).*
//Query contenuto: $.id_sessione_request
//Query HeaderHttp: GovWay-TestSuite-ID-Sessione

public class SessioneStickyTest extends ConfigLoader {
	
	// TODO: Fai un unico test per la Max Age
	// TODO: Anche qui test con valori multipli per lo stesso header,
	//			conflitti fra gli headers appartenenti alla classe XForwardedFor
	// 			e test con valori mancanti per i null pointers

	// TODO: Chiedi ad andrea se vale la pena cambiare anche la strategia di load
	//		balancing invece di usare anche il round robin.
	//		Non cambia molto nei test, devo sostituire solo la parte finale, dove data una 
	//		lista di richieste e relative risposte, conto i connettori
	//		utilizzati e mi aspetto che i numeri seguano la strategia.
	//
	// NOTA: Nelle erogazioni della sessione sticky il connettore rotto è disabilitato in modo da
	//			non far durare troppo e inutilmente i test.
	//
	// TODO: Aggiungere in ogni caso nei check, la capacità di controllare anche le richieste finite ad
	//	un connettore rotto.
	//
	// 	Struttura dei test: tutte richieste in parallelo:
	//	due gruppi di richieste con id sessione impostato,
	//  un gruppo di richieste senza id sessione impostato e che segue un round robin per i conti propri
	//	verifico che le richieste con l'id sessione impostato siano finite sullo stesso connettore.
	//	verifico la politica di bilanciamento del carico per l'altro gruppo di richieste
	static String HEADER_ID_SESSIONE = "GovWay-TestSuite-ID-Sessione";
	static int richiesteParallele = 15;
	
	static List<String> IDSessioni = Arrays.asList(
			UUID.randomUUID().toString(),
			UUID.randomUUID().toString());	

	
	
	static HttpRequest buildRequest_HeaderHttp(String IDSessione, String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-header-http"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.addHeader(HEADER_ID_SESSIONE, IDSessione);
		return request;
	}
	

	static HttpRequest buildRequest_LoadBalanced(String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		return request;
	}
	

	static HttpRequest buildRequest_UrlInvocazione(String IDSessione, String erogazione) {
		// L'espressione regolare sull'erogazione matcha il parametro query govway-testsuite-id_connettore_request.		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-url-invocazione"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_sessione_request="+IDSessione); 		 
		return request;
	}
	

	
	@Test
	public void headerHttp() {
		
		final String erogazione = "LoadBalanceSessioneStickyHeaderHttp";
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_HeaderHttp(IDSessioni.get(0), erogazione), 
				buildRequest_HeaderHttp(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		checkAllResponsesSameConnettore(responsesByKind.get(0));
		checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Controllo che la politica di round robin sia andata a buon fine
		checkRoundRobin(responsesByKind.get(2), Arrays.asList(Common.CONNETTORE_0));
	}


	@Test	
	public void urlInvocazione() {
		final String erogazione = "LoadBalanceSessioneStickyUrlInvocazione";
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_UrlInvocazione(IDSessioni.get(0), erogazione), 
				buildRequest_UrlInvocazione(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		checkAllResponsesSameConnettore(responsesByKind.get(0));
		checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Controllo che la politica di round robin sia andata a buon fine
		checkRoundRobin(responsesByKind.get(2), Arrays.asList(Common.CONNETTORE_0));

	}
	

	private Map<Integer, List<HttpResponse>> makeRequests(List<HttpRequest> richieste, int nthreads) {
		assertTrue(nthreads <= Integer.valueOf(System.getProperty("soglia_richieste_simultanee")));

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nthreads);
		int i = 0;
		Map<Integer,List<HttpResponse>> responsesByKind = new ConcurrentHashMap<>();
		while(i<nthreads) {
			
			int index = i%richieste.size();
			if (!responsesByKind.containsKey(index)) {
				responsesByKind.put(index, new Vector<HttpResponse>());
			}
			
			executor.execute(() -> {
				var resp = Utils.makeRequest(richieste.get(index));
				responsesByKind.get(index).add(resp);
			});
			i++;
		}
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logCore.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		return responsesByKind;
	}
	
	


	private void checkRoundRobin(List<HttpResponse> list, List<String> asList) {
		// TODO Auto-generated method stub
		
	}


	private void checkAllResponsesSameConnettore(List<HttpResponse> responsesSessione0) {
		String connettore = responsesSessione0.get(0).getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
		for(var resp : responsesSessione0) {
			assertEquals(connettore, resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		}
	}

}
