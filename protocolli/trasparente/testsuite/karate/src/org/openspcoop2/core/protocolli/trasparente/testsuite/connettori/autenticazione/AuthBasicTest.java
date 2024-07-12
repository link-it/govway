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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.autenticazione;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* AuthBasicTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AuthBasicTest extends ConfigLoader {
	
	private static final String API = "AuthBasicTest";
	
	@Test
	public void erogazioneHttpBasic() throws Exception {
		httpbasic(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneHttpBasic() throws Exception {
		httpbasic(TipoServizio.FRUIZIONE);
	}
	private void httpbasic(TipoServizio tipoServizio) throws Exception {
		Map<String, String> headerHTTPAttesi = new HashMap<>();
		String atteso = "TestUtenteBasic:PasswordUtenteBasic";
		headerHTTPAttesi.put("GovWay-TestSuite-Authorization", 
				HttpConstants.AUTHORIZATION_PREFIX_BASIC+ Base64Utilities.encodeAsString(atteso.getBytes()));
		ApiKeyTest.test(tipoServizio,
				API, "standard",
				headerHTTPAttesi);
	}
	
	
	
	
	@Test
	public void erogazioneApikeyCustom() throws Exception {
		apikeyCustom(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneApikeyCustom() throws Exception {
		apikeyCustom(TipoServizio.FRUIZIONE);
	}
	private void apikeyCustom(TipoServizio tipoServizio) throws Exception {
		Map<String, String> headerHTTPAttesi = new HashMap<>();
		String atteso = "TestUtenteBasicViaApiKey:PasswordUtenteBasicViaApiKey";
		headerHTTPAttesi.put("GovWay-TestSuite-Authorization", 
				HttpConstants.AUTHORIZATION_PREFIX_BASIC+ Base64Utilities.encodeAsString(atteso.getBytes()));
		ApiKeyTest.test(tipoServizio,
				API, "viaApiKey",
				headerHTTPAttesi);
	}
	
}
