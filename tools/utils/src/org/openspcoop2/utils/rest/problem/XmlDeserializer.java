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

package org.openspcoop2.utils.rest.problem;

import java.util.List;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * XmlDeserializer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XmlDeserializer extends AbstractDeserializer {

	private XMLUtils xmlUtils;
	
	public XmlDeserializer() {
		this(false);
	}
	public XmlDeserializer(boolean generateTypeBlank) {
		super(generateTypeBlank);
		this.xmlUtils = XMLUtils.getInstance();
	}
	
	public boolean isProblemRFC7807(String problemString) {
		Element problemNode = null;
		try {
			problemNode = this.xmlUtils.newElement(problemString.getBytes());
		}catch(Exception e) {
			return false;
		}
		return this.isProblemRFC7807(problemNode);
	}
	public boolean isProblemRFC7807(byte[] problemByteArray) {
		Element problemNode = null;
		try {
			problemNode = this.xmlUtils.newElement(problemByteArray);
		}catch(Exception e) {
			return false;
		}
		return this.isProblemRFC7807(problemNode);
	}
	public boolean isProblemRFC7807(Node problemNode) {
		return problemNode!=null && 
				XmlSerializer.XML_PROBLEM_DETAILS_RFC_7807_LOCAL_NAME.equals(problemNode.getLocalName()) &&
				XmlSerializer.XML_PROBLEM_DETAILS_RFC_7807_NAMESPACE.equals(problemNode.getNamespaceURI());
	}
	
	public ProblemRFC7807 fromString(String problemString) throws UtilsException {
		Element problemNode = null;
		try {
			problemNode = this.xmlUtils.newElement(problemString.getBytes());
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		return this.fromNode(problemNode);
	}
	public ProblemRFC7807 fromByteArray(byte[] problemByteArray) throws UtilsException {
		Element problemNode = null;
		try {
			problemNode = this.xmlUtils.newElement(problemByteArray);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		return this.fromNode(problemNode);
	}
	public ProblemRFC7807 fromNode(Node problemNode) throws UtilsException {
		
		ProblemRFC7807 problem = new ProblemRFC7807();
		
		List<Node> list = this.xmlUtils.getNotEmptyChildNodes(problemNode);
		for (Node node : list) {
			
			String name = node.getLocalName();
			Object value = node.getTextContent();
			
			super.set(problem, name, value);
		}

		return problem;
	}
	
}
