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
package org.openspcoop2.core.protocolli.trasparente.testsuite.trasformazione.protocollo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;

import jakarta.mail.internet.ContentType;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* Soap11to12Test
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Soap11to12Test extends ConfigLoader {

	private static final String soapaction11 = "http://govway.org/testSoapAction/v11";
	private static final String soapaction12 = "http://govway.org/testSoapAction/v12";
	
	private static final String CONTENT_TYPE_SOAP11_WITH_ATTACHMENTS = HttpConstants.CONTENT_TYPE_MULTIPART_RELATED+"; boundary=\"----=_Part_0_6330713.1171639717331\"; type=\""+HttpConstants.CONTENT_TYPE_SOAP_1_1+"\"";
	private static final String CONTENT_TYPE_SOAP12_WITH_ATTACHMENTS = HttpConstants.CONTENT_TYPE_MULTIPART_RELATED+"; boundary=\"----=_Part_0_6330713.1171639717331\"; type=\""+HttpConstants.CONTENT_TYPE_SOAP_1_2+"\"";
	
	@Test
	public void erogazione_soap11to12() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"simple_11_to_12", 
				HttpConstants.CONTENT_TYPE_SOAP_1_2, null);
	}
	@Test
	public void fruizione_soap11to12() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"simple_11_to_12", 
				HttpConstants.CONTENT_TYPE_SOAP_1_2, null);
	}
	
	@Test
	public void erogazione_soap12to11() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes(),
				"simple_12_to_11", 
				HttpConstants.CONTENT_TYPE_SOAP_1_1, soapaction11);
	}
	@Test
	public void fruizione_soap12to11() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes(),
				"simple_12_to_11", 
				HttpConstants.CONTENT_TYPE_SOAP_1_1, soapaction11);
	}
	
	
	@Test
	public void erogazione_soapWithAttachments11to12() throws Throwable {
		OpenSPCoop2Message msg = Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		msg.writeTo(bout, true);
		bout.flush();
		bout.close();
		_test(TipoServizio.EROGAZIONE, msg.getContentType(), bout.toByteArray(),
				"attachments_11_to_12", 
				CONTENT_TYPE_SOAP12_WITH_ATTACHMENTS, null);
	}
	@Test
	public void fruizione_soapWithAttachments11to12() throws Throwable {
		OpenSPCoop2Message msg = Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		msg.writeTo(bout, true);
		bout.flush();
		bout.close();
		_test(TipoServizio.FRUIZIONE, msg.getContentType(), bout.toByteArray(),
				"attachments_11_to_12", 
				CONTENT_TYPE_SOAP12_WITH_ATTACHMENTS, null);
	}
	
	@Test
	public void erogazione_soapWithAttachments12to11() throws Throwable {
		OpenSPCoop2Message msg = Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		msg.writeTo(bout, true);
		bout.flush();
		bout.close();
		_test(TipoServizio.EROGAZIONE, msg.getContentType(), bout.toByteArray(),
				"attachments_12_to_11", 
				CONTENT_TYPE_SOAP11_WITH_ATTACHMENTS, soapaction11);
	}
	@Test
	public void fruizione_soapWithAttachments12to11() throws Throwable {
		OpenSPCoop2Message msg = Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		msg.writeTo(bout, true);
		bout.flush();
		bout.close();
		_test(TipoServizio.FRUIZIONE, msg.getContentType(), bout.toByteArray(),
				"attachments_12_to_11", 
				CONTENT_TYPE_SOAP11_WITH_ATTACHMENTS, soapaction11);
	}
	
	
	
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentTypeRequest, byte[]content,
			String operazione,
			String contentTypeAttesoRichiestaTrasformata, String soapActionAttesaRichiestaTrasformata) throws Exception {
		

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/TrasformazioneVersioneSoap/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TrasformazioneVersioneSoap/v1/"+operazione;

		String soapActionRequest = operazione.contains("11_to_12") ? "\""+soapaction11+"\"" : "\""+soapaction12+"\"";
		
		HttpRequest request = new HttpRequest();
		
		if(operazione.contains("11_to_12")) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, soapActionRequest);
		}
		else {
			contentTypeRequest=contentTypeRequest +"; "+HttpConstants.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION+"="+soapActionRequest;
		}
				
		request.setReadTimeout(20000);
						
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentTypeRequest);
		
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
		
		assertEquals(200, response.getResultHTTPOperation());
		//System.out.println("ATTESO ["+contentTypeResponse+"]");
		//System.out.println("RICEVUTO ["+response.getContentType()+"]");
		if(operazione.startsWith("attachments_")) {
			ContentType ctRequest = new ContentType(contentTypeRequest);
			ContentType ctResponse = new ContentType(response.getContentType());
			assertEquals(ctRequest.getBaseType(), ctResponse.getBaseType());
			assertEquals(ctRequest.getSubType(), ctResponse.getSubType());
			assertEquals(ctRequest.getPrimaryType(), ctResponse.getPrimaryType());
			assertEquals(ContentTypeUtilities.getInternalMultipartContentType(contentTypeRequest), 
					ContentTypeUtilities.getInternalMultipartContentType(response.getContentType()));
		}
		else {
			assertEquals(contentTypeRequest, response.getContentType());
		}
		
		String contentTypeRicevutaBackend = response.getHeaderFirstValue("GovWay-TestSuite-Content-Type");
		assertNotNull("verifica content type ricevuta '"+contentTypeRicevutaBackend+"'", contentTypeRicevutaBackend);
		assertEquals("verifica content type ricevuta '"+contentTypeRicevutaBackend+"' rispetto a quella attesa '"+contentTypeRequest+"'", contentTypeAttesoRichiestaTrasformata, contentTypeRicevutaBackend);
		
		if(operazione.contains("12_to_11")) {
			String soapAction = response.getHeaderFirstValue("GovWay-TestSuite-SOAPAction");
			assertNotNull("verifica soap action ricevuta '"+soapAction+"'", soapAction);
			assertEquals(soapActionAttesaRichiestaTrasformata, soapAction);
		}
		
		return response;
		
	}
	
}
