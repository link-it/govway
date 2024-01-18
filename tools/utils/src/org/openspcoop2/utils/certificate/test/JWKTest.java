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

package org.openspcoop2.utils.certificate.test;

import java.io.File;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkReaderWriter;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.JWK;
import org.openspcoop2.utils.certificate.JWKPrivateKeyConverter;
import org.openspcoop2.utils.certificate.JWKPublicKeyConverter;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;

import com.nimbusds.jose.jwk.KeyUse;

/**	
 * TestJWK
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JWKTest {

	public static void main(String[] args) throws Exception {
		
		testJWKset();
		
		testKeystore();

		testSecretKey();
		
		testPrivatePublicKey();
		
		System.out.println("\n\nTestsuite finita");
	}
	
	public static void testJWKset() throws Exception {
		
		InputStream isKeystore = null;
		File fKeystore = null;
		
		InputStream jwks_isKeystore = null;
		File jwks_fKeystore = null;
		
		InputStream jwk_isKeystore = null;
		File jwk_fKeystore = null;
		
		try{
			isKeystore = JWKTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_example.jks");
			fKeystore = File.createTempFile("keystore", ".jks");
			FileSystemUtilities.writeFile(fKeystore, Utilities.getAsByteArray(isKeystore));
			
			jwks_isKeystore = JWKTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_example.jwks");
			jwks_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwks_fKeystore, Utilities.getAsByteArray(jwks_isKeystore));
			
			jwk_isKeystore = JWKTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_example.jwk");
			jwk_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwk_fKeystore, Utilities.getAsByteArray(jwk_isKeystore));
			
			String passwordChiavePrivata = "key123456";
			String passwordStore = "123456";
			String alias = "openspcoop";
			KeyStore keystore = new KeyStore(fKeystore.getAbsolutePath(), passwordStore);
			
			
			System.out.println("\n\n=============== JWKSet (Costruttore String) ==============");
			
			JWKSet jwks_keystore = new JWKSet(FileSystemUtilities.readFile(jwks_fKeystore));
			List<JWK> jwk_set = jwks_keystore.getJwks();
			JsonWebKeys jwk_set_cxf = jwks_keystore.getJsonWebKeys();
			com.nimbusds.jose.jwk.JWKSet jwk_set_nimbusds = jwks_keystore.getJWKSet();
			printJKWSet(jwks_keystore);
			
			System.out.println("\n\n=============== JWKSet (Costruttore jwk_set) ==============");
			
			jwks_keystore = new JWKSet(jwk_set);
			printJKWSet(jwks_keystore);
			
			System.out.println("\n\n=============== JWKSet (Costruttore jwk_set_cxf) ==============");
			
			jwks_keystore = new JWKSet(jwk_set_cxf);
			printJKWSet(jwks_keystore);
			
			System.out.println("\n\n=============== JWKSet (Costruttore jwk_set_nimbusds) ==============");
			
			jwks_keystore = new JWKSet(jwk_set_nimbusds);
			printJKWSet(jwks_keystore);
			
			System.out.println("\n\n=============== JWKSet (Costruttore jwk_set from JKS) ==============");
			
			List<JWK> jwk_set_jks = new ArrayList<>();
			jwk_set_jks.add(new JWK(keystore,alias,passwordChiavePrivata));
			jwks_keystore = new JWKSet(jwk_set_jks);
			printJKWSet(jwks_keystore);
			
						
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
			
		}
	}
	
	public static void testKeystore() throws Exception {
		
		InputStream isKeystore = null;
		File fKeystore = null;
		
		InputStream jwks_isKeystore = null;
		File jwks_fKeystore = null;
		
		InputStream jwk_isKeystore = null;
		File jwk_fKeystore = null;
		
		try{
			isKeystore = JWKTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_example.jks");
			fKeystore = File.createTempFile("keystore", ".jks");
			FileSystemUtilities.writeFile(fKeystore, Utilities.getAsByteArray(isKeystore));
			
			jwks_isKeystore = JWKTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_example.jwks");
			jwks_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwks_fKeystore, Utilities.getAsByteArray(jwks_isKeystore));
			
			jwk_isKeystore = JWKTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_example.jwk");
			jwk_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwk_fKeystore, Utilities.getAsByteArray(jwk_isKeystore));
			
			String passwordChiavePrivata = "key123456";
			String passwordStore = "123456";
			String alias = "openspcoop";
			KeyStore keystore = new KeyStore(fKeystore.getAbsolutePath(), passwordStore);
			
			
			
			System.out.println("\n\n=============== JWK (Costruttore String) ==============");
			JWK jwk_keystore = new JWK(FileSystemUtilities.readFile(jwk_fKeystore));
			JsonWebKey jwk_cxf = jwk_keystore.getJsonWebKey();
			com.nimbusds.jose.jwk.JWK jwk_nimbusds = jwk_keystore.getJWK();
			printJKW(jwk_keystore);
			
			System.out.println("\n\n=============== JWK (Costruttore jwk_cxf) ==============");
			jwk_keystore = new JWK(jwk_cxf);
			printJKW(jwk_keystore);
			
			System.out.println("\n\n=============== JWK (Costruttore jwk_nimbusds) ==============");
			jwk_keystore = new JWK(jwk_nimbusds);
			printJKW(jwk_keystore);
			
			
			System.out.println("\n\n=============== JWK (Costruttore jks) ==============");
			jwk_keystore = new JWK(keystore,alias);
			printJKW(jwk_keystore);
			
			System.out.println("\n\n=============== JWK (Costruttore jks + use) ==============");
			jwk_keystore = new JWK(keystore,alias,KeyUse.SIGNATURE);
			printJKW(jwk_keystore);
			
			System.out.println("\n\n=============== JWK (Costruttore jks + kid) ==============");
			jwk_keystore = new JWK(keystore,alias,true);
			printJKW(jwk_keystore);
			
			System.out.println("\n\n=============== JWK (Costruttore jks + use + kid) ==============");
			jwk_keystore = new JWK(keystore,alias,KeyUse.SIGNATURE,true);
			printJKW(jwk_keystore);
			
			System.out.println("\n\n=============== JWK (Costruttore jks + use + nokid) ==============");
			jwk_keystore = new JWK(keystore,alias,KeyUse.ENCRYPTION,false);
			printJKW(jwk_keystore);
			
			
						
			System.out.println("\n\n=============== JWK (Costruttore jks + chiavePrivata) ==============");
			jwk_keystore = new JWK(keystore,alias,passwordChiavePrivata);
			printJKW(jwk_keystore);
			
			System.out.println("\n\n=============== JWK (Costruttore jks + chiavePrivata + use) ==============");
			jwk_keystore = new JWK(keystore,alias,passwordChiavePrivata,KeyUse.ENCRYPTION);
			printJKW(jwk_keystore);

			System.out.println("\n\n=============== JWK (Costruttore jks + chiavePrivata + kid) ==============");
			jwk_keystore = new JWK(keystore,alias,passwordChiavePrivata,true);
			printJKW(jwk_keystore);
			
			System.out.println("\n\n=============== JWK (Costruttore jks + chiavePrivata + use + kid) ==============");
			jwk_keystore = new JWK(keystore,alias,passwordChiavePrivata,KeyUse.SIGNATURE,true);
			printJKW(jwk_keystore);
			
			System.out.println("\n\n=============== JWK (Costruttore jks + chiavePrivata + use + nokid) ==============");
			jwk_keystore = new JWK(keystore,alias,passwordChiavePrivata,KeyUse.SIGNATURE,false);
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
			
		}
	}
	
	
	
	public static void testPrivatePublicKey() throws Exception {
		
		try{
					
			KeyUtils keyUtils = new KeyUtils(KeyUtils.ALGO_RSA);
			
			byte [] publicKeyBytes = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.publicKey.pem"));
			PublicKey publicKey = keyUtils.getPublicKey(publicKeyBytes);
			
			byte [] privateKeyBytes = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs8.privateKey.pem"));
			PrivateKey privateKey = KeyUtils.getInstance().readPKCS8PrivateKeyPEMFormat(privateKeyBytes);
			
			
			
			System.out.println("\n\n=============== JWK (Costruttore publicKey) ==============");
			JWK jwk_keystore = new JWK(publicKey);
			printJKW(jwk_keystore);
			verificaMainConverter(jwk_keystore, publicKeyBytes, null);
			
			System.out.println("\n\n=============== JWK (Costruttore publicKey + alias) ==============");
			jwk_keystore = new JWK(publicKey, "myKey");
			printJKW(jwk_keystore);
			verificaMainConverter(jwk_keystore, publicKeyBytes, "myKey");
			
			System.out.println("\n\n=============== JWK (Costruttore publicKey + use) ==============");
			jwk_keystore = new JWK(publicKey, KeyUse.SIGNATURE);
			printJKW(jwk_keystore);
			
			System.out.println("\n\n=============== JWK (Costruttore publicKey + alias + use) ==============");
			jwk_keystore = new JWK(publicKey, "myKey", KeyUse.SIGNATURE);
			printJKW(jwk_keystore);
			
			
			
			System.out.println("\n\n=============== JWK (Costruttore publicKey + privateKey) ==============");
			jwk_keystore = new JWK(publicKey, privateKey);
			printJKW(jwk_keystore);
			verificaMainConverter(jwk_keystore, privateKeyBytes, publicKeyBytes, null);
			
			System.out.println("\n\n=============== JWK (Costruttore publicKey + privateKey + alias) ==============");
			jwk_keystore = new JWK(publicKey, privateKey, "myKey");
			printJKW(jwk_keystore);
			verificaMainConverter(jwk_keystore, privateKeyBytes, publicKeyBytes, "myKey");
			
			System.out.println("\n\n=============== JWK (Costruttore publicKey + privateKey + use) ==============");
			jwk_keystore = new JWK(publicKey, privateKey, KeyUse.SIGNATURE);
			printJKW(jwk_keystore);
			
			System.out.println("\n\n=============== JWK (Costruttore publicKey + privateKey + alias + use) ==============");
			jwk_keystore = new JWK(publicKey, privateKey, "myKey", KeyUse.SIGNATURE);
			printJKW(jwk_keystore);
			
			
			
		}finally {
			// ignore
		}
	}
	private static void verificaMainConverter(JWK jwk_keystore, byte [] publicKeyBytes, String kid) throws Exception {
		
		File fPublicKey = null;
		File fJWK = null;
		try {
			fPublicKey = File.createTempFile("publicKey", ".pem");
			FileSystemUtilities.writeFile(fPublicKey, publicKeyBytes);
			
			fJWK  = File.createTempFile("test", ".jwk");
			
			if(kid==null) {
				kid = JWKPublicKeyConverter.KID_NULL;
			}
			
			String [] args = new String[4];
			args[0] = fPublicKey.getAbsolutePath();
			args[1] = fJWK.getAbsolutePath();
			args[2] = kid;
			args[3] = "false";
			
			// jwk
			
			JWKPublicKeyConverter.main(args);
			
			System.out.println("\n --- JWKPublicKeyConverter from files");
			String readJWK = FileSystemUtilities.readFile(fJWK);
			JWK newJWK = new JWK(readJWK);
			printJKW(newJWK);
			
			if(!newJWK.getJson().equals(jwk_keystore.getJson())) {
				throw new Exception("JWK differiscono, ATTESO: ["+jwk_keystore.getJson()+"] OTTENUTO: ["+newJWK.getJson()+"]");
			}
			
			// jwkset
			
			args = new String[3];
			args[0] = fPublicKey.getAbsolutePath();
			args[1] = fJWK.getAbsolutePath();
			args[2] = kid;
			// default args[3] = "true";
			
			JWKPublicKeyConverter.main(args);
			
			System.out.println("\n --- JWKPublicKeyConverter (jwkset) from files");
			readJWK = FileSystemUtilities.readFile(fJWK);
			JWKSet newJWKset = new JWKSet(readJWK);
			printJKW(newJWKset.getJwks().get(0));
			
			if(!newJWKset.getJwks().get(0).getJson().equals(jwk_keystore.getJson())) {
				throw new Exception("JWK differiscono, ATTESO: ["+jwk_keystore.getJson()+"] OTTENUTO: ["+newJWKset.getJwks().get(0).getJson()+"]");
			}
			
			
			// jwk (pretty)
			
			args = new String[5];
			args[0] = fPublicKey.getAbsolutePath();
			args[1] = fJWK.getAbsolutePath();
			args[2] = kid;
			args[3] = "false";
			args[4] = "true";
			
			JWKPublicKeyConverter.main(args);
			
			System.out.println("\n --- JWKPublicKeyConverter (pretty) from files");
			readJWK = FileSystemUtilities.readFile(fJWK);
			newJWK = new JWK(readJWK);
			printJKW(newJWK);
			
			if(!newJWK.getJsonPretty().equals(jwk_keystore.getJsonPretty())) {
				throw new Exception("JWK differiscono, ATTESO: ["+jwk_keystore.getJsonPretty()+"] OTTENUTO: ["+newJWK.getJsonPretty()+"]");
			}
			
			// jwkset  (pretty)
			
			args = new String[5];
			args[0] = fPublicKey.getAbsolutePath();
			args[1] = fJWK.getAbsolutePath();
			args[2] = kid;
			args[3] = "true";
			args[4] = "true";
			
			JWKPublicKeyConverter.main(args);
			
			System.out.println("\n --- JWKPublicKeyConverter (jwkset) from files");
			readJWK = FileSystemUtilities.readFile(fJWK);
			newJWKset = new JWKSet(readJWK);
			printJKW(newJWKset.getJwks().get(0));
			
			if(!newJWKset.getJwks().get(0).getJsonPretty().equals(jwk_keystore.getJsonPretty())) {
				throw new Exception("JWK differiscono, ATTESO: ["+jwk_keystore.getJsonPretty()+"] OTTENUTO: ["+newJWKset.getJwks().get(0).getJsonPretty()+"]");
			}
			
		}finally {
			try{
				if(fPublicKey!=null){
					fPublicKey.delete();
				}
			}catch(Exception e){
				// close
			}
			try{
				if(fJWK!=null){
					fJWK.delete();
				}
			}catch(Exception e){
				// close
			}
		}
	}
	private static void verificaMainConverter(JWK jwk_keystore, byte [] privateKeyBytes, byte [] publicKeyBytes, String kid) throws Exception {
		
		File fPrivateKey = null;
		File fPublicKey = null;
		File fJWK = null;
		try {
			fPrivateKey = File.createTempFile("privateKey", ".pem");
			FileSystemUtilities.writeFile(fPrivateKey, privateKeyBytes);
			
			fPublicKey = File.createTempFile("publicKey", ".pem");
			FileSystemUtilities.writeFile(fPublicKey, publicKeyBytes);
			
			fJWK  = File.createTempFile("test", ".jwk");
			
			if(kid==null) {
				kid = JWKPublicKeyConverter.KID_NULL;
			}
			
			String [] args = new String[5];
			args[0] = fPublicKey.getAbsolutePath();
			args[1] = fPrivateKey.getAbsolutePath();
			args[2] = fJWK.getAbsolutePath();
			args[3] = kid;
			args[4] = "false";
			
			// jwk
			
			JWKPrivateKeyConverter.main(args);
			
			System.out.println("\n --- JWKPrivateKeyConverter from files");
			String readJWK = FileSystemUtilities.readFile(fJWK);
			JWK newJWK = new JWK(readJWK);
			printJKW(newJWK);
			
			if(!newJWK.getJson().equals(jwk_keystore.getJson())) {
				throw new Exception("JWK differiscono, ATTESO: ["+jwk_keystore.getJson()+"] OTTENUTO: ["+newJWK.getJson()+"]");
			}
						
			// jwkset
			
			args = new String[4];
			args[0] = fPublicKey.getAbsolutePath();
			args[1] = fPrivateKey.getAbsolutePath();
			args[2] = fJWK.getAbsolutePath();
			args[3] = kid;
			// default args[4] = "true";
			
			JWKPrivateKeyConverter.main(args);
			
			System.out.println("\n --- JWKPrivateKeyConverter (jwkset) from files");
			readJWK = FileSystemUtilities.readFile(fJWK);
			JWKSet newJWKset = new JWKSet(readJWK);
			printJKW(newJWKset.getJwks().get(0));
			
			if(!newJWKset.getJwks().get(0).getJson().equals(jwk_keystore.getJson())) {
				throw new Exception("JWK differiscono, ATTESO: ["+jwk_keystore.getJson()+"] OTTENUTO: ["+newJWKset.getJwks().get(0).getJson()+"]");
			}
			
			// jwk (pretty)
			
			args = new String[6];
			args[0] = fPublicKey.getAbsolutePath();
			args[1] = fPrivateKey.getAbsolutePath();
			args[2] = fJWK.getAbsolutePath();
			args[3] = kid;
			args[4] = "false";
			args[5] = "true";
			
			JWKPrivateKeyConverter.main(args);
			
			System.out.println("\n --- JWKPrivateKeyConverter (pretty) from files");
			readJWK = FileSystemUtilities.readFile(fJWK);
			newJWK = new JWK(readJWK);
			printJKW(newJWK);
			
			if(!newJWK.getJsonPretty().equals(jwk_keystore.getJsonPretty())) {
				throw new Exception("JWK differiscono, ATTESO: ["+jwk_keystore.getJsonPretty()+"] OTTENUTO: ["+newJWK.getJsonPretty()+"]");
			}
			
			// jwkset  (pretty)
			
			args = new String[6];
			args[0] = fPublicKey.getAbsolutePath();
			args[1] = fPrivateKey.getAbsolutePath();
			args[2] = fJWK.getAbsolutePath();
			args[3] = kid;
			args[4] = "true";
			args[5] = "true";
			
			JWKPrivateKeyConverter.main(args);
			
			System.out.println("\n --- JWKPrivateKeyConverter (jwkset) from files");
			readJWK = FileSystemUtilities.readFile(fJWK);
			newJWKset = new JWKSet(readJWK);
			printJKW(newJWKset.getJwks().get(0));
			
			if(!newJWKset.getJwks().get(0).getJsonPretty().equals(jwk_keystore.getJsonPretty())) {
				throw new Exception("JWK differiscono, ATTESO: ["+jwk_keystore.getJsonPretty()+"] OTTENUTO: ["+newJWKset.getJwks().get(0).getJsonPretty()+"]");
			}
			
		}finally {
			try{
				if(fPrivateKey!=null){
					fPrivateKey.delete();
				}
			}catch(Exception e){
				// close
			}
			try{
				if(fPublicKey!=null){
					fPublicKey.delete();
				}
			}catch(Exception e){
				// close
			}
			try{
				if(fJWK!=null){
					fJWK.delete();
				}
			}catch(Exception e){
				// close
			}
		}
	}
	
	public static void testSecretKey() throws Exception {
				
		InputStream isKeystoreJCEKS = null;
		File fKeystoreJCEKS = null;
		try{
					
			isKeystoreJCEKS = JWKTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/example.jceks");
			fKeystoreJCEKS = File.createTempFile("keystore", "jceks");
			FileSystemUtilities.writeFile(fKeystoreJCEKS, Utilities.getAsByteArray(isKeystoreJCEKS));
			
			String passwordChiavePrivata = "key123456";
			String passwordStore = "123456";
			String alias = "openspcoop";
			KeyStore keystoreJCEKS = new KeyStore(fKeystoreJCEKS.getAbsolutePath(), "JCEKS", passwordStore);
			SecretKey secretKey = keystoreJCEKS.getSecretKey(alias, passwordChiavePrivata);
			
			System.out.println("\n\n=============== JWK (Costruttore secretKey) ==============");
			JWK jwk_keystore = new JWK(secretKey);
			printJKW(jwk_keystore);
			
			System.out.println("\n\n=============== JWK (Costruttore secretKey + use) ==============");
			jwk_keystore = new JWK(secretKey, alias, KeyUse.SIGNATURE);
			printJKW(jwk_keystore);
			
		}finally{			
			try{
				if(isKeystoreJCEKS!=null){
					isKeystoreJCEKS.close();
				}
			}catch(Exception e){}
			try{
				if(fKeystoreJCEKS!=null){
					fKeystoreJCEKS.delete();
				}
			}catch(Exception e){
				// close
			}
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
