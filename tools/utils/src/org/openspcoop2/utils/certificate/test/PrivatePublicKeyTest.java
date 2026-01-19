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

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.Arrays;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.certificate.PEMArchive;

/**
 * PrivatePublicKeyTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PrivatePublicKeyTest {

	public static void main(String[] args) throws Exception {

		testPrivateKeyPkcs1PEM();

		testPrivateKeyPkcs1DER();

		testPrivateKeyPkcs1EncryptedPEM();

		testPrivateKeyPkcs8PEM();

		testPrivateKeyPkcs8DER();

		testPrivateKeyPkcs8EncryptedPEM();

		testPrivateKeyPkcs8EncryptedDER();

		testPublicKeyPEM();

		testPublicKeyDER();

		testCertificatePEM();

		testCertificateDER();

		testPrivatePublicKeyPkcs1PEM();

		testPublicPrivateKeyPkcs1PEM();

		// RSA tests
		
		testPrivatePublicKeyPkcs1EncryptedPEM();

		testPublicPrivateKeyPkcs1EncryptedPEM();

		testPrivatePublicKeyPkcs8PEM();

		testPublicPrivateKeyPkcs8PEM();

		testPrivatePublicKeyPkcs8EncryptedPEM();

		testPublicPrivateKeyPkcs8EncryptedPEM();

		testPrivateCertificatePkcs1PEM();

		testCertificatePrivateKeyPkcs1PEM();

		testPrivateCertificatesPkcs8PEM();

		testCertificatesPrivateKeyPkcs8PEM();

		// EC (ECDSA) tests
		testEcPrivateKeySec1PEM();

		testEcPrivateKeySec1DER();

		testEcPrivateKeySec1EncryptedPEM();

		testEcPrivateKeyPkcs8PEM();

		testEcPrivateKeyPkcs8DER();

		testEcPrivateKeyPkcs8EncryptedPEM();

		testEcPrivateKeyPkcs8EncryptedDER();

		testEcPublicKeyPEM();

		testEcPublicKeyDER();

		testEcPrivatePublicKeySec1PEM();

		testEcPublicPrivateKeySec1PEM();

		testEcPrivatePublicKeySec1EncryptedPEM();

		testEcPublicPrivateKeySec1EncryptedPEM();

		testEcPrivatePublicKeyPkcs8PEM();

		testEcPublicPrivateKeyPkcs8PEM();

		testEcPrivatePublicKeyPkcs8EncryptedPEM();

		testEcPublicPrivateKeyPkcs8EncryptedPEM();

		// RSA-PSS tests
		testRsaPssPrivateKeyPkcs8PEM();

		testRsaPssPrivateKeyPkcs8DER();

		testRsaPssPrivateKeyPkcs8EncryptedPEM();

		testRsaPssPrivateKeyPkcs8EncryptedDER();

		testRsaPssPublicKeyPEM();

		testRsaPssPublicKeyDER();

		testRsaPssPrivatePublicKeyPkcs8PEM();

		testRsaPssPublicPrivateKeyPkcs8PEM();

		testRsaPssPrivatePublicKeyPkcs8EncryptedPEM();

		testRsaPssPublicPrivateKeyPkcs8EncryptedPEM();

		print("\n\nTestsuite finita");
	}

	private static void initBC() {
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
	}
	private static void releaseBC() {
		Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
	}
	
	private static void print(String msg) {
		System.out.println(msg);
	}
	
	private static final String RSA = "RSA";
	private static final String EC = "EC";
	private static final String ECDSA = "ECDSA";
	private static final String RSASSA_PSS = "RSASSA-PSS";
	private static final String PKCS8 = "PKCS#8";
	private static final String X509 = "X.509";
	private static final String PASSWORD = "123456";
	
	private static final String PRIVATE_KEY_NON_LETTA = "PrivateKey non letta";
	private static final String PUBLIC_KEY_NON_LETTA = "PublicKey non letta";
	
	private static final String COMPARE_PRIVATE_KEY = " -- compare con getPrivateKey";
	private static final String COMPARE_PEM_ARCHIVE = " -- compare con PEMArchive";
	
	private static void check(String atteso, String trovato) throws UtilsException {
		if(!atteso.equals(trovato)) {
			throw new UtilsException("Atteso algoritmo '"+atteso+"', trovato '"+trovato+"'");
		}
	}
	private static void checkEc(String trovato) throws UtilsException {
		// Accetta sia "EC" che "ECDSA" come algoritmo valido per chiavi EC
		if(!EC.equals(trovato) && !ECDSA.equals(trovato)) {
			throw new UtilsException("Atteso algoritmo '"+EC+"' o '"+ECDSA+"', trovato '"+trovato+"'");
		}
	}
	private static void checkPrivateKey(PrivateKey pKey, PrivateKey pKey2, boolean debug) throws UtilsException {
		if(!pKey.equals(pKey2)) {
			throw new UtilsException("PrivateKey lette differenti");
		}
		if(!Arrays.equals(pKey.getEncoded(), pKey2.getEncoded())) {
			print("P1: "+new String(pKey.getEncoded()));
			print("P2: "+new String(pKey2.getEncoded()));
			throw new UtilsException("PrivateKey lette differenti (encoded)");
		}
		if(!pKey.getFormat().equals(pKey2.getFormat())) {
			throw new UtilsException("PrivateKey lette differenti (format)");
		}
		if(!pKey.getAlgorithm().equals(pKey2.getAlgorithm())) {
			throw new UtilsException("PrivateKey lette differenti (algorithm)");
		}
		if(debug) {
			print("Read chiave privata con algoritmo '"+pKey.getAlgorithm()+"'");
		}
	}
	private static void checkPrivateKey(PrivateKey pKey, byte[] privateKey, boolean pem) throws UtilsException{
		checkPrivateKey(pKey, privateKey, null, pem);
	}
	private static void checkPrivateKey(PrivateKey pKey, byte[] privateKey, String password, boolean pem) throws UtilsException{
		
		print(COMPARE_PRIVATE_KEY);
		
		if(pKey==null) {
			throw new UtilsException(PRIVATE_KEY_NON_LETTA);
		}
		check(RSA, pKey.getAlgorithm());
		check(PKCS8, pKey.getFormat());
		
		if(password==null) {
			PrivateKey pKey2 = KeyUtils.getInstance().getPrivateKey(privateKey);
			if(pKey2==null) {
				throw new UtilsException(PRIVATE_KEY_NON_LETTA);
			}
			check(RSA, pKey2.getAlgorithm());
			check(PKCS8, pKey2.getFormat());
			
			checkPrivateKey(pKey, pKey2, false);
		}
		
		// verifico cmq il metodo con la password anche non fornendogliela, dovrebbe funzionare se l'archivio non e' protetto
		PrivateKey pKeyConPassword2 = KeyUtils.getInstance().getPrivateKey(privateKey, password);
		if(pKeyConPassword2==null) {
			throw new UtilsException(PRIVATE_KEY_NON_LETTA);
		}
		check(RSA, pKeyConPassword2.getAlgorithm());
		check(PKCS8, pKeyConPassword2.getFormat());
		
		checkPrivateKey(pKey, pKeyConPassword2, true);
		
		// controlli utilizzando PEMArchive
		if(!pem) {
			return;
		}
		
		print(COMPARE_PEM_ARCHIVE);
		
		if(password==null) {
			PEMArchive pemArchive = new PEMArchive(privateKey);
			PrivateKey pKey2 = pemArchive.getPrivateKey();
			if(pKey2==null) {
				throw new UtilsException(PRIVATE_KEY_NON_LETTA);
			}
			check(RSA, pKey2.getAlgorithm());
			check(PKCS8, pKey2.getFormat());
			
			checkPrivateKey(pKey, pKey2, false);
		}
		
		// verifico cmq il metodo con la password anche non fornendogliela, dovrebbe funzionare se l'archivio non e' protetto
		PEMArchive pemArchive = new PEMArchive(privateKey, password);
		pKeyConPassword2 = pemArchive.getPrivateKey();
		if(pKeyConPassword2==null) {
			throw new UtilsException(PRIVATE_KEY_NON_LETTA);
		}
		check(RSA, pKeyConPassword2.getAlgorithm());
		check(PKCS8, pKeyConPassword2.getFormat());
		
		checkPrivateKey(pKey, pKeyConPassword2, true);
	}
	
	private static void checkPublicKey(PublicKey pKey, byte[] publicKey, boolean pem, boolean certificate) throws UtilsException{
		checkPublicKey(pKey, publicKey, null, pem, certificate);
	}
	private static void checkPublicKey(PublicKey pKey, byte[] publicKey, String password, boolean pem, boolean certificate) throws UtilsException{
		
		print(COMPARE_PRIVATE_KEY);
		
		if(pKey==null) {
			throw new UtilsException(PUBLIC_KEY_NON_LETTA);
		}
		check(RSA, pKey.getAlgorithm());
		check(X509, pKey.getFormat());
		
		PublicKey pKey2 = KeyUtils.getInstance().getPublicKey(publicKey);
		if(pKey2==null) {
			throw new UtilsException(PUBLIC_KEY_NON_LETTA);
		}
		check(RSA, pKey2.getAlgorithm());
		check(X509, pKey2.getFormat());
		
		checkPublicKey(pKey, pKey2, true);
		
		// controlli utilizzando PEMArchive
		if(!pem) {
			return;
		}
		
		print(COMPARE_PEM_ARCHIVE);
		
		PEMArchive pemArchive = password==null ? new PEMArchive(new String(publicKey)) : new PEMArchive(new String(publicKey),password);
		if(certificate) {
			pKey2 = pemArchive.getCertificates().get(0).getCertificate().getPublicKey();
		}
		else {
			pKey2 = pemArchive.getPublicKey();
		}
		if(pKey2==null) {
			throw new UtilsException(PUBLIC_KEY_NON_LETTA);
		}
		check(RSA, pKey2.getAlgorithm());
		check(X509, pKey2.getFormat());
		
		checkPublicKey(pKey, pKey2, true);
	}
	private static void checkPublicKey(PublicKey pKey, PublicKey pKey2, boolean debug) throws UtilsException {
		if(!pKey.equals(pKey2)) {
			throw new UtilsException("PrivateKey lette differenti");
		}
		if(!Arrays.equals(pKey.getEncoded(), pKey2.getEncoded())) {
			print("P1: "+new String(pKey.getEncoded()));
			print("P2: "+new String(pKey2.getEncoded()));
			throw new UtilsException("PrivateKey lette differenti (encoded)");
		}
		if(!pKey.getFormat().equals(pKey2.getFormat())) {
			throw new UtilsException("PrivateKey lette differenti (format)");
		}
		if(!pKey.getAlgorithm().equals(pKey2.getAlgorithm())) {
			throw new UtilsException("PrivateKey lette differenti (algorithm)");
		}
		if(debug) {
			print("Read chiave pubblica con algoritmo '"+pKey.getAlgorithm()+"'");
		}
	}

	// ========== EC (ECDSA) helper methods ==========

	private static void checkPrivateKeyEc(PrivateKey pKey, byte[] privateKey, boolean pem) throws UtilsException{
		checkPrivateKeyEc(pKey, privateKey, null, pem);
	}
	private static void checkPrivateKeyEc(PrivateKey pKey, byte[] privateKey, String password, boolean pem) throws UtilsException{

		print(COMPARE_PRIVATE_KEY);

		if(pKey==null) {
			throw new UtilsException(PRIVATE_KEY_NON_LETTA);
		}
		checkEc(pKey.getAlgorithm());
		check(PKCS8, pKey.getFormat());

		if(password==null) {
			PrivateKey pKey2 = KeyUtils.getInstance(EC).getPrivateKey(privateKey);
			if(pKey2==null) {
				throw new UtilsException(PRIVATE_KEY_NON_LETTA);
			}
			checkEc(pKey2.getAlgorithm());
			check(PKCS8, pKey2.getFormat());

			checkPrivateKey(pKey, pKey2, false);
		}

		// verifico cmq il metodo con la password anche non fornendogliela, dovrebbe funzionare se l'archivio non e' protetto
		PrivateKey pKeyConPassword2 = KeyUtils.getInstance(EC).getPrivateKey(privateKey, password);
		if(pKeyConPassword2==null) {
			throw new UtilsException(PRIVATE_KEY_NON_LETTA);
		}
		checkEc(pKeyConPassword2.getAlgorithm());
		check(PKCS8, pKeyConPassword2.getFormat());

		checkPrivateKey(pKey, pKeyConPassword2, true);

		// controlli utilizzando PEMArchive
		if(!pem) {
			return;
		}

		print(COMPARE_PEM_ARCHIVE);

		if(password==null) {
			PEMArchive pemArchive = new PEMArchive(privateKey, KeyUtils.ALGO_EC, null);
			PrivateKey pKey2 = pemArchive.getPrivateKey();
			if(pKey2==null) {
				throw new UtilsException(PRIVATE_KEY_NON_LETTA);
			}
			checkEc(pKey2.getAlgorithm());
			check(PKCS8, pKey2.getFormat());

			checkPrivateKey(pKey, pKey2, false);
		}

		// verifico cmq il metodo con la password anche non fornendogliela, dovrebbe funzionare se l'archivio non e' protetto
		PEMArchive pemArchive = new PEMArchive(privateKey, KeyUtils.ALGO_EC, password);
		pKeyConPassword2 = pemArchive.getPrivateKey();
		if(pKeyConPassword2==null) {
			throw new UtilsException(PRIVATE_KEY_NON_LETTA);
		}
		checkEc(pKeyConPassword2.getAlgorithm());
		check(PKCS8, pKeyConPassword2.getFormat());

		checkPrivateKey(pKey, pKeyConPassword2, true);
	}

	private static void checkPublicKeyEc(PublicKey pKey, byte[] publicKey, boolean pem) throws UtilsException{
		checkPublicKeyEc(pKey, publicKey, null, pem);
	}
	private static void checkPublicKeyEc(PublicKey pKey, byte[] publicKey, String password, boolean pem) throws UtilsException{

		print(" -- compare con getPublicKey");

		if(pKey==null) {
			throw new UtilsException(PUBLIC_KEY_NON_LETTA);
		}
		checkEc(pKey.getAlgorithm());
		check(X509, pKey.getFormat());

		PublicKey pKey2 = KeyUtils.getInstance(EC).getPublicKey(publicKey);
		if(pKey2==null) {
			throw new UtilsException(PUBLIC_KEY_NON_LETTA);
		}
		checkEc(pKey2.getAlgorithm());
		check(X509, pKey2.getFormat());

		checkPublicKey(pKey, pKey2, true);

		// controlli utilizzando PEMArchive
		if(!pem) {
			return;
		}

		print(COMPARE_PEM_ARCHIVE);

		PEMArchive pemArchive = new PEMArchive(new String(publicKey), KeyUtils.ALGO_EC, password);
		pKey2 = pemArchive.getPublicKey();
		if(pKey2==null) {
			throw new UtilsException(PUBLIC_KEY_NON_LETTA);
		}
		checkEc(pKey2.getAlgorithm());
		check(X509, pKey2.getFormat());

		checkPublicKey(pKey, pKey2, true);
	}

	// ========== RSA-PSS helper methods ==========

	private static void checkPrivateKeyRsaPss(PrivateKey pKey, byte[] privateKey, boolean pem) throws UtilsException{
		checkPrivateKeyRsaPss(pKey, privateKey, null, pem);
	}
	private static void checkPrivateKeyRsaPss(PrivateKey pKey, byte[] privateKey, String password, boolean pem) throws UtilsException{

		print(COMPARE_PRIVATE_KEY);

		if(pKey==null) {
			throw new UtilsException(PRIVATE_KEY_NON_LETTA);
		}
		check(RSASSA_PSS, pKey.getAlgorithm());
		check(PKCS8, pKey.getFormat());

		if(password==null) {
			PrivateKey pKey2 = KeyUtils.getInstance(RSASSA_PSS).getPrivateKey(privateKey);
			if(pKey2==null) {
				throw new UtilsException(PRIVATE_KEY_NON_LETTA);
			}
			check(RSASSA_PSS, pKey2.getAlgorithm());
			check(PKCS8, pKey2.getFormat());

			checkPrivateKey(pKey, pKey2, false);
		}

		// verifico cmq il metodo con la password anche non fornendogliela, dovrebbe funzionare se l'archivio non e' protetto
		PrivateKey pKeyConPassword2 = KeyUtils.getInstance(RSASSA_PSS).getPrivateKey(privateKey, password);
		if(pKeyConPassword2==null) {
			throw new UtilsException(PRIVATE_KEY_NON_LETTA);
		}
		check(RSASSA_PSS, pKeyConPassword2.getAlgorithm());
		check(PKCS8, pKeyConPassword2.getFormat());

		checkPrivateKey(pKey, pKeyConPassword2, true);

		// controlli utilizzando PEMArchive
		if(!pem) {
			return;
		}

		print(COMPARE_PEM_ARCHIVE);

		if(password==null) {
			PEMArchive pemArchive = new PEMArchive(privateKey);
			PrivateKey pKey2 = pemArchive.getPrivateKey();
			if(pKey2==null) {
				throw new UtilsException(PRIVATE_KEY_NON_LETTA);
			}
			check(RSASSA_PSS, pKey2.getAlgorithm());
			check(PKCS8, pKey2.getFormat());

			checkPrivateKey(pKey, pKey2, false);
		}

		// verifico cmq il metodo con la password anche non fornendogliela, dovrebbe funzionare se l'archivio non e' protetto
		PEMArchive pemArchive = new PEMArchive(privateKey, password);
		pKeyConPassword2 = pemArchive.getPrivateKey();
		if(pKeyConPassword2==null) {
			throw new UtilsException(PRIVATE_KEY_NON_LETTA);
		}
		check(RSASSA_PSS, pKeyConPassword2.getAlgorithm());
		check(PKCS8, pKeyConPassword2.getFormat());

		checkPrivateKey(pKey, pKeyConPassword2, true);
	}

	private static void checkPublicKeyRsaPss(PublicKey pKey, byte[] publicKey, boolean pem) throws UtilsException{
		checkPublicKeyRsaPss(pKey, publicKey, null, pem);
	}
	private static void checkPublicKeyRsaPss(PublicKey pKey, byte[] publicKey, String password, boolean pem) throws UtilsException{

		print(" -- compare con getPublicKey");

		if(pKey==null) {
			throw new UtilsException(PUBLIC_KEY_NON_LETTA);
		}
		check(RSASSA_PSS, pKey.getAlgorithm());
		check(X509, pKey.getFormat());

		PublicKey pKey2 = KeyUtils.getInstance(RSASSA_PSS).getPublicKey(publicKey);
		if(pKey2==null) {
			throw new UtilsException(PUBLIC_KEY_NON_LETTA);
		}
		check(RSASSA_PSS, pKey2.getAlgorithm());
		check(X509, pKey2.getFormat());

		checkPublicKey(pKey, pKey2, true);

		// controlli utilizzando PEMArchive
		if(!pem) {
			return;
		}

		print(COMPARE_PEM_ARCHIVE);

		PEMArchive pemArchive = password==null ? new PEMArchive(new String(publicKey)) : new PEMArchive(new String(publicKey),password);
		pKey2 = pemArchive.getPublicKey();
		if(pKey2==null) {
			throw new UtilsException(PUBLIC_KEY_NON_LETTA);
		}
		check(RSASSA_PSS, pKey2.getAlgorithm());
		check(X509, pKey2.getFormat());

		checkPublicKey(pKey, pKey2, true);
	}

	public static void testPrivateKeyPkcs1PEM() throws UtilsException {

		print("========================= private key pkcs1 PEM ==============================");

		initBC();
		try {
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs1.privateKey.pem"));
			PrivateKey pKey = KeyUtils.getInstance().readPKCS1PrivateKeyPEMFormat(privateKey);
			checkPrivateKey(pKey, privateKey, true);
		}finally {
			releaseBC();
		}
	}
	
	public static void testPrivateKeyPkcs1DER() throws UtilsException {

		print("========================= private key pkcs1 DER ==============================");

		initBC();
		try {
		
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs1.privateKey.der"));
			PrivateKey pKey = KeyUtils.getInstance().readPKCS8PrivateKeyDERFormat(privateKey); // e' un pkcs8 nel formato DER
			checkPrivateKey(pKey, privateKey, false);
		}finally {
			releaseBC();
		}
	}
	
	public static void testPrivateKeyPkcs1EncryptedPEM() throws UtilsException {

		print("========================= private key pkcs1 encrypted PEM ==============================");

		initBC();
		try {
		
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs1_encrypted.privateKey.pem"));
			PrivateKey pKey = KeyUtils.getInstance().readPKCS1EncryptedPrivateKeyPEMFormat(privateKey, PASSWORD);
			checkPrivateKey(pKey, privateKey, PASSWORD, true);
		}finally {
			releaseBC();
		}
	}
	
	public static void testPrivateKeyPkcs8PEM() throws UtilsException {

		print("========================= private key pkcs8 PEM ==============================");

		initBC();
		try {
		
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs8.privateKey.pem"));
			PrivateKey pKey = KeyUtils.getInstance().readPKCS8PrivateKeyPEMFormat(privateKey);
			checkPrivateKey(pKey, privateKey, true);
		}finally {
			releaseBC();
		}
	}
	
	public static void testPrivateKeyPkcs8DER() throws UtilsException {

		print("========================= private key pkcs8 DER ==============================");

		initBC();
		try {
		
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs8.privateKey.der"));
			PrivateKey pKey = KeyUtils.getInstance().readPKCS8PrivateKeyDERFormat(privateKey);
			checkPrivateKey(pKey, privateKey, false);
		}finally {
			releaseBC();
		}
	}
	
	public static void testPrivateKeyPkcs8EncryptedPEM() throws UtilsException {

		print("========================= private key pkcs8 encrypted PEM ==============================");

		initBC();
		try {
		
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs8_encrypted.privateKey.pem"));
			PrivateKey pKey = KeyUtils.getInstance().readPKCS8EncryptedPrivateKeyPEMFormat(privateKey, PASSWORD);
			checkPrivateKey(pKey, privateKey, PASSWORD, true);
		}finally {
			releaseBC();
		}
	}
	
	public static void testPrivateKeyPkcs8EncryptedDER() throws UtilsException {

		print("========================= private key pkcs8 encrypted DER ==============================");

		initBC();
		try {
		
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs8_encrypted.privateKey.der"));
			PrivateKey pKey = KeyUtils.getInstance().readPKCS8EncryptedPrivateKeyDERFormat(privateKey, PASSWORD);
			checkPrivateKey(pKey, privateKey, PASSWORD, false);
		}finally {
			releaseBC();
		}
	}
	
	public static void testPublicKeyPEM() throws UtilsException {

		print("========================= public key PEM ==============================");

		initBC();
		try {
		
			byte [] publicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.publicKey.pem"));
			PublicKey pKey = KeyUtils.getInstance().readPublicKeyPEMFormat(publicKey);
			checkPublicKey(pKey, publicKey, true, false);
		}finally {
			releaseBC();
		}
	}
	
	public static void testPublicKeyDER() throws UtilsException {

		print("========================= public key DER ==============================");

		initBC();
		try {
		
			byte [] publicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.publicKey.der"));
			PublicKey pKey = KeyUtils.getInstance().readPublicKeyDERFormat(publicKey);
			checkPublicKey(pKey, publicKey, false, false);
		}finally {
			releaseBC();
		}
	}
	
	public static void testCertificatePEM() throws UtilsException {

		print("========================= certificate PEM ==============================");

		initBC();
		try {
		
			byte [] publicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"govway_test.pem"));
			PublicKey pKey = KeyUtils.getInstance().readCertificate(publicKey);
			checkPublicKey(pKey, publicKey, true, true);
		}finally {
			releaseBC();
		}
	}
	
	public static void testCertificateDER() throws UtilsException {

		print("========================= certificate DER ==============================");

		initBC();
		try {
		
			byte [] publicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"govway_test.cer"));
			PublicKey pKey = KeyUtils.getInstance().readCertificate(publicKey);
			checkPublicKey(pKey, publicKey, false, true);
		}finally {
			releaseBC();
		}
	}
	
	public static void testPrivatePublicKeyPkcs1PEM() throws UtilsException {

		print("========================= private public key pkcs1 PEM ==============================");

		initBC();
		try {
		
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs1.privatePublicKey.pem"));
			
			PrivateKey privateKey = KeyUtils.getInstance().readPKCS1PrivateKeyPEMFormat(privatePublicKey);
			checkPrivateKey(privateKey, privatePublicKey, true);
	
			PublicKey publicKey = KeyUtils.getInstance().readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKey(publicKey, privatePublicKey, true, false);
		}finally {
			releaseBC();
		}
	}	
	
	public static void testPublicPrivateKeyPkcs1PEM() throws UtilsException {

		print("========================= public private key pkcs1 PEM ==============================");

		initBC();
		try {
		
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs1.publicPrivateKey.pem"));
			
			PrivateKey privateKey = KeyUtils.getInstance().readPKCS1PrivateKeyPEMFormat(privatePublicKey);
			checkPrivateKey(privateKey, privatePublicKey, true);
	
			PublicKey publicKey = KeyUtils.getInstance().readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKey(publicKey, privatePublicKey, true, false);
		}finally {
			releaseBC();
		}
	}	
	
	public static void testPrivatePublicKeyPkcs1EncryptedPEM() throws UtilsException {

		print("========================= private public key pkcs1 encrypted PEM ==============================");

		initBC();
		try {
		
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs1_encrypted.privatePublicKey.pem"));
			
			PrivateKey privateKey = KeyUtils.getInstance().readPKCS1EncryptedPrivateKeyPEMFormat(privatePublicKey, PASSWORD);
			checkPrivateKey(privateKey, privatePublicKey, PASSWORD, true);
	
			PublicKey publicKey = KeyUtils.getInstance().readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKey(publicKey, privatePublicKey, PASSWORD, true, false);
		}finally {
			releaseBC();
		}
	}	
	
	public static void testPublicPrivateKeyPkcs1EncryptedPEM() throws UtilsException {

		print("========================= public private key pkcs1 encrypted PEM ==============================");

		initBC();
		try {
		
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs1_encrypted.publicPrivateKey.pem"));
			
			PrivateKey privateKey = KeyUtils.getInstance().readPKCS1EncryptedPrivateKeyPEMFormat(privatePublicKey, PASSWORD);
			checkPrivateKey(privateKey, privatePublicKey, PASSWORD, true);
	
			PublicKey publicKey = KeyUtils.getInstance().readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKey(publicKey, privatePublicKey, PASSWORD, true, false);
		}finally {
			releaseBC();
		}
	}	
	
	public static void testPrivatePublicKeyPkcs8PEM() throws UtilsException {

		print("========================= private public key pkcs8 PEM ==============================");

		initBC();
		try {
		
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs8.privatePublicKey.pem"));
			
			PrivateKey privateKey = KeyUtils.getInstance().readPKCS8PrivateKeyPEMFormat(privatePublicKey);
			checkPrivateKey(privateKey, privatePublicKey, true);
	
			PublicKey publicKey = KeyUtils.getInstance().readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKey(publicKey, privatePublicKey, true, false);
		}finally {
			releaseBC();
		}
	}	
	
	public static void testPublicPrivateKeyPkcs8PEM() throws UtilsException {

		print("========================= public private key pkcs8 PEM ==============================");

		initBC();
		try {
		
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs8.publicPrivateKey.pem"));
			
			PrivateKey privateKey = KeyUtils.getInstance().readPKCS8PrivateKeyPEMFormat(privatePublicKey);
			checkPrivateKey(privateKey, privatePublicKey, true);
	
			PublicKey publicKey = KeyUtils.getInstance().readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKey(publicKey, privatePublicKey, true, false);
		}finally {
			releaseBC();
		}
	}	
	
	public static void testPrivatePublicKeyPkcs8EncryptedPEM() throws UtilsException {

		print("========================= private public key pkcs8 encrypted PEM ==============================");

		initBC();
		try {
		
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs8_encrypted.privatePublicKey.pem"));
			
			PrivateKey privateKey = KeyUtils.getInstance().readPKCS8EncryptedPrivateKeyPEMFormat(privatePublicKey, PASSWORD);
			checkPrivateKey(privateKey, privatePublicKey, PASSWORD, true);
	
			PublicKey publicKey = KeyUtils.getInstance().readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKey(publicKey, privatePublicKey, PASSWORD, true, false);
		}finally {
			releaseBC();
		}
	}	
	
	public static void testPublicPrivateKeyPkcs8EncryptedPEM() throws UtilsException {

		print("========================= public private key pkcs8 encrypted PEM ==============================");

		initBC();
		try {
		
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs8_encrypted.publicPrivateKey.pem"));
			
			PrivateKey privateKey = KeyUtils.getInstance().readPKCS8EncryptedPrivateKeyPEMFormat(privatePublicKey, PASSWORD);
			checkPrivateKey(privateKey, privatePublicKey, PASSWORD, true);
	
			PublicKey publicKey = KeyUtils.getInstance().readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKey(publicKey, privatePublicKey, PASSWORD, true, false);
		}finally {
			releaseBC();
		}
	}

	public static void testPrivateCertificatePkcs1PEM() throws UtilsException {

		print("========================= private key e certificate pkcs1 PEM ==============================");

		initBC();
		try {
		
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs1.privateCertificate.pem"));
			
			PrivateKey privateKey = KeyUtils.getInstance().readPKCS1PrivateKeyPEMFormat(privatePublicKey);
			checkPrivateKey(privateKey, privatePublicKey, true);
	
			PublicKey publicKey = KeyUtils.getInstance().readCertificate(privatePublicKey);
			checkPublicKey(publicKey, privatePublicKey, true, true);
		}finally {
			releaseBC();
		}
	}	
	
	public static void testCertificatePrivateKeyPkcs1PEM() throws UtilsException {

		print("========================= certificate e private key pkcs1 PEM ==============================");

		initBC();
		try {
		
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs1.certificatePrivateKey.pem"));
			
			PrivateKey privateKey = KeyUtils.getInstance().readPKCS1PrivateKeyPEMFormat(privatePublicKey);
			checkPrivateKey(privateKey, privatePublicKey, true);
	
			PublicKey publicKey = KeyUtils.getInstance().readCertificate(privatePublicKey);
			checkPublicKey(publicKey, privatePublicKey, true, true);
		}finally {
			releaseBC();
		}
	}
	
	public static void testPrivateCertificatesPkcs8PEM() throws UtilsException {

		print("========================= private key e certificates pkcs8 PEM ==============================");
		
		initBC();
		try {
		
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs8.privateCertificates.pem"));
			
			PrivateKey privateKey = KeyUtils.getInstance().readPKCS8PrivateKeyPEMFormat(privatePublicKey);
			checkPrivateKey(privateKey, privatePublicKey, true);
	
			PublicKey publicKey = KeyUtils.getInstance().readCertificate(privatePublicKey);
			checkPublicKey(publicKey, privatePublicKey, true, true);
		}finally {
			releaseBC();
		}
	}	
	
	public static void testCertificatesPrivateKeyPkcs8PEM() throws UtilsException {

		print("========================= certificates e private key pkcs8 PEM ==============================");

		initBC();
		try {

			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs8.certificatesPrivateKey.pem"));

			PrivateKey privateKey = KeyUtils.getInstance().readPKCS8PrivateKeyPEMFormat(privatePublicKey);
			checkPrivateKey(privateKey, privatePublicKey, true);

			PublicKey publicKey = KeyUtils.getInstance().readCertificate(privatePublicKey);
			checkPublicKey(publicKey, privatePublicKey, true, true);
		}finally {
			releaseBC();
		}
	}

	// ========== EC (ECDSA) test methods ==========

	public static void testEcPrivateKeySec1PEM() throws UtilsException {

		print("========================= EC private key SEC1 PEM ==============================");

		initBC();
		try {
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.sec1.privateKey.pem"));
			PrivateKey pKey = KeyUtils.getInstance(EC).readPKCS1PrivateKeyPEMFormat(privateKey);
			checkPrivateKeyEc(pKey, privateKey, true);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPrivateKeySec1DER() throws UtilsException {

		print("========================= EC private key SEC1 DER ==============================");

		initBC();
		try {
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.sec1.privateKey.der"));
			PrivateKey pKey = KeyUtils.getInstance(EC).readSEC1PrivateKeyDERFormat(privateKey); // e' un SEC1 nel formato DER
			checkPrivateKeyEc(pKey, privateKey, false);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPrivateKeySec1EncryptedPEM() throws UtilsException {

		print("========================= EC private key SEC1 encrypted PEM ==============================");

		initBC();
		try {
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.sec1_encrypted.privateKey.pem"));
			PrivateKey pKey = KeyUtils.getInstance(EC).readPKCS1EncryptedPrivateKeyPEMFormat(privateKey, PASSWORD);
			checkPrivateKeyEc(pKey, privateKey, PASSWORD, true);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPrivateKeyPkcs8PEM() throws UtilsException {

		print("========================= EC private key PKCS8 PEM ==============================");

		initBC();
		try {
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.pkcs8.privateKey.pem"));
			PrivateKey pKey = KeyUtils.getInstance(EC).readPKCS8PrivateKeyPEMFormat(privateKey);
			checkPrivateKeyEc(pKey, privateKey, true);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPrivateKeyPkcs8DER() throws UtilsException {

		print("========================= EC private key PKCS8 DER ==============================");

		initBC();
		try {
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.pkcs8.privateKey.der"));
			PrivateKey pKey = KeyUtils.getInstance(EC).readPKCS8PrivateKeyDERFormat(privateKey);
			checkPrivateKeyEc(pKey, privateKey, false);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPrivateKeyPkcs8EncryptedPEM() throws UtilsException {

		print("========================= EC private key PKCS8 encrypted PEM ==============================");

		initBC();
		try {
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.pkcs8_encrypted.privateKey.pem"));
			PrivateKey pKey = KeyUtils.getInstance(EC).readPKCS8EncryptedPrivateKeyPEMFormat(privateKey, PASSWORD);
			checkPrivateKeyEc(pKey, privateKey, PASSWORD, true);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPrivateKeyPkcs8EncryptedDER() throws UtilsException {

		print("========================= EC private key PKCS8 encrypted DER ==============================");

		initBC();
		try {
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.pkcs8_encrypted.privateKey.der"));
			PrivateKey pKey = KeyUtils.getInstance(EC).readPKCS8EncryptedPrivateKeyDERFormat(privateKey, PASSWORD);
			checkPrivateKeyEc(pKey, privateKey, PASSWORD, false);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPublicKeyPEM() throws UtilsException {

		print("========================= EC public key PEM ==============================");

		initBC();
		try {
			byte [] publicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.publicKey.pem"));
			PublicKey pKey = KeyUtils.getInstance(EC).readPublicKeyPEMFormat(publicKey);
			checkPublicKeyEc(pKey, publicKey, true);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPublicKeyDER() throws UtilsException {

		print("========================= EC public key DER ==============================");

		initBC();
		try {
			byte [] publicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.publicKey.der"));
			PublicKey pKey = KeyUtils.getInstance(EC).readPublicKeyDERFormat(publicKey);
			checkPublicKeyEc(pKey, publicKey, false);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPrivatePublicKeySec1PEM() throws UtilsException {

		print("========================= EC private public key SEC1 PEM ==============================");

		initBC();
		try {
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.sec1.privatePublicKey.pem"));

			PrivateKey privateKey = KeyUtils.getInstance(EC).readPKCS1PrivateKeyPEMFormat(privatePublicKey);
			checkPrivateKeyEc(privateKey, privatePublicKey, true);

			PublicKey publicKey = KeyUtils.getInstance(EC).readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKeyEc(publicKey, privatePublicKey, true);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPublicPrivateKeySec1PEM() throws UtilsException {

		print("========================= EC public private key SEC1 PEM ==============================");

		initBC();
		try {
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.sec1.publicPrivateKey.pem"));

			PrivateKey privateKey = KeyUtils.getInstance(EC).readPKCS1PrivateKeyPEMFormat(privatePublicKey);
			checkPrivateKeyEc(privateKey, privatePublicKey, true);

			PublicKey publicKey = KeyUtils.getInstance(EC).readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKeyEc(publicKey, privatePublicKey, true);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPrivatePublicKeySec1EncryptedPEM() throws UtilsException {

		print("========================= EC private public key SEC1 encrypted PEM ==============================");

		initBC();
		try {
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.sec1_encrypted.privatePublicKey.pem"));

			PrivateKey privateKey = KeyUtils.getInstance(EC).readPKCS1EncryptedPrivateKeyPEMFormat(privatePublicKey, PASSWORD);
			checkPrivateKeyEc(privateKey, privatePublicKey, PASSWORD, true);

			PublicKey publicKey = KeyUtils.getInstance(EC).readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKeyEc(publicKey, privatePublicKey, PASSWORD, true);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPublicPrivateKeySec1EncryptedPEM() throws UtilsException {

		print("========================= EC public private key SEC1 encrypted PEM ==============================");

		initBC();
		try {
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.sec1_encrypted.publicPrivateKey.pem"));

			PrivateKey privateKey = KeyUtils.getInstance(EC).readPKCS1EncryptedPrivateKeyPEMFormat(privatePublicKey, PASSWORD);
			checkPrivateKeyEc(privateKey, privatePublicKey, PASSWORD, true);

			PublicKey publicKey = KeyUtils.getInstance(EC).readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKeyEc(publicKey, privatePublicKey, PASSWORD, true);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPrivatePublicKeyPkcs8PEM() throws UtilsException {

		print("========================= EC private public key PKCS8 PEM ==============================");

		initBC();
		try {
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.pkcs8.privatePublicKey.pem"));

			PrivateKey privateKey = KeyUtils.getInstance(EC).readPKCS8PrivateKeyPEMFormat(privatePublicKey);
			checkPrivateKeyEc(privateKey, privatePublicKey, true);

			PublicKey publicKey = KeyUtils.getInstance(EC).readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKeyEc(publicKey, privatePublicKey, true);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPublicPrivateKeyPkcs8PEM() throws UtilsException {

		print("========================= EC public private key PKCS8 PEM ==============================");

		initBC();
		try {
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.pkcs8.publicPrivateKey.pem"));

			PrivateKey privateKey = KeyUtils.getInstance(EC).readPKCS8PrivateKeyPEMFormat(privatePublicKey);
			checkPrivateKeyEc(privateKey, privatePublicKey, true);

			PublicKey publicKey = KeyUtils.getInstance(EC).readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKeyEc(publicKey, privatePublicKey, true);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPrivatePublicKeyPkcs8EncryptedPEM() throws UtilsException {

		print("========================= EC private public key PKCS8 encrypted PEM ==============================");

		initBC();
		try {
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.pkcs8_encrypted.privatePublicKey.pem"));

			PrivateKey privateKey = KeyUtils.getInstance(EC).readPKCS8EncryptedPrivateKeyPEMFormat(privatePublicKey, PASSWORD);
			checkPrivateKeyEc(privateKey, privatePublicKey, PASSWORD, true);

			PublicKey publicKey = KeyUtils.getInstance(EC).readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKeyEc(publicKey, privatePublicKey, PASSWORD, true);
		}finally {
			releaseBC();
		}
	}

	public static void testEcPublicPrivateKeyPkcs8EncryptedPEM() throws UtilsException {

		print("========================= EC public private key PKCS8 encrypted PEM ==============================");

		initBC();
		try {
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.ec.pkcs8_encrypted.publicPrivateKey.pem"));

			PrivateKey privateKey = KeyUtils.getInstance(EC).readPKCS8EncryptedPrivateKeyPEMFormat(privatePublicKey, PASSWORD);
			checkPrivateKeyEc(privateKey, privatePublicKey, PASSWORD, true);

			PublicKey publicKey = KeyUtils.getInstance(EC).readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKeyEc(publicKey, privatePublicKey, PASSWORD, true);
		}finally {
			releaseBC();
		}
	}

	// ========== RSA-PSS test methods ==========

	public static void testRsaPssPrivateKeyPkcs8PEM() throws UtilsException {

		print("========================= RSA-PSS private key PKCS8 PEM ==============================");

		initBC();
		try {
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsapss.pkcs8.privateKey.pem"));
			PrivateKey pKey = KeyUtils.getInstance(RSASSA_PSS).readPKCS8PrivateKeyPEMFormat(privateKey);
			checkPrivateKeyRsaPss(pKey, privateKey, true);
		}finally {
			releaseBC();
		}
	}

	public static void testRsaPssPrivateKeyPkcs8DER() throws UtilsException {

		print("========================= RSA-PSS private key PKCS8 DER ==============================");

		initBC();
		try {
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsapss.pkcs8.privateKey.der"));
			PrivateKey pKey = KeyUtils.getInstance(RSASSA_PSS).readPKCS8PrivateKeyDERFormat(privateKey);
			checkPrivateKeyRsaPss(pKey, privateKey, false);
		}finally {
			releaseBC();
		}
	}

	public static void testRsaPssPrivateKeyPkcs8EncryptedPEM() throws UtilsException {

		print("========================= RSA-PSS private key PKCS8 encrypted PEM ==============================");

		initBC();
		try {
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsapss.pkcs8_encrypted.privateKey.pem"));
			PrivateKey pKey = KeyUtils.getInstance(RSASSA_PSS).readPKCS8EncryptedPrivateKeyPEMFormat(privateKey, PASSWORD);
			checkPrivateKeyRsaPss(pKey, privateKey, PASSWORD, true);
		}finally {
			releaseBC();
		}
	}

	public static void testRsaPssPrivateKeyPkcs8EncryptedDER() throws UtilsException {

		print("========================= RSA-PSS private key PKCS8 encrypted DER ==============================");

		initBC();
		try {
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsapss.pkcs8_encrypted.privateKey.der"));
			PrivateKey pKey = KeyUtils.getInstance(RSASSA_PSS).readPKCS8EncryptedPrivateKeyDERFormat(privateKey, PASSWORD);
			checkPrivateKeyRsaPss(pKey, privateKey, PASSWORD, false);
		}finally {
			releaseBC();
		}
	}

	public static void testRsaPssPublicKeyPEM() throws UtilsException {

		print("========================= RSA-PSS public key PEM ==============================");

		initBC();
		try {
			byte [] publicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsapss.publicKey.pem"));
			PublicKey pKey = KeyUtils.getInstance(RSASSA_PSS).readPublicKeyPEMFormat(publicKey);
			checkPublicKeyRsaPss(pKey, publicKey, true);
		}finally {
			releaseBC();
		}
	}

	public static void testRsaPssPublicKeyDER() throws UtilsException {

		print("========================= RSA-PSS public key DER ==============================");

		initBC();
		try {
			byte [] publicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsapss.publicKey.der"));
			PublicKey pKey = KeyUtils.getInstance(RSASSA_PSS).readPublicKeyDERFormat(publicKey);
			checkPublicKeyRsaPss(pKey, publicKey, false);
		}finally {
			releaseBC();
		}
	}

	public static void testRsaPssPrivatePublicKeyPkcs8PEM() throws UtilsException {

		print("========================= RSA-PSS private public key PKCS8 PEM ==============================");

		initBC();
		try {
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsapss.pkcs8.privatePublicKey.pem"));

			PrivateKey privateKey = KeyUtils.getInstance(RSASSA_PSS).readPKCS8PrivateKeyPEMFormat(privatePublicKey);
			checkPrivateKeyRsaPss(privateKey, privatePublicKey, true);

			PublicKey publicKey = KeyUtils.getInstance(RSASSA_PSS).readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKeyRsaPss(publicKey, privatePublicKey, true);
		}finally {
			releaseBC();
		}
	}

	public static void testRsaPssPublicPrivateKeyPkcs8PEM() throws UtilsException {

		print("========================= RSA-PSS public private key PKCS8 PEM ==============================");

		initBC();
		try {
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsapss.pkcs8.publicPrivateKey.pem"));

			PrivateKey privateKey = KeyUtils.getInstance(RSASSA_PSS).readPKCS8PrivateKeyPEMFormat(privatePublicKey);
			checkPrivateKeyRsaPss(privateKey, privatePublicKey, true);

			PublicKey publicKey = KeyUtils.getInstance(RSASSA_PSS).readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKeyRsaPss(publicKey, privatePublicKey, true);
		}finally {
			releaseBC();
		}
	}

	public static void testRsaPssPrivatePublicKeyPkcs8EncryptedPEM() throws UtilsException {

		print("========================= RSA-PSS private public key PKCS8 encrypted PEM ==============================");

		initBC();
		try {
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsapss.pkcs8_encrypted.privatePublicKey.pem"));

			PrivateKey privateKey = KeyUtils.getInstance(RSASSA_PSS).readPKCS8EncryptedPrivateKeyPEMFormat(privatePublicKey, PASSWORD);
			checkPrivateKeyRsaPss(privateKey, privatePublicKey, PASSWORD, true);

			PublicKey publicKey = KeyUtils.getInstance(RSASSA_PSS).readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKeyRsaPss(publicKey, privatePublicKey, PASSWORD, true);
		}finally {
			releaseBC();
		}
	}

	public static void testRsaPssPublicPrivateKeyPkcs8EncryptedPEM() throws UtilsException {

		print("========================= RSA-PSS public private key PKCS8 encrypted PEM ==============================");

		initBC();
		try {
			byte [] privatePublicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsapss.pkcs8_encrypted.publicPrivateKey.pem"));

			PrivateKey privateKey = KeyUtils.getInstance(RSASSA_PSS).readPKCS8EncryptedPrivateKeyPEMFormat(privatePublicKey, PASSWORD);
			checkPrivateKeyRsaPss(privateKey, privatePublicKey, PASSWORD, true);

			PublicKey publicKey = KeyUtils.getInstance(RSASSA_PSS).readPublicKeyPEMFormat(privatePublicKey);
			checkPublicKeyRsaPss(publicKey, privatePublicKey, PASSWORD, true);
		}finally {
			releaseBC();
		}
	}
}
