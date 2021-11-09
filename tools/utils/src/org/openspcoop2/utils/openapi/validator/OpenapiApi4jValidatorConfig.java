/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
public class OpenapiApi4jValidatorConfig {

	private OpenAPILibrary openApiLibrary = OpenAPILibrary.json_schema;
	
	private boolean mergeAPISpec = false;
	
	private boolean validateAPISpec = true;
	
	private boolean validateRequestQuery = true;
	private boolean validateRequestHeaders = true;
	private boolean validateRequestCookie = true;
	private boolean validateRequestBody = true;
	
	private boolean validateResponseHeaders = true;
	private boolean validateResponseBody = true;
	
	// TODO PER ANDREA: CONFIGURAZIONI AGGIUNTIVE SWAGGER VALIDATOR
	private boolean isValidateUnexpectedQueryParam = false;
		
	// Dice se lasciare il transformer che aggiunge additionalProperties=false negli schemi (true)
	// o di disattivarlo (false)
	private boolean isInjectingAdditionalProperties = false; 
	
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

	public boolean isValidateUnexpectedQueryParam() {
		return isValidateUnexpectedQueryParam;
	}

	public void setValidateUnexpectedQueryParam(boolean isValidateUnexpectedQueryParam) {
		this.isValidateUnexpectedQueryParam = isValidateUnexpectedQueryParam;
	}

	public boolean isInjectingAdditionalProperties() {
		return isInjectingAdditionalProperties;
	}

	public void setInjectingAdditionalProperties(boolean isInjectingAdditionalProperties) {
		this.isInjectingAdditionalProperties = isInjectingAdditionalProperties;
	}
	
}
