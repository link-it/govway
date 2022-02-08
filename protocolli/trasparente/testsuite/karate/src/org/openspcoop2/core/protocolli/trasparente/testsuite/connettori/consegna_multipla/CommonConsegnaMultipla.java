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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_3;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.checkAll200;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * 
 * @author froggo
 *
 */
public class CommonConsegnaMultipla {
	
	public final static EsitiProperties esitiProperties = getEsitiProperties();
	
	public final static String PROTOCOL_NAME = "trasparente";

	public final static int ESITO_CONSEGNA_MULTIPLA = 38;

	public final static int ESITO_CONSEGNA_MULTIPLA_COMPLETATA = 39;

	public final static int ESITO_CONSEGNA_MULTIPLA_FALLITA = 40;

	public final static int ESITO_CONSEGNA_MULTIPLA_IN_CORSO = 48;

	public final static int intervalloControllo = Integer
			.valueOf(System.getProperty("connettori.consegna_multipla.next_messages.intervallo_controllo")) * 1000;
	
	public final static int scheduleNewAfter = Integer
			.valueOf(System.getProperty("connettori.consegna_multipla.next_messages.schedule_new_after")) * 1000;
	
	public final static int intervalloControlloFallite = Integer.valueOf(
			System.getProperty("connettori.consegna_multipla.next_messages.consegna_fallita.intervallo_controllo"))
			* 1000;
	
	public final static int intervalloMinimoRiconsegna = Integer.valueOf(System
			.getProperty("connettori.consegna_multipla.next_messages.consegna_fallita.intervallo_minimo_riconsegna"))
			* 1000;
	
	public final static Path connettoriFilePath = Paths
			.get(System.getProperty("connettori.consegna_multipla.connettore_file.path"));
	
	
	final static Map<Integer, Set<String>> statusCode2xxVsConnettori  = Map
			.of(200, Set.of(CONNETTORE_1,CONNETTORE_2),
				  201, Set.of(CONNETTORE_1, CONNETTORE_3),
				  202, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
				  206, Set.of(CONNETTORE_1),
				  299, Set.of(CONNETTORE_1));
	
	final static Map<Integer, Set<String>> statusCode4xxVsConnettori  = Map
			.of(400, Set.of(CONNETTORE_1,CONNETTORE_2),
				  401, Set.of(CONNETTORE_1, CONNETTORE_3),
				  402, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
				  406, Set.of(CONNETTORE_1),
				  499, Set.of(CONNETTORE_1));
	
	final static Map<Integer, Set<String>> statusCode5xxVsConnettori  = Map
			.of(500, Set.of(CONNETTORE_1,CONNETTORE_2),
				  501, Set.of(CONNETTORE_1, CONNETTORE_3),
				  502, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
				  506, Set.of(CONNETTORE_1),
			  	  599, Set.of(CONNETTORE_1));
	
	public final static Map<Integer,Set<String>> statusCodeVsConnettori = new HashMap<>();
	static {
		statusCodeVsConnettori.putAll(statusCode2xxVsConnettori);
		statusCodeVsConnettori.putAll(statusCode4xxVsConnettori);
		statusCodeVsConnettori.putAll(statusCode5xxVsConnettori);
	}

	public static <T> Set<T> setIntersection(Set<T> lhs, Set<T> rhs) {
		Set<T> ret = new HashSet<T>();
		
		for(var el : lhs) {
			if (rhs.contains(el)) {
				ret.add(el);
			}
		}
				
		return ret;
	}
	
