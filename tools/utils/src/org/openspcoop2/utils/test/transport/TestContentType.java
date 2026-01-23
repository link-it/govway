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

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.openspcoop2.utils.transport.http.test.ContentTypeTest;
import org.testng.annotations.Test;

/**
 * TestContentType
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestContentType {

	private static final String ID_TEST = "ContentType";

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".equalsString"})
	public void testEqualsString() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".equalsString' ...");
		ContentTypeTest.testEqualsString();
		TestLogger.info("Run test '"+ID_TEST+".equalsString' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".equalsList"})
	public void testEqualsList() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".equalsList' ...");
		ContentTypeTest.testEqualsList();
		TestLogger.info("Run test '"+ID_TEST+".equalsList' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".validateContentType"})
	public void testValidateContentType() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".validateContentType' ...");
		ContentTypeTest.testValidateContentType();
		TestLogger.info("Run test '"+ID_TEST+".validateContentType' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".readBaseType"})
	public void testReadBaseType() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".readBaseType' ...");
		ContentTypeTest.testReadBaseType();
		TestLogger.info("Run test '"+ID_TEST+".readBaseType' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".readPrimaryType"})
	public void testReadPrimaryType() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".readPrimaryType' ...");
		ContentTypeTest.testReadPrimaryType();
		TestLogger.info("Run test '"+ID_TEST+".readPrimaryType' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".readSubType"})
	public void testReadSubType() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".readSubType' ...");
		ContentTypeTest.testReadSubType();
		TestLogger.info("Run test '"+ID_TEST+".readSubType' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".readCharset"})
	public void testReadCharset() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".readCharset' ...");
		ContentTypeTest.testReadCharset();
		TestLogger.info("Run test '"+ID_TEST+".readCharset' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".normalizeToRfc7230"})
	public void testNormalizeToRfc7230() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".normalizeToRfc7230' ...");
		ContentTypeTest.testNormalizeToRfc7230();
		TestLogger.info("Run test '"+ID_TEST+".normalizeToRfc7230' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".buildContentType"})
	public void testBuildContentType() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".buildContentType' ...");
		ContentTypeTest.testBuildContentType();
		TestLogger.info("Run test '"+ID_TEST+".buildContentType' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".isMultipart"})
	public void testIsMultipart() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".isMultipart' ...");
		ContentTypeTest.testIsMultipart();
		TestLogger.info("Run test '"+ID_TEST+".isMultipart' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".readMultipartBoundary"})
	public void testReadMultipartBoundary() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".readMultipartBoundary' ...");
		ContentTypeTest.testReadMultipartBoundary();
		TestLogger.info("Run test '"+ID_TEST+".readMultipartBoundary' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".isMatch"})
	public void testIsMatch() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".isMatch' ...");
		ContentTypeTest.testIsMatch();
		TestLogger.info("Run test '"+ID_TEST+".isMatch' ok");
	}
}
