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

package org.openspcoop2.security.message.signature;

import java.util.Iterator;

import javax.xml.soap.AttachmentPart;

import org.slf4j.Logger;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;

import org.apache.xml.utils.URI;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



/**
 * SunEnvelopeIdResolver
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SunEnvelopeIdResolver extends ResourceResolverSpi {

    private static final Logger logger = LoggerWrapperFactory.getLogger(SunEnvelopeIdResolver.class);
  
    public synchronized static ResourceResolverSpi getInstance(OpenSPCoop2Message message) {
        return new SunEnvelopeIdResolver(message);
    }

//    private AbstractXMLUtils xmlUtils = null;
    private OpenSPCoop2Message message = null;
    private SunEnvelopeIdResolver(OpenSPCoop2Message message) {
    	this.message = message;
//    	this.xmlUtils = XMLUtils.getInstance();
    }

    @SuppressWarnings("all")
    public boolean engineIsThreadSafe() {
        return true;
    }

    @Override
	public XMLSignatureInput engineResolve(Attr uri, String BaseURI) throws ResourceResolverException {

        String wsuId = null;
        boolean attach = false;
        if(uri.getNodeValue().startsWith("#")){
        	wsuId = uri.getNodeValue().substring(1);
        }else if(uri.getNodeValue().startsWith("cid:")){
        	wsuId = uri.getNodeValue().substring(4);
        	attach = true;
        }else{
        	throw new ResourceResolverException("Cannot resoulve uri "+uri.getNodeValue(),uri,BaseURI);
        }
        Document doc = uri.getOwnerDocument();

        if (SunEnvelopeIdResolver.logger.isDebugEnabled()) {
            SunEnvelopeIdResolver.logger.debug("Attempting to resolve : #" + wsuId);
        }

        if(attach){
        	Iterator<?> it = this.message.getAttachments();
        	while (it.hasNext()) {
				AttachmentPart ap = (AttachmentPart) it.next();
				String contentId = ap.getContentId();
				if(contentId.startsWith("<"))
					contentId = contentId.substring(1);
				if(contentId.endsWith(">"))
					contentId = contentId.substring(0,contentId.length()-1);
				if(wsuId.equals(contentId)){
					try{
						byte[]raw = ap.getRawContentBytes();
						XMLSignatureInput result = new XMLSignatureInput(raw);
						result.setMIMEType(ap.getContentType());
						return result;
					}catch(Exception e){
						throw new ResourceResolverException(e.getMessage(),e,uri,BaseURI);
					}
//					try{
////						String contentType = ap.getContentType();
////						byte[]raw = ap.getRawContentBytes();
////						if("text/xml".equals(contentType)){
////							Element signElement = this.xmlUtils.newElement(raw);
////							XMLSignatureInput result = new XMLSignatureInput(signElement);
////					        result.setMIMEType("text/xml");
////					        try {
////					            URI uriNew = new URI(new URI(BaseURI), uri.getNodeValue());
////					            result.setSourceURI(uriNew.toString());
////					        } catch (URI.MalformedURIException ex) {
////					            result.setSourceURI(BaseURI);
////					        }
////					        return result;
////						}
////						else{
////							Canonicalizer canonicalizer = CanonicalizerFactory.getCanonicalizer(contentType);
////							byte[] canonicalize = canonicalizer.canonicalize(raw);
////							XMLSignatureInput result = new XMLSignatureInput(Base64.encode(canonicalize));
////							result.setMIMEType(ap.getContentType());
////							return result;
////						}
//					}catch(Exception e){
//						throw new ResourceResolverException(e.getMessage(),e,uri,BaseURI);
//					}
				}
			}
        	
        	throw new ResourceResolverException("Cannot resoulve attachment uri "+uri.getNodeValue(),uri,BaseURI);
        }
        else{
        	Element refElem = null;
        	try{
        		refElem = EnvelopeIdResolverUtilities.findElementById(doc, wsuId, SecurityConstants.WSS_HEADER_UTILITY_NAMESPACE);
		        if (refElem == null) {
		            refElem = EnvelopeIdResolverUtilities.findElementById(doc, wsuId, "");
		        }
        	}catch(Exception e){
        		throw new ResourceResolverException("Cannot resoulve uri "+uri.getNodeValue()+" : "+e.getMessage(),e,uri,BaseURI);
        	}
	
	        XMLSignatureInput result = new XMLSignatureInput(refElem);
	        result.setMIMEType("text/xml");
	        try {
	            URI uriNew = new URI(new URI(BaseURI), uri.getNodeValue());
	            result.setSourceURI(uriNew.toString());
	        } catch (URI.MalformedURIException ex) {
	            result.setSourceURI(BaseURI);
	        }
	
	        if (SunEnvelopeIdResolver.logger.isDebugEnabled()) {
	            SunEnvelopeIdResolver.logger.debug("Result: " + result);
	        }
	        return result;
        }
        
    }

    /**
     * This method helps the ResourceResolver to decide whether a
     * ResourceResolverSpi is able to perform the requested action.
     *
     * @param uri
     * @param BaseURI
     * @return true if this attribute can be resolved
     */
    @Override
	public boolean engineCanResolve(Attr uri, String BaseURI) {
        if (uri == null) {
            return false;
        }
        String uriNodeValue = uri.getNodeValue();
        return uriNodeValue.startsWith("#") || uriNodeValue.startsWith("cid:");
    }
}
