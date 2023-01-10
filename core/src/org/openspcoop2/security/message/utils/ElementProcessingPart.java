/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.security.message.utils;

import javax.xml.namespace.QName;

import org.openspcoop2.message.OpenSPCoop2SoapMessage;
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
	public Element getOutput(OpenSPCoop2SoapMessage message) throws Exception {
		return getFirstChild(message.getSOAPPart().getEnvelope().getOwnerDocument().getDocumentElement(), this.part.getNamespaceURI(), this.part.getLocalPart());
	}


}
