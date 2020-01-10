/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
package org.openspcoop2.message.soap.mtom;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Client
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Client {

	public static void main(String[] args) throws Exception {
		
		// TEST 1
		System.out.println("============ SOAP 11 ============");
		new Client(MessageType.SOAP_11, null, false);
		
		// TEST 2
		System.out.println("============ SOAP 12 ============");
		new Client(MessageType.SOAP_12, null, false);
		
		// TEST 3
		System.out.println("============ SOAP 11 (attach) ============");
		new Client(MessageType.SOAP_11, null, true);
		
		// TEST 4
		System.out.println("============ SOAP 12 (attach) ============");
		new Client(MessageType.SOAP_12, null, true);
				
	}
	
	public Client(MessageType messageType, String messageFactoryClass, boolean addAttachInMsgOriginale) throws Exception {
		
		if(messageFactoryClass!=null){
			OpenSPCoop2MessageFactory.setMessageFactoryImpl(messageFactoryClass);
		}
		
		OpenSPCoop2MessageFactory.initDefaultMessageFactory();
		OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		
		Document d = XMLUtils.getInstance(messageFactory).newDocument("<prova xmlns=\"www.openspcoop.org\"><esempio>Esempio di Utilizzo</esempio></prova>".getBytes());
		Element contenuto1 = d.createElementNS("www.openspcoop.org", "contenuto1");
		contenuto1.setTextContent(Base64Utilities.encodeAsString("<esempioXml xmlns=\"www.openspcoop.org/example1\">PROVA</esempioXml>".getBytes()));
		d.getFirstChild().appendChild(contenuto1);
		Element contenuto2 = d.createElementNS("www.openspcoop.org", "contenuto2");
		contenuto2.setTextContent(Base64Utilities.encodeAsString("<esempioXml2 xmlns=\"www.openspcoop.org/example2\"><nodoInterno>PROVA</nodoInterno></esempioXml2>".getBytes()));
		d.getLastChild().appendChild(contenuto2);
		byte[]xmlOriginale = XMLUtils.getInstance(messageFactory).toByteArray(d,true);
		
		
		String soap11Prefix = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n\t<soapenv:Body>\n";
		String soap11Suffix = "\n\t</soapenv:Body>\n</soapenv:Envelope>";
		
		String soap12Prefix = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">\n\t<soapenv:Body>\n";
		String soap12Suffix = "\n\t</soapenv:Body>\n</soapenv:Envelope>";
		
		
		// SOAP
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		if(MessageType.SOAP_11.equals(messageType)){
			bout.write(soap11Prefix.getBytes());
			bout.write(xmlOriginale);
			bout.write(soap11Suffix.getBytes());
		}
		else{
			bout.write(soap12Prefix.getBytes());
			bout.write(xmlOriginale);
			bout.write(soap12Suffix.getBytes());
		}
		OpenSPCoop2MessageParseResult pr = messageFactory.
				createMessage(messageType, MessageRole.REQUEST, MessageUtilities.getDefaultContentType(messageType), bout.toByteArray());
		OpenSPCoop2Message omsg  = pr.getMessage_throwParseException();
		OpenSPCoop2SoapMessage msg = omsg.castAsSoap();
		
		// usare addAttachInMsgOriginale PER TEST CON ATTACH PER VERIFICARE CHE VENGA LASCIATO INALTERATO L'ATTACHMENT ORIGINALE
		if(addAttachInMsgOriginale){
			javax.xml.soap.AttachmentPart ap = msg.createAttachmentPart();
			String contentType = HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
			ap.setContentId(msg.createContentID("www.openspcoop2.org/example"));
			ap.setBase64Content(new java.io.ByteArrayInputStream(Base64Utilities.encode("PROVA".getBytes())), contentType);
			msg.addAttachmentPart(ap);
		}
		
		msg.updateContentType();
		System.out.println("SOAP ["+msg.getContentType()+"]");
		msg.writeTo(System.out, false);
		System.out.println("\n\n\n");		
		
		
		
		// Packaging
		List<MtomXomPackageInfo> packageInfos = new ArrayList<MtomXomPackageInfo>();
		MtomXomPackageInfo fileInfo = new MtomXomPackageInfo();
		fileInfo.setName("Contenuto1");
		fileInfo.setXpathExpression("//{www.openspcoop.org}contenuto1");
		packageInfos.add(fileInfo);
		MtomXomPackageInfo fileMetaInfo = new MtomXomPackageInfo();
		fileMetaInfo.setName("Contenuto2");
		fileMetaInfo.setXpathExpression("//{www.openspcoop.org}contenuto2");
		fileMetaInfo.setContentType("text/xml");
		fileMetaInfo.setRequired(true);
		packageInfos.add(fileMetaInfo);
		msg.mtomPackaging(packageInfos);
		
		msg.updateContentType();
		System.out.println("PACKAGING ["+msg.getContentType()+"]");
		msg.writeTo(System.out, false);
		System.out.println("\n\n\n");
		
		
		
		
		// Verify
		System.out.println("VERIFICA ...");
		msg.mtomVerify(packageInfos);
		System.out.println("VERIFICA OK");
		System.out.println("\n\n\n");
		
		
		
		// Validazione XSD
		List<MtomXomReference> mtomReferences = msg.mtomFastUnpackagingForXSDConformance();
		System.out.println("PRE-VERIFICA XSD");
		for (MtomXomReference mtomXomReference : mtomReferences) {
			System.out.println("\t- "+mtomXomReference.toString());
		}
		msg.writeTo(System.out, false);
		System.out.println("\n\n\n");
		
		
		
		// Ripristino Validazione XSD
		msg.mtomRestoreAfterXSDConformance(mtomReferences);
		System.out.println("POST-VERIFICA XSD");
		msg.writeTo(System.out, false);
		System.out.println("\n\n\n");
		
		
		
		
		// Unpackaging
		mtomReferences = msg.mtomUnpackaging();
		msg.updateContentType();
		System.out.println("UNPACKAGING ["+msg.getContentType()+"]");
		for (MtomXomReference mtomXomReference : mtomReferences) {
			System.out.println("\t- "+mtomXomReference.toString());
		}
		msg.writeTo(System.out, false);
		System.out.println("\n\n\n");
		
		
		
		
		
		
		// Packaging
		msg.mtomPackaging(packageInfos);
		msg.updateContentType();
		System.out.println("PACKAGING ["+msg.getContentType()+"]");
		msg.writeTo(System.out, false);
		System.out.println("\n\n\n");
	}

}
