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

package org.openspcoop2.core.protocolli.trasparente.testsuite;

import java.io.ByteArrayOutputStream;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.soap.AttachmentPart;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.dch.InputStreamDataSource;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* Bodies
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Bodies {
	
	public final static int SIZE_1K = 1024; // 1024 byte=1K
	public final static int SIZE_5K = 1024*5; 
	public final static int SIZE_50K = 1024*50; 
	public final static int SIZE_500K = 1024*500; 
	
	public final static int SMALL_SIZE = 1024; // 1024 byte=1K
	public final static int BIG_SIZE = 1024*1024*2; // {(1024*1024*2)}, // 2MB
	
	private final static String PATH = "/org/openspcoop2/core/protocolli/trasparente/testsuite/";
	
	private final static String getXmlPayload(int sizePayload,String prefix) { 
		StringBuilder sb = new StringBuilder();
		int index = 1;
		while(sb.length()<sizePayload) {
			sb.append(prefix).append("<xmlFragment>TEST ESEMPIO ").append(index++).append("</xmlFragment>\n");
		}
		return sb.toString();
	}
	
	public final static String getXML(int sizePayload) { 
		return "<ns2:Test xmlns:ns2=\"http://govway.org/example\">\n" + 
			getXmlPayload(sizePayload,"") +
			"\n</ns2:Test>";
	}
	
	public final static String getSOAPEnvelope11(int sizePayload) { 
		return "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +  
			"    <soap:Body>\n" + 
			"        <ns2:Test xmlns:ns2=\"http://govway.org/example\">\n" + 
			getXmlPayload(sizePayload,"           ") +
			"        </ns2:Test>\n" + 
			"    </soap:Body>\n" + 
			"</soap:Envelope>";
	}
	
	public final static String getSOAPEnvelope12(int sizePayload) { 
		return "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +  
			"    <soap:Body>\n" + 
			"        <ns2:Test xmlns:ns2=\"http://govway.org/example\">\n" + 
			getXmlPayload(sizePayload,"           ") +
			"        </ns2:Test>\n" + 
			"    </soap:Body>\n" + 
			"</soap:Envelope>";
	}
	
	
	private final static String getJsonPayload(int sizePayload) { 
		StringBuilder sb = new StringBuilder();
		int index = 1;
		while(sb.length()<sizePayload) {
			if(index>1) {
				sb.append(",");
			}
			sb.append("\n  \"claim-"+index+"\": \"TEST ESEMPIO ").append(index++).append("\"");
		}
		return sb.toString();
	}
	
	public final static String getJson(int sizePayload) { 
		return "{\n" + 
				getJsonPayload(sizePayload) +
			"\n}";
	}
		
	public final static byte[] getPdf() throws UtilsException {
		return Utilities.getAsByteArray(Bodies.class.getResourceAsStream(PATH+"HelloWorld.pdf"));
	}
	public final static byte[] getZip() throws UtilsException {
		return Utilities.getAsByteArray(Bodies.class.getResourceAsStream(PATH+"HelloWorld.zip"));
	}
	
	public final static MimeMultipart getMultipartRelated(int sizePayload) throws Exception {
		return getMultipartRelated(sizePayload, "related");
	}
	public final static MimeMultipart getMultipartMixed(int sizePayload) throws Exception {
		return getMultipartRelated(sizePayload, "mixed");
	}
	private final static MimeMultipart getMultipartRelated(int sizePayload, String subType) throws Exception { 
		
		org.openspcoop2.utils.id.UniversallyUniqueIdentifierGenerator uuidGeneratore = new org.openspcoop2.utils.id.UniversallyUniqueIdentifierGenerator();
		
		MimeMultipart mm = new MimeMultipart(subType);
		
		BodyPart bodyXml = new MimeBodyPart();
		String contentType = HttpConstants.CONTENT_TYPE_XML;
		bodyXml.setDataHandler(new DataHandler(new ByteArrayDataSource(getXML(sizePayload/2).getBytes(), contentType)));
		bodyXml.addHeader("Content-Type", contentType);
		bodyXml.addHeader("Content-Id",  uuidGeneratore.newID().getAsString().replace("-", ""));
		mm.addBodyPart(bodyXml);
		
		BodyPart bodyJson = new MimeBodyPart();
		String contentTypeJson = HttpConstants.CONTENT_TYPE_JSON;
		bodyJson.setDataHandler(new DataHandler(new ByteArrayDataSource(getJson(sizePayload/2).getBytes(), contentTypeJson)));
		bodyJson.addHeader("Content-Type", contentTypeJson);
		bodyJson.addHeader("Content-Id",  uuidGeneratore.newID().getAsString().replace("-", ""));
		mm.addBodyPart(bodyJson);

		byte[]pdf = getPdf();
		BodyPart bodyPdf = new MimeBodyPart();
		String contentTypePdf = HttpConstants.CONTENT_TYPE_PDF;
		bodyPdf.setDataHandler(new DataHandler(new ByteArrayDataSource(pdf, contentTypePdf)));
		bodyPdf.addHeader("Content-Type", contentTypePdf);
		bodyPdf.addHeader("Content-Id", uuidGeneratore.newID().getAsString().replace("-", ""));
		mm.addBodyPart(bodyPdf);

		BodyPart bodyTxt = new MimeBodyPart();
		String contentTypeTxt = HttpConstants.CONTENT_TYPE_PLAIN;
		bodyTxt.setDataHandler(new DataHandler(new ByteArrayDataSource("Hello world".getBytes(), contentTypeTxt)));
		bodyTxt.addHeader("Content-Type", contentTypeTxt);
		bodyTxt.addHeader("Content-Id", uuidGeneratore.newID().getAsString().replace("-", ""));
		mm.addBodyPart(bodyTxt);

		byte[]zip = getZip();
		BodyPart bodyZip = new MimeBodyPart();
		String contentTypeZip = HttpConstants.CONTENT_TYPE_ZIP;
		bodyZip.setDataHandler(new DataHandler(new ByteArrayDataSource(zip, contentTypeZip)));
		bodyZip.addHeader("Content-Type", contentTypeZip);
		bodyZip.addHeader("Content-Id", uuidGeneratore.newID().getAsString().replace("-", ""));
		mm.addBodyPart(bodyZip);
		
		return mm;
		
	}
	public final static byte[] toByteArray(MimeMultipart mm) throws Exception{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		mm.writeTo(os);
		
		os.flush();
		os.close();
		return os.toByteArray();
	}
	
	
	public final static OpenSPCoop2Message getSOAP11WithAttachments(int sizePayload) throws Throwable{
		return _getSOAPWithAttachments(MessageType.SOAP_11, HttpConstants.CONTENT_TYPE_SOAP_1_1, getSOAPEnvelope11(sizePayload).getBytes());
	}
	public final static OpenSPCoop2Message getSOAP12WithAttachments(int sizePayload) throws Throwable{
		return _getSOAPWithAttachments(MessageType.SOAP_12, HttpConstants.CONTENT_TYPE_SOAP_1_2, getSOAPEnvelope12(sizePayload).getBytes());
	}
	private final static OpenSPCoop2Message _getSOAPWithAttachments(MessageType messageType, String contentType, byte [] envelope) throws Throwable { 
		
		OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		OpenSPCoop2MessageParseResult parse = factory.createMessage(messageType, MessageRole.NONE, contentType, envelope);
		OpenSPCoop2Message msg = parse.getMessage_throwParseThrowable();
		OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
		
		AttachmentPart apXml = null;
		String contentTypeXml = HttpConstants.CONTENT_TYPE_XML;
		byte [] contentXml = getXML(SMALL_SIZE).getBytes();
		InputStreamDataSource isSourceXml = new InputStreamDataSource("attachXml", contentTypeXml, contentXml);
		apXml = soapMsg.createAttachmentPart(new DataHandler(isSourceXml));		
		String contentIDXml = soapMsg.createContentID("http://govway.org/example");
		if(contentIDXml.startsWith("<")){
			contentIDXml = contentIDXml.substring(1);
		}
		if(contentIDXml.endsWith(">")){
			contentIDXml = contentIDXml.substring(0,contentIDXml.length()-1);
		}
		apXml.setContentId(contentIDXml);
		soapMsg.addAttachmentPart(apXml);

		
		AttachmentPart apJson = null;
		String contentTypeJson = HttpConstants.CONTENT_TYPE_JSON;
		byte [] contentJson = getJson(SMALL_SIZE).getBytes();
		InputStreamDataSource isSourceJson = new InputStreamDataSource("attachJson", contentTypeJson, contentJson);
		apJson = soapMsg.createAttachmentPart(new DataHandler(isSourceJson));		
		String contentIDJson = soapMsg.createContentID("http://govway.org/example");
		if(contentIDJson.startsWith("<")){
			contentIDJson = contentIDJson.substring(1);
		}
		if(contentIDJson.endsWith(">")){
			contentIDJson = contentIDJson.substring(0,contentIDJson.length()-1);
		}
		apJson.setContentId(contentIDJson);
		soapMsg.addAttachmentPart(apJson);
		
		
		AttachmentPart apPdf = null;
		String contentTypePdf = HttpConstants.CONTENT_TYPE_PDF;
		byte [] contentPdf = getPdf();
		InputStreamDataSource isSourcePdf = new InputStreamDataSource("attachPdf", contentTypePdf, contentPdf);
		apPdf = soapMsg.createAttachmentPart(new DataHandler(isSourcePdf));		
		String contentIDPdf = soapMsg.createContentID("http://govway.org/example");
		if(contentIDPdf.startsWith("<")){
			contentIDPdf = contentIDPdf.substring(1);
		}
		if(contentIDPdf.endsWith(">")){
			contentIDPdf = contentIDPdf.substring(0,contentIDPdf.length()-1);
		}
		apPdf.setContentId(contentIDPdf);
		soapMsg.addAttachmentPart(apPdf);
		
		
		
		AttachmentPart apTextPlain = null;
		String contentTypeTextPlain = HttpConstants.CONTENT_TYPE_PLAIN;
		byte [] contentTextPlain = "Hello world".getBytes();
		InputStreamDataSource isSourceTextPlain = new InputStreamDataSource("attachTextPlain", contentTypeTextPlain, contentTextPlain);
		apTextPlain = soapMsg.createAttachmentPart(new DataHandler(isSourceTextPlain));		
		String contentIDTextPlain = soapMsg.createContentID("http://govway.org/example");
		if(contentIDTextPlain.startsWith("<")){
			contentIDTextPlain = contentIDTextPlain.substring(1);
		}
		if(contentIDTextPlain.endsWith(">")){
			contentIDTextPlain = contentIDTextPlain.substring(0,contentIDTextPlain.length()-1);
		}
		apTextPlain.setContentId(contentIDTextPlain);
		soapMsg.addAttachmentPart(apTextPlain);
		
		
		
		AttachmentPart apZip = null;
		String contentTypeZip = HttpConstants.CONTENT_TYPE_ZIP;
		byte [] contentZip = getZip();
		InputStreamDataSource isSourceZip = new InputStreamDataSource("attachZip", contentTypeZip, contentZip);
		apZip = soapMsg.createAttachmentPart(new DataHandler(isSourceZip));		
		String contentIDZip = soapMsg.createContentID("http://govway.org/example");
		if(contentIDZip.startsWith("<")){
			contentIDZip = contentIDZip.substring(1);
		}
		if(contentIDZip.endsWith(">")){
			contentIDZip = contentIDZip.substring(0,contentIDZip.length()-1);
		}
		apZip.setContentId(contentIDZip);
		soapMsg.addAttachmentPart(apZip);
		
		
		return soapMsg;
		
	}
	public final static byte[] toByteArray(OpenSPCoop2Message msg) throws Exception{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		msg.writeTo(os, true);
		
		os.flush();
		os.close();
		return os.toByteArray();
	}
}
