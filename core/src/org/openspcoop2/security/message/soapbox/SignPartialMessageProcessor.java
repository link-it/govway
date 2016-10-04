/*
 * AdroitLogic UltraESB Enterprise Service Bus
 *
 * Copyright (c) 2010 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 *
 * GNU Affero General Public License Usage
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program (See LICENSE-AGPL.TXT).
 * If not, see http://www.gnu.org/licenses/agpl-3.0.html
 *
 * Commercial Usage
 *
 * Licensees holding valid UltraESB Commercial licenses may use this file in accordance with the UltraESB Commercial
 * License Agreement provided with the Software or, alternatively, in accordance with the terms contained in a written
 * agreement between you and AdroitLogic.
 *
 * If you are unsure which license is appropriate for your use, or have questions regarding the use of this file,
 * please contact AdroitLogic at info@adroitlogic.com
 */
/*
 * Modificato per supportare le seguenti funzionalita':
 * - firma e cifratura degli attachments
 * - cifratura con chiave simmetrica
 * - supporto CRL 
 * 
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, 
 * either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope 
 * that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.security.message.soapbox;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;

import org.adroitlogic.soapbox.CryptoUtil;
import org.adroitlogic.soapbox.InvalidMessageDataException;
import org.adroitlogic.soapbox.MessageSecurityContext;
import org.adroitlogic.soapbox.Processor;
import org.adroitlogic.soapbox.SBConstants;
import org.adroitlogic.soapbox.SecurityConfig;
import org.adroitlogic.soapbox.SecurityFailureException;
import org.adroitlogic.soapbox.SignatureRequest;
import org.apache.wss4j.common.WSS4JConstants;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.security.message.constants.WSSAttachmentsConstants;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xml.wss.core.SignatureHeaderBlock;


/**
 * SignPartialMessageProcessor
 *
 * @author Andrea Poli <apoli@link.it>
 * @author Giovanni Bussu <bussu@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SignPartialMessageProcessor implements Processor {

	public final static QName TIMESTAMP = new QName(SBConstants.WSU, "Timestamp");
	
	private boolean useXMLSec = true;
	public boolean isUseXMLSec() {
		return this.useXMLSec;
	}
	public void setUseXMLSec(boolean useXMLSec) {
		this.useXMLSec = useXMLSec;
	}
	
	private AbstractXMLUtils xmlUtils = null;
	
	protected List<QName> signQNames;
    protected List<Boolean> elementsSignatureContent;
    protected List<AttachmentPart> signAttachments;
    protected OpenSPCoop2Message message;
	public void setMessage(OpenSPCoop2Message message) {
		this.message = message;
	}
	protected String actor;
	protected boolean mustUnderstand;
	public void setActor(String actor) {
		this.actor = actor;
	}
	public void setMustUnderstand(boolean mustUnderstand) {
		this.mustUnderstand = mustUnderstand;
	}
    
	public SignPartialMessageProcessor() {
		this.xmlUtils = XMLUtils.getInstance();
		this.signQNames = new ArrayList<QName>();
		this.signAttachments = new ArrayList<AttachmentPart>();
		this.elementsSignatureContent = new ArrayList<Boolean>();
	}
	
	public void addElementToSign(QName element , boolean content) {
		this.signQNames.add(element);
		this.elementsSignatureContent.add(content);
	}
	
	public void addAttachmentsToSign(AttachmentPart part , boolean content) {
		this.signAttachments.add(part);
	}
	
    @Override
	public void process(SecurityConfig secConfig, MessageSecurityContext msgSecCtx) {

        SignatureRequest signReq = msgSecCtx.getSignatureRequest();

       // System.out.println("SIGN XMLSEC["+this.useXMLSec+"]");
        
        
        // *** ensure existence of the wsse:Security header, and create one if none exists ***
        Element wsseSecurityElem = null;
        try{
        	wsseSecurityElem = WSSUtils.getWSSecurityHeader(msgSecCtx.getDocument(), this.actor, this.mustUnderstand);
        }catch(Exception e){
			throw new SecurityFailureException(e.getMessage(), e);
		}
        
        
        
        
        // *** we will not sign an already signed document ***
        if (CryptoUtil.getFirstChildOrNull(wsseSecurityElem, SBConstants.DS, "Signature") != null) {
            throw new InvalidMessageDataException("Message is already signed");
        }

        
        
    
        // **** grab certificate used to sign, and find out the algorithm to be used ****
        X509Certificate[] certs = secConfig.getCertificatesByAlias(signReq.getCertAlias());
        String sigAlgoURI = signReq.getSignatureAlgoURI();
        if (sigAlgoURI == null) {
            if ("DSA".equalsIgnoreCase(certs[0].getPublicKey().getAlgorithm())) {
            	if(this.useXMLSec){
            		sigAlgoURI = org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_DSA;
            	}else{
            		sigAlgoURI = com.sun.org.apache.xml.internal.security.signature.XMLSignature.ALGO_ID_SIGNATURE_DSA;
            	}
            } else if ("RSA".equalsIgnoreCase(certs[0].getPublicKey().getAlgorithm())) {
            	if(this.useXMLSec){
            		sigAlgoURI = org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA;
            	}else{
            		sigAlgoURI = com.sun.org.apache.xml.internal.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA;
            	}
            } else {
                throw new SecurityFailureException("Signature algorithm not specified, and cannot be auto detected");
            }
        }

        
        
        
        // **** create XMLSignature engine ****
        // NOTA:
        // Vi sono fondamentalmente due versioni di XMLSignature con classi correlate.
        // - com.sun.org.apache.xml.internal.security.signature: presente nel runtime di java
        // - org.apache.xml.security.signature: presente in xmlsec-2.0.7.jar
        //
        // A seconda della versione utilizzata devono essere implementate delle classi a corredo:
        // - com.sun.org.apache.xml.internal.security.transforms.TransformSpi implementato tramite org.openspcoop2.security.message.signature.SunAttachmentContentTransform
        // - org.apache.xml.security.transforms.TransformSpi implementato tramite org.openspcoop2.security.message.signature.XMLSecAttachmentContentTransform
        // NOTA: L'implementazione del Transformer tramite le classi della Sun cosi come realizzate usano metodi diversi presenti su Java 1.6 patch 26 o maggiore rispetto a Java 7.
        //		 Java 1.7 ha modificato i metodi della classe astratta com.sun.org.apache.xml.internal.security.transforms.TransformSpi
        //	     Il codice seguente e' stato scritto per poter effettuare i test incrociati sulle due versioni adeguando le classi utilizzate rispetto ad una variabile cablata nel codice
        //		 definita in org.openspcoop2.security.message.soapbox.MessageSecurityContext_soapbox.USE_XMLSEC_IMPL
        // 
        // A seconda della versione utilizzata devono inoltre essere implementate le classe di risoluzione delle signature reference
        // - com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi implementata tramite org.openspcoop2.security.message.signature.SunEnvelopeIdResolver
        // - org.apache.xml.security.utils.resolver.ResourceResolverSpi implementata tramite org.openspcoop2.security.message.signature.XMLSecEnvelopeIdResolver
        
        Document doc = msgSecCtx.getDocument();
        Element env = doc.getDocumentElement();
        com.sun.org.apache.xml.internal.security.signature.XMLSignature sigSUN = null;
        org.apache.xml.security.signature.XMLSignature sigXMLSec = null;
        if (org.apache.xml.security.c14n.Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS.equals(signReq.getC14nAlgoURI())) {
            Element canonicalizationMethodElem = doc.createElementNS(SBConstants.DS, "ds:CanonicalizationMethod");
            canonicalizationMethodElem.setAttribute("Algorithm", signReq.getC14nAlgoURI());
              
            try {
	            if(this.useXMLSec){
	            	org.apache.xml.security.algorithms.SignatureAlgorithm signatureAlgorithm = new org.apache.xml.security.algorithms.SignatureAlgorithm(doc, sigAlgoURI);
	            	sigXMLSec = new org.apache.xml.security.signature.XMLSignature(doc, null, signatureAlgorithm.getElement(), canonicalizationMethodElem);
	            	sigXMLSec.setId(SignPartialMessageProcessor.getSignId());
	            }
	            else{
	            	com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm signatureAlgorithm = new com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm(doc, sigAlgoURI);
	            	//OLDMETHOD: sig = new XMLSignature(doc, null, signatureAlgorithm.getElement(), canonicalizationMethodElem);
	            	sigSUN = new com.sun.org.apache.xml.internal.security.signature.XMLSignature(doc, null, signatureAlgorithm.getElement(), canonicalizationMethodElem);
	            	sigSUN.setId(SignPartialMessageProcessor.getSignId());
	            }
            } catch (Exception e) {
                throw new SecurityFailureException("Error signing document with credentials of alias : " +
                    signReq.getCertAlias() + " using algorithm : " + sigAlgoURI +
                    " and c14n with : " + signReq.getC14nAlgoURI(), e);
            }

        } else {
            try {
            	if(this.useXMLSec){
            		sigXMLSec = new org.apache.xml.security.signature.XMLSignature(doc, null, sigAlgoURI, signReq.getC14nAlgoURI());
            		sigXMLSec.setId(SignPartialMessageProcessor.getSignId());
            	}else{
            		//	OLDMETHOD: sig = new XMLSignature(doc, null, sigAlgoURI, signReq.getC14nAlgoURI());
            		sigSUN = new com.sun.org.apache.xml.internal.security.signature.XMLSignature(doc, null, sigAlgoURI, signReq.getC14nAlgoURI());
            		sigSUN.setId(SignPartialMessageProcessor.getSignId());
            	}
            } catch (Exception e) {
                throw new SecurityFailureException("Error signing document with credentials of alias : " +
                    signReq.getCertAlias() + " using algorithm : " + sigAlgoURI +
                    " and c14n with : " + signReq.getC14nAlgoURI(), e);
            }
        }

        
        
        
        
        
        // ** Definizione degli elementi da firmare **
        // by default the elements to sign are the body and timestamp
        if(this.signQNames.isEmpty() && this.signAttachments.isEmpty()) {
        	this.signQNames.add(new QName(env.getNamespaceURI(), "Body"));
        	this.elementsSignatureContent.add(false);
        	this.signQNames.add(SignPartialMessageProcessor.TIMESTAMP);
        	this.elementsSignatureContent.add(false);
        }
        
        List<Element> signElements = new ArrayList<Element>();
        List<Boolean> signTypeElements = new ArrayList<Boolean>();
        for (int i = 0; i < this.signQNames.size(); i++) {
        	QName name = this.signQNames.get(i);
        	signElements.add(CryptoUtil.getFirstChild(env, name.getNamespaceURI(), name.getLocalPart()));
        	signTypeElements.add(this.elementsSignatureContent.get(i));
		}
        
        
        // ** Firma degli elementi indicati presenti nella SOAP Envelope **
        signElements(signElements,signTypeElements, signReq, sigSUN, sigXMLSec);
        
        
        // ** Firma degli attachments indicati **
        if(this.useXMLSec){
        	sigXMLSec.addResourceResolver(org.openspcoop2.security.message.signature.XMLSecEnvelopeIdResolver.getInstance(this.message));
        }else{
        	sigSUN.addResourceResolver(org.openspcoop2.security.message.signature.SunEnvelopeIdResolver.getInstance(this.message));
        }
        signAttachments(this.signAttachments, signReq, sigSUN, sigXMLSec);
        
        
        
        // ** Creazione del SignatureHeaderBlock e firma **
        // NOTA:
        // La creazione del SignatureHeaderBlock puo' essere gestita tramite una utility fornita dalla SunJava: com.sun.xml.wss.core.SignatureHeaderBlock
        // tale classe richiede pero' l'utilizzo proprio della XMLSignature Engine della Sun.
        // Altrimenti e' possibile agire direttamente tramite XMLSignature Engine in entrambe le soluzioni.
        // Di seguito il boolean useSignatureHeaderBlock cablato nel codice serve proprio a switchare tra le due soluzioni
        
        boolean useSignatureHeaderBlock = false; // debug
        Element signatureElem = null;
	       
        if(useSignatureHeaderBlock){
        
	        SignatureHeaderBlock signatureHeaderBlock = null;
	        try {
	        	if(this.useXMLSec){
	        		//signatureHeaderBlock = new SignatureHeaderBlock(sigXMLSec.getDocument(),sigAlgoURI);
	        		throw new Exception("Not implemented for XMLSec IMPL");
	        	}else{
	        		signatureHeaderBlock = new SignatureHeaderBlock(sigSUN);
	        	}
	//        	signAttachments(this.signAttachments, signReq, signatureHeaderBlock); // La firma degli attachments deve essere fatta prima cosi come ho fatto prima la firma degli elementi
	        	
	        	if (signReq.isWsiBPCompliant()) {
	        		
	        		Element canonicalizationMethodElem = (Element) signatureHeaderBlock.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "CanonicalizationMethod").item(0);
	        		HashSet<String> prefixes = new HashSet<String>();
	        		prefixes.addAll(getInclusivePrefixes(signatureHeaderBlock, false));
	        		com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces inclusiveNamespaces = 
	         			   new  com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces(doc, prefixes);
	         	   	canonicalizationMethodElem.appendChild(inclusiveNamespaces.getElement());
	         	   	
	           }
	        	
	        	signatureHeaderBlock.sign(secConfig.getPrivateKeyByAlias(signReq.getCertAlias()));
	        	signatureElem = signatureHeaderBlock.getAsSoapElement();
	        } catch (Exception e) {
	            throw new SecurityFailureException("Error signing document using alias : " + signReq.getCertAlias(), e);
	        }
	        
        }
        else{
        	try {
        		
	        	if(this.useXMLSec){
	        		
	        		signatureElem = convertToSoapElement(sigXMLSec);
	        		
	        		if (signReq.isWsiBPCompliant()) {
	        			HashSet<String> prefixes = new HashSet<String>();
	        			prefixes.addAll(getInclusivePrefixes(signatureElem, false));
		        		Element canonicalizationMethodElem = (Element) signatureElem.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "CanonicalizationMethod").item(0);
		        		org.apache.xml.security.transforms.params.InclusiveNamespaces inclusiveNamespaces = 
		         			   new  org.apache.xml.security.transforms.params.InclusiveNamespaces(doc, prefixes);
		         	   	canonicalizationMethodElem.appendChild(inclusiveNamespaces.getElement());
	        		}
	        		
	        		sigXMLSec.sign(secConfig.getPrivateKeyByAlias(signReq.getCertAlias()));
	        		signatureElem = convertToSoapElement(sigXMLSec);
	        		
	        	}else{
	        		
	        		signatureElem = convertToSoapElement(sigSUN);
	        		
	        		if (signReq.isWsiBPCompliant()) {
		        		
	        			HashSet<String> prefixes = new HashSet<String>();
	        			prefixes.addAll(getInclusivePrefixes(signatureElem, false));
	        			Element canonicalizationMethodElem = (Element) signatureElem.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "CanonicalizationMethod").item(0);
	        			com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces inclusiveNamespaces = 
	        					new  com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces(doc, prefixes);
			        	canonicalizationMethodElem.appendChild(inclusiveNamespaces.getElement());
	        		}
	        		
	        		sigSUN.sign(secConfig.getPrivateKeyByAlias(signReq.getCertAlias()));
	        		signatureElem = convertToSoapElement(sigSUN);
	        		
	        	}
	        	
        	 } catch (Exception e) {
 	            throw new SecurityFailureException("Error signing document using alias : " + signReq.getCertAlias(), e);
 	        }
        }
        
        
        
        // *** create and attach the keyinfo element ***
        addKeyInfo(signatureElem, doc, signReq, msgSecCtx, secConfig, wsseSecurityElem);

    }
    
    
    
    protected void addKeyInfo(Element signatureElem,Document doc,SignatureRequest signReq,MessageSecurityContext msgSecCtx,
    		SecurityConfig secConfig,Element wsseSecurityElem){
    	// //OLDMETHODCHENONGESTIVA ISSUER: signatureElem.appendChild(CryptoUtil.createKeyInfoElement(doc, signReq, msgSecCtx, secConfig));
        signatureElem.appendChild(WSSUtils.createKeyInfoElement(doc, signReq, msgSecCtx, secConfig));
        Element firstChild = CryptoUtil.getFirstElementChild(wsseSecurityElem);
        if (firstChild != null) {
            wsseSecurityElem.insertBefore(signatureElem, firstChild);
        } else {
            wsseSecurityElem.appendChild(signatureElem);
        }

        NodeList nl = wsseSecurityElem.getElementsByTagNameNS(SBConstants.WSSE, "BinarySecurityToken");
        for (int i=0; i<nl.getLength(); i++) {
            wsseSecurityElem.insertBefore(nl.item(i), signatureElem);
        }
    }
    

    
    private SOAPElement convertToSoapElement(org.apache.xml.security.utils.ElementProxy proxy) {
        org.w3c.dom.Element elem = proxy.getElement();
        if(elem instanceof SOAPElement)
            return (SOAPElement)elem;
        return (SOAPElement)proxy.getDocument().importNode(elem, true);
    }
    
    private SOAPElement convertToSoapElement(com.sun.org.apache.xml.internal.security.utils.ElementProxy proxy) {
    	org.w3c.dom.Element elem = proxy.getElement();
        if(elem instanceof SOAPElement)
            return (SOAPElement)elem;
        return (SOAPElement)proxy.getDocument().importNode(elem, true);
    }

    
    
    /**
     * Get the List of inclusive prefixes from the DOM Element argument 
     */
    public List<String> getInclusivePrefixes(Element target, boolean excludeVisible) {
        List<String> result = new ArrayList<String>();
        Node parent = target;
        while (parent.getParentNode() != null &&
            !(Node.DOCUMENT_NODE == parent.getParentNode().getNodeType())) {
            parent = parent.getParentNode();
            NamedNodeMap attributes = parent.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                if (WSS4JConstants.XMLNS_NS.equals(attribute.getNamespaceURI())) {
                    if ("xmlns".equals(attribute.getNodeName())) {
                        //System.out.println("FOUND #default per "+parent.getLocalName());
                    	result.add("#default");
                    } else {
                    	//System.out.println("FOUND "+attribute.getLocalName()+" per "+parent.getLocalName());
                        result.add(attribute.getLocalName());
                    }
                }
            }
        }

        //System.out.println("SIZE PRIMA EXCLUDE: "+result.size());
        
        if (excludeVisible == true) {
            NamedNodeMap attributes = target.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                if (WSS4JConstants.XMLNS_NS.equals(attribute.getNamespaceURI())) {
                    if ("xmlns".equals(attribute.getNodeName())) {
                    	//System.out.println("REMOVE #default per "+target.getLocalName());
                    	result.remove("#default");
                    } else {
                    	//System.out.println("REMOVE "+attribute.getLocalName()+" per "+target.getLocalName());
                    	result.remove(attribute.getLocalName());
                    }
                }
                if (attribute.getPrefix() != null) {
                	//System.out.println("REMOVE PREFIX "+attribute.getPrefix()+" per "+target.getLocalName());
                	result.remove(attribute.getPrefix());
                }
            }

            if (target.getPrefix() == null) {
            	//System.out.println("REMOVE TARGET #default per "+target.getLocalName());
            	result.remove("#default");
            } else {
            	//System.out.println("REMOVE TARGET PREFIX "+target.getPrefix()+" per "+target.getLocalName());
            	result.remove(target.getPrefix());
            }
        }

        //System.out.println("SIZE FINALE: "+result.size());
        
        return result;
    }
    
