/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import static org.junit.Assert.assertTrue;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_0;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_3;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_DISABILITATO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.HEADER_ID_CONDIZIONE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * ConsegnaCondizionaleByNome
 * 
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 *	Connettore0, Connettore1, Connettore2, Connettore3, ConnettoreDisabilitato, ConnettoreRotto
 *	
 *   Invio N batch (per ora 1) di 15 richieste parallele, tre per connettore. Verifico che ciascuna richiesta
 *   raggiunga il connettore deisderato. In questo modo simulo un pattern di richieste verosimile.
 *   Faccio anche tre batch di richieste che ottengono 400, il test non è in grado di distinguere tra i casi
 *   di identificazione fallita, connettore non trovato o connettore disabilitato, quello che ci interessa in questi
 *   test è che la consegna condizionale avvenga correttamente sotto un carico di lavoro realistico.
 *  
 * 
 *
 *   Non vengono fatti test di case sensitivity sui valori in quanto i valori di parametri query, headers http
 *   e contenuto della richiesta sono tutti case sensitive.
 * 
 */
public class ConsegnaCondizionaleByNomeTest extends ConfigLoader {
	
	public static List<String> connettoriTestati = List
			.of(CONNETTORE_0,
				CONNETTORE_1, 
				CONNETTORE_2,
				CONNETTORE_3,
				Common.CONNETTORE_ID_FALLITA,
				Common.CONNETTORE_ID_NON_TROVATO,
				CONNETTORE_DISABILITATO);
	

	@Test
	public void headerHttp() {
		final String erogazione = "ConsegnaCondizionaleHeaderHttpByNome";
		
		HttpRequest request0 = RequestBuilder.buildRequest_HeaderHttp(CONNETTORE_0, erogazione);
		HttpRequest request1 = RequestBuilder.buildRequest_HeaderHttp(CONNETTORE_1, erogazione);
		HttpRequest request2 = RequestBuilder.buildRequest_HeaderHttp(CONNETTORE_2, erogazione);
		// La terza richiesta specifica due volte lo stesso connettore. deve comunque funzionare
		HttpRequest request3 = RequestBuilder.buildRequest_HeaderHttp(CONNETTORE_3, erogazione);
		request3.addHeader(HEADER_ID_CONDIZIONE, CONNETTORE_3); 	
		
		HttpRequest requestRotto = RequestBuilder.buildRequest_HeaderHttp(Common.CONNETTORE_ROTTO, erogazione);

		// Testo l'instradamento verso il connettore rotto solo in questo test.
		HttpRequest[] requestsByConnettore = { request0, request1, request2, request3, requestRotto };
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
			logCore.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		
		for(int i=0;i<requestsByConnettore.length;i++) {
			String connettoreRichiesta = requestsByConnettore[i].getHeaderFirstValue(HEADER_ID_CONDIZIONE);
			
			for(var response : responsesByConnettore.get(i)) {
				
				String connettoreRisposta = response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
				if (connettoreRisposta == null) {
					connettoreRisposta = Common.CONNETTORE_ROTTO;
				}
				
				assertEquals(connettoreRichiesta, connettoreRisposta);
			}
			
		}
	}
	
	
	@Test
	public void headerHttpConflitti() throws UtilsException {
		// Quando sono presenti più valori da estrarre, govway sceglie il primo ed è 
		// contento con quello.
		
		final String erogazione = "ConsegnaCondizionaleHeaderHttpByNome";
		
		HttpRequest request = RequestBuilder.buildRequest_HeaderHttp(CONNETTORE_1, erogazione);
		request.addHeader(HEADER_ID_CONDIZIONE, CONNETTORE_0);

		var response = HttpUtilities.httpInvoke(request);
		assertEquals(200,response.getResultHTTPOperation());
		
		String connettore = response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
		boolean oneOrTheOther = connettore.equals(CONNETTORE_1) || connettore.equals(CONNETTORE_0);
		
		assertTrue(oneOrTheOther);
	}


	@Test
	public void parametroUrlConflitti() throws UtilsException {
		// Quando sono presenti più valori da estrarre, govway sceglie il primo ed è 
		// contento con quello.
		
		final String erogazione = "ConsegnaCondizionaleParametroUrlByNome";

		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-parametro-url"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request="+CONNETTORE_0
				+ "&govway-testsuite-id_connettore_request="+CONNETTORE_1);
		
