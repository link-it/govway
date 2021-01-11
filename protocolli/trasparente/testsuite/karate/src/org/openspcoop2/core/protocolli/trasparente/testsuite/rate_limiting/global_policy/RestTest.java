/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;

public class RestTest extends ConfigLoader {
	
	@BeforeClass
	public static void initTest() {
		String idPolicy = null;

		try {
			idPolicy = dbUtils.getIdGlobalPolicy("Orario");
		} catch (Exception e) {
		}
		
		if (idPolicy == null) {
			createGlobalPolicy();
			idPolicy = dbUtils.getIdGlobalPolicy("Orario");
		}
		
		if (idPolicy == null) {
			throw new RuntimeException("Fallito il recupero della policy globale appena creata.");
		}
	}
	
	@Test
	public void erogazione() {
		testGlobalPolicy(TipoServizio.EROGAZIONE);
	}
	
	@Test
	public void fruizione() {
		testGlobalPolicy(TipoServizio.FRUIZIONE);
	}
	
	static void testGlobalPolicy(TipoServizio tipoServizio) {
		// Usiamo l'erogazione già presente per il test numero_richieste
		// visto che li ci sono policy sul numero di richieste parallele passando lo header
		// GovWay-TestSuite-RL-GlobalPolicy=Orario per attivare la policy globale
		// vedo anche che succede agli headers quando c'è una policy globale+policy locale

		final int maxRequests = 5;
		final int windowSize = Utils.getPolicyWindowSize(PolicyAlias.ORARIO);
		final String idPolicy = dbUtils.getIdGlobalPolicy("Orario");
		final String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/NumeroRichiesteRest/v1/richieste-simultanee"
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/NumeroRichiesteRest/v1/richieste-simultanee";
		
		Utils.waitForNewHour();
		Utils.resetCounters(idPolicy);
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, 0, 0);
				
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(url);
		request.addHeader("GovWay-TestSuite-RL-GlobalPolicy", "Orario");
		
		Vector<HttpResponse> responseOk = Utils.makeSequentialRequests(request, maxRequests);
		Vector<HttpResponse> responseBlocked = Utils.makeSequentialRequests(request, maxRequests);
		
		Utils.checkConditionsNumeroRichieste(idPolicy, 0, maxRequests, maxRequests);
		// TODO: Gli header da controllare sono diversi.
		// X-RateLimit-Limit=10, 10;w=3600, X-RateLimit-Remaining=9
		// Sono solo questi due. Siamo sicuri? Chiedi ad andrea.
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkOkRequests(responseOk, windowSize, maxRequests);
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_completate_con_successo.RestTest.checkFailedRequests(responseBlocked, windowSize, maxRequests);
	}
	
	
	static void createGlobalPolicy() {
		String policy_id = "__TestsuiteGlobaleTestOrarioConFiltroPerChiave__";
		String policy_alias = "Orario";
		
		Boolean policy_continue = false;
		Boolean policy_enabled = true;
		Boolean policy_redefined = true;
		Boolean filtro_enabled = true;
		Boolean filtro_key_enabled = true;

		String query = "INSERT INTO ct_active_policy "
				+ "("
				+ 		"active_policy_id,"
				+ 		"policy_update_time,"
				+ 		"policy_alias,"
				+ 		"policy_posizione,"
				+ 		"policy_continue,"
				+ 		"policy_id,"
				+ 		"policy_enabled,"
				+ 		"policy_redefined,"
				+ 		"policy_valore,"
				+ 		"filtro_enabled,"
				+ 		"filtro_protocollo,"
				+ 		"filtro_ruolo,"
				+		"filtro_key_enabled,"
				+ 		"filtro_key_type,"
				+ 		"filtro_key_name,"
				+ 		"filtro_key_value"
				+ ")"
				+ " VALUES ("
				+ 		"'"+policy_id+"',"
				+ 		"CURRENT_TIMESTAMP,"
				+ 		"'"+policy_alias+"',"
				+ 		"1,"
				+ 		"?,"	// policy_continue
				+ 		"'_built-in_NumeroRichiesteCompletateConSuccesso-ControlloRealtimeOrario',"
				+ 		"?,"	// policy_enabled
				+ 		"?,"	// policy_redefined
				+ 		"5,"
				+ 		"?,"	// filtro_enabled
				+ 		"'trasparente',"
				+ 		"'entrambi',"
				+		"?,"	// filtro_key_enabled
				+ 		"'HeaderBased', "
				+ 		"'GovWay-TestSuite-RL-GlobalPolicy', "
				+ 		"'Orario'"
				+ 	")";
		
		
		logRateLimiting.info(query);
		dbUtils.jdbc.update(query,policy_continue, policy_enabled, policy_redefined, filtro_enabled, filtro_key_enabled);
	}

}
