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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.observability.ErogazioneTestMetricsUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.observability.PrometheusMetricsUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.Utilities;

/**
 * LatencyMetricsTest
 *
 * Verifica che le metriche di latenza/esito riflettano i parametri della richiesta. Le API
 * TestMetrics inoltrano al TestService di GovWay, che interpreta i parametri di query:
 *  - returnCode=<codice>: il backend risponde con quel codice HTTP -> label 'outcome';
 *  - sleep=<ms>: il backend attende quei ms -> la latenza cade nei bucket corrispondenti.
 *
 * @author Burlon Tommaso
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LatencyMetricsTest extends ConfigLoader {

	private static final String OP = "info1";

	private static final int CALLS_OK = 3;
	private static final int CALLS_KO = 2;
	private static final int CALLS_SLEEP = 3;

	// sleep del backend: la latenza totale sara' > 500ms e < (verosimilmente) 1s
	private static final int SLEEP_MS = 600;

	private static final String METRIC_REQUESTS_TOTAL = "govway_requests_total";
	private static final String METRIC_REQUEST_DURATION_COUNT = "govway_request_duration_seconds_count";
	private static final String METRIC_REQUEST_DURATION_BUCKET = "govway_request_duration_seconds_bucket";

	// Bucket 'le' attesi: default 'observability.metrics.latency-slotMs' (ms -> secondi) + +Inf
	private static final Set<String> EXPECTED_LE = new TreeSet<>(Arrays.asList(
			"0.005", "0.01", "0.025", "0.05", "0.1", "0.25", "0.5", "1.0", "2.5", "5.0", "10.0", "+Inf"));

	private static final int[] POLL_BACKOFF_MS = { 200, 300, 500, 1000, 1000, 2000, 2000, 3000, 5000 };

	// ---- esiti (returnCode -> outcome) ----

	@Test
	public void esitiErogazione() throws Exception {
		testEsiti(TipoServizio.EROGAZIONE);
	}

	@Test
	public void esitiFruizione() throws Exception {
		testEsiti(TipoServizio.FRUIZIONE);
	}

	private void testEsiti(TipoServizio tipo) throws Exception {
		String metricsUrl = ErogazioneTestMetricsUtils.metricsUrl();
		String pddRole = ErogazioneTestMetricsUtils.pddRole(tipo);

		Map<String,String> ok = baseLabels(pddRole, "ok");
		Map<String,String> ko = baseLabels(pddRole, "ko");

		PrometheusMetricsUtils before = PrometheusMetricsUtils.scrape(metricsUrl);

		// chiamate con esito ok (200) e ko (returnCode=500 sul backend)
		ErogazioneTestMetricsUtils.invoke(tipo, OP, null, CALLS_OK, 200, getLoggerCore());
		ErogazioneTestMetricsUtils.invoke(tipo, OP, "returnCode=500", CALLS_KO, 500, getLoggerCore());

		PrometheusMetricsUtils after = scrapeUntil(before, metricsUrl, METRIC_REQUESTS_TOTAL, ok, CALLS_OK);

		double deltaOk = PrometheusMetricsUtils.delta(before, after, METRIC_REQUESTS_TOTAL, ok);
		double deltaKo = PrometheusMetricsUtils.delta(before, after, METRIC_REQUESTS_TOTAL, ko);

		assertEquals("["+tipo+"] Le richieste con esito 'ok' non corrispondono alle chiamate con returnCode 200",
				(double) CALLS_OK, deltaOk, 0.0);
		assertEquals("["+tipo+"] Le richieste con esito 'ko' non corrispondono alle chiamate con returnCode 500",
				(double) CALLS_KO, deltaKo, 0.0);
	}

	// ---- latenza (sleep -> bucket) ----

	@Test
	public void bucketLatenzaErogazione() throws Exception {
		testBucketLatenza(TipoServizio.EROGAZIONE);
	}

	@Test
	public void bucketLatenzaFruizione() throws Exception {
		testBucketLatenza(TipoServizio.FRUIZIONE);
	}

	private void testBucketLatenza(TipoServizio tipo) throws Exception {
		String metricsUrl = ErogazioneTestMetricsUtils.metricsUrl();
		String pddRole = ErogazioneTestMetricsUtils.pddRole(tipo);

		// serie: latenza totale, esito ok
		Map<String,String> serie = baseLabels(pddRole, "ok");
		serie.put("phase", "total");

		PrometheusMetricsUtils before = PrometheusMetricsUtils.scrape(metricsUrl);

		// chiamate con sleep sul backend: la latenza sara' > SLEEP_MS
		ErogazioneTestMetricsUtils.invoke(tipo, OP, "sleep="+SLEEP_MS, CALLS_SLEEP, 200, getLoggerCore());

		PrometheusMetricsUtils after = scrapeUntil(before, metricsUrl, METRIC_REQUEST_DURATION_COUNT, serie, CALLS_SLEEP);

		// il count della serie deve essere aumentato del numero di chiamate
		double deltaCount = PrometheusMetricsUtils.delta(before, after, METRIC_REQUEST_DURATION_COUNT, serie);
		assertEquals("["+tipo+"] Il count della latenza non corrisponde alle chiamate con sleep",
				(double) CALLS_SLEEP, deltaCount, 0.0);

		// con sleep=600ms nessuna richiesta deve cadere nei bucket <= 500ms
		double deltaLe500 = PrometheusMetricsUtils.delta(before, after, METRIC_REQUEST_DURATION_BUCKET, withLe(serie, "0.5"));
		assertEquals("["+tipo+"] Con sleep="+SLEEP_MS+"ms nessuna richiesta deve cadere nel bucket le=0.5s",
				0.0, deltaLe500, 0.0);
		double deltaLe250 = PrometheusMetricsUtils.delta(before, after, METRIC_REQUEST_DURATION_BUCKET, withLe(serie, "0.25"));
		assertEquals("["+tipo+"] Con sleep="+SLEEP_MS+"ms nessuna richiesta deve cadere nel bucket le=0.25s",
				0.0, deltaLe250, 0.0);

		// tutte le richieste devono comunque essere conteggiate nel bucket +Inf
		double deltaLeInf = PrometheusMetricsUtils.delta(before, after, METRIC_REQUEST_DURATION_BUCKET, withLe(serie, "+Inf"));
		assertEquals("["+tipo+"] Il bucket +Inf deve contare tutte le richieste",
				(double) CALLS_SLEEP, deltaLeInf, 0.0);

		// i confini 'le' devono coincidere con quelli di default (latency-slotMs)
		Set<String> leValues = after.labelValues(METRIC_REQUEST_DURATION_BUCKET, "le", serie);
		assertTrue("["+tipo+"] I bucket di latenza non coincidono con quelli di default (latency-slotMs): "+leValues,
				leValues.equals(EXPECTED_LE));
	}

	// ---- helper ----

	private static Map<String,String> baseLabels(String pddRole, String outcome) {
		Map<String,String> m = new HashMap<>();
		m.put("pdd_role", pddRole);
		m.put("protocol", "trasparente");
		m.put("outcome", outcome);
		return m;
	}

	private static Map<String,String> withLe(Map<String,String> base, String le) {
		Map<String,String> m = new HashMap<>(base);
		m.put("le", le);
		return m;
	}

	/** Scrape ripetuto finche' il delta della metrica/filtro raggiunge il valore atteso (o timeout). */
	private static PrometheusMetricsUtils scrapeUntil(PrometheusMetricsUtils before, String metricsUrl,
			String metric, Map<String,String> filtro, int expectedDelta) throws Exception {
		PrometheusMetricsUtils after = null;
		for (int i = 0; i < POLL_BACKOFF_MS.length; i++) {
			Utilities.sleep(POLL_BACKOFF_MS[i]);
			after = PrometheusMetricsUtils.scrape(metricsUrl);
			if(PrometheusMetricsUtils.delta(before, after, metric, filtro) >= expectedDelta) {
				return after;
			}
		}
		return after;
	}
}
