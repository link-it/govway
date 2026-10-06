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
import java.util.Map;

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

	public GovwayOtlpConfig(String url, Duration step, Map<String,String> headers) {
		this.url = url;
		this.step = step;
		this.headers = (headers!=null) ? headers : Collections.emptyMap();
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
		return Collections.singletonMap("service.name", "govway");
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
