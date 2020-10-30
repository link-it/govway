/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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



package org.openspcoop2.message.soap;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.mail.internet.ContentType;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.Text;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Libreria contenente metodi utili per la gestione della busta Soap.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SoapUtils {
	
	
	
	// SOAP FACTORY
	
	public static SOAPFactory getSoapFactory(OpenSPCoop2MessageFactory messageFactory, MessageType messageType) throws MessageNotSupportedException {
		if(MessageType.SOAP_11.equals(messageType))
			return messageFactory.getSoapFactory11();
		else if(MessageType.SOAP_12.equals(messageType))
			return messageFactory.getSoapFactory12();
		else
			throw MessageNotSupportedException.newMessageNotSupportedException(messageType);
	}
	public static MessageFactory getMessageFactory(OpenSPCoop2MessageFactory messageFactory) throws SOAPException {
		return messageFactory.getSoapMessageFactory();
	}
		
	
	// SOAP Fault String 'Locale' for LANG
	
	private static Locale soapFaultStringLocale = null;
	public static void setSoapFaultStringLocale(Locale soapFaultStringLocale) {
		SoapUtils.soapFaultStringLocale = soapFaultStringLocale;
	}
	public static void setFaultString(SOAPFault fault, String faultString) throws SOAPException {
		 setFaultString(fault, faultString, null);
	}
	public static void setFaultString(SOAPFault fault, String faultString, Locale lParam) throws SOAPException {
		if(lParam!=null) {
			fault.setFaultString(faultString, lParam);
		}
		else if(soapFaultStringLocale!=null) {
			fault.setFaultString(faultString, soapFaultStringLocale);
		}
		else {
			fault.setFaultString(faultString);
		}
	}
	
	
	
	
	// SOAP Content Type
	
	public static String getContentType(SOAPMessage msg) {
	 	if(msg.getMimeHeaders()==null) {
    		return null;
    	}
    	String[] values = msg.getMimeHeaders().getHeader(HttpConstants.CONTENT_TYPE);
    	if (values == null || values.length<=0) {
    		return null;
    	}
    	else {
    		return values[0];
    	}
	}
	
	
	
	
	// SOAPAction
	
	public static String getSoapAction(TransportRequestContext transportRequestContext, MessageType messageType, String contentType) throws MessageException, MessageNotSupportedException{

		if(MessageType.SOAP_11.equals(messageType)){
		
			if(transportRequestContext.getParametersTrasporto().size()<=0){
				throw new MessageException("Header http '"+Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+"' non valorizzato (nessun header di trasporto trovato)");
			}
			
			Iterator<String> enTrasporto = transportRequestContext.getParametersTrasporto().keySet().iterator();
			while(enTrasporto.hasNext()){
				String nomeProperty = (String)enTrasporto.next();
				if(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION.equalsIgnoreCase(nomeProperty)){
					//System.out.println("TROVATO SOAP ACTION: "+req.getHeader(nomeProperty));
					String soapAction = transportRequestContext.getParameterTrasporto(nomeProperty);
					if(soapAction==null){
						throw new MessageException("Header http '"+Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+"' non valorizzato (null)");
					}
					soapAction = soapAction.trim();
					if(soapAction.startsWith("\"")){
						if(!soapAction.endsWith("\"")){
							throw new MessageException("Header http '"+Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+"' non valorizzato correttamente (action quotata? Non è stato trovato il carattere di chiusura \" ma è presente quello di apertura)");
						}	
					}
					if(soapAction.endsWith("\"")){
						if(!soapAction.startsWith("\"")){
							throw new MessageException("Header http '"+Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+"' non valorizzato correttamente (action quotata? Non è stato trovato il carattere di apertura \" ma è presente quello di chiusura)");
						}	
					}
					return soapAction;
				}
			}
	
			throw new MessageException("Header http '"+Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+"' non presente");
			
		}
		else if(MessageType.SOAP_12.equals(messageType)){
			// The SOAP 1.1 mandatory SOAPAction HTTP header has been removed in SOAP 1.2. In its place is an optional action parameter on the application/soap+xml media type.
			ContentType ct = null;
			try{
				ct = new ContentType(contentType);
			}catch(Exception e){
				throw new MessageException("Content-Type '"+contentType+"' non valorizzato correttamente: "+e.getMessage(),e);
			}
			if(ct.getParameterList()!=null && ct.getParameterList().size()>0){
				Enumeration<?> names = ct.getParameterList().getNames();
				while (names.hasMoreElements()) {
					String name = (String) names.nextElement();
					if(Costanti.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION.equals(name)){
						return ct.getParameterList().get(name);
					}	
				}
			}
			
			return null;
		}
		else{
			throw MessageNotSupportedException.newMessageNotSupportedException(messageType);
		}
		
	}
	
	public static void checkSoapActionQuotedString(String soapAction,MessageType messageType) throws MessageException, MessageNotSupportedException{
		if(MessageType.SOAP_11.equals(messageType)){
			// WS-I BasicProfile 1.1: R1109   The value of the SOAPAction HTTP header field in a HTTP request MESSAGE MUST be a quoted string.
			if(!soapAction.startsWith("\"") || !soapAction.endsWith("\"")){
				throw new MessageException("Header http '"+Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+"' valorizzato tramite una stringa non quotata (WSI-BP-1.1 R1109)");
			}
		}
		else{
			throw MessageNotSupportedException.newMessageNotSupportedException(messageType);
		}
	}
	
	
	
	
	
	// SOAP HEADER E MUST UNDERSTAND
	
	public static String getSoapActor(SOAPHeaderElement headerElement, MessageType messageType) throws MessageNotSupportedException{
		
		if(MessageType.SOAP_11.equals(messageType)){
			return headerElement.getActor();
		}
		else if(MessageType.SOAP_12.equals(messageType)){
			return headerElement.getRole();
		}
		else{
			throw MessageNotSupportedException.newMessageNotSupportedException(messageType);
		}
	}
	
	public static boolean checkMustUnderstandHeaderElement(MessageType messageType, SOAPHeader header,List<NameValue> filtri, StringBuilder bfErrorParam) throws MessageException {
		
		
		StringBuilder bfError = new StringBuilder();
		try{
			java.util.Iterator<?> headers = header.getChildElements();
			while (headers.hasNext()) {
				Object element = headers.next();
				if(!(element instanceof SOAPHeaderElement)){
					continue;
				}
				
				SOAPHeaderElement elementHeader = (SOAPHeaderElement) element;
				
				String actor = SoapUtils.getSoapActor(elementHeader, messageType);
				
				// Prendo gli headers con MustUnderstand="1" senza Actor
				if(actor==null && elementHeader.getMustUnderstand()==true){
					boolean checked = false;
					if(filtri!=null && filtri.size()>0){
						for (NameValue nameValue : filtri) {
							String localName = nameValue.getName();
							String namespaceURI = nameValue.getValue();
							if(localName.equals(elementHeader.getLocalName()) && namespaceURI.equals(elementHeader.getNamespaceURI())){
								// Ok si bypassa.
								checked = true;
								break;
							}
						}
					}
					
					// Controllo se abbiamo bypassato l'header
					if(!checked){
						// Abbiamo un MustUnderstand="1" senza Actor che non appare nella lista da Bypassare!

						if(bfError.length()>0){
							bfError.append(", ");
						}
						bfError.append("{").append(elementHeader.getNamespaceURI()).append("}").append(elementHeader.getLocalName());
						
					}
				}
			}
		}catch(java.lang.Exception e) {
			
			Throwable t = ParseExceptionUtils.getParseException(e);
			if(t!=null){
				throw new MessageException(e);
			}
			
			throw new MessageException("Riscontrato errore durante l'applicazione del bypassMustUnderstandHeader: "+e.getMessage(),e);
		}  
		
		if(bfError.length()>0){
			bfErrorParam.append(bfError.toString());
		}
		return bfError.length()<=0;
	}
	
	
	
	
	
	// GET NODES e ELEMENT
	
	public static List<Node> getNotEmptyChildNodes(OpenSPCoop2MessageFactory messageFactory, Node e){
		return getNotEmptyChildNodes(messageFactory, e, true);
	}
	public static List<Node> getNotEmptyChildNodes(OpenSPCoop2MessageFactory messageFactory, Node e, boolean consideraTextNotEmptyAsNode){
		return XMLUtils.getInstance(messageFactory).getNotEmptyChildNodes(e, consideraTextNotEmptyAsNode);
	}
	
	public static Node getFirstNotEmptyChildNode(OpenSPCoop2MessageFactory messageFactory, Node e){
		return getFirstNotEmptyChildNode(messageFactory, e, true);
	}
	public static Node getFirstNotEmptyChildNode(OpenSPCoop2MessageFactory messageFactory, Node e, boolean consideraTextNotEmptyAsNode){
		return XMLUtils.getInstance(messageFactory).getFirstNotEmptyChildNode(e, consideraTextNotEmptyAsNode);
	}
	
	public static List<SOAPElement> getNotEmptyChildSOAPElement(SOAPElement e){
		List<SOAPElement> vec = new ArrayList<SOAPElement>();
		Iterator<?> soapElements = e.getChildElements();
		while (soapElements.hasNext()) {
			Object soapElement = soapElements.next();
			if(soapElement instanceof SOAPElement){
				vec.add((SOAPElement)soapElement);
			}
		}
		return vec;
	}
	
	public static SOAPElement getNotEmptyFirstChildSOAPElement(SOAPElement e){
		Iterator<?> soapElements = e.getChildElements();
		while (soapElements.hasNext()) {
			Object soapElement = soapElements.next();
			if(soapElement instanceof SOAPElement){
				return (SOAPElement)soapElement;
			}
		}
		return null;
	}

	
	
	// VERSION, NAMESPACE e CONTENT-TYPE
	
	public static String getSoapEnvelopeNS(MessageType messageType) throws MessageNotSupportedException {
		if(MessageType.SOAP_11.equals(messageType))
			return Costanti.SOAP_ENVELOPE_NAMESPACE;
		else if(MessageType.SOAP_12.equals(messageType))
			return Costanti.SOAP12_ENVELOPE_NAMESPACE;
		else
			throw MessageNotSupportedException.newMessageNotSupportedException(messageType);
	}
	
	public static String getSoapContentTypeForMessageWithoutAttachments(MessageType messageType) throws MessageNotSupportedException{
		if(MessageType.SOAP_11.equals(messageType) || MessageType.SOAP_12.equals(messageType))
			return MessageUtilities.getDefaultContentType(messageType);
		else
			throw MessageNotSupportedException.newMessageNotSupportedException(messageType);
	}
	
	public static MessageType getSOAPVersion(Logger log, byte [] xml){
		// La posizione dovrebbe garantirmi il giusto namespace
		// Nel caso all'interno del body viene usato l'altro.
		String s = new String(xml);
		if( (s.contains("<Envelope") || s.contains(":Envelope") ) ){
			int indexOfSoap11 = s.indexOf(Costanti.SOAP_ENVELOPE_NAMESPACE);
			int indexOfSoap12 = s.indexOf(Costanti.SOAP12_ENVELOPE_NAMESPACE);
			if(indexOfSoap11>0 && indexOfSoap12>0){
				if(indexOfSoap11<indexOfSoap12){
					return MessageType.SOAP_11;
				}
				else{
					return MessageType.SOAP_12;
				}
			}
			else if(indexOfSoap11>0){
				return MessageType.SOAP_11;
			}
			else if(indexOfSoap12>0){
				return MessageType.SOAP_12;
			}
		}
		return null;
	}
	
	public static boolean checkSOAPEnvelopeNamespace(OpenSPCoop2SoapMessage message, MessageType messageType) throws MessageException, MessageNotSupportedException{
		try {
			SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
			if(messageType.equals(MessageType.SOAP_11) &&  Costanti.SOAP_ENVELOPE_NAMESPACE.equals(envelope.getNamespaceURI())) {
				return true;
			}
			
			if(messageType.equals(MessageType.SOAP_12) &&  Costanti.SOAP12_ENVELOPE_NAMESPACE.equals(envelope.getNamespaceURI())) {
				return true;
			}
			
			return false;

		} catch (Exception ex) {
			
			Throwable t = ParseExceptionUtils.getParseException(ex);
			if(t!=null){
				throw new MessageException(ex);
			}
			
			throw new MessageException(ex.getMessage(),ex);
		} 	
	}

	
	
	// NODE UTILS
	
	public static boolean matchLocalName(OpenSPCoop2MessageFactory messageFactory, Node nodo,String nodeName,String prefix,String namespace){
		return XMLUtils.getInstance(messageFactory).matchLocalName(nodo, nodeName, prefix, namespace);
	} 
	
	public static Node getAttributeNode(OpenSPCoop2MessageFactory messageFactory, Node node,String attributeName){
		return XMLUtils.getInstance(messageFactory).getAttributeNode(node, attributeName);
	}
	public static Node getQualifiedAttributeNode(OpenSPCoop2MessageFactory messageFactory, Node node,String attributeName,String namespace){
		return XMLUtils.getInstance(messageFactory).getQualifiedAttributeNode(node, attributeName, namespace);
	}
	
	
	// EQUALS
	
	public static void equalsSoapElements(SOAPElement element1,SOAPElement element2,boolean checkTextComment) throws MessageException{
		try{
			_equalsSoapElements(element1, element2, new ArrayList<String>(),checkTextComment);
		}catch(Exception e){
			try{
				String soapReq = PrettyPrintXMLUtils.prettyPrintWithTrAX(element1);
				String soapRes = PrettyPrintXMLUtils.prettyPrintWithTrAX(element2);
				throw new MessageException("Element1 ["+soapReq+"] risulta differente da Element2 ["+soapRes+"] (motivo:"+e.getMessage()+")",e);
			}catch(Exception eInternal){
				throw new MessageException(eInternal.getMessage(),eInternal);
			}
		}
	}

	private static String _getQualifiedName(QName attr){
		if(attr.getNamespaceURI()!=null){
			return "{"+ attr.getNamespaceURI()+" }"+attr.getLocalPart();
		}
		else{
			return attr.getLocalPart();
		}
	}
	
	private static void _equalsSoapElements(SOAPElement el1,SOAPElement el2,List<String> namespacePrefixEl1,boolean checkTextComment) throws MessageException{
		
		/**************** controllo nome del nodo *****************************/
		if(!el1.getNodeName().equals(el2.getNodeName())){
			//System.out.println("NOME DIVERSO");
			throw new MessageException("Node1 possiede un nome ["+el1.getNodeName()+"] differente dal nome del Node2 ["+el2.getNodeName()+"]");
		}
		
		
		Iterator<?> it=el1.getAllAttributesAsQNames();
		Iterator<?> it2=el2.getAllAttributesAsQNames();
		List <String>vet=new ArrayList<String>();
		List <String>vet2=new ArrayList<String>();
		/**************** controllo se gli attributi sono uguali*****************************/
		while(it.hasNext()){
			if(!it2.hasNext()){
				//System.out.println("ATTR 1");
				throw new MessageException("Node1 ["+el1.getNodeName()+"] possiede degli attributi, mentre nel node2 ["+el2.getNodeName()+"] non ve ne sono");
			}
			//Attributes att=(Attributes)it.next();
			QName attr1 = ((QName) it.next());
			vet.add( _getQualifiedName(attr1) );
			
			QName attr2 = ((QName) it2.next());
			vet2.add( _getQualifiedName(attr2) );
			
		}
		if(it2.hasNext()){
			//System.out.println("ATTR 2");
			throw new MessageException("Node2 ["+el2.getNodeName()+"] possiede piu' attributi del Node1 ["+el1.getNodeName()+"]");
		}
		if(!vet.containsAll(vet2)){
			//System.out.println("ATTR 3");
			
			StringBuilder bfNode1 = new StringBuilder();
			for (int i = 0; i < vet.size(); i++) {
				if(i>0){
					bfNode1.append(",");
				}
				bfNode1.append(vet.get(i));
			}
			
			StringBuilder bfNode2 = new StringBuilder();
			for (int i = 0; i < vet2.size(); i++) {
				if(i>0){
					bfNode2.append(",");
				}
				bfNode2.append(vet2.get(i));
			}
			
			throw new MessageException("Node1 ["+el1.getNodeName()+"] e Node2 ["+el2.getNodeName()+"] non hanno gli stessi attributi. Attributi("+vet.size()+") di Node1: "+bfNode1+ " . Attributi("+vet2.size()+") di Node2: "+bfNode2);
		}


		for(int i=0;i<vet.size();i++){
			String value=vet.get(i);
			if(!el1.getAttribute(value).equals(el2.getAttribute(value))){
				throw new MessageException("L'attributo ["+value+"] di Node1 ["+el1.getNodeName()+"] possiede un valore ("+
							el1.getAttribute(value)+") differente dal valore presente nello stesso attributo nel Node2 ["+el2.getNodeName()+"] (valore:"+el2.getAttribute(value)+")");
			}
		}

		/***************************** Namespace URI del nodo ********************************/
        String str1=el1.getNamespaceURI();
        String str2=el2.getNamespaceURI();
       // System.out.println("el1 -- il namespace Uri del nodo e' "+str1);
        //System.out.println("el2 -- il namespace Uri del nodo e' "+str2);
        boolean namespaceIdentico = false;
        if(str1!=null && str2!=null){
        	namespaceIdentico = str1.equals(str2);
        }
        else if(str1==null && str2==null){
        	namespaceIdentico = true;
        }
        if(!namespaceIdentico){
        	//System.out.println("URI");
        	throw new MessageException("Node1 ["+el1.getNodeName()+"] possiede un namespace ["+str1+"] differente dal namespace del Node2 ["+el2.getNodeName()+"] (namespace:"+str2+")");
        }
		
		
		/*****************************Controllo se i namespace sono uguali********************************/
        Iterator<?> nameSp1=el1.getNamespacePrefixes();
        Iterator<?> nameSp2=el2.getNamespacePrefixes();
        List <String>nameSpVet1=new ArrayList<String>();
        List <String>nameSpVet2=new ArrayList<String>();
        String prefix1, prefix2, urlPrefix1, urlPrefix2;
        while(nameSp1.hasNext() && nameSp2.hasNext())
        {
            prefix1=(String) nameSp1.next();
            try{
            	urlPrefix1 = el1.getNamespaceURI(prefix1);
            }catch(Exception e){
            	urlPrefix1 = el1.getNamespaceURI();
            }
            nameSpVet1.add(prefix1+"="+urlPrefix1);
            
            if(namespacePrefixEl1.contains((prefix1+"="+urlPrefix1))==false){
            	//System.out.println("ADD COMPLESSIVO: "+prefix1+"="+urlPrefix1);
            	namespacePrefixEl1.add(prefix1+"="+urlPrefix1);
            }
            
            prefix2=(String) nameSp2.next();
            try{
            	urlPrefix2 = el2.getNamespaceURI(prefix2);
            }catch(Exception e){
            	urlPrefix2 = el2.getNamespaceURI();
            }
            nameSpVet2.add(prefix2+"="+urlPrefix2);            
        }
        
        // Controllo uguaglianza
        for(int i=0; i<nameSpVet1.size(); i++){
        	String n1 = (String) nameSpVet1.get(i);
        	boolean trovato = false;
        	for(int j=0; j<nameSpVet2.size(); j++){
        		String n2 = (String) nameSpVet2.get(j);
        		if(n1.equals(n2)){
        			trovato = true;
        			break;
        		}			
        	}
        	if(trovato==false){
        		// Cerco nei namespaces del padre
        		if(namespacePrefixEl1.contains(n1)==false){
        			//System.out.println("NON TROVATO: "+n1);
        			throw new MessageException("Node1 ["+el1.getNodeName()+"] non contiene il prefix: "+n1);
        		}
        	}
        }
        

        if(!(nameSpVet1.size() == nameSpVet2.size())){
        	//System.out.println("SIZE NAMESPACE");
        	
			StringBuilder bfNode1 = new StringBuilder();
			for (int i = 0; i < nameSpVet1.size(); i++) {
				if(i>0){
					bfNode1.append(",");
				}
				bfNode1.append(nameSpVet1.get(i));
			}
			
			StringBuilder bfNode2 = new StringBuilder();
			for (int i = 0; i < nameSpVet2.size(); i++) {
				if(i>0){
					bfNode2.append(",");
				}
				bfNode2.append(nameSpVet2.get(i));
			}
        	
        	throw new MessageException("Node1 ["+el1.getNodeName()+"] e Node2 ["+el2.getNodeName()+"] non hanno gli stessi prefix. Attributi("+nameSpVet1.size()+") di Node1: "+bfNode1+ " . Attributi("+nameSpVet2.size()+") di Node2: "+bfNode2);
        }    
        


		/*****************chiamata ricorsiva per i figli********************/
		Iterator<?> child=el1.getChildElements();
		Iterator<?> child2=el2.getChildElements();
		while(child.hasNext()){
			if(checkTextComment){
				if(!child2.hasNext()){
					//System.out.println("CHILD1");
					throw new MessageException("Node2 ["+el2.getNodeName()+"] non ha child element, mentre il Node1 ["+el1.getNodeName()+"] ne possiede"); 
				}
			}
			Object obj=null;
			if(child.hasNext())
				obj = child.next();
			
			Object obj2=null;
			if(child2.hasNext())
				obj2=child2.next();
			
			if(checkTextComment==false){
				
				while( (obj!=null) && (obj instanceof Text) ){
					if(child.hasNext()){
						obj=child.next();
					}
					else{
						obj=null;
					}
				}
				
				while( (obj2!=null) && (obj2 instanceof Text) ){
					if(child2.hasNext()){
						obj2=child2.next();
					}
					else{
						obj2=null;
					}
				}
			
				if(obj==null){
					if(obj2!=null){
						throw new MessageException("Node2 ["+el2.getNodeName()+"] possiede ulteriori child element ("+((SOAPElement)obj2).getNodeName()+") non presenti nel Node1 ["+el1.getNodeName()+"]");
					}
					else{
						break; // elementi terminati
					}
				}
				else{
					if(obj2==null){
						throw new MessageException("Node1 ["+el1.getNodeName()+"] possiede ulteriori child element ("+((SOAPElement)obj).getNodeName()+") non presenti nel Node2 ["+el2.getNodeName()+"]");
					}
				}
			}
			
			
			if (obj instanceof Text) {
				Text text = (Text) obj;
				if (!(obj2 instanceof Text)){
					//System.out.println("CHILD2");
					throw new MessageException("Node2 ["+el2.getNodeName()+"] non possiede l'element Text presente nel Node1 ["+el1.getNodeName()+"] (valore: "+text.toString()+")"); 
				}
				else{
					Text text2 = (Text) obj2;
					boolean value = text.toString().equals(text2.toString());
					//System.out.println("CHILD3 ["+value+"]");
					if(value==false){
						throw new MessageException("Node2 ["+el2.getNodeName()+"] possiede un element Text con valore ("+text2.toString()+") differente da quello presente nel Node1 ["+el1.getNodeName()+"] (valore:"+text.toString()+")");
					}
				}
			}
			else{
				if(obj2 instanceof Text){
					//System.out.println("CHILD4");
					throw new MessageException("Node2 ["+el2.getNodeName()+"] possiede un element Text ("+((Text)obj2).toString()+") non presente nel Node1 ["+el1.getNodeName()+"]"); 
				}
				@SuppressWarnings("unchecked")
				List<String> namespacePrefixEl1Parent = (List<String>) ((ArrayList<String>) namespacePrefixEl1).clone();
				_equalsSoapElements((SOAPElement)obj, (SOAPElement)obj2 , namespacePrefixEl1Parent,checkTextComment);
			}
		}


	}
	
	
	
	



	/* ********  FAULT  ******** */ 


	public static String toString(OpenSPCoop2MessageFactory messageFactory, SOAPFault fault) throws MessageException{
		return SoapUtils.toString(messageFactory, fault,true);
	}
	
	public static String toString(OpenSPCoop2MessageFactory messageFactory, SOAPFault fault,boolean printDetails) throws MessageException{
		try{
			if(printDetails){
				if(fault!=null){
					return OpenSPCoop2MessageFactory.getAsString(messageFactory, fault,true);
				}else{
					return "SOAPFault non presente";
				}	
			}
			else{
				StringBuilder bf = new StringBuilder();
				if(fault!=null){
					bf.append("SOAPFault");
					if(fault.getFaultCode()!=null && !"".equals(fault.getFaultCode())){
						bf.append(" faultCode["+fault.getFaultCode()+"]");
					}
					if(fault.getFaultActor()!=null && !"".equals(fault.getFaultActor())){
						bf.append(" faultActor["+fault.getFaultActor()+"]");
					}
					if(fault.getFaultString()!=null && !"".equals(fault.getFaultString())){
						bf.append(" faultString["+fault.getFaultString()+"]");
					}
					
				}else{
					return "SOAPFault non presente";
				}
				
				return bf.toString();
			}
			
		} catch(Exception e) {
			throw new MessageException("toString SOAPFault: "+e.getMessage(),e);
		}
	}


	
	
	
	
	
	
	/* ********  M E S S A G G I    F A U L T     S O A P    ******** */ 
	
	/**
	 * Metodo che si occupa di costruire un messaggio <code>SOAPFault</code>  con i seguenti campi :
	 * <p>
	 * <ul>
	 *   <li> FaultString : parametro <var>aFault</var>,
	 *   <li> FaultCode   : parametro <var>aCode</var>,
	 *   <li> FaultActor  : parametro <var>aActor</var>,
	 *   <li> <code>Detail</code> composto da tanti Detail quanto sono la lunghezza dei parametri seguenti :
	 *   <ul>
	 *       <li> LocalName : parametro <var>aDetailLocalName</var>,
	 *       <li> Prefix    : parametro <var>aDetailPrefix</var>,
	 *       <li> URI       : parametro <var>aDetailURI</var>,
	 *   </ul>
	 * </ul>
	 *
	 * @param aFault faultString del SOAPFault.
	 * @param aActor faultActor del SOAPFault.
	 * @param aCode  faultCode del SOAPFault.
	 * @param dettaglioEccezione Dettaglio dell'eccezione
	 * @return <tt>byte[]</tt> del messaggio Soap Fault costruito in caso di successo, <tt>null</tt> altrimenti.
	 * 
	 */
	public static byte[] build_Soap_Fault(OpenSPCoop2MessageFactory messageFactory, MessageType messageType, String aFault, String aActor, QName aCode, 
			Element dettaglioEccezione,boolean generaDetails) throws MessageException, MessageNotSupportedException  {

		if(!MessageType.SOAP_11.equals(messageType) && !MessageType.SOAP_12.equals(messageType)){
			throw MessageNotSupportedException.newMessageNotSupportedException(messageType);
		}
		
		ByteArrayOutputStream byteMessaggio = null;
		try{
			OpenSPCoop2Message msg = messageFactory.createEmptyMessage(messageType,MessageRole.FAULT);
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			SOAPEnvelope env = (soapMsg.getSOAPPart()).getEnvelope();

			//log.info("Setting SOAPBody with SOAPFault");
			SOAPBody bdy = env.getBody();
			bdy.addFault();
			SOAPFault fault = bdy.getFault();

			//log.info("Setting Fault");
			
			if(aFault != null)
				fault.setFaultString(aFault);
			else
				fault.setFaultString("");

			//log.info("Setting Fault Code");
			
			if(aCode != null)
				fault.setFaultCode(aCode);

			//log.info("Setting Actor");
			if(aActor != null)
				fault.setFaultActor(aActor);

			//log.info("Setting Details");
			if(generaDetails && dettaglioEccezione!=null){
				fault.addDetail();	
				Detail d = fault.getDetail();
				d.appendChild(d.getOwnerDocument().importNode(dettaglioEccezione,true));
			}

			//log.info("Build complete MessageSOAPFault");

			// Return byte ....
			byteMessaggio = new ByteArrayOutputStream();
			msg.writeTo(byteMessaggio, true);
			//log.info("-----Imbustamento------");
			//log.info(byteMessaggio.toString());
			//log.info("-----Imbustamento------");

			byte [] risultato = byteMessaggio.toByteArray();
			byteMessaggio.close();
			return risultato;
		} catch(Exception e) {
			try{
				if(byteMessaggio!=null)
					byteMessaggio.close();
			}catch(Exception eis){}
			throw new MessageException("Creazione MsgSOAPFault non riuscito: "+e.getMessage(),e);
		}
	}


}

