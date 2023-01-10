/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * TestOCSP
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestOCSP {

	private static final String ID_TEST = "OCSP";
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".checkGovWay"})
	public void testGovWay() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".checkGovWay' ...");
		org.openspcoop2.utils.certificate.ocsp.test.OCSPTest.checkGovWay();
		TestLogger.info("Run test '"+ID_TEST+".checkGovWay' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".checkGoogle"})
	public void testGoogle() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".checkGoogle' ...");
		org.openspcoop2.utils.certificate.ocsp.test.OCSPTest.checkGoogle();
		TestLogger.info("Run test '"+ID_TEST+".checkGoogle' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".checkAlternativeCrlCheck"})
	public void testAlternativeCrlCheck() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".checkAlternativeCrlCheck' ...");
		org.openspcoop2.utils.certificate.ocsp.test.OCSPTest.checkAlternativeCrlCheck();
		TestLogger.info("Run test '"+ID_TEST+".checkAlternativeCrlCheck' ok");
		
	}
		
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".checkOCSPResponse_signedByResponderCertificate_case2"})
	@Parameters({"opensslCmd","waitStartupServerMs"})
	public void testOCSPResponse_signedByResponderCertificate_case2(String opensslCmd, String waitStartupServerMs) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".checkOCSPResponse_signedByResponderCertificate_case2' ...");
		org.openspcoop2.utils.certificate.ocsp.test.OCSPTest.checkOCSPResponse_signedByResponderCertificate_case2(opensslCmd, waitStartupServerMs);
		TestLogger.info("Run test '"+ID_TEST+".checkOCSPResponse_signedByResponderCertificate_case2' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".checkOCSPResponse_signedByResponderCertificate_case2_differentNonce"})
	@Parameters({"opensslCmd","waitStartupServerMs"})
	public void testOCSPResponse_signedByResponderCertificate_case2_differentNonce(String opensslCmd, String waitStartupServerMs) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".checkOCSPResponse_signedByResponderCertificate_case2_differentNonce' ...");
		org.openspcoop2.utils.certificate.ocsp.test.OCSPTest.checkOCSPResponse_signedByResponderCertificate_case2_differentNonce(opensslCmd, waitStartupServerMs);
		TestLogger.info("Run test '"+ID_TEST+".checkOCSPResponse_signedByResponderCertificate_case2_differentNonce' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".checkOCSPResponse_signedByResponderCertificate_case3"})
	@Parameters({"opensslCmd","waitStartupServerMs"})
	public void testOCSPResponse_signedByResponderCertificate_case3(String opensslCmd, String waitStartupServerMs) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".checkOCSPResponse_signedByResponderCertificate_case3' ...");
		org.openspcoop2.utils.certificate.ocsp.test.OCSPTest.checkOCSPResponse_signedByResponderCertificate_case3(opensslCmd, waitStartupServerMs);
		TestLogger.info("Run test '"+ID_TEST+".checkOCSPResponse_signedByResponderCertificate_case3' ok");
		
	}
	
}
