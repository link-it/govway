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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectations.TipoFault;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilsException;

/**
 * 
 * @author Francesco Scarlato
 *
 */
public class CommonConsegnaMultipla {
	
	public final static EsitiProperties esitiProperties = getEsitiProperties();
	
	public final static String PROTOCOL_NAME = "trasparente";

	public final static int ESITO_CONSEGNA_MULTIPLA = 38;

	public final static int ESITO_CONSEGNA_MULTIPLA_COMPLETATA = 39;

	public final static int ESITO_CONSEGNA_MULTIPLA_FALLITA = 40;

	public final static int ESITO_CONSEGNA_MULTIPLA_IN_CORSO = 48;
	
	public final static int ESITO_ERRORE_PROCESSAMENTO_PDD_4XX = 4;
	
	public final static int ESITO_3XX = 28;
	public final static int ESITO_4XX = 29;
	public final static int ESITO_5XX = 30;
	public final static int ESITO_OK = 0;
	public final static int ESITO_ERRORE_INVOCAZIONE = 10;
	public final static int ESITO_ERRORE_APPLICATIVO = 2;
	public final static int ESITO_OK_PRESENZA_ANOMALIE = 12;
	
	public final static String FAULT_REST = "{\"type\":\"https://httpstatuses.com/500\",\"title\":\"Internal Server Error\",\"detail\":\"Problem ritornato dalla servlet di trace, esempio di OpenSPCoop\"}";
	public final static String FORMATO_FAULT_REST = "JSON";
	
	public final static String FAULT_SOAP1_1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault><faultcode xmlns:ns0=\"http://www.openspcoop2.org/example\">ns0:Server.OpenSPCoo"
			+ "pExampleFault</faultcode><faultstring>Fault ritornato dalla servlet di trace, esempio di OpenSPCoop</faultstring><faultactor>OpenSPCoopTrace</faultactor></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
	
	public final static String FAULT_SOAP1_2 = "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"><env:Header/><env:Body><env:Fault><env:Code><env:Value>env:Receiver</env:Value><env:Subcode><env:Value xmlns:ns1=\"http://www.openspcoop2.or"
			+ "g/example\">ns1:Server.OpenSPCoopExampleFault</env:Value></env:Subcode></env:Code><env:Reason><env:Text xml:lang=\"en-US\">Fault ritornato dalla servlet di trace, esempio di OpenSPCoop</env:Text></env:Reason><env:Role>OpenSPCoopTrace</env:Ro"
			+ "le></env:Fault></env:Body></env:Envelope>";
	
	public final static String FORMATO_FAULT_SOAP1_1 = "SOAP_11";
	public final static String FORMATO_FAULT_SOAP1_2 = "SOAP_12";

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
	
	
	public final static Map<Integer, Set<String>> statusCode2xxVsConnettori  = Map
			.of(200, Set.of(CONNETTORE_1,CONNETTORE_2),
				  201, Set.of(CONNETTORE_1, CONNETTORE_3),
				  202, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
				  206, Set.of(CONNETTORE_1),
				  299, Set.of(CONNETTORE_1));
	
	
	public final static Map<Integer, Set<String>> statusCode3xxVsConnettori  = Map
			.of(300, Set.of(CONNETTORE_1,CONNETTORE_2),
				  301, Set.of(CONNETTORE_1, CONNETTORE_3),
				  302, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
				  306, Set.of(CONNETTORE_1),
				  399, Set.of(CONNETTORE_1));
	
	public final static Map<Integer, Set<String>> statusCode4xxVsConnettori  = Map
			.of(400, Set.of(CONNETTORE_1,CONNETTORE_2),
				  401, Set.of(CONNETTORE_1, CONNETTORE_3),
				  402, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
				  406, Set.of(CONNETTORE_1),
				  499, Set.of(CONNETTORE_1));
	
	public final static Map<Integer, Set<String>> statusCode5xxVsConnettori  = Map
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
	
	public final static Map<Integer,Set<String>> statusCodeRestVsConnettori = new HashMap<>();
	static {
		statusCodeRestVsConnettori.putAll(statusCode2xxVsConnettori);
		statusCodeRestVsConnettori.putAll(statusCode3xxVsConnettori);
		statusCodeRestVsConnettori.putAll(statusCode4xxVsConnettori);
		statusCodeRestVsConnettori.putAll(statusCode5xxVsConnettori);
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
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads-2);
	
