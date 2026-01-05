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
package org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509.Utils;

/**
* WSSecurityTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class WSSecurityKeystoreSenzaPasswordTest extends ConfigLoader {

	private static final String api = "TestWSSecurityKeystoreSenzaPassword";
	
	public static final String DN_SOGGETTO1 = "CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT,1.2.840.113549.1.9.1=#160d61706f6c69406c696e6b2e6974";
			
	@Test
	public void signature() throws Exception {
		Utils.testSoap11(logCore, api, "signature", 
				null, null, 
				null, DN_SOGGETTO1.replaceAll(" ", ""),  DN_SOGGETTO1.replaceAll(" ", ""));
	}

	@Test
	public void encrypt() throws Exception {
		Utils.testSoap11(logCore, api, "encrypt", 
				null, null, 
				null, DN_SOGGETTO1.replaceAll(" ", ""),  DN_SOGGETTO1.replaceAll(" ", ""));
	}
	
	@Test
	public void saml_bearer() throws Exception {
		Utils.testSoap11(logCore, api, "saml-bearer", 
				null, null, 
				null, "saml2:Assertion",  DN_SOGGETTO1.replaceAll(" ", ""));
	}
	
	@Test
	public void saml_holder_of_key() throws Exception {
		Utils.testSoap11(logCore, api, "saml-holder-of-key", 
				null, null, 
				null, DN_SOGGETTO1.replaceAll(" ", ""),  DN_SOGGETTO1.replaceAll(" ", ""));
	}
	
	@Test
	public void saml_sender_vouches() throws Exception {
		Utils.testSoap11(logCore, api, "saml-sender-vouches", 
				null, null, 
				null, DN_SOGGETTO1.replaceAll(" ", ""),  DN_SOGGETTO1.replaceAll(" ", ""));
	}
}