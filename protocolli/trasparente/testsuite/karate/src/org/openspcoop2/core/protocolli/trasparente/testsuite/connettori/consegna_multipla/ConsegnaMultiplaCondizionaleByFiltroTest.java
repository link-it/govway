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

import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_3;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_UTILIZZO_CONNETTORE_DEFAULT;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_UTILIZZO_TUTTI_CONNETTORI;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.DIAGNOSTICO_SEVERITA_ERROR;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.DIAGNOSTICO_SEVERITA_INFO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_FALLBACK;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_FALLBACK_TUTTI_CONNETTORI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * ConsegnaMultiplaCondizionaleByFiltroTest
 * 
 * @author Francesco Scarlato
 * @author $Author$
 * @version $Rev$, $Date$
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
 * http://localhost:8080/TestService/echo?id_connettore=ConnettorePrincipale

 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConsegnaMultiplaCondizionaleByFiltroTest extends ConfigLoader {
	
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
	public void headerHttpICFDiagnosticoInfo_2xx_4xx() {
		headerHttpICFDiagnosticoInfo(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void headerHttpICFDiagnosticoInfo_5xx() {
		headerHttpICFDiagnosticoInfo(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	
	
	@Test
	public void XForwardedForPrefissoESuffisso_2xx_4xx() throws UtilsException {
		XForwardedForPrefissoESuffisso(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void XForwardedForPrefissoESuffisso_5xx() throws UtilsException {
		XForwardedForPrefissoESuffisso(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	
	
	@Test
	public void clientIp_2xx_4xx() {
		clientIp(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void clientIp_5xx() {
		clientIp(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	

	@Test
	public void contenutoSuffisso2xx_4xx() {
		contenutoSuffisso(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void contenutoSuffisso_5xx() {
		contenutoSuffisso(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}

	
	@Test
	public void urlInvocazionePrefisso2xx_4xx() {
		urlInvocazionePrefisso(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void urlInvocazionePrefisso_5xx() {
		urlInvocazionePrefisso(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}

	
	@Test
	public void ICFDiagnosticoErrorNCUDiagnosticoInfo2xx_4xx() {
		ICFDiagnosticoErrorNCUDiagnosticoInfo(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void ICFDiagnosticoErrorNCUDiagnosticoInfo_5xx() {
		ICFDiagnosticoErrorNCUDiagnosticoInfo(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}	

	
	@Test
	public void freemarkerTemplateICFQualsiasiNoDiagnostico2xx_4xx() {
		freemarkerTemplateICFQualsiasiNoDiagnostico(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void freemarkerTemplateICFQualsiasiNoDiagnostico_5xx() {
		freemarkerTemplateICFQualsiasiNoDiagnostico(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	
	
	@Test
	public void velocityTemplateNCUQualsiasiNoDiagnostico2xx_4xx() {
		velocityTemplateNCUQualsiasiNoDiagnostico(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void velocityTemplateNCUQualsiasiNoDiagnostico_5xx() {
		velocityTemplateNCUQualsiasiNoDiagnostico(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	
	
	@Test
	public void templateICFDiagnosticoError2xx_4xx() {
		templateICFDiagnosticoError(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void templateICFDiagnosticoError_5xx() {
		templateICFDiagnosticoError(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	
	
	@Test
	public void parametroUrlNCUDiagnosticoInfo2xx_4xx() {
		parametroUrlNCUDiagnosticoInfo(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void parametroUrlNCUDiagnosticoInfo_5xx() {
		parametroUrlNCUDiagnosticoInfo(CommonConsegnaMultipla.statusCode5xxVsConnettori);
	}
	
	@Test
	public void soapActionNCUDiagnosticoError2xx_4xx() {
		soapActionNCUDiagnosticoError(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void soapActionNCUDiagnosticoError_5xx() {
		soapActionNCUDiagnosticoError(CommonConsegnaMultipla.statusCode5xxVsConnettori);
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

	
	private void headerHttpICFDiagnosticoInfo(Map<Integer, Set<String>> statusCodeVsConnettori) {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroHeaderHttpICFDiagnosticoInfo";
		
		// Il connettore di fallback è il 2

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(Common.HEADER_ID_CONDIZIONE, Common.filtriPools.get(pool).get(0));
			
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			i++;
		}
		
		// Request identificazione fallita, in questo caso viene rediretta sul connettore2
		String pool = Common.POOL_0;
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.addHeader(Common.HEADER_ID_CONDIZIONE+"-SBAGLIATO", Common.filtriPools.get(pool).get(0));
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of(Common.CONNETTORE_2));
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
		
		statusCode = 202;
		connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_2);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.addHeader(Common.HEADER_ID_CONDIZIONE+"-SBAGLIATO", Common.filtriPools.get(pool).get(0));
		current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of(Common.CONNETTORE_2));
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CommonConsegnaMultipla.checkResponses(responsesByKind);
		
		String messaggioFallbackAtteso = 	"Per la consegna viene utilizzato il connettore 'Connettore2', configurato per essere utilizzato in caso di identificazione condizionale fallita";

		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		for (var toCheck: toCheckForDiagnostici) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO, 
						CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO,
						IdentificazioneFallitaTest.MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(
						id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_UTILIZZO_CONNETTORE_DEFAULT, 
						messaggioFallbackAtteso);
			}
		}
		
	}
	
	public void regole(Map<Integer, Set<String>> statusCodeVsConnettori) throws UtilsException {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByRegole";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		List<String> forwardingHeaders = HttpUtilities.getClientAddressHeaders();


		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			final String filtro = Common.filtriPools.get(pool).get(0); 

			// Filtro HeaderHttp
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaHeaderHttp",   "SA_TestRegolaHeaderHttp",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(Common.HEADER_ID_CONDIZIONE, filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Filtro ParametroUrl
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaParametroUrl",   "SA_TestRegolaParametroUrl",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Filtro Regola Statica, vanno tutte sul pool2
			Set<String> connettoriPoolStatica = new HashSet<>(Common.connettoriPools.get(Common.POOL_2));
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaStatica",   "SA_TestRegolaStatica",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPoolStatica);
			requestsByKind.add(current);
			
			// Filtro Regola ClientI Ip, vanno tutte sul Connettore0
			Set<String> connettoriPoolClientIp = Set.of(Common.CONNETTORE_0);
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaClientIp",   "SA_TestRegolaClientIp",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPoolClientIp);
			requestsByKind.add(current);
			
			
			// Filtro Regola Contenuto
			String content = "<Filtro>"+filtro+"</Filtro>";
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaContenuto",   "SA_TestRegolaContenuto",  soapContentType, content);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Filtro Regola FreemarkerTemplate
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaFreemarkerTemplate",   "SA_TestRegolaFreemarkerTemplate",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);

			// Filtro Regola Velocity Template
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaVelocityTemplate",   "SA_TestRegolaVelocityTemplate",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Filtro Regola Template
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaTemplate",   "SA_TestRegolaTemplate",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Filtro Regola SoapAction, è il filtro Pool0-Filtro0, va sul pool0
			Set<String> connettoriPoolSoapAction = new HashSet<>(Common.connettoriPools.get(Common.POOL_0));
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaSoapAction",   "Pool0-Filtro0",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPoolSoapAction);
			requestsByKind.add(current);
			
			// Filtro Regola Url Invocazione
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaUrlInvocazione",   "SA_TestRegolaUrlInvocazione",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Filtro Regola XForwardedFor
			String header = forwardingHeaders.get(i%forwardingHeaders.size());
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaXForwardedFor",   "SA_TestRegolaXForwardedFor",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(header, filtro);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Questa fallisce l'identificazione
			request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaHeaderHttp",   "SA_TestRegolaHeaderHttp",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader("HEADER-SBAGLIATO", filtro);
			current = new RequestAndExpectations(request, Set.of(), Set.of(), CommonConsegnaMultipla.ESITO_ERRORE_PROCESSAMENTO_PDD_4XX, 500);
			requestsByKind.add(current);			

			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CommonConsegnaMultipla.checkResponses(responsesByKind);
	}
	
	public void parametroUrlNCUDiagnosticoInfo(Map<Integer, Set<String>> statusCodeVsConnettori) {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroParametroUrlNCUDiagnosticoInfo";
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();

		// Il connettore di ripiego in caso di ncu è il 3

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1
					: HttpConstants.CONTENT_TYPE_SOAP_1_2;

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla", "test",	soapContentType);
			request.setUrl(request.getUrl() + "&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="
					+ Common.filtriPools.get(pool).get(0));
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(), entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			i++;
		}
		
		// Request Nessun connettore utilizzabile
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request=FiltroInesistente");
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of(Common.CONNETTORE_3));
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
		
		statusCode = 202;
		connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_2);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request=FiltroInesistente");
		current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of(Common.CONNETTORE_3));
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);

		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla
				.makeRequestsByKind(requestsByKind, 1);

		CommonConsegnaMultipla.checkResponses(responsesByKind);
		
		String messaggioAtteso = "Il valore estratto dalla richiesta 'FiltroInesistente', ottenuto tramite identificazione 'FormBased' (Parametro URL: govway-testsuite-id_connettore_request), non consente di identificare alcun connettore da utilizzare";
		String messaggioFallbackAtteso = 	"Per la consegna viene utilizzato il connettore 'Connettore3', configurato per essere utilizzato nel caso in cui la condizione estratta dalla richiesta non ha permesso di identificare alcun connettore";

		String CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_INFO_2 = "007044";
		
		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		for (var toCheck: toCheckForDiagnostici) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO, 
						CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_INFO_2,
						messaggioAtteso);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(
						id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_UTILIZZO_CONNETTORE_DEFAULT, 
						messaggioFallbackAtteso);
			}
		}
		
	}
	
	public void ICFDiagnosticoErrorNCUDiagnosticoInfo(Map<Integer, Set<String>> statusCodeVsConnettori) {
		final String erogazione = "TestConsegnaMultiplaCondizionaleICFDiagnosticoErrorNCUDiagnosticoInfo";
		
		// Per ICF i connettori di fallback sono tutti
		// Per NCU il connettore di fallback è il 1

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnosticiICF = new HashSet<>();
		var connettoriRipiegoICF = Common.setConnettoriAbilitati;
		Set<RequestAndExpectations> toCheckForDiagnosticiNCU = new HashSet<>();
		var connettoriRipiegoNCU= Set.of(Common.CONNETTORE_1);

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;

			
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(Common.HEADER_ID_CONDIZIONE, Common.filtriPools.get(pool).get(0));
			
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			i++;
		}
		
		// Request identificazione fallita, in questo caso viene rediretta su tutti i connettori
		String pool = Common.POOL_0;
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.addHeader(Common.HEADER_ID_CONDIZIONE+"-SBAGLIATO", Common.filtriPools.get(pool).get(0));
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, connettoriRipiegoICF);
		requestsByKind.add(current);
		toCheckForDiagnosticiICF.add(current);
		
		// Request nessun connettore utilizzabile, in questo caso viene rediretta sul connettore1
		statusCode = 202;
		request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_2);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.addHeader(Common.HEADER_ID_CONDIZIONE, "CONNETTORE_INESISTENTE");
		current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, connettoriRipiegoNCU);
		requestsByKind.add(current);
		toCheckForDiagnosticiNCU.add(current);
		
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CommonConsegnaMultipla.checkResponses(responsesByKind);
		
		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici info
		for (var toCheck: toCheckForDiagnosticiICF) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(id_transazione, 
						DIAGNOSTICO_SEVERITA_ERROR, 
						CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR,
						IdentificazioneFallitaTest.MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_UTILIZZO_TUTTI_CONNETTORI, 
						MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_FALLBACK_TUTTI_CONNETTORI);
			}
		}
		
		String messaggioFallbackAtteso = 	"Per la consegna viene utilizzato il connettore 'Connettore1', configurato per essere utilizzato nel caso in cui la condizione estratta dalla richiesta non ha permesso di identificare alcun connettore";
		String messaggioDiagnostico = "Il valore estratto dalla richiesta 'CONNETTORE_INESISTENTE', ottenuto tramite identificazione 'HeaderBased' (Header HTTP: GovWay-TestSuite-Connettore), non consente di identificare alcun connettore da utilizzare";


		String CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR_2 = "007044";
		
		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		for (var toCheck: toCheckForDiagnosticiNCU) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO, 
						CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR_2,
						messaggioDiagnostico);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_UTILIZZO_CONNETTORE_DEFAULT, 
						messaggioFallbackAtteso);
			}
		}
		
	}
	
	public void templateICFDiagnosticoError(Map<Integer, Set<String>> statusCodeVsConnettori) {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroTemplateICFDiagnosticoError";
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();


		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+Common.filtriPools.get(pool).get(0));
			
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			i++;
		}
		
		// Request identificazione fallita, in questo caso viene rediretta sul connettore0
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&govway-testsuite-id_SBAGLIATO_connettore_request=FiltroInesistente");
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request,statusCode, connettoriSuccesso, Set.of(Common.CONNETTORE_0));
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
		
		statusCode = 202;
		connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_2);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&govway-testsuite-id_SBAGLIATO_connettore_request=FiltroInesistente");
		current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request,statusCode, connettoriSuccesso, Set.of(Common.CONNETTORE_0));
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CommonConsegnaMultipla.checkResponses(responsesByKind);
		
		String messaggioAtteso = "Identificazione 'Template' (${query:govway-testsuite-id_connettore_request}) non è riuscita ad estrarre dalla richiesta l'informazione utile ad i"
				+ "dentificare il connettore da utilizzare: Proprieta' 'ConditionalConfig.gwt' contiene un valore non corretto: Placeholder [{query:govway-testsuite-id_connettore_request}"
				+ "] resolution failed: object [java.util.HashMap] 'govway-testsuite-id_connettore_request' not exists in map";
		
		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		for (var toCheck: toCheckForDiagnostici) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(id_transazione, 
						DIAGNOSTICO_SEVERITA_ERROR, 
						CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR,
						messaggioAtteso);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(
						id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_UTILIZZO_CONNETTORE_DEFAULT, 
						MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_FALLBACK);
			}
		}
	}
	
	
	public void velocityTemplateNCUQualsiasiNoDiagnostico(Map<Integer, Set<String>> statusCodeVsConnettori) {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroVelocityTemplateNCUQualsiasiNoDiagnostico";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();
		var connettoriRipiego = Common.setConnettoriAbilitati;

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+Common.filtriPools.get(pool).get(0));
			
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			i++;
		}
		
		// Request nessun connettore utilizzabile	
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request=FiltroInesistente");
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, connettoriRipiego);
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
		
		statusCode = 202;
		connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_2);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request=FiltroInesistente");
		current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, connettoriRipiego);
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CommonConsegnaMultipla.checkResponses(responsesByKind);
		
		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		for (var toCheck: toCheckForDiagnostici) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				IdentificazioneFallitaTest.checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR);
				IdentificazioneFallitaTest.checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(
						id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO_UTILIZZO_TUTTI_CONNETTORI, 
						IdentificazioneFallitaTest.MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO_FALLBACK_TUTTI_CONNETTORI);
			}
		}
	}
	
	
	public void freemarkerTemplateICFQualsiasiNoDiagnostico(Map<Integer, Set<String>> statusCodeVsConnettori) {
		/**
		 * Le richieste con identificazione fallita vengono redirette su tutti i connettori
		 */
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroFreemarkerTemplateICFQualsiasiNoDiagnostico";
		var connettoriRipiego = Common.setConnettoriAbilitati;

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();
		
		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+Common.filtriPools.get(pool).get(0));
			
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			i++;
		}
		
		// Request identificazione fallita			
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&govway-testsuite-id_SBAGLIATO_connettore_request=FiltroInesistente");
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, connettoriRipiego);
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
		
		statusCode = 202;
		connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  HttpConstants.CONTENT_TYPE_SOAP_1_2);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&govway-testsuite-id_SBAGLIATO_connettore_request=FiltroInesistente");
		current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, connettoriRipiego);
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
	
		CommonConsegnaMultipla.checkResponses(responsesByKind);
		
		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		for (var toCheck: toCheckForDiagnostici) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				IdentificazioneFallitaTest.checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR);
				IdentificazioneFallitaTest.checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(
						id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_UTILIZZO_TUTTI_CONNETTORI, 
						MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_FALLBACK_TUTTI_CONNETTORI);
			}
		}
		
	}
	
	public void urlInvocazionePrefisso(Map<Integer, Set<String>> statusCodeVsConnettori) {
		/**
		 * Questo test testa anche la funzionalità di prefisso, per cui i ai filtri inviati viene rimosso
		 * il prefisso.  
		 */
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroUrlInvocazionePrefisso";
		final String prefix = "Pool";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			String filtro = Common.filtriPools.get(pool).get(0);
			filtro = filtro.substring(prefix.length());
			
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			i++;
		}
		
		// Questa fallisce l'identificazione
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaHeaderHttp",   "SA_TestRegolaHeaderHttp",  HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.setUrl(request.getUrl()+"&returnCode=" + 200);			
		var current = new RequestAndExpectations(request, Set.of(), Set.of(), CommonConsegnaMultipla.ESITO_ERRORE_PROCESSAMENTO_PDD_4XX, 500);
		requestsByKind.add(current);	
		
		request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaHeaderHttp",   "SA_TestRegolaHeaderHttp",  HttpConstants.CONTENT_TYPE_SOAP_1_2);
		request.setUrl(request.getUrl()+"&returnCode=" + 200);			
		current = new RequestAndExpectations(request, Set.of(), Set.of(), CommonConsegnaMultipla.ESITO_ERRORE_PROCESSAMENTO_PDD_4XX, 500);
		requestsByKind.add(current);	
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CommonConsegnaMultipla.checkResponses(responsesByKind);
	
	}
	
	
	public void XForwardedForPrefissoESuffisso(Map<Integer, Set<String>> statusCodeVsConnettori) throws UtilsException {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroXForwardedForPrefissoESuffisso";
		
		/**
		 * L'erogazione aggiunge "Pool" "-Filtro0" come prefisso e suffisso, per cui mi basta inviare l'indice del 
		 * connettore.
		 */
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		List<String> forwardingHeaders = HttpUtilities.getClientAddressHeaders();
		
		String prefix = "Pool0";

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			String header = forwardingHeaders.get(i%forwardingHeaders.size());
			String filtro = Common.filtriPools.get(pool).get(0); 			
			
			filtro = filtro.substring(0,prefix.length());
			filtro = filtro.substring("Pool".length());

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(header, filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
						
			i++;
		}
		
		// Questa fallisce l'identificazione
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaHeaderHttp",   "SA_TestRegolaHeaderHttp",  HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.setUrl(request.getUrl()+"&returnCode=" + 200);
		var current = new RequestAndExpectations(request, Set.of(), Set.of(), CommonConsegnaMultipla.ESITO_ERRORE_PROCESSAMENTO_PDD_4XX, 500);
		requestsByKind.add(current);
		
		request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaHeaderHttp",   "SA_TestRegolaHeaderHttp",  HttpConstants.CONTENT_TYPE_SOAP_1_2);
		request.setUrl(request.getUrl()+"&returnCode=" + 200);
		current = new RequestAndExpectations(request, Set.of(), Set.of(), CommonConsegnaMultipla.ESITO_ERRORE_PROCESSAMENTO_PDD_4XX, 500);
		requestsByKind.add(current);
		
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CommonConsegnaMultipla.checkResponses(responsesByKind);
	}
	
	
	public void contenutoSuffisso(Map<Integer, Set<String>> statusCodeVsConnettori) {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroContenutoSuffisso";

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		String prefisso = "PoolX";

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			final String pool = Common.pools.get(i % Common.pools.size());
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			final Set<String> connettoriSuccesso = entry.getValue();
			final Integer statusCode = entry.getKey();
			final Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			String filtro = Common.filtriPools.get(pool).get(0);
			filtro = filtro.substring(0,prefisso.length());
			
			String content = "<Filtro>"+filtro+"</Filtro>";
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType, content);
			
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			requestsByKind.add(current);
			
			i++;
		}
		
		// Questa fallisce l'identificazione
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaHeaderHttp",   "SA_TestRegolaHeaderHttp",  HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.setUrl(request.getUrl()+"&returnCode=" + 200);
		var current = new RequestAndExpectations(request, Set.of(), Set.of(), CommonConsegnaMultipla.ESITO_ERRORE_PROCESSAMENTO_PDD_4XX, 500);
		requestsByKind.add(current);
		
		request = RequestBuilder.buildSoapRequest(erogazione, "TestRegolaHeaderHttp",   "SA_TestRegolaHeaderHttp",  HttpConstants.CONTENT_TYPE_SOAP_1_2);
		request.setUrl(request.getUrl()+"&returnCode=" + 200);
		current = new RequestAndExpectations(request, Set.of(), Set.of(), CommonConsegnaMultipla.ESITO_ERRORE_PROCESSAMENTO_PDD_4XX, 500);
		requestsByKind.add(current);
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		CommonConsegnaMultipla.checkResponses(responsesByKind);
	}

	
	public void soapActionNCUDiagnosticoError(Map<Integer, Set<String>> statusCodeVsConnettori) {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroSoapActionNCUDiagnosticoError";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();

		
		var azioniBySoapAction = Map.of(
				"Pool0-Filtro0", "TestCondizionale0",
				"Pool1-Filtro0", "TestCondizionale1",
				"Pool2-Filtro0", "TestCondizionale2");

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			String filtro = Common.filtriPools.get(pool).get(0);

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, azioniBySoapAction.get(filtro),   filtro,  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			i++;
		}
		
		// Richiesta Nessun Connettore Utilizzabile, portata sul connettore 0
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla", "test", HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of(Common.CONNETTORE_0));
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
		
		statusCode = 202;
		connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla", "test", HttpConstants.CONTENT_TYPE_SOAP_1_2);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of(Common.CONNETTORE_0));
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CommonConsegnaMultipla.checkResponses(responsesByKind);

		String messaggioAtteso = "Il valore estratto dalla richiesta 'test', ottenuto tramite identificazione 'SOAPActionBased', non consente di identificare alcun connettore da utilizzare";
		String CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR_2 = "007043";
		
		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		for (var toCheck: toCheckForDiagnostici) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(id_transazione, 
						DIAGNOSTICO_SEVERITA_ERROR, 
						CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR_2,	
						messaggioAtteso);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(
						id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_UTILIZZO_CONNETTORE_DEFAULT, 
						IdentificazioneFallitaTest.MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO_FALLBACK);
			}
		}
	}
	
	
	public void clientIp(Map<Integer, Set<String>> statusCodeVsConnettori) {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroClientIp";

		int i = 0;
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		// Finiscono tutte sul primo connettore
		for (var entry : statusCodeVsConnettori.entrySet()) {
			Set<String> connettoriPool = Set.of(Common.CONNETTORE_0);
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CommonConsegnaMultipla.checkResponses(responsesByKind);
	}
	


}
