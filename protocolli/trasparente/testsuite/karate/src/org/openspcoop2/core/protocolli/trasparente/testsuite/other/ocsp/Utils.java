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

package org.openspcoop2.core.protocolli.trasparente.testsuite.other.ocsp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.ocsp.test.OpenSSLThread;
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
public class Utils {

	private static final int PORT_CASE2 = 64900;
	private static final int PORT_CASE3 = 64901;
	private static final int PORT_CASE2_DIFFERENT_NONCE = 64902;
	
	public static final String CERTIFICATE_REVOKED_CONNECTION_REFUSED = "OCSP response error (-3 - OCSP_INVOKE_FAILED): OCSP invoke failed; OCSP [certificate: CN=test.esempio.it, O=Esempio, C=it] (url: http://127.0.0.1:PORT): Invoke OCSP 'http://127.0.0.1:PORT' failed: Connection refused (Connection refused)";
	public static final String CERTIFICATE_VALID_CONNECTION_REFUSED = "OCSP response error (-3 - OCSP_INVOKE_FAILED): OCSP invoke failed; OCSP [certificate: CN=Client-test.esempio.it, O=Esempio, C=it] (url: http://127.0.0.1:PORT): Invoke OCSP 'http://127.0.0.1:PORT' failed: Connection refused (Connection refused)";
	
	public static final String CERTIFICATE_REVOKED_CASE2_CONNECTION_REFUSED = CERTIFICATE_REVOKED_CONNECTION_REFUSED.replaceAll("PORT", PORT_CASE2+"");
	public static final String CERTIFICATE_VALID_CASE2_CONNECTION_REFUSED = CERTIFICATE_VALID_CONNECTION_REFUSED.replaceAll("PORT", PORT_CASE2+"");
	
	public static final String CERTIFICATE_REVOKED_CASE3_CONNECTION_REFUSED = CERTIFICATE_REVOKED_CONNECTION_REFUSED.replaceAll("PORT", PORT_CASE3+"");
	public static final String CERTIFICATE_VALID_CASE3_CONNECTION_REFUSED = CERTIFICATE_VALID_CONNECTION_REFUSED.replaceAll("PORT", PORT_CASE3+"");
	
	public static final String CERTIFICATE_REVOKED_CASE2_DIFFERENT_NONCE_CONNECTION_REFUSED = CERTIFICATE_REVOKED_CONNECTION_REFUSED.replaceAll("PORT", PORT_CASE2_DIFFERENT_NONCE+"");
	public static final String CERTIFICATE_VALID_CASE2_DIFFERENT_NONCE_CONNECTION_REFUSED = CERTIFICATE_VALID_CONNECTION_REFUSED.replaceAll("PORT", PORT_CASE2_DIFFERENT_NONCE+"");
	
	public static final String CERTIFICATE_VALID_DIFFERENT_NONCE = "OCSP [certificate: CN=Client-test.esempio.it, O=Esempio, C=it] OCSP analysis failed (url: http://127.0.0.1:64902): OCSP Response not valid: nonces do not match";
	
	public static final String CERTIFICATE_VALID_KEY_USAGE_NOT_FOUND = "OCSP [certificate: CN=Client-test.esempio.it, O=Esempio, C=it] OCSP analysis failed (url: http://127.0.0.1:64901): Signing certificate not valid for signing OCSP responses: extended key usage 'OCSP_SIGNING' not found";
	
	public static final String CERTIFICATE_VALID_UNAUTHORIZED_DIFFERENT_ISSUER_CERTIFICATE = "OCSP [certificate: CN=Client-test.esempio.it, O=Esempio, C=it] OCSP analysis failed (url: http://127.0.0.1:64901): Signing certificate is not authorized to sign OCSP responses: unauthorized different issuer certificate 'C=IT,ST=Italy,L=Pisa,O=Example,CN=ExampleCA'";
	
	public static final String CERTIFICATE_REVOKED_CESSATION_OF_OPERATION = "Certificate revoked in date '%' (Reason: CESSATION_OF_OPERATION)";
	
	public static final String API_UNAVAILABLE = "APIUnavailable";
	public static final String API_UNAVAILABLE_MESSAGE = "The API Implementation is temporary unavailable";
	
	public static final boolean ERROR_CACHED = true;
	
	private static void verifyKo(HttpResponse response, String error, int code, String errorMsg, boolean checkErrorTypeGovWay) {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509.Utils.verifyKo(response, error, code, errorMsg, checkErrorTypeGovWay);
		
	}
	
	private static void verifyOk(HttpResponse response, int code, String expectedContentType) {
		
		assertEquals(code, response.getResultHTTPOperation());
		if(expectedContentType!=null) {
			assertEquals(expectedContentType, response.getContentType());
		}
		
	}
	
	public static HttpResponse get(Logger logCore, String api, String operazione, String msgErrore) throws Exception {
		return test(HttpRequestMethod.GET, null, null,
				logCore, api, operazione, msgErrore);
	}
	public static HttpResponse test(HttpRequestMethod method, String contentType, byte[] content,
			Logger logCore, String api, String operazione, String msgErrore) throws Exception {
		
	
		String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(method);
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

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if(msgErrore!=null) {
			int code = -1;
			String error = null;
			String msg = null;
			boolean checkErrorTypeGovWay = true;
		
			esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE);
			code = 503;
			error = Utils.API_UNAVAILABLE;
			msg = Utils.API_UNAVAILABLE_MESSAGE;
			checkErrorTypeGovWay = false;
			
			Utils.verifyKo(response, error, code, msg, checkErrorTypeGovWay);
		}
		else {
			Utils.verifyOk(response, 200, contentType);
		}
				
