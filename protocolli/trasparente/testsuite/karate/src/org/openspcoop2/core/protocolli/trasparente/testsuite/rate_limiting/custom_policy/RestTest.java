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


package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.custom_policy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Vector;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;


/** 
 * Per questi test non è necessario far fallire la policy, è sufficiente verificare
 *  che venga attivata quella giusta con il relativo conteggio.
 *  
 */

public class RestTest extends ConfigLoader {
	
	private static final String basePath = System.getProperty("govway_base_path");

	@Test
	public void customPolicyErogazione() {
		customPolicy(TipoServizio.EROGAZIONE);
	}
	
	
	@Test
	public void customPolicyFruizione() {
		customPolicy(TipoServizio.FRUIZIONE);
	}
	
	public static void customPolicy(TipoServizio tipoServizio) {
		final String erogazione = "CustomPolicyRest";
		final int nrequests = 5;
		final int payload_size = 1024;
		
		final String url = tipoServizio == TipoServizio.EROGAZIONE
				? basePath + "/SoggettoInternoTest/"+erogazione+"/v1"
				: basePath + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+erogazione+"/v1";
		

		// Aspettiamo nuove statistiche pulite.
		Utils.waitForDbStats();

		// La policy con l'intervallo più breve è quella oraria, allora per essere
		// sicuri di non sforare in tutti i casi nell'intervallo successivo, è sufficiente
		// aspettare l'ora
		Utils.waitForNewHour();	
		
		// Creo un messaggio grande payload_size bytes, e con un tempo di attesa di 2 secondi.
		// Faccio le richieste, che devono andare tutte bene e controllo che
		//	1 - lo header RequestSuccesfullRemaining sia corretto
		//	2 - Quello dell'occupazione banda anche
		// 	3 - I dati sulla policy per il tempo medio risposta siano corretti.
				
		//	/orario 	 -> policy oraria
		//	/giornaliero -> policy giornaliera
		//  /no-policy 	 -> policy settimanale
		// 	/minuto		 -> policy mensile

		final PolicyAlias[] policies = { PolicyAlias.ORARIO, PolicyAlias.GIORNALIERO, PolicyAlias.NO_POLICY, PolicyAlias.MINUTO };

		final int[] succesfullHeaderRemaining = new int[policies.length];
		final int[] bandwidthQuotaRemaining = new int[policies.length];
		
		// Faccio una richiesta per vedere quante ne mancano ogni header
		// e altre n richieste per far incrementare il conteggio.
		for(int i=0;i<policies.length;i++) {
			logRateLimiting.info("Attivo conteggio su " + policies[i]);
			HttpRequest request = new HttpRequest();
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.POST);
			request.setUrl(url + "/" + Utils.getPolicyPath(policies[i]));
			request.setContent(new String(generatePayload(payload_size)).getBytes());
						
			// Vedo quante ne mancano per ogni header
			HttpResponse firstResp = Utils.makeRequest(request);
			assertEquals(200, firstResp.getResultHTTPOperation());
			succesfullHeaderRemaining[i] = Integer.valueOf(firstResp.getHeader(Headers.RequestSuccesfulRemaining));
			bandwidthQuotaRemaining[i] = Integer.valueOf(firstResp.getHeader(Headers.BandWidthQuotaRemaining));
						
			// Faccio n richieste
			Vector<HttpResponse> responses = Utils.makeParallelRequests(request, nrequests);

			// Le richieste devono essere andate tutte bene
			assertEquals(nrequests, responses.stream().filter(r -> r.getResultHTTPOperation() == 200).count()); 
		}
		
		logRateLimiting.info("Succesful Headers remaining: " );
		for(int i = 0; i < policies.length; i++) {
			var h  = succesfullHeaderRemaining[i];
			logRateLimiting.info(policies[i] + ": " + String.valueOf(h));
		}
		
		logRateLimiting.info("Bandwidth quota Headers remaining: " );
		for(int i = 0; i < policies.length; i++) {
			var h  = bandwidthQuotaRemaining[i];
			logRateLimiting.info(policies[i] + ": " + String.valueOf(h));
		}
		
		Utils.waitForDbStats();

