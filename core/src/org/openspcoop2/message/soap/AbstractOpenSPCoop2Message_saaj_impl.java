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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.wss4j.common.WSS4JConstants;
import org.openspcoop2.message.ForwardConfig;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.soap.reference.AttachmentReference;
import org.openspcoop2.message.soap.reference.ElementReference;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.message.xml.DynamicNamespaceContextFactory;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.dch.DataContentHandlerManager;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * AbstractXMLBaseOpenSPCoop2Message
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractOpenSPCoop2Message_saaj_impl extends AbstractBaseOpenSPCoop2SoapMessage {

	private SOAPMessage soapMessage;	
	protected SOAPMessage getSoapMessage() {
		return this.soapMessage;
	}

	public AbstractOpenSPCoop2Message_saaj_impl(OpenSPCoop2MessageFactory messageFactory, SOAPMessage soapMessage){
		super(messageFactory);
		this.soapMessage = soapMessage;
	}
	
	
	/* Metodi SOAP */
	
	@Override
	protected SOAPMessage _getSOAPMessage() throws MessageException{
		return this.soapMessage;
	}
	
	@Override
	public SOAPPart getSOAPPart() throws MessageException,MessageNotSupportedException{
		return this.soapMessage.getSOAPPart();
	}
	
	@Override
	public SOAPBody getSOAPBody() throws MessageException,MessageNotSupportedException{
		try{
			return this.soapMessage.getSOAPBody();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public SOAPHeader getSOAPHeader() throws MessageException,MessageNotSupportedException{
		try{
			return this.soapMessage.getSOAPHeader();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	

	
	
	/* Attachments SOAP */
	
	@Override
	public void addAttachmentPart(AttachmentPart attachmentPart) throws MessageException,MessageNotSupportedException{
		this.soapMessage.addAttachmentPart(attachmentPart);
	}
	
	@Override
	public AttachmentPart createAttachmentPart(DataHandler dataHandler) throws MessageException,MessageNotSupportedException{
		return this.soapMessage.createAttachmentPart(dataHandler);
	}
	
	@Override
	public AttachmentPart createAttachmentPart() throws MessageException,MessageNotSupportedException{
		return this.soapMessage.createAttachmentPart();
	}
	
	@Override
	public int countAttachments() throws MessageException,MessageNotSupportedException{
		return this.soapMessage.countAttachments();
	}
	
	@Override
	public Iterator<?> getAttachments() throws MessageException,MessageNotSupportedException{
		return this.soapMessage.getAttachments();
	}
	
	@Override
	public Iterator<?> getAttachments(MimeHeaders headers) throws MessageException,MessageNotSupportedException{
		String[] values = headers.getHeader("Content-Id");
		if(values.length > 0 && (!values[0].startsWith("<") || !values[0].endsWith(">"))) {
			headers.removeHeader("Content-Id");
			headers.setHeader("Content-Id", "<" + values[0] + ">");
		}
		return this.soapMessage.getAttachments(headers);
	}
	
	@Override
	public AttachmentPart getAttachment(SOAPElement element) throws MessageException,MessageNotSupportedException{
		try{
			return this.soapMessage.getAttachment(element);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public void removeAllAttachments() throws MessageException,MessageNotSupportedException{
		this.soapMessage.removeAllAttachments();
	}
	
	@Override
	public void removeAttachments(MimeHeaders mhs) throws MessageException,MessageNotSupportedException{
		this.soapMessage.removeAttachments(mhs);
		// Aggiorno il Content-Type se sono finiti gli Attachments.
		if(this.soapMessage.countAttachments() == 0){
			this.setContentType(HttpConstants.CONTENT_TYPE_SOAP_1_1);
		}
	}
	
	@Override
	public void updateAttachmentPart(AttachmentPart ap,DataHandler dh) throws MessageException,MessageNotSupportedException{
		ap.setDataHandler(dh);
	}
	
	@Override
	public void updateAttachmentPart(AttachmentPart ap,byte[]content,String contentType) throws MessageException,MessageNotSupportedException {
		// Se si usa il solo metodo del ramo else, in tomcat si ottiene il seguente errore (con dump abilitato):
		// ... Unable to run the JAXP transformer on a stream [B cannot be cast to javax.xml.transform.Source (sourceException: Error during saving a multipart message)
		try {
			String baseType = ContentTypeUtilities.readBaseTypeFromContentType(contentType);
			if(HttpConstants.CONTENT_TYPE_TEXT_XML.equals(baseType)) {
				Source streamSource = null;
				DataContentHandlerManager dchManager = new DataContentHandlerManager(LoggerWrapperFactory.getLogger(AbstractOpenSPCoop2Message_saaj_impl.class));
				if(dchManager.readMimeTypesContentHandler().containsKey(HttpConstants.CONTENT_TYPE_TEXT_XML)) {
					// Se è non registrato un content handler per text/xml
					// succede se dentro l'ear non c'e' il jar mailapi e l'application server non ha caricato il modulo mailapi (es. tramite versione standalone standard)
					// e si usa il metodo seguente DOMSource si ottiene il seguente errore:
					// javax.xml.soap.SOAPException: no object DCH for MIME type text/xml
					//    at com.sun.xml.messaging.saaj.soap.MessageImpl.writeTo(MessageImpl.java:1396) ~[saaj-impl-1.3.28.jar:?]
					//System.out.println("XML (DOMSource)");
					streamSource = new DOMSource(XMLUtils.getInstance(this.messageFactory).newElement(content));
				}
				else {
					// Se è registrato un content handler per text/xml
					// e succede se dentro l'ear c'e' il jar mailapi oppure se l'application server ha caricato il modulo mailapi (es. tramite versione standalone full)
					// e si usa il metodo seguente StreamSource, si ottiene il seguente errore:
					//  Unable to run the JAXP transformer on a stream org.xml.sax.SAXParseException; Premature end of file. (sourceException: Error during saving a multipart message) 
					//  	com.sun.xml.messaging.saaj.SOAPExceptionImpl: Error during saving a multipart message
					//        at com.sun.xml.messaging.saaj.soap.MessageImpl.writeTo(MessageImpl.java:1396) ~[saaj-impl-1.3.28.jar:?]
					//        at org.openspcoop2.message.Message1_1_FIX_Impl.writeTo(Message1_1_FIX_Impl.java:172) ~[openspcoop2_message_BUILD-13516.jar:?]
					//        at org.openspcoop2.message.OpenSPCoop2Message_11_impl.writeTo
					//System.out.println("XML (StreamSource)");
					streamSource = new javax.xml.transform.stream.StreamSource(new java.io.ByteArrayInputStream(content));
				}
				ap.setContent(streamSource, contentType);
			}
			else {
				this.updateAttachmentPart(ap, new DataHandler(content,contentType)); 
			}
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public void updateAttachmentPart(AttachmentPart ap,String content,String contentType) throws MessageException,MessageNotSupportedException {
		this.updateAttachmentPart(ap, new DataHandler(content,contentType));
	}

	
	
	/* ContentID Attachments SOAP */
	
	@Override
	public String createContentID(String ns) throws MessageException,MessageNotSupportedException{
		try{
			return "<" + org.apache.cxf.attachment.AttachmentUtil.createContentID(ns) + ">";
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	/* Trasporto */
	
	@Override
	public OpenSPCoop2MessageProperties getForwardTransportHeader(ForwardConfig forwardConfig) throws MessageException{
		OpenSPCoop2MessageProperties msg = super.getForwardTransportHeader(forwardConfig);
		return new OpenSPCoop2MessageMimeHeaderProperties(this.soapMessage,msg);
	}
	


	/* ContentType */
	
	protected abstract String _super_getContentType();
	
	@Override
	public void updateContentType() throws MessageException {
		try{
			if(countAttachments() > 0){
				if(saveRequired()) {
					saveChanges();
				}
			}
			else {
			
				if(saveRequired()) {
					
					boolean pulizia = false;
					String contentType = _super_getContentType();
					if((ContentTypeUtilities.isMtom(contentType)) ){
						// Bug Fix: OP-375  'Unable to internalize message' con messaggi senza attachments con ContentType 'multipart/related; ...type="application/xop+xml"'
						//			Capita per i messaggi che contengono un content type multipart e però non sono effettivamente presenti attachments.
						saveChanges();
						pulizia = true;
					}
					else if((ContentTypeUtilities.isMultipart(contentType)) ){
						// Bug Fix: OP-678 'Unable to internalize message' con messaggi senza attachments con ContentType 'multipart/related; ...type="text/xml"'
						//			Capita per i messaggi che contengono un content type multipart e però non sono effettivamente presenti attachments.
						saveChanges();
						pulizia = true;
					}
					
					if(pulizia) {
						try {
							javax.mail.internet.ContentType ctObj = new javax.mail.internet.ContentType(contentType);
							
							// Bug Fix: OP-909
							// Rimane il tipo 'Multipart-Type' come Content-Type in caso di messaggio 'MTOM' senza allegati.
							String contentTypeInternal = ContentTypeUtilities.getInternalMultipartContentType(contentType);
							javax.mail.internet.ContentType ctObjInternal = new javax.mail.internet.ContentType(contentTypeInternal);
							ctObj.setPrimaryType(ctObjInternal.getPrimaryType());
							ctObj.setSubType(ctObjInternal.getSubType());
							
							String type = ctObj.getParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE);
							if(type!=null && !type.equals("")) {
								ctObj.getParameterList().remove(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE);
							}
							
							String boundary = ctObj.getParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_BOUNDARY);
							if(boundary!=null && !boundary.equals("")) {
								ctObj.getParameterList().remove(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_BOUNDARY);
							}
							
							String start = ctObj.getParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_START);
							if(start!=null && !start.equals("")) {
								ctObj.getParameterList().remove(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_START);
							}
							
							String startInfo = ctObj.getParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_START_INFO);
							if(startInfo!=null && !startInfo.equals("")) {
								ctObj.getParameterList().remove(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_START_INFO);
							}
							
							this.setContentType(ctObj.toString());
							
						}catch(Throwable t) {
							System.err.println("Pulizia messaggio Multipart normalizzato senza attachment non riuscita: "+t.getMessage());
							t.printStackTrace(System.err);
						}
					}
				}
				
			}
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}


	
	/* WriteTo e Save */
		
	@Override
	public void writeTo(OutputStream os, boolean consume) throws MessageException{
		try{
			CountingOutputStream cos = new CountingOutputStream(os);
			this.soapMessage.writeTo(cos);
			this.outgoingsize = cos.getByteCount();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public void saveChanges() throws MessageException{
		try{
			this.soapMessage.saveChanges();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean saveRequired(){
		return this.soapMessage.saveRequired();
	}
	
	
	
	/* SOAP Utilities */
	
	@Override
	public Element getFirstChildElement(SOAPElement element) throws MessageException,MessageNotSupportedException {
		Element firstElement = null;
		Iterator<?> it = element.getChildElements();
		while (it.hasNext() && firstElement==null){
			Node tmp = (Node) it.next();
			if(tmp instanceof Element) firstElement = (Element) tmp;
		}
		return firstElement;
	}
	
	@Override
	public SOAPElement createSOAPElement(byte[] bytes) throws MessageException,MessageNotSupportedException {
		try{
			SOAPFactory soapFactory = null;
			if(MessageType.SOAP_11.equals(this.getMessageType())){
				soapFactory = this.messageFactory.getSoapFactory11();
			}
			else if(MessageType.SOAP_12.equals(this.getMessageType())){
				soapFactory = this.messageFactory.getSoapFactory12();
			}
			else{
				throw new MessageException("MessageType ["+this.getMessageType()+"] not supported");
			}
			return soapFactory.createElement(XMLUtils.getInstance(this.messageFactory).newElement(bytes));
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public SOAPHeaderElement newSOAPHeaderElement(SOAPHeader hdr,QName name) throws MessageException,MessageNotSupportedException {
		try{
			SOAPHeaderElement newHeader = 	hdr.addHeaderElement(name);
			return newHeader;
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public void addHeaderElement(SOAPHeader hdr,SOAPHeaderElement hdrElement) throws MessageException,MessageNotSupportedException{
		try{
			hdr.addChildElement(hdrElement);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public void removeHeaderElement(SOAPHeader hdr,SOAPHeaderElement hdrElement) throws MessageException,MessageNotSupportedException{
		try{
			hdr.removeChild(hdrElement);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public void setFaultCode(SOAPFault fault, SOAPFaultCode code,
			QName eccezioneName) throws MessageException,MessageNotSupportedException {
		try{
			fault.setFaultCode(eccezioneName);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public void setFaultString(SOAPFault fault, String message) throws MessageException,MessageNotSupportedException{
		try{
			SoapUtils.setFaultString(fault, message, null);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	@Override
	public void setFaultString(SOAPFault fault, String message, Locale locale) throws MessageException,MessageNotSupportedException{
		try{
			SoapUtils.setFaultString(fault, message, locale);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	
	
	/* Ws Security */
	
	private SOAPPart getSOAPPartForSearchWSSecurity(){
		return this.soapMessage.getSOAPPart();
	}
	
	@Override
	public List<Reference> getWSSDirtyElements(String actor, boolean mustUnderstand) throws MessageException,MessageNotSupportedException {
		
		try{
		
			List<Reference> references = new ArrayList<Reference>();
			
			// Prendo il security Header di mia competenza
	        SOAPElement security = (SOAPElement) WSSecurityUtils.getSecurityHeader(this.getSOAPPartForSearchWSSecurity(),this.messageType, actor,
	        		this.isThrowExceptionIfFoundMoreSecurityHeader());
	       
	        //TODO verificare se actor==null && mustUnderstand==false?
	        if(security!=null){
			    // Prendo i riferimenti agli elementi cifrati
			    Iterator<?> it = security.getChildElements(new QName(WSS4JConstants.ENC_NS, WSS4JConstants.ENC_KEY_LN));
			    if(it.hasNext()){ 
			    	SOAPElement encryptedKey = (SOAPElement) it.next();
			    	SOAPElement referenceList = (SOAPElement) encryptedKey.getChildElements(new QName(WSS4JConstants.ENC_NS, WSS4JConstants.REF_LIST_LN)).next();
			    	List<SOAPElement> referenceListElements = SoapUtils.getNotEmptyChildSOAPElement(referenceList);
		        	for (int i = 0; i < referenceListElements.size(); i++) {
		        		String referenceWithSharp = referenceListElements.get(i).getAttributeValue(new QName("URI"));
			    		// Il riferimento presenta un # prima dell'identificativo se e' un elemento o cid: se e' un attachment
			    		if(referenceWithSharp.startsWith("#")){
				    		String reference = referenceWithSharp.substring(1);
				    		// Vado a vedere se ' cifrato {Content} o {Element}
				    		SOAPElement encryptedElement = (SOAPElement) org.apache.wss4j.common.util.XMLUtils.findElementById(this.getSOAPPartForSearchWSSecurity().getEnvelope(), reference, true);
				    		if(encryptedElement==null){
				    			throw new SOAPException("Element with 'Id' attribute value ("+referenceWithSharp+") not found "+Costanti.FIND_ERROR_ENCRYPTED_REFERENCES);
				    		}
				    		
			        		// Verifico se sto cifrando un attachment
			        		QName qName = new QName(WSS4JConstants.ENC_NS, "CipherData");
			        		Iterator<?> childElements = encryptedElement.getChildElements(qName);
			        		if(childElements!=null && childElements.hasNext()){
			        			
			        			QName qNameReference = new QName(WSS4JConstants.ENC_NS, "CipherReference");
				        		Iterator<?> childElementsReference = ((SOAPElement)childElements.next()).getChildElements(qNameReference);
				        		if(childElementsReference!=null && childElementsReference.hasNext()){
				        			
				        			// Attachment cifrato
				        			String referenceAttach = ((SOAPElement) childElementsReference.next()).getAttributeValue(new QName("URI"));
				        			if(referenceAttach.startsWith("cid:")){
				 	        			String referenceCID = referenceAttach.substring(4);
				 		        		references.add(new AttachmentReference (AttachmentReference.TYPE_ENCRYPT_ATTACHMENT, referenceCID));
				 	        		}else{
					        			throw new SOAPException("Element 'CipherReference' with attribute 'cid' wrong "+Costanti.FIND_ERROR_ENCRYPTED_REFERENCES);
				        			}
				        			
				        		}else{
				        			
				        			// Elemento cifrato
					        		if(encryptedElement.getAttributeNS(null, "Type").equals(WSS4JConstants.ENC_NS + "Content"))
					        			references.add(new ElementReference(encryptedElement.getParentElement(), ElementReference.TYPE_ENCRYPT_CONTENT, reference));
					        		else
					        			references.add(new ElementReference (encryptedElement.getParentElement(), ElementReference.TYPE_ENCRYPT_ELEMENT, reference));	
				        		}
			        		}
			        		else{
			        			throw new SOAPException("Element 'CipherData' not found "+Costanti.FIND_ERROR_ENCRYPTED_REFERENCES);
			        		}
				    		
			    		} 
			    	}
			    }
			    
			    // Prendo i riferimenti agli elementi firmati
			    it = security.getChildElements(new QName(WSS4JConstants.SIG_NS, WSS4JConstants.SIG_LN));
			    if(it.hasNext()){ 
			    	SOAPElement signature = (SOAPElement) it.next();
			    	SOAPElement signatureInfo = (SOAPElement) signature.getChildElements(new QName(WSS4JConstants.SIG_NS, "SignedInfo")).next();
			    	Iterator<?> referenceIterator = signatureInfo.getChildElements(new QName(WSS4JConstants.SIG_NS, "Reference"));
			    	while (referenceIterator.hasNext()) {
			    		String referenceWithSharp = ((SOAPElement) referenceIterator.next()).getAttributeValue(new QName("URI"));
			    		// Il riferimento presenta un # prima dell'identificativo se e' un elemento o cid: se e' un attachment
			    		if(referenceWithSharp.startsWith("#")){
				    		String reference = referenceWithSharp.substring(1);
				    		
				    		SOAPEnvelope soapEnvelope = this.getSOAPPartForSearchWSSecurity().getEnvelope();
				    		
				    		SOAPElement signedElement = (SOAPElement) org.apache.wss4j.common.util.XMLUtils.findElementById(soapEnvelope, reference, true);
				    		if(signedElement==null){
				    			
				    			// Provo a vedere se l'elemento firmato e' una Assertion
				    			
				    			DynamicNamespaceContext dnc = null;
				    			if(MessageType.SOAP_11.equals(this.getMessageType())) {
				    				dnc = DynamicNamespaceContextFactory.getInstance(this.messageFactory).getNamespaceContextFromSoapEnvelope11(soapEnvelope);
				    			} else {
				    				dnc = DynamicNamespaceContextFactory.getInstance(this.messageFactory).getNamespaceContextFromSoapEnvelope12(soapEnvelope);
				    			}
				    	    	
				    			AbstractXPathExpressionEngine xpathExpressionEngine = new XPathExpressionEngine(this.messageFactory);
				    			
				    			try {
				    				String xpath = Costanti.XPATH_SAML_20_ASSERTION + "[@"+Costanti.SAML_20_ASSERTION_ID+"='"+reference+"']";
				    				Object o = xpathExpressionEngine.getMatchPattern(security, dnc, xpath, XPathReturnType.NODE);
				    				signedElement = (SOAPElement) o;
				    			} catch(XPathNotFoundException e) {}
				    			
				    			if(signedElement==null){
					    			try {
					    				String xpath = Costanti.XPATH_SAML_11_ASSERTION + "[@"+Costanti.SAML_11_ASSERTION_ID+"='"+reference+"']";
					    				Object o = xpathExpressionEngine.getMatchPattern(security, dnc, xpath, XPathReturnType.NODE);
					    				signedElement = (SOAPElement) o;
					    			} catch(XPathNotFoundException e) {}
				    			}
				    			
				    			if(signedElement==null){
				    				throw new SOAPException("Element with 'Id' attribute value ("+referenceWithSharp+") not found "+Costanti.FIND_ERROR_SIGNATURE_REFERENCES);
				    			}
				    		}
				   			references.add(new ElementReference (signedElement, ElementReference.TYPE_SIGNATURE, reference));
			    		} else if(referenceWithSharp.startsWith("cid:")){
		        			String reference = referenceWithSharp.substring(4);
			        		references.add(new AttachmentReference(AttachmentReference.TYPE_SIGN_ATTACHMENT, reference));
		        		}
			    	}
			    }
	        }
	
	        return references;
	        
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	

	@Override
	public void cleanWSSDirtyElements(String actor, boolean mustUnderstand, List<Reference> references, boolean detachHeaderWSSecurity, boolean removeAllIdRef) throws MessageException,MessageNotSupportedException {
		
		try{
		
			// Prendo il security Header di mia competenza
	        SOAPElement security = (SOAPElement) WSSecurityUtils.getSecurityHeader(this.getSOAPPartForSearchWSSecurity(),this.messageType, actor,
	        		this.isThrowExceptionIfFoundMoreSecurityHeader());
	        
	        // Rimuovo l'header Security
	        if(detachHeaderWSSecurity){
	        	security.detachNode();
	        }
	        
	        boolean found;
	        
	        // Pulisco i nodi sporchi
			for(int i=0; i<references.size(); i++){
				Reference reference = references.get(i);
				if(reference instanceof ElementReference) {
					SOAPElement elementToClean = ((ElementReference)reference).getElement();
					switch (reference.getType()) {
					case ElementReference.TYPE_SIGNATURE:
						// Devo vedere se altri hanno firmato l'elemento ed in tal caso lasciar fare l'id ed il namespace
						found = false;
						NodeList securities = this.getSOAPHeader().getElementsByTagNameNS(WSS4JConstants.WSSE_NS, WSS4JConstants.WSSE_LN);
						for(int s=0; s<securities.getLength(); s++){
							security = (SOAPElement) securities.item(s);
							// Prendo i riferimenti agli elementi firmati
					        Iterator<?>  it = security.getChildElements(new QName(WSS4JConstants.SIG_NS, WSS4JConstants.SIG_LN));
					        if(it.hasNext()){ 
					        	SOAPElement signature = (SOAPElement) it.next();
					        	SOAPElement signatureInfo = (SOAPElement) signature.getChildElements(new QName(WSS4JConstants.SIG_NS, "SignedInfo")).next();
					        	Iterator<?> referenceIterator = signatureInfo.getChildElements(new QName(WSS4JConstants.SIG_NS, "Reference"));
					        	while (referenceIterator.hasNext()) {
					        		String referenceWithSharp = ((SOAPElement) referenceIterator.next()).getAttributeValue(new QName("URI"));
					        		if(reference.getReference().equals(referenceWithSharp.substring(1))) {
					        			found = true;
					        		}
					        	}
					        }
						}
						if(!found) {
							
							boolean removeIdRefSignature = false;
							if(removeAllIdRef){
								// alcune implementazioni vecchie generano degli attributi wsu:Id 'zombie' che quindi non possono essere ripuliti controllandoli con le reference.
								// Per poter interoperare con tali implementazioni è possibile abilitare tale opzione
								removeIdRefSignature = true;
								elementToClean.removeAttributeNS(WSS4JConstants.WSU_NS, "Id");
							}
							else{
								String valoreRefSignature = elementToClean.getAttributeNS(WSS4JConstants.WSU_NS, "Id");
								// fix: l'id puo' appartenere ad un altro header wssecurity con diverso actor/mustUnderstand. Controllo il valore.
								//System.out.println("CHECK TYPE_SIGNATURE ["+valoreRefSignature+"] ["+reference.getReference()+"]");
								if(valoreRefSignature!=null && valoreRefSignature.equals(reference.getReference())){
									removeIdRefSignature = true;
								}
								
								if(removeIdRefSignature){
									elementToClean.removeAttributeNS(WSS4JConstants.WSU_NS, "Id");
								}
							}
								
							List<String> prefixesToRemoveContent = new ArrayList<String>();
							Iterator<?> prefixes = elementToClean.getNamespacePrefixes();
							while(prefixes.hasNext()){
								String prefix = (String) prefixes.next();
								String namespace = elementToClean.getNamespaceURI(prefix);
								if(namespace.equals(WSS4JConstants.WSU_NS)) {
									if(removeIdRefSignature){
										prefixesToRemoveContent.add(prefix);
									}
								}
							}
							for(int y=0; y<prefixesToRemoveContent.size(); y++)
								elementToClean.removeNamespaceDeclaration(prefixesToRemoveContent.get(y));
						}
						break;
		
					case ElementReference.TYPE_ENCRYPT_CONTENT:
						List<String> prefixesToRemoveContent = new ArrayList<String>();
						Iterator<?> prefixesContent = elementToClean.getNamespacePrefixes();
						while(prefixesContent.hasNext()){
							String prefix = (String) prefixesContent.next();
							String namespace = elementToClean.getNamespaceURI(prefix);
							if(namespace.equals(WSS4JConstants.ENC_NS) ||  namespace.equals(elementToClean.getNamespaceURI(prefix))){
								prefixesToRemoveContent.add(prefix);
							}
						}
						for(int y=0; y<prefixesToRemoveContent.size(); y++)
							elementToClean.removeNamespaceDeclaration(prefixesToRemoveContent.get(y));
						
						boolean removeIdRefEncContent = false;
						if(removeAllIdRef){
							// alcune implementazioni vecchie generano degli attributi wsu:Id 'zombie' che quindi non possono essere ripuliti controllandoli con le reference.
							// Per poter interoperare con tali implementazioni è possibile abilitare tale opzione
							removeIdRefEncContent = true;
							elementToClean.removeAttributeNS(WSS4JConstants.WSU_NS, "Id");
						}
						else{
							String valoreRefEncContent = elementToClean.getAttributeNS(WSS4JConstants.WSU_NS, "Id");
							// fix: l'id puo' appartenere ad un altro header wssecurity con diverso actor/mustUnderstand. Controllo il valore.
							//System.out.println("CHECK TYPE_ENCRYPT_CONTENT ["+valoreRefEncContent+"] ["+reference.getReference()+"]");
							if(valoreRefEncContent!=null && valoreRefEncContent.equals(reference.getReference())){
								removeIdRefEncContent = true;
							}
							
							if(removeIdRefEncContent){
								elementToClean.removeAttributeNS(WSS4JConstants.WSU_NS, "Id");
							}
						}
							
						prefixesToRemoveContent = new ArrayList<String>();
						prefixesContent = elementToClean.getNamespacePrefixes();
						while(prefixesContent.hasNext()){
							String prefix = (String) prefixesContent.next();
							String namespace = elementToClean.getNamespaceURI(prefix);
							if(namespace.equals(WSS4JConstants.WSU_NS)) {
								if(removeIdRefEncContent){
									prefixesToRemoveContent.add(prefix);
								}
							}
						}
						for(int y=0; y<prefixesToRemoveContent.size(); y++)
							elementToClean.removeNamespaceDeclaration(prefixesToRemoveContent.get(y));
						break;
						
					case ElementReference.TYPE_ENCRYPT_ELEMENT:
						// Questo codice si occupa di pulire la "sporcizia" wss presente comunque nel elemento cifrato e decifrato.
						// Poiche' non c'e' modo, esaminando il messaggio cifrato, di identificare (nome e namespace da decifrare) l'elemento cifrato per intero, si e' scelto di tenersi traccia del padre,
						// e a questo punto di ripulire tutti i figli che contengono sporcizia.
						// Eventuali figli cifrati in modalita' 'content' rientreranno comunque anche in questa casistica.
						Iterator<?> childrenToClean =  elementToClean.getChildElements();
						while(childrenToClean.hasNext()) {
							Object next = childrenToClean.next();
							if(next instanceof SOAPElement) {
								SOAPElement childToClean = (SOAPElement) next;
								List<String> prefixesToRemoveElement = new ArrayList<String>();
								Iterator<?> prefixesElement = childToClean.getNamespacePrefixes();
								while(prefixesElement.hasNext()){
									String prefix = (String) prefixesElement.next();
									String namespace = childToClean.getNamespaceURI(prefix);
									if(namespace.equals(WSS4JConstants.WSU_NS)){
										prefixesToRemoveElement.add(prefix);
									}
								}
								for(int y=0; y<prefixesToRemoveElement.size(); y++)
									childToClean.removeNamespaceDeclaration(prefixesToRemoveElement.get(y));
								
								boolean removeIdRefEncElement = false;
								if(removeAllIdRef){
									// alcune implementazioni vecchie generano degli attributi wsu:Id 'zombie' che quindi non possono essere ripuliti controllandoli con le reference.
									// Per poter interoperare con tali implementazioni è possibile abilitare tale opzione
									removeIdRefEncContent = true;
									childToClean.removeAttributeNS(WSS4JConstants.WSU_NS, "Id");
								}
								else{
									String valoreRefEncElement = childToClean.getAttributeNS(WSS4JConstants.WSU_NS, "Id");
									// fix: l'id puo' appartenere ad un altro header wssecurity con diverso actor/mustUnderstand. Controllo il valore.
									//System.out.println("CHECK TYPE_ENCRYPT_ELEMENT ["+valoreRefEncElement+"] ["+reference.getReference()+"]");
									if(valoreRefEncElement!=null && valoreRefEncElement.equals(reference.getReference())){
										removeIdRefEncElement = true;
									}
									
									if(removeIdRefEncElement){
										childToClean.removeAttributeNS(WSS4JConstants.WSU_NS, "Id");
									}
								}
									
								prefixesToRemoveElement = new ArrayList<String>();
								prefixesElement = childToClean.getNamespacePrefixes();
								while(prefixesElement.hasNext()){
									String prefix = (String) prefixesElement.next();
									String namespace = childToClean.getNamespaceURI(prefix);
									if(namespace.equals(WSS4JConstants.WSU_NS)) {
										if(removeIdRefEncElement){
											prefixesToRemoveElement.add(prefix);
										}
									}
								}
								for(int y=0; y<prefixesToRemoveElement.size(); y++)
									childToClean.removeNamespaceDeclaration(prefixesToRemoveElement.get(y));
							}
						}
						break;
						
					default:
						break;
					}
				}
			}
			
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	
	/* Ws Security (SoapBox) */
	
	@Override
	public String getEncryptedDataHeaderBlockClass() {
		return com.sun.xml.wss.core.EncryptedDataHeaderBlock.class.getName();
	}

	@Override
	public String getProcessPartialEncryptedMessageClass() {
		return "org.openspcoop2.security.message.soapbox.ProcessPartialEncryptedMessage";
	}

	@Override
	public String getSignPartialMessageProcessorClass() {
		return "org.openspcoop2.security.message.soapbox.SignPartialMessageProcessor";
	}
	
	
}
