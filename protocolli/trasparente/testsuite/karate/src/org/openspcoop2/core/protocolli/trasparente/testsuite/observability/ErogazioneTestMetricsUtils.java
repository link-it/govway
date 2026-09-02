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

package org.openspcoop2.core.protocolli.trasparente.testsuite.observability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.charset.StandardCharsets;

import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
 * ErogazioneTestMetricsUtils
 *
 * Helper condiviso dai test di observability per invocare l'API 'TestMetrics'
 * (in erogazione o in fruizione) e per comporre l'URL dell'endpoint '/metrics'.
 *
 * @author Burlon Tommaso
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErogazioneTestMetricsUtils {

	private ErogazioneTestMetricsUtils() {}

	public static final String SOGGETTO_INTERNO = "SoggettoInternoTest";
	public static final String SOGGETTO_ESTERNO = "SoggettoEsternoTest";
	// L'API ha nome diverso a seconda del ruolo: erogazione vs fruizione
	public static final String API_EROGAZIONE = "TestMetricsInbound";
	public static final String API_FRUIZIONE = "TestMetricsOutbound";

	// Versione nel path URL: l'erogazione espone la v2, la fruizione la v1
	public static final String VERSIONE_PATH_EROGAZIONE = "v2";
	public static final String VERSIONE_PATH_FRUIZIONE = "v1";

	// Versione del SERVIZIO (erogazione/fruizione), label 'service_version' delle metriche di dettaglio
	public static final String SERVICE_VERSION_EROGAZIONE = "2";
	public static final String SERVICE_VERSION_FRUIZIONE = "1";
	// Versione dell'API (accordo parte comune), label 'api_version': la stessa (1) per erogazione e fruizione
	public static final String API_VERSION = "1";

	// Valori della label 'pdd_role' esposta dalle metriche
	public static final String PDD_ROLE_EROGAZIONE = "inbound";
	public static final String PDD_ROLE_FRUIZIONE = "outbound";

	/** Body a dimensione nota, riusabile per verificare anche la metrica sulla dimensione della richiesta. */
	public static final byte[] BODY = "{\"metric\":\"test\",\"value\":42}".getBytes(StandardCharsets.UTF_8);

	/** Nome dell'API/servizio (label 'service' delle metriche) per il tipo di servizio. */
	public static String api(TipoServizio tipo) {
		return TipoServizio.FRUIZIONE.equals(tipo) ? API_FRUIZIONE : API_EROGAZIONE;
	}

	/** Versione del servizio (label 'service_version') per il tipo di servizio: 2 erogazione, 1 fruizione. */
	public static String serviceVersion(TipoServizio tipo) {
		return TipoServizio.FRUIZIONE.equals(tipo) ? SERVICE_VERSION_FRUIZIONE : SERVICE_VERSION_EROGAZIONE;
	}

	/** Versione dell'API/accordo (label 'api_version'): 1 in entrambi i casi. */
	public static String apiVersion(TipoServizio tipo) {
		return API_VERSION;
	}

	/** Valore della label 'pdd_role' atteso per il tipo di servizio. */
	public static String pddRole(TipoServizio tipo) {
		return TipoServizio.FRUIZIONE.equals(tipo) ? PDD_ROLE_FRUIZIONE : PDD_ROLE_EROGAZIONE;
	}

	/** URL dell'endpoint di scrape delle metriche. */
	public static String metricsUrl() {
		return System.getProperty("govway_base_path") + "/metrics";
	}

	/** URL di una risorsa dell'API TestMetrics, in erogazione (v2) o in fruizione (v1). */
	public static String resourceUrl(TipoServizio tipo, String operazione) {
		String base = System.getProperty("govway_base_path");
		if(TipoServizio.FRUIZIONE.equals(tipo)) {
			// /out/<fruitore>/<erogatore>/<api>/<versione>/<risorsa>
			return base + "/out/" + SOGGETTO_INTERNO + "/" + SOGGETTO_ESTERNO + "/" + API_FRUIZIONE + "/" + VERSIONE_PATH_FRUIZIONE + "/" + operazione;
		}
		// erogazione: /<erogatore>/<api>/<versione>/<risorsa>
		return base + "/" + SOGGETTO_INTERNO + "/" + API_EROGAZIONE + "/" + VERSIONE_PATH_EROGAZIONE + "/" + operazione;
	}

	/** Invoca 'count' volte in POST la risorsa indicata, verificando HTTP 200 e la presenza dell'id transazione. */
	public static void invoke(TipoServizio tipo, String operazione, int count, Logger log) throws Exception {
		invoke(tipo, operazione, null, count, 200, log);
	}

	/**
	 * Invoca 'count' volte in POST la risorsa indicata (con eventuale query string, es. "sleep=600"
	 * o "returnCode=500" per il TestService di backend), verificando il codice HTTP atteso e la
	 * presenza dell'id transazione.
	 */
	public static void invoke(TipoServizio tipo, String operazione, String query, int count, int expectedHttpCode, Logger log) throws Exception {
		String url = resourceUrl(tipo, operazione);
		if(query!=null && !query.isEmpty()) {
			url = url + "?" + query;
		}
		for (int i = 0; i < count; i++) {
			HttpRequest request = new HttpRequest();
			request.setReadTimeout(30000);
			request.setMethod(HttpRequestMethod.POST);
			request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
			request.setContent(BODY);
			request.setUrl(url);

			HttpResponse response = HttpUtilities.httpInvoke(request);

			String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
			assertNotNull("Nessun id transazione per la chiamata a "+url, idTransazione);
			assertEquals("Chiamata a "+url+" con codice HTTP inatteso", expectedHttpCode, response.getResultHTTPOperation());
		}
		if(log!=null) {
			log.info("Eseguite {} chiamate POST a {} (atteso HTTP {})", count, url, expectedHttpCode);
		}
	}
}
