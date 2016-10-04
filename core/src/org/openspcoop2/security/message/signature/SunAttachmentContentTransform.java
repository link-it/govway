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

package org.openspcoop2.security.message.signature;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.bouncycastle.util.encoders.Base64;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.security.message.constants.WSSAttachmentsConstants;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XMLException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.xml.wss.impl.c14n.Canonicalizer;
import com.sun.xml.wss.impl.c14n.CanonicalizerFactory;

/**
 * SunAttachmentContentTransform
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SunAttachmentContentTransform extends TransformSpi {

	private AbstractXMLUtils xmlUtils = null;
	
    public SunAttachmentContentTransform(){
    	this.xmlUtils = XMLUtils.getInstance();
    }
    
	@Override
	protected String engineGetURI() {
		return WSSAttachmentsConstants.ATTACHMENT_CONTENT_SIGNATURE_TRANSFORM_URI;
	}
	
	
	// RIDEFINIZIONE DEI METODI DELLA CLASSE  com.sun.org.apache.xml.internal.security.transforms.TransformSpi PRESENTE IN JAVA 1.7
	@Override
	protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input,
			OutputStream os, Transform _transformObject) throws IOException,
			CanonicalizationException, InvalidCanonicalizerException,
			TransformationException, ParserConfigurationException, SAXException {
    	//System.out.println("XMLSignatureInput 1 JAVA 1.7");
		if("text/xml".equals(input.getMIMEType())){
			//System.out.println("XMLContent 1 JAVA 1.7");
			SunAttachmentTextXMLContentTransform t =
					new SunAttachmentTextXMLContentTransform();
			try {
				return t.enginePerformTransform(getTextXMLSignatureInput(input), os, _transformObject);
			} catch (XMLException e) {
				throw new SAXException(e.getMessage(),e);
			}
		}
		else{
			//System.out.println("Base64Content 1 JAVA 1.7");
			SunAttachmentBase64ContentTransform t =
					new SunAttachmentBase64ContentTransform();
			try {
				return t.sunEnginePerformTransform(getBase64SignatureInput(input), os, _transformObject);
			} catch (Exception e) {
				throw new SAXException(e.getMessage(),e);
			}
		}
	}
	 
    @Override
	protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input,
			Transform _transformObject) throws IOException,
			CanonicalizationException, InvalidCanonicalizerException,
			TransformationException, ParserConfigurationException, SAXException {
		//System.out.println("XMLSignatureInput 2 JAVA 1.7");
		if("text/xml".equals(input.getMIMEType())){
			//System.out.println("XMLContent 2 JAVA 1.7");
			SunAttachmentTextXMLContentTransform t =
					new SunAttachmentTextXMLContentTransform();
			try {
				return t.enginePerformTransform(getTextXMLSignatureInput(input),_transformObject);
			} catch (XMLException e) {
				throw new SAXException(e.getMessage(),e);
			}
		}
		else{
			//System.out.println("Base64Content 2 JAVA 1.7");
			SunAttachmentBase64ContentTransform t =
					new SunAttachmentBase64ContentTransform();
			try {
				return t.sunEnginePerformTransform(getBase64SignatureInput(input),_transformObject);
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



