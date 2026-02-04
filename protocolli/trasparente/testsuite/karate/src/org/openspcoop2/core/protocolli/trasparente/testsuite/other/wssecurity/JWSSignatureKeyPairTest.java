/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity;

import static org.junit.Assert.assertNotNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.ocsp.DBVerifier;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* JWSSignatureKeyPairTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JWSSignatureKeyPairTest extends ConfigLoader {

	private static final String API = "TestJWSKeyPair";

	

	@Test
	public void rsa() throws Exception {
		test("rsa");
	}
	@Test
	public void ec() throws Exception {
		test("ec");
	}

	
	private static HttpResponse test(String operazione) throws Exception {
		
		String soggetto = "SoggettoInternoTestFruitore";
		HttpRequestMethod method = HttpRequestMethod.POST;
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte [] content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		
		String url = System.getProperty("govway_base_path") + "/out/"+soggetto+"/SoggettoInternoTest/"+API+"/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		
		request.setReadTimeout(20000);
		request.setMethod(method);
		request.setContentType(contentType);
		request.setContent(content);
		request.setUrl(url);
		
		HttpResponse response = null;
		try {
			/**System.out.println("INVOKE ["+request.getUrl()+"]");*/
			response = HttpUtilities.httpInvoke(request);
		}catch(Exception t) {
			throw t;
		}

		String idTransazioneHeader = "GovWay-Transaction-ID";
		String idTransazione = response.getHeaderFirstValue(idTransazioneHeader);
		assertNotNull(idTransazione);
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.verifyOk(idTransazione, response, 200, contentType);
		
		DBVerifier.verify(idTransazione, esitoExpected);
		
		return response;
		
	}
}