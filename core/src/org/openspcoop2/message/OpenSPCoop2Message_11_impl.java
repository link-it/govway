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

package org.openspcoop2.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.wss4j.common.WSS4JConstants;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.util.WSSecurityUtil;
import org.openspcoop2.message.mtom.MTOMUtilities;
import org.openspcoop2.message.mtom.MtomXomPackageInfo;
import org.openspcoop2.message.mtom.MtomXomReference;
import org.openspcoop2.message.reference.AttachmentReference;
import org.openspcoop2.message.reference.ElementReference;
import org.openspcoop2.message.reference.Reference;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.io.notifier.NotifierInputStream;
import org.openspcoop2.utils.resources.TransportRequestContext;
import org.openspcoop2.utils.resources.TransportResponseContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;

/**
 * Implementazione dell'OpenSPCoop2Message
 *
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class OpenSPCoop2Message_11_impl extends Message1_1_FIX_Impl implements org.openspcoop2.message.OpenSPCoop2Message
{
	private ParseException parseException;
	
	private long outgoingsize = -1;
	private long incomingsize = -1;
	private Long incomingSizeForced = null;
	private String protocolName;
	
	private NotifierInputStream notifierInputStream;
	
	private Map<String, String> contentTypeParamaters = new Hashtable<String, String>();
	
	private Map<String, Object> context = new Hashtable<String, Object>();
	
	private TransportRequestContext transportRequestContext;
	private TransportResponseContext transportResponseContext;
	
	private String forcedResponseCode;
	
	public OpenSPCoop2Message_11_impl() {	
		super();
	}
	
	public OpenSPCoop2Message_11_impl(MimeHeaders mhs, InputStream is, boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold, long overhead) throws SOAPException, IOException{
		super(mhs, new CountingInputStream(is));
		setLazyAttachments(false);
		this.incomingsize = super.getCountingInputStream().getByteCount() - overhead;
	}
	
	public OpenSPCoop2Message_11_impl(SOAPMessage msg) {	
		//TODO questo costruttore non funziona con messaggi con attachment. 
		//C'e' un bug nell'implementazione della sun che non copia gli attachment
		//In particolare il parametro super.mimePart (protetto non accessibile).
		
		//Uso una classe creata ad hoc
		super(msg);
	}
	// GetAs 
	
	@Override
	public String getAsString(Node ele, boolean consume){
		try {
			return XMLUtils.getInstance().toString(ele,true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	@Override
	public byte[] getAsByte(Node ele, boolean consume){
		try {
			return XMLUtils.getInstance().toByteArray(ele,true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	
	// SOAP Content Constants 
	
	@Override
	public String createContentID(String ns) throws UnsupportedEncodingException{
		return "<" + org.apache.cxf.attachment.AttachmentUtil.createContentID(ns) + ">";
	}
	
	@Override
	public String getContentID(AttachmentPart ap) {
		String cid = ap.getContentId();
		if(cid != null && cid.startsWith("<") && cid.endsWith(">"))
			return cid.substring(1, cid.length() -1);
		return cid;
	}
	
	@Override
	public void addContentTypeParameter(String name,String value) throws SOAPException{
		this.contentTypeParamaters.put(name, value);
	}
	
	@Override
	public void removeContentTypeParameter(String name) throws SOAPException{
		this.contentTypeParamaters.remove(name);
	}
		
	@Override
	public void setContentType(String type){
		super.setContentType(type);
	}
	
	@Override
	public String getContentType(){
		try {
			ContentType cType = new ContentType(super.getContentType());
			if(cType.getBaseType().equals(Costanti.CONTENT_TYPE_MULTIPART)) {
				if(super.mmp != null)
					cType.setParameter(Costanti.CONTENT_TYPE_MULTIPART_BOUNDARY, super.mmp.getContentType().getParameter(Costanti.CONTENT_TYPE_MULTIPART_BOUNDARY));
			}
			return ContentTypeUtilities.buildContentType(this.contentTypeParamaters, cType.toString());	
		} catch (com.sun.xml.messaging.saaj.packaging.mime.internet.ParseException e) {
			e.printStackTrace();
			return ContentTypeUtilities.buildContentType(this.contentTypeParamaters,super.getContentType());
		}
	}
	
	
	// Write To
	
	@Override
	public void writeTo(OutputStream os, boolean consume) throws SOAPException, IOException{
		CountingOutputStream cos = new CountingOutputStream(os);
		this.writeTo(cos);
		this.outgoingsize = cos.getByteCount();
	}
	
	
	// Content Length
	
	@Override
	public void updateIncomingMessageContentLength(){
		// NOP (Viene letta tutta nel costruttore)
	}
	
	@Override
	public void updateIncomingMessageContentLength(long incomingsize){
		this.incomingSizeForced = incomingsize;
	}
	
	@Override
	public long getIncomingMessageContentLength() {
		if(this.incomingSizeForced!=null){
			return this.incomingSizeForced;
		}else{
			return this.incomingsize;
		}
	}

	@Override
	public void updateOutgoingMessageContentLength(long outgoingsize){
		this.outgoingsize = outgoingsize;
	}
	
	@Override
	public long getOutgoingMessageContentLength() {
		return this.outgoingsize;
	}

	

	
	
	// SOAP WithAttachments
	
	@Override
	public void removeAttachments(MimeHeaders mhs){
		super.removeAttachments(mhs);
		// SAggiorno il Content-Type se son finiti gli Attachments.
		if(super.countAttachments() == 0){
			this.setContentType(SOAPConstants.SOAP_1_1_CONTENT_TYPE);
		}
	}
	
	@Override
	public void updateAttachmentPart(AttachmentPart ap,byte[]content,String contentType){
		this.updateAttachmentPart(ap, new DataHandler(content,contentType));
	}
	
	@Override
	public void updateAttachmentPart(AttachmentPart ap,String content,String contentType){
		this.updateAttachmentPart(ap, new DataHandler(content,contentType));
	}
	
	@Override
	public void updateAttachmentPart(AttachmentPart ap,DataHandler dh){
		ap.setDataHandler(dh);
	}
	
	
	// SOAP Envelope
	
	@Override
	public SOAPElement createSOAPElement(byte[] bytes) throws IOException, ParserConfigurationException, SAXException, SOAPException{
//		org.w3c.dom.Document doc =  org.apache.cxf.helpers.XMLUtils.parse(bytes);
//		OpenSPCoop2MessageFactory fac = new OpenSPCoop2MessageFactory_impl();
//		return ((OpenSPCoop2Message_11_impl) fac.createMessage(SOAPVersion.SOAP11)).getSOAPBody().addDocument(doc);
		try{
			return OpenSPCoop2MessageFactory.getMessageFactory().getSoapFactory11().createElement(XMLUtils.getInstance().newElement(bytes));
		}catch(Exception e){
			throw new SOAPException(e.getMessage(),e);
		}
	}

	@Override
	public SOAPHeaderElement cleanXSITypes(SOAPHeaderElement header) throws SOAPException {
		// CXF non serve
		return header;
	}
	
	@Override
	public SOAPElement cleanXSITypes(SOAPElement header) throws SOAPException{
		// CXF non server
		return header;
	}
	
	@Override
	public SOAPHeaderElement newSOAPHeaderElement(SOAPHeader hdr,QName name) throws SOAPException {
		SOAPHeaderElement newHeader = 	hdr.addHeaderElement(name);
		return newHeader;
	}
	
	@Override
	public void addHeaderElement(SOAPHeader hdr,SOAPHeaderElement hdrElement) throws SOAPException{
		hdr.addChildElement(hdrElement);
	}
	
	@Override
	public void removeHeaderElement(SOAPHeader hdr,SOAPHeaderElement hdrElement) throws SOAPException{
		hdr.removeChild(hdrElement);
	}
	
	@Override
	public Element getFirstChildElement(SOAPElement element) {
		Element firstElement = null;
		Iterator<?> it = element.getChildElements();
		while (it.hasNext() && firstElement==null){
			Node tmp = (Node) it.next();
			if(tmp instanceof Element) firstElement = (Element) tmp;
		}
		return firstElement;
	}
	
	@Override
	public Iterator<?> getAttachments(MimeHeaders headers){
		String[] values = headers.getHeader("Content-Id");
		if(values.length > 0 && (!values[0].startsWith("<") || !values[0].endsWith(">"))) {
			headers.removeHeader("Content-Id");
			headers.setHeader("Content-Id", "<" + values[0] + ">");
		}
		return super.getAttachments(headers);
	}
	
	@Override
	public List<Reference> getWSSDirtyElements(String actor, boolean mustUnderstand) throws SOAPException, WSSecurityException {
		List<Reference> references = new ArrayList<Reference>();
		
		// Prendo il security Header di mia competenza
        SOAPElement security = (SOAPElement) WSSecurityUtil.getSecurityHeader(this.soapPartImpl, actor);
       
        //TODO verificare se actor==null && mustUnderstand==false?
        if(security!=null){
	        // Prendo i riferimenti agli elementi cifrati
	        Iterator<?> it = security.getChildElements(new QName(WSS4JConstants.ENC_NS, WSS4JConstants.ENC_KEY_LN));
	        if(it.hasNext()){ 
	        	SOAPElement encryptedKey = (SOAPElement) it.next();
	        	SOAPElement referenceList = (SOAPElement) encryptedKey.getChildElements(new QName(WSS4JConstants.ENC_NS, WSS4JConstants.REF_LIST_LN)).next();
	        	Vector<SOAPElement> referenceListElements = SoapUtils.getNotEmptyChildSOAPElement(referenceList);
	        	for (int i = 0; i < referenceListElements.size(); i++) {
	        		String referenceWithSharp = referenceListElements.get(i).getAttributeValue(new QName("URI"));
	        		// Il riferimento presenta un # prima dell'identificativo se e' un elemento o cid: se e' un attachment
	        		if(referenceWithSharp.startsWith("#")){
		        		String reference = referenceWithSharp.substring(1);
		        		// Vado a vedere se ' cifrato {Content} o {Element}
		        		SOAPElement encryptedElement = (SOAPElement) org.apache.wss4j.common.util.XMLUtils.findElementById(this.soapPartImpl.getEnvelope(), reference, true);
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
			 	        			String referenceCID = referenceWithSharp.substring(4);
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
		        		
		        		SOAPElement signedElement = (SOAPElement) org.apache.wss4j.common.util.XMLUtils.findElementById(this.soapPartImpl.getEnvelope(), reference, true);
		        		if(signedElement==null){
		        			throw new SOAPException("Element with 'Id' attribute value ("+referenceWithSharp+") not found "+Costanti.FIND_ERROR_SIGNATURE_REFERENCES);
		        		}
		       			references.add(new ElementReference (signedElement, ElementReference.TYPE_SIGNATURE, reference));
	        		} else if(referenceWithSharp.startsWith("cid:")){
	        			String reference = referenceWithSharp.substring(4);
		        		references.add(new AttachmentReference (AttachmentReference.TYPE_SIGN_ATTACHMENT, reference));
	        		}
	        	}
	        }
        }
        
        return references;
	}
	

	@Override
	public void cleanWSSDirtyElements(String actor, boolean mustUnderstand, List<Reference> references) throws SOAPException, WSSecurityException {
		// Prendo il security Header di mia competenza
        SOAPElement security = (SOAPElement) WSSecurityUtil.getSecurityHeader(this.soapPartImpl, actor);
        
        // Rimuovo l'header Security
        security.detachNode();
        
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
						
						String valoreRefSignature = elementToClean.getAttributeNS(WSS4JConstants.WSU_NS, "Id");
						// fix: l'id puo' appartenere ad un altro header wssecurity con diverso actor/mustUnderstand. Controllo il valore.
						//System.out.println("CHECK TYPE_SIGNATURE ["+valoreRefSignature+"] ["+reference.getReference()+"]");
						boolean removeIdRefSignature = false;
						if(valoreRefSignature!=null && valoreRefSignature.equals(reference.getReference())){
							removeIdRefSignature = true;
						}
						
						if(removeIdRefSignature){
							elementToClean.removeAttributeNS(WSS4JConstants.WSU_NS, "Id");
						}
									
						Iterator<?> prefixes = elementToClean.getNamespacePrefixes();
						while(prefixes.hasNext()){
							String prefix = (String) prefixes.next();
							String namespace = elementToClean.getNamespaceURI(prefix);
							if(namespace.equals(WSS4JConstants.WSU_NS)) {
								if(removeIdRefSignature){
									elementToClean.removeNamespaceDeclaration(prefix);
								}
							}
						}
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
					
					String valoreRefEncContent = elementToClean.getAttributeNS(WSS4JConstants.WSU_NS, "Id");
					// fix: l'id puo' appartenere ad un altro header wssecurity con diverso actor/mustUnderstand. Controllo il valore.
					//System.out.println("CHECK TYPE_ENCRYPT_CONTENT ["+valoreRefEncContent+"] ["+reference.getReference()+"]");
					boolean removeIdRefEncContent = false;
					if(valoreRefEncContent!=null && valoreRefEncContent.equals(reference.getReference())){
						removeIdRefEncContent = true;
					}
					
					if(removeIdRefEncContent){
						elementToClean.removeAttributeNS(WSS4JConstants.WSU_NS, "Id");
					}
										
					prefixesContent = elementToClean.getNamespacePrefixes();
					while(prefixesContent.hasNext()){
						String prefix = (String) prefixesContent.next();
						String namespace = elementToClean.getNamespaceURI(prefix);
						if(namespace.equals(WSS4JConstants.WSU_NS)) {
							if(removeIdRefEncContent){
								elementToClean.removeNamespaceDeclaration(prefix);
							}
						}
					}
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
							
							String valoreRefEncElement = childToClean.getAttributeNS(WSS4JConstants.WSU_NS, "Id");
							// fix: l'id puo' appartenere ad un altro header wssecurity con diverso actor/mustUnderstand. Controllo il valore.
							//System.out.println("CHECK TYPE_ENCRYPT_ELEMENT ["+valoreRefEncElement+"] ["+reference.getReference()+"]");
							boolean removeIdRefEncElement = false;
							if(valoreRefEncElement!=null && valoreRefEncElement.equals(reference.getReference())){
								removeIdRefEncElement = true;
							}
							
							if(removeIdRefEncElement){
								childToClean.removeAttributeNS(WSS4JConstants.WSU_NS, "Id");
							}
												
							prefixesElement = childToClean.getNamespacePrefixes();
							while(prefixesElement.hasNext()){
								String prefix = (String) prefixesElement.next();
								String namespace = childToClean.getNamespaceURI(prefix);
								if(namespace.equals(WSS4JConstants.WSU_NS)) {
									if(removeIdRefEncElement){
										childToClean.removeNamespaceDeclaration(prefix);
									}
								}
							}
						}
					}
					break;
					
				default:
					break;
				}
			}
		}
	}

	@Override
	public void updateContentType() throws SOAPException {
		if(countAttachments() > 0 && saveRequired()){
	   		saveChanges();
		}else if((SOAPVersion.isMtom(LoggerWrapperFactory.getLogger(OpenSPCoop2Message.class), this.getContentType())) && saveRequired() ){
			// Bug Fix: OP-375  'Unable to internalize message' con messaggi senza attachments con ContentType 'multipart/related; ...type="application/xop+xml"'
			//			Capita per i messaggi che contengono un content type multipart e però non sono effettivamente presenti attachments.
			saveChanges();
		}
	}

	@Override
	public ParseException getParseException(){
		return this.parseException;
	}

	@Override
	public void setParseException(ParseException e){
		this.parseException = e;
	}
	
	@Override
	public void setParseException(Throwable e){
		this.parseException = MessageUtils.buildParseException(e);
	}

	@Override
	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}

	@Override
	public String getProtocolName() {
		return this.protocolName;
	}

	@Override
	public SOAPVersion getVersioneSoap() {
		return SOAPVersion.SOAP11;
	}

	@Override
	public void setFaultCode(SOAPFault fault, SOAPFaultCode code,
			QName eccezioneName) throws SOAPException {
		fault.setFaultCode(eccezioneName);
	}
	
	@Override
	public void setNotifierInputStream(NotifierInputStream is) {
		this.notifierInputStream = is;
	}

	@Override
	public NotifierInputStream getNotifierInputStream() {
		return this.notifierInputStream;
	}
	
	
	/* Trasporto */
	
	@Override
	public void setTransportRequestContext(TransportRequestContext transportRequestContext){
		this.transportRequestContext = transportRequestContext;
	}
	@Override
	public TransportRequestContext getTransportRequestContext(){
		return this.transportRequestContext;
	}
	@Override
	public void setTransportResponseContext(TransportResponseContext transportResponseContext){
		this.transportResponseContext = transportResponseContext;
	}
	@Override
	public TransportResponseContext getTransportResponseContext(){
		return this.transportResponseContext;
	}
	@Override
	public void setForcedResponseCode(String code){
		this.forcedResponseCode = code;
	}
	@Override
	public String getForcedResponseCode(){
		return this.forcedResponseCode;
	}
	
	
	/* MTOM */
	
	@Override
	public List<MtomXomReference> mtomUnpackaging() throws OpenSPCoop2MessageException{
		return MTOMUtilities.unpackaging(this, false, true);
	}
	@Override
	public List<MtomXomReference> mtomPackaging( List<MtomXomPackageInfo> packageInfos) throws OpenSPCoop2MessageException{
		return MTOMUtilities.packaging(this, packageInfos, true);
	}
	@Override
	public List<MtomXomReference> mtomVerify( List<MtomXomPackageInfo> packageInfos) throws OpenSPCoop2MessageException{
		return MTOMUtilities.verify(this, packageInfos, true);
	}
	@Override
	public List<MtomXomReference> mtomFastUnpackagingForXSDConformance() throws OpenSPCoop2MessageException{
		return MTOMUtilities.unpackaging(this, true, true);
	}
	@Override
	public void mtomRestoreAfterXSDConformance(List<MtomXomReference> references) throws OpenSPCoop2MessageException{
		MTOMUtilities.restoreAfterFastUnpackaging(this, references, true);
	}
	
	
	/* CONTEXT */
	@Override
	public void addContextProperty(String property, Object value){
		this.context.put(property, value);
	}
	@Override
	public Iterator<String> keysContextProperty(){
		return this.context.keySet().iterator();
	}
	@Override
	public Object getContextProperty(String property){
		return this.context.get(property);
	}
	@Override
	public Object removeContextProperty(String property){
		return this.context.remove(property);
	}
	
}
