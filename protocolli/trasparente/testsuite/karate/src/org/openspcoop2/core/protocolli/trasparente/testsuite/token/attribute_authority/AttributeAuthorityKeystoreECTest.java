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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.attribute_authority;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;

/**
* TestAttributeAuthorityEC
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AttributeAuthorityKeystoreECTest extends ConfigLoader {

	private static final String API = "TestAttributeAuthorityEC";
	
	
	@Test
	public void ecNoPassword() throws Exception {
		test(API, "ecNoPassword", "TestAttributeAuthority-keyEC-NoPassword", true);
	}
	@Test
	public void ecWithPassword() throws Exception {
		test(API, "ecWithPassword", "TestAttributeAuthority-keyEC-WithPassword", true);
	}
	
	
	@Test
	public void ecNoPasswordAudErrata() throws Exception {
		test(API, "ecNoPassword", "TestAttributeAuthority-keyEC-NoPassword", false);
	}
	@Test
	public void ecWithPasswordAudErrata() throws Exception {
		test(API, "ecWithPassword", "TestAttributeAuthority-keyEC-WithPassword", false);
	}
		
	
	private void test(String api, String azione, String nomeAA, boolean audOk) throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		
		String msgErrore = null;
		String tipoTest = "singleAA_authzContenuti";
		if(audOk) {
			map.httpHeaders.put("GovWay-TestSuite-AAExpectedAudience", map.httpHeaders.get("GovWay-TestSuite-AAAudience"));
		}
		else {
			map.httpHeaders.put("GovWay-TestSuite-AAExpectedAudience", map.httpHeaders.get("GovWay-TestSuite-AAAudience")+"VALOREDIFFERENTE");
			msgErrore = "Recupero attributi dall'Authority '"+nomeAA+"' fallito: Invalid audience";
			tipoTest = RestTest.TIPO_TEST_VALIDITY_CHECK;
		}
		
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		RestTest._test(TipoServizio.EROGAZIONE, api, azione,
				tipoTest, msgErrore, map);
	}
}
