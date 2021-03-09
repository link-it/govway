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


package org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.parametri;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.TransportUtils;
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
public class OpenAPI30_QueryParameterSerializationTest extends ConfigLoader {
	
	// https://swagger.io/docs/specification/serialization/
	
	@Test
	public void erogazione_array_form_explode_true() throws Exception {
		_array_form_explode_true(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_array_form_explode_true() throws Exception {
		_array_form_explode_true(TipoServizio.FRUIZIONE);
	}
	private void _array_form_explode_true(TipoServizio tipo) throws Exception {
		List<String> l = testParametro(tipo, "array_form_explode_true", "param=a3&param=bb4&param=cc5");
		//System.out.println("Header ritornato ["+l+"]");
		assertEquals(3, l.size());
		assertEquals(true, l.contains("a3"));
		assertEquals(true, l.contains("bb4"));
		assertEquals(true, l.contains("cc5"));
	}
	

	@Test
	public void erogazione_array_form_explode_false() throws Exception {
		_array_form_explode_false(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_array_form_explode_false() throws Exception {
		_array_form_explode_false(TipoServizio.FRUIZIONE);
	}
	private void _array_form_explode_false(TipoServizio tipo) throws Exception {
		List<String> l = testParametro(tipo, "array_form_explode_false", "param=a3,bb4,cc5");
		//System.out.println("Header ritornato ["+l+"]");
		assertEquals(1, l.size());
		assertEquals(true, l.contains("a3,bb4,cc5"));
	}
	
	
	// TODO: modifica al codice
//	@Test
//	public void erogazione_object_form_explode_true() throws Exception {
//		_object_form_explode_true(TipoServizio.EROGAZIONE);
//	}
//	@Test
//	public void fruizione_object_form_explode_true() throws Exception {
//		_object_form_explode_true(TipoServizio.FRUIZIONE);
//	}
//	private void _object_form_explode_true(TipoServizio tipo) throws Exception {
//		List<String> l = testParametro(tipo, "object_form_explode_true", "role=admin&firstName=Alex");
//		//System.out.println("Header ritornato ["+l+"]");
//		assertEquals(1, l.size());
//		assertEquals(true, l.contains("admin"));
//	}
	
	@Test
	public void erogazione_object_form_explode_false() throws Exception {
		_object_form_explode_false(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_object_form_explode_false() throws Exception {
		_object_form_explode_false(TipoServizio.FRUIZIONE);
	}
	private void _object_form_explode_false(TipoServizio tipo) throws Exception {
		List<String> l = testParametro(tipo, "object_form_explode_false", "param=role,admin,firstName,Alex");
		//System.out.println("Header ritornato ["+l+"]");
		assertEquals(1, l.size());
		assertEquals(true, l.contains("role,admin,firstName,Alex"));
	}
	
	@Test
	public void erogazione_array_spaceDelimited_explode_true() throws Exception {
		_array_spaceDelimited_explode_true(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_array_spaceDelimited_explode_true() throws Exception {
		_array_spaceDelimited_explode_true(TipoServizio.FRUIZIONE);
	}
	private void _array_spaceDelimited_explode_true(TipoServizio tipo) throws Exception {
		List<String> l = testParametro(tipo, "array_spaceDelimited_explode_true", "param=a3&param=bb4&param=cc5");
		//System.out.println("Header ritornato ["+l+"]");
		assertEquals(3, l.size());
		assertEquals(true, l.contains("a3"));
		assertEquals(true, l.contains("bb4"));
		assertEquals(true, l.contains("cc5"));
	}
		
	@Test
	public void erogazione_array_spaceDelimited_explode_false() throws Exception {
		_array_spaceDelimited_explode_false(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_array_spaceDelimited_explode_false() throws Exception {
		_array_spaceDelimited_explode_false(TipoServizio.FRUIZIONE);
	}
	private void _array_spaceDelimited_explode_false(TipoServizio tipo) throws Exception {
		List<String> l = testParametro(tipo, "array_spaceDelimited_explode_false", "param=a3%20bb4%20cc5");
		//System.out.println("Header ritornato ["+l+"]");
		assertEquals(1, l.size());
		assertEquals(true, l.contains("a3 bb4 cc5"));
	}
	
	@Test
	public void erogazione_array_pipeDelimited_explode_true() throws Exception {
		_array_pipeDelimited_explode_true(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_array_pipeDelimited_explode_true() throws Exception {
		_array_pipeDelimited_explode_true(TipoServizio.FRUIZIONE);
	}
	private void _array_pipeDelimited_explode_true(TipoServizio tipo) throws Exception {
		List<String> l = testParametro(tipo, "array_pipeDelimited_explode_true", "param=a3&param=bb4&param=cc5");
		//System.out.println("Header ritornato ["+l+"]");
		assertEquals(3, l.size());
		assertEquals(true, l.contains("a3"));
		assertEquals(true, l.contains("bb4"));
		assertEquals(true, l.contains("cc5"));
	}
	
	@Test
	public void erogazione_array_pipeDelimited_explode_false() throws Exception {
		_array_pipeDelimited_explode_false(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_array_pipeDelimited_explode_false() throws Exception {
		_array_pipeDelimited_explode_false(TipoServizio.FRUIZIONE);
	}
	private void _array_pipeDelimited_explode_false(TipoServizio tipo) throws Exception {
		List<String> l = testParametro(tipo, "array_pideDelimited_explode_false", "param="+ TransportUtils.urlEncodeParam("a3|bb4|cc5",Charset.UTF_8.getValue()));
		//System.out.println("Header ritornato ["+l+"]");
		assertEquals(1, l.size());
		assertEquals(true, l.contains("a3|bb4|cc5"));
	}

	// TODO: modifica al codice
//	@Test
//	public void erogazione_object_deepObject() throws Exception {
//		_object_deepObject(TipoServizio.EROGAZIONE);
//	}
//	@Test
//	public void fruizione_object_deepObject() throws Exception {
//		_object_deepObject(TipoServizio.FRUIZIONE);
//	}
//	private void _object_deepObject(TipoServizio tipo) throws Exception {
//		List<String> l = testParametro(tipo, "object_deepObject", "param[role]=admin&param[firstName]=Alex");
//		//System.out.println("Header ritornato ["+l+"]");
//		assertEquals(3, l.size());
//		assertEquals(true, l.contains("a3"));
//		assertEquals(true, l.contains("bb4"));
//		assertEquals(true, l.contains("cc5"));
//	}
	
	
	
	static List<String> testParametro(TipoServizio tipoServizio, String path, String value) throws Exception {
		

		final String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/ParameterSerialization/v1/query/"+path
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/ParameterSerialization/v1/query/"+path;
		
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		
		
		// CASO KO
		
		request.setUrl(url); // senza parametro
		HttpResponse response = HttpUtilities.httpInvoke(request);
		OpenAPI30_Utils.verifyKo(response);
		
		
		// CASO OK
		
		StringBuilder sb = new StringBuilder();
		String returnHeader = "param";
		if(path.equals("object_form_explode_true")) {
			returnHeader = "role";
		}
		sb.append(url).append("?replyQueryParameter=").append(returnHeader).append("&replyPrefixQueryParameter=X-Test-&").append(value);
		request.setUrl(sb.toString());
		
		response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());
			
		String paramRisposta = "X-Test-"+returnHeader;
		List<String> list = response.getHeaderValues(paramRisposta);
		assertEquals(true, (list!=null && !list.isEmpty()));
		return list;
		
	}

}