		// Faccio per ogni azione una nuova richiesta in modo da controllare
		// che gli headers remaining siano diminuiti opportunamente 
		for(int i=0;i<policies.length;i++) {
			
			logRateLimiting.info("Controllo il conteggio su "+policies[i]);

			String body = new String(generatePayload(payload_size));
			HttpRequest request = new HttpRequest();
			request.setContentType("application/json");
			request.setMethod(HttpRequestMethod.POST);
			request.setUrl(url + "/" + Utils.getPolicyPath(policies[i]));
			request.setContent(body.getBytes());
			
			// Siccome le policy custom sono sull'intervallo statistico corrente, potrebbe succedere che nel momento in cui avviene la richiesta, 
			// il timer ha eliminato le informazioni statistiche sulla data corrente per aggiornarle.
			// Questo provoca che gli header ritornati possiedono dei valori remaining uguali al limite massimo della policy (es. 10000 per la banda) o comunque che non tengono conto dell'intervallo corrente (es. per settimanale e mensile)
			// Il test in caso di fallimento verrà quindi ripetuto tre volete prima di assumere che sia fallito.
			// Si spera che in queste tre volte non si rientri nel caso limite.
			// A regime non ha senso fare una policy sull'intervallo statistico basato sul'intervallo corrente con finestra uguale a 1.
			// Lo si è fatto solo per motivi di test.
			
			boolean requestSuccesful = false;
			boolean bandwidth = false;
			boolean avg = false;
			
			for (int j = 0; j < 3; j++) {
				
				logRateLimiting.info("["+j+"] Invocazione ...");
				HttpResponse lastResp = Utils.makeRequest(request);
				assertEquals(200, lastResp.getResultHTTPOperation());
				
				if(!requestSuccesful) {
					logRateLimiting.info("["+j+"] Controllo " + Headers.RequestSuccesfulRemaining);
					int updatedHeaderRemaining = Integer.valueOf(lastResp.getHeader(Headers.RequestSuccesfulRemaining));
					int shouldRemaining = succesfullHeaderRemaining[i] - (nrequests+1);
					//assertEquals(shouldRemaining, updatedHeaderRemaining);
					logRateLimiting.info("["+j+"] Richieste completate con successo su "+policies[i]+": " + updatedHeaderRemaining);
					logRateLimiting.info("["+j+"] Dovrebbero rimanerne meno di: " + shouldRemaining);
					requestSuccesful = updatedHeaderRemaining <= shouldRemaining;
					logRateLimiting.info("["+j+"] Esito: " + requestSuccesful);
				}
				
				if(!bandwidth) {
					logRateLimiting.info("["+j+"] Controllo " + Headers.BandWidthQuotaRemaining);
					int updatedHeaderRemaining = Integer.valueOf(lastResp.getHeader(Headers.BandWidthQuotaRemaining));
					int shouldRemaining = bandwidthQuotaRemaining[i] - (2*(nrequests+1)*body.getBytes().length)/1024;
					logRateLimiting.info("["+j+"] Banda rimanente su "+policies[i]+": " + updatedHeaderRemaining);
					logRateLimiting.info("["+j+"] Dovrebbero rimanerne meno di: " + shouldRemaining);
					bandwidth = updatedHeaderRemaining <= shouldRemaining;
					logRateLimiting.info("["+j+"] Esito: " + bandwidth);
				}
				
				if(!avg) {
					logRateLimiting.info("["+j+"] Controllo presenza header:" + Headers.AvgTimeResponseLimit);
					avg = lastResp.getHeader(Headers.AvgTimeResponseLimit) != null;
					logRateLimiting.info("["+j+"] Esito: " + avg);
				}
				
				if(!requestSuccesful || !bandwidth || !avg) {
					Utils.waitForDbStats();
				}
				else {
					break;
				}
				
			}
			
			assertTrue(requestSuccesful);
			assertTrue(bandwidth);
			assertTrue(avg);
			
		}
	}
	
	
	private static byte[] generatePayload(int payloadSize) {
		byte[] ret = new byte[payloadSize];
		Arrays.fill(ret, (byte) 97);
		return ret;
	}
}
