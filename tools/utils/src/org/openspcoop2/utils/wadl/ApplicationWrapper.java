/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.wadl;

import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import org.jvnet.ws.wadl.ast.ApplicationNode;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XSDSchemaCollection;
import org.openspcoop2.utils.xml.XSDUtils;

/**
 * ApplicationWrapper
 *
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApplicationWrapper {

	private ApplicationNode applicationNode;
	private Map<String, byte[]> resources = new HashMap<String, byte[]>();
	private Map<String, String> mappingNamespaceLocations = new HashMap<String, String>();
	private AbstractXMLUtils xmlUtils = null;
	private XSDUtils xsdUtils = null;
	
	public ApplicationWrapper(ApplicationNode applicationNode,
			Map<String, byte[]> resources, Map<String, String> mappingNamespaceLocations,
			AbstractXMLUtils xmlUtils){
		this.applicationNode = applicationNode;
		this.resources = resources;
		this.mappingNamespaceLocations = mappingNamespaceLocations;
		this.xmlUtils = xmlUtils;
		this.xsdUtils = new XSDUtils(this.xmlUtils);
	}
	
	public ApplicationNode getApplicationNode() {
		return this.applicationNode;
	}

	public Map<String, byte[]> getResources() {
		return this.resources;
	}

	public Map<String, String> getMappingNamespaceLocations() {
		return this.mappingNamespaceLocations;
	}
	
	public void addResource(String systemId, byte[] resource) throws XMLException{
		
		if(this.resources.containsKey(systemId)==false){
		
			// registro risorsa con systemid
			this.resources.put(systemId, resource);
	
			// registro namespace
			this.xsdUtils.registraMappingNamespaceLocations(resource, systemId, this.mappingNamespaceLocations);
			
		}
	}
	
	public XSDSchemaCollection buildSchemaCollection(Logger log) throws XMLException{
		return this.xsdUtils.buildSchemaCollection(this.resources, this.mappingNamespaceLocations, log);
	}

}
