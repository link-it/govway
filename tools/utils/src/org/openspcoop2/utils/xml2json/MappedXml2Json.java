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
/**
 * 
 */
package org.openspcoop2.utils.xml2json;

import java.util.Enumeration;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedXMLOutputFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Node;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class MappedXml2Json extends AbstractXml2Json {

	private AbstractXMLUtils xmlUtils;
	// private Map<String, String> jsonNamespaceMap;
	private MappedXMLOutputFactory mappedXMLOutputFactory;
	private Configuration configuration;
	public MappedXml2Json() {
		super();
		this.configuration = new Configuration();
		this.init();
	}
	public MappedXml2Json(Map<String, String> jsonNamespaceMap) {
		super();
		//this.jsonNamespaceMap = jsonNamespaceMap;
		this.configuration = new Configuration(jsonNamespaceMap);
		this.init();
	}
	public MappedXml2Json(Configuration configuration) {
		super();
		this.configuration = configuration;
		//this.jsonNamespaceMap = configuration.getXmlToJsonNamespaces();
		/*if(this.jsonNamespaceMap!=null && this.jsonNamespaceMap.isEmpty()) {
			this.jsonNamespaceMap = null;
		}*/
		this.init();
	}
	private void init() {
		this.xmlUtils = XMLUtils.getInstance();
		this.mappedXMLOutputFactory = new MappedXMLOutputFactory(this.configuration);			
	}
	
	public Configuration getConfiguration() {
		return this.configuration;
	}
	
	@Override
	protected XMLOutputFactory getOutputFactory() {
		return this.mappedXMLOutputFactory;
	}

	@Override
	public String xml2json(Node node) throws UtilsException {
		if(this.configuration.getXmlToJsonNamespaces() == null || this.configuration.getXmlToJsonNamespaces().isEmpty()) {
			DynamicNamespaceContext dnc = new DynamicNamespaceContext();
			dnc.findPrefixNamespace(node);
			this.refreshOutputFactory(dnc);
		}
		return super.xml2json(node);
	}
	@Override
	public String xml2json(String xmlString) throws UtilsException {
		try {
			return xml2json(this.xmlUtils.newElement(xmlString.getBytes()));
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	@SuppressWarnings("unchecked")
	private void refreshOutputFactory(DynamicNamespaceContext f) {
	/*	if(this.configuration.getXmlToJsonNamespaces() == null) {
			this.configuration.setXmlToJsonNamespaces(new java.util.HashMap<>());
		}*/
		Enumeration<?> prefixes = f.getPrefixes();
		while(prefixes.hasMoreElements()){
			Object nextElement = prefixes.nextElement();
			if(!nextElement.equals("xmlns"))
				this.configuration.getXmlToJsonNamespaces().put(f.getNamespaceURI((String)nextElement),(String) nextElement);
		}
		this.mappedXMLOutputFactory = new MappedXMLOutputFactory(this.configuration);		
	}
	

}
