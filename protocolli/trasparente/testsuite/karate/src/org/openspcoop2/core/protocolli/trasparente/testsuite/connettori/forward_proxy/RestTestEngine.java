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
import java.security.PublicKey;
import java.util.ArrayList;

import org.openspcoop2.protocol.sdk.RestMessageSecurityToken;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione.ValidazioneJWTDynamicDiscoveryTest;
import org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione.ValidazioneJWTKeystoreDinamicoTest;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.JWKPublicKeyConverter;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author Tommaso Burlon (tommaso.burlon@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTestEngine extends ConfigLoader {
	
	
	private HttpLibraryMode mode = null;
	protected void setHttpLibraryMode(HttpLibraryMode mode) {
		this.mode = mode;
	}
	
	// configurazione base
	@Test
	public void erogazioneBase() throws Exception {
		test(TipoServizio.EROGAZIONE,
				"TestForwardProxy","base");
	}
	@Test
	public void fruizioneBase() throws Exception {
		test(TipoServizio.FRUIZIONE, 
				"TestForwardProxy","base");
	}
	

	
	// validazione dynamic discovery
	@Test
	public void erogazioneDynamicDiscovery() throws Exception {
		test(TipoServizio.EROGAZIONE,
				"TestForwardProxyTokenValidazione","dynamicDiscovery");
	}
	@Test
	public void fruizioneDynamicDiscovery() throws Exception {
		test(TipoServizio.FRUIZIONE, 
				"TestForwardProxyTokenValidazione","dynamicDiscovery");
	}
	
	
	
	// validazione jwt
	@Test
	public void erogazioneValidazioneJwt() throws Exception {
		test(TipoServizio.EROGAZIONE,
				"TestForwardProxyTokenValidazione","validazioneJwt");
	}
	@Test
	public void fruizioneValidazioneJwt() throws Exception {
		test(TipoServizio.FRUIZIONE, 
				"TestForwardProxyTokenValidazione","validazioneJwt");
	}
	
	
	
	// validazione introspection
	@Test
	public void erogazioneIntrospection() throws Exception {
		test(TipoServizio.EROGAZIONE,
				"TestForwardProxyTokenValidazione","introspection");
	}
	@Test
	public void fruizioneIntrospection() throws Exception {
		test(TipoServizio.FRUIZIONE, 
				"TestForwardProxyTokenValidazione","introspection");
	}
	
	
	
	// validazione userinfo
	@Test
	public void erogazioneUserinfo() throws Exception {
		test(TipoServizio.EROGAZIONE,
				"TestForwardProxyTokenValidazione","userinfo");
	}
	@Test
	public void fruizioneUserinfo() throws Exception {
		test(TipoServizio.FRUIZIONE, 
				"TestForwardProxyTokenValidazione","userinfo");
	}
	
	
	
	
	
	// negoziazione
	@Test
	public void erogazioneNegoziazione() throws Exception {
		test(TipoServizio.EROGAZIONE,
				"TestForwardProxyTokenValidazione","negoziazione");
	}
	@Test
	public void fruizioneNegoziazione() throws Exception {
		test(TipoServizio.FRUIZIONE, 
				"TestForwardProxyTokenValidazione","negoziazione");
	}
	
	
	
	
	
	// attribute authority
	@Test
	public void erogazioneAttributeAuthority() throws Exception {
		test(TipoServizio.EROGAZIONE,
				"TestForwardProxyTokenValidazione","aa");
	}
	@Test
	public void fruizioneAttributeAuthority() throws Exception {
		test(TipoServizio.FRUIZIONE, 
				"TestForwardProxyTokenValidazione","aa");
	}

	
	
	
	// attribute authority response
	@Test
	public void erogazioneAttributeAuthorityRispostaJwt() throws Exception {
		test(TipoServizio.EROGAZIONE,
				"TestForwardProxyTokenValidazione","aaRispostaJwt");
	}
	@Test
	public void fruizioneAttributeAuthorityRispostaJwt() throws Exception {
		test(TipoServizio.FRUIZIONE, 
				"TestForwardProxyTokenValidazione","aaRispostaJwt");
	}
	
	
	
	
	
	private HttpResponse test(
			TipoServizio tipoServizio, 
			String api,String operazione) throws Exception {
		
		File f = null;
		File f2 = null;
		try {
			String json = null;
			if("TestForwardProxyTokenValidazione".equals(api)) {
				
				if("dynamicDiscovery".equals(operazione)){
					f2 = new File("/tmp/introResponse.json");
					FileSystemUtilities.deleteFile(f2);
					String introJson = "{\"active\": true,\"client_id\": \"l238j323ds-23ij4\",\"username\": \"jdoe\"}";
					FileSystemUtilities.writeFile(f2, introJson.getBytes());
					
					f = new File("/tmp/response.json");
					FileSystemUtilities.deleteFile(f);
					
					String introspectionUri = "http://127.0.0.1:8080/TestService/echo?destFile=/tmp/introResponse.json&destFileContentType=application/json";
					json = ValidazioneJWTDynamicDiscoveryTest.buildDD("", new ArrayList<>(),
							ValidazioneJWTDynamicDiscoveryTest.TEST+"/jwkUri", introspectionUri, ValidazioneJWTDynamicDiscoveryTest.TEST+"/user", ValidazioneJWTDynamicDiscoveryTest.TEST+"/altro");
					FileSystemUtilities.writeFile(f, json.getBytes());
				}
				else if("validazioneJwt".equals(operazione)){
					f = new File("/tmp/response.json");
					FileSystemUtilities.deleteFile(f);
					byte[]keystore = FileSystemUtilities.readBytesFromFile(getGovwayCfgKeys() + "/erogatore.jks");
					PublicKey publicKey = ArchiveLoader.loadFromKeystoreJKS(keystore, "erogatore", "openspcoop").getCertificate().getCertificate().getPublicKey();
					json = JWKPublicKeyConverter.convert(publicKey, "erogatore", true, false);
					FileSystemUtilities.writeFile(f, json.getBytes());
				}
				else if("aaRispostaJwt".equals(operazione)){
					f2 = new File("/tmp/token.jwt");
					FileSystemUtilities.deleteFile(f2);
					String token = ValidazioneJWTKeystoreDinamicoTest.buildJWT(new ArrayList<>());
					FileSystemUtilities.writeFile(f2, token.getBytes());
					
					f = new File("/tmp/response.json");
					FileSystemUtilities.deleteFile(f);
					byte[]keystore = FileSystemUtilities.readBytesFromFile(getGovwayCfgKeys() + "/erogatore.jks");
					PublicKey publicKey = ArchiveLoader.loadFromKeystoreJKS(keystore, "erogatore", "openspcoop").getCertificate().getCertificate().getPublicKey();
					json = JWKPublicKeyConverter.convert(publicKey, "erogatore", true, false);
					FileSystemUtilities.writeFile(f, json.getBytes());
				}
				else if("negoziazione".equals(operazione)){
					f = new File("/tmp/response.json");
					FileSystemUtilities.deleteFile(f);
					String token = ValidazioneJWTKeystoreDinamicoTest.buildJWT(new ArrayList<>());
					json = "{\"access_token\":\""+token+"\",\"token_type\":\"example\",\"expires_in\":3600,\"refresh_token\":\"tGzv3JOkF0XG5Qx2TlKWIA\"}";
					FileSystemUtilities.writeFile(f, json.getBytes());
				}
				else {
					f = new File("/tmp/response.json");
					FileSystemUtilities.deleteFile(f);
					json = "{\"active\": true,\"client_id\": \"l238j323ds-23ij4\",\"username\": \"jdoe\"}";
					FileSystemUtilities.writeFile(f, json.getBytes());
				}
			}
		
			String API = "SoggettoInternoTest/"+api+"/v1";
	
			String url = tipoServizio == TipoServizio.EROGAZIONE
					? System.getProperty("govway_base_path") + "/in/"+API+"/"+operazione
					: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/"+API+"/"+operazione;
			if("TestForwardProxyTokenValidazione".equals(api)) {
				if("validazioneJwt".equals(operazione)){
					String token = ValidazioneJWTKeystoreDinamicoTest.buildJWT(new ArrayList<>());
					url=url+"?access_token="+token;
				}
				else {
					url=url+"?access_token="+operazione;
				}
			}
			else {
				url=url+"?test="+operazione;
			}
			
			HttpRequest request = new HttpRequest();
			request.setReadTimeout(20000);
					
			String contentType = HttpConstants.CONTENT_TYPE_JSON;
			byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
					
			request.setMethod(HttpRequestMethod.POST);
			request.setContentType(contentType);
			
			request.setContent(content);
			
			request.setUrl(url);
			
			if (this.mode != null)
				this.mode.patchRequest(request);
			
			HttpResponse response = null;
			try {
				response = HttpUtilities.httpInvoke(request);
			}catch(Throwable t) {
				throw t;
			}
	
			assertEquals(200, response.getResultHTTPOperation());
						
			String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
			assertNotNull(idTransazione);
			
			if("TestForwardProxy".equals(api)) {
				
				assertEquals(contentType, response.getContentType());
				
				String uriResponse = null; 
				if(tipoServizio == TipoServizio.EROGAZIONE) {
					uriResponse = response.getHeaderFirstValue("test-govway-api-address");
				}
				else {
					uriResponse = response.getHeaderFirstValue("test_govway_api_address");
				}
				assertNotNull(uriResponse);
				
				//System.out.println("uriResponse: ["+uriResponse+"]");
				String decoded = new String(Base64Utilities.decode(uriResponse.getBytes()));
				//System.out.println("decoded: ["+decoded+"]");
				
				String tipo = "http://127.0.0.1:8080/TestService/echo/base?test="+operazione+"&tipoForwardProxy="+operazione;
				assertEquals("ID:"+idTransazione,tipo,decoded);
				
			}
			else if("TestForwardProxyTokenValidazione".equals(api)) {
				
				assertEquals(contentType, response.getContentType());
				
				String res = new String(response.getContent());
				assertEquals("ID:"+idTransazione,res,json);
			}
			
			long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
					
			DBVerifier.verify(idTransazione, esitoExpected, this.mode);
					
			return response;
			
		}finally {
			if(f!=null) {
				FileSystemUtilities.deleteFile(f);
			}
			if(f2!=null) {
				FileSystemUtilities.deleteFile(f2);
			}
		}

	}


	// ============================================================================================================
	// DPoP (RFC 9449) + forward proxy
	//
	// 1) Forward proxy sul connettore BACKEND (API 'TestForwardProxyNegoziazioneDPoP'): il claim 'htu' del DPoP backend
	//    e la relativa chiave di cache devono riferirsi all'URL REALE del backend, non a quello del proxy.
	// 2) Forward proxy sulla NEGOZIAZIONE/token retrieve (API 'TestForwardProxyNegoziazioneDPoPRetrieve'): il claim 'htu'
	//    del DPoP di negoziazione deve essere l'endpoint CONFIGURATO (finto), non quello del proxy.
	// ============================================================================================================

	/** API con forward proxy sul connettore BACKEND e policy di negoziazione token DPoP. */
	private static final String API_BACKEND_DPOP = "TestForwardProxyNegoziazioneDPoP";
	/** API con forward proxy sulla NEGOZIAZIONE (token retrieve) e token policy con endpoint FINTO. */
	private static final String API_RETRIEVE_DPOP = "TestForwardProxyNegoziazioneDPoPRetrieve";
	/** URL reale del connettore backend (host distinto e non raggiungibile): htu atteso del DPoP backend; il path op1/op2 viene appeso da GovWay. */
	private static final String BACKEND_BASE_URL = "http://backendReale:8080/TestService/echo/";
	/** Endpoint dell'AS della negoziazione diretta (API backend): htu atteso del DPoP di negoziazione. */
	private static final String AS_ENDPOINT_DPOP = "http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerDPoPDummy/v1/signedJwt";
	/** Endpoint FINTO della token policy dell'API_RETRIEVE_DPOP: htu atteso del DPoP di negoziazione (NON il proxy). */
	private static final String FAKE_ENDPOINT_DPOP = "http://authServerFinto:8080/signedJwt";
	/** File con il token "canned" restituito dall'echo-proxy della negoziazione (deve combaciare con destFile nella sys-prop govway-proxy del tag Retrieve). */
	private static final String CANNED_TOKEN_FILE_DPOP = "/tmp/responseNegoziazioneDPoPRetrieve.json";
	/** Header con cui il backend echo rinvia il DPoP proof ricevuto: replyHttpHeader=DPoP, replyPrefixHttpHeader=govway-testsuite- */
	private static final String DPOP_REPLY_HEADER = "govway-testsuite-dpop";


	@Test
	public void erogazioneHtuBackendDPoPRealeNonProxy() throws Exception {
		htuBackendDPoPRealeNonProxy(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneHtuBackendDPoPRealeNonProxy() throws Exception {
		htuBackendDPoPRealeNonProxy(TipoServizio.FRUIZIONE);
	}
	private void htuBackendDPoPRealeNonProxy(TipoServizio tipoServizio) throws Exception {
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);

		HttpResponse response = invokeDPoP(API_BACKEND_DPOP, tipoServizio, "op1");
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");

		// DPoP backend: htu = URL reale del backend (non proxy)
		String htu = getDPoPClaim(getDPoPBackend(response), "htu");
		String htuExpected = BACKEND_BASE_URL + "op1";
		assertEquals("HTU backend atteso (URL reale, non proxy) ["+htuExpected+"] trovato ["+htu+"]", htuExpected, htu);

		// DPoP negoziazione: htu = endpoint AS (il forward proxy sul backend non lo influenza)
		verificaHtuNegoziazioneDPoP(idTransazione, AS_ENDPOINT_DPOP);
	}


	@Test
	public void erogazioneCacheKeyBackendDPoPSuUrlReale() throws Exception {
		cacheKeyBackendDPoPSuUrlReale(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneCacheKeyBackendDPoPSuUrlReale() throws Exception {
		cacheKeyBackendDPoPSuUrlReale(TipoServizio.FRUIZIONE);
	}
	/**
	 * La chiave di cache del DPoP backend deve includere l'URL reale: op1 (2 chiamate -> cache hit, stesso jti) vs op2
	 * (path reale differente, stessa policy/token -> stesso ath) deve dare htu e jti differenti (nessuna collisione sul proxy URL).
	 */
	private void cacheKeyBackendDPoPSuUrlReale(TipoServizio tipoServizio) throws Exception {
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);

		String dpopOp1a = getDPoPBackend(invokeDPoP(API_BACKEND_DPOP, tipoServizio, "op1"));
		String htuOp1 = getDPoPClaim(dpopOp1a, "htu");
		String jtiOp1 = getDPoPClaim(dpopOp1a, "jti");
		assertEquals(BACKEND_BASE_URL + "op1", htuOp1);

		String dpopOp1b = getDPoPBackend(invokeDPoP(API_BACKEND_DPOP, tipoServizio, "op1"));
		assertEquals("Atteso cache hit DPoP backend per op1 (stesso jti)", jtiOp1, getDPoPClaim(dpopOp1b, "jti"));

		String dpopOp2 = getDPoPBackend(invokeDPoP(API_BACKEND_DPOP, tipoServizio, "op2"));
		String htuOp2 = getDPoPClaim(dpopOp2, "htu");
		String jtiOp2 = getDPoPClaim(dpopOp2, "jti");

		assertEquals("HTU op2 deve essere l'URL reale di op2", BACKEND_BASE_URL + "op2", htuOp2);
		assertNotEquals("htu op2 deve differire da htu op1 (nessuna collisione sul proxy URL)", htuOp1, htuOp2);
		assertNotEquals("jti op2 deve differire da jti op1 (nessuna collisione di cache key)", jtiOp1, jtiOp2);
	}


	@Test
	public void erogazioneHtuNegoziazioneDPoPEndpointNonProxy() throws Exception {
		htuNegoziazioneDPoPEndpointNonProxy(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneHtuNegoziazioneDPoPEndpointNonProxy() throws Exception {
		htuNegoziazioneDPoPEndpointNonProxy(TipoServizio.FRUIZIONE);
	}
	/**
	 * Forward proxy sulla negoziazione + token policy con endpoint FINTO: il proxy è un echo che ritorna un token
	 * "canned" (non valida il DPoP), quindi la negoziazione va a buon fine; l'htu del DPoP di negoziazione = endpoint
	 * configurato (finto), non l'URL del proxy.
	 */
	private void htuNegoziazioneDPoPEndpointNonProxy(TipoServizio tipoServizio) throws Exception {
		File f = new File(CANNED_TOKEN_FILE_DPOP);
		try {
			org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);

			FileSystemUtilities.deleteFile(f);
			String token = ValidazioneJWTKeystoreDinamicoTest.buildJWT(new ArrayList<>());
			String json = "{\"access_token\":\""+token+"\",\"token_type\":\"example\",\"expires_in\":3600,\"refresh_token\":\"tGzv3JOkF0XG5Qx2TlKWIA\"}";
			FileSystemUtilities.writeFile(f, json.getBytes());

			HttpResponse response = invokeDPoP(API_RETRIEVE_DPOP, tipoServizio, "op1");
			String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");

			verificaHtuNegoziazioneDPoP(idTransazione, FAKE_ENDPOINT_DPOP);
		} finally {
			FileSystemUtilities.deleteFile(f);
		}
	}


	// Helpers DPoP

	private HttpResponse invokeDPoP(String api, TipoServizio tipoServizio, String operazione) throws Exception {
		String apiPath = "SoggettoInternoTest/"+api+"/v1";
		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/in/"+apiPath+"/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/"+apiPath+"/"+operazione;

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		request.setContent(Bodies.getJson(Bodies.SMALL_SIZE).getBytes());
		request.setUrl(url);
		if (this.mode != null) {
			this.mode.patchRequest(request);
		}

		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		long esitoOk = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		DBVerifier.verify(idTransazione, esitoOk, this.mode);

		return response;
	}

	private String getDPoPBackend(HttpResponse response) {
		String dpop = response.getHeaderFirstValue(DPOP_REPLY_HEADER);
		assertNotNull("DPoP backend proof non rinviato dal backend (header '"+DPOP_REPLY_HEADER+"')", dpop);
		return dpop;
	}

	private String getDPoPClaim(String dpopProof, String claim) throws Exception {
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
	private void verificaHtuNegoziazioneDPoP(String idTransazione, String htuAtteso) throws Exception {
		String tokenInfo = org.openspcoop2.core.protocolli.trasparente.testsuite.token.negoziazione.DBVerifier.readTokenInfo(idTransazione);
		assertNotNull("token_info assente per la transazione "+idTransazione, tokenInfo);

		JsonNode root = JSONUtils.getInstance().getAsNode(tokenInfo);
		String proof = root.path("request").path("dpop").path("token").asText(null);
		assertNotNull("Proof DPoP di negoziazione assente nel token_info (request.dpop.token): "+tokenInfo, proof);

		String htu = getDPoPClaim(proof, "htu");
		assertEquals("HTU negoziazione atteso ["+htuAtteso+"] trovato ["+htu+"]", htuAtteso, htu);
	}
}
