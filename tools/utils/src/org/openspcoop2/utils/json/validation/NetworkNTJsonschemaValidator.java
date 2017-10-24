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

import java.util.Set;

import org.openspcoop2.utils.json.IJsonSchemaValidator;
import org.openspcoop2.utils.json.ValidationException;
import org.openspcoop2.utils.json.ValidationResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

	@Override
	public void setSchema(byte[] schema) throws ValidationException {
		try {
	        JsonSchemaFactory factory = new JsonSchemaFactory();
	        this.schema = factory.getSchema(new String(schema));
		} catch(Exception e) {
			throw new ValidationException(e);
		}
	}

	@Override
	public ValidationResponse validate(byte[] rawObject) throws ValidationException {
		
		ValidationResponse response = new ValidationResponse();
		try {
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode node = mapper.readTree(rawObject);

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
