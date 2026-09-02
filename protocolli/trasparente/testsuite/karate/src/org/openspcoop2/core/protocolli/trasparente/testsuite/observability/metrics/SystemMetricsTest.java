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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.observability.ErogazioneTestMetricsUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.observability.PrometheusMetricsUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.Utilities;

/**
 * SystemMetricsTest
 *
 * Verifica che l'endpoint '/metrics' esponga le metriche "di sistema": sia i binder di default
 * JVM/process di Micrometer, sia i gauge specifici di GovWay (transazioni attive, thread del
 * rate limiting, connessioni DB allocate, ecc.). Prima dello scrape viene effettuata una
 * chiamata all'erogazione, cosi' da garantire che il registry sia stato popolato.
 * Per i gauge di GovWay si verifica solo la presenza (nessun controllo sul valore).
 *
 * @author Burlon Tommaso
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SystemMetricsTest extends ConfigLoader {

	/**
	 * Una metrica rappresentativa per ciascun binder di default abilitato in GovwayMeterRegistry:
	 *  - jvm_classes_loaded_classes   -> ClassLoaderMetrics
	 *  - jvm_memory_used_bytes        -> JvmMemoryMetrics
	 *  - jvm_threads_live_threads     -> JvmThreadMetrics
	 *  - system_cpu_count             -> ProcessorMetrics
	 *  - process_uptime_seconds       -> UptimeMetrics
	 */
	private static final List<String> REQUIRED_JVM_METRICS = Arrays.asList(
			"jvm_classes_loaded_classes",
			"jvm_memory_used_bytes",
			"jvm_threads_live_threads",
			"system_cpu_count",
			"process_uptime_seconds"
	);

	/**
	 * Gauge specifici di GovWay (Gruppo A) collegati in GovwayMeterRegistry: se ne verifica
	 * solo la presenza (thread attivi, connessioni DB/queue allocate, connettori, ecc.).
	 */
	private static final List<String> REQUIRED_GOVWAY_METRICS = Arrays.asList(
			"govway_active_transactions",
			"govway_active_protocol_ids",
			"govway_rate_limiting_active_threads",
			"govway_pdd_congested",
			"govway_active_connectors",
			"govway_datasource_allocated_connections",
			"govway_queue_allocated_connections"
	);

	private static PrometheusMetricsUtils metrics;

	@BeforeClass
	public static void invokeAndScrape() throws Exception {
		// ConfigLoader.setupProperties() (@BeforeClass della superclasse) ha gia' popolato le System properties.
		// Effettuo prima una chiamata all'erogazione, cosi' da essere certi che il registry contenga dei dati.
		ErogazioneTestMetricsUtils.invoke(TipoServizio.EROGAZIONE, "info1", 1, getLoggerCore());

		String url = ErogazioneTestMetricsUtils.metricsUrl();
		getLoggerCore().info("Scrape delle metriche da: {}", url);
		metrics = PrometheusMetricsUtils.scrape(url);
		getLoggerCore().info("Metriche esposte: {} serie, {} nomi distinti", metrics.samples().size(), metrics.names().size());
	}

	@Test
	public void defaultSystemMetricsArePresent() {
		assertFalse("Lo scrape dell'endpoint '/metrics' non ha restituito alcuna metrica", metrics.names().isEmpty());
		assertPresenti("Metriche di default JVM/process assenti dall'endpoint '/metrics': ", REQUIRED_JVM_METRICS);
	}

	@Test
	public void govwaySystemMetricsArePresent() {
		assertPresenti("Metriche di sistema GovWay assenti dall'endpoint '/metrics': ", REQUIRED_GOVWAY_METRICS);
	}

	/**
	 * Lo stato dei pool di connessioni HTTP client viene esposto (aggregato per mode/state) ad ogni
	 * scrape: la serie {mode="bio", state="max"} deve essere sempre presente (valore 0 se il pool
	 * BIO non e' utilizzato). La label 'job' viene aggiunta da Prometheus in fase di scrape e non e'
	 * presente nell'esposizione grezza, quindi non fa parte del filtro.
	 */
	@Test
	public void httpPoolConnectionsMetricPresent() {
		String metrica = "govway_http_pool_connections";
		assertTrue("La serie '"+metrica+"{mode=\"bio\",state=\"max\"}' deve essere presente sull'endpoint '/metrics'",
				metrics.value(metrica, Map.of("mode", "bio", "state", "max")).isPresent());
	}

	private static void assertPresenti(String messaggio, List<String> attese) {
		List<String> missing = new ArrayList<>();
		for (String metricName : attese) {
			if(!metrics.contains(metricName)) {
				missing.add(metricName);
			}
		}
		assertTrue(messaggio + missing, missing.isEmpty());
	}

	private static final String METRIC_CACHE_ELEMENTS = "govway_cache_elements";
	private static final int[] POLL_BACKOFF_MS = { 200, 300, 500, 1000, 1000, 2000 };

	// Cache di configurazione delle porte: popolata dalle invocazioni business e NON ricaricata
	// dalla richiesta di scrape '/metrics' (a differenza delle cache 'gestoreRichieste-*', che
	// vengono ripopolate proprio dalla richiesta di scrape). E' quindi quella adatta a verificare
	// che la pulizia della cache si rifletta a zero sulla metrica.
	private static final java.util.Map<String,String> CACHE_CONFIGURAZIONE = java.util.Map.of("cache", "configurazionePdD");

	/**
	 * Dopo un'invocazione la metrica govway_cache_elements deve essere presente e con elementi;
	 * la pulizia delle cache deve ridurne il contenuto.
	 *
	 * Nota: non si puo' verificare che il totale torni esattamente a zero perche' la stessa
	 * richiesta di scrape '/metrics' (necessaria per leggere la metrica) ripopola alcune cache
	 * (es. 'gestoreRichieste-*' e un paio di voci di 'configurazionePdD'). Si verifica quindi che
	 * il reset abbia ridotto significativamente gli elementi in cache.
	 */
	@Test
	public void cacheMetrics() throws Exception {
		String metricsUrl = ErogazioneTestMetricsUtils.metricsUrl();

		// 1) Invocazione: popola le cache (es. configurazionePdD, gestoreRichieste, ...)
		ErogazioneTestMetricsUtils.invoke(TipoServizio.EROGAZIONE, "info1", 1, getLoggerCore());

		PrometheusMetricsUtils dopoInvocazione = PrometheusMetricsUtils.scrape(metricsUrl);
		assertTrue("La metrica '"+METRIC_CACHE_ELEMENTS+"' deve essere presente dopo un'invocazione",
				dopoInvocazione.contains(METRIC_CACHE_ELEMENTS));
		double elementiPrima = dopoInvocazione.sum(METRIC_CACHE_ELEMENTS, CACHE_CONFIGURAZIONE);
		assertTrue("La cache di configurazione deve contenere elementi dopo un'invocazione (trovati: "+elementiPrima+")",
				elementiPrima > 0);

		// 2) Pulizia di tutte le cache (via JMX /check, come negli altri test)
		resetCache();

		// 3) Dopo la pulizia gli elementi della cache di configurazione devono diminuire
		double elementiDopo = elementiPrima;
		for (int i = 0; i < POLL_BACKOFF_MS.length; i++) {
			Utilities.sleep(POLL_BACKOFF_MS[i]);
			elementiDopo = PrometheusMetricsUtils.scrape(metricsUrl).sum(METRIC_CACHE_ELEMENTS, CACHE_CONFIGURAZIONE);
			if(elementiDopo < elementiPrima) {
				break;
			}
		}
		assertTrue("La pulizia delle cache deve ridurre gli elementi della cache 'configurazionePdD' "
				+ "(prima="+elementiPrima+", dopo="+elementiDopo+")", elementiDopo < elementiPrima);
	}
}
