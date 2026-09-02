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

package org.openspcoop2.core.protocolli.trasparente.testsuite.observability.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.observability.ErogazioneTestMetricsUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.observability.PrometheusMetricsUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.Utilities;

/**
 * TransactionMetricsTest
 *
 * Verifica che una serie di invocazioni verso l'erogazione 'TestMetrics' aggiornino
 * le metriche esposte su '/metrics'. L'API espone tre risorse:
 *  - info1, info2:     la proprieta' custom 'metrics.tempiElaborazione' NON e' abilitata
 *                      -> vengono aggiornate solo le metriche aggregate;
 *  - metricsDetails:   la proprieta' e' abilitata
 *                      -> vengono aggiornate anche le metriche di dettaglio per servizio/fase.
 *
 * Poiche' l'endpoint puo' esporre gia' dei dati all'inizio del test, si confrontano
 * due scrape (before/after) valutando solo il delta prodotto dalle chiamate.
 *
 * @author Burlon Tommaso
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionMetricsTest extends ConfigLoader {

	// Risorse dell'API (path URL): le prime due SENZA la proprieta' dei dettagli, la terza CON
	private static final String OP_INFO_1 = "info1";
	private static final String OP_INFO_2 = "info2";
	private static final String OP_DETAILS = "metricsDetails";

	// Valore della label 'action' (operationId) associato alle risorse
	private static final String ACTION_INFO_1 = "info1";
	private static final String ACTION_INFO_2 = "info2";
	private static final String ACTION_DETAILS = "details";

	// Numero di invocazioni per risorsa (valori distinti per rendere i delta significativi)
	private static final int CALLS_INFO_1 = 2;
	private static final int CALLS_INFO_2 = 3;
	private static final int CALLS_DETAILS = 4;

	// Metriche
	private static final String METRIC_REQUESTS_TOTAL = "govway_requests_total";
	private static final String METRIC_REQUEST_DURATION_COUNT = "govway_request_duration_seconds_count";
	private static final String METRIC_REQUEST_SIZE_COUNT = "govway_request_size_bytes_count";
	private static final String METRIC_REQUEST_SIZE_SUM = "govway_request_size_bytes_sum";
	private static final String METRIC_SERVICE_REQUEST_DURATION_COUNT = "govway_service_request_duration_seconds_count";
	private static final String METRIC_SERVICE_REQUEST_SIZE_COUNT = "govway_service_request_size_bytes_count";
	private static final String METRIC_PROCESSING_PHASE_COUNT = "govway_processing_phase_seconds_count";

	@Test
	public void metricheAggiornateErogazione() throws Exception {
		test(TipoServizio.EROGAZIONE);
	}

	@Test
	public void metricheAggiornateFruizione() throws Exception {
		test(TipoServizio.FRUIZIONE);
	}

	private void test(TipoServizio tipo) throws Exception {

		String metricsUrl = ErogazioneTestMetricsUtils.metricsUrl();
		String pddRole = ErogazioneTestMetricsUtils.pddRole(tipo); // erogazione=inbound / fruizione=outbound
		String api = ErogazioneTestMetricsUtils.api(tipo);         // TestMetricsInbound / TestMetricsOutbound

		// 1) Scrape iniziale (l'endpoint puo' contenere gia' dei dati preesistenti)
		PrometheusMetricsUtils before = PrometheusMetricsUtils.scrape(metricsUrl);

		// 2) Invocazioni verso le tre risorse dell'API
		ErogazioneTestMetricsUtils.invoke(tipo, OP_INFO_1, CALLS_INFO_1, getLoggerCore());
		ErogazioneTestMetricsUtils.invoke(tipo, OP_INFO_2, CALLS_INFO_2, getLoggerCore());
		ErogazioneTestMetricsUtils.invoke(tipo, OP_DETAILS, CALLS_DETAILS, getLoggerCore());

		int totalCalls = CALLS_INFO_1 + CALLS_INFO_2 + CALLS_DETAILS;

		Map<String,String> aggregato = Map.of(
				"pdd_role", pddRole,
				"protocol", "trasparente",
				"outcome", "ok");

		// 3) Scrape finale con polling: le metriche sono registrate nell'handler PostOutResponse,
		//    asincrono rispetto alla risposta HTTP; attendo che i delta attesi siano visibili.
		PrometheusMetricsUtils after = scrapeUntilRecorded(before, metricsUrl, aggregato, totalCalls, api, pddRole);

		double deltaRequests = PrometheusMetricsUtils.delta(before, after, METRIC_REQUESTS_TOTAL, aggregato);
		assertEquals("["+tipo+"] Il contatore aggregato '"+METRIC_REQUESTS_TOTAL+"' non e' aumentato del numero di chiamate",
				(double) totalCalls, deltaRequests, 0.0);

		// L'istogramma di latenza aggregato ha una serie per fase (total/service/gateway): filtro su 'total'
		Map<String,String> aggregatoTotale = Map.of(
				"pdd_role", pddRole,
				"protocol", "trasparente",
				"outcome", "ok",
				"phase", "total");
		double deltaDuration = PrometheusMetricsUtils.delta(before, after, METRIC_REQUEST_DURATION_COUNT, aggregatoTotale);
		assertEquals("["+tipo+"] Il count dell'istogramma di latenza aggregato non e' aumentato del numero di chiamate",
				(double) totalCalls, deltaDuration, 0.0);

		// --- Dimensione della richiesta (direction=in_req): il body inviato ha dimensione nota ---
		Map<String,String> aggregatoInReq = Map.of(
				"pdd_role", pddRole,
				"protocol", "trasparente",
				"outcome", "ok",
				"direction", "in_req");
		double deltaSizeCount = PrometheusMetricsUtils.delta(before, after, METRIC_REQUEST_SIZE_COUNT, aggregatoInReq);
		assertEquals("["+tipo+"] Il count della metrica di dimensione richiesta non e' aumentato del numero di chiamate",
				(double) totalCalls, deltaSizeCount, 0.0);
		double deltaSizeSum = PrometheusMetricsUtils.delta(before, after, METRIC_REQUEST_SIZE_SUM, aggregatoInReq);
		assertEquals("["+tipo+"] La dimensione media della richiesta registrata non corrisponde ai byte del body inviato",
				(double) ErogazioneTestMetricsUtils.BODY.length, deltaSizeSum / deltaSizeCount, 0.0);

		// --- Metriche di dettaglio per servizio: SOLO 'metricsDetails' (action='details') ha la proprieta' abilitata ---
		// Il count delle metriche per-servizio (fase 'total') deve aumentare solo per le chiamate a metricsDetails,
		// con action corretta e le versioni distinte: service_version (2 erogazione / 1 fruizione) e api_version (1).
		String serviceVersion = ErogazioneTestMetricsUtils.serviceVersion(tipo);
		String apiVersion = ErogazioneTestMetricsUtils.apiVersion(tipo);
		Map<String,String> servizioTotale = labels("service", api, "service_version", serviceVersion,
				"pdd_role", pddRole, "action", ACTION_DETAILS, "api_version", apiVersion, "phase", "total");
		double deltaServizio = PrometheusMetricsUtils.delta(before, after, METRIC_SERVICE_REQUEST_DURATION_COUNT, servizioTotale);
		assertEquals("["+tipo+"] Le metriche per-servizio devono essere prodotte solo dalla risorsa con '"
				+"observability.metrics.details' abilitata (service+service_version+action+api_version)",
				(double) CALLS_DETAILS, deltaServizio, 0.0);

		// La label 'api' (nome accordo) deve essere presente e non vuota sulle metriche di dettaglio
		Set<String> apiValues = after.labelValues(METRIC_SERVICE_REQUEST_DURATION_COUNT, "api", servizioTotale);
		assertFalse("["+tipo+"] La label 'api' deve essere presente sulle metriche di dettaglio", apiValues.isEmpty());
		assertFalse("["+tipo+"] La label 'api' non deve essere vuota", apiValues.contains(""));

		// Anche la dimensione per-servizio (direction=in_req) deve arrivare solo da metricsDetails
		double deltaServizioSize = PrometheusMetricsUtils.delta(before, after, METRIC_SERVICE_REQUEST_SIZE_COUNT,
				labels("service", api, "service_version", serviceVersion, "pdd_role", pddRole,
						"action", ACTION_DETAILS, "api_version", apiVersion, "direction", "in_req"));
		assertEquals("["+tipo+"] La dimensione per-servizio deve essere prodotta solo dalla risorsa metricsDetails",
				(double) CALLS_DETAILS, deltaServizioSize, 0.0);

		// Controprova: filtrando per servizio (senza action) il totale deve restare CALLS_DETAILS,
		// cioe' nessun'altra risorsa (info1/info2) ha prodotto metriche per-servizio.
		double deltaServizioAllActions = PrometheusMetricsUtils.delta(before, after, METRIC_SERVICE_REQUEST_DURATION_COUNT,
				Map.of("service", api, "pdd_role", pddRole, "phase", "total"));
		assertEquals("["+tipo+"] Le metriche per-servizio devono provenire esclusivamente da metricsDetails",
				(double) CALLS_DETAILS, deltaServizioAllActions, 0.0);

		// info1/info2 NON devono produrre metriche per-servizio con la propria action
		assertEquals("["+tipo+"] La risorsa 'info1' non deve produrre metriche per-servizio",
				0.0, PrometheusMetricsUtils.delta(before, after, METRIC_SERVICE_REQUEST_DURATION_COUNT,
						Map.of("service", api, "pdd_role", pddRole, "action", ACTION_INFO_1)), 0.0);
		assertEquals("["+tipo+"] La risorsa 'info2' non deve produrre metriche per-servizio",
				0.0, PrometheusMetricsUtils.delta(before, after, METRIC_SERVICE_REQUEST_DURATION_COUNT,
						Map.of("service", api, "pdd_role", pddRole, "action", ACTION_INFO_2)), 0.0);

		// --- Istogrammi per-fase di elaborazione: presenti solo per metricsDetails (service+action+versioni) ---
		double deltaFasi = PrometheusMetricsUtils.delta(before, after, METRIC_PROCESSING_PHASE_COUNT,
				labels("service", api, "service_version", serviceVersion, "pdd_role", pddRole,
						"action", ACTION_DETAILS, "api_version", apiVersion));
		assertTrue("["+tipo+"] Gli istogrammi per-fase ('"+METRIC_PROCESSING_PHASE_COUNT+"') devono essere aggiornati dalla risorsa metricsDetails",
				deltaFasi > 0);
	}

	/** Costruisce una mappa di label da coppie chiave/valore (Map.of supporta al massimo 5 coppie). */
	private static Map<String,String> labels(String... kv) {
		Map<String,String> m = new HashMap<>();
		for (int i = 0; i+1 < kv.length; i += 2) {
			m.put(kv[i], kv[i+1]);
		}
		return m;
	}

	// Polling: fino a ~15s con backoff, in linea con l'approccio dei test su fase PostOutResponse
	private static final int[] POLL_BACKOFF_MS = { 200, 300, 500, 1000, 1000, 2000, 2000, 3000, 5000 };

	/**
	 * Esegue scrape ripetuti finche' i delta prodotti dalle chiamate non sono visibili
	 * (contatore aggregato e istogrammi per-fase della risorsa metricsDetails), oppure
	 * fino allo scadere dei tentativi; restituisce comunque l'ultimo scrape effettuato.
	 */
	private static PrometheusMetricsUtils scrapeUntilRecorded(PrometheusMetricsUtils before, String metricsUrl,
			Map<String,String> aggregato, int expectedRequestsDelta, String api, String pddRole) throws Exception {
		Map<String,String> servizio = Map.of("service", api, "pdd_role", pddRole);
		PrometheusMetricsUtils after = null;
		for (int i = 0; i < POLL_BACKOFF_MS.length; i++) {
			Utilities.sleep(POLL_BACKOFF_MS[i]);
			after = PrometheusMetricsUtils.scrape(metricsUrl);
			double deltaRequests = PrometheusMetricsUtils.delta(before, after, METRIC_REQUESTS_TOTAL, aggregato);
			double deltaFasi = PrometheusMetricsUtils.delta(before, after, METRIC_PROCESSING_PHASE_COUNT, servizio);
			if(deltaRequests >= expectedRequestsDelta && deltaFasi > 0) {
				getLoggerCore().info("Metriche registrate dopo {} tentativi (delta richieste={}, delta fasi={})",
						i+1, deltaRequests, deltaFasi);
				return after;
			}
		}
		getLoggerCore().warn("Timeout in attesa della registrazione delle metriche: proseguo con l'ultimo scrape");
		return after;
	}
}
