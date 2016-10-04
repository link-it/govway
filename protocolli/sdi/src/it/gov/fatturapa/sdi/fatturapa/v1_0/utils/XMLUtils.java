/*
 * OpenSPCoop - Customizable API Gateway 
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

package it.gov.fatturapa.sdi.fatturapa.v1_0.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * XMLUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLUtils {

	public static boolean isFattura(byte [] doc){
		try{
			org.openspcoop2.message.XMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isFattura_engine(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isFattura(Document docXML){
		try{
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isFattura_engine(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isFattura(Element elemXML){
		return isFattura_engine(elemXML);
	}
	public static boolean isFattura(Node nodeXml){
		return isFattura_engine(nodeXml);
	}
	private static boolean isFattura_engine(Node nodeXml){
		try{
			ProjectInfo info = new ProjectInfo();
			if("FatturaElettronica".equals(nodeXml.getLocalName()) && 
					info.getProjectNamespace().equals(nodeXml.getNamespaceURI() ) 
				){
				return true;
			}
			else{
				return false;
			}
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	
}
