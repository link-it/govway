/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.identificazione_azione;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* Utilities
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Utilities extends ConfigLoader {
	
	public static HttpResponse test(String contextUrl,
			String operazioneAttesa, String erroreAtteso
			) throws Exception {

		String contentType=null;
		byte[]content=null;
		contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
		content=Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes();
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "test");

		String url = System.getProperty("govway_base_path") + contextUrl;
		
		HttpRequest request = new HttpRequest();
		
		request.setReadTimeout(20000);

		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		request.setUrl(url);
		for (Entry<String, String> entry : headers.entrySet()) {
			request.addHeader(entry.getKey(), entry.getValue());
		}
		
		HttpResponse response = HttpUtilities.httpInvoke(request);

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		int code = 200;
		String error = null;
		String errorMsg = null;
		boolean clientError = false;
		boolean soap11 = true;
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if(erroreAtteso!=null) {
			
			clientError = true;
			
			if(erroreAtteso.contains("non trovata per il servizio")) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OPERAZIONE_NON_INDIVIDUATA);
				code = 404;	
				error = "UndefinedOperation";
				errorMsg = "Operation undefined in the API specification";
			}
		}
		
		if(erroreAtteso!=null) {
			org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.json.Utilities.verifyKo(response, code, error, errorMsg, soap11, clientError, logCore);
		}
		else {
			org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.json.Utilities.verifyOk(response, code, contentType); // il codice http e' gia' stato impostato
		}
			
		DBVerifier.verify(idTransazione, esitoExpected, operazioneAttesa, erroreAtteso);
		
		return response;	
	}
	
	
}
