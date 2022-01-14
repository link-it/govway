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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * SessioneStickyMaxAgeTest
 * 
 * 
 * Quando un id sessione mai visto raggiunge l'erogazione, la richiesta viene assegnata al prossimo
 * connettore scelto dalla strategia di load balancing.
 * 
 * Round Robin:
 * 	Faccio un test normale e verifico il corretto funzionamento della sessione sticky + load balancing,
 *  successivamente faccio scadere la sessione sticky e invio due nuove richieste con l'id sessione impostato. 
 *  Verifico che le richieste vengano inviate ai connettori previsti.
 * 
 *  Dato il numero di di richieste che hanno raggiunto i connettori:
 *  	Se conosco il tipo di weighted round robin implementato da andrea posso conoscere il prossimo connettore.
 *  	Con il round robin non è un problema
 *  	Lo stesso con il least connections.
 *  	Non so farlo per il random e weighted random
 * 
 * @author froggo
 *
 */
public class SessioneStickyMaxAgeTest extends ConfigLoader {

	@Test
	public void maxAgeRoundRobin() {
		
		// Per i max Age una classe di test a parte, laddove posso tengo traccia dello stato dello
		// scheduler e alla richiesta successiva dopo lo scadere del maxAge devo controllare che
		// vada sul connettore atteso.
		// TODO: Per tutte le strategie di load balancing
		
		final String erogazione = "LoadBalanceSessioneStickyHeaderHttpMaxAge";
		
		List<HttpRequest> richieste = Arrays.asList(
				SessioneStickyTest.buildRequest_HeaderHttp(Common.IDSessioni.get(0), erogazione), 
				SessioneStickyTest.buildRequest_HeaderHttp(Common.IDSessioni.get(1), erogazione),
				SessioneStickyTest.buildRequest_LoadBalanced(erogazione)
			);

		Map<Integer, List<HttpResponse>> responsesByKind = SessioneStickyTest.makeRequests(richieste, Common.richiesteParallele);
		
		// Tutte le richieste con lo stesso id sessione finiscono
		// sullo stesso connettore.
		SessioneStickyTest.checkAllResponsesSameConnettore(responsesByKind.get(0));
		SessioneStickyTest.checkAllResponsesSameConnettore(responsesByKind.get(1));
		
		// Tra le richieste\risposte che sono state bilanciate vanno considerate
		// anche le prime richieste che portano con se un id sessione ancora mai visto prima.
		// Le risposte che qui vado a pescare non sono necessariamente corrispondenti a tali richieste
		// ma sono utili ai fini del conteggio.
		var balancedResponses = responsesByKind.get(2);
		balancedResponses.add(responsesByKind.get(0).get(0));
		balancedResponses.add(responsesByKind.get(1).get(0));
		
		Common.checkRoundRobin(balancedResponses, Common.setConnettoriAbilitati);
		
		org.openspcoop2.utils.Utilities.sleep(Common.maxAge);
		
		// Sono in round robin, il connettore che ha meno richieste dovrà essere il prossimo selezionato
		// dalla sessione sticky.

	}

}
