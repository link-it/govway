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
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.xml.namespace.QName;

import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Classe utilizzata per leggere/modificare i wsdl
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lezza Aldo (lezza@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DefinitionWrapper implements javax.wsdl.Definition{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** WSDL Definitorio */
	private javax.wsdl.Definition wsdlDefinition = null; 
	
	/** Eventuale Path del wsdl caricato */
	private String path;
	
	
	private AbstractXMLUtils xmlUtils = null;
	private WSDLUtilities wsdlUtilities = null;
	
	
	/** ---------------------------- Costruttori --------------------------- */
	/** NOTA: le importsDocument riguardano i WSDL Imports e non gli xsd imports!!!!!!! */
	
	/**
	 * Costruttore. Inizializza questo WSDLWrapper leggendo il WSDL dal path.
	 * @param file Il file dal quale leggere il WSDL.
	 */
	public DefinitionWrapper(File file,AbstractXMLUtils xmlUtils)throws WSDLException{
		this(file,xmlUtils,false,true);
	}
	public DefinitionWrapper(File file,AbstractXMLUtils xmlUtils,boolean verbose,boolean importsDocument)throws WSDLException{
		if (file != null){
			this.wsdlDefinition = this.wsdlUtilities.readWSDLFromFile(file,verbose,importsDocument);
			this.path = file.getAbsolutePath();
		}else{
			throw new WSDLException("WSDL(File file)","File wsdl non fornitp");
		}
	}
	
	/**
	 * Costruttore. Inizializza questo WSDLWrapper leggendo il WSDL dal path.
	 * @param path Il path dal quale leggere il WSDL.
	 */
	public DefinitionWrapper(String path,AbstractXMLUtils xmlUtils)throws WSDLException{
		this(path,xmlUtils,false,true);
	}
	public DefinitionWrapper(String path,AbstractXMLUtils xmlUtils,boolean verbose,boolean importsDocument)throws WSDLException{
		this.xmlUtils = xmlUtils;
		this.wsdlUtilities = WSDLUtilities.getInstance(this.xmlUtils);
		if (path != null){
			this.wsdlDefinition = this.wsdlUtilities.readWSDLFromLocation(path,verbose,importsDocument);
			this.path = path;
		}else{
			throw new WSDLException("WSDL(String path)","Location del wsdl non fornita");
		}
	}

	/**
	 * Costruttore. Inizializza questo WSDLWrapper leggendo il WSDL da bytes.
	 * @param bytes I bytes da cui leggere il WSDL.
	 */
	public DefinitionWrapper(byte[] bytes,AbstractXMLUtils xmlUtils)throws WSDLException{
		this(bytes,xmlUtils,false,true);
	}
	public DefinitionWrapper(byte[] bytes,AbstractXMLUtils xmlUtils,boolean verbose,boolean importsDocument)throws WSDLException{
		this.xmlUtils = xmlUtils;
		this.wsdlUtilities = WSDLUtilities.getInstance(this.xmlUtils);
		if (bytes != null){
			this.wsdlDefinition = this.wsdlUtilities.readWSDLFromBytes(bytes,verbose,importsDocument);
		}else{
			throw new WSDLException("WSDL(byte[] bytes)","Bytes del wsdl non forniti");
		}
	}

	/**
	 * Costruttore. Inizializza questo WSDLWrapper da un documento WSDL gia' letto.
	 * @param doc Il Document con il WSDL gia' parsato.
	 */
	public DefinitionWrapper(Document doc,AbstractXMLUtils xmlUtils)throws WSDLException{
		this(doc,xmlUtils,false,true);
	}
	public DefinitionWrapper(Document doc,AbstractXMLUtils xmlUtils,boolean verbose,boolean importsDocument) throws WSDLException {
		this.xmlUtils = xmlUtils;
		this.wsdlUtilities = WSDLUtilities.getInstance(this.xmlUtils);
		if (doc != null){
			this.wsdlDefinition = this.wsdlUtilities.readWSDLFromDocument(doc,verbose,importsDocument);
		}else{
			throw new WSDLException("WSDL(Document doc)","Document del wsdl non fornito");
		}
	}

	/**
	 * Costruttore. Inizializza questo WSDLWrapper da un documento WSDL gia' letto in un elemento XML.
	 * @param el L'elemento root del documento XML.
	 */
	public DefinitionWrapper(Element el,AbstractXMLUtils xmlUtils)throws WSDLException{
		this(el,xmlUtils,false,true);
	}
	public DefinitionWrapper(Element el,AbstractXMLUtils xmlUtils,boolean verbose,boolean importsDocument) throws WSDLException{
		this.xmlUtils = xmlUtils;
		this.wsdlUtilities = WSDLUtilities.getInstance(this.xmlUtils);
		if (el != null){
			this.wsdlDefinition = this.wsdlUtilities.readWSDLFromElement(el,verbose,importsDocument);
		}else{
			throw new WSDLException("WSDL(Element el)","Element del wsdl non fornito");
		}
	}
	/**
	 * Costruttore. Inizializza questo WSDLWrapper da un documento WSDL letto da una URI.
	 * @param uri La URI da cui leggere il WSDL.
	 */
	public DefinitionWrapper(URI uri,AbstractXMLUtils xmlUtils)throws WSDLException{
		this(uri,xmlUtils, false,true);
	}
	public DefinitionWrapper(URI uri,AbstractXMLUtils xmlUtils,boolean verbose,boolean importsDocument) throws WSDLException{
		this.xmlUtils = xmlUtils;
		this.wsdlUtilities = WSDLUtilities.getInstance(this.xmlUtils);
		if (uri != null){
			this.wsdlDefinition = this.wsdlUtilities.readWSDLFromURI(uri,verbose,importsDocument);
			this.path = uri.getPath();
		}else{
			throw new WSDLException("WSDL(URI uri)","URI del wsdl non fornita");
		}
	}
	/**
	 * Costruttore. Inizializza questo WSDLWrapper da un documento WSDL letto da una URI.
	 * @param wsdl il WSDL.
	 */
	public DefinitionWrapper(javax.wsdl.Definition wsdl,AbstractXMLUtils xmlUtils) throws WSDLException{
		this.xmlUtils = xmlUtils;
		this.wsdlUtilities = WSDLUtilities.getInstance(this.xmlUtils);
		if(wsdl==null)
			throw new WSDLException("WSDL(javax.wsdl.Definition wsdl)","WSDL non fornito");
		this.wsdlDefinition = wsdl;
	}
	

	
	
	
	/**
	 * Ritorna la definizione di questo oggetto.
	 * @return La definizione di questo oggetto.
	 */
	public Definition getWsdlDefinition() {
		return this.wsdlDefinition;
	}

	/**
	 * Imposta la definizione di questo oggetto.
	 * @param myDef La definizione da aggiornare.
	 */
	public void setWsdlDefinitio(Definition myDef) {
		this.wsdlDefinition = myDef;
	}
	
	
	
	
	
	/** ----------------- NAMESPACE -------------------------- */
	
	@Override
	public void addNamespace(String arg0, String arg1) {
		this.wsdlDefinition.addNamespace(arg0, arg1);
	}
	
	@Override
	public Map<?,?> getNamespaces() {
		return this.wsdlDefinition.getNamespaces();
	}
	
	@Override
	public String getNamespace(String arg0) {
		return this.wsdlDefinition.getNamespace(arg0);
	}

	@Override
	public String removeNamespace(String arg0) {
		return this.removeNamespace(arg0);
	}
	
	
	
	
	
	
	/** ----------------- IMPORT -------------------------- */
	
	@Override
	public void addImport(Import arg0) {
		this.wsdlDefinition.addImport(arg0);
	}
	
	@Override
	public Import createImport() {
		return this.wsdlDefinition.createImport();
	}
	
	@Override
	public Map<?,?> getImports() {
		return this.wsdlDefinition.getImports();
	}
	
	@Override
	public List<?> getImports(String arg0) {
		return this.wsdlDefinition.getImports(arg0);
	}
	
	@Override
	public Import removeImport(Import arg0) {
		return this.wsdlDefinition.removeImport(arg0);
	}
	
	public void removeAllImports() throws WSDLException{
		try{
			Map<?,?> importsMap = this.wsdlDefinition.getImports();
			Iterator<?> importsIt = importsMap.values().iterator();
			while(importsIt.hasNext()){
				List<?> imports = (List<?>) importsIt.next();
				for(int i = 0; i<imports.size(); i++){
					Import myimport = (Import) imports.get(i);
					this.wsdlDefinition.removeImport(myimport);
				}
			}
		}catch(Exception e){
			throw new WSDLException("removeAllImports()","Rimozione di tutti gli import non riuscita");
		}
	}
	
	
	
	
	
	
	/** ----------------- TYPES -------------------------- */
	
	@Override
	public Types createTypes() {
		return this.wsdlDefinition.createTypes();
	}
	
	@Override
	public Types getTypes() {
		return this.wsdlDefinition.getTypes();
	}
	
	@Override
	public void setTypes(Types arg0) {
		this.wsdlDefinition.setTypes(arg0);
	}
	
	
	
	
	
	
	/** ----------------- MESSAGE -------------------------- */
	
	@Override
	public void addMessage(Message arg0) {
		this.wsdlDefinition.addMessage(arg0);
	}
	
	@Override
	public Message createMessage() {
		return this.wsdlDefinition.createMessage();
	}
	
	@Override
	public Part createPart() {
		return this.wsdlDefinition.createPart();
	}
	
	@Override
	public Map<?,?> getMessages() {
		//return this.wsdlDefinition.getMessages();
		// BUG: vengono generati come messaggi anche quelli definiti negli input/output delle operations, anche se poi non definite realmente come messages nel wsdl.
		// Per evitare il bug, controllo che siano definite le parts
		 Map<?,?> maps = this.wsdlDefinition.getMessages();
		 Map<QName,Message> results = new HashMap<QName, Message>();
		 Iterator<?> itMessages = maps.keySet().iterator();
		 while(itMessages.hasNext()){
			 QName key = (QName) itMessages.next();
			 Message message = (Message) maps.get(key);
			 if(message.getParts()!=null && message.getParts().size()>0){
				 results.put(key, message);
			 }
		 }
		return results;
	}
	
	@Override
	public Message getMessage(QName arg0) {
		return this.wsdlDefinition.getMessage(arg0);
	}
	
	@Override
	public Message removeMessage(QName arg0) {
		return this.wsdlDefinition.removeMessage(arg0);
	}
	
	public void removeAllMessages() throws WSDLException{
		try{
			Map<?,?> m = this.wsdlDefinition.getMessages();
			Iterator<?> it = m.values().iterator();
			Vector<QName> v = new Vector<QName>();
			while (it.hasNext()) {
				v.add(((Message) it.next()).getQName());
			}
			for (int i = 0, j = v.size(); i < j; i++)
				this.wsdlDefinition.removeMessage(v.get(i));
		}catch(Exception e){
			throw new WSDLException("removeAllMessages()","Rimozione di tutti i messaggi non riuscita");
		}
	}
	
	
	
	
	
	
	
	/** ----------------- PORT-TYPE -------------------------- */
	
	@Override
	public void addPortType(PortType arg0) {
		this.wsdlDefinition.addPortType(arg0);
	}
	
	@Override
	public PortType createPortType() {
		return this.wsdlDefinition.createPortType();
	}
	
	@Override
	public Operation createOperation() {
		return this.wsdlDefinition.createOperation();
	}
	
	@Override
	public Input createInput() {
		return this.wsdlDefinition.createInput();
	}

	@Override
	public Output createOutput() {
		return this.wsdlDefinition.createOutput();
	}
	
	@Override
	public Map<?,?> getAllPortTypes() {
		return this.wsdlDefinition.getAllPortTypes();
	}
	
	@Override
	public Map<?,?> getPortTypes() {
		return this.wsdlDefinition.getPortTypes();
	}
	
	@Override
	public PortType getPortType(QName arg0) {
		return this.wsdlDefinition.getPortType(arg0);
	}

	@Override
	public PortType removePortType(QName arg0) {
		return this.wsdlDefinition.removePortType(arg0);
	}

	public void removeAllPortTypes() throws WSDLException{
		try{
			Map<?,?> m = this.wsdlDefinition.getAllPortTypes();
			//System.out.println("REMOVE SIZE: "+m.size());
			Iterator<?> it = m.values().iterator();
			Vector<QName> v = new Vector<QName>();
			while (it.hasNext()){
				PortType pt = (PortType)it.next();
				v.add(pt.getQName());
				//System.out.println("ADD REMOVE: "+pt.getQName());
			}
			for (int i=0; i<v.size(); i++){
				//System.out.println("REMOVE: "+v.get(i));
				this.wsdlDefinition.removePortType(v.get(i));
			}
			m = this.wsdlDefinition.getAllPortTypes();
			//System.out.println("SIZE AFTER REMOVE: "+m.size());
		}catch(Exception e){
			//System.out.println("ERRORE: "+e.getMessage());
			throw new WSDLException("removeAllPortTypes()","Rimozione di tutti i port type non riuscita");
		}
	}
	
	
	
	
	
	
	/** ----------------- BINDING -------------------------- */

	@Override
	public void addBinding(Binding arg0) {
		this.wsdlDefinition.addBinding(arg0);
	}

	@Override
	public Binding createBinding() {
		return this.wsdlDefinition.createBinding();
	}
	
	@Override
	public Fault createFault() {
		return this.wsdlDefinition.createFault();
	}
	
	@Override
	public BindingOperation createBindingOperation() {
		return this.wsdlDefinition.createBindingOperation();
	}
	
	@Override
	public BindingFault createBindingFault() {
		return this.wsdlDefinition.createBindingFault();
	}

	@Override
	public BindingInput createBindingInput() {
		return this.wsdlDefinition.createBindingInput();
	}
	
	@Override
	public BindingOutput createBindingOutput() {
		return this.wsdlDefinition.createBindingOutput();
	}
	
	@Override
	public Map<?,?> getAllBindings() {
		return this.wsdlDefinition.getAllBindings();
	}
	
	@Override
	public Binding getBinding(QName arg0) {
		return this.wsdlDefinition.getBinding(arg0);
	}
	
	@Override
	public Map<?,?> getBindings() {
		return this.wsdlDefinition.getBindings();
	}
	
	@Override
	public Binding removeBinding(QName arg0) {
		return this.wsdlDefinition.removeBinding(arg0);
	}
	
	public void removeAllBindings() throws WSDLException{
		try{
			Map<?,?> m = this.wsdlDefinition.getAllBindings();
			Iterator<?> it = m.values().iterator();
			Vector<QName> v = new Vector<QName>();
			while (it.hasNext()){
				v.add(((Binding)it.next()).getQName());
			}
			for (int i=0, j=v.size(); i<j; i++)
				this.wsdlDefinition.removeBinding(v.get(i));
		}catch(Exception e){
			throw new WSDLException("removeAllBindings()","Rimozione di tutti i bindings non riuscita");
		}
	}
	
	public Hashtable<QName,QName> getMapPortTypesImplementedBinding(){
		Hashtable<QName,QName> list = new Hashtable<QName,QName>(); 
		java.util.Map<?,?> bindings = this.getAllBindings();
		if(bindings==null || bindings.size()==0){
			return list;
		}
		Iterator<?> it = bindings.keySet().iterator();
		while(it.hasNext()) {
			javax.xml.namespace.QName key = (javax.xml.namespace.QName) it.next();
			Binding binding = (Binding) bindings.get(key);
			list.put(binding.getQName(),binding.getPortType().getQName());
		}
		return list;
	}
	
	
	
	
	
	/** ----------------- SERVICES -------------------------- */
	@Override
	public void addService(Service arg0) {
		this.wsdlDefinition.addService(arg0);
	}

	@Override
	public Service createService() {
		return this.wsdlDefinition.createService();
	}
	
	@Override
	public Port createPort() {
		return this.wsdlDefinition.createPort();
	}

	@Override
	public Map<?,?> getAllServices() {
		return this.wsdlDefinition.getAllServices();
	}
	
	@Override
	public Map<?,?> getServices() {
		return this.wsdlDefinition.getServices();
	}
	
	@Override
	public Service getService(QName arg0) {
		return this.wsdlDefinition.getService(arg0);
	}

	@Override
	public Service removeService(QName arg0) {
		return this.wsdlDefinition.removeService(arg0);
	}

	public void removeAllServices() throws WSDLException{
		try{
			Map<?,?> m = this.wsdlDefinition.getAllServices();
			Iterator<?> it = m.values().iterator();
			Vector<QName> v = new Vector<QName>();
			while (it.hasNext()){
				v.add(((Service)it.next()).getQName());
			}
			for (int i=0, j=v.size(); i<j; i++)
				this.wsdlDefinition.removeService(v.get(i));
		}catch(Exception e){
			throw new WSDLException("removeAllServices()","Rimozione di tutti i services non riuscita");
		}
	}


	


	
	
	/** ----------------- OTHER ------------------------- */
	@Override
	public String getDocumentBaseURI() {
		return this.wsdlDefinition.getDocumentBaseURI();
	}

	@Override
	public void setDocumentBaseURI(String arg0) {
		this.wsdlDefinition.setDocumentBaseURI(arg0);
	}

	@Override
	public String getPrefix(String arg0) {
		return this.wsdlDefinition.getPrefix(arg0);
	}

	@Override
	public String getTargetNamespace() {
		return this.wsdlDefinition.getTargetNamespace();
	}
	
	@Override
	public void setTargetNamespace(String arg0) {
		this.wsdlDefinition.setTargetNamespace(arg0);
	}
	
	@Override
	public QName getQName() {
		return this.wsdlDefinition.getQName();
	}
	
	@Override
	public void setQName(QName arg0) {
		this.wsdlDefinition.setQName(arg0);
	}
	
	@Override
	public ExtensionRegistry getExtensionRegistry() {
		return this.wsdlDefinition.getExtensionRegistry();
	}
	
	@Override
	public void setExtensionRegistry(ExtensionRegistry arg0) {
		this.wsdlDefinition.setExtensionRegistry(arg0);
	}

	@Override
	public Element getDocumentationElement() {
		return this.wsdlDefinition.getDocumentationElement();
	}
	
	@Override
	public void setDocumentationElement(Element arg0) {
		this.wsdlDefinition.setDocumentationElement(arg0);
	}

	@Override
	public Object getExtensionAttribute(QName arg0) {
		return this.wsdlDefinition.getExtensionAttribute(arg0);
	}

	@Override
	public Map<?,?> getExtensionAttributes() {
		return this.wsdlDefinition.getExtensionAttributes();
	}
	
	@Override
	public void setExtensionAttribute(QName arg0, Object arg1) {
		this.setExtensionAttribute(arg0, arg1);
	}

	@Override
	public List<?> getNativeAttributeNames() {
		return this.getNativeAttributeNames();
	}
	
	@Override
	public void addExtensibilityElement(ExtensibilityElement arg0) {
		this.wsdlDefinition.addExtensibilityElement(arg0);
	}


	@Override
	public List<?> getExtensibilityElements() {
		return this.wsdlDefinition.getExtensibilityElements();
	}


	@Override
	public ExtensibilityElement removeExtensibilityElement(
			ExtensibilityElement arg0) {
		return this.wsdlDefinition.removeExtensibilityElement(arg0);
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	
	
	
	
	
	
	/** ----------------- WRITE TO ------------------------- */
	
	public byte[] toByteArray() throws WSDLException{
		return toByteArray(false);
	}
	public byte[] toByteArray(boolean prettyPrint) throws WSDLException{
		try{			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.wsdlUtilities.writeWsdlTo(this.wsdlDefinition, bout,prettyPrint);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new WSDLException("toByteArray()","Conversione in byte[] non riuscita: "+e.getMessage());
		}
	}
	@Override
	public String toString() {
		return toString(false);
	}
	public String toString(boolean prettyPrint) {
		try{
			return new String(this.toByteArray(prettyPrint));
		}catch(Exception e){
			return null;
		}
	}
	
	public void writeTo(OutputStream out) throws IOException,WSDLException,org.openspcoop2.utils.wsdl.WSDLException{
		writeTo(out,false);
	}
	public void writeTo(OutputStream out,boolean prettyPrint) throws IOException,WSDLException,org.openspcoop2.utils.wsdl.WSDLException{
		this.wsdlUtilities.writeWsdlTo(this.wsdlDefinition, out,prettyPrint);
	}
	
	public void writeTo(Writer writer) throws IOException,WSDLException,org.openspcoop2.utils.wsdl.WSDLException{
		writeTo(writer,false);
	}
	public void writeTo(Writer writer,boolean prettyPrint) throws IOException,WSDLException,org.openspcoop2.utils.wsdl.WSDLException{
		this.wsdlUtilities.writeWsdlTo(this.wsdlDefinition, writer,prettyPrint);
	}
	
	public void writeTo(File file) throws IOException,WSDLException,org.openspcoop2.utils.wsdl.WSDLException{
		writeTo(file,false);
	}
	public void writeTo(File file,boolean prettyPrint) throws IOException,WSDLException,org.openspcoop2.utils.wsdl.WSDLException{
		this.wsdlUtilities.writeWsdlTo(this.wsdlDefinition, file,prettyPrint);
	}
	
	public void writeTo(String absoluteFilePath) throws IOException,WSDLException,org.openspcoop2.utils.wsdl.WSDLException{
		writeTo(absoluteFilePath,false);
	}
	public void writeTo(String absoluteFilePath,boolean prettyPrint) throws IOException,WSDLException,org.openspcoop2.utils.wsdl.WSDLException{
		this.wsdlUtilities.writeWsdlTo(this.wsdlDefinition, absoluteFilePath,prettyPrint);
	}
	
	
	
	
	
	
	
	/** ----------------- VALIDATE ------------------------- */
	
	public void valida() throws org.openspcoop2.utils.wsdl.WSDLException{
		valida(true);
	}
	public void valida(boolean validaBinding) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		// Messages
		Map<?, ?> messages = this.getMessages();
		if(messages==null || messages.size()==0){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Messages non presenti");
		}
		Iterator<?> itMessages = messages.keySet().iterator();
		while(itMessages.hasNext()){
			javax.xml.namespace.QName keyMessage = (javax.xml.namespace.QName) itMessages.next();
			int count = 0;
			Iterator<?> itMessagesCheck = messages.keySet().iterator();
			while(itMessagesCheck.hasNext()){
				javax.xml.namespace.QName keyMessageCheck = (javax.xml.namespace.QName) itMessagesCheck.next();
				if(keyMessageCheck.equals(keyMessage)){
					count++;
				}
			}
			if(count>1){
				throw new org.openspcoop2.utils.wsdl.WSDLException("Wsdl:message "+keyMessage+" definito piu' di una volta");
			}
		}
		
		// Port-types
		Map<?, ?> porttypes = this.getAllPortTypes();
		if(porttypes==null || porttypes.size()==0){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Port types non presenti");
		}
		Iterator<?> itPortTypes = porttypes.keySet().iterator();
		while(itPortTypes.hasNext()){
			javax.xml.namespace.QName keyPT = (javax.xml.namespace.QName) itPortTypes.next();
			int count = 0;
			Iterator<?> itPTCheck = porttypes.keySet().iterator();
			while(itPTCheck.hasNext()){
				javax.xml.namespace.QName keyPTCheck = (javax.xml.namespace.QName) itPTCheck.next();
				if(keyPTCheck.equals(keyPT)){
					count++;
				}
			}
			if(count>1){
				throw new org.openspcoop2.utils.wsdl.WSDLException("Port type "+keyPT+" definito piu' di una volta");
			}
		}
		Iterator<?> it = porttypes.keySet().iterator();
		while(it.hasNext()){
			javax.xml.namespace.QName key = (javax.xml.namespace.QName) it.next();
			javax.wsdl.PortType pt = (javax.wsdl.PortType) porttypes.get(key);
			
			// operation port type
			List<?> operations = pt.getOperations();
			if(operations==null || operations.size()==0){
				throw new org.openspcoop2.utils.wsdl.WSDLException("Port type "+key+" non possiede operations");
			}
			Iterator<?> itOperations = operations.iterator();
			while(itOperations.hasNext()){
				javax.wsdl.Operation op = (javax.wsdl.Operation) itOperations.next();
				String keyOp = op.getName();
				int count = 0;
				Iterator<?> itOperationsCheck = operations.iterator();
				while(itOperationsCheck.hasNext()){
					javax.wsdl.Operation opCheck = (javax.wsdl.Operation) itOperationsCheck.next();
					String keyOpCheck = opCheck.getName();
					if(keyOpCheck.equals(keyOp)){
						count++;
					}
				}
				if(count>1){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Port type "+key+" possiede l'operation "+keyOp+" definita piu' di una volta");
				}
			}
			
			for(int i=0; i<operations.size();i++){
				javax.wsdl.Operation op = (javax.wsdl.Operation) operations.get(i);
			
				Input input = op.getInput();
				Output output = op.getOutput();
				
				String bindingWarning = "";
				try{
					java.util.Map<?,?> bindings = this.getAllBindings();
					if(bindings!=null && bindings.size()>0){
						bindingWarning = " (Verificare che il problema non sia dovuto a errori ereditati da una definizione del binding non corretta (es. port-type indicato nel binding inesistente))";
					}
				}catch(Throwable e){}
				
				// INPUT
				if(input==null){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Non e' stato definito un input per l'operation "+op.getName()+" (Port type "+key+")"+bindingWarning);
				}
				if(input.getMessage()==null){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Non e' stato definito un input message per l'operation "+op.getName()+" (Port type "+key+")"+bindingWarning);
				}
				if(input.getMessage().getQName()==null){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Non e' stato definito un input message (QName) per l'operation "+op.getName()+" (Port type "+key+")"+bindingWarning);
				}
				QName messageInput = input.getMessage().getQName();
				itMessages = messages.keySet().iterator();
				boolean findMessageInput = false;
				while(itMessages.hasNext()){
					javax.xml.namespace.QName keyMessage = (javax.xml.namespace.QName) itMessages.next();
					if( 
							( (messageInput.getNamespaceURI()==null) && (messageInput.getPrefix()==null) ) 
								|| 
							( "".equals(messageInput.getNamespaceURI()) && "".equals(messageInput.getPrefix()) ) 
					){ // workaroung necessario per axiomDocumentBuilder
						if(keyMessage.getLocalPart().equals(messageInput.getLocalPart())){
							findMessageInput = true;
						}
					}else{
						if(keyMessage.equals(messageInput)){
							findMessageInput = true;
						}
					}
				}
				if(!findMessageInput){
					throw new org.openspcoop2.utils.wsdl.WSDLException("E' stato associato un wsdl:message inesistente ("+messageInput+")  all'input dell'operation "+op.getName()+" (Port type "+key+")"+bindingWarning);
				}
				
				// OUTPUT
				if(output!=null){
					if(output.getMessage()==null){
						throw new org.openspcoop2.utils.wsdl.WSDLException("Non e' stato definito un output message per l'operation "+op.getName()+" (Port type "+key+")"+bindingWarning);
					}
					if(output.getMessage().getQName()==null){
						throw new org.openspcoop2.utils.wsdl.WSDLException("Non e' stato definito un output message (QName) per l'operation "+op.getName()+" (Port type "+key+")"+bindingWarning);
					}
					
					QName messageOutput = input.getMessage().getQName();
					itMessages = messages.keySet().iterator();
					boolean findMessageOutput = false;
					while(itMessages.hasNext()){
						javax.xml.namespace.QName keyMessage = (javax.xml.namespace.QName) itMessages.next();
						if( 
								( (messageOutput.getNamespaceURI()==null) && (messageOutput.getPrefix()==null) ) 
									|| 
								( "".equals(messageOutput.getNamespaceURI()) && "".equals(messageOutput.getPrefix()) ) 
						){ // workaroung necessario per axiomDocumentBuilder
							if(keyMessage.getLocalPart().equals(messageOutput.getLocalPart())){
								findMessageOutput = true;
							}
						}else{
							if(keyMessage.equals(messageOutput)){
								findMessageOutput = true;
							}
						}
					}
					if(!findMessageOutput){
						throw new org.openspcoop2.utils.wsdl.WSDLException("E' stato associato un wsdl:message inesistente ("+messageOutput+") all'output dell'operation "+op.getName()+" (Port type "+key+")"+bindingWarning);
					}
				}
			}
		}
		
		if(validaBinding){
			
			java.util.Map<?,?> bindings = this.getAllBindings();
			if(bindings==null || bindings.size()==0){
				throw new org.openspcoop2.utils.wsdl.WSDLException("Bindings non presenti");
			}
			Iterator<?> itBindings = bindings.keySet().iterator();
			while(itBindings.hasNext()){
				javax.xml.namespace.QName keyBinding = (javax.xml.namespace.QName) itBindings.next();
				int count = 0;
				Iterator<?> itBindingsCheck = bindings.keySet().iterator();
				while(itBindingsCheck.hasNext()){
					javax.xml.namespace.QName keyBindingCheck = (javax.xml.namespace.QName) itBindingsCheck.next();
					if(keyBindingCheck.equals(keyBinding)){
						count++;
					}
				}
				if(count>1){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Wsdl:binding "+keyBinding+" definito piu' di una volta");
				}
			}
			it = bindings.keySet().iterator();
			while(it.hasNext()) {
				javax.xml.namespace.QName key = (javax.xml.namespace.QName) it.next();
				Binding binding = (Binding) bindings.get(key);
				
				if(binding.getPortType()==null){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Non e' stato associato un port type al binding "+key);
				}
				if(binding.getPortType().getQName()==null){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Non e' stato associato un port type (Qname) al binding "+key);
				}
				
				QName portTypeQname = binding.getPortType().getQName();
				boolean findPT = false;
				Iterator<?> itPT = this.getPortTypes().keySet().iterator();
				while(itPT.hasNext()){
					javax.xml.namespace.QName keyPT = (javax.xml.namespace.QName) itPT.next();
					if(keyPT.equals(portTypeQname)){
						findPT =  true;
						break;
					}
				}
				if(!findPT){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Il port type "+portTypeQname+" associato al  binding "+key +" non esiste");
				}
				
				
			}
			
			java.util.Map<?,?> services = this.getAllServices();
			if(services==null || services.size()==0){
				throw new org.openspcoop2.utils.wsdl.WSDLException("Services non presenti");
			}
			Iterator<?> itServices = services.keySet().iterator();
			while(itServices.hasNext()){
				javax.xml.namespace.QName keyService = (javax.xml.namespace.QName) itServices.next();
				int count = 0;
				Iterator<?> itServicesCheck = services.keySet().iterator();
				while(itServicesCheck.hasNext()){
					javax.xml.namespace.QName keyServiceCheck = (javax.xml.namespace.QName) itServicesCheck.next();
					if(keyServiceCheck.equals(keyService)){
						count++;
					}
				}
				if(count>1){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Wsdl:service "+keyService+" definito piu' di una volta");
				}
			}
			it = services.keySet().iterator();
			while(it.hasNext()) {
				javax.xml.namespace.QName keyService = (javax.xml.namespace.QName) it.next();
				Service service = (Service) services.get(keyService);
				
				java.util.Map<?,?> ports = service.getPorts();
				if(ports==null || ports.size()==0){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Ports non presenti per il service "+keyService);
				}
				
				Iterator<?> itPort = ports.keySet().iterator();
				while(itPort.hasNext()) {
					String keyPort = (String) itPort.next();
					Port port = (Port) ports.get(keyPort);
					
					if(port.getBinding()==null){
						throw new org.openspcoop2.utils.wsdl.WSDLException("Wsdl:port "+keyPort+" non possiede un binding associato (service "+keyService+")");
					}
					if(port.getBinding().getQName()==null){
						throw new org.openspcoop2.utils.wsdl.WSDLException("Wsdl:port "+keyPort+" non possiede un binding (QName) associato (service "+keyService+")");
					}
					
					QName bindingQName = port.getBinding().getQName();
					boolean findBinding = false;
					itBindings = this.getAllBindings().keySet().iterator();
					while(itBindings.hasNext()){
						javax.xml.namespace.QName keyBinding = (javax.xml.namespace.QName) itBindings.next();
						if(keyBinding.equals(bindingQName)){
							findBinding =  true;
							break;
						}
					}
					if(!findBinding){
						throw new org.openspcoop2.utils.wsdl.WSDLException("Binding  "+bindingQName+" associato al  wsdl:port "+keyPort +" non esiste (service "+keyService+")");
					}
				}
			}
		}
	}
	
	public void validaBinding(List<String> portTypes) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		java.util.Map<?,?> bindings = this.getAllBindings();
		if(bindings==null || bindings.size()==0){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Bindings non presenti");
		}
		Iterator<?> itBindings = bindings.keySet().iterator();
		while(itBindings.hasNext()){
			javax.xml.namespace.QName keyBinding = (javax.xml.namespace.QName) itBindings.next();
			int count = 0;
			Iterator<?> itBindingsCheck = bindings.keySet().iterator();
			while(itBindingsCheck.hasNext()){
				javax.xml.namespace.QName keyBindingCheck = (javax.xml.namespace.QName) itBindingsCheck.next();
				if(keyBindingCheck.equals(keyBinding)){
					count++;
				}
			}
			if(count>1){
				throw new org.openspcoop2.utils.wsdl.WSDLException("Wsdl:binding "+keyBinding+" definito piu' di una volta");
			}
		}
		Iterator<?> it = bindings.keySet().iterator();
		while(it.hasNext()) {
			javax.xml.namespace.QName key = (javax.xml.namespace.QName) it.next();
			Binding binding = (Binding) bindings.get(key);
			
			if( !(
					( (key.getNamespaceURI()==null) && (key.getPrefix()==null) ) 
						|| 
					( "".equals(key.getNamespaceURI()) && "".equals(key.getPrefix()) )
				)
			){ // workaroung necessario per axiomDocumentBuilder
			
				if(binding.getPortType()==null){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Non e' stato associato un port type al binding "+key);
				}
				if(binding.getPortType().getQName()==null){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Non e' stato associato un port type (QName) al binding "+key);
				}
				
				QName portTypeQname = binding.getPortType().getQName();
				boolean findPT = false;
				for(int i=0; i<portTypes.size(); i++){
					String keyPT = portTypes.get(i);
					//System.out.println("CHECK ["+keyPT+"]==["+portTypes.get(i)+"]");
					if(keyPT.equals(portTypeQname.getLocalPart())){
						//System.out.println("FIND");
						findPT =  true;
						break;
					}
				}
				if(!findPT){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Il port type "+portTypeQname+" associato al  binding "+key +" non esiste");
				}
			}
			
			
		}
		
		java.util.Map<?,?> services = this.getAllServices();
		if(services==null || services.size()==0){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Services non presenti");
		}
		Iterator<?> itServices = services.keySet().iterator();
		while(itServices.hasNext()){
			javax.xml.namespace.QName keyService = (javax.xml.namespace.QName) itServices.next();
			int count = 0;
			Iterator<?> itServicesCheck = services.keySet().iterator();
			while(itServicesCheck.hasNext()){
				javax.xml.namespace.QName keyServiceCheck = (javax.xml.namespace.QName) itServicesCheck.next();
				if(keyServiceCheck.equals(keyService)){
					count++;
				}
			}
			if(count>1){
				throw new org.openspcoop2.utils.wsdl.WSDLException("Wsdl:service "+keyService+" definito piu' di una volta");
			}
		}
		it = services.keySet().iterator();
		while(it.hasNext()) {
			javax.xml.namespace.QName keyService = (javax.xml.namespace.QName) it.next();
			Service service = (Service) services.get(keyService);
			
			java.util.Map<?,?> ports = service.getPorts();
			if(ports==null || ports.size()==0){
				throw new org.openspcoop2.utils.wsdl.WSDLException("Ports non presenti per il service "+keyService);
			}
			
			Iterator<?> itPort = ports.keySet().iterator();
			while(itPort.hasNext()) {
				String keyPort = (String) itPort.next();
				Port port = (Port) ports.get(keyPort);
				
				if(port.getBinding()==null){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Wsdl:port "+keyPort+" non possiede un binding associato (service "+keyService+")");
				}
				if(port.getBinding().getQName()==null){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Wsdl:port "+keyPort+" non possiede un binding (QName) associato (service "+keyService+")");
				}
				
				QName bindingQName = port.getBinding().getQName();
				boolean findBinding = false;
				itBindings = this.getAllBindings().keySet().iterator();
				while(itBindings.hasNext()){
					javax.xml.namespace.QName keyBinding = (javax.xml.namespace.QName) itBindings.next();
					if(keyBinding.equals(bindingQName)){
						findBinding =  true;
						break;
					}
				}
				if(!findBinding){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Binding  "+bindingQName+" associato al  wsdl:port "+keyPort +" non esiste (service "+keyService+")");
				}
			}
		}
	}
	
}
