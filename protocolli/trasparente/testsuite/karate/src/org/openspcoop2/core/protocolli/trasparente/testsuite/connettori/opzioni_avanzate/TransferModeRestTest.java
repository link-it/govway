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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
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
public class TransferModeRestTest extends ConfigLoader {
	
	private static final String API = "TestDataTransferRest";
	
	private static final String MODE_DEFAULT = "default";
	private static final String MODE_TRANSFER_ENCODING_CHUNKED = "transfer";
	private static final String MODE_CONTENT_LENGTH = "length";
	
	private static final String CONFIG_STREAMING = "streaming";
	private static final String CONFIG_DEBUG_CONNETTORI_LOG = "debugConnettoriLog";
	private static final String CONFIG_REGISTRAZIONE_MESSAGGIO = "registrazioneMessaggio";
	private static final String CONFIG_CONTENT_BUILD = "contentBuild";
	
	@Test
	public void defaultSmallStreaming() throws Exception {
		test(MODE_DEFAULT,CONFIG_STREAMING,Bodies.SMALL_SIZE);
	}
	@Test
	public void defaultBigStreaming() throws Exception {
		test(MODE_DEFAULT,CONFIG_STREAMING,Bodies.BIG_SIZE);
	}
	
	@Test
	public void lengthSmallStreaming() throws Exception {
		test(MODE_CONTENT_LENGTH,CONFIG_STREAMING,Bodies.SMALL_SIZE);
	}
	@Test
	public void lengthBigStreaming() throws Exception {
		test(MODE_CONTENT_LENGTH,CONFIG_STREAMING,Bodies.BIG_SIZE);
	}
	
	@Test
	public void transferSmallStreaming() throws Exception {
		test(MODE_TRANSFER_ENCODING_CHUNKED,CONFIG_STREAMING,Bodies.SMALL_SIZE);
	}
	@Test
	public void transferBigStreaming() throws Exception {
		test(MODE_TRANSFER_ENCODING_CHUNKED,CONFIG_STREAMING,Bodies.BIG_SIZE);
	}
	
	
	
	@Test
	public void defaultSmallDebugConnettoriLog() throws Exception {
		test(MODE_DEFAULT,CONFIG_DEBUG_CONNETTORI_LOG,Bodies.SMALL_SIZE);
	}
	@Test
	public void defaultBigDebugConnettoriLog() throws Exception {
		test(MODE_DEFAULT,CONFIG_DEBUG_CONNETTORI_LOG,Bodies.BIG_SIZE);
	}
	
	@Test
	public void lengthSmallDebugConnettoriLog() throws Exception {
		test(MODE_CONTENT_LENGTH,CONFIG_DEBUG_CONNETTORI_LOG,Bodies.SMALL_SIZE);
	}
	@Test
	public void lengthBigDebugConnettoriLog() throws Exception {
		test(MODE_CONTENT_LENGTH,CONFIG_DEBUG_CONNETTORI_LOG,Bodies.BIG_SIZE);
	}
	
	@Test
	public void transferSmallDebugConnettoriLog() throws Exception {
		test(MODE_TRANSFER_ENCODING_CHUNKED,CONFIG_DEBUG_CONNETTORI_LOG,Bodies.SMALL_SIZE);
	}
	@Test
	public void transferBigDebugConnettoriLog() throws Exception {
		test(MODE_TRANSFER_ENCODING_CHUNKED,CONFIG_DEBUG_CONNETTORI_LOG,Bodies.BIG_SIZE);
	}
	
	
	
	
	
	@Test
	public void defaultSmallRegistrazioneMessaggio() throws Exception {
		test(MODE_DEFAULT,CONFIG_REGISTRAZIONE_MESSAGGIO,Bodies.SMALL_SIZE);
	}
	@Test
	public void defaultBigRegistrazioneMessaggio() throws Exception {
		test(MODE_DEFAULT,CONFIG_REGISTRAZIONE_MESSAGGIO,Bodies.BIG_SIZE);
	}
	
	@Test
	public void lengthSmallRegistrazioneMessaggio() throws Exception {
		test(MODE_CONTENT_LENGTH,CONFIG_REGISTRAZIONE_MESSAGGIO,Bodies.SMALL_SIZE);
	}
	@Test
	public void lengthBigRegistrazioneMessaggio() throws Exception {
		test(MODE_CONTENT_LENGTH,CONFIG_REGISTRAZIONE_MESSAGGIO,Bodies.BIG_SIZE);
	}
	
