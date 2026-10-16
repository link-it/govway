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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibrary;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * PayloadMetodiHttpTestEngine: verifica il contenuto inoltrato dai connettori per ciascun metodo HTTP.
 *
 * Comportamento atteso:
 * - POST, PUT, PATCH: il contenuto viene inoltrato;
 * - OPTIONS: il contenuto viene inoltrato se presente insieme al Content-Type (RFC 9110 9.3.7),
 *   indipendentemente dalla proprieta' 'connettori.request.forceSendContent.enabled' (la risorsa 'forzato' non lo dichiara);
 * - GET, DELETE, HEAD: il contenuto non viene inoltrato (OpenAPI 3.0 'requestBody', RFC 9110 9.3.1/9.3.2/9.3.5),
 *   salvo abilitazione della proprieta' 'connettori.request.forceSendContent.enabled' (risorsa 'forzato');
 *   con HttpURLConnection la GET con contenuto non e' supportata, poiche' la libreria la trasformerebbe in POST;
 * - TRACE: il contenuto non viene mai inoltrato (RFC 9110 9.3.8);
 * - quando il contenuto non viene inoltrato non deve essere inoltrato neanche il Content-Type e
 *   deve essere registrato un diagnostico.
 *
 * Il client di test utilizza 'java.net.http.HttpClient', l'unico in grado di inviare un contenuto con
 * qualsiasi metodo: le utility http di GovWay applicano infatti le medesime regole dei connettori.
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class PayloadMetodiHttpTestEngine extends ConfigLoader {

	/* ---------------------------- Setup mock backend ---------------------------- */

	private static PayloadMetodiHttpMockServer mockServer;
	private static final String PROP_MOCK_PORT = "connettori.opzioni_avanzate.payloadMetodiHttp.mock.port";

	@BeforeClass
	public static void startMock() throws IOException {
		int port = Integer.parseInt(System.getProperty(PROP_MOCK_PORT, "8096"));
		mockServer = new PayloadMetodiHttpMockServer(port);
		mockServer.start();
	}

	@AfterClass
	public static void stopMock() {
		if (mockServer != null) {
			mockServer.stop();
			mockServer = null;
		}
	}

	/* ---------------------------- Modalita' libreria HTTP ---------------------------- */

	private static final String LIBRARY_HEADER = "GovWay-TestSuite-HttpLibrary";

	private final HttpLibraryMode libraryMode;

	protected PayloadMetodiHttpTestEngine(HttpLibraryMode libraryMode) {
		this.libraryMode = libraryMode;
	}

	/* ---------------------------- Costanti ---------------------------- */

	private static final String API = "TestPayloadMetodiHttp";

	/** Risorsa con il comportamento di default. */
	private static final String RES_STANDARD = "standard";
	/** Risorsa con la proprieta' 'connettori.request.forceSendContent.enabled=true'. */
	private static final String RES_FORZATO = "forzato";

	private static final String GET = "GET";
	private static final String DELETE = "DELETE";
	private static final String HEAD = "HEAD";
	private static final String OPTIONS = "OPTIONS";
	private static final String TRACE = "TRACE";
	private static final String POST = "POST";
	private static final String PUT = "PUT";
	private static final String PATCH = "PATCH";

	private static final String CONTENT_TYPE = HttpConstants.CONTENT_TYPE_JSON;
	private static final String BODY = "{\"payloadMetodiHttp\":\"richiesta\"}";

	/** Diagnostico emesso quando il contenuto della richiesta non viene inoltrato al backend. */
	protected static final String DIAG_CONTENUTO_NON_INOLTRATO = "Contenuto della richiesta non inoltrato al backend";
	/** Frammento dell'errore sollevato dal connettore HttpURLConnection per una GET con contenuto da inoltrare. */
	protected static final String DIAG_GET_NON_SUPPORTATA = "con metodo HTTP GET non supportato dalla libreria HttpURLConnection";

	private static final String HEADER_GOVWAY_TRANSACTION_ID = "GovWay-Transaction-ID";

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
			.version(HttpClient.Version.HTTP_1_1)
			.connectTimeout(Duration.ofSeconds(10))
			.build();

	private enum Contenuto {
		/** Richiesta con contenuto e Content-Type. */
		PAYLOAD,
		/** Richiesta senza contenuto e senza Content-Type. */
		NESSUNO,
		/** Richiesta senza contenuto ma con il Content-Type. */
		SOLO_CONTENT_TYPE
	}



	/* ====================================================================================== */
	/* ======================= Risorsa 'standard' (comportamento di default) ================= */
	/* ====================================================================================== */

	// GET, DELETE, HEAD: contenuto non inoltrato

	@Test
	public void erogazioneStandardGetConPayload() throws Exception {
		verifyNonInoltrato(TipoServizio.EROGAZIONE, RES_STANDARD, GET);
	}
	@Test
	public void fruizioneStandardGetConPayload() throws Exception {
		verifyNonInoltrato(TipoServizio.FRUIZIONE, RES_STANDARD, GET);
	}

	@Test
	public void erogazioneStandardDeleteConPayload() throws Exception {
		verifyNonInoltrato(TipoServizio.EROGAZIONE, RES_STANDARD, DELETE);
	}
	@Test
	public void fruizioneStandardDeleteConPayload() throws Exception {
		verifyNonInoltrato(TipoServizio.FRUIZIONE, RES_STANDARD, DELETE);
	}

	@Test
	public void erogazioneStandardHeadConPayload() throws Exception {
		verifyNonInoltrato(TipoServizio.EROGAZIONE, RES_STANDARD, HEAD);
	}
	@Test
	public void fruizioneStandardHeadConPayload() throws Exception {
		verifyNonInoltrato(TipoServizio.FRUIZIONE, RES_STANDARD, HEAD);
	}

	// OPTIONS: contenuto inoltrato (RFC 9110 9.3.7)

	@Test
	public void erogazioneStandardOptionsConPayload() throws Exception {
		verifyInoltrato(TipoServizio.EROGAZIONE, RES_STANDARD, OPTIONS);
	}
	@Test
	public void fruizioneStandardOptionsConPayload() throws Exception {
		verifyInoltrato(TipoServizio.FRUIZIONE, RES_STANDARD, OPTIONS);
	}

	// TRACE: contenuto mai inoltrato (RFC 9110 9.3.8)

	@Test
	public void erogazioneStandardTraceConPayload() throws Exception {
		verifyNonInoltrato(TipoServizio.EROGAZIONE, RES_STANDARD, TRACE);
	}
	@Test
	public void fruizioneStandardTraceConPayload() throws Exception {
		verifyNonInoltrato(TipoServizio.FRUIZIONE, RES_STANDARD, TRACE);
	}

	// Richieste senza contenuto

	@Test
	public void erogazioneStandardGetSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.EROGAZIONE, RES_STANDARD, GET, Contenuto.NESSUNO);
	}
	@Test
	public void fruizioneStandardGetSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.FRUIZIONE, RES_STANDARD, GET, Contenuto.NESSUNO);
	}

	@Test
	public void erogazioneStandardDeleteSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.EROGAZIONE, RES_STANDARD, DELETE, Contenuto.NESSUNO);
	}
	@Test
	public void fruizioneStandardDeleteSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.FRUIZIONE, RES_STANDARD, DELETE, Contenuto.NESSUNO);
	}

	@Test
	public void erogazioneStandardHeadSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.EROGAZIONE, RES_STANDARD, HEAD, Contenuto.NESSUNO);
	}
	@Test
	public void fruizioneStandardHeadSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.FRUIZIONE, RES_STANDARD, HEAD, Contenuto.NESSUNO);
	}

	@Test
	public void erogazioneStandardOptionsSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.EROGAZIONE, RES_STANDARD, OPTIONS, Contenuto.NESSUNO);
	}
	@Test
	public void fruizioneStandardOptionsSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.FRUIZIONE, RES_STANDARD, OPTIONS, Contenuto.NESSUNO);
	}

	@Test
	public void erogazioneStandardTraceSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.EROGAZIONE, RES_STANDARD, TRACE, Contenuto.NESSUNO);
	}
	@Test
	public void fruizioneStandardTraceSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.FRUIZIONE, RES_STANDARD, TRACE, Contenuto.NESSUNO);
	}

	// Content-Type senza contenuto: il Content-Type non deve essere inoltrato

	@Test
	public void erogazioneStandardDeleteSoloContentType() throws Exception {
		verifySenzaContenuto(TipoServizio.EROGAZIONE, RES_STANDARD, DELETE, Contenuto.SOLO_CONTENT_TYPE);
	}
	@Test
	public void fruizioneStandardDeleteSoloContentType() throws Exception {
		verifySenzaContenuto(TipoServizio.FRUIZIONE, RES_STANDARD, DELETE, Contenuto.SOLO_CONTENT_TYPE);
	}

	// Non regressione: metodi con semantica definita per il contenuto

	@Test
	public void erogazioneStandardPostConPayload() throws Exception {
		verifyInoltrato(TipoServizio.EROGAZIONE, RES_STANDARD, POST);
	}
	@Test
	public void fruizioneStandardPostConPayload() throws Exception {
		verifyInoltrato(TipoServizio.FRUIZIONE, RES_STANDARD, POST);
	}

	@Test
	public void erogazioneStandardPutConPayload() throws Exception {
		verifyInoltrato(TipoServizio.EROGAZIONE, RES_STANDARD, PUT);
	}
	@Test
	public void fruizioneStandardPutConPayload() throws Exception {
		verifyInoltrato(TipoServizio.FRUIZIONE, RES_STANDARD, PUT);
	}

	@Test
	public void erogazioneStandardPatchConPayload() throws Exception {
		verifyInoltrato(TipoServizio.EROGAZIONE, RES_STANDARD, PATCH);
	}
	@Test
	public void fruizioneStandardPatchConPayload() throws Exception {
		verifyInoltrato(TipoServizio.FRUIZIONE, RES_STANDARD, PATCH);
	}



	/* ====================================================================================== */
	/* ============ Risorsa 'forzato' (connettori.request.forceSendContent.enabled) ========= */
	/* ====================================================================================== */

	// GET: inoltrata, salvo con HttpURLConnection dove viene restituito un errore

	@Test
	public void erogazioneForzatoGetConPayload() throws Exception {
		verifyForzatoGet(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneForzatoGetConPayload() throws Exception {
		verifyForzatoGet(TipoServizio.FRUIZIONE);
	}

	// DELETE, HEAD: contenuto inoltrato

	@Test
	public void erogazioneForzatoDeleteConPayload() throws Exception {
		verifyInoltrato(TipoServizio.EROGAZIONE, RES_FORZATO, DELETE);
	}
	@Test
	public void fruizioneForzatoDeleteConPayload() throws Exception {
		verifyInoltrato(TipoServizio.FRUIZIONE, RES_FORZATO, DELETE);
	}

	@Test
	public void erogazioneForzatoHeadConPayload() throws Exception {
		verifyInoltrato(TipoServizio.EROGAZIONE, RES_FORZATO, HEAD);
	}
	@Test
	public void fruizioneForzatoHeadConPayload() throws Exception {
		verifyInoltrato(TipoServizio.FRUIZIONE, RES_FORZATO, HEAD);
	}

	// TRACE: la proprieta' non ha effetto

	@Test
	public void erogazioneForzatoTraceConPayload() throws Exception {
		verifyNonInoltrato(TipoServizio.EROGAZIONE, RES_FORZATO, TRACE);
	}
	@Test
	public void fruizioneForzatoTraceConPayload() throws Exception {
		verifyNonInoltrato(TipoServizio.FRUIZIONE, RES_FORZATO, TRACE);
	}

	// Richieste senza contenuto: nessun effetto della proprieta' (anche per la GET con HttpURLConnection)

	@Test
	public void erogazioneForzatoGetSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.EROGAZIONE, RES_FORZATO, GET, Contenuto.NESSUNO);
	}
	@Test
	public void fruizioneForzatoGetSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.FRUIZIONE, RES_FORZATO, GET, Contenuto.NESSUNO);
	}

	@Test
	public void erogazioneForzatoDeleteSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.EROGAZIONE, RES_FORZATO, DELETE, Contenuto.NESSUNO);
	}
	@Test
	public void fruizioneForzatoDeleteSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.FRUIZIONE, RES_FORZATO, DELETE, Contenuto.NESSUNO);
	}

	@Test
	public void erogazioneForzatoHeadSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.EROGAZIONE, RES_FORZATO, HEAD, Contenuto.NESSUNO);
	}
	@Test
	public void fruizioneForzatoHeadSenzaPayload() throws Exception {
		verifySenzaContenuto(TipoServizio.FRUIZIONE, RES_FORZATO, HEAD, Contenuto.NESSUNO);
	}

	@Test
	public void erogazioneForzatoDeleteSoloContentType() throws Exception {
		verifySenzaContenuto(TipoServizio.EROGAZIONE, RES_FORZATO, DELETE, Contenuto.SOLO_CONTENT_TYPE);
	}
	@Test
	public void fruizioneForzatoDeleteSoloContentType() throws Exception {
		verifySenzaContenuto(TipoServizio.FRUIZIONE, RES_FORZATO, DELETE, Contenuto.SOLO_CONTENT_TYPE);
	}



	/* ====================================================================================== */
	/* ==================================== Engine ========================================== */
	/* ====================================================================================== */

	private void verifyForzatoGet(TipoServizio tipo) throws Exception {
		if (HttpLibrary.URLCONNECTION.equals(this.libraryMode.getLibrary()) && !this.libraryMode.isNIO()) {
			verifyGetNonSupportata(tipo);
		} else {
			verifyInoltrato(tipo, RES_FORZATO, GET);
		}
	}

	/**
	 * Il backend deve ricevere il metodo invocato, il contenuto e il Content-Type.
	 */
	private void verifyInoltrato(TipoServizio tipo, String resource, String method) throws Exception {

		HttpResponse<byte[]> response = invoke(tipo, resource, method, Contenuto.PAYLOAD);

		assertEquals("Codice HTTP", 200, response.statusCode());
		assertRicevutoDalBackend(response, method);

		assertEquals("Contenuto ricevuto dal backend", String.valueOf(BODY.getBytes(StandardCharsets.UTF_8).length),
				header(response, PayloadMetodiHttpMockServer.HEADER_RECEIVED_BODY_LENGTH));
		String bodyBase64 = header(response, PayloadMetodiHttpMockServer.HEADER_RECEIVED_BODY);
		assertNotNull("Contenuto ricevuto dal backend", bodyBase64);
		assertEquals("Contenuto ricevuto dal backend", BODY, new String(Base64.getDecoder().decode(bodyBase64), StandardCharsets.UTF_8));

		String contentType = header(response, PayloadMetodiHttpMockServer.HEADER_RECEIVED_CONTENT_TYPE);
		assertNotNull(contentType);
		assertTrue("Content-Type ricevuto dal backend: " + contentType, contentType.startsWith(CONTENT_TYPE));

		String idTransazione = verifyEsito(response, EsitoTransazioneName.OK);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_CONTENUTO_NON_INOLTRATO);
	}

	/**
	 * Il backend deve ricevere il metodo invocato senza contenuto e senza Content-Type,
	 * e deve essere registrato il diagnostico (warning) che segnala il contenuto non inoltrato.
	 */
	private void verifyNonInoltrato(TipoServizio tipo, String resource, String method) throws Exception {

		HttpResponse<byte[]> response = invoke(tipo, resource, method, Contenuto.PAYLOAD);

		assertEquals("Codice HTTP", 200, response.statusCode());
		assertRicevutoDalBackend(response, method);
		assertNessunContenutoRicevuto(response);

		// il diagnostico e' classificato come warning (livello errorIntegration): la transazione risulta 'OK con anomalie'
		String idTransazione = verifyEsito(response, EsitoTransazioneName.OK_PRESENZA_ANOMALIE);
		DBVerifier.existsDiagnostico(idTransazione, DIAG_CONTENUTO_NON_INOLTRATO);
	}

	/**
	 * Richiesta senza contenuto: il backend non deve ricevere ne' contenuto ne' Content-Type
	 * e non deve essere registrato alcun diagnostico di contenuto non inoltrato.
	 */
	private void verifySenzaContenuto(TipoServizio tipo, String resource, String method, Contenuto contenuto) throws Exception {

		HttpResponse<byte[]> response = invoke(tipo, resource, method, contenuto);

		assertEquals("Codice HTTP", 200, response.statusCode());
		assertRicevutoDalBackend(response, method);
		assertNessunContenutoRicevuto(response);

		String idTransazione = verifyEsito(response, EsitoTransazioneName.OK);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_CONTENUTO_NON_INOLTRATO);
	}

	/**
	 * Con HttpURLConnection una GET con contenuto verrebbe trasformata in POST dalla libreria:
	 * il connettore deve terminare la richiesta con un errore esplicito, senza contattare il backend.
	 */
	private void verifyGetNonSupportata(TipoServizio tipo) throws Exception {

		HttpResponse<byte[]> response = invoke(tipo, RES_FORZATO, GET, Contenuto.PAYLOAD);

		assertEquals("Codice HTTP", 503, response.statusCode());
		String contentType = response.headers().firstValue(HttpConstants.CONTENT_TYPE).orElse(null);
		assertNotNull(contentType);
		assertTrue("Content-Type inatteso: " + contentType, contentType.contains(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807));
		String problemDetail = new String(response.body(), StandardCharsets.UTF_8);
		assertTrue("Problem detail inatteso: " + problemDetail, problemDetail.contains("APIUnavailable"));

		assertEquals("Il backend non deve essere stato invocato", null, header(response, PayloadMetodiHttpMockServer.HEADER_RECEIVED_METHOD));

		String idTransazione = header(response, HEADER_GOVWAY_TRANSACTION_ID);
		assertNotNull(idTransazione);
		long esitoKo = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME)
				.convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE);
		DBVerifier.verify(idTransazione, esitoKo);
		DBVerifier.existsDiagnostico(idTransazione, DIAG_GET_NON_SUPPORTATA);
	}

	private static void assertRicevutoDalBackend(HttpResponse<byte[]> response, String method) {
		assertEquals("Metodo ricevuto dal backend", method, header(response, PayloadMetodiHttpMockServer.HEADER_RECEIVED_METHOD));
		if (!HEAD.equals(method)) {
			String body = new String(response.body(), StandardCharsets.UTF_8);
			assertTrue("Risposta del backend non ricevuta: " + body, body.contains("\"method\":\"" + method + "\""));
		}
		else {
			assertEquals("La risposta ad una HEAD non deve avere contenuto", 0, response.body().length);
		}
	}

	private static void assertNessunContenutoRicevuto(HttpResponse<byte[]> response) {
		assertEquals("Contenuto ricevuto dal backend", "0", header(response, PayloadMetodiHttpMockServer.HEADER_RECEIVED_BODY_LENGTH));
		assertEquals("Content-Type ricevuto dal backend", PayloadMetodiHttpMockServer.VALUE_ASSENTE,
				header(response, PayloadMetodiHttpMockServer.HEADER_RECEIVED_CONTENT_TYPE));
	}

	private String verifyEsito(HttpResponse<byte[]> response, EsitoTransazioneName esitoAtteso) throws Exception {
		String idTransazione = header(response, HEADER_GOVWAY_TRANSACTION_ID);
		assertNotNull(idTransazione);
		long esito = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME)
				.convertoToCode(esitoAtteso);
		DBVerifier.verify(idTransazione, esito, this.libraryMode);
		return idTransazione;
	}

	private static String header(HttpResponse<byte[]> response, String name) {
		return response.headers().firstValue(name).orElse(null);
	}

	private HttpResponse<byte[]> invoke(TipoServizio tipo, String resource, String method, Contenuto contenuto) throws Exception {

		HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(buildUrl(tipo, resource)))
				.timeout(Duration.ofSeconds(20));

		switch (contenuto) {
		case PAYLOAD:
			builder.header(HttpConstants.CONTENT_TYPE, CONTENT_TYPE);
			builder.method(method, HttpRequest.BodyPublishers.ofString(BODY, StandardCharsets.UTF_8));
			break;
		case SOLO_CONTENT_TYPE:
			builder.header(HttpConstants.CONTENT_TYPE, CONTENT_TYPE);
			builder.method(method, HttpRequest.BodyPublishers.noBody());
			break;
		case NESSUNO:
		default:
			builder.method(method, HttpRequest.BodyPublishers.noBody());
			break;
		}

		builder.header(LIBRARY_HEADER, this.libraryMode.getLibrary().getJavaLibrary());

		return HTTP_CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
	}

	private String buildUrl(TipoServizio tipo, String resource) {
		String base = System.getProperty("govway_base_path");
		String async = this.libraryMode.isNIO() ? "async/" : "";
		if (TipoServizio.EROGAZIONE.equals(tipo)) {
			return base + "/in/" + async + "SoggettoInternoTest/" + API + "/v1/" + resource;
		}
		return base + "/out/" + async + "SoggettoInternoTestFruitore/SoggettoInternoTest/" + API + "/v1/" + resource;
	}
}
