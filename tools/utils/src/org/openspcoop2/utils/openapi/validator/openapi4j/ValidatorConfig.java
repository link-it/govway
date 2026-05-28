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
package org.openspcoop2.utils.openapi.validator.openapi4j;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import org.openspcoop2.utils.json.JsonValidatorAPI.ApiName;
import org.openspcoop2.utils.openapi.validator.OpenapiLibraryValidatorConfig;
import org.openspcoop2.utils.rest.ApiValidatorConfig;

/**
 * SwaggerApiValidatorConfig
 * 
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ValidatorConfig extends ApiValidatorConfig {

	private static final long serialVersionUID = 1L;
	
	private ApiName jsonValidatorAPI;
	private boolean mergeAPISpec = false;
	
	private boolean validateAPISpec = true;
	
	private boolean validateRequestPath = true;
	private boolean validateRequestQuery = true;
	/* utilizzato solo in swagger_request_validator per disabilitare il controllo.
	 * Da specifica i parametri ulteriori vanno ignorati, mentre la libreria swagger_request_validator li valida
	 * Per default quindi questa opzione viene disabilitata
	 */
	private boolean validateRequestUnexpectedQueryParam = false;
	private boolean validateRequestHeaders = true;
	private boolean validateRequestCookie = true;
	private boolean validateRequestBody = true;
	
	private boolean validateResponseHeaders = true;
	private boolean validateResponseBody = true;
	
	// Dice se trattare il subtype '*' come json e quindi validare lo schema del body passato
	private boolean validateWildcardSubtypeAsJson = true;
	
	// La validazione delle richieste multipart/form-data (o mixed) prevede per default il processamento di tutto lo stream.
	// Poichè le parti "binarie" non richiedono una validazione rispetto ad uno schema e sono tipicamente serializzate dopo i metadati (plain o json) 
	// potrebbero essere "saltate" terminando l'analisi dello stream dopo aver validato i metadati in modo da avere benefici prestazionali visto che tipicamente le parti binarie rappresentano 
	// la maggior dimensione del messaggio in termini di bytes.
	// L'ottimizzazione sopra indicata non consente però di verificare se esistono part non definite nella specifica da segnalare come non validi quando viene definito l'additionalProperties=false.
	// Per il motivo precedentemente indicato l'ottimizzazione non è abilitata per default.
	// NOTA: feature supportata solamente dalla libreria openapi4j.
	private boolean validateMultipartOptimization = false;

	public ValidatorConfig() {
		// default
	}

	/**
	 * Legge i flag da un property provider.
	 * Le chiavi sono quelle definite in {@link OpenapiLibraryValidatorConfig} (suffisso senza prefisso).
	 * Se il provider restituisce null per una chiave, il default del campo viene mantenuto.
	 */
	@Override
	public void readProperties(UnaryOperator<String> propertyProvider) {
		String json = propertyProvider.apply(OpenapiLibraryValidatorConfig.PROPERTY_KEY_JSON_VALIDATOR_API);
		if (json != null) {
			setJsonValidatorAPI(ApiName.valueOf(json));
		}
		applyBoolean(propertyProvider, OpenapiLibraryValidatorConfig.PROPERTY_KEY_MERGE_API_SPEC, this::setMergeAPISpec);
		applyBoolean(propertyProvider, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_API_SPEC, this::setValidateAPISpec);
		applyBoolean(propertyProvider, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_PATH, this::setValidateRequestPath);
		applyBoolean(propertyProvider, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_QUERY, this::setValidateRequestQuery);
		applyBoolean(propertyProvider, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM, this::setValidateRequestUnexpectedQueryParam);
		applyBoolean(propertyProvider, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_HEADERS, this::setValidateRequestHeaders);
		applyBoolean(propertyProvider, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_COOKIES, this::setValidateRequestCookie);
		applyBoolean(propertyProvider, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_BODY, this::setValidateRequestBody);
		applyBoolean(propertyProvider, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_RESPONSE_HEADERS, this::setValidateResponseHeaders);
		applyBoolean(propertyProvider, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_RESPONSE_BODY, this::setValidateResponseBody);
		applyBoolean(propertyProvider, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_WILDCARD_SUBTYPE_AS_JSON, this::setValidateWildcardSubtypeAsJson);
		applyBoolean(propertyProvider, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_MULTIPART_OPTIMIZATION, this::setValidateMultipartOptimization);
	}

	private static void applyBoolean(UnaryOperator<String> provider, String key, Consumer<Boolean> setter) {
		String value = provider.apply(key);
		if (value != null) {
			setter.accept(Boolean.parseBoolean(value));
		}
	}

	public boolean isMergeAPISpec() {
		return this.mergeAPISpec;
	}

	public void setMergeAPISpec(boolean mergeAPISpec) {
		setProperty(OpenapiLibraryValidatorConfig.PROPERTY_KEY_MERGE_API_SPEC, String.valueOf(mergeAPISpec));
		this.mergeAPISpec = mergeAPISpec;
	}

	public boolean isValidateAPISpec() {
		return this.validateAPISpec;
	}

	public void setValidateAPISpec(boolean validateAPISpec) {
		setProperty(OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_API_SPEC, String.valueOf(validateAPISpec));
		this.validateAPISpec = validateAPISpec;
	}

	public boolean isValidateRequestPath() {
		return this.validateRequestPath;
	}

	public void setValidateRequestPath(boolean validateRequestPath) {
		setProperty(OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_PATH, String.valueOf(validateRequestPath));
		this.validateRequestPath = validateRequestPath;
	}

	public boolean isValidateRequestQuery() {
		return this.validateRequestQuery;
	}

	public void setValidateRequestQuery(boolean validateRequestQuery) {
		setProperty(OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_QUERY, String.valueOf(validateRequestQuery));
		this.validateRequestQuery = validateRequestQuery;
	}

	public boolean isValidateRequestHeaders() {
		return this.validateRequestHeaders;
	}

	public void setValidateRequestHeaders(boolean validateRequestHeaders) {
		setProperty(OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_HEADERS, String.valueOf(validateRequestHeaders));
		this.validateRequestHeaders = validateRequestHeaders;
	}

	public boolean isValidateRequestCookie() {
		return this.validateRequestCookie;
	}

	public void setValidateRequestCookie(boolean validateRequestCookie) {
		setProperty(OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_COOKIES, String.valueOf(validateRequestCookie));
		this.validateRequestCookie = validateRequestCookie;
	}

	public boolean isValidateRequestBody() {
		return this.validateRequestBody;
	}

	public void setValidateRequestBody(boolean validateRequestBody) {
		setProperty(OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_BODY, String.valueOf(validateRequestBody));
		this.validateRequestBody = validateRequestBody;
	}

	public boolean isValidateResponseHeaders() {
		return this.validateResponseHeaders;
	}

	public void setValidateResponseHeaders(boolean validateResponseHeaders) {
		setProperty(OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_RESPONSE_HEADERS, String.valueOf(validateResponseHeaders));
		this.validateResponseHeaders = validateResponseHeaders;
	}

	public boolean isValidateResponseBody() {
		return this.validateResponseBody;
	}

	public void setValidateResponseBody(boolean validateResponseBody) {
		setProperty(OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_RESPONSE_BODY, String.valueOf(validateResponseBody));
		this.validateResponseBody = validateResponseBody;
	}

	public boolean isValidateRequestUnexpectedQueryParam() {
		return this.validateRequestUnexpectedQueryParam;
	}

	public void setValidateRequestUnexpectedQueryParam(boolean isValidateUnexpectedQueryParam) {
		setProperty(OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM, String.valueOf(isValidateUnexpectedQueryParam));
		this.validateRequestUnexpectedQueryParam = isValidateUnexpectedQueryParam;
	}

	public boolean isValidateWildcardSubtypeAsJson() {
		return this.validateWildcardSubtypeAsJson;
	}

	public void setValidateWildcardSubtypeAsJson(boolean validateWildcardSubtypeAsJson) {
		setProperty(OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_WILDCARD_SUBTYPE_AS_JSON, String.valueOf(validateWildcardSubtypeAsJson));
		this.validateWildcardSubtypeAsJson = validateWildcardSubtypeAsJson;
	}

	public boolean isValidateMultipartOptimization() {
		return this.validateMultipartOptimization;
	}

	public void setValidateMultipartOptimization(boolean validateMultipartOptimization) {
		setProperty(OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_MULTIPART_OPTIMIZATION, String.valueOf(validateMultipartOptimization));
		this.validateMultipartOptimization = validateMultipartOptimization;
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
