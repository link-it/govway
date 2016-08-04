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

import java.io.OutputStream;

import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;

/**
 * SunAttachmentTextXMLContentTransform
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SunAttachmentTextXMLContentTransform extends com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NExclusive {


	@Override
	protected XMLSignatureInput enginePerformTransform(XMLSignatureInput xmlsignatureinput,
			Transform _transformObject) throws CanonicalizationException {
		return performTransform_engine(xmlsignatureinput, null, _transformObject);
	}
	@Override
	protected XMLSignatureInput enginePerformTransform(XMLSignatureInput xmlsignatureinput,
			OutputStream os, Transform _transformObject)
			throws CanonicalizationException {
		return performTransform_engine(xmlsignatureinput, os, _transformObject);
	}
	
	
	private XMLSignatureInput performTransform_engine(XMLSignatureInput xmlsignatureinput,
			OutputStream outputstream,
			Transform transform) throws CanonicalizationException {
		
		try{		
	        XMLSignatureInput xmlsignatureinput1 = null;
	        String s = null;
	        if(transform.length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1)
	        {
	            Element element = com.sun.org.apache.xml.internal.security.utils.XMLUtils.selectNode(transform.getElement().getFirstChild(), 
	            		"http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0);
	            s = (new com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces(element, transform.getBaseURI())).getInclusiveNamespaces();
	        }
	        com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclOmitComments canonicalizer20010315exclomitcomments = 
	        		new com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclOmitComments();
	        if(outputstream != null)
	            canonicalizer20010315exclomitcomments.setWriter(outputstream);
	        xmlsignatureinput.setNeedsToBeExpanded(true);
	        byte abyte0[] = canonicalizer20010315exclomitcomments.engineCanonicalize(xmlsignatureinput, s);
	        xmlsignatureinput1 = new XMLSignatureInput(abyte0);
	        if(outputstream != null)
	            xmlsignatureinput1.setOutputStream(outputstream);
	        return xmlsignatureinput1;
		}catch(Exception e){
			throw new CanonicalizationException(e.getMessage(),e);
		}
	}
}
