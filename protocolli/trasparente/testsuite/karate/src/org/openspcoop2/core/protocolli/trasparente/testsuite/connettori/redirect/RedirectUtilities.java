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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.redirect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
* EngineTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RedirectUtilities {

	public static void _test(
			Logger logCore,
			String api, String operazione, 
			int numeroRedirect, int httpRedirectStatus, boolean relative,
			boolean withContent, String contentType, String contentS) throws Exception {

		Date now = DateManager.getDate();

		String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione;
		if(httpRedirectStatus!=307) {
			if(!url.contains("?")) {
				url = url + "?";
			}
			else {
				url = url + "&";
			}
			url = url +"redirectReturnCode="+httpRedirectStatus;
		}
		if(relative) {
			if(!url.contains("?")) {
				url = url + "?";
			}
			else {
				url = url + "&";
			}
			url = url +"redirectAbsoluteUrl="+false;
		}
		
		HttpRequest request = new HttpRequest();
		
		if(api.contains("SOAP")) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "testRedirect");
		}
		
		request.setReadTimeout(20000);

		String contentTypeError = contentType;
		if(withContent) {
			byte[]content=contentS.getBytes();
			request.setMethod(HttpRequestMethod.POST);
			request.setContentType(contentType);
			request.setContent(content);
		}
		else {
			request.setMethod(HttpRequestMethod.GET);
			contentS = null;
			contentType = null;
		}
		
		request.setUrl(url);
		
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		String diagnostico = null;
		long esitoExpected = -1;
		if(numeroRedirect>0) {
			if("follow-maxhop".equals(operazione)) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE);
				if(api.contains("REST")) {
					verifyKo(response, 503, HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807, "APIUnavailable");
				}
				else {
					verifyKo(response, 500, contentTypeError, "APIUnavailable");
				}
				diagnostico = "non consentita ulteriormente, sono già stati gestiti 2 redirects";
			}
			else {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
				verifyOk(response, 200, contentType, contentS, logCore); // il codice http e' gia' stato impostato
			}
		}
		else {
			if(api.contains("SOAP")) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE);
				verifyKo(response, 500, contentTypeError, "APIUnavailable");
				diagnostico = "Gestione redirect (code:"+httpRedirectStatus+" Location:http://localhost:8080/govway/SoggettoInternoTest/BackendRedirect/v1%) non attiva";
				if(relative) {
					diagnostico = "Gestione redirect (code:"+httpRedirectStatus+" Location:/govway/SoggettoInternoTest/BackendRedirect/v1%) non attiva";
				}
			}
			else {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.HTTP_3xx);
				verifyOk(response, httpRedirectStatus, contentType, contentS, logCore); // il codice http e' gia' stato impostato
				diagnostico = " [redirect-location: http://localhost:8080/govway/SoggettoInternoTest/BackendRedirect/v1";
				if(relative) {
					diagnostico = " [redirect-location: /govway/SoggettoInternoTest/BackendRedirect/v1";
				}
			}
		}
		
		DBVerifier.verify(idTransazione, esitoExpected, diagnostico);
		
		List<String> idByCheckApplicativo = DBVerifier.getIdsTransazioneByIdApplicativoRichiesta(idTransazione, now);
		assertNotNull(idByCheckApplicativo);
		if("follow-maxhop".equals(operazione)) {
			boolean expected = idByCheckApplicativo.size()==3;
			if(!expected) {
				throw new Exception("Attese "+(numeroRedirect+1)+" transazioni di backend con id correlazione '"+idTransazione+"', trovate "+idByCheckApplicativo.size()+"");
			}
		}
		else if(numeroRedirect>0) {
			boolean expected = idByCheckApplicativo.size()==(numeroRedirect+1);
			if(!expected) {
				throw new Exception("Attese "+(numeroRedirect+1)+" transazioni di backend con id correlazione '"+idTransazione+"', trovate "+idByCheckApplicativo.size()+"");
			}
		}
		else {
			boolean expected = idByCheckApplicativo.size()==1; 
			if(!expected) {
				throw new Exception("Attesa 1 transazione di backend con id correlazione '"+idTransazione+"', trovate "+idByCheckApplicativo.size()+"");
			}
		}
		
		for (int i = 0; i < idByCheckApplicativo.size(); i++) {
			diagnostico = " [redirect-location: http://localhost:8080/TestService/echo";
			if(relative) {
				diagnostico = " [redirect-location: /TestService/echo";
			}
			if(i==(idByCheckApplicativo.size()-1)) {
				if(!"follow-maxhop".equals(operazione) && numeroRedirect>0) {
					esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
					diagnostico = null;
				}
				else {
					esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.HTTP_3xx);
				}
			}
			else {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.HTTP_3xx);
			}
			DBVerifier.verify(idByCheckApplicativo.get(i), esitoExpected, diagnostico);
		}

	}
	
	public static void verifyOk(HttpResponse response, int code, String contentType, String contentS, Logger logCore) {
		
		assertEquals(code, response.getResultHTTPOperation());
		if(contentS!=null) {
			try {
				assertEquals(contentType, response.getContentType());
			}catch(Throwable t) {
				logCore.error("Ricevuto: ["+response.getContentType()+"] provo altra verifica... ");
				try {
					assertEquals(contentType+";charset=utf-8", response.getContentType());
				}catch(Throwable tInternal) {
					logCore.error("Ricevuto: ["+response.getContentType()+"]");
					throw t;
				}
			}
			assertEquals(contentS, new String(response.getContent()));
		}
		else {
			assertNull("content type risposta non atteso", response.getContentType());
			String content = null;
			if(response.getContent()!=null && response.getContent().length>0) {
				content = new String(response.getContent());
			}
			assertNull("contenuto risposta non atteso", content);
		}
		
	}
	
	public static void verifyKo(HttpResponse response, int code, String contentType, String error) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(contentType, response.getContentType());
		assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
		
	}
}
