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

import java.util.Base64;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.TokenHttpLibraryMode;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * AttributeAuthorityTestEngine
 *
 * Override della configurazione HTTPS della JVM ({@code connettori.httpsEndpoint.jvmConfigOverride.*})
 * applicato al connettore INTERNO di ATTRIBUTE AUTHORITY, analogo di {@link RestTestEngine} (stessa funzionalita'
 * sul connettore di backend principale).
 *
 * <ul>
 *   <li><b>opt-in</b> (porta o default globale): la chiamata HTTPS verso l'endpoint AA su 8444 riesce ->
 *       attributo 'a2' recuperato -> autorizzazione contenuti soddisfatta -> risposta 200;</li>
 *   <li><b>default</b> (spento): handshake/mutual-TLS verso 8444 non valido -> recupero attributi fallito.
 *       Poiche' il fallimento di un'AA e' non bloccante, la richiesta viene respinta tramite l'autorizzazione
 *       per contenuti sulla porta ({@code ${aa:attributes[a2]}=av2}): attributo assente -> 403.</li>
 * </ul>
 *
 * La porta ha autenticazione basic; l'override, se attivo, fa presentare all'endpoint AA il certificato
 * client corretto (CN=Soggetto2) superandone la mutual-TLS, altrimenti il default JVM (CN=Erogatore) viene negato.
 * La libreria http del connettore interno e' pilotata dall'header {@code GovWay-TestSuite-TokenHttpLibrary}
 * (vedi {@link TokenHttpLibraryMode}): le sottoclassi eseguono la batteria su HttpCore5 (BIO) e UrlConnection.
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AttributeAuthorityTestEngine extends ConfigLoader {

	private static final String API = "TestAttributeAuthorityHttpsOverride";
	private static final String APPLICATIVO = "ApplicativoSoggettoInternoTestFruitore1";
	private static final String APPLICATIVO_PASSWORD = "123456";

	private TokenHttpLibraryMode mode = null;
	protected void setTokenHttpLibraryMode(TokenHttpLibraryMode mode) {
		this.mode = mode;
	}


	@Test
	public void erogazione_optInPorta_configDefault() throws Exception {
		assertOk(invoke(TipoServizio.EROGAZIONE, "optInPortaConfigDefault"));
	}
	@Test
	public void fruizione_optInPorta_configDefault() throws Exception {
		assertOk(invoke(TipoServizio.FRUIZIONE, "optInPortaConfigDefault"));
	}

	@Test
	public void erogazione_optInGlobale() throws Exception {
		assertOk(invoke(TipoServizio.EROGAZIONE, "optInGlobale"));
	}

	@Test
	public void erogazione_default_overrideNonApplicato() throws Exception {
		assertKo(invoke(TipoServizio.EROGAZIONE, "defaultNoOverride"));
	}
	@Test
	public void fruizione_default_overrideNonApplicato() throws Exception {
		assertKo(invoke(TipoServizio.FRUIZIONE, "defaultNoOverride"));
	}


	private HttpResponse invoke(TipoServizio tipoServizio, String operazione) throws Exception {
		Utils.resetCacheToken(logCore);
		Utils.resetCacheAutorizzazione(logCore);
		// gli attributi AA sono cacheati per (endpoint + JWT di richiesta); essendo i claim statici il JWT e' identico
		// tra le invocazioni, quindi senza reset il caso override-off riuserebbe l'esito positivo di un caso opt-in
		Utils.resetCacheAttributeAuthority(logCore);

		String suffix = tipoServizio == TipoServizio.FRUIZIONE
				? "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/" + API + "/v1/" + operazione
				: "/SoggettoInternoTest/" + API + "/v1/" + operazione;
		String url = System.getProperty("govway_base_path") + suffix;

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		String basic = Base64.getEncoder().encodeToString((APPLICATIVO + ":" + APPLICATIVO_PASSWORD).getBytes());
		request.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BASIC + basic);
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

	// Override spento sul connettore AA: recupero attributi fallito -> attributo assente -> autorizzazione contenuti negata (403)
	private void assertKo(HttpResponse response) {
		assertNotNull(response.getHeaderFirstValue("GovWay-Transaction-ID"));
		assertTrue("Attesa risposta di errore (!=200), ottenuto: " + response.getResultHTTPOperation(),
				response.getResultHTTPOperation() != 200);
	}

}
