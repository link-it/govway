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

package org.openspcoop2.utils.xml;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

/**	
 * XMLUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLUtils extends AbstractXMLUtils {

	private static XMLUtils xmlUtils = null;
	private static synchronized void init(){
		if(XMLUtils.xmlUtils==null){
			XMLUtils.xmlUtils = new XMLUtils();
		}
	}
	public static XMLUtils getInstance(){
		if(XMLUtils.xmlUtils==null){
			XMLUtils.init();
		}
		return XMLUtils.xmlUtils;
	}
	
	@Override
	protected DocumentBuilderFactory newDocumentBuilderFactory() throws XMLException {
		return DocumentBuilderFactory.newInstance();
	}

	@Override
	protected TransformerFactory newTransformerFactory() throws XMLException {
		try{
			return TransformerFactory.newInstance();
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}

	@Override
	protected DatatypeFactory newDatatypeFactory() throws XMLException {
		try{
			return DatatypeFactory.newInstance();
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}

}
