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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.observability.ErogazioneTestMetricsUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.observability.PrometheusMetricsUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.Utilities;

/**
 * ServiceLabelsMetricsTest
 *
 * Verifica le label delle metriche di transazione (aggregate e di dettaglio) e delle metriche per fase,
 * per ciascuna classe di esito (OK, FAULT, KO):
 * - erogazione/fruizione senza gruppi ('TestMetricsSimple'), con la proprietà 'observability.metrics.details'
 *   sulla configurazione predefinita e la registrazione dei 'Tempi Elaborazione' disabilitata nel tracciamento:
 *   le metriche per fase devono essere prodotte comunque;
 * - gruppo con una sola azione e proprietà abilitata ('details');
 * - gruppo con più azioni e proprietà abilitata ('multi': multi1, multi2);
 * - gruppo senza proprietà ('nodetails'), che non deve produrre metriche di dettaglio.
 * Per ogni famiglia di metriche viene verificato l'insieme esatto delle label esposte; la label 'tags'
 * riporta i tag dell'API in ordine alfabetico separati da virgola.
 * La label 'interface_id' deve riportare sempre la porta di default (forma normalizzata dell'url
 * di invocazione) anche quando la richiesta è gestita da un gruppo, indicato dalla label 'group'.
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiceLabelsMetricsTest extends ConfigLoader {

	// Metriche aggregate
	private static final String METRIC_REQUESTS_TOTAL = "govway_requests_total";
	private static final String METRIC_REQUEST_DURATION_COUNT = "govway_request_duration_seconds_count";
	private static final String METRIC_REQUEST_SIZE_COUNT = "govway_request_size_bytes_count";
	// Metriche di dettaglio
	private static final String METRIC_SERVICE_REQUESTS_TOTAL = "govway_service_requests_total";
	private static final String METRIC_SERVICE_REQUEST_DURATION_COUNT = "govway_service_request_duration_seconds_count";
	private static final String METRIC_SERVICE_REQUEST_SIZE_COUNT = "govway_service_request_size_bytes_count";
	private static final String METRIC_PROCESSING_PHASE_COUNT = "govway_processing_phase_seconds_count";

	// Label
	private static final List<String> LABEL_AGGREGATE = Arrays.asList("role", "protocol", "result_class");
	private static final List<String> LABEL_ESITO_CONTATORI = Arrays.asList("result_code", "http_status");
	private static final List<String> LABEL_DETTAGLIO = Arrays.asList("interface_id", "group",
			"provider_type", "provider", "sender_type", "sender",
			"service_type", "service", "service_version", "action",
			"api", "api_version", "api_provider_type", "api_provider", "tags");

	// Fasi di elaborazione: sempre registrate per una richiesta REST gestita (le altre dipendono dalle funzionalità configurate)
	private static final Set<String> FASI_OBBLIGATORIE = new TreeSet<>(Arrays.asList(
			"trafficControl_maxRequests", "trafficControl_rateLimiting", "requestTracing", "responseTracing"));
	// Fasi di elaborazione previste (valori ammessi della label 'phase')
	private static final Set<String> FASI_AMMESSE = new TreeSet<>(Arrays.asList(
			"token", "authentication", "tokenAuthentication", "tokenApplicationAuthentication", "authorization",
			"contentAuthorization", "requestValidation", "responseValidation", "trafficControl_maxRequests",
			"trafficControl_rateLimiting", "requestMessageSecurity", "responseMessageSecurity", "requestAttachmentsHandling",
			"responseAttachmentsHandling", "requestApplicationCorrelation", "responseApplicationCorrelation", "requestTracing",
			"responseTracing", "dumpRequestInbound", "dumpRequestOutbound", "dumpResponseInbound", "dumpResponseOutbound",
			"dumpBinaryRequestInbound", "dumpBinaryRequestOutbound", "dumpBinaryResponseInbound", "dumpBinaryResponseOutbound",
			"dumpIntegrationManager", "responseCachingDigestComputation", "responseCachingReadFromCache",
			"responseCachingSaveInCache", "requestTransformation", "responseTransformation", "attributeAuthority"));

	private static final String SERVICE_SIMPLE = "TestMetricsSimple";
	private static final String SERVICE_SIMPLE_VERSION = "1";
	private static final String API = "TestMetrics";
	private static final String API_VERSION = "1";
	// Tag dell'API TestMetrics (definiti come Observability, Metrics): esposti in ordine alfabetico separati da virgola
	private static final String API_TAGS = "Metrics,Observability";
	private static final String TIPO_GW = "gw";
	private static final String GROUP_DEFAULT = "Predefinito";

	private static final int CALLS = 2;

	/** Classi di esito prodotte invocando il TestService di backend. */
	private enum Esito {
		OK(null, 200),
		FAULT("problem=true", 500), // problem detail (RFC 7807) restituito dal backend: fault applicativo
		KO("returnCode=500", 500);  // errore http del backend senza problem detail
		private final String query;
		private final int httpStatus;
		Esito(String query, int httpStatus) {
			this.query = query;
			this.httpStatus = httpStatus;
		}
	}

	// Polling: fino a ~15s con backoff, le metriche sono registrate in PostOutResponse
	private static final int[] POLL_BACKOFF_MS = { 200, 300, 500, 1000, 1000, 2000, 2000, 3000, 5000 };

	@Test
	public void erogazioneSenzaGruppi() throws Exception {
		verificaSenzaGruppi(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneSenzaGruppi() throws Exception {
		verificaSenzaGruppi(TipoServizio.FRUIZIONE);
	}

	@Test
	public void erogazioneFasiSenzaTempiElaborazione() throws Exception {
		verificaFasiSenzaTempiElaborazione(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneFasiSenzaTempiElaborazione() throws Exception {
		verificaFasiSenzaTempiElaborazione(TipoServizio.FRUIZIONE);
	}

	@Test
	public void erogazioneGruppoAzioneSingola() throws Exception {
		verificaGruppo(TipoServizio.EROGAZIONE, "details", new String[] {"metricsDetails"}, new String[] {"details"});
	}
	@Test
	public void fruizioneGruppoAzioneSingola() throws Exception {
		verificaGruppo(TipoServizio.FRUIZIONE, "details", new String[] {"metricsDetails"}, new String[] {"details"});
	}

	@Test
	public void erogazioneGruppoPiuAzioni() throws Exception {
		verificaGruppo(TipoServizio.EROGAZIONE, "multi", new String[] {"multi1", "multi2"}, new String[] {"multi1", "multi2"});
	}
	@Test
	public void fruizioneGruppoPiuAzioni() throws Exception {
		verificaGruppo(TipoServizio.FRUIZIONE, "multi", new String[] {"multi1", "multi2"}, new String[] {"multi1", "multi2"});
	}

	@Test
	public void erogazioneGruppoNonAbilitato() throws Exception {
		verificaGruppoNonAbilitato(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneGruppoNonAbilitato() throws Exception {
		verificaGruppoNonAbilitato(TipoServizio.FRUIZIONE);
	}


	// ---- casi ----

	private void verificaSenzaGruppi(TipoServizio tipo) throws Exception {
		Map<String,String> attese = labelsAttese(tipo, interfaceIdSimple(tipo), GROUP_DEFAULT, providerSimple(tipo), SERVICE_SIMPLE, SERVICE_SIMPLE_VERSION, "info1");
		for (Esito esito : Esito.values()) {
			verificaMetriche(tipo, urlSimple(tipo, "info1"), esito, attese);
		}
	}

	private void verificaGruppo(TipoServizio tipo, String gruppo, String[] risorse, String[] azioni) throws Exception {
		for (int i = 0; i < risorse.length; i++) {
			Map<String,String> attese = labelsAttese(tipo, interfaceIdGruppi(tipo), gruppo, providerGruppi(tipo),
					ErogazioneTestMetricsUtils.api(tipo), ErogazioneTestMetricsUtils.serviceVersion(tipo), azioni[i]);
			for (Esito esito : Esito.values()) {
				verificaMetriche(tipo, ErogazioneTestMetricsUtils.resourceUrl(tipo, risorse[i]), esito, attese);
			}
		}
	}

	private void verificaGruppoNonAbilitato(TipoServizio tipo) throws Exception {
		String metricsUrl = ErogazioneTestMetricsUtils.metricsUrl();
		Map<String,String> filtroAggregato = Map.of("role", ErogazioneTestMetricsUtils.pddRole(tipo), "protocol", "trasparente", "result_class", Esito.OK.name());
		Map<String,String> filtroDettaglio = Map.of("interface_id", interfaceIdGruppi(tipo), "action", "nodetails");

		PrometheusMetricsUtils before = PrometheusMetricsUtils.scrape(metricsUrl);
		ErogazioneTestMetricsUtils.invokeUrl(ErogazioneTestMetricsUtils.resourceUrl(tipo, "noDetails"), CALLS, 200, getLoggerCore());

		// le richieste devono essere contabilizzate nelle metriche aggregate...
		PrometheusMetricsUtils after = null;
		double deltaAggregato = 0;
		for (int i = 0; i < POLL_BACKOFF_MS.length && deltaAggregato < CALLS; i++) {
			Utilities.sleep(POLL_BACKOFF_MS[i]);
			after = PrometheusMetricsUtils.scrape(metricsUrl);
			deltaAggregato = PrometheusMetricsUtils.delta(before, after, METRIC_REQUESTS_TOTAL, filtroAggregato);
		}
		assertTrue("["+tipo+"] Le richieste verso il gruppo 'nodetails' devono essere contabilizzate nelle metriche aggregate", deltaAggregato >= CALLS);

		// ...ma non devono produrre metriche di dettaglio: la proprietà non è abilitata sul gruppo
		for (String metrica : Arrays.asList(METRIC_SERVICE_REQUESTS_TOTAL, METRIC_SERVICE_REQUEST_DURATION_COUNT,
				METRIC_SERVICE_REQUEST_SIZE_COUNT, METRIC_PROCESSING_PHASE_COUNT)) {
			assertEquals("["+tipo+"] Il gruppo 'nodetails' (proprietà non abilitata) non deve produrre la metrica di dettaglio '"+metrica+"'",
					0, (int) PrometheusMetricsUtils.delta(before, after, metrica, filtroDettaglio));
		}
	}


	/**
	 * Le metriche per fase non dipendono dall'opzione 'Tempi Elaborazione' del tracciamento, che governa
	 * solamente il salvataggio dei tempi nella traccia della transazione: su 'TestMetricsSimple' (opzione
	 * disabilitata) le fasi devono essere registrate nella metrica mentre la colonna 'tempi_elaborazione'
	 * della transazione deve restare vuota; sul gruppo 'multi' (opzione abilitata) la colonna è valorizzata.
	 */
	private void verificaFasiSenzaTempiElaborazione(TipoServizio tipo) throws Exception {
		String metricsUrl = ErogazioneTestMetricsUtils.metricsUrl();
		String tag = "["+tipo+" tempi elaborazione disabilitati] ";
		Map<String,String> attese = labelsAttese(tipo, interfaceIdSimple(tipo), GROUP_DEFAULT, providerSimple(tipo), SERVICE_SIMPLE, SERVICE_SIMPLE_VERSION, "info1");

		PrometheusMetricsUtils before = PrometheusMetricsUtils.scrape(metricsUrl);
		List<String> idDisabilitati = ErogazioneTestMetricsUtils.invokeUrlGetIdTransazioni(urlSimple(tipo, "info1"), CALLS, 200, getLoggerCore());
		List<String> idAbilitati = ErogazioneTestMetricsUtils.invokeUrlGetIdTransazioni(ErogazioneTestMetricsUtils.resourceUrl(tipo, "multi1"), 1, 200, getLoggerCore());

		PrometheusMetricsUtils after = null;
		double deltaFasi = 0;
		for (int i = 0; i < POLL_BACKOFF_MS.length && deltaFasi <= 0; i++) {
			Utilities.sleep(POLL_BACKOFF_MS[i]);
			after = PrometheusMetricsUtils.scrape(metricsUrl);
			deltaFasi = PrometheusMetricsUtils.delta(before, after, METRIC_PROCESSING_PHASE_COUNT, attese);
		}
		assertTrue(tag+"Gli istogrammi per fase con label "+attese+" devono essere aggiornati anche con i tempi di elaborazione disabilitati", deltaFasi > 0);
		Set<String> fasi = after.labelValues(METRIC_PROCESSING_PHASE_COUNT, "phase", attese);
		assertTrue(tag+"Fasi obbligatorie "+FASI_OBBLIGATORIE+" non tutte presenti: "+fasi, fasi.containsAll(FASI_OBBLIGATORIE));

		for (String id : idDisabilitati) {
			assertFalse(tag+"La transazione '"+id+"' non deve riportare i tempi di elaborazione (opzione disabilitata)", isTempiElaborazioneRegistrati(id));
		}
		for (String id : idAbilitati) {
			assertTrue(tag+"La transazione '"+id+"' del gruppo 'multi' deve riportare i tempi di elaborazione (opzione abilitata)", isTempiElaborazioneRegistrati(id));
		}
	}

	/** Attende la registrazione della transazione e indica se la colonna 'tempi_elaborazione' è valorizzata. */
	private static boolean isTempiElaborazioneRegistrati(String idTransazione) {
		Integer count = 0;
		for (int i = 0; i < POLL_BACKOFF_MS.length && count==0; i++) {
			Utilities.sleep(POLL_BACKOFF_MS[i]);
			count = dbUtils.readValue("select count(*) from transazioni where id=?", Integer.class, idTransazione);
		}
		assertEquals("Transazione '"+idTransazione+"' non registrata", 1, count.intValue());
		return dbUtils.readValue("select count(*) from transazioni where id=? and tempi_elaborazione is not null", Integer.class, idTransazione) > 0;
	}


	// ---- verifica ----

	private void verificaMetriche(TipoServizio tipo, String url, Esito esito, Map<String,String> attese) throws Exception {
		String metricsUrl = ErogazioneTestMetricsUtils.metricsUrl();
		String tag = "["+tipo+" "+esito+" "+attese.get("action")+"] ";
		String role = ErogazioneTestMetricsUtils.pddRole(tipo);
		String httpStatus = String.valueOf(esito.httpStatus);

		Map<String,String> aggregato = new LinkedHashMap<>();
		aggregato.put("role", role);
		aggregato.put("protocol", "trasparente");
		aggregato.put("result_class", esito.name());
		Map<String,String> dettaglio = new LinkedHashMap<>(attese);
		dettaglio.put("protocol", "trasparente");
		dettaglio.put("result_class", esito.name());
		Map<String,String> contatoreAggregato = with(aggregato, "http_status", httpStatus);
		Map<String,String> contatoreDettaglio = with(dettaglio, "http_status", httpStatus);

		PrometheusMetricsUtils before = PrometheusMetricsUtils.scrape(metricsUrl);
		ErogazioneTestMetricsUtils.invokeUrl(esito.query!=null ? url+"?"+esito.query : url, CALLS, esito.httpStatus, getLoggerCore());

		PrometheusMetricsUtils after = null;
		double delta = 0;
		double deltaFasi = 0;
		for (int i = 0; i < POLL_BACKOFF_MS.length; i++) {
			Utilities.sleep(POLL_BACKOFF_MS[i]);
			after = PrometheusMetricsUtils.scrape(metricsUrl);
			delta = PrometheusMetricsUtils.delta(before, after, METRIC_SERVICE_REQUESTS_TOTAL, contatoreDettaglio);
			deltaFasi = PrometheusMetricsUtils.delta(before, after, METRIC_PROCESSING_PHASE_COUNT, attese);
			if(delta >= CALLS && deltaFasi > 0) {
				break;
			}
		}

		// --- contatori: classe, codice dell'esito e status http ---
		assertEquals(tag+"Il contatore di dettaglio con label "+contatoreDettaglio+" non è aumentato del numero di chiamate a "+url,
				CALLS, (int) delta);
		assertTrue(tag+"Il contatore aggregato con label "+contatoreAggregato+" non è aumentato almeno del numero di chiamate",
				PrometheusMetricsUtils.delta(before, after, METRIC_REQUESTS_TOTAL, contatoreAggregato) >= CALLS);
		Set<String> resultCodes = after.labelValues(METRIC_SERVICE_REQUESTS_TOTAL, "result_code", contatoreDettaglio);
		assertEquals(tag+"Atteso un solo codice di esito per la classe "+esito+": "+resultCodes, 1, resultCodes.size());
		assertTrue(tag+"Il codice di esito deve essere numerico: "+resultCodes, resultCodes.iterator().next().matches("\\d+"));
		verificaChiavi(tag, after, METRIC_REQUESTS_TOTAL, contatoreAggregato, LABEL_AGGREGATE, LABEL_ESITO_CONTATORI);
		verificaChiavi(tag, after, METRIC_SERVICE_REQUESTS_TOTAL, contatoreDettaglio, LABEL_AGGREGATE, LABEL_DETTAGLIO, LABEL_ESITO_CONTATORI);

		// --- istogrammi: solo la classe dell'esito ---
		Map<String,String> durata = with(dettaglio, "phase", "total");
		assertEquals(tag+"L'istogramma di latenza di dettaglio con label "+durata+" non è aumentato del numero di chiamate",
				CALLS, (int) PrometheusMetricsUtils.delta(before, after, METRIC_SERVICE_REQUEST_DURATION_COUNT, durata));
		Map<String,String> dimensione = with(dettaglio, "direction", "in_req");
		assertEquals(tag+"L'istogramma di dimensione di dettaglio con label "+dimensione+" non è aumentato del numero di chiamate",
				CALLS, (int) PrometheusMetricsUtils.delta(before, after, METRIC_SERVICE_REQUEST_SIZE_COUNT, dimensione));
		assertEquals(tag+"Valori della label 'direction' inattesi",
				new TreeSet<>(Arrays.asList("in_req", "out_req", "in_resp", "out_resp")),
				after.labelValues(METRIC_SERVICE_REQUEST_SIZE_COUNT, "direction", dettaglio));
		assertEquals(tag+"Valori della label 'phase' (latenza) inattesi",
				new TreeSet<>(Arrays.asList("total", "service", "gateway")),
				after.labelValues(METRIC_SERVICE_REQUEST_DURATION_COUNT, "phase", dettaglio));
		verificaChiavi(tag, after, METRIC_REQUEST_DURATION_COUNT, aggregato, LABEL_AGGREGATE, Arrays.asList("phase"));
		verificaChiavi(tag, after, METRIC_REQUEST_SIZE_COUNT, aggregato, LABEL_AGGREGATE, Arrays.asList("direction"));
		verificaChiavi(tag, after, METRIC_SERVICE_REQUEST_DURATION_COUNT, dettaglio, LABEL_AGGREGATE, LABEL_DETTAGLIO, Arrays.asList("phase"));
		verificaChiavi(tag, after, METRIC_SERVICE_REQUEST_SIZE_COUNT, dettaglio, LABEL_AGGREGATE, LABEL_DETTAGLIO, Arrays.asList("direction"));
		verificaLabelVuote(tag, tipo, after.samples(METRIC_SERVICE_REQUESTS_TOTAL, contatoreDettaglio));

		// --- fasi di elaborazione: nessuna label di esito ---
		assertTrue(tag+"Gli istogrammi per fase con label "+attese+" devono essere aggiornati dalle chiamate a "+url, deltaFasi > 0);
		verificaChiavi(tag, after, METRIC_PROCESSING_PHASE_COUNT, attese, Arrays.asList("role"), LABEL_DETTAGLIO, Arrays.asList("phase"));
		Set<String> fasi = after.labelValues(METRIC_PROCESSING_PHASE_COUNT, "phase", attese);
		assertTrue(tag+"Fasi obbligatorie "+FASI_OBBLIGATORIE+" non tutte presenti: "+fasi, fasi.containsAll(FASI_OBBLIGATORIE));
		Set<String> fasiInattese = new TreeSet<>(fasi);
		fasiInattese.removeAll(FASI_AMMESSE);
		assertTrue(tag+"Fasi non previste: "+fasiInattese, fasiInattese.isEmpty());
		verificaLabelVuote(tag, tipo, after.samples(METRIC_PROCESSING_PHASE_COUNT, attese));
	}

	/** Verifica che tutte le serie che matchano il filtro espongano esattamente le label attese. */
	@SafeVarargs
	private static void verificaChiavi(String tag, PrometheusMetricsUtils metrics, String metrica, Map<String,String> filtro, List<String> ... gruppiLabel) {
		Set<String> attese = new TreeSet<>();
		for (List<String> gruppo : gruppiLabel) {
			attese.addAll(gruppo);
		}
		Set<Set<String>> trovate = metrics.labelKeySets(metrica, filtro);
		assertFalse(tag+"Nessuna serie '"+metrica+"' con label "+filtro, trovate.isEmpty());
		assertEquals(tag+"Label esposte dalla metrica '"+metrica+"' diverse da quelle attese", Set.of(attese), trovate);
	}

	/** 'sender'/'sender_type' sono valorizzate solo per le fruizioni; 'api_provider'/'api_provider_type' solo per i profili con soggetto referente (non 'trasparente'). */
	private static void verificaLabelVuote(String tag, TipoServizio tipo, List<PrometheusMetricsUtils.Sample> samples) {
		assertFalse(tag+"Nessuna serie di dettaglio trovata", samples.isEmpty());
		for (PrometheusMetricsUtils.Sample s : samples) {
			if(!TipoServizio.FRUIZIONE.equals(tipo)) {
				assertTrue(tag+"La label 'sender' deve essere vuota per un'erogazione: "+s, isEmpty(s.getLabel("sender")));
				assertTrue(tag+"La label 'sender_type' deve essere vuota per un'erogazione: "+s, isEmpty(s.getLabel("sender_type")));
			}
			assertTrue(tag+"La label 'api_provider' deve essere vuota per il profilo trasparente: "+s, isEmpty(s.getLabel("api_provider")));
			assertTrue(tag+"La label 'api_provider_type' deve essere vuota per il profilo trasparente: "+s, isEmpty(s.getLabel("api_provider_type")));
		}
	}
	private static boolean isEmpty(String v) {
		return v==null || v.isEmpty();
	}
	private static Map<String,String> with(Map<String,String> base, String key, String value) {
		Map<String,String> m = new LinkedHashMap<>(base);
		m.put(key, value);
		return m;
	}


	// ---- valori attesi ----

	private static Map<String,String> labelsAttese(TipoServizio tipo, String interfaceId, String gruppo, String provider,
			String service, String serviceVersion, String action) {
		Map<String,String> m = new LinkedHashMap<>();
		m.put("role", ErogazioneTestMetricsUtils.pddRole(tipo));
		m.put("interface_id", interfaceId);
		m.put("group", gruppo);
		m.put("provider_type", TIPO_GW);
		m.put("provider", provider);
		if(TipoServizio.FRUIZIONE.equals(tipo)) {
			m.put("sender_type", TIPO_GW);
			m.put("sender", ErogazioneTestMetricsUtils.SOGGETTO_INTERNO);
		}
		m.put("service_type", TIPO_GW);
		m.put("service", service);
		m.put("service_version", serviceVersion);
		m.put("action", action);
		m.put("api", API);
		m.put("api_version", API_VERSION);
		m.put("tags", API_TAGS);
		return m;
	}

	// Erogazione e fruizione 'TestMetricsSimple' hanno lo stesso nome e versione ma erogatori diversi:
	// 'interface_id' e 'provider' le distinguono
	private static String providerSimple(TipoServizio tipo) {
		return TipoServizio.FRUIZIONE.equals(tipo) ? ErogazioneTestMetricsUtils.SOGGETTO_ESTERNO : ErogazioneTestMetricsUtils.SOGGETTO_INTERNO;
	}
	private static String interfaceIdSimple(TipoServizio tipo) {
		if(TipoServizio.FRUIZIONE.equals(tipo)) {
			return ErogazioneTestMetricsUtils.SOGGETTO_INTERNO + "/" + ErogazioneTestMetricsUtils.SOGGETTO_ESTERNO + "/" + SERVICE_SIMPLE + "/v" + SERVICE_SIMPLE_VERSION;
		}
		return ErogazioneTestMetricsUtils.SOGGETTO_INTERNO + "/" + SERVICE_SIMPLE + "/v" + SERVICE_SIMPLE_VERSION;
	}
	private static String urlSimple(TipoServizio tipo, String operazione) {
		String base = System.getProperty("govway_base_path");
		if(TipoServizio.FRUIZIONE.equals(tipo)) {
			return base + "/out/" + interfaceIdSimple(tipo) + "/" + operazione;
		}
		return base + "/" + interfaceIdSimple(tipo) + "/" + operazione;
	}

	private static String providerGruppi(TipoServizio tipo) {
		return TipoServizio.FRUIZIONE.equals(tipo) ? ErogazioneTestMetricsUtils.SOGGETTO_ESTERNO : ErogazioneTestMetricsUtils.SOGGETTO_INTERNO;
	}
	private static String interfaceIdGruppi(TipoServizio tipo) {
		if(TipoServizio.FRUIZIONE.equals(tipo)) {
			return ErogazioneTestMetricsUtils.SOGGETTO_INTERNO + "/" + ErogazioneTestMetricsUtils.SOGGETTO_ESTERNO + "/"
					+ ErogazioneTestMetricsUtils.API_FRUIZIONE + "/" + ErogazioneTestMetricsUtils.VERSIONE_PATH_FRUIZIONE;
		}
		return ErogazioneTestMetricsUtils.SOGGETTO_INTERNO + "/" + ErogazioneTestMetricsUtils.API_EROGAZIONE + "/" + ErogazioneTestMetricsUtils.VERSIONE_PATH_EROGAZIONE;
	}
}
