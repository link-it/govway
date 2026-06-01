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
package org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
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
 * Utilities condivise dai test del package {@code validazione.rest}.
 * Le API esercitate sono {@code TestValidazioneRestSwagger2}, {@code TestValidazioneRestOpenApi30}
 * e {@code TestValidazioneRestOpenApi31}: hanno la stessa struttura di risorse {@code /default},
 * {@code /alternate}, {@code /incompatible} (più 3 risorse legacy solo per OpenAPI 3.0).
 * Il backend è il servlet {@code TestService/echo} che fa echo del body; supporta il parametro
 * query {@code ?replace=<old>:<new>} per alterare la response e poter validare il path di
 * validazione lato risposta.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneRestUtils {

	private ValidazioneRestUtils() {}

	/* ---------------------------- URL builder ---------------------------- */

	/**
	 * Costruisce l'URL della risorsa esposta da GovWay in base al tipo servizio
	 * (erogazione o fruizione) e al nome API.
	 */
	public static String buildUrl(TipoServizio tipo, String api, String resource) {
		String base = System.getProperty("govway_base_path");
		if (TipoServizio.EROGAZIONE.equals(tipo)) {
			return base + "/in/SoggettoInternoTest/" + api + "/v1/" + resource;
		}
		return base + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/" + api + "/v1/" + resource;
	}

	/* ---------------------------- Invocazione ---------------------------- */

	/** POST application/json verso GovWay; ritorna la response. */
	public static HttpResponse invokePost(String url, byte[] body) throws Exception {
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		request.setContent(body);
		request.setUrl(url);
		return HttpUtilities.httpInvoke(request);
	}

	/* ---------------------------- Verifica esiti ---------------------------- */

	/** Diagnostico emesso quando GovWay avvia la validazione della richiesta. */
	public static final String DIAG_REQ_IN_CORSO  = "Validazione [interface] del contenuto applicativo della richiesta in corso";
	/** Diagnostico emesso a fine validazione richiesta riuscita. */
	public static final String DIAG_REQ_OK        = "Validazione [interface] del contenuto applicativo della richiesta completata con successo";
	/** Diagnostico emesso a fine validazione richiesta fallita (preceduto dal dettaglio dell'errore specifico). */
	public static final String DIAG_REQ_KO        = "Validazione [interface] del contenuto applicativo della richiesta fallita";

	/** Diagnostico emesso quando GovWay avvia la validazione della risposta. */
	public static final String DIAG_RESP_IN_CORSO = "Validazione [interface] del contenuto applicativo della risposta in corso";
	/** Diagnostico emesso a fine validazione risposta riuscita. */
	public static final String DIAG_RESP_OK       = "Validazione [interface] del contenuto applicativo della risposta completata con successo";
	/** Diagnostico emesso a fine validazione risposta fallita (preceduto dal dettaglio dell'errore specifico). */
	public static final String DIAG_RESP_KO       = "Validazione [interface] del contenuto applicativo della risposta fallita";

	/** Wrapper di GovWay che precede l'errore specifico del motore nel diagnostico di fallimento richiesta. */
	public static final String DIAG_REQ_NOT_CONFORM  = "Request content not conform to API specification";
	/** Wrapper di GovWay che precede l'errore specifico del motore nel diagnostico di fallimento risposta. */
	public static final String DIAG_RESP_NOT_CONFORM = "Response content not conform to API specification";

	/* ---------------------------- Dettagli errore per motore ----------------------------
	 * Frammento di stringa atteso nel messaggio specifico dell'engine quando il pattern del
	 * campo 'code' è violato. Le stringhe sono shared tra le diverse classi di test poiché il
	 * messaggio dipende solo dalla libreria, non dal formato dello spec.
	 */

	/*
	 * NB: i frammenti si fermano volutamente a '^[A-Z]{2}', omettendo il '\d{3}$' del pattern
	 * '^[A-Z]{2}\d{3}$'. Vengono cercati su DB con 'messaggio LIKE ?' e il backslash di '\d' e'
	 * carattere di escape di default nel LIKE di PostgreSQL/MySQL (mentre e' letterale su Oracle):
	 * includerlo farebbe fallire il match a seconda del database. '^[A-Z]{2}' e' gia' specifico per
	 * il campo 'code' e non contiene backslash.
	 */

	/** Dettaglio json_schema su pattern violato (uguale per request e response, comune a Swagger 2.0 / OpenAPI 3.0 / OpenAPI 3.1). */
	public static final String DETAIL_JSON_SCHEMA = "$.code: does not match the regex pattern ^[A-Z]{2}";

	/** Dettaglio swagger_request_validator su pattern violato (parte comune a request e response, comune a Swagger 2.0 / OpenAPI 3.0). */
	public static final String DETAIL_SWAGGER_REQUEST_VALIDATOR = "[Path '/code'] ECMA 262 regex \"^[A-Z]{2}";

	/** Dettaglio openapi4j su pattern violato (usato per OpenAPI 3.0: /default e /legacy-openapi4j). */
	public static final String DETAIL_OPENAPI4J = "body.code: 'xx' does not respect pattern '^[A-Z]{2}";

	/** Dettaglio kappa su pattern violato (usato per OpenAPI 3.1: /default). */
	public static final String DETAIL_KAPPA = "body.code: instance value did not match pattern ^[A-Z]{2}";

	/**
	 * Verifica esito OK: HTTP 200 + esito {@code OK} sul DB + i 4 diagnostici di apertura/chiusura
	 * della validazione richiesta e risposta.
	 */
	public static void verifyOk(HttpResponse response, String label) throws Exception {
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(label + " manca GovWay-Transaction-ID", idTransazione);
		assertEquals(label + " status code atteso 200", 200, response.getResultHTTPOperation());

		long esitoOk = EsitiProperties
				.getInstanceFromProtocolName(ConfigLoader.getLoggerCore(), Costanti.TRASPARENTE_PROTOCOL_NAME)
				.convertoToCode(EsitoTransazioneName.OK);
		DBVerifier.verify(idTransazione, esitoOk);

		// La validazione è stata invocata e ha avuto successo su richiesta e risposta.
		DBVerifier.existsDiagnostico(idTransazione, DIAG_REQ_IN_CORSO);
		DBVerifier.existsDiagnostico(idTransazione, DIAG_REQ_OK);
		DBVerifier.existsDiagnostico(idTransazione, DIAG_RESP_IN_CORSO);
		DBVerifier.existsDiagnostico(idTransazione, DIAG_RESP_OK);
		// Nessun fallimento.
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_REQ_KO);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_RESP_KO);
	}

	/**
	 * Verifica esito KO richiesta: HTTP 400 + esito {@code ERRORE_VALIDAZIONE_RICHIESTA} sul DB +
	 * diagnostico {@link #DIAG_REQ_KO} + wrapper {@link #DIAG_REQ_NOT_CONFORM} + dettaglio
	 * specifico del motore di validazione. Il flusso si interrompe alla validazione richiesta, quindi
	 * la validazione risposta non parte (no diagnostici {@code DIAG_RESP_*}).
	 *
	 * @param expectedErrorDetail frammento di stringa atteso nel messaggio dettaglio specifico del
	 *                            motore (es. {@code $.code: does not match the regex pattern});
	 *                            <strong>obbligatorio</strong>: se {@code null}/blank il test fallisce
	 *                            esplicitamente per ricordare di valorizzare la costante.
	 */
	public static void verifyKoRequest(HttpResponse response, String expectedErrorDetail, String label) throws Exception {
		requireDetail(expectedErrorDetail, label);

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(label + " manca GovWay-Transaction-ID", idTransazione);
		assertEquals(label + " status code atteso 400", 400, response.getResultHTTPOperation());

		long esitoKoReq = EsitiProperties
				.getInstanceFromProtocolName(ConfigLoader.getLoggerCore(), Costanti.TRASPARENTE_PROTOCOL_NAME)
				.convertoToCode(EsitoTransazioneName.ERRORE_VALIDAZIONE_RICHIESTA);
		DBVerifier.verify(idTransazione, esitoKoReq, DIAG_REQ_KO);

		// Wrapper di GovWay + dettaglio engine-specific (entrambi obbligatori).
		DBVerifier.existsDiagnostico(idTransazione, DIAG_REQ_NOT_CONFORM);
		DBVerifier.existsDiagnostico(idTransazione, expectedErrorDetail);

		// La validazione richiesta è stata invocata ed è fallita: niente diagnostico di successo
		// né apertura/chiusura della validazione risposta.
		DBVerifier.existsDiagnostico(idTransazione, DIAG_REQ_IN_CORSO);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_REQ_OK);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_RESP_IN_CORSO);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_RESP_OK);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_RESP_KO);
	}

	/**
	 * Verifica esito KO risposta: HTTP 502 + esito {@code ERRORE_VALIDAZIONE_RISPOSTA} sul DB +
	 * diagnostico {@link #DIAG_RESP_KO} + wrapper {@link #DIAG_RESP_NOT_CONFORM} + dettaglio
	 * specifico del motore di validazione. La richiesta è stata validata con successo e poi la
	 * risposta dal backend ha fatto fallire la validazione.
	 *
	 * @param expectedErrorDetail frammento di stringa atteso nel messaggio dettaglio specifico del
	 *                            motore (es. {@code $.code: does not match the regex pattern});
	 *                            <strong>obbligatorio</strong>: se {@code null}/blank il test fallisce
	 *                            esplicitamente per ricordare di valorizzare la costante.
	 */
	public static void verifyKoResponse(HttpResponse response, String expectedErrorDetail, String label) throws Exception {
		requireDetail(expectedErrorDetail, label);

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(label + " manca GovWay-Transaction-ID", idTransazione);
		assertEquals(label + " status code atteso 502", 502, response.getResultHTTPOperation());

		long esitoKoResp = EsitiProperties
				.getInstanceFromProtocolName(ConfigLoader.getLoggerCore(), Costanti.TRASPARENTE_PROTOCOL_NAME)
				.convertoToCode(EsitoTransazioneName.ERRORE_VALIDAZIONE_RISPOSTA);
		DBVerifier.verify(idTransazione, esitoKoResp, DIAG_RESP_KO);

		// Wrapper di GovWay + dettaglio engine-specific (entrambi obbligatori).
		DBVerifier.existsDiagnostico(idTransazione, DIAG_RESP_NOT_CONFORM);
		DBVerifier.existsDiagnostico(idTransazione, expectedErrorDetail);

		// Richiesta validata con successo, risposta validata con fallimento.
		DBVerifier.existsDiagnostico(idTransazione, DIAG_REQ_IN_CORSO);
		DBVerifier.existsDiagnostico(idTransazione, DIAG_REQ_OK);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_REQ_KO);
		DBVerifier.existsDiagnostico(idTransazione, DIAG_RESP_IN_CORSO);
		DBVerifier.notExistsDiagnostico(idTransazione, DIAG_RESP_OK);
	}

	/**
	 * Verifica configurazione libreria non compatibile col formato dello spec: la porta deve fallire
	 * al boot. Si verifica che la transazione abbia un diagnostico con il pattern atteso (es.
	 * {@code la libreria non supporta OpenAPI 3.0}).
	 */
	public static void verifyIncompatible(HttpResponse response, String diagPattern, String label) throws Exception {
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(label + " manca GovWay-Transaction-ID", idTransazione);
		DBVerifier.existsDiagnostico(idTransazione, diagPattern);
	}

	/**
	 * Fallisce esplicitamente se il dettaglio engine-specific atteso non è valorizzato. Serve a
	 * forzare la popolazione delle costanti {@code DETAIL_*} nei singoli test prima di poterli
	 * considerare passanti.
	 */
	private static void requireDetail(String expectedErrorDetail, String label) {
		if (expectedErrorDetail == null || expectedErrorDetail.isBlank()) {
			fail(label + " expectedErrorDetail non valorizzato: estrarre dal log il messaggio specifico"
					+ " della libreria di validazione e popolare la costante DETAIL_<engine>");
		}
	}
}
