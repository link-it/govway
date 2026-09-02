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

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.observability.ErogazioneTestMetricsUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.observability.PrometheusMetricsUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.Utilities;

/**
 * ProcessingPhaseMetricsTest
 *
 * La risorsa 'phases' dell'API TestMetrics abilita il tracciamento dei tempi di
 * elaborazione: le sue invocazioni devono aggiornare gli istogrammi per-fase
 * (govway_processing_phase_seconds). Questo test verifica SOLO la presenza di tali metriche
 * (per erogazione e per fruizione); l'assenza per le altre risorse e' verificata da altri test.
 *
 * @author Burlon Tommaso
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProcessingPhaseMetricsTest extends ConfigLoader {

	private static final String OP_PHASES = "phases";
	private static final int CALLS = 3;

	private static final String METRIC_PROCESSING_PHASE_COUNT = "govway_processing_phase_seconds_count";

	// Polling: fino a ~15s con backoff, in linea con l'approccio dei test su fase PostOutResponse
	private static final int[] POLL_BACKOFF_MS = { 200, 300, 500, 1000, 1000, 2000, 2000, 3000, 5000 };

	@Test
	public void istogrammiPerFasePresentiErogazione() throws Exception {
		test(TipoServizio.EROGAZIONE);
	}

	@Test
	public void istogrammiPerFasePresentiFruizione() throws Exception {
		test(TipoServizio.FRUIZIONE);
	}

	private void test(TipoServizio tipo) throws Exception {

		String metricsUrl = ErogazioneTestMetricsUtils.metricsUrl();
		// Filtro per service + pdd_role + versioni: verifica che le metriche siano quelle attese
		// per il tipo di servizio (erogazione=inbound/v2 / fruizione=outbound/v1), con api_version=1
		Map<String,String> filtro = Map.of(
				"service", ErogazioneTestMetricsUtils.api(tipo),
				"pdd_role", ErogazioneTestMetricsUtils.pddRole(tipo),
				"service_version", ErogazioneTestMetricsUtils.serviceVersion(tipo),
				"api_version", ErogazioneTestMetricsUtils.apiVersion(tipo));

		// 1) Scrape iniziale (l'endpoint puo' contenere gia' dati preesistenti)
		PrometheusMetricsUtils before = PrometheusMetricsUtils.scrape(metricsUrl);

		// 2) Invocazioni verso la risorsa che abilita il tracciamento dei tempi di elaborazione
		ErogazioneTestMetricsUtils.invoke(tipo, OP_PHASES, CALLS, getLoggerCore());

		// 3) Scrape finale con polling: le metriche sono registrate in modo asincrono (PostOutResponse)
		double deltaFasi = 0;
		PrometheusMetricsUtils after = null;
		for (int i = 0; i < POLL_BACKOFF_MS.length; i++) {
			Utilities.sleep(POLL_BACKOFF_MS[i]);
			after = PrometheusMetricsUtils.scrape(metricsUrl);
			deltaFasi = PrometheusMetricsUtils.delta(before, after, METRIC_PROCESSING_PHASE_COUNT, filtro);
			if(deltaFasi > 0) {
				getLoggerCore().info("[{}] Istogrammi per-fase registrati dopo {} tentativi (delta fasi={})", tipo, i+1, deltaFasi);
				break;
			}
		}

		// 4) Verifica: gli istogrammi per-fase sono stati aggiornati dalle chiamate a 'phases'
		assertTrue("["+tipo+"] Gli istogrammi per-fase ('"+METRIC_PROCESSING_PHASE_COUNT+"') devono essere aggiornati dalla risorsa 'phases'",
				deltaFasi > 0);
	}
}
