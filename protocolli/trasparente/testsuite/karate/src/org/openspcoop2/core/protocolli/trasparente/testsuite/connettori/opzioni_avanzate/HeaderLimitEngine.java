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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * Engine dei test sui limiti applicati dal connettore durante la lettura degli header HTTP della
 * risposta ricevuta dal backend:
 * <ul>
 *   <li>dimensione massima di un singolo header, 65536 bytes per default
 *       ('org.openspcoop2.pdd.connettori.syncClient.http1.maxHeaderLineLength' e
 *       'asyncClient.http1.maxHeaderLineLength');</li>
 *   <li>numero massimo di header presenti nella risposta, 250 per default
 *       ('...http1.maxHeaderCount').</li>
 * </ul>
 *
 * <p>Entrambi i limiti riguardano il solo protocollo HTTP/1.1 e sono implementati dalla libreria
 * Apache HttpClient 5: questa classe contiene quindi i soli casi che devono terminare con successo
 * su qualunque libreria configurata. I casi di superamento del limite, specifici della libreria
 * 'org.apache.hc.client5', sono in {@link HeaderLimitHttpCoreEngine}; il comportamento su HTTP/2 e
 * quello del connettore JDK sono verificati rispettivamente in {@link HttpCoreNIOHeaderLimitTest} e
 * {@link UrlConnBIOHeaderLimitTest}.
 *
 * <p>Il backend e' il mock {@link HeaderLimitMockServer}, che risponde con header di dimensione e
 * numero pilotati dal test e riporta il protocollo utilizzato.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HeaderLimitEngine extends ConfigLoader {

	/* ---------------------------- Setup mock backend ---------------------------- */

	private static HeaderLimitMockServer mockServer;
	private static final String PROP_MOCK_PORT = "connettori.opzioni_avanzate.headerLimit.mock.port";
	private static final String PROP_MOCK_PORT_HTTP2 = "connettori.opzioni_avanzate.headerLimit.mock.http2.port";

	@BeforeClass
	public static void startMock() throws IOException {
		int port = Integer.parseInt(System.getProperty(PROP_MOCK_PORT, "8094"));
		int portHttp2 = Integer.parseInt(System.getProperty(PROP_MOCK_PORT_HTTP2, "8095"));
		mockServer = new HeaderLimitMockServer(port, portHttp2);
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

	// Modalita' libreria HTTP impostata dalle sottoclassi (HC5 BIO/NIO, UrlConn BIO).
	// Se null, si usa la libreria di default configurata sul connettore.
	private HttpLibraryMode libraryMode = null;
	protected void setHttpLibraryMode(HttpLibraryMode mode) {
		this.libraryMode = mode;
	}

	/* ---------------------------- Costanti API e risorse ---------------------------- */

	private static final String API = "TestHeaderLimit";

	/** Risorsa la cui configurazione lascia il default 'NEGOTIATE': su cleartext si ottiene HTTP/1.1. */
	protected static final String RES_HTTP1 = "http1";
	/** Risorsa la cui configurazione forza HTTP/2 ('connettori.httpVersionPolicy=FORCE_HTTP_2'). */
	protected static final String RES_HTTP2 = "http2";

	protected static final String PROTOCOL_HTTP_1_1 = "HTTP/1.1";
	protected static final String PROTOCOL_HTTP_2 = "HTTP/2";

	/* Dimensioni e numerosita' degli header generati dal mock, rispetto ai default di GovWay
	 * (65536 bytes per singolo header, 250 header per messaggio). */

	/** Caso reale dei profili di sicurezza messaggio ModI: un header di circa 10 KB. */
	protected static final int HEADER_BYTES_MODIPA = 10 * 1024;
	/** Header di dimensione rilevante ma sotto il limite di default. */
	protected static final int HEADER_BYTES_UNDER_LIMIT = 60 * 1024;
	/** Header oltre il limite di default. */
	protected static final int HEADER_BYTES_OVER_LIMIT = 70 * 1024;
	/** Numero di header sotto il limite di default. */
	protected static final int HEADER_COUNT_UNDER_LIMIT = 200;
	/** Numero di header oltre il limite di default. */
	protected static final int HEADER_COUNT_OVER_LIMIT = 300;

	/** Frammenti dei diagnostici prodotti dalla libreria al superamento dei limiti. */
	protected static final String DIAG_MAX_LINE_LENGTH = "Maximum line length limit exceeded";
	protected static final String DIAG_MAX_HEADER_COUNT = "Maximum header count exceeded";

	private static final String REQUEST_BODY = "{\"headerLimit\":\"request\"}";

	private static final String HEADER_GOVWAY_TRANSACTION_ID = "GovWay-Transaction-ID";

	/* ====================================================================================== */
	/* =============================== @Test — casi con esito OK ============================= */
	/* ====================================================================================== */

	/*
	 * Il caso 'modipa' e' la verifica di non regressione della casistica per cui i limiti sono
	 * stati resi configurabili: un header di circa 10 KB, come quelli veicolati dai profili di
	 * sicurezza messaggio ModI, deve transitare senza errori.
	 */

	@Test
	public void erogazioneHeaderModipa() throws Exception {
		verifyOk(TipoServizio.EROGAZIONE, RES_HTTP1, HEADER_BYTES_MODIPA, 0, PROTOCOL_HTTP_1_1);
	}

	@Test
	public void fruizioneHeaderModipa() throws Exception {
		verifyOk(TipoServizio.FRUIZIONE, RES_HTTP1, HEADER_BYTES_MODIPA, 0, PROTOCOL_HTTP_1_1);
	}

	@Test
	public void erogazioneHeaderSottoLimite() throws Exception {
		verifyOk(TipoServizio.EROGAZIONE, RES_HTTP1, HEADER_BYTES_UNDER_LIMIT, 0, PROTOCOL_HTTP_1_1);
	}

	@Test
	public void fruizioneHeaderSottoLimite() throws Exception {
		verifyOk(TipoServizio.FRUIZIONE, RES_HTTP1, HEADER_BYTES_UNDER_LIMIT, 0, PROTOCOL_HTTP_1_1);
	}

	@Test
	public void erogazioneNumeroHeaderSottoLimite() throws Exception {
		verifyOk(TipoServizio.EROGAZIONE, RES_HTTP1, 0, HEADER_COUNT_UNDER_LIMIT, PROTOCOL_HTTP_1_1);
	}

	@Test
	public void fruizioneNumeroHeaderSottoLimite() throws Exception {
		verifyOk(TipoServizio.FRUIZIONE, RES_HTTP1, 0, HEADER_COUNT_UNDER_LIMIT, PROTOCOL_HTTP_1_1);
	}

	/* ====================================================================================== */
	/* ==================================== Engine ========================================== */
	/* ====================================================================================== */

	/**
	 * Invoca il gateway e verifica che la transazione termini con successo.
	 *
	 * @param expectedProtocolVersion prefisso della versione di protocollo attesa sulla tratta
	 *        gateway-backend, come riportata dal mock: il controllo viene effettuato sia sui casi
	 *        HTTP/1.1 che su quelli HTTP/2, poiche' e' il protocollo utilizzato a determinare quali
	 *        limiti vengono applicati
	 */
	protected void verifyOk(TipoServizio tipo, String resource, int headerBytes, int headerCount,
			String expectedProtocolVersion) throws Exception {

		HttpResponse response = invoke(tipo, resource, headerBytes, headerCount);

		assertEquals(200, response.getResultHTTPOperation());

		assertEquals(String.valueOf(headerBytes), response.getHeaderFirstValue(HeaderLimitMockServer.HEADER_RECEIVED_HEADER_BYTES));
		assertEquals(String.valueOf(headerCount), response.getHeaderFirstValue(HeaderLimitMockServer.HEADER_RECEIVED_HEADER_COUNT));

		String protocolVersion = response.getHeaderFirstValue(HeaderLimitMockServer.HEADER_RECEIVED_PROTOCOL_VERSION);
		assertNotNull(protocolVersion);
		assertTrue("Versione di protocollo attesa '" + expectedProtocolVersion + "', rilevata dal backend '" + protocolVersion + "'",
				protocolVersion.startsWith(expectedProtocolVersion));

		String idTransazione = response.getHeaderFirstValue(HEADER_GOVWAY_TRANSACTION_ID);
		assertNotNull(idTransazione);

		long esitoOk = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME)
				.convertoToCode(EsitoTransazioneName.OK);
		DBVerifier.verify(idTransazione, esitoOk, this.libraryMode);

		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_MAX_LINE_LENGTH);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_MAX_HEADER_COUNT);
	}

	/**
	 * Invoca il gateway e verifica che la risposta del backend venga rifiutata dal connettore:
	 * la transazione termina con errore di invocazione e il client riceve un problem detail
	 * 'APIUnavailable' con codice HTTP 503.
	 *
	 * @param diagnosticoAtteso frammento del diagnostico prodotto dalla libreria http
	 */
	protected void verifyKo(TipoServizio tipo, String resource, int headerBytes, int headerCount,
			String diagnosticoAtteso) throws Exception {

		HttpResponse response = invoke(tipo, resource, headerBytes, headerCount);

		assertEquals(503, response.getResultHTTPOperation());
		assertNotNull(response.getContentType());
		assertTrue("Content-Type inatteso: " + response.getContentType(),
				response.getContentType().contains(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807));
		assertNotNull(response.getContent());
		String problemDetail = new String(response.getContent());
		assertTrue("Problem detail inatteso: " + problemDetail, problemDetail.contains("APIUnavailable"));

		String idTransazione = response.getHeaderFirstValue(HEADER_GOVWAY_TRANSACTION_ID);
		assertNotNull(idTransazione);

		long esitoKo = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME)
				.convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE);
		DBVerifier.verify(idTransazione, esitoKo);

		DBVerifier.existsDiagnostico(idTransazione, diagnosticoAtteso);
	}

	private HttpResponse invoke(TipoServizio tipo, String resource, int headerBytes, int headerCount) throws UtilsException {

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		request.setContent(REQUEST_BODY.getBytes());
		request.setUrl(buildUrl(tipo, resource));

		request.addHeader(HeaderLimitMockServer.HEADER_REPLY_HEADER_BYTES, String.valueOf(headerBytes));
		request.addHeader(HeaderLimitMockServer.HEADER_REPLY_HEADER_COUNT, String.valueOf(headerCount));

		/*
		 * I limiti sotto test sono quelli applicati dal connettore in lettura della risposta del
		 * backend. Il client della testsuite deve invece poter leggere qualunque risposta il gateway
		 * propaghi, header sovradimensionati compresi, altrimenti sarebbe lui a rifiutare la risposta
		 * (utilizza la medesima implementazione http e i medesimi default di prodotto del connettore).
		 * I controlli vengono quindi disabilitati sulla richiesta indicando il valore 0.
		 */
		request.setMaxHeaderLineLength(0);
		request.setMaxHeaderCount(0);

		if (this.libraryMode != null) {
			this.libraryMode.patchRequest(request);
		}

		return HttpUtilities.httpInvoke(request);
	}

	private String buildUrl(TipoServizio tipo, String resource) {
		String base = System.getProperty("govway_base_path");
		if (TipoServizio.EROGAZIONE.equals(tipo)) {
			return base + "/in/SoggettoInternoTest/" + API + "/v1/" + resource;
		}
		return base + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/" + API + "/v1/" + resource;
	}
}
