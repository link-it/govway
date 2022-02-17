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

import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.IdentificazioneFallitaTest.DIAGNOSTICO_SEVERITA_INFO;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.buildRequestAndExpectationFiltered;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla.statusCodeRestVsConnettori;

import java.io.File;
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
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * 
 * @author Francesco Scarlato
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
public class CondizionaleByNomeRestTest extends ConfigLoader {
	
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
	
	
	@Test
	public void headerHttp() {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeHeaderHttpRest";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		int i = 0;
		
		for (var entry : statusCodeRestVsConnettori.entrySet()) {
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			final int statusCode = entry.getKey();
			final Set<String> connettoriSuccesso = entry.getValue();

			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.addHeader(Common.HEADER_ID_CONDIZIONE, filtro);			
			var current = buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso,Set.of(filtro));
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
		CondizionaleByFiltroRestTest.checkResponses(responsesByKind);
	}

	
	@Test
	public void parametroUrl() {
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeParametroUrlRest";
		parametroUrl_Impl(erogazione);
	}
	
	
	@Test
	public void template() {
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeTemplateRest";
		parametroUrl_Impl(erogazione);		
	}
	
	@Test
	public void velocityTemplate() {
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeVelocityTemplateRest";
		parametroUrl_Impl(erogazione);		
	}
	
	
	@Test
	public void freemarkerTemplate() {
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeFreemarkerTemplateRest";
		parametroUrl_Impl(erogazione);		
	}
	
	
	@Test
	public void clientIp() {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		// Finiscono tutte sul primo connettore
		
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeClientIpRest";
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		for (var entry : statusCodeRestVsConnettori.entrySet()) {
			int statusCode = entry.getKey();
			Set<String> connettoriSuccesso = entry.getValue();
			Set<String> connettoriPool = Set.of("127.0.0.1");
			
			// Sostituisco Il Connettore0 con "127.0.0.1"
			connettoriSuccesso = connettoriSuccesso.stream()
					.map( c -> c.equals(Common.CONNETTORE_0) ? "127.0.0.1" : c)
					.collect(Collectors.toSet());
					
			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
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
		CondizionaleByFiltroRestTest.checkResponses(responsesByKind);
	}
	
	
	@Test
	public void regole() throws UtilsException {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		//		ErroreDiConsegna
		
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeRegoleRest";
		final String CONNETTORE_LOCALHOST = "127.0.0.1";

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		List<String> forwardingHeaders = HttpUtilities.getClientAddressHeaders();	
		
		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeRestVsConnettori.entrySet()) {
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			var connettoriPool = Set.of(filtro);
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
			
			// Filtro Regola Statica, vanno tutte sul Connettore0
			Set<String> connettoriPoolStatica = Set.of(Common.CONNETTORE_0);
			request = RequestBuilder.buildRequest_Statica(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), connettoriPoolStatica);
			requestsByKind.add(current);
			
			/// Filtro Regola ClientI Ip, vanno tutte sul Connettore a parte per il client ip
			// Il connettore localhost ha la stessa configurazione del connettore0 ovvero passa solo con soapFault, per tutto il resto fallisce.
			// Non è necessario perciò aggiungerlo alla map statusCodeVsConnettori, dato che sarà un connettore che vedrà solo rispedizioni.
			// Per come è costruita la buildRequestAndExpectationFiltered, il connettore localhost va a finire direttamente nei connettori 
			// da considerare falliti.
			Set<String> connettoriPoolClientIp = Set.of(CONNETTORE_LOCALHOST);
			
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
		CondizionaleByFiltroRestTest.checkResponses(responsesByKind);
	}
	
	
	@Test
	public void contenutoSuffisso() {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeContenutoSuffissoRest";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		int i = 0;
		
		for (var entry : statusCodeRestVsConnettori.entrySet()) {
			int statusCode = entry.getKey();
			// Aggiungo i suffissi ai connettori da testare
			Set<String> connettoriSuccesso = entry.getValue().stream()
					.map( c -> c + "-SuffissoTest")
					.collect(Collectors.toSet());
			
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());

			HttpRequest request = RequestBuilder.buildRequest_Contenuto(filtro, erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			
			var current = buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of(filtro+"-SuffissoTest"));
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
		CondizionaleByFiltroRestTest.checkResponses(responsesByKind);
	}
	
	
	@Test
	public void urlInvocazionePrefisso() {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeUrlInvocazionePrefissoRest";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		final String prefisso = "Connettore";

		int i = 0;
		for (var entry : statusCodeRestVsConnettori.entrySet()) {
			final String connettore =Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size()); 
			final String filtro = connettore.substring(prefisso.length());
			final int statusCode = entry.getKey();

			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			
			var current = buildRequestAndExpectationFiltered(request, entry.getKey(),entry.getValue(), Set.of(connettore));
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
		CondizionaleByFiltroRestTest.checkResponses(responsesByKind);
	}
	
	
	@Test
	public void XForwardedForPrefissoESuffisso() throws UtilsException {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		final String erogazione = "TestConsegnaConNotificheByNomeXForwardedForPrefissoESuffissoRest";

		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		List<String> forwardingHeaders = HttpUtilities.getClientAddressHeaders();
		
		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeRestVsConnettori.entrySet()) {
			final int statusCode = entry.getKey();
			
			// Aggiungo i suffissi ai connettori da testare
			Set<String> connettoriSuccesso = entry.getValue().stream()
					.map( c -> "PrefissoTest-" + c + "-SuffissoTest")
					.collect(Collectors.toSet());
			
			String header = forwardingHeaders.get(i%forwardingHeaders.size());
			String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());

			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.addHeader(header, filtro);
			
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, Set.of("PrefissoTest-"+filtro+"-SuffissoTest"));
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
		CondizionaleByFiltroRestTest.checkResponses(responsesByKind);
	}
	
	
	@Test
	public void parametroUrlNCUNessunConnettore() {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		//	Nessun connettore notificato in caso di NCU

		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeParametroUrlNCUNessunConnettoreRest";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeRestVsConnettori.entrySet()) {
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			final int statusCode = entry.getKey();
			final Set<String> connettoriSuccesso = entry.getValue();
			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, Set.of(filtro));
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}			
			if (statusCode >= 500 && statusCode <= 599) {
				current.principaleSuperata = false;
			}
			requestsByKind.add(current);
			
			request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request=FiltroInesistente");
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of());
			current.principaleSuperata = false;
			
			requestsByKind.add(current);
			toCheckForDiagnostici.add(current);
			
			i++;
		}
		
		Map<RequestAndExpectations, List<HttpResponse>> responsesByKind = CommonConsegnaMultipla.makeRequestsByKind(requestsByKind, 1);

		CondizionaleByFiltroRestTest.checkResponses(responsesByKind);

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
						"007050",
						"Il messaggio di richiesta non verrà notificato ad alcun connettore poichè l'identificazione condizionale è fallita");
			}
		}
		
	}	
	
	
	@Test
	public void parametroUrlICFNessunConnettore() {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		//	Nessun connettore notificato in caso di ICF

		final String erogazione = "TestConsegnaConNotificheCondizionaleByNomeParametroUrlICFNessunConnettoreRest";
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();
		Set<RequestAndExpectations> toCheckForDiagnostici = new HashSet<>();

		int i = 0;
		// Prima costruisco le richieste normalmente
		for (var entry : statusCodeRestVsConnettori.entrySet()) {
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			final int statusCode = entry.getKey();
			final Set<String> connettoriSuccesso = entry.getValue();
			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			var current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode,connettoriSuccesso, Set.of(filtro));
			if (statusCode >= 400 && statusCode <= 499) {
				current.principaleSuperata = false;
			}			
			if (statusCode >= 500 && statusCode <= 599) {
				current.principaleSuperata = false;
			}
			requestsByKind.add(current);
			
			// Richiesta ICF
			request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + entry.getKey());
			request.setUrl(request.getUrl() + "&parametroSbagliato=FiltroInesistente");
			current = CommonConsegnaMultipla.buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso, Set.of());
			current.principaleSuperata = false;
			
			requestsByKind.add(current);
			toCheckForDiagnostici.add(current);
			
			i++;
		}
		
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
	
	
	private static void parametroUrl_Impl(String erogazione) {
		// Notifiche Condizionali Quando:
		//		CompletateConSuccesso
		//		FaultApplicativo
		
		List<RequestAndExpectations> requestsByKind = new ArrayList<>();

		int i = 0;
		
		for (var entry : statusCodeRestVsConnettori.entrySet()) {
			final String filtro = Common.connettoriAbilitati.get(i % Common.connettoriAbilitati.size());
			final int statusCode = entry.getKey();
			final Set<String> connettoriSuccesso = entry.getValue();
			HttpRequest request = RequestBuilder.buildRestRequest(erogazione);
			request.setUrl(request.getUrl()+"&returnCode=" + statusCode);
			request.setUrl(request.getUrl() + "&govway-testsuite-id_connettore_request="+filtro);
			var current = buildRequestAndExpectationFiltered(request, statusCode, connettoriSuccesso,Set.of(filtro));
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
		
		CondizionaleByFiltroSoapTest.checkResponses(responsesByKind);
	}
}
