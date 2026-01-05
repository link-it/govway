/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.dimensione_messaggi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
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
* RestUtilities
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestUtilities extends ConfigLoader {


	public static HttpResponse test(
			boolean chunkedMode,
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, String msgErrore,
			String headerTest, boolean expectedError,
			boolean verifyEvents) throws Exception {
		return test(
				chunkedMode,
				tipoServizio, contentType, content,
				operazione, tipoTest, msgErrore,
				null,
				headerTest, expectedError,
				verifyEvents);
	}
	public static HttpResponse test(
			boolean chunkedMode,
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, String msgErrore,
			String applicativeId, 
			String headerTest, boolean expectedError,
			boolean verifyEvents) throws Exception {
		return test(
				chunkedMode,
				tipoServizio, contentType, content,
				operazione, tipoTest, msgErrore,
				applicativeId, 
				headerTest, expectedError,
				verifyEvents,
				"DimensioneMassimaMessaggiREST");
	}
	public static HttpResponse test(
			boolean chunkedMode,
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, String msgErrore,
			String applicativeId, 
			String headerTest, boolean expectedError,
			boolean verifyEvents,
			String idAPI,
			String ... otherValueContentLength) throws Exception {

		LocalDateTime dataSpedizione = LocalDateTime.now();

		String idServizio = "SoggettoInternoTest/"+idAPI+"/v1";
		Optional<String> gruppo = null;
		if(!"sendRegistrazioneDisabilitata".equals(operazione)) {
			gruppo = Optional.of(operazione.replace("/", "."));
		}
		if("filtro2".equals(operazione)) {
			gruppo= Optional.of("filtro");
		}
		
		boolean policyApi = true;
		if("globale".equals(operazione)) {
			policyApi = false;
		}
		
		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/"+idServizio+"/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/"+idServizio+"/"+operazione;
		url=url+"?test="+tipoTest;
		if(headerTest!=null) {
			url=url+"&TipoTestDimensioneMassimaMessaggio="+headerTest;
		}
		if(!chunkedMode) {
			url=url+"&forceContentLength=true"; // nella risposta
		}
		
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
						
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		if(chunkedMode) {
			request.setForceTransferEncodingChunked(true);
		}
		
		if(otherValueContentLength!=null && otherValueContentLength.length>0) {
			request.addHeader(HttpConstants.CONTENT_LENGTH, Arrays.asList(otherValueContentLength));
		}
		
		request.setUrl(url);
		
		if(headerTest!=null) {
			request.addHeader("TipoTestDimensioneMassimaMessaggio", headerTest);
		}
		
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
			
		int httpCodeError = 413;
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA);
		String errorCode = REQUEST_SIZE_EXCEEDED;
		String errorMessage = REQUEST_SIZE_EXCEEDED_MESSAGE;
		String nomePolicy = "DimensioneMassimaMessaggi";
		if(!expectedError) {
			esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		}
		if(headerTest!=null && headerTest.equals("Risposta")) {
			nomePolicy = "Risposta50K";
			if(expectedError) {
				httpCodeError = 502;
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA);
				errorCode = RESPONSE_SIZE_EXCEEDED;
				errorMessage = RESPONSE_SIZE_EXCEEDED_MESSAGE;
			}
		}
		else if(headerTest!=null && headerTest.equals("Richiesta")) {
			nomePolicy = "Richiesta50K";
		}
		
		if((chunkedMode || RestCheckHeaderContenLengthDisabledTest.ID_API_CONFIG.equals(idAPI)) &&
				"Risposta".equals(headerTest) && expectedError && 
				(
					"sendRegistrazioneAbilitata/client".equals(operazione)
						|| 
					"sendRegistrazioneDisabilitata".equals(operazione)
						||
					"sendRegistrazioneDisabilitata/fileTraceClient".equals(operazione)
						||
					"filtro".equals(operazione)
				)
			){
			verifyOk(response, 200); // il codice http e' gia' stato impostato
		}
		else {
			if(expectedError) {
				verifyKo(response, errorCode, httpCodeError, errorMessage);
			}
			else {
				verifyOk(response, 200); // il codice http e' gia' stato impostato
			}
		}
		
		DBVerifier.verify(idTransazione, esitoExpected, msgErrore);
		
		if(expectedError && verifyEvents) {
			DBVerifier.checkEventiConViolazioneRL(idServizio, gruppo, dataSpedizione,
					response, logRateLimiting, nomePolicy, policyApi);
		}
		
		return response;
		
	}
	
	public static final String API_UNAVAILABLE = "APIUnavailable";
	public static final String API_UNAVAILABLE_MESSAGE = "The API Implementation is temporary unavailable";
	
	public static final String REQUEST_SIZE_EXCEEDED = "RequestSizeExceeded";
	public static final String REQUEST_SIZE_EXCEEDED_MESSAGE = "Request size exceeded detected";
	
	public static final String RESPONSE_SIZE_EXCEEDED = "ResponseSizeExceeded";
	public static final String RESPONSE_SIZE_EXCEEDED_MESSAGE = "Response size exceeded detected";

	public static void verifyKo(HttpResponse response, String error, int code, String errorMsg) {
		
		assertEquals(code, response.getResultHTTPOperation());
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
	
	public static void verifyOk(HttpResponse response, int code) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_JSON, response.getContentType());
		
	}
}