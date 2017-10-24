package org.openspcoop2.utils.json;

import javax.validation.ValidationException;

public interface IJsonSchemaValidator {

	public void setSchema(byte[] schema) throws ValidationException ;
	public ValidationResponse validate(byte[] object) throws ValidationException;
}
