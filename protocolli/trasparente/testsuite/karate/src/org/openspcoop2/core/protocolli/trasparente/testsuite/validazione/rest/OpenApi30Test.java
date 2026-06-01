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

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * Test di validazione REST per l'API {@code TestValidazioneRestOpenApi30}, profilo OpenAPI 3.0.
 * <p>
 * Sono coperti:
 * <ul>
 *   <li>il gruppo {@code /default} con la libreria di default (openapi4j);</li>
 *   <li>il gruppo {@code /alternate} con override esplicito della nuova proprietà
 *       {@code validation.openApi.30.library};</li>
 *   <li>i 3 gruppi legacy {@code /legacy-openapi4j}, {@code /legacy-json-schema} e
 *       {@code /legacy-swagger-validator} che esercitano i flag deprecati
 *       {@code validation.openApi4j.enabled} / {@code validation.swaggerRequestValidator.enabled};</li>
 *   <li>il gruppo {@code /incompatible} con libreria non compatibile con OpenAPI 3.0 (kappa):
 *       la porta deve fallire al boot con il diagnostico atteso.</li>
 * </ul>
 * Ogni gruppo è esercitato sia da erogazione che da fruizione, e per ciascun caso vengono
 * verificati i tre esiti: ok, ko richiesta, ko risposta.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenApi30Test extends ConfigLoader {

	private static final String API = "TestValidazioneRestOpenApi30";

	/** Body Echo valido secondo lo schema OpenAPI 3.0. */
	private static final byte[] VALID_BODY =
			"{\"code\":\"AB123\",\"name\":\"hello\",\"score\":42.5,\"tags\":[\"alpha\",\"beta\"]}"
					.getBytes();

	/** Body con {@code code} che viola il pattern {@code ^[A-Z]{2}\d{3}$}. */
	private static final byte[] INVALID_REQUEST_BODY =
			"{\"code\":\"xx\",\"name\":\"hello\",\"score\":42.5,\"tags\":[\"alpha\",\"beta\"]}"
					.getBytes();

	/**
	 * Parametro query passato al backend echo per sostituire il valore valido di {@code code} con
	 * uno non conforme al pattern (rompe la response e attiva la validazione lato risposta).
	 */
	private static final String QUERY_BREAK_RESPONSE = "?replace=AB123:xx";

	/** Diagnostico emesso da GovWay quando la libreria configurata non supporta OpenAPI 3.0. */
	private static final String DIAG_INCOMPATIBILE = "la libreria non supporta OpenAPI 3.0";

	/* =========================== /default — libreria di default (openapi4j) =========================== */

	@Test public void defaultErogazioneOk()          throws Exception { okCase(TipoServizio.EROGAZIONE, "default"); }
	@Test public void defaultErogazioneKoRequest()   throws Exception { koRequestCase(TipoServizio.EROGAZIONE, "default", ValidazioneRestUtils.DETAIL_OPENAPI4J); }
	@Test public void defaultErogazioneKoResponse()  throws Exception { koResponseCase(TipoServizio.EROGAZIONE, "default", ValidazioneRestUtils.DETAIL_OPENAPI4J); }
	@Test public void defaultFruizioneOk()           throws Exception { okCase(TipoServizio.FRUIZIONE,  "default"); }
	@Test public void defaultFruizioneKoRequest()    throws Exception { koRequestCase(TipoServizio.FRUIZIONE,  "default", ValidazioneRestUtils.DETAIL_OPENAPI4J); }
	@Test public void defaultFruizioneKoResponse()   throws Exception { koResponseCase(TipoServizio.FRUIZIONE,  "default", ValidazioneRestUtils.DETAIL_OPENAPI4J); }

	/* ===================== /alternate — libreria alternativa via 'validation.openApi.30.library' (swagger_request_validator) ===================== */

	@Test public void alternateErogazioneOk()         throws Exception { okCase(TipoServizio.EROGAZIONE, "alternate"); }
	@Test public void alternateErogazioneKoRequest()  throws Exception { koRequestCase(TipoServizio.EROGAZIONE, "alternate", ValidazioneRestUtils.DETAIL_SWAGGER_REQUEST_VALIDATOR); }
	@Test public void alternateErogazioneKoResponse() throws Exception { koResponseCase(TipoServizio.EROGAZIONE, "alternate", ValidazioneRestUtils.DETAIL_SWAGGER_REQUEST_VALIDATOR); }
	@Test public void alternateFruizioneOk()          throws Exception { okCase(TipoServizio.FRUIZIONE,  "alternate"); }
	@Test public void alternateFruizioneKoRequest()   throws Exception { koRequestCase(TipoServizio.FRUIZIONE,  "alternate", ValidazioneRestUtils.DETAIL_SWAGGER_REQUEST_VALIDATOR); }
	@Test public void alternateFruizioneKoResponse()  throws Exception { koResponseCase(TipoServizio.FRUIZIONE,  "alternate", ValidazioneRestUtils.DETAIL_SWAGGER_REQUEST_VALIDATOR); }

	/* ===================== /legacy-openapi4j — flag legacy 'validation.openApi4j.enabled=true' ===================== */

	@Test public void legacyOpenapi4jErogazioneOk()         throws Exception { okCase(TipoServizio.EROGAZIONE, "legacy-openapi4j"); }
	@Test public void legacyOpenapi4jErogazioneKoRequest()  throws Exception { koRequestCase(TipoServizio.EROGAZIONE, "legacy-openapi4j", ValidazioneRestUtils.DETAIL_OPENAPI4J); }
	@Test public void legacyOpenapi4jErogazioneKoResponse() throws Exception { koResponseCase(TipoServizio.EROGAZIONE, "legacy-openapi4j", ValidazioneRestUtils.DETAIL_OPENAPI4J); }
	@Test public void legacyOpenapi4jFruizioneOk()          throws Exception { okCase(TipoServizio.FRUIZIONE,  "legacy-openapi4j"); }
	@Test public void legacyOpenapi4jFruizioneKoRequest()   throws Exception { koRequestCase(TipoServizio.FRUIZIONE,  "legacy-openapi4j", ValidazioneRestUtils.DETAIL_OPENAPI4J); }
	@Test public void legacyOpenapi4jFruizioneKoResponse()  throws Exception { koResponseCase(TipoServizio.FRUIZIONE,  "legacy-openapi4j", ValidazioneRestUtils.DETAIL_OPENAPI4J); }

	/* ===================== /legacy-json-schema — entrambi i flag legacy a false (forza json_schema) ===================== */

	@Test public void legacyJsonSchemaErogazioneOk()         throws Exception { okCase(TipoServizio.EROGAZIONE, "legacy-json-schema"); }
	@Test public void legacyJsonSchemaErogazioneKoRequest()  throws Exception { koRequestCase(TipoServizio.EROGAZIONE, "legacy-json-schema", ValidazioneRestUtils.DETAIL_JSON_SCHEMA); }
	@Test public void legacyJsonSchemaErogazioneKoResponse() throws Exception { koResponseCase(TipoServizio.EROGAZIONE, "legacy-json-schema", ValidazioneRestUtils.DETAIL_JSON_SCHEMA); }
	@Test public void legacyJsonSchemaFruizioneOk()          throws Exception { okCase(TipoServizio.FRUIZIONE,  "legacy-json-schema"); }
	@Test public void legacyJsonSchemaFruizioneKoRequest()   throws Exception { koRequestCase(TipoServizio.FRUIZIONE,  "legacy-json-schema", ValidazioneRestUtils.DETAIL_JSON_SCHEMA); }
	@Test public void legacyJsonSchemaFruizioneKoResponse()  throws Exception { koResponseCase(TipoServizio.FRUIZIONE,  "legacy-json-schema", ValidazioneRestUtils.DETAIL_JSON_SCHEMA); }

	/* ===================== /legacy-swagger-validator — flag legacy 'validation.swaggerRequestValidator.enabled=true' ===================== */

	@Test public void legacySwaggerValidatorErogazioneOk()         throws Exception { okCase(TipoServizio.EROGAZIONE, "legacy-swagger-validator"); }
	@Test public void legacySwaggerValidatorErogazioneKoRequest()  throws Exception { koRequestCase(TipoServizio.EROGAZIONE, "legacy-swagger-validator", ValidazioneRestUtils.DETAIL_SWAGGER_REQUEST_VALIDATOR); }
	@Test public void legacySwaggerValidatorErogazioneKoResponse() throws Exception { koResponseCase(TipoServizio.EROGAZIONE, "legacy-swagger-validator", ValidazioneRestUtils.DETAIL_SWAGGER_REQUEST_VALIDATOR); }
	@Test public void legacySwaggerValidatorFruizioneOk()          throws Exception { okCase(TipoServizio.FRUIZIONE,  "legacy-swagger-validator"); }
	@Test public void legacySwaggerValidatorFruizioneKoRequest()   throws Exception { koRequestCase(TipoServizio.FRUIZIONE,  "legacy-swagger-validator", ValidazioneRestUtils.DETAIL_SWAGGER_REQUEST_VALIDATOR); }
	@Test public void legacySwaggerValidatorFruizioneKoResponse()  throws Exception { koResponseCase(TipoServizio.FRUIZIONE,  "legacy-swagger-validator", ValidazioneRestUtils.DETAIL_SWAGGER_REQUEST_VALIDATOR); }

	/* ===================== /incompatible — libreria non compatibile (kappa) ===================== */

	@Test public void incompatibleErogazione() throws Exception { incompatibleCase(TipoServizio.EROGAZIONE); }
	@Test public void incompatibleFruizione()  throws Exception { incompatibleCase(TipoServizio.FRUIZIONE); }

	/* =================================== Helper =================================== */

	private void okCase(TipoServizio tipo, String resource) throws Exception {
		String url = ValidazioneRestUtils.buildUrl(tipo, API, resource);
		HttpResponse response = ValidazioneRestUtils.invokePost(url, VALID_BODY);
		ValidazioneRestUtils.verifyOk(response, "[" + tipo + "/" + resource + "/ok]");
	}

	private void koRequestCase(TipoServizio tipo, String resource, String expectedErrorDetail) throws Exception {
		String url = ValidazioneRestUtils.buildUrl(tipo, API, resource);
		HttpResponse response = ValidazioneRestUtils.invokePost(url, INVALID_REQUEST_BODY);
		ValidazioneRestUtils.verifyKoRequest(response, expectedErrorDetail, "[" + tipo + "/" + resource + "/ko-request]");
	}

	private void koResponseCase(TipoServizio tipo, String resource, String expectedErrorDetail) throws Exception {
		String url = ValidazioneRestUtils.buildUrl(tipo, API, resource) + QUERY_BREAK_RESPONSE;
		HttpResponse response = ValidazioneRestUtils.invokePost(url, VALID_BODY);
		ValidazioneRestUtils.verifyKoResponse(response, expectedErrorDetail, "[" + tipo + "/" + resource + "/ko-response]");
	}

	private void incompatibleCase(TipoServizio tipo) throws Exception {
		String url = ValidazioneRestUtils.buildUrl(tipo, API, "incompatible");
		HttpResponse response = ValidazioneRestUtils.invokePost(url, VALID_BODY);
		ValidazioneRestUtils.verifyIncompatible(response, DIAG_INCOMPATIBILE, "[" + tipo + "/incompatible]");
	}
}
