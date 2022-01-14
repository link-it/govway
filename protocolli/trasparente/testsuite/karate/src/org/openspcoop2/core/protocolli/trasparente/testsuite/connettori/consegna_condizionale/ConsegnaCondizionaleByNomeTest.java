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
import static org.junit.Assert.assertTrue;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.CONNETTORE_0;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.CONNETTORE_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common.HEADER_ID_CONDIZIONE;

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
 * ConsegnaCondizionaleByNome
 * 
 * @author Francesco Scarlato (scarlato@link.it)
 * 
 *	Connettore0, Connettore1, Connettore2, Connettore3, ConnettoreDisabilitato, ConnettoreRotto
 *	
 *	Testo anche che al connettore disabilitato non venga instradato nulla, ogni politica ha un connettore disabilitato.
 *
 *  Invio N batch (per ora 1) di 15 richieste parallele, tre per connettore. Verifico che ciascuna richiesta
 *  raggiunga il connettore deisderato. In questo modo simulo un pattern di ri richieste verosimile.
 *  
 *   TODO: Provare nei test caratteri unicode strani, tipo le emoticon 😛
 *   TODO: Aggiungere nei test il connettore disabilitato e rilevare il 400
 *   
 *   TODO: Per ogni test aggiungi un set di richieste per cui fallisce l'identificazione, 
 *   			un set di richieste per cui il connettore non viene trovato
 *   			e un set di richieste che vanno sul connettore disabilitato
 *   
 *   TODO: Test XForwardedFor con più headers appartenenti alla stessa classe, ne deve essere scelto uno a caso
 *   
 *   TODO: Il test identificazioneFallita, ripetilo anche sulle singole erogazioni oltre che sulle regole
 *   
 *   Non vengono fatti test di case sensitivity sui valori in quanto i valori di parametri query, headers http
 *   e contenuto della richiesta sono tutti case sensitive.
 * 
 */


public class ConsegnaCondizionaleByNomeTest extends ConfigLoader {
	
