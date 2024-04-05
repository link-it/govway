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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_con_notifiche;

import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_3;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.DIAGNOSTICO_SEVERITA_INFO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.buildRequestAndExpectationFiltered;

import java.util.ArrayList;
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
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectations;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * CondizionaleByNomeSoapTest
 * 
 * @author Francesco Scarlato
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 * QUERY:
 * TestContenuto: //Filtro/text()
 * Template: ${query:govway-testsuite-id_connettore_request}
 * Freemarker: ${query["govway-testsuite-id_connettore_request"]}
 * Velocity:		#if($query.containsKey("govway-testsuite-id_connettore_request"))
							$query["govway-testsuite-id_connettore_request"]
						#end
 * UrlInvocazione: .+govway-testsuite-id_connettore_request=([^&]*).*
 * 
http://localhost:8080/TestService/echo?id_connettore=ConnettorePrincipale

 */
public class CondizionaleByNomeSoapTest extends ConfigLoader {

	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
	}

	@AfterClass
	public static void After() {
		Common.fermaRiconsegne(dbUtils);
	}

	@org.junit.After
	public void AfterEach() {
		Common.fermaRiconsegne(dbUtils);
	}

	@Test
	public void headerHttp2xx_4xx() {
		headerHttp(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void headerHttp5xx() {
		headerHttp(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	
	
	@Test
	public void parametroUrl2xx_4xx() {
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeParametroUrl";
		parametroUrl_Impl(erogazione, CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	
	@Test
	public void parametroUrl5xx() {
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeParametroUrl";
		parametroUrl_Impl(erogazione, CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	
	
	@Test
	public void template2xx_4xx() {
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeTemplate";
		parametroUrl_Impl(erogazione, CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);		
	}
	@Test
	public void template5xx() {
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeTemplate";
		parametroUrl_Impl(erogazione, CommonConsegnaMultipla.statusCode5xxVsConnettori);		
	}
	
	
	@Test
	public void velocityTemplate2xx_4xx() {
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeVelocityTemplate";
		parametroUrl_Impl(erogazione,CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);		
	}
	@Test
	public void velocityTemplate5xx() {
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeVelocityTemplate";
		parametroUrl_Impl(erogazione,CommonConsegnaMultipla.statusCode5xxVsConnettori);		
	}
	
	
	@Test
	public void freemarkerTemplate2xx_4xx() {
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeFreemarkerTemplate";
		parametroUrl_Impl(erogazione,CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);		
	}
	@Test
	public void freemarkerTemplate5xx() {
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeFreemarkerTemplate";
		parametroUrl_Impl(erogazione,CommonConsegnaMultipla.statusCode5xxVsConnettori);		
	}
	
	
	@Test
	public void clientIp2xx_4xx() {
		clientIp(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void clientIp5xx() {
		clientIp(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	
	
	@Test
	public void soapAction2xx_4xx() {
		soapAction(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void soapAction5xx() {
		soapAction(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	
	
	@Test
	public void contenutoSuffisso2xx_4xx() {
		contenutoSuffisso(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void contenutoSuffisso5xx() {
		contenutoSuffisso(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	
	
	@Test
	public void urlInvocazionePrefisso2xx_4xx() {
		urlInvocazionePrefisso(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void urlInvocazionePrefisso5xx() {
		urlInvocazionePrefisso(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	
	
	@Test
	public void XForwardedForPrefissoESuffisso2xx_4xx() throws UtilsException {
		XForwardedForPrefissoESuffisso(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void XForwardedForPrefissoESuffisso5xx() throws UtilsException {
		XForwardedForPrefissoESuffisso(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	
	
	@Test
	public void parametroUrlNCUNessunConnettore2xx_4xx() throws UtilsException {
		parametroUrlNCUNessunConnettore(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void parametroUrlNCUNessunConnettore5xx() throws UtilsException {
		parametroUrlNCUNessunConnettore(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	
	
	@Test
	public void parametroUrlICFNessunConnettore2xx_4xx() throws UtilsException {
		parametroUrlICFNessunConnettore(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void parametroUrlICFNessunConnettore5xx() throws UtilsException {
		parametroUrlICFNessunConnettore(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}

	@Test
	public void regole200() throws UtilsException {
		final Map<Integer, Set<String>> statusCode200VsConnettori  = Map
				.of(200, Set.of(CONNETTORE_1,CONNETTORE_2));
		regole(statusCode200VsConnettori);
	}
	@Test 
	public void regole501() throws UtilsException {
		final Map<Integer, Set<String>> statusCode501VsConnettori  = Map
				.of(501, Set.of(CONNETTORE_1, CONNETTORE_3));
		regole(statusCode501VsConnettori);
	}
	
	public void headerHttp(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeHeaderHttp";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		int i = 0;
		
		for (var entry : statusCodeVsConnettori.entrySet()) {
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			final int statusCode = entry.getKey();
			final Set<String> connettoriSuccesso = entry.getValue();

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.addHeader(Common.HEADER_ID_CONDIZIONE, filtro);			
			var current = buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso,Set.of(filtro));
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}			
			if (statusCode > 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			requestsByKind.add(current);
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CondizionaleByFiltroSoapTest.checkResponses(responsesByKind);
	}
		
	
	public void soapAction(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		//		Errori di consegna
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeSoapAction";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		var azioniBySoapAction = Map.of(
				"Connettore0", "TestCondizionaleByNome0",
				"Connettore1", "TestCondizionaleByNome1",
				"Connettore2", "TestCondizionaleByNome2",
				"Connettore3", "TestCondizionaleByNome3");
		
		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {			

			int statusCode = entry.getKey();
			
			Set<String> connettoriSuccesso = entry.getValue();
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			Set<String> connettoriPool = Set.of(filtro);

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, azioniBySoapAction.get(filtro),   filtro,  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, connettoriPool);
			if (statusCode == 401) {
				current.esitoSincrono = CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE; 
			}
			requestsByKind.add(current);
			i++;
		}
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		CondizionaleByFiltroSoapTest.checkResponses(responsesByKind);
	}
	
	
	public void clientIp(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		// 		Finiscono tutte sul primo connettore
		
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeClientIp";
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		int i = 0;

		for (var entry : statusCodeVsConnettori.entrySet()) {
			int statusCode = entry.getKey();
			Set<String> connettoriSuccesso = entry.getValue();
			Set<String> connettoriPool = Set.of("127.0.0.1");
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			// Sostituisco Il Connettore0 con "127.0.0.1"
			connettoriSuccesso = connettoriSuccesso.stream()
					.map( c -> c.equals(Common.CONNETTORE_0) ? "127.0.0.1" : c)
					.collect(Collectors.toSet());
					
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, connettoriPool);
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}			
			if (statusCode > 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			requestsByKind.add(current);
			
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CondizionaleByFiltroSoapTest.checkResponses(responsesByKind);
	}
	
	
	public void regole(Map<Integer,Set<String>> statusCodeVsConnettori) throws UtilsException {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		//		ErroreDiConsegna
		
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeRegole";
		final String CONNETTORE_LOCALHOST = "127.0.0.1";

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		List<String> forwardingHeaders = HttpUtilities.getClientAddressHeaders();	
		
		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			final Set<String> connettoriPool = Set.of(filtro);
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			final int statusCode = entry.getKey();
			final Set<String> connettoriSuccesso = entry.getValue();

			// Filtro HeaderHttp
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaHeaderHttp",   "SA_TestRegolaHeaderHttp",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.addHeader(Common.HEADER_ID_CONDIZIONE, filtro);			
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			if (statusCode == 401) {
				current.esitoSincrono = CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE; 
			}
			requestsByKind.add(current);
			
			// Filtro ParametroUrl
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaParametroUrl",   "SA_TestRegolaParametroUrl",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			if (statusCode == 401) {
				current.esitoSincrono = CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE; 
			}
			requestsByKind.add(current);
			
			// Filtro Regola Statica, vanno tutte sul  connettore0
			Set<String> connettoriPoolStatica = Set.of(Common.CONNETTORE_0);
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaStatica",   "SA_TestRegolaStatica",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPoolStatica);
			if (statusCode == 401) {
				current.esitoSincrono = CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE; 
			}
			requestsByKind.add(current);
			
			// Filtro Regola ClientI Ip, vanno tutte sul Connettore a parte per il client ip
			// Il connettore localhost ha la stessa configurazione del connettore0 ovvero passa solo con soapFault, per tutto il resto fallisce.
			// Non è necessario perciò aggiungerlo alla map statusCodeVsConnettori, dato che sarà un connettore che vedrà solo rispedizioni.
			// Per come è costruita la buildRequestAndExpectationFiltered, il connettore localhost va a finire direttamente nei connettori 
			// da considerare falliti.
			Set<String> connettoriPoolClientIp = Set.of(CONNETTORE_LOCALHOST);
			
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaClientIp",   "SA_TestRegolaClientIp",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPoolClientIp);
			if (statusCode == 401) {
				current.esitoSincrono = CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE; 
			}
			requestsByKind.add(current);
			
			
			// Filtro Regola Contenuto
			String content = "<Filtro>"+filtro+"</Filtro>";
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaContenuto",   "SA_TestRegolaContenuto",  soapContentType, content);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			if (statusCode == 401) {
				current.esitoSincrono = CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE; 
			}
			requestsByKind.add(current);
			
			// Filtro Regola FreemarkerTemplate
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaFreemarkerTemplate",   "SA_TestRegolaFreemarkerTemplate",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			if (statusCode == 401) {
				current.esitoSincrono = CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE; 
			}
			requestsByKind.add(current);

			// Filtro Regola Velocity Template
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaVelocityTemplate",   "SA_TestRegolaVelocityTemplate",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			if (statusCode == 401) {
				current.esitoSincrono = CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE; 
			}
			requestsByKind.add(current);
			
			// Filtro Regola Template
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaTemplate",   "SA_TestRegolaTemplate",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			if (statusCode == 401) {
				current.esitoSincrono = CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE; 
			}
			requestsByKind.add(current);
			
			// Filtro Regola SoapAction
			request = RequestBuilder.buildSoapRequest(erogazione, "TestCondizionaleByNome0",   Common.CONNETTORE_0,  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, Set.of(Common.CONNETTORE_0));
			if (statusCode == 401) {
				current.esitoSincrono = CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE; 
			}
			requestsByKind.add(current);
			
			// Filtro Regola Url Invocazione
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaUrlInvocazione",   "SA_TestRegolaUrlInvocazione",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			if (statusCode == 401) {
				current.esitoSincrono = CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE; 
			}
			requestsByKind.add(current);
			
			// Filtro Regola XForwardedFor
			String header = forwardingHeaders.get(i%forwardingHeaders.size());
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaXForwardedFor",   "SA_TestRegolaXForwardedFor",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.addHeader(header, filtro);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			if (statusCode == 401) {
				current.esitoSincrono = CommonConsegnaMultipla.ESITO_ERRORE_INVOCAZIONE; 
			}
			requestsByKind.add(current);

			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CondizionaleByFiltroSoapTest.checkResponses(responsesByKind);
	}
	
	
	public void contenutoSuffisso(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeContenutoSuffisso";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		int i = 0;
		
		for (var entry : statusCodeVsConnettori.entrySet()) {
			int statusCode = entry.getKey();
			// Aggiungo i suffissi ai connettori da testare
			Set<String> connettoriSuccesso = entry.getValue().stream()
					.map( c -> c + "-SuffissoTest")
					.collect(Collectors.toSet());
			
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			final String content = "<Filtro>"+filtro+"</Filtro>";

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType,content);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			
			var current = buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of(filtro+"-SuffissoTest"));
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}			
			if (statusCode > 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			requestsByKind.add(current);
			
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CondizionaleByFiltroSoapTest.checkResponses(responsesByKind);
	}
	

	public void urlInvocazionePrefisso(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeUrlInvocazionePrefisso";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		final String prefisso = "Connettore";

		int i = 0;
		
		for (var entry : statusCodeVsConnettori.entrySet()) {
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			final String connettore =Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size()); 
			final String filtro = connettore.substring(prefisso.length());
			final int statusCode = entry.getKey();

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			
			var current = buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), Set.of(connettore));
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}			
			if (statusCode > 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			requestsByKind.add(current);
			
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CondizionaleByFiltroSoapTest.checkResponses(responsesByKind);
	}
	
	
	public void XForwardedForPrefissoESuffisso(Map<Integer,Set<String>> statusCodeVsConnettori) throws UtilsException {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		final String erogazione = "TestConsegnaConNotificheByNomeXForwardedForPrefissoESuffisso";

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		List<String> forwardingHeaders = HttpUtilities.getClientAddressHeaders();
		
		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			final int statusCode = entry.getKey();
			
			// Aggiungo i suffissi ai connettori da testare
			Set<String> connettoriSuccesso = entry.getValue().stream()
					.map( c -> "PrefissoTest-" + c + "-SuffissoTest")
					.collect(Collectors.toSet());
			
			String header = forwardingHeaders.get(i%forwardingHeaders.size());
			String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(header, filtro);
			
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, Set.of("PrefissoTest-"+filtro+"-SuffissoTest"));
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}			
			if (statusCode > 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			requestsByKind.add(current);
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		CondizionaleByFiltroSoapTest.checkResponses(responsesByKind);
	}
	
	
	public void parametroUrlNCUNessunConnettore(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		//	Nessun connettore notificato in caso di NCU

		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeParametroUrlNCUNessunConnettore";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			final int statusCode = entry.getKey();
			final Set<String> connettoriSuccesso = entry.getValue();
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, Set.of(filtro));
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}			
			if (statusCode > 500 && statusCode <= 599) {
				current.principaleSuperata = false;
			}
			requestsByKind.add(current);
			i++;
		}
		
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request=FiltroInesistente");
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of());
		current.principaleSuperata = false;
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
		
		statusCode = 202;
		connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_2);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request=FiltroInesistente");
		current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of());
		current.principaleSuperata = false;
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		CondizionaleByFiltroSoapTest.checkResponses(responsesByKind);

		String CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_INFO_2 = "007044";
		String CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR_2 = "007043";
		
		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		for (var toCheck: toCheckForDiagnostici) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				
				IdentificazioneFallitaTest.checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_INFO_2);
				IdentificazioneFallitaTest.checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR_2);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(
						id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_NOTIFICHE_NESSUN_CONNETTORE_TROVATO_UTILIZZO_NESSUN_CONNETTORE,	
						"Il messaggio di richiesta non verrà notificato ad alcun connettore poichè la condizione estratta dalla richiesta non ha permesso di identificare alcun connettore");
			}
		}
		
	}
	
	
	public void parametroUrlICFNessunConnettore(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		//	Nessun connettore notificato in caso di ICF

		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeParametroUrlICFNessunConnettore";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			final int statusCode = entry.getKey();
			final Set<String> connettoriSuccesso = entry.getValue();
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, Set.of(filtro));
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}			
			if (statusCode > 500 && statusCode <= 599) {
				current.principaleSuperata = false;
			}
			requestsByKind.add(current);
			i++;
		}
		
		// Richiesta ICF
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&parametroSbagliato=FiltroInesistente");
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of());
		current.principaleSuperata = false;
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
		
		statusCode = 202;
		connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_2);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&parametroSbagliato=FiltroInesistente");
		current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of());
		current.principaleSuperata = false;
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		CondizionaleByFiltroSoapTest.checkResponses(responsesByKind);
		
		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		for (var toCheck: toCheckForDiagnostici) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				
				IdentificazioneFallitaTest.checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR);
				IdentificazioneFallitaTest.checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(
						id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						"007050",
						"Il messaggio di richiesta non verrà notificato ad alcun connettore poichè l'identificazione condizionale è fallita");
			}
		}
		
	}
	
	
	private static void parametroUrl_Impl(String erogazione, Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		int i = 0;
		
		for (var entry : statusCodeVsConnettori.entrySet()) {
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			final int statusCode = entry.getKey();
			final Set<String> connettoriSuccesso = entry.getValue();
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			var current = buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso,Set.of(filtro));
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}			
			if (statusCode > 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			requestsByKind.add(current);
			
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CondizionaleByFiltroSoapTest.checkResponses(responsesByKind);
	}

}