	public static <T> Set<T> setSum(Set<T> lhs, Set<T> rhs) {
		Set<T> ret = new HashSet<T>(lhs);
		ret.addAll(rhs);
		return ret;
	}
	
	
	public static <T> Set<T> setDifference(Set<T> lhs, Set<T> rhs) {
		Set<T> ret = new HashSet<T>(lhs);
		ret.removeAll(rhs);
		return ret;
	}
	
	
	private static EsitiProperties getEsitiProperties() {
		try {
			return EsitiProperties.getInstance(ConfigLoader.getLoggerCore(), CommonConsegnaMultipla.PROTOCOL_NAME);
		} catch (ProtocolException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	

	/**
	 * Esegue requests_per_batch richieste per ogni richiesta della lista.
	 * Restituisce la map delle risposte indicizzate per richiesta.
	 * 
	 */
	public static Map<RequestAndExpectations, List<HttpResponse>> makeRequestsByKind(List<RequestAndExpectations> requests, int requests_per_batch) {
		Map<RequestAndExpectations,  List<HttpResponse>> responses = new HashMap<>();
		
		int nThreads = Common.sogliaRichiesteSimultanee; 
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
	
		for (var request : requests) {			
			responses.put(request, new Vector<>());
			
			for (int i = 0; i<requests_per_batch; i++) {
				executor.execute( () -> {
					responses.get(request).add(Utils.makeRequest(request.request));
				});
			}			
		}
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			ConfigLoader.getLoggerCore().error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		return responses;
				
	}

	
	
	public  static void checkPresaInConsegna(List<HttpResponse> responses) {
		for (var r : responses) {
			checkPresaInConsegna(r);
		}
	}

	public static void checkPresaInConsegna(HttpResponse response) {
		checkPresaInConsegna(response, 4);
	}
	
	public static void checkPresaInConsegna(HttpResponse response, int numConnettori) {
		checkStatoConsegna(response, ESITO_CONSEGNA_MULTIPLA, numConnettori);
	}
	
	public static void checkConsegnaCompletata(HttpResponse response) {
		checkStatoConsegna(response, ESITO_CONSEGNA_MULTIPLA_COMPLETATA, 0);		
	}
	
    
	public static void checkStatoConsegna(List<HttpResponse> responses, int esito, int consegneMultipleRimanenti) {

		var id_transazioni = responses.stream()
				.map( response -> response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE));
		var sargs = Stream.of(esito, consegneMultipleRimanenti);
		
		Object[] args = Stream.concat(id_transazioni,sargs).toArray();
		
		// Questo mi crea una stringa del tipo ( ?, ?, ? )
		String IN_CLAUSE = "( " + String.join(",", Collections.nCopies(responses.size(), "?")) + ") ";
		
		String query = "select count(*) from transazioni where id in "+IN_CLAUSE+" and esito = ? and esito_sincrono = 0 and consegne_multiple = ?";
		//String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		ConfigLoader.getLoggerCore().info("Checking stato consegna for transazioni:  " + IN_CLAUSE + " AND esito = " + esito + " AND consegne-rimanenti: " + consegneMultipleRimanenti);
		Integer count = ConfigLoader.getDbUtils().readValueArray(query, Integer.class,  args);
		assertEquals(Integer.valueOf(responses.size()), count);
	}

	/**
	 * Controlla che la transazione principale abbia un determinato esito e determinate consegne da fare
	 */
	public static void checkStatoConsegna(HttpResponse response, int esito, int consegneMultipleRimanenti) {
		String query = "select count(*) from transazioni where id=? and esito = ? and esito_sincrono = 0 and consegne_multiple = ?";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		ConfigLoader.getLoggerCore().info("Checking stato consegna for transazione:  " + id_transazione + " AND esito = " + esito + " AND consegne-rimanenti: " + consegneMultipleRimanenti);
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class,  id_transazione, esito, consegneMultipleRimanenti);
		
		// Uncoment for debug
		/*String query2 = "select esito, esito_sincrono, consegne_multiple from transazioni where id = ?";
		List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione);
		for (var v : letto) {
			ConfigLoader.getLoggerCore().info(v.toString());
		}*/
		
		assertEquals(Integer.valueOf(1), count);
	}

