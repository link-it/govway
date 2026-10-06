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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.micrometer.registry.otlp.AggregationTemporality;
import io.micrometer.registry.otlp.OtlpConfig;

/**
 * GovwayOtlpConfig
 *
 * Configurazione (type-safe) del registry OTLP push di Micrometer: endpoint, intervallo di invio,
 * temporalita', resource attributes e header di autenticazione. I valori sono forniti direttamente
 * (nessuna sorgente esterna chiave-valore: {@link #get(String)} restituisce {@code null}).
 *
 * @author Burlon Tommaso
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GovwayOtlpConfig implements OtlpConfig {

	private final String url;
	private final Duration step;
	private final Map<String,String> headers;
	private final Map<String,String> resourceAttributes;

	/** Nome del servizio di default con cui GovWay si presenta al collector (attributo di risorsa 'service.name'). */
	public static final String SERVICE_NAME = "govway";

	/**
	 * @param serviceName nome del servizio (attributo di risorsa 'service.name'); se {@code null} viene utilizzato {@value #SERVICE_NAME}
	 * @param version versione del prodotto (attributo di risorsa 'service.version'), opzionale
	 * @param instanceId identificativo del nodo (attributo di risorsa 'service.instance.id'): in push il collector
	 *        non conosce la sorgente dei dati, per cui senza di esso le serie di nodi diversi coinciderebbero
	 */
	public GovwayOtlpConfig(String url, Duration step, Map<String,String> headers, String serviceName, String version, String instanceId) {
		this.url = url;
		this.step = step;
		this.headers = (headers!=null) ? headers : Collections.emptyMap();
		Map<String,String> attributes = new LinkedHashMap<>();
		attributes.put("service.name", serviceName!=null ? serviceName : SERVICE_NAME);
		if(instanceId!=null) {
			attributes.put("service.instance.id", instanceId);
		}
		if(version!=null) {
			attributes.put("service.version", version);
		}
		this.resourceAttributes = Collections.unmodifiableMap(attributes);
	}

	@Override
	public String url() {
		return this.url;
	}

	@Override
	public Duration step() {
		return this.step;
	}

	@Override
	public AggregationTemporality aggregationTemporality() {
		// CUMULATIVE: compatibile con la semantica dei counter Prometheus
		return AggregationTemporality.CUMULATIVE;
	}

	@Override
	public Map<String,String> resourceAttributes() {
		return this.resourceAttributes;
	}

	@Override
	public TimeUnit baseTimeUnit() {
		// Secondi, come sull'endpoint Prometheus: il default di Micrometer per OTLP sono i millisecondi,
		// che renderebbero incoerenti i valori delle metriche '*_seconds' tra i due collettori
		return TimeUnit.SECONDS;
	}

	@Override
	public Map<String,String> headers() {
		return this.headers;
	}

	@Override
	public String get(String key) {
		return null; // nessuna sorgente esterna: si usano i valori sopra e i default
	}
}
