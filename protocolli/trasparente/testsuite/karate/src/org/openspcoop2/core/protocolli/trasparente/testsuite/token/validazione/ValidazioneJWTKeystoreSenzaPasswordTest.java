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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* ValidazioneJWTKeystoreSenzaPasswordTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ValidazioneJWTKeystoreSenzaPasswordTest extends ConfigLoader {

	public static final String validazione = "TestValidazioneToken-ValidazioneJWT-TestSSLKeystoreSenzaPassword"; 
	public static final String validazione_jwe = "TestValidazioneToken-ValidazioneJWE-TestSSLKeystoreSenzaPassword"; 
	public static final String validazione_dynamic_discovery = "TestValidazioneTokenDynamicDiscovery-TestSSLKeystoreSenzaPassword";

	@Test
	public void truststoreJksSenzaPassword() throws Exception {
		test(TipoServizio.EROGAZIONE, "success");
	}
	@Test
	public void truststorePkcs12SenzaPassword() throws Exception {
		test(TipoServizio.EROGAZIONE, "not"); // uso azione not
	}
	private void test(TipoServizio tipoServizio, String azione) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				ValidazioneJWTTest.buildJWS(true, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, azione, headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	@Test
	public void jweKeystoreJksNoPasswordKeyNoPassword() throws Exception {
		testJWE(TipoServizio.EROGAZIONE, "keystoreJksNoPassword-KeyNoPassword");
	}
	@Test
	public void jweKeystoreJksNoPasswordKeyWithPassword() throws Exception {
		testJWE(TipoServizio.EROGAZIONE, "keystoreJksNoPassword-KeyWithPassword"); 
	}
	@Test
	public void jweKeystorePkcs12NoPasswordKeyNoPassword() throws Exception {
		testJWE(TipoServizio.EROGAZIONE, "keystorePkcs12NoPassword-KeyNoPassword");
	}
	@Test
	public void jweKeystorePkcs12NoPasswordKeyWithPassword() throws Exception {
		testJWE(TipoServizio.EROGAZIONE, "keystorePkcs12NoPassword-KeyWithPassword"); 
	}
	private void testJWE(TipoServizio tipoServizio, String azione) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				ValidazioneJWTTest.buildJWE(true, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione_jwe, azione, headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
		
	@Test
	public void dynamicDiscovery_truststoreJksSenzaPassword() throws Exception {
		ValidazioneJWTDynamicDiscoveryTest.test(TipoServizio.EROGAZIONE, validazione_dynamic_discovery, "oidc");
	}
	
}