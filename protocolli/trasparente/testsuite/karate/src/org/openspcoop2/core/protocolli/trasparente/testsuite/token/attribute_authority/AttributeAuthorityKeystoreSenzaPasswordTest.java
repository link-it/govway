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
* AttributeAuthorityKeystoreSenzaPasswordTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AttributeAuthorityKeystoreSenzaPasswordTest extends ConfigLoader {

	private static final String API = "TestAttributeAuthorityKeystoreSenzaPassword";
	private static final String API_SSL = "TestAttributeAuthority-SSL-KeystoreSenzaPassword";
	
	// nei test è comppreso anche il truststore senza password
	@Test
	public void signKeystoreJksNoPasswordKeyNoPassword() throws Exception {
		test(API, "keystoreJksNoPassword-KeyNoPassword");
	}
	@Test
	public void signKeystoreJksNoPasswordKeyWithPassword() throws Exception {
		test(API, "keystoreJksNoPassword-KeyWithPassword");
	}
	@Test
	public void signKeystorePkcs12NoPasswordKeyNoPassword() throws Exception {
		test(API, "keystorePkcs12NoPassword-KeyNoPassword");
	}
	@Test
	public void signKeystorePkcs12NoPasswordKeyWithPassword() throws Exception {
		test(API, "keystorePkcs12NoPassword-KeyWithPassword");
	}
	
	
	@Test
	public void sslTruststoreJksSenzaPassword() throws Exception {
		test(API_SSL, "truststoreJksSenzaPassword");
	}
	@Test
	public void sslTruststorePkcs12SenzaPassword() throws Exception {
		test(API_SSL, "truststorePkcs12SenzaPassword");
	}
	
	@Test
	public void sslKeystoreJksNoPasswordKeyNoPassword() throws Exception {
		test(API_SSL, "keystoreJksNoPassword-KeyNoPassword");
	}
	@Test
	public void sslKeystoreJksNoPasswordKeyNoPasswordUsaTrustStore() throws Exception {
		test(API_SSL, "keystoreJksNoPassword-KeyNoPassword-usaTrustStore");
	}
	@Test
	public void sslKeystoreJksNoPasswordKeyWithPassword() throws Exception {
		test(API_SSL, "keystoreJksNoPassword-KeyWithPassword");
	}
	
	@Test
	public void sslKeystorePkcs12NoPasswordKeyNoPassword() throws Exception {
		test(API_SSL, "keystorePkcs12NoPassword-KeyNoPassword");
	}
	@Test
	public void sslKeystorePkcs12NoPasswordKeyNoPasswordUsaTrustStore() throws Exception {
		test(API_SSL, "keystorePkcs12NoPassword-KeyNoPassword-usaTrustStore");
	}
	@Test
	public void sslKeystorePkcs12NoPasswordKeyWithPassword() throws Exception {
		test(API_SSL, "keystorePkcs12NoPassword-KeyWithPassword");
	}
	
	
	private void test(String api, String azione) throws Exception {
		AAHeaderMap map = new AAHeaderMap();
		map.applicativo="ApplicativoSoggettoInternoTestFruitore1";
		RestTest._test(TipoServizio.EROGAZIONE, api, azione,
				"singleAA_authzContenuti", null, map);
	}
}
