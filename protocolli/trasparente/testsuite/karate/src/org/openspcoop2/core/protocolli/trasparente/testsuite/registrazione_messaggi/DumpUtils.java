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

package org.openspcoop2.core.protocolli.trasparente.testsuite.registrazione_messaggi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jakarta.xml.soap.AttachmentPart;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* DumpUtils
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class DumpUtils {

	public static HttpResponse testRest(TipoServizio tipoServizio, String operazione, String contentType, byte[]content, long contentLength, MessageType formatoMessaggio,
			boolean richiestaIngressoHeader, boolean richiestaIngressoBody,
			boolean richiestaUscitaHeader, boolean richiestaUscitaBody,
			boolean rispostaIngressoHeader, boolean rispostaIngressoBody,
			boolean rispostaUscitaHeader, boolean rispostaUscitaBody,
			boolean multipartParsing, int numAttachments) throws Exception {
		return _test(true, 
				tipoServizio, operazione, contentType, content, contentLength, formatoMessaggio,
				richiestaIngressoHeader, richiestaIngressoBody,
				richiestaUscitaHeader, richiestaUscitaBody,
				rispostaIngressoHeader, rispostaIngressoBody,
				rispostaUscitaHeader, rispostaUscitaBody,
				multipartParsing, numAttachments);
	}
	
	public static HttpResponse testSoap(TipoServizio tipoServizio, String operazione, String contentType, byte[]content, long contentLength, MessageType formatoMessaggio,
			boolean richiestaIngressoHeader, boolean richiestaIngressoBody,
			boolean richiestaUscitaHeader, boolean richiestaUscitaBody,
			boolean rispostaIngressoHeader, boolean rispostaIngressoBody,
			boolean rispostaUscitaHeader, boolean rispostaUscitaBody,
			boolean multipartParsing, int numAttachments) throws Exception {
		return _test(false, 
				tipoServizio, operazione, contentType, content, contentLength, formatoMessaggio,
				richiestaIngressoHeader, richiestaIngressoBody,
				richiestaUscitaHeader, richiestaUscitaBody,
				rispostaIngressoHeader, rispostaIngressoBody,
				rispostaUscitaHeader, rispostaUscitaBody,
				multipartParsing, numAttachments);
	}
	
	private static HttpResponse _test(boolean rest, 
			TipoServizio tipoServizio, String operazione, String contentType, byte[]content, long contentLength, MessageType formatoMessaggio,
			boolean richiestaIngressoHeader, boolean richiestaIngressoBody,
			boolean richiestaUscitaHeader, boolean richiestaUscitaBody,
			boolean rispostaIngressoHeader, boolean rispostaIngressoBody,
			boolean rispostaUscitaHeader, boolean rispostaUscitaBody,
			boolean multipartParsing, int numAttachments) throws Exception {
		

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/TestRegistrazioneMessaggi"+(rest?"REST":"SOAP")+"/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestRegistrazioneMessaggi"+(rest?"REST":"SOAP")+"/v1/"+operazione;
		
		
		HttpRequest request = new HttpRequest();
		
		if(!rest) {
			if(HttpConstants.CONTENT_TYPE_SOAP_1_2.equals(content)) {
				// NOTA: lo metto nella url per tenere, ma non e' corretto, andrebbe nel content-type
				// per questi test di dump non e' comunque una informazione importante
				url=url+"?"+HttpConstants.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION+"=test";
			}
			else {
				request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"test\"");
			}
		}
		
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		request.setUrl(url);
		
		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());
		assertNotNull(response.getContentType());
		if(numAttachments==0) {
			assertEquals(contentType, response.getContentType());
		}
		else {
			assertTrue("Verifico content-type '"+response.getContentType()+"' sia multipart", ContentTypeUtilities.isMultipartType(response.getContentType()));
		}
		
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		assertNotNull(response.getContent());
		String msgContentLength = "";
		if(rest || numAttachments==0) {
			if(content.length!=response.getContent().length) {
				File fSend = File.createTempFile("sendFile-"+idTransazione, ".tmp");
				FileSystemUtilities.writeFile(fSend, content);
				File fReceived = File.createTempFile("receivedFile-"+idTransazione, ".tmp");
				FileSystemUtilities.writeFile(fReceived, response.getContent());
				msgContentLength = "Richiesta spedita serializzata in:"+fSend.getAbsolutePath()+"\nRisposta ricevuta serializzata in: "+fReceived.getAbsolutePath();
			}
			assertEquals(msgContentLength, content.length, response.getContent().length);
		}
		else {
			assertTrue("Verifico content-length '"+response.getContent().length+"' >0", response.getContent().length>0);
		}
		
		DBVerifier.verify(idTransazione, 
				richiestaIngressoHeader, richiestaIngressoBody,
				richiestaUscitaHeader, richiestaUscitaBody,
				rispostaIngressoHeader, rispostaIngressoBody,
				rispostaUscitaHeader, rispostaUscitaBody,
				multipartParsing, numAttachments,
				contentType, (rest || numAttachments==0) ? contentLength : -1, formatoMessaggio.name());
		
		return response;
		
	}
	
	
	public static void verificaRispostaSoap(MessageType messageType, String contentType, byte[] content, HttpResponse response) throws Throwable {
		OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		
		OpenSPCoop2MessageParseResult parse = factory.createMessage(messageType, MessageRole.NONE, contentType, content);
		OpenSPCoop2Message msg = parse.getMessage_throwParseThrowable();
		OpenSPCoop2SoapMessage mm = msg.castAsSoap();
		
		OpenSPCoop2MessageParseResult parseResponse = factory.createMessage(messageType, MessageRole.NONE, response.getContentType(), response.getContent());
		OpenSPCoop2Message msgResponse = parseResponse.getMessage_throwParseThrowable();
		OpenSPCoop2SoapMessage mmResponse = msgResponse.castAsSoap();
		
		Iterator<?> it = mm.getAttachments();
		Iterator<?> itResponse = mmResponse.getAttachments();
		int i = 0;
		while (it.hasNext()) {
			AttachmentPart attachmentPartMM = (AttachmentPart) it.next();
			AttachmentPart attachmentPartMMResponse = (AttachmentPart) itResponse.next();
			
			ConfigLoader.getLoggerRegistrazioneMessaggi().debug("BodyParts["+i+"] ContentType ["+attachmentPartMM.getContentType()+"] == ["+attachmentPartMMResponse.getContentType()+"]");
			assertEquals(attachmentPartMM.getContentType(),attachmentPartMMResponse.getContentType());
			ConfigLoader.getLoggerRegistrazioneMessaggi().debug("BodyParts["+i+"] Size ["+attachmentPartMM.getSize()+"] == ["+attachmentPartMMResponse.getSize()+"]");
			assertEquals(attachmentPartMM.getSize(),attachmentPartMMResponse.getSize());
			
			i++;
		}
	}
	
	
	public static final String HEADER_TEST_1 = "HeaderTest1";
	public static final String HEADER_TEST_2 = "HeaderTest2";
	public static final String HEADER_TEST_3 = "HeaderTest3";
	public static final String HEADER_TEST_4 = "HeaderTest4";
	public static final String HEADER_TEST_5 = "HeaderTest5";
	public static final String HEADER_TEST_VALORE_PREFIX = "Valore-";
	private static List<String> headerWhiteList = new ArrayList<>();
	public static List<String> getHeaderWhiteList() {
		return headerWhiteList;
	}
	static {
		headerWhiteList.add(HEADER_TEST_1);
		headerWhiteList.add(HEADER_TEST_2);
		headerWhiteList.add(HEADER_TEST_5);
	}
	private static List<String> headerBlackList = new ArrayList<>();
	public static List<String> getHeaderBlackList() {
		return headerBlackList;
	}
	static {
		headerBlackList.add(HEADER_TEST_1);
		headerBlackList.add(HEADER_TEST_3);
		headerBlackList.add(HEADER_TEST_4);
	}
	
	public static List<String> getHeaderListCustom(String ... hdr) {
		List<String> l = new ArrayList<>();
		if(hdr!=null && hdr.length>0) {
			l.addAll(Arrays.asList(hdr));
		}
		return l;
	}
	
	
	public static HttpResponse testHeader(boolean rest, boolean dumpBinario, 
			TipoServizio tipoServizio, String operazione, String contentType, byte[]content, long contentLength, MessageType formatoMessaggio,
			boolean richiestaIngressoHeaderWhiteList, List<String> richiestaIngressoHeaderList,
			boolean richiestaUscitaHeaderWhiteList, List<String> richiestaUscitaHeaderList,
			boolean rispostaIngressoHeaderWhiteList, List<String> rispostaIngressoHeaderList,
			boolean rispostaUscitaHeaderWhiteList, List<String> rispostaUscitaHeaderList) throws Exception {
		

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/TestRegistrazioneMessaggiHeaderCustom/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestRegistrazioneMessaggiHeaderCustom/v1/"+operazione;
		
		
		HttpRequest request = new HttpRequest();
		
		if(!rest) {
			if(HttpConstants.CONTENT_TYPE_SOAP_1_2.equals(content)) {
				// NOTA: lo metto nella url per tenere, ma non e' corretto, andrebbe nel content-type
				// per questi test di dump non e' comunque una informazione importante
				url=url+"?"+HttpConstants.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION+"=test";
			}
			else {
				request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"test\"");
			}
		}
		
		request.addHeader(HEADER_TEST_1, HEADER_TEST_VALORE_PREFIX+HEADER_TEST_1);
		request.addHeader(HEADER_TEST_2, HEADER_TEST_VALORE_PREFIX+HEADER_TEST_2);
		request.addHeader(HEADER_TEST_3, HEADER_TEST_VALORE_PREFIX+HEADER_TEST_3);
		request.addHeader(HEADER_TEST_4, HEADER_TEST_VALORE_PREFIX+HEADER_TEST_4);
		request.addHeader(HEADER_TEST_5, HEADER_TEST_VALORE_PREFIX+HEADER_TEST_5);
		
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		request.setUrl(url);
		
		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());
		assertNotNull(response.getContentType());
		assertEquals(contentType, response.getContentType());
		
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		assertNotNull(response.getContent());
		String msgContentLength = "";
		if(rest) {
			if(content.length!=response.getContent().length) {
				File fSend = File.createTempFile("sendFile-"+idTransazione, ".tmp");
				FileSystemUtilities.writeFile(fSend, content);
				File fReceived = File.createTempFile("receivedFile-"+idTransazione, ".tmp");
				FileSystemUtilities.writeFile(fReceived, response.getContent());
				msgContentLength = "Richiesta spedita serializzata in:"+fSend.getAbsolutePath()+"\nRisposta ricevuta serializzata in: "+fReceived.getAbsolutePath();
			}
			assertEquals(msgContentLength, content.length, response.getContent().length);
		}
		else {
			assertTrue("Verifico content-length '"+response.getContent().length+"' >0", response.getContent().length>0);
		}
		
		
		
		DBVerifier.verifyHeaders(idTransazione, 
				dumpBinario ? TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO : TipoMessaggio.RICHIESTA_INGRESSO,
						richiestaIngressoHeaderWhiteList, 
						richiestaIngressoHeaderList);
		
		DBVerifier.verifyHeaders(idTransazione, 
				dumpBinario ? TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO : TipoMessaggio.RICHIESTA_USCITA,
						richiestaUscitaHeaderWhiteList, 
						richiestaUscitaHeaderList);
		
		DBVerifier.verifyHeaders(idTransazione, 
				dumpBinario ? TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO : TipoMessaggio.RISPOSTA_INGRESSO,
						rispostaIngressoHeaderWhiteList, 
						rispostaIngressoHeaderList);
		
		DBVerifier.verifyHeaders(idTransazione, 
				dumpBinario ? TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO : TipoMessaggio.RISPOSTA_USCITA,
						rispostaUscitaHeaderWhiteList, 
						rispostaUscitaHeaderList);

		
		return response;
		
	}
}
