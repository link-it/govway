/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

import org.openspcoop2.utils.json.IJsonSchemaValidator;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig;
import org.openspcoop2.utils.json.ValidationException;
import org.openspcoop2.utils.json.ValidationResponse;
import org.openspcoop2.utils.json.ValidationResponse.ESITO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;

/**
 * FGEJsonschemaValidator
 *
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FGEJsonschemaValidator implements IJsonSchemaValidator {

	private JsonValidator validator;
	private JsonNode schema;
	private ObjectMapper jsonMapper;
	
	public FGEJsonschemaValidator() {
		this.validator = JsonSchemaFactory.byDefault().getValidator();
		this.jsonMapper = new ObjectMapper();
		
	}

	@Override
	public ValidationResponse validate(byte[] rawObject) throws ValidationException {

		ValidationResponse response = new ValidationResponse();
		try {
			JsonNode object = this.jsonMapper.readTree(rawObject);
			ProcessingReport report = this.validator.validate(this.schema, object, true);
			
			if(report.isSuccess()) {
				response.setEsito(ESITO.OK);
			} else {
				response.setEsito(ESITO.KO);
				Iterator<ProcessingMessage> iterator = report.iterator();
				while(iterator.hasNext()) {
					
					ProcessingMessage msg = iterator.next();
					StringBuilder messageString = new StringBuilder();
					if(msg.getLogLevel().equals(LogLevel.ERROR) || msg.getLogLevel().equals(LogLevel.FATAL)) {
						response.getErrors().add(msg.getMessage());
						messageString.append(msg.getMessage()).append("\n");
					}
					response.setException(new Exception(messageString.toString()));
				}
			}
		} catch(Exception e) {
			throw new ValidationException(e);
		}
		
		return response;
	}

	@Override
	public void setSchema(byte[] schema, JsonSchemaValidatorConfig config) throws ValidationException {
		try {
			this.schema = this.jsonMapper.readTree(schema);

			Map<String, String> map = new HashMap<>();
			map.put("type", "string");
			ObjectNode jsonStringTypeNode = this.jsonMapper.getNodeFactory().objectNode();
			jsonStringTypeNode.set("type", this.jsonMapper.getNodeFactory().textNode("string"));
			
			BooleanNode booleanNode = this.jsonMapper.getNodeFactory().booleanNode(false);
			switch(config.getAdditionalProperties()) {
			case DEFAULT:
				break;
			case FORCE_DISABLE: addAdditionalProperties(this.schema, booleanNode, true);
				break;
			case FORCE_STRING: addAdditionalProperties(this.schema, jsonStringTypeNode, true);
				break;
			case IF_NULL_DISABLE: addAdditionalProperties(this.schema, booleanNode, false);
				break;
			case IF_NULL_STRING: addAdditionalProperties(this.schema, jsonStringTypeNode, false);
				break;
			default:
				break;
			}
			
			switch(config.getPoliticaInclusioneTipi()) {
			case DEFAULT:
				break;
			case ALL: addTypes(this.schema, config.getTipi(), true);
				break;
			case ANY: addTypes(this.schema, config.getTipi(), false);
				break;
			default:
				break;
			}

		} catch(Exception e) {
			throw new ValidationException(e);
		}
	}
	
	private void addTypes(JsonNode jsonSchemaObject, List<String> nomi, boolean all) {
		
		String allAny = all ? "allOf" : "anyOf";
		ArrayNode array = this.jsonMapper.getNodeFactory().arrayNode();
		for(String nome: nomi) {
			array.add(this.jsonMapper.getNodeFactory().objectNode().put("$ref", nome));
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

}
