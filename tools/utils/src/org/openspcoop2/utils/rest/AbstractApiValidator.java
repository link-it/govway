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

package org.openspcoop2.utils.rest;

import java.net.URL;
import java.util.List;

import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiBodyParameter;
import org.openspcoop2.utils.rest.api.ApiCookieParameter;
import org.openspcoop2.utils.rest.api.ApiHeaderParameter;
import org.openspcoop2.utils.rest.api.ApiOperation;
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
	
	public abstract void validateValueAsType(String value,String type, ApiSchemaTypeRestriction typeRestriction) throws ProcessingException,ValidatorException;
	
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
							if(input.isAllMediaType() || ContentTypeUtilities.isMatch(baseTypeHttp, input.getMediaType())) {
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
										if(outputBodyParameter.isAllMediaType() || ContentTypeUtilities.isMatch(baseTypeHttp, outputBodyParameter.getMediaType()) ) {
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
									if(outputBodyParameter.isAllMediaType() || ContentTypeUtilities.isMatch(baseTypeHttp, outputBodyParameter.getMediaType()) ) {
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
						String value = TransportUtils.get(request.getParametersTrasporto(), name);
						if(value==null){
							if(paramHeader.isRequired()){
								throw new ValidatorException("Required http header '"+name+"' not found");
							}
						}
						if(value!=null){
							try{
								validateValueAsType(value,paramHeader.getType(),paramHeader.getSchema());
							}catch(ValidatorException val){
								throw new ValidatorException("Invalid value '"+value+"' in http header '"+name+"' (expected type '"+paramHeader.getType()+"'): "+val.getMessage(),val);
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
							try{
								validateValueAsType(value,paramCookie.getType(),paramCookie.getSchema());
							}catch(ValidatorException val){
								throw new ValidatorException("Invalid value '"+value+"' in cookie '"+name+"' (expected type '"+paramCookie.getType()+"'): "+val.getMessage(),val);
							}
						}
					}
				}
				
				if(operation.getRequest()!=null &&  operation.getRequest().sizeQueryParameters()>0){
					for (ApiRequestQueryParameter paramQuery : operation.getRequest().getQueryParameters()) {
						String name = paramQuery.getName();
						String value = TransportUtils.get(request.getParametersQuery(), name);
						if(value==null){
							if(paramQuery.isRequired()){
								throw new ValidatorException("Required query parameter '"+name+"' not found");
							}
						}
						if(value!=null){
							try{
								validateValueAsType(value,paramQuery.getType(),paramQuery.getSchema());
							}catch(ValidatorException val){
								throw new ValidatorException("Invalid value '"+value+"' in query parameter '"+name+"' (expected type '"+paramQuery.getType()+"'): "+val.getMessage(),val);
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
							try{
								validateValueAsType(valueFound,paramDynamicPath.getType(),paramDynamicPath.getSchema());
							}catch(ValidatorException val){
								throw new ValidatorException("Invalid value '"+valueFound+"' in dynamic path '"+paramDynamicPath.getName()+"' (expected type '"+paramDynamicPath.getType()+"'): "+val.getMessage(),val);
							}
						}
					}
				}
				
				if(operation.getRequest()!=null &&  operation.getRequest().sizeFormParameters()>0){
					for (ApiRequestFormParameter paramForm : operation.getRequest().getFormParameters()) {
						String name = paramForm.getName();
						String value = TransportUtils.get(request.getParametersForm(), name);
						if(value==null){
							if(paramForm.isRequired()){
								throw new ValidatorException("Required form parameter '"+name+"' not found");
							}
						}
						if(value!=null){
							try{
								validateValueAsType(value,paramForm.getType(),paramForm.getSchema());
							}catch(ValidatorException val){
								throw new ValidatorException("Invalid value '"+value+"' in form parameter '"+name+"' (expected type '"+paramForm.getType()+"'): "+val.getMessage(),val);
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
						String value = TransportUtils.get(response.getParametersTrasporto(), name);
						if(value==null){
							if(paramHeader.isRequired()){
								throw new ValidatorException("Required http header '"+name+"' not found");
							}
						}
						if(value!=null){
							try{
								validateValueAsType(value,paramHeader.getType(),paramHeader.getSchema());
							}catch(ValidatorException val){
								throw new ValidatorException("Invalid value '"+value+"' in http header '"+name+"' (expected type '"+paramHeader.getType()+"'): "+val.getMessage(),val);
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
							try{
								validateValueAsType(value,paramCookie.getType(),paramCookie.getSchema());
							}catch(ValidatorException val){
								throw new ValidatorException("Invalid value '"+value+"' in cookie '"+name+"' (expected type '"+paramCookie.getType()+"'): "+val.getMessage(),val);
							}
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
	
}
