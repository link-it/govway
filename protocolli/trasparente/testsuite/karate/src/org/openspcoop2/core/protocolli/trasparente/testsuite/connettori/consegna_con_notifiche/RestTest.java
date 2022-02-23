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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_OK;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.FORMATO_FAULT_REST;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.FORMATO_FAULT_SOAP1_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkStatoConsegna;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.esitoConsegnaFromStatusCode;
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
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectationsFault;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectations.TipoFault;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilsException;

/**
 * 
 * 
 * 
 * @author Francesco Scarlato
 *
 */
public class RestTest extends ConfigLoader{

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
	
	
	public static RequestAndExpectations buildRequestAndExpectations(String erogazione, int statusCode, Set<String> connettoriOk, Set<String> connettoriErrore) {
		HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		
		int esito;
		// Se qualsiasi connettore accetta uno status code non 2xx, la consegna multipla va segnata come fallita.
		if ( ( statusCode < 200 || statusCode > 299) && !connettoriOk.isEmpty()) {
			esito = ESITO_CONSEGNA_MULTIPLA_FALLITA;
		} else {
			esito =  connettoriErrore.isEmpty() ? ESITO_CONSEGNA_MULTIPLA_COMPLETATA : ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
		}
		
		return new RequestAndExpectations(request, connettoriOk, connettoriErrore, esito, statusCode, true);
	}
	
