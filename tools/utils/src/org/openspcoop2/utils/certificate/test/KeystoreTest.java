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
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.slf4j.Logger;

/**
 * KeystoreTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KeystoreTest {

	public static final String ALIAS_1 = "govway_test";
	public static final String DN_1 = "CN=govway_test, OU=govway_test_ou, O=govway_test_o, L=govway_test_l, ST=Italy, C=IT, EMAILADDRESS=info@link.it";
	
	public static final String PASSWORD = "123456";
	public static final String PREFIX = "/org/openspcoop2/utils/certificate/test/";
	
	public static final String PKCS11_CLIENT1 = "pkcs11-client1";
	public static final String PKCS11_CLIENT2 = "pkcs11-client2";
	public static final String PKCS11_SERVER = "pkcs11-server";
	public static final String PKCS11_MODI_CLIENT = "pkcs11-modi-client";
	
	public static final String ALIAS_PKCS11_CLIENT1 = "client1_hsm";
	public static final String ALIAS_PKCS11_CLIENT_SYMMETRIC = "client_symmetric_hsm";
	public static final String ALIAS_PKCS11_CLIENT2 = "client2_hsm";
	public static final String ALIAS_PKCS11_SERVER = "server_hsm";
	public static final String ALIAS_PKCS11_SERVER2 = "server2_hsm";
	public static final String ALIAS_PKCS11_MODI_CLIENT1 = "modi_client1_hsm";
	public static final String ALIAS_PKCS11_MODI_CLIENT2 = "modi_client2_hsm";
	public static final String ALIAS_PKCS11_MODI_CLIENT3 = "modi_client3_hsm";
	
	public static final String DN_PKCS11_CLIENT1 = "CN=ExampleClient1HSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT";
	public static final String DN_PKCS11_CLIENT2 = "CN=ExampleClient2HSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT";
	public static final String DN_PKCS11_SERVER = "CN=ExampleServerHSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT";
	public static final String DN_PKCS11_SERVER2 = "CN=ExampleServer2HSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT";
	public static final String DN_PKCS11_MODI_CLIENT1 = "CN=ExampleModIClient1HSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT";
	public static final String DN_PKCS11_MODI_CLIENT2 = "CN=ExampleModIClient2HSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT";
	public static final String DN_PKCS11_MODI_CLIENT3 = "CN=ExampleModIClient3HSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT";
	
	public static void main(String[] args) throws Exception {
		
		testPrivateKeyInJKS();
		
		testPrivateKeyInPKCS12();

		testPrivateKeyInPKCS11();
		
	}

	public static void testPrivateKeyInJKS() throws Exception {

		System.out.println("========================= JKS ==============================");

		KeyStore keystore = new KeyStore(Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(PREFIX+"govway_test.jks")), ArchiveType.JKS.name(), PASSWORD);
		_test(keystore, ALIAS_1, PASSWORD, DN_1);

	}
	
	public static void testPrivateKeyInPKCS12() throws Exception {

		System.out.println("========================= PKCS12 ==============================");

		KeyStore keystore = new KeyStore(Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(PREFIX+"govway_test.p12")), ArchiveType.JKS.name(), PASSWORD);
		_test(keystore, ALIAS_1, PASSWORD, DN_1);

	}
	
	public static void testPrivateKeyInPKCS11() throws Exception {
		
		System.out.println("========================= PKCS11 ==============================");
		
		System.out.println("env SOFTHSM2_CONF: "+System.getenv("SOFTHSM2_CONF"));
		
		byte [] b = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(PREFIX+"govway_test_hsm.properties"));
		
		File f = File.createTempFile("test", ".properties");
		try(FileOutputStream fout = new FileOutputStream(f)){
			fout.write(b);
			fout.flush();
		}
		
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ALL);
		Logger log = LoggerWrapperFactory.getLogger(KeystoreTest.class);
		HSMManager.init(f, true, log, true);
		
		HSMManager hsmManager = HSMManager.getInstance();
		boolean uniqueProviderInstance = true;
		hsmManager.providerInit(log, uniqueProviderInstance);
		
		System.out.println("Keystore registered: "+hsmManager.getKeystoreTypes());
		if(!hsmManager.getKeystoreTypes().contains(PKCS11_CLIENT1)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_CLIENT1+"' not found");
		}
		if(!hsmManager.getKeystoreTypes().contains(PKCS11_CLIENT2)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_CLIENT2+"' not found");
		}
		if(!hsmManager.getKeystoreTypes().contains(PKCS11_SERVER)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_SERVER+"' not found");
		}
		if(!hsmManager.getKeystoreTypes().contains(PKCS11_MODI_CLIENT)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_MODI_CLIENT+"' not found");
		}
		
		if(!hsmManager.existsKeystoreType(PKCS11_CLIENT1)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_CLIENT1+"' not found");
		}
		if(!hsmManager.existsKeystoreType(PKCS11_CLIENT2)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_CLIENT2+"' not found");
		}
		if(!hsmManager.existsKeystoreType(PKCS11_SERVER)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_SERVER+"' not found");
		}
		if(!hsmManager.existsKeystoreType(PKCS11_MODI_CLIENT)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_MODI_CLIENT+"' not found");
		}
		
		if(hsmManager.isUsableAsTrustStore(PKCS11_CLIENT1)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_CLIENT1+"'; expected not usable as truststore");
		}
		if(hsmManager.isUsableAsTrustStore(PKCS11_CLIENT2)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_CLIENT2+"'; expected not usable as truststore");
		}
		if(!hsmManager.isUsableAsTrustStore(PKCS11_SERVER)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_SERVER+"'; expected usable as truststore");
		}
		if(!hsmManager.isUsableAsTrustStore(PKCS11_MODI_CLIENT)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_MODI_CLIENT+"'; expected usable as truststore");
		}
		
		if(!hsmManager.isUsableAsSecretKeyStore(PKCS11_CLIENT1)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_CLIENT1+"'; expected usable as secret key store");
		}
		if(hsmManager.isUsableAsSecretKeyStore(PKCS11_CLIENT2)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_CLIENT2+"'; expected not usable as secret key store");
		}
		if(hsmManager.isUsableAsSecretKeyStore(PKCS11_SERVER)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_SERVER+"'; expected not usable as secret key store");
		}
		if(hsmManager.isUsableAsSecretKeyStore(PKCS11_MODI_CLIENT)) {
			throw new UtilsException("PKCS11 Keystore '"+PKCS11_MODI_CLIENT+"'; expected not usable as secret key store");
		}
		
		KeyStore keystore = hsmManager.getKeystore(PKCS11_SERVER);
		
		_test(keystore, ALIAS_PKCS11_SERVER, PASSWORD, DN_PKCS11_SERVER);
		_test(keystore, ALIAS_PKCS11_SERVER, "", DN_PKCS11_SERVER); // la password in pkcs11 non esiste per gli oggetti interni
		
		_test(keystore, ALIAS_PKCS11_SERVER2, PASSWORD, DN_PKCS11_SERVER2);
		_test(keystore, ALIAS_PKCS11_SERVER2, "", DN_PKCS11_SERVER2); // la password in pkcs11 non esiste per gli oggetti interni
			
		
		keystore = hsmManager.getKeystore(PKCS11_CLIENT1);
		
		_test(keystore, ALIAS_PKCS11_CLIENT1, PASSWORD, DN_PKCS11_CLIENT1);
		_test(keystore, ALIAS_PKCS11_CLIENT1, "", DN_PKCS11_CLIENT1); // la password in pkcs11 non esiste per gli oggetti interni
		
		_testSymmetric(keystore, ALIAS_PKCS11_CLIENT_SYMMETRIC, PASSWORD);
		_testSymmetric(keystore, ALIAS_PKCS11_CLIENT_SYMMETRIC, ""); // la password in pkcs11 non esiste per gli oggetti interni
		
		
		keystore = hsmManager.getKeystore(PKCS11_CLIENT2);
		
		_test(keystore, ALIAS_PKCS11_CLIENT2, PASSWORD, DN_PKCS11_CLIENT2);
		_test(keystore, ALIAS_PKCS11_CLIENT2, "", DN_PKCS11_CLIENT2); // la password in pkcs11 non esiste per gli oggetti interni
		
		keystore = hsmManager.getKeystore(PKCS11_MODI_CLIENT);
		
		_test(keystore, ALIAS_PKCS11_MODI_CLIENT1, PASSWORD, DN_PKCS11_MODI_CLIENT1);
		_test(keystore, ALIAS_PKCS11_MODI_CLIENT1, "", DN_PKCS11_MODI_CLIENT1); // la password in pkcs11 non esiste per gli oggetti interni
		
		_test(keystore, ALIAS_PKCS11_MODI_CLIENT2, PASSWORD, DN_PKCS11_MODI_CLIENT2);
		_test(keystore, ALIAS_PKCS11_MODI_CLIENT2, "", DN_PKCS11_MODI_CLIENT2); // la password in pkcs11 non esiste per gli oggetti interni
		
		_test(keystore, ALIAS_PKCS11_MODI_CLIENT3, PASSWORD, DN_PKCS11_MODI_CLIENT3);
		_test(keystore, ALIAS_PKCS11_MODI_CLIENT3, "", DN_PKCS11_MODI_CLIENT3); // la password in pkcs11 non esiste per gli oggetti interni
		
		f.delete();
		
	}
	
	private static void _test(KeyStore keystore, String alias, String passwordPrivateKey, String subjectAtteso) throws UtilsException {
		
		PrivateKey privateKey = keystore.getPrivateKey(alias, passwordPrivateKey);
		if(privateKey==null) {
			throw new UtilsException("Private key not found");
		}
		System.out.println("PrivateKey class: "+privateKey.getClass().getName());
		System.out.println("PrivateKey ALGO: "+privateKey.getAlgorithm());
		
		Certificate cert = keystore.getCertificate(alias);
		if(cert==null) {
			throw new UtilsException("Public key not found");
		}
		
		X509Certificate x509 = (X509Certificate) cert;
		String subject = x509.getSubjectX500Principal().toString();
		System.out.println("Recuperato x509 "+subject);
		if(!subject.equals(subjectAtteso)) {
			throw new UtilsException("Subject recuperato differente da quello atteso: "+subjectAtteso);
		}
		
	}
	
	private static void _testSymmetric(KeyStore keystore, String alias, String passwordSecretKey) throws UtilsException {
		
		SecretKey secretKey = keystore.getSecretKey(alias, passwordSecretKey);
		if(secretKey==null) {
			throw new UtilsException("Secret key not found");
		}
		System.out.println("SecretKey class: "+secretKey.getClass().getName());
		System.out.println("SecretKey ALGO: "+secretKey.getAlgorithm());
		
	}
}
