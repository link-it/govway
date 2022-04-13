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
package org.openspcoop2.core.protocolli.trasparente.testsuite.encoding.entity_reference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.encoding.charset.CharsetUtilities;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
* SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {



	
	// STREAM
	@Test
	public void erogazione_soap11_stream() throws Exception {
		_test("stream",true, false);
	}
	@Test
	public void erogazione_soap12_stream() throws Exception {
		_test("stream",false, false);
	}

	
	// STREAM + addHeader
	@Test
	public void erogazione_soap11_stream_addHeader() throws Exception {
		_test("addHeader",true, true);
	}
	@Test
	public void erogazione_soap12_stream_addHeader() throws Exception {
		_test("addHeader",false, true);
	}
	
	
	// DUMP
	@Test
	public void erogazione_soap11_dump() throws Exception {
		_test("dump",true,false);
	}
	@Test
	public void erogazione_soap12_dump() throws Exception {
		_test("dump",false,false);
	}
	
	
	
	// DUMP + addHeader
	@Test
	public void erogazione_soap11_dump_addHeader() throws Exception {
		_test("dump_addHeader", true, true);
	}
	@Test
	public void erogazione_soap12_dump_addHeader() throws Exception {
		_test("dump_addHeader", false, true);
	}
	

		
	
	// modify_content: non eseguibile, causa una allocazione del GC che fa andare in timeout la richiesta. Nel GC vi è un eccesivo utilizzo di G1 Humongous Allocation
	/*
	@Test
	public void erogazione_soap11_modify_content() throws Exception {
		_test("modify_content",true);
	}
	@Test
	public void erogazione_soap12_modify_content() throws Exception {
		_test("modify_content",false);
	}
	*/
	
	
	
	
	private static void _test(
			String operazione, boolean soap11, boolean addHeader) throws Exception {


		
		String api = "TestEntityReference";
		
		String contentType = null;
		if(soap11) {
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
		}
		else {
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_2;
		}
		
		byte [] content = Utilities.getAsByteArray(SoapTest.class.getResource("/org/openspcoop2/core/protocolli/trasparente/testsuite/encoding/entity_reference/test_soap"+(soap11 ? "11" : "12")+".xml"));
		
		String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		
		if(soap11) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "testCharset");
		}
		
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
		
		int esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		verifyOk(response, 200, contentType, logCore);
		
		verifyResponseContent(content, response, addHeader, soap11, operazione);
		
		DBVerifier.verify(idTransazione, esitoExpected, null);
	}
	
	public static void verifyOk(HttpResponse response, int code, String contentType, Logger logCore) throws Exception {
		
		assertEquals(code, response.getResultHTTPOperation());
			
		assertEquals(contentType, response.getContentType());
		
		boolean b = response.getContent()!=null;
		assertEquals(true, b);
				
	}
	
	public static void verifyResponseContent(byte[]content, HttpResponse response, boolean addHeader, boolean soap11, String operazione) throws Exception {
		
		String contentS = new String(content);
		
		if(addHeader) {
			String namespace = soap11 ? org.openspcoop2.message.constants.Costanti.SOAP_ENVELOPE_NAMESPACE : org.openspcoop2.message.constants.Costanti.SOAP12_ENVELOPE_NAMESPACE;
			contentS = contentS.replace("<soapenv:Body>", CharsetUtilities.getSoapHeader(namespace, !soap11, false, Charset.UTF_8, operazione)+"<soapenv:Body>");
			contentS = contentS.replace("soap:Header xmlns:soap","soapenv:Header xmlns:soapenv");
			contentS = contentS.replace("soap:Header","soapenv:Header");
			contentS = contentS.replace("xmlns:soapenv=\""+namespace+"\" soapenv:mustUnderstand=\""+(soap11 ? "1" : "true")+"\">",
					"soapenv:mustUnderstand=\""+(soap11 ? "1" : "true")+"\" xmlns:soapenv=\""+namespace+"\">");
			content = contentS.getBytes();
		}
		
		if(!Arrays.equals(content, response.getContent())) {
			String responseS = new String(response.getContent());
			//System.out.println("RESPONSE: "+responseS);
			org.openspcoop2.utils.resources.FileSystemUtilities.writeFile("/tmp/atteso", content);
			org.openspcoop2.utils.resources.FileSystemUtilities.writeFile("/tmp/ricevuto", response.getContent());
			System.out.println("DEBUG ATTIVO, scritto in /tmp");
			System.out.println("atteso ["+contentS.substring(0, 300)+"] ricevuto ["+responseS.substring(0, 300)+"]");
			assertEquals(contentS, responseS); // per far loggare i due messagi
		}
	}
}
