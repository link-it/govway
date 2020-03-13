/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.util.ArrayList;
import java.util.List;
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

		InputStream jwks_symmetric_isKeystore = null;
		File jwks_symmetric_fKeystore = null;

		InputStream jwk_symmetric_isKeystore = null;
		File jwk_symmetric_fKeystore = null;
		try{
			isKeystore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jks");
			fKeystore = File.createTempFile("keystore", ".jks");
			FileSystemUtilities.writeFile(fKeystore, Utilities.getAsByteArray(isKeystore));

			isTruststore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/truststore_example.jks");
			fTruststore = File.createTempFile("truststore", ".jks");
			FileSystemUtilities.writeFile(fTruststore, Utilities.getAsByteArray(isTruststore));

			isKeystoreJCEKS = TestEncrypt.class.getResourceAsStream("/org/openspcoop2/utils/security/example.jceks");
			fKeystoreJCEKS = File.createTempFile("keystore", "jceks");
			FileSystemUtilities.writeFile(fKeystoreJCEKS, Utilities.getAsByteArray(isKeystoreJCEKS));

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

			jwks_symmetric_isKeystore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_symmetricKey_example.jwks");
			jwks_symmetric_fKeystore = File.createTempFile("keystore", ".jwks");
			FileSystemUtilities.writeFile(jwks_symmetric_fKeystore, Utilities.getAsByteArray(jwks_symmetric_isKeystore));

			jwk_symmetric_isKeystore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_symmetricKey_example.jwk");
			jwk_symmetric_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwk_symmetric_fKeystore, Utilities.getAsByteArray(jwk_symmetric_isKeystore));

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

			JsonWebKeys jwks_symmetric_keystore = new JWKSet(FileSystemUtilities.readFile(jwks_symmetric_fKeystore)).getJsonWebKeys();
			JsonWebKey jwk_symmetric_keystore = new JWK(FileSystemUtilities.readFile(jwk_symmetric_fKeystore)).getJsonWebKey();


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


			// Esempio Signature PKCS7

			if (tipoTest == null || TestSignature.TipoTest.PKCS7_SIGNATURE.equals(tipoTest)) {
				testPkcs7(TestSignature.TipoTest.PKCS7_SIGNATURE, keystore, truststore, alias, passwordChiavePrivata);
			}



			// Esempio Signature Xml 

			if(tipoTest==null || TipoTest.XML_SIGNATURE.equals(tipoTest)) {
				testXmlSignature(TipoTest.XML_SIGNATURE, keystore, truststore, alias, passwordChiavePrivata);
			}



			// Esempio Signature JSON

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JKS.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS, fKeystore, fTruststore, truststore, null, null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM, fKeystore, fTruststore, truststore, jwtHeader, null);
				jwtHeader.setX509Url(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM_KID_ONLY.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM_KID_ONLY, fKeystore, fTruststore, truststore, jwtHeader, null);
			}



			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS, fKeystoreJCEKS, fKeystoreJCEKS, null, null, null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM, fKeystoreJCEKS, fKeystoreJCEKS, null, jwtHeader, null);
				jwtHeader.setX509Url(null);
			}



			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK, jwks_fKeystore, jwks_fTruststore, truststore, null, null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM, jwks_fKeystore, jwks_fTruststore, truststore, jwtHeader, null);
				jwtHeader.setJwkUrl(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY, jwks_fKeystore, jwks_fTruststore, truststore, jwtHeader, jwks_truststore);
			}



			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC, jwks_symmetric_fKeystore, jwks_symmetric_fKeystore, null, null, null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM, jwks_symmetric_fKeystore, jwks_symmetric_fKeystore, null, jwtHeader, null);
				jwtHeader.setJwkUrl(null);
			}




			// Esempio Signature JSON con altri costruttori

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JKS_KEYSTORE.equals(tipoTest)) {

				jwtHeader.addX509cert((X509Certificate)keystore.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(alias);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_JKS_KEYSTORE, keystore, truststore, alias, passwordChiavePrivata, jwtHeader);

				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JKS_KEYSTORE_HEADER_CUSTOM.equals(tipoTest)) {

				jwtHeader.addX509cert((X509Certificate)keystore.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				jwtHeader.setKid(alias);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_JKS_KEYSTORE_HEADER_CUSTOM, keystore, truststore, alias, passwordChiavePrivata, jwtHeader);

				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JCEKS_KEYSTORE.equals(tipoTest)) {

				jwtHeader.setKid(alias);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_JCEKS_KEYSTORE, keystoreJCEKS, keystoreJCEKS, alias, passwordChiavePrivata, jwtHeader);

				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JCEKS_KEYSTORE_HEADER_CUSTOM.equals(tipoTest)) {

				jwtHeader.setKid(alias);
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));

				testJsonKeystore(TipoTest.JSON_SIGNATURE_JCEKS_KEYSTORE_HEADER_CUSTOM, keystoreJCEKS, keystoreJCEKS, alias, passwordChiavePrivata, jwtHeader);

				jwtHeader.setKid(null);
				jwtHeader.setX509Url(null);
			}



			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_KEYS.equals(tipoTest)) {

				jwtHeader.setJwKey(jwks_keystore, alias);
				jwtHeader.setKid(alias);

				testJsonJwkKeys(TipoTest.JSON_SIGNATURE_JWK_KEYS, jwks_keystore, jwks_truststore, truststore, alias, jwtHeader);

				jwtHeader.setJwKey(null);
				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_KEYS_HEADER_CUSTOM.equals(tipoTest)) {

				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));

				testJsonJwkKeys(TipoTest.JSON_SIGNATURE_JWK_KEYS_HEADER_CUSTOM, jwks_keystore, jwks_truststore, truststore, alias, jwtHeader);

				jwtHeader.setJwkUrl(null);
				jwtHeader.setKid(null);
			}



			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_KEY.equals(tipoTest)) {

				jwtHeader.setJwKey(jwk_keystore);
				jwtHeader.setKid(alias);

				testJsonJwkKey(TipoTest.JSON_SIGNATURE_JWK_KEY, jwk_keystore, jwk_truststore, truststore, jwtHeader);

				jwtHeader.setJwKey(null);
				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_KEY_HEADER_CUSTOM.equals(tipoTest)) {

				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));

				testJsonJwkKey(TipoTest.JSON_SIGNATURE_JWK_KEY_HEADER_CUSTOM, jwk_keystore, jwk_truststore, truststore, jwtHeader);

				jwtHeader.setJwkUrl(null);
				jwtHeader.setKid(null);
			}






			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEYS.equals(tipoTest)) {

				jwtHeader.setKid(alias);

				testJsonJwkKeys(TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEYS, jwks_symmetric_keystore, jwks_symmetric_keystore, null, alias, jwtHeader);

				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEYS_HEADER_CUSTOM.equals(tipoTest)) {

				jwtHeader.setJwkUrl(new URI("file://"+jwks_symmetric_fKeystore.getAbsolutePath()));

				testJsonJwkKeys(TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEYS_HEADER_CUSTOM, jwks_symmetric_keystore, jwks_symmetric_keystore, null, alias, jwtHeader);

				jwtHeader.setJwkUrl(null);
			}



			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEY.equals(tipoTest)) {

				jwtHeader.setKid(alias);

				testJsonJwkKey(TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEY, jwk_symmetric_keystore, jwk_symmetric_keystore, null, jwtHeader);

				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEY_HEADER_CUSTOM.equals(tipoTest)) {

				jwtHeader.setJwkUrl(new URI("file://"+jwk_symmetric_fKeystore.getAbsolutePath()));

				testJsonJwkKey(TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEY_HEADER_CUSTOM, jwk_symmetric_keystore, jwk_symmetric_keystore, null, jwtHeader);

				jwtHeader.setJwkUrl(null);
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

			try{
				if(jwks_symmetric_isKeystore!=null){
					jwks_symmetric_isKeystore.close();
				}
			}catch(Exception e){}
			try{
				if(jwks_symmetric_fKeystore!=null){
					jwks_symmetric_fKeystore.delete();
				}
			}catch(Exception e){}

			try{
				if(jwk_symmetric_isKeystore!=null){
					jwk_symmetric_isKeystore.close();
				}
			}catch(Exception e){}
			try{
				if(jwk_symmetric_fKeystore!=null){
					jwk_symmetric_fKeystore.delete();
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



	private static void testPkcs7(TestSignature.TipoTest tipo, KeyStore keystore, KeyStore truststore, String alias, String passwordChiavePrivata) throws Exception {
		System.out.println("\n\n ============== " + tipo + " ==================");
		System.out.println("[" + tipo + "]. Example PKCS7Signature \n");
		String contenutoDaFirmare = "MarioRossi:23:05:1980";
		String signedAlgorithm = "SHA256withRSA";
		PKCS7Signature signature = new PKCS7Signature(keystore, alias, passwordChiavePrivata);
		byte[] signed = signature.sign(contenutoDaFirmare.getBytes(), signedAlgorithm);
		System.out.println("[" + tipo + "]. PKCS7Signature Signed: " + new String(signed));
		Utilities.sleep(2000L);
		byte[] signed2 = signature.sign(contenutoDaFirmare.getBytes(), signedAlgorithm);
		boolean equals = signed.length == signed2.length;
		if (equals) {
			for(int i = 0; i < signed.length; ++i) {
				if (signed[i] != signed2[i]) {
					equals = false;
					break;
				}
			}
		}

		if (equals) {
			System.out.println("[" + tipo + "]. Ottenute 2 firme uguali a distanza di 2 secondi");
		}

		VerifyPKCS7Signature verify = new VerifyPKCS7Signature(truststore, alias);
		System.out.println("[" + tipo + "]. PKCS7Signature Verify: " + verify.verify(signed, signedAlgorithm));
		String contenuto = new String(verify.getOriginalContent());
		System.out.println("[" + tipo + "]. PKCS7Signature Decodificato: " + contenuto);
		if (!contenutoDaFirmare.equals(contenuto)) {
			throw new Exception("Contenuto ottenuto dalla verifica differente");
		}
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

	private static void testJsonProperties(TipoTest tipo, File fKeystore, File fTruststore, KeyStore truststore, JwtHeaders headers,
			JsonWebKeys jsonWebKeys) throws Exception {
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
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo)) {
			signatureProps.put("rs.security.keystore.type", "jceks");
			signatureProps.put("rs.security.signature.algorithm","HS256");
			signatureProps.put("rs.security.signature.include.key.id","false"); // non e' possibile aggiungerlo
			signatureProps.put("rs.security.signature.include.cert","false"); // non e' possibile aggiungerlo"

			signatureProps.remove("rs.security.signature.include.cert.sha1");
			signatureProps.remove("rs.security.signature.include.cert.sha256");
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
			signatureProps.put("rs.security.keystore.type", "jwk");

			signatureProps.remove("rs.security.signature.include.cert.sha1");
			signatureProps.remove("rs.security.signature.include.cert.sha256");
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			signatureProps.put("rs.security.keystore.type", "jwk");
			signatureProps.put("rs.security.signature.algorithm","HS256");
			signatureProps.put("rs.security.signature.include.key.id","false"); // non e' possibile aggiungerlo
			signatureProps.put("rs.security.signature.include.public.key","false"); // non e' possibile aggiungerlo"

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
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo)) {
			verifySignatureProps.put("rs.security.keystore.type", "jceks");
			verifySignatureProps.put("rs.security.signature.algorithm","HS256");
			verifySignatureProps.put("rs.security.key.password","key123456");
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
			verifySignatureProps.put("rs.security.keystore.type", "jwk");
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			verifySignatureProps.put("rs.security.keystore.type", "jwk");
			verifySignatureProps.put("rs.security.signature.algorithm","HS256");
		}


		System.out.println("\n");





		// **** JSON - !detached -  payloadEncoding ***

		// Signature Json
		JWSOptions options = new JWSOptions(JOSESerialization.JSON);
		JsonSignature jsonSignature = null;
		if(headers==null) {
			jsonSignature = new JsonSignature(signatureProps, options);
		}
		else {
			jsonSignature = new JsonSignature(signatureProps, headers, options);
		}
		String attachSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, attachSign, jsonInput, options, signatureProps, headers);

		// Verifica
		JWTOptions optionsVerify = new JWTOptions(JOSESerialization.JSON);
		JsonVerifySignature jsonVerify = new JsonVerifySignature(verifySignatureProps,optionsVerify);
		verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);

		// Verifica basata sull'header
		if(!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipo) && 
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) && 
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipo) &&
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
				jsonVerify = new JsonVerifySignature(jsonWebKeys, optionsVerify);
			}
			else {
				jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
			}
			verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** JSON - !detached -  !payloadEncoding ***

		// Signature Json - Unencoded Payload Option (RFC 7797)
		options = new JWSOptions(JOSESerialization.JSON);
		options.setPayloadEncoding(false);
		if(headers==null) {
			jsonSignature = new JsonSignature(signatureProps, options);
		}
		else {
			jsonSignature = new JsonSignature(signatureProps, headers, options);
		}
		attachSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo,attachSign, jsonInput, options, signatureProps, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.JSON);
		jsonVerify = new JsonVerifySignature(verifySignatureProps,optionsVerify);
		verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);

		// Verifica basata sull'header
		if(!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipo) && 
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) && 
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipo) &&
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
				jsonVerify = new JsonVerifySignature(jsonWebKeys, optionsVerify);
			}
			else {
				jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
			}
			verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** JSON - detached -  [n.c. payloadEncoding] ***

		// Signature Json - detached
		options = new JWSOptions(JOSESerialization.JSON);
		options.setDetached(true);
		if(headers==null) {
			jsonSignature = new JsonSignature(signatureProps, options);
		}
		else {
			jsonSignature = new JsonSignature(signatureProps, headers, options);
		}
		String detachedSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo,detachedSign, jsonInput, options, signatureProps, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.JSON);
		jsonVerify = new JsonVerifySignature(verifySignatureProps,optionsVerify);
		verifySignature(tipo, false, jsonVerify, detachedSign, jsonInput, options);

		// Verifica basata sull'header
		if(!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipo) && 
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) && 
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipo) &&
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
				jsonVerify = new JsonVerifySignature(jsonWebKeys, optionsVerify);
			}
			else {
				jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
			}
			verifySignature(tipo, true, jsonVerify, detachedSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** COMPACT - !detached -  [n.c. payloadEncoding] ***		

		// Signature Compact
		options = new JWSOptions(JOSESerialization.COMPACT);
		if(headers==null) {
			jsonSignature = new JsonSignature(signatureProps, options);
		}
		else {
			jsonSignature = new JsonSignature(signatureProps, headers, options);
		}
		String compactSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, compactSign, jsonInput, options, signatureProps, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.COMPACT);
		jsonVerify = new JsonVerifySignature(verifySignatureProps, optionsVerify);
		verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);

		// Verifica basata sull'header
		if(!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipo) && 
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) && 
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipo) &&
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
				jsonVerify = new JsonVerifySignature(jsonWebKeys, optionsVerify);
			}
			else {
				jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
			}
			verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** COMPACT - detached -  [n.c. payloadEncoding] ***

		// Signature Compact
		options = new JWSOptions(JOSESerialization.COMPACT);
		options.setDetached(true);
		if(headers==null) {
			jsonSignature = new JsonSignature(signatureProps, options);
		}
		else {
			jsonSignature = new JsonSignature(signatureProps, headers, options);
		}
		compactSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, compactSign, jsonInput, options, signatureProps, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.COMPACT);
		jsonVerify = new JsonVerifySignature(verifySignatureProps, optionsVerify);
		verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);

		// Verifica basata sull'header
		if(!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipo) && 
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) && 
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipo) &&
				!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
				jsonVerify = new JsonVerifySignature(jsonWebKeys, optionsVerify);
			}
			else {
				jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
			}
			verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
		}

		System.out.println("\n\n");
	}


	private static void testJsonKeystore(TipoTest tipo, KeyStore keystore, KeyStore truststore, String alias, String passwordChiavePrivata, JwtHeaders headers) throws Exception {
		// Esempio Signature JSON con altri costruttori

		String jsonInput = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"value2\"\n}";

		System.out.println("\n\n ================ "+tipo+"  ================");
		System.out.println("["+tipo+"]. Example JsonSignature (Costruttore keystore) \n");

		Properties signaturePropsTmp = new Properties(); // per comprendere l'algoritmo
		InputStream is = TestSignature.class.getResourceAsStream("jws.signature.properties");
		signaturePropsTmp.load(is);
		String signatureAlgorithm = signaturePropsTmp.getProperty("rs.security.signature.algorithm");


		System.out.println("\n");


		boolean secretKey = false;
		if(TipoTest.JSON_SIGNATURE_JCEKS_KEYSTORE.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_JCEKS_KEYSTORE_HEADER_CUSTOM.equals(tipo)) {
			secretKey = true;
			signatureAlgorithm = "HS256";
		}


		// **** JSON - !detached -  payloadEncoding ***

		// Signature Json
		JWSOptions options = new JWSOptions(JOSESerialization.JSON);
		JsonSignature jsonSignature = null;
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore.getKeystore(), secretKey, alias, passwordChiavePrivata, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore.getKeystore(), secretKey, alias, passwordChiavePrivata, signatureAlgorithm, headers, options);
		}
		String attachSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, attachSign, jsonInput, options, null, headers);

		// Verifica
		JWTOptions optionsVerify = new JWTOptions(JOSESerialization.JSON);
		JsonVerifySignature jsonVerify = null;
		if(secretKey) {
			jsonVerify = new JsonVerifySignature(truststore.getKeystore(), alias, passwordChiavePrivata, signatureAlgorithm,optionsVerify);
		}else {
			jsonVerify = new JsonVerifySignature(truststore.getKeystore(), alias, signatureAlgorithm,optionsVerify);
		}
		verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
			verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** JSON - !detached -  !payloadEncoding ***

		// Signature Json - Unencoded Payload Option (RFC 7797)
		options = new JWSOptions(JOSESerialization.JSON);
		options.setPayloadEncoding(false);
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore.getKeystore(), secretKey, alias, passwordChiavePrivata, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore.getKeystore(), secretKey, alias, passwordChiavePrivata, signatureAlgorithm, headers, options);
		}
		attachSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo,attachSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.JSON);
		if(secretKey) {
			jsonVerify = new JsonVerifySignature(truststore.getKeystore(), alias, passwordChiavePrivata, signatureAlgorithm,optionsVerify);
		}else {
			jsonVerify = new JsonVerifySignature(truststore.getKeystore(), alias, signatureAlgorithm,optionsVerify);
		}
		verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststore,optionsVerify);
			verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** JSON - detached -  [n.c. payloadEncoding] ***

		// Signature Json - detached
		options = new JWSOptions(JOSESerialization.JSON);
		options.setDetached(true);
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore.getKeystore(), secretKey, alias, passwordChiavePrivata, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore.getKeystore(), secretKey, alias, passwordChiavePrivata, signatureAlgorithm, headers, options);
		}
		String detachedSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo,detachedSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.JSON);
		if(secretKey) {
			jsonVerify = new JsonVerifySignature(truststore.getKeystore(), alias, passwordChiavePrivata, signatureAlgorithm,optionsVerify);
		}else {
			jsonVerify = new JsonVerifySignature(truststore.getKeystore(), alias, signatureAlgorithm,optionsVerify);
		}
		verifySignature(tipo, false, jsonVerify, detachedSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststore,optionsVerify);
			verifySignature(tipo, true, jsonVerify, detachedSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** COMPACT - !detached -  [n.c. payloadEncoding] ***		

		// Signature Compact
		options = new JWSOptions(JOSESerialization.COMPACT);
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore.getKeystore(), secretKey, alias, passwordChiavePrivata, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore.getKeystore(), secretKey, alias, passwordChiavePrivata, signatureAlgorithm, headers, options);
		}
		String compactSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, compactSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.COMPACT);
		if(secretKey) {
			jsonVerify = new JsonVerifySignature(truststore.getKeystore(), alias, passwordChiavePrivata, signatureAlgorithm,optionsVerify);
		}else {
			jsonVerify = new JsonVerifySignature(truststore.getKeystore(), alias, signatureAlgorithm,optionsVerify);
		}
		verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
			verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** COMPACT - detached -  [n.c. payloadEncoding] ***

		// Signature Compact
		options = new JWSOptions(JOSESerialization.COMPACT);
		options.setDetached(true);
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore.getKeystore(), secretKey, alias, passwordChiavePrivata, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore.getKeystore(), secretKey, alias, passwordChiavePrivata, signatureAlgorithm, headers, options);
		}
		compactSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, compactSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.COMPACT);
		if(secretKey) {
			jsonVerify = new JsonVerifySignature(truststore.getKeystore(), alias, passwordChiavePrivata, signatureAlgorithm,optionsVerify);
		}else {
			jsonVerify = new JsonVerifySignature(truststore.getKeystore(), alias, signatureAlgorithm,optionsVerify);
		}
		verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
			verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
		}

		System.out.println("\n\n");

	}

	private static void testJsonJwkKeys(TipoTest tipo, JsonWebKeys keystore, JsonWebKeys truststore, KeyStore truststoreJKS, String alias, JwtHeaders headers) throws Exception {
		// Esempio Signature JSON con altri costruttori

		String jsonInput = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"value2\"\n}";

		System.out.println("\n\n =============== "+tipo+" =================");
		System.out.println("["+tipo+"]. Example JsonSignature (Costruttore keystore) \n");

		Properties signaturePropsTmp = new Properties();
		InputStream is = TestSignature.class.getResourceAsStream("jws.signature.properties");
		signaturePropsTmp.load(is);
		String signatureAlgorithm = signaturePropsTmp.getProperty("rs.security.signature.algorithm"); // per comprendere l'algoritmo

		boolean secretKey = false;
		if(TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEYS.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEYS_HEADER_CUSTOM.equals(tipo)) {
			secretKey = true;
			signatureAlgorithm = "HS256";
		}

		System.out.println("\n");



		// **** JSON - !detached -  payloadEncoding ***

		// Signature Json
		JWSOptions options = new JWSOptions(JOSESerialization.JSON);
		JsonSignature jsonSignature = null;
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore, secretKey, alias, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore, secretKey, alias, signatureAlgorithm, headers, options);
		}
		String attachSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, attachSign, jsonInput, options, null, headers);

		// Verifica
		JWTOptions optionsVerify = new JWTOptions(JOSESerialization.JSON);
		JsonVerifySignature jsonVerify = new JsonVerifySignature(truststore, secretKey, alias, signatureAlgorithm,optionsVerify);
		verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststoreJKS, optionsVerify);
			verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** JSON - !detached -  !payloadEncoding ***

		// Signature Json - Unencoded Payload Option (RFC 7797)
		options = new JWSOptions(JOSESerialization.JSON);
		options.setPayloadEncoding(false);
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore, secretKey, alias, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore, secretKey, alias, signatureAlgorithm, headers, options);
		}
		attachSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo,attachSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.JSON);
		jsonVerify = new JsonVerifySignature(truststore, secretKey, alias, signatureAlgorithm,optionsVerify);
		verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststoreJKS,optionsVerify);
			verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** JSON - detached -  [n.c. payloadEncoding] ***

		// Signature Json - detached
		options = new JWSOptions(JOSESerialization.JSON);
		options.setDetached(true);
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore, secretKey, alias, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore, secretKey, alias, signatureAlgorithm, headers, options);
		}
		String detachedSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo,detachedSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.JSON);
		jsonVerify = new JsonVerifySignature(truststore, secretKey, alias, signatureAlgorithm,optionsVerify);
		verifySignature(tipo, false, jsonVerify, detachedSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststoreJKS,optionsVerify);
			verifySignature(tipo, true, jsonVerify, detachedSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** COMPACT - !detached -  [n.c. payloadEncoding] ***		

		// Signature Compact
		options = new JWSOptions(JOSESerialization.COMPACT);
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore, secretKey, alias, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore, secretKey, alias, signatureAlgorithm, headers, options);
		}
		String compactSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, compactSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.COMPACT);
		jsonVerify = new JsonVerifySignature(truststore, secretKey, alias, signatureAlgorithm, optionsVerify);
		verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststoreJKS, optionsVerify);
			verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** COMPACT - detached -  [n.c. payloadEncoding] ***

		// Signature Compact
		options = new JWSOptions(JOSESerialization.COMPACT);
		options.setDetached(true);
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore, secretKey, alias, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore, secretKey, alias, signatureAlgorithm, headers, options);
		}
		compactSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, compactSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.COMPACT);
		jsonVerify = new JsonVerifySignature(truststore, secretKey, alias, signatureAlgorithm, optionsVerify);
		verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststoreJKS, optionsVerify);
			verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
		}

		System.out.println("\n\n");

	}


	private static void testJsonJwkKey(TipoTest tipo, JsonWebKey keystore, JsonWebKey truststore, KeyStore truststoreJKS, JwtHeaders headers) throws Exception {
		// Esempio Signature JSON con altri costruttori

		String jsonInput = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"value2\"\n}";

		System.out.println("\n\n =============== "+tipo+" =================");
		System.out.println("["+tipo+"]. Example JsonSignature (Costruttore keystore) \n");

		Properties signaturePropsTmp = new Properties();
		InputStream is = TestSignature.class.getResourceAsStream("jws.signature.properties");
		signaturePropsTmp.load(is);
		String signatureAlgorithm = signaturePropsTmp.getProperty("rs.security.signature.algorithm"); // per comprendere l'algoritmo

		boolean secretKey = false;
		if(TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEY.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEY_HEADER_CUSTOM.equals(tipo)) {
			secretKey = true;
			signatureAlgorithm = "HS256";
		}

		System.out.println("\n");


		// **** JSON - !detached -  payloadEncoding ***

		// Signature Json
		JWSOptions options = new JWSOptions(JOSESerialization.JSON);
		JsonSignature jsonSignature = null;
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore, secretKey, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore, secretKey, signatureAlgorithm, headers, options);
		}
		String attachSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, attachSign, jsonInput, options, null, headers);

		// Verifica
		JWTOptions optionsVerify = new JWTOptions(JOSESerialization.JSON);
		JsonVerifySignature jsonVerify = new JsonVerifySignature(truststore, secretKey, signatureAlgorithm,optionsVerify);
		verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststoreJKS, optionsVerify);
			verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** JSON - !detached -  !payloadEncoding ***

		// Signature Json - Unencoded Payload Option (RFC 7797)
		options = new JWSOptions(JOSESerialization.JSON);
		options.setPayloadEncoding(false);
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore, secretKey, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore, secretKey, signatureAlgorithm, headers, options);
		}
		attachSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo,attachSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.JSON);
		jsonVerify = new JsonVerifySignature(truststore, secretKey, signatureAlgorithm,optionsVerify);
		verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststoreJKS,optionsVerify);
			verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** JSON - detached -  [n.c. payloadEncoding] ***

		// Signature Json - detached
		options = new JWSOptions(JOSESerialization.JSON);
		options.setDetached(true);
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore, secretKey, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore, secretKey, signatureAlgorithm, headers, options);
		}
		String detachedSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo,detachedSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.JSON);
		jsonVerify = new JsonVerifySignature(truststore, secretKey, signatureAlgorithm,optionsVerify);
		verifySignature(tipo, false, jsonVerify, detachedSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststoreJKS,optionsVerify);
			verifySignature(tipo, true, jsonVerify, detachedSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** COMPACT - !detached -  [n.c. payloadEncoding] ***		

		// Signature Compact
		options = new JWSOptions(JOSESerialization.COMPACT);
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore, secretKey, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore, secretKey, signatureAlgorithm, headers, options);
		}
		String compactSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, compactSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.COMPACT);
		jsonVerify = new JsonVerifySignature(truststore, secretKey, signatureAlgorithm, optionsVerify);
		verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststoreJKS, optionsVerify);
			verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
		}

		System.out.println("\n\n");



		// **** COMPACT - detached -  [n.c. payloadEncoding] ***

		// Signature Compact
		options = new JWSOptions(JOSESerialization.COMPACT);
		options.setDetached(true);
		if(headers==null) {
			jsonSignature = new JsonSignature(keystore, secretKey, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(keystore, secretKey, signatureAlgorithm, headers, options);
		}
		compactSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, compactSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.COMPACT);
		jsonVerify = new JsonVerifySignature(truststore, secretKey, signatureAlgorithm, optionsVerify);
		verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststoreJKS, optionsVerify);
			verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
		}

		System.out.println("\n\n");

	}


	private static void verifySignatureBuild(TipoTest tipo, String signature, String jsonInput, JWSOptions options, 
			Properties signatureProps, JwtHeaders headers) throws Exception {

		List<String> checkHeaders = getHeaders(signatureProps, headers);

		System.out.println("["+tipo+"] "+options.getSerialization().name()+" Signature (detached:"+options.isDetached()+" payloadEncoding:"+options.isPayloadEncoding()+") Signed: \n"+signature);
		if(JOSESerialization.JSON.equals(options.getSerialization())) {
			if(options.isDetached()) {
				if(signature.contains("\"payload\":")) {
					throw new Exception("Found payload? (detached mode)");
				}
			}
			else {
				if(signature.contains("\"payload\":")==false) {
					throw new Exception("Not Found payload");
				}
				if(options.isPayloadEncoding()) {
					if(signature.contains(jsonInput)) {
						throw new Exception("Payload not encoded (payload encoding enabled)");
					}
				}
				else {
					if(!signature.contains(jsonInput)) {
						throw new Exception("Payload encoded (payload encoding disabled)");
					}
				}
			}

			int indexOf = signature.indexOf("\"protected\":\"");
			String protectedHeaders = signature.substring(indexOf+"\"protected\":\"".length());
			protectedHeaders = protectedHeaders.substring(0, protectedHeaders.indexOf("\""));
			String hdrDecoded = new String (Base64Utilities.decode(protectedHeaders));
			System.out.println("["+tipo+"] "+options.getSerialization().name()+" Signature (detached:"+options.isDetached()+" payloadEncoding:"+options.isPayloadEncoding()+") protected base64 decoded: "+hdrDecoded);
			if(checkHeaders!=null && !checkHeaders.isEmpty()) {
				for (String hdr : checkHeaders) {
					System.out.println("["+tipo+"] "+options.getSerialization().name()+" Verifico presenza '"+hdr+"'");
					String hdrName = "\""+hdr+"\":";
					if(!hdrDecoded.contains(hdrName)) {
						throw new Exception("'"+hdr+"' not found in headers");
					}
				}
			}
		}
		else {
			if(signature.contains(".")==false) {
				throw new Exception("Expected format with '.' separator");
			}
			String [] tmp = signature.split("\\.");
			if(tmp==null) {
				throw new Exception("Expected format with '.' separator");
			}
			if(options.isDetached()) {
				if(signature.contains("..")==false) {
					throw new Exception("Expected format with '..' separator (detached)");
				}
			}
			int length = 3;
			if(tmp.length!=length) {
				throw new Exception("Expected format with 3 base64 info ('.' separator");
			}
			for (int i = 0; i < tmp.length; i++) {

				String part = tmp[i];
				if(i==1) {
					if(options.isDetached()) {
						if(!(part==null || "".equals(part))) {
							throw new Exception("Found payload? (detached mode)");
						}
						continue;
					}
				}
				if(i==1) {
					if(options.isPayloadEncoding()) {
						byte [] decoded = Base64Utilities.decode(part);
						String jsonDecoded = new String(decoded);
						if(!jsonDecoded.equals(jsonInput)) {
							throw new Exception("Found different payload after decoded (payload encoding enabled)");
						}
					}
					else {
						boolean decodeError = false;
						try {
							Base64Utilities.decode(part);
						}catch(Exception e) {
							decodeError = true;
						}
						if(!decodeError) {
							throw new Exception("Position '"+i+"' base64 encoded, expected payload encoding disabled");
						}
						if(!part.equals(jsonInput)) {
							throw new Exception("Found different payload (payload encoding disabled)");
						}
					}
				}
				else {
					try {
						Base64Utilities.decode(part);
					}catch(Exception e) {
						throw new Exception("Position '"+i+"' uncorrect base64 encoded:"+e.getMessage(),e);
					}
				}

			}

			String hdrDecoded = new String (Base64Utilities.decode(signature.split("\\.")[0]));
			System.out.println("["+tipo+"] "+options.getSerialization().name()+" Signature (detached:"+options.isDetached()+" payloadEncoding:"+options.isPayloadEncoding()+") HDR base64 decoded: "+hdrDecoded);
			if(checkHeaders!=null && !checkHeaders.isEmpty()) {
				for (String hdr : checkHeaders) {
					System.out.println("["+tipo+"] "+options.getSerialization().name()+" Verifico presenza '"+hdr+"'");
					String hdrName = "\""+hdr+"\":";
					if(!hdrDecoded.contains(hdrName)) {
						throw new Exception("'"+hdr+"' not found in headers");
					}
				}
			}
		}
	}

	private static void verifySignature(TipoTest tipo, boolean useHdrsForValidation, JsonVerifySignature verify, String signature, String jsonInput, JWSOptions options) throws Exception {
		boolean result = false;
		if(options.isDetached()) {
			result = verify.verifyDetached(signature, jsonInput);
		}
		else {
			result = verify.verify(signature);
		}
		System.out.println("["+tipo+"] "+options.getSerialization().name()+" Verify Signature (use-hdrs: "+useHdrsForValidation+" detached:"+options.isDetached()+" payloadEncoding:"+options.isPayloadEncoding()+")  result:"+result+" payload: "+verify.getDecodedPayload());
		if(!result) {
			throw new Exception("Signed invalid");
		}
		if(verify.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}

		// test per corrompere il contenuto
		if(options.isDetached()) {

			// body
			String jsonInputCorrotto = jsonInput.replace("value1", "value1CORROTTO");
			if(verify.verifyDetached(signature, jsonInputCorrotto)!=false) {
				throw new Exception("Expected validation error (bodyCorrupted: "+jsonInputCorrotto+")");
			}

			// signature
			String signatureCorrotta = null;
			if(JOSESerialization.JSON.equals(options.getSerialization())) {
				signatureCorrotta = signature.replace("signature\":\"", "signature\":\"CORROTTO");
			}
			else {
				signatureCorrotta = signature.replaceFirst("\\.", "TEMPLATECORROTTO"); // per non corrompere il body
				signatureCorrotta = signatureCorrotta.replaceFirst("\\.", ".CORROTTO");
				signatureCorrotta = signatureCorrotta.replaceFirst("TEMPLATECORROTTO","."); // ripristino il body
			}
			if(verify.verifyDetached(signatureCorrotta, jsonInput)!=false) {
				throw new Exception("Expected validation error (signatureCorrupted: "+signatureCorrotta+")");
			}
		}
		else {

			// body
			String signatureCorrottaParteBody = null;
			if(JOSESerialization.JSON.equals(options.getSerialization())) {
				if(options.isPayloadEncoding()) {
					signatureCorrottaParteBody = signature.replace("payload\":\"", "payload\":\"CORROTTO");
				}
				else {
					signatureCorrottaParteBody = signature.replace("value1", "value1differente");
				}
			}
			else {
				if(options.isPayloadEncoding()) {
					signatureCorrottaParteBody = signature.replaceFirst("\\.", ".CORROTTO");
				}
				else {
					signatureCorrottaParteBody = signature.replace("value1", "value1differente");
				}
			}
			if(verify.verify(signatureCorrottaParteBody)!=false) {
				throw new Exception("Expected validation error (bodyCorrupted: "+signatureCorrottaParteBody+")");
			}

			// signature
			String signatureCorrotta = null;
			if(JOSESerialization.JSON.equals(options.getSerialization())) {
				signatureCorrotta = signature.replace("signature\":\"", "signature\":\"CORROTTO");
			}
			else {
				signatureCorrotta = signature.replaceFirst("\\.", "TEMPLATECORROTTO"); // per non corrompere il body
				signatureCorrotta = signatureCorrotta.replaceFirst("\\.", ".CORROTTO");
				signatureCorrotta = signatureCorrotta.replaceFirst("TEMPLATECORROTTO","."); // ripristino il body
			}
			if(verify.verify(signatureCorrotta)!=false) {
				throw new Exception("Expected validation error (signatureCorrupted: "+signatureCorrotta+")");
			}
		}

	}

	private static List<String> getHeaders(Properties signatureProps, JwtHeaders headers){
		List<String> list = new ArrayList<>();

		if(signatureProps!=null) {

			String kid = signatureProps.getProperty("rs.security.signature.include.key.id");
			if("true".equals(kid)) {
				list.add(JwtHeaders.JWT_HDR_KID);
			}

			String tipo = signatureProps.getProperty("rs.security.keystore.type");
			if("jwk".equals(tipo)) {
				String jwk = signatureProps.getProperty("rs.security.signature.include.public.key");
				if("true".equals(jwk)) {
					list.add(JwtHeaders.JWT_HDR_JWK);
				}
			}
			else {
				String x5c = signatureProps.getProperty("rs.security.signature.include.cert");
				if("true".equals(x5c)) {
					list.add(JwtHeaders.JWT_HDR_X5C);
				}
				String x5t = signatureProps.getProperty("rs.security.signature.include.cert.sha1");
				if("true".equals(x5t)) {
					list.add(JwtHeaders.JWT_HDR_X5T);
				}
				String x5t_256 = signatureProps.getProperty("rs.security.signature.include.cert.sha256");
				if("true".equals(x5t_256)) {
					list.add(JwtHeaders.JWT_HDR_X5t_S256);
				}	
			}

		}

		if(headers!=null) {
			List<String> listHDR = headers.headers();
			if(listHDR!=null && !listHDR.isEmpty()) {
				list.addAll(listHDR);
			}
		}

		return list;
	}

	public enum TipoTest {

		JAVA_SIGNATURE, 
		PKCS7_SIGNATURE,
		XML_SIGNATURE,
		JSON_SIGNATURE_PROPERTIES_JKS,
		JSON_SIGNATURE_PROPERTIES_JCEKS,
		JSON_SIGNATURE_PROPERTIES_JWK,
		JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC,
		JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM_KID_ONLY,
		JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY,
		JSON_SIGNATURE_JKS_KEYSTORE,
		JSON_SIGNATURE_JKS_KEYSTORE_HEADER_CUSTOM,
		JSON_SIGNATURE_JCEKS_KEYSTORE,
		JSON_SIGNATURE_JCEKS_KEYSTORE_HEADER_CUSTOM,
		JSON_SIGNATURE_JWK_KEYS,
		JSON_SIGNATURE_JWK_KEYS_HEADER_CUSTOM,
		JSON_SIGNATURE_JWK_KEY,
		JSON_SIGNATURE_JWK_KEY_HEADER_CUSTOM,
		JSON_SIGNATURE_JWK_SYMMETRIC_KEYS,
		JSON_SIGNATURE_JWK_SYMMETRIC_KEYS_HEADER_CUSTOM,
		JSON_SIGNATURE_JWK_SYMMETRIC_KEY,
		JSON_SIGNATURE_JWK_SYMMETRIC_KEY_HEADER_CUSTOM

	}
}
