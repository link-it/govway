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

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.xml.security.keys.KeyInfo;
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
 * TestSignature
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestSignature {

	public static void main(String[] args) throws Exception {
		
		TipoTest tipoTest = null;
		if(args!=null && args.length>0) {
			tipoTest = TipoTest.valueOf(args[0]);
		}
		
		InputStream isKeystore = null;
		File fKeystore = null;
		InputStream isTruststore = null;
		File fTruststore = null;
		
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
			isKeystore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jks");
			fKeystore = File.createTempFile("keystore", ".jks");
			FileSystemUtilities.writeFile(fKeystore, Utilities.getAsByteArray(isKeystore));
			
			isTruststore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/truststore_example.jks");
			fTruststore = File.createTempFile("truststore", ".jks");
			FileSystemUtilities.writeFile(fTruststore, Utilities.getAsByteArray(isTruststore));
			
			jwks_isKeystore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jwks");
			jwks_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwks_fKeystore, Utilities.getAsByteArray(jwks_isKeystore));
			
			jwks_isTruststore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/truststore_example.jwks");
			jwks_fTruststore = File.createTempFile("truststore", ".jwk");
			FileSystemUtilities.writeFile(jwks_fTruststore, Utilities.getAsByteArray(jwks_isTruststore));
			
			jwk_isKeystore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jwk");
			jwk_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwk_fKeystore, Utilities.getAsByteArray(jwk_isKeystore));
			
			jwk_isTruststore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/truststore_example.jwk");
			jwk_fTruststore = File.createTempFile("truststore", ".jwk");
			FileSystemUtilities.writeFile(jwk_fTruststore, Utilities.getAsByteArray(jwk_isTruststore));
			
			String passwordChiavePrivata = "key123456";
			String passwordStore = "123456";
			String alias = "openspcoop";
			
			KeyStore keystore = new KeyStore(fKeystore.getAbsolutePath(), passwordStore);
			KeyStore truststore = new KeyStore(fTruststore.getAbsolutePath(), passwordStore);
			
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
			
			
			// Esempio Signature Java
			
			if(tipoTest==null || TipoTest.JAVA_SIGNATURE.equals(tipoTest)) {
				testJava(TipoTest.JAVA_SIGNATURE, keystore, truststore, alias, passwordChiavePrivata);
			}
			
			
			
			// Esempio Signature Xml 
			
			if(tipoTest==null || TipoTest.XML_SIGNATURE.equals(tipoTest)) {
				testXmlSignature(TipoTest.XML_SIGNATURE, keystore, truststore, alias, passwordChiavePrivata);
			}
			
			

			// Esempio Signature JSON
				
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JKS.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS, fKeystore, fTruststore, truststore, null);
			}
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM, fKeystore, fTruststore, truststore, jwtHeader);
				jwtHeader.setX509Url(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK, jwks_fKeystore, jwks_fTruststore, truststore, null);
			}
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM, jwks_fKeystore, jwks_fTruststore, truststore, jwtHeader);
				jwtHeader.setJwkUrl(null);
			}
			
			
			
			
			
			
			// Esempio Signature JSON con altri costruttori
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_KEYSTORE.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystore.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(alias);
				
				testJsonKeystore(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_KEYSTORE, keystore, truststore, alias, passwordChiavePrivata, jwtHeader);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_KEYSTORE_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystore.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				jwtHeader.setKid(alias);
				
				testJsonKeystore(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_KEYSTORE_HEADER_CUSTOM, keystore, truststore, alias, passwordChiavePrivata, jwtHeader);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
			}
			
			
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_KEYS.equals(tipoTest)) {
				
				jwtHeader.setJwKey(jwks_keystore, alias);
				jwtHeader.setKid(alias);
				
				testJsonJwkKeys(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_KEYS, jwks_keystore, jwks_truststore, alias, jwtHeader);
				
				jwtHeader.setJwKey(null);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_KEYS_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));
				
				testJsonJwkKeys(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_KEYS, jwks_keystore, jwks_truststore, alias, jwtHeader);
				
				jwtHeader.setJwkUrl(null);
				jwtHeader.setKid(null);
			}
			
			
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_KEY.equals(tipoTest)) {
				
				jwtHeader.setJwKey(jwk_keystore);
				jwtHeader.setKid(alias);
				
				testJsonJwkKey(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_KEY, jwk_keystore, jwk_truststore, jwtHeader);
				
				jwtHeader.setJwKey(null);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_KEY_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));
				
				testJsonJwkKey(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_KEY, jwk_keystore, jwk_truststore, jwtHeader);
				
				jwtHeader.setJwkUrl(null);
				jwtHeader.setKid(null);
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
		// Esempio Signature Java 
		
		System.out.println("\n\n ============== "+tipo+" ==================");
		System.out.println("["+tipo+"]. Example JavaSignature \n");
		
		String contenutoDaFirmare = "MarioRossi:23:05:1980";
		
		// Firma
		String signedAlgorithm = "SHA1WithRSA";
		Signature signature = new Signature(keystore, alias, passwordChiavePrivata);
		byte[] signed = signature.sign(contenutoDaFirmare.getBytes(), signedAlgorithm);
		System.out.println("["+tipo+"]. JavaSignature Signed: "+new String(signed));
		
		// Verifica
		VerifySignature verify = new VerifySignature(truststore,alias);
		System.out.println("["+tipo+"]. JavaSignature Verify: "+verify.verify(contenutoDaFirmare.getBytes(), signed, signedAlgorithm));
	}
	
	
	private static void testXmlSignature(TipoTest tipo, KeyStore keystore, KeyStore truststore, String alias, String passwordChiavePrivata) throws Exception {
		// Esempio Signature Xml 
		
		System.out.println("\n\n ============== "+tipo+" ==================");
		System.out.println("["+tipo+"]. Example XmlSignature \n");
		
		String xmlInput = "<prova><test>VALORE</test></prova>";
		Element node = XMLUtils.getInstance().newElement(xmlInput.getBytes());
		
		// Firma
		XmlSignature xmlSignature = new XmlSignature(keystore, alias, passwordChiavePrivata);
		xmlSignature.addX509KeyInfo();
		xmlSignature.sign(node);
		System.out.println("["+tipo+"]-a. XmlSignature Signed (X509 KeyInfo): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		// Verifica
		VerifyXmlSignature xmlVerify = new VerifyXmlSignature(truststore,alias);
		System.out.println("["+tipo+"]-a. XmlSignature Verify (X509 KeyInfo) (no clean): "+xmlVerify.verify(node, false));
		System.out.println("["+tipo+"]-a. XmlSignature Verify (X509 KeyInfo) (no clean) xml: "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		System.out.println("["+tipo+"]-a. XmlSignature Verify (X509 KeyInfo) (clean): "+xmlVerify.verify(node, true));
		System.out.println("["+tipo+"]-a. XmlSignature Verify (X509 KeyInfo) (clean) xml: "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		KeyInfo keyInfo = xmlVerify.getKeyInfo();
		System.out.println("["+tipo+"]-a. XmlSignature KeyInfo (X509 KeyInfo): "+keyInfo.getX509Certificate().getIssuerX500Principal().getName());
		
		
		// Firma
		xmlSignature = new XmlSignature(keystore, alias, passwordChiavePrivata);
		xmlSignature.addRSAKeyInfo();
		xmlSignature.sign(node);
		System.out.println("["+tipo+"]-b. XmlSignature Signed (RSA KeyInfo): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		// Verifica
		 xmlVerify = new VerifyXmlSignature(truststore,alias);
		System.out.println("["+tipo+"]-b. XmlSignature Verify (RSA KeyInfo) (no clean): "+xmlVerify.verify(node, false));
		System.out.println("["+tipo+"]-b. XmlSignature Verify (RSA KeyInfo) (no clean) xml: "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		System.out.println("["+tipo+"]-b. XmlSignature Verify (RSA KeyInfo) (clean): "+xmlVerify.verify(node, true));
		System.out.println("["+tipo+"]-b. XmlSignature Verify (RSA KeyInfo) (clean) xml: "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		keyInfo = xmlVerify.getKeyInfo();
		System.out.println("["+tipo+"]-b. XmlSignature KeyInfo (RSA KeyInfo): "+keyInfo.getPublicKey());
	}
	
	private static void testJsonProperties(TipoTest tipo, File fKeystore, File fTruststore, KeyStore truststore, JwtHeaders headers) throws Exception {
		// Esempio Signature JSON
		
		System.out.println("\n\n ============== "+tipo+" ==================");
		System.out.println("["+tipo+"]. Example JsonSignature \n");

		String jsonInput = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"value2\"\n}";

		// Firma
		Properties signatureProps = new Properties();
		InputStream is = TestSignature.class.getResourceAsStream("jws.signature.properties");
		signatureProps.load(is);
		signatureProps.put("rs.security.keystore.file", fKeystore.getPath());
		signatureProps.remove("rs.security.keystore.type");
		if(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipo)) {
			signatureProps.put("rs.security.keystore.type", "jks");
			
			if(headers!=null) {
				// inverto
				signatureProps.put("rs.security.signature.include.cert.sha1", "true");
				signatureProps.put("rs.security.signature.include.cert.sha256", "false");
			}
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipo)) {
			signatureProps.put("rs.security.keystore.type", "jwk");
			
			signatureProps.remove("rs.security.signature.include.cert.sha1");
			signatureProps.remove("rs.security.signature.include.cert.sha256");
		}
		
		
		if(headers!=null) {
			signatureProps.remove("rs.security.signature.include.cert");
			signatureProps.remove("rs.security.signature.include.public.key");
		}
		
		Properties verifySignatureProps = new Properties();
		InputStream verifySignaturePropsis = TestSignature.class.getResourceAsStream("jws.verify.signature.properties");
		verifySignatureProps.load(verifySignaturePropsis);
		verifySignatureProps.put("rs.security.keystore.file", fTruststore.getPath());
		verifySignatureProps.remove("rs.security.keystore.type");
		if(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipo)) {
			verifySignatureProps.put("rs.security.keystore.type", "jks");
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipo) ) {
			verifySignatureProps.put("rs.security.keystore.type", "jwk");
		}
		
		

		System.out.println("\n");
		
		// 3a. Signature Attached 
		JsonSignature jsonAttachedSignature = new JsonSignature(signatureProps, JOSERepresentation.SELF_CONTAINED);
		String attachSign = jsonAttachedSignature.sign(jsonInput);
		
		System.out.println("["+tipo+"]-a. JsonSelfContainedSignature Signed: \n"+attachSign);
		
		// Verifica
		JsonVerifySignature jsonAttachedVerify = new JsonVerifySignature(verifySignatureProps,JOSERepresentation.SELF_CONTAINED);
		System.out.println("["+tipo+"]-a. JsonSelfContainedSignature Verify ("+jsonAttachedVerify.verify(attachSign)+") payload: "+jsonAttachedVerify.getDecodedPayload());
		if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		if(jsonAttachedVerify.verify(attachSign.replace("payload\":\"", "payload\":\"CORROMPO"))!=false) {
			throw new Exception("Expected validation error");
		}
		
		System.out.println("\n\n");
		
		// 3b. Signature Compact
		JsonSignature jsonCompactSignature = null;
		if(headers==null) {
			jsonCompactSignature = new JsonSignature(signatureProps, JOSERepresentation.COMPACT);
		}
		else {
			jsonCompactSignature = new JsonSignature(signatureProps, headers, JOSERepresentation.COMPACT);
		}
		String compactSign = jsonCompactSignature.sign(jsonInput);
		
		System.out.println("["+tipo+"]-b. JsonCompactSignature Signed: \n"+compactSign);
		
		String hdrDecoded = new String (Base64Utilities.decode(compactSign.split("\\.")[0]));
		System.out.println("["+tipo+"]-b. JsonCompactSignature HDR base64 decoded: "+hdrDecoded);
		
		// Verifica
		JsonVerifySignature jsonCompactVerify = new JsonVerifySignature(verifySignatureProps, JOSERepresentation.COMPACT);
		System.out.println("["+tipo+"]-b. JsonCompactSignature Verify ("+jsonCompactVerify.verify(compactSign)+" ) payload: "+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		if(jsonCompactVerify.verify(compactSign.replace(".", ".CORROMPO"))!=false) {
			throw new Exception("Expected validation error");
		}
		
		// Verifica basata sull'header
		jsonCompactVerify = new JsonVerifySignature(truststore);
		System.out.println("["+tipo+"]-b2. JsonCompactSignature Verify HEADER ("+jsonCompactVerify.verify(compactSign)+" ) payload: "+jsonCompactVerify.getDecodedPayload());
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
		
		System.out.println("["+tipo+"]-c. JsonDetachedSignature Signed: \n"+detachedSign);
		
		// Verifica
		JsonVerifySignature jsonDetachedVerify = new JsonVerifySignature(verifySignatureProps, JOSERepresentation.DETACHED);
		System.out.println("["+tipo+"]-c. JsonDetachedSignature Verify ("+jsonDetachedVerify.verify(detachedSign, jsonInput)+") payload:"+jsonDetachedVerify.getDecodedPayload());
		if(jsonDetachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		String jsonInputCorretto = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"valueCORROMPO\"\n}";
		if(jsonDetachedVerify.verify(detachedSign, jsonInputCorretto)!=false) {
			throw new Exception("Expected validation error");
		}
	}
	
	private static void testJsonKeystore(TipoTest tipo, KeyStore keystore, KeyStore truststore, String alias, String passwordChiavePrivata, JwtHeaders headers) throws Exception {
		// Esempio Signature JSON con altri costruttori
			
		String jsonInput = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"value2\"\n}";
		
		System.out.println("\n\n ================ "+tipo+"  ================");
		System.out.println("["+tipo+"]. Example JsonSignature (Costruttore keystore) \n");

		Properties signatureProps = new Properties();
		InputStream is = TestSignature.class.getResourceAsStream("jws.signature.properties");
		signatureProps.load(is);
		
		String signatureAlgorithm = signatureProps.getProperty("rs.security.signature.algorithm");

		
		System.out.println("\n");
		
		// 4a. Signature Attached 
		JsonSignature jsonAttachedSignature = new JsonSignature(keystore.getKeystore(), alias, passwordChiavePrivata, signatureAlgorithm , JOSERepresentation.SELF_CONTAINED);
		String attachSign = jsonAttachedSignature.sign(jsonInput);
		
		System.out.println("["+tipo+"]-a. JsonSelfContainedSignature Signed: \n"+attachSign);
		
		// Verifica
		JsonVerifySignature jsonAttachedVerify = new JsonVerifySignature(truststore.getKeystore(), alias, signatureAlgorithm, JOSERepresentation.SELF_CONTAINED);
		System.out.println("["+tipo+"]-a. JsonSelfContainedSignature Verify ("+jsonAttachedVerify.verify(attachSign)+") payload: "+jsonAttachedVerify.getDecodedPayload());
		if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		if(jsonAttachedVerify.verify(attachSign.replace("payload\":\"", "payload\":\"CORROMPO"))!=false) {
			throw new Exception("Expected validation error");
		}
		
		System.out.println("\n\n");
		
		// 4b. Signature Compact (e uso keystore openspcoop)
		JsonSignature jsonCompactSignature = new JsonSignature(keystore, alias, passwordChiavePrivata, signatureAlgorithm, headers, JOSERepresentation.COMPACT);
		String compactSign = jsonCompactSignature.sign(jsonInput);
		
		System.out.println("["+tipo+"]-b. JsonCompactSignature Signed: \n"+compactSign);
		
		String hdrDecoded = new String (Base64Utilities.decode(compactSign.split("\\.")[0]));
		System.out.println("["+tipo+"]-b. JsonCompactSignature HDR base64 decoded: "+hdrDecoded);
		
		// Verifica
		JsonVerifySignature jsonCompactVerify = new JsonVerifySignature(truststore, alias, signatureAlgorithm, JOSERepresentation.COMPACT);
		System.out.println("["+tipo+"]-b. JsonCompactSignature Verify ("+jsonCompactVerify.verify(compactSign)+" ) payload: "+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		if(jsonCompactVerify.verify(compactSign.replace(".", ".CORROMPO"))!=false) {
			throw new Exception("Expected validation error");
		}
		
		// Verifica basata sull'header
		jsonCompactVerify = new JsonVerifySignature(truststore);
		System.out.println("["+tipo+"]-b2. JsonCompactSignature Verify HEADER ("+jsonCompactVerify.verify(compactSign)+" ) payload: "+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		if(jsonCompactVerify.verify(compactSign.replace(".", ".CORROMPO"))!=false) {
			throw new Exception("Expected validation error");
		}

		
		System.out.println("\n\n");
		
		// 4c. Signature Detached
		JsonSignature jsonDetachedSignature = new JsonSignature(keystore.getKeystore(), alias, passwordChiavePrivata, signatureAlgorithm, JOSERepresentation.DETACHED);
		String detachedSign = jsonDetachedSignature.sign(jsonInput);
		
		System.out.println("["+tipo+"]-c. JsonDetachedSignature Signed: \n"+detachedSign);
		
		// Verifica
		JsonVerifySignature jsonDetachedVerify = new JsonVerifySignature(truststore.getKeystore(), alias, signatureAlgorithm, JOSERepresentation.DETACHED);
		System.out.println("["+tipo+"]-c. JsonDetachedSignature Verify ("+jsonDetachedVerify.verify(detachedSign, jsonInput)+") payload:"+jsonDetachedVerify.getDecodedPayload());
		if(jsonDetachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		String jsonInputCorretto = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"valueCORROMPO\"\n}";
		if(jsonDetachedVerify.verify(detachedSign, jsonInputCorretto)!=false) {
			throw new Exception("Expected validation error");
		}
	}
	
	private static void testJsonJwkKeys(TipoTest tipo, JsonWebKeys keystore, JsonWebKeys truststore, String alias, JwtHeaders headers) throws Exception {
		// Esempio Signature JSON con altri costruttori
			
		String jsonInput = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"value2\"\n}";
		
		System.out.println("\n\n =============== "+tipo+" =================");
		System.out.println("["+tipo+"]. Example JsonSignature (Costruttore keystore) \n");

		Properties signatureProps = new Properties();
		InputStream is = TestSignature.class.getResourceAsStream("jws.signature.properties");
		signatureProps.load(is);
		
		String signatureAlgorithm = signatureProps.getProperty("rs.security.signature.algorithm");

		
		System.out.println("\n");
		
		// 4a. Signature Attached 
		JsonSignature jsonAttachedSignature = new JsonSignature(keystore, alias, signatureAlgorithm , JOSERepresentation.SELF_CONTAINED);
		String attachSign = jsonAttachedSignature.sign(jsonInput);
		
		System.out.println("["+tipo+"]-a. JsonSelfContainedSignature Signed: \n"+attachSign);
		
		// Verifica
		JsonVerifySignature jsonAttachedVerify = new JsonVerifySignature(truststore, alias, signatureAlgorithm, JOSERepresentation.SELF_CONTAINED);
		System.out.println("["+tipo+"]-a. JsonSelfContainedSignature Verify ("+jsonAttachedVerify.verify(attachSign)+") payload: "+jsonAttachedVerify.getDecodedPayload());
		if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		if(jsonAttachedVerify.verify(attachSign.replace("payload\":\"", "payload\":\"CORROMPO"))!=false) {
			throw new Exception("Expected validation error");
		}
		
		System.out.println("\n\n");
		
		// 4b. Signature Compact (e uso keystore openspcoop)
		JsonSignature jsonCompactSignature = new JsonSignature(keystore, alias, signatureAlgorithm, headers, JOSERepresentation.COMPACT);
		String compactSign = jsonCompactSignature.sign(jsonInput);
		
		System.out.println("["+tipo+"]-b. JsonCompactSignature Signed: \n"+compactSign);
		
		String hdrDecoded = new String (Base64Utilities.decode(compactSign.split("\\.")[0]));
		System.out.println("["+tipo+"]-b. JsonCompactSignature HDR base64 decoded: "+hdrDecoded);
		
		// Verifica
		JsonVerifySignature jsonCompactVerify = new JsonVerifySignature(truststore, alias, signatureAlgorithm, JOSERepresentation.COMPACT);
		System.out.println("["+tipo+"]-b. JsonCompactSignature Verify ("+jsonCompactVerify.verify(compactSign)+" ) payload: "+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		if(jsonCompactVerify.verify(compactSign.replace(".", ".CORROMPO"))!=false) {
			throw new Exception("Expected validation error");
		}
		
		// Verifica basata sull'header
		jsonCompactVerify = new JsonVerifySignature();
		System.out.println("["+tipo+"]-b2. JsonCompactSignature Verify HEADER ("+jsonCompactVerify.verify(compactSign)+" ) payload: "+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		if(jsonCompactVerify.verify(compactSign.replace(".", ".CORROMPO"))!=false) {
			throw new Exception("Expected validation error");
		}

		
		System.out.println("\n\n");
		
		// 4c. Signature Detached
		JsonSignature jsonDetachedSignature = new JsonSignature(keystore, alias, signatureAlgorithm, JOSERepresentation.DETACHED);
		String detachedSign = jsonDetachedSignature.sign(jsonInput);
		
		System.out.println("["+tipo+"]-c. JsonDetachedSignature Signed: \n"+detachedSign);
		
		// Verifica
		JsonVerifySignature jsonDetachedVerify = new JsonVerifySignature(truststore, alias, signatureAlgorithm, JOSERepresentation.DETACHED);
		System.out.println("["+tipo+"]-c. JsonDetachedSignature Verify ("+jsonDetachedVerify.verify(detachedSign, jsonInput)+") payload:"+jsonDetachedVerify.getDecodedPayload());
		if(jsonDetachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		String jsonInputCorretto = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"valueCORROMPO\"\n}";
		if(jsonDetachedVerify.verify(detachedSign, jsonInputCorretto)!=false) {
			throw new Exception("Expected validation error");
		}
	}
	
	
	private static void testJsonJwkKey(TipoTest tipo, JsonWebKey keystore, JsonWebKey truststore, JwtHeaders headers) throws Exception {
		// Esempio Signature JSON con altri costruttori
			
		String jsonInput = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"value2\"\n}";
		
		System.out.println("\n\n =============== "+tipo+" =================");
		System.out.println("["+tipo+"]. Example JsonSignature (Costruttore keystore) \n");

		Properties signatureProps = new Properties();
		InputStream is = TestSignature.class.getResourceAsStream("jws.signature.properties");
		signatureProps.load(is);
		
		String signatureAlgorithm = signatureProps.getProperty("rs.security.signature.algorithm");

		
		System.out.println("\n");
		
		// 4a. Signature Attached 
		JsonSignature jsonAttachedSignature = new JsonSignature(keystore, signatureAlgorithm , JOSERepresentation.SELF_CONTAINED);
		String attachSign = jsonAttachedSignature.sign(jsonInput);
		
		System.out.println("["+tipo+"]-a. JsonSelfContainedSignature Signed: \n"+attachSign);
		
		// Verifica
		JsonVerifySignature jsonAttachedVerify = new JsonVerifySignature(truststore, signatureAlgorithm, JOSERepresentation.SELF_CONTAINED);
		System.out.println("["+tipo+"]-a. JsonSelfContainedSignature Verify ("+jsonAttachedVerify.verify(attachSign)+") payload: "+jsonAttachedVerify.getDecodedPayload());
		if(jsonAttachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		if(jsonAttachedVerify.verify(attachSign.replace("payload\":\"", "payload\":\"CORROMPO"))!=false) {
			throw new Exception("Expected validation error");
		}
		
		System.out.println("\n\n");
		
		// 4b. Signature Compact (e uso keystore openspcoop)
		JsonSignature jsonCompactSignature = new JsonSignature(keystore, signatureAlgorithm, headers, JOSERepresentation.COMPACT);
		String compactSign = jsonCompactSignature.sign(jsonInput);
		
		System.out.println("["+tipo+"]-b. JsonCompactSignature Signed: \n"+compactSign);
		
		String hdrDecoded = new String (Base64Utilities.decode(compactSign.split("\\.")[0]));
		System.out.println("["+tipo+"]-b. JsonCompactSignature HDR base64 decoded: "+hdrDecoded);
		
		// Verifica
		JsonVerifySignature jsonCompactVerify = new JsonVerifySignature(truststore, signatureAlgorithm, JOSERepresentation.COMPACT);
		System.out.println("["+tipo+"]-b. JsonCompactSignature Verify ("+jsonCompactVerify.verify(compactSign)+" ) payload: "+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		if(jsonCompactVerify.verify(compactSign.replace(".", ".CORROMPO"))!=false) {
			throw new Exception("Expected validation error");
		}
		
		// Verifica basata sull'header
		jsonCompactVerify = new JsonVerifySignature();
		System.out.println("["+tipo+"]-b2. JsonCompactSignature Verify HEADER ("+jsonCompactVerify.verify(compactSign)+" ) payload: "+jsonCompactVerify.getDecodedPayload());
		if(jsonCompactVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		if(jsonCompactVerify.verify(compactSign.replace(".", ".CORROMPO"))!=false) {
			throw new Exception("Expected validation error");
		}
		
		
		System.out.println("\n\n");
		
		// 4c. Signature Detached
		JsonSignature jsonDetachedSignature = new JsonSignature(keystore, signatureAlgorithm, JOSERepresentation.DETACHED);
		String detachedSign = jsonDetachedSignature.sign(jsonInput);
		
		System.out.println("["+tipo+"]-c. JsonDetachedSignature Signed: \n"+detachedSign);
		
		// Verifica
		JsonVerifySignature jsonDetachedVerify = new JsonVerifySignature(truststore, signatureAlgorithm, JOSERepresentation.DETACHED);
		System.out.println("["+tipo+"]-c. JsonDetachedSignature Verify ("+jsonDetachedVerify.verify(detachedSign, jsonInput)+") payload:"+jsonDetachedVerify.getDecodedPayload());
		if(jsonDetachedVerify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		String jsonInputCorretto = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"valueCORROMPO\"\n}";
		if(jsonDetachedVerify.verify(detachedSign, jsonInputCorretto)!=false) {
			throw new Exception("Expected validation error");
		}
	}
	
	public enum TipoTest {
		
		JAVA_SIGNATURE, 
		XML_SIGNATURE,
		JSON_SIGNATURE_PROPERTIES_JKS,
		JSON_SIGNATURE_PROPERTIES_JWK,
		JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_JKS_KEYSTORE,
		JSON_SIGNATURE_PROPERTIES_JKS_KEYSTORE_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_JWK_KEYS,
		JSON_SIGNATURE_PROPERTIES_JWK_KEYS_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_JWK_KEY,
		JSON_SIGNATURE_PROPERTIES_JWK_KEY_HEADER_CUSTOM,
		
	}
}
