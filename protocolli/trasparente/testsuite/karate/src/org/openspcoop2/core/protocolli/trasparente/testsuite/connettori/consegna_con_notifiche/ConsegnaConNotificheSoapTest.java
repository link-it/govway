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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_0;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_3;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_COMPLETATA;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_FALLITA;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.setDifference;

//import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.statusCodeVsConnettori;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectations;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;

// TODO: Test scelta connettore principale quello disabilitato.

// Completate Con Successo -> 2xx
// FaultApplicativo: fault=true&faultSoapVersion="+faultSoapVersion Oppure "problem=true" in REST
// ErroreDiConsegna -> 4xx
// Errore Di Processamento --> ??	 NON FARLO, TRALASCIALO
// RichiesteScartate --> Errore Autenticazione

/**
 * 
 * @author Francesco Scarlato
 *
 * 
 * QUERY:
 * 
 * TestContenuto: //Filtro/text()
 * Template: ${query:govway-testsuite-id_connettore_request}
 * Freemarker: ${query["govway-testsuite-id_connettore_request"]}
 * Velocity:		#if($query.containsKey("govway-testsuite-id_connettore_request"))
							$query["govway-testsuite-id_connettore_request"]
						#end
 * UrlInvocazione: .+govway-testsuite-id_connettore_request=([^&]*).*
 */
