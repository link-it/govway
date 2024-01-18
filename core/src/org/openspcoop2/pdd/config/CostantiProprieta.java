/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.pdd.core.dynamic.InformazioniIntegrazioneCodifica;
import org.openspcoop2.pdd.core.dynamic.InformazioniIntegrazioneSorgente;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.certificate.KeystoreType;

/**
 * Classe che raccoglie le proprieta
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class CostantiProprieta {

	private CostantiProprieta() {}
	
	
	public static final String VALUE_ENABLED = "true";
	public static final String VALUE_DISABLED = "false";
	
	
	
	// ****  VALIDAZIONE *****
	
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_ENABLED = VALUE_ENABLED;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_DISABLED = VALUE_DISABLED;
	
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RICHIESTA_ENABLED = "validation.request.enabled";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RISPOSTA_ENABLED = "validation.response.enabled";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RICHIESTA_TIPO = "validation.request.type";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RISPOSTA_TIPO = "validation.response.type";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RICHIESTA_ACCEPT_MTOM_MESSAGE = "validation.request.acceptMtom";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RISPOSTA_ACCEPT_MTOM_MESSAGE = "validation.response.acceptMtom";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_WARNING_ONLY = "warning";
	
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_ENABLED_SUFFIX= "enabled";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_MERGE_API_SPEC_SUFFIX= "mergeAPISpec";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_API_SPEC_SUFFIX= "validateAPISpec";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_PATH_SUFFIX= "validateRequestPath";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_QUERY_SUFFIX= "validateRequestQuery";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM_SUFFIX= "validateRequestUnexpectedQueryParam";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_HEADERS_SUFFIX= "validateRequestHeaders";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_COOKIES_SUFFIX= "validateRequestCookies";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_BODY_SUFFIX= "validateRequestBody";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_RESPONSE_HEADERS_SUFFIX= "validateResponseHeaders";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_RESPONSE_BODY_SUFFIX= "validateResponseBody";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_WILDCARD_SUBTYPE_AS_JSON_SUFFIX="validateWildcardSubtypeAsJson";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_MULTIPART_OPTIMIZATION_SUFFIX="validateMultipartOptimization";
	
	private static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_PREFIX= "validation.openApi.";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_COMMONS_ENABLED = VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_ENABLED;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_COMMONS_DISABLED = VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_DISABLED;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_MERGE_API_SPEC= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_MERGE_API_SPEC_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_API_SPEC= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_API_SPEC_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_PATH= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_PATH_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_QUERY= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_QUERY_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_HEADERS= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_HEADERS_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_COOKIES= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_COOKIES_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_BODY= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_BODY_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_RESPONSE_HEADERS= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_RESPONSE_HEADERS_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_RESPONSE_BODY= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_RESPONSE_BODY_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_WILDCARD_SUBTYPE_AS_JSON= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_WILDCARD_SUBTYPE_AS_JSON_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_MULTIPART_OPTIMIZATION= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_MULTIPART_OPTIMIZATION_SUFFIX;
	
	private static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX= "validation.openApi4j.";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_ENABLED = VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_ENABLED;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_DISABLED = VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_DISABLED;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_ENABLED= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_ENABLED_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_MERGE_API_SPEC= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_MERGE_API_SPEC_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_API_SPEC= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_API_SPEC_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_PATH= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_PATH_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_QUERY= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_QUERY_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_HEADERS= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_HEADERS_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_COOKIES= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_COOKIES_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_BODY= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_BODY_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_RESPONSE_HEADERS= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_RESPONSE_HEADERS_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_RESPONSE_BODY= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_RESPONSE_BODY_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_WILDCARD_SUBTYPE_AS_JSON= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_WILDCARD_SUBTYPE_AS_JSON_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_MULTIPART_OPTIMIZATION= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_MULTIPART_OPTIMIZATION_SUFFIX;
	
	private static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX= "validation.swaggerRequestValidator.";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_SWAGGER_REQUEST_VALIDATOR_ENABLED = VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_ENABLED;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_SWAGGER_REQUEST_VALIDATOR_DISABLED = VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_DISABLED;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_ENABLED= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_ENABLED_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_MERGE_API_SPEC= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_MERGE_API_SPEC_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_API_SPEC= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_API_SPEC_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_REQUEST_PATH= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_PATH_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_REQUEST_QUERY= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_QUERY_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_REQUEST_HEADERS= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_HEADERS_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_REQUEST_COOKIES= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_COOKIES_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_REQUEST_BODY= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_BODY_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_RESPONSE_HEADERS= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_RESPONSE_HEADERS_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_RESPONSE_BODY= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_RESPONSE_BODY_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_WILDCARD_SUBTYPE_AS_JSON= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX+VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_WILDCARD_SUBTYPE_AS_JSON_SUFFIX;
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_INJECTING_ADDITIONAL_PROPERTIES_FALSE= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX+"injectingAdditionalPropertiesFalse";
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_RESOLVE_FULLY_API_SPEC= VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_PREFIX+"resolveFullyApiSpec";	
	
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_BUFFER_ENABLED = "validation.buffer.enabled";
	
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SOAPACTION_ENABLED = "validation.soapAction.enabled";
	
	public static final String VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RPC_ACCEPT_ROOT_ELEMENT_UNQUALIFIED_ENABLED = "validation.rpc.rootElementUnqualified.accept";
	
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
	
	
	
	// ****  AUTENTICAZIONE HTTPS *****
	
	public static final String AUTENTICAZIONE_VALUE_ENABLED = VALUE_ENABLED;
	public static final String AUTENTICAZIONE_VALUE_DISABLED = VALUE_DISABLED;
	
	public static final String AUTENTICAZIONE_HTTPS_VALIDITY_CHECK = "authentication.https.validityCheck";
	
	public static final String AUTENTICAZIONE_HTTPS_TRUSTSTORE_ENABLED = "authentication.https.truststore.enabled";
	public static final String AUTENTICAZIONE_HTTPS_TRUSTSTORE = "authentication.https.truststore";
	public static final String AUTENTICAZIONE_HTTPS_TRUSTSTORE_PASSWORD = "authentication.https.truststore.password";
	public static final String AUTENTICAZIONE_HTTPS_TRUSTSTORE_TYPE = "authentication.https.truststore.type";
	public static final String AUTENTICAZIONE_HTTPS_TRUSTSTORE_CRLS = "authentication.https.truststore.crls";
	public static final String AUTENTICAZIONE_HTTPS_TRUSTSTORE_OCSP_POLICY = "authentication.https.truststore.ocspPolicy";
	
	public static boolean isAutenticazioneHttpsValidityCheck(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, AUTENTICAZIONE_HTTPS_VALIDITY_CHECK, defaultValue, AUTENTICAZIONE_VALUE_ENABLED, AUTENTICAZIONE_VALUE_DISABLED);
	}
	
	public static boolean isAutenticazioneHttpsTrustStore(List<Proprieta> proprieta, File pathDefaultValue) {
		String enabled = readValue(proprieta, AUTENTICAZIONE_HTTPS_TRUSTSTORE_ENABLED);
		boolean isEnabled = true;
		if(enabled!=null && !StringUtils.isEmpty(enabled) &&
				AUTENTICAZIONE_VALUE_DISABLED.equals(enabled.trim())) {
			isEnabled = false;
		}
		if(!isEnabled) {
			return false; // la proprietà serve soprattutto a disabilitarlo se definito in govway.properties
		}
		
		String path = readValue(proprieta, AUTENTICAZIONE_HTTPS_TRUSTSTORE);
		if( 
				(path==null || StringUtils.isEmpty(path)) 
				&&
				pathDefaultValue!=null
			) {
			path = pathDefaultValue.getAbsolutePath();
		}
		
		// La presenza di un path, basta ad abilitarlo. La proprietà enabled serve per disabilitarlo
		return path!=null && StringUtils.isNotEmpty(path);
	}
	
	public static String getAutenticazioneHttpsTrustStorePath(List<Proprieta> proprieta, File pathDefaultValue) {
		String path = readValue(proprieta, AUTENTICAZIONE_HTTPS_TRUSTSTORE);
		if(
				(path==null || StringUtils.isEmpty(path))
				&&
				pathDefaultValue!=null
			) {
			path = pathDefaultValue.getAbsolutePath();
		}
		return path;
	}
	public static String getAutenticazioneHttpsTrustStorePassword(List<Proprieta> proprieta, String passwordDefaultValue) {
		String password = readValue(proprieta, AUTENTICAZIONE_HTTPS_TRUSTSTORE_PASSWORD);
		if(password==null) {
			password = passwordDefaultValue;
		}
		return password;
	}
	public static String getAutenticazioneHttpsTrustStoreType(List<Proprieta> proprieta, String typeDefaultValue) {
		String type = readValue(proprieta, AUTENTICAZIONE_HTTPS_TRUSTSTORE_TYPE);
		if(type==null) {
			type = typeDefaultValue;
		}
		if(type==null) {
			type = KeystoreType.JKS.getNome();
		}
		return type;
	}
	public static String getAutenticazioneHttpsTrustStoreCRLs(List<Proprieta> proprieta, String crlsDefaultValue) {
		String crls = readValue(proprieta, AUTENTICAZIONE_HTTPS_TRUSTSTORE_CRLS);
		if(crls==null) {
			crls = crlsDefaultValue;
		}
		return crls;
	}
	public static String getAutenticazioneHttpsTrustStoreOCSPPolicy(List<Proprieta> proprieta, String crlsDefaultValue) {
		String crls = readValue(proprieta, AUTENTICAZIONE_HTTPS_TRUSTSTORE_OCSP_POLICY);
		if(crls==null) {
			crls = crlsDefaultValue;
		}
		return crls;
	}
	
	
	// ****  CORRELAZIONE APPLICATIVA *****
	
	public static final String CORRELAZIONE_APPLICATIVA_VALUE_ENABLED = VALUE_ENABLED;
	public static final String CORRELAZIONE_APPLICATIVA_VALUE_DISABLED = VALUE_DISABLED;
	
	public static final String CORRELAZIONE_APPLICATIVA_PROPERTY_NAME_TRUNCATE_ID = "correlation.truncate";
	public static final String CORRELAZIONE_APPLICATIVA_PROPERTY_NAME_ACCEPT_IDENTIFICATION_FAILED_TRUNCATE_ID = "correlation.acceptIdentificationFailed.truncate";
	public static final String CORRELAZIONE_APPLICATIVA_PROPERTY_NAME_BLOCK_IDENTIFICATION_FAILED_TRUNCATE_ID = "correlation.blockIdentificationFailed.truncate";
	
	public static final String CORRELAZIONE_APPLICATIVA_RICHIESTA_PROPERTY_NAME_TRUNCATE_ID = "correlation.request.truncate";
	public static final String CORRELAZIONE_APPLICATIVA_RICHIESTA_PROPERTY_NAME_ACCEPT_IDENTIFICATION_FAILED_TRUNCATE_ID = "correlation.request.acceptIdentificationFailed.truncate";
	public static final String CORRELAZIONE_APPLICATIVA_RICHIESTA_PROPERTY_NAME_BLOCK_IDENTIFICATION_FAILED_TRUNCATE_ID = "correlation.request.blockIdentificationFailed.truncate";
	
	public static final String CORRELAZIONE_APPLICATIVA_RISPOSTA_PROPERTY_NAME_TRUNCATE_ID = "correlation.response.truncate";
	public static final String CORRELAZIONE_APPLICATIVA_RISPOSTA_PROPERTY_NAME_ACCEPT_IDENTIFICATION_FAILED_TRUNCATE_ID = "correlation.response.acceptIdentificationFailed.truncate";
	public static final String CORRELAZIONE_APPLICATIVA_RISPOSTA_PROPERTY_NAME_BLOCK_IDENTIFICATION_FAILED_TRUNCATE_ID = "correlation.response.blockIdentificationFailed.truncate";
	
	public static final String CORRELAZIONE_APPLICATIVA_RICHIESTA_PROPERTY_NAME_EXTRACTED_IDENTIFIER_IS_NULL_ABORT_TRANSACTION_ID = "correlation.request.extractedIdentifierIsNull.abortTransaction";
	public static final String CORRELAZIONE_APPLICATIVA_RISPOSTA_PROPERTY_NAME_EXTRACTED_IDENTIFIER_IS_NULL_ABORT_TRANSACTION_ID = "correlation.response.extractedIdentifierIsNull.abortTransaction";
	
	public static final String CORRELAZIONE_APPLICATIVA_RICHIESTA_PROPERTY_NAME_EXTRACTED_IDENTIFIER_IS_EMPTY_ABORT_TRANSACTION_ID = "correlation.request.extractedIdentifierIsEmpty.abortTransaction";
	public static final String CORRELAZIONE_APPLICATIVA_RISPOSTA_PROPERTY_NAME_EXTRACTED_IDENTIFIER_IS_EMPTY_ABORT_TRANSACTION_ID = "correlation.response.extractedIdentifierIsEmpty.abortTransaction";
			
	public static final String CORRELAZIONE_APPLICATIVA_RICHIESTA_PROPERTY_NAME_RULE_NOT_FOUND_ABORT_TRANSACTION_ID = "correlation.request.ruleNotFound.abortTransaction";
	public static final String CORRELAZIONE_APPLICATIVA_RISPOSTA_PROPERTY_NAME_RULE_NOT_FOUND_ABORT_TRANSACTION_ID = "correlation.response.ruleNotFound.abortTransaction";
	
	public static boolean isCorrelazioneApplicativaRichiestaIdentificazioneFallitaBloccaTruncate(List<Proprieta> proprieta, boolean defaultValue) {
		return isCorrelazioneApplicativaTruncate(proprieta, defaultValue,
				CORRELAZIONE_APPLICATIVA_RICHIESTA_PROPERTY_NAME_BLOCK_IDENTIFICATION_FAILED_TRUNCATE_ID,
				CORRELAZIONE_APPLICATIVA_RICHIESTA_PROPERTY_NAME_TRUNCATE_ID,
				CORRELAZIONE_APPLICATIVA_PROPERTY_NAME_BLOCK_IDENTIFICATION_FAILED_TRUNCATE_ID,
				CORRELAZIONE_APPLICATIVA_PROPERTY_NAME_TRUNCATE_ID);
	}
	public static boolean isCorrelazioneApplicativaRichiestaIdentificazioneFallitaAccettaTruncate(List<Proprieta> proprieta, boolean defaultValue) {
		return isCorrelazioneApplicativaTruncate(proprieta, defaultValue,
				CORRELAZIONE_APPLICATIVA_RICHIESTA_PROPERTY_NAME_ACCEPT_IDENTIFICATION_FAILED_TRUNCATE_ID,
				CORRELAZIONE_APPLICATIVA_RICHIESTA_PROPERTY_NAME_TRUNCATE_ID,
				CORRELAZIONE_APPLICATIVA_PROPERTY_NAME_ACCEPT_IDENTIFICATION_FAILED_TRUNCATE_ID,
				CORRELAZIONE_APPLICATIVA_PROPERTY_NAME_TRUNCATE_ID);
	}
	public static boolean isCorrelazioneApplicativaRispostaIdentificazioneFallitaBloccaTruncate(List<Proprieta> proprieta, boolean defaultValue) {
		return isCorrelazioneApplicativaTruncate(proprieta, defaultValue,
				CORRELAZIONE_APPLICATIVA_RISPOSTA_PROPERTY_NAME_BLOCK_IDENTIFICATION_FAILED_TRUNCATE_ID,
				CORRELAZIONE_APPLICATIVA_RISPOSTA_PROPERTY_NAME_TRUNCATE_ID,
				CORRELAZIONE_APPLICATIVA_PROPERTY_NAME_BLOCK_IDENTIFICATION_FAILED_TRUNCATE_ID,
				CORRELAZIONE_APPLICATIVA_PROPERTY_NAME_TRUNCATE_ID);
	}
	public static boolean isCorrelazioneApplicativaRispostaIdentificazioneFallitaAccettaTruncate(List<Proprieta> proprieta, boolean defaultValue) {
		return isCorrelazioneApplicativaTruncate(proprieta, defaultValue,
				CORRELAZIONE_APPLICATIVA_RISPOSTA_PROPERTY_NAME_ACCEPT_IDENTIFICATION_FAILED_TRUNCATE_ID,
				CORRELAZIONE_APPLICATIVA_RISPOSTA_PROPERTY_NAME_TRUNCATE_ID,
				CORRELAZIONE_APPLICATIVA_PROPERTY_NAME_ACCEPT_IDENTIFICATION_FAILED_TRUNCATE_ID,
				CORRELAZIONE_APPLICATIVA_PROPERTY_NAME_TRUNCATE_ID);
	}
	private static boolean isCorrelazioneApplicativaTruncate(List<Proprieta> proprieta, boolean defaultValue,
			String propertyNameIdentificationFailedFlow, String propertyNameFlow,
			String propertyNameIdentificationFailed, String propertyName) {
		
		BooleanNullable flow = isCorrelazioneApplicativaTruncateFlow(proprieta,
				propertyNameIdentificationFailedFlow, propertyNameFlow);
		if(flow!=null && flow.getValue()!=null) {
			return flow.getValue().booleanValue();
		}
				
		String valueS = readValue(proprieta, propertyNameIdentificationFailed);
		if(valueS!=null && StringUtils.isNotEmpty(valueS)) {
			if(CORRELAZIONE_APPLICATIVA_VALUE_ENABLED.equals(valueS.trim())) {
				return true;
			}
			else if(CORRELAZIONE_APPLICATIVA_VALUE_DISABLED.equals(valueS.trim())) {
				return false;
			}
		}
		
		valueS = readValue(proprieta, propertyName);
		if(valueS!=null && StringUtils.isNotEmpty(valueS)) {
			if(CORRELAZIONE_APPLICATIVA_VALUE_ENABLED.equals(valueS.trim())) {
				return true;
			}
			else if(CORRELAZIONE_APPLICATIVA_VALUE_DISABLED.equals(valueS.trim())) {
				return false;
			}
		}
		
		return defaultValue;
	}
	private static BooleanNullable isCorrelazioneApplicativaTruncateFlow(List<Proprieta> proprieta,
			String propertyNameIdentificationFailedFlow, String propertyNameFlow) {
		String valueS = readValue(proprieta, propertyNameIdentificationFailedFlow);
		if(valueS!=null && StringUtils.isNotEmpty(valueS)) {
			if(CORRELAZIONE_APPLICATIVA_VALUE_ENABLED.equals(valueS.trim())) {
				return BooleanNullable.TRUE();
			}
			else if(CORRELAZIONE_APPLICATIVA_VALUE_DISABLED.equals(valueS.trim())) {
				return BooleanNullable.FALSE();
			}
		}
		
		valueS = readValue(proprieta, propertyNameFlow);
		if(valueS!=null && StringUtils.isNotEmpty(valueS)) {
			if(CORRELAZIONE_APPLICATIVA_VALUE_ENABLED.equals(valueS.trim())) {
				return BooleanNullable.TRUE();
			}
			else if(CORRELAZIONE_APPLICATIVA_VALUE_DISABLED.equals(valueS.trim())) {
				return BooleanNullable.FALSE();
			}
		}
		
		return BooleanNullable.NULL();
	}
	
	public static boolean isCorrelazioneApplicativaRichiestaIdentificativoEstrattoIsNullTerminaTransazioneConErrore(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, CORRELAZIONE_APPLICATIVA_RICHIESTA_PROPERTY_NAME_EXTRACTED_IDENTIFIER_IS_NULL_ABORT_TRANSACTION_ID, defaultValue, CORRELAZIONE_APPLICATIVA_VALUE_ENABLED, CORRELAZIONE_APPLICATIVA_VALUE_DISABLED);
	}
	public static boolean isCorrelazioneApplicativaRispostaIdentificativoEstrattoIsNullTerminaTransazioneConErrore(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, CORRELAZIONE_APPLICATIVA_RISPOSTA_PROPERTY_NAME_EXTRACTED_IDENTIFIER_IS_NULL_ABORT_TRANSACTION_ID, defaultValue, CORRELAZIONE_APPLICATIVA_VALUE_ENABLED, CORRELAZIONE_APPLICATIVA_VALUE_DISABLED);
	}
	
	public static boolean isCorrelazioneApplicativaRichiestaIdentificativoEstrattoIsEmptyTerminaTransazioneConErrore(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, CORRELAZIONE_APPLICATIVA_RICHIESTA_PROPERTY_NAME_EXTRACTED_IDENTIFIER_IS_EMPTY_ABORT_TRANSACTION_ID, defaultValue, CORRELAZIONE_APPLICATIVA_VALUE_ENABLED, CORRELAZIONE_APPLICATIVA_VALUE_DISABLED);
	}
	public static boolean isCorrelazioneApplicativaRispostaIdentificativoEstrattoIsEmptyTerminaTransazioneConErrore(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, CORRELAZIONE_APPLICATIVA_RISPOSTA_PROPERTY_NAME_EXTRACTED_IDENTIFIER_IS_EMPTY_ABORT_TRANSACTION_ID, defaultValue, CORRELAZIONE_APPLICATIVA_VALUE_ENABLED, CORRELAZIONE_APPLICATIVA_VALUE_DISABLED);
	}
	
	public static boolean isCorrelazioneApplicativaRichiestaRegolaNonTrovataTerminaTransazioneConErrore(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, CORRELAZIONE_APPLICATIVA_RICHIESTA_PROPERTY_NAME_RULE_NOT_FOUND_ABORT_TRANSACTION_ID, defaultValue, CORRELAZIONE_APPLICATIVA_VALUE_ENABLED, CORRELAZIONE_APPLICATIVA_VALUE_DISABLED);
	}
	public static boolean isCorrelazioneApplicativaRispostaRegolaNonTrovataTerminaTransazioneConErrore(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, CORRELAZIONE_APPLICATIVA_RISPOSTA_PROPERTY_NAME_RULE_NOT_FOUND_ABORT_TRANSACTION_ID, defaultValue, CORRELAZIONE_APPLICATIVA_VALUE_ENABLED, CORRELAZIONE_APPLICATIVA_VALUE_DISABLED);
	}
	
	
	// ****  CONNETTORI *****
	
	public static final String CONNETTORE_VALUE_ENABLED = VALUE_ENABLED;
	public static final String CONNETTORE_VALUE_DISABLED = VALUE_DISABLED;
	
	private static final String CONNETTORE_TIMEOUT_INPUT_STREAM_ENABLED = "connettori.timeoutInputStream.enabled";
	private static final String CONNETTORE_TIMEOUT_INPUT_STREAM_REQUEST_TIMEOUT = "connettori.request.timeoutMs";
	
	public static boolean isConnettoriUseTimeoutInputStream(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, CONNETTORE_TIMEOUT_INPUT_STREAM_ENABLED, defaultValue, CONNETTORE_VALUE_ENABLED, CONNETTORE_VALUE_DISABLED);
	}
	public static int getConnettoriRequestTimeout(List<Proprieta> proprieta, int defaultValue) {
		return readIntValueWithDefault(proprieta, CONNETTORE_TIMEOUT_INPUT_STREAM_REQUEST_TIMEOUT, defaultValue);
	}
	public static boolean existsConnettoriRequestTimeout(List<Proprieta> proprieta) {
		return readIntValueWithDefault(proprieta, CONNETTORE_TIMEOUT_INPUT_STREAM_REQUEST_TIMEOUT, -1) > 0;
	}
	
	
	
	// ****  CONNETTORI PROXY PASS *****
	
	public static final String CONNETTORE_PROXY_PASS_VALUE_ENABLED = VALUE_ENABLED;
	public static final String CONNETTORE_PROXY_PASS_VALUE_DISABLED = VALUE_DISABLED;
		
	private static final String CONNETTORE_PROXY_PASS_REVERSE_ENABLED = "connettori.proxyPassReverse.enabled";
	private static final String CONNETTORE_PROXY_PASS_REVERSE_HEADERS = "connettori.proxyPassReverse.headers";
	
	private static final String CONNETTORE_PROXY_PASS_REVERSE_SETCOOKIE_ENABLED = "connettori.proxyPassReverse.setCookie.enabled";
	private static final String CONNETTORE_PROXY_PASS_REVERSE_SETCOOKIE_PATH_ENABLED = "connettori.proxyPassReverse.setCookie.path.enabled";
	private static final String CONNETTORE_PROXY_PASS_REVERSE_SETCOOKIE_DOMAIN_ENABLED = "connettori.proxyPassReverse.setCookie.domain.enabled";
	private static final String CONNETTORE_PROXY_PASS_REVERSE_SETCOOKIE_HEADERS = "connettori.proxyPassReverse.setCookie.headers";
	
	private static final String CONNETTORE_PROXY_PASS_REVERSE_USE_PROTOCOL_PREFIX = "connettori.proxyPassReverse.useProtocolPrefix";
	
	
	public static boolean isConnettoriProxyPassReverseEnabled(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, CONNETTORE_PROXY_PASS_REVERSE_ENABLED, defaultValue, CONNETTORE_PROXY_PASS_VALUE_ENABLED, CONNETTORE_PROXY_PASS_VALUE_DISABLED);
	}
	public static List<String> getConnettoriProxyPassReverseHeaders(List<Proprieta> proprieta, List<String> defaultValue) {
		return engineGetConnettoriProxyPassReverseHeaders(CONNETTORE_PROXY_PASS_REVERSE_HEADERS, proprieta, defaultValue);
	}
	
	public static boolean isConnettoriProxyPassReverseSetCookiePathEnabled(List<Proprieta> proprieta, boolean defaultValue) {
		String setCookieEnabled = readValue(proprieta, CONNETTORE_PROXY_PASS_REVERSE_SETCOOKIE_PATH_ENABLED);
		if(setCookieEnabled!=null && !"".equals(setCookieEnabled)) {
			if(CONNETTORE_PROXY_PASS_VALUE_ENABLED.equals(setCookieEnabled.trim())) {
				return true;
			}
			else if(CONNETTORE_PROXY_PASS_VALUE_DISABLED.equals(setCookieEnabled.trim())) {
				return false;
			}
		}
		return readBooleanValueWithDefault(proprieta, CONNETTORE_PROXY_PASS_REVERSE_SETCOOKIE_ENABLED, defaultValue, CONNETTORE_PROXY_PASS_VALUE_ENABLED, CONNETTORE_PROXY_PASS_VALUE_DISABLED);
	}
	public static boolean isConnettoriProxyPassReverseSetCookieDomainEnabled(List<Proprieta> proprieta, boolean defaultValue) {
		String setCookieEnabled = readValue(proprieta, CONNETTORE_PROXY_PASS_REVERSE_SETCOOKIE_DOMAIN_ENABLED);
		if(setCookieEnabled!=null && !"".equals(setCookieEnabled)) {
			if(CONNETTORE_PROXY_PASS_VALUE_ENABLED.equals(setCookieEnabled.trim())) {
				return true;
			}
			else if(CONNETTORE_PROXY_PASS_VALUE_DISABLED.equals(setCookieEnabled.trim())) {
				return false;
			}
		}
		return readBooleanValueWithDefault(proprieta, CONNETTORE_PROXY_PASS_REVERSE_SETCOOKIE_ENABLED, defaultValue, CONNETTORE_PROXY_PASS_VALUE_ENABLED, CONNETTORE_PROXY_PASS_VALUE_DISABLED);
	}
	public static List<String> getConnettoriProxyPassReverseSetCookieHeaders(List<Proprieta> proprieta, List<String> defaultValue) {
		return engineGetConnettoriProxyPassReverseHeaders(CONNETTORE_PROXY_PASS_REVERSE_SETCOOKIE_HEADERS, proprieta, defaultValue);
	}
	
	public static boolean isConnettoriProxyPassReverseUseProtocolPrefix(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, CONNETTORE_PROXY_PASS_REVERSE_USE_PROTOCOL_PREFIX, defaultValue, CONNETTORE_PROXY_PASS_VALUE_ENABLED, CONNETTORE_PROXY_PASS_VALUE_DISABLED);
	}
	
	private static List<String> engineGetConnettoriProxyPassReverseHeaders(String pName, List<Proprieta> proprieta, List<String> defaultValue) {
		String v = readValue(proprieta, pName);
		if(v==null || StringUtils.isEmpty(v)) {
			return defaultValue;
		}
		List<String> l = new ArrayList<>();
		if(v.contains(",")) {
			String [] tmp = v.split(",");
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].trim());
			}
		}
		else {
			l.add(v);
		}
		return l;
	}
	
	
	
	
	// ****  CONNETTORI HTTPS *****
	
	public static final String CONNETTORE_HTTPS_ENDPOINT_JVM_CONFIG_OVERRIDE_VALUE_ENABLED = VALUE_ENABLED;
	public static final String CONNETTORE_HTTPS_ENDPOINT_JVM_CONFIG_OVERRIDE_VALUE_DISABLED = VALUE_DISABLED;
	
	private static final String CONNETTORE_HTTPS_ENDPOINT_JVM_CONFIG_OVERRIDE_ENABLED = "connettori.httpsEndpoint.jvmConfigOverride.enabled";
	private static final String CONNETTORE_HTTPS_ENDPOINT_JVM_CONFIG_OVERRIDE_REPOSITORY = "connettori.httpsEndpoint.jvmConfigOverride.repository";
	private static final String CONNETTORE_HTTPS_ENDPOINT_JVM_CONFIG_OVERRIDE_CONFIG = "connettori.httpsEndpoint.jvmConfigOverride.config";
	
	public static boolean isConnettoriHttpsEndpointJvmConfigOverrideEnabled(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, CONNETTORE_HTTPS_ENDPOINT_JVM_CONFIG_OVERRIDE_ENABLED, defaultValue, CONNETTORE_HTTPS_ENDPOINT_JVM_CONFIG_OVERRIDE_VALUE_ENABLED, CONNETTORE_HTTPS_ENDPOINT_JVM_CONFIG_OVERRIDE_VALUE_DISABLED);
	}
	
	public static String getConnettoriHttpsEndpointJvmConfigOverrideRepository(List<Proprieta> proprieta, String defaultValue) {
		String v = readValue(proprieta, CONNETTORE_HTTPS_ENDPOINT_JVM_CONFIG_OVERRIDE_REPOSITORY);
		if(v==null || StringUtils.isEmpty(v)) {
			return defaultValue;
		}
		return v;
	}
	
	public static String getConnettoriHttpsEndpointJvmConfigOverrideConfig(List<Proprieta> proprieta, String defaultValue) {
		String v = readValue(proprieta, CONNETTORE_HTTPS_ENDPOINT_JVM_CONFIG_OVERRIDE_CONFIG);
		if(v==null || StringUtils.isEmpty(v)) {
			return defaultValue;
		}
		return v;
	}
	
	
	
	
	// ****  REGISTRAZIONE MESSAGGI *****
		
	private static final String REGISTRAZIONE_MESSAGGI_WHITE_LIST = "registrazioneMessaggi.whiteList";
	private static final String REGISTRAZIONE_MESSAGGI_BLACK_LIST = "registrazioneMessaggi.blackList";
	
	private static final String REGISTRAZIONE_MESSAGGI_RICHIESTA_INGRESSO_WHITE_LIST = "registrazioneMessaggi.richiestaIngresso.whiteList";
	private static final String REGISTRAZIONE_MESSAGGI_RICHIESTA_INGRESSO_BLACK_LIST = "registrazioneMessaggi.richiestaIngresso.blackList";
	
	private static final String REGISTRAZIONE_MESSAGGI_RICHIESTA_USCITA_WHITE_LIST = "registrazioneMessaggi.richiestaUscita.whiteList";
	private static final String REGISTRAZIONE_MESSAGGI_RICHIESTA_USCITA_BLACK_LIST = "registrazioneMessaggi.richiestaUscita.blackList";
	
	private static final String REGISTRAZIONE_MESSAGGI_RISPOSTA_INGRESSO_WHITE_LIST = "registrazioneMessaggi.rispostaIngresso.whiteList";
	private static final String REGISTRAZIONE_MESSAGGI_RISPOSTA_INGRESSO_BLACK_LIST = "registrazioneMessaggi.rispostaIngresso.blackList";
	
	private static final String REGISTRAZIONE_MESSAGGI_RISPOSTA_USCITA_WHITE_LIST = "registrazioneMessaggi.rispostaUscita.whiteList";
	private static final String REGISTRAZIONE_MESSAGGI_RISPOSTA_USCITA_BLACK_LIST = "registrazioneMessaggi.rispostaUscita.blackList";
	
	public static List<String> getRegistrazioneMessaggiWhiteList(List<Proprieta> proprieta) {
		return getRegistrazioneMessaggiLista(proprieta, REGISTRAZIONE_MESSAGGI_WHITE_LIST);
	}
	public static List<String> getRegistrazioneMessaggiBlackList(List<Proprieta> proprieta) {
		return getRegistrazioneMessaggiLista(proprieta, REGISTRAZIONE_MESSAGGI_BLACK_LIST);
	}
	
	public static List<String> getRegistrazioneMessaggiRichiestaIngressoWhiteList(List<Proprieta> proprieta) {
		return getRegistrazioneMessaggiLista(proprieta, REGISTRAZIONE_MESSAGGI_RICHIESTA_INGRESSO_WHITE_LIST);
	}
	public static List<String> getRegistrazioneMessaggiRichiestaIngressoBlackList(List<Proprieta> proprieta) {
		return getRegistrazioneMessaggiLista(proprieta, REGISTRAZIONE_MESSAGGI_RICHIESTA_INGRESSO_BLACK_LIST);
	}
	
	public static List<String> getRegistrazioneMessaggiRichiestaUscitaWhiteList(List<Proprieta> proprieta) {
		return getRegistrazioneMessaggiLista(proprieta, REGISTRAZIONE_MESSAGGI_RICHIESTA_USCITA_WHITE_LIST);
	}
	public static List<String> getRegistrazioneMessaggiRichiestaUscitaBlackList(List<Proprieta> proprieta) {
		return getRegistrazioneMessaggiLista(proprieta, REGISTRAZIONE_MESSAGGI_RICHIESTA_USCITA_BLACK_LIST);
	}
	
	public static List<String> getRegistrazioneMessaggiRispostaIngressoWhiteList(List<Proprieta> proprieta) {
		return getRegistrazioneMessaggiLista(proprieta, REGISTRAZIONE_MESSAGGI_RISPOSTA_INGRESSO_WHITE_LIST);
	}
	public static List<String> getRegistrazioneMessaggiRispostaIngressoBlackList(List<Proprieta> proprieta) {
		return getRegistrazioneMessaggiLista(proprieta, REGISTRAZIONE_MESSAGGI_RISPOSTA_INGRESSO_BLACK_LIST);
	}
	
	public static List<String> getRegistrazioneMessaggiRispostaUscitaWhiteList(List<Proprieta> proprieta) {
		return getRegistrazioneMessaggiLista(proprieta, REGISTRAZIONE_MESSAGGI_RISPOSTA_USCITA_WHITE_LIST);
	}
	public static List<String> getRegistrazioneMessaggiRispostaUscitaBlackList(List<Proprieta> proprieta) {
		return getRegistrazioneMessaggiLista(proprieta, REGISTRAZIONE_MESSAGGI_RISPOSTA_USCITA_BLACK_LIST);
	}
	
	private static List<String> getRegistrazioneMessaggiLista(List<Proprieta> proprieta, String propertyName) {
		
		List<String> l = new ArrayList<>();
		
		String v = readValue(proprieta, propertyName);
		if(v==null || StringUtils.isEmpty(v)) {
			return l;
		}
		
		initList(l, v);
		return l;
	}
	

	
	
	
	
	// ****  FILE TRACE *****
		
	private static final String FILE_TRACE_VALUE_ENABLED = VALUE_ENABLED;
	private static final String FILE_TRACE_VALUE_DISABLED = VALUE_DISABLED;
	
	private static final String FILE_TRACE_ENABLED = "fileTrace.enabled";
	
	private static final String FILE_TRACE_DUMP_BINARIO_ENABLED = "fileTrace.dumpBinario.enabled";
	private static final String FILE_TRACE_DUMP_BINARIO_PAYLOAD_ENABLED = "fileTrace.dumpBinario.payload.enabled";
	private static final String FILE_TRACE_DUMP_BINARIO_HEADERS_ENABLED = "fileTrace.dumpBinario.headers.enabled";
	
	private static final String FILE_TRACE_DUMP_BINARIO_CONNETTORE_ENABLED = "fileTrace.dumpBinario.connettore.enabled";
	private static final String FILE_TRACE_DUMP_BINARIO_CONNETTORE_PAYLOAD_ENABLED = "fileTrace.dumpBinario.connettore.payload.enabled";
	private static final String FILE_TRACE_DUMP_BINARIO_CONNETTORE_HEADERS_ENABLED = "fileTrace.dumpBinario.connettore.headers.enabled";

	
	private static final String FILE_TRACE_CONFIG = "fileTrace.config";
	
	public static boolean isFileTraceEnabled(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, FILE_TRACE_ENABLED, defaultValue, FILE_TRACE_VALUE_ENABLED, FILE_TRACE_VALUE_DISABLED);
	}
	
	public static boolean isFileTraceDumpBinarioPayloadEnabled(List<Proprieta> proprieta, boolean defaultValue) {
		String fileTraceEnabled = readValue(proprieta, FILE_TRACE_DUMP_BINARIO_PAYLOAD_ENABLED);
		if(fileTraceEnabled!=null && !"".equals(fileTraceEnabled)) {
			if(FILE_TRACE_VALUE_ENABLED.equals(fileTraceEnabled.trim())) {
				return true;
			}
			else if(FILE_TRACE_VALUE_DISABLED.equals(fileTraceEnabled.trim())) {
				return false;
			}
		}
		return readBooleanValueWithDefault(proprieta, FILE_TRACE_DUMP_BINARIO_ENABLED, defaultValue, FILE_TRACE_VALUE_ENABLED, FILE_TRACE_VALUE_DISABLED);
	}
	public static boolean isFileTraceDumpBinarioHeadersEnabled(List<Proprieta> proprieta, boolean defaultValue) {
		String fileTraceEnabled = readValue(proprieta, FILE_TRACE_DUMP_BINARIO_HEADERS_ENABLED);
		if(fileTraceEnabled!=null && !"".equals(fileTraceEnabled)) {
			if(FILE_TRACE_VALUE_ENABLED.equals(fileTraceEnabled.trim())) {
				return true;
			}
			else if(FILE_TRACE_VALUE_DISABLED.equals(fileTraceEnabled.trim())) {
				return false;
			}
		}
		return readBooleanValueWithDefault(proprieta, FILE_TRACE_DUMP_BINARIO_ENABLED, defaultValue, FILE_TRACE_VALUE_ENABLED, FILE_TRACE_VALUE_DISABLED);
	}
	
	public static boolean isFileTraceDumpBinarioConnettorePayloadEnabled(List<Proprieta> proprieta, boolean defaultValue) {
		String fileTraceEnabled = readValue(proprieta, FILE_TRACE_DUMP_BINARIO_CONNETTORE_PAYLOAD_ENABLED);
		if(fileTraceEnabled!=null && !"".equals(fileTraceEnabled)) {
			if(FILE_TRACE_VALUE_ENABLED.equals(fileTraceEnabled.trim())) {
				return true;
			}
			else if(FILE_TRACE_VALUE_DISABLED.equals(fileTraceEnabled.trim())) {
				return false;
			}
		}
		return readBooleanValueWithDefault(proprieta, FILE_TRACE_DUMP_BINARIO_CONNETTORE_ENABLED, defaultValue, FILE_TRACE_VALUE_ENABLED, FILE_TRACE_VALUE_DISABLED);
	}
	public static boolean isFileTraceDumpBinarioConnettoreHeadersEnabled(List<Proprieta> proprieta, boolean defaultValue) {
		String fileTraceEnabled = readValue(proprieta, FILE_TRACE_DUMP_BINARIO_CONNETTORE_HEADERS_ENABLED);
		if(fileTraceEnabled!=null && !"".equals(fileTraceEnabled)) {
			if(FILE_TRACE_VALUE_ENABLED.equals(fileTraceEnabled.trim())) {
				return true;
			}
			else if(FILE_TRACE_VALUE_DISABLED.equals(fileTraceEnabled.trim())) {
				return false;
			}
		}
		return readBooleanValueWithDefault(proprieta, FILE_TRACE_DUMP_BINARIO_CONNETTORE_ENABLED, defaultValue, FILE_TRACE_VALUE_ENABLED, FILE_TRACE_VALUE_DISABLED);
	}
	
	
	public static File getFileTraceConfig(List<Proprieta> proprieta, File defaultValue) throws CoreException {
		String v = readValue(proprieta, FILE_TRACE_CONFIG);
		if(v==null || StringUtils.isEmpty(v)) {
			return defaultValue;
		}
		
		File getTransazioniFileTraceConfig = new File(v);
		if(!getTransazioniFileTraceConfig.exists()) {
			String rootDir = OpenSPCoop2Properties.getInstance().getRootDirectory();
			if(rootDir!=null && !"".equals(rootDir)) {
				getTransazioniFileTraceConfig = new File(rootDir, v);
			}
		}
		
		checkFile(getTransazioniFileTraceConfig);
				
		return getTransazioniFileTraceConfig;
	}
	
	
	// ****  FILTRO DUPLICATI *****
	
	public static final String FILTRO_DUPLICATI_TEST_ENABLED = VALUE_ENABLED;
	public static final String FILTRO_DUPLICATI_TEST_DISABLED = VALUE_DISABLED;
	
	private static final String FILTRO_DUPLICATI_TEST = "duplicates-filter-test.enabled";
	
	public static boolean isFiltroDuplicatiTestEnabled(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, FILTRO_DUPLICATI_TEST, defaultValue, FILTRO_DUPLICATI_TEST_ENABLED, FILTRO_DUPLICATI_TEST_DISABLED);
	}
	
	
	
	
	// ****  INFORMAZIONI INTEGRAZIONE *****
	
	public static final String INFORMAZIONI_INTEGRAZIONE_VALUE_ENABLED = VALUE_ENABLED;
	public static final String INFORMAZIONI_INTEGRAZIONE_VALUE_DISABLED = VALUE_DISABLED;
		
	private static final String INFORMAZIONI_INTEGRAZIONE_ENABLED = "integrationInfo.enabled";
	private static final String INFORMAZIONI_INTEGRAZIONE_TYPE = "integrationInfo.type";
	private static final String INFORMAZIONI_INTEGRAZIONE_NAME = "integrationInfo.name";
	private static final String INFORMAZIONI_INTEGRAZIONE_ENCODE = "integrationInfo.encode";
	private static final String INFORMAZIONI_INTEGRAZIONE_REQUIRED = "integrationInfo.required";
		
	public static boolean isInformazioniIntegrazioneEnabled(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, INFORMAZIONI_INTEGRAZIONE_ENABLED, defaultValue, INFORMAZIONI_INTEGRAZIONE_VALUE_ENABLED, INFORMAZIONI_INTEGRAZIONE_VALUE_DISABLED);
	}
	public static InformazioniIntegrazioneSorgente getTipoInformazioniIntegrazione(List<Proprieta> proprieta, InformazioniIntegrazioneSorgente defaultValue) throws CoreException {
		String valueS = readValue(proprieta, INFORMAZIONI_INTEGRAZIONE_TYPE);
		if(valueS!=null && !StringUtils.isEmpty(valueS)) {
			try {
				return InformazioniIntegrazioneSorgente.valueOf(valueS);
			}catch(Exception t) {
				throw newTipoInformazioniIntegrazioneException(valueS);
			}
		}
		return defaultValue;
	}
	private static CoreException newTipoInformazioniIntegrazioneException(String valueS) {
		InformazioniIntegrazioneSorgente [] s = InformazioniIntegrazioneSorgente.values();
		StringBuilder sb = new StringBuilder();
		if(s!=null) {
			for (InformazioniIntegrazioneSorgente informazioniIntegrazioneSorgente : s) {
				if(sb.length()>0) {
					sb.append(", ");
				}
				sb.append(informazioniIntegrazioneSorgente);
			}
		}
		return newCoreException(valueS, INFORMAZIONI_INTEGRAZIONE_TYPE, sb.toString());
	}
	public static String getNomeSorgenteInformazioniIntegrazione(List<Proprieta> proprieta, String defaultValue) {
		String valueS = readValue(proprieta, INFORMAZIONI_INTEGRAZIONE_NAME);
		if(valueS!=null && !StringUtils.isEmpty(valueS)) {
			return valueS;
		}
		return defaultValue;
	}
	public static InformazioniIntegrazioneCodifica getTipoCodificaInformazioniIntegrazione(List<Proprieta> proprieta, InformazioniIntegrazioneCodifica defaultValue) throws CoreException {
		String valueS = readValue(proprieta, INFORMAZIONI_INTEGRAZIONE_ENCODE);
		if(valueS!=null && !StringUtils.isEmpty(valueS)) {
			try {
				return InformazioniIntegrazioneCodifica.valueOf(valueS);
			}catch(Exception t) {
				throw newTipoCodificaInformazioniIntegrazioneException(valueS);
			}
		}
		return defaultValue;
	}
	private static CoreException newTipoCodificaInformazioniIntegrazioneException(String valueS) {
		InformazioniIntegrazioneCodifica [] s = InformazioniIntegrazioneCodifica.values();
		StringBuilder sb = new StringBuilder();
		if(s!=null) {
			for (InformazioniIntegrazioneCodifica informazioniIntegrazioneCodifica : s) {
				if(sb.length()>0) {
					sb.append(", ");
				}
				sb.append(informazioniIntegrazioneCodifica);
			}
		}
		return newCoreException(valueS, INFORMAZIONI_INTEGRAZIONE_ENCODE, sb.toString());
	}
	public static boolean isInformazioniIntegrazioneRequired(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, INFORMAZIONI_INTEGRAZIONE_REQUIRED, defaultValue, INFORMAZIONI_INTEGRAZIONE_VALUE_ENABLED, INFORMAZIONI_INTEGRAZIONE_VALUE_DISABLED);
	}
	
	// ****  INFORMAZIONI INTEGRAZIONE RISPOSTA *****
	
	public static final String INFORMAZIONI_INTEGRAZIONE_RISPOSTA_VALUE_ENABLED = VALUE_ENABLED;
	public static final String INFORMAZIONI_INTEGRAZIONE_RISPOSTA_VALUE_DISABLED = VALUE_DISABLED;
	
	private static final String INFORMAZIONI_INTEGRAZIONE_RISPOSTA_ENABLED = "responseIntegrationInfo.enabled";
	private static final String INFORMAZIONI_INTEGRAZIONE_RISPOSTA_NAME = "responseIntegrationInfo.name";
	private static final String INFORMAZIONI_INTEGRAZIONE_RISPOSTA_ENCODE = "responseIntegrationInfo.encode";
	private static final String INFORMAZIONI_INTEGRAZIONE_RISPOSTA_REQUIRED = "responseIntegrationInfo.required";
		
	public static boolean isInformazioniIntegrazioneRispostaEnabled(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, INFORMAZIONI_INTEGRAZIONE_RISPOSTA_ENABLED, defaultValue, INFORMAZIONI_INTEGRAZIONE_RISPOSTA_VALUE_ENABLED, INFORMAZIONI_INTEGRAZIONE_RISPOSTA_VALUE_DISABLED);
	}
	public static String getNomeSorgenteInformazioniIntegrazioneRisposta(List<Proprieta> proprieta, String defaultValue) {
		String valueS = readValue(proprieta, INFORMAZIONI_INTEGRAZIONE_RISPOSTA_NAME);
		if(valueS!=null && !StringUtils.isEmpty(valueS)) {
			return valueS;
		}
		return defaultValue;
	}
	public static InformazioniIntegrazioneCodifica getTipoCodificaInformazioniIntegrazioneRisposta(List<Proprieta> proprieta, InformazioniIntegrazioneCodifica defaultValue) throws CoreException {
		String valueS = readValue(proprieta, INFORMAZIONI_INTEGRAZIONE_RISPOSTA_ENCODE);
		if(valueS!=null && !StringUtils.isEmpty(valueS)) {
			try {
				return InformazioniIntegrazioneCodifica.valueOf(valueS);
			}catch(Exception t) {
				throw newTipoCodificaInformazioniIntegrazioneRispostaException(valueS);
			}
		}
		return defaultValue;
	}
	private static CoreException newTipoCodificaInformazioniIntegrazioneRispostaException(String valueS) {
		InformazioniIntegrazioneCodifica [] s = InformazioniIntegrazioneCodifica.values();
		StringBuilder sb = new StringBuilder();
		if(s!=null) {
			for (InformazioniIntegrazioneCodifica informazioniIntegrazioneCodifica : s) {
				if(sb.length()>0) {
					sb.append(", ");
				}
				sb.append(informazioniIntegrazioneCodifica);
			}
		}
		return newCoreException(valueS, INFORMAZIONI_INTEGRAZIONE_RISPOSTA_ENCODE, sb.toString());
	}
	public static boolean isInformazioniIntegrazioneRispostaRequired(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, INFORMAZIONI_INTEGRAZIONE_RISPOSTA_REQUIRED, defaultValue, INFORMAZIONI_INTEGRAZIONE_RISPOSTA_VALUE_ENABLED, INFORMAZIONI_INTEGRAZIONE_RISPOSTA_VALUE_DISABLED);
	}
	
	
	
	// ****  SECURITY HEADERS *****
	
	public static final String SECURITY_HEADERS_VALUE_ENABLED = VALUE_ENABLED;
	public static final String SECURITY_HEADERS_VALUE_DISABLED = VALUE_DISABLED;
		
	private static final String SECURITY_HEADERS_ENABLED = "securityHeaders.enabled";
	private static final String SECURITY_HEADERS_DEFAULT_ENABLED = "securityHeaders.default";
	private static final String SECURITY_HEADERS_LIST = "securityHeaders"; // separati con la virgola
	private static final String SECURITY_HEADERS_PREFIX = "securityHeaders.";

	public static boolean isSecurityHeadersEnabled(List<Proprieta> proprieta, boolean defaultValue) {
		return readBooleanValueWithDefault(proprieta, SECURITY_HEADERS_ENABLED, defaultValue, SECURITY_HEADERS_VALUE_ENABLED, SECURITY_HEADERS_VALUE_DISABLED);
	}
	public static Map<String, String> getSecurityHeaders(List<Proprieta> proprieta, Properties defaultConfig) {
		
		Map<String, String> p = new HashMap<>();
		
		boolean useDefault = readBooleanValueWithDefault(proprieta, SECURITY_HEADERS_DEFAULT_ENABLED, true, SECURITY_HEADERS_VALUE_ENABLED, SECURITY_HEADERS_VALUE_DISABLED);
		if(useDefault) {
			for (Object oKey : defaultConfig.keySet()) {
				if(oKey instanceof String) {
					String key = (String) oKey;
					String value = defaultConfig.getProperty(key);
					if(StringUtils.isNotEmpty(key) &&
						value!=null && StringUtils.isNotEmpty(value)) {
						p.put(key, value);
					}
				}
			}
		}
	
		fillSecurityHeaders(p, proprieta);
		
		return p;
	}
	private static void fillSecurityHeaders(Map<String, String> p, List<Proprieta> proprieta) {
		String pLista = readValue(proprieta, SECURITY_HEADERS_LIST);
		if(pLista!=null && StringUtils.isNotEmpty(pLista)) {
			String [] tmp = pLista.split(",");
			if(tmp!=null && tmp.length>0) {
				for (String key : tmp) {
					addSecurityHeader(p, key, proprieta);
				}
			}
		}
	}
	private static void addSecurityHeader(Map<String, String> p, String key, List<Proprieta> proprieta) {
		if(key!=null) {
			key = key.trim();
			if(StringUtils.isNotEmpty(key)) {
				String pName = (SECURITY_HEADERS_PREFIX+key);
				String value = readValue(proprieta, pName);
				if(value!=null) {
					value = value.trim();
					p.put(key, value); // se gia' esiste viene sovrascritto
				}
			}
		}
	}
	
	
	
	
	
	// ****  MODI *****
	
	public static final String MODI_VALUE_ENABLED = VALUE_ENABLED;
	public static final String MODI_VALUE_DISABLED = VALUE_DISABLED;
	
	private static final String MODI_AUDIT_CLAIM_PREFIX = "modi.audit.";
	private static final String MODI_AUDIT_SUFFIX_TRACE_ENABLED = ".trace.enabled";
	private static final String MODI_AUDIT_SUFFIX_FORWARD_BACKEND_ENABLED = ".forwardBackend.enabled";
	private static final String MODI_AUDIT_SUFFIX_FORWARD_BACKEND = ".forwardBackend";

	public static boolean isModIAuditTraceEnabled(List<Proprieta> proprieta, String claim, boolean defaultValue) {
		String p = MODI_AUDIT_CLAIM_PREFIX+claim+MODI_AUDIT_SUFFIX_TRACE_ENABLED;
		return readBooleanValueWithDefault(proprieta, p, defaultValue, MODI_VALUE_ENABLED, MODI_VALUE_DISABLED);
	}
	public static boolean isModIAuditForwardBackendEnabled(List<Proprieta> proprieta, String claim, boolean defaultValue) {
		String p = MODI_AUDIT_CLAIM_PREFIX+claim+MODI_AUDIT_SUFFIX_FORWARD_BACKEND_ENABLED;
		return readBooleanValueWithDefault(proprieta, p, defaultValue, MODI_VALUE_ENABLED, MODI_VALUE_DISABLED);
	}
	public static String getModIAuditForwardBackend(List<Proprieta> proprieta, String claim, String defaultValue) {
		String p = MODI_AUDIT_CLAIM_PREFIX+claim+MODI_AUDIT_SUFFIX_FORWARD_BACKEND;
		String valueS = readValue(proprieta, p);
		if(valueS!=null && !StringUtils.isEmpty(valueS)) {
			return valueS;
		}
		return defaultValue;
	}
	
	
	
	
	
	// METODI DI UTILITA GENERICI
	
	private static CoreException newCoreException(String valueS, String property, String supportedValues) {
		return new CoreException("Uncorrect value '"+valueS+"' for property '"+property+"' (Supported values: "+supportedValues+")");
	}
	
	private static String readValue(List<Proprieta> proprieta, String nome) {
		if(proprieta==null || proprieta.isEmpty()) {
			return null;
		}
		for (Proprieta proprietaCheck : proprieta) {
			if(nome.equalsIgnoreCase(proprietaCheck.getNome())) {
				return proprietaCheck.getValore()!=null ? proprietaCheck.getValore().trim() : null;
			}
		}
		return null;
	}
	private static boolean readBooleanValueWithDefault(List<Proprieta> proprieta, String nome, boolean defaultValue, String trueValue, String falseValue) {
		String valueS = readValue(proprieta, nome);
		if(valueS!=null && !StringUtils.isEmpty(valueS)) {
			if(trueValue.equals(valueS.trim())) {
				return true;
			}
			else if(falseValue.equals(valueS.trim())) {
				return false;
			}
		}
		return defaultValue;
	}
	private static int readIntValueWithDefault(List<Proprieta> proprieta, String nome, int defaultValue) {
		String valueS = readValue(proprieta, nome);
		if(valueS!=null && !StringUtils.isEmpty(valueS)) {
			try {
				return Integer.valueOf(valueS);
			}catch(Exception e) {
				// ignore
			}
		}
		return defaultValue;
	}
	
	private static void initList(List<String> list, String tmp) {
		if(tmp!=null && !"".equals(tmp.trim())) {
			tmp = tmp.trim();
			if(tmp.contains(",")) {
				String [] split = tmp.split(",");
				initList(list, split);
			}
			else {
				list.add(tmp);
			}
		}
	}
	private static void initList(List<String> list, String [] split) {
		if(split!=null && split.length>0) {
			for (String s : split) {
				if(s!=null) {
					s = s.trim();
					if(!"".equals(s)) {
						list.add(s);
					}
				}
			}
		}
	}
	
	public static void checkFile(File file) throws CoreException {
		checkFileEngine(file, false);
	}
	public static void checkDir(File file) throws CoreException {
		checkFileEngine(file, true);
	}
	private static void checkFileEngine(File file, boolean expectedDir) throws CoreException {
		String prefix = "Config file ["+file.getAbsolutePath()+"] ";
		if(!file.exists()) {
			throw new CoreException(prefix+"not exists");
		}
		if(expectedDir) {
			if(!file.isDirectory()) {
				throw new CoreException(prefix+"isn't directory");
			}
		}
		else {
			if(file.isDirectory()) {
				throw new CoreException(prefix+"is directory");
			}
		}
		if(!file.canRead()) {
			throw new CoreException(prefix+"cannot read");
		}
	}
}
