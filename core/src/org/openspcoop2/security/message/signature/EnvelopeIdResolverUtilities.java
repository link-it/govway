/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * EnvelopeIdResolverUtilities
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EnvelopeIdResolverUtilities {

    public static Element findElementById(Node startNode, String value, String ns) throws Exception
    {
        Element foundElement = null;
        if(startNode == null)
            return null;
        Node startParent = startNode.getParentNode();
        Node processedNode = null;
        while(startNode != null) 
        {
            if(startNode.getNodeType() == 1)
            {
                Element se = (Element)startNode;
                if(se.hasAttributeNS(ns, "Id") && value.equals(se.getAttributeNS(ns, "Id")))
                    if(foundElement == null)
                        foundElement = se;
                    else
                        throw new Exception((new StringBuilder()).append("Multiple elements with the same 'Id' attribute : ").append(value).append(" has been found").toString());
            }
            processedNode = startNode;
            startNode = startNode.getFirstChild();
            if(startNode == null)
                startNode = processedNode.getNextSibling();
            while(startNode == null) 
            {
                processedNode = processedNode.getParentNode();
                if(processedNode == startParent)
                    return foundElement;
                startNode = processedNode.getNextSibling();
            }
        }
        return foundElement;
    }

}
