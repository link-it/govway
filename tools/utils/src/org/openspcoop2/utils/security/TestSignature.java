/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

import org.apache.xml.security.keys.KeyInfo;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Element;

/**	
 * TestSignature
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestSignature {

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
			
			
			// 1. Esempio Signature Java 
			
			System.out.println("\n\n ================================");
			System.out.println("1. Example JavaSignature \n");
			
			String contenutoDaFirmare = "MarioRossi:23:05:1980";
			
			// Firma
			String signedAlgorithm = "SHA1WithRSA";
			Signature signature = new Signature(keystore, alias, passwordChiavePrivata);
			byte[] signed = signature.sign(contenutoDaFirmare.getBytes(), signedAlgorithm);
			System.out.println("1. JavaSignature Signed: "+new String(signed));
			
			// Verifica
			VerifySignature verify = new VerifySignature(truststore,alias);
			System.out.println("1. JavaSignature Verify: "+verify.verify(contenutoDaFirmare.getBytes(), signed, signedAlgorithm));
	
			
			
			
			// 2. Esempio Signature Xml 
			
			System.out.println("\n\n ================================");
			System.out.println("2. Example XmlSignature \n");
			
			String xmlInput = "<prova><test>VALORE</test></prova>";
			Element node = XMLUtils.getInstance().newElement(xmlInput.getBytes());
			
			// Firma
			XmlSignature xmlSignature = new XmlSignature(keystore, alias, passwordChiavePrivata);
			xmlSignature.addX509KeyInfo();
			xmlSignature.sign(node);
			System.out.println("2a. XmlSignature Signed (X509 KeyInfo): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			// Verifica
			VerifyXmlSignature xmlVerify = new VerifyXmlSignature(truststore,alias);
			System.out.println("2a. XmlSignature Verify (X509 KeyInfo) (no clean): "+xmlVerify.verify(node, false));
			System.out.println("2a. XmlSignature Verify (X509 KeyInfo) (no clean) xml: "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			System.out.println("2a. XmlSignature Verify (X509 KeyInfo) (clean): "+xmlVerify.verify(node, true));
			System.out.println("2a. XmlSignature Verify (X509 KeyInfo) (clean) xml: "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			KeyInfo keyInfo = xmlVerify.getKeyInfo();
			System.out.println("2a. XmlSignature KeyInfo (X509 KeyInfo): "+keyInfo.getX509Certificate().getIssuerX500Principal().getName());
			
			
			// Firma
			xmlSignature = new XmlSignature(keystore, alias, passwordChiavePrivata);
			xmlSignature.addRSAKeyInfo();
			xmlSignature.sign(node);
			System.out.println("2b. XmlSignature Signed (RSA KeyInfo): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			// Verifica
			 xmlVerify = new VerifyXmlSignature(truststore,alias);
			System.out.println("2b. XmlSignature Verify (RSA KeyInfo) (no clean): "+xmlVerify.verify(node, false));
			System.out.println("2b. XmlSignature Verify (RSA KeyInfo) (no clean) xml: "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			System.out.println("2b. XmlSignature Verify (RSA KeyInfo) (clean): "+xmlVerify.verify(node, true));
			System.out.println("2b. XmlSignature Verify (RSA KeyInfo) (clean) xml: "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			keyInfo = xmlVerify.getKeyInfo();
			System.out.println("2b. XmlSignature KeyInfo (RSA KeyInfo): "+keyInfo.getPublicKey());
			
			
			
			
			
			// 3. Esempio Signature JSON
						
			System.out.println("\n\n ================================");
			System.out.println("3. Example JsonSignature \n");

			String jsonInput = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"value2\"\n}";

			// Firma
			Properties signatureProps = new Properties();
			InputStream is = TestSignature.class.getResourceAsStream("jws.signature.properties");
			signatureProps.load(is);
			signatureProps.put("rs.security.keystore.file", fKeystore.getPath());

			Properties verifySignatureProps = new Properties();
			InputStream verifySignaturePropsis = TestSignature.class.getResourceAsStream("jws.verify.signature.properties");
			verifySignatureProps.load(verifySignaturePropsis);
			verifySignatureProps.put("rs.security.keystore.file", fTruststore.getPath());
			
			
			System.out.println("\n");
			
			// 3a. Signature Attached 
			JsonSignature jsonAttachedSignature = new JsonSignature(signatureProps, JOSERepresentation.SELF_CONTAINED);
			String attachSign = jsonAttachedSignature.sign(jsonInput);
			
			System.out.println("3a. JsonSelfContainedSignature Signed: \n"+attachSign);
			
			// Verifica
			JsonVerifySignature jsonAttachedVerify = new JsonVerifySignature(verifySignatureProps,JOSERepresentation.SELF_CONTAINED);
			System.out.println("3a. JsonSelfContainedSignature Verify ("+jsonAttachedVerify.verify(attachSign)+") payload: "+jsonAttachedVerify.getDecodedPayload());
			if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
				throw new Exception("Found different payload");
			}
			if(jsonAttachedVerify.verify(attachSign.replace("payload\":\"", "payload\":\"CORROMPO"))!=false) {
				throw new Exception("Expected validation error");
			}
			
			System.out.println("\n\n");
			
			// 3b. Signature Compact
			JsonSignature jsonCompactSignature = new JsonSignature(signatureProps, JOSERepresentation.COMPACT);
			String compactSign = jsonCompactSignature.sign(jsonInput);
			
			System.out.println("3b. JsonCompactSignature Signed: \n"+compactSign);
			
			// Verifica
			JsonVerifySignature jsonCompactVerify = new JsonVerifySignature(verifySignatureProps, JOSERepresentation.COMPACT);
			System.out.println("3b. JsonCompactSignature Verify ("+jsonCompactVerify.verify(compactSign)+" ) payload: "+jsonCompactVerify.getDecodedPayload());
			if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
				throw new Exception("Found different payload");
			}
			if(jsonCompactVerify.verify(compactSign.replace(".", ".CORROMPO"))!=false) {
				throw new Exception("Expected validation error");
			}

			
			System.out.println("\n\n");
			
			// 3c. Signature Detached
			JsonSignature jsonDetachedSignature = new JsonSignature(signatureProps, JOSERepresentation.DETACHED);
			String detachedSign = jsonDetachedSignature.sign(jsonInput);
			
			System.out.println("3c. JsonDetachedSignature Signed: \n"+detachedSign);
			
			// Verifica
			JsonVerifySignature jsonDetachedVerify = new JsonVerifySignature(verifySignatureProps, JOSERepresentation.DETACHED);
			System.out.println("3c. JsonDetachedSignature Verify ("+jsonDetachedVerify.verify(detachedSign, jsonInput)+") payload:"+jsonDetachedVerify.getDecodedPayload());
			if(jsonDetachedVerify.getDecodedPayload().equals(jsonInput)==false) {
				throw new Exception("Found different payload");
			}
			String jsonInputCorretto = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"valueCORROMPO\"\n}";
			if(jsonDetachedVerify.verify(detachedSign, jsonInputCorretto)!=false) {
				throw new Exception("Expected validation error");
			}
			
			
			
			
			
			
			
			
			
			
			
			// 4. Esempio Signature JSON con altri costruttori
			
			System.out.println("\n\n ================================");
			System.out.println("4. Example JsonSignature (Costruttore keystore) \n");

			String signatureAlgorithm = signatureProps.getProperty("rs.security.signature.algorithm");

			
			System.out.println("\n");
			
			// 4a. Signature Attached 
			jsonAttachedSignature = new JsonSignature(keystore.getKeystore(), alias, passwordChiavePrivata, signatureAlgorithm , JOSERepresentation.SELF_CONTAINED);
			attachSign = jsonAttachedSignature.sign(jsonInput);
			
			System.out.println("4a. JsonSelfContainedSignature Signed: \n"+attachSign);
			
			// Verifica
			jsonAttachedVerify = new JsonVerifySignature(truststore.getKeystore(), alias, signatureAlgorithm, JOSERepresentation.SELF_CONTAINED);
			System.out.println("4a. JsonSelfContainedSignature Verify ("+jsonAttachedVerify.verify(attachSign)+") payload: "+jsonAttachedVerify.getDecodedPayload());
			if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
				throw new Exception("Found different payload");
			}
			if(jsonAttachedVerify.verify(attachSign.replace("payload\":\"", "payload\":\"CORROMPO"))!=false) {
				throw new Exception("Expected validation error");
			}
			
			System.out.println("\n\n");
			
			// 4b. Signature Compact (e uso keystore openspcoop)
			jsonCompactSignature = new JsonSignature(keystore, alias, passwordChiavePrivata, signatureAlgorithm, JOSERepresentation.COMPACT);
			compactSign = jsonCompactSignature.sign(jsonInput);
			
			System.out.println("4b. JsonCompactSignature Signed: \n"+compactSign);
			
			// Verifica
			jsonCompactVerify = new JsonVerifySignature(truststore, alias, signatureAlgorithm, JOSERepresentation.COMPACT);
			System.out.println("4b. JsonCompactSignature Verify ("+jsonCompactVerify.verify(compactSign)+" ) payload: "+jsonCompactVerify.getDecodedPayload());
			if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
				throw new Exception("Found different payload");
			}
			if(jsonCompactVerify.verify(compactSign.replace(".", ".CORROMPO"))!=false) {
				throw new Exception("Expected validation error");
			}

			
			System.out.println("\n\n");
			
			// 4c. Signature Detached
			jsonDetachedSignature = new JsonSignature(keystore.getKeystore(), alias, passwordChiavePrivata, signatureAlgorithm, JOSERepresentation.DETACHED);
			detachedSign = jsonDetachedSignature.sign(jsonInput);
			
			System.out.println("4c. JsonDetachedSignature Signed: \n"+detachedSign);
			
			// Verifica
			jsonDetachedVerify = new JsonVerifySignature(truststore.getKeystore(), alias, signatureAlgorithm, JOSERepresentation.DETACHED);
			System.out.println("4c. JsonDetachedSignature Verify ("+jsonDetachedVerify.verify(detachedSign, jsonInput)+") payload:"+jsonDetachedVerify.getDecodedPayload());
			if(jsonDetachedVerify.getDecodedPayload().equals(jsonInput)==false) {
				throw new Exception("Found different payload");
			}
			jsonInputCorretto = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"valueCORROMPO\"\n}";
			if(jsonDetachedVerify.verify(detachedSign, jsonInputCorretto)!=false) {
				throw new Exception("Expected validation error");
			}
			
			
			
			
			
			System.out.println("Testsuite terminata");


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
