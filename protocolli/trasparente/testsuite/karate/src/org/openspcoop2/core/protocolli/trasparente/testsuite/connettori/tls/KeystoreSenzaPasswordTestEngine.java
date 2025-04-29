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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.tls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
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
public class KeystoreSenzaPasswordTestEngine extends ConfigLoader {
	
	private HttpLibraryMode mode = null;
	public KeystoreSenzaPasswordTestEngine(HttpLibraryMode mode) {
		this.mode = mode;
	}
	
	@Test
	public void truststoreJksSenzaPassword() throws Exception {
		test("truststoreJksSenzaPassword", this.mode);
	}
	
	
	@Test
	public void truststorePkcs12SenzaPassword() throws Exception {
		test("truststorePkcs12SenzaPassword", this.mode);
	}
	
	
	@Test
	public void keystoreJksNoPasswordKeyNoPassword() throws Exception {
		test("keystoreJksNoPassword-KeyNoPassword", this.mode);
	}
	
	@Test
	public void keystoreJksNoPasswordKeyWithPassword() throws Exception {
		test("keystoreJksNoPassword-KeyWithPassword", this.mode);
	}
	
	@Test
	public void keystoreJksNoPasswordKeyNoPasswordUsaTrustStore() throws Exception {
		test("keystoreJksNoPassword-KeyNoPassword-usaTrustStore", this.mode);
	}
	
	
	@Test
	public void keystorePkcs12NoPasswordKeyNoPassword() throws Exception {
		test("keystorePkcs12NoPassword-KeyNoPassword", this.mode);
	}

	@Test
	public void keystorePkcs12NoPasswordKeyWithPassword() throws Exception {
		test("keystorePkcs12NoPassword-KeyWithPassword", this.mode);
	}
	
	@Test
	public void keystorePkcs12NoPasswordKeyNoPasswordUsaTrustStore() throws Exception {
		test("keystorePkcs12NoPassword-KeyNoPassword-usaTrustStore", this.mode);
	}

	
	
	private static HttpResponse test(
			String operazione, HttpLibraryMode mode) throws Exception {
		
		String url = System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestSSLKeystoreSenzaPassword/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
				
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		request.setUrl(url);
		
		if (mode != null)
			mode.patchRequest(request);
		HttpResponse response = HttpUtilities.httpInvoke(request);

		assertEquals(200, response.getResultHTTPOperation());
		assertEquals(contentType, response.getContentType());
		
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);

		String msgErrore = null;
		DBVerifier.verify(idTransazione, esitoExpected, msgErrore, mode);
				
		return response;
		
	}
	
	
}
