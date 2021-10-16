/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

/**
* JoseSecurityTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class JoseSecurityTest extends ConfigLoader {

	public final static String api = "TestJoseSecurity";
	public final static String apiNONE = "TestJoseSecurityNONE";
	
	@Test
	public void signature() throws Exception {
		Utils.testJson(logCore, api, "signature", 
				null, null, 
				null, "\"signatures\"", null);
	}
	@Test
	public void signatureNone() throws Exception {
		Utils.testJson(logCore, apiNONE, "signature", 
				null, "Signature verification failed", 
				null, "\"signatures\"", null);
	}
	
	@Test
	public void encrypt() throws Exception {
		Utils.testJson(logCore, api, "encrypt", 
				null, null, 
				null, "\"recipients\"", null);
	}
		
	
	
}