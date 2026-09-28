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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.clientDecode;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.encodeForRequest;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.requestDeclaredEncoding;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.verifyBackendRicevutoIdentico;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.verifyBackendRicevutoInChiaro;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.verifyBodyIdentico;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.verifyNoContentEncoding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.Enc;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.Kind;
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
 * Transito di richieste e risposte REST non Problem Details con Content-Encoding, per tutti i tipi di
 * contenuto (JSON, XML, testo, binario, multipart mixed/related/form-data).
 *
 * <h2>Scenari</h2>
 * <ul>
 *   <li><b>Passthrough</b> (risorsa {@code default}, decompressione disabilitata, nessuna funzionalità
 *       che accede al contenuto): byte e Content-Encoding devono arrivare invariati al destinatario
 *       (client per la risposta, backend per la richiesta), per qualsiasi encoding, anche non supportato
 *       o non valido.</li>
 *   <li><b>Decompressione abilitata</b> (risorse {@code response} e {@code request}): con encoding
 *       supportato il destinatario riceve il contenuto in chiaro senza Content-Encoding; con encoding non
 *       supportato o byte non validi la transazione fallisce (comportamento esistente).</li>
 * </ul>
 * I contenuti sono costruiti per rendere visibile qualsiasi alterazione: il binario contiene tutti i 256
 * valori di byte, i contenuti testuali caratteri non ASCII, i multipart parti miste testo/binario.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentTypeContentEncodingEngine extends ConfigLoader {

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

	private static final String API = "TestContentEncoding";
	private static final String RES_DEFAULT = "default";
	private static final String RES_REQUEST = "request";
	private static final String RES_RESPONSE = "response";

	private static final String DIAG_DECOMPRESSED = "applicata decompressione automatica";
	private static final String DIAG_UNSUPPORTED = "non gestibile dalla decompressione automatica";

	private static final String BOUNDARY = "govwayTestBoundaryContentEncoding";
	/** Risposta in chiaro restituita dal mock negli scenari lato richiesta. */
	private static final byte[] RISPOSTA_FISSA = "{\"esito\":\"ricevuto\"}".getBytes(StandardCharsets.UTF_8);

	/** Tipi di contenuto verificati. */
	enum Tipo {
		JSON, XML, TEXT, BINARY, MULTIPART_MIXED, MULTIPART_RELATED, MULTIPART_FORM_DATA;

		String contentType() {
			switch (this) {
			case JSON: return "application/json";
			case XML: return "application/xml";
			case TEXT: return "text/plain; charset=UTF-8";
			case BINARY: return "application/octet-stream";
			case MULTIPART_MIXED: return "multipart/mixed; boundary=\"" + BOUNDARY + "\"";
			case MULTIPART_RELATED: return "multipart/related; type=\"application/json\"; boundary=\"" + BOUNDARY + "\"";
			default: return "multipart/form-data; boundary=" + BOUNDARY;
			}
		}

		byte[] body() throws IOException {
			switch (this) {
			case JSON: return "{\"tipo\":\"json\",\"id\":\"id-json\",\"valore\":\"contenuto àèìòù €\"}".getBytes(StandardCharsets.UTF_8);
			case XML: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><documento><tipo>xml</tipo><valore>contenuto àèìòù €</valore></documento>".getBytes(StandardCharsets.UTF_8);
			case TEXT: return "contenuto testuale àèìòù €\nseconda riga".getBytes(StandardCharsets.UTF_8);
			case BINARY: return binario();
			default: return multipart(this);
			}
		}
	}

	/** Tutti i 256 valori di byte, ripetuti: qualsiasi conversione in stringa li altererebbe. */
	static byte[] binario() {
		byte[] b = new byte[1024];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) (i % 256);
		}
		return b;
	}

	static byte[] multipart(Tipo tipo) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		String crlf = "\r\n";
		if (Tipo.MULTIPART_FORM_DATA.equals(tipo)) {
			bout.write(("--" + BOUNDARY + crlf + "Content-Disposition: form-data; name=\"id\"" + crlf + crlf + "id-form-data" + crlf).getBytes(StandardCharsets.UTF_8));
			bout.write(("--" + BOUNDARY + crlf + "Content-Disposition: form-data; name=\"file\"; filename=\"dati.bin\"" + crlf
					+ "Content-Type: application/octet-stream" + crlf + crlf).getBytes(StandardCharsets.UTF_8));
		}
		else {
			bout.write(("--" + BOUNDARY + crlf + "Content-Type: application/json" + crlf + "Content-ID: <parte1>" + crlf + crlf
					+ "{\"parte\":1,\"valore\":\"àèì\"}" + crlf).getBytes(StandardCharsets.UTF_8));
			bout.write(("--" + BOUNDARY + crlf + "Content-Type: application/octet-stream" + crlf + "Content-ID: <parte2>" + crlf + crlf).getBytes(StandardCharsets.UTF_8));
		}
		bout.write(binario());
		bout.write((crlf + "--" + BOUNDARY + "--" + crlf).getBytes(StandardCharsets.UTF_8));
		return bout.toByteArray();
	}

	/* ====================================================================================== */
	/* ======================================= @Test ========================================= */
	/* ====================================================================================== */

	/* ---- erogazione JSON ---- */
	@Test public void erogazioneJsonRispostaPassthroughNone() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.NONE); }
	@Test public void erogazioneJsonRispostaPassthroughIdentity() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.IDENTITY); }
	@Test public void erogazioneJsonRispostaPassthroughGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.GZIP); }
	@Test public void erogazioneJsonRispostaPassthroughXGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.X_GZIP); }
	@Test public void erogazioneJsonRispostaPassthroughDeflateZlib() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneJsonRispostaPassthroughDeflateRaw() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.DEFLATE_RAW); }
	@Test public void erogazioneJsonRispostaPassthroughGzipUppercase() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneJsonRispostaPassthroughBr() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.BR); }
	@Test public void erogazioneJsonRispostaPassthroughDeflateGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneJsonRispostaPassthroughGzipInvalid() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.GZIP_INVALID); }
	@Test public void erogazioneJsonRispostaDecompressNone() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.NONE); }
	@Test public void erogazioneJsonRispostaDecompressIdentity() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.IDENTITY); }
	@Test public void erogazioneJsonRispostaDecompressGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.GZIP); }
	@Test public void erogazioneJsonRispostaDecompressXGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.X_GZIP); }
	@Test public void erogazioneJsonRispostaDecompressDeflateZlib() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneJsonRispostaDecompressDeflateRaw() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.DEFLATE_RAW); }
	@Test public void erogazioneJsonRispostaDecompressGzipUppercase() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneJsonRispostaDecompressBr() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.BR); }
	@Test public void erogazioneJsonRispostaDecompressDeflateGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneJsonRispostaDecompressGzipInvalid() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.GZIP_INVALID); }
	@Test public void erogazioneJsonRichiestaPassthroughNone() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.NONE); }
	@Test public void erogazioneJsonRichiestaPassthroughIdentity() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.IDENTITY); }
	@Test public void erogazioneJsonRichiestaPassthroughGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.GZIP); }
	@Test public void erogazioneJsonRichiestaPassthroughXGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.X_GZIP); }
	@Test public void erogazioneJsonRichiestaPassthroughDeflateZlib() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneJsonRichiestaPassthroughDeflateRaw() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.DEFLATE_RAW); }
	@Test public void erogazioneJsonRichiestaPassthroughGzipUppercase() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneJsonRichiestaPassthroughBr() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.BR); }
	@Test public void erogazioneJsonRichiestaPassthroughDeflateGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneJsonRichiestaPassthroughGzipInvalid() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.GZIP_INVALID); }
	@Test public void erogazioneJsonRichiestaDecompressNone() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.NONE); }
	@Test public void erogazioneJsonRichiestaDecompressIdentity() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.IDENTITY); }
	@Test public void erogazioneJsonRichiestaDecompressGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.GZIP); }
	@Test public void erogazioneJsonRichiestaDecompressXGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.X_GZIP); }
	@Test public void erogazioneJsonRichiestaDecompressDeflateZlib() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneJsonRichiestaDecompressDeflateRaw() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.DEFLATE_RAW); }
	@Test public void erogazioneJsonRichiestaDecompressGzipUppercase() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneJsonRichiestaDecompressBr() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.BR); }
	@Test public void erogazioneJsonRichiestaDecompressDeflateGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneJsonRichiestaDecompressGzipInvalid() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.JSON, Enc.GZIP_INVALID); }

	/* ---- erogazione XML ---- */
	@Test public void erogazioneXmlRispostaPassthroughNone() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.NONE); }
	@Test public void erogazioneXmlRispostaPassthroughIdentity() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.IDENTITY); }
	@Test public void erogazioneXmlRispostaPassthroughGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.GZIP); }
	@Test public void erogazioneXmlRispostaPassthroughXGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.X_GZIP); }
	@Test public void erogazioneXmlRispostaPassthroughDeflateZlib() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneXmlRispostaPassthroughDeflateRaw() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.DEFLATE_RAW); }
	@Test public void erogazioneXmlRispostaPassthroughGzipUppercase() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneXmlRispostaPassthroughBr() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.BR); }
	@Test public void erogazioneXmlRispostaPassthroughDeflateGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneXmlRispostaPassthroughGzipInvalid() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.GZIP_INVALID); }
	@Test public void erogazioneXmlRispostaDecompressNone() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.NONE); }
	@Test public void erogazioneXmlRispostaDecompressIdentity() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.IDENTITY); }
	@Test public void erogazioneXmlRispostaDecompressGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.GZIP); }
	@Test public void erogazioneXmlRispostaDecompressXGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.X_GZIP); }
	@Test public void erogazioneXmlRispostaDecompressDeflateZlib() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneXmlRispostaDecompressDeflateRaw() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.DEFLATE_RAW); }
	@Test public void erogazioneXmlRispostaDecompressGzipUppercase() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneXmlRispostaDecompressBr() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.BR); }
	@Test public void erogazioneXmlRispostaDecompressDeflateGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneXmlRispostaDecompressGzipInvalid() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.GZIP_INVALID); }
	@Test public void erogazioneXmlRichiestaPassthroughNone() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.NONE); }
	@Test public void erogazioneXmlRichiestaPassthroughIdentity() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.IDENTITY); }
	@Test public void erogazioneXmlRichiestaPassthroughGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.GZIP); }
	@Test public void erogazioneXmlRichiestaPassthroughXGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.X_GZIP); }
	@Test public void erogazioneXmlRichiestaPassthroughDeflateZlib() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneXmlRichiestaPassthroughDeflateRaw() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.DEFLATE_RAW); }
	@Test public void erogazioneXmlRichiestaPassthroughGzipUppercase() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneXmlRichiestaPassthroughBr() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.BR); }
	@Test public void erogazioneXmlRichiestaPassthroughDeflateGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneXmlRichiestaPassthroughGzipInvalid() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.XML, Enc.GZIP_INVALID); }
	@Test public void erogazioneXmlRichiestaDecompressNone() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.NONE); }
	@Test public void erogazioneXmlRichiestaDecompressIdentity() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.IDENTITY); }
	@Test public void erogazioneXmlRichiestaDecompressGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.GZIP); }
	@Test public void erogazioneXmlRichiestaDecompressXGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.X_GZIP); }
	@Test public void erogazioneXmlRichiestaDecompressDeflateZlib() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneXmlRichiestaDecompressDeflateRaw() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.DEFLATE_RAW); }
	@Test public void erogazioneXmlRichiestaDecompressGzipUppercase() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneXmlRichiestaDecompressBr() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.BR); }
	@Test public void erogazioneXmlRichiestaDecompressDeflateGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneXmlRichiestaDecompressGzipInvalid() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.XML, Enc.GZIP_INVALID); }

	/* ---- erogazione TEXT ---- */
	@Test public void erogazioneTextRispostaPassthroughNone() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.NONE); }
	@Test public void erogazioneTextRispostaPassthroughIdentity() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.IDENTITY); }
	@Test public void erogazioneTextRispostaPassthroughGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.GZIP); }
	@Test public void erogazioneTextRispostaPassthroughXGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.X_GZIP); }
	@Test public void erogazioneTextRispostaPassthroughDeflateZlib() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneTextRispostaPassthroughDeflateRaw() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.DEFLATE_RAW); }
	@Test public void erogazioneTextRispostaPassthroughGzipUppercase() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneTextRispostaPassthroughBr() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.BR); }
	@Test public void erogazioneTextRispostaPassthroughDeflateGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneTextRispostaPassthroughGzipInvalid() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.GZIP_INVALID); }
	@Test public void erogazioneTextRispostaDecompressNone() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.NONE); }
	@Test public void erogazioneTextRispostaDecompressIdentity() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.IDENTITY); }
	@Test public void erogazioneTextRispostaDecompressGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.GZIP); }
	@Test public void erogazioneTextRispostaDecompressXGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.X_GZIP); }
	@Test public void erogazioneTextRispostaDecompressDeflateZlib() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneTextRispostaDecompressDeflateRaw() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.DEFLATE_RAW); }
	@Test public void erogazioneTextRispostaDecompressGzipUppercase() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneTextRispostaDecompressBr() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.BR); }
	@Test public void erogazioneTextRispostaDecompressDeflateGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneTextRispostaDecompressGzipInvalid() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.GZIP_INVALID); }
	@Test public void erogazioneTextRichiestaPassthroughNone() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.NONE); }
	@Test public void erogazioneTextRichiestaPassthroughIdentity() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.IDENTITY); }
	@Test public void erogazioneTextRichiestaPassthroughGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.GZIP); }
	@Test public void erogazioneTextRichiestaPassthroughXGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.X_GZIP); }
	@Test public void erogazioneTextRichiestaPassthroughDeflateZlib() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneTextRichiestaPassthroughDeflateRaw() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.DEFLATE_RAW); }
	@Test public void erogazioneTextRichiestaPassthroughGzipUppercase() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneTextRichiestaPassthroughBr() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.BR); }
	@Test public void erogazioneTextRichiestaPassthroughDeflateGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneTextRichiestaPassthroughGzipInvalid() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.GZIP_INVALID); }
	@Test public void erogazioneTextRichiestaDecompressNone() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.NONE); }
	@Test public void erogazioneTextRichiestaDecompressIdentity() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.IDENTITY); }
	@Test public void erogazioneTextRichiestaDecompressGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.GZIP); }
	@Test public void erogazioneTextRichiestaDecompressXGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.X_GZIP); }
	@Test public void erogazioneTextRichiestaDecompressDeflateZlib() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneTextRichiestaDecompressDeflateRaw() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.DEFLATE_RAW); }
	@Test public void erogazioneTextRichiestaDecompressGzipUppercase() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneTextRichiestaDecompressBr() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.BR); }
	@Test public void erogazioneTextRichiestaDecompressDeflateGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneTextRichiestaDecompressGzipInvalid() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.TEXT, Enc.GZIP_INVALID); }

	/* ---- erogazione BINARY ---- */
	@Test public void erogazioneBinaryRispostaPassthroughNone() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.NONE); }
	@Test public void erogazioneBinaryRispostaPassthroughIdentity() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.IDENTITY); }
	@Test public void erogazioneBinaryRispostaPassthroughGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.GZIP); }
	@Test public void erogazioneBinaryRispostaPassthroughXGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.X_GZIP); }
	@Test public void erogazioneBinaryRispostaPassthroughDeflateZlib() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneBinaryRispostaPassthroughDeflateRaw() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.DEFLATE_RAW); }
	@Test public void erogazioneBinaryRispostaPassthroughGzipUppercase() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneBinaryRispostaPassthroughBr() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.BR); }
	@Test public void erogazioneBinaryRispostaPassthroughDeflateGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneBinaryRispostaPassthroughGzipInvalid() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.GZIP_INVALID); }
	@Test public void erogazioneBinaryRispostaDecompressNone() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.NONE); }
	@Test public void erogazioneBinaryRispostaDecompressIdentity() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.IDENTITY); }
	@Test public void erogazioneBinaryRispostaDecompressGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.GZIP); }
	@Test public void erogazioneBinaryRispostaDecompressXGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.X_GZIP); }
	@Test public void erogazioneBinaryRispostaDecompressDeflateZlib() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneBinaryRispostaDecompressDeflateRaw() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.DEFLATE_RAW); }
	@Test public void erogazioneBinaryRispostaDecompressGzipUppercase() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneBinaryRispostaDecompressBr() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.BR); }
	@Test public void erogazioneBinaryRispostaDecompressDeflateGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneBinaryRispostaDecompressGzipInvalid() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.GZIP_INVALID); }
	@Test public void erogazioneBinaryRichiestaPassthroughNone() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.NONE); }
	@Test public void erogazioneBinaryRichiestaPassthroughIdentity() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.IDENTITY); }
	@Test public void erogazioneBinaryRichiestaPassthroughGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.GZIP); }
	@Test public void erogazioneBinaryRichiestaPassthroughXGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.X_GZIP); }
	@Test public void erogazioneBinaryRichiestaPassthroughDeflateZlib() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneBinaryRichiestaPassthroughDeflateRaw() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.DEFLATE_RAW); }
	@Test public void erogazioneBinaryRichiestaPassthroughGzipUppercase() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneBinaryRichiestaPassthroughBr() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.BR); }
	@Test public void erogazioneBinaryRichiestaPassthroughDeflateGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneBinaryRichiestaPassthroughGzipInvalid() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.GZIP_INVALID); }
	@Test public void erogazioneBinaryRichiestaDecompressNone() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.NONE); }
	@Test public void erogazioneBinaryRichiestaDecompressIdentity() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.IDENTITY); }
	@Test public void erogazioneBinaryRichiestaDecompressGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.GZIP); }
	@Test public void erogazioneBinaryRichiestaDecompressXGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.X_GZIP); }
	@Test public void erogazioneBinaryRichiestaDecompressDeflateZlib() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneBinaryRichiestaDecompressDeflateRaw() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.DEFLATE_RAW); }
	@Test public void erogazioneBinaryRichiestaDecompressGzipUppercase() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneBinaryRichiestaDecompressBr() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.BR); }
	@Test public void erogazioneBinaryRichiestaDecompressDeflateGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneBinaryRichiestaDecompressGzipInvalid() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.BINARY, Enc.GZIP_INVALID); }

	/* ---- erogazione MULTIPART_MIXED ---- */
	@Test public void erogazioneMultipartMixedRispostaPassthroughNone() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.NONE); }
	@Test public void erogazioneMultipartMixedRispostaPassthroughIdentity() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.IDENTITY); }
	@Test public void erogazioneMultipartMixedRispostaPassthroughGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP); }
	@Test public void erogazioneMultipartMixedRispostaPassthroughXGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.X_GZIP); }
	@Test public void erogazioneMultipartMixedRispostaPassthroughDeflateZlib() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneMultipartMixedRispostaPassthroughDeflateRaw() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_RAW); }
	@Test public void erogazioneMultipartMixedRispostaPassthroughGzipUppercase() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneMultipartMixedRispostaPassthroughBr() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.BR); }
	@Test public void erogazioneMultipartMixedRispostaPassthroughDeflateGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneMultipartMixedRispostaPassthroughGzipInvalid() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_INVALID); }
	@Test public void erogazioneMultipartMixedRispostaDecompressNone() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.NONE); }
	@Test public void erogazioneMultipartMixedRispostaDecompressIdentity() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.IDENTITY); }
	@Test public void erogazioneMultipartMixedRispostaDecompressGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP); }
	@Test public void erogazioneMultipartMixedRispostaDecompressXGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.X_GZIP); }
	@Test public void erogazioneMultipartMixedRispostaDecompressDeflateZlib() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneMultipartMixedRispostaDecompressDeflateRaw() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_RAW); }
	@Test public void erogazioneMultipartMixedRispostaDecompressGzipUppercase() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneMultipartMixedRispostaDecompressBr() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.BR); }
	@Test public void erogazioneMultipartMixedRispostaDecompressDeflateGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneMultipartMixedRispostaDecompressGzipInvalid() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_INVALID); }
	@Test public void erogazioneMultipartMixedRichiestaPassthroughNone() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.NONE); }
	@Test public void erogazioneMultipartMixedRichiestaPassthroughIdentity() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.IDENTITY); }
	@Test public void erogazioneMultipartMixedRichiestaPassthroughGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP); }
	@Test public void erogazioneMultipartMixedRichiestaPassthroughXGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.X_GZIP); }
	@Test public void erogazioneMultipartMixedRichiestaPassthroughDeflateZlib() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneMultipartMixedRichiestaPassthroughDeflateRaw() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_RAW); }
	@Test public void erogazioneMultipartMixedRichiestaPassthroughGzipUppercase() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneMultipartMixedRichiestaPassthroughBr() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.BR); }
	@Test public void erogazioneMultipartMixedRichiestaPassthroughDeflateGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneMultipartMixedRichiestaPassthroughGzipInvalid() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_INVALID); }
	@Test public void erogazioneMultipartMixedRichiestaDecompressNone() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.NONE); }
	@Test public void erogazioneMultipartMixedRichiestaDecompressIdentity() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.IDENTITY); }
	@Test public void erogazioneMultipartMixedRichiestaDecompressGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP); }
	@Test public void erogazioneMultipartMixedRichiestaDecompressXGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.X_GZIP); }
	@Test public void erogazioneMultipartMixedRichiestaDecompressDeflateZlib() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneMultipartMixedRichiestaDecompressDeflateRaw() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_RAW); }
	@Test public void erogazioneMultipartMixedRichiestaDecompressGzipUppercase() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneMultipartMixedRichiestaDecompressBr() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.BR); }
	@Test public void erogazioneMultipartMixedRichiestaDecompressDeflateGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneMultipartMixedRichiestaDecompressGzipInvalid() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_INVALID); }

	/* ---- erogazione MULTIPART_RELATED ---- */
	@Test public void erogazioneMultipartRelatedRispostaPassthroughNone() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.NONE); }
	@Test public void erogazioneMultipartRelatedRispostaPassthroughIdentity() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.IDENTITY); }
	@Test public void erogazioneMultipartRelatedRispostaPassthroughGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP); }
	@Test public void erogazioneMultipartRelatedRispostaPassthroughXGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.X_GZIP); }
	@Test public void erogazioneMultipartRelatedRispostaPassthroughDeflateZlib() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneMultipartRelatedRispostaPassthroughDeflateRaw() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_RAW); }
	@Test public void erogazioneMultipartRelatedRispostaPassthroughGzipUppercase() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneMultipartRelatedRispostaPassthroughBr() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.BR); }
	@Test public void erogazioneMultipartRelatedRispostaPassthroughDeflateGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneMultipartRelatedRispostaPassthroughGzipInvalid() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_INVALID); }
	@Test public void erogazioneMultipartRelatedRispostaDecompressNone() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.NONE); }
	@Test public void erogazioneMultipartRelatedRispostaDecompressIdentity() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.IDENTITY); }
	@Test public void erogazioneMultipartRelatedRispostaDecompressGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP); }
	@Test public void erogazioneMultipartRelatedRispostaDecompressXGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.X_GZIP); }
	@Test public void erogazioneMultipartRelatedRispostaDecompressDeflateZlib() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneMultipartRelatedRispostaDecompressDeflateRaw() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_RAW); }
	@Test public void erogazioneMultipartRelatedRispostaDecompressGzipUppercase() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneMultipartRelatedRispostaDecompressBr() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.BR); }
	@Test public void erogazioneMultipartRelatedRispostaDecompressDeflateGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneMultipartRelatedRispostaDecompressGzipInvalid() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_INVALID); }
	@Test public void erogazioneMultipartRelatedRichiestaPassthroughNone() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.NONE); }
	@Test public void erogazioneMultipartRelatedRichiestaPassthroughIdentity() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.IDENTITY); }
	@Test public void erogazioneMultipartRelatedRichiestaPassthroughGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP); }
	@Test public void erogazioneMultipartRelatedRichiestaPassthroughXGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.X_GZIP); }
	@Test public void erogazioneMultipartRelatedRichiestaPassthroughDeflateZlib() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneMultipartRelatedRichiestaPassthroughDeflateRaw() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_RAW); }
	@Test public void erogazioneMultipartRelatedRichiestaPassthroughGzipUppercase() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneMultipartRelatedRichiestaPassthroughBr() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.BR); }
	@Test public void erogazioneMultipartRelatedRichiestaPassthroughDeflateGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneMultipartRelatedRichiestaPassthroughGzipInvalid() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_INVALID); }
	@Test public void erogazioneMultipartRelatedRichiestaDecompressNone() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.NONE); }
	@Test public void erogazioneMultipartRelatedRichiestaDecompressIdentity() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.IDENTITY); }
	@Test public void erogazioneMultipartRelatedRichiestaDecompressGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP); }
	@Test public void erogazioneMultipartRelatedRichiestaDecompressXGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.X_GZIP); }
	@Test public void erogazioneMultipartRelatedRichiestaDecompressDeflateZlib() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneMultipartRelatedRichiestaDecompressDeflateRaw() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_RAW); }
	@Test public void erogazioneMultipartRelatedRichiestaDecompressGzipUppercase() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneMultipartRelatedRichiestaDecompressBr() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.BR); }
	@Test public void erogazioneMultipartRelatedRichiestaDecompressDeflateGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneMultipartRelatedRichiestaDecompressGzipInvalid() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_INVALID); }

	/* ---- erogazione MULTIPART_FORM_DATA ---- */
	@Test public void erogazioneMultipartFormDataRispostaPassthroughNone() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.NONE); }
	@Test public void erogazioneMultipartFormDataRispostaPassthroughIdentity() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.IDENTITY); }
	@Test public void erogazioneMultipartFormDataRispostaPassthroughGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP); }
	@Test public void erogazioneMultipartFormDataRispostaPassthroughXGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.X_GZIP); }
	@Test public void erogazioneMultipartFormDataRispostaPassthroughDeflateZlib() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneMultipartFormDataRispostaPassthroughDeflateRaw() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneMultipartFormDataRispostaPassthroughGzipUppercase() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneMultipartFormDataRispostaPassthroughBr() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.BR); }
	@Test public void erogazioneMultipartFormDataRispostaPassthroughDeflateGzip() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneMultipartFormDataRispostaPassthroughGzipInvalid() throws Exception { _testRispostaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_INVALID); }
	@Test public void erogazioneMultipartFormDataRispostaDecompressNone() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.NONE); }
	@Test public void erogazioneMultipartFormDataRispostaDecompressIdentity() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.IDENTITY); }
	@Test public void erogazioneMultipartFormDataRispostaDecompressGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP); }
	@Test public void erogazioneMultipartFormDataRispostaDecompressXGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.X_GZIP); }
	@Test public void erogazioneMultipartFormDataRispostaDecompressDeflateZlib() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneMultipartFormDataRispostaDecompressDeflateRaw() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneMultipartFormDataRispostaDecompressGzipUppercase() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneMultipartFormDataRispostaDecompressBr() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.BR); }
	@Test public void erogazioneMultipartFormDataRispostaDecompressDeflateGzip() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneMultipartFormDataRispostaDecompressGzipInvalid() throws Exception { _testRispostaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_INVALID); }
	@Test public void erogazioneMultipartFormDataRichiestaPassthroughNone() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.NONE); }
	@Test public void erogazioneMultipartFormDataRichiestaPassthroughIdentity() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.IDENTITY); }
	@Test public void erogazioneMultipartFormDataRichiestaPassthroughGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP); }
	@Test public void erogazioneMultipartFormDataRichiestaPassthroughXGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.X_GZIP); }
	@Test public void erogazioneMultipartFormDataRichiestaPassthroughDeflateZlib() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneMultipartFormDataRichiestaPassthroughDeflateRaw() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneMultipartFormDataRichiestaPassthroughGzipUppercase() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneMultipartFormDataRichiestaPassthroughBr() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.BR); }
	@Test public void erogazioneMultipartFormDataRichiestaPassthroughDeflateGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneMultipartFormDataRichiestaPassthroughGzipInvalid() throws Exception { _testRichiestaPassthrough(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_INVALID); }
	@Test public void erogazioneMultipartFormDataRichiestaDecompressNone() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.NONE); }
	@Test public void erogazioneMultipartFormDataRichiestaDecompressIdentity() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.IDENTITY); }
	@Test public void erogazioneMultipartFormDataRichiestaDecompressGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP); }
	@Test public void erogazioneMultipartFormDataRichiestaDecompressXGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.X_GZIP); }
	@Test public void erogazioneMultipartFormDataRichiestaDecompressDeflateZlib() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneMultipartFormDataRichiestaDecompressDeflateRaw() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_RAW); }
	@Test public void erogazioneMultipartFormDataRichiestaDecompressGzipUppercase() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneMultipartFormDataRichiestaDecompressBr() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.BR); }
	@Test public void erogazioneMultipartFormDataRichiestaDecompressDeflateGzip() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneMultipartFormDataRichiestaDecompressGzipInvalid() throws Exception { _testRichiestaDecompress(TipoServizio.EROGAZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_INVALID); }

	/* ---- fruizione JSON ---- */
	@Test public void fruizioneJsonRispostaPassthroughNone() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.NONE); }
	@Test public void fruizioneJsonRispostaPassthroughIdentity() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.IDENTITY); }
	@Test public void fruizioneJsonRispostaPassthroughGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.GZIP); }
	@Test public void fruizioneJsonRispostaPassthroughXGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.X_GZIP); }
	@Test public void fruizioneJsonRispostaPassthroughDeflateZlib() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneJsonRispostaPassthroughDeflateRaw() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.DEFLATE_RAW); }
	@Test public void fruizioneJsonRispostaPassthroughGzipUppercase() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneJsonRispostaPassthroughBr() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.BR); }
	@Test public void fruizioneJsonRispostaPassthroughDeflateGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneJsonRispostaPassthroughGzipInvalid() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.GZIP_INVALID); }
	@Test public void fruizioneJsonRispostaDecompressNone() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.NONE); }
	@Test public void fruizioneJsonRispostaDecompressIdentity() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.IDENTITY); }
	@Test public void fruizioneJsonRispostaDecompressGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.GZIP); }
	@Test public void fruizioneJsonRispostaDecompressXGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.X_GZIP); }
	@Test public void fruizioneJsonRispostaDecompressDeflateZlib() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneJsonRispostaDecompressDeflateRaw() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.DEFLATE_RAW); }
	@Test public void fruizioneJsonRispostaDecompressGzipUppercase() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneJsonRispostaDecompressBr() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.BR); }
	@Test public void fruizioneJsonRispostaDecompressDeflateGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneJsonRispostaDecompressGzipInvalid() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.GZIP_INVALID); }
	@Test public void fruizioneJsonRichiestaPassthroughNone() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.NONE); }
	@Test public void fruizioneJsonRichiestaPassthroughIdentity() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.IDENTITY); }
	@Test public void fruizioneJsonRichiestaPassthroughGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.GZIP); }
	@Test public void fruizioneJsonRichiestaPassthroughXGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.X_GZIP); }
	@Test public void fruizioneJsonRichiestaPassthroughDeflateZlib() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneJsonRichiestaPassthroughDeflateRaw() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.DEFLATE_RAW); }
	@Test public void fruizioneJsonRichiestaPassthroughGzipUppercase() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneJsonRichiestaPassthroughBr() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.BR); }
	@Test public void fruizioneJsonRichiestaPassthroughDeflateGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneJsonRichiestaPassthroughGzipInvalid() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.GZIP_INVALID); }
	@Test public void fruizioneJsonRichiestaDecompressNone() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.NONE); }
	@Test public void fruizioneJsonRichiestaDecompressIdentity() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.IDENTITY); }
	@Test public void fruizioneJsonRichiestaDecompressGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.GZIP); }
	@Test public void fruizioneJsonRichiestaDecompressXGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.X_GZIP); }
	@Test public void fruizioneJsonRichiestaDecompressDeflateZlib() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneJsonRichiestaDecompressDeflateRaw() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.DEFLATE_RAW); }
	@Test public void fruizioneJsonRichiestaDecompressGzipUppercase() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneJsonRichiestaDecompressBr() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.BR); }
	@Test public void fruizioneJsonRichiestaDecompressDeflateGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneJsonRichiestaDecompressGzipInvalid() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.JSON, Enc.GZIP_INVALID); }

	/* ---- fruizione XML ---- */
	@Test public void fruizioneXmlRispostaPassthroughNone() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.NONE); }
	@Test public void fruizioneXmlRispostaPassthroughIdentity() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.IDENTITY); }
	@Test public void fruizioneXmlRispostaPassthroughGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.GZIP); }
	@Test public void fruizioneXmlRispostaPassthroughXGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.X_GZIP); }
	@Test public void fruizioneXmlRispostaPassthroughDeflateZlib() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneXmlRispostaPassthroughDeflateRaw() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.DEFLATE_RAW); }
	@Test public void fruizioneXmlRispostaPassthroughGzipUppercase() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneXmlRispostaPassthroughBr() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.BR); }
	@Test public void fruizioneXmlRispostaPassthroughDeflateGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneXmlRispostaPassthroughGzipInvalid() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.GZIP_INVALID); }
	@Test public void fruizioneXmlRispostaDecompressNone() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.NONE); }
	@Test public void fruizioneXmlRispostaDecompressIdentity() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.IDENTITY); }
	@Test public void fruizioneXmlRispostaDecompressGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.GZIP); }
	@Test public void fruizioneXmlRispostaDecompressXGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.X_GZIP); }
	@Test public void fruizioneXmlRispostaDecompressDeflateZlib() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneXmlRispostaDecompressDeflateRaw() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.DEFLATE_RAW); }
	@Test public void fruizioneXmlRispostaDecompressGzipUppercase() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneXmlRispostaDecompressBr() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.BR); }
	@Test public void fruizioneXmlRispostaDecompressDeflateGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneXmlRispostaDecompressGzipInvalid() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.GZIP_INVALID); }
	@Test public void fruizioneXmlRichiestaPassthroughNone() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.NONE); }
	@Test public void fruizioneXmlRichiestaPassthroughIdentity() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.IDENTITY); }
	@Test public void fruizioneXmlRichiestaPassthroughGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.GZIP); }
	@Test public void fruizioneXmlRichiestaPassthroughXGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.X_GZIP); }
	@Test public void fruizioneXmlRichiestaPassthroughDeflateZlib() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneXmlRichiestaPassthroughDeflateRaw() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.DEFLATE_RAW); }
	@Test public void fruizioneXmlRichiestaPassthroughGzipUppercase() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneXmlRichiestaPassthroughBr() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.BR); }
	@Test public void fruizioneXmlRichiestaPassthroughDeflateGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneXmlRichiestaPassthroughGzipInvalid() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.XML, Enc.GZIP_INVALID); }
	@Test public void fruizioneXmlRichiestaDecompressNone() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.NONE); }
	@Test public void fruizioneXmlRichiestaDecompressIdentity() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.IDENTITY); }
	@Test public void fruizioneXmlRichiestaDecompressGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.GZIP); }
	@Test public void fruizioneXmlRichiestaDecompressXGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.X_GZIP); }
	@Test public void fruizioneXmlRichiestaDecompressDeflateZlib() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneXmlRichiestaDecompressDeflateRaw() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.DEFLATE_RAW); }
	@Test public void fruizioneXmlRichiestaDecompressGzipUppercase() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneXmlRichiestaDecompressBr() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.BR); }
	@Test public void fruizioneXmlRichiestaDecompressDeflateGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneXmlRichiestaDecompressGzipInvalid() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.XML, Enc.GZIP_INVALID); }

	/* ---- fruizione TEXT ---- */
	@Test public void fruizioneTextRispostaPassthroughNone() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.NONE); }
	@Test public void fruizioneTextRispostaPassthroughIdentity() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.IDENTITY); }
	@Test public void fruizioneTextRispostaPassthroughGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.GZIP); }
	@Test public void fruizioneTextRispostaPassthroughXGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.X_GZIP); }
	@Test public void fruizioneTextRispostaPassthroughDeflateZlib() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneTextRispostaPassthroughDeflateRaw() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.DEFLATE_RAW); }
	@Test public void fruizioneTextRispostaPassthroughGzipUppercase() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneTextRispostaPassthroughBr() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.BR); }
	@Test public void fruizioneTextRispostaPassthroughDeflateGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneTextRispostaPassthroughGzipInvalid() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.GZIP_INVALID); }
	@Test public void fruizioneTextRispostaDecompressNone() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.NONE); }
	@Test public void fruizioneTextRispostaDecompressIdentity() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.IDENTITY); }
	@Test public void fruizioneTextRispostaDecompressGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.GZIP); }
	@Test public void fruizioneTextRispostaDecompressXGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.X_GZIP); }
	@Test public void fruizioneTextRispostaDecompressDeflateZlib() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneTextRispostaDecompressDeflateRaw() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.DEFLATE_RAW); }
	@Test public void fruizioneTextRispostaDecompressGzipUppercase() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneTextRispostaDecompressBr() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.BR); }
	@Test public void fruizioneTextRispostaDecompressDeflateGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneTextRispostaDecompressGzipInvalid() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.GZIP_INVALID); }
	@Test public void fruizioneTextRichiestaPassthroughNone() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.NONE); }
	@Test public void fruizioneTextRichiestaPassthroughIdentity() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.IDENTITY); }
	@Test public void fruizioneTextRichiestaPassthroughGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.GZIP); }
	@Test public void fruizioneTextRichiestaPassthroughXGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.X_GZIP); }
	@Test public void fruizioneTextRichiestaPassthroughDeflateZlib() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneTextRichiestaPassthroughDeflateRaw() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.DEFLATE_RAW); }
	@Test public void fruizioneTextRichiestaPassthroughGzipUppercase() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneTextRichiestaPassthroughBr() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.BR); }
	@Test public void fruizioneTextRichiestaPassthroughDeflateGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneTextRichiestaPassthroughGzipInvalid() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.GZIP_INVALID); }
	@Test public void fruizioneTextRichiestaDecompressNone() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.NONE); }
	@Test public void fruizioneTextRichiestaDecompressIdentity() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.IDENTITY); }
	@Test public void fruizioneTextRichiestaDecompressGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.GZIP); }
	@Test public void fruizioneTextRichiestaDecompressXGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.X_GZIP); }
	@Test public void fruizioneTextRichiestaDecompressDeflateZlib() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneTextRichiestaDecompressDeflateRaw() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.DEFLATE_RAW); }
	@Test public void fruizioneTextRichiestaDecompressGzipUppercase() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneTextRichiestaDecompressBr() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.BR); }
	@Test public void fruizioneTextRichiestaDecompressDeflateGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneTextRichiestaDecompressGzipInvalid() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.TEXT, Enc.GZIP_INVALID); }

	/* ---- fruizione BINARY ---- */
	@Test public void fruizioneBinaryRispostaPassthroughNone() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.NONE); }
	@Test public void fruizioneBinaryRispostaPassthroughIdentity() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.IDENTITY); }
	@Test public void fruizioneBinaryRispostaPassthroughGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.GZIP); }
	@Test public void fruizioneBinaryRispostaPassthroughXGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.X_GZIP); }
	@Test public void fruizioneBinaryRispostaPassthroughDeflateZlib() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneBinaryRispostaPassthroughDeflateRaw() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.DEFLATE_RAW); }
	@Test public void fruizioneBinaryRispostaPassthroughGzipUppercase() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneBinaryRispostaPassthroughBr() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.BR); }
	@Test public void fruizioneBinaryRispostaPassthroughDeflateGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneBinaryRispostaPassthroughGzipInvalid() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.GZIP_INVALID); }
	@Test public void fruizioneBinaryRispostaDecompressNone() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.NONE); }
	@Test public void fruizioneBinaryRispostaDecompressIdentity() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.IDENTITY); }
	@Test public void fruizioneBinaryRispostaDecompressGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.GZIP); }
	@Test public void fruizioneBinaryRispostaDecompressXGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.X_GZIP); }
	@Test public void fruizioneBinaryRispostaDecompressDeflateZlib() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneBinaryRispostaDecompressDeflateRaw() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.DEFLATE_RAW); }
	@Test public void fruizioneBinaryRispostaDecompressGzipUppercase() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneBinaryRispostaDecompressBr() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.BR); }
	@Test public void fruizioneBinaryRispostaDecompressDeflateGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneBinaryRispostaDecompressGzipInvalid() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.GZIP_INVALID); }
	@Test public void fruizioneBinaryRichiestaPassthroughNone() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.NONE); }
	@Test public void fruizioneBinaryRichiestaPassthroughIdentity() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.IDENTITY); }
	@Test public void fruizioneBinaryRichiestaPassthroughGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.GZIP); }
	@Test public void fruizioneBinaryRichiestaPassthroughXGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.X_GZIP); }
	@Test public void fruizioneBinaryRichiestaPassthroughDeflateZlib() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneBinaryRichiestaPassthroughDeflateRaw() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.DEFLATE_RAW); }
	@Test public void fruizioneBinaryRichiestaPassthroughGzipUppercase() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneBinaryRichiestaPassthroughBr() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.BR); }
	@Test public void fruizioneBinaryRichiestaPassthroughDeflateGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneBinaryRichiestaPassthroughGzipInvalid() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.GZIP_INVALID); }
	@Test public void fruizioneBinaryRichiestaDecompressNone() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.NONE); }
	@Test public void fruizioneBinaryRichiestaDecompressIdentity() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.IDENTITY); }
	@Test public void fruizioneBinaryRichiestaDecompressGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.GZIP); }
	@Test public void fruizioneBinaryRichiestaDecompressXGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.X_GZIP); }
	@Test public void fruizioneBinaryRichiestaDecompressDeflateZlib() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneBinaryRichiestaDecompressDeflateRaw() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.DEFLATE_RAW); }
	@Test public void fruizioneBinaryRichiestaDecompressGzipUppercase() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneBinaryRichiestaDecompressBr() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.BR); }
	@Test public void fruizioneBinaryRichiestaDecompressDeflateGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneBinaryRichiestaDecompressGzipInvalid() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.BINARY, Enc.GZIP_INVALID); }

	/* ---- fruizione MULTIPART_MIXED ---- */
	@Test public void fruizioneMultipartMixedRispostaPassthroughNone() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.NONE); }
	@Test public void fruizioneMultipartMixedRispostaPassthroughIdentity() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.IDENTITY); }
	@Test public void fruizioneMultipartMixedRispostaPassthroughGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP); }
	@Test public void fruizioneMultipartMixedRispostaPassthroughXGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.X_GZIP); }
	@Test public void fruizioneMultipartMixedRispostaPassthroughDeflateZlib() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneMultipartMixedRispostaPassthroughDeflateRaw() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_RAW); }
	@Test public void fruizioneMultipartMixedRispostaPassthroughGzipUppercase() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneMultipartMixedRispostaPassthroughBr() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.BR); }
	@Test public void fruizioneMultipartMixedRispostaPassthroughDeflateGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneMultipartMixedRispostaPassthroughGzipInvalid() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_INVALID); }
	@Test public void fruizioneMultipartMixedRispostaDecompressNone() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.NONE); }
	@Test public void fruizioneMultipartMixedRispostaDecompressIdentity() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.IDENTITY); }
	@Test public void fruizioneMultipartMixedRispostaDecompressGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP); }
	@Test public void fruizioneMultipartMixedRispostaDecompressXGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.X_GZIP); }
	@Test public void fruizioneMultipartMixedRispostaDecompressDeflateZlib() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneMultipartMixedRispostaDecompressDeflateRaw() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_RAW); }
	@Test public void fruizioneMultipartMixedRispostaDecompressGzipUppercase() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneMultipartMixedRispostaDecompressBr() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.BR); }
	@Test public void fruizioneMultipartMixedRispostaDecompressDeflateGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneMultipartMixedRispostaDecompressGzipInvalid() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_INVALID); }
	@Test public void fruizioneMultipartMixedRichiestaPassthroughNone() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.NONE); }
	@Test public void fruizioneMultipartMixedRichiestaPassthroughIdentity() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.IDENTITY); }
	@Test public void fruizioneMultipartMixedRichiestaPassthroughGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP); }
	@Test public void fruizioneMultipartMixedRichiestaPassthroughXGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.X_GZIP); }
	@Test public void fruizioneMultipartMixedRichiestaPassthroughDeflateZlib() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneMultipartMixedRichiestaPassthroughDeflateRaw() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_RAW); }
	@Test public void fruizioneMultipartMixedRichiestaPassthroughGzipUppercase() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneMultipartMixedRichiestaPassthroughBr() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.BR); }
	@Test public void fruizioneMultipartMixedRichiestaPassthroughDeflateGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneMultipartMixedRichiestaPassthroughGzipInvalid() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_INVALID); }
	@Test public void fruizioneMultipartMixedRichiestaDecompressNone() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.NONE); }
	@Test public void fruizioneMultipartMixedRichiestaDecompressIdentity() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.IDENTITY); }
	@Test public void fruizioneMultipartMixedRichiestaDecompressGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP); }
	@Test public void fruizioneMultipartMixedRichiestaDecompressXGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.X_GZIP); }
	@Test public void fruizioneMultipartMixedRichiestaDecompressDeflateZlib() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneMultipartMixedRichiestaDecompressDeflateRaw() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_RAW); }
	@Test public void fruizioneMultipartMixedRichiestaDecompressGzipUppercase() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneMultipartMixedRichiestaDecompressBr() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.BR); }
	@Test public void fruizioneMultipartMixedRichiestaDecompressDeflateGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneMultipartMixedRichiestaDecompressGzipInvalid() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_MIXED, Enc.GZIP_INVALID); }

	/* ---- fruizione MULTIPART_RELATED ---- */
	@Test public void fruizioneMultipartRelatedRispostaPassthroughNone() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.NONE); }
	@Test public void fruizioneMultipartRelatedRispostaPassthroughIdentity() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.IDENTITY); }
	@Test public void fruizioneMultipartRelatedRispostaPassthroughGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP); }
	@Test public void fruizioneMultipartRelatedRispostaPassthroughXGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.X_GZIP); }
	@Test public void fruizioneMultipartRelatedRispostaPassthroughDeflateZlib() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneMultipartRelatedRispostaPassthroughDeflateRaw() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_RAW); }
	@Test public void fruizioneMultipartRelatedRispostaPassthroughGzipUppercase() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneMultipartRelatedRispostaPassthroughBr() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.BR); }
	@Test public void fruizioneMultipartRelatedRispostaPassthroughDeflateGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneMultipartRelatedRispostaPassthroughGzipInvalid() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_INVALID); }
	@Test public void fruizioneMultipartRelatedRispostaDecompressNone() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.NONE); }
	@Test public void fruizioneMultipartRelatedRispostaDecompressIdentity() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.IDENTITY); }
	@Test public void fruizioneMultipartRelatedRispostaDecompressGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP); }
	@Test public void fruizioneMultipartRelatedRispostaDecompressXGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.X_GZIP); }
	@Test public void fruizioneMultipartRelatedRispostaDecompressDeflateZlib() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneMultipartRelatedRispostaDecompressDeflateRaw() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_RAW); }
	@Test public void fruizioneMultipartRelatedRispostaDecompressGzipUppercase() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneMultipartRelatedRispostaDecompressBr() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.BR); }
	@Test public void fruizioneMultipartRelatedRispostaDecompressDeflateGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneMultipartRelatedRispostaDecompressGzipInvalid() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_INVALID); }
	@Test public void fruizioneMultipartRelatedRichiestaPassthroughNone() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.NONE); }
	@Test public void fruizioneMultipartRelatedRichiestaPassthroughIdentity() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.IDENTITY); }
	@Test public void fruizioneMultipartRelatedRichiestaPassthroughGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP); }
	@Test public void fruizioneMultipartRelatedRichiestaPassthroughXGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.X_GZIP); }
	@Test public void fruizioneMultipartRelatedRichiestaPassthroughDeflateZlib() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneMultipartRelatedRichiestaPassthroughDeflateRaw() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_RAW); }
	@Test public void fruizioneMultipartRelatedRichiestaPassthroughGzipUppercase() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneMultipartRelatedRichiestaPassthroughBr() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.BR); }
	@Test public void fruizioneMultipartRelatedRichiestaPassthroughDeflateGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneMultipartRelatedRichiestaPassthroughGzipInvalid() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_INVALID); }
	@Test public void fruizioneMultipartRelatedRichiestaDecompressNone() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.NONE); }
	@Test public void fruizioneMultipartRelatedRichiestaDecompressIdentity() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.IDENTITY); }
	@Test public void fruizioneMultipartRelatedRichiestaDecompressGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP); }
	@Test public void fruizioneMultipartRelatedRichiestaDecompressXGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.X_GZIP); }
	@Test public void fruizioneMultipartRelatedRichiestaDecompressDeflateZlib() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneMultipartRelatedRichiestaDecompressDeflateRaw() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_RAW); }
	@Test public void fruizioneMultipartRelatedRichiestaDecompressGzipUppercase() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneMultipartRelatedRichiestaDecompressBr() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.BR); }
	@Test public void fruizioneMultipartRelatedRichiestaDecompressDeflateGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneMultipartRelatedRichiestaDecompressGzipInvalid() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_RELATED, Enc.GZIP_INVALID); }

	/* ---- fruizione MULTIPART_FORM_DATA ---- */
	@Test public void fruizioneMultipartFormDataRispostaPassthroughNone() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.NONE); }
	@Test public void fruizioneMultipartFormDataRispostaPassthroughIdentity() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.IDENTITY); }
	@Test public void fruizioneMultipartFormDataRispostaPassthroughGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP); }
	@Test public void fruizioneMultipartFormDataRispostaPassthroughXGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.X_GZIP); }
	@Test public void fruizioneMultipartFormDataRispostaPassthroughDeflateZlib() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneMultipartFormDataRispostaPassthroughDeflateRaw() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneMultipartFormDataRispostaPassthroughGzipUppercase() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneMultipartFormDataRispostaPassthroughBr() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.BR); }
	@Test public void fruizioneMultipartFormDataRispostaPassthroughDeflateGzip() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneMultipartFormDataRispostaPassthroughGzipInvalid() throws Exception { _testRispostaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_INVALID); }
	@Test public void fruizioneMultipartFormDataRispostaDecompressNone() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.NONE); }
	@Test public void fruizioneMultipartFormDataRispostaDecompressIdentity() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.IDENTITY); }
	@Test public void fruizioneMultipartFormDataRispostaDecompressGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP); }
	@Test public void fruizioneMultipartFormDataRispostaDecompressXGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.X_GZIP); }
	@Test public void fruizioneMultipartFormDataRispostaDecompressDeflateZlib() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneMultipartFormDataRispostaDecompressDeflateRaw() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneMultipartFormDataRispostaDecompressGzipUppercase() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneMultipartFormDataRispostaDecompressBr() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.BR); }
	@Test public void fruizioneMultipartFormDataRispostaDecompressDeflateGzip() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneMultipartFormDataRispostaDecompressGzipInvalid() throws Exception { _testRispostaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_INVALID); }
	@Test public void fruizioneMultipartFormDataRichiestaPassthroughNone() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.NONE); }
	@Test public void fruizioneMultipartFormDataRichiestaPassthroughIdentity() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.IDENTITY); }
	@Test public void fruizioneMultipartFormDataRichiestaPassthroughGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP); }
	@Test public void fruizioneMultipartFormDataRichiestaPassthroughXGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.X_GZIP); }
	@Test public void fruizioneMultipartFormDataRichiestaPassthroughDeflateZlib() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneMultipartFormDataRichiestaPassthroughDeflateRaw() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneMultipartFormDataRichiestaPassthroughGzipUppercase() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneMultipartFormDataRichiestaPassthroughBr() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.BR); }
	@Test public void fruizioneMultipartFormDataRichiestaPassthroughDeflateGzip() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneMultipartFormDataRichiestaPassthroughGzipInvalid() throws Exception { _testRichiestaPassthrough(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_INVALID); }
	@Test public void fruizioneMultipartFormDataRichiestaDecompressNone() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.NONE); }
	@Test public void fruizioneMultipartFormDataRichiestaDecompressIdentity() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.IDENTITY); }
	@Test public void fruizioneMultipartFormDataRichiestaDecompressGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP); }
	@Test public void fruizioneMultipartFormDataRichiestaDecompressXGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.X_GZIP); }
	@Test public void fruizioneMultipartFormDataRichiestaDecompressDeflateZlib() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneMultipartFormDataRichiestaDecompressDeflateRaw() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_RAW); }
	@Test public void fruizioneMultipartFormDataRichiestaDecompressGzipUppercase() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneMultipartFormDataRichiestaDecompressBr() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.BR); }
	@Test public void fruizioneMultipartFormDataRichiestaDecompressDeflateGzip() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneMultipartFormDataRichiestaDecompressGzipInvalid() throws Exception { _testRichiestaDecompress(TipoServizio.FRUIZIONE, Tipo.MULTIPART_FORM_DATA, Enc.GZIP_INVALID); }




	/* ====================================================================================== */
	/* ======================================= ENGINE ======================================== */
	/* ====================================================================================== */

	/** Risposta compressa dal backend, risorsa senza decompressione: il client riceve byte e Content-Encoding invariati. */
	private void _testRispostaPassthrough(TipoServizio tipo, Tipo contenuto, Enc enc) throws Exception {
		byte[] plain = contenuto.body();
		HttpResponse response = invokeRisposta(tipo, RES_DEFAULT, contenuto, plain, enc);
		String label = label(tipo, RES_DEFAULT, contenuto, enc) + "[risposta]";
		verifyOk(response, label);
		verifyBodyIdentico(response, label);
		if (enc.isClientDecodable()) {
			assertTrue(label + " body decodificato dal client diverso da quello inviato dal backend",
					Arrays.equals(plain, clientDecode(response.getContent(), enc)));
		}
	}

	/** Risposta compressa dal backend, risorsa con decompressione della risposta abilitata. */
	private void _testRispostaDecompress(TipoServizio tipo, Tipo contenuto, Enc enc) throws Exception {
		byte[] plain = contenuto.body();
		HttpResponse response = invokeRisposta(tipo, RES_RESPONSE, contenuto, plain, enc);
		String label = label(tipo, RES_RESPONSE, contenuto, enc) + "[risposta]";
		String idTransazione = idTransazione(response, label);
		if (enc.isPlain()) {
			verifyOk(response, label);
			assertTrue(label + " body alterato", Arrays.equals(plain, response.getContent()));
			return;
		}
		if (Kind.PARSED.equals(enc.kind)) {
			verifyOk(response, label);
			verifyNoContentEncoding(response, label);
			assertTrue(label + " atteso body in chiaro uguale a quello originale", Arrays.equals(plain, response.getContent()));
			DBVerifier.existsDiagnostico(idTransazione, DIAG_DECOMPRESSED);
			return;
		}
		assertFalse(label + " atteso errore, ricevuto " + response.getResultHTTPOperation(), response.getResultHTTPOperation() == 200);
		if (Kind.UNSUPPORTED.equals(enc.kind)) {
			DBVerifier.existsDiagnostico(idTransazione, DIAG_UNSUPPORTED);
		}
	}

	/** Richiesta compressa dal client, risorsa senza decompressione: il backend riceve byte e Content-Encoding invariati. */
	private void _testRichiestaPassthrough(TipoServizio tipo, Tipo contenuto, Enc enc) throws Exception {
		byte[] plain = contenuto.body();
		byte[] onWire = encodeForRequest(plain, enc);
		HttpResponse response = invokeRichiesta(tipo, RES_DEFAULT, contenuto, onWire, enc);
		String label = label(tipo, RES_DEFAULT, contenuto, enc) + "[richiesta]";
		verifyOk(response, label);
		verifyBackendRicevutoIdentico(response, onWire, enc, label);
	}

	/** Richiesta compressa dal client, risorsa con decompressione della richiesta abilitata. */
	private void _testRichiestaDecompress(TipoServizio tipo, Tipo contenuto, Enc enc) throws Exception {
		byte[] plain = contenuto.body();
		byte[] onWire = encodeForRequest(plain, enc);
		HttpResponse response = invokeRichiesta(tipo, RES_REQUEST, contenuto, onWire, enc);
		String label = label(tipo, RES_REQUEST, contenuto, enc) + "[richiesta]";
		String idTransazione = idTransazione(response, label);
		if (enc.isPlain()) {
			verifyOk(response, label);
			verifyBackendRicevutoIdentico(response, onWire, enc, label);
			return;
		}
		if (Kind.PARSED.equals(enc.kind)) {
			verifyOk(response, label);
			verifyBackendRicevutoInChiaro(response, plain, label);
			DBVerifier.existsDiagnostico(idTransazione, DIAG_DECOMPRESSED);
			return;
		}
		assertFalse(label + " atteso errore, ricevuto " + response.getResultHTTPOperation(), response.getResultHTTPOperation() == 200);
		if (Kind.UNSUPPORTED.equals(enc.kind)) {
			DBVerifier.existsDiagnostico(idTransazione, DIAG_UNSUPPORTED);
		}
	}

	/* ====================================================================================== */
	/* ===================================== Utilities ======================================= */
	/* ====================================================================================== */

	private HttpResponse invokeRisposta(TipoServizio tipo, String resource, Tipo contenuto, byte[] plain, Enc enc) throws Exception {
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		request.setContent("{\"richiesta\":\"risposta compressa\"}".getBytes(StandardCharsets.UTF_8));
		request.setUrl(ContentEncodingTestUtils.buildUrl(tipo, API, resource));
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_BODY_BASE64, Base64.getEncoder().encodeToString(plain));
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_ENCODING, enc.replyEncoding);
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_CONTENT_TYPE, contenuto.contentType());
		if (enc.declaredOverride != null) {
			request.addHeader(ContentEncodingMockServer.HEADER_REPLY_CONTENT_ENCODING, enc.declaredOverride);
		}
		if (this.libraryMode != null) {
			this.libraryMode.patchRequest(request);
		}
		return HttpUtilities.httpInvoke(request);
	}

	private HttpResponse invokeRichiesta(TipoServizio tipo, String resource, Tipo contenuto, byte[] onWire, Enc enc) throws Exception {
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contenuto.contentType());
		request.setContent(onWire);
		request.setUrl(ContentEncodingTestUtils.buildUrl(tipo, API, resource));
		String ce = requestDeclaredEncoding(enc);
		if (!ce.isEmpty()) {
			request.addHeader(HttpConstants.CONTENT_ENCODING, ce);
		}
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_BODY_BASE64, Base64.getEncoder().encodeToString(RISPOSTA_FISSA));
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_ENCODING, ContentEncodingMockServer.REPLY_ENCODING_NONE);
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
		if (this.libraryMode != null) {
			this.libraryMode.patchRequest(request);
		}
		return HttpUtilities.httpInvoke(request);
	}

	private static String label(TipoServizio tipo, String resource, Tipo contenuto, Enc enc) {
		return "[" + tipo + "/" + resource + "/" + contenuto + "/" + enc + "]";
	}

	private static String idTransazione(HttpResponse response, String label) {
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(label + " manca GovWay-Transaction-ID", idTransazione);
		return idTransazione;
	}

	private void verifyOk(HttpResponse response, String label) throws Exception {
		String idTransazione = idTransazione(response, label);
		assertEquals(label + " status code", 200, response.getResultHTTPOperation());
		EsitiProperties esiti = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME);
		DBVerifier.verify(idTransazione, esiti.convertoToCode(EsitoTransazioneName.OK), this.libraryMode);
	}
}
