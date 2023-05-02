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

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.Test;

/**
 * TestRemoteStore
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestRemoteStore {

	private static final String ID_TEST = "RemoteStore";
	
	private void logInCorso(String id) {
		TestLogger.info("Run test '"+ID_TEST+"."+id+"' ...");
	}
	private void logFinished(String id) {
		TestLogger.info("Run test '"+ID_TEST+"."+id+"' ok");
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".jwk"})
	public void testJWK() throws UtilsException{
		
		logInCorso("jwk");
		org.openspcoop2.utils.certificate.remote.test.RemoteStoreTest.testJWK();
		logFinished("jwk");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".publicKey"})
	public void testPublicKey() throws UtilsException{
		
		logInCorso("publicKey");
		org.openspcoop2.utils.certificate.remote.test.RemoteStoreTest.testPublicKey();
		logFinished("publicKey");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".x509"})
	public void testX509() throws UtilsException{
		
		logInCorso("x509");
		org.openspcoop2.utils.certificate.remote.test.RemoteStoreTest.testX509();
		logFinished("x509");
		
	}
	
}
