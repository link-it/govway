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
package org.openspcoop2.core.protocolli.trasparente.testsuite.trasformazione.info_integrazione;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

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
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {

	@Test
	public void erogazione_correlazioneApplicativaRequest() throws Exception {
		_test(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_correlazioneApplicativaRequest() throws Exception {
		_test(TipoServizio.FRUIZIONE);
	}
		
	private static HttpResponse _test(TipoServizio tipoServizio) throws Exception {
		

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/TestTrasformazioneInfoIntegrazione/v1/test"
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestTrasformazioneInfoIntegrazione/v1/test";

		HttpRequest request = new HttpRequest();
		
		String idCorrelazioneTest = UUID.randomUUID().toString();
		request.addHeader("x-id-correlazione-test",idCorrelazioneTest);
		
		String contentTypeRequest = HttpConstants.CONTENT_TYPE_JSON;
						
		request.setReadTimeout(20000);
						
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentTypeRequest);
		
		request.setContent(Bodies.getJson(Bodies.SMALL_SIZE).getBytes());
		
		request.setUrl(url);
		
		HttpResponse response = HttpUtilities.httpInvoke(request);

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		assertEquals(200, response.getResultHTTPOperation());
		assertEquals(contentTypeRequest, response.getContentType());
		
		String responseIntegrazioneInfo = response.getHeaderFirstValue("x-test-trasformazione-correlazione");
		assertNotNull("check responseIntegrazioneInfo per idTransazione not null: "+idTransazione, responseIntegrazioneInfo);
		assertEquals("check responseIntegrazioneInfo per idTransazione: "+idTransazione,responseIntegrazioneInfo,idCorrelazioneTest);
		
		return response;
		
	}
	
}
