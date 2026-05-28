/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.openapi.validator.json_schema;

import java.util.function.UnaryOperator;

import org.openspcoop2.utils.json.JsonValidatorAPI.ApiName;
import org.openspcoop2.utils.openapi.validator.OpenapiLibraryValidatorConfig;
import org.openspcoop2.utils.rest.ApiValidatorConfig;

/**
 * JSON-Schema fallback validator config (NetworkNT / FGE).
 *
 * Usata come fallback quando l'API non è parsata come OpenAPI 3 o Swagger 2,
 * oppure quando openapi4j / swagger-request-validator non sono abilitati.
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidatorConfig extends ApiValidatorConfig {

	private static final long serialVersionUID = 1L;

	private ApiName jsonValidatorAPI;

	public ValidatorConfig() {
		// default
	}

	@Override
	public void readProperties(UnaryOperator<String> propertyProvider) {
		String json = propertyProvider.apply(OpenapiLibraryValidatorConfig.PROPERTY_KEY_JSON_VALIDATOR_API);
		if (json != null) {
			setJsonValidatorAPI(ApiName.valueOf(json));
		}
	}

	public ApiName getJsonValidatorAPI() {
		return this.jsonValidatorAPI;
	}

	public void setJsonValidatorAPI(ApiName jsonValidatorAPI) {
		if (jsonValidatorAPI != null) {
			setProperty(OpenapiLibraryValidatorConfig.PROPERTY_KEY_JSON_VALIDATOR_API, jsonValidatorAPI.name());
		}
		this.jsonValidatorAPI = jsonValidatorAPI;
	}
}
