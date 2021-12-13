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


package org.openspcoop2.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPathFactory;

import org.openspcoop2.utils.UtilsException;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Classe utilizzabile per ottenere documenti xml
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractXMLUtils {

	// XERCES
	protected abstract DocumentBuilderFactory newDocumentBuilderFactory() throws XMLException;
	protected abstract DatatypeFactory newDatatypeFactory() throws XMLException;
	protected abstract javax.xml.parsers.SAXParserFactory newSAXParserFactory() throws XMLException;
//	protected abstract javax.xml.stream.XMLEventFactory newXMLEventFactory() throws XMLException;
	protected abstract SchemaFactory newSchemaFactory() throws XMLException;
	
	// XALAN
	protected abstract TransformerFactory newTransformerFactory() throws XMLException;
	protected abstract XPathFactory newXPathFactory() throws XMLException;
	
	
	// XERCES
	private DocumentBuilderFactory documentFactory = null;
	private DatatypeFactory datatypeFactory = null;
	private javax.xml.parsers.SAXParserFactory saxParserFactory = null;
//	private javax.xml.stream.XMLEventFactory xmlEventFactory = null;
	private SchemaFactory schemaFactory = null;
	
	// XALAN	
	private TransformerFactory transformerFactory = null;
	private XPathFactory xpathFactory = null;
	
	// OTHER
	private GregorianCalendar gregorianCalendar = null;
	
	
	
	// INIT - XERCES
	
	public synchronized void initDocumentBuilderFactory() throws XMLException {
		if(this.documentFactory==null){
			try {
				this.documentFactory = newDocumentBuilderFactory();
			} catch (Exception e) {
				throw new XMLException(e.getMessage(),e);
			}
			this.documentFactory.setNamespaceAware(true);
		}
	}
	
	public synchronized void initDatatypeFactory() throws XMLException {
		if(this.datatypeFactory==null){
			try {
				this.datatypeFactory = newDatatypeFactory();
			} catch (Exception e) {
				throw new XMLException(e.getMessage(),e);
			}
		}
	}
	
	public synchronized void initSAXParserFactory() throws XMLException {
		if(this.saxParserFactory==null){
			try {
				this.saxParserFactory = newSAXParserFactory();
			} catch (Exception e) {
				throw new XMLException(e.getMessage(),e);
			}
		}
	}
//	
//	public synchronized void initXMLEventFactory() throws XMLException {
//		if(this.xmlEventFactory==null){
//			try {
//				this.xmlEventFactory = newXMLEventFactory();
//			} catch (Exception e) {
//				throw new XMLException(e.getMessage(),e);
//			}
//		}
//	}
	
	public synchronized void initSchemaFactory() throws XMLException {
		if(this.schemaFactory==null){
			try {
				this.schemaFactory = newSchemaFactory();
			} catch (Exception e) {
				throw new XMLException(e.getMessage(),e);
			}
		}
	}

	// INIT - XALAN
	
	public synchronized void initTransformerFactory() throws XMLException {
		if(this.transformerFactory==null){
			this.transformerFactory = newTransformerFactory();
		}
	}
	
	public synchronized void initXPathFactory() throws XMLException {
		if(this.xpathFactory==null){
			this.xpathFactory = newXPathFactory();
		}
	}
	
	// INIT - OTHER
	
	public synchronized void initCalendarConverter() throws XMLException {
		try{
			this.initDatatypeFactory();
			this.gregorianCalendar = (GregorianCalendar) Calendar.getInstance(); 
		} catch (Exception e) {
			throw new XMLException(e.getMessage(),e);
		}
		
	}
	
	
	// GET - XERCES
	
	public DocumentBuilderFactory getDocumentBuilderFactory() throws XMLException {
		if(this.documentFactory==null){
			this.initDocumentBuilderFactory();
		}
		return this.documentFactory;
	}
	
	public DatatypeFactory getDatatypeFactory() throws XMLException {
		if(this.datatypeFactory==null){
			this.initDatatypeFactory();
		}
		return this.datatypeFactory;
	}
	
	public javax.xml.parsers.SAXParserFactory getSAXParserFactory() throws XMLException {
		if(this.saxParserFactory==null){
			this.initSAXParserFactory();
		}
		return this.saxParserFactory;
	}
//	
//	public javax.xml.stream.XMLEventFactory getXMLEventFactory() throws XMLException {
//		if(this.xmlEventFactory==null){
//			this.initXMLEventFactory();
//		}
//		return this.xmlEventFactory;
//	}
	
	public SchemaFactory getSchemaFactory() throws XMLException {
		if(this.schemaFactory==null){
			this.initSchemaFactory();
		}
		return this.schemaFactory;
	}
	
	
	// GET - XALAN

	public TransformerFactory getTransformerFactory() throws XMLException {
		if(this.transformerFactory==null){
			this.initTransformerFactory();
		}
		return this.transformerFactory;
	}
	
	public XPathFactory getXPathFactory() throws XMLException {
		if(this.xpathFactory==null){
			this.initXPathFactory();
		}
		return this.xpathFactory;
	}

	// GET - OTHER
	
	public XMLGregorianCalendar toGregorianCalendar(Date d) throws XMLException {
		if(this.datatypeFactory==null || this.gregorianCalendar==null){
			this.initCalendarConverter();
		}
		this.gregorianCalendar.setTime(d);
		return this.datatypeFactory.newXMLGregorianCalendar(this.gregorianCalendar);
	}



	// GET AS
	
	public Document getAsDocument(Element element) throws IOException, SAXException, ParserConfigurationException, TransformerException, XMLException{
		return this.newDocument(this.toByteArray(element));
	}
	public Document getAsDocument(Element element,ErrorHandler errorHandler) throws IOException, SAXException, ParserConfigurationException, TransformerException, XMLException{
		return this.newDocument(this.toByteArray(element),errorHandler);
	}
	public Document getAsDocument(Element element,EntityResolver entityResolver) throws IOException, SAXException, ParserConfigurationException, TransformerException, XMLException{
		return this.newDocument(this.toByteArray(element),entityResolver);
	}
	public Document getAsDocument(Element element,ErrorHandler errorHandler,EntityResolver entityResolver) throws IOException, SAXException, ParserConfigurationException, TransformerException, XMLException{
		return this.newDocument(this.toByteArray(element),errorHandler,entityResolver);
	}
	
	public Document getAsDocument(Node Node) throws IOException, SAXException, ParserConfigurationException, TransformerException, XMLException{
		return this.newDocument(this.toByteArray(Node));
	}
	public Document getAsDocument(Node Node,ErrorHandler errorHandler) throws IOException, SAXException, ParserConfigurationException, TransformerException, XMLException{
		return this.newDocument(this.toByteArray(Node),errorHandler);
	}
	public Document getAsDocument(Node Node,EntityResolver entityResolver) throws IOException, SAXException, ParserConfigurationException, TransformerException, XMLException{
		return this.newDocument(this.toByteArray(Node),entityResolver);
	}
	public Document getAsDocument(Node Node,ErrorHandler errorHandler,EntityResolver entityResolver) throws IOException, SAXException, ParserConfigurationException, TransformerException, XMLException{
		return this.newDocument(this.toByteArray(Node),errorHandler,entityResolver);
	}
	
	
	

	// NEW DOCUMENT

	public Document newDocument() throws IOException,SAXException,ParserConfigurationException,XMLException{
		return this.newDocument_engine(new XMLErrorHandler(),null);
	}
	public Document newDocument(ErrorHandler errorHandler) throws IOException,SAXException,ParserConfigurationException,XMLException{
		return this.newDocument_engine(errorHandler,null);
	}
	public Document newDocument(EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException,XMLException{
		return this.newDocument_engine(new XMLErrorHandler(),entityResolver);
	}
	public Document newDocument(ErrorHandler errorHandler,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException,XMLException{
		return this.newDocument_engine(errorHandler,entityResolver);
	}
	private Document newDocument_engine(ErrorHandler errorHandler,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException,XMLException{
		DocumentBuilder documentBuilder = this.getDocumentBuilderFactory().newDocumentBuilder();
		documentBuilder.setErrorHandler(errorHandler);
		if(entityResolver!=null){
			documentBuilder.setEntityResolver(entityResolver);
		}
		return documentBuilder.newDocument();
	}
	
	public Document newDocument(byte[] xml) throws IOException,SAXException,ParserConfigurationException,XMLException{
		return this.newDocument(xml,new XMLErrorHandler(),null);
	}
	public Document newDocument(byte[] xml,ErrorHandler errorHandler) throws IOException,SAXException,ParserConfigurationException,XMLException{
		return this.newDocument(xml,errorHandler,null);
	}
	public Document newDocument(byte[] xml,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException,XMLException{
		return this.newDocument(xml,new XMLErrorHandler(),entityResolver);
	}
	public Document newDocument(byte[] xml,ErrorHandler errorHandler,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException,XMLException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(xml);
			return this.newDocument(bin,errorHandler,entityResolver);
		}finally{
			if(bin!=null){
				try{
					bin.close();
				}catch(Exception eClose){}
			}
		}
	}

	public Document newDocument(InputStream is) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(is,new XMLErrorHandler(),null);
	}
	public Document newDocument(InputStream is,ErrorHandler errorHandler) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(is,errorHandler,null);
	}
	public Document newDocument(InputStream is,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(is,new XMLErrorHandler(),entityResolver);
	}
	public Document newDocument(InputStream is,ErrorHandler errorHandler,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		DocumentBuilder documentBuilder = this.getDocumentBuilderFactory().newDocumentBuilder();
		documentBuilder.setErrorHandler(errorHandler);
		if(entityResolver!=null)
			documentBuilder.setEntityResolver(entityResolver);
		return documentBuilder.parse(is);
	}

	public Document newDocument(File f) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(f,new XMLErrorHandler(),null);
	}
	public Document newDocument(File f,ErrorHandler errorHandler) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(f,errorHandler,null);
	}
	public Document newDocument(File f,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(f,new XMLErrorHandler(),entityResolver);
	}
	public Document newDocument(File f,ErrorHandler errorHandler,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		DocumentBuilder documentBuilder = this.getDocumentBuilderFactory().newDocumentBuilder();
		documentBuilder.setErrorHandler(errorHandler);
		if(entityResolver!=null){
			documentBuilder.setEntityResolver(entityResolver);
		}
		return documentBuilder.parse(f);
	}

	public Document newDocument(InputSource is) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(is,new XMLErrorHandler(),null);
	}
	public Document newDocument(InputSource is,ErrorHandler errorHandler) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(is,errorHandler,null);
	}
	public Document newDocument(InputSource is,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(is,new XMLErrorHandler(),entityResolver);
	}
	public Document newDocument(InputSource is,ErrorHandler errorHandler,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		DocumentBuilder documentBuilder = this.getDocumentBuilderFactory().newDocumentBuilder();
		documentBuilder.setErrorHandler(errorHandler);
		if(entityResolver!=null){
			documentBuilder.setEntityResolver(entityResolver);
		}
		return documentBuilder.parse(is);
	}



	// NEW ELEMENT

	public Element newElement() throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newElement_engine(new XMLErrorHandler(),null);
	}
	public Element newElement(ErrorHandler errorHandler) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newElement_engine(errorHandler,null);
	}
	public Element newElement(EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newElement_engine(new XMLErrorHandler(),entityResolver);
	}
	public Element newElement(ErrorHandler errorHandler,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newElement_engine(errorHandler,entityResolver);
	}
	private Element newElement_engine(ErrorHandler errorHandler,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		DocumentBuilder documentBuilder = this.getDocumentBuilderFactory().newDocumentBuilder();
		documentBuilder.setErrorHandler(errorHandler);
		if(entityResolver!=null){
			documentBuilder.setEntityResolver(entityResolver);
		}
		return documentBuilder.newDocument().getDocumentElement();
	}
	
	public Element newElement(byte[] xml) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(xml).getDocumentElement();
	}
	public Element newElement(byte[] xml,ErrorHandler errorHandler) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(xml,errorHandler).getDocumentElement();
	}
	public Element newElement(byte[] xml,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(xml,entityResolver).getDocumentElement();
	}
	public Element newElement(byte[] xml,ErrorHandler errorHandler,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(xml,errorHandler,entityResolver).getDocumentElement();
	}

	public Element newElement(InputStream is) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(is).getDocumentElement();
	}
	public Element newElement(InputStream is,ErrorHandler errorHandler) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(is,errorHandler).getDocumentElement();
	}
	public Element newElement(InputStream is,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(is,entityResolver).getDocumentElement();
	}
	public Element newElement(InputStream is,ErrorHandler errorHandler,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(is,errorHandler,entityResolver).getDocumentElement();
	}

	public Element newElement(File f) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(f).getDocumentElement();
	}
	public Element newElement(File f,ErrorHandler errorHandler) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(f,errorHandler).getDocumentElement();
	}
	public Element newElement(File f,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(f,entityResolver).getDocumentElement();
	}
	public Element newElement(File f,ErrorHandler errorHandler,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(f,errorHandler,entityResolver).getDocumentElement();
	}

	public Element newElement(InputSource is) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(is).getDocumentElement();
	}
	public Element newElement(InputSource is,ErrorHandler errorHandler) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(is,errorHandler).getDocumentElement();
	}
	public Element newElement(InputSource is,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(is,entityResolver).getDocumentElement();
	}
	public Element newElement(InputSource is,ErrorHandler errorHandler,EntityResolver entityResolver) throws IOException,SAXException,ParserConfigurationException, XMLException{
		return this.newDocument(is,errorHandler,entityResolver).getDocumentElement();
	}



	// TO BYTE ARRAY

	public byte[] toByteArray(Document doc) throws TransformerException,IOException, XMLException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.writeTo(doc,bout);
		bout.close();
		return bout.toByteArray();
	}
	public byte[] toByteArray(Document doc,boolean omitXMLDeclaration) throws TransformerException,IOException, XMLException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.writeTo(doc,bout,omitXMLDeclaration);
		bout.close();
		return bout.toByteArray();
	}
	public byte[] toByteArray(Element element) throws TransformerException,IOException, XMLException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.writeTo(element,bout);
		bout.close();
		return bout.toByteArray();
	}
	public byte[] toByteArray(Element element,boolean omitXMLDeclaration) throws TransformerException,IOException, XMLException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.writeTo(element,bout,omitXMLDeclaration);
		bout.close();
		return bout.toByteArray();
	}
	public byte[] toByteArray(Node node) throws TransformerException,IOException, XMLException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.writeTo(node,bout);
		bout.close();
		return bout.toByteArray();
	}
	public byte[] toByteArray(Node node,boolean omitXMLDeclaration) throws TransformerException,IOException, XMLException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.writeTo(node,bout,omitXMLDeclaration);
		bout.close();
		return bout.toByteArray();
	}


	// TO STRING

	public String toString(Document doc) throws TransformerException,IOException, XMLException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.writeTo(doc,bout);
		bout.close();
		return bout.toString();
	}
	public String toString(Document doc,boolean omitXMLDeclaration) throws TransformerException,IOException, XMLException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.writeTo(doc,bout,omitXMLDeclaration);
		bout.close();
		return bout.toString();
	}
	public String toString(Element element) throws TransformerException,IOException, XMLException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.writeTo(element,bout);
		bout.close();
		return bout.toString();
	}
	public String toString(Element element,boolean omitXMLDeclaration) throws TransformerException,IOException, XMLException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.writeTo(element,bout,omitXMLDeclaration);
		bout.close();
		return bout.toString();
	}
	public String toString(Node node) throws TransformerException,IOException, XMLException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.writeTo(node,bout);
		bout.close();
		return bout.toString();
	}
	public String toString(Node node,boolean omitXMLDeclaration) throws TransformerException,IOException, XMLException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.writeTo(node,bout,omitXMLDeclaration);
		bout.close();
		return bout.toString();
	}


	// WRITE TO

	public void writeTo(Document doc,OutputStream os)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, os, new XMLErrorListener(),false, null);
	}
	public void writeTo(Document doc,OutputStream os,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, os,errorListener,false, null);
	}
	public void writeTo(Document doc,OutputStream os,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, os, new XMLErrorListener(),omitXMLDeclaration, null);
	}
	public void writeTo(Document doc,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, os,errorListener,omitXMLDeclaration, null);
	}
	public void writeTo(Document doc,OutputStream os, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, os, new XMLErrorListener(),false,  charset);
	}
	public void writeTo(Document doc,OutputStream os,ErrorListener errorListener, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, os,errorListener,false,  charset);
	}
	public void writeTo(Document doc,OutputStream os,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, os, new XMLErrorListener(),omitXMLDeclaration,  charset);
	}
	public void writeTo(Document doc,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, os,errorListener,omitXMLDeclaration,  charset);
	}

	public void writeTo(Document doc,Writer writer)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, writer, new XMLErrorListener(),false, null);
	}
	public void writeTo(Document doc,Writer writer,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, writer,errorListener,false, null);
	}
	public void writeTo(Document doc,Writer writer,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, writer, new XMLErrorListener(), omitXMLDeclaration, null);
	}
	public void writeTo(Document doc,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, writer,errorListener, omitXMLDeclaration, null);
	}
	public void writeTo(Document doc,Writer writer, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, writer, new XMLErrorListener(),false, charset);
	}
	public void writeTo(Document doc,Writer writer,ErrorListener errorListener, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, writer,errorListener,false, charset);
	}
	public void writeTo(Document doc,Writer writer,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, writer, new XMLErrorListener(), omitXMLDeclaration, charset);
	}
	public void writeTo(Document doc,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, writer,errorListener, omitXMLDeclaration, charset);
	}

	public void writeTo(Document doc,File file)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, file, new XMLErrorListener(),false, null);
	}
	public void writeTo(Document doc,File file,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, file,errorListener,false, null);
	}
	public void writeTo(Document doc,File file,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, file, new XMLErrorListener(),omitXMLDeclaration, null);
	}
	public void writeTo(Document doc,File file,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, file,errorListener,omitXMLDeclaration, null);
	}
	public void writeTo(Document doc,File file, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, file, new XMLErrorListener(),false, charset);
	}
	public void writeTo(Document doc,File file,ErrorListener errorListener, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, file,errorListener,false, charset);
	}
	public void writeTo(Document doc,File file,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, file, new XMLErrorListener(),omitXMLDeclaration, charset);
	}
	public void writeTo(Document doc,File file,ErrorListener errorListener,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, file,errorListener,omitXMLDeclaration, charset);
	}

	public void writeTo(Element element,OutputStream os)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, os, new XMLErrorListener(),false, null);
	}
	public void writeTo(Element element,OutputStream os,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, os,errorListener,false, null);
	}
	public void writeTo(Element element,OutputStream os,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, os, new XMLErrorListener(),omitXMLDeclaration, null);
	}
	public void writeTo(Element element,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, os,errorListener,omitXMLDeclaration, null);
	}
	public void writeTo(Element element,OutputStream os, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, os, new XMLErrorListener(),false, charset);
	}
	public void writeTo(Element element,OutputStream os,ErrorListener errorListener, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, os,errorListener,false, charset);
	}
	public void writeTo(Element element,OutputStream os,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, os, new XMLErrorListener(),omitXMLDeclaration, charset);
	}
	public void writeTo(Element element,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, os,errorListener,omitXMLDeclaration, charset);
	}

	public void writeTo(Element element,Writer writer)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, writer, new XMLErrorListener(),false, null);
	}
	public void writeTo(Element element,Writer writer,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, writer,errorListener,false, null);
	}
	public void writeTo(Element element,Writer writer,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, writer, new XMLErrorListener(), omitXMLDeclaration, null);
	}
	public void writeTo(Element element,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, writer,errorListener, omitXMLDeclaration, null);
	}
	public void writeTo(Element element,Writer writer, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, writer, new XMLErrorListener(),false, charset);
	}
	public void writeTo(Element element,Writer writer,ErrorListener errorListener, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, writer,errorListener,false, charset);
	}
	public void writeTo(Element element,Writer writer,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, writer, new XMLErrorListener(), omitXMLDeclaration, charset);
	}
	public void writeTo(Element element,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, writer,errorListener, omitXMLDeclaration, charset);
	}

	public void writeTo(Element element,File file)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, file, new XMLErrorListener(),false, null);
	}
	public void writeTo(Element element,File file,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, file,errorListener,false, null);
	}
	public void writeTo(Element element,File file,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, file, new XMLErrorListener(),omitXMLDeclaration, null);
	}
	public void writeTo(Element element,File file,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, file,errorListener,omitXMLDeclaration, null);
	}
	public void writeTo(Element element,File file, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, file, new XMLErrorListener(),false, charset);
	}
	public void writeTo(Element element,File file,ErrorListener errorListener, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, file,errorListener,false, charset);
	}
	public void writeTo(Element element,File file,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, file, new XMLErrorListener(),omitXMLDeclaration, charset);
	}
	public void writeTo(Element element,File file,ErrorListener errorListener,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, file,errorListener,omitXMLDeclaration, charset);
	}

	public void writeTo(Node node,OutputStream os)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,os, new XMLErrorListener(),false, null);
	}
	public void writeTo(Node node,OutputStream os,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,os,errorListener,false, null);
	}
	public void writeTo(Node node,OutputStream os,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,os, new XMLErrorListener(),omitXMLDeclaration, null);
	}
	public void writeTo(Node node,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,os,errorListener,omitXMLDeclaration, null);
	}
	public void writeTo(Node node,OutputStream os, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,os, new XMLErrorListener(),false, charset);
	}
	public void writeTo(Node node,OutputStream os,ErrorListener errorListener, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,os,errorListener,false, charset);
	}
	public void writeTo(Node node,OutputStream os,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,os, new XMLErrorListener(),omitXMLDeclaration, charset);
	}
	public void writeTo(Node node,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,os,errorListener,omitXMLDeclaration, charset);
	}

	public void writeTo(Node node,Writer writer)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,writer, new XMLErrorListener(),false, null);
	}
	public void writeTo(Node node,Writer writer,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,writer,errorListener,false, null);
	}
	public void writeTo(Node node,Writer writer,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,writer, new XMLErrorListener(),omitXMLDeclaration, null);
	}
	public void writeTo(Node node,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,writer,errorListener,omitXMLDeclaration, null);
	}
	public void writeTo(Node node,Writer writer, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,writer, new XMLErrorListener(),false, charset);
	}
	public void writeTo(Node node,Writer writer,ErrorListener errorListener, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,writer,errorListener,false, charset);
	}
	public void writeTo(Node node,Writer writer,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,writer, new XMLErrorListener(),omitXMLDeclaration, charset);
	}
	public void writeTo(Node node,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,writer,errorListener,omitXMLDeclaration, charset);
	}

	public void writeTo(Node node,File file)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,file, new XMLErrorListener(),false, null);
	}
	public void writeTo(Node node,File file,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,file,errorListener,false, null);
	}
	public void writeTo(Node node,File file,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,file, new XMLErrorListener(),omitXMLDeclaration, null);
	}
	public void writeTo(Node node,File file,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,file,errorListener,omitXMLDeclaration, null);
	}
	public void writeTo(Node node,File file, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,file, new XMLErrorListener(),false, charset);
	}
	public void writeTo(Node node,File file,ErrorListener errorListener, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,file,errorListener,false, charset);
	}
	public void writeTo(Node node,File file,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,file, new XMLErrorListener(),omitXMLDeclaration, charset);
	}
	public void writeTo(Node node,File file,ErrorListener errorListener,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,file,errorListener,omitXMLDeclaration, charset);
	}

	
	private void writeNodeTo(Node node,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException, XMLException{
		Source source = new DOMSourceFix(node);
		StreamResult result = new StreamResult(os);
		Transformer transformer = getTransformerFactory().newTransformer();
		if(omitXMLDeclaration)
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
		if(charset!=null) {
			transformer.setOutputProperty(OutputKeys.ENCODING, charset);
		}
		transformer.setErrorListener(errorListener);
		transformer.transform(source, result);
		os.flush();
	}
	private void writeNodeTo(Node node,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		Source source = new DOMSourceFix(node);
		StreamResult result = new StreamResult(writer);
		Transformer transformer = getTransformerFactory().newTransformer();
		if(omitXMLDeclaration)
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
		if(charset!=null) {
			transformer.setOutputProperty(OutputKeys.ENCODING, charset);
		}
		transformer.setErrorListener(errorListener);
		transformer.transform(source, result);
		writer.flush();
	}
	private void writeNodeTo(Node node,File file,ErrorListener errorListener,boolean omitXMLDeclaration, String charset)throws TransformerException,IOException, XMLException{
		Source source = new DOMSourceFix(node);
		StreamResult result = new StreamResult(file);
		Transformer transformer = getTransformerFactory().newTransformer();
		if(omitXMLDeclaration)
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
		if(charset!=null) {
			transformer.setOutputProperty(OutputKeys.ENCODING, charset);
		}
		transformer.setErrorListener(errorListener);
		transformer.transform(source, result);
	}

	
	
	
	// ELIMINAZIONE DICHIARAZIONE XML
	
	@Deprecated
	public byte[] eraserXML(byte[] xml){
		return this.eraserXML(new String(xml)).getBytes();
	}
	@Deprecated
	public String eraserXML(String xml){
		String tmp = xml.trim();
		if(tmp.startsWith("<?xml")){
			int indexOf = tmp.indexOf(">");
			return tmp.substring(indexOf+1);
		}else{
			return xml;
		}
	}
	
	/**
	 * Metodo che si occupa di effettuare l'eliminazione degli xsd:type String
	 *
	 * @deprecated  utility che elimina gli xsd type
	 * @param xml Xml su cui effettuare la pulizia dell'header.
	 * @return byte[] contenente un xml 'pulito'.
	 * 
	 */
	@Deprecated public byte[] eraserXsiType(byte[] xml) throws UtilsException{

		ByteArrayOutputStream cleanXML = null;
		try{
			String eraserString = " xsi:type=\"xsd:string\"";
			cleanXML = new ByteArrayOutputStream();

			// Busta
			for(int i=0; i<xml.length ; ){

				if(xml[i] == ' '){

					// String
					if(i+eraserString.length() < xml.length){ 
						StringBuilder test = new StringBuilder();
						for(int k=0 ; k < eraserString.length(); k++){
							test.append((char)xml[i+k]);
						}
						if(test.toString().equals(eraserString)){
							i = i + eraserString.length();
							continue;
						}
					}

					cleanXML.write(xml[i]);
					i++;

				}else{
					cleanXML.write(xml[i]);
					i++;
				}

			}

			byte [] risultato = cleanXML.toByteArray();
			cleanXML.close();
			return risultato;

		} catch(Exception e) {
			try{
				if(cleanXML!=null)
					cleanXML.close();
			}catch(Exception eis){}
			throw new UtilsException("Utilities.eraserType error "+e.getMessage(),e);
		}
	}  
	
	
	
	// IS
	
	public boolean isDocument(byte[]xml){
		try{
			return this.newDocument(xml)!=null;
		}catch(Exception e){
			return false;
		}
	}
	public boolean isElement(byte[]xml){
		try{
			return this.newElement(xml)!=null;
		}catch(Exception e){
			return false;
		}
	}

	

	
	// ATTRIBUTE
	
	public String getAttributeValue(Node n,String attrName){
		NamedNodeMap att = n.getAttributes();
		if(att!=null){
			Node nA = att.getNamedItem(attrName);
			if(nA!=null)
				return nA.getNodeValue();
		}
		return null;
	}
	
	public void removeAttribute(Attr attr, Element src){
		
		// NOTA:
		//	 attr.getName ritorna anche il prefisso se c'è a differenza di attr.getLocalName
		
		if("http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI())){
			if("xmlns".equals(attr.getName())){
				if(attr.getClass().getName().contains("org.apache.axiom.")){
					// axiom
					//System.out.println("REMOVE NS SPECIAL AXIOM XMLNS ["+attr.getClass().getName()+"]");
					src.removeAttributeNS(attr.getNamespaceURI(), "");
				}
				else{
					// cxf
					//System.out.println("REMOVE NS SPECIAL CXF XMLNS ["+attr.getClass().getName()+"]");
					src.removeAttributeNS(attr.getNamespaceURI(), attr.getName());
				}
			}
			else if(attr.getNamespaceURI()!=null){
				//System.out.println("REMOVE NS XMLNS");
				src.removeAttributeNS(attr.getNamespaceURI(), attr.getLocalName()); // Deve essere usato localName per cxf
			}
		}
		else{
			//System.out.println("REMOVE NORMAL");
			src.removeAttribute(attr.getName());
		}
	}
	
	public void addAttribute(Attr attr, Element src){
		
		// NOTA:
		//	 attr.getName ritorna anche il prefisso se c'è a differenza di attr.getLocalName
		
		if("http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI())){
			if("xmlns".equals(attr.getLocalName())){
				//System.out.println("ADD NS SPECIAL XMLNS");
				src.setAttribute(attr.getName(), attr.getValue());
			}
			else{
				//System.out.println("ADD NS XMLNS");
				src.setAttributeNS(attr.getNamespaceURI(), attr.getName(), attr.getValue());
			}
		}
		else{
			//System.out.println("ADD NORMAL ATTRIBUTE");
			src.setAttribute(attr.getName(), attr.getValue());
		}
	}


	
	
	
	
	
	// NAMESPACE
	
	public Map<String, String> getNamespaceDeclaration(Node n){
		NamedNodeMap map = n.getAttributes();
		Map<String, String> namespaces = new HashMap<String, String>();
		if(map!=null){
			for (int i = 0; i < map.getLength(); i++) {
				Node attribute = map.item(i);
				//System.out.println("ATTRIBUTE["+i+"] ("+attribute.getClass().getName()+") name["+attribute.getLocalName()+"] uri["+attribute.getNamespaceURI()+"] value["+attribute.getNodeValue()+"] prefix["+attribute.getPrefix()+"]");
				
				if(attribute!=null && (attribute instanceof  Attr) ){
					Attr a = (Attr) attribute;
					
					//System.out.println("ATTRIBUTE["+i+"]  INSTACE name("+a.getName()+") value("+a.getValue()+") uri("+a.getBaseURI()+") prefix("+a.getPrefix()+") localName("+a.getLocalName()+")");
					// INSTACE name(xmlns) value(www.namespace.org) 
					// INSTACE name(xmlns:pp) value(www.namespace2.org) 
					
					String prefix = a.getName();
					if(prefix!=null && (prefix.startsWith("xmlns") || prefix.equals("xmlns"))){
						if(prefix.contains(":")){
							prefix = prefix.split(":")[1];
						}
						else{
							prefix = "";
						}
						namespaces.put(prefix, a.getValue());
					}
				}
			}
		}
		return namespaces;
	}
	
	public String findNamespaceByPrefix(String prefix,Node node) {
		return this.findNamespaceByPrefix(prefix, node, node);
	}
	public String findNamespaceByPrefix(String prefix,Node node, Node parentNode) {
		if(node==null) {
			return null;
		}
		
//		System.out.println("[{"+node.getNamespaceURI()+"}"+node.getLocalName()+"] CERCO NAMESPACE ...");
		
		// cerco tra le definizioni del nodo
		NamedNodeMap nn = node.getAttributes();
		if(nn!=null && nn.getLength()>0) {
			for (int k = 0; k < nn.getLength(); k++) {
				Node nAttr = nn.item(k);
				if(nAttr instanceof Attr) {
					Attr attr = (Attr) nAttr;
					String prefixAttr = attr.getName();
					if(prefixAttr!=null && (prefixAttr.startsWith("xmlns") || prefix.equals("prefixAttr"))){
						if(prefixAttr.contains(":")){
							prefixAttr = prefixAttr.split(":")[1];
						}
						else{
							prefixAttr = "";
						}
					}
//					System.out.println("\t CONFRONTO ["+prefix+"]==["+prefixAttr+"] ["+attr.getName()+"] ["+attr.getLocalName()+"] ["+attr.getValue()+"] ["+attr.getNamespaceURI()+"] ...");
					if(prefix.equals(prefixAttr)) {
//						System.out.println("TROVATO PREFIX ["+prefix+"] in node [{"+node.getNamespaceURI()+"}"+node.getLocalName()+
//								"], namespace is ["+attr.getNamespaceURI()+"]["+attr.getValue()+"]");
						// TROVATO PREFIX [ns10] in node [{http://www.xxxx}ElementName], namespace is [http://www.w3.org/2000/xmlns/][http://namespaceApplicativo]
						return attr.getValue();
					}
				}
			}
		}
		
		// Sono al padre non salgo ulteriormente
		if(node.isSameNode(parentNode)) {
//			System.out.println("\tRicerca ["+prefix+"] RAGGIUNTO IL PADRE [{"+parentNode.getNamespaceURI()+"}"+parentNode.getLocalName()+"]");
			return null;
		}
		// cerco nel padre a meno che non ho raggiunto il parentNode
		Node p = node.getParentNode();
		if(p==null) {
			return null;
		}

		return this.findNamespaceByPrefix(prefix, p, parentNode);
	}
	
	public void addNamespaceDeclaration(Map<String, String> namespace, Element destNode){
		if(namespace!=null && namespace.size()>0){
			Map<String, String> declarationNamespacesDestNode = this.getNamespaceDeclaration(destNode);
//			if(declarationNamespacesDestNode.size()>0){
//				Enumeration<String> decSchema = declarationNamespacesDestNode.keys();
//				while (decSchema.hasMoreElements()) {
//					String dec = (String) decSchema.nextElement();
//					System.out.println("TROVATO ORIGINALE ["+dec+"]=["+declarationNamespacesDestNode.get(dec)+"]");
//				}
//			}
			for (String dec : namespace.keySet()) {
				//System.out.println("TROVATO ["+dec+"]=["+namespace.get(dec)+"]");
				if(declarationNamespacesDestNode.containsKey(dec)==false){
					//System.out.println("AGGIUNGO IN SCHEMA");
					String name = "xmlns:"+dec;
					if("".equals(dec)){
						name = "xmlns";
					}
					destNode.setAttributeNS("http://www.w3.org/2000/xmlns/",name,namespace.get(dec));
				}
			}
		}
	}
	
	public void removeNamespaceDeclaration(Map<String, String> namespace, Element destNode){
		
		// TODO: Da verificare, non ancora utilizzato
		if(namespace!=null && namespace.size()>0){
			for (String dec : namespace.keySet()) {
				//System.out.println("RIMUOVO ["+dec+"]=["+namespace.get(dec)+"]");
				String name = "xmlns:"+dec;
				if("".equals(dec)){
					name = "xmlns";
				}
				destNode.removeAttributeNS("http://www.w3.org/2000/xmlns/", name);
			}
		}
	}
	
	
	
	
	
	
	// NAMESPACE XSI TYPE
	
	private static final String XMLSCHEMA_INSTANCE_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
	private static final String XMLSCHEMA_INSTANCE_LOCAL_NAME_TYPE = "type";
	
	public void addNamespaceXSITypeIfNotExists(Node node, DynamicNamespaceContext dnc, boolean deep) {
		this.addNamespaceXSITypeIfNotExists(node, dnc, deep, node);
	}
	private void addNamespaceXSITypeIfNotExists(Node node, DynamicNamespaceContext dnc, boolean deep, Node parentNode) {
		
		if(node==null) {
			return;
		}
		
		
		
		// Gestisco attributi
		if(node instanceof Element) {
			Element element = (Element) node;
			NamedNodeMap nn = element.getAttributes();
			if(nn!=null && nn.getLength()>0) {
				List<String> prefixXSIType = new ArrayList<>();
				for (int k = 0; k < nn.getLength(); k++) {
					Node nAttr = nn.item(k);
					//System.out.println("ATTR ["+nAttr.getClass().getName()+"]");
					if(nAttr instanceof Attr) {
						Attr attr = (Attr) nAttr;
						//System.out.println("\t name["+attr.getName()+"] value["+attr.getValue()+"] namespace["+attr.getNamespaceURI()+"] prefix["+attr.getPrefix()+"]");
						// name[xsi:type] value[ns10:XXXXType] namespace[http://www.w3.org/2001/XMLSchema-instance] prefix[xsi]
						if(AbstractXMLUtils.XMLSCHEMA_INSTANCE_NAMESPACE.equals(attr.getNamespaceURI())) {
							String prefix = attr.getPrefix();
							String nameAtteso = prefix+":"+AbstractXMLUtils.XMLSCHEMA_INSTANCE_LOCAL_NAME_TYPE;
							if(prefix==null || "".equals(prefix)) {
								nameAtteso = AbstractXMLUtils.XMLSCHEMA_INSTANCE_LOCAL_NAME_TYPE;
							}
							if(nameAtteso.equals(attr.getName())) {
								if(attr.getValue()!=null && attr.getValue().contains(":")) {
									prefixXSIType.add(attr.getValue());
								}
							}
						}
					}
				}
				if(prefixXSIType.size()>0) {
					for (String prefixValue : prefixXSIType) {
						String [] tmp = prefixValue.split(":");
						if(tmp!=null && tmp.length==2) {
							String prefix = tmp[0];
							//String localName = tmp[1];
							
//							System.out.println("\n\n\n =============================================================");
//							System.out.println("[{"+node.getNamespaceURI()+"}"+node.getLocalName()+"] prefix ["+prefix+"]");
//							System.out.println("=============================================================\n");
							
							//System.out.println("VERIFICO PRESENZA NAMESPACE PER ["+prefixValue+"] prefix["+prefix+"] localName["+localName+"]");
							String namespaceEsistente = this.findNamespaceByPrefix(prefix, element, parentNode);
							boolean foundNamespace = namespaceEsistente!=null;
							if(!foundNamespace) {
								String namespace = dnc.getNamespaceURI(prefix);
//								System.out.println("[{"+node.getNamespaceURI()+"}"+node.getLocalName()+"] NAMESPACE AGGIUNTO PERCHE' NON TROVATO per ["+prefix+"]=["+namespace+"]");
								element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:"+prefix, namespace);
							}
//							else {
//								System.out.println("[{"+node.getNamespaceURI()+"}"+node.getLocalName()+"] ESISTE NAMESPACE ["+namespaceEsistente+"] PER PREFIX: "+prefix);
//							}
						}
					}
				}
			}
		}
		
		// Vado in ricorsione sugli elementi figli
		if(deep) {
			List<Node> childList = this.getNotEmptyChildNodes(node, false);
			if(childList!=null && childList.size()>0) {
				for (Node child : childList) {
					this.addNamespaceXSITypeIfNotExists(child, dnc, deep, parentNode);
				}
			}
		}
	}
	

	
	
	
	
	
	
	
	// UTILITIES
	
	public List<Node> getNotEmptyChildNodes(Node e){
		return getNotEmptyChildNodes(e, true);
	}
	public List<Node> getNotEmptyChildNodes(Node e, boolean consideraTextNotEmptyAsNode){
		NodeList nl = e.getChildNodes();
		List<Node> vec = new ArrayList<Node>();
		if(nl!=null){
			for(int index = 0 ; index<nl.getLength(); index++){
				Node n = nl.item(index);
				if(n instanceof Text){
					if(consideraTextNotEmptyAsNode){
						if (((Text) nl.item(index)).getData().trim().length() == 0) { 
							continue;
						}
					}else{
						continue;
					}
				}
				else if (n instanceof Comment) { 
					continue;
				}
				vec.add(nl.item(index));
			}
		}
		return vec;
	}
	
	public Node getFirstNotEmptyChildNode(Node e){
		return getFirstNotEmptyChildNode(e, true);
	}
	public Node getFirstNotEmptyChildNode(Node e, boolean consideraTextNotEmptyAsNode){
		NodeList nl = e.getChildNodes();
		if(nl!=null){
			for(int index = 0 ; index<nl.getLength(); index++){
				Node n = nl.item(index);
				if(n instanceof Text){
					if(consideraTextNotEmptyAsNode){
						if (((Text) nl.item(index)).getData().trim().length() == 0) { 
							continue;
						}
					}else{
						continue;
					}
				}
				else if (nl.item(index) instanceof Comment) { 
					continue;
				}
				return nl.item(index);
			}
		}
		return null;
	}
	
	public boolean matchLocalName(Node nodo,String nodeName,String prefix,String namespace){
		if(nodo==null)
			return false;
		if(nodo.getNodeName()==null)
			return false;
		// Il nodo possiede il prefisso atteso
		if(nodo.getNodeName().equals(prefix+nodeName))
			return true;
		// Il nodo puo' ridefinire il prefisso ridefinendo il namespace
		String namespaceNodo = nodo.getNamespaceURI();
		if(namespaceNodo!=null && namespaceNodo.equals(namespace)){
			String xmlns = nodo.getPrefix();
			if(xmlns == null){ 
				xmlns = "";
			}else if(!xmlns.equals("")){
				xmlns = xmlns + ":";
			}
			if(nodo.getNodeName().equals(xmlns+nodeName))
				return true;
		}
		return false;
	} 
	
	public Node getAttributeNode(Node node,String attributeName){
		if (node == null)
		{
			return null;
		}
		NamedNodeMap map = node.getAttributes();
		if(map==null || map.getLength()==0){
			return null;
		}
		else{
			return map.getNamedItem(attributeName);
		}
	}
	public Node getQualifiedAttributeNode(Node node,String attributeName,String namespace){
		if (node == null)
		{
			return null;
		}
		NamedNodeMap map = node.getAttributes();
		if(map==null || map.getLength()==0){
			return null;
		}
		else{
			return map.getNamedItemNS(namespace, attributeName);
		}
	}
	
}
