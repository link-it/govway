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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.POOL_ROTTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * LoadBalanceConsegnaCondizionaleTest
 * 
 * @author Francesco Scarlato (scarlato@link.it)
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
 * 		- L'implementazione di govway tratta l'insieme di connettori identificato da un filtro come un un'unica 
 * 			unità sulla quale poi applicare la strategia di load balancing. 
 */

public class LoadBalanceConsegnaCondizionaleTest extends ConfigLoader {
	
	
	@Test
	public void headerHttp() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleHeaderHttp";
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : Common.filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildRequests_HeaderHttp(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}
	
	@Test
	public void identificazioneFallita() {
		/** 
		 * Raggiungo correttamente il pool0 e il pool1, e sbaglio l'identificazione per un 
		 * terzo pool. Quel terzo pool diventa quello dei connettori abilitati.
		 * Controllo che sia avvenuto il round robin sui tre pool
		 */
		
		final String erogazione = "LoadBalanceConsegnaCondizionaleIdentificazioneFallita";

		Map<String,List<String>> filtriPoolsIdentificazioneOk = Map
				.of(Common.POOL_0, Arrays.asList("Pool0-Filtro0"),
					Common.POOL_1, Arrays.asList("Pool1-Filtro0"));
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : filtriPoolsIdentificazioneOk.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildRequests_HeaderHttp(e.getValue(), erogazione)
				);			
		}
		
		HttpRequest requestIdentificazioneFallita = new HttpRequest();
		requestIdentificazioneFallita.setMethod(HttpRequestMethod.GET);
		requestIdentificazioneFallita.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		requestsByPool.put(
				Common.POOL_IDENTIFICAZIONE_FALLITA,
				Arrays.asList( requestIdentificazioneFallita )
			);
		
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
		
		// Controllo anche che le richieste con l'identificazione fallita abbiano
		// raggiunto tutti i connettori disponibili della erogazione
		
		Set<String> connettoriRaggiunti = responsesByPool.get(Common.POOL_IDENTIFICAZIONE_FALLITA)
				.stream()
				.map( resp -> resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE))
				.collect(Collectors.toSet());
		for(String connettore : Common.setConnettoriAbilitati) {
			assertTrue(connettoriRaggiunti.contains(connettore));
		}
	}
	
	
	@Test
	public void nessunConnettoreUtilizzabile() {
		// Uguale al test di identificazione fallita, però questa volta la condizione
		// la metto con un filtro inesistente.
		
		final String erogazione = "LoadBalanceConsegnaCondizionaleNessunConnettoreUtilizzabile";

		Map<String,List<String>> filtriPoolsIdentificazione = Map
				.of(Common.POOL_0, Arrays.asList("Pool0-Filtro0"),
					Common.POOL_1, Arrays.asList("Pool1-Filtro0"),
					Common.POOL_IDENTIFICAZIONE_FALLITA_NO_CONNETTORI, Arrays.asList("FiltroInesistente"));

		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : filtriPoolsIdentificazione.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildRequests_HeaderHttp(e.getValue(), erogazione)
				);			
		}
		
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
		
		// Controllo anche che le richieste con l'identificazione fallita abbiano
		// raggiunto tutti i connettori disponibili della erogazione
		
		Set<String> connettoriRaggiunti = responsesByPool.get(Common.POOL_IDENTIFICAZIONE_FALLITA_NO_CONNETTORI)
				.stream()
				.map( resp -> resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE))
				.collect(Collectors.toSet());
		for(String connettore : Common.setConnettoriAbilitati) {
			assertTrue(connettoriRaggiunti.contains(connettore));
		}
	}
	
	
	@Test
	public void identificazioneFallitaNessunConnettoreUtilizzabile() {
		// Come i test di sopra, ma ci sono due pool di richieste che vengono dirottati
		// sul pool di tutti i connettori 
		
		final String erogazione = "LoadBalanceConsegnaCondizionaleIdentificazioneFallitaNessunConnettoreUtilizzabile";
		
		Map<String,List<String>> filtriPoolsIdentificazioneOk = Map
				.of(Common.POOL_0, Arrays.asList("Pool0-Filtro0"),
					Common.POOL_IDENTIFICAZIONE_FALLITA_NO_CONNETTORI, Arrays.asList("FiltroInesistente"));
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : filtriPoolsIdentificazioneOk.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildRequests_HeaderHttp(e.getValue(), erogazione)
				);			
		}
		
		HttpRequest requestIdentificazioneFallita = new HttpRequest();
		requestIdentificazioneFallita.setMethod(HttpRequestMethod.GET);
		requestIdentificazioneFallita.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		requestsByPool.put(
				Common.POOL_IDENTIFICAZIONE_FALLITA,
				Arrays.asList( requestIdentificazioneFallita )
			);
		
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
		
		// Controllo anche che le richieste con l'identificazione fallita abbiano
		// raggiunto tutti i connettori disponibili della erogazione
		
		Set<String> connettoriRaggiunti = responsesByPool.get(Common.POOL_IDENTIFICAZIONE_FALLITA)
				.stream()
				.map( resp -> resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE))
				.collect(Collectors.toSet());
		for(String connettore : Common.setConnettoriAbilitati) {
			assertTrue(connettoriRaggiunti.contains(connettore));
		}
		
		connettoriRaggiunti = responsesByPool.get(Common.POOL_IDENTIFICAZIONE_FALLITA_NO_CONNETTORI)
				.stream()
				.map( resp -> resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE))
				.collect(Collectors.toSet());
		for(String connettore : Common.setConnettoriAbilitati) {
			assertTrue(connettoriRaggiunti.contains(connettore));
		}
	
	}
	
	
	@Test
	public void prefisso() {
		/*
		 * Mando solo i suffissi, la consegna condizionale aggiungerà il prefisso "Pool"
		 * mentre l'id connettore inviato al server di echo resta lo stesso.
		 */
		
		final String erogazione = "LoadBalanceConsegnaCondizionalePrefisso";
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : Common.filtriPools.entrySet()) {
			var filtri = e.getValue().stream()
					.map( filtro -> filtro.substring("Pool".length()))
					.collect(Collectors.toList());
			
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildRequests_HeaderHttp(filtri, erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}
	
	@Test
	public void prefissoESuffisso() {
		/**
		 * Il suffisso configurato sull'erogazione è: "-Filtro0", in queso modo raggiungo 
		 * solo i vari PoolX-Filtro0
		 * Il prefisso è "Pool"
		 * 
		 * Mando solo [0,1,2]
		 */
		
		final String erogazione = "LoadBalanceConsegnaCondizionalePrefissoESuffisso";
		
		 Map<String,List<String>> filtriPoolsNoSuffissoNoPrefisso = Map
					.of(Common.POOL_0, Arrays.asList("0"),
						Common.POOL_1, Arrays.asList("1"),
						Common.POOL_2, Arrays.asList("2"));

		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : filtriPoolsNoSuffissoNoPrefisso.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildRequests_HeaderHttp(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}
	
	
	
	@Test
	public void suffisso() {
		/**
		 * Il suffisso configurato sull'erogazione è: "-Filtro0", in queso modo raggiungo 
		 * solo i vari PoolX-Filtro0
		 * 
		 * Mando solo [Pool0,Pool1,Pool2]
		 */
		
		final String erogazione = "LoadBalanceConsegnaCondizionaleSuffisso";
		
		 Map<String,List<String>> filtriPoolsNoSuffissoNoPrefisso = Map
					.of(Common.POOL_0, Arrays.asList("Pool0"),
						Common.POOL_1, Arrays.asList("Pool1"),
						Common.POOL_2, Arrays.asList("Pool2"));

		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : filtriPoolsNoSuffissoNoPrefisso.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildRequests_HeaderHttp(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}

	
	@Test
	public void urlInvocazione() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleUrlInvocazione";
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : Common.filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildRequests_UrlInvocazione(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}
	
	
	//@Test
	public void soapAction1_1() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleSoapAction";
		soapAction_Impl(erogazione, HttpConstants.CONTENT_TYPE_SOAP_1_1);
	}
	
	
	//@Test 
	public void soapAction1_2() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleSoapAction";
		soapAction_Impl(erogazione, HttpConstants.CONTENT_TYPE_SOAP_1_2);
	}
	
	
	public void soapAction_Impl(String erogazione, String contentTypeVersioneSoap) {
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : Common.filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildSoapRequests(e.getValue(), erogazione, contentTypeVersioneSoap)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}


	@Test
	public void parametroUrl() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleParametroUrl";
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : Common.filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildRequests_ParametroUrl(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}
	
	
	@Test
	public void contenuto() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleContenuto";

		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : Common.filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildRequests_Contenuto(e.getValue(), erogazione)
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
		for (var e : Common.filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildRequests_ForwardedFor(e.getValue(), forwardingHeaders, erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool, forwardingHeaders.size()*4);

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
		
		var responses = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeParallelRequests(request, 15);
		
		checkResponses(Map.of(Common.POOL_LOCALHOST, responses));
	}
	
	
	@Test
	public void template() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleTemplate";
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : Common.filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildRequests_Template(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}
	
	
	@Test
	public void freemarkerTemplate() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleFreemarkerTemplate";
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : Common.filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildRequests_FreemarkerTemplate(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}
	
	
	@Test
	public void velocityTemplate() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleVelocityTemplate";
		
		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		for (var e : Common.filtriPools.entrySet()) {
			requestsByPool.put(
					e.getKey(),
					RequestBuilder.buildRequests_VelocityTemplate(e.getValue(), erogazione)
				);
		}
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool);

		checkResponses(responsesByPool);
	}
	
	@Test
	public void healthCheckOvunqueDisabilitato() {
		final String erogazione = "LoadBalanceConsegnaCondizionaleHealthCheck";
		
		// In questa erogazione abbiamo riuniti nel PoolRotto il [Connettore2, Connettore3, ConnettoreDisabilitato, ConnettoreRotto]
		// Contatto il connettore rotto in un pool.

		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		requestsByPool.put(POOL_ROTTO, 
					RequestBuilder.buildRequests_HeaderHttp(Arrays.asList("PoolRotto-Filtro0"), erogazione));
		
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool,3);
		var responses = responsesByPool.get(POOL_ROTTO);
		var howManys = Common.contaConnettoriUtilizzati(responses);
		Common.printMap(howManys);
		assertEquals(Integer.valueOf(1), howManys.get(Common.CONNETTORE_ROTTO));
		assertEquals(Integer.valueOf(1), howManys.get(Common.CONNETTORE_2));
		assertEquals(Integer.valueOf(1), howManys.get(Common.CONNETTORE_3));
		
		// Ripeto con lo stesso filtro, stavolta non devo contattare il pool rotto
		responsesByPool = makeBatchedRequests(requestsByPool,3);
		responses = responsesByPool.get(POOL_ROTTO);
		howManys = Common.contaConnettoriUtilizzati(responses);
		Common.printMap(howManys);
		assertEquals(null, howManys.get(Common.CONNETTORE_ROTTO));
		assertTrue(howManys.get(Common.CONNETTORE_2) > 0 && howManys.get(Common.CONNETTORE_2) < 3);
		assertTrue(howManys.get(Common.CONNETTORE_3) > 0 && howManys.get(Common.CONNETTORE_3) < 3);
		
		// Mi aspetto di ricontattarlo più se cambio filtro
		// Cambiando filtro questo viene ricontattato dalla politica di load balancer
		requestsByPool = new HashMap<>();
		requestsByPool.put(POOL_ROTTO, 
				RequestBuilder.buildRequests_HeaderHttp(Arrays.asList("PoolRotto-Filtro1"), erogazione));
	
		responsesByPool = makeBatchedRequests(requestsByPool,3);
		responses = responsesByPool.get(POOL_ROTTO);
		howManys = Common.contaConnettoriUtilizzati(responses);
		Common.printMap(howManys);
		
		assertEquals(Integer.valueOf(1), howManys.get(Common.CONNETTORE_ROTTO));
		assertEquals(Integer.valueOf(1), howManys.get(Common.CONNETTORE_2));
		assertEquals(Integer.valueOf(1), howManys.get(Common.CONNETTORE_3));

		// Attendo che il connettore venga reinserito e mi aspetto di raggiungere nuovamente
		// il connettore rotto
		org.openspcoop2.utils.Utilities.sleep(Common.intervalloEsclusione + 100);
		
		requestsByPool = new HashMap<>();
		requestsByPool.put(POOL_ROTTO, 
				RequestBuilder.buildRequests_HeaderHttp(Arrays.asList("PoolRotto-Filtro0"), erogazione));
	
		responsesByPool = makeBatchedRequests(requestsByPool,3);
		responses = responsesByPool.get(POOL_ROTTO);
		howManys = Common.contaConnettoriUtilizzati(responses);
		Common.printMap(howManys);
		assertEquals(Integer.valueOf(1), howManys.get(Common.CONNETTORE_ROTTO));
		assertEquals(Integer.valueOf(1), howManys.get(Common.CONNETTORE_2));
		assertEquals(Integer.valueOf(1), howManys.get(Common.CONNETTORE_3));

	}
	
	
	@Test
	public void regole() throws UtilsException {
		 // Regola+FIltro identifica il pool
		// Uso filtri diversi sulla stessa regola e verifico la condizionalita e il load balancing

		final String erogazione = "LoadBalanceConsegnaCondizionaleRegole";

		Map<String,List<HttpRequest>> requestsByPool = new HashMap<>();
		requestsByPool.put(Common.POOL_0, Arrays.asList(RequestBuilder.buildRequest_HeaderHttp("Pool0-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_1, Arrays.asList(RequestBuilder.buildRequest_HeaderHttp("Pool1-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_2, Arrays.asList(RequestBuilder.buildRequest_HeaderHttp("Pool2-Filtro0", erogazione)));
		
		Map<String, List<HttpResponse>> responsesByPool = makeBatchedRequests(requestsByPool,15);
		checkResponses(responsesByPool);
		
		requestsByPool = new HashMap<>();
		requestsByPool.put(Common.POOL_0, Arrays.asList(RequestBuilder.buildRequest_UrlInvocazione("Pool0-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_1, Arrays.asList(RequestBuilder.buildRequest_UrlInvocazione("Pool1-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_2, Arrays.asList(RequestBuilder.buildRequest_UrlInvocazione("Pool2-Filtro0", erogazione)));
		
		responsesByPool = makeBatchedRequests(requestsByPool,15);
		checkResponses(responsesByPool);
		
		requestsByPool = new HashMap<>();
		requestsByPool.put(Common.POOL_0, Arrays.asList(RequestBuilder.buildRequest_ParametroUrl("Pool0-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_1, Arrays.asList(RequestBuilder.buildRequest_ParametroUrl("Pool1-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_2, Arrays.asList(RequestBuilder.buildRequest_ParametroUrl("Pool2-Filtro0", erogazione)));
		
		responsesByPool = makeBatchedRequests(requestsByPool,15);
		checkResponses(responsesByPool);
		
		requestsByPool = new HashMap<>();
		requestsByPool.put(Common.POOL_0, Arrays.asList(RequestBuilder.buildRequest_Contenuto("Pool0-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_1, Arrays.asList(RequestBuilder.buildRequest_Contenuto("Pool1-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_2, Arrays.asList(RequestBuilder.buildRequest_Contenuto("Pool2-Filtro0", erogazione)));
		
		responsesByPool = makeBatchedRequests(requestsByPool,15);
		checkResponses(responsesByPool);
		
		requestsByPool = new HashMap<>();
		requestsByPool.put(Common.POOL_0, Arrays.asList(RequestBuilder.buildRequest_Template("Pool0-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_1, Arrays.asList(RequestBuilder.buildRequest_Template("Pool1-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_2, Arrays.asList(RequestBuilder.buildRequest_Template("Pool2-Filtro0", erogazione)));
		
		responsesByPool = makeBatchedRequests(requestsByPool,15);
		checkResponses(responsesByPool);
		
		requestsByPool = new HashMap<>();
		requestsByPool.put(Common.POOL_0, Arrays.asList(RequestBuilder.buildRequest_VelocityTemplate("Pool0-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_1, Arrays.asList(RequestBuilder.buildRequest_VelocityTemplate("Pool1-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_2, Arrays.asList(RequestBuilder.buildRequest_VelocityTemplate("Pool2-Filtro0", erogazione)));
		
		responsesByPool = makeBatchedRequests(requestsByPool,15);
		checkResponses(responsesByPool);
		
		requestsByPool = new HashMap<>();
		requestsByPool.put(Common.POOL_0, Arrays.asList(RequestBuilder.buildRequest_FreemarkerTemplate("Pool0-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_1, Arrays.asList(RequestBuilder.buildRequest_FreemarkerTemplate("Pool1-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_2, Arrays.asList(RequestBuilder.buildRequest_FreemarkerTemplate("Pool2-Filtro0", erogazione)));
		
		responsesByPool = makeBatchedRequests(requestsByPool,15);
		checkResponses(responsesByPool);
		
		requestsByPool = new HashMap<>();
		requestsByPool.put(Common.POOL_0, Arrays.asList(RequestBuilder.buildRequest_ForwardedFor("Pool0-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_1, Arrays.asList(RequestBuilder.buildRequest_ForwardedFor("Pool1-Filtro0", erogazione)));
		requestsByPool.put(Common.POOL_2, Arrays.asList(RequestBuilder.buildRequest_ForwardedFor("Pool2-Filtro0", erogazione)));
		
		responsesByPool = makeBatchedRequests(requestsByPool,15);
		checkResponses(responsesByPool);
		
		
		// Dalla configurazione, il filtro statico è Pool2-Filtro1, quindi viene attivato
		// il load balancing sul pool2
		HttpRequest requestIdentificazioneStatica = new HttpRequest();
		requestIdentificazioneStatica.setMethod(HttpRequestMethod.GET);
		requestIdentificazioneStatica.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-statica"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		var responses = Common.makeParallelRequests(requestIdentificazioneStatica, 15);
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
		}
		
		Set<String> connettoriPool2 = new HashSet<>(Common.connettoriPools.get(Common.POOL_2));
		connettoriPool2.remove(Common.CONNETTORE_DISABILITATO);
		Common.checkRoundRobin(responses, connettoriPool2);


		HttpRequest requestClientIp = new HttpRequest();
		requestClientIp.setMethod(HttpRequestMethod.GET);
		requestClientIp.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-client-ip"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		Set<String> connettoriPoolLocalhost = new HashSet<>(Common.connettoriPools.get(Common.POOL_LOCALHOST));
		connettoriPoolLocalhost.remove(Common.CONNETTORE_DISABILITATO);
		responses = Common.makeParallelRequests(requestClientIp, 15);
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
		}
		Common.checkRoundRobin(responses, connettoriPoolLocalhost);
	}
	
	
	/**
	 * Verifica che le richieste siano arrivate ai pool indicati e che nei pool
	 * indicati il round robin sia avvenuto correttamente.
	 *  
	 * @param responsesByPool
	 */

	static void checkResponses(Map<String, List<HttpResponse>> responsesByPool) {
		for(String pool : responsesByPool.keySet()) {
			logCore.info("Checking Responses for pool: " + pool);
			List<HttpResponse> responses = responsesByPool.get(pool);
			List<String> connettoriPool = Common.connettoriPools.get(pool);

			assertTrue(responses.size() > 0);
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
			Map<String,Integer> howManys = Common.contaConnettoriUtilizzati(responses);
			Common.printMap(howManys);
			for(var connettore : howManys.keySet()) {
				int n = howManys.get(connettore);
				assertTrue(n >= requests_per_connettore);
				if(n<min_req) min_req = n;
				if(n>max_req) max_req = n;
			}
			assertTrue(max_req-min_req <= 1);
		}
	}


	static Map<String, List<HttpResponse>> makeBatchedRequests(Map<String, List<HttpRequest>> requestsByPool) {
		int count = Common.richiesteParallele;
		return makeBatchedRequests(requestsByPool, count); 
	}
	
	static Map<String, List<HttpResponse>> makeBatchedRequests(Map<String, List<HttpRequest>> requestsByPool, int count) {
		
		Map<String, List<HttpResponse>> responsesByPool = new ConcurrentHashMap<>();
		for(var e : requestsByPool.keySet()) {
			responsesByPool.put(e, new Vector<HttpResponse>());
		}
		
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Common.sogliaRichiesteSimultanee);
				
		List<String> poolCandidates = new ArrayList<>(requestsByPool.keySet());
		int npools = poolCandidates.size();
		
		int pool_index = 0;
		int i = 0;
		
		while(count>0) {
			String pool = poolCandidates.get(pool_index%npools);
			var requests = requestsByPool.get(pool);
			
			HttpRequest request = requests.get(i % requests.size());
			executor.execute(() -> {
				responsesByPool.get(pool).add(org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeRequest(request));
			});
			
			count--;
			i++;
			if (i >= requests.size()) {
				i = 0;
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

}
