/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
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
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {
	
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
	
	
	
	
	
	private static HttpResponse test(
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
					byte[]keystore = FileSystemUtilities.readBytesFromFile("/etc/govway/keys/erogatore.jks");
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
					byte[]keystore = FileSystemUtilities.readBytesFromFile("/etc/govway/keys/erogatore.jks");
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
					? System.getProperty("govway_base_path") + "/"+API+"/"+operazione
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
					
			DBVerifier.verify(idTransazione, esitoExpected, null);
					
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
	
	
}
