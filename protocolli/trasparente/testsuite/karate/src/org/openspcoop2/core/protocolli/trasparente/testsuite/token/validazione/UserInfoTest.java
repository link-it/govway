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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
* ValidazioneTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class UserInfoTest extends ConfigLoader {

	public final static String validazione = "TestValidazioneToken-UserInfo";
		
	@Test
	public void success() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT(true, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				null,
				mapExpectedTokenInfo);
	}
	
	@Test
	public void notFound() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers, null,
				"Non è stato riscontrata la proprietà della URL 'test-userinfo' contenente il token",
				null);
	}
		
	
	@Test
	public void invalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_dates(false, false, false, false,  // tutte buone, elimino la risposta introspection dopo 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		File f = new File("/tmp/userinfoResponse.json");
		f.delete();
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"Risposta del servizio di UserInfo non valida: Connessione terminata con errore (codice trasporto: 500)",
				null);
	}
	
	
	@Test
	public void iatInvalid() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_dates(true, false, false, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"Token expired; iat time '%' too old",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void iatInTheFuture() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_dates(false, true, false, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"Token valid in the future; iat time '%' is in the future",
				mapExpectedTokenInfo);
	}
	
	
	@Test
	public void nbfInvalid() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_dates(false, false, true, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"Token not usable before %",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void expInvalid() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_dates(false, false, false, true, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"Token expired",
				mapExpectedTokenInfo);
	}
	
	
	@Test
	public void requiredClaims_clientId() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT(true,
				false, true, true, true, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without clientId claim",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaims_issuer() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT(true,
				true, false, true, true, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without issuer claim",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaims_subject() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT(true,
				true, true, false, true, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without subject claim",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaims_username() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT(true,
				true, true, true, false, true,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without username claim",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaims_eMail() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT(true,
				true, true, true, true, false,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"Autenticazione token (Issuer,ClientId,Subject,Username,eMail) fallita: Token without email claim",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void requiredClaims_disabilitato() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT(false,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "requiredClaims", headers,  query,
				null,
				mapExpectedTokenInfo);
	}

	
	
	
	@Test
	public void scopeAny() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_scope(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "scopeAny", headers,  query,
				null,
				mapExpectedTokenInfo);
	}
	
	@Test
	public void scopeAny2() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_scope(false, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "scopeAny", headers,  query,
				null,
				mapExpectedTokenInfo);
	}
	
	@Test
	public void scopeAnyKo() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_scope(false, false, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "scopeAny", headers,  query,
				"(Token without scopes) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void scopeKo() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_scope(true, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"(Scope '"+Utilities.s2+"' not found) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void scopeKo2() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_scope(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"(Scope '"+Utilities.s3+"' not found) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				mapExpectedTokenInfo);
	}
	
	
	
	@Test
	public void rolesAny() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_roles(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "rolesAny", headers,  query,null,
				mapExpectedTokenInfo);
	}
	
	@Test
	public void rolesAny2() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_roles(false, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "rolesAny", headers,  query,null,
				mapExpectedTokenInfo);
	}
	
	@Test
	public void rolesAnyKo() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_roles(false, false, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "rolesAny", headers,  query,
				"(Roles not found in request context) Il mittente non è autorizzato ad invocare il servizio gw/TestValidazioneToken-UserInfo (versione:1) erogato da gw/SoggettoInternoTest",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void rolesKo() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_roles(true, false, true,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"(Role 'r2' not found in request context) Il mittente non è autorizzato ad invocare il servizio gw/TestValidazioneToken-UserInfo (versione:1) erogato da gw/SoggettoInternoTest",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void rolesKo2() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_roles(true, true, false,
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"(Role 'r3' not found in request context) Il mittente non è autorizzato ad invocare il servizio gw/TestValidazioneToken-UserInfo (versione:1) erogato da gw/SoggettoInternoTest",
				mapExpectedTokenInfo);
	}
	
	
	
	@Test
	public void clientInvalid() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_invalid(true, false, false, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"(Token claim 'client_id' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				mapExpectedTokenInfo);
	}
	
	
	@Test
	public void audienceInvalid() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_invalid(false, true, false, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"(Token claim 'aud' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				mapExpectedTokenInfo);
	}
	
	
	@Test
	public void usernameInvalid1() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_invalid(false, false, true, false, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"(Token claim 'username' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				mapExpectedTokenInfo);
	}
	@Test
	public void usernameInvalid2() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_invalid(false, false, false, false, // tutti corretti 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username+"ERRORE"); // l'errore e' nel valore dinamico dell'header usato per il confronto
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"(Token claim 'username' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				mapExpectedTokenInfo);
	}
	
	
	@Test
	public void claimUnexpected() throws Exception {
				
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_invalid(false, false, false, true, 
				mapExpectedTokenInfo));
		headers.put("test-username", Utilities.username);
		
		Utilities._test(logCore, validazione, "success", headers,  query,
				"(Token unexpected claim 'nonEsistente') La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				mapExpectedTokenInfo);
	}
	
	
	
	
	
	@Test
	public void not_success() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "not", headers,  query,
				null,
				mapExpectedTokenInfo);
	}
	
	@Test
	public void not_clientIdInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo",
				buildJWT(true,
						true, true, true, true, true,
						true, true, true,
						false,true,false,
						false,
						false, false, false, false,
						true, false, false, false,
						mapExpectedTokenInfo));
		Utilities._test(logCore, validazione, "not", headers,  query,
				"(Token claim 'client_id' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void not_audienceInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "not", headers,  query,
				"(Token claim 'aud' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void not_issInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte");
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_roles(false,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "not", headers,  query,
				"(Token claim 'iss' with unauthorized value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void not_roleInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", buildJWT_roles(true,true,false, 
						mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "not", headers,  query,
				"(Token claim 'role' with unexpected value (regExpr not match failed)) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				mapExpectedTokenInfo);
	}
	
	@Test
	public void not_usernameInvalid() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		List<String> mapExpectedTokenInfo = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("test-username", Utilities.username);
		headers.put("test-not-aud", "7777.apps.invalid");
		headers.put("test-not-iss", "testAuthEnte.invalid");
		Map<String, String> query = new HashMap<String, String>();
		query.put("test-userinfo", 
				buildJWT(true,
				true, true, true, true, true,
				true, true, true,
				false,true,false,
				false,
				false, false, false, false,
				false, false, true, false,
				mapExpectedTokenInfo));
		
		Utilities._test(logCore, validazione, "not", headers,  query,
				"(Token claim 'username' with unexpected value (regExpr not find failed)) La richiesta presenta un token non sufficiente per fruire del servizio richiesto",
				mapExpectedTokenInfo);
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
				false, false, false, false,
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
				false, false, false, false,
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
				false, false, false, false,
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
				false, false, false, false,
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
				invalidClientId, invalidAudience, invalidUsername, invalidClaimCheNonDeveEsistere,
				mapExpectedTokenInfo);
	}			
	private static String buildJWT(boolean requiredClaims,
			boolean requiredClaims_clientId, boolean requiredClaims_issuer, boolean requiredClaims_subject,
			boolean requiredClaims_username, boolean requiredClaims_eMail,
			boolean scope1, boolean scope2, boolean scope3,
			boolean role1, boolean role2, boolean role3,
			boolean signWithSoggetto1,
			boolean invalidIat, boolean futureIat, boolean invalidNbf, boolean invalidExp,
			boolean invalidClientId, boolean invalidAudience, boolean invalidUsername, boolean invalidClaimCheNonDeveEsistere,
			List<String> mapExpectedTokenInfo) throws Exception {
		
		String jsonInput = Utilities.buildJson(requiredClaims, 
				requiredClaims_clientId, requiredClaims_issuer, requiredClaims_subject, 
				requiredClaims_username, requiredClaims_eMail, 
				scope1, scope2, scope3, 
				role1, role2, role3, 
				invalidIat, futureIat, invalidNbf, invalidExp, 
				invalidClientId, invalidAudience, invalidUsername, invalidClaimCheNonDeveEsistere, 
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