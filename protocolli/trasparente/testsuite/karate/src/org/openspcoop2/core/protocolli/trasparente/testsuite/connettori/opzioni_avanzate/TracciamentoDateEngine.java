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

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
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
 * Engine di verifica della corretta valorizzazione delle 10 date di tracciamento della tabella
 * <code>transazioni</code>:
 * <ol>
 *   <li>data_accettazione_richiesta</li>
 *   <li>data_ingresso_richiesta</li>
 *   <li>data_ingresso_richiesta_stream (valorizzata solo se la richiesta ha un body)</li>
 *   <li>data_uscita_richiesta</li>
 *   <li>data_uscita_richiesta_stream (valorizzata solo se la richiesta ha un body inoltrato al backend)</li>
 *   <li>data_accettazione_risposta</li>
 *   <li>data_ingresso_risposta</li>
 *   <li>data_ingresso_risposta_stream (valorizzata solo se la risposta ha un body)</li>
 *   <li>data_uscita_risposta</li>
 *   <li>data_uscita_risposta_stream</li>
 * </ol>
 * al variare del canale HTTP in uscita (UrlConnection BIO, HttpCore BIO, HttpCore NIO) e delle 4
 * combinazioni richiesta/risposta (con/senza payload).
 * <p>
 * Il canale viene forzato via header <code>GovWay-TestSuite-HttpLibrary</code>
 * (proprieta' <code>org.openspcoop2.pdd.connettori.forceLibraryViaHeader</code>) e, per il NIO,
 * tramite il prefisso di path <code>/async/</code>; entrambi gestiti da {@link HttpLibraryMode#patchRequest}.
 * <p>
 * Backend utilizzato: <code>http://127.0.0.1:8080/TestService</code>
 * <ul>
 *   <li><code>/ping</code> &rarr; risposta senza body</li>
 *   <li><code>/echo?destFile=...&amp;destFileContentType=...</code> &rarr; risposta con body (contenuto del file),
 *       indipendentemente dal metodo HTTP</li>
 * </ul>
 * Le sottoclassi ({@code UrlConnBIO...}, {@code HttpCoreBIO...}, {@code HttpCoreNIO...}) si limitano a
 * impostare la {@link HttpLibraryMode}.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class TracciamentoDateEngine extends ConfigLoader {

	private HttpLibraryMode libraryMode = null;
	protected void setHttpLibraryMode(HttpLibraryMode mode) {
		this.libraryMode = mode;
	}

	private static final String API = "TestTracciamentoDate";

	// /echo con destFile: forza una risposta con body (payload = contenuto del file), sia in GET che in POST
	private static final String ECHO_WITH_BODY =
			"echo?destFile=/etc/govway/testfiles/HelloWorld.json&destFileContentType=application/json";
	// /ping: risposta sempre priva di body
	private static final String PING_NO_BODY = "ping";

	/* ============================ @Test — EROGAZIONE ============================ */

	@Test public void erogazioneGetNoBodyResponseNoBody()   throws Exception { invokeAndVerify(TipoServizio.EROGAZIONE, HttpRequestMethod.GET,  PING_NO_BODY,   false, false); }
	@Test public void erogazioneGetNoBodyResponseBody()     throws Exception { invokeAndVerify(TipoServizio.EROGAZIONE, HttpRequestMethod.GET,  ECHO_WITH_BODY, false, true);  }
	@Test public void erogazionePostBodyResponseNoBody()    throws Exception { invokeAndVerify(TipoServizio.EROGAZIONE, HttpRequestMethod.POST, PING_NO_BODY,   true,  false); }
	@Test public void erogazionePostBodyResponseBody()      throws Exception { invokeAndVerify(TipoServizio.EROGAZIONE, HttpRequestMethod.POST, ECHO_WITH_BODY, true,  true);  }

	/* ============================ @Test — FRUIZIONE ============================ */

	@Test public void fruizioneGetNoBodyResponseNoBody()    throws Exception { invokeAndVerify(TipoServizio.FRUIZIONE,  HttpRequestMethod.GET,  PING_NO_BODY,   false, false); }
	@Test public void fruizioneGetNoBodyResponseBody()      throws Exception { invokeAndVerify(TipoServizio.FRUIZIONE,  HttpRequestMethod.GET,  ECHO_WITH_BODY, false, true);  }
	@Test public void fruizionePostBodyResponseNoBody()     throws Exception { invokeAndVerify(TipoServizio.FRUIZIONE,  HttpRequestMethod.POST, PING_NO_BODY,   true,  false); }
	@Test public void fruizionePostBodyResponseBody()       throws Exception { invokeAndVerify(TipoServizio.FRUIZIONE,  HttpRequestMethod.POST, ECHO_WITH_BODY, true,  true);  }

	/* ============================ engine ============================ */

	private void invokeAndVerify(TipoServizio tipo, HttpRequestMethod method, String resource,
			boolean hasRequestBody, boolean hasResponseBody) throws Exception {

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(method);
		request.setUrl(buildUrl(tipo, resource));

		if (hasRequestBody) {
			request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
			request.setContent(Bodies.getJson(Bodies.SMALL_SIZE).getBytes());
		}

		// forza il canale (libreria http + BIO/NIO)
		if (this.libraryMode != null) {
			this.libraryMode.patchRequest(request);
		}

		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		long esitoOk = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME)
				.convertoToCode(EsitoTransazioneName.OK);

		DBVerifier.verifyDateTracciamento(idTransazione, esitoOk, hasRequestBody, hasResponseBody);
	}

	private String buildUrl(TipoServizio tipo, String resource) {
		String base = System.getProperty("govway_base_path");
		if (TipoServizio.EROGAZIONE.equals(tipo)) {
			return base + "/in/SoggettoInternoTest/" + API + "/v1/" + resource;
		}
		return base + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/" + API + "/v1/" + resource;
	}
}
