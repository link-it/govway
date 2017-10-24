package org.openspcoop2.utils.json;

import java.util.HashMap;
import java.util.Map;

public class JsonValidatorAPI {

	private String validatorClass;

	public String getValidatorClass() {
		return this.validatorClass;
	}

	public void setValidatorClass(String validatorClass) {
		this.validatorClass = validatorClass;
	}
	
	public enum ApiName {NETWORK_NT, EVERIT, FGE}
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
