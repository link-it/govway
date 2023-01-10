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


package org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.parametri;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* OpenAPI30_HeaderParameterSerializationTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class OpenAPI30_HeaderParameterSerializationTest extends ConfigLoader {
	
	// https://swagger.io/docs/specification/serialization/
	
	@Test
	public void erogazione_array_simple_explode_true() throws Exception {
		_array_simple_explode_true(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_array_simple_explode_true() throws Exception {
		_array_simple_explode_true(TipoServizio.FRUIZIONE);
	}
	private void _array_simple_explode_true(TipoServizio tipo) throws Exception {
		testHeader(tipo, "array_simple_explode_true", "a3,bb4,cc5", "rr1,rr2");
	}
	
	
	@Test
	public void erogazione_array_simple_explode_false() throws Exception {
		_array_simple_explode_false(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_array_simple_explode_false() throws Exception {
		_array_simple_explode_false(TipoServizio.FRUIZIONE);
	}
	private void _array_simple_explode_false(TipoServizio tipo) throws Exception {
		testHeader(tipo, "array_simple_explode_false", "a3,bb4,cc5", "rr1,rr2");
	}
	
	
	
	@Test
	public void erogazione_object_simple_explode_true() throws Exception {
		_object_simple_explode_true(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_object_simple_explode_true() throws Exception {
		_object_simple_explode_true(TipoServizio.FRUIZIONE);
	}
	private void _object_simple_explode_true(TipoServizio tipo) throws Exception {
		testHeader(tipo, "object_simple_explode_true", "role=admin,firstName=Alex", "role=admin2,firstName=Alex22");
	}
	
	
	@Test
	public void erogazione_object_simple_explode_false() throws Exception {
		_object_simple_explode_false(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_object_simple_explode_false() throws Exception {
		_object_simple_explode_false(TipoServizio.FRUIZIONE);
	}
	private void _object_simple_explode_false(TipoServizio tipo) throws Exception {
		testHeader(tipo, "object_simple_explode_false", "role,admin,firstName,Alex", "role,admin2,firstName,Alex22");
	}
	
	
	
	static void testHeader(TipoServizio tipoServizio, String path, String requestValue, String responseValue) throws Exception {
		

		final String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/ParameterSerialization/v1/header/"+path
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/ParameterSerialization/v1/header/"+path;
		
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		StringBuilder sb = new StringBuilder();
		String returnHeader="X-TestHeader";
		request.addHeader(returnHeader, requestValue);
		sb.append(url).append("?replyHttpHeader=").append(returnHeader).append("&replyPrefixHttpHeader=X-TestReply-&").
			append("returnHttpHeader=X-TestHeaderResponse:").append(responseValue).append("&returnHttpHeaderSingleValue=true");
		request.setUrl(sb.toString());
		
		// CASO OK
		
		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());
			
		String paramRisposta = "X-TestReply-"+returnHeader;
		List<String> list = response.getHeaderValues(paramRisposta);
		assertEquals(true, (list!=null && !list.isEmpty()));
		assertEquals(1, list.size());
		assertEquals(true, list.contains(requestValue));
			
		list = response.getHeaderValues("X-TestHeaderResponse");
		assertEquals(true, (list!=null && !list.isEmpty()));
		assertEquals(1, list.size());
		assertEquals(true, list.contains(responseValue));
		
		// Caso KO
		
		request.getHeadersValues().remove(returnHeader);
		request.addHeader(returnHeader+"ERROR", requestValue);
		response = HttpUtilities.httpInvoke(request);
		OpenAPI30_Utils.verifyKo(response);
		
		
	}

}
