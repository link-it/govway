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

import java.security.PublicKey;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**	
 * JWKPublicKeyConverter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JWKPublicKeyConverter {

	public static final String KID_NULL = "#none#";
	
	public static void main(String [] args) throws UtilsException {
		
		if(args==null || args.length<2) {
			throw new UtilsException("ERROR: argomenti non forniti (USAGE: JWKPublicKeyConverter pathPublicKey pathJWK [kid] [jwkset(true/false)] [pretty(true/false)])");
		}
		
		try {
		
			java.security.Security.addProvider(
			         new org.bouncycastle.jce.provider.BouncyCastleProvider()
			);
			
			String pathPublicKey = args[0];
			byte[] publicKey = FileSystemUtilities.readBytesFromFile(pathPublicKey);
			
			KeyUtils keyUtils = new KeyUtils(KeyUtils.ALGO_RSA);
			
			PublicKey pKey = keyUtils.getPublicKey(publicKey);
			
			String pathJWK = args[1];
						
			String kid = null;
			if(args.length>2) {
				kid = args[2];
			}
			if(kid==null || StringUtils.isEmpty(kid)) {
				kid = UUID.randomUUID().toString();
			}
			if(KID_NULL.equals(kid)) {
				kid = null;
			}
			
			boolean jwks = true;
			if(args.length>3) {
				String tmp = args[3];
				jwks = "true".equals(tmp);
			}
			
			boolean pretty = false;
			if(args.length>4) {
				String tmp = args[4];
				pretty = "true".equals(tmp);
			}
			
			JWK jwk = new JWK(pKey, kid);
			String json = null;
			
			if(jwks) {
				JWKSet jwkSet = new JWKSet();
				jwkSet.addJwk(jwk);
				json = pretty? jwkSet.getJsonPretty() : jwkSet.getJson(); 
			}
			else {
				json = pretty? jwk.getJsonPretty() : jwk.getJson(); 
			}
			
			FileSystemUtilities.writeFile(pathJWK, json.getBytes());
			
		}catch(Exception t) {
			throw new UtilsException(t.getMessage(),t);
		}
		
	}
	
}