		for (var request : requests) {			
			responses.put(request, new Vector<>());
			
			for (int i = 0; i<requests_per_batch; i++) {
				executor.execute( () -> {
					responses.get(request).add(Utils.makeRequest(request.request));
					org.openspcoop2.utils.Utilities.sleep(50);
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

	
	public  static void checkPresaInConsegna(List<HttpResponse> responses, int numConnettori) {
		for (var r : responses) {
			checkPresaInConsegna(r, numConnettori);
		}
	}
	
	
	/**
	 * Qui controlliamo anche l'esito sincrono, è per la consegna con notifiche.
	 */
	public  static void checkPresaInConsegnaNotifica(List<HttpResponse> responses, int numConnettori, int esitoSincrono) {
		for (var r : responses) {
			checkStatoConsegnaSincrona(r, ESITO_CONSEGNA_MULTIPLA, esitoSincrono, numConnettori);
		}
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
		
		var debug_transazioni = responses.stream()
				.map( response -> response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE))
				.collect(Collectors.toList());
		String debug_clause = String.join(",",debug_transazioni);  
		
		// Questo mi crea una stringa del tipo ( ?, ?, ? )
		String IN_CLAUSE = "( " + String.join(",", Collections.nCopies(responses.size(), "?")) + ") ";
		
		String query = "select count(*) from transazioni where id in "+IN_CLAUSE+" and esito = ? and consegne_multiple = ?";
		ConfigLoader.getLoggerCore().info("Checking stato consegna for transazioni:  " + debug_clause + " AND esito = " + esito + " AND consegne-rimanenti: " + consegneMultipleRimanenti);
		ConfigLoader.getLoggerCore().info("Query: " + query);
		Integer count = ConfigLoader.getDbUtils().readValueArray(query, Integer.class,  args);
		assertEquals(Integer.valueOf(responses.size()), count);
	}
	
	
/*	public static void checkStatoConsegna(HttpResponse response, int esitoPrincipale, int consegneMultipleRimanenti) {
	
	}*/
	
	public static void checkStatoConsegnaSincrona(HttpResponse response, int esito, int esitoSincrono, int consegneMultipleRimanenti) {
		String query = "select count(*) from transazioni where id=? and esito = ?  and esito_sincrono = ? and consegne_multiple = ?";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		ConfigLoader.getLoggerCore().info("Checking stato consegna for transazione:  " + id_transazione + " AND esito = " + esito + " AND consegne-rimanenti: " + consegneMultipleRimanenti + " AND esito-sincrono: " + esitoSincrono);
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class,  id_transazione, esito, esitoSincrono, consegneMultipleRimanenti);
		
		// Uncoment for debug
		/*String query2 = "select esito, esito_sincrono, consegne_multiple from transazioni where id = ?";
		List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione);
		for (var v : letto) {
			ConfigLoader.getLoggerCore().info(v.toString());
		}*/
		
		assertEquals(Integer.valueOf(1), count);
	}
	

	/**
	 * Controlla che la transazione principale abbia un determinato esito e determinate consegne da fare
	 */
	public static void checkStatoConsegna(HttpResponse response, int esito, int consegneMultipleRimanenti) {
		String query = "select count(*) from transazioni where id=? and esito = ? and consegne_multiple = ?";
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

	
	/**
	 * Controlla che non sia partita nessuna consegna multipla, l'esito della transazione non deve essere uno delle
	 * consegne asincrone e l'esito sincrono è fermo a zero.
	 * @param response
	 */
	public static void checkNessunaNotifica(HttpResponse response) {
		String esitiConsegnaMultipla = "( 38, 39, 40, 48 )";
		String query = "select count(*) from transazioni where id=? and esito not in "+esitiConsegnaMultipla+" and esito_sincrono = 0 and consegne_multiple = 0";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		ConfigLoader.getLoggerCore().info("Checking nessuna consegna for transazione:  " + id_transazione);
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class,  id_transazione);
		assertEquals(Integer.valueOf(1), count);
	}
	
	
	public static void checkNessunoScheduling(HttpResponse response) {
		String query = "select count(*) from transazioni_sa where id_transazione=?";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		
		ConfigLoader.getLoggerCore().info("Checking nessuno scheduling connettore in corso for transazione:  " + id_transazione);
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione);
		assertEquals(Integer.valueOf(0), count);
	}

	
	public  static void checkSchedulingConnettoreIniziato(HttpResponse response, String connettore) {
		String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = ? and numero_tentativi = 0 and identificativo_messaggio is not null";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		ConfigLoader.getLoggerCore().info("Checking scheduling connettore iniziato for transazione:  " + id_transazione + " AND connettore = " + connettore);
		Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, false);
		assertEquals(Integer.valueOf(1), count);
	}
	
	
	public static void checkSchedulingConnettoreInCorso(HttpResponse response, String connettore, int esitoConsegna, String fault, String formatoFault) {
		String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = ? and numero_tentativi >=1 and dettaglio_esito = ? and identificativo_messaggio is not null";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
	
		ConfigLoader.getLoggerCore().info("Checking scheduling connettore in corso for transazione:  " + id_transazione
				+ " AND connettore = " + connettore + " AND dettaglio_esito = " + esitoConsegna + " fault = " + fault + " AND formato_fault= " + formatoFault);
		
		Integer count;
		if (fault.isEmpty()) {
			query += " and fault is null and formato_fault is null";
			count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, false, esitoConsegna);
		} else {
			query += " and fault = ? and formato_fault = ?";
			count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, false, esitoConsegna, fault, formatoFault);
		}
		
		assertEquals(Integer.valueOf(1), count);
	}


	@Deprecated
	public static void checkSchedulingConnettoreCompletato(HttpResponse response, String connettore) {
		checkSchedulingConnettoreCompletato(response, connettore, 0);
	}

	@Deprecated
	public static void checkSchedulingConnettoreCompletato(HttpResponse response, String connettore, int esito) {
		checkSchedulingConnettoreCompletato(response, connettore, esito, "", "");
	}

	
	/**
	 * Il numero tentativi è a uno perchè per i connettori per cui si completa la richiesta, la si completa
	 * sempre al primo tentativo.
	 * 
	 */
	public static void checkSchedulingConnettoreCompletato(HttpResponse response, String connettore, int esitoConsegna, String fault, String formatoFault) {
			String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = ? and numero_tentativi = 1 and dettaglio_esito = ? and identificativo_messaggio is not null";
			String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
			
			ConfigLoader.getLoggerCore().info("Checking scheduling connettore completato for transazione:  "
					+ id_transazione + " AND connettore = " + connettore + " AND dettaglio_esito = " + esitoConsegna+ " fault = " + fault + " AND formato_fault= " + formatoFault);
			
			Integer count;
			if (fault.isEmpty()) {
				query += " and fault is null and formato_fault is null";
				count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, true, esitoConsegna);
			} else {
				query += " and fault = ? and formato_fault = ?";
				count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, true, esitoConsegna, fault, formatoFault);
			}
			
			assertEquals(Integer.valueOf(1), count);				
	}

	
	public static  int getNumeroTentativiSchedulingConnettore(HttpResponse response, String connettore) {
		String query = "select numero_tentativi from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = ?";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		
		return ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, false);
	}
	
	public static boolean is5XX(int statusCode) {
		return statusCode >= 500 && statusCode <= 599;
	}
	
	public static boolean is4XX(int statusCode) {
		return statusCode >= 400 && statusCode <= 499;
	}
	
	public static boolean is3XX(int statusCode) {
		return statusCode >= 300 && statusCode <= 399;
	}
	
	public static boolean isEsitoErrore(int statusCode) {
		return is5XX(statusCode) || is4XX(statusCode) || is3XX(statusCode);
	}
	
	public static void checkRequestExpectations(RequestAndExpectations requestAndExpectation, HttpResponse response) {
		checkRequestExpectations(requestAndExpectation, response, Set.of());
	}
	
	/**
	 * Controlla che la richiesta/rispsta soddisfi le condizioni attese.
	 * Per i connettori falliti controllo che lo scheduling sia ancora in corso e per i connettori con successo
	 * controllo che lo scheduling sia completato.
	 * 
	 * @param requestAndExpectation
	 * @param response
	 */
	public static void checkRequestExpectations(RequestAndExpectations requestAndExpectation, HttpResponse response, Set<String> connettoriFile) {
		String fault = "";
		String formatoFault = "";
		
		switch (requestAndExpectation.tipoFault) {
		case REST:
			fault = FAULT_REST;
			formatoFault = FORMATO_FAULT_REST;
		break;
		case SOAP1_1:
			fault = FAULT_SOAP1_1;
			formatoFault = FORMATO_FAULT_SOAP1_1;
		break;
		case SOAP1_2:
			fault = FAULT_SOAP1_2;
			formatoFault = FORMATO_FAULT_SOAP1_2;
		break;
		default:
			break;
		}
		
		for (var connettore : requestAndExpectation.connettoriFallimento) {
			int esitoNotifica;
			if (requestAndExpectation.tipoFault != TipoFault.NESSUNO) {
				esitoNotifica = ESITO_ERRORE_APPLICATIVO;				
			} else if (isEsitoErrore(requestAndExpectation.statusCodePrincipale)) {			
				esitoNotifica = ESITO_ERRORE_INVOCAZIONE;
			} else {
				esitoNotifica = esitoConsegnaFromStatusCode(requestAndExpectation.statusCodePrincipale);
			}
			checkSchedulingConnettoreInCorso(response, connettore, esitoNotifica,fault, formatoFault);
		}
				
		for (var connettore : requestAndExpectation.connettoriSuccesso) {
			int esitoNotifica;
			if (connettoriFile.contains(connettore)) {
				esitoNotifica = ESITO_OK;
				checkSchedulingConnettoreCompletato(response, connettore, esitoNotifica,"", "");
			} else	if (requestAndExpectation.tipoFault != TipoFault.NESSUNO) {
				esitoNotifica = ESITO_ERRORE_APPLICATIVO; 
				checkSchedulingConnettoreCompletato(response, connettore, esitoNotifica, fault, formatoFault);
			} else {
				esitoNotifica =  esitoConsegnaFromStatusCode(requestAndExpectation.statusCodePrincipale);
				checkSchedulingConnettoreCompletato(response, connettore, esitoNotifica, fault, formatoFault);
			}
		}
		
		Set<String> wholeConnettori = setSum(requestAndExpectation.connettoriSuccesso,requestAndExpectation.connettoriFallimento);
		checkConnettoriRaggiuntiEsclusivamente(response, wholeConnettori);
		
		checkStatoConsegna(response,requestAndExpectation.esitoPrincipale, requestAndExpectation.connettoriFallimento.size());
	}

	
	public static void checkRequestExpectationsFinal(RequestAndExpectations requestAndExpectation, HttpResponse response) {
		checkRequestExpectationsFinal(requestAndExpectation, response, Set.of());
	}

	
	public static void checkRequestExpectationsFinal(RequestAndExpectations requestAndExpectation, HttpResponse response, Set<String> connettoriFile) {
		String fault = "";
		String formatoFault = "";
		
		switch (requestAndExpectation.tipoFault) {
		case REST:
			fault = FAULT_REST;
			formatoFault = FORMATO_FAULT_REST;
		break;
		case SOAP1_1:
			fault = FAULT_SOAP1_1;
			formatoFault = FORMATO_FAULT_SOAP1_1;
		break;
		case SOAP1_2:
			fault = FAULT_SOAP1_2;
			formatoFault = FORMATO_FAULT_SOAP1_2;
		break;
		default:
			break;
		}
		
		for ( var connettore : requestAndExpectation.connettoriFallimento) {
			assertTrue( getNumeroTentativiSchedulingConnettore(response, connettore) >= 2);
		}
		
		for (var connettore : requestAndExpectation.connettoriSuccesso) {
			int esitoNotifica;
			if (connettoriFile.contains(connettore)) {
				esitoNotifica = ESITO_OK;
				checkSchedulingConnettoreCompletato(response, connettore, esitoNotifica, "", "");
			} else if (requestAndExpectation.tipoFault != TipoFault.NESSUNO) {
				esitoNotifica = ESITO_ERRORE_APPLICATIVO;
				checkSchedulingConnettoreCompletato(response, connettore, esitoNotifica, fault, formatoFault);
			} else {
				esitoNotifica = esitoConsegnaFromStatusCode(requestAndExpectation.statusCodePrincipale);
				checkSchedulingConnettoreCompletato(response, connettore, esitoNotifica, fault, formatoFault);
			}	
		}
		
		Set<String> wholeConnettori = setSum(requestAndExpectation.connettoriSuccesso,requestAndExpectation.connettoriFallimento);
		checkConnettoriRaggiuntiEsclusivamente(response, wholeConnettori);
		
		checkStatoConsegna(response,requestAndExpectation.esitoPrincipale, requestAndExpectation.connettoriFallimento.size());
	}
	
	
	
	
	public static int esitoConsegnaFromStatusCode(int statusCode) {
		if (statusCode >= 200 && statusCode <= 299) {
			return ESITO_OK;
		} else if (statusCode >= 300 && statusCode <= 399) {
			return ESITO_3XX;
		}  else if (statusCode >= 400 && statusCode <= 499) {
			return ESITO_4XX;
		}  else if (statusCode >= 500 && statusCode <= 599) {
			return ESITO_5XX;
		}
		else throw new IllegalArgumentException("Invalid StatusCode " + statusCode);
		
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
		
		return new RequestAndExpectations(request, connettoriOk, connettoriErrore, esito,  statusCode, true);
	}
	
	/*@Deprecated
	public  static void checkSchedulingConnettoriCompletato(HttpResponse response, Set<String> connettori) {
		checkSchedulingConnettoriCompletato(response,connettori,0, "", "");
	}*/
	
	
	public  static void checkSchedulingConnettoriCompletato(HttpResponse response, Set<String> connettori, int esito, String fault, String formatoFault) {
		for (var connettore : connettori) {
			checkSchedulingConnettoreCompletato(response, connettore,esito, fault, formatoFault);			
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
		} else if (connettoriSuccesso.isEmpty() && !connettoriFallimento.isEmpty()){
			esito = ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
		} else if (connettoriFallimento.isEmpty()) {
			esito = ESITO_CONSEGNA_MULTIPLA_COMPLETATA; 
		} else {
			esito = ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
		}
		
		return new RequestAndExpectations(request, connettoriSuccesso, connettoriFallimento, esito, statusCode, true);
	}

	public static void checkResponses(Map<RequestAndExpectations, List<HttpResponse>> responsesByKind) {
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia 
		// su tutti i connettori abilitati e filtrati dalla consegna condizionale
		assertTrue(!responsesByKind.isEmpty());
		
		// Controllo che le richieste siano state consegnate e le notifiche schedulate
		for (var requestAndExpectation : responsesByKind.keySet() ) {
				var responses = responsesByKind.get(requestAndExpectation);
				assertTrue(!responses.isEmpty());
				Set<String> connettoriCoinvolti = setSum(requestAndExpectation.connettoriSuccesso, requestAndExpectation.connettoriFallimento);
				
				if (requestAndExpectation.esitoPrincipale != ESITO_ERRORE_PROCESSAMENTO_PDD_4XX) {
					if (requestAndExpectation.tipoFault != TipoFault.NESSUNO) {
						Common.checkResponsesStatus(responses, 500);
					} else {
						checkAll200(responses);
					}
					checkSchedulingConnettoreIniziato(responses, connettoriCoinvolti);
					checkStatoConsegna(responses, ESITO_CONSEGNA_MULTIPLA, connettoriCoinvolti.size());
				} else {
					checkStatoConsegna(responses, ESITO_ERRORE_PROCESSAMENTO_PDD_4XX, 0);
				}

		}
		
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*intervalloControllo);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			if (requestAndExpectation.statusCodePrincipale == 200) {
				for (var response : responsesByKind.get(requestAndExpectation)) {
					checkRequestExpectations(requestAndExpectation, response);
				}
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		org.openspcoop2.utils.Utilities.sleep(2*intervalloControlloFallite);
		for (var requestAndExpectation : responsesByKind.keySet()) {
			if (requestAndExpectation.statusCodePrincipale == 200) {
				for (var response : responsesByKind.get(requestAndExpectation)) {
					checkRequestExpectationsFinal(requestAndExpectation, response);
				}
			}
		}
	}

	/** 
	 * Controlla che la richiesta sia stata inoltrata su file dai set di connettori indicati.
	 * 
	 * @param request 				-	La richiesta iniziale
	 * @param responseSync	-	La risposta di presa in carico
	 * @param connettoriFile		-  I connettori dell'erogazione che vanno su file 
	 *
	 */
	public static  void checkConsegnaConnettoreFile(HttpRequest request, HttpResponse responseSync, Set<String> connettoriFile) throws IOException {
		String idTransazione = responseSync.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		List<File> filesCreati = CommonConsegnaMultipla.getFilesCreati(idTransazione);
		
		
		for (var connettore : connettoriFile) {
			String requestFilename = connettore+":"+idTransazione+":request";
			String requestHeadersFilename = connettore+":"+idTransazione+":request-headers";
			
			File requestFile = filesCreati.stream().filter
					( f -> f.getName().equals(requestFilename)).findAny().get();
			
			File requestHeadersFile = filesCreati.stream().filter
					( f -> f.getName().equals(requestHeadersFilename)).findAny().get();
			
			HttpRequest requestConsegna = new HttpRequest();
			requestConsegna.setContent(Files.readAllBytes(requestFile.toPath()));
			
			List<String> content = Files.readAllLines(requestHeadersFile.toPath());
			for (var line : content) {
				String[] header = line.split("=");
				requestConsegna.addHeader(header[0], header[1]);
			}
			
			if (request.getContent() != null) {
				assertTrue(Arrays.equals(request.getContent(), requestConsegna.getContent()));
			}
			assertEquals(idTransazione, requestConsegna.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE));
			
			assertEquals(connettoriFile.size()*2, filesCreati.size());
		}
	}

	public static  List<File> getFilesCreati(String idTransazione) throws IOException {
		return Files.list(connettoriFilePath)
				.filter( file -> !Files.isDirectory(file) && file.toFile().getName().contains(idTransazione) )
				.map(Path::toFile)
				.collect(Collectors.toList());
	}

	public static void jmxAbilitaSchedulingConnettore(String erogazione, String connettore) throws UtilsException, HttpUtilsException {
	    	org.slf4j.Logger logger = ConfigLoader.getLoggerCore();
	    	logger.debug("Abilito connettore: " + connettore+ " per erogazione: "+ erogazione);
	    	
	        String jmx_user = System.getProperty("jmx_username");
	        String jmx_pass = System.getProperty("jmx_password"); 
	        
	        String nomePorta = "gw_SoggettoInternoTest/gw_"+erogazione+"/v1";
	
	        String url =  System.getProperty("govway_base_path") + "/check?resourceName=ConfigurazionePdD&methodName=enableSchedulingConnettoreMultiplo&paramValue="+nomePorta+"&paramValue2="+connettore;
	        org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmx_user, jmx_pass);
	        
	        url =  System.getProperty("govway_base_path") + "/check?resourceName=ConfigurazionePdD&methodName=enableSchedulingConnettoreMultiploRuntimeRepository&paramValue="+nomePorta+"&paramValue2="+connettore;
	        org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmx_user, jmx_pass);
	}

	public static void jmxDisabilitaSchedulingConnettore(String erogazione, String connettore) throws UtilsException, HttpUtilsException {
		org.slf4j.Logger logger = ConfigLoader.getLoggerCore();
		logger.debug("Disabilito connettore: " + connettore+ " per erogazione: "+ erogazione);
	
	    String jmx_user = System.getProperty("jmx_username");
	    String jmx_pass = System.getProperty("jmx_password"); 
	    
	    String nomePorta = "gw_SoggettoInternoTest/gw_"+erogazione+"/v1";
	    
	    String url =  System.getProperty("govway_base_path") + "/check?resourceName=ConfigurazionePdD&methodName=disableSchedulingConnettoreMultiplo&paramValue="+nomePorta+"&paramValue2="+connettore;
	    org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmx_user, jmx_pass);
	    
	    url =  System.getProperty("govway_base_path") + "/check?resourceName=ConfigurazionePdD&methodName=disableSchedulingConnettoreMultiploRuntimeRepository&paramValue="+nomePorta+"&paramValue2="+connettore;
	    org.openspcoop2.utils.transport.http.HttpUtilities.check(url, jmx_user, jmx_pass);
		
	}

	
	
}
