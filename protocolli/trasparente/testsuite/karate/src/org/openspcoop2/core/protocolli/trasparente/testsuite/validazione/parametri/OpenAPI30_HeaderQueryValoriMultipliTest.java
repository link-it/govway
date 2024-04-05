/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.parametri;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* OpenAPI30_ParameterSerializationTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class OpenAPI30_HeaderQueryValoriMultipliTest extends ConfigLoader {
	
	@Test
	public void erogazione_query() throws Exception {
		_query(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_query() throws Exception {
		_query(TipoServizio.FRUIZIONE);
	}
	private void _query(TipoServizio tipo) throws Exception {
		List<String> l = testParametro(tipo, "array_form_explode_true", "param=a3&param=bb4&param=cc5uguale&param=cc5uguale");
		//System.out.println("Header ritornato ["+l+"]");
		assertEquals(4, l.size());
		assertEquals(true, l.contains("a3"));
		assertEquals(true, l.contains("bb4"));
		assertEquals(true, l.contains("cc5uguale"));
	}
	
	static List<String> testParametro(TipoServizio tipoServizio, String path, String value) throws Exception {
		

		final String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/ParameterSerialization/v1/query/"+path
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/ParameterSerialization/v1/query/"+path;
		
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		
		StringBuilder sb = new StringBuilder();
		String returnHeader = "param";
		sb.append(url).append("?replyQueryParameter=").append(returnHeader).append("&replyPrefixQueryParameter=X-Test-&").append(value);
		request.setUrl(sb.toString());
		
		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());
			
		String paramRisposta = "X-Test-"+returnHeader;
		List<String> list = response.getHeaderValues(paramRisposta);
		assertEquals(true, (list!=null && !list.isEmpty()));
		return list;
		
	}

	
	@Test
	public void erogazione_header() throws Exception {
		_header(TipoServizio.EROGAZIONE, false);
	}
	@Test
	public void fruizione_header() throws Exception {
		_header(TipoServizio.FRUIZIONE, false);
	}
	@Test
	public void erogazione_header_setCookie() throws Exception {
		_header(TipoServizio.EROGAZIONE, true); // https://tools.ietf.org/html/rfc7230#section-3.2.2
	}
	@Test
	public void fruizione_header_setCookie() throws Exception {
		_header(TipoServizio.FRUIZIONE, true); // https://tools.ietf.org/html/rfc7230#section-3.2.2
	}
	private void _header(TipoServizio tipo, boolean setCookie) throws Exception {
		List<String> requestValues = new ArrayList<>();
		requestValues.add("valoreRichiesta1");
		requestValues.add("valoreRichiesta2");
		requestValues.add("valoreRichiesta3Uguale");
		requestValues.add("valoreRichiesta3Uguale");
		List<String> responseValues = new ArrayList<>();
		if(setCookie) {
			responseValues.add("id=V1");
			responseValues.add("JSESSION_ID=V2XXXX; "+HttpConstants.SET_COOKIE_MAX_AGE_PARAMETER+"=22");
			responseValues.add("JSESSION_ID=YYYV3XXXX; "+HttpConstants.SET_COOKIE_MAX_AGE_PARAMETER+"=33; "+HttpConstants.SET_COOKIE_PATH_PARAMETER+"=/test");
			responseValues.add("id=V4; "+HttpConstants.SET_COOKIE_HTTP_ONLY_PARAMETER);
		}
		else {
			responseValues.add("valoreRisposta1");
			responseValues.add("valoreRisposta2");
			responseValues.add("valoreRisposta3Uguale");
			responseValues.add("valoreRisposta3Uguale");
			responseValues.add("valoreRisposta4");
		}
		testHeader(tipo, "array_simple_explode_true", requestValues, 
				setCookie ? HttpConstants.SET_COOKIE : "X-TestHeaderRisposta", 
				responseValues);
	}
	
	static void testHeader(TipoServizio tipoServizio, String path, List<String> requestValues, String responseHttpHeaderName, List<String> responseValues) throws Exception {
		

		final String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/ParameterSerialization/v1/header/"+path
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/ParameterSerialization/v1/header/"+path;
		
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		StringBuilder sb = new StringBuilder();
		String returnHeader="X-TestHeader";
		for (String v : requestValues) {
			request.addHeader(returnHeader, v);
		}
		StringBuilder responseValue  = new StringBuilder();
		for (String rv : responseValues) {
			if(responseValue.length()>0) {
				responseValue.append(",");
			}
			responseValue.append(TransportUtils.urlEncodeParam(rv,Charset.UTF_8.getValue()));
		}
		sb.append(url).append("?replyHttpHeader=").append(returnHeader).append("&replyPrefixHttpHeader=X-TestReply-&").
			append("returnHttpHeader=").append(responseHttpHeaderName).append(":").append(responseValue.toString());
		request.setUrl(sb.toString());
		
		// CASO OK
		
		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());
			
		String paramRisposta = "X-TestReply-"+returnHeader;
		List<String> list = response.getHeaderValues(paramRisposta);
		assertEquals(true, (list!=null && !list.isEmpty()));
		assertEquals(requestValues.size(), list.size());
		for (String v : requestValues) {
			assertEquals(true, list.contains(v));
		}
			
		list = response.getHeaderValues(responseHttpHeaderName);
		assertEquals(true, (list!=null && !list.isEmpty()));
		assertEquals(responseValues.size(), list.size());
		for (String rv : responseValues) {
			assertEquals(true, list.contains(rv));
		}
		
		
		
	}
}
