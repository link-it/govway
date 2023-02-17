/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import javax.xml.XMLConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPathFactory;

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
	
	// XERCES
	@Override
	protected DocumentBuilderFactory newDocumentBuilderFactory() throws XMLException{
		try{
			// force xerces impl
//			System.setProperty("javax.xml.parsers.DocumentBuilderFactory", org.apache.xerces.jaxp.DocumentBuilderFactoryImpl.class.getName());
//			return DocumentBuilderFactory.newInstance();
			return DocumentBuilderFactory.newInstance(org.apache.xerces.jaxp.DocumentBuilderFactoryImpl.class.getName(),this.getClass().getClassLoader());
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	@Override
	protected DatatypeFactory newDatatypeFactory() throws XMLException{
		try{
			// force xerces impl
//			System.setProperty("javax.xml.datatype.DatatypeFactory", org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl.class.getName());
//			return DatatypeFactory.newInstance();
			return DatatypeFactory.newInstance(org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl.class.getName(), this.getClass().getClassLoader());
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	@Override
	protected javax.xml.parsers.SAXParserFactory newSAXParserFactory() throws XMLException{
		try{
			// force xerces impl
//			System.setProperty("javax.xml.parsers.SAXParserFactory", org.apache.xerces.jaxp.SAXParserFactoryImpl.class.getName());
//			return javax.xml.parsers.SAXParserFactory.newInstance();
			javax.xml.parsers.SAXParserFactory saxParserFactory = javax.xml.parsers.SAXParserFactory.newInstance(org.apache.xerces.jaxp.SAXParserFactoryImpl.class.getName(), this.getClass().getClassLoader());
			if(DISABLE_DTDs) {
				saxParserFactory.setFeature(getFeatures_disallow_doctype_decl(), true);
			}
			return saxParserFactory;
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	/*
	@Override
	protected javax.xml.stream.XMLEventFactory newXMLEventFactory() throws XMLException{
		try{			
			// force xerces impl
//			System.setProperty("javax.xml.stream.XMLEventFactory", org.apache.xerces.stax.XMLEventFactoryImpl.class.getName());
//			return javax.xml.stream.XMLEventFactory.newFactory();
			return javax.xml.stream.XMLEventFactory.newFactory(org.apache.xerces.stax.XMLEventFactoryImpl.class.getName(), this.getClass().getClassLoader());
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	*/
	@Override
	protected SchemaFactory newSchemaFactory() throws XMLException{
		try{
			// force xerces impl
			return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI,
					org.apache.xerces.jaxp.validation.XMLSchemaFactory.class.getName(), this.getClass().getClassLoader());
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	
	// XALAN
	@Override
	protected TransformerFactory newTransformerFactory() throws XMLException{
		try{
			// force xalan impl
//			System.setProperty("javax.xml.transform.TransformerFactory", org.apache.xalan.processor.TransformerFactoryImpl.class.getName());
//			return TransformerFactory.newInstance();
			return TransformerFactory.newInstance(org.apache.xalan.processor.TransformerFactoryImpl.class.getName(), this.getClass().getClassLoader());
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	@Override
	protected XPathFactory newXPathFactory() throws XMLException{
		try{
			//return XPathFactory.newInstance();
			// force xalan impl
			return XPathFactory.newInstance(XPathFactory.DEFAULT_OBJECT_MODEL_URI,
					org.apache.xpath.jaxp.XPathFactoryImpl.class.getName(), this.getClass().getClassLoader());
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}

}
