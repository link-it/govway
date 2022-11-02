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

package org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Properties;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.test.KeystoreTest;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWEOptions;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonEncrypt;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

import net.minidev.json.JSONObject;

/**
* Utils
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Utils {

	public static final String AUTHORIZATION_DENY = "AuthorizationDeny";
	public static final String AUTHORIZATION_DENY_MESSAGE = "Authorization deny";
	
	public static final String INVALID_TOKEN = "TokenAuthenticationFailed";
	public static final String INVALID_TOKEN_MESSAGE = "Invalid token";
	
	public static final String BAD_REQUEST = "BadRequest";
	public static final String BAD_REQUEST_MESSAGE = "Bad request";
	
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
	
	public static void verifyOk(HttpResponse response, int code, String expectedContentType) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(expectedContentType, response.getContentType());
		
	}
	
	public static HttpResponse testJson(Logger logCore, String api, String operazione, String msgErroreFruizione, String msgErroreErogazione, 
			String mittente, String msgRequestDetail, String msgResponseDetail) throws Exception {
		
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();

		return _test(contentType, content,
				logCore, api, operazione, msgErroreFruizione, msgErroreErogazione, 
				mittente, msgRequestDetail, msgResponseDetail);
	}
	public static HttpResponse testXml(Logger logCore, String api, String operazione, String msgErroreFruizione, String msgErroreErogazione, 
			String mittente, String msgRequestDetail, String msgResponseDetail) throws Exception {
		
		String contentType = HttpConstants.CONTENT_TYPE_XML;
		byte[]content = Bodies.getXML(Bodies.SMALL_SIZE).getBytes();

		return _test(contentType, content,
				logCore, api, operazione, msgErroreFruizione, msgErroreErogazione, 
				mittente, msgRequestDetail, msgResponseDetail);
	}
	public static HttpResponse testSoap11(Logger logCore, String api, String operazione, String msgErroreFruizione, String msgErroreErogazione, 
			String mittente, String msgRequestDetail, String msgResponseDetail) throws Exception {
		
		String contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
		byte[]content = Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes();

		return _test(contentType, content,
				logCore, api, operazione, msgErroreFruizione, msgErroreErogazione, 
				mittente, msgRequestDetail, msgResponseDetail);
	}
	public static HttpResponse testSoap12(Logger logCore, String api, String operazione, String msgErroreFruizione, String msgErroreErogazione, 
			String mittente, String msgRequestDetail, String msgResponseDetail) throws Exception {
		
		String contentType = HttpConstants.CONTENT_TYPE_SOAP_1_2;
		byte[]content = Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes();

		return _test(contentType, content,
				logCore, api, operazione, msgErroreFruizione, msgErroreErogazione, 
				mittente, msgRequestDetail, msgResponseDetail);
	}
	
	private static HttpResponse _test(String contentType, byte[] content,
			Logger logCore, String api, String operazione, String msgErroreFruizione, String msgErroreErogazione, 
			String mittente, String msgRequestDetail, String msgResponseDetail) throws Exception {
		
		Date now = DateManager.getDate();
		
		boolean signatureNone = false;
		if(AuthorizationServerValidazioneTest.api_validazione.equals(api) && AuthorizationServerValidazioneTest.PKCS11_ValidazioneJWS_none.equals(operazione)) {
			signatureNone = true;
			operazione = AuthorizationServerValidazioneTest.PKCS11_ValidazioneJWS;
		}
		
		boolean forwardJWT = false;
		if(AuthorizationServerValidazioneTest.api_validazione.equals(api) && 
				(AuthorizationServerValidazioneTest.PKCS11_ForwardGovWayJWS.equals(operazione) ||
				AuthorizationServerValidazioneTest.PKCS11_ForwardJWS.equals(operazione) ||
				AuthorizationServerValidazioneTest.PKCS11_ForwardJWE.equals(operazione))
				) {
			forwardJWT = true;
		}
		
		String url = System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+operazione;
		if( (AuthorizationServerValidazioneTest.api_validazione.equals(api) && !forwardJWT) 
				||
			(AuthorizationServerNegoziazioneTest.api_negoziazione.equals(api))
				||
			(AttributeAuthorityTest.api.equals(api))
				) {
			url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione;
		}
		
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		request.setContent(content);
		request.setUrl(url);
		
		if(HttpConstants.CONTENT_TYPE_SOAP_1_1.equals(contentType)) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, operazione);
		}
		String testId = null;
		boolean checkErogatore = true;
		if(AuthorizationServerValidazioneTest.api_validazione.equals(api)) {
			String token = null;
			if(AuthorizationServerValidazioneTest.PKCS11_ValidazioneJWS.equals(operazione)) {
				token = buildJwsTokenServerHSM(logCore, signatureNone);
				checkErogatore = false;
			}
			else if(AuthorizationServerValidazioneTest.PKCS11_ValidazioneJWE.equals(operazione)) {
				token = buildJweTokenClient2HSM(logCore);
				checkErogatore = false;
			}
			else if(AuthorizationServerValidazioneTest.PKCS11_ForwardJWE.equals(operazione)) {
				token = buildJweTokenSoggetto1(logCore);
			}
			else {
				token = buildJwsTokenSoggetto1(logCore, signatureNone);
			}
			request.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+token);
			if(!forwardJWT) {
				testId = operazione;
			}
		}
		else if(AuthorizationServerNegoziazioneTest.api_negoziazione.equals(api)) {
			testId = operazione;
		}
		else if(AttributeAuthorityTest.api.equals(api)) {
			testId = operazione;
		}
				
		HttpResponse response = null;
		try {
			//System.out.println("INVOKE ["+request.getUrl()+"]");
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		long esitoExpectedFruizione = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		long esitoExpectedErogazione = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if(msgErroreFruizione!=null || msgErroreErogazione!=null) {
			int code = -1;
			String error = null;
			String msg = null;
			boolean checkErrorTypeGovWay = true;
			if(operazione.equals(TlsTest.Trust_NoKeyAlias)) {
				esitoExpectedFruizione = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_APPLICATIVO);
				esitoExpectedErogazione = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTORIZZAZIONE);
				code = 403;
				error = Utils.AUTHORIZATION_DENY;
				msg = Utils.AUTHORIZATION_DENY_MESSAGE;
				checkErrorTypeGovWay = false;
			}
			else if(operazione.equals(AuthorizationServerValidazioneTest.PKCS11_ValidazioneJWS) && signatureNone) {
				esitoExpectedFruizione = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_TOKEN);
				code = 401;
				error = Utils.INVALID_TOKEN;
				msg = Utils.INVALID_TOKEN_MESSAGE;
				checkErrorTypeGovWay = true;
			}
			else if(api.equals(JoseSecurityTest.apiNONE)) {
				esitoExpectedFruizione = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_APPLICATIVO);
				esitoExpectedErogazione = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA);
				code = 400;
				error = Utils.BAD_REQUEST;
				msg = Utils.BAD_REQUEST_MESSAGE;
				checkErrorTypeGovWay = false;
			}
			Utils.verifyKo(response, error, code, msg, checkErrorTypeGovWay);
		}
		else {
			Utils.verifyOk(response, 200, contentType);
		}
				
		String fruizioneRequestContent = msgRequestDetail;
		if((AttributeAuthorityTest.api.equals(api))) {
			fruizioneRequestContent = null;
		}
		DBVerifier.verify(idTransazione, esitoExpectedFruizione, msgErroreFruizione,
				null, fruizioneRequestContent, msgResponseDetail);
		
		if(checkErogatore) {
			if(msgErroreErogazione==null || mittente!=null) {
				if(testId!=null) {
					idTransazione = DBVerifier.getIdTransazione(now, testId);
				}
				else {
					idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID-Erogazione");
				}
				assertNotNull(idTransazione);
				
				DBVerifier.verify(idTransazione, esitoExpectedErogazione, msgErroreErogazione,
						mittente, msgRequestDetail, msgResponseDetail);
			}
		}
		
		return response;
		
	}
	
	private static String buildJwsTokenSoggetto1(Logger log, boolean signatureNone) throws Exception {
		return _buildJwtToken(true, false, false, log, true, signatureNone);
	}
	private static String buildJwsTokenServerHSM(Logger log, boolean signatureNone) throws Exception {
		return _buildJwtToken(false, true, false, log, true, signatureNone);
	}
//	private static String buildJwsTokenClient2HSM(Logger log, boolean signatureNone) throws Exception {
//		return _buildJwtToken(false, false, true, log, true, signatureNone);
//	}
	private static String buildJweTokenSoggetto1(Logger log) throws Exception {
		return _buildJwtToken(true, false, false, log, false, false);
	}
//	private static String buildJweTokenServerHSM(Logger log) throws Exception {
//		return _buildJwtToken(false, true, false, log, false, false);
//	}
	private static String buildJweTokenClient2HSM(Logger log) throws Exception {
		return _buildJwtToken(false, false, true, log, false, false);
	}
	private static String _buildJwtToken(boolean soggetto1, boolean serverHSM, boolean client2HSM, Logger log, boolean signature, boolean signatureNone) throws Exception {
		
		String type = "jks";
		String alias = "soggetto1_multipleou"; // uso questo certificato poiche' generato con RSA256. Gli altri sono RSA1
		String password = "123456";
		String passwordKey = "123456";
		if(serverHSM) {
			alias = "server_hsm";
			password = KeystoreTest.PASSWORD;
			passwordKey = "";
			type = "pkcs11";
		}
		else if(client2HSM) {
			alias = "client2_hsm";
			password = KeystoreTest.PASSWORD;
			passwordKey = "";
			type = "pkcs11";
		}
		
		String jsonInput = "{\n"+
				"\"iss\": \"http://iss/"+alias+"\",\n"+
				"\"sub\": \"testSub-"+alias+"\",\n"+
				"\"client_id\": \"testClientId-"+alias+"\",\n"+
				"\"azp\": \"testClientId-"+alias+"\",\n"+
				"\"username\": \"testUser-"+alias+"\",\n"+
				"\"preferred_username\": \"testUser-"+alias+"\",\n"+
				"\"aud\": \"testAud-"+alias+"\",\n"+
				"\"iat\": "+(DateManager.getTimeMillis()/1000)+",\n"+
				"\"exp\": "+((DateManager.getTimeMillis()/1000)+300)+",\n"+
				"\"jti\": \""+java.util.UUID.randomUUID().toString()+"\"\n"+
				"}";
		
		KeyStore keystore = null;
		File f = null;
		try {
			if(!soggetto1) {
				log.info("[alias:"+alias+"] env SOFTHSM2_CONF: "+System.getenv("SOFTHSM2_CONF"));
				
				byte [] b = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"govway_test_hsm.properties"));
				
				f = File.createTempFile("test", ".properties");
				try(FileOutputStream fout = new FileOutputStream(f)){
					fout.write(b);
					fout.flush();
				}
				
				HSMManager.init(f, true, log, true);
				
				HSMManager hsmManager = HSMManager.getInstance();
				boolean uniqueProviderInstance = true;
				hsmManager.providerInit(log, uniqueProviderInstance);
				
				log.info("[alias:"+alias+"] Keystore registered: "+hsmManager.getKeystoreTypes());
				
				if(serverHSM) {
					keystore = hsmManager.getKeystore(KeystoreTest.PKCS11_SERVER);
				}
				else if(client2HSM) {
					keystore = hsmManager.getKeystore(KeystoreTest.PKCS11_CLIENT2);
				}
			}
			
			Properties props = new Properties();
			if(soggetto1) {
				if(signature) {
					props.put("rs.security.keystore.file", "/etc/govway/keys/soggetto1_multipleOU.jks");
				}
				else {
					props.put("rs.security.keystore.file", "/etc/govway/keys/erogatore.jks");
					password = "openspcoop";
				}
			}
			else {
				props.put("rs.security.keystore", keystore.getKeystore());
			}
			
			props.put("rs.security.keystore.type",type);
			props.put("rs.security.keystore.alias",alias);
			props.put("rs.security.keystore.password",password);
			props.put("rs.security.key.password",passwordKey);
			
			//System.out.println(jsonInput);
			if(signature) {
				JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
				
				if(signatureNone) {
					props.put("rs.security.signature.algorithm","NONE");
					props.put("rs.security.signature.include.cert","false");
					props.put("rs.security.signature.include.key.id","false");
				}
				else {
					props.put("rs.security.signature.algorithm","RS256");
					props.put("rs.security.signature.include.cert","true");
					props.put("rs.security.signature.include.key.id","true");
				}
				props.put("rs.security.signature.include.public.key","false");
				props.put("rs.security.signature.include.cert.sha1","false");
				props.put("rs.security.signature.include.cert.sha256","false");
				
				JsonSignature jsonSignature = new JsonSignature(props, options);
				String token = jsonSignature.sign(jsonInput);
				//System.out.println(token);
				
				return token;		
				
			}
			else {
				JWEOptions options = new JWEOptions(JOSESerialization.COMPACT);
				
				props.put("rs.security.encryption.key.algorithm", "RSA1_5");
				props.put("rs.security.encryption.content.algorithm","A256GCM");
				props.put("rs.security.encryption.include.cert","true");
				props.put("rs.security.encryption.include.public.key","false");
				props.put("rs.security.encryption.include.key.id","true");
				props.put("rs.security.encryption.include.cert.sha1","false");
				props.put("rs.security.encryption.include.cert.sha256","false");
				
				JsonEncrypt jsonEncrypt = new JsonEncrypt(props, options);
				String token = jsonEncrypt.encrypt(jsonInput);
				//System.out.println(token);
				
				return token;	
			}
		}finally {
			if(f!=null) {
				f.delete();
			}
		}
	}

}
