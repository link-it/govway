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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
* UserInfoTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class UserInfoTest extends ConfigLoader {

	public static final String validazione = "TestValidazioneToken-UserInfo";
		
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT(true, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
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
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers, null,
				"Non è stato riscontrata la proprietà della URL 'test-userinfo' contenente il token",
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
		
		List<String> mapExpectedTokenInfo = new ArrayList<>();
		String tokenInvalid = buildJWT_dates(false, false, false, false,  // tutte buone, elimino la risposta introspection dopo 
				mapExpectedTokenInfo);
		
		Map<String, String> headers = new HashMap<>();
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", tokenInvalid);
		headers.put("test-username", Utilities.username);
		
		// serve ad eliminare il file creato con i precedenti test
		File f = new File("/tmp/userinfoResponse.json");
		f.delete();
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
				"Risposta del servizio di UserInfo non valida: Connessione terminata con errore (codice trasporto: 500)",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_dates(true, false, false, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_dates(false, true, false, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_dates(false, false, true, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_dates(false, false, false, true, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT(true,
				false, true, true, true, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT(true,
				true, false, true, true, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT(true,
				true, true, false, true, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT(true,
				true, true, true, false, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT(true,
				true, true, true, true, false,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT(false,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "requiredClaims", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_scope(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "scopeAny", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_scope(false, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "scopeAny", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_scope(false, false, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "scopeAny", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_scope(true, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_scope(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_roles(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "rolesAny", headers,  query,null,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_roles(false, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "rolesAny", headers,  query,null,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_roles(false, false, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "rolesAny", headers,  query,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ?
						"(Roles not found in request context) Il mittente non è autorizzato ad invocare il servizio gw/TestValidazioneToken-UserInfo (versione:1) erogato da gw/SoggettoInternoTest"
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_roles(true, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ?
						"(Role 'r2' not found in request context) Il mittente non è autorizzato ad invocare il servizio gw/TestValidazioneToken-UserInfo (versione:1) erogato da gw/SoggettoInternoTest"
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_roles(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ?
						"(Role 'r3' not found in request context) Il mittente non è autorizzato ad invocare il servizio gw/TestValidazioneToken-UserInfo (versione:1) erogato da gw/SoggettoInternoTest"
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_invalid(true, false, false, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
				"(Token claim 'client_id' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_invalid(false, true, false, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
				"(Token claim 'aud' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_singleValueNoArrayAudience(false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_singleValueNoArrayAudience(true, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
				"(Token claim 'aud' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_invalid(false, false, true, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
				"(Token claim 'username' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_invalid(false, false, false, false, // tutti corretti 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username+"ERRORE"); // l'errore e' nel valore dinamico dell'header usato per il confronto
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
				"(Token claim 'username' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_invalid(false, false, false, true, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, tipoServizio, validazione, "success", headers,  query,
				"(Token unexpected claim 'nonEsistente') La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "not", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo",
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
		Utilities._test(logCore, tipoServizio, validazione, "not", headers,  query,
				"(Token claim 'client_id' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "not", headers,  query,
				"(Token claim 'aud' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "not", headers,  query,
				"(Token claim 'iss' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", buildJWT_roles(true,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "not", headers,  query,
				"(Token claim 'role' with unexpected value (regExpr not match failed)) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
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
		
		Utilities._test(logCore, tipoServizio, validazione, "not", headers,  query,
				"(Token claim 'username' with unexpected value (regExpr not find failed)) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "notOnlyAuthzContenuti", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
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
		Utilities._test(logCore, tipoServizio, validazione, "notOnlyAuthzContenuti", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "notOnlyAuthzContenuti", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "notOnlyAuthzContenuti", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
				buildJWT_roles(true,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "notOnlyAuthzContenuti", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
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
		
		Utilities._test(logCore, tipoServizio, validazione, "notOnlyAuthzContenuti", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCase", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCase", headers,  query,
				"(Token claim 'iss' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCase", headers,  query,
				"(Token claim 'name' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCase", headers,  query,
				"(Token claim 'given_name' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCaseOnlyAuthzContenuti", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCaseOnlyAuthzContenuti", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCaseOnlyAuthzContenuti", headers,  query,
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
		Map<String, String> query = new HashMap<>();
		query.put("test-userinfo", 
				buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, tipoServizio, validazione, "ignoreCaseOnlyAuthzContenuti", headers,  query,
				TipoServizio.EROGAZIONE.equals(tipoServizio) ?
						"(Resource '${tokenInfo:userInfo.firstName}' with unexpected value 'Mario') Il chiamante non è autorizzato ad invocare l'API"
						:
						"(Resource '${tokenInfo:userInfo.firstName}' with unexpected value 'Mario')  Servizio non invocabile con il contenuto applicativo fornito dal servizio applicativo Anonimo",
				Utilities.credenzialiMittente, mapExpectedTokenInfo);
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
			boolean singleValueNoArrayAudience, boolean invalidAudience, 
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
				""); 
		
		if(mapExpectedTokenInfo!=null) {
			mapExpectedTokenInfo.add("\"sourceType\":\"USER_INFO\"");
		}
		
		//System.out.println("TOKEN ["+jsonInput+"]");
		
		File f = new File("/tmp/userinfoResponse.json");
		FileSystemUtilities.writeFile(f, jsonInput.getBytes());
			
		return "access-token-opaco";		
		
	}
}