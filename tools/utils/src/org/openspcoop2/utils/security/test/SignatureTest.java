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

package org.openspcoop2.utils.security.test;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.logging.log4j.Level;
import org.apache.xml.security.keys.KeyInfo;
import org.openspcoop2.utils.LoggerBuffer;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.CRLCertstore;
import org.openspcoop2.utils.certificate.JWK;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.ocsp.IOCSPResourceReader;
import org.openspcoop2.utils.certificate.ocsp.OCSPManager;
import org.openspcoop2.utils.certificate.ocsp.OCSPResourceReader;
import org.openspcoop2.utils.certificate.ocsp.OCSPValidatorImpl;
import org.openspcoop2.utils.certificate.ocsp.test.OCSPTest;
import org.openspcoop2.utils.certificate.ocsp.test.OpenSSLThread;
import org.openspcoop2.utils.certificate.test.KeystoreTest;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JWTOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.JsonUtils;
import org.openspcoop2.utils.security.JsonVerifySignature;
import org.openspcoop2.utils.security.JwtHeaders;
import org.openspcoop2.utils.security.PKCS7Signature;
import org.openspcoop2.utils.security.Signature;
import org.openspcoop2.utils.security.VerifyPKCS7Signature;
import org.openspcoop2.utils.security.VerifySignature;
import org.openspcoop2.utils.security.VerifyXmlSignature;
import org.openspcoop2.utils.security.XmlSignature;
import org.openspcoop2.utils.transport.http.IOCSPValidator;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**	
 * TestSignature
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SignatureTest {

	/* !!NOTA!!: 
	 * l'esecuzione dei test P11 richiedono la configurazione descritta in org/openspcoop2/utils/certificate/hsm/HSM.example 
	 * Deve inoltre essere impostata la variabile di sistema (utilizzando path assoluti!!!):
	 * export SOFTHSM2_CONF=DIRECTORY_SCELTA_FASE_INSTALLAZIONE/lib/softhsm/softhsm2.conf
	 */
	
	public static void main(String[] args) throws Exception {

		/*
		 * La chiave segreta generata non è utilizzabile per la firma
		Caused by: java.security.InvalidKeyException: init() failed
			at jdk.crypto.cryptoki/sun.security.pkcs11.P11Mac.engineInit(P11Mac.java:208)
			at java.base/javax.crypto.Mac.init(Mac.java:433)
			at org.openspcoop2.utils.security.HmacJwsSignatureProviderExtended.doCreateJwsSignature(HmacJwsSignatureProvider.java:82)
			... 8 more
		Caused by: sun.security.pkcs11.wrapper.PKCS11Exception: CKR_KEY_TYPE_INCONSISTENT
			at jdk.crypto.cryptoki/sun.security.pkcs11.wrapper.PKCS11.C_SignInit(Native Method)
			at jdk.crypto.cryptoki/sun.security.pkcs11.P11Mac.initialize(P11Mac.java:177)
			at jdk.crypto.cryptoki/sun.security.pkcs11.P11Mac.engineInit(P11Mac.java:206)
			
		Rimane quindi da verificare tutto il funzinamento. Essendo una chiave simmetrica, servira' un fix anche lato Verifier
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
		
		String com = null;
		if(args!=null && args.length>2) {
			String tmp = args[2];
			if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
				com = tmp;
			}
		}
		
		String waitStartupServer = null;
		if(args!=null && args.length>3) {
			String tmp = args[3];
			if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
				waitStartupServer = tmp;
			}
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
		
		InputStream isKeystore_crl_valid = null;
		File fKeystore_crl_valid = null;
		InputStream isKeystore_crl_expired = null;
		File fKeystore_crl_expired = null;
		InputStream isKeystore_crl_revoked = null;
		File fKeystore_crl_revoked = null;
		InputStream isTruststore_crl = null;
		File fTruststore_crl = null;
		InputStream is_crl = null;
		File f_crl = null;
		
		File fCertX509_crl_valid = null;
		File fCertX509_crl_expired = null;
		File fCertX509_crl_revoked = null;
		
		InputStream isKeystore_ocsp_valid = null;
		File fKeystore_ocsp_valid = null;
		InputStream isKeystore_ocsp_revoked = null;
		File fKeystore_ocsp_revoked = null;
		InputStream isTruststore_ocsp = null;
		File fTruststore_ocsp = null;
		
		File fCertX509_ocsp_valid = null;
		File fCertX509_ocsp_revoked = null;
		try{
			System.out.println("env SOFTHSM2_CONF: "+System.getenv("SOFTHSM2_CONF"));
			
			LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ALL);
			Logger log = LoggerWrapperFactory.getLogger(SignatureTest.class);
			
			isKeystoreJKS = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_example.jks");
			fKeystoreJKS = File.createTempFile("keystore", ".jks");
			FileSystemUtilities.writeFile(fKeystoreJKS, Utilities.getAsByteArray(isKeystoreJKS));
			
			isKeystoreP12 = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_example.p12");
			fKeystoreP12 = File.createTempFile("keystore", ".p12");
			FileSystemUtilities.writeFile(fKeystoreP12, Utilities.getAsByteArray(isKeystoreP12));

			isTruststore = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/truststore_example.jks");
			fTruststore = File.createTempFile("truststore", ".jks");
			FileSystemUtilities.writeFile(fTruststore, Utilities.getAsByteArray(isTruststore));

			fKeystoreP11 = File.createTempFile("keystore_hsm", ".properties");
			FileSystemUtilities.writeFile(fKeystoreP11, Utilities.getAsByteArray(SignatureTest.class.getResourceAsStream(KeystoreTest.PREFIX+"govway_test_hsm.properties")) );
			
			fTruststoreP11 = File.createTempFile("truststore_hsm", ".jks");
						
			isKeystoreJCEKS = EncryptTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/example.jceks");
			fKeystoreJCEKS = File.createTempFile("keystore", "jceks");
			FileSystemUtilities.writeFile(fKeystoreJCEKS, Utilities.getAsByteArray(isKeystoreJCEKS));

			jwks_isKeystore = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_example.jwks");
			jwks_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwks_fKeystore, Utilities.getAsByteArray(jwks_isKeystore));

			jwks_isTruststore = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/truststore_example.jwks");
			jwks_fTruststore = File.createTempFile("truststore", ".jwk");
			FileSystemUtilities.writeFile(jwks_fTruststore, Utilities.getAsByteArray(jwks_isTruststore));

			jwk_isKeystore = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_example.jwk");
			jwk_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwk_fKeystore, Utilities.getAsByteArray(jwk_isKeystore));

			jwk_isTruststore = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/truststore_example.jwk");
			jwk_fTruststore = File.createTempFile("truststore", ".jwk");
			FileSystemUtilities.writeFile(jwk_fTruststore, Utilities.getAsByteArray(jwk_isTruststore));

			jwks_symmetric_isKeystore = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_symmetricKey_example.jwks");
			jwks_symmetric_fKeystore = File.createTempFile("keystore", ".jwks");
			FileSystemUtilities.writeFile(jwks_symmetric_fKeystore, Utilities.getAsByteArray(jwks_symmetric_isKeystore));

			jwk_symmetric_isKeystore = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_symmetricKey_example.jwk");
			jwk_symmetric_fKeystore = File.createTempFile("keystore", ".jwk");
			FileSystemUtilities.writeFile(jwk_symmetric_fKeystore, Utilities.getAsByteArray(jwk_symmetric_isKeystore));

			isKeystore_crl_valid = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/crl/ExampleClient1.p12");
			fKeystore_crl_valid = File.createTempFile("keystore", ".p12");
			FileSystemUtilities.writeFile(fKeystore_crl_valid, Utilities.getAsByteArray(isKeystore_crl_valid));
			
			isKeystore_crl_expired = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/crl/ExampleClientScaduto.p12");
			fKeystore_crl_expired = File.createTempFile("keystore", ".p12");
			FileSystemUtilities.writeFile(fKeystore_crl_expired, Utilities.getAsByteArray(isKeystore_crl_expired));
			
			isKeystore_crl_revoked = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/crl/ExampleClientRevocato.p12");
			fKeystore_crl_revoked = File.createTempFile("keystore", ".p12");
			FileSystemUtilities.writeFile(fKeystore_crl_revoked, Utilities.getAsByteArray(isKeystore_crl_revoked));
			
			isTruststore_crl = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/crl/trustStore_ca_certificati.jks");
			fTruststore_crl = File.createTempFile("truststore", ".jks");
			FileSystemUtilities.writeFile(fTruststore_crl, Utilities.getAsByteArray(isTruststore_crl));
			
			is_crl = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/crl/ExampleCA.crl");
			f_crl = File.createTempFile("lista", ".crl");
			FileSystemUtilities.writeFile(f_crl, Utilities.getAsByteArray(is_crl));
			
			isKeystore_ocsp_valid = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/ocsp/testClient.p12");
			fKeystore_ocsp_valid = File.createTempFile("keystore", ".p12");
			FileSystemUtilities.writeFile(fKeystore_ocsp_valid, Utilities.getAsByteArray(isKeystore_ocsp_valid));
			
			isKeystore_ocsp_revoked = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/ocsp/test.p12");
			fKeystore_ocsp_revoked = File.createTempFile("keystore", ".p12");
			FileSystemUtilities.writeFile(fKeystore_ocsp_revoked, Utilities.getAsByteArray(isKeystore_ocsp_revoked));
			
			isTruststore_ocsp = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/ocsp/ca_certificati_TEST.jks");
			fTruststore_ocsp = File.createTempFile("truststore", ".jks");
			FileSystemUtilities.writeFile(fTruststore_ocsp, Utilities.getAsByteArray(isTruststore_ocsp));
			

			String passwordChiavePrivata = "key123456";
			String passwordStore = "123456";
			String alias = "openspcoop";

			KeyStore keystoreJKS = new KeyStore(fKeystoreJKS.getAbsolutePath(), KeystoreType.JKS.getNome(), passwordStore);
			if(!KeystoreType.JKS.getNome().equalsIgnoreCase(keystoreJKS.getKeystoreType())) {
				throw new Exception("Atteso tipo JKS, trovato '"+keystoreJKS.getKeystoreType()+"'");
			}
			KeyStore keystoreP12 = new KeyStore(fKeystoreP12.getAbsolutePath(), KeystoreType.PKCS12.getNome(), passwordStore);
			if(!KeystoreType.PKCS12.getNome().equalsIgnoreCase(keystoreP12.getKeystoreType())) {
				throw new Exception("Atteso tipo PKCS12, trovato '"+keystoreP12.getKeystoreType()+"'");
			}
			KeyStore truststore = new KeyStore(fTruststore.getAbsolutePath(), passwordStore);
			if(!KeystoreType.JKS.getNome().equalsIgnoreCase(truststore.getKeystoreType())) {
				throw new Exception("Atteso tipo JKS, trovato '"+truststore.getKeystoreType()+"'");
			}
			
			KeyStore keystoreP12_crl_valid = new KeyStore(fKeystore_crl_valid.getAbsolutePath(), KeystoreType.PKCS12.getNome(), passwordStore);
			if(!KeystoreType.PKCS12.getNome().equalsIgnoreCase(keystoreP12_crl_valid.getKeystoreType())) {
				throw new Exception("Atteso tipo PKCS12, trovato '"+keystoreP12_crl_valid.getKeystoreType()+"'");
			}
			
			KeyStore keystoreP12_crl_expired = new KeyStore(fKeystore_crl_expired.getAbsolutePath(), KeystoreType.PKCS12.getNome(), passwordStore);
			if(!KeystoreType.PKCS12.getNome().equalsIgnoreCase(keystoreP12_crl_expired.getKeystoreType())) {
				throw new Exception("Atteso tipo PKCS12, trovato '"+keystoreP12_crl_expired.getKeystoreType()+"'");
			}
			
			KeyStore keystoreP12_crl_revoked = new KeyStore(fKeystore_crl_revoked.getAbsolutePath(), KeystoreType.PKCS12.getNome(), passwordStore);
			if(!KeystoreType.PKCS12.getNome().equalsIgnoreCase(keystoreP12_crl_revoked.getKeystoreType())) {
				throw new Exception("Atteso tipo PKCS12, trovato '"+keystoreP12_crl_revoked.getKeystoreType()+"'");
			}
			
			KeyStore truststoreCRL = new KeyStore(fTruststore_crl.getAbsolutePath(), passwordStore);
			if(!KeystoreType.JKS.getNome().equalsIgnoreCase(truststoreCRL.getKeystoreType())) {
				throw new Exception("Atteso tipo JKS, trovato '"+truststoreCRL.getKeystoreType()+"'");
			}
			
			CRLCertstore crl = new CRLCertstore(f_crl.getAbsolutePath());
			
			fCertX509_crl_valid =  File.createTempFile("certValid", ".cer");
			FileSystemUtilities.writeFile(fCertX509_crl_valid, truststoreCRL.getCertificate("ExampleClient1").getEncoded());
			
			fCertX509_crl_expired =  File.createTempFile("certExpired", ".cer");
			FileSystemUtilities.writeFile(fCertX509_crl_expired, truststoreCRL.getCertificate("ExampleClientScaduto").getEncoded());
			
			fCertX509_crl_revoked =  File.createTempFile("certRevoked", ".cer");
			FileSystemUtilities.writeFile(fCertX509_crl_revoked, truststoreCRL.getCertificate("ExampleClientRevocato").getEncoded());
			
			
			
			KeyStore keystoreP12_ocsp_valid = new KeyStore(fKeystore_ocsp_valid.getAbsolutePath(), KeystoreType.PKCS12.getNome(), passwordStore);
			if(!KeystoreType.PKCS12.getNome().equalsIgnoreCase(keystoreP12_ocsp_valid.getKeystoreType())) {
				throw new Exception("Atteso tipo PKCS12, trovato '"+keystoreP12_ocsp_valid.getKeystoreType()+"'");
			}
			
			KeyStore keystoreP12_ocsp_revoked = new KeyStore(fKeystore_ocsp_revoked.getAbsolutePath(), KeystoreType.PKCS12.getNome(), passwordStore);
			if(!KeystoreType.PKCS12.getNome().equalsIgnoreCase(keystoreP12_ocsp_revoked.getKeystoreType())) {
				throw new Exception("Atteso tipo PKCS12, trovato '"+keystoreP12_ocsp_revoked.getKeystoreType()+"'");
			}
			
			KeyStore truststoreOCSP = new KeyStore(fTruststore_ocsp.getAbsolutePath(), passwordStore);
			if(!KeystoreType.JKS.getNome().equalsIgnoreCase(truststoreOCSP.getKeystoreType())) {
				throw new Exception("Atteso tipo JKS, trovato '"+truststoreOCSP.getKeystoreType()+"'");
			}
			
			IOCSPResourceReader ocspResourceReader = new OCSPResourceReader();
			LoggerBuffer lb = new LoggerBuffer();
			lb.setLogError(log);
			lb.setLogDebug(log);
			File f = null;
			try(InputStream is = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/ocsp_test.properties")){
				
				byte[] content = Utilities.getAsByteArray(is);
				
				f = File.createTempFile("test", ".properties");
				FileSystemUtilities.writeFile(f, content);
			
				OCSPManager.init(f.getAbsoluteFile(), true, false, log);
			}
			finally {
				if(f!=null) {
					f.delete();
				}
			}
			IOCSPValidator ocspValidator = new OCSPValidatorImpl(lb, truststoreOCSP, null, "signedByResponderCertificate_case2", ocspResourceReader);
			
			fCertX509_ocsp_valid =  File.createTempFile("certValid", ".cer");
			FileSystemUtilities.writeFile(fCertX509_ocsp_valid, truststoreOCSP.getCertificate("testclient").getEncoded());
			
			fCertX509_ocsp_revoked =  File.createTempFile("certRevoked", ".cer");
			FileSystemUtilities.writeFile(fCertX509_ocsp_revoked, truststoreOCSP.getCertificate("test").getEncoded());
			
			
			
			String aliasP11 = KeystoreTest.ALIAS_PKCS11_SERVER2;
			
			boolean uniqueProviderInstance = true;			
			HSMManager.init(fKeystoreP11, true, log, true);
			HSMManager hsmManager = HSMManager.getInstance();
			hsmManager.providerInit(log, uniqueProviderInstance);
			System.out.println("PKCS11 Keystore registered: "+hsmManager.getKeystoreTypes());
			KeyStore keystoreP11 = hsmManager.getKeystore(KeystoreTest.PKCS11_SERVER);
			if(!KeystoreType.PKCS11.getNome().equalsIgnoreCase(keystoreP11.getKeystoreType())) {
				throw new Exception("Atteso tipo PKCS11, trovato '"+keystoreP12.getKeystoreType()+"'");
			}
			KeyStore truststoreP11 = null;
			if(useP11asTrustStore) {
				truststoreP11 = hsmManager.getKeystore(KeystoreTest.PKCS11_SERVER);
				if(!KeystoreType.PKCS11.getNome().equalsIgnoreCase(truststoreP11.getKeystoreType())) {
					throw new Exception("Atteso tipo PKCS11, trovato '"+truststoreP11.getKeystoreType()+"'");
				}
			}
			else {
				java.security.KeyStore tP11 = java.security.KeyStore.getInstance(KeystoreType.JKS.getNome());
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
				if(!KeystoreType.JKS.getNome().equalsIgnoreCase(truststoreP11.getKeystoreType())) {
					throw new Exception("Atteso tipo JKS, trovato '"+truststoreP11.getKeystoreType()+"'");
				}
			}
			
			KeyStore keystoreJCEKS = new KeyStore(fKeystoreJCEKS.getAbsolutePath(), "JCEKS", passwordStore);
			if(!"JCEKS".equalsIgnoreCase(keystoreJCEKS.getKeystoreType())) {
				throw new Exception("Atteso tipo JCEKS, trovato '"+keystoreJCEKS.getKeystoreType()+"'");
			}
			
			KeyStore keystoreSecretP11 = hsmManager.getKeystore(KeystoreTest.PKCS11_CLIENT1);
			if(!KeystoreType.PKCS11.getNome().equalsIgnoreCase(keystoreSecretP11.getKeystoreType())) {
				throw new Exception("Atteso tipo PKCS11, trovato '"+keystoreSecretP11.getKeystoreType()+"'");
			}
			KeyStore truststoreSecretP11 = null;
			if(useP11asTrustStore) {
				truststoreSecretP11 = hsmManager.getKeystore(KeystoreTest.PKCS11_CLIENT1);
				if(!KeystoreType.PKCS11.getNome().equalsIgnoreCase(truststoreSecretP11.getKeystoreType())) {
					throw new Exception("Atteso tipo PKCS11, trovato '"+truststoreSecretP11.getKeystoreType()+"'");
				}
			}
			
			String aliasSecretP11 = KeystoreTest.ALIAS_PKCS11_CLIENT_SYMMETRIC;

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

			String secret = UUID.randomUUID().toString();

			JwtHeaders jwtHeader = new JwtHeaders();
			jwtHeader.setContentType("application/json");
			jwtHeader.setType("application/json[0]");
			jwtHeader.addCriticalHeader("a1");
			jwtHeader.addCriticalHeader("a2");
			jwtHeader.addExtension("a1", "v1");
			jwtHeader.addExtension("a2", "v2");
			jwtHeader.addExtension("a3", "v3");


			// Esempio Signature Java

			if(tipoTest==null || TipoTest.JAVA_SIGNATURE_JKS.equals(tipoTest)) {
				testJava(TipoTest.JAVA_SIGNATURE_JKS, useP11asTrustStore, 
						keystoreJKS, truststore, alias, passwordChiavePrivata);
			}
			if(tipoTest==null || TipoTest.JAVA_SIGNATURE_PKCS12.equals(tipoTest)) {
				testJava(TipoTest.JAVA_SIGNATURE_PKCS12, useP11asTrustStore, 
						keystoreP12, truststore, alias, passwordChiavePrivata);
			}
			if(tipoTest==null || TipoTest.JAVA_SIGNATURE_PKCS11.equals(tipoTest)) {
				testJava(TipoTest.JAVA_SIGNATURE_PKCS11, useP11asTrustStore, 
						keystoreP11, truststoreP11, aliasP11, passwordChiavePrivata);
			}
			

			// Esempio Signature PKCS7

			if (tipoTest == null || SignatureTest.TipoTest.PKCS7_SIGNATURE_JKS.equals(tipoTest)) {
				testPkcs7(SignatureTest.TipoTest.PKCS7_SIGNATURE_JKS, useP11asTrustStore, 
						keystoreJKS, truststore, alias, passwordChiavePrivata);
			}
			if (tipoTest == null || SignatureTest.TipoTest.PKCS7_SIGNATURE_PKCS12.equals(tipoTest)) {
				testPkcs7(SignatureTest.TipoTest.PKCS7_SIGNATURE_PKCS12, useP11asTrustStore, 
						keystoreP12, truststore, alias, passwordChiavePrivata);
			}
			if (tipoTest == null || SignatureTest.TipoTest.PKCS7_SIGNATURE_PKCS11.equals(tipoTest)) {
				testPkcs7(SignatureTest.TipoTest.PKCS7_SIGNATURE_PKCS11, useP11asTrustStore, 
						keystoreP11, truststoreP11, aliasP11, passwordChiavePrivata);
			}

			
			
			// Esempio Signature Xml 

			if(tipoTest==null || TipoTest.XML_SIGNATURE_JKS.equals(tipoTest)) {
				testXmlSignature(TipoTest.XML_SIGNATURE_JKS, useP11asTrustStore, 
						keystoreJKS, truststore, alias, passwordChiavePrivata);
			}
			if(tipoTest==null || TipoTest.XML_SIGNATURE_PKCS12.equals(tipoTest)) {
				testXmlSignature(TipoTest.XML_SIGNATURE_PKCS12, useP11asTrustStore, 
						keystoreP12, truststore, alias, passwordChiavePrivata);
			}
			if(tipoTest==null || TipoTest.XML_SIGNATURE_PKCS11.equals(tipoTest)) {
				testXmlSignature(TipoTest.XML_SIGNATURE_PKCS11, useP11asTrustStore, 
						keystoreP11, truststoreP11, aliasP11, passwordChiavePrivata);
			}

			
			
			// Esempio Signature JSON

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JKS.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS, useP11asTrustStore, 
						fKeystoreJKS, null, null, fTruststore, truststore, null, null, null);
			}
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM, useP11asTrustStore, 
						fKeystoreJKS, null, null, fTruststore, truststore, jwtHeader, null, null);
				jwtHeader.setX509Url(null);
			}
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM_KID_ONLY.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM_KID_ONLY, useP11asTrustStore, 
						fKeystoreJKS, null, null, fTruststore, truststore, jwtHeader, null, null);
			}

			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS12.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS12, useP11asTrustStore,
						fKeystoreP12, null, null, fTruststore, truststore, null, null, null);
			}
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS12_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS12_HEADER_CUSTOM, useP11asTrustStore,
						 fKeystoreP12, null, null, fTruststore, truststore, jwtHeader, null, null);
				jwtHeader.setX509Url(null);
			}
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS12_HEADER_CUSTOM_KID_ONLY.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS12_HEADER_CUSTOM_KID_ONLY, useP11asTrustStore,
						 fKeystoreP12, null, null, fTruststore, truststore, jwtHeader, null, null);
			}
			
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11, useP11asTrustStore,
						 null, keystoreP11, aliasP11, fTruststoreP11, truststoreP11, null, null, null);
			}
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setX509Url(new URI("file://"+fCertX509_P11.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_HEADER_CUSTOM, useP11asTrustStore,
						 null, keystoreP11, aliasP11, fTruststoreP11, truststoreP11, jwtHeader, null, null);
				jwtHeader.setX509Url(null);
			}
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_HEADER_CUSTOM_KID_ONLY.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_HEADER_CUSTOM_KID_ONLY, useP11asTrustStore,
						 null, keystoreP11, aliasP11, fTruststoreP11, truststoreP11, jwtHeader, null, null);
			}
			

			

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS, useP11asTrustStore,
						 fKeystoreJCEKS, null, null, fKeystoreJCEKS, null, null, null, null);
			}
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM, useP11asTrustStore,
						 fKeystoreJCEKS, null, null, fKeystoreJCEKS, null, jwtHeader, null, null);
				jwtHeader.setX509Url(null);
			}
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET.equals(tipoTest)) {
				if(runPKCS11SecretTest) {
					testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET, useP11asTrustStore,
							 null, keystoreSecretP11, aliasSecretP11, fTruststoreP11, truststoreSecretP11, null, null, null);
				}
				else {
					System.out.println("Test ["+TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET+"] disabilitato");
				}
			}
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM.equals(tipoTest)) {
				if(runPKCS11SecretTest) {
					jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
					testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM, useP11asTrustStore,
							 null, keystoreSecretP11, aliasSecretP11, fTruststoreP11, truststoreSecretP11, jwtHeader, null, null);
					jwtHeader.setX509Url(null);
				}
				else {
					System.out.println("Test ["+TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM+"] disabilitato");
				}
			}
			

			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_CRL.equals(tipoTest)) {
				// valid
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL, false,
						fKeystore_crl_valid, null, "ExampleClient1", fTruststore_crl, truststoreCRL, null, null, null,
						crl, null);
				// expired
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL, false,
						fKeystore_crl_expired, null, "ExampleClientScaduto", fTruststore_crl, truststoreCRL, null, null, null,
						crl, null);
				// revoked
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL, false,
						fKeystore_crl_revoked, null, "ExampleClientRevocato", fTruststore_crl, truststoreCRL, null, null, null,
						crl, null);
			}
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM.equals(tipoTest)) {
				// valid
				jwtHeader.setX509Url(new URI("file://"+fCertX509_crl_valid.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM, false,
						fKeystore_crl_valid, null, "ExampleClient1", fTruststore_crl, truststoreCRL, jwtHeader, null, null,
						crl, null);
				// expired
				jwtHeader.setX509Url(new URI("file://"+fCertX509_crl_expired.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM, false,
						fKeystore_crl_expired, null, "ExampleClientScaduto", fTruststore_crl, truststoreCRL, jwtHeader, null, null,
						crl, null);
				// revoked
				jwtHeader.setX509Url(new URI("file://"+fCertX509_crl_revoked.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM, false,
						fKeystore_crl_revoked, null, "ExampleClientRevocato", fTruststore_crl, truststoreCRL, jwtHeader, null, null,
						crl, null);
				
				jwtHeader.setX509Url(null);
			}
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY.equals(tipoTest)) {
				// valid
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY, false,
						fKeystore_crl_valid, null, "ExampleClient1", fTruststore_crl, truststoreCRL, jwtHeader, null, null,
						crl, null);
				// expired
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY, false,
						fKeystore_crl_expired, null, "ExampleClientScaduto", fTruststore_crl, truststoreCRL, jwtHeader, null, null,
						crl, null);
				// revoked
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY, false,
						fKeystore_crl_revoked, null, "ExampleClientRevocato", fTruststore_crl, truststoreCRL, jwtHeader, null, null,
						crl, null);
			}
			
			
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP.equals(tipoTest)) {
				OpenSSLThread sslThread = newOpenSSLThread(com, waitStartupServer);
				Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
				try {
					// valid
					testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP, false,
							fKeystore_ocsp_valid, null, "testclient", fTruststore_ocsp, truststoreOCSP, null, null, null,
							null, ocspValidator);
					// revoked
					testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP, false,
							fKeystore_ocsp_revoked, null, "test", fTruststore_ocsp, truststoreOCSP, null, null, null,
							null, ocspValidator);
				}
				finally {
					Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
					stopOpenSSLThread(sslThread, waitStartupServer);
				}
			}
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM.equals(tipoTest)) {
				OpenSSLThread sslThread = newOpenSSLThread(com, waitStartupServer);
				Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
				try {
					// valid
					jwtHeader.setX509Url(new URI("file://"+fCertX509_ocsp_valid.getAbsolutePath()));
					testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM, false,
							fKeystore_ocsp_valid, null, "testclient", fTruststore_ocsp, truststoreOCSP, jwtHeader, null, null,
							null, ocspValidator);
					// revoked
					jwtHeader.setX509Url(new URI("file://"+fCertX509_ocsp_revoked.getAbsolutePath()));
					testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM, false,
							fKeystore_ocsp_revoked, null, "test", fTruststore_ocsp, truststoreOCSP, jwtHeader, null, null,
							null, ocspValidator);
					
					jwtHeader.setX509Url(null);
				}
				finally {
					Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
					stopOpenSSLThread(sslThread, waitStartupServer);
				}
			}
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM_KID_ONLY.equals(tipoTest)) {
				OpenSSLThread sslThread = newOpenSSLThread(com, waitStartupServer);
				Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
				try {
					// valid
					testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM_KID_ONLY, false,
							fKeystore_ocsp_valid, null, "testclient", fTruststore_ocsp, truststoreOCSP, jwtHeader, null, null,
							null, ocspValidator);
					// revoked
					testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM_KID_ONLY, false,
							fKeystore_ocsp_revoked, null, "test", fTruststore_ocsp, truststoreOCSP, jwtHeader, null, null,
							null, ocspValidator);
				}
				finally {
					Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
					stopOpenSSLThread(sslThread, waitStartupServer);
				}
			}
			
			
			

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK, useP11asTrustStore,
						 jwks_fKeystore, null, null, jwks_fTruststore, truststore, null, null, null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM, useP11asTrustStore,
						 jwks_fKeystore, null, null, jwks_fTruststore, truststore, jwtHeader, null, null);
				jwtHeader.setJwkUrl(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY, useP11asTrustStore,
						 jwks_fKeystore, null, null, jwks_fTruststore, truststore, jwtHeader, jwks_truststore, null);
			}



			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC, useP11asTrustStore,
						 jwks_symmetric_fKeystore, null, null, jwks_symmetric_fKeystore, null, null, null, null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipoTest)) {
				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM, useP11asTrustStore,
						 jwks_symmetric_fKeystore, null, null, jwks_symmetric_fKeystore, null, jwtHeader, null, null);
				jwtHeader.setJwkUrl(null);
			}
			
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PROPERTIES_SECRET.equals(tipoTest)) {
				testJsonProperties(TipoTest.JSON_SIGNATURE_PROPERTIES_SECRET, useP11asTrustStore,
						 null, null, null, null, null, null, null, secret);
			}

			
			
			
			

			// Esempio Signature JSON con altri costruttori

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JKS_KEYSTORE.equals(tipoTest)) {

				jwtHeader.addX509cert((X509Certificate)keystoreJKS.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(alias);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_JKS_KEYSTORE, useP11asTrustStore,
						 keystoreJKS, truststore, alias, passwordChiavePrivata, jwtHeader);

				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JKS_KEYSTORE_HEADER_CUSTOM.equals(tipoTest)) {

				jwtHeader.addX509cert((X509Certificate)keystoreJKS.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				jwtHeader.setKid(alias);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_JKS_KEYSTORE_HEADER_CUSTOM, useP11asTrustStore,
						 keystoreJKS, truststore, alias, passwordChiavePrivata, jwtHeader);

				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
			}
			
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PKCS12_KEYSTORE.equals(tipoTest)) {

				jwtHeader.addX509cert((X509Certificate)keystoreP12.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(alias);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_PKCS12_KEYSTORE, useP11asTrustStore,
						 keystoreP12, truststore, alias, passwordChiavePrivata, jwtHeader);

				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PKCS12_KEYSTORE_HEADER_CUSTOM.equals(tipoTest)) {

				jwtHeader.addX509cert((X509Certificate)keystoreP12.getCertificate(alias));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
				jwtHeader.setKid(alias);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_PKCS12_KEYSTORE_HEADER_CUSTOM, useP11asTrustStore,
						 keystoreP12, truststore, alias, passwordChiavePrivata, jwtHeader);

				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
			}
			
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PKCS11_KEYSTORE.equals(tipoTest)) {

				jwtHeader.addX509cert((X509Certificate)keystoreP11.getCertificate(aliasP11));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(aliasP11);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_PKCS11_KEYSTORE, useP11asTrustStore,
						 keystoreP11, truststoreP11, aliasP11, passwordChiavePrivata, jwtHeader);

				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PKCS11_KEYSTORE_HEADER_CUSTOM.equals(tipoTest)) {

				jwtHeader.addX509cert((X509Certificate)keystoreP11.getCertificate(aliasP11));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509_P11.getAbsolutePath()));
				jwtHeader.setKid(aliasP11);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_PKCS11_KEYSTORE_HEADER_CUSTOM, useP11asTrustStore,
						 keystoreP11, truststoreP11, aliasP11, passwordChiavePrivata, jwtHeader);

				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
			}
			
			
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JCEKS_KEYSTORE.equals(tipoTest)) {

				jwtHeader.setKid(alias);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_JCEKS_KEYSTORE, useP11asTrustStore,
						 keystoreJCEKS, keystoreJCEKS, alias, passwordChiavePrivata, jwtHeader);

				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JCEKS_KEYSTORE_HEADER_CUSTOM.equals(tipoTest)) {

				jwtHeader.setKid(alias);
				jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));

				testJsonKeystore(TipoTest.JSON_SIGNATURE_JCEKS_KEYSTORE_HEADER_CUSTOM, useP11asTrustStore,
						 keystoreJCEKS, keystoreJCEKS, alias, passwordChiavePrivata, jwtHeader);

				jwtHeader.setKid(null);
				jwtHeader.setX509Url(null);
			}
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PKCS11_SECRET_KEYSTORE.equals(tipoTest)) {
				if(runPKCS11SecretTest) {
					jwtHeader.setKid(alias);
	
					testJsonKeystore(TipoTest.JSON_SIGNATURE_PKCS11_SECRET_KEYSTORE, useP11asTrustStore,
							keystoreSecretP11, keystoreSecretP11, aliasSecretP11, passwordChiavePrivata, jwtHeader);
	
					jwtHeader.setKid(null);
				}
				else {
					System.out.println("Test ["+TipoTest.JSON_SIGNATURE_PKCS11_SECRET_KEYSTORE+"] disabilitato");
				}
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_PKCS11_SECRET_KEYSTORE_HEADER_CUSTOM.equals(tipoTest)) {
				if(runPKCS11SecretTest) {
					jwtHeader.setKid(alias);
					jwtHeader.setX509Url(new URI("file://"+fCertX509.getAbsolutePath()));
	
					testJsonKeystore(TipoTest.JSON_SIGNATURE_PKCS11_SECRET_KEYSTORE_HEADER_CUSTOM, useP11asTrustStore,
							keystoreSecretP11, keystoreSecretP11, aliasSecretP11, passwordChiavePrivata, jwtHeader);
	
					jwtHeader.setKid(null);
					jwtHeader.setX509Url(null);
				}
				else {
					System.out.println("Test ["+TipoTest.JSON_SIGNATURE_PKCS11_SECRET_KEYSTORE_HEADER_CUSTOM+"] disabilitato");
				}
			}

			
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_CRL_KEYSTORE.equals(tipoTest)) {

				// valid
				String aliasCrl = "ExampleClient1";
				jwtHeader.addX509cert((X509Certificate)truststoreCRL.getCertificate(aliasCrl));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(aliasCrl);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_CRL_KEYSTORE, false,
						keystoreP12_crl_valid, truststoreCRL, aliasCrl, passwordStore, jwtHeader,
						crl, null);

				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
				
				// expired
				aliasCrl = "ExampleClientScaduto";
				jwtHeader.addX509cert((X509Certificate)truststoreCRL.getCertificate(aliasCrl));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(aliasCrl);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_CRL_KEYSTORE, false,
						keystoreP12_crl_expired, truststoreCRL, aliasCrl, passwordStore, jwtHeader,
						crl, null);

				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
				
				// revoked
				aliasCrl = "ExampleClientRevocato";
				jwtHeader.addX509cert((X509Certificate)truststoreCRL.getCertificate(aliasCrl));
				jwtHeader.setX509IncludeCertSha1(true);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(aliasCrl);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_CRL_KEYSTORE, false,
						keystoreP12_crl_revoked, truststoreCRL, aliasCrl, passwordStore, jwtHeader,
						crl, null);

				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_CRL_KEYSTORE_HEADER_CUSTOM.equals(tipoTest)) {

				// valid
				String aliasCrl = "ExampleClient1";
				jwtHeader.addX509cert((X509Certificate)truststoreCRL.getCertificate(aliasCrl));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509_crl_valid.getAbsolutePath()));
				jwtHeader.setKid(aliasCrl);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_CRL_KEYSTORE_HEADER_CUSTOM, false,
						 keystoreP12_crl_valid, truststoreCRL, aliasCrl, passwordStore, jwtHeader,
						crl, null);

				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
				
				// expired
				aliasCrl = "ExampleClientScaduto";
				jwtHeader.addX509cert((X509Certificate)truststoreCRL.getCertificate(aliasCrl));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509_crl_expired.getAbsolutePath()));
				jwtHeader.setKid(aliasCrl);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_CRL_KEYSTORE_HEADER_CUSTOM, false,
						 keystoreP12_crl_expired, truststoreCRL, aliasCrl, passwordStore, jwtHeader,
						crl, null);

				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
				
				// revoked
				aliasCrl = "ExampleClientRevocato";
				jwtHeader.addX509cert((X509Certificate)truststoreCRL.getCertificate(aliasCrl));
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(true);
				jwtHeader.setX509Url(new URI("file://"+fCertX509_crl_revoked.getAbsolutePath()));
				jwtHeader.setKid(aliasCrl);

				testJsonKeystore(TipoTest.JSON_SIGNATURE_CRL_KEYSTORE_HEADER_CUSTOM, false,
						 keystoreP12_crl_revoked, truststoreCRL, aliasCrl, passwordStore, jwtHeader,
						crl, null);

				jwtHeader.getX509c().clear();
				jwtHeader.setX509IncludeCertSha1(false);
				jwtHeader.setX509IncludeCertSha256(false);
				jwtHeader.setX509Url(null);
				jwtHeader.setKid(null);
			}
			
			
			
			
			if(tipoTest==null || TipoTest.JSON_SIGNATURE_OCSP_KEYSTORE.equals(tipoTest)) {
				OpenSSLThread sslThread = newOpenSSLThread(com, waitStartupServer);
				Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
				try {
					// valid
					String aliasOcsp = "testclient";
					jwtHeader.addX509cert((X509Certificate)truststoreOCSP.getCertificate(aliasOcsp));
					jwtHeader.setX509IncludeCertSha1(true);
					jwtHeader.setX509IncludeCertSha256(false);
					jwtHeader.setKid(aliasOcsp);
	
					testJsonKeystore(TipoTest.JSON_SIGNATURE_OCSP_KEYSTORE, false,
							keystoreP12_ocsp_valid, truststoreOCSP, aliasOcsp, passwordStore, jwtHeader,
							null, ocspValidator);
	
					jwtHeader.getX509c().clear();
					jwtHeader.setX509IncludeCertSha1(false);
					jwtHeader.setX509IncludeCertSha256(false);
					jwtHeader.setKid(null);
					
					// revoked
					aliasOcsp = "test";
					jwtHeader.addX509cert((X509Certificate)truststoreOCSP.getCertificate(aliasOcsp));
					jwtHeader.setX509IncludeCertSha1(true);
					jwtHeader.setX509IncludeCertSha256(false);
					jwtHeader.setKid(aliasOcsp);
	
					testJsonKeystore(TipoTest.JSON_SIGNATURE_OCSP_KEYSTORE, false,
							keystoreP12_ocsp_revoked, truststoreOCSP, aliasOcsp, passwordStore, jwtHeader,
							null, ocspValidator);
	
					jwtHeader.getX509c().clear();
					jwtHeader.setX509IncludeCertSha1(false);
					jwtHeader.setX509IncludeCertSha256(false);
					jwtHeader.setKid(null);
				}
				finally {
					Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
					stopOpenSSLThread(sslThread, waitStartupServer);
				}
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_OCSP_KEYSTORE_HEADER_CUSTOM.equals(tipoTest)) {
				OpenSSLThread sslThread = newOpenSSLThread(com, waitStartupServer);
				Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
				try {
					// valid
					String aliasOcsp = "testclient";
					jwtHeader.addX509cert((X509Certificate)truststoreOCSP.getCertificate(aliasOcsp));
					jwtHeader.setX509IncludeCertSha1(false);
					jwtHeader.setX509IncludeCertSha256(true);
					jwtHeader.setX509Url(new URI("file://"+fCertX509_ocsp_valid.getAbsolutePath()));
					jwtHeader.setKid(aliasOcsp);
	
					testJsonKeystore(TipoTest.JSON_SIGNATURE_OCSP_KEYSTORE_HEADER_CUSTOM, false,
							 keystoreP12_ocsp_valid, truststoreOCSP, aliasOcsp, passwordStore, jwtHeader,
							 null, ocspValidator);
	
					jwtHeader.getX509c().clear();
					jwtHeader.setX509IncludeCertSha1(false);
					jwtHeader.setX509IncludeCertSha256(false);
					jwtHeader.setX509Url(null);
					jwtHeader.setKid(null);
					
					// revoked
					aliasOcsp = "test";
					jwtHeader.addX509cert((X509Certificate)truststoreOCSP.getCertificate(aliasOcsp));
					jwtHeader.setX509IncludeCertSha1(false);
					jwtHeader.setX509IncludeCertSha256(true);
					jwtHeader.setX509Url(new URI("file://"+fCertX509_ocsp_revoked.getAbsolutePath()));
					jwtHeader.setKid(aliasOcsp);
	
					testJsonKeystore(TipoTest.JSON_SIGNATURE_OCSP_KEYSTORE_HEADER_CUSTOM, false,
							 keystoreP12_ocsp_revoked, truststoreOCSP, aliasOcsp, passwordStore, jwtHeader,
							 null, ocspValidator);
	
					jwtHeader.getX509c().clear();
					jwtHeader.setX509IncludeCertSha1(false);
					jwtHeader.setX509IncludeCertSha256(false);
					jwtHeader.setX509Url(null);
					jwtHeader.setKid(null);
				}
				finally {
					Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
					stopOpenSSLThread(sslThread, waitStartupServer);
				}
			}
			
			
			


			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_KEYS.equals(tipoTest)) {

				jwtHeader.setJwKey(jwks_keystore, alias);
				jwtHeader.setKid(alias);

				testJsonJwkKeys(TipoTest.JSON_SIGNATURE_JWK_KEYS, useP11asTrustStore,
						 jwks_keystore, jwks_truststore, truststore, alias, jwtHeader);

				jwtHeader.setJwKey(null);
				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_KEYS_HEADER_CUSTOM.equals(tipoTest)) {

				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));

				testJsonJwkKeys(TipoTest.JSON_SIGNATURE_JWK_KEYS_HEADER_CUSTOM, useP11asTrustStore,
						 jwks_keystore, jwks_truststore, truststore, alias, jwtHeader);

				jwtHeader.setJwkUrl(null);
				jwtHeader.setKid(null);
			}



			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_KEY.equals(tipoTest)) {

				jwtHeader.setJwKey(jwk_keystore);
				jwtHeader.setKid(alias);

				testJsonJwkKey(TipoTest.JSON_SIGNATURE_JWK_KEY, useP11asTrustStore,
						 jwk_keystore, jwk_truststore, truststore, jwtHeader);

				jwtHeader.setJwKey(null);
				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_KEY_HEADER_CUSTOM.equals(tipoTest)) {

				jwtHeader.setJwkUrl(new URI("file://"+jwks_fTruststore.getAbsolutePath()));

				testJsonJwkKey(TipoTest.JSON_SIGNATURE_JWK_KEY_HEADER_CUSTOM, useP11asTrustStore,
						 jwk_keystore, jwk_truststore, truststore, jwtHeader);

				jwtHeader.setJwkUrl(null);
				jwtHeader.setKid(null);
			}






			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEYS.equals(tipoTest)) {

				jwtHeader.setKid(alias);

				testJsonJwkKeys(TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEYS, useP11asTrustStore,
						 jwks_symmetric_keystore, jwks_symmetric_keystore, null, alias, jwtHeader);

				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEYS_HEADER_CUSTOM.equals(tipoTest)) {

				jwtHeader.setJwkUrl(new URI("file://"+jwks_symmetric_fKeystore.getAbsolutePath()));

				testJsonJwkKeys(TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEYS_HEADER_CUSTOM, useP11asTrustStore,
						 jwks_symmetric_keystore, jwks_symmetric_keystore, null, alias, jwtHeader);

				jwtHeader.setJwkUrl(null);
			}



			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEY.equals(tipoTest)) {

				jwtHeader.setKid(alias);

				testJsonJwkKey(TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEY, useP11asTrustStore,
						 jwk_symmetric_keystore, jwk_symmetric_keystore, null, jwtHeader);

				jwtHeader.setKid(null);
			}

			if(tipoTest==null || TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEY_HEADER_CUSTOM.equals(tipoTest)) {

				jwtHeader.setJwkUrl(new URI("file://"+jwk_symmetric_fKeystore.getAbsolutePath()));

				testJsonJwkKey(TipoTest.JSON_SIGNATURE_JWK_SYMMETRIC_KEY_HEADER_CUSTOM, useP11asTrustStore,
						 jwk_symmetric_keystore, jwk_symmetric_keystore, null, jwtHeader);

				jwtHeader.setJwkUrl(null);
			}


			if(tipoTest==null || TipoTest.JSON_SIGNATURE_SECRET.equals(tipoTest)) {

				testSecret(TipoTest.JSON_SIGNATURE_SECRET, useP11asTrustStore,
						 secret, null);
				
			}
			
			
			

			System.out.println("Testsuite terminata");


		}finally{
			try{
				if(isKeystoreJKS!=null){
					isKeystoreJKS.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fKeystoreJKS!=null){
					fKeystoreJKS.delete();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(isKeystoreP12!=null){
					isKeystoreP12.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fKeystoreP12!=null){
					fKeystoreP12.delete();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(isTruststore!=null){
					isTruststore.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fTruststore!=null){
					fTruststore.delete();
				}
			}catch(Exception e){
				// ignore
			}
			
			try{
				if(fKeystoreP11!=null){
					fKeystoreP11.delete();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fTruststoreP11!=null){
					fTruststoreP11.delete();
				}
			}catch(Exception e){
				// ignore
			}

			try{
				if(isKeystoreJCEKS!=null){
					isKeystoreJCEKS.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fKeystoreJCEKS!=null){
					fKeystoreJCEKS.delete();
				}
			}catch(Exception e){
				// ignore
			}

			try{
				if(fCertX509!=null){
					fCertX509.delete();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fCertX509_P11!=null){
					fCertX509_P11.delete();
				}
			}catch(Exception e){
				// ignore
			}

			try{
				if(jwk_isKeystore!=null){
					jwk_isKeystore.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(jwk_fKeystore!=null){
					jwk_fKeystore.delete();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(jwk_isTruststore!=null){
					jwk_isTruststore.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(jwk_fTruststore!=null){
					jwk_fTruststore.delete();
				}
			}catch(Exception e){
				// ignore
			}

			try{
				if(jwks_isKeystore!=null){
					jwks_isKeystore.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(jwks_fKeystore!=null){
					jwks_fKeystore.delete();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(jwks_isTruststore!=null){
					jwks_isTruststore.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(jwks_fTruststore!=null){
					jwks_fTruststore.delete();
				}
			}catch(Exception e){
				// ignore
			}

			try{
				if(jwks_symmetric_isKeystore!=null){
					jwks_symmetric_isKeystore.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(jwks_symmetric_fKeystore!=null){
					jwks_symmetric_fKeystore.delete();
				}
			}catch(Exception e){
				// ignore
			}

			try{
				if(jwk_symmetric_isKeystore!=null){
					jwk_symmetric_isKeystore.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(jwk_symmetric_fKeystore!=null){
					jwk_symmetric_fKeystore.delete();
				}
			}catch(Exception e){
				// ignore
			}
			
			try{
				if(isKeystore_crl_valid!=null){
					isKeystore_crl_valid.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fKeystore_crl_valid!=null){
					fKeystore_crl_valid.delete();
				}
			}catch(Exception e){
				// ignore
			}
			
			try{
				if(isKeystore_crl_expired!=null){
					isKeystore_crl_expired.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fKeystore_crl_expired!=null){
					fKeystore_crl_expired.delete();
				}
			}catch(Exception e){
				// ignore
			}
			
			try{
				if(isKeystore_crl_revoked!=null){
					isKeystore_crl_revoked.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fKeystore_crl_revoked!=null){
					fKeystore_crl_revoked.delete();
				}
			}catch(Exception e){
				// ignore
			}

			try{
				if(isTruststore_crl!=null){
					isTruststore_crl.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fTruststore_crl!=null){
					fTruststore_crl.delete();
				}
			}catch(Exception e){
				// ignore
			}

			try{
				if(is_crl!=null){
					is_crl.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(f_crl!=null){
					f_crl.delete();
				}
			}catch(Exception e){
				// ignore
			}
			
			try{
				if(fCertX509_crl_valid!=null){
					fCertX509_crl_valid.delete();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fCertX509_crl_expired!=null){
					fCertX509_crl_expired.delete();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fCertX509_crl_revoked!=null){
					fCertX509_crl_revoked.delete();
				}
			}catch(Exception e){
				// ignore
			}
			
			try{
				if(isKeystore_ocsp_valid!=null){
					isKeystore_ocsp_valid.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fKeystore_ocsp_valid!=null){
					fKeystore_ocsp_valid.delete();
				}
			}catch(Exception e){
				// ignore
			}
			
			try{
				if(isKeystore_ocsp_revoked!=null){
					isKeystore_ocsp_revoked.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fKeystore_ocsp_revoked!=null){
					fKeystore_ocsp_revoked.delete();
				}
			}catch(Exception e){
				// ignore
			}

			try{
				if(isTruststore_ocsp!=null){
					isTruststore_ocsp.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fTruststore_ocsp!=null){
					fTruststore_ocsp.delete();
				}
			}catch(Exception e){
				// ignore
			}
			
			try{
				if(fCertX509_ocsp_valid!=null){
					fCertX509_ocsp_valid.delete();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fCertX509_ocsp_revoked!=null){
					fCertX509_ocsp_revoked.delete();
				}
			}catch(Exception e){
				// ignore
			}
			
		}
	}

	private static String getLabelTest(TipoTest tipo, boolean useP11asTrustStore) {
		String tipoL = tipo.toString();
		if( tipoL.toLowerCase().contains(KeystoreType.PKCS11.getNome()) ) {
			tipoL = tipoL + " - useP11asTrustStore:" + useP11asTrustStore;
		}
		return tipoL;
	}

	private static void testJava(TipoTest tipo, boolean useP11asTrustStore, KeyStore keystore, KeyStore truststore, String alias, String passwordChiavePrivata) throws Exception {
		// Esempio Signature Java 


		System.out.println("\n\n ============== "+getLabelTest(tipo,useP11asTrustStore)+" ==================");
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



	private static void testPkcs7(SignatureTest.TipoTest tipo, boolean useP11asTrustStore, KeyStore keystore, KeyStore truststore, String alias, String passwordChiavePrivata) throws Exception {
		System.out.println("\n\n ============== "+getLabelTest(tipo,useP11asTrustStore)+" ==================");
		System.out.println("[" + tipo + "]. Example PKCS7Signature \n");
		String contenutoDaFirmare = "MarioRossi:23:05:1980";
		String signedAlgorithm = "SHA256withRSA";
		PKCS7Signature signature = null;
		if(SignatureTest.TipoTest.PKCS7_SIGNATURE_PKCS11.equals(tipo)) {
			signature = new PKCS7Signature(keystore, alias, passwordChiavePrivata);
		}
		else {
			signature = new PKCS7Signature(keystore, alias, passwordChiavePrivata, true, true);
		}
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


	private static void testXmlSignature(TipoTest tipo, boolean useP11asTrustStore, KeyStore keystore, KeyStore truststore, String alias, String passwordChiavePrivata) throws Exception {
		// Esempio Signature Xml 

		System.out.println("\n\n ============== "+getLabelTest(tipo,useP11asTrustStore)+" ==================");
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

	private static void testJsonProperties(TipoTest tipo, boolean useP11asTrustStore, File fKeystore, KeyStore keystore, String alias, File fTruststore, KeyStore truststore, JwtHeaders headers,
			JsonWebKeys jsonWebKeys, String secret) throws Exception {
		testJsonProperties(tipo, useP11asTrustStore, fKeystore, keystore, alias, fTruststore, truststore, headers,
				jsonWebKeys, secret,
				null, null);
	}
	private static void testJsonProperties(TipoTest tipo, boolean useP11asTrustStore, File fKeystore, KeyStore keystore, String alias, File fTruststore, KeyStore truststore, JwtHeaders headers,
			JsonWebKeys jsonWebKeys, String secret,
			CRLCertstore crl, IOCSPValidator ocspValidator) throws Exception {
		// Esempio Signature JSON

		System.out.println("\n\n ============== "+getLabelTest(tipo,useP11asTrustStore)+" ==================");
		System.out.println("["+tipo+"]. Example JsonSignature \n");

		String jsonInput = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"value2\"\n}";

		// Firma
		Properties signatureProps = new Properties();
		InputStream is = SignatureTest.class.getResourceAsStream("jws.signature.properties");
		signatureProps.load(is);
		if(fKeystore!=null) {
			signatureProps.put("rs.security.keystore.file", fKeystore.getPath());
		}
		else if(keystore!=null){
			signatureProps.put("rs.security.keystore", keystore.getKeystore());
		}
		signatureProps.remove("rs.security.keystore.type");
		if(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS12.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS12_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_CRL.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
			
			if(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS.equals(tipo) || 
					TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipo)) {
				signatureProps.put("rs.security.keystore.type", KeystoreType.JKS.getNome());
			}
			else if(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS12.equals(tipo) || 
					TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS12_HEADER_CUSTOM.equals(tipo)) {
				signatureProps.put("rs.security.keystore.type", KeystoreType.PKCS12.getNome());
			}
			else if(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11.equals(tipo) || 
					TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_HEADER_CUSTOM.equals(tipo)) {
				signatureProps.put("rs.security.keystore.type", KeystoreType.PKCS11.getNome());
				
				signatureProps.remove("rs.security.keystore.alias");
				signatureProps.put("rs.security.keystore.alias", alias);
			}
			else if(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL.equals(tipo) || 
					TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM.equals(tipo) ||
					TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY.equals(tipo) ||
					TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP.equals(tipo) || 
					TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM.equals(tipo) ||
					TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
				signatureProps.put("rs.security.keystore.type", KeystoreType.PKCS12.getNome());
				
				signatureProps.remove("rs.security.keystore.alias");
				signatureProps.put("rs.security.keystore.alias", alias);
				
				signatureProps.remove("rs.security.key.password");
				signatureProps.put("rs.security.key.password", "123456");
			}

			if(headers!=null) {
				// inverto
				signatureProps.put("rs.security.signature.include.cert.sha1", "true");
				signatureProps.put("rs.security.signature.include.cert.sha256", "false");
			}
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM.equals(tipo)) {
			signatureProps.put("rs.security.keystore.type", "jceks");
			signatureProps.put("rs.security.signature.algorithm","HS256");
			signatureProps.put("rs.security.signature.include.key.id","false"); // non e' possibile aggiungerlo
			signatureProps.put("rs.security.signature.include.cert","false"); // non e' possibile aggiungerlo"

			signatureProps.remove("rs.security.signature.include.cert.sha1");
			signatureProps.remove("rs.security.signature.include.cert.sha256");
			
			if(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET.equals(tipo) || 
					TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM.equals(tipo)) {
				signatureProps.put("rs.security.keystore.type", KeystoreType.PKCS11.getNome());
				
				signatureProps.remove("rs.security.keystore.alias");
				signatureProps.put("rs.security.keystore.alias", alias);
			}
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
			signatureProps.put("rs.security.keystore.type", KeystoreType.JWK_SET.getNome());

			signatureProps.remove("rs.security.signature.include.cert.sha1");
			signatureProps.remove("rs.security.signature.include.cert.sha256");
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			signatureProps.put("rs.security.keystore.type", KeystoreType.JWK_SET.getNome());
			signatureProps.put("rs.security.signature.algorithm","HS256");
			signatureProps.put("rs.security.signature.include.key.id","false"); // non e' possibile aggiungerlo
			signatureProps.put("rs.security.signature.include.public.key","false"); // non e' possibile aggiungerlo"

			signatureProps.remove("rs.security.signature.include.cert.sha1");
			signatureProps.remove("rs.security.signature.include.cert.sha256");
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_SECRET.equals(tipo)) {
			
			signatureProps.put("rs.security.signature.algorithm","HS256");
			signatureProps.put("rs.security.key.password",secret);
			signatureProps.put("rs.security.keystore.type",JsonUtils.RSSEC_KEY_STORE_TYPE_SECRET);
			
			signatureProps.remove("rs.security.signature.include.cert.sha1");
			signatureProps.remove("rs.security.signature.include.cert.sha256");
			signatureProps.remove("rs.security.signature.include.key.id");
			signatureProps.remove("rs.security.signature.include.public.key");
			signatureProps.remove("rs.security.signature.include.cert");
			signatureProps.remove("rs.security.keystore.password");
			signatureProps.remove("rs.security.keystore.alias");
			signatureProps.remove("rs.security.keystore.file");
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_HEADER_CUSTOM_KID_ONLY.equals(tipo) ) {
			signatureProps.put("rs.security.keystore.type", KeystoreType.PKCS11.getNome());
			
			signatureProps.remove("rs.security.keystore.alias");
			signatureProps.put("rs.security.keystore.alias", alias);
		}


		if(headers!=null) {
			signatureProps.remove("rs.security.signature.include.cert");
			signatureProps.remove("rs.security.signature.include.public.key");
		}

		boolean test_jsonSignature = true;
		boolean test_compact = true;
		
		Properties verifySignatureProps = new Properties();
		InputStream verifySignaturePropsis = SignatureTest.class.getResourceAsStream("jws.verify.signature.properties");
		verifySignatureProps.load(verifySignaturePropsis);
		if(fTruststore!=null) {
			verifySignatureProps.put("rs.security.keystore.file", fTruststore.getPath());
		}
		verifySignatureProps.remove("rs.security.keystore.type");
		if(TipoTest.JSON_SIGNATURE_PROPERTIES_JKS.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS12.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS12_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_CRL.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
			verifySignatureProps.put("rs.security.keystore.type", KeystoreType.JKS.getNome());
			
			if(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11.equals(tipo) || 
					TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_HEADER_CUSTOM.equals(tipo)) {
				verifySignatureProps.remove("rs.security.keystore.alias");
				verifySignatureProps.put("rs.security.keystore.alias", alias);

				if(useP11asTrustStore) {
					verifySignatureProps.remove("rs.security.keystore.file");
					verifySignatureProps.put("rs.security.keystore", truststore.getKeystore());
					
					verifySignatureProps.remove("rs.security.keystore.type");
				}
			}
			else if(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL.equals(tipo) || 
					TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM.equals(tipo) ||
					TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY.equals(tipo) ||
					TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP.equals(tipo) || 
					TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM.equals(tipo) ||
					TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
				verifySignatureProps.remove("rs.security.keystore.alias");
				verifySignatureProps.put("rs.security.keystore.alias", alias);
			}
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET.equals(tipo) || 
				TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM.equals(tipo)) {
			verifySignatureProps.put("rs.security.keystore.type", "jceks");
			verifySignatureProps.put("rs.security.signature.algorithm","HS256");
			verifySignatureProps.put("rs.security.key.password","key123456");
			
			if(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET.equals(tipo) || 
					TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM.equals(tipo)) {
				verifySignatureProps.remove("rs.security.keystore.alias");
				verifySignatureProps.put("rs.security.keystore.alias", alias);

				if(useP11asTrustStore) {
					verifySignatureProps.remove("rs.security.keystore.file");
					verifySignatureProps.put("rs.security.keystore", truststore.getKeystore());
				}
			}
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
			verifySignatureProps.put("rs.security.keystore.type", KeystoreType.JWK_SET.getNome());
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo)) {
			verifySignatureProps.put("rs.security.keystore.type", KeystoreType.JWK_SET.getNome());
			verifySignatureProps.put("rs.security.signature.algorithm","HS256");
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_SECRET.equals(tipo) ) {
			verifySignatureProps.put("rs.security.signature.algorithm","HS256");
			verifySignatureProps.put("rs.security.key.password",secret);
			verifySignatureProps.put("rs.security.keystore.type",JsonUtils.RSSEC_KEY_STORE_TYPE_SECRET);
			
			verifySignatureProps.remove("rs.security.keystore.password");
			verifySignatureProps.remove("rs.security.keystore.alias");
			verifySignatureProps.remove("rs.security.keystore.file");
		}
		else if(TipoTest.JSON_SIGNATURE_PROPERTIES_PKCS11_HEADER_CUSTOM_KID_ONLY.equals(tipo) ) {
			verifySignatureProps.remove("rs.security.keystore.alias");
			verifySignatureProps.put("rs.security.keystore.alias", alias);
			
			if(useP11asTrustStore) {
				verifySignatureProps.remove("rs.security.keystore.file");
				verifySignatureProps.put("rs.security.keystore", truststore.getKeystore());
			}
		}


		System.out.println("\n");



		if(test_jsonSignature) {

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
			if(crl!=null) {
				jsonVerify.setCrlX509(crl.getCertStore());
			}
			if(ocspValidator!=null) {
				jsonVerify.setOcspValidatorX509(ocspValidator);
			}
			if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
				try {
					verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
					throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
				}catch(Exception e) {
					String atteso = "ExampleClientScaduto".equals(alias) ? 
							"Certificato di firma scaduto: NotAfter:"
							:
							"Certificato di firma non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date:";
					if("test".equals(alias)){
						atteso = "Certificato di firma non valido: Certificate revoked in date";
					}
					if(e.getMessage().contains(atteso)) {
						System.out.println("Eccezione attesa: "+e.getMessage());
					}else {
						throw e;
					}
				}
			}
			else {
				verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);
			}
	
			// Verifica basata sull'header
			if(!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipo) && 
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) && 
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipo) &&
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo) &&
					!TipoTest.JSON_SIGNATURE_PROPERTIES_SECRET.equals(tipo)) {
				if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
					jsonVerify = new JsonVerifySignature(jsonWebKeys, optionsVerify);
				}
				else {
					jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
				}
				if(crl!=null) {
					jsonVerify.setCrlX509(crl.getCertStore());
				}
				if(ocspValidator!=null) {
					jsonVerify.setOcspValidatorX509(ocspValidator);
				}
				if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
					try {
						verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
						throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
					}catch(Exception e) {
						String headerName = "x5c";
						if(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM.equals(tipo) || TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM.equals(tipo)) {
							headerName = "x5u";
						}
						else if(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY.equals(tipo) || TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
							headerName = "kid";
						}
						String atteso = "ExampleClientScaduto".equals(alias) ? 
								"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' scaduto: NotAfter: "
								:
								"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date";
						if("test".equals(alias)){
							atteso = "Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate revoked in date";
						}
						if(e.getMessage().contains(atteso)) {
							System.out.println("Eccezione attesa: "+e.getMessage());
						}else {
							throw e;
						}
					}
				}
				else {
					verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
				}
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
			if(crl!=null) {
				jsonVerify.setCrlX509(crl.getCertStore());
			}
			if(ocspValidator!=null) {
				jsonVerify.setOcspValidatorX509(ocspValidator);
			}
			if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
				try {
					verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);
					throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
				}catch(Exception e) {
					String atteso = "ExampleClientScaduto".equals(alias) ? 
							"Certificato di firma scaduto: NotAfter:"
							:
							"Certificato di firma non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date:";
					if("test".equals(alias)){
						atteso = "Certificato di firma non valido: Certificate revoked in date";
					}
					if(e.getMessage().contains(atteso)) {
						System.out.println("Eccezione attesa: "+e.getMessage());
					}else {
						throw e;
					}
				}
			}
			else {
				verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);
			}
	
			// Verifica basata sull'header
			if(!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipo) && 
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) && 
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipo) &&
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo) &&
					!TipoTest.JSON_SIGNATURE_PROPERTIES_SECRET.equals(tipo)) {
				if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
					jsonVerify = new JsonVerifySignature(jsonWebKeys, optionsVerify);
				}
				else {
					jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
				}
				if(crl!=null) {
					jsonVerify.setCrlX509(crl.getCertStore());
				}
				if(ocspValidator!=null) {
					jsonVerify.setOcspValidatorX509(ocspValidator);
				}
				if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
					try {
						verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
						throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
					}catch(Exception e) {
						String headerName = "x5c";
						if(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM.equals(tipo) || TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM.equals(tipo)) {
							headerName = "x5u";
						}
						else if(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY.equals(tipo) || TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
							headerName = "kid";
						}
						String atteso = "ExampleClientScaduto".equals(alias) ? 
								"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' scaduto: NotAfter: "
								:
								"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date";
						if("test".equals(alias)){
							atteso = "Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate revoked in date";
						}
						if(e.getMessage().contains(atteso)) {
							System.out.println("Eccezione attesa: "+e.getMessage());
						}else {
							throw e;
						}
					}
				}
				else {
					verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
				}
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
			if(crl!=null) {
				jsonVerify.setCrlX509(crl.getCertStore());
			}
			if(ocspValidator!=null) {
				jsonVerify.setOcspValidatorX509(ocspValidator);
			}
			if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
				try {
					verifySignature(tipo, false, jsonVerify, detachedSign, jsonInput, options);
					throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
				}catch(Exception e) {
					String atteso = "ExampleClientScaduto".equals(alias) ? 
							"Certificato di firma scaduto: NotAfter:"
							:
							"Certificato di firma non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date:";
					if("test".equals(alias)){
						atteso = "Certificato di firma non valido: Certificate revoked in date";
					}
					if(e.getMessage().contains(atteso)) {
						System.out.println("Eccezione attesa: "+e.getMessage());
					}else {
						throw e;
					}
				}
			}
			else {
				verifySignature(tipo, false, jsonVerify, detachedSign, jsonInput, options);
			}
	
			// Verifica basata sull'header
			if(!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipo) && 
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) && 
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipo) &&
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo) &&
					!TipoTest.JSON_SIGNATURE_PROPERTIES_SECRET.equals(tipo)) {
				if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
					jsonVerify = new JsonVerifySignature(jsonWebKeys, optionsVerify);
				}
				else {
					jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
				}
				if(crl!=null) {
					jsonVerify.setCrlX509(crl.getCertStore());
				}
				if(ocspValidator!=null) {
					jsonVerify.setOcspValidatorX509(ocspValidator);
				}
				if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
					try {
						verifySignature(tipo, true, jsonVerify, detachedSign, jsonInput, options);
						throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
					}catch(Exception e) {
						String headerName = "x5c";
						if(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM.equals(tipo) || TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM.equals(tipo)) {
							headerName = "x5u";
						}
						else if(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY.equals(tipo) || TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
							headerName = "kid";
						}
						String atteso = "ExampleClientScaduto".equals(alias) ? 
								"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' scaduto: NotAfter: "
								:
								"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date";
						if("test".equals(alias)){
							atteso = "Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate revoked in date";
						}
						if(e.getMessage().contains(atteso)) {
							System.out.println("Eccezione attesa: "+e.getMessage());
						}else {
							throw e;
						}
					}
				}
				else {
					verifySignature(tipo, true, jsonVerify, detachedSign, jsonInput, options);
				}
			}
	
			System.out.println("\n\n");
			
		}


		if(test_compact) {

			// **** COMPACT - !detached -  [n.c. payloadEncoding] ***		
	
			// Signature Compact
			JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
			JsonSignature jsonSignature = null;
			if(headers==null) {
				jsonSignature = new JsonSignature(signatureProps, options);
			}
			else {
				jsonSignature = new JsonSignature(signatureProps, headers, options);
			}
			String compactSign = jsonSignature.sign(jsonInput);
			verifySignatureBuild(tipo, compactSign, jsonInput, options, signatureProps, headers);
	
			// Verifica
			JWTOptions optionsVerify = new JWTOptions(JOSESerialization.COMPACT);
			JsonVerifySignature jsonVerify = new JsonVerifySignature(verifySignatureProps, optionsVerify);
			if(crl!=null) {
				jsonVerify.setCrlX509(crl.getCertStore());
			}
			if(ocspValidator!=null) {
				jsonVerify.setOcspValidatorX509(ocspValidator);
			}
			if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
				try {
					verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);
					throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
				}catch(Exception e) {
					String atteso = "ExampleClientScaduto".equals(alias) ? 
							"Certificato di firma scaduto: NotAfter:"
							:
							"Certificato di firma non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date:";
					if("test".equals(alias)){
						atteso = "Certificato di firma non valido: Certificate revoked in date";
					}
					if(e.getMessage().contains(atteso)) {
						System.out.println("Eccezione attesa: "+e.getMessage());
					}else {
						throw e;
					}
				}
			}
			else {
				verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);
			}
	
			// Verifica basata sull'header
			if(!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipo) && 
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) && 
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipo) &&
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo) &&
					!TipoTest.JSON_SIGNATURE_PROPERTIES_SECRET.equals(tipo)) {
				if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
					jsonVerify = new JsonVerifySignature(jsonWebKeys, optionsVerify);
				}
				else {
					jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
				}
				if(crl!=null) {
					jsonVerify.setCrlX509(crl.getCertStore());
				}
				if(ocspValidator!=null) {
					jsonVerify.setOcspValidatorX509(ocspValidator);
				}
				if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
					try {
						verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
						throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
					}catch(Exception e) {
						String headerName = "x5c";
						if(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM.equals(tipo) || TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM.equals(tipo)) {
							headerName = "x5u";
						}
						else if(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY.equals(tipo) || TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
							headerName = "kid";
						}
						String atteso = "ExampleClientScaduto".equals(alias) ? 
								"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' scaduto: NotAfter: "
								:
								"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date";
						if("test".equals(alias)){
							atteso = "Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate revoked in date";
						}
						if(e.getMessage().contains(atteso)) {
							System.out.println("Eccezione attesa: "+e.getMessage());
						}else {
							throw e;
						}
					}
				}
				else {
					verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
				}
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
			if(crl!=null) {
				jsonVerify.setCrlX509(crl.getCertStore());
			}
			if(ocspValidator!=null) {
				jsonVerify.setOcspValidatorX509(ocspValidator);
			}
			if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
				try {
					verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);
					throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
				}catch(Exception e) {
					String atteso = "ExampleClientScaduto".equals(alias) ? 
							"Certificato di firma scaduto: NotAfter:"
							:
							"Certificato di firma non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date:";
					if("test".equals(alias)){
						atteso = "Certificato di firma non valido: Certificate revoked in date";
					}
					if(e.getMessage().contains(atteso)) {
						System.out.println("Eccezione attesa: "+e.getMessage());
					}else {
						throw e;
					}
				}
			}
			else {
				verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);
			}
	
			// Verifica basata sull'header
			if(!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS.equals(tipo) && 
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM.equals(tipo) && 
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC.equals(tipo) &&
					!TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM.equals(tipo) &&
					!TipoTest.JSON_SIGNATURE_PROPERTIES_SECRET.equals(tipo)) {
				if(TipoTest.JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
					jsonVerify = new JsonVerifySignature(jsonWebKeys, optionsVerify);
				}
				else {
					jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
				}
				if(crl!=null) {
					jsonVerify.setCrlX509(crl.getCertStore());
				}
				if(ocspValidator!=null) {
					jsonVerify.setOcspValidatorX509(ocspValidator);
				}
				if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
					try {
						verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
						throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
					}catch(Exception e) {
						String headerName = "x5c";
						if(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM.equals(tipo) || TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM.equals(tipo)) {
							headerName = "x5u";
						}
						else if(TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY.equals(tipo) || TipoTest.JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM_KID_ONLY.equals(tipo)) {
							headerName = "kid";
						}
						String atteso = "ExampleClientScaduto".equals(alias) ? 
								"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' scaduto: NotAfter: "
								:
								"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date";
						if("test".equals(alias)){
							atteso = "Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate revoked in date";
						}
						if(e.getMessage().contains(atteso)) {
							System.out.println("Eccezione attesa: "+e.getMessage());
						}else {
							throw e;
						}
					}
				}
				else {
					verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
				}
			}
	
			System.out.println("\n\n");
		}
	}

	private static void testJsonKeystore(TipoTest tipo, boolean useP11asTrustStore, KeyStore keystore, KeyStore truststore, String alias, String passwordChiavePrivata, JwtHeaders headers) throws Exception {
		testJsonKeystore(tipo, useP11asTrustStore, keystore, truststore, alias, passwordChiavePrivata, headers,
				null, null);
	}
	private static void testJsonKeystore(TipoTest tipo, boolean useP11asTrustStore, KeyStore keystore, KeyStore truststore, String alias, String passwordChiavePrivata, JwtHeaders headers,
			CRLCertstore crl, IOCSPValidator ocspValidator) throws Exception {
		// Esempio Signature JSON con altri costruttori

		String jsonInput = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"value2\"\n}";

		System.out.println("\n\n ================ "+getLabelTest(tipo,useP11asTrustStore)+" ================");
		System.out.println("["+tipo+"]. Example JsonSignature (Costruttore keystore) \n");

		Properties signaturePropsTmp = new Properties(); // per comprendere l'algoritmo
		InputStream is = SignatureTest.class.getResourceAsStream("jws.signature.properties");
		signaturePropsTmp.load(is);
		String signatureAlgorithm = signaturePropsTmp.getProperty("rs.security.signature.algorithm");


		System.out.println("\n");


		boolean secretKey = false;
		if(TipoTest.JSON_SIGNATURE_JCEKS_KEYSTORE.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_JCEKS_KEYSTORE_HEADER_CUSTOM.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PKCS11_SECRET_KEYSTORE.equals(tipo) ||
				TipoTest.JSON_SIGNATURE_PKCS11_SECRET_KEYSTORE_HEADER_CUSTOM.equals(tipo)) {
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
		if(crl!=null) {
			jsonVerify.setCrlX509(crl.getCertStore());
		}
		if(ocspValidator!=null) {
			jsonVerify.setOcspValidatorX509(ocspValidator);
		}
		if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
			try {
				verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);
				throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
			}catch(Exception e) {
				String atteso = "ExampleClientScaduto".equals(alias) ? 
						"Certificato di firma scaduto: NotAfter:"
						:
						"Certificato di firma non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date:";
				if("test".equals(alias)){
					atteso = "Certificato di firma non valido: Certificate revoked in date";
				}
				if(e.getMessage().contains(atteso)) {
					System.out.println("Eccezione attesa: "+e.getMessage());
				}else {
					throw e;
				}
			}
		}
		else {
			verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);
		}

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
			if(crl!=null) {
				jsonVerify.setCrlX509(crl.getCertStore());
			}
			if(ocspValidator!=null) {
				jsonVerify.setOcspValidatorX509(ocspValidator);
			}
			if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
				try {
					verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
					throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
				}catch(Exception e) {
					String headerName = "kid";
					if(TipoTest.JSON_SIGNATURE_CRL_KEYSTORE_HEADER_CUSTOM.equals(tipo) || TipoTest.JSON_SIGNATURE_OCSP_KEYSTORE_HEADER_CUSTOM.equals(tipo)) {
						headerName = "x5u";
					}
					String atteso = "ExampleClientScaduto".equals(alias) ? 
							"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' scaduto: NotAfter: "
							:
							"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date";
					if("test".equals(alias)){
						atteso = "Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate revoked in date";
					}
					if(e.getMessage().contains(atteso)) {
						System.out.println("Eccezione attesa: "+e.getMessage());
					}else {
						throw e;
					}
				}
			}
			else {
				verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
			}
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
		if(crl!=null) {
			jsonVerify.setCrlX509(crl.getCertStore());
		}
		if(ocspValidator!=null) {
			jsonVerify.setOcspValidatorX509(ocspValidator);
		}
		if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
			try {
				verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);
				throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
			}catch(Exception e) {
				String atteso = "ExampleClientScaduto".equals(alias) ? 
						"Certificato di firma scaduto: NotAfter:"
						:
						"Certificato di firma non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date:";
				if("test".equals(alias)){
					atteso = "Certificato di firma non valido: Certificate revoked in date";
				}
				if(e.getMessage().contains(atteso)) {
					System.out.println("Eccezione attesa: "+e.getMessage());
				}else {
					throw e;
				}
			}
		}
		else {
			verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);
		}

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststore,optionsVerify);
			if(crl!=null) {
				jsonVerify.setCrlX509(crl.getCertStore());
			}
			if(ocspValidator!=null) {
				jsonVerify.setOcspValidatorX509(ocspValidator);
			}
			if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
				try {
					verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
					throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
				}catch(Exception e) {
					String headerName = "kid";
					if(TipoTest.JSON_SIGNATURE_CRL_KEYSTORE_HEADER_CUSTOM.equals(tipo) || TipoTest.JSON_SIGNATURE_OCSP_KEYSTORE_HEADER_CUSTOM.equals(tipo)) {
						headerName = "x5u";
					}
					String atteso = "ExampleClientScaduto".equals(alias) ? 
							"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' scaduto: NotAfter: "
							:
							"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date";
					if("test".equals(alias)){
						atteso = "Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate revoked in date";
					}
					if(e.getMessage().contains(atteso)) {
						System.out.println("Eccezione attesa: "+e.getMessage());
					}else {
						throw e;
					}
				}
			}
			else {
				verifySignature(tipo, true, jsonVerify, attachSign, jsonInput, options);
			}
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
		if(crl!=null) {
			jsonVerify.setCrlX509(crl.getCertStore());
		}
		if(ocspValidator!=null) {
			jsonVerify.setOcspValidatorX509(ocspValidator);
		}
		if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
			try {
				verifySignature(tipo, false, jsonVerify, detachedSign, jsonInput, options);
				throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
			}catch(Exception e) {
				String atteso = "ExampleClientScaduto".equals(alias) ? 
						"Certificato di firma scaduto: NotAfter:"
						:
						"Certificato di firma non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date:";
				if("test".equals(alias)){
					atteso = "Certificato di firma non valido: Certificate revoked in date";
				}
				if(e.getMessage().contains(atteso)) {
					System.out.println("Eccezione attesa: "+e.getMessage());
				}else {
					throw e;
				}
			}
		}
		else {
			verifySignature(tipo, false, jsonVerify, detachedSign, jsonInput, options);
		}

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststore,optionsVerify);
			if(crl!=null) {
				jsonVerify.setCrlX509(crl.getCertStore());
			}
			if(ocspValidator!=null) {
				jsonVerify.setOcspValidatorX509(ocspValidator);
			}
			if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
				try {
					verifySignature(tipo, true, jsonVerify, detachedSign, jsonInput, options);
					throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
				}catch(Exception e) {
					String headerName = "kid";
					if(TipoTest.JSON_SIGNATURE_CRL_KEYSTORE_HEADER_CUSTOM.equals(tipo) || TipoTest.JSON_SIGNATURE_OCSP_KEYSTORE_HEADER_CUSTOM.equals(tipo)) {
						headerName = "x5u";
					}
					String atteso = "ExampleClientScaduto".equals(alias) ? 
							"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' scaduto: NotAfter: "
							:
							"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date";
					if("test".equals(alias)){
						atteso = "Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate revoked in date";
					}
					if(e.getMessage().contains(atteso)) {
						System.out.println("Eccezione attesa: "+e.getMessage());
					}else {
						throw e;
					}
				}
			}
			else {
				verifySignature(tipo, true, jsonVerify, detachedSign, jsonInput, options);
			}
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
		if(crl!=null) {
			jsonVerify.setCrlX509(crl.getCertStore());
		}
		if(ocspValidator!=null) {
			jsonVerify.setOcspValidatorX509(ocspValidator);
		}
		if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
			try {
				verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);
				throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
			}catch(Exception e) {
				String atteso = "ExampleClientScaduto".equals(alias) ? 
						"Certificato di firma scaduto: NotAfter:"
						:
						"Certificato di firma non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date:";
				if("test".equals(alias)){
					atteso = "Certificato di firma non valido: Certificate revoked in date";
				}
				if(e.getMessage().contains(atteso)) {
					System.out.println("Eccezione attesa: "+e.getMessage());
				}else {
					throw e;
				}
			}
		}
		else {
			verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);
		}

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
			if(crl!=null) {
				jsonVerify.setCrlX509(crl.getCertStore());
			}
			if(ocspValidator!=null) {
				jsonVerify.setOcspValidatorX509(ocspValidator);
			}
			if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
				try {
					verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
					throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
				}catch(Exception e) {
					String headerName = "kid";
					if(TipoTest.JSON_SIGNATURE_CRL_KEYSTORE_HEADER_CUSTOM.equals(tipo) || TipoTest.JSON_SIGNATURE_OCSP_KEYSTORE_HEADER_CUSTOM.equals(tipo)) {
						headerName = "x5u";
					}
					String atteso = "ExampleClientScaduto".equals(alias) ? 
							"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' scaduto: NotAfter: "
							:
							"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date";
					if("test".equals(alias)){
						atteso = "Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate revoked in date";
					}
					if(e.getMessage().contains(atteso)) {
						System.out.println("Eccezione attesa: "+e.getMessage());
					}else {
						throw e;
					}
				}
			}
			else {
				verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
			}
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
		if(crl!=null) {
			jsonVerify.setCrlX509(crl.getCertStore());
		}
		if(ocspValidator!=null) {
			jsonVerify.setOcspValidatorX509(ocspValidator);
		}
		if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
			try {
				verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);
				throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
			}catch(Exception e) {
				String atteso = "ExampleClientScaduto".equals(alias) ? 
						"Certificato di firma scaduto: NotAfter:"
						:
						"Certificato di firma non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date:";
				if("test".equals(alias)){
					atteso = "Certificato di firma non valido: Certificate revoked in date";
				}
				if(e.getMessage().contains(atteso)) {
					System.out.println("Eccezione attesa: "+e.getMessage());
				}else {
					throw e;
				}
			}
		}
		else {
			verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);
		}

		// Verifica basata sull'header
		if(!secretKey) {
			jsonVerify = new JsonVerifySignature(truststore, optionsVerify);
			if(crl!=null) {
				jsonVerify.setCrlX509(crl.getCertStore());
			}
			if(ocspValidator!=null) {
				jsonVerify.setOcspValidatorX509(ocspValidator);
			}
			if("ExampleClientScaduto".equals(alias) || "ExampleClientRevocato".equals(alias) || "test".equals(alias)) {
				try {
					verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
					throw new Exception("Attesa eccezione certificato non valido (alias: "+alias+")");
				}catch(Exception e) {
					String headerName = "kid";
					if(TipoTest.JSON_SIGNATURE_CRL_KEYSTORE_HEADER_CUSTOM.equals(tipo) || TipoTest.JSON_SIGNATURE_OCSP_KEYSTORE_HEADER_CUSTOM.equals(tipo)) {
						headerName = "x5u";
					}
					String atteso = "ExampleClientScaduto".equals(alias) ? 
							"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' scaduto: NotAfter: "
							:
							"Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate has been revoked, reason: KEY_COMPROMISE, revocation date";
					if("test".equals(alias)){
						atteso = "Process '"+headerName+"' error: Certificato presente nell'header '"+headerName+"' non valido: Certificate revoked in date";
					}
					if(e.getMessage().contains(atteso)) {
						System.out.println("Eccezione attesa: "+e.getMessage());
					}else {
						throw e;
					}
				}
			}
			else {
				verifySignature(tipo, true, jsonVerify, compactSign, jsonInput, options);
			}
		}

		System.out.println("\n\n");

	}

	private static void testJsonJwkKeys(TipoTest tipo, boolean useP11asTrustStore, JsonWebKeys keystore, JsonWebKeys truststore, KeyStore truststoreJKS, String alias, JwtHeaders headers) throws Exception {
		// Esempio Signature JSON con altri costruttori

		String jsonInput = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"value2\"\n}";

		System.out.println("\n\n =============== "+getLabelTest(tipo,useP11asTrustStore)+" =================");
		System.out.println("["+tipo+"]. Example JsonSignature (Costruttore keystore) \n");

		Properties signaturePropsTmp = new Properties();
		InputStream is = SignatureTest.class.getResourceAsStream("jws.signature.properties");
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


	private static void testJsonJwkKey(TipoTest tipo, boolean useP11asTrustStore, JsonWebKey keystore, JsonWebKey truststore, KeyStore truststoreJKS, JwtHeaders headers) throws Exception {
		// Esempio Signature JSON con altri costruttori

		String jsonInput = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"value2\"\n}";

		System.out.println("\n\n =============== "+getLabelTest(tipo,useP11asTrustStore)+" =================");
		System.out.println("["+tipo+"]. Example JsonSignature (Costruttore keystore) \n");

		Properties signaturePropsTmp = new Properties();
		InputStream is = SignatureTest.class.getResourceAsStream("jws.signature.properties");
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
	
	
	private static void testSecret(TipoTest tipo, boolean useP11asTrustStore, String secret, JwtHeaders headers) throws Exception {
		// Esempio Signature JSON con altri costruttori

		String jsonInput = "\n{\n\t\"name\":\"value1\",\n\t\"name2\":\"value2\"\n}";

		System.out.println("\n\n ================ "+getLabelTest(tipo,useP11asTrustStore)+" ================");
		System.out.println("["+tipo+"]. Example JsonSignature (Costruttore keystore) \n");

		String signatureAlgorithm = "HS256";
		

		// **** JSON - !detached -  payloadEncoding ***

		// Signature Json
		JWSOptions options = new JWSOptions(JOSESerialization.JSON);
		JsonSignature jsonSignature = null;
		if(headers==null) {
			jsonSignature = new JsonSignature(secret, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(secret, signatureAlgorithm, headers, options);
		}
		String attachSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, attachSign, jsonInput, options, null, headers);

		// Verifica
		JWTOptions optionsVerify = new JWTOptions(JOSESerialization.JSON);
		JsonVerifySignature jsonVerify = null;
		jsonVerify = new JsonVerifySignature(secret, signatureAlgorithm,optionsVerify);
		verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);

		System.out.println("\n\n");



		// **** JSON - !detached -  !payloadEncoding ***

		// Signature Json - Unencoded Payload Option (RFC 7797)
		options = new JWSOptions(JOSESerialization.JSON);
		options.setPayloadEncoding(false);
		if(headers==null) {
			jsonSignature = new JsonSignature(secret, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(secret, signatureAlgorithm, headers, options);
		}
		attachSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo,attachSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.JSON);
		jsonVerify = new JsonVerifySignature(secret, signatureAlgorithm,optionsVerify);
		verifySignature(tipo, false, jsonVerify, attachSign, jsonInput, options);

		System.out.println("\n\n");



		// **** JSON - detached -  [n.c. payloadEncoding] ***

		// Signature Json - detached
		options = new JWSOptions(JOSESerialization.JSON);
		options.setDetached(true);
		if(headers==null) {
			jsonSignature = new JsonSignature(secret, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(secret, signatureAlgorithm, headers, options);
		}
		String detachedSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo,detachedSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.JSON);
		jsonVerify = new JsonVerifySignature(secret, signatureAlgorithm,optionsVerify);
		verifySignature(tipo, false, jsonVerify, detachedSign, jsonInput, options);

		System.out.println("\n\n");



		// **** COMPACT - !detached -  [n.c. payloadEncoding] ***		

		// Signature Compact
		options = new JWSOptions(JOSESerialization.COMPACT);
		if(headers==null) {
			jsonSignature = new JsonSignature(secret, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(secret, signatureAlgorithm, headers, options);
		}
		String compactSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, compactSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.COMPACT);
		jsonVerify = new JsonVerifySignature(secret, signatureAlgorithm,optionsVerify);
		verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);

		System.out.println("\n\n");



		// **** COMPACT - detached -  [n.c. payloadEncoding] ***

		// Signature Compact
		options = new JWSOptions(JOSESerialization.COMPACT);
		options.setDetached(true);
		if(headers==null) {
			jsonSignature = new JsonSignature(secret, signatureAlgorithm, options);
		}
		else {
			jsonSignature = new JsonSignature(secret, signatureAlgorithm, headers, options);
		}
		compactSign = jsonSignature.sign(jsonInput);
		verifySignatureBuild(tipo, compactSign, jsonInput, options, null, headers);

		// Verifica
		optionsVerify = new JWTOptions(JOSESerialization.COMPACT);
		jsonVerify = new JsonVerifySignature(secret, signatureAlgorithm,optionsVerify);
		verifySignature(tipo, false, jsonVerify, compactSign, jsonInput, options);

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
			if(KeystoreType.JWK_SET.getNome().equals(tipo)) {
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
	
	protected static OpenSSLThread newOpenSSLThread(String com, String waitStartupServer) throws Exception {
		
		 boolean started = false;
		 int index = 0;
		 OpenSSLThread sslThread = null;
		 int tentativi = 30; // quando succede l'errore di indirizzo già utilizzato è perchè impiega molto tempo a rilasciare la porta in ambiente jenkins
		 while(!started && index<tentativi) {
		 
			 sslThread = new OpenSSLThread(OCSPTest.normalizeOpensslCommand(com), 64900, "ocsp/ocsp_TEST.cert.pem", "ocsp/ocsp_TEST.key.pem", "ocsp/ca_TEST.cert.pem", "ocsp/index.txt", false);
			
			 try {
				 try {
					 sslThread.start();
					 System.out.println("START, sleep ...");
					
					 Utilities.sleep(OCSPTest.normalizeWaitStartupServer(waitStartupServer));
				 }catch(Throwable t) {
					 // ignore
				 }
				 
				 started = sslThread.debugMsg(false);
			 }finally {
				 if(!started) {
					 index++;
					 System.out.println("NOT STARTED (iteration: "+index+")");
					 // rilascio risorse
					 sslThread.setStop(true);
					 sslThread.waitShutdown(200, 10000);
					 sslThread.close();
				 }
			 }
			 			 
		 }
		
		 System.out.println("STARTED");
		 
		 return sslThread;
	}
	
	protected static void stopOpenSSLThread(OpenSSLThread sslThread, String waitStopServer) throws Exception {
		sslThread.setStop(true);
		sslThread.waitShutdown(200, 10000);
		sslThread.close();
		
		// anche se il processo esce, il rilascio della porta serve impiega più tempo
		try {
			System.out.println("STOP, sleep ...");
			
			Utilities.sleep(OCSPTest.normalizeWaitStartupServer(waitStopServer));
		}catch(Throwable t) {
			// ignore
		}
		System.out.println("STOP");
	}

	public enum TipoTest {

		JAVA_SIGNATURE_JKS,
		JAVA_SIGNATURE_PKCS12,
		JAVA_SIGNATURE_PKCS11,
		
		PKCS7_SIGNATURE_JKS,
		PKCS7_SIGNATURE_PKCS12,
		PKCS7_SIGNATURE_PKCS11,
		
		XML_SIGNATURE_JKS,
		XML_SIGNATURE_PKCS12,
		XML_SIGNATURE_PKCS11,
		
		JSON_SIGNATURE_PROPERTIES_JKS,
		JSON_SIGNATURE_PROPERTIES_PKCS12,
		JSON_SIGNATURE_PROPERTIES_PKCS11,
		JSON_SIGNATURE_PROPERTIES_JCEKS,
		JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET,
		JSON_SIGNATURE_PROPERTIES_JWK,
		JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC,
		JSON_SIGNATURE_PROPERTIES_CRL,
		JSON_SIGNATURE_PROPERTIES_OCSP,
		
		JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_PKCS12_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_PKCS11_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_JCEKS_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_PKCS11_SECRET_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM,
		JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM,
		
		JSON_SIGNATURE_PROPERTIES_JKS_HEADER_CUSTOM_KID_ONLY,
		JSON_SIGNATURE_PROPERTIES_PKCS12_HEADER_CUSTOM_KID_ONLY,
		JSON_SIGNATURE_PROPERTIES_PKCS11_HEADER_CUSTOM_KID_ONLY,
		JSON_SIGNATURE_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY,
		JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY,
		JSON_SIGNATURE_PROPERTIES_OCSP_HEADER_CUSTOM_KID_ONLY,
		
		JSON_SIGNATURE_PROPERTIES_SECRET,
		
		JSON_SIGNATURE_JKS_KEYSTORE,
		JSON_SIGNATURE_JKS_KEYSTORE_HEADER_CUSTOM,
		JSON_SIGNATURE_PKCS12_KEYSTORE,
		JSON_SIGNATURE_PKCS12_KEYSTORE_HEADER_CUSTOM,
		JSON_SIGNATURE_PKCS11_KEYSTORE,
		JSON_SIGNATURE_PKCS11_KEYSTORE_HEADER_CUSTOM,
		JSON_SIGNATURE_JCEKS_KEYSTORE,
		JSON_SIGNATURE_JCEKS_KEYSTORE_HEADER_CUSTOM,
		JSON_SIGNATURE_PKCS11_SECRET_KEYSTORE,
		JSON_SIGNATURE_PKCS11_SECRET_KEYSTORE_HEADER_CUSTOM,
		JSON_SIGNATURE_CRL_KEYSTORE,
		JSON_SIGNATURE_CRL_KEYSTORE_HEADER_CUSTOM,
		JSON_SIGNATURE_OCSP_KEYSTORE,
		JSON_SIGNATURE_OCSP_KEYSTORE_HEADER_CUSTOM,
		
		JSON_SIGNATURE_JWK_KEYS,
		JSON_SIGNATURE_JWK_KEYS_HEADER_CUSTOM,
		JSON_SIGNATURE_JWK_KEY,
		JSON_SIGNATURE_JWK_KEY_HEADER_CUSTOM,
		
		JSON_SIGNATURE_JWK_SYMMETRIC_KEYS,
		JSON_SIGNATURE_JWK_SYMMETRIC_KEYS_HEADER_CUSTOM,
		JSON_SIGNATURE_JWK_SYMMETRIC_KEY,
		JSON_SIGNATURE_JWK_SYMMETRIC_KEY_HEADER_CUSTOM,
		
		JSON_SIGNATURE_SECRET

	}
}
