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
 * Test di validazione REST per l'API {@code TestValidazioneRestSwagger2External}: spec Swagger 2.0
 * con uno schema referenziato via {@code $ref} cross-file (allegato).
 * <p>
 * La porta principale forza la libreria {@code swagger_request_validator} via property
 * {@code validation.swagger.2.library=swagger_request_validator} (il default {@code json_schema} non
 * supporta la risoluzione di $ref cross-file). Le due risorse:
 * <ul>
 *   <li>{@code /default-merge}: pre-merge dell'allegato in fase di setup
 *       ({@code mergeAPISpec=true}, default);</li>
 *   <li>{@code /default-no-merge}: porta {@code Specific1} che attiva
 *       {@code validation.openApi.mergeAPISpec=false}; la risoluzione del $ref cross-file
 *       avviene a runtime nel motore.</li>
 * </ul>
 * Entrambi i percorsi devono validare con successo il body completo e fallire allo stesso modo se
 * il body viola il pattern del campo {@code code} (validazione richiesta) o se la risposta del
 * backend viene alterata via {@code ?replace=AB123:xx} (validazione risposta).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Swagger2ExternalTest extends ConfigLoader {

	private static final String API = "TestValidazioneRestSwagger2External";

	/** Body valido secondo {@code EchoExternal}: code+name+details (con $ref cross-file su Details). */
	private static final byte[] VALID_BODY =
			"{\"code\":\"AB123\",\"name\":\"hello\",\"details\":{\"category\":\"warning\",\"priority\":3,\"note\":\"ok\"}}"
					.getBytes();

	/** Body con {@code code} che viola il pattern {@code ^[A-Z]{2}\d{3}$}. */
	private static final byte[] INVALID_REQUEST_BODY =
			"{\"code\":\"xx\",\"name\":\"hello\",\"details\":{\"category\":\"warning\",\"priority\":3,\"note\":\"ok\"}}"
					.getBytes();

	/** Sostituisce nella response il code valido con uno non conforme al pattern (ko-response). */
	private static final String QUERY_BREAK_RESPONSE = "?replace=AB123:xx";

	/* ===================== /default-merge — mergeAPISpec=true (default) ===================== */

	@Test public void defaultMergeErogazioneOk()          throws Exception { okCase(TipoServizio.EROGAZIONE, "default-merge"); }
	@Test public void defaultMergeErogazioneKoRequest()   throws Exception { koRequestCase(TipoServizio.EROGAZIONE, "default-merge"); }
	@Test public void defaultMergeErogazioneKoResponse()  throws Exception { koResponseCase(TipoServizio.EROGAZIONE, "default-merge"); }
	@Test public void defaultMergeFruizioneOk()           throws Exception { okCase(TipoServizio.FRUIZIONE,  "default-merge"); }
	@Test public void defaultMergeFruizioneKoRequest()    throws Exception { koRequestCase(TipoServizio.FRUIZIONE,  "default-merge"); }
	@Test public void defaultMergeFruizioneKoResponse()   throws Exception { koResponseCase(TipoServizio.FRUIZIONE,  "default-merge"); }

	/* ===================== /default-no-merge — mergeAPISpec=false port-level ===================== */

	@Test public void defaultNoMergeErogazioneOk()         throws Exception { okCase(TipoServizio.EROGAZIONE, "default-no-merge"); }
	@Test public void defaultNoMergeErogazioneKoRequest()  throws Exception { koRequestCase(TipoServizio.EROGAZIONE, "default-no-merge"); }
	@Test public void defaultNoMergeErogazioneKoResponse() throws Exception { koResponseCase(TipoServizio.EROGAZIONE, "default-no-merge"); }
	@Test public void defaultNoMergeFruizioneOk()          throws Exception { okCase(TipoServizio.FRUIZIONE,  "default-no-merge"); }
	@Test public void defaultNoMergeFruizioneKoRequest()   throws Exception { koRequestCase(TipoServizio.FRUIZIONE,  "default-no-merge"); }
	@Test public void defaultNoMergeFruizioneKoResponse()  throws Exception { koResponseCase(TipoServizio.FRUIZIONE,  "default-no-merge"); }

	/* =================================== Helper =================================== */

	private void okCase(TipoServizio tipo, String resource) throws Exception {
		String url = ValidazioneRestUtils.buildUrl(tipo, API, resource);
		HttpResponse response = ValidazioneRestUtils.invokePost(url, VALID_BODY);
		ValidazioneRestUtils.verifyOk(response, "[" + tipo + "/" + resource + "/ok]");
	}

	private void koRequestCase(TipoServizio tipo, String resource) throws Exception {
		String url = ValidazioneRestUtils.buildUrl(tipo, API, resource);
		HttpResponse response = ValidazioneRestUtils.invokePost(url, INVALID_REQUEST_BODY);
		ValidazioneRestUtils.verifyKoRequest(response, ValidazioneRestUtils.DETAIL_SWAGGER_REQUEST_VALIDATOR,
				"[" + tipo + "/" + resource + "/ko-request]");
	}

	private void koResponseCase(TipoServizio tipo, String resource) throws Exception {
		String url = ValidazioneRestUtils.buildUrl(tipo, API, resource) + QUERY_BREAK_RESPONSE;
		HttpResponse response = ValidazioneRestUtils.invokePost(url, VALID_BODY);
		ValidazioneRestUtils.verifyKoResponse(response, ValidazioneRestUtils.DETAIL_SWAGGER_REQUEST_VALIDATOR,
				"[" + tipo + "/" + resource + "/ko-response]");
	}
}
