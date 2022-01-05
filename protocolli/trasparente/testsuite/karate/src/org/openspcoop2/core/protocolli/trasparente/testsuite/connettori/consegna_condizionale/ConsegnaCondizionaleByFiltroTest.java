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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * ConsegnaCondizionaleByFiltroTest
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * 
 */
public class ConsegnaCondizionaleByFiltroTest extends ConfigLoader {
	
	// TODO: Se la consegna condizionale identifica più di un connettore, segnala errore.
	//		La console però consente di creare questa configurazione non valida.
	//		Fixare la console impedendo tale configurazione e mettere una proprietà in govway per
	//		disabilitare questo fix, in modo da poterlo testare.
	// TODO: Aggiungere anche qui il controllo sul connettore disabilitato?
	
	
	/* 
	 * I test seguono lo schema per la consegna condizionale by nome, solo che ogni connettore
	 * viene raggiunto da due filtri, invece che solo da uno.
	 * 
	 */	
	
	static Map<String,List<String>> filtriConnettori = Map.of(
			Common.CONNETTORE_0, Arrays.asList("Connettore0-Filtro0", "Connettore0-Filtro1"),
			Common.CONNETTORE_1, Arrays.asList("Connettore1-Filtro0", "Connettore1-Filtro1"),
			Common.CONNETTORE_2, Arrays.asList("Connettore2-Filtro0", "Connettore2-Filtro1"),
			Common.CONNETTORE_3, Arrays.asList("Connettore3-Filtro0", "Connettore3-Filtro1")
		);
			
	
	public Map<String,List<HttpResponse>> makeBatchedRequests(Map<String,List<HttpRequest>> requestsByConnettore, int requests_per_batch) {
		var responsesByConnettore = new ConcurrentHashMap<String,List<HttpResponse>>();
		
		int nThreads = requestsByConnettore.keySet().size();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);

