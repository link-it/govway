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
package org.openspcoop2.core.protocolli.trasparente.testsuite.encoding.dtd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.slf4j.Logger;
import org.w3c.dom.Document;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {

	@Test
	public void xml() throws Exception {
		_test(HttpConstants.CONTENT_TYPE_XML,"test_xml.xml");
	}
		
	private static void _test(String contentType, String fileName) throws Exception {

		String api = "TestRestDTDs";
		
		byte [] content = Utilities.getAsByteArray(RestTest.class.getResource("/org/openspcoop2/core/protocolli/trasparente/testsuite/encoding/dtd/"+fileName));
		
		String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/test";
		
		HttpRequest request = new HttpRequest();
		
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
		
		int esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO);
			verifyKo(response,
				logCore);
		
		DBVerifier.verify(idTransazione, esitoExpected, "DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true.");
	}
	
	public static void verifyKo(HttpResponse response, Logger logCore) throws Exception {
		
		int code = 400;
		
		String error = "UnprocessableRequestContent";
		String errorMsg = "Request payload is different from what declared in 'Content-Type' header";
			
		assertEquals(HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807, response.getContentType());
		
		try {
			XPathExpressionEngine xpathEngine = new XPathExpressionEngine();
			Document d = XMLUtils.getInstance().newDocument(response.getContent());
			DynamicNamespaceContext dnc = new DynamicNamespaceContext();
			dnc.findPrefixNamespace(d);
			
			assertEquals("https://govway.org/handling-errors/"+code+"/"+error+".html", xpathEngine.getStringMatchPattern(d, dnc, "//{urn:ietf:rfc:7807}type/text()"));
			assertEquals(error, xpathEngine.getStringMatchPattern(d, dnc, "//{urn:ietf:rfc:7807}title/text()"));
			assertEquals(code+"", xpathEngine.getStringMatchPattern(d, dnc,  "//{urn:ietf:rfc:7807}status/text()"));
			assertNotEquals(null, xpathEngine.getStringMatchPattern(d, dnc, "//{urn:ietf:rfc:7807}govway_id/text()"));	
			assertEquals(true, xpathEngine.getStringMatchPattern(d, dnc, "//{urn:ietf:rfc:7807}detail/text()").equals(errorMsg));
			
			assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));

			if(code==504) {
				assertNotNull(response.getHeaderFirstValue(HttpConstants.RETRY_AFTER));
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
				
	}
	
}
