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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.InstanceProperties;
import org.slf4j.Logger;

/**
 * ObservabilityProperties
 *
 * Legge la configurazione dell'observability (file 'govway.observability.properties',
 * ridefinibile tramite 'govway_local.observability.properties'). All'inizializzazione
 * i collettori vengono parsati, validati e memorizzati (nessun lazy loading):
 * un errore di configurazione fa fallire subito l'avvio.
 *
 * Struttura:
 *   observability.collectors=<lista>
 *   observability.collector.<name>.type=(prometheus|otel)
 *   observability.collector.<name>.<signal>.<prop>=<value>
 * con default (validi per tutti i collettori) a livello di segnale:
 *   observability.<signal>.<prop>=<value>
 *
 * @author Burlon Tommaso
 * @version $Rev$, $Date$
 */
public class ObservabilityProperties extends InstanceProperties {

	private static final String PROPERTIES = "OBSERVABILITY_PROPERTIES";
	private static final String PROPERTIES_LOCAL_PATH = "govway_local.observability.properties";
	private static final String BASE_RESOURCE = "/govway.observability.properties";
	public static final String OPENSPCOOP2_LOCAL_HOME = "GOVWAY_HOME";

	/** Elenco dei collettori */
	static final String COLLECTORS_PROP = "observability.collectors";
	/** Prefisso della sezione di un singolo collettore */
	static final String COLLECTOR_PREFIX = "observability.collector.";
	/** Prefisso dei default (validi per tutti i collettori), a livello di segnale */
	static final String DEFAULT_PREFIX = "observability.";

	/** Abilitazione delle metriche di sistema (binder JVM/process di default) */
	static final String METRICS_SYSTEM_ENABLED = "observability.metrics.system-metrics.enabled";
	/** Bucket SLO degli istogrammi (metrics): liste separate da virgola */
	static final String METRICS_LATENCY_SLO = "observability.metrics.latency-slotMs";
	static final String METRICS_SIZE_SLO = "observability.metrics.size-slotByte";
	static final String METRICS_PERSISTENCE_SLO = "observability.metrics.persistence-slotMs";

	/** Collettori parsati e validati all'init (chiave = nome, ordine preservato) */
	private final Map<String, CollectorProperties> collectors;

	/** Metriche di sistema abilitate (default true) */
	private final boolean metricsSystemEnabled;

	/** Bucket SLO parsati e validati all'init ({@code null} se non configurati) */
	private final long[] metricsLatencySlotMs;
	private final double[] metricsSizeSlotByte;
	private final long[] metricsPersistenceSlotMs;

	private static ObservabilityProperties instance = null;

	public static synchronized void initialize(Logger log) throws UtilsException {
		if(instance==null) {
			instance = new ObservabilityProperties(log);
		}
	}

	public static synchronized ObservabilityProperties getInstance() throws UtilsException {
		if(instance==null) {
			initialize(LoggerWrapperFactory.getLogger(ObservabilityProperties.class));
		}
		return instance;
	}

	private ObservabilityProperties(Logger log) throws UtilsException {
		super(OPENSPCOOP2_LOCAL_HOME, loadBaseProperties(log), log);

		// Directory di configurazione (per la ricerca di govway_local.observability.properties):
		// come le altre componenti (cfr. ConfigLocalProperties), si usa la root directory di GovWay.
		String confDir = null;
		try {
			confDir = OpenSPCoop2Properties.getInstance().getRootDirectory();
		}catch(Exception e) {
			if(log!=null) {
				log.warn("Observability: impossibile determinare la directory di configurazione ('org.openspcoop2.pdd.confDirectory'): {}", e.getMessage());
			}
		}

		super.setLocalFileImplementation(PROPERTIES, PROPERTIES_LOCAL_PATH, confDir);

		// Parsing + validazione + cache dei collettori (fail-fast all'inizializzazione)
		this.collectors = parseAndValidateCollectors();

		// Abilitazione delle metriche di sistema (default true se non valorizzata)
		this.metricsSystemEnabled = parseBooleanDefaultTrue(METRICS_SYSTEM_ENABLED);

		// Parsing + validazione + cache dei bucket SLO degli istogrammi (metrics)
		this.metricsLatencySlotMs = parseLongList(METRICS_LATENCY_SLO);
		this.metricsSizeSlotByte = parseDoubleList(METRICS_SIZE_SLO);
		this.metricsPersistenceSlotMs = parseLongList(METRICS_PERSISTENCE_SLO);
	}

	private static Properties loadBaseProperties(Logger log) throws UtilsException {
		Properties p = new Properties();
		try (InputStream is = ObservabilityProperties.class.getResourceAsStream(BASE_RESOURCE)) {
			if(is!=null) {
				p.load(is);
			}
			else if(log!=null) {
				log.warn("File base '"+BASE_RESOURCE+"' non trovato nel classpath: verranno usati solo eventuali override locali");
			}
		}catch(Exception e) {
			throw new UtilsException("Lettura del file '"+BASE_RESOURCE+"' fallita: "+e.getMessage(), e);
		}
		return p;
	}

	// ---- API pubblica (legge dalla struttura cachata, nessun lazy loading) ----

	/** Nomi dei collettori configurati. */
	public List<String> getCollectorNames() {
		return new ArrayList<>(this.collectors.keySet());
	}

	/** Collettore per nome ({@code null} se non presente). */
	public CollectorProperties getCollector(String collectorName) {
		return (collectorName!=null) ? this.collectors.get(collectorName) : null;
	}

	/** Tutti i collettori configurati. */
	public List<CollectorProperties> getCollectors() {
		return new ArrayList<>(this.collectors.values());
	}

