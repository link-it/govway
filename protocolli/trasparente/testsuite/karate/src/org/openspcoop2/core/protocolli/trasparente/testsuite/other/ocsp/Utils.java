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

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ocsp.test.OCSPTest;
import org.openspcoop2.utils.certificate.ocsp.test.OpenSSLThread;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.JwtHeaders;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;
import org.springframework.web.util.UriUtils;

/**
* Utils
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Utils {

	public static final String PROPERTY_OCSP_OPENSSL_COMMAND = "ocsp.opensslCommand";
	public static final String PROPERTY_OCSP_WAIT_STARTUP_SERVER = "ocsp.waitStartupServer";
	public static final String PROPERTY_OCSP_WAIT_STOP_SERVER = "ocsp.waitStopServer";
	
	private static final int PORT_CASE2 = 64900;
	private static final int PORT_CASE3 = 64901;
	private static final int PORT_CASE2_DIFFERENT_NONCE = 64902;
	
	public static final String CERTIFICATE_REVOKED_CONNECTION_REFUSED = "OCSP response error (-3 - OCSP_INVOKE_FAILED): OCSP invoke failed; OCSP [certificate: CN=test.esempio.it, O=Esempio, C=it] (url: http://127.0.0.1:PORT): Invoke OCSP 'http://127.0.0.1:PORT' failed: Connection refused (Connection refused)";
	public static final String CERTIFICATE_REVOKED_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO = 
			"OCSP response error (-3 - OCSP_INVOKE_FAILED): OCSP invoke failed; OCSP [certificate: C=it,O=Esempio,CN=test.esempio.it] (url: http://127.0.0.1:PORT): Invoke OCSP 'http://127.0.0.1:PORT' failed: Connection refused (Connection refused)";
	public static final String CERTIFICATE_VALID_CONNECTION_REFUSED = "OCSP response error (-3 - OCSP_INVOKE_FAILED): OCSP invoke failed; OCSP [certificate: CN=Client-test.esempio.it, O=Esempio, C=it] (url: http://127.0.0.1:PORT): Invoke OCSP 'http://127.0.0.1:PORT' failed: Connection refused (Connection refused)";
	public static final String CERTIFICATE_VALID_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO = 
			"OCSP response error (-3 - OCSP_INVOKE_FAILED): OCSP invoke failed; OCSP [certificate: C=it,O=Esempio,CN=Client-test.esempio.it] (url: http://127.0.0.1:PORT): Invoke OCSP 'http://127.0.0.1:PORT' failed: Connection refused (Connection refused)";
	
	public static final String CERTIFICATE_REVOKED_CASE2_CONNECTION_REFUSED = CERTIFICATE_REVOKED_CONNECTION_REFUSED.replaceAll("PORT", PORT_CASE2+"");
	public static final String CERTIFICATE_REVOKED_CASE2_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO = CERTIFICATE_REVOKED_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO.replaceAll("PORT", PORT_CASE2+"");
	public static final String CERTIFICATE_VALID_CASE2_CONNECTION_REFUSED = CERTIFICATE_VALID_CONNECTION_REFUSED.replaceAll("PORT", PORT_CASE2+"");
	public static final String CERTIFICATE_VALID_CASE2_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO = CERTIFICATE_VALID_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO.replaceAll("PORT", PORT_CASE2+"");
	
	public static final String CERTIFICATE_REVOKED_CASE3_CONNECTION_REFUSED = CERTIFICATE_REVOKED_CONNECTION_REFUSED.replaceAll("PORT", PORT_CASE3+"");
	public static final String CERTIFICATE_REVOKED_CASE3_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO = CERTIFICATE_REVOKED_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO.replaceAll("PORT", PORT_CASE3+"");
	public static final String CERTIFICATE_VALID_CASE3_CONNECTION_REFUSED = CERTIFICATE_VALID_CONNECTION_REFUSED.replaceAll("PORT", PORT_CASE3+"");
	public static final String CERTIFICATE_VALID_CASE3_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO = CERTIFICATE_VALID_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO.replaceAll("PORT", PORT_CASE3+"");
	
	public static final String CERTIFICATE_REVOKED_CASE2_DIFFERENT_NONCE_CONNECTION_REFUSED = CERTIFICATE_REVOKED_CONNECTION_REFUSED.replaceAll("PORT", PORT_CASE2_DIFFERENT_NONCE+"");
	public static final String CERTIFICATE_VALID_CASE2_DIFFERENT_NONCE_CONNECTION_REFUSED = CERTIFICATE_VALID_CONNECTION_REFUSED.replaceAll("PORT", PORT_CASE2_DIFFERENT_NONCE+"");
	
	public static final String CERTIFICATE_VALID_DIFFERENT_NONCE = "OCSP [certificate: CN=Client-test.esempio.it, O=Esempio, C=it] OCSP analysis failed (url: http://127.0.0.1:64902): OCSP Response not valid: nonces do not match";
	
	public static final String CERTIFICATE_VALID_KEY_USAGE_NOT_FOUND = "OCSP [certificate: CN=Client-test.esempio.it, O=Esempio, C=it] OCSP analysis failed (url: http://127.0.0.1:64901): Signing certificate not valid for signing OCSP responses: extended key usage 'OCSP_SIGNING' not found";
	
	public static final String CERTIFICATE_VALID_UNAUTHORIZED_DIFFERENT_ISSUER_CERTIFICATE = "OCSP [certificate: CN=Client-test.esempio.it, O=Esempio, C=it] OCSP analysis failed (url: http://127.0.0.1:64901): Signing certificate is not authorized to sign OCSP responses: unauthorized different issuer certificate 'C=IT,ST=Italy,L=Pisa,O=Example,CN=ExampleCA'";
	
	public static final String CERTIFICATE_REVOKED_CESSATION_OF_OPERATION = "Certificate revoked in date '%' (Reason: CESSATION_OF_OPERATION)";
	
	public static final String CERTIFICATE_CRL_REVOKED_UNSPECIFIED_MSG_KEY_COMPROMISE = "Certificate revoked (Reason: UNSPECIFIED): Certificate not valid (CRL): Certificate revocation after %, reason: keyCompromise";
	
	public static final String CERTIFICATE_CRL_REVOKED_UNSPECIFIED_MSG_KEY_COMPROMISE_WSS = "A security error was encountered when verifying the message ; Certificate revocation after";
	
	public static final String CERTIFICATE_CRL_REVOKED_UNSPECIFIED_MSG_KEY_COMPROMISE_SAML = "Certificate revocation after %, reason: keyCompromise";
	
	public static final String CERTIFICATE_CRL_EXPIRED = "Certificate expired in date '%': OCSP [certificate: C=IT,ST=Italy,L=Pisa,O=Example,CN=ExampleClientScaduto] certificate expired on %";
	
	public static final String CERTIFICATE_CRL_EXPIRED_WSS_SAML = "Certificate expired in date '%': OCSP [certificate: CN=ExampleClientScaduto, O=Example, L=Pisa, ST=Italy, C=IT] NotAfter: %";
	
	public static final String CERTIFICATE_CRL_EXPIRED_WSS_SAML_2 = "Could not validate certificate: NotAfter";
	
	public static final String CERTIFICATE_CRL_EXPIRED_WSS = "A security error was encountered when verifying the message ; certificate expired on";
	
	public static final String CERTIFICATE_CRL_EXPIRED_JOSE_X5C = "Signature verification failure: Process 'x5c' error: Certificato presente nell'header 'x5c' scaduto: certificate expired on ";
	
	public static final String CERTIFICATE_CRL_EXPIRED_JOSE_X5U = "Signature verification failure: Process 'x5u' error: Certificato presente nell'header 'x5u' scaduto: certificate expired on ";
	
	public static final String CERTIFICATE_CRL_EXPIRED_TOKEN_X5T = "Process 'x5t#S256' error: Certificato presente nell'header 'x5t#S256' scaduto: ";
	
	public static final String API_UNAVAILABLE = "APIUnavailable";
	public static final String API_UNAVAILABLE_MESSAGE = "The API Implementation is temporary unavailable";
	
	public static final String PROXY_CREDENTIALS_ID = "PROXY_ID";
	public static final String PROXY_CREDENTIALS_MSG = "Ottenute credenziali di accesso ( SSL-Subject '%' ) fornite da "+PROXY_CREDENTIALS_ID;
	
	public static final boolean ERROR_CACHED = true;
	
//	private static void verifyKo(HttpResponse response, String error, int code, String errorMsg, boolean checkErrorTypeGovWay) {
//		
//		org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509.Utils.verifyKo(response, error, code, errorMsg, checkErrorTypeGovWay);
//		
//	}
	
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
	
	public static HttpResponse get(Logger logCore, String api, String soggetto, String operazione, String ... msgErrore) throws Exception {
		return test(HttpRequestMethod.GET, null, null,
				logCore, api, soggetto, operazione, msgErrore);
	}
	public static HttpResponse get(Logger logCore, TipoServizio tipoServizio, String api, String soggetto, String operazione, String ... msgErrore) throws Exception {
		return test(tipoServizio, HttpRequestMethod.GET, null, null,
				logCore, api, soggetto, operazione, msgErrore);
	}
	public static HttpResponse test(HttpRequestMethod method, String contentType, byte[] content,
			Logger logCore, String api, String soggetto, String operazione, String ... msgErroreParam) throws Exception {
		return test(TipoServizio.EROGAZIONE, method, contentType, content,
				logCore, api, soggetto, operazione, msgErroreParam);
	}
	public static HttpResponse test(TipoServizio tipoServizio, HttpRequestMethod method, String contentType, byte[] content,
			Logger logCore, String api, String soggetto, String operazione, String ... msgErroreParam) throws Exception {
		
		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/"+soggetto+"/"+api+"/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/"+soggetto+"/SoggettoInternoTest/"+api+"/v1/"+operazione;
		
		HttpRequest request = new HttpRequest();
		
		List<String> msgErroreList = new ArrayList<>();
		if(msgErroreParam!=null && msgErroreParam.length>0) {
			for (String msgError : msgErroreParam) {
				if(msgError!=null) {
					msgErroreList.add(msgError);
				}
			}
		}
		boolean attesoErrore = !msgErroreList.isEmpty();
		
		if(GestoreCredenzialiTest.api.equals(api) || AutenticazioneHttpsTest.api.equals(api)) {
			
			String nomeHeaderCertificato = (tipoServizio == TipoServizio.EROGAZIONE) ? "X-Erogazione-SSL-Cert" : "X-Fruizione-SSL-Cert";
			
			boolean addCheckMsgDiagnosticoCredenziali = true;
			if(!msgErroreList.isEmpty()) {
				addCheckMsgDiagnosticoCredenziali = false;
				for (String msgErrore : msgErroreList) {
					if(msgErrore.contains("Certificate revoked in date") && !GestoreCredenzialiTest.api.equals(api)) {
						addCheckMsgDiagnosticoCredenziali = true;
					}
				}
			}
			//System.out.println("CHECK MSG: "+addCheckMsgDiagnosticoCredenziali);
			
			if(GestoreCredenzialiTest.soggetto_caseCRL.equals(soggetto) || AutenticazioneHttpsTest.soggetto_xca.equals(soggetto)) {
				
				if(addCheckMsgDiagnosticoCredenziali) {
					if(GestoreCredenzialiTest.soggetto_caseCRL.equals(soggetto)) {
						msgErroreList.add(PROXY_CREDENTIALS_MSG.replace(PROXY_CREDENTIALS_ID, "WebServerErogazioniSoggettoOCSPCaseCRL"));
					}
					else {
						msgErroreList.add(PROXY_CREDENTIALS_MSG.replace(PROXY_CREDENTIALS_ID,
								(tipoServizio == TipoServizio.EROGAZIONE) ? "WebServerErogazioniSoggettoHttpsXca" : "WebServerFruizioniSoggettoHttpsXca"));
					}
				}
				
				if(operazione.contains("-revoked")) {
					if(operazione.contains("case2")) {
						request.addHeader(nomeHeaderCertificato, getUrlEncodedRevokedCert());
					}
					else {
						request.addHeader(nomeHeaderCertificato, getUrlEncodedExpiredCert());
					}
				}
				else {
					request.addHeader(nomeHeaderCertificato, getUrlEncodedValidCert());
				}
			}
			else {
				
				if(addCheckMsgDiagnosticoCredenziali) {
					if(AutenticazioneHttpsTest.soggetto_ocsp.equals(soggetto)) {
						msgErroreList.add(PROXY_CREDENTIALS_MSG.replace(PROXY_CREDENTIALS_ID,
								(tipoServizio == TipoServizio.EROGAZIONE) ? "WebServerErogazioniSoggettoHttpsOcsp" : "WebServerFruizioniSoggettoHttpsOcsp"));
					}
					else {
						if(operazione.contains("case2")) {
							msgErroreList.add(PROXY_CREDENTIALS_MSG.replace(PROXY_CREDENTIALS_ID, "WebServerErogazioniSoggettoOCSPCase2"));
						}
						else {
							msgErroreList.add(PROXY_CREDENTIALS_MSG.replace(PROXY_CREDENTIALS_ID, "WebServerErogazioniSoggettoOCSPCase3"));
						}
					}
				}
				
				if(operazione.contains("-revoked")) {
					request.addHeader(nomeHeaderCertificato, getBase64RevokedCert());
				}
				else {
					request.addHeader(nomeHeaderCertificato, getBase64ValidCert());
				}
			}
		}
		
		if(TokenPolicyValidazioneTest.api.equals(api)) {
			request.addHeader(HttpConstants.AUTHORIZATION,HttpConstants.AUTHORIZATION_PREFIX_BEARER+buildJwt(operazione));
		}
		
		if(AttributeAuthorityTest.api.equals(api)) {
			request.addHeader("GovWay-TestSuite-JWS",buildJwt(operazione));
		}
		
		if(HttpRequestMethod.POST.equals(method) && HttpConstants.CONTENT_TYPE_SOAP_1_1.equals(contentType)) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, operazione);
		}
		
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

		String idTransazioneHeader = "GovWay-Transaction-ID";
		String idTransazione = response.getHeaderFirstValue(idTransazioneHeader);
		assertNotNull(idTransazione);
		
		if(WSSecuritySignatureTest.api.equals(api) || 
				api.startsWith(WSSecuritySAMLTokenTest.api_saml_prefix) ||
				api.startsWith(JoseSignatureTest.api_prefix)) {
			idTransazione = response.getHeaderFirstValue("GovWay-Erogazione-Transaction-ID");
			assertNotNull(idTransazione);
		}
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if(attesoErrore) {
			int code = -1;
			String error = null;
			String msg = null;
			boolean checkErrorTypeGovWay = true;
			boolean soap = false;
			String soapPrefixError = null;
		
			esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE);
			code = 503;
			error = Utils.API_UNAVAILABLE;
			msg = Utils.API_UNAVAILABLE_MESSAGE;
			checkErrorTypeGovWay = false;
			boolean errorHttpNull = false;
			
			if(GestoreCredenzialiTest.api.equals(api) || AutenticazioneHttpsTest.api.equals(api)) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTENTICAZIONE);
				code = 401;
				if(GestoreCredenzialiTest.api.equals(api)) {
					error = org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali.Utilities.FORWARD_PROXY_AUTHENTICATION_REQUIRED;
					msg = org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali.Utilities.FORWARD_PROXY_AUTHENTICATION_REQUIRED_MESSAGE;
				}
				else if(AutenticazioneHttpsTest.api.equals(api)){
					error = org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali.Utilities.AUTHENTICATION_FAILED;
					msg = org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali.Utilities.AUTHENTICATION_FAILED_MESSAGE;
				}
			}
			
			else if(TokenPolicyValidazioneTest.api.equals(api)) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_TOKEN);
				code = 401;
				error = org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509.Utils.INVALID_TOKEN;
				msg = org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509.Utils.INVALID_TOKEN_MESSAGE;
			}
			
			else if(AttributeAuthorityTest.api.equals(api)) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_AUTORIZZAZIONE);
				code = 403;
				error = org.openspcoop2.core.protocolli.trasparente.testsuite.token.attribute_authority.RestTest.AUTHORIZATION_CONTENT_DENY;
				msg = org.openspcoop2.core.protocolli.trasparente.testsuite.token.attribute_authority.RestTest.AUTHORIZATION_CONTENT_DENY_MESSAGE;
			}
			
			else if(WSSecuritySignatureTest.api.equals(api) || 
					api.startsWith(WSSecuritySAMLTokenTest.api_saml_prefix)) {
				soap = true;
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA);
				code = 400;
				soapPrefixError = "Client";
				error = org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509.Utils.BAD_REQUEST;
				msg = org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509.Utils.BAD_REQUEST_MESSAGE;
				errorHttpNull = true;
			}
			
			else if(api.startsWith(JoseSignatureTest.api_prefix)) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA);
				code = 400;
				error = org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509.Utils.BAD_REQUEST;
				msg = org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509.Utils.BAD_REQUEST_MESSAGE;
				errorHttpNull = true;
			}
			
			String errorHttp = errorHttpNull ? null : error;
			
			if(soap) {
				Utils.verifyKoSoap(logCore, response, error, code, msg, checkErrorTypeGovWay, soapPrefixError, errorHttp);
			}
			else {
				Utils.verifyKoRest(logCore, response, error, code, msg, checkErrorTypeGovWay);
			}
		}
		else {
			Utils.verifyOk(response, 200, contentType);
		}
		
		String [] msgErrore = null;
		if(msgErroreList!=null && !msgErroreList.isEmpty()) {
			msgErrore = msgErroreList.toArray(new String[1]);
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
	
	public static void composedTestSuccess(Logger logCore, TipoServizio tipoServizio, String api, String soggetto,
			String opensslCommand, int waitStartupServer, int waitStopServer,
			String action, 
			String connectionRefusedMsg) throws Exception {
		_composedTest(logCore, tipoServizio, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				action, 
				null, false,
				connectionRefusedMsg);
	}
	public static void composedTestSuccess(Logger logCore, String api, String soggetto,
			String opensslCommand, int waitStartupServer, int waitStopServer,
			String action, 
			String connectionRefusedMsg) throws Exception {
		_composedTest(logCore, TipoServizio.EROGAZIONE, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				action, 
				null, false,
				connectionRefusedMsg);
	}
	public static void composedTestError(Logger logCore, TipoServizio tipoServizio, String api, String soggetto,
			String opensslCommand, int waitStartupServer, int waitStopServer,
			String action, String msgErrore, boolean errorCached,
			String connectionRefusedMsg) throws Exception {
		_composedTest(logCore, tipoServizio, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				action, 
				msgErrore, errorCached,
				connectionRefusedMsg);
	}
	public static void composedTestError(Logger logCore, String api, String soggetto,
			String opensslCommand, int waitStartupServer, int waitStopServer,
			String action, String msgErrore, boolean errorCached,
			String connectionRefusedMsg) throws Exception {
		_composedTest(logCore, TipoServizio.EROGAZIONE, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				action, 
				msgErrore, errorCached,
				connectionRefusedMsg);
	}
	private static void _composedTest(Logger logCore, TipoServizio tipoServizio, String api, String soggetto,
			String opensslCommand, int waitStartupServer, int waitStopServer,
			String action, 
			String msgErrore, boolean errorCached,
			String connectionRefusedMsg) throws Exception {
		
		ConfigLoader.resetCache();
		
		boolean get = true;
		String contentType = null;
		byte[]content = null;
		HttpRequestMethod method = null;
		if(WSSecuritySignatureTest.api.equals(api) || api.startsWith(WSSecuritySAMLTokenTest.api_saml_prefix)) {
			get = false;
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
			content = Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes();
			method = HttpRequestMethod.POST;
		}
		else if(api.startsWith(JoseSignatureTest.api_prefix)) {
			get = false;
			contentType = HttpConstants.CONTENT_TYPE_JSON;
			content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
			method = HttpRequestMethod.PUT;
		}
		
		// attendo errore connection refused
		if(get) {
			Utils.get(logCore, tipoServizio, api, soggetto, action, 
					connectionRefusedMsg);
		}
		else {
			Utils.test(tipoServizio, method, contentType, content,
					logCore, api, soggetto, action, 
					connectionRefusedMsg);
		}
		
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
			if(get) {
				Utils.get(logCore, tipoServizio, api, soggetto, action, 
						msgErrore);
			}
			else {
				Utils.test(tipoServizio, method, contentType, content,
						logCore, api, soggetto, action, 
						msgErrore);
			}
		}
		finally {
			Utils.stopOpenSSLThread(sslThread, waitStopServer);
		}
		
		// Deve continuare a funzionare per via della cache
		if(msgErrore==null || errorCached) {
			if(get) {
				Utils.get(logCore, tipoServizio, api, soggetto, action, 
						msgErrore);
			}
			else {
				Utils.test(tipoServizio, method, contentType, content,
						logCore, api, soggetto, action, 
						msgErrore);
			}
		}
		else {
			if(get) {
				Utils.get(logCore, tipoServizio, api, soggetto, action, 
						connectionRefusedMsg);
			}
			else {
				Utils.test(tipoServizio, method, contentType, content,
						logCore, api, soggetto, action, 
						connectionRefusedMsg);
			}
		}
		
		ConfigLoader.resetCache_excludeCachePrimoLivello();
		
		// Deve continuare a funzionare per via della cache di secondo livello 'DatiRichieste'
		// Fa eccezione l'autenticazione https il cui risultato non viene salvato nella cache di secondo livello
		if( !AutenticazioneHttpsTest.api.equals(api) &&
				(msgErrore==null || errorCached)) {
			if(get) {
				Utils.get(logCore, tipoServizio, api, soggetto, action, 
						msgErrore);
			}
			else {
				Utils.test(tipoServizio, method, contentType, content,
						logCore, api, soggetto, action, 
						msgErrore);
			}
		}
		else {
			if(get) {
				Utils.get(logCore, tipoServizio, api, soggetto, action, 
						connectionRefusedMsg);
			}
			else {
				Utils.test(tipoServizio, method, contentType, content,
						logCore, api, soggetto, action, 
						connectionRefusedMsg);
			}
		}
		
		ConfigLoader.resetCachePrimoLivello();
		
		// attendo errore connection refused
		if(get) {
			Utils.get(logCore, tipoServizio, api, soggetto, action, 
					connectionRefusedMsg);
		}
		else {
			Utils.test(tipoServizio, method, contentType, content,
					logCore, api, soggetto, action, 
					connectionRefusedMsg);
		}
		
	}
	
	public static String getBase64ValidCert() throws Exception {
		return getBase64Cert("ee_TEST_Client-test.esempio.it.cert.pem");
	}
	public static String getBase64RevokedCert() throws Exception {
		return getBase64Cert( "ee_TEST_test.esempio.it.cert.pem");
	}
	private static String getBase64Cert(String fileName) throws Exception {
		
		try(InputStream is = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/ocsp/"+fileName);){
			byte [] certBytes = Utilities.getAsByteArray(is);
			return Base64Utilities.encodeAsString(ArchiveLoader.load(certBytes).getCertificate().getCertificate().getEncoded());
		}
		
	}
	
	public static String getUrlEncodedValidCert() throws Exception {
		return getUrlEncodedCert("ExampleClient1.crt");
	}
	public static String getUrlEncodedRevokedCert() throws Exception {
		return getUrlEncodedCert("ExampleClientRevocato.crt");
	}
	public static String getUrlEncodedExpiredCert() throws Exception {
		return getUrlEncodedCert("ExampleClientScaduto.crt");
	}
	private static String getUrlEncodedCert(String fileName) throws Exception {
		
		try(InputStream is = new FileInputStream("/etc/govway/keys/xca/"+fileName)){
			String s = Utilities.getAsString(is, Charset.UTF_8.getValue());
			return UriUtils.encode(s, Charset.UTF_8.getValue());
		}
		
	}
	
	private static String buildJwt(String operazione) throws Exception {
		
		String type = "pkcs12";
		String alias = null;
		String password = "123456";
		String passwordKey = "123456";
		String file = null;
		if(operazione.equals("case2") || operazione.equals("case3")) {
			alias = "testclient";
			file = "/etc/govway/keys/ocsp/testClient.jks";
		}
		else if(operazione.equals("case2-revoked") || operazione.equals("case3-revoked")) {
			alias = "test";
			file = "/etc/govway/keys/ocsp/test.jks";
		}
		else if(operazione.endsWith("-expired")) {
			alias = "ExampleClientScaduto";
			file = "/etc/govway/keys/xca/ExampleClientScaduto.p12";
		}
		else if(operazione.endsWith("-revoked")) {
			alias = "ExampleClientRevocato";
			file = "/etc/govway/keys/xca/ExampleClientRevocato.p12";
		}
		else {
			alias = "ExampleClient1";
			file = "/etc/govway/keys/xca/ExampleClient1.p12";
		}
		
		boolean x5c = operazione.contains("case2") || operazione.startsWith("crl-");
		boolean x5t = operazione.contains("ocsp-crl-") || operazione.startsWith("crl-");
		String x5u_url = null;
		if(operazione.contains("case3")) {
			if(operazione.equals("case3")) {
				x5u_url = "http://127.0.0.1:8080/ee_TEST_Client-test.esempio.it.cert.pem";
			}
			else {
				x5u_url = "http://127.0.0.1:8080/ee_TEST_test.esempio.it.cert.pem";
			}
		}
		
		
		String jsonInput = "{\n"+
				"\"iss\": \"http://iss/"+alias+"\",\n"+
				"\"sub\": \"testSub-"+alias+"\",\n"+
				"\"client_id\": \"testClientId-"+alias+"\",\n"+
				"\"azp\": \"testClientId-"+alias+"\",\n"+
				"\"username\": \"testUser-"+alias+"\",\n"+
				"\"preferred_username\": \"testUser-"+alias+"\",\n"+
				"\"aud\": \"testAud-"+alias+"\",\n"+
				"\"iat\": "+(DateManager.getTimeMillis()/1000)+",\n"+
				"\"exp\": "+((DateManager.getTimeMillis()/1000)+300)+",\n"+
				"\"jti\": \""+java.util.UUID.randomUUID().toString()+"\"\n"+
				"}";
			
		Properties props = new Properties();
		props.put("rs.security.keystore.file", file);
		props.put("rs.security.keystore.type",type);
		props.put("rs.security.keystore.alias",alias);
		props.put("rs.security.keystore.password",password);
		props.put("rs.security.key.password",passwordKey);
		props.put("rs.security.signature.algorithm","RS256");
		if(x5c) {
			props.put("rs.security.signature.include.cert","true");
		}
		else {
			props.put("rs.security.signature.include.cert","false");
		}
		props.put("rs.security.signature.include.cert.sha1","false");
		if(x5t) {
			props.put("rs.security.signature.include.cert.sha256","true");
		}
		else {
			props.put("rs.security.signature.include.cert.sha256","false");
		}
		
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
		JsonSignature jsonSignature = null;
		if(x5u_url!=null) {
			JwtHeaders jwtHeaders = new JwtHeaders();
			jwtHeaders.setX509Url(new URI(x5u_url));
			jsonSignature = new JsonSignature(props, jwtHeaders, options);
		}
		else {
			jsonSignature = new JsonSignature(props, options);
		}
		String token = jsonSignature.sign(jsonInput);
		//System.out.println(token);
		//System.out.println("DECODED HEADER :"+new String(org.openspcoop2.utils.io.Base64Utilities.decode(token.substring(0, token.indexOf(".")))));
		
		return token;	
	}
}
