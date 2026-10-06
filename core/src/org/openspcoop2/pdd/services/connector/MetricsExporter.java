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

package org.openspcoop2.pdd.services.connector;

import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.openspcoop2.pdd.core.observability.GovwayMeterRegistry;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

import io.prometheus.metrics.expositionformats.PrometheusTextFormatWriter;

/**
 * MetricsExporter
 *
 * Espone su '/metrics' le metriche raccolte nel {@link GovwayMeterRegistry}
 * effettuando lo scrape del registry Micrometer/Prometheus, analogamente a
 * quanto fa il servlet Proxy per le risorse JMX.
 *
 * Le metriche di tipo gauge (binder JVM/process) vengono campionate dalle API
 * al momento dello scrape.
 *
 * @author Burlon Tommaso
 * @version $Rev$, $Date$
 */
public class MetricsExporter extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override public void doGet(HttpServletRequest req, HttpServletResponse res) {

		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopProxy();
		if(log==null) {
			log = LoggerWrapperFactory.getLogger(MetricsExporter.class);
		}

		if( !OpenSPCoop2Startup.initialize ) {
			CheckStatoPdD.serializeNotInitializedResponse(res, log);
			return;
		}

		// Senza un collettore prometheus configurato (default) la risorsa non esiste
		if( !GovwayMeterRegistry.isInitialized() || !GovwayMeterRegistry.getInstance().isPrometheusEnabled() ) {
			try {
				res.sendError(404, ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeFunctionUnsupported(IDService.METRICS)));
			} catch (Exception e) {
				log.error("[MetricsExporter] Response failed", e);
			}
			return;
		}

		// Aggiorna le metriche lette "al volo" prima dello scrape (es. numero elementi cache, pool HTTP)
		GovwayMeterRegistry.getInstance().refresh();

		String scrape = GovwayMeterRegistry.getInstance().scrape();

		try {
			res.setStatus(200);
			// formato testuale Prometheus classico (0.0.4), prodotto da PrometheusMeterRegistry.scrape()
			res.setContentType(PrometheusTextFormatWriter.CONTENT_TYPE);
			res.getOutputStream().write(scrape.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			log.error("[MetricsExporter] Response failed", e);
		}
	}
}
