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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* ValidazioneTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AllTest extends ConfigLoader {

	public static final String validazione = "TestValidazioneToken-MergeToken";
		
	@Test
	public void success() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT(true, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void notFound() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"Non è stato riscontrato un token nella posizione [RFC 6750 - Bearer Token Usage]",
				null, null);
	}
	
	@Test
	public void invalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		String tokenInvalid = "AAA.AAAA.AAAA";
		
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				tokenInvalid);
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"Validazione del token 'JWS' fallita: Token non valido: [COMPACT] Signature verification failure",
				null, Utilities.getMapExpectedTokenInfoInvalid(tokenInvalid));
	}
	
	@Test
	public void signatureInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		String tokenInvalid = buildJWT_signInvalid();
		
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				tokenInvalid);
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"Validazione del token 'JWS' fallita: Token non valido",
				null, Utilities.getMapExpectedTokenInfoInvalid(tokenInvalid));
	}
	
	
	
	@Test
	public void iatInvalid() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_dates(true, null, 
				false, null,
				false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"Token expired; iat time '%' too old",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void iatInTheFuture() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_dates(false, 8000l, // tolleranza a 5secondi 
				false, null,
				false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"Token valid in the future; iat time '%' is in the future",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void iatInTheFutureTolerance() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_dates(false, 2000l, // tolleranza a 5secondi 
				false, null,
				false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void nbfInvalid() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_dates(false, null, 
				true, null,
				false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"Token not usable before %",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void nbfInTheFuture() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_dates(false, null,
				false, 8000l, // tolleranza a 5secondi 
				false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"Token not usable before %",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void nbfInTheFutureTolerance() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_dates(false, null,
				false, 2000l, // tolleranza a 5secondi 
				false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void expInvalid() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_dates(false, null, 
				false, null,
				true, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"Token expired",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	@Test
	public void requiredClaims_clientId() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT(true,
				false, true, true, true, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without clientId claim",
				Utilities.credenzialiMittente_clientIdNull, mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaims_issuer() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT(true,
				true, false, true, true, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without issuer claim",
				Utilities.credenzialiMittente_issuerNull, mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaims_subject() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT(true,
				true, true, false, true, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without subject claim",
				Utilities.credenzialiMittente_subjectNull, mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaims_username() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT(true,
				true, true, true, false, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without username claim",
				Utilities.credenzialiMittente_usernameNull, mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaims_eMail() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT(true,
				true, true, true, true, false,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without email claim",
				Utilities.credenzialiMittente_emailNull, mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaims_disabilitato() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT(false,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "requiredClaims", headers,  null,
				null,
				null, mapExpectedTokenInfo);
	}

	
	
	
	@Test
	public void scopeAny() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_scope(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "scopeAny", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void scopeAny2() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_scope(false, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "scopeAny", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void scopeAnyKo() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_scope(false, false, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "scopeAny", headers,  null,
				"(Token without scopes) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void scopeKo() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_scope(true, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"(Scope '"+Utilities.s2+"' not found) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void scopeKo2() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_scope(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"(Scope '"+Utilities.s3+"' not found) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	
	@Test
	public void rolesAny() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "rolesAny", headers,  null,null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void rolesAny2() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(false, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "rolesAny", headers,  null,null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void rolesAnyKo() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(false, false, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "rolesAny", headers,  null,
				"(Roles not found in request context) Il mittente non è autorizzato ad invocare il servizio gw/TestValidazioneToken-MergeToken (versione:1) erogato da gw/SoggettoInternoTest",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void rolesKo() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(true, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"(Role 'r2' not found in request context) Il mittente non è autorizzato ad invocare il servizio gw/TestValidazioneToken-MergeToken (versione:1) erogato da gw/SoggettoInternoTest",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void rolesKo2() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"(Role 'r3' not found in request context) Il mittente non è autorizzato ad invocare il servizio gw/TestValidazioneToken-MergeToken (versione:1) erogato da gw/SoggettoInternoTest",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	
	@Test
	public void clientInvalid() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_invalid(true, false, false, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"(Token claim 'client_id' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente_clientIdInvalid, mapExpectedTokenInfo);
	}
	
	
	@Test
	public void audienceInvalid() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_invalid(false, true, false, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"(Token claim 'aud' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void audienceSingleValueOk() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_singleValueNoArrayAudience(false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void audienceSingleValueKo() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_singleValueNoArrayAudience(true, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"(Token claim 'aud' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	@Test
	public void usernameInvalid1() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_invalid(false, false, true, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"(Token claim 'username' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente_usernameInvalid, mapExpectedTokenInfo);
	}
	@Test
	public void usernameInvalid2() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_invalid(false, false, false, false, // tutti corretti 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username+"ERRORE"); // l'errore e' nel valore dinamico dell'header usato per il confronto
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"(Token claim 'username' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	@Test
	public void claimUnexpected() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_invalid(false, false, false, true, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  null,
				"(Token unexpected claim 'nonEsistente') La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	
	@Test
	public void not_success() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "not", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void not_clientIdInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT(true,
						true, true, true, true, true,
						true, true, true,
						false,true,false,
						false,
						false, null, 
						false, null,
						false,
						true, 
						false, false, 
						false, false,
						mapExpectedTokenInfo));
		Utilities._test(logCore, validazione, "not", headers,  null,
				"(Token claim 'client_id' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente_clientIdInvalid, mapExpectedTokenInfo);
	}
	
	@Test
	public void not_audienceInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "not", headers,  null,
				"(Token claim 'aud' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void not_issInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "not", headers,  null,
				"(Token claim 'iss' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void not_roleInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ buildJWT_roles(true,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "not", headers,  null,
				"(Token claim 'role' with unexpected value (regExpr not match failed)) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void not_usernameInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ 
				buildJWT(true,
				true, true, true, true, true,
				true, true, true,
				false,true,false,
				false,
				false, null, 
				false, null,
				false,
				false, 
				false, false, 
				true, false,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "not", headers,  null,
				"(Token claim 'username' with unexpected value (regExpr not find failed)) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente_usernameInvalid, mapExpectedTokenInfo);
	}
	
	
	
	
	
	
	
	@Test
	public void notOnlyAuthzContenuti_success() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "notOnlyAuthzContenuti", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void notOnlyAuthzContenuti_clientIdInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT(true,
						true, true, true, true, true,
						true, true, true,
						false,true,false,
						false,
						false, null, 
						false, null,
						false,
						true, 
						false, false, 
						false, false,
						mapExpectedTokenInfo));
		Utilities._test(logCore, validazione, "notOnlyAuthzContenuti", headers,  null,
				"(Resource '${tokenInfo:clientId}' with unauthorized value '18192.apps.invalid') Il chiamante non è autorizzato ad invocare l'API",
				Utilities.credenzialiMittente_clientIdInvalid, mapExpectedTokenInfo);
	}
	
	@Test
	public void notOnlyAuthzContenuti_audienceInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "23223.apps");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "notOnlyAuthzContenuti", headers,  null,
				"(Resource '${tokenInfo:aud[0]}' with unauthorized value '23223.apps') Il chiamante non è autorizzato ad invocare l'API",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void notOnlyAuthzContenuti_issInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "notOnlyAuthzContenuti", headers,  null,
				"(Resource '${tokenInfo:iss}' with unauthorized value 'testAuthEnte') Il chiamante non è autorizzato ad invocare l'API",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void notOnlyAuthzContenuti_roleInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ buildJWT_roles(true,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "notOnlyAuthzContenuti", headers,  null,
				"(Resource '${tokenInfo:roles[0]}' with unexpected value 'https://r1' (regExpr not match failed)) Il chiamante non è autorizzato ad invocare l'API",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void notOnlyAuthzContenuti_usernameInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ 
				buildJWT(true,
				true, true, true, true, true,
				true, true, true,
				false,true,false,
				false,
				false, null, 
				false, null,
				false,
				false, 
				false, false, 
				true, false,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "notOnlyAuthzContenuti", headers,  null,
				"Resource '${tokenInfo:username}' with unexpected value 'usernameErrato' (regExpr not find failed)) Il chiamante non è autorizzato ad invocare l'API",
				Utilities.credenzialiMittente_usernameInvalid, mapExpectedTokenInfo);
	}
	
	
	
	
	@Test
	public void ignoreCase_success() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-ignoreCase-fullName", "bianchi ROSSI");
		headers.put("test-ignoreCase-firstName", "MARIO");
		headers.put("test-ignoreCase-iss", "TESTAUTHENTE.INVALID");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "ignoreCase", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	@Test
	public void ignoreCase_issInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-ignoreCase-fullName", "bianchi ROSSI");
		headers.put("test-ignoreCase-firstName", "MARIO");
		headers.put("test-ignoreCase-iss", "TESTAUTHENTE"); // c'e' la regola not
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "ignoreCase", headers,  null,
				"(Token claim 'iss' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void ignoreCase_userInfo_fullNameInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-ignoreCase-fullName", "bianchi VERDI"); // atteso Rossi
		headers.put("test-ignoreCase-firstName", "MARIO");
		headers.put("test-ignoreCase-iss", "TESTAUTHENTE.INVALID");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "ignoreCase", headers,  null,
				"(Token claim 'name' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void ignoreCase_userInfo_firstNameInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-ignoreCase-fullName", "bianchi ROSSI");
		headers.put("test-ignoreCase-firstName", "ALTRONOME");
		headers.put("test-ignoreCase-iss", "TESTAUTHENTE.INVALID");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "ignoreCase", headers,  null,
				"(Token claim 'given_name' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	
	
	
	@Test
	public void ignoreCaseOnlyAuthzContenuti_success() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-ignoreCase-fullName", "bianchi ROSSI");
		headers.put("test-ignoreCase-firstName", "MARIO");
		headers.put("test-ignoreCase-iss", "TESTAUTHENTE.INVALID");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "ignoreCaseOnlyAuthzContenuti", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	@Test
	public void ignoreCaseOnlyAuthzContenuti_issInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-ignoreCase-fullName", "bianchi ROSSI");
		headers.put("test-ignoreCase-firstName", "MARIO");
		headers.put("test-ignoreCase-iss", "TESTAUTHENTE"); // c'e' la regola not
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "ignoreCaseOnlyAuthzContenuti", headers,  null,
				"(Resource '${tokenInfo:iss}' with unauthorized value 'testAuthEnte') Il chiamante non è autorizzato ad invocare l'API",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void ignoreCaseOnlyAuthzContenuti_userInfo_fullNameInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-ignoreCase-fullName", "bianchi VERDI"); // atteso Rossi
		headers.put("test-ignoreCase-firstName", "MARIO");
		headers.put("test-ignoreCase-iss", "TESTAUTHENTE.INVALID");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "ignoreCaseOnlyAuthzContenuti", headers,  null,
				"(Resource '${tokenInfo:userInfo.fullName}' with unexpected value 'Mario Bianchi Rossi') Il chiamante non è autorizzato ad invocare l'API",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void ignoreCaseOnlyAuthzContenuti_userInfo_firstNameInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-ignoreCase-fullName", "bianchi ROSSI");
		headers.put("test-ignoreCase-firstName", "ALTRONOME");
		headers.put("test-ignoreCase-iss", "TESTAUTHENTE.INVALID");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "ignoreCaseOnlyAuthzContenuti", headers,  null,
				"(Resource '${tokenInfo:userInfo.firstName}' with unexpected value 'Mario') Il chiamante non è autorizzato ad invocare l'API",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	private static String buildJWT_signInvalid() throws Exception {
		return buildJWT(true,
				true, true, true, true, true,
				true, true, true,
				true, true, true,
				true,
				false, null, 
				false, null,
				false,
				false, 
				false, false,
				false, false,
				null);
	}
	private static String buildJWT(boolean requiredClaims,
			List<String> mapExpectedTokenInfo) throws Exception {
		return buildJWT(requiredClaims,
				true, true, true, true, true,
				mapExpectedTokenInfo);
	}
	private static String buildJWT(boolean requiredClaims,
			boolean requiredClaims_clientId, boolean requiredClaims_issuer, boolean requiredClaims_subject,
			boolean requiredClaims_username, boolean requiredClaims_eMail,
			List<String> mapExpectedTokenInfo) throws Exception {
		return buildJWT(requiredClaims,
				requiredClaims_clientId, requiredClaims_issuer, requiredClaims_subject,
				requiredClaims_username, requiredClaims_eMail,
				true, true, true,
				true, true, true,
				false,
				false, null, 
				false, null,
				false,
				false, 
				false, false,
				false, false,
				mapExpectedTokenInfo);
	}
	private static String buildJWT_scope(boolean scope1, boolean scope2, boolean scope3,
			List<String> mapExpectedTokenInfo) throws Exception {
		return buildJWT(true,
				true, true, true, true, true,
				scope1, scope2, scope3,
				true, true, true,
				false,
				false, null, 
				false, null,
				false,
				false, 
				false, false,
				false, false,
				mapExpectedTokenInfo);
	}
	private static String buildJWT_roles(boolean role1, boolean role2, boolean role3,
			List<String> mapExpectedTokenInfo) throws Exception {
		return buildJWT(true,
				true, true, true, true, true,
				true, true, true,
				role1, role2, role3,
				false,
				false, null, 
				false, null,
				false,
				false, 
				false, false,
				false, false,
				mapExpectedTokenInfo);
	}
	private static String buildJWT_dates(boolean invalidIat, Long futureIat, 
			boolean invalidNbf, Long futureNbf, 
			boolean invalidExp,
			List<String> mapExpectedTokenInfo) throws Exception {
		return buildJWT(true,
				true, true, true, true, true,
				true, true, true,
				true, true, true,
				false,
				invalidIat, futureIat, 
				invalidNbf, futureNbf,
				invalidExp,
				false, 
				false, false,
				false, false,
				mapExpectedTokenInfo);
	}	
	private static String buildJWT_invalid(boolean invalidClientId, boolean invalidAudience, boolean invalidUsername, boolean invalidClaimCheNonDeveEsistere,
			List<String> mapExpectedTokenInfo) throws Exception {
		return buildJWT(true,
				true, true, true, true, true,
				true, true, true,
				true, true, true,
				false,
				false, null, 
				false, null,
				false,
				invalidClientId, 
				false, invalidAudience, 
				invalidUsername, invalidClaimCheNonDeveEsistere,
				mapExpectedTokenInfo);
	}			
	private static String buildJWT_singleValueNoArrayAudience(boolean invalidAudience,
			List<String> mapExpectedTokenInfo) throws Exception {
		return buildJWT(true,
				true, true, true, true, true,
				true, true, true,
				true, true, true,
				false,
				false, null, 
				false, null,
				false,
				false, 
				true, invalidAudience, 
				false, false,
				mapExpectedTokenInfo);
	}
	private static String buildJWT(boolean requiredClaims,
			boolean requiredClaims_clientId, boolean requiredClaims_issuer, boolean requiredClaims_subject,
			boolean requiredClaims_username, boolean requiredClaims_eMail,
			boolean scope1, boolean scope2, boolean scope3,
			boolean role1, boolean role2, boolean role3,
			boolean signWithSoggetto1,
			boolean invalidIat, Long futureIat, 
			boolean invalidNbf, Long futureNbf, 
			boolean invalidExp,
			boolean invalidClientId, 
			boolean singleValueNoArrayAudience, boolean invalidAudience,
			boolean invalidUsername, boolean invalidClaimCheNonDeveEsistere,
			List<String> mapExpectedTokenInfo) throws Exception {
		
		String jsonInput = Utilities.buildJson(requiredClaims, 
				requiredClaims_clientId, requiredClaims_issuer, requiredClaims_subject, 
				requiredClaims_username, requiredClaims_eMail, 
				scope1, scope2, scope3, 
				role1, role2, role3, 
				invalidIat, futureIat, 
				invalidNbf, futureNbf,
				invalidExp, 
				invalidClientId, 
				singleValueNoArrayAudience, invalidAudience, 
				invalidUsername, invalidClaimCheNonDeveEsistere, 
				mapExpectedTokenInfo,
				""); 
		
		File f = new File("/tmp/introspectionResponse.json");
		FileSystemUtilities.writeFile(f, jsonInput.getBytes());
		
		f = new File("/tmp/userinfoResponse.json");
		FileSystemUtilities.writeFile(f, jsonInput.getBytes());
		
		if(mapExpectedTokenInfo!=null) {
			if(invalidIat || (futureIat!=null && futureIat.longValue()>2000) || invalidNbf || (futureNbf!=null && futureNbf.longValue()>2000) || invalidExp) {
				mapExpectedTokenInfo.add("\"sourceType\":\"JWT\"");
			}
			else {
				mapExpectedTokenInfo.add("\"sourcesTokenInfo\"");
				mapExpectedTokenInfo.add("\"JWT\"");
				mapExpectedTokenInfo.add("\"USER_INFO\"");
				mapExpectedTokenInfo.add("\"INTROSPECTION\"");
			}
		}
		
		//System.out.println("TOKEN ["+jsonInput+"]");
		
		Properties props = new Properties();
		props.put("rs.security.keystore.type","JKS");
		String password = "openspcoop";
		if(signWithSoggetto1) {
			props.put("rs.security.keystore.file", "/etc/govway/keys/soggetto1.jks");
			props.put("rs.security.keystore.alias","soggetto1");
			props.put("rs.security.keystore.password","openspcoopjks");
			props.put("rs.security.key.password",password);
		}
		else {
			props.put("rs.security.keystore.file", "/etc/govway/keys/erogatore.jks");
			props.put("rs.security.keystore.alias","erogatore");
			props.put("rs.security.keystore.password",password);
			props.put("rs.security.key.password",password);
		}
		
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
			
		props.put("rs.security.signature.algorithm","RS256");
		props.put("rs.security.signature.include.cert","false");
		props.put("rs.security.signature.include.key.id","true");
		props.put("rs.security.signature.include.public.key","false");
		props.put("rs.security.signature.include.cert.sha1","false");
		props.put("rs.security.signature.include.cert.sha256","false");
			
		JsonSignature jsonSignature = new JsonSignature(props, options);
		String token = jsonSignature.sign(jsonInput);
		//System.out.println(token);
			
		return token;		
		
	}
}