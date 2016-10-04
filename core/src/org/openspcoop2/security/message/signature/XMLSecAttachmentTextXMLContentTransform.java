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

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclOmitComments;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.implementations.TransformC14NExclusive;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * XMLSecAttachmentTextXMLContentTransform
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLSecAttachmentTextXMLContentTransform extends TransformC14NExclusive {

	@Override
	public XMLSignatureInput enginePerformTransform(XMLSignatureInput xmlsignatureinput,
			OutputStream outputstream, Transform transform) throws CanonicalizationException {
		return performTransform_engine(xmlsignatureinput, outputstream, transform);
	}

	@Override
	public XMLSignatureInput enginePerformTransform(XMLSignatureInput xmlsignatureinput,
			Transform transform) throws CanonicalizationException, InvalidCanonicalizerException, TransformationException, IOException, ParserConfigurationException, SAXException {
		return performTransform_engine(xmlsignatureinput, null, transform);
	}

	private XMLSignatureInput performTransform_engine(XMLSignatureInput xmlsignatureinput,
			OutputStream outputstream,
			Transform transform) throws CanonicalizationException {
		
		try{		
	        XMLSignatureInput xmlsignatureinput1 = null;
	        String s = null;
	        if(transform.length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1)
	        {
	            Element element = XMLUtils.selectNode(transform.getElement().getFirstChild(), 
	            		"http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0);
	            s = (new InclusiveNamespaces(element, transform.getBaseURI())).getInclusiveNamespaces();
	        }
	        Canonicalizer20010315ExclOmitComments canonicalizer20010315exclomitcomments = 
	        		new Canonicalizer20010315ExclOmitComments();
	        if(outputstream != null)
	            canonicalizer20010315exclomitcomments.setWriter(outputstream);
	        xmlsignatureinput.setNeedsToBeExpanded(true);
	        byte abyte0[] = canonicalizer20010315exclomitcomments.engineCanonicalize(xmlsignatureinput, s);
	        xmlsignatureinput1 = new XMLSignatureInput(abyte0);
	        if(outputstream != null)
	            xmlsignatureinput1.setOutputStream(outputstream);
	        return xmlsignatureinput1;
		}catch(Exception e){
			throw new CanonicalizationException(e);
		}
	}
}
