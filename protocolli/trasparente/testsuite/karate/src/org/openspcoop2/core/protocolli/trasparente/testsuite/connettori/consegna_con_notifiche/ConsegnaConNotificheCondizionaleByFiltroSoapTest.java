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

import static org.junit.Assert.assertTrue;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkRequestExpectations;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkRequestExpectationsFinal;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.setSum;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.statusCodeVsConnettori;

import java.io.File;
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
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;

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
public class ConsegnaConNotificheCondizionaleByFiltroSoapTest extends ConfigLoader {
	
	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
		File cartellaRisposte = CommonConsegnaMultipla.connettoriFilePath.toFile();
		if (!cartellaRisposte.isDirectory()|| !cartellaRisposte.canWrite()) {
			throw new RuntimeException("E' necessario creare la cartella per scrivere le richieste dei connettori, indicata dalla poprietà: <connettori.consegna_multipla.connettore_file.path> ");
		}
	}
	
	@Test
	public void headerHttpICFDiagnosticoInfo() {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		
		final String erogazione = "TestConsegnaConNotificheCondizionaleByFiltroHeaderHttpICFDiagnosticoInfo";
		
		// Il connettore di fallback è il 2
		// TODO: Nella consegnaMultiplaCondizionale non ho mai costruito richieste con soapFault, fai pure quelle

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			Set<String> connettoriSuccesso = entry.getValue();
			final int statusCode = entry.getKey();
			final String soapContentType = i % 2 == 0 ?HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			final String filtro =Common.filtriPools.get(pool).get(0); 

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.addHeader(Common.HEADER_ID_CONDIZIONE, filtro);
			
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}
			
			// In Soap un 500 senza soapFault è considerato Ok con anomialia, quindi mi aspetto un errore nella transazione principale
			// solo in caso di 4xx e 5xx, con 5xx > 500
			if (statusCode > 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			
			// Ogni 5 richieste ne faccio sbagliare una sull'autenticazione
			if ( i % 5 == 0) {
				current.request.getHeadersValues().remove("Authorization");
				current.principaleSuperata = false;
				current.statusCodePrincipale = 500;
			}
			
			requestsByKind.add(current);
			
			// Request identificazione fallita, in questo caso viene rediretta sul connettore0
			/*request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(Common.HEADER_ID_CONDIZIONE+"-SBAGLIATO", Common.filtriPools.get(pool).get(0));
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), Set.of(Common.CONNETTORE_2));
			requestsByKind.add(current);*/
			
			toCheckForDiagnostici.add(current);
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		checkResponses(responsesByKind);

//		CommonConsegnaMultipla.checkResponses(responsesByKind);
		
		String messaggioFallbackAtteso = 	"Per la consegna viene utilizzato il connettore 'Connettore2', configurato per essere utilizzato in caso di identificazione condizionale fallita";

		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		/*for (var toCheck: toCheckForDiagnostici) {
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
		}*/
		
	}
	
	
	@Test
	public void parametroUrlNCUDiagnosticoInfo() {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		// 		Errore di Consegna (4xx e 502 bad gateway)
		//
		// Il connettore di ripiego in caso di ncu è il 3

		final String erogazione = "TestConsegnaConNotificheCondizionaleByFiltroParametroUrlNCUDiagnosticoInfo";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			Set<String> connettoriSuccesso = entry.getValue();
			final int statusCode = entry.getKey();
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			final String filtro =Common.filtriPools.get(pool).get(0); 

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			
			// TODO: Chiedi ad andrea 
			// A quanto pare in errore di consegna rientrano tutti  i 4xx e tutti i 5xx da 501 in poi, 
			// Quindi in questo test tutte le 2xx, 4xx e 5xx vengono notificate.
					
			// Ogni 5 richieste ne faccio sbagliare una sull'autenticazione
			if ( i % 5 == 0) {
				current.request.getHeadersValues().remove("Authorization");
				current.principaleSuperata = false;
				current.statusCodePrincipale = 500;
			}
			
			requestsByKind.add(current);
			
			// TODO: NCU			
			
			toCheckForDiagnostici.add(current);
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		checkResponses(responsesByKind);
	}
	
	
	@Test
	public void templateICFDiagnosticoError() {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//
		// Il connettore di ripiego in caso di ICF è il 0

		final String erogazione = "TestConsegnaConNotificheCondizionaleByFiltroTemplateICFDiagnosticoError";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			Set<String> connettoriSuccesso = entry.getValue();
			final int statusCode = entry.getKey();
			final String soapContentType = i % 2 == 0 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			final String filtro =Common.filtriPools.get(pool).get(0); 

			HttpRequest request = RequestBuilder.buildSoapRequest(erogazione, "TestConsegnaMultipla",   "test",  soapContentType);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			
			// In Soap un 500 senza soapFault è considerato Ok con anomialia, quindi mi aspetto un errore nella transazione principale
			// solo in caso di 4xx e 5xx, con 5xx > 500
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}
			if (statusCode > 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}					
			// Ogni 5 richieste ne faccio sbagliare una sull'autenticazione
			if ( i % 5 == 0) {
				current.request.getHeadersValues().remove("Authorization");
				current.principaleSuperata = false;
				current.statusCodePrincipale = 500;
			}
			
			requestsByKind.add(current);
			
			// TODO: ICF
			
			toCheckForDiagnostici.add(current);
			i++;
		}
		
		HttpRequest requestSoapFault = RequestBuilder.buildSoapRequestFault(erogazione, "TestConsegnaMultipla", "test", HttpConstants.CONTENT_TYPE_SOAP_1_1 );
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		checkResponses(responsesByKind);
	}

	
	
	
	
	@Test
	public void ICFDiagnosticoInfoNCUDiagnosticoError() {
		final String erogazione = "TestConsegnaConNotificheCondizionaleICFDiagnosticoErrorNCUDiagnosticoInfo";
		// TODOs
	}
	
	
	@Test
	public void regole() throws UtilsException {
		final String erogazione = "TestConsegnaConNotificheCondizionaleByRegole";
		// TODOs
	}
	
	
	private static void checkResponses(Map<RequestAndExpectations, List<HttpResponse>> responsesByKind) {
		assertTrue(!responsesByKind.isEmpty());
		
		// Le richieste per cui la transazione sincrona ha avuto successo devono essere schedulate
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);

			assertTrue(!responses.isEmpty());
			
			// Deve essere la fusione dei connettoriOk e i connettoriErrore\conettoriScheduling Disabilitato
			Set<String> connettoriCoinvolti = setSum(requestExpectation.connettoriSuccesso, requestExpectation.connettoriFallimento);
						
			if (requestExpectation.principaleSuperata) {
				CommonConsegnaMultipla.checkPresaInConsegna(responses, connettoriCoinvolti.size());		// TODO: Ci passo connettoriCoinvolti invece della size e la size la prendo dentro il metodo
				CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(responses, connettoriCoinvolti);
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
					checkRequestExpectations(requestExpectation, response);
				}
			}
		}
		
		// Attendo l'intervallo di riconsegna e controllo che il contatore delle consegne sia almeno a 2
		org.openspcoop2.utils.Utilities.sleep(2*CommonConsegnaMultipla.intervalloControlloFallite);
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);

			if (requestExpectation.principaleSuperata) {
				for (var response : responses) {
					checkRequestExpectationsFinal(requestExpectation, response);
				}
			}
		}
	}

	
}
