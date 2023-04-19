/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.filtri;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Properties;
import java.util.List;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Credenziali;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.CredenzialiBasic;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
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
	
	
	static final String basePath = System.getProperty("govway_base_path");
	
	
	@Test
	public void filtroSoggettoErogazione() {
		filtroRichiedente(TipoServizio.EROGAZIONE, "FiltroSoggettoRest", Credenziali.nonFiltrateSoggetto, Credenziali.filtrateSoggetto);
	}
	
	
	@Test
	public void filtroApplicativoErogazione() {
		filtroRichiedente(TipoServizio.EROGAZIONE, "FiltroApplicativoRest", Credenziali.nonFiltrateApplicativo, Credenziali.filtrateApplicativo);
	}
	
	
	@Test
	public void filtroApplicativoFruizione() {
		filtroRichiedente(TipoServizio.FRUIZIONE, "FiltroApplicativoRest", Credenziali.applicativoSITFNonFiltrato, Credenziali.applicativoSITFFiltrato);
	}
	
	/**
	 * Testiamo che la policy venga applicata solo per il soggetto indicato
	 * nel filtro. Il filtro per soggetto è applicabile solamente per le erogazioni.
	 * 
	 * La policy che andiamo a testare è NumeroRichiesteCompletateConSuccesso con
	 * finestra Oraria.
	 */
	public void filtroRichiedente(TipoServizio tipoServizio, String erogazione, CredenzialiBasic nonFiltrate, CredenzialiBasic filtrate) {
		// Sulla stessa api 
		final int maxRequests = 5;
		
		final PolicyAlias policy = PolicyAlias.ORARIO;
		final int windowSize = Utils.getPolicyWindowSize(policy);
		
		final String idPolicy = tipoServizio == TipoServizio.EROGAZIONE
				? dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy)
				: dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
				

		final String urlServizio = tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		Utils.resetCounters(idPolicy);
		Utils.waitForPolicy(policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		// Faccio N richieste che non devono essere conteggiate perchè di un richiedente non filtrato
		
		HttpRequest requestNonFiltrata = new HttpRequest();
		requestNonFiltrata.setContentType("application/json");
		requestNonFiltrata.setMethod(HttpRequestMethod.GET);
		requestNonFiltrata.setUrl(urlServizio);
		requestNonFiltrata.setUsername(nonFiltrate.username);
		requestNonFiltrata.setPassword(nonFiltrate.password);
		
		List<HttpResponse> nonFiltrateResponses = Utils.makeSequentialRequests(requestNonFiltrata, maxRequests+1);
		
		assertEquals(maxRequests+1, nonFiltrateResponses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);

		// Faccio N richieste che devono far scattare la policy perchè di un richiedente filtrato
		
		HttpRequest requestFiltrata = new HttpRequest();
		requestFiltrata.setContentType("application/json");
		requestFiltrata.setMethod(HttpRequestMethod.GET);
		requestFiltrata.setUrl(urlServizio);
		requestFiltrata.setUsername(filtrate.username);
		requestFiltrata.setPassword(filtrate.password);
		
		
		List<HttpResponse> filtrateResponsesOk = Utils.makeSequentialRequests(requestFiltrata, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);

		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkOkRequests(filtrateResponsesOk, windowSize, maxRequests);
		
		List<HttpResponse> filtrateResponsesBlocked = Utils.makeSequentialRequests(requestFiltrata, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkFailedRequests(filtrateResponsesBlocked, windowSize, maxRequests);
		
		// Faccio N richieste che non devono essere filtrate e devono passare
		
		nonFiltrateResponses = Utils.makeSequentialRequests(requestNonFiltrata, maxRequests+1);
			
		assertEquals(maxRequests+1, nonFiltrateResponses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
			
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
		
	}
	
	
	@Test
	public void filtroApplicativoTokenErogazione() throws Exception {
		filtroRichiedenteToken(TipoServizio.EROGAZIONE, "FiltroApplicativoTokenRest", "clientIdApplicativoToken2SoggettoInternoFruitore", "clientIdApplicativoToken1SoggettoInternoFruitore");
	}
	
	
	@Test
	public void filtroApplicativoTokenFruizione() throws Exception {
		filtroRichiedenteToken(TipoServizio.FRUIZIONE, "FiltroApplicativoTokenRest", "clientIdApplicativoToken2SoggettoInternoFruitore", "clientIdApplicativoToken1SoggettoInternoFruitore");
	}
	
	@Test
	public void filtroApplicativoTokenErogazione_testIdNonRegistrato() throws Exception {
		filtroRichiedenteToken(TipoServizio.EROGAZIONE, "FiltroApplicativoTokenRest", "clientIdNonRegistrato", "clientIdApplicativoToken1SoggettoInternoFruitore");
	}
	
	
	@Test
	public void filtroApplicativoTokenFruizione_testIdNonRegistrato() throws Exception {
		filtroRichiedenteToken(TipoServizio.FRUIZIONE, "FiltroApplicativoTokenRest", "clientIdNonRegistrato", "clientIdApplicativoToken1SoggettoInternoFruitore");
	}
	
	/**
	 * Testiamo che la policy venga applicata solo per il soggetto indicato
	 * nel filtro. Il filtro per soggetto è applicabile solamente per le erogazioni.
	 * 
	 * La policy che andiamo a testare è NumeroRichiesteCompletateConSuccesso con
	 * finestra Oraria.
	 * @throws Exception 
	 */
	public void filtroRichiedenteToken(TipoServizio tipoServizio, String erogazione, String clientIdNonFiltrato, String clientIdFiltrato) throws Exception {
		// Sulla stessa api 
		final int maxRequests = 5;
		
		final PolicyAlias policy = PolicyAlias.ORARIO;
		final int windowSize = Utils.getPolicyWindowSize(policy);
		
		final String idPolicy = tipoServizio == TipoServizio.EROGAZIONE
				? dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy)
				: dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
				

		final String urlServizio = tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		Utils.resetCounters(idPolicy);
		Utils.waitForPolicy(policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		// Faccio N richieste che non devono essere conteggiate perchè di un richiedente non filtrato
		
		String tokenNonFiltrato = org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.buildJWT(clientIdNonFiltrato, null, false);
		
		HttpRequest requestNonFiltrata = new HttpRequest();
		requestNonFiltrata.setContentType("application/json");
		requestNonFiltrata.setMethod(HttpRequestMethod.GET);
		requestNonFiltrata.setUrl(urlServizio);
		requestNonFiltrata.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+tokenNonFiltrato);
		
		List<HttpResponse> nonFiltrateResponses = Utils.makeSequentialRequests(requestNonFiltrata, maxRequests+1);
		
		assertEquals(maxRequests+1, nonFiltrateResponses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);

		// Faccio N richieste che devono far scattare la policy perchè di un richiedente filtrato
		
		String tokenFiltrato = org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.buildJWT(clientIdFiltrato, null, false);
				
		HttpRequest requestFiltrata = new HttpRequest();
		requestFiltrata.setContentType("application/json");
		requestFiltrata.setMethod(HttpRequestMethod.GET);
		requestFiltrata.setUrl(urlServizio);
		requestFiltrata.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+tokenFiltrato);
		
		
		List<HttpResponse> filtrateResponsesOk = Utils.makeSequentialRequests(requestFiltrata, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);

		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkOkRequests(filtrateResponsesOk, windowSize, maxRequests);
		
		List<HttpResponse> filtrateResponsesBlocked = Utils.makeSequentialRequests(requestFiltrata, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkFailedRequests(filtrateResponsesBlocked, windowSize, maxRequests);
		
		// Faccio N richieste che non devono essere filtrate e devono passare
		
		nonFiltrateResponses = Utils.makeSequentialRequests(requestNonFiltrata, maxRequests+1);
			
		assertEquals(maxRequests+1, nonFiltrateResponses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
			
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
		
	}
	
	
	/*
	 * Analogo al test filtroRichiedente ma filtriamo per ruoli, 
	 */
	@Test
	public void filtroRuoloRichiedenteErogazione() {
		
		String erogazione  = "FiltroRuoloRest";
		// Sulla stessa api 
		final int maxRequests = 5;
		
		final PolicyAlias policy = PolicyAlias.ORARIO;
		final int windowSize = Utils.getPolicyWindowSize(policy);
		
		final String idPolicy =  dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy);

		final String urlServizio = basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		Utils.resetCounters(idPolicy);
		Utils.waitForPolicy(policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		// Faccio una richiesta con soggetto con ruolo che deve essere filtrato
		
		HttpRequest requestFiltrata = new HttpRequest();
		requestFiltrata.setContentType("application/json");
		requestFiltrata.setMethod(HttpRequestMethod.GET);
		requestFiltrata.setUrl(urlServizio);
		requestFiltrata.setUsername(Credenziali.soggettoRuoloFiltrato.username);
		requestFiltrata.setPassword(Credenziali.soggettoRuoloFiltrato.password);
		
		List<HttpResponse> filtrateResponsesOk = Utils.makeSequentialRequests(requestFiltrata, 1);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 1, 0);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkOkRequests(filtrateResponsesOk, windowSize, maxRequests);
		
		// Faccio N richieste con soggetto senza ruolo che non deve essere contato
		
		HttpRequest requestNonFiltrata = new HttpRequest();
		requestNonFiltrata.setContentType("application/json");
		requestNonFiltrata.setMethod(HttpRequestMethod.GET);
		requestNonFiltrata.setUrl(urlServizio);
		requestNonFiltrata.setUsername(Credenziali.nonFiltrateSoggetto.username);
		requestNonFiltrata.setPassword(Credenziali.nonFiltrateSoggetto.password);
		
		List<HttpResponse> nonFiltrateResponses = Utils.makeSequentialRequests(requestNonFiltrata, maxRequests+1);
		
		assertEquals(maxRequests+1, nonFiltrateResponses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 1, 0);

		
		// Seconda richiesta con altro soggetto con ruolo che deve essere filtrato
		
		requestFiltrata.setUsername(Credenziali.soggettoRuoloFiltrato2.username);
		requestFiltrata.setPassword(Credenziali.soggettoRuoloFiltrato2.password);
		
		filtrateResponsesOk = Utils.makeSequentialRequests(requestFiltrata, 1);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 2, 0);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkOkRequests(filtrateResponsesOk, windowSize, maxRequests);
		
		
		// Altre N richieste con applicativo senza ruolo che non deve essere contato
		
		requestNonFiltrata.setUsername(Credenziali.nonFiltrateApplicativo.username);
		requestNonFiltrata.setPassword(Credenziali.nonFiltrateApplicativo.password);
		
		nonFiltrateResponses = Utils.makeSequentialRequests(requestNonFiltrata, maxRequests+1);
		
		assertEquals(maxRequests+1, nonFiltrateResponses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 2, 0);

	
		// Terza richiesta con applicativo con ruolo che deve essere filtrato
		
		requestFiltrata.setUsername(Credenziali.applicativoRuoloFiltrato.username);
		requestFiltrata.setPassword(Credenziali.applicativoRuoloFiltrato.password);

		filtrateResponsesOk = Utils.makeSequentialRequests(requestFiltrata, 1);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 3, 0);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkOkRequests(filtrateResponsesOk, windowSize, maxRequests);
		// Restanti richieste con altro applicativo con ruolo che deve essere filtrato
		
		requestFiltrata.setUsername(Credenziali.applicativoRuoloFiltrato2.username);
		requestFiltrata.setPassword(Credenziali.applicativoRuoloFiltrato2.password);
		

		filtrateResponsesOk = Utils.makeSequentialRequests(requestFiltrata, 2);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkOkRequests(filtrateResponsesOk, windowSize, maxRequests);
		
		// Faccio altre n richieste che devono essere tutte bloccate. 
		
		List<HttpResponse> filtrateResponsesBlocked = Utils.makeSequentialRequests(requestFiltrata, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkFailedRequests(filtrateResponsesBlocked, windowSize, maxRequests);
		
		// Faccio altre n richieste che non devono essere conteggiate
		
		requestNonFiltrata.setUsername(Credenziali.nonFiltrateSoggetto.username);
		requestNonFiltrata.setPassword(Credenziali.nonFiltrateSoggetto.password);
		
		nonFiltrateResponses = Utils.makeSequentialRequests(requestNonFiltrata, maxRequests);
		
		assertEquals(maxRequests, nonFiltrateResponses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
		
		
		// Faccio altre n richieste che non devono essere conteggiate (Usa un applicativo che ha un ruolo non filtrato)
		
		requestNonFiltrata.setUsername(Credenziali.nonFiltrateApplicativo.username);
		requestNonFiltrata.setPassword(Credenziali.nonFiltrateApplicativo.password);
		
		nonFiltrateResponses = Utils.makeSequentialRequests(requestNonFiltrata, maxRequests);
		
		assertEquals(maxRequests, nonFiltrateResponses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
	}
	
	
	/*
	 * Analogo al test filtroRichiedente ma filtriamo per ruoli, 
	 */
	@Test
	public void filtroRuoloRichiedenteFruizione() {
		
		String erogazione  = "FiltroRuoloRest";
		// Sulla stessa api 
		final int maxRequests = 5;
		
		final PolicyAlias policy = PolicyAlias.ORARIO;
		final int windowSize = Utils.getPolicyWindowSize(policy);
		
		final String idPolicy =  dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);

		final String urlServizio = basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		Utils.resetCounters(idPolicy);
		Utils.waitForPolicy(policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		// Faccio maxRequests con applicativo con ruolo filtrato che deve essere conteggiata
		
		HttpRequest requestFiltrata = new HttpRequest();
		requestFiltrata.setContentType("application/json");
		requestFiltrata.setMethod(HttpRequestMethod.GET);
		requestFiltrata.setUrl(urlServizio);
		requestFiltrata.setUsername(Credenziali.applicativoSITFRuoloFiltrato.username);
		requestFiltrata.setPassword(Credenziali.applicativoSITFRuoloFiltrato.password);

		List<HttpResponse> filtrateResponsesOk = Utils.makeSequentialRequests(requestFiltrata, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkOkRequests(filtrateResponsesOk, windowSize, maxRequests);
		
		// Faccio N richieste con applicativo con ruolo non filtrato che non deve essere conteggiata
		
		HttpRequest requestNonFiltrata = new HttpRequest();
		requestNonFiltrata.setContentType("application/json");
		requestNonFiltrata.setMethod(HttpRequestMethod.GET);
		requestNonFiltrata.setUrl(urlServizio);
		requestNonFiltrata.setUsername(Credenziali.applicativoSITFRuoloNonFiltrato.username);
		requestNonFiltrata.setPassword(Credenziali.applicativoSITFRuoloNonFiltrato.password);
		
		List<HttpResponse> nonFiltrateResponses = Utils.makeSequentialRequests(requestNonFiltrata, maxRequests+1);
		
		assertEquals(maxRequests+1, nonFiltrateResponses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		
		
		// Faccio altre n richieste che devono essere tutte bloccate.
		
		List<HttpResponse> filtrateResponsesBlocked = Utils.makeSequentialRequests(requestFiltrata, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkFailedRequests(filtrateResponsesBlocked, windowSize, maxRequests);
		
		// Faccio altre n richieste che non devono essere conteggiate
		
		nonFiltrateResponses = Utils.makeSequentialRequests(requestNonFiltrata, maxRequests);
		
		assertEquals(maxRequests, nonFiltrateResponses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
	}
	
	
	/*
	 * Analogo al test filtroRichiedente ma filtriamo per ruoli, 
	 */
	@Test
	public void filtroRuoloRichiedenteTokenErogazione() throws Exception {
		filtroRichiedenteToken(TipoServizio.EROGAZIONE, "FiltroRuoloTokenRest", "clientIdApplicativoToken1SoggettoInternoFruitore", "clientIdApplicativoToken2SoggettoInternoFruitore");
	}
	
	@Test
	public void filtroRuoloRichiedenteTokenFruizione() throws Exception {
		filtroRichiedenteToken(TipoServizio.FRUIZIONE, "FiltroRuoloTokenRest", "clientIdApplicativoToken1SoggettoInternoFruitore", "clientIdApplicativoToken2SoggettoInternoFruitore");
	}
	
	@Test
	public void filtroRuoloRichiedenteTokenErogazione_testIdNonRegistrato() throws Exception {
		filtroRichiedenteToken(TipoServizio.EROGAZIONE, "FiltroRuoloTokenRest", "clientIdNonRegistrato", "clientIdApplicativoToken2SoggettoInternoFruitore");
	}
	
	
	@Test
	public void filtroRuoloRichiedenteTokenFruizione_testIdNonRegistrato() throws Exception {
		filtroRichiedenteToken(TipoServizio.FRUIZIONE, "FiltroRuoloTokenRest", "clientIdNonRegistrato", "clientIdApplicativoToken2SoggettoInternoFruitore");
	}
	
	
	@Test
	public void filtroHeaderErogazione() {
		filtroHeader("X-Test-Filtro-Chiave", "filtrami", TipoServizio.EROGAZIONE, PolicyAlias.FILTROHEADER);
	}

	@Test
	public void filtroHeaderFruizione() {
		filtroHeader("X-Test-Filtro-Chiave", "filtrami", TipoServizio.FRUIZIONE, PolicyAlias.FILTROHEADER);
	}
	
	@Test
	public void filtroParametroUrlErogazione() {
		filtroParametroUrl(TipoServizio.EROGAZIONE);
	}
	
	@Test
	public void filtroParametroUrlFruizione() {
		filtroParametroUrl(TipoServizio.FRUIZIONE);
	}
	
	@Test
	public void filtroXForwardedForErogazione() throws UtilsException {
		HttpUtilities.getClientAddressHeaders().forEach( headerName ->
				filtroHeader(headerName, "filtrami", TipoServizio.EROGAZIONE, PolicyAlias.FILTROXFORWARDEDFOR)
			);
	}
	
	@Test
	public void filtroXForwardedForFruizione() throws UtilsException  {
		HttpUtilities.getClientAddressHeaders().forEach( headerName ->
				filtroHeader(headerName, "filtrami", TipoServizio.FRUIZIONE, PolicyAlias.FILTROXFORWARDEDFOR)
			);
	}

	@Test
	public void filtroContenutoErogazione() {
		filtroContenuto(TipoServizio.EROGAZIONE);
	}
	
	@Test
	public void filtroContenutoFruizione() {
		filtroContenuto(TipoServizio.FRUIZIONE);
	}
	
	@Test
	public void filtroUrlInvocazioneErogazione() {
		filtroUrlInvocazione(TipoServizio.EROGAZIONE);
	}

	@Test
	public void filtroUrlInvocazioneFruizione() {
		filtroUrlInvocazione(TipoServizio.FRUIZIONE);
	}
	
	public void filtroUrlInvocazione(TipoServizio tipoServizio) {
		
		final String erogazione = "FiltroChiaveRest";
		final PolicyAlias policy = PolicyAlias.FILTROURLINVOCAZIONE;

		final String urlServizioFiltered = tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/no-policy"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/no-policy";
		
		final String urlServizioNotFiltered = tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/minuto"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/minuto";
		
		HttpRequest requestFiltrata = new HttpRequest();
		requestFiltrata.setContentType("application/json");
		requestFiltrata.setMethod(HttpRequestMethod.GET);
		requestFiltrata.setUrl(urlServizioFiltered);
		
		
		HttpRequest requestNonFiltrata = new HttpRequest();
		requestNonFiltrata.setContentType("application/json");
		requestNonFiltrata.setMethod(HttpRequestMethod.GET);
		requestNonFiltrata.setUrl(urlServizioNotFiltered);
		
		
		makeRequestsAndCheck(tipoServizio, policy, requestFiltrata, requestNonFiltrata);
	}
	

	public void filtroParametroUrl(TipoServizio tipoServizio) {
		final String param = "x-test-filtro-chiave";
		final String paramValue = "filtrami";
		
		final String erogazione = "FiltroChiaveRest";
		final PolicyAlias policy = PolicyAlias.FILTROPARAMETROURL;

		final String urlServizio = tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		HttpRequest requestFiltrata = new HttpRequest();
		requestFiltrata.setContentType("application/json");
		requestFiltrata.setMethod(HttpRequestMethod.GET);
		requestFiltrata.setUrl(urlServizio+"?"+param+"="+paramValue);
		
		
		HttpRequest requestNonFiltrata = new HttpRequest();
		requestNonFiltrata.setContentType("application/json");
		requestNonFiltrata.setMethod(HttpRequestMethod.GET);
		requestNonFiltrata.setUrl(urlServizio+"?"+param+"=nonFiltrarmi");
		
		
		makeRequestsAndCheck(tipoServizio, policy, requestFiltrata, requestNonFiltrata);
	}
	
	
	public void filtroHeader(String header, String valueToFilter, TipoServizio tipoServizio, PolicyAlias policy) {
		
		final String valueToNotFilter = valueToFilter + "puppa";
		final String erogazione = "FiltroChiaveRest";

		final String urlServizio = tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		HttpRequest requestFiltrata = new HttpRequest();
		requestFiltrata.setContentType("application/json");
		requestFiltrata.setMethod(HttpRequestMethod.GET);
		requestFiltrata.addHeader(header, valueToFilter);
		requestFiltrata.setUrl(urlServizio);
		
		
		HttpRequest requestNonFiltrata = new HttpRequest();
		requestNonFiltrata.setContentType("application/json");
		requestNonFiltrata.setMethod(HttpRequestMethod.GET);
		requestNonFiltrata.addHeader(header, valueToNotFilter);
		requestNonFiltrata.setUrl(urlServizio);
		
		
		makeRequestsAndCheck(tipoServizio, policy, requestFiltrata, requestNonFiltrata);
	}
	

	public void filtroContenuto(TipoServizio tipoServizio) {
		final String bodyToFilter = "{ \"testFiltroContenuto\": \"filtrami\" }";
		final String bodyToNotFilter = "{ \"testFiltroContenuto\": \"non-filtrarmi\" }";
		
		
		final String erogazione = "FiltroChiaveRest";
		final PolicyAlias policy = PolicyAlias.FILTROCONTENUTO;
		final String urlServizio = tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		HttpRequest requestFiltrata = new HttpRequest();
		requestFiltrata.setContentType("application/json");
		requestFiltrata.setMethod(HttpRequestMethod.POST);
		requestFiltrata.setContent(bodyToFilter.getBytes());
		requestFiltrata.setUrl(urlServizio);
		
		
		HttpRequest requestNonFiltrata = new HttpRequest();
		requestNonFiltrata.setContentType("application/json");
		requestNonFiltrata.setMethod(HttpRequestMethod.POST);
		requestNonFiltrata.setContent(bodyToNotFilter.getBytes());
		requestNonFiltrata.setUrl(urlServizio);
		
		
		makeRequestsAndCheck(tipoServizio, policy, requestFiltrata, requestNonFiltrata);
	}



	private void makeRequestsAndCheck(TipoServizio tipoServizio, PolicyAlias policy, HttpRequest requestFiltrata, HttpRequest requestNonFiltrata) {

		final int maxRequests = 5;
		final int windowSize = Utils.getPolicyWindowSize(policy);
		final String erogazione = "FiltroChiaveRest";


		final String idPolicy = tipoServizio == TipoServizio.EROGAZIONE
				? dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy)
				: dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
				
		Utils.resetCounters(idPolicy);
		Utils.waitForPolicy(policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		
		List<HttpResponse> nonFiltrateResponses = Utils.makeSequentialRequests(requestNonFiltrata, maxRequests);
		assertEquals(maxRequests, nonFiltrateResponses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());

		List<HttpResponse> filtrateResponsesOk = Utils.makeParallelRequests(requestFiltrata, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest
				.checkOkRequests(filtrateResponsesOk, windowSize, maxRequests);

		List<HttpResponse> filtrateResponsesBlocked = Utils.makeSequentialRequests(requestFiltrata, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest
				.checkFailedRequests(filtrateResponsesBlocked, windowSize, maxRequests);

		// Faccio altre n richieste che non devono essere conteggiate

		nonFiltrateResponses = Utils.makeSequentialRequests(requestNonFiltrata, maxRequests);

		assertEquals(maxRequests, nonFiltrateResponses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());

		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
	}
	
	
	
	

	
	
	@Test
	public void filtroTokenClaimsErogazione() throws Exception {
		filtroTokenClaims(TipoServizio.EROGAZIONE, "FiltroTokenClaims","SUB2", "SUB1");
	}
	
	@Test
	public void filtroTokenClaimsFruizione() throws Exception {
		filtroTokenClaims(TipoServizio.FRUIZIONE, "FiltroTokenClaims","SUB2", "SUB1");
	}
	
	/**
	 * Testiamo che la policy venga applicata solo per il soggetto indicato
	 * nel filtro. Il filtro per soggetto è applicabile solamente per le erogazioni.
	 * 
	 * La policy che andiamo a testare è NumeroRichiesteCompletateConSuccesso con
	 * finestra Oraria.
	 */
	public void filtroTokenClaims(TipoServizio tipoServizio, String erogazione, String subNonFiltrato, String subFiltrato) throws Exception {
		// Sulla stessa api 
		final int maxRequests = 5;

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
		
		String nonFiltrato = "{\n"+
		  "\"sub\": \""+subNonFiltrato+"\",\n"+
		  "\"iss\": \"ISS\",\n"+
		  "\"preferred_username\": \"John Doe\",\n"+
		  "\"azp\": \"clientTest\",\n"+
		  "\"iat\": "+(iat.getTime()/1000)+"\n"+
		"}";
		String tokenSigned_nonFiltrato = jsonSignature.sign(nonFiltrato);
		
		String filtrato = "{\n"+
		  "\"sub\": \""+subFiltrato+"\",\n"+
		  "\"iss\": \"ISS\",\n"+
		  "\"preferred_username\": \"John Doe\",\n"+
		  "\"azp\": \"clientTest\",\n"+
		  "\"iat\": "+(iat.getTime()/1000)+"\n"+
		"}";
		String tokenSigned_filtrato = jsonSignature.sign(filtrato);
		
		
		final PolicyAlias policy = PolicyAlias.ORARIO;
		final int windowSize = Utils.getPolicyWindowSize(policy);
		
		final String idPolicy = tipoServizio == TipoServizio.EROGAZIONE
				? dbUtils.getIdPolicyErogazione("SoggettoInternoTest", erogazione, policy)
				: dbUtils.getIdPolicyFruizione("SoggettoInternoTestFruitore", "SoggettoInternoTest", erogazione, policy);
				

		final String urlServizio = tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1/orario"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1/orario";
		
		Utils.resetCounters(idPolicy);
		Utils.waitForPolicy(policy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
		
		// Faccio N richieste che non devono essere conteggiate perchè di un richiedente non filtrato
		
		HttpRequest requestNonFiltrata = new HttpRequest();
		requestNonFiltrata.setContentType("application/json");
		requestNonFiltrata.setMethod(HttpRequestMethod.GET);
		requestNonFiltrata.setUrl(urlServizio);
		requestNonFiltrata.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+tokenSigned_nonFiltrato);
		
		List<HttpResponse> nonFiltrateResponses = Utils.makeSequentialRequests(requestNonFiltrata, maxRequests+1);
		
		assertEquals(maxRequests+1, nonFiltrateResponses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);

		// Faccio N richieste che devono far scattare la policy perchè di un richiedente filtrato
		
		HttpRequest requestFiltrata = new HttpRequest();
		requestFiltrata.setContentType("application/json");
		requestFiltrata.setMethod(HttpRequestMethod.GET);
		requestFiltrata.setUrl(urlServizio);
		requestFiltrata.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+tokenSigned_filtrato);
		
		
		List<HttpResponse> filtrateResponsesOk = Utils.makeSequentialRequests(requestFiltrata, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, 0);

		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkOkRequests(filtrateResponsesOk, windowSize, maxRequests);
		
		List<HttpResponse> filtrateResponsesBlocked = Utils.makeSequentialRequests(requestFiltrata, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkFailedRequests(filtrateResponsesBlocked, windowSize, maxRequests);
		
		// Faccio N richieste che non devono essere filtrate e devono passare
		
		nonFiltrateResponses = Utils.makeSequentialRequests(requestNonFiltrata, maxRequests+1);
			
		assertEquals(maxRequests+1, nonFiltrateResponses.stream().filter(r -> r.getResultHTTPOperation() == 200).count());
			
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
		
	}
	

}
