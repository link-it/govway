/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.template;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.autenticazione.Utilities;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {

	
	@Test
	public void erogazione_soap11_template_request_cookie() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_COOKIE+";"+Utilities.COOKIE_NAME+";"+Utilities.TOKEN;
		headers.put(Utilities.SECURITY_TOKEN, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"template",
				headers);
		
		Utilities.verifyResponseCookieHeader(response, false);
	}
	@Test
	public void erogazione_soap12_template_request_cookie() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_COOKIE+";"+Utilities.COOKIE_NAME+";"+Utilities.TOKEN;
		headers.put(Utilities.SECURITY_TOKEN, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_12,
				TipoServizio.EROGAZIONE,
				"template",
				headers);
		
		Utilities.verifyResponseCookieHeader(response, false);
	}
	
	
	@Test
	public void erogazione_soap11_template_request_jwt() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_JWT+";"+Utilities.TOKEN_TYPE_JWT_BEARER+";"+Utilities.TOKEN;
		headers.put(Utilities.SECURITY_TOKEN, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"template",
				headers);
		
		Utilities.verifyResponseAuthorizationHeader(response, false);
	}
	
	@Test
	public void erogazione_soap12_template_request_jwt() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_JWT+";"+Utilities.TOKEN_TYPE_JWT_BEARER+";"+Utilities.TOKEN;
		headers.put(Utilities.SECURITY_TOKEN, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_12,
				TipoServizio.EROGAZIONE,
				"template",
				headers);
		
		Utilities.verifyResponseAuthorizationHeader(response, false);
	}
	
	
	@Test
	public void erogazione_soap11_template_request_soapHeader() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_HEADER_SOAP+";"+Utilities.TOKEN_TYPE_HEADER_SOAP_VALUE+";"+Utilities.TOKEN;
		headers.put(Utilities.SECURITY_TOKEN, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"template",
				headers);
		
		Utilities.verifyResponseSoapHeader(response, false);
	}
	
	@Test
	public void erogazione_soap12_template_request_soapHeader() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_HEADER_SOAP+";"+Utilities.TOKEN_TYPE_HEADER_SOAP_VALUE+";"+Utilities.TOKEN;
		headers.put(Utilities.SECURITY_TOKEN, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_12,
				TipoServizio.EROGAZIONE,
				"template",
				headers);
		
		Utilities.verifyResponseSoapHeader(response, false);
	}
	
	
	
	
	@Test
	public void erogazione_soap11_template_response_cookie() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_COOKIE+";"+Utilities.COOKIE_NAME+";"+Utilities.TOKEN_RISPOSTA;
		headers.put(Utilities.SECURITY_TOKEN_RESPONSE, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"template_response",
				headers);
		
		Utilities.verifyResponseCookieHeader(response, true);
	}
	
	@Test
	public void erogazione_soap12_template_response_cookie() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_COOKIE+";"+Utilities.COOKIE_NAME+";"+Utilities.TOKEN_RISPOSTA;
		headers.put(Utilities.SECURITY_TOKEN_RESPONSE, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_12,
				TipoServizio.EROGAZIONE,
				"template_response",
				headers);
		
		Utilities.verifyResponseCookieHeader(response, true);
	}
	
	
	@Test
	public void erogazione_soap11_template_response_jwt() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_JWT+";"+Utilities.TOKEN_TYPE_JWT_BEARER+";"+Utilities.TOKEN_RISPOSTA;
		headers.put(Utilities.SECURITY_TOKEN_RESPONSE, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"template_response",
				headers);
		
		Utilities.verifyResponseAuthorizationHeader(response, true);
	}
	
	@Test
	public void erogazione_soap12_template_response_jwt() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_JWT+";"+Utilities.TOKEN_TYPE_JWT_BEARER+";"+Utilities.TOKEN_RISPOSTA;
		headers.put(Utilities.SECURITY_TOKEN_RESPONSE, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_12,
				TipoServizio.EROGAZIONE,
				"template_response",
				headers);
		
		Utilities.verifyResponseAuthorizationHeader(response, true);
	}
	
	@Test
	public void erogazione_soap11_template_response_soapHeader() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_HEADER_SOAP+";"+Utilities.TOKEN_TYPE_HEADER_SOAP_VALUE+";"+Utilities.TOKEN_RISPOSTA;
		headers.put(Utilities.SECURITY_TOKEN_RESPONSE, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"template_response",
				headers);
		
		Utilities.verifyResponseSoapHeader(response, true);
	}
	
	@Test
	public void erogazione_soap12_template_response_soapHeader() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_HEADER_SOAP+";"+Utilities.TOKEN_TYPE_HEADER_SOAP_VALUE+";"+Utilities.TOKEN_RISPOSTA;
		headers.put(Utilities.SECURITY_TOKEN_RESPONSE, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_12,
				TipoServizio.EROGAZIONE,
				"template_response",
				headers);
		
		Utilities.verifyResponseSoapHeader(response, true);
	}
	
	
	

	
	@Test
	public void fruizione_soap11_template_request_cookie() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_COOKIE+";"+Utilities.COOKIE_NAME+";"+Utilities.TOKEN;
		headers.put(Utilities.SECURITY_TOKEN, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_11,
				TipoServizio.FRUIZIONE,
				"template",
				headers);
		
		Utilities.verifyResponseCookieHeader(response, false);
	}
	
	@Test
	public void fruizione_soap12_template_request_cookie() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_COOKIE+";"+Utilities.COOKIE_NAME+";"+Utilities.TOKEN;
		headers.put(Utilities.SECURITY_TOKEN, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"template",
				headers);
		
		Utilities.verifyResponseCookieHeader(response, false);
	}
	
	
	@Test
	public void fruizione_soap11_template_request_jwt() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_JWT+";"+Utilities.TOKEN_TYPE_JWT_BEARER+";"+Utilities.TOKEN;
		headers.put(Utilities.SECURITY_TOKEN, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_11,
				TipoServizio.FRUIZIONE,
				"template",
				headers);
		
		Utilities.verifyResponseAuthorizationHeader(response, false);
	}
	
	@Test
	public void fruizione_soap12_template_request_jwt() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_JWT+";"+Utilities.TOKEN_TYPE_JWT_BEARER+";"+Utilities.TOKEN;
		headers.put(Utilities.SECURITY_TOKEN, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"template",
				headers);
		
		Utilities.verifyResponseAuthorizationHeader(response, false);
	}
	
	@Test
	public void fruizione_soap11_template_request_soapHeader() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_HEADER_SOAP+";"+Utilities.TOKEN_TYPE_HEADER_SOAP_VALUE+";"+Utilities.TOKEN;
		headers.put(Utilities.SECURITY_TOKEN, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_11,
				TipoServizio.FRUIZIONE,
				"template",
				headers);
		
		Utilities.verifyResponseSoapHeader(response, false);
	}
	
	@Test
	public void fruizione_soap12_template_request_soapHeader() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_HEADER_SOAP+";"+Utilities.TOKEN_TYPE_HEADER_SOAP_VALUE+";"+Utilities.TOKEN;
		headers.put(Utilities.SECURITY_TOKEN, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"template",
				headers);
		
		Utilities.verifyResponseSoapHeader(response, false);
	}
	
	
	
	
	@Test
	public void fruizione_soap11_template_response_cookie() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_COOKIE+";"+Utilities.COOKIE_NAME+";"+Utilities.TOKEN_RISPOSTA;
		headers.put(Utilities.SECURITY_TOKEN_RESPONSE, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_11,
				TipoServizio.FRUIZIONE,
				"template_response",
				headers);
		
		Utilities.verifyResponseCookieHeader(response, true);
	}
	
	@Test
	public void fruizione_soap12_template_response_cookie() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_COOKIE+";"+Utilities.COOKIE_NAME+";"+Utilities.TOKEN_RISPOSTA;
		headers.put(Utilities.SECURITY_TOKEN_RESPONSE, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"template_response",
				headers);
		
		Utilities.verifyResponseCookieHeader(response, true);
	}
	
	@Test
	public void fruizione_soap11_template_response_jwt() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_JWT+";"+Utilities.TOKEN_TYPE_JWT_BEARER+";"+Utilities.TOKEN_RISPOSTA;
		headers.put(Utilities.SECURITY_TOKEN_RESPONSE, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_11,
				TipoServizio.FRUIZIONE,
				"template_response",
				headers);
		
		Utilities.verifyResponseAuthorizationHeader(response, true);
	}
	
	@Test
	public void fruizione_soap12_template_response_jwt() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_JWT+";"+Utilities.TOKEN_TYPE_JWT_BEARER+";"+Utilities.TOKEN_RISPOSTA;
		headers.put(Utilities.SECURITY_TOKEN_RESPONSE, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"template_response",
				headers);
		
		Utilities.verifyResponseAuthorizationHeader(response, true);
	}
	
	
	
	@Test
	public void fruizione_soap11_template_response_soapHeader() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_HEADER_SOAP+";"+Utilities.TOKEN_TYPE_HEADER_SOAP_VALUE+";"+Utilities.TOKEN_RISPOSTA;
		headers.put(Utilities.SECURITY_TOKEN_RESPONSE, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_11,
				TipoServizio.FRUIZIONE,
				"template_response",
				headers);
		
		Utilities.verifyResponseSoapHeader(response, true);
	}
	
	@Test
	public void fruizione_soap12_template_response_soapHeader() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		String token = Utilities.TOKEN_TYPE_HEADER_SOAP+";"+Utilities.TOKEN_TYPE_HEADER_SOAP_VALUE+";"+Utilities.TOKEN_RISPOSTA;
		headers.put(Utilities.SECURITY_TOKEN_RESPONSE, token);
		
		HttpResponse response = Utilities._test(MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"template_response",
				headers);
		
		Utilities.verifyResponseSoapHeader(response, true);
	}
	
}
