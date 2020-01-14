/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
/**
 * 
 */
package org.openspcoop2.protocol.as4.pmode.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PropertySet {

	private String name;
	private List<String> propertyRef;
	/**
	 * @param node
	 * @throws XMLException 
	 * @throws TransformerException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public PropertySet(Node node) throws Exception {
		this.name = node.getAttributes().getNamedItem("name").getNodeValue();
		NodeList pRef = XMLUtils.getInstance().getAsDocument(node).getElementsByTagName("propertyRef");
		this.propertyRef = new ArrayList<>();
		
		for(int i = 0; i < pRef.getLength(); i++) {
			Node item = pRef.item(i);
			Node namedItem = item.getAttributes().getNamedItem("property");
			this.propertyRef.add(namedItem.getNodeValue());
		}

	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getPropertyRef() {
		return this.propertyRef;
	}
	public void setPropertyRef(List<String> propertyRef) {
		this.propertyRef = propertyRef;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null)
			return false;
		
		if(!(obj instanceof PropertySet))
			return false;
		
		PropertySet p = (PropertySet) obj;
		
		if(!this.name.equals(p.getName()))
			return false;
		
		if(this.propertyRef.size() != p.getPropertyRef().size())
			return false;
		
		for(String ref: this.propertyRef) {
			if(!p.getPropertyRef().contains(ref))
				return false;
		}
		
		return true;
		

	}
}
