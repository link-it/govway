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

package org.openspcoop2.utils.swagger.validator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.rest.AbstractApiValidator;
import org.openspcoop2.utils.rest.ApiValidatorConfig;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.ValidatorException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.entity.HttpBaseEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseRequestEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseResponseEntity;
import org.openspcoop2.utils.swagger.SwaggerApi;
import org.slf4j.Logger;

import com.atlassian.oai.validator.interaction.ApiOperationResolver;
import com.atlassian.oai.validator.interaction.RequestValidator;
import com.atlassian.oai.validator.interaction.ResponseValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.model.SimpleResponse;
import com.atlassian.oai.validator.report.MessageResolver;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.report.ValidationReport.Message;
import com.atlassian.oai.validator.schema.SchemaValidator;

/**
 * Validator
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13134 $, $Date: 2017-07-13 12:32:49 +0200(gio, 13 lug 2017) $
 */
public class Validator extends AbstractApiValidator implements IApiValidator {

	private SwaggerApi api;
	private SchemaValidator schemaValidator;

	@Override
	public void init(Logger log, Api api, ApiValidatorConfig config)
			throws ProcessingException {

		if(api == null)
			throw new ProcessingException("Api cannot be null");

		if(!(api instanceof SwaggerApi))
			throw new ProcessingException("Api class invalid. Expected ["+SwaggerApi.class.getName()+"] found ["+api.getClass().getName()+"]");

		this.api = (SwaggerApi) api;
		this.schemaValidator = new SchemaValidator(this.api.getSwagger(), new MessageResolver());
	}

	@Override
	public void validate(HttpBaseEntity<?> httpEntity)
			throws ProcessingException, ValidatorException {
		List<Object> args = new ArrayList<>();
		super.validate(this.api, httpEntity, args);
	}

	@Override
	public void validatePreConformanceCheck(HttpBaseEntity<?> httpEntity,
			ApiOperation operation, Object... args) throws ProcessingException,
	ValidatorException {

        ApiOperationResolver apiOperationResolver = new ApiOperationResolver(this.api.getSwagger(), this.api.getSwagger().getBasePath());
        com.atlassian.oai.validator.model.ApiOperation apiOperation = apiOperationResolver.findApiOperation(operation.getPath(), Request.Method.valueOf(operation.getHttpMethod().toString())).getApiOperation();

        
		if(httpEntity instanceof HttpBaseRequestEntity<?>) {
			SimpleRequest.Builder builder = new SimpleRequest.Builder(operation.getHttpMethod().toString(), operation.getPath());
			if(httpEntity.getContent() != null) {
				builder.withBody(httpEntity.getContent().toString()); //TODO vedere come recuperare la stringa
			}
			if(httpEntity.getParametersTrasporto() != null) {
				for(Object key: httpEntity.getParametersTrasporto().keySet()) {
					if(key instanceof String) {
						String keyS = (String)key;
						builder.withHeader(keyS, httpEntity.getParametersTrasporto().getProperty(keyS));
					}
				}
			}
			if(httpEntity.getParametersQuery() != null) {
				for(Object key: httpEntity.getParametersQuery().keySet()) {
					if(key instanceof String) {
						String keyS = (String)key;
						builder.withQueryParam(keyS, httpEntity.getParametersQuery().getProperty(keyS));
					}
				}
			}
			SimpleRequest request = builder.build();
			RequestValidator validator = new RequestValidator(this.schemaValidator, new MessageResolver(), this.api.getSwagger());
	        ValidationReport validateRequest = validator.validateRequest(request, apiOperation);
	        if(validateRequest.hasErrors()) {
	        	throw buildException(validateRequest.getMessages());
	        }
		} else if(httpEntity instanceof HttpBaseResponseEntity<?>) {
			SimpleResponse.Builder builder = new SimpleResponse.Builder(((HttpBaseResponseEntity<?>)httpEntity).getStatus());
			if(httpEntity.getContent() != null) {
				builder.withBody(httpEntity.getContent().toString()); //TODO vedere come recuperare la stringa
			}
			SimpleResponse request = builder.build();
			ResponseValidator validator = new ResponseValidator(this.schemaValidator, new MessageResolver(), this.api.getSwagger());
	        ValidationReport validateResponse = validator.validateResponse(request, apiOperation);
	        if(validateResponse.hasErrors()) {
	        	throw buildException(validateResponse.getMessages());
	        }
		} 

	}

	private ValidatorException buildException(List<Message> messages) {
		StringBuilder msg = new StringBuilder();
		if(messages!=null && !messages.isEmpty()) {
			for(Message message: messages) {
				msg.append(message.getKey()).append(": ").append(message.getMessage()).append("\n");
			}
		}
		return new ValidatorException(msg.toString());
	}

	@Override
	public void validatePostConformanceCheck(HttpBaseEntity<?> httpEntity,
			ApiOperation operation, Object... args) throws ProcessingException,
	ValidatorException {
		//NOP
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
