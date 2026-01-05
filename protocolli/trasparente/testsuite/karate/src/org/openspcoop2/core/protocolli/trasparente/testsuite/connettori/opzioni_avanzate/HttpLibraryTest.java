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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibrary;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* HttpLibraryTest: classe per i test sul controllo delle librerie http usate dai connettori
* nel caso di configurazione avanzata da console.
* @author Tommaso Burlon (tommaso.burlon@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class HttpLibraryTest extends ConfigLoader {
	
	private static final String HTTPCORE_LIBRARY = "core";
	private static final String HTTPCONNECTION_LIBRARY = "UrlConn";
	
	private static final String API = "TestHttpLibrary";
	
	@Test
	public void testHttpCoreNIO() throws SQLQueryObjectException, ProtocolException, UtilsException {
		HttpLibraryMode mode = new HttpLibraryMode(HttpLibrary.HTTPCORE, true, false);
		String id = test(mode);
		checkLibrary(id, mode);
	}
	
	@Test
	public void testHttpsCoreNIO() throws SQLQueryObjectException, ProtocolException, UtilsException {
		HttpLibraryMode mode = new HttpLibraryMode(HttpLibrary.HTTPCORE, true, true);
		String id = test(mode);
		checkLibrary(id, mode);
	}
	
	@Test
	public void testHttpCoreBIO() throws SQLQueryObjectException, ProtocolException, UtilsException {
		HttpLibraryMode mode = new HttpLibraryMode(HttpLibrary.HTTPCORE, false, false);
		String id = test(mode);
		checkLibrary(id, mode);
	}
	
	@Test
	public void testHttpsCoreBIO() throws SQLQueryObjectException, ProtocolException, UtilsException {
		HttpLibraryMode mode = new HttpLibraryMode(HttpLibrary.HTTPCORE, false, true);
		String id = test(mode);
		checkLibrary(id, mode);
	}
	
	@Test
	public void testHttpUrlConnBIO() throws SQLQueryObjectException, ProtocolException, UtilsException {
		HttpLibraryMode mode = new HttpLibraryMode(HttpLibrary.URLCONNECTION, false, false);
		String id = test(mode);
		checkLibrary(id, mode);
	}
	
	@Test
	public void testHttpsUrlConnBIO() throws SQLQueryObjectException, ProtocolException, UtilsException {
		HttpLibraryMode mode = new HttpLibraryMode(HttpLibrary.URLCONNECTION, false, true);
		String id = test(mode);
		checkLibrary(id, mode);
	}
	
	@Test
	public void testHttpUrlConnNIO() throws SQLQueryObjectException, ProtocolException, UtilsException {
		HttpLibraryMode mode = new HttpLibraryMode(HttpLibrary.URLCONNECTION, true, false);
		String id = test(mode);
		checkLibrary(id, new HttpLibraryMode(HttpLibrary.HTTPCORE, true, false));
	}
	
	@Test
	public void testHttpsUrlConnNIO() throws SQLQueryObjectException, ProtocolException, UtilsException {
		HttpLibraryMode mode = new HttpLibraryMode(HttpLibrary.URLCONNECTION, true, true);
		String id = test(mode);
		checkLibrary(id, new HttpLibraryMode(HttpLibrary.HTTPCORE, true, true));
	}
	
	private String test(HttpLibraryMode mode) throws UtilsException  {
		String libName = null;
		
		if (mode.getLibrary().equals(HttpLibrary.HTTPCORE)) {
			libName = HTTPCORE_LIBRARY;
		} else if (mode.getLibrary().equals(HttpLibrary.URLCONNECTION)) {
			libName = HTTPCONNECTION_LIBRARY;
		}
		
		String operazione = (mode.isHttps() ? "https" : "http") + libName;
		String url = new StringBuilder()
				.append(System.getProperty("govway_base_path"))
				.append(mode.isNIO() ? "/in/async" : "")
				.append("/").append("SoggettoInternoTest")
				.append("/").append(API)
				.append("/").append("v1")
				.append("/").append(operazione)
				.toString();

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
				
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
				
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		request.setUrl(url);
		
		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());
		
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
				
		return idTransazione;
	}
	
	private void checkLibrary(String idTransazione, HttpLibraryMode mode) throws SQLQueryObjectException, ProtocolException {
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		DBVerifier.verify(idTransazione, esitoExpected, mode);
	}
}
