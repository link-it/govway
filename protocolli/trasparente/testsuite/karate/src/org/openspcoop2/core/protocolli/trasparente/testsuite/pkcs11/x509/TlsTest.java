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

/**
* TlsTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TlsTest extends ConfigLoader {

	public final static String api = "TestTLS";
	public final static String Trust_NoKeyAlias = "Trust-NoKeyAlias";
	
	@Test
	public void trustAll_keyServerAlias() throws Exception {
		Utils.testJson(logCore, api, "TrustAll-KeyServerAlias", 
				null, null, 
				"HSMServer", null, null);
	}
	@Test
	public void trustAll_keyServerAlias2() throws Exception {
		Utils.testJson(logCore, api, "TrustAll-KeyServer2Alias", 
				null, null, 
				"HSMServer2", null, null);
	}
	
	@Test
	public void trust_keyServerAlias() throws Exception {
		Utils.testJson(logCore, api, "Trust-KeyServerAlias", 
				null, null, 
				"HSMServer", null, null);
	}
	@Test
	public void trust_keyServerAlias2() throws Exception {
		Utils.testJson(logCore, api, "Trust-KeyServer2Alias", 
				null, null, 
				"HSMServer2", null, null);
	}
	
	@Test
	public void trustAll_noKeyAlias() throws Exception {
		Utils.testJson(logCore, api, "TrustAll-NoKeyAlias", 
				null, null, 
				"HSMClient1", null, null);
	}
	@Test
	public void trust_noKeyAlias() throws Exception {
		Utils.testJson(logCore, api, Trust_NoKeyAlias, 
				null, "L'applicativo HSMClient2 (soggetto gw/SoggettoInternoTestFruitore) non è autorizzato ad invocare il servizio gw/TestTLS (versione:1) erogato da gw/SoggettoInternoTest", 
				"HSMClient2", null, null);
	}
	
	@Test
	public void keyAliasUpperCase() throws Exception {
		Utils.testJson(logCore, api, "KeyAliasUpperCase", 
				null, null, 
				"HSMClient1", null, null);
	}
	
}