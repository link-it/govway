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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
* WSSecurityUsernameTokenTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WSSecuritySAMLTokenTest extends ConfigLoader {

	private static final String API = "TestWSSecuritySAMLToken";

	public static final String AUTHORIZATION_CONTENT_DENY = "AuthorizationContentDeny";
	public static final String AUTHORIZATION_CONTENT_DENY_MESSAGE = "Unauthorized request content";
	

	@Test
	public void samlAutenticazioneTestOk() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("custom-method","CustomMethod");
		headers.put("custom-declaration-method","DeclarationMethod");
		headers.put("custom-ip","128.0.1.2");
		headers.put("custom-dns","serverAuth");
		test("testAuthn", 
				headers,
				null,null);
	}
	
	@Test
	public void samlAutenticazioneTestKoMethod() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("custom-method","CustomMethodErrato");
		headers.put("custom-declaration-method","DeclarationMethod");
		headers.put("custom-ip","128.0.1.2");
		headers.put("custom-dns","serverAuth");
		test("testAuthn", 
				headers,
				null,null,
				"Unauthorized request content");
	}
	
	@Test
	public void samlAutenticazioneTestKoDeclMethod() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("custom-method","CustomMethod");
		headers.put("custom-declaration-method","DeclarationMethodErrato");
		headers.put("custom-ip","128.0.1.2");
		headers.put("custom-dns","serverAuth");
		test("testAuthn", 
				headers,
				null,null,
				"Unauthorized request content");
	}
	
	@Test
	public void samlAutenticazioneTestKoIp() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("custom-method","CustomMethod");
		headers.put("custom-declaration-method","DeclarationMethod");
		headers.put("custom-ip","128.0.1.2Errato");
		headers.put("custom-dns","serverAuth");
		test("testAuthn", 
				headers,
				null,null,
				"Unauthorized request content");
	}
	
	@Test
	public void samlAutenticazioneTestKoDns() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("custom-method","CustomMethod");
		headers.put("custom-declaration-method","DeclarationMethod");
		headers.put("custom-ip","128.0.1.2");
		headers.put("custom-dns","serverAuthErrato");
		test("testAuthn", 
				headers,
				null,null,
				"Unauthorized request content");
	}
	
	
	private static HttpResponse test(String operazione, 
			Map<String, String> headers,
			String username, String password,
			String ... msgErroreParam) throws Exception {
		
		String soggetto = "SoggettoInternoTestFruitore";
		HttpRequestMethod method = HttpRequestMethod.POST;
		String contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
		byte [] content = Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes();
		
		String url = System.getProperty("govway_base_path") + "/out/"+soggetto+"/SoggettoInternoTest/"+API+"/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		
		if(headers!=null && !headers.isEmpty()) {
			for (Map.Entry<String,String> entry : headers.entrySet()) {
				request.addHeader(entry.getKey(), entry.getValue());	
			}
		}
		
		request.setUsername(username);
		request.setPassword(password);
		
		request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, operazione);
		
		List<String> msgErroreList = new ArrayList<>();
		if(msgErroreParam!=null && msgErroreParam.length>0) {
			for (String msgError : msgErroreParam) {
				if(msgError!=null) {
					msgErroreList.add(msgError);
				}
			}
		}
		boolean attesoErrore = !msgErroreList.isEmpty();
		
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
		
		long esitoExpectedFruizione = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if(attesoErrore) {
			int code = -1;
			String error = null;
			String msg = null;
			boolean checkErrorTypeGovWay = true;
			String soapPrefixError = "Client";
		
			esitoExpectedFruizione = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
			code = 403;
			error = AUTHORIZATION_CONTENT_DENY;
			msg = AUTHORIZATION_CONTENT_DENY_MESSAGE;
			checkErrorTypeGovWay = false;
			boolean errorHttpNull = true;
			
			String errorHttp = errorHttpNull ? null : error;
			
			org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.
			verifyKo(idTransazione, response, error, code, msg, checkErrorTypeGovWay, 
					false, soapPrefixError, errorHttp, ConfigLoader.logCore);

		}
		else {
			org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.verifyOk(idTransazione, response, 200, contentType);
		}
		
		DBVerifier.verify(idTransazione, esitoExpectedFruizione);
		
		return response;
		
	}
}