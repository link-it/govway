/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.security;
import java.util.Properties;

/**	
 * TestVerifyGoogleJWT
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestVerifyGoogleJWT {

	public static void main(String[] args) throws Exception {
		
		// https://developers.google.com/identity/protocols/OpenIDConnect#discovery
		
		Properties verifySignatureProps = new Properties();
		verifySignatureProps.setProperty("rs.security.keystore.type", "jwk");
		verifySignatureProps.setProperty("rs.security.keystore.file", "https://www.googleapis.com/oauth2/v3/certs");
		verifySignatureProps.setProperty("rs.security.keystore.alias", "*");
		
//		verifySignatureProps.setProperty("rs.security.keystore.type.ssl", "jks");
//		verifySignatureProps.setProperty("rs.security.keystore.file.ssl", "/opt/local/GIT_GOV/GovWay/core/deploy/jks/Token.jks");
//		verifySignatureProps.setProperty("rs.security.keystore.password.ssl", "123456");
//		
		// NOTA: Questo test non funziona se il token qua sotto è troppo vecchio e si usa l'opzione '*'.
		//       Il motivo risiede nel fatto che con '*' l'alias viene cercato nel token stesso, e se google ha aggiornato i certificati, non vi sarà un match tra i kids
		//       Ogni tanto aggiornare il token
		String compactSign = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImMzMTA0YzY4OGMxNWU2YjhlNThlNjdhMzI4NzgwOTUyYjIxNzQwMTciLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI0MDc0MDg3MTgxOTIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI0MDc0MDg3MTgxOTIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDYyMzU2NTc1OTI2NTQzOTc2ODkiLCJhdF9oYXNoIjoiUmpKb3FIemozMzlrWGpEVU5CdzNtdyIsImlhdCI6MTYzMjMwOTE0OCwiZXhwIjoxNjMyMzEyNzQ4fQ.qcy8D6wWbwJsIlcTxz2m4dnFFerf4rg6_OcVGFOhJ1vNZjS3QsW5ybG5XGuNisN2JQqUQMxlAeCs6-MxD_m0uIBkIA7-BNtWfBi0c6WH3gjG1jBa4uHJDvHX4JqH1PeHJxQ0lt6fn36KxT-zog_6ZkGSt3jKCCeNyGBtyVGsB1DyDH115Cz1OHpiz2SW-z_xouJVcVAIAcismRa-dLseQ56NtodrDWQWFM-AL0hJy3fFUFcDxQZpBYV2vkR6KImzB50iPlsYI0w9-baLfbva1oc6qAnb2ZaOtT9TZOBf5oCGGDeX12S4-lPh87xwjmhgGSGxxNgen1Y3z3gqg6D-fg";
		
		try {
			JWTOptions options = new JWTOptions(JOSESerialization.COMPACT);
			JsonVerifySignature jsonCompactVerify = new JsonVerifySignature(verifySignatureProps, options);
			System.out.println("JsonCompactSignature Verify ("+jsonCompactVerify.verify(compactSign)+" ) payload: "+jsonCompactVerify.getDecodedPayload());
		}catch(Exception e) {
			if(e.getMessage().contains("NO_VERIFIER")) {
				System.out.println("JsonCompactSignature fallimento atteso: "+e.getMessage());
				System.out.println("WARNING: aggiornare l'id_token tramite playground selezionando solo lo scope 'oauth2'");
			}
			else {
				throw e;
			}
		}

		
	}

}
