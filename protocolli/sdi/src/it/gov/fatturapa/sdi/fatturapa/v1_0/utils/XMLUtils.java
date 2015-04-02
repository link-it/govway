package it.gov.fatturapa.sdi.fatturapa.v1_0.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
