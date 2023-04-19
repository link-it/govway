/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
* AuthorizationServerTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class NegoziazioneTest extends ConfigLoader {

	public static final String api_negoziazione = "TestNegoziazioneToken";
	
	
	@Test
	public void clientid_clientsecret() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		HttpResponse response = _test(logCore, api_negoziazione, "clientid_clientsecret", null,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"clientCredentials\"",
				"\"clientId\":\"ClientIDVerificaNegoziazioneConSecret\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/clientid_clientsecret\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneClientCredentials\""
				);
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache
		
		_test(logCore, api_negoziazione, "clientid_clientsecret", null,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"clientCredentials\"",
				"\"clientId\":\"ClientIDVerificaNegoziazioneConSecret\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/clientid_clientsecret\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneClientCredentials\"",
				"\"transactionId\":\""+idRichiestaOriginale+"\""
				);
	}
	
	@Test
	public void clientid_clientsecret2() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-azione", "clientid_clientsecret2");
		headers.put("test-user", "ClientIDVerificaNegoziazioneConSecret2");
		headers.put("test-scope", "testNegoziazioneScope2");
		headers.put("test-audience", "testNegoziazioneAudience");
		headers.put("test-p2", "testNegoziazioneP2");
		headers.put("test_client_credentials", "vTest2");
		
		HttpResponse response = _test(logCore, api_negoziazione, "clientid_clientsecret2", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"clientCredentials\"",
				"\"clientId\":\"ClientIDVerificaNegoziazioneConSecret2\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/clientid_clientsecret2\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneClientCredentials2\"",
				"\"test_client_credentials_2\":\"vTest2\"",
			    "\"test_client_credentials_1\":\"vTest1\"");
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test-user", "ClientIDVerificaNegoziazioneConSecret2-ERRATO");
		
		_test(logCore, api_negoziazione, "clientid_clientsecret2", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 401)%AuthenticationFailed");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test-user", "ClientIDVerificaNegoziazioneConSecret2");
		headers.put("test-scope", "testNegoziazioneScope2-ERRATO");
		
		_test(logCore, api_negoziazione, "clientid_clientsecret2", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test-scope", "testNegoziazioneScope2");
		headers.put("test-audience", "testNegoziazioneAudience-ERRATO");
		
		_test(logCore, api_negoziazione, "clientid_clientsecret2", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test-audience", "testNegoziazioneAudience");
		headers.put("test-p2", "testNegoziazioneP2-ERRATO");
		
		_test(logCore, api_negoziazione, "clientid_clientsecret2", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test-p2", "testNegoziazioneP2");
		headers.put("test_client_credentials", "vTest2-ERRATO");
		
		_test(logCore, api_negoziazione, "clientid_clientsecret2", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, ripristinando la configurazione corretta
		headers.put("test_client_credentials", "vTest2");
		_test(logCore, api_negoziazione, "clientid_clientsecret2", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"clientCredentials\"",
				"\"clientId\":\"ClientIDVerificaNegoziazioneConSecret2\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/clientid_clientsecret2\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneClientCredentials2\"",
				"\"test_client_credentials_2\":\"vTest2\"",
			    "\"test_client_credentials_1\":\"vTest1\"",
				"\"transactionId\":\""+idRichiestaOriginale+"\""
				);
	}
	
	@Test
	public void clientid_clientsecret3() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-azione", "clientid_clientsecret3");
		
		HttpResponse response = _test(logCore, api_negoziazione, "clientid_clientsecret3", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"clientCredentials\"",
				"\"clientToken\":\"TOKEN-XXX\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/clientid_clientsecret3\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneClientCredentials3\""
				);
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache
		
		_test(logCore, api_negoziazione, "clientid_clientsecret3", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"clientCredentials\"",
				"\"clientToken\":\"TOKEN-XXX\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/clientid_clientsecret3\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneClientCredentials3\"",
				"\"transactionId\":\""+idRichiestaOriginale+"\""
				);
	}

	@Test
	public void user_password() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-azione", "user_password");
		headers.put("test-username", "ResourceOwnerPasswordUsername");
		
		HttpResponse response = _test(logCore, api_negoziazione, "user_password", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"usernamePassword\"",
				"\"username\":\"ResourceOwnerPasswordUsername\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/user_password\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneUserPassword\"");
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test-azione", "user_passwordERRATA");
		
		_test(logCore, api_negoziazione, "user_password", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test-azione", "user_password");
		headers.put("test-username", "ResourceOwnerPasswordUsernameERRATO");
		
		_test(logCore, api_negoziazione, "user_password", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache
		
		headers.put("test-username", "ResourceOwnerPasswordUsername");
		
		_test(logCore, api_negoziazione, "user_password", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"usernamePassword\"",
				"\"username\":\"ResourceOwnerPasswordUsername\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/user_password\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneUserPassword\"",
				"\"transactionId\":\""+idRichiestaOriginale+"\""
				);
	}
	
	@Test
	public void signedJWT_clientSecret() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		Map<String, String> headers_0 = new HashMap<String, String>();
		headers_0.put("test-azione", "signedJWT_clientSecret");
		headers_0.put("test-suffix", "DYNAMIC");
		headers_0.put("test-decode-position", "0");
		
		HttpResponse response = _test(logCore, api_negoziazione, "signedJWT_clientSecret", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_clientSecret\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT_clientSecret\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWTwithClientSecret\"");
		String idRichiestaOriginale_0 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		// serviva prima di implementare i parametri dinamici all'interno della chiave della cache.
		//org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		Map<String, String> headers_1 = new HashMap<String, String>();
		headers_1.put("test-azione", "signedJWT_clientSecret");
		headers_1.put("test-suffix", "DYNAMIC");
		headers_1.put("test-decode-position", "1");
		
		response = _test(logCore, api_negoziazione, "signedJWT_clientSecret", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_clientSecret\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT_clientSecret\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWTwithClientSecret\"");
		String idRichiestaOriginale_1 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		headers_0.put("test-azione", "signedJWT_clientSecretERRATA");
		_test(logCore, api_negoziazione, "signedJWT_clientSecret", headers_0,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_0.put("test-azione", "signedJWT_clientSecret");
		
		headers_1.put("test-azione", "signedJWT_clientSecretERRATA");
		_test(logCore, api_negoziazione, "signedJWT_clientSecret", headers_1,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_1.put("test-azione", "signedJWT_clientSecret");
			
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		// Sull'header non vi sono informazioni dinamiche per questa policy
//		headers_0.put("test-suffix", "DYNAMICERRATA");
//		_test(logCore, api_negoziazione, "signedJWT_clientSecret", headers_0,
//				true,
//				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
//		headers_0.put("test-suffix", "DYNAMIC");
		
		headers_1.put("test-suffix", "DYNAMICERRATA");
		_test(logCore, api_negoziazione, "signedJWT_clientSecret", headers_1,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		headers_1.put("test-suffix", "DYNAMIC");
		
	
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache
		
		_test(logCore, api_negoziazione, "signedJWT_clientSecret", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_clientSecret\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT_clientSecret\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWTwithClientSecret\"",
				"\"transactionId\":\""+idRichiestaOriginale_0+"\""
				);
		
		_test(logCore, api_negoziazione, "signedJWT_clientSecret", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_clientSecret\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT_clientSecret\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWTwithClientSecret\"",
				"\"transactionId\":\""+idRichiestaOriginale_1+"\""
				);
	}
	
	@Test
	public void signedJWT_clientSecret2() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		Map<String, String> headers_0 = new HashMap<String, String>();
		headers_0.put("test-azione", "signedJWT_clientSecret2");
		headers_0.put("test-suffix", "DYNAMIC");
		headers_0.put("test-decode-position", "0");
		headers_0.put("test-p2", "testNegoziazioneP2");
		
		HttpResponse response = _test(logCore, api_negoziazione, "signedJWT_clientSecret2", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_clientSecret\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT_clientSecret2\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWTwithClientSecret2\"");
		String idRichiestaOriginale_0 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		// serviva prima di implementare i parametri dinamici all'interno della chiave della cache.
		//org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		Map<String, String> headers_1 = new HashMap<String, String>();
		headers_1.put("test-azione", "signedJWT_clientSecret2");
		headers_1.put("test-suffix", "DYNAMIC");
		headers_1.put("test-decode-position", "1");
		headers_1.put("test-p2", "testNegoziazioneP2");
		
		response = _test(logCore, api_negoziazione, "signedJWT_clientSecret2", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_clientSecret\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT_clientSecret2\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWTwithClientSecret2\"");
		String idRichiestaOriginale_1 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		headers_0.put("test-azione", "signedJWT_clientSecret2ERRATA");
		_test(logCore, api_negoziazione, "signedJWT_clientSecret2", headers_0,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_0.put("test-azione", "signedJWT_clientSecret2");
		
		headers_1.put("test-azione", "signedJWT_clientSecret2ERRATA");
		_test(logCore, api_negoziazione, "signedJWT_clientSecret2", headers_1,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_1.put("test-azione", "signedJWT_clientSecret2");
			
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		// Sull'header non vi sono informazioni dinamiche per questa policy
//		headers_0.put("test-suffix", "DYNAMICERRATA");
//		_test(logCore, api_negoziazione, "signedJWT_clientSecret2", headers_0,
//				true,
//				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
//		headers_0.put("test-suffix", "DYNAMIC");
		
		headers_1.put("test-suffix", "DYNAMICERRATA");
		_test(logCore, api_negoziazione, "signedJWT_clientSecret2", headers_1,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		headers_1.put("test-suffix", "DYNAMIC");
		
		
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		// Sull'header non vi sono informazioni dinamiche per questa policy
//		headers_0.put("test-p2", "testNegoziazioneP2ERRATA");
//		_test(logCore, api_negoziazione, "signedJWT_clientSecret2", headers_0,
//				true,
//				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
//		headers_0.put("test-p2", "testNegoziazioneP2");
		
		headers_1.put("test-p2", "testNegoziazioneP2ERRATA");
		_test(logCore, api_negoziazione, "signedJWT_clientSecret2", headers_1,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		headers_1.put("test-p2", "testNegoziazioneP2");
		
		
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache
		
		 _test(logCore, api_negoziazione, "signedJWT_clientSecret2", headers_0,
					false,
					"\"type\":\"retrieved_token\"",
					"\"grantType\":\"rfc7523_clientSecret\"",
					"\"jwtClientAssertion\":{\"token\":\"",
					"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT_clientSecret2\"",
					"\"accessToken\":\"",
					"\"expiresIn\":",
					"\"policy\":\"TestNegoziazioneSignedJWTwithClientSecret2\"",
					"\"transactionId\":\""+idRichiestaOriginale_0+"\""
					);
		
		 _test(logCore, api_negoziazione, "signedJWT_clientSecret2", headers_1,
					false,
					"\"type\":\"retrieved_token\"",
					"\"grantType\":\"rfc7523_clientSecret\"",
					"\"jwtClientAssertion\":{\"token\":\"",
					"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT_clientSecret2\"",
					"\"accessToken\":\"",
					"\"expiresIn\":",
					"\"policy\":\"TestNegoziazioneSignedJWTwithClientSecret2\"",
					"\"transactionId\":\""+idRichiestaOriginale_1+"\""
					);
	}
	
	@Test
	public void signedJWT_clientSecret3() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		Map<String, String> headers_0 = new HashMap<String, String>();
		headers_0.put("test-azione", "signedJWT_clientSecret3");
		headers_0.put("test-suffix", "DYNAMIC");
		headers_0.put("test-decode-position", "0");
		
		HttpResponse response = _test(logCore, api_negoziazione, "signedJWT_clientSecret3", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_clientSecret\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT_clientSecret3\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWTwithClientSecret3\"");
		String idRichiestaOriginale_0 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		// serviva prima di implementare i parametri dinamici all'interno della chiave della cache.
		//org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		Map<String, String> headers_1 = new HashMap<String, String>();
		headers_1.put("test-azione", "signedJWT_clientSecret3");
		headers_1.put("test-suffix", "DYNAMIC");
		headers_1.put("test-decode-position", "1");
		
		response = _test(logCore, api_negoziazione, "signedJWT_clientSecret3", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_clientSecret\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT_clientSecret3\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWTwithClientSecret3\"");
		String idRichiestaOriginale_1 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		headers_0.put("test-azione", "signedJWT_clientSecret3ERRATA");
		_test(logCore, api_negoziazione, "signedJWT_clientSecret3", headers_0,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_0.put("test-azione", "signedJWT_clientSecret3");
		
		headers_1.put("test-azione", "signedJWT_clientSecret3ERRATA");
		_test(logCore, api_negoziazione, "signedJWT_clientSecret3", headers_1,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_1.put("test-azione", "signedJWT_clientSecret3");
			
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		headers_0.put("test-suffix", "DYNAMICERRATA");
		_test(logCore, api_negoziazione, "signedJWT_clientSecret3", headers_0,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		headers_0.put("test-suffix", "DYNAMIC");

		// Sul payload non vi sono informazioni dinamiche per questa policy, influenzate dal test-suffix
//		headers_1.put("test-suffix", "DYNAMICERRATA");
//		_test(logCore, api_negoziazione, "signedJWT_clientSecret3", headers_1,
//				true,
//				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
//		headers_1.put("test-suffix", "DYNAMIC");
		
		
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache
		
		_test(logCore, api_negoziazione, "signedJWT_clientSecret3", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_clientSecret\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT_clientSecret3\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWTwithClientSecret3\"",
				"\"transactionId\":\""+idRichiestaOriginale_0+"\""
				);
		
		_test(logCore, api_negoziazione, "signedJWT_clientSecret3", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_clientSecret\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT_clientSecret3\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWTwithClientSecret3\"",
				"\"transactionId\":\""+idRichiestaOriginale_1+"\""
				);
	}
	
	@Test
	public void signedJWT() throws Exception {
		
		// AccessToken ritornato è opaco (nel test signedJWT3 sarà un JWT)
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		Map<String, String> headers_0 = new HashMap<String, String>();
		headers_0.put("test-azione", "signedJWT");
		headers_0.put("test-suffix", "DYNAMIC");
		headers_0.put("test-decode-position", "0");
		
		HttpResponse response = _test(logCore, api_negoziazione, "signedJWT", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT\"");
		String idRichiestaOriginale_0 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		// serviva prima di implementare i parametri dinamici all'interno della chiave della cache.
		//org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		Map<String, String> headers_1 = new HashMap<String, String>();
		headers_1.put("test-azione", "signedJWT");
		headers_1.put("test-suffix", "DYNAMIC");
		headers_1.put("test-decode-position", "1");
		
		response = _test(logCore, api_negoziazione, "signedJWT", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT\"");
		String idRichiestaOriginale_1 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		headers_0.put("test-azione", "signedJWTERRATA");
		_test(logCore, api_negoziazione, "signedJWT", headers_0,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_0.put("test-azione", "signedJWT");
		
		headers_1.put("test-azione", "signedJWTERRATA");
		_test(logCore, api_negoziazione, "signedJWT", headers_1,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_1.put("test-azione", "signedJWT");
		
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		// Sull'header non vi sono informazioni dinamiche per questa policy
//		headers_0.put("test-suffix", "DYNAMICERRATA");
//		_test(logCore, api_negoziazione, "signedJWT", headers_0,
//				true,
//				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
//		headers_0.put("test-suffix", "DYNAMIC");
		
		headers_1.put("test-suffix", "DYNAMICERRATA");
		_test(logCore, api_negoziazione, "signedJWT", headers_1,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		headers_1.put("test-suffix", "DYNAMIC");
				
		
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache
		
		_test(logCore, api_negoziazione, "signedJWT", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT\"",
				"\"transactionId\":\""+idRichiestaOriginale_0+"\"");

		_test(logCore, api_negoziazione, "signedJWT", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT\"",
				"\"transactionId\":\""+idRichiestaOriginale_1+"\"");
	}
	
	@Test
	public void signedJWT2() throws Exception {
	
		// AccessToken ritornato è opaco (nel test signedJWT3 sarà un JWT)
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		Map<String, String> headers_0 = new HashMap<String, String>();
		headers_0.put("test-azione", "signedJWT2");
		headers_0.put("test-suffix", "DYNAMIC");
		headers_0.put("test-decode-position", "0");
		headers_0.put("test-p2", "testNegoziazioneP2");
		
		HttpResponse response = _test(logCore, api_negoziazione, "signedJWT2", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT2\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT2\"");
		String idRichiestaOriginale_0 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		// serviva prima di implementare i parametri dinamici all'interno della chiave della cache.
		//org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		Map<String, String> headers_1 = new HashMap<String, String>();
		headers_1.put("test-azione", "signedJWT2");
		headers_1.put("test-suffix", "DYNAMIC");
		headers_1.put("test-decode-position", "1");
		headers_1.put("test-p2", "testNegoziazioneP2");
		
		response = _test(logCore, api_negoziazione, "signedJWT2", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT2\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT2\"");
		String idRichiestaOriginale_1 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		headers_0.put("test-azione", "signedJWT2ERRATA");
		_test(logCore, api_negoziazione, "signedJWT2", headers_0,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_0.put("test-azione", "signedJWT2");
		
		headers_1.put("test-azione", "signedJWT2ERRATA");
		_test(logCore, api_negoziazione, "signedJWT2", headers_1,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_1.put("test-azione", "signedJWT2");
		
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		// Sull'header non vi sono informazioni dinamiche per questa policy
//		headers_0.put("test-suffix", "DYNAMICERRATA");
//		_test(logCore, api_negoziazione, "signedJWT2", headers_0,
//				true,
//				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
//		headers_0.put("test-suffix", "DYNAMIC");
		
		headers_1.put("test-suffix", "DYNAMICERRATA");
		_test(logCore, api_negoziazione, "signedJWT2", headers_1,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		headers_1.put("test-suffix", "DYNAMIC");
		
		
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache
		
		_test(logCore, api_negoziazione, "signedJWT2", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT2\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT2\"",
				"\"transactionId\":\""+idRichiestaOriginale_0+"\""
				);
		
		_test(logCore, api_negoziazione, "signedJWT2", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT2\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT2\"",
				"\"transactionId\":\""+idRichiestaOriginale_1+"\""
				);
		
	}
	
	@Test
	public void signedJWT3() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		// AccessToken ritornato è un JWT
		
		Map<String, String> headers_0 = new HashMap<String, String>();
		headers_0.put("test-azione", "signedJWT3");
		headers_0.put("test-suffix", "DYNAMIC");
		headers_0.put("test-decode-position", "0");
		
		HttpResponse response = _test(logCore, api_negoziazione, "signedJWT3", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT3\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT3\"");
		String idRichiestaOriginale_0 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		// serviva prima di implementare i parametri dinamici all'interno della chiave della cache.
		//org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		Map<String, String> headers_1 = new HashMap<String, String>();
		headers_1.put("test-azione", "signedJWT3");
		headers_1.put("test-suffix", "DYNAMIC");
		headers_1.put("test-decode-position", "1");
		
		response = _test(logCore, api_negoziazione, "signedJWT3", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT3\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT3\"");
		String idRichiestaOriginale_1 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		headers_0.put("test-azione", "signedJWT3ERRATA");
		_test(logCore, api_negoziazione, "signedJWT3", headers_0,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_0.put("test-azione", "signedJWT3");
		
		headers_1.put("test-azione", "signedJWT3ERRATA");
		_test(logCore, api_negoziazione, "signedJWT3", headers_1,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_1.put("test-azione", "signedJWT3");
			
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		headers_0.put("test-suffix", "DYNAMICERRATA");
		_test(logCore, api_negoziazione, "signedJWT3", headers_0,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		headers_0.put("test-suffix", "DYNAMIC");

		// Sul payload non vi sono informazioni dinamiche per questa policy, influenzate dal test-suffix
//		headers_1.put("test-suffix", "DYNAMICERRATA");
//		_test(logCore, api_negoziazione, "signedJWT3", headers_1,
//				true,
//				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
//		headers_1.put("test-suffix", "DYNAMIC");
		
		
		
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache
		
		_test(logCore, api_negoziazione, "signedJWT3", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT3\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT3\"",
				"\"transactionId\":\""+idRichiestaOriginale_0+"\""
				);
		
		_test(logCore, api_negoziazione, "signedJWT3", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT3\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT3\"",
				"\"transactionId\":\""+idRichiestaOriginale_1+"\""
				);
		
	}
	
	@Test
	public void signedJWT_PDND() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		Map<String, String> headers_0 = new HashMap<String, String>();
		headers_0.put("test-azione", "signedJWT-PDND");
		
		headers_0.put("test-kid", "PDND");
		
		headers_0.put("test-issuer", "DYNAMIC");
		headers_0.put("test-subject", "DYNAMIC");
		headers_0.put("test-clientId", "DYNAMIC");
		headers_0.put("test-aud", "DYNAMIC");
		headers_0.put("test-jti", "DYNAMIC");
		headers_0.put("test-purposeId", "DYNAMIC");
		headers_0.put("test-sessionInfo", "DYNAMIC");
		headers_0.put("test-claim", "DYNAMIC");
		
		headers_0.put("test-scope", "DYNAMIC");
		headers_0.put("test-formClientId", "DYNAMIC");
		headers_0.put("test-p2", "testNegoziazioneP2");
				
		headers_0.put("test-suffix", "DYNAMIC");
		headers_0.put("test-decode-position", "0");
		
		HttpResponse response = _test(logCore, api_negoziazione, "signedJWT-PDND", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT-PDND\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-PDND\"");
		String idRichiestaOriginale_0 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		// serviva prima di implementare i parametri dinamici all'interno della chiave della cache.
		//org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		Map<String, String> headers_1 = new HashMap<String, String>();
		headers_1.put("test-azione", "signedJWT-PDND");
		
		headers_1.put("test-kid", "PDND");
		
		headers_1.put("test-issuer", "DYNAMIC");
		headers_1.put("test-subject", "DYNAMIC");
		headers_1.put("test-clientId", "DYNAMIC");
		headers_1.put("test-aud", "DYNAMIC");
		headers_1.put("test-jti", "DYNAMIC");
		headers_1.put("test-purposeId", "DYNAMIC");
		headers_1.put("test-sessionInfo", "DYNAMIC");
		headers_1.put("test-claim", "DYNAMIC");
		
		headers_1.put("test-scope", "DYNAMIC");
		headers_1.put("test-formClientId", "DYNAMIC");
		headers_1.put("test-p2", "testNegoziazioneP2");
		
		headers_1.put("test-suffix", "DYNAMIC");
		headers_1.put("test-decode-position", "1");
		
		response = _test(logCore, api_negoziazione, "signedJWT-PDND", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT-PDND\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-PDND\"");
		String idRichiestaOriginale_1 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		headers_0.put("test-azione", "signedJWT-PDNDERRATA");
		_test(logCore, api_negoziazione, "signedJWT-PDND", headers_0,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_0.put("test-azione", "signedJWT-PDND");
		
		headers_1.put("test-azione", "signedJWT-PDNDERRATA");
		_test(logCore, api_negoziazione, "signedJWT-PDND", headers_1,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_1.put("test-azione", "signedJWT-PDND");
		
		
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache
		
		_test(logCore, api_negoziazione, "signedJWT-PDND", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT-PDND\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-PDND\"",
				"\"transactionId\":\""+idRichiestaOriginale_0+"\""
				);
		
		_test(logCore, api_negoziazione, "signedJWT-PDND", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT-PDND\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-PDND\"",
				"\"transactionId\":\""+idRichiestaOriginale_1+"\""
				);
		
	}
	
	
	
	
	
	
	@Test
	public void signedJWT_PDNDv41() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		Map<String, String> headers_0 = new HashMap<String, String>();
		headers_0.put("test-azione", "signedJWT-PDNDv41");
		
		headers_0.put("test-kid", "PDND");
		
		headers_0.put("test-issuer", "DYNAMIC");
		headers_0.put("test-subject", "DYNAMIC");
		headers_0.put("test-clientId", "DYNAMIC");
		headers_0.put("test-aud", "DYNAMIC");
		headers_0.put("test-jti", "DYNAMIC");
		headers_0.put("test-purposeId", "DYNAMIC");
		headers_0.put("test-sessionInfo", "DYNAMIC");
		headers_0.put("test-claim", "DYNAMIC");
		
		headers_0.put("test-scope", "DYNAMIC");
		headers_0.put("test-formClientId", "DYNAMIC");
		headers_0.put("test-formResource", "DYNAMIC");
		headers_0.put("test-p2", "testNegoziazioneP2");
				
		headers_0.put("test-suffix", "DYNAMIC");
		headers_0.put("test-decode-position", "0");
		
		HttpResponse response = _test(logCore, api_negoziazione, "signedJWT-PDNDv41", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT-PDNDv41\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-PDNDv41\"");
		String idRichiestaOriginale_0 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		// serviva prima di implementare i parametri dinamici all'interno della chiave della cache.
		//org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		Map<String, String> headers_1 = new HashMap<String, String>();
		headers_1.put("test-azione", "signedJWT-PDNDv41");
		
		headers_1.put("test-kid", "PDND");
		
		headers_1.put("test-issuer", "DYNAMIC");
		headers_1.put("test-subject", "DYNAMIC");
		headers_1.put("test-clientId", "DYNAMIC");
		headers_1.put("test-aud", "DYNAMIC");
		headers_1.put("test-jti", "DYNAMIC");
		headers_1.put("test-purposeId", "DYNAMIC");
		headers_1.put("test-sessionInfo", "DYNAMIC");
		headers_1.put("test-claim", "DYNAMIC");
		
		headers_1.put("test-scope", "DYNAMIC");
		headers_1.put("test-formClientId", "DYNAMIC");
		headers_1.put("test-formResource", "DYNAMIC");
		headers_1.put("test-p2", "testNegoziazioneP2");
		
		headers_1.put("test-suffix", "DYNAMIC");
		headers_1.put("test-decode-position", "1");
		
		response = _test(logCore, api_negoziazione, "signedJWT-PDNDv41", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT-PDNDv41\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-PDNDv41\"");
		String idRichiestaOriginale_1 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		headers_0.put("test-azione", "signedJWT-PDNDv41ERRATA");
		_test(logCore, api_negoziazione, "signedJWT-PDNDv41", headers_0,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_0.put("test-azione", "signedJWT-PDNDv41");
		
		headers_1.put("test-azione", "signedJWT-PDNDv41ERRATA");
		_test(logCore, api_negoziazione, "signedJWT-PDNDv41", headers_1,
				true,
				"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation");
		headers_1.put("test-azione", "signedJWT-PDNDv41");
		
		
		
		// effettuo un altro test verificando che viene utilizzato il token salvato in cache
		
		_test(logCore, api_negoziazione, "signedJWT-PDNDv41", headers_0,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT-PDNDv41\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-PDNDv41\"",
				"\"transactionId\":\""+idRichiestaOriginale_0+"\""
				);
		
		_test(logCore, api_negoziazione, "signedJWT-PDNDv41", headers_1,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT-PDNDv41\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-PDNDv41\"",
				"\"transactionId\":\""+idRichiestaOriginale_1+"\""
				);
		
	}
	
	
	
	
	
	
	
	protected static HttpResponse _test(Logger logCore, String api, String operazione,
			Map<String, String> headers, 
			boolean error, String diagnostico,
			String ... tokenInfoCheck) throws Exception {
		
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		
		String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		
		if(headers!=null && !headers.isEmpty()) {
			for (String hdrName : headers.keySet()) {
				request.addHeader(hdrName, headers.get(hdrName));
			}
		}
		
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		request.setContent(content);
		request.setUrl(url);
						
		HttpResponse response = null;
		try {
			//System.out.println("INVOKE ["+request.getUrl()+"]");
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		if(!error) {
		
			long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
			verifyOk(response, 200, contentType);
						
			DBVerifier.verify(idTransazione, esitoExpected, null, tokenInfoCheck);
			
		}
		else {
			
			long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE);
			verifyOk(response, 503, HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);
			
			if(tokenInfoCheck==null || tokenInfoCheck.length==0) {
				// una registrazione avviene comunque
				tokenInfoCheck = new String[1];
				tokenInfoCheck[0] = "\"valid\":false";
			}
			
			DBVerifier.verify(idTransazione, esitoExpected, diagnostico, tokenInfoCheck);
			
		}
		
		return response;
		
	}
	
	public static void verifyOk(HttpResponse response, int code, String expectedContentType) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(expectedContentType, response.getContentType());
		
	}
}
