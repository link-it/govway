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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_0;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_3;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;


/**
 * 
 * @author froggo
 * 
 * Connettore0: http
 * Connettore1: http
 * Connettore2: file
 * Connettore3: file
 * 
 * TODO: Per ora sto usando gli esiti come numeri interi, poi usa costanti presi da esiti properties?
 * TODO: Test soap 1_2
 * 
 * àùàùàòù àòùàòùòùàùàò TEST ACCENTI, si leggono dopo il push?? TODO
 */
public class ConsegnaMultiplaTest  extends ConfigLoader {
	
	private class RequestAndExpectations {

		public final HttpRequest request;
		public final Set<String> connettoriSuccesso;
		public final Set<String> connettoriFallimento;
		public final int esito;
		
		public RequestAndExpectations(HttpRequest request, Set<String> connettoriSuccesso, Set<String> connettoriFallimento, int esito) {
			this.request = request;
			this.connettoriSuccesso = connettoriSuccesso;
			this.connettoriFallimento = connettoriFallimento;
			this.esito = esito;
		}
		
	}
	
	
	private static EsitiProperties getEsitiProperties() {
		try {
			return EsitiProperties.getInstance(logCore, PROTOCOL_NAME);
		} catch (ProtocolException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	// Prima di ogni test  fermo le attuali riconsegne in atto.
	
	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
		
		// TODO: Pulisci la cartella delle richieste dei coinnettori file
//		Files.del
	}

	// Alla fine della classe di test fermo le eventuali riconsegne ancora in atto
	
	@AfterClass
	public static void After() {
		//Common.fermaRiconsegne(dbUtils);
	}
	

	final static String PROTOCOL_NAME = "trasparente";
	
	final static int ESITO_CONSEGNA_MULTIPLA = 38;
	
	final static int ESITO_CONSEGNA_MULTIPLA_COMPLETATA = 39;
	
	final static int ESITO_CONSEGNA_MULTIPLA_FALLITA = 40;
	
	final static int ESITO_CONSEGNA_MULTIPLA_IN_CORSO = 48;

	final static int intervalloControllo = Integer
			.valueOf(System.getProperty("connettori.consegna_multipla.next_messages.intervallo_controllo")) * 1000;
	
	final static int scheduleNewAfter = Integer
			.valueOf(System.getProperty("connettori.consegna_multipla.next_messages.schedule_new_after")) * 1000;
	
	final static int intervalloControlloFallite = Integer
			.valueOf(System.getProperty("connettori.consegna_multipla.next_messages.consegna_fallita.intervallo_controllo")) *1000;
	
	final static int intervalloMinimoRiconsegna = Integer
			.valueOf(System.getProperty("connettori.consegna_multipla.next_messages.consegna_fallita.intervallo_minimo_riconsegna")) *1000;

	
	final static Path connettoriFilePath = Paths.get(System.getProperty("connettori.consegna_multipla.connettore_file.path"));
	
	final static EsitiProperties esitiProperties = getEsitiProperties();
	
	
	@Test
	public void primoTest() throws IOException {
		
		final String erogazione = "TestConsegnaMultipla";
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		
		var responses = Common.makeParallelRequests(request, 10);
		Common.checkAll200(responses);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			checkPresaInConsegna(r);
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
		}
	
		// Attendo la consegna
		
		org.openspcoop2.utils.Utilities.sleep(2*intervalloControllo);
		
		for (var r : responses) {
			checkConsegnaCompletata(r, ESITO_CONSEGNA_MULTIPLA_COMPLETATA);
		}

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		Set<String> connettoriFile = Set.of(CONNETTORE_2, CONNETTORE_3);
		for (var response : responses) {
			checkConsegnaConnettoreFile(request, response, connettoriFile);
			checkSchedulingConnettoriCompletato(response,  Common.setConnettoriAbilitati);
		}
	}
	
	
	/**
	 * Nel primo test verifico il funzionamento senza particolari condizioni: invio una richiesta
	 * e verifico le consegne. Qui invece inizio facendo fallire qualche richiesta.
	 * I connettori file non posso istruirli, quindi lavoro solo sui primi due connettori.
	 * @throws IOException 
	 */
	@Test
	public void secondoTest() throws IOException {
		final String erogazione = "TestConsegnaMultipla";
		
		/* TODO: Fai un test a parte per andrea sulla gestione della 300
		 * HttpRequest request3xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 ); TODO: Per la 3xx manda mail ad andrea
		request3xx.setUrl(request3xx.getUrl() + "&returnCode=304");
		HttpResponse response3xx = Utils.makeRequest(request3xx);*/

		// Sui connettori file è sempre tutto ok
		Set<String> connettoriFile = Set.of(CONNETTORE_2, CONNETTORE_3);
		
		Set<String> connettoriSuccessoRequest5xx = Set.of(CONNETTORE_2, CONNETTORE_3);
		Set<String> connettoriFallimentoRequest5xx = Set.of(CONNETTORE_0, CONNETTORE_1);
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		for(int i=0; i<10;i++) {
			HttpRequest request5xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
			request5xx.setUrl(request5xx.getUrl()+"&returnCode=" + (500+i));
			requestsByKind.add(new RequestAndExpectations(request5xx, connettoriSuccessoRequest5xx, connettoriFallimentoRequest5xx, ESITO_CONSEGNA_MULTIPLA_IN_CORSO));
		}		
		
		for(int i=0; i<10;i++) {
			HttpRequest request2xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
			request2xx.setUrl(request2xx.getUrl()+"&returnCode=" + (200+i));
			requestsByKind.add(new RequestAndExpectations(request2xx, Common.setConnettoriAbilitati, Set.of(), ESITO_CONSEGNA_MULTIPLA_COMPLETATA ));
		}
		
		for(int i=0; i<10;i++) {
			HttpRequest request4xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
			request4xx.setUrl(request4xx.getUrl()+"&returnCode=" + (400+i));
			requestsByKind.add(new RequestAndExpectations(request4xx, connettoriSuccessoRequest5xx, connettoriFallimentoRequest5xx, ESITO_CONSEGNA_MULTIPLA_IN_CORSO));
		}
		
		HttpRequest requestSoapFault = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestSoapFault.setUrl(requestSoapFault.getUrl() +"&fault=true");
		requestsByKind.add(new RequestAndExpectations(requestSoapFault, Common.setConnettoriAbilitati, Set.of(),  ESITO_CONSEGNA_MULTIPLA_FALLITA ));
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
			checkPresaInConsegna(responses);
			checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
		}
		
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*intervalloControllo);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				checkConsegnaConnettoreFile(requestAndExpectation.request, response, connettoriFile);
				checkRequestExpectations(requestAndExpectation, response);
			}
		}
		
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		org.openspcoop2.utils.Utilities.sleep(2*intervalloControlloFallite);
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				checkRequestExpectationsFinal(requestAndExpectation, response);
			}
		}
		
	}
	
	
	private void checkRequestExpectations(RequestAndExpectations requestAndExpectation, HttpResponse response) {
		for ( var connettore : requestAndExpectation.connettoriFallimento) {
			checkSchedulingConnettoreInCorso(response, connettore);
		}
		
		for (var connettore : requestAndExpectation.connettoriSuccesso) {
			checkSchedulingConnettoreCompletato(response, connettore);
		}
		
		checkStatoConsegna(response,requestAndExpectation.esito, requestAndExpectation.connettoriFallimento.size());
	}
	
	
	private void checkRequestExpectationsFinal(RequestAndExpectations requestAndExpectation, HttpResponse response) {
		for ( var connettore : requestAndExpectation.connettoriFallimento) {
			assertTrue( getNumeroTentativiSchedulingConnettore(response, connettore) >= 2);
		}
		
		for (var connettore : requestAndExpectation.connettoriSuccesso) {
			checkSchedulingConnettoreCompletato(response, connettore);
		}
		
		checkStatoConsegna(response,requestAndExpectation.esito, requestAndExpectation.connettoriFallimento.size());
	}
	
	
	private void checkStatoConsegna(HttpResponse response, int esito, int consegne_multiple_rimanenti) {
		String query = "select count(*) from transazioni where id=? and esito = ? and esito_sincrono = 0 and consegne_multiple = ?";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		Integer count = dbUtils.readValue(query, Integer.class,  id_transazione, esito, consegne_multiple_rimanenti);
		assertEquals(Integer.valueOf(1), count);
	}
	
	
	private void checkConsegnaCompletata(HttpResponse response, int esito) {
		String query = "select count(*) from transazioni where id=? and esito = ? and esito_sincrono = 0 and consegne_multiple = 0";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		Integer count = dbUtils.readValue(query, Integer.class,  id_transazione, esito);
		assertEquals(Integer.valueOf(1), count);
	}
	

	
	/**
	 * Esegue requests_per_batch richieste per ogni richiesta della lista.
	 * Restituisce la map delle risposte indicizzate per richiesta.
	 * 
	 * @param requests
	 * @param requests_per_batch
	 * @return
	 */
	Map<RequestAndExpectations, List<HttpResponse>> makeRequestsByKind(List<RequestAndExpectations> requests, int requests_per_batch) {
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
			logCore.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		return responses;
				
	}
	
	
	private void checkSchedulingConnettoriCompletato(HttpResponse response, Set<String> connettori) {
		for (var connettore : connettori) {
			checkSchedulingConnettoreCompletato(response, connettore);			
		}		
	}
	
	private void checkSchedulingConnettoriInCorso(HttpResponse response, Set<String> connettori) {
		for (var connettore : connettori) {
			checkSchedulingConnettoreInCorso(response, connettore);			
		}		
	}
	
	private void checkSchedulingConnettoreInCorso(HttpResponse response, String connettore) {
		String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = false and numero_tentativi >=1";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);

		Integer count = dbUtils.readValue(query, Integer.class, id_transazione, connettore);
		assertEquals(Integer.valueOf(1), count);
	}
	
	

	private void checkSchedulingConnettoreIniziato(List<HttpResponse> responses, Set<String> connettori) {
		for (var r : responses) {
			checkSchedulingConnettoreIniziato(r, connettori);
		}
	}
	
	private void checkSchedulingConnettoreIniziato(HttpResponse response, Set<String> connettori) {	
		for (var connettore : connettori) {
			checkSchedulingConnettoreIniziato(response, connettore);
		}		
	}
	
	private void checkSchedulingConnettoreIniziato(HttpResponse response, String connettore) {
		String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = false and numero_tentativi = 0";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);

		Integer count = dbUtils.readValue(query, Integer.class, id_transazione, connettore);
		assertEquals(Integer.valueOf(1), count);
	}

	
	
	private void checkPresaInConsegna(List<HttpResponse> responses) {
		for (var r : responses) {
			checkPresaInConsegna(r);
		}
	}
	
	private void checkConsegnaInCorso(HttpResponse response) {
				// esito 48: Consegna multipla completata
				String query = "select count(*) from transazioni where id=? and esito = 48 and esito_sincrono = 0 and consegne_multiple = 2";
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				Integer count = dbUtils.readValue(query, Integer.class,  id_transazione);
				assertEquals(Integer.valueOf(1), count);	
	}
	
	
	private void checkPresaInConsegna(HttpResponse response) {
		/**
		 * esito | 38 		CONSEGNA MULTIPLA
		 * esito_sincrono | 0			Transazione gestita con successo
		 * consegne_multiple | 4	 Sono il numero di consegne multiple rimanenti
		 */
		
		String query = "select count(*) from transazioni where id=? and esito = 38 and esito_sincrono = 0 and consegne_multiple = 4";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		Integer count = dbUtils.readValue(query, Integer.class,  id_transazione);
		assertEquals(Integer.valueOf(1), count);
	}

	
	private void checkSchedulingConnettoreCompletato(HttpResponse response, String connettore) {
			String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = true and numero_tentativi = 1";
			String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);

			Integer count = dbUtils.readValue(query, Integer.class, id_transazione, connettore);
			assertEquals(Integer.valueOf(1), count);				
	}
	

	
	private int getNumeroTentativiSchedulingConnettore(HttpResponse response, String connettore) {
		String query = "select numero_tentativi from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = false";
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		
		return dbUtils.readValue(query, Integer.class, id_transazione, connettore);
	}

	







	/** 
	 * Controlla che la richiesta sia stata inoltrata su file dai set di connettori indicati.
	 * 
	 * @param request 				-	La richiesta iniziale
	 * @param responseSync	-	La risposta di presa in carico
	 * @param connettoriFile		-  I connettori dell'erogazione che vanno su file 
	 *
	 */
	private void checkConsegnaConnettoreFile(HttpRequest request, HttpResponse responseSync, Set<String> connettoriFile) throws IOException {
		String idTransazione = responseSync.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		List<File> filesCreati = getFilesCreati(idTransazione);
		
		
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
			
			assertTrue(Arrays.equals(request.getContent(), requestConsegna.getContent()));
			assertEquals(idTransazione, requestConsegna.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE));
			
			assertEquals(connettoriFile.size()*2, filesCreati.size());
		}
	}
	
	private List<File> getFilesCreati(String idTransazione) throws IOException {
		return Files.list(connettoriFilePath)
				.filter( file -> !Files.isDirectory(file) && file.toFile().getName().contains(idTransazione) )
				.map(Path::toFile)
				.collect(Collectors.toList());
	}

	@Test
	public void testRegoleSoapFault() {
		final String erogazione = "TestConsegnaMultiplaRegoleSoapFault";

	}

}
