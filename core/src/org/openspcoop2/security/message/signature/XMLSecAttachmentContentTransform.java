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

package org.openspcoop2.security.message.signature;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.bouncycastle.util.encoders.Base64;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.security.message.constants.WSSAttachmentsConstants;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XMLException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sun.xml.wss.impl.c14n.Canonicalizer;
import com.sun.xml.wss.impl.c14n.CanonicalizerFactory;

/**
 * XMLSecAttachmentContentTransform
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLSecAttachmentContentTransform extends TransformSpi {

	private AbstractXMLUtils xmlUtils = null;
	
    public XMLSecAttachmentContentTransform(){
    	this.xmlUtils = XMLUtils.DEFAULT;
    }
    
	@Override
	protected String engineGetURI() {
		return WSSAttachmentsConstants.ATTACHMENT_CONTENT_SIGNATURE_TRANSFORM_URI;
	}
	
	
	// con jar: metro2.2-webservices_xwss_com_sun_org_jdk1.5.jar

	@Override
	protected XMLSignatureInput enginePerformTransform(org.apache.xml.security.signature.XMLSignatureInput xmlSignatureInput, 
			OutputStream out, 
			Element transformElement, String baseURI, boolean secureValidation) throws IOException, CanonicalizationException, InvalidCanonicalizerException,
			TransformationException, ParserConfigurationException, SAXException {

    	//System.out.println("XMLSignatureInput METRO 1");
    	if("text/xml".equals(xmlSignatureInput.getMIMEType())){
			
    		//System.out.println("XMLContent METRO 1");
    		XMLSecAttachmentTextXMLContentTransform t =
					new XMLSecAttachmentTextXMLContentTransform();
			try {
				return t.enginePerformTransform(getTextXMLSignatureInput(xmlSignatureInput), out, transformElement, baseURI, secureValidation);
			} catch (XMLException e) {
				throw new SAXException(e.getMessage(),e);
			}
		}
		else{
			
			//System.out.println("Base64Content METRO 1");
			XMLSecAttachmentBase64ContentTransform t =
					new XMLSecAttachmentBase64ContentTransform();
			try {
				return t.enginePerformTransform(getBase64SignatureInput(xmlSignatureInput), out, transformElement, baseURI, secureValidation);
			} catch (Exception e) {
				throw new SAXException(e.getMessage(),e);
			}
		}
	}

	private XMLSignatureInput getTextXMLSignatureInput(XMLSignatureInput input) throws CanonicalizationException, IOException, SAXException, ParserConfigurationException, XMLException{
		Element signElement = this.xmlUtils.newElement(input.getBytes());
		XMLSignatureInput result = new XMLSignatureInput(signElement);
		result.setMIMEType(input.getMIMEType());
		return result;
	}
	
	private XMLSignatureInput getBase64SignatureInput(XMLSignatureInput input) throws Exception{
		Canonicalizer canonicalizer = CanonicalizerFactory.getCanonicalizer(input.getMIMEType());
		byte[] canonicalize = canonicalizer.canonicalize(input.getBytes());
		XMLSignatureInput result = new XMLSignatureInput(Base64.encode(canonicalize));
		result.setMIMEType(input.getMIMEType());
		return result;
	}

}





