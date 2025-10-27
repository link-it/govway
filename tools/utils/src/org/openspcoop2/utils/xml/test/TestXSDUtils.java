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
package org.openspcoop2.utils.xml.test;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml.XSDResourceResolver;
import org.openspcoop2.utils.xml.XSDSchemaCollection;
import org.openspcoop2.utils.xml.XSDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * TestXSDUtils
 * 
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestXSDUtils {

	public static final String TYPE_CYCLIC_1 =
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
		+ "<xs:schema targetNamespace=\"http://test.cyclic.org\"\n"
		+ "  elementFormDefault=\"qualified\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
		+ "  xmlns:sg=\"http://test.cyclic.org\">\n"
		+ "\n"
		+ "  <xs:include schemaLocation=\"cycle_2.xsd\"/>\n"
		+ "\n"
		+ "  <xs:complexType name=\"abstractType\" abstract=\"true\"/>\n"
		+ "\n"
		+ "</xs:schema>";

	public static final String TYPE_CYCLIC_2 =
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
		+ "<xs:schema targetNamespace=\"http://test.cyclic.org\"\n"
		+ "  elementFormDefault=\"qualified\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
		+ "  xmlns:sg=\"http://test.cyclic.org\">\n"
		+ "  \n"
		+ "  <xs:include schemaLocation=\"cycle_1.xsd\"/>\n"
		+ "\n"
		+ "</xs:schema>";

	public static final String ROOT_XSD =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
		+ "	elementFormDefault=\"qualified\"\n"
		+ "	targetNamespace=\"http://www.test.org/root\"\n"
		+ "	xmlns:c=\"http://test.cyclic.org\"\n"
		+ "	xmlns:r=\"http://test.root.org\">\n"
		+ "	\n"
		+ "	<xs:import namespace=\"http://test.cyclic.org\" schemaLocation=\"cycle_1.xsd\"/>\n"
		+ "				\n"
		+ "	<xs:complexType name=\"concreteType\">\n"
		+ "		<xs:complexContent>\n"
		+ "			<xs:extension base=\"c:abstractType\">\n"
		+ "			</xs:extension>\n"
		+ "		</xs:complexContent>\n"
		+ "	</xs:complexType>\n"
		+ "	\n"
		+ "</xs:schema>";

	public static void main(String[] args) throws Exception {
		TestXSDUtils test = new TestXSDUtils();
		
		test.testCycle();
	}
	
	/**
	 * Test che controlla il parsing di schemi multipli xsd con cicli 
	 * @throws XMLException
	 * @throws SAXException
	 */
	public void testCycle() throws XMLException, SAXException {
		XMLUtils xmlUtils = XMLUtils.getInstance();
		XSDUtils xsdUtils = new XSDUtils(xmlUtils);
		
		Map<String, byte[]> resources = new HashMap<>();
		resources.put("cycle_1.xsd", TYPE_CYCLIC_1.getBytes());
		resources.put("cycle_2.xsd", TYPE_CYCLIC_2.getBytes());
		resources.put("root.xsd", ROOT_XSD.getBytes());
				
		Map<String, List<String>> mappingNamespaceLocation = new HashMap<>();
		mappingNamespaceLocation.put("http://test.cyclic.org", List.of("cycle_1.xsd", "cycle_2.xsd"));
		mappingNamespaceLocation.put("http://www.test.org/root", List.of("root.xsd"));
		
		Logger logger = LoggerFactory.getLogger(TestXSDUtils.class);
		
		XSDSchemaCollection collection = xsdUtils.buildSchemaCollection(resources, mappingNamespaceLocation, logger);
		SchemaFactory factory = xmlUtils.getSchemaFactory();

		XSDResourceResolver resourceResolver = new XSDResourceResolver(collection.getResources());
		factory.setResourceResolver(resourceResolver);

		StreamSource schemaSource = new StreamSource(new ByteArrayInputStream(collection.getSchemaRoot()));
		factory.newSchema(schemaSource);
	}
}
