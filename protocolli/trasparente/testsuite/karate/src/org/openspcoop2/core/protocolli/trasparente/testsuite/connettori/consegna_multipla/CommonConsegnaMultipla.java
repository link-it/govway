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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * 
 * @author froggo
 *
 */
public class CommonConsegnaMultipla {
	
	final static String PROTOCOL_NAME = "trasparente";

	final static int ESITO_CONSEGNA_MULTIPLA = 38;

	final static int ESITO_CONSEGNA_MULTIPLA_COMPLETATA = 39;

	final static int ESITO_CONSEGNA_MULTIPLA_FALLITA = 40;

	final static int ESITO_CONSEGNA_MULTIPLA_IN_CORSO = 48;

	final static int intervalloControllo = Integer
			.valueOf(System.getProperty("connettori.consegna_multipla.next_messages.intervallo_controllo")) * 1000;
	final static int scheduleNewAfter = Integer
			.valueOf(System.getProperty("connettori.consegna_multipla.next_messages.schedule_new_after")) * 1000;
	final static int intervalloControlloFallite = Integer.valueOf(
			System.getProperty("connettori.consegna_multipla.next_messages.consegna_fallita.intervallo_controllo"))
			* 1000;
	final static int intervalloMinimoRiconsegna = Integer.valueOf(System
			.getProperty("connettori.consegna_multipla.next_messages.consegna_fallita.intervallo_minimo_riconsegna"))
			* 1000;
	final static Path connettoriFilePath = Paths
			.get(System.getProperty("connettori.consegna_multipla.connettore_file.path"));

	public static <T> Set<T> setIntersection(Set<T> lhs, Set<T> rhs) {
		Set<T> ret = new HashSet<T>();
		
		for(var el : lhs) {
			if (rhs.contains(el)) {
				ret.add(el);
			}
		}
				
		return ret;
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
				executor.execute( () -> responses.get(request).add(Utils.makeRequest(request.request)));
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

	/**
	 * Controlla che la transazione principale abbia un determinato esito e determinate consegne da fare
	 */
	public static void checkStatoConsegna(HttpResponse response, int esito, int consegne_multiple_rimanenti) {
		String query = "select count(*) from transazioni where id=? and esito = ? and esito_sincrono = 0 and consegne_multiple = ?";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class,  id_transazione, esito, consegne_multiple_rimanenti);
		assertEquals(Integer.valueOf(1), count);
	}
	

	static void checkPresaInConsegna(HttpResponse response) {
		/**
		 * esito | 38 		CONSEGNA MULTIPLA
		 * esito_sincrono | 0			Transazione gestita con successo
		 * consegne_multiple | 4	 Sono il numero di consegne multiple rimanenti
		 */
		
		String query = "select count(*) from transazioni where id=? and esito = 38 and esito_sincrono = 0 and consegne_multiple = 4";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class,  id_transazione);
		assertEquals(Integer.valueOf(1), count);
	}


	public static void checkConsegnaCompletata(HttpResponse response, int esito) {
		String query = "select count(*) from transazioni where id=? and esito = ? and esito_sincrono = 0 and consegne_multiple = 0";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		Integer count =ConfigLoader.getDbUtils().readValue(query, Integer.class,  id_transazione, esito);
		assertEquals(Integer.valueOf(1), count);
	}

	public static void checkSchedulingConnettoreInCorso(HttpResponse response, String connettore) {
		String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = false and numero_tentativi >=1";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
	
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore);
		assertEquals(Integer.valueOf(1), count);
	}


	public  static void checkSchedulingConnettoreIniziato(HttpResponse response, String connettore) {
		String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = false and numero_tentativi = 0";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
	
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore);
		assertEquals(Integer.valueOf(1), count);
	}

	/**
	 * Il numero tentativi è a uno perchè per i connettori per cui si completa la richiesta, la si completa
	 * sempre al primo tentativo.
	 * 
	 */
	public static void checkSchedulingConnettoreCompletato(HttpResponse response, String connettore) {
			String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = true and numero_tentativi = 1";
			String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
	
			Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore);
			assertEquals(Integer.valueOf(1), count);				
	}

	public static  int getNumeroTentativiSchedulingConnettore(HttpResponse response, String connettore) {
		String query = "select numero_tentativi from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = false";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		
		return ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore);
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
		for ( var connettore : requestAndExpectation.connettoriFallimento) {
			checkSchedulingConnettoreInCorso(response, connettore);
		}
		
		for (var connettore : requestAndExpectation.connettoriSuccesso) {
			checkSchedulingConnettoreCompletato(response, connettore);
		}
		
		// TODO: Devo controllare sotto transazioni_sa che non siano stati raggiunti o schedulati connettori in più
		// oltre a quelli specificati in connettoriFallimento e connettoriSuccesso, in questo modo sono a posto anche
		// per la consegna condizionale
		
		checkStatoConsegna(response,requestAndExpectation.esito, requestAndExpectation.connettoriFallimento.size());
	}

	public static void checkRequestExpectationsFinal(RequestAndExpectations requestAndExpectation, HttpResponse response) {
		for ( var connettore : requestAndExpectation.connettoriFallimento) {
			assertTrue( getNumeroTentativiSchedulingConnettore(response, connettore) >= 2);
		}
		
		for (var connettore : requestAndExpectation.connettoriSuccesso) {
			checkSchedulingConnettoreCompletato(response, connettore);
		}
		
		// TODO: Devo controllare sotto transazioni_sa che non siano stati raggiunti o schedulati connettori in più
		// oltre a quelli specificati in connettoriFallimento e connettoriSuccesso, in questo modo sono a posto anche
		// per la consegna condizionale
		
		checkStatoConsegna(response,requestAndExpectation.esito, requestAndExpectation.connettoriFallimento.size());
	}

	/**
	 * Costruisce la richiesta e le condizioni attese sull'esito della richiesta.
	 * 
	 * Se la richiesta ha uno status != 2xx e viene accettata da un connettore, lo stato diventa ESITO_CONSEGNA_MULTIPLA_FALLITA
	 * 
	 * 
	 * Nel caso 
	 * @return
	 */
	public static RequestAndExpectations buildRequestAndExpectations(String erogazione, int statusCode, Set<String> connettoriOk, String soapContentType) {
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		
		var connettoriErrore = new HashSet<>(Common.setConnettoriAbilitati);
		connettoriErrore.removeAll(connettoriOk);
		int esito;
		
		// Se qualsiasi connettore accetta uno status code non OK, la consegna multipla va segnata come fallita.
		if ( ( statusCode < 200 || statusCode > 299) && !connettoriOk.isEmpty()) {
			esito = ESITO_CONSEGNA_MULTIPLA_FALLITA;
		} else {
			esito =  connettoriErrore.isEmpty() ? ESITO_CONSEGNA_MULTIPLA_COMPLETATA : ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
		}
		
		return new RequestAndExpectations(request, connettoriOk, connettoriErrore, esito);
	}
	
	
	public  static void checkPresaInConsegna(List<HttpResponse> responses) {
		for (var r : responses) {
			checkPresaInConsegna(r);
		}
	}

	public  static void checkSchedulingConnettoriCompletato(HttpResponse response, Set<String> connettori) {
		for (var connettore : connettori) {
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, connettore);			
		}		
	}

	public static void checkSchedulingConnettoreIniziato(HttpResponse response, Set<String> connettori) {	
		for (var connettore : connettori) {
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(response, connettore);
		}		
	}
	
	public static void checkSchedulingConnettoreIniziato(List<HttpResponse> responses, Set<String> connettori) {
		for (var r : responses) {
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);
		}
	}

	
	
}