		for (var connettore : requestsByConnettore.keySet()) {			
			responsesByConnettore.put(connettore, new ArrayList<>());
			
			executor.execute(() -> {
				for(var request : requestsByConnettore.get(connettore)) {
					responsesByConnettore.get(connettore).addAll(Utils.makeSequentialRequests(request, 5));
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
		return responsesByConnettore;
	}
	
	
	@Test
	public void headerHttp() {
		
		final String erogazione = "ConsegnaCondizionaleHeaderHttpByFiltro";
		
		// Da qui in poi Il pattern per i test della consegna condizionale è lo steso per tutti.
		// Mando tanti thread in parallelo quanti sono i connettori da raggiungere, con ciascun
		// thread che raggiunge sempre lo stesso connettore.
		// Quindi Controllo che nelle risposte di quel thread sia stato raggiunto sempre lo stesso connettore.
		
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		Common.connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(connettore, new ArrayList<>());
			
			var filtri = filtriConnettori.get(connettore);
			for(int j=0; j<filtri.size(); j++) {
				var request = ConsegnaCondizionaleByNomeTest.buildRequest_HeaderHttpByNome(filtri.get(j), erogazione);
				requestsByConnettore.get(connettore).add(request);
			}
		});
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		
		matchResponsesByConnettore(responsesByConnettore);
	}
	
	
	@Test
	public void urlInvocazione() {

		final String erogazione = "ConsegnaCondizionaleUrlInvocazioneByFiltro";
		
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		Common.connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(connettore, new ArrayList<>());
			
			var filtri = filtriConnettori.get(connettore);
			for(int j=0; j<filtri.size(); j++) {
				var request = ConsegnaCondizionaleByNomeTest.buildRequest_UrlInvocazioneByNome(filtri.get(j), erogazione);
				requestsByConnettore.get(connettore).add(request);
			}
		});
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		
		matchResponsesByConnettore(responsesByConnettore);
	}
	
	
	@Test
	public void parametroUrl() {
		
		final String erogazione = "ConsegnaCondizionaleParametroUrlByFiltro";
		
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		Common.connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(connettore, new ArrayList<>());
			
			var filtri = filtriConnettori.get(connettore);
			for(int j=0; j<filtri.size(); j++) {
				var request = ConsegnaCondizionaleByNomeTest.buildRequest_ParametroUrlByNome(filtri.get(j), erogazione);
				requestsByConnettore.get(connettore).add(request);
			}
		});
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		
		matchResponsesByConnettore(responsesByConnettore);
	}
	
	
	@Test
	public void contenuto() {
		
		final String erogazione = "ConsegnaCondizionaleContenutoByFiltro";
		
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		Common.connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(connettore, new ArrayList<>());
			
			var filtri = filtriConnettori.get(connettore);
			for(int j=0; j<filtri.size(); j++) {
				var request = ConsegnaCondizionaleByNomeTest.buildRequest_ContenutoByNome(filtri.get(j), erogazione);
				requestsByConnettore.get(connettore).add(request);
			}
		});
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		
		matchResponsesByConnettore(responsesByConnettore);
	}
	
	
	
	@Test
	public void template() {
		
		final String erogazione = "ConsegnaCondizionaleTemplateByFiltro";
		
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		Common.connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(connettore, new ArrayList<>());
			
			var filtri = filtriConnettori.get(connettore);
			for(int j=0; j<filtri.size(); j++) {
				var request = ConsegnaCondizionaleByNomeTest.buildRequest_TemplateByNome(filtri.get(j), erogazione);
				requestsByConnettore.get(connettore).add(request);
			}
		});
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		
		matchResponsesByConnettore(responsesByConnettore);
	}
	
	
	
	@Test
	public void freemarkerTemplate() {
		
		final String erogazione = "ConsegnaCondizionaleFreemarkerTemplateByFiltro";
		
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		Common.connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(connettore, new ArrayList<>());
			
			var filtri = filtriConnettori.get(connettore);
			for(int j=0; j<filtri.size(); j++) {
				var request = ConsegnaCondizionaleByNomeTest.buildRequest_FreemarkerTemplateByNome(filtri.get(j), erogazione);
				requestsByConnettore.get(connettore).add(request);
			}
		});
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		
		matchResponsesByConnettore(responsesByConnettore);
	}

	
	
	@Test
	public void velocityTemplate() {
		
		final String erogazione = "ConsegnaCondizionaleVelocityTemplateByFiltro";
		
		var requestsByConnettore = new HashMap<String,List<HttpRequest>>();
		Common.connettoriAbilitati.forEach( connettore -> {
			requestsByConnettore.put(connettore, new ArrayList<>());
			
			var filtri = filtriConnettori.get(connettore);
			for(int j=0; j<filtri.size(); j++) {
				var request = ConsegnaCondizionaleByNomeTest.buildRequest_VelocityTemplateByNome(filtri.get(j), erogazione);
				requestsByConnettore.get(connettore).add(request);
			}
		});
		
		var responsesByConnettore = makeBatchedRequests(requestsByConnettore,5);
		
		matchResponsesByConnettore(responsesByConnettore);
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
		
		var responses = Utils.makeParallelRequests(request, 15);
		for (var resp : responses) {
			assertEquals(200, resp.getResultHTTPOperation());
			assertEquals(Common.CONNETTORE_0, resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));			
		}	
	}
	
	
	@Test
	public void XForwardedFor() throws UtilsException {
		// TODO
	}

	
	@Test
	public void regole() {
		
		final String erogazione = "ConsegnaCondizionaleRegoleByFiltro";
	}
	
	@Test
	public void identificazioneFallita() throws UtilsException {
		// Per ogni regola per cui è possibile farlo, faccio fallire l'identificazione controllando che non avvengano 
		// dei 500 dovuti a null pointer exceptions, ma solo 400
	}



	private void matchResponsesByConnettore(Map<String, List<HttpResponse>> responsesByConnettore) {
		for (String connettore : responsesByConnettore.keySet()) {
			var responses = responsesByConnettore.get(connettore);
			for (var resp: responses) {
				assertEquals(200,resp.getResultHTTPOperation());
				assertEquals(connettore, resp.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
			}
		}
	}

}