public class ConsegnaConNotificheSoapTest extends ConfigLoader {

	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
		File cartellaRisposte = CommonConsegnaMultipla.connettoriFilePath.toFile();
		if (!cartellaRisposte.isDirectory()|| !cartellaRisposte.canWrite()) {
			throw new RuntimeException("E' necessario creare la cartella per scrivere le richieste dei connettori, indicata dalla poprietà: <connettori.consegna_multipla.connettore_file.path> ");
		}
	}
	
	/*@AfterClass
	public static void After() {
		Common.fermaRiconsegne(dbUtils);
	}*/
	
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
		
		return new RequestAndExpectations(request, connettoriOk, connettoriErrore, esito, statusCode);
	}
	
	
	@Test
	public void notificheCondizionali1() {
		// Notifiche avvengono se: Completate Con Successo, Fault Applicativo.
	}
	
	@Test
	public void notificheCondizionali2() {
		// Notifiche condizionali: Errore di Consegna, Richieste Scartate
	}
	
	
	
	@Test
	public void consegnaConNotificheSemplice() throws IOException {

		
		// Il ConnettorePrincipale è quello sincrono, non subisce rispedizioni in caso di fallimento.
		// La sua configurazione puntuale serve a indicare cosa scrivere nei diagnostici.		(TODOs Controlla)
		//  La configurazione sugli esiti invece guarda all'esito della transazione principale e in base a quell'esito
		//		partono le notifiche come per i test scritti per la consegna multipla
		
		final String erogazione = "TestConsegnaConNotificheSoap";
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);

		var responses = Common.makeParallelRequests(request1, 10);
		var responses2 = Common.makeParallelRequests(request2, 10);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
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
	public void erroreTransazionePrincipale() {
		/* In questo test conrollo che non avvenga la spedizione nel caso di errori sulla transazione principale */
		
		// Se la consegna multipla fosse avvenuta, l'esito non sarebbe più 10, ma quell'esito andrebbe in 'esito_sincrono (TODO controlla)
		
		final String erogazione = "TestConsegnaConNotificheSoap";
		
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request1.setUrl(request1.getUrl()+"&returnCode=401");
		
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		request2.setUrl(request2.getUrl()+"&returnCode=401");
		
		var responses = Common.makeParallelRequests(request1, 10);
		var responses2 = Common.makeParallelRequests(request2, 10);
		
		for (var r : responses) {
			// Le richieste che vedono fallita la transazione principale, ottengono un 500
			// 29 = Esito4xx
			assertEquals(500 , r.getResultHTTPOperation());
			CommonConsegnaMultipla.checkNessunaConsegna(r);
			CommonConsegnaMultipla.checkNessunoScheduling(r);
		}
		
		for (var r : responses2) {
			// Le richieste che vedono fallita la transazione principale, ottengono un 500
			// 29 = Esito4xx
			assertEquals(500 , r.getResultHTTPOperation());
			CommonConsegnaMultipla.checkNessunaConsegna(r);
			CommonConsegnaMultipla.checkNessunoScheduling(r);
		}
	}
	
	
	@Test
	public void varieCombinazioniDiRegole() {
		// La consegna sincrona ha successo in caso di 200 o di soap fault
		final String erogazione = "TestConsegnaConNotificheSoapVarieCombinazioniDiRegole";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		int i = 0;
		for (var entry : CommonConsegnaMultipla.statusCodeVsConnettori.entrySet()) {
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			int statusCode = entry.getKey();
			var connettoriSuccesso = entry.getValue();
			var connettoriErrore = setDifference(Common.setConnettoriAbilitati,connettoriSuccesso);
			var requestExpectation = buildRequestAndExpectations(erogazione, statusCode, connettoriSuccesso, connettoriErrore, soapContentType);
			
			// In Soap un 500 senza soapFault è considerato Ok con anomialia, quindi mi aspetto un errore nella transazione principale
			// solo in caso di 4xx e 5xx, con 5xx > 500
			if (statusCode >= 400 && statusCode <= 499) {
				requestExpectation.principaleSuperata = false;
			}
			if (statusCode > 500 && statusCode <= 599) {
				requestExpectation.principaleSuperata = false;	
			}
			
			// Ogni 5 richieste ne faccio sbagliare una sull'autenticazione
			if ( i % 5 == 0) {
				requestExpectation.request.getHeadersValues().remove("Authorization");
				requestExpectation.principaleSuperata = false;
				requestExpectation.statusCodePrincipale = 500;
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
			
			// TODO: al client non viene restituito lo statusCode del server di echo, ma viene manipolato.
			// A volte corrisponde, a volte no, come nel caso di un 401 che viene trasformato in 500 per cui questi controlli non so farli
			// Puoi però analizzare sul db il return code della transazione se ti interessa.	
			if (requestExpectation.principaleSuperata) {
				CommonConsegnaMultipla.checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size());
				CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
			} else {
				for (var response : responses) {
					CommonConsegnaMultipla.checkNessunaConsegna(response);
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
	public void alcuniErroriTransazionePrincipale() throws IOException {
		/**
		 * In questo test riunisco le varie funzionalità. Alcune richieste sincrone avranno successo altre no.
		 */
		final String erogazione = "TestConsegnaConNotificheSoap";
		
		
		String soapContentType = "";
		//List.of("returnCode=200", "returnCode=401", "soap")
		HttpRequest requestCompletataConSuccesso = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType);
		HttpRequest requestFaultApplicativo = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", soapContentType);
		HttpRequest requestErroreDiConsegna =  RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType);
		requestErroreDiConsegna.setUrl(requestErroreDiConsegna.getUrl()+"&returnCode=401");
		
		// Questa richiesta non avrà l'autenticazione
		HttpRequest requestScartata = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType);
		requestScartata.getHeadersValues().remove("Authorization");
		
		

		
		
		for (int i=0;i<10;i++) {
			soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType);

		}
		
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request1.setUrl(request1.getUrl()+"&returnCode=401");
		
	}
	
	
	
	@Test
	public void consegnaConNotificheErroriCompleta() throws IOException {
		/**
		 * In questo test riunisco le varie funzionalità. Alcune richieste sincrone avranno successo altre no.
		 * Inoltre ci saranno anche errori di consegna per i connettori e verificherò le rispedizioni.
		 *  
		 */
		
		
	}
	
	
	@Test
	public void schemaConsegnaConNotifiche() {
		final String erogazione ="";
		
		
		// Il connettore di fallback è il 2

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : CommonConsegnaMultipla.statusCodeVsConnettori.entrySet()) {
			
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(Common.HEADER_ID_CONDIZIONE, Common.filtriPools.get(pool).get(0));
			
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Request consegna sincrona fallita, per questa richiesta non deve essere schedulata la consegna
			// sugli altri connettori
			request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			//request.setCampiPerFarlaSbagliareDataLErogazione(); TODO
			current = new RequestAndExpectations(request, Set.of(), Set.of(), entry.getKey(), 500);
			requestsByKind.add(current);
			
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// TODO: Riarrangia questo per controllare che le richieste sincrone non vedano scheduling
		CommonConsegnaMultipla.checkResponses(responsesByKind);
	}

}
