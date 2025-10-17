/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* RestTest
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class UrlArrayParameterRestTestEngine extends ConfigLoader {
	
	private HttpLibraryMode mode = null;
	protected void setHttpLibraryMode(HttpLibraryMode mode) {
		this.mode = mode;
	}
	
	static final String API = "TestUrlParameterRest";
	
	private static final String ARRAY = "arrayParam";
	private static final String SINGLE = "singleParam";
	
	@Test
	public void test1() throws Exception {
		String urlArrayParameters = ARRAY+"[]=1&"+ARRAY+"[]=2&"+ARRAY+"[]=3&"+ARRAY+"[]=4&"+ARRAY+"[]=5";
		String urlSingle = SINGLE+"=true";
		test(5, "", "true",
				urlArrayParameters, urlSingle);
	}
	@Test
	public void test2() throws Exception {
		String urlArrayParameters = ARRAY+"[]=s1&"+ARRAY+"[]=s2&"+ARRAY+"[]=s3&"+ARRAY+"[]=s4&"+ARRAY+"[]=s5";
		String urlSingle = SINGLE+"=valore";
		test(5, "s", "valore",
				urlArrayParameters, urlSingle);
	}
	@Test
	public void test3() throws Exception {
		String urlArrayParameters = ARRAY+"[]=1&"+ARRAY+"[]=2&"+ARRAY+"[]=3&"+ARRAY+"[]=4&"+ARRAY+"[]=5";
		String urlSingle = SINGLE+"=true";
		test(5, "", "true",
				urlSingle, urlArrayParameters);
	}
	@Test
	public void test4() throws Exception {
		String urlArrayParameters = ARRAY+"[]=s1&"+ARRAY+"[]=s2&"+ARRAY+"[]=s3&"+ARRAY+"[]=s4&"+ARRAY+"[]=s5";
		String urlSingle = SINGLE+"=valore";
		test(5, "s", "valore",
				urlSingle, urlArrayParameters);
	}
	
	@Test
	public void testSoloArray() throws Exception {
		String urlArrayParameters = ARRAY+"[]=1&"+ARRAY+"[]=2&"+ARRAY+"[]=3&"+ARRAY+"[]=4&"+ARRAY+"[]=5";
		test(5, "", null,
				urlArrayParameters);
	}
	
	
	private HttpResponse test(
			int arraySize, String prefixArrayValue, String singleValue,
			String ... urlParameters ) throws Exception {
		
		StringBuilder sb = new StringBuilder();
		for (String s : urlParameters) {
			if(!sb.isEmpty()) {
				sb.append("&");
			}
			sb.append(s);
		}
		String url = System.getProperty("govway_base_path") + "/in/SoggettoInternoTest/"+API+"/v1/test?"+sb.toString();
		
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);	
		
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(url);
		
		if (this.mode != null) {
			this.mode.patchRequest(request);
		}
		
		HttpResponse response = HttpUtilities.httpInvoke(request);

		assertEquals(200, response.getResultHTTPOperation());
		
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		if(singleValue!=null) {
			String hdr = "GovWay-TestSuite-"+SINGLE;
			String single = response.getHeaderFirstValue(hdr);
			assertNotNull(single);
			assertEquals("hdr="+single+" (atteso:"+singleValue+")" , single, singleValue);
		}
		
		if(arraySize>0) {
			List<String> l = response.getHeaderValues("GovWay-TestSuite-"+ARRAY+"[]");
			assertNotNull(l);
			for (int i = 0; i < arraySize; i++) {
				String atteso = prefixArrayValue+(i+1);
				assertTrue("trovato:"+l+" (atteso valore:"+atteso+")" , l.contains(atteso));
			}
		}
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);		
		DBVerifier.verify(idTransazione, esitoExpected, this.mode);
		
		
		String encodedParentesiQuadraAperta = TransportUtils.urlEncodeParam("[", Charset.UTF_8.getValue());
		String encodedParentesiQuadraChiusa = TransportUtils.urlEncodeParam("]", Charset.UTF_8.getValue());
				
		for (String s : urlParameters) {
			String msgDiagnosticoAtteso = s.replace("[", encodedParentesiQuadraAperta).replace("]", encodedParentesiQuadraChiusa); // uso errore per fare il controllo anche se non è un errore
			DBVerifier.existsDiagnostico(idTransazione, msgDiagnosticoAtteso);
		}
		
		
		return response;
		
	}
}
