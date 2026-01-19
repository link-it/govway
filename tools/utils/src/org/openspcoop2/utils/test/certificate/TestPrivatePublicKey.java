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

package org.openspcoop2.utils.test.certificate;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.Test;

/**
 * TestKeystore
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestPrivatePublicKey {

	private static final String ID_TEST = "PrivatePublicKey";
	
	private void logInCorso(String id) {
		TestLogger.info("Run test '"+ID_TEST+"."+id+"' ...");
	}
	private void logFinished(String id) {
		TestLogger.info("Run test '"+ID_TEST+"."+id+"' ok");
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".privateKey.pkcs1.pem"})
	public void testPrivateKeyPkcs1PEM() throws UtilsException{
		
		logInCorso("privateKey.pkcs1.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPrivateKeyPkcs1PEM();
		logFinished("privateKey.pkcs1.pem");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".privateKey.pkcs1.der"})
	public void testPrivateKeyPkcs1DER() throws UtilsException{
		
		logInCorso("privateKey.pkcs1.der");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPrivateKeyPkcs1DER();
		logFinished("privateKey.pkcs1.der");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".privateKey.pkcs1_encrypted.pem"})
	public void testPrivateKeyPkcs1EncryptedPEM() throws UtilsException{
		
		logInCorso("privateKey.pkcs1_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPrivateKeyPkcs1EncryptedPEM();
		logFinished("privateKey.pkcs1_encrypted.pem");
		
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".privateKey.pkcs8.pem"})
	public void testPrivateKeyPkcs8PEM() throws UtilsException{
		
		logInCorso("privateKey.pkcs8.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPrivateKeyPkcs8PEM();
		logFinished("privateKey.pkcs8.pem");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".privateKey.pkcs8.der"})
	public void testPrivateKeyPkcs8DER() throws UtilsException{
		
		logInCorso("privateKey.pkcs8.der");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPrivateKeyPkcs8DER();
		logFinished("privateKey.pkcs8.der");
		
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".privateKey.pkcs8_encrypted.pem"})
	public void testPrivateKeyPkcs8EncryptedPEM() throws UtilsException{
		
		logInCorso("privateKey.pkcs8_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPrivateKeyPkcs8EncryptedPEM();
		logFinished("privateKey.pkcs8_encrypted.pem");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".privateKey.pkcs8_encrypted.der"})
	public void testPrivateKeyPkcs8EncryptedDER() throws UtilsException{
		
		logInCorso("privateKey.pkcs8_encrypted.der");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPrivateKeyPkcs8EncryptedDER();
		logFinished("privateKey.pkcs8_encrypted.der");
		
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".publicKey.pem"})
	public void testPublicKeyPEM() throws UtilsException{
		
		logInCorso("publicKey.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPublicKeyPEM();
		logFinished("publicKey.pem");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".publicKey.der"})
	public void testPublicKeyDER() throws UtilsException{
		
		logInCorso("publicKey.der");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPublicKeyDER();
		logFinished("publicKey.der");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".certificate.pem"})
	public void testCertificatePEM() throws UtilsException{
		
		logInCorso("certificate.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testCertificatePEM();
		logFinished("certificate.pem");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".certificate.der"})
	public void testCertificateDER() throws UtilsException{
		
		logInCorso("certificate.der");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testCertificateDER();
		logFinished("certificate.der");
		
	}

	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".privatePublicKey.pkcs1.pem"})
	public void testPrivatePublicKeyPkcs1PEM() throws UtilsException{
		
		logInCorso("privatePublicKey.pkcs1.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPrivatePublicKeyPkcs1PEM();
		logFinished("privatePublicKey.pkcs1.pem");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".publicPrivateKey.pkcs1.pem"})
	public void testPublicPrivateKeyPkcs1PEM() throws UtilsException{
		
		logInCorso("publicPrivateKey.pkcs1.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPublicPrivateKeyPkcs1PEM();
		logFinished("publicPrivateKey.pkcs1.pem");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".privatePublicKey.pkcs1_encrypted.pem"})
	public void testPrivatePublicKeyPkcs1EncryptedPEM() throws UtilsException{
		
		logInCorso("privatePublicKey.pkcs1_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPrivatePublicKeyPkcs1EncryptedPEM();
		logFinished("privatePublicKey.pkcs1_encrypted.pem");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".publicPrivateKey.pkcs1_encrypted.pem"})
	public void testPublicPrivateKeyPkcs1EncryptedPEM() throws UtilsException{
		
		logInCorso("publicPrivateKey.pkcs1_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPublicPrivateKeyPkcs1EncryptedPEM();
		logFinished("publicPrivateKey.pkcs1_encrypted.pem");
		
	}
	
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".privatePublicKey.pkcs8.pem"})
	public void testPrivatePublicKeyPkcs8PEM() throws UtilsException{
		
		logInCorso("privatePublicKey.pkcs8.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPrivatePublicKeyPkcs8PEM();
		logFinished("privatePublicKey.pkcs8.pem");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".publicPrivateKey.pkcs8.pem"})
	public void testPublicPrivateKeyPkcs8PEM() throws UtilsException{
		
		logInCorso("publicPrivateKey.pkcs8.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPublicPrivateKeyPkcs8PEM();
		logFinished("publicPrivateKey.pkcs8.pem");
		
	}
	

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".privatePublicKey.pkcs8_encrypted.pem"})
	public void testPrivatePublicKeyPkcs8EncryptedPEM() throws UtilsException{
		
		logInCorso("privatePublicKey.pkcs8_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPrivatePublicKeyPkcs8EncryptedPEM();
		logFinished("privatePublicKey.pkcs8_encrypted.pem");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".publicPrivateKey.pkcs8_encrypted.pem"})
	public void testPublicPrivateKeyPkcs8EncryptedPEM() throws UtilsException{
		
		logInCorso("publicPrivateKey.pkcs8_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPublicPrivateKeyPkcs8EncryptedPEM();
		logFinished("publicPrivateKey.pkcs8_encrypted.pem");
		
	}
	

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".privateCertificate.pkcs1.pem"})
	public void testPrivateCertificatePkcs1PEM() throws UtilsException{
		
		logInCorso("privateCertificate.pkcs1.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPrivateCertificatePkcs1PEM();
		logFinished("privateCertificate.pkcs1.pem");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".certificatePrivateKey.pkcs1.pem"})
	public void testCertificatePrivateKeyPkcs1PEM() throws UtilsException{
		
		logInCorso("certificatePrivateKey.pkcs1.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testCertificatePrivateKeyPkcs1PEM();
		logFinished("certificatePrivateKey.pkcs1.pem");
		
	}
	

	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".privateCertificates.pkcs8.pem"})
	public void testPrivateCertificatesPkcs8PEM() throws UtilsException{
		
		logInCorso("privateCertificates.pkcs8.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testPrivateCertificatesPkcs8PEM();
		logFinished("privateCertificates.pkcs8.pem");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".certificatesPrivateKey.pkcs8.pem"})
	public void testCertificatesPrivateKeyPkcs8PEM() throws UtilsException{

		logInCorso("certificatesPrivateKey.pkcs8.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testCertificatesPrivateKeyPkcs8PEM();
		logFinished("certificatesPrivateKey.pkcs8.pem");

	}

	// ========== EC (ECDSA) tests ==========

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.privateKey.sec1.pem"})
	public void testEcPrivateKeySec1PEM() throws UtilsException{

		logInCorso("ec.privateKey.sec1.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPrivateKeySec1PEM();
		logFinished("ec.privateKey.sec1.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.privateKey.sec1.der"})
	public void testEcPrivateKeySec1DER() throws UtilsException{

		logInCorso("ec.privateKey.sec1.der");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPrivateKeySec1DER();
		logFinished("ec.privateKey.sec1.der");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.privateKey.sec1_encrypted.pem"})
	public void testEcPrivateKeySec1EncryptedPEM() throws UtilsException{

		logInCorso("ec.privateKey.sec1_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPrivateKeySec1EncryptedPEM();
		logFinished("ec.privateKey.sec1_encrypted.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.privateKey.pkcs8.pem"})
	public void testEcPrivateKeyPkcs8PEM() throws UtilsException{

		logInCorso("ec.privateKey.pkcs8.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPrivateKeyPkcs8PEM();
		logFinished("ec.privateKey.pkcs8.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.privateKey.pkcs8.der"})
	public void testEcPrivateKeyPkcs8DER() throws UtilsException{

		logInCorso("ec.privateKey.pkcs8.der");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPrivateKeyPkcs8DER();
		logFinished("ec.privateKey.pkcs8.der");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.privateKey.pkcs8_encrypted.pem"})
	public void testEcPrivateKeyPkcs8EncryptedPEM() throws UtilsException{

		logInCorso("ec.privateKey.pkcs8_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPrivateKeyPkcs8EncryptedPEM();
		logFinished("ec.privateKey.pkcs8_encrypted.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.privateKey.pkcs8_encrypted.der"})
	public void testEcPrivateKeyPkcs8EncryptedDER() throws UtilsException{

		logInCorso("ec.privateKey.pkcs8_encrypted.der");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPrivateKeyPkcs8EncryptedDER();
		logFinished("ec.privateKey.pkcs8_encrypted.der");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.publicKey.pem"})
	public void testEcPublicKeyPEM() throws UtilsException{

		logInCorso("ec.publicKey.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPublicKeyPEM();
		logFinished("ec.publicKey.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.publicKey.der"})
	public void testEcPublicKeyDER() throws UtilsException{

		logInCorso("ec.publicKey.der");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPublicKeyDER();
		logFinished("ec.publicKey.der");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.privatePublicKey.sec1.pem"})
	public void testEcPrivatePublicKeySec1PEM() throws UtilsException{

		logInCorso("ec.privatePublicKey.sec1.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPrivatePublicKeySec1PEM();
		logFinished("ec.privatePublicKey.sec1.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.publicPrivateKey.sec1.pem"})
	public void testEcPublicPrivateKeySec1PEM() throws UtilsException{

		logInCorso("ec.publicPrivateKey.sec1.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPublicPrivateKeySec1PEM();
		logFinished("ec.publicPrivateKey.sec1.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.privatePublicKey.sec1_encrypted.pem"})
	public void testEcPrivatePublicKeySec1EncryptedPEM() throws UtilsException{

		logInCorso("ec.privatePublicKey.sec1_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPrivatePublicKeySec1EncryptedPEM();
		logFinished("ec.privatePublicKey.sec1_encrypted.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.publicPrivateKey.sec1_encrypted.pem"})
	public void testEcPublicPrivateKeySec1EncryptedPEM() throws UtilsException{

		logInCorso("ec.publicPrivateKey.sec1_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPublicPrivateKeySec1EncryptedPEM();
		logFinished("ec.publicPrivateKey.sec1_encrypted.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.privatePublicKey.pkcs8.pem"})
	public void testEcPrivatePublicKeyPkcs8PEM() throws UtilsException{

		logInCorso("ec.privatePublicKey.pkcs8.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPrivatePublicKeyPkcs8PEM();
		logFinished("ec.privatePublicKey.pkcs8.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.publicPrivateKey.pkcs8.pem"})
	public void testEcPublicPrivateKeyPkcs8PEM() throws UtilsException{

		logInCorso("ec.publicPrivateKey.pkcs8.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPublicPrivateKeyPkcs8PEM();
		logFinished("ec.publicPrivateKey.pkcs8.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.privatePublicKey.pkcs8_encrypted.pem"})
	public void testEcPrivatePublicKeyPkcs8EncryptedPEM() throws UtilsException{

		logInCorso("ec.privatePublicKey.pkcs8_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPrivatePublicKeyPkcs8EncryptedPEM();
		logFinished("ec.privatePublicKey.pkcs8_encrypted.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".ec.publicPrivateKey.pkcs8_encrypted.pem"})
	public void testEcPublicPrivateKeyPkcs8EncryptedPEM() throws UtilsException{

		logInCorso("ec.publicPrivateKey.pkcs8_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testEcPublicPrivateKeyPkcs8EncryptedPEM();
		logFinished("ec.publicPrivateKey.pkcs8_encrypted.pem");

	}

	// ========== RSA-PSS tests ==========

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".rsapss.privateKey.pkcs8.pem"})
	public void testRsaPssPrivateKeyPkcs8PEM() throws UtilsException{

		logInCorso("rsapss.privateKey.pkcs8.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testRsaPssPrivateKeyPkcs8PEM();
		logFinished("rsapss.privateKey.pkcs8.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".rsapss.privateKey.pkcs8.der"})
	public void testRsaPssPrivateKeyPkcs8DER() throws UtilsException{

		logInCorso("rsapss.privateKey.pkcs8.der");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testRsaPssPrivateKeyPkcs8DER();
		logFinished("rsapss.privateKey.pkcs8.der");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".rsapss.privateKey.pkcs8_encrypted.pem"})
	public void testRsaPssPrivateKeyPkcs8EncryptedPEM() throws UtilsException{

		logInCorso("rsapss.privateKey.pkcs8_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testRsaPssPrivateKeyPkcs8EncryptedPEM();
		logFinished("rsapss.privateKey.pkcs8_encrypted.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".rsapss.privateKey.pkcs8_encrypted.der"})
	public void testRsaPssPrivateKeyPkcs8EncryptedDER() throws UtilsException{

		logInCorso("rsapss.privateKey.pkcs8_encrypted.der");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testRsaPssPrivateKeyPkcs8EncryptedDER();
		logFinished("rsapss.privateKey.pkcs8_encrypted.der");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".rsapss.publicKey.pem"})
	public void testRsaPssPublicKeyPEM() throws UtilsException{

		logInCorso("rsapss.publicKey.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testRsaPssPublicKeyPEM();
		logFinished("rsapss.publicKey.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".rsapss.publicKey.der"})
	public void testRsaPssPublicKeyDER() throws UtilsException{

		logInCorso("rsapss.publicKey.der");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testRsaPssPublicKeyDER();
		logFinished("rsapss.publicKey.der");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".rsapss.privatePublicKey.pkcs8.pem"})
	public void testRsaPssPrivatePublicKeyPkcs8PEM() throws UtilsException{

		logInCorso("rsapss.privatePublicKey.pkcs8.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testRsaPssPrivatePublicKeyPkcs8PEM();
		logFinished("rsapss.privatePublicKey.pkcs8.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".rsapss.publicPrivateKey.pkcs8.pem"})
	public void testRsaPssPublicPrivateKeyPkcs8PEM() throws UtilsException{

		logInCorso("rsapss.publicPrivateKey.pkcs8.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testRsaPssPublicPrivateKeyPkcs8PEM();
		logFinished("rsapss.publicPrivateKey.pkcs8.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".rsapss.privatePublicKey.pkcs8_encrypted.pem"})
	public void testRsaPssPrivatePublicKeyPkcs8EncryptedPEM() throws UtilsException{

		logInCorso("rsapss.privatePublicKey.pkcs8_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testRsaPssPrivatePublicKeyPkcs8EncryptedPEM();
		logFinished("rsapss.privatePublicKey.pkcs8_encrypted.pem");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".rsapss.publicPrivateKey.pkcs8_encrypted.pem"})
	public void testRsaPssPublicPrivateKeyPkcs8EncryptedPEM() throws UtilsException{

		logInCorso("rsapss.publicPrivateKey.pkcs8_encrypted.pem");
		org.openspcoop2.utils.certificate.test.PrivatePublicKeyTest.testRsaPssPublicPrivateKeyPkcs8EncryptedPEM();
		logFinished("rsapss.publicPrivateKey.pkcs8_encrypted.pem");

	}

}
