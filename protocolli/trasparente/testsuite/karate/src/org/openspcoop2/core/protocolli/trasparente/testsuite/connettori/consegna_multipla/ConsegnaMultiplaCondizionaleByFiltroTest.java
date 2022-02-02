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

import static org.junit.Assert.assertTrue;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.checkAll200;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkRequestExpectations;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkRequestExpectationsFinal;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkSchedulingConnettoreIniziato;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkStatoConsegna;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.setIntersection;

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
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * 
 * @author froggo
 *
 * TODO: Identificazione condizione fallita, Prefisso e Suffisso
 */
public class ConsegnaMultiplaCondizionaleByFiltroTest extends ConfigLoader {
	
	// Prima di ogni test  fermo le attuali riconsegne in atto.	
	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
		// TODO: Pulisci la cartella delle richieste dei coinnettori file, ma fallo dentro Common.fermaRiconsegne
	}

	
	public RequestAndExpectations buildRequestAndExpectationFiltered(HttpRequest request, int statusCode, Set<String> connettoriSuccesso, Set<String> connettoriFiltrati, String soapContentType) {
		connettoriFiltrati = new HashSet<>(connettoriFiltrati);
		connettoriFiltrati.remove(Common.CONNETTORE_DISABILITATO);
		connettoriSuccesso = setIntersection(connettoriSuccesso, connettoriFiltrati);
		connettoriFiltrati.removeAll(connettoriSuccesso);
		Set<String> connettoriFallimento = connettoriFiltrati;
	
		int esito;
		
		if ( ( statusCode < 200 || statusCode > 299) && !connettoriSuccesso.isEmpty()) {
			esito = CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_FALLITA;
		} else {
			esito =  connettoriFallimento.isEmpty() ? CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_COMPLETATA : CommonConsegnaMultipla.ESITO_CONSEGNA_MULTIPLA_IN_CORSO;
		}
		
		return new RequestAndExpectations(request, connettoriSuccesso, connettoriFallimento, esito);
	}
	
	
	@Test
	public void headerHttp() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroHeaderHttp";

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
			
			var current = buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool, soapContentType);
			requestsByKind.add(current);
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		checkResponses(responsesByKind);
	}
	
	
	@Test
	public void parametroUrl() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroParametroUrl";
		parametroUrl_Impl(erogazione);
	}
	
	@Test
	public void template() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroTemplate";
		parametroUrl_Impl(erogazione);
	}
	
	
	@Test
	public void velocityTemplate() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroVelocityTemplate";
		parametroUrl_Impl(erogazione);
	}
	
	
	@Test
	public void freemarkerTemplate() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroFreemarkerTemplate";
		parametroUrl_Impl(erogazione);
	}
	
	@Test
	public void urlInvocazione() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroUrlInvocazione";
		parametroUrl_Impl(erogazione);
	}
	
	
	@Test
	public void XForwardedFor() throws UtilsException {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroXForwardedFor";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		List<String> forwardingHeaders = HttpUtilities.getClientAddressHeaders();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : CommonConsegnaMultipla.statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;

			String header = forwardingHeaders.get(i%forwardingHeaders.size());

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(header, Common.filtriPools.get(pool).get(0));
			
			var current = buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool, soapContentType);

			requestsByKind.add(current);
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		checkResponses(responsesByKind);
	}
	
	
	@Test
	public void contenuto() {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroContenuto";

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : CommonConsegnaMultipla.statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			
			String content = "<Filtro>"+Common.filtriPools.get(pool).get(0)+"</Filtro>";
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType, content);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			
			var current = buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool, soapContentType);

			requestsByKind.add(current);
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		checkResponses(responsesByKind);
	}

	
	@Test
	public void soapAction( ) {
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroSoapAction";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		
		var azioniBySoapAction = Map.of(
				"Pool0-Filtro0", "TestCondizionale0",
				"Pool1-Filtro0", "TestCondizionale1",
				"Pool2-Filtro0", "TestCondizionale2");

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : CommonConsegnaMultipla.statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			String filtro = Common.filtriPools.get(pool).get(0);

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, azioniBySoapAction.get(filtro),   filtro,  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			
			var current = buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool, soapContentType);

			requestsByKind.add(current);
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		checkResponses(responsesByKind);
	}
	
	
	//@Test
	public void clientIp() {
		// TODO: Si ritorna al caso con un solo connettore e niente consegna multipla.
		final String erogazione = "TestConsegnaMultiplaCondizionaleByFiltroClientIp";
		final String soapContentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;

/*		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		int i=0;
		// Finiscono tutte sul primo connettore
		for (var entry : CommonConsegnaMultipla.statusCodeVsConnettori.entrySet()) {
			Set<String> connettoriPool = Set.of(Common.CONNETTORE_0);
			
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			//request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+Common.filtriPools.get(pool).get(0));
			
			var current = buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool, soapContentType);
			requestsByKind.add(current);
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		checkResponses(responsesByKind);*/
		
	}
	
	private void parametroUrl_Impl(String erogazione) {
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : CommonConsegnaMultipla.statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			
			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+Common.filtriPools.get(pool).get(0));
			
			var current = buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool, soapContentType);
			requestsByKind.add(current);
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		checkResponses(responsesByKind);
	}
	
	
	private void checkResponses(Map<RequestAndExpectations, List<HttpResponse>> responsesByKind) {
		// Tutte le richieste indipendentemente dal tipo devono essere state prese in consegna e lo scheduling inizia 
		// su tutti i connettori abilitati e filtrati dalla consegna condizionale
		assertTrue(!responsesByKind.isEmpty());
		
		for (var requestAndExpectation : responsesByKind.keySet() ) {
			var responses = responsesByKind.get(requestAndExpectation);
			assertTrue(!responses.isEmpty());
			checkAll200(responses);
			
			Set<String> connettoriGestiti = new HashSet<>(requestAndExpectation.connettoriSuccesso);
			connettoriGestiti.addAll(requestAndExpectation.connettoriFallimento);

			checkStatoConsegna(responses, ESITO_CONSEGNA_MULTIPLA, connettoriGestiti.size());

			// Deve essere la fusione dei connettoriOk e i connettoriErrore\conettoriScheduling Disabilitato
			checkSchedulingConnettoreIniziato(responses, connettoriGestiti);
		}
		
		// Attendo la consegna
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControllo);
		
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				checkRequestExpectations(requestAndExpectation, response);
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControlloFallite);
		for (var requestAndExpectation : responsesByKind.keySet()) {
			for (var response : responsesByKind.get(requestAndExpectation)) {
				checkRequestExpectationsFinal(requestAndExpectation, response);
			}
		}
	}
	


}
