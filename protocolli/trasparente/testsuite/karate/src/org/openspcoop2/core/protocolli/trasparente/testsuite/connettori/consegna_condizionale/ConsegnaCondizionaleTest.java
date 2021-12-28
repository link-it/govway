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


package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * RestTest
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 *	Connettore0, Connettore1, Connettore2, Connettore3, ConnettoreDisabilitato, ConnettoreRotto
 *	
 *	Testo anche che al connettore disabilitato non venga instradato nulla, ogni politica ha un connettore disabilitato.
 *
 *  Invio N batch (per ora 1) di 15 richieste parallele, tre per connettore. Verifico che ciascuna richiesta
 *  raggiunga il connettore deisderato. In questo modo simulo un pattern di ri richieste verosimile. 
 * 
 */


public class ConsegnaCondizionaleTest extends ConfigLoader {
	
	public static final String CONNETTORE_0 = "Connettore0";
	public static final String CONNETTORE_1 = "Connettore1";
	public static final String CONNETTORE_2 = "Connettore2";
	public static final String CONNETTORE_3 = "Connettore3";
	
	public static final String CONNETTORE_ROTTO = "ConnettoreRotto";
	public static final String CONNETTORE_DISABILITATO = "ConnettoreDisabilitato";
	public static final String HEADER_CONDIZIONE = "GovWay-TestSuite-Connettore";

	private static final List<String> connettoriAbilitati = Arrays.asList(
			CONNETTORE_0,
			CONNETTORE_1,
			CONNETTORE_2,
			CONNETTORE_3);
	
	HttpRequest buildRequest_HeaderHttpByNome(String connettore) {
		final String erogazione = "ConsegnaCondizionaleHeaderHttpByNome";
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.addHeader(HEADER_CONDIZIONE, connettore);
		
		return request;
	}
	

	HttpRequest buildRequest_UrlInvocazioneByNome(String connettore) {

		// L'espressione regolare sull'erogazione matcha il parametro query govway-testsuite-id_connettore_request.

		final String erogazione = "ConsegnaCondizionaleUrlInvocazioneByNome";
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request="+connettore); 		 

		return request;
	}
	
	
	HttpRequest buildRequest_ParametroUrlByNome(String connettore) {

		// L'espressione regolare sull'erogazione matcha il parametro query govway-testsuite-id_connettore_request.

		final String erogazione = "ConsegnaCondizionaleParametroUrlByNome";
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request="+connettore); 		 

		return request;
	}
	
	HttpRequest buildRequest_ContenutoByNome(String connettore) {

		// L'espressione regolare sull'erogazione matcha il parametro query govway-testsuite-id_connettore_request.

		final String erogazione = "ConsegnaCondizionaleContenutoByNome";
		final String content = "{ \"id_connettore_request\": \""+connettore+"\" }"; 
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.setContentType("application/json");
		request.setContent(content.getBytes());

		return request;
	}
	
	
	HttpRequest buildRequest_TemplateByNome(String connettore) {

		// L'espressione regolare sull'erogazione matcha il parametro query govway-testsuite-id_connettore_request.

		final String erogazione = "ConsegnaCondizionaleTemplateByNome";
		
		HttpRequest request = new HttpRequest();
		//request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request="+connettore); 		 

		return request;
	}
	
	

	/* 
	 * Esegue un thread per ogni richiesta e per ogni thread esegue requests_per_batch richieste
	 * 
	 * Restituisce le risposte raggruppate per richiesta, e.g: il primo vettore di risposte corrisponde
	 * al batch di richieste fatte per la prima richiesta della lista `requests`
	 * 
	 */
	Vector<Vector<HttpResponse>> makeBatchedRequests(List<HttpRequest> requests, int requests_per_batch) {
		
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(requests.size());
		var ret = new Vector<Vector<HttpResponse>>(requests.size());
		
		for(int i=0; i<requests.size();i++) {
			ret.add(new Vector<>());
			int index=i;
			
			executor.execute(() -> {
					ret.get(index).addAll(Utils.makeSequentialRequests(requests.get(index), requests_per_batch));
			});
		}
		
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logRateLimiting.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
	
		return ret;
	}
	
