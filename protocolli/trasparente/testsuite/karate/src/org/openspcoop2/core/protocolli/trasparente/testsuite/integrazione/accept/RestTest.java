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
package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.accept;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ProblemUtilities;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;
import org.w3c.dom.Node;

import net.minidev.json.JSONObject;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {


	@Test
	public void erogazione_accept_undefined() throws Exception {
		_test(TipoServizio.EROGAZIONE, null, true);
	}
	@Test
	public void fruizione_accept_undefined() throws Exception {
		_test(TipoServizio.FRUIZIONE, null, true);
	}
	
	@Test
	public void erogazione_accept_json() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, true);
	}
	@Test
	public void fruizione_accept_json() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, true);
	}
	
	@Test
	public void erogazione_accept_xml() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML, false);
	}
	@Test
	public void fruizione_accept_xml() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML, false);
	}
	
	@Test
	public void erogazione_accept_xml_soap11() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, false);
	}
	@Test
	public void fruizione_accept_xml_soap11() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, false);
	}
	
	@Test
	public void erogazione_accept_piu_json() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807, true);
	}
	@Test
	public void fruizione_accept_piu_json() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807, true);
	}
	
	@Test
	public void erogazione_accept_piu_xml() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807, false);
	}
	@Test
	public void fruizione_accept_piu_xml() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807, false);
	}
	
	@Test
	public void erogazione_accept_piu_xml_soap12() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, false);
	}
	@Test
	public void fruizione_accept_piu_xml_soap12() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, false);
	}
	
	@Test
	public void erogazione_accept_meno_json() throws Exception {
		_test(TipoServizio.EROGAZIONE, "*/test-json", true);
	}
	@Test
	public void fruizione_accept_meno_json() throws Exception {
		_test(TipoServizio.FRUIZIONE, "*/test-json", true);
	}
	
	@Test
	public void erogazione_accept_meno_xml() throws Exception {
		_test(TipoServizio.EROGAZIONE, "*/test-xml", true);
	}
	@Test
	public void fruizione_accept_meno_xml() throws Exception {
		_test(TipoServizio.FRUIZIONE, "*/test-xml", true);
	}

	
	@Test
	public void erogazione_accept_json_xml() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON+", "+HttpConstants.CONTENT_TYPE_XML, true);
	}
	@Test
	public void fruizione_accept_json_xml() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON+","+HttpConstants.CONTENT_TYPE_XML, true);
	}
	
	
	@Test
	public void erogazione_accept_xml_json() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML+", "+HttpConstants.CONTENT_TYPE_JSON, false);
	}
	@Test
	public void fruizione_accept_xml_json() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML+","+HttpConstants.CONTENT_TYPE_JSON, false);
	}
	
	
	
	@Test
	public void erogazione_accept_json_other() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_HTML+", "+ HttpConstants.CONTENT_TYPE_JSON+", */*", true);
	}
	@Test
	public void fruizione_accept_json_other() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_HTML+","+ HttpConstants.CONTENT_TYPE_JSON+",*", true);
	}
	
	
	@Test
	public void erogazione_accept_xml_other() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_HTML+", "+ HttpConstants.CONTENT_TYPE_XML+", */*", false);
	}
	@Test
	public void fruizione_accept_xml_other() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_HTML+","+ HttpConstants.CONTENT_TYPE_XML+",*", false);
	}
	
	
	
	@Test
	public void erogazione_accept_json_xml_q() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML+";q=0.8, "+HttpConstants.CONTENT_TYPE_JSON, true);
	}
	@Test
	public void fruizione_accept_json_xml_q() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML+" ; q = .8,"+HttpConstants.CONTENT_TYPE_JSON, true);
	}
	
	@Test
	public void erogazione_accept_json_xml_q2() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML+";q=0.8, "+HttpConstants.CONTENT_TYPE_JSON+";q=0.9", true);
	}
	@Test
	public void fruizione_accept_json_xml_q2() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML+" ; q = .8,"+HttpConstants.CONTENT_TYPE_JSON+" ; q = .9", true);
	}
	
	@Test
	public void erogazione_accept_xml_json_q() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON+";q=0.8, "+HttpConstants.CONTENT_TYPE_XML, false);
	}
	@Test
	public void fruizione_accept_xml_json_q() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON+" ; q = .8,"+HttpConstants.CONTENT_TYPE_XML, false);
	}
	
	@Test
	public void erogazione_accept_xml_json_q2() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML+";q=0.8, "+HttpConstants.CONTENT_TYPE_JSON+";q=0.2", false);
	}
	@Test
	public void fruizione_accept_xml_json_q2() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML+" ; q = .8,"+HttpConstants.CONTENT_TYPE_JSON+" ; q = .2", false);
	}
	
	@Test
	public void erogazione_accept_xml_json_q3() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON+";q=.1, "+HttpConstants.CONTENT_TYPE_XML+";q=0.2", false);
	}
	@Test
	public void fruizione_accept_xml_json_q3() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON+" ; q = 0.1,"+HttpConstants.CONTENT_TYPE_XML+" ; q = .2", false);
	}

	
	
	
	private static HttpResponse _test(TipoServizio tipoServizio,
			String headerValue, boolean expectedJson) throws Exception {

		String api = "TestAutenticazioneGatewayREST";
		String operazione = "NonEsistente";
		
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[] content=Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		
		//Date now = DateManager.getDate();
		
		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		
		request.setReadTimeout(20000);

		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		//System.out.println("URL ["+url+"]");
		request.setUrl(url);
		if(headerValue!=null) {
			request.addHeader(HttpConstants.ACCEPT, headerValue);
		}
				
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		verifyKo(response, expectedJson, ConfigLoader.logCore);
			
		return response;	
	}
	
	public static void verifyKo(HttpResponse response, boolean expectedJson, Logger log) {
		
		int code = 404;	
		String error = "UndefinedOperation";
		String errorMsg = "Operation undefined in the API specification";
		
		assertEquals(code, response.getResultHTTPOperation());
		
		if(expectedJson) {
		
			assertEquals(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807, response.getContentType());
			
			try {
				JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
				JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(response.getContent()));
				
				assertEquals("https://govway.org/handling-errors/"+code+"/"+error+".html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
				assertEquals(error, jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
				assertEquals(code, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
				assertNotEquals(null, jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0));	
				assertEquals(true, jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0).equals(errorMsg));
				
				assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
	
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
		}
		
		else {
			
			assertEquals(HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807, response.getContentType());
			
			try {
				OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
				OpenSPCoop2MessageParseResult parse = factory.createMessage(MessageType.XML, MessageRole.NONE, response.getContentType(), response.getContent());
				OpenSPCoop2Message msg = parse.getMessage_throwParseThrowable();
				OpenSPCoop2RestXmlMessage xmlMsg = msg.castAsRestXml();
				
				Node problemNode = xmlMsg.getContent();
				
				ProblemUtilities.verificaProblem(problemNode, 
						code, error, errorMsg, true, 
						log);
				
				assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
				
			} catch (Throwable e) {
				throw new RuntimeException(e.getMessage(),e);
			}
			
		}
		
	}
	
}
