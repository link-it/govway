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
package org.openspcoop2.core.protocolli.trasparente.testsuite.trasformazione.soap_action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {

	private final static String soapaction11 = "http://govway.org/testSoapAction/v11";
	private final static String soapaction12 = "http://govway.org/testSoapAction/v12";
	private final static String soapaction12_nonQuotata = "testNonQuotatasoap12";
	
	@Test
	public void erogazione_soap11() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"soap11", "http://govway.org/testSoapAction/v11", null,
				false, false);
	}
	@Test
	public void fruizione_soap11() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"soap11", "http://govway.org/testSoapAction/v11", null,
				false, false);
	}
	@Test
	public void erogazione_soap11_quotata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"soap11", "\"http://govway.org/testSoapAction/v11\"", null,
				true, false);
	}
	@Test
	public void fruizione_soap11_quotata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"soap11", "\"http://govway.org/testSoapAction/v11\"", null,
				true, false);
	}
	
	@Test
	public void erogazione_soap12() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes(),
				"soap12", soapaction12_nonQuotata, null,
				false, false);
	}
	@Test
	public void fruizione_soap12() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes(),
				"soap12", soapaction12_nonQuotata, null,
				false, false);
	}
	@Test
	public void erogazione_soap12_quotata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes(),
				"soap12", "\"http://govway.org/testSoapAction/v12\"", null,
				true, false);
	}
	@Test
	public void fruizione_soap12_quotata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes(),
				"soap12", "\"http://govway.org/testSoapAction/v12\"", null,
				true, false);
	}
	
	
	
	
	@Test
	public void erogazione_soap11_trasformazioneViaTemplate() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"soap11.trasformazioneTemplate", "http://govway.org/testSoapActionTrasformata/template/1/v11", null,
				false, true);
	}
	@Test
	public void fruizione_soap11_trasformazioneViaTemplate() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"soap11.trasformazioneTemplate", "http://govway.org/testSoapActionTrasformata/template/2/v11", null,
				false, true);
	}
	@Test
	public void erogazione_soap11_quotata_trasformazioneViaTemplate() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"soap11.trasformazioneTemplate", "\"http://govway.org/testSoapActionTrasformata/template/3/v11\"", null,
				true, true);
	}
	@Test
	public void fruizione_soap11_quotata_trasformazioneViaTemplate() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"soap11.trasformazioneTemplate", "\"http://govway.org/testSoapActionTrasformata/template/4/v11\"", null,
				true, true);
	}
	
	
	@Test
	public void erogazione_soap12_trasformazioneViaTemplate() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes(),
				"soap12.trasformazioneTemplate", "http://govway.org/testSoapActionTrasformata/template/1/v12", null,
				false, true);
	}
	@Test
	public void fruizione_soap12_trasformazioneViaTemplate() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes(),
				"soap12.trasformazioneTemplate", "http://govway.org/testSoapActionTrasformata/template/2/v12", null,
				false, true);
	}
	@Test
	public void erogazione_soap12_quotata_trasformazioneViaTemplate() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes(),
				"soap12.trasformazioneTemplate", "\"http://govway.org/testSoapActionTrasformata/template/3/v12\"", null,
				true, true);
	}
	@Test
	public void fruizione_soap12_quotata_trasformazioneViaTemplate() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes(),
				"soap12.trasformazioneTemplate", "\"http://govway.org/testSoapActionTrasformata/template/4/v12\"", null,
				true, true);
	}
	
	
	
	
	
	@Test
	public void erogazione_soap11_trasformazioneViaHeader() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"soap11.trasformazioneHeader", "http://govway.org/testSoapActionTrasformata/hdr/1/v11", null,
				false, true);
	}
	@Test
	public void fruizione_soap11_trasformazioneViaHeader() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"soap11.trasformazioneHeader", "http://govway.org/testSoapActionTrasformata/hdr/2/v11", null,
				false, true);
	}
	@Test
	public void erogazione_soap11_quotata_trasformazioneViaHeader() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"soap11.trasformazioneHeader", "\"http://govway.org/testSoapActionTrasformata/hdr/3/v11\"", null,
				true, true);
	}
	@Test
	public void fruizione_soap11_quotata_trasformazioneViaHeader() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"soap11.trasformazioneHeader", "\"http://govway.org/testSoapActionTrasformata/hdr/4/v11\"", null,
				true, true);
	}
	
	
	
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentTypeParam, byte[]content,
			String operazione, String soapActionAttesaRisposta, String msgErrore,
			boolean quota, boolean trasforma) throws Exception {
		

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/ApiSoapTestTrasformazioni/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/ApiSoapTestTrasformazioni/v1/"+operazione;

		String soapActionRequest = null;
		if(quota) {
			soapActionRequest = operazione.contains("11") ? "\""+soapaction11+"\"" : "\""+soapaction12+"\"";
		}
		else {
			soapActionRequest = operazione.contains("11") ? soapaction11 : soapaction12_nonQuotata; 
		}
		
		HttpRequest request = new HttpRequest();
		
		if(trasforma) {
			request.addHeader("Trasformazione",soapActionAttesaRisposta);
		}
		
		String contentTypeRequest = contentTypeParam;
		String contentTypeResponse = contentTypeParam;
		if(operazione.contains("11")) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, soapActionRequest);
		}
		else {
			contentTypeRequest=contentTypeRequest +"; "+HttpConstants.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION+"="+soapActionRequest;
			if(trasforma && !soapActionAttesaRisposta.startsWith("\"")) {
				contentTypeResponse=contentTypeResponse +"; "+HttpConstants.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION+"=\""+soapActionAttesaRisposta+"\"";
			}
			else {
				contentTypeResponse=contentTypeResponse +"; "+HttpConstants.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION+"="+soapActionAttesaRisposta;
			}
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
		assertEquals(contentTypeResponse, response.getContentType());
		
		if(operazione.contains("11")) {
			String soapAction = response.getHeaderFirstValue("GovWay-TestSuite-SOAPAction");
			assertNotNull("verifica soap action ricevuta '"+soapAction+"'", soapAction);
			assertEquals(soapActionAttesaRisposta, soapAction);
		}
		else {
			String soapAction = response.getHeaderFirstValue("GovWay-TestSuite-Content-Type");
			assertNotNull("verifica soap action ricevuta '"+soapAction+"'", soapAction);
			assertEquals(contentTypeResponse, soapAction);
		}
		
		return response;
		
	}
	
}