	/** Collettori con il segnale metrics abilitato. */
	public List<CollectorProperties> getCollectorsForMetrics() {
		List<CollectorProperties> list = new ArrayList<>();
		for (CollectorProperties c : this.collectors.values()) {
			if(c.isMetricsEnabled()) {
				list.add(c);
			}
		}
		return list;
	}

	/** Indica se le metriche di sistema (binder JVM/process di default) sono abilitate. */
	public boolean isMetricsSystemEnabled() {
		return this.metricsSystemEnabled;
	}

	/** Bucket SLO per gli istogrammi di latenza, in millisecondi ({@code null} se non configurati). */
	public long[] getMetricsLatencySlotMs() {
		return this.metricsLatencySlotMs;
	}

	/** Bucket SLO per gli istogrammi di dimensione, in byte ({@code null} se non configurati). */
	public double[] getMetricsSizeSlotByte() {
		return this.metricsSizeSlotByte;
	}

	/** Bucket SLO per gli istogrammi di persistenza del tracciamento, in millisecondi ({@code null} se non configurati). */
	public long[] getMetricsPersistenceSlotMs() {
		return this.metricsPersistenceSlotMs;
	}

	// ---- Parsing / validazione (eseguiti una sola volta all'init) ----

	private Map<String, CollectorProperties> parseAndValidateCollectors() throws UtilsException {
		Map<String, CollectorProperties> map = new LinkedHashMap<>();
		int prometheusCount = 0;
		for (String name : readCollectorNames()) {

			if(map.containsKey(name)) {
				throw new UtilsException("Collettore '"+name+"' duplicato nella proprieta' '"+COLLECTORS_PROP+"'");
			}

			// Il collettore effettua parsing e validazione degli argomenti alla creazione,
			// usando il resolver override+default passato come lambda
			CollectorProperties collector = new CollectorProperties(name, key -> resolveCollectorProperty(name, key));

			// Vincolo: al massimo un collettore prometheus (endpoint /metrics unico)
			if(collector.isPrometheus()) {
				prometheusCount++;
				if(prometheusCount>1) {
					throw new UtilsException("E' ammesso al massimo un collettore di tipo 'prometheus' (l'endpoint di scrape e' unico)");
				}
			}

			map.put(name, collector);
		}
		return map;
	}

	/** Parsa un booleano; ritorna {@code true} se la chiave non e' valorizzata. */
	private boolean parseBooleanDefaultTrue(String key) throws UtilsException {
		String v = super.getValue(key);
		if(v==null || v.trim().isEmpty()) {
			return true;
		}
		return "true".equalsIgnoreCase(v.trim());
	}

	/** Parsa una lista di interi positivi separati da virgola ({@code null} se la chiave non e' valorizzata). */
	private long[] parseLongList(String key) throws UtilsException {
		String v = super.getValue(key);
		if(v==null || v.trim().isEmpty()) {
			return new long[0];
		}
		List<Long> values = new ArrayList<>();
		for (String s : v.split(",")) {
			String t = s.trim();
			if(t.isEmpty()) {
				continue;
			}
			try {
				long n = Long.parseLong(t);
				if(n<=0) {
					throw new UtilsException("Proprieta' ' "+key+"': i valori devono essere > 0 (valore: "+t+")");
				}
				values.add(n);
			}catch(NumberFormatException e) {
				throw new UtilsException("Proprieta' ' "+key+"': valore non numerico '"+t+"'");
			}
		}
		if(values.isEmpty()) {
			return new long[0];
		}
		long[] result = new long[values.size()];
		for (int i=0; i<result.length; i++) {
			result[i] = values.get(i);
		}
		return result;
	}

	/** Parsa una lista di numeri positivi separati da virgola ({@code null} se la chiave non e' valorizzata). */
	private double[] parseDoubleList(String key) throws UtilsException {
		String v = super.getValue(key);
		if(v==null || v.trim().isEmpty()) {
			return new double[0];
		}
		List<Double> values = new ArrayList<>();
		for (String s : v.split(",")) {
			String t = s.trim();
			if(t.isEmpty()) {
				continue;
			}
			try {
				double n = Double.parseDouble(t);
				if(n<=0) {
					throw new UtilsException("Proprieta' '"+key+"': i valori devono essere > 0 (valore: "+t+")");
				}
				values.add(n);
			}catch(NumberFormatException e) {
				throw new UtilsException("Proprieta' '"+key+"': valore non numerico '"+t+"'");
			}
		}
		if(values.isEmpty()) {
			return new double[0];
		}
		double[] result = new double[values.size()];
		for (int i=0; i<result.length; i++) {
			result[i] = values.get(i);
		}
		return result;
	}

	private List<String> readCollectorNames() throws UtilsException {
		List<String> names = new ArrayList<>();
		String v = super.getValue(COLLECTORS_PROP);
		if(v!=null) {
			for (String s : v.split(",")) {
				String t = s.trim();
				if(!t.isEmpty()) {
					names.add(t);
				}
			}
		}
		return names;
	}

	/**
	 * Risolve una chiave di un collettore applicando override+default:
	 *   1) override:  observability.collector.&lt;collector&gt;.&lt;key&gt;
	 *   2) default:   observability.&lt;key&gt;
	 * Vince l'override se presente.
	 * NOTA: la Function passata al collettore non puo' propagare eccezioni checked;
	 * un errore di lettura viene rilanciato come RuntimeException (fa fallire l'init).
	 */
	private String resolveCollectorProperty(String collectorName, String key) {
		try {
			String override = super.getValue(COLLECTOR_PREFIX + collectorName + "." + key);
			if(override!=null) {
				return override;
			}
			return super.getValue(DEFAULT_PREFIX + key);
		}catch(UtilsException e) {
			throw new IllegalStateException("Errore lettura chiave '"+key+"' del collettore '"+collectorName+"': "+e.getMessage(), e);
		}
	}
}
