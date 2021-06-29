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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclOmitComments;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.implementations.TransformC14NExclusive;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * XMLSecAttachmentTextXMLContentTransform
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLSecAttachmentTextXMLContentTransform extends TransformC14NExclusive {

	@Override
	protected XMLSignatureInput enginePerformTransform(XMLSignatureInput xmlsignatureinput,
			OutputStream outputstream, Element transformElement, String baseURI, boolean secureValidation) throws CanonicalizationException {
		
		try{		
	        XMLSignatureInput xmlsignatureinput1 = null;
	        String s = null;
	        if(length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", transformElement) == 1)
	        {
	            Element element = XMLUtils.selectNode(transformElement.getFirstChild(), 
	            		"http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0);
	            s = (new InclusiveNamespaces(element, baseURI)).getInclusiveNamespaces();
	        }
	        Canonicalizer20010315ExclOmitComments canonicalizer20010315exclomitcomments = 
	        		new Canonicalizer20010315ExclOmitComments();
	        xmlsignatureinput.setNeedsToBeExpanded(true);
	        ByteArrayOutputStream bout = new ByteArrayOutputStream();
	        canonicalizer20010315exclomitcomments.engineCanonicalize(xmlsignatureinput, s, bout, secureValidation);
	        bout.flush();
	        bout.close();
	        byte abyte0[] = bout.toByteArray();
	        xmlsignatureinput1 = new XMLSignatureInput(abyte0);
	        if(outputstream != null)
	            xmlsignatureinput1.setOutputStream(outputstream);
	        return xmlsignatureinput1;
		}catch(Exception e){
			throw new CanonicalizationException(e);
		}
	}
	
    private int length(String namespace, String localname, Element transformElement) {
        int number = 0;
        Node sibling = transformElement.getFirstChild();
        while (sibling != null) {        
            if (localname.equals(sibling.getLocalName())
                && namespace.equals(sibling.getNamespaceURI())) {
                number++;
            }
            sibling = sibling.getNextSibling();
        }
        return number;
    }
}
