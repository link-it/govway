/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
import java.net.URISyntaxException;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.transport.http.HttpLibrary;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.test.HttpTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import jakarta.servlet.http.HttpServletResponse;

/**
 * TestHttp
 * 
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestHttp {

	private static final String ID_GROUP = "request";
	private static final String ID_TEST = "http";
	private static final HttpTest test = new HttpTest();
	
	@BeforeClass(alwaysRun = true)
	public void startServer() throws IOException {
		System.out.println("Starting HTTP mock server");
		HttpTest.startServers();
	}
	
	@AfterClass(alwaysRun = true)
	public void stopServer() {
		System.out.println("Stopping HTTP mock server");
		HttpTest.stopServers();
	}
	
	@DataProvider(name="libraryDP")
	public Object[][] libraryDP(){	
		return new Object[][] {
			{HttpLibrary.HTTP_CORE5},
			{HttpLibrary.HTTP_URL_CONNECTION}
		};
	}
	
	@DataProvider(name="redirectDP")
	public Object[][] redirectDP(){	
		return new Object[][] {
			{HttpLibrary.HTTP_CORE5, HttpServletResponse.SC_TEMPORARY_REDIRECT},
			{HttpLibrary.HTTP_URL_CONNECTION, HttpServletResponse.SC_TEMPORARY_REDIRECT},
			{HttpLibrary.HTTP_CORE5, HttpServletResponse.SC_MOVED_PERMANENTLY},
			{HttpLibrary.HTTP_URL_CONNECTION, HttpServletResponse.SC_MOVED_PERMANENTLY}
		};
	}
	
	@DataProvider(name="throttlingDP")
	public Object[][] throttlingDP(){	
		return new Object[][] {
			{HttpLibrary.HTTP_CORE5, 100, 100},
			{HttpLibrary.HTTP_URL_CONNECTION, 100, 100}
		};
	}
	
	@DataProvider(name="methodDP")
	public Object[][] methodDP(){	
		return test.methodsHttpDataProvider();
	}
	
	@DataProvider(name="paramDP")
	public Object[][] paramDP(){	
		return test.paramsHttpDataProvider();
	}
	
	@DataProvider(name="headerDP")
	public Object[][] headerDP(){	
		return test.headersHttpDataProvider();
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_GROUP,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST+".file"},dataProvider="libraryDP")
	public void testFile(HttpLibrary lib) throws UtilsException, IOException {
		System.out.println("Starting testFile");
		test.testFile(lib);
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_GROUP,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST+".proxy"},dataProvider="libraryDP")
	public void testProxy(HttpLibrary lib) throws UtilsException {
		System.out.println("Starting testProxy");
		test.testHttpProxy(lib);
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_GROUP,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST+".readTimeout"},dataProvider="libraryDP")
	public void testReadTimeout(HttpLibrary lib) throws UtilsException {
		System.out.println("Starting testReadTimeout");
		test.testReadTimeout(lib);
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_GROUP,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST+".redirect"},dataProvider="redirectDP")
	public void testRedirect(HttpLibrary lib, Integer redirectType) throws UtilsException, URISyntaxException {
		System.out.println("Starting testRedirect");
		test.testRedirect(lib, redirectType);
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_GROUP,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST+".throttling"},dataProvider="throttlingDP")
	public void testThrottling(HttpLibrary lib, int throttlingSize, int throttlingMs) throws UtilsException {
		System.out.println("Starting testThrottling");
		test.testThrottling(lib, throttlingSize, throttlingMs);
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_GROUP,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST+".method"},dataProvider="methodDP")
	public void testMethod(HttpRequest req, HttpResponse res) throws UtilsException {
		System.out.println("Starting testMethod");
		test.check(req, res);
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_GROUP,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST+".header"},dataProvider="headerDP")
	public void testHeader(HttpRequest req, HttpResponse res) throws UtilsException {
		System.out.println("Starting testHeader");
		test.check(req, res);
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_GROUP,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_GROUP+"."+ID_TEST+".param"},dataProvider="paramDP")
	public void testParam(HttpRequest req, HttpResponse res) throws UtilsException {
		System.out.println("Starting testParam");
		test.check(req, res);
	}
	
	
	
}
