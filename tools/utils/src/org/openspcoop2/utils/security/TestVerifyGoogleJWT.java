/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
		String compactSign = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjBiMDFhOTU4YjY4MGI2MzhmMDU2YzE3ZWQ4NzQ4YmY0YzBiNmQzZTIiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI0MDc0MDg3MTgxOTIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI0MDc0MDg3MTgxOTIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDYyMzU2NTc1OTI2NTQzOTc2ODkiLCJhdF9oYXNoIjoiTlprQ1NPNms4eEZrQ3JBcG5QdFJNdyIsImlhdCI6MTU0NDUyMDk3NywiZXhwIjoxNTQ0NTI0NTc3fQ.EjJwbsrgC3bjclhmKVuh9yn1NoffdOadBDCZHTCIVWz4GQGDnRHC0rxPdH4GhXvuwBQLlD089xuQBPt7HuXR0Td09KGu-KrK7BDeFMm0YnZTZiOq8hdXdPTMBhfjVvusXTA5w0gLdKjqPFsNWWZ2SlvBUehfhyLQ3qWNO8SnnyVKDgyVqOiyp4sIY29lubCUs5Lf6AFdBci_iug1-jjnpsn9SMgtgYl6EIh1bs-oqpjwB4gefA3MlawmxUG2bw5Rqqs-l7jNihejR8Hkz2z0fGDE8u0woAklLA4lmJ_cTU7N_EoYTjcp4uCgQhHT6keTBMdvTU_l3afnthQ9wp8isg";
		
		try {
			JWTOptions options = new JWTOptions(JOSESerialization.COMPACT);
			JsonVerifySignature jsonCompactVerify = new JsonVerifySignature(verifySignatureProps, options);
			System.out.println("JsonCompactSignature Verify ("+jsonCompactVerify.verify(compactSign)+" ) payload: "+jsonCompactVerify.getDecodedPayload());
		}catch(Exception e) {
			if(e.getMessage().contains("NO_VERIFIER")) {
				System.out.println("JsonCompactSignature fallimento atteso: "+e.getMessage());
			}
			else {
				throw e;
			}
		}

		
	}

}
