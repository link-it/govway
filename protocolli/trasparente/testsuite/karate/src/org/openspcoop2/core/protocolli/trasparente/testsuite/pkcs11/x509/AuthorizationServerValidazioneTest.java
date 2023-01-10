/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
* AuthorizationServerTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AuthorizationServerValidazioneTest extends ConfigLoader {

	public final static String api_validazione = "TestValidazioneAuthorizationServer";
	
	public final static String PKCS11_ValidazioneJWS_none = "PKCS11-ValidazioneJWS-none";
	public final static String PKCS11_ValidazioneJWS = "PKCS11-ValidazioneJWS";
	public final static String PKCS11_ValidazioneJWE = "PKCS11-ValidazioneJWE";
	
	public final static String PKCS11_ForwardGovWayJWS = "PKCS11-ForwardGovWayJWS";
	public final static String PKCS11_ForwardJWS = "PKCS11-ForwardJWS";
	public final static String PKCS11_ForwardJWE = "PKCS11-ForwardJWE";
	
	
	@Test
	public void introspection_trustAll_noAlias() throws Exception {
		Utils.testJson(logCore, api_validazione, "TLS-TrustAll-ClientNoAliasIntrospection", 
				null, null, 
				"HSMClient1", null, null);
	}
	@Test
	public void userinfo_trustAll_noAlias() throws Exception {
		Utils.testJson(logCore, api_validazione, "TLS-TrustAll-ClientNoAliasUserInfo", 
				null, null, 
				"HSMClient1", null, null);
	}
	
	@Test
	public void introspection_trust_alias() throws Exception {
		Utils.testJson(logCore, api_validazione, "TLS-Trust-ServerAliasIntrospection", 
				null, null, 
				"HSMServer2", null, null);
	}
	@Test
	public void userinfo_trust_alias() throws Exception {
		Utils.testJson(logCore, api_validazione, "TLS-Trust-ServerAliasUserInfo", 
				null, null, 
				"HSMServer2", null, null);
	}
	
	@Test
	public void validazione_jws() throws Exception {
		Utils.testJson(logCore, api_validazione, PKCS11_ValidazioneJWS, 
				null, null, 
				null, null, null);
	}
	@Test
	public void validazione_jws_none() throws Exception {
		Utils.testJson(logCore, api_validazione, PKCS11_ValidazioneJWS_none, 
				"Token non valido", null, 
				null, null, null);
	}
	
	@Test
	public void validazione_jwe() throws Exception {
		Utils.testJson(logCore, api_validazione, PKCS11_ValidazioneJWE, 
				null, null, 
				null, null, null);
	}
	
	
	@Test
	public void forward_govway_jws() throws Exception {
		Utils.testJson(logCore, api_validazione, PKCS11_ForwardGovWayJWS, 
				null, null, 
				null, null, null);
	}
	
	@Test
	public void forward_jws() throws Exception {
		Utils.testJson(logCore, api_validazione, PKCS11_ForwardJWS, 
				null, null, 
				null, null, null);
	}
	
	
	@Test
	public void forward_jwe() throws Exception {
		Utils.testJson(logCore, api_validazione, PKCS11_ForwardJWE, 
				null, null, 
				null, null, null);
	}
}