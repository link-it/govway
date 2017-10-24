package org.openspcoop2.utils.json;

import org.openspcoop2.utils.json.JsonValidatorAPI.ApiName;

public class ValidatorFactory {


	public static IJsonSchemaValidator newJsonSchemaValidator(ApiName apiName) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return (IJsonSchemaValidator) Class.forName(JsonValidatorAPI.get(apiName).getValidatorClass()).newInstance();
	}

}
