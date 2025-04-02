/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;

/**
* NegoziazioneCustomTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class NegoziazioneKeystoreSenzaPassword extends ConfigLoader {

	public static final String API_NEGOZIAZIONE_SIGN = "TestNegoziazioneTokenKeystoreSenzaPassword";
	public static final String API_NEGOZIAZIONE_SSL = "TestNegoziazioneTokenSSLKeystoreSenzaPassword";
	
	private static Map<String, String> fillHeaders0() {
		Map<String, String> headers0 = new HashMap<>();
		headers0.put("test-azione", "signedJWT");
		headers0.put("test-suffix", "DYNAMIC");
		headers0.put("test-decode-position", "0");
		return headers0;
	}
	private static Map<String, String> fillHeaders1() {
		Map<String, String> headers1 = new HashMap<>();
		headers1.put("test-azione", "signedJWT");
		headers1.put("test-suffix", "DYNAMIC");
		headers1.put("test-decode-position", "1");
		return headers1;
	}
	
	
	@Test
	public void signKeystoreJksNoPasswordKeyNoPassword() throws Exception {
		test(API_NEGOZIAZIONE_SIGN, "keystoreJksNoPassword-KeyNoPassword", "TestNegoziazioneSignedJWT-keystoreJksNoPassword-KeyNoPassword");
	}
	@Test
	public void signKeystoreJksNoPasswordKeyWithPassword() throws Exception {
		test(API_NEGOZIAZIONE_SIGN, "keystoreJksNoPassword-KeyWithPassword", "TestNegoziazioneSignedJWT-keystoreJksNoPassword-KeyWithPassword");
	}
	@Test
	public void signKeystorePkcs12NoPasswordKeyNoPassword() throws Exception {
		test(API_NEGOZIAZIONE_SIGN, "keystorePkcs12NoPassword-KeyNoPassword", "TestNegoziazioneSignedJWT-keystorePkcs12NoPassword-KeyNoPassword");
	}
	@Test
	public void signKeystorePkcs12NoPasswordKeyWithPassword() throws Exception {
		test(API_NEGOZIAZIONE_SIGN, "keystorePkcs12NoPassword-KeyWithPassword", "TestNegoziazioneSignedJWT-keystorePkcs12NoPassword-KeyWithPassword");
	}
	
	
	
	
	@Test
	public void sslTruststoreJksSenzaPassword() throws Exception {
		test(API_NEGOZIAZIONE_SSL, "truststoreJksSenzaPassword", "TestNegoziazioneSignedJWT-TestSSL-truststoreJksSenzaPassword");
	}
	@Test
	public void sslTruststorePkcs12SenzaPassword() throws Exception {
		test(API_NEGOZIAZIONE_SSL, "truststorePkcs12SenzaPassword", "TestNegoziazioneSignedJWT-TestSSL-truststorePkcs12SenzaPassword");
	}
	
	
	
	
	
	private void test(String api, String azione, String policy) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		String prefix = API_NEGOZIAZIONE_SSL.equals(api) ? "https://Erogatore:8445" : "http://localhost:8080"; 
		
		String [] token = new String [] {
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\""+prefix+"/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\""+policy+"\""
		};
		
		// Test HDR
		
		Map<String, String> headers0 = fillHeaders0();
				
		NegoziazioneTest._test(logCore, api, azione, headers0,
				false,
				null,
				token);
		
		// Test PAYLOAD
		
		Map<String, String> headers1 = fillHeaders1(); 
				
		NegoziazioneTest._test(logCore, api, azione, headers1,
				false,
				null,
				token);
				
	}

}