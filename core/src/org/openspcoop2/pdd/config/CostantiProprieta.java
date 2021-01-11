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


package org.openspcoop2.pdd.config;

/**
 * Classe che raccoglie le proprieta
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class CostantiProprieta {

	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_ENABLED = "true";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_DISABLED = "false";
	
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RICHIESTA_ENABLED = "validation.request.enabled";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RISPOSTA_ENABLED = "validation.response.enabled";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RICHIESTA_TIPO = "validation.request.type";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RISPOSTA_TIPO = "validation.response.type";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RICHIESTA_ACCEPT_MTOM_MESSAGE = "validation.request.acceptMtom";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RISPOSTA_ACCEPT_MTOM_MESSAGE = "validation.response.acceptMtom";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_WARNING_ONLY = "warning";
	
	private static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX= "validation.openApi4j.";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_ENABLED = VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_ENABLED;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_DISABLED = VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_DISABLED;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_ENABLED= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+"enabled";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_MERGE_API_SPEC= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+"mergeAPISpec";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_API_SPEC= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+"validateAPISpec";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_QUERY= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+"validateRequestQuery";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_HEADERS= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+"validateRequestHeaders";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_COOKIES= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+"validateRequestCookies";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_BODY= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+"validateRequestBody";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_RESPONSE_HEADERS= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+"validateResponseHeaders";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_RESPONSE_BODY= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+"validateResponseBody";
	
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SOAPACTION_ENABLED = "validation.soapAction.enabled";
	
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_FAULT_GOVWAY_ENABLED = "validation.faultGovway.enabled";
	
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_PROBLEM_DETAIL_ENABLED = "validation.problemDetails.enabled";
	
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_EMPTY_RESPONSE_ENABLED = "validation.emptyResponse.enabled";
	
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_ENABLED = "validation.returnCode";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR = ",";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_INTERVAL_SEPARATOR = "-";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_NOT = "validation.returnCode.not";
	
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_CONTENT_TYPE_LIST_ENABLED = "validation.contentType";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_CONTENT_TYPE_LIST_SEPARATOR = ",";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_CONTENT_TYPE_NOT = "validation.contentType.not";	
}
