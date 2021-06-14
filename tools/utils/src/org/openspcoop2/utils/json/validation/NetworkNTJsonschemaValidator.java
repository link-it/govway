/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import java.util.Set;

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
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.SpecVersionDetector;
import com.networknt.schema.ValidationMessage;

/**
 * NetworkNTJsonschemaValidator
 *
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NetworkNTJsonschemaValidator implements IJsonSchemaValidator {

	/*
	 * NOTA: in caso di errore 'should be valid to one and only one of the schemas'
	 * 
	 * Significa che più elementi possono matchare in un oneOf. Questo succede se ad esempio non è stato definito "additionalProperties: false" in ogni oggetto riferito dal oneOf
	 * 
	 * NOTA2: Aggiungendo le additionalProperties a false, se si aggiungono male e non si ha un match con nessun oggetto, viene ritornato l'errore relativo ad uno dei tre a caso.
	 * */
	
	private JsonSchema schema;
	private byte[] schemaBytes;
	private JsonNode jsonSchema;
	
	private ObjectMapper mapper;
	private Logger log;
	private boolean logError;

	/**
	 * 
	 */
	public NetworkNTJsonschemaValidator() {	
		this.mapper = new ObjectMapper();
	}
	@Override
	public void setSchema(byte[] schema, JsonSchemaValidatorConfig config, Logger log) throws ValidationException {
		
		this.log = log;
		if(this.log==null) {
			this.log = LoggerWrapperFactory.getLogger(NetworkNTJsonschemaValidator.class);
		}
		this.logError = config!=null ? config.isEmitLogError() : true;
		this.schemaBytes = schema;
		
		try {
			JsonNode jsonSchema =  this.mapper.readTree(schema);
	        
			SpecVersion.VersionFlag version	= null;
			if(config!=null && config.getJsonSchemaVersion()!=null) {
				version = config.getJsonSchemaVersion();
			}
			else {
				version = SpecVersionDetector.detect(jsonSchema);
			}
			
	        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(version);
			
			switch(config.getAdditionalProperties()) {
			case DEFAULT:
				break;
			case FORCE_DISABLE: ValidationUtils.disableAdditionalProperties(this.mapper, jsonSchema, true, true);
				break;
			case FORCE_STRING: ValidationUtils.disableAdditionalProperties(this.mapper, jsonSchema, false, true);
				break;
			case IF_NULL_DISABLE: ValidationUtils.disableAdditionalProperties(this.mapper, jsonSchema, true, false);
				break;
			case IF_NULL_STRING: ValidationUtils.disableAdditionalProperties(this.mapper, jsonSchema, false, false);
				break;
			default:
				break;
			}
	        
			switch(config.getPoliticaInclusioneTipi()) {
			case DEFAULT:
				break;
			case ALL: ValidationUtils.addTypes(this.mapper, jsonSchema, config.getTipi(), true);
				break;
			case ANY: ValidationUtils.addTypes(this.mapper, jsonSchema, config.getTipi(), false);
				break;
			default:
				break;
			}

			if(config.isVerbose()) {
				try {
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					JSONUtils.getInstance(true).writeTo(jsonSchema, bout);
					bout.flush();
					bout.close();
					this.log.debug("JSON Schema: "+bout.toString());
				}catch(Exception e) {
					this.log.debug("JSON Schema build error: "+e.getMessage(),e);
				}
			}
			
			this.jsonSchema = jsonSchema;
	        this.schema = factory.getSchema(jsonSchema);
	        
		} catch(Throwable e) {
			throw new ValidationException(e);
		}
	}
	



	@Override
	public ValidationResponse validate(byte[] rawObject) throws ValidationException {
		
		ValidationResponse response = new ValidationResponse();
		try {
			boolean expectedString = false;
			if(this.jsonSchema.has("type")) {
				try {
					JsonNode type = this.jsonSchema.get("type");
					String vType = type.asText();
					expectedString = "string".equals(vType);
				}catch(Exception e) {}
			}
			
	        JsonNode node = null;
	        try {
	        	if(expectedString) {
	        		node = this.mapper.getNodeFactory().textNode(new String(rawObject));
	        	}
	        	else {
	        		node = this.mapper.readTree(rawObject);
	        	}
	        }
	        catch(Exception e) {
	        	this.log.error(e.getMessage(),e);
	        	String messageString = "Read rawObject as jsonNode failed: "+e.getMessage();
	        	response.setEsito(ESITO.KO);
	        	if(this.logError) {
	        		ValidationUtils.logError(this.log, messageString.toString(), rawObject, this.schemaBytes, this.jsonSchema);
	        	}
				response.setException(new Exception(messageString.toString()));
	        }

	        if(node!=null) {
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
					if(this.logError) {
		        		ValidationUtils.logError(this.log, messageString.toString(), rawObject, this.schemaBytes, this.jsonSchema);
					}
					response.setException(new Exception(messageString.toString()));
	
				}
	        }
			
		} catch(Exception e) {
			throw new ValidationException(e);
		}
		return response;
	}
	
}
