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
package org.openspcoop2.security.message.utils;

import javax.xml.namespace.QName;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * ElementProcessingPart
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ElementProcessingPart extends ProcessingPart<QName, Element>{
	
	public ElementProcessingPart(QName qname, boolean content) {
		this.part = qname;
		this.content = content;
	}

    private static Element getFirstChild(Element elem, String ns, String localName) throws Exception {
        if (elem.getLocalName() != null && elem.getLocalName().equals(localName) &&
            ((ns == null && elem.getNamespaceURI() == null) ||
            (ns != null && ns.equals(elem.getNamespaceURI())))) {
            return elem;
        }

        NodeList nl = elem.getElementsByTagNameNS(ns, localName);
        if (nl != null && nl.getLength() == 1 && nl.item(0) instanceof Element) {
            return (Element) nl.item(0);
        } else {
            throw new Exception("Expected element : {" + ns + "}" +
                localName + " not found as a child of : " + elem.toString());
        }
    }

	@Override
	public Element getOutput(OpenSPCoop2Message message) throws Exception {
		return getFirstChild(message.getSOAPPart().getEnvelope().getOwnerDocument().getDocumentElement(), this.part.getNamespaceURI(), this.part.getLocalPart());
	}


}
