/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_ERRORE_APPLICATIVO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_OK;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.FORMATO_FAULT_SOAP1_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.FORMATO_FAULT_SOAP1_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.buildRequestAndExpectations;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkConsegnaCompletata;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkPresaInConsegna;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkSchedulingConnettoreIniziato;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkStatoConsegna;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.getNumeroTentativiSchedulingConnettore;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.setDifference;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectations.TipoFault;
import org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception;
import org.openspcoop2.pdd.services.cxf.MessageBox;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilsException;

 
// se tutti e quattro i connettori falliscono lo stato rimaneva IN CODA (38) fare un test
// su questo dopo il pull


/**
 * ConsegnaMultiplaTest
 * 
 * @author Francesco Scarlato
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 * Connettore0: http
 * Connettore1: http
 * Connettore2: file
 * Connettore3: file
 * 
 */
public class ConsegnaMultiplaTest  extends ConfigLoader {
	
	// Prima di ogni test  fermo le attuali riconsegne in atto.
	
	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
		File cartellaRisposte = CommonConsegnaMultipla.connettoriFilePath.toFile();
		if(!cartellaRisposte.exists()) {
			cartellaRisposte.mkdir();
		}
		if (!cartellaRisposte.isDirectory()|| !cartellaRisposte.canWrite()) {
			throw new RuntimeException("E' necessario creare la cartella per scrivere le richieste dei connettori, indicata dalla poprietà: <connettori.consegna_multipla.connettore_file.path> ");
		}
	}
	
	@AfterClass
	public static void After() {
		Common.fermaRiconsegne(dbUtils);
		File cartellaRisposte = CommonConsegnaMultipla.connettoriFilePath.toFile();
		if(cartellaRisposte.exists() && cartellaRisposte.isDirectory() && cartellaRisposte.canWrite()) {
			FileSystemUtilities.emptyDir(cartellaRisposte);
		}
	}
	
	@Test
	public void varieCombinazioniDiRegole2xx_4xx() {
		varieCombinazioniDiRegole(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void varieCombinazioniDiRegole5xx() {
		varieCombinazioniDiRegole(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}

	
	@Test
	public void schedulingAbilitatoDisabilitato() throws UtilsException, HttpUtilsException {
		
		/*
		 * Prima si disabilita lo scheduling su di un connettore, si verifica lo stato sul db e poi si riattiva
		 * lo scheduling.
		 */
		final String erogazione = "TestSchedulingAbilitatoDisabilitato";
		
		CommonConsegnaMultipla.jmxDisabilitaSchedulingConnettore(erogazione, Common.CONNETTORE_1);
		
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		
		var responses = Common.makeParallelRequests(request1, 5);
		var responses2 = Common.makeParallelRequests(request2, 5);
		Common.checkAll200(responses);
		Common.checkAll200(responses2);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(r,4));
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
		}
		
		for (var r : responses2) {
			checkPresaInConsegna(r,4);
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
		}
		
		List<String> connettori = new ArrayList<>();
		connettori.add(Common.CONNETTORE_0);
		connettori.add(Common.CONNETTORE_2);
		connettori.add(Common.CONNETTORE_3);
		CommonConsegnaMultipla.waitConsegna(responses, connettori);
		
		String fault = "";
		String formatoFault = "";
		for (var response : responses) {
			// Sul connettore disabilitato non è avvenuto nulla ancora.
			checkSchedulingConnettoreIniziato(response, Common.CONNETTORE_1);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2, ESITO_OK, 200,fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3, ESITO_OK, 200,fault, formatoFault);
			CommonConsegnaMultipla.checkStatoConsegna(response, ESITO_CONSEGNA_MULTIPLA_IN_CORSO,  1);
		}
		
		for (var response : responses2) {
			// Sul connettore disabilitato non è avvenuto nulla ancora.
			checkSchedulingConnettoreIniziato(response, Common.CONNETTORE_1);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkStatoConsegna(response, ESITO_CONSEGNA_MULTIPLA_IN_CORSO,  1);
		}
				
		CommonConsegnaMultipla.jmxAbilitaSchedulingConnettore(erogazione, Common.CONNETTORE_1);
		
		connettori = new ArrayList<>();
		connettori.add(Common.CONNETTORE_1);
		CommonConsegnaMultipla.waitConsegna(responses, connettori);
		
		for (var response : responses) {
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_1, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3, ESITO_OK, 200, fault, formatoFault);
			checkConsegnaCompletata(response);
		}
		
		for (var response : responses2) {
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_1, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2, ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3, ESITO_OK, 200, fault, formatoFault);
			checkConsegnaCompletata(response);
		}
		
	}

	
	@Test
	public void consegnaMultiplaSemplice() throws IOException {
		
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
		final String erogazione = "TestConsegnaMultipla";
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		
		var responses = Common.makeParallelRequests(request1, 5);
		var responses2 = Common.makeParallelRequests(request2, 5);
		Common.checkAll200(responses);
		Common.checkAll200(responses2);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(r,4));
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
		}
		
		for (var r : responses2) {
			checkPresaInConsegna(r,4);
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
		}
	
		// Attendo la consegna
		List<HttpResponse> responsesCheck = new ArrayList<HttpResponse>();
		responsesCheck.addAll(responses);
		responsesCheck.addAll(responses2);
		List<String> connettoriCheck = new ArrayList<>();
		connettoriCheck.addAll(Common.setConnettoriAbilitati);
		CommonConsegnaMultipla.waitConsegna(responsesCheck, connettoriCheck);
		
		for (var r : responses) {
			checkConsegnaCompletata(r);
		}
		for (var r : responses2) {
			checkConsegnaCompletata(r);
		}

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		Set<String> connettoriFile = Set.of(CONNETTORE_2, CONNETTORE_3);
		String fault = "";
		String formatoFault = "";
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaConnettoreFile(request1, response, connettoriFile);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  Common.setConnettoriAbilitati, ESITO_OK, 200, fault, formatoFault);
		}
		for (var response : responses2) {
			CommonConsegnaMultipla.checkConsegnaConnettoreFile(request2, response, connettoriFile);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  Common.setConnettoriAbilitati, ESITO_OK, 200, fault, formatoFault);
		}
		
	}
	

	
	@Test
	public void consegnaMultiplaErrori() throws IOException {
		
		/**
		 * Nel primo test verifico il funzionamento senza particolari condizioni: invio una richiesta
		 * e verifico le consegne. Qui invece inizio facendo fallire qualche richiesta.
		 * I connettori file non posso istruirli, quindi uso tutti connettori http.
		 */
		
		final String erogazione = "TestConsegnaMultipla";

		// Sui connettori file è sempre tutto ok
		Set<String> connettoriFile = Set.of(CONNETTORE_2, CONNETTORE_3);
		
		Set<String> connettoriSuccessoRequest = Set.of(CONNETTORE_2, CONNETTORE_3);
		Set<String> connettoriFallimentoRequest = Set.of(CONNETTORE_0, CONNETTORE_1);
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		for(int i=0; i<4;i++) {
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			HttpRequest request5xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType );
			request5xx.setUrl(request5xx.getUrl()+"&returnCode=" + (500+i));
			requestsByKind.add(new RequestAndExpectations(request5xx, connettoriSuccessoRequest, connettoriFallimentoRequest, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 500+i));
			
			
		    HttpRequest request2xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType );
			request2xx.setUrl(request2xx.getUrl()+"&returnCode=" + (200+i));
			requestsByKind.add(new RequestAndExpectations(request2xx, Common.setConnettoriAbilitati, Set.of(), ESITO_CONSEGNA_MULTIPLA_COMPLETATA, 200+i ));
			
			HttpRequest request4xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType );
			request4xx.setUrl(request4xx.getUrl()+"&returnCode=" + (400+i));
			requestsByKind.add(new RequestAndExpectations(request4xx, connettoriSuccessoRequest, connettoriFallimentoRequest, ESITO_CONSEGNA_MULTIPLA_IN_CORSO,400+i));
		}		
		
		HttpRequest requestSoapFault =  RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFault, 
				Common.setConnettoriAbilitati, 
				Set.of(),
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, true, TipoFault.SOAP1_1 ));
		
		requestSoapFault =  RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2 );
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFault, 
				Common.setConnettoriAbilitati, 
				Set.of(),  
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, true, TipoFault.SOAP1_2 ));
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
			Common.checkAll200(responses);
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(responses,4));
			checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
		}
		
		// Attendo la prima consegna
		Map<String, java.sql.Timestamp> primaConsegna = CommonConsegnaMultipla.waitPrimaConsegna(responsesByKind);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				CommonConsegnaMultipla.checkConsegnaConnettoreFile(requestAndExpectation.request, response, connettoriFile);
				CommonConsegnaMultipla.checkRequestExpectations(requestAndExpectation, response, connettoriFile);
			}
		}

		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		CommonConsegnaMultipla.waitProssimaConsegna(responsesByKind,primaConsegna);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				CommonConsegnaMultipla.checkRequestExpectationsFinal(requestAndExpectation, response, connettoriFile);
			}
		}

	}
	
	
	public void varieCombinazioniDiRegole(Map<Integer,Set<String>> statusCodeVsConnettori) {
		/**
		 * Qui si testa la corretta rispedizione di richieste diverse su connettori diversi.
		 * Vengono testati tutte le regole. 
		 */
		final String erogazione = "TestConsegnaMultiplaVarieCombinazioniDiRegole";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		int i = 0;
		for (var entry : statusCodeVsConnettori.entrySet()) {
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
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_1 ));
		
		requestSoapFault = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFault,
				Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
				Set.of(CONNETTORE_1),  
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_2 ));
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
		   	Common.checkAll200(responses);
		   	CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(responses,4));
			checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
		}
		
		// Attendo la prima consegna
		Map<String, java.sql.Timestamp> primaConsegna = CommonConsegnaMultipla.waitPrimaConsegna(responsesByKind);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				CommonConsegnaMultipla.checkRequestExpectations(requestAndExpectation, response);
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		CommonConsegnaMultipla.waitProssimaConsegna(responsesByKind,primaConsegna);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				CommonConsegnaMultipla.checkRequestExpectationsFinal(requestAndExpectation, response);
			}
		}		
	}
	
	
	@Test
	public void  regoleSoapFault() {
		
		/**
		 * In questo test viene controllato che la rispedizione avvenga in caso di vari match col soapFault
		 * e che non venga rispedita se non c'è match.
		 */
		
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
	
		String  fault = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault>"
				 +"<faultcode xmlns:ns0=\"http://www.openspcoop2.org/example\">ns0:CodeRispedita</faultcode><faultstring>MessageRispedita</faultstring>"
				 + "<faultactor>ActorRispedita</faultactor></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
	
		requestsByKind.add(new RequestAndExpectationsFault(
				requestSoapFaultTuttiValorizzati,				
				Set.of(CONNETTORE_1,CONNETTORE_2),
				Set.of(CONNETTORE_0, CONNETTORE_3),
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_1, fault, FORMATO_FAULT_SOAP1_1)
			);
	
		 
		 fault = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault>"
				 +"<faultcode xmlns:ns0=\"http://www.openspcoop2.org/example\">ns0:CodeRispedita123</faultcode><faultstring>MessageRispedita123</faultstring>"
				 + "<faultactor>ActorRispedita123</faultactor></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestSoapFaultTuttiRegex,
				Set.of(CONNETTORE_0,CONNETTORE_2,CONNETTORE_3),
				Set.of(CONNETTORE_1), 
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_1, fault, FORMATO_FAULT_SOAP1_1)
			);
		
		 fault = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault>"
				 +"<faultcode xmlns:ns0=\"http://www.openspcoop2.org/example\">ns0:CodeNotImportant</faultcode><faultstring>MessageNotImportant</faultstring>"
				 + "<faultactor>ActorRispedita</faultactor></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
					
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestSoapFaultSoloActor,
				Set.of(CONNETTORE_0,CONNETTORE_1,CONNETTORE_2),
				Set.of(CONNETTORE_3), 
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_1, fault, FORMATO_FAULT_SOAP1_1)
			);
		
	
		 fault = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault>"
				 +"<faultcode xmlns:ns0=\"http://www.openspcoop2.org/example\">ns0:2341</faultcode><faultstring>Container-MessageRispeditaContains-Container</faultstring>"
				 + "<faultactor>12345</faultactor></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestSoapMessageContains,				
				Set.of(CONNETTORE_0, CONNETTORE_1, CONNETTORE_3),
				Set.of(CONNETTORE_2),
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_1, fault, FORMATO_FAULT_SOAP1_1)
			);
		
		requestSoapFaultTuttiValorizzati = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestSoapFaultTuttiValorizzati.setUrl(requestSoapFaultTuttiValorizzati.getUrl()+"&faultActor=ActorRispedita&faultCode=CodeRispedita&faultMessage=MessageRispedita");	
		
		requestSoapFaultTuttiRegex = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestSoapFaultTuttiRegex.setUrl(requestSoapFaultTuttiRegex.getUrl()+"&faultActor=ActorRispedita123&faultCode=CodeRispedita123&faultMessage=MessageRispedita123");
		
		requestSoapFaultSoloActor = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestSoapFaultSoloActor.setUrl(requestSoapFaultSoloActor.getUrl()+"&faultActor=ActorRispedita&faultCode=CodeNotImportant&faultMessage=MessageNotImportant");

		// Questa richiesta testa che sul campo message venga fatto il contains.
		requestSoapMessageContains = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestSoapMessageContains.setUrl(requestSoapMessageContains.getUrl()+"&faultActor=12345&faultCode=2341&faultMessage=Container-MessageRispeditaContains-Container"); 
	
		 fault = "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"><env:Header/><env:Body><env:Fault><env:Code><env:Value>env:Receiver</env:Value>"
					+ "<env:Subcode><env:Value xmlns:ns1=\"http://www.openspcoop2.org/example\">ns1:CodeRispedita</env:Value></env:Subcode></env:Code><env:Reason>"
					+"<env:Text xml:lang=\"en-US\">MessageRispedita</env:Text></env:Reason><env:Role>ActorRispedita</env:Role></env:Fault></env:Body></env:Envelope>";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestSoapFaultTuttiValorizzati,				
				Set.of(CONNETTORE_1,CONNETTORE_2),
				Set.of(CONNETTORE_0, CONNETTORE_3),
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_2, fault, FORMATO_FAULT_SOAP1_2)
			);
		
		 fault = "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"><env:Header/><env:Body><env:Fault><env:Code><env:Value>env:Receiver</env:Value>"
					+ "<env:Subcode><env:Value xmlns:ns1=\"http://www.openspcoop2.org/example\">ns1:CodeRispedita123</env:Value></env:Subcode></env:Code><env:Reason>"
					+"<env:Text xml:lang=\"en-US\">MessageRispedita123</env:Text></env:Reason><env:Role>ActorRispedita123</env:Role></env:Fault></env:Body></env:Envelope>";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestSoapFaultTuttiRegex,
				Set.of(CONNETTORE_0,CONNETTORE_2,CONNETTORE_3),
				Set.of(CONNETTORE_1), 
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_2, fault, FORMATO_FAULT_SOAP1_2)
			);
		
		 fault =  "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"><env:Header/><env:Body><env:Fault><env:Code><env:Value>env:Receiver</env:Value>"
					+ "<env:Subcode><env:Value xmlns:ns1=\"http://www.openspcoop2.org/example\">ns1:CodeNotImportant</env:Value></env:Subcode></env:Code><env:Reason>"
					+"<env:Text xml:lang=\"en-US\">MessageNotImportant</env:Text></env:Reason><env:Role>ActorRispedita</env:Role></env:Fault></env:Body></env:Envelope>";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestSoapFaultSoloActor,
				Set.of(CONNETTORE_0,CONNETTORE_1,CONNETTORE_2),
				Set.of(CONNETTORE_3), 
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_2, fault, FORMATO_FAULT_SOAP1_2)
			);

		 fault = "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"><env:Header/><env:Body><env:Fault><env:Code><env:Value>env:Receiver</env:Value>"
					+ "<env:Subcode><env:Value xmlns:ns1=\"http://www.openspcoop2.org/example\">ns1:2341</env:Value></env:Subcode></env:Code><env:Reason>"
					+"<env:Text xml:lang=\"en-US\">Container-MessageRispeditaContains-Container</env:Text></env:Reason><env:Role>12345</env:Role></env:Fault></env:Body></env:Envelope>";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestSoapMessageContains,				
				Set.of(CONNETTORE_0,CONNETTORE_1,CONNETTORE_3),
				Set.of(CONNETTORE_2),
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_2, fault, FORMATO_FAULT_SOAP1_2)
			);
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
			Common.checkAll200(responses);
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(responses,4));
			checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
		}
		
		// Attendo la prima consegna
		Map<String, java.sql.Timestamp> primaConsegna = CommonConsegnaMultipla.waitPrimaConsegna(responsesByKind);
		
		for (var _requestAndExpectation : responsesByKind.keySet()) {
			var requestAndExpectation = (RequestAndExpectationsFault) _requestAndExpectation;
			
			for (var response : responsesByKind.get(requestAndExpectation)) {
				
				for (var connettore : requestAndExpectation.connettoriFallimento) {
					int esitoNotifica = ESITO_ERRORE_APPLICATIVO;
					CommonConsegnaMultipla.checkSchedulingConnettoreInCorso(response, connettore, esitoNotifica, requestAndExpectation.statusCodePrincipale, requestAndExpectation.faultMessage, requestAndExpectation.faultType);
				}
				
				for (var connettore : requestAndExpectation.connettoriSuccesso) {
					int esitoNotifica = ESITO_ERRORE_APPLICATIVO;
					CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, connettore, esitoNotifica,  requestAndExpectation.statusCodePrincipale, requestAndExpectation.faultMessage, requestAndExpectation.faultType);
				}
				
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		CommonConsegnaMultipla.waitProssimaConsegna(responsesByKind,primaConsegna);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				for ( var connettore : requestAndExpectation.connettoriFallimento) {
					assertTrue( getNumeroTentativiSchedulingConnettore(response, connettore) >= 2);
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
		
		final String erogazione = "TestConsegnaMultiplaRispedizione";
		
		
		Set<String> connettoriSuccessoRequest = Set.of();
		Set<String> connettoriFallimentoRequest = Common.setConnettoriAbilitati;
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		for(int i=0; i<4;i++) {
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			HttpRequest request5xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType );
			request5xx.setUrl(request5xx.getUrl()+"&returnCode=" + (500+i));
			requestsByKind.add(new RequestAndExpectations(request5xx, connettoriSuccessoRequest, connettoriFallimentoRequest, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 500+i));

			HttpRequest request2xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType );
			request2xx.setUrl(request2xx.getUrl()+"&returnCode=" + (200+i));
			requestsByKind.add(new RequestAndExpectations(request2xx, Common.setConnettoriAbilitati, Set.of(), ESITO_CONSEGNA_MULTIPLA_COMPLETATA,200+i));

			HttpRequest request4xx = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", soapContentType );
			request4xx.setUrl(request4xx.getUrl()+"&returnCode=" + (400+i));
			requestsByKind.add(new RequestAndExpectations(request4xx, connettoriSuccessoRequest, connettoriFallimentoRequest, ESITO_CONSEGNA_MULTIPLA_IN_CORSO,400+i));
		}
		
		HttpRequest requestSoapFault =  RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		requestsByKind.add(new RequestAndExpectations(requestSoapFault, Common.setConnettoriAbilitati, Set.of(),  ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_1));
		
		requestSoapFault =  RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestsByKind.add(new RequestAndExpectations(requestSoapFault, Common.setConnettoriAbilitati, Set.of(),  ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_2));
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
		   	Common.checkAll200(responses);
		   	CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(responses,4));
			checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
		}
		
		// Attendo la prima consegna
		Map<String, java.sql.Timestamp> primaConsegna = CommonConsegnaMultipla.waitPrimaConsegna(responsesByKind);

		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				CommonConsegnaMultipla.checkRequestExpectations(requestAndExpectation, response);
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		CommonConsegnaMultipla.waitProssimaConsegna(responsesByKind,primaConsegna,CONNETTORE_0);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
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
		
		// Attendo un minuto, anche il connettore 0 ora deve avere una rispedizione in più
		ConfigLoader.getLoggerCore().info("@CadenzaRispedizione test in corso, attendo 1 minuto ...");
		org.openspcoop2.utils.Utilities.sleep(1000*60);
		org.openspcoop2.utils.Utilities.sleep(CommonConsegnaMultipla.intervalloControllo);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
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

	
	@Test
	public void connettoreRotto() {
		
		// Va come il primo test, ci si aspetta che funzionino tutti i connettori
		// eccetto quello rotto, dove devono avvenire le rispedizioni.
		
		final String erogazione = "TestConsegnaMultiplaConnettoreRotto";

		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		
		var responses = Common.makeParallelRequests(request1, 5);
		var responses2 = Common.makeParallelRequests(request2, 5);
		
		Common.checkAll200(responses);
		Common.checkAll200(responses2);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
   	   	Set<String> connettoriSchedulati = new HashSet<>(Common.setConnettoriAbilitati);
   	   	connettoriSchedulati.add(CONNETTORE_ROTTO);
   	   	
		for (var r : responses) {
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(r,connettoriSchedulati.size()));
			checkSchedulingConnettoreIniziato(r, connettoriSchedulati);
		}
		
		for (var r : responses2) {
			checkPresaInConsegna(r,connettoriSchedulati.size());
			checkSchedulingConnettoreIniziato(r, connettoriSchedulati);
		}
	
		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<>();
		connettoriCheck.addAll(connettoriSchedulati);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);
	
		// La consegna deve risultare ancora in corso per via dell'errore sul connettore rotto, con un connettore
		// in rispedizione
		String fault = "";
		String formatoFault = "";
		for (var r : responses) {
			CommonConsegnaMultipla.checkStatoConsegna(r, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			CommonConsegnaMultipla.checkSchedulingConnettoreInCorso(r, CONNETTORE_ROTTO,ESITO_ERRORE_INVOCAZIONE, 0, fault, formatoFault);
			for (var connettore : Common.setConnettoriAbilitati) {
				CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(r, connettore, ESITO_OK, 200, fault, formatoFault);
			}
		}
		
		for (var r : responses2) {
			CommonConsegnaMultipla.checkStatoConsegna(r, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			CommonConsegnaMultipla.checkSchedulingConnettoreInCorso(r, CONNETTORE_ROTTO, ESITO_ERRORE_INVOCAZIONE, 0, fault, formatoFault);
			for (var connettore : Common.setConnettoriAbilitati) {
				CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(r, connettore, ESITO_OK, 200, fault, formatoFault);
			}
		}
		
	}
	
	
	@Test
	public void integrationManager() throws MalformedURLException, IntegrationManagerException_Exception {
			
		/**
		 * Diversamente dalla consegna con notifiche, In questa erogazione la messageBox è fornita da un applicativo server.
		 */
		
		String erogazione 							= "TestConsegnaMultiplaIntegrationManager";
		String connettoreMessageBox 	= Common.CONNETTORE_0;
		Set<String> connettoriSuccesso = Set.of(Common.CONNETTORE_1,Common.CONNETTORE_2,Common.CONNETTORE_3);
		
		String username	= "UtenteTestApplicativoMessageBox";
		String password 	= "JzpvsjyUU63z5RB38#4F";
		
		MessageBox imMessageBoxPort = CommonConsegnaMultipla.getMessageBox(username, password);
		
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);

		var responsesSoap1 = Common.makeParallelRequests(request1, 5);
		var responsesSoap2 = Common.makeParallelRequests(request2, 5);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var response : responsesSoap1) {
			assertEquals(200, response.getResultHTTPOperation());
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(response, Common.setConnettoriAbilitati.size()));
			checkSchedulingConnettoreIniziato(response, Common.setConnettoriAbilitati);
			
			// check consegna_im
			String query = "select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ? and consegna_im = ? ";
			
			String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
			Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettoreMessageBox, true);
			assertEquals(Integer.valueOf(1), count);
		}
		
		// Ripeto per soap2
		for (var response : responsesSoap2) {
			assertEquals(200, response.getResultHTTPOperation());
			checkPresaInConsegna(response, Common.setConnettoriAbilitati.size());
			checkSchedulingConnettoreIniziato(response, Common.setConnettoriAbilitati);	
			
			// check consegna_im
			String query = "select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ? and consegna_im = ? ";
			
			String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
			Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettoreMessageBox, true);
			assertEquals(Integer.valueOf(1), count);
		}
	
		// Attendo la consegna
		List<HttpResponse> responsesCheck = new ArrayList<HttpResponse>();
		responsesCheck.addAll(responsesSoap1);
		responsesCheck.addAll(responsesSoap2);
		List<String> connettoriCheck = new ArrayList<>();
		connettoriCheck.addAll(connettoriSuccesso);
		CommonConsegnaMultipla.waitConsegna(responsesCheck, connettoriCheck);

		// Controllo che le notifiche siano completate sui connettori non message box
		for (var response : responsesSoap1) {
			CommonConsegnaMultipla.checkStatoConsegna(response, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			for (var connettore : connettoriSuccesso) {
				CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, connettore,ESITO_OK, 200, "" , "");
			}
			// Controllo che lo scheduling sia stato effettuato solo sui connettori indicati, ci metto anche quello che fa da MessageBox
			CommonConsegnaMultipla.checkConnettoriRaggiuntiEsclusivamente(response, Common.setConnettoriAbilitati);
		}
		
		// ripeto per soap2
		for (var response : responsesSoap2) {
			CommonConsegnaMultipla.checkStatoConsegna(response, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			for (var connettore : connettoriSuccesso) {
				CommonConsegnaMultipla.checkSchedulingConnettoreCompletato(response, connettore,ESITO_OK, 200, "" , "");			
			}
			// Controllo che lo scheduling sia stato effettuato solo sui connettori indicati, ci metto anche quello che fa da MessageBox
			CommonConsegnaMultipla.checkConnettoriRaggiuntiEsclusivamente(response, Common.setConnettoriAbilitati);		
		}
		
		CommonConsegnaMultipla.checkMessageBox(connettoreMessageBox, imMessageBoxPort);
		
		// Adesso cancello tutti i messaggi in pancia in modo che la consegna risulti completata
		imMessageBoxPort.deleteAllMessages();
		
		// Attendo la consegna
		CommonConsegnaMultipla.waitConsegna(responsesCheck, connettoriCheck);
		
		for (var response : responsesSoap1) {
			CommonConsegnaMultipla.checkStatoConsegna(response, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_COMPLETATA, 0);
			CommonConsegnaMultipla.checkConsegnaTerminataNoStatusCode(response, connettoreMessageBox);
		}
		for (var response : responsesSoap2) {
			CommonConsegnaMultipla.checkStatoConsegna(response, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_COMPLETATA, 0);
			CommonConsegnaMultipla.checkConsegnaTerminataNoStatusCode(response, connettoreMessageBox);
		}
		
	}

}
