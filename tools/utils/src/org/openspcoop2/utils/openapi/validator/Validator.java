/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

package org.openspcoop2.utils.openapi.validator;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.json.IJsonSchemaValidator;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig.ADDITIONAL;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig.POLITICA_INCLUSIONE_TIPI;
import org.openspcoop2.utils.json.JsonValidatorAPI.ApiName;
import org.openspcoop2.utils.json.ValidationException;
import org.openspcoop2.utils.json.ValidationResponse;
import org.openspcoop2.utils.json.ValidationResponse.ESITO;
import org.openspcoop2.utils.json.ValidatorFactory;
import org.openspcoop2.utils.openapi.OpenapiApi;
import org.openspcoop2.utils.rest.AbstractApiValidator;
import org.openspcoop2.utils.rest.ApiValidatorConfig;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.ValidatorException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiBodyParameter;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.entity.HttpBaseEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseRequestEntity;
import org.slf4j.Logger;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.core.util.Json;

/**
 * Validator
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Validator extends AbstractApiValidator implements IApiValidator {

	private OpenapiApi api;
	private Map<String, IJsonSchemaValidator> validatorMap;
	
	@Override
	public void init(Logger log, Api api, ApiValidatorConfig config)
			throws ProcessingException {

		if(api == null)
			throw new ProcessingException("Api cannot be null");

		if(!(api instanceof OpenapiApi))
			throw new ProcessingException("Api class invalid. Expected ["+OpenapiApi.class.getName()+"] found ["+api.getClass().getName()+"]");

		this.api = (OpenapiApi) api;
		ApiName jsonValidatorAPI;
		if(config instanceof OpenapiApiValidatorConfig) {
			jsonValidatorAPI = ((OpenapiApiValidatorConfig)config).getJsonValidatorAPI();
		} else {
			jsonValidatorAPI = ApiName.NETWORK_NT;
		}
		
		try {
			
			Map<String, Schema<?>> definitions = this.api.getAllDefinitions();
			
			this.validatorMap = new HashMap<>();
			
			String definitionString = Json.mapper().writeValueAsString(definitions);
			for(String schemaName: definitions.keySet()) {
				byte[] schema = ("{\"definitions\" : "+definitionString+"}").getBytes();
				
				IJsonSchemaValidator validator = ValidatorFactory.newJsonSchemaValidator(jsonValidatorAPI);
				
				JsonSchemaValidatorConfig schemaValidationConfig = new JsonSchemaValidatorConfig();
				schemaValidationConfig.setAdditionalProperties(ADDITIONAL.FORCE_DISABLE);
				schemaValidationConfig.setPoliticaInclusioneTipi(POLITICA_INCLUSIONE_TIPI.ANY);
				schemaValidationConfig.setTipi(Arrays.asList("#/definitions/"+schemaName));
				validator.setSchema(schema, schemaValidationConfig);
				
				this.validatorMap.put(schemaName, validator);

			}
					
		} catch(Exception e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	public void validate(HttpBaseEntity<?> httpEntity)
			throws ProcessingException, ValidatorException {
		List<Object> args = new ArrayList<>();
		super.validate(this.api, httpEntity, args);
	}

	@Override
	public void validatePreConformanceCheck(HttpBaseEntity<?> httpEntity,
			ApiOperation operation, Object... args) throws ProcessingException, ValidatorException {

		boolean required = false;
		if(httpEntity instanceof HttpBaseRequestEntity) {
			List<ApiBodyParameter> bodyParameters = operation.getRequest().getBodyParameters();

			for(ApiBodyParameter body: bodyParameters) {
				if(body.isRequired())
					required = true;
			}
			
			if(required) {
				
				if(httpEntity.getContent() == null) {
					throw new ValidatorException("Body required ma non trovato");
				}
				
				try {
					List<IJsonSchemaValidator> validatorLst = getValidatorList(operation, httpEntity);
					boolean valid = false;
					Exception exc = null;
					byte[] bytes = httpEntity.getContent().toString().getBytes();
					for(IJsonSchemaValidator validator: validatorLst) {
						ValidationResponse response = validator.validate(bytes);
						if(!ESITO.OK.equals(response.getEsito())) {
							exc = response.getException();
						} else {
							valid = true;
						}
					}
					
					if(!valid) {
						throw new ValidatorException(exc);
					}
				} catch (ValidationException e) {
					throw new ValidatorException(e);
				}
			}

		}
		
	}

	/**
	 * @param operation
	 * @return
	 */
	private List<IJsonSchemaValidator> getValidatorList(ApiOperation operation, HttpBaseEntity<?> httpEntity) throws ValidatorException {
		List<IJsonSchemaValidator> lst = new ArrayList<>();
		
		if(httpEntity instanceof HttpBaseRequestEntity) {
			for(ApiBodyParameter body: operation.getRequest().getBodyParameters()) {
				if(this.validatorMap.containsKey(body.getElement())) {
					lst.add(this.validatorMap.get(body.getElement()));
				}
			}
		}
		
		if(lst.isEmpty())
			throw new ValidatorException("Nessun validatore trovato");
		
		return lst;
	}

	@Override
	public void validatePostConformanceCheck(HttpBaseEntity<?> httpEntity,
			ApiOperation operation, Object... args) throws ProcessingException,
	ValidatorException {
		
	}

	@Override
	public void validateValueAsType(String value, String type)
			throws ProcessingException, ValidatorException {

		if(type!=null){
			type = type.trim();

			if("byte".equalsIgnoreCase(type) || "unsignedByte".equalsIgnoreCase(type)){
				try{
					Byte.parseByte(value);
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("char".equalsIgnoreCase(type)){
				if(value.length()>1){
					throw new ValidatorException("More than one character");
				}
			}
			else if("double".equalsIgnoreCase(type) || "decimal".equalsIgnoreCase(type)){
				try{
					Double.parseDouble(value);
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("float".equalsIgnoreCase(type)){
				try{
					Float.parseFloat(value);
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("int".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type) || 
					"positiveInteger".equalsIgnoreCase(type) || "negativeInteger".equalsIgnoreCase(type) ||
					"nonPositiveInteger".equalsIgnoreCase(type) || "nonNegativeInteger".equalsIgnoreCase(type) || 
					"unsignedInt".equalsIgnoreCase(type)){
				try{
					int i = Integer.parseInt(value);
					if("positiveInteger".equalsIgnoreCase(type)){
						if(i<=0){
							throw new ValidatorException("Expected a positive value");
						}
					}
					else if("nonNegativeInteger".equalsIgnoreCase(type)){
						if(i<0){
							throw new ValidatorException("Expected a non negative value");
						}
					}
					else if("negativeInteger".equalsIgnoreCase(type)){
						if(i>=0){
							throw new ValidatorException("Expected a negative value");
						}
					}
					else if("nonPositiveInteger".equalsIgnoreCase(type)){
						if(i>0){
							throw new ValidatorException("Expected a non positive value");
						}
					}
					else if("unsignedInt".equalsIgnoreCase(type)){
						if(i<0){
							throw new ValidatorException("Expected a unsigned value");
						}
					}
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("long".equalsIgnoreCase(type) || "unsignedLong".equalsIgnoreCase(type)){
				try{
					long l = Long.parseLong(value);
					if("unsignedLong".equalsIgnoreCase(type)){
						if(l<0){
							throw new ValidatorException("Expected a unsigned value");
						}
					}
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("short".equalsIgnoreCase(type) || "unsignedShort".equalsIgnoreCase(type)){
				try{
					short s = Short.parseShort(value);
					if("unsignedShort".equalsIgnoreCase(type)){
						if(s<0){
							throw new ValidatorException("Expected a unsigned value");
						}
					}
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("boolean".equalsIgnoreCase(type)){
				try{
					Boolean.parseBoolean(value);
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("anyURI".equalsIgnoreCase(type)){
				try{
					new URI(value);
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
		}

		// altri tipi non li valido per ora


	}

}
