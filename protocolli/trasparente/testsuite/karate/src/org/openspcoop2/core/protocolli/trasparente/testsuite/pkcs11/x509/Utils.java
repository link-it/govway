package org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
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
		
		String url = System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		request.setContent(content);
		request.setUrl(url);
		
		if(HttpConstants.CONTENT_TYPE_SOAP_1_1.equals(contentType)) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, operazione);
		}
				
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		long esitoExpectedFruizione = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		long esitoExpectedErogazione = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if(msgErroreFruizione!=null || msgErroreErogazione!=null) {
			int code = -1;
			String error = null;
			String msg = null;
			boolean checkErrorTypeGovWay = true;
			if(operazione.equals("Trust-NoKeyAlias")) {
				esitoExpectedFruizione = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_APPLICATIVO);
				esitoExpectedErogazione = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTORIZZAZIONE);
				code = 403;
				error = Utils.AUTHORIZATION_DENY;
				msg = Utils.AUTHORIZATION_DENY_MESSAGE;
				checkErrorTypeGovWay = false;
			}
			Utils.verifyKo(response, error, code, msg, checkErrorTypeGovWay);
		}
		else {
			Utils.verifyOk(response, 200, contentType);
		}
				
		DBVerifier.verify(idTransazione, esitoExpectedFruizione, msgErroreFruizione,
				null, msgRequestDetail, msgResponseDetail);
		
		if(msgErroreErogazione==null || mittente!=null) {
			idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID-Erogazione");
			assertNotNull(idTransazione);
			
			DBVerifier.verify(idTransazione, esitoExpectedErogazione, msgErroreErogazione,
					mittente, msgRequestDetail, msgResponseDetail);
		}
		
		return response;
		
	}
}
