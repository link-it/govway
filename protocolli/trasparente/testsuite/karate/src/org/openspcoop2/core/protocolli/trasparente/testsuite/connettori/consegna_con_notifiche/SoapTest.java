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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_con_notifiche;

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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkSchedulingConnettoreCompletato;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkStatoConsegna;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.getNumeroTentativiSchedulingConnettore;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.setDifference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectations;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilsException;

// TODO:  Test errore di processamento facendo fallire la correlazione applicativa sulla risposta, faccio prima rest e poi torno qui, chiedi ad andrea se la correlazione applicativa 
//		si attiva solo sull'azione scelta.


// Completate Con Successo -> 2xx
// FaultApplicativo: fault=true&faultSoapVersion="+faultSoapVersion Oppure "problem=true" in REST
// ErroreDiConsegna -> 4xx e anche 5xx ( > 500 )
// Errore Di Processamento --> Segui mail di andrea registrando una correlazione applicativa sulla risposta

/**
 * 
 * @author Francesco Scarlato
 *
 */
public class SoapTest extends ConfigLoader {

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
		Common.fermaRiconsegne(dbUtils);
	}

	@org.junit.After
	public void AfterEach() {
		Common.fermaRiconsegne(dbUtils);
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
		// Se qualsiasi connettore accetta uno status code non 2xx, la consegna multipla va segnata come fallita.
		if ( ( statusCode < 200 || statusCode > 299) && !connettoriOk.isEmpty()) {
			esito = ESITO_CONSEGNA_MULTIPLA_FALLITA;
		} else {
			esito =  connettoriErrore.isEmpty() ? ESITO_CONSEGNA_MULTIPLA_COMPLETATA : ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
		}
		
		return new RequestAndExpectations(request, connettoriOk, connettoriErrore, esito, statusCode);
	}
	


	@Test
	public void consegnaConNotificheSemplice() throws IOException {

		// Il ConnettorePrincipale è quello sincrono, non subisce rispedizioni in caso di fallimento.
		// Non controllo lo status code delle richieste sincrone perchè non coincide con quello inviato al server di echo, e.g.: un 401 viene trasformato in 500
		// Controllo però che la logica delle rispedizioni segua quanto scelto nelle maschere di configurazione.
		// Così per tutti i test.
		
		final String erogazione = "TestConsegnaConNotificheSoap";
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);

		var responses = Common.makeParallelRequests(request1, 10);
		var responses2 = Common.makeParallelRequests(request2, 10);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		// Non controllo la valorizzazione puntuale del campo esito_sincrono, perchè dovrei costruirmi un mapping e sapere
		// dato ciascuno  status code in quale dei tanti esiti si va a finire.
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}
		for (var r : responses2) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}
	
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		Set<String> connettoriFile = Set.of(CONNETTORE_2, CONNETTORE_3);
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkConsegnaConnettoreFile(request1, response, connettoriFile);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  Common.setConnettoriAbilitati);
		}
		for (var response : responses2) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkConsegnaConnettoreFile(request2, response, connettoriFile);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  Common.setConnettoriAbilitati);
		}

	}
	

	
	@Test
	public void schedulingAbilitatoDisabilitato() throws UtilsException, HttpUtilsException {
		final String erogazione = "TestConsegnaConNotificheSchedulingAbilitatoDisabilitatoSoap";
		
		CommonConsegnaMultipla.jmxDisabilitaSchedulingConnettore(erogazione, Common.CONNETTORE_1);
		
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		
		var responses = Common.makeParallelRequests(request1, 10);
		var responses2 = Common.makeParallelRequests(request2, 10);
		Common.checkAll200(responses);
		Common.checkAll200(responses2);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			CommonConsegnaMultipla.checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
		}
		
		for (var r : responses2) {
			CommonConsegnaMultipla.checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
		}
		
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
		
		for (var response : responses) {
			// Sul connettore disabilitato non è avvenuto nulla ancora.
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(response, Common.CONNETTORE_1);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3);
			CommonConsegnaMultipla.checkStatoConsegna(response, ESITO_CONSEGNA_MULTIPLA_IN_CORSO,  1);
		}
		
		for (var response : responses2) {
			// Sul connettore disabilitato non è avvenuto nulla ancora.
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(response, Common.CONNETTORE_1);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3);
			CommonConsegnaMultipla.checkStatoConsegna(response, ESITO_CONSEGNA_MULTIPLA_IN_CORSO,  1);
		}
				
		CommonConsegnaMultipla.jmxAbilitaSchedulingConnettore(erogazione, Common.CONNETTORE_1);
		
		// Attendo la consegna sul connettore appena abilitato
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
		
		for (var response : responses) {
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_1);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3);
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
		}
		
		for (var response : responses2) {
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_1);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3);
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
		}
		
	}

	
	@Test
	public void consegnaConNotificheNonCondizionale() {
		// Non c'è la condizionalità sulla transazione sincrona, per cui si comporta come una consegna multipla classica
		final String erogazione = "TestConsegnaConNotificheNonCondizionaleSoap";
		
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
		   	//Common.checkAll200(responses);
			CommonConsegnaMultipla.checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size());
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
	public void consegnaConNotificheSemplice2() {
		// Errore di Consegna => Spedizione
		// CompletateConSuccesso, FaultApplicativo => Errore
		
		final String erogazione = "TestConsegnaConNotifiche2Soap";

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		// 401, principale superata, scheduling fallito su tutti i connettori

		Set<String> connettoriSuccesso = Set.of();
		var connettoriErrore = Common.setConnettoriAbilitati;
		var requestAndExpectation = buildRequestAndExpectations(erogazione, 401, connettoriSuccesso, connettoriErrore, HttpConstants.CONTENT_TYPE_SOAP_1_1);
		requestsByKind.add(requestAndExpectation);
		
		
		// Fault applicativo, principale fallita, nessuno scheduling 
		HttpRequest requestSoapFault = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla", "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		requestAndExpectation = new RequestAndExpectations(
					requestSoapFault,
					Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
					Set.of(CONNETTORE_1),  
					0, 
					500,
					false);
		requestsByKind.add(requestAndExpectation);
		
		// 200, principale fallita, nessuno scheduling
		requestAndExpectation = buildRequestAndExpectations(erogazione,200, Set.of(), Set.of(), HttpConstants.CONTENT_TYPE_SOAP_1_1);
		requestAndExpectation.principaleSuperata = false;
		requestsByKind.add(requestAndExpectation);

		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		// Le richieste per cui la transazione sincrona ha avuto successo devono essere schedulate
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			
			if (requestExpectation.principaleSuperata) {
				CommonConsegnaMultipla.checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size());
				CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
			} else {
				for (var response : responses) {
					CommonConsegnaMultipla.checkNessunaNotifica(response);
					CommonConsegnaMultipla.checkNessunoScheduling(response);
				}
			}
		}
		
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
		
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			
			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					CommonConsegnaMultipla.checkRequestExpectations(requestExpectation, response);
				}
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControlloFallite);
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);

			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					CommonConsegnaMultipla.checkRequestExpectationsFinal(requestExpectation, response);
				}
			}
		}
		
	}
	
	
	@Test
	public void erroreTransazionePrincipale() {
		/* In questo test conrollo che non avvenga la spedizione nel caso di errori sulla transazione principale */
		
		final String erogazione = "TestConsegnaConNotificheSoap";
		
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request1.setUrl(request1.getUrl()+"&returnCode=401");
		
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		request2.setUrl(request2.getUrl()+"&returnCode=401");
		
		var responses = Common.makeParallelRequests(request1, 10);
		var responses2 = Common.makeParallelRequests(request2, 10);
		
		for (var r : responses) {
			// Le richieste che vedono fallita la transazione principale con un 401, ottengono un 500
			assertEquals(500 , r.getResultHTTPOperation());
			CommonConsegnaMultipla.checkNessunaNotifica(r);
			CommonConsegnaMultipla.checkNessunoScheduling(r);
		}
		
		for (var r : responses2) {
			// Le richieste che vedono fallita la transazione principale, ottengono un 500
			assertEquals(500 , r.getResultHTTPOperation());
			CommonConsegnaMultipla.checkNessunaNotifica(r);
			CommonConsegnaMultipla.checkNessunoScheduling(r);
		}
	}
	
	
	@Test
	public void varieCombinazioniDiRegole() {
		// La consegna sincrona ha successo in caso di 200 o di soap fault. In questo test alcune transazioni
		// principali falliscono, altre no e per queste viene schedulata la consegna. Controllo che tale scheduling sia corretto.
		final String erogazione = "TestConsegnaConNotificheSoapVarieCombinazioniDiRegole";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		int i = 0;
		for (var entry : CommonConsegnaMultipla.statusCodeVsConnettori.entrySet()) {
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			int statusCode = entry.getKey();
			var connettoriSuccesso = entry.getValue();
			var connettoriErrore = setDifference(Common.setConnettoriAbilitati,connettoriSuccesso);
			var requestExpectation = buildRequestAndExpectations(erogazione, statusCode, connettoriSuccesso, connettoriErrore, soapContentType);
			
		
			if (statusCode >= 400 && statusCode <= 499) {
				requestExpectation.principaleSuperata = false;
			}
			
			// In Soap un 500 senza soapFault è considerato Ok con anomialia, quindi mi aspetto un errore nella transazione principale
			// solo in caso di 4xx e 5xx, con 5xx > 500
			if (statusCode > 500 && statusCode <= 599) {
				requestExpectation.principaleSuperata = false;	
			}
			
			requestsByKind.add(requestExpectation);
			i++;
		}
		
		// Le soap fault passano
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

		// Le richieste per cui la transazione sincrona ha avuto successo devono essere schedulate
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			
			if (requestExpectation.principaleSuperata) {
				CommonConsegnaMultipla.checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size());
				CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
			} else {
				for (var response : responses) {
					CommonConsegnaMultipla.checkNessunaNotifica(response);
					CommonConsegnaMultipla.checkNessunoScheduling(response);
				}
			}
		}
		
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
		
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			
			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					CommonConsegnaMultipla.checkRequestExpectations(requestExpectation, response);
				}
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControlloFallite);
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);

			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					CommonConsegnaMultipla.checkRequestExpectationsFinal(requestExpectation, response);
				}
			}
		}
		
	}
	
	
	@Test
	public void cadenzaRispedizione() {
			// La configurazione della API è come quella del primo test: TestConsegnaMultipla
			// L'unica differenza è sul Connettore0 dove è stato impostato un intervallo di rispedizione pari ad un minuto.
			// Il test dunque controlla che in caso di mancata consegna, per gli altri connettori avviene il reinvio dopo "intervalloControlloFallite",
			// per il Connettore0 invece il reinvio avviene dopo un minuto + intervalloControlloFallite
			
			final String erogazione = "TestConsegnaConNotificheRispedizioneSoap";
			
			Set<String> connettoriSuccessoRequest = Set.of();
			Set<String> connettoriFallimentoRequest = Common.setConnettoriAbilitati;
			
			List<RequestAndExpectations> requestsByKind = new ArrayList<>();
			
			for(int i=0; i<10;i++) {
				final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
				int statusCode = 500+i;
				HttpRequest request5xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType);
				request5xx.setUrl(request5xx.getUrl()+"&returnCode=" + statusCode);
				requestsByKind.add(new RequestAndExpectations(request5xx, connettoriSuccessoRequest, connettoriFallimentoRequest, ESITO_CONSEGNA_MULTIPLA_IN_CORSO,statusCode));

				statusCode = 200+i;
				HttpRequest request2xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType);
				request2xx.setUrl(request2xx.getUrl()+"&returnCode=" + statusCode);
				requestsByKind.add(new RequestAndExpectations(request2xx, Common.setConnettoriAbilitati, Set.of(), ESITO_CONSEGNA_MULTIPLA_COMPLETATA, statusCode));

				statusCode = 400+i;
				HttpRequest request4xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType);
				request4xx.setUrl(request4xx.getUrl()+"&returnCode=" + statusCode);
				requestsByKind.add(new RequestAndExpectations(request4xx, connettoriSuccessoRequest, connettoriFallimentoRequest, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, statusCode));
			}
			
			HttpRequest requestSoapFault =  RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
			requestsByKind.add(new RequestAndExpectations(requestSoapFault, Common.setConnettoriAbilitati, Set.of(),  ESITO_CONSEGNA_MULTIPLA_FALLITA));
			
			requestSoapFault =  RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
			requestsByKind.add(new RequestAndExpectations(requestSoapFault, Common.setConnettoriAbilitati, Set.of(),  ESITO_CONSEGNA_MULTIPLA_FALLITA));
			
			// Configuro gli esiti per la richiesta sincrona
			for (var requestExpectation : requestsByKind) {
				int statusCode = requestExpectation.statusCodePrincipale;
				
				// Il 204 toglie il body dalla risposta, in soap non è ammesso uno status code ok senza body
				if (statusCode == 204) {
					requestExpectation.principaleSuperata = false;
				}
				
				if (statusCode >= 400 && statusCode <= 499) {
					requestExpectation.principaleSuperata = false;
				}
				
				// In Soap un 500 senza soapFault è considerato Ok con anomialia, quindi mi aspetto un errore nella transazione principale
				// solo in caso di 4xx e 5xx, con 5xx > 500
				if (statusCode > 500 && statusCode <= 599) {
					requestExpectation.principaleSuperata = false;	
				}
			}
			
			Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
			
			// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
			// Solo Le richieste per cui la transazione sincrona ha avuto successo devono essere schedulate
			for (var requestExpectation : responsesByKind.keySet()) {
				var responses = responsesByKind.get(requestExpectation);
				
				if (requestExpectation.principaleSuperata) {
					CommonConsegnaMultipla.checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size());
					CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
				} else {
					for (var response : responses) {
						CommonConsegnaMultipla.checkNessunaNotifica(response);
						CommonConsegnaMultipla.checkNessunoScheduling(response);
					}
				}
			}
			// Attendo la consegna
			org.openspcoop2.utils.Utilities.sleep(2 * CommonConsegnaMultipla.intervalloControllo);

			// Adesso devono essere state effettuate le consegne
			for (var requestExpectation : responsesByKind.keySet()) {
				var responses = responsesByKind.get(requestExpectation);
				
				if (requestExpectation.principaleSuperata) {
					for (var response : responses) {
						CommonConsegnaMultipla.checkRequestExpectations(requestExpectation, response);
					}
				}
			}
			
			// Attendo una riconsegna, per ogni richiesta andata male, solo il connettore0 non deve avere effettuato rispedizioni			
			org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControlloFallite);			
			for (var requestAndExpectation : responsesByKind.keySet()) {
				if (requestAndExpectation.principaleSuperata ) {
				
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
			}
			
			// Attendo un minuto, anche il connettore 0 ora deve avere una rispedizione in più
			org.openspcoop2.utils.Utilities.sleep(1000*60);
			for (var requestAndExpectation : responsesByKind.keySet()) {
				if (requestAndExpectation.principaleSuperata) {
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
		}
	
	
	@Test
	public void connettoreRotto() {
		final String erogazione = "TestConsegnaConNotificheConnettoreRottoSoap";
		
		// Va come il primo test, ci si aspetta che funzionino tutti i connettori
		// eccetto quello rotto, dove devono avvenire le rispedizioni.
		
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);

		var responses = Common.makeParallelRequests(request1, 10);
		var responses2 = Common.makeParallelRequests(request2, 10);
		
		Common.checkAll200(responses);
		Common.checkAll200(responses2);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna		
   	   	Set<String> connettoriSchedulati = new HashSet<>(Common.setConnettoriAbilitati);
   	   	connettoriSchedulati.add(CONNETTORE_ROTTO);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		// Non controllo la valorizzazione puntuale del campo esito_sincrono, perchè dovrei costruirmi un mapping e sapere
		// dato ciascuno  status code in quale dei tanti esiti si va a finire.
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			CommonConsegnaMultipla.checkPresaInConsegna(r, connettoriSchedulati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettoriSchedulati);	
		}
		for (var r : responses2) {
			assertEquals(200, r.getResultHTTPOperation());
			CommonConsegnaMultipla.checkPresaInConsegna(r, connettoriSchedulati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettoriSchedulati);	
		}
	
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(3*CommonConsegnaMultipla.intervalloControllo);
	
		// La consegna deve risultare ancora in corso per via dell'errore sul connettore rotto, con un connettore
		// in rispedizione
		for (var r : responses) {
			CommonConsegnaMultipla.checkStatoConsegna(r, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			CommonConsegnaMultipla.checkSchedulingConnettoreInCorso(r, CONNETTORE_ROTTO);
			for (var connettore : Common.setConnettoriAbilitati) {
				CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(r, connettore);
			}
		}
		
		for (var r : responses2) {
			CommonConsegnaMultipla.checkStatoConsegna(r, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			CommonConsegnaMultipla.checkSchedulingConnettoreInCorso(r, CONNETTORE_ROTTO);
			for (var connettore : Common.setConnettoriAbilitati) {
				CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(r, connettore);
			}
		}
	}
			

		
}