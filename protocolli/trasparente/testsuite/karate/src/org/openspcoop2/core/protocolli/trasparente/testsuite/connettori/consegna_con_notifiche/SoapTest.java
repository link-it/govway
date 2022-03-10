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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_ERRORE_APPLICATIVO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_OK;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.FORMATO_FAULT_SOAP1_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.FORMATO_FAULT_SOAP1_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkNessunaNotifica;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkPresaInConsegna;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkSchedulingConnettoreCompletato;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkSchedulingConnettoreInCorso;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkSchedulingConnettoreIniziato;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkStatoConsegna;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.esitoConsegnaFromStatusCode;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.getNumeroTentativiSchedulingConnettore;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.isEsitoErrore;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.setDifference;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.time.Instant;
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
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectations.TipoFault;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectationsFault;
import org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception;
import org.openspcoop2.pdd.services.cxf.MessageBox;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilsException;


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
	public void varieCombinazioniDiRegole2xx_4xx() {
		varieCombinazioniDiRegole(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void varieCombinazioniDiRegole5xx() {
		varieCombinazioniDiRegole(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}

	@Test
	public void consegnaConNotificheSemplice() throws IOException {
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
		
		final String erogazione = "TestConsegnaConNotificheSoap";
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);

		var responses = Common.makeParallelRequests(request1, 5);
		var responses2 = Common.makeParallelRequests(request2, 5);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(r, Common.setConnettoriAbilitati.size()));
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}
		for (var r : responses2) {
			assertEquals(200, r.getResultHTTPOperation());
			
			checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}
	
		// Attendo la consegna
		List<HttpResponse> responsesCheck = new ArrayList<HttpResponse>();
		responsesCheck.addAll(responses);
		responsesCheck.addAll(responses2);
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(Common.setConnettoriAbilitati);
		CommonConsegnaMultipla.waitConsegna(responsesCheck, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		Set<String> connettoriFile = Set.of(CONNETTORE_2, CONNETTORE_3);
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkConsegnaConnettoreFile(request1, response, connettoriFile);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  Common.setConnettoriAbilitati, ESITO_OK, 200, "", "");
		}
		for (var response : responses2) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkConsegnaConnettoreFile(request2, response, connettoriFile);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  Common.setConnettoriAbilitati, ESITO_OK, 200, "", "");
		}

	}
	
	
	@Test
	public void  regoleSoapFault() {
		final String erogazione = "TestConsegnaConNotificheRegoleSoapFault";
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		HttpRequest requestRispeditaTuttiValorizzati = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestRispeditaTuttiValorizzati.setUrl(requestRispeditaTuttiValorizzati.getUrl()+"&returnCode=501&faultActor=ActorRispedita&faultCode=CodeRispedita&faultMessage=MessageRispedita");	
		
		HttpRequest requestRispeditaTuttiRegex = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestRispeditaTuttiRegex.setUrl(requestRispeditaTuttiRegex.getUrl()+"&returnCode=502&faultActor=ActorRispedita123&faultCode=CodeRispedita123&faultMessage=MessageRispedita123");
		
		HttpRequest requestCompletataTuttiValorizzati = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestCompletataTuttiValorizzati.setUrl(requestCompletataTuttiValorizzati.getUrl()+"&returnCode=503&faultActor=ActorCompletata&faultCode=CodeCompletata&faultMessage=MessageCompletata");

		// Questa richiesta testa che sul campo message venga fatto il contains.
		HttpRequest requestCompletataTuttiRegex =  RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
		requestCompletataTuttiRegex.setUrl(requestCompletataTuttiRegex.getUrl()+"&returnCode=504&faultActor=ActorCompletata123&faultCode=CodeCompletata123&faultMessage=MessageCompletata123");
	
		String  fault = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault>"
				 +"<faultcode xmlns:ns0=\"http://www.openspcoop2.org/example\">ns0:CodeRispedita</faultcode><faultstring>MessageRispedita</faultstring>"
				 + "<faultactor>ActorRispedita</faultactor></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
	
		requestsByKind.add(new RequestAndExpectationsFault(
				requestRispeditaTuttiValorizzati,				
				Set.of(),
				Set.of(CONNETTORE_0, CONNETTORE_3,CONNETTORE_1,CONNETTORE_2),
				ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 500, TipoFault.SOAP1_1, fault, FORMATO_FAULT_SOAP1_1)
			);
	
		 
		 fault = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault>"
				 +"<faultcode xmlns:ns0=\"http://www.openspcoop2.org/example\">ns0:CodeRispedita123</faultcode><faultstring>MessageRispedita123</faultstring>"
				 + "<faultactor>ActorRispedita123</faultactor></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestRispeditaTuttiRegex,
				Set.of(),
				Set.of(CONNETTORE_1,CONNETTORE_0,CONNETTORE_2,CONNETTORE_3), 
				ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 500, TipoFault.SOAP1_1, fault, FORMATO_FAULT_SOAP1_1)
			);
		
		 fault = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault>"
				 +"<faultcode xmlns:ns0=\"http://www.openspcoop2.org/example\">ns0:CodeCompletata</faultcode><faultstring>MessageCompletata</faultstring>"
				 + "<faultactor>ActorCompletata</faultactor></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestCompletataTuttiValorizzati,
				Set.of(CONNETTORE_2),
				Set.of(CONNETTORE_3,CONNETTORE_0,CONNETTORE_1), 
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_1, fault, FORMATO_FAULT_SOAP1_1)
			);
		
	
	  fault = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault>"
				 +"<faultcode xmlns:ns0=\"http://www.openspcoop2.org/example\">ns0:CodeCompletata123</faultcode><faultstring>MessageCompletata123</faultstring>"
				 + "<faultactor>ActorCompletata123</faultactor></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestCompletataTuttiRegex,				
				Set.of(CONNETTORE_3),
				Set.of(CONNETTORE_2,CONNETTORE_0,CONNETTORE_1),
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_1, fault, FORMATO_FAULT_SOAP1_1)
			);
		
		requestRispeditaTuttiValorizzati = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestRispeditaTuttiValorizzati.setUrl(requestRispeditaTuttiValorizzati.getUrl()+"&returnCode=501&faultActor=ActorRispedita&faultCode=CodeRispedita&faultMessage=MessageRispedita");	
		
		requestRispeditaTuttiRegex = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestRispeditaTuttiRegex.setUrl(requestRispeditaTuttiRegex.getUrl()+"&returnCode=502&faultActor=ActorRispedita123&faultCode=CodeRispedita123&faultMessage=MessageRispedita123");
		
		requestCompletataTuttiValorizzati = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestCompletataTuttiValorizzati.setUrl(requestCompletataTuttiValorizzati.getUrl()+"&returnCode=503&faultActor=ActorCompletata&faultCode=CodeCompletata&faultMessage=MessageCompletata");

		// Questa richiesta testa che sul campo message venga fatto il contains.
		requestCompletataTuttiRegex =  RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestCompletataTuttiRegex.setUrl(requestCompletataTuttiRegex.getUrl()+"&returnCode=504&faultActor=ActorCompletata123&faultCode=CodeCompletata123&faultMessage=MessageCompletata123");
	
		 fault = "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"><env:Header/><env:Body><env:Fault><env:Code><env:Value>env:Receiver</env:Value>"
					+ "<env:Subcode><env:Value xmlns:ns1=\"http://www.openspcoop2.org/example\">ns1:CodeRispedita</env:Value></env:Subcode></env:Code><env:Reason>"
					+"<env:Text xml:lang=\"en-US\">MessageRispedita</env:Text></env:Reason><env:Role>ActorRispedita</env:Role></env:Fault></env:Body></env:Envelope>";
	
		requestsByKind.add(new RequestAndExpectationsFault(
				requestRispeditaTuttiValorizzati,				
				Set.of(),
				Set.of(CONNETTORE_0, CONNETTORE_3,CONNETTORE_1,CONNETTORE_2),
				ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 500, TipoFault.SOAP1_2, fault, FORMATO_FAULT_SOAP1_2)
			);
	
		 
		 fault = "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"><env:Header/><env:Body><env:Fault><env:Code><env:Value>env:Receiver</env:Value>"
					+ "<env:Subcode><env:Value xmlns:ns1=\"http://www.openspcoop2.org/example\">ns1:CodeRispedita123</env:Value></env:Subcode></env:Code><env:Reason>"
					+"<env:Text xml:lang=\"en-US\">MessageRispedita123</env:Text></env:Reason><env:Role>ActorRispedita123</env:Role></env:Fault></env:Body></env:Envelope>";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestRispeditaTuttiRegex,
				Set.of(),
				Set.of(CONNETTORE_1,CONNETTORE_0,CONNETTORE_2,CONNETTORE_3), 
				ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 500, TipoFault.SOAP1_2, fault, FORMATO_FAULT_SOAP1_2)
			);
		
		fault = "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"><env:Header/><env:Body><env:Fault><env:Code><env:Value>env:Receiver</env:Value>"
				+ "<env:Subcode><env:Value xmlns:ns1=\"http://www.openspcoop2.org/example\">ns1:CodeCompletata</env:Value></env:Subcode></env:Code><env:Reason>"
				+"<env:Text xml:lang=\"en-US\">MessageCompletata</env:Text></env:Reason><env:Role>ActorCompletata</env:Role></env:Fault></env:Body></env:Envelope>";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestCompletataTuttiValorizzati,
				Set.of(CONNETTORE_2),
				Set.of(CONNETTORE_3,CONNETTORE_0,CONNETTORE_1), 
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_2, fault, FORMATO_FAULT_SOAP1_2)
			);
		
	
		fault = "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"><env:Header/><env:Body><env:Fault><env:Code><env:Value>env:Receiver</env:Value>"
				+ "<env:Subcode><env:Value xmlns:ns1=\"http://www.openspcoop2.org/example\">ns1:CodeCompletata123</env:Value></env:Subcode></env:Code><env:Reason>"
				+"<env:Text xml:lang=\"en-US\">MessageCompletata123</env:Text></env:Reason><env:Role>ActorCompletata123</env:Role></env:Fault></env:Body></env:Envelope>";
		 
		requestsByKind.add(new RequestAndExpectationsFault(
				requestCompletataTuttiRegex,				
				Set.of(CONNETTORE_3),
				Set.of(CONNETTORE_2,CONNETTORE_0,CONNETTORE_1),
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, TipoFault.SOAP1_2, fault, FORMATO_FAULT_SOAP1_2)
			);
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CondizionaleByFiltroSoapTest.checkResponses(responsesByKind);
		
	}
	
	
	@Test
	public void valorizzazioneCampiAvanzata() throws IOException {
		/* Qui vengono testati i vari campi e la rivalorizzazione delle date. 
		 * 
		 * data_registrazione
		 * data_accettazione_richiesta     
 			data_uscita_richiesta           
 			data_accettazione_risposta      
 			data_ingresso_risposta         
		    data_primo_tentativo          
 			data_ultimo_errore              
 			dettaglio_esito_ultimo_errore   
 			codice_risposta_ultimo_errore  
 			ultimo_errore  
 			location_ultimo_errore  
 			fault_ultimo_errore        
 			formato_fault_ultimo_errore                    
		 */

		final String erogazione = "TestConsegnaConNotificheSoapVarieCombinazioniDiRegole";
		
		// seguo la statusCode2xxVsConnettori, mando solo 2xx così  la principale supera sempre e controllo
		// i campi sulle notifiche
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		int i = 0;
		for (var entry : CommonConsegnaMultipla.statusCode2xxVsConnettori.entrySet()) {
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			int statusCode = entry.getKey();
			var connettoriSuccesso = entry.getValue();
			var connettoriErrore = setDifference(Common.setConnettoriAbilitati,connettoriSuccesso);
			var requestExpectation = buildRequestAndExpectations(erogazione, statusCode, connettoriSuccesso, connettoriErrore, soapContentType);
		
			requestsByKind.add(requestExpectation);
			i++;
		}
		
		// Le soap fault passano
		HttpRequest requestSoapFault = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFault,
				Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
				Set.of(CONNETTORE_1),  
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, true, TipoFault.SOAP1_1));
		
		requestSoapFault = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFault,
				Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
				Set.of(CONNETTORE_1),  
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, true, TipoFault.SOAP1_2));
		
		Timestamp dataRiferimentoTest = java.sql.Timestamp.from(Instant.now());
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Le richieste per cui la transazione sincrona ha avuto successo devono essere schedulate
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			
			if (requestExpectation.principaleSuperata) {
				CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size()));
				checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
			} else {
				for (var response : responses) {
					checkNessunaNotifica(response);
					CommonConsegnaMultipla.checkNessunoScheduling(response);
				}
			}
		}
		
		// Attendo la prima consegna
		Map<String, java.sql.Timestamp> primaConsegna = CommonConsegnaMultipla.waitPrimaConsegna(responsesByKind);
				
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);

			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					CommonConsegnaMultipla.checkRequestExpectations(requestExpectation, response);

					for (var connettore : requestExpectation.connettoriFallimento) {
						String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
						
						String query = "Select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ?"
								+ " and data_uscita_richiesta > ? and data_registrazione > ? and data_ingresso_risposta >= data_uscita_richiesta"
								+ " and data_ingresso_risposta >= data_accettazione_risposta and data_uscita_richiesta = data_primo_tentativo"
								+ " and cluster_id_in_coda = 'IDGW' and cluster_id_consegna = 'IDGW' ";
						
						getLoggerCore().info("Checking date per connettori fallimento: " + id_transazione + " " + connettore + " " + dataRiferimentoTest.toString() );
						Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione,	connettore, dataRiferimentoTest, dataRiferimentoTest);
						
						// debug for error
						if(count.intValue() != 1) {
							ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa (dataRiferimentoTest:"+dataRiferimentoTest+"):");
							String query2 = "select data_uscita_richiesta, data_registrazione, data_ingresso_risposta, data_accettazione_risposta, data_primo_tentativo,cluster_id_in_coda,cluster_id_consegna from transazioni_sa where id_transazione = ? and connettore_nome = ?";
							List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione, connettore);
							for (var v : letto) {
								ConfigLoader.getLoggerCore().error(v.toString());
							}
						}
						
						assertEquals(Integer.valueOf(1), count);
						
						String fault 		 = "";
						String formatoFault = "";
						String ultimoErrore;
						String locationUltimoErrore; 

						if (requestExpectation.tipoFault == TipoFault.SOAP1_1) {
							fault 		 = CommonConsegnaMultipla.FAULT_SOAP1_1;
							formatoFault = CommonConsegnaMultipla.FORMATO_FAULT_SOAP1_1;
							ultimoErrore = "Consegna [http] con errore: errore applicativo, <SOAP-ENV:Fault xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><faultcode xmlns:ns0=\"http://www.openspcoop2.org/example\">ns0:Server.OpenSPCoopExa"
									+ "mpleFault</faultcode><faultstring>Fault ritornato dalla servlet di trace, esempio di OpenSPCoop</faultstring><faultactor>OpenSPCoopTrace</faultactor></SOAP-ENV:Fault>";
							locationUltimoErrore = "http://localhost:8080/TestService/echo?id_connettore="+connettore+"&returnCode="+requestExpectation.statusCodePrincipale+"&replyPrefixQueryParameter=GovWay-TestSuite-&fault=true&faultSoapVersion=11&replyQueryParameter=id_connettore";							
						} else if (requestExpectation.tipoFault == TipoFault.SOAP1_2) {
							fault 		 = CommonConsegnaMultipla.FAULT_SOAP1_2;
							formatoFault = CommonConsegnaMultipla.FORMATO_FAULT_SOAP1_2;
							ultimoErrore = "Consegna [http] con errore: errore applicativo, <env:Fault xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"><env:Code><env:Value>env:Receiver</env:Value><env:Subcode><env:Value xmlns:ns1=\"http://www.ope"
									+ "nspcoop2.org/example\">ns1:Server.OpenSPCoopExampleFault</env:Value></env:Subcode></env:Code><env:Reason><env:Text xml:lang=\"en-US\">Fault ritornato dalla servlet di trace, esempio di OpenSPCoop</env:Text></env:Reason><env:Role>OpenSPCoopTr"
									+ "ace</env:Role></env:Fault>";
							locationUltimoErrore = "http://localhost:8080/TestService/echo?id_connettore="+connettore+"&returnCode="+requestExpectation.statusCodePrincipale+"&replyPrefixQueryParameter=GovWay-TestSuite-&fault=true&faultSoapVersion=12&replyQueryParameter=id_connettore";
						} else {
							ultimoErrore =  "Consegna [http] con errore: errore HTTP " + requestExpectation.statusCodePrincipale;
							locationUltimoErrore = "http://localhost:8080/TestService/echo?id_connettore="+connettore+"&returnCode="+requestExpectation.statusCodePrincipale+"&replyPrefixQueryParameter=GovWay-TestSuite-&replyQueryParameter=id_connettore";
						}
						
						query = "Select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ? and data_ultimo_errore > ? and dettaglio_esito_ultimo_errore = ? and codice_risposta_ultimo_errore = ? and ultimo_errore LIKE '"+ultimoErrore+"' and location_ultimo_errore LIKE '"+locationUltimoErrore+"'";
												
						int esitoNotifica;
						if (requestExpectation.tipoFault != TipoFault.NESSUNO) {
							esitoNotifica = ESITO_ERRORE_APPLICATIVO;				
						} else if (isEsitoErrore(requestExpectation.statusCodePrincipale)) {			
							esitoNotifica = ESITO_ERRORE_INVOCAZIONE;
						} else {
							esitoNotifica = esitoConsegnaFromStatusCode(requestExpectation.statusCodePrincipale);
						}
						String statusCode = String.valueOf(requestExpectation.statusCodePrincipale);
						
						getLoggerCore().info("Checking info errori date per connettori fallimento: " + id_transazione + " " + connettore + " " + dataRiferimentoTest.toString() + " " + esitoNotifica + " " + statusCode + " " + ultimoErrore + " " + locationUltimoErrore );
						if (fault.isEmpty() ) {
							query += " and fault_ultimo_errore is null and formato_fault_ultimo_errore is null";
							count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, dataRiferimentoTest, esitoNotifica, statusCode);
						} else {
							query += " and fault_ultimo_errore LIKE '"+fault+"' and formato_fault_ultimo_errore = ? and cluster_id_ultimo_errore = 'IDGW'";
							count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, dataRiferimentoTest, esitoNotifica, statusCode, formatoFault);
						}
						
						// debug for error
						if(count.intValue() != 1) {
							ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa:");
							String query2 = "select data_ultimo_errore, dettaglio_esito_ultimo_errore, codice_risposta_ultimo_errore, ultimo_errore, location_ultimo_errore,fault_ultimo_errore,formato_fault_ultimo_errore,cluster_id_ultimo_errore from transazioni_sa where id_transazione = ? and connettore_nome = ?";
							List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione, connettore);
							for (var v : letto) {
								ConfigLoader.getLoggerCore().error(v.toString());
							}
						}
						
						assertEquals(Integer.valueOf(1), count);
					}

					for (var connettore : requestExpectation.connettoriSuccesso) {
						
						String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
						String query = "Select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ?"
								+ " and data_uscita_richiesta > ? and data_registrazione > ? and data_ingresso_risposta >= data_uscita_richiesta"
								+ " and data_ingresso_risposta >= data_accettazione_risposta and data_uscita_richiesta = data_primo_tentativo"
								+ " and data_ultimo_errore is null and dettaglio_esito_ultimo_errore = 0 and codice_risposta_ultimo_errore is null and ultimo_errore is null and location_ultimo_errore  is null"
								+ " and data_uscita_richiesta >= data_accettazione_richiesta ";
																																		
						getLoggerCore().info("Checking date per connettori successo: " + id_transazione + " " + connettore + " " + dataRiferimentoTest.toString() );
						Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione,	connettore, dataRiferimentoTest, dataRiferimentoTest);
						
						// debug for error
						if(count.intValue() != 1) {
							ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa (dataRiferimentoTest:"+dataRiferimentoTest+"):");
							String query2 = "select data_accettazione_richiesta, data_uscita_richiesta, data_registrazione, data_ingresso_risposta, data_accettazione_risposta, data_primo_tentativo, data_ultimo_errore, dettaglio_esito_ultimo_errore, codice_risposta_ultimo_errore, ultimo_errore, location_ultimo_errore from transazioni_sa where id_transazione = ? and connettore_nome = ?";
							List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione, connettore);
							for (var v : letto) {
								ConfigLoader.getLoggerCore().error(v.toString());
							}
						}
						
						assertEquals(Integer.valueOf(1), count);
					}

				}
			}
		}

		// Attendo l'intervallo di riconsegna e controllo che le date vengano aggiornate
		CommonConsegnaMultipla.waitProssimaConsegna(responsesByKind,primaConsegna);
		
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);

			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					CommonConsegnaMultipla.checkRequestExpectationsFinal(requestExpectation, response);
					
					for (var connettore : requestExpectation.connettoriFallimento) {
						
						String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
						String key = CommonConsegnaMultipla.buildKey(response, connettore, id_transazione);
						dataRiferimentoTest = primaConsegna.get(key);					
						
						String query = "Select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ?"
								+ " and data_uscita_richiesta > ? and data_ingresso_risposta >= data_uscita_richiesta"
								+ " and data_ingresso_risposta >= data_accettazione_risposta and data_uscita_richiesta > data_primo_tentativo";
						getLoggerCore().info("Checking date per connettori fallimento: " + id_transazione + " " + connettore + " " + dataRiferimentoTest.toString() );
						Integer count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione,	connettore, dataRiferimentoTest);
						
						// debug for error
						if(count.intValue() != 1) {
							ConfigLoader.getLoggerCore().error("configurazione differente da quella attesa (dataRiferimentoTest:"+dataRiferimentoTest+"):");
							String query2 = "select data_uscita_richiesta, data_ingresso_risposta, data_accettazione_risposta, data_primo_tentativo from transazioni_sa where id_transazione = ? and connettore_nome = ?";
							List<Map<String, Object>> letto = ConfigLoader.getDbUtils().readRows(query2, id_transazione, connettore);
							for (var v : letto) {
								ConfigLoader.getLoggerCore().error(v.toString());
							}
						}
						
						assertEquals(Integer.valueOf(1), count);
						
						String fault 		 = "";
						String formatoFault = "";
						String ultimoErrore;
						String locationUltimoErrore; 

						if (requestExpectation.tipoFault == TipoFault.SOAP1_1) {
							fault 		 = CommonConsegnaMultipla.FAULT_SOAP1_1;
							formatoFault = CommonConsegnaMultipla.FORMATO_FAULT_SOAP1_1;
							ultimoErrore = "Consegna [http] con errore: errore applicativo, <SOAP-ENV:Fault xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><faultcode xmlns:ns0=\"http://www.openspcoop2.org/example\">ns0:Server.OpenSPCoopExa"
									+ "mpleFault</faultcode><faultstring>Fault ritornato dalla servlet di trace, esempio di OpenSPCoop</faultstring><faultactor>OpenSPCoopTrace</faultactor></SOAP-ENV:Fault>";
							locationUltimoErrore = "http://localhost:8080/TestService/echo?id_connettore="+connettore+"&returnCode="+requestExpectation.statusCodePrincipale+"&replyPrefixQueryParameter=GovWay-TestSuite-&fault=true&faultSoapVersion=11&replyQueryParameter=id_connettore";							
						} else if (requestExpectation.tipoFault == TipoFault.SOAP1_2) {
							fault 		 = CommonConsegnaMultipla.FAULT_SOAP1_2;
							formatoFault = CommonConsegnaMultipla.FORMATO_FAULT_SOAP1_2;
							ultimoErrore = "Consegna [http] con errore: errore applicativo, <env:Fault xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"><env:Code><env:Value>env:Receiver</env:Value><env:Subcode><env:Value xmlns:ns1=\"http://www.ope"
									+ "nspcoop2.org/example\">ns1:Server.OpenSPCoopExampleFault</env:Value></env:Subcode></env:Code><env:Reason><env:Text xml:lang=\"en-US\">Fault ritornato dalla servlet di trace, esempio di OpenSPCoop</env:Text></env:Reason><env:Role>OpenSPCoopTr"
									+ "ace</env:Role></env:Fault>";
							locationUltimoErrore = "http://localhost:8080/TestService/echo?id_connettore="+connettore+"&returnCode="+requestExpectation.statusCodePrincipale+"&replyPrefixQueryParameter=GovWay-TestSuite-&fault=true&faultSoapVersion=12&replyQueryParameter=id_connettore";
						} else {
							ultimoErrore =  "Consegna [http] con errore: errore HTTP " + requestExpectation.statusCodePrincipale;
							locationUltimoErrore = "http://localhost:8080/TestService/echo?id_connettore="+connettore+"&returnCode="+requestExpectation.statusCodePrincipale+"&replyPrefixQueryParameter=GovWay-TestSuite-&replyQueryParameter=id_connettore";
						}

						query = "Select count(*) from transazioni_sa where id_transazione = ? and connettore_nome = ? and data_ultimo_errore > ? and dettaglio_esito_ultimo_errore = ? and codice_risposta_ultimo_errore = ? and ultimo_errore LIKE '"+ultimoErrore+"' and location_ultimo_errore LIKE '"+locationUltimoErrore+"'";
						
						int esitoNotifica;
						if (requestExpectation.tipoFault != TipoFault.NESSUNO) {
							esitoNotifica = ESITO_ERRORE_APPLICATIVO;				
						} else if (isEsitoErrore(requestExpectation.statusCodePrincipale)) {			
							esitoNotifica = ESITO_ERRORE_INVOCAZIONE;
						} else {
							esitoNotifica = esitoConsegnaFromStatusCode(requestExpectation.statusCodePrincipale);
						}
						String statusCode = String.valueOf(requestExpectation.statusCodePrincipale);
						
						getLoggerCore().info("Checking info errori date per connettori fallimento: " + id_transazione + " " + connettore + " " + dataRiferimentoTest.toString() + " " + esitoNotifica + " " + statusCode + " " + ultimoErrore + " " + locationUltimoErrore );
						if (fault.isEmpty() ) {
							query += " and fault_ultimo_errore is null and formato_fault_ultimo_errore is null";
							count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, dataRiferimentoTest, esitoNotifica, statusCode);
							assertEquals(Integer.valueOf(1), count);
						} else {
							query += " and fault_ultimo_errore LIKE '"+fault+"' and formato_fault_ultimo_errore = ?";
							count = ConfigLoader.getDbUtils().readValue(query, Integer.class, id_transazione, connettore, dataRiferimentoTest, esitoNotifica, statusCode, formatoFault);
							assertEquals(Integer.valueOf(1), count);
						}
					}
					
					
				}
			}
		}
		
	}
	
	
	@Test
	public void erroreDiProcessamentoOk() {
		/**
		 *		Si testa la consegna di notifiche in caso di errore di processamento 
		 */
		
		final String erogazione = "TestConsegnaConNotificheErroreDiProcessamentoOKSoap";
		var soapContentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
		
		final String content = "<Filtro>IdApplicativo</Filtro>";
		HttpRequest requestOk = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaContenuto",   "SA_TestRegolaContenuto",  soapContentType,content);
		requestOk.setUrl(requestOk.getUrl()+"&returnCode=200");
		
		 //  Questa va su test-regola-contenuto e la correlazione applicativa fallisce perchè non vi trova il contenuto.
		//   Per questa transazione le notifiche saranno schedulate lo stesso perchè così è impostata la configurazione dell'erogazione
 		HttpRequest requestErroreProcessamentoOK =  RequestBuilder.buildSoapRequest(erogazione, "TestRegolaContenuto",   "SA_TestRegolaContenuto",  soapContentType); 	
 		requestErroreProcessamentoOK.setUrl(requestErroreProcessamentoOK.getUrl()+"&returnCode=200");

		var responsesOk = Common.makeParallelRequests(requestOk, 5);
		var responsesErroreProcessamentoOK = Common.makeParallelRequests(requestErroreProcessamentoOK, 5);

		for (var r : responsesOk) {
			assertEquals(200, r.getResultHTTPOperation());
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(r, Common.setConnettoriAbilitati.size()));
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}
		
		for (var r : responsesErroreProcessamentoOK) {
			assertEquals(500, r.getResultHTTPOperation());
			checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}

		// Attendo la consegna
		List<HttpResponse> responsesCheck = new ArrayList<HttpResponse>();
		responsesCheck.addAll(responsesOk);
		responsesCheck.addAll(responsesErroreProcessamentoOK);
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(Common.setConnettoriAbilitati);
		CommonConsegnaMultipla.waitConsegna(responsesCheck, connettoriCheck);	

		for (var r : responsesOk) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(r,  Common.setConnettoriAbilitati, ESITO_OK,  200, "", "");
		}
		for (var r : responsesErroreProcessamentoOK) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(r,  Common.setConnettoriAbilitati, ESITO_OK,  200, "", "");
		}
		
		soapContentType = HttpConstants.CONTENT_TYPE_SOAP_1_2;
		
		requestOk = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaContenuto",   "SA_TestRegolaContenuto",  soapContentType,content);
		requestOk.setUrl(requestOk.getUrl()+"&returnCode=200");
		
		 //  Questa va su test-regola-contenuto e la correlazione applicativa fallisce perchè non vi trova il contenuto.
		//   Per questa transazione le notifiche saranno schedulate lo stesso perchè così è impostata la configurazione dell'erogazione
 		requestErroreProcessamentoOK =  RequestBuilder.buildSoapRequest(erogazione, "TestRegolaContenuto",   "SA_TestRegolaContenuto",  soapContentType); 	
 		requestErroreProcessamentoOK.setUrl(requestErroreProcessamentoOK.getUrl()+"&returnCode=200");

		responsesOk = Common.makeParallelRequests(requestOk, 5);
		responsesErroreProcessamentoOK = Common.makeParallelRequests(requestErroreProcessamentoOK, 5);

		for (var r : responsesOk) {
			assertEquals(200, r.getResultHTTPOperation());
			checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}
		
		for (var r : responsesErroreProcessamentoOK) {
			assertEquals(500, r.getResultHTTPOperation());
			checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}

		// Attendo la consegna
		responsesCheck = new ArrayList<HttpResponse>();
		responsesCheck.addAll(responsesOk);
		responsesCheck.addAll(responsesErroreProcessamentoOK);
		CommonConsegnaMultipla.waitConsegna(responsesCheck, connettoriCheck);	

		for (var r : responsesOk) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(r,  Common.setConnettoriAbilitati, ESITO_OK, 200, "", "");
		}
		for (var r : responsesErroreProcessamentoOK) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(r,  Common.setConnettoriAbilitati, ESITO_OK,  200, "", "");
		}
		
		
	}
	
	
	@Test
	public void erroreDiProcessamento() {
		/**
		 *		Si testa la riconsegna di notifiche in caso di errore di processamento 
		 */
		
		final String erogazione = "TestConsegnaConNotificheErroreDiProcessamentoSoap";
		var soapContentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
		
		final String content = "<Filtro>IdApplicativo</Filtro>";
		HttpRequest requestOk = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaContenuto",   "SA_TestRegolaContenuto",  soapContentType,content);
		requestOk.setUrl(requestOk.getUrl()+"&returnCode=200");
		
		 //  Questa va su test-regola-contenuto e la correlazione applicativa fallisce perchè non vi trova l'id applicativo.
		//   Per questa transazione le notifiche non saranno schedulate
 		HttpRequest requestErroreProcessamento =  RequestBuilder.buildSoapRequest(erogazione, "TestRegolaContenuto",   "SA_TestRegolaContenuto",  soapContentType); 	
 		requestErroreProcessamento.setUrl(requestErroreProcessamento.getUrl()+"&returnCode=200");

		var responsesOk = Common.makeParallelRequests(requestOk, 5);
		var responsesErroreProcessamento = Common.makeParallelRequests(requestErroreProcessamento, 5);

		for (var r : responsesOk) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(r, Common.setConnettoriAbilitati.size()));
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}
		
		for (var r : responsesErroreProcessamento) {
			getLoggerCore().info("Checking status code for transaction: " + r.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE));
			assertEquals(500, r.getResultHTTPOperation());
			checkNessunaNotifica(r);
			CommonConsegnaMultipla.checkNessunoScheduling(r);
		}

		// Attendo la consegna
		List<HttpResponse> responsesCheck = new ArrayList<HttpResponse>();
		responsesCheck.addAll(responsesOk);
		//responsesCheck.addAll(responsesErroreProcessamento);
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(Common.setConnettoriAbilitati);
		CommonConsegnaMultipla.waitConsegna(responsesCheck, connettoriCheck);

		for (var r : responsesOk) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(r,  Common.setConnettoriAbilitati, ESITO_OK,  200, "", "");
		}
		
		soapContentType = HttpConstants.CONTENT_TYPE_SOAP_1_2;
		
		requestOk = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaContenuto",   "SA_TestRegolaContenuto",  soapContentType,content);
		requestOk.setUrl(requestOk.getUrl()+"&returnCode=200");
		
		 //  Questa va su test-regola-contenuto e la correlazione applicativa fallisce perchè non vi trova il contenuto.
		//   Per questa transazione le notifiche non saranno schedulate
 		requestErroreProcessamento =  RequestBuilder.buildSoapRequest(erogazione, "TestRegolaContenuto",   "SA_TestRegolaContenuto",  soapContentType); 	
 		requestErroreProcessamento.setUrl(requestErroreProcessamento.getUrl()+"&returnCode=200");

		responsesOk = Common.makeParallelRequests(requestOk, 5);
		responsesErroreProcessamento = Common.makeParallelRequests(requestErroreProcessamento, 5);

		for (var r : responsesOk) {
			assertEquals(200, r.getResultHTTPOperation());
			checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);	
		}
		
		for (var r : responsesErroreProcessamento) {
			getLoggerCore().info("Checking status code for transaction: " + r.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE));
			assertEquals(500, r.getResultHTTPOperation());
			checkNessunaNotifica(r);
			CommonConsegnaMultipla.checkNessunoScheduling(r);
		}

		// Attendo la consegna
		responsesCheck = new ArrayList<HttpResponse>();
		responsesCheck.addAll(responsesOk);
		//responsesCheck.addAll(responsesErroreProcessamento);
		CommonConsegnaMultipla.waitConsegna(responsesCheck, connettoriCheck);

		for (var r : responsesOk) {
			CommonConsegnaMultipla.checkConsegnaCompletata(r);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(r,  Common.setConnettoriAbilitati, ESITO_OK,  200, "", "");
		}
		
	}
	

	
	@Test
	public void schedulingAbilitatoDisabilitato() throws UtilsException, HttpUtilsException {
		/*
		 * Prima si disabilita lo scheduling su di un connettore, si verifica lo stato sul db e poi si riattiva
		 * lo scheduling.
		 */
		
		final String erogazione = "TestConsegnaConNotificheSchedulingAbilitatoDisabilitatoSoap";
		
		CommonConsegnaMultipla.jmxDisabilitaSchedulingConnettore(erogazione, Common.CONNETTORE_1);
		
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		
		var responses = Common.makeParallelRequests(request1, 5);
		var responses2 = Common.makeParallelRequests(request2, 5);
		Common.checkAll200(responses);
		Common.checkAll200(responses2);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(r, Common.setConnettoriAbilitati.size()));
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
		}
		
		for (var r : responses2) {
			checkPresaInConsegna(r, Common.setConnettoriAbilitati.size());
			checkSchedulingConnettoreIniziato(r, Common.setConnettoriAbilitati);
		}
		
		List<String> connettori = new ArrayList<String>();
		connettori.add(Common.CONNETTORE_0);
		connettori.add(Common.CONNETTORE_2);
		connettori.add(Common.CONNETTORE_3);
		CommonConsegnaMultipla.waitConsegna(responses, connettori);
		
		String fault = "";
		String formatoFault = "";
		for (var response : responses) {
			// Sul connettore disabilitato non è avvenuto nulla ancora.
			checkSchedulingConnettoreIniziato(response, Common.CONNETTORE_1);
			checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0, ESITO_OK, 200, fault, formatoFault);
			checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2, ESITO_OK, 200, fault, formatoFault);
			checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3, ESITO_OK, 200, fault, formatoFault);
			checkStatoConsegna(response, ESITO_CONSEGNA_MULTIPLA_IN_CORSO,  1);
		}
		
		for (var response : responses2) {
			// Sul connettore disabilitato non è avvenuto nulla ancora.
			checkSchedulingConnettoreIniziato(response, Common.CONNETTORE_1);
			checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0,ESITO_OK, 200, fault, formatoFault);
			checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2, ESITO_OK, 200, fault, formatoFault);
			checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3, ESITO_OK, 200, fault, formatoFault);
			checkStatoConsegna(response, ESITO_CONSEGNA_MULTIPLA_IN_CORSO,  1);
		}
				
		CommonConsegnaMultipla.jmxAbilitaSchedulingConnettore(erogazione, Common.CONNETTORE_1);
		
		// Attendo la consegna sul connettore appena abilitato
		
		connettori = new ArrayList<String>();
		connettori.add(Common.CONNETTORE_1);
		CommonConsegnaMultipla.waitConsegna(responses, connettori);
		
		for (var response : responses) {
			checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_1,ESITO_OK, 200, fault, formatoFault);
			checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0,ESITO_OK, 200, fault, formatoFault);
			checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2,ESITO_OK, 200, fault, formatoFault);
			checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3,ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
		}
		
		for (var response : responses2) {
			checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_1,ESITO_OK, 200, fault, formatoFault);
			checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_0,ESITO_OK, 200, fault, formatoFault);
			checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_2,ESITO_OK, 200, fault, formatoFault);
			checkSchedulingConnettoreCompletato(response, Common.CONNETTORE_3,ESITO_OK, 200, fault, formatoFault);
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
		}
		
	}

	
	@Test
	public void consegnaConNotificheNonCondizionale() {
		// Non c'è la condizionalità sulla transazione sincrona, per cui si comporta come una consegna multipla classica
		// Questa non la divido anche per i 3xx e 5xx visto che stiamo testando tutt'altra funzionalità

		final String erogazione = "TestConsegnaConNotificheNonCondizionaleSoap";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		int i = 0;
		for (var entry : CommonConsegnaMultipla.statusCode2xx4xxVsConnettori.entrySet()) {
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
				ESITO_CONSEGNA_MULTIPLA_FALLITA,500, true,TipoFault.SOAP1_1));
		
		requestSoapFault = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFault,
				Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
				Set.of(CONNETTORE_1),  
				ESITO_CONSEGNA_MULTIPLA_FALLITA,500, true,TipoFault.SOAP1_2));
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
		for (var responses : responsesByKind.values()) {
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size()));
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
	public void consegnaConNotificheErrori() {
		/**
		 * Nel primo test verifico il funzionamento senza particolari condizioni: invio una richiesta
		 * e verifico le consegne. Qui invece inizio facendo fallire qualche richiesta.
		 * I connettori file non posso istruirli, quindi uso tutti connettori http.
		 */
		
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
					false,
					TipoFault.SOAP1_1);
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
				CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size()));
				checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
			} else {
				for (var response : responses) {
					CommonConsegnaMultipla.withBackoff( () -> checkNessunaNotifica(response));
					CommonConsegnaMultipla.checkNessunoScheduling(response);
				}
			}
		}
		
		// Attendo la prima consegna
		Map<String, java.sql.Timestamp> primaConsegna = CommonConsegnaMultipla.waitPrimaConsegna(responsesByKind);
		
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			
			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					CommonConsegnaMultipla.checkRequestExpectations(requestExpectation, response);
				}
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		CommonConsegnaMultipla.waitProssimaConsegna(responsesByKind,primaConsegna);
				
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
		
		var responses = Common.makeParallelRequests(request1, 5);
		var responses2 = Common.makeParallelRequests(request2, 5);
		
		for (var r : responses) {
			// Le richieste che vedono fallita la transazione principale con un 401, ottengono un 500
			assertEquals(500 , r.getResultHTTPOperation());
			CommonConsegnaMultipla.withBackoff( () -> checkNessunaNotifica(r));
			CommonConsegnaMultipla.checkNessunoScheduling(r);
		}
		
		for (var r : responses2) {
			// Le richieste che vedono fallita la transazione principale, ottengono un 500
			assertEquals(500 , r.getResultHTTPOperation());
			checkNessunaNotifica(r);
			CommonConsegnaMultipla.checkNessunoScheduling(r);
		}
	}
	
	
	public void varieCombinazioniDiRegole(Map<Integer, Set<String>> statusCodeVsConnettori) {
		// La consegna sincrona ha successo in caso di 200 o di soap fault. In questo test alcune transazioni
		// principali falliscono, altre no e per queste viene schedulata la consegna. Controllo che tale scheduling sia corretto.
		final String erogazione = "TestConsegnaConNotificheSoapVarieCombinazioniDiRegole";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		int i = 0;
		for (var entry : statusCodeVsConnettori.entrySet()) {
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
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, true, TipoFault.SOAP1_1));
		
		requestSoapFault = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		requestsByKind.add(new RequestAndExpectations(
				requestSoapFault,
				Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3),
				Set.of(CONNETTORE_1),  
				ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, true, TipoFault.SOAP1_2));
		
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		// Le richieste per cui la transazione sincrona ha avuto successo devono essere schedulate
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			
			if (requestExpectation.principaleSuperata) {
				CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size()));
				checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
			} else {
				for (var response : responses) {
					CommonConsegnaMultipla.withBackoff( () -> checkNessunaNotifica(response));
					CommonConsegnaMultipla.checkNessunoScheduling(response);
				}
			}
		}
		
		// Attendo la prima consegna
		Map<String, java.sql.Timestamp> primaConsegna = CommonConsegnaMultipla.waitPrimaConsegna(responsesByKind);
		
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			
			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					CommonConsegnaMultipla.checkRequestExpectations(requestExpectation, response);
				}
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		CommonConsegnaMultipla.waitProssimaConsegna(responsesByKind,primaConsegna);
				
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
			
			for(int i=0; i<4;i++) {
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
			
			HttpRequest requestSoapFault =  RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
			requestsByKind.add(new RequestAndExpectations(requestSoapFault, Common.setConnettoriAbilitati, Set.of(),  ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, true, TipoFault.SOAP1_1));
			
			requestSoapFault =  RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
			requestsByKind.add(new RequestAndExpectations(requestSoapFault, Common.setConnettoriAbilitati, Set.of(),  ESITO_CONSEGNA_MULTIPLA_FALLITA, 500, true, TipoFault.SOAP1_2));
			
			
			Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
			
			// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia su tutti i connettori abilitati
			// Solo Le richieste per cui la transazione sincrona ha avuto successo devono essere schedulate
			for (var requestExpectation : responsesByKind.keySet()) {
				var responses = responsesByKind.get(requestExpectation);
				
				if (requestExpectation.principaleSuperata) {
					CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(responses, Common.setConnettoriAbilitati.size()));
					checkSchedulingConnettoreIniziato(responses, Common.setConnettoriAbilitati);
				} else {
					for (var response : responses) {
						CommonConsegnaMultipla.withBackoff( () -> checkNessunaNotifica(response));
						CommonConsegnaMultipla.checkNessunoScheduling(response);
					}
				}
			}
			
			// Attendo la prima consegna
			Map<String, java.sql.Timestamp> primaConsegna = CommonConsegnaMultipla.waitPrimaConsegna(responsesByKind);

			// Adesso devono essere state effettuate le consegne
			for (var requestExpectation : responsesByKind.keySet()) {
				var responses = responsesByKind.get(requestExpectation);
				
				if (requestExpectation.principaleSuperata) {
					for (var response : responses) {
						CommonConsegnaMultipla.checkRequestExpectations(requestExpectation, response);
					}
				}
			}
			
			// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
			CommonConsegnaMultipla.waitProssimaConsegna(responsesByKind,primaConsegna,CONNETTORE_0);
			
			for (var requestAndExpectation : responsesByKind.keySet()) {
				if (requestAndExpectation.principaleSuperata) {

					for (var response : responsesByKind.get(requestAndExpectation)) {

						for (var connettore : requestAndExpectation.connettoriFallimento) {
							if (connettore.equals(CONNETTORE_0)) {
								assertTrue(getNumeroTentativiSchedulingConnettore(response, connettore) == 1);
							} else {
								assertTrue(getNumeroTentativiSchedulingConnettore(response, connettore) >= 2);
							}
						}

						checkStatoConsegna(response, requestAndExpectation.esitoPrincipale,
								requestAndExpectation.connettoriFallimento.size());
					}
				}
			}
			
			// Attendo un minuto, anche il connettore 0 ora deve avere una rispedizione in più
			ConfigLoader.getLoggerCore().info("@CadenzaRispedizione test in corso, attendo 1 minuto ...");
			org.openspcoop2.utils.Utilities.sleep(1000*60);
			org.openspcoop2.utils.Utilities.sleep(CommonConsegnaMultipla.intervalloControllo);
			
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
		final String erogazione = "TestConsegnaConNotificheConnettoreRottoSoap";
		
		// Va come il primo test, ci si aspetta che funzionino tutti i connettori
		// eccetto quello rotto, dove devono avvenire le rispedizioni.
		
		HttpRequest request1 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		HttpRequest request2 = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);

		var responses = Common.makeParallelRequests(request1, 5);
		var responses2 = Common.makeParallelRequests(request2, 5);
		
		Common.checkAll200(responses);
		Common.checkAll200(responses2);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna		
   	   	Set<String> connettoriSchedulati = new HashSet<>(Common.setConnettoriAbilitati);
   	   	connettoriSchedulati.add(CONNETTORE_ROTTO);

		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegna(r, connettoriSchedulati.size()));
			checkSchedulingConnettoreIniziato(r, connettoriSchedulati);	
		}
		for (var r : responses2) {
			assertEquals(200, r.getResultHTTPOperation());
			checkPresaInConsegna(r, connettoriSchedulati.size());
			checkSchedulingConnettoreIniziato(r, connettoriSchedulati);	
		}
	
		// Attendo la consegna
		List<HttpResponse> responsesCheck = new ArrayList<HttpResponse>();
		responsesCheck.addAll(responses);
		responsesCheck.addAll(responses2);
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(connettoriSchedulati);
		CommonConsegnaMultipla.waitConsegna(responsesCheck, connettoriCheck);
	
		// La consegna deve risultare ancora in corso per via dell'errore sul connettore rotto, con un connettore
		// in rispedizione
		String fault = "";
		String formatoFault = "";
		for (var r : responses) {
			CommonConsegnaMultipla.withBackoff( () -> checkStatoConsegna(r, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1));
			checkSchedulingConnettoreInCorso(r, CONNETTORE_ROTTO, ESITO_ERRORE_INVOCAZIONE, 0, fault, formatoFault);
			for (var connettore : Common.setConnettoriAbilitati) {
				checkSchedulingConnettoreCompletato(r, connettore, ESITO_OK, 200, fault, formatoFault);
			}
		}
		
		for (var r : responses2) {
			checkStatoConsegna(r, ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			checkSchedulingConnettoreInCorso(r, CONNETTORE_ROTTO, ESITO_ERRORE_INVOCAZIONE, 0, fault, formatoFault);
			for (var connettore : Common.setConnettoriAbilitati) {
				checkSchedulingConnettoreCompletato(r, connettore, ESITO_OK, 200, fault, formatoFault);
			}
		}
	}
	
	
	@Test
	public void integrationManager() throws MalformedURLException, IntegrationManagerException_Exception {
		// Solo il connettore0 usa il servizio di message box, gli altri completano con 
		// 200 e soap fault

		String erogazione 							= "TestConsegnaConNotificheIntegrationManager";
		String connettoreMessageBox 	= Common.CONNETTORE_0;
		String username	= "UtenteTestIntegrationManager";
		String password 	= "YRpyf8)Zq4Z34kv27vJD";
		Set<String> connettoriSuccesso = Set.of(Common.CONNETTORE_1,Common.CONNETTORE_2,Common.CONNETTORE_3);
		
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
		List<String> connettoriCheck = new ArrayList<String>();
		connettoriCheck.addAll(connettoriSuccesso);
		CommonConsegnaMultipla.waitConsegna(responsesCheck, connettoriCheck);

		// Controllo che le notifiche siano completate sui connettori non message box
		for (var response : responsesSoap1) {
			checkStatoConsegna(response, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			for (var connettore : connettoriSuccesso) {
				checkSchedulingConnettoreCompletato(response, connettore,ESITO_OK, 200, "" , "");
			}
			// Controllo che lo scheduling sia stato effettuato solo sui connettori indicati, ci metto anche quello che fa da MessageBox
			CommonConsegnaMultipla.checkConnettoriRaggiuntiEsclusivamente(response, Common.setConnettoriAbilitati);
		}
		
		// ripeto per soap2
		for (var response : responsesSoap2) {
			checkStatoConsegna(response, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO, 1);
			for (var connettore : connettoriSuccesso) {
				checkSchedulingConnettoreCompletato(response, connettore,ESITO_OK, 200, "" , "");			
			}
			// Controllo che lo scheduling sia stato effettuato solo sui connettori indicati, ci metto anche quello che fa da MessageBox
			CommonConsegnaMultipla.checkConnettoriRaggiuntiEsclusivamente(response, Common.setConnettoriAbilitati);		
		}
		
		// Recupero i messaggi
		// ID messaggi
		CommonConsegnaMultipla.checkMessageBox(connettoreMessageBox, imMessageBoxPort);
		
		// Adesso cancello tutti i messaggi in pancia in modo che la consegna risulti completata
		imMessageBoxPort.deleteAllMessages();
		
		responsesCheck = new ArrayList<HttpResponse>();
		responsesCheck.addAll(responsesSoap1);
		responsesCheck.addAll(responsesSoap2);
		CommonConsegnaMultipla.waitConsegna(responsesCheck, connettoriCheck);
		
		for (var response : responsesSoap1) {
			checkStatoConsegna(response, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_COMPLETATA, 0);
			CommonConsegnaMultipla.checkConsegnaTerminataNoStatusCode(response, connettoreMessageBox);
		}
		for (var response : responsesSoap2) {
			checkStatoConsegna(response, CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_COMPLETATA, 0);
			CommonConsegnaMultipla.checkConsegnaTerminataNoStatusCode(response, connettoreMessageBox);
		}

	}
			

		
}
