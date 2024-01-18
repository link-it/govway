/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
* AttributeAuthorityTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AttributeAuthorityTest extends ConfigLoader {

	public static final String api = "TestAttributeAuthorityPKCS11";
	
	
	@Test
	public void trustAll_noAlias() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		Utils.testJson(logCore, api, "TLS-TrustAll-ClientNoAlias", 
				null, null, 
				"HSMClient1", "\"attributes\": [\"role1\",\"attrRole2\"]", null);
	}

	@Test
	public void trust_alias() throws Exception {
		Utils.testJson(logCore, api, "TLS-Trust-ServerAlias", 
				null, null, 
				"HSMServer2", "\"attributes\": [\"role1\",\"attrRole2\"]", null);
	}
	
	@Test
	public void jwsRequest() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		Utils.testJson(logCore, api, "JWSRequest", 
				null, null, 
				null, null, null);
	}
	
	@Test
	public void jwsResponse() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		Utils.testJson(logCore, api, "JWSResponse", 
				null, null, 
				null, null, null);
	}
}