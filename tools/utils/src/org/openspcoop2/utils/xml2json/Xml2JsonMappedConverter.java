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
package org.openspcoop2.utils.xml2json;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;
import org.w3c.dom.Node;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Xml2JsonMappedConverter
 * 
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Xml2JsonMappedConverter extends AbstractMappedConverter{ 

	private boolean ignoreNamespaces = true;
	private boolean attributeAsElement = true;
	private boolean dropRootElement = true;
	private boolean convertAllValuesAsString = false;
		
	public void setIgnoreNamespaces(boolean ignoreNamespaces) {
		this.ignoreNamespaces = ignoreNamespaces;
	}
	public void setAttributeAsElement(boolean attributeAsElement) {
		this.attributeAsElement = attributeAsElement;
	}
	public void setDropRootElement(boolean dropRootElement) {
		this.dropRootElement = dropRootElement;
	}
	public void setConvertAllValuesAsString(boolean convertAllValuesAsString) {
		this.convertAllValuesAsString = convertAllValuesAsString;
	}
	
	// -- Convert
	
	public String convert(Node xml) throws UtilsException {
		String json = getXml2Json().xml2json(xml);
		return _convert(json);
	}
	
	public String convert(String xml) throws UtilsException {
		String json = getXml2Json().xml2json(xml);
		return _convert(json);
	}
	
	private IXml2Json getXml2Json(){
		IXml2Json xml2json = Xml2JsonFactory.getXml2JsonMapped();
		((MappedXml2Json)xml2json).getConfiguration().setIgnoreNamespaces(this.ignoreNamespaces);
		((MappedXml2Json)xml2json).getConfiguration().setSupressAtAttributes(this.attributeAsElement);
		((MappedXml2Json)xml2json).getConfiguration().setDropRootElement(this.dropRootElement);
		if(this.convertAllValuesAsString) {
			((MappedXml2Json)xml2json).getConfiguration().setTypeConverter(new org.codehaus.jettison.mapped.SimpleConverter());
		}
		return xml2json;
	}
	
	private String _convert(String json) throws UtilsException {
		
		JSONUtils utils = JSONUtils.getInstance(this.prettyPrint);
		
		JsonNode node = utils.getAsNode(json);
		
		List<String> arrays_renamed = this.arrays;
		List<String> reorderChildren_renamed = this.reorderChildren.keys();
		
		if(!this.renameFields.isEmpty()) {
			
			List<String> keys = this.renameFields.keys(); 
			Collections.sort(keys, Comparator.reverseOrder());
			for (String path : keys) {
				String newName = this.renameFields.get(path);
				
				//System.out.println("RENAME ["+path+"] in ["+newName+"]");
				
				boolean forceReorder = this.forceReorder;

				//System.out.println("FORCE REORDER: "+forceReorder);
				utils.renameFieldByPath(node, path, newName, forceReorder, false); // non e' obbligatoria la presenza
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
			boolean forceReorder = this.forceReorder;
			utils.renameFieldInCamelCase(node, this.camelCase_firstLower, forceReorder);
		}
		
		return utils.toString(node);
		
	}
}
