/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
import java.util.Properties;

import javax.crypto.SecretKey;

import org.apache.xml.security.encryption.XMLCipher;
import org.openspcoop2.utils.Utilities;
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
		
		InputStream isKeystore = null;
		File fKeystore = null;
		InputStream isTruststore = null;
		File fTruststore = null;
		InputStream isKeystoreJCEKS = null;
		File fKeystoreJCEKS = null;
		try{
			isKeystore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jks");
			fKeystore = File.createTempFile("keystore", "jks");
			FileSystemUtilities.writeFile(fKeystore, Utilities.getAsByteArray(isKeystore));
			
			isTruststore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/truststore_example.jks");
			fTruststore = File.createTempFile("truststore", "jks");
			FileSystemUtilities.writeFile(fTruststore, Utilities.getAsByteArray(isTruststore));
			
			isKeystoreJCEKS = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/example.jceks");
			fKeystoreJCEKS = File.createTempFile("keystore", "jceks");
			FileSystemUtilities.writeFile(fKeystoreJCEKS, Utilities.getAsByteArray(isKeystoreJCEKS));
			
			String passwordChiavePrivata = "key123456";
			String passwordStore = "123456";
			String alias = "openspcoop";
			
			KeyStore keystore = new KeyStore(fKeystore.getAbsolutePath(), passwordStore);
			KeyStore truststore = new KeyStore(fTruststore.getAbsolutePath(), passwordStore);
			KeyStore keystoreJCEKS = new KeyStore(fKeystoreJCEKS.getAbsolutePath(), "JCEKS", passwordStore);
			
			
			// 1. Esempio Encrypt Java 
			
			System.out.println("\n\n ================================");
			System.out.println("1. Example JavaEncrypt \n");
			
			String contenutoDaCifrare = "MarioRossi:23:05:1980";
			
			// 1a Cifratura con chiave privata e decifratura con chiave pubblica. Si tratta anche di un caso di firma implicita
			
			// Encrypt
			String encryptAlgorithm = "RSA";
			Encrypt encrypt = new Encrypt(keystore, alias, passwordChiavePrivata);
			byte[] encryptedBytes = encrypt.encrypt(contenutoDaCifrare.getBytes(), encryptAlgorithm);
			System.out.println("1a. JavaEncrypt Encrypted (Private): "+new String(encryptedBytes));
			
			// Decrypt
			Decrypt decrypt = new Decrypt(truststore,alias);
			System.out.println("1a. JavaEncrypt Decrypted (Public): "+new String(decrypt.decrypt(encryptedBytes, encryptAlgorithm)));
			
			
			// 1b Cifratura con chiave pubblica e decifratura con chiave privata.
			
			// Encrypt
			encrypt = new Encrypt(truststore, alias);
			encryptedBytes = encrypt.encrypt(contenutoDaCifrare.getBytes(), encryptAlgorithm);
			System.out.println("1b. JavaEncrypt Encrypted (Public): "+new String(encryptedBytes));
			
			// Decrypt
			decrypt = new Decrypt(keystore,alias,passwordChiavePrivata);
			System.out.println("1b. JavaEncrypt Decrypted (Private): "+new String(decrypt.decrypt(encryptedBytes, encryptAlgorithm)));
			
			
			
			// 2. Esempio Signature Xml 
			
			System.out.println("\n\n ================================");
			System.out.println("2. Example XmlSignature \n");
			
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
			encryptAlgorithm = XMLCipher.AES_128;
			XmlEncrypt xmlEncrypt = new XmlEncrypt(SymmetricKeyWrappedMode.SYM_ENC_KEY_NO_WRAPPED, secretKeyGenerata);
			xmlEncrypt.encryptSymmetric(node, encryptAlgorithm, canonicalizationAlgorithm, digestAlgorithm);
			System.out.println("2 (1a) XmlSignature Encrypted (SymmetricKey-Generata): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			// Decifratura
			XmlDecrypt xmlDecrypt = new XmlDecrypt(SymmetricKeyWrappedMode.SYM_ENC_KEY_NO_WRAPPED, secretKeyGenerata);
			xmlDecrypt.decrypt(node);
			System.out.println("2 (1a) XmlSignature Decrypted (SymmetricKey-Generata): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			
			// *** TEST 1b:  Cifratura con chiave simmetrica non wrapped; chiave simmetrica letta da un keystore JCEKS
					
			// Crifratura
			boolean symmetricKey = true;
			encryptAlgorithm = XMLCipher.AES_128;
			//xmlEncrypt = new XmlEncrypt(secretKeyLetta);
			xmlEncrypt = new XmlEncrypt(keystoreJCEKS, symmetricKey, SymmetricKeyWrappedMode.SYM_ENC_KEY_NO_WRAPPED, alias, passwordChiavePrivata);
			xmlEncrypt.encryptSymmetric(node, encryptAlgorithm);
			System.out.println("2 (1b) XmlSignature Encrypted (SymmetricKey-Letta): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			// Decifratura
			//xmlDecrypt = new XmlDecrypt(secretKeyLetta);
			xmlDecrypt = new XmlDecrypt(keystoreJCEKS, symmetricKey, SymmetricKeyWrappedMode.SYM_ENC_KEY_NO_WRAPPED, alias, passwordChiavePrivata);
			xmlDecrypt.decrypt(node);
			System.out.println("2 (1b) XmlSignature Decrypted (SymmetricKey-Letta): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			
			// *** TEST 2a:  Cifratura con wrapped della chiave simmetrica generata dinamicanete; il wrapped avviente tramite una chiave asimmetrica
			
			// 2b Cifratura con chiave simmetrica wrapped ed utilizzo di chiavi asimmetriche
			//    Cifratura con chiave privata e decifratura con chiave pubblica. Si tratta anche di un caso di firma implicita
			
			// Crifratura
			keyAlgorithm = "AES";
			encryptAlgorithm = XMLCipher.AES_128;
			String wrappedKeyAlgorithm = XMLCipher.RSA_v1dot5;
			xmlEncrypt = new XmlEncrypt(SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY, keystore,alias,passwordChiavePrivata);
			xmlEncrypt.encrypt(node, encryptAlgorithm, canonicalizationAlgorithm, digestAlgorithm, keyAlgorithm, wrappedKeyAlgorithm);
			System.out.println("2 (a1). XmlSignature Encrypted (Private Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			// Decifratura
			xmlDecrypt = new XmlDecrypt(truststore, alias);
			xmlDecrypt.decrypt(node);
			System.out.println("2 (a1) XmlSignature Decrypted (Public Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			
			// 2b Cifratura con chiave simmetrica wrapped ed utilizzo di chiavi asimmetriche
			//	  Cifratura con chiave pubblica e decifratura con chiave privata.
			
			// Firma
			xmlEncrypt = new XmlEncrypt(truststore, alias);
			xmlEncrypt.encrypt(node, encryptAlgorithm, keyAlgorithm, wrappedKeyAlgorithm);
			System.out.println("2 (b1) XmlSignature Encrypted (Public Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			// Verifica
			xmlDecrypt = new XmlDecrypt(SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY, keystore,alias,passwordChiavePrivata);
			xmlDecrypt.decrypt(node);
			System.out.println("2 (b2) XmlSignature Decrypted (Private Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			
			// *** TEST 2b:  Cifratura con wrapped della chiave simmetrica generata dinamicanete; il wrapped avviente tramite una chiave simmetrica
			
			// 2b Cifratura con chiave simmetrica wrapped ed utilizzo di chiavi simmetriche
			
			// Crifratura
			keyAlgorithm = "AES";
			encryptAlgorithm = XMLCipher.AES_128;
			wrappedKeyAlgorithm = XMLCipher.AES_128_KeyWrap;
			xmlEncrypt = new XmlEncrypt(SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_SYMMETRIC_KEY, keystoreJCEKS,alias,passwordChiavePrivata);
			xmlEncrypt.encrypt(node, encryptAlgorithm, canonicalizationAlgorithm, digestAlgorithm, keyAlgorithm, wrappedKeyAlgorithm);
			System.out.println("2 (a1). XmlSignature Encrypted (Symmetric Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			// Decifratura
			xmlDecrypt = new XmlDecrypt(keystoreJCEKS, true, SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_SYMMETRIC_KEY, alias, passwordChiavePrivata);
			xmlDecrypt.decrypt(node);
			System.out.println("2 (a1) XmlSignature Decrypted (Symmetric Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			
			
			
			
			
			
			System.out.println("\n\n ================================");
			System.out.println("3. Example JsonEncrypt \n");
			
			String jsonInput = "\n{\n\t\"name\":\"value1\", \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n}";

			// Firma
			Properties encryptProps = new Properties();
			InputStream encryptPropsis = TestSignature.class.getResourceAsStream("jws.encrypt.properties");
			encryptProps.load(encryptPropsis);
			encryptProps.put("rs.security.keystore.file", fTruststore.getPath());
			//encryptProps.put("rs.security.keystore.file", fKeystore.getPath());
			
			Properties decryptProps = new Properties();
			InputStream decryptPropsis = TestSignature.class.getResourceAsStream("jws.decrypt.properties");
			decryptProps.load(decryptPropsis);
			decryptProps.put("rs.security.keystore.file", fKeystore.getPath());
			
			
			
			
			
			
			// Cifratura con chiave pubblica e decifratura con chiave privata.
			
			System.out.println("\n");
			
			// 3a. Encrypt Attached 
			JsonEncrypt jsonAttachedEncrypt = new JsonEncrypt(encryptProps, JOSERepresentation.SELF_CONTAINED);
			String attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
			
			System.out.println("3a. JsonAttachedEncrypt Encrypted (Public) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
			
			// Verifica
			JsonDecrypt jsonAttachedVerify = new JsonDecrypt(decryptProps,JOSERepresentation.SELF_CONTAINED);
			jsonAttachedVerify.decrypt(attachEncrypt);
			System.out.println("3a. JsonAttachedEncrypt Verify (Private): \n"+jsonAttachedVerify.getDecodedPayload());
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
			JsonEncrypt jsonCompactEncrypt = new JsonEncrypt(encryptProps, JOSERepresentation.COMPACT);
			String compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
			
			System.out.println("3b. JsonCompactEncrypt Encrypted (Public) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
			
			// Verifica
			JsonDecrypt jsonCompactVerify = new JsonDecrypt(decryptProps, JOSERepresentation.COMPACT);
			jsonCompactVerify.decrypt(compactEncrypt);
			System.out.println("3b. JsonCompactEncrypt Verify (Private): \n"+jsonCompactVerify.getDecodedPayload());
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
			encryptPropsis = TestSignature.class.getResourceAsStream("jws.encrypt.properties");
			encryptProps.load(encryptPropsis);
			encryptProps.put("rs.security.keystore.file", fTruststore.getPath());
			
			decryptProps = new Properties();
			decryptPropsis.close();
			decryptPropsis = TestSignature.class.getResourceAsStream("jws.decrypt.properties");
			decryptProps.load(decryptPropsis);
			decryptProps.put("rs.security.keystore.file", fKeystore.getPath());
			

			encryptProps.put("rs.security.encryption.zip.algorithm","DEF");
			//decryptProps.put("rs.security.encryption.zip.algorithm","DEF"); non serve, l'informazione viaggia
			
			

			
			// 3e. Encrypt Attached
			jsonAttachedEncrypt = new JsonEncrypt(encryptProps, JOSERepresentation.SELF_CONTAINED);
			attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
			
			System.out.println("3e. JsonAttachedEncrypt Deflate (Private) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
			
			// Verifica
			jsonAttachedVerify = new JsonDecrypt(decryptProps,JOSERepresentation.SELF_CONTAINED);
			jsonAttachedVerify.decrypt(attachEncrypt);
			System.out.println("3e. JsonAttachedEncrypt Verify-Deflate (Public): \n"+jsonAttachedVerify.getDecodedPayload());
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
			jsonCompactEncrypt = new JsonEncrypt(encryptProps, JOSERepresentation.COMPACT);
			compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
			
			System.out.println("3f. JsonCompactEncrypt Deflate (Private) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
			
			// Verifica
			jsonCompactVerify = new JsonDecrypt(decryptProps, JOSERepresentation.COMPACT);
			jsonCompactVerify.decrypt(compactEncrypt);
			System.out.println("3f. JsonCompactEncrypt Verify-Deflate (Public): \n"+jsonCompactVerify.getDecodedPayload());
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
			
			
			
			
			
			
			
			
			
			
			
			System.out.println("\n\n ================================");
			System.out.println("4. Example JsonEncrypt \n");

			keyAlgorithm = "RSA-OAEP-256"; 
			String contentAlgorithm = "A256GCM";
			
			
			
			// Cifratura con chiave pubblica e decifratura con chiave privata.
			
			System.out.println("\n");
			
			// 4a. Encrypt Attached 
			jsonAttachedEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, JOSERepresentation.SELF_CONTAINED);
			attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
			
			System.out.println("4a. JsonAttachedEncrypt Encrypted (Public) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
			
			// Verifica
			jsonAttachedVerify = new JsonDecrypt(keystore, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.SELF_CONTAINED);
			jsonAttachedVerify.decrypt(attachEncrypt);
			System.out.println("4a. JsonAttachedEncrypt Verify (Private): \n"+jsonAttachedVerify.getDecodedPayload());
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
			jsonCompactEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, JOSERepresentation.COMPACT);
			compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
			
			System.out.println("4b. JsonCompactEncrypt Encrypted (Public) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
			
			// Verifica
			jsonCompactVerify = new JsonDecrypt(keystore, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.COMPACT);
			jsonCompactVerify.decrypt(compactEncrypt);
			System.out.println("4b. JsonCompactEncrypt Verify (Private): \n"+jsonCompactVerify.getDecodedPayload());
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
			
			System.out.println("4e. JsonAttachedEncrypt Deflate (Private) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
			
			// Verifica
			jsonAttachedVerify = new JsonDecrypt(keystore, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm,JOSERepresentation.SELF_CONTAINED);
			jsonAttachedVerify.decrypt(attachEncrypt);
			System.out.println("4e. JsonAttachedEncrypt Verify-Deflate (Public): \n"+jsonAttachedVerify.getDecodedPayload());
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
			jsonCompactEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, deflate, JOSERepresentation.COMPACT);
			compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
			
			System.out.println("4f. JsonCompactEncrypt Deflate (Private) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
			
			// Verifica
			jsonCompactVerify = new JsonDecrypt(keystore, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.COMPACT);
			jsonCompactVerify.decrypt(compactEncrypt);
			System.out.println("4f. JsonCompactEncrypt Verify-Deflate (Public): \n"+jsonCompactVerify.getDecodedPayload());
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
			
			
			
			
			
			System.out.println("\n\n ================================");
			System.out.println("5. Example JsonEncrypt (Symmetric) \n");

			keyAlgorithm = "A256KW"; 
			contentAlgorithm = "A256GCM";
			boolean symmetric = true;
			
			
			// Cifratura con chiave simmetrica
			
			System.out.println("\n");
			
			// 5a. Encrypt Attached 
			jsonAttachedEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.SELF_CONTAINED);
			attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
			
			System.out.println("5a. JsonAttachedEncrypt Encrypted (Symmetric) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
			
			// Verifica
			jsonAttachedVerify = new JsonDecrypt(keystoreJCEKS, symmetric, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.SELF_CONTAINED);
			jsonAttachedVerify.decrypt(attachEncrypt);
			System.out.println("5a. JsonAttachedEncrypt Verify (Symmetric): \n"+jsonAttachedVerify.getDecodedPayload());
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
			jsonCompactEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.COMPACT);
			compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
			
			System.out.println("5b. JsonCompactEncrypt Encrypted (Symmetric) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
			
			// Verifica
			jsonCompactVerify = new JsonDecrypt(keystoreJCEKS, symmetric, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.COMPACT);
			jsonCompactVerify.decrypt(compactEncrypt);
			System.out.println("5b. JsonCompactEncrypt Verify (Symmetric): \n"+jsonCompactVerify.getDecodedPayload());
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
			
				
			// 5e. Encrypt Attached
			jsonAttachedEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, deflate, JOSERepresentation.SELF_CONTAINED);
			attachEncrypt = jsonAttachedEncrypt.encrypt(jsonInput);
			
			System.out.println("5e. JsonAttachedEncrypt Deflate (Symmetric) (size: "+attachEncrypt.length()+"): \n"+attachEncrypt);
			
			// Verifica
			jsonAttachedVerify = new JsonDecrypt(keystoreJCEKS, symmetric, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm,JOSERepresentation.SELF_CONTAINED);
			jsonAttachedVerify.decrypt(attachEncrypt);
			System.out.println("5e. JsonAttachedEncrypt Verify-Deflate (Symmetric): \n"+jsonAttachedVerify.getDecodedPayload());
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
			jsonCompactEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, deflate, JOSERepresentation.COMPACT);
			compactEncrypt = jsonCompactEncrypt.encrypt(jsonInput);
			
			System.out.println("5f. JsonCompactEncrypt Deflate (Symmetric) (size: "+compactEncrypt.length()+"): \n"+compactEncrypt);
			
			// Verifica
			jsonCompactVerify = new JsonDecrypt(keystoreJCEKS, symmetric, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, JOSERepresentation.COMPACT);
			jsonCompactVerify.decrypt(compactEncrypt);
			System.out.println("5f. JsonCompactEncrypt Verify-Deflate (Symmetric): \n"+jsonCompactVerify.getDecodedPayload());
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
		}
	}

}
