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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.TokenHttpLibraryMode;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * NegoziazioneTokenTestEngine
 *
 * Override della configurazione HTTPS della JVM ({@code connettori.httpsEndpoint.jvmConfigOverride.*})
 * applicato al connettore INTERNO di NEGOZIAZIONE del token (analogo di {@link RestTestEngine}, che verifica
 * la stessa funzionalita' sul connettore di backend principale).
 *
 * <ul>
 *   <li><b>opt-in</b> (porta o default globale): l'override si applica -> negoziazione verso l'endpoint
 *       HTTPS su 8444 riuscita -> risposta 200;</li>
 *   <li><b>default</b> (spento): l'override non si applica -> handshake TLS verso 8444 fallito
 *       (truststore JVM di default) -> negoziazione in errore -> risposta != 200.</li>
 * </ul>
 *
 * La libreria http del connettore interno e' pilotata dall'header {@code GovWay-TestSuite-TokenHttpLibrary}
 * (vedi {@link TokenHttpLibraryMode}): le sottoclassi concrete eseguono la stessa batteria di test su HttpCore5 (BIO)
 * e su UrlConnection.
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class NegoziazioneTokenTestEngine extends ConfigLoader {

	private static final String API = "TestNegoziazioneTokenHttpsOverride";

	// Applicativo fruitore autenticato per lo scenario con config file dinamico
	// (valorizza ${busta:servizioApplicativoFruitore} -> erogazione_/fruizione_<applicativo>.properties)
	private static final String APPLICATIVO_FRUITORE = "ApplicativoSoggettoInternoTestFruitore1";
	private static final String APPLICATIVO_PASSWORD = "123456";

	private TokenHttpLibraryMode mode = null;
	protected void setTokenHttpLibraryMode(TokenHttpLibraryMode mode) {
		this.mode = mode;
	}


	// *** opt-in di porta, config file dal naming di default (busta) ***
	@Test
	public void erogazione_optInPorta_configDefault() throws Exception {
		assertOk(invoke(TipoServizio.EROGAZIONE, "optInPortaConfigDefault", null));
	}
	@Test
	public void fruizione_optInPorta_configDefault() throws Exception {
		assertOk(invoke(TipoServizio.FRUIZIONE, "optInPortaConfigDefault", null));
	}

	// *** opt-in di porta, config file dinamico (applicativo fruitore autenticato) ***
	@Test
	public void erogazione_optInPorta_configDinamico() throws Exception {
		assertOk(invoke(TipoServizio.EROGAZIONE, "optInPortaConfigDinamico", basicAuthHeaders()));
	}
	@Test
	public void fruizione_optInPorta_configDinamico() throws Exception {
		assertOk(invoke(TipoServizio.FRUIZIONE, "optInPortaConfigDinamico", basicAuthHeaders()));
	}

	// *** opt-in tramite default globale ***
	@Test
	public void erogazione_optInGlobale() throws Exception {
		assertOk(invoke(TipoServizio.EROGAZIONE, "optInGlobale", null));
	}
	@Test
	public void fruizione_optInGlobale() throws Exception {
		assertOk(invoke(TipoServizio.FRUIZIONE, "optInGlobale", null));
	}

	// *** default: override NON applicato -> negoziazione in errore ***
	@Test
	public void erogazione_default_overrideNonApplicato() throws Exception {
		assertKo(invoke(TipoServizio.EROGAZIONE, "defaultNoOverride", null));
	}
	@Test
	public void fruizione_default_overrideNonApplicato() throws Exception {
		assertKo(invoke(TipoServizio.FRUIZIONE, "defaultNoOverride", null));
	}


	private Map<String, String> basicAuthHeaders() {
		Map<String, String> headers = new HashMap<>();
		String basic = Base64.getEncoder().encodeToString((APPLICATIVO_FRUITORE + ":" + APPLICATIVO_PASSWORD).getBytes());
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BASIC + basic);
		return headers;
	}

	// Risposta del token endpoint: pre-scritta su /tmp e restituita dall'echo (destFile), come per introspection/userInfo
	private static final String TOKEN_RESPONSE = "{\"access_token\":\"2YotnFZFEjr1zCsicMWpAA\",\"token_type\":\"Bearer\",\"expires_in\":3600,\"scope\":\"scope1\"}";

	private HttpResponse invoke(TipoServizio tipoServizio, String operazione, Map<String, String> headers) throws Exception {
		Utils.resetCacheToken(logCore);
		FileSystemUtilities.writeFile(new File("/tmp/negoziazioneTokenResponse.json"), TOKEN_RESPONSE.getBytes());

		String suffix = tipoServizio == TipoServizio.FRUIZIONE
				? "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/" + API + "/v1/" + operazione
				: "/SoggettoInternoTest/" + API + "/v1/" + operazione;
		String url = System.getProperty("govway_base_path") + suffix;

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
		if (this.mode != null) {
			this.mode.patchRequest(request);
		}
		return HttpUtilities.httpInvoke(request);
	}

	private void assertOk(HttpResponse response) {
		assertNotNull(response.getHeaderFirstValue("GovWay-Transaction-ID"));
		assertEquals(200, response.getResultHTTPOperation());
	}

	// Override spento sul connettore di negoziazione: la connessione HTTPS verso 8444 fallisce -> esito di errore
	private void assertKo(HttpResponse response) {
		assertNotNull(response.getHeaderFirstValue("GovWay-Transaction-ID"));
		assertTrue("Attesa risposta di errore (!=200), ottenuto: " + response.getResultHTTPOperation(),
				response.getResultHTTPOperation() != 200);
	}

}
