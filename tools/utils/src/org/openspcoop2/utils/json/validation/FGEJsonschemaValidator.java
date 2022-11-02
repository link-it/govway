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

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.json.IJsonSchemaValidator;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig;
import org.openspcoop2.utils.json.ValidationException;
import org.openspcoop2.utils.json.ValidationResponse;
import org.openspcoop2.utils.json.ValidationResponse.ESITO;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	private byte[] schemaBytes;
	
	private ObjectMapper jsonMapper;

	private Logger log;
	private boolean logError;
	
	public FGEJsonschemaValidator() {
		this.validator = JsonSchemaFactory.byDefault().getValidator();
		this.jsonMapper = new ObjectMapper();
		
	}

	@Override
	public void setSchema(byte[] schema, JsonSchemaValidatorConfig config, Logger log) throws ValidationException {
		
		this.log = log;
		if(this.log==null) {
			this.log = LoggerWrapperFactory.getLogger(FGEJsonschemaValidator.class);
		}
		this.logError = config!=null ? config.isEmitLogError() : true;
		this.schemaBytes = schema;
		
		try {
			this.schema = this.jsonMapper.readTree(schema);

			if(config!=null) {
				switch(config.getAdditionalProperties()) {
				case DEFAULT:
					break;
				case FORCE_DISABLE: ValidationUtils.disableAdditionalProperties(this.jsonMapper, this.schema, true, true);
					break;
				case FORCE_STRING: ValidationUtils.disableAdditionalProperties(this.jsonMapper, this.schema, false, true);
					break;
				case IF_NULL_DISABLE: ValidationUtils.disableAdditionalProperties(this.jsonMapper, this.schema, true, false);
					break;
				case IF_NULL_STRING: ValidationUtils.disableAdditionalProperties(this.jsonMapper, this.schema, false, false);
					break;
				default:
					break;
				}
			}
			
			if(config!=null) {
				switch(config.getPoliticaInclusioneTipi()) {
				case DEFAULT:
					break;
				case ALL: ValidationUtils.addTypes(this.jsonMapper, this.schema, config.getTipi(), true);
					break;
				case ANY: ValidationUtils.addTypes(this.jsonMapper, this.schema, config.getTipi(), false);
					break;
				default:
					break;
				}
			}
			
			if(config!=null && config.isVerbose()) {
				try {
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					JSONUtils.getInstance(true).writeTo(this.schema, bout);
					bout.flush();
					bout.close();
					this.log.debug("JSON Schema: "+bout.toString());
				}catch(Exception e) {
					this.log.debug("JSON Schema build error: "+e.getMessage(),e);
				}
			}
			

		} catch(Exception e) {
			throw new ValidationException(e);
		}
	}

	@Override
	public ValidationResponse validate(byte[] rawObject) throws ValidationException {

		ValidationResponse response = new ValidationResponse();
		try {
			boolean expectedString = false;
			if(this.schema.has("type")) {
				try {
					JsonNode type = this.schema.get("type");
					String vType = type.asText();
					expectedString = "string".equals(vType);
				}catch(Exception e) {}
			}
			
	        JsonNode object = null;
	        try {
	        	if(expectedString) {
	        		object = this.jsonMapper.getNodeFactory().textNode(new String(rawObject));
	        	}
	        	else {
	        		object = this.jsonMapper.readTree(rawObject);
	        	}
	        }
	        catch(Exception e) {
	        	this.log.error(e.getMessage(),e);
	        	String messageString = "Read rawObject as jsonNode failed: "+e.getMessage();
	        	response.setEsito(ESITO.KO);
	        	if(this.logError) {
	        		ValidationUtils.logError(this.log, messageString.toString(), rawObject, this.schemaBytes, this.schema);
	        	}
				response.setException(new Exception(messageString.toString()));
	        }
	        
	        if(object!=null) {
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
						if(this.logError) {
			        		ValidationUtils.logError(this.log, messageString.toString(), rawObject, this.schemaBytes, this.schema);
						}
						response.setException(new Exception(messageString.toString()));
					}
				}
	        }
		} catch(Exception e) {
			throw new ValidationException(e);
		}
		
		return response;
	}
}
