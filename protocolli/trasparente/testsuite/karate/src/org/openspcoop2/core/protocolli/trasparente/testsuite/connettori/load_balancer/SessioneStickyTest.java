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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.CONNETTORE_0;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.CONNETTORE_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.CONNETTORE_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.CONNETTORE_3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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

//Query Freemarker Template: ${query["govway-testsuite-id_sessione_request"]}
//Query Template: ${query:govway-testsuite-id_sessione_request}
//Query Velocity Template: $query["govway-testsuite-id_sessione_request"]
//Query UrlInvocazione: .+govway-testsuite-id_sessione_request=([^&]*).*
//Query contenuto: $.id_sessione_request
//Query HeaderHttp: GovWay-TestSuite-ID-Sessione
//Query ParametroUrl: govway-testsuite-id_sessione_request
//Query cookie: govway-testsuite-id_sessione_cookie

/**
 * La strategia di Load Balancing viene cambiata al cambiare dell'identificazione dell'ID sessione,
 * in modo da coprire più path nel codice lasciando invariato il numero di test.
 * 
 * In questi test il ConnettoreRotto è disabilitato di default, in modo da non rallentare
 * l'esecuzione dei test.
 * 
 * @author froggo
 *
 */
public class SessioneStickyTest extends ConfigLoader {
	
	// TODO: Fai un unico test per la Max Age
	// TODO: Anche qui test con valori multipli per lo stesso header,
	//			conflitti fra gli headers appartenenti alla classe XForwardedFor
	// 			e test con valori mancanti per i null pointers. NOTA: Andrea e tito hanno deciso
	//			che va bene prendere uno qualunque dei valori\header, faccio comunque il test per verificare
	//			che uno dei due venga preso

	// NOTA: Nelle erogazioni della sessione sticky il connettore rotto è disabilitato in modo da
	//			non far durare troppo e inutilmente i test.
	//
	// TODO: Aggiungere in ogni caso nei check, la capacità di controllare anche le richieste finite ad
	//	un connettore rotto.
	//
	// TODO: Cookie, prova a fare il test header http utilizzando come header il Cookie o i vari forwarded for.
	// 	Struttura dei test: tutte richieste in parallelo:
	//	due gruppi di richieste con id sessione impostato,
	//  un gruppo di richieste senza id sessione impostato e che segue un round robin per i conti propri
	//	verifico che le richieste con l'id sessione impostato siano finite sullo stesso connettore.
	//	verifico la politica di bilanciamento del carico per l'altro gruppo di richieste
	static String HEADER_ID_SESSIONE = "GovWay-TestSuite-ID-Sessione";
	
	static int richiesteParallele = Integer.valueOf(System.getProperty("soglia_richieste_simultanee"));
	
	static int richiesteTestRandom = 50; // TODOInteger.valueOf("connettori.load_balancer.numero_richieste_random");
	
	
	static List<String> IDSessioni = Arrays.asList(
			UUID.randomUUID().toString(),
			UUID.randomUUID().toString());
	
	
	static Map<String,Integer> pesiConnettoriStandard = Map.of(
			CONNETTORE_0, 1,
			CONNETTORE_1, 1,
			CONNETTORE_2, 2,
			CONNETTORE_3, 3			
		);
	
	
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
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		checkRoundRobin(balancedResponses, Common.setConnettoriAbilitati);
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
		
