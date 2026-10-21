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
package org.openspcoop2.pdd.core.observability.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.pdd.core.observability.GovwayMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.prometheus.metrics.model.registry.PrometheusRegistry;

/**
 * Verifica che le metriche per-transazione di {@link GovwayMeterRegistry}, registrate tramite i meter in cache,
 * producano la stessa esposizione Prometheus dell'algoritmo originale (builder e registrazione ad ogni invocazione),
 * anche con label per servizio e con invocazioni concorrenti.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MeterCacheTest {

	public static void main(String[] args) throws Exception {
		test();
	}

	private static final Logger LOG = LoggerFactory.getLogger(MeterCacheTest.class);

	private static final Duration[] LATENCY_SLO = toDurations(5,10,25,50,100,250,500,1000,2500,5000,10000);
	private static final Duration[] PERSISTENCE_SLO = toDurations(5,10,25,50,100,250,500,1000,2500,5000,10000);
	private static final double[] SIZE_SLO = {256,1024,4096,16384,65536,262144,1048576,4194304,16777216};

	private static int verifiche = 0;

	public static void test() throws Exception {
		verifiche = 0;

		// 1) stesse invocazioni: implementazione con cache vs algoritmo originale
		MockClock clock = new MockClock();
		PrometheusMeterRegistry promAtteso = newPrometheus(clock);
		CompositeMeterRegistry atteso = newComposite(clock, promAtteso);
		PrometheusMeterRegistry promOttenuto = newPrometheus(clock);
		GovwayMeterRegistry gmr = newGovwayMeterRegistry(newComposite(clock, promOttenuto));

		for (int i = 0; i < 2000; i++) {
			MisurazioniTransazione m = misurazione(i);
			int httpStatus = (i % 7 == 0) ? 500 : 200;
			Map<String,String> serviceLabels = (i % 3 == 0) ? serviceLabels(i) : null;
			String resultClass = classifyEsito(gmr, m);
			gmr.recordTransazione(m, httpStatus, serviceLabels);
			recordTransazioneOriginale(atteso, m, httpStatus, serviceLabels, resultClass);

			String phase = (i % 2 == 0) ? "POST_OUT_RESPONSE" : "OUT_RESPONSE";
			Map<String,Long> aggregated = components(i, "fillTransaction", "checkTraffic", "writeDatabase");
			Map<String,Long> detailed = components(i, "fillTransaction", "getConnection", "insertTransaction", "commit");
			gmr.recordTracciamentoPersistenza(phase, i % 50, aggregated, detailed);
			recordTracciamentoPersistenzaOriginale(atteso, phase, i % 50, aggregated, detailed);
		}
		String scrape = promOttenuto.scrape();
		check("esposizione prometheus", promAtteso.scrape(), scrape);
		// il confronto riguarda tutte le famiglie di metriche per-transazione
		for (String metrica : new String[] {"govway_requests_total", "govway_request_duration_seconds", "govway_request_size_bytes",
				"govway_service_requests_total", "govway_service_request_duration_seconds", "govway_service_request_size_bytes",
				"govway_tracing_persistence_seconds", "govway_tracing_persistence_components_seconds", "govway_tracing_persistence_components_details_seconds"}) {
			check("metrica presente: "+metrica, "true", String.valueOf(scrape.contains(metrica)));
		}

		// 2) invocazioni concorrenti: i contatori non devono perdere incrementi
		PrometheusMeterRegistry promConcorrente = newPrometheus(clock);
		CompositeMeterRegistry compositeConcorrente = newComposite(clock, promConcorrente);
		GovwayMeterRegistry gmrConcorrente = newGovwayMeterRegistry(compositeConcorrente);
		int threads = 8;
		int perThread = 5000;
		ExecutorService es = Executors.newFixedThreadPool(threads);
		List<Future<?>> futures = new ArrayList<>();
		for (int t = 0; t < threads; t++) {
			final int offset = t;
			futures.add(es.submit(() -> {
				for (int i = 0; i < perThread; i++) {
					MisurazioniTransazione m = misurazione(i + offset);
					gmrConcorrente.recordTransazione(m, 200, (i % 2 == 0) ? serviceLabels(i) : null);
					gmrConcorrente.recordTracciamentoPersistenza("POST_OUT_RESPONSE", 3, null, components(i, "commit"));
				}
			}));
		}
		for (Future<?> f : futures) {
			f.get();
		}
		es.shutdown();
		double totale = 0;
		for (Counter c : compositeConcorrente.find("govway_requests_total").counters()) {
			totale += c.count();
		}
		check("richieste conteggiate (concorrenza)", String.valueOf((double)threads*perThread), String.valueOf(totale));
		long persistenza = 0;
		for (Timer t : compositeConcorrente.find("govway_tracing_persistence_seconds").timers()) {
			persistenza += t.count();
		}
		check("persistenze conteggiate (concorrenza)", String.valueOf((long)threads*perThread), String.valueOf(persistenza));

		System.out.println("Test cache dei meter completato: "+verifiche+" verifiche effettuate");
	}

	private static void check(String nome, String atteso, String ottenuto) throws Exception {
		if(!atteso.equals(ottenuto)) {
			throw new Exception("Verifica '"+nome+"' fallita:\n--- atteso ---\n"+atteso+"\n--- ottenuto ---\n"+ottenuto);
		}
		verifiche++;
	}

	private static Duration[] toDurations(long... ms) {
		Duration[] d = new Duration[ms.length];
		for (int i = 0; i < ms.length; i++) {
			d[i] = Duration.ofMillis(ms[i]);
		}
		return d;
	}

	private static PrometheusMeterRegistry newPrometheus(MockClock clock) {
		return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT, new PrometheusRegistry(), clock);
	}
	private static CompositeMeterRegistry newComposite(MockClock clock, MeterRegistry child) {
		CompositeMeterRegistry composite = new CompositeMeterRegistry(clock);
		composite.add(child);
		return composite;
	}

	/** Istanza con il registry indicato, senza la configurazione di GovWay (proprieta' e collettori) */
	private static GovwayMeterRegistry newGovwayMeterRegistry(CompositeMeterRegistry composite) throws Exception {
		Constructor<GovwayMeterRegistry> c = GovwayMeterRegistry.class.getDeclaredConstructor(Logger.class);
		c.setAccessible(true);
		GovwayMeterRegistry gmr = c.newInstance(LOG);
		setField(gmr, "registry", composite);
		setField(gmr, "latencySlo", LATENCY_SLO);
		setField(gmr, "persistenceSlo", PERSISTENCE_SLO);
		setField(gmr, "sizeSlo", SIZE_SLO);
		setField(gmr, "active", true);
		return gmr;
	}
	private static void setField(Object o, String name, Object value) throws Exception {
		Field f = GovwayMeterRegistry.class.getDeclaredField(name);
		f.setAccessible(true);
		f.set(o, value);
	}
	private static String classifyEsito(GovwayMeterRegistry gmr, MisurazioniTransazione m) throws Exception {
		Method method = GovwayMeterRegistry.class.getDeclaredMethod("classifyEsito", String.class, int.class);
		method.setAccessible(true);
		return (String) method.invoke(gmr, m.getProtocollo()!=null ? m.getProtocollo() : "", m.getEsitoTransazione());
	}

	private static MisurazioniTransazione misurazione(int i) {
		MisurazioniTransazione m = new MisurazioniTransazione();
		m.setProtocollo((i % 4 == 0) ? "modipa" : "trasparente");
		m.setEsitoTransazione(i % 5);
		m.setTipoPdD((i % 2 == 0) ? TipoPdD.APPLICATIVA : TipoPdD.DELEGATA);
		long t0 = 1_000_000L + i;
		m.setDataIngressoRichiesta(new Date(t0));
		m.setDataUscitaRichiesta(new Date(t0 + (i % 3)));
		m.setDataIngressoRisposta(new Date(t0 + (i % 3) + (i % 40)));
		m.setDataUscitaRisposta(new Date(t0 + (i % 3) + (i % 40) + (i % 2)));
		m.setRichiestaIngressoBytes((long)(i % 5000));
		m.setRichiestaUscitaBytes((i % 11 == 0) ? null : (long)(i % 3000));
		m.setRispostaIngressoBytes((long)(i % 70000));
		m.setRispostaUscitaBytes((long)(i % 70000));
		return m;
	}
	private static Map<String,String> serviceLabels(int i) {
		Map<String,String> l = new LinkedHashMap<>();
		l.put("interface_id", "gw_ENTE/gw_Servizio"+(i % 4)+"/v1");
		l.put("group", (i % 2 == 0) ? "Predefinito" : "Gruppo1");
		l.put("provider_type", "gw");
		l.put("provider", "ENTE");
		l.put("service", "Servizio"+(i % 4));
		l.put("service_version", "1");
		l.put("action", (i % 5 == 0) ? null : "azione"+(i % 6));
		l.put("tags", "");
		return l;
	}
	private static Map<String,Long> components(int i, String... nomi) {
		Map<String,Long> m = new LinkedHashMap<>();
		for (int j = 0; j < nomi.length; j++) {
			m.put(nomi[j], (long)((i + j) % 30));
		}
		return m;
	}

	/* ---- Algoritmo originale (builder e registrazione ad ogni invocazione) ---- */

	private static void recordTransazioneOriginale(MeterRegistry r, MisurazioniTransazione m, int httpStatus, Map<String,String> serviceLabels, String resultClass) {
		String tipoPdd = TipoPdD.DELEGATA.equals(m.getTipoPdD()) ? "outbound" : "inbound";
		String protocollo = m.getProtocollo()!=null ? m.getProtocollo() : "";
		Tags base = Tags.of("role", tipoPdd, "result_class", resultClass, "protocol", protocollo);
		Tags result = Tags.of("result_code", String.valueOf(m.getEsitoTransazione()),
				"http_status", httpStatus>0 ? String.valueOf(httpStatus) : "");
		Long latTotale = diff(m.getDataUscitaRisposta(), m.getDataIngressoRichiesta());
		Long latServizio = diff(m.getDataIngressoRisposta(), m.getDataUscitaRichiesta());
		Long reqSeg = diff(m.getDataUscitaRichiesta(), m.getDataIngressoRichiesta());
		Long respSeg = diff(m.getDataUscitaRisposta(), m.getDataIngressoRisposta());
		Long latPorta = (reqSeg==null || respSeg==null) ? null : reqSeg + respSeg;

		Counter.builder("govway_requests_total").description("Number of handled requests").tags(base.and(result)).register(r).increment();
		emitLatenze(r, "govway_request_duration_seconds", base, latTotale, latServizio, latPorta);
		emitDimensioni(r, "govway_request_size_bytes", base, m);
		if(serviceLabels!=null && !serviceLabels.isEmpty()) {
			List<Tag> l = new ArrayList<>();
			for(Map.Entry<String,String> e : serviceLabels.entrySet()) {
				l.add(Tag.of(e.getKey(), e.getValue()!=null ? e.getValue() : ""));
			}
			Tags svc = base.and(Tags.of(l));
			Counter.builder("govway_service_requests_total").description("Number of handled requests per service").tags(svc.and(result)).register(r).increment();
			emitLatenze(r, "govway_service_request_duration_seconds", svc, latTotale, latServizio, latPorta);
			emitDimensioni(r, "govway_service_request_size_bytes", svc, m);
		}
	}
	private static void emitLatenze(MeterRegistry r, String name, Tags tags, Long tot, Long svc, Long porta) {
		recordLatenza(r, name, tags, "total", tot);
		recordLatenza(r, name, tags, "service", svc);
		recordLatenza(r, name, tags, "gateway", porta);
	}
	private static void recordLatenza(MeterRegistry r, String name, Tags tags, String fase, Long ms) {
		if(ms==null || ms<0) {
			return;
		}
		Timer.builder(name).description("Request processing latency").tags(tags).tag("phase", fase)
			.serviceLevelObjectives(LATENCY_SLO).register(r).record(Duration.ofMillis(ms));
	}
	private static void emitDimensioni(MeterRegistry r, String name, Tags tags, MisurazioniTransazione m) {
		recordDimensione(r, name, tags, "in_req", m.getRichiestaIngressoBytes());
		recordDimensione(r, name, tags, "out_req", m.getRichiestaUscitaBytes());
		recordDimensione(r, name, tags, "in_resp", m.getRispostaIngressoBytes());
		recordDimensione(r, name, tags, "out_resp", m.getRispostaUscitaBytes());
	}
	private static void recordDimensione(MeterRegistry r, String name, Tags tags, String dir, Long bytes) {
		if(bytes==null || bytes<0) {
			return;
		}
		DistributionSummary.builder(name).description("Message size").baseUnit("bytes").tags(tags).tag("direction", dir)
			.serviceLevelObjectives(SIZE_SLO).register(r).record(bytes.doubleValue());
	}
	private static void recordTracciamentoPersistenzaOriginale(MeterRegistry r, String phase, long totalMs, Map<String,Long> aggregated, Map<String,Long> detailed) {
		Tags base = Tags.of("phase", phase!=null ? phase : "");
		if(totalMs>=0) {
			Timer.builder("govway_tracing_persistence_seconds").description("Total duration of the tracing persistence to DB").tags(base)
				.serviceLevelObjectives(PERSISTENCE_SLO).register(r).record(Duration.ofMillis(totalMs));
		}
		emitComponents(r, "govway_tracing_persistence_components_seconds", "Duration of the aggregated tracing persistence components", base, aggregated);
		emitComponents(r, "govway_tracing_persistence_components_details_seconds", "Duration of the detailed tracing persistence components", base, detailed);
	}
	private static void emitComponents(MeterRegistry r, String name, String description, Tags base, Map<String,Long> components) {
		if(components==null) {
			return;
		}
		for (Map.Entry<String,Long> e : components.entrySet()) {
			if(e.getValue()!=null && e.getValue()>=0) {
				Timer.builder(name).description(description).tags(base).tag("component", e.getKey())
					.serviceLevelObjectives(PERSISTENCE_SLO).register(r).record(Duration.ofMillis(e.getValue()));
			}
		}
	}
	private static Long diff(Date end, Date start) {
		if(end==null || start==null) {
			return null;
		}
		return end.getTime() - start.getTime();
	}
}
