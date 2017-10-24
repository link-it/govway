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

import java.util.Iterator;

import org.openspcoop2.utils.json.IJsonSchemaValidator;
import org.openspcoop2.utils.json.ValidationException;
import org.openspcoop2.utils.json.ValidationResponse;
import org.openspcoop2.utils.json.ValidationResponse.ESITO;

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
	public void setSchema(byte[] schema) throws ValidationException {
		try {
			this.schema = this.jsonMapper.readTree(schema);
		} catch(Exception e) {
			throw new ValidationException(e);
		}
	}
	
}
