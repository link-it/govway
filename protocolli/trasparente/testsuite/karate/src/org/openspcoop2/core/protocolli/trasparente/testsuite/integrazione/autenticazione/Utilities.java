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
package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.autenticazione;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.w3c.dom.Element;

/**
* Utilities
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Utilities extends ConfigLoader {
	
	public final static String PREFIX_HEADER = "GovWay-TestSuite-";
	public final static String CLIENT_ID_HEADER = "Client-ID";
	public final static String CLIENT_ID_VALUE_TEST = "Id-Test";
	public final static String CLIENT_SECRET_HEADER = "Client-Secret";
	public final static String CLIENT_SECRET_VALUE_TEST = "Id-XXX-Test";
	public final static String SECURITY_TOKEN = "X-Security-Token";
	public final static String TOKEN = "TOKENESEMPIOXXX";
	public final static String TOKEN_TYPE_COOKIE = "COOKIE";
	public final static String TOKEN_TYPE_JWT = "JWT";
	public final static String TOKEN_TYPE_JWT_BEARER = "Bearer";
	public final static String TOKEN_TYPE_HEADER_SOAP = "HEADER-SOAP";
	public final static String TOKEN_TYPE_HEADER_SOAP_VALUE = "3.3.6";
	public final static String COOKIE_NAME = "GovWayCookieExample";
	
	public final static String TOKEN_RISPOSTA = "TOKENESEMPIORISPOSTAXXX";
	public final static String SECURITY_TOKEN_RESPONSE = CLIENT_ID_HEADER;

	
	public static HttpResponse _test(MessageType messageType,
			TipoServizio tipoServizio,
			String operazione,
			Map<String, String> headers) throws Exception {

		String api = "TestAutenticazioneGateway";
		
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
				headers = new HashMap<String, String>();
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
		
		for (String key : headers.keySet()) {
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
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		
		verifyOk(response, 200, contentType); // il codice http e' gia' stato impostato
		
		DBVerifier.verify(idTransazione, esitoExpected);
				
		return response;	
	}
	
	public static void verifyOk(HttpResponse response, int code, String contentTypeAtteso) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(contentTypeAtteso, response.getContentType());
		
	}
	
	public static void verifyResponseSecretHeader(HttpResponse response) {
		int sizeSecret = 0;
		String secret = null;
		for (String hdr : response.getHeadersValues().keySet()) {
			//System.out.println("HDR ["+hdr+"]=["+response.getHeadersValues().get(hdr)+"]");
			if((Utilities.PREFIX_HEADER+Utilities.CLIENT_SECRET_HEADER).equalsIgnoreCase(hdr)) {
				if(response.getHeaderValues(hdr)!=null) {
					sizeSecret = response.getHeaderValues(hdr).size();
					secret = response.getHeaderFirstValue(hdr);
				}
			}
		}
		assertEquals(1, sizeSecret);
		assertNotNull(secret);
		if(!secret.equals(Utilities.CLIENT_SECRET_VALUE_TEST)) {
			System.out.println(Utilities.PREFIX_HEADER+Utilities.CLIENT_SECRET_HEADER+": ["+secret+"]");
		}
		assertEquals(Utilities.CLIENT_SECRET_VALUE_TEST, secret);
	}
	
	public static void verifyResponseIdHeader(HttpResponse response) {
		int sizeSecret = 0;
		String secret = null;
		for (String hdr : response.getHeadersValues().keySet()) {
			//System.out.println("HDR ["+hdr+"]=["+response.getHeadersValues().get(hdr)+"]");
			if((Utilities.PREFIX_HEADER+Utilities.CLIENT_ID_HEADER).equalsIgnoreCase(hdr)) {
				if(response.getHeaderValues(hdr)!=null) {
					sizeSecret = response.getHeaderValues(hdr).size();
					secret = response.getHeaderFirstValue(hdr);
				}
			}
		}
		assertEquals(1, sizeSecret);
		assertNotNull(secret);
		if(!secret.equals(Utilities.CLIENT_ID_VALUE_TEST)) {
			System.out.println(Utilities.PREFIX_HEADER+Utilities.CLIENT_ID_HEADER+": ["+secret+"]");
		}
		assertEquals(Utilities.CLIENT_ID_VALUE_TEST, secret);
	}
	
	public static void verifyResponseCookieHeader(HttpResponse response, boolean risposta) {
		int sizeSecret = 0;
		String secret = null;
		String hdrAtteso = (risposta ? "" : Utilities.PREFIX_HEADER)+HttpConstants.COOKIE;
		for (String hdr : response.getHeadersValues().keySet()) {
			//System.out.println("HDR ["+hdr+"]=["+response.getHeadersValues().get(hdr)+"]");
			if(hdrAtteso.equalsIgnoreCase(hdr)) {
				if(response.getHeaderValues(hdr)!=null) {
					sizeSecret = response.getHeaderValues(hdr).size();
					secret = response.getHeaderFirstValue(hdr);
				}
			}
		}
		
		String atteso = Utilities.COOKIE_NAME+"="+(risposta ? Utilities.TOKEN_RISPOSTA : Utilities.TOKEN);
		assertEquals(1, sizeSecret);
		assertNotNull(secret);
		if(!secret.equals(atteso)) {
			System.out.println(hdrAtteso+": ["+secret+"]");
		}
		assertEquals(atteso, secret);
	}
	
	public static void verifyResponseAuthorizationHeader(HttpResponse response, boolean risposta) {
		int sizeSecret = 0;
		String secret = null;
		String hdrAtteso = (risposta ? "" : Utilities.PREFIX_HEADER)+HttpConstants.AUTHORIZATION;
		for (String hdr : response.getHeadersValues().keySet()) {
			//System.out.println("HDR ["+hdr+"]=["+response.getHeadersValues().get(hdr)+"]");
			if(hdrAtteso.equalsIgnoreCase(hdr)) {
				if(response.getHeaderValues(hdr)!=null) {
					sizeSecret = response.getHeaderValues(hdr).size();
					secret = response.getHeaderFirstValue(hdr);
				}
			}
		}
		
		String atteso = HttpConstants.AUTHORIZATION_PREFIX_BEARER+(risposta ? Utilities.TOKEN_RISPOSTA : Utilities.TOKEN);
		assertEquals(1, sizeSecret);
		assertNotNull(secret);
		if(!secret.equals(atteso)) {
			System.out.println(hdrAtteso+": ["+secret+"]");
		}
		assertEquals(atteso, secret);
	}
	
	public static void verifyResponseSoapHeader(HttpResponse response, boolean risposta) throws Exception {
	
		String atteso = (risposta ? Utilities.TOKEN_RISPOSTA : Utilities.TOKEN);
		
		String forSearch = new String(response.getContent());
		
		try {
			XMLUtils xmlUtils = new XMLUtils();
			Element el = xmlUtils.newElement(response.getContent());
			DynamicNamespaceContext dnc = new DynamicNamespaceContext();
			dnc.findPrefixNamespace(el);
			
			XPathExpressionEngine xpathEngine = new XPathExpressionEngine();
			String sec = "{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security";
			String bst = "{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}BinarySecurityToken";
			assertEquals(atteso, xpathEngine.getStringMatchPattern(forSearch, dnc, "//"+sec+"/"+bst+"/text()"));
		}catch(Throwable t) {
			System.out.println("RISPOSTA: ["+forSearch+"]");
			throw t;
		}
		
		
	}
}
