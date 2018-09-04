/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspcoop2.utils.json.IJsonSchemaValidator;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig;
import org.openspcoop2.utils.json.ValidationException;
import org.openspcoop2.utils.json.ValidationResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;

import org.openspcoop2.utils.json.ValidationResponse.ESITO;

/**
 * NetworkNTJsonschemaValidator
 *
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NetworkNTJsonschemaValidator implements IJsonSchemaValidator {

	
	private JsonSchema schema;
	private ObjectMapper mapper;

	/**
	 * 
	 */
	public NetworkNTJsonschemaValidator() {
		this.mapper = new ObjectMapper();
	}
	@Override
	public void setSchema(byte[] schema, JsonSchemaValidatorConfig config) throws ValidationException {
		try {
	        JsonSchemaFactory factory = JsonSchemaFactory.getInstance();

	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode jsonSchema =  mapper.readTree(schema);
	        
	        Map<String, String> map = new HashMap<>();
			map.put("type", "string");
			ObjectNode jsonStringTypeNode = this.mapper.getNodeFactory().objectNode();
			jsonStringTypeNode.set("type", this.mapper.getNodeFactory().textNode("string"));
			
			BooleanNode booleanNode = this.mapper.getNodeFactory().booleanNode(false);
			switch(config.getAdditionalProperties()) {
			case DEFAULT:
				break;
			case FORCE_DISABLE: addAdditionalProperties(jsonSchema, booleanNode, true);
				break;
			case FORCE_STRING: addAdditionalProperties(jsonSchema, jsonStringTypeNode, true);
				break;
			case IF_NULL_DISABLE: addAdditionalProperties(jsonSchema, booleanNode, false);
				break;
			case IF_NULL_STRING: addAdditionalProperties(jsonSchema, jsonStringTypeNode, false);
				break;
			default:
				break;
			}
	        
			switch(config.getPoliticaInclusioneTipi()) {
			case DEFAULT:
				break;
			case ALL: addTypes(jsonSchema, config.getTipi(), true);
				break;
			case ANY: addTypes(jsonSchema, config.getTipi(), false);
				break;
			default:
				break;
			}

	        this.schema = factory.getSchema(jsonSchema);
	        
		} catch(Exception e) {
			throw new ValidationException(e);
		}
	}
	
	private void addTypes(JsonNode jsonSchemaObject, List<String> nomi, boolean all) {
		
		String allAny = all ? "allOf" : "anyOf";
		ArrayNode array = this.mapper.getNodeFactory().arrayNode();
		for(String nome: nomi) {
			array.add(this.mapper.getNodeFactory().objectNode().put("$ref", nome));
		}
		((ObjectNode)jsonSchemaObject).remove("allOf");
		((ObjectNode)jsonSchemaObject).remove("anyOf");
		((ObjectNode)jsonSchemaObject).set(allAny, array);
	}

	private void addAdditionalProperties(JsonNode jsonSchemaObject, JsonNode additionalPropertiesObject, boolean force) {
		JsonNode definitions = jsonSchemaObject.get("definitions");
		
		Iterator<JsonNode> iterator = definitions.iterator();
		while(iterator.hasNext()) {
			ObjectNode definition = (ObjectNode) iterator.next();
			if(force || !definition.has("additionalProperties")) {
				definition.set("additionalProperties", additionalPropertiesObject);
			}
		}
	}


	@Override
	public ValidationResponse validate(byte[] rawObject) throws ValidationException {
		
		ValidationResponse response = new ValidationResponse();
		try {
	        JsonNode node = this.mapper.readTree(rawObject);

			Set<ValidationMessage> validate = this.schema.validate(node);
			if(validate.isEmpty()) {
				response.setEsito(ESITO.OK);	
			} else {
				response.setEsito(ESITO.KO);
				StringBuilder messageString = new StringBuilder();
				for(ValidationMessage msg: validate) {
					String errorMsg = msg.getCode() + " " + msg.getMessage();
					response.getErrors().add(errorMsg);
					messageString.append(errorMsg).append("\n");
				}
				response.setException(new Exception(messageString.toString()));

			}
			
		} catch(Exception e) {
			throw new ValidationException(e);
		}
		return response;
	}
	
}
