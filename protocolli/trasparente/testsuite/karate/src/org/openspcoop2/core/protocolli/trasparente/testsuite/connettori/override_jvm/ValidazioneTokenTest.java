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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.override_jvm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione.Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * ValidazioneTokenTest
 *
 * Override della configurazione HTTPS della JVM ({@code connettori.httpsEndpoint.jvmConfigOverride.*})
 * applicato ai connettori INTERNI di VALIDAZIONE del token (introspection / userInfo), analogo di
 * {@link RestTest} (stessa funzionalita' sul connettore di backend principale).
 *
 * <ul>
 *   <li><b>opt-in</b> (porta o default globale): la chiamata HTTPS verso l'endpoint di introspection/userInfo
 *       su 8444 riesce -> token validato -> risposta 200;</li>
 *   <li><b>default</b> (spento): handshake TLS verso 8444 fallito -> validazione in errore -> risposta != 200.</li>
 * </ul>
 *
 * Il "token" da validare e' opaco: la risposta attesa dell'endpoint (introspection/userInfo) viene pre-scritta
 * su file (che l'echo riflette). Il corpo JSON e' costruito riusando {@code Utilities.buildJson} (public).
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneTokenTest extends ConfigLoader {

	private static final String API_INTROSPECTION = "TestValidazioneTokenHttpsOverrideIntrospection";
	private static final String API_USERINFO = "TestValidazioneTokenHttpsOverrideUserInfo";

	private static final String TOKEN_OPACO = "access-token-opaco";


	// *** INTROSPECTION ***
	@Test
	public void introspection_erogazione_optInPorta_configDefault() throws Exception {
		assertOk(invokeIntrospection(TipoServizio.EROGAZIONE, "optInPortaConfigDefault"));
	}
	@Test
	public void introspection_fruizione_optInPorta_configDefault() throws Exception {
		assertOk(invokeIntrospection(TipoServizio.FRUIZIONE, "optInPortaConfigDefault"));
	}
	@Test
	public void introspection_erogazione_optInGlobale() throws Exception {
		assertOk(invokeIntrospection(TipoServizio.EROGAZIONE, "optInGlobale"));
	}
	@Test
	public void introspection_erogazione_default_overrideNonApplicato() throws Exception {
		assertKo(invokeIntrospection(TipoServizio.EROGAZIONE, "defaultNoOverride"));
	}
	@Test
	public void introspection_fruizione_default_overrideNonApplicato() throws Exception {
		assertKo(invokeIntrospection(TipoServizio.FRUIZIONE, "defaultNoOverride"));
	}


	// *** USER INFO ***
	@Test
	public void userInfo_erogazione_optInPorta_configDefault() throws Exception {
		assertOk(invokeUserInfo(TipoServizio.EROGAZIONE, "optInPortaConfigDefault"));
	}
	@Test
	public void userInfo_fruizione_optInPorta_configDefault() throws Exception {
		assertOk(invokeUserInfo(TipoServizio.FRUIZIONE, "optInPortaConfigDefault"));
	}
	@Test
	public void userInfo_erogazione_optInGlobale() throws Exception {
		assertOk(invokeUserInfo(TipoServizio.EROGAZIONE, "optInGlobale"));
	}
	@Test
	public void userInfo_erogazione_default_overrideNonApplicato() throws Exception {
		assertKo(invokeUserInfo(TipoServizio.EROGAZIONE, "defaultNoOverride"));
	}
	@Test
	public void userInfo_fruizione_default_overrideNonApplicato() throws Exception {
		assertKo(invokeUserInfo(TipoServizio.FRUIZIONE, "defaultNoOverride"));
	}


	// costruisce il JSON di risposta introspection/userInfo (claim validi) riusando Utilities.buildJson (public)
	private static String buildResponseJson(String prefix) throws Exception {
		return Utilities.buildJson(true,
				true, true, true, true, true,
				true, true, true,
				true, true, true,
				false, null,
				false, null,
				false,
				false,
				false, false,
				false, false,
				null,
				prefix);
	}

	private HttpResponse invokeIntrospection(TipoServizio tipoServizio, String operazione) throws Exception {
		resetCaches();
		FileSystemUtilities.writeFile(new File("/tmp/introspectionResponse.json"), buildResponseJson("TEST").getBytes());
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-introspection", TOKEN_OPACO);
		return invoke(tipoServizio, API_INTROSPECTION, operazione, headers, null);
	}

	private HttpResponse invokeUserInfo(TipoServizio tipoServizio, String operazione) throws Exception {
		resetCaches();
		FileSystemUtilities.writeFile(new File("/tmp/userinfoResponse.json"), buildResponseJson("").getBytes());
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", TOKEN_OPACO);
		return invoke(tipoServizio, API_USERINFO, operazione, headers, query);
	}

	private HttpResponse invoke(TipoServizio tipoServizio, String api, String operazione,
			Map<String, String> headers, Map<String, String> query) throws Exception {
		String suffix = tipoServizio == TipoServizio.FRUIZIONE
				? "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/" + api + "/v1/" + operazione
				: "/SoggettoInternoTest/" + api + "/v1/" + operazione;
		String url = System.getProperty("govway_base_path") + suffix;
		if (query != null && !query.isEmpty()) {
			StringBuilder sb = new StringBuilder(url);
			boolean first = true;
			for (Map.Entry<String, String> e : query.entrySet()) {
				sb.append(first ? "?" : "&").append(e.getKey()).append("=").append(e.getValue());
				first = false;
			}
			url = sb.toString();
		}

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		if (headers != null) {
			for (Map.Entry<String, String> e : headers.entrySet()) {
				request.addHeader(e.getKey(), e.getValue());
			}
		}
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		request.setContent(Bodies.getJson(Bodies.SMALL_SIZE).getBytes());
		request.setUrl(url);
		return HttpUtilities.httpInvoke(request);
	}

	private void resetCaches() throws Exception {
		Utils.resetCacheToken(logCore);
		Utils.resetCacheAutorizzazione(logCore);
	}

	private void assertOk(HttpResponse response) {
		assertNotNull(response.getHeaderFirstValue("GovWay-Transaction-ID"));
		assertEquals(200, response.getResultHTTPOperation());
	}

	// Override spento sul connettore di validazione: la connessione HTTPS verso 8444 fallisce -> esito di errore
	private void assertKo(HttpResponse response) {
		assertNotNull(response.getHeaderFirstValue("GovWay-Transaction-ID"));
		assertTrue("Attesa risposta di errore (!=200), ottenuto: " + response.getResultHTTPOperation(),
				response.getResultHTTPOperation() != 200);
	}

}
