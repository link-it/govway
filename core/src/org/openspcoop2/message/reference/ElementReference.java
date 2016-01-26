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

package org.openspcoop2.message.reference;

import javax.xml.soap.SOAPElement;

/**
 * ElementReference
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ElementReference extends Reference {

	private SOAPElement element;
	
	public static final int TYPE_ENCRYPT_CONTENT = 1;
	public static final int TYPE_ENCRYPT_ELEMENT = 2;
	public static final int TYPE_SIGNATURE = 3;
	
	public ElementReference(SOAPElement element, int type, String reference) {
		super(type, reference);
		this.setElement(element);
	}
	
	@Override
	public String toString() {
		
		String referenceType = null;
		if(super.type == ElementReference.TYPE_ENCRYPT_CONTENT || super.type == ElementReference.TYPE_SIGNATURE) {
			referenceType = Reference.REFERENCE_CONTENT;
		} else if(this.type == ElementReference.TYPE_ENCRYPT_ELEMENT) {
			referenceType = Reference.REFERENCE_ELEMENT;
		}
		return "{"+referenceType+"}{"+this.getElement().getNamespaceURI()+"}"+this.getElement().getLocalName();
	}

	public SOAPElement getElement() {
		return this.element;
	}

	public void setElement(SOAPElement element) {
		this.element = element;
	}
}
