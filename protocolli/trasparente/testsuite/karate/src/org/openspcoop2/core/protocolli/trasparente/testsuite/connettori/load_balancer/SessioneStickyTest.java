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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.HEADER_CONDIZIONE;
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
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.ConsegnaCondizionaleByNomeTest;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;


// TODO: Cookie, prova a fare il test header http utilizzando come header il Cookie o i vari forwarded for.
// TODO: Anche qui test con valori multipli per lo stesso header,
//	  	 conflitti fra gli headers appartenenti alla classe XForwardedFor
// 		 e test con valori mancanti per i null pointers. NOTA: Andrea e tito hanno deciso
//		 che va bene prendere uno qualunque dei valori\header, faccio comunque il test per verificare
//		 che uno dei due venga preso
// TODO: gli health check si potrebbero scrivere tirando su un mock che si chiude da solo dopo 
//		aver gestito una richiesta.

// TODO: il logRateLimiting va sostituito con il loggerCore

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
 *	Query Velocity Template: $query["govway-testsuite-id_sessione_request"]
 *	Query UrlInvocazione: .+govway-testsuite-id_sessione_request=([^&]*).*
 *	Query contenuto: $.id_sessione_request
 *	Query HeaderHttp: GovWay-TestSuite-ID-Sessione
 *	Query ParametroUrl: govway-testsuite-id_sessione_request
 *	Query cookie: govway-testsuite-id_sessione_cookie
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

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
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

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
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
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_FreemarkerTemplate(IDSessioni.get(0), erogazione), 
				buildRequest_FreemarkerTemplate(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
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
		int remaining = Common.richiesteTestRandom - balancedResponses.size();
		while (remaining > 0) {
			int nReq = Math.min(Common.richiesteParallele, remaining);
			balancedResponses.addAll(Common.makeParallelRequests(richieste.get(2), nReq));
			remaining -= nReq;
		}

		checkWeightedRandomStrategy(balancedResponses, Common.pesiConnettoriStandard);
	}
	
	
	@Test	
	public void urlInvocazione() {
		final String erogazione = "LoadBalanceSessioneStickyUrlInvocazione";
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_UrlInvocazione(IDSessioni.get(0), erogazione), 
				buildRequest_UrlInvocazione(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
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
		int remaining = (getPesoTotale(Common.pesiConnettoriStandard)+1) - balancedResponses.size();
		balancedResponses.addAll(Common.makeParallelRequests(richieste.get(2), remaining));
		
		checkWeightedRoundRobin(balancedResponses, Common.pesiConnettoriStandard);
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
		int remaining = (getPesoTotale(Common.pesiConnettoriStandard)+1) - balancedResponses.size();
		balancedResponses.addAll(Common.makeParallelRequests(richieste.get(2), remaining));
		
		checkWeightedRoundRobin(balancedResponses, Common.pesiConnettoriStandard);	
	}
	
	
	
	@Test	
	public void parametroUrl() {
		final String erogazione = "LoadBalanceSessioneStickyParametroUrl";
		
		List<HttpRequest> richieste = Arrays.asList(
				buildRequest_ParametroUrl(IDSessioni.get(0), erogazione), 
				buildRequest_ParametroUrl(IDSessioni.get(1), erogazione),
				buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele);
		
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
		int remaining = Common.richiesteTestRandom - balancedResponses.size();
		while (remaining > 0) {
			int nReq = Math.min(Common.richiesteParallele, remaining);
			balancedResponses.addAll(Common.makeParallelRequests(richieste.get(2), nReq));
			remaining -= nReq;
		}
		
		checkRandomStrategy(balancedResponses, Common.pesiConnettoriStandard);
	}
	
		
	@Test
	public void velocityTemplate() throws InterruptedException, ExecutionException {
		
		// TODO 1 Qui il problema è dovuto al fatto che il velocity template $query["govway-testsuite-id_sessione_request"] 
		// restituisce il template stesso se la chiave non è presente, quindi un id sessione veniva comunque trovato.
				
		final String erogazione = "LoadBalanceSessioneStickyVelocityTemplate";
		
		// Lancio le due chiamate bloccanti, devono durare per tutto il test
		HttpRequest requestBlockingIdSessione1 = buildRequest_VelocityTemplate(IDSessioni.get(0), erogazione);
		requestBlockingIdSessione1.setUrl(requestBlockingIdSessione1.getUrl()+"&sleep="+Common.durataBloccanteLunga);
		
		HttpRequest requestBlockingIdSessione2 = buildRequest_VelocityTemplate(IDSessioni.get(1), erogazione);
		requestBlockingIdSessione2.setUrl(requestBlockingIdSessione2.getUrl()+"&sleep="+Common.durataBloccanteLunga);
		
		var futureResp1 = Utils.makeBackgroundRequest(requestBlockingIdSessione1);
		
		org.openspcoop2.utils.Utilities.sleep(delayRichiesteBackground);
		
		var futureResp2 = Utils.makeBackgroundRequest(requestBlockingIdSessione2);

		// Faccio una serie di richieste con id sessione impostato e verifico che vadano tutte nello stesso connettore
		// Per il debug di andrea commento queste richieste qui. TODO: decommentare sotto dopo il fix di andrea
		/*List<HttpRequest> richieste = Arrays.asList(
				buildRequest_VelocityTemplate(Common.IDSessioni.get(0), erogazione), 
				buildRequest_VelocityTemplate(Common.IDSessioni.get(1), erogazione)				
			);
		
		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, Common.richiesteParallele-2);
		checkAllResponsesSameConnettore(responsesByKind.get(0));
		checkAllResponsesSameConnettore(responsesByKind.get(1));

		String connettoreSessione0 = responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		String connettoreSessione1 = responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		
		assertNotEquals(connettoreSessione0, connettoreSessione1);
		
		getLoggerCore().info("Connettore sessione 0: " + connettoreSessione0);
		getLoggerCore().info("Connettore sessione 1: " + connettoreSessione1);*/
		
		assertFalse(futureResp1.isDone() || futureResp2.isDone());
		
		// Faccio 2 richieste senza idSessione impostato e verifico che vadano a finire negli altri 2 connettori
		// TODO: Questo è perchè il velocity template restituisce il template stesso se l'accesso alla map $query fallisce.
		
		HttpRequest requestBalanced = buildRequest_LoadBalanced(erogazione);
		requestBalanced.setUrl(requestBalanced.getUrl()+"&sleep="+Common.durataBloccante);
		
		var futureBlockingResponses = Utils.makeBackgroundRequests(requestBalanced, 2, delayRichiesteBackground);
		
		Vector<HttpResponse> balancedResponses = Utils.awaitResponses(futureBlockingResponses);
		
		var howManys = Common.contaConnettoriUtilizzati(balancedResponses);
		Common.printMap(howManys);
		for (var connettore : howManys.keySet()) {
			// TODO: Qui fallisce.
			
			// TODO: Verificare anche che i connettori utilizzati senza id di sessione siano differenti da quelli utilizzati con l'id di sessione
			
			assertEquals(Integer.valueOf(1), howManys.get(connettore));
		}
		
		assertFalse(futureResp1.isDone() || futureResp2.isDone());
		
		// Mi assicuro che le richieste con id sessione impostato siano andate sui connettori
		// scelti durante le prime due richieste.		
		
		var responseIdSessione0 = futureResp1.get();
		var responseIdSessione1 = futureResp2.get();
		
		
	/*	assertEquals(connettoreSessione0, responseIdSessione0.getHeaderFirstValue(HEADER_ID_CONNETTORE)); TODO: Decommentare dopo il fix di andrea
		assertEquals(connettoreSessione1, responseIdSessione1.getHeaderFirstValue(HEADER_ID_CONNETTORE));*/
		
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
		
		// TODO: Non funziona il passive Health Check.
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
			} else {
				assertEquals(null, idSessioneConnettoreRotto);	// Un solo connettore rotto
				idSessioneConnettoreRotto = request.getHeaderFirstValue(HEADER_ID_SESSIONE);
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
		
		// Attendo che venga reinserito nel pool e ripeto la parte iniziale del test
		// per instradare una richiesta nuovamente sul connettore rotto
		// TODO: Usare il parametro e aumentare anche nella configurazione da 2 a 4 o 5
		org.openspcoop2.utils.Utilities.sleep(2100);

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
		// TODO dopo l'eventuale fix sul maxAge
	}
	
	
	private static String findPoolForConnettore(String connettore) {
		
		Set<String> toDiscard = Set.of(Common.POOL_LOCALHOST, Common.POOL_ROTTO);
		
		for( var poolAndConnettori : Common.connettoriPools.entrySet()) {			
			if (toDiscard.contains(poolAndConnettori.getKey())) {
				continue;
			}			
			else if (poolAndConnettori.getValue().contains(connettore)) {
				return poolAndConnettori.getKey();
			}			
		}
		return null;
	}
	
	
	@Test
	public void consegnaCondizionaleSemplice() {
		final String erogazione = "LoadBalanceSessioneStickyConsegnaCondizionaleSempliceHeaderHttp";
		
		// Simile al test consegnaCondizionale ma tutte le richieste lavorano
		// con il filtro.
		String pool1 = Common.POOL_0;
		String pool2 = Common.POOL_1;
		String pool3 = Common.POOL_2;
		
		HttpRequest req1 = buildRequest_HeaderHttp(IDSessioni.get(0), erogazione);
		req1.addHeader(HEADER_CONDIZIONE, Common.filtriPools.get(pool1).get(0));
		
		HttpRequest req2 = buildRequest_HeaderHttp(IDSessioni.get(1), erogazione);
		req2.addHeader(HEADER_CONDIZIONE, Common.filtriPools.get(pool2).get(0));
		
		HttpRequest req3 = ConsegnaCondizionaleByNomeTest.buildRequest_HeaderHttpByNome(
				Common.filtriPools.get(pool3).get(0), erogazione);

		List<HttpRequest> richieste = Arrays.asList(
				req1, 			// Vanno tutte sullo stesso connettore dell pool1
				req2,			// Vanno tutte sullo stesso connettore del pool2
				req3			// Vengono bilanciate in round robin sul connettore del pool3
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, richieste.size()*5);
		
		checkAllResponsesSameConnettore(responsesByKind.get(0));
		checkAllResponsesSameConnettore(responsesByKind.get(1));

		Set<String> connettoriBilanciati = new HashSet<>(Common.connettoriPools.get(pool3));
		connettoriBilanciati.remove(Common.CONNETTORE_DISABILITATO);
		
		checkRoundRobin(responsesByKind.get(2), connettoriBilanciati);	
	}
	
	@Test
	public void consegnaCondizionale() {
		
		// Sulla erogazione l'identificazione condizione fallita è impostata in modo
		// da utilizzare tutti i connettori possibili.
		// In questo modo posso in questo testo posso fare richieste senza l'id condizione
		// impostato.
		
		// TODO: Il fatto di avere un'associazione

		
		final String erogazione = "LoadBalanceSessioneStickyConsegnaCondizionaleHeaderHttp";
		
		// Prima faccio avvenire delle richieste solo con l'id sessione per sapere in che pool
		// sono finiti i connettori sticky.
		// Fare test semplice che mostra come non ricorda l'associazione tra id sessione
		// e connettore quando si cambia il pool con il filtro.
		
		HttpRequest req1 = buildRequest_HeaderHttp(IDSessioni.get(0), erogazione);
		HttpRequest req2 = buildRequest_HeaderHttp(IDSessioni.get(1), erogazione);
		
		var resp1 = Utils.makeRequest(req1);
		var resp2 = Utils.makeRequest(req2);
		
		String connettore1 = resp1.getHeaderFirstValue(HEADER_ID_CONNETTORE); 
		String connettore2 = resp2.getHeaderFirstValue(HEADER_ID_CONNETTORE);
		
		// Siamo in round robin quindi i connettori devono essere diversi.
		assertNotEquals(null, connettore1);
		assertNotEquals(connettore1, connettore2);
		
		String pool1 = findPoolForConnettore(connettore1);
		String pool2 = findPoolForConnettore(connettore2);
		
		Set<String> poolCandidates = new HashSet<>(Common.pools);
		poolCandidates.remove(pool1);
		poolCandidates.remove(pool2);
		String pool3 = poolCandidates.stream().findAny().get();

		// Successivamente ripeto le richieste con l'id sessione più filtro 
		// insieme ad altre richieste senza id sessione e con filtro
		// ed altre richieste senza id sessione e senza filtro...
		req1.addHeader(HEADER_CONDIZIONE, Common.filtriPools.get(pool1).get(0));
		req2.addHeader(HEADER_CONDIZIONE, Common.filtriPools.get(pool2).get(0));
		
		HttpRequest requestSoloFiltro = ConsegnaCondizionaleByNomeTest
				.buildRequest_HeaderHttpByNome(
						Common.filtriPools.get(pool3).get(0),
						erogazione);
		
		HttpRequest requestSoloIdSessione = buildRequest_HeaderHttp("IDSessioneDiverso", erogazione);
		HttpRequest requestSemplice = buildRequest_LoadBalanced(erogazione);
				
		List<HttpRequest> richieste = Arrays.asList(
				req1, 		// Va nel pool1, senza attivare il load balancing perchè l'id sessione è impostato
				req2,		// Va nel pool2, senza attivare il load balancing perchè l'id sessione è impostato
				requestSoloIdSessione, // Attiva una sola volta il load balancing su TUTTI i connettori perchè l'id sessione è impostato ma è nuovo
				requestSoloFiltro, // Va nel pool3 che è diverso da tutti gli altri perchè scelto ad hoc
				requestSemplice // Va nel pool di tutti i connettori
			);

		Map<Integer, List<HttpResponse>> responsesByKind = makeRequests(richieste, richieste.size()*5);

		// Tests:
		// Le prime richieste vanno tutte sullo stesso connettore1
		assertEquals(connettore1, responsesByKind.get(0).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE));
		checkAllResponsesSameConnettore(responsesByKind.get(0));
		
		// Le seconde richieste vanno tutte sullo stesso connettore2
		assertEquals(connettore2, responsesByKind.get(1).get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE));
		checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Le terze richieste vanno tutte sullo stesso connettore X
		checkAllResponsesSameConnettore(responsesByKind.get(2));

		// Le quarte richieste vanno sul poolW diverso da tutti e con un round robin suo
		var connettoriAbilitatiPool3 = new HashSet<>(Common.connettoriPools.get(pool3));
		connettoriAbilitatiPool3.remove(Common.CONNETTORE_DISABILITATO);
		
		checkRoundRobin(responsesByKind.get(3), connettoriAbilitatiPool3);
		
		// Le quinte risposte vanno sul pool di tutti i connettori e hanno un round robin loro
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(4);
		balancedResponses.add(resp1);
		balancedResponses.add(resp2);
		balancedResponses.add(responsesByKind.get(2).get(0));
		checkRoundRobin(balancedResponses, Common.setConnettoriAbilitati);
	}
	
	
	@Test
	public void consegnaCondizionaleConflitti() {
		// TODO: La consegna condizionale dice di andare su un pool di connettori
		//		ma l'id sessione dice di andare su un altro.
		
		final String erogazione = "LoadBalanceSessioneStickyConsegnaCondizionaleConflitti";
		
		// Simile al test consegnaCondizionale ma tutte le richieste lavorano
		// con il filtro.
		String pool1 = Common.POOL_0;
		String pool2 = Common.POOL_2;
		
		// Prima faccio avvenire una richiesta solo con l'id sessione per creare
		// la sessione. 
		HttpRequest req1 = buildRequest_HeaderHttp(IDSessioni.get(0), erogazione);
		req1.addHeader(HEADER_CONDIZIONE, Common.filtriPools.get(pool1).get(0));

		var resp1 = Utils.makeRequest(req1);
		
		String connettore1 = resp1.getHeaderFirstValue(HEADER_ID_CONNETTORE); 
		
		// Poi faccio delle richieste con un pool disgiunto dal POOL_0 ma stesso id sessione 
		HttpRequest requestConflict = buildRequest_HeaderHttp(IDSessioni.get(0), erogazione);
		req1.addHeader(HEADER_CONDIZIONE, Common.filtriPools.get(pool1).get(0));
		
		var responseConflict = Utils.makeRequest(requestConflict);
		
		assertNotEquals(200, resp1.getResultHTTPOperation());

		

	}
	
	

	static int getPesoTotale(Map<String,Integer> pesiConnettori) {
		return pesiConnettori.values().stream().reduce(0, Integer::sum);
	}
	
	
	static void checkRoundRobin(List<HttpResponse> responses, Set<String> connettori) {
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

	
	static void checkRandomStrategy(List<HttpResponse> responses, Map<String,Integer> pesiConnettori) {
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

	
	static Map<Integer, List<HttpResponse>> makeRequests(List<HttpRequest> richieste, int nRequests) {
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
	

	static void checkAllResponsesSameConnettore(List<HttpResponse> responses) {
		String connettore = responses.get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertNotEquals(null, connettore);
		for(var resp : responses) {
			assertEquals(connettore, resp.getHeaderFirstValue(HEADER_ID_CONNETTORE));
		}
	}

}
