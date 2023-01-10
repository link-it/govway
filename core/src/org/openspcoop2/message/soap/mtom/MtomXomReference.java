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
package org.openspcoop2.message.soap.mtom;

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
		StringBuilder bf = new StringBuilder();
		bf.append("NodeName[").append(this.nodeName).append("]");
		bf.append(" ContentId[").append(this.contentId).append("]");
		return bf.toString();
	}
}
