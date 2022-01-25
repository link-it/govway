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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_0;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_3;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_DISABILITATO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.connettoriAbilitati;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * ConsegnaCondizionaleByFiltroTest
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * 
 * Query Freemarker Template: ${query["govway-testsuite-id_connettore_request"]}
 * Query Template: ${query:govway-testsuite-id_connettore_request}
 * Query Velocity Template: $query["govway-testsuite-id_connettore_request"]
 * Query UrlInvocazione: .+govway-testsuite-id_connettore_request=([^&]*).*
 * Query contenuto: $.id_connettore_request
 * Query HeaderHttp: GovWay-TestSuite-id_connettore
 * 
 * I test seguono lo schema per la consegna condizionale by nome, solo che ogni connettore
 * viene raggiunto da due filtri, invece che solo da uno.
 * 
 */
public class ConsegnaCondizionaleByFiltroTest extends ConfigLoader {
	
	static Map<String,List<String>> filtriConnettori = Map.of(
			CONNETTORE_0, Arrays.asList("Connettore0-Filtro0", "Connettore0-Filtro1"),
			CONNETTORE_1, Arrays.asList("Connettore1-Filtro0", "Connettore1-Filtro1"),
			CONNETTORE_2, Arrays.asList("Connettore2-Filtro0", "Connettore2-Filtro1"),
			CONNETTORE_3, Arrays.asList("Connettore3-Filtro0", "Connettore3-Filtro1")
		);
			
	
	@Test
	public void headerHttp() {
		
		final String erogazione = "ConsegnaCondizionaleHeaderHttpByFiltro";
		
		// Da qui in poi Il pattern per i test della consegna condizionale è lo steso per tutti.
		// Mando tanti thread in parallelo quanti sono i connettori da raggiungere, con ciascun
		// thread che raggiunge sempre lo stesso connettore.
		// Quindi Controllo che nelle risposte di quel thread sia stato raggiunto sempre lo stesso connettore.
		
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		
		connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(
					connettore,
					RequestBuilder.buildRequests_HeaderHttp(filtriConnettori.get(connettore), erogazione));
		});
		
		HttpRequest requestIdentificazioneFallita = Common.buildRequest_Semplice(erogazione);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildRequest_HeaderHttp("ConnettoreInesistente", erogazione);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildRequest_HeaderHttp(CONNETTORE_DISABILITATO, erogazione);
		
		requestsByConnettore.put(Common.CONNETTORE_DISABILITATO,List.of(requestConnettoreDisabilitato));
		requestsByConnettore.put(Common.CONNETTORE_ID_FALLITA,List.of(requestIdentificazioneFallita));
		requestsByConnettore.put(Common.CONNETTORE_ID_NON_TROVATO,List.of(requestConnettoreNonTrovato));
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		matchResponsesByConnettoreRest(responsesByConnettore);
	}
	
	
	
	@Test
	public void conflittoFiltri() {
		// Abilitando la proprietà connettoriMultipli.consegnaCondizionale.stessoFiltro=true
		// in console_local.properties è possibile assegnare lo stesso filtro a più connettori.
		// L'engine della consegna condizionale quando identifica più di un connettore candidato,
		// deve sollevare errore.
		
		final String erogazione = "ConsegnaCondizionaleConflittoFiltri";

		var requestError = RequestBuilder.buildRequest_HeaderHttp("Connettore0-Filtro0", erogazione);
		var requestOk = RequestBuilder.buildRequest_HeaderHttp("Connettore0-Filtro1", erogazione);
		
		var responseError = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeRequest(requestError);
		
		assertEquals(400, responseError.getResultHTTPOperation());
		
		var responseOk = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeRequest(requestOk);
		
		assertEquals(200, responseOk.getResultHTTPOperation());
		assertEquals(Common.CONNETTORE_0, responseOk.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
	}
	
	
	
	@Test
	public void urlInvocazione() {

		final String erogazione = "ConsegnaCondizionaleUrlInvocazioneByFiltro";
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		
		connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(
					connettore,
					RequestBuilder.buildRequests_UrlInvocazione(filtriConnettori.get(connettore), erogazione));
		});
		
		HttpRequest requestIdentificazioneFallita = Common.buildRequest_Semplice(erogazione);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildRequest_UrlInvocazione("ConnettoreInesistente", erogazione);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildRequest_UrlInvocazione(CONNETTORE_DISABILITATO, erogazione);
		
		requestsByConnettore.put(Common.CONNETTORE_DISABILITATO,List.of(requestConnettoreDisabilitato));
		requestsByConnettore.put(Common.CONNETTORE_ID_FALLITA,List.of(requestIdentificazioneFallita));
		requestsByConnettore.put(Common.CONNETTORE_ID_NON_TROVATO,List.of(requestConnettoreNonTrovato));
		
		// La prima volta che viene eseguito fallisce, viene superata la soglia richieste simultanee.
		// Questo perchè govway non ha ancora le cache pronte e impiega tempo per rispondere e nel mentre
		// aggiorna i contatori per il numero di richieste simultanee, ne arrivano di nuove che fanno superare
		// la soglia.
		// Stranamente accade solo per questo test. Faccio prima un giro di richieste sequenziali per popolare
		// la cache in modo che makeBatchedRequests non generi  dei 429
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,1);
		matchResponsesByConnettoreRest(responsesByConnettore);
		
		org.openspcoop2.utils.Utilities.sleep(Common.delayRichiesteBackground);
		
		responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		matchResponsesByConnettoreRest(responsesByConnettore);
	}
	

	
	@Test
	public void parametroUrl() {
		
		final String erogazione = "ConsegnaCondizionaleParametroUrlByFiltro";	
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		
		connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(
					connettore,
					RequestBuilder.buildRequests_ParametroUrl(filtriConnettori.get(connettore), erogazione));
		});
		
		HttpRequest requestIdentificazioneFallita = Common.buildRequest_Semplice(erogazione);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildRequest_ParametroUrl("ConnettoreInesistente", erogazione);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildRequest_ParametroUrl(CONNETTORE_DISABILITATO, erogazione);
		
		requestsByConnettore.put(Common.CONNETTORE_DISABILITATO,List.of(requestConnettoreDisabilitato));
		requestsByConnettore.put(Common.CONNETTORE_ID_FALLITA,List.of(requestIdentificazioneFallita));
		requestsByConnettore.put(Common.CONNETTORE_ID_NON_TROVATO,List.of(requestConnettoreNonTrovato));
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		matchResponsesByConnettoreRest(responsesByConnettore);
	}

	
	@Test
	public void contenuto() {
		
		final String erogazione = "ConsegnaCondizionaleContenutoByFiltro";
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		
		connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(
					connettore,
					RequestBuilder.buildRequests_Contenuto(filtriConnettori.get(connettore), erogazione));
		});
		
		HttpRequest requestIdentificazioneFallita = Common.buildRequest_Semplice(erogazione);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildRequest_Contenuto("ConnettoreInesistente", erogazione);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildRequest_Contenuto(CONNETTORE_DISABILITATO, erogazione);
		
		requestsByConnettore.put(Common.CONNETTORE_DISABILITATO,List.of(requestConnettoreDisabilitato));
		requestsByConnettore.put(Common.CONNETTORE_ID_FALLITA,List.of(requestIdentificazioneFallita));
		requestsByConnettore.put(Common.CONNETTORE_ID_NON_TROVATO,List.of(requestConnettoreNonTrovato));
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		matchResponsesByConnettoreRest(responsesByConnettore);
	}
	
	
	@Test
	public void soapAction1_1() {
		
		final String erogazione = "ConsegnaCondizionaleSoapActionByFiltro";
		soapActionImpl(erogazione, HttpConstants.CONTENT_TYPE_SOAP_1_1);		
	}
	
	

	@Test
	public void soapAction1_2() {
		
		final String erogazione = "ConsegnaCondizionaleSoapActionByFiltro";
		soapActionImpl(erogazione, HttpConstants.CONTENT_TYPE_SOAP_1_2);		
	}
	
	
	@Test
	public void template() {
		
		final String erogazione = "ConsegnaCondizionaleTemplateByFiltro";		
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		
		connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(
					connettore,
					RequestBuilder.buildRequests_Template(filtriConnettori.get(connettore), erogazione));
		});
		
		HttpRequest requestIdentificazioneFallita = Common.buildRequest_Semplice(erogazione);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildRequest_Template("ConnettoreInesistente", erogazione);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildRequest_Template(CONNETTORE_DISABILITATO, erogazione);
		
		requestsByConnettore.put(Common.CONNETTORE_DISABILITATO,List.of(requestConnettoreDisabilitato));
		requestsByConnettore.put(Common.CONNETTORE_ID_FALLITA,List.of(requestIdentificazioneFallita));
		requestsByConnettore.put(Common.CONNETTORE_ID_NON_TROVATO,List.of(requestConnettoreNonTrovato));
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		matchResponsesByConnettoreRest(responsesByConnettore);
	}
	
	
	@Test
	public void freemarkerTemplate() {
		
		final String erogazione = "ConsegnaCondizionaleFreemarkerTemplateByFiltro";
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		
		connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(
					connettore, 
					RequestBuilder.buildRequests_FreemarkerTemplate(filtriConnettori.get(connettore), erogazione));
		});
		
		HttpRequest requestIdentificazioneFallita = Common.buildRequest_Semplice(erogazione);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildRequest_FreemarkerTemplate("ConnettoreInesistente", erogazione);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildRequest_FreemarkerTemplate(CONNETTORE_DISABILITATO, erogazione);
		
		requestsByConnettore.put(Common.CONNETTORE_DISABILITATO,List.of(requestConnettoreDisabilitato));
		requestsByConnettore.put(Common.CONNETTORE_ID_FALLITA,List.of(requestIdentificazioneFallita));
		requestsByConnettore.put(Common.CONNETTORE_ID_NON_TROVATO,List.of(requestConnettoreNonTrovato));
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		matchResponsesByConnettoreRest(responsesByConnettore);
	}


	@Test
	public void velocityTemplate() {
		
		final String erogazione = "ConsegnaCondizionaleVelocityTemplateByFiltro";		
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		
		connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(
					connettore, 
					RequestBuilder.buildRequests_VelocityTemplate(filtriConnettori.get(connettore), erogazione));
		});
		
		HttpRequest requestIdentificazioneFallita = Common.buildRequest_Semplice(erogazione);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildRequest_VelocityTemplate("ConnettoreInesistente", erogazione);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildRequest_VelocityTemplate(CONNETTORE_DISABILITATO, erogazione);
		
		requestsByConnettore.put(Common.CONNETTORE_DISABILITATO,List.of(requestConnettoreDisabilitato));
		requestsByConnettore.put(Common.CONNETTORE_ID_FALLITA,List.of(requestIdentificazioneFallita));
		requestsByConnettore.put(Common.CONNETTORE_ID_NON_TROVATO,List.of(requestConnettoreNonTrovato));
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		matchResponsesByConnettoreRest(responsesByConnettore);
	}
	
	
	@Test
	public void clientIp() {
		/**
		 * Non ho modo di cambiare l'indirizzo ip sorgente, per cui verifico semplicemente
		 * che tutto vada a finire nello stesso connettore.
		 */
		final String erogazione = "ConsegnaCondizionaleClientIpByFiltro";
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		var responses = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeParallelRequests(request, 15);
		for (var resp : responses) {
			assertEquals(200, resp.getResultHTTPOperation());
			assertEquals(CONNETTORE_0, resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));			
		}	
	}
	
	
	@Test
	public void XForwardedFor() throws UtilsException {

		final String erogazione = "ConsegnaCondizionaleXForwardedForByFiltro";
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		var headers = HttpUtilities.getClientAddressHeaders();

		connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(
					connettore, 
					RequestBuilder.buildRequests_ForwardedFor(filtriConnettori.get(connettore), headers, erogazione));
		});
		
		
		HttpRequest requestIdentificazioneFallita = Common.buildRequest_Semplice(erogazione);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildRequest_ForwardedFor("ConnettoreInesistente", erogazione);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildRequest_ForwardedFor(CONNETTORE_DISABILITATO, erogazione);
		
		requestsByConnettore.put(Common.CONNETTORE_DISABILITATO,List.of(requestConnettoreDisabilitato));
		requestsByConnettore.put(Common.CONNETTORE_ID_FALLITA,List.of(requestIdentificazioneFallita));
		requestsByConnettore.put(Common.CONNETTORE_ID_NON_TROVATO,List.of(requestConnettoreNonTrovato));
	
	}

	
	@Test
	public void regole() throws UtilsException {
		/**
		 * Ogni regola la mando su uno specifico connettore.
		 * 
		 * Connettore0 => HeaderHttp, UrlInvocazione, Statica (come da configurazione erogazione)
		 * Connettore1 => QueryParams, Contenuto
		 * Connettore2 => XForwardedFor, Template, ClientIp (come da configurazione erogazione)
		 * Connettore3 => FreemarkerTemplate, VelocityTemplate 
		 */
		
		final String erogazione = "ConsegnaCondizionaleRegoleByFiltro";
		
		HttpRequest requestIdentificazioneStatica = new HttpRequest();
		requestIdentificazioneStatica.setMethod(HttpRequestMethod.GET);
		requestIdentificazioneStatica.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-statica"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		

		HttpRequest requestClientIp = new HttpRequest();
		requestClientIp.setMethod(HttpRequestMethod.GET);
		requestClientIp.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-client-ip"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);

		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		connettoriAbilitati.forEach( c -> requestsByConnettore.put(c, new ArrayList<HttpRequest>()));
		
		requestsByConnettore.get(CONNETTORE_0)
			.addAll(RequestBuilder.buildRequests_HeaderHttp(filtriConnettori.get(CONNETTORE_0), erogazione));
		
		requestsByConnettore.get(CONNETTORE_0)
			.addAll(RequestBuilder.buildRequests_UrlInvocazione(filtriConnettori.get(CONNETTORE_0), erogazione));
		
		requestsByConnettore.get(CONNETTORE_0)
			.add(requestIdentificazioneStatica);

		
		requestsByConnettore.get(CONNETTORE_1)
			.addAll(RequestBuilder.buildRequests_ParametroUrl(filtriConnettori.get(CONNETTORE_1), erogazione));
		
		requestsByConnettore.get(CONNETTORE_1)
			.addAll(RequestBuilder.buildRequests_Contenuto(filtriConnettori.get(CONNETTORE_1), erogazione));
		
		
		requestsByConnettore.get(CONNETTORE_2)
			.addAll(RequestBuilder.buildRequests_ForwardedFor(filtriConnettori.get(CONNETTORE_2), HttpUtilities.getClientAddressHeaders(), erogazione));
		
		requestsByConnettore.get(CONNETTORE_2)
			.addAll(RequestBuilder.buildRequests_Template(filtriConnettori.get(CONNETTORE_2), erogazione));
		
		requestsByConnettore.get(CONNETTORE_2).add(requestClientIp);
		
		requestsByConnettore.get(CONNETTORE_3)
			.addAll(RequestBuilder.buildRequests_VelocityTemplate(filtriConnettori.get(CONNETTORE_3), erogazione));
		
		requestsByConnettore.get(CONNETTORE_3)
			.addAll(RequestBuilder.buildRequests_FreemarkerTemplate(filtriConnettori.get(CONNETTORE_3), erogazione));
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		
		matchResponsesByConnettoreRest(responsesByConnettore);
		
	}
	

	private static void soapActionImpl(String erogazione, String versioneSoap) {
		// Non posso avere due azioni che si chiamano allo stesso modo, quindi uso solo un filtro.

		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
 
		connettoriAbilitati.forEach( connettore -> {
			String filtroConnettore = filtriConnettori.get(connettore).get(0);
			String azione = connettore;
			requestsByConnettore.put(
					connettore,
					Arrays.asList( RequestBuilder.buildSoapRequest(erogazione, azione, filtroConnettore, versioneSoap)) );
		});
		
			
		HttpRequest requestIdentificazioneFallita = RequestBuilder.buildSoapRequest_Semplice(erogazione,versioneSoap);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildSoapRequest(erogazione, "ConnettoreInesistente", "ConnettoreInesistente", versioneSoap);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildSoapRequest(erogazione, CONNETTORE_DISABILITATO, CONNETTORE_DISABILITATO+"-Filtro0", versioneSoap);
		
		requestsByConnettore.put(Common.CONNETTORE_DISABILITATO,List.of(requestConnettoreDisabilitato));
		requestsByConnettore.put(Common.CONNETTORE_ID_FALLITA,List.of(requestIdentificazioneFallita));
		requestsByConnettore.put(Common.CONNETTORE_ID_NON_TROVATO,List.of(requestConnettoreNonTrovato));
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		matchResponsesByConnettoreSoap(responsesByConnettore);
		
	}
	

	
	/**
	 * Per ogni chiave della map @requestsByConnettore vengono fatte @requests_per_batch
	 * richieste pescandol la richiesta dalla lista assegnata.
	 * 
	 */
	static Map<String,List<HttpResponse>> makeBatchedRequests(Map<String,List<HttpRequest>> requestsByConnettore, int requests_per_batch) {
		var responsesByConnettore = new ConcurrentHashMap<String,List<HttpResponse>>();
		
		int nThreads = Common.sogliaRichiesteSimultanee; 
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);

		for (var connettore : requestsByConnettore.keySet()) {			
			responsesByConnettore.put(connettore, new ArrayList<>());
			
			executor.execute(() -> {
				for(var request : requestsByConnettore.get(connettore)) {
					responsesByConnettore.get(connettore).addAll(org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeSequentialRequests(request, requests_per_batch));
				}
			});
		}
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logCore.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		return responsesByConnettore;
	}


	// Verifico che le risposte sotto una certa chiave siano finite tutte sotto lo stesso
	// connettore che corrisponde alla chiave.
	
	static void matchResponsesByConnettoreRest(Map<String, List<HttpResponse>> responsesByConnettore) {
		for (String connettore : responsesByConnettore.keySet()) {
			
			var responses = responsesByConnettore.get(connettore);
			
			if (connettore.equals(CONNETTORE_DISABILITATO) || 
				connettore.equals(Common.CONNETTORE_ID_FALLITA) || 
				connettore.equals(Common.CONNETTORE_ID_NON_TROVATO)) {
				
				for (var response : responses) {
					assertEquals(400,response.getResultHTTPOperation());
				}
			} else {			
				for (var resp: responses) {
					assertEquals(200,resp.getResultHTTPOperation());
					assertEquals(connettore, resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
				}
			}
		}
	}
	
	
	static void matchResponsesByConnettoreSoap(Map<String, List<HttpResponse>> responsesByConnettore) {
		for (String connettore : responsesByConnettore.keySet()) {
			
			var responses = responsesByConnettore.get(connettore);
			
			if (connettore.equals(CONNETTORE_DISABILITATO) || 
				connettore.equals(Common.CONNETTORE_ID_FALLITA) || 
				connettore.equals(Common.CONNETTORE_ID_NON_TROVATO)) {
				
				for (var response : responses) {
					assertEquals(500,response.getResultHTTPOperation());
				}
			} else {			
				for (var resp: responses) {
					assertEquals(200,resp.getResultHTTPOperation());
					assertEquals(connettore, resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
				}
			}
		}
	}

}
