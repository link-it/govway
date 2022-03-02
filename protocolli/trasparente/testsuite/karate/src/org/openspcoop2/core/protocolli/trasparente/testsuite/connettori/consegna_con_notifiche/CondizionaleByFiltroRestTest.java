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

// OK con anomalia: Transazione andata bene ma è stato emesso un diagnostico error.

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_con_notifiche;

import static org.junit.Assert.assertTrue;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_0;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_1;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_2;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.CONNETTORE_3;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_CONSEGNA_NOTIFICHE_IDENTIFICAZIONE_FALLITA_UTILIZZO_CONNETTORE_DEFAULT;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_UTILIZZO_TUTTI_CONNETTORI;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.DIAGNOSTICO_SEVERITA_ERROR;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.DIAGNOSTICO_SEVERITA_INFO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_FALLBACK_TUTTI_CONNETTORI;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkNessunaNotifica;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkNessunoScheduling;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkPresaInConsegnaNotifica;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkRequestExpectations;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkRequestExpectationsFinal;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.checkSchedulingConnettoreIniziato;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.setSum;

import java.io.File;
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
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.RequestBuilder;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectations;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.RequestAndExpectations.TipoFault;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;


/**
 * 
 * @author Francesco Scarlato
 * 
 * QUERY:
 * 
 * Query contenuto: $.id_connettore_request
 * Template: ${query:govway-testsuite-id_connettore_request}
 * Freemarker: ${query["govway-testsuite-id_connettore_request"]}
 * Velocity:		#if($query.containsKey("govway-testsuite-id_connettore_request"))
							$query["govway-testsuite-id_connettore_request"]
						#end
 * UrlInvocazione: .+govway-testsuite-id_connettore_request=([^&]*).*
 * 
http://localhost:8080/TestService/echo?id_connettore=ConnettorePrincipale
 */
