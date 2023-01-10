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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.util.io.pem.PemReader;
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

	public static void main(String [] args) throws UtilsException {
		
		if(args==null || args.length<2) {
			throw new UtilsException("ERROR: argomenti non forniti (USAGE: JWKPublicKeyConverter pathPublicKey pathJWK [jwkset] [pretty])");
		}
		
		try {
		
			java.security.Security.addProvider(
			         new org.bouncycastle.jce.provider.BouncyCastleProvider()
			);
			
			String pathPublicKey = args[0];
			byte[] publicKey = FileSystemUtilities.readBytesFromFile(pathPublicKey);
			
			X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
			KeyFactory kf = KeyFactory.getInstance("RSA");
				    
			PublicKey pKey = null;
			try {
				pKey = kf.generatePublic(spec);
			}catch(Throwable t) {
				// provo PEM
				try (ByteArrayInputStream bin = new ByteArrayInputStream(publicKey);
					 InputStreamReader ir = new InputStreamReader(bin);
					 PemReader pemReader = new PemReader(ir);){
		            byte [] encoded = pemReader.readPemObject().getContent();
		            spec = new X509EncodedKeySpec(encoded);
		            pKey = kf.generatePublic(spec);
		        } 
			}
			JWK jwk = new JWK(pKey);
			
			String pathJWK = args[1];
			
			String json = null;
			
			boolean jwks = true;
			if(args.length>2) {
				String tmp = args[2];
				jwks = "true".equals(tmp);
			}
			
			boolean pretty = false;
			if(args.length>3) {
				String tmp = args[3];
				pretty = "true".equals(tmp);
			}
			
			if(jwks) {
				JWKSet jwkSet = new JWKSet();
				jwkSet.addJwk(jwk);
				json = pretty? jwkSet.getJsonPretty() : jwkSet.getJson(); 
			}
			else {
				json = pretty? jwk.getJsonPretty() : jwk.getJson(); 
			}
			
			FileSystemUtilities.writeFile(pathJWK, json.getBytes());
			
		}catch(Throwable t) {
			throw new UtilsException(t.getMessage(),t);
		}
		
	}
	
}
