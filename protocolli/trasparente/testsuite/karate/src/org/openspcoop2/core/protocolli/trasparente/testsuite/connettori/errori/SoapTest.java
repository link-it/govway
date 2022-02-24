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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.errori;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.soap.SOAPFault;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ProblemUtilities;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
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
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.w3c.dom.Node;

/**
* SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {

	// html 302
	@Test
	public void erogazione_html_302() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"html302", "Gestione redirect (code:302 Location:null) non attiva");
	}
	@Test
	public void fruizione_html_302() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"html302", "Gestione redirect (code:302 Location:null) non attiva");
	}
	
	// html 403
	@Test
	public void erogazione_html_403() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"html403", "(403) Forbidden\nhttp response: <html>Access not permitted: ERROR CODE 403</html>");
	}
	@Test
	public void fruizione_html_403() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"html403", "(403) Forbidden\nhttp response: <html>Access not permitted: ERROR CODE 403</html>");
	}
	
	// html 404
	@Test
	public void erogazione_html_404() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"html404", "(404) Not Found\nhttp response: <html><head><title>Error</title></head><body>404 - Not Found</body></html>");
	}
	@Test
	public void fruizione_html_404() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"html404", "(404) Not Found\nhttp response: <html><head><title>Error</title></head><body>404 - Not Found</body></html>");
	}
	
	// html 500
	@Test
	public void erogazione_html_500() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"html500", "(500) Internal Server Error\nhttp response: <html><head><title>Error</title></head><body>500 - InternalError</body></html>");
	}
	@Test
	public void fruizione_html_500() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"html500", "(500) Internal Server Error\nhttp response: <html><head><title>Error</title></head><body>500 - InternalError</body></html>");
	}

	
	
	// noContent 302
	@Test
	public void erogazione_noContent_302() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContent302", "Gestione redirect (code:302 Location:null) non attiva");
	}
	@Test
	public void fruizione_noContent_302() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContent302", "Gestione redirect (code:302 Location:null) non attiva");
	}
	
	// noContent 403
	@Test
	public void erogazione_noContent_403() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContent403", null);
	}
	@Test
	public void fruizione_noContent_403() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContent403", null);
	}
	
	// noContent 404
	@Test
	public void erogazione_noContent_404() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContent404", null);
	}
	@Test
	public void fruizione_noContent_404() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContent404", null);
	}
	
	// noContent 500
	@Test
	public void erogazione_noContent_500() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContent500", null);
	}
	@Test
	public void fruizione_noContent_500() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContent500", null);
	}
	
	
	
	
	// noContentType 302
	@Test
	public void erogazione_noContentType_302() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContentType302", "Gestione redirect (code:302 Location:null) non attiva");
	}
	@Test
	public void fruizione_noContentType_302() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContentType302", "Gestione redirect (code:302 Location:null) non attiva");
	}
	
	// noContentType 403
	@Test
	public void erogazione_noContentType_403() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContentType403", "(403) Forbidden\nhttp response: <html>Access not permitted: ERROR CODE 403</html>");
	}
	@Test
	public void fruizione_noContentType_403() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContentType403", "(403) Forbidden\nhttp response: <html>Access not permitted: ERROR CODE 403</html>");
	}
	
	// noContentType 404
	@Test
	public void erogazione_noContentType_404() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContentType404", "(404) Not Found\nhttp response: <html><head><title>Error</title></head><body>404 - Not Found</body></html>");
	}
	@Test
	public void fruizione_noContentType_404() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContentType404", "(404) Not Found\nhttp response: <html><head><title>Error</title></head><body>404 - Not Found</body></html>");
	}
	
	// noContentType 500
	@Test
	public void erogazione_noContentType_500() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContentType500", "(500) Internal Server Error\nhttp response: <html><head><title>Error</title></head><body>500 - InternalError</body></html>");
	}
	@Test
	public void fruizione_noContentType_500() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"noContentType500", "(500) Internal Server Error\nhttp response: <html><head><title>Error</title></head><body>500 - InternalError</body></html>");
	}
	
	
	// invalidContentType (caso 1)
	@Test
	public void erogazione_invalidContentType_caso1() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"invalidContentType1", 
				"Il contenuto applicativo della risposta ricevuta non è processabile: Content-Type ''\"text/xml\";charset=UTF-8'' presente nella risposta non valido: In Content-Type string <\"text/xml\";charset=UTF-8>, expected MIME type, got text/xml");
	}
	@Test
	public void fruizione_invalidContentType_caso1() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"invalidContentType1", 
				"Il contenuto applicativo della risposta ricevuta non è processabile: Content-Type ''\"text/xml\";charset=UTF-8'' presente nella risposta non valido: In Content-Type string <\"text/xml\";charset=UTF-8>, expected MIME type, got text/xml");
	}
	
	// invalidContentType (caso 2)
	@Test
	public void erogazione_invalidContentType_caso2() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"invalidContentType2", 
				"Il contenuto applicativo della risposta ricevuta non è processabile: Content-Type ''\"text/xml\"'' presente nella risposta non valido: In Content-Type string <\"text/xml\">, expected MIME type, got text/xml");
	}
	@Test
	public void fruizione_invalidContentType_caso2() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"invalidContentType2", 
				"Il contenuto applicativo della risposta ricevuta non è processabile: Content-Type ''\"text/xml\"'' presente nella risposta non valido: In Content-Type string <\"text/xml\">, expected MIME type, got text/xml");
	}
	
	
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String msgErrore) throws Exception {
		
		String API = "SoggettoInternoTest/BackendRisposteNonSoap/v1";

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/"+API+"/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/"+API+"/"+operazione;
		url=url+"?test="+operazione;
		
		HttpRequest request = new HttpRequest();
		request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\""+operazione+"\"");
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
		
		long esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE);
		if(operazione.startsWith("invalidContentType")) {
			esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO);
		}
		
		if(operazione.startsWith("invalidContentType")) {
			verifyKo(response, org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER, INVALID_RESPONSE, 502, INVALID_RESPONSE_MESSAGE);
		}
		else {
			verifyKo(response, org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER, API_UNAVAILABLE, 503, API_UNAVAILABLE_MESSAGE);
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
	
	public static final String INVALID_RESPONSE = "InvalidResponse";
	public static final String INVALID_RESPONSE_MESSAGE = "Invalid response received from the API Implementation";

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
			
			if(!INVALID_RESPONSE.equals(error)) {
				assertNotNull(response.getHeaderFirstValue(HttpConstants.RETRY_AFTER));
			}

		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
}
