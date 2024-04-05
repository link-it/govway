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


package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.raggruppamento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Credenziali;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {
	
	private static final String basePath = System.getProperty("govway_base_path");
	private static final String testIdHeader = "GovWay-TestSuite-RL-Grouping";
	
	@Test
	public void perRichiedenteFruizione() {
		
		LocalDateTime dataSpedizione = LocalDateTime.now();	
		
		final String erogazione = "RaggruppamentoRichiedenteRest";
		final String urlServizio =  basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/json");
		requestGroup1.setMethod(HttpRequestMethod.GET);
		requestGroup1.setUrl(urlServizio);
		requestGroup1.setUsername(Credenziali.applicativoSITFFiltrato.username);
		requestGroup1.setPassword(Credenziali.applicativoSITFFiltrato.password);
		
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/json");
		requestGroup2.setMethod(HttpRequestMethod.GET);
		requestGroup2.setUrl(urlServizio);
		requestGroup2.setUsername(Credenziali.applicativoSITFNonFiltrato.username);
		requestGroup2.setPassword(Credenziali.applicativoSITFNonFiltrato.password);
		
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/json");
		requestGroup3.setMethod(HttpRequestMethod.GET);
		requestGroup3.setUrl(urlServizio);
		requestGroup3.setUsername(Credenziali.applicativoSITFRuoloFiltrato.username);
		requestGroup3.setPassword(Credenziali.applicativoSITFRuoloFiltrato.password);
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		makeAndCheckGroupRequests(TipoServizio.FRUIZIONE, PolicyAlias.ORARIO, erogazione, requests);	
		
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.waitForDbEvents();
		List<Map<String, Object>> events = org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.getNotificheEventi(dataSpedizione);		
		
		
		String idServizio = "SoggettoInternoTestFruitore/SoggettoInternoTest/RaggruppamentoRichiedenteRest/v1";
		String groupBy = null;
		
		groupBy = "Fruitore: gw/SoggettoInternoTestFruitore, SAFruitore: "+Credenziali.applicativoSITFFiltrato.username+", ApplicativoToken: *";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Fruitore: gw/SoggettoInternoTestFruitore, SAFruitore: "+Credenziali.applicativoSITFNonFiltrato.username+", ApplicativoToken: *";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Fruitore: gw/SoggettoInternoTestFruitore, SAFruitore: "+Credenziali.applicativoSITFRuoloFiltrato.username+", ApplicativoToken: *";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
	}
	
	@Test
	public void perRichiedenteErogazione() {
		
		LocalDateTime dataSpedizione = LocalDateTime.now();	
		
		final String erogazione = "RaggruppamentoRichiedenteRest";
		final String urlServizio =  basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/json");
		requestGroup1.setMethod(HttpRequestMethod.GET);
		requestGroup1.setUrl(urlServizio);
		requestGroup1.setUsername(Credenziali.applicativoRuoloFiltrato.username);
		requestGroup1.setPassword(Credenziali.applicativoRuoloFiltrato.password);
		
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/json");
		requestGroup2.setMethod(HttpRequestMethod.GET);
		requestGroup2.setUrl(urlServizio);
		requestGroup2.setUsername(Credenziali.applicativoRuoloFiltrato2.username);
		requestGroup2.setPassword(Credenziali.applicativoRuoloFiltrato2.password);
		
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/json");
		requestGroup3.setMethod(HttpRequestMethod.GET);
		requestGroup3.setUrl(urlServizio);
		requestGroup3.setUsername(Credenziali.soggettoRuoloFiltrato.username);
		requestGroup3.setPassword(Credenziali.soggettoRuoloFiltrato.password);
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		makeAndCheckGroupRequests(TipoServizio.EROGAZIONE, PolicyAlias.ORARIO, erogazione, requests);
		
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.waitForDbEvents();
		List<Map<String, Object>> events = org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.getNotificheEventi(dataSpedizione);		
				
		String idServizio = "SoggettoInternoTest/RaggruppamentoRichiedenteRest/v1";
		String groupBy = null;
		
		groupBy = "Fruitore: gw/SoggettoNonFiltrato, SAFruitore: "+Credenziali.applicativoRuoloFiltrato.username+", ApplicativoToken: *";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Fruitore: gw/SoggettoNonFiltrato, SAFruitore: "+Credenziali.applicativoRuoloFiltrato2.username+", ApplicativoToken: *";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Fruitore: gw/"+Credenziali.soggettoRuoloFiltrato.username+", SAFruitore: *, ApplicativoToken: *";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
	}
	
	@Test
	public void perRichiedenteTokenFruizione() throws Exception {
		
		LocalDateTime dataSpedizione = LocalDateTime.now();	
		
		final String erogazione = "RaggruppamentoRichiedenteTokenRest";
		final String urlServizio =  basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/json");
		requestGroup1.setMethod(HttpRequestMethod.GET);
		requestGroup1.setUrl(urlServizio);
		String clientIdNonFiltrato = "clientIdNonRegistrato";
		String tokenClienIdSonosciuto = org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.buildJWT(clientIdNonFiltrato, null, false);
		requestGroup1.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+tokenClienIdSonosciuto);
		
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/json");
		requestGroup2.setMethod(HttpRequestMethod.GET);
		requestGroup2.setUrl(urlServizio);
		String clientIdApp1 = "clientIdApplicativoToken1SoggettoInternoFruitore";
		String tokenClientIdApp1 = org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.buildJWT(clientIdApp1, null, false);
		requestGroup2.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+tokenClientIdApp1);
		
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/json");
		requestGroup3.setMethod(HttpRequestMethod.GET);
		requestGroup3.setUrl(urlServizio);
		String clientIdApp2 = "clientIdApplicativoToken2SoggettoInternoFruitore";
		String tokenClientIdApp2 = org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.buildJWT(clientIdApp2, null, false);
		requestGroup3.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+tokenClientIdApp2);
		
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		makeAndCheckGroupRequests(TipoServizio.FRUIZIONE, PolicyAlias.ORARIO, erogazione, requests);
		
		
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.waitForDbEvents();
		List<Map<String, Object>> events = org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.getNotificheEventi(dataSpedizione);		
				
		String idServizio = "SoggettoInternoTestFruitore/SoggettoInternoTest/RaggruppamentoRichiedenteTokenRest/v1";
		String groupBy = null;
		
		groupBy = "Fruitore: gw/SoggettoInternoTestFruitore, SAFruitore: Anonimo, ApplicativoToken: gw/SoggettoInternoTestFruitore/ApplicativoToken1SoggettoInternoFruitore";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Fruitore: gw/SoggettoInternoTestFruitore, SAFruitore: Anonimo, ApplicativoToken: *";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Fruitore: gw/SoggettoInternoTestFruitore, SAFruitore: Anonimo, ApplicativoToken: gw/SoggettoInternoTestFruitore/ApplicativoToken2SoggettoInternoFruitore";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);

	}
	
	@Test
	public void perRichiedenteTokenErogazione() throws Exception {
		
		LocalDateTime dataSpedizione = LocalDateTime.now();	
		
		final String erogazione = "RaggruppamentoRichiedenteTokenRest";
		final String urlServizio =  basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/json");
		requestGroup1.setMethod(HttpRequestMethod.GET);
		requestGroup1.setUrl(urlServizio);
		String clientIdNonFiltrato = "clientIdNonRegistrato";
		String tokenClienIdSonosciuto = org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.buildJWT(clientIdNonFiltrato, null, false);
		requestGroup1.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+tokenClienIdSonosciuto);
		
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/json");
		requestGroup2.setMethod(HttpRequestMethod.GET);
		requestGroup2.setUrl(urlServizio);
		String clientIdApp1 = "clientIdApplicativoToken1SoggettoInternoFruitore";
		String tokenClientIdApp1 = org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.buildJWT(clientIdApp1, null, false);
		requestGroup2.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+tokenClientIdApp1);
		
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/json");
		requestGroup3.setMethod(HttpRequestMethod.GET);
		requestGroup3.setUrl(urlServizio);
		String clientIdApp2 = "clientIdApplicativoToken2SoggettoInternoFruitore";
		String tokenClientIdApp2 = org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.buildJWT(clientIdApp2, null, false);
		requestGroup3.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+tokenClientIdApp2);
		
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		makeAndCheckGroupRequests(TipoServizio.EROGAZIONE, PolicyAlias.ORARIO, erogazione, requests);
		
			
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.waitForDbEvents();
		List<Map<String, Object>> events = org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.getNotificheEventi(dataSpedizione);		
				
		String idServizio = "SoggettoInternoTest/RaggruppamentoRichiedenteTokenRest/v1";
		String groupBy = null;
		
		groupBy = "Fruitore: -, SAFruitore: *, ApplicativoToken: *";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Fruitore: -, SAFruitore: *, ApplicativoToken: gw/SoggettoInternoTestFruitore/ApplicativoToken1SoggettoInternoFruitore";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Fruitore: -, SAFruitore: *, ApplicativoToken: gw/SoggettoInternoTestFruitore/ApplicativoToken2SoggettoInternoFruitore";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
	}
	
	private static void makeAndCheckGroupRequests(TipoServizio tipoServizio, PolicyAlias policy, String erogazione, HttpRequest[] requests) {
		
		final int maxRequests = 5;
		final int windowSize = Utils.getPolicyWindowSize(policy);
		
		final String idPolicy = tipoServizio == TipoServizio.EROGAZIONE
				? dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy)
				: dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
				
		
		Utils.resetCounters(idPolicy);
		Utils.waitForPolicy(policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		// Faccio le richieste tra i gruppi in maniera simultanea
		//	per assicurarmi che il conteggio sia corretto anche in caso di
		//	richieste parallele e quindi codice concorrente lato server
		
		final List<HttpResponse> responsesOk = new java.util.ArrayList<>();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxRequests*requests.length);

		for (int i = 0; i < maxRequests; i++) {
			
			Utilities.sleep(200); // essendo la policy 'completata con sucesso' si conta solo dopo aver completato la richiesta, e andando in parallelo potrebbero risultare nei limiti
						
			for(var request : requests) {
			executor.execute(() -> {
				try {
					logRateLimiting.info(request.getMethod() + " " + request.getUrl());
					responsesOk.add(HttpUtilities.httpInvoke(request));
					logRateLimiting.info("Richiesta effettuata..");
				} catch (UtilsException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			});
			}
		}
		
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logRateLimiting.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		
		responsesOk.forEach(r -> {
			logRateLimiting.info("statusCode: " + r.getResultHTTPOperation());
			logRateLimiting.info("headers: " + r.getHeadersValues());
		});
		Utils.waitForZeroGovWayThreads();
		
		logRateLimiting.info(Utils.getPolicy(idPolicy));
		
		// Le richieste di sopra devono andare tutte bene e devono essere conteggiate
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkOkRequests(responsesOk, windowSize, maxRequests);
		
		
		final List<HttpResponse> responsesFailed = new java.util.ArrayList<>();
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxRequests*requests.length);
		for (int i = 0; i < maxRequests; i++) {
			
			Utilities.sleep(200); // essendo la policy 'completata con sucesso' si conta solo dopo aver completato la richiesta, e andando in parallelo potrebbero risultare nei limiti
			
			for(var request : requests) {
			executor.execute(() -> {
				try {
					logRateLimiting.info(request.getMethod() + " " + request.getUrl());
					responsesFailed.add(HttpUtilities.httpInvoke(request));
					logRateLimiting.info("Richiesta effettuata..");
				} catch (UtilsException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			});
			}
		}
		
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logRateLimiting.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		
		responsesFailed.forEach(r -> {
			logRateLimiting.info("statusCode: " + r.getResultHTTPOperation());
			logRateLimiting.info("headers: " + r.getHeadersValues());
		});
		Utils.waitForZeroGovWayThreads();
		logRateLimiting.info(Utils.getPolicy(idPolicy));
		// Tutte le richieste di sopra falliscono perchè il limit è stato raggiunto dal primo set di richieste
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkFailedRequests(responsesFailed, windowSize, maxRequests);
		
	}
	
	
	@Test
	public void perParametroUrlErogazione() {
		perParametroUrl(TipoServizio.EROGAZIONE);
	}
	
	@Test
	public void perParametroUrlFruizione() {
		perParametroUrl(TipoServizio.FRUIZIONE);
	}

	@Test
	public void perContenutoErogazione() {
		perContenuto(TipoServizio.EROGAZIONE);
	}
	
	@Test
	public void perContenutoFruizione() {
		perContenuto(TipoServizio.FRUIZIONE);
	}
	
	@Test
	public void perUrlInvocazioneErogazione() {
		perUrlInvocazione(TipoServizio.EROGAZIONE);
	}
	
	@Test
	public void perUrlInvocazioneFruizione() {
		perUrlInvocazione(TipoServizio.EROGAZIONE);
	}
	
	@Test
	public void perHeaderErogazione() {
		perHeader(TipoServizio.EROGAZIONE);
	}
	
	
	@Test
	public void perHeaderFruizione() {
		perHeader(TipoServizio.FRUIZIONE);
	}
	
	@Test
	public void perHeaderXForwardedForErogazione() throws UtilsException {
		List<String> test = new ArrayList<>();
		HttpUtilities.getClientAddressHeaders().forEach( headerName ->
				perHeaderXForwardedFor(TipoServizio.EROGAZIONE, headerName, test)
			);
	}
	
	@Test
	public void perHeaderXForwardedForFruizione() throws UtilsException {
		List<String> test = new ArrayList<>();
		HttpUtilities.getClientAddressHeaders().forEach( headerName ->
			perHeaderXForwardedFor(TipoServizio.FRUIZIONE, headerName, test)
		);
	}
	
	
	private static final String EROGAZIONE_1 = "RaggruppamentoTokenRest";
	private static final String EROGAZIONE_2 = "Raggruppamento2TokenRest";
	private static final String EROGAZIONE_3 = "Raggruppamento3TokenRest";
	private static final String RAGGRUPPAMENTO_SUBJECT = "subject";
	private static final String RAGGRUPPAMENTO_CLIENT = "client";
	private static final String RAGGRUPPAMENTO_MULTIPLO = "multiplo";
	
	@Test
	public void perTokenSubjectErogazione() throws UtilsException { 
		perToken(TipoServizio.EROGAZIONE, EROGAZIONE_1, RAGGRUPPAMENTO_SUBJECT);
	}
	@Test
	public void perTokenSubjectFruizione() throws UtilsException { 
		perToken(TipoServizio.FRUIZIONE, EROGAZIONE_1, RAGGRUPPAMENTO_SUBJECT);
	}
	
	@Test
	public void perTokenClientIdErogazione() throws UtilsException { 
		perToken(TipoServizio.EROGAZIONE, EROGAZIONE_2, RAGGRUPPAMENTO_CLIENT);
	}
	@Test
	public void perTokenClientIdFruizione() throws UtilsException {
		perToken(TipoServizio.FRUIZIONE, EROGAZIONE_2, RAGGRUPPAMENTO_CLIENT);
	}
	
	@Test
	public void perTokenErogazione() throws UtilsException { 
		perToken(TipoServizio.EROGAZIONE, EROGAZIONE_3, RAGGRUPPAMENTO_MULTIPLO);
	}
	@Test
	public void perTokenFruizione() throws UtilsException {
		perToken(TipoServizio.FRUIZIONE, EROGAZIONE_3, RAGGRUPPAMENTO_MULTIPLO);
	}
	
	
	@Test
	public void perRisorsaErogazione() {
		perRisorsa(TipoServizio.EROGAZIONE);
	}
	
	
	@Test
	public void perRisorsaFruizione() {
		perRisorsa(TipoServizio.FRUIZIONE);
	}
	
	
	public static void perRisorsa(TipoServizio tipoServizio) {
		LocalDateTime dataSpedizione = LocalDateTime.now();	
		
		final String erogazione = "RaggruppamentoRest";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/json");
		requestGroup1.setMethod(HttpRequestMethod.GET);
		requestGroup1.setUrl(urlServizio + "/minuto");		
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/json");
		requestGroup2.setMethod(HttpRequestMethod.GET);
		requestGroup2.setUrl(urlServizio + "/giornaliero");		
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/json");
		requestGroup3.setMethod(HttpRequestMethod.GET);
		requestGroup3.setUrl(urlServizio + "/orario");
		
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		// Dico quale policy di rate limiting deve attivarsi, perchè sono 
		// filtrate per header
		for(var r: requests) {
			r.addHeader(testIdHeader, PolicyAlias.FILTRORISORSA.value);
		}
		
		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.FILTRORISORSA, erogazione, requests);	
		
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.waitForDbEvents();
		List<Map<String, Object>> events = org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.getNotificheEventi(dataSpedizione);		
				
		String idServizio = "SoggettoInternoTest/RaggruppamentoRest/v1";
		if(TipoServizio.FRUIZIONE.equals(tipoServizio)) {
			idServizio = "SoggettoInternoTestFruitore/"+idServizio;
		}
		String groupBy = null;
		
		groupBy = "Azione: GET_minuto";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTRORISORSA, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Azione: GET_giornaliero";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTRORISORSA, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Azione: GET_orario";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTRORISORSA, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
	}
	
	
	public static void perToken(TipoServizio tipoServizio, String erogazione, String tipoRaggruppamento) throws UtilsException {
		LocalDateTime dataSpedizione = LocalDateTime.now();	
		
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		// Signature Json
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
		Properties signatureProps = new Properties();
		signatureProps.put("rs.security.keystore.file", "/etc/govway/keys/pa.p12");
		signatureProps.put("rs.security.keystore.type","pkcs12");
		signatureProps.put("rs.security.keystore.alias","paP12");
		signatureProps.put("rs.security.keystore.password","keypa");
		signatureProps.put("rs.security.key.password","keypa");
		signatureProps.put("rs.security.signature.algorithm","RS256");
		signatureProps.put("rs.security.signature.include.cert","false");
		signatureProps.put("rs.security.signature.include.public.key","false");
		signatureProps.put("rs.security.signature.include.key.id","true");
		signatureProps.put("rs.security.signature.include.cert.sha1","false");
		signatureProps.put("rs.security.signature.include.cert.sha256","false");
		
		JsonSignature jsonSignature = null;
		jsonSignature = new JsonSignature(signatureProps, options);
		
		Date now = DateManager.getDate();
		Date campione = new Date( (now.getTime()/1000)*1000);
		Date iat = new Date(campione.getTime());
		
		String token1 = "{\n"+
		  "\"sub\": \"gruppo1\",\n"+
		  "\"iss\": \"example.org\",\n"+
		  "\"preferred_username\": \"Utente1\",\n"+
		  "\"azp\": \"clientTest1\",\n"+
		  "\"email\": \"email1@prova.org\",\n"+
		  "\"iat\": "+(iat.getTime()/1000)+"\n"+
		"}";
		String token1Signed = jsonSignature.sign(token1);
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/json");
		requestGroup1.setMethod(HttpRequestMethod.GET);
		requestGroup1.setUrl(urlServizio+"?access_token="+token1Signed);
		
		
		String token2 = "{\n"+
				  "\"sub\": \"gruppo2\",\n"+
				  "\"iss\": \"example.org\",\n"+
				  "\"preferred_username\": \"Utente2\",\n"+
				  "\"azp\": \"clientTest2\",\n"+
				  "\"email\": \"email2@prova.org\",\n"+
				  "\"iat\": "+(iat.getTime()/1000)+"\n"+
				"}";
				
		String token2Signed = jsonSignature.sign(token2);
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/json");
		requestGroup2.setMethod(HttpRequestMethod.GET);
		requestGroup2.setUrl(urlServizio+"?access_token="+token2Signed);
		
		String token3 = "{\n"+
				  "\"sub\": \"gruppo3\",\n"+
				  "\"iss\": \"example.org\",\n"+
				  "\"preferred_username\": \"Utente3\",\n"+
				  "\"azp\": \"clientTest3\",\n"+
				  "\"email\": \"email3@prova.org\",\n"+
				  "\"iat\": "+(iat.getTime()/1000)+"\n"+
				"}";
				
		String token3Signed = jsonSignature.sign(token3);
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/json");
		requestGroup3.setMethod(HttpRequestMethod.GET);
		requestGroup3.setUrl(urlServizio+"?access_token="+token3Signed);
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		

		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.ORARIO, erogazione, requests);	
		
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.waitForDbEvents();
		List<Map<String, Object>> events = org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.getNotificheEventi(dataSpedizione);		
				
		String idServizio = "SoggettoInternoTest/"+erogazione+"/v1";
		if(TipoServizio.FRUIZIONE.equals(tipoServizio)) {
			idServizio = "SoggettoInternoTestFruitore/"+idServizio;
		}
		String groupBy = null;
		
		if(RAGGRUPPAMENTO_SUBJECT.equals(tipoRaggruppamento)) {
			groupBy = "token_issuer: example.org, token_subject: gruppo1";
		}
		else if(RAGGRUPPAMENTO_CLIENT.equals(tipoRaggruppamento)) {
			groupBy = "token_clientId: clientTest1";
		}
		else if(RAGGRUPPAMENTO_MULTIPLO.equals(tipoRaggruppamento)) {
			groupBy = "token_issuer: example.org, token_subject: gruppo1, token_clientId: clientTest1, token_username: Utente1, token_eMail: email1@prova.org";
		}
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		if(RAGGRUPPAMENTO_SUBJECT.equals(tipoRaggruppamento)) {
			groupBy = "token_issuer: example.org, token_subject: gruppo2";
		}
		else if(RAGGRUPPAMENTO_CLIENT.equals(tipoRaggruppamento)) {
			groupBy = "token_clientId: clientTest2";
		}
		else if(RAGGRUPPAMENTO_MULTIPLO.equals(tipoRaggruppamento)) {
			groupBy = "token_issuer: example.org, token_subject: gruppo2, token_clientId: clientTest2, token_username: Utente2, token_eMail: email2@prova.org";
		}
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		if(RAGGRUPPAMENTO_SUBJECT.equals(tipoRaggruppamento)) {
			groupBy = "token_issuer: example.org, token_subject: gruppo3";
		}
		else if(RAGGRUPPAMENTO_CLIENT.equals(tipoRaggruppamento)) {
			groupBy = "token_clientId: clientTest3";
		}
		else if(RAGGRUPPAMENTO_MULTIPLO.equals(tipoRaggruppamento)) {
			groupBy = "token_issuer: example.org, token_subject: gruppo3, token_clientId: clientTest3, token_username: Utente3, token_eMail: email3@prova.org";
		}
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.ORARIO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
	}
	
	
	public static void perHeader(TipoServizio tipoServizio) {
		
		LocalDateTime dataSpedizione = LocalDateTime.now();	
		
		final String erogazione = "RaggruppamentoRest";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/json");
		requestGroup1.setMethod(HttpRequestMethod.GET);
		requestGroup1.setUrl(urlServizio);
		requestGroup1.addHeader("Gruppo", "gruppo1");
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/json");
		requestGroup2.setMethod(HttpRequestMethod.GET);
		requestGroup2.setUrl(urlServizio);
		requestGroup2.addHeader("Gruppo", "gruppo2");
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/json");
		requestGroup3.setMethod(HttpRequestMethod.GET);
		requestGroup3.setUrl(urlServizio);
		requestGroup3.addHeader("Gruppo", "gruppo3");
		
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		// Dico quale policy di rate limiting deve attivarsi, perchè sono 
		// filtrate per header
		for(var r: requests) {
			r.addHeader(testIdHeader, PolicyAlias.FILTROHEADER.value);
		}
		
		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.FILTROHEADER, erogazione, requests);	
		
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.waitForDbEvents();
		List<Map<String, Object>> events = org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.getNotificheEventi(dataSpedizione);		
				
		String idServizio = "SoggettoInternoTest/RaggruppamentoRest/v1";
		if(TipoServizio.FRUIZIONE.equals(tipoServizio)) {
			idServizio = "SoggettoInternoTestFruitore/"+idServizio;
		}
		String groupBy = null;
		
		groupBy = "Chiave-Tipo: HeaderBased, Chiave-Criterio: Gruppo, Chiave-Valore: gruppo1";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROHEADER, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Chiave-Tipo: HeaderBased, Chiave-Criterio: Gruppo, Chiave-Valore: gruppo2";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROHEADER, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Chiave-Tipo: HeaderBased, Chiave-Criterio: Gruppo, Chiave-Valore: gruppo3";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROHEADER, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
	}
	
	public static void perHeaderXForwardedFor(TipoServizio tipoServizio, String headerName, List<String> test) {
		
		test.add(headerName);		
		LocalDateTime dataSpedizione = LocalDateTime.now();	
		
		final String erogazione = "RaggruppamentoRest";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/json");
		requestGroup1.setMethod(HttpRequestMethod.GET);
		requestGroup1.setUrl(urlServizio);
		requestGroup1.addHeader(headerName, "gruppo1");
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/json");
		requestGroup2.setMethod(HttpRequestMethod.GET);
		requestGroup2.setUrl(urlServizio);
		requestGroup2.addHeader(headerName, "gruppo2");
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/json");
		requestGroup3.setMethod(HttpRequestMethod.GET);
		requestGroup3.setUrl(urlServizio);
		requestGroup3.addHeader(headerName, "gruppo3");
		
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		// Dico quale policy di rate limiting deve attivarsi, perchè sono 
		// filtrate per header
		for(var r: requests) {
			r.addHeader(testIdHeader, PolicyAlias.FILTROXFORWARDEDFOR.value);
		}
		
		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.FILTROXFORWARDEDFOR, erogazione, requests);	
		
		if(test.size()<=1) {
			
			// solo una volta faccio la verifica degli eventi
			
			org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.waitForDbEvents();
			List<Map<String, Object>> events = org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.getNotificheEventi(dataSpedizione);		
					
			String idServizio = "SoggettoInternoTest/RaggruppamentoRest/v1";
			if(TipoServizio.FRUIZIONE.equals(tipoServizio)) {
				idServizio = "SoggettoInternoTestFruitore/"+idServizio;
			}
			String groupBy = null;
			
			groupBy = "Chiave-Tipo: IndirizzoIP_Forwarded, Chiave-Valore: gruppo1";
			EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROXFORWARDEDFOR, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
			
			groupBy = "Chiave-Tipo: IndirizzoIP_Forwarded, Chiave-Valore: gruppo2";
			EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROXFORWARDEDFOR, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
			
			groupBy = "Chiave-Tipo: IndirizzoIP_Forwarded, Chiave-Valore: gruppo3";
			EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROXFORWARDEDFOR, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		}
	}
	
	public static void perUrlInvocazione(TipoServizio tipoServizio) {
		
		LocalDateTime dataSpedizione = LocalDateTime.now();	
		
		final String erogazione = "RaggruppamentoRest";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/json");
		requestGroup1.setMethod(HttpRequestMethod.GET);
		requestGroup1.setUrl(urlServizio + "/minuto");		
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/json");
		requestGroup2.setMethod(HttpRequestMethod.GET);
		requestGroup2.setUrl(urlServizio + "/giornaliero");		
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/json");
		requestGroup3.setMethod(HttpRequestMethod.GET);
		requestGroup3.setUrl(urlServizio + "/orario");
		
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		// Dico quale policy di rate limiting deve attivarsi, perchè sono 
		// filtrate per header
		for(var r: requests) {
			r.addHeader(testIdHeader, PolicyAlias.FILTROURLINVOCAZIONE.value);
		}
		
		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.FILTROURLINVOCAZIONE, erogazione, requests);
		
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.waitForDbEvents();
		List<Map<String, Object>> events = org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.getNotificheEventi(dataSpedizione);		
				
		String idServizio = "SoggettoInternoTest/RaggruppamentoRest/v1";
		if(TipoServizio.FRUIZIONE.equals(tipoServizio)) {
			idServizio = "SoggettoInternoTestFruitore/"+idServizio;
		}
		String groupBy = null;
		
		groupBy = "Chiave-Tipo: URLBased, Chiave-Criterio: .*/(?:gw_)?SoggettoInternoTest/(?:gw_)?RaggruppamentoRest/v1/([^/|^?]*).*, Chiave-Valore: minuto";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROURLINVOCAZIONE, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Chiave-Tipo: URLBased, Chiave-Criterio: .*/(?:gw_)?SoggettoInternoTest/(?:gw_)?RaggruppamentoRest/v1/([^/|^?]*).*, Chiave-Valore: giornaliero";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROURLINVOCAZIONE, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Chiave-Tipo: URLBased, Chiave-Criterio: .*/(?:gw_)?SoggettoInternoTest/(?:gw_)?RaggruppamentoRest/v1/([^/|^?]*).*, Chiave-Valore: orario";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROURLINVOCAZIONE, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
	}
	
	public static void perContenuto(TipoServizio tipoServizio) {
	
		LocalDateTime dataSpedizione = LocalDateTime.now();	
		
		final String erogazione = "RaggruppamentoRest";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/json");
		requestGroup1.setMethod(HttpRequestMethod.POST);
		requestGroup1.setUrl(urlServizio);
		requestGroup1.setContent("{ \"gruppo\": \"gruppo1\" }".getBytes());
		
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/json");
		requestGroup2.setMethod(HttpRequestMethod.POST);
		requestGroup2.setUrl(urlServizio);
		requestGroup2.setContent("{ \"gruppo\": \"gruppo2\" }".getBytes());
		
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/json");
		requestGroup3.setMethod(HttpRequestMethod.POST);
		requestGroup3.setUrl(urlServizio);
		requestGroup3.setContent("{ \"gruppo\": \"gruppo3\" }".getBytes());
		
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		// Dico quale policy di rate limiting deve attivarsi, perchè sono 
		// filtrate per header
		for(var r: requests) {
			r.addHeader(testIdHeader, PolicyAlias.FILTROCONTENUTO.value);
		}
		
		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.FILTROCONTENUTO, erogazione, requests);
		
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.waitForDbEvents();
		List<Map<String, Object>> events = org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.getNotificheEventi(dataSpedizione);		
				
		String idServizio = "SoggettoInternoTest/RaggruppamentoRest/v1";
		if(TipoServizio.FRUIZIONE.equals(tipoServizio)) {
			idServizio = "SoggettoInternoTestFruitore/"+idServizio;
		}
		String groupBy = null;
		
		groupBy = "Chiave-Tipo: ContentBased, Chiave-Criterio: $.gruppo, Chiave-Valore: gruppo1";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROCONTENUTO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Chiave-Tipo: ContentBased, Chiave-Criterio: $.gruppo, Chiave-Valore: gruppo2";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROCONTENUTO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Chiave-Tipo: ContentBased, Chiave-Criterio: $.gruppo, Chiave-Valore: gruppo3";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROCONTENUTO, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
	}
	
	public static void perParametroUrl(TipoServizio tipoServizio) {
		
		LocalDateTime dataSpedizione = LocalDateTime.now();	
		
		final String erogazione = "RaggruppamentoRest";
		final String urlServizio =  tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		HttpRequest requestGroup1 = new HttpRequest();
		requestGroup1.setContentType("application/json");
		requestGroup1.setMethod(HttpRequestMethod.GET);
		requestGroup1.setUrl(urlServizio+"?gruppo=gruppo1");
		
		
		HttpRequest requestGroup2 = new HttpRequest();
		requestGroup2.setContentType("application/json");
		requestGroup2.setMethod(HttpRequestMethod.GET);
		requestGroup2.setUrl(urlServizio+"?gruppo=gruppo2");
		
		
		HttpRequest requestGroup3 = new HttpRequest();
		requestGroup3.setContentType("application/json");
		requestGroup3.setMethod(HttpRequestMethod.GET);
		requestGroup3.setUrl(urlServizio+"?gruppo=gruppo3");
		
		
		HttpRequest[] requests = {requestGroup1, requestGroup2, requestGroup3};
		
		for(var r: requests) {
			r.addHeader(testIdHeader, PolicyAlias.FILTROPARAMETROURL.value);
		}
		
		makeAndCheckGroupRequests(tipoServizio, PolicyAlias.FILTROPARAMETROURL, erogazione, requests);
		
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.waitForDbEvents();
		List<Map<String, Object>> events = org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.getNotificheEventi(dataSpedizione);		
				
		String idServizio = "SoggettoInternoTest/RaggruppamentoRest/v1";
		if(TipoServizio.FRUIZIONE.equals(tipoServizio)) {
			idServizio = "SoggettoInternoTestFruitore/"+idServizio;
		}
		String groupBy = null;
		
		groupBy = "Chiave-Tipo: FormBased, Chiave-Criterio: gruppo, Chiave-Valore: gruppo1";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROPARAMETROURL, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Chiave-Tipo: FormBased, Chiave-Criterio: gruppo, Chiave-Valore: gruppo2";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROPARAMETROURL, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
		groupBy = "Chiave-Tipo: FormBased, Chiave-Criterio: gruppo, Chiave-Valore: gruppo3";
		EventiUtils.checkEventConViolazioneRL(events, PolicyAlias.FILTROPARAMETROURL, idServizio, dataSpedizione, Optional.empty(), Optional.of(groupBy), logRateLimiting);
		
	}

}
