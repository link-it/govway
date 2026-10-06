/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* Verifica che una violazione di policy registrata presso il notificatore degli eventi con una data antecedente
* all'ultima elaborazione del timer degli eventi produca comunque l'evento di violazione (e la successiva risoluzione).
*
* La situazione si verifica quando il timer degli eventi viene eseguito tra la rilevazione della violazione (in cui viene presa la data)
* e la sua registrazione in memoria: l'evento veniva perso. Su Jenkins si manifestava con l'assenza degli eventi di alcuni
* raggruppamenti violati contemporaneamente (es. RestTest.perHeaderXForwardedForFruizione).
*
* La registrazione ritardata viene simulata dall'handler 'classes.NotificaEventoRitardatoHandler' (plugin incluso in extraTestBundle.zip), configurato sull'erogazione 'TestEventiRateLimiting'.
*
* @author $Author$
* @version $Rev$, $Date$
*/
public class EventoViolazioneRegistrataInRitardoTest extends ConfigLoader {

	// header gestiti dall'handler NotificaEventoRitardatoHandler
	private static final String HEADER_ID_EVENTO = "govway-testsuite-evento-ritardato-id";
	private static final String HEADER_SECONDI_RITARDO = "govway-testsuite-evento-ritardato-secondi";
	
	private static final String TIPO_EVENTO = "RateLimiting_PolicyAPI";
	private static final String CODICE_VIOLAZIONE = "Violazione";
	private static final String CODICE_VIOLAZIONE_RISOLTA = "ViolazioneRisolta";

	@Test
	public void violazioneConDataAntecedenteUltimoIntervallo() throws Exception {

		// La data della violazione viene arretrata di tre periodi del timer degli eventi: risulta quindi sicuramente
		// antecedente all'ultima elaborazione del timer, che avviene ogni 'eventi_db_delay' secondi.
		int periodoTimerSecondi = Integer.parseInt(System.getProperty("eventi_db_delay"));
		int ritardoSecondi = periodoTimerSecondi * 3;

		String idEvento = "TestEventoViolazioneRegistrataInRitardo - "+UUID.randomUUID().toString();

		LocalDateTime filtroData = LocalDateTime.now().minusSeconds(ritardoSecondi + 60l);
		Date dataInvio = DateManager.getDate();

		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/TestEventiRateLimiting/v1/echo");
		request.addHeader(HEADER_ID_EVENTO, idEvento);
		request.addHeader(HEADER_SECONDI_RITARDO, ritardoSecondi+"");

		logRateLimiting.info("Invio richiesta con violazione registrata in ritardo di "+ritardoSecondi+" secondi (id: "+idEvento+")");
		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());

		// Attendo almeno due elaborazioni del timer: la prima emette la violazione, la successiva la sua risoluzione
		org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.waitForDbEvents();

		List<Map<String, Object>> eventsAll = org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils.getNotificheEventi(filtroData);
		if(eventsAll==null) {
			eventsAll = List.of();
		}
		List<Map<String, Object>> events = eventsAll
				.stream()
				.filter(ev -> idEvento.equals(ev.get("id_configurazione")))
				.collect(Collectors.toList());
		logRateLimiting.info("Eventi registrati per l'id '"+idEvento+"': "+events);

		List<Map<String, Object>> violazioni = events.stream()
				.filter(ev -> TIPO_EVENTO.equals(ev.get("tipo")) && CODICE_VIOLAZIONE.equals(ev.get("codice")))
				.collect(Collectors.toList());
		assertEquals("Eventi di violazione attesi per l'id '"+idEvento+"'", 1, violazioni.size());
		assertEquals(1, violazioni.get(0).get("severita"));

		// L'evento di violazione riporta la data in cui è stata rilevata la violazione, antecedente all'invio della richiesta
		Object oraRegistrazione = violazioni.get(0).get("ora_registrazione");
		assertTrue("ora_registrazione non è una data: "+oraRegistrazione, oraRegistrazione instanceof Date);
		assertTrue("ora_registrazione '"+oraRegistrazione+"' non antecedente all'invio della richiesta '"+dataInvio+"'",
				((Date)oraRegistrazione).before(dataInvio));

		List<Map<String, Object>> risoluzioni = events.stream()
				.filter(ev -> TIPO_EVENTO.equals(ev.get("tipo")) && CODICE_VIOLAZIONE_RISOLTA.equals(ev.get("codice")))
				.collect(Collectors.toList());
		assertEquals("Eventi di risoluzione della violazione attesi per l'id '"+idEvento+"'", 1, risoluzioni.size());
		assertEquals(3, risoluzioni.get(0).get("severita"));

	}

}
