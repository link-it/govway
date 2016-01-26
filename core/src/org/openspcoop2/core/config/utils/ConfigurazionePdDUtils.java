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

package org.openspcoop2.core.config.utils;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *  ConfigurazionePdDUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePdDUtils {

	public static boolean isConfigurazionePdD(byte [] doc){
		return ConfigurazionePdDUtils.isConfigurazionePdD(doc,CostantiConfigurazione.ROOT_LOCAL_NAME_CONFIG);
	}
	public static boolean isConfigurazionePdD(byte [] doc,String localName){
		try{
			org.openspcoop2.message.XMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			return ConfigurazionePdDUtils.isConfigurazionePdD_engine(elemXML,localName);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isConfigurazionePdD(Document docXML){
		return ConfigurazionePdDUtils.isConfigurazionePdD(docXML,CostantiConfigurazione.ROOT_LOCAL_NAME_CONFIG);
	}
	public static boolean isConfigurazionePdD(Document docXML,String localName){
		try{
			Element elemXML = docXML.getDocumentElement();
			return ConfigurazionePdDUtils.isConfigurazionePdD_engine(elemXML,localName);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isConfigurazionePdD(Element elemXML,String localName){
		return isConfigurazionePdD_engine(elemXML,localName);
	}
	public static boolean isConfigurazionePdD(Node nodeXml,String localName){
		return isConfigurazionePdD_engine(nodeXml,localName);
	}
	private static boolean isConfigurazionePdD_engine(Node nodeXml,String localName){
		try{
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME_DETTAGLIO_ECCEZIONE+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");
			if(localName.equals(nodeXml.getLocalName()) && 
					CostantiConfigurazione.TARGET_NAMESPACE.equals(nodeXml.getNamespaceURI() ) 
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
