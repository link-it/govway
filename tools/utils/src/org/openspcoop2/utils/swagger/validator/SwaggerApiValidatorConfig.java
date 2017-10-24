/**
 * 
 */
package org.openspcoop2.utils.swagger.validator;

import org.openspcoop2.utils.json.JsonValidatorAPI;
import org.openspcoop2.utils.rest.ApiValidatorConfig;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $ Rev: 12563 $, $Date$
 * 
 */
public class SwaggerApiValidatorConfig extends ApiValidatorConfig {

	private JsonValidatorAPI jsonValidatorAPI;

	public JsonValidatorAPI getJsonValidatorAPI() {
		return this.jsonValidatorAPI;
	}

	public void setJsonValidatorAPI(JsonValidatorAPI jsonValidatorAPI) {
		this.jsonValidatorAPI = jsonValidatorAPI;
	}
}