public class CondizionaleByFiltroRestTest  extends ConfigLoader {
	
	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
		File cartellaRisposte = CommonConsegnaMultipla.connettoriFilePath.toFile();
		cartellaRisposte.mkdir();
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
	
	
	@Test
	public void headerHttpNCUDiagnosticoError_2xx_4xx() {
		headerHttpNCUDiagnosticoError(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void headerHttpNCUDiagnosticoError_3xx_5xx() {
		headerHttpNCUDiagnosticoError(CommonConsegnaMultipla.statusCode3xx5xxVsConnettori);
	}
	
	@Test
	public void headerHttpICFDiagnosticoInfo_2xx_4xx() {
		headerHttpICFDiagnosticoInfo(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void headerHttpICFDiagnosticoInfo_3xx_5xx() {
		headerHttpICFDiagnosticoInfo(CommonConsegnaMultipla.statusCode3xx5xxVsConnettori);
	}
	
	@Test
	public void XForwardedForPrefissoESuffisso_2xx_4xx() throws UtilsException {
		XForwardedForPrefissoESuffisso(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void XForwardedForPrefissoESuffisso_3xx_4xx() throws UtilsException {
		XForwardedForPrefissoESuffisso(CommonConsegnaMultipla.statusCode3xx5xxVsConnettori);
	}
	
	
	@Test
	public void clientIp_2xx_4xx() {
		clientIp(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void clientIp_3xx_5xx() {
		clientIp(CommonConsegnaMultipla.statusCode3xx5xxVsConnettori);
	}
	

	@Test
	public void contenutoSuffisso2xx_4xx() {
		contenutoSuffisso(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void contenutoSuffisso3xx_5xx() {
		contenutoSuffisso(CommonConsegnaMultipla.statusCode3xx5xxVsConnettori);
	}

	
	@Test
	public void urlInvocazionePrefisso2xx_4xx() {
		urlInvocazionePrefisso(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void urlInvocazionePrefisso3xx_5xx() {
		urlInvocazionePrefisso(CommonConsegnaMultipla.statusCode3xx5xxVsConnettori);
	}

	
	@Test
	public void ICFDiagnosticoErrorNCUDiagnosticoInfo2xx_4xx() {
		ICFDiagnosticoErrorNCUDiagnosticoInfo(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void ICFDiagnosticoErrorNCUDiagnosticoInfo3xx_5xx() {
		ICFDiagnosticoErrorNCUDiagnosticoInfo(CommonConsegnaMultipla.statusCode3xx5xxVsConnettori);
	}	

	
	@Test
	public void freemarkerTemplateICFQualsiasiNoDiagnostico2xx_4xx() {
		freemarkerTemplateICFQualsiasiNoDiagnostico(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void freemarkerTemplateICFQualsiasiNoDiagnostico3xx_5xx() {
		freemarkerTemplateICFQualsiasiNoDiagnostico(CommonConsegnaMultipla.statusCode3xx5xxVsConnettori);
	}
	
	
	@Test
	public void velocityTemplateNCUQualsiasiNoDiagnostico2xx_4xx() {
		velocityTemplateNCUQualsiasiNoDiagnostico(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void velocityTemplateNCUQualsiasiNoDiagnostico3xx_5xx() {
		velocityTemplateNCUQualsiasiNoDiagnostico(CommonConsegnaMultipla.statusCode3xx5xxVsConnettori);
	}
	
	
	@Test
	public void templateICFDiagnosticoError2xx_4xx() {
		templateICFDiagnosticoError(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void templateICFDiagnosticoError3xx_5xx() {
		templateICFDiagnosticoError(CommonConsegnaMultipla.statusCode3xx5xxVsConnettori);
	}
	
	
	@Test
	public void parametroUrlNCUDiagnosticoInfo2xx_4xx() {
		parametroUrlNCUDiagnosticoInfo(CommonConsegnaMultipla.statusCode2xx4xxVsConnettori);
	}
	@Test
	public void parametroUrlNCUDiagnosticoInfo3xx_5xx() {
		parametroUrlNCUDiagnosticoInfo(CommonConsegnaMultipla.statusCode3xx5xxVsConnettori);
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
	
	public void headerHttpNCUDiagnosticoError(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		// Il connettore di fallback è il 2
		final String erogazione = "TestConsegnaConNotificheCondizionaleByFiltroHeaderHttpNCUDiagnosticoErrorRest";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			Set<String> connettoriSuccesso = entry.getValue();
			final int statusCode = entry.getKey();
			final String filtro =Common.filtriPools.get(pool).get(0); 

			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.addHeader(Common.HEADER_ID_CONDIZIONE, filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}			
			if (statusCode >= 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			
			requestsByKind.add(current);
			
			i++;
		}
		
		// Request nessun connettore utilizzabile, in questo caso viene rediretta sul connettore2
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.addHeader(Common.HEADER_ID_CONDIZIONE, "CONNETTORE_INESISTENTE");
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of(CONNETTORE_2));
		current.esitoSincrono = CommonConsegnaMultipla.ESITO_OK_PRESENZA_ANOMALIE;
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		checkResponses(responsesByKind);
		
		String messaggioFallbackAtteso = 	"Per la notifica viene utilizzato il connettore 'Connettore2', configurato per essere utilizzato nel caso in cui la condizione estratta dalla richiesta non ha permesso di identificare alcun connettore";
		String messaggioDiagnostico = "Il valore estratto dalla richiesta 'CONNETTORE_INESISTENTE', ottenuto tramite identificazione 'HeaderBased' (Header HTTP: GovWay-TestSuite-Connettore), non consente di identificare alcun connettore da utilizzare";

		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		for (var toCheck: toCheckForDiagnostici) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(id_transazione, 
						DIAGNOSTICO_SEVERITA_ERROR, 
						IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR_2,
						messaggioDiagnostico);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_NOTIFICHE_NESSUN_CONNETTORE_UTILIZZABILE_UTILIZZO_CONNETTORE_DEFAULT, 
						messaggioFallbackAtteso);
			}
		}
		
	}
	
	
	public void headerHttpICFDiagnosticoInfo(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		// Il connettore di fallback è il 2
		
		final String erogazione = "TestConsegnaConNotificheCondizionaleByFiltroHeaderHttpICFDiagnosticoInfoRest";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			Set<String> connettoriSuccesso = entry.getValue();
			final int statusCode = entry.getKey();
			final String filtro =Common.filtriPools.get(pool).get(0); 

			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.addHeader(Common.HEADER_ID_CONDIZIONE, filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}			
			if (statusCode >= 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			
			requestsByKind.add(current);
			i++;
		}
		
		// Request identificazione fallita, in questo caso viene rediretta sul connettore2
		String pool = Common.POOL_0;
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.addHeader(Common.HEADER_ID_CONDIZIONE+"-SBAGLIATO", Common.filtriPools.get(pool).get(0));
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso,Set.of(Common.CONNETTORE_2));
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		checkResponses(responsesByKind);
		
		String messaggioFallbackAtteso = 	"Per la notifica viene utilizzato il connettore 'Connettore2', configurato per essere utilizzato in caso di identificazione condizionale fallita";

		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		for (var toCheck: toCheckForDiagnostici) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO, 
						CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO,
						MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(
						id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						CODICE_DIAGNOSTICO_CONSEGNA_NOTIFICHE_IDENTIFICAZIONE_FALLITA_UTILIZZO_CONNETTORE_DEFAULT, 
						messaggioFallbackAtteso);
			}
		}
		
	}
	
	public void parametroUrlNCUDiagnosticoInfo(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		// 		Errore di Consegna (4xx e 5xx )
		//
		// Il connettore di ripiego in caso di ncu è il 3

		final String erogazione = "TestConsegnaConNotificheCondizionaleByFiltroParametroUrlNCUDiagnosticoInfoRest";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			Set<String> connettoriSuccesso = entry.getValue();
			final int statusCode = entry.getKey();
			final String filtro =Common.filtriPools.get(pool).get(0); 

			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			
			// In "errore di consegna" rientrano tutti  i 4xx e tutti i 5xx da 501 in poi, 
			// Quindi in questo test tutte le 2xx, 4xx e 5xx vengono notificate.					
			
			requestsByKind.add(current);
			
			i++;
		}
		
		// richiesta ncu
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request=FiltroInesistente");
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of(Common.CONNETTORE_3));
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
		
		// Aggiungo richiesta problem, che deve passare
		String pool = Common.POOL_0;
		String filtro = Common.filtriPools.get(pool).get(0);
		connettoriSuccesso = Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3);
		var connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
		HttpRequest requestRestProblem = RequestBuilder.buildRestRequestProblem(erogazione);
		requestRestProblem.setUrl(requestRestProblem.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
		current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(requestRestProblem, 500, connettoriSuccesso, connettoriPool);
		current.tipoFault = TipoFault.REST;
		
		requestsByKind.add(current);
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		checkResponses(responsesByKind);
		
		String messaggioAtteso = "Il valore estratto dalla richiesta 'FiltroInesistente', ottenuto tramite identificazione 'FormBased' (Parametro URL: govway-testsuite-id_connettore_request), non consente di identificare alcun connettore da utilizzare";
		String messaggioFallbackAtteso = 	"Per la notifica viene utilizzato il connettore 'Connettore3', configurato per essere utilizzato nel caso in cui la condizione estratta dalla richiesta non ha permesso di identificare alcun connettore";
		
		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		for (var toCheck: toCheckForDiagnostici) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO, 
						IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_NOTIFICA_NESSUN_CONNETTORE_UTILIZZABILE_INFO_2,
						messaggioAtteso);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(
						id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_NOTIFICHE_NESSUN_CONNETTORE_UTILIZZABILE_UTILIZZO_CONNETTORE_DEFAULT, 
						messaggioFallbackAtteso);
			}
		}
	}
	
	
	public void templateICFDiagnosticoError(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//
		// Il connettore di ripiego in caso di ICF è il 0
		final String erogazione = "TestConsegnaConNotificheCondizionaleByFiltroTemplateICFDiagnosticoErrorRest";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			Set<String> connettoriSuccesso = entry.getValue();
			final int statusCode = entry.getKey();
			final String filtro =Common.filtriPools.get(pool).get(0);

			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}
			if (statusCode >= 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}					
			requestsByKind.add(current);
			i++;
		}
		
		// Request identificazione fallita, in questo caso viene rediretta sul connettore0
		int statusCode = 201;
		HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&govway-testsuite-id_SBAGLIATO_connettore_request=FiltroInesistente");
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso,Set.of(Common.CONNETTORE_0));
		current.esitoSincrono = CommonConsegnaMultipla.ESITO_OK_PRESENZA_ANOMALIE;
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
		
		// Aggiungo Richiesta Problem
		String pool = Common.POOL_0;
		String filtro = Common.filtriPools.get(pool).get(0);
		HttpRequest requestProblem = RequestBuilder.buildRestRequestProblem(erogazione);
		requestProblem.setUrl(requestProblem.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
		connettoriSuccesso = Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3);
		var connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
		current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(requestProblem, 500, connettoriSuccesso, connettoriPool);
		current.principaleSuperata = false;
		current.tipoFault = TipoFault.REST;
		requestsByKind.add(current);

		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		checkResponses(responsesByKind);
		
		String messaggioAtteso = "Identificazione 'Template' (${query:govway-testsuite-id_connettore_request}) non è riuscita ad estrarre dalla richiesta l'informazione utile ad i"
				+ "dentificare il connettore da utilizzare: Proprieta' 'ConditionalConfig.gwt' contiene un valore non corretto: Placeholder [{query:govway-testsuite-id_connettore_request}"
				+ "] resolution failed: object [java.util.HashMap] with wrong position [govway-testsuite-id_connettore_request] not exists as key in map";
		
		String messaggioFallback = 	"Per la notifica viene utilizzato il connettore 'Connettore0', configurato per essere utilizzato in caso di identificazione condizionale fallita";

		
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
						CODICE_DIAGNOSTICO_CONSEGNA_NOTIFICHE_IDENTIFICAZIONE_FALLITA_UTILIZZO_CONNETTORE_DEFAULT, 
						messaggioFallback);
			}
		}
	}
	
	
	public void velocityTemplateNCUQualsiasiNoDiagnostico(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		
		final String erogazione = "TestConsegnaConNotificheCondizionaleByFiltroVelocityTemplateNCUQualsiasiNoDiagnosticoRest";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();
		var connettoriRipiego = Common.setConnettoriAbilitati;

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			Set<String> connettoriSuccesso = entry.getValue();
			final int statusCode = entry.getKey();
			final String filtro =Common.filtriPools.get(pool).get(0);

			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}
			if (statusCode >= 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			
			requestsByKind.add(current);
			i++;
		}
		
		// Richiesta NCU
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request=FiltroInesistente");
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, connettoriRipiego);
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
		
		// Aggiungo richiesta Rest Problem
		String pool = Common.POOL_0;
		String filtro = Common.filtriPools.get(pool).get(0);
		HttpRequest requestProblem = RequestBuilder.buildRestRequestProblem(erogazione);
		requestProblem.setUrl(requestProblem.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
		connettoriSuccesso = Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3);
		var connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
		current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(requestProblem, 500, connettoriSuccesso, connettoriPool);
		current.tipoFault = TipoFault.REST;
		requestsByKind.add(current);

		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		checkResponses(responsesByKind);
		
		String messaggioAtteso = "Il messaggio verrà notificato a tutti i connettori indiscriminatamente poichè la condizione estratta dalla richiesta non ha permesso di identificare alcun connettore";
		
		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		for (var toCheck: toCheckForDiagnostici) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				IdentificazioneFallitaTest.checkAssenzaDiagnosticoTransazione(id_transazione, IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_INFO_2);
				IdentificazioneFallitaTest.checkAssenzaDiagnosticoTransazione(id_transazione, IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR_2);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(
						id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_NOTIFICHE_NESSUN_CONNETTORE_TROVATO_UTILIZZO_TUTTI_CONNETTORI,
						messaggioAtteso);
			}
		}
		
	}
	
	
	public void freemarkerTemplateICFQualsiasiNoDiagnostico(Map<Integer,Set<String>> statusCodeVsConnettori) {		
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		//	Le richieste con identificazione fallita vengono redirette su tutti i connettori

		final String erogazione = "TestConsegnaConNotificheCondizionaleByFiltroFreemarkerTemplateICFQualsiasiNoDiagnosticoRest";

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();
		var connettoriRipiego = Common.setConnettoriAbilitati;

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			Set<String> connettoriSuccesso = entry.getValue();
			final int statusCode = entry.getKey();
			final String filtro =Common.filtriPools.get(pool).get(0);

			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, connettoriPool);

			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}
			if (statusCode >= 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			
			requestsByKind.add(current);
			i++;
		}

		// Request identificazione fallita
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.setUrl(request.getUrl() + "&govway-testsuite-id_SBAGLIATO_connettore_request=FiltroInesistente");
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso,connettoriRipiego);
		requestsByKind.add(current);
		toCheckForDiagnostici.add(current);
		
		
		// Aggiungo Richiesta Problem
		String pool = Common.POOL_0;
		String filtro = Common.filtriPools.get(pool).get(0);
		HttpRequest requestProblem = RequestBuilder.buildRestRequestProblem(erogazione);
		requestProblem.setUrl(requestProblem.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
		connettoriSuccesso = Set.of(CONNETTORE_0, CONNETTORE_2, CONNETTORE_3);
		var connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
		current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(requestProblem, 500, connettoriSuccesso, connettoriPool);
		current.tipoFault = TipoFault.REST;
		requestsByKind.add(current);
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		checkResponses(responsesByKind);
		
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
	
	
	public void ICFDiagnosticoErrorNCUDiagnosticoInfo(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		// Per ICF i connettori di fallback sono tutti
		// Per NCU il connettore di fallback è il 1
		final String erogazione = "TestConsegnaConNotificheCondizionaleICFDiagnosticoErrorNCUDiagnosticoInfoRest";

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
			Set<String> connettoriSuccesso = entry.getValue();
			int statusCode = entry.getKey();

			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(Common.HEADER_ID_CONDIZIONE, Common.filtriPools.get(pool).get(0));
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso,connettoriPool);
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}
			if (statusCode >= 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			requestsByKind.add(current);
			
			
			// Request nessun connettore utilizzabile, in questo caso viene rediretta sul connettore1
			request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(Common.HEADER_ID_CONDIZIONE, "CONNETTORE_INESISTENTE");
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, connettoriRipiegoNCU);
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}
			if (statusCode >= 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			requestsByKind.add(current);
			
			if (current.principaleSuperata) {
				toCheckForDiagnosticiNCU.add(current);
			}
			
			i++;
		}
		
		// Request identificazione fallita, in questo caso viene rediretta su tutti i connettori
		String pool = Common.POOL_0;
		int statusCode = 201;
		var connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.addHeader(Common.HEADER_ID_CONDIZIONE+"-SBAGLIATO", Common.filtriPools.get(pool).get(0));
		var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, connettoriRipiegoICF);
		current.esitoSincrono = CommonConsegnaMultipla.ESITO_OK_PRESENZA_ANOMALIE;
		requestsByKind.add(current);
		toCheckForDiagnosticiICF.add(current);
		
		
		// Request nessun connettore utilizzabile, in questo caso viene rediretta sul connettore1
		statusCode = 202;
		connettoriSuccesso = CommonConsegnaMultipla.statusCode2xxVsConnettori.get(statusCode);
		request = RequestBuilder.buildRestRequest(erogazione);
		request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
		request.addHeader(Common.HEADER_ID_CONDIZIONE, "CONNETTORE_INESISTENTE");
		current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, connettoriRipiegoNCU);
		requestsByKind.add(current);
		toCheckForDiagnosticiNCU.add(current);
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		checkResponses(responsesByKind);
		
		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici info
		for (var toCheck: toCheckForDiagnosticiICF) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(
						id_transazione, 
						DIAGNOSTICO_SEVERITA_ERROR, 
						CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR,
						MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(
						id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_UTILIZZO_TUTTI_CONNETTORI, 
						MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_FALLBACK_TUTTI_CONNETTORI);
			}
		}
		
		String messaggioFallbackAtteso = 	"Per la notifica viene utilizzato il connettore 'Connettore1', configurato per essere utilizzato nel caso in cui la condizione estratta dalla richiesta non ha permesso di identificare alcun connettore";
		String messaggioDiagnostico = "Il valore estratto dalla richiesta 'CONNETTORE_INESISTENTE', ottenuto tramite identificazione 'HeaderBased' (Header HTTP: GovWay-TestSuite-Connettore), non consente di identificare alcun connettore da utilizzare";

		String CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_2 = "007044";
		
		// Recupero tutte le risposte che devono aver fallito l'identificazione e controllo i diagnostici
		for (var toCheck: toCheckForDiagnosticiNCU) {
			for (var response : responsesByKind.get(toCheck) ) {
				String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO, 
						CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_2,
						messaggioDiagnostico);
				
				IdentificazioneFallitaTest.checkDiagnosticoTransazione(id_transazione, 
						DIAGNOSTICO_SEVERITA_INFO,
						IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_NOTIFICHE_NESSUN_CONNETTORE_UTILIZZABILE_UTILIZZO_CONNETTORE_DEFAULT, 
						messaggioFallbackAtteso);
			}
		}

	}
	
	
	public void urlInvocazionePrefisso(Map<Integer,Set<String>> statusCodeVsConnettori) {
		/**
		 * Questo test testa anche la funzionalità di prefisso, per cui i ai filtri inviati viene rimosso
		 * il prefisso.  
		 */
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		final String erogazione = "TestConsegnaConNotificheCondizionaleByFiltroUrlInvocazionePrefissoRest";
		final String prefix = "Pool";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			Set<String> connettoriSuccesso = entry.getValue();
			int statusCode = entry.getKey();

			String filtro = Common.filtriPools.get(pool).get(0);
			filtro = filtro.substring(prefix.length());
			
			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso,connettoriPool);
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}
			if (statusCode >= 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			requestsByKind.add(current);
			
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		checkResponses(responsesByKind);
	
	}
	
	
	public void contenutoSuffisso(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		final String erogazione = "TestConsegnaConNotificheCondizionaleByFiltroContenutoSuffissoRest";

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		String prefisso = "PoolX";

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			Set<String> connettoriSuccesso = entry.getValue();
			int statusCode = entry.getKey();
			String filtro = Common.filtriPools.get(pool).get(0);
			filtro = filtro.substring(0,prefisso.length());
			
			HttpRequest request = RequestBuilder.buildRequest_Contenuto(filtro, erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, connettoriPool);
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}			
			if (statusCode >= 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			
			requestsByKind.add(current);
			
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		checkResponses(responsesByKind);
	}
	
	
	
	public void clientIp(Map<Integer,Set<String>> statusCodeVsConnettori) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		//	Il connettore0 ha il filtro con 127.0.0.1
		final String erogazione = "TestConsegnaConNotificheCondizionaleByFiltroClientIpRest";

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		// Finiscono tutte sul primo connettore
		for (var entry : statusCodeVsConnettori.entrySet()) {
			Set<String> connettoriPool = Set.of(Common.CONNETTORE_0);
			Set<String> connettoriSuccesso = entry.getValue();
			final int statusCode = entry.getKey();

			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());			
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, connettoriPool);
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}			
			if (statusCode >= 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			requestsByKind.add(current);
			
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		checkResponses(responsesByKind);
	}

	
	public void XForwardedForPrefissoESuffisso(Map<Integer,Set<String>> statusCodeVsConnettori) throws UtilsException {				
		/**
		 * L'erogazione aggiunge "Pool" "-Filtro0" come prefisso e suffisso, per cui mi basta inviare l'indice del 
		 * connettore.
		 */
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo		
		
		final String erogazione = "TestConsegnaConNotificheCondizionaleByFiltroXForwardedForPrefissoESuffissoRest";
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		List<String> forwardingHeaders = HttpUtilities.getClientAddressHeaders();
		
		String prefix = "Pool0";

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			Set<String> connettoriSuccesso = entry.getValue();
			final int statusCode = entry.getKey();
			
			String header = forwardingHeaders.get(i%forwardingHeaders.size());
			String filtro = Common.filtriPools.get(pool).get(0); 			
			
			filtro = filtro.substring(0,prefix.length());
			filtro = filtro.substring("Pool".length());

			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(header, filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso,connettoriPool);

			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}
			if (statusCode >= 500 && statusCode <= 599) {
				current.principaleSuperata = false;	
			}
			requestsByKind.add(current);
			
			i++;
		}
				
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		checkResponses(responsesByKind);
	}
	
	

	public void regole(Map<Integer,Set<String>> statusCodeVsConnettori) throws UtilsException {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo		
		// 		ErroreDiConsegna
		final String erogazione = "TestConsegnaConNotificheCondizionaleByRegoleRest";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		List<String> forwardingHeaders = HttpUtilities.getClientAddressHeaders();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeVsConnettori.entrySet()) {
			String pool = Common.pools.get(i % Common.pools.size());
			Set<String> connettoriPool = new HashSet<>(Common.connettoriPools.get(pool));
			final String filtro = Common.filtriPools.get(pool).get(0); 

			// Filtro HeaderHttp
			HttpRequest request = RequestBuilder.buildRequest_HeaderHttp(filtro, erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Filtro ParametroUrl
			request = RequestBuilder.buildRequest_ParametroUrl(filtro, erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Filtro Regola Statica, vanno tutte sul pool2
			Set<String> connettoriPoolStatica = new HashSet<>(Common.connettoriPools.get(Common.POOL_2));
			request = RequestBuilder.buildRequest_Statica(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPoolStatica);
			requestsByKind.add(current);
			
			// Filtro Regola ClientI Ip, vanno tutte sul Connettore0
			Set<String> connettoriPoolClientIp = Set.of(Common.CONNETTORE_0);
			request = RequestBuilder.buildRequest_ClientIp(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPoolClientIp);
			requestsByKind.add(current);
			
			// Filtro Regola Contenuto
			request = RequestBuilder.buildRequest_Contenuto(filtro, erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Filtro Regola FreemarkerTemplate
			request = RequestBuilder.buildRequest_FreemarkerTemplate(filtro, erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);

			// Filtro Regola Velocity Template
			request = RequestBuilder.buildRequest_VelocityTemplate(filtro, erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Filtro Regola Template
			request = RequestBuilder.buildRequest_Template(filtro, erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Filtro Regola Url Invocazione
			request = RequestBuilder.buildRequest_UrlInvocazione(filtro, erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			// Filtro Regola XForwardedFor
			String header = forwardingHeaders.get(i%forwardingHeaders.size());
			request = RequestBuilder.buildRequest_ForwardedFor(filtro, header, erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPool);
			requestsByKind.add(current);
			
			i++;
		}
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);
		
		checkResponses(responsesByKind);
				
	}
	
	
	public static void checkResponses(Map<RequestAndExpectations, List<HttpResponse>> responsesByKind) {
		assertTrue(!responsesByKind.isEmpty());
		
		// Le richieste per cui la transazione sincrona ha avuto successo devono essere schedulate
		for (var requestExpectation : responsesByKind.keySet()) {
			var responses = responsesByKind.get(requestExpectation);
			Common.checkResponsesStatus(responses, requestExpectation.statusCodePrincipale);
			assertTrue(!responses.isEmpty());
			
			// Deve essere la fusione dei connettoriOk e i connettoriErrore\conettoriScheduling Disabilitato
			Set<String> connettoriCoinvolti = setSum(requestExpectation.connettoriSuccesso, requestExpectation.connettoriFallimento);
						
			if (requestExpectation.principaleSuperata) {
				int esitoSincrono;
				if (requestExpectation.esitoSincrono != -1) {
					esitoSincrono = requestExpectation.esitoSincrono;
				}
				else if (requestExpectation.tipoFault != TipoFault.NESSUNO) {
					esitoSincrono = CommonConsegnaMultipla.ESITO_ERRORE_APPLICATIVO;				
				} else {
					esitoSincrono = CommonConsegnaMultipla.esitoConsegnaFromStatusCode(requestExpectation.statusCodePrincipale);
				}
				CommonConsegnaMultipla.withBackoff( () -> checkPresaInConsegnaNotifica(responses, connettoriCoinvolti.size(), esitoSincrono));				
				checkSchedulingConnettoreIniziato(responses, connettoriCoinvolti);
			} else {
				for (var response : responses) {
					CommonConsegnaMultipla.withBackoff( () -> checkNessunaNotifica(response));
					checkNessunoScheduling(response);
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
