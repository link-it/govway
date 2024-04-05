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
	
}