	@Test
	public void transferSmallRegistrazioneMessaggio() throws Exception {
		test(MODE_TRANSFER_ENCODING_CHUNKED,CONFIG_REGISTRAZIONE_MESSAGGIO,Bodies.SMALL_SIZE);
	}
	@Test
	public void transferBigRegistrazioneMessaggio() throws Exception {
		test(MODE_TRANSFER_ENCODING_CHUNKED,CONFIG_REGISTRAZIONE_MESSAGGIO,Bodies.BIG_SIZE);
	}
	
	
	
	
	
	@Test
	public void defaultSmallContentBuild() throws Exception {
		test(MODE_DEFAULT,CONFIG_CONTENT_BUILD,Bodies.SMALL_SIZE);
	}
	@Test
	public void defaultBigContentBuild() throws Exception {
		test(MODE_DEFAULT,CONFIG_CONTENT_BUILD,Bodies.BIG_SIZE);
	}
	
	@Test
	public void lengthSmallContentBuild() throws Exception {
		test(MODE_CONTENT_LENGTH,CONFIG_CONTENT_BUILD,Bodies.SMALL_SIZE);
	}
	@Test
	public void lengthBigContentBuild() throws Exception {
		test(MODE_CONTENT_LENGTH,CONFIG_CONTENT_BUILD,Bodies.BIG_SIZE);
	}
	
	@Test
	public void transferSmallContentBuild() throws Exception {
		test(MODE_TRANSFER_ENCODING_CHUNKED,CONFIG_CONTENT_BUILD,Bodies.SMALL_SIZE);
	}
	@Test
	public void transferBigContentBuild() throws Exception {
		test(MODE_TRANSFER_ENCODING_CHUNKED,CONFIG_CONTENT_BUILD,Bodies.BIG_SIZE);
	}
	
	
	
	
	
	
	private static HttpResponse test(
			String mode, String config, int size) throws Exception {
		
		String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+API+"/v1/"+mode+"."+config;
		
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
				
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(size).getBytes();
		
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		boolean expectedTransferMode = MODE_TRANSFER_ENCODING_CHUNKED.equals(mode) || 
				MODE_DEFAULT.equals(mode);
		
		if(expectedTransferMode) {
			request.addHeader("check-not-exists-header", HttpConstants.CONTENT_LENGTH);
			request.addHeader("check-exists-header", HttpConstants.TRANSFER_ENCODING);
		}
		else {
			request.addHeader("check-not-exists-header", HttpConstants.TRANSFER_ENCODING);
			request.addHeader("check-exists-header", HttpConstants.CONTENT_LENGTH);
		}
		
		request.setUrl(url);
		
		HttpResponse response = HttpUtilities.httpInvoke(request);

		assertEquals(200, response.getResultHTTPOperation());
		assertEquals(contentType, response.getContentType());
		
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);

		DBVerifier.verify(idTransazione, esitoExpected, null);
		
		if(expectedTransferMode) {
			String hdrTest = response.getHeaderFirstValue("GovWay-TestSuite-"+HttpConstants.TRANSFER_ENCODING);
			assertNotNull(hdrTest);
			assertEquals(hdrTest, HttpConstants.TRANSFER_ENCODING_VALUE_CHUNCKED);
		}
		else {
			String hdrTest = response.getHeaderFirstValue("GovWay-TestSuite-"+HttpConstants.CONTENT_LENGTH);
			int value = Integer.parseInt(hdrTest);
			
			int atteso = content.length;
			if(CONFIG_CONTENT_BUILD.equals(config)) {
				// aggiunto "addElement" : "valoreEsempioAggiunto"
				String elemento = "\"addElement\":\"valoreEsempioAggiunto\"";
				String r = new String(response.getContent());
				if(!r.contains(elemento)) {
					System.out.println("ATTESO: ["+new String(content)+"]");
					System.out.println("RICEVUTO: ["+r+"]");
				}
				assertTrue("Ricevuto '"+value+"' > 0", value>0);
				assertTrue("Atteso elemento aggiuntivo", r.contains(elemento));
			}
			else {
				assertEquals(value, atteso);
			}
		}
		
		return response;
		
	}
	
	
}
