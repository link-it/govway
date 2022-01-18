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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.ID_CONNETTORE_REPLY_PREFIX;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * 
 * @author froggo
 *
 */
public class LoadBalanceHealthCheckTest extends ConfigLoader {

	// Anche se con un solo test, questa classe è fatta appositamente per segnalare la necessità 
	// di altri test sull'health check tirando su un server mock.
	
	@Test
	public void healthCheck() {
		
		final String erogazione = "LoadBalanceHealthCheckRoundRobin";
		
		HttpRequest request = new HttpRequest();
		request.setContentType("application/json");
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+ID_CONNETTORE_REPLY_PREFIX);
		
		// Faccio prima 5 richieste in modo da generare un errore di connessione,
		// e tutti gli altri connettori vengono raggiunti almeno una volta
		var responsesWithError = Common.makeParallelRequests(request, 5);
		
		Set<String> connettoriNonRaggiunti = new HashSet<>(Common.setConnettoriAbilitati);
		int connectionErrors=0;
		for(var response : responsesWithError) {
			switch (response.getResultHTTPOperation()) {
			case 200:
				connettoriNonRaggiunti.remove(response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
				break;
			case 503:
				connectionErrors++;
				break;
			default:
				// Status code non atteso
				assertFalse(true);
			}
		}
		assertEquals(1,connectionErrors);
		assertTrue(connettoriNonRaggiunti.isEmpty());

		// Adesso tutte le risposte vanno come un round robin normale
		var responses = Common.makeParallelRequests(request, 15);
		
		Common.checkRoundRobin(responses, Common.setConnettoriAbilitati);
		
		// attendo il passive health check
		org.openspcoop2.utils.Utilities.sleep(Common.intervalloEsclusione + 100);
		
		// 5 richieste, una deve fallire, come sopra
		responsesWithError = Common.makeParallelRequests(request, 5);
		
		connettoriNonRaggiunti = new HashSet<>(Common.setConnettoriAbilitati);
		connectionErrors=0;
		for(var response : responsesWithError) {
			switch (response.getResultHTTPOperation()) {
			case 200:
				connettoriNonRaggiunti.remove(response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
				break;
			case 503:
				connectionErrors++;
				break;
			default:
				// Status code non atteso
				assertFalse(true);
			}
		}
		assertEquals(1,connectionErrors);
		assertTrue(connettoriNonRaggiunti.isEmpty());
	}
	

}
