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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.JwtHeaders;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* ValidazioneTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ValidazioneJWTTest extends ConfigLoader {

	public static final String validazione = "TestValidazioneToken-ValidazioneJWT";
	
	public static final String validazioneHeaderRFC9068 = "TestValidazioneToken-ValidazioneJWT-HeaderRFC9068";
	public static final String validazioneHeaderRFC9068noCTY = "TestValidazioneToken-ValidazioneJWT-HeaderRFC9068noCTY";
	public static final String validazioneHeaderRFC9068noTYP = "TestValidazioneToken-ValidazioneJWT-HeaderRFC9068noTYP";

	public static final String validazioneHeader = "TestValidazioneToken-ValidazioneJWT-Header";
		
	@Test
	public void success() throws Exception {
		success(TipoServizio.EROGAZIONE);
	}
	@Test
	public void successFruizione() throws Exception {
		success(TipoServizio.FRUIZIONE);
	}
	private void success(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT(true, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void notFound() throws Exception {
		notFound(TipoServizio.EROGAZIONE);
	}
	@Test
	public void notFoundFruizione() throws Exception {
		notFound(TipoServizio.FRUIZIONE);
	}
	private void notFound(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"Non è stato riscontrato un token nella posizione [RFC 6750 - Bearer Token Usage]",
				null, null);
	}
	
	@Test
	public void invalid() throws Exception {
		invalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void invalidFruizione() throws Exception {
		invalid(TipoServizio.FRUIZIONE);
	}
	private void invalid(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		String tokenInvalid = "AAA.AAAA.AAAA";
		
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				tokenInvalid);
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"Validazione del token 'JWS' fallita: Token non valido: [COMPACT] Signature verification failure",
				null, Utilities.getMapExpectedTokenInfoInvalid(tokenInvalid));
	}
	
	@Test
	public void signatureInvalid() throws Exception {
		signatureInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void signatureInvalidFruizione() throws Exception {
		signatureInvalid(TipoServizio.FRUIZIONE);
	}
	private void signatureInvalid(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		String tokenInvalid = buildJWT_signInvalid();
		
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				tokenInvalid);
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"Validazione del token 'JWS' fallita: Token non valido",
				null, Utilities.getMapExpectedTokenInfoInvalid(tokenInvalid));
	}
	
	
	
	@Test
	public void iatInvalid() throws Exception {
		iatInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void iatInvalidFruizione() throws Exception {
		iatInvalid(TipoServizio.FRUIZIONE);
	}
	private void iatInvalid(TipoServizio tipoServizio) throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_dates(true, false, false, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"Token expired; iat time '%' too old",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void iatInTheFuture() throws Exception {
		iatInTheFuture(TipoServizio.EROGAZIONE);
	}
	@Test
	public void iatInTheFutureFruizione() throws Exception {
		iatInTheFuture(TipoServizio.FRUIZIONE);
	}
	private void iatInTheFuture(TipoServizio tipoServizio) throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_dates(false, true, false, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"Token valid in the future; iat time '%' is in the future",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void nbfInvalid() throws Exception {
		nbfInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void nbfInvalidFruizione() throws Exception {
		nbfInvalid(TipoServizio.FRUIZIONE);
	}
	private void nbfInvalid(TipoServizio tipoServizio) throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_dates(false, false, true, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"Token not usable before %",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void expInvalid() throws Exception {
		expInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void expInvalidFruizione() throws Exception {
		expInvalid(TipoServizio.FRUIZIONE);
	}
	private void expInvalid(TipoServizio tipoServizio) throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_dates(false, false, false, true, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"Token expired",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	@Test
	public void requiredClaimsClientId() throws Exception {
		requiredClaimsClientId(TipoServizio.EROGAZIONE);
	}
	@Test
	public void requiredClaimsClientIdFruizione() throws Exception {
		requiredClaimsClientId(TipoServizio.FRUIZIONE);
	}
	private void requiredClaimsClientId(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT(true,
				false, true, true, true, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without clientId claim",
				Utilities.credenzialiMittente_clientIdNull, mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaimsIssuer() throws Exception {
		requiredClaimsIssuer(TipoServizio.EROGAZIONE);
	}
	@Test
	public void requiredClaimsIssuerFruizione() throws Exception {
		requiredClaimsIssuer(TipoServizio.FRUIZIONE);
	}
	private void requiredClaimsIssuer(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT(true,
				true, false, true, true, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without issuer claim",
				Utilities.credenzialiMittente_issuerNull, mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaimsSubject() throws Exception {
		requiredClaimsSubject(TipoServizio.EROGAZIONE);
	}
	@Test
	public void requiredClaimsSubjectFruizione() throws Exception {
		requiredClaimsSubject(TipoServizio.FRUIZIONE);
	}
	private void requiredClaimsSubject(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT(true,
				true, true, false, true, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without subject claim",
				Utilities.credenzialiMittente_subjectNull, mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaimsUsername() throws Exception {
		requiredClaimsUsername(TipoServizio.EROGAZIONE);
	}
	@Test
	public void requiredClaimsUsernameFruizione() throws Exception {
		requiredClaimsUsername(TipoServizio.FRUIZIONE);
	}
	private void requiredClaimsUsername(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT(true,
				true, true, true, false, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without username claim",
				Utilities.credenzialiMittente_usernameNull, mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaimsEMail() throws Exception {
		requiredClaimsEMail(TipoServizio.EROGAZIONE);
	}
	@Test
	public void requiredClaimsEMailFruizione() throws Exception {
		requiredClaimsEMail(TipoServizio.FRUIZIONE);
	}
	private void requiredClaimsEMail(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT(true,
				true, true, true, true, false,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without email claim",
				Utilities.credenzialiMittente_emailNull, mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaimsDisabilitato() throws Exception {
		requiredClaimsDisabilitato(TipoServizio.EROGAZIONE);
	}
	@Test
	public void requiredClaimsDisabilitatoFruizione() throws Exception {
		requiredClaimsDisabilitato(TipoServizio.FRUIZIONE);
	}
	private void requiredClaimsDisabilitato(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT(false,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "requiredClaims", headers,  null,
				null,
				null, mapExpectedTokenInfo);
	}

	
	
	
	@Test
	public void scopeAny() throws Exception {
		scopeAny(TipoServizio.EROGAZIONE);
	}
	@Test
	public void scopeAnyFruizione() throws Exception {
		scopeAny(TipoServizio.FRUIZIONE);
	}
	private void scopeAny(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_scope(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "scopeAny", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void scopeAny2() throws Exception {
		scopeAny2(TipoServizio.EROGAZIONE);
	}
	@Test
	public void scopeAny2Fruizione() throws Exception {
		scopeAny2(TipoServizio.FRUIZIONE);
	}
	private void scopeAny2(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_scope(false, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "scopeAny", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void scopeAnyKo() throws Exception {
		scopeAnyKo(TipoServizio.EROGAZIONE);
	}
	@Test
	public void scopeAnyKoFruizione() throws Exception {
		scopeAnyKo(TipoServizio.FRUIZIONE);
	}
	private void scopeAnyKo(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_scope(false, false, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "scopeAny", headers,  null,
				"(Token without scopes) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void scopeKo() throws Exception {
		scopeKo(TipoServizio.EROGAZIONE);
	}
	@Test
	public void scopeKoFruizione() throws Exception {
		scopeKo(TipoServizio.FRUIZIONE);
	}
	private void scopeKo(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_scope(true, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"(Scope '"+Utilities.s2+"' not found) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void scopeKo2() throws Exception {
		scopeKo2(TipoServizio.EROGAZIONE);
	}
	@Test
	public void scopeKo2Fruizione() throws Exception {
		scopeKo2(TipoServizio.FRUIZIONE);
	}
	private void scopeKo2(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_scope(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"(Scope '"+Utilities.s3+"' not found) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	
	@Test
	public void rolesAny() throws Exception {
		rolesAny(TipoServizio.EROGAZIONE);
	}
	@Test
	public void rolesAnyFruizione() throws Exception {
		rolesAny(TipoServizio.FRUIZIONE);
	}
	private void rolesAny(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "rolesAny", headers,  null,null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void rolesAny2() throws Exception {
		rolesAny2(TipoServizio.EROGAZIONE);
	}
	@Test
	public void rolesAny2Fruizione() throws Exception {
		rolesAny2(TipoServizio.FRUIZIONE);
	}
	private void rolesAny2(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(false, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "rolesAny", headers,  null,null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void rolesAnyKo() throws Exception {
		rolesAnyKo(TipoServizio.EROGAZIONE);
	}
	@Test
	public void rolesAnyKoFruizione() throws Exception {
		rolesAnyKo(TipoServizio.FRUIZIONE);
	}
	private void rolesAnyKo(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(false, false, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "rolesAny", headers,  null,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ? 
						"(Roles not found in request context) Il mittente non è autorizzato ad invocare il servizio gw/TestValidazioneToken-ValidazioneJWT (versione:1) erogato da gw/SoggettoInternoTest"
						:
						"(Roles not found in request context) Il servizio applicativo Anonimo non risulta autorizzato a fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void rolesKo() throws Exception {
		rolesKo(TipoServizio.EROGAZIONE);
	}
	@Test
	public void rolesKoFruizione() throws Exception {
		rolesKo(TipoServizio.FRUIZIONE);
	}
	private void rolesKo(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(true, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ? 
						"(Role 'r2' not found in request context) Il mittente non è autorizzato ad invocare il servizio gw/TestValidazioneToken-ValidazioneJWT (versione:1) erogato da gw/SoggettoInternoTest"
						:
						"(Role 'r2' not found in request context) Il servizio applicativo Anonimo non risulta autorizzato a fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void rolesKo2() throws Exception {
		rolesKo2(TipoServizio.EROGAZIONE);
	}
	@Test
	public void rolesKo2Fruizione() throws Exception {
		rolesKo2(TipoServizio.FRUIZIONE);
	}
	private void rolesKo2(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_roles(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ? 
						"(Role 'r3' not found in request context) Il mittente non è autorizzato ad invocare il servizio gw/TestValidazioneToken-ValidazioneJWT (versione:1) erogato da gw/SoggettoInternoTest"
						:
						"(Role 'r3' not found in request context) Il servizio applicativo Anonimo non risulta autorizzato a fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	
	@Test
	public void clientInvalid() throws Exception {
		clientInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void clientInvalidFruizione() throws Exception {
		clientInvalid(TipoServizio.FRUIZIONE);
	}
	private void clientInvalid(TipoServizio tipoServizio) throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_invalid(true, false, false, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"(Token claim 'TESTclient_id' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente_clientIdInvalid, mapExpectedTokenInfo);
	}
	
	
	@Test
	public void audienceInvalid() throws Exception {
		audienceInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void audienceInvalidFruizione() throws Exception {
		audienceInvalid(TipoServizio.FRUIZIONE);
	}
	private void audienceInvalid(TipoServizio tipoServizio) throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_invalid(false, true, false, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"(Token claim 'TESTaud' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	@Test
	public void audienceSingleValueOk() throws Exception {
		audienceSingleValueOk(TipoServizio.EROGAZIONE);
	}
	@Test
	public void audienceSingleValueOkFruizione() throws Exception {
		audienceSingleValueOk(TipoServizio.FRUIZIONE);
	}
	private void audienceSingleValueOk(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_singleValueNoArrayAudience(false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "singleAuditValueNoArray", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void audienceSingleValueKo() throws Exception {
		audienceSingleValueKo(TipoServizio.EROGAZIONE);
	}
	@Test
	public void audienceSingleValueKoFruizione() throws Exception {
		audienceSingleValueKo(TipoServizio.FRUIZIONE);
	}
	private void audienceSingleValueKo(TipoServizio tipoServizio) throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT_singleValueNoArrayAudience(true,
						mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "singleAuditValueNoArray", headers,  null,
				"(Token claim 'TESTaud' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	@Test
	public void usernameInvalid1() throws Exception {
		usernameInvalid1(TipoServizio.EROGAZIONE);
	}
	@Test
	public void usernameInvalid1Fruizione() throws Exception {
		usernameInvalid1(TipoServizio.FRUIZIONE);
	}
	private void usernameInvalid1(TipoServizio tipoServizio) throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_invalid(false, false, true, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"(Token claim 'TESTusername' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente_usernameInvalid, mapExpectedTokenInfo);
	}
	@Test
	public void usernameInvalid2() throws Exception {
		usernameInvalid2(TipoServizio.EROGAZIONE);
	}
	@Test
	public void usernameInvalid2Fruizione() throws Exception {
		usernameInvalid2(TipoServizio.FRUIZIONE);
	}
	private void usernameInvalid2(TipoServizio tipoServizio) throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_invalid(false, false, false, false, // tutti corretti 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username+"ERRORE"); // l'errore e' nel valore dinamico dell'header usato per il confronto
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"(Token claim 'TESTusername' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	@Test
	public void claimUnexpected() throws Exception {
		claimUnexpected(TipoServizio.EROGAZIONE);
	}
	@Test
	public void claimUnexpectedFruizione() throws Exception {
		claimUnexpected(TipoServizio.FRUIZIONE);
	}
	private void claimUnexpected(TipoServizio tipoServizio) throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJWT_invalid(false, false, false, true, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  null,
				"(Token unexpected claim 'TESTnonEsistente') La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	
	@Test
	public void notSuccess() throws Exception {
		notSuccess(TipoServizio.EROGAZIONE);
	}
	@Test
	public void notSuccessFruizione() throws Exception {
		notSuccess(TipoServizio.FRUIZIONE);
	}
	private void notSuccess(TipoServizio tipoServizio) throws Exception {
		
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
		
		Utilities._test(logCore, tipoServizio, validazione, "not", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void notClientIdInvalid() throws Exception {
		notClientIdInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void notClientIdInvalidFruizione() throws Exception {
		notClientIdInvalid(TipoServizio.FRUIZIONE);
	}
	private void notClientIdInvalid(TipoServizio tipoServizio) throws Exception {
		
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
						false, false, false, false,
						true, 
						false, false, 
						false, false,
						mapExpectedTokenInfo));
		Utilities._test(logCore, tipoServizio, validazione, "not", headers,  null,
				"(Token claim 'TESTclient_id' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente_clientIdInvalid, mapExpectedTokenInfo);
	}
	
	@Test
	public void notAudienceInvalid() throws Exception {
		notAudienceInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void notAudienceInvalidFruizione() throws Exception {
		notAudienceInvalid(TipoServizio.FRUIZIONE);
	}
	private void notAudienceInvalid(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "not", headers,  null,
				"(Token claim 'TESTaud' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void notIssInvalid() throws Exception {
		notIssInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void notIssInvalidFruizione() throws Exception {
		notIssInvalid(TipoServizio.FRUIZIONE);
	}
	private void notIssInvalid(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "not", headers,  null,
				"(Token claim 'TESTiss' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void notRoleInvalid() throws Exception {
		notRoleInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void notRoleInvalidFruizione() throws Exception {
		notRoleInvalid(TipoServizio.FRUIZIONE);
	}
	private void notRoleInvalid(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ buildJWT_roles(true,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "not", headers,  null,
				"(Token claim 'TESTrole' with unexpected value (regExpr not match failed)) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void notUsernameInvalid() throws Exception {
		notUsernameInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void notUsernameInvalidFruizione() throws Exception {
		notUsernameInvalid(TipoServizio.FRUIZIONE);
	}
	private void notUsernameInvalid(TipoServizio tipoServizio) throws Exception {
		
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
				false, false, false, false,
				false, 
				false, false, 
				true, false,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "not", headers,  null,
				"(Token claim 'TESTusername' with unexpected value (regExpr not find failed)) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente_usernameInvalid, mapExpectedTokenInfo);
	}
	
	
	
	
	
	
	
	@Test
	public void notOnlyAuthzContenutiSuccess() throws Exception {
		notOnlyAuthzContenutiSuccess(TipoServizio.EROGAZIONE);
	}
	@Test
	public void notOnlyAuthzContenutiSuccessFruizione() throws Exception {
		notOnlyAuthzContenutiSuccess(TipoServizio.FRUIZIONE);
	}
	private void notOnlyAuthzContenutiSuccess(TipoServizio tipoServizio) throws Exception {
		
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
		
		Utilities._test(logCore, tipoServizio, validazione, "notOnlyAuthzContenuti", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void notOnlyAuthzContenutiClientIdInvalid() throws Exception {
		notOnlyAuthzContenutiClientIdInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void notOnlyAuthzContenutiClientIdInvalidFruizione() throws Exception {
		notOnlyAuthzContenutiClientIdInvalid(TipoServizio.FRUIZIONE);
	}
	private void notOnlyAuthzContenutiClientIdInvalid(TipoServizio tipoServizio) throws Exception {
		
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
						false, false, false, false,
						true, 
						false, false, 
						false, false,
						mapExpectedTokenInfo));
		Utilities._test(logCore, tipoServizio, validazione, "notOnlyAuthzContenuti", headers,  null,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ?
						"(Resource '${tokenInfo:clientId}' with unauthorized value '18192.apps.invalid') Il chiamante non è autorizzato ad invocare l'API"
						:
						"(Resource '${tokenInfo:clientId}' with unauthorized value '18192.apps.invalid')  Servizio non invocabile con il contenuto applicativo fornito dal servizio applicativo Anonimo",
				Utilities.credenzialiMittente_clientIdInvalid, mapExpectedTokenInfo);
	}
	
	@Test
	public void notOnlyAuthzContenutiAudienceInvalid() throws Exception {
		notOnlyAuthzContenutiAudienceInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void notOnlyAuthzContenutiAudienceInvalidFruizione() throws Exception {
		notOnlyAuthzContenutiAudienceInvalid(TipoServizio.FRUIZIONE);
	}
	private void notOnlyAuthzContenutiAudienceInvalid(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "23223.apps");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "notOnlyAuthzContenuti", headers,  null,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ?
						"(Resource '${tokenInfo:aud[0]}' with unauthorized value '23223.apps') Il chiamante non è autorizzato ad invocare l'API"
						:
						"(Resource '${tokenInfo:aud[0]}' with unauthorized value '23223.apps')  Servizio non invocabile con il contenuto applicativo fornito dal servizio applicativo Anonimo",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void notOnlyAuthzContenutiIssInvalid() throws Exception {
		notOnlyAuthzContenutiIssInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void notOnlyAuthzContenutiIssInvalidFruizione() throws Exception {
		notOnlyAuthzContenutiIssInvalid(TipoServizio.FRUIZIONE);
	}
	private void notOnlyAuthzContenutiIssInvalid(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "notOnlyAuthzContenuti", headers,  null,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ?
						"(Resource '${tokenInfo:iss}' with unauthorized value 'testAuthEnte') Il chiamante non è autorizzato ad invocare l'API"
						:
						"(Resource '${tokenInfo:iss}' with unauthorized value 'testAuthEnte')  Servizio non invocabile con il contenuto applicativo fornito dal servizio applicativo Anonimo",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void notOnlyAuthzContenutiRoleInvalid() throws Exception {
		notOnlyAuthzContenutiRoleInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void notOnlyAuthzContenutiRoleInvalidFruizione() throws Exception {
		notOnlyAuthzContenutiRoleInvalid(TipoServizio.FRUIZIONE);
	}
	private void notOnlyAuthzContenutiRoleInvalid(TipoServizio tipoServizio) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+ buildJWT_roles(true,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "notOnlyAuthzContenuti", headers,  null,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ?
						"(Resource '${tokenInfo:roles[0]}' with unexpected value 'https://r1' (regExpr not match failed)) Il chiamante non è autorizzato ad invocare l'API"
						:
						"(Resource '${tokenInfo:roles[0]}' with unexpected value 'https://r1' (regExpr not match failed))  Servizio non invocabile con il contenuto applicativo fornito dal servizio applicativo Anonimo",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void notOnlyAuthzContenutiUsernameInvalid() throws Exception {
		notOnlyAuthzContenutiUsernameInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void notOnlyAuthzContenutiUsernameInvalidFruizione() throws Exception {
		notOnlyAuthzContenutiUsernameInvalid(TipoServizio.FRUIZIONE);
	}
	private void notOnlyAuthzContenutiUsernameInvalid(TipoServizio tipoServizio) throws Exception {
		
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
				false, false, false, false,
				false, 
				false, false, 
				true, false,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "notOnlyAuthzContenuti", headers,  null,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ?
						"Resource '${tokenInfo:username}' with unexpected value 'usernameErrato' (regExpr not find failed)) Il chiamante non è autorizzato ad invocare l'API"
						:
						"Resource '${tokenInfo:username}' with unexpected value 'usernameErrato' (regExpr not find failed))  Servizio non invocabile con il contenuto applicativo fornito dal servizio applicativo Anonimo",
				Utilities.credenzialiMittente_usernameInvalid, mapExpectedTokenInfo);
	}
	
	
	
	
	
	
	
	
	@Test
	public void ignoreCaseSuccess() throws Exception {
		ignoreCaseSuccess(TipoServizio.EROGAZIONE);
	}
	@Test
	public void ignoreCaseSuccessFruizione() throws Exception {
		ignoreCaseSuccess(TipoServizio.FRUIZIONE);
	}
	private void ignoreCaseSuccess(TipoServizio tipoServizio) throws Exception {
		
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
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCase", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	@Test
	public void ignoreCaseIssInvalid() throws Exception {
		ignoreCaseIssInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void ignoreCaseIssInvalidFruizione() throws Exception {
		ignoreCaseIssInvalid(TipoServizio.FRUIZIONE);
	}
	private void ignoreCaseIssInvalid(TipoServizio tipoServizio) throws Exception {
		
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
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCase", headers,  null,
				"(Token claim 'TESTiss' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void ignoreCaseUserInfoFullNameInvalid() throws Exception {
		ignoreCaseUserInfoFullNameInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void ignoreCaseUserInfoFullNameInvalidFruizione() throws Exception {
		ignoreCaseUserInfoFullNameInvalid(TipoServizio.FRUIZIONE);
	}
	private void ignoreCaseUserInfoFullNameInvalid(TipoServizio tipoServizio) throws Exception {
		
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
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCase", headers,  null,
				"(Token claim 'TESTname' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void ignoreCaseUserInfoFirstNameInvalid() throws Exception {
		ignoreCaseUserInfoFirstNameInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void ignoreCaseUserInfoFirstNameInvalidFruizione() throws Exception {
		ignoreCaseUserInfoFirstNameInvalid(TipoServizio.FRUIZIONE);
	}
	private void ignoreCaseUserInfoFirstNameInvalid(TipoServizio tipoServizio) throws Exception {
		
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
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCase", headers,  null,
				"(Token claim 'TESTgiven_name' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	
	
	
	@Test
	public void ignoreCaseOnlyAuthzContenutiSuccess() throws Exception {
		ignoreCaseOnlyAuthzContenutiSuccess(TipoServizio.EROGAZIONE);
	}
	@Test
	public void ignoreCaseOnlyAuthzContenutiSuccessFruizione() throws Exception {
		ignoreCaseOnlyAuthzContenutiSuccess(TipoServizio.FRUIZIONE);
	}
	private void ignoreCaseOnlyAuthzContenutiSuccess(TipoServizio tipoServizio) throws Exception {
		
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
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCaseOnlyAuthzContenuti", headers,  null,
				null,
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	@Test
	public void ignoreCaseOnlyAuthzContenutiIssInvalid() throws Exception {
		ignoreCaseOnlyAuthzContenutiIssInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void ignoreCaseOnlyAuthzContenutiIssInvalidFruizione() throws Exception {
		ignoreCaseOnlyAuthzContenutiIssInvalid(TipoServizio.FRUIZIONE);
	}
	private void ignoreCaseOnlyAuthzContenutiIssInvalid(TipoServizio tipoServizio) throws Exception {
		
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
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCaseOnlyAuthzContenuti", headers,  null,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ?
					"(Resource '${tokenInfo:iss}' with unauthorized value 'testAuthEnte') Il chiamante non è autorizzato ad invocare l'API"
					:
					"(Resource '${tokenInfo:iss}' with unauthorized value 'testAuthEnte')  Servizio non invocabile con il contenuto applicativo fornito dal servizio applicativo Anonimo",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void ignoreCaseOnlyAuthzContenutiUserInfoFullNameInvalid() throws Exception {
		ignoreCaseOnlyAuthzContenutiUserInfoFullNameInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void ignoreCaseOnlyAuthzContenutiUserInfoFullNameInvalidFruizione() throws Exception {
		ignoreCaseOnlyAuthzContenutiUserInfoFullNameInvalid(TipoServizio.FRUIZIONE);
	}
	private void ignoreCaseOnlyAuthzContenutiUserInfoFullNameInvalid(TipoServizio tipoServizio) throws Exception {
		
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
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCaseOnlyAuthzContenuti", headers,  null,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ?
						"(Resource '${tokenInfo:userInfo.fullName}' with unexpected value 'Mario Bianchi Rossi') Il chiamante non è autorizzato ad invocare l'API"
						:
						"(Resource '${tokenInfo:userInfo.fullName}' with unexpected value 'Mario Bianchi Rossi')  Servizio non invocabile con il contenuto applicativo fornito dal servizio applicativo Anonimo",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	@Test
	public void ignoreCaseOnlyAuthzContenutiUserInfoFirstNameInvalid() throws Exception {
		ignoreCaseOnlyAuthzContenutiUserInfoFirstNameInvalid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void ignoreCaseOnlyAuthzContenutiUserInfoFirstNameInvalidFruizione() throws Exception {
		ignoreCaseOnlyAuthzContenutiUserInfoFirstNameInvalid(TipoServizio.FRUIZIONE);
	}
	private void ignoreCaseOnlyAuthzContenutiUserInfoFirstNameInvalid(TipoServizio tipoServizio) throws Exception {
		
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
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCaseOnlyAuthzContenuti", headers,  null,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ? 
						"(Resource '${tokenInfo:userInfo.firstName}' with unexpected value 'Mario') Il chiamante non è autorizzato ad invocare l'API"
						:
						"(Resource '${tokenInfo:userInfo.firstName}' with unexpected value 'Mario')  Servizio non invocabile con il contenuto applicativo fornito dal servizio applicativo Anonimo",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
	}
	
	
	
	
	
	
	
	
	
	
	
	@Test
	public void successValidazioneHeader() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWTHeader("JWT", "test/JWT", null,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazioneHeader, "success", headers,  null,
				null,
				null, mapExpectedTokenInfo);
	}
	
	@Test
	public void success2ValidazioneHeader() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWTHeader("jws", "Application/Jws", null,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazioneHeader, "success", headers,  null,
				null,
				null, mapExpectedTokenInfo);
	}
	
	@Test
	public void invalidValidazioneHeaderTyp() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		String tokenInvalid = buildJWTHeader("AltroValore", "Application/Jws", null,
				null);
		
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				tokenInvalid);
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazioneHeader, "success", headers,  null,
				"Validazione del token 'JWS' fallita: Token non valido: JWT header validation failed; Claim 'typ' with invalid value 'AltroValore'",
				null, Utilities.getMapExpectedTokenInfoInvalid(tokenInvalid));
	}
	
	@Test
	public void invalidValidazioneHeaderCty() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		String tokenInvalid = buildJWTHeader("jws", "ValoreNonCorretto", null,
				null);
		
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				tokenInvalid);
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazioneHeader, "success", headers,  null,
				"Validazione del token 'JWS' fallita: Token non valido: JWT header validation failed; Claim 'cty' with invalid value 'ValoreNonCorretto'",
				null, Utilities.getMapExpectedTokenInfoInvalid(tokenInvalid));
	}
	
	
	
	
	
	
	@Test
	public void successValidazioneHeaderRFC9068() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWTHeader("at+jwt", "application/json", null,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazioneHeaderRFC9068, "success", headers,  null,
				null,
				null, mapExpectedTokenInfo);
	}
	
	@Test
	public void success2ValidazioneHeaderRFC9068() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWTHeader("application/at+jwt", "application/JSON", null,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazioneHeaderRFC9068, "success", headers,  null,
				null,
				null, mapExpectedTokenInfo);
	}
	
	@Test
	public void invalidValidazioneHeaderTypRFC9068() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		String tokenInvalid = buildJWTHeader("AltroValore", "application/json", null,
				null);
		
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				tokenInvalid);
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazioneHeaderRFC9068, "success", headers,  null,
				"Validazione del token 'JWS' fallita: Token non valido: JWT header validation failed; Claim 'typ' with invalid value 'AltroValore'",
				null, Utilities.getMapExpectedTokenInfoInvalid(tokenInvalid));
	}
	
	@Test
	public void invalidValidazioneHeaderCtyRFC9068() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		String tokenInvalid = buildJWTHeader("application/at+jwt", "ValoreNonCorretto", null,
				null);
		
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				tokenInvalid);
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazioneHeaderRFC9068, "success", headers,  null,
				"Validazione del token 'JWS' fallita: Token non valido: JWT header validation failed; Claim 'cty' with invalid value 'ValoreNonCorretto'",
				null, Utilities.getMapExpectedTokenInfoInvalid(tokenInvalid));
	}
	
	
	
	
	
	
	
	
	
	
	@Test
	public void successValidazioneHeaderRFC9068noCTY() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWTHeader("at+jwt", 
						"application/json", // anche se fornito viene ignorato 
						null,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazioneHeaderRFC9068noCTY, "success", headers,  null,
				null,
				null, mapExpectedTokenInfo);
	}
	
	@Test
	public void success2ValidazioneHeaderRFC9068noCTY() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWTHeader("application/at+jwt", 
						"application/JSON", // anche se fornito viene ignorato 
						null,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazioneHeaderRFC9068noCTY, "success", headers,  null,
				null,
				null, mapExpectedTokenInfo);
	}
	
	@Test
	public void invalidValidazioneHeaderTypRFC9068noCTY() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		String tokenInvalid = buildJWTHeader("AltroValore", "application/json", null,
				null);
		
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				tokenInvalid);
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazioneHeaderRFC9068noCTY, "success", headers,  null,
				"Validazione del token 'JWS' fallita: Token non valido: JWT header validation failed; Claim 'typ' with invalid value 'AltroValore'",
				null, Utilities.getMapExpectedTokenInfoInvalid(tokenInvalid));
	}
	
	@Test
	public void successInvalidValidazioneHeaderCtyRFC9068() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		String tokenInvalid = buildJWTHeader("application/at+jwt", 
				"ValoreNonCorretto", // anche se fornito viene ignorato 
				null,
				mapExpectedTokenInfo);
		
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				tokenInvalid);
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazioneHeaderRFC9068noCTY, "success", headers,  null,
				null,
				null, mapExpectedTokenInfo);

	}
	
	@Test
	public void successUndefinedValidazioneHeaderCtyRFC9068() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		String tokenInvalid = buildJWTHeader("application/at+jwt", 
				null, // cty non fornito
				null,
				mapExpectedTokenInfo);
		
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				tokenInvalid);
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazioneHeaderRFC9068noCTY, "success", headers,  null,
				null,
				null, mapExpectedTokenInfo);

	}
	
	
	
	
	
	
	
	
	
	
	@Test
	public void successValidazioneHeaderRFC9068noTYP() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWTHeader("at+jwt", // anche se fornito viene ignorato 
						"application/json", 
						null,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazioneHeaderRFC9068noTYP, "success", headers,  null,
				null,
				null, mapExpectedTokenInfo);
	}
	
	@Test
	public void successInvalidValidazioneHeaderTypRFC9068() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("test-username", Utilities.username);
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWTHeader("AT+JWT/ValoreNonCorretto", // anche se fornito viene ignorato 
						"application/json", 
						null,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazioneHeaderRFC9068noTYP, "success", headers,  null,
				null,
				null, mapExpectedTokenInfo);
	}
	
	@Test
	public void successUndefinedValidazioneHeaderTypRFC9068() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		String tokenInvalid = buildJWTHeader(null,  // cty non fornito
				"application/json", 
				null,
				mapExpectedTokenInfo);
		
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				tokenInvalid);
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazioneHeaderRFC9068noTYP, "success", headers,  null,
				null,
				null, mapExpectedTokenInfo);

	}
	
	@Test
	public void invalidValidazioneHeaderCtyRFC9068noTYP() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		String tokenInvalid = buildJWTHeader(null, "AltroValore", null,
				null);
		
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				tokenInvalid);
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazioneHeaderRFC9068noTYP, "success", headers,  null,
				"Validazione del token 'JWS' fallita: Token non valido: JWT header validation failed; Claim 'cty' with invalid value 'AltroValore'",
				null, Utilities.getMapExpectedTokenInfoInvalid(tokenInvalid));
	}
	
	
	
	
	private static String buildJWT_signInvalid() throws Exception {
		return buildJWT(true,
				true, true, true, true, true,
				true, true, true,
				true, true, true,
				true,
				false, false, false, false,
				false, 
				false, false, 
				false, false,
				null);
	}
	public static String buildJWT(boolean requiredClaims,
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
				false, false, false, false,
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
				false, false, false, false,
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
				false, false, false, false,
				false, 
				false, false, 
				false, false,
				mapExpectedTokenInfo);
	}
	private static String buildJWT_dates(boolean invalidIat, boolean futureIat, boolean invalidNbf, boolean invalidExp,
			List<String> mapExpectedTokenInfo) throws Exception {
		return buildJWT(true,
				true, true, true, true, true,
				true, true, true,
				true, true, true,
				false,
				invalidIat, futureIat, invalidNbf, invalidExp,
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
				false, false, false, false,
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
				false, false, false, false,
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
			boolean invalidIat, boolean futureIat, boolean invalidNbf, boolean invalidExp,
			boolean invalidClientId, 
			boolean singleValueNoArrayAudience,boolean invalidAudience,
			boolean invalidUsername, boolean invalidClaimCheNonDeveEsistere,
			List<String> mapExpectedTokenInfo) throws Exception {
		
		String jsonInput = Utilities.buildJson(requiredClaims, 
				requiredClaims_clientId, requiredClaims_issuer, requiredClaims_subject, 
				requiredClaims_username, requiredClaims_eMail, 
				scope1, scope2, scope3, 
				role1, role2, role3, 
				invalidIat, futureIat, invalidNbf, invalidExp, 
				invalidClientId, 
				singleValueNoArrayAudience, invalidAudience, 
				invalidUsername, invalidClaimCheNonDeveEsistere, 
				mapExpectedTokenInfo,
				"TEST"); 
		
		if(mapExpectedTokenInfo!=null) {
			mapExpectedTokenInfo.add("\"sourceType\":\"JWT\"");
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
	
	
	
	
	private static String buildJWTHeader(String typ,String cty,String algo,
			List<String> mapExpectedTokenInfo) throws Exception {
		
		String jsonInput = Utilities.buildJson(true, 
				false, false, false, 
				false, false, 
				false, false, false, 
				false, false, false, 
				false, false, false, false, 
				false, 
				false, false, 
				false, false, 
				mapExpectedTokenInfo,
				""); 
		
		if(mapExpectedTokenInfo!=null) {
			mapExpectedTokenInfo.add("\"sourceType\":\"JWT\"");
		}
		
		//System.out.println("TOKEN ["+jsonInput+"]");
		
		Properties props = new Properties();
		props.put("rs.security.keystore.type","JKS");
		String password = "openspcoop";
		props.put("rs.security.keystore.file", "/etc/govway/keys/erogatore.jks");
		props.put("rs.security.keystore.alias","erogatore");
		props.put("rs.security.keystore.password",password);
		props.put("rs.security.key.password",password);
		
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
			
		if(algo==null) {
			algo = "RS256";
		}
		props.put("rs.security.signature.algorithm",algo);
		props.put("rs.security.signature.include.cert","false");
		props.put("rs.security.signature.include.key.id","true");
		props.put("rs.security.signature.include.public.key","false");
		props.put("rs.security.signature.include.cert.sha1","false");
		props.put("rs.security.signature.include.cert.sha256","false");
			
		JwtHeaders jwtHeaders = new JwtHeaders();
		jwtHeaders.setType(typ);
		jwtHeaders.setContentType(cty);
		
		JsonSignature jsonSignature = new JsonSignature(props, jwtHeaders, options);
		String token = jsonSignature.sign(jsonInput);
		//System.out.println(token);
			
		return token;		
		
	}
}