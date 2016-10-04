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



package org.openspcoop2.utils.wadl;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.jvnet.ws.wadl.HTTPMethods;
import org.jvnet.ws.wadl.ast.ApplicationNode;
import org.jvnet.ws.wadl.ast.MethodNode;
import org.jvnet.ws.wadl.ast.ResourceNode;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




/**
 * Utilities per i WADL
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class WADLUtilities {


	//	public static WADLUtilities getInstance(){
	//		return new WADLUtilities(null); // senza xmlUtils
	//	}
	public static WADLUtilities getInstance(AbstractXMLUtils xmlUtils){
		return new WADLUtilities(xmlUtils); 
	}




	private AbstractXMLUtils xmlUtils = null;
	public WADLUtilities(AbstractXMLUtils xmlUtils){
		this.xmlUtils = xmlUtils;
	}






	/* ---------------- METODI STATICI PUBBLICI ------------------------- */

	/** WADLReader */
	public WADLReader getWADLReader(Logger log, boolean verbose, boolean processInclude, boolean processInlineSchema){
		return new WADLReader(log, this.xmlUtils, verbose, processInclude,processInlineSchema);
	}



	// IS WADL
	public boolean isWADL(byte[]wadl) throws org.openspcoop2.utils.wadl.WADLException {
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WADLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}

			if(!this.xmlUtils.isDocument(wadl)){
				return false;
			}
			Document docXML = this.xmlUtils.newDocument(wadl);
			Element elemXML = docXML.getDocumentElement();
			return isWADL(elemXML);
		}catch(Exception e){
			throw new org.openspcoop2.utils.wadl.WADLException(e.getMessage(),e);
		}
	}
	public boolean isWADL(Document wadl) throws org.openspcoop2.utils.wadl.WADLException {
		Element elemXML = wadl.getDocumentElement();
		return isWADL(elemXML);
	}

	public boolean isWADL(Element wadl) throws org.openspcoop2.utils.wadl.WADLException {
		return isWADL((Node)wadl);
	}
	public boolean isWADL(Node wadl) throws org.openspcoop2.utils.wadl.WADLException {
		try{
			if(wadl == null){
				throw new Exception("Documento wadl da verificare non definito");
			}
			//System.out.println("LOCAL["+wadl.getLocalName()+"]  NAMESPACE["+wadl.getNamespaceURI()+"]");
			if(!"application".equals(wadl.getLocalName())){
				return false;
			}
			if(!"http://wadl.dev.java.net/2009/02".equals(wadl.getNamespaceURI())){
				return false;
			}
			return true;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wadl.WADLException(e.getMessage(),e);
		}
	}




	// TARGET NAMESPACE

	public String getTargetNamespace(byte[]xsd) throws org.openspcoop2.utils.wadl.WADLException {
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WADLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}

			if(!this.xmlUtils.isDocument(xsd)){
				throw new Exception("Wadl non e' un documento valido");
			}
			Document docXML = this.xmlUtils.newDocument(xsd);
			Element elemXML = docXML.getDocumentElement();
			return getTargetNamespace(elemXML);
		}catch(Exception e){
			throw new org.openspcoop2.utils.wadl.WADLException(e.getMessage(),e);
		}
	}

	public String getTargetNamespace(Document xsd) throws org.openspcoop2.utils.wadl.WADLException {
		Element elemXML = xsd.getDocumentElement();
		return getTargetNamespace(elemXML);
	}

	public String getTargetNamespace(Element elemXML) throws org.openspcoop2.utils.wadl.WADLException {
		return getTargetNamespace((Node)elemXML);
	}

	public String getTargetNamespace(Node elemXML) throws org.openspcoop2.utils.wadl.WADLException {
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WADLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}

			if(elemXML == null){
				throw new Exception("Wadl non e' un documento valido");
			}
			//System.out.println("LOCAL["+elemXML.getLocalName()+"]  NAMESPACE["+elemXML.getNamespaceURI()+"]");
			if(!"application".equals(elemXML.getLocalName())){
				throw new Exception("Root element non e' un application wadl ("+elemXML.getLocalName()+")");
			}
			String targetNamespace = this.xmlUtils.getAttributeValue(elemXML, "targetNamespace");
			//System.out.println("TARGET["+targetNamespace+"]");
			return targetNamespace;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wadl.WADLException(e.getMessage(),e);
		}
	}

















	/* ------------------ LETTURA---------------------------- */

	/**
	 * Legge un WADL dal path in parametro e ne ritorna la rappresentazione a oggetti.
	 * @param file Il file da cui leggere il WADL.
	 * @return L'oggetto o null in caso di path mancante o errori.
	 */
	public ApplicationWrapper readWADLFromFile(Logger log,File file) throws WADLException{
		return readWADLFromFile(log, file, false, true, true);
	}
	public ApplicationWrapper readWADLFromFile(Logger log, File file,boolean verbose,boolean processInclude, boolean processInlineSchema) throws WADLException{
		try{
			if (file == null) throw new Exception("Path non definito");
			WADLReader reader = getWADLReader(log, verbose, processInclude, processInlineSchema);
			ApplicationNode appNode = reader.readWADL(file.getAbsolutePath());
			ApplicationWrapper ap = new ApplicationWrapper(appNode, reader.getResources(), reader.getMappingNamespaceLocations(), this.xmlUtils);
			return ap;
		}catch(Exception e){
			throw new WADLException("Lettura del wadl non riuscita (File): "+e.getMessage(),e);
		}
	}
	/**
	 * Legge un WADL dal path in parametro e ne ritorna la rappresentazione a oggetti.
	 * @param path Il path da cui leggere il WADL.
	 * @return La definition corretta o null in caso di path mancante o errori.
	 */
	public ApplicationWrapper readWADLFromLocation(Logger log,String path) throws WADLException{
		return readWADLFromLocation(log, path, false, true, true);
	}
	public ApplicationWrapper readWADLFromLocation(Logger log, String path,boolean verbose,boolean processInclude, boolean processInlineSchema) throws WADLException{
		try{
			if (path == null) 
				throw new Exception("Path non definito");
			WADLReader reader = getWADLReader(log, verbose, processInclude, processInlineSchema);
			ApplicationNode appNode = reader.readWADL(path);
			ApplicationWrapper ap = new ApplicationWrapper(appNode, reader.getResources(), reader.getMappingNamespaceLocations(), this.xmlUtils);
			return ap;
		}catch(Exception e){
			throw new WADLException("Lettura del wadl non riuscita (Path): "+e.getMessage(),e);
		}
	}
	/**
	 * Legge un WADL dal path in parametro e ne ritorna la rappresentazione a oggetti.
	 * @param wadl I bytes da cui leggere il WADL.
	 * @return La definition corretta o null in caso di path mancante o errori.
	 */
	public ApplicationWrapper readWADLFromBytes(Logger log, byte[] wadl) throws WADLException{
		return readWADLFromBytes(log, wadl, false, true, true);
	}
	public ApplicationWrapper readWADLFromBytes(Logger log, byte[] wadl,boolean verbose,boolean processInclude, boolean processInlineSchema) throws WADLException{
		try{
			if (wadl == null) 
				throw new Exception("Bytes non definiti");
			File tmp = File.createTempFile("wadl", ".tmp");
			try {
				FileSystemUtilities.writeFile(tmp, wadl);
				return this.readWADLFromFile(log, tmp, verbose, processInclude, processInlineSchema);
			} catch (WADLException e) {
				throw e;
			}finally{
				tmp.delete();
			}
		}catch(Exception e){
			throw new WADLException("Lettura del wadl non riuscita (byte[]): "+e.getMessage(),e);
		}
	}
	/**
	 * Legge un WADL dal path in parametro e ne ritorna la rappresentazione a oggetti.
	 * @param doc Document da cui leggere il WADL.
	 * @return La definition corretta o null in caso di path mancante o errori.
	 */
	public ApplicationWrapper readWADLFromDocument(Logger log,Document doc) throws WADLException{
		return readWADLFromDocument(log, doc, false, true, true);
	}
	public ApplicationWrapper readWADLFromDocument(Logger log, Document doc,boolean verbose,boolean processInclude, boolean processInlineSchema) throws WADLException{
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WADLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			if (doc == null) 
				throw new Exception("Document non definito");
			try {
				byte[]wadl = this.xmlUtils.toByteArray(doc);
				return this.readWADLFromBytes(log, wadl, verbose, processInclude, processInlineSchema);
			} catch (WADLException e) {
				throw e;
			}
		}catch(Exception e){
			throw new WADLException("Lettura del wadl non riuscita (Document): "+e.getMessage(),e);
		}
	}
	/**
	 * Legge un WADL dal path in parametro e ne ritorna la rappresentazione a oggetti.
	 * @param elem Element da cui leggere il WADL.
	 * @return La definition corretta o null in caso di path mancante o errori.
	 */
	public ApplicationWrapper readWADLFromDocument(Logger log,Element elem) throws WADLException{
		return readWADLFromDocument(log, elem, false, true, true);
	}
	public ApplicationWrapper readWADLFromDocument(Logger log, Element elem,boolean verbose,boolean processInclude, boolean processInlineSchema) throws WADLException{
		try{
			if(this.xmlUtils==null){
				throw new Exception("XMLUtils not initialized in WADLUtilities, use static instance 'getInstance(AbstractXMLUtils xmlUtils)'");
			}
			if (elem == null) 
				throw new Exception("Element non definito");
			try {
				byte[]wadl = this.xmlUtils.toByteArray(elem);
				return this.readWADLFromBytes(log, wadl, verbose, processInclude, processInlineSchema);
			} catch (WADLException e) {
				throw e;
			}
		}catch(Exception e){
			throw new WADLException("Lettura del wadl non riuscita (Document): "+e.getMessage(),e);
		}
	}
	/**
	 * Legge un WADL dal path in parametro e ne ritorna la rappresentazione a oggetti.
	 * @param uri La URI da cui leggere il WADL.
	 * @return La definition corretta o null in caso di path mancante o errori.
	 */
	public ApplicationWrapper readWADLFromURI(Logger log,URI uri) throws WADLException{
		return readWADLFromURI(log, uri, false, true, true);
	}
	public ApplicationWrapper readWADLFromURI(Logger log, URI uri, boolean verbose,boolean processInclude, boolean processInlineSchema) throws WADLException{
		try{
			if (uri == null) 
				throw new Exception("URI non definita");
			WADLReader reader = getWADLReader(log, verbose, processInclude, processInlineSchema);
			ApplicationNode appNode = reader.readWADL(uri);
			ApplicationWrapper ap = new ApplicationWrapper(appNode, reader.getResources(), reader.getMappingNamespaceLocations(), this.xmlUtils);
			return ap;
		}catch(Exception e){
			throw new WADLException("Lettura del wadl non riuscita (URI): "+e.getMessage(),e);
		}
	}













	/* ---------------- METODI GET ------------------------- */

	public Node getIfExistsApplicationElementIntoWADL(Document wadl) throws org.openspcoop2.utils.wadl.WADLException{

		try{
			NodeList list = wadl.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("application".equals(child.getLocalName())){
						return child;
					}
				}
			}
			return null;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wadl.WADLException("Riscontrato errore durante la lettura del wadl: "+e.getMessage(),e);
		}
	}

	public Node getIfExistsGrammarsElementIntoWADL(Document wadl) throws org.openspcoop2.utils.wadl.WADLException{

		try{
			NodeList list = wadl.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("application".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("grammars".equals(childDefinition.getLocalName())){
									return childDefinition;
								}
							}
						}
					}
				}
			}
			return null;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wadl.WADLException("Riscontrato errore durante la lettura del wadl: "+e.getMessage(),e);
		}
	}








	/* ---------------- METODI READ IMPORTS ------------------------- */

	public List<Node> readIncludes(Document wadl) throws org.openspcoop2.utils.wadl.WADLException{

		try{
			Vector<Node> includes = new Vector<Node>();

			NodeList list = wadl.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("application".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("grammars".equals(childDefinition.getLocalName())){
									NodeList listGrammars = childDefinition.getChildNodes();
									if(listGrammars!=null){
										for (int k = 0; k < listGrammars.getLength(); k++) {
											Node childGrammar = listGrammars.item(k);
											if("include".equals(childGrammar.getLocalName())){
												includes.add(childGrammar);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			return includes;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wadl.WADLException("Riscontrato errore durante la lettura del wadl: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/* ---------------- METODI REMOVE IMPORTS ------------------------- */
	
	public List<Node> removeIncludes(Document wadl) throws org.openspcoop2.utils.wadl.WADLException{
		
		try{
			Vector<Node> includes = new Vector<Node>();
			
			NodeList list = wadl.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("application".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("grammars".equals(childDefinition.getLocalName())){
									NodeList listGrammars = childDefinition.getChildNodes();
									if(listGrammars!=null){
										for (int k = 0; k < listGrammars.getLength(); k++) {
											Node childGrammar = listGrammars.item(k);
											if("include".equals(childGrammar.getLocalName())){
												includes.add(childGrammar);
												childDefinition.removeChild(childGrammar);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			return includes;
		}catch(Exception e){
			throw new org.openspcoop2.utils.wadl.WADLException("Riscontrato errore durante la lettura del wadl: "+e.getMessage(),e);
		}
	}
	
	
	





	/* ---------------- METODI NAVIGAZIONE WADL ------------------------- */

	public static ResourceNode findResourceNode(ApplicationNode application,String url) throws WADLException{
		if(application == null)
			throw new WADLException("ApplicationNode non fornita");
		return findResourceNode(application.getResources(), url);
	}

	private static ResourceNode findResourceNode(List<ResourceNode> resources,String url) throws WADLException{


		String baseURI = null;

		for (int i = 0; i< resources.size() && baseURI == null; i++) {

			ResourceNode resourceNode = resources.get(i);

			if(baseURI == null && resourceNode.getAllResourceUriTemplate().equals("/")) { //significa che siamo nel primo nodo, che ci serve per ricavare la baseURI
				baseURI = resourceNode.getUriTemplate();
			}

		}

		String[] urlList = extractUrlList(baseURI, url);

		return getResourceNode(urlList, 0, resources);
	}

	private static String[] extractUrlList(String baseURI, String url) throws WADLException{
		if(url == null)
			throw new WADLException("URL non fornita");

		List<String> urlList = new ArrayList<String>();

		if(baseURI != null) {
			urlList.add(baseURI);
			if(url.startsWith(baseURI)) {
				url = url.substring(baseURI.length(), url.length());
			}
		}

		for(String s : url.split("/")) {
			if(!s.equals("")) {
				urlList.add("/"+s);
			}
		}

		return urlList.toArray(new String[] {});

	}

	public static boolean isTemplate(String partialURL) {
		String realPartialUrl = partialURL;
		if(realPartialUrl.startsWith("/")) {
			realPartialUrl = realPartialUrl.substring(1);
		}
		return realPartialUrl.startsWith("$");
	}
	
	public static WADLOperation findOperation(List<WADLOperation> operations, String url, HTTPMethods method) throws WADLException {
		String[] urlSplit = extractUrlList(null, url);
		for(WADLOperation op: operations) {
			String[] urlList = extractUrlList(null, op.getPath());
			List<WADLOperation> lst = new ArrayList<WADLOperation>();
			WADLOperation root =  getTreeWadlOperation(op, urlList[0]);
			lst.add(root);
			
			for (int i = 1; i < urlList.length; i++) {
				WADLOperation wadlOp = getTreeWadlOperation(root, urlList[i]);
				lst.add(wadlOp);
			}
			WADLOperation found = getWADLOperation(urlSplit, 0, lst, method);
			if(found != null) {
				return found;
			}
		}
		throw new WADLException("Risorsa ["+url+"] non definita");
	}
	
	private static WADLOperation getTreeWadlOperation(WADLOperation father, String url) {
		WADLOperation child = new WADLOperation();
		child.setMethod(father.getMethod());
		child.setName(father.getName());
		child.setPath(url);
		child.setTemplate(isTemplate(url));
		return child;
	}

	private static WADLOperation getWADLOperation(String[] url, int index, List<WADLOperation> operations, HTTPMethods method) throws WADLException{
		
		if(url.length != operations.size()) { // Non e' sicuramente l'operation giusta
			return null;
		}
		
		WADLOperation operationFound = null;

		if(method.equals(operations.get(index).getMethod())) {
			if(operations.get(index).getPath().equals(url[index])) { //risorsa precisa
				operationFound = operations.get(index);
			} else if(operations.get(index).isTemplate()) { //risorsa template
				operationFound = operations.get(index);
			}
		}

		if(operationFound != null) {
			if(index < url.length-1) {
				WADLOperation rn = getWADLOperation(url, index+1, operations, method);
				if(rn != null) {
					return rn;
				}
			} else {
				return operationFound;
			}
		}

		return null;

	}	

	private static ResourceNode getResourceNode(String[] url, int index, List<ResourceNode> resources) throws WADLException{
		ResourceNode resourceFound = null;
		for(int i = 0; i< resources.size() && resourceFound == null; i++) {
			ResourceNode resourceNode = resources.get(i);

			if(resourceNode.getPathSegment().getTemplate().equals(url[index])) { //risorsa precisa
				resourceFound = resourceNode;
			} else if(resourceNode.getPathSegment().getTemplateParameters().size() > 0) { //risorsa template
				resourceFound = resourceNode;
			}

		}
		if(resourceFound != null) {
			if(index < url.length-1) {
				ResourceNode rn = getResourceNode(url, index+1, resourceFound.getChildResources());
				if(rn != null) {
					return rn;
				}
			} else {
				return resourceFound;
			}
		} else {
			String urlString = "";

			for(String s: url) {
				urlString += s;
			}

			throw new WADLException("Risorsa ["+urlString+"] non definita");
		}

		return null;

	}

	public static MethodNode findMethodNode(ResourceNode resourceNode,HTTPMethods httpMethod) throws org.openspcoop2.utils.wadl.WADLException{

		if(httpMethod == null)
			throw new WADLException("Metodo HTTP da cercare non fornito");

		for(MethodNode method: resourceNode.getMethods()) {
			if(httpMethod.value().equals(method.getName())) {
				return method;
			}
		} 

		throw new WADLException("Metodo ["+httpMethod+"] non definito per la risorsa ["+resourceNode.getAllResourceUriTemplate()+"]");
	}


}
