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
package org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.utils.certificate.test.KeystoreTest;

/**
* TlsTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class XmlSecurityTest extends ConfigLoader {

	private final static String api = "TestXmlSecurity";
	
	@Test
	public void signature() throws Exception {
		Utils.testXml(logCore, api, "signature", 
				null, null, 
				null, KeystoreTest.DN_PKCS11_CLIENT1.replaceAll(" ", ""),  KeystoreTest.DN_PKCS11_SERVER.replaceAll(" ", ""));
	}
	
	@Test
	public void encrypt() throws Exception {
		Utils.testXml(logCore, api, "encrypt", 
				null, null, 
				null, "xenc:EncryptedKey",  "xenc:EncryptedKey");
	}
		
	
	
}