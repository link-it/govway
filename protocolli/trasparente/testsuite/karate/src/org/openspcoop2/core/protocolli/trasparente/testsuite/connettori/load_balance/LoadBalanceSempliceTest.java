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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balance;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;

/*
	6 Connettori per erogazione.
	ID = 0, 1, 2, 3					=> Connettori funzionanti
	ID = 'connettore-disabilitato'	=> Connettore disabilitato
	ID = 'connettore-rotto'			=> Connettore con errore di connessione
	
	Testo anche che al connettore disabilitato non venga instradato nulla, ogni politica ha un connettore disabilitato.
*/

/**
 * RestTest
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoadBalanceSempliceTest extends ConfigLoader {

	// Per adesso eseguo i test sempre con -Dload=false -Ddelete=false
	public static final String CONNETTORE_ROTTO = "connettore-rotto";
	public static final String CONNETTORE_DISABILITATO = "connettore-disabilitato";
	public static final String ID_CONNETTORE_QUERY = "id_connettore";

	@Test
	public void roundRobin() {
		/*
		 * La richiesta viene instradata ai connettori in maniera circolare, uno dopo
		 * l'altro. Faccio 15 richieste, che devono andare 4 per connettore, anche
		 * quelle con errore di connessione.
		 */

		final String erogazione = "LoadBalanceRoundRobin";
		final String path = "test";

		HttpRequest okRequest = new HttpRequest();
		okRequest.setContentType("application/json");
		okRequest.setMethod(HttpRequestMethod.GET);
		okRequest.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/" + path
				+ "?replyQueryParameter=id_connettore");

		Vector<HttpResponse> responses = Utils.makeParallelRequests(okRequest, 15);

		// Ho 5 connettori buoni, mi aspetto tre richieste per ognuno.
		Map<String, Integer> howManys = new HashMap<>();
		for (var response : responses) {
			String id_connettore;
			if (response.getResultHTTPOperation() == 200) {
				id_connettore = response.getHeaderFirstValue(ID_CONNETTORE_QUERY);
			} else {
				id_connettore = CONNETTORE_ROTTO;
			}

			howManys.put(id_connettore, howManys.getOrDefault(id_connettore, 0)+1);
		}

		for (var results : howManys.entrySet()) {
			assertEquals(Integer.valueOf(3), results.getValue());
		}

	}

	@Test
	public void weightedRoundRobin() {
		/*
		 * Ogni connettore prende una percentuale di richieste
		 */

		final String erogazione = "LoadBalanceWeightedRoundRobin";
		final String path = "test";

		HttpRequest okRequest = new HttpRequest();
		okRequest.setContentType("application/json");
		okRequest.setMethod(HttpRequestMethod.GET);
		okRequest.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/" + path
				+ "?replyQueryParameter=id_connettore");

		Vector<HttpResponse> responses = Utils.makeParallelRequests(okRequest, 15);

		// Ho 5 connettori buoni, mi aspetto tre richieste per ognuno.
		Map<String, Integer> howManys = new HashMap<>();
		for (var response : responses) {
			String id_connettore;
			if (response.getResultHTTPOperation() == 200) {
				id_connettore = response.getHeaderFirstValue(ID_CONNETTORE_QUERY);
			} else {
				id_connettore = CONNETTORE_ROTTO;
			}

			howManys.put(id_connettore, howManys.getOrDefault(id_connettore, 0)+1);
		}

		// PESI:
		// Connettore0 => 1
		// Connettore1 => 2
		// Connettore2 => 3
		// Connettore3 => 4
		// ConnettoreRotto => 6 Ma le richieste son 15 e quindi ne devono arrivare 5,
		// 	altrimenti si avrebbe un connettore mai raggiunto
		
		for (var results : howManys.entrySet()) {
			if (results.getKey().equals(CONNETTORE_ROTTO)) {
				assertEquals(Integer.valueOf(5), results.getValue());
			} else {			
				Integer expected = Integer.valueOf(results.getKey()) + 1;
				assertEquals(expected, results.getValue());
			}
		}

	}

	@Test
	public void testLeastConnections() {
		/**
		 * La richiesta viene instradata al connettore con il minimo numero di
		 * connessioni attive.
		 * 
		 * L'erogazione ha 4 connettori. Ogni connettore ha nei parametri query della
		 * url invocazione il suo identificativo in modo che il server di echo risponda
		 * al client con l'identificativo del connettore attivato.
		 * 
		 * Test 1: Test effettivo della funzionalità. Faccio quattro richieste seriali
		 * con sleep e attendo la terminazione. Mi assicuro che siano stati raggiunti
		 * tutti e 4 i connettori.
		 * 
		 * Poi variante parallela: 20 richieste parallele con sleep e mi aspetto che si
		 * comporti esattamente come un round robin: una richiesta alla volta per
		 * connettore.
		 */
		//
		// Inizio 3 richieste con una sleep sui primi 3
	}

}