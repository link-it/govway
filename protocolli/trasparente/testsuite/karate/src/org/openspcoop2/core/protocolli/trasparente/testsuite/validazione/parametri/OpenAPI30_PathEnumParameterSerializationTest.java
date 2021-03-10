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
public class OpenAPI30_PathEnumParameterSerializationTest extends ConfigLoader {
	
	// https://swagger.io/docs/specification/serialization/
	
	@Test
	public void erogazione_OK_array_simple_explode_false() throws Exception {
		_OK_array_simple_explode_false(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_OK_array_simple_explode_false() throws Exception {
		_OK_array_simple_explode_false(TipoServizio.FRUIZIONE);
	}
	private void _OK_array_simple_explode_false(TipoServizio tipo) throws Exception {
		testPath(tipo, "array_simple_explode_false", "Valore1,Valore2,Valore33", true);
	}
	
	@Test
	public void erogazione_OK_array_simple_explode_true() throws Exception {
		_OK_array_simple_explode_true(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_OK_array_simple_explode_true() throws Exception {
		_OK_array_simple_explode_true(TipoServizio.FRUIZIONE);
	}
	private void _OK_array_simple_explode_true(TipoServizio tipo) throws Exception {
		testPath(tipo, "array_simple_explode_true", "Valore1,Valore2,Valore33", true);
	}
		
	
	@Test
	public void erogazione_OK_array_label_explode_false() throws Exception {
		_OK_array_label_explode_false(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_OK_array_label_explode_false() throws Exception {
		_OK_array_label_explode_false(TipoServizio.FRUIZIONE);
	}
	private void _OK_array_label_explode_false(TipoServizio tipo) throws Exception {
		testPath(tipo, "array_label_explode_false", ".Valore1,Valore2,Valore33", true);
	}
	
	@Test
	public void erogazione_OK_array_label_explode_true() throws Exception {
		_OK_array_label_explode_true(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_OK_array_label_explode_true() throws Exception {
		_OK_array_label_explode_true(TipoServizio.FRUIZIONE);
	}
	private void _OK_array_label_explode_true(TipoServizio tipo) throws Exception {
		testPath(tipo, "array_label_explode_true", ".Valore1.Valore2.Valore33", true);
	}
	
	
	
	@Test
	public void erogazione_OK_array_matrix_explode_false() throws Exception {
		_OK_array_matrix_explode_false(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_OK_array_matrix_explode_false() throws Exception {
		_OK_array_matrix_explode_false(TipoServizio.FRUIZIONE);
	}
	private void _OK_array_matrix_explode_false(TipoServizio tipo) throws Exception {
		testPath(tipo, "array_matrix_explode_false", ";param=Valore1,Valore2,Valore33", true);
	}
	
	@Test
	public void erogazione_OK_array_matrix_explode_true() throws Exception {
		_OK_array_matrix_explode_true(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_OK_array_matrix_explode_true() throws Exception {
		_OK_array_matrix_explode_true(TipoServizio.FRUIZIONE);
	}
	private void _OK_array_matrix_explode_true(TipoServizio tipo) throws Exception {
		testPath(tipo, "array_matrix_explode_true", ";param=Valore1;param=Valore2;param=Valore33", true);
	}
	
	
	
	
	
	static void testPath(TipoServizio tipoServizio, String path, String requestValue, boolean expectedOk) throws Exception {
		

		final String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/EnumParameterSerialization/v1/path/"+path+"/"+requestValue
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/EnumParameterSerialization/v1/path/"+path+"/"+requestValue;
		
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		request.setUrl(sb.toString());
		
		HttpResponse response = HttpUtilities.httpInvoke(request);
		
		if(expectedOk) {
			assertEquals(200, response.getResultHTTPOperation());
		}
		else {
			OpenAPI30_Utils.verifyKo(response);
		}
		
	}

}
