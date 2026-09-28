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
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.declaredEncoding;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.encodeForRequest;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.requestDeclaredEncoding;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.toHex;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.verifyBodyIdentico;
import static org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate.ContentEncodingTestUtils.verifyNoContentEncoding;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * Verifica della gestione delle risposte di errore Problem Details (RFC 7807) restituite dal backend
 * con un {@code Content-Encoding}.
 *
 * <h2>Problema verificato</h2>
 * La gestione dell'errore del connettore interpreta il Problem Details per applicare le regole di
 * consegna. Con il body compresso:
 * <ul>
 *   <li>la risposta inoltrata al client non deve essere alterata: deve arrivare byte per byte come
 *       inviata dal backend, con il proprio {@code Content-Encoding};</li>
 *   <li>per gli encoding supportati (gzip, x-gzip, deflate) il Problem Details deve essere
 *       interpretato (diagnostico 'Ricevuto un Problem Detail');</li>
 *   <li>per gli encoding non supportati, per una decompressione non riuscita o per una dimensione
 *       decompressa oltre la soglia, il Problem Details non viene interpretato e viene emesso un
 *       diagnostico dedicato (livello errorIntegration, classificato come warning: compare nel
 *       'warning_log' della transazione), senza alterare la risposta. L'esito resta 'Fault Applicativo',
 *       come per ogni risposta Problem Details (anche 2xx).</li>
 * </ul>
 * Vengono inoltre verificati il caso con decompressione della risposta abilitata e il secondo punto
 * di interpretazione del problem, successivo a una trasformazione della risposta che cambia il tipo
 * di messaggio: la risposta trasformata non deve ereditare il {@code Content-Encoding} del backend.
 *
 * <h2>Mock backend</h2>
 * {@link ContentEncodingMockServer} restituisce come body il contenuto ricevuto, codificato secondo
 * l'header {@code govway-testsuite-reply-encoding}; status e Content-Type sono pilotati dagli header
 * {@code govway-testsuite-reply-status} e {@code govway-testsuite-reply-content-type}, mentre
 * {@code govway-testsuite-reply-content-encoding} consente di dichiarare un encoding diverso da quello
 * realmente applicato (es. byte gzip dichiarati 'br').
 *
 * <h2>Risorse dell'API {@code TestContentEncoding}</h2>
 * <ul>
 *   <li>{@code default}: decompressione disabilitata (configurazione di default, caso segnalato);</li>
 *   <li>{@code response}: {@code connettori.contentEncoding.response.decompress=true};</li>
 *   <li>{@code trasformazioneProblemXml}: trasformazione della risposta in un Problem Details XML;</li>
 *   <li>{@code trasformazioneProblemJson}: trasformazione della risposta in un Problem Details JSON;</li>
 *   <li>{@code trasformazioneContenutoJson}: riscrittura della risposta con un template JSON (stesso tipo);</li>
 *   <li>{@code trasformazioneSoloHeader}: aggiunta di un header su richiesta e risposta, senza riscrivere il contenuto;</li>
 *   <li>{@code trasformazioneRichiesta}: riscrittura della richiesta con un template JSON.</li>
 * </ul>
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProblemContentEncodingEngine extends ConfigLoader {

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

	/* ---------------------------- Modalità libreria HTTP ---------------------------- */

	private HttpLibraryMode libraryMode = null;
	protected void setHttpLibraryMode(HttpLibraryMode mode) {
		this.libraryMode = mode;
	}

	/* ---------------------------- Costanti ---------------------------- */

	private static final String API = "TestContentEncoding";
	private static final String RES_DEFAULT = "default";
	private static final String RES_RESPONSE = "response";
	private static final String RES_TRASFORMAZIONE_PROBLEM_XML = "trasformazioneProblemXml";
	private static final String RES_TRASFORMAZIONE_PROBLEM_JSON = "trasformazioneProblemJson";
	private static final String RES_TRASFORMAZIONE_CONTENUTO_JSON = "trasformazioneContenutoJson";
	private static final String RES_TRASFORMAZIONE_SOLO_HEADER = "trasformazioneSoloHeader";
	private static final String RES_TRASFORMAZIONE_RICHIESTA = "trasformazioneRichiesta";

	/* Contenuti prodotti dalle trasformazioni configurate sulle risorse trasformazioneContenutoJson e trasformazioneRichiesta */
	private static final String TEMPLATE_RISPOSTA_JSON = "{\"trasformato\":\"contenuto-json\",\"origine\":\"trasformazione della risposta\"}";
	private static final String TEMPLATE_RICHIESTA_JSON = "{\"trasformato\":\"richiesta-json\",\"origine\":\"trasformazione della richiesta\"}";
	/* Header aggiunti dalla trasformazione della risorsa trasformazioneSoloHeader */
	private static final String HEADER_TRASFORMAZIONE_RISPOSTA = "govway-testsuite-trasformazione-risposta";
	private static final String HEADER_TRASFORMAZIONE_RICHIESTA = ContentEncodingMockServer.HEADER_ECHO_PREFIX + "trasformazione-richiesta";
	private static final String VALORE_HEADER_TRASFORMAZIONE = "applicata";

	/** Soglia della dimensione decompressa oltre la quale il Problem Details non viene interpretato (default 1024 KB). */
	private static final int SOGLIA_KB = 1024;
	private static final int SOGLIA_BYTES = SOGLIA_KB * 1024;
	private static final int MARGINE_SOGLIA_BYTES = 64 * 1024;

	/* Diagnostici esistenti */
	private static final String DIAG_RICEVUTO_PROBLEM = "Ricevuto un Problem Detail (RFC 7807) in seguito all'invio";
	/* Diagnostico di consegna della risposta: 'Risposta (<contenuto>) consegnata al mittente con codice di trasporto: <status>' (erogazione)
	 * e 'Risposta applicativa (<contenuto>) consegnata al servizio applicativo con codice di trasporto: <status>' (fruizione). */
	private static final String DIAG_RISPOSTA = "Risposta";
	private static final String DIAG_RISPOSTA_CONSEGNATA = ") consegnata al ";
	private static final String DIAG_RISPOSTA_CODICE_TRASPORTO = "con codice di trasporto: ";
	/* Contenuto riportato nel diagnostico di consegna al posto di un problem compresso non interpretabile. */
	private static final String DIAG_RISPOSTA_CONTENUTO_COMPRESSO = "(contenuto compresso, Content-Encoding: ";
	private static final String DIAG_DECOMPRESSED = "applicata decompressione automatica";
	private static final String DIAG_UNSUPPORTED_DECOMPRESS = "non gestibile dalla decompressione automatica";

	/* Nuovo diagnostico (vedi analisi, implementazione I3): il testo deve restare allineato a
	 * govway.msgDiagnostici.properties. */
	private static final String DIAG_PROBLEM_NON_INTERPRETATO_PREFIX = "Problem Detail (RFC 7807) ricevuto con 'Content-Encoding: ";
	private static final String DIAG_PROBLEM_NON_INTERPRETATO = "' non interpretato: ";
	private static final String DIAG_MOTIVO_ENCODING_NON_SUPPORTATO = "encoding non supportato";
	private static final String DIAG_MOTIVO_DECOMPRESSIONE_FALLITA = "decompressione non riuscita";
	private static final String DIAG_MOTIVO_SOGLIA = "superiore alla soglia di " + SOGLIA_KB + " KB";
	/** Severità 'errorIntegration' (LogLevels.SEVERITA_ERROR_INTEGRATION): con esito altrimenti OK produce 'OK con anomalie'. */
	private static final int SEVERITA_ERROR_INTEGRATION = 2;
	/** Codici del nuovo diagnostico, classificati come warning (MsgDiagnosticiProperties.MSG_DIAGNOSTICI_WARNING). */
	private static final String CODICE_DIAG_NON_INTERPRETATO_EROGAZIONE = "007083"; // consegnaContenutiApplicativi
	private static final String CODICE_DIAG_NON_INTERPRETATO_FRUIZIONE = "003074"; // inoltroBuste

	/* Problem Details prodotti dalle trasformazioni configurate sulle risorse trasformazioneProblem* */
	private static final String TYPE_TRASFORMATO_XML = "https://govway.org/testsuite/problem/trasformato-xml";
	private static final String TYPE_TRASFORMATO_JSON = "https://govway.org/testsuite/problem/trasformato-json";

	/* ---------------------------- Enum ---------------------------- */

	private enum Format {
		JSON(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807, HttpConstants.CONTENT_TYPE_JSON),
		XML(HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807, HttpConstants.CONTENT_TYPE_XML);
		private final String problemContentType;
		private final String requestContentType;
		Format(String problemContentType, String requestContentType) {
			this.problemContentType = problemContentType;
			this.requestContentType = requestContentType;
		}
	}

	/* ====================================================================================== */
	/* ======================================= @Test ========================================= */
	/* ====================================================================================== */

	/* ---- erogazione: risorsa default (decompressione disabilitata), 403 ---- */
	@Test public void erogazioneProblemJson403None() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.NONE); }
	@Test public void erogazioneProblemJson403Identity() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.IDENTITY); }
	@Test public void erogazioneProblemJson403Gzip() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.GZIP); }
	@Test public void erogazioneProblemJson403XGzip() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.X_GZIP); }
	@Test public void erogazioneProblemJson403DeflateZlib() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneProblemJson403DeflateRaw() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.DEFLATE_RAW); }
	@Test public void erogazioneProblemJson403GzipUppercase() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneProblemJson403Br() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.BR); }
	@Test public void erogazioneProblemJson403Zstd() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.ZSTD); }
	@Test public void erogazioneProblemJson403Compress() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.COMPRESS); }
	@Test public void erogazioneProblemJson403XCompress() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.X_COMPRESS); }
	@Test public void erogazioneProblemJson403DeflateGzip() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneProblemJson403Unknown() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.UNKNOWN); }
	@Test public void erogazioneProblemJson403GzipInvalid() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.GZIP_INVALID); }
	@Test public void erogazioneProblemJson403GzipTruncated() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 403, Enc.GZIP_TRUNCATED); }
	@Test public void erogazioneProblemXml403None() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.NONE); }
	@Test public void erogazioneProblemXml403Identity() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.IDENTITY); }
	@Test public void erogazioneProblemXml403Gzip() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.GZIP); }
	@Test public void erogazioneProblemXml403XGzip() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.X_GZIP); }
	@Test public void erogazioneProblemXml403DeflateZlib() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneProblemXml403DeflateRaw() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.DEFLATE_RAW); }
	@Test public void erogazioneProblemXml403GzipUppercase() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.GZIP_UPPERCASE); }
	@Test public void erogazioneProblemXml403Br() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.BR); }
	@Test public void erogazioneProblemXml403Zstd() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.ZSTD); }
	@Test public void erogazioneProblemXml403Compress() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.COMPRESS); }
	@Test public void erogazioneProblemXml403XCompress() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.X_COMPRESS); }
	@Test public void erogazioneProblemXml403DeflateGzip() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneProblemXml403Unknown() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.UNKNOWN); }
	@Test public void erogazioneProblemXml403GzipInvalid() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.GZIP_INVALID); }
	@Test public void erogazioneProblemXml403GzipTruncated() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 403, Enc.GZIP_TRUNCATED); }
	/* ---- erogazione: risorsa default, 500 ---- */
	@Test public void erogazioneProblemJson500None() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 500, Enc.NONE); }
	@Test public void erogazioneProblemJson500Gzip() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 500, Enc.GZIP); }
	@Test public void erogazioneProblemJson500DeflateRaw() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 500, Enc.DEFLATE_RAW); }
	@Test public void erogazioneProblemJson500Br() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 500, Enc.BR); }
	@Test public void erogazioneProblemJson500GzipInvalid() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 500, Enc.GZIP_INVALID); }
	@Test public void erogazioneProblemXml500None() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 500, Enc.NONE); }
	@Test public void erogazioneProblemXml500Gzip() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 500, Enc.GZIP); }
	@Test public void erogazioneProblemXml500DeflateRaw() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 500, Enc.DEFLATE_RAW); }
	@Test public void erogazioneProblemXml500Br() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 500, Enc.BR); }
	@Test public void erogazioneProblemXml500GzipInvalid() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 500, Enc.GZIP_INVALID); }
	/* ---- erogazione: risorsa default, 200 con Problem Details (esito 'Fault Applicativo') ---- */
	@Test public void erogazioneProblemJson200None() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 200, Enc.NONE); }
	@Test public void erogazioneProblemJson200Gzip() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 200, Enc.GZIP); }
	@Test public void erogazioneProblemJson200Br() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 200, Enc.BR); }
	@Test public void erogazioneProblemJson200GzipInvalid() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.JSON, 200, Enc.GZIP_INVALID); }
	@Test public void erogazioneProblemXml200None() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 200, Enc.NONE); }
	@Test public void erogazioneProblemXml200Gzip() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 200, Enc.GZIP); }
	@Test public void erogazioneProblemXml200Br() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 200, Enc.BR); }
	@Test public void erogazioneProblemXml200GzipInvalid() throws Exception { _testDefault(TipoServizio.EROGAZIONE, Format.XML, 200, Enc.GZIP_INVALID); }
	/* ---- erogazione: soglia sulla dimensione decompressa ---- */
	@Test public void erogazioneProblemJsonGzipSottoSoglia() throws Exception { _testSoglia(TipoServizio.EROGAZIONE, Format.JSON, false); }
	@Test public void erogazioneProblemJsonGzipSopraSoglia() throws Exception { _testSoglia(TipoServizio.EROGAZIONE, Format.JSON, true); }
	@Test public void erogazioneProblemXmlGzipSottoSoglia() throws Exception { _testSoglia(TipoServizio.EROGAZIONE, Format.XML, false); }
	@Test public void erogazioneProblemXmlGzipSopraSoglia() throws Exception { _testSoglia(TipoServizio.EROGAZIONE, Format.XML, true); }
	/* ---- erogazione: controllo, risposta di errore non Problem Details ---- */
	@Test public void erogazioneJson403NonProblemNone() throws Exception { _testNonProblem(TipoServizio.EROGAZIONE, Enc.NONE); }
	@Test public void erogazioneJson403NonProblemGzip() throws Exception { _testNonProblem(TipoServizio.EROGAZIONE, Enc.GZIP); }
	@Test public void erogazioneJson403NonProblemBr() throws Exception { _testNonProblem(TipoServizio.EROGAZIONE, Enc.BR); }
	/* ---- erogazione: risorsa response (decompressione abilitata) ---- */
	@Test public void erogazioneProblemJson403DecompressGzip() throws Exception { _testDecompressAbilitata(TipoServizio.EROGAZIONE, Format.JSON, Enc.GZIP); }
	@Test public void erogazioneProblemJson403DecompressXGzip() throws Exception { _testDecompressAbilitata(TipoServizio.EROGAZIONE, Format.JSON, Enc.X_GZIP); }
	@Test public void erogazioneProblemJson403DecompressDeflateZlib() throws Exception { _testDecompressAbilitata(TipoServizio.EROGAZIONE, Format.JSON, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneProblemJson403DecompressDeflateRaw() throws Exception { _testDecompressAbilitata(TipoServizio.EROGAZIONE, Format.JSON, Enc.DEFLATE_RAW); }
	@Test public void erogazioneProblemJson403DecompressBr() throws Exception { _testDecompressAbilitataNonSupportato(TipoServizio.EROGAZIONE, Format.JSON, Enc.BR); }
	@Test public void erogazioneProblemJson403DecompressDeflateGzip() throws Exception { _testDecompressAbilitataNonSupportato(TipoServizio.EROGAZIONE, Format.JSON, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneProblemJson403DecompressGzipSopraSoglia() throws Exception { _testDecompressAbilitataSopraSoglia(TipoServizio.EROGAZIONE, Format.JSON); }
	@Test public void erogazioneProblemXml403DecompressGzip() throws Exception { _testDecompressAbilitata(TipoServizio.EROGAZIONE, Format.XML, Enc.GZIP); }
	@Test public void erogazioneProblemXml403DecompressXGzip() throws Exception { _testDecompressAbilitata(TipoServizio.EROGAZIONE, Format.XML, Enc.X_GZIP); }
	@Test public void erogazioneProblemXml403DecompressDeflateZlib() throws Exception { _testDecompressAbilitata(TipoServizio.EROGAZIONE, Format.XML, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneProblemXml403DecompressDeflateRaw() throws Exception { _testDecompressAbilitata(TipoServizio.EROGAZIONE, Format.XML, Enc.DEFLATE_RAW); }
	@Test public void erogazioneProblemXml403DecompressBr() throws Exception { _testDecompressAbilitataNonSupportato(TipoServizio.EROGAZIONE, Format.XML, Enc.BR); }
	@Test public void erogazioneProblemXml403DecompressDeflateGzip() throws Exception { _testDecompressAbilitataNonSupportato(TipoServizio.EROGAZIONE, Format.XML, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneProblemXml403DecompressGzipSopraSoglia() throws Exception { _testDecompressAbilitataSopraSoglia(TipoServizio.EROGAZIONE, Format.XML); }
	/* ---- erogazione: trasformazione del contenuto della risposta (stesso tipo, JSON -> JSON) ---- */
	@Test public void erogazioneTrasformazioneContenutoRispostaNone() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.EROGAZIONE, Enc.NONE); }
	@Test public void erogazioneTrasformazioneContenutoRispostaGzip() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.EROGAZIONE, Enc.GZIP); }
	@Test public void erogazioneTrasformazioneContenutoRispostaXGzip() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.EROGAZIONE, Enc.X_GZIP); }
	@Test public void erogazioneTrasformazioneContenutoRispostaDeflateZlib() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.EROGAZIONE, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneTrasformazioneContenutoRispostaDeflateRaw() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.EROGAZIONE, Enc.DEFLATE_RAW); }
	@Test public void erogazioneTrasformazioneContenutoRispostaBr() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.EROGAZIONE, Enc.BR); }
	@Test public void erogazioneTrasformazioneContenutoRispostaDeflateGzip() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.EROGAZIONE, Enc.DEFLATE_GZIP); }
	@Test public void erogazioneTrasformazioneContenutoRispostaGzipInvalid() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.EROGAZIONE, Enc.GZIP_INVALID); }
	/* ---- erogazione: trasformazione dei soli header (il Content-Encoding deve restare) ---- */
	@Test public void erogazioneTrasformazioneSoloHeaderRispostaNone() throws Exception { _testTrasformazioneSoloHeaderRisposta(TipoServizio.EROGAZIONE, Enc.NONE); }
	@Test public void erogazioneTrasformazioneSoloHeaderRispostaGzip() throws Exception { _testTrasformazioneSoloHeaderRisposta(TipoServizio.EROGAZIONE, Enc.GZIP); }
	@Test public void erogazioneTrasformazioneSoloHeaderRispostaDeflateRaw() throws Exception { _testTrasformazioneSoloHeaderRisposta(TipoServizio.EROGAZIONE, Enc.DEFLATE_RAW); }
	@Test public void erogazioneTrasformazioneSoloHeaderRispostaBr() throws Exception { _testTrasformazioneSoloHeaderRisposta(TipoServizio.EROGAZIONE, Enc.BR); }
	@Test public void erogazioneTrasformazioneSoloHeaderRichiestaGzip() throws Exception { _testTrasformazioneSoloHeaderRichiesta(TipoServizio.EROGAZIONE, Enc.GZIP); }
	@Test public void erogazioneTrasformazioneSoloHeaderRichiestaDeflateRaw() throws Exception { _testTrasformazioneSoloHeaderRichiesta(TipoServizio.EROGAZIONE, Enc.DEFLATE_RAW); }
	@Test public void erogazioneTrasformazioneSoloHeaderRichiestaBr() throws Exception { _testTrasformazioneSoloHeaderRichiesta(TipoServizio.EROGAZIONE, Enc.BR); }
	/* ---- erogazione: trasformazione del contenuto della richiesta ---- */
	@Test public void erogazioneTrasformazioneRichiestaNone() throws Exception { _testTrasformazioneRichiesta(TipoServizio.EROGAZIONE, Enc.NONE); }
	@Test public void erogazioneTrasformazioneRichiestaGzip() throws Exception { _testTrasformazioneRichiesta(TipoServizio.EROGAZIONE, Enc.GZIP); }
	@Test public void erogazioneTrasformazioneRichiestaXGzip() throws Exception { _testTrasformazioneRichiesta(TipoServizio.EROGAZIONE, Enc.X_GZIP); }
	@Test public void erogazioneTrasformazioneRichiestaDeflateZlib() throws Exception { _testTrasformazioneRichiesta(TipoServizio.EROGAZIONE, Enc.DEFLATE_ZLIB); }
	@Test public void erogazioneTrasformazioneRichiestaDeflateRaw() throws Exception { _testTrasformazioneRichiesta(TipoServizio.EROGAZIONE, Enc.DEFLATE_RAW); }
	@Test public void erogazioneTrasformazioneRichiestaBr() throws Exception { _testTrasformazioneRichiesta(TipoServizio.EROGAZIONE, Enc.BR); }
	@Test public void erogazioneTrasformazioneRichiestaGzipInvalid() throws Exception { _testTrasformazioneRichiesta(TipoServizio.EROGAZIONE, Enc.GZIP_INVALID); }
	/* ---- erogazione: trasformazione della risposta che cambia il tipo di messaggio ---- */
	@Test public void erogazioneProblemJson403TrasformazioneNone() throws Exception { _testTrasformazione(TipoServizio.EROGAZIONE, Format.JSON, Enc.NONE); }
	@Test public void erogazioneProblemJson403TrasformazioneGzip() throws Exception { _testTrasformazione(TipoServizio.EROGAZIONE, Format.JSON, Enc.GZIP); }
	@Test public void erogazioneProblemJson403TrasformazioneDeflateRaw() throws Exception { _testTrasformazione(TipoServizio.EROGAZIONE, Format.JSON, Enc.DEFLATE_RAW); }
	@Test public void erogazioneProblemJson403TrasformazioneBr() throws Exception { _testTrasformazione(TipoServizio.EROGAZIONE, Format.JSON, Enc.BR); }
	@Test public void erogazioneProblemJson403TrasformazioneGzipInvalid() throws Exception { _testTrasformazione(TipoServizio.EROGAZIONE, Format.JSON, Enc.GZIP_INVALID); }
	@Test public void erogazioneProblemXml403TrasformazioneNone() throws Exception { _testTrasformazione(TipoServizio.EROGAZIONE, Format.XML, Enc.NONE); }
	@Test public void erogazioneProblemXml403TrasformazioneGzip() throws Exception { _testTrasformazione(TipoServizio.EROGAZIONE, Format.XML, Enc.GZIP); }
	@Test public void erogazioneProblemXml403TrasformazioneDeflateRaw() throws Exception { _testTrasformazione(TipoServizio.EROGAZIONE, Format.XML, Enc.DEFLATE_RAW); }
	@Test public void erogazioneProblemXml403TrasformazioneBr() throws Exception { _testTrasformazione(TipoServizio.EROGAZIONE, Format.XML, Enc.BR); }
	@Test public void erogazioneProblemXml403TrasformazioneGzipInvalid() throws Exception { _testTrasformazione(TipoServizio.EROGAZIONE, Format.XML, Enc.GZIP_INVALID); }

	/* ---- fruizione: risorsa default (decompressione disabilitata), 403 ---- */
	@Test public void fruizioneProblemJson403None() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.NONE); }
	@Test public void fruizioneProblemJson403Identity() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.IDENTITY); }
	@Test public void fruizioneProblemJson403Gzip() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.GZIP); }
	@Test public void fruizioneProblemJson403XGzip() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.X_GZIP); }
	@Test public void fruizioneProblemJson403DeflateZlib() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneProblemJson403DeflateRaw() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.DEFLATE_RAW); }
	@Test public void fruizioneProblemJson403GzipUppercase() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneProblemJson403Br() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.BR); }
	@Test public void fruizioneProblemJson403Zstd() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.ZSTD); }
	@Test public void fruizioneProblemJson403Compress() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.COMPRESS); }
	@Test public void fruizioneProblemJson403XCompress() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.X_COMPRESS); }
	@Test public void fruizioneProblemJson403DeflateGzip() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneProblemJson403Unknown() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.UNKNOWN); }
	@Test public void fruizioneProblemJson403GzipInvalid() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.GZIP_INVALID); }
	@Test public void fruizioneProblemJson403GzipTruncated() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 403, Enc.GZIP_TRUNCATED); }
	@Test public void fruizioneProblemXml403None() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.NONE); }
	@Test public void fruizioneProblemXml403Identity() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.IDENTITY); }
	@Test public void fruizioneProblemXml403Gzip() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.GZIP); }
	@Test public void fruizioneProblemXml403XGzip() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.X_GZIP); }
	@Test public void fruizioneProblemXml403DeflateZlib() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneProblemXml403DeflateRaw() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.DEFLATE_RAW); }
	@Test public void fruizioneProblemXml403GzipUppercase() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.GZIP_UPPERCASE); }
	@Test public void fruizioneProblemXml403Br() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.BR); }
	@Test public void fruizioneProblemXml403Zstd() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.ZSTD); }
	@Test public void fruizioneProblemXml403Compress() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.COMPRESS); }
	@Test public void fruizioneProblemXml403XCompress() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.X_COMPRESS); }
	@Test public void fruizioneProblemXml403DeflateGzip() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneProblemXml403Unknown() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.UNKNOWN); }
	@Test public void fruizioneProblemXml403GzipInvalid() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.GZIP_INVALID); }
	@Test public void fruizioneProblemXml403GzipTruncated() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 403, Enc.GZIP_TRUNCATED); }
	/* ---- fruizione: risorsa default, 500 ---- */
	@Test public void fruizioneProblemJson500None() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 500, Enc.NONE); }
	@Test public void fruizioneProblemJson500Gzip() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 500, Enc.GZIP); }
	@Test public void fruizioneProblemJson500DeflateRaw() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 500, Enc.DEFLATE_RAW); }
	@Test public void fruizioneProblemJson500Br() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 500, Enc.BR); }
	@Test public void fruizioneProblemJson500GzipInvalid() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 500, Enc.GZIP_INVALID); }
	@Test public void fruizioneProblemXml500None() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 500, Enc.NONE); }
	@Test public void fruizioneProblemXml500Gzip() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 500, Enc.GZIP); }
	@Test public void fruizioneProblemXml500DeflateRaw() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 500, Enc.DEFLATE_RAW); }
	@Test public void fruizioneProblemXml500Br() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 500, Enc.BR); }
	@Test public void fruizioneProblemXml500GzipInvalid() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 500, Enc.GZIP_INVALID); }
	/* ---- fruizione: risorsa default, 200 con Problem Details (esito 'Fault Applicativo') ---- */
	@Test public void fruizioneProblemJson200None() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 200, Enc.NONE); }
	@Test public void fruizioneProblemJson200Gzip() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 200, Enc.GZIP); }
	@Test public void fruizioneProblemJson200Br() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 200, Enc.BR); }
	@Test public void fruizioneProblemJson200GzipInvalid() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.JSON, 200, Enc.GZIP_INVALID); }
	@Test public void fruizioneProblemXml200None() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 200, Enc.NONE); }
	@Test public void fruizioneProblemXml200Gzip() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 200, Enc.GZIP); }
	@Test public void fruizioneProblemXml200Br() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 200, Enc.BR); }
	@Test public void fruizioneProblemXml200GzipInvalid() throws Exception { _testDefault(TipoServizio.FRUIZIONE, Format.XML, 200, Enc.GZIP_INVALID); }
	/* ---- fruizione: soglia sulla dimensione decompressa ---- */
	@Test public void fruizioneProblemJsonGzipSottoSoglia() throws Exception { _testSoglia(TipoServizio.FRUIZIONE, Format.JSON, false); }
	@Test public void fruizioneProblemJsonGzipSopraSoglia() throws Exception { _testSoglia(TipoServizio.FRUIZIONE, Format.JSON, true); }
	@Test public void fruizioneProblemXmlGzipSottoSoglia() throws Exception { _testSoglia(TipoServizio.FRUIZIONE, Format.XML, false); }
	@Test public void fruizioneProblemXmlGzipSopraSoglia() throws Exception { _testSoglia(TipoServizio.FRUIZIONE, Format.XML, true); }
	/* ---- fruizione: controllo, risposta di errore non Problem Details ---- */
	@Test public void fruizioneJson403NonProblemNone() throws Exception { _testNonProblem(TipoServizio.FRUIZIONE, Enc.NONE); }
	@Test public void fruizioneJson403NonProblemGzip() throws Exception { _testNonProblem(TipoServizio.FRUIZIONE, Enc.GZIP); }
	@Test public void fruizioneJson403NonProblemBr() throws Exception { _testNonProblem(TipoServizio.FRUIZIONE, Enc.BR); }
	/* ---- fruizione: risorsa response (decompressione abilitata) ---- */
	@Test public void fruizioneProblemJson403DecompressGzip() throws Exception { _testDecompressAbilitata(TipoServizio.FRUIZIONE, Format.JSON, Enc.GZIP); }
	@Test public void fruizioneProblemJson403DecompressXGzip() throws Exception { _testDecompressAbilitata(TipoServizio.FRUIZIONE, Format.JSON, Enc.X_GZIP); }
	@Test public void fruizioneProblemJson403DecompressDeflateZlib() throws Exception { _testDecompressAbilitata(TipoServizio.FRUIZIONE, Format.JSON, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneProblemJson403DecompressDeflateRaw() throws Exception { _testDecompressAbilitata(TipoServizio.FRUIZIONE, Format.JSON, Enc.DEFLATE_RAW); }
	@Test public void fruizioneProblemJson403DecompressBr() throws Exception { _testDecompressAbilitataNonSupportato(TipoServizio.FRUIZIONE, Format.JSON, Enc.BR); }
	@Test public void fruizioneProblemJson403DecompressDeflateGzip() throws Exception { _testDecompressAbilitataNonSupportato(TipoServizio.FRUIZIONE, Format.JSON, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneProblemJson403DecompressGzipSopraSoglia() throws Exception { _testDecompressAbilitataSopraSoglia(TipoServizio.FRUIZIONE, Format.JSON); }
	@Test public void fruizioneProblemXml403DecompressGzip() throws Exception { _testDecompressAbilitata(TipoServizio.FRUIZIONE, Format.XML, Enc.GZIP); }
	@Test public void fruizioneProblemXml403DecompressXGzip() throws Exception { _testDecompressAbilitata(TipoServizio.FRUIZIONE, Format.XML, Enc.X_GZIP); }
	@Test public void fruizioneProblemXml403DecompressDeflateZlib() throws Exception { _testDecompressAbilitata(TipoServizio.FRUIZIONE, Format.XML, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneProblemXml403DecompressDeflateRaw() throws Exception { _testDecompressAbilitata(TipoServizio.FRUIZIONE, Format.XML, Enc.DEFLATE_RAW); }
	@Test public void fruizioneProblemXml403DecompressBr() throws Exception { _testDecompressAbilitataNonSupportato(TipoServizio.FRUIZIONE, Format.XML, Enc.BR); }
	@Test public void fruizioneProblemXml403DecompressDeflateGzip() throws Exception { _testDecompressAbilitataNonSupportato(TipoServizio.FRUIZIONE, Format.XML, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneProblemXml403DecompressGzipSopraSoglia() throws Exception { _testDecompressAbilitataSopraSoglia(TipoServizio.FRUIZIONE, Format.XML); }
	/* ---- fruizione: trasformazione del contenuto della risposta (stesso tipo, JSON -> JSON) ---- */
	@Test public void fruizioneTrasformazioneContenutoRispostaNone() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.FRUIZIONE, Enc.NONE); }
	@Test public void fruizioneTrasformazioneContenutoRispostaGzip() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.FRUIZIONE, Enc.GZIP); }
	@Test public void fruizioneTrasformazioneContenutoRispostaXGzip() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.FRUIZIONE, Enc.X_GZIP); }
	@Test public void fruizioneTrasformazioneContenutoRispostaDeflateZlib() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.FRUIZIONE, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneTrasformazioneContenutoRispostaDeflateRaw() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.FRUIZIONE, Enc.DEFLATE_RAW); }
	@Test public void fruizioneTrasformazioneContenutoRispostaBr() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.FRUIZIONE, Enc.BR); }
	@Test public void fruizioneTrasformazioneContenutoRispostaDeflateGzip() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.FRUIZIONE, Enc.DEFLATE_GZIP); }
	@Test public void fruizioneTrasformazioneContenutoRispostaGzipInvalid() throws Exception { _testTrasformazioneContenutoRisposta(TipoServizio.FRUIZIONE, Enc.GZIP_INVALID); }
	/* ---- fruizione: trasformazione dei soli header (il Content-Encoding deve restare) ---- */
	@Test public void fruizioneTrasformazioneSoloHeaderRispostaNone() throws Exception { _testTrasformazioneSoloHeaderRisposta(TipoServizio.FRUIZIONE, Enc.NONE); }
	@Test public void fruizioneTrasformazioneSoloHeaderRispostaGzip() throws Exception { _testTrasformazioneSoloHeaderRisposta(TipoServizio.FRUIZIONE, Enc.GZIP); }
	@Test public void fruizioneTrasformazioneSoloHeaderRispostaDeflateRaw() throws Exception { _testTrasformazioneSoloHeaderRisposta(TipoServizio.FRUIZIONE, Enc.DEFLATE_RAW); }
	@Test public void fruizioneTrasformazioneSoloHeaderRispostaBr() throws Exception { _testTrasformazioneSoloHeaderRisposta(TipoServizio.FRUIZIONE, Enc.BR); }
	@Test public void fruizioneTrasformazioneSoloHeaderRichiestaGzip() throws Exception { _testTrasformazioneSoloHeaderRichiesta(TipoServizio.FRUIZIONE, Enc.GZIP); }
	@Test public void fruizioneTrasformazioneSoloHeaderRichiestaDeflateRaw() throws Exception { _testTrasformazioneSoloHeaderRichiesta(TipoServizio.FRUIZIONE, Enc.DEFLATE_RAW); }
	@Test public void fruizioneTrasformazioneSoloHeaderRichiestaBr() throws Exception { _testTrasformazioneSoloHeaderRichiesta(TipoServizio.FRUIZIONE, Enc.BR); }
	/* ---- fruizione: trasformazione del contenuto della richiesta ---- */
	@Test public void fruizioneTrasformazioneRichiestaNone() throws Exception { _testTrasformazioneRichiesta(TipoServizio.FRUIZIONE, Enc.NONE); }
	@Test public void fruizioneTrasformazioneRichiestaGzip() throws Exception { _testTrasformazioneRichiesta(TipoServizio.FRUIZIONE, Enc.GZIP); }
	@Test public void fruizioneTrasformazioneRichiestaXGzip() throws Exception { _testTrasformazioneRichiesta(TipoServizio.FRUIZIONE, Enc.X_GZIP); }
	@Test public void fruizioneTrasformazioneRichiestaDeflateZlib() throws Exception { _testTrasformazioneRichiesta(TipoServizio.FRUIZIONE, Enc.DEFLATE_ZLIB); }
	@Test public void fruizioneTrasformazioneRichiestaDeflateRaw() throws Exception { _testTrasformazioneRichiesta(TipoServizio.FRUIZIONE, Enc.DEFLATE_RAW); }
	@Test public void fruizioneTrasformazioneRichiestaBr() throws Exception { _testTrasformazioneRichiesta(TipoServizio.FRUIZIONE, Enc.BR); }
	@Test public void fruizioneTrasformazioneRichiestaGzipInvalid() throws Exception { _testTrasformazioneRichiesta(TipoServizio.FRUIZIONE, Enc.GZIP_INVALID); }
	/* ---- fruizione: trasformazione della risposta che cambia il tipo di messaggio ---- */
	@Test public void fruizioneProblemJson403TrasformazioneNone() throws Exception { _testTrasformazione(TipoServizio.FRUIZIONE, Format.JSON, Enc.NONE); }
	@Test public void fruizioneProblemJson403TrasformazioneGzip() throws Exception { _testTrasformazione(TipoServizio.FRUIZIONE, Format.JSON, Enc.GZIP); }
	@Test public void fruizioneProblemJson403TrasformazioneDeflateRaw() throws Exception { _testTrasformazione(TipoServizio.FRUIZIONE, Format.JSON, Enc.DEFLATE_RAW); }
	@Test public void fruizioneProblemJson403TrasformazioneBr() throws Exception { _testTrasformazione(TipoServizio.FRUIZIONE, Format.JSON, Enc.BR); }
	@Test public void fruizioneProblemJson403TrasformazioneGzipInvalid() throws Exception { _testTrasformazione(TipoServizio.FRUIZIONE, Format.JSON, Enc.GZIP_INVALID); }
	@Test public void fruizioneProblemXml403TrasformazioneNone() throws Exception { _testTrasformazione(TipoServizio.FRUIZIONE, Format.XML, Enc.NONE); }
	@Test public void fruizioneProblemXml403TrasformazioneGzip() throws Exception { _testTrasformazione(TipoServizio.FRUIZIONE, Format.XML, Enc.GZIP); }
	@Test public void fruizioneProblemXml403TrasformazioneDeflateRaw() throws Exception { _testTrasformazione(TipoServizio.FRUIZIONE, Format.XML, Enc.DEFLATE_RAW); }
	@Test public void fruizioneProblemXml403TrasformazioneBr() throws Exception { _testTrasformazione(TipoServizio.FRUIZIONE, Format.XML, Enc.BR); }
	@Test public void fruizioneProblemXml403TrasformazioneGzipInvalid() throws Exception { _testTrasformazione(TipoServizio.FRUIZIONE, Format.XML, Enc.GZIP_INVALID); }


	/* ====================================================================================== */
	/* ======================================= ENGINE ======================================== */
	/* ====================================================================================== */

	/**
	 * Risorsa {@code default} (decompressione disabilitata): la risposta deve arrivare al client
	 * byte per byte come inviata dal backend; il Problem Details deve essere interpretato solo con
	 * gli encoding supportati, altrimenti deve essere emesso il diagnostico di mancata interpretazione.
	 */
	private void _testDefault(TipoServizio tipo, Format format, int status, Enc enc) throws Exception {
		String type = "https://govway.org/testsuite/problem/" + status + "/" + enc.name().toLowerCase();
		byte[] problem = buildProblem(format, type, status, "Problem compresso dal backend (" + enc.name() + ")");
		HttpResponse response = invoke(tipo, RES_DEFAULT, format, problem, status, format.problemContentType, enc);

		String label = label(tipo, RES_DEFAULT, format, status, enc);
		String idTransazione = verifyCommon(response, status, label);
		verifyBodyIdentico(response, label);
		if (enc.isClientDecodable()) {
			assertEquals(label + " body decodificato dal client diverso dal problem inviato",
					new String(problem, StandardCharsets.UTF_8), new String(clientDecode(response.getContent(), enc), StandardCharsets.UTF_8));
		}
		switch (enc.kind) {
		case PARSED:
			verifyProblemInterpretato(idTransazione, type);
			verifyRispostaConsegnata(idTransazione, type, status);
			break;
		case UNSUPPORTED:
			verifyProblemNonInterpretato(tipo, idTransazione, declaredEncoding(enc), DIAG_MOTIVO_ENCODING_NON_SUPPORTATO);
			verifyRispostaConsegnataCompressa(idTransazione, declaredEncoding(enc), status);
			break;
		case DECOMPRESSION_FAILED:
			verifyProblemNonInterpretato(tipo, idTransazione, declaredEncoding(enc), DIAG_MOTIVO_DECOMPRESSIONE_FALLITA);
			verifyRispostaConsegnataCompressa(idTransazione, declaredEncoding(enc), status);
			break;
		}
	}

	/** Problem Details gzip con dimensione decompressa appena sotto o appena sopra la soglia. */
	private void _testSoglia(TipoServizio tipo, Format format, boolean sopraSoglia) throws Exception {
		int status = 403;
		String type = "https://govway.org/testsuite/problem/soglia/" + (sopraSoglia ? "sopra" : "sotto");
		int size = sopraSoglia ? SOGLIA_BYTES + MARGINE_SOGLIA_BYTES : SOGLIA_BYTES - MARGINE_SOGLIA_BYTES;
		byte[] problem = buildProblemOfSize(format, type, status, size);
		HttpResponse response = invoke(tipo, RES_DEFAULT, format, problem, status, format.problemContentType, Enc.GZIP);

		String label = label(tipo, RES_DEFAULT, format, status, Enc.GZIP) + "[soglia:" + (sopraSoglia ? "sopra" : "sotto") + "]";
		String idTransazione = verifyCommon(response, status, label);
		verifyBodyIdentico(response, label);
		assertTrue(label + " body decodificato dal client diverso dal problem inviato",
				Arrays.equals(problem, clientDecode(response.getContent(), Enc.GZIP)));
		if (sopraSoglia) {
			verifyProblemNonInterpretato(tipo, idTransazione, HttpConstants.CONTENT_ENCODING_VALUE_GZIP, DIAG_MOTIVO_SOGLIA);
			verifyRispostaConsegnataCompressa(idTransazione, HttpConstants.CONTENT_ENCODING_VALUE_GZIP, status);
		} else {
			verifyProblemInterpretato(idTransazione, type);
			verifyRispostaConsegnata(idTransazione, type, status);
		}
	}

	/** Controllo: risposta di errore compressa ma non Problem Details (application/json). */
	private void _testNonProblem(TipoServizio tipo, Enc enc) throws Exception {
		int status = 403;
		byte[] body = "{\"errore\":\"risposta di errore non problem\",\"codice\":403}".getBytes(StandardCharsets.UTF_8);
		HttpResponse response = invoke(tipo, RES_DEFAULT, Format.JSON, body, status, HttpConstants.CONTENT_TYPE_JSON, enc);

		String label = label(tipo, RES_DEFAULT, Format.JSON, status, enc) + "[non-problem]";
		String idTransazione = verifyCommon(response, status, EsitoTransazioneName.HTTP_4xx, label);
		verifyBodyIdentico(response, label);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_RICEVUTO_PROBLEM);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_PROBLEM_NON_INTERPRETATO_PREFIX);
	}

	/**
	 * Risorsa {@code response} (decompressione abilitata): GovWay decomprime a monte, il client riceve
	 * il Problem Details in chiaro senza Content-Encoding e il problem viene interpretato.
	 */
	private void _testDecompressAbilitata(TipoServizio tipo, Format format, Enc enc) throws Exception {
		int status = 403;
		String type = "https://govway.org/testsuite/problem/decompress/" + enc.name().toLowerCase();
		byte[] problem = buildProblem(format, type, status, "Problem compresso, decompressione abilitata (" + enc.name() + ")");
		HttpResponse response = invoke(tipo, RES_RESPONSE, format, problem, status, format.problemContentType, enc);

		String label = label(tipo, RES_RESPONSE, format, status, enc);
		String idTransazione = verifyCommon(response, status, label);
		verifyNoContentEncoding(response, label);
		assertEquals(label + " body atteso in chiaro", new String(problem, StandardCharsets.UTF_8), new String(response.getContent(), StandardCharsets.UTF_8));
		DBVerifier.existsDiagnostico(idTransazione, DIAG_DECOMPRESSED);
		verifyProblemInterpretato(idTransazione, type);
		verifyRispostaConsegnata(idTransazione, type, status);
	}

	/**
	 * Risorsa {@code response} con encoding non supportato: comportamento invariato, la risposta viene
	 * rifiutata dalla decompressione automatica prima della gestione dell'errore.
	 */
	private void _testDecompressAbilitataNonSupportato(TipoServizio tipo, Format format, Enc enc) throws Exception {
		int status = 403;
		String type = "https://govway.org/testsuite/problem/decompress/" + enc.name().toLowerCase();
		byte[] problem = buildProblem(format, type, status, "Problem con encoding non supportato, decompressione abilitata");
		HttpResponse response = invoke(tipo, RES_RESPONSE, format, problem, status, format.problemContentType, enc);

		String label = label(tipo, RES_RESPONSE, format, status, enc);
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(label + " manca GovWay-Transaction-ID", idTransazione);
		assertFalse(label + " atteso errore del gateway, ricevuto " + response.getResultHTTPOperation(),
				response.getResultHTTPOperation() == status);
		DBVerifier.existsDiagnostico(idTransazione, DIAG_UNSUPPORTED_DECOMPRESS);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_RICEVUTO_PROBLEM);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_PROBLEM_NON_INTERPRETATO_PREFIX);
	}

	/**
	 * Risorsa {@code response} con problem oltre la soglia: la soglia riguarda solo la decompressione
	 * interna finalizzata al parsing; con la decompressione abilitata il body arriva già in chiaro e
	 * il problem viene interpretato.
	 */
	private void _testDecompressAbilitataSopraSoglia(TipoServizio tipo, Format format) throws Exception {
		int status = 403;
		String type = "https://govway.org/testsuite/problem/decompress/soglia";
		byte[] problem = buildProblemOfSize(format, type, status, SOGLIA_BYTES + MARGINE_SOGLIA_BYTES);
		HttpResponse response = invoke(tipo, RES_RESPONSE, format, problem, status, format.problemContentType, Enc.GZIP);

		String label = label(tipo, RES_RESPONSE, format, status, Enc.GZIP) + "[soglia:sopra]";
		String idTransazione = verifyCommon(response, status, label);
		verifyNoContentEncoding(response, label);
		assertTrue(label + " body atteso in chiaro", Arrays.equals(problem, response.getContent()));
		DBVerifier.existsDiagnostico(idTransazione, DIAG_DECOMPRESSED);
		verifyProblemInterpretato(idTransazione, type);
		verifyRispostaConsegnata(idTransazione, type, status);
	}

	/**
	 * Risorse {@code trasformazioneProblem*}: la risposta compressa del backend viene trasformata in un
	 * Problem Details di tipo diverso (JSON -> XML e XML -> JSON), attivando il secondo punto di
	 * interpretazione del problem. La risposta trasformata è in chiaro: non deve avere il
	 * Content-Encoding del backend e il problem trasformato deve essere interpretato.
	 * Il Problem Details originale segue le regole del punto di interpretazione principale.
	 *
	 * @param format formato del Problem Details restituito dal backend
	 */
	private void _testTrasformazione(TipoServizio tipo, Format format, Enc enc) throws Exception {
		int status = 403;
		String resource = Format.JSON.equals(format) ? RES_TRASFORMAZIONE_PROBLEM_XML : RES_TRASFORMAZIONE_PROBLEM_JSON;
		String typeTrasformato = Format.JSON.equals(format) ? TYPE_TRASFORMATO_XML : TYPE_TRASFORMATO_JSON;
		String typeOriginale = "https://govway.org/testsuite/problem/originale/" + enc.name().toLowerCase();
		byte[] problem = buildProblem(format, typeOriginale, status, "Problem originale da trasformare (" + enc.name() + ")");
		HttpResponse response = invoke(tipo, resource, format, problem, status, format.problemContentType, enc);

		String label = label(tipo, resource, format, status, enc);
		String idTransazione = verifyCommon(response, status, label);
		verifyNoContentEncoding(response, label);
		String body = new String(response.getContent(), StandardCharsets.UTF_8);
		assertTrue(label + " atteso il problem prodotto dalla trasformazione, ricevuto: " + body, body.contains(typeTrasformato));
		/* il problem prodotto dalla trasformazione (in chiaro) viene interpretato e consegnato al client */
		DBVerifier.existsDiagnosticoFrammenti(idTransazione, DIAG_RICEVUTO_PROBLEM, typeTrasformato);
		verifyRispostaConsegnata(idTransazione, typeTrasformato, status);
		/* il problem originale segue le regole del punto di interpretazione principale */
		switch (enc.kind) {
		case PARSED:
			DBVerifier.notExistsDiagnostico(idTransazione, DIAG_PROBLEM_NON_INTERPRETATO_PREFIX);
			break;
		case UNSUPPORTED:
			verifyProblemNonInterpretato(tipo, idTransazione, declaredEncoding(enc), DIAG_MOTIVO_ENCODING_NON_SUPPORTATO, false);
			break;
		case DECOMPRESSION_FAILED:
			verifyProblemNonInterpretato(tipo, idTransazione, declaredEncoding(enc), DIAG_MOTIVO_DECOMPRESSIONE_FALLITA, false);
			break;
		}
	}

	/**
	 * Risorsa {@code trasformazioneContenutoJson}: la risposta JSON compressa del backend viene riscritta
	 * da un template JSON (stesso tipo di messaggio). La risposta trasformata è in chiaro e non deve
	 * avere il Content-Encoding del backend (implementazione I5).
	 */
	private void _testTrasformazioneContenutoRisposta(TipoServizio tipo, Enc enc) throws Exception {
		byte[] body = "{\"risposta\":\"backend\"}".getBytes(StandardCharsets.UTF_8);
		HttpResponse response = invoke(tipo, RES_TRASFORMAZIONE_CONTENUTO_JSON, Format.JSON, body, 200, HttpConstants.CONTENT_TYPE_JSON, enc);

		String label = label(tipo, RES_TRASFORMAZIONE_CONTENUTO_JSON, Format.JSON, 200, enc);
		verifyCommon(response, 200, EsitoTransazioneName.OK, label);
		verifyNoContentEncoding(response, label);
		assertEquals(label + " atteso il contenuto prodotto dalla trasformazione", TEMPLATE_RISPOSTA_JSON, new String(response.getContent(), StandardCharsets.UTF_8));
	}

	/**
	 * Risorsa {@code trasformazioneSoloHeader}, lato risposta: la trasformazione aggiunge solo un header
	 * e non riscrive il contenuto; i byte e il Content-Encoding del backend devono restare invariati.
	 */
	private void _testTrasformazioneSoloHeaderRisposta(TipoServizio tipo, Enc enc) throws Exception {
		byte[] body = "{\"risposta\":\"backend\",\"trasformazione\":\"solo header\"}".getBytes(StandardCharsets.UTF_8);
		HttpResponse response = invoke(tipo, RES_TRASFORMAZIONE_SOLO_HEADER, Format.JSON, body, 200, HttpConstants.CONTENT_TYPE_JSON, enc);

		String label = label(tipo, RES_TRASFORMAZIONE_SOLO_HEADER, Format.JSON, 200, enc) + "[risposta]";
		verifyCommon(response, 200, EsitoTransazioneName.OK, label);
		assertEquals(label + " header aggiunto dalla trasformazione della risposta",
				VALORE_HEADER_TRASFORMAZIONE, response.getHeaderFirstValue(HEADER_TRASFORMAZIONE_RISPOSTA));
		verifyBodyIdentico(response, label);
		if (enc.isClientDecodable()) {
			assertEquals(label + " body decodificato dal client diverso da quello inviato",
					new String(body, StandardCharsets.UTF_8), new String(clientDecode(response.getContent(), enc), StandardCharsets.UTF_8));
		}
	}

	/**
	 * Risorsa {@code trasformazioneSoloHeader}, lato richiesta: il client invia un body compresso, la
	 * trasformazione aggiunge solo un header; il backend deve ricevere i byte e il Content-Encoding
	 * originali.
	 */
	private void _testTrasformazioneSoloHeaderRichiesta(TipoServizio tipo, Enc enc) throws Exception {
		byte[] plain = "{\"richiesta\":\"client\",\"trasformazione\":\"solo header\"}".getBytes(StandardCharsets.UTF_8);
		byte[] onWire = encodeForRequest(plain, enc);
		HttpResponse response = invokeRequestEncoded(tipo, RES_TRASFORMAZIONE_SOLO_HEADER, onWire, enc);

		String label = label(tipo, RES_TRASFORMAZIONE_SOLO_HEADER, Format.JSON, 200, enc) + "[richiesta]";
		verifyCommon(response, 200, EsitoTransazioneName.OK, label);
		assertEquals(label + " header aggiunto dalla trasformazione della richiesta non ricevuto dal backend", VALORE_HEADER_TRASFORMAZIONE,
				response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_PREFIX + HEADER_TRASFORMAZIONE_RICHIESTA));
		assertEquals(label + " Content-Encoding ricevuto dal backend", requestDeclaredEncoding(enc),
				response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_CONTENT_ENCODING));
		assertEquals(label + " dimensione del body ricevuto dal backend", String.valueOf(onWire.length),
				response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_BODY_BYTES));
		assertEquals(label + " primi byte del body ricevuto dal backend", toHex(onWire, 8),
				response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_BODY_MAGIC));
		assertEquals(label + " body ricevuto dal backend alterato rispetto a quello inviato dal client (sha256)",
				ContentEncodingMockServer.sha256Hex(onWire), response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_BODY_SHA256));
	}

	/**
	 * Risorsa {@code trasformazioneRichiesta}: il client invia un body compresso, la trasformazione lo
	 * riscrive con un template JSON. Il backend riceve il contenuto in chiaro e non deve ricevere il
	 * Content-Encoding del client (implementazione I5, lato richiesta).
	 */
	private void _testTrasformazioneRichiesta(TipoServizio tipo, Enc enc) throws Exception {
		byte[] plain = "{\"richiesta\":\"client\"}".getBytes(StandardCharsets.UTF_8);
		byte[] onWire = encodeForRequest(plain, enc);
		HttpResponse response = invokeRequestEncoded(tipo, RES_TRASFORMAZIONE_RICHIESTA, onWire, enc);

		String label = label(tipo, RES_TRASFORMAZIONE_RICHIESTA, Format.JSON, 200, enc) + "[richiesta]";
		verifyCommon(response, 200, EsitoTransazioneName.OK, label);
		String receivedCE = response.getHeaderFirstValue(ContentEncodingMockServer.HEADER_RECEIVED_CONTENT_ENCODING);
		assertTrue(label + " il backend non deve ricevere il Content-Encoding del client su un contenuto riscritto; ricevuto: " + receivedCE,
				receivedCE == null || receivedCE.isEmpty());
		/* il mock restituisce il body ricevuto: è il contenuto prodotto dalla trasformazione */
		assertEquals(label + " il backend deve ricevere il contenuto prodotto dalla trasformazione",
				TEMPLATE_RICHIESTA_JSON, new String(response.getContent(), StandardCharsets.UTF_8));
	}

	/* ====================================================================================== */
	/* ===================================== Utilities ======================================= */
	/* ====================================================================================== */

	private HttpResponse invoke(TipoServizio tipo, String resource, Format format, byte[] body,
			int replyStatus, String replyContentType, Enc enc) throws Exception {
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(format.requestContentType);
		request.setContent(body);
		request.setUrl(buildUrl(tipo, resource));
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_ENCODING, enc.replyEncoding);
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_STATUS, String.valueOf(replyStatus));
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_CONTENT_TYPE, replyContentType);
		if (enc.declaredOverride != null) {
			request.addHeader(ContentEncodingMockServer.HEADER_REPLY_CONTENT_ENCODING, enc.declaredOverride);
		}
		if (this.libraryMode != null) {
			this.libraryMode.patchRequest(request);
		}
		return HttpUtilities.httpInvoke(request);
	}

	/** Richiesta con body già codificato dal client; il mock risponde 200 in chiaro con il body ricevuto. */
	private HttpResponse invokeRequestEncoded(TipoServizio tipo, String resource, byte[] onWire, Enc enc) throws Exception {
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		request.setContent(onWire);
		request.setUrl(buildUrl(tipo, resource));
		String ce = requestDeclaredEncoding(enc);
		if (!ce.isEmpty()) {
			request.addHeader(HttpConstants.CONTENT_ENCODING, ce);
		}
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_ENCODING, ContentEncodingMockServer.REPLY_ENCODING_NONE);
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_STATUS, "200");
		request.addHeader(ContentEncodingMockServer.HEADER_REPLY_CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
		if (this.libraryMode != null) {
			this.libraryMode.patchRequest(request);
		}
		return HttpUtilities.httpInvoke(request);
	}

	private String buildUrl(TipoServizio tipo, String resource) {
		return ContentEncodingTestUtils.buildUrl(tipo, API, resource);
	}

	private static String label(TipoServizio tipo, String resource, Format format, int status, Enc enc) {
		return "[" + tipo + "/" + resource + "/" + format + "/" + status + "/" + enc + "]";
	}

	/** Verifiche comuni: status, id transazione, esito registrato e libreria utilizzata. */
	private String verifyCommon(HttpResponse response, int status, String label) throws Exception {
		/* Una risposta Problem Details è classificata come 'Fault Applicativo' qualunque sia lo status
		 * (anche 2xx), indipendentemente dalla sua interpretazione. */
		return verifyCommon(response, status, EsitoTransazioneName.ERRORE_APPLICATIVO, label);
	}
	private String verifyCommon(HttpResponse response, int status, EsitoTransazioneName esito, String label) throws Exception {
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(label + " manca GovWay-Transaction-ID", idTransazione);
		assertEquals(label + " status code", status, response.getResultHTTPOperation());
		EsitiProperties esiti = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME);
		DBVerifier.verify(idTransazione, esiti.convertoToCode(esito), this.libraryMode);
		return idTransazione;
	}

	private static void verifyProblemInterpretato(String idTransazione, String type) throws Exception {
		/* il diagnostico di ricezione (consegnaContenutiApplicativi / inoltroBuste .ricezioneRestProblem) deve riportare il problem interpretato */
		DBVerifier.existsDiagnosticoFrammenti(idTransazione, DIAG_RICEVUTO_PROBLEM, type);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_PROBLEM_NON_INTERPRETATO_PREFIX);
	}

	/** Il diagnostico di consegna della risposta al client deve riportare il problem consegnato (in chiaro anche se inoltrato compresso). */
	private static void verifyRispostaConsegnata(String idTransazione, String type, int status) throws Exception {
		DBVerifier.existsDiagnosticoFrammenti(idTransazione, DIAG_RISPOSTA, " (", type, DIAG_RISPOSTA_CONSEGNATA, DIAG_RISPOSTA_CODICE_TRASPORTO + status);
	}

	/** Problem compresso non interpretabile: il diagnostico di consegna ne riporta solo la presenza con il Content-Encoding. */
	private static void verifyRispostaConsegnataCompressa(String idTransazione, String contentEncoding, int status) throws Exception {
		DBVerifier.existsDiagnosticoFrammenti(idTransazione, DIAG_RISPOSTA, DIAG_RISPOSTA_CONTENUTO_COMPRESSO + contentEncoding + DIAG_RISPOSTA_CONSEGNATA,
				DIAG_RISPOSTA_CODICE_TRASPORTO + status);
	}

	private static void verifyProblemNonInterpretato(TipoServizio tipo, String idTransazione, String contentEncoding, String motivo) throws Exception {
		verifyProblemNonInterpretato(tipo, idTransazione, contentEncoding, motivo, true);
	}
	/**
	 * @param ricevutoAssente false quando un problem successivo (es. prodotto da una trasformazione) viene interpretato
	 *        ed emette il diagnostico di ricezione
	 */
	private static void verifyProblemNonInterpretato(TipoServizio tipo, String idTransazione, String contentEncoding, String motivo, boolean ricevutoAssente) throws Exception {
		if (ricevutoAssente) {
			DBVerifier.notExistsDiagnostico(idTransazione, DIAG_RICEVUTO_PROBLEM);
		}
		String diag = DIAG_PROBLEM_NON_INTERPRETATO_PREFIX + contentEncoding + DIAG_PROBLEM_NON_INTERPRETATO;
		DBVerifier.existsDiagnosticoSeveritaCodice(idTransazione, SEVERITA_ERROR_INTEGRATION,
				TipoServizio.EROGAZIONE.equals(tipo) ? CODICE_DIAG_NON_INTERPRETATO_EROGAZIONE : CODICE_DIAG_NON_INTERPRETATO_FRUIZIONE,
				diag);
		DBVerifier.existsDiagnostico(idTransazione, motivo);
		DBVerifier.existsWarningLog(idTransazione, diag);
	}

	private static byte[] buildProblem(Format format, String type, int status, String detail) {
		String title = status == 500 ? "Internal Server Error" : (status == 200 ? "OK" : "Forbidden");
		String s;
		if (Format.JSON.equals(format)) {
			s = "{\"type\":\"" + type + "\",\"title\":\"" + title + "\",\"status\":" + status
					+ ",\"detail\":\"" + detail + "\",\"instance\":\"/testsuite/problem\"}";
		} else {
			s = "<problem xmlns=\"urn:ietf:rfc:7807\"><type>" + type + "</type><title>" + title + "</title><status>" + status
					+ "</status><detail>" + detail + "</detail><instance>/testsuite/problem</instance></problem>";
		}
		return s.getBytes(StandardCharsets.UTF_8);
	}

	/** Problem Details la cui dimensione totale è {@code size} bytes (il campo detail fa da riempimento). */
	private static byte[] buildProblemOfSize(Format format, String type, int status, int size) {
		int base = buildProblem(format, type, status, "").length;
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size - base; i++) {
			sb.append((char) ('a' + (i % 26)));
		}
		byte[] problem = buildProblem(format, type, status, sb.toString());
		assertEquals("dimensione del problem di test", size, problem.length);
		return problem;
	}
}
