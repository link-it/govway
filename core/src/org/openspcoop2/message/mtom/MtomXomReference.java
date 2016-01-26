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
package org.openspcoop2.message.mtom;

import javax.xml.namespace.QName;

import org.w3c.dom.Node;

/**
 * MtomXomReference
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MtomXomReference {

	private QName nodeName;
	private Node node;
	private Node xomReference;
	private String contentId;
	
	public QName getNodeName() {
		return this.nodeName;
	}
	public void setNodeName(QName nodeName) {
		this.nodeName = nodeName;
	}
	public Node getNode() {
		return this.node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public Node getXomReference() {
		return this.xomReference;
	}
	public void setXomReference(Node xomReference) {
		this.xomReference = xomReference;
	}
	public String getContentId() {
		return this.contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		bf.append("NodeName[").append(this.nodeName).append("]");
		bf.append(" ContentId[").append(this.contentId).append("]");
		return bf.toString();
	}
}
