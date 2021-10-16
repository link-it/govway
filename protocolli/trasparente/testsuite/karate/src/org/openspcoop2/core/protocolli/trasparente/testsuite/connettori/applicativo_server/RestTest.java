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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.applicativo_server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.date.DateManager;
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

	// normale: ApplicativoServer1
	@Test
	public void erogazione_ApplicativoServer1() throws Exception {
		_test("TestApplicativoServer1","test",
				"ApplicativoServer1");
	}
	
	// normale: ApplicativoServer2
	@Test
	public void erogazione_ApplicativoServer2() throws Exception {
		_test("TestApplicativoServer2","test",
				"ApplicativoServer2");
	}
	
	// loadBalancer
	@Test
	public void erogazione_loadBalancer() throws Exception {
		_test("TestApplicativoServerLoadBalancer","test",
				"ApplicativoServer1", "ApplicativoServer2", "ApplicativoServer1", "ApplicativoServer2");
	}

	// gruppo: ApplicativoServer1
	@Test
	public void erogazione_gruppi_ApplicativoServer1() throws Exception {
		_test("TestApplicativoServerGruppi","applicativoserver1",
				"ApplicativoServer1");
	}
	
	// gruppo: ApplicativoServer2
	@Test
	public void erogazione_gruppi_ApplicativoServer2() throws Exception {
		_test("TestApplicativoServerGruppi","applicativoserver2",
				"ApplicativoServer2");
	}
	
	// gruppo: loadBalancer
	@Test
	public void erogazione_gruppi_loadBalancer() throws Exception {
		_test("TestApplicativoServerGruppi","loadbalancer",
				"ApplicativoServer1", "ApplicativoServer2", "ApplicativoServer1", "ApplicativoServer2");
	}
	
	
	private static void _test(
			String api, String operazione, 
			String ... applicativeIds) throws Exception {

		for (String applicativeId : applicativeIds) {
		
			Date now = DateManager.getDate();
	
			String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione;
			
			String diagnostico = "tipo="+applicativeId;
			
			HttpRequest request = new HttpRequest();
			
			request.setReadTimeout(20000);
	
			String contentType= HttpConstants.CONTENT_TYPE_JSON;
			byte[]content=Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
			
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
			
			long esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
			verifyOk(response, 200); // il codice http e' gia' stato impostato
			DBVerifier.verify(idTransazione, esitoExpected, diagnostico);
			
			String idByCheckApplicativo = DBVerifier.getIdTransazioneByIdApplicativoRisposta(applicativeId, now);
			assertNotNull(idByCheckApplicativo);
			assertEquals("Atteso id["+idTransazione+"] = idByApp["+idByCheckApplicativo+"]", idTransazione, idByCheckApplicativo);
		
		}
		
	}
	
	public static void verifyOk(HttpResponse response, int code) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_JSON, response.getContentType());
		
	}
}