	@Test
	public void valorizzazioneCampi() throws IOException {

		/**
		 *  * TODO: SOLO UN TEST.
 * Sui test nei quale c'è una cadenza rispedizione testare anche che i campi
 * data_uscita_richiesta          | 2022-02-15 19:20:34.684
 *data_accettazione_risposta     | 2022-02-15 19:20:34.698
 * data_ingresso_risposta         | 2022-02-15 19:20:34.698
 *	Vengano aggiornati.
 *
 *  data_primo_tentativo           | 2022-02-15 19:20:34.684	Deve rimanere uguale

 * data_ultimo_errore             | controlla solo questa e che gli altri siano e restino valorizzati 
dettaglio_esito_ultimo_errore  | 0
codice_risposta_ultimo_errore  | 
ultimo_errore                  | 
location_ultimo_errore         | 
cluster_id_ultimo_errore       | 
fault_ultimo_errore            | 
formato_fault_ultimo_errore    | 
		 */
		
		final String erogazione = "TestConsegnaConNotificheRest";
		HttpRequest request1 = RequestBuilder.buildRestRequest(erogazione);
		
		HttpRequest requestKo = RequestBuilder.buildRestRequest(erogazione);
		requestKo.setUrl(requestKo.getUrl()+"&returnCode=402");

		var responses = Common.makeParallelRequests(request1, 10);
		var responsesKo = Common.makeParallelRequests(requestKo, 10);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
			
			for (var connettore : Common.setConnettoriAbilitati) {
				String query = "select count(*) from transazioni_sa where id_transazione=? and connettore_nome = ?  and consegna_terminata = ? and numero_tentativi = 0 and identificativo_messaggio is not null";
				String id_transazione = r.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				ConfigLoader.getLoggerCore().info("Checking scheduling connettore iniziato for transazione:  " + id_transazione + " AND connettore = " + connettore);
				Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, false);
				assertEquals(Integer.valueOf(1), count);
			}
			
		}
		
		for (var r : responsesKo) {
			assertEquals(402, r.getResultHTTPOperation());	
			CommonConsegnaMultipla.checkNessunaNotifica(r);
			CommonConsegnaMultipla.checkNessunoScheduling(r);
		}

		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		Set<String> connettoriFile = Set.of(CONNETTORE_2, CONNETTORE_3);
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkConsegnaConnettoreFile(request1, response, connettoriFile);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  Common.setConnettoriAbilitati, ESITO_OK, "", "");
		}
	}
	
	
	@Test
	public void regoleRispedizioneProblem() {
		/**
		 * Qui si testano le varie regole di rispedizione per i singoli connettori tenendo conto dei campi del Problem.
		 * Connettore0, Connettore1:  Consegna Fallita Personalizzata
		 * Connettore2, Connettore3: Consegna  Completata Personalizzata
		 */
		final String erogazione = "TestConsegnaConNotificheRegoleProblem";
		//  Per generare Problem Detail (REST):
        // problem=true
        //è possibile anche personalizzare aspetti del fault quali:
         //problemStatus, problemTitle, problemType, problemDetail e problemSerializationType (json/xml)
		// claimCompletata=valoreClaimCompletata
		// claimRispedizione=valoreClaimRispedizione

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		HttpRequest requestRispeditaTuttiValorizzati = new HttpRequest();
		requestRispeditaTuttiValorizzati.setMethod(HttpRequestMethod.POST);
		requestRispeditaTuttiValorizzati.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&problem=true&problemStatus=501&problemType=TypeRispedita");
		
		String content = "{ \"claimRispedizione\": \"valoreClaimRispedizione\"}";
		requestRispeditaTuttiValorizzati.setContentType("application/json");
		requestRispeditaTuttiValorizzati.setContent(content.getBytes());
		
		HttpRequest requestRispeditaRegex = new HttpRequest();
		requestRispeditaRegex.setMethod(HttpRequestMethod.GET);
		requestRispeditaRegex.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&problem=true&problemStatus=502&problemType=TypeRispedita123");
		
		HttpRequest requestCompletataTuttiValorizzati =  new HttpRequest();
		requestCompletataTuttiValorizzati.setMethod(HttpRequestMethod.POST);
		requestCompletataTuttiValorizzati.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&problem=true&problemStatus=501&problemType=TypeCompletata");
		content = "{ \"claimCompletata\": \"valoreClaimCompletata\"}";
		requestCompletataTuttiValorizzati.setContentType("application/json");
		requestCompletataTuttiValorizzati.setContent(content.getBytes());
		
		HttpRequest requestCompletataRegex = new HttpRequest();
		requestCompletataRegex.setMethod(HttpRequestMethod.GET);
		requestCompletataRegex.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&problem=true&problemStatus=502&problemType=TypeCompletata123");
		
		String  fault = "{\"type\":\"TypeRispedita\",\"title\":\"Not Implemented\",\"status\":501,\"detail\":\"Problem ritornato dalla servlet di trace, esempio di OpenSPCoop\"}";
		
	/*	requestsByKind.add(new RequestAndExpectationsFault(
				requestRispeditaTuttiValorizzati,	
				Set.of(),
				Set.of(CONNETTORE_0,CONNETTORE_2, CONNETTORE_3,CONNETTORE_1),
				ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 501, TipoFault.REST, fault, FORMATO_FAULT_REST)
			);
	
		 
		 fault = "{\"type\":\"TypeRispedita123\",\"title\":\"Bad Gateway\",\"status\":502,\"detail\":\"Problem ritornato dalla servlet di trace, esempio di OpenSPCoop\"}";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestRispeditaRegex,
				Set.of(),
				Set.of(CONNETTORE_1,CONNETTORE_2,CONNETTORE_3, CONNETTORE_0), 
				ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 502, TipoFault.REST, fault, FORMATO_FAULT_REST)
			);*/
		
		 fault = "{\"type\":\"TypeCompletata\",\"title\":\"Not Implemented\",\"status\":501,\"detail\":\"Problem ritornato dalla servlet di trace, esempio di OpenSPCoop\"}";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestCompletataTuttiValorizzati,
				Set.of(CONNETTORE_2),
				Set.of(CONNETTORE_3, CONNETTORE_1, CONNETTORE_0), 
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 501, TipoFault.REST, fault, FORMATO_FAULT_REST)
			);
		
	
	/* fault = "{\"type\":\"TypeCompletata123\",\"title\":\"Bad Gateway\",\"status\":502,\"detail\":\"Problem ritornato dalla servlet di trace, esempio di OpenSPCoop\"}";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestCompletataRegex,				
				Set.of(CONNETTORE_3),
				Set.of(CONNETTORE_2, CONNETTORE_0,CONNETTORE_1),
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 502, TipoFault.REST, fault, FORMATO_FAULT_REST)
			);*/
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CondizionaleByFiltroRestTest.checkResponses(responsesByKind);
	}
	
	
	@Test
	public void schedulingAbilitatoDisabilitato() throws UtilsException, HttpUtilsException {
		final String erogazione = "TestConsegnaConNotificheSchedulingAbilitatoDisabilitatoRest";
		
		CommonConsegnaMultipla.jmxDisabilitaSchedulingConnettore(erogazione, Common.CONNETTORE_1);
		
		HttpRequest request1 = RequestBuilder.buildRestRequest(erogazione);
		
		var responses = Common.makeParallelRequests(request1, 10);
		Common.checkAll200(responses);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			CommonConsegnaMultipla.checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
		}
		
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
		
		String fault = "";
		String formatoFault = "";
		for (var response : responses) {
			// Sul connettore disabilitato non è avvenuto nulla ancora.
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(response, Common.CONNETTORE_1);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0, ESITO_OK, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2, ESITO_OK, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3, ESITO_OK, fault, formatoFault);
			CommonConsegnaMultipla.checkStatoConsegna(response, ESITO_CONSEGNA_MULTIPLA_IN_CORSO,  1);
		}
				
		CommonConsegnaMultipla.jmxAbilitaSchedulingConnettore(erogazione, Common.CONNETTORE_1);
		
		// Attendo la consegna sul connettore appena abilitato
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
		
		for (var response : responses) {
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_1, ESITO_OK, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0, ESITO_OK, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2, ESITO_OK, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3, ESITO_OK, fault, formatoFault);
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
		}
	}
	
	
	
	@Test
	public void consegnaConNotificheSemplice() throws IOException {

		// Il ConnettorePrincipale è quello sincrono, non subisce rispedizioni in caso di fallimento.
		// La sua configurazione puntuale serve a indicare cosa scrivere nei diagnostici.
		// Non controllo lo status code delle richieste sincrone perchè non coincide con quello inviato al server di echo, e.g.: un 401 viene trasformato in 500
		// Controllo però che la logica delle rispedizioni segua quanto scelto nelle maschere di configurazione.
		// Così per tutti i test.
		
		final String erogazione = "TestConsegnaConNotificheRest";
		HttpRequest request1 = RequestBuilder.buildRestRequest(erogazione);

		var responses = Common.makeParallelRequests(request1, 10);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
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
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  Common.setConnettoriAbilitati, ESITO_OK, "", "");
		}

	}
	
	
	@Test
	public void erroreDiProcessamentoOk() {
		// In questo caso l'errore di processamento è configurato per passare e far avviare le notifiche.
		final String erogazione = "TestConsegnaConNotificheErroreDiProcessamentoOKRest";
		
		// Questa va su test-regola-contenuto e la correlazione applicativa estrae dal contenuto l'id, per questa le notifiche vengono schedulate.
		HttpRequest requestOk = RequestBuilder.buildRequest_Contenuto("valoreIdApplicativo", erogazione) ;
		
		 //  Questa va su test-regola-contenuto e la correlazione applicativa fallisce perchè è una get semplice e non vi trova il contenuto
		// Per questa le notifiche saranno schedulate lo stesso
 		HttpRequest requestErroreProcessamentoOK = new HttpRequest(); 	
		requestErroreProcessamentoOK.setMethod(HttpRequestMethod.GET);
		requestErroreProcessamentoOK.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-contenuto"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);

		var responsesOk = Common.makeParallelRequests(requestOk, 10);
		var responsesErroreProcessamentoOK = Common.makeParallelRequests(requestErroreProcessamentoOK, 10);

		for (var r : responsesOk) {
			assertEquals(200, r.getResultHTTPOperation());
			CommonConsegnaMultipla.checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}
		
		for (var r : responsesErroreProcessamentoOK) {
			assertEquals(502, r.getResultHTTPOperation());
			CommonConsegnaMultipla.checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);						
		}

		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
		for (var r : responsesOk) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(r,  Common.setConnettoriAbilitati, ESITO_OK,  "", "");
		}
		for (var r : responsesErroreProcessamentoOK) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(r,  Common.setConnettoriAbilitati, ESITO_OK,  "", "");
		}
		
	}
	
	
	@Test
	public void erroreDiProcessamento() {
		final String erogazione = "TestConsegnaConNotificheErroreDiProcessamentoRest";
		
		HttpRequest requestStandard = RequestBuilder.buildRestRequest(erogazione);
		HttpRequest requestOk = RequestBuilder.buildRequest_Contenuto("valoreIdApplicativo", erogazione) ;		// Questa va su test-regola-contenuto e la correlazione applicativa estrae dal contenuto l'id, per questa le notifiche vengono schedulate.
		
		 //  Questa va su test-regola-contenuto e la correlazione applicativa fallisce perchè è una get semplice e non vi trova il contenuto Per questa le notifiche non saranno schedulate
 		HttpRequest requestErroreProcessamento = new HttpRequest(); 	
		requestErroreProcessamento.setMethod(HttpRequestMethod.GET);
		requestErroreProcessamento.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-contenuto"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);

		var responses 		= Common.makeParallelRequests(requestStandard, 10);
		var responsesOk = Common.makeParallelRequests(requestOk, 10);
		var responsesErroreProcessamento = Common.makeParallelRequests(requestErroreProcessamento, 10);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			CommonConsegnaMultipla.checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}
		
		for (var r : responsesOk) {
			assertEquals(200, r.getResultHTTPOperation());
			CommonConsegnaMultipla.checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}
		
		for (var r : responsesErroreProcessamento) {
			assertEquals(502, r.getResultHTTPOperation());
			CommonConsegnaMultipla.checkNessunaNotifica(r);
			CommonConsegnaMultipla.checkNessunoScheduling(r);						
		}

		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);

		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  Common.setConnettoriAbilitati, ESITO_OK,  "", "");
		}
		for (var r : responsesOk) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(r,  Common.setConnettoriAbilitati, ESITO_OK,  "", "");
		}
		
	}
	
	@Test
	public void consegnaConNotificheNonCondizionale() {
		// Non c'è la condizionalità sulla transazione sincrona, per cui si comporta come una consegna multipla classica
		final String erogazione = "TestConsegnaConNotificheNonCondizionaleRest";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		for (var entry : CommonConsegnaMultipla.statusCodeRestVsConnettori.entrySet()) {
			int statusCode = entry.getKey();
			var connettoriSuccesso = entry.getValue();
			var connettoriErrore = setDifference(Common.setConnettoriAbilitati,connettoriSuccesso);
			requestsByKind.add(buildRequestAndExpectations(erogazione, statusCode, connettoriSuccesso, connettoriErrore));			
		}
		
		HttpRequest requestProblem = RequestBuilder.buildRestRequestProblem(erogazione);
		requestsByKind.add(new RequestAndExpectations(
				requestProblem,
				Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
				Set.of(CONNETTORE_1),  
				ESITO_CONSEGNA_MULTIPLA_FALLITA,
				500,
				true,
				TipoFault.REST));
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
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
		
		final String erogazione = "TestConsegnaConNotifiche2Rest";

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		// 401, principale superata, scheduling fallito su tutti i connettori

		Set<String> connettoriSuccesso = Set.of();
		var connettoriErrore = Common.setConnettoriAbilitati;
		var requestAndExpectation = buildRequestAndExpectations(erogazione, 401, connettoriSuccesso, connettoriErrore);
		requestsByKind.add(requestAndExpectation);
		
		
		// Fault applicativo, principale fallita, nessuno scheduling 
		HttpRequest requestProblem = RequestBuilder.buildRestRequestProblem(erogazione);
		requestAndExpectation = new RequestAndExpectations(
					requestProblem,
					Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
					Set.of(CONNETTORE_1),  
					0, 
					500,
					false,
					TipoFault.REST);
		requestsByKind.add(requestAndExpectation);
		
		// 200, principale fallita, nessuno scheduling
		requestAndExpectation = buildRequestAndExpectations(erogazione,200, Set.of(), Set.of());
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
		
		final String erogazione = "TestConsegnaConNotificheRest";
		
		HttpRequest request1 = RequestBuilder.buildRestRequest(erogazione);
		request1.setUrl(request1.getUrl()+"&returnCode=401");
		
		var responses = Common.makeParallelRequests(request1, 10);
		
		for (var r : responses) {
			// Le richieste che vedono fallita la transazione principale con un 401, ottengono un 500
			assertEquals(401, r.getResultHTTPOperation());
			CommonConsegnaMultipla.checkNessunaNotifica(r);
			CommonConsegnaMultipla.checkNessunoScheduling(r);
		}
	}
	
	
	@Test
	public void varieCombinazioniDiRegole() {
		
		final String erogazione = "TestConsegnaConNotificheRestVarieCombinazioniDiRegole";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		for (var entry : CommonConsegnaMultipla.statusCodeRestVsConnettori.entrySet()) {
			int statusCode = entry.getKey();
			var connettoriSuccesso = entry.getValue();
			var connettoriErrore = setDifference(Common.setConnettoriAbilitati,connettoriSuccesso);
			var requestExpectation = buildRequestAndExpectations(erogazione, statusCode, connettoriSuccesso, connettoriErrore);
			
			if (statusCode >= 400 && statusCode <= 499) {
				requestExpectation.principaleSuperata = false;
			}
			if (statusCode >= 500 && statusCode <= 599) {
				requestExpectation.principaleSuperata = false;	
			}
			
			requestsByKind.add(requestExpectation);
		}
		
		// Le soap fault passano
		HttpRequest requestProblem = RequestBuilder.buildRestRequestProblem(erogazione);
		requestsByKind.add(new RequestAndExpectations(
				requestProblem,
				Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
				Set.of(CONNETTORE_1),  
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, true, TipoFault.REST ));
		
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
			
			final String erogazione = "TestConsegnaConNotificheRispedizioneRest";
			
			Set<String> connettoriSuccessoRequest = Set.of();
			Set<String> connettoriFallimentoRequest = Common.setConnettoriAbilitati;
			
			List<RequestAndExpectations> requestsByKind = new ArrayList<>();
			
			for(int i=0; i<10;i++) {
				int statusCode = 500+i;
				HttpRequest request5xx = RequestBuilder.buildRestRequest(erogazione);
				request5xx.setUrl(request5xx.getUrl()+"&returnCode=" + statusCode);
				requestsByKind.add(new RequestAndExpectations(request5xx, connettoriSuccessoRequest, connettoriFallimentoRequest, ESITO_CONSEGNA_MULTIPLA_IN_CORSO,statusCode));

				statusCode = 200+i;
				HttpRequest request2xx = RequestBuilder.buildRestRequest(erogazione);
				request2xx.setUrl(request2xx.getUrl()+"&returnCode=" + statusCode);
				requestsByKind.add(new RequestAndExpectations(request2xx, Common.setConnettoriAbilitati, Set.of(), ESITO_CONSEGNA_MULTIPLA_COMPLETATA, statusCode));

				statusCode = 400+i;
				HttpRequest request4xx = RequestBuilder.buildRestRequest(erogazione);
				request4xx.setUrl(request4xx.getUrl()+"&returnCode=" + statusCode);
				requestsByKind.add(new RequestAndExpectations(request4xx, connettoriSuccessoRequest, connettoriFallimentoRequest, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, statusCode));
			}
									
			// Configuro gli esiti per la richiesta sincrona
			for (var requestExpectation : requestsByKind) {
				int statusCode = requestExpectation.statusCodePrincipale;
				
				if (statusCode >= 400 && statusCode <= 499) {
					requestExpectation.principaleSuperata = false;
				}
				
				if (statusCode >= 500 && statusCode <= 599) {
					requestExpectation.principaleSuperata = false;	
				}
			}			
			HttpRequest requestProblem =  RequestBuilder.buildRestRequestProblem(erogazione);
			requestsByKind.add(new RequestAndExpectations(requestProblem, Common.setConnettoriAbilitati, Set.of(),  ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, true, TipoFault.REST));
			
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
	
						checkStatoConsegna(response, requestAndExpectation.esitoPrincipale, requestAndExpectation.connettoriFallimento.size());
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
	
						checkStatoConsegna(response, requestAndExpectation.esitoPrincipale, requestAndExpectation.connettoriFallimento.size());
					}
				}
			}
		}
	
	
	@Test
	public void connettoreRotto() {
		final String erogazione = "TestConsegnaConNotificheConnettoreRottoRest";
		
		// Va come il primo test, ci si aspetta che funzionino tutti i connettori
		// eccetto quello rotto, dove devono avvenire le rispedizioni.
		
		HttpRequest request1 = RequestBuilder.buildRestRequest(erogazione);

		var responses = Common.makeParallelRequests(request1, 10);
		
		Common.checkAll200(responses);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna		
   	   	Set<String> connettoriSchedulati = new HashSet<>(Common.setConnettoriAbilitati);
   	   	connettoriSchedulati.add(CONNETTORE_ROTTO);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			CommonConsegnaMultipla.checkPresaInConsegna(r, connettoriSchedulati.size());
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettoriSchedulati);	
		}
	
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(3*CommonConsegnaMultipla.intervalloControllo);
	
		// La consegna deve risultare ancora in corso per via dell'errore sul connettore rotto, con un connettore
		// in rispedizione
		String fault = "";
		String formatoFault = "";
		for (var r : responses) {
			CommonConsegnaMultipla.checkStatoConsegna(r, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			CommonConsegnaMultipla.checkSchedulingConnettoreInCorso(r, CONNETTORE_ROTTO, ESITO_ERRORE_INVOCAZIONE, fault, formatoFault);
			for (var connettore : Common.setConnettoriAbilitati) {				
				CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(r, connettore, esitoConsegnaFromStatusCode(200), fault, formatoFault);
			}
		}
		
	}

}
