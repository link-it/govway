package org.openspcoop2.utils.json.validation;

import javax.validation.ValidationException;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.openspcoop2.utils.json.IJsonSchemaValidator;
import org.openspcoop2.utils.json.ValidationResponse;

import org.openspcoop2.utils.json.ValidationResponse.ESITO;

public class EveritJsonschemaValidator implements IJsonSchemaValidator {

	private Schema schema;

	@Override
	public void setSchema(byte[] schema) throws ValidationException {
		try {
			this.schema = SchemaLoader.load(new JSONObject(new String(schema)));
		} catch(Exception e) {
			throw new ValidationException(e);
		}
	}

	@Override
	public ValidationResponse validate(byte[] object) throws ValidationException {
		
		ValidationResponse response = new ValidationResponse();
		try {
			try {
				this.schema.validate(new JSONObject(new String(object))); // throws a ValidationException if this object is invalid
				response.setEsito(ESITO.OK);
			} catch(org.everit.json.schema.ValidationException e) {
				response.setEsito(ESITO.KO);
				response.setException(e);
				response.getErrors().add(e.getErrorMessage());
			}

		} catch(Exception e) {
			throw new ValidationException(e);
		}
		
		return response;
	}
	
}
