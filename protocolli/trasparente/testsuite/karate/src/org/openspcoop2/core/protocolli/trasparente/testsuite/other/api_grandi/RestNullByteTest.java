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
package org.openspcoop2.core.protocolli.trasparente.testsuite.other.api_grandi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* RestNullByteTest
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestNullByteTest extends ConfigLoader {

	private static final String ID_DOCUMENTO = "doc123";

	private static final String API_VALIDAZIONE_RICHIESTA = "/SoggettoInternoTest/TestAPIRestPayloadBinarioGrandeConNullByteValidazioneRichiesta/v1";
	private static final String API_VALIDAZIONE_RISPOSTA = "/SoggettoInternoTest/TestAPIRestPayloadBinarioGrandeConNullByteValidazioneRisposta/v1";

	private static final String RISORSA_BINARY = "/documenti/" + ID_DOCUMENTO + "/binary";
	private static final String RISORSA_BASE64 = "/documenti/" + ID_DOCUMENTO;

	// Validazione Richiesta

	@Test
	public void validazione_richiesta_binary() throws Exception {
		_test(API_VALIDAZIONE_RICHIESTA + RISORSA_BINARY, false, false);
	}
	@Test
	public void validazione_richiesta_base64() throws Exception {
		_test(API_VALIDAZIONE_RICHIESTA + RISORSA_BASE64, true, false);
	}
	@Test
	public void validazione_richiesta_binary_inviatosubase64resource() throws Exception {
		_test(API_VALIDAZIONE_RICHIESTA + RISORSA_BASE64, false, true);
	}

	// Validazione Risposta

	@Test
	public void validazione_risposta_binary() throws Exception {
		_test(API_VALIDAZIONE_RISPOSTA + RISORSA_BINARY, false, false);
	}
	@Test
	public void validazione_risposta_base64() throws Exception {
		_test(API_VALIDAZIONE_RISPOSTA + RISORSA_BASE64, true, false);
	}
	@Test
	public void validazione_risposta_binary_inviatosubase64resource() throws Exception {
		_test(API_VALIDAZIONE_RISPOSTA + RISORSA_BASE64, false, true);
	}

	private static void _test(String path, boolean base64Encode, boolean expectedError) throws Exception {

		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);

		byte[] fileContent;
		try (InputStream is = RestNullByteTest.class.getResourceAsStream("file_nullbyte_1MB.pdf")) {
			assertNotNull("File file_nullbyte_1MB.pdf not found in classpath", is);
			fileContent = Utilities.getAsByteArray(is);
		}

		byte[] content;
		if (base64Encode) {
			content = Base64Utilities.encodeAsString(fileContent).getBytes();
		} else {
			content = fileContent;
		}

		String url = System.getProperty("govway_base_path") + path;

		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		request.setContent(content);
		request.setUrl(url);

		
		int result = 200;
		int esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		String errore = null;
		if(expectedError) {
			if(path.startsWith(API_VALIDAZIONE_RISPOSTA)) {
				result = 502;
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_VALIDAZIONE_RISPOSTA);
				errore = "Validazione [interface] del contenuto applicativo della risposta fallita: ";
			}
			else {
				result = 400;
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_VALIDAZIONE_RICHIESTA);
				errore = "Validazione [interface] del contenuto applicativo della richiesta fallita: ";
			}
		}
		
		HttpResponse resp = HttpUtilities.httpInvoke(request);
		
		assertEquals(result, resp.getResultHTTPOperation());

		String idTransazione = resp.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		DBVerifier.verifyEsito(idTransazione, esitoExpected, errore);
		
		if(expectedError) {
			String diag = DBVerifier.existsDiagnostico(idTransazione, errore);
			assertTrue("Verifico lunghezza diagnostico ("+diag.length()+")",(diag.length()<4100));
		}
		else {
			
			if(!path.startsWith(API_VALIDAZIONE_RISPOSTA)) {
				String risposta = "Validazione [interface] del contenuto applicativo della richiesta completata con successo";
				DBVerifier.existsDiagnostico(idTransazione, risposta);
			}
			
			String risposta = "Validazione [interface] del contenuto applicativo della risposta completata con successo";
			DBVerifier.existsDiagnostico(idTransazione, risposta);
			
		}
	}
}
