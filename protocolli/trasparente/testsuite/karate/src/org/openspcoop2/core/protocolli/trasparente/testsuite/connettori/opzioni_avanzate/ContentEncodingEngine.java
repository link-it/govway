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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

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
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * Verifica della decompressione automatica del body lato richiesta e risposta, per i 4 stati della
 * property {@code connettori.contentEncoding.[request|response].decompress} (mapping risorsa
 * {@code default|request|response|both}), su erogazione e fruizione.
 *
 * <h2>Strategia</h2>
 * I test sono separati per direzione: {@link #_testRequest} codifica il body della richiesta lato
 * client; {@link #_testResponse} chiede al mock backend di rispondere con body compresso. Non si
 * testano combinazioni request+response in un singolo scenario.
 *
 * <h2>Mock backend</h2>
 * {@link ContentEncodingMockServer} fa echo del body ricevuto e pilota l'encoding di response via
 * header {@code govway-testsuite-reply-encoding}. Tutti i metadati utili al test (Content-Encoding,
 * Content-Length, Accept-Encoding ricevuti, magic dei primi byte) sono restituiti come header
 * {@code received-*} (in minuscolo per robustezza Tomcat) nella response.
 *
 * <h2>Naming risorse</h2>
 * <ul>
 *   <li>{@code default} → nessuna property → passthrough</li>
 *   <li>{@code request} → {@code connettori.contentEncoding.request.decompress=true}</li>
 *   <li>{@code response} → {@code connettori.contentEncoding.response.decompress=true}</li>
 *   <li>{@code both} → {@code connettori.contentEncoding.decompress=true} (ombrello)</li>
 * </ul>
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentEncodingEngine extends ConfigLoader {

	/* ---------------------------- Setup mock backend ---------------------------- */

	private static ContentEncodingMockServer mockServer;
	private static final String PROP_MOCK_PORT = "connettori.opzioni_avanzate.contentEncoding.mock.port";

	@BeforeClass
	public static void startMock() throws IOException {
		int port = Integer.parseInt(System.getProperty(PROP_MOCK_PORT, "8093"));
		mockServer = new ContentEncodingMockServer(port);
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

	// API standard (registrazione messaggi NON attiva)
	private static final String API_STD = "TestContentEncoding";
	// API gemella con registrazione messaggi attiva (per verifiche su dump_messaggi)
	private static final String API_REG = "TestContentEncodingRegistrazione";

	// Nomi delle risorse (=path nell'OpenAPI) — equivalenti alle azioni invocate
	private static final String RES_DEFAULT  = "default";
	private static final String RES_REQUEST  = "request";
	private static final String RES_RESPONSE = "response";
	private static final String RES_BOTH     = "both";

	// Encoding usati dai test (i 4 supportati + identity + 1 non gestito)
	private static final String ENC_GZIP         = "gzip";
	private static final String ENC_X_GZIP       = "x-gzip";
	private static final String ENC_DEFLATE_ZLIB = "deflate-zlib";  // RFC 1950 zlib-wrapped
	private static final String ENC_DEFLATE_RAW  = "deflate-raw";   // RFC 1951 raw
	private static final String ENC_IDENTITY     = "identity";
	private static final String ENC_BROTLI       = "br";            // non gestito

	// Frammenti dei diagnostici emessi da GovWay
	private static final String DIAG_DECOMPRESSED = "applicata decompressione automatica";
	private static final String DIAG_UNSUPPORTED  = "non gestibile dalla decompressione automatica";

	// Body del test (plain): da inviare nella request o da ricevere come echo dal mock
	private static final String PLAIN_BODY = "{\"contentEncoding\":\"test-payload\",\"random\":\"abcdefghijklmnopqrstuvwxyz\"}";

	/* ---------------------------- Enum di esito atteso ---------------------------- */

	/**
	 * Esito atteso di un test.
	 * <ul>
	 *   <li>{@code PASSTHROUGH}: GovWay non decomprime; body raw passa fino al destinatario; nessun
	 *       diagnostico decompressed/unsupported emesso.</li>
	 *   <li>{@code DECOMPRESSED}: GovWay decomprime; il destinatario riceve body in chiaro; viene
	 *       emesso il diagnostico decompressed.</li>
	 *   <li>{@code UNSUPPORTED_ERROR}: GovWay rifiuta perche' l'encoding non e' gestito; viene
	 *       emesso il diagnostico unsupported; la chiamata fallisce.</li>
	 * </ul>
	 */
	private enum Outcome {
		PASSTHROUGH,
		DECOMPRESSED,
		UNSUPPORTED_ERROR
	}

	private enum Side { REQUEST, RESPONSE }

	/* ====================================================================================== */
	/* ============================ @Test — REQUEST side, API standard ====================== */
	/* ====================================================================================== */

	/* default: nessuna property -> passthrough per qualunque encoding. */
	@Test public void erogazioneRequestDefaultGzip()        throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_DEFAULT, ENC_GZIP,         Outcome.PASSTHROUGH); }
	@Test public void erogazioneRequestDefaultXGzip()       throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_DEFAULT, ENC_X_GZIP,       Outcome.PASSTHROUGH); }
	@Test public void erogazioneRequestDefaultDeflateZlib() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_DEFAULT, ENC_DEFLATE_ZLIB, Outcome.PASSTHROUGH); }
	@Test public void erogazioneRequestDefaultDeflateRaw()  throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_DEFAULT, ENC_DEFLATE_RAW,  Outcome.PASSTHROUGH); }
	@Test public void erogazioneRequestDefaultIdentity()    throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_DEFAULT, ENC_IDENTITY,     Outcome.PASSTHROUGH); }
	@Test public void erogazioneRequestDefaultBrotli()      throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_DEFAULT, ENC_BROTLI,       Outcome.PASSTHROUGH); }
	@Test public void fruizioneRequestDefaultGzip()         throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_DEFAULT, ENC_GZIP,         Outcome.PASSTHROUGH); }
	@Test public void fruizioneRequestDefaultXGzip()        throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_DEFAULT, ENC_X_GZIP,       Outcome.PASSTHROUGH); }
	@Test public void fruizioneRequestDefaultDeflateZlib()  throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_DEFAULT, ENC_DEFLATE_ZLIB, Outcome.PASSTHROUGH); }
	@Test public void fruizioneRequestDefaultDeflateRaw()   throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_DEFAULT, ENC_DEFLATE_RAW,  Outcome.PASSTHROUGH); }
	@Test public void fruizioneRequestDefaultIdentity()     throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_DEFAULT, ENC_IDENTITY,     Outcome.PASSTHROUGH); }
	@Test public void fruizioneRequestDefaultBrotli()       throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_DEFAULT, ENC_BROTLI,       Outcome.PASSTHROUGH); }

	/* request: decompress lato richiesta abilitato → encoding supportati decompresi, br rifiutato. */
	@Test public void erogazioneRequestRequestGzip()        throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_REQUEST, ENC_GZIP,         Outcome.DECOMPRESSED); }
	@Test public void erogazioneRequestRequestXGzip()       throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_REQUEST, ENC_X_GZIP,       Outcome.DECOMPRESSED); }
	@Test public void erogazioneRequestRequestDeflateZlib() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_REQUEST, ENC_DEFLATE_ZLIB, Outcome.DECOMPRESSED); }
	@Test public void erogazioneRequestRequestDeflateRaw()  throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_REQUEST, ENC_DEFLATE_RAW,  Outcome.DECOMPRESSED); }
	@Test public void erogazioneRequestRequestIdentity()    throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_REQUEST, ENC_IDENTITY,     Outcome.PASSTHROUGH); }
	@Test public void erogazioneRequestRequestBrotli()      throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_REQUEST, ENC_BROTLI,       Outcome.UNSUPPORTED_ERROR); }
	@Test public void fruizioneRequestRequestGzip()         throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_REQUEST, ENC_GZIP,         Outcome.DECOMPRESSED); }
	@Test public void fruizioneRequestRequestXGzip()        throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_REQUEST, ENC_X_GZIP,       Outcome.DECOMPRESSED); }
	@Test public void fruizioneRequestRequestDeflateZlib()  throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_REQUEST, ENC_DEFLATE_ZLIB, Outcome.DECOMPRESSED); }
	@Test public void fruizioneRequestRequestDeflateRaw()   throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_REQUEST, ENC_DEFLATE_RAW,  Outcome.DECOMPRESSED); }
	@Test public void fruizioneRequestRequestIdentity()     throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_REQUEST, ENC_IDENTITY,     Outcome.PASSTHROUGH); }
	@Test public void fruizioneRequestRequestBrotli()       throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_REQUEST, ENC_BROTLI,       Outcome.UNSUPPORTED_ERROR); }

	/* response: decompress lato risposta abilitato, ma il body request non viene toccato → passthrough. */
	@Test public void erogazioneRequestResponseGzip()         throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_RESPONSE, ENC_GZIP,         Outcome.PASSTHROUGH); }
	@Test public void erogazioneRequestResponseDeflateZlib()  throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_RESPONSE, ENC_DEFLATE_ZLIB, Outcome.PASSTHROUGH); }
	@Test public void erogazioneRequestResponseBrotli()       throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_RESPONSE, ENC_BROTLI,       Outcome.PASSTHROUGH); }
	@Test public void fruizioneRequestResponseGzip()          throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_RESPONSE, ENC_GZIP,         Outcome.PASSTHROUGH); }
	@Test public void fruizioneRequestResponseDeflateZlib()   throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_RESPONSE, ENC_DEFLATE_ZLIB, Outcome.PASSTHROUGH); }
	@Test public void fruizioneRequestResponseBrotli()        throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_RESPONSE, ENC_BROTLI,       Outcome.PASSTHROUGH); }

	/* both: come request side per la richiesta. */
	@Test public void erogazioneRequestBothGzip()         throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_BOTH, ENC_GZIP,         Outcome.DECOMPRESSED); }
	@Test public void erogazioneRequestBothXGzip()        throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_BOTH, ENC_X_GZIP,       Outcome.DECOMPRESSED); }
	@Test public void erogazioneRequestBothDeflateZlib()  throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_BOTH, ENC_DEFLATE_ZLIB, Outcome.DECOMPRESSED); }
	@Test public void erogazioneRequestBothDeflateRaw()   throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_BOTH, ENC_DEFLATE_RAW,  Outcome.DECOMPRESSED); }
	@Test public void erogazioneRequestBothIdentity()     throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_BOTH, ENC_IDENTITY,     Outcome.PASSTHROUGH); }
	@Test public void erogazioneRequestBothBrotli()       throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_STD, RES_BOTH, ENC_BROTLI,       Outcome.UNSUPPORTED_ERROR); }
	@Test public void fruizioneRequestBothGzip()          throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_BOTH, ENC_GZIP,         Outcome.DECOMPRESSED); }
	@Test public void fruizioneRequestBothXGzip()         throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_BOTH, ENC_X_GZIP,       Outcome.DECOMPRESSED); }
	@Test public void fruizioneRequestBothDeflateZlib()   throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_BOTH, ENC_DEFLATE_ZLIB, Outcome.DECOMPRESSED); }
	@Test public void fruizioneRequestBothDeflateRaw()    throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_BOTH, ENC_DEFLATE_RAW,  Outcome.DECOMPRESSED); }
	@Test public void fruizioneRequestBothIdentity()      throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_BOTH, ENC_IDENTITY,     Outcome.PASSTHROUGH); }
	@Test public void fruizioneRequestBothBrotli()        throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_STD, RES_BOTH, ENC_BROTLI,       Outcome.UNSUPPORTED_ERROR); }

	/* ====================================================================================== */
	/* ============================ @Test — RESPONSE side, API standard ===================== */
	/* ====================================================================================== */

	/* default: passthrough per qualunque encoding response. */
	@Test public void erogazioneResponseDefaultGzip()         throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_DEFAULT, ENC_GZIP,         Outcome.PASSTHROUGH); }
	@Test public void erogazioneResponseDefaultXGzip()        throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_DEFAULT, ENC_X_GZIP,       Outcome.PASSTHROUGH); }
	@Test public void erogazioneResponseDefaultDeflateZlib()  throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_DEFAULT, ENC_DEFLATE_ZLIB, Outcome.PASSTHROUGH); }
	@Test public void erogazioneResponseDefaultDeflateRaw()   throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_DEFAULT, ENC_DEFLATE_RAW,  Outcome.PASSTHROUGH); }
	@Test public void erogazioneResponseDefaultIdentity()     throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_DEFAULT, ENC_IDENTITY,     Outcome.PASSTHROUGH); }
	@Test public void erogazioneResponseDefaultBrotli()       throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_DEFAULT, ENC_BROTLI,       Outcome.PASSTHROUGH); }
	@Test public void fruizioneResponseDefaultGzip()          throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_DEFAULT, ENC_GZIP,         Outcome.PASSTHROUGH); }
	@Test public void fruizioneResponseDefaultXGzip()         throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_DEFAULT, ENC_X_GZIP,       Outcome.PASSTHROUGH); }
	@Test public void fruizioneResponseDefaultDeflateZlib()   throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_DEFAULT, ENC_DEFLATE_ZLIB, Outcome.PASSTHROUGH); }
	@Test public void fruizioneResponseDefaultDeflateRaw()    throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_DEFAULT, ENC_DEFLATE_RAW,  Outcome.PASSTHROUGH); }
	@Test public void fruizioneResponseDefaultIdentity()      throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_DEFAULT, ENC_IDENTITY,     Outcome.PASSTHROUGH); }
	@Test public void fruizioneResponseDefaultBrotli()        throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_DEFAULT, ENC_BROTLI,       Outcome.PASSTHROUGH); }

	/* request: decompress request, ma la risposta non viene toccata → passthrough. */
	@Test public void erogazioneResponseRequestGzip()         throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_REQUEST, ENC_GZIP,         Outcome.PASSTHROUGH); }
	@Test public void erogazioneResponseRequestDeflateZlib()  throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_REQUEST, ENC_DEFLATE_ZLIB, Outcome.PASSTHROUGH); }
	@Test public void erogazioneResponseRequestBrotli()       throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_REQUEST, ENC_BROTLI,       Outcome.PASSTHROUGH); }
	@Test public void fruizioneResponseRequestGzip()          throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_REQUEST, ENC_GZIP,         Outcome.PASSTHROUGH); }
	@Test public void fruizioneResponseRequestDeflateZlib()   throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_REQUEST, ENC_DEFLATE_ZLIB, Outcome.PASSTHROUGH); }
	@Test public void fruizioneResponseRequestBrotli()        throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_REQUEST, ENC_BROTLI,       Outcome.PASSTHROUGH); }

	/* response: decompress lato risposta → encoding supportati decompresi, br rifiutato. */
	@Test public void erogazioneResponseResponseGzip()         throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_RESPONSE, ENC_GZIP,         Outcome.DECOMPRESSED); }
	@Test public void erogazioneResponseResponseXGzip()        throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_RESPONSE, ENC_X_GZIP,       Outcome.DECOMPRESSED); }
	@Test public void erogazioneResponseResponseDeflateZlib()  throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_RESPONSE, ENC_DEFLATE_ZLIB, Outcome.DECOMPRESSED); }
	@Test public void erogazioneResponseResponseDeflateRaw()   throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_RESPONSE, ENC_DEFLATE_RAW,  Outcome.DECOMPRESSED); }
	@Test public void erogazioneResponseResponseIdentity()     throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_RESPONSE, ENC_IDENTITY,     Outcome.PASSTHROUGH); }
	@Test public void erogazioneResponseResponseBrotli()       throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_RESPONSE, ENC_BROTLI,       Outcome.UNSUPPORTED_ERROR); }
	@Test public void fruizioneResponseResponseGzip()          throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_RESPONSE, ENC_GZIP,         Outcome.DECOMPRESSED); }
	@Test public void fruizioneResponseResponseXGzip()         throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_RESPONSE, ENC_X_GZIP,       Outcome.DECOMPRESSED); }
	@Test public void fruizioneResponseResponseDeflateZlib()   throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_RESPONSE, ENC_DEFLATE_ZLIB, Outcome.DECOMPRESSED); }
	@Test public void fruizioneResponseResponseDeflateRaw()    throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_RESPONSE, ENC_DEFLATE_RAW,  Outcome.DECOMPRESSED); }
	@Test public void fruizioneResponseResponseIdentity()      throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_RESPONSE, ENC_IDENTITY,     Outcome.PASSTHROUGH); }
	@Test public void fruizioneResponseResponseBrotli()        throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_RESPONSE, ENC_BROTLI,       Outcome.UNSUPPORTED_ERROR); }

	/* both: come response side per la risposta. */
	@Test public void erogazioneResponseBothGzip()         throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_BOTH, ENC_GZIP,         Outcome.DECOMPRESSED); }
	@Test public void erogazioneResponseBothXGzip()        throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_BOTH, ENC_X_GZIP,       Outcome.DECOMPRESSED); }
	@Test public void erogazioneResponseBothDeflateZlib()  throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_BOTH, ENC_DEFLATE_ZLIB, Outcome.DECOMPRESSED); }
	@Test public void erogazioneResponseBothDeflateRaw()   throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_BOTH, ENC_DEFLATE_RAW,  Outcome.DECOMPRESSED); }
	@Test public void erogazioneResponseBothIdentity()     throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_BOTH, ENC_IDENTITY,     Outcome.PASSTHROUGH); }
	@Test public void erogazioneResponseBothBrotli()       throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_BOTH, ENC_BROTLI,       Outcome.UNSUPPORTED_ERROR); }
	@Test public void fruizioneResponseBothGzip()          throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_BOTH, ENC_GZIP,         Outcome.DECOMPRESSED); }
	@Test public void fruizioneResponseBothXGzip()         throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_BOTH, ENC_X_GZIP,       Outcome.DECOMPRESSED); }
	@Test public void fruizioneResponseBothDeflateZlib()   throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_BOTH, ENC_DEFLATE_ZLIB, Outcome.DECOMPRESSED); }
	@Test public void fruizioneResponseBothDeflateRaw()    throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_BOTH, ENC_DEFLATE_RAW,  Outcome.DECOMPRESSED); }
	@Test public void fruizioneResponseBothIdentity()      throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_BOTH, ENC_IDENTITY,     Outcome.PASSTHROUGH); }
	@Test public void fruizioneResponseBothBrotli()        throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_BOTH, ENC_BROTLI,       Outcome.UNSUPPORTED_ERROR); }

	/* ====================================================================================== */
	/* ===================== @Test — REQUEST/RESPONSE su API REGISTRAZIONE ================== */
	/* Sottoinsieme dei test base + verifiche aggiuntive sul dump_messaggi (vedi verifyDump). */
	/* ====================================================================================== */

	@Test public void registrazioneErogazioneRequestRequestGzip()         throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REG, RES_REQUEST,  ENC_GZIP,         Outcome.DECOMPRESSED); }
	@Test public void registrazioneErogazioneRequestRequestDeflateZlib()  throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REG, RES_REQUEST,  ENC_DEFLATE_ZLIB, Outcome.DECOMPRESSED); }
	@Test public void registrazioneErogazioneRequestRequestBrotli()       throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REG, RES_REQUEST,  ENC_BROTLI,       Outcome.UNSUPPORTED_ERROR); }
	@Test public void registrazioneErogazioneRequestDefaultGzip()         throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REG, RES_DEFAULT,  ENC_GZIP,         Outcome.PASSTHROUGH); }
	@Test public void registrazioneErogazioneRequestBothGzip()            throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REG, RES_BOTH,     ENC_GZIP,         Outcome.DECOMPRESSED); }
	@Test public void registrazioneFruizioneRequestRequestGzip()          throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_REG, RES_REQUEST,  ENC_GZIP,         Outcome.DECOMPRESSED); }
	@Test public void registrazioneFruizioneRequestRequestBrotli()        throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_REG, RES_REQUEST,  ENC_BROTLI,       Outcome.UNSUPPORTED_ERROR); }
	@Test public void registrazioneFruizioneRequestDefaultGzip()          throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_REG, RES_DEFAULT,  ENC_GZIP,         Outcome.PASSTHROUGH); }
	@Test public void registrazioneFruizioneRequestBothGzip()             throws Throwable { _testRequest(TipoServizio.FRUIZIONE,  API_REG, RES_BOTH,     ENC_GZIP,         Outcome.DECOMPRESSED); }

	@Test public void registrazioneErogazioneResponseResponseGzip()        throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REG, RES_RESPONSE, ENC_GZIP,         Outcome.DECOMPRESSED); }
	@Test public void registrazioneErogazioneResponseResponseDeflateZlib() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REG, RES_RESPONSE, ENC_DEFLATE_ZLIB, Outcome.DECOMPRESSED); }
	@Test public void registrazioneErogazioneResponseResponseBrotli()      throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REG, RES_RESPONSE, ENC_BROTLI,       Outcome.UNSUPPORTED_ERROR); }
	@Test public void registrazioneErogazioneResponseDefaultGzip()         throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REG, RES_DEFAULT,  ENC_GZIP,         Outcome.PASSTHROUGH); }
	@Test public void registrazioneErogazioneResponseBothGzip()            throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REG, RES_BOTH,     ENC_GZIP,         Outcome.DECOMPRESSED); }
	@Test public void registrazioneFruizioneResponseResponseGzip()         throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_REG, RES_RESPONSE, ENC_GZIP,         Outcome.DECOMPRESSED); }
	@Test public void registrazioneFruizioneResponseResponseBrotli()       throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_REG, RES_RESPONSE, ENC_BROTLI,       Outcome.UNSUPPORTED_ERROR); }
	@Test public void registrazioneFruizioneResponseDefaultGzip()          throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_REG, RES_DEFAULT,  ENC_GZIP,         Outcome.PASSTHROUGH); }
	@Test public void registrazioneFruizioneResponseBothGzip()             throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_REG, RES_BOTH,     ENC_GZIP,         Outcome.DECOMPRESSED); }


	/* ====================================================================================== */
	/* ============ @Test — RESPONSE side con client Accept-Encoding (passthrough) ========== */
	/* Verifica che GovWay propaghi al backend l'header Accept-Encoding inviato dal client.   */
	/* Solo un sottoinsieme rappresentativo: copre i 4 stati property × 2 tipi servizio.      */
	/* ====================================================================================== */

	private static final String CLIENT_ACCEPT_ENCODING_SAMPLE = "gzip, deflate";

	@Test public void erogazioneResponseDefaultGzipAcceptEncoding()  throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_DEFAULT,  ENC_GZIP, Outcome.PASSTHROUGH,  CLIENT_ACCEPT_ENCODING_SAMPLE); }
	@Test public void erogazioneResponseRequestGzipAcceptEncoding()  throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_REQUEST,  ENC_GZIP, Outcome.PASSTHROUGH,  CLIENT_ACCEPT_ENCODING_SAMPLE); }
	@Test public void erogazioneResponseResponseGzipAcceptEncoding() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_RESPONSE, ENC_GZIP, Outcome.DECOMPRESSED, CLIENT_ACCEPT_ENCODING_SAMPLE); }
	@Test public void erogazioneResponseBothGzipAcceptEncoding()     throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_STD, RES_BOTH,     ENC_GZIP, Outcome.DECOMPRESSED, CLIENT_ACCEPT_ENCODING_SAMPLE); }
	@Test public void fruizioneResponseDefaultGzipAcceptEncoding()   throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_DEFAULT,  ENC_GZIP, Outcome.PASSTHROUGH,  CLIENT_ACCEPT_ENCODING_SAMPLE); }
	@Test public void fruizioneResponseRequestGzipAcceptEncoding()   throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_REQUEST,  ENC_GZIP, Outcome.PASSTHROUGH,  CLIENT_ACCEPT_ENCODING_SAMPLE); }
	@Test public void fruizioneResponseResponseGzipAcceptEncoding()  throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_RESPONSE, ENC_GZIP, Outcome.DECOMPRESSED, CLIENT_ACCEPT_ENCODING_SAMPLE); }
	@Test public void fruizioneResponseBothGzipAcceptEncoding()      throws Throwable { _testResponse(TipoServizio.FRUIZIONE,  API_STD, RES_BOTH,     ENC_GZIP, Outcome.DECOMPRESSED, CLIENT_ACCEPT_ENCODING_SAMPLE); }


	/* ====================================================================================== */
	/* ================================== ENGINE — REQUEST side ============================= */
	/* ====================================================================================== */

	/**
	 * Test della decompressione lato RICHIESTA: il client codifica il body con {@code encoding} e
	 * lo invia a GovWay. Il mock backend riceve la chiamata e restituisce nei header response i
	 * metadati di cosa ha visto. Il test verifica che:
	 * <ul>
	 *   <li>il body al backend sia decompresso se l'expectation e' DECOMPRESSED;</li>
	 *   <li>il body al backend sia raw (con il proprio Content-Encoding) se PASSTHROUGH;</li>
	 *   <li>la chiamata fallisca con esito non-OK + diagnostico unsupported se UNSUPPORTED_ERROR;</li>
	 *   <li>i diagnostici emessi siano coerenti con l'expectation;</li>
	 *   <li>per API_REG vengano registrati i dump del request body.</li>
	 * </ul>
	 */
	private void _testRequest(TipoServizio tipo, String api, String resource, String encoding, Outcome expected) throws Exception {
		byte[] plain = PLAIN_BODY.getBytes();
		byte[] requestBodyOnWire = applyEncoding(plain, encoding);

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		request.setContent(requestBodyOnWire);
		request.setUrl(buildUrl(tipo, api, resource));
		String wireContentEncoding = wireContentEncodingHeader(encoding);
		if (wireContentEncoding != null) {
			request.addHeader(HttpConstants.CONTENT_ENCODING, wireContentEncoding);
		}
		/* Mock: risponde plain, no encoding (testiamo solo il request side). */
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_ENCODING, ContentEncodingMockServer.REPLY_ENCODING_NONE);
		if (this.libraryMode != null) {
			this.libraryMode.patchRequest(request);
		}

		HttpResponse response = HttpUtilities.httpInvoke(request);
		verify(response, expected, Side.REQUEST, tipo, api, resource, encoding, plain, requestBodyOnWire, null);
	}

	/* ====================================================================================== */
	/* ================================ ENGINE — RESPONSE side ============================== */
	/* ====================================================================================== */

	/**
	 * Test della decompressione lato RISPOSTA: il client manda body plain, il mock risponde
	 * comprimendo con {@code encoding}. Il test verifica che:
	 * <ul>
	 *   <li>il body ricevuto dal client sia decompresso se DECOMPRESSED;</li>
	 *   <li>il body ricevuto dal client sia raw (con CE preservato) se PASSTHROUGH;</li>
	 *   <li>la chiamata fallisca con diagnostico unsupported se UNSUPPORTED_ERROR;</li>
	 *   <li>per API_REG i dump della response siano registrati.</li>
	 * </ul>
	 *
	 * Per default il client NON invia {@code Accept-Encoding}: vedi
	 * {@link #_testResponse(TipoServizio, String, String, String, Outcome, String)} per la variante
	 * con header esplicito.
	 */
	private void _testResponse(TipoServizio tipo, String api, String resource, String encoding, Outcome expected) throws Exception {
		_testResponse(tipo, api, resource, encoding, expected, null);
	}

	/**
	 * Variante di {@link #_testResponse(TipoServizio, String, String, String, Outcome)} che invia
	 * un {@code Accept-Encoding} esplicito dal client. Serve a verificare il passthrough
	 * trasparente dell'header verso il backend (GovWay non deve sovrascriverlo).
	 *
	 * @param clientAcceptEncoding valore dell'header {@code Accept-Encoding} da inviare con la
	 *  richiesta; {@code null} per non inviare l'header.
	 */
	private void _testResponse(TipoServizio tipo, String api, String resource, String encoding, Outcome expected, String clientAcceptEncoding) throws Exception {
		byte[] plain = PLAIN_BODY.getBytes();

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		request.setContent(plain);
		request.setUrl(buildUrl(tipo, api, resource));
		/* Pilota il mock: rispondi con questo encoding. */
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_ENCODING, encoding);
		/* Optional: invia anche Accept-Encoding lato client (verifica del passthrough). */
		if (clientAcceptEncoding != null) {
			request.addHeader(HttpConstants.ACCEPT_ENCODING, clientAcceptEncoding);
		}
		if (this.libraryMode != null) {
			this.libraryMode.patchRequest(request);
		}

		HttpResponse response = HttpUtilities.httpInvoke(request);
		verify(response, expected, Side.RESPONSE, tipo, api, resource, encoding, plain, null, clientAcceptEncoding);
	}

	/* ====================================================================================== */
	/* ============================== Asserzioni e verifica DB ============================== */
	/* ====================================================================================== */

	/* Costanti dei tipi-messaggio della tabella dump_messaggi (configurazione binaria, vedi
	 * payload-parsing=disabilitato nelle PA Specific). */
	private static final String TIPO_RICHIESTA_INGRESSO = "RichiestaIngressoDumpBinario";
	private static final String TIPO_RICHIESTA_USCITA   = "RichiestaUscitaDumpBinario";
	private static final String TIPO_RISPOSTA_INGRESSO  = "RispostaIngressoDumpBinario";
	private static final String TIPO_RISPOSTA_USCITA    = "RispostaUscitaDumpBinario";

	private void verify(HttpResponse response, Outcome expected, Side side,
			TipoServizio tipo, String api, String resource, String encoding,
			byte[] plain, byte[] requestBodyOnWire, String clientAcceptEncoding) throws Exception {

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull("[" + side + "/" + resource + "/" + encoding + "] manca GovWay-Transaction-ID", idTransazione);

		EsitiProperties esiti = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME);
		long esitoOk = esiti.convertoToCode(EsitoTransazioneName.OK);

		String label = "[" + side + "/" + resource + "/" + encoding + "/" + tipo + "]";

		switch (expected) {
			case PASSTHROUGH:
				assertEquals(label + " status code atteso 200", 200, response.getResultHTTPOperation());
				DBVerifier.verify(idTransazione, esitoOk, this.libraryMode);
				DBVerifier.notExistsDiagnostico(idTransazione, DIAG_DECOMPRESSED);
				DBVerifier.notExistsDiagnostico(idTransazione, DIAG_UNSUPPORTED);
				if (side == Side.REQUEST) {
					verifyBackendReceivedBody(response, requestBodyOnWire, encoding, /*decompressed*/ false, label);
				} else {
					verifyClientReceivedResponseBody(response, plain, encoding, /*decompressed*/ false, label);
				}
				break;

			case DECOMPRESSED:
				assertEquals(label + " status code atteso 200", 200, response.getResultHTTPOperation());
				DBVerifier.verify(idTransazione, esitoOk, this.libraryMode);
				DBVerifier.existsDiagnostico(idTransazione, DIAG_DECOMPRESSED);
				DBVerifier.notExistsDiagnostico(idTransazione, DIAG_UNSUPPORTED);
				if (side == Side.REQUEST) {
					verifyBackendReceivedBody(response, plain, encoding, /*decompressed*/ true, label);
				} else {
					verifyClientReceivedResponseBody(response, plain, encoding, /*decompressed*/ true, label);
				}
				break;

			case UNSUPPORTED_ERROR:
				if (response.getResultHTTPOperation() == 200) {
					fail(label + " atteso errore ma il gateway ha risposto 200; idTransazione=" + idTransazione);
				}
				DBVerifier.existsDiagnostico(idTransazione, DIAG_UNSUPPORTED);
				DBVerifier.notExistsDiagnostico(idTransazione, DIAG_DECOMPRESSED);
				break;
		}

		/* Verifica del passthrough dell'Accept-Encoding: GovWay deve propagare l'header del client
		 * al backend, e NON deve aggiungerne uno suo se il client non l'ha mandato. Si applica solo
		 * agli scenari NON UNSUPPORTED_ERROR (per gli error la response del mock non e' visibile). */
		if (expected != Outcome.UNSUPPORTED_ERROR) {
			verifyAcceptEncodingPassthrough(response, clientAcceptEncoding, label);
		}

		/* Per API con dump abilitato: presenza delle 4 righe dump_messaggi + verifica del
		 * Content-Encoding nella tabella dump_header_trasporto. Salta gli UNSUPPORTED_ERROR
		 * perche' il flusso si interrompe prima del dump del lato che ha generato l'errore. */
		if (API_REG.equals(api) && expected != Outcome.UNSUPPORTED_ERROR) {
			DBVerifier.existsDumpMessaggio(idTransazione, TIPO_RICHIESTA_INGRESSO);
			DBVerifier.existsDumpMessaggio(idTransazione, TIPO_RICHIESTA_USCITA);
			DBVerifier.existsDumpMessaggio(idTransazione, TIPO_RISPOSTA_INGRESSO);
			DBVerifier.existsDumpMessaggio(idTransazione, TIPO_RISPOSTA_USCITA);
			verifyDumpContentEncodingHeaders(idTransazione, side, expected, encoding);
		}
	}

	/**
	 * Verifica la presenza/assenza dell'header {@code Content-Encoding} nei 4 dump della
	 * transazione. Si appoggia su {@link DBVerifier#existsDumpHeaderTrasporto} /
	 * {@link DBVerifier#notExistsDumpHeaderTrasporto}.
	 * <ul>
	 *   <li><b>Side REQUEST</b>:
	 *     <ul>
	 *       <li>RichiestaIngresso → CE originale presente (se inviato dal client)</li>
	 *       <li>RichiestaUscita → CE presente se PASSTHROUGH, ASSENTE se DECOMPRESSED
	 *         (GovWay rimuove CE/CL prima di inoltrare al backend)</li>
	 *       <li>RispostaIngresso/Uscita → no CE (mock risponde plain in questa modalita')</li>
	 *     </ul>
	 *   </li>
	 *   <li><b>Side RESPONSE</b>:
	 *     <ul>
	 *       <li>RispostaIngresso → CE originale dal mock presente</li>
	 *       <li>RispostaUscita → CE presente se PASSTHROUGH, ASSENTE se DECOMPRESSED
	 *         (GovWay rimuove CE/CL prima di inoltrare al client)</li>
	 *       <li>RichiestaIngresso/Uscita → no CE (client manda body plain in questa modalita')</li>
	 *     </ul>
	 *   </li>
	 * </ul>
	 */
	private void verifyDumpContentEncodingHeaders(String idTransazione, Side side, Outcome expected, String encoding) throws Exception {
		String wireRequestCE = wireContentEncodingHeader(encoding);
		String wireResponseCE = wireResponseContentEncodingHeader(encoding);

		if (side == Side.REQUEST) {
			if (wireRequestCE != null) {
				DBVerifier.existsDumpHeaderTrasporto(idTransazione, TIPO_RICHIESTA_INGRESSO, HttpConstants.CONTENT_ENCODING, wireRequestCE);
				if (expected == Outcome.DECOMPRESSED) {
					DBVerifier.notExistsDumpHeaderTrasporto(idTransazione, TIPO_RICHIESTA_USCITA, HttpConstants.CONTENT_ENCODING);
				} else {
					DBVerifier.existsDumpHeaderTrasporto(idTransazione, TIPO_RICHIESTA_USCITA, HttpConstants.CONTENT_ENCODING, wireRequestCE);
				}
			} else {
				/* encoding=identity: il client non manda CE, il dump non lo deve avere */
				DBVerifier.notExistsDumpHeaderTrasporto(idTransazione, TIPO_RICHIESTA_INGRESSO, HttpConstants.CONTENT_ENCODING);
				DBVerifier.notExistsDumpHeaderTrasporto(idTransazione, TIPO_RICHIESTA_USCITA, HttpConstants.CONTENT_ENCODING);
			}
			/* Risposta plain dal mock: nessun CE nei dump risposta */
			DBVerifier.notExistsDumpHeaderTrasporto(idTransazione, TIPO_RISPOSTA_INGRESSO, HttpConstants.CONTENT_ENCODING);
			DBVerifier.notExistsDumpHeaderTrasporto(idTransazione, TIPO_RISPOSTA_USCITA, HttpConstants.CONTENT_ENCODING);
		} else {
			if (wireResponseCE != null) {
				DBVerifier.existsDumpHeaderTrasporto(idTransazione, TIPO_RISPOSTA_INGRESSO, HttpConstants.CONTENT_ENCODING, wireResponseCE);
				if (expected == Outcome.DECOMPRESSED) {
					DBVerifier.notExistsDumpHeaderTrasporto(idTransazione, TIPO_RISPOSTA_USCITA, HttpConstants.CONTENT_ENCODING);
				} else {
					DBVerifier.existsDumpHeaderTrasporto(idTransazione, TIPO_RISPOSTA_USCITA, HttpConstants.CONTENT_ENCODING, wireResponseCE);
				}
			} else {
				DBVerifier.notExistsDumpHeaderTrasporto(idTransazione, TIPO_RISPOSTA_INGRESSO, HttpConstants.CONTENT_ENCODING);
				DBVerifier.notExistsDumpHeaderTrasporto(idTransazione, TIPO_RISPOSTA_USCITA, HttpConstants.CONTENT_ENCODING);
			}
			/* Richiesta plain dal client: nessun CE nei dump richiesta */
			DBVerifier.notExistsDumpHeaderTrasporto(idTransazione, TIPO_RICHIESTA_INGRESSO, HttpConstants.CONTENT_ENCODING);
			DBVerifier.notExistsDumpHeaderTrasporto(idTransazione, TIPO_RICHIESTA_USCITA, HttpConstants.CONTENT_ENCODING);
		}
	}

	/** Valore atteso sul wire per il Content-Encoding presente nella response del mock. */
	private String wireResponseContentEncodingHeader(String encoding) {
		if (ENC_DEFLATE_ZLIB.equals(encoding) || ENC_DEFLATE_RAW.equals(encoding)) {
			return "deflate";
		}
		/* identity: il mock mette CE=identity nella response (vedi ContentEncodingMockServer). */
		if (ENC_IDENTITY.equals(encoding)) {
			return ENC_IDENTITY;
		}
		return encoding; /* gzip, x-gzip, br */
	}

	/** Verifica i metadati restituiti dal mock per il body request. */
	private void verifyBackendReceivedBody(HttpResponse response, byte[] expectedBody, String encoding, boolean decompressed, String label) {
		String receivedMagic = response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_BODY_MAGIC);
		String receivedCE    = response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_CONTENT_ENCODING);
		assertNotNull(label + " manca header " + ContentEncodingMockServer.HEADER_RECEIVED_BODY_MAGIC, receivedMagic);

		if (decompressed) {
			/* Il backend ha ricevuto il body decompresso. I primi byte devono essere ASCII JSON. */
			assertFalse(label + " atteso body decompresso al backend, ma e' gzip (magic 1f 8b): " + receivedMagic,
					receivedMagic.startsWith("1f 8b"));
			/* Il connettore di GovWay (lato uscita verso backend) NON propaga gli header CE/CL del body
			 * originale (perche' il body in chiaro che invia ha lunghezza diversa). */
			assertTrue(label + " atteso Content-Encoding rimosso lato backend; ricevuto: " + receivedCE,
					receivedCE == null || receivedCE.isEmpty());
		} else {
			/* Il backend ha ricevuto il body raw (passthrough). Verifico il magic in base all'encoding. */
			if (ENC_GZIP.equals(encoding) || ENC_X_GZIP.equals(encoding)) {
				assertTrue(label + " atteso body gzip raw al backend, magic: " + receivedMagic,
						receivedMagic.startsWith("1f 8b"));
			}
			/* CE preservato (sara' lo stesso encoding inviato, modulo case insensitive). */
			if (!ENC_IDENTITY.equals(encoding)) {
				assertNotNull(label + " atteso Content-Encoding preservato lato backend", receivedCE);
			}
		}
	}

	/** Verifica il body della response ricevuto dal client (decomprimendo se serve). */
	private void verifyClientReceivedResponseBody(HttpResponse response, byte[] expectedPlain, String encoding, boolean decompressed, String label) throws IOException {
		byte[] body = response.getContent();
		String responseCE = response.getHeaderFirstValue(HttpConstants.CONTENT_ENCODING);

		if (decompressed) {
			/* GovWay ha decompresso: il client riceve body in chiaro, senza Content-Encoding. */
			assertTrue(label + " atteso Content-Encoding rimosso nella response al client; ricevuto: " + responseCE,
					responseCE == null || responseCE.isEmpty() || ENC_IDENTITY.equalsIgnoreCase(responseCE));
			assertEquals(label + " body atteso decompresso", new String(expectedPlain), new String(body));
		} else {
			/* Passthrough: client riceve body raw + Content-Encoding preservato (se diverso da identity/none). */
			if (ENC_IDENTITY.equals(encoding)) {
				assertEquals(label + " body atteso identico (identity)", new String(expectedPlain), new String(body));
			} else if (ENC_BROTLI.equals(encoding)) {
				/* Il mock dichiara CE=br senza comprimere davvero: passthrough lo lascia visibile al client. */
				assertEquals(label + " atteso Content-Encoding=br preservato", ENC_BROTLI.toLowerCase(), responseCE != null ? responseCE.toLowerCase() : null);
			} else {
				/* Il client decomprime localmente per asserire il payload originale. */
				byte[] decoded = clientDecode(body, encoding);
				assertEquals(label + " body atteso = plain post-decompress lato client", new String(expectedPlain), new String(decoded));
				assertNotNull(label + " atteso Content-Encoding preservato nella response al client", responseCE);
			}
		}
	}

	/**
	 * Verifica il passthrough dell'header Accept-Encoding verso il backend:
	 * <ul>
	 *   <li>se il client lo ha inviato ({@code clientAcceptEncoding != null}), il mock deve
	 *     averlo ricevuto (GovWay propaga trasparente);</li>
	 *   <li>se il client NON l'ha inviato, il mock NON deve averlo ricevuto (GovWay non aggiunge
	 *     header propri verso il backend nei connettori).</li>
	 * </ul>
	 * NB: comportamento diverso dal path utility {@code HttpUtilities.httpInvoke} con flag
	 * {@code decompressResponseContentEncoding=true}, dove invece Apache HC5 inietta automaticamente
	 * Accept-Encoding. Sui connettori GovWay {@code disableContentCompression()} resta ON e la
	 * decompressione e' fatta esplicitamente in {@code decodeResponseBodyContentEncoding()}.
	 */
	private void verifyAcceptEncodingPassthrough(HttpResponse response, String clientAcceptEncoding, String label) {
		String received = response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_ACCEPT_ENCODING);
		if (clientAcceptEncoding != null) {
			assertNotNull(label + " atteso Accept-Encoding propagato al backend, header assente", received);
			assertFalse(label + " atteso Accept-Encoding non vuoto al backend; client ha inviato: '" + clientAcceptEncoding + "'", received.isEmpty());
			assertEquals(label + " atteso Accept-Encoding propagato identico", clientAcceptEncoding, received);
		} else {
			assertTrue(label + " atteso Accept-Encoding non aggiunto da GovWay al backend; ricevuto: '" + received + "'",
					received == null || received.isEmpty());
		}
	}

	/* ====================================================================================== */
	/* ====================== Helper URL + encoding/decoding ============================ */
	/* ====================================================================================== */

	private String buildUrl(TipoServizio tipo, String api, String resource) {
		String base = System.getProperty("govway_base_path");
		if (TipoServizio.EROGAZIONE.equals(tipo)) {
			return base + "/in/SoggettoInternoTest/" + api + "/v1/" + resource;
		}
		return base + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/" + api + "/v1/" + resource;
	}

	/**
	 * Codifica i byte plain secondo il marker {@code encoding}:
	 * <ul>
	 *   <li>{@code gzip}/{@code x-gzip} → GZIPOutputStream</li>
	 *   <li>{@code deflate-zlib} → DeflaterOutputStream default (RFC 1950)</li>
	 *   <li>{@code deflate-raw}  → Deflater nowrap=true (RFC 1951)</li>
	 *   <li>{@code identity} → nessuna codifica</li>
	 *   <li>{@code br} → nessuna codifica (il test vuole solo dichiarare l'encoding nell'header,
	 *     il body resta plain perche' GovWay rifiutera' senza tentare la decompressione)</li>
	 * </ul>
	 */
	private byte[] applyEncoding(byte[] plain, String encoding) throws IOException {
		if (ENC_GZIP.equals(encoding) || ENC_X_GZIP.equals(encoding)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (GZIPOutputStream gz = new GZIPOutputStream(baos)) {
				gz.write(plain);
			}
			return baos.toByteArray();
		}
		if (ENC_DEFLATE_ZLIB.equals(encoding)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (DeflaterOutputStream d = new DeflaterOutputStream(baos)) {
				d.write(plain);
			}
			return baos.toByteArray();
		}
		if (ENC_DEFLATE_RAW.equals(encoding)) {
			Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (DeflaterOutputStream d = new DeflaterOutputStream(baos, deflater)) {
				d.write(plain);
			} finally {
				deflater.end();
			}
			return baos.toByteArray();
		}
		return plain; // identity / br
	}

	/** Mappa il marker test ai valori "ufficiali" del wire Content-Encoding (deflate-zlib/raw → deflate). */
	private String wireContentEncodingHeader(String encoding) {
		if (ENC_DEFLATE_ZLIB.equals(encoding) || ENC_DEFLATE_RAW.equals(encoding)) {
			return "deflate";
		}
		if (ENC_IDENTITY.equals(encoding)) {
			return null; // no header
		}
		return encoding; // gzip, x-gzip, br
	}

	/** Decodifica lato client per verificare il body in passthrough. */
	private byte[] clientDecode(byte[] encoded, String encoding) throws IOException {
		if (ENC_GZIP.equals(encoding) || ENC_X_GZIP.equals(encoding)) {
			try (GZIPInputStream gz = new GZIPInputStream(new ByteArrayInputStream(encoded))) {
				return gz.readAllBytes();
			}
		}
		if (ENC_DEFLATE_ZLIB.equals(encoding)) {
			try (InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(encoded))) {
				return in.readAllBytes();
			}
		}
		if (ENC_DEFLATE_RAW.equals(encoding)) {
			try (InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(encoded), new Inflater(true))) {
				return in.readAllBytes();
			}
		}
		return encoded;
	}
}
