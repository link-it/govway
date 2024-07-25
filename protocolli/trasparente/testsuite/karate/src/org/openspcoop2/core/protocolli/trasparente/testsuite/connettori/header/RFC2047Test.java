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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.RFC2047Utilities;

/**
* TestConnettoriRFC2047
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RFC2047Test extends ConfigLoader {

	private static final String API = "TestConnettoriRFC2047";
	
	private static final String API_OP_DEFAULT = "default";
	private static final String API_OP_DISABLE = "disable";
	private static final String API_OP_REQUEST_ONLY = "requestOnly";
	private static final String API_OP_RESPONSE_ONLY = "responseOnly";
	private static final String API_OP_CHARSET = "charset";
	private static final String API_OP_ENCODING = "encoding";

	
	@Test
	public void erogazioneDefault() throws Exception {
		test(TipoServizio.EROGAZIONE, API, API_OP_DEFAULT);
	}
	@Test
	public void fruizioneDefault() throws Exception {
		test(TipoServizio.FRUIZIONE, API, API_OP_DEFAULT);
	}
	
	@Test
	public void erogazioneDisable() throws Exception {
		test(TipoServizio.EROGAZIONE, API, API_OP_DISABLE);
	}
	@Test
	public void fruizioneDisable() throws Exception {
		test(TipoServizio.FRUIZIONE, API, API_OP_DISABLE);
	}
	
	
	@Test
	public void erogazioneRequestOnly() throws Exception {
		test(TipoServizio.EROGAZIONE, API, API_OP_REQUEST_ONLY);
	}
	@Test
	public void fruizioneRequestOnly() throws Exception {
		test(TipoServizio.FRUIZIONE, API, API_OP_REQUEST_ONLY);
	}
	
	
	@Test
	public void erogazioneResponseOnly() throws Exception {
		test(TipoServizio.EROGAZIONE, API, API_OP_RESPONSE_ONLY);
	}
	@Test
	public void fruizioneResponseOnly() throws Exception {
		test(TipoServizio.FRUIZIONE, API, API_OP_RESPONSE_ONLY);
	}
	
	
	
	@Test
	public void erogazioneCharset() throws Exception {
		test(TipoServizio.EROGAZIONE, API, API_OP_CHARSET);
	}
	@Test
	public void fruizioneCharset() throws Exception {
		test(TipoServizio.FRUIZIONE, API, API_OP_CHARSET);
	}
	
	
	
	@Test
	public void erogazioneEncoding() throws Exception {
		test(TipoServizio.EROGAZIONE, API, API_OP_ENCODING);
	}
	@Test
	public void fruizioneEncoding() throws Exception {
		test(TipoServizio.FRUIZIONE, API, API_OP_ENCODING);
	}
	
	
	public static void test(
			TipoServizio tipoServizio, String api, String operazione) throws Exception {
		
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
		
		String valoreRichiesta = "Non è possibile inviare un header con/questi caratteri nella richiesta";
		String valoreRisposta = "Non è possibile inviare un header con/questi caratteri nella risposta";
		
		request.addHeader("testsuite-rfc2047", valoreRichiesta);
		
		request.setUrl(url);
		
		HttpResponse response = HttpUtilities.httpInvoke(request);

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		verifyOk(response, 200); // il codice http e' gia' stato impostato
		DBVerifier.verify(idTransazione, esitoExpected, null);
		
		String usAsciiQ = "=?US-ASCII?Q?";
		String usAsciiQtest2 = "?= =?US-ASCII?Q?";
		
		String entryRequestValue = response.getHeaderFirstValue("govway-testsuite-request-testsuite-rfc2047");
		assertNotNull(entryRequestValue);
		String msg = "Richiesta [govway-testsuite-request-testsuite-rfc2047] Ricevuto ["+entryRequestValue+"]";
		String parte1 = "possibile_inviare_un_header";
		String parte2 = "con/questi_caratteri_nella";
		String parteFinale = "possibile inviare un header con/questi caratteri nella richiesta";
		String decodeBase64 = new String(Base64Utilities.decode(entryRequestValue));
		msg = msg + " decodificato ["+decodeBase64+"]";
		getLoggerCore().info(msg);
		if(API_OP_DEFAULT.equals(operazione) || 
				API_OP_REQUEST_ONLY.equals(operazione)) {
			assertTrue(msg, decodeBase64.startsWith(usAsciiQ));
			assertTrue(msg, decodeBase64.contains(usAsciiQtest2));
			assertTrue(msg, decodeBase64.endsWith("?="));
			assertTrue(msg, decodeBase64.contains(parte1));
			assertTrue(msg, decodeBase64.contains(parte2));
		}
		else if(API_OP_ENCODING.equals(operazione)) {
			assertTrue(msg, decodeBase64.startsWith("=?US-ASCII?B?"));
			assertTrue(msg, decodeBase64.contains("?= =?US-ASCII?B?"));
			assertTrue(msg, decodeBase64.endsWith("?="));
			assertFalse(msg, decodeBase64.contains(parte1)); // codificate in base64
			assertFalse(msg, decodeBase64.contains(parte2)); // codificate in base64
		}
		else if(API_OP_DISABLE.equals(operazione) || 
				API_OP_RESPONSE_ONLY.equals(operazione) ||
				API_OP_CHARSET.equals(operazione) // tutti i caratteri usati rientrano nel charset indicato (ISO-8859-1) e quindi non viene fatta la codifica
				) {
			assertFalse(msg, decodeBase64.startsWith(usAsciiQ));
			assertFalse(msg, decodeBase64.contains(usAsciiQtest2));
			assertFalse(msg, decodeBase64.contains("?="));
			assertFalse(msg, decodeBase64.endsWith("?="));
			assertTrue(msg, decodeBase64.startsWith("Non"));
			assertTrue(msg, decodeBase64.endsWith(parteFinale));
		}

		String entryResponseValue = response.getHeaderFirstValue("testsuite-rfc2047-response");
		assertNotNull(entryResponseValue);
		msg = "Risposta [testsuite-rfc2047-response] Ricevuto ["+entryResponseValue+"]";
		getLoggerCore().info(msg);
		if(API_OP_DEFAULT.equals(operazione) || API_OP_RESPONSE_ONLY.equals(operazione)) {
			assertTrue(msg, entryResponseValue.startsWith(usAsciiQ));
			assertTrue(msg, entryResponseValue.contains(usAsciiQtest2));
			assertTrue(msg, entryResponseValue.endsWith("?="));
			String decode = RFC2047Utilities.decode(entryResponseValue);
			assertEquals(valoreRisposta, decode);
		}
		else if(API_OP_ENCODING.equals(operazione)) {
			assertTrue(msg, entryResponseValue.startsWith("=?US-ASCII?B?"));
			assertTrue(msg, entryResponseValue.contains("?= =?US-ASCII?B?"));
			assertTrue(msg, entryResponseValue.endsWith("?="));
			String decode = RFC2047Utilities.decode(entryResponseValue);
			assertEquals(valoreRisposta, decode);
		}
		else if(API_OP_DISABLE.equals(operazione) || 
				API_OP_REQUEST_ONLY.equals(operazione) ||
				API_OP_CHARSET.equals(operazione) // tutti i caratteri usati rientrano nel charset indicato (UTF-8 per la richiesta ISO-8859-1 per la risposta) e quindi non viene fatta la codifica
				) {
			assertEquals(valoreRisposta, entryResponseValue);
		}

	}
	public static void verifyOk(HttpResponse response, int code) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_JSON, response.getContentType());
		
	}
}
