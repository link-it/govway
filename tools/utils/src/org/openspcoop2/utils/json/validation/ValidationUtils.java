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

package org.openspcoop2.utils.json.validation;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * ValidationUtils
 *
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidationUtils {


	public static void logError(Logger log, String msg, byte[] rawObject, byte[] schemaBytes, JsonNode jsonSchema) {
		
		if(log==null) {
			return;
		}
		
		int max100KB = 1024*100;
		
		JSONUtils jsonUtils = JSONUtils.getInstance(true);
		
		StringBuilder sb = new StringBuilder();
		sb.append("Validation error: ").append(msg);
		if(rawObject!=null) {
			sb.append("\nMessage: ");
			if(rawObject.length<max100KB) {
				try {
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					jsonUtils.writeTo(jsonUtils.getAsNode(rawObject), bout);
					bout.flush();
					bout.close();
					sb.append("\n").append(bout.toString());
				}catch(Exception e) {
					sb.append("\n").append(new String(rawObject));
				}
			}
			else {
				sb.append("too large for debug ("+Utilities.convertBytesToFormatString(rawObject.length)+")");
			}
		}
		if(schemaBytes!=null) {
			sb.append("\nSchema: ");
			if(schemaBytes.length<max100KB) {
				try {
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					if(jsonSchema!=null) {
						jsonUtils.writeTo(jsonSchema, bout);
					}
					else {
						jsonUtils.writeTo(jsonUtils.getAsNode(schemaBytes), bout);
					}
					bout.flush();
					bout.close();
					sb.append("\n").append(bout.toString());
				}catch(Exception e) {
					sb.append("\n").append(new String(schemaBytes));
				}
			}
			else {
				sb.append("too large for debug ("+Utilities.convertBytesToFormatString(schemaBytes.length)+")");
			}
		}
		log.error(sb.toString());
	}
	
	public static void disableAdditionalProperties(ObjectMapper mapper, JsonNode jsonSchemaObject, boolean booleanType, boolean force) {
		JsonNode definitions = jsonSchemaObject.get("definitions");
		
		BooleanNode booleanFalseNode = null;
		BooleanNode booleanTrueNode = null;
		ObjectNode jsonStringTypeNode = null;
		if(booleanType) {
			booleanFalseNode = mapper.getNodeFactory().booleanNode(false);
			booleanTrueNode = mapper.getNodeFactory().booleanNode(true);
		}
		else {
			Map<String, String> map = new HashMap<>();
			map.put("type", "string");
			jsonStringTypeNode = mapper.getNodeFactory().objectNode();
			jsonStringTypeNode.set("type", mapper.getNodeFactory().textNode("string"));
		}
		
		/*
		 * ANDREBBE EFFETTUATA LA RISCRITTURA DEL JSON SCHEMA DOVE SONO PRESENTI I ALLOF RISOLVENDO I REF E GESTENDO NELL'OGGETTO L'ADDIOTIONAL PROPERTIES
		Iterator<String> fieldNames = definitions.fieldNames();
		HashMap<String, JsonNode> mapNode = new HashMap<>();
		while (fieldNames.hasNext()) {
			
			String fieldName = (String) fieldNames.next();
			JsonNode definition = definitions.get(fieldName);
			
			
		}
		*/
		
		Iterator<JsonNode> iterator = definitions.iterator();
		while(iterator.hasNext()) {
			ObjectNode definition = (ObjectNode) iterator.next();
						
			boolean allOf = definition.has("allOf");
			//boolean oneOf = definition.has("oneOf");
			boolean hasAdditionalProperties = definition.has("additionalProperties");
			
			//System.out.println("LOG ["+allOf+"]["+oneOf+"]["+hasAdditionalProperties+"] ["+definition.getClass().getName()+"]");
			
			//if(!allOf && !oneOf) {
			if(force || !hasAdditionalProperties) {
				definition.set("additionalProperties", booleanType ? booleanFalseNode : jsonStringTypeNode);
				
				if(allOf) {
					JsonNode allOfNode = definition.get("allOf");
					if(allOfNode!=null && allOfNode instanceof ArrayNode) {
						
						ArrayNode allOfArrayNode = (ArrayNode) allOfNode;
						for (int i = 0; i < allOfArrayNode.size(); i++) {
							JsonNode child = allOfArrayNode.get(i);
							//System.out.println("I["+i+"] ["+child.getClass().getName()+"]");
							if(child instanceof ObjectNode) {
								ObjectNode oChild = (ObjectNode) child;
								boolean ref = oChild.has("$ref");
								if(!ref) {
									boolean hasAdditionalPropertiesChild = oChild.has("additionalProperties");
									if(force || !hasAdditionalPropertiesChild) {
										oChild.set("additionalProperties", booleanTrueNode);
									}
								}
							}
						}
					}
				}
			}
			//}
		}
	}
	
	public static void addTypes(ObjectMapper mapper, JsonNode jsonSchemaObject, List<String> nomi, boolean all) {
		
		String allAny = all ? "allOf" : "anyOf";
		ArrayNode array = mapper.getNodeFactory().arrayNode();
		for(String nome: nomi) {
			array.add(mapper.getNodeFactory().objectNode().put("$ref", nome));
		}
		((ObjectNode)jsonSchemaObject).remove("allOf");
		((ObjectNode)jsonSchemaObject).remove("anyOf");
		((ObjectNode)jsonSchemaObject).set(allAny, array);
	}
	
}
