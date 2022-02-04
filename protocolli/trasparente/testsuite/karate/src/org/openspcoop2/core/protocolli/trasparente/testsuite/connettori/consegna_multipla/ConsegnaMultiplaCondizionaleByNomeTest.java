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

import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.buildRequestAndExpectationFiltered;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkResponses;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.statusCodeVsConnettori;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * 
 * @author froggo
 *
 */
public class ConsegnaMultiplaCondizionaleByNomeTest extends ConfigLoader {

	
	@Test
	public void consegnaMultiplaFallita() {
		// TODO: 1 -Manca il campo identificativo.
		//					2- Nome connettore errato
		//					3 - Utilizzo connettore disabiltato
		//					4 - Utilizzo connettore con scheduling disabilitato.
	}
	
	@Test
	public void headerHttp() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByNomeHeaderHttp";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		int i = 0;
		
		for (var entry : statusCodeVsConnettori.entrySet()) {
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(Common.HEADER_ID_CONDIZIONE, filtro);
			
			var current = buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), Set.of(filtro));
			requestsByKind.add(current);
			
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		checkResponses(responsesByKind);

	}
	
	@Test
	public void template() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByNomeTemplate";
		parametroUrl_Impl(erogazione);		
	}
	
	@Test
	public void velocityTemplate() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByNomeVelocityTemplate";
		parametroUrl_Impl(erogazione);		
	}
	
	@Test
	public void freemarkerTemplate() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByNomeFreemarkerTemplate";
		parametroUrl_Impl(erogazione);		
	}
	
	@Test
	public void soapAction() {
		// TODO: Fail le soap action che si chiamano come i connettori
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
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			String filtro = Common.filtriPools.get(pool).get(0);

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, azioniBySoapAction.get(filtro),   filtro,  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Richiesta Nessun Connettore Utilizzabile, portata sul connettore 0
			request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla", "test",    soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), Set.of(Common.CONNETTORE_0));
			requestsByKind.add(current);
			toCheckForDiagnostici.add(current);
			
			i++;
		}
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CommonConsegnaMultipla.checkResponses(responsesByKind);
		
	}
	
	@Test
	public void clientIp() {
		// TODO: L'erogazione è già fatta
		final String erogazione = "TestConsegnaMultiplaCondizionaleByNomeClientIp";
		final String soapContentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		// Finiscono tutte sul primo connettore
		for (var entry : statusCodeVsConnettori.entrySet()) {
			Set<String> connettoriPool = Set.of(Common.CONNETTORE_0);
			
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CommonConsegnaMultipla.checkResponses(responsesByKind);
	}
	
	@Test
	public void regole() throws UtilsException {
		// TODO: Crea il connettore per la regola clientIp
		// TODO: Fai prima anche il test SoapAction
		final String erogazione = "TestConsegnaMultiplaCondizionaleByNomeRegole";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		List<String> forwardingHeaders = HttpUtilities.getClientAddressHeaders();


		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			var connettoriPool = Set.of(filtro);
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
 

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

			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		CommonConsegnaMultipla.checkResponses(responsesByKind);
		
	}
	
	@Test
	public void contenutoSuffisso() {
		// TODO: Modifica i nomi e gli id dei connettori aggiungendo il suffisso
		final String erogazione = "TestConsegnaMultiplaCondizionaleByNomeContenutoSuffisso";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		int i = 0;
		
		for (var entry : statusCodeVsConnettori.entrySet()) {
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(Common.HEADER_ID_CONDIZIONE, filtro);
			
			var current = buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), Set.of(filtro));
			requestsByKind.add(current);
			
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		checkResponses(responsesByKind);
	}
	
	@Test
	public void parametroUrl() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByNomeParametroUrl";
		parametroUrl_Impl(erogazione);
	}

	
	@Test
	public void urlInvocazione() {
		// TODO: Fai UrlInvocazionePrefisso
		final String erogazione = "TestConsegnaMultiplaCondizionaleByNomeUrlInvocazione";
		parametroUrl_Impl(erogazione);
	}
	
	
	@Test
	public void XForwardedForPrefissoESuffisso() throws UtilsException {
		// TODO: Modifica gli id dei connettori aggiungendo prefissi e suffissi
		
		final String erogazione = "TestConsegnaMultiplaCondizionaleByNomeXForwardedForPrefissoESuffisso";

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		List<String> forwardingHeaders = HttpUtilities.getClientAddressHeaders();
		
		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			String header = forwardingHeaders.get(i%forwardingHeaders.size());
			String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(header, filtro);
			
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), Set.of(filtro));
			requestsByKind.add(current);
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		CommonConsegnaMultipla.checkResponses(responsesByKind);
	}
	
	
	private static void parametroUrl_Impl(String erogazione) {
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		int i = 0;
		
		for (var entry : statusCodeVsConnettori.entrySet()) {
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			
			var current = buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), Set.of(filtro));
			requestsByKind.add(current);
			
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		checkResponses(responsesByKind);
	}
		
}
