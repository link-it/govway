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
package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.soap.SOAPFault;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ProblemUtilities;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.JwtHeaders;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;
import org.w3c.dom.Node;

import net.minidev.json.JSONObject;

/**
 * Utilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Utilities {

	public static final String DEFAULT_HTTP_HEADER = "GovWay-Integration";
	public static final String HTTP_HEADER_CUSTOM = "GovWay-Integration-NomeDifferente";
	
	public static final String CORRELAZIONE_RICHIESTA = "Richiesta_valoreClientString_12467";
	public static final String CORRELAZIONE_RISPOSTA = "Risposta_1399.56_valoreClientLeve3String";
		
	public static String getIntegrazioneJson() throws Exception {
		
		try(InputStream is = Utilities.class.getResourceAsStream("/org/openspcoop2/core/protocolli/trasparente/testsuite/integrazione/json/integration_info.json")){
			return org.openspcoop2.utils.Utilities.getAsString(is, Charset.UTF_8.getValue());
		}
		
	}
		
	public static String getBase64IntegrazioneJson() throws Exception {
		String json = getIntegrazioneJson();
		return Base64Utilities.encodeAsString(json.getBytes());
	}
	
	public static String getHexIntegrazioneJson() throws Exception {
		return HexBinaryUtilities.encodeAsString(getIntegrazioneJson().getBytes());
	}
	
	public static String getJWTIntegrazioneJson() throws Exception {
		Properties signatureProps = new Properties();
		signatureProps.put("rs.security.signature.algorithm","none");
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
		JwtHeaders jwtHeaders = new JwtHeaders();
		JsonSignature jsonSignature = new JsonSignature(signatureProps, jwtHeaders, options);
		return jsonSignature.sign(getIntegrazioneJson());
	}
	
	public static String getIntegrazioneJsonCorrotto() throws Exception {
		return "{  \"clientClaimString\": \"valoreClientString\", }";
	}
	public static String getBase64IntegrazioneJsonCorrotto() throws Exception {
		String json = getIntegrazioneJsonCorrotto();
		return Base64Utilities.encodeAsString(json.getBytes());
	}
	
	public static String getIntegrazioneJsonShort() throws Exception {
		return 	"{ "+
				"\"clientClaimString\": \"valoreClientString\", \"clientClaimBoolean\": true, \"clientClaimDouble\": 1399.56, "+
				" \"clientClaimComplex\": { "+
				" \"clientClaimLeve2Complex\": { "+
				" \"clientClaimLeve3String\": \"valoreClientLeve3String\", \"clientClaimLeve3Int\": 12467 "+
				" }"+
				" }"+
				 "}";
	}
	
	public static HttpResponse _test(Logger log, MessageType messageType, 
			TipoServizio tipoServizio,
			String operazione,
			Map<String, String> headers,
			String errore,
			String idCorrelazioneRichiesta, String idCorrelazioneRisposta) throws Exception {

		String api = "TestIntegrazioneJson";
		
		String contentType=null;
		byte[]content=null;
		if(MessageType.JSON.equals(messageType)) {
			contentType = HttpConstants.CONTENT_TYPE_JSON;
			content=Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
			api = api + "REST";
		}
		else if(MessageType.SOAP_11.equals(messageType)) {
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
			content=Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes();
			api = api + "SOAP";
			if(headers==null) {
				headers = new HashMap<>();
			}
			headers.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "test");
		}
		else if(MessageType.SOAP_12.equals(messageType)) {
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_2;
			content=Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes();
			api = api + "SOAP";
		}
		else {
			throw new Exception("Tipo ["+messageType+"] non gestito");
		}
		
		Date now = DateManager.getDate();
		
		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		
		request.setReadTimeout(20000);

		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		if("url_jwt".equals(operazione)) {
			url = url + "?" + Utilities.HTTP_HEADER_CUSTOM + "=" + headers.get(Utilities.HTTP_HEADER_CUSTOM);
		}
		//System.out.println("URL ["+url+"]");
		request.setUrl(url);
		
		for (String key : headers.keySet()) {
			if(Utilities.HTTP_HEADER_CUSTOM.equals(key) && "url_jwt".equals(operazione)) {
				continue;
			}
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
		
		String responseIntegrazioneInfo = response.getHeaderFirstValue("x-test-forward-json");
		if(!"default_all_optional".equals(operazione) && errore==null) {
			assertNotNull("check responseIntegrazioneInfo per idTransazione not null: "+idTransazione, responseIntegrazioneInfo);
			assertEquals("check responseIntegrazioneInfo per idTransazione: "+idTransazione,responseIntegrazioneInfo,"1399.56");
		}
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(log, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if(errore!=null) {
			
			int code = -1;
			String error = null;
			String errorMsg = null;
			boolean clientError = false;
			
			if("default".equals(operazione) || "required_request".equals(operazione)) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(log, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_4XX);
				code = 400;
				error = "BadRequest";
				errorMsg = "Bad request";
				clientError = true;
			}
			else if("risposta_senza_json".equals(operazione) || "required_response".equals(operazione)) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(log, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
				code = 502;
				error = "InvalidResponse";
				errorMsg = "Invalid response received from the API Implementation";
				clientError = false;
			}
			
			if(MessageType.JSON.equals(messageType)) {
				verifyKo(response, code, error, errorMsg, log);
			}
			else {
				verifyKo(response, code, error, errorMsg, MessageType.SOAP_11.equals(messageType), clientError, log);
			}
			
		}
		else {
			verifyOk(response, 200, contentType); // il codice http e' gia' stato impostato
		}
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier.verify(idTransazione, esitoExpected, errore);
			
		if(!"default_all_optional".equals(operazione) && errore==null) {
		
			String id = org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier.getIdTransazioneByIdApplicativoRichiesta(idCorrelazioneRichiesta, now);
			if(idCorrelazioneRichiesta==null) {
				if(id!=null) {
					throw new Exception("Trovata correlazione applicativa richiesta non attesa");
				}
			}
			else {
				assertEquals("idTransazione: "+idTransazione,idTransazione,id);
			}
			
			id = org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier.getIdTransazioneByIdApplicativoRisposta(idCorrelazioneRisposta, now);
			if(idCorrelazioneRisposta==null) {
				if(id!=null) {
					throw new Exception("Trovata correlazione applicativa risposta non attesa");
				}
			}
			else {
				assertEquals("idTransazione: "+idTransazione,idTransazione,id);
			}
			
		}
		
		return response;	
	}
	
	public static void verifyOk(HttpResponse response, int code, String contentTypeAtteso) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(contentTypeAtteso, response.getContentType());
		
	}
	
	public static void verifyKo(HttpResponse response, int code, String error, String errorMsg, Logger logCore) throws Exception {
		
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
	
	public static void verifyKo(HttpResponse response, int code, String error, String errorMsg, boolean soap11, boolean clientError, Logger logCore) throws Exception {
		
		String soapPrefixError = soap11 ? "Client" : "Sender";
		if(!clientError) {
			soapPrefixError = soap11 ? "Server" : "Receiver";
		}
			
		String contentType = soap11 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
		MessageType messageType = soap11 ? MessageType.SOAP_11 : MessageType.SOAP_12;
		
		assertEquals(500, response.getResultHTTPOperation());
		assertEquals(contentType, response.getContentType());
		
		try {
			OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			OpenSPCoop2MessageParseResult parse = factory.createMessage(messageType, 
					MessageRole.NONE, response.getContentType(), response.getContent());
			OpenSPCoop2Message msg = parse.getMessage_throwParseThrowable();
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			assertEquals(true ,soapMsg.isFault());
			SOAPFault soapFault = soapMsg.getSOAPBody().getFault();
			assertNotNull(soapFault);
			
			assertNotNull(soapFault.getFaultCodeAsQName());
			String faultCode = soapFault.getFaultCodeAsQName().getLocalPart();
			assertNotNull(faultCode);
			if(soap11) {
				assertEquals(soapPrefixError+"."+error, faultCode);
			}
			else {
				
			}
			
			String faultString = soapFault.getFaultString();
			assertNotNull(faultString);
			assertEquals(errorMsg, faultString);
			
			String faultActor = soapFault.getFaultActor();
			assertNotNull(faultActor);
			assertEquals("http://govway.org/integration", faultActor);
						
			assertEquals(true ,ProblemUtilities.existsProblem(soapFault.getDetail(), logCore));
			Node problemNode = ProblemUtilities.getProblem(soapFault.getDetail(), logCore);
			
			ProblemUtilities.verificaProblem(problemNode, 
					code, error, errorMsg, true, 
					logCore);
			
			assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
			
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
				
	}
}
