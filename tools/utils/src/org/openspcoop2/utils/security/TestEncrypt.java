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
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.crypto.SecretKey;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.xml.security.encryption.XMLCipher;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.JWK;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Element;

/**	
 * TestEncrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestEncrypt {

	public static void main(String[] args) throws Exception {
		
		TipoTest tipoTest = null;
		if(args!=null && args.length>0) {
			tipoTest = TipoTest.valueOf(args[0]);
		}
		
		InputStream isKeystore = null;
		File fKeystore = null;
		InputStream isTruststore = null;
		File fTruststore = null;
		
		InputStream isKeystoreJCEKS = null;
		File fKeystoreJCEKS = null;
		
		File fCertX509 = null;
		
		InputStream jwks_isKeystore = null;
		File jwks_fKeystore = null;
		InputStream jwks_isTruststore = null;
		File jwks_fTruststore = null;
		
		InputStream jwk_isKeystore = null;
		File jwk_fKeystore = null;
		InputStream jwk_isTruststore = null;
		File jwk_fTruststore = null;
		try{
			isKeystore = TestEncrypt.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jks");
			fKeystore = File.createTempFile("keystore", "jks");
			FileSystemUtilities.writeFile(fKeystore, Utilities.getAsByteArray(isKeystore));
			
			isTruststore = TestEncrypt.class.getResourceAsStream("/org/openspcoop2/utils/security/truststore_example.jks");
			fTruststore = File.createTempFile("truststore", "jks");
			FileSystemUtilities.writeFile(fTruststore, Utilities.getAsByteArray(isTruststore));
			
			isKeystoreJCEKS = TestEncrypt.class.getResourceAsStream("/org/openspcoop2/utils/security/example.jceks");
			fKeystoreJCEKS = File.createTempFile("keystore", "jceks");
			FileSystemUtilities.writeFile(fKeystoreJCEKS, Utilities.getAsByteArray(isKeystoreJCEKS));
			
			jwks_isKeystore = TestEncrypt.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jwks");
			jwks_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwks_fKeystore, Utilities.getAsByteArray(jwks_isKeystore));
			
			jwks_isTruststore = TestEncrypt.class.getResourceAsStream("/org/openspcoop2/utils/security/truststore_example.jwks");
			jwks_fTruststore = File.createTempFile("truststore", ".jwk");
			FileSystemUtilities.writeFile(jwks_fTruststore, Utilities.getAsByteArray(jwks_isTruststore));
			
			jwk_isKeystore = TestEncrypt.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jwk");
			jwk_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwk_fKeystore, Utilities.getAsByteArray(jwk_isKeystore));
			
			jwk_isTruststore = TestEncrypt.class.getResourceAsStream("/org/openspcoop2/utils/security/truststore_example.jwk");
			jwk_fTruststore = File.createTempFile("truststore", ".jwk");
			FileSystemUtilities.writeFile(jwk_fTruststore, Utilities.getAsByteArray(jwk_isTruststore));
			
			String passwordChiavePrivata = "key123456";
			String passwordStore = "123456";
			String alias = "openspcoop";
			
			KeyStore keystore = new KeyStore(fKeystore.getAbsolutePath(), passwordStore);
			KeyStore truststore = new KeyStore(fTruststore.getAbsolutePath(), passwordStore);
			KeyStore keystoreJCEKS = new KeyStore(fKeystoreJCEKS.getAbsolutePath(), "JCEKS", passwordStore);
			
			fCertX509 =  File.createTempFile("cert", ".cer");
			FileSystemUtilities.writeFile(fCertX509, truststore.getCertificate(alias).getEncoded());
			
			JsonWebKey jwk_keystore = new JWK(FileSystemUtilities.readFile(jwk_fKeystore)).getJsonWebKey();
			JsonWebKey jwk_truststore = new JWK(FileSystemUtilities.readFile(jwk_fTruststore)).getJsonWebKey();
			JsonWebKeys jwks_keystore = new JWKSet(FileSystemUtilities.readFile(jwks_fKeystore)).getJsonWebKeys();
			JsonWebKeys jwks_truststore = new JWKSet(FileSystemUtilities.readFile(jwks_fTruststore)).getJsonWebKeys();
			
			
			JwtHeaders jwtHeader = new JwtHeaders();
			jwtHeader.setContentType("application/json");
			jwtHeader.setType("application/json[0]");
			jwtHeader.addCriticalHeader("a1");
			jwtHeader.addCriticalHeader("a2");
			jwtHeader.addExtension("a1", "v1");
			jwtHeader.addExtension("a2", "v2");
			jwtHeader.addExtension("a3", "v3");
			
			
			
			// Esempio Encrypt Java 
			
			if(tipoTest==null || TipoTest.JAVA_ENCRYPT.equals(tipoTest)) {
				testJava(TipoTest.JAVA_ENCRYPT, keystore, truststore, alias, passwordChiavePrivata);
			}
			
			
			
			// Esempio Signature Xml 
			
			if(tipoTest==null || TipoTest.XML_ENCRYPT.equals(tipoTest)) {
				testXmlEncrypt(TipoTest.XML_ENCRYPT, keystore, truststore, keystoreJCEKS, alias, passwordChiavePrivata);
			}
			
			
			
			// Esempio Signature JSON
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JKS.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS, fKeystore, fTruststore, null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM, fKeystore, fTruststore, jwtHeader);
				jwtHeader.setX509Url(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JWK.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK, jwks_fKeystore, jwks_fTruststore, null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM, jwks_fKeystore, jwks_fTruststore, jwtHeader);
				jwtHeader.setJwkUrl(null);
			}

			
			
			// Esempio Signature JSON con altri costruttori
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_KEYSTORE.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystore.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(alias);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_KEYSTORE, keystore, truststore, alias, passwordChiavePrivata, jwtHeader);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_KEYSTORE_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystore.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				jwtHeader.setKid(alias);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_KEYSTORE_HEADER_CUSTOM, keystore, truststore, alias, passwordChiavePrivata, jwtHeader);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
			}
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_KEYSTORE_JCE.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystore.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(alias);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_KEYSTORE_JCE, keystoreJCEKS, alias, passwordChiavePrivata, jwtHeader);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_KEYSTORE_JCE_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystore.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				jwtHeader.setKid(alias);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_KEYSTORE_JCE_HEADER_CUSTOM, keystoreJCEKS, alias, passwordChiavePrivata, jwtHeader);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
			}
			
			

			
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_KEYS.equals(tipoTest)) {
				
				jwtHeader.setJwKey(jwks_keystore, alias);
				jwtHeader.setKid(alias);
				
				testJsonJwkKeys(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_KEYS, jwks_keystore, jwks_truststore, alias, jwtHeader);
				
				jwtHeader.setJwKey(null);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_KEYS_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));
				
				testJsonJwkKeys(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_KEYS, jwks_keystore, jwks_truststore, alias, jwtHeader);
				
				jwtHeader.setJwkUrl(null);
				jwtHeader.setKid(null);
			}
			
			
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_KEY.equals(tipoTest)) {
				
				jwtHeader.setJwKey(jwk_keystore);
				jwtHeader.setKid(alias);
				
				testJsonJwkKey(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_KEY, jwk_keystore, jwk_truststore, jwtHeader);
				
				jwtHeader.setJwKey(null);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_KEY_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));
				
				testJsonJwkKey(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_KEY, jwk_keystore, jwk_truststore, jwtHeader);
				
				jwtHeader.setJwkUrl(null);
				jwtHeader.setKid(null);
			}
			
			

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
				if(isTruststore!=null){
					isTruststore.close();
				}
			}catch(Exception e){}
			try{
				if(fTruststore!=null){
					fTruststore.delete();
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
			
			try{
				if(fCertX509!=null){
					fCertX509.delete();
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
				if(jwk_isTruststore!=null){
					jwk_isTruststore.close();
				}
			}catch(Exception e){}
			try{
				if(jwk_fTruststore!=null){
					jwk_fTruststore.delete();
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
				if(jwks_isTruststore!=null){
					jwks_isTruststore.close();
				}
			}catch(Exception e){}
			try{
				if(jwks_fTruststore!=null){
					jwks_fTruststore.delete();
				}
			}catch(Exception e){}
		}
	}
	
	private static void testJava(TipoTest tipo, KeyStore keystore, KeyStore truststore, String alias, String passwordChiavePrivata) throws Exception {
		System.out.println("\n\n ============== "+tipo+" ==================");
		System.out.println("["+tipo+"]. Example JavaEncrypt \n");
		
		String contenutoDaCifrare = "MarioRossi:23:05:1980";
		
		// 1a Cifratura con chiave privata e decifratura con chiave pubblica. Si tratta anche di un caso di firma implicita
		
		// Encrypt
		String encryptAlgorithm = "RSA";
		Encrypt encrypt = new Encrypt(keystore, alias, passwordChiavePrivata);
		byte[] encryptedBytes = encrypt.encrypt(contenutoDaCifrare.getBytes(), encryptAlgorithm);
		System.out.println("["+tipo+"]-a. JavaEncrypt Encrypted (Private): "+new String(encryptedBytes));
		
		// Decrypt
		Decrypt decrypt = new Decrypt(truststore,alias);
		System.out.println("["+tipo+"]-a. JavaEncrypt Decrypted (Public): "+new String(decrypt.decrypt(encryptedBytes, encryptAlgorithm)));
		
		
		// 1b Cifratura con chiave pubblica e decifratura con chiave privata.
		
		// Encrypt
		encrypt = new Encrypt(truststore, alias);
		encryptedBytes = encrypt.encrypt(contenutoDaCifrare.getBytes(), encryptAlgorithm);
		System.out.println("["+tipo+"]-b. JavaEncrypt Encrypted (Public): "+new String(encryptedBytes));
		
		// Decrypt
		decrypt = new Decrypt(keystore,alias,passwordChiavePrivata);
		System.out.println("["+tipo+"]-b. JavaEncrypt Decrypted (Private): "+new String(decrypt.decrypt(encryptedBytes, encryptAlgorithm)));
	}
	
	
	private static void testXmlEncrypt(TipoTest tipo, KeyStore keystore, KeyStore truststore, KeyStore keystoreJCEKS, String alias, String passwordChiavePrivata) throws Exception {
		System.out.println("\n\n ============== "+tipo+" ==================");
		System.out.println("["+tipo+"]-. Example XmlEncrypt \n");
		
		String xmlInput = "<prova><test>VALORE</test></prova>";
		Element node = XMLUtils.getInstance().newElement(xmlInput.getBytes());
		
		
		// 2a Cifratura con chiave simmetrica
		
		// I test possibili sono 3:
		// - 1) Cifratura con chiave simmetrica. La chiave simmetrica fornita viene utilizzata direttamente per cifrare il contenuto.
		//      Faremo due test, uno con una chiave simmetrica generata, ed un altro con una chiave simmetrica presa da un keystore JCEKS
		// - 2) La cifratura avviene con una chiave simmetrica generata ogni volta, la quale viene poi cifrata con una chiave fornita dall'utente. Questo meccanismo si chiama wrapped key
		//      Faremo due test, uno in cui la chiave fornita dall'utente è una chiave asimmetrica ed uno in cui la chiave fornita è una chiave simmetrica.
		//      E' quindi possibile usare la chiave simmetrica per "wrappare" la chiave simmetrica generata dinamicamente.
		//      In quest'ultimo esempio è necessario anche indicare l'algoritmo della chiave generata.
		
		// Chiave simmetrica
		String keyAlgorithm = "AES";
		String canonicalizationAlgorithm = null;
		String digestAlgorithm = "SHA-256";
		SecretKey secretKeyGenerata = AbstractXmlCipher.generateSecretKey(keyAlgorithm);
		// Si pup usareanche DESede per TRIPLEDES
		// keytool -genseckey -alias 'openspcoop' -keyalg 'AES' -keystore example.jceks -storepass '123456' -keypass 'key123456' -storetype "JCEKS" -keysize 256
		@SuppressWarnings("unused")
		SecretKey secretKeyLetta = keystoreJCEKS.getSecretKey(alias, passwordChiavePrivata);
		
		// *** TEST 1a:  Cifratura con chiave simmetrica non wrapped; chiave simmetrica utilizzata generata precedentemente. 
					
		// Crifratura
		String encryptAlgorithm = XMLCipher.AES_128;
		XmlEncrypt xmlEncrypt = new XmlEncrypt(SymmetricKeyWrappedMode.SYM_ENC_KEY_NO_WRAPPED, secretKeyGenerata);
		xmlEncrypt.encryptSymmetric(node, encryptAlgorithm, canonicalizationAlgorithm, digestAlgorithm);
		System.out.println("["+tipo+"]- (1a) Xml Encrypt (SymmetricKey-Generata): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		// Decifratura
		XmlDecrypt xmlDecrypt = new XmlDecrypt(SymmetricKeyWrappedMode.SYM_ENC_KEY_NO_WRAPPED, secretKeyGenerata);
		xmlDecrypt.decrypt(node);
		System.out.println("["+tipo+"]- (1a) Xml Decrypt (SymmetricKey-Generata): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		
		// *** TEST 1b:  Cifratura con chiave simmetrica non wrapped; chiave simmetrica letta da un keystore JCEKS
				
		// Crifratura
		boolean symmetricKey = true;
		encryptAlgorithm = XMLCipher.AES_128;
		//xmlEncrypt = new XmlEncrypt(secretKeyLetta);
		xmlEncrypt = new XmlEncrypt(keystoreJCEKS, symmetricKey, SymmetricKeyWrappedMode.SYM_ENC_KEY_NO_WRAPPED, alias, passwordChiavePrivata);
		xmlEncrypt.encryptSymmetric(node, encryptAlgorithm);
		System.out.println("["+tipo+"]- (1b) Xml Encrypt (SymmetricKey-Letta): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		// Decifratura
		//xmlDecrypt = new XmlDecrypt(secretKeyLetta);
		xmlDecrypt = new XmlDecrypt(keystoreJCEKS, symmetricKey, SymmetricKeyWrappedMode.SYM_ENC_KEY_NO_WRAPPED, alias, passwordChiavePrivata);
		xmlDecrypt.decrypt(node);
		System.out.println("["+tipo+"]- (1b) Xml Decrypt (SymmetricKey-Letta): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		
		// *** TEST 2a:  Cifratura con wrapped della chiave simmetrica generata dinamicanete; il wrapped avviente tramite una chiave asimmetrica
		
		// 2b Cifratura con chiave simmetrica wrapped ed utilizzo di chiavi asimmetriche
		//    Cifratura con chiave privata e decifratura con chiave pubblica. Si tratta anche di un caso di firma implicita
		
		// Crifratura
		keyAlgorithm = "AES";
		encryptAlgorithm = XMLCipher.AES_128;
		String wrappedKeyAlgorithm = XMLCipher.RSA_v1dot5;
		xmlEncrypt = new XmlEncrypt(SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY, keystore,alias,passwordChiavePrivata);
		xmlEncrypt.encrypt(node, encryptAlgorithm, canonicalizationAlgorithm, digestAlgorithm, keyAlgorithm, wrappedKeyAlgorithm);
		System.out.println("["+tipo+"]- (a1). Xml Encrypt (Private Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		// Decifratura
		xmlDecrypt = new XmlDecrypt(truststore, alias);
		xmlDecrypt.decrypt(node);
		System.out.println("["+tipo+"]- (a1) Xml Decrypt (Public Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		
		// 2b Cifratura con chiave simmetrica wrapped ed utilizzo di chiavi asimmetriche
		//	  Cifratura con chiave pubblica e decifratura con chiave privata.
		
		// Firma
		xmlEncrypt = new XmlEncrypt(truststore, alias);
		xmlEncrypt.encrypt(node, encryptAlgorithm, keyAlgorithm, wrappedKeyAlgorithm);
		System.out.println("["+tipo+"]- (b1) Xml Encrypt (Public Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		// Verifica
		xmlDecrypt = new XmlDecrypt(SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY, keystore,alias,passwordChiavePrivata);
		xmlDecrypt.decrypt(node);
		System.out.println("["+tipo+"]- (b2) Xml Decrypt (Private Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		
		// *** TEST 2b:  Cifratura con wrapped della chiave simmetrica generata dinamicanete; il wrapped avviente tramite una chiave simmetrica
		
		// 2b Cifratura con chiave simmetrica wrapped ed utilizzo di chiavi simmetriche
		
		// Crifratura
		keyAlgorithm = "AES";
		encryptAlgorithm = XMLCipher.AES_128;
		wrappedKeyAlgorithm = XMLCipher.AES_128_KeyWrap;
		xmlEncrypt = new XmlEncrypt(SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_SYMMETRIC_KEY, keystoreJCEKS,alias,passwordChiavePrivata);
		xmlEncrypt.encrypt(node, encryptAlgorithm, canonicalizationAlgorithm, digestAlgorithm, keyAlgorithm, wrappedKeyAlgorithm);
		System.out.println("["+tipo+"]- (a1). Xml Encrypt (Symmetric Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		// Decifratura
		xmlDecrypt = new XmlDecrypt(keystoreJCEKS, true, SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_SYMMETRIC_KEY, alias, passwordChiavePrivata);
		xmlDecrypt.decrypt(node);
		System.out.println("["+tipo+"]- (a1) Xml Decrypt (Symmetric Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
	}
	
	
	private static void testJsonProperties(TipoTest tipo, File fKeystore, File fTruststore, JwtHeaders headers) throws Exception {
		
		System.out.println("\n\n ============= "+tipo+" ===================");
		System.out.println("["+tipo+"]. Example JsonEncrypt \n");
		
		String jsonInput = "\n{\n\t\"name\":\"value1\", \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n}";

		// Firma
		Properties encryptProps = new Properties();
		InputStream encryptPropsis = TestEncrypt.class.getResourceAsStream("jws.encrypt.properties");
		encryptProps.load(encryptPropsis);
		encryptProps.put("rs.security.keystore.file", fTruststore.getPath());
		//encryptProps.put("rs.security.keystore.file", fKeystore.getPath());
		if(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS.equals(tipo) || 
				TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipo)) {
			encryptProps.put("rs.security.keystore.type", "jks");
			
			if(headers!=null) {
				// inverto
				encryptProps.put("rs.security.encryption.include.cert.sha1", "true");
				encryptProps.put("rs.security.encryption.include.cert.sha256", "false");
			}
		}
		else if(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipo)) {
			encryptProps.put("rs.security.keystore.type", "jwk");
			
			encryptProps.remove("rs.security.encryption.include.cert.sha1");
			encryptProps.remove("rs.security.encryption.include.cert.sha256");
		}
			
		if(headers!=null) {
			encryptProps.remove("rs.security.encryption.include.cert");
			encryptProps.remove("rs.security.encryption.include.public.key");
		}
		
		Properties decryptProps = new Properties();
		InputStream decryptPropsis = TestEncrypt.class.getResourceAsStream("jws.decrypt.properties");
		decryptProps.load(decryptPropsis);
		decryptProps.put("rs.security.keystore.file", fKeystore.getPath());
		decryptProps.remove("rs.security.keystore.type");
		if(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS.equals(tipo) || 
				TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipo)) {
			decryptProps.put("rs.security.keystore.type", "jks");
		}
		else if(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipo) ) {
			decryptProps.put("rs.security.keystore.type", "jwk");
		}
		
		
		
		
		
		// Cifratura con chiave pubblica e decifratura con chiave privata.
		
		System.out.println("\n");
		
		// 3a. Encrypt Attached 
		JsonEncrypt jsonAttachedEncrypt = new JsonEncrypt(encryptProps, JOSERepresentation.SELF_CONTAINED);
		String attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-a. JsonAttachedEncrypt Encrypted (Public) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
		
		// Verifica
		JsonDecrypt jsonAttachedVerify = new JsonDecrypt(decryptProps,JOSERepresentation.SELF_CONTAINED);
		jsonAttachedVerify.decrypt(attachEncrypt);
		System.out.println("["+tipo+"]-a. JsonAttachedEncrypt Verify (Private): \n"+jsonAttachedVerify.getDecodedPayload());
		if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonAttachedVerify.decrypt(attachEncrypt.replace("ciphertext\":\"", "ciphertext\":\"CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		
		System.out.println("\n\n");
		
		// 3b. Encrypt Compact
		JsonEncrypt jsonCompactEncrypt = null;
		if(headers==null) {
			jsonCompactEncrypt = new JsonEncrypt(encryptProps, JOSERepresentation.COMPACT);
		}
		else {
			jsonCompactEncrypt = new JsonEncrypt(encryptProps, headers, JOSERepresentation.COMPACT);	
		}
		String compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt Encrypted (Public) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
		
		String hdrDecoded = new String (Base64Utilities.decode(compactEncrypt.split("\\.")[0]));
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt HDR base64 decoded: "+hdrDecoded);
		
		// Verifica
		JsonDecrypt jsonCompactVerify = new JsonDecrypt(decryptProps, JOSERepresentation.COMPACT);
		jsonCompactVerify.decrypt(compactEncrypt);
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt Verify (Private): \n"+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonCompactVerify.decrypt(compactEncrypt.replace(".", ".CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		System.out.println("\n\n");
		
		
		
		
		
		
		
		// Ripeto i test riabilitando l'encrypt algorithm e aggiungendo DEFLATE!!!
		
		encryptProps = new Properties();
		encryptPropsis.close();
		encryptPropsis = TestEncrypt.class.getResourceAsStream("jws.encrypt.properties");
		encryptProps.load(encryptPropsis);
		encryptProps.put("rs.security.keystore.file", fTruststore.getPath());
		if(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS.equals(tipo) || 
				TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipo)) {
			encryptProps.put("rs.security.keystore.type", "jks");
			
			if(headers!=null) {
				// inverto
				encryptProps.put("rs.security.encryption.include.cert.sha1", "true");
				encryptProps.put("rs.security.encryption.include.cert.sha256", "false");
			}
		}
		else if(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipo)) {
			encryptProps.put("rs.security.keystore.type", "jwk");
			
			encryptProps.remove("rs.security.encryption.include.cert.sha1");
			encryptProps.remove("rs.security.encryption.include.cert.sha256");
		}
				
		if(headers!=null) {
			encryptProps.remove("rs.security.encryption.include.cert");
			encryptProps.remove("rs.security.encryption.include.public.key");
		}
		
		
		decryptProps = new Properties();
		decryptPropsis.close();
		decryptPropsis = TestEncrypt.class.getResourceAsStream("jws.decrypt.properties");
		decryptProps.load(decryptPropsis);
		decryptProps.put("rs.security.keystore.file", fKeystore.getPath());
		decryptProps.remove("rs.security.keystore.type");
		if(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS.equals(tipo) || 
				TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipo)) {
			decryptProps.put("rs.security.keystore.type", "jks");
		}
		else if(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipo) ) {
			decryptProps.put("rs.security.keystore.type", "jwk");
		}
		

		encryptProps.put("rs.security.encryption.zip.algorithm","DEF");
		//decryptProps.put("rs.security.encryption.zip.algorithm","DEF"); non serve, l'informazione viaggia
		
		

		
		// 3e. Encrypt Attached
		jsonAttachedEncrypt = new JsonEncrypt(encryptProps, JOSERepresentation.SELF_CONTAINED);
		attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-e. JsonAttachedEncrypt Deflate (Private) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
		
		// Verifica
		jsonAttachedVerify = new JsonDecrypt(decryptProps,JOSERepresentation.SELF_CONTAINED);
		jsonAttachedVerify.decrypt(attachEncrypt);
		System.out.println("["+tipo+"]-e. JsonAttachedEncrypt Verify-Deflate (Public): \n"+jsonAttachedVerify.getDecodedPayload());
		if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonAttachedVerify.decrypt(attachEncrypt.replace("ciphertext\":\"", "ciphertext\":\"CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
	
	
	
		System.out.println("\n\n");
		
		// 3f. Encrypt Compact
		if(headers==null) {
			jsonCompactEncrypt = new JsonEncrypt(encryptProps, JOSERepresentation.COMPACT);
		}
		else {
			jsonCompactEncrypt = new JsonEncrypt(encryptProps, headers, JOSERepresentation.COMPACT);
		}
		compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt Deflate (Private) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
		
		hdrDecoded = new String (Base64Utilities.decode(compactEncrypt.split("\\.")[0]));
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt HDR base64 decoded: "+hdrDecoded);
		
		// Verifica
		jsonCompactVerify = new JsonDecrypt(decryptProps, JOSERepresentation.COMPACT);
		jsonCompactVerify.decrypt(compactEncrypt);
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt Verify-Deflate (Public): \n"+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonCompactVerify.decrypt(compactEncrypt.replace(".", ".CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		System.out.println("\n\n");
		
	}
	
	private static void testJsonKeystore(TipoTest tipo, KeyStore keystore, KeyStore truststore, String alias, String passwordChiavePrivata, JwtHeaders headers) throws Exception {
		
		System.out.println("\n\n ============= "+tipo+" ===================");
		System.out.println("["+tipo+"]. Example JsonEncrypt \n");
		
		String jsonInput = "\n{\n\t\"name\":\"value1\", \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n}";

		String keyAlgorithm = "RSA-OAEP-256"; 
		String contentAlgorithm = "A256GCM";
		
		
		
		// Cifratura con chiave pubblica e decifratura con chiave privata.
		
		System.out.println("\n");
		
		// 4a. Encrypt Attached 
		JsonEncrypt jsonAttachedEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, JOSERepresentation.SELF_CONTAINED);
		String attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-a. JsonAttachedEncrypt Encrypted (Public) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
		
		// Verifica
		JsonDecrypt jsonAttachedVerify = new JsonDecrypt(keystore, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.SELF_CONTAINED);
		jsonAttachedVerify.decrypt(attachEncrypt);
		System.out.println("["+tipo+"]-a. JsonAttachedEncrypt Verify (Private): \n"+jsonAttachedVerify.getDecodedPayload());
		if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonAttachedVerify.decrypt(attachEncrypt.replace("ciphertext\":\"", "ciphertext\":\"CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		
		System.out.println("\n\n");
		
		// 4b. Encrypt Compact
		JsonEncrypt jsonCompactEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, headers, JOSERepresentation.COMPACT);
		String compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt Encrypted (Public) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
		
		String hdrDecoded = new String (Base64Utilities.decode(compactEncrypt.split("\\.")[0]));
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt HDR base64 decoded: "+hdrDecoded);
		
		// Verifica
		JsonDecrypt jsonCompactVerify = new JsonDecrypt(keystore, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.COMPACT);
		jsonCompactVerify.decrypt(compactEncrypt);
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt Verify (Private): \n"+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonCompactVerify.decrypt(compactEncrypt.replace(".", ".CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		
		System.out.println("\n\n");
		
		
		
		
		
		
		
		// Ripeto i test aggiungendo DEFLATE!!!
		
		boolean deflate = true;
		
			
		// 4e. Encrypt Attached
		jsonAttachedEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, deflate, JOSERepresentation.SELF_CONTAINED);
		attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-e. JsonAttachedEncrypt Deflate (Private) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
		
		// Verifica
		jsonAttachedVerify = new JsonDecrypt(keystore, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm,JOSERepresentation.SELF_CONTAINED);
		jsonAttachedVerify.decrypt(attachEncrypt);
		System.out.println("["+tipo+"]-. JsonAttachedEncrypt Verify-Deflate (Public): \n"+jsonAttachedVerify.getDecodedPayload());
		if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonAttachedVerify.decrypt(attachEncrypt.replace("ciphertext\":\"", "ciphertext\":\"CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
	
	
	
		System.out.println("\n\n");
		
		// 4f. Encrypt Compact
		jsonCompactEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, deflate, headers, JOSERepresentation.COMPACT);
		compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt Deflate (Private) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
		
		hdrDecoded = new String (Base64Utilities.decode(compactEncrypt.split("\\.")[0]));
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt HDR base64 decoded: "+hdrDecoded);
		
		// Verifica
		jsonCompactVerify = new JsonDecrypt(keystore, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.COMPACT);
		jsonCompactVerify.decrypt(compactEncrypt);
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt Verify-Deflate (Public): \n"+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonCompactVerify.decrypt(compactEncrypt.replace(".", ".CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		System.out.println("\n\n");
		
	}
	
	private static void testJsonKeystore(TipoTest tipo, KeyStore keystoreJCEKS, String alias, String passwordChiavePrivata, JwtHeaders headers) throws Exception {
		System.out.println("\n\n ============= "+tipo+" ===================");
		System.out.println("["+tipo+"]. Example JsonEncrypt (Symmetric) \n");

		String jsonInput = "\n{\n\t\"name\":\"value1\", \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n}";
		
		String keyAlgorithm = "A256KW"; 
		String contentAlgorithm = "A256GCM";
		boolean symmetric = true;
		
		
		// Cifratura con chiave simmetrica
		
		System.out.println("\n");
		
		// 5a. Encrypt Attached 
		JsonEncrypt jsonAttachedEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.SELF_CONTAINED);
		String attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-a. JsonAttachedEncrypt Encrypted (Symmetric) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
		
		// Verifica
		JsonDecrypt jsonAttachedVerify = new JsonDecrypt(keystoreJCEKS, symmetric, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.SELF_CONTAINED);
		jsonAttachedVerify.decrypt(attachEncrypt);
		System.out.println("["+tipo+"]-a. JsonAttachedEncrypt Verify (Symmetric): \n"+jsonAttachedVerify.getDecodedPayload());
		if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonAttachedVerify.decrypt(attachEncrypt.replace("ciphertext\":\"", "ciphertext\":\"CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		
		System.out.println("\n\n");
		
		// 5b. Encrypt Compact
		JsonEncrypt jsonCompactEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, headers, JOSERepresentation.COMPACT);
		String compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt Encrypted (Symmetric) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
		
		String hdrDecoded = new String (Base64Utilities.decode(compactEncrypt.split("\\.")[0]));
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt HDR base64 decoded: "+hdrDecoded);
		
		// Verifica
		JsonDecrypt jsonCompactVerify = new JsonDecrypt(keystoreJCEKS, symmetric, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.COMPACT);
		jsonCompactVerify.decrypt(compactEncrypt);
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt Verify (Symmetric): \n"+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonCompactVerify.decrypt(compactEncrypt.replace(".", ".CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		
		System.out.println("\n\n");
		
		
		// Ripeto i test aggiungendo DEFLATE!!!
		
		boolean deflate = true;
		
			
		// 5e. Encrypt Attached
		jsonAttachedEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, deflate, JOSERepresentation.SELF_CONTAINED);
		attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-e. JsonAttachedEncrypt Deflate (Symmetric) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
		
		// Verifica
		jsonAttachedVerify = new JsonDecrypt(keystoreJCEKS, symmetric, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm,JOSERepresentation.SELF_CONTAINED);
		jsonAttachedVerify.decrypt(attachEncrypt);
		System.out.println("["+tipo+"]-e. JsonAttachedEncrypt Verify-Deflate (Symmetric): \n"+jsonAttachedVerify.getDecodedPayload());
		if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonAttachedVerify.decrypt(attachEncrypt.replace("ciphertext\":\"", "ciphertext\":\"CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
	
	
	
		System.out.println("\n\n");
		
		// 5f. Encrypt Compact
		jsonCompactEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, deflate, headers, JOSERepresentation.COMPACT);
		compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt Deflate (Symmetric) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
		
		hdrDecoded = new String (Base64Utilities.decode(compactEncrypt.split("\\.")[0]));
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt HDR base64 decoded: "+hdrDecoded);
		
		// Verifica
		jsonCompactVerify = new JsonDecrypt(keystoreJCEKS, symmetric, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.COMPACT);
		jsonCompactVerify.decrypt(compactEncrypt);
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt Verify-Deflate (Symmetric): \n"+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonCompactVerify.decrypt(compactEncrypt.replace(".", ".CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		System.out.println("\n\n");
	}
	
	private static void testJsonJwkKeys(TipoTest tipo, JsonWebKeys keystore, JsonWebKeys truststore, String alias, JwtHeaders headers) throws Exception {
		
		System.out.println("\n\n ============= "+tipo+" ===================");
		System.out.println("["+tipo+"]. Example JsonEncrypt \n");
		
		String jsonInput = "\n{\n\t\"name\":\"value1\", \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n}";

		String keyAlgorithm = "RSA-OAEP-256"; 
		String contentAlgorithm = "A256GCM";
		
		
		
		// Cifratura con chiave pubblica e decifratura con chiave privata.
		
		System.out.println("\n");
		
		// 4a. Encrypt Attached 
		JsonEncrypt jsonAttachedEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, JOSERepresentation.SELF_CONTAINED);
		String attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-a. JsonAttachedEncrypt Encrypted (Public) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
		
		// Verifica
		JsonDecrypt jsonAttachedVerify = new JsonDecrypt(keystore, alias, keyAlgorithm, contentAlgorithm, JOSERepresentation.SELF_CONTAINED);
		jsonAttachedVerify.decrypt(attachEncrypt);
		System.out.println("["+tipo+"]-a. JsonAttachedEncrypt Verify (Private): \n"+jsonAttachedVerify.getDecodedPayload());
		if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonAttachedVerify.decrypt(attachEncrypt.replace("ciphertext\":\"", "ciphertext\":\"CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		
		System.out.println("\n\n");
		
		// 4b. Encrypt Compact
		JsonEncrypt jsonCompactEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, headers, JOSERepresentation.COMPACT);
		String compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt Encrypted (Public) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
		
		String hdrDecoded = new String (Base64Utilities.decode(compactEncrypt.split("\\.")[0]));
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt HDR base64 decoded: "+hdrDecoded);
		
		// Verifica
		JsonDecrypt jsonCompactVerify = new JsonDecrypt(keystore, alias, keyAlgorithm, contentAlgorithm, JOSERepresentation.COMPACT);
		jsonCompactVerify.decrypt(compactEncrypt);
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt Verify (Private): \n"+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonCompactVerify.decrypt(compactEncrypt.replace(".", ".CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		
		System.out.println("\n\n");
		
		
		
		
		
		
		
		// Ripeto i test aggiungendo DEFLATE!!!
		
		boolean deflate = true;
		
			
		// 4e. Encrypt Attached
		jsonAttachedEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, deflate, JOSERepresentation.SELF_CONTAINED);
		attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-e. JsonAttachedEncrypt Deflate (Private) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
		
		// Verifica
		jsonAttachedVerify = new JsonDecrypt(keystore, alias, keyAlgorithm, contentAlgorithm,JOSERepresentation.SELF_CONTAINED);
		jsonAttachedVerify.decrypt(attachEncrypt);
		System.out.println("["+tipo+"]-. JsonAttachedEncrypt Verify-Deflate (Public): \n"+jsonAttachedVerify.getDecodedPayload());
		if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonAttachedVerify.decrypt(attachEncrypt.replace("ciphertext\":\"", "ciphertext\":\"CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
	
	
	
		System.out.println("\n\n");
		
		// 4f. Encrypt Compact
		jsonCompactEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, deflate, headers, JOSERepresentation.COMPACT);
		compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt Deflate (Private) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
		
		hdrDecoded = new String (Base64Utilities.decode(compactEncrypt.split("\\.")[0]));
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt HDR base64 decoded: "+hdrDecoded);
		
		// Verifica
		jsonCompactVerify = new JsonDecrypt(keystore, alias, keyAlgorithm, contentAlgorithm, JOSERepresentation.COMPACT);
		jsonCompactVerify.decrypt(compactEncrypt);
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt Verify-Deflate (Public): \n"+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonCompactVerify.decrypt(compactEncrypt.replace(".", ".CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		System.out.println("\n\n");
		
	}
	
	
	
	private static void testJsonJwkKey(TipoTest tipo, JsonWebKey keystore, JsonWebKey truststore, JwtHeaders headers) throws Exception {
		
		System.out.println("\n\n ============= "+tipo+" ===================");
		System.out.println("["+tipo+"]. Example JsonEncrypt \n");
		
		String jsonInput = "\n{\n\t\"name\":\"value1\", \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n}";

		String keyAlgorithm = "RSA-OAEP-256"; 
		String contentAlgorithm = "A256GCM";
		
		
		
		// Cifratura con chiave pubblica e decifratura con chiave privata.
		
		System.out.println("\n");
		
		// 4a. Encrypt Attached 
		JsonEncrypt jsonAttachedEncrypt = new JsonEncrypt(truststore, keyAlgorithm, contentAlgorithm, JOSERepresentation.SELF_CONTAINED);
		String attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-a. JsonAttachedEncrypt Encrypted (Public) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
		
		// Verifica
		JsonDecrypt jsonAttachedVerify = new JsonDecrypt(keystore, keyAlgorithm, contentAlgorithm, JOSERepresentation.SELF_CONTAINED);
		jsonAttachedVerify.decrypt(attachEncrypt);
		System.out.println("["+tipo+"]-a. JsonAttachedEncrypt Verify (Private): \n"+jsonAttachedVerify.getDecodedPayload());
		if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonAttachedVerify.decrypt(attachEncrypt.replace("ciphertext\":\"", "ciphertext\":\"CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		
		System.out.println("\n\n");
		
		// 4b. Encrypt Compact
		JsonEncrypt jsonCompactEncrypt = new JsonEncrypt(truststore, keyAlgorithm, contentAlgorithm, headers, JOSERepresentation.COMPACT);
		String compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt Encrypted (Public) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
		
		String hdrDecoded = new String (Base64Utilities.decode(compactEncrypt.split("\\.")[0]));
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt HDR base64 decoded: "+hdrDecoded);
		
		// Verifica
		JsonDecrypt jsonCompactVerify = new JsonDecrypt(keystore, keyAlgorithm, contentAlgorithm, JOSERepresentation.COMPACT);
		jsonCompactVerify.decrypt(compactEncrypt);
		System.out.println("["+tipo+"]-b. JsonCompactEncrypt Verify (Private): \n"+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonCompactVerify.decrypt(compactEncrypt.replace(".", ".CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		
		System.out.println("\n\n");
		
		
		
		
		
		
		
		// Ripeto i test aggiungendo DEFLATE!!!
		
		boolean deflate = true;
		
			
		// 4e. Encrypt Attached
		jsonAttachedEncrypt = new JsonEncrypt(truststore, keyAlgorithm, contentAlgorithm, deflate, JOSERepresentation.SELF_CONTAINED);
		attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-e. JsonAttachedEncrypt Deflate (Private) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
		
		// Verifica
		jsonAttachedVerify = new JsonDecrypt(keystore, keyAlgorithm, contentAlgorithm,JOSERepresentation.SELF_CONTAINED);
		jsonAttachedVerify.decrypt(attachEncrypt);
		System.out.println("["+tipo+"]-. JsonAttachedEncrypt Verify-Deflate (Public): \n"+jsonAttachedVerify.getDecodedPayload());
		if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonAttachedVerify.decrypt(attachEncrypt.replace("ciphertext\":\"", "ciphertext\":\"CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
	
	
	
		System.out.println("\n\n");
		
		// 4f. Encrypt Compact
		jsonCompactEncrypt = new JsonEncrypt(truststore, keyAlgorithm, contentAlgorithm, deflate, headers, JOSERepresentation.COMPACT);
		compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
		
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt Deflate (Private) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
		
		hdrDecoded = new String (Base64Utilities.decode(compactEncrypt.split("\\.")[0]));
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt HDR base64 decoded: "+hdrDecoded);
		
		// Verifica
		jsonCompactVerify = new JsonDecrypt(keystore, keyAlgorithm, contentAlgorithm, JOSERepresentation.COMPACT);
		jsonCompactVerify.decrypt(compactEncrypt);
		System.out.println("["+tipo+"]-f. JsonCompactEncrypt Verify-Deflate (Public): \n"+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		try {
			jsonCompactVerify.decrypt(compactEncrypt.replace(".", ".CORROMPO"));
			throw new Exception("Expected validation error");
		}catch(Exception e) {
			System.out.println("Expected error: "+e.getMessage());
		}
		
		System.out.println("\n\n");
		
	}
	
	public enum TipoTest {
		
		JAVA_ENCRYPT, 
		XML_ENCRYPT,
		JSON_ENCRYPT_PROPERTIES_JKS,
		JSON_ENCRYPT_PROPERTIES_JWK,
		JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM,
		JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM,
		JSON_ENCRYPT_PROPERTIES_JKS_KEYSTORE,
		JSON_ENCRYPT_PROPERTIES_JKS_KEYSTORE_HEADER_CUSTOM,
		JSON_ENCRYPT_PROPERTIES_JKS_KEYSTORE_JCE,
		JSON_ENCRYPT_PROPERTIES_JKS_KEYSTORE_JCE_HEADER_CUSTOM,
		JSON_ENCRYPT_PROPERTIES_JWK_KEYS,
		JSON_ENCRYPT_PROPERTIES_JWK_KEYS_HEADER_CUSTOM,
		JSON_ENCRYPT_PROPERTIES_JWK_KEY,
		JSON_ENCRYPT_PROPERTIES_JWK_KEY_HEADER_CUSTOM,
		
	}

}
