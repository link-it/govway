/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.encoding.charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Date;

import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
* CharsetUtilities
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class CharsetUtilities {

	public static final String caratteri_element_name = "caratteri";
	public static final String caratteriNonUTF_JSON = "altro|!£$%&/()=?'^ìéè*+[]ç°§òàù@#<>;,._-fine";
	public static final String caratteriNonUTF_XML = "altro|\\!\"£$%&amp;/()=?'^ìéè*+[]ç°§òàù@#&lt;&gt;;,:._-fine";
	
	public static String convertTo(Logger log, String content, Charset charset) throws Exception {
		
		// NOTA: log può essere null
		
		//Sul file sembra salvare con un formato non corretto (charset=binary) ?
		// Ma di fatto poi iconv non funziona correttamente con UTF-16
		byte[] b = content.getBytes(charset.getValue());
		//org.openspcoop2.utils.resources.FileSystemUtilities.writeFile("/tmp/TEST-"+charset.getValue()+".txt3", b);
		return new String(b,charset.getValue());
		
//		File fTmp = File.createTempFile("testCharset-in-"+charset.getValue()+"-", ".tmp");
//		File fTmpDest = File.createTempFile("testCharset-out-"+charset.getValue()+"-", ".tmp");
//		try {
//			FileSystemUtilities.writeFile(fTmp, content.getBytes());
//			ScriptInvoker scriptInvoker = new ScriptInvoker("iconv");
//			//System.out.println("PARAMETRI ["-c"+"+" -f "+ " UTF-8 "+ " -t " + charset.getValue() + " -o " + fTmpDest.getAbsolutePath() + " " +fTmp.getAbsolutePath()+"]");
//			scriptInvoker.run("-c", "-f", "UTF-8", "-t", charset.getValue(), "-o", fTmpDest.getAbsolutePath(), fTmp.getAbsolutePath());
//			if(log!=null) {
//				log.error("Iconv script eseguito con codice '"+scriptInvoker.getExitValue()+"'");
//				if(scriptInvoker.getOutputStream()!=null) {
//					log.error("OUT: "+scriptInvoker.getOutputStream());
//				}
//				if(scriptInvoker.getErrorStream()!=null) {
//					log.error("ERR: "+scriptInvoker.getErrorStream());
//				}
//			}
//			return FileSystemUtilities.readFile(fTmpDest);
//		}finally {
//			fTmp.delete();
//			//fTmpDest.delete();
//			System.out.println("EEEEEEEE ["+fTmpDest.getAbsolutePath()+"]");
//		}
		
	}
	
	public static void _test(
			Logger logCore,
			String api, String operazione, 
			String paramContentType, String paramContentS, byte[] contentBinary, Charset charset) throws Exception {

		String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione;
				
		HttpRequest request = new HttpRequest();
		
		if(api.contains("SOAP")) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "testCharset");
		}
		
		request.setReadTimeout(20000);

		String contentTypeCharset = null;
		boolean multipart = false;
		try {
			multipart = ContentTypeUtilities.isMultipartType(paramContentType);
		}catch(Throwable t) {}
		if(multipart) {
			contentTypeCharset = paramContentType;
		}