	@Test
	public void headerHttp() {
		final String erogazione = "ConsegnaCondizionaleHeaderHttpByNome";
		
		HttpRequest request0 = Common.buildRequest_HeaderHttp(CONNETTORE_0, erogazione);
		HttpRequest request1 = Common.buildRequest_HeaderHttp(CONNETTORE_1, erogazione);
		HttpRequest request2 = Common.buildRequest_HeaderHttp(Common.CONNETTORE_2, erogazione);
		// La terza richiesta specifica due volte lo stesso connettore. deve comunque funzionare
		HttpRequest request3 = Common.buildRequest_HeaderHttp(Common.CONNETTORE_3, erogazione);
		request3.addHeader(HEADER_ID_CONDIZIONE, Common.CONNETTORE_3); 	
		
		HttpRequest requestRotto = Common.buildRequest_HeaderHttp(Common.CONNETTORE_ROTTO, erogazione);

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
		
		HttpRequest request = Common.buildRequest_HeaderHttp(CONNETTORE_1, erogazione);
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
				.map(c -> Common.buildRequest_UrlInvocazione(c,erogazione))
				.collect(Collectors.toList());
							
		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettori(Common.connettoriAbilitati, responsesByConnettore);
	}

	
	@Test
	public void parametroUrl() {
		final String erogazione = "ConsegnaCondizionaleParametroUrlByNome";
		
		var requestsByConnettore = Common.connettoriAbilitati.stream()
				.map(c -> Common.buildRequest_ParametroUrl(c,erogazione))
				.collect(Collectors.toList());
							
		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettori(Common.connettoriAbilitati, responsesByConnettore);		

	}
	
	
	@Test
	public void contenuto() {
		
		final String erogazione = "ConsegnaCondizionaleContenutoByNome";
		
		var requestsByConnettore = Common.connettoriAbilitati.stream()
				.map(c -> Common.buildRequest_Contenuto(c,erogazione))
				.collect(Collectors.toList());
							
		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettori(Common.connettoriAbilitati, responsesByConnettore);		
		
	}
	
	
	@Test
	public void testConnettoreDisabilitato() {
		
		final String erogazione = "ConsegnaCondizionaleContenutoByNome";
		
		List<String> connettori = Arrays.asList( CONNETTORE_0,
				CONNETTORE_1,
				Common.CONNETTORE_2,
				Common.CONNETTORE_3,
				Common.CONNETTORE_DISABILITATO);
		
		var requestsByConnettore = connettori.stream()
				.map(c -> Common.buildRequest_Contenuto(c,erogazione))
				.collect(Collectors.toList());
							
		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
		for(int i=0;i<connettori.size();i++) {
			String connettoreRichiesta = connettori.get(i);
			
			if (Common.CONNETTORE_DISABILITATO.equals(connettoreRichiesta)) {
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
		
		var responses = Utils.makeParallelRequests(request, 15);
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
		Vector<Vector<HttpResponse>> responsesByConnettore = new Vector<>(Common.connettoriAbilitati.size());

		// Inizializzo le risposte, alla richiesta di indice i verrà assegnato il connettore di indice i
		// responsesByConnettore.get(i) restituisce le risposte relative all'i-esimo connettore
		Common.connettoriAbilitati.forEach( c -> responsesByConnettore.add(new Vector<>()));

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
		
		matchResponsesWithConnettori(Common.connettoriAbilitati, responsesByConnettore);
	}
	
	
	@Test
	public void template() {
		
		// Il template pesca il valore del parametro query govway-testsuite-id_connettore_request,
		// riuso quanto fatto per il test parametro query
		final String erogazione = "ConsegnaCondizionaleTemplateByNome";
		
		var requestsByConnettore = Common.connettoriAbilitati.stream()
				.map(c -> Common.buildRequest_Template(c, erogazione))
				.collect(Collectors.toList());
							
		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettori(Common.connettoriAbilitati, responsesByConnettore);		
		
	}
	
	
	@Test
	public void freemarkerTemplate() {
		// Test uguale a templateByNome
		final String erogazione = "ConsegnaCondizionaleFreemarkerTemplateByNome";
		
		var requestsByConnettore = Common.connettoriAbilitati.stream()
				.map(c -> Common.buildRequest_FreemarkerTemplate(c, erogazione))
				.collect(Collectors.toList());

		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettori(Common.connettoriAbilitati, responsesByConnettore);		
		
	}
	
	
	@Test
	public void velocityTemplate() {
		// Test uguale a templateByNome
		final String erogazione = "ConsegnaCondizionaleVelocityTemplateByNome";
		
		var requestsByConnettore = Common.connettoriAbilitati.stream()
				.map(c -> Common.buildRequest_VelocityTemplate(c, erogazione))
				.collect(Collectors.toList());
							
		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettori(Common.connettoriAbilitati, responsesByConnettore);		
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
		requestForwardedFor.addHeader("X-Forwarded-For", Common.CONNETTORE_3);
		
		HttpRequest requestIdentificazioneStatica = new HttpRequest();
		requestIdentificazioneStatica.setMethod(HttpRequestMethod.GET);
		requestIdentificazioneStatica.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-statica"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX); 
		
		HttpRequest requestIdentificazioneClientIp= new HttpRequest();
		requestIdentificazioneClientIp.setMethod(HttpRequestMethod.GET);
		requestIdentificazioneClientIp.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-client-ip"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		
		
		List<List<Object>> connettoriAndrequests = Arrays.asList(
				Arrays.asList(CONNETTORE_0, Common.buildRequest_HeaderHttp(CONNETTORE_0, erogazione)),
				Arrays.asList(CONNETTORE_1, Common.buildRequest_UrlInvocazione(CONNETTORE_1, erogazione)),
				Arrays.asList(Common.CONNETTORE_2, Common.buildRequest_ParametroUrl(Common.CONNETTORE_2, erogazione)),				
				Arrays.asList(Common.CONNETTORE_3, Common.buildRequest_Contenuto(Common.CONNETTORE_3, erogazione)),				
				Arrays.asList(CONNETTORE_0, Common.buildRequest_Template(CONNETTORE_0, erogazione)),
				Arrays.asList(CONNETTORE_1, Common.buildRequest_FreemarkerTemplate(CONNETTORE_1, erogazione)),
				Arrays.asList(Common.CONNETTORE_2, Common.buildRequest_VelocityTemplate(Common.CONNETTORE_2, erogazione)),
				Arrays.asList(Common.CONNETTORE_3, requestForwardedFor),
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
		var clientIpResponses = responsesByConnettore.lastElement();
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
				.map(c -> Common.buildRequest_HeaderHttp(c, erogazione))
				.collect(Collectors.toList());
							
		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettori(Common.connettoriAbilitati, responsesByConnettore);	
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
				.map(c -> Common.buildRequest_HeaderHttp(c, erogazione))
				.collect(Collectors.toList());
							
		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettori(Common.connettoriAbilitati, responsesByConnettore);	
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
				.map(c -> Common.buildRequest_HeaderHttp(c, erogazione))
				.collect(Collectors.toList());
							
		var responsesByConnettore = ConsegnaCondizionaleByNomeTest.makeBatchedRequests(requestsByConnettore, 3);
		
		matchResponsesWithConnettori(Common.connettoriAbilitati, responsesByConnettore);	
		
	}


	@Test
	public void unicode() throws UtilsException {
		
		// Connettori:
		//	😛😛 => Connettore0
		//  ΛΛ => Connettore1
		
		// TODO: La console consente caratteri unicode nei filtri, ma poi viene dato 400 quando si cerca di usarli
		final String erogazione = "ConsegnaCondizionaleUnicode";
		
/*		HttpRequest requestHeaderHttp = new HttpRequest();
		requestHeaderHttp.setMethod(HttpRequestMethod.GET);
		requestHeaderHttp.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-header-http"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		requestHeaderHttp.addHeader(HEADER_CONDIZIONE, "😛😛");
		
		var response = HttpUtilities.httpInvoke(requestHeaderHttp);
		assertEquals(200, response.getResultHTTPOperation());
		assertEquals(CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));*/
		
		final String content = "{ \"id_connettore_request\": \"😛😛\" }";
		
		HttpRequest requestContenuto = new HttpRequest();
		requestContenuto.setMethod(HttpRequestMethod.POST);
		requestContenuto.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-contenuto"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		requestContenuto.setContentType("application/json");
		requestContenuto.setContent(content.getBytes());
		
		var responseContenuto = HttpUtilities.httpInvoke(requestContenuto);
		assertEquals(200, responseContenuto.getResultHTTPOperation());
		assertEquals(CONNETTORE_0, responseContenuto.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		// La freemarker template la riprovo sullo header per vedere se funge
		HttpRequest requestFreemarkerTemplate = new HttpRequest();
		requestFreemarkerTemplate.setMethod(HttpRequestMethod.GET);
		requestFreemarkerTemplate.setContentType("application/json");	// TODO: Rimuovere dopo il fix
		requestFreemarkerTemplate.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-freemarker-template"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		requestFreemarkerTemplate.addHeader(HEADER_ID_CONDIZIONE, "😛😛");
		
		var response = HttpUtilities.httpInvoke(requestFreemarkerTemplate);
		assertEquals(200, response.getResultHTTPOperation());
		assertEquals(CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));

	}


	/*
	 * Esegue un thread per ogni richiesta e per ogni thread esegue
	 * requests_per_batch richieste
	 * 
	 * Restituisce le risposte raggruppate per richiesta, e.g: il primo vettore di
	 * risposte corrisponde al batch di richieste fatte per la prima richiesta della
	 * lista `requests`
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
			// logCore.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
	
		return ret;
	}


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
					String connettoreRisposta = response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
					assertEquals(connettoreRichiesta, connettoreRisposta);
				}
			//}
		}
	}
		
		
}
