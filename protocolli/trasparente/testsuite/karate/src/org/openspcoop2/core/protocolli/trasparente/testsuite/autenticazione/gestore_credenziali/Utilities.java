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
package org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.springframework.web.util.UriUtils;

import net.minidev.json.JSONObject;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Utilities extends ConfigLoader {

	public final static String CREDENZIALI_NON_CORRETTE = "credenzialiNonCorrette";
	public final static String CREDENZIALI_NON_FORNITE = "credenzialiNonFornite";
	public final static String AUTORIZZAZIONE_NEGATA = "autorizzazioneNegata";
	public final static String CREDENZIALI_PROXY_NON_FORNITE = "credenzialiProxyNonFornite";
	public final static String CREDENZIALI_PROXY_FORNITE_NON_CONFORMI = "credenzialiProxyForniteNonConformi";
	public final static String CREDENZIALI_PROXY_NON_CORRETTE = "credenzialiProxyNonCorrette";
	
	public static final String AUTHENTICATION_FAILED = "AuthenticationFailed";
	public static final String AUTHENTICATION_FAILED_MESSAGE = "Authentication Failed";
	
	public static final String AUTHENTICATION_REQUIRED = "AuthenticationRequired";
	public static final String AUTHENTICATION_REQUIRED_MESSAGE = "Authentication required";
	
	public static final String AUTHORIZATION_DENY = "AuthorizationDeny";
	public static final String AUTHORIZATION_DENY_MESSAGE = "Authorization deny";
	
	public static final String PROXY_AUTHENTICATION_REQUIRED = "ProxyAuthenticationRequired";
	public static final String PROXY_AUTHENTICATION_REQUIRED_MESSAGE = "Proxy authentication required";
	
	public static final String FORWARD_PROXY_AUTHENTICATION_REQUIRED = "ForwardProxyAuthenticationRequired";
	public static final String FORWARD_PROXY_AUTHENTICATION_REQUIRED_MESSAGE = "Peer credentials not forwarded by proxy";
	
	public static final String PROXY_AUTHENTICATION_FAILED = "ProxyAuthenticationFailed";
	public static final String PROXY_AUTHENTICATION_FAILED_MESSAGE = "Proxy Authentication failed";

	
	public static HttpResponse _test(TipoServizio tipoServizio,
			String soggetto,
			String operazione,
			Map<String, String> headers,
			String identitaSoggetto,
			String identitaServizioApplicativo,
			String credenziali,
			String wwwAuthenticateMessage,
			String tipoErrore,
			String ... msgErrori) throws Exception {
		return _test(null,
				tipoServizio,
				soggetto,
				operazione,
				headers,
				identitaSoggetto,
				identitaServizioApplicativo,
				credenziali,
				wwwAuthenticateMessage,
				tipoErrore,
				msgErrori);
	}
	public static HttpResponse _test(HttpRequest requestParam,
			TipoServizio tipoServizio,
			String soggetto,
			String operazione,
			Map<String, String> headers,
			String identitaSoggetto,
			String identitaServizioApplicativo,
			String credenziali,
			String wwwAuthenticateMessage,
			String tipoErrore,
			String ... msgErrori) throws Exception {
		String api = "TestGestoreCredenziali";
		return _test(api,
				requestParam,
				tipoServizio,
				soggetto,
				operazione,
				headers,
				identitaSoggetto,
				identitaServizioApplicativo,
				credenziali,
				wwwAuthenticateMessage,
				tipoErrore,
				msgErrori);
	}
	public static HttpResponse _test(String api,
			HttpRequest requestParam,
			TipoServizio tipoServizio,
			String soggetto,
			String operazione,
			Map<String, String> headers,
			String identitaSoggetto,
			String identitaServizioApplicativo,
			String credenziali,
			String wwwAuthenticateMessage,
			String tipoErrore,
			String ... msgErrori) throws Exception {

		//Date now = DateManager.getDate();
		
		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/"+soggetto+"/"+api+"/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/"+soggetto+"/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
		
		HttpRequest request = requestParam!=null ? requestParam : new HttpRequest();
		
		request.setReadTimeout(20000);

		String contentType= HttpConstants.CONTENT_TYPE_JSON;
		byte[]content=Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
				
		if(request.getKeyStorePath()!=null) {
			url = url.replace("http:", "https:");
			url = url.replace(":8080", ":8444");
		}
		else if(request.getUsername()!=null && (request.getUsername().equals("esempioFruitoreTrasparentePrincipal") || request.getUsername().equals("esempioFruitoreTrasparentePrincipal2"))) {
			url = url.replace("govway", "govwaySec");
		}
		//System.out.println("URL ["+url+"]");
		request.setUrl(url);
		
		for (String key : headers.keySet()) {
			request.addHeader(key, headers.get(key));
		}
		

		
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		
		if(tipoErrore!=null) {
			
			int code = -1;
			String error = null;
			String msg = null;
			
			if(CREDENZIALI_NON_CORRETTE.equals(tipoErrore)) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTENTICAZIONE);
				code = 401;
				error = AUTHENTICATION_FAILED;
				msg = AUTHENTICATION_FAILED_MESSAGE;
			}
			else if(CREDENZIALI_NON_FORNITE.equals(tipoErrore)) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTENTICAZIONE);
				code = 401;
				error = AUTHENTICATION_REQUIRED;
				msg = AUTHENTICATION_REQUIRED_MESSAGE;
			}
			else if(AUTORIZZAZIONE_NEGATA.equals(tipoErrore)) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTORIZZAZIONE);
				code = 403;
				error = AUTHORIZATION_DENY;
				msg = AUTHORIZATION_DENY_MESSAGE;
			}
			else if(CREDENZIALI_PROXY_NON_FORNITE.equals(tipoErrore)) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTENTICAZIONE);
				code = 401;
				error = PROXY_AUTHENTICATION_REQUIRED;
				msg = PROXY_AUTHENTICATION_REQUIRED_MESSAGE;
			}
			else if(CREDENZIALI_PROXY_NON_CORRETTE.equals(tipoErrore)) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTENTICAZIONE);
				code = 401;
				error = PROXY_AUTHENTICATION_FAILED;
				msg = PROXY_AUTHENTICATION_FAILED_MESSAGE;
			}
			else if(CREDENZIALI_PROXY_FORNITE_NON_CONFORMI.equals(tipoErrore)) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTENTICAZIONE);
				code = 401;
				error = FORWARD_PROXY_AUTHENTICATION_REQUIRED;
				msg = FORWARD_PROXY_AUTHENTICATION_REQUIRED_MESSAGE;
			}
			else {
				throw new Exception("Tipo ["+tipoErrore+"] non gestito");
			}
			verifyKo(response, error, code, msg);
			
			if(code==401) {
				String wwwAuthenticate = null;
				int sizeWwwAuthenticate = 0;
				//System.out.println("=================");
				for (String hdr : response.getHeadersValues().keySet()) {
					//System.out.println("HDR ["+hdr+"]=["+response.getHeadersValues().get(hdr)+"]");
					if(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE.equalsIgnoreCase(hdr)) {
						if(response.getHeaderValues(hdr)!=null) {
							sizeWwwAuthenticate = response.getHeaderValues(hdr).size();
							wwwAuthenticate = response.getHeaderFirstValue(hdr);
						}
					}
				}
				assertEquals(1, sizeWwwAuthenticate);
				assertNotNull(wwwAuthenticate);
				if(!wwwAuthenticate.equals(wwwAuthenticateMessage)) {
					System.out.println(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE+": ["+wwwAuthenticate+"]");
				}
				assertEquals(wwwAuthenticateMessage, wwwAuthenticate);
			}
		}
		else {
			verifyOk(response, 200); // il codice http e' gia' stato impostato
			
		}
				
		DBVerifier.verify(idTransazione, esitoExpected, 
				identitaSoggetto, identitaServizioApplicativo,
				credenziali,
				msgErrori);
		
		return response;	
	}
	
	public static void verifyOk(HttpResponse response, int code) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_JSON, response.getContentType());
		
	}
	
	public static void verifyKo(HttpResponse response, String error, int code, String errorMsg) {
		
		assertEquals(code, response.getResultHTTPOperation());
		
		if(error!=null) {
			assertEquals(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807, response.getContentType());
			
			try {
				JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
				JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(response.getContent()));
				
				assertEquals("https://govway.org/handling-errors/"+code+"/"+error+".html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
				assertEquals(error, jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
				assertEquals(code, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
				assertTrue(jsonPath.getStringMatchPattern(jsonResp, "$.govway_id") !=null);	
				assertTrue(jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0) !=null);	
				assertEquals(true, jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0).equals(errorMsg));
				
				assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
	
				if(code==504) {
					assertNotNull(response.getHeaderFirstValue(HttpConstants.RETRY_AFTER));
				}
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static String getCertificate(boolean der) throws Exception {
		return getCertificate(der, "applicativoCertExample.pem");
	}
	public static String getCertificate2(boolean der) throws Exception {
		return getCertificate(der, "applicativoCertExample2.pem");
	}
	public static String getCertificate(boolean der, String fileName) throws Exception {
		
		// Certificato generato con il comando: 'openssl req -x509 -newkey rsa:4096 -keyout applicativoCertExample.key -out applicativoCertExample.pem -sha256 -days 36500 -subj '/CN=example.gestoreCredenziali/O=test/C=IT/'
		// password: 123456
		
		String path = "src/org/openspcoop2/core/protocolli/trasparente/testsuite/autenticazione/gestore_credenziali/"+fileName;
		byte [] certBytes = FileSystemUtilities.readBytesFromFile(path);
		
		if(der) {
			
			return Base64Utilities.encodeAsString(ArchiveLoader.load(certBytes).getCertificate().getCertificate().getEncoded());
			
		}
		else {
			
			String  s = new String(certBytes);
			s = s.replace("-----BEGIN CERTIFICATE-----\n", "");
			s = s.replace("\n-----END CERTIFICATE-----", "");
			s = s.replaceAll("\n","\t");
			return UriUtils.encode(s, Charset.UTF_8.getValue());
			
		}
	}
}
