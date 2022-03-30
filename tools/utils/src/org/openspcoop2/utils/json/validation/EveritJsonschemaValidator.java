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

package org.openspcoop2.utils.json.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.json.IJsonSchemaValidator;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig;
import org.openspcoop2.utils.json.ValidationException;
import org.openspcoop2.utils.json.ValidationResponse;
import org.openspcoop2.utils.json.ValidationResponse.ESITO;
import org.slf4j.Logger;

import net.sf.json.JSONArray;

/**
 * EveritJsonschemaValidator
 *
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EveritJsonschemaValidator implements IJsonSchemaValidator {

	private Schema schema;
	private byte[] schemaBytes;
	private Logger log;
	private boolean logError;

	@Override
	public void setSchema(byte[] schema, JsonSchemaValidatorConfig config, Logger log) throws ValidationException {
		
		this.log = log;
		if(this.log==null) {
			this.log = LoggerWrapperFactory.getLogger(EveritJsonschemaValidator.class);
		}
		this.logError = config!=null ? config.isEmitLogError() : true;
		this.schemaBytes = schema;
		
		try {
			JSONObject jsonSchemaObject = new JSONObject(new String(schema));
			Map<String, String> map = new HashMap<>();
			map.put("type", "string");
			JSONObject jsonStringObject = new JSONObject(map);
			
			switch(config.getAdditionalProperties()) {
			case DEFAULT:
				break;
			case FORCE_DISABLE: addAdditionalProperties(jsonSchemaObject, Boolean.FALSE, true);
				break;
			case FORCE_STRING: addAdditionalProperties(jsonSchemaObject, jsonStringObject, true);
				break;
			case IF_NULL_DISABLE: addAdditionalProperties(jsonSchemaObject, Boolean.FALSE, false);
				break;
			case IF_NULL_STRING: addAdditionalProperties(jsonSchemaObject, jsonStringObject, false);
				break;
			default:
				break;
			}
			
			switch(config.getPoliticaInclusioneTipi()) {
			case DEFAULT:
				break;
			case ALL: addTypes(jsonSchemaObject, config.getTipi(), true);
				break;
			case ANY: addTypes(jsonSchemaObject, config.getTipi(), false);
				break;
			default:
				break;
			}
			
			this.schema = SchemaLoader.load(jsonSchemaObject);
			
			System.out.println(jsonSchemaObject);
			
		} catch(Exception e) {
			throw new ValidationException(e);
		}
	}

	private void addTypes(JSONObject jsonSchemaObject, List<String> nomi, boolean all) {
		
		String allAny = all ? "allOf" : "anyOf";
		JSONArray array = new JSONArray();
		for(String nome: nomi) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("$ref", nome);
			JSONObject obj = new JSONObject(map);
			array.element(obj);
		}

		jsonSchemaObject.remove("anyOf");
		jsonSchemaObject.remove("allOf");
		jsonSchemaObject.put(allAny, array);
	}

	private void addAdditionalProperties(JSONObject jsonSchemaObject, Object additionalPropertiesObject, boolean force) {
		JSONObject definitions = (JSONObject) jsonSchemaObject.get("definitions");
		
		for(String def: definitions.keySet()) {
			JSONObject definition = (JSONObject) definitions.get(def);
			if(force || !definition.has("additionalProperties")) {
				definition.put("additionalProperties", additionalPropertiesObject);
			}
		}
	}

	@Override
	public ValidationResponse validate(byte[] object) throws ValidationException {
		
		ValidationResponse response = new ValidationResponse();
		try {
			JSONObject jsonObject = null;
	        try {
	        	jsonObject = new JSONObject(new String(object));
	        }
	        catch(Exception e) {
	        	this.log.error(e.getMessage(),e);
	        	String messageString = "Read rawObject as jsonObject failed: "+e.getMessage();
	        	response.setEsito(ESITO.KO);
	        	if(this.logError) {
	        		ValidationUtils.logError(this.log, messageString.toString(), object, this.schemaBytes, null);
	        	}
				response.setException(new Exception(messageString.toString()));
	        }
			
	        if(jsonObject!=null) {
				try {
					this.schema.validate(jsonObject); // throws a ValidationException if this object is invalid
					response.setEsito(ESITO.OK);
				} catch(org.everit.json.schema.ValidationException e) {
					response.setEsito(ESITO.KO);
					response.setException(e);
					response.getErrors().add(e.getErrorMessage());
					if(this.logError) {
		        		ValidationUtils.logError(this.log, e.getErrorMessage(), object, this.schemaBytes, null);
					}
				}
	        }

		} catch(Exception e) {
			throw new ValidationException(e);
		}
		
		return response;
	}
	
}
