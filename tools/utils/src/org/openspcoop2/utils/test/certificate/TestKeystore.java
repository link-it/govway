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
 * TestKeystore
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestKeystore {

	private static final String ID_TEST = "Keystore";
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".jks"})
	public void testPrivateKeyJKS() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".privateKey.jks' ...");
		org.openspcoop2.utils.certificate.test.KeystoreTest.testPrivateKeyInJKS();
		TestLogger.info("Run test '"+ID_TEST+".privateKey.jks' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".pkcs12"})
	public void testPrivateKeyPKCS12() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".privateKey.pkcs12' ...");
		org.openspcoop2.utils.certificate.test.KeystoreTest.testPrivateKeyInPKCS12();
		TestLogger.info("Run test '"+ID_TEST+".privateKey.pkcs12' ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".pkcs11"})
	public void testPrivateKeyPKCS11() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+".privateKey.pkcs11' ...");
		org.openspcoop2.utils.certificate.test.KeystoreTest.testPrivateKeyInPKCS11();
		TestLogger.info("Run test '"+ID_TEST+".privateKey.pkcs12' ok");
		
	}
}
