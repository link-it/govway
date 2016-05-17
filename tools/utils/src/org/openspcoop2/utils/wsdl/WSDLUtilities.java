/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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



package org.openspcoop2.utils.wsdl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;
import javax.xml.transform.TransformerException;

import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XSDUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.ibm.wsdl.xml.WSDLReaderImpl;
import com.ibm.wsdl.xml.WSDLWriterImpl;



/**
 * Utilities per i WSDL
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class WSDLUtilities {

	
//	public static WSDLUtilities getInstance(){
//		return new WSDLUtilities(null); // senza xmlUtils
//	}
	public static WSDLUtilities getInstance(AbstractXMLUtils xmlUtils){
		return new WSDLUtilities(xmlUtils); 
	}
	
	
	

	private AbstractXMLUtils xmlUtils = null;
	public WSDLUtilities(AbstractXMLUtils xmlUtils){
		this.xmlUtils = xmlUtils;
	}
	
	
	
	
	
	
	/* ---------------- METODI STATICI PUBBLICI ------------------------- */
	
	/** WSDLReader */
	public WSDLReaderImpl getWSDLReader(boolean verbose,boolean importDocuments){
		WSDLReaderImpl wsdlReader = new WSDLReaderImpl();
		wsdlReader.setFeature("javax.wsdl.verbose", verbose);
		wsdlReader.setFeature("javax.wsdl.importDocuments", importDocuments);
		return wsdlReader;
	}
	
	
	
	// IS WSDL
	public boolean isWSDL(byte[]wsdl) throws org.openspcoop2.utils.wsdl.WSDLException {
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			
			if(!this.xmlUtils.isDocument(wsdl)){
				return false;
			}
			Document docXML = this.xmlUtils.newDocument(wsdl);
			Element elemXML = docXML.getDocumentElement();
			return isWSDL(elemXML);
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException(e.getMessage(),e);
		}
	}
	public boolean isWSDL(Document wsdl) throws org.openspcoop2.utils.wsdl.WSDLException {
		Element elemXML = wsdl.getDocumentElement();
		return isWSDL(elemXML);
	}
	
	public boolean isWSDL(Element wsdl) throws org.openspcoop2.utils.wsdl.WSDLException {
		return isWSDL((Node)wsdl);
	}
	public boolean isWSDL(Node wsdl) throws org.openspcoop2.utils.wsdl.WSDLException {
		try{
			if(wsdl == null){
				throw new Exception("Documento wsdl da verificare non definito");
			}
			//System.out.println("LOCAL["+wsdl.getLocalName()+"]  NAMESPACE["+wsdl.getNamespaceURI()+"]");
			if(!"definitions".equals(wsdl.getLocalName())){
				return false;
			}
			if(!"http://schemas.xmlsoap.org/wsdl/".equals(wsdl.getNamespaceURI())){
				return false;
			}
			return true;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException(e.getMessage(),e);
		}
	}
	
	
	
	
	// TARGET NAMESPACE
	
	public String getTargetNamespace(byte[]xsd) throws org.openspcoop2.utils.wsdl.WSDLException {
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			
			if(!this.xmlUtils.isDocument(xsd)){
				throw new Exception("Wsdl non e' un documento valido");
			}
			Document docXML = this.xmlUtils.newDocument(xsd);
			Element elemXML = docXML.getDocumentElement();
			return getTargetNamespace(elemXML);
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException(e.getMessage(),e);
		}
	}
	
	public String getTargetNamespace(Document xsd) throws org.openspcoop2.utils.wsdl.WSDLException {
		Element elemXML = xsd.getDocumentElement();
		return getTargetNamespace(elemXML);
	}
	
	public String getTargetNamespace(Element elemXML) throws org.openspcoop2.utils.wsdl.WSDLException {
		return getTargetNamespace((Node)elemXML);
	}
	
	public String getTargetNamespace(Node elemXML) throws org.openspcoop2.utils.wsdl.WSDLException {
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			
			if(elemXML == null){
				throw new Exception("Wsdl non e' un documento valido");
			}
			//System.out.println("LOCAL["+elemXML.getLocalName()+"]  NAMESPACE["+elemXML.getNamespaceURI()+"]");
			if(!"definitions".equals(elemXML.getLocalName())){
				throw new Exception("Root element non e' un definition wsdl ("+elemXML.getLocalName()+")");
			}
			String targetNamespace = this.xmlUtils.getAttributeValue(elemXML, "targetNamespace");
			//System.out.println("TARGET["+targetNamespace+"]");
			return targetNamespace;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException(e.getMessage(),e);
		}
	}
	
	public String getTargetNamespace(Schema schema){
		NamedNodeMap attributi = schema.getElement().getAttributes();
		if(attributi!=null){
			for(int i=0; i<attributi.getLength(); i++){
				Node a = attributi.item(i);
				//System.out.println("aaa ["+a.getLocalName()+"] ["+a.getPrefix()+"] ["+a.getNodeValue()+"]");
				if("targetNamespace".equals(a.getLocalName())){
					return a.getNodeValue();
				}
			}
		}
		return null;
	}
	
	
	// IMPORT
	public String getImportNamespace(Node elemXML) throws org.openspcoop2.utils.wsdl.WSDLException {
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			if(elemXML == null){
				throw new Exception("Non e' un import valido");
			}
			//System.out.println("LOCAL["+elemXML.getLocalName()+"]  NAMESPACE["+elemXML.getNamespaceURI()+"]");
			if(!"import".equals(elemXML.getLocalName())){
				throw new Exception("Root element non e' un import wsdl ("+elemXML.getLocalName()+")");
			}
			String targetNamespace = this.xmlUtils.getAttributeValue(elemXML, "namespace");
			//System.out.println("TARGET["+targetNamespace+"]");
			return targetNamespace;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException(e.getMessage(),e);
		}
	}
	public String getImportLocation(Node elemXML) throws org.openspcoop2.utils.wsdl.WSDLException {
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			if(elemXML == null){
				throw new Exception("Non e' un import valido");
			}
			//System.out.println("LOCAL["+elemXML.getLocalName()+"]  NAMESPACE["+elemXML.getNamespaceURI()+"]");
			if(!"import".equals(elemXML.getLocalName())){
				throw new Exception("Root element non e' un import wsdl ("+elemXML.getLocalName()+")");
			}
			String targetNamespace = this.xmlUtils.getAttributeValue(elemXML, "location");
			//System.out.println("TARGET["+targetNamespace+"]");
			return targetNamespace;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/* ---------------- WRITE TO ------------------------- */
	
	public void writeWsdlTo(Definition wsdl, String absoluteFilename) throws WSDLException, IOException, org.openspcoop2.utils.wsdl.WSDLException{
		writeWsdlTo(wsdl,absoluteFilename,false);
	}
	public void writeWsdlTo(Definition wsdl, String absoluteFilename,boolean prettyPrint) throws WSDLException, IOException, org.openspcoop2.utils.wsdl.WSDLException{
		if(wsdl == null) return;
		WSDLWriterImpl writer = new WSDLWriterImpl();
		if(prettyPrint){
			writeWsdlTo(wsdl, new File(absoluteFilename), prettyPrint);
		}else{
			writer.writeWSDL(wsdl, new FileWriter(absoluteFilename));
		}
	}
	
	public void writeWsdlTo(Definition wsdl, File file) throws WSDLException, IOException, org.openspcoop2.utils.wsdl.WSDLException{
		writeWsdlTo(wsdl,file,false);
	}
	public void writeWsdlTo(Definition wsdl, File file,boolean prettyPrint) throws WSDLException, IOException, org.openspcoop2.utils.wsdl.WSDLException{
		if(wsdl == null) return;
		WSDLWriterImpl writer = new WSDLWriterImpl();
		if(prettyPrint){
			FileOutputStream fout = null;
			try{
				fout = new FileOutputStream(file);
				writeWsdlTo(wsdl, fout, prettyPrint);
			}finally{
				try{
					fout.flush();
				}catch(Exception eClose){}
				try{
					fout.close();
				}catch(Exception eClose){}
			}
		}else{
			writer.writeWSDL(wsdl, new FileWriter(file));
		}
	}
	
	public void writeWsdlTo(Definition wsdl, OutputStream out) throws WSDLException, IOException, org.openspcoop2.utils.wsdl.WSDLException{
		writeWsdlTo(wsdl,out,false);
	}
	public void writeWsdlTo(Definition wsdl, OutputStream out,boolean prettyPrint) throws WSDLException, IOException, org.openspcoop2.utils.wsdl.WSDLException{
		if(wsdl == null) return;
		WSDLWriterImpl writer = new WSDLWriterImpl();
		if(prettyPrint){
			out.write(prettyPrintWsdl(wsdl).getBytes());
		}else{
			writer.writeWSDL(wsdl, out);
		}
	}
	
	public void writeWsdlTo(Definition wsdl, Writer writer)throws WSDLException, IOException, org.openspcoop2.utils.wsdl.WSDLException{
		writeWsdlTo(wsdl,writer,false);
	}
	public void writeWsdlTo(Definition wsdl, Writer writer,boolean prettyPrint)throws WSDLException, IOException, org.openspcoop2.utils.wsdl.WSDLException{
		if(wsdl == null) return;
		WSDLWriterImpl writerWsdl = new WSDLWriterImpl();
		if(prettyPrint){
			writer.append(prettyPrintWsdl(wsdl));
		}else{
			writerWsdl.writeWSDL(wsdl, writer);
		}
	}
	
	private String prettyPrintWsdl(Definition wsdl) throws WSDLException, IOException, org.openspcoop2.utils.wsdl.WSDLException{
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			writeWsdlTo(wsdl, bout, false);
			bout.flush();
			bout.close();
			Document wsdlDocument = this.xmlUtils.newDocument(bout.toByteArray());
			return PrettyPrintXMLUtils.prettyPrintWithTrAX(wsdlDocument);
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	/* ------------------ GENERAZIONE DEFINITION ---------------------------- */
	/** NOTA: le importsDocument riguardano i WSDL Imports e non gli xsd imports!!!!!!! */
	/**
	 * Legge un WSDL dal path in parametro e ne ritorna la Definition.
	 * @param file Il file da cui leggere il WSDL.
	 * @return La definition corretta o null in caso di path mancante o errori.
	 */
	public Definition readWSDLFromFile(File file) throws WSDLException{
		return readWSDLFromFile(file, false, true);
	}
	public Definition readWSDLFromFile(File file,boolean verbose,boolean importsDocument) throws WSDLException{
		try{
			if (file == null) throw new Exception("Path non definito");
			WSDLReaderImpl reader = getWSDLReader(verbose, importsDocument);
			try {
				Definition def = reader.readWSDL(file.getAbsolutePath());
				return def;
			} catch (WSDLException e) {
				throw e;
			}
		}catch(Exception e){
			throw new WSDLException("WSDLDefinitorio.readWSDLFromLocation(String path)","Lettura del wsdl non riuscita: "+e.getMessage());
		}
	}
	/**
	 * Legge un WSDL dal path in parametro e ne ritorna la Definition.
	 * @param path Il path da cui leggere il WSDL.
	 * @return La definition corretta o null in caso di path mancante o errori.
	 */
	public Definition readWSDLFromLocation(String path) throws WSDLException{
		return readWSDLFromLocation(path, false, true);
	}
	public Definition readWSDLFromLocation(String path,boolean verbose,boolean importsDocument) throws WSDLException{
		try{
			if (path == null) throw new Exception("Path non definito");
			WSDLReaderImpl reader = getWSDLReader(verbose, importsDocument);
			try {
				Definition def = reader.readWSDL(path);
				return def;
			} catch (WSDLException e) {
				throw e;
			}
		}catch(Exception e){
			throw new WSDLException("WSDLDefinitorio.readWSDLFromLocation(String path)","Lettura del wsdl non riuscita: "+e.getMessage());
		}
	}
	/**
	 * Legge un WSDL dal path in parametro e ne ritorna la Definition.
	 * @param wsdl I bytes da cui leggere il WSDL.
	 * @return La definition corretta o null in caso di path mancante o errori.
	 */
	public Definition readWSDLFromBytes(byte[] wsdl) throws WSDLException{
		return readWSDLFromBytes(wsdl, false, true);
	}
	public Definition readWSDLFromBytes(byte[] wsdl,boolean verbose,boolean importsDocument) throws WSDLException{
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			if (wsdl == null) throw new Exception("Bytes non definiti");
			WSDLReaderImpl reader = getWSDLReader(verbose, importsDocument);
			try {
				Document document = this.xmlUtils.newDocument(wsdl);
				Definition def = reader.readWSDL(null,document);
				return def;
			} catch (WSDLException e) {
				throw e;
			}
		}catch(Exception e){
			throw new WSDLException("WSDLDefinitorio.readWSDLFromBytes(byte[] wsdl)","Lettura del wsdl non riuscita: "+e.getMessage());
		}
	}
	/**
	 * Legge un WSDL dal path in parametro e ne ritorna la Definition.
	 * @param doc Document da cui leggere il WSDL.
	 * @return La definition corretta o null in caso di path mancante o errori.
	 */
	public Definition readWSDLFromDocument(Document doc) throws WSDLException{
		return readWSDLFromDocument(doc, false, true);
	}
	public Definition readWSDLFromDocument(Document doc,boolean verbose,boolean importsDocument) throws WSDLException{
		try{
			if (doc == null) throw new Exception("Document non definito");
			WSDLReaderImpl reader = getWSDLReader(verbose, importsDocument);
			try {
				Definition def = reader.readWSDL(null,doc);
				return def;
			} catch (WSDLException e) {
				throw e;
			}
		}catch(Exception e){
			throw new WSDLException("WSDLDefinitorio.readWSDLFromDocument(Document doc)","Lettura del wsdl non riuscita: "+e.getMessage());
		}
	}
	/**
	 * Legge un WSDL dal path in parametro e ne ritorna la Definition.
	 * @param elem Element da cui leggere il WSDL.
	 * @return La definition corretta o null in caso di path mancante o errori.
	 */
	public Definition readWSDLFromElement(Element elem) throws WSDLException{
		return readWSDLFromElement(elem, false, true);
	}
	public Definition readWSDLFromElement(Element elem,boolean verbose,boolean importsDocument) throws WSDLException{
		try{
			if (elem == null) throw new Exception("Element non definito");
			WSDLReaderImpl reader = getWSDLReader(verbose, importsDocument);
			try {
				String param = null;
				Definition def = reader.readWSDL(param,elem);
				return def;
			} catch (WSDLException e) {
				throw e;
			}
		}catch(Exception e){
			throw new WSDLException("WSDLDefinitorio.readWSDLFromElement(Element elem)","Lettura del wsdl non riuscita: "+e.getMessage());
		}
	}
	/**
	 * Legge un WSDL dal path in parametro e ne ritorna la Definition.
	 * @param uri La URI da cui leggere il WSDL.
	 * @return La definition corretta o null in caso di path mancante o errori.
	 */
	public Definition readWSDLFromURI(URI uri) throws WSDLException{
		return readWSDLFromURI(uri, false, true);
	}
	public Definition readWSDLFromURI(URI uri,boolean verbose,boolean importsDocument) throws WSDLException{
		try{
			if (uri == null) throw new Exception("URI non definita");
			WSDLReaderImpl reader = getWSDLReader(verbose, importsDocument);
			try {
				Definition def = reader.readWSDL(null,uri.getPath());
				return def;
			} catch (WSDLException e) {
				throw e;
			}
		}catch(Exception e){
			throw new WSDLException("WSDLDefinitorio.readWSDLFromURI(URI uri)","Lettura del wsdl non riuscita: "+e.getMessage());
		}
	}
	
	
	
	
	
	
	
	
	
	/* ---------------- METODI ADD ------------------------- */
	public void addSchemaIntoTypes(Document wsdl, Node schema) throws org.openspcoop2.utils.wsdl.WSDLException{
		try{
			NodeList list = wsdl.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("definitions".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("types".equals(childDefinition.getLocalName())){
									
									childDefinition.appendChild(wsdl.createTextNode("\n\t\t"));
									childDefinition.appendChild(schema);
									childDefinition.appendChild(wsdl.createTextNode("\n"));
									
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Riscontrato errore durante l'aggiunto di uno schema nell'elemento Types: "+e.getMessage(),e);
		}
	}
	
	public void addImportSchemaIntoTypes(Document wsdl, String targetNamespace, String location) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		// NOTA: types deve esistere
		
		try{
			NodeList list = wsdl.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("definitions".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("types".equals(childDefinition.getLocalName())){
									
									Element importSchemaElement = wsdl.createElementNS("http://www.w3.org/2001/XMLSchema", "schema");
									importSchemaElement.setAttribute("targetNamespace", targetNamespace);
									
									Element importElement = wsdl.createElementNS("http://www.w3.org/2001/XMLSchema", "import");
									importElement.setAttribute("namespace", targetNamespace);
									importElement.setAttribute("schemaLocation", location);
							
									importSchemaElement.appendChild(wsdl.createTextNode("\n\t\t\t"));
									importSchemaElement.appendChild(importElement);
									importSchemaElement.appendChild(wsdl.createTextNode("\n\t\t"));
									
									childDefinition.appendChild(wsdl.createTextNode("\n\t\t"));
									childDefinition.appendChild(importSchemaElement);
									childDefinition.appendChild(wsdl.createTextNode("\n\n"));
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Riscontrato errore durante l'aggiunto di uno schema nell'elemento Types: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	/* ---------------- METODI GET ------------------------- */
			
	public Node getIfExistsDefinitionsElementIntoWSDL(Document wsdl) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		try{
			NodeList list = wsdl.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("definitions".equals(child.getLocalName())){
						return child;
					}
				}
			}
			return null;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Riscontrato errore durante la lettura del wsdl: "+e.getMessage(),e);
		}
	}
		
	

	
	
	
	
	
	/* ---------------- METODI READ IMPORTS ------------------------- */
	
	public List<Node> readImports(Document wsdl) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		try{
			Vector<Node> imports = new Vector<Node>();
			
			NodeList list = wsdl.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("definitions".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("import".equals(childDefinition.getLocalName())){
									imports.add(childDefinition);
								}
							}
						}
					}
				}
			}
			return imports;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Riscontrato errore durante la lettura del wsdl: "+e.getMessage(),e);
		}
	}
	
	public List<Node> readImportsAndIncludesSchemaIntoTypes(Document wsdl) throws org.openspcoop2.utils.wsdl.WSDLException{
		return readImportsIncludesSchemaIntoTypes(wsdl,true,true);
	}
	public List<Node> readImportsSchemaIntoTypes(Document wsdl) throws org.openspcoop2.utils.wsdl.WSDLException{
		return readImportsIncludesSchemaIntoTypes(wsdl,true,false);
	}
	public List<Node> readIncludesSchemaIntoTypes(Document wsdl) throws org.openspcoop2.utils.wsdl.WSDLException{
		return readImportsIncludesSchemaIntoTypes(wsdl,false,true);
	}
	private List<Node> readImportsIncludesSchemaIntoTypes(Document wsdl,boolean readImport,boolean readInclude) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			XSDUtils xsdUtils = new XSDUtils(this.xmlUtils);
			
			Vector<Node> imports = new Vector<Node>();
			
			NodeList list = wsdl.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("definitions".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("types".equals(childDefinition.getLocalName())){
									NodeList listTypes = childDefinition.getChildNodes();
									if(listTypes!=null){
										for(int h=0; h<listTypes.getLength(); h++){
											Node childTypes = listTypes.item(h);
											if("schema".equals(childTypes.getLocalName())){
												
												String targetNamespaceSchema = null;
												if(targetNamespaceSchema==null){
													try{
														targetNamespaceSchema = xsdUtils.getTargetNamespace(childTypes);
													}catch(Exception e){}
												}
												
												if(readImport){
													List<Node> importsSchemi = xsdUtils.readImports(targetNamespaceSchema,childTypes);
													if(importsSchemi!=null && importsSchemi.size()>0){
														imports.addAll(importsSchemi);
													}
												}
												if(readInclude){
													List<Node> includesSchemi = xsdUtils.readIncludes(targetNamespaceSchema,childTypes);
													if(includesSchemi!=null && includesSchemi.size()>0){
														imports.addAll(includesSchemi);
													}
												}
												
											}
										}
									}
								}
							}
						}
					}
				}
			}
			return imports;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Riscontrato errore durante la lettura del wsdl: "+e.getMessage(),e);
		}
	}
	
	public List<Node> readImportsAndIncludesFromSchemaXSD(Schema xsd) throws org.openspcoop2.utils.wsdl.WSDLException{
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			XSDUtils xsdUtils = new XSDUtils(this.xmlUtils);
			return xsdUtils.readImportsAndIncludes(getTargetNamespace(xsd),xsd.getElement());
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException(e.getMessage(),e);
		}
	}
	public List<Node> readImportsFromSchemaXSD(Schema xsd) throws org.openspcoop2.utils.wsdl.WSDLException{
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			XSDUtils xsdUtils = new XSDUtils(this.xmlUtils);
			return xsdUtils.readImports(getTargetNamespace(xsd),xsd.getElement());
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException(e.getMessage(),e);
		}
	}
	public List<Node> readIncludesFromSchemaXSD(Schema xsd) throws org.openspcoop2.utils.wsdl.WSDLException{
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			XSDUtils xsdUtils = new XSDUtils(this.xmlUtils);
			return xsdUtils.readIncludes(getTargetNamespace(xsd),xsd.getElement());
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException(e.getMessage(),e);
		}
	}
	
	
	/**
	 * Recupera l'array dei documenti in xsd:include
	 * @param wsdlNormalizzato
	 * @return array dei documenti in xsd:include
	 * @throws IOException 
	 * @throws XMLException 
	 * @throws org.openspcoop2.utils.wsdl.WSDLException 
	 */
	public Vector<byte[]> getSchemiXSD(Definition wsdlNormalizzato) throws IOException,TransformerException, XMLException, org.openspcoop2.utils.wsdl.WSDLException{
		
		if(this.xmlUtils==null){
			throw new org.openspcoop2.utils.wsdl.WSDLException("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
		}
		
		// Il wsdl deve contenere solo schemi xsd completi
		
		Vector<byte[]> v = new Vector<byte[]>();
		
		Types types = wsdlNormalizzato.getTypes();
		List<?> xsdTypes = types.getExtensibilityElements();
		for (int i = 0; i< xsdTypes.size(); i++){
			Schema schema = (Schema) xsdTypes.get(i);
			v.add(this.xmlUtils.toByteArray(schema.getElement())); 
		}
		return v;
		
	}
	
	public List<Node> getSchemiXSD(Document wsdl) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			XSDUtils xsdUtils = new XSDUtils(this.xmlUtils);
			
			Vector<Node> schemi = new Vector<Node>();
			
			NodeList list = wsdl.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("definitions".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("types".equals(childDefinition.getLocalName())){
									NodeList listTypes = childDefinition.getChildNodes();
									if(listTypes!=null){
										for(int h=0; h<listTypes.getLength(); h++){
											Node childTypes = listTypes.item(h);
											
											if("schema".equals(childTypes.getLocalName()) && xsdUtils.isXSDSchema(childTypes) ){
												
												schemi.add(childTypes);
												
											}
										}
									}
								}
							}
						}
					}
				}
			}
			return schemi;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Riscontrato errore durante la lettura del wsdl: "+e.getMessage(),e);
		}
	}
	public List<byte[]> getBytesSchemiXSD(Document wsdl) throws org.openspcoop2.utils.wsdl.WSDLException{
		List<Node> schemi = this.getSchemiXSD(wsdl);
		
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			List<byte[]> schemiBytes = new ArrayList<byte[]>();
			if(schemi!=null && schemi.size()>0){
				for (Node node : schemi) {
					schemiBytes.add(this.xmlUtils.toByteArray(node));		
				}
			}
			return schemiBytes;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Riscontrato errore durante la lettura del wsdl: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	/* ---------------- METODI NORMALIZZAZIONE ------------------------- */
	
	public String normalizzazioneSchemaPerInserimentoInWsdl(Element schemaXSD,Element wsdl,
			HashMap<String,String> prefixForWSDL,String uniquePrefixWSDL,boolean docImportato,String targetNamespaceParent) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		String targetNamespace = null;
		
		if(docImportato){
			
			// GESTIONE DOCUMENTO IMPORTATO
			//System.out.println("***** IMPORT");
			
			// Esamino targetNamespace e prefisso dell'xsd per verificarne la presenza nel definitions del wsdl
			targetNamespace = readPrefixForWsdl(schemaXSD, wsdl, prefixForWSDL, uniquePrefixWSDL, true);
		
			// Rimozione completa di tutti gli attributi all'infuori del TargetNamespace e dell'associato prefix
			NamedNodeMap attributi = schemaXSD.getAttributes();
			Vector<Attr> attributiDaMantenere = new Vector<Attr>();	
			if(attributi!=null && attributi.getLength()>0){
				for (int i = (attributi.getLength()-1); i >= 0; i--) {
					Attr attr = (Attr) attributi.item(i);
					//System.out.println("REMOVE NAME["+attr.getName()+"] LOCAL_NAME["+attr.getLocalName()+"] VALUE["+attr.getNodeValue()+"] URI["+attr.getNamespaceURI()+"]...");
					if(targetNamespace.equals(attr.getNodeValue())){
						//System.out.println("REMOVE NON EFFETTUATO");
						attributiDaMantenere.add(attr);
					}
					//schemaXSD.removeAttributeNode(attr);
					this.xmlUtils.removeAttribute(attr, schemaXSD);
				}
			}
//			IL Codice sotto non può funzionare, poichè in axiom la struttura NamedNodeMap non viene aggiornata dopo la remove
//			while(attributi.getLength()>0){
//				Attr attr = (Attr) attributi.item(0);
//				System.out.println("REMOVE NAME["+attr.getLocalName()+"] VALUE["+attr.getNodeValue()+"] URI["+attr.getNamespaceURI()+"]...");
//				if(targetNamespace.equals(attr.getNodeValue())){
//					System.out.println("REMOVE NON EFFETTUATO");
//					attributiDaMantenere.add(attr);
//				}
//				schemaXSD.removeAttributeNode(attr);
//			}
			
//			// DEBUG DOPO ELIMINAZIONE
//			attributi = schemaXSD.getAttributes();
//			System.out.println("TEST: "+attributi.getLength());
//			for (int i = 0; i < attributi.getLength(); i++) {
//				Attr attr = (Attr) attributi.item(i);
//				System.out.println("TEST NAME["+attr.getName()+"] LOCAL_NAME["+attr.getLocalName()+"] VALUE["+attr.getNodeValue()+"] URI["+attr.getNamespaceURI()+"]...");
//			}
			
			while(attributiDaMantenere.size()>0){
				//schemaXSD.setAttributeNode(attributiDaMantenere.remove(0));
				Attr attr = attributiDaMantenere.remove(0);
				//System.out.println("RE-ADD NAME["+attr.getName()+"] LOCAL_NAME["+attr.getLocalName()+"] VALUE["+attr.getNodeValue()+"] URI["+attr.getNamespaceURI()+"]...");
				this.xmlUtils.addAttribute(attr, schemaXSD);
			}
			
//			// DEBUG DOPO AGGIUNTA
//			attributi = schemaXSD.getAttributes();
//			System.out.println("TEST2: "+attributi.getLength());
//			for (int i = 0; i < attributi.getLength(); i++) {
//				Attr attr = (Attr) attributi.item(i);
//				System.out.println("TEST2 NAME["+attr.getName()+"] LOCAL_NAME["+attr.getLocalName()+"] VALUE["+attr.getNodeValue()+"] URI["+attr.getNamespaceURI()+"]...");
//			}
			
		}else{
			
			// GESTIONE DOCUMENTO INCLUSO
			//System.out.println("***** INCLUDE");
			
			targetNamespace = targetNamespaceParent;
			
			// Rimozione completa di tutti gli attributi 
			NamedNodeMap attributi = schemaXSD.getAttributes();
			Vector<Attr> attributiDaMantenere = new Vector<Attr>();	
			if(attributi!=null && attributi.getLength()>0){
				for (int i = (attributi.getLength()-1); i >= 0; i--) {
					Attr attr = (Attr) attributi.item(i);
					//System.out.println("REMOVE NAME["+attr.getName()+"] LOCAL_NAME["+attr.getLocalName()+"] VALUE["+attr.getNodeValue()+"] URI["+attr.getNamespaceURI()+"]...");
					if(targetNamespace.equals(attr.getNodeValue())){
						//System.out.println("REMOVE NON EFFETTUATO");
						attributiDaMantenere.add(attr);
					}
					//schemaXSD.removeAttributeNode(attr);
					this.xmlUtils.removeAttribute(attr, schemaXSD);
				}
			}
//			IL Codice sotto non può funzionare, poichè in axiom la struttura NamedNodeMap non viene aggiornata dopo la remove
//			while(attributi.getLength()>0){
//				Attr attr = (Attr) attributi.item(0);
//				//System.out.println("REMOVE NAME["+attr.getLocalName()+"] VALUE["+attr.getNodeValue()+"]...");
//				schemaXSD.removeAttributeNode(attr);
//			}
			
//			// DEBUG DOPO ELIMINAZIONE
//			attributi = schemaXSD.getAttributes();
//			System.out.println("TEST: "+attributi.getLength());
//			for (int i = 0; i < attributi.getLength(); i++) {
//				Attr attr = (Attr) attributi.item(i);
//				System.out.println("TEST NAME["+attr.getName()+"] LOCAL_NAME["+attr.getLocalName()+"] VALUE["+attr.getNodeValue()+"] URI["+attr.getNamespaceURI()+"]...");
//			}
			
			// Aggiungo targetNamespace e altri prefix associati al namespace
			boolean foundTargetNamespace = false;
			if(attributiDaMantenere.size()>0){
				while(attributiDaMantenere.size()>0){
					//schemaXSD.setAttributeNode(attributiDaMantenere.remove(0));
					Attr attr = attributiDaMantenere.remove(0);
					if("targetNamespace".equals(attr.getName())){
						foundTargetNamespace = true;
					}
					//System.out.println("RE-ADD NAME["+attr.getName()+"] LOCAL_NAME["+attr.getLocalName()+"] VALUE["+attr.getNodeValue()+"] URI["+attr.getNamespaceURI()+"]...");
					this.xmlUtils.addAttribute(attr, schemaXSD);
				}
			}
			if(!foundTargetNamespace){
				schemaXSD.setAttribute("targetNamespace", targetNamespace);
			}
			
//			// DEBUG DOPO AGGIUNTA
//			attributi = schemaXSD.getAttributes();
//			System.out.println("TEST2: "+attributi.getLength());
//			for (int i = 0; i < attributi.getLength(); i++) {
//				Attr attr = (Attr) attributi.item(i);
//				System.out.println("TEST2 NAME["+attr.getName()+"] LOCAL_NAME["+attr.getLocalName()+"] VALUE["+attr.getNodeValue()+"] URI["+attr.getNamespaceURI()+"]...");
//			}
		}
		
		return targetNamespace;
	}
	
	public String readPrefixForWsdl(Element schemaXSD,Element wsdl,
			HashMap<String,String> prefixForWSDL,String uniquePrefixWSDL,boolean targetNamespaceObbligatorio) throws org.openspcoop2.utils.wsdl.WSDLException{
			
		String targetNamespace = null;
		
		//System.out.println("TARGETNAMESPACE="+docElement.getNamespaceURI());
		NamedNodeMap attributi = schemaXSD.getAttributes();
		if(attributi!=null){
			// ricerca targetNamespace XSD
			for(int i=0; i<attributi.getLength(); i++){
				Node attr = attributi.item(i);
				//System.out.println("NAME["+attr.getLocalName()+"] VALUE["+attr.getNodeValue()+"] PREFIX["+attr.getPrefix()+"]");
				if("targetNamespace".equals(attr.getLocalName())){
					targetNamespace =attr.getNodeValue();
					break;
				}
			}
			if(targetNamespace==null){
				if(targetNamespaceObbligatorio)
					throw new org.openspcoop2.utils.wsdl.WSDLException("Target namespace non trovato");
				else
					return null;
			}
			// ricerca prefisso associato al target namespace dell'XSD
			String prefixTargetNamespaceXSD = null;
			for(int i=0; i<attributi.getLength(); i++){
				Node attr = attributi.item(i);
				//System.out.println("NAME["+attr.getLocalName()+"] VALUE["+attr.getNodeValue()+"] PREFIX["+attr.getPrefix()+"]");
				if(targetNamespace.equals(attr.getNodeValue()) && "xmlns".equals(attr.getPrefix())){
					//System.out.println("FOUND");
					prefixTargetNamespaceXSD =attr.getLocalName();
					break;
				}
			}
			//System.out.println("PREFIX!!! ["+prefixTargetNamespaceXSD+"] ["+targetNamespaceXSD+"]");
			
			// Cerco il prefix nel definition del wsdl, ed in caso non sia definito lo registra per una successiva aggiunta
			NamedNodeMap attributi_wsdl = wsdl.getAttributes();
			if(attributi_wsdl!=null){
				boolean findIntoWSDL = false;
				for(int i=0; i<attributi_wsdl.getLength(); i++){
					Node attr = attributi_wsdl.item(i);
					//System.out.println("WSDL ATTR   NAME["+attr.getLocalName()+"] VALUE["+attr.getNodeValue()+"] PREFIX["+attr.getPrefix()+"]");
					if(targetNamespace.equals(attr.getNodeValue()) && "xmlns".equals(attr.getPrefix())){
						if(prefixTargetNamespaceXSD==null){
							findIntoWSDL = (attr.getLocalName()==null);
							break;
						}
						else if(prefixTargetNamespaceXSD.equals(attr.getLocalName())){
							findIntoWSDL = true;
							break;
						}
					}	
				}
				if(!findIntoWSDL){
					String newPrefix = null;
					if(prefixTargetNamespaceXSD==null){
						//System.out.println("AGGIUNGO IN WSDL XMLNS (unique:"+uniquePrefixWSDL+"): "+targetNamespace);
						if(uniquePrefixWSDL!=null){
							newPrefix = "xmlns:"+uniquePrefixWSDL;
						}
						else{
							newPrefix = "xmlns";
						}
					}else{
						//System.out.println("AGGIUNGO IN WSDL XMLNS (unique:"+uniquePrefixWSDL+"):"+prefixTargetNamespaceXSD+" "+targetNamespace);
						if(uniquePrefixWSDL!=null){
							newPrefix = "xmlns:"+uniquePrefixWSDL+prefixTargetNamespaceXSD;
						}else{
							newPrefix = "xmlns:"+prefixTargetNamespaceXSD;
						}
					}
					
					//System.out.println("SET NEW ATTRIBUTE NS LOCALNAME["+newPrefix+"] ...");
					schemaXSD.setAttribute(newPrefix, targetNamespace);
					//System.out.println("SET NEW ATTRIBUTE NS LOCALNAME["+newPrefix+"] ["+targetNamespace+"] OK");
					
					if(prefixForWSDL.containsKey(newPrefix)==false){
						prefixForWSDL.put(newPrefix, targetNamespace);
					}
					else{
						String namespace = prefixForWSDL.get(newPrefix);
						if(namespace.equals(targetNamespace)==false){
							throw new org.openspcoop2.utils.wsdl.WSDLException("Rilevati due prefissi a cui sono stati associati namespace differenti. \nPrefix["+
									newPrefix+"]=Namespace["+namespace+"]\nPrefix["+newPrefix+"]=Namespace["+targetNamespace+"]"); 
						}
					}
				}
			}
		}
		
		return targetNamespace;
	}
	
	
	
	
	
	
	
	
	
	/* ---------------- METODI REMOVE ------------------------- */
	
	/**
	 * Rimuove i wsdl:import dal documento
	 * 
	 * @param document
	 */
	public void removeImports(Document document){
		removeImport(document,null);
	}
	public void removeImport(Document document,Node importNode){
		// rimuovo eventuale import per i definitori, visto che nella versione byte[] dal db, gli import non possono essere risolti
		NodeList list = document.getChildNodes();
		if(list!=null){
			for(int i=0; i<list.getLength(); i++){
				Node child = list.item(i);
				if("definitions".equals(child.getLocalName())){
					NodeList listDefinition = child.getChildNodes();
					if(listDefinition!=null){
						for(int j=0; j<listDefinition.getLength(); j++){
							Node childDefinition = listDefinition.item(j);
							if("import".equals(childDefinition.getLocalName())){
								if(importNode==null){
									//	System.out.println("REMOVE IMPORT");
									child.removeChild(childDefinition);
								}else{
									if(importNode.equals(childDefinition)){
										//System.out.println("REMOVE MIRATA");
										child.removeChild(childDefinition);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Rimuove gli schemi dal wsdl
	 * 
	 * @param document
	 * @throws org.openspcoop2.utils.wsdl.WSDLException
	 */
	public void removeSchemiIntoTypes(Document document) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		try{
			NodeList list = document.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("definitions".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("types".equals(childDefinition.getLocalName())){
									NodeList listTypes = childDefinition.getChildNodes();
									if(listTypes!=null){
										boolean onlySchemaAndComment = true;
										for(int h=0; h<listTypes.getLength(); h++){
											Node childTypes = listTypes.item(h);
											//System.out.println("?? IMPORT SCHEMA: "+childTypes.getLocalName());
											if("schema".equals(childTypes.getLocalName())){
												//System.out.println("REMOVE IMPORT SCHEMA");
												childDefinition.removeChild(childTypes);
											}
											else{
												if( !(childTypes instanceof Text) && !(childTypes instanceof Comment) ){
													onlySchemaAndComment = false;
												}
											}
										}
										if(onlySchemaAndComment){
											listTypes = childDefinition.getChildNodes();
											if(listTypes!=null){
												for(int h=0; h<listTypes.getLength(); h++){
													childDefinition.removeChild(listTypes.item(h));
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Riscontrato errore durante la lettura del wsdl: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Rimuove l'elemento types dal wsdl
	 * 
	 * @param wsdl
	 * @throws org.openspcoop2.utils.wsdl.WSDLException
	 */
	public void removeTypes(Document wsdl) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		try{
			NodeList list = wsdl.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("definitions".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("types".equals(childDefinition.getLocalName())){
									//System.out.println("REMOVE TYPES");
									child.removeChild(childDefinition);
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Riscontrato errore durante la rimozione dell'elemento Types: "+e.getMessage(),e);
		}
	}
	
	public void removeImportsFromSchemaXSD(Schema xsd) throws org.openspcoop2.utils.wsdl.WSDLException{
		if(this.xmlUtils==null){
			throw new org.openspcoop2.utils.wsdl.WSDLException("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
		}
		XSDUtils xsdUtils = new XSDUtils(this.xmlUtils);
		xsdUtils.removeImports(xsd.getElement());
	}
	public void removeIncludesFromSchemaXSD(Schema xsd) throws org.openspcoop2.utils.wsdl.WSDLException{
		if(this.xmlUtils==null){
			throw new org.openspcoop2.utils.wsdl.WSDLException("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
		}
		XSDUtils xsdUtils = new XSDUtils(this.xmlUtils);
		xsdUtils.removeIncludes(xsd.getElement());
	}
	public void removeImportsAndIncludesFromSchemaXSD(Schema xsd) throws org.openspcoop2.utils.wsdl.WSDLException{
		if(this.xmlUtils==null){
			throw new org.openspcoop2.utils.wsdl.WSDLException("XMLUtils not initialized in WSDLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
		}
		XSDUtils xsdUtils = new XSDUtils(this.xmlUtils);
		xsdUtils.removeImportsAndIncludes(xsd.getElement());
	}
	
	/**
	 * Rimuove TUTTI gli Import da una definizione.
	 * @param definition La definizione da modificare.
	 */
	public void removeAllImports(Definition definition) throws WSDLException{
		if (definition != null) {
			DefinitionWrapper wsdl = new DefinitionWrapper(definition,this.xmlUtils);
			wsdl.removeAllImports();
		}else{
			throw new WSDLException("removeAllImports(Definition definition)","WSDL non fornito");
		}
	}
	
	/**
	 * Rimuove TUTTI i messaggi da una definizione.
	 * @param definition La definizione da modificare.
	 */
	public void removeAllMessages(Definition definition) throws WSDLException{
		if (definition != null) {
			DefinitionWrapper wsdl = new DefinitionWrapper(definition,this.xmlUtils);
			wsdl.removeAllMessages();
		}else{
			throw new WSDLException("removeAllMessages(Definition definition)","WSDL non fornito");
		}
	}
	
	/**
	 * Rimuove TUTTI i PortType da una definizione.
	 * @param definition La definizione da modificare.
	 */
	public void removeAllPortTypes(Definition definition) throws WSDLException{
		if (definition != null) {
			DefinitionWrapper wsdl = new DefinitionWrapper(definition,this.xmlUtils);
			wsdl.removeAllPortTypes();
		}else{
			throw new WSDLException("removeAllPortTypes(Definition definition)","WSDL non fornito");
		}
	}

	/**
	 * Rimuove TUTTI i Binding da una definizione.
	 * @param definition La definizione da modificare.
	 */
	public void removeAllBindings(Definition definition) throws WSDLException{
		if (definition != null) {
			DefinitionWrapper wsdl = new DefinitionWrapper(definition,this.xmlUtils);
			wsdl.removeAllBindings();
		}else{
			throw new WSDLException("removeAllBindings(Definition definition)","WSDL non fornito");
		}
	}
	
	/**
	 * Rimuove TUTTI i Service da una definizione.
	 * @param definition La definizione da modificare.
	 */
	public void removeAllServices(Definition definition) throws WSDLException{
		if (definition != null) {
			DefinitionWrapper wsdl = new DefinitionWrapper(definition,this.xmlUtils);
			wsdl.removeAllServices();
		}else{
			throw new WSDLException("removeAllServices(Definition definition)","WSDL non fornito");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ---------------- TYPES ------------------------- */

	public boolean existsTypes(Document document) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		try{
			NodeList list = document.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("definitions".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("types".equals(childDefinition.getLocalName())){
									return true;
								}
							}
						}
					}
				}
			}

		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Riscontrato errore durante la lettura del wsdl: "+e.getMessage(),e);
		}
		
		return false;
	}
	
	public Node getIfExistsTypesElementIntoWSDL(Document wsdl) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		try{
			NodeList list = wsdl.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("definitions".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("types".equals(childDefinition.getLocalName())){
									return childDefinition;
								}
							}
						}
					}
				}
			}
			return null;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Riscontrato errore durante la lettura del wsdl: "+e.getMessage(),e);
		}
	}
	
	public Node addEmptyTypesIfNotExists(Document document) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		try{
			boolean exists = false;
			NodeList list = document.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("definitions".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("types".equals(childDefinition.getLocalName())){
									exists = true;
									break;
								}
							}
						}
						if(!exists){
							String name = "types";
							if(child.getPrefix()!=null && !"".equals(child.getPrefix())){
								name = child.getPrefix()+":"+name;
							}
							Node n = this.xmlUtils.getFirstNotEmptyChildNode(child,false);
							//System.out.println("FIRST ["+n.getLocalName()+"] ["+n.getPrefix()+"] ["+n.getNamespaceURI()+"]");
							Node type = document.createElementNS(child.getNamespaceURI(), name);
							child.insertBefore(type,n);
							return type;
						}
					}
				}
			}

			return null;
			
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Riscontrato errore durante la lettura del wsdl: "+e.getMessage(),e);
		}
	}
}
