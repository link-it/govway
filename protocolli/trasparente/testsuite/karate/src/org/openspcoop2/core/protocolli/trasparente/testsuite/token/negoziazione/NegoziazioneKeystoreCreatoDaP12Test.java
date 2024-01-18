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
public class NegoziazioneKeystoreCreatoDaP12Test extends ConfigLoader {

	public static final String API_NEGOZIAZIONE = "TestNegoziazioneTokenKeystoreCreatoDaP12";
	
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
	public void p12() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		String [] token = new String [] {
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-KeystoreInP12.p12\""
		};
		
		// Test HDR
		
		Map<String, String> headers0 = fillHeaders0();
				
		NegoziazioneTest._test(logCore, API_NEGOZIAZIONE, "p12", headers0,
				false,
				null,
				token);
		
		// Test PAYLOAD
		
		Map<String, String> headers1 = fillHeaders1(); 
				
		NegoziazioneTest._test(logCore, API_NEGOZIAZIONE, "p12", headers1,
				false,
				null,
				token);
				
	}
	
	
	@Test
	public void jks() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		String [] token = new String [] {
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-KeystoreInP12.jks\""
		};
		
		// Test HDR
		
		Map<String, String> headers0 = fillHeaders0();
		
		NegoziazioneTest._test(logCore, API_NEGOZIAZIONE, "jks", headers0,
				false,
				null,
				token);
		
		// Test PAYLOAD
		
		Map<String, String> headers1 = fillHeaders1(); 
		
		NegoziazioneTest._test(logCore, API_NEGOZIAZIONE, "jks", headers1,
				false,
				null,
				token);
				
	}

}