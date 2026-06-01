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
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * Verifica della compensazione di un Content-Type 'multipart/related' privo del parametro 'type'
 * (RFC 2387 §3.1) sia lato richiesta sia lato risposta, per i protocolli SOAP 1.1 e SOAP 1.2,
 * per erogazione e fruizione.
 *
 * Ogni azione URL mappa una porta (PA/PD) Specific che applica un'override di property sulla
 * strategia di compensazione (`none`, `inferFromRequest`, `inferFromBody`, `forceSoap11`,
 * `forceSoap12`). Vedi `ANALISI_ERRORE/CONFIG_test_multipart_type_missing.md`.
 *
 * Ogni metodo di test indica un {@link Outcome} atteso che comprende sia l'esito HTTP/DB sia
 * la presenza/assenza del diagnostico di compensazione applicata. Per i casi `none` in cui il
 * body SOAP coincide per caso con la classificazione di default del binding
 * (multipart/related → SOAP_12), l'esito è OK ma senza compensazione applicata.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MultipartTypeMissingSoapTest extends ConfigLoader {

	// Erogazione standard (registrazione messaggi NON attiva)
	private static final String API = "TestMultipartTypeMissingSoap";
	// Erogazione gemella con registrazione messaggi attiva: serve a verificare che la compensazione
	// continui a funzionare correttamente anche quando il payload viene letto/dumpato in più punti
	private static final String API_REGISTRAZIONE = "TestMultipartTypeMissingSoapRegistrazione";

	// header HTTP custom che permette al test di pilotare il Content-Type della risposta del backend echo.
	// L'header viene propagato al backend dal connettore SA e letto direttamente da ServletTestService,
	// evitando il transito via query string (che produrrebbe doppio encoding sui caratteri ';' e '"').
	private static final String HEADER_TEST_REPLY_CONTENT_TYPE = "GovWay-TestSuite-Response-ContentType";

	// frammenti dei diagnostici cercati (LIKE '%...%')
	private static final String DIAG_COMPENSATED = "applicata compensazione";
	// errore lato richiesta quando il classificatore di default mappa multipart/related → SOAP_12
	// e il body è invece SOAP 1.1: namespace mismatch sull'envelope
	private static final String DIAG_ERR_REQUEST_NAMESPACE_MISMATCH = "diverso da quello atteso per messaggi";
	// errore lato risposta quando il classificatore di default mappa la response multipart non
	// conforme a una versione SOAP diversa da quella della richiesta (caso INPS)
	private static final String DIAG_ERR_RESPONSE_TYPE_MISMATCH = "Header Content-Type definito nell'http reply";
	// Diagnostico emesso quando il parser SAAJ 1.2 non riesce a internalizzare un multipart privo del parametro 'type'
	private static final String DIAG_ERR_UNABLE_TO_INTERNALIZE = "Unable to internalize message";

	// nomi delle azioni (corrispondono ai segmenti URL configurati nelle Specific)
	// l'azione 'default' invoca una PA/PD Specific PRIVA di override di property:
	// vengono utilizzati i default globali (none lato richiesta, inferFromRequest lato risposta).
	private static final String ACTION_DEFAULT                 = "default";

	// azioni che configurano la property OMBRELLO (connettori.multipart.related.missingType.behavior),
	// applicata sia al lato richiesta sia al lato risposta
	private static final String ACTION_UMBRELLA_INFER_FROM_BODY     = "inferFromBody";
	// 'inferFromRequest' su ombrello: valido lato risposta, configurazione NON applicabile lato richiesta
	private static final String ACTION_UMBRELLA_INFER_FROM_REQUEST  = "inferFromRequest";

	private static final String ACTION_REQUEST_NONE               = "request.none";
	// 'inferFromRequest' specifico request side: configurazione SEMPRE non applicabile (fallback a 'none')
	private static final String ACTION_REQUEST_INFER_FROM_REQUEST = "request.inferFromRequest";
	private static final String ACTION_REQUEST_INFER_FROM_BODY    = "request.inferFromBody";
	private static final String ACTION_REQUEST_FORCE_SOAP11       = "request.forceSoap11";
	private static final String ACTION_REQUEST_FORCE_SOAP12       = "request.forceSoap12";

	private static final String ACTION_RESPONSE_NONE               = "response.none";
	private static final String ACTION_RESPONSE_INFER_FROM_REQUEST = "response.inferFromRequest";
	private static final String ACTION_RESPONSE_INFER_FROM_BODY    = "response.inferFromBody";
	private static final String ACTION_RESPONSE_FORCE_SOAP11       = "response.forceSoap11";
	private static final String ACTION_RESPONSE_FORCE_SOAP12       = "response.forceSoap12";

	/**
	 * Lato del test su cui agisce la compensazione: REQUEST se il Content-Type malformato è
	 * sull'header HTTP della richiesta in ingresso, RESPONSE se è sulla risposta del backend.
	 */
	private enum Side { REQUEST, RESPONSE }

	/**
	 * Esito atteso di un test, comprensivo di:
	 *  - esito HTTP/DB (OK vs errore)
	 *  - applicazione del diagnostico di compensazione
	 *  - eventuale frammento di diagnostico di errore atteso (null se non verificato)
	 */
	private static final class Outcome {
		final boolean ok;
		final boolean compensated;
		final String  expectedErrorDiag;
		private Outcome(boolean ok, boolean compensated, String expectedErrorDiag) {
			this.ok = ok;
			this.compensated = compensated;
			this.expectedErrorDiag = expectedErrorDiag;
		}
		static Outcome okCompensated() { return new Outcome(true, true, null); }
		static Outcome okNoCompensation() { return new Outcome(true, false, null); }
		static Outcome koNoCompensation(String errDiag) { return new Outcome(false, false, errDiag); }
		static Outcome koCompensated() { return new Outcome(false, true, null); }
	}


	/* ----------------------------- DEFAULT (no override) — verifica i default globali ----------------------------- */

	// Lato richiesta: default globale = 'none'.
	//  - body SOAP_11: classificatore default mappa multipart/related → SOAP_12 → mismatch → KO
	//  - body SOAP_12: classificatore default mappa SOAP_12 e il body è SOAP_12 → match (coincidenza) → OK senza compensazione
	@Test public void erogazioneDefaultRequestSoap11() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_DEFAULT, Outcome.koNoCompensation(DIAG_ERR_REQUEST_NAMESPACE_MISMATCH)); }
	@Test public void fruizioneDefaultRequestSoap11() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_DEFAULT, Outcome.koNoCompensation(DIAG_ERR_REQUEST_NAMESPACE_MISMATCH)); }
	@Test public void erogazioneDefaultRequestSoap12() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_DEFAULT, Outcome.okNoCompensation()); }
	@Test public void fruizioneDefaultRequestSoap12() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_DEFAULT, Outcome.okNoCompensation()); }

	// Lato risposta: default globale = 'inferFromRequest' → compensazione sempre applicata
	@Test public void erogazioneDefaultResponseSoap11() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_DEFAULT, Outcome.okCompensated()); }
	@Test public void fruizioneDefaultResponseSoap11() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_DEFAULT, Outcome.okCompensated()); }
	@Test public void erogazioneDefaultResponseSoap12() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_DEFAULT, Outcome.okCompensated()); }
	@Test public void fruizioneDefaultResponseSoap12() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_DEFAULT, Outcome.okCompensated()); }


	/* ----------------------------- REQUEST SIDE — SOAP 1.1 ----------------------------- */

	@Test public void erogazioneRequestSoap11None() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_REQUEST_NONE, Outcome.koNoCompensation(DIAG_ERR_REQUEST_NAMESPACE_MISMATCH)); }
	@Test public void fruizioneRequestSoap11None() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_REQUEST_NONE, Outcome.koNoCompensation(DIAG_ERR_REQUEST_NAMESPACE_MISMATCH)); }

	@Test public void erogazioneRequestSoap11InferFromBody() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_REQUEST_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void fruizioneRequestSoap11InferFromBody() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_REQUEST_INFER_FROM_BODY, Outcome.okCompensated()); }

	@Test public void erogazioneRequestSoap11ForceSoap11() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_REQUEST_FORCE_SOAP11, Outcome.okCompensated()); }
	@Test public void fruizioneRequestSoap11ForceSoap11() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_REQUEST_FORCE_SOAP11, Outcome.okCompensated()); }

	// body SOAP 1.1 + forceSoap12: compensazione applicata, ma parser SOAP 1.2 su body 1.1 → KO
	@Test public void erogazioneRequestSoap11ForceSoap12() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_REQUEST_FORCE_SOAP12, Outcome.koCompensated()); }
	@Test public void fruizioneRequestSoap11ForceSoap12() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_REQUEST_FORCE_SOAP12, Outcome.koCompensated()); }


	/* ----------------------------- REQUEST SIDE — SOAP 1.2 ----------------------------- */

	// body SOAP 1.2 + none: coincide col classificatore di default → OK senza compensazione
	@Test public void erogazioneRequestSoap12None() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_REQUEST_NONE, Outcome.okNoCompensation()); }
	@Test public void fruizioneRequestSoap12None() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_REQUEST_NONE, Outcome.okNoCompensation()); }

	@Test public void erogazioneRequestSoap12InferFromBody() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_REQUEST_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void fruizioneRequestSoap12InferFromBody() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_REQUEST_INFER_FROM_BODY, Outcome.okCompensated()); }

	// body SOAP 1.2 + forceSoap11: compensazione applicata, ma parser SOAP 1.1 su body 1.2 → KO
	@Test public void erogazioneRequestSoap12ForceSoap11() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_REQUEST_FORCE_SOAP11, Outcome.koCompensated()); }
	@Test public void fruizioneRequestSoap12ForceSoap11() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_REQUEST_FORCE_SOAP11, Outcome.koCompensated()); }

	@Test public void erogazioneRequestSoap12ForceSoap12() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_REQUEST_FORCE_SOAP12, Outcome.okCompensated()); }
	@Test public void fruizioneRequestSoap12ForceSoap12() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_REQUEST_FORCE_SOAP12, Outcome.okCompensated()); }


	/* ----------------------------- RESPONSE SIDE — SOAP 1.1 ----------------------------- */

	@Test public void erogazioneResponseSoap11None() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_NONE, Outcome.koNoCompensation(DIAG_ERR_RESPONSE_TYPE_MISMATCH)); }
	@Test public void fruizioneResponseSoap11None() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_RESPONSE_NONE, Outcome.koNoCompensation(DIAG_ERR_RESPONSE_TYPE_MISMATCH)); }

	@Test public void erogazioneResponseSoap11InferFromRequest() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_INFER_FROM_REQUEST, Outcome.okCompensated()); }
	@Test public void fruizioneResponseSoap11InferFromRequest() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_RESPONSE_INFER_FROM_REQUEST, Outcome.okCompensated()); }

	@Test public void erogazioneResponseSoap11InferFromBody() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void fruizioneResponseSoap11InferFromBody() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_RESPONSE_INFER_FROM_BODY, Outcome.okCompensated()); }

	@Test public void erogazioneResponseSoap11ForceSoap11() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_FORCE_SOAP11, Outcome.okCompensated()); }
	@Test public void fruizioneResponseSoap11ForceSoap11() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_RESPONSE_FORCE_SOAP11, Outcome.okCompensated()); }

	// body SOAP 1.1 + forceSoap12: compensazione applicata, ma parser SOAP 1.2 su body 1.1 → KO
	@Test public void erogazioneResponseSoap11ForceSoap12() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_FORCE_SOAP12, Outcome.koCompensated()); }
	@Test public void fruizioneResponseSoap11ForceSoap12() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_RESPONSE_FORCE_SOAP12, Outcome.koCompensated()); }


	/* ----------------------------- RESPONSE SIDE — SOAP 1.2 ----------------------------- */

	// body SOAP 1.2 + none lato risposta: coincide col classificatore di default → OK senza compensazione
	// SAAJ 1.2 è strict sulla presenza del parametro 'type' nei multipart/related: senza compensazione
	// (none) la risposta non viene internalizzata e GovWay genera un Fault 'InvalidResponse'.
	@Test public void erogazioneResponseSoap12None() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_NONE, Outcome.koNoCompensation(DIAG_ERR_UNABLE_TO_INTERNALIZE)); }
	@Test public void fruizioneResponseSoap12None() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_RESPONSE_NONE, Outcome.koNoCompensation(DIAG_ERR_UNABLE_TO_INTERNALIZE)); }

	@Test public void erogazioneResponseSoap12InferFromRequest() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_INFER_FROM_REQUEST, Outcome.okCompensated()); }
	@Test public void fruizioneResponseSoap12InferFromRequest() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_RESPONSE_INFER_FROM_REQUEST, Outcome.okCompensated()); }

	@Test public void erogazioneResponseSoap12InferFromBody() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void fruizioneResponseSoap12InferFromBody() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_RESPONSE_INFER_FROM_BODY, Outcome.okCompensated()); }

	// body SOAP 1.2 + forceSoap11: compensazione applicata, ma parser SOAP 1.1 su body 1.2 → KO
	@Test public void erogazioneResponseSoap12ForceSoap11() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_FORCE_SOAP11, Outcome.koCompensated()); }
	@Test public void fruizioneResponseSoap12ForceSoap11() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_RESPONSE_FORCE_SOAP11, Outcome.koCompensated()); }

	@Test public void erogazioneResponseSoap12ForceSoap12() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_FORCE_SOAP12, Outcome.okCompensated()); }
	@Test public void fruizioneResponseSoap12ForceSoap12() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_RESPONSE_FORCE_SOAP12, Outcome.okCompensated()); }


	/* ===================================================================================== */
	/* ===== Property OMBRELLO (connettori.multipart.related.missingType.behavior)         ===== */
	/* ===================================================================================== */
	/* La property ombrello vale sia lato richiesta sia lato risposta. Si testa qui che il    */
	/* meccanismo di lookup funzioni (inferFromBody) e che 'inferFromRequest', valido lato     */
	/* risposta, sia rifiutato come configurazione non valida lato richiesta.                  */

	// Umbrella inferFromBody: applicabile a entrambi i lati
	@Test public void erogazioneRequestSoap11UmbrellaInferFromBody() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_UMBRELLA_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void fruizioneRequestSoap11UmbrellaInferFromBody() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_UMBRELLA_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void erogazioneRequestSoap12UmbrellaInferFromBody() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_UMBRELLA_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void fruizioneRequestSoap12UmbrellaInferFromBody() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_UMBRELLA_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void erogazioneResponseSoap11UmbrellaInferFromBody() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_UMBRELLA_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void fruizioneResponseSoap11UmbrellaInferFromBody() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_UMBRELLA_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void erogazioneResponseSoap12UmbrellaInferFromBody() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_UMBRELLA_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void fruizioneResponseSoap12UmbrellaInferFromBody() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_UMBRELLA_INFER_FROM_BODY, Outcome.okCompensated()); }

	// Umbrella inferFromRequest applicato al lato risposta: valido, default-like
	@Test public void erogazioneResponseSoap11UmbrellaInferFromRequest() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_UMBRELLA_INFER_FROM_REQUEST, Outcome.okCompensated()); }
	@Test public void fruizioneResponseSoap11UmbrellaInferFromRequest() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_UMBRELLA_INFER_FROM_REQUEST, Outcome.okCompensated()); }
	@Test public void erogazioneResponseSoap12UmbrellaInferFromRequest() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_UMBRELLA_INFER_FROM_REQUEST, Outcome.okCompensated()); }
	@Test public void fruizioneResponseSoap12UmbrellaInferFromRequest() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_UMBRELLA_INFER_FROM_REQUEST, Outcome.okCompensated()); }

	// Umbrella inferFromRequest applicato al lato richiesta: la strategia non è applicabile
	// (non c'è una richiesta precedente da cui inferire) e viene degradata a 'none'.
	// Il comportamento osservato è quindi identico ai test *None corrispondenti.
	@Test public void erogazioneRequestSoap11UmbrellaInferFromRequest() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_UMBRELLA_INFER_FROM_REQUEST, Outcome.koNoCompensation(DIAG_ERR_REQUEST_NAMESPACE_MISMATCH)); }
	@Test public void fruizioneRequestSoap11UmbrellaInferFromRequest() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_UMBRELLA_INFER_FROM_REQUEST, Outcome.koNoCompensation(DIAG_ERR_REQUEST_NAMESPACE_MISMATCH)); }
	@Test public void erogazioneRequestSoap12UmbrellaInferFromRequest() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_UMBRELLA_INFER_FROM_REQUEST, Outcome.okNoCompensation()); }
	@Test public void fruizioneRequestSoap12UmbrellaInferFromRequest() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_UMBRELLA_INFER_FROM_REQUEST, Outcome.okNoCompensation()); }

	// request.inferFromRequest (property specifica request-side): non applicabile, degradata a 'none'.
	@Test public void erogazioneRequestSoap11InferFromRequestInvalid() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_11, ACTION_REQUEST_INFER_FROM_REQUEST, Outcome.koNoCompensation(DIAG_ERR_REQUEST_NAMESPACE_MISMATCH)); }
	@Test public void fruizioneRequestSoap11InferFromRequestInvalid() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_11, ACTION_REQUEST_INFER_FROM_REQUEST, Outcome.koNoCompensation(DIAG_ERR_REQUEST_NAMESPACE_MISMATCH)); }
	@Test public void erogazioneRequestSoap12InferFromRequestInvalid() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, MessageType.SOAP_12, ACTION_REQUEST_INFER_FROM_REQUEST, Outcome.okNoCompensation()); }
	@Test public void fruizioneRequestSoap12InferFromRequestInvalid() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, MessageType.SOAP_12, ACTION_REQUEST_INFER_FROM_REQUEST, Outcome.okNoCompensation()); }


	/* ===================================================================================== */
	/* ===== Variante con REGISTRAZIONE MESSAGGI attiva (API_REGISTRAZIONE)                ===== */
	/* ===================================================================================== */
	/* La registrazione messaggi coinvolge dump del payload in più punti: questi test         */
	/* verificano che la compensazione (in particolare INFER_FROM_BODY, che modifica lo       */
	/* stream con peek + SequenceInputStream) continui a funzionare correttamente.            */

	// Default
	@Test public void registrazioneErogazioneDefaultRequestSoap11() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_DEFAULT, Outcome.koNoCompensation(DIAG_ERR_REQUEST_NAMESPACE_MISMATCH)); }
	@Test public void registrazioneFruizioneDefaultRequestSoap11() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_DEFAULT, Outcome.koNoCompensation(DIAG_ERR_REQUEST_NAMESPACE_MISMATCH)); }
	@Test public void registrazioneErogazioneDefaultRequestSoap12() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_DEFAULT, Outcome.okNoCompensation()); }
	@Test public void registrazioneFruizioneDefaultRequestSoap12() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_DEFAULT, Outcome.okNoCompensation()); }
	@Test public void registrazioneErogazioneDefaultResponseSoap11() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_DEFAULT, Outcome.okCompensated()); }
	@Test public void registrazioneFruizioneDefaultResponseSoap11() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_DEFAULT, Outcome.okCompensated()); }
	@Test public void registrazioneErogazioneDefaultResponseSoap12() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_DEFAULT, Outcome.okCompensated()); }
	@Test public void registrazioneFruizioneDefaultResponseSoap12() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_DEFAULT, Outcome.okCompensated()); }

	// Request side — SOAP 1.1
	@Test public void registrazioneErogazioneRequestSoap11None() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_REQUEST_NONE, Outcome.koNoCompensation(DIAG_ERR_REQUEST_NAMESPACE_MISMATCH)); }
	@Test public void registrazioneFruizioneRequestSoap11None() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_REQUEST_NONE, Outcome.koNoCompensation(DIAG_ERR_REQUEST_NAMESPACE_MISMATCH)); }
	@Test public void registrazioneErogazioneRequestSoap11InferFromBody() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_REQUEST_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void registrazioneFruizioneRequestSoap11InferFromBody() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_REQUEST_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void registrazioneErogazioneRequestSoap11ForceSoap11() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_REQUEST_FORCE_SOAP11, Outcome.okCompensated()); }
	@Test public void registrazioneFruizioneRequestSoap11ForceSoap11() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_REQUEST_FORCE_SOAP11, Outcome.okCompensated()); }
	@Test public void registrazioneErogazioneRequestSoap11ForceSoap12() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_REQUEST_FORCE_SOAP12, Outcome.koCompensated()); }
	@Test public void registrazioneFruizioneRequestSoap11ForceSoap12() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_REQUEST_FORCE_SOAP12, Outcome.koCompensated()); }

	// Request side — SOAP 1.2
	@Test public void registrazioneErogazioneRequestSoap12None() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_REQUEST_NONE, Outcome.okNoCompensation()); }
	@Test public void registrazioneFruizioneRequestSoap12None() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_REQUEST_NONE, Outcome.okNoCompensation()); }
	@Test public void registrazioneErogazioneRequestSoap12InferFromBody() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_REQUEST_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void registrazioneFruizioneRequestSoap12InferFromBody() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_REQUEST_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void registrazioneErogazioneRequestSoap12ForceSoap11() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_REQUEST_FORCE_SOAP11, Outcome.koCompensated()); }
	@Test public void registrazioneFruizioneRequestSoap12ForceSoap11() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_REQUEST_FORCE_SOAP11, Outcome.koCompensated()); }
	@Test public void registrazioneErogazioneRequestSoap12ForceSoap12() throws Throwable { _testRequest(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_REQUEST_FORCE_SOAP12, Outcome.okCompensated()); }
	@Test public void registrazioneFruizioneRequestSoap12ForceSoap12() throws Throwable { _testRequest(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_REQUEST_FORCE_SOAP12, Outcome.okCompensated()); }

	// Response side — SOAP 1.1
	@Test public void registrazioneErogazioneResponseSoap11None() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_NONE, Outcome.koNoCompensation(DIAG_ERR_RESPONSE_TYPE_MISMATCH)); }
	@Test public void registrazioneFruizioneResponseSoap11None() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_NONE, Outcome.koNoCompensation(DIAG_ERR_RESPONSE_TYPE_MISMATCH)); }
	@Test public void registrazioneErogazioneResponseSoap11InferFromRequest() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_INFER_FROM_REQUEST, Outcome.okCompensated()); }
	@Test public void registrazioneFruizioneResponseSoap11InferFromRequest() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_INFER_FROM_REQUEST, Outcome.okCompensated()); }
	@Test public void registrazioneErogazioneResponseSoap11InferFromBody() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void registrazioneFruizioneResponseSoap11InferFromBody() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void registrazioneErogazioneResponseSoap11ForceSoap11() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_FORCE_SOAP11, Outcome.okCompensated()); }
	@Test public void registrazioneFruizioneResponseSoap11ForceSoap11() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_FORCE_SOAP11, Outcome.okCompensated()); }
	@Test public void registrazioneErogazioneResponseSoap11ForceSoap12() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_FORCE_SOAP12, Outcome.koCompensated()); }
	@Test public void registrazioneFruizioneResponseSoap11ForceSoap12() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_11, ACTION_RESPONSE_FORCE_SOAP12, Outcome.koCompensated()); }

	// Response side — SOAP 1.2
	@Test public void registrazioneErogazioneResponseSoap12None() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_NONE, Outcome.koNoCompensation(DIAG_ERR_UNABLE_TO_INTERNALIZE)); }
	@Test public void registrazioneFruizioneResponseSoap12None() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_NONE, Outcome.koNoCompensation(DIAG_ERR_UNABLE_TO_INTERNALIZE)); }
	@Test public void registrazioneErogazioneResponseSoap12InferFromRequest() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_INFER_FROM_REQUEST, Outcome.okCompensated()); }
	@Test public void registrazioneFruizioneResponseSoap12InferFromRequest() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_INFER_FROM_REQUEST, Outcome.okCompensated()); }
	@Test public void registrazioneErogazioneResponseSoap12InferFromBody() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void registrazioneFruizioneResponseSoap12InferFromBody() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_INFER_FROM_BODY, Outcome.okCompensated()); }
	@Test public void registrazioneErogazioneResponseSoap12ForceSoap11() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_FORCE_SOAP11, Outcome.koCompensated()); }
	@Test public void registrazioneFruizioneResponseSoap12ForceSoap11() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_FORCE_SOAP11, Outcome.koCompensated()); }
	@Test public void registrazioneErogazioneResponseSoap12ForceSoap12() throws Throwable { _testResponse(TipoServizio.EROGAZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_FORCE_SOAP12, Outcome.okCompensated()); }
	@Test public void registrazioneFruizioneResponseSoap12ForceSoap12() throws Throwable { _testResponse(TipoServizio.FRUIZIONE, API_REGISTRAZIONE, MessageType.SOAP_12, ACTION_RESPONSE_FORCE_SOAP12, Outcome.okCompensated()); }


	/* ----------------------------- Engine ----------------------------- */

	/**
	 * Test della compensazione lato RICHIESTA:
	 *  - il client invia un body SOAP-with-Attachments
	 *  - l'header HTTP Content-Type viene riscritto rimuovendo il parametro 'type'
	 *  - l'header Test-Reply-Content-Type è valorizzato col Content-Type *conforme* (mantiene type),
	 *    in modo che il backend echo risponda con un Content-Type ben formato (il test deve
	 *    verificare la compensazione lato richiesta, non quella lato risposta)
	 *  - GovWay deve applicare la strategia configurata per la PA/PD richiamata
	 */
	private void _testRequest(TipoServizio tipo, MessageType soapVersion, String action, Outcome expected) throws Throwable {
		_testRequest(tipo, API, soapVersion, action, expected);
	}
	private void _testRequest(TipoServizio tipo, String api, MessageType soapVersion, String action, Outcome expected) throws Throwable {
		OpenSPCoop2Message msg = buildSoapWithAttachments(soapVersion);
		byte[] body = Bodies.toByteArray(msg);
		String validContentType = msg.getContentType();
		String malformedContentType = stripTypeParameter(validContentType);

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(malformedContentType);
		request.setContent(body);
		request.setUrl(buildUrl(tipo, api, action));
		addSoapActionIfNeeded(request, soapVersion, action);
		// L'echo backend legge questo header per impostare il Content-Type della risposta.
		// Qui passiamo il valore conforme così la risposta resta ben formata e non interferisce
		// con la compensazione testata lato richiesta.
		request.addHeader(HEADER_TEST_REPLY_CONTENT_TYPE, validContentType);

		HttpResponse response = HttpUtilities.httpInvoke(request);
		verify(response, expected, "request side", api, Side.REQUEST, soapVersion, malformedContentType);
	}

	/**
	 * Test della compensazione lato RISPOSTA:
	 *  - il client invia un body SOAP-with-Attachments con Content-Type conforme
	 *  - l'header HTTP custom Test-Reply-Content-Type indica al backend echo quale Content-Type
	 *    *malformato* deve mettere nella risposta (mantenendo lo stesso boundary del body)
	 *  - GovWay deve applicare la strategia configurata per la PA/PD richiamata in fase di
	 *    ricezione risposta
	 */
	private void _testResponse(TipoServizio tipo, MessageType soapVersion, String action, Outcome expected) throws Throwable {
		_testResponse(tipo, API, soapVersion, action, expected);
	}
	private void _testResponse(TipoServizio tipo, String api, MessageType soapVersion, String action, Outcome expected) throws Throwable {
		OpenSPCoop2Message msg = buildSoapWithAttachments(soapVersion);
		byte[] body = Bodies.toByteArray(msg);
		String validContentType = msg.getContentType();
		String malformedContentType = stripTypeParameter(validContentType);

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(validContentType);
		request.setContent(body);
		request.setUrl(buildUrl(tipo, api, action));
		addSoapActionIfNeeded(request, soapVersion, action);
		// Pilota il backend echo a rispondere con il Content-Type malformato (senza parametro 'type').
		request.addHeader(HEADER_TEST_REPLY_CONTENT_TYPE, malformedContentType);

		HttpResponse response = HttpUtilities.httpInvoke(request);
		verify(response, expected, "response side", api, Side.RESPONSE, soapVersion, malformedContentType);
	}


	private OpenSPCoop2Message buildSoapWithAttachments(MessageType soapVersion) throws Throwable {
		if (MessageType.SOAP_11.equals(soapVersion)) {
			return Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE);
		}
		if (MessageType.SOAP_12.equals(soapVersion)) {
			return Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE);
		}
		throw new IllegalArgumentException("MessageType non supportato dal test: " + soapVersion);
	}

	/**
	 * Per SOAP 1.1, l'header HTTP `SOAPAction` è obbligatorio (R1132 WS-I BP 1.1).
	 * Per SOAP 1.2 l'action è invece veicolata come parametro del Content-Type e l'header HTTP non serve.
	 */
	private void addSoapActionIfNeeded(HttpRequest request, MessageType soapVersion, String action) {
		if (MessageType.SOAP_11.equals(soapVersion)) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"" + action + "\"");
		}
	}

	/**
	 * Riscrive un Content-Type 'multipart/related; type="…"; boundary="ABC"' rimuovendo
	 * unicamente il parametro 'type' (preservando boundary e altri parametri).
	 */
	private String stripTypeParameter(String contentType) throws Exception {
		String boundary = ContentTypeUtilities.readMultipartBoundaryFromContentType(contentType);
		assertNotNull("Il Content-Type generato dovrebbe contenere il parametro 'boundary'", boundary);
		return "multipart/related; boundary=\"" + boundary + "\"";
	}

	private String buildUrl(TipoServizio tipo, String api, String action) {
		String base = System.getProperty("govway_base_path");
		if (TipoServizio.EROGAZIONE.equals(tipo)) {
			return base + "/SoggettoInternoTest/" + api + "/v1/" + action;
		}
		return base + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/" + api + "/v1/" + action;
	}

	private void verify(HttpResponse response, Outcome expected, String label,
			String api, Side side, MessageType soapVersion, String malformedContentType) throws Exception {
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull("[" + label + "] manca header GovWay-Transaction-ID nella risposta", idTransazione);

		EsitiProperties esiti = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME);
		long esitoOk = esiti.convertoToCode(EsitoTransazioneName.OK);
		long esitoOkAnomalie = esiti.convertoToCode(EsitoTransazioneName.OK_PRESENZA_ANOMALIE);

		if (expected.ok) {
			assertEquals("[" + label + "] status code atteso 200", 200, response.getResultHTTPOperation());
			// Quando la compensazione viene applicata, il diagnostico dedicato (severity errorIntegration)
			// marca la transazione come OK_PRESENZA_ANOMALIE; altrimenti rimane OK puro.
			long esitoAtteso = expected.compensated ? esitoOkAnomalie : esitoOk;
			DBVerifier.verify(idTransazione, esitoAtteso, null);
		} else {
			if (response.getResultHTTPOperation() == 200) {
				fail("[" + label + "] atteso esito di errore ma il gateway ha risposto 200; idTransazione=" + idTransazione);
			}
			// verifica che l'esito NON sia OK e NON sia OK_PRESENZA_ANOMALIE
			assertEsitoNonOk(idTransazione, esitoOk, esitoOkAnomalie, label);
		}

		// verifica diagnostico di COMPENSAZIONE
		if (expected.compensated) {
			DBVerifier.existsDiagnostico(idTransazione, DIAG_COMPENSATED);
		} else {
			DBVerifier.notExistsDiagnostico(idTransazione, DIAG_COMPENSATED);
		}

		// verifica eventuale diagnostico di ERRORE atteso
		if (expected.expectedErrorDiag != null) {
			DBVerifier.existsDiagnostico(idTransazione, expected.expectedErrorDiag);
		}

		// verifica tabella dump_messaggi
		verifyDump(idTransazione, api, side, expected, soapVersion, malformedContentType, label);
	}

	// La configurazione di registrazione messaggi usa payload-parsing="disabilitato"; in tale modalità
	// GovWay registra il dump come *DumpBinario, che contiene il content-type HTTP completo del multipart.
	// È la modalità adatta a verificare la compensazione, che opera proprio sul Content-Type HTTP.
	private static final String TIPO_RICHIESTA_INGRESSO = "RichiestaIngressoDumpBinario";
	private static final String TIPO_RICHIESTA_USCITA   = "RichiestaUscitaDumpBinario";
	private static final String TIPO_RISPOSTA_INGRESSO  = "RispostaIngressoDumpBinario";
	private static final String TIPO_RISPOSTA_USCITA    = "RispostaUscitaDumpBinario";

	/**
	 * Verifica la presenza/assenza e il content-type nella tabella `dump_messaggi`.
	 *  - API senza registrazione messaggi → nessuna riga in dump_messaggi
	 *  - API con registrazione messaggi:
	 *     - se la compensazione è stata applicata (okCompensated), verifica che sull'ingresso
	 *       del lato testato il content-type sia quello originale (malformato) e sull'uscita
	 *       sia quello "fixato" (originale + parametro 'type' inferito)
	 *     - negli altri casi verifica solo presenza dei 4 dump (RICHIESTA/RISPOSTA × IN/USCITA)
	 */
	private void verifyDump(String idTransazione, String api, Side side, Outcome expected,
			MessageType soapVersion, String malformedContentType, String label) throws Exception {

		boolean registrazioneMessaggi = API_REGISTRAZIONE.equals(api);

		if(!registrazioneMessaggi) {
			DBVerifier.notExistsDumpMessaggi(idTransazione);
			return;
		}

		// Registrazione attiva. La presenza dei dump dipende dal flusso effettivo:
		// - RichiestaIngresso e RispostaUscita sono sempre presenti quando la transazione viene
		//   tracciata in fase di ricezione e si genera una risposta al client (anche un SOAP Fault).
		// - RichiestaUscita e RispostaIngresso sono presenti solo se la richiesta raggiunge davvero
		//   il backend. Lato richiesta in caso di KO (parser fallito sul body dopo la compensazione)
		//   il backend non viene chiamato, quindi quei due dump non esistono.
		DBVerifier.existsDumpMessaggio(idTransazione, TIPO_RICHIESTA_INGRESSO);
		DBVerifier.existsDumpMessaggio(idTransazione, TIPO_RISPOSTA_USCITA);
		boolean backendCalled = expected.ok || Side.RESPONSE.equals(side);
		if(backendCalled) {
			DBVerifier.existsDumpMessaggio(idTransazione, TIPO_RICHIESTA_USCITA);
			DBVerifier.existsDumpMessaggio(idTransazione, TIPO_RISPOSTA_INGRESSO);
		}

		// solo quando la compensazione è effettivamente stata applicata e completata con OK,
		// verifica puntuale dei content-type ingresso (malformato) / uscita (fixato)
		if(!expected.compensated || !expected.ok) {
			return;
		}
		String fixedContentType = buildFixedContentType(malformedContentType, soapVersion);
		String inferredTypeLikePattern = buildFixedContentTypeLikePattern(soapVersion);
		if(Side.REQUEST.equals(side)) {
			// Lato richiesta: GovWay inoltra al backend mantenendo il boundary letto dal messaggio
			// originale (la rigenerazione del MIME riusa lo stesso boundary in uscita).
			DBVerifier.existsDumpMessaggioWithContentType(idTransazione, TIPO_RICHIESTA_INGRESSO, malformedContentType);
			DBVerifier.existsDumpMessaggioWithContentType(idTransazione, TIPO_RICHIESTA_USCITA, fixedContentType);
		}
		else {
			// Lato risposta: la RispostaIngresso conserva il content-type ricevuto dal backend
			// (malformato). La RispostaUscita verso il client è invece riserializzata da GovWay
			// con un NUOVO boundary; verifichiamo solo che sia un multipart/related ben formato
			// con il parametro 'type' atteso, ignorando il boundary specifico.
			DBVerifier.existsDumpMessaggioWithContentType(idTransazione, TIPO_RISPOSTA_INGRESSO, malformedContentType);
			DBVerifier.existsDumpMessaggioWithContentTypeLike(idTransazione, TIPO_RISPOSTA_USCITA, inferredTypeLikePattern);
		}
		assertNotNull("[" + label + "] sanity check", fixedContentType);
	}

	private static String buildFixedContentType(String malformedContentType, MessageType soapVersion) {
		String inferredType = MessageType.SOAP_11.equals(soapVersion)
				? "text/xml"
				: "application/soap+xml";
		return malformedContentType + "; type=\"" + inferredType + "\"";
	}

	/**
	 * Pattern SQL LIKE per il content_type del messaggio in uscita verso il client quando la
	 * compensazione è stata applicata lato risposta. GovWay riserializza il multipart con un
	 * nuovo boundary, quindi verifichiamo solo i tratti deterministici: prefisso 'multipart/related'
	 * e presenza del parametro 'type' con il valore atteso.
	 */
	private static String buildFixedContentTypeLikePattern(MessageType soapVersion) {
		String inferredType = MessageType.SOAP_11.equals(soapVersion)
				? "text/xml"
				: "application/soap+xml";
		return "multipart/related%type=\"" + inferredType + "\"%";
	}

	/**
	 * Asserisce che l'esito DB della transazione NON sia uno dei codici "OK" (né OK puro né OK_PRESENZA_ANOMALIE).
	 * La scrittura della transazione è asincrona: dopo il primo tentativo si riprova con sleep crescenti
	 * per tollerare il lag (transazioni veloci possono ancora non essere state persistite quando il test legge).
	 */
	private static void assertEsitoNonOk(String idTransazione, long esitoOk, long esitoOkAnomalie, String label) throws Exception {
		long[] backoffMs = { 0L, 100L, 500L, 2000L, 5000L };
		for(int attempt=0; attempt<backoffMs.length; attempt++) {
			if(backoffMs[attempt] > 0L) {
				org.openspcoop2.utils.Utilities.sleep(backoffMs[attempt]);
			}
			try {
				assertEsitoNonOkEngine(idTransazione, esitoOk, esitoOkAnomalie, label);
				return;
			} catch(Throwable t) {
				if(attempt == backoffMs.length - 1) {
					throw t;
				}
			}
		}
	}
	private static void assertEsitoNonOkEngine(String idTransazione, long esitoOk, long esitoOkAnomalie, String label) throws Exception {
		String query = "select esito from transazioni where id = ?";
		Long esito = ConfigLoader.getDbUtils().readValue(query, Long.class, idTransazione);
		assertNotNull("[" + label + "] esito non trovato sul DB; idTransazione=" + idTransazione, esito);
		if (esito.longValue() == esitoOk || esito.longValue() == esitoOkAnomalie) {
			fail("[" + label + "] atteso esito di errore ma sul DB risulta '" + esito + "'; idTransazione=" + idTransazione);
		}
	}
}
