/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.utils.xml2json;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.AbstractUtils;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Json2XmlMappedConverter extends AbstractMappedConverter {

	private String rootElement_localName = "Root";
	private String rootElement_namespace = null;

	private List<String> attributes = new ArrayList<>();
	
	
	public Json2XmlMappedConverter() {
		this.forceReorder = true; // viene effettuato il riordinamento per ogni operazione di modifica dei nomi, attribute... e' possibile disabilitarlo se si usa il riordinamento manuale
	}
	
	
	// -- RootElement
	
	public void setRootElement(String rootElement) {
		this.rootElement_localName = rootElement;
	}
	public void setRootElement(String rootElement, String namespace) {
		this.rootElement_localName = rootElement;
		this.rootElement_namespace = namespace;
	}
	
	// -- Attributes
	
	public void addAttributeMapping(String path) throws UtilsException {
		this.attributes.add(path);
	}
	public void readAttributeMappingFromFile(String path) throws UtilsException {
		this.readAttributeMappingFromFile(new File(path));
	}
	public void readAttributeMappingFromFile(File path) throws UtilsException {
		if(path.exists()==false) {
			throw new UtilsException("File ["+path.getAbsolutePath()+"] not exists");
		}
		if(path.canRead()==false) {
			throw new UtilsException("File ["+path.getAbsolutePath()+"] cannot read");
		}
		byte [] r = null;
		try {
			r = FileSystemUtilities.readBytesFromFile(path);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		this.readAttributeMappingFromFile(r);
	}
	public void readAttributeMappingFromFile(byte[] resource) throws UtilsException {
		readFileConfig(resource, this.attributes);
	}	
	
	
	
	// -- Convert
	
	public String convert(String json) throws UtilsException {
		
		JSONUtils utils = JSONUtils.getInstance(false);
		JsonNode node = utils.getAsNode(json);
		
		return convert(node);
	}
	
	public String convert(JsonNode node) throws UtilsException {
				
		JSONUtils utils = JSONUtils.getInstance(false);
		
		List<String> attributes_renamed = this.attributes;
		List<String> arrays_renamed = this.arrays;
		List<String> reorderChildren_renamed = this.reorderChildren.keys();
		
		if(!this.renameFields.isEmpty()) {
			
			List<String> keys = this.renameFields.keys(); 
			Collections.sort(keys, Comparator.reverseOrder());
			for (String path : keys) {
				String newName = this.renameFields.get(path);
				
				//System.out.println("RENAME ["+path+"] in ["+newName+"]");
				
				boolean forceReorder = false;
				if(this.attributes.contains(path)) {
					//System.out.println("RENAME ATTRIBUTE ["+newName+"]");
					this.attributes.remove(path);
					newName = "@"+newName;
				}
				else {
					forceReorder = this.forceReorder;
				}

				//System.out.println("FORCE REORDER: "+forceReorder);
				utils.renameFieldByPath(node, path, newName, forceReorder, false); // non e' obbligatoria la presenza
			}
			
			// sistemo path negli attributi
			if(!attributes_renamed.isEmpty()) {
				attributes_renamed = correctPath("attributes",attributes_renamed);
			}
			
			// sistemo path nell'array
			if(!arrays_renamed.isEmpty()) {
				arrays_renamed = correctPath("arrays", arrays_renamed);
			}
			
			// sistemo path nelle istruzioni di reorder
			if(!reorderChildren_renamed.isEmpty()) {
				reorderChildren_renamed = correctPath("reorder", reorderChildren_renamed);
			}
			
		}
		
		if(!attributes_renamed.isEmpty()) {
			for (String path : attributes_renamed) {
				
				@SuppressWarnings("unused")
				String parentPath = getParentPath(path);
				String attrName = getLastNamePath(path);
				
				//System.out.println("ATTRIBUTE ["+path+"] ["+attrName+"]");
				if(this.camelCase) {
					attrName = AbstractUtils.camelCase(attrName, this.camelCase_firstLower);
				}
				utils.renameFieldByPath(node, path, "@"+attrName, false); // non e' obbligatoria la presenza
			}
		}
		
		if(!arrays_renamed.isEmpty()) {
			for (String path : arrays_renamed) {
				//System.out.println("ARRAY ["+path+"]");
				utils.convertFieldToArrayByPath(node, path, this.forceReorder, false); // non e' obbligatoria la presenza
			}
		}
		
		if(!reorderChildren_renamed.isEmpty()) {
			for (String path : reorderChildren_renamed) {
				String [] children = this.reorderChildren.get(path);
				//System.out.println("REORDER ["+path+"] ["+children.length+"] ["+java.util.Arrays.asList(children)+"]");
				utils.reorderFieldChildrenByPath(node, path, false, // non e' obbligatoria la presenza 
						children);
			}
		}
		
		// lasciare come ultimo visto che modifica i nomi
		if(this.camelCase) {
			boolean forceReorder = true; // per mantenere ordine, questo serve sempre
			utils.renameFieldInCamelCase(node, this.camelCase_firstLower, forceReorder);
		}
		
		// Fix: se il nodo json è formato da più elementi, viene convertito solo il primo. Aggiungo un root Element
		JsonNode rootNode = node;
		Map<String, String> map = new HashMap<>();
		if(this.rootElement_localName!=null) {
			rootNode = utils.newObjectNode();
			if(this.rootElement_namespace!=null) {
				((ObjectNode)rootNode).set("NS."+this.rootElement_localName, node);
				map.put(this.rootElement_namespace,"NS");
			}
			else {
				((ObjectNode)rootNode).set(this.rootElement_localName, node);
			}
		}
		String json = utils.toString(rootNode);
		
		//System.out.println("JSON: "+json);
		
		IJson2Xml json2xml = Xml2JsonFactory.getJson2XmlMapped(map);
		String xml = json2xml.json2xml(json);
				
		//System.out.println("XML: "+xml);
		
		if(this.prettyPrint) {
			try {
				Node n = XMLUtils.getInstance().newDocument(xml.getBytes());
				return org.openspcoop2.utils.xml.PrettyPrintXMLUtils.prettyPrintWithTrAX(n, true);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
		
		return xml;
	}
	
}
