/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

	// Keystore PKCS12 con cifratura 'legacy' (pbeWithSHA1And3-KeyTripleDES-CBC / pbeWithSHA1And40BitRC2-CBC, MAC SHA-1)
	public static final String FILE_PKCS12_LEGACY = "govway_test.p12";
	// Keystore PKCS12 con cifratura moderna (PBES2 / PBKDF2 / AES-256-CBC), default di OpenSSL 3 e di keytool dal JDK 16
	public static final String FILE_PKCS12_PBES2 = "govway_test_fromp12.p12";
	// Keystore PKCS12 cifrato con un algoritmo non fornito da SunJCE (PBES2 / CAMELLIA-256-CBC), leggibile solo tramite BouncyCastle
	public static final String FILE_PKCS12_CAMELLIA = "govway_test_camellia.p12";
	// Truststore PKCS12 costruito con 'keytool -importcert': i certificati sono marcati con l'attributo 'trustedKeyUsage' e vengono esposti come 'trustedCertEntry'
	public static final String FILE_TRUSTSTORE_PKCS12 = "govway_test_truststore.p12";
	// Truststore PKCS12 costruito con 'openssl pkcs12 -export -nokeys': i certificati NON sono marcati e non vengono esposti da java
	public static final String FILE_TRUSTSTORE_PKCS12_OPENSSL = "govway_test_truststore_openssl.p12";

	private static final String PROVIDER_SUN = "SUN";
	private static final String PROVIDER_BC = org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;
	
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

		testPrivateKeyInPKCS12ModernEncryption();

		testPrivateKeyInPKCS12ModernEncryptionWithBouncyCastle();

		testPrivateKeyInPKCS12LegacyEncryptionWithBouncyCastle();

		testPrivateKeyInPKCS12WithBouncyCastleWithoutFallback();

		testPrivateKeyInPKCS12BouncyCastleOnlyEncryption();

		testTruststoreInPKCS12();

		testTruststoreInPKCS12WithoutTrustedKeyUsage();

		testPrivateKeyInPKCS11();
		
		System.out.println("Testsuite terminata");

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

	public static void testPrivateKeyInPKCS12ModernEncryption() throws Exception {

		System.out.println("========================= PKCS12 (PBES2/AES-256) ==============================");

		// Un keystore PKCS12 cifrato con gli algoritmi moderni deve essere letto direttamente dall'implementazione del jdk,
		// senza necessita' di ricorrere al fallback su BouncyCastle presente in KeystoreUtils
		KeyStore keystore = _readKeystore(FILE_PKCS12_PBES2);
		_testProvider(keystore, FILE_PKCS12_PBES2, PROVIDER_SUN);
		_test(keystore, ALIAS_1, PASSWORD, DN_1);

	}

	public static void testPrivateKeyInPKCS12ModernEncryptionWithBouncyCastle() throws Exception {

		System.out.println("========================= PKCS12 (PBES2/AES-256) con BouncyCastle ==============================");

		// Il provider BouncyCastle viene registrato davanti a SunJCE, come avviene a runtime in tutti i processi GovWay.
		// La lettura del keystore deve continuare ad essere servita dall'implementazione del jdk: se il provider BouncyCastle
		// intercetta il nome generico 'PBE' utilizzato internamente da sun.security.pkcs12.PKCS12KeyStore sul ramo PBES2,
		// la password viene codificata secondo la convenzione PKCS#12 anziche' UTF-8 e la lettura fallisce con BadPaddingException
		initBouncyCastle();
		try {

			KeyStore keystore = _readKeystore(FILE_PKCS12_PBES2);
			_testProvider(keystore, FILE_PKCS12_PBES2, PROVIDER_SUN);
			_test(keystore, ALIAS_1, PASSWORD, DN_1);

		}finally {
			releaseBouncyCastle();
		}

	}

	public static void testPrivateKeyInPKCS12LegacyEncryptionWithBouncyCastle() throws Exception {

		System.out.println("========================= PKCS12 (legacy) con BouncyCastle ==============================");

		initBouncyCastle();
		try {

			KeyStore keystore = _readKeystore(FILE_PKCS12_LEGACY);
			_testProvider(keystore, FILE_PKCS12_LEGACY, PROVIDER_SUN);
			_test(keystore, ALIAS_1, PASSWORD, DN_1);

		}finally {
			releaseBouncyCastle();
		}

	}

	public static void testPrivateKeyInPKCS12WithBouncyCastleWithoutFallback() throws Exception {

		System.out.println("========================= PKCS12 con BouncyCastle, senza fallback ==============================");

		// Verifica la lettura tramite la sola istruzione 'java.security.KeyStore.getInstance(tipo)', cioe' come la effettuano
		// gli application server (connettori https) e le librerie di terze parti, che non dispongono del fallback su BouncyCastle
		// implementato in KeystoreUtils
		initBouncyCastle();
		try {

			for (String file : new String[] {FILE_PKCS12_LEGACY, FILE_PKCS12_PBES2}) {

				java.security.KeyStore keystore = java.security.KeyStore.getInstance(ArchiveType.PKCS12.name());
				try(java.io.InputStream is = KeystoreTest.class.getResourceAsStream(PREFIX+file)){
					keystore.load(is, PASSWORD.toCharArray());
				}catch(Exception e) {
					throw new UtilsException("Lettura del keystore '"+file+"' non riuscita tramite il provider risolto in modo generico: "+e.getMessage(),e);
				}

				System.out.println("Keystore '"+file+"' letto tramite il provider '"+keystore.getProvider().getName()+"'");

				if(!keystore.containsAlias(ALIAS_1)) {
					throw new UtilsException("Alias '"+ALIAS_1+"' non trovato nel keystore '"+file+"'");
				}
				if(!keystore.isKeyEntry(ALIAS_1)) {
					throw new UtilsException("Alias '"+ALIAS_1+"' nel keystore '"+file+"'; attesa una entry contenente una chiave privata");
				}

			}

		}finally {
			releaseBouncyCastle();
		}

	}

	public static void testPrivateKeyInPKCS12BouncyCastleOnlyEncryption() throws Exception {

		System.out.println("========================= PKCS12 (PBES2/CAMELLIA-256) ==============================");

		// Verifica il fallback su BouncyCastle implementato in KeystoreUtils: il keystore e' cifrato con un algoritmo
		// non fornito da SunJCE, quindi la lettura tramite il provider risolto in modo generico deve fallire
		initBouncyCastle();
		try {

			java.security.KeyStore keystoreKo = java.security.KeyStore.getInstance(ArchiveType.PKCS12.name());
			try(java.io.InputStream is = KeystoreTest.class.getResourceAsStream(PREFIX+FILE_PKCS12_CAMELLIA)){
				keystoreKo.load(is, PASSWORD.toCharArray());
				throw new UtilsException("Attesa una eccezione durante la lettura del keystore '"+FILE_PKCS12_CAMELLIA+"' tramite il provider risolto in modo generico");
			}catch(UtilsException e) {
				throw e;
			}catch(Exception e) {
				System.out.println("Lettura del keystore '"+FILE_PKCS12_CAMELLIA+"' tramite il provider risolto in modo generico fallita come atteso: "+e.getMessage());
			}

			KeyStore keystore = _readKeystore(FILE_PKCS12_CAMELLIA);
			_testProvider(keystore, FILE_PKCS12_CAMELLIA, PROVIDER_BC);
			_test(keystore, ALIAS_1, PASSWORD, DN_1);

		}finally {
			releaseBouncyCastle();
		}

	}

	public static void testTruststoreInPKCS12() throws Exception {

		System.out.println("========================= PKCS12 truststore ==============================");

		// Il truststore deve essere leggibile sia nella configurazione base sia con il provider BouncyCastle registrato
		_testTruststore(FILE_TRUSTSTORE_PKCS12, ALIAS_1, DN_1);

		initBouncyCastle();
		try {
			_testTruststore(FILE_TRUSTSTORE_PKCS12, ALIAS_1, DN_1);
		}finally {
			releaseBouncyCastle();
		}

	}

	public static void testTruststoreInPKCS12WithoutTrustedKeyUsage() throws Exception {

		System.out.println("========================= PKCS12 truststore, senza attributo trustedKeyUsage ==============================");

		// Un truststore prodotto con 'openssl pkcs12 -export -nokeys' non contiene l'attributo 'trustedKeyUsage'
		// (oid 2.16.840.1.113894.746875.1.1) richiesto da java per esporre un certificato come 'trustedCertEntry'.
		// Il file viene caricato senza errori, ma risulta privo di alias: il test fissa il comportamento in modo che,
		// se una versione futura del jdk dovesse modificarlo, la differenza venga rilevata
		KeyStore keystore = _readKeystore(FILE_TRUSTSTORE_PKCS12_OPENSSL);

		java.util.List<String> aliases = new java.util.ArrayList<>();
		java.util.Enumeration<String> en = keystore.aliases();
		while (en.hasMoreElements()) {
			aliases.add(en.nextElement());
		}

		System.out.println("Truststore '"+FILE_TRUSTSTORE_PKCS12_OPENSSL+"' letto tramite il provider '"+keystore.getKeystoreProvider().getName()+"'; alias rilevati: "+aliases);

		if(!aliases.isEmpty()) {
			throw new UtilsException("Truststore '"+FILE_TRUSTSTORE_PKCS12_OPENSSL+"'; attesa nessuna entry poiche' i certificati non sono marcati con l'attributo 'trustedKeyUsage', rilevati invece gli alias: "+aliases);
		}

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
	
	public static void initBouncyCastle() {
		// registra il provider nella medesima modalita' utilizzata a runtime da GovWay
		Utilities.addBouncyCastleAfterSun(true);
	}
	public static void releaseBouncyCastle() {
		java.security.Security.removeProvider(PROVIDER_BC);
	}

	private static KeyStore _readKeystore(String file) throws UtilsException {
		return new KeyStore(Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(PREFIX+file)), ArchiveType.PKCS12.name(), PASSWORD);
	}

	private static void _testProvider(KeyStore keystore, String file, String providerAtteso) throws UtilsException {

		java.security.Provider provider = keystore.getKeystoreProvider();
		if(provider==null) {
			throw new UtilsException("Keystore '"+file+"'; provider non individuato");
		}

		System.out.println("Keystore '"+file+"' letto tramite il provider '"+provider.getName()+"'");

		if(!providerAtteso.equals(provider.getName())) {
			throw new UtilsException("Keystore '"+file+"'; atteso il provider '"+providerAtteso+"', rilevato invece il provider '"+provider.getName()+"'");
		}

	}

	private static void _testTruststore(String file, String alias, String subjectAtteso) throws UtilsException {

		KeyStore keystore = _readKeystore(file);

		System.out.println("Truststore '"+file+"' letto tramite il provider '"+keystore.getKeystoreProvider().getName()+"'");

		if(!keystore.existsAlias(alias)) {
			throw new UtilsException("Truststore '"+file+"'; alias '"+alias+"' non trovato");
		}

		try {
			if(!keystore.getKeystore().isCertificateEntry(alias)) {
				throw new UtilsException("Truststore '"+file+"'; alias '"+alias+"' non risulta una entry di tipo 'trustedCertEntry'");
			}
		}catch(java.security.KeyStoreException e) {
			throw new UtilsException(e.getMessage(),e);
		}

		Certificate cert = keystore.getCertificate(alias);
		if(cert==null) {
			throw new UtilsException("Truststore '"+file+"'; certificato non trovato per l'alias '"+alias+"'");
		}

		X509Certificate x509 = (X509Certificate) cert;
		String subject = x509.getSubjectX500Principal().toString();
		System.out.println("Recuperato x509 "+subject);
		if(!subject.equals(subjectAtteso)) {
			throw new UtilsException("Truststore '"+file+"'; subject recuperato differente da quello atteso: "+subjectAtteso);
		}

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
