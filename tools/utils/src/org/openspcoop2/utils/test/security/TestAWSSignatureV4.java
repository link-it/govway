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
package org.openspcoop2.utils.test.security;

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.Test;

/**
 * TestAWSSignatureV4 — wrapper TestNG per l'implementazione
 * {@link org.openspcoop2.utils.security.test.AWSSignatureV4Test}.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class TestAWSSignatureV4 {

	private static final String ID_TEST = "AWSSignatureV4";

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testSigningKeyDerivation"})
	public void testSigningKeyDerivation() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".testSigningKeyDerivation' ...");
		org.openspcoop2.utils.security.test.AWSSignatureV4Test.testSigningKeyDerivation();
		TestLogger.info("Run test '"+ID_TEST+".testSigningKeyDerivation' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testIamListUsersSignature"})
	public void testIamListUsersSignature() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".testIamListUsersSignature' ...");
		org.openspcoop2.utils.security.test.AWSSignatureV4Test.testIamListUsersSignature();
		TestLogger.info("Run test '"+ID_TEST+".testIamListUsersSignature' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testSessionTokenIncluded"})
	public void testSessionTokenIncluded() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".testSessionTokenIncluded' ...");
		org.openspcoop2.utils.security.test.AWSSignatureV4Test.testSessionTokenIncluded();
		TestLogger.info("Run test '"+ID_TEST+".testSessionTokenIncluded' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testCanonicalQueryStringSorting"})
	public void testCanonicalQueryStringSorting() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".testCanonicalQueryStringSorting' ...");
		org.openspcoop2.utils.security.test.AWSSignatureV4Test.testCanonicalQueryStringSorting();
		TestLogger.info("Run test '"+ID_TEST+".testCanonicalQueryStringSorting' ok");
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testEmptyPayloadHash"})
	public void testEmptyPayloadHash() throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".testEmptyPayloadHash' ...");
		org.openspcoop2.utils.security.test.AWSSignatureV4Test.testEmptyPayloadHash();
		TestLogger.info("Run test '"+ID_TEST+".testEmptyPayloadHash' ok");
	}

}
