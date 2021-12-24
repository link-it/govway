/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package it.gov.fatturapa.sdi.messaggi.v1_0.utils;

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

	public static final String NAMESPACE_SENZA_GOV = new ProjectInfo().getProjectNamespace().replace("www.fatturapa.gov.it", "www.fatturapa.it");
		
	public static boolean isNotificaPA(byte [] doc, boolean compatibilitaNamespaceSenzaGov){
		try{
			org.openspcoop2.message.xml.XMLUtils xmlUtils = org.openspcoop2.message.xml.XMLUtils.DEFAULT;
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isNotificaPA_engine(elemXML, compatibilitaNamespaceSenzaGov);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isNotificaPA(Document docXML, boolean compatibilitaNamespaceSenzaGov){
		try{
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isNotificaPA_engine(elemXML,compatibilitaNamespaceSenzaGov);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isNotificaPA(Element elemXML, boolean compatibilitaNamespaceSenzaGov){
		return isNotificaPA_engine(elemXML,compatibilitaNamespaceSenzaGov);
	}
	public static boolean isNotificaPA(Node nodeXml, boolean compatibilitaNamespaceSenzaGov){
		return isNotificaPA_engine(nodeXml, compatibilitaNamespaceSenzaGov);
	}
	private static boolean isNotificaPA_engine(Node nodeXml, boolean compatibilitaNamespaceSenzaGov){
		try{
			ProjectInfo info = new ProjectInfo();
			if(info.getProjectNamespace().equals(nodeXml.getNamespaceURI() ) 
				){
				return true;
			}
			else if(compatibilitaNamespaceSenzaGov && NAMESPACE_SENZA_GOV.equals(nodeXml.getNamespaceURI())) {
				return true;
			}
			else {
				return false;
			}
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	
	
	
}
