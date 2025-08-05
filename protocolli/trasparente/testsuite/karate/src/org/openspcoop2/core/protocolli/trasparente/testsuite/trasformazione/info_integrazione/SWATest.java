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
package org.openspcoop2.core.protocolli.trasparente.testsuite.trasformazione.info_integrazione;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;

/**
* SWATest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SWATest extends ConfigLoader {

	
	// NOTA: il soap engine saaj anche se presente il parametro start non lo preserva e lo elimina prima di serializzare il messaggio.
	

	@Test
	public void erogazione_soap11() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), false,
				"none",
				false, false);
	}
	@Test
	public void fruizione_soap11() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), false,
				"none",
				false, false);
	}
	
	@Test
	public void erogazione_soap12() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), false,
				"none",
				false, false);
	}
	@Test
	public void fruizione_soap12() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), false,
				"none",
				false, false);
	}
	
	
	
	
	
	@Test
	public void erogazione_soap11_start() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), true,
				"none",
				true, false);
	}
	@Test
	public void fruizione_soap11_start() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), true,
				"none",
				true, false);
	}
	
	@Test
	public void erogazione_soap12_start() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), true,
				"none",
				true, false);
	}
	@Test
	public void fruizione_soap12_start() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), true,
				"none",
				true, false);
	}
	
	
	
	
	
	@Test
	public void erogazione_soap11_richiesta() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), false,
				"richiesta",
				true, false);
	}
	@Test
	public void fruizione_soap11_richiesta() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), false,
				"richiesta",
				true, false);
	}
	
	@Test
	public void erogazione_soap12_richiesta() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), false,
				"richiesta",
				true, false);
	}
	@Test
	public void fruizione_soap12_richiesta() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), false,
				"richiesta",
				true, false);
	}
	
	
	
	
	
	
	
	@Test
	public void erogazione_soap11_risposta() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), false,
				"risposta",
				false, true);
	}
	@Test
	public void fruizione_soap11_risposta() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), false,
				"risposta",
				false, true);
	}
	
	@Test
	public void erogazione_soap12_risposta() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), false,
				"risposta",
				false, true);
	}
	@Test
	public void fruizione_soap12_risposta() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), false,
				"risposta",
				false, true);
	}
	
	
	
	
	
	
	
	@Test
	public void erogazione_soap11_richiesta_risposta() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), true,
				"risposta",
				true, true);
	}
	@Test
	public void fruizione_soap11_richiesta_risposta() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), true,
				"risposta",
				true, true);
	}
	
	@Test
	public void erogazione_soap12_richiesta_risposta() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), true,
				"risposta",
				true, true);
	}
	@Test
	public void fruizione_soap12_richiesta_risposta() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), true,
				"risposta",
				true, true);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test
	public void erogazione_soap11_richiesta_context_modifica_prima_spedire() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), false,
				"richiestaModificaPrimaSpedire",
				true, false);
	}
	@Test
	public void fruizione_soap11_richiesta_context_modifica_prima_spedire() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), false,
				"richiestaModificaPrimaSpedire",
				true, false);
	}
	
	@Test
	public void erogazione_soap12_richiesta_context_modifica_prima_spedire() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), false,
				"richiestaModificaPrimaSpedire",
				true, false);
	}
	@Test
	public void fruizione_soap12_richiesta_context_modifica_prima_spedire() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), false,
				"richiestaModificaPrimaSpedire",
				true, false);
	}
	
	
	
	
	
	
	
	@Test
	public void erogazione_soap11_risposta_context_modifica_prima_spedire() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), false,
				"rispostaModificaPrimaSpedire",
				false, true);
	}
	@Test
	public void fruizione_soap11_risposta_context_modifica_prima_spedire() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), false,
				"rispostaModificaPrimaSpedire",
				false, true);
	}
	
	@Test
	public void erogazione_soap12_risposta_context_modifica_prima_spedire() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), false,
				"rispostaModificaPrimaSpedire",
				false, true);
	}
	@Test
	public void fruizione_soap12_risposta_context_modifica_prima_spedire() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), false,
				"rispostaModificaPrimaSpedire",
				false, true);
	}
	
	
	
	
	
	
	
	@Test
	public void erogazione_soap11_richiesta_risposta_context_modifica_prima_spedire() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), true,
				"rispostaModificaPrimaSpedire",
				true, true);
	}
	@Test
	public void fruizione_soap11_richiesta_risposta_context_modifica_prima_spedire() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE), true,
				"rispostaModificaPrimaSpedire",
				true, true);
	}
	
	@Test
	public void erogazione_soap12_richiesta_risposta_context_modifica_prima_spedire() throws Throwable {
		_test(TipoServizio.EROGAZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), true,
				"rispostaModificaPrimaSpedire",
				true, true);
	}
	@Test
	public void fruizione_soap12_richiesta_risposta_context_modifica_prima_spedire() throws Throwable {
		_test(TipoServizio.FRUIZIONE, Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE), true,
				"rispostaModificaPrimaSpedire",
				true, true);
	}
	
	
	
	
	
	
	
	
	private static HttpResponse _test(
			TipoServizio tipoServizio, OpenSPCoop2Message msg, boolean forceStart,
			String operazione,
			boolean attesoRichiesta, boolean attesoRisposta
			)	throws Exception {
		

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/ApiSoapWithAttachmentTestTrasformazioniCT/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/ApiSoapWithAttachmentTestTrasformazioniCT/v1/"+operazione;

		String soapActionRequest = "\""+operazione+"\"";
		
		HttpRequest request = new HttpRequest();
		
		if(forceStart) {
			SoapUtils.addSWAStartParameterIfNotPresent(msg);
		}
		
		msg.updateContentType();
		msg.saveChanges();
		String contentTypeRequest = msg.getContentType();
		
		if(MessageType.SOAP_11.equals(msg.getMessageType())) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, soapActionRequest);
		}
		else {
			contentTypeRequest=contentTypeRequest +"; "+HttpConstants.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION+"="+soapActionRequest;
		}
				
		request.setReadTimeout(20000);
						
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentTypeRequest);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		msg.writeTo(bout, true);
		bout.flush();
		bout.close();
		request.setContent(bout.toByteArray());
		
		request.setUrl(url);
		
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		assertEquals(200, response.getResultHTTPOperation());
		
		String ctRequest = response.getHeaderFirstValue("GovWay-TestSuite-Request-Content-Type");
		assertNotNull(ctRequest);
		assertTrue("found '"+ctRequest+"' atteso multipart",ContentTypeUtilities.isMultipartRelated(ctRequest));
		
		ContentType ctRequestObject = new ContentType(ctRequest);
		String startParamRequest = ctRequestObject.getParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_START);
		if(attesoRichiesta) {
			if(startParamRequest==null || StringUtils.isEmpty(startParamRequest)) {
				throw new UtilsException("Parametro start atteso non trovato nella richiesta");
			}
		}
		else {
			if(StringUtils.isNotEmpty(startParamRequest)) {
				throw new UtilsException("Parametro start non atteso nella richiesta");
			}
		}
		
		String ctResponse = response.getContentType();
		assertNotNull(ctResponse);
		assertTrue("found '"+ctResponse+"' atteso multipart",ContentTypeUtilities.isMultipartRelated(ctResponse));
		
		ContentType ctResponseObject = new ContentType(ctResponse);
		String startParamResponse = ctResponseObject.getParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_START);
		if(attesoRisposta) {
			if(startParamResponse==null || StringUtils.isEmpty(startParamResponse)) {
				throw new UtilsException("Parametro start atteso non trovato nella risposta");
			}
		}
		else {
			if(StringUtils.isNotEmpty(startParamResponse)) {
				throw new UtilsException("Parametro start non atteso nella risposta");
			}
		}
		
		return response;
		
	}
	
}
