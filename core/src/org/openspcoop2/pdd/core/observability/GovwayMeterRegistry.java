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

package org.openspcoop2.pdd.core.observability;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.micrometer.registry.otlp.OtlpConfig;
import io.micrometer.registry.otlp.OtlpMeterRegistry;
import io.micrometer.core.instrument.Clock;
import org.apache.hc.core5.pool.PoolStats;

import java.util.HashMap;
import java.util.concurrent.ThreadFactory;

import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.DBConsegneMessageBoxManager;
import org.openspcoop2.pdd.config.DBConsegnePreseInCaricoManager;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.DBStatisticheManager;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.QueueManager;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.core.connettori.RepositoryConnettori;
import org.openspcoop2.pdd.core.controllo_traffico.GestoreControlloTraffico;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;
import org.openspcoop2.core.transazioni.utils.TempiElaborazione;
import org.openspcoop2.core.transazioni.utils.TempiElaborazioneFunzionalita;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * GovwayMeterRegistry
 *
 * Singleton che incapsula il registry Micrometer/Prometheus condiviso per l'intera JVM.
 * Viene inizializzato all'avvio (OpenSPCoop2Startup) e utilizzato dal servlet MetricsExporter
 * per lo scrape sull'endpoint '/metrics', nonche' dagli hook che registrano le metriche.
 *
 * Accesso tramite {@link #getInstance()}.
 *
 * @author Burlon Tommaso
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GovwayMeterRegistry {

	/** Unica istanza (singleton) */
	private static final GovwayMeterRegistry INSTANCE = new GovwayMeterRegistry();

	public static GovwayMeterRegistry getInstance() {
		return INSTANCE;
	}

	private GovwayMeterRegistry() {}

	/** Registry composito: tutti i meter vengono pubblicati su tutti i collettori configurati */
	private CompositeMeterRegistry registry = null;

	/** Registry Prometheus (se e' configurato un collettore prometheus): usato da scrape() per l'endpoint /metrics */
	private PrometheusMeterRegistry prometheusRegistry = null;

	/** Registry OTLP push (uno per collettore otel): mantenuti per lo stop/flush allo shutdown */
	private final List<OtlpMeterRegistry> otlpRegistries = new ArrayList<>();

	/** JvmGcMetrics è Closeable: va mantenuto per poterlo rilasciare allo shutdown */
	private JvmGcMetrics jvmGcMetrics = null;

	/** MultiGauge per il numero di elementi nelle cache, aggiornato ad ogni scrape */
	private MultiGauge cacheElements = null;

	/** MultiGauge per lo stato dei pool di connessioni HTTP client, aggiornato ad ogni scrape */
	private MultiGauge httpPoolConnections = null;

	/** SLO per gli istogrammi di persistenza tracciamento: costruiti all'avvio */
	private Duration[] persistenceSlo = null;

	/** Bucket SLO per la latenza, popolati all'init dalla property 'observability.metrics.latency-slotMs' */
	private Duration[] latencySlo = null;

	/** Bucket SLO per la dimensione messaggi, popolati all'init dalla property 'observability.metrics.size-slotByte' */
	private double[] sizeSlo = null;

	private Logger log;

	// Nomi delle metriche esposte
	public static final String METRIC_CACHE_ELEMENTS = "govway_cache_elements";
	public static final String METRIC_HTTP_POOL_CONNECTIONS = "govway_http_pool_connections";
	public static final String METRIC_ACTIVE_TRANSACTIONS = "govway_active_transactions";
	public static final String METRIC_ACTIVE_PROTOCOL_IDS = "govway_active_protocol_ids";
	public static final String METRIC_RATE_LIMITING_ACTIVE_THREADS = "govway_rate_limiting_active_threads";
	public static final String METRIC_PDD_CONGESTED = "govway_pdd_congested";
	public static final String METRIC_ACTIVE_CONNECTORS = "govway_active_connectors";
	public static final String METRIC_DATASOURCE_ALLOCATED_CONNECTIONS = "govway_datasource_allocated_connections";
	public static final String METRIC_QUEUE_ALLOCATED_CONNECTIONS = "govway_queue_allocated_connections";
	public static final String METRIC_REQUESTS_TOTAL = "govway_requests_total";
	public static final String METRIC_REQUEST_DURATION_SECONDS = "govway_request_duration_seconds";
	public static final String METRIC_REQUEST_SIZE_BYTES = "govway_request_size_bytes";
	public static final String METRIC_SERVICE_REQUEST_DURATION_SECONDS = "govway_service_request_duration_seconds";
	public static final String METRIC_SERVICE_REQUEST_SIZE_BYTES = "govway_service_request_size_bytes";
	public static final String METRIC_PROCESSING_PHASE_SECONDS = "govway_processing_phase_seconds";
	public static final String METRIC_TRACING_PERSISTENCE_SECONDS = "govway_tracing_persistence_seconds";
	public static final String METRIC_TRACING_PERSISTENCE_COMPONENTS_SECONDS = "govway_tracing_persistence_components_seconds";
	public static final String METRIC_TRACING_PERSISTENCE_COMPONENTS_DETAILS_SECONDS = "govway_tracing_persistence_components_details_seconds";
	public static final String METRIC_EVENTS_TOTAL = "govway_events_total";

	private static final String PDD_ROLE = "pdd_role";
	private static final String PHASE = "phase";
	/**
	 * Inizializza il registry composito a partire dai collettori configurati
	 * (ObservabilityProperties) e vi collega i binder di default JVM/process.
	 * Idempotente: successive invocazioni non hanno effetto.
	 */
	public synchronized void initialize(Logger startupLog) throws UtilsException {
		if(this.registry!=null) {
			return;
		}

		// logger runtime
		Logger coreLogger = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(coreLogger!=null) {
			this.log = coreLogger;
		}

		CompositeMeterRegistry composite = new CompositeMeterRegistry();

		// Un registry per ciascun collettore con il segnale metrics abilitato
		for (CollectorProperties collector : ObservabilityProperties.getInstance().getCollectorsForMetrics()) {
			addMetricsCollector(composite, collector, startupLog);
		}

		// Metriche di sistema: binder di default JVM/process (attivabili via property)
		if(ObservabilityProperties.getInstance().isMetricsSystemEnabled()) {
			new ClassLoaderMetrics().bindTo(composite);
			new JvmMemoryMetrics().bindTo(composite);
			this.jvmGcMetrics = new JvmGcMetrics();
			this.jvmGcMetrics.bindTo(composite);
			new JvmThreadMetrics().bindTo(composite);
			new ProcessorMetrics().bindTo(composite);
			new UptimeMetrics().bindTo(composite);
			new FileDescriptorMetrics().bindTo(composite);
		}

		// Bucket SLO degli istogrammi latenza/dimensione (da property)
		this.latencySlo = buildLatencySlo();
		this.sizeSlo = buildSizeSlo();

		// Bucket SLO per gli istogrammi di persistenza tracciamento (da property)
		this.persistenceSlo = buildPersistenceSlo();

		// Gauge già campionati da GovWay, letti live al momento dello scrape
		bindGovwayGauges(composite);

		// MultiGauge per il numero di elementi delle cache (popolato ad ogni scrape via refreshCacheMetrics)
		this.cacheElements = MultiGauge.builder(METRIC_CACHE_ELEMENTS)
			.description("Number of elements in each cache")
			.register(composite);

		// MultiGauge per lo stato dei pool di connessioni HTTP client (popolato ad ogni scrape)
		this.httpPoolConnections = MultiGauge.builder(METRIC_HTTP_POOL_CONNECTIONS)
			.description("HTTP client connection pool state")
			.register(composite);

		this.registry = composite;
	}

	/** Crea e aggiunge al composite il registry corrispondente al collettore (segnale metrics). */
	private void addMetricsCollector(CompositeMeterRegistry composite, CollectorProperties collector, Logger startupLog) {
		if(collector.isPrometheus()) {
			PrometheusMeterRegistry prom = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
			composite.add(prom);
			this.prometheusRegistry = prom; // usato da scrape() per l'endpoint /metrics
			startupLog.info("Observability: prometheus collector '{}' active (endpoint {})", collector.getName(), collector.getMetricsEndpoint());
		}
		else if(collector.isOtel()) {
			OtlpMeterRegistry otlp = buildOtlpRegistry(collector);
			composite.add(otlp);
			this.otlpRegistries.add(otlp);
			startupLog.info("Observability: otel collector '{}' active (OTLP push to {}, step {}s)",
				collector.getName(), collector.getMetricsEndpoint(), collector.getMetricsStepSeconds());
		}
	}

	/**
	 * Costruisce un registry OTLP push (HTTP/protobuf) dal collettore. Il costruttore avvia
	 * automaticamente lo scheduler periodico di invio, su un thread daemon dedicato.
	 * Temporalita' CUMULATIVE (compatibile con la semantica dei counter Prometheus).
	 */
	private OtlpMeterRegistry buildOtlpRegistry(CollectorProperties collector) {
		final String url = collector.getMetricsEndpoint();
		final Duration step = Duration.ofSeconds(collector.getMetricsStepSeconds());
		final String collectorName = collector.getName();
		// Header di autenticazione Basic verso il collector, se sono configurate le credenziali
		final Map<String,String> headers = new HashMap<>();
		String basicAuth = collector.getMetricsBasicAuthorizationHeader();
		if(basicAuth!=null) {
			headers.put("Authorization", basicAuth);
		}

		OtlpConfig cfg = new GovwayOtlpConfig(url, step, headers);

		// Thread daemon dedicato e con nome parlante (idioma GovWay, cfr. LocalJtiCacheManager)
		ThreadFactory tf = r -> {
			Thread t = new Thread(r, "govway-otlp-"+collectorName);
			t.setDaemon(true);
			return t;
		};

		// Registry OTLP che, prima di ogni pubblicazione periodica, aggiorna le metriche lette "al volo"
		// (MultiGauge cache/pool) — altrimenti verrebbero inviate con l'ultimo valore campionato allo scrape.
		return new GovwayOtlpMeterRegistry(cfg, Clock.SYSTEM, tf, this, collectorName, url, this.log);
	}

	/** Bucket SLO per la persistenza del tracciamento: dalla property 'observability.metrics.persistence-slotMs' (ms), sempre presente nel file base. */
	private static Duration[] buildPersistenceSlo() throws UtilsException {
		long[] ms = ObservabilityProperties.getInstance().getMetricsPersistenceSlotMs();
		if(ms==null || ms.length==0) {
			throw new UtilsException("Proprieta' 'observability.metrics.persistence-slotMs' non valorizzata");
		}
		Duration[] slo = new Duration[ms.length];
		for (int i=0; i<ms.length; i++) {
			slo[i] = Duration.ofMillis(ms[i]);
		}
		return slo;
	}

	/** Bucket SLO per la latenza: dalla property 'observability.metrics.latency-slotMs' (ms), sempre presente nel file base. */
	private static Duration[] buildLatencySlo() throws UtilsException {
		long[] ms = ObservabilityProperties.getInstance().getMetricsLatencySlotMs();
		if(ms==null || ms.length==0) {
			throw new UtilsException("Proprieta' 'observability.metrics.latency-slotMs' non valorizzata");
		}
		Duration[] slo = new Duration[ms.length];
		for (int i=0; i<ms.length; i++) {
			slo[i] = Duration.ofMillis(ms[i]);
		}
		return slo;
	}

	/** Bucket SLO per la dimensione messaggi: dalla property 'observability.metrics.size-slotByte' (byte), sempre presente nel file base. */
	private static double[] buildSizeSlo() throws UtilsException {
		double[] bytes = ObservabilityProperties.getInstance().getMetricsSizeSlotByte();
		if(bytes==null || bytes.length==0) {
			throw new UtilsException("Proprieta' 'observability.metrics.size-slotByte' non valorizzata");
		}
		return bytes;
	}
	
	public synchronized boolean isInitialized() {
		return this.registry!=null;
	}

	/**
	 * Restituisce l'esposizione testuale delle metriche nel formato Prometheus classico.
	 * Disponibile solo se e' configurato un collettore prometheus (registry dedicato).
	 * I binder di tipo gauge (JVM/process) vengono campionati al momento dello scrape.
	 */
	public String scrape() {
		this.log.debug("Richiesta di scraping delle metriche");
		return this.prometheusRegistry!=null ? this.prometheusRegistry.scrape() : "";
	}

	/**
	 * Registra i gauge le cui misure sono già campionate dalle API interne di GovWay.
	 * I supplier vengono invocati dal registry al momento dello scrape (lettura live);
	 * sono tutti difensivi: in caso di errore restituiscono NaN anziché propagare.
	 */
	private static void bindGovwayGauges(MeterRegistry r) {

		Gauge.builder(METRIC_ACTIVE_TRANSACTIONS, GovwayMeterRegistry::readTransazioniAttive)
			.description("Transactions currently in progress").register(r);

		Gauge.builder(METRIC_ACTIVE_PROTOCOL_IDS, GovwayMeterRegistry::readIdProtocolloAttivi)
			.description("Protocol identifiers in the in-memory anti-duplicate filter").register(r);

		Gauge.builder(METRIC_RATE_LIMITING_ACTIVE_THREADS, GovwayMeterRegistry::readControlloTrafficoThreadAttivi)
			.description("Concurrent requests handled by rate limiting").register(r);

		Gauge.builder(METRIC_PDD_CONGESTED, GovwayMeterRegistry::readPddCongestionata)
			.description("Congestion flag (0/1) of the domain gateway").register(r);

		Gauge.builder(METRIC_ACTIVE_CONNECTORS, GovwayMeterRegistry::readConnettoriPd)
			.description("Connectors with a delivery in progress").tag(PDD_ROLE, "outbound").register(r);
		Gauge.builder(METRIC_ACTIVE_CONNECTORS, GovwayMeterRegistry::readConnettoriPa)
			.tag(PDD_ROLE, "inbound").register(r);

		Gauge.builder(METRIC_DATASOURCE_ALLOCATED_CONNECTIONS, GovwayMeterRegistry::readDatasourceRuntime)
			.description("Allocated DB connections per pool").tag("pool", "runtime").register(r);
		Gauge.builder(METRIC_DATASOURCE_ALLOCATED_CONNECTIONS, GovwayMeterRegistry::readDatasourceTransazioni)
			.tag("pool", "transactions").register(r);
		Gauge.builder(METRIC_DATASOURCE_ALLOCATED_CONNECTIONS, GovwayMeterRegistry::readDatasourceStatistiche)
			.tag("pool", "statistics").register(r);
		Gauge.builder(METRIC_DATASOURCE_ALLOCATED_CONNECTIONS, GovwayMeterRegistry::readDatasourceConsegnePreseInCarico)
			.tag("pool", "scheduled_deliveries").register(r);
		Gauge.builder(METRIC_DATASOURCE_ALLOCATED_CONNECTIONS, GovwayMeterRegistry::readDatasourceConsegneMessageBox)
			.tag("pool", "message_box_deliveries").register(r);

		Gauge.builder(METRIC_QUEUE_ALLOCATED_CONNECTIONS, GovwayMeterRegistry::readQueue)
			.description("Allocated connections to the JMS broker").register(r);
	}

	private static double len(String[] a) {
		return a!=null ? a.length : 0d;
	}

	private static double readTransazioniAttive() {
		try {
			return TransactionContext.getTransactionKeys().size();
		}catch(Exception t) {
			return Double.NaN;
		}
	}

	private static double readIdProtocolloAttivi() {
		try {
			return TransactionContext.getIdBustaKeys().size();
		}catch(Exception t) {
			return Double.NaN;
		}
	}

	private static double readControlloTrafficoThreadAttivi() {
		try {
			return GestoreControlloTraffico.getInstance().sizeActiveThreads();
		}catch(Exception t) {
			return Double.NaN;
		}
	}

	private static double readPddCongestionata() {
		try {
			ConfigurazioneGenerale cg = ConfigurazionePdDManager.getInstance().getConfigurazioneControlloTraffico(null);
			Long maxThreads = cg.getControlloTraffico().getControlloMaxThreadsSoglia();
			Integer threshold = cg.getControlloTraffico().getControlloCongestioneThreshold();
			Boolean congestionata = GestoreControlloTraffico.getInstance().isPortaDominioCongestionata(maxThreads, threshold);
			return (congestionata!=null && congestionata.booleanValue()) ? 1d : 0d;
		}catch(Exception t) {
			return Double.NaN;
		}
	}

	private static double readConnettoriPd() {
		try {
			return RepositoryConnettori.getConnettori_pd().size();
		}catch(Exception t) {
			return Double.NaN;
		}
	}

	private static double readConnettoriPa() {
		try {
			return RepositoryConnettori.getConnettori_pa().size();
		}catch(Exception t) {
			return Double.NaN;
		}
	}

	private static double readDatasourceRuntime() {
		try {
			return len(DBManager.getStatoRisorse());
		}catch(Exception t) {
			return Double.NaN;
		}
	}

	private static double readDatasourceTransazioni() {
		try {
			return len(DBTransazioniManager.getStatoRisorse());
		}catch(Exception t) {
			return Double.NaN;
		}
	}

	private static double readDatasourceStatistiche() {
		try {
			return len(DBStatisticheManager.getStatoRisorse());
		}catch(Exception t) {
			return Double.NaN;
		}
	}

	private static double readDatasourceConsegnePreseInCarico() {
		try {
			return len(DBConsegnePreseInCaricoManager.getStatoRisorseSmistatore());
		}catch(Exception t) {
			return Double.NaN;
		}
	}

	private static double readDatasourceConsegneMessageBox() {
		try {
			return len(DBConsegneMessageBoxManager.getStatoRisorse_runtime());
		}catch(Exception t) {
			return Double.NaN;
		}
	}

	private static double readQueue() {
		try {
			return len(QueueManager.getStatoRisorse());
		}catch(Exception t) {
			return Double.NaN;
		}
	}

	/** Nome della proprieta (su erogazione/fruizione) che abilita l'esposizione delle metriche di dettaglio (per servizio e per fase). */
	public static final String PROPRIETA_METRICHE_DETAILS = "observability.metrics.details";

	/**
	 * Registra le metriche per-transazione (Gruppo B): conteggio richieste, istogramma latenza
	 * (fase totale/servizio/porta) e istogramma dimensione messaggi. Invocato una volta per
	 * transazione dall'hook nel PostOutResponseHandler del controllo del traffico.
	 *
	 * Se {@code perServizio} è true (proprieta {@value #PROPRIETA_METRICHE_DETAILS}=true
	 * sul servizio) emette le stesse misure anche con label service/service_version/action/api/api_version,
	 * sotto nomi metrica dedicati (govway_service_*). La versione del servizio (erogazione/fruizione) e
	 * quella dell'API (accordo parte comune) sono distinte.
	 *
	 * Difensivo: eventuali errori vengono loggati e non propagati.
	 */
	public void recordTransazione(MisurazioniTransazione m, boolean perServizio, String service, String serviceVersion,
			String action, String api, String apiVersion) {
		MeterRegistry r = this.registry;
		if(r==null || m==null) {
			return;
		}
		try {
			String tipoPdd = mapTipoPdD(m.getTipoPdD());
			String protocollo = m.getProtocollo()!=null ? m.getProtocollo() : "";
			String esito = classifyEsito(protocollo, m.getEsitoTransazione());

			Tags base = Tags.of(PDD_ROLE, tipoPdd, "outcome", esito, "protocol", protocollo);

			// Valori calcolati una sola volta
			Long latTotale = diff(m.getDataUscitaRisposta(), m.getDataIngressoRichiesta());
			Long latServizio = diff(m.getDataIngressoRisposta(), m.getDataUscitaRichiesta());
			Long latPorta = latenzaPorta(m);

			// --- Aggregato ---
			Counter.builder(METRIC_REQUESTS_TOTAL)
				.description("Number of handled requests")
				.tags(base)
				.register(r)
				.increment();
			emitLatenze(r, METRIC_REQUEST_DURATION_SECONDS, base, latTotale, latServizio, latPorta);
			emitDimensioni(r, METRIC_REQUEST_SIZE_BYTES, base, m);

			// --- Per servizio (opt-in via proprieta) ---
			if(perServizio) {
				Tags svc = base.and("service", nz(service), "service_version", nz(serviceVersion),
						"action", nz(action), "api", nz(api), "api_version", nz(apiVersion));
				emitLatenze(r, METRIC_SERVICE_REQUEST_DURATION_SECONDS, svc, latTotale, latServizio, latPorta);
				emitDimensioni(r, METRIC_SERVICE_REQUEST_SIZE_BYTES, svc, m);
			}

		}catch(Exception t) {
			this.log.debug("Error while recording transaction metrics: "+t.getMessage(), t);
		}
	}

	private void emitLatenze(MeterRegistry r, String metricName, Tags tags, Long latTotale, Long latServizio, Long latPorta) {
		// fase 'totale' = tempo di elaborazione totale; 'servizio'/'porta' = latenza
		recordLatenza(r, metricName, tags, "total", latTotale);
		recordLatenza(r, metricName, tags, "service", latServizio);
		recordLatenza(r, metricName, tags, "gateway", latPorta);
	}

	private void emitDimensioni(MeterRegistry r, String metricName, Tags tags, MisurazioniTransazione m) {
		recordDimensione(r, metricName, tags, "in_req", m.getRichiestaIngressoBytes());
		recordDimensione(r, metricName, tags, "out_req", m.getRichiestaUscitaBytes());
		recordDimensione(r, metricName, tags, "in_resp", m.getRispostaIngressoBytes());
		recordDimensione(r, metricName, tags, "out_resp", m.getRispostaUscitaBytes());
	}

	private void recordLatenza(MeterRegistry r, String metricName, Tags tags, String fase, Long ms) {
		if(ms==null || ms.longValue()<0) {
			return;
		}
		Timer.builder(metricName)
			.description("Request processing latency")
			.tags(tags).tag(PHASE, fase)
			.serviceLevelObjectives(this.latencySlo)
			.register(r)
			.record(Duration.ofMillis(ms.longValue()));
	}

	private void recordDimensione(MeterRegistry r, String metricName, Tags tags, String direzione, Long bytes) {
		if(bytes==null || bytes.longValue()<0) {
			return;
		}
		DistributionSummary.builder(metricName)
			.description("Message size")
			.baseUnit("bytes")
			.tags(tags).tag("direction", direzione)
			.serviceLevelObjectives(this.sizeSlo)
			.register(r)
			.record(bytes.doubleValue());
	}

	private static String nz(String s) {
		return s!=null ? s : "";
	}

	private static Long diff(Date end, Date start) {
		if(end==null || start==null) {
			return null;
		}
		return end.getTime() - start.getTime();
	}

	private static Long latenzaPorta(MisurazioniTransazione m) {
		Long reqSeg = diff(m.getDataUscitaRichiesta(), m.getDataIngressoRichiesta());
		Long respSeg = diff(m.getDataUscitaRisposta(), m.getDataIngressoRisposta());
		if(reqSeg==null || respSeg==null) {
			return null;
		}
		return reqSeg.longValue() + respSeg.longValue();
	}

	private static String mapTipoPdD(TipoPdD t) {
		if(t==null) {
			return "unknown";
		}
		switch(t) {
			case DELEGATA: return "outbound";
			case APPLICATIVA: return "inbound";
			default: return t.getTipo();
		}
	}

	private String classifyEsito(String protocollo, int esito) {
		try {
			EsitiProperties ep = EsitiProperties.getInstanceFromProtocolName(this.log, protocollo);
			if(ep.getEsitiCodeFaultApplicativo().contains(esito)) {
				return "fault";
			}
			if(ep.getEsitiCodeOk().contains(esito)) {
				return "ok";
			}
			return "ko";
		}catch(Exception t) {
			return "unknown";
		}
	}

	/**
	 * Registra come istogrammi (govway_fase_elaborazione_seconds) le 33 fasi dei tempi di
	 * elaborazione, secondo quanto indicato dall'aggregazione (label da applicare e fasi da
	 * esporre). Invocato dalla persistenza del tracciamento solo quando abilitato via proprietà
	 * del servizio. Difensivo: non propaga eccezioni.
	 */
	public void recordTempiElaborazione(TempiElaborazione te, TempiElaborazioneAggregation aggregation) {
		MeterRegistry r = this.registry;
		if(r==null || te==null || aggregation==null) {
			return;
		}
		try {
			Tags base = toTags(aggregation.getLabels());
			List<String> fasi = aggregation.getFasi();

			recordFase(r, base, fasi, "token", te.getToken());
			recordFase(r, base, fasi, "authentication", te.getAutenticazione());
			recordFase(r, base, fasi, "tokenAuthentication", te.getAutenticazioneToken());
			recordFase(r, base, fasi, "tokenApplicationAuthentication", te.getAutenticazioneApplicativoToken());
			recordFase(r, base, fasi, "authorization", te.getAutorizzazione());
			recordFase(r, base, fasi, "contentAuthorization", te.getAutorizzazioneContenuti());
			recordFase(r, base, fasi, "requestValidation", te.getValidazioneRichiesta());
			recordFase(r, base, fasi, "responseValidation", te.getValidazioneRisposta());
			recordFase(r, base, fasi, "trafficControl_maxRequests", te.getControlloTraffico_maxRequests());
			recordFase(r, base, fasi, "trafficControl_rateLimiting", te.getControlloTraffico_rateLimiting());
			recordFase(r, base, fasi, "requestMessageSecurity", te.getSicurezzaMessaggioRichiesta());
			recordFase(r, base, fasi, "responseMessageSecurity", te.getSicurezzaMessaggioRisposta());
			recordFase(r, base, fasi, "requestAttachmentsHandling", te.getGestioneAttachmentsRichiesta());
			recordFase(r, base, fasi, "responseAttachmentsHandling", te.getGestioneAttachmentsRisposta());
			recordFase(r, base, fasi, "requestApplicationCorrelation", te.getCorrelazioneApplicativaRichiesta());
			recordFase(r, base, fasi, "responseApplicationCorrelation", te.getCorrelazioneApplicativaRisposta());
			recordFase(r, base, fasi, "requestTracing", te.getTracciamentoRichiesta());
			recordFase(r, base, fasi, "responseTracing", te.getTracciamentoRisposta());
			recordFase(r, base, fasi, "dumpRequestInbound", te.getDumpRichiestaIngresso());
			recordFase(r, base, fasi, "dumpRequestOutbound", te.getDumpRichiestaUscita());
			recordFase(r, base, fasi, "dumpResponseInbound", te.getDumpRispostaIngresso());
			recordFase(r, base, fasi, "dumpResponseOutbound", te.getDumpRispostaUscita());
			recordFase(r, base, fasi, "dumpBinaryRequestInbound", te.getDumpBinarioRichiestaIngresso());
			recordFase(r, base, fasi, "dumpBinaryRequestOutbound", te.getDumpBinarioRichiestaUscita());
			recordFase(r, base, fasi, "dumpBinaryResponseInbound", te.getDumpBinarioRispostaIngresso());
			recordFase(r, base, fasi, "dumpBinaryResponseOutbound", te.getDumpBinarioRispostaUscita());
			recordFase(r, base, fasi, "dumpIntegrationManager", te.getDumpIntegrationManager());
			recordFase(r, base, fasi, "responseCachingDigestComputation", te.getResponseCachingCalcoloDigest());
			recordFase(r, base, fasi, "responseCachingReadFromCache", te.getResponseCachingReadFromCache());
			recordFase(r, base, fasi, "responseCachingSaveInCache", te.getResponseCachingSaveInCache());
			recordFase(r, base, fasi, "requestTransformation", te.getTrasformazioneRichiesta());
			recordFase(r, base, fasi, "responseTransformation", te.getTrasformazioneRisposta());
			recordFase(r, base, fasi, "attributeAuthority", te.getAttributeAuthority());

		}catch(Exception t) {
			this.log.debug("Error while recording processing-times metrics: "+t.getMessage(), t);
		}
	}

	private void recordFase(MeterRegistry r, Tags base, List<String> fasi, String fase, TempiElaborazioneFunzionalita f) {
		if(f==null) {
			return;
		}
		if(fasi!=null && !fasi.isEmpty() && !fasi.contains(fase)) {
			return;
		}
		long lat = f.getLatenza();
		if(lat<0) {
			return;
		}
		Timer.builder(METRIC_PROCESSING_PHASE_SECONDS)
			.description("Latency per processing phase")
			.tags(base).tag(PHASE, fase)
			.serviceLevelObjectives(this.latencySlo)
			.register(r)
			.record(Duration.ofMillis(lat));
	}

	private static Tags toTags(Map<String,String> labels) {
		if(labels==null || labels.isEmpty()) {
			return Tags.empty();
		}
		List<Tag> l = new ArrayList<>();
		for(Map.Entry<String,String> e : labels.entrySet()) {
			l.add(Tag.of(e.getKey(), e.getValue()!=null ? e.getValue() : ""));
		}
		return Tags.of(l);
	}

	// Invocata sia dallo scrape '/metrics' sia dal ciclo di publish di ogni collettore OTLP:
	// synchronized per evitare re-registrazioni concorrenti delle MultiGauge.
	public synchronized void refresh() {
		refreshCacheMetrics();
		refreshHttpPoolMetrics();
	}
	
	/**
	 * Aggiorna il MultiGauge del numero di elementi delle cache leggendo il registro centrale.
	 * Va invocato prima dello scrape (dal MetricsExporter). Economico: solo getItemCount().
	 */
	public void refreshCacheMetrics() {
		MultiGauge mg = this.cacheElements;
		if(mg==null) {
			return;
		}
		try {
			Map<String,Integer> counts = Cache.getItemCountByCache();
			List<MultiGauge.Row<?>> rows = new ArrayList<>();
			for (Map.Entry<String,Integer> e : counts.entrySet()) {
				rows.add(MultiGauge.Row.of(Tags.of("cache", e.getKey()!=null ? e.getKey() : ""), e.getValue()));
			}
			mg.register(rows, true);
		}catch(Exception t) {
			this.log.debug("Error while refreshing cache metrics: "+t.getMessage(), t);
		}
	}

	/**
	 * Aggiorna il MultiGauge dello stato dei pool di connessioni HTTP client, aggregando per
	 * 'mode' (bio/nio) le statistiche di tutti i pool. Va invocato prima dello scrape.
	 */
	private void refreshHttpPoolMetrics() {
		MultiGauge mg = this.httpPoolConnections;
		if(mg==null) {
			return;
		}
		try {
			List<MultiGauge.Row<?>> rows = new ArrayList<>();
			addHttpPoolRows(rows, "bio", org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCOREConnectionManager.getPoolsStats());
			addHttpPoolRows(rows, "nio", org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPCOREConnectionManager.getPoolsStats());
			mg.register(rows, true);
		}catch(Exception t) {
			this.log.debug("Error while refreshing http pool metrics: "+t.getMessage(), t);
		}
	}

	/** Somma le PoolStats dei pool di un dato mode ed aggiunge una riga per ciascuno stato (leased/pending/available/max). */
	private static void addHttpPoolRows(List<MultiGauge.Row<?>> rows, String mode, List<PoolStats> pools) {
		long leased = 0;
		long pending = 0;
		long available = 0;
		long max = 0;
		if(pools!=null) {
			for (PoolStats ps : pools) {
				if(ps!=null) {
					leased += ps.getLeased();
					pending += ps.getPending();
					available += ps.getAvailable();
					max += ps.getMax();
				}
			}
		}
		final String STATE = "state";
		rows.add(MultiGauge.Row.of(Tags.of("mode", mode, STATE, "leased"), leased));
		rows.add(MultiGauge.Row.of(Tags.of("mode", mode, STATE, "pending"), pending));
		rows.add(MultiGauge.Row.of(Tags.of("mode", mode, STATE, "available"), available));
		rows.add(MultiGauge.Row.of(Tags.of("mode", mode, STATE, "max"), max));
	}

	/**
	 * Registra gli istogrammi della persistenza del tracciamento su DB (Gruppo slow-log):
	 * la durata totale e i sotto-tempi che la compongono. Invocato dal TracciamentoManager
	 * solo quando lo slow-log è abilitato (i tempi esistono solo in quella modalità).
	 * Difensivo: non propaga eccezioni.
	 */
	public void recordTracciamentoPersistenza(String phase, long totalMs, Map<String,Long> aggregatedComponents, Map<String,Long> detailComponents) {
		MeterRegistry r = this.registry;
		if(r==null) {
			return;
		}
		try {
			Tags base = Tags.of(PHASE, nz(phase));

			if(totalMs>=0) {
				Timer.builder(METRIC_TRACING_PERSISTENCE_SECONDS)
					.description("Total duration of the tracing persistence to DB")
					.tags(base)
					.serviceLevelObjectives(this.persistenceSlo)
					.register(r)
					.record(Duration.ofMillis(totalMs));
			}

			// Componenti aggregate (fillTransaction, checkTraffic, writeDatabase)
			emitTracingComponents(r, METRIC_TRACING_PERSISTENCE_COMPONENTS_SECONDS,
				"Duration of the aggregated tracing persistence components", base, aggregatedComponents);

			// Componenti di dettaglio (tutte le componenti non aggregate)
			emitTracingComponents(r, METRIC_TRACING_PERSISTENCE_COMPONENTS_DETAILS_SECONDS,
				"Duration of the detailed tracing persistence components", base, detailComponents);
		}catch(Exception t) {
			this.log.debug("Error while recording tracing persistence metrics: "+t.getMessage(), t);
		}
	}

	/** Registra un istogramma per ciascuna componente misurata (tag 'component' = nome componente). */
	private void emitTracingComponents(MeterRegistry r, String metricName, String description, Tags base, Map<String,Long> components) {
		if(components==null) {
			return;
		}
		for (Map.Entry<String,Long> e : components.entrySet()) {
			if(e.getValue()!=null && e.getValue().longValue()>=0) {
				Timer.builder(metricName)
					.description(description)
					.tags(base).tag("component", e.getKey())
					.serviceLevelObjectives(this.persistenceSlo)
					.register(r)
					.record(Duration.ofMillis(e.getValue().longValue()));
			}
		}
	}

	
	private String convertSeverity(int severity) {
		try {
			return SeveritaConverter.toSeverita(severity).getValue();
		}catch(Exception t) {
			return String.valueOf(severity);
		}
	}
	/**
	 * Registra il conteggio degli eventi (govway_eventi_total) per tipo/codice/severita/nodo.
	 * Invocato dal GestoreEventi ad ogni evento persistito. Difensivo: non propaga eccezioni.
	 */
	public void recordEvento(String tipo, String codice, int severita, String clusterId) {
		MeterRegistry r = this.registry;
		if(r==null) {
			return;
		}
		try {
			String sev = convertSeverity(severita);
			Counter.builder(METRIC_EVENTS_TOTAL)
				.description("Number of recorded events")
				.tag("type", nz(tipo))
				.tag("code", nz(codice))
				.tag("severity", nz(sev))
				.tag("cluster_id", nz(clusterId))
				.register(r)
				.increment();
		}catch(Exception t) {
			this.log.debug("Error while recording event metric: "+t.getMessage(), t);
		}
	}

	public synchronized void close() {
		if(this.jvmGcMetrics!=null) {
			this.jvmGcMetrics.close();
			this.jvmGcMetrics = null;
		}
		// Chiusura dei registry OTLP: ferma lo scheduler ed esegue un ultimo flush
		for (OtlpMeterRegistry otlp : this.otlpRegistries) {
			try {
				otlp.close();
			}catch(Exception t) {
				this.log.debug("Error while closing OTLP registry: "+t.getMessage(), t);
			}
		}
		this.otlpRegistries.clear();
		if(this.prometheusRegistry!=null) {
			this.prometheusRegistry.close();
			this.prometheusRegistry = null;
		}
		if(this.registry!=null) {
			this.registry.close();
			this.registry = null;
		}
		this.cacheElements = null;
		this.httpPoolConnections = null;
	}
}
