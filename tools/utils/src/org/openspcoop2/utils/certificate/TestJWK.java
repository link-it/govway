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

package org.openspcoop2.utils.certificate;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkReaderWriter;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;

import com.nimbusds.jose.jwk.KeyUse;

/**	
 * TestJWK
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestJWK {

	public static void main(String[] args) throws Exception {
		
		InputStream isKeystore = null;
		File fKeystore = null;
		
		InputStream jwks_isKeystore = null;
		File jwks_fKeystore = null;
		
		InputStream jwk_isKeystore = null;
		File jwk_fKeystore = null;
		
		InputStream isKeystoreJCEKS = null;
		File fKeystoreJCEKS = null;
		try{
			isKeystore = TestJWK.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jks");
			fKeystore = File.createTempFile("keystore", ".jks");
			FileSystemUtilities.writeFile(fKeystore, Utilities.getAsByteArray(isKeystore));
			
			jwks_isKeystore = TestJWK.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jwks");
			jwks_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwks_fKeystore, Utilities.getAsByteArray(jwks_isKeystore));
			
			jwk_isKeystore = TestJWK.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jwk");
			jwk_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwk_fKeystore, Utilities.getAsByteArray(jwk_isKeystore));
			
			isKeystoreJCEKS = TestJWK.class.getResourceAsStream("/org/openspcoop2/utils/security/example.jceks");
			fKeystoreJCEKS = File.createTempFile("keystore", "jceks");
			FileSystemUtilities.writeFile(fKeystoreJCEKS, Utilities.getAsByteArray(isKeystoreJCEKS));
			
			String passwordChiavePrivata = "key123456";
			String passwordStore = "123456";
			String alias = "openspcoop";
			KeyStore keystore = new KeyStore(fKeystore.getAbsolutePath(), passwordStore);
			KeyStore keystoreJCEKS = new KeyStore(fKeystoreJCEKS.getAbsolutePath(), "JCEKS", passwordStore);
			SecretKey secretKey = keystoreJCEKS.getSecretKey(alias, passwordChiavePrivata);
			
			
			System.out.println("=============== JWKSet (Costruttore String) ==============");
			
			JWKSet jwks_keystore = new JWKSet(FileSystemUtilities.readFile(jwks_fKeystore));
			List<JWK> jwk_set = jwks_keystore.getJwks();
			JsonWebKeys jwk_set_cxf = jwks_keystore.getJsonWebKeys();
			com.nimbusds.jose.jwk.JWKSet jwk_set_nimbusds = jwks_keystore.getJWKSet();
			printJKWSet(jwks_keystore);
			
			System.out.println("=============== JWKSet (Costruttore jwk_set) ==============");
			
			jwks_keystore = new JWKSet(jwk_set);
			printJKWSet(jwks_keystore);
			
			System.out.println("=============== JWKSet (Costruttore jwk_set_cxf) ==============");
			
			jwks_keystore = new JWKSet(jwk_set_cxf);
			printJKWSet(jwks_keystore);
			
			System.out.println("=============== JWKSet (Costruttore jwk_set_nimbusds) ==============");
			
			jwks_keystore = new JWKSet(jwk_set_nimbusds);
			printJKWSet(jwks_keystore);
			
			System.out.println("=============== JWKSet (Costruttore jwk_set from JKS) ==============");
			
			List<JWK> jwk_set_jks = new ArrayList<>();
			jwk_set_jks.add(new JWK(keystore,alias,passwordChiavePrivata));
			jwks_keystore = new JWKSet(jwk_set_jks);
			printJKWSet(jwks_keystore);
			
			
			System.out.println("=============== JWK (Costruttore String) ==============");
			JWK jwk_keystore = new JWK(FileSystemUtilities.readFile(jwk_fKeystore));
			JsonWebKey jwk_cxf = jwk_keystore.getJsonWebKey();
			com.nimbusds.jose.jwk.JWK jwk_nimbusds = jwk_keystore.getJWK();
			printJKW(jwk_keystore);
			
			System.out.println("=============== JWK (Costruttore jwk_cxf) ==============");
			jwk_keystore = new JWK(jwk_cxf);
			printJKW(jwk_keystore);
			
			System.out.println("=============== JWK (Costruttore jwk_nimbusds) ==============");
			jwk_keystore = new JWK(jwk_nimbusds);
			printJKW(jwk_keystore);
			
			System.out.println("=============== JWK (Costruttore jks + chiavePrivata) ==============");
			jwk_keystore = new JWK(keystore,alias,passwordChiavePrivata);
			printJKW(jwk_keystore);
			
			System.out.println("=============== JWK (Costruttore jks) ==============");
			jwk_keystore = new JWK(keystore,alias);
			printJKW(jwk_keystore);
			
			System.out.println("=============== JWK (Costruttore jks + chiavePrivata + use + nokid) ==============");
			jwk_keystore = new JWK(keystore,alias,passwordChiavePrivata,KeyUse.SIGNATURE,false);
			printJKW(jwk_keystore);
			
			System.out.println("=============== JWK (Costruttore jks + use + nokid) ==============");
			jwk_keystore = new JWK(keystore,alias,KeyUse.ENCRYPTION,false);
			printJKW(jwk_keystore);
			
			System.out.println("=============== JWK (Costruttore secretKey) ==============");
			jwk_keystore = new JWK(secretKey);
			printJKW(jwk_keystore);
			
			System.out.println("=============== JWK (Costruttore secretKey + use) ==============");
			jwk_keystore = new JWK(secretKey, alias, KeyUse.SIGNATURE);
			printJKW(jwk_keystore);
			
		}finally{
			try{
				if(isKeystore!=null){
					isKeystore.close();
				}
			}catch(Exception e){}
			try{
				if(fKeystore!=null){
					fKeystore.delete();
				}
			}catch(Exception e){}
			
			try{
				if(jwk_isKeystore!=null){
					jwk_isKeystore.close();
				}
			}catch(Exception e){}
			try{
				if(jwk_fKeystore!=null){
					jwk_fKeystore.delete();
				}
			}catch(Exception e){}
			
			try{
				if(jwks_isKeystore!=null){
					jwks_isKeystore.close();
				}
			}catch(Exception e){}
			try{
				if(jwks_fKeystore!=null){
					jwks_fKeystore.delete();
				}
			}catch(Exception e){}
			
			try{
				if(isKeystoreJCEKS!=null){
					isKeystoreJCEKS.close();
				}
			}catch(Exception e){}
			try{
				if(fKeystoreJCEKS!=null){
					fKeystoreJCEKS.delete();
				}
			}catch(Exception e){}
		}
	}

	private static void printJKWSet(JWKSet jwks_keystore) throws Exception {
		System.out.println("JSON: "+jwks_keystore.getJson());
		System.out.println("JSON Pretty: "+jwks_keystore.getJsonPretty());
		System.out.println("Node: "+jwks_keystore.getNode());
		System.out.println("List JWK: "+jwks_keystore.getJwks());
		JsonWebKeys jwk_set_cxf = jwks_keystore.getJsonWebKeys();
		System.out.println("JsonWebKeys (cxf): "+jwk_set_cxf);
		JwkReaderWriter engineCxf = new JwkReaderWriter();
		System.out.println("JsonWebKeys (cxf as string): "+engineCxf.jwkSetToJson(jwk_set_cxf));
		com.nimbusds.jose.jwk.JWKSet jwk_set_nimbusds = jwks_keystore.getJWKSet();
		System.out.println("JWKSet (nimbusds): "+jwk_set_nimbusds);
	}
	
	private static void printJKW(JWK jwk_keystore) throws Exception {
		System.out.println("JSON: "+jwk_keystore.getJson());
		System.out.println("JSON Pretty: "+jwk_keystore.getJsonPretty());
		System.out.println("Node: "+jwk_keystore.getNode());
		JsonWebKey jwk_cxf = jwk_keystore.getJsonWebKey();
		System.out.println("JsonWebKey (cxf): "+jwk_cxf);
		JwkReaderWriter engineCxf = new JwkReaderWriter();
		System.out.println("JsonWebKey (cxf as string): "+engineCxf.jwkToJson(jwk_cxf));
		com.nimbusds.jose.jwk.JWK jwk_nimbusds = jwk_keystore.getJWK();
		System.out.println("JWK (nimbusds): "+jwk_nimbusds);
	}
}
