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

package org.openspcoop2.utils.rest;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiBodyParameter;
import org.openspcoop2.utils.rest.api.ApiCookieParameter;
import org.openspcoop2.utils.rest.api.ApiHeaderParameter;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.api.ApiParameterSchema;
import org.openspcoop2.utils.rest.api.ApiParameterSchemaComplexType;
import org.openspcoop2.utils.rest.api.ApiParameterTypeSchema;
import org.openspcoop2.utils.rest.api.ApiRequestDynamicPathParameter;
import org.openspcoop2.utils.rest.api.ApiRequestFormParameter;
import org.openspcoop2.utils.rest.api.ApiRequestQueryParameter;
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.openspcoop2.utils.rest.api.ApiSchemaTypeRestriction;
import org.openspcoop2.utils.rest.api.ApiUtilities;
import org.openspcoop2.utils.rest.entity.Cookie;
import org.openspcoop2.utils.rest.entity.HttpBaseEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseRequestEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseResponseEntity;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.slf4j.Logger;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * ApiValidatorConfig
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractApiValidator   {

	// keyword '$ref' usato negli schemi JSON Schema / OpenAPI.
	// Valore identico a org.openapi4j.core.model.v3.OAI3SchemaKeywords.$REF,
	// inlineato qui per mantenere utils/rest/ disaccoppiato da openapi4j.
	private static final String REF_KEYWORD = "$ref";

	// Il nome (gruppo 1) esclude sia ';' che '=': cosi' '[^;=]+' e il separatore '=' sono disgiunti,
	// eliminando ogni backtracking (evita la segnalazione ReDoS "polynomial runtime").
	private static final Pattern PREFIXED_SEMICOLON_NAME_REGEX = Pattern.compile("(?:;)([^;=]+)(?:=)([^;]*)");

	/** Api corrente. Valorizzata dalle sottoclassi nel loro init. */
	protected Api api;

	/** Logger corrente. Valorizzato dalle sottoclassi nel loro init. */
	protected Logger log;

	public abstract void validatePreConformanceCheck(HttpBaseEntity<?> httpEntity,ApiOperation operation,Object ... args) throws ProcessingException,ValidatorException;

	/**
	 * Default no-op. Override nelle sottoclassi solo se servono check post-validazione
	 * (es. nomi di elementi in un XSD).
	 */
	public void validatePostConformanceCheck(HttpBaseEntity<?> httpEntity,ApiOperation operation,Object ... args) throws ProcessingException,ValidatorException {
		// no-op di default
	}

	/**
	 * Valida una HttpBaseEntity usando l'Api cachata sull'istanza (this.api).
	 * Utility per consumer dell'interfaccia IApiValidator; delega a validate(Api, ...).
	 */
	public void validate(HttpBaseEntity<?> httpEntity) throws ProcessingException, ValidatorException {
		List<Object> args = new ArrayList<>();
		this.validate(this.api, httpEntity, args);
	}

	public void validate(Api api, HttpBaseEntity<?> httpEntity, Object ... args) throws ProcessingException,ValidatorException{

		ApiOperation operation = api.findOperation(httpEntity.getMethod(), httpEntity.getUrl());
		if(operation==null){
			throw new ProcessingException("Resource "+httpEntity.getMethod()+" '"+httpEntity.getUrl()+"' not found");
		}

		// es. xsd
		validatePreConformanceCheck(httpEntity, operation, args);

		validateConformanceCheck(httpEntity, operation, api.getBaseURL());

		// es. elementi specifici come nomi nel xsd etc..
		validatePostConformanceCheck(httpEntity, operation, args);

	}

	private void validateConformanceCheck(HttpBaseEntity<?> httpEntity,ApiOperation operation, URL baseUrl) throws ProcessingException,ValidatorException{

		try{



			if(httpEntity.getContentType() != null) {

				String baseTypeHttp = ContentTypeUtilities.readBaseTypeFromContentType(httpEntity.getContentType());

				boolean contentTypeSupported = false;
				List<ApiBodyParameter> requestBodyParametersList = null;
				List<ApiResponse> responses = null;
				if(operation.getRequest()!=null &&  operation.getRequest().sizeBodyParameters()>0){
					requestBodyParametersList = operation.getRequest().getBodyParameters();
				}
				if(operation.sizeResponses()>0){
					responses = operation.getResponses();
				}
				int status = -1;

				if(httpEntity instanceof HttpBaseRequestEntity<?>) {

					if(requestBodyParametersList != null) {
						for(ApiBodyParameter input: requestBodyParametersList) {
							if(input.isAllMediaType() || ContentTypeUtilities.isMatch(null, baseTypeHttp, input.getMediaType())) {
								contentTypeSupported = true;
								break;
							}
						}
					}
				} else if(httpEntity instanceof HttpBaseResponseEntity<?>) {
					status = ((HttpBaseResponseEntity<?>)httpEntity).getStatus();

					if(responses != null) {

						// Fix: se si traccia del codice http esatto non devo andare a verificare il codice di default

						// prima faccio la verifica con codice esatto
						ApiResponse outputDefault = null;
						boolean findExactResponseCode = false;
						for(ApiResponse output: responses) {
							if(status==output.getHttpReturnCode()) {
								findExactResponseCode = true;
								if(output.sizeBodyParameters()>0) {
									for(ApiBodyParameter outputBodyParameter: output.getBodyParameters()) {
										if(outputBodyParameter.isAllMediaType() || ContentTypeUtilities.isMatch(null, baseTypeHttp, outputBodyParameter.getMediaType()) ) {
											contentTypeSupported = true;
											break;
										}
									}
								}
							}
							else if(output.isDefaultHttpReturnCode()) {
								outputDefault = output;
							}
						}

						// poi con l'eventuale default
						if(!contentTypeSupported && !findExactResponseCode && outputDefault!=null) {
							if(outputDefault.sizeBodyParameters()>0) {
								for(ApiBodyParameter outputBodyParameter: outputDefault.getBodyParameters()) {
									if(outputBodyParameter.isAllMediaType() || ContentTypeUtilities.isMatch(null, baseTypeHttp, outputBodyParameter.getMediaType()) ) {
										contentTypeSupported = true;
										break;
									}
								}
							}
						}
					}

				}

				if(!contentTypeSupported) {
					if(status>0)
						throw new ValidatorException("Content-Type '"+baseTypeHttp+"' (http response status '"+status+"') unsupported");
					else
						throw new ValidatorException("Content-Type '"+baseTypeHttp+"' unsupported");
				}
			}
			else {

				// senza content-type

				if(httpEntity instanceof HttpBaseRequestEntity<?>) {

					if(operation.getRequest()!=null &&  operation.getRequest().sizeBodyParameters()>0){

						boolean required = false;
						for(ApiBodyParameter input: operation.getRequest().getBodyParameters()) {
							if(input.isRequired()) {
								required = true;
							}
						}
						if(required) {
							throw new ValidatorException("Request without payload (Content-Type 'null') unsupported");
						}

					}

				}


			}

			if(httpEntity instanceof HttpBaseRequestEntity<?>) {

				HttpBaseRequestEntity<?> request = (HttpBaseRequestEntity<?>) httpEntity;

				if(operation.getRequest()!=null &&  operation.getRequest().sizeHeaderParameters()>0){
					for (ApiHeaderParameter paramHeader : operation.getRequest().getHeaderParameters()) {
						String name = paramHeader.getName();
						List<String> values = TransportUtils.getRawObject(request.getHeaders(), name);
						if(values==null || values.isEmpty()){
							if(paramHeader.isRequired()){
								throw new ValidatorException("Required http header '"+name+"' not found");
							}
						}
						else {
							for (String value : values) {
								if(value!=null){
									validate(paramHeader.getApiParameterSchema(), ApiParameterType.header, name, value, "http header");
								}
							}
						}
					}
				}

				if(operation.getRequest()!=null &&  operation.getRequest().sizeCookieParameters()>0){
					for (ApiCookieParameter paramCookie : operation.getRequest().getCookieParameters()) {
						String name = paramCookie.getName();
						String value = null;
						if(request.getCookies()!=null){
							for (Cookie cookie : request.getCookies()) {
								if(name.equalsIgnoreCase(cookie.getName())){
									value = cookie.getValue();
								}
							}
						}
						if(value==null){
							if(paramCookie.isRequired()){
								throw new ValidatorException("Required Cookie '"+name+"' not found");
							}
						}
						if(value!=null){
							validate(paramCookie.getApiParameterSchema(), ApiParameterType.cookie, name, value, "cookie");
						}
					}
				}

				if(operation.getRequest()!=null &&  operation.getRequest().sizeQueryParameters()>0){
					for (ApiRequestQueryParameter paramQuery : operation.getRequest().getQueryParameters()) {

						// i parametri sono esplosi. La validazione viene fatta con json o openapi
						boolean parametriEsplosi = false;
						if(paramQuery.getApiParameterSchema()!=null && paramQuery.getApiParameterSchema().getSchemas()!=null && !paramQuery.getApiParameterSchema().getSchemas().isEmpty()) {
							for (ApiParameterTypeSchema schema : paramQuery.getApiParameterSchema().getSchemas()) {
								// Il controllo basta farlo sul primo.
								if(schema.getSchema()!=null && schema.getSchema().isTypeObject()) {
									if (
											(schema.getSchema().isStyleQueryForm() && schema.getSchema().isExplodeEnabled())
											||
											(schema.getSchema().isStyleQueryDeepObject())
											) {
										parametriEsplosi = true;
										break;
									}
								}
							}
						}
						if(parametriEsplosi) {
							continue;
						}

						String name = paramQuery.getName();
						List<String> values = TransportUtils.getRawObject(request.getParameters(), name);
						if(values==null || values.isEmpty()){
							if(paramQuery.isRequired()){
								throw new ValidatorException("Required query parameter '"+name+"' not found");
							}
						}
						else {
							for (String value : values) {
								if(value!=null){
									validate(paramQuery.getApiParameterSchema(), ApiParameterType.query, name, value, "query parameter");
								}
							}
						}
					}
				}

				if(operation.getRequest()!=null &&  operation.getRequest().sizeDynamicPathParameters()>0){
					for (ApiRequestDynamicPathParameter paramDynamicPath : operation.getRequest().getDynamicPathParameters()) {
						boolean find = false;
						String valueFound = null;
						if(operation.isDynamicPath()){
							for (int i = 0; i < operation.sizePath(); i++) {
								if(operation.isDynamicPath()){
									String idDinamic = operation.getDynamicPathId(i);
									if(paramDynamicPath.getName().equals(idDinamic)){
										String [] urlList = ApiUtilities.extractUrlList(baseUrl, request.getUrl());
										if(i>=urlList.length){
											throw new ValidatorException("Dynamic path '"+paramDynamicPath.getName()+"' not found (position:"+i+", urlLenght:"+urlList.length+")");
										}
										find = true;
										valueFound = urlList[i];
										break;
									}
								}
							}
						}
						if(!find && paramDynamicPath.isRequired()){
							throw new ValidatorException("Required dynamic path '"+paramDynamicPath.getName()+"' not found");
						}
						if(find){
							String valueUrlDecoded = valueFound;
							// il valore può essere url encoded
							try {
								// Note that Java’s URLEncoder class encodes space character(" ") into a + sign.
								// This is contrary to other languages like Javascript that encode space character into %20.
								/*
								 *  URLEncoder is not for encoding URLs, but for encoding parameter names and values for use in GET-style URLs or POST forms.
								 *  That is, for transforming plain text into the application/x-www-form-urlencoded MIME format as described in the HTML specification.
								 **/
								//valueUrlDecoded = java.net.URLDecoder.decode(valueFound, org.openspcoop2.utils.resources.Charset.UTF_8.getValue());

								valueUrlDecoded = UriUtils.decode(valueFound, org.openspcoop2.utils.resources.Charset.UTF_8.getValue());

								//System.out.println("DOPO '"+valueUrlDecoded+"' PRIMA '"+valueFound+"'");

							}catch(Throwable e) {
								//System.out.println("ERRORE");
								//e.printStackTrace(System.out);
								// utilizzo valore originale
								//throw new RuntimeException(e.getMessage(),e);
							}
							validate(paramDynamicPath.getApiParameterSchema(), ApiParameterType.path, paramDynamicPath.getName(), valueUrlDecoded, "dynamic path",
									(valueUrlDecoded!=null && !valueUrlDecoded.equals(valueFound)) ? valueFound : null);
						}
					}
				}

				if(operation.getRequest()!=null &&  operation.getRequest().sizeFormParameters()>0){
					for (ApiRequestFormParameter paramForm : operation.getRequest().getFormParameters()) {
						String name = paramForm.getName();
						List<String> values = TransportUtils.getRawObject(request.getParameters(), name);
						if(values==null || values.isEmpty()){
							if(paramForm.isRequired()){
								throw new ValidatorException("Required form parameter '"+name+"' not found");
							}
						}
						else {
							for (String value : values) {
								if(value!=null){
									validate(paramForm.getApiParameterSchema(), ApiParameterType.form, name, value, "form parameter");
								}
							}
						}
					}
				}

			}

			if(httpEntity instanceof HttpBaseResponseEntity<?>) {

				HttpBaseResponseEntity<?> response = (HttpBaseResponseEntity<?>) httpEntity;
				ApiResponse apiResponseFound = null;
				ApiResponse apiResponseDefault = null;

				for (ApiResponse apiResponse : operation.getResponses()) {
					if(apiResponse.isDefaultHttpReturnCode()) {
						apiResponseDefault = apiResponse;
					}
					if(response.getStatus() == apiResponse.getHttpReturnCode()){
						apiResponseFound = apiResponse;
						break;
					}
				}

				if(apiResponseFound==null && apiResponseDefault!=null) {
					apiResponseFound = apiResponseDefault;
				}
				if(apiResponseFound==null){
					throw new ValidatorException("Http status code '"+response.getStatus()+"' unsupported");
				}

				if(apiResponseFound.sizeHeaderParameters()>0){
					for (ApiHeaderParameter paramHeader : apiResponseFound.getHeaderParameters()) {
						String name = paramHeader.getName();
						List<String> values = TransportUtils.getRawObject(response.getHeaders(), name);
						if(values==null || values.isEmpty()){
							if(paramHeader.isRequired()){
								throw new ValidatorException("Required http header '"+name+"' not found");
							}
						}
						else {
							for (String value : values) {
								if(value!=null){
									validate(paramHeader.getApiParameterSchema(), ApiParameterType.header, name, value, "http header");
								}
							}
						}
					}
				}

				if(apiResponseFound.sizeCookieParameters()>0){
					for (ApiCookieParameter paramCookie : apiResponseFound.getCookieParameters()) {
						String name = paramCookie.getName();
						String value = null;
						if(response.getCookies()!=null){
							for (Cookie cookie : response.getCookies()) {
								if(name.equalsIgnoreCase(cookie.getName())){
									value = cookie.getValue();
								}
							}
						}
						if(value==null){
							if(paramCookie.isRequired()){
								throw new ValidatorException("Required cookie '"+name+"' not found");
							}
						}
						if(value!=null){
							validate(paramCookie.getApiParameterSchema(), ApiParameterType.cookie, name, value, "cookie");
						}
					}
				}

			}

		}catch(ProcessingException e){
			throw e;
		}catch(ValidatorException e){
			throw e;
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}

	}


	private void validate(ApiParameterSchema apiParameterSchema, ApiParameterType type, String name, String value, String position) throws ValidatorException, ProcessingException {
		this.validate(apiParameterSchema, type, name, value, position, null);
	}
	private void validate(ApiParameterSchema apiParameterSchema, ApiParameterType type, String name, String value, String position, String valueUrlEncoded) throws ValidatorException, ProcessingException {
		if(apiParameterSchema!=null && apiParameterSchema.getSchemas()!=null && !apiParameterSchema.getSchemas().isEmpty()) {
			ApiParameterSchemaComplexType ct = apiParameterSchema.getComplexType();
			if(ct==null) {
				ct = ApiParameterSchemaComplexType.simple;
			}

			String prefixError = "Invalid value '"+value+"' in "+position+" '"+name+"'";
			if(valueUrlEncoded!=null) {
				 prefixError = "Invalid value '"+value+"' (urlEncoded: '"+valueUrlEncoded+"') in "+position+" '"+name+"'";
			}

			switch (ct) {
			case simple: // caso speciale con 1 solo
			case allOf:
				StringBuilder sbException = new StringBuilder();
				List<ValidatorException> listValidatorException = new ArrayList<>();
				for (ApiParameterTypeSchema schema : apiParameterSchema.getSchemas()) {
					try{
						validateValueAsType(type, value, schema.getType(),schema.getSchema());
					}catch(ValidatorException val){
						String msgError = prefixError+" (expected type '"+schema.getType()+"'): "+val.getMessage();
						if(sbException.length()>0) {
							sbException.append("\n");
						}
						sbException.append(msgError);
						listValidatorException.add( new ValidatorException(msgError,val) );
					}
				}
				if(!listValidatorException.isEmpty()) {
					UtilsMultiException multi = new UtilsMultiException(listValidatorException.toArray(new ValidatorException[1]));
					throw new ValidatorException(sbException.toString(), multi);
				}
				return;

			case anyOf:
				sbException = new StringBuilder();
				listValidatorException = new ArrayList<>();
				for (ApiParameterTypeSchema schema : apiParameterSchema.getSchemas()) {
					try{
						validateValueAsType(type, value, schema.getType(),schema.getSchema());
						return; // ne basta una che valida correttamente
					}catch(ValidatorException val){
						String msgError = prefixError+" (expected type '"+schema.getType()+"'): "+val.getMessage();
						if(sbException.length()>0) {
							sbException.append("\n");
						}
						sbException.append(msgError);
						listValidatorException.add( new ValidatorException(msgError,val) );
					}
				}
				if(!listValidatorException.isEmpty()) {
					UtilsMultiException multi = new UtilsMultiException(listValidatorException.toArray(new ValidatorException[1]));
					throw new ValidatorException(sbException.toString(), multi);
				}
				return; //caso che non può accadere

			case oneOf:
				sbException = new StringBuilder();
				listValidatorException = new ArrayList<>();
				int schemiValidi = 0;
				for (ApiParameterTypeSchema schema : apiParameterSchema.getSchemas()) {
					try{
						validateValueAsType(type, value, schema.getType(),schema.getSchema());
						schemiValidi++;
					}catch(ValidatorException val){
						String msgError = prefixError+" (expected type '"+schema.getType()+"'): "+val.getMessage();
						if(sbException.length()>0) {
							sbException.append("\n");
						}
						sbException.append(msgError);
						listValidatorException.add( new ValidatorException(msgError,val) );
					}
				}
				if(schemiValidi==0) {
					if(!listValidatorException.isEmpty()) {
						UtilsMultiException multi = new UtilsMultiException(listValidatorException.toArray(new ValidatorException[1]));
						throw new ValidatorException(sbException.toString(), multi);
					}
					else {
						throw new ValidatorException(prefixError);
					}
				}
				else if(schemiValidi>1) {
					throw new ValidatorException(prefixError+": expected validates the value against exactly one of the subschemas; founded valid in "+schemiValidi+" schemas");
				}
				return; // deve essere validato rispetto esattamente ad uno schema

			}
		}



	}


	// =========================================================================
	// Helpers comuni condivisi fra tutti gli engine (openapi4j / swagger / json_schema).
	// Spostati qui per eliminare la duplicazione nelle sottoclassi.
	// =========================================================================

	/**
	 * Estrae i body parameter pertinenti in base al tipo di HttpBaseEntity (request o response,
	 * con eventuale matching su status code).
	 */
	protected List<ApiBodyParameter> getBodyParameters(HttpBaseEntity<?> httpEntity, ApiOperation operation) {
		List<ApiBodyParameter> bodyParameters = null;
		if (httpEntity instanceof HttpBaseRequestEntity) {
			if (operation.getRequest() != null) {
				bodyParameters = operation.getRequest().getBodyParameters();
			}
		} else if (httpEntity instanceof HttpBaseResponseEntity<?>) {
			ApiResponse apiResponseFound = null;
			ApiResponse apiResponseDefault = null;

			if (operation.getResponses() != null) {
				for (ApiResponse apiResponse : operation.getResponses()) {
					if (apiResponse.isDefaultHttpReturnCode()) {
						apiResponseDefault = apiResponse;
					}
					HttpBaseResponseEntity<?> response = (HttpBaseResponseEntity<?>) httpEntity;
					if (response.getStatus() == apiResponse.getHttpReturnCode()) {
						apiResponseFound = apiResponse;
						break;
					}
				}
			}

			if (apiResponseFound == null && apiResponseDefault != null) {
				apiResponseFound = apiResponseDefault;
			}
			if (apiResponseFound != null) {
				// eventuali errori di stato non trovato sono gestiti successivamente nella validazione
				bodyParameters = apiResponseFound.getBodyParameters();
			}
		}
		return bodyParameters;
	}

	/**
	 * Ritorna la parte path di un riferimento '$ref' esterno a file (prima del '#'),
	 * oppure null se il ref è interno al documento corrente.
	 */
	protected String getRefPath(String ref) {
		if (ref.trim().startsWith("#")) {
			return null;
		}
		return ref.trim().substring(0, ref.indexOf("#"));
	}

	/**
	 * Ritorna la parte di tipo (dopo il '#') di un riferimento '$ref'.
	 */
	protected static String getRefType(String ref) {
		if (ref.trim().startsWith("#")) {
			return ref;
		}
		return ref.trim().substring(ref.indexOf("#"), ref.length());
	}

	/**
	 * Normalizza un path a un nome file atomico (rimuove qualunque prefisso http/https/file e dirs).
	 */
	protected String normalizePath(String path) throws ProcessingException {
		if (path.startsWith(org.openspcoop2.utils.Costanti.PROTOCOL_HTTP_PREFIX)
				|| path.startsWith(org.openspcoop2.utils.Costanti.PROTOCOL_HTTPS_PREFIX)
				|| path.startsWith(org.openspcoop2.utils.Costanti.PROTOCOL_FILE_PREFIX)) {
			try {
				URL url = new URI(path).toURL();
				File fileUrl = new File(url.getFile());
				return fileUrl.getName();
			} catch (Exception e) {
				throw new ProcessingException(e.getMessage(), e);
			}
		} else {
			File f = new File(path);
			return f.getName();
		}
	}

	/**
	 * Riscrive tutti i '$ref' verso file esterni sostituendoli con un puntamento al nome
	 * atomico restituito da {@link #normalizePath(String)}.
	 */
	protected void normalizeRefs(JsonNode node) throws ProcessingException {
		List<JsonNode> listRef = node.findParents(REF_KEYWORD);
		if(listRef!=null && !listRef.isEmpty()) {
			for (int i = 0; i < listRef.size(); i++) {
				JsonNode jsonNodeRef = listRef.get(i);
				if(jsonNodeRef instanceof ObjectNode) {
					ObjectNode oNode = (ObjectNode) jsonNodeRef;
					JsonNode valore = oNode.get(REF_KEYWORD);
					if(valore == null || !valore.isTextual()) {
						// '$ref' usato come NOME di proprieta' di uno schema (valore non stringa): non e' un JSON Reference, salto
						continue;
					}
					normalizeRefs(valore, oNode);
				}
			}
		}
	}
	private void normalizeRefs(JsonNode valore, ObjectNode oNode) throws ProcessingException {
		String ref = valore.asText();
		String path = getRefPath(ref);
		if(path!=null) {
			String normalizePath = normalizePath(path);
			String refType = getRefType(ref);
			/**System.out.println("REF ("+jsonNodeRef.getClass().getName()+") : "+jsonNodeRef);
			System.out.println("Tipo ("+refType+") VALORE:"+normalizePath);*/
			oNode.remove(REF_KEYWORD);
			oNode.put(REF_KEYWORD, normalizePath+refType);
		}
	}

	/**
	 * Validazione di tipo di un singolo valore (primitivo o stringa) rispetto ad un type string
	 * (string, integer, boolean, date-time, uuid, anyURI, ecc.) con eventuale restrizione
	 * (min/max, enum, pattern, length, multipleOf, style per array).
	 *
	 * Logica identica per tutti gli engine REST; le sottoclassi possono override se servono
	 * comportamenti custom, ma nessuna delle implementazioni attuali lo fa.
	 */
	public void validateValueAsType(ApiParameterType parameterType, String value, String type,
			ApiSchemaTypeRestriction typeRestriction) throws ValidatorException {

		if (type != null) {
			type = type.trim();

			BigDecimal numberValue = null;
			String stringValue = null;

			if ("string".equalsIgnoreCase(type)) {
				stringValue = value;
			} else if ("byte".equalsIgnoreCase(type) || "unsignedByte".equalsIgnoreCase(type)) {
				try {
					byte v = Byte.parseByte(value);
					numberValue = new BigDecimal(v);
				} catch (Throwable e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else if ("char".equalsIgnoreCase(type)) {
				if (value.length() > 1) {
					throw new ValidatorException("More than one character");
				}
				stringValue = value;
			} else if ("double".equalsIgnoreCase(type) || "decimal".equalsIgnoreCase(type)) {
				try {
					double v = Double.parseDouble(value);
					numberValue = BigDecimal.valueOf(v);
				} catch (Throwable e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else if ("float".equalsIgnoreCase(type)) {
				try {
					float v = Float.parseFloat(value);
					numberValue = BigDecimal.valueOf(v);
				} catch (Throwable e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else if ("int".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type)
					|| "positiveInteger".equalsIgnoreCase(type) || "negativeInteger".equalsIgnoreCase(type)
					|| "nonPositiveInteger".equalsIgnoreCase(type) || "nonNegativeInteger".equalsIgnoreCase(type)
					|| "unsignedInt".equalsIgnoreCase(type) || "int32".equalsIgnoreCase(type)) {
				try {
					int i = Integer.parseInt(value);
					if ("positiveInteger".equalsIgnoreCase(type)) {
						if (i <= 0)
							throw new ValidatorException("Expected a positive value");
					} else if ("nonNegativeInteger".equalsIgnoreCase(type)) {
						if (i < 0)
							throw new ValidatorException("Expected a non negative value");
					} else if ("negativeInteger".equalsIgnoreCase(type)) {
						if (i >= 0)
							throw new ValidatorException("Expected a negative value");
					} else if ("nonPositiveInteger".equalsIgnoreCase(type)) {
						if (i > 0)
							throw new ValidatorException("Expected a non positive value");
					} else if ("unsignedInt".equalsIgnoreCase(type)) {
						if (i < 0)
							throw new ValidatorException("Expected a unsigned value");
					}
					numberValue = BigDecimal.valueOf(i);
				} catch (Throwable e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else if ("long".equalsIgnoreCase(type) || "unsignedLong".equalsIgnoreCase(type)
					|| "int64".equalsIgnoreCase(type)) {
				try {
					long l = Long.parseLong(value);
					if ("unsignedLong".equalsIgnoreCase(type)) {
						if (l < 0)
							throw new ValidatorException("Expected a unsigned value");
					}
					numberValue = new BigDecimal(l);
				} catch (Throwable e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else if ("number".equalsIgnoreCase(type)) {
				try {
					try {
						double d = Double.parseDouble(value);
						numberValue = BigDecimal.valueOf(d);
					} catch (Exception e) {
						long l = Long.parseLong(value);
						numberValue = new BigDecimal(l);
					}
				} catch (Throwable e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else if ("short".equalsIgnoreCase(type) || "unsignedShort".equalsIgnoreCase(type)) {
				try {
					short s = Short.parseShort(value);
					if ("unsignedShort".equalsIgnoreCase(type)) {
						if (s < 0)
							throw new ValidatorException("Expected a unsigned value");
					}
					numberValue = new BigDecimal(s);
				} catch (Throwable e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else if ("boolean".equalsIgnoreCase(type)) {
				try {
					if (!"true".equals(value) && !"false".equals(value)) {
						throw new Exception("Only true/false value expected (found: " + value + ")");
					}
				} catch (Throwable e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else if ("anyURI".equalsIgnoreCase(type)) {
				try {
					new URI(value);
				} catch (Throwable e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else if ("uuid".equalsIgnoreCase(type)) {
				try {
					UUID.fromString(value);
				} catch (Throwable e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else if ("date-time".equalsIgnoreCase(type)) {
				try {
					DateUtils.validateDateTimeAsRFC3339Sec56(value);
				} catch (Throwable e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else if ("date".equalsIgnoreCase(type)) {
				try {
					DateUtils.validateDateAsRFC3339Sec56(value);
				} catch (Throwable e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			}

			if (typeRestriction != null) {

				if (numberValue != null) {
					if (typeRestriction.getMaximum() != null) {
						int compare = numberValue.compareTo(typeRestriction.getMaximum());
						if (compare > 0) {
							throw new ValidatorException(
									"Value higher than the maximum '" + typeRestriction.getMaximum() + "'");
						} else if (compare == 0) {
							if (typeRestriction.getExclusiveMaximum() != null
									&& typeRestriction.getExclusiveMaximum()) {
								throw new ValidatorException("Value equals to the maximum '"
										+ typeRestriction.getMaximum() + "' and exclusive maximum is enabled");
							}
						}
					}

					if (typeRestriction.getMinimum() != null) {
						int compare = numberValue.compareTo(typeRestriction.getMinimum());
						if (compare < 0) {
							throw new ValidatorException(
									"Value lowest than the minimum '" + typeRestriction.getMinimum() + "'");
						} else if (compare == 0) {
							if (typeRestriction.getExclusiveMinimum() != null
									&& typeRestriction.getExclusiveMinimum()) {
								throw new ValidatorException("Value equals to the minimum '"
										+ typeRestriction.getMinimum() + "' and exclusive minimum is enabled");
							}
						}
					}

					if (typeRestriction.getMultipleOf() != null) {
						if (numberValue.compareTo(typeRestriction.getMultipleOf()) != 0) {
							try {
								@SuppressWarnings("unused")
								BigDecimal bd = numberValue.divide(typeRestriction.getMultipleOf(), 0,
										RoundingMode.UNNECESSARY);
							} catch (ArithmeticException e) {
								throw new ValidatorException(
										"Value is not multiple of '" + typeRestriction.getMultipleOf() + "'");
							}
						}
					}
				}

				if (stringValue != null) {

					if (typeRestriction.getEnumValues() != null && !typeRestriction.getEnumValues().isEmpty()) {

						List<String> valoriPresenti = new ArrayList<>();
						if (typeRestriction.isArrayParameter()) {
							if (ApiParameterType.query.equals(parameterType)
									|| ApiParameterType.form.equals(parameterType)) {
								if (typeRestriction.isStyleQueryForm() || typeRestriction.getStyle() == null) {
									if (typeRestriction.isExplodeDisabled()) {
										List<String> l = tokenize(stringValue, ",");
										if (l != null && !l.isEmpty()) {
											valoriPresenti.addAll(l);
										}
									}
								} else if (typeRestriction.isStyleQuerySpaceDelimited()) {
									if (typeRestriction.isExplodeDisabled()) {
										List<String> l = tokenize(stringValue, Pattern.quote(" "));
										if (l != null && !l.isEmpty()) {
											valoriPresenti.addAll(l);
										}
									}
								} else if (typeRestriction.isStyleQueryPipeDelimited()) {
									if (typeRestriction.isExplodeDisabled()) {
										List<String> l = tokenize(stringValue, Pattern.quote("|"));
										if (l != null && !l.isEmpty()) {
											valoriPresenti.addAll(l);
										}
									}
								}
							} else if (ApiParameterType.header.equals(parameterType)) {
								if (typeRestriction.isStyleHeaderSimple() || typeRestriction.getStyle() == null) {
									List<String> l = tokenize(stringValue, ",");
									if (l != null && !l.isEmpty()) {
										valoriPresenti.addAll(l);
									}
								}
							} else if (ApiParameterType.path.equals(parameterType)) {
								if (typeRestriction.isStylePathSimple() || typeRestriction.getStyle() == null) {
									List<String> l = tokenize(stringValue, ",");
									if (l != null && !l.isEmpty()) {
										valoriPresenti.addAll(l);
									}
								} else if (typeRestriction.isStylePathLabel()) {
									if (stringValue.length() > 1) {
										String splitPattern = typeRestriction.isExplodeEnabled() ? "\\." : ",";
										String[] v = stringValue.substring(1).split(splitPattern);
										if (v.length > 0) {
											for (String valore : v) {
												valoriPresenti.add(valore);
											}
										}
									}
								} else if (typeRestriction.isStylePathMatrix()) {
									String splitPattern = typeRestriction.isExplodeEnabled() ? ";" : ",";
									List<String> l = getArrayValues(typeRestriction.isExplodeEnabled(), stringValue,
											splitPattern);
									if (l != null && !l.isEmpty()) {
										valoriPresenti.addAll(l);
									}
								}
							}
						}
						if (valoriPresenti.isEmpty()) {
							valoriPresenti.add(stringValue);
						}

						for (String valorePresente : valoriPresenti) {
							boolean found = false;
							StringBuilder sbList = new StringBuilder();
							for (Object o : typeRestriction.getEnumValues()) {
								if (o != null) {
									String check = o.toString();
									if (sbList.length() > 0) {
										sbList.append(",");
									}
									sbList.append(check);
									if (valorePresente.equals(check)) {
										found = true;
										break;
									}
								}
							}
							if (!found) {
								throw new ValidatorException("Uncorrect enum value '" + valorePresente
										+ "', expected: '" + sbList.toString() + "'");
							}
						}
					}

					if (typeRestriction.getMinLength() != null) {
						if (stringValue.length() < typeRestriction.getMinLength().intValue()) {
							throw new ValidatorException(
									"Too short, expected min length '" + typeRestriction.getMinLength() + "'");
						}
					}

					if (typeRestriction.getMaxLength() != null) {
						if (stringValue.length() > typeRestriction.getMaxLength().intValue()) {
							throw new ValidatorException(
									"Too big, expected max length '" + typeRestriction.getMaxLength() + "'");
						}
					}

					if (typeRestriction.getPattern() != null) {
						String pattern = typeRestriction.getPattern().trim();
						try {
							if (pattern.startsWith("^") && pattern.endsWith("$")) {
								if (!RegularExpressionEngine.isMatch(stringValue, pattern)) {
									throw new ValidatorException("Pattern match failed ('" + pattern + "')");
								}
							} else {
								if (!RegularExpressionEngine.isFind(stringValue, pattern)) {
									throw new ValidatorException("Pattern match failed ('" + pattern + "')");
								}
							}
						} catch (ValidatorException e) {
							throw e;
						} catch (Throwable e) {
							throw new ValidatorException(
									"Pattern validation error '" + pattern + "': " + e.getMessage(), e);
						}
					}
				}
			}
		}
	}

	/**
	 * Tokenize su separatore, ignora stringhe vuote, niente trim.
	 * Sostituisce org.openapi4j.core.util.StringUtil.tokenize(s, sep, false, false)
	 * per non introdurre dipendenza da openapi4j in questo package.
	 */
	private static List<String> tokenize(String s, String pattern) {
		if (s == null || s.isEmpty()) {
			return Collections.emptyList();
		}
		String[] parts = s.split(pattern, -1);
		List<String> result = new ArrayList<>(parts.length);
		for (String p : parts) {
			if (!p.isEmpty()) {
				result.add(p);
			}
		}
		return result;
	}

	/**
	 * Estrae i valori da una stringa con prefisso punto-virgola (es. 'matrix' style di OpenAPI
	 * per path parameter). Usa la regex PREFIXED_SEMICOLON_NAME_REGEX.
	 */
	private List<String> getArrayValues(boolean explode, String rawValue, String splitPattern) {
		try {
			Matcher matcher = PREFIXED_SEMICOLON_NAME_REGEX.matcher(rawValue);
			if (explode) {
				List<String> arrayValues = new ArrayList<>();
				int index = 0;
				int limit = 1000; // guardia contro DoS
				while (matcher.find() && index < limit) {
					arrayValues.add(matcher.group(2));
					index++;
				}
				return arrayValues;
			} else {
				return matcher.matches() ? java.util.Arrays.asList(matcher.group(2).split(splitPattern)) : null;
			}
		} catch (Throwable t) {
			if (this.log != null) {
				this.log.error(t.getMessage(), t);
			}
			return null;
		}
	}
}
