/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.xml;

import javax.xml.soap.SOAPElement;

import org.w3c.dom.Element;

/**	
 * XPathExpressionEngine
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XPathExpressionEngine extends org.openspcoop2.utils.xml.AbstractXPathExpressionEngine {

	@Override
	public String getAsString(SOAPElement element) {
		try{
			return getXMLUtils().toString(element);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public AbstractXMLUtils getXMLUtils() {
		return new XMLUtils();
	}

	@Override
	public Element readXPathElement(Element contenutoAsElement){
		return contenutoAsElement;
	}
	
}