		var response = HttpUtilities.httpInvoke(request);
		assertEquals(200,response.getResultHTTPOperation());
		
		String connettore = response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
		boolean oneOrTheOther = connettore.equals(CONNETTORE_1) || connettore.equals(CONNETTORE_0);
		
		assertTrue(oneOrTheOther);
	}
	
	
	@Test
	public void XForwardedForConflitti() throws UtilsException {		
		
		final String erogazione = "ConsegnaCondizionaleXForwardedForByNome";
		List<String> forwardedHeaders = HttpUtilities.getClientAddressHeaders();

		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.addHeader(forwardedHeaders.get(0), CONNETTORE_0);
		request.addHeader(forwardedHeaders.get(1), CONNETTORE_1);
		
		var response = HttpUtilities.httpInvoke(request);
		assertEquals(200,response.getResultHTTPOperation());
		
		String connettore = response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
		boolean oneOrTheOther = connettore.equals(CONNETTORE_1) || connettore.equals(CONNETTORE_0);
		
		assertTrue(oneOrTheOther);
	}
	
	
	@Test
	public void urlInvocazione() {
		
		final String erogazione = "ConsegnaCondizionaleUrlInvocazioneByNome";
		// Da qui in poi Il pattern per i test della consegna condizionale è lo steso per tutti.
		// Mando tanti thread in parallelo quanti sono i connettori da raggiungere, con ciascun
		// thread che raggiunge sempre lo stesso connettore.
		// Quindi Controllo che nelle risposte di quel thread sia stato raggiunto sempre lo stesso connettore. 
		
		var requestsByConnettore = Common.connettoriAbilitati.stream()
				.map(c -> RequestBuilder.buildRequest_UrlInvocazione(c,erogazione))
				.collect(Collectors.toList());
		
		HttpRequest requestIdentificazioneFallita = Common.buildRequest_Semplice(erogazione);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildRequest_UrlInvocazione("ConnettoreInesistente", erogazione);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildRequest_UrlInvocazione(CONNETTORE_DISABILITATO, erogazione);
		
		requestsByConnettore.add(requestIdentificazioneFallita);
		requestsByConnettore.add(requestConnettoreNonTrovato);
		requestsByConnettore.add(requestConnettoreDisabilitato);
							
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettoriRest(connettoriTestati, responsesByConnettore);
	}

	
	@Test
	public void parametroUrl() {
		final String erogazione = "ConsegnaCondizionaleParametroUrlByNome";
		
		var requestsByConnettore = Common.connettoriAbilitati.stream()
				.map(c -> RequestBuilder.buildRequest_ParametroUrl(c,erogazione))
				.collect(Collectors.toList());
							
		HttpRequest requestIdentificazioneFallita = Common.buildRequest_Semplice(erogazione);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildRequest_ParametroUrl("ConnettoreInesistente", erogazione);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildRequest_ParametroUrl(CONNETTORE_DISABILITATO, erogazione);
		
		requestsByConnettore.add(requestIdentificazioneFallita);
		requestsByConnettore.add(requestConnettoreNonTrovato);
		requestsByConnettore.add(requestConnettoreDisabilitato);
							
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettoriRest(connettoriTestati, responsesByConnettore);

	}
	
	
	
	@Test
	public void soapAction1_1() {
		final String erogazione = "ConsegnaCondizionaleByNomeSoap";
		soapActionImpl(erogazione, HttpConstants.CONTENT_TYPE_SOAP_1_1);
	}
	
	
	@Test
	public void soapAction1_2() {
		final String erogazione = "ConsegnaCondizionaleByNomeSoap";
		soapActionImpl(erogazione, HttpConstants.CONTENT_TYPE_SOAP_1_2);
	}
	
	
	
	
	@Test
	public void contenuto() {
		
		final String erogazione = "ConsegnaCondizionaleContenutoByNome";
		
		var requestsByConnettore = Common.connettoriAbilitati.stream()
				.map(c -> RequestBuilder.buildRequest_Contenuto(c,erogazione))
				.collect(Collectors.toList());
							
		HttpRequest requestIdentificazioneFallita = Common.buildRequest_Semplice(erogazione);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildRequest_Contenuto("ConnettoreInesistente", erogazione);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildRequest_Contenuto(CONNETTORE_DISABILITATO, erogazione);
		
		requestsByConnettore.add(requestIdentificazioneFallita);
		requestsByConnettore.add(requestConnettoreNonTrovato);
		requestsByConnettore.add(requestConnettoreDisabilitato);
							
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettoriRest(connettoriTestati, responsesByConnettore);
	}
	
	
	@Test
	public void testConnettoreDisabilitato() {
		
		final String erogazione = "ConsegnaCondizionaleContenutoByNome";
		
		List<String> connettori = Arrays.asList( CONNETTORE_0,
				CONNETTORE_1,
				CONNETTORE_2,
				CONNETTORE_3,
				CONNETTORE_DISABILITATO);
		
		var requestsByConnettore = connettori.stream()
				.map(c -> RequestBuilder.buildRequest_Contenuto(c,erogazione))
				.collect(Collectors.toList());
							
		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
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
	public void clientIp() {
		/**
		 * Non ho modo di cambiare l'indirizzo ip sorgente, per cui verifico semplicemente
		 * che tutto vada a finire nello stesso connettore.
		 */
		final String erogazione = "ConsegnaCondizionaleClientIpByNome";
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		var responses = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeParallelRequests(request, 15);
		for (var resp : responses) {
			assertEquals(200, resp.getResultHTTPOperation());
			assertEquals("127.0.0.1", resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));			
		}	
	}
	
	
	@Test
	public void XForwardedFor() throws UtilsException {
		
		List<String> forwardedHeaders = HttpUtilities.getClientAddressHeaders();
		
		final String erogazione = "ConsegnaCondizionaleXForwardedForByNome";
		
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(15);
		List<List<HttpResponse>> responsesByConnettore = new java.util.ArrayList<>(Common.connettoriAbilitati.size());

		// Inizializzo le risposte, alla richiesta di indice i verrà assegnato il connettore di indice i
		// responsesByConnettore.get(i) restituisce le risposte relative all'i-esimo connettore
		Common.connettoriAbilitati.forEach( c -> responsesByConnettore.add(new java.util.ArrayList<>()));

		// Voglio usare tutti gli headers possibili, quindi pesco ogni volta un nome di header 
		// appartenente alla classe Forwarded-For diverso.
		
		for (int i = 0; i < 15; i++) {
			
			int index_connettore = i%Common.connettoriAbilitati.size();
			int index_header = i%forwardedHeaders.size(); 
			
			executor.execute(() -> {
				try {
					String header_condizione = forwardedHeaders.get(index_header);
					String connettore = Common.connettoriAbilitati.get(index_connettore);
					
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
			logCore.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		
		matchResponsesWithConnettoriRest(Common.connettoriAbilitati, responsesByConnettore);
	}
	
	
	@Test
	public void template() {
		
		// Il template pesca il valore del parametro query govway-testsuite-id_connettore_request,
		// riuso quanto fatto per il test parametro query
		final String erogazione = "ConsegnaCondizionaleTemplateByNome";
		
		var requestsByConnettore = Common.connettoriAbilitati.stream()
				.map(c -> RequestBuilder.buildRequest_Template(c, erogazione))
				.collect(Collectors.toList());
							
		HttpRequest requestIdentificazioneFallita = Common.buildRequest_Semplice(erogazione);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildRequest_Template("ConnettoreInesistente", erogazione);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildRequest_Template(CONNETTORE_DISABILITATO, erogazione);
		
		requestsByConnettore.add(requestIdentificazioneFallita);
		requestsByConnettore.add(requestConnettoreNonTrovato);
		requestsByConnettore.add(requestConnettoreDisabilitato);
							
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettoriRest(connettoriTestati, responsesByConnettore);
	}
	
	
	@Test
	public void freemarkerTemplate() {
		// Test uguale a templateByNome
		final String erogazione = "ConsegnaCondizionaleFreemarkerTemplateByNome";
		
		var requestsByConnettore = Common.connettoriAbilitati.stream()
				.map(c -> RequestBuilder.buildRequest_FreemarkerTemplate(c, erogazione))
				.collect(Collectors.toList());

		HttpRequest requestIdentificazioneFallita = Common.buildRequest_Semplice(erogazione);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildRequest_FreemarkerTemplate("ConnettoreInesistente", erogazione);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildRequest_FreemarkerTemplate(CONNETTORE_DISABILITATO, erogazione);
		
		requestsByConnettore.add(requestIdentificazioneFallita);
		requestsByConnettore.add(requestConnettoreNonTrovato);
		requestsByConnettore.add(requestConnettoreDisabilitato);
							
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettoriRest(connettoriTestati, responsesByConnettore);
	}
	
	
	@Test
	public void velocityTemplate() {
		// Test uguale a templateByNome
		final String erogazione = "ConsegnaCondizionaleVelocityTemplateByNome";
		
		var requestsByConnettore = Common.connettoriAbilitati.stream()
				.map(c -> RequestBuilder.buildRequest_VelocityTemplate(c, erogazione))
				.collect(Collectors.toList());
							
		HttpRequest requestIdentificazioneFallita = Common.buildRequest_Semplice(erogazione);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildRequest_VelocityTemplate("ConnettoreInesistente", erogazione);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildRequest_VelocityTemplate(CONNETTORE_DISABILITATO, erogazione);
		
		requestsByConnettore.add(requestIdentificazioneFallita);
		requestsByConnettore.add(requestConnettoreNonTrovato);
		requestsByConnettore.add(requestConnettoreDisabilitato);
							
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettoriRest(connettoriTestati, responsesByConnettore);
	}
	
	
	@Test
	public void regole() {
		
		final String erogazione = "ConsegnaCondizionaleRegoleByNome";
		// Ho 10 regole, le nove testate sopra più una statica: sulla risorsa /test-regola-statica, si va al Connettore0
		// Faccio partire un batch di 10 thread, ciascuno deve raggiungere un connettore.
		
		HttpRequest requestForwardedFor = new HttpRequest();
		requestForwardedFor.setMethod(HttpRequestMethod.GET);
		requestForwardedFor.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-xforwarded-for"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		requestForwardedFor.addHeader("X-Forwarded-For", CONNETTORE_3);
		
		HttpRequest requestIdentificazioneStatica = new HttpRequest();
		requestIdentificazioneStatica.setMethod(HttpRequestMethod.GET);
		requestIdentificazioneStatica.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-statica"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX); 
		
		HttpRequest requestIdentificazioneClientIp= new HttpRequest();
		requestIdentificazioneClientIp.setMethod(HttpRequestMethod.GET);
		requestIdentificazioneClientIp.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-client-ip"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		
		List<List<Object>> connettoriAndrequests = Arrays.asList(
				Arrays.asList(CONNETTORE_0, RequestBuilder.buildRequest_HeaderHttp(CONNETTORE_0, erogazione)),
				Arrays.asList(CONNETTORE_1, RequestBuilder.buildRequest_UrlInvocazione(CONNETTORE_1, erogazione)),
				Arrays.asList(CONNETTORE_2, RequestBuilder.buildRequest_ParametroUrl(CONNETTORE_2, erogazione)),				
				Arrays.asList(CONNETTORE_3, RequestBuilder.buildRequest_Contenuto(CONNETTORE_3, erogazione)),				
				Arrays.asList(CONNETTORE_0, RequestBuilder.buildRequest_Template(CONNETTORE_0, erogazione)),
				Arrays.asList(CONNETTORE_1, RequestBuilder.buildRequest_FreemarkerTemplate(CONNETTORE_1, erogazione)),
				Arrays.asList(CONNETTORE_2, RequestBuilder.buildRequest_VelocityTemplate(CONNETTORE_2, erogazione)),
				Arrays.asList(CONNETTORE_3, requestForwardedFor),
				Arrays.asList(CONNETTORE_0,requestIdentificazioneStatica),
				Arrays.asList(null, requestIdentificazioneClientIp)
			);
		
		var requestsByConnettore = connettoriAndrequests
				.stream()
				.map( (List<Object> l) -> (HttpRequest) l.get(1))
				.collect(Collectors.toList());
		
		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
		// Per tutti i batch so determinare a priori quale sarà il connettore di destinazione,
		// tranne che per l'ultimo, il ClientIp.
		for(int i=0;i<requestsByConnettore.size()-1;i++) {  
			String connettoreRichiesta = (String) connettoriAndrequests.get(i).get(0);
			
			for(var response : responsesByConnettore.get(i)) {
				String connettoreRisposta = response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);			
				assertEquals(connettoreRichiesta, connettoreRisposta);
			}
		}

		// Testo che l'identificazione per client Ip sia andata tutta sullo stesso connettore
		var clientIpResponses = responsesByConnettore.get(responsesByConnettore.size()-1); //lastElement();
		String connettore = clientIpResponses.get(0).getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
		for (var resp : clientIpResponses) {
			assertEquals(200, resp.getResultHTTPOperation());
			assertEquals(connettore, resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));			
		}	
	}
	
	
	@Test
	public void ordinamentoRegole() throws UtilsException {
		// Le regole vengono matchate in ordine, appena una regola matcha la risorsa corrente 
		// viene fatta l'identificazione del connettore. Se l'identificazione fallisce tutto
		// il processo deve fallire.
		
		final String erogazione = "ConsegnaCondizionaleRegoleByNome";
		
		HttpRequest requestForwardedFor = new HttpRequest();
		requestForwardedFor.setMethod(HttpRequestMethod.GET);
		requestForwardedFor.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-xforwarded-for"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		requestForwardedFor.addHeader(HEADER_ID_CONDIZIONE, CONNETTORE_0);
		
		// Sebbene sull'erogazione sia presente la regola di default che guarda il valore dello header
		// HEADER_CONDIZIONE, la richiesta deve comunque fallire perchè la regola matchata è quella 
		// per lo header X-Forwarded-for
		
		var response = HttpUtilities.httpInvoke(requestForwardedFor);
		
		assertEquals(400, response.getResultHTTPOperation());
	}
	
	
	
	@Test
	public void prefisso() {
		/*
		 * Mando solo i suffissi, la consegna condizionale aggiungerà il prefisso "Connettore"
		 * mentre l'id connettore inviato al server di echo resta lo stesso.
		 */
		final String erogazione = "ConsegnaCondizionalePrefisso";
		
		var connettoriSuffissi = Arrays.asList("0", "1", "2", "3"); 
		
		var requestsByConnettore = connettoriSuffissi.stream()
				.map(c -> RequestBuilder.buildRequest_HeaderHttp(c, erogazione))
				.collect(Collectors.toList());
							
		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettoriRest(Common.connettoriAbilitati, responsesByConnettore);	
	}
	
	@Test
	public void suffisso() {
		/*
		 * Mando il nome del connettore per intero, la consegna condizionale aggiungerà il suffisso
		 * "Suffisso-test" mentre l'id connettore inviato al server di echo resta lo stesso.
		 * 
		 */
		final String erogazione = "ConsegnaCondizionaleSuffisso";
		
		var requestsByConnettore = Common.connettoriAbilitati.stream()
				.map(c -> RequestBuilder.buildRequest_HeaderHttp(c, erogazione))
				.collect(Collectors.toList());
							
		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettoriRest(Common.connettoriAbilitati, responsesByConnettore);	
	}
	
	
	@Test
	public void prefissoESuffisso() {
		/*
		 * Mando solo i numeri dei connettori, la consegna condizionale aggiungerà il prefisso "Connettore"
		 * e il suffisso "-SuffissoTest", mentre l'id connettore inviato al server di echo resta lo stesso.
		 */
		
		final String erogazione = "ConsegnaCondizionalePrefissoESuffisso";
		
		var connettoriSuffissi = Arrays.asList("0", "1", "2", "3"); 
		
		var requestsByConnettore = connettoriSuffissi.stream()
				.map(c -> RequestBuilder.buildRequest_HeaderHttp(c, erogazione))
				.collect(Collectors.toList());
							
		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettoriRest(Common.connettoriAbilitati, responsesByConnettore);	
		
	}


	
	@Test
	public void unicode() throws UtilsException {
		
		// Connettori:
		//	😛😛 => Connettore0
		//  ΛΛ => Connettore1
		
		final String erogazione = "ConsegnaCondizionaleUnicode";
		
		final String content = "{ \"id_connettore_request\": \"😛😛\" }";
		final String content2 = "{ \"id_connettore_request\": \"ΛΛ\" }";

		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-contenuto"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.setContentType("application/json");
		request.setContent(content.getBytes());
		
		var response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());
		assertEquals(CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));

		// Forwarded For
		request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-freemarker-template"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.setContentType("application/json");
		request.setContent(content2.getBytes());
		
		response = HttpUtilities.httpInvoke(request);
				
		if(Utils.isJenkins()) {
			if(response.getResultHTTPOperation() == 400) {
				System.out.println("WARNING: operazione che fallisce in ambiente jenkins, funziona con il riavvia di Tomcat.");
			}
			else {
				assertEquals(200, response.getResultHTTPOperation());
				assertEquals(CONNETTORE_1, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
			}
		}
		else {
			assertEquals(200, response.getResultHTTPOperation());
			assertEquals(CONNETTORE_1, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		}
	}
	
	static void soapActionImpl(String erogazione, String versioneSoap) {

		var requestsByConnettore = Common.connettoriAbilitati.stream()
				.map(c -> RequestBuilder.buildSoapRequest(erogazione, c,c, versioneSoap))
				.collect(Collectors.toList());
							
		HttpRequest requestIdentificazioneFallita = RequestBuilder.buildSoapRequest_Semplice(erogazione, versioneSoap);
		HttpRequest requestConnettoreNonTrovato = RequestBuilder.buildSoapRequest(erogazione, "ConnettoreInesistente", "ConnettoreInesistente",  versioneSoap);
		HttpRequest requestConnettoreDisabilitato = RequestBuilder.buildSoapRequest(erogazione, CONNETTORE_DISABILITATO, CONNETTORE_DISABILITATO,  versioneSoap);
		
		requestsByConnettore.add(requestIdentificazioneFallita);
		requestsByConnettore.add(requestConnettoreNonTrovato);
		requestsByConnettore.add(requestConnettoreDisabilitato);
							
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettoriSoap(connettoriTestati, responsesByConnettore);
	}


	/*
	 * Esegue un thread per ogni richiesta e per ogni thread esegue
	 * requests_per_batch richieste
	 * 
	 * Restituisce le risposte raggruppate per richiesta, e.g: il primo vettore di
	 * risposte corrisponde al batch di richieste fatte per la prima richiesta della
	 * lista `requests`
	 */
	public static List<List<HttpResponse>> makeBatchedRequests(List<HttpRequest> requests, int nsequential_requests) {
		assertTrue(Common.sogliaRichiesteSimultanee >= requests.size());
		
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(requests.size());
		var ret = new ArrayList<List<HttpResponse>>(requests.size());
	
		for (int i = 0; i < requests.size(); i++) {
			ret.add(new java.util.ArrayList<>());
			int index = i;
	
			executor.execute(() -> {
				ret.get(index).addAll(org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeSequentialRequests(requests.get(index), nsequential_requests));
			});
		}
	
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// logCore.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
	
		return ret;
	}


	public static void matchResponsesWithConnettoriRest(List<String> connettori, List<List<HttpResponse>> responsesByConnettore) {
		assertEquals(connettori.size(), responsesByConnettore.size());
		
		for (int i = 0; i < connettori.size(); i++) {
			String connettoreRichiesta = connettori.get(i);
			
			
			if (connettoreRichiesta.equals(CONNETTORE_DISABILITATO) 
					|| connettoreRichiesta.equals(Common.CONNETTORE_ID_FALLITA) 
					|| connettoreRichiesta.equals(Common.CONNETTORE_ID_NON_TROVATO)) {
				
				for (var response : responsesByConnettore.get(i)) {
					assertEquals(400,response.getResultHTTPOperation());
				}
				
			} else {
				for (var response : responsesByConnettore.get(i)) {
					String connettoreRisposta = response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
					assertEquals(connettoreRichiesta, connettoreRisposta);
				}
			}
		}
	}
	
	
	public static void matchResponsesWithConnettoriSoap(List<String> connettori, List<List<HttpResponse>> responsesByConnettore) {
		assertEquals(connettori.size(), responsesByConnettore.size());
		
		for (int i = 0; i < connettori.size(); i++) {
			String connettoreRichiesta = connettori.get(i);
			
			
			if (connettoreRichiesta.equals(CONNETTORE_DISABILITATO) 
					|| connettoreRichiesta.equals(Common.CONNETTORE_ID_FALLITA) 
					|| connettoreRichiesta.equals(Common.CONNETTORE_ID_NON_TROVATO)) {
				
				for (var response : responsesByConnettore.get(i)) {
					assertEquals(500,response.getResultHTTPOperation());
				}
				
			} else {
				for (var response : responsesByConnettore.get(i)) {
					String connettoreRisposta = response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
					assertEquals(connettoreRichiesta, connettoreRisposta);
				}
			}
		}
	}
		
		
}
