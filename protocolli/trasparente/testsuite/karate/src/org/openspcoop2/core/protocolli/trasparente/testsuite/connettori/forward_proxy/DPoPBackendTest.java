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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.forward_proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.token.negoziazione.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione.ValidazioneJWTKeystoreDinamicoTest;
import org.openspcoop2.protocol.sdk.RestMessageSecurityToken;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * DPoPBackendTest
 *
 * Verifica l'interazione tra la funzionalità di forward proxy (govway-proxy) e i due DPoP proof (RFC 9449) generati
 * da GovWay: quello verso il BACKEND e quello di NEGOZIAZIONE verso l'Authorization Server.
 *
 * 1) Forward proxy sul connettore BACKEND (API 'TestForwardProxyNegoziazioneDPoP'):
 *    - il claim 'htu' del DPoP backend deve essere l'URL REALE del backend, non quello del proxy verso cui GovWay invia
 *      fisicamente la richiesta;
 *    - la chiave di cache del DPoP backend deve essere costruita sull'URL reale: due operazioni con path backend reale
 *      differente, stessa policy (stesso access_token -> stesso 'ath') e stesso proxy, NON devono collidere in cache.
 *
 * 2) Forward proxy sulla NEGOZIAZIONE / token retrieve (API 'TestForwardProxyNegoziazioneDPoPRetrieve'):
 *    - la richiesta di negoziazione viene inviata fisicamente al proxy (un echo che ritorna un token "canned" e non valida
 *      il DPoP), mentre la token policy ha un endpoint.url FINTO;
 *    - il claim 'htu' del DPoP di negoziazione deve essere l'endpoint CONFIGURATO (URL finta), non quello del proxy.
 *
 * Il DPoP backend viene letto dall'header che il backend echo rimanda indietro; il DPoP di negoziazione viene letto dal
 * token_info (campo 'request.dpop.token', il proof base64 inviato, sempre presente) e decodificato nel test.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class DPoPBackendTest extends ConfigLoader {

	/** API con forward proxy sul connettore BACKEND e policy di negoziazione token DPoP. */
	private static final String API_BACKEND = "TestForwardProxyNegoziazioneDPoP";

	/** API con forward proxy sulla NEGOZIAZIONE (token retrieve) e token policy con endpoint FINTO. */
	private static final String API_RETRIEVE = "TestForwardProxyNegoziazioneDPoPRetrieve";

	/**
	 * URL reale configurato sul connettore backend (property 'location'). È volutamente un host distinto e non
	 * raggiungibile: la richiesta viene inviata fisicamente al proxy (echo), mentre questo è l'URL che deve comparire
	 * nel claim 'htu' del DPoP backend. Con il bug l'htu conterrebbe invece l'URL del proxy
	 * (http://127.0.0.1:8080/TestService/echo). Il path della risorsa (op1/op2) viene appeso da GovWay.
	 */
	private static final String BACKEND_BASE_URL = "http://backendReale:8080/TestService/echo/";

	/** Endpoint dell'AS verso cui avviene la negoziazione diretta (API backend): htu atteso del DPoP di negoziazione. */
	private static final String AS_ENDPOINT = "http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerDPoPDummy/v1/signedJwt";

	/** Endpoint FINTO configurato nella token policy dell'API_RETRIEVE: htu atteso del DPoP di negoziazione (NON il proxy). */
	private static final String FAKE_ENDPOINT = "http://authServerFinto:8080/signedJwt";

	/** File con il token "canned" restituito dall'echo-proxy della negoziazione (deve combaciare con destFile nella sys-prop govway-proxy del tag Retrieve). */
	private static final String CANNED_TOKEN_FILE = "/tmp/responseNegoziazioneDPoPRetrieve.json";

	/** Header con cui il backend (echo) rinvia indietro il DPoP proof ricevuto: replyHttpHeader=DPoP, replyPrefixHttpHeader=govway-testsuite- */
	private static final String DPOP_REPLY_HEADER = "govway-testsuite-dpop";


	// 1) Forward proxy sul connettore BACKEND

	@Test
	public void erogazioneHtuBackendRealeNonProxy() throws Exception {
		htuBackendRealeNonProxy(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneHtuBackendRealeNonProxy() throws Exception {
		htuBackendRealeNonProxy(TipoServizio.FRUIZIONE);
	}

	/**
	 * Il claim 'htu' del DPoP backend deve essere l'URL reale del backend (path dell'operazione incluso),
	 * NON l'URL del proxy verso cui GovWay invia fisicamente la richiesta.
	 * Si verifica inoltre che l'htu del DPoP di negoziazione (nel token_info) resti l'endpoint dell'AS.
	 */
	private void htuBackendRealeNonProxy(TipoServizio tipoServizio) throws Exception {
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);

		HttpResponse response = invoke(API_BACKEND, tipoServizio, "op1");
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");

		// DPoP backend: htu = URL reale del backend (non proxy)
		String htu = getClaim(getDpopBackend(response), "htu");
		String htuExpected = BACKEND_BASE_URL + "op1";
		assertEquals("HTU backend atteso (URL reale, non proxy) ["+htuExpected+"] trovato ["+htu+"]", htuExpected, htu);

		// DPoP negoziazione: htu = endpoint AS (il forward proxy sul backend non lo influenza)
		verificaHtuNegoziazione(idTransazione, AS_ENDPOINT);
	}


	@Test
	public void erogazioneCacheKeyBackendSuUrlReale() throws Exception {
		cacheKeyBackendSuUrlReale(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneCacheKeyBackendSuUrlReale() throws Exception {
		cacheKeyBackendSuUrlReale(TipoServizio.FRUIZIONE);
	}

	/**
	 * La chiave di cache del DPoP backend deve includere l'URL reale.
	 * Due operazioni con path backend reale differente, stessa policy (stesso access_token -> stesso 'ath') e stesso proxy:
	 * - op1 chiamata due volte -> stesso proof (cache hit, stesso jti);
	 * - op2 -> proof differente, con htu differente (nessuna collisione sulla chiave costruita sul proxy URL).
	 *
	 * Senza il fix la chiave sarebbe costruita sull'URL del proxy (identico per op1 e op2): op2 otterrebbe per
	 * collisione il proof di op1 (stesso jti, htu = path di op1), facendo fallire le asserzioni sottostanti.
	 */
	private void cacheKeyBackendSuUrlReale(TipoServizio tipoServizio) throws Exception {
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);

		// op1: primo accesso (cache miss) e secondo accesso (cache hit -> stesso jti)
		String dpopOp1a = getDpopBackend(invoke(API_BACKEND, tipoServizio, "op1"));
		String htuOp1 = getClaim(dpopOp1a, "htu");
		String jtiOp1 = getClaim(dpopOp1a, "jti");
		assertEquals(BACKEND_BASE_URL + "op1", htuOp1);

		String dpopOp1b = getDpopBackend(invoke(API_BACKEND, tipoServizio, "op1"));
		String jtiOp1b = getClaim(dpopOp1b, "jti");
		assertEquals("Atteso cache hit DPoP backend per op1 (stesso jti)", jtiOp1, jtiOp1b);

		// op2: path backend reale differente -> chiave di cache differente -> proof differente con htu differente
		String dpopOp2 = getDpopBackend(invoke(API_BACKEND, tipoServizio, "op2"));
		String htuOp2 = getClaim(dpopOp2, "htu");
		String jtiOp2 = getClaim(dpopOp2, "jti");

		assertEquals("HTU op2 deve essere l'URL reale di op2", BACKEND_BASE_URL + "op2", htuOp2);
		assertNotEquals("htu op2 deve differire da htu op1 (nessuna collisione sul proxy URL)", htuOp1, htuOp2);
		assertNotEquals("jti op2 deve differire da jti op1 (nessuna collisione di cache key)", jtiOp1, jtiOp2);
	}


	// 2) Forward proxy sulla NEGOZIAZIONE (token retrieve)

	@Test
	public void erogazioneHtuNegoziazioneEndpointNonProxy() throws Exception {
		htuNegoziazioneEndpointNonProxy(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneHtuNegoziazioneEndpointNonProxy() throws Exception {
		htuNegoziazioneEndpointNonProxy(TipoServizio.FRUIZIONE);
	}

	/**
	 * Con forward proxy attivo sulla negoziazione e token policy con endpoint FINTO:
	 * - il proxy è un echo che ritorna un token "canned" (non valida il DPoP), quindi la negoziazione va a buon fine;
	 * - il claim 'htu' del DPoP di negoziazione = endpoint CONFIGURATO (finto), non l'URL del proxy.
	 */
	private void htuNegoziazioneEndpointNonProxy(TipoServizio tipoServizio) throws Exception {
		File f = new File(CANNED_TOKEN_FILE);
		try {
			org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);

			// token "canned" restituito dall'echo-proxy della negoziazione (come negli altri test forward_proxy)
			FileSystemUtilities.deleteFile(f);
			String token = ValidazioneJWTKeystoreDinamicoTest.buildJWT(new ArrayList<>());
			String json = "{\"access_token\":\""+token+"\",\"token_type\":\"example\",\"expires_in\":3600,\"refresh_token\":\"tGzv3JOkF0XG5Qx2TlKWIA\"}";
			FileSystemUtilities.writeFile(f, json.getBytes());

			HttpResponse response = invoke(API_RETRIEVE, tipoServizio, "op1");
			String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");

			verificaHtuNegoziazione(idTransazione, FAKE_ENDPOINT);
		} finally {
			FileSystemUtilities.deleteFile(f);
		}
	}


	// Helpers

	/** Invoca l'API e restituisce la risposta (200, con Transaction-ID). */
	private HttpResponse invoke(String api, TipoServizio tipoServizio, String operazione) throws Exception {
		String suffix = tipoServizio == TipoServizio.FRUIZIONE
				? "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+operazione
				: "/SoggettoInternoTest/"+api+"/v1/"+operazione;
		String url = System.getProperty("govway_base_path") + suffix;

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		request.setContent(Bodies.getJson(Bodies.SMALL_SIZE).getBytes());
		request.setUrl(url);

		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());
		assertNotNull(response.getHeaderFirstValue("GovWay-Transaction-ID"));
		return response;
	}

	private String getDpopBackend(HttpResponse response) {
		String dpop = response.getHeaderFirstValue(DPOP_REPLY_HEADER);
		assertNotNull("DPoP backend proof non rinviato dal backend (header '"+DPOP_REPLY_HEADER+"')", dpop);
		return dpop;
	}

	private String getClaim(String dpopProof, String claim) throws Exception {
		RestMessageSecurityToken r = new RestMessageSecurityToken();
		r.setToken(dpopProof);
		String value = r.getPayloadClaim(claim);
		assertNotNull("Claim '"+claim+"' non presente nel DPoP proof", value);
		return value;
	}

	/**
	 * Verifica l'htu del DPoP di NEGOZIAZIONE leggendo il proof inviato dal token_info (request.dpop.token, base64) e
	 * decodificandolo: non dipende dalla normalizzazione delle info DPoP né dalla riflessione lato AS.
	 */
	private void verificaHtuNegoziazione(String idTransazione, String htuAtteso) throws Exception {
		String tokenInfo = DBVerifier.readTokenInfo(idTransazione);
		assertNotNull("token_info assente per la transazione "+idTransazione, tokenInfo);

		JsonNode root = JSONUtils.getInstance().getAsNode(tokenInfo);
		String proof = root.path("request").path("dpop").path("token").asText(null);
		assertNotNull("Proof DPoP di negoziazione assente nel token_info (request.dpop.token): "+tokenInfo, proof);

		String htu = getClaim(proof, "htu");
		assertEquals("HTU negoziazione atteso ["+htuAtteso+"] trovato ["+htu+"]", htuAtteso, htu);
	}
}
