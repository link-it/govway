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
package org.openspcoop2.utils.xml.test;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * TestXXE
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestXXE {

	public static byte [] xmlMessage = ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><!DOCTYPE x [ <!ENTITY\n"+
			"% test SYSTEM \"http://X.X.X.X/data.dtd\"> %test; ]>\n"+
			"<root>\n"+
			"<esempio>TEST</esempio>\n"+
			"</root>").getBytes();
	
	public static byte [] xsdSchema = ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"+
			"<!DOCTYPE x [ <!ENTITY\n"+
			"% test SYSTEM \"http://X.X.X.X/data.dtd\"> %test; ]>\n"+
			"<xsd:schema xmlns=\"http://www.govway.org/test\"\n"+
    "targetNamespace=\"http://www.govway.org/test\"\n"+
    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\"\n"+
    "attributeFormDefault=\"unqualified\">\n"+
    "\n"+
    "<xsd:element name=\"root\">\n"+
    "        <xsd:complexType>\n"+
    "                <xsd:sequence>\n"+
    "                        <xsd:element name=\"test1\" type=\"xsd:string\" maxOccurs=\"unbounded\" minOccurs=\"1\"/>\n"+
    "                        <xsd:element name=\"test2\" type=\"xsd:string\" maxOccurs=\"1\" minOccurs=\"1\"/>\n"+
    "                </xsd:sequence>\n"+
    "        </xsd:complexType>\n"+
    "</xsd:element>\n"+
    "</xsd:schema>").getBytes();
	
	public static byte [] xslt = ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"+
			"<!DOCTYPE x [ <!ENTITY\n"+
			"% test SYSTEM \"http://X.X.X.X/data.dtd\"> %test; ]>\n"+
			"<xsl:template match=\"*\">\n"+
					"    <xsl:element name=\"ns:{local-name()}\" namespace=\"https://example.govway.org\">\n"+
					"      <xsl:apply-templates select=\"@* | node()\"/>\n"+
					"    </xsl:element>\n"+
					"  </xsl:template>").getBytes();
	
	public static byte [] multipart = ("------=_Part_0_6330713.1171639717331\n"+
			"Content-Type: text/xml; charset=UTF-8\n"+
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
			"------=_Part_0_6330713.1171639717331--").getBytes();
	public static String multipart_content_type = "multipart/related;   boundary=\"----=_Part_0_6330713.1171639717331\";   type=\"application/xml\"";
	
	public static void main(String[] args) throws Exception {
		test();
	}
	
	public static void test() throws Exception {
		
		// Default è true
		// AbstractXMLUtils.DISABLE_DTDs=false;
		
		
		System.out.println("- newDocument(byte[])");
		try {
			org.openspcoop2.utils.xml.XMLUtils.getInstance().newDocument(xmlMessage);
			throw new Exception("newDocument ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		System.out.println("");
		System.out.println("- newDocument(InputStream)");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(xmlMessage)){
				org.openspcoop2.utils.xml.XMLUtils.getInstance().newDocument(bin);
			}
			throw new Exception("newDocument ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		System.out.println("");
		System.out.println("- newDocument(File)");
		File file = File.createTempFile("testXXE", ".xml");
		try {
			FileSystemUtilities.writeFile(file, xmlMessage);
			org.openspcoop2.utils.xml.XMLUtils.getInstance().newDocument(file);
			throw new Exception("newDocument ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}finally {
			file.delete();
		}
		
		System.out.println("");
		System.out.println("- newDocument(InputSource)");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(xmlMessage)){
				InputSource inputSource = new InputSource(bin);
				org.openspcoop2.utils.xml.XMLUtils.getInstance().newDocument(inputSource);
			}
			throw new Exception("newDocument ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		System.out.println("");
		System.out.println("- newElement(byte[])");
		try {
			org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement(xmlMessage);
			throw new Exception("newElement ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		System.out.println("");
		System.out.println("- newElement(InputStream)");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(xmlMessage)){
				org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement(bin);
			}
			throw new Exception("newElement ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		System.out.println("");
		System.out.println("- newElement(File)");
		file = File.createTempFile("testXXE", ".xml");
		try {
			FileSystemUtilities.writeFile(file, xmlMessage);
			org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement(file);
			throw new Exception("newElement ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}finally {
			file.delete();
		}
		
		System.out.println("");
		System.out.println("- newElement(InputSource)");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(xmlMessage)){
				InputSource inputSource = new InputSource(bin);
				org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement(inputSource);
			}
			throw new Exception("newElement ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		
		System.out.println("");
		System.out.println("- getSAXParserFactory usage");
		try {
			SAXParserFactory saxFactory = org.openspcoop2.utils.xml.XMLUtils.getInstance().getSAXParserFactory();
			saxFactory.setNamespaceAware(true);
			SAXParser saxParser = saxFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			try(ByteArrayInputStream bin = new ByteArrayInputStream(xmlMessage)){
				InputSource inputSource = new InputSource(bin);
				xmlReader.parse(inputSource);
			}
			throw new Exception("getSAXParserFactory usage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		
		System.out.println("");
		System.out.println("- getSchemaFactory usage");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(xsdSchema)){
				StreamSource streamSource = new StreamSource(bin);
				SchemaFactory factory = org.openspcoop2.utils.xml.XMLUtils.getInstance().getSchemaFactory();
				@SuppressWarnings("unused")
				Schema schema = factory.newSchema(streamSource);
			}
			throw new Exception("getSchemaFactory usage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		
		System.out.println("");
		System.out.println("- ValidatoreXSD usage (schemaFactory)");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(xsdSchema)){
				@SuppressWarnings("unused")
				org.openspcoop2.utils.xml.ValidatoreXSD validatoreXSD = new org.openspcoop2.utils.xml.ValidatoreXSD(LoggerWrapperFactory.getLogger(TestXXE.class),
						"org.apache.xerces.jaxp.validation.XMLSchemaFactory",
						bin);
			}
			throw new Exception("ValidatoreXSD usage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		
		System.out.println("");
		System.out.println("- ValidatoreXSD usage");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(xsdSchema)){
				@SuppressWarnings("unused")
				org.openspcoop2.utils.xml.ValidatoreXSD validatoreXSD = new org.openspcoop2.utils.xml.ValidatoreXSD(LoggerWrapperFactory.getLogger(TestXXE.class),
						bin);
			}
			throw new Exception("ValidatoreXSD usage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		
		System.out.println("");
		System.out.println("- ValidatoreXSD usage (multipleIS + schemaFactory)");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(xsdSchema)){
				@SuppressWarnings("unused")
				org.openspcoop2.utils.xml.ValidatoreXSD validatoreXSD = new org.openspcoop2.utils.xml.ValidatoreXSD(LoggerWrapperFactory.getLogger(TestXXE.class),
						"org.apache.xerces.jaxp.validation.XMLSchemaFactory",
						bin,bin,bin,bin);
			}
			throw new Exception("ValidatoreXSD usage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		
		System.out.println("");
		System.out.println("- ValidatoreXSD usage (multipleIS)");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(xsdSchema)){
				@SuppressWarnings("unused")
				org.openspcoop2.utils.xml.ValidatoreXSD validatoreXSD = new org.openspcoop2.utils.xml.ValidatoreXSD(LoggerWrapperFactory.getLogger(TestXXE.class),
						bin,bin,bin,bin);
			}
			throw new Exception("ValidatoreXSD usage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
		
		
		System.out.println("");
		System.out.println("- getTransformerFactory usage");
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(xslt)){
				// NOTA: non e' sul transformer factory che si interviene ma bensì sul source dell'xslt
				//StreamSource streamSource = new StreamSource(bin);
				Source xsltSource = new DOMSource(org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement(xslt));
				@SuppressWarnings("unused")
				Transformer trans = org.openspcoop2.utils.xml.XMLUtils.getInstance().getTransformerFactory().newTransformer(xsltSource);
			}
			throw new Exception("getTransformerFactory usage ok ?");
		}catch(Exception e) {
			if(e.getMessage().contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true")) {
				System.out.println("\t Eccezione attesa: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		
	}

}
