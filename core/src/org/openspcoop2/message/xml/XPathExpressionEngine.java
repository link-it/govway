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

package org.openspcoop2.message.xml;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Element;

/**
 * XPathExpressionEngine
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XPathExpressionEngine extends org.openspcoop2.utils.xml.AbstractXPathExpressionEngine {

	private OpenSPCoop2MessageFactory messageFactory;
	
	public XPathExpressionEngine(OpenSPCoop2MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}
	
	@Override
	public String getAsString(SOAPElement element) {
		try{
			return OpenSPCoop2MessageFactory.getAsString(this.messageFactory, element, false);
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	@Override
	public AbstractXMLUtils getXMLUtils() {
		return org.openspcoop2.message.xml.XMLUtils.getInstance(this.messageFactory);
	}

	@Override
	public Element readXPathElement(Element contenutoAsElement){
		return this.messageFactory.convertoForXPathSearch(contenutoAsElement);
	}
}