//		else if(paramContentType.contains("charset")) {
//			contentTypeCharset = paramContentType;
//		}
		else {
			contentTypeCharset = paramContentType+";charset="+charset.getValue();
		}
		
		String contentCharset = null;
		byte[]content=null;
		if(api.contains("SOAP") && multipart) {
			content = contentBinary;
		}else {
			contentCharset = convertTo(logCore, paramContentS, charset);
			content=contentCharset.getBytes(charset.getValue());
		}
		
		
		//File inviato = File.createTempFile("send", "tmp");
		//FileSystemUtilities.writeFile(inviato, content);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentTypeCharset);
		request.setContent(content);
		
		request.setUrl(url);
		
		Date now = DateManager.getDate();
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		int esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if(api.contains("SOAP") && !Charset.UTF_8.equals(charset) && !multipart) {
			esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK_PRESENZA_ANOMALIE);
		}
		verifyOk(response, 200, contentTypeCharset, charset, logCore);
		
		try {
			multipart = ContentTypeUtilities.isMultipartType(response.getContentType());
		}catch(Throwable t) {}
		
		if(operazione.startsWith("modify") || multipart) {
			if(api.contains("SOAP")) {
				verifyContentSingleValueSOAP(response, 20, contentTypeCharset, charset, logCore, operazione);
			}
			else {
				verifyContentSingleValueREST(response, 20, contentTypeCharset, contentTypeCharset.startsWith(HttpConstants.CONTENT_TYPE_JSON), charset, logCore);
			}
		}
		else {
			verifyContentOk(response, 200, contentTypeCharset, contentCharset, content, charset, logCore);
		}
		
		DBVerifier.verify(idTransazione, esitoExpected, null);

		if(operazione.startsWith("access_content_read_only")) {
			
			String idCheck = null;
			if(api.contains("SOAP")) {
				idCheck = CharsetUtilities.caratteriNonUTF_XML;
			}
			else {
				if(contentTypeCharset.startsWith(HttpConstants.CONTENT_TYPE_JSON)) {
					idCheck = CharsetUtilities.caratteriNonUTF_JSON;
				}
				else {
					idCheck = CharsetUtilities.caratteriNonUTF_XML;
				}
			}
			
			String id = DBVerifier.getIdTransazioneByIdApplicativoRichiesta(idCheck, now);
			if(id==null) {
				System.out.println("WARN: provo a recuperare l'id di correlazione della richiesta direttamente ("+operazione+") tramite id '"+idTransazione+"' per superare problematiche jenkins");
				String idCorrelazioneApplicativa = DBVerifier.getIdCorrelazioneApplicativaRichiesta(idTransazione);
				assertNotNull("richiesta applicativa by id transazione ["+idTransazione+"]", idCorrelazioneApplicativa);
				try {
					assertEquals("richiesta applicativa by id transazione ["+idCorrelazioneApplicativa+"]", idCorrelazioneApplicativa, idCheck);
				}catch(Throwable t) {
					System.out.println("Attiva verifica lazy (charset:"+charset+") per via della gestione non corretta dei charset su jenkins ("+idTransazione+")");
					if(!idCorrelazioneApplicativa.startsWith("altro|") || !idCorrelazioneApplicativa.endsWith("._-fine") ) {
						throw new Exception(t.getMessage(),t);
					}	
				}
			}
			else {
				assertNotNull("richiesta applicativa ["+idCheck+"]", id);
				assertEquals("richiesta applicativa ["+idCheck+"]", id, idTransazione);
			}
			
			id = DBVerifier.getIdTransazioneByIdApplicativoRisposta(idCheck, now);
			if(id==null) {
				System.out.println("WARN: provo a recuperare l'id di correlazione della risposta direttamente ("+operazione+") tramite id '"+idTransazione+"' per superare problematiche jenkins");
				String idCorrelazioneApplicativa = DBVerifier.getIdCorrelazioneApplicativaRisposta(idTransazione);
				assertNotNull("risposta applicativa by id transazione ["+idTransazione+"]", idCorrelazioneApplicativa);
				try {
					assertEquals("risposta applicativa by id transazione ["+idCorrelazioneApplicativa+"]", idCorrelazioneApplicativa, idCheck);
				}catch(Throwable t) {
					System.out.println("Attiva verifica lazy (charset:"+charset+") per via della gestione non corretta dei charset su jenkins ("+idTransazione+")");
					if(!idCorrelazioneApplicativa.startsWith("altro|") || !idCorrelazioneApplicativa.endsWith("._-fine") ) {
						throw new Exception(t.getMessage(),t);
					}	
				}
			}
			else {
				assertNotNull("risposta applicativa ["+idCheck+"]", id);
				assertEquals("risposta applicativa ["+idCheck+"]", id, idTransazione);
			}
		}

	}
	
	public static void verifyOk(HttpResponse response, int code, String contentType, Charset charset, Logger logCore) throws Exception {
		
		assertEquals(code, response.getResultHTTPOperation());
			
		boolean multipart = false;
		try {
			multipart = ContentTypeUtilities.isMultipartType(response.getContentType());
		}catch(Throwable t) {}
		boolean multipartExpected = false;
		try {
			multipartExpected = ContentTypeUtilities.isMultipartType(contentType);
		}catch(Throwable t) {}
		
		if(multipartExpected!=multipart) {
			throw new Exception("Ricevuto content type ["+response.getContentType()+"] in cui il check multipart ha restituito '"+multipart+"' differente dal check multipart atteso '"+multipartExpected+"' (ct atteso:["+contentType+"])");
		}
		
		if(multipart) {
			String baseType = ContentTypeUtilities.getInternalMultipartContentType(response.getContentType());
			String baseTypeExpected = ContentTypeUtilities.getInternalMultipartContentType(contentType);
			assertEquals(baseType, baseTypeExpected);
		}
		else {
			String charsetCT = ContentTypeUtilities.readCharsetFromContentType(response.getContentType());
			assertEquals(charsetCT, charset.getValue());
			assertEquals(contentType, response.getContentType());
		}
				
	}
	
	public static void verifyContentOk(HttpResponse response, int code, String contentType, String contentS, byte[]content, Charset charset, Logger logCore) throws Exception {

		//File ricevuto = File.createTempFile("received", "tmp");
		//FileSystemUtilities.writeFile(ricevuto, response.getContent());
		
		String charsetCT = ContentTypeUtilities.readCharsetFromContentType(response.getContentType());
		
		if(!Arrays.equals(content, response.getContent())) {
			String responseS = new String(response.getContent(), charsetCT);
			//System.out.println("RESPONSE: "+responseS);
			System.out.println("charset ["+charsetCT+"] atteso ["+contentS.substring(0, 100)+"] ricevuto ["+responseS.substring(0, 100)+"]");
			assertEquals(contentS, responseS); // per far loggare i due messagi
		}
//		else {
//			System.out.println("UGUALE");
//		}
		
	}
	
	public static void verifyContentSingleValueREST(HttpResponse response, int code, String contentType,  boolean json, Charset charset, Logger logCore) throws Exception {
		String charsetCT = ContentTypeUtilities.readCharsetFromContentType(response.getContentType());
		String responseS = new String(response.getContent(), charsetCT);
		if(json) {
			String pattern = "$."+CharsetUtilities.caratteri_element_name;
			String v = JsonPathExpressionEngine.extractAndConvertResultAsString(responseS, pattern, logCore);
			assertEquals(caratteriNonUTF_JSON, v);
			
			pattern = "$.TESTRICHIESTAADD";
			v = JsonPathExpressionEngine.extractAndConvertResultAsString(responseS, pattern, logCore);
			assertEquals("REQ", v);
			
			pattern = "$.TESTRISPOSTAADD";
			v = JsonPathExpressionEngine.extractAndConvertResultAsString(responseS, pattern, logCore);
			assertEquals("RES", v);
			
			pattern = "$.claim-1";
			try {
				v = JsonPathExpressionEngine.extractAndConvertResultAsString(responseS, pattern, logCore);
				throw new Exception("Attesa eccezione not found per claim-1");
			}catch(org.openspcoop2.utils.json.JsonPathNotFoundException notFound) {}
			
			pattern = "$.claim-2";
			try {
				v = JsonPathExpressionEngine.extractAndConvertResultAsString(responseS, pattern, logCore);
				throw new Exception("Attesa eccezione not found per claim-2");
			}catch(org.openspcoop2.utils.json.JsonPathNotFoundException notFound) {}
		}
		else {
			
			String pattern = "//"+CharsetUtilities.caratteri_element_name+"/text()";
			String v = AbstractXPathExpressionEngine.extractAndConvertResultAsString(responseS, null, new XPathExpressionEngine(), pattern, logCore);
			assertEquals(caratteriNonUTF_XML, v);
			
			pattern = "//TESTRICHIESTAADD/text()";
			v = AbstractXPathExpressionEngine.extractAndConvertResultAsString(responseS, null, new XPathExpressionEngine(), pattern, logCore);
			assertEquals("REQ", v);
			
			pattern = "//{http://govway.org/exampleAdd}TESTRISPOSTAADD/text()";
			Element e = XMLUtils.getInstance().newElement(responseS.getBytes());
			DynamicNamespaceContext dnc = new DynamicNamespaceContext();
			dnc.equals(e);
			v = AbstractXPathExpressionEngine.extractAndConvertResultAsString(e, dnc, new XPathExpressionEngine(), pattern, logCore);
			assertEquals("RES", v);
			
			pattern = "//xmlFragment1";
			try {
				v = AbstractXPathExpressionEngine.extractAndConvertResultAsString(responseS, new DynamicNamespaceContext(), new XPathExpressionEngine(), pattern, logCore);
				if(v==null || "".equals(v)) {
					throw new XPathNotFoundException();
				}
				throw new Exception("Attesa eccezione not found per xmlFragment1");
			}catch(XPathNotFoundException notFound) {}
			catch(UtilsMultiException multi) {
				if(!(multi.getExceptions().get(0) instanceof XPathNotFoundException)) {
					throw multi;
				}
			}
			
			pattern = "//xmlFragment2";
			try {
				v = AbstractXPathExpressionEngine.extractAndConvertResultAsString(responseS, new DynamicNamespaceContext(), new XPathExpressionEngine(), pattern, logCore);
				if(v==null || "".equals(v)) {
					throw new XPathNotFoundException();
				}
				throw new Exception("Attesa eccezione not found per xmlFragment2");
			}catch(XPathNotFoundException notFound) {}
			catch(UtilsMultiException multi) {
				if(!(multi.getExceptions().get(0) instanceof XPathNotFoundException)) {
					throw multi;
				}
			}
		}
	}
	
	public static void verifyContentSingleValueSOAP(HttpResponse response, int code, String contentType, Charset charset, Logger logCore, String operazione) throws Exception {
		
		boolean multipart = false;
		try {
			multipart = ContentTypeUtilities.isMultipartType(contentType);
		}catch(Throwable t) {}
		
		if(multipart) {
			
			OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			OpenSPCoop2MessageParseResult parse = factory.createMessage(response.getContentType().contains( HttpConstants.CONTENT_TYPE_SOAP_1_2) ? MessageType.SOAP_12 :MessageType.SOAP_11, 
					MessageRole.NONE, response.getContentType(), response.getContent());
			OpenSPCoop2Message msg = parse.getMessage();
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			DynamicNamespaceContext dnc = new DynamicNamespaceContext();
			dnc.findPrefixNamespace(soapMsg.getSOAPPart().getEnvelope());
			
			String pattern = "//"+CharsetUtilities.caratteri_element_name+"/text()";
			String v = AbstractXPathExpressionEngine.extractAndConvertResultAsString(soapMsg.getSOAPPart().getEnvelope(), dnc, new XPathExpressionEngine(), pattern, logCore);
			try {
				assertEquals(caratteriNonUTF_XML, v);
			}catch(Throwable t) {
				//if(Charset.UTF_8.equals(charset)) {
				throw new Exception(t.getMessage(),t);	
				/*}
				else {
					System.out.println("Attiva verifica lazy (charset:"+charset+") per via della gestione non corretta dei charset differenti da UTF-8 in saaj library");
					if(!v.startsWith("altro|\\!") || !v.endsWith(";,:._-fine") ) {
						throw new Exception(t.getMessage(),t);
					}
				}*/
			}
			
			if(operazione.startsWith("modify")) {
				
				pattern = "//"+soapMsg.getSOAPPart().getEnvelope().getPrefix()+":Header/{http://govway.org/example/req}testSOAPHeaderRichiesta/text()";
				v = AbstractXPathExpressionEngine.extractAndConvertResultAsString(soapMsg.getSOAPPart().getEnvelope(), dnc, new XPathExpressionEngine(), pattern, logCore);
				assertEquals("REQ", v);
				
				pattern = "//"+soapMsg.getSOAPPart().getEnvelope().getPrefix()+":Header/{http://govway.org/example/res}testSOAPHeaderRisposta/text()";
				v = AbstractXPathExpressionEngine.extractAndConvertResultAsString(soapMsg.getSOAPPart().getEnvelope(), dnc, new XPathExpressionEngine(), pattern, logCore);
				assertEquals("RES", v);
				
			}
		}
		else {
			
			String charsetCT = ContentTypeUtilities.readCharsetFromContentType(response.getContentType());
			String responseS = new String(response.getContent(), charsetCT);
			
			String pattern = "//"+CharsetUtilities.caratteri_element_name+"/text()";
			String v = AbstractXPathExpressionEngine.extractAndConvertResultAsString(responseS, null, new XPathExpressionEngine(), pattern, logCore);
			assertEquals(caratteriNonUTF_XML, v);
			
			if(operazione.startsWith("modify")) {
				
				DynamicNamespaceContext dnc = new DynamicNamespaceContext();
				dnc.addNamespace("req", "http://govway.org/example/req");
				dnc.addNamespace("res", "http://govway.org/example/res");
				
				pattern = "//req:testSOAPHeaderRichiesta/text()";
				v = AbstractXPathExpressionEngine.extractAndConvertResultAsString(responseS, dnc, new XPathExpressionEngine(), pattern, logCore);
				assertEquals("REQ", v);
				
				pattern = "//res:testSOAPHeaderRisposta/text()";
				v = AbstractXPathExpressionEngine.extractAndConvertResultAsString(responseS, dnc, new XPathExpressionEngine(), pattern, logCore);
				assertEquals("RES", v);

			}
		}
		

	}

}