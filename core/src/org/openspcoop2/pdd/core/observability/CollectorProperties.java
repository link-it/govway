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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Function;

import org.openspcoop2.utils.UtilsException;

/**
 * CollectorProperties
 *
 * Vista immutabile di un collettore di observability. Un collettore e'
 * signal-agnostic: la connessione ('type') e' comune, mentre ogni segnale
 * (metrics, e in futuro traces/logs) ha la propria configurazione.
 *
 * Il costruttore riceve il nome del collettore e una funzione di risoluzione
 * {@code (chiave) -> valore} che applica override+default:
 *   override:  observability.collector.<name>.<chiave>
 *   default:   observability.<chiave>
 * (es. chiave "metrics.endpoint" -> default 'observability.metrics.endpoint').
 * Parsing e validazione avvengono qui, alla creazione; i valori sono cachati.
 *
 * @author Tommaso Burlon
 * @version $Rev$, $Date$
 */
public class CollectorProperties {

	/** Tipologie di collettore supportate. */
	public enum CollectorType {
		PROMETHEUS("prometheus"),
		OTEL("otel");

		private final String value;
		CollectorType(String value) {
			this.value = value;
		}
		public String getValue() {
			return this.value;
		}
		public static CollectorType fromValue(String value) {
			if(value!=null) {
				for (CollectorType t : values()) {
					if(t.value.equalsIgnoreCase(value.trim())) {
						return t;
					}
				}
			}
			return null;
		}
	}

	/** Proprieta' a livello di collettore */
	static final String PROP_TYPE = "type";

	/** Segnale metrics e sue proprieta' */
	static final String SIGNAL_METRICS = "metrics";
	static final String PROP_ENABLED = "enabled";
	static final String PROP_ENDPOINT = "endpoint";
	static final String PROP_STEP_SECONDS = "stepS";
	static final String PROP_CREDENTIAL_USERNAME = "credential.username";
	static final String PROP_CREDENTIAL_PASSWORD = "credential.password";

	private final String name;
	private final CollectorType type;

	// segnale metrics
	private final boolean metricsEnabled;
	private final String metricsEndpoint;
	private final Integer metricsStepSeconds;
	private final String metricsCredentialUsername;
	private final String metricsCredentialPassword;

	/**
	 * @param name nome del collettore
	 * @param propertyResolver funzione che, data una chiave (es. "type", "metrics.endpoint"),
	 *        restituisce il valore risolto applicando override+default
	 */
	public CollectorProperties(String name, Function<String,String> propertyResolver) throws UtilsException {
		this.name = name;

		// type del collettore (obbligatorio, deve essere prometheus|otel)
		String typeRaw = propertyResolver.apply(PROP_TYPE);
		this.type = CollectorType.fromValue(typeRaw);
		if(this.type==null) {
			throw new UtilsException("Collettore '"+name+"': proprieta' 'type' mancante o non valida ('"+typeRaw+"'), atteso: prometheus|otel");
		}

		// segnale metrics
		this.metricsEnabled = parseBoolean(propertyResolver.apply(signalKey(SIGNAL_METRICS, PROP_ENABLED)));
		this.metricsEndpoint = trimToNull(propertyResolver.apply(signalKey(SIGNAL_METRICS, PROP_ENDPOINT)));
		this.metricsStepSeconds = parseStepSeconds(name, propertyResolver.apply(signalKey(SIGNAL_METRICS, PROP_STEP_SECONDS)));
		this.metricsCredentialUsername = trimToNull(propertyResolver.apply(signalKey(SIGNAL_METRICS, PROP_CREDENTIAL_USERNAME)));
		this.metricsCredentialPassword = trimToNull(propertyResolver.apply(signalKey(SIGNAL_METRICS, PROP_CREDENTIAL_PASSWORD)));

		validate();
	}

	private void validate() throws UtilsException {
		if(this.metricsEnabled) {
			if(this.metricsEndpoint==null) {
				throw new UtilsException("Collettore '"+this.name+"' (metrics): proprieta' 'endpoint' obbligatoria");
			}
			// i collettori push (otel) richiedono lo step di invio
			if(CollectorType.OTEL.equals(this.type) && this.metricsStepSeconds==null) {
				throw new UtilsException("Collettore otel '"+this.name+"' (metrics): proprieta' 'stepS' obbligatoria");
			}
		}
		// credenziali Basic: se presente una delle due, richieste entrambe
		if( (this.metricsCredentialUsername==null) != (this.metricsCredentialPassword==null) ) {
			throw new UtilsException("Collettore '"+this.name+"' (metrics): 'credential.username' e 'credential.password' vanno indicate entrambe");
		}
	}

	private static String signalKey(String signal, String prop) {
		return signal + "." + prop;
	}

	public String getName() {
		return this.name;
	}

	public CollectorType getType() {
		return this.type;
	}

	public boolean isPrometheus() {
		return CollectorType.PROMETHEUS.equals(this.type);
	}

	public boolean isOtel() {
		return CollectorType.OTEL.equals(this.type);
	}

	// ---- segnale metrics ----

	public boolean isMetricsEnabled() {
		return this.metricsEnabled;
	}

	/** Endpoint del segnale metrics (path di scrape per prometheus, URL base collector per otel). */
	public String getMetricsEndpoint() {
		return this.metricsEndpoint;
	}

	/** Intervallo di invio in secondi per il segnale metrics (collettori push, es. otel); {@code null} se non previsto. */
	public Integer getMetricsStepSeconds() {
		return this.metricsStepSeconds;
	}

	/** Username per l'autenticazione Basic verso il collector (segnale metrics); {@code null} se non impostato. */
	public String getMetricsCredentialUsername() {
		return this.metricsCredentialUsername;
	}

	/** Password per l'autenticazione Basic verso il collector (segnale metrics); {@code null} se non impostato. */
	public String getMetricsCredentialPassword() {
		return this.metricsCredentialPassword;
	}

	/**
	 * Valore dell'header {@code Authorization: Basic <base64(username:password)>} per il segnale metrics,
	 * oppure {@code null} se le credenziali non sono configurate.
	 */
	public String getMetricsBasicAuthorizationHeader() {
		if(this.metricsCredentialUsername==null || this.metricsCredentialPassword==null) {
			return null;
		}
		String token = this.metricsCredentialUsername + ":" + this.metricsCredentialPassword;
		return "Basic " + Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String toString() {
		return "Collector[name="+this.name+", type="+(this.type!=null ? this.type.getValue() : null)
			+", metrics{enabled="+this.metricsEnabled+", endpoint="+this.metricsEndpoint+", stepSeconds="+this.metricsStepSeconds
			+", credential="+(this.metricsCredentialUsername!=null ? "set" : "none")+"}]";
	}

	private static boolean parseBoolean(String value) {
		return value!=null && "true".equalsIgnoreCase(value.trim());
	}

	private static Integer parseStepSeconds(String collectorName, String value) throws UtilsException {
		if(value==null || value.trim().isEmpty()) {
			return null;
		}
		try {
			int s = Integer.parseInt(value.trim());
			if(s<=0) {
				throw new UtilsException("Collettore '"+collectorName+"': la proprieta' 'stepS' deve essere > 0 (valore: "+value+")");
			}
			return Integer.valueOf(s);
		}catch(NumberFormatException e) {
			throw new UtilsException("Collettore '"+collectorName+"': la proprieta' 'stepS' non e' numerica (valore: "+value+")");
		}
	}

	private static String trimToNull(String s) {
		if(s==null) {
			return null;
		}
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}
}
