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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.HEADER_ID_CONDIZIONE;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.HEADER_ID_CONNETTORE;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.HEADER_ID_SESSIONE;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.IDSessioni;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.delayRichiesteBackground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;


/**
 *	La strategia di Load Balancing viene cambiata al cambiare dell'identificazione dell'ID sessione,
 *	in modo da coprire più path nel codice lasciando invariato il numero di test.
 * 
 *	In questi test il ConnettoreRotto è disabilitato di default, in modo da non rallentare
 *	l'esecuzione dei test.
 *
 *  NOTA: Nelle erogazioni della sessione sticky il connettore rotto è disabilitato in modo da
 *			non far durare troppo e inutilmente i test.
 *
 * 	STRUTTURA DEI TEST: tutte richieste in parallelo
 *	due gruppi di richieste con id sessione impostato,
 *  un gruppo di richieste senza id sessione impostato e che segue un round robin per i conti propri
 *	verifico che le richieste con l'id sessione impostato siano finite sullo stesso connettore.
 *	verifico la politica di bilanciamento del carico per l'altro gruppo di richieste
 * 
 *  Query Freemarker Template: ${query["govway-testsuite-id_sessione_request"]}
 *  Query Template: ${query:govway-testsuite-id_sessione_request}
 *	Query Velocity Template: 
				#if($query.containsKey("govway-testsuite-id_sessione_request"))
				$query["govway-testsuite-id_connettore_request"]
				#end
 *	Query UrlInvocazione: .+govway-testsuite-id_sessione_request=([^&]*).*
 *	Query contenuto: $.id_sessione_request
 *	Query HeaderHttp: GovWay-TestSuite-ID-Sessione
 *	Query ParametroUrl: govway-testsuite-id_sessione_request
 *	Query cookie: govway-testsuite-id_sessione_cookie
 *
 *  TODO: Aggiungere ovunque Common.checkAll200();
 *  TODO: Quando eseguo tutti i test insieme, alcuni falliscono sui check di differenza dei connettore dati i due id sessione
 * 
 * @author froggo
 *
 */

public class SessioneStickyTest extends ConfigLoader {

	@BeforeClass
	public static void resetCache() {
		try {
			ConfigLoader.resetCache();
		}catch(Throwable t) {
            throw new RuntimeException(t.getMessage(),t);
        }
	}
	
	// Costruisce richieste che non portano con se alcun ID Sessione e che quindi 
	// viene bilanciata fra i vari connettori
	static HttpRequest buildRequest_LoadBalanced(String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		return request;
	}
	
	
	static HttpRequest buildRequest_HeaderHttp(String IDSessione, String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-header-http"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.addHeader(HEADER_ID_SESSIONE, IDSessione);
		return request;
	}
	
	
	static HttpRequest buildRequest_Cookie(String IDSessione, String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-cookie"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.addHeader("Cookie", "govway-testsuite-id_sessione_cookie=" + IDSessione);
		return request;
	}
		

