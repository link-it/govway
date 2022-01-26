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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
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
 */
public class ConsegnaMultiplaTest  extends ConfigLoader {
	
	// Prima di ogni test  fermo le attuali riconsegne in atto.
	
	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
	}

	// Alla fine della classe di test fermo le eventuali riconsegne ancora in atto
	
	@AfterClass
	public static void After() {
		// TODO?
		//Common.fermaRiconsegne(dbUtils);
	}

	final static EsitiProperties esitiProperties = getEsitiProperties();
	
	
	@Test
	public void primoTest() throws IOException {
		
		final String erogazione = "TestConsegnaMultipla";
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		
		var responses = Common.makeParallelRequests(request, 10);
		Common.checkAll200(responses);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			CommonConsegnaMultipla.checkPresaInConsegna(r);
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
		}
	
		// Attendo la consegna
		
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
		
		for (var r : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_COMPLETATA);
		}

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		Set<String> connettoriFile = Set.of(CONNETTORE_2, CONNETTORE_3);
		for (var response : responses) {
			checkConsegnaConnettoreFile(request, response, connettoriFile);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  Common.setConnettoriAbilitati);
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

		// Sui connettori file è sempre tutto ok
		Set<String> connettoriFile = Set.of(CONNETTORE_2, CONNETTORE_3);
		
		Set<String> connettoriSuccessoRequest5xx = Set.of(CONNETTORE_2, CONNETTORE_3);
		Set<String> connettoriFallimentoRequest5xx = Set.of(CONNETTORE_0, CONNETTORE_1);
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		for(int i=0; i<10;i++) {
			HttpRequest request5xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
			request5xx.setUrl(request5xx.getUrl()+"&returnCode=" + (500+i));
			requestsByKind.add(new RequestAndExpectations(request5xx, connettoriSuccessoRequest5xx, connettoriFallimentoRequest5xx, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO));
		}		
		
		for(int i=0; i<10;i++) {
			HttpRequest request2xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
			request2xx.setUrl(request2xx.getUrl()+"&returnCode=" + (200+i));
			requestsByKind.add(new RequestAndExpectations(request2xx, Common.setConnettoriAbilitati, Set.of(), CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_COMPLETATA ));
		}
		
		for(int i=0; i<10;i++) {
			HttpRequest request4xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
			request4xx.setUrl(request4xx.getUrl()+"&returnCode=" + (400+i));
			requestsByKind.add(new RequestAndExpectations(request4xx, connettoriSuccessoRequest5xx, connettoriFallimentoRequest5xx, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO));
		}
		
		 for(int i=0; i<10;i++) { 
			HttpRequest request3xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
			request3xx.setUrl(request3xx.getUrl()+"&returnCode=" + (300+i));
			requestsByKind.add(new RequestAndExpectations(request3xx, connettoriSuccessoRequest5xx, connettoriFallimentoRequest5xx, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO));
		}
		
		
		HttpRequest requestSoapFault = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestSoapFault.setUrl(requestSoapFault.getUrl() +"&fault=true");
		requestsByKind.add(new RequestAndExpectations(requestSoapFault, Common.setConnettoriAbilitati, Set.of(),  CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_FALLITA ));
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
			CommonConsegnaMultipla.checkPresaInConsegna(responses);
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
		}
		
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				checkConsegnaConnettoreFile(requestAndExpectation.request, response, connettoriFile);
				CommonConsegnaMultipla.checkRequestExpectations(requestAndExpectation, response);
			}
		}

		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControlloFallite);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				CommonConsegnaMultipla.checkRequestExpectationsFinal(requestAndExpectation, response);
			}
		}
	}
	
	
	@Test
	public void varieCombinazioniDiRegole() {
		final String erogazione = "TestConsegnaMultiplaVarieCombinazioniDiRegole";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		Map<Integer, Set<String>> statusCode2xxVsConnettori  = Map
				.of(200, Set.of(CONNETTORE_1,CONNETTORE_2),
					  201, Set.of(CONNETTORE_1, CONNETTORE_3),
					  202, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
					  206, Set.of(CONNETTORE_1),
					  299, Set.of(CONNETTORE_1));
		
		Map<Integer, Set<String>> statusCode4xxVsConnettori  = Map
				.of(400, Set.of(CONNETTORE_1,CONNETTORE_2),
					  401, Set.of(CONNETTORE_1, CONNETTORE_3),
					  402, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
					  406, Set.of(CONNETTORE_1),
					  499, Set.of(CONNETTORE_1));
		
		Map<Integer, Set<String>> statusCode5xxVsConnettori  = Map
				.of(500, Set.of(CONNETTORE_1,CONNETTORE_2),
					  501, Set.of(CONNETTORE_1, CONNETTORE_3),
					  502, Set.of(CONNETTORE_1,CONNETTORE_2, CONNETTORE_3),
					  506, Set.of(CONNETTORE_1),
				  	  599, Set.of(CONNETTORE_1));
		
		
		for (var entry : statusCode2xxVsConnettori.entrySet()) {
			requestsByKind.add(CommonConsegnaMultipla.buildRequestAndExpectations(erogazione, entry.getKey(), entry.getValue()));
		}
		
		// TODO: Contatta andrea, in questi casi l'esito diventa "Consegna Asincrona Fallita", mentre dovrebbe darmi
		// "Consegna Asincrona In Corso"
		// TODO: Quando è fixato vedi di aggiungere un nuovo connettore con scheduling disabilitato
		
		/*for (var entry : statusCode4xxVsConnettori.entrySet()) {
			requestsByKind.add(buildRequestAndExpectations(erogazione, entry.getKey(), entry.getValue()));
		}*/
				
		/*for (var entry : statusCode5xxVsConnettori.entrySet()) { 
			requestsByKind.add(buildRequestAndExpectations(erogazione, entry.getKey(), entry.getValue()));
		}*/
		
		/*
		HttpRequest requestSoapFault = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestSoapFault.setUrl(requestSoapFault.getUrl() +"&fault=true");
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFault,
				Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
				Set.of(CONNETTORE_1),  
				ESITO_CONSEGNA_MULTIPLA_IN_CORSO ));*/
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
			CommonConsegnaMultipla.checkPresaInConsegna(responses);
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
		}
		
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				CommonConsegnaMultipla.checkRequestExpectations(requestAndExpectation, response);
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControlloFallite);
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				CommonConsegnaMultipla.checkRequestExpectationsFinal(requestAndExpectation, response);
			}
		}
		
		// TODO: Aggiungi anche un connettore con scheduling disabilitato
		// TODO: Aggiungi un testo con connettore rotto
	}
	
	
	@Test
	public void testRegoleSoapFault() {
		final String erogazione = "TestConsegnaMultiplaRegoleSoapFault";
		
		// passare al server di echo faultSoapVersion (11 o 12) TODO?
		
		HttpRequest requestSoapFaultByCode = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestSoapFaultByCode.setUrl(requestSoapFaultByCode.getUrl()+"&fault=true&faultActor=Actor&faultCode=CodeCompletata&faultMessage=Message");	
		
		HttpRequest requestSoapFaultByActor = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestSoapFaultByActor.setUrl(requestSoapFaultByActor.getUrl()+"&fault=true&faultActor=ActorCompletata&faultCode=Code&faultMessage=Message");
		
		HttpRequest requestSoapFaultByMessage = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestSoapFaultByMessage.setUrl(requestSoapFaultByMessage.getUrl()+"&fault=true&faultActor=Actor&faultCode=Code&faultMessage=MessageCompletata");
		
		// TODO: Chiedi ad andrea se i campi del soap fault sono in and
		// TODO: Sembra inoltre che sia il contrario di quello che mi ha detto lui, ovvero che se c'è un match fra 
		//	uno dei campi actor message e code, allora la richiesta è considerata fallita
		
		HttpRequest requestSoapFaultByRegex = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestSoapFaultByRegex.setUrl(requestSoapFaultByCode.getUrl()+"&fault=true&faultActor=12345&faultCode=2341&faultMessage=MessageCompletataRegex"); 
	
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
/*		requestsByKind.add(new RequestAndExpectations(
				requestSoapFaultCode,
				Set.of(CONNETTORE_0), 
				Set.of(CONNETTORE_1,CONNETTORE_2,CONNETTORE_3), 
				ESITO_CONSEGNA_MULTIPLA_IN_CORSO)
			);*/
		
		/*requestsByKind.add(new RequestAndExpectations(
				requestSoapFaultActor,
				Set.of(CONNETTORE_1), 
				Set.of(CONNETTORE_0,CONNETTORE_2,CONNETTORE_3), 
				ESITO_CONSEGNA_MULTIPLA_IN_CORSO)
			);*/
		
		/*requestsByKind.add(new RequestAndExpectations(
				requestSoapFaultByMessage,
				Set.of(CONNETTORE_2), 
				Set.of(CONNETTORE_0,CONNETTORE_1,CONNETTORE_3), 
				ESITO_CONSEGNA_MULTIPLA_IN_CORSO)
			);*/
		
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFaultByRegex,
				Set.of(CONNETTORE_3), 
				Set.of(CONNETTORE_0,CONNETTORE_1,CONNETTORE_2), 
				CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO)
			);
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
			CommonConsegnaMultipla.checkPresaInConsegna(responses);
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
		}
		
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				CommonConsegnaMultipla.checkRequestExpectations(requestAndExpectation, response);
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControlloFallite);
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				CommonConsegnaMultipla.checkRequestExpectationsFinal(requestAndExpectation, response);
			}
		}

	}
	
	@Test
	public void cadenzaRispedizione() {
		// TODO
	}
	
	private static EsitiProperties getEsitiProperties() {
		try {
			return EsitiProperties.getInstance(logCore, CommonConsegnaMultipla.PROTOCOL_NAME);
		} catch (ProtocolException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
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
		return Files.list(CommonConsegnaMultipla.connettoriFilePath)
				.filter( file -> !Files.isDirectory(file) && file.toFile().getName().contains(idTransazione) )
				.map(Path::toFile)
				.collect(Collectors.toList());
	}

}
