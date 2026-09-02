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
 * PersistenceMetricsTest
 *
 * Verifica gli istogrammi di persistenza del tracciamento (govway_tracing_persistence_seconds):
 * vengono registrati per TUTTE le transazioni (non solo quelle lente).
 * Si verifica la presenza dopo un'invocazione e che i bucket coincidano con quelli di default
 * (property 'observability.metrics.persistence-slotMs').
 *
 * @author Burlon Tommaso
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PersistenceMetricsTest extends ConfigLoader {

	private static final String METRIC_PERSISTENCE_COUNT = "govway_tracing_persistence_seconds_count";
	private static final String METRIC_PERSISTENCE_BUCKET = "govway_tracing_persistence_seconds_bucket";

	private static final String FASE_POST_OUT_RESPONSE = "POST_OUT_RESPONSE";

	// Bucket 'le' attesi: default 'observability.metrics.persistence-slotMs' (ms -> secondi) + +Inf
	private static final Set<String> EXPECTED_LE = new TreeSet<>(Arrays.asList(
			"0.005", "0.01", "0.025", "0.05", "0.1", "0.25", "0.5", "1.0", "2.5", "5.0", "10.0", "+Inf"));

	private static final int[] POLL_BACKOFF_MS = { 200, 300, 500, 1000, 1000, 2000, 2000, 3000, 5000 };

	@Test
	public void istogrammiPersistenzaPresentiConBucketDiDefault() throws Exception {

		String metricsUrl = ErogazioneTestMetricsUtils.metricsUrl();
		Map<String,String> filtro = Map.of("phase", FASE_POST_OUT_RESPONSE);

		// 1) Scrape iniziale (l'endpoint puo' contenere gia' dati preesistenti)
		PrometheusMetricsUtils before = PrometheusMetricsUtils.scrape(metricsUrl);

		// 2) Invocazioni: ogni transazione registra la persistenza del tracciamento
		ErogazioneTestMetricsUtils.invoke(TipoServizio.EROGAZIONE, "info1", 3, getLoggerCore());

		// 3) Scrape finale con polling: la persistenza e' registrata in PostOutResponse (asincrono)
		double deltaCount = 0;
		PrometheusMetricsUtils after = null;
		for (int i = 0; i < POLL_BACKOFF_MS.length; i++) {
			Utilities.sleep(POLL_BACKOFF_MS[i]);
			after = PrometheusMetricsUtils.scrape(metricsUrl);
			deltaCount = PrometheusMetricsUtils.delta(before, after, METRIC_PERSISTENCE_COUNT, filtro);
			if(deltaCount > 0) {
				getLoggerCore().info("Persistenza tracciamento registrata dopo {} tentativi (delta count={})", i+1, deltaCount);
				break;
			}
		}

		// 4a) Presenza: l'istogramma della persistenza (fase POST_OUT_RESPONSE) e' stato aggiornato
		assertTrue("L'istogramma '"+METRIC_PERSISTENCE_COUNT+"' deve essere aggiornato dopo le invocazioni "
				+ "(richiede slow-log abilitato)", deltaCount > 0);

		// 4b) Bucket: i confini 'le' devono coincidere con quelli di default (persistence-slotMs)
		Set<String> leValues = after.labelValues(METRIC_PERSISTENCE_BUCKET, "le", filtro);
		assertEquals("I bucket dell'istogramma di persistenza non coincidono con quelli di default (persistence-slotMs)",
				EXPECTED_LE, leValues);
	}
}
