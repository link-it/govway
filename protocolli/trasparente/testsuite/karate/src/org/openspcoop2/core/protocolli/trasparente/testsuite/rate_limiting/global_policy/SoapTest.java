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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.global_policy;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.SoapBodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {
	
	@Test
	public void erogazione() {
		testGlobalPolicy(TipoServizio.EROGAZIONE, RestTest.ID_POLICY_GLOBALE, RestTest.HEADER_GLOBAL_POLICY);
	}
	
	@Test
	public void fruizione() {
		testGlobalPolicy(TipoServizio.FRUIZIONE, RestTest.ID_POLICY_GLOBALE, RestTest.HEADER_GLOBAL_POLICY);
	}
	
	@Test
	public void erogazione_filtroTag() {
		testGlobalPolicy(TipoServizio.EROGAZIONE, RestTest.ID_POLICY_GLOBALE_FILTRO_TAG, RestTest.HEADER_GLOBAL_POLICY_FILTRO_TAG);
	}
	
	@Test
	public void fruizione_filtroTag() {
		testGlobalPolicy(TipoServizio.FRUIZIONE, RestTest.ID_POLICY_GLOBALE_FILTRO_TAG, RestTest.HEADER_GLOBAL_POLICY_FILTRO_TAG);
	}
	
	@BeforeClass
	public static void initTest() {
		RestTest.initTest();
	}
	
	
	static void testGlobalPolicy(TipoServizio tipoServizio, String nomePolicy, String headerName) {
		// Usiamo l'erogazione già presente per il test numero_richieste
		// visto che li ci sono policy sul numero di richieste parallele passando lo header
		// GovWay-TestSuite-RL-GlobalPolicy=Orario per attivare la policy globale
		// vedo anche che succede agli headers quando c'è una policy globale+policy locale

		final int maxRequests = 5;
		final int windowSize = Utils.getPolicyWindowSize(PolicyAlias.ORARIO);
		final String idPolicy = dbUtils.getIdGlobalPolicy(nomePolicy);
		final String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/TempoMedioRispostaSoap/v1"
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TempoMedioRispostaSoap/v1";
		
		Utils.waitForNewHour();
		Utils.resetCounters(idPolicy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/soap+xml");
		request.setContent(SoapBodies.get(PolicyAlias.RICHIESTE_SIMULTANEE).getBytes());
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(url);
		request.addHeader(headerName, RestTest.HEADER_GLOBAL_POLICY_VALUE);
		
		List<HttpResponse> responseOk = Utils.makeSequentialRequests(request, maxRequests);
		List<HttpResponse> responseBlocked = Utils.makeSequentialRequests(request, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
		// X-RateLimit-Limit=10, 10;w=3600, X-RateLimit-Remaining=9
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.SoapTest.checkOkRequests(responseOk, windowSize, maxRequests);
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.SoapTest.checkFailedRequests(responseBlocked, windowSize, maxRequests);
	}

}