		checkRoundRobin(balancedResponses, Common.setConnettoriAbilitati);
	}
	
	
	@Test
	public void contenuto() {
		final String erogazione = "LoadBalanceSessioneStickyContenuto";
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_Contenuto(IDSessioni.get(0), erogazione), 
				buildRequest_Contenuto(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		checkAllResponsesSameConnettore(responsesByKind.get(0));
		checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		checkRoundRobin(balancedResponses, Common.setConnettoriAbilitati);
	}
	
	
	@Test	
	public void cookie() {
		final String erogazione = "LoadBalanceSessioneStickyCookie";
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_Cookie(IDSessioni.get(0), erogazione), 
				buildRequest_Cookie(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		checkAllResponsesSameConnettore(responsesByKind.get(0));
		checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		
		// Per testare il weighted random faccio un numero di richieste elevato, in modo tale
		//	da creare la distribuzione secondo i pesi. 
		int remaining = richiesteTestRandom - balancedResponses.size();
		while (remaining > 0) {
			int nReq = Math.min(richiesteParallele, remaining);
			balancedResponses.addAll(Utils.makeParallelRequests(richieste.get(2), nReq));
			remaining -= nReq;
		}
		
		checkWeightedRandomStrategy(balancedResponses, pesiConnettoriStandard);
	}
	
	
	@Test
	public void freemarkerTemplate() {
		
		final String erogazione = "LoadBalanceSessioneStickyFreemarkerTemplate";
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_FreemarkerTemplate(IDSessioni.get(0), erogazione), 
				buildRequest_FreemarkerTemplate(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		checkAllResponsesSameConnettore(responsesByKind.get(0));
		checkAllResponsesSameConnettore(responsesByKind.get(1));
		
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
		int remaining = richiesteTestRandom - balancedResponses.size();
		while (remaining > 0) {
			int nReq = Math.min(richiesteParallele, remaining);
			balancedResponses.addAll(Utils.makeParallelRequests(richieste.get(2), nReq));
			remaining -= nReq;
		}

		checkWeightedRandomStrategy(balancedResponses, pesiConnettoriStandard);
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
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		// Per testare il weighted round robin mi assicuro di aver fatto Smax+1 richieste
		int remaining = (getPesoTotale(pesiConnettoriStandard)+1) - balancedResponses.size();
		balancedResponses.addAll(Utils.makeParallelRequests(richieste.get(2), remaining));
		
		checkWeightedRoundRobin(balancedResponses, pesiConnettoriStandard);
	}
	
	
	
	@Test
	public void template() {
		final String erogazione = "LoadBalanceSessioneStickyTemplate";
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_Template(IDSessioni.get(0), erogazione), 
				buildRequest_Template(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		checkAllResponsesSameConnettore(responsesByKind.get(0));
		checkAllResponsesSameConnettore(responsesByKind.get(1));
		
	
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		// Per testare il weighted round robin mi assicuro di aver fatto Smax+1 richieste
		int remaining = (getPesoTotale(pesiConnettoriStandard)+1) - balancedResponses.size();
		balancedResponses.addAll(Utils.makeParallelRequests(richieste.get(2), remaining));
		
		checkWeightedRoundRobin(balancedResponses, pesiConnettoriStandard);	
	}
	
	
	
	@Test	
	public void parametroUrl() {
		final String erogazione = "LoadBalanceSessioneStickyParametroUrl";
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_ParametroUrl(IDSessioni.get(0), erogazione), 
				buildRequest_ParametroUrl(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		checkAllResponsesSameConnettore(responsesByKind.get(0));
		checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		// Per testare il random faccio un numero di richieste elevato, in modo tale
		//	da creare la distribuzione
		int remaining = richiesteTestRandom - balancedResponses.size();
		while (remaining > 0) {
			int nReq = Math.min(richiesteParallele, remaining);
			balancedResponses.addAll(Utils.makeParallelRequests(richieste.get(2), nReq));
			remaining -= nReq;
		}
		
		checkRandomStrategy(balancedResponses, pesiConnettoriStandard);
	}
	
		
	@Test
	public void velocityTemplate() {
		// TODO:
		// Questo test lavora con la least connections quindi segue uno schema leggermente diverso:
		// Prima teniamo bloccati con una richiesta lunga due connettori usando l'id sessione.
		// Durante il mantenimento di quete richieste, fare il test che sto per fare sotto.
		// Delle 13 restanti richieste, 4 e 4 le continuo a mandare con l'id sessione impostato, le altre
		// 4 mi aspetto non raggiungano mai questi due connettori iniziali.
		// Vengono smistati infatti tra i restanti tre (All'uopo fare un quinto connettore abilitato. (Il Connettore4)
		//
		final String erogazione = "LoadBalanceSessioneStickyVelocityTemplate";
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_VelocityTemplate(IDSessioni.get(0), erogazione), 
				buildRequest_VelocityTemplate(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		// Siccome testo il least connections come un round robin, aggiungo una sleep alle
		// richieste in modo che avvenga esattamente ciò
		for(var r : richieste) {
			String oldUrl = r.getUrl();
			r.setUrl(oldUrl+"&sleep="+Common.durataBloccante);
		}

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		checkAllResponsesSameConnettore(responsesByKind.get(0));
		checkAllResponsesSameConnettore(responsesByKind.get(1));

		// 
		//checkLeastConnectionsAsRoundRobin(null);
	}
	
	
	
	@Test
	public void clientIp() {
		final String erogazione = "LoadBalanceSessioneStickyClientIp";
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		var responses = Common.makeParallelRequests(request, richiesteParallele);
		String connettore = responses.get(0).getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
		for (var resp : responses) {
			assertEquals(200, resp.getResultHTTPOperation());
			assertEquals(connettore, resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));			
		}
		
	}
	

	@Test
	public void maxAge() {
		// TODO
	}

	
	@Test
	public void healthCheck() {
		// TODO
	}
	
	
	@Test
	public void consegnaCondizionale() {
		// TODO test funzionalità semplice
	}
	
	
	@Test
	public void consegnaCondizionaleConflitti() {
		// TODO: La consegna condizionale dice di andare su un pool di connettori
		//		ma l'id sessione dice di andare su un altro.
		
		// TODO: La consegna condizionale identifica un set di connettori ma la sessione sticky
		//		butta la richiesta su un connettore fuori da questo pool
	}
	

	private int getPesoTotale(Map<String,Integer> pesiConnettori) {
		return pesiConnettori.values().stream().reduce(0, Integer::sum);
	}
	
	
	private void checkRoundRobin(List<HttpResponse> responses, Set<String> connettori) {
		// A ciascun connettore devono essere arrivate almeno nRequests/nConnettori richieste
		// (nRequests % nConnettori) connettori hanno una richiesta in più
		// Tutti i connettori indicati nel secondo parametro devono essere stati raggiunti.		
		int nRequests = responses.size();
		int nConnettori = connettori.size();
		int minRequests = nRequests / nConnettori;
		int restoRequests = nRequests % nConnettori;
		int nDiPiu = 0;		// Quanti connettori sono stati raggiunti da al più 
		
		var howManys = Common.contaConnettoriUtilizzati(responses);
		Common.printMap(howManys);
		
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
	
	
	private void checkWeightedRoundRobin(List<HttpResponse> responses, Map<String,Integer> pesiConnettori) {
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
	
	
	private void checkWeightedRandomStrategy(List<HttpResponse> responses, Map<String,Integer> pesiConnettori) {
		/*
		 * Le richieste vengono distribuite casualmente considerando il peso di ciascun connettore.
		 * Controllo che su un carico abbastanza alto di richieste, le relazioni d'ordine tra il numero 
		 * di richieste gestite segua i pesi.
		 * 
		 */		
		var howManys = Common.contaConnettoriUtilizzati(responses);
		Common.printMap(howManys);
		for (var connettore : pesiConnettori.keySet()) {
			// Tutti i connettori devono essere stati raggiunti
			assertTrue(howManys.containsKey(connettore));
			for(var connettore2 : pesiConnettori.keySet()) {
				// Le relazioni d'ordine con gli altri connettori devono essere mantenute
				if (pesiConnettori.get(connettore) < pesiConnettori.get(connettore2)) {
					assertTrue(howManys.get(connettore) < howManys.get(connettore2));
				}
			}
		}
	}

	
	private void checkRandomStrategy(List<HttpResponse> responses, Map<String,Integer> pesiConnettori) {
		// Sotto un numero di richieste alto, controllo che
		// nessun connettore riceva molte più richieste degli altri.
		int nRequests = responses.size();
		int n = nRequests / pesiConnettori.size();
		
		var howManys = Common.contaConnettoriUtilizzati(responses);
		
		for (var connettore : pesiConnettori.keySet()) {
			// Il connettore deve essere stato raggiunto
			assertTrue(howManys.containsKey(connettore));
			// Mi aspetto un random uniforme, nessun connettore deve avere più di n richieste
			// rispetto ad un altro connettore
			for (var connettore2 : howManys.keySet()) {
				assertTrue(Math.abs(howManys.get(connettore2) - howManys.get(connettore)) <= n ); 
			}
		}

	}


	private void checkLeastConnectionsAsRoundRobin(List<HttpResponse> list) {
		
	}
	
	
	private Map<Integer, List<HttpResponse>> makeRequests(List<HttpRequest> richieste, int nRequests) {
		int nthreads = Integer.valueOf(System.getProperty("soglia_richieste_simultanee"));

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nthreads);
		int i = 0;
		Map<Integer,List<HttpResponse>> responsesByKind = new ConcurrentHashMap<>();
		while(i<nRequests) {
			
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
	

	private void checkAllResponsesSameConnettore(List<HttpResponse> responsesSessione0) {
		String connettore = responsesSessione0.get(0).getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
		for(var resp : responsesSessione0) {
			assertEquals(connettore, resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		}
	}

}
