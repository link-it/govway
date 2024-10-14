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
import static org.junit.Assert.assertNotNull;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
* Utils
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class HttpProxyUtils {
	
	private HttpProxyUtils() {}

	public static final String ACTION_HTTP = "http";
	public static final String ACTION_HTTPS = "https";
	public static final String ACTION_HTTPS_CUSTOM = "httpsCustom";
	
	public static final String ACTION_HTTP_AUTH = "httpAuth";
	public static final String ACTION_HTTPS_AUTH = "httpsAuth";
	public static final String ACTION_HTTPS_CUSTOM_AUTH = "httpsCustomAuth";
	
	public static final String PROPERTY_MITMDUMP_OPENSSL_COMMAND = "mitmdump.command";
	public static final String PROPERTY_MITMDUMP_WAIT_STARTUP_SERVER = "mitmdump.waitStartupServer";
	public static final String PROPERTY_MITMDUMP_WAIT_STOP_SERVER = "mitmdump.waitStopServer";
	
	public static final String PROXY_USERNAME = "govway-user";
	public static final String PROXY_PASSWORD = "govway-password-123456";
	
	private static final String API_UNAVAILABLE = "APIUnavailable";
	private static final String API_UNAVAILABLE_MESSAGE = "The API Implementation is temporary unavailable";
	
	private static final String CONNECTION_SUCCESS_REST = "[proxy: localhost:PORT] http-method:METHOD) con codice di trasporto: 200";
	private static final String CONNECTION_SUCCESS_SOAP = "[proxy: localhost:PORT]) con codice di trasporto: 200";
	public static String getConnectionSuccessMessageProxyNoAuth(HttpRequestMethod method, boolean soap) {
		return getConnectionSuccessMessage(HttpProxyThread.PORT_NO_AUTH, method, soap);
	}
	public static String getConnectionSuccessMessageProxyAuth(HttpRequestMethod method, boolean soap) {
		return getConnectionSuccessMessage(HttpProxyThread.PORT_AUTH, method, soap);
	}
	public static String getConnectionSuccessMessage(int port, HttpRequestMethod method, boolean soap) {
		String s = soap ? CONNECTION_SUCCESS_SOAP : CONNECTION_SUCCESS_REST;
		return s.replace("PORT", port+"").replace("METHOD", method.name());
	}
	
	private static final String CONNECTION_REFUSED_REST = "[proxy: localhost:PORT] http-method:METHOD): Errore avvenuto durante la consegna HTTP: Connection refused (Connection refused)";
	private static final String CONNECTION_REFUSED_SOAP = "[proxy: localhost:PORT]): Errore avvenuto durante la consegna HTTP: Connection refused (Connection refused)";
	public static String getConnectionRefusedMessageProxyNoAuth(HttpRequestMethod method, boolean soap) {
		return getConnectionRefusedMessage(HttpProxyThread.PORT_NO_AUTH, method, soap);
	}
	public static String getConnectionRefusedMessageProxyAuth(HttpRequestMethod method, boolean soap) {
		return getConnectionRefusedMessage(HttpProxyThread.PORT_AUTH, method, soap);
	}
	public static String getConnectionRefusedMessage(int port, HttpRequestMethod method, boolean soap) {
		String s = soap ? CONNECTION_REFUSED_SOAP : CONNECTION_REFUSED_REST;
		return s.replace("PORT", port+"").replace("METHOD", method.name());
	}
	
	private static final String CONNECTION_ERROR_REST = "[proxy: localhost:PORT] http-method:METHOD): Errore avvenuto durante la consegna HTTP: Unexpected end of file from server";
	private static final String CONNECTION_ERROR_SOAP = "[proxy: localhost:PORT]): Errore avvenuto durante la consegna HTTP: Unexpected end of file from server";
	public static String getConnectionErrorMessageProxyNoAuth(HttpRequestMethod method, boolean soap) {
		return getConnectionErrorMessage(HttpProxyThread.PORT_NO_AUTH, method, soap);
	}
	public static String getConnectionErrorMessageProxyAuth(HttpRequestMethod method, boolean soap) {
		return getConnectionErrorMessage(HttpProxyThread.PORT_AUTH, method, soap);
	}
	public static String getConnectionErrorMessage(int port, HttpRequestMethod method, boolean soap) {
		String s = soap ? CONNECTION_ERROR_SOAP : CONNECTION_ERROR_REST;
		return s.replace("PORT", port+"").replace("METHOD", method.name());
	}
	
	private static final String CONNECTION_PROXY_AUTH_ERROR_REST = "[proxy: localhost:PORT] http-method:METHOD): errore HTTP 407";
	private static final String CONNECTION_PROXY_AUTH_ERROR_SOAP = "[proxy: localhost:PORT]): (407) Proxy Authentication Required";
	public static String getConnectionProxyAuthErrorMessageProxyAuth(HttpRequestMethod method, boolean soap) {
		return getConnectionProxyAuthErrorMessage(HttpProxyThread.PORT_AUTH, method, soap);
	}
	public static String getConnectionProxyAuthErrorMessage(int port, HttpRequestMethod method, boolean soap) {
		String s = soap ? CONNECTION_PROXY_AUTH_ERROR_SOAP : CONNECTION_PROXY_AUTH_ERROR_REST;
		return s.replace("PORT", port+"").replace("METHOD", method.name());
	}
	
	private static void verifyKoRest(Logger log,HttpResponse response, String error, int code, String errorMsg, boolean checkErrorTypeGovWay) {
		org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.
			verifyKo(response, error, code, errorMsg, checkErrorTypeGovWay, 
					true, null, log);
	}
	private static void verifyKoSoap(Logger log,HttpResponse response, String error, int code, String errorMsg, boolean checkErrorTypeGovWay, String soapPrefixError, String errorHttp) {
		org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.
			verifyKo(response, error, code, errorMsg, checkErrorTypeGovWay, 
					false, soapPrefixError, errorHttp, log);
	}
	
	private static void verifyOk(HttpResponse response, int code, String expectedContentType) {
		
		assertEquals(code, response.getResultHTTPOperation());
		if(expectedContentType!=null) {
			assertEquals(expectedContentType, response.getContentType());
		}
		
	}
	
	private static HttpResponse test(TipoServizio tipoServizio, boolean soap, HttpRequestMethod method, String contentType, byte[] content,
			Logger logCore, String api, String operazione, 
			String msgDiagnostico, boolean attesoErrore) throws Exception {
		
		
		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		
		if(HttpRequestMethod.POST.equals(method) && HttpConstants.CONTENT_TYPE_SOAP_1_1.equals(contentType)) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, operazione);
		}
		
		HttpBodyParameters params = new HttpBodyParameters(method, contentType);
		
		request.setReadTimeout(20000);
		request.setMethod(method);
		if(!params.isDoOutput()) {
			contentType = null;
			content = null;
		}
		request.setContentType(contentType);
		request.setContent(content);
		request.setUrl(url);
		
		HttpResponse response = null;
		try {
			//System.out.println("INVOKE ["+request.getUrl()+"]");
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazioneHeader = "GovWay-Transaction-ID";
		String idTransazione = response.getHeaderFirstValue(idTransazioneHeader);
		assertNotNull(idTransazione);
						
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if(attesoErrore) {
			int code = -1;
			String error = null;
			String msg = null;
			boolean checkErrorTypeGovWay = true;
			String soapPrefixError = "Server";
		
			esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE);
			code = 503;
			error = HttpProxyUtils.API_UNAVAILABLE;
			msg = HttpProxyUtils.API_UNAVAILABLE_MESSAGE;
			checkErrorTypeGovWay = false;
			boolean errorHttpNull = false;
						
			String errorHttp = errorHttpNull ? null : error;
			
			if(msgDiagnostico!=null && msgDiagnostico.contains("HTTP 407")) {
				if(soap) {
					esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE);
				}
				else {
					esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.HTTP_4xx);
				}
				code = 407;
				if(soap) {
					assertEquals(500, response.getResultHTTPOperation());
					assertEquals(HttpConstants.CONTENT_TYPE_SOAP_1_1, response.getContentType());
				}
				else {
					assertEquals(code, response.getResultHTTPOperation());
				}
			}
			else if(HttpRequestMethod.HEAD.equals(method)) {
				if(soap) {
					assertEquals(500, response.getResultHTTPOperation());
					assertEquals(HttpConstants.CONTENT_TYPE_SOAP_1_1, response.getContentType());
				}
				else {
					assertEquals(code, response.getResultHTTPOperation());
					assertEquals(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807, response.getContentType());
				}
			}
			else {
				if(soap) {
					HttpProxyUtils.verifyKoSoap(logCore, response, error, code, msg, checkErrorTypeGovWay, soapPrefixError, errorHttp);
				}
				else {
					HttpProxyUtils.verifyKoRest(logCore, response, error, code, msg, checkErrorTypeGovWay);
				}
			}
		}
		else {
			HttpProxyUtils.verifyOk(response, 200, contentType);
		}
		
		DBVerifier.verify(idTransazione, esitoExpected, msgDiagnostico);
		
		return response;
		
	}
	
	public static void composedTestSuccess(Logger logCore, HttpRequestMethod method, TipoServizio tipoServizio, String api, 
			String mitmdumpCommand, int waitStartupServer, int waitStopServer,
			String action) throws Exception {
		_composedTest(logCore, method, tipoServizio, api, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				action, 
				null);
	}
	public static void composedTestError(Logger logCore, HttpRequestMethod method, TipoServizio tipoServizio, String api, 
			String mitmdumpCommand, int waitStartupServer, int waitStopServer,
			String action, String msgErrore) throws Exception {
		_composedTest(logCore, method, tipoServizio, api, 
				mitmdumpCommand, waitStartupServer, waitStopServer,
				action, 
				msgErrore);
	}
	private static void _composedTest(Logger logCore, HttpRequestMethod method, TipoServizio tipoServizio, String api,
			String mitmdumpCommand, int waitStartupServer, int waitStopServer,
			String action, 
			String msgErrore) throws Exception {
		
		ConfigLoader.resetCache();
		
		boolean proxyAuth = action.toLowerCase().contains("auth");
		
		String contentType = null;
		byte[]content = null;
		boolean soap = false;
		if(HttpProxySoapTest.API.equals(api)) {
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
			content = Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes();
			soap = true;
		}
		else {
			contentType = HttpConstants.CONTENT_TYPE_JSON;
			content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		}
		
		String connectionRefusedMsg = 
				proxyAuth ?
						HttpProxyUtils.getConnectionRefusedMessageProxyAuth(method, soap)
						:
						HttpProxyUtils.getConnectionRefusedMessageProxyNoAuth(method, soap);
		
		// attendo errore connection refused
		HttpProxyUtils.test(tipoServizio, soap, method, contentType, content,
				logCore, api, action, 
				connectionRefusedMsg, true);
		
		HttpProxyThread httpProxyThread = null;
		if(proxyAuth) {
			httpProxyThread = HttpProxyThread.newHttpProxyThreadAuth(mitmdumpCommand, waitStartupServer, PROXY_USERNAME, PROXY_PASSWORD);
		}
		else {
			httpProxyThread = HttpProxyThread.newHttpProxyThreadNoAuth(mitmdumpCommand, waitStartupServer);
		}
		
				
		try {
			HttpProxyUtils.test(tipoServizio, soap,method, contentType, content,
						logCore, api, action, 
						msgErrore, (msgErrore!=null));
		}
		finally {
			HttpProxyThread.stopHttpProxyThread(httpProxyThread, waitStopServer);
		}
		
		
				
		// attendo errore connection refused
		String error = connectionRefusedMsg;
		switch (method) {
		case LINK:
		case UNLINK:
		case PUT:
		case PATCH:
			error = proxyAuth ?
					HttpProxyUtils.getConnectionErrorMessageProxyAuth(method, soap)
					:
					HttpProxyUtils.getConnectionErrorMessageProxyNoAuth(method, soap);
			break;
		default:
			break;
		}
		HttpProxyUtils.test(tipoServizio, soap,method, contentType, content,
					logCore, api, action, 
					error, true);
		
		
		if(proxyAuth) {
		
			httpProxyThread = HttpProxyThread.newHttpProxyThreadAuth(mitmdumpCommand, waitStartupServer, PROXY_USERNAME+"ERRATO", PROXY_PASSWORD+"ERRATO");
			
			String msgErroreAuth = HttpProxyUtils.getConnectionProxyAuthErrorMessageProxyAuth(method, soap);
			try {
				HttpProxyUtils.test(tipoServizio, soap,method, contentType, content,
							logCore, api, action, 
							msgErroreAuth, true);
			}
			finally {
				HttpProxyThread.stopHttpProxyThread(httpProxyThread, waitStopServer);
			}
			
			
					
			// attendo errore connection refused
			error = connectionRefusedMsg;
			/**switch (method) {
			case LINK:
			case UNLINK:
			case PUT:
			case PATCH:
				error = HttpProxyUtils.getConnectionErrorMessageProxyAuth(method);
				break;
			default:
				break;
			}*/
			HttpProxyUtils.test(tipoServizio, soap,method, contentType, content,
						logCore, api, action, 
						error, true);
			
		}
		
	}
	
}
