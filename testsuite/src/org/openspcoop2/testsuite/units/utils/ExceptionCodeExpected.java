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

package org.openspcoop2.testsuite.units.utils;

import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;

/**
 * ExceptionCodeExpected
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExceptionCodeExpected {

	private boolean genericCode;
	private IntegrationFunctionError integrationFunctionError; 
	
	private boolean protocolException;
	private String codiceErrore;
	private String codiceErroreSpecifico;
	private int codiceErroreSpecificoNumerico;
	
	public boolean isGenericCode() {
		return this.genericCode;
	}
	public void setGenericCode(boolean genericCode) {
		this.genericCode = genericCode;
	}
	public IntegrationFunctionError getIntegrationFunctionError() {
		return this.integrationFunctionError;
	}
	public void setIntegrationFunctionError(IntegrationFunctionError integrationFunctionError) {
		this.integrationFunctionError = integrationFunctionError;
	}
	public int getGovWayReturnCode() {
		switch (this.integrationFunctionError) {
		case CONTENT_TYPE_NOT_PROVIDED:
		case CONTENT_TYPE_NOT_SUPPORTED:
		case SOAP_MUST_UNDERSTAND_UNKNOWN:
		case SOAP_VERSION_MISMATCH:
		case UNPROCESSABLE_REQUEST_CONTENT:
		case REQUEST_TIMED_OUT:
		case NOT_SUPPORTED_BY_PROTOCOL:
		case CORRELATION_INFORMATION_NOT_FOUND:
		case APPLICATION_CORRELATION_IDENTIFICATION_REQUEST_FAILED:
		case INVALID_REQUEST_CONTENT:
		case INTEROPERABILITY_PROFILE_REQUEST_ALREADY_EXISTS:
		case INVALID_INTEROPERABILITY_PROFILE_REQUEST:
		case BAD_REQUEST:
		case ATTACHMENTS_PROCESSING_REQUEST_FAILED:
		case MESSAGE_SECURITY_REQUEST_FAILED:
		case INTEROPERABILITY_PROFILE_ENVELOPING_REQUEST_FAILED:
		case TRANSFORMATION_RULE_REQUEST_FAILED:
		case CONNECTOR_NOT_FOUND:
		case WRAP_400_INTERNAL_BAD_REQUEST:
			return 400;
			
		case PROXY_AUTHENTICATION_CREDENTIALS_NOT_FOUND:
		case PROXY_AUTHENTICATION_INVALID_CREDENTIALS:
		case PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND:
		case AUTHENTICATION_CREDENTIALS_NOT_FOUND:
		case AUTHENTICATION_INVALID_CREDENTIALS:
		case TOKEN_NOT_FOUND:
		case TOKEN_INVALID:
		case TOKEN_EXPIRED:
		case TOKEN_NOT_USABLE_BEFORE:
		case TOKEN_IN_THE_FUTURE:
		case TOKEN_REQUIRED_CLAIMS_NOT_FOUND:
		case AUTHENTICATION:
			return 401;
			
		case CONTENT_AUTHORIZATION_DENY:
		case CONTENT_AUTHORIZATION_POLICY_DENY:
		case AUTHORIZATION_DENY:
		case AUTHORIZATION_POLICY_DENY:
		case AUTHORIZATION_TOKEN_DENY:
		case AUTHORIZATION_MISSING_SCOPE:
		case AUTHORIZATION_MISSING_ROLE:
		case AUTHORIZATION:
			return 403;
			
		case API_IN_UNKNOWN:
		case API_OUT_UNKNOWN:
		case OPERATION_UNDEFINED:
		case IM_MESSAGES_NOT_FOUND:
		case IM_MESSAGE_NOT_FOUND:
		case NOT_FOUND:
			return 404;
			
		case CONFLICT_IN_QUEUE:
		case CONFLICT:
			return 409;
			
		case REQUEST_SIZE_EXCEEDED:
			return 413;
			
		case LIMIT_EXCEEDED_CONDITIONAL_CONGESTION:
		case LIMIT_EXCEEDED_CONDITIONAL_DETERIORATION_PERFORMANCE:
		case LIMIT_EXCEEDED:
		case TOO_MANY_REQUESTS_CONDITIONAL_CONGESTION:
		case TOO_MANY_REQUESTS_CONDITIONAL_DETERIORATION_PERFORMANCE:
		case TOO_MANY_REQUESTS:
			return 429;
			
		case UNPROCESSABLE_RESPONSE_CONTENT:
		case ATTACHMENTS_PROCESSING_RESPONSE_FAILED:
		case APPLICATION_CORRELATION_IDENTIFICATION_RESPONSE_FAILED:
		case MESSAGE_SECURITY_RESPONSE_FAILED:
		case INVALID_RESPONSE_CONTENT:
		case INTEROPERABILITY_PROFILE_ENVELOPING_RESPONSE_FAILED:
		case INVALID_INTEROPERABILITY_PROFILE_RESPONSE:
		case INTEROPERABILITY_PROFILE_RESPONSE_ALREADY_EXISTS:
		case INTEROPERABILITY_PROFILE_RESPONSE_ERROR:
		case TRANSFORMATION_RULE_RESPONSE_FAILED:
		case EXPECTED_RESPONSE_NOT_FOUND:
		case CONFLICT_RESPONSE:
		case RESPONSE_SIZE_EXCEEDED:
		case BAD_RESPONSE:
		case INTERNAL_RESPONSE_ERROR:
		case WRAP_502_BAD_RESPONSE:
		case WRAP_502_INTERNAL_RESPONSE_ERROR:
			return 502;
			
		case SERVICE_UNAVAILABLE:
		case API_SUSPEND:
		case GOVWAY_NOT_INITIALIZED:
		case GOVWAY_RESOURCES_NOT_AVAILABLE:
		case INTERNAL_REQUEST_ERROR:
		case WRAP_503_INTERNAL_ERROR:
			return 503;
			
		case ENDPOINT_REQUEST_TIMED_OUT:
			return 504;

		}
		return 500;
	}

	public boolean isProtocolException() {
		return this.protocolException;
	}
	public void setProtocolException(boolean protocolException) {
		this.protocolException = protocolException;
	}
	public String getCodiceErrore() {
		return this.codiceErrore;
	}
	public void setCodiceErrore(String codiceErrore) {
		this.codiceErrore = codiceErrore;
	}
	public String getCodiceErroreSpecifico() {
		return this.codiceErroreSpecifico;
	}
	public void setCodiceErroreSpecifico(String codiceErroreSpecifico) {
		this.codiceErroreSpecifico = codiceErroreSpecifico;
	}
	public int getCodiceErroreSpecificoNumerico() {
		return this.codiceErroreSpecificoNumerico;
	}
	public void setCodiceErroreSpecificoNumerico(int codiceErroreSpecificoNumerico) {
		this.codiceErroreSpecificoNumerico = codiceErroreSpecificoNumerico;
	}
}
