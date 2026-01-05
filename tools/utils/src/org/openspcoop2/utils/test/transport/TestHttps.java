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
package org.openspcoop2.utils.test.transport;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.transport.http.HttpLibrary;
import org.openspcoop2.utils.transport.http.test.HttpTest;
import org.openspcoop2.utils.transport.http.test.HttpsTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestHttps
 * 
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestHttps {

	private static final String ID_GROUP = "request";
	private static final String ID_TEST = "https";
	private static final HttpsTest test = new HttpsTest();
	
	
	@BeforeClass(alwaysRun = true)
	public void startServer() throws IOException {
		HttpTest.startServers();
	}
	
	@AfterClass(alwaysRun = true)
	public void stopServer() {
		HttpTest.stopServers();
	}
	
	@DataProvider(name="libraryDP")
	public Object[][] libraryDP(){	
		return new Object[][] {
			{HttpLibrary.HTTP_CORE5},
			{HttpLibrary.HTTP_URL_CONNECTION}
		};
	}
	
	@DataProvider(name="libraryPathDP")
	public Object[][] libraryPathDP(){	
		return new Object[][] {
			{HttpLibrary.HTTP_CORE5, true},
			{HttpLibrary.HTTP_URL_CONNECTION, true},
			{HttpLibrary.HTTP_CORE5, false},
			{HttpLibrary.HTTP_URL_CONNECTION, false}
		};
	}
	
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_GROUP,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST, Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST+".auth"},dataProvider="libraryPathDP")
	public void testAuth(HttpLibrary lib, boolean usePath) throws UtilsException, IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException {
		test.testAuth(lib, usePath);
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_GROUP,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST, Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST+".https"},dataProvider="libraryDP")
	public void testHttps(HttpLibrary lib) throws UtilsException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
		test.testHttps(lib);
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_GROUP,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST, Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST+".trust"},dataProvider="libraryPathDP")
	public void testTrust(HttpLibrary lib, boolean usePath) throws UtilsException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
		test.testTrust(lib, usePath);
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_GROUP,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST, Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST+".trustAll"},dataProvider="libraryPathDP")
	public void testTrustAll(HttpLibrary lib, boolean usePath) throws UtilsException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
		test.testTrustAll(lib, usePath);
	}
}
