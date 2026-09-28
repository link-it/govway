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
 * Messaggi SOAP (1.1 e 1.2, normali e SOAP Fault) con Content-Encoding, in richiesta e in risposta.
 *
 * <h2>Comportamento atteso (analisi sez. 13 D1, sez. 14 S4)</h2>
 * <ul>
 *   <li><b>Decompressione disabilitata</b> (risorsa {@code default}): un messaggio SOAP compresso non è
 *       gestibile (il messaggio viene sempre interpretato in costruzione): la transazione fallisce con un
 *       diagnostico esplicito che indica il contenuto compresso e suggerisce di abilitare la
 *       decompressione; stessa gestione per messaggi normali e SOAP Fault. In chiaro: comportamento
 *       invariato.</li>
 *   <li><b>Decompressione abilitata</b> sul gruppo (risorse {@code request} e {@code response}, gruppi
 *       identificati dalla URL) e sull'intera API ({@code TestContentEncodingSoapDecompress}): con encoding
 *       supportato il messaggio viene gestito in chiaro (SOAP Fault compresi); encoding non supportato o
 *       byte non validi: errore (comportamento esistente).</li>
 * </ul>
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapContentEncodingEngine extends ConfigLoader {

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

	private static final String API = "TestContentEncodingSoap";
	/** API con la decompressione abilitata sulla porta di default (non dipende dall'identificazione del gruppo). */
	private static final String API_DECOMPRESS = "TestContentEncodingSoapDecompress";
	private static final String RES_DEFAULT = "default";
	private static final String RES_REQUEST = "request";
	private static final String RES_RESPONSE = "response";

	private static final String DIAG_DECOMPRESSED = "applicata decompressione automatica";
	private static final String DIAG_UNSUPPORTED = "non gestibile dalla decompressione automatica";
	private static final String DIAG_RICEVUTO_SOAP_FAULT = "Ricevuto un SOAPFault in seguito all'invio";

	private static final String NS_TEST = "http://govway.org/testsuite/contentEncoding";

	/* Per il SOAP la configurazione della testsuite non propaga gli header HTTP (black list '*'): un Content-Encoding
	 * 'identity' non arriva quindi né al backend né al client (equivalente all'assenza dell'header). */
	private static final boolean CONTENT_ENCODING_PROPAGATO = false;

	enum Versione {
		SOAP11, SOAP12;
		String contentType() {
			return SOAP11.equals(this) ? "text/xml; charset=UTF-8" : "application/soap+xml; charset=UTF-8; action=\"test\"";
		}
		String envelopeNs() {
			return SOAP11.equals(this) ? "http://schemas.xmlsoap.org/soap/envelope/" : "http://www.w3.org/2003/05/soap-envelope";
		}
	}

	enum Messaggio { NORMALE, FAULT }

	static byte[] envelope(Versione versione, Messaggio messaggio, String valore) {
		String body;
		if (Messaggio.FAULT.equals(messaggio)) {
			if (Versione.SOAP11.equals(versione)) {
				body = "<env:Fault><faultcode>env:Server</faultcode><faultstring>Fault di test compresso " + valore
						+ "</faultstring><faultactor>http://govway.org/testsuite</faultactor></env:Fault>";
			}
			else {
				body = "<env:Fault><env:Code><env:Value>env:Receiver</env:Value></env:Code><env:Reason><env:Text xml:lang=\"it\">Fault di test compresso "
						+ valore + "</env:Text></env:Reason></env:Fault>";
			}
		}
		else {
			body = "<ns:test xmlns:ns=\"" + NS_TEST + "\"><ns:valore>" + valore + "</ns:valore></ns:test>";
		}
		return ("<env:Envelope xmlns:env=\"" + versione.envelopeNs() + "\"><env:Body>" + body + "</env:Body></env:Envelope>")
				.getBytes(StandardCharsets.UTF_8);
	}

	/* ====================================================================================== */
	/* ======================================= @Test ========================================= */
	/* ====================================================================================== */

	/* ---- erogazione SOAP11 NORMALE: risposta ---- */
	@Test public void erogazioneSoap11NormaleRispostaSenzaDecompressioneNone() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.NONE); }
	@Test public void erogazioneSoap11NormaleRispostaSenzaDecompressioneIdentity() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.IDENTITY); }
	@Test public void erogazioneSoap11NormaleRispostaSenzaDecompressioneGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP); }
	@Test public void erogazioneSoap11NormaleRispostaSenzaDecompressioneXGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.X_GZIP); }
	@Test public void erogazioneSoap11NormaleRispostaSenzaDecompressioneDeflateZlib() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap11NormaleRispostaSenzaDecompressioneDeflateRaw() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap11NormaleRispostaSenzaDecompressioneGzipUppercase() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap11NormaleRispostaSenzaDecompressioneBr() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.BR); }
	@Test public void erogazioneSoap11NormaleRispostaSenzaDecompressioneDeflateGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap11NormaleRispostaSenzaDecompressioneGzipInvalid() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP_INVALID); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneNone() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.NONE); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneIdentity() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.IDENTITY); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneXGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.X_GZIP); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneDeflateZlib() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneDeflateRaw() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneGzipUppercase() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneBr() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.BR); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneDeflateGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneGzipInvalid() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP_INVALID); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneApiNone() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.NONE); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneApiIdentity() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.IDENTITY); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneApiGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneApiXGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.X_GZIP); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneApiDeflateZlib() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneApiDeflateRaw() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneApiGzipUppercase() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneApiBr() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.BR); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneApiDeflateGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap11NormaleRispostaConDecompressioneApiGzipInvalid() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP_INVALID); }
	/* ---- erogazione SOAP11 FAULT: risposta ---- */
	@Test public void erogazioneSoap11FaultRispostaSenzaDecompressioneNone() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.NONE); }
	@Test public void erogazioneSoap11FaultRispostaSenzaDecompressioneIdentity() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.IDENTITY); }
	@Test public void erogazioneSoap11FaultRispostaSenzaDecompressioneGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP); }
	@Test public void erogazioneSoap11FaultRispostaSenzaDecompressioneXGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.X_GZIP); }
	@Test public void erogazioneSoap11FaultRispostaSenzaDecompressioneDeflateZlib() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap11FaultRispostaSenzaDecompressioneDeflateRaw() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap11FaultRispostaSenzaDecompressioneGzipUppercase() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap11FaultRispostaSenzaDecompressioneBr() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.BR); }
	@Test public void erogazioneSoap11FaultRispostaSenzaDecompressioneDeflateGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap11FaultRispostaSenzaDecompressioneGzipInvalid() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP_INVALID); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneNone() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.NONE); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneIdentity() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.IDENTITY); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneXGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.X_GZIP); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneDeflateZlib() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneDeflateRaw() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneGzipUppercase() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneBr() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.BR); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneDeflateGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneGzipInvalid() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP_INVALID); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneApiNone() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.NONE); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneApiIdentity() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.IDENTITY); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneApiGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneApiXGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.X_GZIP); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneApiDeflateZlib() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneApiDeflateRaw() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneApiGzipUppercase() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneApiBr() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.BR); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneApiDeflateGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap11FaultRispostaConDecompressioneApiGzipInvalid() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP_INVALID); }
	/* ---- erogazione SOAP11: richiesta ---- */
	@Test public void erogazioneSoap11RichiestaSenzaDecompressioneNone() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.NONE); }
	@Test public void erogazioneSoap11RichiestaSenzaDecompressioneIdentity() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.IDENTITY); }
	@Test public void erogazioneSoap11RichiestaSenzaDecompressioneGzip() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.GZIP); }
	@Test public void erogazioneSoap11RichiestaSenzaDecompressioneXGzip() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.X_GZIP); }
	@Test public void erogazioneSoap11RichiestaSenzaDecompressioneDeflateZlib() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap11RichiestaSenzaDecompressioneDeflateRaw() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap11RichiestaSenzaDecompressioneGzipUppercase() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap11RichiestaSenzaDecompressioneBr() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.BR); }
	@Test public void erogazioneSoap11RichiestaSenzaDecompressioneDeflateGzip() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap11RichiestaSenzaDecompressioneGzipInvalid() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.GZIP_INVALID); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneNone() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.NONE); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneIdentity() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.IDENTITY); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneGzip() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.GZIP); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneXGzip() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.X_GZIP); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneDeflateZlib() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneDeflateRaw() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneGzipUppercase() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneBr() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.BR); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneDeflateGzip() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneGzipInvalid() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.GZIP_INVALID); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneApiNone() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.NONE); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneApiIdentity() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.IDENTITY); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneApiGzip() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.GZIP); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneApiXGzip() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.X_GZIP); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneApiDeflateZlib() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneApiDeflateRaw() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneApiGzipUppercase() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneApiBr() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.BR); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneApiDeflateGzip() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap11RichiestaConDecompressioneApiGzipInvalid() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP11, Enc.GZIP_INVALID); }

	/* ---- erogazione SOAP12 NORMALE: risposta ---- */
	@Test public void erogazioneSoap12NormaleRispostaSenzaDecompressioneNone() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.NONE); }
	@Test public void erogazioneSoap12NormaleRispostaSenzaDecompressioneIdentity() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.IDENTITY); }
	@Test public void erogazioneSoap12NormaleRispostaSenzaDecompressioneGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP); }
	@Test public void erogazioneSoap12NormaleRispostaSenzaDecompressioneXGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.X_GZIP); }
	@Test public void erogazioneSoap12NormaleRispostaSenzaDecompressioneDeflateZlib() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap12NormaleRispostaSenzaDecompressioneDeflateRaw() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap12NormaleRispostaSenzaDecompressioneGzipUppercase() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap12NormaleRispostaSenzaDecompressioneBr() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.BR); }
	@Test public void erogazioneSoap12NormaleRispostaSenzaDecompressioneDeflateGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap12NormaleRispostaSenzaDecompressioneGzipInvalid() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP_INVALID); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneNone() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.NONE); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneIdentity() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.IDENTITY); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneXGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.X_GZIP); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneDeflateZlib() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneDeflateRaw() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneGzipUppercase() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneBr() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.BR); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneDeflateGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneGzipInvalid() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP_INVALID); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneApiNone() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.NONE); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneApiIdentity() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.IDENTITY); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneApiGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneApiXGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.X_GZIP); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneApiDeflateZlib() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneApiDeflateRaw() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneApiGzipUppercase() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneApiBr() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.BR); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneApiDeflateGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap12NormaleRispostaConDecompressioneApiGzipInvalid() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP_INVALID); }
	/* ---- erogazione SOAP12 FAULT: risposta ---- */
	@Test public void erogazioneSoap12FaultRispostaSenzaDecompressioneNone() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.NONE); }
	@Test public void erogazioneSoap12FaultRispostaSenzaDecompressioneIdentity() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.IDENTITY); }
	@Test public void erogazioneSoap12FaultRispostaSenzaDecompressioneGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP); }
	@Test public void erogazioneSoap12FaultRispostaSenzaDecompressioneXGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.X_GZIP); }
	@Test public void erogazioneSoap12FaultRispostaSenzaDecompressioneDeflateZlib() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap12FaultRispostaSenzaDecompressioneDeflateRaw() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap12FaultRispostaSenzaDecompressioneGzipUppercase() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap12FaultRispostaSenzaDecompressioneBr() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.BR); }
	@Test public void erogazioneSoap12FaultRispostaSenzaDecompressioneDeflateGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap12FaultRispostaSenzaDecompressioneGzipInvalid() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP_INVALID); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneNone() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.NONE); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneIdentity() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.IDENTITY); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneXGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.X_GZIP); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneDeflateZlib() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneDeflateRaw() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneGzipUppercase() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneBr() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.BR); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneDeflateGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneGzipInvalid() throws Exception { _testRispostaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP_INVALID); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneApiNone() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.NONE); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneApiIdentity() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.IDENTITY); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneApiGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneApiXGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.X_GZIP); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneApiDeflateZlib() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneApiDeflateRaw() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneApiGzipUppercase() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneApiBr() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.BR); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneApiDeflateGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap12FaultRispostaConDecompressioneApiGzipInvalid() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP_INVALID); }
	/* ---- erogazione SOAP12: richiesta ---- */
	@Test public void erogazioneSoap12RichiestaSenzaDecompressioneNone() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.NONE); }
	@Test public void erogazioneSoap12RichiestaSenzaDecompressioneIdentity() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.IDENTITY); }
	@Test public void erogazioneSoap12RichiestaSenzaDecompressioneGzip() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.GZIP); }
	@Test public void erogazioneSoap12RichiestaSenzaDecompressioneXGzip() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.X_GZIP); }
	@Test public void erogazioneSoap12RichiestaSenzaDecompressioneDeflateZlib() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap12RichiestaSenzaDecompressioneDeflateRaw() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap12RichiestaSenzaDecompressioneGzipUppercase() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap12RichiestaSenzaDecompressioneBr() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.BR); }
	@Test public void erogazioneSoap12RichiestaSenzaDecompressioneDeflateGzip() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap12RichiestaSenzaDecompressioneGzipInvalid() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.GZIP_INVALID); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneNone() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.NONE); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneIdentity() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.IDENTITY); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneGzip() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.GZIP); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneXGzip() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.X_GZIP); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneDeflateZlib() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneDeflateRaw() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneGzipUppercase() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneBr() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.BR); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneDeflateGzip() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneGzipInvalid() throws Exception { _testRichiestaConDecompressione(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.GZIP_INVALID); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneApiNone() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.NONE); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneApiIdentity() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.IDENTITY); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneApiGzip() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.GZIP); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneApiXGzip() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.X_GZIP); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneApiDeflateZlib() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneApiDeflateRaw() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.DEFLATE_RAW); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneApiGzipUppercase() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneApiBr() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.BR); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneApiDeflateGzip() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneSoap12RichiestaConDecompressioneApiGzipInvalid() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.EROGAZIONE, Versione.SOAP12, Enc.GZIP_INVALID); }

	/* ---- fruizione SOAP11 NORMALE: risposta ---- */
	@Test public void fruizioneSoap11NormaleRispostaSenzaDecompressioneNone() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.NONE); }
	@Test public void fruizioneSoap11NormaleRispostaSenzaDecompressioneIdentity() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.IDENTITY); }
	@Test public void fruizioneSoap11NormaleRispostaSenzaDecompressioneGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP); }
	@Test public void fruizioneSoap11NormaleRispostaSenzaDecompressioneXGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.X_GZIP); }
	@Test public void fruizioneSoap11NormaleRispostaSenzaDecompressioneDeflateZlib() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap11NormaleRispostaSenzaDecompressioneDeflateRaw() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap11NormaleRispostaSenzaDecompressioneGzipUppercase() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap11NormaleRispostaSenzaDecompressioneBr() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.BR); }
	@Test public void fruizioneSoap11NormaleRispostaSenzaDecompressioneDeflateGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap11NormaleRispostaSenzaDecompressioneGzipInvalid() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP_INVALID); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneNone() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.NONE); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneIdentity() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.IDENTITY); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneXGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.X_GZIP); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneDeflateZlib() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneDeflateRaw() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneGzipUppercase() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneBr() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.BR); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneDeflateGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneGzipInvalid() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP_INVALID); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneApiNone() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.NONE); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneApiIdentity() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.IDENTITY); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneApiGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneApiXGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.X_GZIP); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneApiDeflateZlib() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneApiDeflateRaw() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneApiGzipUppercase() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneApiBr() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.BR); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneApiDeflateGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap11NormaleRispostaConDecompressioneApiGzipInvalid() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.NORMALE, Enc.GZIP_INVALID); }
	/* ---- fruizione SOAP11 FAULT: risposta ---- */
	@Test public void fruizioneSoap11FaultRispostaSenzaDecompressioneNone() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.NONE); }
	@Test public void fruizioneSoap11FaultRispostaSenzaDecompressioneIdentity() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.IDENTITY); }
	@Test public void fruizioneSoap11FaultRispostaSenzaDecompressioneGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP); }
	@Test public void fruizioneSoap11FaultRispostaSenzaDecompressioneXGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.X_GZIP); }
	@Test public void fruizioneSoap11FaultRispostaSenzaDecompressioneDeflateZlib() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap11FaultRispostaSenzaDecompressioneDeflateRaw() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap11FaultRispostaSenzaDecompressioneGzipUppercase() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap11FaultRispostaSenzaDecompressioneBr() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.BR); }
	@Test public void fruizioneSoap11FaultRispostaSenzaDecompressioneDeflateGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap11FaultRispostaSenzaDecompressioneGzipInvalid() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP_INVALID); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneNone() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.NONE); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneIdentity() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.IDENTITY); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneXGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.X_GZIP); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneDeflateZlib() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneDeflateRaw() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneGzipUppercase() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneBr() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.BR); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneDeflateGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneGzipInvalid() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP_INVALID); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneApiNone() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.NONE); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneApiIdentity() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.IDENTITY); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneApiGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneApiXGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.X_GZIP); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneApiDeflateZlib() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneApiDeflateRaw() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneApiGzipUppercase() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneApiBr() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.BR); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneApiDeflateGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap11FaultRispostaConDecompressioneApiGzipInvalid() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Messaggio.FAULT, Enc.GZIP_INVALID); }
	/* ---- fruizione SOAP11: richiesta ---- */
	@Test public void fruizioneSoap11RichiestaSenzaDecompressioneNone() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.NONE); }
	@Test public void fruizioneSoap11RichiestaSenzaDecompressioneIdentity() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.IDENTITY); }
	@Test public void fruizioneSoap11RichiestaSenzaDecompressioneGzip() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.GZIP); }
	@Test public void fruizioneSoap11RichiestaSenzaDecompressioneXGzip() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.X_GZIP); }
	@Test public void fruizioneSoap11RichiestaSenzaDecompressioneDeflateZlib() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap11RichiestaSenzaDecompressioneDeflateRaw() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap11RichiestaSenzaDecompressioneGzipUppercase() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap11RichiestaSenzaDecompressioneBr() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.BR); }
	@Test public void fruizioneSoap11RichiestaSenzaDecompressioneDeflateGzip() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap11RichiestaSenzaDecompressioneGzipInvalid() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.GZIP_INVALID); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneNone() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.NONE); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneIdentity() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.IDENTITY); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneGzip() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.GZIP); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneXGzip() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.X_GZIP); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneDeflateZlib() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneDeflateRaw() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneGzipUppercase() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneBr() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.BR); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneDeflateGzip() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneGzipInvalid() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.GZIP_INVALID); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneApiNone() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.NONE); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneApiIdentity() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.IDENTITY); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneApiGzip() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.GZIP); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneApiXGzip() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.X_GZIP); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneApiDeflateZlib() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneApiDeflateRaw() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneApiGzipUppercase() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneApiBr() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.BR); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneApiDeflateGzip() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap11RichiestaConDecompressioneApiGzipInvalid() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP11, Enc.GZIP_INVALID); }

	/* ---- fruizione SOAP12 NORMALE: risposta ---- */
	@Test public void fruizioneSoap12NormaleRispostaSenzaDecompressioneNone() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.NONE); }
	@Test public void fruizioneSoap12NormaleRispostaSenzaDecompressioneIdentity() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.IDENTITY); }
	@Test public void fruizioneSoap12NormaleRispostaSenzaDecompressioneGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP); }
	@Test public void fruizioneSoap12NormaleRispostaSenzaDecompressioneXGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.X_GZIP); }
	@Test public void fruizioneSoap12NormaleRispostaSenzaDecompressioneDeflateZlib() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap12NormaleRispostaSenzaDecompressioneDeflateRaw() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap12NormaleRispostaSenzaDecompressioneGzipUppercase() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap12NormaleRispostaSenzaDecompressioneBr() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.BR); }
	@Test public void fruizioneSoap12NormaleRispostaSenzaDecompressioneDeflateGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap12NormaleRispostaSenzaDecompressioneGzipInvalid() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP_INVALID); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneNone() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.NONE); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneIdentity() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.IDENTITY); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneXGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.X_GZIP); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneDeflateZlib() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneDeflateRaw() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneGzipUppercase() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneBr() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.BR); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneDeflateGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneGzipInvalid() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP_INVALID); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneApiNone() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.NONE); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneApiIdentity() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.IDENTITY); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneApiGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneApiXGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.X_GZIP); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneApiDeflateZlib() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneApiDeflateRaw() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneApiGzipUppercase() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneApiBr() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.BR); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneApiDeflateGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap12NormaleRispostaConDecompressioneApiGzipInvalid() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.NORMALE, Enc.GZIP_INVALID); }
	/* ---- fruizione SOAP12 FAULT: risposta ---- */
	@Test public void fruizioneSoap12FaultRispostaSenzaDecompressioneNone() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.NONE); }
	@Test public void fruizioneSoap12FaultRispostaSenzaDecompressioneIdentity() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.IDENTITY); }
	@Test public void fruizioneSoap12FaultRispostaSenzaDecompressioneGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP); }
	@Test public void fruizioneSoap12FaultRispostaSenzaDecompressioneXGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.X_GZIP); }
	@Test public void fruizioneSoap12FaultRispostaSenzaDecompressioneDeflateZlib() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap12FaultRispostaSenzaDecompressioneDeflateRaw() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap12FaultRispostaSenzaDecompressioneGzipUppercase() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap12FaultRispostaSenzaDecompressioneBr() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.BR); }
	@Test public void fruizioneSoap12FaultRispostaSenzaDecompressioneDeflateGzip() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap12FaultRispostaSenzaDecompressioneGzipInvalid() throws Exception { _testRispostaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP_INVALID); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneNone() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.NONE); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneIdentity() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.IDENTITY); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneXGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.X_GZIP); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneDeflateZlib() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneDeflateRaw() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneGzipUppercase() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneBr() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.BR); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneDeflateGzip() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneGzipInvalid() throws Exception { _testRispostaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP_INVALID); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneApiNone() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.NONE); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneApiIdentity() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.IDENTITY); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneApiGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneApiXGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.X_GZIP); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneApiDeflateZlib() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneApiDeflateRaw() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneApiGzipUppercase() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneApiBr() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.BR); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneApiDeflateGzip() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap12FaultRispostaConDecompressioneApiGzipInvalid() throws Exception { _testRispostaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Messaggio.FAULT, Enc.GZIP_INVALID); }
	/* ---- fruizione SOAP12: richiesta ---- */
	@Test public void fruizioneSoap12RichiestaSenzaDecompressioneNone() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.NONE); }
	@Test public void fruizioneSoap12RichiestaSenzaDecompressioneIdentity() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.IDENTITY); }
	@Test public void fruizioneSoap12RichiestaSenzaDecompressioneGzip() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.GZIP); }
	@Test public void fruizioneSoap12RichiestaSenzaDecompressioneXGzip() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.X_GZIP); }
	@Test public void fruizioneSoap12RichiestaSenzaDecompressioneDeflateZlib() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap12RichiestaSenzaDecompressioneDeflateRaw() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap12RichiestaSenzaDecompressioneGzipUppercase() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap12RichiestaSenzaDecompressioneBr() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.BR); }
	@Test public void fruizioneSoap12RichiestaSenzaDecompressioneDeflateGzip() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap12RichiestaSenzaDecompressioneGzipInvalid() throws Exception { _testRichiestaSenzaDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.GZIP_INVALID); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneNone() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.NONE); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneIdentity() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.IDENTITY); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneGzip() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.GZIP); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneXGzip() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.X_GZIP); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneDeflateZlib() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneDeflateRaw() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneGzipUppercase() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneBr() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.BR); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneDeflateGzip() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneGzipInvalid() throws Exception { _testRichiestaConDecompressione(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.GZIP_INVALID); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneApiNone() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.NONE); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneApiIdentity() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.IDENTITY); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneApiGzip() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.GZIP); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneApiXGzip() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.X_GZIP); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneApiDeflateZlib() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneApiDeflateRaw() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.DEFLATE_RAW); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneApiGzipUppercase() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneApiBr() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.BR); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneApiDeflateGzip() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneSoap12RichiestaConDecompressioneApiGzipInvalid() throws Exception { _testRichiestaConDecompressioneApi(TipoServizio.FRUIZIONE, Versione.SOAP12, Enc.GZIP_INVALID); }






	/* ====================================================================================== */
	/* ======================================= ENGINE ======================================== */
	/* ====================================================================================== */

	/** Risposta SOAP compressa dal backend, decompressione disabilitata. */
	private void _testRispostaSenzaDecompressione(TipoServizio tipo, Versione versione, Messaggio messaggio, Enc enc) throws Exception {
		byte[] plain = envelope(versione, messaggio, "risposta àèì");
		int status = Messaggio.FAULT.equals(messaggio) ? 500 : 200;
		HttpResponse response = invokeRisposta(tipo, API, RES_DEFAULT, versione, plain, status, enc);
		String label = label(tipo, RES_DEFAULT, versione, messaggio, enc) + "[risposta]";
		String idTransazione = idTransazione(response, label);
		if (enc.isPlain()) {
			verifyEsito(response, status, messaggio, label);
			verifyBodyIdentico(response, CONTENT_ENCODING_PROPAGATO, label);
			return;
		}
		verifyErrore(response, label);
		verifyDiagnosticoContenutoCompresso(idTransazione, declaredEncoding(enc));
	}

	/** Risposta SOAP compressa dal backend, decompressione della risposta abilitata sul gruppo 'response'. */
	private void _testRispostaConDecompressione(TipoServizio tipo, Versione versione, Messaggio messaggio, Enc enc) throws Exception {
		_testRispostaConDecompressione(tipo, API, RES_RESPONSE, versione, messaggio, enc);
	}
	/** Risposta SOAP compressa dal backend, decompressione abilitata sull'intera API. */
	private void _testRispostaConDecompressioneApi(TipoServizio tipo, Versione versione, Messaggio messaggio, Enc enc) throws Exception {
		_testRispostaConDecompressione(tipo, API_DECOMPRESS, RES_DEFAULT, versione, messaggio, enc);
	}
	private void _testRispostaConDecompressione(TipoServizio tipo, String api, String resource, Versione versione, Messaggio messaggio, Enc enc) throws Exception {
		byte[] plain = envelope(versione, messaggio, "risposta àèì");
		int status = Messaggio.FAULT.equals(messaggio) ? 500 : 200;
		HttpResponse response = invokeRisposta(tipo, api, resource, versione, plain, status, enc);
		String label = label(tipo, api + "/" + resource, versione, messaggio, enc) + "[risposta]";
		String idTransazione = idTransazione(response, label);
		if (enc.isPlain()) {
			verifyEsito(response, status, messaggio, label);
			verifyBodyIdentico(response, CONTENT_ENCODING_PROPAGATO, label);
			return;
		}
		if (Kind.PARSED.equals(enc.kind)) {
			verifyEsito(response, status, messaggio, label);
			verifyNoContentEncoding(response, label);
			assertTrue(label + " atteso messaggio SOAP in chiaro uguale a quello originale; ricevuto: " + new String(response.getContent(), StandardCharsets.UTF_8),
					Arrays.equals(plain, response.getContent()));
			DBVerifier.existsDiagnostico(idTransazione, DIAG_DECOMPRESSED);
			if (Messaggio.FAULT.equals(messaggio)) {
				DBVerifier.existsDiagnostico(idTransazione, DIAG_RICEVUTO_SOAP_FAULT);
			}
			return;
		}
		verifyErrore(response, label);
		if (Kind.UNSUPPORTED.equals(enc.kind)) {
			DBVerifier.existsDiagnostico(idTransazione, DIAG_UNSUPPORTED);
		}
	}

	/** Richiesta SOAP compressa dal client, decompressione disabilitata. */
	private void _testRichiestaSenzaDecompressione(TipoServizio tipo, Versione versione, Enc enc) throws Exception {
		byte[] plain = envelope(versione, Messaggio.NORMALE, "richiesta àèì");
		byte[] onWire = encodeForRequest(plain, enc);
		HttpResponse response = invokeRichiesta(tipo, API, RES_DEFAULT, versione, onWire, enc);
		String label = label(tipo, RES_DEFAULT, versione, Messaggio.NORMALE, enc) + "[richiesta]";
		String idTransazione = idTransazione(response, label);
		if (enc.isPlain()) {
			verifyEsito(response, 200, Messaggio.NORMALE, label);
			verifyBackendRicevutoIdentico(response, onWire, enc, CONTENT_ENCODING_PROPAGATO, label);
			return;
		}
		verifyErroreRichiesta(response, label);
		verifyDiagnosticoContenutoCompresso(idTransazione, requestDeclaredEncoding(enc));
	}

	/**
	 * Richiesta SOAP compressa dal client, decompressione della richiesta abilitata sul gruppo 'request'
	 * (gruppo identificato dalla URL, senza accedere al contenuto).
	 */
	private void _testRichiestaConDecompressione(TipoServizio tipo, Versione versione, Enc enc) throws Exception {
		_testRichiestaConDecompressione(tipo, API, RES_REQUEST, versione, enc);
	}
	/** Richiesta SOAP compressa dal client, decompressione abilitata sull'intera API. */
	private void _testRichiestaConDecompressioneApi(TipoServizio tipo, Versione versione, Enc enc) throws Exception {
		_testRichiestaConDecompressione(tipo, API_DECOMPRESS, RES_DEFAULT, versione, enc);
	}
	private void _testRichiestaConDecompressione(TipoServizio tipo, String api, String resource, Versione versione, Enc enc) throws Exception {
		byte[] plain = envelope(versione, Messaggio.NORMALE, "richiesta àèì");
		byte[] onWire = encodeForRequest(plain, enc);
		HttpResponse response = invokeRichiesta(tipo, api, resource, versione, onWire, enc);
		String label = label(tipo, api + "/" + resource, versione, Messaggio.NORMALE, enc) + "[richiesta]";
		String idTransazione = idTransazione(response, label);
		if (enc.isPlain()) {
			verifyEsito(response, 200, Messaggio.NORMALE, label);
			verifyBackendRicevutoIdentico(response, onWire, enc, CONTENT_ENCODING_PROPAGATO, label);
			return;
		}
		if (Kind.PARSED.equals(enc.kind)) {
			verifyEsito(response, 200, Messaggio.NORMALE, label);
			verifyBackendRicevutoInChiaro(response, plain, label);
			DBVerifier.existsDiagnostico(idTransazione, DIAG_DECOMPRESSED);
			return;
		}
		verifyErroreRichiesta(response, label);
		if (Kind.UNSUPPORTED.equals(enc.kind)) {
			DBVerifier.existsDiagnostico(idTransazione, DIAG_UNSUPPORTED);
		}
	}

	/* ====================================================================================== */
	/* ===================================== Utilities ======================================= */
	/* ====================================================================================== */

	private HttpResponse invokeRisposta(TipoServizio tipo, String api, String resource, Versione versione, byte[] plain, int status, Enc enc) throws Exception {
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(versione.contentType());
		if (Versione.SOAP11.equals(versione)) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"test\"");
		}
		request.setContent(envelope(versione, Messaggio.NORMALE, "richiesta in chiaro"));
		request.setUrl(ContentEncodingTestUtils.buildUrl(tipo, api, resource));
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_BODY_BASE64, Base64.getEncoder().encodeToString(plain));
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_ENCODING, enc.replyEncoding);
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_STATUS, String.valueOf(status));
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_CONTENT_TYPE, versione.contentType());
		if (enc.declaredOverride != null) {
			request.addHeader(ContentEncodingMockServer.HEADER_REPLY_CONTENT_ENCODING, enc.declaredOverride);
		}
		if (this.libraryMode != null) {
			this.libraryMode.patchRequest(request);
		}
		return HttpUtilities.httpInvoke(request);
	}

	private HttpResponse invokeRichiesta(TipoServizio tipo, String api, String resource, Versione versione, byte[] onWire, Enc enc) throws Exception {
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(versione.contentType());
		if (Versione.SOAP11.equals(versione)) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"test\"");
		}
		request.setContent(onWire);
		request.setUrl(ContentEncodingTestUtils.buildUrl(tipo, api, resource));
		String ce = requestDeclaredEncoding(enc);
		if (!ce.isEmpty()) {
			request.addHeader(HttpConstants.CONTENT_ENCODING, ce);
		}
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_BODY_BASE64,
				Base64.getEncoder().encodeToString(envelope(versione, Messaggio.NORMALE, "risposta in chiaro")));
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_ENCODING, ContentEncodingMockServer.REPLY_ENCODING_NONE);
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_CONTENT_TYPE, versione.contentType());
		if (this.libraryMode != null) {
			this.libraryMode.patchRequest(request);
		}
		return HttpUtilities.httpInvoke(request);
	}

	private static String label(TipoServizio tipo, String resource, Versione versione, Messaggio messaggio, Enc enc) {
		return "[" + tipo + "/" + resource + "/" + versione + "/" + messaggio + "/" + enc + "]";
	}

	private static String idTransazione(HttpResponse response, String label) {
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(label + " manca GovWay-Transaction-ID", idTransazione);
		return idTransazione;
	}

	private void verifyEsito(HttpResponse response, int status, Messaggio messaggio, String label) throws Exception {
		String idTransazione = idTransazione(response, label);
		assertEquals(label + " status code", status, response.getResultHTTPOperation());
		EsitiProperties esiti = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME);
		EsitoTransazioneName esito = Messaggio.FAULT.equals(messaggio) ? EsitoTransazioneName.ERRORE_APPLICATIVO : EsitoTransazioneName.OK;
		DBVerifier.verify(idTransazione, esiti.convertoToCode(esito), this.libraryMode);
	}

	/**
	 * Errore generato da GovWay sulla risposta: il client non deve ricevere il messaggio del backend
	 * (né compresso né alterato) ma un errore del gateway.
	 */
	private static void verifyErrore(HttpResponse response, String label) {
		assertFalse(label + " atteso errore del gateway, ricevuto 200", response.getResultHTTPOperation() == 200);
		String sentSha = response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_SENT_BODY_SHA256);
		byte[] body = response.getContent() != null ? response.getContent() : new byte[0];
		assertFalse(label + " il client ha ricevuto il messaggio del backend invece di un errore del gateway",
				sentSha != null && sentSha.equals(ContentEncodingMockServer.sha256Hex(body)));
	}

	/** Errore generato da GovWay sulla richiesta. */
	private static void verifyErroreRichiesta(HttpResponse response, String label) {
		assertFalse(label + " atteso errore del gateway, ricevuto 200", response.getResultHTTPOperation() == 200);
	}
}
