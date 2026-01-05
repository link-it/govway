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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* ForwardInformazioniKeystoreSenzaPasswordTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ForwardInformazioniKeystoreSenzaPasswordTest extends ConfigLoader {

	public static final String API_JWS = "TestForwardInformazioniToken-TestSSLKeystoreSenzaPassword";
	public static final String API_JWE = "TestForwardJWEInformazioniToken-TestSSLKeystoreSenzaPassword";
	
	public static final String HEADER_JWS = "govway-testsuite-custom-jws-nopassword";
	public static final String HEADER_JWE = "govway-testsuite-custom-jwe-nopassword";

	
	@Test
	public void jwsKeystoreJksNoPasswordKeyNoPassword() throws Exception {
		jws("keystoreJksNoPassword-KeyNoPassword");
	}
	@Test
	public void jwsKeystoreJksNoPasswordKeyWithPassword() throws Exception {
		jws("keystoreJksNoPassword-KeyWithPassword");
	}
	@Test
	public void jwsKeystorePkcs12NoPasswordKeyNoPassword() throws Exception {
		jws("keystorePkcs12NoPassword-KeyNoPassword");
	}
	@Test
	public void jwsKeystorePkcs12NoPasswordKeyWithPassword() throws Exception {
		jws("keystorePkcs12NoPassword-KeyWithPassword");
	}
	public void jws(String azione) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		Map<String, List<String>> values = new HashMap<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				ForwardInformazioniTest.buildJWT(values));
		
		HttpResponse response = Utilities._test(logCore, API_JWS, azione, headers,  null,
				null,
				null, null);
		
		ForwardInformazioniTest.checkGovWayJwt(HEADER_JWS, response, values, false);
	}
	
	
	
	
	@Test
	public void jweKeystoreJksNoPasswordKeyNoPassword() throws Exception {
		jwe("keystoreJksNoPassword-KeyNoPassword");
	}
	@Test
	public void jweKeystoreJksNoPasswordKeyWithPassword() throws Exception {
		jwe("keystoreJksNoPassword-KeyWithPassword");
	}
	@Test
	public void jweKeystorePkcs12NoPasswordKeyNoPassword() throws Exception {
		jwe("keystorePkcs12NoPassword-KeyNoPassword");
	}
	@Test
	public void jweKeystorePkcs12NoPasswordKeyWithPassword() throws Exception {
		jwe("keystorePkcs12NoPassword-KeyWithPassword");
	}
	public void jwe(String azione) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		Map<String, List<String>> values = new HashMap<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				ForwardInformazioniTest.buildJWT(values));
		
		HttpResponse response = Utilities._test(logCore, API_JWE, azione, headers,  null,
				null,
				null, null);
		
		ForwardInformazioniTest.checkGovWayJwt(HEADER_JWE, response, values, false);
	}
	
}