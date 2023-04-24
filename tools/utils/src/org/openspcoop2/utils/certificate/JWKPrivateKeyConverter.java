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
package org.openspcoop2.utils.certificate;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**	
 * JWKPrivateKeyConverter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JWKPrivateKeyConverter {

	public static void main(String [] args) throws UtilsException {
		
		if(args==null || args.length<3) {
			throw new UtilsException("ERROR: argomenti non forniti (USAGE: JWKPrivateKeyConverter pathPublicKey pathPrivateKey pathJWK [kid] [jwkset(true/false)] [pretty(true/false)])");
		}
		
		try {
					
			java.security.Security.addProvider(
			         new org.bouncycastle.jce.provider.BouncyCastleProvider()
			);
			
			String pathPublicKey = args[0];
			byte[] publicKey = FileSystemUtilities.readBytesFromFile(pathPublicKey);
			
			String pathPrivateKey = args[1];
			byte[] privateKey = FileSystemUtilities.readBytesFromFile(pathPrivateKey);
			
			KeyUtils keyUtils = new KeyUtils(KeyUtils.ALGO_RSA);
			
			PublicKey pKey = keyUtils.getPublicKey(publicKey);
			
			PrivateKey privKey = keyUtils.getPrivateKey(privateKey);
			
			String pathJWK = args[2];
						
			String kid = null;
			if(args.length>3) {
				kid = args[3];
			}
			if(kid==null || StringUtils.isEmpty(kid)) {
				kid = UUID.randomUUID().toString();
			}
			if(JWKPublicKeyConverter.KID_NULL.equals(kid)) {
				kid = null;
			}
			
			boolean jwks = true;
			if(args.length>4) {
				String tmp = args[4];
				jwks = "true".equals(tmp);
			}
			
			boolean pretty = false;
			if(args.length>5) {
				String tmp = args[5];
				pretty = "true".equals(tmp);
			}
			
			String json = convert(pKey, privKey, kid, jwks, pretty);
			
			FileSystemUtilities.writeFile(pathJWK, json.getBytes());
			
		}catch(Exception t) {
			throw new UtilsException(t.getMessage(),t);
		}
		
	}
	
	public static String convert(PublicKey pKey, PrivateKey privKey, String kid, boolean jwks, boolean pretty) throws UtilsException {
		JWK jwk = new JWK(pKey,privKey,kid);
		String json = null;
		
		if(jwks) {
			JWKSet jwkSet = new JWKSet();
			jwkSet.addJwk(jwk);
			json = pretty? jwk.getJsonPretty() : jwk.getJson();
			json = "{\"keys\":[" + json + "]}";
		}
		else {
			json = pretty? jwk.getJsonPretty() : jwk.getJson(); 
		}
		
		return json;
	}
}