//    protected void signAttachments(List<AttachmentPart> part, SignatureRequest signReq, SignatureHeaderBlock signatureHeaderBlock) throws Exception {
    protected void signAttachments(List<AttachmentPart> part, SignatureRequest signReq,
    		com.sun.org.apache.xml.internal.security.signature.XMLSignature sigSUN, org.apache.xml.security.signature.XMLSignature sigXMLSec) {
		
    	try {
    		// Specifica in Web Services Security SOAP Messages With Attachments (Swa) Profile 1.1
    		// 5.4.4 Processing Rules for Attachment Signing
    		// The processing rule for signing is modified based on the SOAP Message Security rules
    		// After determining which attachments are to be included as references in a signature, create a
    		// <ds:Signature> element in a <wsse:Security> header block targeted at the recipient, including a
    		// <ds:Reference> for each attachment to be protected by the signature. Additional <ds:Reference>
    		// elements may refer to content in the SOAP envelope to be included in the signature.
    		
    		// 1. MIME Part Canonicalize the content of the attachment, as appropriate to the MIME type of the part, as
        	// outlined in section 4.4.2 Attachments of an XML content type require Exclusive XML Canonicalization
        	// without comments[Excl-Canon].
    		
    		// 2. If MIME headers are to be included in the signature, perform MIME header canonicalization as
    		// outlined in section 4.4.1.
    		
        	//3. Determine the CID scheme URL to be used to reference the part and set the <ds:Reference> URL
    		// attribute value to this URL.
    		
    		// 4. Include a <ds:Transforms> element in the <ds:Reference>. This <ds:Transforms> element MUST
        	// include a <ds:Transform> element with the Algorithm attribute having the full URL value specified
        	// earlier in this profile – corresponding to either the Attachment-Complete-Signature-Transform or
        	// Attachment-Content-Signature-Transform, depending on what is to be included in the hash calculation.
        	// This MUST be the first transform listed. The <ds:Transform> element MUST NOT contain any
        	// transform for a MIME transfer encoding purpose (e.g. base64 encoding) since transfer encoding is left
        	// to the MIME layer as noted in section 2. This does not preclude the use of XML Transforms, including a
        	// base64 transform, for other purposes.
    		
        	// 5. Extract the appropriate portion of the MIME part consistent with the selected transform.
        	
        	// 6. Create the <ds:Reference> hash value as outlined in the W3C XML Digital Signature	Recommendation.
    		
    		// For each attachment Reference, perform the following steps:
    		if(this.signAttachments==null || this.signAttachments.size()<=0){
    			return;
    		}
	        for(AttachmentPart p : this.signAttachments){ 
	        	
	        	String uri = p.getContentId();
        		if (uri != null) {                
					if(uri.startsWith("<")){
						uri = "cid:" + uri.substring(1, uri.length()-1);
					}else{
						uri = "cid:" + uri;
					}
				} else {
					uri = p.getContentLocation();
				}
        		
        		if(sigSUN!=null){
        			
            		com.sun.org.apache.xml.internal.security.transforms.Transforms transforms = 
            				new com.sun.org.apache.xml.internal.security.transforms.Transforms(this.message.getSOAPHeader().getOwnerDocument());
            		transforms.addTransform(WSSAttachmentsConstants.ATTACHMENT_CONTENT_SIGNATURE_TRANSFORM_URI);
            		
            		String contentType = p.getContentType();
            		if("text/xml".equals(contentType)){
            			//transforms.addTransform(com.sun.org.apache.xml.internal.security.transforms.Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            			if (signReq.isWsiBPCompliant()) {
            				byte[]raw = p.getRawContentBytes();
            				Element signElement = this.xmlUtils.newElement(raw);
            				transforms.item(0).getElement().appendChild(new com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces(
            						this.message.getSOAPHeader().getOwnerDocument(), CryptoUtil.getInclusivePrefixes(signElement, true)).getElement());
            			}
            		}
            		/*else{
            			transforms.addTransform(com.sun.org.apache.xml.internal.security.transforms.Transforms.TRANSFORM_BASE64_DECODE);
            		}*/
        			
        			sigSUN.addDocument(uri, transforms, signReq.getDigestAlgoURI());
        			
        		}
        		
        		if(sigXMLSec!=null){
        			
        			org.apache.xml.security.transforms.Transforms transforms = 
            				new org.apache.xml.security.transforms.Transforms(this.message.getSOAPHeader().getOwnerDocument());
            		transforms.addTransform(WSSAttachmentsConstants.ATTACHMENT_CONTENT_SIGNATURE_TRANSFORM_URI);
            		
            		String contentType = p.getContentType();
            		if("text/xml".equals(contentType)){
            			//transforms.addTransform(com.sun.org.apache.xml.internal.security.transforms.Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            			if (signReq.isWsiBPCompliant()) {
            				byte[]raw = p.getRawContentBytes();
            				Element signElement = this.xmlUtils.newElement(raw);
            				transforms.item(0).getElement().appendChild(new org.apache.xml.security.transforms.params.InclusiveNamespaces(
            						this.message.getSOAPHeader().getOwnerDocument(), CryptoUtil.getInclusivePrefixes(signElement, true)).getElement());
            			}
            		}
            		/*else{
            			transforms.addTransform(com.sun.org.apache.xml.internal.security.transforms.Transforms.TRANSFORM_BASE64_DECODE);
            		}*/
        			
        			sigXMLSec.addDocument(uri, transforms, signReq.getDigestAlgoURI());
        		}
	        	//signatureHeaderBlock.addSignedInfoReference(uri, transforms, signReq.getDigestAlgoURI());
	        	
//	        	byte[]raw = p.getRawContentBytes();
//	        	String contentType = p.getContentType();
//	        	com.sun.org.apache.xml.internal.security.transforms.Transforms transforms = 
//        				new com.sun.org.apache.xml.internal.security.transforms.Transforms(this.message.getSOAPHeader().getOwnerDocument());
//	        	
//	        	// 1. MIME Part Canonicalize the content of the attachment, as appropriate to the MIME type of the part, as
//	        	// outlined in section 4.4.2 Attachments of an XML content type require Exclusive XML Canonicalization
//	        	// without comments[Excl-Canon].
//	        	if("text/xml".equals(contentType)){
//	        		Element signElement = this.xmlUtils.newElement(raw);
//	        		transforms.addTransform(com.sun.org.apache.xml.internal.security.transforms.Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
//	        		if (signReq.isWsiBPCompliant()) {
//	        			transforms.item(0).getElement().appendChild(new InclusiveNamespaces(
//	        					signElement.getOwnerDocument(), CryptoUtil.getInclusivePrefixes(signElement, true)).getElement());
//	        		}
//	        	}
//	        	else{
//	        		Canonicalizer canonicalizer = CanonicalizerFactory.getCanonicalizer(contentType);
//	        		byte[] canonicalize = canonicalizer.canonicalize(raw);
//	        		transforms = 
//	        				new com.sun.org.apache.xml.internal.security.transforms.Transforms(this.message.getSOAPHeader().getOwnerDocument());
//	        		transforms.addTransform(com.sun.org.apache.xml.internal.security.transforms.Transforms.TRANSFORM_BASE64_DECODE);
//	        		transforms.addBase64Text(Base64.encode(canonicalize));
//	        	}
//	        	
//        		// 2. If MIME headers are to be included in the signature, perform MIME header canonicalization as
//        		// outlined in section 4.4.1.
//        		// TODO
//	        	
//	        	//3. Determine the CID scheme URL to be used to reference the part and set the <ds:Reference> URL
//        		//attribute value to this URL.
//	        	String uri = p.getContentId();
//        		if (uri != null) {                
//					uri = "cid:" + uri.substring(1, uri.length()-1);
//				} else {
//					uri = p.getContentLocation();
//				}
//	        
//	        	// 4. Include a <ds:Transforms> element in the <ds:Reference>. This <ds:Transforms> element MUST
//	        	// include a <ds:Transform> element with the Algorithm attribute having the full URL value specified
//	        	// earlier in this profile – corresponding to either the Attachment-Complete-Signature-Transform or
//	        	// Attachment-Content-Signature-Transform, depending on what is to be included in the hash calculation.
//	        	// This MUST be the first transform listed. The <ds:Transform> element MUST NOT contain any
//	        	// transform for a MIME transfer encoding purpose (e.g. base64 encoding) since transfer encoding is left
//	        	// to the MIME layer as noted in section 2. This does not preclude the use of XML Transforms, including a
//	        	// base64 transform, for other purposes.
//        		
//	        	// 5. Extract the appropriate portion of the MIME part consistent with the selected transform.
//	        	
//	        	// 6. Create the <ds:Reference> hash value as outlined in the W3C XML Digital Signature	Recommendation.
//	        	
//        		signatureHeaderBlock.addSignedInfoReference(uri, transforms, signReq.getDigestAlgoURI());
	        	
	        }
        } catch(Exception e) {
        	throw new SecurityFailureException("Error signing attachments", e);
        }
    	

    }
    
    private void signElements(List<Element> signList,List<Boolean> signTypeList, SignatureRequest signReq, 
    		com.sun.org.apache.xml.internal.security.signature.XMLSignature sigSUN, org.apache.xml.security.signature.XMLSignature sigXMLSec) {    
    //OLDMETHOD: private void signElements(List<Element> signList,List<Boolean> signTypeList, SignatureRequest signReq, XMLSignature sig) {
      
		for (int i = 0; i < signList.size(); i++) {
			
			Element signElement = signList.get(i);
			@SuppressWarnings("unused")
			Boolean content = signTypeList.get(i);

            String signId = null;
            try {
                signId = signElement.getAttributeNS(SBConstants.WSU, "Id");
                if (signId == null || signId.length() == 0) {
                    signId = signElement.getAttribute("Id");
                }
                if (signId == null || signId.length() == 0) {
                    signId = SignPartialMessageProcessor.getSignId();
                    //signElement.setAttributeNS(SBConstants.WSU, "wsu:Id", signId);
                    CryptoUtil.setWsuId(signElement,  signId);
                } 

                if(sigSUN!=null){
	            
                	com.sun.org.apache.xml.internal.security.transforms.Transforms transforms = new com.sun.org.apache.xml.internal.security.transforms.Transforms(signElement.getOwnerDocument());
	                transforms.addTransform(com.sun.org.apache.xml.internal.security.transforms.Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
	                //OLDMETHOD: Transforms transforms = new Transforms(signElement.getOwnerDocument());
	             	//OLDMETHOD: transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
	                if (signReq.isWsiBPCompliant()) {
	
	                	transforms.item(0).getElement().appendChild(new com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces(
	                        signElement.getOwnerDocument(), CryptoUtil.getInclusivePrefixes(signElement, true)).getElement());
	                	
	                }
	                sigSUN.addDocument("#" + signId, transforms, signReq.getDigestAlgoURI());
                
                }
                
                if(sigXMLSec!=null){
                	
                	 org.apache.xml.security.transforms.Transforms transforms = new org.apache.xml.security.transforms.Transforms(signElement.getOwnerDocument());
                     transforms.addTransform(org.apache.xml.security.transforms.Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
                     //OLDMETHOD: Transforms transforms = new Transforms(signElement.getOwnerDocument());
                  	//OLDMETHOD: transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
                     if (signReq.isWsiBPCompliant()) {

                     	transforms.item(0).getElement().appendChild(new org.apache.xml.security.transforms.params.InclusiveNamespaces(
                             signElement.getOwnerDocument(), CryptoUtil.getInclusivePrefixes(signElement, true)).getElement());
                     	
                     }
                     sigXMLSec.addDocument("#" + signId, transforms, signReq.getDigestAlgoURI());
                	
                }

            } catch (Exception e) {
                throw new SecurityFailureException("Error processing signature for element : {" +
                    signElement.getNamespaceURI() + "}" + signElement.getLocalName() + " with Id : " + signId, e);
            }
        }
    }
    
	
	public static String getSignId(){
		//return CryptoUtil.getRandomId();
		return MessageSecurityContext_soapbox.getWsuIdAllocator().createId(CryptoUtil.getRandomId(), null);
	}
}
