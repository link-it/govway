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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

import net.minidev.json.JSONObject;

/**
* ValidazioneTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Utilities extends ConfigLoader {

	public static HttpResponse _test(Logger logCore, String api, String operazione,
			Map<String, String> headers, Map<String, String> queryParameters, String msgError,
			List<String> mapExpectedTokenInfo) throws Exception {
		
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		
		String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione;
		if(queryParameters!=null && !queryParameters.isEmpty()) {
			for (String key : queryParameters.keySet()) {
				url+= url.contains("?") ? "&" : "?";
				url+=key;
				url+="=";
				url+=queryParameters.get(key);
			}
		}
		
		HttpRequest request = new HttpRequest();
		
		if(headers!=null && !headers.isEmpty()) {
			for (String hdrName : headers.keySet()) {
				request.addHeader(hdrName, headers.get(hdrName));
			}
		}
		
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		request.setContent(content);
		request.setUrl(url);
						
		HttpResponse response = null;
		try {
			//System.out.println("INVOKE ["+request.getUrl()+"]");
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		long esitoExpected = EsitiProperties.getInstance(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if(msgError!=null) {
			
			int code = -1;
			String error = null;
			String msg = null;
			
			if(msgError.contains("La richiesta presenta un token non sufficiente per fruire del servizio richiesto")) {
				esitoExpected = EsitiProperties.getInstance(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTORIZZAZIONE);
				code = 403;
				if(msgError.contains("Scope ") || msgError.contains("scopes")) {
					error = "AuthorizationMissingScope";
					msg = "Required token scopes not found";
				}
			}
			else if(msgError.contains("Il mittente non è autorizzato ad invocare il servizio gw/TestValidazioneToken-")){
				esitoExpected = EsitiProperties.getInstance(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTORIZZAZIONE);
				code = 403;
				if(msgError.contains("Role ") || msgError.contains("roles")) {
					error = "AuthorizationMissingRole";
					msg = "Authorization deny by role";
				}
			}
			else if(msgError.contains("Token without ")) {
				esitoExpected = EsitiProperties.getInstance(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTENTICAZIONE_TOKEN);
				code = 401;
				error = "TokenRequiredClaimsNotFound";
				msg = "Required token claims not found";
			}
			else if(msgError.contains("Non è stato riscontrato un token nella posizione [RFC 6750 - Bearer Token Usage]")||
					msgError.contains("Non è stato riscontrato l'header http 'test-introspection' contenente il token") ||
					msgError.contains("Non è stato riscontrata la proprietà della URL 'test-userinfo' contenente il token")) {
				esitoExpected = EsitiProperties.getInstance(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.TOKEN_NON_PRESENTE);
				code = 401;
				error = "TokenAuthenticationRequired";
				msg = "A token is required";
			}
			else if(msgError.contains("Validazione del token 'JWS' fallita:") ||
					msgError.contains("Risposta del servizio di Introspection non valida")  ||
					msgError.contains("Risposta del servizio di UserInfo non valida")) {
				esitoExpected = EsitiProperties.getInstance(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_TOKEN);
				code = 401;
				error = "TokenAuthenticationFailed";
				msg = "Invalid token";
			}
			else if(msgError.contains("Token expired")) {
				esitoExpected = EsitiProperties.getInstance(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_TOKEN);
				code = 401;
				error = "TokenExpired";
				msg = "Expired token";
			}
			else if(msgError.contains("Token not usable before")) {
				esitoExpected = EsitiProperties.getInstance(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_TOKEN);
				code = 401;
				error = "TokenNotBefore";
				msg = "Invalid 'notBefore' token claim";
			}
			verifyKo(response, error, code, msg, true);
		
		}
		else {
			verifyOk(response, 200, contentType);
		}
						
		DBVerifier.verify(idTransazione, esitoExpected, msgError,
				mapExpectedTokenInfo);
		
		return response;
		
	}
	
	public static void verifyOk(HttpResponse response, int code, String expectedContentType) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(expectedContentType, response.getContentType());
		
	}
	
	public static void verifyKo(HttpResponse response, String error, int code, String errorMsg, boolean checkErrorTypeGovWay) {
		
		assertEquals(code, response.getResultHTTPOperation());
		
		if(error!=null) {
			assertEquals(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807, response.getContentType());
			
			try {
				JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
				JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(response.getContent()));
				
				assertEquals("https://govway.org/handling-errors/"+code+"/"+error+".html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
				assertEquals(error, jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
				assertEquals(code, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
				assertNotEquals(null, jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0));	
				assertEquals(true, jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0).equals(errorMsg));
				
				if(checkErrorTypeGovWay) {
					assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
				}
	
				if(code==504) {
					assertNotNull(response.getHeaderFirstValue(HttpConstants.RETRY_AFTER));
				}
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public final static String username = "Utente di Prova";
	
	public final static String s1 = "https://userinfo.email";
	public final static String s2 = "https://userinfo.profile";
	public final static String s3 = "s3";
	
	public final static String r1 = "https://r1";
	public final static String r2 = "https://r2";
	public final static String r3 = "r3";
	
	public static String buildJson(boolean requiredClaims,
			boolean requiredClaims_clientId, boolean requiredClaims_issuer, boolean requiredClaims_subject,
			boolean requiredClaims_username, boolean requiredClaims_eMail,
			boolean scope1, boolean scope2, boolean scope3,
			boolean role1, boolean role2, boolean role3,
			boolean invalidIat, boolean invalidNbf, boolean invalidExp,
			boolean invalidClientId, boolean invalidAudience, boolean invalidUsername, boolean invalidClaimCheNonDeveEsistere,
			List<String> mapExpectedTokenInfo,
			String prefix) throws Exception {
			
		String cliendId = "18192.apps";
		if(invalidClientId) {
			cliendId = "18192.apps.invalid";
		}
		String audience = "23223.apps";
		String audience2 = "7777.apps";
		if(invalidAudience) {
			audience = "23223.apps.invalid";
			audience2 = "7777.apps.invalid";
		}
		
		String issuer = "testAuthEnte";
		String subject = "10623542342342323";
		String jti = "33aa1676-1f9e-34e2-8515-0cfca111a188";
		Date now = DateManager.getDate();
		Date campione = new Date( (now.getTime()/1000)*1000);
		Date iat = invalidIat ? new Date(campione.getTime() - (1000*60*121)) : new Date(campione.getTime());
		Date nbf = invalidNbf ? new Date(campione.getTime() + (1000*60)) : new Date(campione.getTime() - (1000*20));
		Date exp = invalidExp ? new Date(campione.getTime() - (1000*20)) : new Date(campione.getTime() + (1000*60));
		String fullName = "Mario Bianchi Rossi";
		String firstName = "Mario";
		String middleName = "Bianchi";
		String familyName = "Rossi";
		String email = "mariorossi@govway.org";
		
		String aud = "\""+prefix+"aud\":[\""+audience+"\",\""+audience2+"\"]";
		String jsonInput = 
				"{ "+aud+",";
		if(mapExpectedTokenInfo!=null) {
			mapExpectedTokenInfo.add(aud);
		}
		
		if(requiredClaims) {
			if(requiredClaims_clientId) {
				String clientId = "\""+prefix+"client_id\":\""+cliendId+"\"";
				jsonInput = jsonInput+
					clientId+" ,";
				if(mapExpectedTokenInfo!=null) {
					mapExpectedTokenInfo.add(clientId);
				}
			}
			if(requiredClaims_subject) {
				String sub = "\""+prefix+"sub\":\""+subject+"\"";
				jsonInput = jsonInput+
						sub+" , ";
				if(mapExpectedTokenInfo!=null) {
					mapExpectedTokenInfo.add(sub);
				}
			}
			if(requiredClaims_issuer) {
				String iss ="\""+prefix+"iss\":\""+issuer+"\"";
				jsonInput = jsonInput+
						iss+" , ";
				if(mapExpectedTokenInfo!=null) {
					mapExpectedTokenInfo.add(iss);
				}
			}
			if(requiredClaims_username) {
				String u = "\""+prefix+"username\":\""+(invalidUsername ? "usernameErrato" : username)+"\"";
				String name = "\""+prefix+"name\":\""+fullName+"\"";
				jsonInput = jsonInput+
						u+" ,"+
						name+" , ";
				if(mapExpectedTokenInfo!=null) {
					mapExpectedTokenInfo.add(u);
					mapExpectedTokenInfo.add(name);
				}
			}
			if(requiredClaims_eMail) {
				String mail = "\""+prefix+"email\":\""+email+"\"";
				jsonInput = jsonInput+
						mail+" , ";
				if(mapExpectedTokenInfo!=null) {
					mapExpectedTokenInfo.add(mail);
				}
			}
		}
		
		String scopeValue = "";
		if(scope1) {
			if(scopeValue.length()>0) {
				scopeValue+=" ";
			}
			scopeValue+=s1;
		}
		if(scope2) {
			if(scopeValue.length()>0) {
				scopeValue+=" ";
			}
			scopeValue+=s2;
		}
		if(scope3) {
			if(scopeValue.length()>0) {
				scopeValue+=" ";
			}
			scopeValue+=s3;
		}
		if(scopeValue.length()>0) {
			String s = "\""+prefix+"scope\":\""+scopeValue+"\"";
			jsonInput = jsonInput +
					s+" ,";
			if(mapExpectedTokenInfo!=null) {
				mapExpectedTokenInfo.add(s);
			}
		}
		
		String roleValue = "";
		if(role1) {
			if(roleValue.length()>0) {
				roleValue+=",";
			}
			else {
				roleValue+="[";
			}
			roleValue+="\""+r1+"\"";
		}
		if(role2) {
			if(roleValue.length()>0) {
				roleValue+=",";
			}
			else {
				roleValue+="[";
			}
			roleValue+="\""+r2+"\"";
		}
		if(role3) {
			if(roleValue.length()>0) {
				roleValue+=",";
			}
			else {
				roleValue+="[";
			}
			roleValue+="\""+r3+"\"";
		}
		if(roleValue.length()>0) {
			String r = "\""+prefix+"role\":"+roleValue+"]";
			jsonInput = jsonInput +
					r+" ,";
			if(mapExpectedTokenInfo!=null) {
				mapExpectedTokenInfo.add(r);
			}
		}
		
		String iatS = "\""+prefix+"iat\":\""+(iat.getTime()/1000)+"\""; 
		jsonInput = jsonInput +
				iatS + " , ";
		if(mapExpectedTokenInfo!=null) {
			mapExpectedTokenInfo.add(iatS);
		}
		
		String nbfS = "\""+prefix+"nbf\":\""+(nbf.getTime()/1000)+"\"";
		jsonInput = jsonInput +
				nbfS+ " , ";
		if(mapExpectedTokenInfo!=null) {
			mapExpectedTokenInfo.add(nbfS);
		}
		
		String expS = "\""+prefix+"exp\":\""+(exp.getTime()/1000)+"\"";
		jsonInput = jsonInput +
				expS + " ,  ";
		if(mapExpectedTokenInfo!=null) {
			mapExpectedTokenInfo.add(expS);
		}
		
		if(invalidClaimCheNonDeveEsistere) {
			String nonEsistente = "\""+prefix+"nonEsistente\":\"TEST\"";
			jsonInput = jsonInput +
					nonEsistente+ " , ";
			if(mapExpectedTokenInfo!=null) {
				mapExpectedTokenInfo.add(nonEsistente);
			}
		}
		
		String gName = "\""+prefix+"given_name\":\""+firstName+"\"";
		jsonInput = jsonInput +
				gName+" , ";
		if(mapExpectedTokenInfo!=null) {
			mapExpectedTokenInfo.add(gName);
		}
		
		String mName = "\""+prefix+"middle_name\":\""+middleName+"\"";
		jsonInput = jsonInput +
				mName+" , ";
		if(mapExpectedTokenInfo!=null) {
			mapExpectedTokenInfo.add(mName);
		}
		
		String fName = "\""+prefix+"family_name\":\""+familyName+"\"";
		jsonInput = jsonInput +
				fName+" , ";
		if(mapExpectedTokenInfo!=null) {
			mapExpectedTokenInfo.add(fName);
		}
		
		String jtiS = "\""+prefix+"jti\":\""+jti+"\"";
		jsonInput = jsonInput +
				" "+jtiS+"}";
		if(mapExpectedTokenInfo!=null) {
			mapExpectedTokenInfo.add(jtiS);
		}
		
		//System.out.println("TOKEN ["+jsonInput+"]");
		
		return jsonInput;
	}
}