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

import java.util.concurrent.ThreadFactory;

import io.micrometer.core.instrument.Clock;
import io.micrometer.registry.otlp.OtlpConfig;
import io.micrometer.registry.otlp.OtlpMeterRegistry;

import org.slf4j.Logger;

/**
 * GovwayOtlpMeterRegistry
 *
 * Estende {@link OtlpMeterRegistry} per aggiornare le metriche lette "al volo" (le MultiGauge di
 * cache e pool HTTP, campionate a runtime) prima di ogni pubblicazione periodica verso il collector
 * OTLP: altrimenti verrebbero inviate con l'ultimo valore fissato allo scrape dell'endpoint /metrics.
 *
 * Il costruttore di {@link OtlpMeterRegistry} avvia automaticamente lo scheduler che invoca
 * {@link #publish()} ad ogni step; la prima invocazione avviene dopo il primo intervallo, quando i
 * campi di questa sottoclasse sono gia' inizializzati.
 *
 * @author Burlon Tommaso
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GovwayOtlpMeterRegistry extends OtlpMeterRegistry {

	private final GovwayMeterRegistry owner;
	private final String collectorName;
	private final String endpoint;
	private final Logger log;

	public GovwayOtlpMeterRegistry(OtlpConfig config, Clock clock, ThreadFactory threadFactory,
			GovwayMeterRegistry owner, String collectorName, String endpoint, Logger log) {
		super(config, clock, threadFactory);
		this.owner = owner;
		this.collectorName = collectorName;
		this.endpoint = endpoint;
		this.log = log;
	}

	@Override
	protected void publish() {
		// Aggiorna le MultiGauge prima della serializzazione del payload OTLP
		try {
			if(this.owner!=null) {
				this.owner.refresh();
			}
		}catch(Exception e) {
			if(this.log!=null) {
				this.log.debug("Observability: refresh pre-publish OTLP (collettore '{}') fallito: {}", this.collectorName, e.getMessage());
			}
		}
		if(this.log!=null) {
			this.log.debug("Observability: pubblicazione OTLP (collettore '{}') verso {}", this.collectorName, this.endpoint);
		}
		super.publish();
	}
}
