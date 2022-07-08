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

package org.openspcoop2.protocol.sdk.constants;

import java.io.Serializable;

import org.openspcoop2.message.constants.IntegrationError;

/**
 * IntegrationError
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum IntegrationFunctionError implements Serializable {
	
	// 400
	CONTENT_TYPE_NOT_PROVIDED(true),
	CONTENT_TYPE_NOT_SUPPORTED(true),
	SOAP_MUST_UNDERSTAND_UNKNOWN(true),
	SOAP_VERSION_MISMATCH(true),
	UNPROCESSABLE_REQUEST_CONTENT(true),
	REQUEST_TIMED_OUT(true),
	NOT_SUPPORTED_BY_PROTOCOL(true),
	CORRELATION_INFORMATION_NOT_FOUND(true),
	APPLICATION_CORRELATION_IDENTIFICATION_REQUEST_FAILED(true),
	INVALID_REQUEST_CONTENT(true),
	INTEROPERABILITY_PROFILE_REQUEST_ALREADY_EXISTS(true),
	INVALID_INTEROPERABILITY_PROFILE_REQUEST(true),
	BAD_REQUEST(true),
	
	// 400 altri errori
	ATTACHMENTS_PROCESSING_REQUEST_FAILED(true),
	MESSAGE_SECURITY_REQUEST_FAILED(true),
	INTEROPERABILITY_PROFILE_ENVELOPING_REQUEST_FAILED(true),
	TRANSFORMATION_RULE_REQUEST_FAILED(true),
	CONNECTOR_NOT_FOUND(true),
	// Wrap
	WRAP_400_INTERNAL_BAD_REQUEST(true),
	
	// 401
	PROXY_AUTHENTICATION_CREDENTIALS_NOT_FOUND(true),
	PROXY_AUTHENTICATION_INVALID_CREDENTIALS(true),
	PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND(true),
	AUTHENTICATION_CREDENTIALS_NOT_FOUND(true),
	AUTHENTICATION_INVALID_CREDENTIALS(true),
	TOKEN_NOT_FOUND(true),
	TOKEN_INVALID(true),
	TOKEN_EXPIRED(true),
	TOKEN_NOT_USABLE_BEFORE(true),
	TOKEN_IN_THE_FUTURE(true),
	TOKEN_REQUIRED_CLAIMS_NOT_FOUND(true),
	AUTHENTICATION(true),
	
	// 403
	CONTENT_AUTHORIZATION_DENY(true),
	CONTENT_AUTHORIZATION_POLICY_DENY(true),
	AUTHORIZATION_DENY(true),
	AUTHORIZATION_POLICY_DENY(true),
	AUTHORIZATION_TOKEN_DENY(true),
	AUTHORIZATION_MISSING_SCOPE(true),
	AUTHORIZATION_MISSING_ROLE(true),
	AUTHORIZATION(true),
	
	// 404
	API_IN_UNKNOWN(true),
	API_OUT_UNKNOWN(true),
	OPERATION_UNDEFINED(true),
	IM_MESSAGES_NOT_FOUND(true),
	IM_MESSAGE_NOT_FOUND(true),
	NOT_FOUND(true),
	
	// 409
	CONFLICT_IN_QUEUE(true),
	CONFLICT(true),
	
	// 413
	REQUEST_SIZE_EXCEEDED(true),
	
	// 429
	LIMIT_EXCEEDED_CONDITIONAL_CONGESTION(true),
	LIMIT_EXCEEDED_CONDITIONAL_DETERIORATION_PERFORMANCE(true),
	LIMIT_EXCEEDED(true),
	TOO_MANY_REQUESTS_CONDITIONAL_CONGESTION(true),
	TOO_MANY_REQUESTS_CONDITIONAL_DETERIORATION_PERFORMANCE(true),
	TOO_MANY_REQUESTS(true),
	
	// 502
	UNPROCESSABLE_RESPONSE_CONTENT(false),
	ATTACHMENTS_PROCESSING_RESPONSE_FAILED(false),
	APPLICATION_CORRELATION_IDENTIFICATION_RESPONSE_FAILED(false),
	MESSAGE_SECURITY_RESPONSE_FAILED(false),
	INVALID_RESPONSE_CONTENT(false),
	INTEROPERABILITY_PROFILE_ENVELOPING_RESPONSE_FAILED(false),
	INVALID_INTEROPERABILITY_PROFILE_RESPONSE(false),
	INTEROPERABILITY_PROFILE_RESPONSE_ALREADY_EXISTS(false),
	INTEROPERABILITY_PROFILE_RESPONSE_ERROR(false),
	TRANSFORMATION_RULE_RESPONSE_FAILED(false),
	EXPECTED_RESPONSE_NOT_FOUND(false),
	CONFLICT_RESPONSE(false),
	RESPONSE_SIZE_EXCEEDED(false),
	BAD_RESPONSE(false),
	INTERNAL_RESPONSE_ERROR(false),
	// Wrap
	WRAP_502_BAD_RESPONSE(false),
	WRAP_502_INTERNAL_RESPONSE_ERROR(false),
	
	// 503
	SERVICE_UNAVAILABLE(false),
	API_SUSPEND(false),
	GOVWAY_NOT_INITIALIZED(false),
	GOVWAY_RESOURCES_NOT_AVAILABLE(false),
	INTERNAL_REQUEST_ERROR(false),
	// Wrap
	WRAP_503_INTERNAL_ERROR(false),
	
	// 504
	ENDPOINT_REQUEST_TIMED_OUT(false);
	
	private boolean clientError;
	IntegrationFunctionError(boolean clientError){
		this.clientError = clientError;
	}
	
	public static IntegrationFunctionError[] wrappedValues() {
		IntegrationFunctionError [] ife = new IntegrationFunctionError[4];
		ife[0] = WRAP_400_INTERNAL_BAD_REQUEST;
		ife[1] = WRAP_502_BAD_RESPONSE;
		ife[2] = WRAP_502_INTERNAL_RESPONSE_ERROR;
		ife[3] = WRAP_503_INTERNAL_ERROR;
		return ife;
	}
	
	public boolean isClientError() {
		return this.clientError;
	}
	public boolean isServerError() {
		return !this.clientError;
	}
	
	public boolean isWrapBadRequest() {
		return ATTACHMENTS_PROCESSING_REQUEST_FAILED.equals(this) || MESSAGE_SECURITY_REQUEST_FAILED.equals(this) || 
				INTEROPERABILITY_PROFILE_ENVELOPING_REQUEST_FAILED.equals(this) || TRANSFORMATION_RULE_REQUEST_FAILED.equals(this) ||
				CONNECTOR_NOT_FOUND.equals(this);
	}
	public boolean isWrapInternalError() {
		return GOVWAY_NOT_INITIALIZED.equals(this) || GOVWAY_RESOURCES_NOT_AVAILABLE.equals(this) || 
				INTERNAL_REQUEST_ERROR.equals(this);
	}
	public boolean isWrapBadResponse(IntegrationError integrationError) {
		// tutte tranne RESPONSE_SIZE_EXCEEDED
		return IntegrationError.BAD_RESPONSE.equals(integrationError) &&  !RESPONSE_SIZE_EXCEEDED.equals(this);
	}
	public boolean isWrapInternalResponseError(IntegrationError integrationError) {
		// tutte per adesso
		return IntegrationError.INTERNAL_RESPONSE_ERROR.equals(integrationError);
	}
}	