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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.errori;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import net.minidev.json.JSONObject;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {
	
	// invalidContentType (caso 1)
	@Test
	public void erogazione_invalidContentType_caso1() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"invalidContentType1", 
				"Il contenuto applicativo della risposta ricevuta non è processabile: Content-Type ''\"application/json\";charset=UTF-8'' presente nella risposta non valido: In Content-Type string <\"application/json\";charset=UTF-8>, expected MIME type, got application/json");
	}
	@Test
	public void fruizione_invalidContentType_caso1() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"invalidContentType1", 
				"Il contenuto applicativo della risposta ricevuta non è processabile: Content-Type ''\"application/json\";charset=UTF-8'' presente nella risposta non valido: In Content-Type string <\"application/json\";charset=UTF-8>, expected MIME type, got application/json");
	}
	
	// invalidContentType (caso 2)
	@Test
	public void erogazione_invalidContentType_caso2() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"invalidContentType2", 
				"Il contenuto applicativo della risposta ricevuta non è processabile: Content-Type ''\"application/json\"'' presente nella risposta non valido: In Content-Type string <\"application/json\">, expected MIME type, got application/json");
	}
	@Test
	public void fruizione_invalidContentType_caso2() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"invalidContentType2", 
				"Il contenuto applicativo della risposta ricevuta non è processabile: Content-Type ''\"application/json\"'' presente nella risposta non valido: In Content-Type string <\"application/json\">, expected MIME type, got application/json");
	}
	
	
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String msgErrore) throws Exception {
		
		String API = "SoggettoInternoTest/BackendRisposteNonRest/v1";

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/"+API+"/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/"+API+"/"+operazione;
		url=url+"?test="+operazione;
		
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
				
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		request.setUrl(url);
		
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE);
		if(operazione.startsWith("invalidContentType")) {
			esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO);
		}
		
		if(operazione.startsWith("invalidContentType")) {
			verifyKo(response, org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER, INVALID_RESPONSE, 502, INVALID_RESPONSE_MESSAGE);
		}
		else {
			verifyKo(response, org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER, API_UNAVAILABLE, 503, API_UNAVAILABLE_MESSAGE);
		}
		
		try {
			DBVerifier.verify(idTransazione, esitoExpected, msgErrore);
		}catch(Throwable e) {
			if(msgErrore.contains(";charset")) {
				try {
					// in tomcat viene aggiunto uno spazio
					String msgTomcat = msgErrore.replaceAll(";charset", "; charset");
					DBVerifier.verify(idTransazione, esitoExpected, msgTomcat);
				}catch(Throwable e2) {
					// in caso di nuovo errore, rilancio il primo errore
					throw e;
				}
			}
			else {
				throw e;
			}
		}
		
		return response;
		
	}
	
	public static final String API_UNAVAILABLE = "APIUnavailable";
	public static final String API_UNAVAILABLE_MESSAGE = "The API Implementation is temporary unavailable";
	
	public static final String INVALID_RESPONSE = "InvalidResponse";
	public static final String INVALID_RESPONSE_MESSAGE = "Invalid response received from the API Implementation";

	public static void verifyKo(HttpResponse response, String soapPrefixError, String error, int code, String errorMsg) throws Exception {
		
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
				
				assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
	
				if(code==504) {
					assertNotNull(response.getHeaderFirstValue(HttpConstants.RETRY_AFTER));
				}
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
}
