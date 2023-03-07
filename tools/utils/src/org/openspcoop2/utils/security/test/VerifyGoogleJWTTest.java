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

package org.openspcoop2.utils.security.test;
import java.util.Properties;

import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWTOptions;
import org.openspcoop2.utils.security.JsonVerifySignature;

/**	
 * TestVerifyGoogleJWT
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VerifyGoogleJWTTest {

	public static void main(String[] args) throws Exception {
		test();
	}
	
	public static void test() throws Exception {
		
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
		String compactSign = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjQ4NmYxNjQ4MjAwNWEyY2RhZjI2ZDkyMTQwMThkMDI5Y2E0NmZiNTYiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI0MDc0MDg3MTgxOTIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI0MDc0MDg3MTgxOTIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDYyMzU2NTc1OTI2NTQzOTc2ODkiLCJhdF9oYXNoIjoiVy1ELXNPa0w4cUpBVWNleEZNWmRkUSIsImlhdCI6MTY1MzQ3Mzc4MywiZXhwIjoxNjUzNDc3MzgzfQ.chyZymyS4Ie1ZU-WWlQGDMYSLp4fcH4OD8wJovR4SQH6LQRYj2SuC_K7xUsrXWSe71X24bCo1dkJi6L56jDGvmYhEHaQ4nya0xeizMoE8GJ_VNALwD3YvhjcxTZ7pPgjbGdL1ej9VTAYxyCWGf4qGueJlYp3tVawkhdeqd-sGXm4nc6jOriD0Ynb_3wbCM7uqh7D3-B0ujEcmmvR6SSaKYfbmjDPTIdG8YBEgEWSsPqfXz8n2Y_oKlDTOEg5LcrYKbl-ek0AcrqlWVhx84J7bRGW8WCtOL0Lo8D0cdjD4ho78zD6-En_cOqoLUoh9oJIiBbOObFL37udaDtLRFFJvw";
		
		try {
			JWTOptions options = new JWTOptions(JOSESerialization.COMPACT);
			JsonVerifySignature jsonCompactVerify = new JsonVerifySignature(verifySignatureProps, options);
			System.out.println("JsonCompactSignature Verify ("+jsonCompactVerify.verify(compactSign)+" ) payload: "+jsonCompactVerify.getDecodedPayload());
		}catch(Exception e) {
			if(e.getMessage().contains("NO_VERIFIER")) {
				System.out.println("JsonCompactSignature fallimento atteso: "+e.getMessage());
				System.out.println("WARNING: aggiornare l'id_token tramite playground selezionando solo lo scope 'oauth2 -> openid'");
			}
			else {
				throw e;
			}
		}

		
	}

}
