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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.observability.ErogazioneTestMetricsUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.HistogramDataPoint;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.metrics.v1.ScopeMetrics;

/**
 * OtlpMetricsTest
 *
 * Verifica l'invio in push delle metriche verso un collector OTLP/HTTP. I collettori 'otel' e 'otelcustom' della
 * testsuite (file 'govway_local.observability.properties') inviano le metriche ogni 5 secondi al receiver di test
 * della webapp TestService, su path differenti; il test legge i payload ricevuti e li decodifica come
 * ExportMetricsServiceRequest verificando:
 * - il protocollo (POST periodici, Content-Type protobuf, autenticazione Basic);
 * - gli attributi di risorsa che identificano GovWay e il nodo che invia le metriche, con i valori di default
 *   (collettore 'otel') e con valori configurati che riferiscono variabili java (collettore 'otelcustom');
 * - i contatori, cumulativi e con le stesse label dell'endpoint Prometheus (anche di dettaglio);
 * - gli istogrammi, con i tempi espressi in secondi come sull'endpoint Prometheus;
 * - le metriche lette al momento dell'invio (cache, datasource).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OtlpMetricsTest extends ConfigLoader {

	private static final String HEADER_COUNT = "govway-otlp-count";
	private static final String HEADER_RECEIVED = "govway-otlp-received";
	private static final String HEADER_CONTENT_TYPE = "govway-otlp-content-type";
	private static final String HEADER_AUTHORIZATION = "govway-otlp-authorization";

	// credenziali configurate per il collettore 'otel' nel file 'govway_local.observability.properties' della testsuite
	private static final String USERNAME = "otlpUser";
	private static final String PASSWORD = "otlpPassword";

	// path del receiver su cui inviano i due collettori
	private static final String RECEIVER_OTEL = "otlp/v1/metrics";
	private static final String RECEIVER_OTEL_CUSTOM = "otlp/custom/v1/metrics";

	// attesa massima di un nuovo invio: circa 4 volte l'intervallo di invio configurato (5 secondi)
	private static final long ATTESA_INVIO_MS = 20000;

	private static final String SERVICE_SIMPLE = "TestMetricsSimple";
	private static final int CALLS = 3;

	private static final List<Double> LATENCY_BOUNDS_SECONDS = Arrays.asList(0.005, 0.01, 0.025, 0.05, 0.1, 0.25, 0.5, 1.0, 2.5, 5.0, 10.0);
	private static final List<Double> SIZE_BOUNDS_BYTES = Arrays.asList(256d, 1024d, 4096d, 16384d, 65536d, 262144d, 1048576d, 4194304d, 16777216d);


	@Test
	public void invioPeriodicoAutenticato() throws Exception {
		svuotaReceiver(RECEIVER_OTEL);
		Payload p = attendiNuovoInvio(RECEIVER_OTEL, 0);

		assertEquals("Content-Type dell'invio OTLP", "application/x-protobuf", p.contentType);
		String atteso = "Basic " + Base64.getEncoder().encodeToString((USERNAME+":"+PASSWORD).getBytes(StandardCharsets.UTF_8));
		assertEquals("Header Authorization dell'invio OTLP", atteso, p.authorization);

		// l'invio è periodico: ne deve seguire un altro
		Payload successivo = attendiNuovoInvio(RECEIVER_OTEL, p.received);
		assertTrue("Atteso un secondo invio periodico", successivo.count > p.count);
	}

	@Test
	public void attributiDiRisorsa() throws Exception {
		Payload p = attendiNuovoInvio(RECEIVER_OTEL, 0);
		assertFalse("Nessuna ResourceMetrics nel payload", p.request.getResourceMetricsList().isEmpty());
		Map<String,String> attributi = toMap(p.request.getResourceMetrics(0).getResource().getAttributesList());
		assertEquals("Attributo 'service.name'", "govway", attributi.get("service.name"));
		assertTrue("Attributo 'service.instance.id' assente: "+attributi, isNotEmpty(attributi.get("service.instance.id")));
		String versione = attributi.get("service.version");
		assertTrue("Attributo 'service.version' assente: "+attributi, isNotEmpty(versione));
		assertFalse("L'attributo 'service.version' non deve riportare il prefisso del prodotto: "+versione, versione.startsWith("GovWay/"));
	}

	/**
	 * Il collettore 'otelcustom' ridefinisce gli attributi di risorsa riferendo variabili java
	 * (file.separator='/', path.separator=':'), utilizzate anche nell'endpoint, e non prevede autenticazione.
	 */
	@Test
	public void attributiDiRisorsaConfigurati() throws Exception {
		svuotaReceiver(RECEIVER_OTEL_CUSTOM);
		Payload p = attendiNuovoInvio(RECEIVER_OTEL_CUSTOM, 0);
		assertNull("Il collettore 'otelcustom' non prevede autenticazione", p.authorization);
		assertFalse("Nessuna ResourceMetrics nel payload", p.request.getResourceMetricsList().isEmpty());
		Map<String,String> attributi = toMap(p.request.getResourceMetrics(0).getResource().getAttributesList());
		assertEquals("Attributo 'service.name' (govway${path.separator}testsuite)", "govway:testsuite", attributi.get("service.name"));
		assertEquals("Attributo 'service.version'", "1.0-testsuite", attributi.get("service.version"));
		assertEquals("Attributo 'service.instance.id' (testsuite${file.separator}otelcustom)", "testsuite/otelcustom", attributi.get("service.instance.id"));
		assertTrue("Il collettore 'otelcustom' deve inviare le metriche di GovWay", nomiMetriche(p).contains("govway_requests_total"));
	}

	@Test
	public void contatoriCumulativi() throws Exception {
		Map<String,String> labels = new HashMap<>();
		labels.put("role", ErogazioneTestMetricsUtils.PDD_ROLE_EROGAZIONE);
		labels.put("interface_id", ErogazioneTestMetricsUtils.SOGGETTO_INTERNO + "/" + SERVICE_SIMPLE + "/v1");
		labels.put("action", "info1");
		labels.put("result_class", "OK");
		labels.put("http_status", "200");

		Payload prima = attendiNuovoInvio(RECEIVER_OTEL, 0);
		double valorePrima = sommaContatore(prima, "govway_service_requests_total", labels);

		String url = System.getProperty("govway_base_path") + "/" + ErogazioneTestMetricsUtils.SOGGETTO_INTERNO + "/" + SERVICE_SIMPLE + "/v1/info1";
		ErogazioneTestMetricsUtils.invokeUrl(url, CALLS, 200, getLoggerCore());
		long fineChiamate = System.currentTimeMillis();

		// attendo un invio che contenga le chiamate effettuate (registrate al termine della transazione)
		double delta = 0;
		long ultimo = prima.received;
		long scadenza = System.currentTimeMillis() + ATTESA_INVIO_MS;
		while(delta < CALLS && System.currentTimeMillis() < scadenza) {
			Payload dopo = attendiNuovoInvio(RECEIVER_OTEL, Math.max(ultimo, fineChiamate));
			ultimo = dopo.received;
			delta = sommaContatore(dopo, "govway_service_requests_total", labels) - valorePrima;
		}
		assertEquals("Il contatore cumulativo 'govway_service_requests_total' con label "+labels+" deve aumentare del numero di chiamate",
				CALLS, (int) delta);
	}

	@Test
	public void istogrammiInSecondi() throws Exception {
		Payload p = attendiNuovoInvio(RECEIVER_OTEL, 0);

		Metric latenza = metrica(p, "govway_request_duration_seconds");
		assertTrue("La metrica 'govway_request_duration_seconds' deve essere un istogramma", latenza.hasHistogram());
		for (HistogramDataPoint dp : latenza.getHistogram().getDataPointsList()) {
			assertEquals("Confini dei bucket di latenza (attesi in secondi come sull'endpoint Prometheus)",
					LATENCY_BOUNDS_SECONDS, dp.getExplicitBoundsList());
		}

		Metric dimensione = metrica(p, "govway_request_size_bytes");
		assertTrue("La metrica 'govway_request_size_bytes' deve essere un istogramma", dimensione.hasHistogram());
		for (HistogramDataPoint dp : dimensione.getHistogram().getDataPointsList()) {
			assertEquals("Confini dei bucket di dimensione (in byte)", SIZE_BOUNDS_BYTES, dp.getExplicitBoundsList());
		}
	}

	@Test
	public void metricheAggiornateAllInvio() throws Exception {
		Payload p = attendiNuovoInvio(RECEIVER_OTEL, 0);
		Set<String> nomi = nomiMetriche(p);
		for (String atteso : Arrays.asList("govway_cache_elements", "govway_datasource_allocated_connections",
				"govway_db_connections_held", "govway_active_transactions", "govway_requests_total")) {
			assertTrue("Metrica '"+atteso+"' assente dall'invio OTLP: "+nomi, nomi.contains(atteso));
		}
		Metric cache = metrica(p, "govway_cache_elements");
		assertTrue("La metrica 'govway_cache_elements' deve essere un gauge", cache.hasGauge());
		assertFalse("La metrica 'govway_cache_elements' deve riportare almeno una cache", cache.getGauge().getDataPointsList().isEmpty());
	}


	// ---- receiver ----

	private static final class Payload {
		private long count;
		private long received;
		private String contentType;
		private String authorization;
		private ExportMetricsServiceRequest request;
	}

	private static String receiverUrl(String path) {
		// il receiver è nella webapp TestService, sullo stesso application server di GovWay
		String base = System.getProperty("govway_base_path");
		return base.substring(0, base.lastIndexOf('/')) + "/TestService/" + path;
	}

	private static void svuotaReceiver(String path) throws Exception {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.DELETE);
		request.setUrl(receiverUrl(path));
		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals("Svuotamento del receiver OTLP", 204, response.getResultHTTPOperation());
	}

	/** Restituisce il primo payload ricevuto dopo l'istante indicato, attendendo l'invio periodico. */
	private static Payload attendiNuovoInvio(String path, long dopo) throws Exception {
		long scadenza = System.currentTimeMillis() + ATTESA_INVIO_MS;
		while(System.currentTimeMillis() < scadenza) {
			Payload p = leggiUltimoInvio(path);
			if(p!=null && p.received > dopo) {
				return p;
			}
			Utilities.sleep(500);
		}
		throw new AssertionError("Nessun invio OTLP ricevuto da "+receiverUrl(path)+" entro "+ATTESA_INVIO_MS+"ms (verificare i collettori otel in govway_local.observability.properties)");
	}

	private static Payload leggiUltimoInvio(String path) throws Exception {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(receiverUrl(path));
		HttpResponse response = HttpUtilities.httpInvoke(request);
		if(response.getResultHTTPOperation()==404) {
			return null;
		}
		assertEquals("Lettura dal receiver OTLP", 200, response.getResultHTTPOperation());
		Payload p = new Payload();
		p.count = Long.parseLong(response.getHeaderFirstValue(HEADER_COUNT));
		p.received = Long.parseLong(response.getHeaderFirstValue(HEADER_RECEIVED));
		p.contentType = response.getHeaderFirstValue(HEADER_CONTENT_TYPE);
		p.authorization = response.getHeaderFirstValue(HEADER_AUTHORIZATION);
		assertNotNull("Payload OTLP vuoto", response.getContent());
		p.request = ExportMetricsServiceRequest.parseFrom(response.getContent());
		return p;
	}


	// ---- payload ----

	private static List<Metric> metriche(Payload p) {
		List<Metric> l = new ArrayList<>();
		for (ResourceMetrics rm : p.request.getResourceMetricsList()) {
			for (ScopeMetrics sm : rm.getScopeMetricsList()) {
				l.addAll(sm.getMetricsList());
			}
		}
		return l;
	}

	private static Set<String> nomiMetriche(Payload p) {
		Set<String> nomi = new TreeSet<>();
		for (Metric m : metriche(p)) {
			nomi.add(m.getName());
		}
		return nomi;
	}

	private static Metric metrica(Payload p, String nome) {
		for (Metric m : metriche(p)) {
			if(nome.equals(m.getName())) {
				return m;
			}
		}
		throw new AssertionError("Metrica '"+nome+"' assente dall'invio OTLP: "+nomiMetriche(p));
	}

	/** Somma dei data point del contatore che riportano (almeno) le label indicate; 0 se il contatore non è presente. */
	private static double sommaContatore(Payload p, String nome, Map<String,String> labels) {
		double totale = 0;
		for (Metric m : metriche(p)) {
			if(nome.equals(m.getName())) {
				assertTrue("La metrica '"+nome+"' deve essere un contatore (Sum)", m.hasSum());
				assertTrue("Il contatore '"+nome+"' deve essere monotono", m.getSum().getIsMonotonic());
				for (NumberDataPoint dp : m.getSum().getDataPointsList()) {
					if(toMap(dp.getAttributesList()).entrySet().containsAll(labels.entrySet())) {
						totale += dp.getAsDouble();
					}
				}
			}
		}
		return totale;
	}

	private static Map<String,String> toMap(List<KeyValue> attributi) {
		Map<String,String> m = new HashMap<>();
		for (KeyValue kv : attributi) {
			m.put(kv.getKey(), kv.getValue().getStringValue());
		}
		return m;
	}

	private static boolean isNotEmpty(String v) {
		return v!=null && !v.isEmpty();
	}
}
