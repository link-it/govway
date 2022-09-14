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

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JWTOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.JsonVerifySignature;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* ValidazioneTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ForwardInformazioniTest extends ConfigLoader {

	public final static String forward = "TestForwardInformazioniToken";
		
	@Test
	public void govwayHeaders() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		Map<String, List<String>> values = new HashMap<String, List<String>>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT(values));
		
		HttpResponse response = Utilities._test(logCore, forward, "govwayHeaders", headers,  null,
				null,
				null);
		
		checkGovWayHeaders(response, values);
	}
	
	@Test
	public void govwayJson() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		Map<String, List<String>> values = new HashMap<String, List<String>>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT(values));
		
		HttpResponse response = Utilities._test(logCore, forward, "govwayJson", headers,  null,
				null,
				null);
		
		checkGovWayJson("govway-testsuite-govway-token", response, values, true);
	}
	
	@Test
	public void govwayJws() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		Map<String, List<String>> values = new HashMap<String, List<String>>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT(values));
		
		HttpResponse response = Utilities._test(logCore, forward, "govwayJws", headers,  null,
				null,
				null);
		
		checkGovWayJwt("govway-testsuite-govway-jwt", response, values, true);
	}
	
	@Test
	public void jws() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		Map<String, List<String>> values = new HashMap<String, List<String>>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT(values));
		
		HttpResponse response = Utilities._test(logCore, forward, "jws", headers,  null,
				null,
				null);
		
		checkGovWayJwt("govway-testsuite-custom-jws", response, values, false);
	}
	
	@Test
	public void json() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		Map<String, List<String>> values = new HashMap<String, List<String>>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+
				buildJWT(values));
		
		HttpResponse response = Utilities._test(logCore, forward, "json", headers,  null,
				null,
				null);
		
		checkGovWayJson("govway-testsuite-custom-json", response, values, false);
	}
	
	private static String ID = "ID";
	private static String TIME = "TIME";
		
	private static void checkGovWayHeaders(HttpResponse response, Map<String, List<String>> values) throws Exception {
		
		if(response==null || response.getHeadersValues()==null || response.getHeadersValues().isEmpty()) {
			throw new Exception("Header non ritornati");
		}
		
		String format = "yyyy-MM-dd_HH:mm:ss.SSSZ";
				
		Map<String, String> expectedHeaders = new HashMap<String, String>();
		expectedHeaders.put("govway-token-processtime", TIME);
		expectedHeaders.put("govway-token-issuer", values.get("iss").get(0));
		expectedHeaders.put("govway-token-subject", values.get("sub").get(0));
		StringBuilder sbAud = new StringBuilder();
		for (String aud: values.get("aud")) {
			if(sbAud.length()>0) {
				sbAud.append(",");
			}
			sbAud.append(aud);
		}
		expectedHeaders.put("govway-token-audience", sbAud.toString());
		expectedHeaders.put("govway-token-clientid", values.get("clientId").get(0));
		long vDate = Long.parseLong(values.get("iat").get(0)) * 1000;
		expectedHeaders.put("govway-token-issuedat", org.openspcoop2.utils.date.DateUtils.getSimpleDateFormat(format).format(new Date(vDate)));
		vDate = Long.parseLong(values.get("exp").get(0)) * 1000;
		expectedHeaders.put("govway-token-expire", org.openspcoop2.utils.date.DateUtils.getSimpleDateFormat(format).format(new Date(vDate)));
		vDate = Long.parseLong(values.get("nbf").get(0)) * 1000;
		expectedHeaders.put("govway-token-nottobeusedbefore", org.openspcoop2.utils.date.DateUtils.getSimpleDateFormat(format).format(new Date(vDate)));
		StringBuilder sbRoles = new StringBuilder();
		for (String role: values.get("role")) {
			if(sbRoles.length()>0) {
				sbRoles.append(",");
			}
			sbRoles.append(role);
		}
		expectedHeaders.put("govway-token-roles", sbRoles.toString());
		StringBuilder sbScope = new StringBuilder();
		for (String scope: values.get("scope")) {
			if(sbScope.length()>0) {
				sbScope.append(",");
			}
			sbScope.append(scope);
		}
		expectedHeaders.put("govway-token-scopes", sbScope.toString());
		expectedHeaders.put("govway-token-username", values.get("username").get(0));
		expectedHeaders.put("govway-token-fullname", values.get("fullName").get(0));
		expectedHeaders.put("govway-token-firstname", values.get("firstName").get(0));
		expectedHeaders.put("govway-token-middlename", values.get("middleName").get(0));
		expectedHeaders.put("govway-token-familyname", values.get("familyName").get(0));
		expectedHeaders.put("govway-token-email", values.get("email").get(0));
		expectedHeaders.put("govway-token-jti", values.get("jti").get(0));
		expectedHeaders.put("govway-token-purposeid", values.get("purposeId").get(0));
		expectedHeaders.put("govway-token-customclaim", values.get("customclaim").get(0));
		
		for (String hdrAtteso: expectedHeaders.keySet()) {
			String vAtteso = expectedHeaders.get(hdrAtteso);
			hdrAtteso = "govway-testsuite-"+hdrAtteso;
			String v = response.getHeaderFirstValue(hdrAtteso);
			if(v==null || StringUtils.isEmpty(v)) {
				throw new Exception("Header atteso '"+hdrAtteso+"' non presente");
			}
			if(TIME.equals(vAtteso)) {
				try {
					Date d = org.openspcoop2.utils.date.DateUtils.getSimpleDateFormat(format).parse(v);
					if(d==null) {
						throw new Exception("Data non letta");
					}
				}catch(Exception e) {
					throw new Exception("Header atteso '"+hdrAtteso+"' possiede un valore '"+v+"' differente da una data: "+e.getMessage());
				}
			}
			else {
				if(!vAtteso.equals(v)) {
					throw new Exception("Header atteso '"+hdrAtteso+"' possiede un valore '"+v+"' differente da quello atteso '"+vAtteso+"'");
				}
			}
		}
	}
	
	private static void checkGovWayJson(String hdrAtteso, HttpResponse response, Map<String, List<String>> values, boolean govway) throws Exception {
	
		if(response==null || response.getHeadersValues()==null || response.getHeadersValues().isEmpty()) {
			throw new Exception("Header non ritornati");
		}
		
		String v = response.getHeaderFirstValue(hdrAtteso);
		if(v==null || StringUtils.isEmpty(v)) {
			throw new Exception("Header atteso '"+hdrAtteso+"' non presente");
		}
		
		byte[] vDecoded = Base64Utilities.decode(v);
		String json = new String(vDecoded);
		//System.out.println("RICEVUTO: "+json);
		
		try {
			checkJsonValues(json, values, govway);
		}catch(Exception e) {
			System.out.println("RICEVUTO: "+json);
			throw e;
		}
		
	}
	
	private static void checkGovWayJwt(String hdrAtteso, HttpResponse response, Map<String, List<String>> values, boolean govway) throws Exception {
		
		if(response==null || response.getHeadersValues()==null || response.getHeadersValues().isEmpty()) {
			throw new Exception("Header non ritornati");
		}
		
		String v = response.getHeaderFirstValue(hdrAtteso);
		if(v==null || StringUtils.isEmpty(v)) {
			throw new Exception("Header atteso '"+hdrAtteso+"' non presente");
		}
		
		Properties props = new Properties();
		props.put("rs.security.keystore.type","JKS");
		String password = "openspcoop";
		props.put("rs.security.keystore.file", "/etc/govway/keys/erogatore.jks");
		props.put("rs.security.keystore.alias","erogatore");
		props.put("rs.security.keystore.password",password);
		props.put("rs.security.key.password",password);
		
		JWTOptions options = new JWTOptions(JOSESerialization.COMPACT);
			
		props.put("rs.security.signature.algorithm","RS256");
		props.put("rs.security.signature.include.cert","false");
		props.put("rs.security.signature.include.key.id","true");
		props.put("rs.security.signature.include.public.key","false");
		props.put("rs.security.signature.include.cert.sha1","false");
		props.put("rs.security.signature.include.cert.sha256","false");
			
		JsonVerifySignature jsonSignature = new JsonVerifySignature(props, options);
		if(!jsonSignature.verify(v)) {
			throw new Exception("Token non valido ?");
		}
		//System.out.println(token);
		
		
		byte[] vDecoded = jsonSignature.getDecodedPayloadAsByte();
		String json = new String(vDecoded);
		//System.out.println("RICEVUTO: "+json);
		
		try {
			checkJsonValues(json, values, govway);
		}catch(Exception e) {
			System.out.println("RICEVUTO: "+json);
			throw e;
		}
		
	}
	
	private static void checkJsonValues(String json, Map<String, List<String>> values, boolean govway) throws Exception {
		
		//String format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		DateFormat dateFormat = JSONUtils.getInstance().getDateFormat();
			
		Map<String, String> expectedField = new HashMap<String, String>();
		if(govway) {
			expectedField.put("$.id", ID);
			expectedField.put("$.processTime", TIME);
		}
		expectedField.put(govway ? "$.issuer" : "$.iss", values.get("iss").get(0));
		expectedField.put(govway ? "$.subject" : "$.sub", values.get("sub").get(0));
		StringBuilder sbAud = new StringBuilder();
		for (String aud: values.get("aud")) {
			if(sbAud.length()>0) {
				sbAud.append(",");
			}
			sbAud.append(aud);
		}
		expectedField.put(govway ? "$.audience" : "$.aud", sbAud.toString());
		expectedField.put(govway ? "$.clientId" : "$.client_id", values.get("clientId").get(0));
		if(govway) {
			long vDate = Long.parseLong(values.get("iat").get(0)) * 1000;
			expectedField.put("$.iat", dateFormat.format(new Date(vDate)));
			vDate = Long.parseLong(values.get("exp").get(0)) * 1000;
			expectedField.put("$.expire", dateFormat.format(new Date(vDate)));
			vDate = Long.parseLong(values.get("nbf").get(0)) * 1000;
			expectedField.put("$.nbf", dateFormat.format(new Date(vDate)));
		}
		else {
			expectedField.put("$.iat", values.get("iat").get(0));
			expectedField.put("$.exp", values.get("exp").get(0));
			expectedField.put("$.nbf", values.get("nbf").get(0));
		}
		StringBuilder sbRoles = new StringBuilder();
		for (String role: values.get("role")) {
			if(sbRoles.length()>0) {
				sbRoles.append(",");
			}
			sbRoles.append(role);
		}
		expectedField.put(govway ? "$.roles" : "$.role", sbRoles.toString());
		StringBuilder sbScope = new StringBuilder();
		for (String scope: values.get("scope")) {
			if(sbScope.length()>0) {
				if(govway) {
					sbScope.append(",");
				}
				else {
					sbScope.append(" ");
				}
			}
			sbScope.append(scope);
		}
		expectedField.put(govway ? "$.scopes" : "$.scope", sbScope.toString());
		expectedField.put("$.username", values.get("username").get(0));
		expectedField.put(govway ? "$.userInfo.fullName" : "$.name", values.get("fullName").get(0));
		expectedField.put(govway ? "$.userInfo.firstName" : "$.given_name", values.get("firstName").get(0));
		expectedField.put(govway ? "$.userInfo.middleName" : "$.middle_name", values.get("middleName").get(0));
		expectedField.put(govway ? "$.userInfo.familyName" : "$.family_name", values.get("familyName").get(0));
		expectedField.put(govway ? "$.userInfo.eMail" : "$.email", values.get("email").get(0));
		expectedField.put("$.jti", values.get("jti").get(0));
		expectedField.put("$.purposeId", values.get("purposeId").get(0));
		if(govway) {
			expectedField.put("$.claims[0].name", "customClaim");
			expectedField.put("$.claims[0].value", values.get("customclaim").get(0));
		}
		else {
			expectedField.put("$.customClaim", values.get("customclaim").get(0));
		}
		
		for (String pattern : expectedField.keySet()) {
			String expectedValue = expectedField.get(pattern);
			String v = null;
			try {
				v = JsonPathExpressionEngine.extractAndConvertResultAsString(json, pattern, logCore);
				if(v==null || StringUtils.isEmpty(v)) {
					throw new Exception("Valore non presente");
				}
			}catch(Exception e) {
				throw new Exception("Pattern '"+pattern+"' non soddisfatto: "+e.getMessage(),e); 
			}
			if(ID.equals(expectedValue)) {
				// basta che non sia null
			}
			else if(TIME.equals(expectedValue)) {
				try {
					Date d = dateFormat.parse(v);
					if(d==null) {
						throw new Exception("Data non letta");
					}
				}catch(Exception e) {
					throw new Exception("Pattern '"+pattern+"' ha estratto un valore '"+v+"' differente da una data: "+e.getMessage());
				}
			}
			else {
				if(!expectedValue.equals(v)) {
					throw new Exception("Pattern '"+pattern+"' ha estratto un valore '"+v+"' differente da quello atteso '"+expectedValue+"'");
				}
			}
		}
	}
	
	private static String buildJWT(Map<String, List<String>> values) throws Exception {
		
		String jsonInput = Utilities.buildFullJson(values); 
		
		//System.out.println("TOKEN ["+jsonInput+"]");
		
		Properties props = new Properties();
		props.put("rs.security.keystore.type","JKS");
		String password = "openspcoop";
		props.put("rs.security.keystore.file", "/etc/govway/keys/erogatore.jks");
		props.put("rs.security.keystore.alias","erogatore");
		props.put("rs.security.keystore.password",password);
		props.put("rs.security.key.password",password);
		
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