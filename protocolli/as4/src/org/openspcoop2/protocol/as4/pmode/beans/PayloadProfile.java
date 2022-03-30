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
public class PayloadProfile {

	private String name;
	private Long maxSize;
	private List<String> attachments;
	/**
	 * @param node
	 * @throws XMLException 
	 * @throws TransformerException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public PayloadProfile(Node node) throws Exception {
		this.name = node.getAttributes().getNamedItem("name").getNodeValue();
		this.maxSize = Long.parseLong(node.getAttributes().getNamedItem("maxSize").getNodeValue());
		NodeList attachments = XMLUtils.getInstance().getAsDocument(node).getElementsByTagName("attachment");
		this.attachments = new ArrayList<>();
		
		for(int i = 0; i < attachments.getLength(); i++) {
			Node item = attachments.item(i);
			Node namedItem = item.getAttributes().getNamedItem("name");
			this.attachments.add(namedItem.getNodeValue());
		}

	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getMaxSize() {
		return this.maxSize;
	}
	public String getMaxSizeAsString() {
		return this.maxSize.toString();
	}
	public void setMaxSize(Long maxSize) {
		this.maxSize = maxSize;
	}
	public List<String> getAttachments() {
		return this.attachments;
	}
	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null)
			return false;
		
		if(!(obj instanceof PayloadProfile))
			return false;
		
		PayloadProfile p = (PayloadProfile) obj;
		
		if(!this.name.equals(p.getName()))
			return false;
		
		if(!this.maxSize.equals(p.getMaxSize()))
			return false;
		
		if(this.attachments.size() != p.getAttachments().size())
			return false;
		
		for(String att: this.attachments) {
			if(!p.getAttachments().contains(att))
				return false;
		}
		
		return true;
		

	}
}
