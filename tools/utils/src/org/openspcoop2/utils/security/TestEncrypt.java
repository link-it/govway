/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.crypto.SecretKey;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.logging.log4j.Level;
import org.apache.xml.security.encryption.XMLCipher;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.JWK;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.test.KeystoreTest;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**	
 * TestEncrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestEncrypt {

	/* !!NOTA!!: 
	 * l'esecuzione dei test P11 richiedono la configurazione descritta in org/openspcoop2/utils/certificate/hsm/HSM.example 
	 * Deve inoltre essere impostata la variabile di sistema (utilizzando path assoluti!!!):
	 * export SOFTHSM2_CONF=DIRECTORY_SCELTA_FASE_INSTALLAZIONE/lib/softhsm/softhsm2.conf
	 */
	
	public static void main(String[] args) throws Exception {
		
		/*
		TODO
		*/
		boolean runPKCS11SecretTest = false;
		
		TipoTest tipoTest = null;
		if(args!=null && args.length>0) {
			tipoTest = TipoTest.valueOf(args[0]);
		}
		
		boolean useP11asTrustStore = false;
		if(args!=null && args.length>1) {
			useP11asTrustStore = Boolean.valueOf(args[1]);
		}
		
		InputStream isKeystoreJKS = null;
		File fKeystoreJKS = null;
		InputStream isKeystoreP12 = null;
		File fKeystoreP12 = null;
		InputStream isTruststore = null;
		File fTruststore = null;

		File fKeystoreP11 = null;
		File fTruststoreP11 = null;
		
		InputStream isKeystoreJCEKS = null;
		File fKeystoreJCEKS = null;
		
		File fCertX509 = null;
		
		File fCertX509_P11 = null;
		
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
			System.out.println("env SOFTHSM2_CONF: "+System.getenv("SOFTHSM2_CONF"));
			
			LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ALL);
			Logger log = LoggerWrapperFactory.getLogger(TestEncrypt.class);
			
			isKeystoreJKS = TestEncrypt.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jks");
			fKeystoreJKS = File.createTempFile("keystore", "jks");
			FileSystemUtilities.writeFile(fKeystoreJKS, Utilities.getAsByteArray(isKeystoreJKS));
			
			isKeystoreP12 = TestEncrypt.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.p12");
			fKeystoreP12 = File.createTempFile("keystore", "p12");
			FileSystemUtilities.writeFile(fKeystoreP12, Utilities.getAsByteArray(isKeystoreP12));
			
			isTruststore = TestEncrypt.class.getResourceAsStream("/org/openspcoop2/utils/security/truststore_example.jks");
			fTruststore = File.createTempFile("truststore", "jks");
			FileSystemUtilities.writeFile(fTruststore, Utilities.getAsByteArray(isTruststore));

			fKeystoreP11 = File.createTempFile("keystore_hsm", ".properties");
			FileSystemUtilities.writeFile(fKeystoreP11, Utilities.getAsByteArray(TestEncrypt.class.getResourceAsStream(KeystoreTest.PREFIX+"govway_test_hsm.properties")) );
			
			fTruststoreP11 = File.createTempFile("truststore_hsm", ".jks");
			
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
			
			jwks_symmetric_isKeystore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_symmetricKey_example.jwks");
			jwks_symmetric_fKeystore = File.createTempFile("keystore", ".jwks");
			FileSystemUtilities.writeFile(jwks_symmetric_fKeystore, Utilities.getAsByteArray(jwks_symmetric_isKeystore));
			
			jwk_symmetric_isKeystore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_symmetricKey_example.jwk");
			jwk_symmetric_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwk_symmetric_fKeystore, Utilities.getAsByteArray(jwk_symmetric_isKeystore));
			
			String passwordChiavePrivata = "key123456";
			String passwordStore = "123456";
			String alias = "openspcoop";
			HashMap<String, String> keystore_mapAliasPassword = new HashMap<>();
			keystore_mapAliasPassword.put(alias, passwordChiavePrivata);
			
			KeyStore keystoreJKS = new KeyStore(fKeystoreJKS.getAbsolutePath(), "jks", passwordStore);
			if(!"jks".equalsIgnoreCase(keystoreJKS.getKeystoreType())) {
				throw new Exception("Atteso tipo JKS, trovato '"+keystoreJKS.getKeystoreType()+"'");
			}
			KeyStore keystoreP12 = new KeyStore(fKeystoreP12.getAbsolutePath(), "pkcs12", passwordStore);
			if(!"pkcs12".equalsIgnoreCase(keystoreP12.getKeystoreType())) {
				throw new Exception("Atteso tipo PKCS12, trovato '"+keystoreP12.getKeystoreType()+"'");
			}
			KeyStore truststore = new KeyStore(fTruststore.getAbsolutePath(), passwordStore);
			if(!"jks".equalsIgnoreCase(truststore.getKeystoreType())) {
				throw new Exception("Atteso tipo JKS, trovato '"+truststore.getKeystoreType()+"'");
			}

			String aliasP11 = KeystoreTest.ALIAS_PKCS11_SERVER2;
			HashMap<String, String> keystoreP11_mapAliasPassword = new HashMap<>();
			keystoreP11_mapAliasPassword.put(aliasP11, passwordChiavePrivata);
			
			boolean uniqueProviderInstance = true;			
			HSMManager.init(fKeystoreP11, true, log, true);
			HSMManager hsmManager = HSMManager.getInstance();
			hsmManager.providerInit(log, uniqueProviderInstance);
			System.out.println("PKCS11 Keystore registered: "+hsmManager.getKeystoreTypes());
			KeyStore keystoreP11 = hsmManager.getKeystore(KeystoreTest.PKCS11_SERVER);
			if(!"pkcs11".equalsIgnoreCase(keystoreP11.getKeystoreType())) {
				throw new Exception("Atteso tipo PKCS11, trovato '"+keystoreP12.getKeystoreType()+"'");
			}
			KeyStore truststoreP11 = null;
			if(useP11asTrustStore) {
				truststoreP11 = hsmManager.getKeystore(KeystoreTest.PKCS11_SERVER);
				if(!"pkcs11".equalsIgnoreCase(truststoreP11.getKeystoreType())) {
					throw new Exception("Atteso tipo PKCS11, trovato '"+truststoreP11.getKeystoreType()+"'");
				}
			}
			else {
				java.security.KeyStore tP11 = java.security.KeyStore.getInstance("JKS");
				tP11.load(null); // inizializza il keystore
				tP11.setCertificateEntry(KeystoreTest.ALIAS_PKCS11_CLIENT1, 
						hsmManager.getKeystore(KeystoreTest.PKCS11_CLIENT1).getCertificate(KeystoreTest.ALIAS_PKCS11_CLIENT1));
				tP11.setCertificateEntry(KeystoreTest.ALIAS_PKCS11_CLIENT2, 
						hsmManager.getKeystore(KeystoreTest.PKCS11_CLIENT2).getCertificate(KeystoreTest.ALIAS_PKCS11_CLIENT2));
				tP11.setCertificateEntry(KeystoreTest.ALIAS_PKCS11_SERVER, 
						hsmManager.getKeystore(KeystoreTest.PKCS11_SERVER).getCertificate(KeystoreTest.ALIAS_PKCS11_SERVER));
				tP11.setCertificateEntry(KeystoreTest.ALIAS_PKCS11_SERVER2, 
						hsmManager.getKeystore(KeystoreTest.PKCS11_SERVER).getCertificate(KeystoreTest.ALIAS_PKCS11_SERVER2));
				truststoreP11 = new KeyStore(tP11);
				try(FileOutputStream fout = new FileOutputStream(fTruststoreP11)){
					tP11.store(fout, passwordStore.toCharArray());
					fout.flush();
				}
				if(!"jks".equalsIgnoreCase(truststoreP11.getKeystoreType())) {
					throw new Exception("Atteso tipo JKS, trovato '"+truststoreP11.getKeystoreType()+"'");
				}
			}
			
			KeyStore keystoreJCEKS = new KeyStore(fKeystoreJCEKS.getAbsolutePath(), "JCEKS", passwordStore);
			if(!"JCEKS".equalsIgnoreCase(keystoreJCEKS.getKeystoreType())) {
				throw new Exception("Atteso tipo JCEKS, trovato '"+keystoreJCEKS.getKeystoreType()+"'");
			}
			
			KeyStore keystoreSecretP11 = hsmManager.getKeystore(KeystoreTest.PKCS11_CLIENT1);
			if(!"pkcs11".equalsIgnoreCase(keystoreSecretP11.getKeystoreType())) {
				throw new Exception("Atteso tipo PKCS11, trovato '"+keystoreSecretP11.getKeystoreType()+"'");
			}
			KeyStore truststoreSecretP11 = null;
			if(useP11asTrustStore) {
				truststoreSecretP11 = hsmManager.getKeystore(KeystoreTest.PKCS11_CLIENT1);
				if(!"pkcs11".equalsIgnoreCase(truststoreSecretP11.getKeystoreType())) {
					throw new Exception("Atteso tipo PKCS11, trovato '"+truststoreSecretP11.getKeystoreType()+"'");
				}
			}
			
			String aliasSecretP11 = KeystoreTest.ALIAS_PKCS11_CLIENT_SYMMETRIC;
			HashMap<String, String> keystoreSecretP11_mapAliasPassword = new HashMap<>();
			keystoreSecretP11_mapAliasPassword.put(aliasSecretP11, passwordChiavePrivata);
			
			fCertX509 =  File.createTempFile("cert", ".cer");
			FileSystemUtilities.writeFile(fCertX509, truststore.getCertificate(alias).getEncoded());
			
			fCertX509_P11 =  File.createTempFile("certP11", ".cer");
			FileSystemUtilities.writeFile(fCertX509_P11, truststoreP11.getCertificate(aliasP11).getEncoded());
			
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
			
			
			
			// Esempio Encrypt Java 
			
			if(tipoTest==null || TipoTest.JAVA_ENCRYPT_JKS.equals(tipoTest)) {
				testJava(TipoTest.JAVA_ENCRYPT_JKS, useP11asTrustStore, 
						keystoreJKS, truststore, alias, passwordChiavePrivata);
			}
			if(tipoTest==null || TipoTest.JAVA_ENCRYPT_PKCS12.equals(tipoTest)) {
				testJava(TipoTest.JAVA_ENCRYPT_PKCS12, useP11asTrustStore, 
						 keystoreP12, truststore, alias, passwordChiavePrivata);
			}
			if(tipoTest==null || TipoTest.JAVA_ENCRYPT_PKCS11.equals(tipoTest)) {
				testJava(TipoTest.JAVA_ENCRYPT_PKCS11, useP11asTrustStore, 
						 keystoreP11, truststoreP11, aliasP11, passwordChiavePrivata);
			}
			
			
			
			// Esempio Encrypt Xml 
			
			if(tipoTest==null || TipoTest.XML_ENCRYPT_JKS.equals(tipoTest)) {
				testXmlEncrypt(TipoTest.XML_ENCRYPT_JKS, useP11asTrustStore, 
						 keystoreJKS, alias, truststore, keystoreJCEKS, alias, passwordChiavePrivata);
			}
			if(tipoTest==null || TipoTest.XML_ENCRYPT_PKCS12.equals(tipoTest)) {
				testXmlEncrypt(TipoTest.XML_ENCRYPT_PKCS12, useP11asTrustStore, 
						 keystoreP12, alias, truststore, keystoreJCEKS, alias, passwordChiavePrivata);
			}
			if(tipoTest==null || TipoTest.XML_ENCRYPT_PKCS11.equals(tipoTest)) {
				testXmlEncrypt(TipoTest.XML_ENCRYPT_PKCS11, useP11asTrustStore, 
						 keystoreP11, aliasP11, truststoreP11, keystoreJCEKS, alias, passwordChiavePrivata);
			}
			
			
			
			// Esempio Encrypt JSON
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JKS.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS, useP11asTrustStore, 
						fKeystoreJKS, null, null, fTruststore, null,
						truststore, keystoreJKS, keystore_mapAliasPassword, null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM, useP11asTrustStore, 
						fKeystoreJKS, null, null, fTruststore, jwtHeader,
						truststore, keystoreJKS, keystore_mapAliasPassword, null);
				jwtHeader.setX509Url(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM_KID_ONLY.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM_KID_ONLY, useP11asTrustStore, 
						fKeystoreJKS, null, null, fTruststore, jwtHeader,
						truststore, keystoreJKS, keystore_mapAliasPassword, null);
			}
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12, useP11asTrustStore, 
						fKeystoreP12, null, null, fTruststore, null,
						truststore, keystoreP12, keystore_mapAliasPassword, null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12_HEADER_CUSTOM, useP11asTrustStore, 
						fKeystoreP12, null, null, fTruststore, jwtHeader,
						truststore, keystoreP12, keystore_mapAliasPassword, null);
				jwtHeader.setX509Url(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12_HEADER_CUSTOM_KID_ONLY.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12_HEADER_CUSTOM_KID_ONLY, useP11asTrustStore, 
						fKeystoreP12, null, null, fTruststore, jwtHeader,
						truststore, keystoreP12, keystore_mapAliasPassword, null);
			}
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11, useP11asTrustStore, 
						null, keystoreP11, aliasP11, fTruststoreP11, null,
						truststoreP11, keystoreP11, keystoreP11_mapAliasPassword, null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setX509Url(new URI("file://"+fCertX509_P11.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM, useP11asTrustStore, 
						null, keystoreP11, aliasP11, fTruststoreP11, jwtHeader,
						truststoreP11, keystoreP11, keystoreP11_mapAliasPassword, null);
				jwtHeader.setX509Url(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM_KID_ONLY.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM_KID_ONLY, useP11asTrustStore, 
						null, keystoreP11, aliasP11, fTruststoreP11, jwtHeader,
						truststoreP11, keystoreP11, keystoreP11_mapAliasPassword, null);
			}
			
			

			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS, useP11asTrustStore, 
						fKeystoreJCEKS, null, null, fKeystoreJCEKS, null,
						null, null, null, null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS_HEADER_CUSTOM, useP11asTrustStore, 
						fKeystoreJCEKS, null, null, fKeystoreJCEKS, jwtHeader,
						null, null, null, null);
				jwtHeader.setX509Url(null);
			}
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET.equals(tipoTest)) {
				if(runPKCS11SecretTest) {
					testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET, useP11asTrustStore, 
							null, keystoreSecretP11, aliasSecretP11, fTruststoreP11, null,
							truststoreSecretP11, keystoreSecretP11, keystoreSecretP11_mapAliasPassword, null);
				}
				else {
					System.out.println("Test ["+TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET+"] disabilitato");
				}
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM.equals(tipoTest)) {
				if(runPKCS11SecretTest) {
					jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
					testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM, useP11asTrustStore, 
							null, keystoreSecretP11, aliasSecretP11, fTruststoreP11, jwtHeader,
							truststoreSecretP11, keystoreSecretP11, keystoreSecretP11_mapAliasPassword, null);
					jwtHeader.setX509Url(null);
				}
				else {
					System.out.println("Test ["+TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM+"] disabilitato");
				}
			}
			
			
			

			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JWK.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK, useP11asTrustStore, 
						jwks_fKeystore, null, null, jwks_fTruststore, null,
						null,null,null,jwks_keystore);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM, useP11asTrustStore, 
						jwks_fKeystore, null, null, jwks_fTruststore, jwtHeader,
						null,null,null,jwks_keystore);
				jwtHeader.setJwkUrl(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY, useP11asTrustStore, 
						jwks_fKeystore, null, null, jwks_fTruststore, jwtHeader,
						null,null,null,jwks_keystore);
			}
			
			
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC, useP11asTrustStore, 
						jwks_symmetric_fKeystore, null, null,  jwks_symmetric_fKeystore, null,
						null,null,null,null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setJwkUrl(new URI("file://"+jwks_symmetric_fKeystore.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM, useP11asTrustStore, 
						jwks_symmetric_fKeystore, null, null,  jwks_symmetric_fKeystore, jwtHeader,
						null,null,null,null);
				jwtHeader.setJwkUrl(null);
			}
			

			
			
			

			
			
			// Esempio Signature JSON con altri costruttori
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_JKS_KEYSTORE.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystoreJKS.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(alias);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_JKS_KEYSTORE, useP11asTrustStore, 
						keystoreJKS, truststore, alias, passwordChiavePrivata, jwtHeader,
						keystore_mapAliasPassword);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_JKS_KEYSTORE_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystoreJKS.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				jwtHeader.setKid(alias);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_JKS_KEYSTORE_HEADER_CUSTOM, useP11asTrustStore, 
						keystoreJKS, truststore, alias, passwordChiavePrivata, jwtHeader,
						keystore_mapAliasPassword);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
			}
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PKCS12_KEYSTORE.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystoreP12.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(alias);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_PKCS12_KEYSTORE, useP11asTrustStore, 
						keystoreP12, truststore, alias, passwordChiavePrivata, jwtHeader,
						keystore_mapAliasPassword);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PKCS12_KEYSTORE_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystoreP12.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				jwtHeader.setKid(alias);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_PKCS12_KEYSTORE_HEADER_CUSTOM, useP11asTrustStore, 
						keystoreP12, truststore, alias, passwordChiavePrivata, jwtHeader,
						keystore_mapAliasPassword);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
			}
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystoreP11.getCertificate(aliasP11));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(aliasP11);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE, useP11asTrustStore, 
						keystoreP11, truststoreP11, aliasP11, passwordChiavePrivata, jwtHeader,
						keystoreP11_mapAliasPassword);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystoreP11.getCertificate(aliasP11));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509_P11.getAbsolutePath()));
				jwtHeader.setKid(aliasP11);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE_HEADER_CUSTOM, useP11asTrustStore, 
						keystoreP11, truststoreP11, aliasP11, passwordChiavePrivata, jwtHeader,
						keystoreP11_mapAliasPassword);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
			}
			
			
		
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_JKS_KEYSTORE_JCE.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystoreJKS.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(alias);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_JKS_KEYSTORE_JCE, useP11asTrustStore, 
						keystoreJCEKS, alias, passwordChiavePrivata, jwtHeader);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_JKS_KEYSTORE_JCE_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystoreJKS.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				jwtHeader.setKid(alias);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_JKS_KEYSTORE_JCE_HEADER_CUSTOM, useP11asTrustStore, 
						keystoreJCEKS, alias, passwordChiavePrivata, jwtHeader);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
			}
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PKCS12_KEYSTORE_JCE.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystoreP12.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(alias);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_PKCS12_KEYSTORE_JCE, useP11asTrustStore, 
						keystoreJCEKS, alias, passwordChiavePrivata, jwtHeader);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PKCS12_KEYSTORE_JCE_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystoreP12.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				jwtHeader.setKid(alias);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_PKCS12_KEYSTORE_JCE_HEADER_CUSTOM, useP11asTrustStore, 
						keystoreJCEKS, alias, passwordChiavePrivata, jwtHeader);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
			}
			


			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE_JCE.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystoreP11.getCertificate(aliasP11));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(aliasP11);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE_JCE, useP11asTrustStore, 
						keystoreJCEKS, alias, passwordChiavePrivata, jwtHeader);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE_JCE_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.addX509cert((X509Certificate)keystoreP11.getCertificate(aliasP11));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509_P11.getAbsolutePath()));
				jwtHeader.setKid(aliasP11);
				
				testJsonKeystore(TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE_JCE_HEADER_CUSTOM, useP11asTrustStore, 
						keystoreJCEKS, alias, passwordChiavePrivata, jwtHeader);
				
				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
			}
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_JWK_KEYS.equals(tipoTest)) {
				
				jwtHeader.setJwKey(jwks_keystore, alias);
				jwtHeader.setKid(alias);
				
				testJsonJwkKeys(TipoTest.JSON_ENCRYPT_JWK_KEYS, useP11asTrustStore, 
						 jwks_keystore, jwks_truststore, alias, jwtHeader);
				
				jwtHeader.setJwKey(null);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_JWK_KEYS_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));
				
				testJsonJwkKeys(TipoTest.JSON_ENCRYPT_JWK_KEYS_HEADER_CUSTOM, useP11asTrustStore, 
						 jwks_keystore, jwks_truststore, alias, jwtHeader);
				
				jwtHeader.setJwkUrl(null);
				jwtHeader.setKid(null);
			}
			
			
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_JWK_KEY.equals(tipoTest)) {
				
				jwtHeader.setJwKey(jwk_keystore);
				jwtHeader.setKid(alias);
				
				testJsonJwkKey(TipoTest.JSON_ENCRYPT_JWK_KEY, useP11asTrustStore, 
						jwk_keystore, jwk_truststore, jwtHeader,
						jwks_keystore);
				
				jwtHeader.setJwKey(null);
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_JWK_KEY_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));
				
				testJsonJwkKey(TipoTest.JSON_ENCRYPT_JWK_KEY_HEADER_CUSTOM, useP11asTrustStore, 
						jwk_keystore, jwk_truststore, jwtHeader,
						jwks_keystore);
				
				jwtHeader.setJwkUrl(null);
				jwtHeader.setKid(null);
			}
			
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEYS.equals(tipoTest)) {
				
				jwtHeader.setKid(alias);
				
				testJsonJwkKeys(TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEYS, useP11asTrustStore, 
						jwks_symmetric_keystore, jwks_symmetric_keystore, alias, jwtHeader);
				
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEYS_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.setJwkUrl(new URI("file://"+jwks_symmetric_fKeystore.getAbsolutePath()));
				
				testJsonJwkKeys(TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEYS_HEADER_CUSTOM, useP11asTrustStore, 
						jwks_symmetric_keystore, jwks_symmetric_keystore, alias, jwtHeader);
				
				jwtHeader.setJwkUrl(null);

			}
			
			
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEY.equals(tipoTest)) {
				
				jwtHeader.setKid(alias);
				
				testJsonJwkKey(TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEY, useP11asTrustStore, 
						jwk_symmetric_keystore, jwk_symmetric_keystore, jwtHeader,
						null);
				
				jwtHeader.setKid(null);
			}
			
			if(tipoTest==null || TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEY_HEADER_CUSTOM.equals(tipoTest)) {
				
				jwtHeader.setJwkUrl(new URI("file://"+jwk_symmetric_fKeystore.getAbsolutePath()));
				
				testJsonJwkKey(TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEY_HEADER_CUSTOM, useP11asTrustStore, 
						jwk_symmetric_keystore, jwk_symmetric_keystore, jwtHeader,
						null);
				
				jwtHeader.setJwkUrl(null);
			}
			
			
			
			System.out.println("Testsuite terminata");

		}finally{
			try{
				if(isKeystoreJKS!=null){
					isKeystoreJKS.close();
				}
			}catch(Exception e){}
			try{
				if(fKeystoreJKS!=null){
					fKeystoreJKS.delete();
				}
			}catch(Exception e){}
			try{
				if(isKeystoreP12!=null){
					isKeystoreP12.close();
				}
			}catch(Exception e){}
			try{
				if(fKeystoreP12!=null){
					fKeystoreP12.delete();
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
				if(fKeystoreP11!=null){
					fKeystoreP11.delete();
				}
			}catch(Exception e){}
			try{
				if(fTruststoreP11!=null){
					fTruststoreP11.delete();
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
				if(fCertX509_P11!=null){
					fCertX509_P11.delete();
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
	
	private static String getLabelTest(TipoTest tipo, boolean useP11asTrustStore) {
		String tipoL = tipo.toString();
		if( tipoL.toLowerCase().contains("pkcs11") ) {
			tipoL = tipoL + " - useP11asTrustStore:" + useP11asTrustStore;
		}
		return tipoL;
	}
	
	private static void testJava(TipoTest tipo, boolean useP11asTrustStore, KeyStore keystore, KeyStore truststore, String alias, String passwordChiavePrivata) throws Exception {
		System.out.println("\n\n ============== "+getLabelTest(tipo,useP11asTrustStore)+" ==================");
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
	
	
	private static void testXmlEncrypt(TipoTest tipo, boolean useP11asTrustStore, KeyStore keystore, String alias, KeyStore truststore, KeyStore keystoreJCEKS, String aliasSecretKey, String passwordChiavePrivata) throws Exception {
		System.out.println("\n\n ============== "+getLabelTest(tipo,useP11asTrustStore)+" ==================");
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
		SecretKey secretKeyGenerata_symmetric = AbstractXmlCipher.generateSecretKey(keyAlgorithm);

		// Si pup usareanche DESede per TRIPLEDES
		// keytool -genseckey -alias 'openspcoop' -keyalg 'AES' -keystore example.jceks -storepass '123456' -keypass 'key123456' -storetype "JCEKS" -keysize 256
		@SuppressWarnings("unused")
		SecretKey secretKeyLetta = keystoreJCEKS.getSecretKey(aliasSecretKey, passwordChiavePrivata);
		
		// *** TEST 1a:  Cifratura con chiave simmetrica non wrapped; chiave simmetrica utilizzata generata precedentemente. 
					
		// Crifratura
		String encryptAlgorithm = XMLCipher.AES_128;
		XmlEncrypt xmlEncrypt = new XmlEncrypt(SymmetricKeyWrappedMode.SYM_ENC_KEY_NO_WRAPPED, secretKeyGenerata_symmetric);
		xmlEncrypt.encryptSymmetric(node, encryptAlgorithm, canonicalizationAlgorithm, digestAlgorithm);
		System.out.println("["+tipo+"]- (1a) Xml Encrypt (SymmetricKey-Generata): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		// Decifratura
		XmlDecrypt xmlDecrypt = new XmlDecrypt(SymmetricKeyWrappedMode.SYM_ENC_KEY_NO_WRAPPED, secretKeyGenerata_symmetric);
		xmlDecrypt.decrypt(node);
		System.out.println("["+tipo+"]- (1a) Xml Decrypt (SymmetricKey-Generata): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		
		// *** TEST 1b:  Cifratura con chiave simmetrica non wrapped; chiave simmetrica letta da un keystore JCEKS
				
		// Crifratura
		boolean symmetricKey = true;
		encryptAlgorithm = XMLCipher.AES_128;
		//xmlEncrypt = new XmlEncrypt(secretKeyLetta);
		xmlEncrypt = new XmlEncrypt(keystoreJCEKS, symmetricKey, SymmetricKeyWrappedMode.SYM_ENC_KEY_NO_WRAPPED, aliasSecretKey, passwordChiavePrivata);
		xmlEncrypt.encryptSymmetric(node, encryptAlgorithm);
		System.out.println("["+tipo+"]- (1b) Xml Encrypt (SymmetricKey-Letta): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		// Decifratura
		//xmlDecrypt = new XmlDecrypt(secretKeyLetta);
		xmlDecrypt = new XmlDecrypt(keystoreJCEKS, symmetricKey, SymmetricKeyWrappedMode.SYM_ENC_KEY_NO_WRAPPED, aliasSecretKey, passwordChiavePrivata);
		xmlDecrypt.decrypt(node);
		System.out.println("["+tipo+"]- (1b) Xml Decrypt (SymmetricKey-Letta): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		
		// *** TEST 2a:  Cifratura con wrapped della chiave simmetrica generata dinamicanete; il wrapped avviente tramite una chiave asimmetrica
		
		// 2b Cifratura con chiave simmetrica wrapped ed utilizzo di chiavi asimmetriche
		//    Cifratura con chiave privata e decifratura con chiave pubblica. Si tratta anche di un caso di firma implicita
		
		String wrappedKeyAlgorithm = XMLCipher.RSA_v1dot5;
		
		if(!TipoTest.XML_ENCRYPT_PKCS11.equals(tipo)) {
		
			// Con pkcs11 ottengo un errore poiche' non viene supportata questa modalità
			// Caused by: java.security.InvalidKeyException: Wrap has to be used with public keys
			// at jdk.crypto.cryptoki/sun.security.pkcs11.P11RSACipher.implInit(P11RSACipher.java:208)
			// at jdk.crypto.cryptoki/sun.security.pkcs11.P11RSACipher.engineInit(P11RSACipher.java:168)
			// Il P11RSACipher supporta solo l'utilizzo di chiavi pubbliche per cifrare la wrapped key
			
			// Crifratura
			keyAlgorithm = "AES";
			encryptAlgorithm = XMLCipher.AES_128;
			xmlEncrypt = new XmlEncrypt(SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_ASYMMETRIC_KEY, keystore,alias,passwordChiavePrivata);
			xmlEncrypt.encrypt(node, encryptAlgorithm, canonicalizationAlgorithm, digestAlgorithm, keyAlgorithm, wrappedKeyAlgorithm);
			System.out.println("["+tipo+"]- (a1). Xml Encrypt (Private Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			// Decifratura
			xmlDecrypt = new XmlDecrypt(truststore, alias);
			xmlDecrypt.decrypt(node);
			System.out.println("["+tipo+"]- (a1) Xml Decrypt (Public Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
		}
		
		
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
		xmlEncrypt = new XmlEncrypt(SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_SYMMETRIC_KEY, keystoreJCEKS,aliasSecretKey,passwordChiavePrivata);
		xmlEncrypt.encrypt(node, encryptAlgorithm, canonicalizationAlgorithm, digestAlgorithm, keyAlgorithm, wrappedKeyAlgorithm);
		System.out.println("["+tipo+"]- (a1). Xml Encrypt (Symmetric Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
		
		// Decifratura
		xmlDecrypt = new XmlDecrypt(keystoreJCEKS, true, SymmetricKeyWrappedMode.SYM_ENC_KEY_WRAPPED_SYMMETRIC_KEY, aliasSecretKey, passwordChiavePrivata);
		xmlDecrypt.decrypt(node);
		System.out.println("["+tipo+"]- (a1) Xml Decrypt (Symmetric Wrapped): "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
	}
	
	
	private static void testJsonProperties(TipoTest tipo, boolean useP11asTrustStore, File fKeystore, KeyStore keystore, String alias, File fTruststore, JwtHeaders headers,
			KeyStore truststoreJKS,KeyStore keystoreJKS,HashMap<String, String> keystore_mapAliasPassword, JsonWebKeys jsonWebKeys) throws Exception {
		
		System.out.println("\n\n ============= "+getLabelTest(tipo,useP11asTrustStore)+" ===================");
		System.out.println("["+tipo+"]. Example JsonEncrypt \n");
		
		String jsonInput = "\n{\n\t\"name\":\"value1\", \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n}";

		// Firma
		Properties encryptProps = new Properties();
		InputStream encryptPropsis = TestEncrypt.class.getResourceAsStream("jws.encrypt.properties");
		encryptProps.load(encryptPropsis);
		encryptProps.put("rs.security.keystore.file", fTruststore.getPath());
		if(useP11asTrustStore && truststoreJKS!=null) {
			encryptProps.remove("rs.security.keystore.file");
			encryptProps.put("rs.security.keystore", truststoreJKS.getKeystore());
		}
		//encryptProps.put("rs.security.keystore.file", fKeystore.getPath());
		if(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS.equals(tipo) || 
				TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12.equals(tipo) || 
				TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11.equals(tipo) || 
				TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM.equals(tipo)) {
			
			encryptProps.put("rs.security.keystore.type", "jks");
			
			if(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11.equals(tipo) || 
					TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM.equals(tipo)) {
				if(useP11asTrustStore) {
					encryptProps.put("rs.security.keystore.type", "pkcs11");
				}
				
				encryptProps.remove("rs.security.keystore.alias");
				encryptProps.put("rs.security.keystore.alias", alias);
				
				encryptProps.remove("rs.security.encryption.key.algorithm");
				// RSA-OAEP-256 non e' supportato in PKCS11 Provider
				// NOTA: ho provato anche a far arrivare il provider al Cipher, ma l'errore di Unsupported padding permane
				/*
				 * Caused by: java.security.InvalidKeyException: No installed provider supports this key: sun.security.pkcs11.P11Key$P11PrivateKey
						at java.base/javax.crypto.Cipher.chooseProvider(Cipher.java:930)
						at java.base/javax.crypto.Cipher.init(Cipher.java:1286)
						at java.base/javax.crypto.Cipher.init(Cipher.java:1223)
						at org.apache.cxf.rt.security.crypto.CryptoUtils.initCipher(CryptoUtils.java:593)
						... 12 more
					Caused by: javax.crypto.NoSuchPaddingException: Unsupported padding OAEPWithSHA-256AndMGF1Padding
						at jdk.crypto.cryptoki/sun.security.pkcs11.P11RSACipher.engineSetPadding(P11RSACipher.java:137)
						at java.base/javax.crypto.Cipher$Transform.setModePadding(Cipher.java:391)
						at java.base/javax.crypto.Cipher.chooseProvider(Cipher.java:899)
				 * */
				encryptProps.put("rs.security.encryption.key.algorithm", "RSA1_5");

			}
			
			if(headers!=null) {
				// inverto
				encryptProps.put("rs.security.encryption.include.cert.sha1", "true");
				encryptProps.put("rs.security.encryption.include.cert.sha256", "false");
			}
		}
		else if(TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS.equals(tipo) || 
				TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET.equals(tipo) || 
				TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM.equals(tipo)) {
			encryptProps.put("rs.security.keystore.type", "jceks");
			encryptProps.put("rs.security.encryption.content.algorithm","A256GCM");
			encryptProps.put("rs.security.encryption.key.algorithm","DIRECT");
			encryptProps.put("rs.security.key.password","key123456");
			encryptProps.put("rs.security.encryption.include.key.id","false"); // non e' possibile aggiungerlo
			encryptProps.put("rs.security.encryption.include.cert","false"); // non e' possibile aggiungerlo"

			encryptProps.remove("rs.security.encryption.include.cert.sha1");
			encryptProps.remove("rs.security.encryption.include.cert.sha256");
			
			if(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET.equals(tipo) || 
					TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM.equals(tipo)) {
				encryptProps.put("rs.security.keystore.type", "pkcs11");
				
				encryptProps.remove("rs.security.keystore.alias");
				encryptProps.put("rs.security.keystore.alias", alias);
			}
		}
		else if(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipo)  ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
			encryptProps.put("rs.security.keystore.type", "jwk");
			
			encryptProps.remove("rs.security.encryption.include.cert.sha1");
			encryptProps.remove("rs.security.encryption.include.cert.sha256");
		}
		else if(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)  ) {
			encryptProps.put("rs.security.keystore.type", "jwk");
			encryptProps.put("rs.security.encryption.content.algorithm","A256GCM");
			encryptProps.put("rs.security.encryption.key.algorithm","DIRECT");
			encryptProps.put("rs.security.encryption.include.key.id","false"); // non e' possibile aggiungerlo
			encryptProps.put("rs.security.encryption.include.public.key","false"); // non e' possibile aggiungerlo"
			
			encryptProps.remove("rs.security.encryption.include.cert.sha1");
			encryptProps.remove("rs.security.encryption.include.cert.sha256");
		}
		else if(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM_KID_ONLY.equals(tipo) ) {
			encryptProps.remove("rs.security.keystore.alias");
			encryptProps.put("rs.security.keystore.alias", alias);
			
			encryptProps.remove("rs.security.encryption.key.algorithm");
			// RSA-OAEP-256 non e' supportato in PKCS11 Provider
			// NOTA: ho provato anche a far arrivare il provider al Cipher, ma l'errore di Unsupported padding permane
			/*
			 * Caused by: java.security.InvalidKeyException: No installed provider supports this key: sun.security.pkcs11.P11Key$P11PrivateKey
					at java.base/javax.crypto.Cipher.chooseProvider(Cipher.java:930)
					at java.base/javax.crypto.Cipher.init(Cipher.java:1286)
					at java.base/javax.crypto.Cipher.init(Cipher.java:1223)
					at org.apache.cxf.rt.security.crypto.CryptoUtils.initCipher(CryptoUtils.java:593)
					... 12 more
				Caused by: javax.crypto.NoSuchPaddingException: Unsupported padding OAEPWithSHA-256AndMGF1Padding
					at jdk.crypto.cryptoki/sun.security.pkcs11.P11RSACipher.engineSetPadding(P11RSACipher.java:137)
					at java.base/javax.crypto.Cipher$Transform.setModePadding(Cipher.java:391)
					at java.base/javax.crypto.Cipher.chooseProvider(Cipher.java:899)
			 * */
			encryptProps.put("rs.security.encryption.key.algorithm", "RSA1_5");
		}
			
		if(headers!=null) {
			encryptProps.remove("rs.security.encryption.include.cert");
			encryptProps.remove("rs.security.encryption.include.public.key");
		}
		
		Properties decryptProps = new Properties();
		InputStream decryptPropsis = TestEncrypt.class.getResourceAsStream("jws.decrypt.properties");
		decryptProps.load(decryptPropsis);
		
		if(fKeystore!=null) {
			decryptProps.put("rs.security.keystore.file", fKeystore.getPath());
		}
		else if(keystore!=null){
			decryptProps.put("rs.security.keystore", keystore.getKeystore());
		}
		
		decryptProps.remove("rs.security.keystore.type");
		if(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS.equals(tipo) || 
				TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12.equals(tipo) || 
				TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11.equals(tipo) || 
				TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM.equals(tipo)) {
			
			if(TipoTest.JSON_ENCRYPT_PROPERTIES_JKS.equals(tipo) || 
					TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipo)) {
				decryptProps.put("rs.security.keystore.type", "jks");
			}
			else if(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12.equals(tipo) || 
					TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12_HEADER_CUSTOM.equals(tipo)) {
				decryptProps.put("rs.security.keystore.type", "pkcs12");
			}
			else if(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11.equals(tipo) || 
					TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM.equals(tipo)) {
				decryptProps.put("rs.security.keystore.type", "pkcs11");
				
				decryptProps.remove("rs.security.keystore.alias");
				decryptProps.put("rs.security.keystore.alias", alias);
				
				decryptProps.remove("rs.security.encryption.key.algorithm");
				// RSA-OAEP-256 non e' supportato in PKCS11 Provider
				// NOTA: ho provato anche a far arrivare il provider al Cipher, ma l'errore di Unsupported padding permane
				/*
				 * Caused by: java.security.InvalidKeyException: No installed provider supports this key: sun.security.pkcs11.P11Key$P11PrivateKey
						at java.base/javax.crypto.Cipher.chooseProvider(Cipher.java:930)
						at java.base/javax.crypto.Cipher.init(Cipher.java:1286)
						at java.base/javax.crypto.Cipher.init(Cipher.java:1223)
						at org.apache.cxf.rt.security.crypto.CryptoUtils.initCipher(CryptoUtils.java:593)
						... 12 more
					Caused by: javax.crypto.NoSuchPaddingException: Unsupported padding OAEPWithSHA-256AndMGF1Padding
						at jdk.crypto.cryptoki/sun.security.pkcs11.P11RSACipher.engineSetPadding(P11RSACipher.java:137)
						at java.base/javax.crypto.Cipher$Transform.setModePadding(Cipher.java:391)
						at java.base/javax.crypto.Cipher.chooseProvider(Cipher.java:899)
				 * */
				decryptProps.put("rs.security.encryption.key.algorithm", "RSA1_5");
			}
		}
		else if(TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS.equals(tipo) || 
				TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET.equals(tipo) || 
				TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM.equals(tipo)) {
			decryptProps.put("rs.security.keystore.type", "jceks");
			decryptProps.put("rs.security.encryption.content.algorithm","A256GCM");
			decryptProps.put("rs.security.encryption.key.algorithm","DIRECT");
			
			if(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET.equals(tipo) || 
					TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM.equals(tipo)) {
				decryptProps.put("rs.security.keystore.type", "pkcs11");
				
				decryptProps.remove("rs.security.keystore.alias");
				decryptProps.put("rs.security.keystore.alias", alias);
			}
		}
		else if(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo) ) {
			decryptProps.put("rs.security.keystore.type", "jwk");
		}
		else if(TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)  ) {
			decryptProps.put("rs.security.keystore.type", "jwk");
			decryptProps.put("rs.security.encryption.content.algorithm","A256GCM");
			decryptProps.put("rs.security.encryption.key.algorithm","DIRECT");
			decryptProps.put("rs.security.encryption.include.key.id","false"); // non e' possibile aggiungerlo
			decryptProps.put("rs.security.encryption.include.public.key","false"); // non e' possibile aggiungerlo"			
		}
		else if(TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM_KID_ONLY.equals(tipo) ) {
			decryptProps.put("rs.security.keystore.type", "pkcs11");
			
			decryptProps.remove("rs.security.keystore.alias");
			decryptProps.put("rs.security.keystore.alias", alias);
			
			decryptProps.remove("rs.security.encryption.key.algorithm");
			// RSA-OAEP-256 non e' supportato in PKCS11 Provider
			// NOTA: ho provato anche a far arrivare il provider al Cipher, ma l'errore di Unsupported padding permane
			/*
			 * Caused by: java.security.InvalidKeyException: No installed provider supports this key: sun.security.pkcs11.P11Key$P11PrivateKey
					at java.base/javax.crypto.Cipher.chooseProvider(Cipher.java:930)
					at java.base/javax.crypto.Cipher.init(Cipher.java:1286)
					at java.base/javax.crypto.Cipher.init(Cipher.java:1223)
					at org.apache.cxf.rt.security.crypto.CryptoUtils.initCipher(CryptoUtils.java:593)
					... 12 more
				Caused by: javax.crypto.NoSuchPaddingException: Unsupported padding OAEPWithSHA-256AndMGF1Padding
					at jdk.crypto.cryptoki/sun.security.pkcs11.P11RSACipher.engineSetPadding(P11RSACipher.java:137)
					at java.base/javax.crypto.Cipher$Transform.setModePadding(Cipher.java:391)
					at java.base/javax.crypto.Cipher.chooseProvider(Cipher.java:899)
			 * */
			decryptProps.put("rs.security.encryption.key.algorithm", "RSA1_5");
		}
		
		System.out.println("\n\n");
		
		
		
		
		
		// Cifratura con chiave pubblica e decifratura con chiave privata.
		
		
		// **** JSON - !deflated ***
		
		// Encrypt Json 
		JWEOptions options = new JWEOptions(JOSESerialization.JSON);
		JsonEncrypt jsonEncrypt = null;
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(encryptProps, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(encryptProps, headers, options);
		}
		String attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		int lengthNotDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate", attachEncrypt, jsonInput, options, encryptProps, headers);

		// Verifica
		JWTOptions optionsDecrypt = new JWTOptions(JOSESerialization.JSON);
		JsonDecrypt jsonDecrypt = new JsonDecrypt(decryptProps,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		if(!TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS.equals(tipo) && 
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) &&
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC.equals(tipo) && 
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			String suffix = "";
			if(jsonWebKeys!=null) {
				jsonDecrypt = new JsonDecrypt(jsonWebKeys,optionsDecrypt);
				suffix = "-jsonWebKeysHDR";
			}
			else {
				jsonDecrypt = new JsonDecrypt(truststoreJKS,keystoreJKS,keystore_mapAliasPassword,optionsDecrypt);
				suffix = "-jksHDR";
			}
			decrypt(tipo, "encPublic-decryptPrivate"+suffix, true, jsonDecrypt, attachEncrypt, jsonInput, options);
		}
		
		System.out.println("\n\n");
		
		
		
		// **** JSON - deflated via properties ***
		
		encryptProps.put("rs.security.encryption.zip.algorithm","DEF");
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.JSON);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(encryptProps, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(encryptProps, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		int lengthDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate-deflateProperties", attachEncrypt, jsonInput, options, encryptProps, headers);
		if(lengthDeflated>=lengthNotDeflated) {
			throw new Exception("Attesa dimensione minore con utilizzo di deflate");
		}
	
		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.JSON);
		jsonDecrypt = new JsonDecrypt(decryptProps,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate-deflateProperties", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		if(!TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS.equals(tipo) && 
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) &&
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC.equals(tipo) && 
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			String suffix = "";
			if(jsonWebKeys!=null) {
				jsonDecrypt = new JsonDecrypt(jsonWebKeys,optionsDecrypt);
				suffix = "-jsonWebKeysHDR";
			}
			else {
				jsonDecrypt = new JsonDecrypt(truststoreJKS,keystoreJKS,keystore_mapAliasPassword,optionsDecrypt);
				suffix = "-jksHDR";
			}
			decrypt(tipo, "encPublic-decryptPrivate-deflateProperties"+suffix, false, jsonDecrypt, attachEncrypt, jsonInput, options);
		}	
		
		encryptProps.remove("rs.security.encryption.zip.algorithm");
		
		System.out.println("\n\n");
		
		
		
		// **** JSON - deflated via options ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.JSON);
		options.setDeflate(true);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(encryptProps, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(encryptProps, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		lengthDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate-deflate", attachEncrypt, jsonInput, options, encryptProps, headers);
		if(lengthDeflated>=lengthNotDeflated) {
			throw new Exception("Attesa dimensione minore con utilizzo di deflate");
		}

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.JSON);
		jsonDecrypt = new JsonDecrypt(decryptProps,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate-deflate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		if(!TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS.equals(tipo) && 
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) &&
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC.equals(tipo) && 
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			String suffix = "";
				if(jsonWebKeys!=null) {
				jsonDecrypt = new JsonDecrypt(jsonWebKeys,optionsDecrypt);
				suffix = "-jsonWebKeysHDR";
			}
			else {
				jsonDecrypt = new JsonDecrypt(truststoreJKS,keystoreJKS,keystore_mapAliasPassword,optionsDecrypt);
				suffix = "-jksHDR";
			}
			decrypt(tipo, "encPublic-decryptPrivate-deflate"+suffix, true, jsonDecrypt, attachEncrypt, jsonInput, options);
		}
		
		System.out.println("\n\n");
		
		
		
		// **** COMPACT - !deflated ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.COMPACT);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(encryptProps, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(encryptProps, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		lengthNotDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate", attachEncrypt, jsonInput, options, encryptProps, headers);

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.COMPACT);
		jsonDecrypt = new JsonDecrypt(decryptProps,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		if(!TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS.equals(tipo) && 
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) &&
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC.equals(tipo) && 
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			String suffix = "";
				if(jsonWebKeys!=null) {
				jsonDecrypt = new JsonDecrypt(jsonWebKeys,optionsDecrypt);
				suffix = "-jsonWebKeysHDR";
			}
			else {
				jsonDecrypt = new JsonDecrypt(truststoreJKS,keystoreJKS,keystore_mapAliasPassword,optionsDecrypt);
				suffix = "-jksHDR";
			}
			decrypt(tipo, "encPublic-decryptPrivate"+suffix, true, jsonDecrypt, attachEncrypt, jsonInput, options);
		}
		
		System.out.println("\n\n");
		
		
		
		// **** COMPACT - deflated via properties ***
		
		encryptProps.put("rs.security.encryption.zip.algorithm","DEF");
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.COMPACT);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(encryptProps, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(encryptProps, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		lengthDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate-deflate", attachEncrypt, jsonInput, options, encryptProps, headers);
		if(lengthDeflated>=lengthNotDeflated) {
			throw new Exception("Attesa dimensione minore con utilizzo di deflate");
		}

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.COMPACT);
		jsonDecrypt = new JsonDecrypt(decryptProps,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate-deflate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		if(!TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS.equals(tipo) && 
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) &&
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC.equals(tipo) && 
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			String suffix = "";
			if(jsonWebKeys!=null) {
				jsonDecrypt = new JsonDecrypt(jsonWebKeys,optionsDecrypt);
				suffix = "-jsonWebKeysHDR";
			}
			else {
				jsonDecrypt = new JsonDecrypt(truststoreJKS,keystoreJKS,keystore_mapAliasPassword,optionsDecrypt);
				suffix = "-jksHDR";
			}
			decrypt(tipo, "encPublic-decryptPrivate-deflate"+suffix, true, jsonDecrypt, attachEncrypt, jsonInput, options);
		}
		
		encryptProps.remove("rs.security.encryption.zip.algorithm");
		
		System.out.println("\n\n");
		
		
		
		// **** COMPACT - deflated via options ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.COMPACT);
		options.setDeflate(true);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(encryptProps, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(encryptProps, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		lengthDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate-deflate", attachEncrypt, jsonInput, options, encryptProps, headers);
		if(lengthDeflated>=lengthNotDeflated) {
			throw new Exception("Attesa dimensione minore con utilizzo di deflate");
		}

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.COMPACT);
		jsonDecrypt = new JsonDecrypt(decryptProps,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate-deflate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		if(!TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS.equals(tipo) && 
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) &&
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC.equals(tipo) && 
				!TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			String suffix = "";
			if(jsonWebKeys!=null) {
				jsonDecrypt = new JsonDecrypt(jsonWebKeys,optionsDecrypt);
				suffix = "-jsonWebKeysHDR";
			}
			else {
				jsonDecrypt = new JsonDecrypt(truststoreJKS,keystoreJKS,keystore_mapAliasPassword,optionsDecrypt);
				suffix = "-jksHDR";
			}
			decrypt(tipo, "encPublic-decryptPrivate-deflate"+suffix, true, jsonDecrypt, attachEncrypt, jsonInput, options);
		}

		System.out.println("\n\n");
		
	
	}
	
	private static void testJsonKeystore(TipoTest tipo, boolean useP11asTrustStore, KeyStore keystore, KeyStore truststore, String alias, String passwordChiavePrivata, JwtHeaders headers,
			HashMap<String, String> keystore_mapAliasPassword) throws Exception {
		
		System.out.println("\n\n ============= "+getLabelTest(tipo,useP11asTrustStore)+" ===================");
		System.out.println("["+tipo+"]. Example JsonEncrypt \n");
		
		String jsonInput = "\n{\n\t\"name\":\"value1\", \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n}";

		String keyAlgorithm = "RSA-OAEP-256"; 
		String contentAlgorithm = "A256GCM";
		
		if(TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE.equals(tipo) || TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE_HEADER_CUSTOM.equals(tipo)) {
			// RSA-OAEP-256 non e' supportato in PKCS11 Provider
			// NOTA: ho provato anche a far arrivare il provider al Cipher, ma l'errore di Unsupported padding permane
			/*
			 * Caused by: java.security.InvalidKeyException: No installed provider supports this key: sun.security.pkcs11.P11Key$P11PrivateKey
					at java.base/javax.crypto.Cipher.chooseProvider(Cipher.java:930)
					at java.base/javax.crypto.Cipher.init(Cipher.java:1286)
					at java.base/javax.crypto.Cipher.init(Cipher.java:1223)
					at org.apache.cxf.rt.security.crypto.CryptoUtils.initCipher(CryptoUtils.java:593)
					... 12 more
				Caused by: javax.crypto.NoSuchPaddingException: Unsupported padding OAEPWithSHA-256AndMGF1Padding
					at jdk.crypto.cryptoki/sun.security.pkcs11.P11RSACipher.engineSetPadding(P11RSACipher.java:137)
					at java.base/javax.crypto.Cipher$Transform.setModePadding(Cipher.java:391)
					at java.base/javax.crypto.Cipher.chooseProvider(Cipher.java:899)
			 * */
			keyAlgorithm = "RSA1_5"; 
		}

		
		System.out.println("\n\n");
		
		
		
		
		// Cifratura con chiave pubblica e decifratura con chiave privata.
		
		
		// **** JSON - !deflated ***
		
		// Encrypt Json 
		JWEOptions options = new JWEOptions(JOSESerialization.JSON);
		JsonEncrypt jsonEncrypt = null;
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, headers, options);
		}
		String attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		int lengthNotDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate", attachEncrypt, jsonInput, options, null, headers);

		// Verifica
		JWTOptions optionsDecrypt = new JWTOptions(JOSESerialization.JSON);
		JsonDecrypt jsonDecrypt = new JsonDecrypt(keystore, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		String suffix = "";
		jsonDecrypt = new JsonDecrypt(truststore,keystore,keystore_mapAliasPassword,optionsDecrypt);
		suffix = "-jksHDR";
		decrypt(tipo, "encPublic-decryptPrivate"+suffix, true, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		System.out.println("\n\n");
		
		
		
		// **** JSON - deflated ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.JSON);
		options.setDeflate(true);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		int lengthDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate-deflate", attachEncrypt, jsonInput, options, null, headers);
		if(lengthDeflated>=lengthNotDeflated) {
			throw new Exception("Attesa dimensione minore con utilizzo di deflate");
		}

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.JSON);
		jsonDecrypt = new JsonDecrypt(keystore, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate-deflate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		jsonDecrypt = new JsonDecrypt(truststore,keystore,keystore_mapAliasPassword,optionsDecrypt);
		suffix = "-jksHDR";
		decrypt(tipo, "encPublic-decryptPrivate-deflate", true, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		System.out.println("\n\n");
		
		
		
		// **** COMPACT - !deflated ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.COMPACT);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		lengthNotDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate", attachEncrypt, jsonInput, options, null, headers);

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.COMPACT);
		jsonDecrypt = new JsonDecrypt(keystore, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		jsonDecrypt = new JsonDecrypt(truststore,keystore,keystore_mapAliasPassword,optionsDecrypt);
		suffix = "-jksHDR";
		decrypt(tipo, "encPublic-decryptPrivate", true, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		System.out.println("\n\n");
		
		

		// **** COMPACT - deflated ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.COMPACT);
		options.setDeflate(true);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(truststore, alias, keyAlgorithm, contentAlgorithm, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		lengthDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate-deflate", attachEncrypt, jsonInput, options, null, headers);
		if(lengthDeflated>=lengthNotDeflated) {
			throw new Exception("Attesa dimensione minore con utilizzo di deflate");
		}

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.COMPACT);
		jsonDecrypt = new JsonDecrypt(keystore, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate-deflate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		jsonDecrypt = new JsonDecrypt(truststore,keystore,keystore_mapAliasPassword,optionsDecrypt);
		suffix = "-jksHDR";
		decrypt(tipo, "encPublic-decryptPrivate-deflate", true, jsonDecrypt, attachEncrypt, jsonInput, options);

		System.out.println("\n\n");
		
	}
		
	private static void testJsonKeystore(TipoTest tipo, boolean useP11asTrustStore, KeyStore keystoreJCEKS, String alias, String passwordChiavePrivata, JwtHeaders headers) throws Exception {
		System.out.println("\n\n ============= "+getLabelTest(tipo,useP11asTrustStore)+" ===================");
		System.out.println("["+tipo+"]. Example JsonEncrypt (Symmetric) \n");

		String jsonInput = "\n{\n\t\"name\":\"value1\", \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n}";
		
		String keyAlgorithm = "A256KW"; 
		String contentAlgorithm = "A256GCM";
		boolean symmetric = true;
		
		System.out.println("\n");
		
		
	

		// Cifratura con chiave pubblica e decifratura con chiave privata.
		
		
		// **** JSON - !deflated ***
		
		// Encrypt Json 
		JWEOptions options = new JWEOptions(JOSESerialization.JSON);
		JsonEncrypt jsonEncrypt = null;
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, headers, options);
		}
		String attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		int lengthNotDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate", attachEncrypt, jsonInput, options, null, headers);

		// Verifica
		JWTOptions optionsDecrypt = new JWTOptions(JOSESerialization.JSON);
		JsonDecrypt jsonDecrypt = new JsonDecrypt(keystoreJCEKS, symmetric, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		System.out.println("\n\n");
		
		
		// **** JSON - deflated ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.JSON);
		options.setDeflate(true);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		int lengthDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate-deflate", attachEncrypt, jsonInput, options, null, headers);
		if(lengthDeflated>=lengthNotDeflated) {
			throw new Exception("Attesa dimensione minore con utilizzo di deflate");
		}

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.JSON);
		jsonDecrypt = new JsonDecrypt(keystoreJCEKS, symmetric, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate-deflate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		System.out.println("\n\n");
		
		
		
		// **** COMPACT - !deflated ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.COMPACT);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		lengthNotDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate", attachEncrypt, jsonInput, options, null, headers);

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.COMPACT);
		jsonDecrypt = new JsonDecrypt(keystoreJCEKS, symmetric, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		System.out.println("\n\n");
		
		
		
		// **** COMPACT - deflated ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.COMPACT);
		options.setDeflate(true);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(keystoreJCEKS, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		lengthDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate-deflate", attachEncrypt, jsonInput, options, null, headers);
		if(lengthDeflated>=lengthNotDeflated) {
			throw new Exception("Attesa dimensione minore con utilizzo di deflate");
		}

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.COMPACT);
		jsonDecrypt = new JsonDecrypt(keystoreJCEKS, symmetric, alias, passwordChiavePrivata, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate-deflate", false, jsonDecrypt, attachEncrypt, jsonInput, options);

		System.out.println("\n\n");
		
	}
	
	
	
	private static void testJsonJwkKeys(TipoTest tipo, boolean useP11asTrustStore, JsonWebKeys keystore, JsonWebKeys truststore, String alias, JwtHeaders headers) throws Exception {
		
		System.out.println("\n\n ============= "+getLabelTest(tipo,useP11asTrustStore)+" ===================");
		System.out.println("["+tipo+"]. Example JsonEncrypt \n");
		
		String jsonInput = "\n{\n\t\"name\":\"value1\", \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n}";

		String keyAlgorithm = "RSA-OAEP-256"; 
		String contentAlgorithm = "A256GCM";
		
		System.out.println("\n\n");
		
		boolean symmetric = false;
		if(TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEYS.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEYS_HEADER_CUSTOM.equals(tipo)) {
			symmetric = true;
			keyAlgorithm = "DIRECT"; 
			contentAlgorithm = "A256GCM";
		}

		
		
		
		
		
		// Cifratura con chiave pubblica e decifratura con chiave privata.
		
		
		// **** JSON - !deflated ***
		
		// Encrypt Json 
		JWEOptions options = new JWEOptions(JOSESerialization.JSON);
		JsonEncrypt jsonEncrypt = null;
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, alias, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, alias, keyAlgorithm, contentAlgorithm, headers, options);
		}
		String attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		int lengthNotDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate", attachEncrypt, jsonInput, options, null, headers);

		// Verifica
		JWTOptions optionsDecrypt = new JWTOptions(JOSESerialization.JSON);
		JsonDecrypt jsonDecrypt = new JsonDecrypt(keystore, symmetric, alias, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		if(!symmetric) {
			String suffix = "";
			jsonDecrypt = new JsonDecrypt(keystore,optionsDecrypt);
			suffix = "-jsonWebKeysHDR";
			decrypt(tipo, "encPublic-decryptPrivate"+suffix, true, jsonDecrypt, attachEncrypt, jsonInput, options);
		}
		
		System.out.println("\n\n");
		
		
		
		
		// **** JSON - deflated ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.JSON);
		options.setDeflate(true);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, alias, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, alias, keyAlgorithm, contentAlgorithm, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		int lengthDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate-deflate", attachEncrypt, jsonInput, options, null, headers);
		if(lengthDeflated>=lengthNotDeflated) {
			throw new Exception("Attesa dimensione minore con utilizzo di deflate");
		}

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.JSON);
		jsonDecrypt = new JsonDecrypt(keystore, symmetric, alias, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate-deflate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		if(!symmetric) {
			String suffix = "";
			jsonDecrypt = new JsonDecrypt(keystore,optionsDecrypt);
			suffix = "-jsonWebKeysHDR";
			decrypt(tipo, "encPublic-decryptPrivate-deflate"+suffix, true, jsonDecrypt, attachEncrypt, jsonInput, options);
		}
		
		System.out.println("\n\n");
		
		
		
		// **** COMPACT - !deflated ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.COMPACT);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, alias, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, alias, keyAlgorithm, contentAlgorithm, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		lengthNotDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate", attachEncrypt, jsonInput, options, null, headers);

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.COMPACT);
		jsonDecrypt = new JsonDecrypt(keystore, symmetric, alias, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		if(!symmetric) {
			String suffix = "";
			jsonDecrypt = new JsonDecrypt(keystore,optionsDecrypt);
			suffix = "-jsonWebKeysHDR";
			decrypt(tipo, "encPublic-decryptPrivate"+suffix, true, jsonDecrypt, attachEncrypt, jsonInput, options);
		}
		
		System.out.println("\n\n");
		
		
		
		
		// **** COMPACT - deflated ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.COMPACT);
		options.setDeflate(true);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, alias, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, alias, keyAlgorithm, contentAlgorithm, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		lengthDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate-deflate", attachEncrypt, jsonInput, options, null, headers);
		if(lengthDeflated>=lengthNotDeflated) {
			throw new Exception("Attesa dimensione minore con utilizzo di deflate");
		}

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.COMPACT);
		jsonDecrypt = new JsonDecrypt(keystore, symmetric, alias, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate-deflate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		if(!symmetric) {
			String suffix = "";
			jsonDecrypt = new JsonDecrypt(keystore,optionsDecrypt);
			suffix = "-jsonWebKeysHDR";
			decrypt(tipo, "encPublic-decryptPrivate-deflate"+suffix, true, jsonDecrypt, attachEncrypt, jsonInput, options);
		}

		System.out.println("\n\n");
		
		
		
	}
	
	
	
	private static void testJsonJwkKey(TipoTest tipo, boolean useP11asTrustStore, JsonWebKey keystore, JsonWebKey truststore, JwtHeaders headers,
			JsonWebKeys keystoreVerificaHeaders) throws Exception {
		
		System.out.println("\n\n ============= "+getLabelTest(tipo,useP11asTrustStore)+" ===================");
		System.out.println("["+tipo+"]. Example JsonEncrypt \n");
		
		String jsonInput = "\n{\n\t\"name\":\"value1\", \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n, \n\t\"name2\":\"value2\"\n}";

		String keyAlgorithm = "RSA-OAEP-256"; 
		String contentAlgorithm = "A256GCM";

		boolean symmetric = false;
		if(TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEY.equals(tipo) ||
				TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEY_HEADER_CUSTOM.equals(tipo)) {
			symmetric = true;
			keyAlgorithm = "DIRECT"; 
			contentAlgorithm = "A256GCM";
		}
		
		
		
		
		
		// Cifratura con chiave pubblica e decifratura con chiave privata.
		
		
		// **** JSON - !deflated ***
		
		// Encrypt Json 
		JWEOptions options = new JWEOptions(JOSESerialization.JSON);
		JsonEncrypt jsonEncrypt = null;
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, keyAlgorithm, contentAlgorithm, headers, options);
		}
		String attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		int lengthNotDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate", attachEncrypt, jsonInput, options, null, headers);

		// Verifica
		JWTOptions optionsDecrypt = new JWTOptions(JOSESerialization.JSON);
		JsonDecrypt jsonDecrypt = new JsonDecrypt(keystore, symmetric, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		if(!symmetric) {
			String suffix = "";
			jsonDecrypt = new JsonDecrypt(keystoreVerificaHeaders,optionsDecrypt);
			suffix = "-jsonWebKeysHDR";
			decrypt(tipo, "encPublic-decryptPrivate"+suffix, true, jsonDecrypt, attachEncrypt, jsonInput, options);
		}
		
		System.out.println("\n\n");
		
		
		
		
		// **** JSON - deflated ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.JSON);
		options.setDeflate(true);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, keyAlgorithm, contentAlgorithm, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		int lengthDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate-deflate", attachEncrypt, jsonInput, options, null, headers);
		if(lengthDeflated>=lengthNotDeflated) {
			throw new Exception("Attesa dimensione minore con utilizzo di deflate");
		}

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.JSON);
		jsonDecrypt = new JsonDecrypt(keystore, symmetric, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate-deflate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		if(!symmetric) {
			String suffix = "";
			jsonDecrypt = new JsonDecrypt(keystoreVerificaHeaders,optionsDecrypt);
			suffix = "-jsonWebKeysHDR";
			decrypt(tipo, "encPublic-decryptPrivate-deflate"+suffix, true, jsonDecrypt, attachEncrypt, jsonInput, options);
		}
		
		System.out.println("\n\n");
		
		
		
		// **** COMPACT - !deflated ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.COMPACT);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(truststore,symmetric,  keyAlgorithm, contentAlgorithm, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		lengthNotDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate", attachEncrypt, jsonInput, options, null, headers);

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.COMPACT);
		jsonDecrypt = new JsonDecrypt(keystore, symmetric, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		if(!symmetric) {
			String suffix = "";
			jsonDecrypt = new JsonDecrypt(keystoreVerificaHeaders,optionsDecrypt);
			suffix = "-jsonWebKeysHDR";
			decrypt(tipo, "encPublic-decryptPrivate"+suffix, true, jsonDecrypt, attachEncrypt, jsonInput, options);
		}
		
		System.out.println("\n\n");
		
		
		
		
		// **** COMPACT - deflated ***
		
		// Encrypt Json 
		options = new JWEOptions(JOSESerialization.COMPACT);
		options.setDeflate(true);
		if(headers==null) {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, keyAlgorithm, contentAlgorithm, options);
		}
		else {
			jsonEncrypt = new JsonEncrypt(truststore, symmetric, keyAlgorithm, contentAlgorithm, headers, options);
		}
		attachEncrypt = jsonEncrypt.encrypt(jsonInput);
		lengthDeflated = attachEncrypt.length();
		verifyEncryptBuild(tipo, "encPublic-decryptPrivate-deflate", attachEncrypt, jsonInput, options, null, headers);
		if(lengthDeflated>=lengthNotDeflated) {
			throw new Exception("Attesa dimensione minore con utilizzo di deflate");
		}

		// Verifica
		optionsDecrypt = new JWTOptions(JOSESerialization.COMPACT);
		jsonDecrypt = new JsonDecrypt(keystore, symmetric, keyAlgorithm, contentAlgorithm,optionsDecrypt);
		decrypt(tipo, "encPublic-decryptPrivate-deflate", false, jsonDecrypt, attachEncrypt, jsonInput, options);
		
		// Verifica basata sull'header
		if(!symmetric) {
			String suffix = "";
			jsonDecrypt = new JsonDecrypt(keystoreVerificaHeaders,optionsDecrypt);
			suffix = "-jsonWebKeysHDR";
			decrypt(tipo, "encPublic-decryptPrivate-deflate"+suffix, true, jsonDecrypt, attachEncrypt, jsonInput, options);
		}

		System.out.println("\n\n");
		
		
	}

	
	private static void verifyEncryptBuild(TipoTest tipo, String extTipo, String encryption, String jsonInput, JWEOptions options, 
			Properties signatureProps, JwtHeaders headers) throws Exception {
		
		List<String> checkHeaders = getHeaders(signatureProps, headers);
		
		System.out.println("["+tipo+" - "+extTipo+"] "+options.getSerialization().name()+" Encrypt (deflate:"+options.isDeflate()+") Encrypted.length="+encryption.length()+" Encrypted: \n"+encryption);
		if(JOSESerialization.JSON.equals(options.getSerialization())) {
			if(encryption.contains("\"ciphertext\":")==false) {
				throw new Exception("Not Found payload");
			}
			if(encryption.contains(jsonInput)) {
				throw new Exception("Payload not encoded");
			}
						
			int indexOf = encryption.indexOf("\"protected\":\"");
			String protectedHeaders = encryption.substring(indexOf+"\"protected\":\"".length());
			protectedHeaders = protectedHeaders.substring(0, protectedHeaders.indexOf("\""));
			String hdrDecoded = new String (Base64Utilities.decode(protectedHeaders));
			System.out.println("["+tipo+" - "+extTipo+"] "+options.getSerialization().name()+" Encrypt (deflate:"+options.isDeflate()+") protected base64 decoded: "+hdrDecoded);
			if(checkHeaders!=null && !checkHeaders.isEmpty()) {
				for (String hdr : checkHeaders) {
					System.out.println("["+tipo+" - "+extTipo+"] "+options.getSerialization().name()+" Verifico presenza '"+hdr+"'");
					String hdrName = "\""+hdr+"\":";
					if(!hdrDecoded.contains(hdrName)) {
						throw new Exception("'"+hdr+"' not found in headers");
					}
				}
			}
		}
		else {
			if(encryption.contains(".")==false) {
				throw new Exception("Expected format with '.' separator");
			}
			String [] tmp = encryption.split("\\.");
			if(tmp==null) {
				throw new Exception("Expected format with '.' separator");
			}
			int length = 5;
			if(tmp.length!=length) {
				throw new Exception("Expected format with 5 base64 info ('.' separator");
			}
			for (int i = 0; i < tmp.length; i++) {
				String part = tmp[i];
				try {
					Base64Utilities.decode(part);
				}catch(Exception e) {
					throw new Exception("Position '"+i+"' uncorrect base64 encoded:"+e.getMessage(),e);
				}			
			}
			
			String hdrDecoded = new String (Base64Utilities.decode(encryption.split("\\.")[0]));
			System.out.println("["+tipo+" - "+extTipo+"] "+options.getSerialization().name()+" Encrypt (deflate:"+options.isDeflate()+") HDR base64 decoded: "+hdrDecoded);
			if(checkHeaders!=null && !checkHeaders.isEmpty()) {
				for (String hdr : checkHeaders) {
					System.out.println("["+tipo+" - "+extTipo+"] "+options.getSerialization().name()+" Verifico presenza '"+hdr+"'");
					String hdrName = "\""+hdr+"\":";
					if(!hdrDecoded.contains(hdrName)) {
						throw new Exception("'"+hdr+"' not found in headers");
					}
				}
			}
		}
	}
	
	private static void decrypt(TipoTest tipo, String extTipo, boolean useHdrsForValidation, JsonDecrypt decrypt, String encryption, String jsonInput, JWEOptions options) throws Exception {
		decrypt.decrypt(encryption);
		System.out.println("["+tipo+" - "+extTipo+"] "+options.getSerialization().name()+" Decrypt (use-hdrs: "+useHdrsForValidation+" deflate:"+options.isDeflate()+") payload: "+decrypt.getDecodedPayload());
		if(decrypt.getDecodedPayload().equals(jsonInput)==false) {
			throw new Exception("Found different payload");
		}
		

		// test per corrompere il contenuto
		
		// body
		String encryptionCorrottaParteBody = null;
		if(JOSESerialization.JSON.equals(options.getSerialization())) {
			encryptionCorrottaParteBody = encryption.replace("ciphertext\":\"", "ciphertext\":\"CORROTTO");
		}
		else {
			encryptionCorrottaParteBody = encryption.replaceFirst("\\.", ".CORROTTO");
		}
		try {
			decrypt.decrypt(encryptionCorrottaParteBody);
			throw new Exception("Expected validation error (bodyCorrupted: "+encryptionCorrottaParteBody+")");
		}catch(Exception e) {
			System.out.println("[ok] Expected error: "+e.getMessage());
		}

	}
	
	private static List<String> getHeaders(Properties signatureProps, JwtHeaders headers){
		List<String> list = new ArrayList<>();
		
		if(signatureProps!=null) {
			
			String kid = signatureProps.getProperty("rs.security.encryption.include.key.id");
			if("true".equals(kid)) {
				list.add(JwtHeaders.JWT_HDR_KID);
			}
			
			String tipo = signatureProps.getProperty("rs.security.keystore.type");
			if("jwk".equals(tipo)) {
				String jwk = signatureProps.getProperty("rs.security.encryption.include.public.key");
				if("true".equals(jwk)) {
					list.add(JwtHeaders.JWT_HDR_JWK);
				}
			}
			else {
				String x5c = signatureProps.getProperty("rs.security.encryption.include.cert");
				if("true".equals(x5c)) {
					list.add(JwtHeaders.JWT_HDR_X5C);
				}
				String x5t = signatureProps.getProperty("rs.security.encryption.include.cert.sha1");
				if("true".equals(x5t)) {
					list.add(JwtHeaders.JWT_HDR_X5T);
				}
				String x5t_256 = signatureProps.getProperty("rs.security.encryption.include.cert.sha256");
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
		
		JAVA_ENCRYPT_JKS,
		JAVA_ENCRYPT_PKCS12,
		JAVA_ENCRYPT_PKCS11,
		
		XML_ENCRYPT_JKS,
		XML_ENCRYPT_PKCS12,
		XML_ENCRYPT_PKCS11,
		
		JSON_ENCRYPT_PROPERTIES_JKS,
		JSON_ENCRYPT_PROPERTIES_PKCS12,
		JSON_ENCRYPT_PROPERTIES_PKCS11,
		JSON_ENCRYPT_PROPERTIES_JCEKS,
		JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET,
		JSON_ENCRYPT_PROPERTIES_JWK,
		JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC,
		
		JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM,
		JSON_ENCRYPT_PROPERTIES_PKCS12_HEADER_CUSTOM,
		JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM,
		JSON_ENCRYPT_PROPERTIES_JCEKS_HEADER_CUSTOM,
		JSON_ENCRYPT_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM,
		JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM,
		JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM,
		
		JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM_KID_ONLY,
		JSON_ENCRYPT_PROPERTIES_PKCS12_HEADER_CUSTOM_KID_ONLY,
		JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM_KID_ONLY,
		JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY,
		
		JSON_ENCRYPT_JKS_KEYSTORE,
		JSON_ENCRYPT_JKS_KEYSTORE_HEADER_CUSTOM,
		JSON_ENCRYPT_PKCS12_KEYSTORE,
		JSON_ENCRYPT_PKCS12_KEYSTORE_HEADER_CUSTOM,
		JSON_ENCRYPT_PKCS11_KEYSTORE,
		JSON_ENCRYPT_PKCS11_KEYSTORE_HEADER_CUSTOM,
		
		JSON_ENCRYPT_JKS_KEYSTORE_JCE,
		JSON_ENCRYPT_JKS_KEYSTORE_JCE_HEADER_CUSTOM,
		JSON_ENCRYPT_PKCS12_KEYSTORE_JCE,
		JSON_ENCRYPT_PKCS12_KEYSTORE_JCE_HEADER_CUSTOM,
		JSON_ENCRYPT_PKCS11_KEYSTORE_JCE,
		JSON_ENCRYPT_PKCS11_KEYSTORE_JCE_HEADER_CUSTOM,
		
		JSON_ENCRYPT_JWK_KEYS,
		JSON_ENCRYPT_JWK_KEYS_HEADER_CUSTOM,
		JSON_ENCRYPT_JWK_KEY,
		JSON_ENCRYPT_JWK_KEY_HEADER_CUSTOM,
		
		JSON_ENCRYPT_JWK_SYMMETRIC_KEYS,
		JSON_ENCRYPT_JWK_SYMMETRIC_KEYS_HEADER_CUSTOM,
		JSON_ENCRYPT_JWK_SYMMETRIC_KEY,
		JSON_ENCRYPT_JWK_SYMMETRIC_KEY_HEADER_CUSTOM
		
	}

}
