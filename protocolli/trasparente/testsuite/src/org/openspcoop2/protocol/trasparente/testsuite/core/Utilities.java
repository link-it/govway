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



package org.openspcoop2.protocol.trasparente.testsuite.core;

import java.io.File;

import org.openspcoop2.message.DynamicNamespaceContextFactory;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.message.XPathExpressionEngine;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Document;

/**
 * Contiene utility per i test effettuati.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Utilities {

	/** 
	 * Counter utilizzato per la creazione dell'identificativo eGov
	 */
	public static int counterIDSeriale = 1;
	
	/** TestSuite Properties */
	public static TestSuiteProperties testSuiteProperties = TestSuiteProperties.getInstance();
	
	/** Database Properties */
	public static org.openspcoop2.testsuite.units.utils.DatabaseProperties databaseProperties = DatabaseProperties.getInstance();
	
	public static String readApplicationServerVersion() throws Exception{
		byte[] local_env = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(new File("local_env.xml"));
		AbstractXMLUtils xmlUtils = XMLUtils.getInstance();
		XPathExpressionEngine engine = new XPathExpressionEngine();
		Document local_env_document = xmlUtils.newDocument(local_env);
		String version_jbossas = engine.getStringMatchPattern(local_env_document, DynamicNamespaceContextFactory.getInstance().getNamespaceContext(local_env_document), "//property[@name='as']/@value");
		return version_jbossas;
	}
	
	
}


