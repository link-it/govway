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


package org.openspcoop2.pdd.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.Proprieta;

/**
 * Classe che raccoglie le proprieta
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class CostantiProprieta {

	
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
	
	
	
	// ****  CONNETTORI *****
	
	public static final String CONNETTORE_VALUE_ENABLED = VALUE_ENABLED;
	public static final String CONNETTORE_VALUE_DISABLED = VALUE_DISABLED;
	
	private static final String CONNETTORE_TIMEOUT_INPUT_STREAM_ENABLED = "connettori.timeoutInputStream.enabled";
	private static final String CONNETTORE_TIMEOUT_INPUT_STREAM_REQUEST_TIMEOUT = "connettori.request.timeoutMs";
	
	public static boolean isConnettoriUseTimeoutInputStream(List<Proprieta> proprieta, boolean defaultValue) throws Exception {
		return readBooleanValueWithDefault(proprieta, CONNETTORE_TIMEOUT_INPUT_STREAM_ENABLED, defaultValue, CONNETTORE_VALUE_ENABLED, CONNETTORE_VALUE_DISABLED);
	}
	public static int getConnettoriRequestTimeout(List<Proprieta> proprieta, int defaultValue) throws Exception {
		return readIntValueWithDefault(proprieta, CONNETTORE_TIMEOUT_INPUT_STREAM_REQUEST_TIMEOUT, defaultValue);
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
	
	
	public static boolean isConnettoriProxyPassReverseEnabled(List<Proprieta> proprieta, boolean defaultValue) throws Exception {
		return readBooleanValueWithDefault(proprieta, CONNETTORE_PROXY_PASS_REVERSE_ENABLED, defaultValue, CONNETTORE_PROXY_PASS_VALUE_ENABLED, CONNETTORE_PROXY_PASS_VALUE_DISABLED);
	}
	public static List<String> getConnettoriProxyPassReverseHeaders(List<Proprieta> proprieta, List<String> defaultValue) throws Exception {
		return _getConnettoriProxyPassReverseHeaders(CONNETTORE_PROXY_PASS_REVERSE_HEADERS, proprieta, defaultValue);
	}
	
	public static boolean isConnettoriProxyPassReverseSetCookiePathEnabled(List<Proprieta> proprieta, boolean defaultValue) throws Exception {
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
	public static boolean isConnettoriProxyPassReverseSetCookieDomainEnabled(List<Proprieta> proprieta, boolean defaultValue) throws Exception {
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
	public static List<String> getConnettoriProxyPassReverseSetCookieHeaders(List<Proprieta> proprieta, List<String> defaultValue) throws Exception {
		return _getConnettoriProxyPassReverseHeaders(CONNETTORE_PROXY_PASS_REVERSE_SETCOOKIE_HEADERS, proprieta, defaultValue);
	}
	
	public static boolean isConnettoriProxyPassReverseUseProtocolPrefix(List<Proprieta> proprieta, boolean defaultValue) throws Exception {
		return readBooleanValueWithDefault(proprieta, CONNETTORE_PROXY_PASS_REVERSE_USE_PROTOCOL_PREFIX, defaultValue, CONNETTORE_PROXY_PASS_VALUE_ENABLED, CONNETTORE_PROXY_PASS_VALUE_DISABLED);
	}
	
	private static List<String> _getConnettoriProxyPassReverseHeaders(String pName, List<Proprieta> proprieta, List<String> defaultValue) throws Exception {
		String v = readValue(proprieta, pName);
		if(v==null || StringUtils.isEmpty(v)) {
			return defaultValue;
		}
		List<String> l = new ArrayList<String>();
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
	
	
	
	
	// ****  FILE TRACE *****
		
	public static final String FILE_TRACE_VALUE_ENABLED = VALUE_ENABLED;
	public static final String FILE_TRACE_VALUE_DISABLED = VALUE_DISABLED;
	
	private static final String FILE_TRACE_ENABLED = "fileTrace.enabled";
	
	private static final String FILE_TRACE_DUMP_BINARIO_ENABLED = "fileTrace.dumpBinario.enabled";
	private static final String FILE_TRACE_DUMP_BINARIO_PAYLOAD_ENABLED = "fileTrace.dumpBinario.payload.enabled";
	private static final String FILE_TRACE_DUMP_BINARIO_HEADERS_ENABLED = "fileTrace.dumpBinario.headers.enabled";
	
	private static final String FILE_TRACE_DUMP_BINARIO_CONNETTORE_ENABLED = "fileTrace.dumpBinario.connettore.enabled";
	private static final String FILE_TRACE_DUMP_BINARIO_CONNETTORE_PAYLOAD_ENABLED = "fileTrace.dumpBinario.connettore.payload.enabled";
	private static final String FILE_TRACE_DUMP_BINARIO_CONNETTORE_HEADERS_ENABLED = "fileTrace.dumpBinario.connettore.headers.enabled";

	
	private static final String FILE_TRACE_CONFIG = "fileTrace.config";
	
	public static boolean isFileTraceEnabled(List<Proprieta> proprieta, boolean defaultValue) throws Exception {
		return readBooleanValueWithDefault(proprieta, FILE_TRACE_ENABLED, defaultValue, FILE_TRACE_VALUE_ENABLED, FILE_TRACE_VALUE_DISABLED);
	}
	
	public static boolean isFileTraceDumpBinarioPayloadEnabled(List<Proprieta> proprieta, boolean defaultValue) throws Exception {
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
	public static boolean isFileTraceDumpBinarioHeadersEnabled(List<Proprieta> proprieta, boolean defaultValue) throws Exception {
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
	
	public static boolean isFileTraceDumpBinarioConnettorePayloadEnabled(List<Proprieta> proprieta, boolean defaultValue) throws Exception {
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
	public static boolean isFileTraceDumpBinarioConnettoreHeadersEnabled(List<Proprieta> proprieta, boolean defaultValue) throws Exception {
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
	
	
	public static File getFileTraceConfig(List<Proprieta> proprieta, File defaultValue) throws Exception {
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
		
		if(!getTransazioniFileTraceConfig.exists()) {
			throw new Exception("Config file ["+getTransazioniFileTraceConfig.getAbsolutePath()+"] not exists");
		}
		if(getTransazioniFileTraceConfig.isDirectory()) {
			throw new Exception("Config file ["+getTransazioniFileTraceConfig.getAbsolutePath()+"] is directory");
		}
		if(getTransazioniFileTraceConfig.canRead()==false) {
			throw new Exception("Config file ["+getTransazioniFileTraceConfig.getAbsolutePath()+"] cannot read");
		}
		
		return getTransazioniFileTraceConfig;
	}
	
	
	// ****  FILTRO DUPLICATI *****
	
	public static final String FILTRO_DUPLICATI_TEST_ENABLED = VALUE_ENABLED;
	public static final String FILTRO_DUPLICATI_TEST_DISABLED = VALUE_DISABLED;
	
	private static final String FILTRO_DUPLICATI_TEST = "duplicates-filter-test.enabled";
	
	public static boolean isFiltroDuplicatiTestEnabled(List<Proprieta> proprieta, boolean defaultValue) throws Exception {
		return readBooleanValueWithDefault(proprieta, FILTRO_DUPLICATI_TEST, defaultValue, FILTRO_DUPLICATI_TEST_ENABLED, FILTRO_DUPLICATI_TEST_DISABLED);
	}
	
	
	
	// METODI DI UTILITA GENERICI
	
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
			}catch(Throwable e) {}
		}
		return defaultValue;
	}
}
