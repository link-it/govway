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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_ROTTO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_COMPLETATA;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_FALLITA;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.buildRequestAndExpectations;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkSchedulingConnettoreCompletato;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkStatoConsegna;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.getNumeroTentativiSchedulingConnettore;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.setDifference;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;

 
// se tutti e quattro i connettori falliscono lo stato rimaneva IN CODA (38) fare un test
// su questo dopo il pull


// TODO: Aggiungi anche il test con connettore con scheduling disabilitato, disattivandolo e riattivandolo tramite jmx

//TODO: Test threading, se riesco a fare dei test sull'allocazione dei threads di govway, anche solo due test magari da un minuto solo, va bene.

// TODO: raggruppai test Soap 1_1 e Soap 1_2 facendo prima le richieste per entrambi


// TODO: Potrei farmi una passata di tutte le erogazioni e personalizzare ulteriormente i vari status code e le combinazioni
// 		di  regole per far passare o meno una consegna, chiedi ad andrea.

/**
 * 
 * @author froggo
 * 
 * Connettore0: http
 * Connettore1: http
 * Connettore2: file
 * Connettore3: file
 * 
 *  
 * 
 */
public class ConsegnaMultiplaTest  extends ConfigLoader {
	
	// Prima di ogni test  fermo le attuali riconsegne in atto.
	
	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
		File cartellaRisposte = CommonConsegnaMultipla.connettoriFilePath.toFile();
		if (!cartellaRisposte.isDirectory()|| !cartellaRisposte.canWrite()) {
			throw new RuntimeException("E' necessario creare la cartella per scrivere le richieste dei connettori, indicata dalla poprietà: <connettori.consegna_multipla.connettore_file.path> ");
		}
	}
	
	@AfterClass
	public static void After() {
		// Alla fine della classe di test fermo le eventuali riconsegne ancora in atto
		// TODO?
		//Common.fermaRiconsegne(dbUtils);
	}


	@Test
	public void consegnaMultiplaSemplice() throws IOException {
		final String erogazione = "TestConsegnaMultipla";
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		
		var responses = Common.makeParallelRequests(request1, 10);
		var responses2 = Common.makeParallelRequests(request2, 10);
		Common.checkAll200(responses);
		Common.checkAll200(responses2);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			CommonConsegnaMultipla.checkPresaInConsegna(r);
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
		}
		
		for (var r : responses2) {
			CommonConsegnaMultipla.checkPresaInConsegna(r);
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
		}
	
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
		
		for (var r : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r);
		}
		for (var r : responses2) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r);
		}

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		Set<String> connettoriFile = Set.of(CONNETTORE_2, CONNETTORE_3);
		for (var response : responses) {
			checkConsegnaConnettoreFile(request1, response, connettoriFile);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  Common.setConnettoriAbilitati);
		}
		for (var response : responses2) {
			checkConsegnaConnettoreFile(request2, response, connettoriFile);
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
	public void consegnaMultiplaErrori() throws IOException {
		final String erogazione = "TestConsegnaMultipla";

		// Sui connettori file è sempre tutto ok
		Set<String> connettoriFile = Set.of(CONNETTORE_2, CONNETTORE_3);
		
		Set<String> connettoriSuccessoRequest = Set.of(CONNETTORE_2, CONNETTORE_3);
		Set<String> connettoriFallimentoRequest = Set.of(CONNETTORE_0, CONNETTORE_1);
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		for(int i=0; i<10;i++) {
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			HttpRequest request5xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType );
			request5xx.setUrl(request5xx.getUrl()+"&returnCode=" + (500+i));
			requestsByKind.add(new RequestAndExpectations(request5xx, connettoriSuccessoRequest, connettoriFallimentoRequest, ESITO_CONSEGNA_MULTIPLA_IN_CORSO));
			
			HttpRequest request2xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType );
			request2xx.setUrl(request2xx.getUrl()+"&returnCode=" + (200+i));
			requestsByKind.add(new RequestAndExpectations(request2xx, Common.setConnettoriAbilitati, Set.of(), ESITO_CONSEGNA_MULTIPLA_COMPLETATA ));
			
			HttpRequest request4xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType );
			request4xx.setUrl(request4xx.getUrl()+"&returnCode=" + (400+i));
			requestsByKind.add(new RequestAndExpectations(request4xx, connettoriSuccessoRequest, connettoriFallimentoRequest, ESITO_CONSEGNA_MULTIPLA_IN_CORSO));
		}		
				
		HttpRequest requestSoapFault =  RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestsByKind.add(new RequestAndExpectations(requestSoapFault, Common.setConnettoriAbilitati, Set.of(),  ESITO_CONSEGNA_MULTIPLA_FALLITA ));
		
		requestSoapFault =  RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2 );
		requestsByKind.add(new RequestAndExpectations(requestSoapFault, Common.setConnettoriAbilitati, Set.of(),  ESITO_CONSEGNA_MULTIPLA_FALLITA ));
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
			Common.checkAll200(responses);
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
		
		int i = 0;
		for (var entry : CommonConsegnaMultipla.statusCodeVsConnettori.entrySet()) {
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			int statusCode = entry.getKey();
			var connettoriSuccesso = entry.getValue();
			var connettoriErrore = setDifference(Common.setConnettoriAbilitati,connettoriSuccesso);
			requestsByKind.add(buildRequestAndExpectations(erogazione, statusCode, connettoriSuccesso, connettoriErrore, soapContentType));			
			i++;
		}
		
		HttpRequest requestSoapFault = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFault,
				Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
				Set.of(CONNETTORE_1),  
				ESITO_CONSEGNA_MULTIPLA_FALLITA ));
		
		requestSoapFault = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFault,
				Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
				Set.of(CONNETTORE_1),  
				ESITO_CONSEGNA_MULTIPLA_FALLITA ));
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
		   	Common.checkAll200(responses);
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
	
	
	/**
	 * In questo test viene controllato che la rispedizione avvenga in caso di match col soapFault, e che non viene
	 * rispedita se non c'è match.
	 */
	@Test
	public void  testRegoleSoapFault() {
		final String erogazione = "TestConsegnaMultiplaRegoleSoapFault";
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		
		HttpRequest requestSoapFaultTuttiValorizzati = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestSoapFaultTuttiValorizzati.setUrl(requestSoapFaultTuttiValorizzati.getUrl()+"&faultActor=ActorRispedita&faultCode=CodeRispedita&faultMessage=MessageRispedita");	
		
		HttpRequest requestSoapFaultTuttiRegex = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestSoapFaultTuttiRegex.setUrl(requestSoapFaultTuttiRegex.getUrl()+"&faultActor=ActorRispedita123&faultCode=CodeRispedita123&faultMessage=MessageRispedita123");
		
		HttpRequest requestSoapFaultSoloActor = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestSoapFaultSoloActor.setUrl(requestSoapFaultSoloActor.getUrl()+"&faultActor=ActorRispedita&faultCode=CodeNotImportant&faultMessage=MessageNotImportant");

		// Questa richiesta testa che sul campo message venga fatto il contains.
		HttpRequest requestSoapMessageContains = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestSoapMessageContains.setUrl(requestSoapMessageContains.getUrl()+"&faultActor=12345&faultCode=2341&faultMessage=Container-MessageRispeditaContains-Container"); 
	
	/*	requestsByKind.add(new RequestAndExpectations(
				requestSoapFaultTuttiValorizzati,				
				Set.of(CONNETTORE_1,CONNETTORE_2),
				Set.of(CONNETTORE_0, CONNETTORE_3),
				ESITO_CONSEGNA_MULTIPLA_FALLITA)
			);
		
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFaultTuttiRegex,
				Set.of(CONNETTORE_0,CONNETTORE_2,CONNETTORE_3),
				Set.of(CONNETTORE_1), 
				ESITO_CONSEGNA_MULTIPLA_FALLITA)
			);
		
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFaultSoloActor,
				Set.of(CONNETTORE_0,CONNETTORE_1,CONNETTORE_2),
				Set.of(CONNETTORE_3), 
				ESITO_CONSEGNA_MULTIPLA_FALLITA)
			);

		requestsByKind.add(new RequestAndExpectations(
				requestSoapMessageContains,				
				Set.of(CONNETTORE_0,CONNETTORE_1,CONNETTORE_3),
				Set.of(CONNETTORE_2),
				ESITO_CONSEGNA_MULTIPLA_FALLITA)
			);*/
		
		requestSoapFaultTuttiValorizzati = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestSoapFaultTuttiValorizzati.setUrl(requestSoapFaultTuttiValorizzati.getUrl()+"&faultActor=ActorRispedita&faultCode=CodeRispedita&faultMessage=MessageRispedita");	
		
		requestSoapFaultTuttiRegex = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestSoapFaultTuttiRegex.setUrl(requestSoapFaultTuttiRegex.getUrl()+"&faultActor=ActorRispedita123&faultCode=CodeRispedita123&faultMessage=MessageRispedita123");
		
		requestSoapFaultSoloActor = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestSoapFaultSoloActor.setUrl(requestSoapFaultSoloActor.getUrl()+"&faultActor=ActorRispedita&faultCode=CodeNotImportant&faultMessage=MessageNotImportant");

		// Questa richiesta testa che sul campo message venga fatto il contains.
		requestSoapMessageContains = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestSoapMessageContains.setUrl(requestSoapMessageContains.getUrl()+"&faultActor=12345&faultCode=2341&faultMessage=Container-MessageRispeditaContains-Container"); 
	
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFaultTuttiValorizzati,				
				Set.of(CONNETTORE_1,CONNETTORE_2),
				Set.of(CONNETTORE_0, CONNETTORE_3),
				ESITO_CONSEGNA_MULTIPLA_FALLITA)
			);
		
	/*	requestsByKind.add(new RequestAndExpectations(
				requestSoapFaultTuttiRegex,
				Set.of(CONNETTORE_0,CONNETTORE_2,CONNETTORE_3),
				Set.of(CONNETTORE_1), 
				ESITO_CONSEGNA_MULTIPLA_FALLITA)
			);
		
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFaultSoloActor,
				Set.of(CONNETTORE_0,CONNETTORE_1,CONNETTORE_2),
				Set.of(CONNETTORE_3), 
				ESITO_CONSEGNA_MULTIPLA_FALLITA)
			);

		requestsByKind.add(new RequestAndExpectations(
				requestSoapMessageContains,				
				Set.of(CONNETTORE_0,CONNETTORE_1,CONNETTORE_3),
				Set.of(CONNETTORE_2),
				ESITO_CONSEGNA_MULTIPLA_FALLITA)
			);
		*/
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
			Common.checkAll200(responses);
			CommonConsegnaMultipla.checkPresaInConsegna(responses);
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
		}
		
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
	public void cadenzaRispedizione1_1() {
		cadenzaRispedizione_Impl(HttpConstants.CONTENT_TYPE_SOAP_1_1);
	}

	
	@Test
	public void cadenzaRispedizione1_2() {
		cadenzaRispedizione_Impl(HttpConstants.CONTENT_TYPE_SOAP_1_2);
	}
	
	@Test
	public void connettoreRotto1_1() {
		connettoreRotto_Impl(HttpConstants.CONTENT_TYPE_SOAP_1_1);
	}
	
	@Test
	public void connettoreRotto1_2() {
		connettoreRotto_Impl(HttpConstants.CONTENT_TYPE_SOAP_1_2);
	}
	
	private void connettoreRotto_Impl(String soapContentType) {
		final String erogazione = "TestConsegnaMultiplaConnettoreRotto";
		
		// Va come il primo test, ci si aspetta che funzionino tutti i connettori
		// eccetto quello rotto, dove devono avvenire le rispedizioni.
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType );
		
		var responses = Common.makeParallelRequests(request, 10);
   	   Common.checkAll200(responses);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
   	   	Set<String> connettoriSchedulati = new HashSet<>(Common.setConnettoriAbilitati);
   	   	connettoriSchedulati.add(CONNETTORE_ROTTO);
   	   	
		for (var r : responses) {
			CommonConsegnaMultipla.checkPresaInConsegna(r,connettoriSchedulati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettoriSchedulati);
		}
	
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
	
		// La consegna deve risultare ancora in corso per via dell'errore sul connettore rotto, con un connettore
		// in rispedizione
		for (var r : responses) {
			CommonConsegnaMultipla.checkStatoConsegna(r, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
		}
		
	}


	
	private void cadenzaRispedizione_Impl(String soapContentType) {
		// La configurazione della API è come quella del primo test: TestConsegnaMultipla
		// L'unica differenza è sul Connettore0 dove è stato impostato un intervallo di rispedizione pari ad un minuto.
		// Il test dunque controlla che in caso di mancata consegna, per gli altri connettori avviene il reinvio dopo "intervalloControlloFallite",
		// per il Connettore0 invece il reinvio avviene dopo un minuto + intervalloControlloFallite
		
		final String erogazione = "TestConsegnaMultiplaRispedizione";
		
		
		Set<String> connettoriSuccessoRequest = Set.of();
		Set<String> connettoriFallimentoRequest = Common.setConnettoriAbilitati;
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		for(int i=0; i<10;i++) {
			HttpRequest request5xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType );
			request5xx.setUrl(request5xx.getUrl()+"&returnCode=" + (500+i));
			requestsByKind.add(new RequestAndExpectations(request5xx, connettoriSuccessoRequest, connettoriFallimentoRequest, ESITO_CONSEGNA_MULTIPLA_IN_CORSO));
		}		
		
		for(int i=0; i<10;i++) {
			HttpRequest request2xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType );
			request2xx.setUrl(request2xx.getUrl()+"&returnCode=" + (200+i));
			requestsByKind.add(new RequestAndExpectations(request2xx, Common.setConnettoriAbilitati, Set.of(), ESITO_CONSEGNA_MULTIPLA_COMPLETATA ));
		}
		
		for(int i=0; i<10;i++) {
			HttpRequest request4xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType );
			request4xx.setUrl(request4xx.getUrl()+"&returnCode=" + (400+i));
			requestsByKind.add(new RequestAndExpectations(request4xx, connettoriSuccessoRequest, connettoriFallimentoRequest, ESITO_CONSEGNA_MULTIPLA_IN_CORSO));
		}
		
		HttpRequest requestSoapFault =  RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", soapContentType );
		requestsByKind.add(new RequestAndExpectations(requestSoapFault, Common.setConnettoriAbilitati, Set.of(),  ESITO_CONSEGNA_MULTIPLA_FALLITA ));
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
		   	Common.checkAll200(responses);
			CommonConsegnaMultipla.checkPresaInConsegna(responses);
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
		}
		
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2 * CommonConsegnaMultipla.intervalloControllo);

		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				CommonConsegnaMultipla.checkRequestExpectations(requestAndExpectation, response);
			}
		}
		
		// Attendo una riconsegna, per ogni richiesta andata male, solo il connettore0 non deve avere effettuato rispedizioni
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControlloFallite);
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {

				for (var connettore : requestAndExpectation.connettoriFallimento) {
					if (connettore.equals(CONNETTORE_0) ) {
						assertTrue(	getNumeroTentativiSchedulingConnettore(response, connettore) == 1);
					} else {
						assertTrue(	getNumeroTentativiSchedulingConnettore(response, connettore) >= 2);
					}
				}

				for (var connettore : requestAndExpectation.connettoriSuccesso) {
					checkSchedulingConnettoreCompletato(response, connettore);
				}
				
				checkStatoConsegna(response, requestAndExpectation.esito, requestAndExpectation.connettoriFallimento.size());
			}
		}
		
		// Attendo un minuto, anche il connettore 0 ora deve avere una rispedizione in più
		org.openspcoop2.utils.Utilities.sleep(1000*60);
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {

				for (var connettore : requestAndExpectation.connettoriFallimento) {
					if (connettore.equals(CONNETTORE_0) ) {
						assertTrue(	getNumeroTentativiSchedulingConnettore(response, connettore) == 2);
					} else {
						assertTrue(	getNumeroTentativiSchedulingConnettore(response, connettore) >= 2);
					}
				}

				for (var connettore : requestAndExpectation.connettoriSuccesso) {
					checkSchedulingConnettoreCompletato(response, connettore);
				}
				
				checkStatoConsegna(response, requestAndExpectation.esito, requestAndExpectation.connettoriFallimento.size());
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
