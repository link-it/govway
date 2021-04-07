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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.timeout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.soap.SOAPFault;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
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
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.id.IDUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.w3c.dom.Node;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {

	// connectTimeout registrazioneAbilitata
	@Test
	public void erogazione_connectTimeout_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata", "connectionTimeout", "connect timed out",
				true);
	}
	@Test
	public void fruizione_connectTimeout_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata", "connectionTimeout", "connect timed out",
				true);
	}
	
	// connectTimeout registrazioneDisabilitata
	@Test
	public void erogazione_connectTimeout_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "connectionTimeout", "connect timed out",
				true);
	}
	@Test
	public void fruizione_connectTimeout_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "connectionTimeout", "connect timed out",
				true);
	}
	
	
	// echoReceiveRequestSlow registrazioneAbilitata
	@Test
	public void erogazione_echoReceiveRequestSlow_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneAbilitata", "echoReceiveRequestSlow", "Read timed out",
				false);
	}
	@Test
	public void fruizione_echoReceiveRequestSlow_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneAbilitata", "echoReceiveRequestSlow", "Read timed out",
				false);
	}
	
	
	// echoReceiveRequestSlow registrazioneDisabilitata
	@Test
	public void erogazione_echoReceiveRequestSlow_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneDisabilitata", "echoReceiveRequestSlow", "Read timed out",
				false);
	}
	@Test
	public void fruizione_echoReceiveRequestSlow_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneDisabilitata", "echoReceiveRequestSlow", "Read timed out",
				false);
	}
	
	
	// echoSleepBeforeResponse registrazioneAbilitata
	@Test
	public void erogazione_echoSleepBeforeResponse_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneAbilitata", "echoSleepBeforeResponse", "Read timed out",
				false);
	}
	@Test
	public void fruizione_echoSleepBeforeResponse_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneAbilitata", "echoSleepBeforeResponse", "Read timed out",
				false);
	}
	
	// echoSleepBeforeResponse registrazioneDisabilitata
	@Test
	public void erogazione_echoSleepBeforeResponse_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneDisabilitata", "echoSleepBeforeResponse", "Read timed out",
				false);
	}
	@Test
	public void fruizione_echoSleepBeforeResponse_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneDisabilitata", "echoSleepBeforeResponse", "Read timed out",
				false);
	}
	
	
	
	// echoSendResponseSlow registrazioneAbilitata
	@Test
	public void erogazione_echoSendResponseSlow_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneAbilitata", "echoSendResponseSlow", "Response Read timed out",
				false);
	}
	@Test
	public void fruizione_echoSendResponseSlow_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
			"sendRegistrazioneAbilitata", "echoSendResponseSlow", "Response Read timed out",
			false);
	}
	
	
	
	// echoSendResponseSlow registrazioneDisabilitata
	@Test
	public void erogazione_echoSendResponseSlow_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneDisabilitata", "echoSendResponseSlow", "Response Read timed out",
				false);
	}
	@Test
	public void fruizione_echoSendResponseSlow_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
			"sendRegistrazioneDisabilitata", "echoSendResponseSlow", "Response Read timed out",
			false);
	}
	
	
	
	// echoSendResponseSlow correlazioneApplicativa
	@Test
	public void erogazione_echoSendResponseSlow_correlazioneApplicativa() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendCorrelazioneApplicativa", "EchoSendResponseSlow", "Response Read timed out",
				false);
	}
	@Test
	public void fruizione_echoSendResponseSlow_correlazioneApplicativa() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
			"sendCorrelazioneApplicativa", "EchoSendResponseSlow", "Response Read timed out",
			false);
	}
	
	
	
	// clientSendRequestSlow registrazioneAbilitata
	@Test
	public void erogazione_clientSendRequestSlow_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitataClientSendSlow", "default", "Request Read timed out",
				false,
				1000, 100, true);
	}
	@Test
	public void fruizione_clientSendRequestSlow_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitataClientSendSlow", "default", "Request Read timed out",
				false,
				1000, 100, true);
	}
	
	
	// clientSendRequestSlow registrazioneDisabilitata
	@Test
	public void erogazione_clientSendRequestSlow_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitataClientSendSlow", "default", "Request Read timed out",
				false,
				1000, 100, true);
	}
	@Test
	public void fruizione_clientSendRequestSlow_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitataClientSendSlow", "default", "Request Read timed out",
				false,
				1000, 100, true);
	}
	
	
	
	// clientSendRequestSlow correlazioneApplicativa
	@Test
	public void erogazione_clientSendRequestSlow_correlazioneApplicativa() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendCorrelazioneApplicativa", "ClientSendRequestSlow", "Request Read timed out",
				false,
				1000, 100, true);
	}
	@Test
	public void fruizione_clientSendRequestSlow_correlazioneApplicativa() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendCorrelazioneApplicativa", "ClientSendRequestSlow", "Request Read timed out",
				false,
				1000, 100, true);
	}
	
	
	// connessioneCLientInterrotta
	@Test
	public void erogazione_connessioneCLientInterrotta() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes(),
				"connessioneClientInterrotta", "connessioneClientInterrotta", "Broken pipe",
				false,
				idApplicativo);
	}
	@Test
	public void fruizione_connessioneCLientInterrotta() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes(),
				"connessioneClientInterrotta", "connessioneClientInterrotta", "Broken pipe",
				false,
				idApplicativo);
	}
	
	
	
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, String msgErrore,
			boolean connectTimeout) throws Exception {
		return _test(
				tipoServizio, contentType, content,
				operazione, tipoTest, msgErrore,
				connectTimeout,
				null, null, false, 
				null);
	}
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, String msgErrore,
			boolean connectTimeout,
			String applicativeId) throws Exception {
		return _test(
				tipoServizio, contentType, content,
				operazione, tipoTest, msgErrore,
				connectTimeout,
				null, null, false, 
				applicativeId);
	}
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, String msgErrore,
			boolean connectTimeout,
			Integer throttlingByte, Integer throttlingMs, boolean throttlingSend) throws Exception {
		return _test(
				tipoServizio, contentType, content,
				operazione, tipoTest, msgErrore,
				connectTimeout,
				throttlingByte, throttlingMs, throttlingSend,
				null);
	}
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, String msgErrore,
			boolean connectTimeout,
			Integer throttlingByte, Integer throttlingMs, boolean throttlingSend,
			String applicativeId) throws Exception {
		

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/TempiRispostaSOAP/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TempiRispostaSOAP/v1/"+operazione;
		if(TipoServizio.FRUIZIONE.equals(tipoServizio)) {
			if(!"connessioneClientInterrotta".equals(operazione) && 
					!"sendRegistrazioneAbilitataClientSendSlow".equals(operazione) && 
					!"sendRegistrazioneDisabilitataClientSendSlow".equals(operazione)) {
				url=url+"."+tipoTest;
			}
		}
		url=url+"?test="+tipoTest;
		
		HttpRequest request = new HttpRequest();
		request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"test\"");
		
		if(throttlingByte!=null && throttlingMs!=null) {
			if(throttlingSend) {
				request.setThrottlingSendByte(throttlingByte);
				request.setThrottlingSendMs(throttlingMs);
			}
		}
		
		if("connessioneClientInterrotta".equals(operazione)) {
			request.setReadTimeout(2000);
		}
		else {
			request.setReadTimeout(20000);
		}
				
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		request.setUrl(url);
		
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
			if("connessioneClientInterrotta".equals(operazione)) {
				throw new Exception("Expected exception timeout client");
			}
		}catch(Throwable t) {
			if("connessioneClientInterrotta".equals(operazione)) {
				// costruisco io la risposta
				response = new HttpResponse();
				response.setResultHTTPOperation(200);
				response.setContentType(HttpConstants.CONTENT_TYPE_SOAP_1_1);
				Utilities.sleep(5000); // aspetto che termina il server
				response.addHeader("GovWay-Transaction-ID", DBVerifier.getIdTransazione(applicativeId));
			}
			else {
				throw t;
			}
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		long esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE);
		if(connectTimeout) {
			verifyKo(response, org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER, API_UNAVAILABLE, 503, API_UNAVAILABLE_MESSAGE);
		}
		else {
			if(operazione.equals("sendCorrelazioneApplicativa")) {
				if(throttlingSend) {
					esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO);
					verifyKo(response, org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT, REQUEST_TIMED_OUT, 400, REQUEST_TIMED_OUT_MESSAGE);
				}
				else {
					esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO);	
					verifyKo(response, org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER, ENDPOINT_READ_TIMEOUT, 504, ENDPOINT_READ_TIMEOUT_MESSAGE);
				}
			}
			else if(throttlingSend && operazione.contains("SendSlow") ) {
				esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO);
				verifyKo(response, org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT, REQUEST_TIMED_OUT, 400, REQUEST_TIMED_OUT_MESSAGE);
			}
			else if("sendRegistrazioneDisabilitata".equals(operazione) && "echoSendResponseSlow".equals(tipoTest)) {
				esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO);
				verifyKo(response, org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER, ENDPOINT_READ_TIMEOUT, 504, ENDPOINT_READ_TIMEOUT_MESSAGE);
			}
			else if("connessioneClientInterrotta".equals(operazione)) {
				esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_CONNESSIONE_CLIENT_NON_DISPONIBILE);
				verifyOk(response, 200); // il codice http e' gia' stato impostato
			}
			else {
				verifyKo(response, org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER, ENDPOINT_READ_TIMEOUT, 504, ENDPOINT_READ_TIMEOUT_MESSAGE);
			}
		}
		
		DBVerifier.verify(idTransazione, esitoExpected, msgErrore);
		
		return response;
		
	}
	
	public static final String API_UNAVAILABLE = "APIUnavailable";
	public static final String API_UNAVAILABLE_MESSAGE = "The API Implementation is temporary unavailable";
	
	public static final String ENDPOINT_READ_TIMEOUT = "EndpointReadTimeout";
	public static final String ENDPOINT_READ_TIMEOUT_MESSAGE = "Read Timeout calling the API implementation";
	
	public static final String REQUEST_TIMED_OUT = "RequestReadTimeout";
	public static final String REQUEST_TIMED_OUT_MESSAGE = "Timeout reading the request payload";

	public static void verifyKo(HttpResponse response, String soapPrefixError, String error, int code, String errorMsg) throws Exception {
		
		assertEquals(500, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_SOAP_1_1, response.getContentType());
		
		try {
			OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			OpenSPCoop2MessageParseResult parse = factory.createMessage(MessageType.SOAP_11, MessageRole.NONE, response.getContentType(), response.getContent());
			OpenSPCoop2Message msg = parse.getMessage_throwParseThrowable();
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			assertEquals(true ,soapMsg.isFault());
			SOAPFault soapFault = soapMsg.getSOAPBody().getFault();
			assertNotNull(soapFault);
			
			assertNotNull(soapFault.getFaultCodeAsQName());
			String faultCode = soapFault.getFaultCodeAsQName().getLocalPart();
			assertNotNull(faultCode);
			assertEquals(soapPrefixError+"."+error, faultCode);
			
			String faultString = soapFault.getFaultString();
			assertNotNull(faultString);
			assertEquals(errorMsg, faultString);
			
			String faultActor = soapFault.getFaultActor();
			assertNotNull(faultActor);
			assertEquals("http://govway.org/integration", faultActor);
						
			assertEquals(true ,ProblemUtilities.existsProblem(soapFault.getDetail(), getLoggerCore()));
			Node problemNode = ProblemUtilities.getProblem(soapFault.getDetail(), getLoggerCore());
			
			ProblemUtilities.verificaProblem(problemNode, 
					code, error, errorMsg, true, 
					getLoggerCore());
			
			assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
			
			if(code==504) {
				assertNotNull(response.getHeaderFirstValue(HttpConstants.RETRY_AFTER));
			}

		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void verifyOk(HttpResponse response, int code) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_SOAP_1_1, response.getContentType());
		
	}
}
