/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.message.mtom;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.XMLUtils;
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
		new Client(SOAPVersion.SOAP11, null, false);
		
		// TEST 2
		//new Client(SOAPVersion.SOAP12, null, false);
		
		// TEST 3
		//new Client(SOAPVersion.SOAP11, null, true);
		
		// TEST 4
		//new Client(SOAPVersion.SOAP12, null, true);
				
	}
	
	public Client(SOAPVersion soapVersion, String messageFactory, boolean addAttachInMsgOriginale) throws Exception {
		
		if(messageFactory!=null){
			OpenSPCoop2MessageFactory.setMessageFactoryImpl(messageFactory);
		}
		
		
		Document d = XMLUtils.getInstance().newDocument("<prova xmlns=\"www.openspcoop.org\"><esempio>Esempio di Utilizzo</esempio></prova>".getBytes());
		Element contenuto1 = d.createElementNS("www.openspcoop.org", "contenuto1");
		contenuto1.setTextContent(org.apache.soap.encoding.soapenc.Base64.encode("<esempioXml xmlns=\"www.openspcoop.org/example1\">PROVA</esempioXml>".getBytes()));
		d.getFirstChild().appendChild(contenuto1);
		Element contenuto2 = d.createElementNS("www.openspcoop.org", "contenuto2");
		contenuto2.setTextContent(org.apache.soap.encoding.soapenc.Base64.encode("<esempioXml2 xmlns=\"www.openspcoop.org/example2\"><nodoInterno>PROVA</nodoInterno></esempioXml2>".getBytes()));
		d.getLastChild().appendChild(contenuto2);
		byte[]xmlOriginale = XMLUtils.getInstance().toByteArray(d,true);
		
		
		String soap11Prefix = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n\t<soapenv:Body>\n";
		String soap11Suffix = "\n\t</soapenv:Body>\n</soapenv:Envelope>";
		
		String soap12Prefix = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">\n\t<soapenv:Body>\n";
		String soap12Suffix = "\n\t</soapenv:Body>\n</soapenv:Envelope>";
		
		
		// SOAP
		OpenSPCoop2MessageFactory.initMessageFactory();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		if(SOAPVersion.SOAP11.equals(soapVersion)){
			bout.write(soap11Prefix.getBytes());
			bout.write(xmlOriginale);
			bout.write(soap11Suffix.getBytes());
		}
		else{
			bout.write(soap12Prefix.getBytes());
			bout.write(xmlOriginale);
			bout.write(soap12Suffix.getBytes());
		}
		OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(soapVersion, bout.toByteArray());
		OpenSPCoop2Message msg  = pr.getMessage_throwParseException();
		//OpenSPCoop2Message msg = SoapUtils.imbustamentoMessaggio(null, xmlOriginale, true, false, null, null);
		
		// usare addAttachInMsgOriginale PER TEST CON ATTACH PER VERIFICARE CHE VENGA LASCIATO INALTERATO L'ATTACHMENT ORIGINALE
		if(addAttachInMsgOriginale){
			javax.xml.soap.AttachmentPart ap = msg.createAttachmentPart();
			String contentType = org.openspcoop2.message.Costanti.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
			ap.setContentId(msg.createContentID("www.openspcoop2.org/example"));
			ap.setBase64Content(new java.io.ByteArrayInputStream(org.apache.soap.encoding.soapenc.Base64.encode("PROVA".getBytes()).getBytes()), contentType);
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
