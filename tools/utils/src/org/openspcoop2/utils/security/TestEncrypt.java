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
		try{
			isKeystore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jks");
			fKeystore = File.createTempFile("keystore", "jks");
			FileSystemUtilities.writeFile(fKeystore, Utilities.getAsByteArray(isKeystore));
			
			isTruststore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/truststore_example.jks");
			fTruststore = File.createTempFile("truststore", "jks");
			FileSystemUtilities.writeFile(fTruststore, Utilities.getAsByteArray(isTruststore));
			
			String passwordChiavePrivata = "key123456";
			String passwordStore = "123456";
			String alias = "openspcoop";
			
			KeyStore keystore = new KeyStore(fKeystore.getAbsolutePath(), passwordStore);
			KeyStore truststore = new KeyStore(fTruststore.getAbsolutePath(), passwordStore);
			
			
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
			
			// Chiave simmetrica
			String keyAlgorithm = "AES";
			SecretKey secretKey = AbstractXmlCipher.generateSecretKey(keyAlgorithm);
			
			// Firma
			encryptAlgorithm = XMLCipher.AES_128;
			XmlEncrypt xmlEncrypt = new XmlEncrypt(secretKey);
			xmlEncrypt.encrypt(node, encryptAlgorithm);
			System.out.println("2a. XmlSignature Encrypted (SymmetricKey): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			// Verifica
			XmlDecrypt xmlDecrypt = new XmlDecrypt(secretKey);
			xmlDecrypt.decrypt(node);
			System.out.println("2b. XmlSignature Decrypted (SymmetricKey): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			
			
			// 2b Cifratura con chiave simmetrica wrapped ed utilizzo di chiavi asimmetriche
			//    Cifratura con chiave privata e decifratura con chiave pubblica. Si tratta anche di un caso di firma implicita
			
			// Firma
			keyAlgorithm = "AES";
			encryptAlgorithm = XMLCipher.AES_128;
			String wrappedKeyAlgorithm = XMLCipher.RSA_v1dot5;
			xmlEncrypt = new XmlEncrypt(keystore,alias,passwordChiavePrivata);
			xmlEncrypt.encrypt(node, encryptAlgorithm, keyAlgorithm, wrappedKeyAlgorithm);
			System.out.println("2b. XmlSignature Encrypted (Private): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			// Verifica
			xmlDecrypt = new XmlDecrypt(truststore, alias);
			xmlDecrypt.decrypt(node);
			System.out.println("2b. XmlSignature Decrypted (Public): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			
			
			
			// 2b Cifratura con chiave simmetrica wrapped ed utilizzo di chiavi asimmetriche
			//	  Cifratura con chiave pubblica e decifratura con chiave privata.
			
			// Firma
			xmlEncrypt = new XmlEncrypt(truststore, alias);
			xmlEncrypt.encrypt(node, encryptAlgorithm, keyAlgorithm, wrappedKeyAlgorithm);
			System.out.println("2c. XmlSignature Encrypted (Public): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			// Verifica
			xmlDecrypt = new XmlDecrypt(keystore,alias,passwordChiavePrivata);
			xmlDecrypt.decrypt(node);
			System.out.println("2c. XmlSignature Decrypted (Private): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			System.out.println("\n\n ================================");
			System.out.println("3. Example JsonEncrypt \n");
			
			String jsonInput = "{VALORE}";

			// Firma
			Properties encryptProps = new Properties();
			InputStream encryptPropsis = TestSignature.class.getResourceAsStream("jws.encrypt.properties");
			encryptProps.load(encryptPropsis);
			encryptProps.put("rs.security.keystore.file", fKeystore.getPath());
			
			Properties decryptProps = new Properties();
			InputStream decryptPropsis = TestSignature.class.getResourceAsStream("jws.decrypt.properties");
			decryptProps.load(decryptPropsis);
			decryptProps.put("rs.security.keystore.file", fKeystore.getPath());
			
			
			// 3a. Signature Attached 
			JsonEncrypt jsonAttachedEncrypt = new JsonEncrypt(encryptProps, JOSERepresentation.SELF_CONTAINED);
			String attachSign = jsonAttachedEncrypt.encrypt(jsonInput);
			
			System.out.println("3a. JsonAttachedEncrypt Encrypted: "+attachSign);
			
			// Verifica
			JsonDecrypt jsonAttachedVerify = new JsonDecrypt(decryptProps,JOSERepresentation.SELF_CONTAINED);
			System.out.println("3a. JsonAttachedEncrypt Verify: "+jsonAttachedVerify.decrypt(attachSign));
			
			// 3b. Encrypt Compact
			JsonEncrypt jsonCompactEncrypt = new JsonEncrypt(encryptProps, JOSERepresentation.COMPACT);
			String compactSign = jsonCompactEncrypt.encrypt(jsonInput);
			
			System.out.println("3b. JsonCompactEncrypt Encrypted: "+compactSign);
			
			// Verifica
			JsonDecrypt jsonCompactVerify = new JsonDecrypt(decryptProps, JOSERepresentation.COMPACT);
			System.out.println("3b. JsonCompactEncrypt Verify: "+jsonCompactVerify.decrypt(compactSign));

			// 3c. Encrypt Detached
			JsonEncrypt jsonDetachedEncrypt = new JsonEncrypt(encryptProps, JOSERepresentation.DETACHED);
			String detachedSign = jsonDetachedEncrypt.encrypt(jsonInput);
//			String payload = jsonDetachedEncrypt.getPayload(jsonInput);
			
			System.out.println("3c. JsonDetachedEncrypt Encrypted: "+detachedSign);
//			System.out.println("3c. JsonDetachedEncrypt Payload: "+payload);
			
			// Verifica
			JsonDecrypt jsonDetachedVerify = new JsonDecrypt(decryptProps, JOSERepresentation.DETACHED);
			System.out.println("3c. JsonDetachedEncrypt Verify: "+jsonDetachedVerify.decrypt(detachedSign));

			System.out.println("\n\n ================================");
			System.out.println("3. Example JsonEncrypt \n");

			
			
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
		}
	}

}
