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
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
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
	public void headerHttpRoundRobin() {
		
		// Per i max Age una classe di test a parte, laddove posso tengo traccia dello stato dello
		// scheduler e alla richiesta successiva dopo lo scadere del maxAge devo controllare che
		// vada sul connettore atteso.
		
		final String erogazione = "LoadBalanceSessioneStickyHeaderHttpMaxAge";
		
		HttpRequest request1 = SessioneStickyTest.buildRequest_HeaderHttp(Common.IDSessioni.get(0), erogazione);
		HttpRequest request2 = SessioneStickyTest.buildRequest_HeaderHttp(Common.IDSessioni.get(1), erogazione);
		
		HttpResponse response1 = Utils.makeRequest(request1);
		HttpResponse response2 = Utils.makeRequest(request2);
		
		String connettore1 = response1.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
		String connettore2 = response2.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
		
		var responsesSameConnettore = Utils.makeParallelRequests(request1, 10);
		Common.checkAll200(responsesSameConnettore);
		Common.checkAllResponsesSameConnettore(responsesSameConnettore);
		
		responsesSameConnettore = Utils.makeParallelRequests(request2, 10);
		Common.checkAll200(responsesSameConnettore);
		Common.checkAllResponsesSameConnettore(responsesSameConnettore);
		
		// Faccio le richieste rimanenti per completare il giro del round robin
		int richiesteRimanenti = Common.setConnettoriAbilitati.size() - 2;
		HttpRequest balancedRequest = Common.buildRequest_Semplice(erogazione);
		var balancedResponses = Utils.makeParallelRequests(balancedRequest, richiesteRimanenti);
		Common.checkAll200(balancedResponses);
		
		// Adesso attendo il max Age e faccio scadere la sessione
		org.openspcoop2.utils.Utilities.sleep(Common.maxAge);
		
		// Faccio due richieste per saltare i due connettori iniziali
		var responses = Utils.makeSequentialRequests(balancedRequest, 2);
		assertEquals(connettore1, responses.get(0).getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		assertEquals(connettore2, responses.get(1).getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		HttpResponse responseNuova1 = Utils.makeRequest(request1);
		HttpResponse responseNuova2 = Utils.makeRequest(request2);
		
		String connettoreNuovo1 = responseNuova1.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
		String connettoreNuovo2 = responseNuova2.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE);
		
		assertNotEquals(connettore1, connettoreNuovo1);
		assertNotEquals(connettore1, connettoreNuovo2);
		assertNotEquals(connettore2, connettoreNuovo1);
		assertNotEquals(connettore2, connettoreNuovo2);
		assertNotEquals(connettoreNuovo1, connettoreNuovo2);
		
	}
	
	@Test
	public void urlInvocazioneWeightedRoundRobin() {
		
		final String erogazione = "LoadBalanceSessioneStickyWeightedRoundRobinMaxAge";
		
		/** 
		 * La specifica non indica tipo di politica Weight random, se interleaved o classica, per cui
		 * non sono certo nel prevedere quale connettore verrà raggiunto. 
		 * Faccio un test classico assicurandomi che il load balancing e la sessione sticky funzionino.
		 * 
		 */
		SessioneStickyTest.urlInvocazione_Impl(erogazione);
		
		// Adesso attendo il max Age e faccio scadere la sessione
		org.openspcoop2.utils.Utilities.sleep(Common.maxAge);
		
		SessioneStickyTest.urlInvocazione_Impl(erogazione);
	}
	
	@Test
	public void parametroUrlRandom()
	{
		final String erogazione = "LoadBalanceSessioneStickyRandomMaxAge";
		
		/**
		 * Con la strategia random non sono in grado di determinare su quale
		 * connettore va la prossima richiesta, per cui mi limito a ripetere il test.
		 */
		SessioneStickyTest.parametroUrl_Impl(erogazione);
		
		// Adesso attendo il max Age e faccio scadere la sessione
		org.openspcoop2.utils.Utilities.sleep(Common.maxAge);
		
		SessioneStickyTest.parametroUrl_Impl(erogazione);
	}
	
	
	@Test
	public void freemarkerTemplateWeightedRandom() {
		final String erogazione = "LoadBalanceSessioneStickyWeightedRandomMaxAge";
		
		SessioneStickyTest.freemarkerTemplate_Impl(erogazione);

		// Adesso attendo il max Age e faccio scadere la sessione
		org.openspcoop2.utils.Utilities.sleep(Common.maxAge);
		
		SessioneStickyTest.freemarkerTemplate_Impl(erogazione);
	}
	
	
	@Test
	public void velocityTemplateLeastConnections() {
		
		final String erogazione = "LoadBalanceSessioneStickyLeastConnectionsMaxAge";

		// Ne tengo occupati 
		
	}
	

}
