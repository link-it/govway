package org.openspcoop2.utils.json;


public interface IJsonSchemaValidator {

	public void setSchema(byte[] schema) throws ValidationException ;
	public ValidationResponse validate(byte[] object) throws ValidationException;
}
