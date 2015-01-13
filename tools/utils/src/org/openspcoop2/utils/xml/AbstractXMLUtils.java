/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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


package org.openspcoop2.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openspcoop2.utils.UtilsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
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

	
	protected abstract DocumentBuilderFactory newDocumentBuilderFactory() throws XMLException;
	protected abstract TransformerFactory newTransformerFactory() throws XMLException;
	protected abstract DatatypeFactory newDatatypeFactory() throws XMLException;
	
	
	private DocumentBuilderFactory documentFactory = null;

	private TransformerFactory transformerFactory = null;

	private DatatypeFactory datatypeFactory = null;
	
	private GregorianCalendar gregorianCalendar = null;
	
	public synchronized void initBuilder() throws XMLException {
		if(this.documentFactory==null){
			try {
				this.documentFactory = newDocumentBuilderFactory();
			} catch (Exception e) {
				throw new XMLException(e.getMessage(),e);
			}
			this.documentFactory.setNamespaceAware(true);
		}
		DocumentBuilderFactory.newInstance().getClass().getName();
	}

	public synchronized void initTransformer() throws XMLException {
		if(this.transformerFactory==null){
			this.transformerFactory = newTransformerFactory();
		}
	}
	
	public synchronized void initCalendarConverter() throws XMLException {
		try{
			this.datatypeFactory = newDatatypeFactory();
			this.gregorianCalendar = (GregorianCalendar) Calendar.getInstance(); 
		} catch (Exception e) {
			throw new XMLException(e.getMessage(),e);
		}
		
	}
	
	public DocumentBuilderFactory getDocumentFactory() throws XMLException {
		if(this.documentFactory==null){
			this.initBuilder();
		}
		return this.documentFactory;
	}

	public TransformerFactory getTransformerFactory() throws XMLException {
		if(this.transformerFactory==null){
			this.initTransformer();
		}
		return this.transformerFactory;
	}

	public DatatypeFactory getDatatypeFactory() {
		return this.datatypeFactory;
	}
	
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
		if(this.documentFactory==null){
			this.initBuilder();
		}
		DocumentBuilder documentBuilder = this.documentFactory.newDocumentBuilder();
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
		if(this.documentFactory==null){
			this.initBuilder();
		}
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
		if(this.documentFactory==null){
			this.initBuilder();
		}	
		DocumentBuilder documentBuilder = this.documentFactory.newDocumentBuilder();
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
		if(this.documentFactory==null){
			this.initBuilder();
		}
		DocumentBuilder documentBuilder = this.documentFactory.newDocumentBuilder();
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
		if(this.documentFactory==null){
			this.initBuilder();
		}
		DocumentBuilder documentBuilder = this.documentFactory.newDocumentBuilder();
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
		if(this.documentFactory==null){
			this.initBuilder();
		}
		DocumentBuilder documentBuilder = this.documentFactory.newDocumentBuilder();
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
		this.writeNodeTo(doc, os, new XMLErrorListener(),false);
	}
	public void writeTo(Document doc,OutputStream os,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, os,errorListener,false);
	}
	public void writeTo(Document doc,OutputStream os,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, os, new XMLErrorListener(),omitXMLDeclaration);
	}
	public void writeTo(Document doc,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, os,errorListener,omitXMLDeclaration);
	}

	public void writeTo(Document doc,Writer writer)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, writer, new XMLErrorListener(),false);
	}
	public void writeTo(Document doc,Writer writer,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, writer,errorListener,false);
	}
	public void writeTo(Document doc,Writer writer,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, writer, new XMLErrorListener(), omitXMLDeclaration);
	}
	public void writeTo(Document doc,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, writer,errorListener, omitXMLDeclaration);
	}

	public void writeTo(Document doc,File file)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, file, new XMLErrorListener(),false);
	}
	public void writeTo(Document doc,File file,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, file,errorListener,false);
	}
	public void writeTo(Document doc,File file,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, file, new XMLErrorListener(),omitXMLDeclaration);
	}
	public void writeTo(Document doc,File file,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(doc, file,errorListener,omitXMLDeclaration);
	}

	public void writeTo(Element element,OutputStream os)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, os, new XMLErrorListener(),false);
	}
	public void writeTo(Element element,OutputStream os,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, os,errorListener,false);
	}
	public void writeTo(Element element,OutputStream os,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, os, new XMLErrorListener(),omitXMLDeclaration);
	}
	public void writeTo(Element element,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, os,errorListener,omitXMLDeclaration);
	}

	public void writeTo(Element element,Writer writer)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, writer, new XMLErrorListener(),false);
	}
	public void writeTo(Element element,Writer writer,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, writer,errorListener,false);
	}
	public void writeTo(Element element,Writer writer,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, writer, new XMLErrorListener(), omitXMLDeclaration);
	}
	public void writeTo(Element element,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, writer,errorListener, omitXMLDeclaration);
	}

	public void writeTo(Element element,File file)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, file, new XMLErrorListener(),false);
	}
	public void writeTo(Element element,File file,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, file,errorListener,false);
	}
	public void writeTo(Element element,File file,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, file, new XMLErrorListener(),omitXMLDeclaration);
	}
	public void writeTo(Element element,File file,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(element, file,errorListener,omitXMLDeclaration);
	}

	public void writeTo(Node node,OutputStream os)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,os, new XMLErrorListener(),false);
	}
	public void writeTo(Node node,OutputStream os,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,os,errorListener,false);
	}
	public void writeTo(Node node,OutputStream os,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,os, new XMLErrorListener(),omitXMLDeclaration);
	}
	public void writeTo(Node node,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,os,errorListener,omitXMLDeclaration);
	}

	public void writeTo(Node node,Writer writer)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,writer, new XMLErrorListener(),false);
	}
	public void writeTo(Node node,Writer writer,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,writer,errorListener,false);
	}
	public void writeTo(Node node,Writer writer,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,writer, new XMLErrorListener(),omitXMLDeclaration);
	}
	public void writeTo(Node node,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,writer,errorListener,omitXMLDeclaration);
	}

	public void writeTo(Node node,File file)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,file, new XMLErrorListener(),false);
	}
	public void writeTo(Node node,File file,ErrorListener errorListener)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,file,errorListener,false);
	}
	public void writeTo(Node node,File file,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,file, new XMLErrorListener(),omitXMLDeclaration);
	}
	public void writeTo(Node node,File file,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		this.writeNodeTo(node,file,errorListener,omitXMLDeclaration);
	}

	
	private void writeNodeTo(Node node,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException, XMLException{
		if(this.transformerFactory==null){
			this.initTransformer();
		}
		Source source = new DOMSource(node);
		StreamResult result = new StreamResult(os);
		Transformer transformer = this.transformerFactory.newTransformer();
		if(omitXMLDeclaration)
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
		transformer.setErrorListener(errorListener);
		transformer.transform(source, result);
		os.flush();
	}
	private void writeNodeTo(Node node,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		if(this.transformerFactory==null){
			this.initTransformer();
		}
		Source source = new DOMSource(node);
		StreamResult result = new StreamResult(writer);
		Transformer transformer = this.transformerFactory.newTransformer();
		if(omitXMLDeclaration)
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
		transformer.setErrorListener(errorListener);
		transformer.transform(source, result);
		writer.flush();
	}
	private void writeNodeTo(Node node,File file,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException, XMLException{
		if(this.transformerFactory==null){
			this.initTransformer();
		}
		Source source = new DOMSource(node);
		StreamResult result = new StreamResult(file);
		Transformer transformer = this.transformerFactory.newTransformer();
		if(omitXMLDeclaration)
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
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
						StringBuffer test = new StringBuffer();
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

	

	
	// ATTRIBUTE VALUE
	
	public String getAttributeValue(Node n,String attrName){
		NamedNodeMap att = n.getAttributes();
		if(att!=null){
			Node nA = att.getNamedItem(attrName);
			if(nA!=null)
				return nA.getNodeValue();
		}
		return null;
	}

	
	
}
