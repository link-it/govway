/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
 * TestEncrypt
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestJWK {

	private static final String ID_TEST = "JWKs";
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST+".jwtSet"})
	public void testJWKset() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".jwtSet' ...");
		org.openspcoop2.utils.certificate.test.JWKTest.testJWKset();
		TestLogger.info("Run test '"+ID_TEST+".jwtSet' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST+".keystore"})
	public void testKeystore() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".keystore' ...");
		org.openspcoop2.utils.certificate.test.JWKTest.testKeystore();
		TestLogger.info("Run test '"+ID_TEST+".keystore' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST+".secretKey"})
	public void testSecretKey() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".secretKey' ...");
		org.openspcoop2.utils.certificate.test.JWKTest.testSecretKey();
		TestLogger.info("Run test '"+ID_TEST+".secretKey' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST+".privatePublicKey"})
	public void testPrivatePublicKey() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".privatePublicKey' ...");
		org.openspcoop2.utils.certificate.test.JWKTest.testPrivatePublicKey();
		TestLogger.info("Run test '"+ID_TEST+".privatePublicKey' ok");
		
	}

}