	@Test
	public void headerHttpByNome() {
		
		HttpRequest request0 = buildRequest_HeaderHttpByNome(CONNETTORE_0);
		HttpRequest request1 = buildRequest_HeaderHttpByNome(CONNETTORE_1);
		HttpRequest request2 = buildRequest_HeaderHttpByNome(CONNETTORE_2);
		HttpRequest request3 = buildRequest_HeaderHttpByNome(CONNETTORE_3);
		//HttpRequest requestRotto = buildRequest(CONNETTORE_ROTTO);

		// Non testo il connettore rotto nella consegna condizionale altrimenti i test ci mettono troppo
		HttpRequest[] requestsByConnettore = { request0, request1, request2, request3/*, requestRotto*/ };
		List<List<HttpResponse>> responsesByConnettore = new ArrayList<>(requestsByConnettore.length);
		responsesByConnettore.add(new ArrayList<>());
		responsesByConnettore.add(new ArrayList<>());
		responsesByConnettore.add(new ArrayList<>());
		responsesByConnettore.add(new ArrayList<>());
		responsesByConnettore.add(new ArrayList<>());
		
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(15);

		for (int i = 0; i < 15; i++) {			
			int index = i%requestsByConnettore.length;			
			executor.execute(() -> {
				try {
					responsesByConnettore.get(index).add(HttpUtilities.httpInvoke(requestsByConnettore[index]));
				} catch (UtilsException e) {
					throw new RuntimeException(e);
				}				
			});
		}
		
		
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logRateLimiting.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		
		for(int i=0;i<requestsByConnettore.length;i++) {
			String connettoreRichiesta = requestsByConnettore[i].getHeaderFirstValue(HEADER_CONDIZIONE);
			
			for(var response : responsesByConnettore.get(i)) {
				
				String connettoreRisposta = response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
				if (connettoreRisposta == null) {
					connettoreRisposta = CONNETTORE_ROTTO;
				}
				
				assertEquals(connettoreRichiesta, connettoreRisposta);
			}
			
		}
		
	}
	
	
	@Test
	public void urlInvocazioneByNome() {
		
		// Da qui in poi Il pattern per i test della consegna condizionale è lo steso per tutti.
		// Mando tanti thread in parallelo quanti sono i connettori da raggiungere, con ciascun
		// thread che raggiunge sempre lo stesso connettore.
		// Quindi Controllo che nelle risposte di quel thread sia stato raggiunto sempre lo stesso connettore. 
		
		var requestsByConnettore = connettoriAbilitati.stream()
				.map(this::buildRequest_UrlInvocazioneByNome)
				.collect(Collectors.toList());
							
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettori(connettoriAbilitati, responsesByConnettore);
	}

	
	@Test
	public void parametroUrlByNome() {
		
		var requestsByConnettore = connettoriAbilitati.stream()
				.map(this::buildRequest_ParametroUrlByNome)
				.collect(Collectors.toList());
							
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettori(connettoriAbilitati, responsesByConnettore);		

	}
	
	
	@Test
	public void contenutoByNome() {		
		
		var requestsByConnettore = connettoriAbilitati.stream()
				.map(this::buildRequest_ContenutoByNome)
				.collect(Collectors.toList());
							
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettori(connettoriAbilitati, responsesByConnettore);		
		
	}
	
	
	@Test
	public void testConnettoreDisabilitato() {
		
		List<String> connettori = Arrays.asList( CONNETTORE_0,
				CONNETTORE_1,
				CONNETTORE_2,
				CONNETTORE_3,
				CONNETTORE_DISABILITATO);
		
		var requestsByConnettore = connettori.stream()
				.map(this::buildRequest_ContenutoByNome)
				.collect(Collectors.toList());
							
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore, 3);
		
