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
package org.openspcoop2.core.protocolli.trasparente.testsuite.encoding.dtd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.soap.SOAPFault;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ProblemUtilities;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
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
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;
import org.w3c.dom.Node;

/**
* SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {

	@Test
	public void soap11() throws Exception {
		_test(HttpConstants.CONTENT_TYPE_SOAP_1_1,"test_soap11.xml");
	}
	
	@Test
	public void soap12() throws Exception {
		_test(HttpConstants.CONTENT_TYPE_SOAP_1_2,"test_soap12.xml");
	}
	
	@Test
	public void soap11withAttachments() throws Exception {
		_test("multipart/related;   boundary=\"----=_Part_0_6330713.1171639717331\";   type=\""+HttpConstants.CONTENT_TYPE_SOAP_1_1+"\"",
				"test_soap11withAttachments.bin");
	}
	
	@Test
	public void soap12withAttachments() throws Exception {
		_test("multipart/related;   boundary=\"----=_Part_0_6330713.1171639717331\";   type=\""+HttpConstants.CONTENT_TYPE_SOAP_1_2+"\"",
				"test_soap12withAttachments.bin");
	}

	
	private static void _test(String contentType, String fileName) throws Exception {

		String api = "TestSoapDTDs";
		
		byte [] content = Utilities.getAsByteArray(SoapTest.class.getResource("/org/openspcoop2/core/protocolli/trasparente/testsuite/encoding/dtd/"+fileName));
		
		String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/test";
		
		HttpRequest request = new HttpRequest();
		
		request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "testCharset");
		
		request.setReadTimeout(10000);
		
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
		
		int esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO);
			verifyKo(response,
				fileName.contains("11"), 
				logCore);
		
		DBVerifier.verify(idTransazione, esitoExpected, "Unable to create envelope from given source: org.xml.sax.SAXException: Document Type Declaration is not allowed");
	}
	
	public static void verifyKo(HttpResponse response, boolean soap11, Logger logCore) throws Exception {
		
		int code = 400;
		
		String soapPrefixError = soap11 ? "Client" : "Sender";
		String error = "UnprocessableRequestContent";
		String errorMsg = "Request payload is different from what declared in 'Content-Type' header";
			
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
						
			assertEquals(true ,ProblemUtilities.existsProblem(soapFault.getDetail(), getLoggerCore()));
			Node problemNode = ProblemUtilities.getProblem(soapFault.getDetail(), getLoggerCore());
			
			ProblemUtilities.verificaProblem(problemNode, 
					code, error, errorMsg, true, 
					getLoggerCore());
			
			assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
			
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
				
	}
	
}
