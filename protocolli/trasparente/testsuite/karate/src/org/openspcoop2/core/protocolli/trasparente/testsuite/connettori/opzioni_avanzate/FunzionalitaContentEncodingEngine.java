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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.declaredEncoding;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.encodeForRequest;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.requestDeclaredEncoding;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.verifyBackendRicevutoIdentico;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.verifyBackendRicevutoInChiaro;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.verifyBodyIdentico;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.verifyDiagnosticoContenutoCompresso;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.verifyNoContentEncoding;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.Enc;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * Funzionalità di GovWay che accedono al contenuto REST, con richieste e risposte compresse
 * (analisi sez. 13 e 14).
 *
 * <h2>API</h2>
 * <ul>
 *   <li>{@code TestContentEncodingFunzionalita}: decompressione disabilitata;</li>
 *   <li>{@code TestContentEncodingFunzionalitaDecompress}: stessa configurazione con
 *       {@code connettori.contentEncoding.decompress=true} su tutte le porte.</li>
 * </ul>
 * Ogni risorsa appartiene a un gruppo con una sola funzionalità attiva: validazione dei contenuti
 * (normale e warning-only; JSON, XML, multipart, binario, application/gzip senza Content-Encoding),
 * correlazione applicativa, trasformazioni che leggono il payload, registrazione dei messaggi con
 * payload-parsing, response caching, dimensione massima dei messaggi (policy da 8 KB).
 *
 * <h2>Esiti attesi</h2>
 * <ul>
 *   <li>Contenuto in chiaro: la funzionalità opera come oggi.</li>
 *   <li>Contenuto compresso senza decompressione:
 *     <ul>
 *       <li>funzionalità che interpretano il contenuto: errore esplicito ("contenuto compresso,
 *           abilitare la decompressione"); in warning-only la transazione prosegue con lo stesso
 *           diagnostico e byte/Content-Encoding invariati;</li>
 *       <li>accesso binario (validazione di un contenuto binario, response cache, dimensione
 *           massima sul wire): nessun errore, byte e Content-Encoding invariati;</li>
 *       <li>registrazione messaggi con payload-parsing: byte registrati così come ricevuti con un
 *           diagnostico, transazione invariata.</li>
 *     </ul>
 *   </li>
 *   <li>Contenuto compresso con decompressione: la funzionalità opera sul contenuto in chiaro, il
 *       destinatario riceve il contenuto in chiaro senza Content-Encoding; encoding non supportato:
 *       errore esistente; la dimensione massima conta i byte decompressi (protezione zip bomb).</li>
 * </ul>
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FunzionalitaContentEncodingEngine extends ConfigLoader {

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

	private HttpLibraryMode libraryMode = null;
	protected void setHttpLibraryMode(HttpLibraryMode mode) {
		this.libraryMode = mode;
	}

	private static final String DIAG_UNSUPPORTED = "non gestibile dalla decompressione automatica";
	private static final String DIAG_DECOMPRESSED = "applicata decompressione automatica";
	/* Nuovo diagnostico della registrazione messaggi (analisi sez. 14 S6): da tenere allineato. */
	private static final String DIAG_REGISTRAZIONE_SENZA_INTERPRETAZIONE = "registrato così come ricevuto";

	private static final String DIAG_RESPONSE_PAYLOAD_TOO_LARGE = "Response Payload too large";

	/** Soglia (KB) della policy di dimensione massima configurata sul gruppo 'dimensione'. */
	private static final int SOGLIA_DIMENSIONE_KB = 8;

	enum Api {
		/** Decompressione disabilitata. */
		PLAIN("TestContentEncodingFunzionalita"),
		/** Decompressione abilitata su richiesta e risposta. */
		DECOMPRESS("TestContentEncodingFunzionalitaDecompress");
		final String nome;
		Api(String nome) { this.nome = nome; }
	}

	enum Direzione { RICHIESTA, RISPOSTA }

	/** Modalità di accesso al contenuto della funzionalità (analisi sez. 14). */
	enum Accesso { INTERPRETAZIONE, INTERPRETAZIONE_WARNING, BINARIO, REGISTRAZIONE }

	enum Funzionalita {
		VALIDAZIONE_JSON("validazioneJson", "application/json", Accesso.INTERPRETAZIONE),
		VALIDAZIONE_WARNING_JSON("validazioneWarningJson", "application/json", Accesso.INTERPRETAZIONE_WARNING),
		VALIDAZIONE_XML("validazioneXml", "application/xml", Accesso.INTERPRETAZIONE),
		VALIDAZIONE_MULTIPART("validazioneMultipart", "multipart/form-data; boundary=" + ContentTypeBoundary.BOUNDARY, Accesso.INTERPRETAZIONE),
		VALIDAZIONE_BINARY("validazioneBinary", "application/octet-stream", Accesso.BINARIO),
		CORRELAZIONE_JSON("correlazioneJson", "application/json", Accesso.INTERPRETAZIONE),
		TRASFORMAZIONE_RICHIESTA_JSON("trasformazioneRichiestaJson", "application/json", Accesso.INTERPRETAZIONE),
		TRASFORMAZIONE_RISPOSTA_JSON("trasformazioneRispostaJson", "application/json", Accesso.INTERPRETAZIONE),
		REGISTRAZIONE_JSON("registrazioneJson", "application/json", Accesso.REGISTRAZIONE),
		REGISTRAZIONE_MULTIPART("registrazioneMultipart", "multipart/mixed; boundary=" + ContentTypeBoundary.BOUNDARY, Accesso.REGISTRAZIONE),
		CACHING_JSON("cachingJson", "application/json", Accesso.BINARIO);

		final String risorsa;
		final String contentType;
		final Accesso accesso;
		Funzionalita(String risorsa, String contentType, Accesso accesso) {
			this.risorsa = risorsa;
			this.contentType = contentType;
			this.accesso = accesso;
		}
	}

	/** Boundary dei multipart usati dai test. */
	static final class ContentTypeBoundary {
		static final String BOUNDARY = "govwayTestBoundaryFunzionalita";
		private ContentTypeBoundary() {}
	}

	/* ====================================================================================== */
	/* ======================================= @Test ========================================= */
	/* ====================================================================================== */

	/* ---- erogazione PLAIN VALIDAZIONE_JSON ---- */
	@Test public void erogazionePlainValidazioneJsonRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazionePlainValidazioneJsonRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazionePlainValidazioneJsonRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazionePlainValidazioneJsonRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazionePlainValidazioneJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainValidazioneJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainValidazioneJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainValidazioneJsonRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazionePlainValidazioneJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainValidazioneJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazionePlainValidazioneJsonRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazionePlainValidazioneJsonRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazionePlainValidazioneJsonRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazionePlainValidazioneJsonRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazionePlainValidazioneJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainValidazioneJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainValidazioneJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainValidazioneJsonRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazionePlainValidazioneJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainValidazioneJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione PLAIN VALIDAZIONE_WARNING_JSON ---- */
	@Test public void erogazionePlainValidazioneWarningJsonRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazionePlainValidazioneWarningJsonRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazionePlainValidazioneWarningJsonRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazionePlainValidazioneWarningJsonRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazionePlainValidazioneWarningJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainValidazioneWarningJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainValidazioneWarningJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainValidazioneWarningJsonRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazionePlainValidazioneWarningJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainValidazioneWarningJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazionePlainValidazioneWarningJsonRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazionePlainValidazioneWarningJsonRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazionePlainValidazioneWarningJsonRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazionePlainValidazioneWarningJsonRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazionePlainValidazioneWarningJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainValidazioneWarningJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainValidazioneWarningJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainValidazioneWarningJsonRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazionePlainValidazioneWarningJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainValidazioneWarningJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione PLAIN VALIDAZIONE_XML ---- */
	@Test public void erogazionePlainValidazioneXmlRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazionePlainValidazioneXmlRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazionePlainValidazioneXmlRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazionePlainValidazioneXmlRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazionePlainValidazioneXmlRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainValidazioneXmlRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainValidazioneXmlRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainValidazioneXmlRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazionePlainValidazioneXmlRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainValidazioneXmlRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazionePlainValidazioneXmlRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazionePlainValidazioneXmlRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazionePlainValidazioneXmlRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazionePlainValidazioneXmlRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazionePlainValidazioneXmlRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainValidazioneXmlRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainValidazioneXmlRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainValidazioneXmlRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazionePlainValidazioneXmlRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainValidazioneXmlRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione PLAIN VALIDAZIONE_MULTIPART ---- */
	@Test public void erogazionePlainValidazioneMultipartRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazionePlainValidazioneMultipartRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazionePlainValidazioneMultipartRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazionePlainValidazioneMultipartRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazionePlainValidazioneMultipartRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainValidazioneMultipartRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainValidazioneMultipartRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainValidazioneMultipartRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazionePlainValidazioneMultipartRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainValidazioneMultipartRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazionePlainValidazioneMultipartRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazionePlainValidazioneMultipartRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazionePlainValidazioneMultipartRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazionePlainValidazioneMultipartRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazionePlainValidazioneMultipartRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainValidazioneMultipartRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainValidazioneMultipartRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainValidazioneMultipartRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazionePlainValidazioneMultipartRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainValidazioneMultipartRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione PLAIN VALIDAZIONE_BINARY ---- */
	@Test public void erogazionePlainValidazioneBinaryRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazionePlainValidazioneBinaryRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazionePlainValidazioneBinaryRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazionePlainValidazioneBinaryRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazionePlainValidazioneBinaryRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainValidazioneBinaryRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainValidazioneBinaryRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainValidazioneBinaryRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazionePlainValidazioneBinaryRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainValidazioneBinaryRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazionePlainValidazioneBinaryRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazionePlainValidazioneBinaryRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazionePlainValidazioneBinaryRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazionePlainValidazioneBinaryRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazionePlainValidazioneBinaryRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainValidazioneBinaryRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainValidazioneBinaryRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainValidazioneBinaryRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazionePlainValidazioneBinaryRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainValidazioneBinaryRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione PLAIN CORRELAZIONE_JSON ---- */
	@Test public void erogazionePlainCorrelazioneJsonRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazionePlainCorrelazioneJsonRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazionePlainCorrelazioneJsonRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazionePlainCorrelazioneJsonRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazionePlainCorrelazioneJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainCorrelazioneJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainCorrelazioneJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainCorrelazioneJsonRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazionePlainCorrelazioneJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainCorrelazioneJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazionePlainCorrelazioneJsonRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazionePlainCorrelazioneJsonRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazionePlainCorrelazioneJsonRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazionePlainCorrelazioneJsonRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazionePlainCorrelazioneJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainCorrelazioneJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainCorrelazioneJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainCorrelazioneJsonRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazionePlainCorrelazioneJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainCorrelazioneJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione PLAIN TRASFORMAZIONE_RICHIESTA_JSON ---- */
	@Test public void erogazionePlainTrasformazioneRichiestaJsonRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazionePlainTrasformazioneRichiestaJsonRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazionePlainTrasformazioneRichiestaJsonRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazionePlainTrasformazioneRichiestaJsonRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazionePlainTrasformazioneRichiestaJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainTrasformazioneRichiestaJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainTrasformazioneRichiestaJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainTrasformazioneRichiestaJsonRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazionePlainTrasformazioneRichiestaJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainTrasformazioneRichiestaJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	/* ---- erogazione PLAIN TRASFORMAZIONE_RISPOSTA_JSON ---- */
	@Test public void erogazionePlainTrasformazioneRispostaJsonRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazionePlainTrasformazioneRispostaJsonRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazionePlainTrasformazioneRispostaJsonRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazionePlainTrasformazioneRispostaJsonRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazionePlainTrasformazioneRispostaJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainTrasformazioneRispostaJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainTrasformazioneRispostaJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainTrasformazioneRispostaJsonRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazionePlainTrasformazioneRispostaJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainTrasformazioneRispostaJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione PLAIN REGISTRAZIONE_JSON ---- */
	@Test public void erogazionePlainRegistrazioneJsonRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazionePlainRegistrazioneJsonRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazionePlainRegistrazioneJsonRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazionePlainRegistrazioneJsonRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazionePlainRegistrazioneJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainRegistrazioneJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainRegistrazioneJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainRegistrazioneJsonRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazionePlainRegistrazioneJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainRegistrazioneJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazionePlainRegistrazioneJsonRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazionePlainRegistrazioneJsonRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazionePlainRegistrazioneJsonRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazionePlainRegistrazioneJsonRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazionePlainRegistrazioneJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainRegistrazioneJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainRegistrazioneJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainRegistrazioneJsonRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazionePlainRegistrazioneJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainRegistrazioneJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione PLAIN REGISTRAZIONE_MULTIPART ---- */
	@Test public void erogazionePlainRegistrazioneMultipartRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazionePlainRegistrazioneMultipartRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazionePlainRegistrazioneMultipartRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazionePlainRegistrazioneMultipartRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazionePlainRegistrazioneMultipartRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainRegistrazioneMultipartRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainRegistrazioneMultipartRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainRegistrazioneMultipartRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazionePlainRegistrazioneMultipartRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainRegistrazioneMultipartRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazionePlainRegistrazioneMultipartRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazionePlainRegistrazioneMultipartRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazionePlainRegistrazioneMultipartRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazionePlainRegistrazioneMultipartRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazionePlainRegistrazioneMultipartRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainRegistrazioneMultipartRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainRegistrazioneMultipartRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainRegistrazioneMultipartRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazionePlainRegistrazioneMultipartRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainRegistrazioneMultipartRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione PLAIN CACHING_JSON ---- */
	@Test public void erogazionePlainCachingJsonRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazionePlainCachingJsonRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazionePlainCachingJsonRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazionePlainCachingJsonRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazionePlainCachingJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainCachingJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainCachingJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainCachingJsonRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazionePlainCachingJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainCachingJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazionePlainCachingJsonRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazionePlainCachingJsonRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazionePlainCachingJsonRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazionePlainCachingJsonRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazionePlainCachingJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazionePlainCachingJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazionePlainCachingJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazionePlainCachingJsonRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazionePlainCachingJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazionePlainCachingJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione PLAIN validazione con contenuto non valido, archivio gzip, dimensione massima ---- */
	@Test public void erogazionePlainValidazioneJsonNonValidoRichiestaNone() throws Exception { _testValidazioneContenutoNonValido(TipoServizio.EROGAZIONE, Api.PLAIN, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazionePlainValidazioneGzipFileRichiesta() throws Exception { _testValidazioneGzipFile(TipoServizio.EROGAZIONE, Api.PLAIN, Direzione.RICHIESTA); }
	@Test public void erogazionePlainDimensioneRichiestaNone() throws Exception { _testDimensione(TipoServizio.EROGAZIONE, Api.PLAIN, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazionePlainDimensioneRichiestaGzip() throws Exception { _testDimensione(TipoServizio.EROGAZIONE, Api.PLAIN, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazionePlainValidazioneJsonNonValidoRispostaNone() throws Exception { _testValidazioneContenutoNonValido(TipoServizio.EROGAZIONE, Api.PLAIN, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazionePlainValidazioneGzipFileRisposta() throws Exception { _testValidazioneGzipFile(TipoServizio.EROGAZIONE, Api.PLAIN, Direzione.RISPOSTA); }
	@Test public void erogazionePlainDimensioneRispostaNone() throws Exception { _testDimensione(TipoServizio.EROGAZIONE, Api.PLAIN, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazionePlainDimensioneRispostaGzip() throws Exception { _testDimensione(TipoServizio.EROGAZIONE, Api.PLAIN, Direzione.RISPOSTA, Enc.GZIP); }

	/* ---- erogazione DECOMPRESS VALIDAZIONE_JSON ---- */
	@Test public void erogazioneDecompressValidazioneJsonRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazioneDecompressValidazioneJsonRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressValidazioneJsonRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazioneDecompressValidazioneJsonRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressValidazioneJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressValidazioneJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressValidazioneJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressValidazioneJsonRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazioneDecompressValidazioneJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressValidazioneJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazioneDecompressValidazioneJsonRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazioneDecompressValidazioneJsonRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressValidazioneJsonRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazioneDecompressValidazioneJsonRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressValidazioneJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressValidazioneJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressValidazioneJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressValidazioneJsonRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazioneDecompressValidazioneJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressValidazioneJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione DECOMPRESS VALIDAZIONE_WARNING_JSON ---- */
	@Test public void erogazioneDecompressValidazioneWarningJsonRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressValidazioneWarningJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione DECOMPRESS VALIDAZIONE_XML ---- */
	@Test public void erogazioneDecompressValidazioneXmlRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazioneDecompressValidazioneXmlRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressValidazioneXmlRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazioneDecompressValidazioneXmlRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressValidazioneXmlRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressValidazioneXmlRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressValidazioneXmlRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressValidazioneXmlRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazioneDecompressValidazioneXmlRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressValidazioneXmlRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazioneDecompressValidazioneXmlRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazioneDecompressValidazioneXmlRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressValidazioneXmlRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazioneDecompressValidazioneXmlRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressValidazioneXmlRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressValidazioneXmlRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressValidazioneXmlRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressValidazioneXmlRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazioneDecompressValidazioneXmlRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressValidazioneXmlRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione DECOMPRESS VALIDAZIONE_MULTIPART ---- */
	@Test public void erogazioneDecompressValidazioneMultipartRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazioneDecompressValidazioneMultipartRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressValidazioneMultipartRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazioneDecompressValidazioneMultipartRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressValidazioneMultipartRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressValidazioneMultipartRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressValidazioneMultipartRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressValidazioneMultipartRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazioneDecompressValidazioneMultipartRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressValidazioneMultipartRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazioneDecompressValidazioneMultipartRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazioneDecompressValidazioneMultipartRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressValidazioneMultipartRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazioneDecompressValidazioneMultipartRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressValidazioneMultipartRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressValidazioneMultipartRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressValidazioneMultipartRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressValidazioneMultipartRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazioneDecompressValidazioneMultipartRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressValidazioneMultipartRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione DECOMPRESS VALIDAZIONE_BINARY ---- */
	@Test public void erogazioneDecompressValidazioneBinaryRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazioneDecompressValidazioneBinaryRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressValidazioneBinaryRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazioneDecompressValidazioneBinaryRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressValidazioneBinaryRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressValidazioneBinaryRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressValidazioneBinaryRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressValidazioneBinaryRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazioneDecompressValidazioneBinaryRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressValidazioneBinaryRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazioneDecompressValidazioneBinaryRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazioneDecompressValidazioneBinaryRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressValidazioneBinaryRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazioneDecompressValidazioneBinaryRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressValidazioneBinaryRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressValidazioneBinaryRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressValidazioneBinaryRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressValidazioneBinaryRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazioneDecompressValidazioneBinaryRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressValidazioneBinaryRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione DECOMPRESS CORRELAZIONE_JSON ---- */
	@Test public void erogazioneDecompressCorrelazioneJsonRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazioneDecompressCorrelazioneJsonRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressCorrelazioneJsonRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazioneDecompressCorrelazioneJsonRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressCorrelazioneJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressCorrelazioneJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressCorrelazioneJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressCorrelazioneJsonRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazioneDecompressCorrelazioneJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressCorrelazioneJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazioneDecompressCorrelazioneJsonRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazioneDecompressCorrelazioneJsonRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressCorrelazioneJsonRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazioneDecompressCorrelazioneJsonRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressCorrelazioneJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressCorrelazioneJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressCorrelazioneJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressCorrelazioneJsonRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazioneDecompressCorrelazioneJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressCorrelazioneJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione DECOMPRESS TRASFORMAZIONE_RICHIESTA_JSON ---- */
	@Test public void erogazioneDecompressTrasformazioneRichiestaJsonRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazioneDecompressTrasformazioneRichiestaJsonRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressTrasformazioneRichiestaJsonRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazioneDecompressTrasformazioneRichiestaJsonRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressTrasformazioneRichiestaJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressTrasformazioneRichiestaJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressTrasformazioneRichiestaJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressTrasformazioneRichiestaJsonRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazioneDecompressTrasformazioneRichiestaJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressTrasformazioneRichiestaJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	/* ---- erogazione DECOMPRESS TRASFORMAZIONE_RISPOSTA_JSON ---- */
	@Test public void erogazioneDecompressTrasformazioneRispostaJsonRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazioneDecompressTrasformazioneRispostaJsonRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressTrasformazioneRispostaJsonRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazioneDecompressTrasformazioneRispostaJsonRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressTrasformazioneRispostaJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressTrasformazioneRispostaJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressTrasformazioneRispostaJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressTrasformazioneRispostaJsonRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazioneDecompressTrasformazioneRispostaJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressTrasformazioneRispostaJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione DECOMPRESS REGISTRAZIONE_JSON ---- */
	@Test public void erogazioneDecompressRegistrazioneJsonRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazioneDecompressRegistrazioneJsonRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressRegistrazioneJsonRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazioneDecompressRegistrazioneJsonRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressRegistrazioneJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressRegistrazioneJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressRegistrazioneJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressRegistrazioneJsonRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazioneDecompressRegistrazioneJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressRegistrazioneJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazioneDecompressRegistrazioneJsonRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazioneDecompressRegistrazioneJsonRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressRegistrazioneJsonRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazioneDecompressRegistrazioneJsonRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressRegistrazioneJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressRegistrazioneJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressRegistrazioneJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressRegistrazioneJsonRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazioneDecompressRegistrazioneJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressRegistrazioneJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione DECOMPRESS REGISTRAZIONE_MULTIPART ---- */
	@Test public void erogazioneDecompressRegistrazioneMultipartRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressRegistrazioneMultipartRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione DECOMPRESS CACHING_JSON ---- */
	@Test public void erogazioneDecompressCachingJsonRichiestaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazioneDecompressCachingJsonRichiestaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressCachingJsonRichiestaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazioneDecompressCachingJsonRichiestaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressCachingJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressCachingJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressCachingJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressCachingJsonRichiestaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void erogazioneDecompressCachingJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressCachingJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void erogazioneDecompressCachingJsonRispostaNone() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazioneDecompressCachingJsonRispostaIdentity() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void erogazioneDecompressCachingJsonRispostaGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazioneDecompressCachingJsonRispostaXGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void erogazioneDecompressCachingJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneDecompressCachingJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneDecompressCachingJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneDecompressCachingJsonRispostaBr() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void erogazioneDecompressCachingJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneDecompressCachingJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- erogazione DECOMPRESS validazione con contenuto non valido, archivio gzip, dimensione massima ---- */
	@Test public void erogazioneDecompressValidazioneJsonNonValidoRichiestaGzip() throws Exception { _testValidazioneContenutoNonValido(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazioneDecompressValidazioneGzipFileRichiesta() throws Exception { _testValidazioneGzipFile(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Direzione.RICHIESTA); }
	@Test public void erogazioneDecompressDimensioneRichiestaNone() throws Exception { _testDimensione(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void erogazioneDecompressDimensioneRichiestaGzip() throws Exception { _testDimensione(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void erogazioneDecompressValidazioneJsonNonValidoRispostaGzip() throws Exception { _testValidazioneContenutoNonValido(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void erogazioneDecompressValidazioneGzipFileRisposta() throws Exception { _testValidazioneGzipFile(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Direzione.RISPOSTA); }
	@Test public void erogazioneDecompressDimensioneRispostaNone() throws Exception { _testDimensione(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void erogazioneDecompressDimensioneRispostaGzip() throws Exception { _testDimensione(TipoServizio.EROGAZIONE, Api.DECOMPRESS, Direzione.RISPOSTA, Enc.GZIP); }

	/* ---- fruizione PLAIN VALIDAZIONE_JSON ---- */
	@Test public void fruizionePlainValidazioneJsonRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizionePlainValidazioneJsonRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizionePlainValidazioneJsonRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizionePlainValidazioneJsonRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizionePlainValidazioneJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainValidazioneJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainValidazioneJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainValidazioneJsonRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizionePlainValidazioneJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainValidazioneJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizionePlainValidazioneJsonRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizionePlainValidazioneJsonRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizionePlainValidazioneJsonRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizionePlainValidazioneJsonRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizionePlainValidazioneJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainValidazioneJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainValidazioneJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainValidazioneJsonRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizionePlainValidazioneJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainValidazioneJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione PLAIN VALIDAZIONE_WARNING_JSON ---- */
	@Test public void fruizionePlainValidazioneWarningJsonRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizionePlainValidazioneWarningJsonRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizionePlainValidazioneWarningJsonRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizionePlainValidazioneWarningJsonRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizionePlainValidazioneWarningJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainValidazioneWarningJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainValidazioneWarningJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainValidazioneWarningJsonRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizionePlainValidazioneWarningJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainValidazioneWarningJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizionePlainValidazioneWarningJsonRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizionePlainValidazioneWarningJsonRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizionePlainValidazioneWarningJsonRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizionePlainValidazioneWarningJsonRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizionePlainValidazioneWarningJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainValidazioneWarningJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainValidazioneWarningJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainValidazioneWarningJsonRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizionePlainValidazioneWarningJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainValidazioneWarningJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione PLAIN VALIDAZIONE_XML ---- */
	@Test public void fruizionePlainValidazioneXmlRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizionePlainValidazioneXmlRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizionePlainValidazioneXmlRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizionePlainValidazioneXmlRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizionePlainValidazioneXmlRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainValidazioneXmlRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainValidazioneXmlRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainValidazioneXmlRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizionePlainValidazioneXmlRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainValidazioneXmlRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizionePlainValidazioneXmlRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizionePlainValidazioneXmlRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizionePlainValidazioneXmlRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizionePlainValidazioneXmlRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizionePlainValidazioneXmlRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainValidazioneXmlRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainValidazioneXmlRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainValidazioneXmlRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizionePlainValidazioneXmlRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainValidazioneXmlRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione PLAIN VALIDAZIONE_MULTIPART ---- */
	@Test public void fruizionePlainValidazioneMultipartRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizionePlainValidazioneMultipartRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizionePlainValidazioneMultipartRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizionePlainValidazioneMultipartRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizionePlainValidazioneMultipartRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainValidazioneMultipartRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainValidazioneMultipartRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainValidazioneMultipartRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizionePlainValidazioneMultipartRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainValidazioneMultipartRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizionePlainValidazioneMultipartRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizionePlainValidazioneMultipartRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizionePlainValidazioneMultipartRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizionePlainValidazioneMultipartRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizionePlainValidazioneMultipartRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainValidazioneMultipartRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainValidazioneMultipartRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainValidazioneMultipartRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizionePlainValidazioneMultipartRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainValidazioneMultipartRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione PLAIN VALIDAZIONE_BINARY ---- */
	@Test public void fruizionePlainValidazioneBinaryRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizionePlainValidazioneBinaryRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizionePlainValidazioneBinaryRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizionePlainValidazioneBinaryRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizionePlainValidazioneBinaryRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainValidazioneBinaryRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainValidazioneBinaryRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainValidazioneBinaryRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizionePlainValidazioneBinaryRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainValidazioneBinaryRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizionePlainValidazioneBinaryRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizionePlainValidazioneBinaryRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizionePlainValidazioneBinaryRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizionePlainValidazioneBinaryRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizionePlainValidazioneBinaryRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainValidazioneBinaryRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainValidazioneBinaryRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainValidazioneBinaryRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizionePlainValidazioneBinaryRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainValidazioneBinaryRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione PLAIN CORRELAZIONE_JSON ---- */
	@Test public void fruizionePlainCorrelazioneJsonRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizionePlainCorrelazioneJsonRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizionePlainCorrelazioneJsonRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizionePlainCorrelazioneJsonRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizionePlainCorrelazioneJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainCorrelazioneJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainCorrelazioneJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainCorrelazioneJsonRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizionePlainCorrelazioneJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainCorrelazioneJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizionePlainCorrelazioneJsonRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizionePlainCorrelazioneJsonRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizionePlainCorrelazioneJsonRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizionePlainCorrelazioneJsonRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizionePlainCorrelazioneJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainCorrelazioneJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainCorrelazioneJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainCorrelazioneJsonRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizionePlainCorrelazioneJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainCorrelazioneJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione PLAIN TRASFORMAZIONE_RICHIESTA_JSON ---- */
	@Test public void fruizionePlainTrasformazioneRichiestaJsonRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizionePlainTrasformazioneRichiestaJsonRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizionePlainTrasformazioneRichiestaJsonRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizionePlainTrasformazioneRichiestaJsonRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizionePlainTrasformazioneRichiestaJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainTrasformazioneRichiestaJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainTrasformazioneRichiestaJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainTrasformazioneRichiestaJsonRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizionePlainTrasformazioneRichiestaJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainTrasformazioneRichiestaJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	/* ---- fruizione PLAIN TRASFORMAZIONE_RISPOSTA_JSON ---- */
	@Test public void fruizionePlainTrasformazioneRispostaJsonRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizionePlainTrasformazioneRispostaJsonRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizionePlainTrasformazioneRispostaJsonRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizionePlainTrasformazioneRispostaJsonRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizionePlainTrasformazioneRispostaJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainTrasformazioneRispostaJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainTrasformazioneRispostaJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainTrasformazioneRispostaJsonRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizionePlainTrasformazioneRispostaJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainTrasformazioneRispostaJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione PLAIN REGISTRAZIONE_JSON ---- */
	@Test public void fruizionePlainRegistrazioneJsonRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizionePlainRegistrazioneJsonRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizionePlainRegistrazioneJsonRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizionePlainRegistrazioneJsonRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizionePlainRegistrazioneJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainRegistrazioneJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainRegistrazioneJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainRegistrazioneJsonRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizionePlainRegistrazioneJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainRegistrazioneJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizionePlainRegistrazioneJsonRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizionePlainRegistrazioneJsonRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizionePlainRegistrazioneJsonRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizionePlainRegistrazioneJsonRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizionePlainRegistrazioneJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainRegistrazioneJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainRegistrazioneJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainRegistrazioneJsonRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizionePlainRegistrazioneJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainRegistrazioneJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione PLAIN REGISTRAZIONE_MULTIPART ---- */
	@Test public void fruizionePlainRegistrazioneMultipartRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizionePlainRegistrazioneMultipartRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizionePlainRegistrazioneMultipartRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizionePlainRegistrazioneMultipartRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizionePlainRegistrazioneMultipartRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainRegistrazioneMultipartRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainRegistrazioneMultipartRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainRegistrazioneMultipartRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizionePlainRegistrazioneMultipartRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainRegistrazioneMultipartRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizionePlainRegistrazioneMultipartRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizionePlainRegistrazioneMultipartRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizionePlainRegistrazioneMultipartRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizionePlainRegistrazioneMultipartRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizionePlainRegistrazioneMultipartRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainRegistrazioneMultipartRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainRegistrazioneMultipartRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainRegistrazioneMultipartRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizionePlainRegistrazioneMultipartRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainRegistrazioneMultipartRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione PLAIN CACHING_JSON ---- */
	@Test public void fruizionePlainCachingJsonRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizionePlainCachingJsonRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizionePlainCachingJsonRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizionePlainCachingJsonRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizionePlainCachingJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainCachingJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainCachingJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainCachingJsonRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizionePlainCachingJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainCachingJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizionePlainCachingJsonRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizionePlainCachingJsonRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizionePlainCachingJsonRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizionePlainCachingJsonRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizionePlainCachingJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizionePlainCachingJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizionePlainCachingJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizionePlainCachingJsonRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizionePlainCachingJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizionePlainCachingJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.PLAIN, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione PLAIN validazione con contenuto non valido, archivio gzip, dimensione massima ---- */
	@Test public void fruizionePlainValidazioneJsonNonValidoRichiestaNone() throws Exception { _testValidazioneContenutoNonValido(TipoServizio.FRUIZIONE, Api.PLAIN, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizionePlainValidazioneGzipFileRichiesta() throws Exception { _testValidazioneGzipFile(TipoServizio.FRUIZIONE, Api.PLAIN, Direzione.RICHIESTA); }
	@Test public void fruizionePlainDimensioneRichiestaNone() throws Exception { _testDimensione(TipoServizio.FRUIZIONE, Api.PLAIN, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizionePlainDimensioneRichiestaGzip() throws Exception { _testDimensione(TipoServizio.FRUIZIONE, Api.PLAIN, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizionePlainValidazioneJsonNonValidoRispostaNone() throws Exception { _testValidazioneContenutoNonValido(TipoServizio.FRUIZIONE, Api.PLAIN, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizionePlainValidazioneGzipFileRisposta() throws Exception { _testValidazioneGzipFile(TipoServizio.FRUIZIONE, Api.PLAIN, Direzione.RISPOSTA); }
	@Test public void fruizionePlainDimensioneRispostaNone() throws Exception { _testDimensione(TipoServizio.FRUIZIONE, Api.PLAIN, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizionePlainDimensioneRispostaGzip() throws Exception { _testDimensione(TipoServizio.FRUIZIONE, Api.PLAIN, Direzione.RISPOSTA, Enc.GZIP); }

	/* ---- fruizione DECOMPRESS VALIDAZIONE_JSON ---- */
	@Test public void fruizioneDecompressValidazioneJsonRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizioneDecompressValidazioneJsonRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressValidazioneJsonRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizioneDecompressValidazioneJsonRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressValidazioneJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressValidazioneJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressValidazioneJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressValidazioneJsonRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizioneDecompressValidazioneJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressValidazioneJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizioneDecompressValidazioneJsonRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizioneDecompressValidazioneJsonRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressValidazioneJsonRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizioneDecompressValidazioneJsonRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressValidazioneJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressValidazioneJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressValidazioneJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressValidazioneJsonRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizioneDecompressValidazioneJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressValidazioneJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione DECOMPRESS VALIDAZIONE_WARNING_JSON ---- */
	@Test public void fruizioneDecompressValidazioneWarningJsonRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressValidazioneWarningJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_WARNING_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione DECOMPRESS VALIDAZIONE_XML ---- */
	@Test public void fruizioneDecompressValidazioneXmlRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizioneDecompressValidazioneXmlRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressValidazioneXmlRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizioneDecompressValidazioneXmlRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressValidazioneXmlRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressValidazioneXmlRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressValidazioneXmlRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressValidazioneXmlRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizioneDecompressValidazioneXmlRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressValidazioneXmlRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizioneDecompressValidazioneXmlRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizioneDecompressValidazioneXmlRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressValidazioneXmlRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizioneDecompressValidazioneXmlRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressValidazioneXmlRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressValidazioneXmlRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressValidazioneXmlRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressValidazioneXmlRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizioneDecompressValidazioneXmlRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressValidazioneXmlRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_XML, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione DECOMPRESS VALIDAZIONE_MULTIPART ---- */
	@Test public void fruizioneDecompressValidazioneMultipartRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizioneDecompressValidazioneMultipartRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressValidazioneMultipartRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizioneDecompressValidazioneMultipartRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressValidazioneMultipartRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressValidazioneMultipartRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressValidazioneMultipartRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressValidazioneMultipartRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizioneDecompressValidazioneMultipartRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressValidazioneMultipartRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizioneDecompressValidazioneMultipartRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizioneDecompressValidazioneMultipartRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressValidazioneMultipartRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizioneDecompressValidazioneMultipartRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressValidazioneMultipartRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressValidazioneMultipartRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressValidazioneMultipartRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressValidazioneMultipartRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizioneDecompressValidazioneMultipartRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressValidazioneMultipartRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione DECOMPRESS VALIDAZIONE_BINARY ---- */
	@Test public void fruizioneDecompressValidazioneBinaryRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizioneDecompressValidazioneBinaryRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressValidazioneBinaryRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizioneDecompressValidazioneBinaryRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressValidazioneBinaryRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressValidazioneBinaryRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressValidazioneBinaryRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressValidazioneBinaryRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizioneDecompressValidazioneBinaryRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressValidazioneBinaryRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizioneDecompressValidazioneBinaryRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizioneDecompressValidazioneBinaryRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressValidazioneBinaryRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizioneDecompressValidazioneBinaryRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressValidazioneBinaryRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressValidazioneBinaryRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressValidazioneBinaryRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressValidazioneBinaryRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizioneDecompressValidazioneBinaryRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressValidazioneBinaryRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.VALIDAZIONE_BINARY, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione DECOMPRESS CORRELAZIONE_JSON ---- */
	@Test public void fruizioneDecompressCorrelazioneJsonRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizioneDecompressCorrelazioneJsonRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressCorrelazioneJsonRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizioneDecompressCorrelazioneJsonRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressCorrelazioneJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressCorrelazioneJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressCorrelazioneJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressCorrelazioneJsonRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizioneDecompressCorrelazioneJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressCorrelazioneJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizioneDecompressCorrelazioneJsonRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizioneDecompressCorrelazioneJsonRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressCorrelazioneJsonRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizioneDecompressCorrelazioneJsonRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressCorrelazioneJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressCorrelazioneJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressCorrelazioneJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressCorrelazioneJsonRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizioneDecompressCorrelazioneJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressCorrelazioneJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CORRELAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione DECOMPRESS TRASFORMAZIONE_RICHIESTA_JSON ---- */
	@Test public void fruizioneDecompressTrasformazioneRichiestaJsonRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizioneDecompressTrasformazioneRichiestaJsonRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressTrasformazioneRichiestaJsonRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizioneDecompressTrasformazioneRichiestaJsonRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressTrasformazioneRichiestaJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressTrasformazioneRichiestaJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressTrasformazioneRichiestaJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressTrasformazioneRichiestaJsonRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizioneDecompressTrasformazioneRichiestaJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressTrasformazioneRichiestaJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	/* ---- fruizione DECOMPRESS TRASFORMAZIONE_RISPOSTA_JSON ---- */
	@Test public void fruizioneDecompressTrasformazioneRispostaJsonRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizioneDecompressTrasformazioneRispostaJsonRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressTrasformazioneRispostaJsonRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizioneDecompressTrasformazioneRispostaJsonRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressTrasformazioneRispostaJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressTrasformazioneRispostaJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressTrasformazioneRispostaJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressTrasformazioneRispostaJsonRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizioneDecompressTrasformazioneRispostaJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressTrasformazioneRispostaJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione DECOMPRESS REGISTRAZIONE_JSON ---- */
	@Test public void fruizioneDecompressRegistrazioneJsonRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizioneDecompressRegistrazioneJsonRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressRegistrazioneJsonRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizioneDecompressRegistrazioneJsonRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressRegistrazioneJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressRegistrazioneJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressRegistrazioneJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressRegistrazioneJsonRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizioneDecompressRegistrazioneJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressRegistrazioneJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizioneDecompressRegistrazioneJsonRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizioneDecompressRegistrazioneJsonRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressRegistrazioneJsonRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizioneDecompressRegistrazioneJsonRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressRegistrazioneJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressRegistrazioneJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressRegistrazioneJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressRegistrazioneJsonRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizioneDecompressRegistrazioneJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressRegistrazioneJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione DECOMPRESS REGISTRAZIONE_MULTIPART ---- */
	@Test public void fruizioneDecompressRegistrazioneMultipartRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressRegistrazioneMultipartRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.REGISTRAZIONE_MULTIPART, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione DECOMPRESS CACHING_JSON ---- */
	@Test public void fruizioneDecompressCachingJsonRichiestaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizioneDecompressCachingJsonRichiestaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressCachingJsonRichiestaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizioneDecompressCachingJsonRichiestaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressCachingJsonRichiestaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressCachingJsonRichiestaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressCachingJsonRichiestaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressCachingJsonRichiestaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.BR); }
	@Test public void fruizioneDecompressCachingJsonRichiestaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressCachingJsonRichiestaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RICHIESTA, Enc.GZIP_INVALID); }
	@Test public void fruizioneDecompressCachingJsonRispostaNone() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizioneDecompressCachingJsonRispostaIdentity() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.IDENTITY); }
	@Test public void fruizioneDecompressCachingJsonRispostaGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizioneDecompressCachingJsonRispostaXGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.X_GZIP); }
	@Test public void fruizioneDecompressCachingJsonRispostaDeflateZlib() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneDecompressCachingJsonRispostaDeflateRaw() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneDecompressCachingJsonRispostaGzipUppercase() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneDecompressCachingJsonRispostaBr() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.BR); }
	@Test public void fruizioneDecompressCachingJsonRispostaDeflateGzip() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneDecompressCachingJsonRispostaGzipInvalid() throws Exception { _test(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Funzionalita.CACHING_JSON, Direzione.RISPOSTA, Enc.GZIP_INVALID); }
	/* ---- fruizione DECOMPRESS validazione con contenuto non valido, archivio gzip, dimensione massima ---- */
	@Test public void fruizioneDecompressValidazioneJsonNonValidoRichiestaGzip() throws Exception { _testValidazioneContenutoNonValido(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizioneDecompressValidazioneGzipFileRichiesta() throws Exception { _testValidazioneGzipFile(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Direzione.RICHIESTA); }
	@Test public void fruizioneDecompressDimensioneRichiestaNone() throws Exception { _testDimensione(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Direzione.RICHIESTA, Enc.NONE); }
	@Test public void fruizioneDecompressDimensioneRichiestaGzip() throws Exception { _testDimensione(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Direzione.RICHIESTA, Enc.GZIP); }
	@Test public void fruizioneDecompressValidazioneJsonNonValidoRispostaGzip() throws Exception { _testValidazioneContenutoNonValido(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Direzione.RISPOSTA, Enc.GZIP); }
	@Test public void fruizioneDecompressValidazioneGzipFileRisposta() throws Exception { _testValidazioneGzipFile(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Direzione.RISPOSTA); }
	@Test public void fruizioneDecompressDimensioneRispostaNone() throws Exception { _testDimensione(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Direzione.RISPOSTA, Enc.NONE); }
	@Test public void fruizioneDecompressDimensioneRispostaGzip() throws Exception { _testDimensione(TipoServizio.FRUIZIONE, Api.DECOMPRESS, Direzione.RISPOSTA, Enc.GZIP); }




	/* ====================================================================================== */
	/* ======================================= ENGINE ======================================== */
	/* ====================================================================================== */

	/**
	 * Scenario generico: la funzionalità {@code f} è attiva sulla risorsa; il contenuto compresso è
	 * quello della {@code direzione} indicata (richiesta inviata dal client o risposta del backend).
	 */
	private void _test(TipoServizio tipo, Api api, Funzionalita f, Direzione direzione, Enc enc) throws Exception {
		String id = "id-" + UUID.randomUUID();
		byte[] plain = contenuto(f, id, direzione);
		String label = "[" + tipo + "/" + api + "/" + f + "/" + direzione + "/" + enc + "]";

		HttpResponse response;
		byte[] onWire = null;
		if (Direzione.RICHIESTA.equals(direzione)) {
			onWire = encodeForRequest(plain, enc);
			response = invoke(tipo, api, f, onWire, requestDeclaredEncoding(enc), rispostaBackendRichiesta(f, id), Enc.NONE);
		}
		else {
			response = invoke(tipo, api, f, richiestaInChiaro(f, id), "", plain, enc);
		}
		String idTransazione = idTransazione(response, label);

		/* contenuto in chiaro: la funzionalità opera come oggi */
		if (enc.isPlain()) {
			verifyOk(response, label);
			verifyDestinatario(response, direzione, f, plain, onWire, enc, id, true, label);
			verifyFunzionalita(idTransazione, f, direzione, id, label);
			if (Funzionalita.CACHING_JSON.equals(f)) {
				verifyCacheHit(tipo, api, f, direzione, onWire, plain, enc, id, response, label);
			}
			return;
		}

		String ce = Direzione.RICHIESTA.equals(direzione) ? requestDeclaredEncoding(enc) : declaredEncoding(enc);

		if (Api.DECOMPRESS.equals(api)) {
			if (ContentEncodingTestUtils.Kind.PARSED.equals(enc.kind)) {
				verifyOk(response, label);
				DBVerifier.existsDiagnostico(idTransazione, DIAG_DECOMPRESSED);
				verifyDestinatario(response, direzione, f, plain, onWire, enc, id, false, label);
				verifyFunzionalita(idTransazione, f, direzione, id, label);
				if (Funzionalita.CACHING_JSON.equals(f)) {
					verifyCacheHit(tipo, api, f, direzione, onWire, plain, enc, id, response, label);
				}
			}
			else {
				/* encoding non supportato o byte non decomprimibili: errore della decompressione (comportamento esistente) */
				assertFalse(label + " atteso errore, ricevuto 200", response.getResultHTTPOperation() == 200);
				if (ContentEncodingTestUtils.Kind.UNSUPPORTED.equals(enc.kind)) {
					DBVerifier.existsDiagnostico(idTransazione, DIAG_UNSUPPORTED);
				}
			}
			return;
		}

		/* decompressione disabilitata, contenuto compresso */
		switch (f.accesso) {
		case INTERPRETAZIONE:
			assertFalse(label + " atteso errore esplicito, ricevuto 200", response.getResultHTTPOperation() == 200);
			verifyDiagnosticoContenutoCompresso(idTransazione, ce);
			break;
		case INTERPRETAZIONE_WARNING:
			/* validazione warning-only non superata: la transazione prosegue con esito 'OK con anomalie' */
			verifyOk(response, EsitoTransazioneName.OK_PRESENZA_ANOMALIE, label);
			verifyDiagnosticoContenutoCompresso(idTransazione, ce);
			verifyDestinatario(response, direzione, f, plain, onWire, enc, id, true, label);
			break;
		case BINARIO:
			verifyOk(response, label);
			DBVerifier.notExistsDiagnostico(idTransazione, ContentEncodingTestUtils.DIAG_CONTENUTO_COMPRESSO_PREFIX);
			verifyDestinatario(response, direzione, f, plain, onWire, enc, id, true, label);
			if (Funzionalita.CACHING_JSON.equals(f)) {
				verifyCacheHit(tipo, api, f, direzione, onWire, plain, enc, id, response, label);
			}
			break;
		case REGISTRAZIONE:
			verifyOk(response, label);
			verifyDestinatario(response, direzione, f, plain, onWire, enc, id, true, label);
			DBVerifier.existsDiagnostico(idTransazione, DIAG_REGISTRAZIONE_SENZA_INTERPRETAZIONE);
			DBVerifier.existsDumpMessaggio(idTransazione, Direzione.RICHIESTA.equals(direzione) ? "RichiestaIngresso" : "RispostaIngresso");
			break;
		}
	}

	/** Validazione con contenuto (decodificato) non valido: la validazione deve operare davvero sul contenuto in chiaro. */
	private void _testValidazioneContenutoNonValido(TipoServizio tipo, Api api, Direzione direzione, Enc enc) throws Exception {
		byte[] nonValido = "{\"nome\":\"senza id\"}".getBytes(StandardCharsets.UTF_8);
		String id = "id-" + UUID.randomUUID();
		String label = "[" + tipo + "/" + api + "/validazioneJson-nonValido/" + direzione + "/" + enc + "]";
		HttpResponse response;
		if (Direzione.RICHIESTA.equals(direzione)) {
			response = invoke(tipo, api, Funzionalita.VALIDAZIONE_JSON, encodeForRequest(nonValido, enc), requestDeclaredEncoding(enc),
					rispostaBackendRichiesta(Funzionalita.VALIDAZIONE_JSON, id), Enc.NONE);
		}
		else {
			response = invoke(tipo, api, Funzionalita.VALIDAZIONE_JSON, richiestaInChiaro(Funzionalita.VALIDAZIONE_JSON, id), "", nonValido, enc);
		}
		String idTransazione = idTransazione(response, label);
		assertFalse(label + " attesa violazione della validazione, ricevuto 200", response.getResultHTTPOperation() == 200);
		DBVerifier.notExistsDiagnostico(idTransazione, ContentEncodingTestUtils.DIAG_CONTENUTO_COMPRESSO_PREFIX);
	}

	/**
	 * Contenuto application/gzip senza Content-Encoding: è il contenuto stesso ad essere un archivio
	 * compresso, non una codifica HTTP; la validazione (binaria) non cambia comportamento.
	 */
	private void _testValidazioneGzipFile(TipoServizio tipo, Api api, Direzione direzione) throws Exception {
		byte[] archivio = ContentEncodingTestUtils.gzip("contenuto dell'archivio".getBytes(StandardCharsets.UTF_8));
		String label = "[" + tipo + "/" + api + "/validazioneGzipFile/" + direzione + "]";
		HttpRequest request = baseRequest(tipo, api, "validazioneGzipFile");
		request.setContentType("application/gzip");
		if (Direzione.RICHIESTA.equals(direzione)) {
			request.setContent(archivio);
			request.addHeader(ContentEncodingMockServer.HEADER_REPLY_BODY_BASE64, Base64.getEncoder().encodeToString(archivio));
		}
		else {
			request.setContent(archivio);
		}
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_ENCODING, ContentEncodingMockServer.REPLY_ENCODING_NONE);
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_CONTENT_TYPE, "application/gzip");
		HttpResponse response = send(request);
		verifyOk(response, label);
		verifyBodyIdentico(response, label);
		assertTrue(label + " archivio alterato", Arrays.equals(archivio, response.getContent()));
		DBVerifier.notExistsDiagnostico(idTransazione(response, label), ContentEncodingTestUtils.DIAG_CONTENUTO_COMPRESSO_PREFIX);
	}

	/**
	 * Dimensione massima dei messaggi (policy da 8 KB): contenuto in chiaro di 64 KB, compresso in poche
	 * centinaia di byte. Senza decompressione il limite si applica ai byte sul wire (nessun accesso al
	 * contenuto: passthrough); con la decompressione ai byte decompressi (protezione zip bomb).
	 */
	private void _testDimensione(TipoServizio tipo, Api api, Direzione direzione, Enc enc) throws Exception {
		StringBuilder sb = new StringBuilder("{\"id\":\"dimensione\",\"riempimento\":\"");
		while (sb.length() < 64 * 1024) {
			sb.append("abcdefghijklmnopqrstuvwxyz");
		}
		sb.append("\"}");
		byte[] plain = sb.toString().getBytes(StandardCharsets.UTF_8);
		String label = "[" + tipo + "/" + api + "/dimensioneJson/" + direzione + "/" + enc + "]";
		HttpResponse response;
		byte[] onWire = null;
		if (Direzione.RICHIESTA.equals(direzione)) {
			onWire = encodeForRequest(plain, enc);
			assertTrue(label + " il contenuto compresso di test deve stare sotto la soglia", enc.isPlain() || onWire.length < SOGLIA_DIMENSIONE_KB * 1024);
			response = invokeRisorsa(tipo, api, "dimensioneJson", "application/json", onWire, requestDeclaredEncoding(enc),
					"{\"id\":\"ok\"}".getBytes(StandardCharsets.UTF_8), Enc.NONE);
		}
		else {
			response = invokeRisorsa(tipo, api, "dimensioneJson", "application/json", "{\"id\":\"ok\"}".getBytes(StandardCharsets.UTF_8), "", plain, enc);
		}
		if (!enc.isPlain() && Api.DECOMPRESS.equals(api) && Direzione.RISPOSTA.equals(direzione)) {
			/* risposta decompressa: la dimensione si conosce solo leggendo lo stream, come per una risposta chunked. La risposta
			 * viene inoltrata in streaming (status 200 già inviato al client) e la lettura si interrompe al limite dei byte
			 * decompressi (protezione zip bomb): la transazione è classificata come policy violata (vedi rate_limiting.dimensione_messaggi.RestUtilities). */
			verifyOk(response, EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA, label);
			DBVerifier.existsDiagnostico(idTransazione(response, label), DIAG_RESPONSE_PAYLOAD_TOO_LARGE);
			assertFalse(label + " il client non deve ricevere il contenuto decompresso completo oltre il limite",
					Arrays.equals(plain, response.getContent()));
			return;
		}
		if (enc.isPlain() || Api.DECOMPRESS.equals(api)) {
			/* in chiaro, o richiesta decompressa: 64 KB oltre la soglia -> rifiutato */
			assertFalse(label + " atteso rifiuto per dimensione, ricevuto 200", response.getResultHTTPOperation() == 200);
			return;
		}
		/* senza decompressione: sul wire poche centinaia di byte -> transito invariato */
		verifyOk(response, label);
		if (Direzione.RICHIESTA.equals(direzione)) {
			verifyBackendRicevutoIdentico(response, onWire, enc, label);
		}
		else {
			verifyBodyIdentico(response, label);
		}
	}

	/* ====================================================================================== */
	/* ================================== Verifiche ========================================== */
	/* ====================================================================================== */

	/** Verifica cosa ha ricevuto il destinatario (backend per la richiesta, client per la risposta). */
	private static void verifyDestinatario(HttpResponse response, Direzione direzione, Funzionalita f, byte[] plain, byte[] onWire,
			Enc enc, String id, boolean byteOriginali, String label) {
		if (Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON.equals(f) && Direzione.RICHIESTA.equals(direzione)) {
			/* il mock restituisce il body ricevuto: è quello prodotto dalla trasformazione */
			String ricevuto = new String(response.getContent(), StandardCharsets.UTF_8);
			assertEquals(label + " contenuto trasformato ricevuto dal backend", "{\"idEstrattoRichiesta\":\"" + id + "\"}", ricevuto);
			String receivedCE = response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_CONTENT_ENCODING);
			assertTrue(label + " il backend non deve ricevere Content-Encoding su un contenuto trasformato: " + receivedCE,
					receivedCE == null || receivedCE.isEmpty());
			return;
		}
		if (Funzionalita.TRASFORMAZIONE_RISPOSTA_JSON.equals(f) && Direzione.RISPOSTA.equals(direzione)) {
			verifyNoContentEncoding(response, label);
			assertEquals(label + " contenuto trasformato ricevuto dal client", "{\"idEstrattoRisposta\":\"" + id + "\"}",
					new String(response.getContent(), StandardCharsets.UTF_8));
			return;
		}
		if (Direzione.RICHIESTA.equals(direzione)) {
			if (byteOriginali) {
				verifyBackendRicevutoIdentico(response, onWire, enc, label);
			}
			else {
				verifyBackendRicevutoInChiaro(response, plain, label);
			}
		}
		else {
			if (byteOriginali) {
				verifyBodyIdentico(response, label);
			}
			else {
				verifyNoContentEncoding(response, label);
				assertTrue(label + " atteso contenuto in chiaro uguale a quello del backend", Arrays.equals(plain, response.getContent()));
			}
		}
	}

	/** Verifiche specifiche della funzionalità quando ha operato sul contenuto (in chiaro o decompresso). */
	private static void verifyFunzionalita(String idTransazione, Funzionalita f, Direzione direzione, String id, String label) throws Exception {
		if (Funzionalita.CORRELAZIONE_JSON.equals(f)) {
			String colonna = Direzione.RICHIESTA.equals(direzione) ? "id_correlazione_applicativa" : "id_correlazione_risposta";
			String valore = readCorrelazione(idTransazione, colonna);
			assertEquals(label + " identificativo di correlazione (" + colonna + ")", id, valore);
		}
		if (Funzionalita.REGISTRAZIONE_JSON.equals(f) || Funzionalita.REGISTRAZIONE_MULTIPART.equals(f)) {
			DBVerifier.existsDumpMessaggio(idTransazione, Direzione.RICHIESTA.equals(direzione) ? "RichiestaIngresso" : "RispostaIngresso");
			DBVerifier.notExistsDiagnostico(idTransazione, DIAG_REGISTRAZIONE_SENZA_INTERPRETAZIONE);
		}
	}

	private static String readCorrelazione(String idTransazione, String colonna) throws Exception {
		String query = "select " + colonna + " from transazioni where id = ?";
		String valore = null;
		for (int attesa : new int[] {100, 500, 2000, 5000}) {
			Utilities.sleep(attesa);
			try {
				valore = ConfigLoader.getDbUtils().readValue(query, String.class, idTransazione);
			} catch (Exception e) {
				valore = null;
			}
			if (valore != null) {
				break;
			}
		}
		return valore;
	}

	/**
	 * Response cache: una seconda invocazione identica deve essere servita dalla cache (stesso
	 * identificativo di invocazione del mock) con gli stessi byte e lo stesso Content-Encoding.
	 */
	private void verifyCacheHit(TipoServizio tipo, Api api, Funzionalita f, Direzione direzione, byte[] onWire, byte[] plain, Enc enc,
			String id, HttpResponse prima, String label) throws Exception {
		HttpResponse seconda;
		if (Direzione.RICHIESTA.equals(direzione)) {
			seconda = invoke(tipo, api, f, onWire, requestDeclaredEncoding(enc), rispostaBackendRichiesta(f, id), Enc.NONE);
		}
		else {
			seconda = invoke(tipo, api, f, richiestaInChiaro(f, id), "", plain, enc);
		}
		String l2 = label + "[seconda invocazione]";
		assertEquals(l2 + " status code", 200, seconda.getResultHTTPOperation());
		assertEquals(l2 + " attesa risposta servita dalla cache", prima.getHeaderFirstValue(ContentEncodingMockServer.HEADER_INVOCATION_ID),
				seconda.getHeaderFirstValue(ContentEncodingMockServer.HEADER_INVOCATION_ID));
		assertTrue(l2 + " body della risposta in cache diverso dalla prima", Arrays.equals(prima.getContent(), seconda.getContent()));
		assertEquals(l2 + " Content-Encoding della risposta in cache", prima.getHeaderFirstValue(HttpConstants.CONTENT_ENCODING),
				seconda.getHeaderFirstValue(HttpConstants.CONTENT_ENCODING));
	}

	/* ====================================================================================== */
	/* ===================================== Contenuti ======================================= */
	/* ====================================================================================== */

	/** Contenuto (in chiaro) che viaggia compresso nella direzione del test. */
	private static byte[] contenuto(Funzionalita f, String id, Direzione direzione) {
		String s;
		switch (f) {
		case VALIDAZIONE_XML:
			s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><documento><id>" + id + "</id><valore>àèì</valore></documento>";
			break;
		case VALIDAZIONE_MULTIPART:
			s = "--" + ContentTypeBoundary.BOUNDARY + "\r\nContent-Disposition: form-data; name=\"id\"\r\n\r\n" + id + "\r\n"
					+ "--" + ContentTypeBoundary.BOUNDARY + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"dati.txt\"\r\nContent-Type: text/plain\r\n\r\n"
					+ "contenuto del file àèì\r\n--" + ContentTypeBoundary.BOUNDARY + "--\r\n";
			break;
		case REGISTRAZIONE_MULTIPART:
			s = "--" + ContentTypeBoundary.BOUNDARY + "\r\nContent-Type: application/json\r\n\r\n{\"id\":\"" + id + "\"}\r\n"
					+ "--" + ContentTypeBoundary.BOUNDARY + "\r\nContent-Type: text/plain\r\n\r\nparte testuale àèì\r\n--" + ContentTypeBoundary.BOUNDARY + "--\r\n";
			break;
		case VALIDAZIONE_BINARY:
			return ContentTypeContentEncodingEngine.binario();
		default:
			s = "{\"id\":\"" + id + "\",\"direzione\":\"" + direzione + "\",\"valore\":\"àèì €\"}";
			break;
		}
		return s.getBytes(StandardCharsets.UTF_8);
	}

	/** Richiesta in chiaro inviata dal client negli scenari lato risposta (valida per la funzionalità). */
	private static byte[] richiestaInChiaro(Funzionalita f, String id) {
		return contenuto(f, id, Direzione.RICHIESTA);
	}

	/**
	 * Risposta del backend negli scenari lato richiesta: per la trasformazione della richiesta il mock
	 * restituisce il body ricevuto (null = echo), così il test verifica il contenuto trasformato.
	 */
	private static byte[] rispostaBackendRichiesta(Funzionalita f, String id) {
		return Funzionalita.TRASFORMAZIONE_RICHIESTA_JSON.equals(f) ? null : rispostaBackendInChiaro(f, id);
	}

	/** Risposta in chiaro del backend negli scenari lato richiesta (valida per la funzionalità). */
	private static byte[] rispostaBackendInChiaro(Funzionalita f, String id) {
		return contenuto(f, id, Direzione.RISPOSTA);
	}

	/* ====================================================================================== */
	/* ===================================== Invocazioni ===================================== */
	/* ====================================================================================== */

	private HttpResponse invoke(TipoServizio tipo, Api api, Funzionalita f, byte[] requestBody, String requestCE,
			byte[] replyBody, Enc replyEnc) throws Exception {
		return invokeRisorsa(tipo, api, f.risorsa, f.contentType, requestBody, requestCE, replyBody, replyEnc);
	}

	private HttpResponse invokeRisorsa(TipoServizio tipo, Api api, String risorsa, String contentType, byte[] requestBody, String requestCE,
			byte[] replyBody, Enc replyEnc) throws Exception {
		HttpRequest request = baseRequest(tipo, api, risorsa);
		request.setContentType(contentType);
		request.setContent(requestBody);
		if (requestCE != null && !requestCE.isEmpty()) {
			request.addHeader(HttpConstants.CONTENT_ENCODING, requestCE);
		}
		if (replyBody != null) {
			request.addHeader(ContentEncodingMockServer.HEADER_REPLY_BODY_BASE64, Base64.getEncoder().encodeToString(replyBody));
		}
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_ENCODING, replyEnc.replyEncoding);
		if (replyEnc.declaredOverride != null) {
			request.addHeader(ContentEncodingMockServer.HEADER_REPLY_CONTENT_ENCODING, replyEnc.declaredOverride);
		}
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_CONTENT_TYPE, contentType);
		return send(request);
	}

	private HttpRequest baseRequest(TipoServizio tipo, Api api, String risorsa) {
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(ContentEncodingTestUtils.buildUrl(tipo, api.nome, risorsa));
		return request;
	}

	private HttpResponse send(HttpRequest request) throws Exception {
		if (this.libraryMode != null) {
			this.libraryMode.patchRequest(request);
		}
		return HttpUtilities.httpInvoke(request);
	}

	private static String idTransazione(HttpResponse response, String label) {
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(label + " manca GovWay-Transaction-ID", idTransazione);
		return idTransazione;
	}

	private void verifyOk(HttpResponse response, String label) throws Exception {
		verifyOk(response, EsitoTransazioneName.OK, label);
	}
	private void verifyOk(HttpResponse response, EsitoTransazioneName esito, String label) throws Exception {
		String idTransazione = idTransazione(response, label);
		assertEquals(label + " status code", 200, response.getResultHTTPOperation());
		EsitiProperties esiti = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME);
		DBVerifier.verify(idTransazione, esiti.convertoToCode(esito), this.libraryMode);
	}
}
