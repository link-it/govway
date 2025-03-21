/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.UtilsMultiException;
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
import org.springframework.web.util.UriUtils;

/**
 * ApiValidatorConfig
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractApiValidator   {

	public abstract void validatePreConformanceCheck(HttpBaseEntity<?> httpEntity,ApiOperation operation,Object ... args) throws ProcessingException,ValidatorException;
	
	public abstract void validatePostConformanceCheck(HttpBaseEntity<?> httpEntity,ApiOperation operation,Object ... args) throws ProcessingException,ValidatorException;
	
	public abstract void validateValueAsType(ApiParameterType parameterType, String value, String type, ApiSchemaTypeRestriction typeRestriction) throws ProcessingException,ValidatorException;
	
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
				List<ValidatorException> listValidatorException = new ArrayList<ValidatorException>();
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
				listValidatorException = new ArrayList<ValidatorException>();
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
				listValidatorException = new ArrayList<ValidatorException>();
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
}
