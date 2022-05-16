/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

/**
 * OpenapiApi4jValidatorConfig
 * 
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class OpenapiLibraryValidatorConfig {

	private OpenAPILibrary openApiLibrary = OpenAPILibrary.json_schema;
	
	private boolean mergeAPISpec = false;
	
	private boolean validateAPISpec = true;
	
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
	private boolean swaggerRequestValidator_injectingAdditionalPropertiesFalse = false;
		
	/*
	 *  Dice se sostituire inline i $ref nello schema con le loro definizioni
	 *  Per default viene utilizzato il valore 'false' per non risolvere i combinators, 
	 *  poichè quando vengono risolti non c'è modo di ricordarsi i singoli attributi degli schemi combinati (oneOf, allOf ecc..)
	 */
	private boolean swaggerRequestValidator_resolveFullyApiSpec = false;
	
	public OpenAPILibrary getOpenApiLibrary() {
		return this.openApiLibrary;
	}

	public void setOpenApiLibrary(OpenAPILibrary openApiLibrary) {
		this.openApiLibrary = openApiLibrary;
	}

	public boolean isMergeAPISpec() {
		return this.mergeAPISpec;
	}

	public void setMergeAPISpec(boolean mergeAPISpec) {
		this.mergeAPISpec = mergeAPISpec;
	}
	
	public boolean isValidateAPISpec() {
		return this.validateAPISpec;
	}

	public void setValidateAPISpec(boolean validateAPISpec) {
		this.validateAPISpec = validateAPISpec;
	}

	public boolean isValidateRequestQuery() {
		return this.validateRequestQuery;
	}

	public void setValidateRequestQuery(boolean validateRequestQuery) {
		this.validateRequestQuery = validateRequestQuery;
	}

	public boolean isValidateRequestHeaders() {
		return this.validateRequestHeaders;
	}

	public void setValidateRequestHeaders(boolean validateRequestHeaders) {
		this.validateRequestHeaders = validateRequestHeaders;
	}

	public boolean isValidateRequestCookie() {
		return this.validateRequestCookie;
	}

	public void setValidateRequestCookie(boolean validateRequestCookie) {
		this.validateRequestCookie = validateRequestCookie;
	}

	public boolean isValidateRequestBody() {
		return this.validateRequestBody;
	}

	public void setValidateRequestBody(boolean validateRequestBody) {
		this.validateRequestBody = validateRequestBody;
	}

	public boolean isValidateResponseHeaders() {
		return this.validateResponseHeaders;
	}

	public void setValidateResponseHeaders(boolean validateResponseHeaders) {
		this.validateResponseHeaders = validateResponseHeaders;
	}

	public boolean isValidateResponseBody() {
		return this.validateResponseBody;
	}

	public void setValidateResponseBody(boolean validateResponseBody) {
		this.validateResponseBody = validateResponseBody;
	}

	public boolean isValidateRequestUnexpectedQueryParam() {
		return this.validateRequestUnexpectedQueryParam;
	}

	public void setValidateRequestUnexpectedQueryParam(boolean isValidateUnexpectedQueryParam) {
		this.validateRequestUnexpectedQueryParam = isValidateUnexpectedQueryParam;
	}

	public boolean isValidateWildcardSubtypeAsJson() {
		return this.validateWildcardSubtypeAsJson;
	}

	public void setValidateWildcardSubtypeAsJson(boolean validateWildcardSubtypeAsJson) {
		this.validateWildcardSubtypeAsJson = validateWildcardSubtypeAsJson;
	}

	public boolean isValidateMultipartOptimization() {
		return this.validateMultipartOptimization;
	}

	public void setValidateMultipartOptimization(boolean validateMultipartOptimization) {
		this.validateMultipartOptimization = validateMultipartOptimization;
	}
	
	public boolean isSwaggerRequestValidator_InjectingAdditionalPropertiesFalse() {
		return this.swaggerRequestValidator_injectingAdditionalPropertiesFalse;
	}

	public void setSwaggerRequestValidator_InjectingAdditionalPropertiesFalse(boolean isInjectingAdditionalProperties) {
		this.swaggerRequestValidator_injectingAdditionalPropertiesFalse = isInjectingAdditionalProperties;
	}
	
	public boolean isSwaggerRequestValidator_ResolveFullyApiSpec() {
		return this.swaggerRequestValidator_resolveFullyApiSpec;
	}

	public void setSwaggerRequestValidator_ResolveFullyApiSpec(boolean resolveFullyApiSpec) {
		this.swaggerRequestValidator_resolveFullyApiSpec = resolveFullyApiSpec;
	}
	
}