		DBVerifier.verify(idTransazione, esitoExpected, msgErrore);
		
		return response;
		
	}

	private static OpenSSLThread newOpenSSLThread_case2(String opensslCommand, int waitStartupServer) throws Exception {
		return newOpenSSLThread(opensslCommand, waitStartupServer, PORT_CASE2, false);
	}
	private static OpenSSLThread newOpenSSLThread_case2_differentNonce(String opensslCommand, int waitStartupServer) throws Exception {
		return newOpenSSLThread(opensslCommand, waitStartupServer, PORT_CASE2_DIFFERENT_NONCE, true);
	}
	private static OpenSSLThread newOpenSSLThread_case3(String opensslCommand, int waitStartupServer) throws Exception {
		return newOpenSSLThread(opensslCommand, waitStartupServer, PORT_CASE3, false);
	}
	private static OpenSSLThread newOpenSSLThread(String opensslCommand, int waitStartupServer, int port, boolean forceWrongNonceResponseValue) throws Exception {
		
		 String fCert = "ocsp/ocsp_TEST.cert.pem";
		 String fKey = "ocsp/ocsp_TEST.key.pem";
		 if(port == PORT_CASE3) {
			 fCert = "crl/ExampleClient1.crt";
			 fKey = "crl/ExampleClient1.key";
		 }
		
		 boolean started = false;
		 int index = 0;
		 OpenSSLThread sslThread = null;
		 int tentativi = 30; // quando succede l'errore di indirizzo già utilizzato è perchè impiega molto tempo a rilasciare la porta in ambiente jenkins
		 while(!started && index<tentativi) {
		 
			 sslThread = new OpenSSLThread(opensslCommand, port, fCert, fKey, "ocsp/ca_TEST.cert.pem", "ocsp/index.txt", forceWrongNonceResponseValue);
			 try {
				 try {
					 sslThread.start();
					 System.out.println("START, sleep ...");
					
					 Utilities.sleep(waitStartupServer);
				 }catch(Throwable t) {
					 // ignore
				 }
				 
				 started = sslThread.debugMsg(false);
			 }finally {
				 if(!started) {
					 index++;
					 System.out.println("NOT STARTED (iteration: "+index+")");
					 // rilascio risorse
					 sslThread.setStop(true);
					 sslThread.waitShutdown(200, 10000);
					 sslThread.close();
				 }
			 }
			 			 
		 }
		
		 System.out.println("STARTED");
		 
		 return sslThread;
	}
	
	private static void stopOpenSSLThread(OpenSSLThread sslThread, int waitStopServer) throws Exception {
		sslThread.setStop(true);
		sslThread.waitShutdown(200, 10000);
		sslThread.close();
		
		// anche se il processo esce, il rilascio della porta serve impiega più tempo
		try {
			System.out.println("STOP, sleep ...");
			
			Utilities.sleep(waitStopServer);
		}catch(Throwable t) {
			// ignore
		}
		System.out.println("STOP");
	}
	
	public static void composedTestSuccess(Logger logCore, String api,
			String opensslCommand, int waitStartupServer, int waitStopServer,
			String action, 
			String connectionRefusedMsg) throws Exception {
		_composedTest(logCore, api,
				opensslCommand, waitStartupServer, waitStopServer,
				action, 
				null, false,
				connectionRefusedMsg);
	}
	public static void composedTestError(Logger logCore, String api,
			String opensslCommand, int waitStartupServer, int waitStopServer,
			String action, String msgErrore, boolean errorCached,
			String connectionRefusedMsg) throws Exception {
		_composedTest(logCore, api,
				opensslCommand, waitStartupServer, waitStopServer,
				action, 
				msgErrore, errorCached,
				connectionRefusedMsg);
	}
	private static void _composedTest(Logger logCore, String api,
			String opensslCommand, int waitStartupServer, int waitStopServer,
			String action, 
			String msgErrore, boolean errorCached,
			String connectionRefusedMsg) throws Exception {
		
		ConfigLoader.resetCache();
		
		// attendo errore connection refused
		Utils.get(logCore, api, action, 
				connectionRefusedMsg);
		
		OpenSSLThread sslThread = null;
		if(action.contains("case3")) {
			sslThread = Utils.newOpenSSLThread_case3(opensslCommand, waitStartupServer);
		}
		else if(action.contains("case2-different-nonce")) {
			sslThread = Utils.newOpenSSLThread_case2_differentNonce(opensslCommand, waitStartupServer);
		}
		else {
			sslThread = Utils.newOpenSSLThread_case2(opensslCommand, waitStartupServer);
		}
		
				
		try {
			Utils.get(logCore, api, action, msgErrore);
		}
		finally {
			Utils.stopOpenSSLThread(sslThread, waitStopServer);
		}
		
		// Deve continuare a funzionare per via della cache
		if(msgErrore==null || errorCached) {
			Utils.get(logCore, api, action, msgErrore);
		}
		else {
			Utils.get(logCore, api, action, 
					connectionRefusedMsg);
		}
		
		ConfigLoader.resetCache_excludeCachePrimoLivello();
		
		// Deve continuare a funzionare per via della cache di secondo livello 'DatiRichieste'
		if(msgErrore==null || errorCached) {
			Utils.get(logCore, api, action, msgErrore);
		}
		else {
			Utils.get(logCore, api, action, 
					connectionRefusedMsg);
		}
		
		ConfigLoader.resetCachePrimoLivello();
		
		// attendo errore connection refused
		Utils.get(logCore, api, action, 
				connectionRefusedMsg);
		
	}
}
