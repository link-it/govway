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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.negoziazione;

import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.protocol.sdk.RestMessageSecurityToken;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* AuthorizationServerTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class NegoziazioneDPoPTest extends ConfigLoader {

	public static final String api_negoziazione_dpop = "TestNegoziazioneTokenDPoP";
	
	@Test
	public void clientidClientsecretRS256() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		HttpResponse response = NegoziazioneTest._test(logCore, api_negoziazione_dpop, "clientid_clientsecret", null,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"clientCredentials\"",
				"\"clientId\":\"ClientIDVerificaNegoziazioneConSecret\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerDPoPDummy/v1/signedJwt\"",
				"\"accessToken\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"policy\":\"TestNegoziazioneClientCredentialsDPoP\"",
				"\"tokenType\":\"DPoP\"",
				"\"expires_in\":\"3600\"",
				"\"expiresIn\":1",
				"\"retrievedIn\":1",
				"\"retrievedRefreshTokenIn\":1",
				"\"example_parameter\":\"example_value\"",
				"\"request\":",
				"\"prepareRequest\":1",
				"\"sendRequest\":1",
				"\"receiveResponse\":1",
				"\"parseResponse\":1",
				"\"processComplete\":1",
				"\"dpop\":",
				"\"dpopBackend\":"
				);
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		String dpopBakckend = response.getHeaderFirstValue("govway-testsuite-dpop");
		String dpopJti = verificaDPoPBackend(dpopBakckend, "clientid_clientsecret", "RS256", null, true);
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache ma un differente DPoP Token
		
		response = NegoziazioneTest._test(logCore, api_negoziazione_dpop, "clientid_clientsecret", null,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"clientCredentials\"",
				"\"clientId\":\"ClientIDVerificaNegoziazioneConSecret\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerDPoPDummy/v1/signedJwt\"",
				"\"accessToken\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"policy\":\"TestNegoziazioneClientCredentialsDPoP\"",
				"\"tokenType\":\"DPoP\"",
				"\"expires_in\":\"3600\"",
				"\"expiresIn\":1",
				"\"retrievedIn\":1",
				"\"retrievedRefreshTokenIn\":1",
				"\"example_parameter\":\"example_value\"",
				"\"request\":",
				"\"prepareRequest\":1",
				"\"sendRequest\":1",
				"\"receiveResponse\":1",
				"\"parseResponse\":1",
				"\"processComplete\":1",
				"\"transactionId\":\""+idRichiestaOriginale+"\"",
				"\"dpop\":",
				"\"dpopBackend\":"
				);

		dpopBakckend = response.getHeaderFirstValue("govway-testsuite-dpop");
		verificaDPoPBackend(dpopBakckend, "clientid_clientsecret", "RS256", dpopJti, false);
	}
	
	
	
	
	
	@Test
	public void userPasswordPS256ConCache() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", "ResourceOwnerPasswordUsername");
		
		HttpResponse response = NegoziazioneTest._test(logCore, api_negoziazione_dpop, "user_password", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"usernamePassword\"",
				"\"username\":\"ResourceOwnerPasswordUsername\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerDPoPDummy/v1/signedJwt2\"",
				"\"accessToken\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"policy\":\"TestNegoziazioneOwnerPasswordDPoP\"",
				"\"tokenType\":\"DPoP\"",
				"\"expires_in\":\"3600\"",
				"\"expiresIn\":1",
				"\"retrievedIn\":1",
				"\"retrievedRefreshTokenIn\":1",
				"\"example_parameter\":\"example_value\"",
				"\"request\":",
				"\"prepareRequest\":1",
				"\"sendRequest\":1",
				"\"receiveResponse\":1",
				"\"parseResponse\":1",
				"\"processComplete\":1",
				"\"dpop\":",
				"\"dpopBackend\":"
				);
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		String dpopBakckend = response.getHeaderFirstValue("govway-testsuite-custom_parameter");
		String dpopJti = verificaDPoPBackend(dpopBakckend, "user_password", "PS256", null, true);
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache e anche lo stesso DPoP Token
		
		response = NegoziazioneTest._test(logCore, api_negoziazione_dpop, "user_password", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"usernamePassword\"",
				"\"username\":\"ResourceOwnerPasswordUsername\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerDPoPDummy/v1/signedJwt2\"",
				"\"accessToken\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"policy\":\"TestNegoziazioneOwnerPasswordDPoP\"",
				"\"tokenType\":\"DPoP\"",
				"\"expires_in\":\"3600\"",
				"\"expiresIn\":1",
				"\"retrievedIn\":1",
				"\"retrievedRefreshTokenIn\":1",
				"\"example_parameter\":\"example_value\"",
				"\"request\":",
				"\"prepareRequest\":1",
				"\"sendRequest\":1",
				"\"receiveResponse\":1",
				"\"parseResponse\":1",
				"\"processComplete\":1",
				"\"transactionId\":\""+idRichiestaOriginale+"\"",
				"\"dpop\":",
				"\"dpopBackend\":"
				);

		dpopBakckend = response.getHeaderFirstValue("govway-testsuite-custom_parameter");
		verificaDPoPBackend(dpopBakckend, "user_password", "PS256", dpopJti, true);
		
		
		// Aspett tempi di cache DPoP (5 secondi)
		
		Utilities.sleep(5001);
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache ma un differente DPoP Token
		
		response = NegoziazioneTest._test(logCore, api_negoziazione_dpop, "user_password", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"usernamePassword\"",
				"\"username\":\"ResourceOwnerPasswordUsername\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerDPoPDummy/v1/signedJwt2\"",
				"\"accessToken\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"policy\":\"TestNegoziazioneOwnerPasswordDPoP\"",
				"\"tokenType\":\"DPoP\"",
				"\"expires_in\":\"3600\"",
				"\"expiresIn\":1",
				"\"retrievedIn\":1",
				"\"retrievedRefreshTokenIn\":1",
				"\"example_parameter\":\"example_value\"",
				"\"request\":",
				"\"prepareRequest\":1",
				"\"sendRequest\":1",
				"\"receiveResponse\":1",
				"\"parseResponse\":1",
				"\"processComplete\":1",
				"\"transactionId\":\""+idRichiestaOriginale+"\"",
				"\"dpop\":",
				"\"dpopBackend\":"
				);

		dpopBakckend = response.getHeaderFirstValue("govway-testsuite-custom_parameter");
		verificaDPoPBackend(dpopBakckend, "user_password", "PS256", dpopJti, false);
	}
	
	
	
	
	
	
	
	
	
	@Test
	public void signedJwtES256() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		HttpResponse response = NegoziazioneTest._test(logCore, api_negoziazione_dpop, "signedJwt", null,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerDPoPDummy/v1/signedJwt3\"",
				"\"accessToken\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"policy\":\"TestNegoziazioneSignedJWTDPoP\"",
				"\"tokenType\":\"DPoP\"",
				"\"expires_in\":\"3600\"",
				"\"expiresIn\":1",
				"\"retrievedIn\":1",
				"\"retrievedRefreshTokenIn\":1",
				"\"example_parameter\":\"example_value\"",
				"\"request\":",
				"\"prepareRequest\":1",
				"\"sendRequest\":1",
				"\"receiveResponse\":1",
				"\"parseResponse\":1",
				"\"processComplete\":1",
				"\"dpop\":",
				"\"dpopBackend\":"
				);
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		String dpopBakckend = response.getHeaderFirstValue("govway-testsuite-custom_header");
		String dpopJti = verificaDPoPBackend(dpopBakckend, "signedJwt", "ES256", null, true);
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache ma un differente DPoP Token
		
		response = NegoziazioneTest._test(logCore, api_negoziazione_dpop, "signedJwt", null,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerDPoPDummy/v1/signedJwt3\"",
				"\"accessToken\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"policy\":\"TestNegoziazioneSignedJWTDPoP\"",
				"\"tokenType\":\"DPoP\"",
				"\"expires_in\":\"3600\"",
				"\"expiresIn\":1",
				"\"retrievedIn\":1",
				"\"retrievedRefreshTokenIn\":1",
				"\"example_parameter\":\"example_value\"",
				"\"request\":",
				"\"prepareRequest\":1",
				"\"sendRequest\":1",
				"\"receiveResponse\":1",
				"\"parseResponse\":1",
				"\"processComplete\":1",
				"\"transactionId\":\""+idRichiestaOriginale+"\"",
				"\"dpop\":",
				"\"dpopBackend\":"
				);

		dpopBakckend = response.getHeaderFirstValue("govway-testsuite-custom_header");
		verificaDPoPBackend(dpopBakckend, "signedJwt", "ES256", dpopJti, false);
	}
	
	
	
	
	
	
	
	
	
	private String verificaDPoPBackend(String dpopBakckend, String azione, String algExpected, String jtiExpected, boolean jtiEquals) throws UtilsException {
		assertNotNull(dpopBakckend);
		RestMessageSecurityToken r = new RestMessageSecurityToken();
		r.setToken(dpopBakckend);
		
		String alg = r.getHeaderClaim("alg");
		assertEquals("ALG atteso["+algExpected+"] trovato["+alg+"]",algExpected, alg);
		
		String typExpected = "dpop+jwt";
		String typ = r.getHeaderClaim("typ");
		assertEquals("TYP atteso["+typExpected+"] trovato["+typ+"]",typExpected, typ);
		
		String jwkKtyExpected = null;
		if("RS256".equals(algExpected) || "PS256".equals(algExpected)) {
			jwkKtyExpected = "RSA";
		}
		else if("ES256".equals(algExpected)) {
			jwkKtyExpected = "EC";
		}
		String jwkKty = r.getHeaderClaim("jwk.kty");
		assertEquals("JWT.kty atteso["+jwkKtyExpected+"] trovato["+jwkKty+"]",jwkKtyExpected, jwkKty);
				
		if("RSA".equals(jwkKtyExpected)) {
			String jwkN = r.getHeaderClaim("jwk.n");
			assertNotNull(jwkN);
			
			String jwkE = r.getHeaderClaim("jwk.e");
			assertNotNull(jwkE);
		}
		else if("EC".equals(jwkKtyExpected)) {
			String jwkN = r.getHeaderClaim("jwk.x");
			assertNotNull(jwkN);
			
			String jwkE = r.getHeaderClaim("jwk.y");
			assertNotNull(jwkE);
		}
		
		String jwkAlg = r.getHeaderClaim("jwk.alg");
		assertNull(jwkAlg);
		
		String jwkKid = r.getHeaderClaim("jwk.kid");
		assertNull(jwkKid);
		
		String htmExpected = "POST";
		String htm = r.getPayloadClaim("htm");
		assertEquals("HTM atteso["+htmExpected+"] trovato["+htm+"]",htmExpected, htm);
		
		String htuExpected = "http://127.0.0.1:8080/TestService/echo/"+azione;
		String htu = r.getPayloadClaim("htu");
		assertEquals("HTU atteso["+htuExpected+"] trovato["+htu+"]",htuExpected, htu);
		
		String jti = r.getPayloadClaim("jti");
		if(jtiExpected!=null) {
			if(jtiEquals) {
				assertEquals("JTI atteso["+jtiExpected+"] trovato["+jti+"]",jtiExpected, jti);
			}
			else {
				assertNotEquals("JTI atteso diverso da ["+jtiExpected+"] trovato["+jti+"]",jtiExpected, jti);
			}
		}
		else {
			assertNotNull(jti);
		}
		
		String iat = r.getPayloadClaim("iat");
		assertNotNull(iat);
		
		String ath = r.getPayloadClaim("ath");
		assertNotNull(ath);
		
		return jti;
	}
}