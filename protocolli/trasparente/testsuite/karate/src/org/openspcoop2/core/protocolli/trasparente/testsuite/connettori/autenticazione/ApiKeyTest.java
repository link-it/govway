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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.autenticazione;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* ApiKeyTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ApiKeyTest extends ConfigLoader {

	private static final String API = "TestApiKey";
	
	
	@Test
	public void erogazioneApikey() throws Exception {
		apikey(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneApikey() throws Exception {
		apikey(TipoServizio.FRUIZIONE);
	}
	private void apikey(TipoServizio tipoServizio) throws Exception {
		Map<String, String> headerHTTPAttesi = new HashMap<>();
		headerHTTPAttesi.put("GovWay-TestSuite-X-API-KEY", "TESTVALORE");
		test(tipoServizio,
				API, "apikey",
				headerHTTPAttesi);
	}
	
	
	
	
	@Test
	public void erogazioneApikeyCustom() throws Exception {
		apikeyCustom(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneApikeyCustom() throws Exception {
		apikeyCustom(TipoServizio.FRUIZIONE);
	}
	private void apikeyCustom(TipoServizio tipoServizio) throws Exception {
		Map<String, String> headerHTTPAttesi = new HashMap<>();
		headerHTTPAttesi.put("GovWay-TestSuite-X-API-KEY-CUSTOM", "TESTVALORECUSTOM");
		test(tipoServizio,
				API, "apikey_custom",
				headerHTTPAttesi);
	}
	
	
	
	
	@Test
	public void erogazioneAppid() throws Exception {
		appid(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneAppid() throws Exception {
		appid(TipoServizio.FRUIZIONE);
	}
	private void appid(TipoServizio tipoServizio) throws Exception {
		Map<String, String> headerHTTPAttesi = new HashMap<>();
		headerHTTPAttesi.put("GovWay-TestSuite-X-API-KEY", "TESTVALORE");
		headerHTTPAttesi.put("GovWay-TestSuite-X-APP-ID", "APPIDENTITY");
		test(tipoServizio,
				API, "appid",
				headerHTTPAttesi);
	}
	
	
	
	
	@Test
	public void erogazioneAppidCustom() throws Exception {
		appidCustom(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizioneAppidCustom() throws Exception {
		appidCustom(TipoServizio.FRUIZIONE);
	}
	private void appidCustom(TipoServizio tipoServizio) throws Exception {
		Map<String, String> headerHTTPAttesi = new HashMap<>();
		headerHTTPAttesi.put("GovWay-TestSuite-X-API-KEY-CUSTOM", "TESTVALORECUSTOM");
		headerHTTPAttesi.put("GovWay-TestSuite-X-APP-ID-CUSTOM", "APPIDENTITYCUSTOM");
		test(tipoServizio,
				API, "appid_custom",
				headerHTTPAttesi);
	}
	
	
	
	public static void test(
			TipoServizio tipoServizio, String api, String operazione, 
			Map<String, String> headerHTTPAttesi) throws Exception {
		
		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
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
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		verifyOk(response, 200); // il codice http e' gia' stato impostato
		DBVerifier.verify(idTransazione, esitoExpected, null);
		
		if(!headerHTTPAttesi.isEmpty()) {
			for (Map.Entry<String,String> entry : headerHTTPAttesi.entrySet()) {
				String entryValue = response.getHeaderFirstValue(entry.getKey());
				assertNotNull(entryValue);
				assertEquals(entry.getValue(), entryValue);
			} 
		}
	}
	
	public static void verifyOk(HttpResponse response, int code) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_JSON, response.getContentType());
		
	}
}