	public static void checkSchedulingConnettoreInCorso(HttpResponse response, String connettore) {
		String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = ? and numero_tentativi >=1";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
	
		ConfigLoader.getLoggerCore().info("Checking scheduling connettore in corso for transazione:  " + id_transazione + " AND connettore = " + connettore);
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, false);
		assertEquals(Integer.valueOf(1), count);
	}


	public  static void checkSchedulingConnettoreIniziato(HttpResponse response, String connettore) {
		String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = ? and numero_tentativi = 0";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		ConfigLoader.getLoggerCore().info("Checking scheduling connettore iniziato for transazione:  " + id_transazione + " AND connettore = " + connettore);
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, false);
		assertEquals(Integer.valueOf(1), count);
	}

	/**
	 * Il numero tentativi è a uno perchè per i connettori per cui si completa la richiesta, la si completa
	 * sempre al primo tentativo.
	 * 
	 */
	public static void checkSchedulingConnettoreCompletato(HttpResponse response, String connettore) {
			String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = ? and numero_tentativi = 1";
			String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
			ConfigLoader.getLoggerCore().info("Checking scheduling connettore completato for transazione:  " + id_transazione + " AND connettore = " + connettore);
			Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, true);
			assertEquals(Integer.valueOf(1), count);				
	}

	public static  int getNumeroTentativiSchedulingConnettore(HttpResponse response, String connettore) {
		String query = "select numero_tentativi from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = ?";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		
		return ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, false);
	}

	/**
	 * Controlla che la richiesta/rispsta soddisfi le condizioni attese, per i connettori falliti
	 * controllo che lo scheduling sia ancora in corso e per i connettori con successo
	 * controllo che lo scheduling sia completato.
	 * 
	 * @param requestAndExpectation
	 * @param response
	 */
	public static void checkRequestExpectations(RequestAndExpectations requestAndExpectation, HttpResponse response) {
		for (var connettore : requestAndExpectation.connettoriFallimento) {
			checkSchedulingConnettoreInCorso(response, connettore);
		}
		
		for (var connettore : requestAndExpectation.connettoriSuccesso) {
			checkSchedulingConnettoreCompletato(response, connettore);
		}
		
		Set<String> wholeConnettori = setSum(requestAndExpectation.connettoriSuccesso,requestAndExpectation.connettoriFallimento);
		checkConnettoriRaggiuntiEsclusivamente(response, wholeConnettori);
		
		checkStatoConsegna(response,requestAndExpectation.esito, requestAndExpectation.connettoriFallimento.size());
	}

	public static void checkRequestExpectationsFinal(RequestAndExpectations requestAndExpectation, HttpResponse response) {
		for ( var connettore : requestAndExpectation.connettoriFallimento) {
			assertTrue( getNumeroTentativiSchedulingConnettore(response, connettore) >= 2);
		}
		
		for (var connettore : requestAndExpectation.connettoriSuccesso) {
			checkSchedulingConnettoreCompletato(response, connettore);
		}
		
		Set<String> wholeConnettori = setSum(requestAndExpectation.connettoriSuccesso,requestAndExpectation.connettoriFallimento);
		checkConnettoriRaggiuntiEsclusivamente(response, wholeConnettori);
		
		checkStatoConsegna(response,requestAndExpectation.esito, requestAndExpectation.connettoriFallimento.size());
	}


	/**
	 * Costruisce la richiesta e le condizioni attese sull'esito della richiesta.
	 * 
	 * Se la richiesta ha uno status != 2xx e viene accettata da un connettore, lo stato diventa ESITO_CONSEGNA_MULTIPLA_FALLITA
	 * 
	 */
	public static RequestAndExpectations buildRequestAndExpectations(String erogazione, int statusCode, Set<String> connettoriOk, Set<String> connettoriErrore, String soapContentType) {
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		
		int esito;
		
		// Se qualsiasi connettore accetta uno status code non OK, la consegna multipla va segnata come fallita.
		if ( ( statusCode < 200 || statusCode > 299) && !connettoriOk.isEmpty()) {
			esito = ESITO_CONSEGNA_MULTIPLA_FALLITA;
		} else {
			esito =  connettoriErrore.isEmpty() ? ESITO_CONSEGNA_MULTIPLA_COMPLETATA : ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
		}
		
		return new RequestAndExpectations(request, connettoriOk, connettoriErrore, esito);
	}
	
	


	public  static void checkSchedulingConnettoriCompletato(HttpResponse response, Set<String> connettori) {
		for (var connettore : connettori) {
			checkSchedulingConnettoreCompletato(response, connettore);			
		}
		
		// Controllo che lo scheduling sia stato effettuato solo sui connettori indicati
		checkConnettoriRaggiuntiEsclusivamente(response, connettori);
	}

	public static void checkSchedulingConnettoreIniziato(HttpResponse response, Set<String> connettori) {	
		for (var connettore : connettori) {
			checkSchedulingConnettoreIniziato(response, connettore);
		}
		
		// Controllo che lo scheduling sia stato effettuato solo sui connettori indicati
		checkConnettoriRaggiuntiEsclusivamente(response, connettori);
	}


	private static void checkConnettoriRaggiuntiEsclusivamente(HttpResponse response, Set<String> connettori) {
		String query = "select connettore_nome  from transazioni_sa where id_transazione=?";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
	
		List<Map<String, Object>> connettoriRaggiunti = ConfigLoader.getDbUtils().readRows(query, id_transazione);
		for(var row : connettoriRaggiunti) {
			String connettore = (String) row.get("connettore_nome");
			assertTrue(connettori.contains(connettore));
		}
		
		for (var connettore : connettori) {
			var nomeRaggiunti = connettoriRaggiunti.stream().map( row -> row.get("connettore_nome")).collect(Collectors.toList());
			assertTrue(nomeRaggiunti.contains(connettore));
		}
	}
	
	public static void checkSchedulingConnettoreIniziato(List<HttpResponse> responses, Set<String> connettori) {
		for (var r : responses) {
			checkSchedulingConnettoreIniziato(r, connettori);
		}
	}

	public static RequestAndExpectations buildRequestAndExpectationFiltered(HttpRequest request, int statusCode, Set<String> connettoriSuccesso, Set<String> connettoriFiltrati) {
		connettoriFiltrati = new HashSet<>(connettoriFiltrati);
		connettoriFiltrati.remove(Common.CONNETTORE_DISABILITATO);
		connettoriSuccesso = setIntersection(connettoriSuccesso, connettoriFiltrati);
		connettoriFiltrati.removeAll(connettoriSuccesso);
		Set<String> connettoriFallimento = connettoriFiltrati;
	
		int esito;
		
		if ( ( statusCode < 200 || statusCode > 299) && !connettoriSuccesso.isEmpty()) {
			esito = ESITO_CONSEGNA_MULTIPLA_FALLITA;
		} else {
			esito =  connettoriFallimento.isEmpty() ? ESITO_CONSEGNA_MULTIPLA_COMPLETATA : ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
		}
		
		return new RequestAndExpectations(request, connettoriSuccesso, connettoriFallimento, esito);
	}

	public static void checkResponses(Map<RequestAndExpectations, List<HttpResponse>> responsesByKind) {
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia 
		// su tutti i connettori abilitati e filtrati dalla consegna condizionale
		assertTrue(!responsesByKind.isEmpty());
		
		// Controllo che le richieste siano state consegnate e le notifiche schedulate
		for (var requestAndExpectation : responsesByKind.keySet() ) {
			var responses = responsesByKind.get(requestAndExpectation);
			
			if (requestAndExpectation.esitoPrincipale == 200) {
				assertTrue(!responses.isEmpty());
				checkAll200(responses);
				
				// Deve essere la fusione dei connettoriOk e i connettoriErrore\conettoriScheduling Disabilitato
				Set<String> connettoriCoinvoltii = setSum(requestAndExpectation.connettoriSuccesso, requestAndExpectation.connettoriFallimento);
				
				checkStatoConsegna(responses, ESITO_CONSEGNA_MULTIPLA, connettoriCoinvoltii.size());
		
				checkSchedulingConnettoreIniziato(responses, connettoriCoinvoltii);
			} else if (requestAndExpectation.esitoPrincipale == 500) {
				Common.checkResponsesStatus(responses, 500);
			} else {
				throw new RuntimeException("Esito principale atteso non programmato: " + requestAndExpectation.esitoPrincipale);
			}
		}
		
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*intervalloControllo);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			if (requestAndExpectation.esitoPrincipale == 200) {
				for (var response : responsesByKind.get(requestAndExpectation)) {
					checkRequestExpectations(requestAndExpectation, response);
				}
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		org.openspcoop2.utils.Utilities.sleep(2*intervalloControlloFallite);
		for (var requestAndExpectation : responsesByKind.keySet()) {
			if (requestAndExpectation.esitoPrincipale == 200) {
				for (var response : responsesByKind.get(requestAndExpectation)) {
					checkRequestExpectationsFinal(requestAndExpectation, response);
				}
			}
		}
	}

	
	
}
