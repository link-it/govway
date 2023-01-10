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
package org.openspcoop2.message.xml;

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

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
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


	
	public static void main(String [] args) throws Exception{
		test();
	}
	public static void test() throws Exception{
		
		// Default è true
		// AbstractXMLUtils.DISABLE_DTDs=false;
		
		OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		
		System.out.println("- newDocument(byte[])");
		try {
			org.openspcoop2.message.xml.MessageXMLUtils.getInstance(factory).newDocument(org.openspcoop2.utils.xml.test.TestXXE.xmlMessage);
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
			try(ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.xml.test.TestXXE.xmlMessage)){
				org.openspcoop2.message.xml.MessageXMLUtils.getInstance(factory).newDocument(bin);
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
			FileSystemUtilities.writeFile(file, org.openspcoop2.utils.xml.test.TestXXE.xmlMessage);
			org.openspcoop2.message.xml.MessageXMLUtils.getInstance(factory).newDocument(file);
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
			try(ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.xml.test.TestXXE.xmlMessage)){
				InputSource inputSource = new InputSource(bin);
				org.openspcoop2.message.xml.MessageXMLUtils.getInstance(factory).newDocument(inputSource);
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
			org.openspcoop2.message.xml.MessageXMLUtils.getInstance(factory).newElement(org.openspcoop2.utils.xml.test.TestXXE.xmlMessage);
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
			try(ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.xml.test.TestXXE.xmlMessage)){
				org.openspcoop2.message.xml.MessageXMLUtils.getInstance(factory).newElement(bin);
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
			FileSystemUtilities.writeFile(file, org.openspcoop2.utils.xml.test.TestXXE.xmlMessage);
			org.openspcoop2.message.xml.MessageXMLUtils.getInstance(factory).newElement(file);
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
			try(ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.xml.test.TestXXE.xmlMessage)){
				InputSource inputSource = new InputSource(bin);
				org.openspcoop2.message.xml.MessageXMLUtils.getInstance(factory).newElement(inputSource);
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
			SAXParserFactory saxFactory = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(factory).getSAXParserFactory();
			saxFactory.setNamespaceAware(true);
			SAXParser saxParser = saxFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			try(ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.xml.test.TestXXE.xmlMessage)){
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
			try(ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.xml.test.TestXXE.xsdSchema)){
				StreamSource streamSource = new StreamSource(bin);
				SchemaFactory schemafactory = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(factory).getSchemaFactory();
				@SuppressWarnings("unused")
				Schema schema = schemafactory.newSchema(streamSource);
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
			try(ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.xml.test.TestXXE.xsdSchema)){
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
			try(ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.xml.test.TestXXE.xsdSchema)){
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
			try(ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.xml.test.TestXXE.xsdSchema)){
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
			try(ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.xml.test.TestXXE.xsdSchema)){
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
			try(ByteArrayInputStream bin = new ByteArrayInputStream(org.openspcoop2.utils.xml.test.TestXXE.xslt)){
				// NOTA: non e' sul transformer factory che si interviene ma bensì sul source dell'xslt
				//StreamSource streamSource = new StreamSource(bin);
				Source xsltSource = new DOMSource(org.openspcoop2.message.xml.MessageXMLUtils.getInstance(factory).newElement(org.openspcoop2.utils.xml.test.TestXXE.xslt));
				@SuppressWarnings("unused")
				Transformer trans = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(factory).getTransformerFactory().newTransformer(xsltSource);
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
