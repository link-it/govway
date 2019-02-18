/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.json;

import java.util.HashMap;
import java.util.Map;

/**
 * JsonValidatorAPI
 *
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JsonValidatorAPI {

	private String validatorClass;

	public String getValidatorClass() {
		return this.validatorClass;
	}

	public void setValidatorClass(String validatorClass) {
		this.validatorClass = validatorClass;
	}
	
	public enum ApiName {
		NETWORK_NT, 
		EVERIT, 
		FGE}
	private static Map<ApiName, JsonValidatorAPI> mapApi;
	
	public static JsonValidatorAPI get(ApiName name) {
		if(JsonValidatorAPI.mapApi == null) JsonValidatorAPI.initMapApi();
		return JsonValidatorAPI.mapApi.get(name);
	}

	private static void initMapApi() {
		JsonValidatorAPI.mapApi = new HashMap<ApiName, JsonValidatorAPI>();
		
		JsonValidatorAPI networkNtApi = new JsonValidatorAPI();
		networkNtApi.setValidatorClass("org.openspcoop2.utils.json.validation.NetworkNTJsonschemaValidator");
		JsonValidatorAPI.mapApi.put(ApiName.NETWORK_NT, networkNtApi);

		JsonValidatorAPI everitApi = new JsonValidatorAPI();
		everitApi.setValidatorClass("org.openspcoop2.utils.json.validation.EveritJsonschemaValidator");
		JsonValidatorAPI.mapApi.put(ApiName.EVERIT, everitApi);

		JsonValidatorAPI fgeApi = new JsonValidatorAPI();
		fgeApi.setValidatorClass("org.openspcoop2.utils.json.validation.FGEJsonschemaValidator");
		JsonValidatorAPI.mapApi.put(ApiName.FGE, fgeApi);

	}
}
