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
 * Test di validazione REST per l'API {@code TestValidazioneRestSwagger2}, profilo Swagger 2.0.
 * <p>
 * Per ogni gruppo della porta (default + alternate + incompatible) e per i due tipi servizio
 * (erogazione e fruizione) sono presenti i casi:
 * <ul>
 *   <li>body valido → 200 + esito {@code OK};</li>
 *   <li>body non valido in richiesta (pattern violato) → 400 + esito {@code ERRORE_VALIDAZIONE_RICHIESTA};</li>
 *   <li>body valido + parametro {@code replace} sul backend echo → 502 + esito {@code ERRORE_VALIDAZIONE_RISPOSTA}.</li>
 * </ul>
 * Le risorse {@code /incompatible} hanno una libreria non supportata da Swagger 2 (es. {@code openapi4j}):
 * la porta deve fallire al boot con il diagnostico atteso.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Swagger2Test extends ConfigLoader {

	private static final String API = "TestValidazioneRestSwagger2";

	/** Body Echo valido secondo lo schema Swagger 2.0. */
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

	/** Diagnostico emesso da GovWay quando la libreria configurata non supporta Swagger 2.0. */
	private static final String DIAG_INCOMPATIBILE = "la libreria non supporta Swagger 2.0";

	/* =========================== /default — libreria di default (json_schema) =========================== */

	@Test public void defaultErogazioneOk()          throws Exception { okCase(TipoServizio.EROGAZIONE, "default"); }
	@Test public void defaultErogazioneKoRequest()   throws Exception { koRequestCase(TipoServizio.EROGAZIONE, "default", ValidazioneRestUtils.DETAIL_JSON_SCHEMA); }
	@Test public void defaultErogazioneKoResponse()  throws Exception { koResponseCase(TipoServizio.EROGAZIONE, "default", ValidazioneRestUtils.DETAIL_JSON_SCHEMA); }
	@Test public void defaultFruizioneOk()           throws Exception { okCase(TipoServizio.FRUIZIONE,  "default"); }
	@Test public void defaultFruizioneKoRequest()    throws Exception { koRequestCase(TipoServizio.FRUIZIONE,  "default", ValidazioneRestUtils.DETAIL_JSON_SCHEMA); }
	@Test public void defaultFruizioneKoResponse()   throws Exception { koResponseCase(TipoServizio.FRUIZIONE,  "default", ValidazioneRestUtils.DETAIL_JSON_SCHEMA); }

	/* ===================== /alternate — libreria alternativa compatibile (swagger_request_validator) ===================== */

	@Test public void alternateErogazioneOk()         throws Exception { okCase(TipoServizio.EROGAZIONE, "alternate"); }
	@Test public void alternateErogazioneKoRequest()  throws Exception { koRequestCase(TipoServizio.EROGAZIONE, "alternate", ValidazioneRestUtils.DETAIL_SWAGGER_REQUEST_VALIDATOR); }
	@Test public void alternateErogazioneKoResponse() throws Exception { koResponseCase(TipoServizio.EROGAZIONE, "alternate", ValidazioneRestUtils.DETAIL_SWAGGER_REQUEST_VALIDATOR); }
	@Test public void alternateFruizioneOk()          throws Exception { okCase(TipoServizio.FRUIZIONE,  "alternate"); }
	@Test public void alternateFruizioneKoRequest()   throws Exception { koRequestCase(TipoServizio.FRUIZIONE,  "alternate", ValidazioneRestUtils.DETAIL_SWAGGER_REQUEST_VALIDATOR); }
	@Test public void alternateFruizioneKoResponse()  throws Exception { koResponseCase(TipoServizio.FRUIZIONE,  "alternate", ValidazioneRestUtils.DETAIL_SWAGGER_REQUEST_VALIDATOR); }

	/* ===================== /incompatible — libreria non compatibile (openapi4j) ===================== */

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
