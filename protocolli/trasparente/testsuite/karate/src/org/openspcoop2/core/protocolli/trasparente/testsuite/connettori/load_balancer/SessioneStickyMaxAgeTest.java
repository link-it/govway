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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.HEADER_ID_CONNETTORE;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common.IDSessioni;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
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
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SessioneStickyMaxAgeTest extends ConfigLoader {
	
	@BeforeClass
	public static void resetCache() {
		try {
			ConfigLoader.resetCache();
		}catch(Throwable t) {
            throw new RuntimeException(t.getMessage(),t);
        }
	}
	
	@Test
	public void headerHttpRoundRobin() {
		
		// Per i max Age una classe di test a parte, laddove posso tengo traccia dello stato dello
		// scheduler e alla richiesta successiva dopo lo scadere del maxAge devo controllare che
		// vada sul connettore atteso.
		
		final String erogazione = "LoadBalanceSessioneStickyHeaderHttpMaxAge";
		
		HttpRequest request1 = RequestBuilder.buildRequest_HeaderHttp(IDSessioni.get(0), erogazione);
		HttpRequest request2 = RequestBuilder.buildRequest_HeaderHttp(IDSessioni.get(1), erogazione);
		
		HttpResponse response1 = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeRequest(request1);
		HttpResponse response2 = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeRequest(request2);
		
		String connettore1 = response1.getHeaderFirstValue(HEADER_ID_CONNETTORE);
		String connettore2 = response2.getHeaderFirstValue(HEADER_ID_CONNETTORE);
		
		var responsesSameConnettore = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeParallelRequests(request1, 10);
		Common.checkAll200(responsesSameConnettore);
		Common.checkAllResponsesSameConnettore(responsesSameConnettore);
		
		responsesSameConnettore = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeParallelRequests(request2, 10);
		Common.checkAll200(responsesSameConnettore);
		Common.checkAllResponsesSameConnettore(responsesSameConnettore);
		
		// Faccio le richieste rimanenti per completare il giro del round robin
		int richiesteRimanenti = Common.setConnettoriAbilitati.size() - 2;
		HttpRequest balancedRequest = Common.buildRequest_Semplice(erogazione);
		var balancedResponses = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeParallelRequests(balancedRequest, richiesteRimanenti);
		Common.checkAll200(balancedResponses);
		
		// Adesso attendo il max Age e faccio scadere la sessione
		org.openspcoop2.utils.Utilities.sleep(Common.maxAge);
		
		// Faccio due richieste per saltare i due connettori iniziali
		var responses = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeSequentialRequests(balancedRequest, 2);
		assertEquals(connettore1, responses.get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE));
		assertEquals(connettore2, responses.get(1).getHeaderFirstValue(HEADER_ID_CONNETTORE));
		
		HttpResponse responseNuova1 = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeRequest(request1);
		HttpResponse responseNuova2 = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeRequest(request2);
		
		String connettoreNuovo1 = responseNuova1.getHeaderFirstValue(HEADER_ID_CONNETTORE);
		String connettoreNuovo2 = responseNuova2.getHeaderFirstValue(HEADER_ID_CONNETTORE);
		
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
		final String erogazione = "LoadBalanceSessioneStickyParametroUrlRandomMaxAge";
		
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
		final String erogazione = "LoadBalanceSessioneStickyFreemarkerTemplateWeightedRandomMaxAge";
		
		SessioneStickyTest.freemarkerTemplate_Impl(erogazione);
		
		// Adesso attendo il max Age e faccio scadere la sessione
		org.openspcoop2.utils.Utilities.sleep(Common.maxAge);
		
		SessioneStickyTest.freemarkerTemplate_Impl(erogazione);
	}
	
	
	@Test
	public void velocityTemplateLeastConnections() throws InterruptedException, ExecutionException {
		
		final String erogazione = "LoadBalanceSessioneStickyLeastConnectionsMaxAge";
		
		// Faccio quattro richieste con ID Sessione diverso, per tenere occupati i 4 connettori abilitati
		// e per creare le associazioni fra ID Sessione e connettore
		
		// NOTA: Solo Per l'erogazione di questo test il maxAge è impostato a 6 secondi invece che a 4
		// perchè nel test delle least connections ci sono più attese e la sessione nel frattempo scadrebbe
		var idSessioni = List
				.of(IDSessioni.get(0), IDSessioni.get(1), UUID.randomUUID().toString(), UUID.randomUUID().toString());
		List<HttpRequest> requests = idSessioni.stream()
				.map( id -> {
					var request = RequestBuilder.buildRequest_VelocityTemplate(id, erogazione);
					request.setUrl(request.getUrl() + "&sleep="+(Common.maxAge+2000));
					return request;
				})
				.collect(Collectors.toList());
		final List<Future<HttpResponse>> oldAsyncResponses = Common.makeBackgroundRequests(requests, requests.size(), Common.delayRichiesteBackground);

		org.openspcoop2.utils.Utilities.sleep(Common.delayRichiesteBackground);
		
		SessioneStickyTest.velocityTemplate_Impl(erogazione);
		
		// La sessione non ancora è finita, faccio una richiesta con sleep per mantenere
		// bloccato il primo connettore
		var blockingRequest = RequestBuilder.buildRequest_VelocityTemplate(IDSessioni.get(0), erogazione);
		blockingRequest.setUrl(blockingRequest.getUrl() + "&sleep=" + (Common.maxAge + 1000));
		var asyncBlockingResponse = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeBackgroundRequest(blockingRequest);
		
		org.openspcoop2.utils.Utilities.sleep(Common.delayRichiesteBackground);

				
		// Adesso attendo le prime richieste facendo quindi scadere sessioni.
		// Ripetendo una richiesta con il primo id sessione, questa deve cadere su un
		// connettore diverso dal primo perchè è ancora tenuto occupato 
		// e la sua associazione è andata persa nel tempo.
		var oldResponses = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.awaitResponses(oldAsyncResponses);
		String oldConnettore = oldResponses.get(0).getHeaderFirstValue(HEADER_ID_CONNETTORE);
		
		var newRequest = RequestBuilder.buildRequest_VelocityTemplate(IDSessioni.get(0), erogazione);
		var newResponse = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeRequest(newRequest);
		
		String newConnettore = newResponse.getHeaderFirstValue(HEADER_ID_CONNETTORE);
		assertEquals(200, newResponse.getResultHTTPOperation());
		assertNotNull(newConnettore);
		assertNotEquals(oldConnettore, newConnettore);
		
		assertFalse(asyncBlockingResponse.isDone());
		var resp = asyncBlockingResponse.get();
		assertEquals(200, resp.getResultHTTPOperation());
		assertEquals(oldConnettore, resp.getHeaderFirstValue(HEADER_ID_CONNETTORE));
	}

}
