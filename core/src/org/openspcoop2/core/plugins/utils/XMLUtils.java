/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.plugins.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Classe utilizzata per la gestione dei plugins
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class XMLUtils  {

	private XMLUtils() {}
	
	public static boolean isPlugin(byte [] doc){
		try{
			org.openspcoop2.message.xml.MessageXMLUtils xmlUtils = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT;
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isPluginEngine(elemXML);
		}catch(Exception e){
			/**System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());*/
			return false;
		}
	}
	public static boolean isPlugin(Document docXML){
		try{
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isPluginEngine(elemXML);
		}catch(Exception e){
			/**System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());*/
			return false;
		}
	}
	public static boolean isPlugin(Element elemXML){
		return isPluginEngine(elemXML);
	}
	public static boolean isPlugin(Node nodeXml){
		return isPluginEngine(nodeXml);
	}
	private static boolean isPluginEngine(Node nodeXml){
		try{
			ProjectInfo pInfo = new ProjectInfo();
			
			/**System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME_DETTAGLIO_ECCEZIONE+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");*/
			return "plugin".equals(nodeXml.getLocalName()) && 
					pInfo.getProjectNamespace().equals(nodeXml.getNamespaceURI()) ;
		}catch(Exception e){
			/**System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());*/
			return false;
		}
	}
	
}