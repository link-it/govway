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

import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.buildRequestAndExpectationFiltered;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkResponses;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.statusCodeVsConnettori;

import java.io.File;
import java.util.ArrayList;
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
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * 
 * @author Francesco Scarlato
 *
 */
public class ConsegnaConNotificheCondizionaleByNomeSoapTest extends ConfigLoader {

	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
		File cartellaRisposte = CommonConsegnaMultipla.connettoriFilePath.toFile();
		if (!cartellaRisposte.isDirectory() || !cartellaRisposte.canWrite()) {
			throw new RuntimeException(
					"E' necessario creare la cartella per scrivere le richieste dei connettori, indicata dalla poprietà: <connettori.consegna_multipla.connettore_file.path> ");
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
	

	@Test
	public void headerHttp() {
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
			// In Soap un 500 senza soapFault è considerato Ok con anomialia, quindi mi aspetto un errore nella transazione principale
			// solo in caso di 4xx e 5xx, con 5xx > 500
			if (statusCode > 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			requestsByKind.add(current);
			
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		ConsegnaConNotificheCondizionaleByFiltroSoapTest.checkResponses(responsesByKind);
	}
	
	@Test
	public void parametroUrl() {
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeParametroUrl";
		parametroUrl_Impl(erogazione);
	}
	
	
	private static void parametroUrl_Impl(String erogazione) {
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
			// In Soap un 500 senza soapFault è considerato Ok con anomialia, quindi mi aspetto un errore nella transazione principale
			// solo in caso di 4xx e 5xx, con 5xx > 500
			if (statusCode > 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			requestsByKind.add(current);
			
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		ConsegnaConNotificheCondizionaleByFiltroSoapTest.checkResponses(responsesByKind);
	}

}
