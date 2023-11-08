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
public class WSSecurityUsernameTokenTest extends ConfigLoader {

	private static final String API = "TestWSSecurityUsernameToken";


	@Test
	public void usernameTokenPasswordTextTestOk() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("credenziale-utente","WSSecurityUsernameTokenApplicativo1");
		headers.put("credenziale-password","123456");
		headers.put("credenziale-server","serverCred");
		headers.put("credenziale-server-password","serverPWD");
		test("passwordText", 
				headers,
				"TestWSSecurityUsernameTokenApplicativoTest1","123456");
	}
	
	@Test
	public void usernameTokenPasswordTextTestKoUsername() throws Exception {
		// Nel token viene inserito la credenziale dell'header 'credenziale-utente' ma poi l'erogatore si attende la credenziale associata all'applicativo autenticato TestWSSecurityUsernameTokenApplicativoTest1
		Map<String, String> headers = new HashMap<>();
		headers.put("credenziale-utente","WSSecurityUsernameTokenApplicativo2");
		headers.put("credenziale-password","123456");
		headers.put("credenziale-server","serverCred");
		headers.put("credenziale-server-password","serverPWD");
		test("passwordText", 
				headers,
				"TestWSSecurityUsernameTokenApplicativoTest1","123456",
				"Generatosi errore durante il processamento Message-Security(Receiver): A security error was encountered when verifying the message ; The security token could not be authenticated or authorized ; The security token could not be authenticated or authorized");
	}
	@Test
	public void usernameTokenPasswordTextTestKoPassword() throws Exception {
		// Nel token viene inserito la credenziale dell'header 'credenziale-utente' ma poi l'erogatore si attende la credenziale associata all'applicativo autenticato TestWSSecurityUsernameTokenApplicativoTest1
		Map<String, String> headers = new HashMap<>();
		headers.put("credenziale-utente","WSSecurityUsernameTokenApplicativo1");
		headers.put("credenziale-password","errato");
		headers.put("credenziale-server","serverCred");
		headers.put("credenziale-server-password","serverPWD");
		test("passwordText", 
				headers,
				"TestWSSecurityUsernameTokenApplicativoTest1","123456",
				"Generatosi errore durante il processamento Message-Security(Receiver): A security error was encountered when verifying the message ; The security token could not be authenticated or authorized ; The security token could not be authenticated or authorized");
	}
	
	
	
	@Test
	public void usernameTokenPasswordDigestTestOk() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("credenziale-server","serverCredDigest");
		headers.put("credenziale-server-password","serverPWDDigest");
		test("passwordDigest", 
				headers,
				"TestWSSecurityUsernameTokenApplicativoTest1","123456");
	}
	@Test
	public void usernameTokenPasswordDigestTestOk2() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("credenziale-server","serverCredDigest");
		headers.put("credenziale-server-password","serverPWDDigest");
		test("passwordDigest", 
				headers,
				"TestWSSecurityUsernameTokenApplicativoTest2","123456");
	}

	
	
	
	@Test
	public void usernameTokenMapConfigTestSoggettoOk() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("credenziale-utente","WSSecurityUsernameTokenSoggettoTest1");
		headers.put("credenziale-password","123456");
		headers.put("credenziale-server","serverCredMapSoggetto");
		headers.put("credenziale-server-password","serverPWDMapSoggetto");
		test("mappa", 
				headers,
				"TestWSSecurityUsernameTokenSoggettoTest1","123456");
	}
	@Test
	public void usernameTokenMapConfigTestSoggettoKo() throws Exception {
		// Nel token viene inserito la credenziale dell'header 'credenziale-utente' ma poi l'erogatore si attende la credenziale associata al soggetto autenticato TestWSSecurityUsernameTokenSoggettoTest1
		Map<String, String> headers = new HashMap<>();
		headers.put("credenziale-utente","WSSecurityUsernameTokenSoggettoTestERRATO");
		headers.put("credenziale-password","123456");
		headers.put("credenziale-server","serverCred");
		headers.put("credenziale-server-password","serverPWD");
		test("passwordText", 
				headers,
				"TestWSSecurityUsernameTokenSoggettoTest1","123456",
				"Generatosi errore durante il processamento Message-Security(Receiver): A security error was encountered when verifying the message ; The security token could not be authenticated or authorized ; The security token could not be authenticated or authorized");
	}
	
	@Test
	public void usernameTokenMapConfigTestApplicativoOk() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("credenziale-utente","WSSecurityUsernameTokenApplicativo1");
		headers.put("credenziale-password","123456");
		headers.put("credenziale-server","serverCredMapApplicativo");
		headers.put("credenziale-server-password","serverPWDMapApplicativo");
		test("mappa", 
				headers,
				"TestWSSecurityUsernameTokenApplicativoTest1","123456");
	}
	@Test
	public void usernameTokenMapConfigTestApplicativoKo() throws Exception {
		// Nel token viene inserito la credenziale dell'header 'credenziale-utente' ma poi l'erogatore si attende la credenziale associata all'applicativo autenticato TestWSSecurityUsernameTokenApplicativoTest1
		Map<String, String> headers = new HashMap<>();
		headers.put("credenziale-utente","WSSecurityUsernameTokenApplicativo1");
		headers.put("credenziale-password","ERRATO");
		headers.put("credenziale-server","serverCred");
		headers.put("credenziale-server-password","serverPWD");
		test("passwordText", 
				headers,
				"TestWSSecurityUsernameTokenApplicativoTest1","123456",
				"Generatosi errore durante il processamento Message-Security(Receiver): A security error was encountered when verifying the message ; The security token could not be authenticated or authorized ; The security token could not be authenticated or authorized");
	}
	
	
	@Test
	public void usernameTokenMapConfigTestStaticoOk() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("credenziale-utente","usernameTest");
		headers.put("credenziale-password","passwordTest");
		headers.put("credenziale-server","serverCredMapStatico");
		headers.put("credenziale-server-password","serverPWDMapStatico");
		test("mappa", 
				headers,
				"TestWSSecurityUsernameTokenApplicativoTest1","123456");
	}
	@Test
	public void usernameTokenMapConfigTestStaticoKoUsername() throws Exception {
		// Nel token viene inserito la credenziale dell'header 'credenziale-utente' ma poi l'erogatore si attende la credenziale associata all'applicativo autenticato TestWSSecurityUsernameTokenApplicativoTest1 o alla configurazione statica indicata nell'erogazione
		Map<String, String> headers = new HashMap<>();
		headers.put("credenziale-utente","usernameTestErrato");
		headers.put("credenziale-password","passwordTest");
		headers.put("credenziale-server","serverCred");
		headers.put("credenziale-server-password","serverPWD");
		test("passwordText", 
				headers,
				"TestWSSecurityUsernameTokenApplicativoTest1","123456",
				"Generatosi errore durante il processamento Message-Security(Receiver): A security error was encountered when verifying the message ; The security token could not be authenticated or authorized ; The security token could not be authenticated or authorized");
	}
	@Test
	public void usernameTokenMapConfigTestStaticoKoPassword() throws Exception {
		// Nel token viene inserito la credenziale dell'header 'credenziale-utente' ma poi l'erogatore si attende la credenziale associata all'applicativo autenticato TestWSSecurityUsernameTokenApplicativoTest1 o alla configurazione statica indicata nell'erogazione
		Map<String, String> headers = new HashMap<>();
		headers.put("credenziale-utente","usernameTest");
		headers.put("credenziale-password","passwordTestErrato");
		headers.put("credenziale-server","serverCred");
		headers.put("credenziale-server-password","serverPWD");
		test("passwordText", 
				headers,
				"TestWSSecurityUsernameTokenApplicativoTest1","123456",
				"Generatosi errore durante il processamento Message-Security(Receiver): A security error was encountered when verifying the message ; The security token could not be authenticated or authorized ; The security token could not be authenticated or authorized");
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
		
		String idTransazioneErogazioneHeader = "GovWay-Transaction-ID-Erogazione";
		String idTransazioneErogazione = response.getHeaderFirstValue(idTransazioneErogazioneHeader);
		assertNotNull(idTransazioneErogazione);
		
		long esitoExpectedFruizione = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		long esitoExpectedErogazione = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if(attesoErrore) {
			int code = -1;
			String error = null;
			String msg = null;
			boolean checkErrorTypeGovWay = true;
			String soapPrefixError = "Client";
		
			esitoExpectedFruizione = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
			esitoExpectedErogazione = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA);
			code = 400;
			error = org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509.Utils.BAD_REQUEST;
			msg = org.openspcoop2.core.protocolli.trasparente.testsuite.pkcs11.x509.Utils.BAD_REQUEST_MESSAGE;
			checkErrorTypeGovWay = false;
			boolean errorHttpNull = true;
			
			String errorHttp = errorHttpNull ? null : error;
			
			org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.
			verifyKo(response, error, code, msg, checkErrorTypeGovWay, 
					false, soapPrefixError, errorHttp, ConfigLoader.logCore);

		}
		else {
			org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.verifyOk(response, 200, contentType);
		}
		
		String [] msgErrore = null;
		if(msgErroreList!=null && !msgErroreList.isEmpty()) {
			msgErrore = msgErroreList.toArray(new String[1]);
		}
		DBVerifier.verify(idTransazione, esitoExpectedFruizione);
		DBVerifier.verify(idTransazioneErogazione, esitoExpectedErogazione, msgErrore);
		
		return response;
		
	}
}