		for(int i=0;i<connettori.size();i++) {
			String connettoreRichiesta = connettori.get(i);
			
			if (CONNETTORE_DISABILITATO.equals(connettoreRichiesta)) {
				for(var response : responsesByConnettore.get(i)) {
					assertEquals(400, response.getResultHTTPOperation());
				}
				
			} else {			
				for(var response : responsesByConnettore.get(i)) {
					String connettoreRisposta = response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
					assertEquals(200, response.getResultHTTPOperation());
					assertEquals(connettoreRichiesta, connettoreRisposta);
				}
			}
		}		
	}
	
	
	@Test
	public void clientIpByNome() {
		/**
		 * Non ho modo di cambiare l'indirizzo ip sorgente, per cui verifico semplicemente
		 * che tutto vada a finire nello stesso connettore.
		 */
		final String erogazione = "ConsegnaCondizionaleClientIpByNome";
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		var responses = Utils.makeParallelRequests(request, 15);
		String connettore = responses.get(0).getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
		for (var resp : responses) {
			assertEquals(200, resp.getResultHTTPOperation());
			assertEquals(connettore, resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));			
		}	
	}
	
	
	@Test
	public void XForwardedForByNome() throws UtilsException {
		
		List<String> forwardedHeaders = HttpUtilities.getClientAddressHeaders();
		
		final String erogazione = "ConsegnaCondizionaleXForwardedForByNome";
		
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(15);
		Vector<Vector<HttpResponse>> responsesByConnettore = new Vector<>(connettoriAbilitati.size());

		// Inizializzo le risposte, alla richiesta di indice i verrà assegnato il connettore di indice i
		// responsesByConnettore.get(i) restituisce le risposte relative all'i-esimo connettore
		connettoriAbilitati.forEach( c -> responsesByConnettore.add(new Vector<>()));

		// Voglio usare tutti gli headers possibili, quindi pesco ogni volta un nome di header 
		// appartenente alla classe Forwarded-For diverso.
		
		for (int i = 0; i < 15; i++) {
			
			int index_connettore = i%connettoriAbilitati.size();
			int index_header = i%forwardedHeaders.size(); 
			
			executor.execute(() -> {
				try {
					String header_condizione = forwardedHeaders.get(index_header);
					String connettore = connettoriAbilitati.get(index_connettore);
					
					HttpRequest request = new HttpRequest();
					request.setMethod(HttpRequestMethod.GET);
					request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
							+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
					request.addHeader(header_condizione, connettore);
					
					responsesByConnettore.get(index_connettore).add(HttpUtilities.httpInvoke(request));
				} catch (UtilsException e) {
					throw new RuntimeException(e);
				}				
			});
		}
				
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logRateLimiting.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		
		matchResponsesWithConnettori(connettoriAbilitati, responsesByConnettore);
		
		
	}
	
	
	@Test
	public void templateByNome() {
				
		// TODO: Se non metto content-type application/json govway si arrabbia e non parte
		// l'identificazione col template, è un bug.
		
		// Il template pesca il valore del parametro query govway-testsuite-id_connettore_request,
		// riuso quanto fatto per il test parametro query
		
		var requestsByConnettore = connettoriAbilitati.stream()
				.map(this::buildRequest_TemplateByNome)
				.collect(Collectors.toList());
							
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettori(connettoriAbilitati, responsesByConnettore);		
		
	}


	private void matchResponsesWithConnettori(List<String> connettori, Vector<Vector<HttpResponse>> responsesByConnettore) {
		for(int i=0;i<connettori.size();i++) {
			String connettoreRichiesta = connettori.get(i);
			
			for(var response : responsesByConnettore.get(i)) {
				String connettoreRisposta = response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);			
				assertEquals(connettoreRichiesta, connettoreRisposta);
			}
		}
	}
	
	
	
	@Test
	public void headerHttpByFiltro() {
		
	}
	

	
	
	@Test
	public void identificazioneCondizioneFallitaLogError() {
		// Il connettore di fallback è il 3
		// TODO Chiedi ad andrea come verificare il messaggio di log, suppongo la traccia?
		// TODO: Faccio anche il test per "disabilitato?" l'obbiettivo è sempre tenere basso il tempo totale..

		
	}
	
	@Test
	public void identificazioneCondizioneFallitaLogInfo() {
	}
	
	
	@Test
	public void nessunConnettoreUtilizzabileLogError() {
		
	}
	
	
	@Test
	public void nessunConnettoreUtilizzabileLogInfo() {
		
	}
	
	@Test
	public void filtroNomeInHeaderHttp() {
		
	}
	
	@Test
	public void filtroNomeInUrlInvocazione() {
		
	}
	
	// ecc..
	// ....
	
	@Test
	public void filtroFiltroInHeaderHttp() {
		
	}
	
	
	@Test
	public void filtroFiltroInUrlInvocazione() {
		
	}
	
	// ecc... Forse farli in una nuova classe a sto punto
	
	
	@Test
	public void filtroNomeHeaderHttpRegole() {
		
	}
	
	
	
}
