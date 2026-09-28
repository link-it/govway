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

package org.openspcoop2.core.protocolli.modipa.testsuite.rest.content_encoding;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.modipa.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.modipa.testsuite.DbUtils;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * Verifica del Digest ModI (pattern IDAR03) su contenuti compressi (Content-Encoding: gzip),
 * con e senza la decompressione dei contenuti abilitata sulle fruizioni e sulle erogazioni.
 *
 * Flusso: test (client) -> fruizione -> {@link ContentEncodingDigestProxy} -> erogazione -> {@link ContentEncodingDigestMockServer}.
 * La configurazione delle API RestContentEncodingDigest* è presente in extraTestBundle.zip.
 *
 * Il Digest (RFC 3230) si riferisce ai byte trasmessi, cioè al contenuto dopo l'applicazione del Content-Encoding:
 * - chi invia senza decompressione lo calcola sui byte compressi;
 * - chi riceve con la decompressione abilitata deve verificarlo sui byte ricevuti (compressi) e non sul contenuto decompresso.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentEncodingDigestTest extends ConfigLoader {

	private static final String API = "RestContentEncodingDigest";
	private static final String API_DECOMPRESS_FRUIZIONE_RICHIESTA = "RestContentEncodingDigestDecompressFruizioneRichiesta";
	private static final String API_DECOMPRESS_EROGAZIONE_RICHIESTA = "RestContentEncodingDigestDecompressErogazioneRichiesta";
	private static final String API_DECOMPRESS_FRUIZIONE_RISPOSTA = "RestContentEncodingDigestDecompressFruizioneRisposta";

	private static final String SOGGETTO_FRUITORE = "DemoSoggettoFruitore";
	private static final String SOGGETTO_EROGATORE = "DemoSoggettoErogatore";
	private static final String APPLICATIVO = "ApplicativoContentEncodingDigest";
	private static final String RISORSA = "/resources/1/M";

	private static final String HEADER_TRANSACTION_ID = "GovWay-Transaction-ID";
	private static final String DIGEST_NON_CORRISPONDENTE = "possiede un valore non corrispondente al messaggio";
	/** Un header firmato rimosso (es. Content-Encoding dalla decompressione) non deve risultare mancante. */
	private static final String HEADER_FIRMATO_NON_TROVATO = "dichiarato tra gli header firmati, non trovato";
	private static final long ESITO_OK = 0;

	private static ContentEncodingDigestMockServer mock;
	private static ContentEncodingDigestProxy proxy;
	private static HttpClient client;
	private static DbUtils dbUtils;
	private static long dbRetryMaxTotalMs;
	private static long dbRetryIntervalMs;

	@BeforeClass
	public static void startServers() throws Exception {
		int connectTimeout = Integer.parseInt(prop.getProperty("connect_timeout"));
		int readTimeout = Integer.parseInt(prop.getProperty("read_timeout"));

		mock = new ContentEncodingDigestMockServer(Integer.parseInt(prop.getProperty("content_encoding_mock_port")));
		mock.start();
		proxy = new ContentEncodingDigestProxy(Integer.parseInt(prop.getProperty("content_encoding_proxy_port")),
				prop.getProperty("govway_base_path"), SOGGETTO_EROGATORE, connectTimeout, readTimeout);
		proxy.start();

		client = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_1_1)
				.connectTimeout(Duration.ofMillis(connectTimeout))
				.build();

		Map<String, Object> dbConfig = new HashMap<>();
		dbConfig.put("url", prop.getProperty("db_url"));
		dbConfig.put("username", prop.getProperty("db_username"));
		dbConfig.put("password", prop.getProperty("db_password"));
		dbConfig.put("driverClassName", prop.getProperty("db_driverClassName"));
		dbUtils = new DbUtils(dbConfig);
		dbRetryMaxTotalMs = Long.parseLong(prop.getProperty("db_retry_max_total_ms"));
		dbRetryIntervalMs = Long.parseLong(prop.getProperty("db_retry_interval_ms"));
	}

	@AfterClass
	public static void stopServers() {
		if (proxy != null) {
			proxy.close();
		}
		if (mock != null) {
			mock.close();
		}
	}



	// *** D1: richiesta compressa, nessuna decompressione ***

	@Test
	public void richiestaCompressa() throws Exception {
		byte[] plain = ContentEncodingDigestUtils.jsonPayload("D1");
		byte[] gzip = ContentEncodingDigestUtils.gzip(plain);
		prepare(false, false, null, ContentEncodingDigestUtils.jsonPayload("D1-risposta"));

		HttpResponse<byte[]> response = invoke(API, ContentEncodingDigestUtils.GZIP, gzip);

		assertEquals(200, response.statusCode());
		// la fruizione inoltra i byte compressi ricevuti e calcola il Digest su di essi
		ContentEncodingDigestExchange requestIn = proxy.getRequestIn();
		assertEquals(ContentEncodingDigestUtils.GZIP, requestIn.getHeader(ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING));
		assertArrayEquals(gzip, requestIn.getBody());
		assertEquals(digest(gzip), requestIn.getHeader(ContentEncodingDigestUtils.HEADER_DIGEST));
		assertNotNull(requestIn.getHeader(ContentEncodingDigestUtils.HEADER_INTEGRITY));
		// il backend riceve i byte compressi inviati dal client
		ContentEncodingDigestExchange backend = mock.getLastRequest();
		assertEquals(ContentEncodingDigestUtils.GZIP, backend.getHeader(ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING));
		assertArrayEquals(gzip, backend.getBody());

		verifyEsitoOk(response);
	}

	// *** D2: risposta compressa, nessuna decompressione ***

	@Test
	public void rispostaCompressa() throws Exception {
		byte[] plain = ContentEncodingDigestUtils.jsonPayload("D2-risposta");
		byte[] gzip = ContentEncodingDigestUtils.gzip(plain);
		prepare(false, false, ContentEncodingDigestUtils.GZIP, gzip);

		HttpResponse<byte[]> response = invoke(API, null, ContentEncodingDigestUtils.jsonPayload("D2"));

		assertEquals(200, response.statusCode());
		// l'erogazione restituisce i byte compressi ricevuti dal backend e calcola il Digest su di essi
		ContentEncodingDigestExchange responseIn = proxy.getResponseIn();
		assertEquals(ContentEncodingDigestUtils.GZIP, responseIn.getHeader(ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING));
		assertArrayEquals(gzip, responseIn.getBody());
		assertEquals(digest(gzip), responseIn.getHeader(ContentEncodingDigestUtils.HEADER_DIGEST));
		assertNotNull(responseIn.getHeader(ContentEncodingDigestUtils.HEADER_INTEGRITY));
		// il client riceve i byte compressi inviati dal backend
		assertEquals(ContentEncodingDigestUtils.GZIP, header(response, ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING));
		assertArrayEquals(gzip, response.body());

		verifyEsitoOk(response);
	}

	// *** D3: richiesta compressa, decompressione sulla fruizione ***

	@Test
	public void richiestaCompressaDecompressioneFruizione() throws Exception {
		byte[] plain = ContentEncodingDigestUtils.jsonPayload("D3");
		byte[] gzip = ContentEncodingDigestUtils.gzip(plain);
		prepare(false, false, null, ContentEncodingDigestUtils.jsonPayload("D3-risposta"));

		HttpResponse<byte[]> response = invoke(API_DECOMPRESS_FRUIZIONE_RICHIESTA, ContentEncodingDigestUtils.GZIP, gzip);

		assertEquals(200, response.statusCode());
		// la fruizione decomprime: inoltra il contenuto in chiaro, senza Content-Encoding, e calcola il Digest su di esso
		ContentEncodingDigestExchange requestIn = proxy.getRequestIn();
		assertNull(requestIn.getHeader(ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING));
		assertArrayEquals(plain, requestIn.getBody());
		assertEquals(digest(plain), requestIn.getHeader(ContentEncodingDigestUtils.HEADER_DIGEST));
		// il backend riceve il contenuto in chiaro
		ContentEncodingDigestExchange backend = mock.getLastRequest();
		assertNull(backend.getHeader(ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING));
		assertArrayEquals(plain, backend.getBody());

		verifyEsitoOk(response);
	}

	// *** D4: decompressione su chi riceve il messaggio: il Digest va verificato sui byte ricevuti (compressi) ***

	@Test
	public void richiestaCompressaDecompressioneErogazione() throws Exception {
		byte[] plain = ContentEncodingDigestUtils.jsonPayload("D4-richiesta");
		byte[] gzip = ContentEncodingDigestUtils.gzip(plain);
		prepare(false, false, null, ContentEncodingDigestUtils.jsonPayload("D4-richiesta-risposta"));

		HttpResponse<byte[]> response = invoke(API_DECOMPRESS_EROGAZIONE_RICHIESTA, ContentEncodingDigestUtils.GZIP, gzip);

		// la fruizione (senza decompressione) calcola il Digest sui byte compressi
		ContentEncodingDigestExchange requestIn = proxy.getRequestIn();
		assertEquals(ContentEncodingDigestUtils.GZIP, requestIn.getHeader(ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING));
		assertEquals(digest(gzip), requestIn.getHeader(ContentEncodingDigestUtils.HEADER_DIGEST));
		// l'erogazione decomprime e verifica il Digest sui byte ricevuti
		assertEquals(200, response.statusCode());
		ContentEncodingDigestExchange backend = mock.getLastRequest();
		assertNotNull("richiesta non consegnata al backend", backend);
		assertNull(backend.getHeader(ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING));
		assertArrayEquals(plain, backend.getBody());

		verifyEsitoOk(response);
	}

	@Test
	public void rispostaCompressaDecompressioneFruizione() throws Exception {
		byte[] plain = ContentEncodingDigestUtils.jsonPayload("D4-risposta");
		byte[] gzip = ContentEncodingDigestUtils.gzip(plain);
		prepare(false, false, ContentEncodingDigestUtils.GZIP, gzip);

		HttpResponse<byte[]> response = invoke(API_DECOMPRESS_FRUIZIONE_RISPOSTA, null, ContentEncodingDigestUtils.jsonPayload("D4"));

		// l'erogazione (senza decompressione) calcola il Digest sui byte compressi
		ContentEncodingDigestExchange responseIn = proxy.getResponseIn();
		assertEquals(ContentEncodingDigestUtils.GZIP, responseIn.getHeader(ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING));
		assertEquals(digest(gzip), responseIn.getHeader(ContentEncodingDigestUtils.HEADER_DIGEST));
		// la fruizione decomprime e verifica il Digest sui byte ricevuti
		assertEquals(200, response.statusCode());
		assertNull(header(response, ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING));
		assertArrayEquals(plain, response.body());

		verifyEsitoOk(response);
	}

	// *** D4-bis: byte compressi manomessi (contenuto in chiaro identico): il Digest non deve corrispondere ***

	@Test
	public void richiestaCompressaManomessaDecompressioneErogazione() throws Exception {
		verifyRichiestaManomessa(API_DECOMPRESS_EROGAZIONE_RICHIESTA, "D4bis-richiesta");
	}

	@Test
	public void rispostaCompressaManomessaDecompressioneFruizione() throws Exception {
		verifyRispostaManomessa(API_DECOMPRESS_FRUIZIONE_RISPOSTA, "D4bis-risposta");
	}

	// *** Controllo della manomissione senza decompressione ***

	@Test
	public void richiestaCompressaManomessa() throws Exception {
		verifyRichiestaManomessa(API, "manomissione-richiesta");
	}

	@Test
	public void rispostaCompressaManomessa() throws Exception {
		verifyRispostaManomessa(API, "manomissione-risposta");
	}

	// *** D5: contenuti non compressi (riferimento) ***

	@Test
	public void nonCompresso() throws Exception {
		verifyNonCompresso(API);
	}

	@Test
	public void nonCompressoDecompressioneFruizioneRichiesta() throws Exception {
		verifyNonCompresso(API_DECOMPRESS_FRUIZIONE_RICHIESTA);
	}

	@Test
	public void nonCompressoDecompressioneErogazioneRichiesta() throws Exception {
		verifyNonCompresso(API_DECOMPRESS_EROGAZIONE_RICHIESTA);
	}

	@Test
	public void nonCompressoDecompressioneFruizioneRisposta() throws Exception {
		verifyNonCompresso(API_DECOMPRESS_FRUIZIONE_RISPOSTA);
	}



	// *** Verifiche comuni ***

	private static void verifyRichiestaManomessa(String api, String label) throws Exception {
		byte[] plain = ContentEncodingDigestUtils.jsonPayload(label);
		byte[] gzip = ContentEncodingDigestUtils.gzip(plain);
		prepare(true, false, null, ContentEncodingDigestUtils.jsonPayload(label + "-risposta"));

		HttpResponse<byte[]> response = invoke(api, ContentEncodingDigestUtils.GZIP, gzip);

		// la manomissione cambia i byte compressi ma non il contenuto in chiaro
		ContentEncodingDigestExchange requestOut = proxy.getRequestOut();
		assertFalse(Arrays.equals(gzip, requestOut.getBody()));
		assertArrayEquals(plain, ContentEncodingDigestUtils.gunzip(requestOut.getBody()));
		assertEquals(digest(gzip), requestOut.getHeader(ContentEncodingDigestUtils.HEADER_DIGEST));

		// l'erogazione rifiuta la richiesta, che non arriva al backend
		assertNotEquals(200, response.statusCode());
		assertNull("richiesta manomessa consegnata al backend", mock.getLastRequest());
		String idErogazione = proxy.getResponseIn().getHeader(HEADER_TRANSACTION_ID);
		assertNotNull(idErogazione);
		assertNotEquals(ESITO_OK, getEsito(idErogazione));
		verifyDiagnostico(idErogazione, DIGEST_NON_CORRISPONDENTE);
		// l'unico errore è il Digest: il Content-Encoding firmato viene verificato anche se rimosso dalla decompressione
		verifyDiagnosticoAssente(idErogazione, HEADER_FIRMATO_NON_TROVATO);
	}

	private static void verifyRispostaManomessa(String api, String label) throws Exception {
		byte[] plain = ContentEncodingDigestUtils.jsonPayload(label);
		byte[] gzip = ContentEncodingDigestUtils.gzip(plain);
		prepare(false, true, ContentEncodingDigestUtils.GZIP, gzip);

		HttpResponse<byte[]> response = invoke(api, null, ContentEncodingDigestUtils.jsonPayload(label + "-richiesta"));

		// la manomissione cambia i byte compressi ma non il contenuto in chiaro
		ContentEncodingDigestExchange responseOut = proxy.getResponseOut();
		assertFalse(Arrays.equals(gzip, responseOut.getBody()));
		assertArrayEquals(plain, ContentEncodingDigestUtils.gunzip(responseOut.getBody()));
		assertEquals(digest(gzip), responseOut.getHeader(ContentEncodingDigestUtils.HEADER_DIGEST));

		// l'erogazione ha completato la transazione, la fruizione rifiuta la risposta
		String idErogazione = proxy.getResponseIn().getHeader(HEADER_TRANSACTION_ID);
		assertNotNull(idErogazione);
		assertEquals(ESITO_OK, getEsito(idErogazione));
		assertNotEquals(200, response.statusCode());
		String idFruizione = header(response, HEADER_TRANSACTION_ID);
		assertNotNull(idFruizione);
		assertNotEquals(ESITO_OK, getEsito(idFruizione));
		verifyDiagnostico(idFruizione, DIGEST_NON_CORRISPONDENTE);
		// l'unico errore è il Digest: il Content-Encoding firmato viene verificato anche se rimosso dalla decompressione
		verifyDiagnosticoAssente(idFruizione, HEADER_FIRMATO_NON_TROVATO);
	}

	private static void verifyNonCompresso(String api) throws Exception {
		byte[] plainRequest = ContentEncodingDigestUtils.jsonPayload("non-compresso-" + api);
		byte[] plainResponse = ContentEncodingDigestUtils.jsonPayload("non-compresso-risposta-" + api);
		prepare(false, false, null, plainResponse);

		HttpResponse<byte[]> response = invoke(api, null, plainRequest);

		assertEquals(200, response.statusCode());
		ContentEncodingDigestExchange requestIn = proxy.getRequestIn();
		assertNull(requestIn.getHeader(ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING));
		assertEquals(digest(plainRequest), requestIn.getHeader(ContentEncodingDigestUtils.HEADER_DIGEST));
		ContentEncodingDigestExchange responseIn = proxy.getResponseIn();
		assertNull(responseIn.getHeader(ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING));
		assertEquals(digest(plainResponse), responseIn.getHeader(ContentEncodingDigestUtils.HEADER_DIGEST));
		assertArrayEquals(plainRequest, mock.getLastRequest().getBody());
		assertNull(header(response, ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING));
		assertArrayEquals(plainResponse, response.body());

		verifyEsitoOk(response);
	}

	/** Transazioni di fruizione ed erogazione concluse con successo. */
	private static void verifyEsitoOk(HttpResponse<byte[]> response) throws InterruptedException {
		String idFruizione = header(response, HEADER_TRANSACTION_ID);
		assertNotNull(idFruizione);
		assertEquals(ESITO_OK, getEsito(idFruizione));
		String idErogazione = proxy.getResponseIn().getHeader(HEADER_TRANSACTION_ID);
		assertNotNull(idErogazione);
		assertEquals(ESITO_OK, getEsito(idErogazione));
	}



	// *** Utilità ***

	private static void prepare(boolean alterRequestGzip, boolean alterResponseGzip, String responseContentEncoding, byte[] responseBody) {
		proxy.reset(alterRequestGzip, alterResponseGzip);
		mock.setResponse(200, ContentEncodingDigestUtils.CONTENT_TYPE_JSON, responseContentEncoding, responseBody);
	}

	private static HttpResponse<byte[]> invoke(String api, String contentEncoding, byte[] body) throws Exception {
		String url = prop.getProperty("govway_base_path") + "/rest/out/" + SOGGETTO_FRUITORE + "/" + SOGGETTO_EROGATORE + "/" + api + "/v1" + RISORSA;
		String basic = Base64.getEncoder().encodeToString((APPLICATIVO + ":" + APPLICATIVO).getBytes(StandardCharsets.UTF_8));
		HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
				.timeout(Duration.ofMillis(Integer.parseInt(prop.getProperty("read_timeout"))))
				.header("Authorization", "Basic " + basic)
				.header(ContentEncodingDigestUtils.HEADER_CONTENT_TYPE, ContentEncodingDigestUtils.CONTENT_TYPE_JSON)
				.POST(HttpRequest.BodyPublishers.ofByteArray(body));
		if (contentEncoding != null) {
			builder.header(ContentEncodingDigestUtils.HEADER_CONTENT_ENCODING, contentEncoding);
		}
		HttpResponse<byte[]> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
		if (response.statusCode() == 502 && proxy.getResponseIn() == null) {
			fail("Errore del proxy di test durante l'inoltro all'erogazione (vedi output del test)");
		}
		return response;
	}

	private static String header(HttpResponse<byte[]> response, String name) {
		return response.headers().firstValue(name).orElse(null);
	}

	/** Valore dell'header Digest atteso (algoritmo e codifica di default ModI: SHA-256, base64). */
	private static String digest(byte[] content) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return "SHA-256=" + Base64.getEncoder().encodeToString(md.digest(content));
	}

	/** Esito della transazione; attende che venga registrata. */
	private static long getEsito(String idTransazione) throws InterruptedException {
		long deadline = System.currentTimeMillis() + dbRetryMaxTotalMs;
		while (true) {
			try {
				Object esito = dbUtils.readRow("select esito from transazioni where id=?", idTransazione).values().iterator().next();
				return ((Number) esito).longValue();
			} catch (EmptyResultDataAccessException e) {
				if (System.currentTimeMillis() > deadline) {
					fail("Transazione '" + idTransazione + "' non trovata");
				}
				Thread.sleep(dbRetryIntervalMs);
			}
		}
	}

	/** Verifica la presenza di un diagnostico contenente il frammento indicato; attende che venga registrato. */
	private static void verifyDiagnostico(String idTransazione, String frammento) throws InterruptedException {
		long deadline = System.currentTimeMillis() + dbRetryMaxTotalMs;
		while (true) {
			if (countDiagnostici(idTransazione, frammento) > 0) {
				return;
			}
			if (System.currentTimeMillis() > deadline) {
				fail("Diagnostico contenente '" + frammento + "' non trovato nella transazione '" + idTransazione + "'");
			}
			Thread.sleep(dbRetryIntervalMs);
		}
	}

	/**
	 * Verifica l'assenza di diagnostici contenenti il frammento indicato.
	 * Da invocare dopo {@link #verifyDiagnostico(String, String)} sulla stessa transazione, che garantisce che i diagnostici siano già registrati.
	 */
	private static void verifyDiagnosticoAssente(String idTransazione, String frammento) {
		assertEquals("Diagnostico contenente '" + frammento + "' non atteso nella transazione '" + idTransazione + "'",
				0, countDiagnostici(idTransazione, frammento));
	}

	private static long countDiagnostici(String idTransazione, String frammento) {
		Object count = dbUtils.readRow("select count(*) from msgdiagnostici where id_transazione=? and messaggio like ?",
				idTransazione, "%" + frammento + "%").values().iterator().next();
		return ((Number) count).longValue();
	}
}
