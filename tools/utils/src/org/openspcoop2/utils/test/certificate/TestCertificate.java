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

package org.openspcoop2.utils.test.certificate;

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.Test;

/**
 * TestCertificate
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestCertificate {

	private static final String ID_TEST = "Certificate";
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".jks"})
	public void testJKS() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".jks' ...");
		org.openspcoop2.utils.certificate.test.CertificateTest.testJKS();
		TestLogger.info("Run test '"+ID_TEST+".jks' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".pkcs12"})
	public void testPKCS12() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".pkcs12' ...");
		org.openspcoop2.utils.certificate.test.CertificateTest.testPKCS12();
		TestLogger.info("Run test '"+ID_TEST+".pkcs12' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".der"})
	public void testDER() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".der' ...");
		org.openspcoop2.utils.certificate.test.CertificateTest.testDER();
		TestLogger.info("Run test '"+ID_TEST+".der' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".pem"})
	public void testPEM() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".pem' ...");
		org.openspcoop2.utils.certificate.test.CertificateTest.testPEM();
		TestLogger.info("Run test '"+ID_TEST+".pem' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".multipleCerts"})
	public void testMultipleCerts() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".multipleCerts' ...");
		org.openspcoop2.utils.certificate.test.CertificateTest.testMoreCertsJKS();
		TestLogger.info("Run test '"+ID_TEST+".multipleCerts' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".multiplePrivatePublicKeys"})
	public void testMultiplePrivatePublicKeys() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".multiplePrivatePublicKeys' ...");
		org.openspcoop2.utils.certificate.test.CertificateTest.testMultiplePrivatePublicJKS();
		TestLogger.info("Run test '"+ID_TEST+".multiplePrivatePublicKeys' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".compareDifferentFormat"})
	public void testCompareDifferentFormat() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".compareDifferentFormat' ...");
		org.openspcoop2.utils.certificate.test.CertificateTest.testTraFormatiDifferente();
		TestLogger.info("Run test '"+ID_TEST+".compareDifferentFormat' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".compareDifferentFormat_multipleOU"})
	public void testCompareDifferentFormat_multipleOU() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".compareDifferentFormat_multipleOU' ...");
		org.openspcoop2.utils.certificate.test.CertificateTest.testTraFormatiDifferente_MultipleOU();
		TestLogger.info("Run test '"+ID_TEST+".compareDifferentFormat_multipleOU' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".compareDifferentFormat_multipleOU_specialChar"})
	public void testCompareDifferentFormat_multipleOU_specialChar() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".compareDifferentFormat_multipleOU_specialChar' ...");
		org.openspcoop2.utils.certificate.test.CertificateTest.testTraFormatiDifferente_MultipleOU_specialChar();
		TestLogger.info("Run test '"+ID_TEST+".compareDifferentFormat_multipleOU_specialChar' ok");
		
	}
	
}
