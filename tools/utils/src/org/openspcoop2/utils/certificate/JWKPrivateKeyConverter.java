/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Private License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Private License for more details.
 *
 * You should have received a copy of the GNU General Private License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.utils.certificate;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.util.io.pem.PemReader;
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
			throw new UtilsException("ERROR: argomenti non forniti (USAGE: JWKPrivateKeyConverter pathPublicKey pathPrivateKey pathJWK [jwkset] [pretty])");
		}
		
		try {
					
			java.security.Security.addProvider(
			         new org.bouncycastle.jce.provider.BouncyCastleProvider()
			);
			
			String pathPublicKey = args[0];
			byte[] publicKey = FileSystemUtilities.readBytesFromFile(pathPublicKey);
			
			String pathPrivateKey = args[1];
			byte[] privateKey = FileSystemUtilities.readBytesFromFile(pathPrivateKey);
			
			KeyFactory kf = KeyFactory.getInstance("RSA");
			
			X509EncodedKeySpec specPub = new X509EncodedKeySpec(publicKey);
			PublicKey pKey = null;
			try {
				pKey = kf.generatePublic(specPub);
			}catch(Throwable t) {
				// provo PEM
				try (ByteArrayInputStream bin = new ByteArrayInputStream(publicKey);
					 InputStreamReader ir = new InputStreamReader(bin);
					 PemReader pemReader = new PemReader(ir);){
		            byte [] encoded = pemReader.readPemObject().getContent();
		            specPub = new X509EncodedKeySpec(encoded);
		            pKey = kf.generatePublic(specPub);
		        } 
			}
			
			PKCS8EncodedKeySpec specPriv = new PKCS8EncodedKeySpec(privateKey);
			PrivateKey privKey = null;
			try {
				privKey = kf.generatePrivate(specPriv);
			}catch(Throwable t) {
				// provo PEM
				try (ByteArrayInputStream bin = new ByteArrayInputStream(privateKey);
					 InputStreamReader ir = new InputStreamReader(bin);
					 PemReader pemReader = new PemReader(ir);){
		            byte [] encoded = pemReader.readPemObject().getContent();
		            specPriv = new PKCS8EncodedKeySpec(encoded);
		            privKey = kf.generatePrivate(specPriv);
		        } 
			}
			
			JWK jwk = new JWK(pKey,privKey);
			
			String pathJWK = args[2];
			
			String json = null;
			
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
			
			if(jwks) {
				JWKSet jwkSet = new JWKSet();
				jwkSet.addJwk(jwk);
				json = pretty? jwk.getJsonPretty() : jwk.getJson();
				json = "{\"keys\":[" + json + "]}";
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
