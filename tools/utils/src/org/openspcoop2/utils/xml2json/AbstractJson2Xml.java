/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
/**
 * 
 */
package org.openspcoop2.utils.xml2json;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Node;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public abstract class AbstractJson2Xml implements IJson2Xml{

	private TransformerFactory transformerFactory;
	private XMLUtils xmlUtils;
	
	public AbstractJson2Xml() {
		this.xmlUtils = XMLUtils.getInstance();
		// si trova in: jaxp-ri-1.4.5-gov4j-1.jar
		this.transformerFactory = TransformerFactory.newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", this.getClass().getClassLoader());
	}

	protected abstract XMLInputFactory getInputFactory();

	@Override
	public String json2xml(String jsonString) throws UtilsException {
		try {
			StringWriter stringWriter = new StringWriter();
			XMLStreamReader xmlStreamReader = this.getInputFactory().createXMLStreamReader(new StringReader(jsonString));
			Transformer transformer = this.transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	
			transformer.transform(new StAXSource(xmlStreamReader), new StreamResult(stringWriter));
			return stringWriter.toString();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	@Override
	public Node json2xmlNode(String jsonString) throws UtilsException {
		try {
			String xmlstring = json2xml(jsonString);
			return this.xmlUtils.newElement(xmlstring.getBytes());
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
