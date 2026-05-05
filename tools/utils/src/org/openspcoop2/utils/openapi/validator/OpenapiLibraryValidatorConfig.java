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
package org.openspcoop2.utils.openapi.validator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.json.JsonValidatorAPI.ApiName;

/**
 * OpenapiApi4jValidatorConfig
 * 
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class OpenapiLibraryValidatorConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	/* ============================================================
	 * Property keys (suffisso, senza il prefisso "validation.<engine>.")
	 * I prefissi standard sono:
	 *   - validation.openApi.<key>                    (commons / fallback)
	 *   - validation.openApi4j.<key>                  (engine openapi4j)
	 *   - validation.swaggerRequestValidator.<key>    (engine swagger_request_validator)
	 * Le forme complete (prefisso+suffisso) sono in CostantiProprieta.
	 * ============================================================ */

	/** Selezione engine (solo a livello globale: validation.openApi.library). */
	public static final String PROPERTY_KEY_LIBRARY = "library";

	/** Implementazione json-schema validator (NETWORK_NT / FGE). Solo livello globale: validation.openApi.json.validator. */
	public static final String PROPERTY_KEY_JSON_VALIDATOR_API = "json.validator";

	/** Abilitazione engine (es. validation.openApi4j.enabled). */
	public static final String PROPERTY_KEY_ENABLED = "enabled";

	public static final String PROPERTY_KEY_MERGE_API_SPEC = "mergeAPISpec";
	public static final String PROPERTY_KEY_VALIDATE_API_SPEC = "validateAPISpec";

	public static final String PROPERTY_KEY_VALIDATE_REQUEST_PATH = "validateRequestPath";
	public static final String PROPERTY_KEY_VALIDATE_REQUEST_QUERY = "validateRequestQuery";
	public static final String PROPERTY_KEY_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM = "validateRequestUnexpectedQueryParam";
	public static final String PROPERTY_KEY_VALIDATE_REQUEST_HEADERS = "validateRequestHeaders";
	public static final String PROPERTY_KEY_VALIDATE_REQUEST_COOKIES = "validateRequestCookies";
	public static final String PROPERTY_KEY_VALIDATE_REQUEST_BODY = "validateRequestBody";

	public static final String PROPERTY_KEY_VALIDATE_RESPONSE_HEADERS = "validateResponseHeaders";
	public static final String PROPERTY_KEY_VALIDATE_RESPONSE_BODY = "validateResponseBody";

	public static final String PROPERTY_KEY_VALIDATE_WILDCARD_SUBTYPE_AS_JSON = "validateWildcardSubtypeAsJson";
	/** Solo openapi4j. */
	public static final String PROPERTY_KEY_VALIDATE_MULTIPART_OPTIMIZATION = "validateMultipartOptimization";

	/** Solo swagger_request_validator. */
	public static final String PROPERTY_KEY_SWAGGER_INJECTING_ADDITIONAL_PROPERTIES_FALSE = "injectingAdditionalPropertiesFalse";
	/** Solo swagger_request_validator. */
	public static final String PROPERTY_KEY_SWAGGER_RESOLVE_FULLY_API_SPEC = "resolveFullyApiSpec";

	private OpenAPILibrary openApiLibrary;
	private ApiName jsonValidatorAPI;

	private boolean mergeAPISpec;

	private boolean validateAPISpec;

	private boolean validateRequestPath;
	private boolean validateRequestQuery;
	/* utilizzato solo in swagger_request_validator per disabilitare il controllo.
	 * Da specifica i parametri ulteriori vanno ignorati, mentre la libreria swagger_request_validator li valida
	 * Per default quindi questa opzione viene disabilitata
	 */
	private boolean validateRequestUnexpectedQueryParam;
	private boolean validateRequestHeaders;
	private boolean validateRequestCookie;
	private boolean validateRequestBody;

	private boolean validateResponseHeaders;
	private boolean validateResponseBody;

	// Dice se trattare il subtype '*' come json e quindi validare lo schema del body passato
	private boolean validateWildcardSubtypeAsJson;

	// La validazione delle richieste multipart/form-data (o mixed) prevede per default il processamento di tutto lo stream.
	// Poichè le parti "binarie" non richiedono una validazione rispetto ad uno schema e sono tipicamente serializzate dopo i metadati (plain o json)
	// potrebbero essere "saltate" terminando l'analisi dello stream dopo aver validato i metadati in modo da avere benefici prestazionali visto che tipicamente le parti binarie rappresentano
	// la maggior dimensione del messaggio in termini di bytes.
	// L'ottimizzazione sopra indicata non consente però di verificare se esistono part non definite nella specifica da segnalare come non validi quando viene definito l'additionalProperties=false.
	// Per il motivo precedentemente indicato l'ottimizzazione non è abilitata per default.
	// NOTA: feature supportata solamente dalla libreria openapi4j.
	private boolean validateMultipartOptimization;

	/* La libreria swagger_request_validator utilizza per default un transformer che aggiunge additionalProperties=false negli schemi (true)
	 * Tramite la seguente proprietà è possibile disattivarlo.
	 * È necessario disattivarlo per poter validare correttamente gli schemi che definiscono tale proprietà a true.
	 * La libreria lo utilizza come workaround per validare strutture allOf
	 * Per poterlo disabiltare è stata modificata la libreria di atlassian, nello specifico com.atlassian.oai.validator.schema.SchemaValidator,
	 * per rendere pubblica la lista di transformers così da poter rimuovere l'AdditionalPropertiesInjectionTransformer che aggiunge additionalProperties: false negli schemi.
	 * Alcuni interventi dove è stata riportata la segnalazione
	 * - https://bitbucket.org/atlassian/swagger-request-validator/issues/369/make-the
	 * - https://bitbucket.org/atlassian/swagger-request-validator/issues/336/oneof-and-anyof-validations-violate
	 * - https://bitbucket.org/atlassian/swagger-request-validator/issues/293/specify-the-schemavalidator-when-creating
	 * L'intervento è stato fatto con utente 'FullName'
	 * Altri link:
	 * - https://bitbucket.org/atlassian/swagger-request-validator/issues/271/composition-with-allof-anyof-or-oneof
	 */
	private boolean swaggerRequestValidator_injectingAdditionalPropertiesFalse;

	/*
	 *  Dice se sostituire inline i $ref nello schema con le loro definizioni
	 *  Per default viene utilizzato il valore 'false' per non risolvere i combinators,
	 *  poichè quando vengono risolti non c'è modo di ricordarsi i singoli attributi degli schemi combinati (oneOf, allOf ecc..)
	 */
	private boolean swaggerRequestValidator_resolveFullyApiSpec;

	private Map<String, String> validationProperties = new HashMap<>();

	public OpenapiLibraryValidatorConfig() {
		// I default vengono assegnati tramite i setter così da popolare anche la mappa validationProperties.
		setOpenApiLibrary(OpenAPILibrary.json_schema);
		setMergeAPISpec(false);
		setValidateAPISpec(true);
		setValidateRequestPath(true);
		setValidateRequestQuery(true);
		setValidateRequestUnexpectedQueryParam(false);
		setValidateRequestHeaders(true);
		setValidateRequestCookie(true);
		setValidateRequestBody(true);
		setValidateResponseHeaders(true);
		setValidateResponseBody(true);
		setValidateWildcardSubtypeAsJson(true);
		setValidateMultipartOptimization(false);
		setSwaggerRequestValidator_InjectingAdditionalPropertiesFalse(false);
		setSwaggerRequestValidator_ResolveFullyApiSpec(false);
	}

	/**
	 * Ricostruisce nella mappa {@link #validationProperties} le voci standard
	 * leggendo dai campi correnti. Le eventuali voci custom (chiavi non gestite
	 * qui sotto) vengono preservate.
	 */
	private void syncProperties() {
		setProperty(PROPERTY_KEY_LIBRARY, this.openApiLibrary != null ? this.openApiLibrary.name() : null);
		setProperty(PROPERTY_KEY_JSON_VALIDATOR_API, this.jsonValidatorAPI != null ? this.jsonValidatorAPI.name() : null);
		setProperty(PROPERTY_KEY_MERGE_API_SPEC, String.valueOf(this.mergeAPISpec));
		setProperty(PROPERTY_KEY_VALIDATE_API_SPEC, String.valueOf(this.validateAPISpec));
		setProperty(PROPERTY_KEY_VALIDATE_REQUEST_PATH, String.valueOf(this.validateRequestPath));
		setProperty(PROPERTY_KEY_VALIDATE_REQUEST_QUERY, String.valueOf(this.validateRequestQuery));
		setProperty(PROPERTY_KEY_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM, String.valueOf(this.validateRequestUnexpectedQueryParam));
		setProperty(PROPERTY_KEY_VALIDATE_REQUEST_HEADERS, String.valueOf(this.validateRequestHeaders));
		setProperty(PROPERTY_KEY_VALIDATE_REQUEST_COOKIES, String.valueOf(this.validateRequestCookie));
		setProperty(PROPERTY_KEY_VALIDATE_REQUEST_BODY, String.valueOf(this.validateRequestBody));
		setProperty(PROPERTY_KEY_VALIDATE_RESPONSE_HEADERS, String.valueOf(this.validateResponseHeaders));
		setProperty(PROPERTY_KEY_VALIDATE_RESPONSE_BODY, String.valueOf(this.validateResponseBody));
		setProperty(PROPERTY_KEY_VALIDATE_WILDCARD_SUBTYPE_AS_JSON, String.valueOf(this.validateWildcardSubtypeAsJson));
		setProperty(PROPERTY_KEY_VALIDATE_MULTIPART_OPTIMIZATION, String.valueOf(this.validateMultipartOptimization));
		setProperty(PROPERTY_KEY_SWAGGER_INJECTING_ADDITIONAL_PROPERTIES_FALSE, String.valueOf(this.swaggerRequestValidator_injectingAdditionalPropertiesFalse));
		setProperty(PROPERTY_KEY_SWAGGER_RESOLVE_FULLY_API_SPEC, String.valueOf(this.swaggerRequestValidator_resolveFullyApiSpec));
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		if (this.validationProperties == null) {
			this.validationProperties = new HashMap<>();
		}
		syncProperties();
	}

	public OpenAPILibrary getOpenApiLibrary() {
		return this.openApiLibrary;
	}

	public void setOpenApiLibrary(OpenAPILibrary openApiLibrary) {
		setProperty(PROPERTY_KEY_LIBRARY, openApiLibrary != null ? openApiLibrary.name() : null);
		this.openApiLibrary = openApiLibrary;
	}

	public ApiName getJsonValidatorAPI() {
		return this.jsonValidatorAPI;
	}

	public void setJsonValidatorAPI(ApiName jsonValidatorAPI) {
		if (jsonValidatorAPI != null) {
			setProperty(PROPERTY_KEY_JSON_VALIDATOR_API, jsonValidatorAPI.name());
		}
		this.jsonValidatorAPI = jsonValidatorAPI;
	}

	public boolean isMergeAPISpec() {
		return this.mergeAPISpec;
	}

	public void setMergeAPISpec(boolean mergeAPISpec) {
		setProperty(PROPERTY_KEY_MERGE_API_SPEC, String.valueOf(mergeAPISpec));
		this.mergeAPISpec = mergeAPISpec;
	}
	
	public boolean isValidateAPISpec() {
		return this.validateAPISpec;
	}

	public void setValidateAPISpec(boolean validateAPISpec) {
		setProperty(PROPERTY_KEY_VALIDATE_API_SPEC, String.valueOf(validateAPISpec));
		this.validateAPISpec = validateAPISpec;
	}

	public boolean isValidateRequestPath() {
		return this.validateRequestPath;
	}

	public void setValidateRequestPath(boolean validateRequestPath) {
		setProperty(PROPERTY_KEY_VALIDATE_REQUEST_PATH, String.valueOf(validateRequestPath));
		this.validateRequestPath = validateRequestPath;
	}
	
	public boolean isValidateRequestQuery() {
		return this.validateRequestQuery;
	}

	public void setValidateRequestQuery(boolean validateRequestQuery) {
		setProperty(PROPERTY_KEY_VALIDATE_REQUEST_QUERY, String.valueOf(validateRequestQuery));
		this.validateRequestQuery = validateRequestQuery;
	}

	public boolean isValidateRequestHeaders() {
		return this.validateRequestHeaders;
	}

	public void setValidateRequestHeaders(boolean validateRequestHeaders) {
		setProperty(PROPERTY_KEY_VALIDATE_REQUEST_HEADERS, String.valueOf(validateRequestHeaders));
		this.validateRequestHeaders = validateRequestHeaders;
	}

	public boolean isValidateRequestCookie() {
		return this.validateRequestCookie;
	}

	public void setValidateRequestCookie(boolean validateRequestCookie) {
		setProperty(PROPERTY_KEY_VALIDATE_REQUEST_COOKIES, String.valueOf(validateRequestCookie));
		this.validateRequestCookie = validateRequestCookie;
	}

	public boolean isValidateRequestBody() {
		return this.validateRequestBody;
	}

	public void setValidateRequestBody(boolean validateRequestBody) {
		setProperty(PROPERTY_KEY_VALIDATE_REQUEST_BODY, String.valueOf(validateRequestBody));
		this.validateRequestBody = validateRequestBody;
	}

	public boolean isValidateResponseHeaders() {
		return this.validateResponseHeaders;
	}

	public void setValidateResponseHeaders(boolean validateResponseHeaders) {
		setProperty(PROPERTY_KEY_VALIDATE_RESPONSE_HEADERS, String.valueOf(validateResponseHeaders));
		this.validateResponseHeaders = validateResponseHeaders;
	}

	public boolean isValidateResponseBody() {
		return this.validateResponseBody;
	}

	public void setValidateResponseBody(boolean validateResponseBody) {
		setProperty(PROPERTY_KEY_VALIDATE_RESPONSE_BODY, String.valueOf(validateResponseBody));
		this.validateResponseBody = validateResponseBody;
	}

	public boolean isValidateRequestUnexpectedQueryParam() {
		return this.validateRequestUnexpectedQueryParam;
	}

	public void setValidateRequestUnexpectedQueryParam(boolean isValidateUnexpectedQueryParam) {
		setProperty(PROPERTY_KEY_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM, String.valueOf(isValidateUnexpectedQueryParam));
		this.validateRequestUnexpectedQueryParam = isValidateUnexpectedQueryParam;
	}

	public boolean isValidateWildcardSubtypeAsJson() {
		return this.validateWildcardSubtypeAsJson;
	}

	public void setValidateWildcardSubtypeAsJson(boolean validateWildcardSubtypeAsJson) {
		setProperty(PROPERTY_KEY_VALIDATE_WILDCARD_SUBTYPE_AS_JSON, String.valueOf(validateWildcardSubtypeAsJson));
		this.validateWildcardSubtypeAsJson = validateWildcardSubtypeAsJson;
	}

	public boolean isValidateMultipartOptimization() {
		return this.validateMultipartOptimization;
	}

	public void setValidateMultipartOptimization(boolean validateMultipartOptimization) {
		setProperty(PROPERTY_KEY_VALIDATE_MULTIPART_OPTIMIZATION, String.valueOf(validateMultipartOptimization));
		this.validateMultipartOptimization = validateMultipartOptimization;
	}
	
	public boolean isSwaggerRequestValidator_InjectingAdditionalPropertiesFalse() {
		return this.swaggerRequestValidator_injectingAdditionalPropertiesFalse;
	}

	public void setSwaggerRequestValidator_InjectingAdditionalPropertiesFalse(boolean isInjectingAdditionalProperties) {
		setProperty(PROPERTY_KEY_SWAGGER_INJECTING_ADDITIONAL_PROPERTIES_FALSE, String.valueOf(isInjectingAdditionalProperties));
		this.swaggerRequestValidator_injectingAdditionalPropertiesFalse = isInjectingAdditionalProperties;
	}
	
	public boolean isSwaggerRequestValidator_ResolveFullyApiSpec() {
		return this.swaggerRequestValidator_resolveFullyApiSpec;
	}

	public void setSwaggerRequestValidator_ResolveFullyApiSpec(boolean resolveFullyApiSpec) {
		setProperty(PROPERTY_KEY_SWAGGER_RESOLVE_FULLY_API_SPEC, String.valueOf(resolveFullyApiSpec));
		this.swaggerRequestValidator_resolveFullyApiSpec = resolveFullyApiSpec;
	}
	
	public void removeProperty(String name) {
		this.validationProperties.remove(name);
	}
	
	public void setProperty(String name, String value) {
		this.validationProperties.put(name, value);
	}
	
	public String getProperty(String name) {
		return this.validationProperties.get(name);
	}
	
	public Iterable<String> getPropertyKeys() {
		return this.validationProperties.keySet();
	}
	
}