	static HttpRequest buildRequest_UrlInvocazione(String IDSessione, String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-url-invocazione"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_sessione_request="+IDSessione); 		 
		return request;
	}
	
	
	static HttpRequest buildRequest_ParametroUrl(String IDSessione, String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-parametro-url"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_sessione_request="+IDSessione);		
		return request;
	}
	

	static HttpRequest buildRequest_Contenuto(String IDSessione, String erogazione) {
		String body = "{ \"id_sessione_request\": \""+IDSessione+"\" }";
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-contenuto"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.setContentType("application/json");
		request.setContent(body.getBytes());
		return request;
	}
	
	
	static HttpRequest buildRequest_Template(String IDSessione, String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-template"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_sessione_request="+IDSessione);		
		return request;
	}
	
	
	static HttpRequest buildRequest_VelocityTemplate(String IDSessione, String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-velocity-template"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_sessione_request="+IDSessione);		
		return request;
	}
	
	
	static HttpRequest buildRequest_FreemarkerTemplate(String IDSessione, String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-freemarker-template"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_sessione_request="+IDSessione);		
		return request;
	}
	
	static List<HttpRequest> buildRequests_ForwardedFor(String IDSessione, List<String> forwardingHeaders, String erogazione) {
		var ret = new ArrayList<HttpRequest>();
		for(String header : forwardingHeaders) {
			HttpRequest request = new HttpRequest();
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-xforwarded-for"
					+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
			request.addHeader(header, IDSessione);
			ret.add(request);
		}
		
		return ret;
	}
	
	@Test
	public void headerHttpValoriMultipli() 
	{
		// NOTA: E' stato deciso che va bene prendere uno qualunque dei valori\header, faccio comunque il test per verificare
		//	 	che uno dei due venga preso.
		//Inviando più id sessione nella stessa richiesta, ne deve essere preso uno fra i tanti
		
		final String erogazione = "LoadBalanceSessioneStickyHeaderHttpValoriMultipli";
		
		HttpRequest request = buildRequest_HeaderHttp(IDSessioni.get(0), erogazione);
		request.addHeader(HEADER_ID_CONDIZIONE, IDSessioni.get(1));
		
		var responses = Utils.makeParallelRequests(request, 15);
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
		}
		var howManys = Common.contaConnettoriUtilizzati(responses);
		
		assertTrue(howManys.keySet().size() <= 2 && howManys.keySet().size() >= 1);
	}
	
	
	@Test
	public void XForwardedForValoriMultipli()
	{
		// Inviando più id sessione nella stessa richiesta, ne deve essere preso uno fra i tanti
		
		final String erogazione = "LoadBalanceSessioneStickyXForwardedForValoriMultipli";
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-xforwarded-for"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.addHeader("X-Forwarded-For", IDSessioni.get(0));
		request.addHeader("X-Forwarded-For", IDSessioni.get(1));
		
		var responses = Utils.makeParallelRequests(request, 15);
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
		}
		
		// Devo aver utilizzato al più due connettori
		var howManys = Common.contaConnettoriUtilizzati(responses);
		assertTrue(howManys.keySet().size() <= 2 && howManys.keySet().size() >= 1);
	}
	
	
	@Test
	public void headerHttp() {
		
		final String erogazione = "LoadBalanceSessioneStickyHeaderHttp";
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_HeaderHttp(IDSessioni.get(0), erogazione), 
				buildRequest_HeaderHttp(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		String connettore0 = responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE); 
		String connettore1 = responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertNotEquals(connettore0, connettore1);
		Common.checkAllResponsesSameConnettore(responsesByKind.get(0));
		Common.checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		Common.checkRoundRobin(balancedResponses, Common.setConnettoriAbilitati);
	}
	
	
	@Test
	public void headerHttpCookie() {
		// Faccio il test header http utilizzando come header il Cookie verificando che govway
		// non lo rimuova dagli headers

		final String erogazione = "LoadBalanceSessioneStickyHeaderHttpCookie";
		
		HttpRequest requestCookie1 = buildRequest_LoadBalanced(erogazione);
		requestCookie1.addHeader("Cookie", IDSessioni.get(0));
		
		HttpRequest requestCookie2 = buildRequest_LoadBalanced(erogazione);
		requestCookie2.addHeader("Cookie", IDSessioni.get(1));
		
		List<HttpRequest> richieste = Arrays.asList(
				requestCookie1, 
				requestCookie2,
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		String connettore0 = responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE); 
		String connettore1 = responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertNotEquals(connettore0, connettore1);
		Common.checkAllResponsesSameConnettore(responsesByKind.get(0));
		Common.checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		Common.checkRoundRobin(balancedResponses, Common.setConnettoriAbilitati);
	}

	
	@Test
	public void XForwardedFor() throws UtilsException {
		final String erogazione = "LoadBalanceSessioneStickyXForwardedFor";

		var forwardingHeaders = HttpUtilities.getClientAddressHeaders();
		int nHeaders = forwardingHeaders.size();

		// In questa lista ho, in ordine:
		//	nHeaders richieste con l'id sessione 1,  (una per tipo di header)
		//  nHeaders richieste con l'id sessione 2 	 (una per tipo di header)
		//	Una richiesta che verrà bilanciata in round robin
		
		List<HttpRequest> richieste = new ArrayList<>();
		richieste.addAll(buildRequests_ForwardedFor(IDSessioni.get(0), forwardingHeaders, erogazione));
		richieste.addAll(buildRequests_ForwardedFor(IDSessioni.get(1), forwardingHeaders, erogazione));
		richieste.add(buildRequest_LoadBalanced(erogazione));
		
		Map<Integer, List<HttpResponse>> responsesByIndex = makeRequests(richieste, richieste.size()*3);
		
		// Per ogni richiesta nella lista di richieste otttengo una lista di risposte, collasso
		// tutte le risposte relative allo stesso id sessione e riottengo la map usata negli altri test
		Map<Integer, List<HttpResponse>> responsesByKind = Map.of(
				0, responsesByIndex.get(0),
				1, responsesByIndex.get(nHeaders),
				2, responsesByIndex.get(responsesByIndex.size()-1)
			);
				
		for(int i=1;i<nHeaders;i++) {			
			responsesByKind.get(0).addAll(responsesByIndex.get(i));
			responsesByKind.get(1).addAll(responsesByIndex.get(i+nHeaders));
		}
	
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		Common.checkRoundRobin(balancedResponses, Common.setConnettoriAbilitati);
	}
	
	
	@Test
	public void contenuto() {
		final String erogazione = "LoadBalanceSessioneStickyContenuto";
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_Contenuto(IDSessioni.get(0), erogazione), 
				buildRequest_Contenuto(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		String connettore0 = responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE); 
		String connettore1 = responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertNotEquals(connettore0, connettore1);
		Common.checkAllResponsesSameConnettore(responsesByKind.get(0));
		Common.checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		Common.checkRoundRobin(balancedResponses, Common.setConnettoriAbilitati);
	}
	
	
	@Test	
	public void cookie() {
		final String erogazione = "LoadBalanceSessioneStickyCookie";
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_Cookie(IDSessioni.get(0), erogazione), 
				buildRequest_Cookie(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		String connettore0 = responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE); 
		String connettore1 = responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertNotEquals(connettore0, connettore1);
		Common.checkAllResponsesSameConnettore(responsesByKind.get(0));
		Common.checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		
		// Per testare il weighted random faccio un numero di richieste elevato, in modo tale
		//	da creare la distribuzione secondo i pesi. 
		int remaining = Common.richiesteTestRandom - balancedResponses.size();
		while (remaining > 0) {
			int nReq = Math.min(Common.richiesteParallele, remaining);
			balancedResponses.addAll(Common.makeParallelRequests(richieste.get(2), nReq));
			remaining -= nReq;
		}
		
		checkWeightedRandomStrategy(balancedResponses, Common.pesiConnettoriStandard);
	}
	
	
	@Test
	public void freemarkerTemplate() {
		final String erogazione = "LoadBalanceSessioneStickyFreemarkerTemplate";
		freemarkerTemplate_Impl(erogazione);
	}

	
	@Test	
	public void urlInvocazione() {
		final String erogazione = "LoadBalanceSessioneStickyUrlInvocazione";
		
		urlInvocazione_Impl(erogazione);
	}
	

	@Test
	public void template() {
		final String erogazione = "LoadBalanceSessioneStickyTemplate";
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_Template(IDSessioni.get(0), erogazione), 
				buildRequest_Template(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		String connettore0 = responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE); 
		String connettore1 = responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertNotEquals(connettore0, connettore1); 	// TODO: Fallisce qui
		Common.checkAllResponsesSameConnettore(responsesByKind.get(0));
		Common.checkAllResponsesSameConnettore(responsesByKind.get(1));
		
	
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		// Per testare il weighted round robin mi assicuro di aver fatto Smax+1 richieste
		int remaining = (getPesoTotale(Common.pesiConnettoriStandard)+1) - balancedResponses.size();
		balancedResponses.addAll(Common.makeParallelRequests(richieste.get(2), remaining));
		
		checkWeightedRoundRobin(balancedResponses, Common.pesiConnettoriStandard);	
	}
	
	
	
	@Test	
	public void parametroUrl() {
		final String erogazione = "LoadBalanceSessioneStickyParametroUrl";
		
		parametroUrl_Impl(erogazione);
	}
	
	
	public static void velocityTemplate_Impl(String erogazione)  throws InterruptedException, ExecutionException {
		// Qui viene usata la strategia di bilanciamento LeastConnections,
		// Lancio le due chiamate bloccanti, devono durare per tutto il test
		HttpRequest requestBlockingIdSessione1 = buildRequest_VelocityTemplate(IDSessioni.get(0), erogazione);
		requestBlockingIdSessione1
				.setUrl(requestBlockingIdSessione1.getUrl() + "&sleep=" + Common.durataBloccanteLunga);

		HttpRequest requestBlockingIdSessione2 = buildRequest_VelocityTemplate(IDSessioni.get(1), erogazione);
		requestBlockingIdSessione2
				.setUrl(requestBlockingIdSessione2.getUrl() + "&sleep=" + Common.durataBloccanteLunga);

		var futureResp1 = Utils.makeBackgroundRequest(requestBlockingIdSessione1);

		org.openspcoop2.utils.Utilities.sleep(delayRichiesteBackground);

		var futureResp2 = Utils.makeBackgroundRequest(requestBlockingIdSessione2);

		org.openspcoop2.utils.Utilities.sleep(delayRichiesteBackground);

		assertFalse(futureResp1.isDone() || futureResp2.isDone());

		// Faccio una serie di richieste con id sessione impostato e verifico che vadano
		// tutte nello stesso connettore
		List<HttpRequest> richieste = Arrays
				.asList(buildRequest_VelocityTemplate(Common.IDSessioni.get(0), erogazione),
						buildRequest_VelocityTemplate(Common.IDSessioni.get(1), erogazione));

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, 5);
		String connettore0 = responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE); 
		String connettore1 = responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertNotEquals(connettore0, connettore1);
		Common.checkAllResponsesSameConnettore(responsesByKind.get(0));
		Common.checkAllResponsesSameConnettore(responsesByKind.get(1));

		String connettoreSessione0 = responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		String connettoreSessione1 = responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);

		assertNotEquals(connettoreSessione0, connettoreSessione1);

		getLoggerCore().info("Connettore sessione 0: " + connettoreSessione0);
		getLoggerCore().info("Connettore sessione 1: " + connettoreSessione1);

		assertFalse(futureResp1.isDone() || futureResp2.isDone());

		// Faccio 2 richieste senza idSessione impostato e verifico che vadano a finire
		// negli altri 2 connettori

		HttpRequest requestBalanced = buildRequest_LoadBalanced(erogazione);
		requestBalanced.setUrl(requestBalanced.getUrl() + "&sleep=" + Common.durataBloccante);

		var futureBlockingResponses = Utils.makeBackgroundRequests(requestBalanced, 2, delayRichiesteBackground);

		Vector<HttpResponse> balancedResponses = Utils.awaitResponses(futureBlockingResponses);

		var howManys = Common.contaConnettoriUtilizzati(balancedResponses);
		Common.printMap(howManys);
		assertEquals(2, howManys.size());
		assertFalse(howManys.keySet().contains(connettoreSessione0));
		assertFalse(howManys.keySet().contains(connettoreSessione1));
		for (var connettore : howManys.keySet()) {
			assertEquals(Integer.valueOf(1), howManys.get(connettore));
		}

		assertFalse(futureResp1.isDone() || futureResp2.isDone());

		// Mi assicuro che le richieste con id sessione impostato siano andate sui
		// connettori
		// scelti durante le prime due richieste.

		var responseIdSessione0 = futureResp1.get();
		var responseIdSessione1 = futureResp2.get();

		assertEquals(connettoreSessione0, responseIdSessione0.getHeaderFirstValue(HEADER_ID_CONNETTORE));
		assertEquals(connettoreSessione1, responseIdSessione1.getHeaderFirstValue(HEADER_ID_CONNETTORE));

	}
	
		
	@Test
	public void velocityTemplate() throws InterruptedException, ExecutionException {
		final String erogazione = "LoadBalanceSessioneStickyVelocityTemplate";
		velocityTemplate_Impl(erogazione);
	}
	
	
	
	@Test
	public void clientIp() {
		final String erogazione = "LoadBalanceSessioneStickyClientIp";
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		var responses = Common.makeParallelRequests(request, Common.richiesteParallele);
		String connettore = responses.get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		for (var resp : responses) {
			assertEquals(200, resp.getResultHTTPOperation());
			assertEquals(connettore, resp.getHeaderFirstValue(HEADER_ID_CONNETTORE));			
		}
		
	}
	
	@Test
	public void healthCheck() {
		// Forziamo una richiesta con id sessione ad andare verso il connettore rotto.
		// Ci assicuriamo che nelle prossime richieste con lo stesso id sessione, queste
		// vengano instradate ad un altro connettore.
		
		final String erogazione = "LoadBalanceSessioneStickyHealthCheckHeaderHttp";
		var idSessioni = Arrays.asList("a", "b", "c", "d", "e");
		List<HttpRequest> richiesteSonda = idSessioni.stream()
				.map( idSessione -> buildRequest_HeaderHttp(idSessione, erogazione))
				.collect(Collectors.toList());
		
		String idSessioneConnettoreRotto = null;
		
		for(var request : richiesteSonda) {
			HttpResponse response = Utils.makeRequest(request);
			if (response.getResultHTTPOperation() == 200) {
				assertNotEquals(null,response.getHeaderFirstValue(HEADER_ID_CONNETTORE));
			} else if (response.getResultHTTPOperation() == 503) {
				assertEquals(null, idSessioneConnettoreRotto);	// Un solo connettore rotto
				idSessioneConnettoreRotto = request.getHeaderFirstValue(HEADER_ID_SESSIONE);
			} else {
				assertFalse(true);	// Status code non riconosciuto
			}
		}
		assertNotEquals(null,idSessioneConnettoreRotto);
		
		// Questa richiesta adesso deve essere instradata ad un connettore buono
		HttpRequest requestConnettoreRotto = buildRequest_HeaderHttp(idSessioneConnettoreRotto, erogazione);
		
		List<HttpRequest> richieste = Arrays.asList(
				requestConnettoreRotto,
				buildRequest_HeaderHttp(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		String connettore0 = responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE); 
		String connettore1 = responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertNotEquals(connettore0, connettore1);
		Common.checkAllResponsesSameConnettore(responsesByKind.get(0));
		Common.checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		Common.checkRoundRobin(balancedResponses, Common.setConnettoriAbilitati);
		
		
		// Nuova batteria dove il connettore rotto non deve essere preso in considerazione
		
		idSessioni = Arrays.asList("aNewBefore", "bNewBefore", "cNewBefore", "dNewBefore", "eNewBefore");
		richiesteSonda = idSessioni.stream()
				.map( idSessione -> buildRequest_HeaderHttp(idSessione, erogazione))
				.collect(Collectors.toList());
		
		idSessioneConnettoreRotto = null;
		for(var request : richiesteSonda) {
			HttpResponse response = Utils.makeRequest(request);
			if (response.getResultHTTPOperation() == 200) {
				assertNotEquals(null,response.getHeaderFirstValue(HEADER_ID_CONNETTORE));
			} else {
				assertEquals(null, idSessioneConnettoreRotto);	// Un solo connettore rotto
				idSessioneConnettoreRotto = request.getHeaderFirstValue(HEADER_ID_SESSIONE);
			}
		}
		assertEquals(null,idSessioneConnettoreRotto);
		
		// Attendo che venga reinserito nel pool e ripeto la parte iniziale del test
		// per instradare una richiesta nuovamente sul connettore rotto
		org.openspcoop2.utils.Utilities.sleep(Common.intervalloEsclusione + 100);

		idSessioni = Arrays.asList("aNewAfter", "bNewAfter", "cNewAfter", "dNewAfter", "eNewAfter"); // uso nuovi id di sessione per verificare che adesso venga ripreso il connettore rotto
		richiesteSonda = idSessioni.stream()
				.map( idSessione -> buildRequest_HeaderHttp(idSessione, erogazione))
				.collect(Collectors.toList());
		
		idSessioneConnettoreRotto = null;
		for(var request : richiesteSonda) {
			HttpResponse response = Utils.makeRequest(request);
			if (response.getResultHTTPOperation() == 200) {
				assertNotEquals(null,response.getHeaderFirstValue(HEADER_ID_CONNETTORE));
			} else {
				assertEquals(null, idSessioneConnettoreRotto);	// Un solo connettore rotto
				idSessioneConnettoreRotto = request.getHeaderFirstValue(HEADER_ID_SESSIONE);
			}
		}
		assertNotEquals(null,idSessioneConnettoreRotto);
		
	}
	
	
	@Test
	public void healthCheckConsegnaCondizionale() {

		//	 PoolRotto: Connettore0, Connettore1, Connettore2, ConnettoreRotto
		// Uguale al test di sopra ma tutto filtrato per Pool0-Filtro0
		final String erogazione = "LoadBalanceSessioneStickyHealthCheckConsegnaCondizionale";
		
		List<String> connettoriPoolRotto = Common.connettoriPools.get(Common.POOL_ROTTO);
		
		var idSessioni = Arrays.asList("a", "b", "c", "d");
		List<HttpRequest> richiesteSonda = idSessioni.stream()
				.map( idSessione -> {
					var request = buildRequest_HeaderHttp(idSessione, erogazione);
					request.addHeader(Common.HEADER_ID_CONDIZIONE, "PoolRotto-Filtro0");
					return request;
				})
				.collect(Collectors.toList());
		
		String idSessioneConnettoreRotto = null;
		
		for(var request : richiesteSonda) {
			HttpResponse response = Utils.makeRequest(request);
			if (response.getResultHTTPOperation() == 200) {
				
				String connettore = response.getHeaderFirstValue(HEADER_ID_CONNETTORE);
				assertNotEquals(null, connettore);
				assertTrue(connettoriPoolRotto.contains(connettore));
				
			} else if (response.getResultHTTPOperation() == 503) {
				
				assertEquals(null, idSessioneConnettoreRotto);	// Un solo connettore rotto
				idSessioneConnettoreRotto = request.getHeaderFirstValue(HEADER_ID_SESSIONE);
				
			} else {
				
				assertFalse(true);	// Status code non riconosciuto
				
			}
		}
		assertNotEquals(null,idSessioneConnettoreRotto);
		
		// Questa richiesta adesso deve essere instradata ad un connettore buono
		HttpRequest requestConnettoreRotto = buildRequest_HeaderHttp(idSessioneConnettoreRotto, erogazione);
		requestConnettoreRotto.addHeader(Common.HEADER_ID_CONDIZIONE, "PoolRotto-Filtro0");
		
		HttpRequest requestIdSessione =	buildRequest_HeaderHttp(IDSessioni.get(1), erogazione);
		requestIdSessione.addHeader(Common.HEADER_ID_CONDIZIONE, "PoolRotto-Filtro0");
		
		HttpRequest requestBalanced = buildRequest_LoadBalanced(erogazione);
		requestBalanced.addHeader(Common.HEADER_ID_CONDIZIONE, "PoolRotto-Filtro0");

		List<HttpRequest> richieste = Arrays.asList(
				requestConnettoreRotto,
				requestIdSessione,
				requestBalanced
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		String connettore0 = responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE); 
		String connettore1 = responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertNotEquals(connettore0, connettore1);
		Common.checkAllResponsesSameConnettore(responsesByKind.get(0));
		Common.checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		Set<String> connettoriDaRaggiungere = new HashSet<>( Arrays.asList(
				Common.CONNETTORE_0, 
				Common.CONNETTORE_1, 
				Common.CONNETTORE_2));
		Common.checkRoundRobin(balancedResponses, connettoriDaRaggiungere);
		
		// Nuova batteria dove il connettore rotto non deve essere preso in considerazione
		
		idSessioni = Arrays.asList("aNewBefore", "bNewBefore", "cNewBefore", "dNewBefore", "eNewBefore");
		richiesteSonda = idSessioni.stream()
				.map( idSessione -> {
					var request = buildRequest_HeaderHttp(idSessione, erogazione);
					request.addHeader(Common.HEADER_ID_CONDIZIONE, "PoolRotto-Filtro0");
					return request;
				})
				.collect(Collectors.toList());
		
		idSessioneConnettoreRotto = null;
		for(var request : richiesteSonda) {
			HttpResponse response = Utils.makeRequest(request);
			if (response.getResultHTTPOperation() == 200) {
				
				String connettore = response.getHeaderFirstValue(HEADER_ID_CONNETTORE);
				assertNotEquals(null, connettore);
				assertTrue(connettoriPoolRotto.contains(connettore));
				
			} else if (response.getResultHTTPOperation() == 503) {
				
				assertEquals(null, idSessioneConnettoreRotto);	// Un solo connettore rotto
				idSessioneConnettoreRotto = request.getHeaderFirstValue(HEADER_ID_SESSIONE);
				
			} else {
				
				assertFalse(true);	// Status code non riconosciuto
				
			}
		}
		assertEquals(null,idSessioneConnettoreRotto);
		
		// Attendo che venga reinserito nel pool e ripeto la parte iniziale del test
		// per instradare una richiesta nuovamente sul connettore rotto
		org.openspcoop2.utils.Utilities.sleep(Common.intervalloEsclusione + 100);

		idSessioni = Arrays.asList("aNewAfter", "bNewAfter", "cNewAfter", "dNewAfter", "eNewAfter"); // uso nuovi id di sessione per verificare che adesso venga ripreso il connettore rotto
		richiesteSonda = idSessioni.stream()
				.map( idSessione -> {
					var request = buildRequest_HeaderHttp(idSessione, erogazione);
					request.addHeader(Common.HEADER_ID_CONDIZIONE, "PoolRotto-Filtro0");
					return request;
				})
				.collect(Collectors.toList());
		
		idSessioneConnettoreRotto = null;
		for(var request : richiesteSonda) {
			HttpResponse response = Utils.makeRequest(request);
			if (response.getResultHTTPOperation() == 200) {
				
				String connettore = response.getHeaderFirstValue(HEADER_ID_CONNETTORE);
				assertNotEquals(null, connettore);
				assertTrue(connettoriPoolRotto.contains(connettore));
				
			} else if (response.getResultHTTPOperation() == 503) {
				
				assertEquals(null, idSessioneConnettoreRotto);	// Un solo connettore rotto
				idSessioneConnettoreRotto = request.getHeaderFirstValue(HEADER_ID_SESSIONE);
				
			} else {
				
				assertFalse(true);	// Status code non riconosciuto
				
			}
		}
		assertNotEquals(null,idSessioneConnettoreRotto);

	}
	
	
	@Test
	public void consegnaCondizionale() {
		final String erogazione = "LoadBalanceSessioneStickyConsegnaCondizionaleHeaderHttp";
		
		// Simile al test consegnaCondizionale ma tutte le richieste lavorano
		// con il filtro.
		String pool1 = Common.POOL_0;
		String pool2 = Common.POOL_1;
		String pool3 = Common.POOL_2;
		
		HttpRequest requestPool1IdSessione = buildRequest_HeaderHttp(IDSessioni.get(0), erogazione);
		requestPool1IdSessione.addHeader(HEADER_ID_CONDIZIONE, Common.filtriPools.get(pool1).get(0));
		
		HttpRequest requestPool2IdSessione = buildRequest_HeaderHttp(IDSessioni.get(1), erogazione);
		requestPool2IdSessione.addHeader(HEADER_ID_CONDIZIONE, Common.filtriPools.get(pool2).get(0));
		
		HttpRequest requestPool1NoIdSessione = Common
				.buildRequest_HeaderHttp(Common.filtriPools.get(pool1).get(1), erogazione);
		
		HttpRequest requestPool2NoIdSessione = Common
				.buildRequest_HeaderHttp(Common.filtriPools.get(pool2).get(1), erogazione);
		
		HttpRequest requestPool3NoIdSessione = Common.buildRequest_HeaderHttp(
				Common.filtriPools.get(pool3).get(0), erogazione);
		
		HttpRequest requestNoPoolNoIdSessione = buildRequest_LoadBalanced(erogazione);

		List<HttpRequest> richieste = Arrays.asList(
				requestPool1IdSessione, 				// 0 Vanno tutte sullo stesso connettore dell pool1 senza attivare il load balancing sul pool1
				requestPool2IdSessione,					// 1 Vanno tutte sullo stesso connettore del pool2 senza attivare il load balancing sul pool2
				requestPool1NoIdSessione,				// 2 Vengono bilanciate in round robin sui connettori del pool1
				requestPool2NoIdSessione,				// 3 Vengono bilanciate in round robin sui connettori del pool2
				requestPool3NoIdSessione,				// 4 Vengono bilanciate in round robin sui connettori del pool3
				requestNoPoolNoIdSessione				// 5 Vengono bilanciate in round robin sul pool di tutti i connettori
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, richieste.size()*5);
		
		String connettore0 = responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE); 
		String connettore1 = responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertNotEquals(connettore0, connettore1);
		Common.checkAllResponsesSameConnettore(responsesByKind.get(0));
		Common.checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// - 2 Richieste che vanno sul pool1 senza id sessione e vengono bilanciate
		Set<String> connettoriBilanciati = new HashSet<>(Common.connettoriPools.get(pool1));
		connettoriBilanciati.remove(Common.CONNETTORE_DISABILITATO);		
		Common.checkRoundRobin(responsesByKind.get(2), connettoriBilanciati);
		
		// 3 - Richieste che vanno sul pool2 senza id sessione e vengono bilanciate
		connettoriBilanciati = new HashSet<>(Common.connettoriPools.get(pool2));
		connettoriBilanciati.remove(Common.CONNETTORE_DISABILITATO);		
		Common.checkRoundRobin(responsesByKind.get(3), connettoriBilanciati);

		// 4 - Vengono bilanciate in round robin sul connettore del pool3
		connettoriBilanciati = new HashSet<>(Common.connettoriPools.get(pool3));
		connettoriBilanciati.remove(Common.CONNETTORE_DISABILITATO);
		Common.checkRoundRobin(responsesByKind.get(4), connettoriBilanciati);	
		
		// 5 - Vengono bilanciate in round robin sul pool di tutti i connettori
		Common.checkRoundRobin(responsesByKind.get(5), Common.setConnettoriAbilitati);
	}
	
	
	static void urlInvocazione_Impl(String erogazione) {
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_UrlInvocazione(IDSessioni.get(0), erogazione), 
				buildRequest_UrlInvocazione(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		String connettore0 = responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE); 
		String connettore1 = responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertNotEquals(connettore0, connettore1);
		Common.checkAllResponsesSameConnettore(responsesByKind.get(0));
		Common.checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		// Per testare il weighted round robin mi assicuro di aver fatto Smax+1 richieste
		int remaining = (getPesoTotale(Common.pesiConnettoriStandard)+1) - balancedResponses.size();
		balancedResponses.addAll(Common.makeParallelRequests(richieste.get(2), remaining));
		
		checkWeightedRoundRobin(balancedResponses, Common.pesiConnettoriStandard);
	}
	
	
	static void parametroUrl_Impl(String erogazione) {
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_ParametroUrl(IDSessioni.get(0), erogazione), 
				buildRequest_ParametroUrl(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		String connettore0 = responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE); 
		String connettore1 = responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertNotEquals(connettore0, connettore1);
		Common.checkAllResponsesSameConnettore(responsesByKind.get(0));
		Common.checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		// Per testare il random faccio un numero di richieste elevato, in modo tale
		//	da creare la distribuzione
		int remaining = Common.richiesteTestRandom - balancedResponses.size();
		while (remaining > 0) {
			int nReq = Math.min(Common.richiesteParallele, remaining);
			balancedResponses.addAll(Common.makeParallelRequests(richieste.get(2), nReq));
			remaining -= nReq;
		}
		
		checkRandomStrategy(balancedResponses, Common.setConnettoriAbilitati);
	}
	

	static void freemarkerTemplate_Impl(String erogazione) {
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_FreemarkerTemplate(IDSessioni.get(0), erogazione), 
				buildRequest_FreemarkerTemplate(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore
		String connettore0 = responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE); 
		String connettore1 = responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertNotEquals(connettore0, connettore1);
		Common.checkAllResponsesSameConnettore(responsesByKind.get(0));
		Common.checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto
		// prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a
		// tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));

		// Per testare il weighted random faccio un numero di richieste elevato, in modo
		// tale
		// da creare la distribuzione secondo i pesi
		int remaining = Common.richiesteTestRandom - balancedResponses.size();
		while (remaining > 0) {
			int nReq = Math.min(Common.richiesteParallele, remaining);
			balancedResponses.addAll(Common.makeParallelRequests(richieste.get(2), nReq));
			remaining -= nReq;
		}

		checkWeightedRandomStrategy(balancedResponses, Common.pesiConnettoriStandard);
	}
	
	
	static int getPesoTotale(Map<String,Integer> pesiConnettori) {
		return pesiConnettori.values().stream().reduce(0, Integer::sum);
	}
	
	
	static void checkWeightedRoundRobin(List<HttpResponse> responses, Map<String,Integer> pesiConnettori) {
		// Può essere interleaved o classic, siccome non ho specifiche testo
		// il risultato finale.
		//
		// Data Smax = la somma totale dei pesi, se faccio n Smax+1 richieste
		// devo aver raggiunto tutti i connettori ciascuno in maniera uguale al suo peso
		// tranne che per uno (non specificato), che ha gestito una richiesta in più
		int Smax = getPesoTotale(pesiConnettori);		
		assertEquals(responses.size(),Smax+1);
		
		var howManys = Common.contaConnettoriUtilizzati(responses);
		Common.printMap(howManys);
		
		for (var connettore : howManys.keySet()) {
			// Ogni connettore raggiunto deve essere nel set dei connettori
			assertTrue(pesiConnettori.keySet().contains(connettore));
		}
		
		int richiestaInPiu = 0;
		for (var connettore : pesiConnettori.keySet()) {
			// Tutti i connettori devono essere stati raggiunti
			assertTrue(howManys.containsKey(connettore));
			Integer count = howManys.get(connettore);
			Integer expectedCount = pesiConnettori.get(connettore);
			
			assertTrue(count >= expectedCount);
			assertTrue(count <= expectedCount+1);
			
			if(count == expectedCount+1) {
				richiestaInPiu++;
			}
		}
		
		assertEquals(1, richiestaInPiu);
	}
	
	
	static void checkWeightedRandomStrategy(List<HttpResponse> responses, Map<String,Integer> pesiConnettori) {
		/*
		 * Le richieste vengono distribuite casualmente considerando il peso di ciascun connettore.
		 * Controllo che su un carico abbastanza alto di richieste, le relazioni d'ordine tra il numero 
		 * di richieste gestite segua i pesi.
		 * 
		 */		
		var howManys = Common.contaConnettoriUtilizzati(responses);
		Common.printMap(howManys);
		
		for (var connettore : howManys.keySet()) {
			// Ogni connettore raggiunto deve essere nel set dei connettori
			assertTrue(pesiConnettori.keySet().contains(connettore));
		}
		
		for (var connettore : pesiConnettori.keySet()) {
			// Tutti i connettori devono essere stati raggiunti
			assertTrue(howManys.containsKey(connettore));
			for(var connettore2 : pesiConnettori.keySet()) {
				// Le relazioni d'ordine con gli altri connettori devono essere mantenute
				if (pesiConnettori.get(connettore) < pesiConnettori.get(connettore2)) {
					assertTrue(howManys.get(connettore) <= howManys.get(connettore2));
				}
			}
		}
	}

	
	static void checkRandomStrategy(List<HttpResponse> responses, Set<String> connettori) {
		// Sotto un numero di richieste alto, controllo che
		// nessun connettore riceva molte più richieste degli altri.
		int nRequests = responses.size();
		int n = (nRequests / connettori.size())*2;
		
		var howManys = Common.contaConnettoriUtilizzati(responses);
		
		for (var connettore : howManys.keySet()) {
			// Ogni connettore raggiunto deve essere nel set dei connettori
			assertTrue(connettori.contains(connettore));
		}
		
		for (var connettore : connettori) {
			// Il connettore deve essere stato raggiunto
			assertTrue(howManys.containsKey(connettore));
			// Mi aspetto un random uniforme, nessun connettore deve avere più di n richieste
			// rispetto ad un altro connettore
			for (var connettore2 : howManys.keySet()) {
				assertTrue(Math.abs(howManys.get(connettore2) - howManys.get(connettore)) <= n ); 
			}
		}

	}

	
	static Map<Integer, List<HttpResponse>> makeRequests(List<HttpRequest> richieste, int nRequests) {
		int nthreads = Integer.valueOf(System.getProperty("soglia_richieste_simultanee"));

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nthreads);
		int i = 0;
		Map<Integer,List<HttpResponse>> responsesByKind = new ConcurrentHashMap<>();
		while(i<nRequests) {
//			org.openspcoop2.utils.Utilities.sleep(Common.delayRichiesteBackground);
			
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

}
